// #Sireum
// @formatter:off

/*
 Copyright (c) 2019, Robby, Kansas State University
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

// This file is auto-generated from AST.scala

package org.sireum.lang.ast

import org.sireum._

object Transformer {

  @datatype class PreResult[Context, T](ctx: Context,
                                        continu: B,
                                        resultOpt: Option[T])

  @datatype class TPostResult[Context, T](ctx: Context,
                                     resultOpt: Option[T])

  @sig trait PrePost[Context] {

    @pure def preTopUnit(ctx: Context, o: TopUnit): PreResult[Context, TopUnit] = {
      o match {
        case o: TopUnit.Program => return preTopUnitProgram(ctx, o)
        case o: TopUnit.SequentUnit => return preTopUnitSequentUnit(ctx, o)
        case o: TopUnit.TruthTableUnit => return preTopUnitTruthTableUnit(ctx, o)
      }
    }

    @pure def preTopUnitProgram(ctx: Context, o: TopUnit.Program): PreResult[Context, TopUnit] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTopUnitSequentUnit(ctx: Context, o: TopUnit.SequentUnit): PreResult[Context, TopUnit] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTopUnitTruthTableUnit(ctx: Context, o: TopUnit.TruthTableUnit): PreResult[Context, TopUnit] = {
      return PreResult(ctx, T, None())
    }

    @pure def preStmt(ctx: Context, o: Stmt): PreResult[Context, Stmt] = {
      o match {
        case o: Stmt.Import => return preStmtImport(ctx, o)
        case o: Stmt.Var => return preStmtVar(ctx, o)
        case o: Stmt.VarPattern => return preStmtVarPattern(ctx, o)
        case o: Stmt.SpecVar => return preStmtSpecVar(ctx, o)
        case o: Stmt.Method => return preStmtMethod(ctx, o)
        case o: Stmt.ExtMethod => return preStmtExtMethod(ctx, o)
        case o: Stmt.SpecMethod => return preStmtSpecMethod(ctx, o)
        case o: Stmt.Enum => return preStmtEnum(ctx, o)
        case o: Stmt.SubZ => return preStmtSubZ(ctx, o)
        case o: Stmt.Object => return preStmtObject(ctx, o)
        case o: Stmt.Sig => return preStmtSig(ctx, o)
        case o: Stmt.Adt => return preStmtAdt(ctx, o)
        case o: Stmt.TypeAlias => return preStmtTypeAlias(ctx, o)
        case o: Stmt.Assign => return preStmtAssign(ctx, o)
        case o: Stmt.Block => return preStmtBlock(ctx, o)
        case o: Stmt.If => return preStmtIf(ctx, o)
        case o: Stmt.Match => return preStmtMatch(ctx, o)
        case o: Stmt.While => return preStmtWhile(ctx, o)
        case o: Stmt.DoWhile => return preStmtDoWhile(ctx, o)
        case o: Stmt.For => return preStmtFor(ctx, o)
        case o: Stmt.Return => return preStmtReturn(ctx, o)
        case o: Stmt.LStmt => return preStmtLStmt(ctx, o)
        case o: Stmt.Expr => return preStmtExpr(ctx, o)
      }
    }

    @pure def preContract(ctx: Context, o: Contract): PreResult[Context, Contract] = {
      o match {
        case o: Contract.Simple => return preContractSimple(ctx, o)
        case o: Contract.Cases => return preContractCases(ctx, o)
      }
    }

    @pure def preContractSimple(ctx: Context, o: Contract.Simple): PreResult[Context, Contract] = {
      return PreResult(ctx, T, None())
    }

    @pure def preContractCases(ctx: Context, o: Contract.Cases): PreResult[Context, Contract] = {
      return PreResult(ctx, T, None())
    }

    @pure def preContractCase(ctx: Context, o: Contract.Case): PreResult[Context, Contract.Case] = {
      return PreResult(ctx, T, None())
    }

    @pure def preStmtImport(ctx: Context, o: Stmt.Import): PreResult[Context, Stmt] = {
      return PreResult(ctx, T, None())
    }

    @pure def preStmtImportImporter(ctx: Context, o: Stmt.Import.Importer): PreResult[Context, Stmt.Import.Importer] = {
      return PreResult(ctx, T, None())
    }

    @pure def preStmtImportSelector(ctx: Context, o: Stmt.Import.Selector): PreResult[Context, Stmt.Import.Selector] = {
      o match {
        case o: Stmt.Import.MultiSelector => return preStmtImportMultiSelector(ctx, o)
        case o: Stmt.Import.WildcardSelector => return preStmtImportWildcardSelector(ctx, o)
      }
    }

    @pure def preStmtImportMultiSelector(ctx: Context, o: Stmt.Import.MultiSelector): PreResult[Context, Stmt.Import.Selector] = {
      return PreResult(ctx, T, None())
    }

    @pure def preStmtImportWildcardSelector(ctx: Context, o: Stmt.Import.WildcardSelector): PreResult[Context, Stmt.Import.Selector] = {
      return PreResult(ctx, T, None())
    }

    @pure def preStmtImportNamedSelector(ctx: Context, o: Stmt.Import.NamedSelector): PreResult[Context, Stmt.Import.NamedSelector] = {
      return PreResult(ctx, T, None())
    }

    @pure def preStmtVar(ctx: Context, o: Stmt.Var): PreResult[Context, Stmt] = {
      return PreResult(ctx, T, None())
    }

    @pure def preStmtVarPattern(ctx: Context, o: Stmt.VarPattern): PreResult[Context, Stmt] = {
      return PreResult(ctx, T, None())
    }

    @pure def preStmtSpecVar(ctx: Context, o: Stmt.SpecVar): PreResult[Context, Stmt] = {
      return PreResult(ctx, T, None())
    }

    @pure def preStmtMethod(ctx: Context, o: Stmt.Method): PreResult[Context, Stmt] = {
      return PreResult(ctx, T, None())
    }

    @pure def preStmtExtMethod(ctx: Context, o: Stmt.ExtMethod): PreResult[Context, Stmt] = {
      return PreResult(ctx, T, None())
    }

    @pure def preStmtSpecMethod(ctx: Context, o: Stmt.SpecMethod): PreResult[Context, Stmt] = {
      return PreResult(ctx, T, None())
    }

    @pure def preStmtEnum(ctx: Context, o: Stmt.Enum): PreResult[Context, Stmt] = {
      return PreResult(ctx, T, None())
    }

    @pure def preStmtSubZ(ctx: Context, o: Stmt.SubZ): PreResult[Context, Stmt] = {
      return PreResult(ctx, T, None())
    }

    @pure def preStmtObject(ctx: Context, o: Stmt.Object): PreResult[Context, Stmt] = {
      return PreResult(ctx, T, None())
    }

    @pure def preStmtSig(ctx: Context, o: Stmt.Sig): PreResult[Context, Stmt] = {
      return PreResult(ctx, T, None())
    }

    @pure def preStmtAdt(ctx: Context, o: Stmt.Adt): PreResult[Context, Stmt] = {
      return PreResult(ctx, T, None())
    }

    @pure def preStmtTypeAlias(ctx: Context, o: Stmt.TypeAlias): PreResult[Context, Stmt] = {
      return PreResult(ctx, T, None())
    }

    @pure def preStmtAssign(ctx: Context, o: Stmt.Assign): PreResult[Context, Stmt] = {
      return PreResult(ctx, T, None())
    }

    @pure def preStmtBlock(ctx: Context, o: Stmt.Block): PreResult[Context, Stmt] = {
      return PreResult(ctx, T, None())
    }

    @pure def preStmtIf(ctx: Context, o: Stmt.If): PreResult[Context, Stmt] = {
      return PreResult(ctx, T, None())
    }

    @pure def preStmtMatch(ctx: Context, o: Stmt.Match): PreResult[Context, Stmt] = {
      return PreResult(ctx, T, None())
    }

    @pure def preStmtLoop(ctx: Context, o: Stmt.Loop): PreResult[Context, Stmt.Loop] = {
      o match {
        case o: Stmt.While =>
          val r: PreResult[Context, Stmt.Loop] = preStmtWhile(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: Stmt.Loop)) => PreResult(preCtx, continu, Some[Stmt.Loop](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type Stmt.Loop")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[Stmt.Loop]())
          }
          return r
        case o: Stmt.DoWhile =>
          val r: PreResult[Context, Stmt.Loop] = preStmtDoWhile(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: Stmt.Loop)) => PreResult(preCtx, continu, Some[Stmt.Loop](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type Stmt.Loop")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[Stmt.Loop]())
          }
          return r
        case o: Stmt.For =>
          val r: PreResult[Context, Stmt.Loop] = preStmtFor(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: Stmt.Loop)) => PreResult(preCtx, continu, Some[Stmt.Loop](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type Stmt.Loop")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[Stmt.Loop]())
          }
          return r
      }
    }

    @pure def preStmtWhile(ctx: Context, o: Stmt.While): PreResult[Context, Stmt] = {
      return PreResult(ctx, T, None())
    }

    @pure def preStmtDoWhile(ctx: Context, o: Stmt.DoWhile): PreResult[Context, Stmt] = {
      return PreResult(ctx, T, None())
    }

    @pure def preStmtFor(ctx: Context, o: Stmt.For): PreResult[Context, Stmt] = {
      return PreResult(ctx, T, None())
    }

    @pure def preStmtReturn(ctx: Context, o: Stmt.Return): PreResult[Context, Stmt] = {
      return PreResult(ctx, T, None())
    }

    @pure def preStmtLStmt(ctx: Context, o: Stmt.LStmt): PreResult[Context, Stmt] = {
      return PreResult(ctx, T, None())
    }

    @pure def preStmtExpr(ctx: Context, o: Stmt.Expr): PreResult[Context, Stmt] = {
      return PreResult(ctx, T, None())
    }

    @pure def preAssignExp(ctx: Context, o: AssignExp): PreResult[Context, AssignExp] = {
      o match {
        case o: Stmt.Block =>
          val r: PreResult[Context, AssignExp] = preStmtBlock(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: AssignExp)) => PreResult(preCtx, continu, Some[AssignExp](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type AssignExp")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[AssignExp]())
          }
          return r
        case o: Stmt.If =>
          val r: PreResult[Context, AssignExp] = preStmtIf(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: AssignExp)) => PreResult(preCtx, continu, Some[AssignExp](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type AssignExp")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[AssignExp]())
          }
          return r
        case o: Stmt.Match =>
          val r: PreResult[Context, AssignExp] = preStmtMatch(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: AssignExp)) => PreResult(preCtx, continu, Some[AssignExp](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type AssignExp")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[AssignExp]())
          }
          return r
        case o: Stmt.Return =>
          val r: PreResult[Context, AssignExp] = preStmtReturn(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: AssignExp)) => PreResult(preCtx, continu, Some[AssignExp](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type AssignExp")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[AssignExp]())
          }
          return r
        case o: Stmt.Expr =>
          val r: PreResult[Context, AssignExp] = preStmtExpr(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: AssignExp)) => PreResult(preCtx, continu, Some[AssignExp](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type AssignExp")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[AssignExp]())
          }
          return r
      }
    }

    @pure def preLClause(ctx: Context, o: LClause): PreResult[Context, LClause] = {
      o match {
        case o: LClause.Sequent => return preLClauseSequent(ctx, o)
        case o: LClause.Proof => return preLClauseProof(ctx, o)
      }
    }

    @pure def preLClauseSequent(ctx: Context, o: LClause.Sequent): PreResult[Context, LClause] = {
      return PreResult(ctx, T, None())
    }

    @pure def preLClauseProof(ctx: Context, o: LClause.Proof): PreResult[Context, LClause] = {
      return PreResult(ctx, T, None())
    }

    @pure def preCase(ctx: Context, o: Case): PreResult[Context, Case] = {
      return PreResult(ctx, T, None())
    }

    @pure def preEnumGenRange(ctx: Context, o: EnumGen.Range): PreResult[Context, EnumGen.Range] = {
      o match {
        case o: EnumGen.Range.Expr => return preEnumGenRangeExpr(ctx, o)
        case o: EnumGen.Range.Step => return preEnumGenRangeStep(ctx, o)
      }
    }

    @pure def preEnumGenRangeExpr(ctx: Context, o: EnumGen.Range.Expr): PreResult[Context, EnumGen.Range] = {
      return PreResult(ctx, T, None())
    }

    @pure def preEnumGenRangeStep(ctx: Context, o: EnumGen.Range.Step): PreResult[Context, EnumGen.Range] = {
      return PreResult(ctx, T, None())
    }

    @pure def preEnumGenFor(ctx: Context, o: EnumGen.For): PreResult[Context, EnumGen.For] = {
      return PreResult(ctx, T, None())
    }

    @pure def preType(ctx: Context, o: Type): PreResult[Context, Type] = {
      o match {
        case o: Type.Named => return preTypeNamed(ctx, o)
        case o: Type.Fun => return preTypeFun(ctx, o)
        case o: Type.Tuple => return preTypeTuple(ctx, o)
      }
    }

    @pure def preTypeNamed(ctx: Context, o: Type.Named): PreResult[Context, Type] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTypeFun(ctx: Context, o: Type.Fun): PreResult[Context, Type] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTypeTuple(ctx: Context, o: Type.Tuple): PreResult[Context, Type] = {
      return PreResult(ctx, T, None())
    }

    @pure def prePattern(ctx: Context, o: Pattern): PreResult[Context, Pattern] = {
      o match {
        case o: Pattern.Literal => return prePatternLiteral(ctx, o)
        case o: Pattern.LitInterpolate => return prePatternLitInterpolate(ctx, o)
        case o: Pattern.Ref => return prePatternRef(ctx, o)
        case o: Pattern.VarBinding => return prePatternVarBinding(ctx, o)
        case o: Pattern.Wildcard => return prePatternWildcard(ctx, o)
        case o: Pattern.SeqWildcard => return prePatternSeqWildcard(ctx, o)
        case o: Pattern.Structure => return prePatternStructure(ctx, o)
      }
    }

    @pure def prePatternLiteral(ctx: Context, o: Pattern.Literal): PreResult[Context, Pattern] = {
      return PreResult(ctx, T, None())
    }

    @pure def prePatternLitInterpolate(ctx: Context, o: Pattern.LitInterpolate): PreResult[Context, Pattern] = {
      return PreResult(ctx, T, None())
    }

    @pure def prePatternRef(ctx: Context, o: Pattern.Ref): PreResult[Context, Pattern] = {
      return PreResult(ctx, T, None())
    }

    @pure def prePatternVarBinding(ctx: Context, o: Pattern.VarBinding): PreResult[Context, Pattern] = {
      return PreResult(ctx, T, None())
    }

    @pure def prePatternWildcard(ctx: Context, o: Pattern.Wildcard): PreResult[Context, Pattern] = {
      return PreResult(ctx, T, None())
    }

    @pure def prePatternSeqWildcard(ctx: Context, o: Pattern.SeqWildcard): PreResult[Context, Pattern] = {
      return PreResult(ctx, T, None())
    }

    @pure def prePatternStructure(ctx: Context, o: Pattern.Structure): PreResult[Context, Pattern] = {
      return PreResult(ctx, T, None())
    }

    @pure def preExp(ctx: Context, o: Exp): PreResult[Context, Exp] = {
      o match {
        case o: Exp.LitB => return preExpLitB(ctx, o)
        case o: Exp.LitC => return preExpLitC(ctx, o)
        case o: Exp.LitZ => return preExpLitZ(ctx, o)
        case o: Exp.LitF32 => return preExpLitF32(ctx, o)
        case o: Exp.LitF64 => return preExpLitF64(ctx, o)
        case o: Exp.LitR => return preExpLitR(ctx, o)
        case o: Exp.LitString => return preExpLitString(ctx, o)
        case o: Exp.StringInterpolate => return preExpStringInterpolate(ctx, o)
        case o: Exp.This => return preExpThis(ctx, o)
        case o: Exp.Super => return preExpSuper(ctx, o)
        case o: Exp.Unary => return preExpUnary(ctx, o)
        case o: Exp.Binary => return preExpBinary(ctx, o)
        case o: Exp.Ident => return preExpIdent(ctx, o)
        case o: Exp.Eta => return preExpEta(ctx, o)
        case o: Exp.Tuple => return preExpTuple(ctx, o)
        case o: Exp.Select => return preExpSelect(ctx, o)
        case o: Exp.Invoke => return preExpInvoke(ctx, o)
        case o: Exp.InvokeNamed => return preExpInvokeNamed(ctx, o)
        case o: Exp.If => return preExpIf(ctx, o)
        case o: Exp.Fun => return preExpFun(ctx, o)
        case o: Exp.ForYield => return preExpForYield(ctx, o)
        case o: Exp.Quant => return preExpQuant(ctx, o)
      }
    }

    @pure def preLit(ctx: Context, o: Lit): PreResult[Context, Lit] = {
      o match {
        case o: Exp.LitB =>
          val r: PreResult[Context, Lit] = preExpLitB(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: Lit)) => PreResult(preCtx, continu, Some[Lit](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type Lit")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[Lit]())
          }
          return r
        case o: Exp.LitC =>
          val r: PreResult[Context, Lit] = preExpLitC(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: Lit)) => PreResult(preCtx, continu, Some[Lit](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type Lit")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[Lit]())
          }
          return r
        case o: Exp.LitZ =>
          val r: PreResult[Context, Lit] = preExpLitZ(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: Lit)) => PreResult(preCtx, continu, Some[Lit](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type Lit")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[Lit]())
          }
          return r
        case o: Exp.LitF32 =>
          val r: PreResult[Context, Lit] = preExpLitF32(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: Lit)) => PreResult(preCtx, continu, Some[Lit](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type Lit")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[Lit]())
          }
          return r
        case o: Exp.LitF64 =>
          val r: PreResult[Context, Lit] = preExpLitF64(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: Lit)) => PreResult(preCtx, continu, Some[Lit](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type Lit")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[Lit]())
          }
          return r
        case o: Exp.LitR =>
          val r: PreResult[Context, Lit] = preExpLitR(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: Lit)) => PreResult(preCtx, continu, Some[Lit](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type Lit")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[Lit]())
          }
          return r
        case o: Exp.LitString =>
          val r: PreResult[Context, Lit] = preExpLitString(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: Lit)) => PreResult(preCtx, continu, Some[Lit](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type Lit")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[Lit]())
          }
          return r
      }
    }

    @pure def preExpLitB(ctx: Context, o: Exp.LitB): PreResult[Context, Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preExpLitC(ctx: Context, o: Exp.LitC): PreResult[Context, Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preExpLitZ(ctx: Context, o: Exp.LitZ): PreResult[Context, Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preExpLitF32(ctx: Context, o: Exp.LitF32): PreResult[Context, Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preExpLitF64(ctx: Context, o: Exp.LitF64): PreResult[Context, Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preExpLitR(ctx: Context, o: Exp.LitR): PreResult[Context, Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preExpLitString(ctx: Context, o: Exp.LitString): PreResult[Context, Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preExpStringInterpolate(ctx: Context, o: Exp.StringInterpolate): PreResult[Context, Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preExpThis(ctx: Context, o: Exp.This): PreResult[Context, Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preExpSuper(ctx: Context, o: Exp.Super): PreResult[Context, Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preExpUnary(ctx: Context, o: Exp.Unary): PreResult[Context, Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preExpRef(ctx: Context, o: Exp.Ref): PreResult[Context, Exp.Ref] = {
      o match {
        case o: Exp.Ident =>
          val r: PreResult[Context, Exp.Ref] = preExpIdent(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: Exp.Ref)) => PreResult(preCtx, continu, Some[Exp.Ref](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type Exp.Ref")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[Exp.Ref]())
          }
          return r
        case o: Exp.Select =>
          val r: PreResult[Context, Exp.Ref] = preExpSelect(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: Exp.Ref)) => PreResult(preCtx, continu, Some[Exp.Ref](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type Exp.Ref")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[Exp.Ref]())
          }
          return r
      }
    }

    @pure def preExpBinary(ctx: Context, o: Exp.Binary): PreResult[Context, Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preExpIdent(ctx: Context, o: Exp.Ident): PreResult[Context, Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preExpEta(ctx: Context, o: Exp.Eta): PreResult[Context, Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preExpTuple(ctx: Context, o: Exp.Tuple): PreResult[Context, Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preExpSelect(ctx: Context, o: Exp.Select): PreResult[Context, Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preExpInvoke(ctx: Context, o: Exp.Invoke): PreResult[Context, Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preExpInvokeNamed(ctx: Context, o: Exp.InvokeNamed): PreResult[Context, Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preExpIf(ctx: Context, o: Exp.If): PreResult[Context, Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preExpFunParam(ctx: Context, o: Exp.Fun.Param): PreResult[Context, Exp.Fun.Param] = {
      return PreResult(ctx, T, None())
    }

    @pure def preExpFun(ctx: Context, o: Exp.Fun): PreResult[Context, Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preExpForYield(ctx: Context, o: Exp.ForYield): PreResult[Context, Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preExpQuant(ctx: Context, o: Exp.Quant): PreResult[Context, Exp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preNamedArg(ctx: Context, o: NamedArg): PreResult[Context, NamedArg] = {
      return PreResult(ctx, T, None())
    }

    @pure def preVarFragment(ctx: Context, o: VarFragment): PreResult[Context, VarFragment] = {
      return PreResult(ctx, T, None())
    }

    @pure def preDomain(ctx: Context, o: Domain): PreResult[Context, Domain] = {
      o match {
        case o: Domain.Type => return preDomainType(ctx, o)
        case o: Domain.Range => return preDomainRange(ctx, o)
      }
    }

    @pure def preDomainType(ctx: Context, o: Domain.Type): PreResult[Context, Domain] = {
      return PreResult(ctx, T, None())
    }

    @pure def preDomainRange(ctx: Context, o: Domain.Range): PreResult[Context, Domain] = {
      return PreResult(ctx, T, None())
    }

    @pure def preId(ctx: Context, o: Id): PreResult[Context, Id] = {
      return PreResult(ctx, T, None())
    }

    @pure def preName(ctx: Context, o: Name): PreResult[Context, Name] = {
      return PreResult(ctx, T, None())
    }

    @pure def preBody(ctx: Context, o: Body): PreResult[Context, Body] = {
      return PreResult(ctx, T, None())
    }

    @pure def preAdtParam(ctx: Context, o: AdtParam): PreResult[Context, AdtParam] = {
      return PreResult(ctx, T, None())
    }

    @pure def preMethodSig(ctx: Context, o: MethodSig): PreResult[Context, MethodSig] = {
      return PreResult(ctx, T, None())
    }

    @pure def preParam(ctx: Context, o: Param): PreResult[Context, Param] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTypeParam(ctx: Context, o: TypeParam): PreResult[Context, TypeParam] = {
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

    @pure def preAttr(ctx: Context, o: Attr): PreResult[Context, Attr] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTypedAttr(ctx: Context, o: TypedAttr): PreResult[Context, TypedAttr] = {
      return PreResult(ctx, T, None())
    }

    @pure def preResolvedAttr(ctx: Context, o: ResolvedAttr): PreResult[Context, ResolvedAttr] = {
      return PreResult(ctx, T, None())
    }

    @pure def preResolvedInfo(ctx: Context, o: ResolvedInfo): PreResult[Context, ResolvedInfo] = {
      o match {
        case o: ResolvedInfo.BuiltIn => return preResolvedInfoBuiltIn(ctx, o)
        case o: ResolvedInfo.Package => return preResolvedInfoPackage(ctx, o)
        case o: ResolvedInfo.Enum => return preResolvedInfoEnum(ctx, o)
        case o: ResolvedInfo.EnumElement => return preResolvedInfoEnumElement(ctx, o)
        case o: ResolvedInfo.Object => return preResolvedInfoObject(ctx, o)
        case o: ResolvedInfo.Var => return preResolvedInfoVar(ctx, o)
        case o: ResolvedInfo.Method => return preResolvedInfoMethod(ctx, o)
        case o: ResolvedInfo.Methods => return preResolvedInfoMethods(ctx, o)
        case o: ResolvedInfo.Tuple => return preResolvedInfoTuple(ctx, o)
        case o: ResolvedInfo.LocalVar => return preResolvedInfoLocalVar(ctx, o)
      }
    }

    @pure def preResolvedInfoBuiltIn(ctx: Context, o: ResolvedInfo.BuiltIn): PreResult[Context, ResolvedInfo] = {
      return PreResult(ctx, T, None())
    }

    @pure def preResolvedInfoPackage(ctx: Context, o: ResolvedInfo.Package): PreResult[Context, ResolvedInfo] = {
      return PreResult(ctx, T, None())
    }

    @pure def preResolvedInfoEnum(ctx: Context, o: ResolvedInfo.Enum): PreResult[Context, ResolvedInfo] = {
      return PreResult(ctx, T, None())
    }

    @pure def preResolvedInfoEnumElement(ctx: Context, o: ResolvedInfo.EnumElement): PreResult[Context, ResolvedInfo] = {
      return PreResult(ctx, T, None())
    }

    @pure def preResolvedInfoObject(ctx: Context, o: ResolvedInfo.Object): PreResult[Context, ResolvedInfo] = {
      return PreResult(ctx, T, None())
    }

    @pure def preResolvedInfoVar(ctx: Context, o: ResolvedInfo.Var): PreResult[Context, ResolvedInfo] = {
      return PreResult(ctx, T, None())
    }

    @pure def preResolvedInfoMethod(ctx: Context, o: ResolvedInfo.Method): PreResult[Context, ResolvedInfo] = {
      return PreResult(ctx, T, None())
    }

    @pure def preResolvedInfoMethods(ctx: Context, o: ResolvedInfo.Methods): PreResult[Context, ResolvedInfo] = {
      return PreResult(ctx, T, None())
    }

    @pure def preResolvedInfoTuple(ctx: Context, o: ResolvedInfo.Tuple): PreResult[Context, ResolvedInfo] = {
      return PreResult(ctx, T, None())
    }

    @pure def preResolvedInfoLocalVar(ctx: Context, o: ResolvedInfo.LocalVar): PreResult[Context, ResolvedInfo] = {
      return PreResult(ctx, T, None())
    }

    @pure def preProofStep(ctx: Context, o: ProofStep): PreResult[Context, ProofStep] = {
      o match {
        case o: ProofStep.Basic => return preProofStepBasic(ctx, o)
        case o: ProofStep.SubProof => return preProofStepSubProof(ctx, o)
      }
    }

    @pure def preProofStepBasic(ctx: Context, o: ProofStep.Basic): PreResult[Context, ProofStep] = {
      return PreResult(ctx, T, None())
    }

    @pure def preProofStepSubProof(ctx: Context, o: ProofStep.SubProof): PreResult[Context, ProofStep] = {
      return PreResult(ctx, T, None())
    }

    @pure def preAssumeProofStep(ctx: Context, o: AssumeProofStep): PreResult[Context, AssumeProofStep] = {
      o match {
        case o: AssumeProofStep.Regular => return preAssumeProofStepRegular(ctx, o)
        case o: AssumeProofStep.ForallIntroAps => return preAssumeProofStepForallIntroAps(ctx, o)
        case o: AssumeProofStep.ExistsElimAps => return preAssumeProofStepExistsElimAps(ctx, o)
      }
    }

    @pure def preAssumeProofStepRegular(ctx: Context, o: AssumeProofStep.Regular): PreResult[Context, AssumeProofStep] = {
      return PreResult(ctx, T, None())
    }

    @pure def preAssumeProofStepForallIntroAps(ctx: Context, o: AssumeProofStep.ForallIntroAps): PreResult[Context, AssumeProofStep] = {
      return PreResult(ctx, T, None())
    }

    @pure def preAssumeProofStepExistsElimAps(ctx: Context, o: AssumeProofStep.ExistsElimAps): PreResult[Context, AssumeProofStep] = {
      return PreResult(ctx, T, None())
    }

    @pure def preJust(ctx: Context, o: Just): PreResult[Context, Just] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTruthTableRow(ctx: Context, o: TruthTable.Row): PreResult[Context, TruthTable.Row] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTruthTableAssignment(ctx: Context, o: TruthTable.Assignment): PreResult[Context, TruthTable.Assignment] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTruthTableConclusion(ctx: Context, o: TruthTable.Conclusion): PreResult[Context, TruthTable.Conclusion] = {
      o match {
        case o: TruthTable.Conclusion.Validity => return preTruthTableConclusionValidity(ctx, o)
        case o: TruthTable.Conclusion.Tautology => return preTruthTableConclusionTautology(ctx, o)
        case o: TruthTable.Conclusion.Contradictory => return preTruthTableConclusionContradictory(ctx, o)
        case o: TruthTable.Conclusion.Contingent => return preTruthTableConclusionContingent(ctx, o)
      }
    }

    @pure def preTruthTableConclusionValidity(ctx: Context, o: TruthTable.Conclusion.Validity): PreResult[Context, TruthTable.Conclusion] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTruthTableConclusionTautology(ctx: Context, o: TruthTable.Conclusion.Tautology): PreResult[Context, TruthTable.Conclusion] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTruthTableConclusionContradictory(ctx: Context, o: TruthTable.Conclusion.Contradictory): PreResult[Context, TruthTable.Conclusion] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTruthTableConclusionContingent(ctx: Context, o: TruthTable.Conclusion.Contingent): PreResult[Context, TruthTable.Conclusion] = {
      return PreResult(ctx, T, None())
    }

    @pure def postTopUnit(ctx: Context, o: TopUnit): TPostResult[Context, TopUnit] = {
      o match {
        case o: TopUnit.Program => return postTopUnitProgram(ctx, o)
        case o: TopUnit.SequentUnit => return postTopUnitSequentUnit(ctx, o)
        case o: TopUnit.TruthTableUnit => return postTopUnitTruthTableUnit(ctx, o)
      }
    }

    @pure def postTopUnitProgram(ctx: Context, o: TopUnit.Program): TPostResult[Context, TopUnit] = {
      return TPostResult(ctx, None())
    }

    @pure def postTopUnitSequentUnit(ctx: Context, o: TopUnit.SequentUnit): TPostResult[Context, TopUnit] = {
      return TPostResult(ctx, None())
    }

    @pure def postTopUnitTruthTableUnit(ctx: Context, o: TopUnit.TruthTableUnit): TPostResult[Context, TopUnit] = {
      return TPostResult(ctx, None())
    }

    @pure def postStmt(ctx: Context, o: Stmt): TPostResult[Context, Stmt] = {
      o match {
        case o: Stmt.Import => return postStmtImport(ctx, o)
        case o: Stmt.Var => return postStmtVar(ctx, o)
        case o: Stmt.VarPattern => return postStmtVarPattern(ctx, o)
        case o: Stmt.SpecVar => return postStmtSpecVar(ctx, o)
        case o: Stmt.Method => return postStmtMethod(ctx, o)
        case o: Stmt.ExtMethod => return postStmtExtMethod(ctx, o)
        case o: Stmt.SpecMethod => return postStmtSpecMethod(ctx, o)
        case o: Stmt.Enum => return postStmtEnum(ctx, o)
        case o: Stmt.SubZ => return postStmtSubZ(ctx, o)
        case o: Stmt.Object => return postStmtObject(ctx, o)
        case o: Stmt.Sig => return postStmtSig(ctx, o)
        case o: Stmt.Adt => return postStmtAdt(ctx, o)
        case o: Stmt.TypeAlias => return postStmtTypeAlias(ctx, o)
        case o: Stmt.Assign => return postStmtAssign(ctx, o)
        case o: Stmt.Block => return postStmtBlock(ctx, o)
        case o: Stmt.If => return postStmtIf(ctx, o)
        case o: Stmt.Match => return postStmtMatch(ctx, o)
        case o: Stmt.While => return postStmtWhile(ctx, o)
        case o: Stmt.DoWhile => return postStmtDoWhile(ctx, o)
        case o: Stmt.For => return postStmtFor(ctx, o)
        case o: Stmt.Return => return postStmtReturn(ctx, o)
        case o: Stmt.LStmt => return postStmtLStmt(ctx, o)
        case o: Stmt.Expr => return postStmtExpr(ctx, o)
      }
    }

    @pure def postContract(ctx: Context, o: Contract): TPostResult[Context, Contract] = {
      o match {
        case o: Contract.Simple => return postContractSimple(ctx, o)
        case o: Contract.Cases => return postContractCases(ctx, o)
      }
    }

    @pure def postContractSimple(ctx: Context, o: Contract.Simple): TPostResult[Context, Contract] = {
      return TPostResult(ctx, None())
    }

    @pure def postContractCases(ctx: Context, o: Contract.Cases): TPostResult[Context, Contract] = {
      return TPostResult(ctx, None())
    }

    @pure def postContractCase(ctx: Context, o: Contract.Case): TPostResult[Context, Contract.Case] = {
      return TPostResult(ctx, None())
    }

    @pure def postStmtImport(ctx: Context, o: Stmt.Import): TPostResult[Context, Stmt] = {
      return TPostResult(ctx, None())
    }

    @pure def postStmtImportImporter(ctx: Context, o: Stmt.Import.Importer): TPostResult[Context, Stmt.Import.Importer] = {
      return TPostResult(ctx, None())
    }

    @pure def postStmtImportSelector(ctx: Context, o: Stmt.Import.Selector): TPostResult[Context, Stmt.Import.Selector] = {
      o match {
        case o: Stmt.Import.MultiSelector => return postStmtImportMultiSelector(ctx, o)
        case o: Stmt.Import.WildcardSelector => return postStmtImportWildcardSelector(ctx, o)
      }
    }

    @pure def postStmtImportMultiSelector(ctx: Context, o: Stmt.Import.MultiSelector): TPostResult[Context, Stmt.Import.Selector] = {
      return TPostResult(ctx, None())
    }

    @pure def postStmtImportWildcardSelector(ctx: Context, o: Stmt.Import.WildcardSelector): TPostResult[Context, Stmt.Import.Selector] = {
      return TPostResult(ctx, None())
    }

    @pure def postStmtImportNamedSelector(ctx: Context, o: Stmt.Import.NamedSelector): TPostResult[Context, Stmt.Import.NamedSelector] = {
      return TPostResult(ctx, None())
    }

    @pure def postStmtVar(ctx: Context, o: Stmt.Var): TPostResult[Context, Stmt] = {
      return TPostResult(ctx, None())
    }

    @pure def postStmtVarPattern(ctx: Context, o: Stmt.VarPattern): TPostResult[Context, Stmt] = {
      return TPostResult(ctx, None())
    }

    @pure def postStmtSpecVar(ctx: Context, o: Stmt.SpecVar): TPostResult[Context, Stmt] = {
      return TPostResult(ctx, None())
    }

    @pure def postStmtMethod(ctx: Context, o: Stmt.Method): TPostResult[Context, Stmt] = {
      return TPostResult(ctx, None())
    }

    @pure def postStmtExtMethod(ctx: Context, o: Stmt.ExtMethod): TPostResult[Context, Stmt] = {
      return TPostResult(ctx, None())
    }

    @pure def postStmtSpecMethod(ctx: Context, o: Stmt.SpecMethod): TPostResult[Context, Stmt] = {
      return TPostResult(ctx, None())
    }

    @pure def postStmtEnum(ctx: Context, o: Stmt.Enum): TPostResult[Context, Stmt] = {
      return TPostResult(ctx, None())
    }

    @pure def postStmtSubZ(ctx: Context, o: Stmt.SubZ): TPostResult[Context, Stmt] = {
      return TPostResult(ctx, None())
    }

    @pure def postStmtObject(ctx: Context, o: Stmt.Object): TPostResult[Context, Stmt] = {
      return TPostResult(ctx, None())
    }

    @pure def postStmtSig(ctx: Context, o: Stmt.Sig): TPostResult[Context, Stmt] = {
      return TPostResult(ctx, None())
    }

    @pure def postStmtAdt(ctx: Context, o: Stmt.Adt): TPostResult[Context, Stmt] = {
      return TPostResult(ctx, None())
    }

    @pure def postStmtTypeAlias(ctx: Context, o: Stmt.TypeAlias): TPostResult[Context, Stmt] = {
      return TPostResult(ctx, None())
    }

    @pure def postStmtAssign(ctx: Context, o: Stmt.Assign): TPostResult[Context, Stmt] = {
      return TPostResult(ctx, None())
    }

    @pure def postStmtBlock(ctx: Context, o: Stmt.Block): TPostResult[Context, Stmt] = {
      return TPostResult(ctx, None())
    }

    @pure def postStmtIf(ctx: Context, o: Stmt.If): TPostResult[Context, Stmt] = {
      return TPostResult(ctx, None())
    }

    @pure def postStmtMatch(ctx: Context, o: Stmt.Match): TPostResult[Context, Stmt] = {
      return TPostResult(ctx, None())
    }

    @pure def postStmtLoop(ctx: Context, o: Stmt.Loop): TPostResult[Context, Stmt.Loop] = {
      o match {
        case o: Stmt.While =>
          val r: TPostResult[Context, Stmt.Loop] = postStmtWhile(ctx, o) match {
           case TPostResult(postCtx, Some(result: Stmt.Loop)) => TPostResult(postCtx, Some[Stmt.Loop](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type Stmt.Loop")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[Stmt.Loop]())
          }
          return r
        case o: Stmt.DoWhile =>
          val r: TPostResult[Context, Stmt.Loop] = postStmtDoWhile(ctx, o) match {
           case TPostResult(postCtx, Some(result: Stmt.Loop)) => TPostResult(postCtx, Some[Stmt.Loop](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type Stmt.Loop")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[Stmt.Loop]())
          }
          return r
        case o: Stmt.For =>
          val r: TPostResult[Context, Stmt.Loop] = postStmtFor(ctx, o) match {
           case TPostResult(postCtx, Some(result: Stmt.Loop)) => TPostResult(postCtx, Some[Stmt.Loop](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type Stmt.Loop")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[Stmt.Loop]())
          }
          return r
      }
    }

    @pure def postStmtWhile(ctx: Context, o: Stmt.While): TPostResult[Context, Stmt] = {
      return TPostResult(ctx, None())
    }

    @pure def postStmtDoWhile(ctx: Context, o: Stmt.DoWhile): TPostResult[Context, Stmt] = {
      return TPostResult(ctx, None())
    }

    @pure def postStmtFor(ctx: Context, o: Stmt.For): TPostResult[Context, Stmt] = {
      return TPostResult(ctx, None())
    }

    @pure def postStmtReturn(ctx: Context, o: Stmt.Return): TPostResult[Context, Stmt] = {
      return TPostResult(ctx, None())
    }

    @pure def postStmtLStmt(ctx: Context, o: Stmt.LStmt): TPostResult[Context, Stmt] = {
      return TPostResult(ctx, None())
    }

    @pure def postStmtExpr(ctx: Context, o: Stmt.Expr): TPostResult[Context, Stmt] = {
      return TPostResult(ctx, None())
    }

    @pure def postAssignExp(ctx: Context, o: AssignExp): TPostResult[Context, AssignExp] = {
      o match {
        case o: Stmt.Block =>
          val r: TPostResult[Context, AssignExp] = postStmtBlock(ctx, o) match {
           case TPostResult(postCtx, Some(result: AssignExp)) => TPostResult(postCtx, Some[AssignExp](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type AssignExp")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[AssignExp]())
          }
          return r
        case o: Stmt.If =>
          val r: TPostResult[Context, AssignExp] = postStmtIf(ctx, o) match {
           case TPostResult(postCtx, Some(result: AssignExp)) => TPostResult(postCtx, Some[AssignExp](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type AssignExp")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[AssignExp]())
          }
          return r
        case o: Stmt.Match =>
          val r: TPostResult[Context, AssignExp] = postStmtMatch(ctx, o) match {
           case TPostResult(postCtx, Some(result: AssignExp)) => TPostResult(postCtx, Some[AssignExp](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type AssignExp")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[AssignExp]())
          }
          return r
        case o: Stmt.Return =>
          val r: TPostResult[Context, AssignExp] = postStmtReturn(ctx, o) match {
           case TPostResult(postCtx, Some(result: AssignExp)) => TPostResult(postCtx, Some[AssignExp](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type AssignExp")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[AssignExp]())
          }
          return r
        case o: Stmt.Expr =>
          val r: TPostResult[Context, AssignExp] = postStmtExpr(ctx, o) match {
           case TPostResult(postCtx, Some(result: AssignExp)) => TPostResult(postCtx, Some[AssignExp](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type AssignExp")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[AssignExp]())
          }
          return r
      }
    }

    @pure def postLClause(ctx: Context, o: LClause): TPostResult[Context, LClause] = {
      o match {
        case o: LClause.Sequent => return postLClauseSequent(ctx, o)
        case o: LClause.Proof => return postLClauseProof(ctx, o)
      }
    }

    @pure def postLClauseSequent(ctx: Context, o: LClause.Sequent): TPostResult[Context, LClause] = {
      return TPostResult(ctx, None())
    }

    @pure def postLClauseProof(ctx: Context, o: LClause.Proof): TPostResult[Context, LClause] = {
      return TPostResult(ctx, None())
    }

    @pure def postCase(ctx: Context, o: Case): TPostResult[Context, Case] = {
      return TPostResult(ctx, None())
    }

    @pure def postEnumGenRange(ctx: Context, o: EnumGen.Range): TPostResult[Context, EnumGen.Range] = {
      o match {
        case o: EnumGen.Range.Expr => return postEnumGenRangeExpr(ctx, o)
        case o: EnumGen.Range.Step => return postEnumGenRangeStep(ctx, o)
      }
    }

    @pure def postEnumGenRangeExpr(ctx: Context, o: EnumGen.Range.Expr): TPostResult[Context, EnumGen.Range] = {
      return TPostResult(ctx, None())
    }

    @pure def postEnumGenRangeStep(ctx: Context, o: EnumGen.Range.Step): TPostResult[Context, EnumGen.Range] = {
      return TPostResult(ctx, None())
    }

    @pure def postEnumGenFor(ctx: Context, o: EnumGen.For): TPostResult[Context, EnumGen.For] = {
      return TPostResult(ctx, None())
    }

    @pure def postType(ctx: Context, o: Type): TPostResult[Context, Type] = {
      o match {
        case o: Type.Named => return postTypeNamed(ctx, o)
        case o: Type.Fun => return postTypeFun(ctx, o)
        case o: Type.Tuple => return postTypeTuple(ctx, o)
      }
    }

    @pure def postTypeNamed(ctx: Context, o: Type.Named): TPostResult[Context, Type] = {
      return TPostResult(ctx, None())
    }

    @pure def postTypeFun(ctx: Context, o: Type.Fun): TPostResult[Context, Type] = {
      return TPostResult(ctx, None())
    }

    @pure def postTypeTuple(ctx: Context, o: Type.Tuple): TPostResult[Context, Type] = {
      return TPostResult(ctx, None())
    }

    @pure def postPattern(ctx: Context, o: Pattern): TPostResult[Context, Pattern] = {
      o match {
        case o: Pattern.Literal => return postPatternLiteral(ctx, o)
        case o: Pattern.LitInterpolate => return postPatternLitInterpolate(ctx, o)
        case o: Pattern.Ref => return postPatternRef(ctx, o)
        case o: Pattern.VarBinding => return postPatternVarBinding(ctx, o)
        case o: Pattern.Wildcard => return postPatternWildcard(ctx, o)
        case o: Pattern.SeqWildcard => return postPatternSeqWildcard(ctx, o)
        case o: Pattern.Structure => return postPatternStructure(ctx, o)
      }
    }

    @pure def postPatternLiteral(ctx: Context, o: Pattern.Literal): TPostResult[Context, Pattern] = {
      return TPostResult(ctx, None())
    }

    @pure def postPatternLitInterpolate(ctx: Context, o: Pattern.LitInterpolate): TPostResult[Context, Pattern] = {
      return TPostResult(ctx, None())
    }

    @pure def postPatternRef(ctx: Context, o: Pattern.Ref): TPostResult[Context, Pattern] = {
      return TPostResult(ctx, None())
    }

    @pure def postPatternVarBinding(ctx: Context, o: Pattern.VarBinding): TPostResult[Context, Pattern] = {
      return TPostResult(ctx, None())
    }

    @pure def postPatternWildcard(ctx: Context, o: Pattern.Wildcard): TPostResult[Context, Pattern] = {
      return TPostResult(ctx, None())
    }

    @pure def postPatternSeqWildcard(ctx: Context, o: Pattern.SeqWildcard): TPostResult[Context, Pattern] = {
      return TPostResult(ctx, None())
    }

    @pure def postPatternStructure(ctx: Context, o: Pattern.Structure): TPostResult[Context, Pattern] = {
      return TPostResult(ctx, None())
    }

    @pure def postExp(ctx: Context, o: Exp): TPostResult[Context, Exp] = {
      o match {
        case o: Exp.LitB => return postExpLitB(ctx, o)
        case o: Exp.LitC => return postExpLitC(ctx, o)
        case o: Exp.LitZ => return postExpLitZ(ctx, o)
        case o: Exp.LitF32 => return postExpLitF32(ctx, o)
        case o: Exp.LitF64 => return postExpLitF64(ctx, o)
        case o: Exp.LitR => return postExpLitR(ctx, o)
        case o: Exp.LitString => return postExpLitString(ctx, o)
        case o: Exp.StringInterpolate => return postExpStringInterpolate(ctx, o)
        case o: Exp.This => return postExpThis(ctx, o)
        case o: Exp.Super => return postExpSuper(ctx, o)
        case o: Exp.Unary => return postExpUnary(ctx, o)
        case o: Exp.Binary => return postExpBinary(ctx, o)
        case o: Exp.Ident => return postExpIdent(ctx, o)
        case o: Exp.Eta => return postExpEta(ctx, o)
        case o: Exp.Tuple => return postExpTuple(ctx, o)
        case o: Exp.Select => return postExpSelect(ctx, o)
        case o: Exp.Invoke => return postExpInvoke(ctx, o)
        case o: Exp.InvokeNamed => return postExpInvokeNamed(ctx, o)
        case o: Exp.If => return postExpIf(ctx, o)
        case o: Exp.Fun => return postExpFun(ctx, o)
        case o: Exp.ForYield => return postExpForYield(ctx, o)
        case o: Exp.Quant => return postExpQuant(ctx, o)
      }
    }

    @pure def postLit(ctx: Context, o: Lit): TPostResult[Context, Lit] = {
      o match {
        case o: Exp.LitB =>
          val r: TPostResult[Context, Lit] = postExpLitB(ctx, o) match {
           case TPostResult(postCtx, Some(result: Lit)) => TPostResult(postCtx, Some[Lit](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type Lit")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[Lit]())
          }
          return r
        case o: Exp.LitC =>
          val r: TPostResult[Context, Lit] = postExpLitC(ctx, o) match {
           case TPostResult(postCtx, Some(result: Lit)) => TPostResult(postCtx, Some[Lit](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type Lit")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[Lit]())
          }
          return r
        case o: Exp.LitZ =>
          val r: TPostResult[Context, Lit] = postExpLitZ(ctx, o) match {
           case TPostResult(postCtx, Some(result: Lit)) => TPostResult(postCtx, Some[Lit](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type Lit")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[Lit]())
          }
          return r
        case o: Exp.LitF32 =>
          val r: TPostResult[Context, Lit] = postExpLitF32(ctx, o) match {
           case TPostResult(postCtx, Some(result: Lit)) => TPostResult(postCtx, Some[Lit](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type Lit")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[Lit]())
          }
          return r
        case o: Exp.LitF64 =>
          val r: TPostResult[Context, Lit] = postExpLitF64(ctx, o) match {
           case TPostResult(postCtx, Some(result: Lit)) => TPostResult(postCtx, Some[Lit](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type Lit")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[Lit]())
          }
          return r
        case o: Exp.LitR =>
          val r: TPostResult[Context, Lit] = postExpLitR(ctx, o) match {
           case TPostResult(postCtx, Some(result: Lit)) => TPostResult(postCtx, Some[Lit](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type Lit")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[Lit]())
          }
          return r
        case o: Exp.LitString =>
          val r: TPostResult[Context, Lit] = postExpLitString(ctx, o) match {
           case TPostResult(postCtx, Some(result: Lit)) => TPostResult(postCtx, Some[Lit](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type Lit")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[Lit]())
          }
          return r
      }
    }

    @pure def postExpLitB(ctx: Context, o: Exp.LitB): TPostResult[Context, Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postExpLitC(ctx: Context, o: Exp.LitC): TPostResult[Context, Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postExpLitZ(ctx: Context, o: Exp.LitZ): TPostResult[Context, Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postExpLitF32(ctx: Context, o: Exp.LitF32): TPostResult[Context, Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postExpLitF64(ctx: Context, o: Exp.LitF64): TPostResult[Context, Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postExpLitR(ctx: Context, o: Exp.LitR): TPostResult[Context, Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postExpLitString(ctx: Context, o: Exp.LitString): TPostResult[Context, Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postExpStringInterpolate(ctx: Context, o: Exp.StringInterpolate): TPostResult[Context, Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postExpThis(ctx: Context, o: Exp.This): TPostResult[Context, Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postExpSuper(ctx: Context, o: Exp.Super): TPostResult[Context, Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postExpUnary(ctx: Context, o: Exp.Unary): TPostResult[Context, Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postExpRef(ctx: Context, o: Exp.Ref): TPostResult[Context, Exp.Ref] = {
      o match {
        case o: Exp.Ident =>
          val r: TPostResult[Context, Exp.Ref] = postExpIdent(ctx, o) match {
           case TPostResult(postCtx, Some(result: Exp.Ref)) => TPostResult(postCtx, Some[Exp.Ref](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type Exp.Ref")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[Exp.Ref]())
          }
          return r
        case o: Exp.Select =>
          val r: TPostResult[Context, Exp.Ref] = postExpSelect(ctx, o) match {
           case TPostResult(postCtx, Some(result: Exp.Ref)) => TPostResult(postCtx, Some[Exp.Ref](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type Exp.Ref")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[Exp.Ref]())
          }
          return r
      }
    }

    @pure def postExpBinary(ctx: Context, o: Exp.Binary): TPostResult[Context, Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postExpIdent(ctx: Context, o: Exp.Ident): TPostResult[Context, Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postExpEta(ctx: Context, o: Exp.Eta): TPostResult[Context, Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postExpTuple(ctx: Context, o: Exp.Tuple): TPostResult[Context, Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postExpSelect(ctx: Context, o: Exp.Select): TPostResult[Context, Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postExpInvoke(ctx: Context, o: Exp.Invoke): TPostResult[Context, Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postExpInvokeNamed(ctx: Context, o: Exp.InvokeNamed): TPostResult[Context, Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postExpIf(ctx: Context, o: Exp.If): TPostResult[Context, Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postExpFunParam(ctx: Context, o: Exp.Fun.Param): TPostResult[Context, Exp.Fun.Param] = {
      return TPostResult(ctx, None())
    }

    @pure def postExpFun(ctx: Context, o: Exp.Fun): TPostResult[Context, Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postExpForYield(ctx: Context, o: Exp.ForYield): TPostResult[Context, Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postExpQuant(ctx: Context, o: Exp.Quant): TPostResult[Context, Exp] = {
      return TPostResult(ctx, None())
    }

    @pure def postNamedArg(ctx: Context, o: NamedArg): TPostResult[Context, NamedArg] = {
      return TPostResult(ctx, None())
    }

    @pure def postVarFragment(ctx: Context, o: VarFragment): TPostResult[Context, VarFragment] = {
      return TPostResult(ctx, None())
    }

    @pure def postDomain(ctx: Context, o: Domain): TPostResult[Context, Domain] = {
      o match {
        case o: Domain.Type => return postDomainType(ctx, o)
        case o: Domain.Range => return postDomainRange(ctx, o)
      }
    }

    @pure def postDomainType(ctx: Context, o: Domain.Type): TPostResult[Context, Domain] = {
      return TPostResult(ctx, None())
    }

    @pure def postDomainRange(ctx: Context, o: Domain.Range): TPostResult[Context, Domain] = {
      return TPostResult(ctx, None())
    }

    @pure def postId(ctx: Context, o: Id): TPostResult[Context, Id] = {
      return TPostResult(ctx, None())
    }

    @pure def postName(ctx: Context, o: Name): TPostResult[Context, Name] = {
      return TPostResult(ctx, None())
    }

    @pure def postBody(ctx: Context, o: Body): TPostResult[Context, Body] = {
      return TPostResult(ctx, None())
    }

    @pure def postAdtParam(ctx: Context, o: AdtParam): TPostResult[Context, AdtParam] = {
      return TPostResult(ctx, None())
    }

    @pure def postMethodSig(ctx: Context, o: MethodSig): TPostResult[Context, MethodSig] = {
      return TPostResult(ctx, None())
    }

    @pure def postParam(ctx: Context, o: Param): TPostResult[Context, Param] = {
      return TPostResult(ctx, None())
    }

    @pure def postTypeParam(ctx: Context, o: TypeParam): TPostResult[Context, TypeParam] = {
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

    @pure def postAttr(ctx: Context, o: Attr): TPostResult[Context, Attr] = {
      return TPostResult(ctx, None())
    }

    @pure def postTypedAttr(ctx: Context, o: TypedAttr): TPostResult[Context, TypedAttr] = {
      return TPostResult(ctx, None())
    }

    @pure def postResolvedAttr(ctx: Context, o: ResolvedAttr): TPostResult[Context, ResolvedAttr] = {
      return TPostResult(ctx, None())
    }

    @pure def postResolvedInfo(ctx: Context, o: ResolvedInfo): TPostResult[Context, ResolvedInfo] = {
      o match {
        case o: ResolvedInfo.BuiltIn => return postResolvedInfoBuiltIn(ctx, o)
        case o: ResolvedInfo.Package => return postResolvedInfoPackage(ctx, o)
        case o: ResolvedInfo.Enum => return postResolvedInfoEnum(ctx, o)
        case o: ResolvedInfo.EnumElement => return postResolvedInfoEnumElement(ctx, o)
        case o: ResolvedInfo.Object => return postResolvedInfoObject(ctx, o)
        case o: ResolvedInfo.Var => return postResolvedInfoVar(ctx, o)
        case o: ResolvedInfo.Method => return postResolvedInfoMethod(ctx, o)
        case o: ResolvedInfo.Methods => return postResolvedInfoMethods(ctx, o)
        case o: ResolvedInfo.Tuple => return postResolvedInfoTuple(ctx, o)
        case o: ResolvedInfo.LocalVar => return postResolvedInfoLocalVar(ctx, o)
      }
    }

    @pure def postResolvedInfoBuiltIn(ctx: Context, o: ResolvedInfo.BuiltIn): TPostResult[Context, ResolvedInfo] = {
      return TPostResult(ctx, None())
    }

    @pure def postResolvedInfoPackage(ctx: Context, o: ResolvedInfo.Package): TPostResult[Context, ResolvedInfo] = {
      return TPostResult(ctx, None())
    }

    @pure def postResolvedInfoEnum(ctx: Context, o: ResolvedInfo.Enum): TPostResult[Context, ResolvedInfo] = {
      return TPostResult(ctx, None())
    }

    @pure def postResolvedInfoEnumElement(ctx: Context, o: ResolvedInfo.EnumElement): TPostResult[Context, ResolvedInfo] = {
      return TPostResult(ctx, None())
    }

    @pure def postResolvedInfoObject(ctx: Context, o: ResolvedInfo.Object): TPostResult[Context, ResolvedInfo] = {
      return TPostResult(ctx, None())
    }

    @pure def postResolvedInfoVar(ctx: Context, o: ResolvedInfo.Var): TPostResult[Context, ResolvedInfo] = {
      return TPostResult(ctx, None())
    }

    @pure def postResolvedInfoMethod(ctx: Context, o: ResolvedInfo.Method): TPostResult[Context, ResolvedInfo] = {
      return TPostResult(ctx, None())
    }

    @pure def postResolvedInfoMethods(ctx: Context, o: ResolvedInfo.Methods): TPostResult[Context, ResolvedInfo] = {
      return TPostResult(ctx, None())
    }

    @pure def postResolvedInfoTuple(ctx: Context, o: ResolvedInfo.Tuple): TPostResult[Context, ResolvedInfo] = {
      return TPostResult(ctx, None())
    }

    @pure def postResolvedInfoLocalVar(ctx: Context, o: ResolvedInfo.LocalVar): TPostResult[Context, ResolvedInfo] = {
      return TPostResult(ctx, None())
    }

    @pure def postProofStep(ctx: Context, o: ProofStep): TPostResult[Context, ProofStep] = {
      o match {
        case o: ProofStep.Basic => return postProofStepBasic(ctx, o)
        case o: ProofStep.SubProof => return postProofStepSubProof(ctx, o)
      }
    }

    @pure def postProofStepBasic(ctx: Context, o: ProofStep.Basic): TPostResult[Context, ProofStep] = {
      return TPostResult(ctx, None())
    }

    @pure def postProofStepSubProof(ctx: Context, o: ProofStep.SubProof): TPostResult[Context, ProofStep] = {
      return TPostResult(ctx, None())
    }

    @pure def postAssumeProofStep(ctx: Context, o: AssumeProofStep): TPostResult[Context, AssumeProofStep] = {
      o match {
        case o: AssumeProofStep.Regular => return postAssumeProofStepRegular(ctx, o)
        case o: AssumeProofStep.ForallIntroAps => return postAssumeProofStepForallIntroAps(ctx, o)
        case o: AssumeProofStep.ExistsElimAps => return postAssumeProofStepExistsElimAps(ctx, o)
      }
    }

    @pure def postAssumeProofStepRegular(ctx: Context, o: AssumeProofStep.Regular): TPostResult[Context, AssumeProofStep] = {
      return TPostResult(ctx, None())
    }

    @pure def postAssumeProofStepForallIntroAps(ctx: Context, o: AssumeProofStep.ForallIntroAps): TPostResult[Context, AssumeProofStep] = {
      return TPostResult(ctx, None())
    }

    @pure def postAssumeProofStepExistsElimAps(ctx: Context, o: AssumeProofStep.ExistsElimAps): TPostResult[Context, AssumeProofStep] = {
      return TPostResult(ctx, None())
    }

    @pure def postJust(ctx: Context, o: Just): TPostResult[Context, Just] = {
      return TPostResult(ctx, None())
    }

    @pure def postTruthTableRow(ctx: Context, o: TruthTable.Row): TPostResult[Context, TruthTable.Row] = {
      return TPostResult(ctx, None())
    }

    @pure def postTruthTableAssignment(ctx: Context, o: TruthTable.Assignment): TPostResult[Context, TruthTable.Assignment] = {
      return TPostResult(ctx, None())
    }

    @pure def postTruthTableConclusion(ctx: Context, o: TruthTable.Conclusion): TPostResult[Context, TruthTable.Conclusion] = {
      o match {
        case o: TruthTable.Conclusion.Validity => return postTruthTableConclusionValidity(ctx, o)
        case o: TruthTable.Conclusion.Tautology => return postTruthTableConclusionTautology(ctx, o)
        case o: TruthTable.Conclusion.Contradictory => return postTruthTableConclusionContradictory(ctx, o)
        case o: TruthTable.Conclusion.Contingent => return postTruthTableConclusionContingent(ctx, o)
      }
    }

    @pure def postTruthTableConclusionValidity(ctx: Context, o: TruthTable.Conclusion.Validity): TPostResult[Context, TruthTable.Conclusion] = {
      return TPostResult(ctx, None())
    }

    @pure def postTruthTableConclusionTautology(ctx: Context, o: TruthTable.Conclusion.Tautology): TPostResult[Context, TruthTable.Conclusion] = {
      return TPostResult(ctx, None())
    }

    @pure def postTruthTableConclusionContradictory(ctx: Context, o: TruthTable.Conclusion.Contradictory): TPostResult[Context, TruthTable.Conclusion] = {
      return TPostResult(ctx, None())
    }

    @pure def postTruthTableConclusionContingent(ctx: Context, o: TruthTable.Conclusion.Contingent): TPostResult[Context, TruthTable.Conclusion] = {
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

import Transformer._

@datatype class Transformer[Context](pp: PrePost[Context]) {

  @pure def transformTopUnit(ctx: Context, o: TopUnit): TPostResult[Context, TopUnit] = {
    val preR: PreResult[Context, TopUnit] = pp.preTopUnit(ctx, o)
    val r: TPostResult[Context, TopUnit] = if (preR.continu) {
      val o2: TopUnit = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: TPostResult[Context, TopUnit] = o2 match {
        case o2: TopUnit.Program =>
          val r0: TPostResult[Context, Name] = transformName(preR.ctx, o2.packageName)
          val r1: TPostResult[Context, Body] = transformBody(r0.ctx, o2.body)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(packageName = r0.resultOpt.getOrElse(o2.packageName), body = r1.resultOpt.getOrElse(o2.body))))
          else
            TPostResult(r1.ctx, None())
        case o2: TopUnit.SequentUnit =>
          val r0: TPostResult[Context, LClause.Sequent] = transformLClauseSequent(preR.ctx, o2.sequent)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(sequent = r0.resultOpt.getOrElse(o2.sequent))))
          else
            TPostResult(r0.ctx, None())
        case o2: TopUnit.TruthTableUnit =>
          val r0: TPostResult[Context, IS[Z, Id]] = transformISZ(preR.ctx, o2.vars, transformId _)
          val r1: TPostResult[Context, LClause.Sequent] = transformLClauseSequent(r0.ctx, o2.sequent)
          val r2: TPostResult[Context, IS[Z, TruthTable.Row]] = transformISZ(r1.ctx, o2.rows, transformTruthTableRow _)
          val r3: TPostResult[Context, Option[TruthTable.Conclusion]] = transformOption(r2.ctx, o2.conclusionOpt, transformTruthTableConclusion _)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty || r3.resultOpt.nonEmpty)
            TPostResult(r3.ctx, Some(o2(vars = r0.resultOpt.getOrElse(o2.vars), sequent = r1.resultOpt.getOrElse(o2.sequent), rows = r2.resultOpt.getOrElse(o2.rows), conclusionOpt = r3.resultOpt.getOrElse(o2.conclusionOpt))))
          else
            TPostResult(r3.ctx, None())
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: TopUnit = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, TopUnit] = pp.postTopUnit(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformStmt(ctx: Context, o: Stmt): TPostResult[Context, Stmt] = {
    val preR: PreResult[Context, Stmt] = pp.preStmt(ctx, o)
    val r: TPostResult[Context, Stmt] = if (preR.continu) {
      val o2: Stmt = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: TPostResult[Context, Stmt] = o2 match {
        case o2: Stmt.Import =>
          val r0: TPostResult[Context, IS[Z, Stmt.Import.Importer]] = transformISZ(preR.ctx, o2.importers, transformStmtImportImporter _)
          val r1: TPostResult[Context, Attr] = transformAttr(r0.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(importers = r0.resultOpt.getOrElse(o2.importers), attr = r1.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r1.ctx, None())
        case o2: Stmt.Var =>
          val r0: TPostResult[Context, Id] = transformId(preR.ctx, o2.id)
          val r1: TPostResult[Context, Option[Type]] = transformOption(r0.ctx, o2.tipeOpt, transformType _)
          val r2: TPostResult[Context, Option[AssignExp]] = transformOption(r1.ctx, o2.initOpt, transformAssignExp _)
          val r3: TPostResult[Context, ResolvedAttr] = transformResolvedAttr(r2.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty || r3.resultOpt.nonEmpty)
            TPostResult(r3.ctx, Some(o2(id = r0.resultOpt.getOrElse(o2.id), tipeOpt = r1.resultOpt.getOrElse(o2.tipeOpt), initOpt = r2.resultOpt.getOrElse(o2.initOpt), attr = r3.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r3.ctx, None())
        case o2: Stmt.VarPattern =>
          val r0: TPostResult[Context, Pattern] = transformPattern(preR.ctx, o2.pattern)
          val r1: TPostResult[Context, Option[Type]] = transformOption(r0.ctx, o2.tipeOpt, transformType _)
          val r2: TPostResult[Context, AssignExp] = transformAssignExp(r1.ctx, o2.init)
          val r3: TPostResult[Context, Attr] = transformAttr(r2.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty || r3.resultOpt.nonEmpty)
            TPostResult(r3.ctx, Some(o2(pattern = r0.resultOpt.getOrElse(o2.pattern), tipeOpt = r1.resultOpt.getOrElse(o2.tipeOpt), init = r2.resultOpt.getOrElse(o2.init), attr = r3.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r3.ctx, None())
        case o2: Stmt.SpecVar =>
          val r0: TPostResult[Context, Id] = transformId(preR.ctx, o2.id)
          val r1: TPostResult[Context, Type] = transformType(r0.ctx, o2.tipe)
          val r2: TPostResult[Context, ResolvedAttr] = transformResolvedAttr(r1.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(id = r0.resultOpt.getOrElse(o2.id), tipe = r1.resultOpt.getOrElse(o2.tipe), attr = r2.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r2.ctx, None())
        case o2: Stmt.Method =>
          val r0: TPostResult[Context, MethodSig] = transformMethodSig(preR.ctx, o2.sig)
          val r1: TPostResult[Context, Contract] = transformContract(r0.ctx, o2.contract)
          val r2: TPostResult[Context, Option[Body]] = transformOption(r1.ctx, o2.bodyOpt, transformBody _)
          val r3: TPostResult[Context, ResolvedAttr] = transformResolvedAttr(r2.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty || r3.resultOpt.nonEmpty)
            TPostResult(r3.ctx, Some(o2(sig = r0.resultOpt.getOrElse(o2.sig), contract = r1.resultOpt.getOrElse(o2.contract), bodyOpt = r2.resultOpt.getOrElse(o2.bodyOpt), attr = r3.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r3.ctx, None())
        case o2: Stmt.ExtMethod =>
          val r0: TPostResult[Context, MethodSig] = transformMethodSig(preR.ctx, o2.sig)
          val r1: TPostResult[Context, Contract] = transformContract(r0.ctx, o2.contract)
          val r2: TPostResult[Context, ResolvedAttr] = transformResolvedAttr(r1.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(sig = r0.resultOpt.getOrElse(o2.sig), contract = r1.resultOpt.getOrElse(o2.contract), attr = r2.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r2.ctx, None())
        case o2: Stmt.SpecMethod =>
          val r0: TPostResult[Context, MethodSig] = transformMethodSig(preR.ctx, o2.sig)
          val r1: TPostResult[Context, ResolvedAttr] = transformResolvedAttr(r0.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(sig = r0.resultOpt.getOrElse(o2.sig), attr = r1.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r1.ctx, None())
        case o2: Stmt.Enum =>
          val r0: TPostResult[Context, Id] = transformId(preR.ctx, o2.id)
          val r1: TPostResult[Context, IS[Z, Id]] = transformISZ(r0.ctx, o2.elements, transformId _)
          val r2: TPostResult[Context, Attr] = transformAttr(r1.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(id = r0.resultOpt.getOrElse(o2.id), elements = r1.resultOpt.getOrElse(o2.elements), attr = r2.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r2.ctx, None())
        case o2: Stmt.SubZ =>
          val r0: TPostResult[Context, Id] = transformId(preR.ctx, o2.id)
          val r1: TPostResult[Context, Attr] = transformAttr(r0.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(id = r0.resultOpt.getOrElse(o2.id), attr = r1.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r1.ctx, None())
        case o2: Stmt.Object =>
          val r0: TPostResult[Context, Id] = transformId(preR.ctx, o2.id)
          val r1: TPostResult[Context, IS[Z, Stmt]] = transformISZ(r0.ctx, o2.stmts, transformStmt _)
          val r2: TPostResult[Context, Attr] = transformAttr(r1.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(id = r0.resultOpt.getOrElse(o2.id), stmts = r1.resultOpt.getOrElse(o2.stmts), attr = r2.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r2.ctx, None())
        case o2: Stmt.Sig =>
          val r0: TPostResult[Context, Id] = transformId(preR.ctx, o2.id)
          val r1: TPostResult[Context, IS[Z, TypeParam]] = transformISZ(r0.ctx, o2.typeParams, transformTypeParam _)
          val r2: TPostResult[Context, IS[Z, Type.Named]] = transformISZ(r1.ctx, o2.parents, transformTypeNamed _)
          val r3: TPostResult[Context, IS[Z, Stmt]] = transformISZ(r2.ctx, o2.stmts, transformStmt _)
          val r4: TPostResult[Context, Attr] = transformAttr(r3.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty || r3.resultOpt.nonEmpty || r4.resultOpt.nonEmpty)
            TPostResult(r4.ctx, Some(o2(id = r0.resultOpt.getOrElse(o2.id), typeParams = r1.resultOpt.getOrElse(o2.typeParams), parents = r2.resultOpt.getOrElse(o2.parents), stmts = r3.resultOpt.getOrElse(o2.stmts), attr = r4.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r4.ctx, None())
        case o2: Stmt.Adt =>
          val r0: TPostResult[Context, Id] = transformId(preR.ctx, o2.id)
          val r1: TPostResult[Context, IS[Z, TypeParam]] = transformISZ(r0.ctx, o2.typeParams, transformTypeParam _)
          val r2: TPostResult[Context, IS[Z, AdtParam]] = transformISZ(r1.ctx, o2.params, transformAdtParam _)
          val r3: TPostResult[Context, IS[Z, Type.Named]] = transformISZ(r2.ctx, o2.parents, transformTypeNamed _)
          val r4: TPostResult[Context, IS[Z, Stmt]] = transformISZ(r3.ctx, o2.stmts, transformStmt _)
          val r5: TPostResult[Context, Attr] = transformAttr(r4.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty || r3.resultOpt.nonEmpty || r4.resultOpt.nonEmpty || r5.resultOpt.nonEmpty)
            TPostResult(r5.ctx, Some(o2(id = r0.resultOpt.getOrElse(o2.id), typeParams = r1.resultOpt.getOrElse(o2.typeParams), params = r2.resultOpt.getOrElse(o2.params), parents = r3.resultOpt.getOrElse(o2.parents), stmts = r4.resultOpt.getOrElse(o2.stmts), attr = r5.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r5.ctx, None())
        case o2: Stmt.TypeAlias =>
          val r0: TPostResult[Context, Id] = transformId(preR.ctx, o2.id)
          val r1: TPostResult[Context, IS[Z, TypeParam]] = transformISZ(r0.ctx, o2.typeParams, transformTypeParam _)
          val r2: TPostResult[Context, Type] = transformType(r1.ctx, o2.tipe)
          val r3: TPostResult[Context, Attr] = transformAttr(r2.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty || r3.resultOpt.nonEmpty)
            TPostResult(r3.ctx, Some(o2(id = r0.resultOpt.getOrElse(o2.id), typeParams = r1.resultOpt.getOrElse(o2.typeParams), tipe = r2.resultOpt.getOrElse(o2.tipe), attr = r3.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r3.ctx, None())
        case o2: Stmt.Assign =>
          val r0: TPostResult[Context, Exp] = transformExp(preR.ctx, o2.lhs)
          val r1: TPostResult[Context, AssignExp] = transformAssignExp(r0.ctx, o2.rhs)
          val r2: TPostResult[Context, Attr] = transformAttr(r1.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(lhs = r0.resultOpt.getOrElse(o2.lhs), rhs = r1.resultOpt.getOrElse(o2.rhs), attr = r2.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r2.ctx, None())
        case o2: Stmt.Block =>
          val r0: TPostResult[Context, Body] = transformBody(preR.ctx, o2.body)
          val r1: TPostResult[Context, Attr] = transformAttr(r0.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(body = r0.resultOpt.getOrElse(o2.body), attr = r1.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r1.ctx, None())
        case o2: Stmt.If =>
          val r0: TPostResult[Context, Exp] = transformExp(preR.ctx, o2.cond)
          val r1: TPostResult[Context, Body] = transformBody(r0.ctx, o2.thenBody)
          val r2: TPostResult[Context, Body] = transformBody(r1.ctx, o2.elseBody)
          val r3: TPostResult[Context, Attr] = transformAttr(r2.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty || r3.resultOpt.nonEmpty)
            TPostResult(r3.ctx, Some(o2(cond = r0.resultOpt.getOrElse(o2.cond), thenBody = r1.resultOpt.getOrElse(o2.thenBody), elseBody = r2.resultOpt.getOrElse(o2.elseBody), attr = r3.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r3.ctx, None())
        case o2: Stmt.Match =>
          val r0: TPostResult[Context, Exp] = transformExp(preR.ctx, o2.exp)
          val r1: TPostResult[Context, IS[Z, Case]] = transformISZ(r0.ctx, o2.cases, transformCase _)
          val r2: TPostResult[Context, Attr] = transformAttr(r1.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(exp = r0.resultOpt.getOrElse(o2.exp), cases = r1.resultOpt.getOrElse(o2.cases), attr = r2.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r2.ctx, None())
        case o2: Stmt.While =>
          val r0: TPostResult[Context, Exp] = transformExp(preR.ctx, o2.cond)
          val r1: TPostResult[Context, IS[Z, Exp]] = transformISZ(r0.ctx, o2.invariants, transformExp _)
          val r2: TPostResult[Context, IS[Z, Exp.Ident]] = transformISZ(r1.ctx, o2.modifies, transformExpIdent _)
          val r3: TPostResult[Context, Body] = transformBody(r2.ctx, o2.body)
          val r4: TPostResult[Context, Attr] = transformAttr(r3.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty || r3.resultOpt.nonEmpty || r4.resultOpt.nonEmpty)
            TPostResult(r4.ctx, Some(o2(cond = r0.resultOpt.getOrElse(o2.cond), invariants = r1.resultOpt.getOrElse(o2.invariants), modifies = r2.resultOpt.getOrElse(o2.modifies), body = r3.resultOpt.getOrElse(o2.body), attr = r4.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r4.ctx, None())
        case o2: Stmt.DoWhile =>
          val r0: TPostResult[Context, Exp] = transformExp(preR.ctx, o2.cond)
          val r1: TPostResult[Context, IS[Z, Exp]] = transformISZ(r0.ctx, o2.invariants, transformExp _)
          val r2: TPostResult[Context, IS[Z, Exp.Ident]] = transformISZ(r1.ctx, o2.modifies, transformExpIdent _)
          val r3: TPostResult[Context, Body] = transformBody(r2.ctx, o2.body)
          val r4: TPostResult[Context, Attr] = transformAttr(r3.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty || r3.resultOpt.nonEmpty || r4.resultOpt.nonEmpty)
            TPostResult(r4.ctx, Some(o2(cond = r0.resultOpt.getOrElse(o2.cond), invariants = r1.resultOpt.getOrElse(o2.invariants), modifies = r2.resultOpt.getOrElse(o2.modifies), body = r3.resultOpt.getOrElse(o2.body), attr = r4.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r4.ctx, None())
        case o2: Stmt.For =>
          val r0: TPostResult[Context, IS[Z, EnumGen.For]] = transformISZ(preR.ctx, o2.enumGens, transformEnumGenFor _)
          val r1: TPostResult[Context, IS[Z, Exp]] = transformISZ(r0.ctx, o2.invariants, transformExp _)
          val r2: TPostResult[Context, IS[Z, Exp.Ident]] = transformISZ(r1.ctx, o2.modifies, transformExpIdent _)
          val r3: TPostResult[Context, Body] = transformBody(r2.ctx, o2.body)
          val r4: TPostResult[Context, Attr] = transformAttr(r3.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty || r3.resultOpt.nonEmpty || r4.resultOpt.nonEmpty)
            TPostResult(r4.ctx, Some(o2(enumGens = r0.resultOpt.getOrElse(o2.enumGens), invariants = r1.resultOpt.getOrElse(o2.invariants), modifies = r2.resultOpt.getOrElse(o2.modifies), body = r3.resultOpt.getOrElse(o2.body), attr = r4.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r4.ctx, None())
        case o2: Stmt.Return =>
          val r0: TPostResult[Context, Option[Exp]] = transformOption(preR.ctx, o2.expOpt, transformExp _)
          val r1: TPostResult[Context, TypedAttr] = transformTypedAttr(r0.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(expOpt = r0.resultOpt.getOrElse(o2.expOpt), attr = r1.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r1.ctx, None())
        case o2: Stmt.LStmt =>
          val r0: TPostResult[Context, LClause] = transformLClause(preR.ctx, o2.clause)
          val r1: TPostResult[Context, Attr] = transformAttr(r0.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(clause = r0.resultOpt.getOrElse(o2.clause), attr = r1.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r1.ctx, None())
        case o2: Stmt.Expr =>
          val r0: TPostResult[Context, Exp] = transformExp(preR.ctx, o2.exp)
          val r1: TPostResult[Context, TypedAttr] = transformTypedAttr(r0.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(exp = r0.resultOpt.getOrElse(o2.exp), attr = r1.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r1.ctx, None())
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: Stmt = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Stmt] = pp.postStmt(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformContract(ctx: Context, o: Contract): TPostResult[Context, Contract] = {
    val preR: PreResult[Context, Contract] = pp.preContract(ctx, o)
    val r: TPostResult[Context, Contract] = if (preR.continu) {
      val o2: Contract = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: TPostResult[Context, Contract] = o2 match {
        case o2: Contract.Simple =>
          val r0: TPostResult[Context, IS[Z, Exp.Ident]] = transformISZ(preR.ctx, o2.reads, transformExpIdent _)
          val r1: TPostResult[Context, IS[Z, Exp]] = transformISZ(r0.ctx, o2.requires, transformExp _)
          val r2: TPostResult[Context, IS[Z, Exp.Ident]] = transformISZ(r1.ctx, o2.modifies, transformExpIdent _)
          val r3: TPostResult[Context, IS[Z, Exp]] = transformISZ(r2.ctx, o2.ensures, transformExp _)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty || r3.resultOpt.nonEmpty)
            TPostResult(r3.ctx, Some(o2(reads = r0.resultOpt.getOrElse(o2.reads), requires = r1.resultOpt.getOrElse(o2.requires), modifies = r2.resultOpt.getOrElse(o2.modifies), ensures = r3.resultOpt.getOrElse(o2.ensures))))
          else
            TPostResult(r3.ctx, None())
        case o2: Contract.Cases =>
          val r0: TPostResult[Context, IS[Z, Exp.Ident]] = transformISZ(preR.ctx, o2.reads, transformExpIdent _)
          val r1: TPostResult[Context, IS[Z, Exp.Ident]] = transformISZ(r0.ctx, o2.modifies, transformExpIdent _)
          val r2: TPostResult[Context, IS[Z, Contract.Case]] = transformISZ(r1.ctx, o2.cases, transformContractCase _)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(reads = r0.resultOpt.getOrElse(o2.reads), modifies = r1.resultOpt.getOrElse(o2.modifies), cases = r2.resultOpt.getOrElse(o2.cases))))
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
    val o2: Contract = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Contract] = pp.postContract(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformContractCase(ctx: Context, o: Contract.Case): TPostResult[Context, Contract.Case] = {
    val preR: PreResult[Context, Contract.Case] = pp.preContractCase(ctx, o)
    val r: TPostResult[Context, Contract.Case] = if (preR.continu) {
      val o2: Contract.Case = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, IS[Z, Exp]] = transformISZ(preR.ctx, o2.requires, transformExp _)
      val r1: TPostResult[Context, IS[Z, Exp]] = transformISZ(r0.ctx, o2.ensures, transformExp _)
      if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
        TPostResult(r1.ctx, Some(o2(requires = r0.resultOpt.getOrElse(o2.requires), ensures = r1.resultOpt.getOrElse(o2.ensures))))
      else
        TPostResult(r1.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: Contract.Case = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Contract.Case] = pp.postContractCase(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformStmtImportImporter(ctx: Context, o: Stmt.Import.Importer): TPostResult[Context, Stmt.Import.Importer] = {
    val preR: PreResult[Context, Stmt.Import.Importer] = pp.preStmtImportImporter(ctx, o)
    val r: TPostResult[Context, Stmt.Import.Importer] = if (preR.continu) {
      val o2: Stmt.Import.Importer = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, Name] = transformName(preR.ctx, o2.name)
      val r1: TPostResult[Context, Option[Stmt.Import.Selector]] = transformOption(r0.ctx, o2.selectorOpt, transformStmtImportSelector _)
      if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
        TPostResult(r1.ctx, Some(o2(name = r0.resultOpt.getOrElse(o2.name), selectorOpt = r1.resultOpt.getOrElse(o2.selectorOpt))))
      else
        TPostResult(r1.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: Stmt.Import.Importer = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Stmt.Import.Importer] = pp.postStmtImportImporter(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformStmtImportSelector(ctx: Context, o: Stmt.Import.Selector): TPostResult[Context, Stmt.Import.Selector] = {
    val preR: PreResult[Context, Stmt.Import.Selector] = pp.preStmtImportSelector(ctx, o)
    val r: TPostResult[Context, Stmt.Import.Selector] = if (preR.continu) {
      val o2: Stmt.Import.Selector = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: TPostResult[Context, Stmt.Import.Selector] = o2 match {
        case o2: Stmt.Import.MultiSelector =>
          val r0: TPostResult[Context, IS[Z, Stmt.Import.NamedSelector]] = transformISZ(preR.ctx, o2.selectors, transformStmtImportNamedSelector _)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(selectors = r0.resultOpt.getOrElse(o2.selectors))))
          else
            TPostResult(r0.ctx, None())
        case o2: Stmt.Import.WildcardSelector =>
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
    val o2: Stmt.Import.Selector = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Stmt.Import.Selector] = pp.postStmtImportSelector(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformStmtImportNamedSelector(ctx: Context, o: Stmt.Import.NamedSelector): TPostResult[Context, Stmt.Import.NamedSelector] = {
    val preR: PreResult[Context, Stmt.Import.NamedSelector] = pp.preStmtImportNamedSelector(ctx, o)
    val r: TPostResult[Context, Stmt.Import.NamedSelector] = if (preR.continu) {
      val o2: Stmt.Import.NamedSelector = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, Id] = transformId(preR.ctx, o2.from)
      val r1: TPostResult[Context, Id] = transformId(r0.ctx, o2.to)
      if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
        TPostResult(r1.ctx, Some(o2(from = r0.resultOpt.getOrElse(o2.from), to = r1.resultOpt.getOrElse(o2.to))))
      else
        TPostResult(r1.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: Stmt.Import.NamedSelector = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Stmt.Import.NamedSelector] = pp.postStmtImportNamedSelector(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformStmtLoop(ctx: Context, o: Stmt.Loop): TPostResult[Context, Stmt.Loop] = {
    val preR: PreResult[Context, Stmt.Loop] = pp.preStmtLoop(ctx, o)
    val r: TPostResult[Context, Stmt.Loop] = if (preR.continu) {
      val o2: Stmt.Loop = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: TPostResult[Context, Stmt.Loop] = o2 match {
        case o2: Stmt.While =>
          val r0: TPostResult[Context, Exp] = transformExp(preR.ctx, o2.cond)
          val r1: TPostResult[Context, IS[Z, Exp]] = transformISZ(r0.ctx, o2.invariants, transformExp _)
          val r2: TPostResult[Context, IS[Z, Exp.Ident]] = transformISZ(r1.ctx, o2.modifies, transformExpIdent _)
          val r3: TPostResult[Context, Body] = transformBody(r2.ctx, o2.body)
          val r4: TPostResult[Context, Attr] = transformAttr(r3.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty || r3.resultOpt.nonEmpty || r4.resultOpt.nonEmpty)
            TPostResult(r4.ctx, Some(o2(cond = r0.resultOpt.getOrElse(o2.cond), invariants = r1.resultOpt.getOrElse(o2.invariants), modifies = r2.resultOpt.getOrElse(o2.modifies), body = r3.resultOpt.getOrElse(o2.body), attr = r4.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r4.ctx, None())
        case o2: Stmt.DoWhile =>
          val r0: TPostResult[Context, Exp] = transformExp(preR.ctx, o2.cond)
          val r1: TPostResult[Context, IS[Z, Exp]] = transformISZ(r0.ctx, o2.invariants, transformExp _)
          val r2: TPostResult[Context, IS[Z, Exp.Ident]] = transformISZ(r1.ctx, o2.modifies, transformExpIdent _)
          val r3: TPostResult[Context, Body] = transformBody(r2.ctx, o2.body)
          val r4: TPostResult[Context, Attr] = transformAttr(r3.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty || r3.resultOpt.nonEmpty || r4.resultOpt.nonEmpty)
            TPostResult(r4.ctx, Some(o2(cond = r0.resultOpt.getOrElse(o2.cond), invariants = r1.resultOpt.getOrElse(o2.invariants), modifies = r2.resultOpt.getOrElse(o2.modifies), body = r3.resultOpt.getOrElse(o2.body), attr = r4.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r4.ctx, None())
        case o2: Stmt.For =>
          val r0: TPostResult[Context, IS[Z, EnumGen.For]] = transformISZ(preR.ctx, o2.enumGens, transformEnumGenFor _)
          val r1: TPostResult[Context, IS[Z, Exp]] = transformISZ(r0.ctx, o2.invariants, transformExp _)
          val r2: TPostResult[Context, IS[Z, Exp.Ident]] = transformISZ(r1.ctx, o2.modifies, transformExpIdent _)
          val r3: TPostResult[Context, Body] = transformBody(r2.ctx, o2.body)
          val r4: TPostResult[Context, Attr] = transformAttr(r3.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty || r3.resultOpt.nonEmpty || r4.resultOpt.nonEmpty)
            TPostResult(r4.ctx, Some(o2(enumGens = r0.resultOpt.getOrElse(o2.enumGens), invariants = r1.resultOpt.getOrElse(o2.invariants), modifies = r2.resultOpt.getOrElse(o2.modifies), body = r3.resultOpt.getOrElse(o2.body), attr = r4.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r4.ctx, None())
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: Stmt.Loop = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Stmt.Loop] = pp.postStmtLoop(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformAssignExp(ctx: Context, o: AssignExp): TPostResult[Context, AssignExp] = {
    val preR: PreResult[Context, AssignExp] = pp.preAssignExp(ctx, o)
    val r: TPostResult[Context, AssignExp] = if (preR.continu) {
      val o2: AssignExp = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: TPostResult[Context, AssignExp] = o2 match {
        case o2: Stmt.Block =>
          val r0: TPostResult[Context, Body] = transformBody(preR.ctx, o2.body)
          val r1: TPostResult[Context, Attr] = transformAttr(r0.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(body = r0.resultOpt.getOrElse(o2.body), attr = r1.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r1.ctx, None())
        case o2: Stmt.If =>
          val r0: TPostResult[Context, Exp] = transformExp(preR.ctx, o2.cond)
          val r1: TPostResult[Context, Body] = transformBody(r0.ctx, o2.thenBody)
          val r2: TPostResult[Context, Body] = transformBody(r1.ctx, o2.elseBody)
          val r3: TPostResult[Context, Attr] = transformAttr(r2.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty || r3.resultOpt.nonEmpty)
            TPostResult(r3.ctx, Some(o2(cond = r0.resultOpt.getOrElse(o2.cond), thenBody = r1.resultOpt.getOrElse(o2.thenBody), elseBody = r2.resultOpt.getOrElse(o2.elseBody), attr = r3.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r3.ctx, None())
        case o2: Stmt.Match =>
          val r0: TPostResult[Context, Exp] = transformExp(preR.ctx, o2.exp)
          val r1: TPostResult[Context, IS[Z, Case]] = transformISZ(r0.ctx, o2.cases, transformCase _)
          val r2: TPostResult[Context, Attr] = transformAttr(r1.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(exp = r0.resultOpt.getOrElse(o2.exp), cases = r1.resultOpt.getOrElse(o2.cases), attr = r2.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r2.ctx, None())
        case o2: Stmt.Return =>
          val r0: TPostResult[Context, Option[Exp]] = transformOption(preR.ctx, o2.expOpt, transformExp _)
          val r1: TPostResult[Context, TypedAttr] = transformTypedAttr(r0.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(expOpt = r0.resultOpt.getOrElse(o2.expOpt), attr = r1.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r1.ctx, None())
        case o2: Stmt.Expr =>
          val r0: TPostResult[Context, Exp] = transformExp(preR.ctx, o2.exp)
          val r1: TPostResult[Context, TypedAttr] = transformTypedAttr(r0.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(exp = r0.resultOpt.getOrElse(o2.exp), attr = r1.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r1.ctx, None())
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: AssignExp = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, AssignExp] = pp.postAssignExp(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformLClause(ctx: Context, o: LClause): TPostResult[Context, LClause] = {
    val preR: PreResult[Context, LClause] = pp.preLClause(ctx, o)
    val r: TPostResult[Context, LClause] = if (preR.continu) {
      val o2: LClause = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: TPostResult[Context, LClause] = o2 match {
        case o2: LClause.Sequent =>
          val r0: TPostResult[Context, IS[Z, Exp]] = transformISZ(preR.ctx, o2.premises, transformExp _)
          val r1: TPostResult[Context, IS[Z, Exp]] = transformISZ(r0.ctx, o2.conclusions, transformExp _)
          val r2: TPostResult[Context, Option[LClause.Proof]] = transformOption(r1.ctx, o2.proofOpt, transformLClauseProof _)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(premises = r0.resultOpt.getOrElse(o2.premises), conclusions = r1.resultOpt.getOrElse(o2.conclusions), proofOpt = r2.resultOpt.getOrElse(o2.proofOpt))))
          else
            TPostResult(r2.ctx, None())
        case o2: LClause.Proof =>
          val r0: TPostResult[Context, IS[Z, ProofStep]] = transformISZ(preR.ctx, o2.steps, transformProofStep _)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(steps = r0.resultOpt.getOrElse(o2.steps))))
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
    val o2: LClause = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, LClause] = pp.postLClause(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformCase(ctx: Context, o: Case): TPostResult[Context, Case] = {
    val preR: PreResult[Context, Case] = pp.preCase(ctx, o)
    val r: TPostResult[Context, Case] = if (preR.continu) {
      val o2: Case = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, Pattern] = transformPattern(preR.ctx, o2.pattern)
      val r1: TPostResult[Context, Option[Exp]] = transformOption(r0.ctx, o2.condOpt, transformExp _)
      val r2: TPostResult[Context, Body] = transformBody(r1.ctx, o2.body)
      if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
        TPostResult(r2.ctx, Some(o2(pattern = r0.resultOpt.getOrElse(o2.pattern), condOpt = r1.resultOpt.getOrElse(o2.condOpt), body = r2.resultOpt.getOrElse(o2.body))))
      else
        TPostResult(r2.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: Case = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Case] = pp.postCase(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformEnumGenRange(ctx: Context, o: EnumGen.Range): TPostResult[Context, EnumGen.Range] = {
    val preR: PreResult[Context, EnumGen.Range] = pp.preEnumGenRange(ctx, o)
    val r: TPostResult[Context, EnumGen.Range] = if (preR.continu) {
      val o2: EnumGen.Range = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: TPostResult[Context, EnumGen.Range] = o2 match {
        case o2: EnumGen.Range.Expr =>
          val r0: TPostResult[Context, Exp] = transformExp(preR.ctx, o2.exp)
          val r1: TPostResult[Context, Attr] = transformAttr(r0.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(exp = r0.resultOpt.getOrElse(o2.exp), attr = r1.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r1.ctx, None())
        case o2: EnumGen.Range.Step =>
          val r0: TPostResult[Context, Exp] = transformExp(preR.ctx, o2.start)
          val r1: TPostResult[Context, Exp] = transformExp(r0.ctx, o2.end)
          val r2: TPostResult[Context, Option[Exp]] = transformOption(r1.ctx, o2.byOpt, transformExp _)
          val r3: TPostResult[Context, Attr] = transformAttr(r2.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty || r3.resultOpt.nonEmpty)
            TPostResult(r3.ctx, Some(o2(start = r0.resultOpt.getOrElse(o2.start), end = r1.resultOpt.getOrElse(o2.end), byOpt = r2.resultOpt.getOrElse(o2.byOpt), attr = r3.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r3.ctx, None())
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: EnumGen.Range = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, EnumGen.Range] = pp.postEnumGenRange(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformEnumGenFor(ctx: Context, o: EnumGen.For): TPostResult[Context, EnumGen.For] = {
    val preR: PreResult[Context, EnumGen.For] = pp.preEnumGenFor(ctx, o)
    val r: TPostResult[Context, EnumGen.For] = if (preR.continu) {
      val o2: EnumGen.For = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, Option[Id]] = transformOption(preR.ctx, o2.idOpt, transformId _)
      val r1: TPostResult[Context, EnumGen.Range] = transformEnumGenRange(r0.ctx, o2.range)
      val r2: TPostResult[Context, Option[Exp]] = transformOption(r1.ctx, o2.condOpt, transformExp _)
      if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
        TPostResult(r2.ctx, Some(o2(idOpt = r0.resultOpt.getOrElse(o2.idOpt), range = r1.resultOpt.getOrElse(o2.range), condOpt = r2.resultOpt.getOrElse(o2.condOpt))))
      else
        TPostResult(r2.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: EnumGen.For = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, EnumGen.For] = pp.postEnumGenFor(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformType(ctx: Context, o: Type): TPostResult[Context, Type] = {
    val preR: PreResult[Context, Type] = pp.preType(ctx, o)
    val r: TPostResult[Context, Type] = if (preR.continu) {
      val o2: Type = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: TPostResult[Context, Type] = o2 match {
        case o2: Type.Named =>
          val r0: TPostResult[Context, Name] = transformName(preR.ctx, o2.name)
          val r1: TPostResult[Context, IS[Z, Type]] = transformISZ(r0.ctx, o2.typeArgs, transformType _)
          val r2: TPostResult[Context, TypedAttr] = transformTypedAttr(r1.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(name = r0.resultOpt.getOrElse(o2.name), typeArgs = r1.resultOpt.getOrElse(o2.typeArgs), attr = r2.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r2.ctx, None())
        case o2: Type.Fun =>
          val r0: TPostResult[Context, IS[Z, Type]] = transformISZ(preR.ctx, o2.args, transformType _)
          val r1: TPostResult[Context, Type] = transformType(r0.ctx, o2.ret)
          val r2: TPostResult[Context, TypedAttr] = transformTypedAttr(r1.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(args = r0.resultOpt.getOrElse(o2.args), ret = r1.resultOpt.getOrElse(o2.ret), attr = r2.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r2.ctx, None())
        case o2: Type.Tuple =>
          val r0: TPostResult[Context, IS[Z, Type]] = transformISZ(preR.ctx, o2.args, transformType _)
          val r1: TPostResult[Context, TypedAttr] = transformTypedAttr(r0.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(args = r0.resultOpt.getOrElse(o2.args), attr = r1.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r1.ctx, None())
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: Type = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Type] = pp.postType(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformPattern(ctx: Context, o: Pattern): TPostResult[Context, Pattern] = {
    val preR: PreResult[Context, Pattern] = pp.prePattern(ctx, o)
    val r: TPostResult[Context, Pattern] = if (preR.continu) {
      val o2: Pattern = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: TPostResult[Context, Pattern] = o2 match {
        case o2: Pattern.Literal =>
          val r0: TPostResult[Context, Lit] = transformLit(preR.ctx, o2.lit)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(lit = r0.resultOpt.getOrElse(o2.lit))))
          else
            TPostResult(r0.ctx, None())
        case o2: Pattern.LitInterpolate =>
          val r0: TPostResult[Context, TypedAttr] = transformTypedAttr(preR.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(attr = r0.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r0.ctx, None())
        case o2: Pattern.Ref =>
          val r0: TPostResult[Context, Name] = transformName(preR.ctx, o2.name)
          val r1: TPostResult[Context, ResolvedAttr] = transformResolvedAttr(r0.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(name = r0.resultOpt.getOrElse(o2.name), attr = r1.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r1.ctx, None())
        case o2: Pattern.VarBinding =>
          val r0: TPostResult[Context, Id] = transformId(preR.ctx, o2.id)
          val r1: TPostResult[Context, Option[Type]] = transformOption(r0.ctx, o2.tipeOpt, transformType _)
          val r2: TPostResult[Context, TypedAttr] = transformTypedAttr(r1.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(id = r0.resultOpt.getOrElse(o2.id), tipeOpt = r1.resultOpt.getOrElse(o2.tipeOpt), attr = r2.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r2.ctx, None())
        case o2: Pattern.Wildcard =>
          val r0: TPostResult[Context, Option[Type]] = transformOption(preR.ctx, o2.typeOpt, transformType _)
          val r1: TPostResult[Context, TypedAttr] = transformTypedAttr(r0.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(typeOpt = r0.resultOpt.getOrElse(o2.typeOpt), attr = r1.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r1.ctx, None())
        case o2: Pattern.SeqWildcard =>
          val r0: TPostResult[Context, TypedAttr] = transformTypedAttr(preR.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(attr = r0.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r0.ctx, None())
        case o2: Pattern.Structure =>
          val r0: TPostResult[Context, Option[Id]] = transformOption(preR.ctx, o2.idOpt, transformId _)
          val r1: TPostResult[Context, Option[Name]] = transformOption(r0.ctx, o2.nameOpt, transformName _)
          val r2: TPostResult[Context, IS[Z, Pattern]] = transformISZ(r1.ctx, o2.patterns, transformPattern _)
          val r3: TPostResult[Context, ResolvedAttr] = transformResolvedAttr(r2.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty || r3.resultOpt.nonEmpty)
            TPostResult(r3.ctx, Some(o2(idOpt = r0.resultOpt.getOrElse(o2.idOpt), nameOpt = r1.resultOpt.getOrElse(o2.nameOpt), patterns = r2.resultOpt.getOrElse(o2.patterns), attr = r3.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r3.ctx, None())
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: Pattern = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Pattern] = pp.postPattern(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformExp(ctx: Context, o: Exp): TPostResult[Context, Exp] = {
    val preR: PreResult[Context, Exp] = pp.preExp(ctx, o)
    val r: TPostResult[Context, Exp] = if (preR.continu) {
      val o2: Exp = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: TPostResult[Context, Exp] = o2 match {
        case o2: Exp.LitB =>
          val r0: TPostResult[Context, Attr] = transformAttr(preR.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(attr = r0.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r0.ctx, None())
        case o2: Exp.LitC =>
          val r0: TPostResult[Context, Attr] = transformAttr(preR.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(attr = r0.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r0.ctx, None())
        case o2: Exp.LitZ =>
          val r0: TPostResult[Context, Attr] = transformAttr(preR.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(attr = r0.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r0.ctx, None())
        case o2: Exp.LitF32 =>
          val r0: TPostResult[Context, Attr] = transformAttr(preR.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(attr = r0.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r0.ctx, None())
        case o2: Exp.LitF64 =>
          val r0: TPostResult[Context, Attr] = transformAttr(preR.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(attr = r0.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r0.ctx, None())
        case o2: Exp.LitR =>
          val r0: TPostResult[Context, Attr] = transformAttr(preR.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(attr = r0.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r0.ctx, None())
        case o2: Exp.LitString =>
          val r0: TPostResult[Context, Attr] = transformAttr(preR.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(attr = r0.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r0.ctx, None())
        case o2: Exp.StringInterpolate =>
          val r0: TPostResult[Context, IS[Z, Exp.LitString]] = transformISZ(preR.ctx, o2.lits, transformExpLitString _)
          val r1: TPostResult[Context, IS[Z, Exp]] = transformISZ(r0.ctx, o2.args, transformExp _)
          val r2: TPostResult[Context, TypedAttr] = transformTypedAttr(r1.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(lits = r0.resultOpt.getOrElse(o2.lits), args = r1.resultOpt.getOrElse(o2.args), attr = r2.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r2.ctx, None())
        case o2: Exp.This =>
          val r0: TPostResult[Context, TypedAttr] = transformTypedAttr(preR.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(attr = r0.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r0.ctx, None())
        case o2: Exp.Super =>
          val r0: TPostResult[Context, Option[Id]] = transformOption(preR.ctx, o2.idOpt, transformId _)
          val r1: TPostResult[Context, TypedAttr] = transformTypedAttr(r0.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(idOpt = r0.resultOpt.getOrElse(o2.idOpt), attr = r1.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r1.ctx, None())
        case o2: Exp.Unary =>
          val r0: TPostResult[Context, Exp] = transformExp(preR.ctx, o2.exp)
          val r1: TPostResult[Context, ResolvedAttr] = transformResolvedAttr(r0.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(exp = r0.resultOpt.getOrElse(o2.exp), attr = r1.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r1.ctx, None())
        case o2: Exp.Binary =>
          val r0: TPostResult[Context, Exp] = transformExp(preR.ctx, o2.left)
          val r1: TPostResult[Context, Exp] = transformExp(r0.ctx, o2.right)
          val r2: TPostResult[Context, ResolvedAttr] = transformResolvedAttr(r1.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(left = r0.resultOpt.getOrElse(o2.left), right = r1.resultOpt.getOrElse(o2.right), attr = r2.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r2.ctx, None())
        case o2: Exp.Ident =>
          val r0: TPostResult[Context, Id] = transformId(preR.ctx, o2.id)
          val r1: TPostResult[Context, ResolvedAttr] = transformResolvedAttr(r0.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(id = r0.resultOpt.getOrElse(o2.id), attr = r1.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r1.ctx, None())
        case o2: Exp.Eta =>
          val r0: TPostResult[Context, Exp.Ref] = transformExpRef(preR.ctx, o2.ref)
          val r1: TPostResult[Context, TypedAttr] = transformTypedAttr(r0.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(ref = r0.resultOpt.getOrElse(o2.ref), attr = r1.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r1.ctx, None())
        case o2: Exp.Tuple =>
          val r0: TPostResult[Context, IS[Z, Exp]] = transformISZ(preR.ctx, o2.args, transformExp _)
          val r1: TPostResult[Context, TypedAttr] = transformTypedAttr(r0.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(args = r0.resultOpt.getOrElse(o2.args), attr = r1.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r1.ctx, None())
        case o2: Exp.Select =>
          val r0: TPostResult[Context, Option[Exp]] = transformOption(preR.ctx, o2.receiverOpt, transformExp _)
          val r1: TPostResult[Context, Id] = transformId(r0.ctx, o2.id)
          val r2: TPostResult[Context, IS[Z, Type]] = transformISZ(r1.ctx, o2.targs, transformType _)
          val r3: TPostResult[Context, ResolvedAttr] = transformResolvedAttr(r2.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty || r3.resultOpt.nonEmpty)
            TPostResult(r3.ctx, Some(o2(receiverOpt = r0.resultOpt.getOrElse(o2.receiverOpt), id = r1.resultOpt.getOrElse(o2.id), targs = r2.resultOpt.getOrElse(o2.targs), attr = r3.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r3.ctx, None())
        case o2: Exp.Invoke =>
          val r0: TPostResult[Context, Option[Exp]] = transformOption(preR.ctx, o2.receiverOpt, transformExp _)
          val r1: TPostResult[Context, Exp.Ident] = transformExpIdent(r0.ctx, o2.ident)
          val r2: TPostResult[Context, IS[Z, Type]] = transformISZ(r1.ctx, o2.targs, transformType _)
          val r3: TPostResult[Context, IS[Z, Exp]] = transformISZ(r2.ctx, o2.args, transformExp _)
          val r4: TPostResult[Context, ResolvedAttr] = transformResolvedAttr(r3.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty || r3.resultOpt.nonEmpty || r4.resultOpt.nonEmpty)
            TPostResult(r4.ctx, Some(o2(receiverOpt = r0.resultOpt.getOrElse(o2.receiverOpt), ident = r1.resultOpt.getOrElse(o2.ident), targs = r2.resultOpt.getOrElse(o2.targs), args = r3.resultOpt.getOrElse(o2.args), attr = r4.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r4.ctx, None())
        case o2: Exp.InvokeNamed =>
          val r0: TPostResult[Context, Option[Exp]] = transformOption(preR.ctx, o2.receiverOpt, transformExp _)
          val r1: TPostResult[Context, Exp.Ident] = transformExpIdent(r0.ctx, o2.ident)
          val r2: TPostResult[Context, IS[Z, Type]] = transformISZ(r1.ctx, o2.targs, transformType _)
          val r3: TPostResult[Context, IS[Z, NamedArg]] = transformISZ(r2.ctx, o2.args, transformNamedArg _)
          val r4: TPostResult[Context, ResolvedAttr] = transformResolvedAttr(r3.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty || r3.resultOpt.nonEmpty || r4.resultOpt.nonEmpty)
            TPostResult(r4.ctx, Some(o2(receiverOpt = r0.resultOpt.getOrElse(o2.receiverOpt), ident = r1.resultOpt.getOrElse(o2.ident), targs = r2.resultOpt.getOrElse(o2.targs), args = r3.resultOpt.getOrElse(o2.args), attr = r4.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r4.ctx, None())
        case o2: Exp.If =>
          val r0: TPostResult[Context, Exp] = transformExp(preR.ctx, o2.cond)
          val r1: TPostResult[Context, Exp] = transformExp(r0.ctx, o2.thenExp)
          val r2: TPostResult[Context, Exp] = transformExp(r1.ctx, o2.elseExp)
          val r3: TPostResult[Context, TypedAttr] = transformTypedAttr(r2.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty || r3.resultOpt.nonEmpty)
            TPostResult(r3.ctx, Some(o2(cond = r0.resultOpt.getOrElse(o2.cond), thenExp = r1.resultOpt.getOrElse(o2.thenExp), elseExp = r2.resultOpt.getOrElse(o2.elseExp), attr = r3.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r3.ctx, None())
        case o2: Exp.Fun =>
          val r0: TPostResult[Context, IS[Z, Exp.Fun.Param]] = transformISZ(preR.ctx, o2.params, transformExpFunParam _)
          val r1: TPostResult[Context, AssignExp] = transformAssignExp(r0.ctx, o2.exp)
          val r2: TPostResult[Context, TypedAttr] = transformTypedAttr(r1.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(params = r0.resultOpt.getOrElse(o2.params), exp = r1.resultOpt.getOrElse(o2.exp), attr = r2.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r2.ctx, None())
        case o2: Exp.ForYield =>
          val r0: TPostResult[Context, IS[Z, EnumGen.For]] = transformISZ(preR.ctx, o2.enumGens, transformEnumGenFor _)
          val r1: TPostResult[Context, Exp] = transformExp(r0.ctx, o2.exp)
          val r2: TPostResult[Context, TypedAttr] = transformTypedAttr(r1.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(enumGens = r0.resultOpt.getOrElse(o2.enumGens), exp = r1.resultOpt.getOrElse(o2.exp), attr = r2.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r2.ctx, None())
        case o2: Exp.Quant =>
          val r0: TPostResult[Context, IS[Z, VarFragment]] = transformISZ(preR.ctx, o2.varFragments, transformVarFragment _)
          val r1: TPostResult[Context, Exp] = transformExp(r0.ctx, o2.exp)
          val r2: TPostResult[Context, Attr] = transformAttr(r1.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(varFragments = r0.resultOpt.getOrElse(o2.varFragments), exp = r1.resultOpt.getOrElse(o2.exp), attr = r2.resultOpt.getOrElse(o2.attr))))
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
    val o2: Exp = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Exp] = pp.postExp(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformLit(ctx: Context, o: Lit): TPostResult[Context, Lit] = {
    val preR: PreResult[Context, Lit] = pp.preLit(ctx, o)
    val r: TPostResult[Context, Lit] = if (preR.continu) {
      val o2: Lit = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: TPostResult[Context, Lit] = o2 match {
        case o2: Exp.LitB =>
          val r0: TPostResult[Context, Attr] = transformAttr(preR.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(attr = r0.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r0.ctx, None())
        case o2: Exp.LitC =>
          val r0: TPostResult[Context, Attr] = transformAttr(preR.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(attr = r0.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r0.ctx, None())
        case o2: Exp.LitZ =>
          val r0: TPostResult[Context, Attr] = transformAttr(preR.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(attr = r0.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r0.ctx, None())
        case o2: Exp.LitF32 =>
          val r0: TPostResult[Context, Attr] = transformAttr(preR.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(attr = r0.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r0.ctx, None())
        case o2: Exp.LitF64 =>
          val r0: TPostResult[Context, Attr] = transformAttr(preR.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(attr = r0.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r0.ctx, None())
        case o2: Exp.LitR =>
          val r0: TPostResult[Context, Attr] = transformAttr(preR.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(attr = r0.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r0.ctx, None())
        case o2: Exp.LitString =>
          val r0: TPostResult[Context, Attr] = transformAttr(preR.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(attr = r0.resultOpt.getOrElse(o2.attr))))
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
    val o2: Lit = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Lit] = pp.postLit(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformExpRef(ctx: Context, o: Exp.Ref): TPostResult[Context, Exp.Ref] = {
    val preR: PreResult[Context, Exp.Ref] = pp.preExpRef(ctx, o)
    val r: TPostResult[Context, Exp.Ref] = if (preR.continu) {
      val o2: Exp.Ref = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: TPostResult[Context, Exp.Ref] = o2 match {
        case o2: Exp.Ident =>
          val r0: TPostResult[Context, Id] = transformId(preR.ctx, o2.id)
          val r1: TPostResult[Context, ResolvedAttr] = transformResolvedAttr(r0.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(id = r0.resultOpt.getOrElse(o2.id), attr = r1.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r1.ctx, None())
        case o2: Exp.Select =>
          val r0: TPostResult[Context, Option[Exp]] = transformOption(preR.ctx, o2.receiverOpt, transformExp _)
          val r1: TPostResult[Context, Id] = transformId(r0.ctx, o2.id)
          val r2: TPostResult[Context, IS[Z, Type]] = transformISZ(r1.ctx, o2.targs, transformType _)
          val r3: TPostResult[Context, ResolvedAttr] = transformResolvedAttr(r2.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty || r3.resultOpt.nonEmpty)
            TPostResult(r3.ctx, Some(o2(receiverOpt = r0.resultOpt.getOrElse(o2.receiverOpt), id = r1.resultOpt.getOrElse(o2.id), targs = r2.resultOpt.getOrElse(o2.targs), attr = r3.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r3.ctx, None())
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: Exp.Ref = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Exp.Ref] = pp.postExpRef(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformExpFunParam(ctx: Context, o: Exp.Fun.Param): TPostResult[Context, Exp.Fun.Param] = {
    val preR: PreResult[Context, Exp.Fun.Param] = pp.preExpFunParam(ctx, o)
    val r: TPostResult[Context, Exp.Fun.Param] = if (preR.continu) {
      val o2: Exp.Fun.Param = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, Id] = transformId(preR.ctx, o2.id)
      val r1: TPostResult[Context, Option[Type]] = transformOption(r0.ctx, o2.tipeOpt, transformType _)
      val r2: TPostResult[Context, Option[Typed]] = transformOption(r1.ctx, o2.typedOpt, transformTyped _)
      if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
        TPostResult(r2.ctx, Some(o2(id = r0.resultOpt.getOrElse(o2.id), tipeOpt = r1.resultOpt.getOrElse(o2.tipeOpt), typedOpt = r2.resultOpt.getOrElse(o2.typedOpt))))
      else
        TPostResult(r2.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: Exp.Fun.Param = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Exp.Fun.Param] = pp.postExpFunParam(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformNamedArg(ctx: Context, o: NamedArg): TPostResult[Context, NamedArg] = {
    val preR: PreResult[Context, NamedArg] = pp.preNamedArg(ctx, o)
    val r: TPostResult[Context, NamedArg] = if (preR.continu) {
      val o2: NamedArg = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, Id] = transformId(preR.ctx, o2.id)
      val r1: TPostResult[Context, Exp] = transformExp(r0.ctx, o2.arg)
      if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
        TPostResult(r1.ctx, Some(o2(id = r0.resultOpt.getOrElse(o2.id), arg = r1.resultOpt.getOrElse(o2.arg))))
      else
        TPostResult(r1.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: NamedArg = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, NamedArg] = pp.postNamedArg(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformVarFragment(ctx: Context, o: VarFragment): TPostResult[Context, VarFragment] = {
    val preR: PreResult[Context, VarFragment] = pp.preVarFragment(ctx, o)
    val r: TPostResult[Context, VarFragment] = if (preR.continu) {
      val o2: VarFragment = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, IS[Z, Id]] = transformISZ(preR.ctx, o2.ids, transformId _)
      val r1: TPostResult[Context, Option[Domain]] = transformOption(r0.ctx, o2.domainOpt, transformDomain _)
      if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
        TPostResult(r1.ctx, Some(o2(ids = r0.resultOpt.getOrElse(o2.ids), domainOpt = r1.resultOpt.getOrElse(o2.domainOpt))))
      else
        TPostResult(r1.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: VarFragment = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, VarFragment] = pp.postVarFragment(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformDomain(ctx: Context, o: Domain): TPostResult[Context, Domain] = {
    val preR: PreResult[Context, Domain] = pp.preDomain(ctx, o)
    val r: TPostResult[Context, Domain] = if (preR.continu) {
      val o2: Domain = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: TPostResult[Context, Domain] = o2 match {
        case o2: Domain.Type =>
          val r0: TPostResult[Context, Type] = transformType(preR.ctx, o2.tipe)
          val r1: TPostResult[Context, TypedAttr] = transformTypedAttr(r0.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(tipe = r0.resultOpt.getOrElse(o2.tipe), attr = r1.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r1.ctx, None())
        case o2: Domain.Range =>
          val r0: TPostResult[Context, Exp] = transformExp(preR.ctx, o2.lo)
          val r1: TPostResult[Context, Exp] = transformExp(r0.ctx, o2.hi)
          val r2: TPostResult[Context, TypedAttr] = transformTypedAttr(r1.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(lo = r0.resultOpt.getOrElse(o2.lo), hi = r1.resultOpt.getOrElse(o2.hi), attr = r2.resultOpt.getOrElse(o2.attr))))
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
    val o2: Domain = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Domain] = pp.postDomain(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformId(ctx: Context, o: Id): TPostResult[Context, Id] = {
    val preR: PreResult[Context, Id] = pp.preId(ctx, o)
    val r: TPostResult[Context, Id] = if (preR.continu) {
      val o2: Id = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, Attr] = transformAttr(preR.ctx, o2.attr)
      if (hasChanged || r0.resultOpt.nonEmpty)
        TPostResult(r0.ctx, Some(o2(attr = r0.resultOpt.getOrElse(o2.attr))))
      else
        TPostResult(r0.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: Id = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Id] = pp.postId(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformName(ctx: Context, o: Name): TPostResult[Context, Name] = {
    val preR: PreResult[Context, Name] = pp.preName(ctx, o)
    val r: TPostResult[Context, Name] = if (preR.continu) {
      val o2: Name = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, IS[Z, Id]] = transformISZ(preR.ctx, o2.ids, transformId _)
      val r1: TPostResult[Context, Attr] = transformAttr(r0.ctx, o2.attr)
      if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
        TPostResult(r1.ctx, Some(o2(ids = r0.resultOpt.getOrElse(o2.ids), attr = r1.resultOpt.getOrElse(o2.attr))))
      else
        TPostResult(r1.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: Name = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Name] = pp.postName(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformBody(ctx: Context, o: Body): TPostResult[Context, Body] = {
    val preR: PreResult[Context, Body] = pp.preBody(ctx, o)
    val r: TPostResult[Context, Body] = if (preR.continu) {
      val o2: Body = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, IS[Z, Stmt]] = transformISZ(preR.ctx, o2.stmts, transformStmt _)
      val r1: TPostResult[Context, IS[Z, ResolvedInfo.LocalVar]] = transformISZ(r0.ctx, o2.undecls, transformResolvedInfoLocalVar _)
      if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
        TPostResult(r1.ctx, Some(o2(stmts = r0.resultOpt.getOrElse(o2.stmts), undecls = r1.resultOpt.getOrElse(o2.undecls))))
      else
        TPostResult(r1.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: Body = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Body] = pp.postBody(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformAdtParam(ctx: Context, o: AdtParam): TPostResult[Context, AdtParam] = {
    val preR: PreResult[Context, AdtParam] = pp.preAdtParam(ctx, o)
    val r: TPostResult[Context, AdtParam] = if (preR.continu) {
      val o2: AdtParam = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, Id] = transformId(preR.ctx, o2.id)
      val r1: TPostResult[Context, Type] = transformType(r0.ctx, o2.tipe)
      if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
        TPostResult(r1.ctx, Some(o2(id = r0.resultOpt.getOrElse(o2.id), tipe = r1.resultOpt.getOrElse(o2.tipe))))
      else
        TPostResult(r1.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: AdtParam = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, AdtParam] = pp.postAdtParam(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformMethodSig(ctx: Context, o: MethodSig): TPostResult[Context, MethodSig] = {
    val preR: PreResult[Context, MethodSig] = pp.preMethodSig(ctx, o)
    val r: TPostResult[Context, MethodSig] = if (preR.continu) {
      val o2: MethodSig = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, Id] = transformId(preR.ctx, o2.id)
      val r1: TPostResult[Context, IS[Z, TypeParam]] = transformISZ(r0.ctx, o2.typeParams, transformTypeParam _)
      val r2: TPostResult[Context, IS[Z, Param]] = transformISZ(r1.ctx, o2.params, transformParam _)
      val r3: TPostResult[Context, Type] = transformType(r2.ctx, o2.returnType)
      if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty || r3.resultOpt.nonEmpty)
        TPostResult(r3.ctx, Some(o2(id = r0.resultOpt.getOrElse(o2.id), typeParams = r1.resultOpt.getOrElse(o2.typeParams), params = r2.resultOpt.getOrElse(o2.params), returnType = r3.resultOpt.getOrElse(o2.returnType))))
      else
        TPostResult(r3.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: MethodSig = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, MethodSig] = pp.postMethodSig(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformParam(ctx: Context, o: Param): TPostResult[Context, Param] = {
    val preR: PreResult[Context, Param] = pp.preParam(ctx, o)
    val r: TPostResult[Context, Param] = if (preR.continu) {
      val o2: Param = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, Id] = transformId(preR.ctx, o2.id)
      val r1: TPostResult[Context, Type] = transformType(r0.ctx, o2.tipe)
      if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
        TPostResult(r1.ctx, Some(o2(id = r0.resultOpt.getOrElse(o2.id), tipe = r1.resultOpt.getOrElse(o2.tipe))))
      else
        TPostResult(r1.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: Param = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Param] = pp.postParam(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformTypeParam(ctx: Context, o: TypeParam): TPostResult[Context, TypeParam] = {
    val preR: PreResult[Context, TypeParam] = pp.preTypeParam(ctx, o)
    val r: TPostResult[Context, TypeParam] = if (preR.continu) {
      val o2: TypeParam = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, Id] = transformId(preR.ctx, o2.id)
      if (hasChanged || r0.resultOpt.nonEmpty)
        TPostResult(r0.ctx, Some(o2(id = r0.resultOpt.getOrElse(o2.id))))
      else
        TPostResult(r0.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: TypeParam = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, TypeParam] = pp.postTypeParam(r.ctx, o2)
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

  @pure def transformAttr(ctx: Context, o: Attr): TPostResult[Context, Attr] = {
    val preR: PreResult[Context, Attr] = pp.preAttr(ctx, o)
    val r: TPostResult[Context, Attr] = if (preR.continu) {
      val o2: Attr = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      if (hasChanged)
        TPostResult(preR.ctx, Some(o2))
      else
        TPostResult(preR.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: Attr = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Attr] = pp.postAttr(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformTypedAttr(ctx: Context, o: TypedAttr): TPostResult[Context, TypedAttr] = {
    val preR: PreResult[Context, TypedAttr] = pp.preTypedAttr(ctx, o)
    val r: TPostResult[Context, TypedAttr] = if (preR.continu) {
      val o2: TypedAttr = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, Option[Typed]] = transformOption(preR.ctx, o2.typedOpt, transformTyped _)
      if (hasChanged || r0.resultOpt.nonEmpty)
        TPostResult(r0.ctx, Some(o2(typedOpt = r0.resultOpt.getOrElse(o2.typedOpt))))
      else
        TPostResult(r0.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: TypedAttr = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, TypedAttr] = pp.postTypedAttr(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformResolvedAttr(ctx: Context, o: ResolvedAttr): TPostResult[Context, ResolvedAttr] = {
    val preR: PreResult[Context, ResolvedAttr] = pp.preResolvedAttr(ctx, o)
    val r: TPostResult[Context, ResolvedAttr] = if (preR.continu) {
      val o2: ResolvedAttr = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, Option[ResolvedInfo]] = transformOption(preR.ctx, o2.resOpt, transformResolvedInfo _)
      val r1: TPostResult[Context, Option[Typed]] = transformOption(r0.ctx, o2.typedOpt, transformTyped _)
      if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
        TPostResult(r1.ctx, Some(o2(resOpt = r0.resultOpt.getOrElse(o2.resOpt), typedOpt = r1.resultOpt.getOrElse(o2.typedOpt))))
      else
        TPostResult(r1.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: ResolvedAttr = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, ResolvedAttr] = pp.postResolvedAttr(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformResolvedInfo(ctx: Context, o: ResolvedInfo): TPostResult[Context, ResolvedInfo] = {
    val preR: PreResult[Context, ResolvedInfo] = pp.preResolvedInfo(ctx, o)
    val r: TPostResult[Context, ResolvedInfo] = if (preR.continu) {
      val o2: ResolvedInfo = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: TPostResult[Context, ResolvedInfo] = o2 match {
        case o2: ResolvedInfo.BuiltIn =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: ResolvedInfo.Package =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: ResolvedInfo.Enum =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: ResolvedInfo.EnumElement =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: ResolvedInfo.Object =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: ResolvedInfo.Var =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: ResolvedInfo.Method =>
          val r0: TPostResult[Context, Option[Typed.Fun]] = transformOption(preR.ctx, o2.tpeOpt, transformTypedFun _)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(tpeOpt = r0.resultOpt.getOrElse(o2.tpeOpt))))
          else
            TPostResult(r0.ctx, None())
        case o2: ResolvedInfo.Methods =>
          val r0: TPostResult[Context, IS[Z, ResolvedInfo.Method]] = transformISZ(preR.ctx, o2.methods, transformResolvedInfoMethod _)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(methods = r0.resultOpt.getOrElse(o2.methods))))
          else
            TPostResult(r0.ctx, None())
        case o2: ResolvedInfo.Tuple =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: ResolvedInfo.LocalVar =>
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
    val o2: ResolvedInfo = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, ResolvedInfo] = pp.postResolvedInfo(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformProofStep(ctx: Context, o: ProofStep): TPostResult[Context, ProofStep] = {
    val preR: PreResult[Context, ProofStep] = pp.preProofStep(ctx, o)
    val r: TPostResult[Context, ProofStep] = if (preR.continu) {
      val o2: ProofStep = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: TPostResult[Context, ProofStep] = o2 match {
        case o2: ProofStep.Basic =>
          val r0: TPostResult[Context, Exp.LitZ] = transformExpLitZ(preR.ctx, o2.step)
          val r1: TPostResult[Context, Exp] = transformExp(r0.ctx, o2.exp)
          val r2: TPostResult[Context, Just] = transformJust(r1.ctx, o2.just)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(step = r0.resultOpt.getOrElse(o2.step), exp = r1.resultOpt.getOrElse(o2.exp), just = r2.resultOpt.getOrElse(o2.just))))
          else
            TPostResult(r2.ctx, None())
        case o2: ProofStep.SubProof =>
          val r0: TPostResult[Context, Exp.LitZ] = transformExpLitZ(preR.ctx, o2.step)
          val r1: TPostResult[Context, AssumeProofStep] = transformAssumeProofStep(r0.ctx, o2.assumeStep)
          val r2: TPostResult[Context, IS[Z, ProofStep]] = transformISZ(r1.ctx, o2.steps, transformProofStep _)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(step = r0.resultOpt.getOrElse(o2.step), assumeStep = r1.resultOpt.getOrElse(o2.assumeStep), steps = r2.resultOpt.getOrElse(o2.steps))))
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
    val o2: ProofStep = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, ProofStep] = pp.postProofStep(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformAssumeProofStep(ctx: Context, o: AssumeProofStep): TPostResult[Context, AssumeProofStep] = {
    val preR: PreResult[Context, AssumeProofStep] = pp.preAssumeProofStep(ctx, o)
    val r: TPostResult[Context, AssumeProofStep] = if (preR.continu) {
      val o2: AssumeProofStep = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: TPostResult[Context, AssumeProofStep] = o2 match {
        case o2: AssumeProofStep.Regular =>
          val r0: TPostResult[Context, Exp.LitZ] = transformExpLitZ(preR.ctx, o2.step)
          val r1: TPostResult[Context, Exp] = transformExp(r0.ctx, o2.exp)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(step = r0.resultOpt.getOrElse(o2.step), exp = r1.resultOpt.getOrElse(o2.exp))))
          else
            TPostResult(r1.ctx, None())
        case o2: AssumeProofStep.ForallIntroAps =>
          val r0: TPostResult[Context, Exp.LitZ] = transformExpLitZ(preR.ctx, o2.step)
          val r1: TPostResult[Context, IS[Z, VarFragment]] = transformISZ(r0.ctx, o2.varFragments, transformVarFragment _)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(step = r0.resultOpt.getOrElse(o2.step), varFragments = r1.resultOpt.getOrElse(o2.varFragments))))
          else
            TPostResult(r1.ctx, None())
        case o2: AssumeProofStep.ExistsElimAps =>
          val r0: TPostResult[Context, Exp.LitZ] = transformExpLitZ(preR.ctx, o2.step)
          val r1: TPostResult[Context, IS[Z, VarFragment]] = transformISZ(r0.ctx, o2.varFragments, transformVarFragment _)
          val r2: TPostResult[Context, Exp] = transformExp(r1.ctx, o2.exp)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(step = r0.resultOpt.getOrElse(o2.step), varFragments = r1.resultOpt.getOrElse(o2.varFragments), exp = r2.resultOpt.getOrElse(o2.exp))))
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
    val o2: AssumeProofStep = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, AssumeProofStep] = pp.postAssumeProofStep(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformJust(ctx: Context, o: Just): TPostResult[Context, Just] = {
    val preR: PreResult[Context, Just] = pp.preJust(ctx, o)
    val r: TPostResult[Context, Just] = if (preR.continu) {
      val o2: Just = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, IS[Z, Exp]] = transformISZ(preR.ctx, o2.args, transformExp _)
      val r1: TPostResult[Context, Attr] = transformAttr(r0.ctx, o2.attr)
      if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
        TPostResult(r1.ctx, Some(o2(args = r0.resultOpt.getOrElse(o2.args), attr = r1.resultOpt.getOrElse(o2.attr))))
      else
        TPostResult(r1.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: Just = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Just] = pp.postJust(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformTruthTableRow(ctx: Context, o: TruthTable.Row): TPostResult[Context, TruthTable.Row] = {
    val preR: PreResult[Context, TruthTable.Row] = pp.preTruthTableRow(ctx, o)
    val r: TPostResult[Context, TruthTable.Row] = if (preR.continu) {
      val o2: TruthTable.Row = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, TruthTable.Assignment] = transformTruthTableAssignment(preR.ctx, o2.assignment)
      val r1: TPostResult[Context, TruthTable.Assignment] = transformTruthTableAssignment(r0.ctx, o2.values)
      if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
        TPostResult(r1.ctx, Some(o2(assignment = r0.resultOpt.getOrElse(o2.assignment), values = r1.resultOpt.getOrElse(o2.values))))
      else
        TPostResult(r1.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: TruthTable.Row = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, TruthTable.Row] = pp.postTruthTableRow(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformTruthTableAssignment(ctx: Context, o: TruthTable.Assignment): TPostResult[Context, TruthTable.Assignment] = {
    val preR: PreResult[Context, TruthTable.Assignment] = pp.preTruthTableAssignment(ctx, o)
    val r: TPostResult[Context, TruthTable.Assignment] = if (preR.continu) {
      val o2: TruthTable.Assignment = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, IS[Z, Exp.LitB]] = transformISZ(preR.ctx, o2.values, transformExpLitB _)
      val r1: TPostResult[Context, Attr] = transformAttr(r0.ctx, o2.attr)
      if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
        TPostResult(r1.ctx, Some(o2(values = r0.resultOpt.getOrElse(o2.values), attr = r1.resultOpt.getOrElse(o2.attr))))
      else
        TPostResult(r1.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: TruthTable.Assignment = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, TruthTable.Assignment] = pp.postTruthTableAssignment(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformTruthTableConclusion(ctx: Context, o: TruthTable.Conclusion): TPostResult[Context, TruthTable.Conclusion] = {
    val preR: PreResult[Context, TruthTable.Conclusion] = pp.preTruthTableConclusion(ctx, o)
    val r: TPostResult[Context, TruthTable.Conclusion] = if (preR.continu) {
      val o2: TruthTable.Conclusion = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: TPostResult[Context, TruthTable.Conclusion] = o2 match {
        case o2: TruthTable.Conclusion.Validity =>
          val r0: TPostResult[Context, IS[Z, TruthTable.Assignment]] = transformISZ(preR.ctx, o2.assignments, transformTruthTableAssignment _)
          val r1: TPostResult[Context, Attr] = transformAttr(r0.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(assignments = r0.resultOpt.getOrElse(o2.assignments), attr = r1.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r1.ctx, None())
        case o2: TruthTable.Conclusion.Tautology =>
          val r0: TPostResult[Context, Attr] = transformAttr(preR.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(attr = r0.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r0.ctx, None())
        case o2: TruthTable.Conclusion.Contradictory =>
          val r0: TPostResult[Context, Attr] = transformAttr(preR.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(attr = r0.resultOpt.getOrElse(o2.attr))))
          else
            TPostResult(r0.ctx, None())
        case o2: TruthTable.Conclusion.Contingent =>
          val r0: TPostResult[Context, IS[Z, TruthTable.Assignment]] = transformISZ(preR.ctx, o2.trueAssignments, transformTruthTableAssignment _)
          val r1: TPostResult[Context, IS[Z, TruthTable.Assignment]] = transformISZ(r0.ctx, o2.falseAssignments, transformTruthTableAssignment _)
          val r2: TPostResult[Context, Attr] = transformAttr(r1.ctx, o2.attr)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(trueAssignments = r0.resultOpt.getOrElse(o2.trueAssignments), falseAssignments = r1.resultOpt.getOrElse(o2.falseAssignments), attr = r2.resultOpt.getOrElse(o2.attr))))
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
    val o2: TruthTable.Conclusion = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, TruthTable.Conclusion] = pp.postTruthTableConclusion(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformLClauseProof(ctx: Context, o: LClause.Proof): TPostResult[Context, LClause.Proof] = {
    val preR: PreResult[Context, LClause.Proof] = pp.preLClauseProof(ctx, o) match {
     case PreResult(preCtx, continu, Some(r: LClause.Proof)) => PreResult(preCtx, continu, Some[LClause.Proof](r))
     case PreResult(_, _, Some(_)) => halt("Can only produce object of type LClause.Proof")
     case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[LClause.Proof]())
    }
    val r: TPostResult[Context, LClause.Proof] = if (preR.continu) {
      val o2: LClause.Proof = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, IS[Z, ProofStep]] = transformISZ(preR.ctx, o2.steps, transformProofStep _)
      if (hasChanged || r0.resultOpt.nonEmpty)
        TPostResult(r0.ctx, Some(o2(steps = r0.resultOpt.getOrElse(o2.steps))))
      else
        TPostResult(r0.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: LClause.Proof = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, LClause.Proof] = pp.postLClauseProof(r.ctx, o2) match {
     case TPostResult(postCtx, Some(result: LClause.Proof)) => TPostResult(postCtx, Some[LClause.Proof](result))
     case TPostResult(_, Some(_)) => halt("Can only produce object of type LClause.Proof")
     case TPostResult(postCtx, _) => TPostResult(postCtx, None[LClause.Proof]())
    }
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformLClauseSequent(ctx: Context, o: LClause.Sequent): TPostResult[Context, LClause.Sequent] = {
    val preR: PreResult[Context, LClause.Sequent] = pp.preLClauseSequent(ctx, o) match {
     case PreResult(preCtx, continu, Some(r: LClause.Sequent)) => PreResult(preCtx, continu, Some[LClause.Sequent](r))
     case PreResult(_, _, Some(_)) => halt("Can only produce object of type LClause.Sequent")
     case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[LClause.Sequent]())
    }
    val r: TPostResult[Context, LClause.Sequent] = if (preR.continu) {
      val o2: LClause.Sequent = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, IS[Z, Exp]] = transformISZ(preR.ctx, o2.premises, transformExp _)
      val r1: TPostResult[Context, IS[Z, Exp]] = transformISZ(r0.ctx, o2.conclusions, transformExp _)
      val r2: TPostResult[Context, Option[LClause.Proof]] = transformOption(r1.ctx, o2.proofOpt, transformLClauseProof _)
      if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
        TPostResult(r2.ctx, Some(o2(premises = r0.resultOpt.getOrElse(o2.premises), conclusions = r1.resultOpt.getOrElse(o2.conclusions), proofOpt = r2.resultOpt.getOrElse(o2.proofOpt))))
      else
        TPostResult(r2.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: LClause.Sequent = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, LClause.Sequent] = pp.postLClauseSequent(r.ctx, o2) match {
     case TPostResult(postCtx, Some(result: LClause.Sequent)) => TPostResult(postCtx, Some[LClause.Sequent](result))
     case TPostResult(_, Some(_)) => halt("Can only produce object of type LClause.Sequent")
     case TPostResult(postCtx, _) => TPostResult(postCtx, None[LClause.Sequent]())
    }
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformTypeNamed(ctx: Context, o: Type.Named): TPostResult[Context, Type.Named] = {
    val preR: PreResult[Context, Type.Named] = pp.preTypeNamed(ctx, o) match {
     case PreResult(preCtx, continu, Some(r: Type.Named)) => PreResult(preCtx, continu, Some[Type.Named](r))
     case PreResult(_, _, Some(_)) => halt("Can only produce object of type Type.Named")
     case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[Type.Named]())
    }
    val r: TPostResult[Context, Type.Named] = if (preR.continu) {
      val o2: Type.Named = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, Name] = transformName(preR.ctx, o2.name)
      val r1: TPostResult[Context, IS[Z, Type]] = transformISZ(r0.ctx, o2.typeArgs, transformType _)
      val r2: TPostResult[Context, TypedAttr] = transformTypedAttr(r1.ctx, o2.attr)
      if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
        TPostResult(r2.ctx, Some(o2(name = r0.resultOpt.getOrElse(o2.name), typeArgs = r1.resultOpt.getOrElse(o2.typeArgs), attr = r2.resultOpt.getOrElse(o2.attr))))
      else
        TPostResult(r2.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: Type.Named = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Type.Named] = pp.postTypeNamed(r.ctx, o2) match {
     case TPostResult(postCtx, Some(result: Type.Named)) => TPostResult(postCtx, Some[Type.Named](result))
     case TPostResult(_, Some(_)) => halt("Can only produce object of type Type.Named")
     case TPostResult(postCtx, _) => TPostResult(postCtx, None[Type.Named]())
    }
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformExpIdent(ctx: Context, o: Exp.Ident): TPostResult[Context, Exp.Ident] = {
    val preR: PreResult[Context, Exp.Ident] = pp.preExpIdent(ctx, o) match {
     case PreResult(preCtx, continu, Some(r: Exp.Ident)) => PreResult(preCtx, continu, Some[Exp.Ident](r))
     case PreResult(_, _, Some(_)) => halt("Can only produce object of type Exp.Ident")
     case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[Exp.Ident]())
    }
    val r: TPostResult[Context, Exp.Ident] = if (preR.continu) {
      val o2: Exp.Ident = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, Id] = transformId(preR.ctx, o2.id)
      val r1: TPostResult[Context, ResolvedAttr] = transformResolvedAttr(r0.ctx, o2.attr)
      if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
        TPostResult(r1.ctx, Some(o2(id = r0.resultOpt.getOrElse(o2.id), attr = r1.resultOpt.getOrElse(o2.attr))))
      else
        TPostResult(r1.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: Exp.Ident = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Exp.Ident] = pp.postExpIdent(r.ctx, o2) match {
     case TPostResult(postCtx, Some(result: Exp.Ident)) => TPostResult(postCtx, Some[Exp.Ident](result))
     case TPostResult(_, Some(_)) => halt("Can only produce object of type Exp.Ident")
     case TPostResult(postCtx, _) => TPostResult(postCtx, None[Exp.Ident]())
    }
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformExpLitString(ctx: Context, o: Exp.LitString): TPostResult[Context, Exp.LitString] = {
    val preR: PreResult[Context, Exp.LitString] = pp.preExpLitString(ctx, o) match {
     case PreResult(preCtx, continu, Some(r: Exp.LitString)) => PreResult(preCtx, continu, Some[Exp.LitString](r))
     case PreResult(_, _, Some(_)) => halt("Can only produce object of type Exp.LitString")
     case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[Exp.LitString]())
    }
    val r: TPostResult[Context, Exp.LitString] = if (preR.continu) {
      val o2: Exp.LitString = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, Attr] = transformAttr(preR.ctx, o2.attr)
      if (hasChanged || r0.resultOpt.nonEmpty)
        TPostResult(r0.ctx, Some(o2(attr = r0.resultOpt.getOrElse(o2.attr))))
      else
        TPostResult(r0.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: Exp.LitString = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Exp.LitString] = pp.postExpLitString(r.ctx, o2) match {
     case TPostResult(postCtx, Some(result: Exp.LitString)) => TPostResult(postCtx, Some[Exp.LitString](result))
     case TPostResult(_, Some(_)) => halt("Can only produce object of type Exp.LitString")
     case TPostResult(postCtx, _) => TPostResult(postCtx, None[Exp.LitString]())
    }
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformResolvedInfoLocalVar(ctx: Context, o: ResolvedInfo.LocalVar): TPostResult[Context, ResolvedInfo.LocalVar] = {
    val preR: PreResult[Context, ResolvedInfo.LocalVar] = pp.preResolvedInfoLocalVar(ctx, o) match {
     case PreResult(preCtx, continu, Some(r: ResolvedInfo.LocalVar)) => PreResult(preCtx, continu, Some[ResolvedInfo.LocalVar](r))
     case PreResult(_, _, Some(_)) => halt("Can only produce object of type ResolvedInfo.LocalVar")
     case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[ResolvedInfo.LocalVar]())
    }
    val r: TPostResult[Context, ResolvedInfo.LocalVar] = if (preR.continu) {
      val o2: ResolvedInfo.LocalVar = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      if (hasChanged)
        TPostResult(preR.ctx, Some(o2))
      else
        TPostResult(preR.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: ResolvedInfo.LocalVar = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, ResolvedInfo.LocalVar] = pp.postResolvedInfoLocalVar(r.ctx, o2) match {
     case TPostResult(postCtx, Some(result: ResolvedInfo.LocalVar)) => TPostResult(postCtx, Some[ResolvedInfo.LocalVar](result))
     case TPostResult(_, Some(_)) => halt("Can only produce object of type ResolvedInfo.LocalVar")
     case TPostResult(postCtx, _) => TPostResult(postCtx, None[ResolvedInfo.LocalVar]())
    }
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

  @pure def transformResolvedInfoMethod(ctx: Context, o: ResolvedInfo.Method): TPostResult[Context, ResolvedInfo.Method] = {
    val preR: PreResult[Context, ResolvedInfo.Method] = pp.preResolvedInfoMethod(ctx, o) match {
     case PreResult(preCtx, continu, Some(r: ResolvedInfo.Method)) => PreResult(preCtx, continu, Some[ResolvedInfo.Method](r))
     case PreResult(_, _, Some(_)) => halt("Can only produce object of type ResolvedInfo.Method")
     case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[ResolvedInfo.Method]())
    }
    val r: TPostResult[Context, ResolvedInfo.Method] = if (preR.continu) {
      val o2: ResolvedInfo.Method = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, Option[Typed.Fun]] = transformOption(preR.ctx, o2.tpeOpt, transformTypedFun _)
      if (hasChanged || r0.resultOpt.nonEmpty)
        TPostResult(r0.ctx, Some(o2(tpeOpt = r0.resultOpt.getOrElse(o2.tpeOpt))))
      else
        TPostResult(r0.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: ResolvedInfo.Method = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, ResolvedInfo.Method] = pp.postResolvedInfoMethod(r.ctx, o2) match {
     case TPostResult(postCtx, Some(result: ResolvedInfo.Method)) => TPostResult(postCtx, Some[ResolvedInfo.Method](result))
     case TPostResult(_, Some(_)) => halt("Can only produce object of type ResolvedInfo.Method")
     case TPostResult(postCtx, _) => TPostResult(postCtx, None[ResolvedInfo.Method]())
    }
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformExpLitZ(ctx: Context, o: Exp.LitZ): TPostResult[Context, Exp.LitZ] = {
    val preR: PreResult[Context, Exp.LitZ] = pp.preExpLitZ(ctx, o) match {
     case PreResult(preCtx, continu, Some(r: Exp.LitZ)) => PreResult(preCtx, continu, Some[Exp.LitZ](r))
     case PreResult(_, _, Some(_)) => halt("Can only produce object of type Exp.LitZ")
     case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[Exp.LitZ]())
    }
    val r: TPostResult[Context, Exp.LitZ] = if (preR.continu) {
      val o2: Exp.LitZ = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, Attr] = transformAttr(preR.ctx, o2.attr)
      if (hasChanged || r0.resultOpt.nonEmpty)
        TPostResult(r0.ctx, Some(o2(attr = r0.resultOpt.getOrElse(o2.attr))))
      else
        TPostResult(r0.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: Exp.LitZ = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Exp.LitZ] = pp.postExpLitZ(r.ctx, o2) match {
     case TPostResult(postCtx, Some(result: Exp.LitZ)) => TPostResult(postCtx, Some[Exp.LitZ](result))
     case TPostResult(_, Some(_)) => halt("Can only produce object of type Exp.LitZ")
     case TPostResult(postCtx, _) => TPostResult(postCtx, None[Exp.LitZ]())
    }
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformExpLitB(ctx: Context, o: Exp.LitB): TPostResult[Context, Exp.LitB] = {
    val preR: PreResult[Context, Exp.LitB] = pp.preExpLitB(ctx, o) match {
     case PreResult(preCtx, continu, Some(r: Exp.LitB)) => PreResult(preCtx, continu, Some[Exp.LitB](r))
     case PreResult(_, _, Some(_)) => halt("Can only produce object of type Exp.LitB")
     case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[Exp.LitB]())
    }
    val r: TPostResult[Context, Exp.LitB] = if (preR.continu) {
      val o2: Exp.LitB = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, Attr] = transformAttr(preR.ctx, o2.attr)
      if (hasChanged || r0.resultOpt.nonEmpty)
        TPostResult(r0.ctx, Some(o2(attr = r0.resultOpt.getOrElse(o2.attr))))
      else
        TPostResult(r0.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: Exp.LitB = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Exp.LitB] = pp.postExpLitB(r.ctx, o2) match {
     case TPostResult(postCtx, Some(result: Exp.LitB)) => TPostResult(postCtx, Some[Exp.LitB](result))
     case TPostResult(_, Some(_)) => halt("Can only produce object of type Exp.LitB")
     case TPostResult(postCtx, _) => TPostResult(postCtx, None[Exp.LitB]())
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