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

  @datatype trait Exp  {
    @strictpure def pos: Position
    @pure def prettyST: ST
    @pure override def string: String = {
      return prettyST.render
    }
  }

  object Exp {

    @datatype class Bool(val value: B, val pos: Position) extends Exp {
      @strictpure def prettyST: ST = if (value) st"true" else st"false"
    }

    @datatype class Int(val tipe: Typed, val value: Z, val pos: Position) extends Exp {
      @strictpure def prettyST: ST = st"$value [$tipe]"
    }

    @datatype class BitVector(val tipe: Typed, val bitWidth: Z, val hexValue: String, val pos: Position) extends Exp {
      @strictpure def prettyST: ST = st"0x$hexValue [$tipe]"
    }

    @datatype class F32(val value: org.sireum.F32, val pos: Position) extends Exp {
      @strictpure def prettyST: ST = st"$value [f32]"
    }

    @datatype class F64(val value: org.sireum.F64, val pos: Position) extends Exp {
      @strictpure def prettyST: ST = st"$value [f64]"
    }

    @datatype class Register(val n: Z, val pos: Position) extends Exp {
      @strictpure def prettyST: ST = st"$$$n"
    }

    @datatype class LocalVarRef(val id: String, val pos: Position) extends Exp {
      @strictpure def prettyST: ST = st"$id"
    }

    @datatype class Apply(val isInObject: B, val owner: ISZ[String], val id: String, val args: ISZ[Exp], val pos: Position) extends Exp {
      @pure def prettyST: ST = {
        if (ops.StringOps(id).isScalaOp && args.size == 2) {
          return st"(${args(0).prettyST} $id ${args(1).prettyST})"
        } else if (isInObject) {
          return st"${(owner, ".")}.$id(${(for (arg <- args) yield arg.prettyST, ", ")})"
        } else {
          return st"${args(0).prettyST}.$id(${(for (i <- 1 until args.size) yield args(i).prettyST)})"
        }
      }
    }

    @datatype class Select(val exp: Exp, val id: String, val pos: Position) extends Exp {
      @strictpure def prettyST: ST = st"${exp.prettyST}.$id"
    }

    @datatype class Indexing(val exp: Exp, val index: Exp, val pos: Position) extends Exp {
      @strictpure def prettyST: ST = st"${exp.prettyST}(${index.prettyST})"
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

    @datatype trait Assign extends Stmt {
      @strictpure def rhs: Exp
    }

    object Assign {

      @datatype class Local(val copy: B, val lhs: String, val rhs: Exp, val pos: Position) extends Assign {
        @strictpure def prettyST: ST = st"$lhs ${if (copy) ":=" else "="} ${rhs.prettyST}"
      }

      @datatype class Global(val copy: B, val name: ISZ[String], val rhs: Exp, val pos: Position) extends Assign {
        @strictpure def prettyST: ST = st"${(name, ".")} ${if (copy) ":=" else "="} ${rhs.prettyST}"
      }

      @datatype class Register(val lhs: Z, val rhs: Exp, val pos: Position) extends Assign {
        @strictpure def prettyST: ST = st"$lhs = ${rhs.prettyST}"
      }

      @datatype class Field(val copy: B, val register: Z, val id: String, val rhs: Exp, val pos: Position) extends Assign {
        @strictpure def prettyST: ST = st"$$$register.$id ${if (copy) ":=" else "="} ${rhs.prettyST}"
      }

      @datatype class Index(val copy: B, val register: Z, val index: Exp, val rhs: Exp, val pos: Position) extends Assign {
        @strictpure def prettyST: ST = st"$$$register(${index.prettyST}) ${if (copy) ":=" else "="} ${rhs.prettyST}"
      }

    }

    @datatype class If(val condBodies: ISZ[IfCondBody], val elseBody: Body, val pos: Position) extends Stmt {
      @strictpure def prettyST: ST = st"if (${condBodies(0).cond.prettyST}) ${condBodies(0).body.prettyST} ${(for (i <- 1 until condBodies.size) yield st" else if (${condBodies(i).cond.prettyST}) ${condBodies(i).body.prettyST} else ${elseBody.prettyST}", " ")}"
    }

    @datatype class IfCondBody(val cond: Exp, val body: Body, val pos: Position)

    @datatype class Body(val decls: ISZ[Decl], val stmts: ISZ[Stmt], val pos: Position) extends Stmt {
      @strictpure def prettyST: ST = if (decls.isEmpty)
        st"""{
            |  ${(for (stmt <- stmts) yield stmt.prettyST, "\n")}
            |}"""
      else
        st"""{
            |  ${(for (decl <- decls) yield decl.prettyST, "\n")}
            |
            |  ${(for (stmt <- stmts) yield stmt.prettyST, "\n")}
            |}"""
    }

    @datatype class While(val cond: Exp, val body: Body, val pos: Position) extends Stmt {
      @strictpure def prettyST: ST = st"while (${cond.prettyST}) ${body.prettyST}"
    }

    @datatype class Return(val expOpt: Option[Exp], val pos: Position) extends Stmt {
      @strictpure def prettyST: ST = expOpt match {
        case Some(exp) => st"return ${exp.prettyST}"
        case _ => st"return"
      }
    }

    @datatype class Block(val label: Z, val stmts: ISZ[Stmt], jump: Jump, val pos: Position) extends Stmt {
      @strictpure def prettyST: ST =
        st""".$label:
            |  ${(for (stmt <- stmts) yield stmt.prettyST, "\n")}
            |  ${jump.prettyST}"""
    }

  }

  @datatype trait Decl {
    @pure def prettyST: ST
    @pure override def string: String = {
      return prettyST.render
    }
  }

  object Decl {

    @datatype class Local(val tipe: Typed, val id: String, val pos: Position) extends Decl {
      @strictpure def prettyST: ST = st"$id : $tipe"
    }

    @datatype class Register(val tipe: Typed, val n: Z, val pos: Position) extends Decl {
      @strictpure def prettyST: ST = st"$$$n : $tipe"
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

  @datatype class Procedure(val isInObject: B,
                            val typeParams: ISZ[String],
                            val owner: ISZ[String],
                            val id: String,
                            val paramNames: ISZ[String],
                            val tipe: Typed.Fun,
                            val body: Stmt.Body,
                            val pos: Position) {
    @strictpure def prettyST: ST = {
      val pt: ST = if (typeParams.isEmpty) st"" else st"[${(typeParams, ", ")}]"
      st"procedure ${(owner, ".")}${if (isInObject) "." else "#"}$id$pt(${(for (p <- ops.ISZOps(paramNames).zip(tipe.args)) yield st"${p._1}: ${p._2}", ", ")}): ${tipe.ret} ${body.prettyST}"
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

  @datatype class Program(val globals: ISZ[Global], val procedures: ISZ[Procedure]) {
    @strictpure def prettyST: ST =
      st"""${(for (g <- globals) yield g.prettyST, "\n")}
          |
          |${(for (p <- procedures) yield p.prettyST, "\n\n")}"""
    @pure override def string: String = {
      return prettyST.render
    }
  }
}
