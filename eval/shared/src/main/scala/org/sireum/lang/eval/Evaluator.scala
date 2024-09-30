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
import org.sireum.message.{Position, Reporter}

object Evaluator {
  val kind: String = "Slang Evaluator"
  
  @ext("Evaluator_Ext") object Ext {
    @pure def toValue[O](tipe: State.Type, o: O): State.Value = $
    @pure def extractValue[O](v: State.Value): O = $
    @pure def toString(v: State.Value): String = $
    @pure def dropRight[T](s: ISZ[T]): ISZ[T] = $
    @pure def deepClone(v: State.Value): State.Value = $
  }
}

import Evaluator._

@record class Evaluator(val th: TypeHierarchy, val state: State,
                        val reflections: ISZ[Reflection], val reporter: Reporter) {

  val printf: String => Unit = (s: String) => print(s)
  val eprintf: String => Unit = (s: String) => eprint(s)

  def evalBinaryZ(posOpt: Option[Position], exp: AST.CoreExp.Binary, l: State.Ptr, r: State.Ptr): State.Ptr = {
    val left = Ext.extractValue[Z](state.lookupHeap(l))
    val right = Ext.extractValue[Z](state.lookupHeap(r))
    state.gc(l)
    state.gc(r)
    val t = State.Type.Z
    exp.op match {
      case AST.Exp.BinaryOp.Add => return state.alloc(Ext.toValue(t, left + right))
      case AST.Exp.BinaryOp.Sub => return state.alloc(Ext.toValue(t, left - right))
      case AST.Exp.BinaryOp.Mul => return state.alloc(Ext.toValue(t, left * right))
      case AST.Exp.BinaryOp.Div =>
        if (right == 0) {
          err(posOpt, "Division by zero")
        }
        return state.alloc(Ext.toValue(t, left / right))
      case AST.Exp.BinaryOp.Rem =>
        if (right == 0) {
          err(posOpt, "Division by zero")
        }
        return state.alloc(Ext.toValue(t, left % right))
      case AST.Exp.BinaryOp.EquivUni => return state.alloc(Ext.toValue(State.Type.B, left == right))
      case AST.Exp.BinaryOp.InequivUni => return state.alloc(Ext.toValue(State.Type.B, left != right))
      case AST.Exp.BinaryOp.Lt => return state.alloc(Ext.toValue(State.Type.B, left < right))
      case AST.Exp.BinaryOp.Le => return state.alloc(Ext.toValue(State.Type.B, left <= right))
      case AST.Exp.BinaryOp.Gt => return state.alloc(Ext.toValue(State.Type.B, left > right))
      case AST.Exp.BinaryOp.Ge => return state.alloc(Ext.toValue(State.Type.B, left >= right))
      case _ => halt(s"Infeasible: $left ${exp.op} $right")
    }
  }

  def evalCoreExp(posOpt: Option[message.Position], exp: AST.CoreExp,
                  funStack: CoreExpTranslator.FunStack, localMap: CoreExpTranslator.LocalMap): State.Ptr = {
    exp match {
      case exp: AST.CoreExp.LitB => return state.alloc(Ext.toValue(State.Type.B, exp.value))
      case exp: AST.CoreExp.LitZ => return state.alloc(Ext.toValue(State.Type.Z, exp.value))
      case exp: AST.CoreExp.LitC => return state.alloc(Ext.toValue(State.Type.C, exp.value))
      case exp: AST.CoreExp.LitF32 => return state.alloc(Ext.toValue(State.Type.F32, exp.value))
      case exp: AST.CoreExp.LitF64 => return state.alloc(Ext.toValue(State.Type.F64, exp.value))
      case exp: AST.CoreExp.LitR => return state.alloc(Ext.toValue(State.Type.R, exp.value))
      case exp: AST.CoreExp.LitString => return state.alloc(Ext.toValue(State.Type.String, exp.value))
      case exp: AST.CoreExp.LitBits => return state.alloc(Ext.toValue(State.Type.Bits(exp.tipe.asInstanceOf[AST.Typed.Name].ids), exp.value))
      case exp: AST.CoreExp.LitRange => return state.alloc(Ext.toValue(State.Type.Range(exp.tipe.asInstanceOf[AST.Typed.Name].ids), exp.value))
      case exp: AST.CoreExp.LitEnum => halt("TODO")
      case exp: AST.CoreExp.Binary =>
        val l = evalCoreExp(posOpt, exp.left, funStack, localMap)
        state.tipe(l) match {
          case State.Type.Z =>
            val r = evalCoreExp(posOpt, exp.right, funStack, localMap)
            return evalBinaryZ(posOpt, exp, l, r)
          case _ => halt("TODO")
        }
      case _ => halt("TODO")
    }
  }

  @memoize def toCoreExp(exp: AST.Exp): AST.CoreExp = {
    return th.translateToExtendedCoreExp(exp, Stack.empty, HashSMap.empty)
  }

  def evalExp(exp: AST.Exp): State.Ptr = {
    return evalCoreExp(exp.posOpt, toCoreExp(exp), Stack.empty, HashSMap.empty)
  }

  def objectToString(o: State.Value): String = {
    if (o.isObject) {
      val map = o.objectMap
      val t = o.tipe.asInstanceOf[State.Type.Class]
      val r = st"""${t.name(t.name.size - 1)}(${(for (i <- map.values) yield objectToString(state.lookupHeap(i)), ",\n")})"""
      return r.render
    }
    return Ext.toString(o)
  }

  def xprintf(stmt: AST.Stmt.Expr, f: String => Unit, i: Z): Unit = {
    val args = stmt.exp.asInstanceOf[AST.Exp.Invoke].args
    for (j <- i until args.size) {
      val ptr = evalExp(args(j))
      f(objectToString(state.lookupHeap(ptr)))
      state.gc(ptr)
    }
  }

  def err(posOpt: Option[Position], message: String): Unit = {
    reporter.error(posOpt, kind, message)
    halt("Execution terminated")
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
          err(args(0).posOpt, "Assertion violation")
        }
      case AST.Stmt.Expr.Kind.AssertMsg =>
        var ptr = evalExp(args(0))
        val claim = Ext.extractValue[B](state.lookupHeap(ptr))
        state.gc(ptr)
        if (!claim) {
          ptr = evalExp(args(1))
          val msg = Ext.extractValue[String](state.lookupHeap(ptr))
          state.gc(ptr)
          err(args(0).posOpt, s"Assertion violation: $msg")
        }
      case AST.Stmt.Expr.Kind.Assume =>
        val ptr = evalExp(args(0))
        val claim = Ext.extractValue[B](state.lookupHeap(ptr))
        state.gc(ptr)
        if (!claim) {
          err(args(0).posOpt, "Assumption violation")
        }
      case AST.Stmt.Expr.Kind.AssumeMsg =>
        var ptr = evalExp(args(0))
        val claim = Ext.extractValue[B](state.lookupHeap(ptr))
        state.gc(ptr)
        if (!claim) {
          ptr = evalExp(args(1))
          val msg = Ext.extractValue[String](state.lookupHeap(ptr))
          state.gc(ptr)
          err(args(0).posOpt, s"Assumption violation: $msg")
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
      case _: AST.Stmt.Assign => halt("TODO") // TODO
      case _: AST.Stmt.Block => halt("TODO") // TODO
      case _: AST.Stmt.DoWhile => halt("TODO") // TODO
      case _: AST.Stmt.For => halt("TODO") // TODO
      case _: AST.Stmt.If => halt("TODO") // TODO
      case _: AST.Stmt.Match => halt("TODO") // TODO
      case _: AST.Stmt.Return => halt("TODO") // TODO
      case _: AST.Stmt.Var => halt("TODO") // TODO
      case _: AST.Stmt.VarPattern => halt("TODO") // TODO
      case _: AST.Stmt.While => halt("TODO") // TODO
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
