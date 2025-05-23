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
    @strictpure def prettyST(p: Printer): ST = p.exp(this) match {
      case Some(r) => r
      case _ => prettyRawST(p)
    }
    @pure def prettyRawST(p: Printer): ST
    @pure def numOfTemps: Z
    @pure def depth: Z
  }

  object Exp {

    @datatype class Bool(val value: B, val pos: Position) extends Exp {
      @strictpure def tipe: Typed = Typed.b
      @strictpure def prettyRawST(p: Printer): ST = if (value) st"true" else st"false"
      @strictpure def numOfTemps: Z = 0
      @strictpure def depth: Z = 1
    }

    @datatype class Int(val tipe: Typed, val value: Z, val pos: Position) extends Exp {
      @strictpure def prettyRawST(p: Printer): ST = st"($value: $tipe)"
      @strictpure def numOfTemps: Z = 0
      @strictpure def depth: Z = 1
    }

    @datatype class F32(val value: org.sireum.F32, val pos: Position) extends Exp {
      @strictpure def tipe: Typed = Typed.f32
      @strictpure def prettyRawST(p: Printer): ST = st"$value"
      @strictpure def numOfTemps: Z = 0
      @strictpure def depth: Z = 1
    }

    @datatype class F64(val value: org.sireum.F64, val pos: Position) extends Exp {
      @strictpure def tipe: Typed = Typed.f64
      @strictpure def prettyRawST(p: Printer): ST = st"$value"
      @strictpure def numOfTemps: Z = 0
      @strictpure def depth: Z = 1
    }

    @datatype class R(val value: org.sireum.R, val pos: Position) extends Exp {
      @strictpure def tipe: Typed = Typed.r
      @strictpure def prettyRawST(p: Printer): ST = st"$value"
      @strictpure def numOfTemps: Z = 0
      @strictpure def depth: Z = 1
    }

    @datatype class String(val value: org.sireum.String, val pos: Position) extends Exp {
      @strictpure def tipe: Typed = Typed.string
      @strictpure def prettyRawST(p: Printer): ST = st""""${ops.StringOps(value).escapeST}""""
      @strictpure def numOfTemps: Z = 0
      @strictpure def depth: Z = 1
    }

    @datatype class Temp(val n: Z, val tipe: Typed, val pos: Position) extends Exp {
      @strictpure def prettyRawST(p: Printer): ST = st"($$$n: $tipe)"
      @strictpure def numOfTemps: Z = 1
      @strictpure def depth: Z = 1
    }

    @datatype class LocalVarRef(val isVal: B, val context: MethodContext, val id: org.sireum.String, val tipe: Typed, val pos: Position) extends Exp {
      @strictpure def prettyRawST(p: Printer): ST = st"$id"
      @strictpure def numOfTemps: Z = 0
      @strictpure def depth: Z = 1
    }

    @datatype class GlobalVarRef(val name: ISZ[org.sireum.String], val tipe: Typed, val pos: Position) extends Exp {
      @strictpure def prettyRawST(p: Printer): ST = st"${(name, ".")}"
      @strictpure def numOfTemps: Z = 0
      @strictpure def depth: Z = 1
    }

    @datatype class EnumElementRef(val owner: ISZ[org.sireum.String], val id: org.sireum.String, val ordinal: Z, val pos: Position) extends Exp {
      @strictpure def tipe: Typed = Typed.Enum(owner :+ "Type")
      @strictpure def prettyRawST(p: Printer): ST = st"${(owner, ".")}.$id"
      @strictpure def numOfTemps: Z = 0
      @strictpure def depth: Z = 1
    }

    @datatype class FieldVarRef(val receiver: Exp, val id: org.sireum.String, val tipe: Typed, val pos: Position) extends Exp {
      @strictpure def prettyRawST(p: Printer): ST = st"${receiver.prettyST(p)}.$id"
      @strictpure def numOfTemps: Z = receiver.numOfTemps
      @strictpure def depth: Z = 1 + receiver.depth
    }

    @datatype class Unary(val tipe: Typed, val op: lang.ast.Exp.UnaryOp.Type, val exp: Exp, val pos: Position) extends Exp {
      @strictpure def prettyRawST(p: Printer): ST = {
        val opString: org.sireum.String = op match {
          case lang.ast.Exp.UnaryOp.Not => "!"
          case lang.ast.Exp.UnaryOp.Plus => "+"
          case lang.ast.Exp.UnaryOp.Minus => "-"
          case lang.ast.Exp.UnaryOp.Complement => "~"
        }
        st"$opString(${exp.prettyST(p)})"
      }
      @strictpure def numOfTemps: Z = exp.numOfTemps
      @strictpure def depth: Z = 1 + exp.depth
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
      @strictpure def prettyRawST(p: Printer): ST = {
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
        st"(${left.prettyST(p)} $opString ${right.prettyST(p)})"
      }
      @strictpure def numOfTemps: Z = left.numOfTemps + right.numOfTemps
      @strictpure def depth: Z = 1 + max(left.depth, right.depth)
    }

    @datatype class If(val cond: Exp, val thenExp: Exp, val elseExp: Exp, val tipe: Typed, val pos: Position) extends Exp {
      @strictpure def prettyRawST(p: Printer): ST = st"if (${cond.prettyST(p)}) ${thenExp.prettyST(p)} else ${elseExp.prettyST(p)}"
      @strictpure def numOfTemps: Z = cond.numOfTemps + thenExp.numOfTemps + elseExp.numOfTemps
      @strictpure def depth: Z = 1 + max(cond.depth, max(thenExp.depth, elseExp.depth))
    }

    @datatype class Construct(val tipe: Typed.Name, val args: ISZ[Exp], val pos: Position) extends Exp {
      @strictpure def prettyRawST(p: Printer): ST = st"$tipe(${(for (arg <- args) yield arg.prettyST(p), ", ")})"
      @pure def numOfTemps: Z = {
        var r: Z = 0
        for (arg <- args) {
          r = r + arg.numOfTemps
        }
        return r
      }
      @pure def depth: Z = {
        var r: Z = 0
        for (arg <- args) {
          r = max(r, arg.depth)
        }
        return r + 1
      }
    }

    @datatype class Apply(val isInObject: B, val owner: ISZ[org.sireum.String], val id: org.sireum.String,
                          val args: ISZ[Exp], val methodType: Typed.Fun, val pos: Position) extends Exp {
      @strictpure def tipe: Typed = methodType.ret
      @strictpure def prettyRawST(p: Printer): ST = p.exp(this).getOrElse(
        if (!isInObject && ops.StringOps(id).isScalaOp && args.size == 2) st"(${args(0).prettyST(p)} $id ${args(1).prettyST(p)})"
        else if (isInObject) st"${if (owner.nonEmpty) st"${(owner, ".")}." else st""}$id(${(for (arg <- args) yield arg.prettyST(p), ", ")})"
        else st"${args(0).prettyST(p)}.$id(${(for (i <- 1 until args.size) yield args(i).prettyST(p), ", ")})")
      @pure def numOfTemps: Z = {
        var r: Z = 0
        for (arg <- args) {
          r = r + arg.numOfTemps
        }
        return r
      }
      @pure def depth: Z = {
        var r: Z = 0
        for (arg <- args) {
          r = max(r, arg.depth)
        }
        return r + 1
      }
    }

    @datatype class Indexing(val exp: Exp, val index: Exp, val pos: Position) extends Exp {
      @strictpure def tipe: Typed = exp.tipe.asInstanceOf[Typed.Name].args(1)
      @strictpure def prettyRawST(p: Printer): ST = st"${exp.prettyST(p)}(${index.prettyST(p)})"
      @strictpure def numOfTemps: Z = exp.numOfTemps + index.numOfTemps
      @strictpure def depth: Z = 1 + max(exp.depth, index.depth)
    }

    @datatype class Type(val test: B, val exp: Exp, val t: Typed.Name, val pos: Position) extends Exp {
      @strictpure def tipe: Typed.Name = if (test) Typed.b else t
      @strictpure def prettyRawST(p: Printer): ST = st"(${exp.prettyST(p)} ${if (test) "is" else "as"} $t)"
      @strictpure def numOfTemps: Z = exp.numOfTemps
      @strictpure def depth: Z = 1 + exp.depth
    }

    @datatype class Intrinsic(val intrinsic: Intrinsic.Type) extends Exp {
      @strictpure def tipe: Typed = intrinsic.tipe
      @strictpure def prettyRawST(p: Printer): ST = intrinsic.prettyST(p)
      @strictpure def numOfTemps: Z = intrinsic.numOfTemps
      @strictpure def pos: Position = intrinsic.pos
      @strictpure def depth: Z = intrinsic.depth
    }

    object Intrinsic {
      @sig trait Type {
        @pure def tipe: Typed
        @pure def pos: Position
        @pure def prettyST(p: Printer): ST
        @pure def numOfTemps: Z
        @pure def depth: Z
      }
    }
  }

  @datatype trait Stmt {
    @strictpure def pos: Position
    @strictpure def prettyST(p: Printer): ST = p.stmt(this) match {
      case Some(r) => r
      case _ => prettyRawST(p)
    }
    @pure def prettyRawST(p: Printer): ST
  }

  object Stmt {

    @datatype trait Ground extends Stmt

    @datatype class Expr(val exp: Exp.Apply) extends Ground {
      @strictpure def pos: Position = exp.pos
      @strictpure def prettyRawST(p: Printer): ST = exp.prettyST(p)
    }

    @datatype trait Assign extends Ground {
      @strictpure def rhs: Exp
    }

    object Assign {

      @datatype class Local(val context: MethodContext, val lhs: String, val tipe: Typed, val rhs: Exp, val pos: Position) extends Assign {
        @strictpure def prettyRawST(p: Printer): ST = st"$lhs = ${rhs.prettyST(p)}"
      }

      @datatype class Global(val name: ISZ[String], val tipe: Typed, val rhs: Exp, val pos: Position) extends Assign {
        @strictpure def prettyRawST(p: Printer): ST = st"${(name, ".")} = ${rhs.prettyST(p)}"
      }

      @datatype class Temp(val lhs: Z, val rhs: Exp, val pos: Position) extends Assign {
        @strictpure def prettyRawST(p: Printer): ST = st"$$$lhs = ${rhs.prettyST(p)}"
      }

      @datatype class Field(val receiver: Exp, val id: String, val tipe: Typed, val rhs: Exp, val pos: Position) extends Assign {
        @strictpure def prettyRawST(p: Printer): ST = st"${receiver.prettyST(p)}.$id = ${rhs.prettyST(p)}"
      }

      @datatype class Index(val receiver: Exp, val index: Exp, val rhs: Exp, val pos: Position) extends Assign {
        @strictpure def prettyRawST(p: Printer): ST = st"${receiver.prettyST(p)}(${index.prettyST(p)}) = ${rhs.prettyST(p)}"
      }

    }

    @datatype class Decl(val undecl: B, val isVal: B, val isAlloc: B, val context: MethodContext, val locals: ISZ[Decl.Local], val pos: Position) extends Ground {
      @strictpure def undeclare: Decl = {
        val thiz = this
        thiz(undecl = T)
      }
      @strictpure def prettyRawST(p: Printer): ST = st"${if (isAlloc) if (undecl) "unalloc" else "alloc" else if (undecl) "undecl" else "decl"} ${(for (l <- locals) yield l.prettyST, ", ")}"
    }

    object Decl {
      @datatype class Local(val id: String, val tipe: Typed) {
        @strictpure def prettyST: ST = st"$id: $tipe"
      }
    }

    @datatype class Intrinsic(val intrinsic: Intrinsic.Type) extends Ground {
      @strictpure def prettyRawST(p: Printer): ST =intrinsic.prettyST(p)
      @strictpure def pos: Position = intrinsic.pos
    }

    object Intrinsic {
      @sig trait Type {
        @pure def pos: Position
        @pure def prettyST(p: Printer): ST
      }
    }

    @datatype class Assertume(val isAssert: B, val cond: Exp, val messageOpt: Option[ExpBlock], val pos: Position) extends Stmt {
      @strictpure def prettyRawST(p: Printer): ST = {
        val mOpt: Option[ST] = messageOpt match {
          case Some(m) => Some(st", ${m.prettyST(p)}")
          case _ => None()
        }
        st"${if (isAssert) "assert" else "assume"}(${cond.prettyST(p)}$mOpt)"
      }
    }

    @datatype class Print(val kind: Print.Kind.Type, val line: B, val args: ISZ[Exp], val pos: Position) extends Stmt {
      @strictpure def prettyRawST(p: Printer): ST = {
        val printKeyword: String = kind match {
          case Print.Kind.Out => "print"
          case Print.Kind.Err => "eprint"
          case Print.Kind.OutErr => "cprint"
        }
        val ln: String = if (line) "ln" else ""
        st"$printKeyword$ln(${(for (arg <- args) yield arg.prettyST(p), ", ")})"
      }
    }

    @datatype class Halt(val message: Exp, val pos: Position) extends Stmt {
      @strictpure def prettyRawST(p: Printer): ST = st"halt(${message.prettyST(p)})"
    }

    object Print {
      @enum object Kind {
        "Out"
        "Err"
        "OutErr"
      }
    }

    @datatype class AssignPattern(val context: MethodContext, val pattern: lang.ast.Pattern, val rhs: Exp, val pos: Position) extends Stmt {
      @strictpure def prettyRawST(p: Printer): ST = st"${pattern.prettyST} = ${rhs.prettyST(p)}"
    }

    @datatype class Block(val stmts: ISZ[Stmt], val pos: Position) extends Stmt {
      @strictpure def prettyRawST(p: Printer): ST =
        st"""{
            |  ${(for (stmt <- stmts) yield stmt.prettyST(p), "\n")}
            |}"""
    }

    @datatype class If(val cond: Exp, val thenBlock: Block, val elseBlock: Block, val pos: Position) extends Stmt {
      @strictpure def prettyRawST(p: Printer): ST = st"if (${cond.prettyST(p)}) ${thenBlock.prettyST(p)} else ${elseBlock.prettyST(p)}"
    }

    @datatype class Match(val exp: Exp, val cases: ISZ[Match.Case], val pos: Position) extends Stmt {
      @strictpure def prettyRawST(p: Printer): ST =
        st"""${exp.prettyST(p)} match {
            |  ${(for (c <- cases) yield c.prettyST(p), "\n")}
            |}"""
    }

    object Match {
      @datatype class Case(val decl: Stmt.Decl, val pattern: Pattern, val condOpt: Option[ExpBlock], val body: Block) {
        @strictpure def prettyST(p: Printer): ST = {
          val cOpt: Option[ST] = condOpt match {
            case Some(cond) => Some(st" if ${cond.prettyST(p)}")
            case _ => None()
          }
          st"""case ${pattern.prettyST}$cOpt =>
              |  ${(for (stmt <- body.stmts) yield stmt.prettyST(p), "\n")}"""
        }
      }
    }

    @datatype class While(val cond: ExpBlock, val block: Block, val pos: Position) extends Stmt {
      @strictpure def prettyRawST(p: Printer): ST = st"while (${cond.prettyST(p)}) ${block.prettyST(p)}"
    }

    @datatype class For(val context: MethodContext,
                        val idOpt: Option[String],
                        val range: For.Range,
                        val condOpt: Option[ExpBlock],
                        val block: Block,
                        val pos: Position) extends Stmt {
      @strictpure def prettyRawST(p: Printer): ST = {
        val cOpt: Option[ST] = condOpt match {
          case Some(cond) => Some(
            if (cond.stmts.isEmpty)
              st""" if {
                  |  ${(for (stmt <- cond.stmts) yield stmt.prettyST(p), "\n")}
                  |  ${cond.exp.prettyST(p)}
                  |}"""
            else st" if ${cond.exp.prettyST(p)}"
          )
          case _ => None()
        }
        st"for (${idOpt.getOrElse("_")} <- ${range.prettyST(p)}$cOpt) ${block.prettyST(p)}"
      }
    }

    object For {
      @datatype trait Range {
        @pure def prettyST(p: Printer): ST
      }

      object Range {

        @datatype class Expr(val exp: Exp, val pos: Position) extends Range {
          @strictpure override def prettyST(p: Printer): ST = exp.prettyST(p)
        }

        @datatype class Step(val isInclusive: B, val start: Exp, val end: Exp, val byOpt: Option[Exp], val pos: Position) extends Range {
          @strictpure override def prettyST(p: Printer): ST = {
            val bOpt: Option[ST] = byOpt match {
              case Some(by) => Some(st" by ${by.prettyST(p)}")
              case _ => None()
            }
            st"${start.prettyST(p)} ${if (isInclusive) "to" else "until"} ${end.prettyST(p)}$bOpt"
          }
        }

      }
    }

    @datatype class Return(val expOpt: Option[Exp], val pos: Position) extends Stmt {
      @strictpure def prettyRawST(p: Printer): ST = expOpt match {
        case Some(exp) => st"return ${exp.prettyST(p)}"
        case _ => p.stmt(this).getOrElse(st"return")
      }
    }

  }

  @datatype trait Jump {
    @strictpure def prettyST(p: Printer): ST = p.jump(this) match {
      case Some(r) => r
      case _ => prettyRawST(p)
    }
    @pure def prettyRawST(p: Printer): ST
    @pure def targets: ISZ[Z]
    @pure def pos: Position
  }

  object Jump {

    @datatype class Goto(val label: Z, val pos: Position) extends Jump {
      @strictpure def prettyRawST(p: Printer): ST = st"goto .$label"
      @strictpure def targets: ISZ[Z] = ISZ(label)
    }

    @datatype class If(val cond: Exp, thenLabel: Z, elseLabel: Z, val pos: Position) extends Jump {
      @strictpure def prettyRawST(p: Printer): ST = st"if ${cond.prettyST(p)} goto .$thenLabel else goto .$elseLabel"
      @strictpure def targets: ISZ[Z] = ISZ(thenLabel, elseLabel)
    }

    @datatype class Return(val expOpt: Option[Exp], val pos: Position) extends Jump {
      @strictpure def prettyRawST(p: Printer): ST = expOpt match {
        case Some(exp) => st"return ${exp.prettyST(p)}"
        case _ => st"return"
      }
      @strictpure def targets: ISZ[Z] = ISZ()
    }

    @datatype class Switch(val exp: Exp, val cases: ISZ[Switch.Case], val defaultLabelOpt: Option[Z], val pos: Position) extends Jump {
      @strictpure def prettyRawST(p: Printer): ST = {
        val defaultOpt: Option[ST] = defaultLabelOpt match {
          case Some(l) => Some(st"default: goto $l")
          case _ => None()
        }
        st"""switch (${exp.prettyST(p)})
            |  ${(for (c <- cases) yield st"${c.value.prettyST(p)}: goto ${c.label}", "\n")}
            |  $defaultOpt"""
      }

      @pure def targets: ISZ[Z] = {
        var r: ISZ[Z] = for (c <- cases) yield c.label
        defaultLabelOpt match {
          case Some(l) => r = r :+ l
          case _ =>
        }
        return r
      }
    }

    object Switch {
      @datatype class Case(val value: Exp, val label: Z)
    }

    @datatype class Halt(val pos: Position) extends Jump {
      @strictpure def prettyRawST(p: Printer): ST = st"halt"
      @strictpure def targets: ISZ[Z] = ISZ()
    }

    @datatype class Intrinsic(val intrinsic: Intrinsic.Type) extends Jump {
      @strictpure def prettyRawST(p: Printer): ST = intrinsic.prettyST(p)
      @strictpure def pos: Position = intrinsic.pos
      @strictpure def targets: ISZ[Z] = intrinsic.targets
    }

    object Intrinsic {
      @sig trait Type {
        @pure def pos: Position
        @pure def prettyST(p: Printer): ST
        @pure def targets: ISZ[Z]
      }
    }
  }

  @datatype class ExpBlock(val stmts: ISZ[Stmt], val exp: Exp) {
    @strictpure def prettyST(p: Printer): ST = if (stmts.isEmpty) exp.prettyST(p) else
      st"""{
          |  ${(for (stmt <- stmts) yield stmt.prettyST(p), "\n")}
          |  ${exp.prettyST(p)}
          |}"""
  }

  @datatype class BasicBlock(val label: Z, val grounds: ISZ[Stmt.Ground], jump: Jump) {
    @pure def prettyST(p: Printer): ST = {
      var line: Z = 0
      var sts = ISZ[ST]()
      def updatePos(pos: Position): Unit = {
        if (line != pos.beginLine) {
          line = pos.beginLine
          val uriOps = ops.StringOps(pos.uriOpt.get)
          sts = sts :+ st"// ${uriOps.substring(uriOps.lastIndexOf('/') + 1, uriOps.size)}:$line"
        }
      }
      for (g <- grounds) {
        updatePos(g.pos)
        sts = sts :+ g.prettyST(p)
      }
      if (grounds.isEmpty) {
        updatePos(jump.pos)
      }
      sts = sts :+ jump.prettyST(p)
      val r =
        st""".$label:
            |  ${(sts, "\n")}"""
      return r
    }
  }

  @datatype trait Body {
    @pure def prettyST(p: Printer): ST
  }

  object Body {

    @datatype class Block(val block: Stmt.Block) extends Body {
      @strictpure def prettyST(p: Printer): ST = block.prettyST(p)
    }

    @datatype class Basic(val blocks: ISZ[BasicBlock]) extends Body {
      @strictpure def prettyST(p: Printer): ST =
        st"""{
            |  ${(for (bb <- blocks) yield bb.prettyST(p), "\n\n")}
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
    @memoize def context: MethodContext = {
      return MethodContext(isInObject, owner, id, tipe)
    }
    @strictpure def prettyST(p: Printer): ST = {
      val pt: ST = if (typeParams.isEmpty) st"" else st"[${(typeParams, ", ")}]"
      val ownerOpt: Option[ST] = if (owner.isEmpty)  None() else  Some(st"${(owner, ".")}${if (isInObject) "." else "#"}")
      st"procedure $ownerOpt$id$pt(${(for (p <- ops.ISZOps(paramNames).zip(tipe.args)) yield st"${p._1}: ${p._2}", ", ")}): ${tipe.ret} ${body.prettyST(p)}"
    }
  }

  @datatype class Global(val tipe: Typed, val name: ISZ[String], val pos: Position) {
    @strictpure def prettyST: ST = st"global ${(name, ".")}: $tipe"
  }

  @datatype class Program(val threeAddressCode: B,
                          val globals: ISZ[Global],
                          val procedures: ISZ[Procedure]) {
    @strictpure def prettyST(printer: Printer): ST =
      st"""${(for (g <- globals) yield g.prettyST, "\n")}
          |
          |${(for (p <- procedures) yield p.prettyST(printer), "\n\n")}"""
    @pure override def string: String = {
      return prettyST(Printer.Empty()).render
    }
  }

  @sig trait Printer {
    @pure def exp(e: Exp): Option[ST]
    @pure def stmt(stmt: Stmt): Option[ST]
    @pure def jump(j: Jump): Option[ST]
  }

  object Printer {
    @datatype class Empty extends Printer {
      @strictpure def exp(e: Exp): Option[ST] = None()
      @strictpure def stmt(stmt: Stmt): Option[ST] = None()
      @strictpure def jump(j: Jump): Option[ST] = None()
    }
  }

  @strictpure def max(n: Z, m: Z): Z = if (n < m) m else n
  @strictpure def ite(cond: Exp, tExp: Exp, fExp: Exp, t: Typed, pos: Position): Exp = (tExp, fExp) match {
    case (Exp.Bool(T, _), Exp.Bool(T, _)) => tExp
    case (Exp.Bool(T, _), Exp.Bool(F, _)) => cond
    case (Exp.Bool(F, _), Exp.Bool(T, _)) => Exp.Unary(cond.tipe, lang.ast.Exp.UnaryOp.Not, cond, pos)
    case (Exp.Bool(F, _), Exp.Bool(F, _)) => tExp
    case (_, _) => Exp.If(cond, tExp, fExp, t, pos)
  }
  @strictpure def condAnd(left: Exp, right: Exp, pos: Position): Exp = {
    val r = ite(left, right, Exp.Bool(F, pos), Typed.b, pos)
    r match {
      case _: Exp.If => Exp.Binary(Typed.b, left, IR.Exp.Binary.Op.CondAnd, right, pos)
      case _ => r
    }
  }

  @strictpure def condOr(left: Exp, right: Exp, pos: Position): Exp = {
    val r = ite(left, Exp.Bool(T, pos), right, Typed.b, pos)
    r match {
      case _: Exp.If => Exp.Binary(Typed.b, left, IR.Exp.Binary.Op.CondOr, right, pos)
      case _ => r
    }
  }

  @strictpure def condImply(left: Exp, right: Exp, pos: Position): Exp = {
    val r = ite(left, right, Exp.Bool(T, pos), Typed.b, pos)
    r match {
      case _: Exp.If => Exp.Binary(Typed.b, left, IR.Exp.Binary.Op.CondImply, right, pos)
      case _ => r
    }
  }

  @pure def bigAnd(conjuncts: ISZ[Exp], pos: Position): Exp = {
    if (conjuncts.isEmpty) {
      return Exp.Bool(T, pos)
    }
    var r = conjuncts(0)
    for (i <- 1 until conjuncts.size) {
      r = Exp.Binary(Typed.b, r, Exp.Binary.Op.And, conjuncts(i), pos)
    }
    return r
  }
}
