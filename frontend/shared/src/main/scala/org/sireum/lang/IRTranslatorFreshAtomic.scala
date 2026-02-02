/*
 Copyright (c) 2017-2026,Robby, Kansas State University
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
package org.sireum.lang

import org.sireum._
import org.sireum.$internal.MutableMarker

import java.util.concurrent.atomic.AtomicLong

object IRTranslatorFreshAtomic {
  def createFresh: IRTranslator.Fresh = new IRTranslatorFreshAtomic
}

final class IRTranslatorFreshAtomic extends IRTranslator.Fresh {
  val _label = new AtomicLong(0)
  val _temp = new AtomicLong(1)
  var owned = false
  var clonable = true
  override def setLabel(n: Z): Unit = _label.set(n.toLong)
  override def setTemp(n: Z): Unit = _temp.set(n.toLong)
  override def label(): Z = _label.getAndAdd(1)
  override def temp(): Z = _temp.getAndAdd(1)
  override def $clonable: Boolean = clonable
  override def $clonable_=(b: Boolean): MutableMarker = {
    clonable = b
    return this
  }
  override def $owned: Boolean = owned
  override def $owned_=(b: Boolean): MutableMarker = {
    owned = b
    return this
  }
  override def $clone: MutableMarker = {
    val r = new IRTranslatorFreshAtomic
    r.setLabel(_label.get())
    r.setTemp(_temp.get())
    return r
  }
  override def string: String = s"Fresh(${_label.get()}, ${_temp.get()})"

}