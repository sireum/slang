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
  @strictpure def isZero: B = F
  @strictpure def isOne: B = F
  @strictpure def isMinusOne: B = F
}

object CoreExp {

  @datatype trait Base extends CoreExp {
    @pure override def subst(sm: HashMap[String, Typed]): Base
    @pure def incDeBruijn(threshold: Z): Base
    def numberPattern(numMap: MBox[HashMap[(ISZ[String], String), Z]]): CoreExp.Base
    @strictpure def isHalt: B = F
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
    @strictpure override def isZero: B = value == 0
    @strictpure override def isOne: B = value == 1
    @strictpure override def isMinusOne: B = value == -1
  }

  @datatype class LitF32(val value: F32) extends Lit {
    @strictpure override def rawType: Typed = Typed.f32
    @strictpure override def prettyST: ST = st"${value}f"
    @strictpure override def isZero: B = value == 0f
    @strictpure override def isOne: B = value == 1f
    @strictpure override def isMinusOne: B = value == -1f
  }

  @datatype class LitF64(val value: F64) extends Lit {
    @strictpure override def rawType: Typed = Typed.f64
    @strictpure override def prettyST: ST = st"${value}d"
    @strictpure override def isZero: B = value == 0d
    @strictpure override def isOne: B = value == 1d
    @strictpure override def isMinusOne: B = value == -1d
  }

  @datatype class LitR(val value: R) extends Lit {
    @strictpure override def rawType: Typed = Typed.r
    @strictpure override def prettyST: ST = st"""r"$value""""
    @strictpure override def isZero: B = value == r"0"
    @strictpure override def isOne: B = value == r"1"
    @strictpure override def isMinusOne: B = value == r"-1"
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
    @strictpure override def isZero: B = value == 0
    @strictpure override def isOne: B = value == 1
    @strictpure override def isMinusOne: B = value == -1
  }

  @datatype class LitBits(val value: String, val rawType: Typed) extends Lit {
    @pure override def prettyST: ST = {
      val ids = tipe.asInstanceOf[Typed.Name].ids
      return st"""${ops.StringOps(ids(ids.size - 1)).toLower}"$value""""
    }
    @strictpure override def isZero: B = Z(value).get == 0
    @strictpure override def isOne: B = Z(value).get == 1
    @strictpure override def isMinusOne: B = Z(value).get == -1
  }

  @datatype class LitEnum(val owner: ISZ[String], val id: String, val ordinal: Z) extends Lit {
    @strictpure def rawType: Typed = Typed.Enum(owner :+ "Type")
    @pure override def prettyST: ST = {
      return st"""${owner(owner.size - 1)}.$id"""
    }
    @strictpure override def isZero: B = F
    @strictpure override def isOne: B = F
    @strictpure override def isMinusOne: B = F
  }

  @datatype class StringInterpolate(val prefix: String, val lits: ISZ[String], val args: ISZ[Base], @hidden val rawType: Typed) extends Base {
    @pure override def prettyST: ST = {
      val size: Z = if (lits.size < args.size) args.size else lits.size
      val sts: ISZ[ST] = for (i <- 0 until size;
                              e <- (if (i < lits.size) ISZ(ops.StringOps(lits(i)).escapeST) else ISZ[ST]()) ++
                                (if (i < args.size) ISZ(st"$${${args(i).prettyST}}") else ISZ[ST]())) yield e
      return st"""$prefix"${(sts, "")}""""
    }

    @pure override def subst(sm: HashMap[String, Typed]): StringInterpolate = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(args = for (arg <- args) yield arg.subst(sm))
    }

    @pure override def incDeBruijn(threshold: Z): StringInterpolate = {
      val thiz = this
      return thiz(args = for (arg <- args) yield arg.incDeBruijn(threshold))
    }

    @pure override def numberPattern(numMap: MBox[HashMap[(ISZ[String], String), Z]]): StringInterpolate = {
      val thiz = this
      return thiz(args = for (arg <- args) yield arg.numberPattern(numMap))
    }

    @pure override def prettyPatternST: ST = {
      val size: Z = if (lits.size < args.size) args.size else lits.size
      val sts: ISZ[ST] = for (i <- 0 until size;
                              e <- (if (i < lits.size) ISZ(ops.StringOps(lits(i)).escapeST) else ISZ[ST]()) ++
                                (if (i < args.size) ISZ(st"$${${args(i).prettyPatternST}}") else ISZ[ST]())) yield e
      return st"""$prefix"${(sts, "")}""""
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
    @strictpure override def prettyST: ST =
      if (owner.isEmpty) st"$id" else st"${owner(owner.size - 1)}.$id"
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
        case Apply(Select(_, op, _), ISZ(_), _) if ops.StringOps(op).isScalaOp => Some(op)
        case _ => None()
      }
      val rightOpOpt: Option[String] = right match {
        case right: Binary => Some(right.op)
        case Apply(Select(_, op, _), ISZ(_), _) if ops.StringOps(op).isScalaOp => Some(op)
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
    @strictpure def tOpt: Option[ST] = tipe match {
      case _: Typed.Tuple => None()
      case tipe: Typed.Name =>
        if (tipe.ids == Typed.isName || tipe.ids == Typed.msName)
          if (tipe.args(0) == Typed.z) Some(st"${tipe.ids(tipe.ids.size - 1)}Z")
          else Some(st"${tipe.ids(tipe.ids.size - 1)}[${tipe.args(0)}, ${tipe.args(1)}]")
        else if (tipe.ids.size > 2 && tipe.ids(0) == "org" && tipe.ids(1) == "sireum")
          Some(st"${(ops.ISZOps(tipe.ids).drop(2), ".")}")
        else Some(st"${(tipe.ids, ".")}")
      case tipe => Some(st"$tipe")
    }
    @pure override def prettyST: ST = {
      return st"$tOpt(${(for (arg <- args) yield arg.prettyST, ", ")})"
    }
    @pure override def prettyPatternST: ST = {
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
        case True =>
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
      return st"""if (${cond.prettyST})
                 |  ${tExp.prettyST}
                 |else
                 |  ${fExp.prettyST}"""
    }
    @pure override def prettyPatternST: ST = {
      simplOpt match {
        case Some(e) => return e.prettyPatternST
        case _ =>
      }
      return st"""if (${cond.prettyPatternST})
                 |  ${tExp.prettyPatternST}
                 |else
                 |  ${fExp.prettyPatternST}"""
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

  @datatype class Apply(val exp: Base, val args: ISZ[Base], val rawType: Typed) extends Base {
    @pure override def prettyST: ST = {
      if (args.size == 1) {
        exp match {
          case exp: Select if ops.StringOps(exp.id).isScalaOp =>
            return Binary(exp.exp, exp.id, args(0), rawType).prettyST
          case _ =>
        }
      }
      return st"${exp.prettyST}(${(for (arg <- args) yield arg.prettyST, ", ")})"
    }
    @pure override def prettyPatternST: ST = {
      if (args.size == 1) {
        exp match {
          case exp: Select if ops.StringOps(exp.id).isScalaOp =>
            return Binary(exp.exp, exp.id, args(0), rawType).prettyPatternST
          case _ =>
        }
      }
      return st"${exp.prettyPatternST}(${(for (arg <- args) yield arg.prettyPatternST, ", ")})"
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
    @pure def prettySTH(expST: Base => ST @pure): ST = {
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
      val r =
        st"""{(${(params, ", ")}) =>
            |  ${expST(e)}
            |}"""
      return r
    }
    @pure def prettyST: ST = {
      return prettySTH((e: Base) => e.prettyST)
    }
    @pure def prettyPatternST: ST = {
      return prettySTH((e: Base) => e.prettyPatternST)
    }
    @strictpure override def rawType: Typed = Typed.Fun(Purity.StrictPure, F, ISZ(param.tipe), exp.tipe)
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
  }

  @datatype class Quant(val kind: Quant.Kind.Type, val param: Param, val exp: Base) extends Base {
    @strictpure override def rawType: Typed = Typed.b
    @pure def prettySTH(expST: Base => ST @pure): ST = {
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
      return st"$kindString{(${(params, ", ")}) => ${expST(e)}}"
    }
    @pure def prettyST: ST = {
      return prettySTH((e: Base) => e.prettyST)
    }
    @pure def prettyPatternST: ST = {
      return prettySTH((e: Base) => e.prettyPatternST)
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

  @datatype class Halt extends CoreExp.Base {
    @strictpure override def subst(sm: HashMap[String, Typed]): Base = this
    @strictpure override def incDeBruijn(threshold: Z): Base = this
    @strictpure override def numberPattern(numMap: MBox[HashMap[(ISZ[String], String), Z]]): Base = this
    @strictpure override def rawType: Typed = Typed.nothing
    @strictpure override def prettyST: ST = st"""abort"""
    @strictpure override def prettyPatternST: ST = prettyST
    @strictpure override def isHalt: B = T
  }

  @datatype class Labeled(val numOpt: Option[Z], val exp: Base) extends CoreExp.Base {
    @strictpure override def rawType: Typed = exp.rawType
    @strictpure override def prettyST: ST = st"(${exp.prettyST}) : @l"
    @strictpure override def prettyPatternST: ST = prettyST
    @pure override def subst(sm: HashMap[String, Typed]): Labeled = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(exp = exp.subst(sm))
    }
    @pure def incDeBruijn(threshold: Z): Labeled = {
      val thiz = this
      return thiz(exp = exp.incDeBruijn(threshold))
    }
    override def numberPattern(numMap: MBox[HashMap[(ISZ[String], String), Z]]): CoreExp.Base = {
      val thiz = this
      return thiz(exp = exp.numberPattern(numMap))
    }
    @strictpure override def shouldParen: B = T
  }

  @datatype trait Extended extends CoreExp.Base {
    @pure def pos: message.Position
    @pure override def subst(sm: HashMap[String, Typed]): Base = {
      halt("Unsupported operation CoreExp.Extended.subst")
    }
    @pure def incDeBruijn(threshold: Z): Base = {
      halt("Unsupported operation CoreExp.Extended.incDeBruijn")
    }
    def numberPattern(numMap: MBox[HashMap[(ISZ[String], String), Z]]): CoreExp.Base = {
      halt("Unsupported operation CoreExp.Extended.numberPattern")
    }
    @strictpure override def prettyPatternST: ST = {
      halt("Unsupported operation CoreExp.Extended.prettyPatternST")
    }
  }

  object Extended {

    @datatype class StrictPureBlock(val value: lang.ast.AssignExp,
                                    val funStack: Stack[(String, Typed)],
                                    val localStack: HashSMap[(ISZ[String], String), CoreExp.Base]) extends Extended {
      @spec def valueNotReturn = Invariant(!value.isInstanceOf[lang.ast.Stmt.Return])
      @strictpure override def rawType: Typed = value.typedOpt.get
      @strictpure override def pos: message.Position = value.asStmt.posOpt.get
      @pure override def prettyST: ST = {
        return value.prettyST
      }
    }
  }

  val Abort: CoreExp.Halt = CoreExp.Halt()
  val True: CoreExp.LitB = CoreExp.LitB(T)
  val False: CoreExp.LitB = CoreExp.LitB(F)
  val Z_0: CoreExp.LitZ = CoreExp.LitZ(0)
  val Z_1: CoreExp.LitZ = CoreExp.LitZ(1)
  val Z_M1: CoreExp.LitZ = CoreExp.LitZ(-1)
  val F32_0: CoreExp.LitF32 = CoreExp.LitF32(0f)
  val F32_1: CoreExp.LitF32 = CoreExp.LitF32(1f)
  val F32_M1: CoreExp.LitF32 = CoreExp.LitF32(-1f)
  val F32_Max: CoreExp.LitF32 = CoreExp.LitF32(F32.MaxValue)
  val F32_Min: CoreExp.LitF32 = CoreExp.LitF32(F32.MinValue)
  val F32_NaN: CoreExp.LitF32 = CoreExp.LitF32(F32.NaN)
  val F32_PInf: CoreExp.LitF32 = CoreExp.LitF32(F32.PInf)
  val F32_NInf: CoreExp.LitF32 = CoreExp.LitF32(F32.NInf)
  val F64_0: CoreExp.LitF64 = CoreExp.LitF64(0d)
  val F64_1: CoreExp.LitF64 = CoreExp.LitF64(1d)
  val F64_M1: CoreExp.LitF64 = CoreExp.LitF64(-1d)
  val F64_Max: CoreExp.LitF64 = CoreExp.LitF64(F64.MaxValue)
  val F64_Min: CoreExp.LitF64 = CoreExp.LitF64(F64.MinValue)
  val F64_NaN: CoreExp.LitF64 = CoreExp.LitF64(F64.NaN)
  val F64_PInf: CoreExp.LitF64 = CoreExp.LitF64(F64.PInf)
  val F64_NInf: CoreExp.LitF64 = CoreExp.LitF64(F64.NInf)
  val R_0: CoreExp.LitR = CoreExp.LitR(r"0")
  val R_1: CoreExp.LitR = CoreExp.LitR(r"1")
  val R_M1: CoreExp.LitR = CoreExp.LitR(r"-1")

  @strictpure def ite(cond: CoreExp.Base, tExp: CoreExp.Base, fExp: CoreExp.Base, t: Typed): CoreExp.Base = (tExp, fExp) match {
    case (True, True) => True
    case (True, False) => cond
    case (False, True) => CoreExp.Unary(Exp.UnaryOp.Not, cond)
    case (False, False) => False
    case (_, _) => CoreExp.If(cond, tExp, fExp, t)
  }
  @strictpure def condAnd(left: CoreExp.Base, right: CoreExp.Base): CoreExp.Base =
    ite(left, right, False, Typed.b)
  @strictpure def condOr(left: CoreExp.Base, right: CoreExp.Base): CoreExp.Base =
    ite(left, True, right, Typed.b)
  @strictpure def condImply(left: CoreExp.Base, right: CoreExp.Base): CoreExp.Base =
    ite(left, right, True, Typed.b)
  @pure def bigAnd(conjuncts: ISZ[CoreExp.Base]): CoreExp.Base = {
    if (conjuncts.isEmpty) {
      return True
    }
    var r = conjuncts(0)
    for (i <- 1 until conjuncts.size) {
      r = CoreExp.Binary(r, Exp.BinaryOp.And, conjuncts(i), Typed.b)
    }
    return r
  }

}
