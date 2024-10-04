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

object Evaluator {
  val kind: String = "Slang Evaluator"
  
}

import Evaluator._

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
      case t: State.Type.Bits => halt(s"TODO: $t") // TODO
      case t: State.Type.Range => halt(s"TODO: $t") // TODO
      case t => halt(s"TODO: $t")
    }
  }

  def evalEnum(pos: message.Position, exp: AST.CoreExp.LitEnum): State.Ptr = {
    val t = State.Type.Class(T, exp.owner :+ "Type")
    Reflection.find(reflections, exp.owner) match {
      case Some(r) =>
        //return state.alloc(toValue(t, r.invokeStatic0(st"${(exp.owner, ".")}".render, exp.id)))
      case _ =>
        return state.alloc(State.Value.Object(t, 0, HashSMap.empty))
    }
    halt("TODO")
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
      case exp: AST.CoreExp.LitBits => return state.alloc(toValue(State.Type.Bits(exp.tipe.asInstanceOf[AST.Typed.Name].ids), exp.value))
      case exp: AST.CoreExp.LitRange => return state.alloc(toValue(State.Type.Range(exp.tipe.asInstanceOf[AST.Typed.Name].ids), exp.value))
      case exp: AST.CoreExp.LitEnum => halt(s"TODO: $exp") // TODO
      case exp: AST.CoreExp.StringInterpolate => halt(s"TODO: $exp") // TODO
      case exp: AST.CoreExp.Binary => return evalBinary(pos, exp, funStack, localMap)
      case exp: AST.CoreExp.Unary => halt(s"TODO: $exp") // TODO
      case exp: AST.CoreExp.Constructor => halt(s"TODO: $exp") // TODO
      case exp: AST.CoreExp.Apply => halt(s"TODO: $exp") // TODO
      case exp: AST.CoreExp.Halt => halt(s"TODO: $exp") // TODO
      case exp: AST.CoreExp.If => halt(s"TODO: $exp") // TODO
      case exp: AST.CoreExp.Select => halt(s"TODO: $exp") // TODO
      case exp: AST.CoreExp.Fun => halt(s"TODO: $exp") // TODO
      case exp: AST.CoreExp.Indexing => halt(s"TODO: $exp") // TODO
      case exp: AST.CoreExp.IndexingUpdate => halt(s"TODO: $exp") // TODO
      case exp: AST.CoreExp.LocalVarRef => halt(s"TODO: $exp") // TODO
      case exp: AST.CoreExp.ParamVarRef => halt(s"TODO: $exp") // TODO
      case exp: AST.CoreExp.Quant => halt(s"TODO: $exp") // TODO
      case exp: AST.CoreExp.Update => halt(s"TODO: $exp") // TODO
      case exp: AST.CoreExp.VarRef => halt(s"TODO: $exp") // TODO
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
    if (o.isObject) {
      val map = o.objectMap
      val t = o.tipe.asInstanceOf[State.Type.Class]
      val r = st"""${t.name(t.name.size - 1)}(${(for (i <- map.values) yield objectToString(state.lookupHeap(i)), ",\n")})"""
      return r.render
    }
    return o.nativeValueString
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
