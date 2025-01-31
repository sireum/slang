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
import org.sireum.lang.ast.IR.Stmt
import org.sireum.lang.ast.{IR, MIRTransformer}
import org.sireum.lang.symbol.TypeInfo
import org.sireum.lang.tipe.TypeHierarchy
import org.sireum.lang.{ast => AST}

object IRTranslator {
  @record class BlockDeclPreamble extends MIRTransformer {
    override def postIRStmtBlock(o: Stmt.Block): MOption[IR.Stmt] = {
      var decls = ISZ[IR.Stmt]()
      var nonDecls = ISZ[IR.Stmt]()
      for (stmt <- o.stmts) {
        stmt match {
          case stmt: IR.Stmt.Decl => decls = decls :+ stmt
          case _ => nonDecls = nonDecls :+ stmt
        }
      }
      return if (decls.nonEmpty) MSome(o(stmts = decls ++ nonDecls)) else MNone()
    }
  }
}

@record class IRTranslator(val threeAddressCode: B, val th: TypeHierarchy) {

  var methodContext: IR.MethodContext = IR.MethodContext.empty
  var _freshRegister: Z = 0
  var stmts: ISZ[IR.Stmt] = ISZ()

  def translateMethod(receiverTypeOpt: Option[AST.Typed],
                      owner: ISZ[String],
                      method: AST.Stmt.Method): IR.Procedure = {
    val id = method.sig.id.value
    val isInObject = receiverTypeOpt.isEmpty
    var t: AST.Typed.Fun = method.sig.funType
    var paramNames: ISZ[String] = for (p <- method.sig.params) yield p.id.value
    if (!isInObject) {
      paramNames = "this" +: paramNames
      t = t(args = receiverTypeOpt.get +: t.args)
    }
    methodContext = IR.MethodContext(isInObject, owner, id, t)
    val pos = method.sig.id.attr.posOpt.get
    val typeParams: ISZ[String] = for (tp <- method.sig.typeParams) yield tp.id.value
    val body: IR.Body = method.bodyOpt match {
      case Some(body) =>
        val oldStmts = stmts
        stmts = ISZ()
        translateBody(body, None())
        val b = IR.Body.Block(IR.Stmt.Block(stmts, bodyPos(body, pos)))
        stmts = oldStmts
        b
      case _ => IR.Body.Block(IR.Stmt.Block(ISZ(), pos))
    }
    return IR.Procedure(isInObject, typeParams, owner, id, paramNames, t, body, pos)
  }

  def toBasic(body: IR.Body.Block, pos: message.Position): IR.Body.Basic = {
    var _freshLabel = 1
    def freshLabel(): Z = {
      val r = _freshLabel
      _freshLabel = _freshLabel + 1
      return r
    }

    var blocks = ISZ[IR.BasicBlock]()
    var grounds = ISZ[IR.Stmt.Ground]()
    var decls = ISZ[IR.Stmt.Decl]()

    def stmtToBasic(label: Z, stmt: IR.Stmt): Option[Z] = {
      stmt match {
        case stmt: IR.Stmt.Block =>
          return blockToBasic(label, stmt)
        case stmt: IR.Stmt.Assign =>
          grounds = grounds :+ stmt
          return Some(label)
        case stmt: IR.Stmt.Return =>
          for (d <- decls) {
            grounds = grounds :+ d.undeclare
          }
          blocks = blocks :+ IR.BasicBlock(label, grounds, IR.Jump.Return(stmt.expOpt, stmt.pos))
          grounds = ISZ()
          return None()
        case stmt: IR.Stmt.If =>
          val t = freshLabel()
          val f = freshLabel()
          val e = freshLabel()
          blocks = blocks :+ IR.BasicBlock(label, grounds, IR.Jump.If(stmt.cond, t, f, stmt.pos))
          grounds = ISZ()
          var allReturn = T
          blockToBasic(t, stmt.thenBlock) match {
            case Some(l) =>
              blocks = blocks :+ IR.BasicBlock(l, grounds, IR.Jump.Goto(e, stmt.pos))
              allReturn = F
            case _ =>
          }
          grounds = ISZ()
          blockToBasic(f, stmt.elseBlock) match {
            case Some(l) =>
              blocks = blocks :+ IR.BasicBlock(l, grounds, IR.Jump.Goto(e, stmt.pos))
              allReturn = F
            case _ =>
          }
          grounds = ISZ()
          return if (allReturn) Some(e) else None()
        case stmt: IR.Stmt.While =>
          val n = freshLabel()
          blocks = blocks :+ IR.BasicBlock(label, grounds, IR.Jump.Goto(n, stmt.pos))
          grounds = ISZ()
          blockToBasic(n, stmt.condBlock) match {
            case Some(l) =>
              val t = freshLabel()
              val e = freshLabel()
              blocks = blocks :+ IR.BasicBlock(l, grounds, IR.Jump.If(stmt.cond, t, e, stmt.pos))
              grounds = ISZ()
              blockToBasic(t, stmt.block) match {
                case Some(l) => blocks = blocks :+ IR.BasicBlock(l, grounds, IR.Jump.Goto(n, stmt.pos))
                case _ =>
              }
              grounds = ISZ()
              return Some(e)
            case _ =>
              return None()
          }
        case stmt: IR.Stmt.Decl =>
          grounds = grounds :+ stmt
          decls = decls :+ stmt
          return Some(label)
      }
    }

    def blockToBasic(label: Z, block: IR.Stmt.Block): Option[Z] = {
      val oldDecls = decls
      decls = ISZ()
      var l = label
      for (stmt <- block.stmts) {
        stmtToBasic(l, stmt) match {
          case Some(next) => l = next
          case _ => return None()
        }
      }
      for (d <- decls) {
        grounds = grounds :+ d.undeclare
      }
      decls = oldDecls
      return Some(l)
    }

    blockToBasic(0, body.block) match {
      case Some(l) => blocks = blocks :+ IR.BasicBlock(l, grounds, IR.Jump.Return(None(), pos))
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
            val n = freshRegister()
            translateAssignExp(init, n)
            IR.Exp.Register(n, init.asStmt.posOpt.get)
        }
        stmts = stmts :+ IR.Stmt.Assign.Local(shouldCopy(t), methodContext, stmt.id.value, varRhs, pos)
        oldStmts = oldStmts :+ IR.Stmt.Decl.Local(F, stmt.isVal, t, stmt.id.value, pos)
        stmts = oldStmts :+ IR.Stmt.Block(stmts, pos)
      case stmt: AST.Stmt.Assign =>
        val copy = shouldCopy(stmt.rhs.typedOpt.get)
        val oldStmts = stmts
        stmts = ISZ()
        val identRhs: IR.Exp = stmt.rhs match {
          case rhs: AST.Stmt.Expr => translateExp(rhs.exp)
          case _ =>
            val n = freshRegister()
            translateAssignExp(stmt.rhs, n)
            IR.Exp.Register(n, stmt.rhs.asStmt.posOpt.get)
        }
        stmt.lhs match {
          case lhs: AST.Exp.Ident =>
            lhs.resOpt.get match {
              case _: AST.ResolvedInfo.LocalVar =>
                stmts = stmts :+ IR.Stmt.Assign.Local(copy, methodContext, lhs.id.value, identRhs, pos)
              case res: AST.ResolvedInfo.Var =>
                if (res.isInObject) {
                  stmts = stmts :+ IR.Stmt.Assign.Global(copy, res.owner :+ res.id, identRhs, pos)
                }
                val receiverPos = lhs.posOpt.get
                val thiz = IR.Exp.LocalVarRef(methodContext, "this", receiverPos)
                val (receiver, receiverType): (IR.Exp, AST.Typed.Name) = if (threeAddressCode) {
                  val n = freshRegister()
                  stmts = stmts :+ IR.Stmt.Assign.Register(n, thiz, receiverPos)
                  (IR.Exp.Register(n, receiverPos), methodContext.receiverType.asInstanceOf[AST.Typed.Name])
                } else {
                  (thiz, methodContext.receiverType.asInstanceOf[AST.Typed.Name])
                }
                stmts = stmts :+ IR.Stmt.Assign.Field(copy, receiver, receiverType, lhs.id.value, identRhs, pos)
              case res => halt(s"Infeasible: $res")
            }
          case lhs: AST.Exp.Select =>
            def selectRhs(): IR.Exp = {
              stmt.rhs match {
                case rhs: AST.Stmt.Expr => return translateExp(rhs.exp)
                case _ =>
                  val n = freshRegister()
                  translateAssignExp(stmt.rhs, n)
                  return IR.Exp.Register(n, stmt.rhs.asStmt.posOpt.get)
              }
            }
            lhs.resOpt.get match {
              case res: AST.ResolvedInfo.Var if res.isInObject =>
                stmts = stmts :+ IR.Stmt.Assign.Global(copy, res.owner :+ res.id, selectRhs(), pos)
              case _ =>
                val rcv = lhs.receiverOpt.get
                val receiver = translateExp(rcv)
                stmts = stmts :+ IR.Stmt.Assign.Field(copy, receiver, rcv.typedOpt.get.asInstanceOf[AST.Typed.Name],
                  lhs.id.value, selectRhs(), pos)
            }
          case lhs: AST.Exp.Invoke =>
            val rcv = lhs.receiverOpt.get
            val receiverType = rcv.typedOpt.get.asInstanceOf[AST.Typed.Name]
            val receiver = translateExp(rcv)
            val index = translateExp(lhs.args(0))
            val invokeRhs: IR.Exp = stmt.rhs match {
              case rhs: AST.Stmt.Expr => translateExp(rhs.exp)
              case _ =>
                val n = freshRegister()
                translateAssignExp(stmt.rhs, n)
                IR.Exp.Register(n, stmt.rhs.asStmt.posOpt.get)
            }
            stmts = stmts :+ IR.Stmt.Assign.Index(copy, receiver, receiverType, index, invokeRhs, pos)
          case _ => halt("Infeasible")
        }
        stmts = oldStmts :+ IR.Stmt.Block(stmts, pos)
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
        stmts = oldStmts :+ IR.Stmt.Block(condStmts :+
          IR.Stmt.If(cond, IR.Stmt.Block(thenStmts, thenPos), IR.Stmt.Block(elseStmts, elsePos), pos), pos)
      case stmt: AST.Stmt.While =>
        val oldStmts = stmts
        stmts = ISZ()
        val cond = translateExp(stmt.cond)
        val condStmts = stmts
        stmts = ISZ()
        translateBody(stmt.body, None())
        val bPos = bodyPos(stmt.body, pos)
        stmts = oldStmts :+ IR.Stmt.While(IR.Stmt.Block(condStmts, cond.pos), cond, IR.Stmt.Block(stmts, bPos), pos)
      case stmt: AST.Stmt.Expr => translateExp(stmt.exp)
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
        stmts = stmts :+ IR.Stmt.Assign.Register(register, exp, pos)
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

  @memoize def shouldCopy(t: AST.Typed): B = {
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

  def freshRegister(): Z = {
    val r = _freshRegister
    _freshRegister = _freshRegister + 1
    return r
  }

  def translateExp(exp: AST.Exp): IR.Exp = {
    val pos = exp.posOpt.get
    exp match {
      case exp: AST.Exp.LitB => return IR.Exp.Bool(exp.value, pos)
      case exp: AST.Exp.LitC => return IR.Exp.Int(AST.Typed.c, 32, exp.value.toZ, pos)
      case exp: AST.Exp.LitZ => return IR.Exp.Int(AST.Typed.z, 0, exp.value, pos)
      case exp: AST.Exp.LitF32 => return IR.Exp.F32(exp.value, pos)
      case exp: AST.Exp.LitF64 => return IR.Exp.F64(exp.value, pos)
      case exp: AST.Exp.LitR => return IR.Exp.R(exp.value, pos)
      case exp: AST.Exp.LitString => return IR.Exp.String(exp.value, pos)
      case exp: AST.Exp.StringInterpolate =>
        if (isScalar(exp.typedOpt.get)) {
          val t = exp.typedOpt.get.asInstanceOf[AST.Typed.Name]
          val info = th.typeMap.get(t.ids).get.asInstanceOf[TypeInfo.SubZ]
          val value = Z(exp.lits(0).value).get
          return IR.Exp.Int(t, if (info.ast.isBitVector) info.ast.bitWidth else 0, value, pos)
        } else {
          halt(s"TODO: $exp")
        }
      case exp: AST.Exp.Ident =>
        exp.resOpt.get match {
          case res: AST.ResolvedInfo.LocalVar => return IR.Exp.LocalVarRef(methodContext, res.id, pos)
          case res: AST.ResolvedInfo.Var =>
            if (res.isInObject) {
              return IR.Exp.GlobalVarRef(res.owner :+ res.id, pos)
            } else {
              return IR.Exp.FieldVarRef(methodContext.receiverType, IR.Exp.LocalVarRef(methodContext, "this", pos), res.id, pos)
            }
          case res: AST.ResolvedInfo.EnumElement => return IR.Exp.EnumElementRef(res.owner, res.name, res.ordinal, pos)
          case _ => halt(s"Infeasible: $exp")
        }
      case exp: AST.Exp.Select =>
        exp.resOpt.get match {
          case res: AST.ResolvedInfo.Var =>
            if (res.isInObject) {
              return IR.Exp.GlobalVarRef(res.owner :+ res.id, pos)
            } else {
              val receiver = exp.receiverOpt.get
              return IR.Exp.FieldVarRef(receiver.typedOpt.get, translateExp(receiver), res.id, pos)
            }
          case res: AST.ResolvedInfo.EnumElement => return IR.Exp.EnumElementRef(res.owner, res.name, res.ordinal, pos)
          case _ => halt(s"TODO: $exp")
        }
      case exp: AST.Exp.Unary =>
        if (isScalar(exp.typedOpt.get)) {
          val t = exp.typedOpt.get
          val e = IR.Exp.Unary(t, exp.op, translateExp(exp.exp), pos)
          if (!threeAddressCode) {
            return e
          }
          val n = freshRegister()
          stmts = stmts :+ IR.Stmt.Decl.Register(F, t, n, pos)
          stmts = stmts :+ IR.Stmt.Assign.Register(n, e, pos)
          return IR.Exp.Register(n, pos)
        } else {
          halt(s"TODO: $exp")
        }
      case exp: AST.Exp.Binary =>
        if (isScalar(exp.typedOpt.get)) {
          val t = exp.typedOpt.get
          var kind = exp.attr.resOpt.get.asInstanceOf[AST.ResolvedInfo.BuiltIn].kind
          kind match {
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryEq => kind = AST.ResolvedInfo.BuiltIn.Kind.BinaryEquiv
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryNe => kind = AST.ResolvedInfo.BuiltIn.Kind.BinaryInequiv
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryCondAnd if threeAddressCode =>
              return translateExp(AST.Exp.If(exp.left, exp.right, AST.Exp.LitB(T, AST.Attr(exp.posOpt)), AST.TypedAttr(exp.posOpt, AST.Typed.bOpt)))
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryCondOr if threeAddressCode =>
              return translateExp(AST.Exp.If(exp.left, AST.Exp.LitB(T, AST.Attr(exp.posOpt)), exp.right, AST.TypedAttr(exp.posOpt, AST.Typed.bOpt)))
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryCondImply if threeAddressCode =>
              return translateExp(AST.Exp.If(exp.left, exp.right, AST.Exp.LitB(F, AST.Attr(exp.posOpt)), AST.TypedAttr(exp.posOpt, AST.Typed.bOpt)))
            case _ =>
          }
          val e = IR.Exp.Binary(t, translateExp(exp.left), kind, translateExp(exp.right), pos)
          if (!threeAddressCode) {
            return e
          }
          val n = freshRegister()
          stmts = stmts :+ IR.Stmt.Decl.Register(F, t, n, pos)
          stmts = stmts :+ IR.Stmt.Assign.Register(n, e, pos)
          return IR.Exp.Register(n, pos)
        } else {
          halt(s"TODO: $exp")
        }
      case exp: AST.Exp.If =>
        val t = exp.typedOpt.get
        val n = freshRegister()
        val cond = translateExp(exp.cond)
        if (!threeAddressCode) {
          return IR.Exp.If(cond, translateExp(exp.thenExp), translateExp(exp.elseExp), pos)
        }
        stmts = stmts :+ IR.Stmt.Decl.Register(F, t, n, pos)
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
          IR.Stmt.Block(thenStmts :+ IR.Stmt.Assign.Register(n, thenExp, thenPos), thenPos),
          IR.Stmt.Block(elseStmts :+ IR.Stmt.Assign.Register(n, elseExp, elsePos), elsePos), pos)
        return IR.Exp.Register(n, pos)
      case exp: AST.Exp.Invoke =>
        exp.attr.resOpt.get match {
          case res: AST.ResolvedInfo.Method if res.mode == AST.MethodMode.Select =>
            val rcv: IR.Exp = exp.receiverOpt match {
              case Some(receiver) =>
                if (exp.ident.id.value == "apply") {
                  translateExp(receiver)
                } else {
                  translateExp(AST.Exp.Select(Some(receiver), exp.ident.id, exp.targs, exp.ident.attr))
                }
              case _ =>
                translateExp(exp.ident)
            }
            val index = translateExp(exp.args(0))
            val indexing = IR.Exp.Indexing(rcv, index, pos)
            if (threeAddressCode) {
              val n = freshRegister()
              stmts = stmts :+ IR.Stmt.Decl.Register(F, exp.typedOpt.get, n, pos)
              stmts = stmts :+ IR.Stmt.Assign.Register(n, indexing, pos)
              return IR.Exp.Register(n, pos)
            } else {
              return indexing
            }
          case _ => halt(s"TODO: $exp")
        }
      case exp: AST.Exp.This => return IR.Exp.LocalVarRef(methodContext, "this", pos)
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
