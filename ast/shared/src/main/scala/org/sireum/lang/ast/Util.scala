// #Sireum
/*
 Copyright (c) 2017, Robby, Kansas State University
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
import org.sireum.message._
import org.sireum.U64._

object Util {

  @record class TypeSubstitutor(substMap: HashMap[String, Typed]) extends MTransformer {
    override def preTyped(o: Typed): MTransformer.PreResult[Typed] = {
      o match {
        case o: Typed.TypeVar =>
          substMap.get(o.id) match {
            case Some(t) => return MTransformer.PreResult(F, MSome(t))
            case _ =>
          }
        case _ =>
      }
      val r = super.preTyped(o)
      return r
    }
  }

  val symbolCharMap: HashMap[C, String] = HashMap ++ ISZ(
    '+' ~> "__plus",
    '-' ~> "__minus",
    '*' ~> "__star",
    '/' ~> "__slash",
    '%' ~> "__percent",
    '>' ~> "__gt",
    '<' ~> "__lt",
    '=' ~> "__eq",
    '!' ~> "__bang",
    '~' ~> "__tilde",
    '@' ~> "__at",
    '#' ~> "__pound",
    '$' ~> "__dollar",
    '^' ~> "__hat",
    '(' ~> "__lparen",
    ')' ~> "__rparen",
    '{' ~> "__lbrace",
    '}' ~> "__rbrace",
    '[' ~> "__lbracket",
    ']' ~> "__rbracket",
    ':' ~> "__colon",
    ';' ~> "__semi",
    '.' ~> "__dot",
    '?' ~> "__q",
    '\\' ~> "__bslash",
    ',' ~> "__comma",
  )

  @pure def ids2strings(ids: ISZ[Id]): ISZ[String] = {
    val r = MSZ.create[String](ids.size, "")
    for (i <- ids.indices) {
      r(i) = ids(i).value
    }
    return r.toIS
  }

  @pure def fileUriOptEq(posOpt1: Option[Position], posOpt2: Option[Position]): B = {
    (posOpt1, posOpt2) match {
      case (Some(pos1), Some(pos2)) => return pos1.uriOpt == pos2.uriOpt
      case _ => return F
    }
  }

  @pure def beginColumn(posOpt: Option[Position]): Z = {
//    l""" requires ∃pos: Position  posOpt == Some(pos) """
    posOpt match {
      case Some(pos) => return pos.beginColumn
      case _ => return 0
    }
  }

  @pure def beginColumnEqual(posOpt1: Option[Position], posOpt2: Option[Position]): B = {
    (posOpt1, posOpt2) match {
      case (Some(pos1), Some(pos2)) => return pos1.beginColumn == pos2.beginColumn
      case _ => return F
    }
  }

  @pure def posOptRangeA(attr1: Attr, attr2: Attr): Option[Position] = {
    return posOptRange(attr1.posOpt, attr2.posOpt)
  }

  @pure def posOptRangeTA(attr1: TypedAttr, attr2: TypedAttr): Option[Position] = {
    return posOptRange(attr1.posOpt, attr2.posOpt)
  }

  @pure def posOptRangeRA(attr1: ResolvedAttr, attr2: ResolvedAttr): Option[Position] = {
    return posOptRange(attr1.posOpt, attr2.posOpt)
  }

  @pure def posOptRange(posOpt1: Option[Position], posOpt2: Option[Position]): Option[Position] = {
    (posOpt1, posOpt2) match {
      case (Some(pos1: PosInfo), Some(pos2: PosInfo)) =>
        val offset = pos1.offset
        val length = pos2.offset + pos2.length - offset
        return Some(PosInfo(pos1.info, (conversions.Z.toU64(offset) << u64"32") | conversions.Z.toU64(length)))
      case (Some(pos1), Some(pos2)) =>
        return Some(
          FlatPos(
            pos1.uriOpt,
            conversions.Z.toU32(pos1.beginLine),
            conversions.Z.toU32(pos1.beginColumn),
            conversions.Z.toU32(pos2.endLine),
            conversions.Z.toU32(pos2.endColumn),
            conversions.Z.toU32(pos1.offset),
            conversions.Z.toU32(pos2.offset + pos2.length - pos1.length)
          )
        )
      case _ => return None()
    }
  }

  def unop(op: Exp.UnaryOp.Type): String = {
    op match {
      case Exp.UnaryOp.Not => return "!"
      case Exp.UnaryOp.Plus => return "+"
      case Exp.UnaryOp.Minus => return "-"
      case Exp.UnaryOp.Complement => return "~"
    }
  }

  def unopId(op: Exp.UnaryOp.Type): String = {
    op match {
      case Exp.UnaryOp.Not => return "unary_!"
      case Exp.UnaryOp.Plus => return "unary_+"
      case Exp.UnaryOp.Minus => return "unary_-"
      case Exp.UnaryOp.Complement => return "unary_~"
    }
  }

  @pure def isBoolBinop(op: String): B = {
    op match {
      case Exp.BinaryOp.Eq => return T
      case Exp.BinaryOp.Eq3 => return T
      case Exp.BinaryOp.Ne => return T
      case Exp.BinaryOp.Ne3 => return T
      case Exp.BinaryOp.And => return T
      case Exp.BinaryOp.Or => return T
      case Exp.BinaryOp.Xor => return T
      case Exp.BinaryOp.Imply => return T
      case Exp.BinaryOp.CondAnd => return T
      case Exp.BinaryOp.CondOr => return T
      case Exp.BinaryOp.CondImply => return T
      case _ => return F
    }
  }

  @pure def isArithBinop(op: String): B = {
    op match {
      case Exp.BinaryOp.Add => return T
      case Exp.BinaryOp.Sub => return T
      case Exp.BinaryOp.Mul => return T
      case Exp.BinaryOp.Div => return T
      case Exp.BinaryOp.Rem => return T
      case _ => return F
    }
  }

  @pure def isBitsBinop(op: String): B = {
    op match {
      case Exp.BinaryOp.Shl => return T
      case Exp.BinaryOp.Shr => return T
      case Exp.BinaryOp.Ushr => return T
      case Exp.BinaryOp.And => return T
      case Exp.BinaryOp.Or => return T
      case Exp.BinaryOp.Xor => return T
      case _ => return F
    }
  }

  @pure def isCompareBinop(op: String): B = {
    op match {
      case Exp.BinaryOp.Eq => return T
      case Exp.BinaryOp.Eq3 => return T
      case Exp.BinaryOp.Ne => return T
      case Exp.BinaryOp.Ne3 => return T
      case Exp.BinaryOp.Lt => return T
      case Exp.BinaryOp.Le => return T
      case Exp.BinaryOp.Gt => return T
      case Exp.BinaryOp.Ge => return T
      case _ => return F
    }
  }

  @pure def substAssignExp(ast: AssignExp, substMap: HashMap[String, Typed]): AssignExp = {
    if (substMap.nonEmpty) {
      val astOpt = TypeSubstitutor(substMap).transformAssignExp(ast)
      astOpt match {
        case MSome(newAst) => return newAst
        case _ => return ast
      }
    } else {
      return ast
    }
  }

  @pure def substExp(ast: Exp, substMap: HashMap[String, Typed]): Exp = {
    if (substMap.nonEmpty) {
      val astOpt = TypeSubstitutor(substMap).transformExp(ast)
      astOpt match {
        case MSome(newAst) => return newAst
        case _ => return ast
      }
    } else {
      return ast
    }
  }

  @pure def stableTypeSig(t: Typed, width: Z): ST = {
    val max: Z = if (0 < width && width <= 64) width else 64
    val bytes = ops.ISZOps(crypto.SHA3.sum512(conversions.String.toU8is(t.string))).take(max)
    var cs = ISZ[C]()
    for (b <- bytes) {
      val c = conversions.U32.toC(conversions.U8.toU32(b))
      cs = cs :+ ops.COps.hex2c((c >>> '\u0004') & '\u000F')
      cs = cs :+ ops.COps.hex2c(c & '\u000F')
    }
    return st"$cs"
  }

  @pure def stableTypeId(t: Typed, width: Z): (ST, B) = {
    t match {
      case t: Typed.Name => return if (t.args.isEmpty) (mangleName(t.ids), F) else (st"${mangleName(t.ids)}_${stableTypeSig(t, width)}", T)
      case t: Typed.Tuple => return (st"Tuple${t.args.size}_${stableTypeSig(t, width)}", T)
      case t: Typed.Fun => return (st"Fun${t.args.size}_${stableTypeSig(t, width)}", T)
      case _ => halt(s"Infeasible: $t")
    }
  }

  @pure def mangleName(ids: ISZ[String]): ST = {
    val r: ST =
      ids.size match {
        case z"0" => st"top"
        case z"1" => st"top_${ids(0)}"
        case _ => st"${(Typed.short(ids).map(encodeId _), "_")}"
      }
    return r
  }

  @pure def encodeId(id: String): ST = {
    id match {
      case Exp.BinaryOp.Add => return st"_add"
      case Exp.BinaryOp.Sub => return st"_sub"
      case Exp.BinaryOp.Mul => return st"_mul"
      case Exp.BinaryOp.Div => return st"_div"
      case Exp.BinaryOp.Rem => return st"_rem"
      case Exp.BinaryOp.Eq => return st"_eq"
      case Exp.BinaryOp.Ne => return st"_ne"
      case Exp.BinaryOp.Shl => return st"_lt"
      case Exp.BinaryOp.Shr => return st"_le"
      case Exp.BinaryOp.Ushr => return st"_gt"
      case Exp.BinaryOp.Lt => return st"_ge"
      case Exp.BinaryOp.Le => return st"_shl"
      case Exp.BinaryOp.Gt => return st"_shr"
      case Exp.BinaryOp.Ge => return st"_ushr"
      case Exp.BinaryOp.And => return st"_and"
      case Exp.BinaryOp.Or => return st"_or"
      case Exp.BinaryOp.Xor => return st"_xor"
      case Exp.BinaryOp.Append => return st"_append"
      case Exp.BinaryOp.Prepend => return st"_prepend"
      case Exp.BinaryOp.AppendAll => return st"_appendAll"
      case Exp.BinaryOp.RemoveAll => return st"_removeAll"
      case _ =>
        val cis = conversions.String.toCis(id)
        if (ops.ISZOps(cis).exists((c: C) => symbolCharMap.contains(c))) {
          var r = ISZ[String]()
          for (c <- cis) {
            symbolCharMap.get(c) match {
              case Some(s) => r = r :+ s
              case _ => r = r :+ s"$c"
            }
          }
          return st"$r"
        } else {
          return st"$id"
        }
    }
  }
}
