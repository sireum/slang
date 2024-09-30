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

import org.sireum.$internal.MutableMarker
import org.sireum._

object Evaluator_Ext {
  @pure def extractValue[O](v: State.Value): O = v.asInstanceOf[State.Value.Native[_]].value.asInstanceOf[O]
  @pure def dropRight[T](s: ISZ[T]): ISZ[T] = new IS(s.companion, s.data, s.length - 1, s.boxer)
  @pure def deepClone(v: State.Value): State.Value = State.Value.Native[MutableMarker](v.tipe, 0,
    v.asInstanceOf[State.Value.Native[_]].value.asInstanceOf[MutableMarker].$clone)
  @pure def tuple2(v1: State.Value, v2: State.Value): State.Value =
    State.Value.Native(State.Type.Tuple2, 0, (extractValue(v1), extractValue(v2)))
  @pure def tuple3(v1: State.Value, v2: State.Value, v3: State.Value): State.Value =
    State.Value.Native(State.Type.Tuple3, 0, (extractValue(v1), extractValue(v2), extractValue(v3)))
  @pure def tuple4(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value): State.Value =
    State.Value.Native(State.Type.Tuple4, 0, (extractValue(v1), extractValue(v2), extractValue(v3), extractValue(v4)))
  @pure def tuple5(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value): State.Value =
    State.Value.Native(State.Type.Tuple5, 0, (extractValue(v1), extractValue(v2), extractValue(v3), extractValue(v4), extractValue(v5)))
  @pure def tuple6(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value): State.Value =
    State.Value.Native(State.Type.Tuple6, 0, (extractValue(v1), extractValue(v2), extractValue(v3), extractValue(v4), extractValue(v5), extractValue(v6)))
  @pure def tuple7(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value): State.Value =
    State.Value.Native(State.Type.Tuple7, 0, (extractValue(v1), extractValue(v2), extractValue(v3), extractValue(v4), extractValue(v5), extractValue(v6), extractValue(v7)))
  @pure def tuple8(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value): State.Value =
    State.Value.Native(State.Type.Tuple8, 0, (extractValue(v1), extractValue(v2), extractValue(v3), extractValue(v4), extractValue(v5), extractValue(v6), extractValue(v7), extractValue(v8)))
  @pure def tuple9(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value): State.Value =
    State.Value.Native(State.Type.Tuple9, 0, (extractValue(v1), extractValue(v2), extractValue(v3), extractValue(v4), extractValue(v5), extractValue(v6), extractValue(v7), extractValue(v8), extractValue(v9)))
  @pure def tuple10(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value): State.Value =
    State.Value.Native(State.Type.Tuple10, 0, (extractValue(v1), extractValue(v2), extractValue(v3), extractValue(v4), extractValue(v5), extractValue(v6), extractValue(v7), extractValue(v8), extractValue(v9), extractValue(v10)))
  @pure def tuple11(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value): State.Value =
    State.Value.Native(State.Type.Tuple11, 0, (extractValue(v1), extractValue(v2), extractValue(v3), extractValue(v4), extractValue(v5), extractValue(v6), extractValue(v7), extractValue(v8), extractValue(v9), extractValue(v10), extractValue(v11)))
  @pure def tuple12(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value): State.Value =
    State.Value.Native(State.Type.Tuple12, 0, (extractValue(v1), extractValue(v2), extractValue(v3), extractValue(v4), extractValue(v5), extractValue(v6), extractValue(v7), extractValue(v8), extractValue(v9), extractValue(v10), extractValue(v11), extractValue(v12)))
  @pure def tuple13(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value): State.Value =
    State.Value.Native(State.Type.Tuple13, 0, (extractValue(v1), extractValue(v2), extractValue(v3), extractValue(v4), extractValue(v5), extractValue(v6), extractValue(v7), extractValue(v8), extractValue(v9), extractValue(v10), extractValue(v11), extractValue(v12), extractValue(v13)))
  @pure def tuple14(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value): State.Value =
    State.Value.Native(State.Type.Tuple14, 0, (extractValue(v1), extractValue(v2), extractValue(v3), extractValue(v4), extractValue(v5), extractValue(v6), extractValue(v7), extractValue(v8), extractValue(v9), extractValue(v10), extractValue(v11), extractValue(v12), extractValue(v13), extractValue(v14)))
  @pure def tuple15(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value, v15: State.Value): State.Value =
    State.Value.Native(State.Type.Tuple15, 0, (extractValue(v1), extractValue(v2), extractValue(v3), extractValue(v4), extractValue(v5), extractValue(v6), extractValue(v7), extractValue(v8), extractValue(v9), extractValue(v10), extractValue(v11), extractValue(v12), extractValue(v13), extractValue(v14), extractValue(v15)))
  @pure def tuple16(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value, v15: State.Value, v16: State.Value): State.Value =
    State.Value.Native(State.Type.Tuple16, 0, (extractValue(v1), extractValue(v2), extractValue(v3), extractValue(v4), extractValue(v5), extractValue(v6), extractValue(v7), extractValue(v8), extractValue(v9), extractValue(v10), extractValue(v11), extractValue(v12), extractValue(v13), extractValue(v14), extractValue(v15), extractValue(v16)))
  @pure def tuple17(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value, v15: State.Value, v16: State.Value, v17: State.Value): State.Value =
    State.Value.Native(State.Type.Tuple17, 0, (extractValue(v1), extractValue(v2), extractValue(v3), extractValue(v4), extractValue(v5), extractValue(v6), extractValue(v7), extractValue(v8), extractValue(v9), extractValue(v10), extractValue(v11), extractValue(v12), extractValue(v13), extractValue(v14), extractValue(v15), extractValue(v16), extractValue(v17)))
  @pure def tuple18(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value, v15: State.Value, v16: State.Value, v17: State.Value, v18: State.Value): State.Value =
    State.Value.Native(State.Type.Tuple18, 0, (extractValue(v1), extractValue(v2), extractValue(v3), extractValue(v4), extractValue(v5), extractValue(v6), extractValue(v7), extractValue(v8), extractValue(v9), extractValue(v10), extractValue(v11), extractValue(v12), extractValue(v13), extractValue(v14), extractValue(v15), extractValue(v16), extractValue(v17), extractValue(v18)))
  @pure def tuple19(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value, v15: State.Value, v16: State.Value, v17: State.Value, v18: State.Value, v19: State.Value): State.Value =
    State.Value.Native(State.Type.Tuple19, 0, (extractValue(v1), extractValue(v2), extractValue(v3), extractValue(v4), extractValue(v5), extractValue(v6), extractValue(v7), extractValue(v8), extractValue(v9), extractValue(v10), extractValue(v11), extractValue(v12), extractValue(v13), extractValue(v14), extractValue(v15), extractValue(v16), extractValue(v17), extractValue(v18), extractValue(v19)))
  @pure def tuple20(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value, v15: State.Value, v16: State.Value, v17: State.Value, v18: State.Value, v19: State.Value, v20: State.Value): State.Value =
    State.Value.Native(State.Type.Tuple20, 0, (extractValue(v1), extractValue(v2), extractValue(v3), extractValue(v4), extractValue(v5), extractValue(v6), extractValue(v7), extractValue(v8), extractValue(v9), extractValue(v10), extractValue(v11), extractValue(v12), extractValue(v13), extractValue(v14), extractValue(v15), extractValue(v16), extractValue(v17), extractValue(v18), extractValue(v19), extractValue(v20)))
  @pure def tuple21(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value, v15: State.Value, v16: State.Value, v17: State.Value, v18: State.Value, v19: State.Value, v20: State.Value, v21: State.Value): State.Value =
    State.Value.Native(State.Type.Tuple21, 0, (extractValue(v1), extractValue(v2), extractValue(v3), extractValue(v4), extractValue(v5), extractValue(v6), extractValue(v7), extractValue(v8), extractValue(v9), extractValue(v10), extractValue(v11), extractValue(v12), extractValue(v13), extractValue(v14), extractValue(v15), extractValue(v16), extractValue(v17), extractValue(v18), extractValue(v19), extractValue(v20), extractValue(v21)))
  @pure def tuple22(v1: State.Value, v2: State.Value, v3: State.Value, v4: State.Value, v5: State.Value, v6: State.Value, v7: State.Value, v8: State.Value, v9: State.Value, v10: State.Value, v11: State.Value, v12: State.Value, v13: State.Value, v14: State.Value, v15: State.Value, v16: State.Value, v17: State.Value, v18: State.Value, v19: State.Value, v20: State.Value, v21: State.Value, v22: State.Value): State.Value =
    State.Value.Native(State.Type.Tuple22, 0, (extractValue(v1), extractValue(v2), extractValue(v3), extractValue(v4), extractValue(v5), extractValue(v6), extractValue(v7), extractValue(v8), extractValue(v9), extractValue(v10), extractValue(v11), extractValue(v12), extractValue(v13), extractValue(v14), extractValue(v15), extractValue(v16), extractValue(v17), extractValue(v18), extractValue(v19), extractValue(v20), extractValue(v21), extractValue(v22)))
}
