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
import org.sireum.lang.{ast => AST}

object State {
  type Ptr = Z
  type Store = HashSMap[String, Ptr]

  @datatype trait Type {
    @strictpure def isImmutable: B
  }

  object Type {

    @datatype class Class(val isImmutable: B, val name: ISZ[String]) extends Type

    @datatype class Tuple(val isImmutable: B, val numOfArgs: Z) extends Type

    @datatype class Seq(val isImmutable: B) extends Type

    @datatype class Enum(val name: ISZ[String]) extends Type {
      @strictpure override def isImmutable: B = T
    }

    @datatype class Primitive(val name: ISZ[String]) extends Type {
      @strictpure override def isImmutable: B = T
    }

    @datatype class Bits(val name: ISZ[String]) extends Type {
      @strictpure override def isImmutable: B = T
    }

    @datatype class Range(val name: ISZ[String]) extends Type {
      @strictpure override def isImmutable: B = T
    }

    @datatype class Fun(val isByName: B, val numOfArgs: Z) extends Type {
      @strictpure override def isImmutable: B = T
    }

    val Nothing: Class = Class(T, AST.Typed.nothing.ids)
    val Unit: Class = Class(T, AST.Typed.unit.ids)
    val B: Primitive = Primitive(AST.Typed.bName)
    val Z: Primitive = Primitive(AST.Typed.zName)
    val C: Primitive = Primitive(AST.Typed.cName)
    val String: Primitive = Primitive(AST.Typed.stringName)
    val F32: Primitive = Primitive(AST.Typed.f32Name)
    val F64: Primitive = Primitive(AST.Typed.f64Name)
    val R: Primitive = Primitive(AST.Typed.rName)
    val ST: Primitive = Primitive(AST.Typed.stName)
  }

  @record trait Value {
    @pure def tipe: Type

    @pure def counter: Z

    @pure def inc(): Value

    @pure def dec(): Value

    def clone(state: State): Value

    def gc(state: State): Unit

    @strictpure def isNative: B = F

    @strictpure def isBox: B = F

    @pure def objectMap: Store = {
      halt("Infeasible")
    }

    @pure def boxedValue: Ptr = {
      halt("Infeasible")
    }

    def updateBoxedValue(newValue: Ptr): Value.Box = {
      halt("Infeasible")
    }

    def nativeValueString: String = {
      halt("Infeasible")
    }
  }

  object Value {
    @record class Empty extends Value {
      @strictpure def tipe: Type = {
        halt("Infeasible")
      }

      @strictpure def counter: Z = {
        halt("Infeasible")
      }

      @pure def inc(): Value = {
        halt("Infeasible")
      }

      @pure def dec(): Value = {
        halt("Infeasible")
      }

      def clone(state: State): Value = {
        halt("Infeasible")
      }

      def gc(state: State): Unit = {
        halt("Infeasible")
      }
    }

    @record class Box(val tipe: Type, val counter: Z, val value: Ptr) extends Value {
      @strictpure override def boxedValue: Ptr = value

      @pure def inc(): Value = {
        val thiz = this
        return thiz(counter = counter + 1)
      }

      @pure def dec(): Value = {
        val thiz = this
        return thiz(counter = counter - 1)
      }

      def clone(state: State): Value = {
        halt("Infeasible")
      }

      override def gc(state: State): Unit = {
        state.heap(value) = state.heap(value).dec()
        state.gc(value)
      }

      @strictpure override def updateBoxedValue(newValue: Ptr): Value.Box = Box(tipe, counter, newValue)
    }

    @record class Native[@mut T](val tipe: Type, val counter: Z, val value: T) extends Value {
      @pure def inc(): Value = {
        val thiz = this
        return thiz(counter = counter + 1)
      }

      @pure def dec(): Value = {
        val thiz = this
        return thiz(counter = counter - 1)
      }

      def clone(state: State): Value = {
        if (tipe.isImmutable) {
          return this
        }
        return Native(tipe, 0, value)
      }

      def gc(state: State): Unit = {
      }

      @strictpure override def isNative: B = T

      override def nativeValueString: String = {
        return value.string
      }
    }

    @record class Enum(val tipe: Type.Enum, val counter: Z, val id: String, val ordinal: Z) extends Value {
      @pure def inc(): Value = {
        val thiz = this
        return thiz(counter = counter + 1)
      }

      @pure def dec(): Value = {
        val thiz = this
        return thiz(counter = counter - 1)
      }

      def clone(state: State): Value = {
        return this
      }

      def gc(state: State): Unit = {
      }
    }

    @record class Object(val tipe: Type.Class, val counter: Z, val map: Store) extends Value {
      @pure def inc(): Value = {
        val thiz = this
        return thiz(counter = counter + 1)
      }

      @pure def dec(): Value = {
        val thiz = this
        return thiz(counter = counter - 1)
      }

      def clone(state: State): Value = {
        if (this.tipe.isImmutable) {
          return this
        }
        var newMap: Store = HashSMap.empty
        for (p <- map.entries) {
          newMap = newMap + p._1 ~> state.clone(p._2)
        }
        return Object(tipe, 0, newMap)
      }

      def gc(state: State): Unit = {
        for (ptr <- map.values) {
          state.heap(ptr) = state.heap(ptr).dec()
          state.gc(ptr)
        }
      }

      @strictpure override def objectMap: Store = map
    }

    @record class Tuple(val isImmutable: B, val counter: Z, val ptrs: ISZ[State.Ptr]) extends Value {
      @pure def inc(): Value = {
        val thiz = this
        return thiz(counter = counter + 1)
      }

      @pure def dec(): Value = {
        val thiz = this
        return thiz(counter = counter - 1)
      }

      def clone(state: State): Value = {
        if (isImmutable) {
          return this
        }
        val newPtrs = MSZ.create(ptrs.size, 0)
        for (i <- ptrs.indices) {
          state.heap(ptrs(i)) = state.heap(ptrs(i)).dec()
          val o = state.heap(ptrs(i))
          newPtrs(i) = state.alloc(o.clone(state))
        }
        return Tuple(isImmutable, 0, newPtrs.toISZ)
      }

      def gc(state: State): Unit = {
        for (ptr <- ptrs) {
          state.heap(ptr) = state.heap(ptr).dec()
          state.gc(ptr)
        }
      }

      @strictpure override def tipe: State.Type = State.Type.Tuple(isImmutable, ptrs.size)
    }
  }

  @strictpure def empty(initialCapacity: Z): State =
    State(HashSMap.empty, Stack.empty, for (i <- initialCapacity - 1 to 0 by -1) yield i,
      MSZ.create(initialCapacity, State.Value.Empty()))
}

@record class State(var store: State.Store,
                    var stack: Stack[State.Store],
                    var free: ISZ[State.Ptr],
                    var heap: MSZ[State.Value]) {

  def alloc(v: State.Value): State.Ptr = {
    if (free.isEmpty) {
      val newSize = heap.size * 3 / 2
      val oldHeap = heap
      heap = MSZ.create(newSize, State.Value.Empty())
      for (i <- oldHeap.indices) {
        heap(i) = oldHeap(i)
      }
      free = for (i <- newSize - 1 to oldHeap.size + 1 by -1) yield i
      heap(oldHeap.size) = v.inc()
      return oldHeap.size
    }
    val r = free(free.size - 1)
    heap(r) = v.inc()
    free = Util.Ext.dropRight(free)
    return r
  }

  def gc(index: State.Ptr): Unit = {
    if (heap(index).counter > 0) {
      return
    }
    heap(index).gc(this)
    heap(index) = State.Value.Empty()
    free = free :+ index
  }

  def declare(isVar: B, x: String, ptr: State.Ptr): Unit = {
    val newPtr = clone(ptr)
    heap(newPtr) = heap(newPtr).inc()
    store = store + x ~> (if (isVar) alloc(State.Value.Box(heap(newPtr).tipe, 1, newPtr)) else newPtr)
  }

  def clone(ptr: State.Ptr): State.Ptr = {
    assert(!heap(ptr).isBox)
    if (heap(ptr).tipe.isImmutable) {
      return ptr
    }
    if (heap(ptr).isNative) {
      return alloc(Util.Ext.deepClone(heap(ptr)))
    }
    return alloc(heap(ptr).clone(this))
  }

  def assign(x: String, ptr: State.Ptr): Unit = {
    assert(heap(store.get(x).get).isInstanceOf[State.Value.Box])
    val boxIndex = store.get(x).get
    val oldPtr = heap(boxIndex).boxedValue
    heap(oldPtr) = heap(oldPtr).dec()
    gc(oldPtr)
    val newPtr = clone(ptr)
    heap(newPtr) = heap(newPtr).inc()
    heap(boxIndex) = heap(boxIndex).updateBoxedValue(newPtr)
  }

  def undeclare(x: String): Unit = {
    val index = store.get(x).get
    heap(index) = heap(index).dec()
    gc(index)
  }

  def push(): Unit = {
    stack = stack.push(store)
    store = HashSMap.empty
  }

  def pop(): Unit = {
    val p = stack.pop.get
    stack = p._2
    store = p._1
  }

  def lookup(x: String): State.Ptr = {
    val i = store.get(x).get
    if (heap(i).isBox) {
      return heap(i).boxedValue
    } else {
      return i
    }
  }

  def lookupHeap(ptr: State.Ptr): State.Value = {
    assert(!heap(ptr).isInstanceOf[State.Value.Empty])
    return heap(ptr)
  }

  def tipe(ptr: State.Ptr): State.Type = {
    return heap(ptr).tipe
  }
}