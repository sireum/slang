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
  @pure def toValue[O](tipe: State.Type, o: O): State.Value = State.Value.Native[O](tipe, 0, o)
  @pure def extractValue[O](v: State.Value): O = v.asInstanceOf[State.Value.Native[_]].value.asInstanceOf[O]
  @pure def toString(v: State.Value): String = v.asInstanceOf[State.Value.Native[_]].value.string
  @pure def dropRight[T](s: ISZ[T]): ISZ[T] = new IS(s.companion, s.data, s.length - 1, s.boxer)
  @pure def deepClone(v: State.Value): State.Value = State.Value.Native[MutableMarker](v.tipe, 0,
    v.asInstanceOf[State.Value.Native[_]].value.asInstanceOf[MutableMarker].$clone)
}
