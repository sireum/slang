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

package org.sireum.lang

import org.sireum._
import org.sireum.lang.ast.IR
import org.sireum.lang.symbol.TypeInfo
import org.sireum.lang.tipe.TypeHierarchy
import org.sireum.lang.{ast => AST}

object IRTranslator {
  @msig trait Fresh {
    def setLabel(n: Z): Unit
    def setTemp(n: Z): Unit
    def label(): Z
    def temp(): Z
  }

  @ext("IRTranslatorFreshAtomic") object Ext {
    @pure def createFresh: Fresh = $
  }

  @strictpure def createFresh: Fresh = Ext.createFresh
}

@record class IRTranslator(val threeAddressCode: B, val th: TypeHierarchy, val fresh: IRTranslator.Fresh) {

  var methodContext: IR.MethodContext = IR.MethodContext.empty
  var stmts: ISZ[IR.Stmt] = {
    fresh.setTemp(0)
    fresh.setLabel(1)
    ISZ()
  }

  def translateMethodH(isBasic: B,
                       receiverTypeOpt: Option[AST.Typed],
                       owner: ISZ[String],
                       id: String,
                       typeParams: ISZ[String],
                       params: ISZ[String],
                       funType: AST.Typed.Fun,
                       pos: message.Position,
                       bodyOpt: Option[AST.Body]): IR.Procedure = {
    val isInObject = receiverTypeOpt.isEmpty
    var t: AST.Typed.Fun = funType
    var paramNames = params
    if (!isInObject) {
      paramNames = "this" +: paramNames
      t = t(args = receiverTypeOpt.get +: t.args)
    }
    methodContext = IR.MethodContext(isInObject, owner, id, t)
    var body: IR.Body = bodyOpt match {
      case Some(body) =>
        val oldStmts = stmts
        stmts = ISZ()
        translateBody(body, None())
        val b = IR.Body.Block(IR.Stmt.Block(stmts, bodyPos(body, pos)))
        stmts = oldStmts
        b
      case _ => IR.Body.Block(IR.Stmt.Block(ISZ(), pos))
    }
    if (isBasic) {
      body = toBasic(body.asInstanceOf[IR.Body.Block], pos)
    }
    return IR.Procedure(isInObject, typeParams, owner, id, paramNames, t, body, pos)
  }

  def translateMethod(isBasic: B,
                      receiverTypeOpt: Option[AST.Typed],
                      owner: ISZ[String],
                      method: AST.Stmt.Method): IR.Procedure = {
    val typeParams: ISZ[String] = for (tp <- method.sig.typeParams) yield tp.id.value
    val paramNames: ISZ[String] = for (p <- method.sig.params) yield p.id.value
    return translateMethodH(isBasic, receiverTypeOpt, owner, method.sig.id.value,
      typeParams, paramNames, method.sig.funType, method.sig.id.attr.posOpt.get, method.bodyOpt)
  }

  def toBasic(body: IR.Body.Block, pos: message.Position): IR.Body.Basic = {

    var blocks = ISZ[IR.BasicBlock]()
    var grounds = ISZ[IR.Stmt.Ground]()
    var decls = ISZ[IR.Stmt.Decl]()

    def addGround(g: IR.Stmt.Ground): Unit = {
      grounds = grounds :+ g
    }

    val initLabel = fresh.label()

    @pure def basicBlock(label: Z, stmts: ISZ[IR.Stmt.Ground], jump: IR.Jump): IR.BasicBlock = {
      return IR.BasicBlock(label, stmts, jump)
    }

    def stmtToBasic(label: Z, stmt: IR.Stmt): Option[Z] = {
      stmt match {
        case stmt: IR.Stmt.Block =>
          return blockToBasic(label, stmt)
        case stmt: IR.Stmt.Expr =>
          addGround(stmt)
          return Some(label)
        case stmt: IR.Stmt.Assign =>
          addGround(stmt)
          return Some(label)
        case stmt: IR.Stmt.Return =>
          blocks = blocks :+ basicBlock(label, grounds, IR.Jump.Return(stmt.expOpt, pos))
          grounds = ISZ()
          return None()
        case stmt: IR.Stmt.If =>
          val t = fresh.label()
          val e = fresh.label()
          val f: Z = if (stmt.elseBlock.stmts.isEmpty) e else fresh.label()
          blocks = blocks :+ basicBlock(label, grounds, IR.Jump.If(stmt.cond, t, f, stmt.pos))
          grounds = ISZ()
          var allReturn = T
          blockToBasic(t, stmt.thenBlock) match {
            case Some(l) =>
              blocks = blocks :+ basicBlock(l, grounds, IR.Jump.Goto(e, stmt.pos))
              allReturn = F
            case _ =>
          }
          if (stmt.elseBlock.stmts.nonEmpty) {
            grounds = ISZ()
            blockToBasic(f, stmt.elseBlock) match {
              case Some(l) =>
                blocks = blocks :+ basicBlock(l, grounds, IR.Jump.Goto(e, stmt.pos))
                allReturn = F
              case _ =>
            }
          } else {
            allReturn = F
          }
          grounds = ISZ()
          return if (allReturn) None() else Some(e)
        case stmt: IR.Stmt.While =>
          val n = fresh.label()
          blocks = blocks :+ basicBlock(label, grounds, IR.Jump.Goto(n, stmt.pos))
          grounds = ISZ()
          blockToBasic(n, stmt.condBlock) match {
            case Some(l) =>
              val t = fresh.label()
              val e = fresh.label()
              blocks = blocks :+ basicBlock(l, grounds, IR.Jump.If(stmt.cond, t, e, stmt.pos))
              grounds = ISZ()
              blockToBasic(t, stmt.block) match {
                case Some(l) => blocks = blocks :+ basicBlock(l, grounds, IR.Jump.Goto(n, stmt.pos))
                case _ =>
              }
              grounds = ISZ()
              return Some(e)
            case _ =>
              return None()
          }
        case stmt: IR.Stmt.Decl =>
          addGround(stmt)
          decls = decls :+ stmt
          return Some(label)
        case stmt: IR.Stmt.Intrinsic =>
          addGround(stmt)
          return Some(label)
      }
    }

    def blockToBasic(label: Z, block: IR.Stmt.Block): Option[Z] = {
      val oldDecls = decls
      decls = ISZ()
      var l = label
      for (stmt <- block.stmts) {
        var shouldSplit = F
        if (threeAddressCode) {
          stmt match {
            case _: IR.Stmt.Expr => shouldSplit = T
            case stmt: IR.Stmt.Assign if stmt.rhs.isInstanceOf[IR.Exp.Apply] => shouldSplit = T
            case _ =>
          }
          if (shouldSplit) {
            val next = fresh.label()
            blocks = blocks :+ IR.BasicBlock(l, grounds, IR.Jump.Goto(next, stmt.pos))
            grounds = ISZ()
            l = next
          }
        }
        stmtToBasic(l, stmt) match {
          case Some(next) => l = next
          case _ => return None()
        }
        if (shouldSplit) {
          val next = fresh.label()
          blocks = blocks :+ IR.BasicBlock(l, grounds, IR.Jump.Goto(next, stmt.pos))
          grounds = ISZ()
          l = next
        }
      }
      for (d <- decls) {
        addGround(d.undeclare)
      }
      decls = oldDecls
      return Some(l)
    }

    blockToBasic(initLabel, body.block) match {
      case Some(l) => blocks = blocks :+ basicBlock(l, grounds, IR.Jump.Return(None(), pos))
      case _ =>
    }
    return IR.Body.Basic(blocks)
  }

  def translateStmt(stmt: AST.Stmt, registerOpt: Option[Z]): Unit = {
    val pos = stmt.posOpt.get
    stmt match {
      case stmt: AST.Stmt.Var =>
        val init = stmt.initOpt.get
        var oldStmts = stmts
        stmts = ISZ()
        val t = stmt.attr.typedOpt.get
        val varRhs: IR.Exp = init match {
          case init: AST.Stmt.Expr =>
            translateExp(init.exp)
          case _ =>
            val n = fresh.temp()
            translateAssignExp(init, n)
            IR.Exp.Temp(n, t, init.asStmt.posOpt.get)
        }
        stmts = stmts :+ IR.Stmt.Assign.Local(shouldCopy(t), methodContext, stmt.id.value, t, varRhs, pos)
        oldStmts = oldStmts :+ IR.Stmt.Decl(F, stmt.isVal, methodContext, ISZ(IR.Stmt.Decl.Local(stmt.id.value, t)), pos)
        stmts = oldStmts ++ stmts
      case stmt: AST.Stmt.Assign =>
        val copy = shouldCopy(stmt.rhs.typedOpt.get)
        val oldStmts = stmts
        stmts = ISZ()
        def assignRhs(): IR.Exp = {
          stmt.rhs match {
            case rhs: AST.Stmt.Expr => return translateExp(rhs.exp)
            case _ =>
              val n = fresh.temp()
              val t = stmt.rhs.typedOpt.get
              translateAssignExp(stmt.rhs, n)
              return IR.Exp.Temp(n, t, stmt.rhs.asStmt.posOpt.get)
          }
        }
        stmt.lhs match {
          case lhs: AST.Exp.Ident =>
            lhs.resOpt.get match {
              case _: AST.ResolvedInfo.LocalVar =>
                val rhs = assignRhs()
                stmts = stmts :+ IR.Stmt.Assign.Local(copy, methodContext, lhs.id.value, lhs.typedOpt.get, rhs, pos)
              case res: AST.ResolvedInfo.Var =>
                if (res.isInObject) {
                  val rhs = assignRhs()
                  stmts = stmts :+ IR.Stmt.Assign.Global(copy, res.owner :+ res.id, lhs.typedOpt.get, rhs, pos)
                } else {
                  val receiverPos = lhs.posOpt.get
                  val thiz = IR.Exp.LocalVarRef(T, methodContext, "this", methodContext.receiverType, receiverPos)
                  val (receiver, receiverType): (IR.Exp, AST.Typed.Name) = if (threeAddressCode) {
                    val n = fresh.temp()
                    stmts = stmts :+ IR.Stmt.Assign.Temp(n, thiz, receiverPos)
                    (IR.Exp.Temp(n, methodContext.receiverType, receiverPos), methodContext.receiverType.asInstanceOf[AST.Typed.Name])
                  } else {
                    (thiz, methodContext.receiverType.asInstanceOf[AST.Typed.Name])
                  }
                  val rhs = assignRhs()
                  stmts = stmts :+ IR.Stmt.Assign.Field(copy, receiver, receiverType, lhs.id.value, lhs.typedOpt.get, rhs, pos)
                }
              case res => halt(s"Infeasible: $res")
            }
          case lhs: AST.Exp.Select =>
            def selectRhs(): IR.Exp = {
              stmt.rhs match {
                case rhs: AST.Stmt.Expr => return translateExp(rhs.exp)
                case _ =>
                  val n = fresh.temp()
                  val t = stmt.rhs.typedOpt.get
                  val rhsPos = stmt.rhs.asStmt.posOpt.get
                  translateAssignExp(stmt.rhs, n)
                  return IR.Exp.Temp(n, t, rhsPos)
              }
            }

            lhs.resOpt.get match {
              case res: AST.ResolvedInfo.Var if res.isInObject =>
                stmts = stmts :+ IR.Stmt.Assign.Global(copy, res.owner :+ res.id, lhs.typedOpt.get, selectRhs(), pos)
              case _ =>
                val rcv = lhs.receiverOpt.get
                val receiver = translateExp(rcv)
                stmts = stmts :+ IR.Stmt.Assign.Field(copy, receiver, rcv.typedOpt.get.asInstanceOf[AST.Typed.Name],
                  lhs.id.value, lhs.typedOpt.get, selectRhs(), pos)
            }
          case lhs: AST.Exp.Invoke =>
            val rcv = lhs.receiverOpt.get
            val receiverType = rcv.typedOpt.get.asInstanceOf[AST.Typed.Name]
            val receiver = translateExp(rcv)
            val index = translateExp(lhs.args(0))
            val invokeRhs: IR.Exp = stmt.rhs match {
              case rhs: AST.Stmt.Expr => translateExp(rhs.exp)
              case _ =>
                val n = fresh.temp()
                val rhsPos = stmt.rhs.asStmt.posOpt.get
                val t = stmt.rhs.typedOpt.get
                translateAssignExp(stmt.rhs, n)
                IR.Exp.Temp(n, t, rhsPos)
            }
            stmts = stmts :+ IR.Stmt.Assign.Index(copy, receiver, receiverType, index, invokeRhs, pos)
          case _ => halt("Infeasible")
        }
        stmts = oldStmts ++ stmts
      case stmt: AST.Stmt.If =>
        val oldStmts = stmts
        stmts = ISZ()
        val cond = translateExp(stmt.cond)
        val condStmts = stmts
        stmts = ISZ()
        translateBody(stmt.thenBody, registerOpt)
        val thenPos = bodyPos(stmt.thenBody, pos)
        val thenStmts = stmts
        stmts = ISZ()
        translateBody(stmt.elseBody, registerOpt)
        val elsePos = bodyPos(stmt.elseBody, pos)
        val elseStmts = stmts
        stmts = oldStmts ++ condStmts :+
          IR.Stmt.If(cond, IR.Stmt.Block(thenStmts, thenPos), IR.Stmt.Block(elseStmts, elsePos), pos)
      case stmt: AST.Stmt.While =>
        val oldStmts = stmts
        stmts = ISZ()
        val cond = translateExp(stmt.cond)
        val condStmts = stmts
        stmts = ISZ()
        translateBody(stmt.body, None())
        val bPos = bodyPos(stmt.body, pos)
        stmts = oldStmts :+ IR.Stmt.While(IR.Stmt.Block(condStmts, cond.pos), cond, IR.Stmt.Block(stmts, bPos), pos)
      case stmt: AST.Stmt.Expr =>
        val e = translateExp(stmt.exp)
        if (e.tipe == AST.Typed.unit || e.tipe == AST.Typed.nothing) {
          stmts = stmts :+ IR.Stmt.Expr(e.asInstanceOf[IR.Exp.Apply])
        }
      case stmt: AST.Stmt.Return =>
        stmt.expOpt match {
          case Some(exp) =>
            val r = translateExp(exp)
            stmts = stmts :+ IR.Stmt.Return(Some(r), pos)
          case _ =>
            stmts = stmts :+ IR.Stmt.Return(None(), pos)
        }
      case stmt: AST.Stmt.Block => translateBody(stmt.body, registerOpt)
      case stmt: AST.Stmt.Match => halt(s"TODO: $stmt")
      case stmt: AST.Stmt.For => halt(s"TODO: $stmt")
      case stmt: AST.Stmt.VarPattern => halt(s"TODO: $stmt")
      case _: AST.Stmt.SubZ => // skip
      case _: AST.Stmt.Method => // skip
      case _: AST.Stmt.ExtMethod => // skip
      case _: AST.Stmt.Enum => // skip
      case _: AST.Stmt.Sig => // skip
      case _: AST.Stmt.Adt => // skip
      case _: AST.Stmt.Object => // skip
      case _: AST.Stmt.Import => // skip
      case _: AST.Stmt.TypeAlias => // skip
      case _: AST.Stmt.Spec => // skip
    }

  }

  @pure def bodyPos(body: AST.Body, default: message.Position): message.Position = {
    if (body.stmts.isEmpty) {
      return default
    }
    return body.stmts(0).posOpt.get.to(body.stmts(body.stmts.size - 1).posOpt.get)
  }

  def translateBody(body: AST.Body, registerOpt: Option[Z]): Unit = {
    for (stmt <- body.stmts) {
      translateStmt(stmt, registerOpt)
    }
  }

  def translateAssignExp(stmt: AST.AssignExp, register: Z): Unit = {
    val pos = stmt.asStmt.posOpt.get
    stmt match {
      case stmt: AST.Stmt.Expr =>
        val exp = translateExp(stmt.exp)
        stmts = stmts :+ IR.Stmt.Assign.Temp(register, exp, pos)
      case _ => translateStmt(stmt.asStmt, Some(register))
    }
  }

  @memoize def isSubZ(t: AST.Typed): B = {
    t match {
      case t: AST.Typed.Name if t.args.isEmpty =>
        th.typeMap.get(t.ids) match {
          case Some(_: TypeInfo.SubZ) => return T
          case _ =>
        }
      case _ =>
    }
    return F
  }

  @memoize def isScalar(t: AST.Typed): B = {
    t match {
      case AST.Typed.b =>
      case AST.Typed.c =>
      case AST.Typed.z =>
      case AST.Typed.f32 =>
      case AST.Typed.f64 =>
      case AST.Typed.r =>
      case _ => return isSubZ(t)
    }
    return T
  }

  @memoize def isSeq(t: AST.Typed): B = {
    t match {
      case t: AST.Typed.Name => return t.ids == AST.Typed.isName || t.ids == AST.Typed.msName
      case _ =>
    }
    return F
  }

  @memoize def shouldCopy(t: AST.Typed): B = {
    if (isScalar(t)) {
      return F
    }
    t match {
      case t: AST.Typed.Name =>
        t.ids match {
          case AST.Typed.msName => return T
          case AST.Typed.isName =>
          case _ =>
        }
        th.typeMap.get(t.ids) match {
          case Some(info: TypeInfo.Adt) => return !info.ast.isDatatype
          case Some(info: TypeInfo.Sig) => return !info.ast.isImmutable
          case _ =>
        }
      case _ =>
    }
    return F
  }

  def translateExp(exp: AST.Exp): IR.Exp = {
    def norm3AC(e: IR.Exp): IR.Exp = {
      if (threeAddressCode && e.tipe != AST.Typed.unit && e.tipe != AST.Typed.nothing) {
        val n = fresh.temp()
        stmts = stmts :+ IR.Stmt.Assign.Temp(n, e, e.pos)
        return IR.Exp.Temp(n, e.tipe, e.pos)
      } else {
        return e
      }
    }

    def thiz(pos: message.Position): IR.Exp = {
      return norm3AC(IR.Exp.LocalVarRef(T, methodContext, "this", methodContext.receiverType, pos))
    }

    val pos = exp.posOpt.get
    exp match {
      case exp: AST.Exp.LitB => return norm3AC(IR.Exp.Bool(exp.value, pos))
      case exp: AST.Exp.LitC => return norm3AC(IR.Exp.Int(AST.Typed.c, exp.value.toZ, pos))
      case exp: AST.Exp.LitZ => return norm3AC(IR.Exp.Int(AST.Typed.z, exp.value, pos))
      case exp: AST.Exp.LitF32 => return norm3AC(IR.Exp.F32(exp.value, pos))
      case exp: AST.Exp.LitF64 => return norm3AC(IR.Exp.F64(exp.value, pos))
      case exp: AST.Exp.LitR => return norm3AC(IR.Exp.R(exp.value, pos))
      case exp: AST.Exp.LitString => return norm3AC(IR.Exp.String(exp.value, pos))
      case exp: AST.Exp.StringInterpolate =>
        if (isScalar(exp.typedOpt.get)) {
          val t = exp.typedOpt.get.asInstanceOf[AST.Typed.Name]
          val info = th.typeMap.get(t.ids).get.asInstanceOf[TypeInfo.SubZ]
          val value = Z(exp.lits(0).value).get
          return norm3AC(IR.Exp.Int(t, value, pos))
        } else {
          halt(s"TODO: $exp")
        }
      case _: AST.Exp.This => return thiz(pos)
      case exp: AST.Exp.Ident =>
        val t = exp.typedOpt.get
        exp.resOpt.get match {
          case res: AST.ResolvedInfo.LocalVar =>
            return norm3AC(IR.Exp.LocalVarRef(res.isVal, methodContext, res.id, t, pos))
          case res: AST.ResolvedInfo.Var =>
            if (res.isInObject) {
              return norm3AC(IR.Exp.GlobalVarRef(res.owner :+ res.id, t, pos))
            } else {
              return norm3AC(IR.Exp.FieldVarRef(methodContext.receiverType, thiz(pos), res.id, t, pos))
            }
          case res: AST.ResolvedInfo.EnumElement =>
            return norm3AC(IR.Exp.EnumElementRef(res.owner, res.name, res.ordinal, pos))
          case _ => halt(s"Infeasible: $exp")
        }
      case exp: AST.Exp.Select =>
        val t = exp.typedOpt.get
        exp.resOpt.get match {
          case res: AST.ResolvedInfo.Var =>
            if (res.isInObject) {
              return norm3AC(IR.Exp.GlobalVarRef(res.owner :+ res.id, t, pos))
            } else {
              val receiver = exp.receiverOpt.get
              val rcv = translateExp(receiver)
              return norm3AC(IR.Exp.FieldVarRef(receiver.typedOpt.get, rcv, res.id, t, pos))
            }
          case res: AST.ResolvedInfo.EnumElement =>
            return norm3AC(IR.Exp.EnumElementRef(res.owner, res.name, res.ordinal, pos))
          case res: AST.ResolvedInfo.Method =>
            if (isSeq(exp.receiverOpt.get.typedOpt.get)) {
              val receiver = exp.receiverOpt.get
              val rcv = translateExp(receiver)
              return norm3AC(IR.Exp.FieldVarRef(receiver.typedOpt.get, rcv, res.id, res.tpeOpt.get.ret, pos))
            }
            halt(s"TODO: $res")
          case res => halt(s"TODO: $res")
        }
      case exp: AST.Exp.Unary =>
        val t = exp.typedOpt.get
        if (isScalar(t)) {
          val e = translateExp(exp.exp)
          return norm3AC(IR.Exp.Unary(t, exp.op, e, pos))
        } else {
          halt(s"TODO: $exp")
        }
      case exp: AST.Exp.Binary =>
        val t = exp.typedOpt.get
        if (isScalar(t)) {
          val kind: IR.Exp.Binary.Op.Type = exp.attr.resOpt.get.asInstanceOf[AST.ResolvedInfo.BuiltIn].kind match {
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryAdd => IR.Exp.Binary.Op.Add
            case AST.ResolvedInfo.BuiltIn.Kind.BinarySub => IR.Exp.Binary.Op.Sub
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryMul => IR.Exp.Binary.Op.Mul
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryDiv => IR.Exp.Binary.Op.Div
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryRem => IR.Exp.Binary.Op.Rem
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryAnd => IR.Exp.Binary.Op.And
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryOr => IR.Exp.Binary.Op.Or
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryImply => IR.Exp.Binary.Op.Imply
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryXor => IR.Exp.Binary.Op.Xor
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryEq => IR.Exp.Binary.Op.Eq
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryNe => IR.Exp.Binary.Op.Ne
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryEquiv => IR.Exp.Binary.Op.Eq
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryInequiv => IR.Exp.Binary.Op.Ne
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryFpEq => IR.Exp.Binary.Op.FpEq
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryFpNe => IR.Exp.Binary.Op.FpNe
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryGe => IR.Exp.Binary.Op.Ge
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryGt => IR.Exp.Binary.Op.Gt
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryLe => IR.Exp.Binary.Op.Le
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryLt => IR.Exp.Binary.Op.Lt
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryShr => IR.Exp.Binary.Op.Shr
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryUshr => IR.Exp.Binary.Op.Ushr
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryShl => IR.Exp.Binary.Op.Shl
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryCondAnd =>
              if (threeAddressCode) {
                return translateExp(AST.Exp.If(exp.left, exp.right, AST.Exp.LitB(T, AST.Attr(exp.posOpt)),
                  AST.TypedAttr(exp.posOpt, AST.Typed.bOpt)))
              }
              IR.Exp.Binary.Op.CondAnd
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryCondOr =>
              if (threeAddressCode) {
                return translateExp(AST.Exp.If(exp.left, AST.Exp.LitB(T, AST.Attr(exp.posOpt)), exp.right,
                  AST.TypedAttr(exp.posOpt, AST.Typed.bOpt)))
              }
              IR.Exp.Binary.Op.CondOr
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryCondImply =>
              if (threeAddressCode) {
                return translateExp(AST.Exp.If(exp.left, exp.right, AST.Exp.LitB(F, AST.Attr(exp.posOpt)),
                  AST.TypedAttr(exp.posOpt, AST.Typed.bOpt)))
              }
              IR.Exp.Binary.Op.CondImply
            case _ => halt(s"Infeasible: ${exp.attr.resOpt.get}")
          }
          val left = translateExp(exp.left)
          val right = translateExp(exp.right)
          return norm3AC(IR.Exp.Binary(t, left, kind, right, pos))
        }
        if (isSeq(t)) {
          val kindOpt: Option[IR.Exp.Binary.Op.Type] = exp.op match {
            case AST.Exp.BinaryOp.Append => Some(IR.Exp.Binary.Op.Append)
            case AST.Exp.BinaryOp.Prepend => Some(IR.Exp.Binary.Op.Prepend)
            case AST.Exp.BinaryOp.AppendAll => Some(IR.Exp.Binary.Op.AppendAll)
            case _ => None()
          }
          kindOpt match {
            case Some(kind) =>
              val left = translateExp(exp.left)
              val right = translateExp(exp.right)
              return norm3AC(IR.Exp.Binary(t, left, kind, right, pos))
            case _ =>
          }
        }
        halt(s"TODO: $exp")
      case exp: AST.Exp.If =>
        val t = exp.typedOpt.get
        val cond = translateExp(exp.cond)
        if (!threeAddressCode) {
          val thenExp = translateExp(exp.thenExp)
          val elseExp = translateExp(exp.elseExp)
          return IR.Exp.If(cond, thenExp, elseExp, t, pos)
        }
        val n = fresh.temp()
        val oldStmts = stmts
        stmts = ISZ()
        val thenExp = translateExp(exp.thenExp)
        val thenStmts = stmts
        val thenPos = exp.thenExp.posOpt.get
        stmts = ISZ()
        val elseExp = translateExp(exp.elseExp)
        val elseStmts = stmts
        val elsePos = exp.elseExp.posOpt.get
        stmts = oldStmts
        stmts = stmts :+ IR.Stmt.If(cond,
          IR.Stmt.Block(thenStmts :+ IR.Stmt.Assign.Temp(n, thenExp, thenPos), thenPos),
          IR.Stmt.Block(elseStmts :+ IR.Stmt.Assign.Temp(n, elseExp, elsePos), elsePos), pos)
        return IR.Exp.Temp(n, t, pos)
      case exp: AST.Exp.Invoke =>
        exp.attr.resOpt.get match {
          case res: AST.ResolvedInfo.Method =>
            res.mode match {
              case AST.MethodMode.Method =>
                var args = ISZ[IR.Exp]()
                var methodType = res.tpeOpt.get
                exp.receiverOpt match {
                  case Some(receiver) =>
                    args = args :+ translateExp(receiver)
                    methodType = methodType(args = receiver.typedOpt.get +: methodType.args)
                  case _ =>
                }
                for (arg <- exp.args) {
                  args = args :+ translateExp(arg)
                }
                return norm3AC(IR.Exp.Apply(res.isInObject, res.owner, res.id, args, methodType, exp.typedOpt.get, pos))
              case AST.MethodMode.Select =>
                val (rcv, rcvType): (IR.Exp, AST.Typed.Name) = exp.receiverOpt match {
                  case Some(receiver) =>
                    if (exp.ident.id.value == "apply") {
                      (translateExp(receiver), receiver.typedOpt.get.asInstanceOf[AST.Typed.Name])
                    } else {
                      val e = AST.Exp.Select(Some(receiver), exp.ident.id, exp.targs, exp.ident.attr)
                      (translateExp(e), e.typedOpt.get.asInstanceOf[AST.Typed.Name])
                    }
                  case _ => (translateExp(exp.ident), exp.ident.typedOpt.get.asInstanceOf[AST.Typed.Name])
                }
                val index = translateExp(exp.args(0))
                return norm3AC(IR.Exp.Indexing(rcv, rcvType, index, pos))
              case _ => halt(s"TODO: $exp")
            }
          case _ => halt(s"TODO: $exp")
        }
      case exp: AST.Exp.InvokeNamed => halt(s"TODO: $exp")
      case exp: AST.Exp.Tuple => halt(s"TODO: $exp")
      case exp: AST.Exp.ForYield => halt(s"TODO: $exp")
      case exp: AST.Exp.Eta => halt(s"TODO: $exp")
      case exp: AST.Exp.Fun => halt(s"TODO: $exp")
      case exp: AST.Exp.QuantEach => halt(s"TODO: $exp")
      case exp: AST.Exp.QuantRange => halt(s"TODO: $exp")
      case exp: AST.Exp.StrictPureBlock => halt(s"TODO: $exp")
      case exp: AST.Exp.Super => halt(s"TODO: $exp")
      case exp: AST.Exp.Labeled => return translateExp(exp.exp)
      case exp: AST.Exp.QuantType => halt(s"Infeasible: $exp")
      case exp: AST.Exp.AssertAgree => halt(s"Infeasible: $exp")
      case exp: AST.Exp.AssumeAgree => halt(s"Infeasible: $exp")
      case exp: AST.Exp.At => halt(s"Infeasible: $exp")
      case exp: AST.Exp.InfoFlowInvariant => halt(s"Infeasible: $exp")
      case exp: AST.Exp.Input => halt(s"Infeasible: $exp")
      case exp: AST.Exp.LoopIndex => halt(s"Infeasible: $exp")
      case exp: AST.Exp.Old => halt(s"Infeasible: $exp")
      case exp: AST.Exp.RS => halt(s"Infeasible: $exp")
      case exp: AST.Exp.Result => halt(s"Infeasible: $exp")
      case exp: AST.Exp.StateSeq => halt(s"Infeasible: $exp")
      case exp: AST.Exp.Sym => halt(s"Infeasible: $exp")
      case exp: AST.Exp.TypeCond => halt(s"Infeasible: $exp")
      case exp: AST.ProofAst.StepId => halt(s"Infeasible: $exp")
    }
  }
}
