// #Sireum
// @formatter:off

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

// This file is auto-generated from IR.scala

// This file is auto-generated from Typed.scala

package org.sireum.lang.ast

import org.sireum._

object IRTransformer {

  @datatype class PreResult[Context, T](val ctx: Context,
                                        val continu: B,
                                        val resultOpt: Option[T])

  @datatype class TPostResult[Context, T](val ctx: Context,
                                          val resultOpt: Option[T])

  @sig trait PrePost[Context] {

    @pure def preIRMethodContext(ctx: Context, o: IR.MethodContext): PreResult[Context, IR.MethodContext] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRExp(ctx: Context, o: IR.Exp): PreResult[Context, IR.Exp] = {
      o match {
        case o: IR.Exp.Bool => return preIRExpBool(ctx, o)
        case o: IR.Exp.Int => return preIRExpInt(ctx, o)
        case o: IR.Exp.F32 => return preIRExpF32(ctx, o)
        case o: IR.Exp.F64 => return preIRExpF64(ctx, o)
        case o: IR.Exp.R => return preIRExpR(ctx, o)
        case o: IR.Exp.String => return preIRExpString(ctx, o)
        case o: IR.Exp.Temp => return preIRExpTemp(ctx, o)
        case o: IR.Exp.LocalVarRef => return preIRExpLocalVarRef(ctx, o)
        case o: IR.Exp.GlobalVarRef => return preIRExpGlobalVarRef(ctx, o)
        case o: IR.Exp.EnumElementRef => return preIRExpEnumElementRef(ctx, o)
        case o: IR.Exp.FieldVarRef => return preIRExpFieldVarRef(ctx, o)
        case o: IR.Exp.Unary => return preIRExpUnary(ctx, o)
        case o: IR.Exp.Binary => return preIRExpBinary(ctx, o)
        case o: IR.Exp.If => return preIRExpIf(ctx, o)
        case o: IR.Exp.Construct => return preIRExpConstruct(ctx, o)
        case o: IR.Exp.Apply => return preIRExpApply(ctx, o)
        case o: IR.Exp.Indexing => return preIRExpIndexing(ctx, o)
        case o: IR.Exp.Type => return preIRExpType(ctx, o)
        case o: IR.Exp.Intrinsic => return preIRExpIntrinsic(ctx, o)
      }
    }

    @pure def preIRExpBool(ctx: Context, o: IR.Exp.Bool): PreResult[Context, IR.Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRExpInt(ctx: Context, o: IR.Exp.Int): PreResult[Context, IR.Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRExpF32(ctx: Context, o: IR.Exp.F32): PreResult[Context, IR.Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRExpF64(ctx: Context, o: IR.Exp.F64): PreResult[Context, IR.Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRExpR(ctx: Context, o: IR.Exp.R): PreResult[Context, IR.Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRExpString(ctx: Context, o: IR.Exp.String): PreResult[Context, IR.Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRExpTemp(ctx: Context, o: IR.Exp.Temp): PreResult[Context, IR.Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRExpLocalVarRef(ctx: Context, o: IR.Exp.LocalVarRef): PreResult[Context, IR.Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRExpGlobalVarRef(ctx: Context, o: IR.Exp.GlobalVarRef): PreResult[Context, IR.Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRExpEnumElementRef(ctx: Context, o: IR.Exp.EnumElementRef): PreResult[Context, IR.Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRExpFieldVarRef(ctx: Context, o: IR.Exp.FieldVarRef): PreResult[Context, IR.Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRExpUnary(ctx: Context, o: IR.Exp.Unary): PreResult[Context, IR.Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRExpBinary(ctx: Context, o: IR.Exp.Binary): PreResult[Context, IR.Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRExpIf(ctx: Context, o: IR.Exp.If): PreResult[Context, IR.Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRExpConstruct(ctx: Context, o: IR.Exp.Construct): PreResult[Context, IR.Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRExpApply(ctx: Context, o: IR.Exp.Apply): PreResult[Context, IR.Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRExpIndexing(ctx: Context, o: IR.Exp.Indexing): PreResult[Context, IR.Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRExpType(ctx: Context, o: IR.Exp.Type): PreResult[Context, IR.Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRExpIntrinsic(ctx: Context, o: IR.Exp.Intrinsic): PreResult[Context, IR.Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRExpIntrinsicType(ctx: Context, o: IR.Exp.Intrinsic.Type): PreResult[Context, IR.Exp.Intrinsic.Type] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRStmt(ctx: Context, o: IR.Stmt): PreResult[Context, IR.Stmt] = {
      o match {
        case o: IR.Stmt.Expr =>
          val r: PreResult[Context, IR.Stmt] = preIRStmtExpr(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: IR.Stmt)) => PreResult(preCtx, continu, Some[IR.Stmt](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type IR.Stmt")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[IR.Stmt]())
          }
          return r
        case o: IR.Stmt.Assign.Local =>
          val r: PreResult[Context, IR.Stmt] = preIRStmtAssignLocal(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: IR.Stmt)) => PreResult(preCtx, continu, Some[IR.Stmt](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type IR.Stmt")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[IR.Stmt]())
          }
          return r
        case o: IR.Stmt.Assign.Global =>
          val r: PreResult[Context, IR.Stmt] = preIRStmtAssignGlobal(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: IR.Stmt)) => PreResult(preCtx, continu, Some[IR.Stmt](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type IR.Stmt")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[IR.Stmt]())
          }
          return r
        case o: IR.Stmt.Assign.Temp =>
          val r: PreResult[Context, IR.Stmt] = preIRStmtAssignTemp(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: IR.Stmt)) => PreResult(preCtx, continu, Some[IR.Stmt](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type IR.Stmt")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[IR.Stmt]())
          }
          return r
        case o: IR.Stmt.Assign.Field =>
          val r: PreResult[Context, IR.Stmt] = preIRStmtAssignField(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: IR.Stmt)) => PreResult(preCtx, continu, Some[IR.Stmt](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type IR.Stmt")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[IR.Stmt]())
          }
          return r
        case o: IR.Stmt.Assign.Index =>
          val r: PreResult[Context, IR.Stmt] = preIRStmtAssignIndex(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: IR.Stmt)) => PreResult(preCtx, continu, Some[IR.Stmt](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type IR.Stmt")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[IR.Stmt]())
          }
          return r
        case o: IR.Stmt.If => return preIRStmtIf(ctx, o)
        case o: IR.Stmt.Block => return preIRStmtBlock(ctx, o)
        case o: IR.Stmt.While => return preIRStmtWhile(ctx, o)
        case o: IR.Stmt.Return => return preIRStmtReturn(ctx, o)
        case o: IR.Stmt.Halt =>
          val r: PreResult[Context, IR.Stmt] = preIRStmtHalt(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: IR.Stmt)) => PreResult(preCtx, continu, Some[IR.Stmt](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type IR.Stmt")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[IR.Stmt]())
          }
          return r
        case o: IR.Stmt.Decl =>
          val r: PreResult[Context, IR.Stmt] = preIRStmtDecl(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: IR.Stmt)) => PreResult(preCtx, continu, Some[IR.Stmt](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type IR.Stmt")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[IR.Stmt]())
          }
          return r
        case o: IR.Stmt.Intrinsic =>
          val r: PreResult[Context, IR.Stmt] = preIRStmtIntrinsic(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: IR.Stmt)) => PreResult(preCtx, continu, Some[IR.Stmt](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type IR.Stmt")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[IR.Stmt]())
          }
          return r
      }
    }

    @pure def preIRStmtGround(ctx: Context, o: IR.Stmt.Ground): PreResult[Context, IR.Stmt.Ground] = {
      o match {
        case o: IR.Stmt.Expr => return preIRStmtExpr(ctx, o)
        case o: IR.Stmt.Assign.Local =>
          val r: PreResult[Context, IR.Stmt.Ground] = preIRStmtAssignLocal(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: IR.Stmt.Ground)) => PreResult(preCtx, continu, Some[IR.Stmt.Ground](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type IR.Stmt.Ground")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[IR.Stmt.Ground]())
          }
          return r
        case o: IR.Stmt.Assign.Global =>
          val r: PreResult[Context, IR.Stmt.Ground] = preIRStmtAssignGlobal(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: IR.Stmt.Ground)) => PreResult(preCtx, continu, Some[IR.Stmt.Ground](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type IR.Stmt.Ground")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[IR.Stmt.Ground]())
          }
          return r
        case o: IR.Stmt.Assign.Temp =>
          val r: PreResult[Context, IR.Stmt.Ground] = preIRStmtAssignTemp(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: IR.Stmt.Ground)) => PreResult(preCtx, continu, Some[IR.Stmt.Ground](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type IR.Stmt.Ground")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[IR.Stmt.Ground]())
          }
          return r
        case o: IR.Stmt.Assign.Field =>
          val r: PreResult[Context, IR.Stmt.Ground] = preIRStmtAssignField(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: IR.Stmt.Ground)) => PreResult(preCtx, continu, Some[IR.Stmt.Ground](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type IR.Stmt.Ground")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[IR.Stmt.Ground]())
          }
          return r
        case o: IR.Stmt.Assign.Index =>
          val r: PreResult[Context, IR.Stmt.Ground] = preIRStmtAssignIndex(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: IR.Stmt.Ground)) => PreResult(preCtx, continu, Some[IR.Stmt.Ground](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type IR.Stmt.Ground")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[IR.Stmt.Ground]())
          }
          return r
        case o: IR.Stmt.Halt => return preIRStmtHalt(ctx, o)
        case o: IR.Stmt.Decl => return preIRStmtDecl(ctx, o)
        case o: IR.Stmt.Intrinsic => return preIRStmtIntrinsic(ctx, o)
      }
    }

    @pure def preIRStmtExpr(ctx: Context, o: IR.Stmt.Expr): PreResult[Context, IR.Stmt.Ground] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRStmtAssign(ctx: Context, o: IR.Stmt.Assign): PreResult[Context, IR.Stmt.Assign] = {
      o match {
        case o: IR.Stmt.Assign.Local => return preIRStmtAssignLocal(ctx, o)
        case o: IR.Stmt.Assign.Global => return preIRStmtAssignGlobal(ctx, o)
        case o: IR.Stmt.Assign.Temp => return preIRStmtAssignTemp(ctx, o)
        case o: IR.Stmt.Assign.Field => return preIRStmtAssignField(ctx, o)
        case o: IR.Stmt.Assign.Index => return preIRStmtAssignIndex(ctx, o)
      }
    }

    @pure def preIRStmtAssignLocal(ctx: Context, o: IR.Stmt.Assign.Local): PreResult[Context, IR.Stmt.Assign] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRStmtAssignGlobal(ctx: Context, o: IR.Stmt.Assign.Global): PreResult[Context, IR.Stmt.Assign] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRStmtAssignTemp(ctx: Context, o: IR.Stmt.Assign.Temp): PreResult[Context, IR.Stmt.Assign] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRStmtAssignField(ctx: Context, o: IR.Stmt.Assign.Field): PreResult[Context, IR.Stmt.Assign] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRStmtAssignIndex(ctx: Context, o: IR.Stmt.Assign.Index): PreResult[Context, IR.Stmt.Assign] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRStmtIf(ctx: Context, o: IR.Stmt.If): PreResult[Context, IR.Stmt] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRStmtBlock(ctx: Context, o: IR.Stmt.Block): PreResult[Context, IR.Stmt] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRStmtWhile(ctx: Context, o: IR.Stmt.While): PreResult[Context, IR.Stmt] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRStmtReturn(ctx: Context, o: IR.Stmt.Return): PreResult[Context, IR.Stmt] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRStmtHalt(ctx: Context, o: IR.Stmt.Halt): PreResult[Context, IR.Stmt.Ground] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRStmtDecl(ctx: Context, o: IR.Stmt.Decl): PreResult[Context, IR.Stmt.Ground] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRStmtDeclLocal(ctx: Context, o: IR.Stmt.Decl.Local): PreResult[Context, IR.Stmt.Decl.Local] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRStmtIntrinsic(ctx: Context, o: IR.Stmt.Intrinsic): PreResult[Context, IR.Stmt.Ground] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRStmtIntrinsicType(ctx: Context, o: IR.Stmt.Intrinsic.Type): PreResult[Context, IR.Stmt.Intrinsic.Type] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRJump(ctx: Context, o: IR.Jump): PreResult[Context, IR.Jump] = {
      o match {
        case o: IR.Jump.Goto => return preIRJumpGoto(ctx, o)
        case o: IR.Jump.If => return preIRJumpIf(ctx, o)
        case o: IR.Jump.Return => return preIRJumpReturn(ctx, o)
        case o: IR.Jump.Switch => return preIRJumpSwitch(ctx, o)
        case o: IR.Jump.Intrinsic => return preIRJumpIntrinsic(ctx, o)
      }
    }

    @pure def preIRJumpGoto(ctx: Context, o: IR.Jump.Goto): PreResult[Context, IR.Jump] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRJumpIf(ctx: Context, o: IR.Jump.If): PreResult[Context, IR.Jump] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRJumpReturn(ctx: Context, o: IR.Jump.Return): PreResult[Context, IR.Jump] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRJumpSwitch(ctx: Context, o: IR.Jump.Switch): PreResult[Context, IR.Jump] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRJumpSwitchCase(ctx: Context, o: IR.Jump.Switch.Case): PreResult[Context, IR.Jump.Switch.Case] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRJumpIntrinsic(ctx: Context, o: IR.Jump.Intrinsic): PreResult[Context, IR.Jump] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRJumpIntrinsicType(ctx: Context, o: IR.Jump.Intrinsic.Type): PreResult[Context, IR.Jump.Intrinsic.Type] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRBasicBlock(ctx: Context, o: IR.BasicBlock): PreResult[Context, IR.BasicBlock] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRBody(ctx: Context, o: IR.Body): PreResult[Context, IR.Body] = {
      o match {
        case o: IR.Body.Block => return preIRBodyBlock(ctx, o)
        case o: IR.Body.Basic => return preIRBodyBasic(ctx, o)
      }
    }

    @pure def preIRBodyBlock(ctx: Context, o: IR.Body.Block): PreResult[Context, IR.Body] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRBodyBasic(ctx: Context, o: IR.Body.Basic): PreResult[Context, IR.Body] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRProcedure(ctx: Context, o: IR.Procedure): PreResult[Context, IR.Procedure] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRGlobal(ctx: Context, o: IR.Global): PreResult[Context, IR.Global] = {
      return PreResult(ctx, T, None())
    }

    @pure def preIRProgram(ctx: Context, o: IR.Program): PreResult[Context, IR.Program] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTyped(ctx: Context, o: Typed): PreResult[Context, Typed] = {
      o match {
        case o: Typed.Name => return preTypedName(ctx, o)
        case o: Typed.Tuple => return preTypedTuple(ctx, o)
        case o: Typed.Fun => return preTypedFun(ctx, o)
        case o: Typed.TypeVar => return preTypedTypeVar(ctx, o)
        case o: Typed.Package => return preTypedPackage(ctx, o)
        case o: Typed.Object => return preTypedObject(ctx, o)
        case o: Typed.Enum => return preTypedEnum(ctx, o)
        case o: Typed.Method => return preTypedMethod(ctx, o)
        case o: Typed.Methods => return preTypedMethods(ctx, o)
        case o: Typed.Fact => return preTypedFact(ctx, o)
        case o: Typed.Theorem => return preTypedTheorem(ctx, o)
        case o: Typed.Inv => return preTypedInv(ctx, o)
      }
    }

    @pure def preTypedName(ctx: Context, o: Typed.Name): PreResult[Context, Typed] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTypedTuple(ctx: Context, o: Typed.Tuple): PreResult[Context, Typed] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTypedFun(ctx: Context, o: Typed.Fun): PreResult[Context, Typed] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTypedTypeVar(ctx: Context, o: Typed.TypeVar): PreResult[Context, Typed] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTypedPackage(ctx: Context, o: Typed.Package): PreResult[Context, Typed] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTypedObject(ctx: Context, o: Typed.Object): PreResult[Context, Typed] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTypedEnum(ctx: Context, o: Typed.Enum): PreResult[Context, Typed] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTypedMethod(ctx: Context, o: Typed.Method): PreResult[Context, Typed] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTypedMethods(ctx: Context, o: Typed.Methods): PreResult[Context, Typed] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTypedFact(ctx: Context, o: Typed.Fact): PreResult[Context, Typed] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTypedTheorem(ctx: Context, o: Typed.Theorem): PreResult[Context, Typed] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTypedInv(ctx: Context, o: Typed.Inv): PreResult[Context, Typed] = {
      return PreResult(ctx, T, None())
    }

    @pure def postIRMethodContext(ctx: Context, o: IR.MethodContext): TPostResult[Context, IR.MethodContext] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRExp(ctx: Context, o: IR.Exp): TPostResult[Context, IR.Exp] = {
      o match {
        case o: IR.Exp.Bool => return postIRExpBool(ctx, o)
        case o: IR.Exp.Int => return postIRExpInt(ctx, o)
        case o: IR.Exp.F32 => return postIRExpF32(ctx, o)
        case o: IR.Exp.F64 => return postIRExpF64(ctx, o)
        case o: IR.Exp.R => return postIRExpR(ctx, o)
        case o: IR.Exp.String => return postIRExpString(ctx, o)
        case o: IR.Exp.Temp => return postIRExpTemp(ctx, o)
        case o: IR.Exp.LocalVarRef => return postIRExpLocalVarRef(ctx, o)
        case o: IR.Exp.GlobalVarRef => return postIRExpGlobalVarRef(ctx, o)
        case o: IR.Exp.EnumElementRef => return postIRExpEnumElementRef(ctx, o)
        case o: IR.Exp.FieldVarRef => return postIRExpFieldVarRef(ctx, o)
        case o: IR.Exp.Unary => return postIRExpUnary(ctx, o)
        case o: IR.Exp.Binary => return postIRExpBinary(ctx, o)
        case o: IR.Exp.If => return postIRExpIf(ctx, o)
        case o: IR.Exp.Construct => return postIRExpConstruct(ctx, o)
        case o: IR.Exp.Apply => return postIRExpApply(ctx, o)
        case o: IR.Exp.Indexing => return postIRExpIndexing(ctx, o)
        case o: IR.Exp.Type => return postIRExpType(ctx, o)
        case o: IR.Exp.Intrinsic => return postIRExpIntrinsic(ctx, o)
      }
    }

    @pure def postIRExpBool(ctx: Context, o: IR.Exp.Bool): TPostResult[Context, IR.Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRExpInt(ctx: Context, o: IR.Exp.Int): TPostResult[Context, IR.Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRExpF32(ctx: Context, o: IR.Exp.F32): TPostResult[Context, IR.Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRExpF64(ctx: Context, o: IR.Exp.F64): TPostResult[Context, IR.Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRExpR(ctx: Context, o: IR.Exp.R): TPostResult[Context, IR.Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRExpString(ctx: Context, o: IR.Exp.String): TPostResult[Context, IR.Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRExpTemp(ctx: Context, o: IR.Exp.Temp): TPostResult[Context, IR.Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRExpLocalVarRef(ctx: Context, o: IR.Exp.LocalVarRef): TPostResult[Context, IR.Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRExpGlobalVarRef(ctx: Context, o: IR.Exp.GlobalVarRef): TPostResult[Context, IR.Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRExpEnumElementRef(ctx: Context, o: IR.Exp.EnumElementRef): TPostResult[Context, IR.Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRExpFieldVarRef(ctx: Context, o: IR.Exp.FieldVarRef): TPostResult[Context, IR.Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRExpUnary(ctx: Context, o: IR.Exp.Unary): TPostResult[Context, IR.Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRExpBinary(ctx: Context, o: IR.Exp.Binary): TPostResult[Context, IR.Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRExpIf(ctx: Context, o: IR.Exp.If): TPostResult[Context, IR.Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRExpConstruct(ctx: Context, o: IR.Exp.Construct): TPostResult[Context, IR.Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRExpApply(ctx: Context, o: IR.Exp.Apply): TPostResult[Context, IR.Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRExpIndexing(ctx: Context, o: IR.Exp.Indexing): TPostResult[Context, IR.Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRExpType(ctx: Context, o: IR.Exp.Type): TPostResult[Context, IR.Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRExpIntrinsic(ctx: Context, o: IR.Exp.Intrinsic): TPostResult[Context, IR.Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRExpIntrinsicType(ctx: Context, o: IR.Exp.Intrinsic.Type): TPostResult[Context, IR.Exp.Intrinsic.Type] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRStmt(ctx: Context, o: IR.Stmt): TPostResult[Context, IR.Stmt] = {
      o match {
        case o: IR.Stmt.Expr =>
          val r: TPostResult[Context, IR.Stmt] = postIRStmtExpr(ctx, o) match {
           case TPostResult(postCtx, Some(result: IR.Stmt)) => TPostResult(postCtx, Some[IR.Stmt](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type IR.Stmt")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[IR.Stmt]())
          }
          return r
        case o: IR.Stmt.Assign.Local =>
          val r: TPostResult[Context, IR.Stmt] = postIRStmtAssignLocal(ctx, o) match {
           case TPostResult(postCtx, Some(result: IR.Stmt)) => TPostResult(postCtx, Some[IR.Stmt](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type IR.Stmt")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[IR.Stmt]())
          }
          return r
        case o: IR.Stmt.Assign.Global =>
          val r: TPostResult[Context, IR.Stmt] = postIRStmtAssignGlobal(ctx, o) match {
           case TPostResult(postCtx, Some(result: IR.Stmt)) => TPostResult(postCtx, Some[IR.Stmt](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type IR.Stmt")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[IR.Stmt]())
          }
          return r
        case o: IR.Stmt.Assign.Temp =>
          val r: TPostResult[Context, IR.Stmt] = postIRStmtAssignTemp(ctx, o) match {
           case TPostResult(postCtx, Some(result: IR.Stmt)) => TPostResult(postCtx, Some[IR.Stmt](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type IR.Stmt")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[IR.Stmt]())
          }
          return r
        case o: IR.Stmt.Assign.Field =>
          val r: TPostResult[Context, IR.Stmt] = postIRStmtAssignField(ctx, o) match {
           case TPostResult(postCtx, Some(result: IR.Stmt)) => TPostResult(postCtx, Some[IR.Stmt](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type IR.Stmt")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[IR.Stmt]())
          }
          return r
        case o: IR.Stmt.Assign.Index =>
          val r: TPostResult[Context, IR.Stmt] = postIRStmtAssignIndex(ctx, o) match {
           case TPostResult(postCtx, Some(result: IR.Stmt)) => TPostResult(postCtx, Some[IR.Stmt](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type IR.Stmt")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[IR.Stmt]())
          }
          return r
        case o: IR.Stmt.If => return postIRStmtIf(ctx, o)
        case o: IR.Stmt.Block => return postIRStmtBlock(ctx, o)
        case o: IR.Stmt.While => return postIRStmtWhile(ctx, o)
        case o: IR.Stmt.Return => return postIRStmtReturn(ctx, o)
        case o: IR.Stmt.Halt =>
          val r: TPostResult[Context, IR.Stmt] = postIRStmtHalt(ctx, o) match {
           case TPostResult(postCtx, Some(result: IR.Stmt)) => TPostResult(postCtx, Some[IR.Stmt](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type IR.Stmt")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[IR.Stmt]())
          }
          return r
        case o: IR.Stmt.Decl =>
          val r: TPostResult[Context, IR.Stmt] = postIRStmtDecl(ctx, o) match {
           case TPostResult(postCtx, Some(result: IR.Stmt)) => TPostResult(postCtx, Some[IR.Stmt](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type IR.Stmt")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[IR.Stmt]())
          }
          return r
        case o: IR.Stmt.Intrinsic =>
          val r: TPostResult[Context, IR.Stmt] = postIRStmtIntrinsic(ctx, o) match {
           case TPostResult(postCtx, Some(result: IR.Stmt)) => TPostResult(postCtx, Some[IR.Stmt](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type IR.Stmt")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[IR.Stmt]())
          }
          return r
      }
    }

    @pure def postIRStmtGround(ctx: Context, o: IR.Stmt.Ground): TPostResult[Context, IR.Stmt.Ground] = {
      o match {
        case o: IR.Stmt.Expr => return postIRStmtExpr(ctx, o)
        case o: IR.Stmt.Assign.Local =>
          val r: TPostResult[Context, IR.Stmt.Ground] = postIRStmtAssignLocal(ctx, o) match {
           case TPostResult(postCtx, Some(result: IR.Stmt.Ground)) => TPostResult(postCtx, Some[IR.Stmt.Ground](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type IR.Stmt.Ground")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[IR.Stmt.Ground]())
          }
          return r
        case o: IR.Stmt.Assign.Global =>
          val r: TPostResult[Context, IR.Stmt.Ground] = postIRStmtAssignGlobal(ctx, o) match {
           case TPostResult(postCtx, Some(result: IR.Stmt.Ground)) => TPostResult(postCtx, Some[IR.Stmt.Ground](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type IR.Stmt.Ground")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[IR.Stmt.Ground]())
          }
          return r
        case o: IR.Stmt.Assign.Temp =>
          val r: TPostResult[Context, IR.Stmt.Ground] = postIRStmtAssignTemp(ctx, o) match {
           case TPostResult(postCtx, Some(result: IR.Stmt.Ground)) => TPostResult(postCtx, Some[IR.Stmt.Ground](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type IR.Stmt.Ground")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[IR.Stmt.Ground]())
          }
          return r
        case o: IR.Stmt.Assign.Field =>
          val r: TPostResult[Context, IR.Stmt.Ground] = postIRStmtAssignField(ctx, o) match {
           case TPostResult(postCtx, Some(result: IR.Stmt.Ground)) => TPostResult(postCtx, Some[IR.Stmt.Ground](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type IR.Stmt.Ground")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[IR.Stmt.Ground]())
          }
          return r
        case o: IR.Stmt.Assign.Index =>
          val r: TPostResult[Context, IR.Stmt.Ground] = postIRStmtAssignIndex(ctx, o) match {
           case TPostResult(postCtx, Some(result: IR.Stmt.Ground)) => TPostResult(postCtx, Some[IR.Stmt.Ground](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type IR.Stmt.Ground")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[IR.Stmt.Ground]())
          }
          return r
        case o: IR.Stmt.Halt => return postIRStmtHalt(ctx, o)
        case o: IR.Stmt.Decl => return postIRStmtDecl(ctx, o)
        case o: IR.Stmt.Intrinsic => return postIRStmtIntrinsic(ctx, o)
      }
    }

    @pure def postIRStmtExpr(ctx: Context, o: IR.Stmt.Expr): TPostResult[Context, IR.Stmt.Ground] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRStmtAssign(ctx: Context, o: IR.Stmt.Assign): TPostResult[Context, IR.Stmt.Assign] = {
      o match {
        case o: IR.Stmt.Assign.Local => return postIRStmtAssignLocal(ctx, o)
        case o: IR.Stmt.Assign.Global => return postIRStmtAssignGlobal(ctx, o)
        case o: IR.Stmt.Assign.Temp => return postIRStmtAssignTemp(ctx, o)
        case o: IR.Stmt.Assign.Field => return postIRStmtAssignField(ctx, o)
        case o: IR.Stmt.Assign.Index => return postIRStmtAssignIndex(ctx, o)
      }
    }

    @pure def postIRStmtAssignLocal(ctx: Context, o: IR.Stmt.Assign.Local): TPostResult[Context, IR.Stmt.Assign] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRStmtAssignGlobal(ctx: Context, o: IR.Stmt.Assign.Global): TPostResult[Context, IR.Stmt.Assign] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRStmtAssignTemp(ctx: Context, o: IR.Stmt.Assign.Temp): TPostResult[Context, IR.Stmt.Assign] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRStmtAssignField(ctx: Context, o: IR.Stmt.Assign.Field): TPostResult[Context, IR.Stmt.Assign] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRStmtAssignIndex(ctx: Context, o: IR.Stmt.Assign.Index): TPostResult[Context, IR.Stmt.Assign] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRStmtIf(ctx: Context, o: IR.Stmt.If): TPostResult[Context, IR.Stmt] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRStmtBlock(ctx: Context, o: IR.Stmt.Block): TPostResult[Context, IR.Stmt] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRStmtWhile(ctx: Context, o: IR.Stmt.While): TPostResult[Context, IR.Stmt] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRStmtReturn(ctx: Context, o: IR.Stmt.Return): TPostResult[Context, IR.Stmt] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRStmtHalt(ctx: Context, o: IR.Stmt.Halt): TPostResult[Context, IR.Stmt.Ground] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRStmtDecl(ctx: Context, o: IR.Stmt.Decl): TPostResult[Context, IR.Stmt.Ground] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRStmtDeclLocal(ctx: Context, o: IR.Stmt.Decl.Local): TPostResult[Context, IR.Stmt.Decl.Local] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRStmtIntrinsic(ctx: Context, o: IR.Stmt.Intrinsic): TPostResult[Context, IR.Stmt.Ground] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRStmtIntrinsicType(ctx: Context, o: IR.Stmt.Intrinsic.Type): TPostResult[Context, IR.Stmt.Intrinsic.Type] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRJump(ctx: Context, o: IR.Jump): TPostResult[Context, IR.Jump] = {
      o match {
        case o: IR.Jump.Goto => return postIRJumpGoto(ctx, o)
        case o: IR.Jump.If => return postIRJumpIf(ctx, o)
        case o: IR.Jump.Return => return postIRJumpReturn(ctx, o)
        case o: IR.Jump.Switch => return postIRJumpSwitch(ctx, o)
        case o: IR.Jump.Intrinsic => return postIRJumpIntrinsic(ctx, o)
      }
    }

    @pure def postIRJumpGoto(ctx: Context, o: IR.Jump.Goto): TPostResult[Context, IR.Jump] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRJumpIf(ctx: Context, o: IR.Jump.If): TPostResult[Context, IR.Jump] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRJumpReturn(ctx: Context, o: IR.Jump.Return): TPostResult[Context, IR.Jump] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRJumpSwitch(ctx: Context, o: IR.Jump.Switch): TPostResult[Context, IR.Jump] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRJumpSwitchCase(ctx: Context, o: IR.Jump.Switch.Case): TPostResult[Context, IR.Jump.Switch.Case] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRJumpIntrinsic(ctx: Context, o: IR.Jump.Intrinsic): TPostResult[Context, IR.Jump] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRJumpIntrinsicType(ctx: Context, o: IR.Jump.Intrinsic.Type): TPostResult[Context, IR.Jump.Intrinsic.Type] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRBasicBlock(ctx: Context, o: IR.BasicBlock): TPostResult[Context, IR.BasicBlock] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRBody(ctx: Context, o: IR.Body): TPostResult[Context, IR.Body] = {
      o match {
        case o: IR.Body.Block => return postIRBodyBlock(ctx, o)
        case o: IR.Body.Basic => return postIRBodyBasic(ctx, o)
      }
    }

    @pure def postIRBodyBlock(ctx: Context, o: IR.Body.Block): TPostResult[Context, IR.Body] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRBodyBasic(ctx: Context, o: IR.Body.Basic): TPostResult[Context, IR.Body] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRProcedure(ctx: Context, o: IR.Procedure): TPostResult[Context, IR.Procedure] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRGlobal(ctx: Context, o: IR.Global): TPostResult[Context, IR.Global] = {
      return TPostResult(ctx, None())
    }

    @pure def postIRProgram(ctx: Context, o: IR.Program): TPostResult[Context, IR.Program] = {
      return TPostResult(ctx, None())
    }

    @pure def postTyped(ctx: Context, o: Typed): TPostResult[Context, Typed] = {
      o match {
        case o: Typed.Name => return postTypedName(ctx, o)
        case o: Typed.Tuple => return postTypedTuple(ctx, o)
        case o: Typed.Fun => return postTypedFun(ctx, o)
        case o: Typed.TypeVar => return postTypedTypeVar(ctx, o)
        case o: Typed.Package => return postTypedPackage(ctx, o)
        case o: Typed.Object => return postTypedObject(ctx, o)
        case o: Typed.Enum => return postTypedEnum(ctx, o)
        case o: Typed.Method => return postTypedMethod(ctx, o)
        case o: Typed.Methods => return postTypedMethods(ctx, o)
        case o: Typed.Fact => return postTypedFact(ctx, o)
        case o: Typed.Theorem => return postTypedTheorem(ctx, o)
        case o: Typed.Inv => return postTypedInv(ctx, o)
      }
    }

    @pure def postTypedName(ctx: Context, o: Typed.Name): TPostResult[Context, Typed] = {
      return TPostResult(ctx, None())
    }

    @pure def postTypedTuple(ctx: Context, o: Typed.Tuple): TPostResult[Context, Typed] = {
      return TPostResult(ctx, None())
    }

    @pure def postTypedFun(ctx: Context, o: Typed.Fun): TPostResult[Context, Typed] = {
      return TPostResult(ctx, None())
    }

    @pure def postTypedTypeVar(ctx: Context, o: Typed.TypeVar): TPostResult[Context, Typed] = {
      return TPostResult(ctx, None())
    }

    @pure def postTypedPackage(ctx: Context, o: Typed.Package): TPostResult[Context, Typed] = {
      return TPostResult(ctx, None())
    }

    @pure def postTypedObject(ctx: Context, o: Typed.Object): TPostResult[Context, Typed] = {
      return TPostResult(ctx, None())
    }

    @pure def postTypedEnum(ctx: Context, o: Typed.Enum): TPostResult[Context, Typed] = {
      return TPostResult(ctx, None())
    }

    @pure def postTypedMethod(ctx: Context, o: Typed.Method): TPostResult[Context, Typed] = {
      return TPostResult(ctx, None())
    }

    @pure def postTypedMethods(ctx: Context, o: Typed.Methods): TPostResult[Context, Typed] = {
      return TPostResult(ctx, None())
    }

    @pure def postTypedFact(ctx: Context, o: Typed.Fact): TPostResult[Context, Typed] = {
      return TPostResult(ctx, None())
    }

    @pure def postTypedTheorem(ctx: Context, o: Typed.Theorem): TPostResult[Context, Typed] = {
      return TPostResult(ctx, None())
    }

    @pure def postTypedInv(ctx: Context, o: Typed.Inv): TPostResult[Context, Typed] = {
      return TPostResult(ctx, None())
    }

  }

  @pure def transformISZ[Context, T](ctx: Context, s: IS[Z, T], f: (Context, T) => TPostResult[Context, T] @pure): TPostResult[Context, IS[Z, T]] = {
    val s2: MS[Z, T] = s.toMS
    var changed: B = F
    var ctxi = ctx
    for (i <- s2.indices) {
      val e: T = s(i)
      val r: TPostResult[Context, T] = f(ctxi, e)
      ctxi = r.ctx
      changed = changed || r.resultOpt.nonEmpty
      s2(i) = r.resultOpt.getOrElse(e)
    }
    if (changed) {
      return TPostResult(ctxi, Some(s2.toIS))
    } else {
      return TPostResult[Context, IS[Z, T]](ctxi, None[IS[Z, T]]())
    }
  }

  @pure def transformOption[Context, T](ctx: Context, option: Option[T], f: (Context, T) => TPostResult[Context, T] @pure): TPostResult[Context, Option[T]] = {
    option match {
      case Some(v) =>
        val r = f(ctx, v)
        r.resultOpt match {
          case Some(_) => return TPostResult(r.ctx, Some(r.resultOpt))
          case _ => return TPostResult[Context, Option[T]](r.ctx, None[Option[T]]())
        }
      case _ => return TPostResult[Context, Option[T]](ctx, None[Option[T]]())
    }
  }

}

import IRTransformer._

@datatype class IRTransformer[Context](val pp: PrePost[Context]) {

  @pure def transformIRMethodContext(ctx: Context, o: IR.MethodContext): TPostResult[Context, IR.MethodContext] = {
    val preR: PreResult[Context, IR.MethodContext] = pp.preIRMethodContext(ctx, o)
    val r: TPostResult[Context, IR.MethodContext] = if (preR.continu) {
      val o2: IR.MethodContext = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, Typed.Fun] = transformTypedFun(preR.ctx, o2.t)
      if (hasChanged || r0.resultOpt.nonEmpty)
        TPostResult(r0.ctx, Some(o2(t = r0.resultOpt.getOrElse(o2.t))))
      else
        TPostResult(r0.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: IR.MethodContext = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, IR.MethodContext] = pp.postIRMethodContext(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformIRExp(ctx: Context, o: IR.Exp): TPostResult[Context, IR.Exp] = {
    val preR: PreResult[Context, IR.Exp] = pp.preIRExp(ctx, o)
    val r: TPostResult[Context, IR.Exp] = if (preR.continu) {
      val o2: IR.Exp = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: TPostResult[Context, IR.Exp] = o2 match {
        case o2: IR.Exp.Bool =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: IR.Exp.Int =>
          val r0: TPostResult[Context, Typed] = transformTyped(preR.ctx, o2.tipe)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(tipe = r0.resultOpt.getOrElse(o2.tipe))))
          else
            TPostResult(r0.ctx, None())
        case o2: IR.Exp.F32 =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: IR.Exp.F64 =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: IR.Exp.R =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: IR.Exp.String =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: IR.Exp.Temp =>
          val r0: TPostResult[Context, Typed] = transformTyped(preR.ctx, o2.tipe)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(tipe = r0.resultOpt.getOrElse(o2.tipe))))
          else
            TPostResult(r0.ctx, None())
        case o2: IR.Exp.LocalVarRef =>
          val r0: TPostResult[Context, IR.MethodContext] = transformIRMethodContext(preR.ctx, o2.context)
          val r1: TPostResult[Context, Typed] = transformTyped(r0.ctx, o2.tipe)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(context = r0.resultOpt.getOrElse(o2.context), tipe = r1.resultOpt.getOrElse(o2.tipe))))
          else
            TPostResult(r1.ctx, None())
        case o2: IR.Exp.GlobalVarRef =>
          val r0: TPostResult[Context, Typed] = transformTyped(preR.ctx, o2.tipe)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(tipe = r0.resultOpt.getOrElse(o2.tipe))))
          else
            TPostResult(r0.ctx, None())
        case o2: IR.Exp.EnumElementRef =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: IR.Exp.FieldVarRef =>
          val r0: TPostResult[Context, IR.Exp] = transformIRExp(preR.ctx, o2.receiver)
          val r1: TPostResult[Context, Typed] = transformTyped(r0.ctx, o2.tipe)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(receiver = r0.resultOpt.getOrElse(o2.receiver), tipe = r1.resultOpt.getOrElse(o2.tipe))))
          else
            TPostResult(r1.ctx, None())
        case o2: IR.Exp.Unary =>
          val r0: TPostResult[Context, Typed] = transformTyped(preR.ctx, o2.tipe)
          val r1: TPostResult[Context, IR.Exp] = transformIRExp(r0.ctx, o2.exp)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(tipe = r0.resultOpt.getOrElse(o2.tipe), exp = r1.resultOpt.getOrElse(o2.exp))))
          else
            TPostResult(r1.ctx, None())
        case o2: IR.Exp.Binary =>
          val r0: TPostResult[Context, Typed] = transformTyped(preR.ctx, o2.tipe)
          val r1: TPostResult[Context, IR.Exp] = transformIRExp(r0.ctx, o2.left)
          val r2: TPostResult[Context, IR.Exp] = transformIRExp(r1.ctx, o2.right)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(tipe = r0.resultOpt.getOrElse(o2.tipe), left = r1.resultOpt.getOrElse(o2.left), right = r2.resultOpt.getOrElse(o2.right))))
          else
            TPostResult(r2.ctx, None())
        case o2: IR.Exp.If =>
          val r0: TPostResult[Context, IR.Exp] = transformIRExp(preR.ctx, o2.cond)
          val r1: TPostResult[Context, IR.Exp] = transformIRExp(r0.ctx, o2.thenExp)
          val r2: TPostResult[Context, IR.Exp] = transformIRExp(r1.ctx, o2.elseExp)
          val r3: TPostResult[Context, Typed] = transformTyped(r2.ctx, o2.tipe)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty || r3.resultOpt.nonEmpty)
            TPostResult(r3.ctx, Some(o2(cond = r0.resultOpt.getOrElse(o2.cond), thenExp = r1.resultOpt.getOrElse(o2.thenExp), elseExp = r2.resultOpt.getOrElse(o2.elseExp), tipe = r3.resultOpt.getOrElse(o2.tipe))))
          else
            TPostResult(r3.ctx, None())
        case o2: IR.Exp.Construct =>
          val r0: TPostResult[Context, Typed.Name] = transformTypedName(preR.ctx, o2.tipe)
          val r1: TPostResult[Context, IS[Z, IR.Exp]] = transformISZ(r0.ctx, o2.args, transformIRExp _)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(tipe = r0.resultOpt.getOrElse(o2.tipe), args = r1.resultOpt.getOrElse(o2.args))))
          else
            TPostResult(r1.ctx, None())
        case o2: IR.Exp.Apply =>
          val r0: TPostResult[Context, IS[Z, IR.Exp]] = transformISZ(preR.ctx, o2.args, transformIRExp _)
          val r1: TPostResult[Context, Typed.Fun] = transformTypedFun(r0.ctx, o2.methodType)
          val r2: TPostResult[Context, Typed] = transformTyped(r1.ctx, o2.tipe)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(args = r0.resultOpt.getOrElse(o2.args), methodType = r1.resultOpt.getOrElse(o2.methodType), tipe = r2.resultOpt.getOrElse(o2.tipe))))
          else
            TPostResult(r2.ctx, None())
        case o2: IR.Exp.Indexing =>
          val r0: TPostResult[Context, IR.Exp] = transformIRExp(preR.ctx, o2.exp)
          val r1: TPostResult[Context, Typed] = transformTyped(r0.ctx, o2.tipe)
          val r2: TPostResult[Context, IR.Exp] = transformIRExp(r1.ctx, o2.index)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(exp = r0.resultOpt.getOrElse(o2.exp), tipe = r1.resultOpt.getOrElse(o2.tipe), index = r2.resultOpt.getOrElse(o2.index))))
          else
            TPostResult(r2.ctx, None())
        case o2: IR.Exp.Type =>
          val r0: TPostResult[Context, IR.Exp] = transformIRExp(preR.ctx, o2.exp)
          val r1: TPostResult[Context, Typed.Name] = transformTypedName(r0.ctx, o2.t)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(exp = r0.resultOpt.getOrElse(o2.exp), t = r1.resultOpt.getOrElse(o2.t))))
          else
            TPostResult(r1.ctx, None())
        case o2: IR.Exp.Intrinsic =>
          val r0: TPostResult[Context, IR.Exp.Intrinsic.Type] = transformIRExpIntrinsicType(preR.ctx, o2.intrinsic)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(intrinsic = r0.resultOpt.getOrElse(o2.intrinsic))))
          else
            TPostResult(r0.ctx, None())
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: IR.Exp = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, IR.Exp] = pp.postIRExp(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformIRExpIntrinsicType(ctx: Context, o: IR.Exp.Intrinsic.Type): TPostResult[Context, IR.Exp.Intrinsic.Type] = {
    val preR: PreResult[Context, IR.Exp.Intrinsic.Type] = pp.preIRExpIntrinsicType(ctx, o)
    val r: TPostResult[Context, IR.Exp.Intrinsic.Type] = if (preR.continu) {
      val o2: IR.Exp.Intrinsic.Type = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: TPostResult[Context, IR.Exp.Intrinsic.Type] = TPostResult(ctx, None())
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: IR.Exp.Intrinsic.Type = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, IR.Exp.Intrinsic.Type] = pp.postIRExpIntrinsicType(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformIRStmt(ctx: Context, o: IR.Stmt): TPostResult[Context, IR.Stmt] = {
    val preR: PreResult[Context, IR.Stmt] = pp.preIRStmt(ctx, o)
    val r: TPostResult[Context, IR.Stmt] = if (preR.continu) {
      val o2: IR.Stmt = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: TPostResult[Context, IR.Stmt] = o2 match {
        case o2: IR.Stmt.Expr =>
          val r0: TPostResult[Context, IR.Exp.Apply] = transformIRExpApply(preR.ctx, o2.exp)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(exp = r0.resultOpt.getOrElse(o2.exp))))
          else
            TPostResult(r0.ctx, None())
        case o2: IR.Stmt.Assign.Local =>
          val r0: TPostResult[Context, IR.MethodContext] = transformIRMethodContext(preR.ctx, o2.context)
          val r1: TPostResult[Context, Typed] = transformTyped(r0.ctx, o2.tipe)
          val r2: TPostResult[Context, IR.Exp] = transformIRExp(r1.ctx, o2.rhs)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(context = r0.resultOpt.getOrElse(o2.context), tipe = r1.resultOpt.getOrElse(o2.tipe), rhs = r2.resultOpt.getOrElse(o2.rhs))))
          else
            TPostResult(r2.ctx, None())
        case o2: IR.Stmt.Assign.Global =>
          val r0: TPostResult[Context, Typed] = transformTyped(preR.ctx, o2.tipe)
          val r1: TPostResult[Context, IR.Exp] = transformIRExp(r0.ctx, o2.rhs)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(tipe = r0.resultOpt.getOrElse(o2.tipe), rhs = r1.resultOpt.getOrElse(o2.rhs))))
          else
            TPostResult(r1.ctx, None())
        case o2: IR.Stmt.Assign.Temp =>
          val r0: TPostResult[Context, IR.Exp] = transformIRExp(preR.ctx, o2.rhs)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(rhs = r0.resultOpt.getOrElse(o2.rhs))))
          else
            TPostResult(r0.ctx, None())
        case o2: IR.Stmt.Assign.Field =>
          val r0: TPostResult[Context, IR.Exp] = transformIRExp(preR.ctx, o2.receiver)
          val r1: TPostResult[Context, Typed] = transformTyped(r0.ctx, o2.tipe)
          val r2: TPostResult[Context, IR.Exp] = transformIRExp(r1.ctx, o2.rhs)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(receiver = r0.resultOpt.getOrElse(o2.receiver), tipe = r1.resultOpt.getOrElse(o2.tipe), rhs = r2.resultOpt.getOrElse(o2.rhs))))
          else
            TPostResult(r2.ctx, None())
        case o2: IR.Stmt.Assign.Index =>
          val r0: TPostResult[Context, IR.Exp] = transformIRExp(preR.ctx, o2.receiver)
          val r1: TPostResult[Context, IR.Exp] = transformIRExp(r0.ctx, o2.index)
          val r2: TPostResult[Context, IR.Exp] = transformIRExp(r1.ctx, o2.rhs)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(receiver = r0.resultOpt.getOrElse(o2.receiver), index = r1.resultOpt.getOrElse(o2.index), rhs = r2.resultOpt.getOrElse(o2.rhs))))
          else
            TPostResult(r2.ctx, None())
        case o2: IR.Stmt.If =>
          val r0: TPostResult[Context, IR.Exp] = transformIRExp(preR.ctx, o2.cond)
          val r1: TPostResult[Context, IR.Stmt.Block] = transformIRStmtBlock(r0.ctx, o2.thenBlock)
          val r2: TPostResult[Context, IR.Stmt.Block] = transformIRStmtBlock(r1.ctx, o2.elseBlock)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(cond = r0.resultOpt.getOrElse(o2.cond), thenBlock = r1.resultOpt.getOrElse(o2.thenBlock), elseBlock = r2.resultOpt.getOrElse(o2.elseBlock))))
          else
            TPostResult(r2.ctx, None())
        case o2: IR.Stmt.Block =>
          val r0: TPostResult[Context, IS[Z, IR.Stmt]] = transformISZ(preR.ctx, o2.stmts, transformIRStmt _)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(stmts = r0.resultOpt.getOrElse(o2.stmts))))
          else
            TPostResult(r0.ctx, None())
        case o2: IR.Stmt.While =>
          val r0: TPostResult[Context, IR.Stmt.Block] = transformIRStmtBlock(preR.ctx, o2.condBlock)
          val r1: TPostResult[Context, IR.Exp] = transformIRExp(r0.ctx, o2.cond)
          val r2: TPostResult[Context, IR.Stmt.Block] = transformIRStmtBlock(r1.ctx, o2.block)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(condBlock = r0.resultOpt.getOrElse(o2.condBlock), cond = r1.resultOpt.getOrElse(o2.cond), block = r2.resultOpt.getOrElse(o2.block))))
          else
            TPostResult(r2.ctx, None())
        case o2: IR.Stmt.Return =>
          val r0: TPostResult[Context, Option[IR.Exp]] = transformOption(preR.ctx, o2.expOpt, transformIRExp _)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(expOpt = r0.resultOpt.getOrElse(o2.expOpt))))
          else
            TPostResult(r0.ctx, None())
        case o2: IR.Stmt.Halt =>
          val r0: TPostResult[Context, Option[IR.Exp]] = transformOption(preR.ctx, o2.messageOpt, transformIRExp _)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(messageOpt = r0.resultOpt.getOrElse(o2.messageOpt))))
          else
            TPostResult(r0.ctx, None())
        case o2: IR.Stmt.Decl =>
          val r0: TPostResult[Context, IR.MethodContext] = transformIRMethodContext(preR.ctx, o2.context)
          val r1: TPostResult[Context, IS[Z, IR.Stmt.Decl.Local]] = transformISZ(r0.ctx, o2.locals, transformIRStmtDeclLocal _)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(context = r0.resultOpt.getOrElse(o2.context), locals = r1.resultOpt.getOrElse(o2.locals))))
          else
            TPostResult(r1.ctx, None())
        case o2: IR.Stmt.Intrinsic =>
          val r0: TPostResult[Context, IR.Stmt.Intrinsic.Type] = transformIRStmtIntrinsicType(preR.ctx, o2.intrinsic)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(intrinsic = r0.resultOpt.getOrElse(o2.intrinsic))))
          else
            TPostResult(r0.ctx, None())
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: IR.Stmt = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, IR.Stmt] = pp.postIRStmt(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformIRStmtGround(ctx: Context, o: IR.Stmt.Ground): TPostResult[Context, IR.Stmt.Ground] = {
    val preR: PreResult[Context, IR.Stmt.Ground] = pp.preIRStmtGround(ctx, o)
    val r: TPostResult[Context, IR.Stmt.Ground] = if (preR.continu) {
      val o2: IR.Stmt.Ground = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: TPostResult[Context, IR.Stmt.Ground] = o2 match {
        case o2: IR.Stmt.Expr =>
          val r0: TPostResult[Context, IR.Exp.Apply] = transformIRExpApply(preR.ctx, o2.exp)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(exp = r0.resultOpt.getOrElse(o2.exp))))
          else
            TPostResult(r0.ctx, None())
        case o2: IR.Stmt.Assign.Local =>
          val r0: TPostResult[Context, IR.MethodContext] = transformIRMethodContext(preR.ctx, o2.context)
          val r1: TPostResult[Context, Typed] = transformTyped(r0.ctx, o2.tipe)
          val r2: TPostResult[Context, IR.Exp] = transformIRExp(r1.ctx, o2.rhs)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(context = r0.resultOpt.getOrElse(o2.context), tipe = r1.resultOpt.getOrElse(o2.tipe), rhs = r2.resultOpt.getOrElse(o2.rhs))))
          else
            TPostResult(r2.ctx, None())
        case o2: IR.Stmt.Assign.Global =>
          val r0: TPostResult[Context, Typed] = transformTyped(preR.ctx, o2.tipe)
          val r1: TPostResult[Context, IR.Exp] = transformIRExp(r0.ctx, o2.rhs)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(tipe = r0.resultOpt.getOrElse(o2.tipe), rhs = r1.resultOpt.getOrElse(o2.rhs))))
          else
            TPostResult(r1.ctx, None())
        case o2: IR.Stmt.Assign.Temp =>
          val r0: TPostResult[Context, IR.Exp] = transformIRExp(preR.ctx, o2.rhs)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(rhs = r0.resultOpt.getOrElse(o2.rhs))))
          else
            TPostResult(r0.ctx, None())
        case o2: IR.Stmt.Assign.Field =>
          val r0: TPostResult[Context, IR.Exp] = transformIRExp(preR.ctx, o2.receiver)
          val r1: TPostResult[Context, Typed] = transformTyped(r0.ctx, o2.tipe)
          val r2: TPostResult[Context, IR.Exp] = transformIRExp(r1.ctx, o2.rhs)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(receiver = r0.resultOpt.getOrElse(o2.receiver), tipe = r1.resultOpt.getOrElse(o2.tipe), rhs = r2.resultOpt.getOrElse(o2.rhs))))
          else
            TPostResult(r2.ctx, None())
        case o2: IR.Stmt.Assign.Index =>
          val r0: TPostResult[Context, IR.Exp] = transformIRExp(preR.ctx, o2.receiver)
          val r1: TPostResult[Context, IR.Exp] = transformIRExp(r0.ctx, o2.index)
          val r2: TPostResult[Context, IR.Exp] = transformIRExp(r1.ctx, o2.rhs)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(receiver = r0.resultOpt.getOrElse(o2.receiver), index = r1.resultOpt.getOrElse(o2.index), rhs = r2.resultOpt.getOrElse(o2.rhs))))
          else
            TPostResult(r2.ctx, None())
        case o2: IR.Stmt.Halt =>
          val r0: TPostResult[Context, Option[IR.Exp]] = transformOption(preR.ctx, o2.messageOpt, transformIRExp _)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(messageOpt = r0.resultOpt.getOrElse(o2.messageOpt))))
          else
            TPostResult(r0.ctx, None())
        case o2: IR.Stmt.Decl =>
          val r0: TPostResult[Context, IR.MethodContext] = transformIRMethodContext(preR.ctx, o2.context)
          val r1: TPostResult[Context, IS[Z, IR.Stmt.Decl.Local]] = transformISZ(r0.ctx, o2.locals, transformIRStmtDeclLocal _)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(context = r0.resultOpt.getOrElse(o2.context), locals = r1.resultOpt.getOrElse(o2.locals))))
          else
            TPostResult(r1.ctx, None())
        case o2: IR.Stmt.Intrinsic =>
          val r0: TPostResult[Context, IR.Stmt.Intrinsic.Type] = transformIRStmtIntrinsicType(preR.ctx, o2.intrinsic)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(intrinsic = r0.resultOpt.getOrElse(o2.intrinsic))))
          else
            TPostResult(r0.ctx, None())
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: IR.Stmt.Ground = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, IR.Stmt.Ground] = pp.postIRStmtGround(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformIRStmtAssign(ctx: Context, o: IR.Stmt.Assign): TPostResult[Context, IR.Stmt.Assign] = {
    val preR: PreResult[Context, IR.Stmt.Assign] = pp.preIRStmtAssign(ctx, o)
    val r: TPostResult[Context, IR.Stmt.Assign] = if (preR.continu) {
      val o2: IR.Stmt.Assign = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: TPostResult[Context, IR.Stmt.Assign] = o2 match {
        case o2: IR.Stmt.Assign.Local =>
          val r0: TPostResult[Context, IR.MethodContext] = transformIRMethodContext(preR.ctx, o2.context)
          val r1: TPostResult[Context, Typed] = transformTyped(r0.ctx, o2.tipe)
          val r2: TPostResult[Context, IR.Exp] = transformIRExp(r1.ctx, o2.rhs)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(context = r0.resultOpt.getOrElse(o2.context), tipe = r1.resultOpt.getOrElse(o2.tipe), rhs = r2.resultOpt.getOrElse(o2.rhs))))
          else
            TPostResult(r2.ctx, None())
        case o2: IR.Stmt.Assign.Global =>
          val r0: TPostResult[Context, Typed] = transformTyped(preR.ctx, o2.tipe)
          val r1: TPostResult[Context, IR.Exp] = transformIRExp(r0.ctx, o2.rhs)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(tipe = r0.resultOpt.getOrElse(o2.tipe), rhs = r1.resultOpt.getOrElse(o2.rhs))))
          else
            TPostResult(r1.ctx, None())
        case o2: IR.Stmt.Assign.Temp =>
          val r0: TPostResult[Context, IR.Exp] = transformIRExp(preR.ctx, o2.rhs)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(rhs = r0.resultOpt.getOrElse(o2.rhs))))
          else
            TPostResult(r0.ctx, None())
        case o2: IR.Stmt.Assign.Field =>
          val r0: TPostResult[Context, IR.Exp] = transformIRExp(preR.ctx, o2.receiver)
          val r1: TPostResult[Context, Typed] = transformTyped(r0.ctx, o2.tipe)
          val r2: TPostResult[Context, IR.Exp] = transformIRExp(r1.ctx, o2.rhs)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(receiver = r0.resultOpt.getOrElse(o2.receiver), tipe = r1.resultOpt.getOrElse(o2.tipe), rhs = r2.resultOpt.getOrElse(o2.rhs))))
          else
            TPostResult(r2.ctx, None())
        case o2: IR.Stmt.Assign.Index =>
          val r0: TPostResult[Context, IR.Exp] = transformIRExp(preR.ctx, o2.receiver)
          val r1: TPostResult[Context, IR.Exp] = transformIRExp(r0.ctx, o2.index)
          val r2: TPostResult[Context, IR.Exp] = transformIRExp(r1.ctx, o2.rhs)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(receiver = r0.resultOpt.getOrElse(o2.receiver), index = r1.resultOpt.getOrElse(o2.index), rhs = r2.resultOpt.getOrElse(o2.rhs))))
          else
            TPostResult(r2.ctx, None())
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: IR.Stmt.Assign = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, IR.Stmt.Assign] = pp.postIRStmtAssign(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformIRStmtDeclLocal(ctx: Context, o: IR.Stmt.Decl.Local): TPostResult[Context, IR.Stmt.Decl.Local] = {
    val preR: PreResult[Context, IR.Stmt.Decl.Local] = pp.preIRStmtDeclLocal(ctx, o)
    val r: TPostResult[Context, IR.Stmt.Decl.Local] = if (preR.continu) {
      val o2: IR.Stmt.Decl.Local = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, Typed] = transformTyped(preR.ctx, o2.tipe)
      if (hasChanged || r0.resultOpt.nonEmpty)
        TPostResult(r0.ctx, Some(o2(tipe = r0.resultOpt.getOrElse(o2.tipe))))
      else
        TPostResult(r0.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: IR.Stmt.Decl.Local = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, IR.Stmt.Decl.Local] = pp.postIRStmtDeclLocal(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformIRStmtIntrinsicType(ctx: Context, o: IR.Stmt.Intrinsic.Type): TPostResult[Context, IR.Stmt.Intrinsic.Type] = {
    val preR: PreResult[Context, IR.Stmt.Intrinsic.Type] = pp.preIRStmtIntrinsicType(ctx, o)
    val r: TPostResult[Context, IR.Stmt.Intrinsic.Type] = if (preR.continu) {
      val o2: IR.Stmt.Intrinsic.Type = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: TPostResult[Context, IR.Stmt.Intrinsic.Type] = TPostResult(ctx, None())
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: IR.Stmt.Intrinsic.Type = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, IR.Stmt.Intrinsic.Type] = pp.postIRStmtIntrinsicType(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformIRJump(ctx: Context, o: IR.Jump): TPostResult[Context, IR.Jump] = {
    val preR: PreResult[Context, IR.Jump] = pp.preIRJump(ctx, o)
    val r: TPostResult[Context, IR.Jump] = if (preR.continu) {
      val o2: IR.Jump = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: TPostResult[Context, IR.Jump] = o2 match {
        case o2: IR.Jump.Goto =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: IR.Jump.If =>
          val r0: TPostResult[Context, IR.Exp] = transformIRExp(preR.ctx, o2.cond)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(cond = r0.resultOpt.getOrElse(o2.cond))))
          else
            TPostResult(r0.ctx, None())
        case o2: IR.Jump.Return =>
          val r0: TPostResult[Context, Option[IR.Exp]] = transformOption(preR.ctx, o2.expOpt, transformIRExp _)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(expOpt = r0.resultOpt.getOrElse(o2.expOpt))))
          else
            TPostResult(r0.ctx, None())
        case o2: IR.Jump.Switch =>
          val r0: TPostResult[Context, IR.Exp] = transformIRExp(preR.ctx, o2.exp)
          val r1: TPostResult[Context, IS[Z, IR.Jump.Switch.Case]] = transformISZ(r0.ctx, o2.cases, transformIRJumpSwitchCase _)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(exp = r0.resultOpt.getOrElse(o2.exp), cases = r1.resultOpt.getOrElse(o2.cases))))
          else
            TPostResult(r1.ctx, None())
        case o2: IR.Jump.Intrinsic =>
          val r0: TPostResult[Context, IR.Jump.Intrinsic.Type] = transformIRJumpIntrinsicType(preR.ctx, o2.intrinsic)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(intrinsic = r0.resultOpt.getOrElse(o2.intrinsic))))
          else
            TPostResult(r0.ctx, None())
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: IR.Jump = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, IR.Jump] = pp.postIRJump(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformIRJumpSwitchCase(ctx: Context, o: IR.Jump.Switch.Case): TPostResult[Context, IR.Jump.Switch.Case] = {
    val preR: PreResult[Context, IR.Jump.Switch.Case] = pp.preIRJumpSwitchCase(ctx, o)
    val r: TPostResult[Context, IR.Jump.Switch.Case] = if (preR.continu) {
      val o2: IR.Jump.Switch.Case = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, IR.Exp] = transformIRExp(preR.ctx, o2.value)
      if (hasChanged || r0.resultOpt.nonEmpty)
        TPostResult(r0.ctx, Some(o2(value = r0.resultOpt.getOrElse(o2.value))))
      else
        TPostResult(r0.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: IR.Jump.Switch.Case = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, IR.Jump.Switch.Case] = pp.postIRJumpSwitchCase(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformIRJumpIntrinsicType(ctx: Context, o: IR.Jump.Intrinsic.Type): TPostResult[Context, IR.Jump.Intrinsic.Type] = {
    val preR: PreResult[Context, IR.Jump.Intrinsic.Type] = pp.preIRJumpIntrinsicType(ctx, o)
    val r: TPostResult[Context, IR.Jump.Intrinsic.Type] = if (preR.continu) {
      val o2: IR.Jump.Intrinsic.Type = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: TPostResult[Context, IR.Jump.Intrinsic.Type] = TPostResult(ctx, None())
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: IR.Jump.Intrinsic.Type = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, IR.Jump.Intrinsic.Type] = pp.postIRJumpIntrinsicType(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformIRBasicBlock(ctx: Context, o: IR.BasicBlock): TPostResult[Context, IR.BasicBlock] = {
    val preR: PreResult[Context, IR.BasicBlock] = pp.preIRBasicBlock(ctx, o)
    val r: TPostResult[Context, IR.BasicBlock] = if (preR.continu) {
      val o2: IR.BasicBlock = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, IS[Z, IR.Stmt.Ground]] = transformISZ(preR.ctx, o2.grounds, transformIRStmtGround _)
      val r1: TPostResult[Context, IR.Jump] = transformIRJump(r0.ctx, o2.jump)
      if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
        TPostResult(r1.ctx, Some(o2(grounds = r0.resultOpt.getOrElse(o2.grounds), jump = r1.resultOpt.getOrElse(o2.jump))))
      else
        TPostResult(r1.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: IR.BasicBlock = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, IR.BasicBlock] = pp.postIRBasicBlock(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformIRBody(ctx: Context, o: IR.Body): TPostResult[Context, IR.Body] = {
    val preR: PreResult[Context, IR.Body] = pp.preIRBody(ctx, o)
    val r: TPostResult[Context, IR.Body] = if (preR.continu) {
      val o2: IR.Body = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: TPostResult[Context, IR.Body] = o2 match {
        case o2: IR.Body.Block =>
          val r0: TPostResult[Context, IR.Stmt.Block] = transformIRStmtBlock(preR.ctx, o2.block)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(block = r0.resultOpt.getOrElse(o2.block))))
          else
            TPostResult(r0.ctx, None())
        case o2: IR.Body.Basic =>
          val r0: TPostResult[Context, IS[Z, IR.BasicBlock]] = transformISZ(preR.ctx, o2.blocks, transformIRBasicBlock _)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(blocks = r0.resultOpt.getOrElse(o2.blocks))))
          else
            TPostResult(r0.ctx, None())
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: IR.Body = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, IR.Body] = pp.postIRBody(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformIRProcedure(ctx: Context, o: IR.Procedure): TPostResult[Context, IR.Procedure] = {
    val preR: PreResult[Context, IR.Procedure] = pp.preIRProcedure(ctx, o)
    val r: TPostResult[Context, IR.Procedure] = if (preR.continu) {
      val o2: IR.Procedure = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, Typed.Fun] = transformTypedFun(preR.ctx, o2.tipe)
      val r1: TPostResult[Context, IR.Body] = transformIRBody(r0.ctx, o2.body)
      if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
        TPostResult(r1.ctx, Some(o2(tipe = r0.resultOpt.getOrElse(o2.tipe), body = r1.resultOpt.getOrElse(o2.body))))
      else
        TPostResult(r1.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: IR.Procedure = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, IR.Procedure] = pp.postIRProcedure(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformIRGlobal(ctx: Context, o: IR.Global): TPostResult[Context, IR.Global] = {
    val preR: PreResult[Context, IR.Global] = pp.preIRGlobal(ctx, o)
    val r: TPostResult[Context, IR.Global] = if (preR.continu) {
      val o2: IR.Global = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, Typed] = transformTyped(preR.ctx, o2.tipe)
      if (hasChanged || r0.resultOpt.nonEmpty)
        TPostResult(r0.ctx, Some(o2(tipe = r0.resultOpt.getOrElse(o2.tipe))))
      else
        TPostResult(r0.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: IR.Global = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, IR.Global] = pp.postIRGlobal(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformIRProgram(ctx: Context, o: IR.Program): TPostResult[Context, IR.Program] = {
    val preR: PreResult[Context, IR.Program] = pp.preIRProgram(ctx, o)
    val r: TPostResult[Context, IR.Program] = if (preR.continu) {
      val o2: IR.Program = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, IS[Z, IR.Global]] = transformISZ(preR.ctx, o2.globals, transformIRGlobal _)
      val r1: TPostResult[Context, IS[Z, IR.Procedure]] = transformISZ(r0.ctx, o2.procedures, transformIRProcedure _)
      if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
        TPostResult(r1.ctx, Some(o2(globals = r0.resultOpt.getOrElse(o2.globals), procedures = r1.resultOpt.getOrElse(o2.procedures))))
      else
        TPostResult(r1.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: IR.Program = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, IR.Program] = pp.postIRProgram(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformTyped(ctx: Context, o: Typed): TPostResult[Context, Typed] = {
    val preR: PreResult[Context, Typed] = pp.preTyped(ctx, o)
    val r: TPostResult[Context, Typed] = if (preR.continu) {
      val o2: Typed = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: TPostResult[Context, Typed] = o2 match {
        case o2: Typed.Name =>
          val r0: TPostResult[Context, IS[Z, Typed]] = transformISZ(preR.ctx, o2.args, transformTyped _)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(args = r0.resultOpt.getOrElse(o2.args))))
          else
            TPostResult(r0.ctx, None())
        case o2: Typed.Tuple =>
          val r0: TPostResult[Context, IS[Z, Typed]] = transformISZ(preR.ctx, o2.args, transformTyped _)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(args = r0.resultOpt.getOrElse(o2.args))))
          else
            TPostResult(r0.ctx, None())
        case o2: Typed.Fun =>
          val r0: TPostResult[Context, IS[Z, Typed]] = transformISZ(preR.ctx, o2.args, transformTyped _)
          val r1: TPostResult[Context, Typed] = transformTyped(r0.ctx, o2.ret)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(args = r0.resultOpt.getOrElse(o2.args), ret = r1.resultOpt.getOrElse(o2.ret))))
          else
            TPostResult(r1.ctx, None())
        case o2: Typed.TypeVar =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: Typed.Package =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: Typed.Object =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: Typed.Enum =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: Typed.Method =>
          val r0: TPostResult[Context, Typed.Fun] = transformTypedFun(preR.ctx, o2.tpe)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(tpe = r0.resultOpt.getOrElse(o2.tpe))))
          else
            TPostResult(r0.ctx, None())
        case o2: Typed.Methods =>
          val r0: TPostResult[Context, IS[Z, Typed.Method]] = transformISZ(preR.ctx, o2.methods, transformTypedMethod _)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(methods = r0.resultOpt.getOrElse(o2.methods))))
          else
            TPostResult(r0.ctx, None())
        case o2: Typed.Fact =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: Typed.Theorem =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: Typed.Inv =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: Typed = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Typed] = pp.postTyped(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformTypedFun(ctx: Context, o: Typed.Fun): TPostResult[Context, Typed.Fun] = {
    val preR: PreResult[Context, Typed.Fun] = pp.preTypedFun(ctx, o) match {
     case PreResult(preCtx, continu, Some(r: Typed.Fun)) => PreResult(preCtx, continu, Some[Typed.Fun](r))
     case PreResult(_, _, Some(_)) => halt("Can only produce object of type Typed.Fun")
     case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[Typed.Fun]())
    }
    val r: TPostResult[Context, Typed.Fun] = if (preR.continu) {
      val o2: Typed.Fun = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, IS[Z, Typed]] = transformISZ(preR.ctx, o2.args, transformTyped _)
      val r1: TPostResult[Context, Typed] = transformTyped(r0.ctx, o2.ret)
      if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
        TPostResult(r1.ctx, Some(o2(args = r0.resultOpt.getOrElse(o2.args), ret = r1.resultOpt.getOrElse(o2.ret))))
      else
        TPostResult(r1.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: Typed.Fun = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Typed.Fun] = pp.postTypedFun(r.ctx, o2) match {
     case TPostResult(postCtx, Some(result: Typed.Fun)) => TPostResult(postCtx, Some[Typed.Fun](result))
     case TPostResult(_, Some(_)) => halt("Can only produce object of type Typed.Fun")
     case TPostResult(postCtx, _) => TPostResult(postCtx, None[Typed.Fun]())
    }
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformTypedName(ctx: Context, o: Typed.Name): TPostResult[Context, Typed.Name] = {
    val preR: PreResult[Context, Typed.Name] = pp.preTypedName(ctx, o) match {
     case PreResult(preCtx, continu, Some(r: Typed.Name)) => PreResult(preCtx, continu, Some[Typed.Name](r))
     case PreResult(_, _, Some(_)) => halt("Can only produce object of type Typed.Name")
     case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[Typed.Name]())
    }
    val r: TPostResult[Context, Typed.Name] = if (preR.continu) {
      val o2: Typed.Name = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, IS[Z, Typed]] = transformISZ(preR.ctx, o2.args, transformTyped _)
      if (hasChanged || r0.resultOpt.nonEmpty)
        TPostResult(r0.ctx, Some(o2(args = r0.resultOpt.getOrElse(o2.args))))
      else
        TPostResult(r0.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: Typed.Name = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Typed.Name] = pp.postTypedName(r.ctx, o2) match {
     case TPostResult(postCtx, Some(result: Typed.Name)) => TPostResult(postCtx, Some[Typed.Name](result))
     case TPostResult(_, Some(_)) => halt("Can only produce object of type Typed.Name")
     case TPostResult(postCtx, _) => TPostResult(postCtx, None[Typed.Name]())
    }
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformIRExpApply(ctx: Context, o: IR.Exp.Apply): TPostResult[Context, IR.Exp.Apply] = {
    val preR: PreResult[Context, IR.Exp.Apply] = pp.preIRExpApply(ctx, o) match {
     case PreResult(preCtx, continu, Some(r: IR.Exp.Apply)) => PreResult(preCtx, continu, Some[IR.Exp.Apply](r))
     case PreResult(_, _, Some(_)) => halt("Can only produce object of type IR.Exp.Apply")
     case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[IR.Exp.Apply]())
    }
    val r: TPostResult[Context, IR.Exp.Apply] = if (preR.continu) {
      val o2: IR.Exp.Apply = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, IS[Z, IR.Exp]] = transformISZ(preR.ctx, o2.args, transformIRExp _)
      val r1: TPostResult[Context, Typed.Fun] = transformTypedFun(r0.ctx, o2.methodType)
      val r2: TPostResult[Context, Typed] = transformTyped(r1.ctx, o2.tipe)
      if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
        TPostResult(r2.ctx, Some(o2(args = r0.resultOpt.getOrElse(o2.args), methodType = r1.resultOpt.getOrElse(o2.methodType), tipe = r2.resultOpt.getOrElse(o2.tipe))))
      else
        TPostResult(r2.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: IR.Exp.Apply = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, IR.Exp.Apply] = pp.postIRExpApply(r.ctx, o2) match {
     case TPostResult(postCtx, Some(result: IR.Exp.Apply)) => TPostResult(postCtx, Some[IR.Exp.Apply](result))
     case TPostResult(_, Some(_)) => halt("Can only produce object of type IR.Exp.Apply")
     case TPostResult(postCtx, _) => TPostResult(postCtx, None[IR.Exp.Apply]())
    }
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformIRStmtBlock(ctx: Context, o: IR.Stmt.Block): TPostResult[Context, IR.Stmt.Block] = {
    val preR: PreResult[Context, IR.Stmt.Block] = pp.preIRStmtBlock(ctx, o) match {
     case PreResult(preCtx, continu, Some(r: IR.Stmt.Block)) => PreResult(preCtx, continu, Some[IR.Stmt.Block](r))
     case PreResult(_, _, Some(_)) => halt("Can only produce object of type IR.Stmt.Block")
     case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[IR.Stmt.Block]())
    }
    val r: TPostResult[Context, IR.Stmt.Block] = if (preR.continu) {
      val o2: IR.Stmt.Block = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, IS[Z, IR.Stmt]] = transformISZ(preR.ctx, o2.stmts, transformIRStmt _)
      if (hasChanged || r0.resultOpt.nonEmpty)
        TPostResult(r0.ctx, Some(o2(stmts = r0.resultOpt.getOrElse(o2.stmts))))
      else
        TPostResult(r0.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: IR.Stmt.Block = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, IR.Stmt.Block] = pp.postIRStmtBlock(r.ctx, o2) match {
     case TPostResult(postCtx, Some(result: IR.Stmt.Block)) => TPostResult(postCtx, Some[IR.Stmt.Block](result))
     case TPostResult(_, Some(_)) => halt("Can only produce object of type IR.Stmt.Block")
     case TPostResult(postCtx, _) => TPostResult(postCtx, None[IR.Stmt.Block]())
    }
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformTypedMethod(ctx: Context, o: Typed.Method): TPostResult[Context, Typed.Method] = {
    val preR: PreResult[Context, Typed.Method] = pp.preTypedMethod(ctx, o) match {
     case PreResult(preCtx, continu, Some(r: Typed.Method)) => PreResult(preCtx, continu, Some[Typed.Method](r))
     case PreResult(_, _, Some(_)) => halt("Can only produce object of type Typed.Method")
     case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[Typed.Method]())
    }
    val r: TPostResult[Context, Typed.Method] = if (preR.continu) {
      val o2: Typed.Method = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, Typed.Fun] = transformTypedFun(preR.ctx, o2.tpe)
      if (hasChanged || r0.resultOpt.nonEmpty)
        TPostResult(r0.ctx, Some(o2(tpe = r0.resultOpt.getOrElse(o2.tpe))))
      else
        TPostResult(r0.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: Typed.Method = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Typed.Method] = pp.postTypedMethod(r.ctx, o2) match {
     case TPostResult(postCtx, Some(result: Typed.Method)) => TPostResult(postCtx, Some[Typed.Method](result))
     case TPostResult(_, Some(_)) => halt("Can only produce object of type Typed.Method")
     case TPostResult(postCtx, _) => TPostResult(postCtx, None[Typed.Method]())
    }
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

}