// #Sireum
/*
 Copyright (c) 2017-2025, Robby, Kansas State University
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

object IR {

  @datatype class MethodContext(val isInObject: B, val owner: ISZ[String], val id: String, val t: Typed.Fun) {
    @pure def receiverType: Typed = {
      assert(!isInObject)
      return t.args(0)
    }
  }

  object MethodContext {
    @strictpure def empty: MethodContext = MethodContext(F, ISZ(), "", Typed.Fun(Purity.Impure, F, ISZ(), Typed.nothing))
  }

  @datatype trait Exp  {
    @pure def tipe: Typed
    @strictpure def pos: Position
    @pure def prettyST: ST
    @pure override def string: String = {
      return prettyST.render
    }
  }

  object Exp {

    @datatype class Bool(val value: B, val pos: Position) extends Exp {
      @strictpure def tipe: Typed = Typed.b
      @strictpure def prettyST: ST = if (value) st"true" else st"false"
    }

    @datatype class Int(val tipe: Typed, val bitWidth: Z, val value: Z, val pos: Position) extends Exp {
      @strictpure def prettyST: ST = st"$value [$tipe]"
    }

    @datatype class F32(val value: org.sireum.F32, val pos: Position) extends Exp {
      @strictpure def tipe: Typed = Typed.f32
      @strictpure def prettyST: ST = st"$value [f32]"
    }

    @datatype class F64(val value: org.sireum.F64, val pos: Position) extends Exp {
      @strictpure def tipe: Typed = Typed.f64
      @strictpure def prettyST: ST = st"$value [f64]"
    }

    @datatype class R(val value: org.sireum.R, val pos: Position) extends Exp {
      @strictpure def tipe: Typed = Typed.r
      @strictpure def prettyST: ST = st"$value [r]"
    }

    @datatype class String(val value: org.sireum.String, val pos: Position) extends Exp {
      @strictpure def tipe: Typed = Typed.string
      @strictpure def prettyST: ST = ops.StringOps(value).escapeST
    }

    @datatype class Temp(val n: Z, val tipe: Typed, val pos: Position) extends Exp {
      @strictpure def prettyST: ST = st"$$$n"
    }

    @datatype class LocalVarRef(val isVal: B, val context: MethodContext, val id: org.sireum.String, val tipe: Typed, val pos: Position) extends Exp {
      @strictpure def prettyST: ST = st"$id"
    }

    @datatype class GlobalVarRef(val name: ISZ[org.sireum.String], val tipe: Typed, val pos: Position) extends Exp {
      @strictpure def prettyST: ST = st"${(name, ".")}"
    }

    @datatype class EnumElementRef(val owner: ISZ[org.sireum.String], val id: org.sireum.String, val ordinal: Z, val pos: Position) extends Exp {
      @strictpure def tipe: Typed = Typed.Enum(owner :+ "Type")
      @strictpure def prettyST: ST = st"${(owner, ".")}.$id"
    }

    @datatype class FieldVarRef(val owner: Typed, val receiver: Exp, val id: org.sireum.String, val tipe: Typed, val pos: Position) extends Exp {
      @strictpure def prettyST: ST = st"$owner.$id"
    }

    @datatype class Unary(val tipe: Typed, val op: lang.ast.Exp.UnaryOp.Type, val exp: Exp, val pos: Position) extends Exp {
      @strictpure def prettyST: ST = {
        val opString: org.sireum.String = op match {
          case lang.ast.Exp.UnaryOp.Not => "!"
          case lang.ast.Exp.UnaryOp.Plus => "+"
          case lang.ast.Exp.UnaryOp.Minus => "-"
          case lang.ast.Exp.UnaryOp.Complement => "~"
        }
        st"$opString(${exp.prettyST})"
      }
    }

    object Binary {
      @enum object Op {
        "Add"
        "Sub"
        "Mul"
        "Div"
        "Rem"
        "And"
        "Or"
        "Imply"
        "Xor"
        "CondAnd"
        "CondOr"
        "CondImply"
        "Eq"
        "Ne"
        "FpEq"
        "FpNe"
        "Ge"
        "Gt"
        "Le"
        "Lt"
        "Shr"
        "Ushr"
        "Shl"
        "Append"
        "Prepend"
        "AppendAll"
      }
    }

    @datatype class Binary(val tipe: Typed, val left: Exp, val op: Binary.Op.Type, val right: Exp, val pos: Position) extends Exp {
      @strictpure def prettyST: ST = {
        val opString: org.sireum.String = op match {
          case Binary.Op.Add => "+"
          case Binary.Op.Sub => "-"
          case Binary.Op.Mul => "*"
          case Binary.Op.Div => "/"
          case Binary.Op.Rem => "%"
          case Binary.Op.And => "&"
          case Binary.Op.Or => "|"
          case Binary.Op.Imply => "__>:"
          case Binary.Op.Xor => "|^"
          case Binary.Op.CondAnd => "&&"
          case Binary.Op.CondOr => "||"
          case Binary.Op.CondImply => "___>:"
          case Binary.Op.Eq => "≡"
          case Binary.Op.Ne => "≢"
          case Binary.Op.FpEq => "~="
          case Binary.Op.FpNe => "!~"
          case Binary.Op.Ge => ">="
          case Binary.Op.Gt => ">"
          case Binary.Op.Le => "<="
          case Binary.Op.Lt => "<"
          case Binary.Op.Shr => ">>"
          case Binary.Op.Ushr => ">>>"
          case Binary.Op.Shl => "<<"
          case Binary.Op.Append => ":+"
          case Binary.Op.Prepend => "+:"
          case Binary.Op.AppendAll => "++"
        }
        st"(${left.prettyST} $opString ${right.prettyST})"
      }
    }

    @datatype class If(val cond: Exp, val thenExp: Exp, val elseExp: Exp, val tipe: Typed, val pos: Position) extends Exp {
      @strictpure def prettyST: ST = st"if (${cond.prettyST}) ${thenExp.prettyST} else ${elseExp.prettyST}"
    }

    @datatype class Construct(val tipe: Typed, val args: ISZ[Exp], val pos: Position) extends Exp {
      @strictpure def prettyST: ST = st"$tipe(${(for (i <- 1 until args.size) yield args(i).prettyST, ", ")})"
    }

    @datatype class Apply(val isInObject: B, val owner: ISZ[org.sireum.String], val id: org.sireum.String, val args: ISZ[Exp],
                          val methodType: Typed.Fun, val tipe: Typed, val pos: Position) extends Exp {
      @strictpure def prettyST: ST =
        if (!isInObject && ops.StringOps(id).isScalaOp && args.size == 2) st"(${args(0).prettyST} $id ${args(1).prettyST})"
        else if (isInObject) st"${if (owner.nonEmpty) st"${(owner, ".")}." else st""}$id(${(for (arg <- args) yield arg.prettyST, ", ")})"
        else st"${args(0).prettyST}.$id(${(for (i <- 1 until args.size) yield args(i).prettyST)})"
    }

    @datatype class Select(val exp: Exp, val id: String, val tipe: Typed, val pos: Position) extends Exp {
      @strictpure def prettyST: ST = st"${exp.prettyST}.$id"
    }

    @datatype class Indexing(val exp: Exp, val tipe: Typed, val index: Exp, val pos: Position) extends Exp {
      @strictpure def prettyST: ST = st"${exp.prettyST}(${index.prettyST})"
    }

    @datatype class Type(val test: B, val exp: Exp, val t: Typed, val pos: Position) extends Exp {
      @strictpure def tipe: Typed = if (test) Typed.b else t
      @strictpure def prettyST: ST = st"(${exp.prettyST} ${if (test) "is" else "as"} $t)"
    }

  }

  @datatype trait Stmt {
    @strictpure def pos: Position
    @pure def prettyST: ST
    @pure override def string: String = {
      return prettyST.render
    }
  }

  object Stmt {

    @datatype trait Ground extends Stmt

    @datatype class Expr(val exp: Exp.Apply) extends Ground {
      @strictpure def pos: Position = exp.pos
      @strictpure def prettyST: ST = exp.prettyST
    }

    @datatype trait Assign extends Ground {
      @strictpure def rhs: Exp
    }

    object Assign {

      @datatype class Local(val copy: B, val context: MethodContext, val lhs: String, val rhs: Exp, val pos: Position) extends Assign {
        @strictpure def prettyST: ST = st"$lhs ${if (copy) ":=" else "="} ${rhs.prettyST}"
      }

      @datatype class Global(val copy: B, val name: ISZ[String], val rhs: Exp, val pos: Position) extends Assign {
        @strictpure def prettyST: ST = st"${(name, ".")} ${if (copy) ":=" else "="} ${rhs.prettyST}"
      }

      @datatype class Temp(val lhs: Z, val rhs: Exp, val pos: Position) extends Assign {
        @strictpure def prettyST: ST = st"$$$lhs = ${rhs.prettyST}"
      }

      @datatype class Field(val copy: B, val receiver: Exp, val receiverType: Typed.Name, val id: String, val rhs: Exp, val pos: Position) extends Assign {
        @strictpure def prettyST: ST = st"${receiver.prettyST}.$id ${if (copy) ":=" else "="} ${rhs.prettyST}"
      }

      @datatype class Index(val copy: B, val receiver: Exp, val receiverType: Typed.Name, val index: Exp, val rhs: Exp, val pos: Position) extends Assign {
        @strictpure def prettyST: ST = st"${receiver.prettyST}(${index.prettyST}) ${if (copy) ":=" else "="} ${rhs.prettyST}"
      }

    }

    @datatype class If(val cond: Exp, val thenBlock: Block, val elseBlock: Block, val pos: Position) extends Stmt {
      @strictpure def prettyST: ST = st"if (${cond.prettyST}) ${thenBlock.prettyST} else ${elseBlock.prettyST}"
    }

    @datatype class Block(val stmts: ISZ[Stmt], val pos: Position) extends Stmt {
      @strictpure def prettyST: ST =
        st"""{
            |  ${(for (stmt <- stmts) yield stmt.prettyST, "\n")}
            |}"""
    }

    @datatype class While(val condBlock: Block, val cond: Exp, val block: Block, val pos: Position) extends Stmt {
      @strictpure def prettyST: ST =
        st"""$condBlock
            |while (${cond.prettyST}) ${block.prettyST}"""
    }

    @datatype class Return(val expOpt: Option[Exp], val pos: Position) extends Stmt {
      @strictpure def prettyST: ST = expOpt match {
        case Some(exp) => st"return ${exp.prettyST}"
        case _ => st"return"
      }
    }

    @datatype trait Decl extends Ground {
      @pure def undeclare: Decl
    }

    object Decl {

      @datatype trait Single extends Decl {
        @strictpure def undeclare: Single
      }

      @datatype class Local(val undecl: B, val isVal: B, val tipe: Typed, val id: String, val pos: Position) extends Single {
        @strictpure def undeclare: Single = {
          val thiz = this
          thiz(undecl = T)
        }
        @strictpure def prettyST: ST = st"${if (undecl) "de" else ""}local $id: $tipe"
      }

      @datatype class Temp(val undecl: B, val tipe: Typed, val n: Z, val pos: Position) extends Single {
        @strictpure def undeclare: Single = {
          val thiz = this
          thiz(undecl = T)
        }
        @strictpure def prettyST: ST = st"${if (undecl) "un" else ""}decl $$$n: $tipe"
      }

    }

  }

  @datatype trait Jump {
    @pure def prettyST: ST
    @pure override def string: String = {
      return prettyST.render
    }
  }

  object Jump {

    @datatype class Goto(val label: Z, val pos: Position) extends Jump {
      @strictpure def prettyST: ST = st"goto .$label"
    }

    @datatype class If(val cond: Exp, thenLabel: Z, elseLabel: Z, val pos: Position) extends Jump {
      @strictpure def prettyST: ST = st"if ${cond.prettyST} goto .$thenLabel else goto .$elseLabel"
    }

    @datatype class Return(val expOpt: Option[Exp], val pos: Position) extends Jump {
      @strictpure def prettyST: ST = expOpt match {
        case Some(exp) => st"return ${exp.prettyST}"
        case _ => st"return"
      }
    }
  }

  @datatype class BasicBlock(val label: Z, val grounds: ISZ[Stmt.Ground], jump: Jump) {
    @strictpure def prettyST: ST =
      st""".$label:
          |  ${(for (ground <- grounds) yield ground.prettyST, "\n")}
          |  ${jump.prettyST}"""
  }

  @datatype trait Body {
    @pure def prettyST: ST
    @pure override def string: String = {
      return prettyST.render
    }
  }

  object Body {

    @datatype class Block(val block: Stmt.Block) extends Body {
      @strictpure def prettyST: ST = block.prettyST
    }

    @datatype class Basic(val blocks: ISZ[BasicBlock]) extends Body {
      @strictpure def prettyST: ST =
        st"""{
            |  ${(for (bb <- blocks) yield bb.prettyST, "\n\n")}
            |}"""

    }

  }

  @datatype class Procedure(val isInObject: B,
                            val typeParams: ISZ[String],
                            val owner: ISZ[String],
                            val id: String,
                            val paramNames: ISZ[String],
                            val tipe: Typed.Fun,
                            val body: Body,
                            val pos: Position) {
    @strictpure def prettyST: ST = {
      val pt: ST = if (typeParams.isEmpty) st"" else st"[${(typeParams, ", ")}]"
      val ownerOpt: Option[ST] = if (owner.isEmpty)  None() else  Some(st"${(owner, ".")}${if (isInObject) "." else "#"}")
      st"procedure $ownerOpt$id$pt(${(for (p <- ops.ISZOps(paramNames).zip(tipe.args)) yield st"${p._1}: ${p._2}", ", ")}): ${tipe.ret} ${body.prettyST}"
    }
    @pure override def string: String = {
      return prettyST.render
    }
  }

  @datatype class Global(val tipe: Typed, val name: ISZ[String], val pos: Position) {
    @strictpure def prettyST: ST = st"global ${(name, ".")}: $tipe"
    @pure override def string: String = {
      return prettyST.render
    }
  }

  @datatype class Program(val threeAddressCode: B,
                          val globals: ISZ[Global],
                          val procedures: ISZ[Procedure]) {
    @strictpure def prettyST: ST =
      st"""${(for (g <- globals) yield g.prettyST, "\n")}
          |
          |${(for (p <- procedures) yield p.prettyST, "\n\n")}"""
    @pure override def string: String = {
      return prettyST.render
    }
  }
}
