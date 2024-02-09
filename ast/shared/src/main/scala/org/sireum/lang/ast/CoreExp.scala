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
  @strictpure def tipe: Typed
  @pure def prettyST: ST
  @strictpure override def string: String = prettyST.render
  @pure def subst(sm: HashMap[String, Typed]): CoreExp
  @pure def incDeBruijn(threshold: Z): CoreExp
}

object CoreExp {
  @datatype trait Lit extends CoreExp {
    @strictpure override def subst(sm: HashMap[String, Typed]): CoreExp = this
    @strictpure def incDeBruijn(threshold: Z): CoreExp.Lit = this
  }

  @datatype class LitB(val value: B) extends Lit {
    @strictpure override def tipe: Typed = Typed.b

    @strictpure override def prettyST: ST = if (value) st"true" else st"false"
  }

  @datatype class LitC(val value: C) extends Lit {
    @strictpure override def tipe: Typed = Typed.c

    @pure override def prettyST: ST = {
      return st"'${ops.COps(value).escapeString}'"
    }
  }

  @datatype class LitZ(val value: Z) extends Lit {
    @strictpure override def tipe: Typed = Typed.z

    @strictpure override def prettyST: ST = st"$value"
  }

  @datatype class LitF32(val value: F32) extends Lit {
    @strictpure override def tipe: Typed = Typed.f32

    @strictpure override def prettyST: ST = st"${value}f"
  }

  @datatype class LitF64(val value: F64) extends Lit {
    @strictpure override def tipe: Typed = Typed.f64

    @strictpure override def prettyST: ST = st"${value}d"
  }

  @datatype class LitR(val value: R) extends Lit {
    @strictpure override def tipe: Typed = Typed.r

    @strictpure override def prettyST: ST = st"""r"$value""""
  }

  @datatype class LitString(val value: String) extends Lit {
    @strictpure override def tipe: Typed = Typed.string

    @pure override def prettyST: ST = {
      return st""""${ops.StringOps(value).escapeST}""""
    }
  }

  @datatype class LitRange(val value: Z, val tipe: Typed) extends Lit {
    @pure override def prettyST: ST = {
      val ids = tipe.asInstanceOf[Typed.Name].ids
      return st"""${ops.StringOps(ids(ids.size - 1)).toLower}"$value""""
    }
  }

  @datatype class LitBits(val value: String, val tipe: Typed) extends Lit {
    @pure override def prettyST: ST = {
      val ids = tipe.asInstanceOf[Typed.Name].ids
      return st"""${ops.StringOps(ids(ids.size - 1)).toLower}"$value""""
    }
  }

  @datatype class ParamVarRef(val deBruijn: Z, @hidden val id: String, @hidden val tipe: Typed) extends CoreExp {
    @strictpure override def prettyST: ST = st"$$$deBruijn"
    @pure override def subst(sm: HashMap[String, Typed]): CoreExp = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(tipe = tipe.subst(sm))
    }
    @pure def incDeBruijn(threshold: Z): CoreExp.ParamVarRef = {
      val thiz = this
      return if (deBruijn >= threshold) thiz(deBruijn = deBruijn + 1) else thiz
    }
  }

  @datatype class LocalVarRef(val context: ISZ[String], val id: String, val tipe: Typed) extends CoreExp {
    @strictpure override def prettyST: ST = st"$id"
    @pure override def subst(sm: HashMap[String, Typed]): CoreExp = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(tipe = tipe.subst(sm))
    }
    @strictpure def incDeBruijn(threshold: Z): CoreExp.LocalVarRef = this
  }

  @datatype class ObjectVarRef(val owner: ISZ[String], val id: String, val tipe: Typed) extends CoreExp {
    @strictpure override def prettyST: ST = if (owner.isEmpty) st"$id" else st"${owner(owner.size - 1)}.$id"
    @pure override def subst(sm: HashMap[String, Typed]): CoreExp = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(tipe = tipe.subst(sm))
    }
    @strictpure def incDeBruijn(threshold: Z): CoreExp.ObjectVarRef = this
  }

  @datatype class Binary(val left: CoreExp, val op: String, val right: CoreExp) extends CoreExp {
    @strictpure override def tipe: Typed = left.tipe
    @pure override def prettyST: ST = {
      val leftOpOpt: Option[String] = left match {
        case left: CoreExp.Binary => Some(left.op)
        case _ => None()
      }
      val rightOpOpt: Option[String] = right match {
        case right: CoreExp.Binary => Some(right.op)
        case _ => None()
      }
      return Exp.Binary.prettyST(op, ops.StringOps(op).endsWith(":"), left.prettyST, leftOpOpt,
        left.isInstanceOf[If], right.prettyST, rightOpOpt, right.isInstanceOf[If])
    }
    @pure override def subst(sm: HashMap[String, Typed]): CoreExp = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(left = left.subst(sm), right = right.subst(sm))
    }
    @pure def incDeBruijn(threshold: Z): CoreExp.Binary = {
      val thiz = this
      return thiz(left = left.incDeBruijn(threshold), right = right.incDeBruijn(threshold))
    }
  }

  @datatype class Unary(val op: Exp.UnaryOp.Type, exp: CoreExp) extends CoreExp {
    @strictpure override def tipe: Typed = exp.tipe
    @pure override def prettyST: ST = {
      val paren: B = exp match {
        case _: CoreExp.LocalVarRef => F
        case _: CoreExp.ParamVarRef => F
        case _: CoreExp.ObjectVarRef => F
        case _: CoreExp.Lit => F
        case _ => T
      }
      val opString: String = op match {
        case Exp.UnaryOp.Complement => "~"
        case Exp.UnaryOp.Minus => "-"
        case Exp.UnaryOp.Not => "!"
        case Exp.UnaryOp.Plus => "+"
      }
      return if (paren) st"$opString(${exp.prettyST})" else st"$opString${exp.prettyST}"
    }
    @pure override def subst(sm: HashMap[String, Typed]): CoreExp = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(exp = exp.subst(sm))
    }
    @pure def incDeBruijn(threshold: Z): CoreExp.Unary = {
      val thiz = this
      return thiz(exp = exp.incDeBruijn(threshold))
    }
  }

  @datatype class Select(val exp: CoreExp, val id: String, val tipe: Typed) extends CoreExp {
    @pure override def prettyST: ST = {
      return st"${exp.prettyST}.$id"
    }
    @pure override def subst(sm: HashMap[String, Typed]): CoreExp = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(exp = exp.subst(sm), tipe = tipe.subst(sm))
    }
    @pure def incDeBruijn(threshold: Z): CoreExp.Select = {
      val thiz = this
      return thiz(exp = exp.incDeBruijn(threshold))
    }
  }

  @datatype class Update(val exp: CoreExp, val id: String, val arg: CoreExp, val tipe: Typed) extends CoreExp {
    @pure override def prettyST: ST = {
      return st"${exp.prettyST}($id = ${arg.prettyST})"
    }
    @pure override def subst(sm: HashMap[String, Typed]): CoreExp = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(exp = exp.subst(sm), arg = arg.subst(sm), tipe = tipe.subst(sm))
    }
    @pure def incDeBruijn(threshold: Z): CoreExp.Update = {
      val thiz = this
      return thiz(exp = exp.incDeBruijn(threshold), arg = arg.incDeBruijn(threshold))
    }
  }

  @datatype class Indexing(val exp: CoreExp, val index: CoreExp, val tipe: Typed) extends CoreExp {
    @pure override def prettyST: ST = {
      return st"${exp.prettyST}(${index.prettyST})"
    }
    @pure override def subst(sm: HashMap[String, Typed]): CoreExp = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(exp = exp.subst(sm), index = index.subst(sm), tipe = tipe.subst(sm))
    }
    @pure def incDeBruijn(threshold: Z): CoreExp.Indexing = {
      val thiz = this
      return thiz(exp = exp.incDeBruijn(threshold), index = index.incDeBruijn(threshold))
    }
  }

  @datatype class IndexingUpdate(val exp: CoreExp, val index: CoreExp, val arg: CoreExp, val tipe: Typed) extends CoreExp {
    @pure override def prettyST: ST = {
      return st"${exp.prettyST}(${index.prettyST} ~> ${arg.prettyST})"
    }
    @pure override def subst(sm: HashMap[String, Typed]): CoreExp = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(exp = exp.subst(sm), index = index.subst(sm), arg = arg.subst(sm), tipe = tipe.subst(sm))
    }
    @pure def incDeBruijn(threshold: Z): CoreExp.IndexingUpdate = {
      val thiz = this
      return thiz(exp = exp.incDeBruijn(threshold), index = index.incDeBruijn(threshold), arg = arg.incDeBruijn(threshold))
    }
  }

  @datatype class If(val cond: CoreExp, val tExp: CoreExp, val fExp: CoreExp, val tipe: Typed) extends CoreExp {
    @strictpure override def prettyST: ST =
      st"""if (${cond.prettyST}) ${tExp.prettyST}
          |else ${fExp.prettyST}"""
    @pure override def subst(sm: HashMap[String, Typed]): CoreExp = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(cond = cond.subst(sm), tExp = tExp.subst(sm), fExp = fExp.subst(sm), tipe = tipe.subst(sm))
    }
    @pure def incDeBruijn(threshold: Z): CoreExp.If = {
      val thiz = this
      return thiz(cond = cond.incDeBruijn(threshold), tExp = tExp.incDeBruijn(threshold), fExp = fExp.incDeBruijn(threshold))
    }
  }

  @datatype class Tuple(args: ISZ[CoreExp]) extends CoreExp {
    @strictpure override def tipe: Typed = Typed.Tuple(for (arg <- args) yield arg.tipe)
    @pure override def prettyST: ST = {
      return st"(${(for (arg <- args) yield arg.prettyST, ", ")})"
    }
    @pure override def subst(sm: HashMap[String, Typed]): CoreExp = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(args = for (arg <- args) yield arg.subst(sm))
    }
    @pure def incDeBruijn(threshold: Z): CoreExp.Tuple = {
      val thiz = this
      return thiz(args = for (arg <- args) yield arg.incDeBruijn(threshold))
    }
  }

  @datatype class Apply(val exp: CoreExp, val args: ISZ[CoreExp], val tipe: Typed) extends CoreExp {
    @pure override def prettyST: ST = {
      return st"${exp.prettyST}(${(for (arg <- args) yield arg.prettyST, ", ")})"
    }
    @pure override def subst(sm: HashMap[String, Typed]): CoreExp = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(exp = exp.subst(sm), args = for (arg <- args) yield arg.subst(sm), tipe = tipe.subst(sm))
    }
    @pure def incDeBruijn(threshold: Z): CoreExp.Apply = {
      val thiz = this
      return thiz(exp = exp.incDeBruijn(threshold), args = for (arg <- args) yield arg.incDeBruijn(threshold))
    }
  }

  @datatype class Fun(val param: Param, val exp: CoreExp) extends CoreExp {
    @pure override def prettyST: ST = {
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
      return if (params.size == 1) st"(${params(0)}) => ${e.prettyST}"
      else st"{(${(params, ", ")}) => ${e.prettyST}}"
    }
    @strictpure override def tipe: Typed = Typed.Fun(T, F, ISZ(param.tipe), exp.tipe)
    @pure override def subst(sm: HashMap[String, Typed]): CoreExp = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(param = param(tipe = param.tipe.subst(sm)), exp = exp.subst(sm))
    }
    @pure def incDeBruijn(threshold: Z): CoreExp.Fun = {
      val thiz = this
      return thiz(exp = exp.incDeBruijn(threshold + 1))
    }
  }

  @datatype class Quant(val kind: Quant.Kind.Type, val param: Param, val exp: CoreExp) extends CoreExp {
    @strictpure override def tipe: Typed = Typed.b

    @pure override def prettyST: ST = {
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
      return st"$kindString{(${(params, ", ")}) => ${e.prettyST}}"
    }

    @strictpure def kindString: String = kind match {
      case Quant.Kind.ForAll => "∀"
      case Quant.Kind.Exists => "∃"
      case Quant.Kind.Fresh => "Λ"
    }
    @pure override def subst(sm: HashMap[String, Typed]): CoreExp = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(param = param(tipe = param.tipe.subst(sm)), exp = exp.subst(sm))
    }
    @pure def incDeBruijn(threshold: Z): CoreExp.Quant = {
      val thiz = this
      return thiz(exp = exp.incDeBruijn(threshold + 1))
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

  @datatype class InstanceOfExp(val isTest: B, val exp: CoreExp, val tipe: Typed) extends CoreExp {
    @strictpure def prettyST: ST = st"${exp.prettyST}.${if (isTest) "i" else "a"}sInstanceOf[$tipe]"
    @pure override def subst(sm: HashMap[String, Typed]): CoreExp = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(exp = exp.subst(sm), tipe = tipe.subst(sm))
    }
    @pure def incDeBruijn(threshold: Z): CoreExp.InstanceOfExp = {
      val thiz = this
      return thiz(exp = exp.incDeBruijn(threshold))
    }
  }

  @datatype class Arrow(val left: CoreExp, val right: CoreExp) extends CoreExp {
    @pure override def tipe: Typed = {
      return Typed.b
    }
    @strictpure def prettyST: ST = CoreExp.Binary(left, Exp.BinaryOp.Arrow, right).prettyST
    @pure override def subst(sm: HashMap[String, Typed]): CoreExp = {
      if (sm.isEmpty) {
        return this
      }
      val thiz = this
      return thiz(left = left.subst(sm), right = right.subst(sm))
    }
    @pure def incDeBruijn(threshold: Z): CoreExp.Arrow = {
      val thiz = this
      return thiz(left = left.incDeBruijn(threshold), right = right.incDeBruijn(threshold))
    }
  }
}
