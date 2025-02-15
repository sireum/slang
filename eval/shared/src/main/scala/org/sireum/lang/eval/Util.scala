// #Sireum
/*
 Copyright (c) 2017-2025, Robby, Kansas State University
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
import org.sireum.lang.{ast => AST}
import org.sireum.message.Position

object Util {

  @ext("Util_Ext") object Ext {
    @pure def unitValue: State.Value = $
    @pure def extractValue[O](v: State.Value): O = $
    @pure def dropRight[T](s: ISZ[T]): ISZ[T] = $
    @pure def deepClone(v: State.Value): State.Value = $
    @pure def tupleAccess(rs: ISZ[Reflection], o: State.Value, field: String): State.Value = $
    @pure def lookupIndex(rs: ISZ[Reflection], o: State.Value, i: State.Value): State.Value = $
    def updateIndex(o: State.Value, i: State.Value, v: State.Value): Unit = $
    @pure def unaryBits(op: AST.Exp.UnaryOp.Type, v: State.Value): State.Value = $
    @pure def binaryBits(left: State.Value, op: String, right: State.Value): State.Value = $
    @pure def append(s: State.Value, e: State.Value): State.Value = $
    @pure def prepend(e: State.Value, s: State.Value): State.Value = $
    @pure def appendAll(s1: State.Value, s2: State.Value): State.Value = $
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
    def invoke0(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value): State.Value = $
    def invoke1(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value, o1: State.Value): State.Value = $
    def invoke2(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value, o1: State.Value, o2: State.Value): State.Value = $
    def invoke3(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value, o1: State.Value, o2: State.Value, o3: State.Value): State.Value = $
    def invoke4(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value): State.Value = $
    def invoke5(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value): State.Value = $
    def invoke6(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value): State.Value = $
    def invoke7(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value): State.Value = $
    def invoke8(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value): State.Value = $
    def invoke9(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value): State.Value = $
    def invoke10(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value): State.Value = $
    def invoke11(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value): State.Value = $
    def invoke12(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value): State.Value = $
    def invoke13(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value): State.Value = $
    def invoke14(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value): State.Value = $
    def invoke15(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value): State.Value = $
    def invoke16(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value): State.Value = $
    def invoke17(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value, o17: State.Value): State.Value = $
    def invoke18(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value, o17: State.Value, o18: State.Value): State.Value = $
    def invoke19(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value, o17: State.Value, o18: State.Value, o19: State.Value): State.Value = $
    def invoke20(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value, o17: State.Value, o18: State.Value, o19: State.Value, o20: State.Value): State.Value = $
    def invoke21(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value, o17: State.Value, o18: State.Value, o19: State.Value, o20: State.Value, o21: State.Value): State.Value = $
    def invoke22(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value, o17: State.Value, o18: State.Value, o19: State.Value, o20: State.Value, o21: State.Value, o22: State.Value): State.Value = $
    def invoke23(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value, o17: State.Value, o18: State.Value, o19: State.Value, o20: State.Value, o21: State.Value, o22: State.Value, o23: State.Value): State.Value = $
    def invoke24(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value, o17: State.Value, o18: State.Value, o19: State.Value, o20: State.Value, o21: State.Value, o22: State.Value, o23: State.Value, o24: State.Value): State.Value = $
    def invoke25(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value, o17: State.Value, o18: State.Value, o19: State.Value, o20: State.Value, o21: State.Value, o22: State.Value, o23: State.Value, o24: State.Value, o25: State.Value): State.Value = $
    def invoke26(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value, o17: State.Value, o18: State.Value, o19: State.Value, o20: State.Value, o21: State.Value, o22: State.Value, o23: State.Value, o24: State.Value, o25: State.Value, o26: State.Value): State.Value = $
    def invoke27(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value, o17: State.Value, o18: State.Value, o19: State.Value, o20: State.Value, o21: State.Value, o22: State.Value, o23: State.Value, o24: State.Value, o25: State.Value, o26: State.Value, o27: State.Value): State.Value = $
    def invoke28(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value, o17: State.Value, o18: State.Value, o19: State.Value, o20: State.Value, o21: State.Value, o22: State.Value, o23: State.Value, o24: State.Value, o25: State.Value, o26: State.Value, o27: State.Value, o28: State.Value): State.Value = $
    def invoke29(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value, o17: State.Value, o18: State.Value, o19: State.Value, o20: State.Value, o21: State.Value, o22: State.Value, o23: State.Value, o24: State.Value, o25: State.Value, o26: State.Value, o27: State.Value, o28: State.Value, o29: State.Value): State.Value = $
    def invoke30(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value, o17: State.Value, o18: State.Value, o19: State.Value, o20: State.Value, o21: State.Value, o22: State.Value, o23: State.Value, o24: State.Value, o25: State.Value, o26: State.Value, o27: State.Value, o28: State.Value, o29: State.Value, o30: State.Value): State.Value = $
    def invoke31(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value, o17: State.Value, o18: State.Value, o19: State.Value, o20: State.Value, o21: State.Value, o22: State.Value, o23: State.Value, o24: State.Value, o25: State.Value, o26: State.Value, o27: State.Value, o28: State.Value, o29: State.Value, o30: State.Value, o31: State.Value): State.Value = $
    def invoke32(reflection: Reflection, tipe: State.Type, owner: String, name: String, receiver: State.Value, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value, o17: State.Value, o18: State.Value, o19: State.Value, o20: State.Value, o21: State.Value, o22: State.Value, o23: State.Value, o24: State.Value, o25: State.Value, o26: State.Value, o27: State.Value, o28: State.Value, o29: State.Value, o30: State.Value, o31: State.Value, o32: State.Value): State.Value = $
    def invokeStatic0(reflection: Reflection, tipe: State.Type, owner: String, name: String): State.Value = $
    def invokeStatic1(reflection: Reflection, tipe: State.Type, owner: String, name: String, o1: State.Value): State.Value = $
    def invokeStatic2(reflection: Reflection, tipe: State.Type, owner: String, name: String, o1: State.Value, o2: State.Value): State.Value = $
    def invokeStatic3(reflection: Reflection, tipe: State.Type, owner: String, name: String, o1: State.Value, o2: State.Value, o3: State.Value): State.Value = $
    def invokeStatic4(reflection: Reflection, tipe: State.Type, owner: String, name: String, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value): State.Value = $
    def invokeStatic5(reflection: Reflection, tipe: State.Type, owner: String, name: String, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value): State.Value = $
    def invokeStatic6(reflection: Reflection, tipe: State.Type, owner: String, name: String, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value): State.Value = $
    def invokeStatic7(reflection: Reflection, tipe: State.Type, owner: String, name: String, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value): State.Value = $
    def invokeStatic8(reflection: Reflection, tipe: State.Type, owner: String, name: String, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value): State.Value = $
    def invokeStatic9(reflection: Reflection, tipe: State.Type, owner: String, name: String, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value): State.Value = $
    def invokeStatic10(reflection: Reflection, tipe: State.Type, owner: String, name: String, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value): State.Value = $
    def invokeStatic11(reflection: Reflection, tipe: State.Type, owner: String, name: String, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value): State.Value = $
    def invokeStatic12(reflection: Reflection, tipe: State.Type, owner: String, name: String, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value): State.Value = $
    def invokeStatic13(reflection: Reflection, tipe: State.Type, owner: String, name: String, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value): State.Value = $
    def invokeStatic14(reflection: Reflection, tipe: State.Type, owner: String, name: String, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value): State.Value = $
    def invokeStatic15(reflection: Reflection, tipe: State.Type, owner: String, name: String, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value): State.Value = $
    def invokeStatic16(reflection: Reflection, tipe: State.Type, owner: String, name: String, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value): State.Value = $
    def invokeStatic17(reflection: Reflection, tipe: State.Type, owner: String, name: String, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value, o17: State.Value): State.Value = $
    def invokeStatic18(reflection: Reflection, tipe: State.Type, owner: String, name: String, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value, o17: State.Value, o18: State.Value): State.Value = $
    def invokeStatic19(reflection: Reflection, tipe: State.Type, owner: String, name: String, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value, o17: State.Value, o18: State.Value, o19: State.Value): State.Value = $
    def invokeStatic20(reflection: Reflection, tipe: State.Type, owner: String, name: String, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value, o17: State.Value, o18: State.Value, o19: State.Value, o20: State.Value): State.Value = $
    def invokeStatic21(reflection: Reflection, tipe: State.Type, owner: String, name: String, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value, o17: State.Value, o18: State.Value, o19: State.Value, o20: State.Value, o21: State.Value): State.Value = $
    def invokeStatic22(reflection: Reflection, tipe: State.Type, owner: String, name: String, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value, o17: State.Value, o18: State.Value, o19: State.Value, o20: State.Value, o21: State.Value, o22: State.Value): State.Value = $
    def invokeStatic23(reflection: Reflection, tipe: State.Type, owner: String, name: String, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value, o17: State.Value, o18: State.Value, o19: State.Value, o20: State.Value, o21: State.Value, o22: State.Value, o23: State.Value): State.Value = $
    def invokeStatic24(reflection: Reflection, tipe: State.Type, owner: String, name: String, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value, o17: State.Value, o18: State.Value, o19: State.Value, o20: State.Value, o21: State.Value, o22: State.Value, o23: State.Value, o24: State.Value): State.Value = $
    def invokeStatic25(reflection: Reflection, tipe: State.Type, owner: String, name: String, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value, o17: State.Value, o18: State.Value, o19: State.Value, o20: State.Value, o21: State.Value, o22: State.Value, o23: State.Value, o24: State.Value, o25: State.Value): State.Value = $
    def invokeStatic26(reflection: Reflection, tipe: State.Type, owner: String, name: String, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value, o17: State.Value, o18: State.Value, o19: State.Value, o20: State.Value, o21: State.Value, o22: State.Value, o23: State.Value, o24: State.Value, o25: State.Value, o26: State.Value): State.Value = $
    def invokeStatic27(reflection: Reflection, tipe: State.Type, owner: String, name: String, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value, o17: State.Value, o18: State.Value, o19: State.Value, o20: State.Value, o21: State.Value, o22: State.Value, o23: State.Value, o24: State.Value, o25: State.Value, o26: State.Value, o27: State.Value): State.Value = $
    def invokeStatic28(reflection: Reflection, tipe: State.Type, owner: String, name: String, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value, o17: State.Value, o18: State.Value, o19: State.Value, o20: State.Value, o21: State.Value, o22: State.Value, o23: State.Value, o24: State.Value, o25: State.Value, o26: State.Value, o27: State.Value, o28: State.Value): State.Value = $
    def invokeStatic29(reflection: Reflection, tipe: State.Type, owner: String, name: String, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value, o17: State.Value, o18: State.Value, o19: State.Value, o20: State.Value, o21: State.Value, o22: State.Value, o23: State.Value, o24: State.Value, o25: State.Value, o26: State.Value, o27: State.Value, o28: State.Value, o29: State.Value): State.Value = $
    def invokeStatic30(reflection: Reflection, tipe: State.Type, owner: String, name: String, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value, o17: State.Value, o18: State.Value, o19: State.Value, o20: State.Value, o21: State.Value, o22: State.Value, o23: State.Value, o24: State.Value, o25: State.Value, o26: State.Value, o27: State.Value, o28: State.Value, o29: State.Value, o30: State.Value): State.Value = $
    def invokeStatic31(reflection: Reflection, tipe: State.Type, owner: String, name: String, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value, o17: State.Value, o18: State.Value, o19: State.Value, o20: State.Value, o21: State.Value, o22: State.Value, o23: State.Value, o24: State.Value, o25: State.Value, o26: State.Value, o27: State.Value, o28: State.Value, o29: State.Value, o30: State.Value, o31: State.Value): State.Value = $
    def invokeStatic32(reflection: Reflection, tipe: State.Type, owner: String, name: String, o1: State.Value, o2: State.Value, o3: State.Value, o4: State.Value, o5: State.Value, o6: State.Value, o7: State.Value, o8: State.Value, o9: State.Value, o10: State.Value, o11: State.Value, o12: State.Value, o13: State.Value, o14: State.Value, o15: State.Value, o16: State.Value, o17: State.Value, o18: State.Value, o19: State.Value, o20: State.Value, o21: State.Value, o22: State.Value, o23: State.Value, o24: State.Value, o25: State.Value, o26: State.Value, o27: State.Value, o28: State.Value, o29: State.Value, o30: State.Value, o31: State.Value, o32: State.Value): State.Value = $
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
    if (v1.isNative || v2.isNative) {
      return Ext.tuple2(v1, v2)
    }
    val isImmutable: B = v1.tipe.isImmutable && v2.tipe.isImmutable
    return State.Value.Tuple(isImmutable, 0, ISZ(state.alloc(v1), state.alloc(v2)))
  }

  def tuple3(state: State, v1: State.Value, v2: State.Value, v3: State.Value): State.Value = {
    if (v1.isNative || v2.isNative || v3.isNative) {
      return Ext.tuple3(v1, v2, v3)
    }
    val isImmutable: B = v1.tipe.isImmutable && v2.tipe.isImmutable && v3.tipe.isImmutable
    return State.Value.Tuple(isImmutable, 0, ISZ(state.alloc(v1), state.alloc(v2), state.alloc(v3)))
  }

  def tuple4(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value): State.Value = {
    if (v1.isNative || v2.isNative || v3.isNative || v4.isNative) {
      return Ext.tuple4(v1, v2, v3, v4)
    }
    val isImmutable: B = v1.tipe.isImmutable && v2.tipe.isImmutable && v3.tipe.isImmutable && v4.tipe.isImmutable
    return State.Value.Tuple(isImmutable, 0, ISZ(state.alloc(v1), state.alloc(v2), state.alloc(v3), state.alloc(v4)))
  }

  def tuple5(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value): State.Value = {
    if (v1.isNative || v2.isNative || v3.isNative || v4.isNative || v5.isNative) {
      return Ext.tuple5(v1, v2, v3, v4, v5)
    }
    val isImmutable: B = v1.tipe.isImmutable && v2.tipe.isImmutable && v3.tipe.isImmutable && v4.tipe.isImmutable && v5.tipe.isImmutable
    return State.Value.Tuple(isImmutable, 0, ISZ(state.alloc(v1), state.alloc(v2), state.alloc(v3), state.alloc(v4), state.alloc(v5)))
  }

  def tuple6(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value): State.Value = {
    if (v1.isNative || v2.isNative || v3.isNative || v4.isNative || v5.isNative || v6.isNative) {
      return Ext.tuple6(v1, v2, v3, v4, v5, v6)
    }
    val isImmutable: B = v1.tipe.isImmutable && v2.tipe.isImmutable && v3.tipe.isImmutable && v4.tipe.isImmutable && v5.tipe.isImmutable && v6.tipe.isImmutable
    return State.Value.Tuple(isImmutable, 0, ISZ(state.alloc(v1), state.alloc(v2), state.alloc(v3), state.alloc(v4), state.alloc(v5), state.alloc(v6)))
  }

  def tuple7(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value): State.Value = {
    if (v1.isNative || v2.isNative || v3.isNative || v4.isNative || v5.isNative || v6.isNative || v7.isNative) {
      return Ext.tuple7(v1, v2, v3, v4, v5, v6, v7)
    }
    val isImmutable: B = v1.tipe.isImmutable && v2.tipe.isImmutable && v3.tipe.isImmutable && v4.tipe.isImmutable && v5.tipe.isImmutable && v6.tipe.isImmutable && v7.tipe.isImmutable
    return State.Value.Tuple(isImmutable, 0, ISZ(state.alloc(v1), state.alloc(v2), state.alloc(v3), state.alloc(v4), state.alloc(v5), state.alloc(v6), state.alloc(v7)))
  }

  def tuple8(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value): State.Value = {
    if (v1.isNative || v2.isNative || v3.isNative || v4.isNative || v5.isNative || v6.isNative || v7.isNative || v8.isNative) {
      return Ext.tuple8(v1, v2, v3, v4, v5, v6, v7, v8)
    }
    val isImmutable: B = v1.tipe.isImmutable && v2.tipe.isImmutable && v3.tipe.isImmutable && v4.tipe.isImmutable && v5.tipe.isImmutable && v6.tipe.isImmutable && v7.tipe.isImmutable && v8.tipe.isImmutable
    return State.Value.Tuple(isImmutable, 0, ISZ(state.alloc(v1), state.alloc(v2), state.alloc(v3), state.alloc(v4), state.alloc(v5), state.alloc(v6), state.alloc(v7), state.alloc(v8)))
  }

  def tuple9(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value): State.Value = {
    if (v1.isNative || v2.isNative || v3.isNative || v4.isNative || v5.isNative || v6.isNative || v7.isNative || v8.isNative || v9.isNative) {
      return Ext.tuple9(v1, v2, v3, v4, v5, v6, v7, v8, v9)
    }
    val isImmutable: B = v1.tipe.isImmutable && v2.tipe.isImmutable && v3.tipe.isImmutable && v4.tipe.isImmutable && v5.tipe.isImmutable && v6.tipe.isImmutable && v7.tipe.isImmutable && v8.tipe.isImmutable && v9.tipe.isImmutable
    return State.Value.Tuple(isImmutable, 0, ISZ(state.alloc(v1), state.alloc(v2), state.alloc(v3), state.alloc(v4), state.alloc(v5), state.alloc(v6), state.alloc(v7), state.alloc(v8), state.alloc(v9)))
  }

  def tuple10(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value): State.Value = {
    if (v1.isNative || v2.isNative || v3.isNative || v4.isNative || v5.isNative || v6.isNative || v7.isNative || v8.isNative || v9.isNative || v10.isNative) {
      return Ext.tuple10(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10)
    }
    val isImmutable: B = v1.tipe.isImmutable && v2.tipe.isImmutable && v3.tipe.isImmutable && v4.tipe.isImmutable && v5.tipe.isImmutable && v6.tipe.isImmutable && v7.tipe.isImmutable && v8.tipe.isImmutable && v9.tipe.isImmutable && v10.tipe.isImmutable
    return State.Value.Tuple(isImmutable, 0, ISZ(state.alloc(v1), state.alloc(v2), state.alloc(v3), state.alloc(v4), state.alloc(v5), state.alloc(v6), state.alloc(v7), state.alloc(v8), state.alloc(v9), state.alloc(v10)))
  }

  def tuple11(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value): State.Value = {
    if (v1.isNative || v2.isNative || v3.isNative || v4.isNative || v5.isNative || v6.isNative || v7.isNative || v8.isNative || v9.isNative || v10.isNative || v11.isNative) {
      return Ext.tuple11(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11)
    }
    val isImmutable: B = v1.tipe.isImmutable && v2.tipe.isImmutable && v3.tipe.isImmutable && v4.tipe.isImmutable && v5.tipe.isImmutable && v6.tipe.isImmutable && v7.tipe.isImmutable && v8.tipe.isImmutable && v9.tipe.isImmutable && v10.tipe.isImmutable && v11.tipe.isImmutable
    return State.Value.Tuple(isImmutable, 0, ISZ(state.alloc(v1), state.alloc(v2), state.alloc(v3), state.alloc(v4), state.alloc(v5), state.alloc(v6), state.alloc(v7), state.alloc(v8), state.alloc(v9), state.alloc(v10), state.alloc(v11)))
  }

  def tuple12(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value): State.Value = {
    if (v1.isNative || v2.isNative || v3.isNative || v4.isNative || v5.isNative || v6.isNative || v7.isNative || v8.isNative || v9.isNative || v10.isNative || v11.isNative || v12.isNative) {
      return Ext.tuple12(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12)
    }
    val isImmutable: B = v1.tipe.isImmutable && v2.tipe.isImmutable && v3.tipe.isImmutable && v4.tipe.isImmutable && v5.tipe.isImmutable && v6.tipe.isImmutable && v7.tipe.isImmutable && v8.tipe.isImmutable && v9.tipe.isImmutable && v10.tipe.isImmutable && v11.tipe.isImmutable && v12.tipe.isImmutable
    return State.Value.Tuple(isImmutable, 0, ISZ(state.alloc(v1), state.alloc(v2), state.alloc(v3), state.alloc(v4), state.alloc(v5), state.alloc(v6), state.alloc(v7), state.alloc(v8), state.alloc(v9), state.alloc(v10), state.alloc(v11), state.alloc(v12)))
  }

  def tuple13(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value): State.Value = {
    if (v1.isNative || v2.isNative || v3.isNative || v4.isNative || v5.isNative || v6.isNative || v7.isNative || v8.isNative || v9.isNative || v10.isNative || v11.isNative || v12.isNative || v13.isNative) {
      return Ext.tuple13(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13)
    }
    val isImmutable: B = v1.tipe.isImmutable && v2.tipe.isImmutable && v3.tipe.isImmutable && v4.tipe.isImmutable && v5.tipe.isImmutable && v6.tipe.isImmutable && v7.tipe.isImmutable && v8.tipe.isImmutable && v9.tipe.isImmutable && v10.tipe.isImmutable && v11.tipe.isImmutable && v12.tipe.isImmutable && v13.tipe.isImmutable
    return State.Value.Tuple(isImmutable, 0, ISZ(state.alloc(v1), state.alloc(v2), state.alloc(v3), state.alloc(v4), state.alloc(v5), state.alloc(v6), state.alloc(v7), state.alloc(v8), state.alloc(v9), state.alloc(v10), state.alloc(v11), state.alloc(v12), state.alloc(v13)))
  }

  def tuple14(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value): State.Value = {
    if (v1.isNative || v2.isNative || v3.isNative || v4.isNative || v5.isNative || v6.isNative || v7.isNative || v8.isNative || v9.isNative || v10.isNative || v11.isNative || v12.isNative || v13.isNative || v14.isNative) {
      return Ext.tuple14(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14)
    }
    val isImmutable: B = v1.tipe.isImmutable && v2.tipe.isImmutable && v3.tipe.isImmutable && v4.tipe.isImmutable && v5.tipe.isImmutable && v6.tipe.isImmutable && v7.tipe.isImmutable && v8.tipe.isImmutable && v9.tipe.isImmutable && v10.tipe.isImmutable && v11.tipe.isImmutable && v12.tipe.isImmutable && v13.tipe.isImmutable && v14.tipe.isImmutable
    return State.Value.Tuple(isImmutable, 0, ISZ(state.alloc(v1), state.alloc(v2), state.alloc(v3), state.alloc(v4), state.alloc(v5), state.alloc(v6), state.alloc(v7), state.alloc(v8), state.alloc(v9), state.alloc(v10), state.alloc(v11), state.alloc(v12), state.alloc(v13), state.alloc(v14)))
  }

  def tuple15(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value, v15: State.Value): State.Value = {
    if (v1.isNative || v2.isNative || v3.isNative || v4.isNative || v5.isNative || v6.isNative || v7.isNative || v8.isNative || v9.isNative || v10.isNative || v11.isNative || v12.isNative || v13.isNative || v14.isNative || v15.isNative) {
      return Ext.tuple15(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15)
    }
    val isImmutable: B = v1.tipe.isImmutable && v2.tipe.isImmutable && v3.tipe.isImmutable && v4.tipe.isImmutable && v5.tipe.isImmutable && v6.tipe.isImmutable && v7.tipe.isImmutable && v8.tipe.isImmutable && v9.tipe.isImmutable && v10.tipe.isImmutable && v11.tipe.isImmutable && v12.tipe.isImmutable && v13.tipe.isImmutable && v14.tipe.isImmutable && v15.tipe.isImmutable
    return State.Value.Tuple(isImmutable, 0, ISZ(state.alloc(v1), state.alloc(v2), state.alloc(v3), state.alloc(v4), state.alloc(v5), state.alloc(v6), state.alloc(v7), state.alloc(v8), state.alloc(v9), state.alloc(v10), state.alloc(v11), state.alloc(v12), state.alloc(v13), state.alloc(v14), state.alloc(v15)))
  }

  def tuple16(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value, v15: State.Value, v16: State.Value): State.Value = {
    if (v1.isNative || v2.isNative || v3.isNative || v4.isNative || v5.isNative || v6.isNative || v7.isNative || v8.isNative || v9.isNative || v10.isNative || v11.isNative || v12.isNative || v13.isNative || v14.isNative || v15.isNative || v16.isNative) {
      return Ext.tuple16(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16)
    }
    val isImmutable: B = v1.tipe.isImmutable && v2.tipe.isImmutable && v3.tipe.isImmutable && v4.tipe.isImmutable && v5.tipe.isImmutable && v6.tipe.isImmutable && v7.tipe.isImmutable && v8.tipe.isImmutable && v9.tipe.isImmutable && v10.tipe.isImmutable && v11.tipe.isImmutable && v12.tipe.isImmutable && v13.tipe.isImmutable && v14.tipe.isImmutable && v15.tipe.isImmutable && v16.tipe.isImmutable
    return State.Value.Tuple(isImmutable, 0, ISZ(state.alloc(v1), state.alloc(v2), state.alloc(v3), state.alloc(v4), state.alloc(v5), state.alloc(v6), state.alloc(v7), state.alloc(v8), state.alloc(v9), state.alloc(v10), state.alloc(v11), state.alloc(v12), state.alloc(v13), state.alloc(v14), state.alloc(v15), state.alloc(v16)))
  }

  def tuple17(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value, v15: State.Value, v16: State.Value, v17: State.Value): State.Value = {
    if (v1.isNative || v2.isNative || v3.isNative || v4.isNative || v5.isNative || v6.isNative || v7.isNative || v8.isNative || v9.isNative || v10.isNative || v11.isNative || v12.isNative || v13.isNative || v14.isNative || v15.isNative || v16.isNative || v17.isNative) {
      return Ext.tuple17(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17)
    }
    val isImmutable: B = v1.tipe.isImmutable && v2.tipe.isImmutable && v3.tipe.isImmutable && v4.tipe.isImmutable && v5.tipe.isImmutable && v6.tipe.isImmutable && v7.tipe.isImmutable && v8.tipe.isImmutable && v9.tipe.isImmutable && v10.tipe.isImmutable && v11.tipe.isImmutable && v12.tipe.isImmutable && v13.tipe.isImmutable && v14.tipe.isImmutable && v15.tipe.isImmutable && v16.tipe.isImmutable && v17.tipe.isImmutable
    return State.Value.Tuple(isImmutable, 0, ISZ(state.alloc(v1), state.alloc(v2), state.alloc(v3), state.alloc(v4), state.alloc(v5), state.alloc(v6), state.alloc(v7), state.alloc(v8), state.alloc(v9), state.alloc(v10), state.alloc(v11), state.alloc(v12), state.alloc(v13), state.alloc(v14), state.alloc(v15), state.alloc(v16), state.alloc(v17)))
  }

  def tuple18(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value, v15: State.Value, v16: State.Value, v17: State.Value, v18: State.Value): State.Value = {
    if (v1.isNative || v2.isNative || v3.isNative || v4.isNative || v5.isNative || v6.isNative || v7.isNative || v8.isNative || v9.isNative || v10.isNative || v11.isNative || v12.isNative || v13.isNative || v14.isNative || v15.isNative || v16.isNative || v17.isNative || v18.isNative) {
      return Ext.tuple18(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18)
    }
    val isImmutable: B = v1.tipe.isImmutable && v2.tipe.isImmutable && v3.tipe.isImmutable && v4.tipe.isImmutable && v5.tipe.isImmutable && v6.tipe.isImmutable && v7.tipe.isImmutable && v8.tipe.isImmutable && v9.tipe.isImmutable && v10.tipe.isImmutable && v11.tipe.isImmutable && v12.tipe.isImmutable && v13.tipe.isImmutable && v14.tipe.isImmutable && v15.tipe.isImmutable && v16.tipe.isImmutable && v17.tipe.isImmutable && v18.tipe.isImmutable
    return State.Value.Tuple(isImmutable, 0, ISZ(state.alloc(v1), state.alloc(v2), state.alloc(v3), state.alloc(v4), state.alloc(v5), state.alloc(v6), state.alloc(v7), state.alloc(v8), state.alloc(v9), state.alloc(v10), state.alloc(v11), state.alloc(v12), state.alloc(v13), state.alloc(v14), state.alloc(v15), state.alloc(v16), state.alloc(v17), state.alloc(v18)))
  }

  def tuple19(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value, v15: State.Value, v16: State.Value, v17: State.Value, v18: State.Value, v19: State.Value): State.Value = {
    if (v1.isNative || v2.isNative || v3.isNative || v4.isNative || v5.isNative || v6.isNative || v7.isNative || v8.isNative || v9.isNative || v10.isNative || v11.isNative || v12.isNative || v13.isNative || v14.isNative || v15.isNative || v16.isNative || v17.isNative || v18.isNative || v19.isNative) {
      return Ext.tuple19(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19)
    }
    val isImmutable: B = v1.tipe.isImmutable && v2.tipe.isImmutable && v3.tipe.isImmutable && v4.tipe.isImmutable && v5.tipe.isImmutable && v6.tipe.isImmutable && v7.tipe.isImmutable && v8.tipe.isImmutable && v9.tipe.isImmutable && v10.tipe.isImmutable && v11.tipe.isImmutable && v12.tipe.isImmutable && v13.tipe.isImmutable && v14.tipe.isImmutable && v15.tipe.isImmutable && v16.tipe.isImmutable && v17.tipe.isImmutable && v18.tipe.isImmutable && v19.tipe.isImmutable
    return State.Value.Tuple(isImmutable, 0, ISZ(state.alloc(v1), state.alloc(v2), state.alloc(v3), state.alloc(v4), state.alloc(v5), state.alloc(v6), state.alloc(v7), state.alloc(v8), state.alloc(v9), state.alloc(v10), state.alloc(v11), state.alloc(v12), state.alloc(v13), state.alloc(v14), state.alloc(v15), state.alloc(v16), state.alloc(v17), state.alloc(v18), state.alloc(v19)))
  }

  def tuple20(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value, v15: State.Value, v16: State.Value, v17: State.Value, v18: State.Value, v19: State.Value, v20: State.Value): State.Value = {
    if (v1.isNative || v2.isNative || v3.isNative || v4.isNative || v5.isNative || v6.isNative || v7.isNative || v8.isNative || v9.isNative || v10.isNative || v11.isNative || v12.isNative || v13.isNative || v14.isNative || v15.isNative || v16.isNative || v17.isNative || v18.isNative || v19.isNative || v20.isNative) {
      return Ext.tuple20(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19, v20)
    }
    val isImmutable: B = v1.tipe.isImmutable && v2.tipe.isImmutable && v3.tipe.isImmutable && v4.tipe.isImmutable && v5.tipe.isImmutable && v6.tipe.isImmutable && v7.tipe.isImmutable && v8.tipe.isImmutable && v9.tipe.isImmutable && v10.tipe.isImmutable && v11.tipe.isImmutable && v12.tipe.isImmutable && v13.tipe.isImmutable && v14.tipe.isImmutable && v15.tipe.isImmutable && v16.tipe.isImmutable && v17.tipe.isImmutable && v18.tipe.isImmutable && v19.tipe.isImmutable && v20.tipe.isImmutable
    return State.Value.Tuple(isImmutable, 0, ISZ(state.alloc(v1), state.alloc(v2), state.alloc(v3), state.alloc(v4), state.alloc(v5), state.alloc(v6), state.alloc(v7), state.alloc(v8), state.alloc(v9), state.alloc(v10), state.alloc(v11), state.alloc(v12), state.alloc(v13), state.alloc(v14), state.alloc(v15), state.alloc(v16), state.alloc(v17), state.alloc(v18), state.alloc(v19), state.alloc(v20)))
  }

  def tuple21(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value, v15: State.Value, v16: State.Value, v17: State.Value, v18: State.Value, v19: State.Value, v20: State.Value, v21: State.Value): State.Value = {
    if (v1.isNative || v2.isNative || v3.isNative || v4.isNative || v5.isNative || v6.isNative || v7.isNative || v8.isNative || v9.isNative || v10.isNative || v11.isNative || v12.isNative || v13.isNative || v14.isNative || v15.isNative || v16.isNative || v17.isNative || v18.isNative || v19.isNative || v20.isNative || v21.isNative) {
      return Ext.tuple21(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19, v20, v21)
    }
    val isImmutable: B = v1.tipe.isImmutable && v2.tipe.isImmutable && v3.tipe.isImmutable && v4.tipe.isImmutable && v5.tipe.isImmutable && v6.tipe.isImmutable && v7.tipe.isImmutable && v8.tipe.isImmutable && v9.tipe.isImmutable && v10.tipe.isImmutable && v11.tipe.isImmutable && v12.tipe.isImmutable && v13.tipe.isImmutable && v14.tipe.isImmutable && v15.tipe.isImmutable && v16.tipe.isImmutable && v17.tipe.isImmutable && v18.tipe.isImmutable && v19.tipe.isImmutable && v20.tipe.isImmutable && v21.tipe.isImmutable
    return State.Value.Tuple(isImmutable, 0, ISZ(state.alloc(v1), state.alloc(v2), state.alloc(v3), state.alloc(v4), state.alloc(v5), state.alloc(v6), state.alloc(v7), state.alloc(v8), state.alloc(v9), state.alloc(v10), state.alloc(v11), state.alloc(v12), state.alloc(v13), state.alloc(v14), state.alloc(v15), state.alloc(v16), state.alloc(v17), state.alloc(v18), state.alloc(v19), state.alloc(v20), state.alloc(v21)))
  }

  def tuple22(state: State, v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value, v15: State.Value, v16: State.Value, v17: State.Value, v18: State.Value, v19: State.Value, v20: State.Value, v21: State.Value, v22: State.Value): State.Value = {
    if (v1.isNative || v2.isNative || v3.isNative || v4.isNative || v5.isNative || v6.isNative || v7.isNative || v8.isNative || v9.isNative || v10.isNative || v11.isNative || v12.isNative || v13.isNative || v14.isNative || v15.isNative || v16.isNative || v17.isNative || v18.isNative || v19.isNative || v20.isNative || v21.isNative || v22.isNative) {
      return Ext.tuple22(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19, v20, v21, v22)
    }
    val isImmutable: B = v1.tipe.isImmutable && v2.tipe.isImmutable && v3.tipe.isImmutable && v4.tipe.isImmutable && v5.tipe.isImmutable && v6.tipe.isImmutable && v7.tipe.isImmutable && v8.tipe.isImmutable && v9.tipe.isImmutable && v10.tipe.isImmutable && v11.tipe.isImmutable && v12.tipe.isImmutable && v13.tipe.isImmutable && v14.tipe.isImmutable && v15.tipe.isImmutable && v16.tipe.isImmutable && v17.tipe.isImmutable && v18.tipe.isImmutable && v19.tipe.isImmutable && v20.tipe.isImmutable && v21.tipe.isImmutable && v22.tipe.isImmutable
    return State.Value.Tuple(isImmutable, 0, ISZ(state.alloc(v1), state.alloc(v2), state.alloc(v3), state.alloc(v4), state.alloc(v5), state.alloc(v6), state.alloc(v7), state.alloc(v8), state.alloc(v9), state.alloc(v10), state.alloc(v11), state.alloc(v12), state.alloc(v13), state.alloc(v14), state.alloc(v15), state.alloc(v16), state.alloc(v17), state.alloc(v18), state.alloc(v19), state.alloc(v20), state.alloc(v21), state.alloc(v22)))
  }


  @strictpure def isSeq(e: AST.CoreExp): B = e.tipe match {
    case t: AST.Typed.Name => t.ids == AST.Typed.isName || t.ids == AST.Typed.msName
    case _ => F
  }

}
