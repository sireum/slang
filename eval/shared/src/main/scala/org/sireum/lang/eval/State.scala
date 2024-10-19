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

  @datatype class CallFrame(val methodContext: ISZ[String], val isInstance: B, val filename: String, val line: Z, val store: Store)

  @datatype trait Type {
    @strictpure def isImmutable: B
  }

  object Type {

    @enum object Kind {
      "Immutable"
      "Mutable"
      "IS"
      "MS"
      "Enum"
      "Primitive"
      "Bits"
      "Range"
    }

    @datatype class Class(val kind: Kind.Type, val name: ISZ[String]) extends Type {
      @strictpure def isImmutable: B = kind match {
        case Kind.Mutable => F
        case Kind.MS => F
        case _ => T
      }
      @strictpure def isSeq: B = kind match {
        case Kind.MS => T
        case Kind.IS => T
        case _ => F
      }
    }

    @datatype class Tuple(val isImmutable: B, val numOfArgs: Z) extends Type

    @datatype class Fun(val isByName: B, val numOfArgs: Z) extends Type {
      @strictpure override def isImmutable: B = T
    }

    val Nothing: Class = Class(Kind.Immutable, AST.Typed.nothing.ids)
    val Unit: Class = Class(Kind.Immutable, AST.Typed.unit.ids)
    val B: Class = Class(Kind.Primitive, AST.Typed.bName)
    val Z: Class = Class(Kind.Primitive, AST.Typed.zName)
    val C: Class = Class(Kind.Primitive, AST.Typed.cName)
    val String: Class = Class(Kind.Primitive, AST.Typed.stringName)
    val F32: Class = Class(Kind.Primitive, AST.Typed.f32Name)
    val F64: Class = Class(Kind.Primitive, AST.Typed.f64Name)
    val R: Class = Class(Kind.Primitive, AST.Typed.rName)
    val ST: Class = Class(Kind.Primitive, AST.Typed.stName)
    val IS: Class = Class(Kind.MS, AST.Typed.isName)
    val MS: Class = Class(Kind.MS, AST.Typed.msName)
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

    @record class Enum(val tipe: Type.Class, val counter: Z, val id: String, val ordinal: Z) extends Value {
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
    State(HashSSet.empty, HashSMap.empty, ISZ(), F, "", 0, HashSMap.empty, Stack.empty,
      for (i <- initialCapacity - 1 to 0 by -1) yield i, MSZ.create(initialCapacity, State.Value.Empty()))
}

@record class State(var objectInits: HashSSet[ISZ[String]],
                    var globalStore: HashSMap[ISZ[String], State.Ptr],
                    var methodContext: ISZ[String],
                    var isInstance: B,
                    var filename: String,
                    var line: Z,
                    var store: State.Store,
                    var stack: Stack[State.CallFrame],
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

  def assignLocal(x: String, ptr: State.Ptr): Unit = {
    assert(heap(store.get(x).get).isInstanceOf[State.Value.Box])
    val boxIndex = store.get(x).get
    val oldPtr = heap(boxIndex).boxedValue
    heap(oldPtr) = heap(oldPtr).dec()
    gc(oldPtr)
    val newPtr = clone(ptr)
    heap(newPtr) = heap(newPtr).inc()
    heap(boxIndex) = heap(boxIndex).updateBoxedValue(newPtr)
  }

  def assignGlobal(x: ISZ[String], ptr: State.Ptr): Unit = {
    val oldPtr = globalStore.get(x).get
    heap(oldPtr) = heap(oldPtr).dec()
    gc(oldPtr)
    val newPtr = clone(ptr)
    heap(newPtr) = heap(newPtr).inc()
  }

  def assignField(o: State.Ptr, x: String, ptr: State.Ptr): Unit = {
    halt("TODO")
  }

  def assignIndex(o: State.Ptr, i: State.Ptr, ptr: State.Ptr): Unit = {
    halt("TODO")
  }

  def undeclare(x: String): Unit = {
    val index = store.get(x).get
    heap(index) = heap(index).dec()
    gc(index)
  }

  def push(newMethodContext: ISZ[String], newIsInstance: B, newFilename: String, newLine: Z): Unit = {
    stack = stack.push(State.CallFrame(methodContext, isInstance, newFilename, line, store))
    methodContext = newMethodContext
    isInstance = newIsInstance
    line = newLine
    store = HashSMap.empty
  }

  def pop(): Unit = {
    val p = stack.pop.get
    stack = p._2
    val cf = p._1
    methodContext = cf.methodContext
    isInstance = cf.isInstance
    filename = cf.filename
    line = cf.line
    store = cf.store
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

  def lookupHeapNative[T](ptr: State.Ptr): T = {
    assert(!heap(ptr).isInstanceOf[State.Value.Empty])
    return Util.Ext.extractValue[T](heap(ptr))
  }

  def lookupGlobal(x: ISZ[String]): State.Ptr = {
    return globalStore.get(x).get
  }

  def tipe(ptr: State.Ptr): State.Type = {
    return heap(ptr).tipe
  }

  def printStackTrace(isErr: B, msg: ST): Unit = {
    val cfs = State.CallFrame(methodContext, isInstance, filename, line, store) +: stack.elements
    val r =
      st"""$msg
          |  ${(for (cf <- cfs) yield st"at ${(cf.methodContext, ".")}(${cf.filename}:${cf.line})", "\n")}"""
    cprintln(isErr, r.render)
  }
}