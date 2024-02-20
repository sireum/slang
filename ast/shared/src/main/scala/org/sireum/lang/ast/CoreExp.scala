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

package org.sireum.lang.ast

import org.sireum._


@datatype trait CoreExp {
  @strictpure def rawType: Typed
  @strictpure def tipe: Typed = rawType match {
    case t: Typed.Method if t.tpe.isByName => t.tpe.ret
    case t => t
  }
  @pure def prettyST: ST
  @strictpure def prettyPatternST: ST
  @strictpure override def string: String = prettyST.render
  @pure def subst(sm: HashMap[String, Typed]): CoreExp
  @pure def incDeBruijn(threshold: Z): CoreExp
  @pure def <(other: CoreExp): B = {
    val s1 = string
    val s2 = other.string
    return if (s1.size < s2.size) T else if (s2.size < s1.size) F else s1 < s2
  }
  @strictpure def isEquiv: B = F
  @strictpure def shouldParen: B = F
}

object CoreExp {

  @datatype trait Base extends CoreExp {
    @pure override def subst(sm: HashMap[String, Typed]): Base
    @pure def incDeBruijn(threshold: Z): Base
    def numberPattern(numMap: MBox[HashMap[(ISZ[String], String), Z]]): CoreExp.Base
  }

  @datatype trait Lit extends Base {
    @strictpure override def subst(sm: HashMap[String, Typed]): Lit = this
    @strictpure def incDeBruijn(threshold: Z): Lit = this
    @strictpure override def prettyPatternST: ST = prettyST
    override def numberPattern(numMap: MBox[HashMap[(ISZ[String], String), Z]]): CoreExp.Base = {
      return this
    }
  }

  @datatype class LitB(val value: B) extends Lit {
    @strictpure override def rawType: Typed = Typed.b
    @strictpure override def prettyST: ST = if (value) st"T" else st"F"
  }

  @datatype class LitC(val value: C) extends Lit {
    @strictpure override def rawType: Typed = Typed.c
    @pure override def prettyST: ST = {
      return st"'${ops.COps(value).escapeString}'"
    }
  }

  @datatype class LitZ(val value: Z) extends Lit {
    @strictpure override def rawType: Typed = Typed.z
    @strictpure override def prettyST: ST = st"$value"
  }

  @datatype class LitF32(val value: F32) extends Lit {
    @strictpure override def rawType: Typed = Typed.f32
    @strictpure override def prettyST: ST = st"${value}f"
  }

  @datatype class LitF64(val value: F64) extends Lit {
    @strictpure override def rawType: Typed = Typed.f64
    @strictpure override def prettyST: ST = st"${value}d"
  }

  @datatype class LitR(val value: R) extends Lit {
    @strictpure override def rawType: Typed = Typed.r
    @strictpure override def prettyST: ST = st"""r"$value""""
  }

  @datatype class LitString(val value: String) extends Lit {
    @strictpure override def rawType: Typed = Typed.string
    @pure override def prettyST: ST = {
      return st""""${ops.StringOps(value).escapeST}""""
    }
  }

  @datatype class LitRange(val value: Z, val rawType: Typed) extends Lit {
    @pure override def prettyST: ST = {
      val ids = tipe.asInstanceOf[Typed.Name].ids
      return st"""${ops.StringOps(ids(ids.size - 1)).toLower}"$value""""
    }
  }

  @datatype class LitBits(val value: String, val rawType: Typed) extends Lit {
    @pure override def prettyST: ST = {
      val ids = tipe.asInstanceOf[Typed.Name].ids
      return st"""${ops.StringOps(ids(ids.size - 1)).toLower}"$value""""
    }
  }

  @datatype class ParamVarRef(val deBruijn: Z, @hidden val id: String, @hidden val rawType: Typed) extends Base {
    @strictpure override def prettyST: ST = st"$id"
    @strictpure override def prettyPatternST: ST = prettyST
    @pure override def subst(sm: HashMap[String, Typed]): ParamVarRef = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(rawType = rawType.subst(sm))
    }
    @pure def incDeBruijn(threshold: Z): ParamVarRef = {
      val thiz = this
      return if (deBruijn >= threshold) thiz(deBruijn = deBruijn + 1) else thiz
    }
    override def numberPattern(numMap: MBox[HashMap[(ISZ[String], String), Z]]): CoreExp.Base = {
      return this
    }
  }

  @datatype class LocalVarRef(val isPattern: B, val context: ISZ[String], val id: String, val rawType: Typed) extends Base {
    @strictpure override def prettyST: ST = st"$id"
    @strictpure override def prettyPatternST: ST = st"${if (isPattern) "?" else ""}$id"
    @pure override def subst(sm: HashMap[String, Typed]): LocalVarRef = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(rawType = rawType.subst(sm))
    }
    @strictpure def incDeBruijn(threshold: Z): LocalVarRef = this
    override def numberPattern(numMap: MBox[HashMap[(ISZ[String], String), Z]]): CoreExp.Base = {
      if (isPattern) {
        val key = (context, id)
        val num: Z = numMap.value.get(key) match {
          case Some(n) => n
          case _ =>
            val n = numMap.value.size
            numMap.value = numMap.value + key ~> n
            n
        }
        val thiz = this
        return thiz(context = ISZ(), id = num.string, rawType = Typed.nothing)
      } else {
        return this
      }
    }
  }

  @datatype class ObjectVarRef(val owner: ISZ[String], val id: String, val rawType: Typed) extends Base {
    @strictpure override def prettyST: ST = if (owner.isEmpty) st"$id" else st"${owner(owner.size - 1)}.$id"
    @strictpure override def prettyPatternST: ST = prettyST
    @pure override def subst(sm: HashMap[String, Typed]): ObjectVarRef = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(rawType = rawType.subst(sm))
    }
    @strictpure def incDeBruijn(threshold: Z): ObjectVarRef = this
    override def numberPattern(numMap: MBox[HashMap[(ISZ[String], String), Z]]): CoreExp.Base = {
      return this
    }
  }

  @datatype class Binary(val left: Base, val op: String, val right: Base, val rawType: Typed) extends Base {
    @pure def prettySTH(leftST: ST, rightST: ST): ST = {
      val leftOpOpt: Option[String] = left match {
        case left: Binary => Some(left.op)
        case _ => None()
      }
      val rightOpOpt: Option[String] = right match {
        case right: Binary => Some(right.op)
        case _ => None()
      }
      return Exp.Binary.prettyST(op, ops.StringOps(op).endsWith(":"), leftST, leftOpOpt,
        left.isInstanceOf[If], rightST, rightOpOpt, right.isInstanceOf[If])
    }
    @pure override def prettyST: ST = {
      return prettySTH(left.prettyST, right.prettyST)
    }
    @pure override def prettyPatternST: ST = {
      return prettySTH(left.prettyPatternST, right.prettyPatternST)
    }
    @pure override def subst(sm: HashMap[String, Typed]): Binary = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(left = left.subst(sm), right = right.subst(sm))
    }
    @pure def incDeBruijn(threshold: Z): Binary = {
      val thiz = this
      return thiz(left = left.incDeBruijn(threshold), right = right.incDeBruijn(threshold))
    }
    override def numberPattern(numMap: MBox[HashMap[(ISZ[String], String), Z]]): CoreExp.Base = {
      val thiz = this
      return thiz(left = left.numberPattern(numMap), right = right.numberPattern(numMap))
    }
    @strictpure override def isEquiv: B = op == Exp.BinaryOp.EquivUni
    @strictpure override def shouldParen: B = T
  }

  @datatype class Unary(val op: Exp.UnaryOp.Type, exp: Base) extends Base {
    @strictpure override def rawType: Typed = exp.tipe
    @pure def prettySTH(expST: ST): ST = {
      val paren: B = exp match {
        case _: CoreExp.LocalVarRef => F
        case _: CoreExp.ParamVarRef => F
        case _: CoreExp.ObjectVarRef => F
        case exp: CoreExp.LitZ if exp.value >= 0 => F
        case exp: CoreExp.LitF32 if exp.value >= 0f => F
        case exp: CoreExp.LitF64 if exp.value >= 0d => F
        case exp: CoreExp.LitR if exp.value >= r"0" => F
        case _ => T
      }
      return if (paren) st"$opString($expST)" else st"$opString$expST"
    }
    @strictpure def opString: String = op match {
      case Exp.UnaryOp.Complement => "~"
      case Exp.UnaryOp.Minus => "-"
      case Exp.UnaryOp.Not => "!"
      case Exp.UnaryOp.Plus => "+"
    }
    @pure override def prettyST: ST = {
      return prettySTH(exp.prettyST)
    }
    @pure override def prettyPatternST: ST = {
      return prettySTH(exp.prettyPatternST)
    }
    @pure override def subst(sm: HashMap[String, Typed]): Unary = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(exp = exp.subst(sm))
    }
    @pure def incDeBruijn(threshold: Z): Unary = {
      val thiz = this
      return thiz(exp = exp.incDeBruijn(threshold))
    }
    override def numberPattern(numMap: MBox[HashMap[(ISZ[String], String), Z]]): CoreExp.Base = {
      val thiz = this
      return thiz(exp = exp.numberPattern(numMap))
    }
    @strictpure override def shouldParen: B = T
  }

  @datatype class Constructor(val rawType: Typed, args: ISZ[Base]) extends Base {
    @pure override def prettyST: ST = {
      val tOpt: Option[Typed] = if (tipe.isInstanceOf[Typed.Tuple]) None() else Some(tipe)
      return st"$tOpt(${(for (arg <- args) yield arg.prettyST, ", ")})"
    }
    @pure override def prettyPatternST: ST = {
      val tOpt: Option[Typed] = if (tipe.isInstanceOf[Typed.Tuple]) None() else Some(tipe)
      return st"$tOpt(${(for (arg <- args) yield arg.prettyPatternST, ", ")})"
    }
    @pure override def subst(sm: HashMap[String, Typed]): Constructor = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(args = for (arg <- args) yield arg.subst(sm))
    }
    @pure def incDeBruijn(threshold: Z): Constructor = {
      val thiz = this
      return thiz(args = for (arg <- args) yield arg.incDeBruijn(threshold))
    }
    override def numberPattern(numMap: MBox[HashMap[(ISZ[String], String), Z]]): CoreExp.Base = {
      val thiz = this
      return thiz(args = for (arg <- args) yield arg.numberPattern(numMap))
    }
  }

  @datatype class Select(val exp: Base, val id: String, val rawType: Typed) extends Base {
    @pure override def prettyST: ST = {
      return if (exp.shouldParen) st"(${exp.prettyST}).$id" else st"${exp.prettyST}.$id"
    }
    @pure override def prettyPatternST: ST = {
      return if (exp.shouldParen) st"(${exp.prettyPatternST}).$id" else st"${exp.prettyPatternST}.$id"
    }
    @pure override def subst(sm: HashMap[String, Typed]): Select = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(exp = exp.subst(sm), rawType = rawType.subst(sm))
    }
    @pure def incDeBruijn(threshold: Z): Select = {
      val thiz = this
      return thiz(exp = exp.incDeBruijn(threshold))
    }
    override def numberPattern(numMap: MBox[HashMap[(ISZ[String], String), Z]]): CoreExp.Base = {
      val thiz = this
      return thiz(exp = exp.numberPattern(numMap))
    }
  }

  @datatype class Update(val exp: Base, val id: String, val arg: Base, val rawType: Typed) extends Base {
    @pure override def prettyST: ST = {
      return st"${exp.prettyST}($id = ${arg.prettyST})"
    }
    @pure override def prettyPatternST: ST = {
      return st"${exp.prettyPatternST}($id = ${arg.prettyPatternST})"
    }
    @pure override def subst(sm: HashMap[String, Typed]): Update = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(exp = exp.subst(sm), arg = arg.subst(sm), rawType = rawType.subst(sm))
    }
    @pure def incDeBruijn(threshold: Z): Update = {
      val thiz = this
      return thiz(exp = exp.incDeBruijn(threshold), arg = arg.incDeBruijn(threshold))
    }
    override def numberPattern(numMap: MBox[HashMap[(ISZ[String], String), Z]]): CoreExp.Base = {
      val thiz = this
      return thiz(exp = exp.numberPattern(numMap))
    }
  }

  @datatype class Indexing(val exp: Base, val index: Base, val rawType: Typed) extends Base {
    @pure override def prettyST: ST = {
      return st"${exp.prettyST}(${index.prettyST})"
    }
    @pure override def prettyPatternST: ST = {
      return st"${exp.prettyPatternST}(${index.prettyPatternST})"
    }
    @pure override def subst(sm: HashMap[String, Typed]): Indexing = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(exp = exp.subst(sm), index = index.subst(sm), rawType = rawType.subst(sm))
    }
    @pure def incDeBruijn(threshold: Z): Indexing = {
      val thiz = this
      return thiz(exp = exp.incDeBruijn(threshold), index = index.incDeBruijn(threshold))
    }
    override def numberPattern(numMap: MBox[HashMap[(ISZ[String], String), Z]]): CoreExp.Base = {
      val thiz = this
      return thiz(exp = exp.numberPattern(numMap), index = index.numberPattern(numMap))
    }
  }

  @datatype class IndexingUpdate(val exp: Base, val index: Base, val arg: Base, val rawType: Typed) extends Base {
    @pure override def prettyST: ST = {
      return st"${exp.prettyST}(${index.prettyST} ~> ${arg.prettyST})"
    }
    @pure override def prettyPatternST: ST = {
      return st"${exp.prettyPatternST}(${index.prettyPatternST} ~> ${arg.prettyPatternST})"
    }
    @pure override def subst(sm: HashMap[String, Typed]): IndexingUpdate = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(exp = exp.subst(sm), index = index.subst(sm), arg = arg.subst(sm), rawType = rawType.subst(sm))
    }
    @pure def incDeBruijn(threshold: Z): IndexingUpdate = {
      val thiz = this
      return thiz(exp = exp.incDeBruijn(threshold), index = index.incDeBruijn(threshold), arg = arg.incDeBruijn(threshold))
    }
    override def numberPattern(numMap: MBox[HashMap[(ISZ[String], String), Z]]): CoreExp.Base = {
      val thiz = this
      return thiz(exp = exp.numberPattern(numMap), index = index.numberPattern(numMap), arg = arg.numberPattern(numMap))
    }
  }

  @datatype class If(val cond: Base, val tExp: Base, val fExp: Base, val rawType: Typed) extends Base {
    @pure def simplOpt: Option[CoreExp.Base] = {
      tExp match {
        case CoreExp.LitB(T) =>
          return Some(CoreExp.Binary(cond, Exp.BinaryOp.Or, fExp, Typed.b))
        case _ =>
      }
      fExp match {
        case CoreExp.LitB(b) =>
          return Some(CoreExp.Binary(cond, if (b) Exp.BinaryOp.Imply else Exp.BinaryOp.And, tExp, Typed.b))
        case _ =>
      }
      return None()
    }
    @pure override def prettyST: ST = {
      simplOpt match {
        case Some(e) => return e.prettyST
        case _ =>
      }
      return st"""if (${cond.prettyST}) ${tExp.prettyST}
                 |else ${fExp.prettyST}"""
    }
    @pure override def prettyPatternST: ST = {
      simplOpt match {
        case Some(e) => return e.prettyPatternST
        case _ =>
      }
      return st"""if (${cond.prettyPatternST}) ${tExp.prettyPatternST}
                 |else ${fExp.prettyPatternST}"""
    }
    @pure override def subst(sm: HashMap[String, Typed]): If = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(cond = cond.subst(sm), tExp = tExp.subst(sm), fExp = fExp.subst(sm), rawType = rawType.subst(sm))
    }
    @pure def incDeBruijn(threshold: Z): If = {
      val thiz = this
      return thiz(cond = cond.incDeBruijn(threshold), tExp = tExp.incDeBruijn(threshold), fExp = fExp.incDeBruijn(threshold))
    }
    override def numberPattern(numMap: MBox[HashMap[(ISZ[String], String), Z]]): CoreExp.Base = {
      val thiz = this
      return thiz(cond = cond.numberPattern(numMap), tExp = tExp.numberPattern(numMap), fExp = fExp.numberPattern(numMap))
    }
    @strictpure override def shouldParen: B = T
  }

  @datatype class Apply(val hasReceiver: B, val exp: Base, val args: ISZ[Base], val rawType: Typed) extends Base {
    @pure override def prettyST: ST = {
      return if (hasReceiver) st"${args(0).prettyST}.${exp.prettyST}(${(for (arg <- ops.ISZOps(args).drop(1)) yield arg.prettyST, ", ")})"
      else st"${exp.prettyST}(${(for (arg <- args) yield arg.prettyST, ", ")})"
    }
    @pure override def prettyPatternST: ST = {
      return if (hasReceiver) st"${args(0).prettyPatternST}.${exp.prettyPatternST}(${(for (arg <- ops.ISZOps(args).drop(1)) yield arg.prettyPatternST, ", ")})"
      else st"${exp.prettyPatternST}(${(for (arg <- args) yield arg.prettyPatternST, ", ")})"
    }
    @pure override def subst(sm: HashMap[String, Typed]): Apply = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(exp = exp.subst(sm), args = for (arg <- args) yield arg.subst(sm), rawType = rawType.subst(sm))
    }
    @pure def incDeBruijn(threshold: Z): Apply = {
      val thiz = this
      return thiz(exp = exp.incDeBruijn(threshold), args = for (arg <- args) yield arg.incDeBruijn(threshold))
    }
    override def numberPattern(numMap: MBox[HashMap[(ISZ[String], String), Z]]): CoreExp.Base = {
      val thiz = this
      return thiz(exp = exp.numberPattern(numMap), args = for (arg <- args) yield arg.numberPattern(numMap))
    }
  }

  @datatype class Fun(val param: Param, val exp: Base) extends Base {
    @pure def prettySTH(expST: ST): ST = {
      var params = ISZ[ST](param.prettyST)
      var e = exp
      var stop = F
      while (!stop) {
        e match {
          case fun: Fun =>
            params = params :+ fun.param.prettyST
            e = fun.exp
          case _ => stop = T
        }
      }
      return if (params.size == 1) st"(${params(0)}) => $expST"
      else st"{(${(params, ", ")}) => $expST}"
    }
    @pure def prettyST: ST = {
      return prettySTH(exp.prettyST)
    }
    @pure def prettyPatternST: ST = {
      return prettySTH(exp.prettyPatternST)
    }
    @strictpure override def rawType: Typed = Typed.Fun(T, F, ISZ(param.tipe), exp.tipe)
    @pure override def subst(sm: HashMap[String, Typed]): Fun = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(param = param(tipe = param.tipe.subst(sm)), exp = exp.subst(sm))
    }
    @pure def incDeBruijn(threshold: Z): Fun = {
      val thiz = this
      return thiz(exp = exp.incDeBruijn(threshold + 1))
    }
    override def numberPattern(numMap: MBox[HashMap[(ISZ[String], String), Z]]): CoreExp.Base = {
      val thiz = this
      return thiz(exp = exp.numberPattern(numMap))
    }
    @strictpure override def shouldParen: B = T
  }

  @datatype class Quant(val kind: Quant.Kind.Type, val param: Param, val exp: Base) extends Base {
    @strictpure override def rawType: Typed = Typed.b
    @pure def prettySTH(expST: ST): ST = {
      var params = ISZ[ST](param.prettyST)
      var e = exp
      var stop = F
      while (!stop) {
        e match {
          case quant: Quant if kind == quant.kind =>
            params = params :+ quant.param.prettyST
            e = quant.exp
          case _ => stop = T
        }
      }
      return st"$kindString{(${(params, ", ")}) => $expST}"
    }
    @pure def prettyST: ST = {
      return prettySTH(exp.prettyST)
    }
    @pure def prettyPatternST: ST = {
      return prettySTH(exp.prettyPatternST)
    }
    @strictpure def kindString: String = kind match {
      case Quant.Kind.ForAll => "∀"
      case Quant.Kind.Exists => "∃"
      case Quant.Kind.Fresh => "Λ"
    }
    @pure override def subst(sm: HashMap[String, Typed]): Quant = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(param = param(tipe = param.tipe.subst(sm)), exp = exp.subst(sm))
    }
    @pure def incDeBruijn(threshold: Z): Quant = {
      val thiz = this
      return thiz(exp = exp.incDeBruijn(threshold + 1))
    }
    override def numberPattern(numMap: MBox[HashMap[(ISZ[String], String), Z]]): CoreExp.Base = {
      val thiz = this
      return thiz(exp = exp.numberPattern(numMap))
    }
  }

  object Quant {
    @enum object Kind {
      "ForAll"
      "Exists"
      "Fresh"
    }
  }

  @datatype class Param(@hidden val id: String, val tipe: Typed) {
    @strictpure def prettyST: ST = st"$id: $tipe"
  }

  @datatype class InstanceOfExp(val isTest: B, val exp: Base, val rawType: Typed) extends Base {
    @strictpure def prettyST: ST = st"${exp.prettyST}.${if (isTest) "i" else "a"}sInstanceOf[$tipe]"
    @strictpure def prettyPatternST: ST = st"${exp.prettyPatternST}.${if (isTest) "i" else "a"}sInstanceOf[$tipe]"
    @pure override def subst(sm: HashMap[String, Typed]): InstanceOfExp = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(exp = exp.subst(sm), rawType = rawType.subst(sm))
    }
    @pure def incDeBruijn(threshold: Z): InstanceOfExp = {
      val thiz = this
      return thiz(exp = exp.incDeBruijn(threshold))
    }
    override def numberPattern(numMap: MBox[HashMap[(ISZ[String], String), Z]]): CoreExp.Base = {
      val thiz = this
      return thiz(exp = exp.numberPattern(numMap))
    }
    @strictpure override def shouldParen: B = T
  }

  @datatype class Arrow(val left: Base, val right: CoreExp) extends CoreExp {
    @pure override def rawType: Typed = {
      return Typed.b
    }
    @pure def prettySTH(leftST: ST, rightST: ST): ST = {
      val leftOpOpt: Option[String] = left match {
        case left: Binary => Some(left.op)
        case _ => None()
      }
      val rightOpOpt: Option[String] = right match {
        case right: Binary => Some(right.op)
        case _ => None()
      }
      return Exp.Binary.prettyST(Exp.BinaryOp.Arrow, T, leftST, leftOpOpt,
        left.isInstanceOf[If], rightST, rightOpOpt, right.isInstanceOf[If])
    }
    @pure override def prettyST: ST = {
      return prettySTH(left.prettyST, right.prettyST)
    }
    @pure override def prettyPatternST: ST = {
      return prettySTH(left.prettyPatternST, right.prettyPatternST)
    }
    @pure override def subst(sm: HashMap[String, Typed]): Arrow = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(left = left.subst(sm), right = right.subst(sm))
    }
    @pure def incDeBruijn(threshold: Z): Arrow = {
      val thiz = this
      return thiz(left = left.incDeBruijn(threshold), right = right.incDeBruijn(threshold))
    }
  }
}
