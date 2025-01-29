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

  def translateStmt(stmt: AST.Stmt): ISZ[IR.Stmt] = {
    val pos = stmt.posOpt.get
    stmt match {
      case stmt: AST.Stmt.Var =>
        val init = stmt.initOpt.get
        var oldStmts = stmts
        stmts = ISZ()
        val t = stmt.attr.typedOpt.get
        val rhs = translateAssignExp(init)
        stmts = stmts :+ IR.Stmt.Assign.Local(shouldCopy(t), stmt.id.value, rhs, pos)
        oldStmts = oldStmts :+ IR.Stmt.Decl.Local(stmt.isVal, t, stmt.id.value, pos)
        stmts = oldStmts :+ IR.Stmt.Block(stmts, pos)
      case stmt: AST.Stmt.Assign =>
        val oldStmts = stmts
        stmts = ISZ()
        val rhs = translateAssignExp(stmt.rhs)
        stmt.lhs match {
          case lhs: AST.Exp.Ident =>
            lhs.resOpt.get match {
              case _: AST.ResolvedInfo.LocalVar =>
                stmts = stmts :+ IR.Stmt.Assign.Local(shouldCopy(stmt.rhs.typedOpt.get), lhs.id.value, rhs, pos)
              case _ =>
            }
          case lhs: AST.Exp.Select => halt(s"TODO: $lhs")
          case lhs: AST.Exp.Invoke => halt(s"TODO: $lhs")
          case _ => halt("Infeasible")
        }
        stmts = oldStmts :+ IR.Stmt.Block(stmts, pos)
      case stmt: AST.Stmt.If =>
      case stmt: AST.Stmt.While =>
      case _ =>
    }
    halt(s"TODO: $stmt")
  }

  def translateAssignExp(stmt: AST.AssignExp): IR.Exp = {
    stmt match {
      case stmt: AST.Stmt.Expr => return translateExp(stmt.exp)
      case stmt: AST.Stmt.Block =>
      case stmt: AST.Stmt.If =>
      case stmt: AST.Stmt.Match =>
      case stmt: AST.Stmt.Return =>
    }
    halt(s"TODO: $stmt")
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
        stmts = stmts :+ IR.Stmt.If(ISZ(
          IR.Stmt.IfCondBlock(cond,
            IR.Stmt.Block(thenStmts :+ IR.Stmt.Assign.Register(n, thenExp, thenPos), thenPos), thenPos)),
            IR.Stmt.Block(elseStmts :+ IR.Stmt.Assign.Register(n, elseExp, elsePos), elsePos), pos)
        return IR.Exp.Register(n, pos)
      case _ =>
    }
    halt(s"TODO: $exp")
  }
}
