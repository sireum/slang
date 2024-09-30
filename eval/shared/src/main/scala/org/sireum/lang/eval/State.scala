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

    @datatype class Class(val isImmutable: B, val name: ISZ[String]) extends Type {
      @memoize def tupleNumOfArgsOpt: Option[Z] = {
        if (name.size != 3 || name(0) != "org" || name(1) != "sireum") {
          return None()
        }
        val tuplePrefix: String = "Tuple"
        val mTuplePrefix: String = "MTuple"
        val nameOps = ops.StringOps(name(2))
        if (nameOps.startsWith(tuplePrefix)) {
          return org.sireum.Z(nameOps.substring(tuplePrefix.size, nameOps.size))
        }
        if (nameOps.startsWith(mTuplePrefix)) {
          return org.sireum.Z(nameOps.substring(mTuplePrefix.size, nameOps.size))
        }
        return None()
      }
    }

    @datatype class Seq(val isImmutable: B) extends Type

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
    val Tuple2: Class = Class(T, AST.Typed.sireumName :+ "Tuple2")
    val Tuple3: Class = Class(T, AST.Typed.sireumName :+ "Tuple3")
    val Tuple4: Class = Class(T, AST.Typed.sireumName :+ "Tuple4")
    val Tuple5: Class = Class(T, AST.Typed.sireumName :+ "Tuple5")
    val Tuple6: Class = Class(T, AST.Typed.sireumName :+ "Tuple6")
    val Tuple7: Class = Class(T, AST.Typed.sireumName :+ "Tuple7")
    val Tuple8: Class = Class(T, AST.Typed.sireumName :+ "Tuple8")
    val Tuple9: Class = Class(T, AST.Typed.sireumName :+ "Tuple9")
    val Tuple10: Class = Class(T, AST.Typed.sireumName :+ "Tuple10")
    val Tuple11: Class = Class(T, AST.Typed.sireumName :+ "Tuple11")
    val Tuple12: Class = Class(T, AST.Typed.sireumName :+ "Tuple12")
    val Tuple13: Class = Class(T, AST.Typed.sireumName :+ "Tuple13")
    val Tuple14: Class = Class(T, AST.Typed.sireumName :+ "Tuple14")
    val Tuple15: Class = Class(T, AST.Typed.sireumName :+ "Tuple15")
    val Tuple16: Class = Class(T, AST.Typed.sireumName :+ "Tuple16")
    val Tuple17: Class = Class(T, AST.Typed.sireumName :+ "Tuple17")
    val Tuple18: Class = Class(T, AST.Typed.sireumName :+ "Tuple18")
    val Tuple19: Class = Class(T, AST.Typed.sireumName :+ "Tuple19")
    val Tuple20: Class = Class(T, AST.Typed.sireumName :+ "Tuple20")
    val Tuple21: Class = Class(T, AST.Typed.sireumName :+ "Tuple21")
    val Tuple22: Class = Class(T, AST.Typed.sireumName :+ "Tuple22")
    val MTuple2: Class = Class(F, AST.Typed.sireumName :+ "MTuple2")
    val MTuple3: Class = Class(F, AST.Typed.sireumName :+ "MTuple3")
    val MTuple4: Class = Class(F, AST.Typed.sireumName :+ "MTuple4")
    val MTuple5: Class = Class(F, AST.Typed.sireumName :+ "MTuple5")
    val MTuple6: Class = Class(F, AST.Typed.sireumName :+ "MTuple6")
    val MTuple7: Class = Class(F, AST.Typed.sireumName :+ "MTuple7")
    val MTuple8: Class = Class(F, AST.Typed.sireumName :+ "MTuple8")
    val MTuple9: Class = Class(F, AST.Typed.sireumName :+ "MTuple9")
    val MTuple10: Class = Class(F, AST.Typed.sireumName :+ "MTuple10")
    val MTuple11: Class = Class(F, AST.Typed.sireumName :+ "MTuple11")
    val MTuple12: Class = Class(F, AST.Typed.sireumName :+ "MTuple12")
    val MTuple13: Class = Class(F, AST.Typed.sireumName :+ "MTuple13")
    val MTuple14: Class = Class(F, AST.Typed.sireumName :+ "MTuple14")
    val MTuple15: Class = Class(F, AST.Typed.sireumName :+ "MTuple15")
    val MTuple16: Class = Class(F, AST.Typed.sireumName :+ "MTuple16")
    val MTuple17: Class = Class(F, AST.Typed.sireumName :+ "MTuple17")
    val MTuple18: Class = Class(F, AST.Typed.sireumName :+ "MTuple18")
    val MTuple19: Class = Class(F, AST.Typed.sireumName :+ "MTuple19")
    val MTuple20: Class = Class(F, AST.Typed.sireumName :+ "MTuple20")
    val MTuple21: Class = Class(F, AST.Typed.sireumName :+ "MTuple21")
    val MTuple22: Class = Class(F, AST.Typed.sireumName :+ "MTuple22")
  }

  @record trait Value {
    @pure def tipe: Type
    @pure def counter: Z
    @pure def inc(): Value
    @pure def dec(): Value
    @pure def isObject: B
    @pure def isBox: B
    @pure def objectMap: Store
    @pure def boxedValue: Ptr
    @pure def updateBoxedValue(newValue: Ptr): Value.Box
    @pure def nativeValueString: String
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
      @pure def isObject: B = {
        halt("Infeasible")
      }
      @pure def isBox: B = {
        halt("Infeasible")
      }
      @pure def objectMap: Store = {
        halt("Infeasible")
      }
      @pure def boxedValue: Ptr = {
        halt("Infeasible")
      }
      @pure def updateBoxedValue(newValue: Ptr): Value.Box = {
        halt("Infeasible")
      }
      @pure def nativeValueString: String = {
        halt("Infeasible")
      }
    }
    @record class Box(val tipe: Type, val counter: Z, val isObject: B, val boxedValue: Ptr) extends Value {
      @pure def inc(): Value = {
        val thiz = this
        return thiz(counter = counter + 1)
      }
      @pure def dec(): Value = {
        val thiz = this
        return thiz(counter = counter - 1)
      }
      @strictpure def isBox: B = T
      @pure def objectMap: Store = {
        halt("Infeasible")
      }
      @strictpure def updateBoxedValue(newValue: Ptr): Value.Box = this(boxedValue = newValue)
      @pure def nativeValueString: String = {
        halt("Infeasible")
      }
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
      @strictpure def isObject: B = F
      @strictpure def isBox: B = F
      @pure def objectMap: Store = {
        halt("Infeasible")
      }
      @pure def boxedValue: Ptr = {
        halt("Infeasible")
      }
      @pure def updateBoxedValue(newValue: Ptr): Value.Box = {
        halt("Infeasible")
      }
      @pure def nativeValueString: String = {
        return value.string
      }
    }
    @record class Object(val tipe: Type.Class, val counter: Z, val objectMap: Store) extends Value {
      @pure def inc(): Value = {
        val thiz = this
        return thiz(counter = counter + 1)
      }
      @pure def dec(): Value = {
        val thiz = this
        return thiz(counter = counter - 1)
      }
      @strictpure def isObject: B = T
      @strictpure def isBox: B = F
      @pure def boxedValue: Ptr = {
        halt("Infeasible")
      }
      @pure def updateBoxedValue(newValue: Ptr): Value.Box = {
        halt("Infeasible")
      }
      @pure def nativeValueString: String = {
        halt("Infeasible")
      }
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
    if (heap(index).isObject) {
      for (i <- heap(index).objectMap.values) {
        heap(i) = heap(i).dec()
        gc(i)
      }
    }
    heap(index) = State.Value.Empty()
    free = free :+ index
  }

  def declare(isVar: B, x: String, ptr: State.Ptr): Unit = {
    val newPtr = clone(ptr)
    heap(newPtr) = heap(newPtr).inc()
    store = store + x ~> (if (isVar) alloc(State.Value.Box(heap(newPtr).tipe, 1, heap(ptr).isObject, newPtr)) else newPtr)
  }

  def clone(ptr: State.Ptr): State.Ptr = {
    assert(!heap(ptr).isBox)
    if (heap(ptr).tipe.isImmutable) {
      return ptr
    }
    if (!heap(ptr).isObject) {
      return alloc(Util.Ext.deepClone(heap(ptr)))
    }
    var map: State.Store = HashSMap.empty
    for (p <- heap(ptr).objectMap.entries) {
      map = map + p._1 ~> clone(p._2)
    }
    return alloc(State.Value.Object(heap(ptr).tipe.asInstanceOf[State.Type.Class], 0, map))
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