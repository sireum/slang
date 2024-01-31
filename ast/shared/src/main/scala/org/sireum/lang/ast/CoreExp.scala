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
}

object CoreExp {
  @datatype trait Lit extends CoreExp

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

  @datatype class ParamVarRef(val deBruijn: Z, @hidden val id: String, val tipe: Typed) extends CoreExp {
    @strictpure override def prettyST: ST = st"$id"
  }

  @datatype class LocalVarRef(val id: String, val tipe: Typed) extends CoreExp {
    @strictpure override def prettyST: ST = st"$id"
  }

  @datatype class ObjectVarRef(val owner: ISZ[String], val id: String, val tipe: Typed) extends CoreExp {
    @strictpure override def prettyST: ST = if (owner.isEmpty) st"$id" else st"${owner(owner.size - 1)}.$id"
  }

  @datatype class Tuple(val args: ISZ[CoreExp]) extends CoreExp {
    @strictpure override def tipe: Typed = Typed.Tuple(for (arg <- args) yield arg.tipe)

    @strictpure override def prettyST: ST = st"(${(for (arg <- args) yield arg.prettyST, ", ")})"
  }

  @datatype class If(val cond: CoreExp, val tExp: CoreExp, val fExp: CoreExp, val tipe: Typed) extends CoreExp {
    @strictpure override def prettyST: ST =
      st"""if (${cond.prettyST}) ${tExp.prettyST}
          |else ${fExp.prettyST}"""
  }

  @datatype class Apply(val exp: CoreExp, val args: ISZ[CoreExp], val tipe: Typed) extends CoreExp {
    @pure override def prettyST: ST = {
      exp match {
        case exp: CoreExp.ObjectVarRef =>
          if (exp.owner == binaryOwner) {
            val op = exp.id
            val left = args(0)
            val right = args(1)
            val leftOpOpt: Option[String] = left match {
              case left: CoreExp.ObjectVarRef if left.owner == binaryOwner => Some(left.id)
              case _ => None()
            }
            val rightOpOpt: Option[String] = left match {
              case right: CoreExp.ObjectVarRef if right.owner == binaryOwner => Some(right.id)
              case _ => None()
            }
            return Exp.Binary.prettyST(op, ops.StringOps(op).endsWith(":"), left.prettyST, leftOpOpt,
              left.isInstanceOf[If], right.prettyST, rightOpOpt, right.isInstanceOf[If])
          } else if (exp.owner == unaryOwner) {
            val paren: B = args(0) match {
              case _: CoreExp.LocalVarRef => F
              case _: CoreExp.ParamVarRef => F
              case _: CoreExp.ObjectVarRef => F
              case _: CoreExp.Lit => F
              case _ => T
            }
            return if (paren) st"${exp.id}(${args(0).prettyST})" else st"${exp.id}${args(0).prettyST}"
          } else {
            return st"${exp.prettyST}(${(for (arg <- args) yield arg.prettyST, ", ")})"
          }
        case _ => return st"${exp.prettyST}(${(for (arg <- args) yield arg.prettyST, ", ")})"
      }
    }
  }

  @datatype class Select(val exp: CoreExp, val id: String, val tipe: Typed) extends CoreExp {
    @strictpure override def prettyST: ST = st"${exp.prettyST}.$id"
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

  val unaryOwner: ISZ[String] = ISZ("$unary")
  val binaryOwner: ISZ[String] = ISZ("$binary")
}
