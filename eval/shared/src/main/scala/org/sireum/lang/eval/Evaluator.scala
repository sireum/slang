// #Sireum
/*
 Copyright (c) 2017-2024, Robby, Kansas State University
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sireum.lang.eval

import org.sireum._
import org.sireum.lang.tipe.{CoreExpTranslator, TypeHierarchy}
import org.sireum.lang.{ast => AST}
import org.sireum.message.Position
import Util._
import org.sireum.lang.symbol.TypeInfo

object Evaluator {
  val kind: String = "Slang Evaluator"
}

@record class Evaluator(val th: TypeHierarchy, val state: State, val reflections: ISZ[Reflection]) {

  val printf: String => Unit = (s: String) => print(s)
  val eprintf: String => Unit = (s: String) => eprint(s)

  def evalBinaryH[T1, T2](pos: Position, exp: AST.CoreExp.Binary, l: State.Ptr, r: () => State.Ptr,
                          f: (Position, T1, String, () => T2) => State.Value @pure): State.Ptr = {
    def evalRight(): T2 = {
      val rPtr = r()
      val right = Ext.extractValue[T2](state.lookupHeap(rPtr))
      state.gc(rPtr)
      return right
    }
    val left = Ext.extractValue[T1](state.lookupHeap(l))
    state.gc(l)
    return state.alloc(f(pos, left, exp.op, evalRight _))
  }

  def evalUnary(pos: Position, exp: AST.CoreExp.Unary,
                funStack: CoreExpTranslator.FunStack, localMap: CoreExpTranslator.LocalMap): State.Ptr = {
    val e = evalCoreExp(pos, exp.exp, funStack, localMap)
    exp.op match {
      case AST.Exp.UnaryOp.Plus => return e
      case AST.Exp.UnaryOp.Not =>
        val r = toValue(State.Type.B, !Util.Ext.extractValue[B](state.heap(e)))
        state.gc(e)
        return state.alloc(r)
      case AST.Exp.UnaryOp.Complement =>
        state.tipe(e) match {
          case State.Type.B =>
            val r = toValue(State.Type.B, ~Util.Ext.extractValue[B](state.heap(e)))
            state.gc(e)
            return state.alloc(r)
          case State.Type.Class(State.Type.Kind.Bits, name) =>
            if (name.size == 3 && name(0) == "org" && name(1) == "sireum") {
              val v = state.heap(e)
              state.gc(e)
              return state.alloc(Util.Ext.unaryBits(exp.op, v))
            }
            halt(s"TODO: $name")
          case t => halt(s"Infeasible: $t")
        }
      case AST.Exp.UnaryOp.Minus =>
        val t = state.tipe(e)
        state.tipe(e) match {
          case State.Type.Z =>
            val v = -Util.Ext.extractValue[Z](state.heap(e))
            state.gc(e)
            return state.alloc(toValue(t, v))
          case State.Type.F32 =>
            val v = -Util.Ext.extractValue[F32](state.heap(e))
            state.gc(e)
            return state.alloc(toValue(t, v))
          case State.Type.F64 =>
            val v = -Util.Ext.extractValue[F64](state.heap(e))
            state.gc(e)
            return state.alloc(toValue(t, v))
          case State.Type.R =>
            val v = -Util.Ext.extractValue[R](state.heap(e))
            state.gc(e)
            return state.alloc(toValue(t, v))
          case State.Type.Class(State.Type.Kind.Bits, name) =>
            if (name.size == 3 && name(0) == "org" && name(1) == "sireum") {
              val v = state.heap(e)
              state.gc(e)
              return state.alloc(Util.Ext.unaryBits(exp.op, v))
            }
            halt(s"Infeasible: $name")
          case State.Type.Class(State.Type.Kind.Range, name) =>
            val v = -Util.Ext.extractValue[Z](state.heap(e))
            val info = th.typeMap.get(name).get.asInstanceOf[TypeInfo.SubZ]
            if (info.ast.hasMin && v < info.ast.min) {
              halt(st"The low range limit is violated for ${(name, ".")}: $v at line ${pos.beginLine} ${pos.uriOpt}".render)
            }
            if (info.ast.hasMax && v > info.ast.max) {
              halt(st"The high range limit is violated for ${(name, ".")} ($v) at line ${pos.beginLine} ${pos.uriOpt}".render)
            }
            state.gc(e)
            return state.alloc(toValue(t, v))
          case t => halt(s"Infeasible: $t")
        }
    }
  }

  def evalBinary(pos: Position, exp: AST.CoreExp.Binary,
                 funStack: CoreExpTranslator.FunStack, localMap: CoreExpTranslator.LocalMap): State.Ptr = {
    val l = evalCoreExp(pos, exp.left, funStack, localMap)
    def evalRight(): State.Ptr = {
      return evalCoreExp(pos, exp.right, funStack, localMap)
    }
    exp.op match {
      case AST.Exp.BinaryOp.MapsTo =>
        val r = evalRight()
        val left = state.lookupHeap(l)
        val right = state.lookupHeap(r)
        state.gc(l)
        state.gc(r)
        return state.alloc(tuple2(state, left, right))
      case AST.Exp.BinaryOp.Append if isSeq(exp.left) =>
        val r = evalRight()
        val left = state.lookupHeap(l)
        val right = state.lookupHeap(r)
        state.gc(l)
        state.gc(r)
        return state.alloc(Ext.append(left, right))
      case AST.Exp.BinaryOp.AppendAll if isSeq(exp.left) && isSeq(exp.right) =>
        val r = evalRight()
        val left = state.lookupHeap(l)
        val right = state.lookupHeap(r)
        state.gc(l)
        state.gc(r)
        return state.alloc(Ext.appendAll(left, right))
      case AST.Exp.BinaryOp.Prepend if isSeq(exp.right) =>
        val r = evalRight()
        val left = state.lookupHeap(l)
        val right = state.lookupHeap(r)
        state.gc(l)
        state.gc(r)
        return state.alloc(Ext.prepend(left, right))
      case _ =>
    }

    state.tipe(l) match {
      case State.Type.B => return evalBinaryH(pos, exp, l, evalRight _, binaryB _)
      case State.Type.C => return evalBinaryH(pos, exp, l, evalRight _, binaryC _)
      case State.Type.Z => return evalBinaryH(pos, exp, l, evalRight _, binaryZ _)
      case State.Type.F32 => return evalBinaryH(pos, exp, l, evalRight _, binaryF32 _)
      case State.Type.F64 => return evalBinaryH(pos, exp, l, evalRight _, binaryF64 _)
      case State.Type.R => return evalBinaryH(pos, exp, l, evalRight _, binaryR _)
      case State.Type.String => return evalBinaryH(pos, exp, l, evalRight _, binaryR _)
      case State.Type.Class(State.Type.Kind.Bits, name) =>
        val r = evalRight()
        val left = state.lookupHeap(l)
        val right = state.lookupHeap(r)
        state.gc(l)
        state.gc(r)
        if (name.size == 3 && name(0) == "org" && name(1) == "sireum") {
          return state.alloc(Util.Ext.binaryBits(left, exp.op, right))
        }
        halt(s"TODO: $name")
      case State.Type.Class(State.Type.Kind.Range, name) =>
        val r = evalBinaryH(pos, exp, l, evalRight _, binaryZ _)
        val v = Util.Ext.extractValue[Z](state.heap(r))
        val info = th.typeMap.get(name).get.asInstanceOf[TypeInfo.SubZ]
        if (info.ast.hasMin && v < info.ast.min) {
          halt(st"The low range limit is violated for ${(name, ".")}: $v at line ${pos.beginLine} ${pos.uriOpt}".render)
        }
        if (info.ast.hasMax && v > info.ast.max) {
          halt(st"The high range limit is violated for ${(name, ".")} ($v) at line ${pos.beginLine} ${pos.uriOpt}".render)
        }
        return r
      case t => halt(s"Infeasible: $t")
    }
  }

  def evalEnum(exp: AST.CoreExp.LitEnum): State.Ptr = {
    val t = State.Type.Class(State.Type.Kind.Enum, exp.owner :+ "Type")
    Reflection.find(reflections, exp.owner) match {
      case Some(r) =>
        return state.alloc(Util.Ext.invokeStatic0(r, t, st"${(exp.owner, ".")}".render, exp.id))
      case _ =>
        return state.alloc(State.Value.Enum(t, 0, exp.id, exp.ordinal))
    }
  }

  def evalBits(exp: AST.CoreExp.LitBits): State.Ptr = {
    val c = State.Type.Class(State.Type.Kind.Bits, exp.tipe.asInstanceOf[AST.Typed.Name].ids)
    if (c.name.size == 3 && c.name(0) == "org" && c.name(1) == "sireum") {
      c.name(2) match {
        case string"U64" => return state.alloc(toValue(c, U64(exp.value).get))
        case string"U32" => return state.alloc(toValue(c, U32(exp.value).get))
        case string"U16" => return state.alloc(toValue(c, U16(exp.value).get))
        case string"U8" => return state.alloc(toValue(c, U8(exp.value).get))
        case string"S64" => return state.alloc(toValue(c, S64(exp.value).get))
        case string"S32" => return state.alloc(toValue(c, S32(exp.value).get))
        case string"S16" => return state.alloc(toValue(c, S16(exp.value).get))
        case string"S8" => return state.alloc(toValue(c, S8(exp.value).get))
        case string"U1" => return state.alloc(toValue(c, U1(exp.value).get))
        case string"U2" => return state.alloc(toValue(c, U2(exp.value).get))
        case string"U3" => return state.alloc(toValue(c, U3(exp.value).get))
        case string"U4" => return state.alloc(toValue(c, U4(exp.value).get))
        case string"U5" => return state.alloc(toValue(c, U5(exp.value).get))
        case string"U6" => return state.alloc(toValue(c, U6(exp.value).get))
        case string"U7" => return state.alloc(toValue(c, U7(exp.value).get))
        case string"U9" => return state.alloc(toValue(c, U9(exp.value).get))
        case string"U10" => return state.alloc(toValue(c, U10(exp.value).get))
        case string"U11" => return state.alloc(toValue(c, U11(exp.value).get))
        case string"U12" => return state.alloc(toValue(c, U12(exp.value).get))
        case string"U13" => return state.alloc(toValue(c, U13(exp.value).get))
        case string"U14" => return state.alloc(toValue(c, U14(exp.value).get))
        case string"U15" => return state.alloc(toValue(c, U15(exp.value).get))
        case string"U17" => return state.alloc(toValue(c, U17(exp.value).get))
        case string"U18" => return state.alloc(toValue(c, U18(exp.value).get))
        case string"U19" => return state.alloc(toValue(c, U19(exp.value).get))
        case string"U20" => return state.alloc(toValue(c, U20(exp.value).get))
        case string"U21" => return state.alloc(toValue(c, U21(exp.value).get))
        case string"U22" => return state.alloc(toValue(c, U22(exp.value).get))
        case string"U23" => return state.alloc(toValue(c, U23(exp.value).get))
        case string"U24" => return state.alloc(toValue(c, U24(exp.value).get))
        case string"U25" => return state.alloc(toValue(c, U25(exp.value).get))
        case string"U26" => return state.alloc(toValue(c, U26(exp.value).get))
        case string"U27" => return state.alloc(toValue(c, U27(exp.value).get))
        case string"U28" => return state.alloc(toValue(c, U28(exp.value).get))
        case string"U29" => return state.alloc(toValue(c, U29(exp.value).get))
        case string"U30" => return state.alloc(toValue(c, U30(exp.value).get))
        case string"U31" => return state.alloc(toValue(c, U31(exp.value).get))
        case string"U33" => return state.alloc(toValue(c, U33(exp.value).get))
        case string"U34" => return state.alloc(toValue(c, U34(exp.value).get))
        case string"U35" => return state.alloc(toValue(c, U35(exp.value).get))
        case string"U36" => return state.alloc(toValue(c, U36(exp.value).get))
        case string"U37" => return state.alloc(toValue(c, U37(exp.value).get))
        case string"U38" => return state.alloc(toValue(c, U38(exp.value).get))
        case string"U39" => return state.alloc(toValue(c, U39(exp.value).get))
        case string"U40" => return state.alloc(toValue(c, U40(exp.value).get))
        case string"U41" => return state.alloc(toValue(c, U41(exp.value).get))
        case string"U42" => return state.alloc(toValue(c, U42(exp.value).get))
        case string"U43" => return state.alloc(toValue(c, U43(exp.value).get))
        case string"U44" => return state.alloc(toValue(c, U44(exp.value).get))
        case string"U45" => return state.alloc(toValue(c, U45(exp.value).get))
        case string"U46" => return state.alloc(toValue(c, U46(exp.value).get))
        case string"U47" => return state.alloc(toValue(c, U47(exp.value).get))
        case string"U48" => return state.alloc(toValue(c, U48(exp.value).get))
        case string"U49" => return state.alloc(toValue(c, U49(exp.value).get))
        case string"U50" => return state.alloc(toValue(c, U50(exp.value).get))
        case string"U51" => return state.alloc(toValue(c, U51(exp.value).get))
        case string"U52" => return state.alloc(toValue(c, U52(exp.value).get))
        case string"U53" => return state.alloc(toValue(c, U53(exp.value).get))
        case string"U54" => return state.alloc(toValue(c, U54(exp.value).get))
        case string"U55" => return state.alloc(toValue(c, U55(exp.value).get))
        case string"U56" => return state.alloc(toValue(c, U56(exp.value).get))
        case string"U57" => return state.alloc(toValue(c, U57(exp.value).get))
        case string"U58" => return state.alloc(toValue(c, U58(exp.value).get))
        case string"U59" => return state.alloc(toValue(c, U59(exp.value).get))
        case string"U60" => return state.alloc(toValue(c, U60(exp.value).get))
        case string"U61" => return state.alloc(toValue(c, U61(exp.value).get))
        case string"U62" => return state.alloc(toValue(c, U62(exp.value).get))
        case string"U63" => return state.alloc(toValue(c, U63(exp.value).get))
        case _ => halt(s"Infeasible: $c")
      }
    }
    halt(s"TODO: $exp")
  }

  def evalRange(exp: AST.CoreExp.LitRange): State.Ptr = {
    val c = State.Type.Class(State.Type.Kind.Range, exp.tipe.asInstanceOf[AST.Typed.Name].ids)
    return state.alloc(toValue(c, exp.value))
  }

  def evalIfExp(pos: message.Position, exp: AST.CoreExp.If,
                funStack: CoreExpTranslator.FunStack, localMap: CoreExpTranslator.LocalMap): State.Ptr = {
    val cond = evalCoreExp(pos, exp.cond, funStack, localMap)
    val b = Util.Ext.extractValue[B](state.heap(cond))
    state.gc(cond)
    return evalCoreExp(pos, if (b) exp.tExp else exp.fExp, funStack, localMap)
  }

  def evalCoreExp(pos: message.Position, exp: AST.CoreExp.Base,
                  funStack: CoreExpTranslator.FunStack, localMap: CoreExpTranslator.LocalMap): State.Ptr = {
    exp match {
      case exp: AST.CoreExp.LitB => return state.alloc(toValue(State.Type.B, exp.value))
      case exp: AST.CoreExp.LitZ => return state.alloc(toValue(State.Type.Z, exp.value))
      case exp: AST.CoreExp.LitC => return state.alloc(toValue(State.Type.C, exp.value))
      case exp: AST.CoreExp.LitF32 => return state.alloc(toValue(State.Type.F32, exp.value))
      case exp: AST.CoreExp.LitF64 => return state.alloc(toValue(State.Type.F64, exp.value))
      case exp: AST.CoreExp.LitR => return state.alloc(toValue(State.Type.R, exp.value))
      case exp: AST.CoreExp.LitString => return state.alloc(toValue(State.Type.String, exp.value))
      case exp: AST.CoreExp.LitBits => return evalBits(exp)
      case exp: AST.CoreExp.LitRange => return evalRange(exp)
      case exp: AST.CoreExp.LitEnum => return evalEnum(exp)
      case exp: AST.CoreExp.Binary => return evalBinary(pos, exp, funStack, localMap)
      case exp: AST.CoreExp.Unary => return evalUnary(pos, exp, funStack, localMap)
      case exp: AST.CoreExp.If => return evalIfExp(pos, exp, funStack, localMap)
      case exp: AST.CoreExp.LocalVarRef => return state.lookup(exp.id)
      case exp: AST.CoreExp.ParamVarRef => return state.lookup(exp.id)
      case exp: AST.CoreExp.ObjectVarRef => halt(s"TODO: $exp") // TODO
      case exp: AST.CoreExp.Constructor => halt(s"TODO: $exp") // TODO
      case exp: AST.CoreExp.StringInterpolate => halt(s"TODO: $exp") // TODO
      case exp: AST.CoreExp.Apply => halt(s"TODO: $exp") // TODO
      case exp: AST.CoreExp.Halt => halt(s"TODO: $exp") // TODO
      case exp: AST.CoreExp.Select => halt(s"TODO: $exp") // TODO
      case exp: AST.CoreExp.Fun => halt(s"TODO: $exp") // TODO
      case exp: AST.CoreExp.Indexing => halt(s"TODO: $exp") // TODO
      case exp: AST.CoreExp.IndexingUpdate => halt(s"TODO: $exp") // TODO
      case exp: AST.CoreExp.Quant => halt(s"TODO: $exp") // TODO
      case exp: AST.CoreExp.Update => halt(s"TODO: $exp") // TODO
      case exp: AST.CoreExp.Labeled => halt(s"TODO: $exp") // TODO
      case exp: AST.CoreExp.Extended.StrictPureBlock => halt(s"TODO: $exp") // TODO
      case exp: AST.CoreExp.InstanceOfExp => halt(s"TODO: $exp") // TODO
    }
  }

  @memoize def toCoreExp(exp: AST.Exp): AST.CoreExp.Base = {
    return th.translateToExtendedCoreExp(exp, Stack.empty, HashSMap.empty)
  }

  def evalExp(exp: AST.Exp): State.Ptr = {
    return evalCoreExp(exp.posOpt.get, toCoreExp(exp), Stack.empty, HashSMap.empty)
  }

  def objectToString(o: State.Value): String = {
    if (o.isNative) {
      return o.nativeValueString
    }
    o.tipe match {
      case t: State.Type.Tuple =>
      case _ =>
    }
    val map = o.objectMap
    val t = o.tipe.asInstanceOf[State.Type.Class]
    val r = st"""${t.name(t.name.size - 1)}(${(for (i <- map.values) yield objectToString(state.lookupHeap(i)), ",\n")})"""
    return r.render
  }

  def xprintf(stmt: AST.Stmt.Expr, f: String => Unit, i: Z): Unit = {
    val args = stmt.exp.asInstanceOf[AST.Exp.Invoke].args
    for (j <- i until args.size) {
      val ptr = evalExp(args(j))
      f(objectToString(state.lookupHeap(ptr)))
      state.gc(ptr)
    }
  }

  def evalBuiltInStmt(stmt: AST.Stmt.Expr): Unit = {
    val args = stmt.exp.asInstanceOf[AST.Exp.Invoke].args
    stmt.kind match {
      case AST.Stmt.Expr.Kind.Print =>
        xprintf(stmt, printf, 0)
      case AST.Stmt.Expr.Kind.Println =>
        xprintf(stmt, printf, 0)
        println()
      case AST.Stmt.Expr.Kind.Eprint =>
        xprintf(stmt, eprintf, 0)
      case AST.Stmt.Expr.Kind.Eprintln =>
        xprintf(stmt, eprintf, 0)
        eprintln()
      case AST.Stmt.Expr.Kind.Cprint =>
        val ptr = evalExp(args(0))
        val isErr = Ext.extractValue[B](state.lookupHeap(ptr))
        state.gc(ptr)
        xprintf(stmt, if (isErr) eprintf else printf, 1)
      case AST.Stmt.Expr.Kind.Cprintln =>
        val ptr = evalExp(args(0))
        val isErr = Ext.extractValue[B](state.lookupHeap(ptr))
        state.gc(ptr)
        if (isErr) {
          xprintf(stmt, eprintf, 1)
          eprintln()
        } else {
          xprintf(stmt, printf, 1)
          println()
        }
      case AST.Stmt.Expr.Kind.Assert =>
        val ptr = evalExp(args(0))
        val claim = Ext.extractValue[B](state.lookupHeap(ptr))
        state.gc(ptr)
        if (!claim) {
          err(args(0).posOpt.get, "Assertion violation")
        }
      case AST.Stmt.Expr.Kind.AssertMsg =>
        var ptr = evalExp(args(0))
        val claim = Ext.extractValue[B](state.lookupHeap(ptr))
        state.gc(ptr)
        if (!claim) {
          ptr = evalExp(args(1))
          val msg = Ext.extractValue[String](state.lookupHeap(ptr))
          state.gc(ptr)
          err(args(0).posOpt.get, s"Assertion violation: $msg")
        }
      case AST.Stmt.Expr.Kind.Assume =>
        val ptr = evalExp(args(0))
        val claim = Ext.extractValue[B](state.lookupHeap(ptr))
        state.gc(ptr)
        if (!claim) {
          err(args(0).posOpt.get, "Assumption violation")
        }
      case AST.Stmt.Expr.Kind.AssumeMsg =>
        var ptr = evalExp(args(0))
        val claim = Ext.extractValue[B](state.lookupHeap(ptr))
        state.gc(ptr)
        if (!claim) {
          ptr = evalExp(args(1))
          val msg = Ext.extractValue[String](state.lookupHeap(ptr))
          state.gc(ptr)
          err(args(0).posOpt.get, s"Assumption violation: $msg")
        }
      case AST.Stmt.Expr.Kind.SetOptions => // skip
      case AST.Stmt.Expr.Kind.General => halt("Infeasible")
    }
  }

  def evalStmt(stmt: AST.Stmt): Unit = {
    stmt match {
      case stmt: AST.Stmt.Expr =>
        if (stmt.kind == AST.Stmt.Expr.Kind.General) {
          state.gc(evalExp(stmt.exp))
        } else {
          evalBuiltInStmt(stmt)
        }
      case _: AST.Stmt.Assign => halt(s"TODO: $stmt") // TODO
      case _: AST.Stmt.Block => halt(s"TODO: $stmt") // TODO
      case _: AST.Stmt.DoWhile => halt(s"TODO: $stmt") // TODO
      case _: AST.Stmt.For => halt(s"TODO: $stmt") // TODO
      case _: AST.Stmt.If => halt(s"TODO: $stmt") // TODO
      case _: AST.Stmt.Match => halt(s"TODO: $stmt") // TODO
      case _: AST.Stmt.Return => halt(s"TODO: $stmt") // TODO
      case _: AST.Stmt.Var => halt(s"TODO: $stmt") // TODO
      case _: AST.Stmt.VarPattern => halt(s"TODO: $stmt") // TODO
      case _: AST.Stmt.While => halt(s"TODO: $stmt") // TODO
      case _: AST.Stmt.Import => // skip
      case _: AST.Stmt.Method => // skip
      case _: AST.Stmt.Object => // skip
      case _: AST.Stmt.Adt => // skip
      case _: AST.Stmt.Sig => // skip
      case _: AST.Stmt.ExtMethod => // skip
      case _: AST.Stmt.Enum => // skip
      case _: AST.Stmt.SubZ => // skip
      case _: AST.Stmt.TypeAlias => // skip
      case _: AST.Stmt.Spec => // skip
    }
  }

  def evalStmts(stmts: ISZ[AST.Stmt]): Unit = {
    for (stmt <- stmts) {
      evalStmt(stmt)
    }
  }

  def evalWorksheet(p: AST.TopUnit.Program): Unit = {
    evalStmts(p.body.stmts)
  }
}
