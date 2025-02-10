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
    @pure def numOfTemps: Z
  }

  object Exp {

    @datatype class Bool(val value: B, val pos: Position) extends Exp {
      @strictpure def tipe: Typed = Typed.b
      @strictpure def prettyST: ST = if (value) st"true" else st"false"
      @strictpure def numOfTemps: Z = 0
    }

    @datatype class Int(val tipe: Typed, val bitWidth: Z, val value: Z, val pos: Position) extends Exp {
      @strictpure def prettyST: ST = st"$value [$tipe]"
      @strictpure def numOfTemps: Z = 0
    }

    @datatype class F32(val value: org.sireum.F32, val pos: Position) extends Exp {
      @strictpure def tipe: Typed = Typed.f32
      @strictpure def prettyST: ST = st"$value [f32]"
      @strictpure def numOfTemps: Z = 0
    }

    @datatype class F64(val value: org.sireum.F64, val pos: Position) extends Exp {
      @strictpure def tipe: Typed = Typed.f64
      @strictpure def prettyST: ST = st"$value [f64]"
      @strictpure def numOfTemps: Z = 0
    }

    @datatype class R(val value: org.sireum.R, val pos: Position) extends Exp {
      @strictpure def tipe: Typed = Typed.r
      @strictpure def prettyST: ST = st"$value [r]"
      @strictpure def numOfTemps: Z = 0
    }

    @datatype class String(val value: org.sireum.String, val pos: Position) extends Exp {
      @strictpure def tipe: Typed = Typed.string
      @strictpure def prettyST: ST = ops.StringOps(value).escapeST
      @strictpure def numOfTemps: Z = 0
    }

    @datatype class Temp(val n: Z, val tipe: Typed, val pos: Position) extends Exp {
      @strictpure def prettyST: ST = st"$$$n"
      @strictpure def numOfTemps: Z = 1
    }

    @datatype class LocalVarRef(val isVal: B, val context: MethodContext, val id: org.sireum.String, val tipe: Typed, val pos: Position) extends Exp {
      @strictpure def prettyST: ST = st"$id"
      @strictpure def numOfTemps: Z = 0
    }

    @datatype class GlobalVarRef(val name: ISZ[org.sireum.String], val tipe: Typed, val pos: Position) extends Exp {
      @strictpure def prettyST: ST = st"${(name, ".")}"
      @strictpure def numOfTemps: Z = 0
    }

    @datatype class EnumElementRef(val owner: ISZ[org.sireum.String], val id: org.sireum.String, val ordinal: Z, val pos: Position) extends Exp {
      @strictpure def tipe: Typed = Typed.Enum(owner :+ "Type")
      @strictpure def prettyST: ST = st"${(owner, ".")}.$id"
      @strictpure def numOfTemps: Z = 0
    }

    @datatype class FieldVarRef(val owner: Typed, val receiver: Exp, val id: org.sireum.String, val tipe: Typed, val pos: Position) extends Exp {
      @strictpure def prettyST: ST = st"$receiver.$id"
      @strictpure def numOfTemps: Z = receiver.numOfTemps
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
      @strictpure def numOfTemps: Z = exp.numOfTemps
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
      @strictpure def numOfTemps: Z = left.numOfTemps + right.numOfTemps
    }

    @datatype class If(val cond: Exp, val thenExp: Exp, val elseExp: Exp, val tipe: Typed, val pos: Position) extends Exp {
      @strictpure def prettyST: ST = st"if (${cond.prettyST}) ${thenExp.prettyST} else ${elseExp.prettyST}"
      @strictpure def numOfTemps: Z = cond.numOfTemps + thenExp.numOfTemps + elseExp.numOfTemps
    }

    @datatype class Construct(val tipe: Typed, val args: ISZ[Exp], val pos: Position) extends Exp {
      @strictpure def prettyST: ST = st"$tipe(${(for (i <- 1 until args.size) yield args(i).prettyST, ", ")})"
      @pure def numOfTemps: Z = {
        var r: Z = 0
        for (arg <- args) {
          r = r + arg.numOfTemps
        }
        return r
      }
    }

    @datatype class Apply(val isInObject: B, val owner: ISZ[org.sireum.String], val id: org.sireum.String, val args: ISZ[Exp],
                          val methodType: Typed.Fun, val tipe: Typed, val pos: Position) extends Exp {
      @strictpure def prettyST: ST =
        if (!isInObject && ops.StringOps(id).isScalaOp && args.size == 2) st"(${args(0).prettyST} $id ${args(1).prettyST})"
        else if (isInObject) st"${if (owner.nonEmpty) st"${(owner, ".")}." else st""}$id(${(for (arg <- args) yield arg.prettyST, ", ")})"
        else st"${args(0).prettyST}.$id(${(for (i <- 1 until args.size) yield args(i).prettyST)})"
      @pure def numOfTemps: Z = {
        var r: Z = 0
        for (arg <- args) {
          r = r + arg.numOfTemps
        }
        return r
      }
    }

    @datatype class Select(val exp: Exp, val id: String, val tipe: Typed, val pos: Position) extends Exp {
      @strictpure def prettyST: ST = st"${exp.prettyST}.$id"
      @strictpure def numOfTemps: Z = exp.numOfTemps
    }

    @datatype class Indexing(val exp: Exp, val tipe: Typed, val index: Exp, val pos: Position) extends Exp {
      @strictpure def prettyST: ST = st"${exp.prettyST}(${index.prettyST})"
      @strictpure def numOfTemps: Z = exp.numOfTemps + index.numOfTemps
    }

    @datatype class Type(val test: B, val exp: Exp, val t: Typed, val pos: Position) extends Exp {
      @strictpure def tipe: Typed = if (test) Typed.b else t
      @strictpure def prettyST: ST = st"(${exp.prettyST} ${if (test) "is" else "as"} $t)"
      @strictpure def numOfTemps: Z = exp.numOfTemps
    }
  }

  @datatype trait Stmt {
    @strictpure def pos: Position
    @pure def prettyST: ST
    @pure override def string: String = {
      return prettyST.render
    }
    @pure def computeLocalsTemps(locals: Z, temps: Z): (Z, Z)
  }

  object Stmt {

    @datatype trait Ground extends Stmt

    @datatype class Expr(val exp: Exp.Apply) extends Ground {
      @strictpure def pos: Position = exp.pos
      @strictpure def prettyST: ST = exp.prettyST
      @strictpure def computeLocalsTemps(locals: Z, temps: Z): (Z, Z) = (locals, temps - exp.numOfTemps)
    }

    @datatype trait Assign extends Ground {
      @strictpure def rhs: Exp
    }

    object Assign {

      @datatype class Local(val copy: B, val context: MethodContext, val lhs: String, val rhs: Exp, val pos: Position) extends Assign {
        @strictpure def prettyST: ST = st"$lhs ${if (copy) ":=" else "="} ${rhs.prettyST}"
        @strictpure def computeLocalsTemps(locals: Z, temps: Z): (Z, Z) = (locals, temps - rhs.numOfTemps)
      }

      @datatype class Global(val copy: B, val name: ISZ[String], val rhs: Exp, val pos: Position) extends Assign {
        @strictpure def prettyST: ST = st"${(name, ".")} ${if (copy) ":=" else "="} ${rhs.prettyST}"
        @strictpure def computeLocalsTemps(locals: Z, temps: Z): (Z, Z) = (locals, temps - rhs.numOfTemps)
      }

      @datatype class Temp(val lhs: Z, val rhs: Exp, val pos: Position) extends Assign {
        @strictpure def prettyST: ST = st"$$$lhs = ${rhs.prettyST}"
        @strictpure def computeLocalsTemps(locals: Z, temps: Z): (Z, Z) = (locals, temps - rhs.numOfTemps + 1)
      }

      @datatype class Field(val copy: B, val receiver: Exp, val receiverType: Typed.Name, val id: String, val rhs: Exp, val pos: Position) extends Assign {
        @strictpure def prettyST: ST = st"${receiver.prettyST}.$id ${if (copy) ":=" else "="} ${rhs.prettyST}"
        @strictpure def computeLocalsTemps(locals: Z, temps: Z): (Z, Z) = (locals, temps - receiver.numOfTemps - rhs.numOfTemps)
      }

      @datatype class Index(val copy: B, val receiver: Exp, val receiverType: Typed.Name, val index: Exp, val rhs: Exp, val pos: Position) extends Assign {
        @strictpure def prettyST: ST = st"${receiver.prettyST}(${index.prettyST}) ${if (copy) ":=" else "="} ${rhs.prettyST}"
        @strictpure def computeLocalsTemps(locals: Z, temps: Z): (Z, Z) = (locals, temps - receiver.numOfTemps - index.numOfTemps - rhs.numOfTemps)
      }

    }

    @datatype class If(val cond: Exp, val thenBlock: Block, val elseBlock: Block, val pos: Position) extends Stmt {
      @strictpure def prettyST: ST = st"if (${cond.prettyST}) ${thenBlock.prettyST} else ${elseBlock.prettyST}"
      @strictpure def computeLocalsTemps(locals: Z, temps: Z): (Z, Z) = halt("This API can only be used for 3-address code")
    }

    @datatype class Block(val stmts: ISZ[Stmt], val pos: Position) extends Stmt {
      @strictpure def prettyST: ST =
        st"""{
            |  ${(for (stmt <- stmts) yield stmt.prettyST, "\n")}
            |}"""
      @strictpure def computeLocalsTemps(locals: Z, temps: Z): (Z, Z) = halt("This API can only be used for 3-address code")
    }

    @datatype class While(val condBlock: Block, val cond: Exp, val block: Block, val pos: Position) extends Stmt {
      @strictpure def prettyST: ST =
        st"""$condBlock
            |while (${cond.prettyST}) ${block.prettyST}"""
      @strictpure def computeLocalsTemps(locals: Z, temps: Z): (Z, Z) = halt("This API can only be used for 3-address code")
    }

    @datatype class Return(val expOpt: Option[Exp], val pos: Position) extends Stmt {
      @strictpure def prettyST: ST = expOpt match {
        case Some(exp) => st"return ${exp.prettyST}"
        case _ => st"return"
      }
      @strictpure def computeLocalsTemps(locals: Z, temps: Z): (Z, Z) = halt("This API can only be used for 3-address code")
    }

    @datatype class Decl(val undecl: B, val isVal: B, val tipe: Typed, val id: String, val pos: Position) extends Ground {
      @strictpure def undeclare: Decl = {
        val thiz = this
        thiz(undecl = T)
      }
      @strictpure def prettyST: ST = st"${if (undecl) "un" else ""}decl $id: $tipe"
      @strictpure def computeLocalsTemps(locals: Z, temps: Z): (Z, Z) = (locals - (if (undecl) 1 else -1), temps)
    }

  }

  @datatype trait Jump {
    @pure def prettyST: ST
    @pure override def string: String = {
      return prettyST.render
    }
    @pure def computeLocalsTemps(locals: Z, temps: Z): (Z, Z)
    @pure def targets: ISZ[Z]
  }

  object Jump {

    @datatype class Goto(val label: Z, val pos: Position) extends Jump {
      @strictpure def prettyST: ST = st"goto .$label"
      @strictpure def computeLocalsTemps(locals: Z, temps: Z): (Z, Z) = (locals, temps)
      @strictpure def targets: ISZ[Z] = ISZ(label)
    }

    @datatype class GotoLocal(val context: MethodContext, val id: String, val pos: Position) extends Jump {
      @strictpure def prettyST: ST = st"goto $id"
      @strictpure def computeLocalsTemps(locals: Z, temps: Z): (Z, Z) = (locals, temps)
      @strictpure def targets: ISZ[Z] = ISZ()
    }

    @datatype class If(val cond: Exp, thenLabel: Z, elseLabel: Z, val pos: Position) extends Jump {
      @strictpure def prettyST: ST = st"if ${cond.prettyST} goto .$thenLabel else goto .$elseLabel"
      @strictpure def computeLocalsTemps(locals: Z, temps: Z): (Z, Z) = (locals, temps - cond.numOfTemps)
      @strictpure def targets: ISZ[Z] = ISZ(thenLabel, elseLabel)
    }

    @datatype class Return(val expOpt: Option[Exp], val pos: Position) extends Jump {
      @strictpure def prettyST: ST = expOpt match {
        case Some(exp) => st"return ${exp.prettyST}"
        case _ => st"return"
      }
      @strictpure def computeLocalsTemps(locals: Z, temps: Z): (Z, Z) = expOpt match {
        case Some(exp) => (locals, temps - exp.numOfTemps)
        case _ => (locals, temps)
      }
      @strictpure def targets: ISZ[Z] = ISZ()
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
    @memoize def context: MethodContext = {
      return MethodContext(isInObject, owner, id, tipe)
    }
    @strictpure def prettyST: ST = {
      val pt: ST = if (typeParams.isEmpty) st"" else st"[${(typeParams, ", ")}]"
      val ownerOpt: Option[ST] = if (owner.isEmpty)  None() else  Some(st"${(owner, ".")}${if (isInObject) "." else "#"}")
      val maxLocalsAndTempsOpt: Option[ST] = {
        val (l, t) = maxLocalsAndTemps
        if (l == 0 && t == 0) None() else Some(st"/* maxLocals = $l , maxTemps = $t */ ")
      }
      st"procedure $ownerOpt$id$pt(${(for (p <- ops.ISZOps(paramNames).zip(tipe.args)) yield st"${p._1}: ${p._2}", ", ")}): ${tipe.ret} $maxLocalsAndTempsOpt${body.prettyST}"
    }
    @pure override def string: String = {
      return prettyST.render
    }
    @memoize def maxLocalsAndTemps: (Z, Z) = {
      val blocks: ISZ[BasicBlock] = body match {
        case body: Body.Basic if body.blocks.nonEmpty => body.blocks
        case _ => return (0, 0)
      }
      val blockMap: HashMap[Z, BasicBlock] = HashMap ++ (for (b <- blocks) yield (b.label, b))
      var m = HashMap.empty[Z, (Z, Z)] + blocks(0).label ~> (paramNames.size, 0)
      var nextBlocks = ISZ(blocks(0).label)
      var maxLocals: Z = 0
      var maxTemps: Z = 0
      while (nextBlocks.nonEmpty) {
        var ns = ISZ[Z]()
        for (n <- nextBlocks) {
          val b = blockMap.get(n).get
          var (locals, temps) = m.get(n).get
          def updateLocalsTemps(p: (Z, Z)): Unit = {
            val (l, t) = p
            if (maxLocals < l) {
              maxLocals = l
            }
            if (maxTemps < t) {
              maxTemps = t
            }
            locals = l
            temps = t
          }
          for (g <- b.grounds) {
            updateLocalsTemps(g.computeLocalsTemps(locals, temps))
          }
          updateLocalsTemps(b.jump.computeLocalsTemps(locals, temps))
          b.jump match {
            case _: IR.Jump.Return => assert(temps == 0, s"${b.label}: end temps = $temps")
            case _: IR.Jump.GotoLocal => assert(temps == 0, s"${b.label}: end temps = $temps")
            case _ =>
          }
          for (target <- b.jump.targets) {
            m.get(target) match {
              case Some((l, t)) => assert(locals == l && temps == t, s"$target: locals = $locals, l = $l, temps = $temps, t = $t, $m")
              case _ =>
                m = m + target ~> (locals, temps)
                ns = ns :+ target
            }
          }
        }
        nextBlocks = ns
      }
      return (maxLocals, maxTemps)
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
      st"""// maxLocals = ${maxLocalsTemps._1}, maxTemps = ${maxLocalsTemps._2}
          |
          |${(for (g <- globals) yield g.prettyST, "\n")}
          |
          |${(for (p <- procedures) yield p.prettyST, "\n\n")}"""
    @pure override def string: String = {
      return prettyST.render
    }
    @memoize def maxLocalsTemps: (Z, Z) = {
      var locals: Z = 0
      var temps: Z = 0
      for (p <- procedures) {
        val (l, t) = p.maxLocalsAndTemps
        if (locals < l) {
          locals = l
        }
        if (temps < t) {
          temps = t
        }
      }
      return (locals, temps)
    }
  }
}
