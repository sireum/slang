// #Sireum
/*
 Copyright (c) 2017-2023, Robby, Kansas State University
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

  @strictpure def opOpt: Option[String] = None()

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

  @datatype class ParamVarRef(val deBruijn: Z, val id: String, val tipe: Typed) extends CoreExp {
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
      exp.opOpt match {
        case Some(op) => if (args.isEmpty) {
          if (exp.opOpt.isEmpty) {
            return st"$op${exp.prettyST}"
          } else {
            return st"$op(${exp.prettyST})"
          }
        } else {
          val select = exp.asInstanceOf[Select]
          val left = select.exp
          val right = args(0)
          return Exp.Binary.prettyST(op, ops.StringOps(op).endsWith(":"), left.prettyST, left.opOpt,
            left.isInstanceOf[If], right.prettyST, right.opOpt, right.isInstanceOf[If])
        }
        case _ => return st"${exp.prettyST}(${(for (arg <- args) yield arg.prettyST, ", ")})"
      }
    }
  }

  @datatype class Select(val exp: CoreExp, val id: String, val tipe: Typed) extends CoreExp {
    @strictpure override def opOpt: Option[String] = tipe match {
      case tipe: Typed.Method if !tipe.isInObject =>
        tipe.paramNames.size match {
          case z"0" if ops.StringOps(tipe.name).startsWith("unary_") =>
            Some(ops.StringOps(tipe.name).substring(6, tipe.name.size))
          case z"1" =>
            val c = ops.StringOps(tipe.name).first
            ops.COps(c).category match {
              case ops.COps.Category.Sm => Some(tipe.name)
              case ops.COps.Category.So => Some(tipe.name)
              case _ => if ('\u0020' <= c && c <= '\u007E') Some(tipe.name) else None()
            }
          case _ => None()
        }
      case _ => None()
    }

    @strictpure override def prettyST: ST = st"${exp.prettyST}.$id"
  }

  @datatype class Fun(val params: ISZ[Param], val exp: CoreExp, val tipe: Typed) extends CoreExp {
    @strictpure override def prettyST: ST = st"((${(for (param <- params) yield param.prettyST, ", ")}) => ${exp.prettyST})"
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

  @datatype class Param(val id: String, val tipe: Typed) {
    @strictpure def prettyST: ST = st"$id: $tipe"
  }
}
