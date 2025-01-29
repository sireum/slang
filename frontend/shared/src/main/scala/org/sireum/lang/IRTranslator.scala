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

  def translateWorksheet(threeAddressCode: B, th: TypeHierarchy, worksheet: AST.TopUnit.Program): IR.Program = {
    halt("TODO")
  }

}

@record class IRTranslator(val threeAddressCode: B, val th: TypeHierarchy) {

  var _freshRegister: Z = 0
  var stmts: ISZ[IR.Stmt] = ISZ()

  def translateMethod(method: AST.Stmt.Method): IR.Program = {
    halt(s"TODO: $method")
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
        stmts = stmts :+ IR.Stmt.Assign.Local(shouldCopy(t), stmt.id.value, varRhs, pos)
        oldStmts = oldStmts :+ IR.Stmt.Decl.Local(stmt.isVal, t, stmt.id.value, pos)
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
                stmts = stmts :+ IR.Stmt.Assign.Local(copy, lhs.id.value, identRhs, pos)
              case res: AST.ResolvedInfo.Var =>
                if (res.isInObject) {
                  stmts = stmts :+ IR.Stmt.Assign.Global(copy, res.owner :+ res.id, identRhs, pos)
                }
                val receiverPos = lhs.posOpt.get
                val receiver: IR.Exp = if (threeAddressCode) {
                  val n = freshRegister()
                  stmts = stmts :+ IR.Stmt.Assign.Register(n, identRhs, receiverPos)
                  IR.Exp.Register(n, receiverPos)
                } else {
                  IR.Exp.LocalVarRef("this", receiverPos)
                }
                stmts = stmts :+ IR.Stmt.Assign.Field(copy, receiver, lhs.id.value, identRhs, pos)
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
                val receiver = translateExp(lhs.receiverOpt.get)
                stmts = stmts :+ IR.Stmt.Assign.Field(copy, receiver, lhs.id.value, selectRhs(), pos)
            }
          case lhs: AST.Exp.Invoke =>
            val receiver = translateExp(lhs.receiverOpt.get)
            val index = translateExp(lhs.args(0))
            val invokeRhs: IR.Exp = stmt.rhs match {
              case rhs: AST.Stmt.Expr => translateExp(rhs.exp)
              case _ =>
                val n = freshRegister()
                translateAssignExp(stmt.rhs, n)
                IR.Exp.Register(n, stmt.rhs.asStmt.posOpt.get)
            }
            stmts = stmts :+ IR.Stmt.Assign.Index(copy, receiver, index, invokeRhs, pos)
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
        stmts = oldStmts :+ IR.Stmt.Block(condStmts :+
          IR.Stmt.While(cond, IR.Stmt.Block(stmts, bPos), pos), pos)
      case stmt: AST.Stmt.Expr =>
        translateExp(stmt.exp)
      case stmt: AST.Stmt.Return =>
        stmt.expOpt match {
          case Some(exp) => stmts = stmts :+ IR.Stmt.Return(Some(translateExp(exp)), pos)
          case _ => stmts = stmts :+ IR.Stmt.Return(None(), pos)
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
      case exp: AST.Exp.StringInterpolate if isSubZ(exp.typedOpt.get) =>
      case exp: AST.Exp.StringInterpolate if isScalar(exp.typedOpt.get) =>
        val t = exp.typedOpt.get.asInstanceOf[AST.Typed.Name]
        val info = th.typeMap.get(t.ids).get.asInstanceOf[TypeInfo.SubZ]
        val value = Z(exp.lits(0).value).get
        return IR.Exp.Int(t, if (info.ast.isBitVector) info.ast.bitWidth else 0, value, pos)
      case exp: AST.Exp.Unary if isScalar(exp.typedOpt.get) =>
        val t = exp.typedOpt.get
        val e = IR.Exp.Unary(t, exp.op, translateExp(exp.exp), pos)
        if (!threeAddressCode) {
          return e
        }
        val n = freshRegister()
        stmts = stmts :+ IR.Stmt.Decl.Register(t, n, pos)
        stmts = stmts :+ IR.Stmt.Assign.Register(n, e, pos)
        return IR.Exp.Register(n, pos)
      case exp: AST.Exp.Binary if isScalar(exp.typedOpt.get) =>
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
        stmts = stmts :+ IR.Stmt.Decl.Register(t, n, pos)
        stmts = stmts :+ IR.Stmt.Assign.Register(n, e, pos)
        return IR.Exp.Register(n, pos)
      case exp: AST.Exp.If =>
        val t = exp.typedOpt.get
        val n = freshRegister()
        val cond = translateExp(exp.cond)
        if (!threeAddressCode) {
          return IR.Exp.If(cond, translateExp(exp.thenExp), translateExp(exp.elseExp), pos)
        }
        stmts = stmts :+ IR.Stmt.Decl.Register(t, n, pos)
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
      case _ =>
    }
    halt(s"TODO: $exp")
  }
}
