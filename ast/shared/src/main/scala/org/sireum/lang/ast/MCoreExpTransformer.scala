// #Sireum
// @formatter:off

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

// This file is auto-generated from CoreExp.scala, Typed.scala

package org.sireum.lang.ast

import org.sireum._

object MCoreExpTransformer {

  @record class PreResult[T](val continu: B,
                             val resultOpt: MOption[T])

  def transformISZ[T](s: IS[Z, T], f: T => MOption[T]): MOption[IS[Z, T]] = {
    val s2: MS[Z, T] = s.toMS
    var changed: B = F
    for (i <- s2.indices) {
      val e: T = s(i)
      val r: MOption[T] = f(e)
      changed = changed || r.nonEmpty
      s2(i) = r.getOrElse(e)
    }
    if (changed) {
      return MSome(s2.toIS)
    } else {
      return MNone()
    }
  }

  val PreResultTypedName: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedName: MOption[Typed] = MNone()

  val PreResultTypedTuple: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedTuple: MOption[Typed] = MNone()

  val PreResultTypedFun: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedFun: MOption[Typed] = MNone()

  val PreResultTypedTypeVar: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedTypeVar: MOption[Typed] = MNone()

  val PreResultTypedPackage: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedPackage: MOption[Typed] = MNone()

  val PreResultTypedObject: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedObject: MOption[Typed] = MNone()

  val PreResultTypedEnum: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedEnum: MOption[Typed] = MNone()

  val PreResultTypedMethod: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedMethod: MOption[Typed] = MNone()

  val PreResultTypedMethods: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedMethods: MOption[Typed] = MNone()

  val PreResultTypedFact: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedFact: MOption[Typed] = MNone()

  val PreResultTypedTheorem: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedTheorem: MOption[Typed] = MNone()

  val PreResultTypedInv: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedInv: MOption[Typed] = MNone()

  val PreResultCoreExpLitB: PreResult[CoreExp.Lit] = PreResult(T, MNone())

  val PostResultCoreExpLitB: MOption[CoreExp.Lit] = MNone()

  val PreResultCoreExpLitC: PreResult[CoreExp.Lit] = PreResult(T, MNone())

  val PostResultCoreExpLitC: MOption[CoreExp.Lit] = MNone()

  val PreResultCoreExpLitZ: PreResult[CoreExp.Lit] = PreResult(T, MNone())

  val PostResultCoreExpLitZ: MOption[CoreExp.Lit] = MNone()

  val PreResultCoreExpLitF32: PreResult[CoreExp.Lit] = PreResult(T, MNone())

  val PostResultCoreExpLitF32: MOption[CoreExp.Lit] = MNone()

  val PreResultCoreExpLitF64: PreResult[CoreExp.Lit] = PreResult(T, MNone())

  val PostResultCoreExpLitF64: MOption[CoreExp.Lit] = MNone()

  val PreResultCoreExpLitR: PreResult[CoreExp.Lit] = PreResult(T, MNone())

  val PostResultCoreExpLitR: MOption[CoreExp.Lit] = MNone()

  val PreResultCoreExpLitString: PreResult[CoreExp.Lit] = PreResult(T, MNone())

  val PostResultCoreExpLitString: MOption[CoreExp.Lit] = MNone()

  val PreResultCoreExpLitRange: PreResult[CoreExp.Lit] = PreResult(T, MNone())

  val PostResultCoreExpLitRange: MOption[CoreExp.Lit] = MNone()

  val PreResultCoreExpLitBits: PreResult[CoreExp.Lit] = PreResult(T, MNone())

  val PostResultCoreExpLitBits: MOption[CoreExp.Lit] = MNone()

  val PreResultCoreExpParamVarRef: PreResult[CoreExp.Base] = PreResult(T, MNone())

  val PostResultCoreExpParamVarRef: MOption[CoreExp.Base] = MNone()

  val PreResultCoreExpLocalVarRef: PreResult[CoreExp.Base] = PreResult(T, MNone())

  val PostResultCoreExpLocalVarRef: MOption[CoreExp.Base] = MNone()

  val PreResultCoreExpObjectVarRef: PreResult[CoreExp.Base] = PreResult(T, MNone())

  val PostResultCoreExpObjectVarRef: MOption[CoreExp.Base] = MNone()

  val PreResultCoreExpBinary: PreResult[CoreExp.Base] = PreResult(T, MNone())

  val PostResultCoreExpBinary: MOption[CoreExp.Base] = MNone()

  val PreResultCoreExpUnary: PreResult[CoreExp.Base] = PreResult(T, MNone())

  val PostResultCoreExpUnary: MOption[CoreExp.Base] = MNone()

  val PreResultCoreExpConstructor: PreResult[CoreExp.Base] = PreResult(T, MNone())

  val PostResultCoreExpConstructor: MOption[CoreExp.Base] = MNone()

  val PreResultCoreExpSelect: PreResult[CoreExp.Base] = PreResult(T, MNone())

  val PostResultCoreExpSelect: MOption[CoreExp.Base] = MNone()

  val PreResultCoreExpUpdate: PreResult[CoreExp.Base] = PreResult(T, MNone())

  val PostResultCoreExpUpdate: MOption[CoreExp.Base] = MNone()

  val PreResultCoreExpIndexing: PreResult[CoreExp.Base] = PreResult(T, MNone())

  val PostResultCoreExpIndexing: MOption[CoreExp.Base] = MNone()

  val PreResultCoreExpIndexingUpdate: PreResult[CoreExp.Base] = PreResult(T, MNone())

  val PostResultCoreExpIndexingUpdate: MOption[CoreExp.Base] = MNone()

  val PreResultCoreExpIf: PreResult[CoreExp.Base] = PreResult(T, MNone())

  val PostResultCoreExpIf: MOption[CoreExp.Base] = MNone()

  val PreResultCoreExpApply: PreResult[CoreExp.Base] = PreResult(T, MNone())

  val PostResultCoreExpApply: MOption[CoreExp.Base] = MNone()

  val PreResultCoreExpFun: PreResult[CoreExp.Base] = PreResult(T, MNone())

  val PostResultCoreExpFun: MOption[CoreExp.Base] = MNone()

  val PreResultCoreExpQuant: PreResult[CoreExp.Base] = PreResult(T, MNone())

  val PostResultCoreExpQuant: MOption[CoreExp.Base] = MNone()

  val PreResultCoreExpParam: PreResult[CoreExp.Param] = PreResult(T, MNone())

  val PostResultCoreExpParam: MOption[CoreExp.Param] = MNone()

  val PreResultCoreExpInstanceOfExp: PreResult[CoreExp.Base] = PreResult(T, MNone())

  val PostResultCoreExpInstanceOfExp: MOption[CoreExp.Base] = MNone()

  val PreResultCoreExpArrow: PreResult[CoreExp] = PreResult(T, MNone())

  val PostResultCoreExpArrow: MOption[CoreExp] = MNone()

}

import MCoreExpTransformer._

@msig trait MCoreExpTransformer {

  def preTyped(o: Typed): PreResult[Typed] = {
    o match {
      case o: Typed.Name => return preTypedName(o)
      case o: Typed.Tuple => return preTypedTuple(o)
      case o: Typed.Fun => return preTypedFun(o)
      case o: Typed.TypeVar => return preTypedTypeVar(o)
      case o: Typed.Package => return preTypedPackage(o)
      case o: Typed.Object => return preTypedObject(o)
      case o: Typed.Enum => return preTypedEnum(o)
      case o: Typed.Method => return preTypedMethod(o)
      case o: Typed.Methods => return preTypedMethods(o)
      case o: Typed.Fact => return preTypedFact(o)
      case o: Typed.Theorem => return preTypedTheorem(o)
      case o: Typed.Inv => return preTypedInv(o)
    }
  }

  def preTypedName(o: Typed.Name): PreResult[Typed] = {
    return PreResultTypedName
  }

  def preTypedTuple(o: Typed.Tuple): PreResult[Typed] = {
    return PreResultTypedTuple
  }

  def preTypedFun(o: Typed.Fun): PreResult[Typed] = {
    return PreResultTypedFun
  }

  def preTypedTypeVar(o: Typed.TypeVar): PreResult[Typed] = {
    return PreResultTypedTypeVar
  }

  def preTypedPackage(o: Typed.Package): PreResult[Typed] = {
    return PreResultTypedPackage
  }

  def preTypedObject(o: Typed.Object): PreResult[Typed] = {
    return PreResultTypedObject
  }

  def preTypedEnum(o: Typed.Enum): PreResult[Typed] = {
    return PreResultTypedEnum
  }

  def preTypedMethod(o: Typed.Method): PreResult[Typed] = {
    return PreResultTypedMethod
  }

  def preTypedMethods(o: Typed.Methods): PreResult[Typed] = {
    return PreResultTypedMethods
  }

  def preTypedFact(o: Typed.Fact): PreResult[Typed] = {
    return PreResultTypedFact
  }

  def preTypedTheorem(o: Typed.Theorem): PreResult[Typed] = {
    return PreResultTypedTheorem
  }

  def preTypedInv(o: Typed.Inv): PreResult[Typed] = {
    return PreResultTypedInv
  }

  def preCoreExp(o: CoreExp): PreResult[CoreExp] = {
    o match {
      case o: CoreExp.LitB =>
        val r: PreResult[CoreExp] = preCoreExpLitB(o) match {
         case PreResult(continu, MSome(r: CoreExp)) => PreResult(continu, MSome[CoreExp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp]())
        }
        return r
      case o: CoreExp.LitC =>
        val r: PreResult[CoreExp] = preCoreExpLitC(o) match {
         case PreResult(continu, MSome(r: CoreExp)) => PreResult(continu, MSome[CoreExp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp]())
        }
        return r
      case o: CoreExp.LitZ =>
        val r: PreResult[CoreExp] = preCoreExpLitZ(o) match {
         case PreResult(continu, MSome(r: CoreExp)) => PreResult(continu, MSome[CoreExp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp]())
        }
        return r
      case o: CoreExp.LitF32 =>
        val r: PreResult[CoreExp] = preCoreExpLitF32(o) match {
         case PreResult(continu, MSome(r: CoreExp)) => PreResult(continu, MSome[CoreExp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp]())
        }
        return r
      case o: CoreExp.LitF64 =>
        val r: PreResult[CoreExp] = preCoreExpLitF64(o) match {
         case PreResult(continu, MSome(r: CoreExp)) => PreResult(continu, MSome[CoreExp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp]())
        }
        return r
      case o: CoreExp.LitR =>
        val r: PreResult[CoreExp] = preCoreExpLitR(o) match {
         case PreResult(continu, MSome(r: CoreExp)) => PreResult(continu, MSome[CoreExp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp]())
        }
        return r
      case o: CoreExp.LitString =>
        val r: PreResult[CoreExp] = preCoreExpLitString(o) match {
         case PreResult(continu, MSome(r: CoreExp)) => PreResult(continu, MSome[CoreExp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp]())
        }
        return r
      case o: CoreExp.LitRange =>
        val r: PreResult[CoreExp] = preCoreExpLitRange(o) match {
         case PreResult(continu, MSome(r: CoreExp)) => PreResult(continu, MSome[CoreExp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp]())
        }
        return r
      case o: CoreExp.LitBits =>
        val r: PreResult[CoreExp] = preCoreExpLitBits(o) match {
         case PreResult(continu, MSome(r: CoreExp)) => PreResult(continu, MSome[CoreExp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp]())
        }
        return r
      case o: CoreExp.ParamVarRef =>
        val r: PreResult[CoreExp] = preCoreExpParamVarRef(o) match {
         case PreResult(continu, MSome(r: CoreExp)) => PreResult(continu, MSome[CoreExp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp]())
        }
        return r
      case o: CoreExp.LocalVarRef =>
        val r: PreResult[CoreExp] = preCoreExpLocalVarRef(o) match {
         case PreResult(continu, MSome(r: CoreExp)) => PreResult(continu, MSome[CoreExp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp]())
        }
        return r
      case o: CoreExp.ObjectVarRef =>
        val r: PreResult[CoreExp] = preCoreExpObjectVarRef(o) match {
         case PreResult(continu, MSome(r: CoreExp)) => PreResult(continu, MSome[CoreExp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp]())
        }
        return r
      case o: CoreExp.Binary =>
        val r: PreResult[CoreExp] = preCoreExpBinary(o) match {
         case PreResult(continu, MSome(r: CoreExp)) => PreResult(continu, MSome[CoreExp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp]())
        }
        return r
      case o: CoreExp.Unary =>
        val r: PreResult[CoreExp] = preCoreExpUnary(o) match {
         case PreResult(continu, MSome(r: CoreExp)) => PreResult(continu, MSome[CoreExp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp]())
        }
        return r
      case o: CoreExp.Constructor =>
        val r: PreResult[CoreExp] = preCoreExpConstructor(o) match {
         case PreResult(continu, MSome(r: CoreExp)) => PreResult(continu, MSome[CoreExp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp]())
        }
        return r
      case o: CoreExp.Select =>
        val r: PreResult[CoreExp] = preCoreExpSelect(o) match {
         case PreResult(continu, MSome(r: CoreExp)) => PreResult(continu, MSome[CoreExp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp]())
        }
        return r
      case o: CoreExp.Update =>
        val r: PreResult[CoreExp] = preCoreExpUpdate(o) match {
         case PreResult(continu, MSome(r: CoreExp)) => PreResult(continu, MSome[CoreExp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp]())
        }
        return r
      case o: CoreExp.Indexing =>
        val r: PreResult[CoreExp] = preCoreExpIndexing(o) match {
         case PreResult(continu, MSome(r: CoreExp)) => PreResult(continu, MSome[CoreExp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp]())
        }
        return r
      case o: CoreExp.IndexingUpdate =>
        val r: PreResult[CoreExp] = preCoreExpIndexingUpdate(o) match {
         case PreResult(continu, MSome(r: CoreExp)) => PreResult(continu, MSome[CoreExp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp]())
        }
        return r
      case o: CoreExp.If =>
        val r: PreResult[CoreExp] = preCoreExpIf(o) match {
         case PreResult(continu, MSome(r: CoreExp)) => PreResult(continu, MSome[CoreExp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp]())
        }
        return r
      case o: CoreExp.Apply =>
        val r: PreResult[CoreExp] = preCoreExpApply(o) match {
         case PreResult(continu, MSome(r: CoreExp)) => PreResult(continu, MSome[CoreExp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp]())
        }
        return r
      case o: CoreExp.Fun =>
        val r: PreResult[CoreExp] = preCoreExpFun(o) match {
         case PreResult(continu, MSome(r: CoreExp)) => PreResult(continu, MSome[CoreExp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp]())
        }
        return r
      case o: CoreExp.Quant =>
        val r: PreResult[CoreExp] = preCoreExpQuant(o) match {
         case PreResult(continu, MSome(r: CoreExp)) => PreResult(continu, MSome[CoreExp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp]())
        }
        return r
      case o: CoreExp.InstanceOfExp =>
        val r: PreResult[CoreExp] = preCoreExpInstanceOfExp(o) match {
         case PreResult(continu, MSome(r: CoreExp)) => PreResult(continu, MSome[CoreExp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp]())
        }
        return r
      case o: CoreExp.Arrow => return preCoreExpArrow(o)
    }
  }

  def preCoreExpBase(o: CoreExp.Base): PreResult[CoreExp.Base] = {
    o match {
      case o: CoreExp.LitB =>
        val r: PreResult[CoreExp.Base] = preCoreExpLitB(o) match {
         case PreResult(continu, MSome(r: CoreExp.Base)) => PreResult(continu, MSome[CoreExp.Base](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp.Base")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp.Base]())
        }
        return r
      case o: CoreExp.LitC =>
        val r: PreResult[CoreExp.Base] = preCoreExpLitC(o) match {
         case PreResult(continu, MSome(r: CoreExp.Base)) => PreResult(continu, MSome[CoreExp.Base](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp.Base")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp.Base]())
        }
        return r
      case o: CoreExp.LitZ =>
        val r: PreResult[CoreExp.Base] = preCoreExpLitZ(o) match {
         case PreResult(continu, MSome(r: CoreExp.Base)) => PreResult(continu, MSome[CoreExp.Base](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp.Base")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp.Base]())
        }
        return r
      case o: CoreExp.LitF32 =>
        val r: PreResult[CoreExp.Base] = preCoreExpLitF32(o) match {
         case PreResult(continu, MSome(r: CoreExp.Base)) => PreResult(continu, MSome[CoreExp.Base](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp.Base")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp.Base]())
        }
        return r
      case o: CoreExp.LitF64 =>
        val r: PreResult[CoreExp.Base] = preCoreExpLitF64(o) match {
         case PreResult(continu, MSome(r: CoreExp.Base)) => PreResult(continu, MSome[CoreExp.Base](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp.Base")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp.Base]())
        }
        return r
      case o: CoreExp.LitR =>
        val r: PreResult[CoreExp.Base] = preCoreExpLitR(o) match {
         case PreResult(continu, MSome(r: CoreExp.Base)) => PreResult(continu, MSome[CoreExp.Base](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp.Base")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp.Base]())
        }
        return r
      case o: CoreExp.LitString =>
        val r: PreResult[CoreExp.Base] = preCoreExpLitString(o) match {
         case PreResult(continu, MSome(r: CoreExp.Base)) => PreResult(continu, MSome[CoreExp.Base](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp.Base")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp.Base]())
        }
        return r
      case o: CoreExp.LitRange =>
        val r: PreResult[CoreExp.Base] = preCoreExpLitRange(o) match {
         case PreResult(continu, MSome(r: CoreExp.Base)) => PreResult(continu, MSome[CoreExp.Base](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp.Base")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp.Base]())
        }
        return r
      case o: CoreExp.LitBits =>
        val r: PreResult[CoreExp.Base] = preCoreExpLitBits(o) match {
         case PreResult(continu, MSome(r: CoreExp.Base)) => PreResult(continu, MSome[CoreExp.Base](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type CoreExp.Base")
         case PreResult(continu, _) => PreResult(continu, MNone[CoreExp.Base]())
        }
        return r
      case o: CoreExp.ParamVarRef => return preCoreExpParamVarRef(o)
      case o: CoreExp.LocalVarRef => return preCoreExpLocalVarRef(o)
      case o: CoreExp.ObjectVarRef => return preCoreExpObjectVarRef(o)
      case o: CoreExp.Binary => return preCoreExpBinary(o)
      case o: CoreExp.Unary => return preCoreExpUnary(o)
      case o: CoreExp.Constructor => return preCoreExpConstructor(o)
      case o: CoreExp.Select => return preCoreExpSelect(o)
      case o: CoreExp.Update => return preCoreExpUpdate(o)
      case o: CoreExp.Indexing => return preCoreExpIndexing(o)
      case o: CoreExp.IndexingUpdate => return preCoreExpIndexingUpdate(o)
      case o: CoreExp.If => return preCoreExpIf(o)
      case o: CoreExp.Apply => return preCoreExpApply(o)
      case o: CoreExp.Fun => return preCoreExpFun(o)
      case o: CoreExp.Quant => return preCoreExpQuant(o)
      case o: CoreExp.InstanceOfExp => return preCoreExpInstanceOfExp(o)
    }
  }

  def preCoreExpLit(o: CoreExp.Lit): PreResult[CoreExp.Lit] = {
    o match {
      case o: CoreExp.LitB => return preCoreExpLitB(o)
      case o: CoreExp.LitC => return preCoreExpLitC(o)
      case o: CoreExp.LitZ => return preCoreExpLitZ(o)
      case o: CoreExp.LitF32 => return preCoreExpLitF32(o)
      case o: CoreExp.LitF64 => return preCoreExpLitF64(o)
      case o: CoreExp.LitR => return preCoreExpLitR(o)
      case o: CoreExp.LitString => return preCoreExpLitString(o)
      case o: CoreExp.LitRange => return preCoreExpLitRange(o)
      case o: CoreExp.LitBits => return preCoreExpLitBits(o)
    }
  }

  def preCoreExpLitB(o: CoreExp.LitB): PreResult[CoreExp.Lit] = {
    return PreResultCoreExpLitB
  }

  def preCoreExpLitC(o: CoreExp.LitC): PreResult[CoreExp.Lit] = {
    return PreResultCoreExpLitC
  }

  def preCoreExpLitZ(o: CoreExp.LitZ): PreResult[CoreExp.Lit] = {
    return PreResultCoreExpLitZ
  }

  def preCoreExpLitF32(o: CoreExp.LitF32): PreResult[CoreExp.Lit] = {
    return PreResultCoreExpLitF32
  }

  def preCoreExpLitF64(o: CoreExp.LitF64): PreResult[CoreExp.Lit] = {
    return PreResultCoreExpLitF64
  }

  def preCoreExpLitR(o: CoreExp.LitR): PreResult[CoreExp.Lit] = {
    return PreResultCoreExpLitR
  }

  def preCoreExpLitString(o: CoreExp.LitString): PreResult[CoreExp.Lit] = {
    return PreResultCoreExpLitString
  }

  def preCoreExpLitRange(o: CoreExp.LitRange): PreResult[CoreExp.Lit] = {
    return PreResultCoreExpLitRange
  }

  def preCoreExpLitBits(o: CoreExp.LitBits): PreResult[CoreExp.Lit] = {
    return PreResultCoreExpLitBits
  }

  def preCoreExpParamVarRef(o: CoreExp.ParamVarRef): PreResult[CoreExp.Base] = {
    return PreResultCoreExpParamVarRef
  }

  def preCoreExpLocalVarRef(o: CoreExp.LocalVarRef): PreResult[CoreExp.Base] = {
    return PreResultCoreExpLocalVarRef
  }

  def preCoreExpObjectVarRef(o: CoreExp.ObjectVarRef): PreResult[CoreExp.Base] = {
    return PreResultCoreExpObjectVarRef
  }

  def preCoreExpBinary(o: CoreExp.Binary): PreResult[CoreExp.Base] = {
    return PreResultCoreExpBinary
  }

  def preCoreExpUnary(o: CoreExp.Unary): PreResult[CoreExp.Base] = {
    return PreResultCoreExpUnary
  }

  def preCoreExpConstructor(o: CoreExp.Constructor): PreResult[CoreExp.Base] = {
    return PreResultCoreExpConstructor
  }

  def preCoreExpSelect(o: CoreExp.Select): PreResult[CoreExp.Base] = {
    return PreResultCoreExpSelect
  }

  def preCoreExpUpdate(o: CoreExp.Update): PreResult[CoreExp.Base] = {
    return PreResultCoreExpUpdate
  }

  def preCoreExpIndexing(o: CoreExp.Indexing): PreResult[CoreExp.Base] = {
    return PreResultCoreExpIndexing
  }

  def preCoreExpIndexingUpdate(o: CoreExp.IndexingUpdate): PreResult[CoreExp.Base] = {
    return PreResultCoreExpIndexingUpdate
  }

  def preCoreExpIf(o: CoreExp.If): PreResult[CoreExp.Base] = {
    return PreResultCoreExpIf
  }

  def preCoreExpApply(o: CoreExp.Apply): PreResult[CoreExp.Base] = {
    return PreResultCoreExpApply
  }

  def preCoreExpFun(o: CoreExp.Fun): PreResult[CoreExp.Base] = {
    return PreResultCoreExpFun
  }

  def preCoreExpQuant(o: CoreExp.Quant): PreResult[CoreExp.Base] = {
    return PreResultCoreExpQuant
  }

  def preCoreExpParam(o: CoreExp.Param): PreResult[CoreExp.Param] = {
    return PreResultCoreExpParam
  }

  def preCoreExpInstanceOfExp(o: CoreExp.InstanceOfExp): PreResult[CoreExp.Base] = {
    return PreResultCoreExpInstanceOfExp
  }

  def preCoreExpArrow(o: CoreExp.Arrow): PreResult[CoreExp] = {
    return PreResultCoreExpArrow
  }

  def postTyped(o: Typed): MOption[Typed] = {
    o match {
      case o: Typed.Name => return postTypedName(o)
      case o: Typed.Tuple => return postTypedTuple(o)
      case o: Typed.Fun => return postTypedFun(o)
      case o: Typed.TypeVar => return postTypedTypeVar(o)
      case o: Typed.Package => return postTypedPackage(o)
      case o: Typed.Object => return postTypedObject(o)
      case o: Typed.Enum => return postTypedEnum(o)
      case o: Typed.Method => return postTypedMethod(o)
      case o: Typed.Methods => return postTypedMethods(o)
      case o: Typed.Fact => return postTypedFact(o)
      case o: Typed.Theorem => return postTypedTheorem(o)
      case o: Typed.Inv => return postTypedInv(o)
    }
  }

  def postTypedName(o: Typed.Name): MOption[Typed] = {
    return PostResultTypedName
  }

  def postTypedTuple(o: Typed.Tuple): MOption[Typed] = {
    return PostResultTypedTuple
  }

  def postTypedFun(o: Typed.Fun): MOption[Typed] = {
    return PostResultTypedFun
  }

  def postTypedTypeVar(o: Typed.TypeVar): MOption[Typed] = {
    return PostResultTypedTypeVar
  }

  def postTypedPackage(o: Typed.Package): MOption[Typed] = {
    return PostResultTypedPackage
  }

  def postTypedObject(o: Typed.Object): MOption[Typed] = {
    return PostResultTypedObject
  }

  def postTypedEnum(o: Typed.Enum): MOption[Typed] = {
    return PostResultTypedEnum
  }

  def postTypedMethod(o: Typed.Method): MOption[Typed] = {
    return PostResultTypedMethod
  }

  def postTypedMethods(o: Typed.Methods): MOption[Typed] = {
    return PostResultTypedMethods
  }

  def postTypedFact(o: Typed.Fact): MOption[Typed] = {
    return PostResultTypedFact
  }

  def postTypedTheorem(o: Typed.Theorem): MOption[Typed] = {
    return PostResultTypedTheorem
  }

  def postTypedInv(o: Typed.Inv): MOption[Typed] = {
    return PostResultTypedInv
  }

  def postCoreExp(o: CoreExp): MOption[CoreExp] = {
    o match {
      case o: CoreExp.LitB =>
        val r: MOption[CoreExp] = postCoreExpLitB(o) match {
         case MSome(result: CoreExp) => MSome[CoreExp](result)
         case MSome(_) => halt("Can only produce object of type CoreExp")
         case _ => MNone[CoreExp]()
        }
        return r
      case o: CoreExp.LitC =>
        val r: MOption[CoreExp] = postCoreExpLitC(o) match {
         case MSome(result: CoreExp) => MSome[CoreExp](result)
         case MSome(_) => halt("Can only produce object of type CoreExp")
         case _ => MNone[CoreExp]()
        }
        return r
      case o: CoreExp.LitZ =>
        val r: MOption[CoreExp] = postCoreExpLitZ(o) match {
         case MSome(result: CoreExp) => MSome[CoreExp](result)
         case MSome(_) => halt("Can only produce object of type CoreExp")
         case _ => MNone[CoreExp]()
        }
        return r
      case o: CoreExp.LitF32 =>
        val r: MOption[CoreExp] = postCoreExpLitF32(o) match {
         case MSome(result: CoreExp) => MSome[CoreExp](result)
         case MSome(_) => halt("Can only produce object of type CoreExp")
         case _ => MNone[CoreExp]()
        }
        return r
      case o: CoreExp.LitF64 =>
        val r: MOption[CoreExp] = postCoreExpLitF64(o) match {
         case MSome(result: CoreExp) => MSome[CoreExp](result)
         case MSome(_) => halt("Can only produce object of type CoreExp")
         case _ => MNone[CoreExp]()
        }
        return r
      case o: CoreExp.LitR =>
        val r: MOption[CoreExp] = postCoreExpLitR(o) match {
         case MSome(result: CoreExp) => MSome[CoreExp](result)
         case MSome(_) => halt("Can only produce object of type CoreExp")
         case _ => MNone[CoreExp]()
        }
        return r
      case o: CoreExp.LitString =>
        val r: MOption[CoreExp] = postCoreExpLitString(o) match {
         case MSome(result: CoreExp) => MSome[CoreExp](result)
         case MSome(_) => halt("Can only produce object of type CoreExp")
         case _ => MNone[CoreExp]()
        }
        return r
      case o: CoreExp.LitRange =>
        val r: MOption[CoreExp] = postCoreExpLitRange(o) match {
         case MSome(result: CoreExp) => MSome[CoreExp](result)
         case MSome(_) => halt("Can only produce object of type CoreExp")
         case _ => MNone[CoreExp]()
        }
        return r
      case o: CoreExp.LitBits =>
        val r: MOption[CoreExp] = postCoreExpLitBits(o) match {
         case MSome(result: CoreExp) => MSome[CoreExp](result)
         case MSome(_) => halt("Can only produce object of type CoreExp")
         case _ => MNone[CoreExp]()
        }
        return r
      case o: CoreExp.ParamVarRef =>
        val r: MOption[CoreExp] = postCoreExpParamVarRef(o) match {
         case MSome(result: CoreExp) => MSome[CoreExp](result)
         case MSome(_) => halt("Can only produce object of type CoreExp")
         case _ => MNone[CoreExp]()
        }
        return r
      case o: CoreExp.LocalVarRef =>
        val r: MOption[CoreExp] = postCoreExpLocalVarRef(o) match {
         case MSome(result: CoreExp) => MSome[CoreExp](result)
         case MSome(_) => halt("Can only produce object of type CoreExp")
         case _ => MNone[CoreExp]()
        }
        return r
      case o: CoreExp.ObjectVarRef =>
        val r: MOption[CoreExp] = postCoreExpObjectVarRef(o) match {
         case MSome(result: CoreExp) => MSome[CoreExp](result)
         case MSome(_) => halt("Can only produce object of type CoreExp")
         case _ => MNone[CoreExp]()
        }
        return r
      case o: CoreExp.Binary =>
        val r: MOption[CoreExp] = postCoreExpBinary(o) match {
         case MSome(result: CoreExp) => MSome[CoreExp](result)
         case MSome(_) => halt("Can only produce object of type CoreExp")
         case _ => MNone[CoreExp]()
        }
        return r
      case o: CoreExp.Unary =>
        val r: MOption[CoreExp] = postCoreExpUnary(o) match {
         case MSome(result: CoreExp) => MSome[CoreExp](result)
         case MSome(_) => halt("Can only produce object of type CoreExp")
         case _ => MNone[CoreExp]()
        }
        return r
      case o: CoreExp.Constructor =>
        val r: MOption[CoreExp] = postCoreExpConstructor(o) match {
         case MSome(result: CoreExp) => MSome[CoreExp](result)
         case MSome(_) => halt("Can only produce object of type CoreExp")
         case _ => MNone[CoreExp]()
        }
        return r
      case o: CoreExp.Select =>
        val r: MOption[CoreExp] = postCoreExpSelect(o) match {
         case MSome(result: CoreExp) => MSome[CoreExp](result)
         case MSome(_) => halt("Can only produce object of type CoreExp")
         case _ => MNone[CoreExp]()
        }
        return r
      case o: CoreExp.Update =>
        val r: MOption[CoreExp] = postCoreExpUpdate(o) match {
         case MSome(result: CoreExp) => MSome[CoreExp](result)
         case MSome(_) => halt("Can only produce object of type CoreExp")
         case _ => MNone[CoreExp]()
        }
        return r
      case o: CoreExp.Indexing =>
        val r: MOption[CoreExp] = postCoreExpIndexing(o) match {
         case MSome(result: CoreExp) => MSome[CoreExp](result)
         case MSome(_) => halt("Can only produce object of type CoreExp")
         case _ => MNone[CoreExp]()
        }
        return r
      case o: CoreExp.IndexingUpdate =>
        val r: MOption[CoreExp] = postCoreExpIndexingUpdate(o) match {
         case MSome(result: CoreExp) => MSome[CoreExp](result)
         case MSome(_) => halt("Can only produce object of type CoreExp")
         case _ => MNone[CoreExp]()
        }
        return r
      case o: CoreExp.If =>
        val r: MOption[CoreExp] = postCoreExpIf(o) match {
         case MSome(result: CoreExp) => MSome[CoreExp](result)
         case MSome(_) => halt("Can only produce object of type CoreExp")
         case _ => MNone[CoreExp]()
        }
        return r
      case o: CoreExp.Apply =>
        val r: MOption[CoreExp] = postCoreExpApply(o) match {
         case MSome(result: CoreExp) => MSome[CoreExp](result)
         case MSome(_) => halt("Can only produce object of type CoreExp")
         case _ => MNone[CoreExp]()
        }
        return r
      case o: CoreExp.Fun =>
        val r: MOption[CoreExp] = postCoreExpFun(o) match {
         case MSome(result: CoreExp) => MSome[CoreExp](result)
         case MSome(_) => halt("Can only produce object of type CoreExp")
         case _ => MNone[CoreExp]()
        }
        return r
      case o: CoreExp.Quant =>
        val r: MOption[CoreExp] = postCoreExpQuant(o) match {
         case MSome(result: CoreExp) => MSome[CoreExp](result)
         case MSome(_) => halt("Can only produce object of type CoreExp")
         case _ => MNone[CoreExp]()
        }
        return r
      case o: CoreExp.InstanceOfExp =>
        val r: MOption[CoreExp] = postCoreExpInstanceOfExp(o) match {
         case MSome(result: CoreExp) => MSome[CoreExp](result)
         case MSome(_) => halt("Can only produce object of type CoreExp")
         case _ => MNone[CoreExp]()
        }
        return r
      case o: CoreExp.Arrow => return postCoreExpArrow(o)
    }
  }

  def postCoreExpBase(o: CoreExp.Base): MOption[CoreExp.Base] = {
    o match {
      case o: CoreExp.LitB =>
        val r: MOption[CoreExp.Base] = postCoreExpLitB(o) match {
         case MSome(result: CoreExp.Base) => MSome[CoreExp.Base](result)
         case MSome(_) => halt("Can only produce object of type CoreExp.Base")
         case _ => MNone[CoreExp.Base]()
        }
        return r
      case o: CoreExp.LitC =>
        val r: MOption[CoreExp.Base] = postCoreExpLitC(o) match {
         case MSome(result: CoreExp.Base) => MSome[CoreExp.Base](result)
         case MSome(_) => halt("Can only produce object of type CoreExp.Base")
         case _ => MNone[CoreExp.Base]()
        }
        return r
      case o: CoreExp.LitZ =>
        val r: MOption[CoreExp.Base] = postCoreExpLitZ(o) match {
         case MSome(result: CoreExp.Base) => MSome[CoreExp.Base](result)
         case MSome(_) => halt("Can only produce object of type CoreExp.Base")
         case _ => MNone[CoreExp.Base]()
        }
        return r
      case o: CoreExp.LitF32 =>
        val r: MOption[CoreExp.Base] = postCoreExpLitF32(o) match {
         case MSome(result: CoreExp.Base) => MSome[CoreExp.Base](result)
         case MSome(_) => halt("Can only produce object of type CoreExp.Base")
         case _ => MNone[CoreExp.Base]()
        }
        return r
      case o: CoreExp.LitF64 =>
        val r: MOption[CoreExp.Base] = postCoreExpLitF64(o) match {
         case MSome(result: CoreExp.Base) => MSome[CoreExp.Base](result)
         case MSome(_) => halt("Can only produce object of type CoreExp.Base")
         case _ => MNone[CoreExp.Base]()
        }
        return r
      case o: CoreExp.LitR =>
        val r: MOption[CoreExp.Base] = postCoreExpLitR(o) match {
         case MSome(result: CoreExp.Base) => MSome[CoreExp.Base](result)
         case MSome(_) => halt("Can only produce object of type CoreExp.Base")
         case _ => MNone[CoreExp.Base]()
        }
        return r
      case o: CoreExp.LitString =>
        val r: MOption[CoreExp.Base] = postCoreExpLitString(o) match {
         case MSome(result: CoreExp.Base) => MSome[CoreExp.Base](result)
         case MSome(_) => halt("Can only produce object of type CoreExp.Base")
         case _ => MNone[CoreExp.Base]()
        }
        return r
      case o: CoreExp.LitRange =>
        val r: MOption[CoreExp.Base] = postCoreExpLitRange(o) match {
         case MSome(result: CoreExp.Base) => MSome[CoreExp.Base](result)
         case MSome(_) => halt("Can only produce object of type CoreExp.Base")
         case _ => MNone[CoreExp.Base]()
        }
        return r
      case o: CoreExp.LitBits =>
        val r: MOption[CoreExp.Base] = postCoreExpLitBits(o) match {
         case MSome(result: CoreExp.Base) => MSome[CoreExp.Base](result)
         case MSome(_) => halt("Can only produce object of type CoreExp.Base")
         case _ => MNone[CoreExp.Base]()
        }
        return r
      case o: CoreExp.ParamVarRef => return postCoreExpParamVarRef(o)
      case o: CoreExp.LocalVarRef => return postCoreExpLocalVarRef(o)
      case o: CoreExp.ObjectVarRef => return postCoreExpObjectVarRef(o)
      case o: CoreExp.Binary => return postCoreExpBinary(o)
      case o: CoreExp.Unary => return postCoreExpUnary(o)
      case o: CoreExp.Constructor => return postCoreExpConstructor(o)
      case o: CoreExp.Select => return postCoreExpSelect(o)
      case o: CoreExp.Update => return postCoreExpUpdate(o)
      case o: CoreExp.Indexing => return postCoreExpIndexing(o)
      case o: CoreExp.IndexingUpdate => return postCoreExpIndexingUpdate(o)
      case o: CoreExp.If => return postCoreExpIf(o)
      case o: CoreExp.Apply => return postCoreExpApply(o)
      case o: CoreExp.Fun => return postCoreExpFun(o)
      case o: CoreExp.Quant => return postCoreExpQuant(o)
      case o: CoreExp.InstanceOfExp => return postCoreExpInstanceOfExp(o)
    }
  }

  def postCoreExpLit(o: CoreExp.Lit): MOption[CoreExp.Lit] = {
    o match {
      case o: CoreExp.LitB => return postCoreExpLitB(o)
      case o: CoreExp.LitC => return postCoreExpLitC(o)
      case o: CoreExp.LitZ => return postCoreExpLitZ(o)
      case o: CoreExp.LitF32 => return postCoreExpLitF32(o)
      case o: CoreExp.LitF64 => return postCoreExpLitF64(o)
      case o: CoreExp.LitR => return postCoreExpLitR(o)
      case o: CoreExp.LitString => return postCoreExpLitString(o)
      case o: CoreExp.LitRange => return postCoreExpLitRange(o)
      case o: CoreExp.LitBits => return postCoreExpLitBits(o)
    }
  }

  def postCoreExpLitB(o: CoreExp.LitB): MOption[CoreExp.Lit] = {
    return PostResultCoreExpLitB
  }

  def postCoreExpLitC(o: CoreExp.LitC): MOption[CoreExp.Lit] = {
    return PostResultCoreExpLitC
  }

  def postCoreExpLitZ(o: CoreExp.LitZ): MOption[CoreExp.Lit] = {
    return PostResultCoreExpLitZ
  }

  def postCoreExpLitF32(o: CoreExp.LitF32): MOption[CoreExp.Lit] = {
    return PostResultCoreExpLitF32
  }

  def postCoreExpLitF64(o: CoreExp.LitF64): MOption[CoreExp.Lit] = {
    return PostResultCoreExpLitF64
  }

  def postCoreExpLitR(o: CoreExp.LitR): MOption[CoreExp.Lit] = {
    return PostResultCoreExpLitR
  }

  def postCoreExpLitString(o: CoreExp.LitString): MOption[CoreExp.Lit] = {
    return PostResultCoreExpLitString
  }

  def postCoreExpLitRange(o: CoreExp.LitRange): MOption[CoreExp.Lit] = {
    return PostResultCoreExpLitRange
  }

  def postCoreExpLitBits(o: CoreExp.LitBits): MOption[CoreExp.Lit] = {
    return PostResultCoreExpLitBits
  }

  def postCoreExpParamVarRef(o: CoreExp.ParamVarRef): MOption[CoreExp.Base] = {
    return PostResultCoreExpParamVarRef
  }

  def postCoreExpLocalVarRef(o: CoreExp.LocalVarRef): MOption[CoreExp.Base] = {
    return PostResultCoreExpLocalVarRef
  }

  def postCoreExpObjectVarRef(o: CoreExp.ObjectVarRef): MOption[CoreExp.Base] = {
    return PostResultCoreExpObjectVarRef
  }

  def postCoreExpBinary(o: CoreExp.Binary): MOption[CoreExp.Base] = {
    return PostResultCoreExpBinary
  }

  def postCoreExpUnary(o: CoreExp.Unary): MOption[CoreExp.Base] = {
    return PostResultCoreExpUnary
  }

  def postCoreExpConstructor(o: CoreExp.Constructor): MOption[CoreExp.Base] = {
    return PostResultCoreExpConstructor
  }

  def postCoreExpSelect(o: CoreExp.Select): MOption[CoreExp.Base] = {
    return PostResultCoreExpSelect
  }

  def postCoreExpUpdate(o: CoreExp.Update): MOption[CoreExp.Base] = {
    return PostResultCoreExpUpdate
  }

  def postCoreExpIndexing(o: CoreExp.Indexing): MOption[CoreExp.Base] = {
    return PostResultCoreExpIndexing
  }

  def postCoreExpIndexingUpdate(o: CoreExp.IndexingUpdate): MOption[CoreExp.Base] = {
    return PostResultCoreExpIndexingUpdate
  }

  def postCoreExpIf(o: CoreExp.If): MOption[CoreExp.Base] = {
    return PostResultCoreExpIf
  }

  def postCoreExpApply(o: CoreExp.Apply): MOption[CoreExp.Base] = {
    return PostResultCoreExpApply
  }

  def postCoreExpFun(o: CoreExp.Fun): MOption[CoreExp.Base] = {
    return PostResultCoreExpFun
  }

  def postCoreExpQuant(o: CoreExp.Quant): MOption[CoreExp.Base] = {
    return PostResultCoreExpQuant
  }

  def postCoreExpParam(o: CoreExp.Param): MOption[CoreExp.Param] = {
    return PostResultCoreExpParam
  }

  def postCoreExpInstanceOfExp(o: CoreExp.InstanceOfExp): MOption[CoreExp.Base] = {
    return PostResultCoreExpInstanceOfExp
  }

  def postCoreExpArrow(o: CoreExp.Arrow): MOption[CoreExp] = {
    return PostResultCoreExpArrow
  }

  def transformTyped(o: Typed): MOption[Typed] = {
    val preR: PreResult[Typed] = preTyped(o)
    val r: MOption[Typed] = if (preR.continu) {
      val o2: Typed = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[Typed] = o2 match {
        case o2: Typed.Name =>
          val r0: MOption[IS[Z, Typed]] = transformISZ(o2.args, transformTyped _)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(args = r0.getOrElse(o2.args)))
          else
            MNone()
        case o2: Typed.Tuple =>
          val r0: MOption[IS[Z, Typed]] = transformISZ(o2.args, transformTyped _)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(args = r0.getOrElse(o2.args)))
          else
            MNone()
        case o2: Typed.Fun =>
          val r0: MOption[IS[Z, Typed]] = transformISZ(o2.args, transformTyped _)
          val r1: MOption[Typed] = transformTyped(o2.ret)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(args = r0.getOrElse(o2.args), ret = r1.getOrElse(o2.ret)))
          else
            MNone()
        case o2: Typed.TypeVar =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: Typed.Package =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: Typed.Object =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: Typed.Enum =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: Typed.Method =>
          val r0: MOption[Typed.Fun] = transformTypedFun(o2.tpe)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(tpe = r0.getOrElse(o2.tpe)))
          else
            MNone()
        case o2: Typed.Methods =>
          val r0: MOption[IS[Z, Typed.Method]] = transformISZ(o2.methods, transformTypedMethod _)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(methods = r0.getOrElse(o2.methods)))
          else
            MNone()
        case o2: Typed.Fact =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: Typed.Theorem =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: Typed.Inv =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Typed = r.getOrElse(o)
    val postR: MOption[Typed] = postTyped(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformCoreExp(o: CoreExp): MOption[CoreExp] = {
    val preR: PreResult[CoreExp] = preCoreExp(o)
    val r: MOption[CoreExp] = if (preR.continu) {
      val o2: CoreExp = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[CoreExp] = o2 match {
        case o2: CoreExp.LitB =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: CoreExp.LitC =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: CoreExp.LitZ =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: CoreExp.LitF32 =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: CoreExp.LitF64 =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: CoreExp.LitR =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: CoreExp.LitString =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: CoreExp.LitRange =>
          val r0: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(tipe = r0.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: CoreExp.LitBits =>
          val r0: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(tipe = r0.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: CoreExp.ParamVarRef =>
          val r0: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(tipe = r0.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: CoreExp.LocalVarRef =>
          val r0: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(tipe = r0.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: CoreExp.ObjectVarRef =>
          val r0: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(tipe = r0.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: CoreExp.Binary =>
          val r0: MOption[CoreExp.Base] = transformCoreExpBase(o2.left)
          val r1: MOption[CoreExp.Base] = transformCoreExpBase(o2.right)
          val r2: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(left = r0.getOrElse(o2.left), right = r1.getOrElse(o2.right), tipe = r2.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: CoreExp.Unary =>
          val r0: MOption[CoreExp.Base] = transformCoreExpBase(o2.exp)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(exp = r0.getOrElse(o2.exp)))
          else
            MNone()
        case o2: CoreExp.Constructor =>
          val r0: MOption[Typed] = transformTyped(o2.tipe)
          val r1: MOption[IS[Z, CoreExp.Base]] = transformISZ(o2.args, transformCoreExpBase _)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(tipe = r0.getOrElse(o2.tipe), args = r1.getOrElse(o2.args)))
          else
            MNone()
        case o2: CoreExp.Select =>
          val r0: MOption[CoreExp.Base] = transformCoreExpBase(o2.exp)
          val r1: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(exp = r0.getOrElse(o2.exp), tipe = r1.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: CoreExp.Update =>
          val r0: MOption[CoreExp.Base] = transformCoreExpBase(o2.exp)
          val r1: MOption[CoreExp.Base] = transformCoreExpBase(o2.arg)
          val r2: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(exp = r0.getOrElse(o2.exp), arg = r1.getOrElse(o2.arg), tipe = r2.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: CoreExp.Indexing =>
          val r0: MOption[CoreExp.Base] = transformCoreExpBase(o2.exp)
          val r1: MOption[CoreExp.Base] = transformCoreExpBase(o2.index)
          val r2: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(exp = r0.getOrElse(o2.exp), index = r1.getOrElse(o2.index), tipe = r2.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: CoreExp.IndexingUpdate =>
          val r0: MOption[CoreExp.Base] = transformCoreExpBase(o2.exp)
          val r1: MOption[CoreExp.Base] = transformCoreExpBase(o2.index)
          val r2: MOption[CoreExp.Base] = transformCoreExpBase(o2.arg)
          val r3: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty)
            MSome(o2(exp = r0.getOrElse(o2.exp), index = r1.getOrElse(o2.index), arg = r2.getOrElse(o2.arg), tipe = r3.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: CoreExp.If =>
          val r0: MOption[CoreExp.Base] = transformCoreExpBase(o2.cond)
          val r1: MOption[CoreExp.Base] = transformCoreExpBase(o2.tExp)
          val r2: MOption[CoreExp.Base] = transformCoreExpBase(o2.fExp)
          val r3: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty)
            MSome(o2(cond = r0.getOrElse(o2.cond), tExp = r1.getOrElse(o2.tExp), fExp = r2.getOrElse(o2.fExp), tipe = r3.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: CoreExp.Apply =>
          val r0: MOption[CoreExp.Base] = transformCoreExpBase(o2.exp)
          val r1: MOption[IS[Z, CoreExp.Base]] = transformISZ(o2.args, transformCoreExpBase _)
          val r2: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(exp = r0.getOrElse(o2.exp), args = r1.getOrElse(o2.args), tipe = r2.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: CoreExp.Fun =>
          val r0: MOption[CoreExp.Param] = transformCoreExpParam(o2.param)
          val r1: MOption[CoreExp.Base] = transformCoreExpBase(o2.exp)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(param = r0.getOrElse(o2.param), exp = r1.getOrElse(o2.exp)))
          else
            MNone()
        case o2: CoreExp.Quant =>
          val r0: MOption[CoreExp.Param] = transformCoreExpParam(o2.param)
          val r1: MOption[CoreExp.Base] = transformCoreExpBase(o2.exp)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(param = r0.getOrElse(o2.param), exp = r1.getOrElse(o2.exp)))
          else
            MNone()
        case o2: CoreExp.InstanceOfExp =>
          val r0: MOption[CoreExp.Base] = transformCoreExpBase(o2.exp)
          val r1: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(exp = r0.getOrElse(o2.exp), tipe = r1.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: CoreExp.Arrow =>
          val r0: MOption[CoreExp.Base] = transformCoreExpBase(o2.left)
          val r1: MOption[CoreExp] = transformCoreExp(o2.right)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(left = r0.getOrElse(o2.left), right = r1.getOrElse(o2.right)))
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: CoreExp = r.getOrElse(o)
    val postR: MOption[CoreExp] = postCoreExp(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformCoreExpBase(o: CoreExp.Base): MOption[CoreExp.Base] = {
    val preR: PreResult[CoreExp.Base] = preCoreExpBase(o)
    val r: MOption[CoreExp.Base] = if (preR.continu) {
      val o2: CoreExp.Base = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[CoreExp.Base] = o2 match {
        case o2: CoreExp.LitB =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: CoreExp.LitC =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: CoreExp.LitZ =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: CoreExp.LitF32 =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: CoreExp.LitF64 =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: CoreExp.LitR =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: CoreExp.LitString =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: CoreExp.LitRange =>
          val r0: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(tipe = r0.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: CoreExp.LitBits =>
          val r0: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(tipe = r0.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: CoreExp.ParamVarRef =>
          val r0: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(tipe = r0.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: CoreExp.LocalVarRef =>
          val r0: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(tipe = r0.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: CoreExp.ObjectVarRef =>
          val r0: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(tipe = r0.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: CoreExp.Binary =>
          val r0: MOption[CoreExp.Base] = transformCoreExpBase(o2.left)
          val r1: MOption[CoreExp.Base] = transformCoreExpBase(o2.right)
          val r2: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(left = r0.getOrElse(o2.left), right = r1.getOrElse(o2.right), tipe = r2.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: CoreExp.Unary =>
          val r0: MOption[CoreExp.Base] = transformCoreExpBase(o2.exp)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(exp = r0.getOrElse(o2.exp)))
          else
            MNone()
        case o2: CoreExp.Constructor =>
          val r0: MOption[Typed] = transformTyped(o2.tipe)
          val r1: MOption[IS[Z, CoreExp.Base]] = transformISZ(o2.args, transformCoreExpBase _)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(tipe = r0.getOrElse(o2.tipe), args = r1.getOrElse(o2.args)))
          else
            MNone()
        case o2: CoreExp.Select =>
          val r0: MOption[CoreExp.Base] = transformCoreExpBase(o2.exp)
          val r1: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(exp = r0.getOrElse(o2.exp), tipe = r1.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: CoreExp.Update =>
          val r0: MOption[CoreExp.Base] = transformCoreExpBase(o2.exp)
          val r1: MOption[CoreExp.Base] = transformCoreExpBase(o2.arg)
          val r2: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(exp = r0.getOrElse(o2.exp), arg = r1.getOrElse(o2.arg), tipe = r2.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: CoreExp.Indexing =>
          val r0: MOption[CoreExp.Base] = transformCoreExpBase(o2.exp)
          val r1: MOption[CoreExp.Base] = transformCoreExpBase(o2.index)
          val r2: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(exp = r0.getOrElse(o2.exp), index = r1.getOrElse(o2.index), tipe = r2.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: CoreExp.IndexingUpdate =>
          val r0: MOption[CoreExp.Base] = transformCoreExpBase(o2.exp)
          val r1: MOption[CoreExp.Base] = transformCoreExpBase(o2.index)
          val r2: MOption[CoreExp.Base] = transformCoreExpBase(o2.arg)
          val r3: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty)
            MSome(o2(exp = r0.getOrElse(o2.exp), index = r1.getOrElse(o2.index), arg = r2.getOrElse(o2.arg), tipe = r3.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: CoreExp.If =>
          val r0: MOption[CoreExp.Base] = transformCoreExpBase(o2.cond)
          val r1: MOption[CoreExp.Base] = transformCoreExpBase(o2.tExp)
          val r2: MOption[CoreExp.Base] = transformCoreExpBase(o2.fExp)
          val r3: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty)
            MSome(o2(cond = r0.getOrElse(o2.cond), tExp = r1.getOrElse(o2.tExp), fExp = r2.getOrElse(o2.fExp), tipe = r3.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: CoreExp.Apply =>
          val r0: MOption[CoreExp.Base] = transformCoreExpBase(o2.exp)
          val r1: MOption[IS[Z, CoreExp.Base]] = transformISZ(o2.args, transformCoreExpBase _)
          val r2: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(exp = r0.getOrElse(o2.exp), args = r1.getOrElse(o2.args), tipe = r2.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: CoreExp.Fun =>
          val r0: MOption[CoreExp.Param] = transformCoreExpParam(o2.param)
          val r1: MOption[CoreExp.Base] = transformCoreExpBase(o2.exp)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(param = r0.getOrElse(o2.param), exp = r1.getOrElse(o2.exp)))
          else
            MNone()
        case o2: CoreExp.Quant =>
          val r0: MOption[CoreExp.Param] = transformCoreExpParam(o2.param)
          val r1: MOption[CoreExp.Base] = transformCoreExpBase(o2.exp)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(param = r0.getOrElse(o2.param), exp = r1.getOrElse(o2.exp)))
          else
            MNone()
        case o2: CoreExp.InstanceOfExp =>
          val r0: MOption[CoreExp.Base] = transformCoreExpBase(o2.exp)
          val r1: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(exp = r0.getOrElse(o2.exp), tipe = r1.getOrElse(o2.tipe)))
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: CoreExp.Base = r.getOrElse(o)
    val postR: MOption[CoreExp.Base] = postCoreExpBase(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformCoreExpLit(o: CoreExp.Lit): MOption[CoreExp.Lit] = {
    val preR: PreResult[CoreExp.Lit] = preCoreExpLit(o)
    val r: MOption[CoreExp.Lit] = if (preR.continu) {
      val o2: CoreExp.Lit = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[CoreExp.Lit] = o2 match {
        case o2: CoreExp.LitB =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: CoreExp.LitC =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: CoreExp.LitZ =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: CoreExp.LitF32 =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: CoreExp.LitF64 =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: CoreExp.LitR =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: CoreExp.LitString =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: CoreExp.LitRange =>
          val r0: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(tipe = r0.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: CoreExp.LitBits =>
          val r0: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(tipe = r0.getOrElse(o2.tipe)))
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: CoreExp.Lit = r.getOrElse(o)
    val postR: MOption[CoreExp.Lit] = postCoreExpLit(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformCoreExpParam(o: CoreExp.Param): MOption[CoreExp.Param] = {
    val preR: PreResult[CoreExp.Param] = preCoreExpParam(o)
    val r: MOption[CoreExp.Param] = if (preR.continu) {
      val o2: CoreExp.Param = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Typed] = transformTyped(o2.tipe)
      if (hasChanged || r0.nonEmpty)
        MSome(o2(tipe = r0.getOrElse(o2.tipe)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: CoreExp.Param = r.getOrElse(o)
    val postR: MOption[CoreExp.Param] = postCoreExpParam(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformTypedFun(o: Typed.Fun): MOption[Typed.Fun] = {
    val preR: PreResult[Typed.Fun] = preTypedFun(o) match {
     case PreResult(continu, MSome(r: Typed.Fun)) => PreResult(continu, MSome[Typed.Fun](r))
     case PreResult(_, MSome(_)) => halt("Can only produce object of type Typed.Fun")
     case PreResult(continu, _) => PreResult(continu, MNone[Typed.Fun]())
    }
    val r: MOption[Typed.Fun] = if (preR.continu) {
      val o2: Typed.Fun = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[IS[Z, Typed]] = transformISZ(o2.args, transformTyped _)
      val r1: MOption[Typed] = transformTyped(o2.ret)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty)
        MSome(o2(args = r0.getOrElse(o2.args), ret = r1.getOrElse(o2.ret)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Typed.Fun = r.getOrElse(o)
    val postR: MOption[Typed.Fun] = postTypedFun(o2) match {
     case MSome(result: Typed.Fun) => MSome[Typed.Fun](result)
     case MSome(_) => halt("Can only produce object of type Typed.Fun")
     case _ => MNone[Typed.Fun]()
    }
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformTypedMethod(o: Typed.Method): MOption[Typed.Method] = {
    val preR: PreResult[Typed.Method] = preTypedMethod(o) match {
     case PreResult(continu, MSome(r: Typed.Method)) => PreResult(continu, MSome[Typed.Method](r))
     case PreResult(_, MSome(_)) => halt("Can only produce object of type Typed.Method")
     case PreResult(continu, _) => PreResult(continu, MNone[Typed.Method]())
    }
    val r: MOption[Typed.Method] = if (preR.continu) {
      val o2: Typed.Method = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Typed.Fun] = transformTypedFun(o2.tpe)
      if (hasChanged || r0.nonEmpty)
        MSome(o2(tpe = r0.getOrElse(o2.tpe)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Typed.Method = r.getOrElse(o)
    val postR: MOption[Typed.Method] = postTypedMethod(o2) match {
     case MSome(result: Typed.Method) => MSome[Typed.Method](result)
     case MSome(_) => halt("Can only produce object of type Typed.Method")
     case _ => MNone[Typed.Method]()
    }
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

}