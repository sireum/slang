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

object Evaluator {
  val kind: String = "Slang Evaluator"
  
  @ext("Evaluator_Ext") object Ext {
    @pure def extractValue[O](v: State.Value): O = $
    @pure def dropRight[T](s: ISZ[T]): ISZ[T] = $
    @pure def deepClone(v: State.Value): State.Value = $
    @pure def tuple2(v1: State.Value, v2: State.Value): State.Value = $
    @pure def tuple3(v1: State.Value, v2: State.Value, v3: State.Value): State.Value = $
    @pure def tuple4(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value): State.Value = $
    @pure def tuple5(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value): State.Value = $
    @pure def tuple6(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value): State.Value = $
    @pure def tuple7(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value): State.Value = $
    @pure def tuple8(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value): State.Value = $
    @pure def tuple9(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value): State.Value = $
    @pure def tuple10(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value): State.Value = $
    @pure def tuple11(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value): State.Value = $
    @pure def tuple12(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value): State.Value = $
    @pure def tuple13(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value): State.Value = $
    @pure def tuple14(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value): State.Value = $
    @pure def tuple15(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value, v15: State.Value): State.Value = $
    @pure def tuple16(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value, v15: State.Value, v16: State.Value): State.Value = $
    @pure def tuple17(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value, v15: State.Value, v16: State.Value, v17: State.Value): State.Value = $
    @pure def tuple18(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value, v15: State.Value, v16: State.Value, v17: State.Value, v18: State.Value): State.Value = $
    @pure def tuple19(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value, v15: State.Value, v16: State.Value, v17: State.Value, v18: State.Value, v19: State.Value): State.Value = $
    @pure def tuple20(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value, v15: State.Value, v16: State.Value, v17: State.Value, v18: State.Value, v19: State.Value, v20: State.Value): State.Value = $
    @pure def tuple21(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value, v15: State.Value, v16: State.Value, v17: State.Value, v18: State.Value, v19: State.Value, v20: State.Value, v21: State.Value): State.Value = $
    @pure def tuple22(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value, v15: State.Value, v16: State.Value, v17: State.Value, v18: State.Value, v19: State.Value, v20: State.Value, v21: State.Value, v22: State.Value): State.Value = $
  }

  @pure def err(pos: Position, message: String): Unit = {
    pos.uriOpt match {
      case Some(uri) => halt(s"$uri:${pos.beginLine}: $message")
      case _ => halt(s"${pos.beginLine}: $message")
    }
  }

  @strictpure def toValue[@mut O](tipe: State.Type, o: O): State.Value = State.Value.Native[O](tipe, 0, o)

  def binaryB(pos: Position, left: B, op: String, right: () => B): State.Value = {
    op match {
      case AST.Exp.BinaryOp.CondAnd => return toValue(State.Type.B, if (!left) F else right())
      case AST.Exp.BinaryOp.CondOr => return toValue(State.Type.B, if (left) T else right())
      case AST.Exp.BinaryOp.CondImply => return toValue(State.Type.B, if (!left) T else right())
      case AST.Exp.BinaryOp.EquivUni => return toValue(State.Type.B, left == right())
      case AST.Exp.BinaryOp.InequivUni => return toValue(State.Type.B, left != right())
      case AST.Exp.BinaryOp.And => return toValue(State.Type.B, left & right())
      case AST.Exp.BinaryOp.Or => return toValue(State.Type.B, left | right())
      case AST.Exp.BinaryOp.Xor => return toValue(State.Type.B, left |^ right())
      case AST.Exp.BinaryOp.Imply => return toValue(State.Type.B, left __>: right())
      case _ => halt(s"Infeasible: $left $op ${right()}")
    }
  }

  def binaryC(pos: Position, left: C, op: String, right: () => C): State.Value = {
    op match {
      case AST.Exp.BinaryOp.Add => return toValue(State.Type.C, left + right())
      case AST.Exp.BinaryOp.Sub => return toValue(State.Type.C, left - right())
      case AST.Exp.BinaryOp.EquivUni => return toValue(State.Type.B, left == right())
      case AST.Exp.BinaryOp.InequivUni => return toValue(State.Type.B, left != right())
      case AST.Exp.BinaryOp.Lt => return toValue(State.Type.B, left < right())
      case AST.Exp.BinaryOp.Le => return toValue(State.Type.B, left <= right())
      case AST.Exp.BinaryOp.Gt => return toValue(State.Type.B, left > right())
      case AST.Exp.BinaryOp.Ge => return toValue(State.Type.B, left >= right())
      case AST.Exp.BinaryOp.Shl => return toValue(State.Type.B, left << right())
      case AST.Exp.BinaryOp.Shr => return toValue(State.Type.B, left >> right())
      case AST.Exp.BinaryOp.Ushr => return toValue(State.Type.B, left >>> right())
      case _ => halt(s"Infeasible: $left $op $right")
    }
  }

  def binaryZ(pos: Position, left: Z, op: String, right: () => Z): State.Value = {
    op match {
      case AST.Exp.BinaryOp.Add => return toValue(State.Type.Z, left + right())
      case AST.Exp.BinaryOp.Sub => return toValue(State.Type.Z, left - right())
      case AST.Exp.BinaryOp.Mul => return toValue(State.Type.Z, left * right())
      case AST.Exp.BinaryOp.Div =>
        val r = right()
        if (r == 0) {
          err(pos, "Division by zero")
        }
        return toValue(State.Type.Z, left / r)
      case AST.Exp.BinaryOp.Rem =>
        val r = right()
        if (r == 0) {
          err(pos, "Division by zero")
        }
        return toValue(State.Type.Z, left % r)
      case AST.Exp.BinaryOp.EquivUni => return toValue(State.Type.B, left == right())
      case AST.Exp.BinaryOp.InequivUni => return toValue(State.Type.B, left != right())
      case AST.Exp.BinaryOp.Lt => return toValue(State.Type.B, left < right())
      case AST.Exp.BinaryOp.Le => return toValue(State.Type.B, left <= right())
      case AST.Exp.BinaryOp.Gt => return toValue(State.Type.B, left > right())
      case AST.Exp.BinaryOp.Ge => return toValue(State.Type.B, left >= right())
      case _ => halt(s"Infeasible: $left $op $right")
    }
  }

  def binaryF32(pos: Position, left: F32, op: String, right: () => F32): State.Value = {
    op match {
      case AST.Exp.BinaryOp.Add => return toValue(State.Type.F32, left + right())
      case AST.Exp.BinaryOp.Sub => return toValue(State.Type.F32, left - right())
      case AST.Exp.BinaryOp.Mul => return toValue(State.Type.F32, left * right())
      case AST.Exp.BinaryOp.Div =>
        val r = right()
        if (r == 0.0f) {
          err(pos, "Division by zero")
        }
        return toValue(State.Type.F32, left / r)
      case AST.Exp.BinaryOp.Rem =>
        val r = right()
        if (r == 0.0f) {
          err(pos, "Division by zero")
        }
        return toValue(State.Type.F32, left % r)
      case AST.Exp.BinaryOp.EquivUni => return toValue(State.Type.B, left == right())
      case AST.Exp.BinaryOp.InequivUni => return toValue(State.Type.B, left != right())
      case AST.Exp.BinaryOp.FpEq => return toValue(State.Type.B, left ~~ right())
      case AST.Exp.BinaryOp.FpNe => return toValue(State.Type.B, left !~ right())
      case AST.Exp.BinaryOp.Lt => return toValue(State.Type.B, left < right())
      case AST.Exp.BinaryOp.Le => return toValue(State.Type.B, left <= right())
      case AST.Exp.BinaryOp.Gt => return toValue(State.Type.B, left > right())
      case AST.Exp.BinaryOp.Ge => return toValue(State.Type.B, left >= right())
      case _ => halt(s"Infeasible: $left $op $right")
    }
  }

  def binaryF64(pos: Position, left: F64, op: String, right: () => F64): State.Value = {
    op match {
      case AST.Exp.BinaryOp.Add => return toValue(State.Type.F64, left + right())
      case AST.Exp.BinaryOp.Sub => return toValue(State.Type.F64, left - right())
      case AST.Exp.BinaryOp.Mul => return toValue(State.Type.F64, left * right())
      case AST.Exp.BinaryOp.Div =>
        val r = right()
        if (r == 0.0d) {
          err(pos, "Division by zero")
        }
        return toValue(State.Type.F64, left / r)
      case AST.Exp.BinaryOp.Rem =>
        val r = right()
        if (r == 0.0d) {
          err(pos, "Division by zero")
        }
        return toValue(State.Type.F64, left % r)
      case AST.Exp.BinaryOp.EquivUni => return toValue(State.Type.B, left == right())
      case AST.Exp.BinaryOp.InequivUni => return toValue(State.Type.B, left != right())
      case AST.Exp.BinaryOp.FpEq => return toValue(State.Type.B, left ~~ right())
      case AST.Exp.BinaryOp.FpNe => return toValue(State.Type.B, left !~ right())
      case AST.Exp.BinaryOp.Lt => return toValue(State.Type.B, left < right())
      case AST.Exp.BinaryOp.Le => return toValue(State.Type.B, left <= right())
      case AST.Exp.BinaryOp.Gt => return toValue(State.Type.B, left > right())
      case AST.Exp.BinaryOp.Ge => return toValue(State.Type.B, left >= right())
      case _ => halt(s"Infeasible: $left $op $right")
    }
  }

  def binaryR(pos: Position, left: R, op: String, right: () => R): State.Value = {
    op match {
      case AST.Exp.BinaryOp.Add => return toValue(State.Type.R, left + right())
      case AST.Exp.BinaryOp.Sub => return toValue(State.Type.R, left - right())
      case AST.Exp.BinaryOp.Mul => return toValue(State.Type.R, left * right())
      case AST.Exp.BinaryOp.Div =>
        val r = right()
        if (r == r"0.0") {
          err(pos, "Division by zero")
        }
        return toValue(State.Type.R, left / r)
      case AST.Exp.BinaryOp.EquivUni => return toValue(State.Type.B, left == right())
      case AST.Exp.BinaryOp.InequivUni => return toValue(State.Type.B, left != right())
      case AST.Exp.BinaryOp.Lt => return toValue(State.Type.B, left < right())
      case AST.Exp.BinaryOp.Le => return toValue(State.Type.B, left <= right())
      case AST.Exp.BinaryOp.Gt => return toValue(State.Type.B, left > right())
      case AST.Exp.BinaryOp.Ge => return toValue(State.Type.B, left >= right())
      case _ => halt(s"Infeasible: $left $op $right")
    }
  }

  def binaryString(pos: Position, left: String, op: String, right: () => String): State.Value = {
    op match {
      case AST.Exp.BinaryOp.EquivUni => return toValue(State.Type.B, left == right())
      case AST.Exp.BinaryOp.InequivUni => return toValue(State.Type.B, left != right())
      case AST.Exp.BinaryOp.Lt => return toValue(State.Type.B, left < right())
      case AST.Exp.BinaryOp.Le => return toValue(State.Type.B, left <= right())
      case AST.Exp.BinaryOp.Gt => return toValue(State.Type.B, left > right())
      case AST.Exp.BinaryOp.Ge => return toValue(State.Type.B, left >= right())
      case _ => halt(s"Infeasible: $left $op $right")
    }
  }

  def tuple2(state: State, v1: State.Value, v2: State.Value): State.Value = {
    if (!(v1.isObject || v2.isObject)) {
      return Ext.tuple2(v1, v2)
    }
    var map: State.Store = HashSMap.empty
    map = map + "_1" ~> state.alloc(v1)
    map = map + "_2" ~> state.alloc(v2)
    return State.Value.Object(State.Type.Tuple2, 0, map)
  }

  def tuple3(state: State, v1: State.Value, v2: State.Value, v3: State.Value): State.Value = {
    if (!(v1.isObject || v2.isObject || v3.isObject)) {
      return Ext.tuple3(v1, v2, v3)
    }
    var map: State.Store = HashSMap.empty
    map = map + "_1" ~> state.alloc(v1)
    map = map + "_2" ~> state.alloc(v2)
    map = map + "_3" ~> state.alloc(v3)
    return State.Value.Object(State.Type.Tuple3, 0, map)
  }

  def tuple4(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value): State.Value = {
    if (!(v1.isObject || v2.isObject || v3.isObject || v4.isObject)) {
      return Ext.tuple4(v1, v2, v3, v4)
    }
    var map: State.Store = HashSMap.empty
    map = map + "_1" ~> state.alloc(v1)
    map = map + "_2" ~> state.alloc(v2)
    map = map + "_3" ~> state.alloc(v3)
    map = map + "_4" ~> state.alloc(v4)
    return State.Value.Object(State.Type.Tuple4, 0, map)
  }

  def tuple5(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value): State.Value = {
    if (!(v1.isObject || v2.isObject || v3.isObject || v4.isObject || v5.isObject)) {
      return Ext.tuple5(v1, v2, v3, v4, v5)
    }
    var map: State.Store = HashSMap.empty
    map = map + "_1" ~> state.alloc(v1)
    map = map + "_2" ~> state.alloc(v2)
    map = map + "_3" ~> state.alloc(v3)
    map = map + "_4" ~> state.alloc(v4)
    map = map + "_5" ~> state.alloc(v5)
    return State.Value.Object(State.Type.Tuple5, 0, map)
  }

  def tuple6(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value): State.Value = {
    if (!(v1.isObject || v2.isObject || v3.isObject || v4.isObject || v5.isObject || v6.isObject)) {
      return Ext.tuple6(v1, v2, v3, v4, v5, v6)
    }
    var map: State.Store = HashSMap.empty
    map = map + "_1" ~> state.alloc(v1)
    map = map + "_2" ~> state.alloc(v2)
    map = map + "_3" ~> state.alloc(v3)
    map = map + "_4" ~> state.alloc(v4)
    map = map + "_5" ~> state.alloc(v5)
    map = map + "_6" ~> state.alloc(v6)
    return State.Value.Object(State.Type.Tuple6, 0, map)
  }

  def tuple7(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value): State.Value = {
    if (!(v1.isObject || v2.isObject || v3.isObject || v4.isObject || v5.isObject || v6.isObject || v7.isObject)) {
      return Ext.tuple7(v1, v2, v3, v4, v5, v6, v7)
    }
    var map: State.Store = HashSMap.empty
    map = map + "_1" ~> state.alloc(v1)
    map = map + "_2" ~> state.alloc(v2)
    map = map + "_3" ~> state.alloc(v3)
    map = map + "_4" ~> state.alloc(v4)
    map = map + "_5" ~> state.alloc(v5)
    map = map + "_6" ~> state.alloc(v6)
    map = map + "_7" ~> state.alloc(v7)
    return State.Value.Object(State.Type.Tuple7, 0, map)
  }

  def tuple8(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value): State.Value = {
    if (!(v1.isObject || v2.isObject || v3.isObject || v4.isObject || v5.isObject || v6.isObject || v7.isObject || v8.isObject)) {
      return Ext.tuple8(v1, v2, v3, v4, v5, v6, v7, v8)
    }
    var map: State.Store = HashSMap.empty
    map = map + "_1" ~> state.alloc(v1)
    map = map + "_2" ~> state.alloc(v2)
    map = map + "_3" ~> state.alloc(v3)
    map = map + "_4" ~> state.alloc(v4)
    map = map + "_5" ~> state.alloc(v5)
    map = map + "_6" ~> state.alloc(v6)
    map = map + "_7" ~> state.alloc(v7)
    map = map + "_8" ~> state.alloc(v8)
    return State.Value.Object(State.Type.Tuple8, 0, map)
  }

  def tuple9(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value): State.Value = {
    if (!(v1.isObject || v2.isObject || v3.isObject || v4.isObject || v5.isObject || v6.isObject || v7.isObject || v8.isObject || v9.isObject)) {
      return Ext.tuple9(v1, v2, v3, v4, v5, v6, v7, v8, v9)
    }
    var map: State.Store = HashSMap.empty
    map = map + "_1" ~> state.alloc(v1)
    map = map + "_2" ~> state.alloc(v2)
    map = map + "_3" ~> state.alloc(v3)
    map = map + "_4" ~> state.alloc(v4)
    map = map + "_5" ~> state.alloc(v5)
    map = map + "_6" ~> state.alloc(v6)
    map = map + "_7" ~> state.alloc(v7)
    map = map + "_8" ~> state.alloc(v8)
    map = map + "_9" ~> state.alloc(v9)
    return State.Value.Object(State.Type.Tuple9, 0, map)
  }

  def tuple10(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value): State.Value = {
    if (!(v1.isObject || v2.isObject || v3.isObject || v4.isObject || v5.isObject || v6.isObject || v7.isObject || v8.isObject || v9.isObject || v10.isObject)) {
      return Ext.tuple10(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10)
    }
    var map: State.Store = HashSMap.empty
    map = map + "_1" ~> state.alloc(v1)
    map = map + "_2" ~> state.alloc(v2)
    map = map + "_3" ~> state.alloc(v3)
    map = map + "_4" ~> state.alloc(v4)
    map = map + "_5" ~> state.alloc(v5)
    map = map + "_6" ~> state.alloc(v6)
    map = map + "_7" ~> state.alloc(v7)
    map = map + "_8" ~> state.alloc(v8)
    map = map + "_9" ~> state.alloc(v9)
    map = map + "_10" ~> state.alloc(v10)
    return State.Value.Object(State.Type.Tuple10, 0, map)
  }

  def tuple11(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value): State.Value = {
    if (!(v1.isObject || v2.isObject || v3.isObject || v4.isObject || v5.isObject || v6.isObject || v7.isObject || v8.isObject || v9.isObject || v10.isObject || v11.isObject)) {
      return Ext.tuple11(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11)
    }
    var map: State.Store = HashSMap.empty
    map = map + "_1" ~> state.alloc(v1)
    map = map + "_2" ~> state.alloc(v2)
    map = map + "_3" ~> state.alloc(v3)
    map = map + "_4" ~> state.alloc(v4)
    map = map + "_5" ~> state.alloc(v5)
    map = map + "_6" ~> state.alloc(v6)
    map = map + "_7" ~> state.alloc(v7)
    map = map + "_8" ~> state.alloc(v8)
    map = map + "_9" ~> state.alloc(v9)
    map = map + "_10" ~> state.alloc(v10)
    map = map + "_11" ~> state.alloc(v11)
    return State.Value.Object(State.Type.Tuple11, 0, map)
  }

  def tuple12(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value): State.Value = {
    if (!(v1.isObject || v2.isObject || v3.isObject || v4.isObject || v5.isObject || v6.isObject || v7.isObject || v8.isObject || v9.isObject || v10.isObject || v11.isObject || v12.isObject)) {
      return Ext.tuple12(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12)
    }
    var map: State.Store = HashSMap.empty
    map = map + "_1" ~> state.alloc(v1)
    map = map + "_2" ~> state.alloc(v2)
    map = map + "_3" ~> state.alloc(v3)
    map = map + "_4" ~> state.alloc(v4)
    map = map + "_5" ~> state.alloc(v5)
    map = map + "_6" ~> state.alloc(v6)
    map = map + "_7" ~> state.alloc(v7)
    map = map + "_8" ~> state.alloc(v8)
    map = map + "_9" ~> state.alloc(v9)
    map = map + "_10" ~> state.alloc(v10)
    map = map + "_11" ~> state.alloc(v11)
    map = map + "_12" ~> state.alloc(v12)
    return State.Value.Object(State.Type.Tuple12, 0, map)
  }

  def tuple13(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value): State.Value = {
    if (!(v1.isObject || v2.isObject || v3.isObject || v4.isObject || v5.isObject || v6.isObject || v7.isObject || v8.isObject || v9.isObject || v10.isObject || v11.isObject || v12.isObject || v13.isObject)) {
      return Ext.tuple13(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13)
    }
    var map: State.Store = HashSMap.empty
    map = map + "_1" ~> state.alloc(v1)
    map = map + "_2" ~> state.alloc(v2)
    map = map + "_3" ~> state.alloc(v3)
    map = map + "_4" ~> state.alloc(v4)
    map = map + "_5" ~> state.alloc(v5)
    map = map + "_6" ~> state.alloc(v6)
    map = map + "_7" ~> state.alloc(v7)
    map = map + "_8" ~> state.alloc(v8)
    map = map + "_9" ~> state.alloc(v9)
    map = map + "_10" ~> state.alloc(v10)
    map = map + "_11" ~> state.alloc(v11)
    map = map + "_12" ~> state.alloc(v12)
    map = map + "_13" ~> state.alloc(v13)
    return State.Value.Object(State.Type.Tuple13, 0, map)
  }

  def tuple14(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value): State.Value = {
    if (!(v1.isObject || v2.isObject || v3.isObject || v4.isObject || v5.isObject || v6.isObject || v7.isObject || v8.isObject || v9.isObject || v10.isObject || v11.isObject || v12.isObject || v13.isObject || v14.isObject)) {
      return Ext.tuple14(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14)
    }
    var map: State.Store = HashSMap.empty
    map = map + "_1" ~> state.alloc(v1)
    map = map + "_2" ~> state.alloc(v2)
    map = map + "_3" ~> state.alloc(v3)
    map = map + "_4" ~> state.alloc(v4)
    map = map + "_5" ~> state.alloc(v5)
    map = map + "_6" ~> state.alloc(v6)
    map = map + "_7" ~> state.alloc(v7)
    map = map + "_8" ~> state.alloc(v8)
    map = map + "_9" ~> state.alloc(v9)
    map = map + "_10" ~> state.alloc(v10)
    map = map + "_11" ~> state.alloc(v11)
    map = map + "_12" ~> state.alloc(v12)
    map = map + "_13" ~> state.alloc(v13)
    map = map + "_14" ~> state.alloc(v14)
    return State.Value.Object(State.Type.Tuple14, 0, map)
  }

  def tuple15(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value, v15: State.Value): State.Value = {
    if (!(v1.isObject || v2.isObject || v3.isObject || v4.isObject || v5.isObject || v6.isObject || v7.isObject || v8.isObject || v9.isObject || v10.isObject || v11.isObject || v12.isObject || v13.isObject || v14.isObject || v15.isObject)) {
      return Ext.tuple15(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15)
    }
    var map: State.Store = HashSMap.empty
    map = map + "_1" ~> state.alloc(v1)
    map = map + "_2" ~> state.alloc(v2)
    map = map + "_3" ~> state.alloc(v3)
    map = map + "_4" ~> state.alloc(v4)
    map = map + "_5" ~> state.alloc(v5)
    map = map + "_6" ~> state.alloc(v6)
    map = map + "_7" ~> state.alloc(v7)
    map = map + "_8" ~> state.alloc(v8)
    map = map + "_9" ~> state.alloc(v9)
    map = map + "_10" ~> state.alloc(v10)
    map = map + "_11" ~> state.alloc(v11)
    map = map + "_12" ~> state.alloc(v12)
    map = map + "_13" ~> state.alloc(v13)
    map = map + "_14" ~> state.alloc(v14)
    map = map + "_15" ~> state.alloc(v15)
    return State.Value.Object(State.Type.Tuple15, 0, map)
  }

  def tuple16(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value, v15: State.Value, v16: State.Value): State.Value = {
    if (!(v1.isObject || v2.isObject || v3.isObject || v4.isObject || v5.isObject || v6.isObject || v7.isObject || v8.isObject || v9.isObject || v10.isObject || v11.isObject || v12.isObject || v13.isObject || v14.isObject || v15.isObject || v16.isObject)) {
      return Ext.tuple16(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16)
    }
    var map: State.Store = HashSMap.empty
    map = map + "_1" ~> state.alloc(v1)
    map = map + "_2" ~> state.alloc(v2)
    map = map + "_3" ~> state.alloc(v3)
    map = map + "_4" ~> state.alloc(v4)
    map = map + "_5" ~> state.alloc(v5)
    map = map + "_6" ~> state.alloc(v6)
    map = map + "_7" ~> state.alloc(v7)
    map = map + "_8" ~> state.alloc(v8)
    map = map + "_9" ~> state.alloc(v9)
    map = map + "_10" ~> state.alloc(v10)
    map = map + "_11" ~> state.alloc(v11)
    map = map + "_12" ~> state.alloc(v12)
    map = map + "_13" ~> state.alloc(v13)
    map = map + "_14" ~> state.alloc(v14)
    map = map + "_15" ~> state.alloc(v15)
    map = map + "_16" ~> state.alloc(v16)
    return State.Value.Object(State.Type.Tuple16, 0, map)
  }

  def tuple17(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value, v15: State.Value, v16: State.Value, v17: State.Value): State.Value = {
    if (!(v1.isObject || v2.isObject || v3.isObject || v4.isObject || v5.isObject || v6.isObject || v7.isObject || v8.isObject || v9.isObject || v10.isObject || v11.isObject || v12.isObject || v13.isObject || v14.isObject || v15.isObject || v16.isObject || v17.isObject)) {
      return Ext.tuple17(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17)
    }
    var map: State.Store = HashSMap.empty
    map = map + "_1" ~> state.alloc(v1)
    map = map + "_2" ~> state.alloc(v2)
    map = map + "_3" ~> state.alloc(v3)
    map = map + "_4" ~> state.alloc(v4)
    map = map + "_5" ~> state.alloc(v5)
    map = map + "_6" ~> state.alloc(v6)
    map = map + "_7" ~> state.alloc(v7)
    map = map + "_8" ~> state.alloc(v8)
    map = map + "_9" ~> state.alloc(v9)
    map = map + "_10" ~> state.alloc(v10)
    map = map + "_11" ~> state.alloc(v11)
    map = map + "_12" ~> state.alloc(v12)
    map = map + "_13" ~> state.alloc(v13)
    map = map + "_14" ~> state.alloc(v14)
    map = map + "_15" ~> state.alloc(v15)
    map = map + "_16" ~> state.alloc(v16)
    map = map + "_17" ~> state.alloc(v17)
    return State.Value.Object(State.Type.Tuple17, 0, map)
  }

  def tuple18(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value, v15: State.Value, v16: State.Value, v17: State.Value, v18: State.Value): State.Value = {
    if (!(v1.isObject || v2.isObject || v3.isObject || v4.isObject || v5.isObject || v6.isObject || v7.isObject || v8.isObject || v9.isObject || v10.isObject || v11.isObject || v12.isObject || v13.isObject || v14.isObject || v15.isObject || v16.isObject || v17.isObject || v18.isObject)) {
      return Ext.tuple18(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18)
    }
    var map: State.Store = HashSMap.empty
    map = map + "_1" ~> state.alloc(v1)
    map = map + "_2" ~> state.alloc(v2)
    map = map + "_3" ~> state.alloc(v3)
    map = map + "_4" ~> state.alloc(v4)
    map = map + "_5" ~> state.alloc(v5)
    map = map + "_6" ~> state.alloc(v6)
    map = map + "_7" ~> state.alloc(v7)
    map = map + "_8" ~> state.alloc(v8)
    map = map + "_9" ~> state.alloc(v9)
    map = map + "_10" ~> state.alloc(v10)
    map = map + "_11" ~> state.alloc(v11)
    map = map + "_12" ~> state.alloc(v12)
    map = map + "_13" ~> state.alloc(v13)
    map = map + "_14" ~> state.alloc(v14)
    map = map + "_15" ~> state.alloc(v15)
    map = map + "_16" ~> state.alloc(v16)
    map = map + "_17" ~> state.alloc(v17)
    map = map + "_18" ~> state.alloc(v18)
    return State.Value.Object(State.Type.Tuple18, 0, map)
  }

  def tuple19(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value, v15: State.Value, v16: State.Value, v17: State.Value, v18: State.Value, v19: State.Value): State.Value = {
    if (!(v1.isObject || v2.isObject || v3.isObject || v4.isObject || v5.isObject || v6.isObject || v7.isObject || v8.isObject || v9.isObject || v10.isObject || v11.isObject || v12.isObject || v13.isObject || v14.isObject || v15.isObject || v16.isObject || v17.isObject || v18.isObject || v19.isObject)) {
      return Ext.tuple19(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19)
    }
    var map: State.Store = HashSMap.empty
    map = map + "_1" ~> state.alloc(v1)
    map = map + "_2" ~> state.alloc(v2)
    map = map + "_3" ~> state.alloc(v3)
    map = map + "_4" ~> state.alloc(v4)
    map = map + "_5" ~> state.alloc(v5)
    map = map + "_6" ~> state.alloc(v6)
    map = map + "_7" ~> state.alloc(v7)
    map = map + "_8" ~> state.alloc(v8)
    map = map + "_9" ~> state.alloc(v9)
    map = map + "_10" ~> state.alloc(v10)
    map = map + "_11" ~> state.alloc(v11)
    map = map + "_12" ~> state.alloc(v12)
    map = map + "_13" ~> state.alloc(v13)
    map = map + "_14" ~> state.alloc(v14)
    map = map + "_15" ~> state.alloc(v15)
    map = map + "_16" ~> state.alloc(v16)
    map = map + "_17" ~> state.alloc(v17)
    map = map + "_18" ~> state.alloc(v18)
    map = map + "_19" ~> state.alloc(v19)
    return State.Value.Object(State.Type.Tuple19, 0, map)
  }

  def tuple20(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value, v15: State.Value, v16: State.Value, v17: State.Value, v18: State.Value, v19: State.Value, v20: State.Value): State.Value = {
    if (!(v1.isObject || v2.isObject || v3.isObject || v4.isObject || v5.isObject || v6.isObject || v7.isObject || v8.isObject || v9.isObject || v10.isObject || v11.isObject || v12.isObject || v13.isObject || v14.isObject || v15.isObject || v16.isObject || v17.isObject || v18.isObject || v19.isObject || v20.isObject)) {
      return Ext.tuple20(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19, v20)
    }
    var map: State.Store = HashSMap.empty
    map = map + "_1" ~> state.alloc(v1)
    map = map + "_2" ~> state.alloc(v2)
    map = map + "_3" ~> state.alloc(v3)
    map = map + "_4" ~> state.alloc(v4)
    map = map + "_5" ~> state.alloc(v5)
    map = map + "_6" ~> state.alloc(v6)
    map = map + "_7" ~> state.alloc(v7)
    map = map + "_8" ~> state.alloc(v8)
    map = map + "_9" ~> state.alloc(v9)
    map = map + "_10" ~> state.alloc(v10)
    map = map + "_11" ~> state.alloc(v11)
    map = map + "_12" ~> state.alloc(v12)
    map = map + "_13" ~> state.alloc(v13)
    map = map + "_14" ~> state.alloc(v14)
    map = map + "_15" ~> state.alloc(v15)
    map = map + "_16" ~> state.alloc(v16)
    map = map + "_17" ~> state.alloc(v17)
    map = map + "_18" ~> state.alloc(v18)
    map = map + "_19" ~> state.alloc(v19)
    map = map + "_20" ~> state.alloc(v20)
    return State.Value.Object(State.Type.Tuple20, 0, map)
  }

  def tuple21(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value, v15: State.Value, v16: State.Value, v17: State.Value, v18: State.Value, v19: State.Value, v20: State.Value, v21: State.Value): State.Value = {
    if (!(v1.isObject || v2.isObject || v3.isObject || v4.isObject || v5.isObject || v6.isObject || v7.isObject || v8.isObject || v9.isObject || v10.isObject || v11.isObject || v12.isObject || v13.isObject || v14.isObject || v15.isObject || v16.isObject || v17.isObject || v18.isObject || v19.isObject || v20.isObject || v21.isObject)) {
      return Ext.tuple21(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19, v20, v21)
    }
    var map: State.Store = HashSMap.empty
    map = map + "_1" ~> state.alloc(v1)
    map = map + "_2" ~> state.alloc(v2)
    map = map + "_3" ~> state.alloc(v3)
    map = map + "_4" ~> state.alloc(v4)
    map = map + "_5" ~> state.alloc(v5)
    map = map + "_6" ~> state.alloc(v6)
    map = map + "_7" ~> state.alloc(v7)
    map = map + "_8" ~> state.alloc(v8)
    map = map + "_9" ~> state.alloc(v9)
    map = map + "_10" ~> state.alloc(v10)
    map = map + "_11" ~> state.alloc(v11)
    map = map + "_12" ~> state.alloc(v12)
    map = map + "_13" ~> state.alloc(v13)
    map = map + "_14" ~> state.alloc(v14)
    map = map + "_15" ~> state.alloc(v15)
    map = map + "_16" ~> state.alloc(v16)
    map = map + "_17" ~> state.alloc(v17)
    map = map + "_18" ~> state.alloc(v18)
    map = map + "_19" ~> state.alloc(v19)
    map = map + "_20" ~> state.alloc(v20)
    map = map + "_21" ~> state.alloc(v21)
    return State.Value.Object(State.Type.Tuple21, 0, map)
  }

  def tuple22(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value, v15: State.Value, v16: State.Value, v17: State.Value, v18: State.Value, v19: State.Value, v20: State.Value, v21: State.Value, v22: State.Value): State.Value = {
    if (!(v1.isObject || v2.isObject || v3.isObject || v4.isObject || v5.isObject || v6.isObject || v7.isObject || v8.isObject || v9.isObject || v10.isObject || v11.isObject || v12.isObject || v13.isObject || v14.isObject || v15.isObject || v16.isObject || v17.isObject || v18.isObject || v19.isObject || v20.isObject || v21.isObject || v22.isObject)) {
      return Ext.tuple22(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19, v20, v21, v22)
    }
    var map: State.Store = HashSMap.empty
    map = map + "_1" ~> state.alloc(v1)
    map = map + "_2" ~> state.alloc(v2)
    map = map + "_3" ~> state.alloc(v3)
    map = map + "_4" ~> state.alloc(v4)
    map = map + "_5" ~> state.alloc(v5)
    map = map + "_6" ~> state.alloc(v6)
    map = map + "_7" ~> state.alloc(v7)
    map = map + "_8" ~> state.alloc(v8)
    map = map + "_9" ~> state.alloc(v9)
    map = map + "_10" ~> state.alloc(v10)
    map = map + "_11" ~> state.alloc(v11)
    map = map + "_12" ~> state.alloc(v12)
    map = map + "_13" ~> state.alloc(v13)
    map = map + "_14" ~> state.alloc(v14)
    map = map + "_15" ~> state.alloc(v15)
    map = map + "_16" ~> state.alloc(v16)
    map = map + "_17" ~> state.alloc(v17)
    map = map + "_18" ~> state.alloc(v18)
    map = map + "_19" ~> state.alloc(v19)
    map = map + "_20" ~> state.alloc(v20)
    map = map + "_21" ~> state.alloc(v21)
    map = map + "_22" ~> state.alloc(v22)
    return State.Value.Object(State.Type.Tuple22, 0, map)
  }
}

import Evaluator._

@record class Evaluator(val th: TypeHierarchy, val state: State, val reflections: ISZ[Reflection]) {

  val printf: String => Unit = (s: String) => print(s)
  val eprintf: String => Unit = (s: String) => eprint(s)

  def evalBinary[T1, T2](pos: Position, exp: AST.CoreExp.Binary, l: State.Ptr, r: () => State.Ptr,
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

  def evalCoreExp(pos: message.Position, exp: AST.CoreExp,
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
      case exp: AST.CoreExp.LitEnum => halt("TODO")
      case exp: AST.CoreExp.Binary =>
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
          case AST.Exp.BinaryOp.Append =>
          case AST.Exp.BinaryOp.AppendAll =>
          case AST.Exp.BinaryOp.Prepend =>
        }

        state.tipe(l) match {
          case State.Type.B => return evalBinary(pos, exp, l, evalRight _, binaryB _)
          case State.Type.C => return evalBinary(pos, exp, l, evalRight _, binaryC _)
          case State.Type.Z => return evalBinary(pos, exp, l, evalRight _, binaryZ _)
          case State.Type.F32 => return evalBinary(pos, exp, l, evalRight _, binaryF32 _)
          case State.Type.F64 => return evalBinary(pos, exp, l, evalRight _, binaryF64 _)
          case State.Type.R => return evalBinary(pos, exp, l, evalRight _, binaryR _)
          case State.Type.String => return evalBinary(pos, exp, l, evalRight _, binaryR _)
          case _ => halt("TODO")
        }
      case _ => halt("TODO")
    }
  }

  @memoize def toCoreExp(exp: AST.Exp): AST.CoreExp = {
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
