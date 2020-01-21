// #Sireum
// @formatter:off

/*
 Copyright (c) 2020, Robby, Kansas State University
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

object MTransformer {

  @record class PreResult[T](continu: B,
                             resultOpt: MOption[T])

  def transformISZ[T](s: IS[Z, T], f: T => MOption[T]): MOption[IS[Z, T]] = {
    val s2: MS[Z, T] = s.toMS
    var changed: B = F
    for (i <- s2.indices) {
      val e: T = s(i)
      val r: MOption[T] = f(e)
      changed = changed || r.nonEmpty
      s2(i) = r.getOrElse(e)
    }
    if (changed) {
      return MSome(s2.toIS)
    } else {
      return MNone()
    }
  }

  def transformOption[T](option: Option[T], f: T => MOption[T]): MOption[Option[T]] = {
    option match {
      case Some(v) =>
        val r = f(v)
        r match {
          case MSome(v2) => return MSome(Some(v2))
          case _ => return MNone()
        }
      case _ => return MNone()
    }
  }

  val PreResultTopUnitProgram: PreResult[TopUnit] = PreResult(T, MNone())

  val PostResultTopUnitProgram: MOption[TopUnit] = MNone()

  val PreResultTopUnitSequentUnit: PreResult[TopUnit] = PreResult(T, MNone())

  val PostResultTopUnitSequentUnit: MOption[TopUnit] = MNone()

  val PreResultTopUnitTruthTableUnit: PreResult[TopUnit] = PreResult(T, MNone())

  val PostResultTopUnitTruthTableUnit: MOption[TopUnit] = MNone()

  val PreResultStmtImport: PreResult[Stmt] = PreResult(T, MNone())

  val PostResultStmtImport: MOption[Stmt] = MNone()

  val PreResultStmtImportImporter: PreResult[Stmt.Import.Importer] = PreResult(T, MNone())

  val PostResultStmtImportImporter: MOption[Stmt.Import.Importer] = MNone()

  val PreResultStmtImportMultiSelector: PreResult[Stmt.Import.Selector] = PreResult(T, MNone())

  val PostResultStmtImportMultiSelector: MOption[Stmt.Import.Selector] = MNone()

  val PreResultStmtImportWildcardSelector: PreResult[Stmt.Import.Selector] = PreResult(T, MNone())

  val PostResultStmtImportWildcardSelector: MOption[Stmt.Import.Selector] = MNone()

  val PreResultStmtImportNamedSelector: PreResult[Stmt.Import.NamedSelector] = PreResult(T, MNone())

  val PostResultStmtImportNamedSelector: MOption[Stmt.Import.NamedSelector] = MNone()

  val PreResultStmtVar: PreResult[Stmt] = PreResult(T, MNone())

  val PostResultStmtVar: MOption[Stmt] = MNone()

  val PreResultStmtVarPattern: PreResult[Stmt] = PreResult(T, MNone())

  val PostResultStmtVarPattern: MOption[Stmt] = MNone()

  val PreResultStmtSpecVar: PreResult[Stmt] = PreResult(T, MNone())

  val PostResultStmtSpecVar: MOption[Stmt] = MNone()

  val PreResultStmtMethod: PreResult[Stmt] = PreResult(T, MNone())

  val PostResultStmtMethod: MOption[Stmt] = MNone()

  val PreResultStmtExtMethod: PreResult[Stmt] = PreResult(T, MNone())

  val PostResultStmtExtMethod: MOption[Stmt] = MNone()

  val PreResultStmtSpecMethod: PreResult[Stmt] = PreResult(T, MNone())

  val PostResultStmtSpecMethod: MOption[Stmt] = MNone()

  val PreResultStmtEnum: PreResult[Stmt] = PreResult(T, MNone())

  val PostResultStmtEnum: MOption[Stmt] = MNone()

  val PreResultStmtSubZ: PreResult[Stmt] = PreResult(T, MNone())

  val PostResultStmtSubZ: MOption[Stmt] = MNone()

  val PreResultStmtObject: PreResult[Stmt] = PreResult(T, MNone())

  val PostResultStmtObject: MOption[Stmt] = MNone()

  val PreResultStmtSig: PreResult[Stmt] = PreResult(T, MNone())

  val PostResultStmtSig: MOption[Stmt] = MNone()

  val PreResultStmtAdt: PreResult[Stmt] = PreResult(T, MNone())

  val PostResultStmtAdt: MOption[Stmt] = MNone()

  val PreResultStmtTypeAlias: PreResult[Stmt] = PreResult(T, MNone())

  val PostResultStmtTypeAlias: MOption[Stmt] = MNone()

  val PreResultStmtAssign: PreResult[Stmt] = PreResult(T, MNone())

  val PostResultStmtAssign: MOption[Stmt] = MNone()

  val PreResultStmtBlock: PreResult[Stmt] = PreResult(T, MNone())

  val PostResultStmtBlock: MOption[Stmt] = MNone()

  val PreResultStmtIf: PreResult[Stmt] = PreResult(T, MNone())

  val PostResultStmtIf: MOption[Stmt] = MNone()

  val PreResultStmtMatch: PreResult[Stmt] = PreResult(T, MNone())

  val PostResultStmtMatch: MOption[Stmt] = MNone()

  val PreResultStmtWhile: PreResult[Stmt] = PreResult(T, MNone())

  val PostResultStmtWhile: MOption[Stmt] = MNone()

  val PreResultStmtDoWhile: PreResult[Stmt] = PreResult(T, MNone())

  val PostResultStmtDoWhile: MOption[Stmt] = MNone()

  val PreResultStmtFor: PreResult[Stmt] = PreResult(T, MNone())

  val PostResultStmtFor: MOption[Stmt] = MNone()

  val PreResultStmtReturn: PreResult[Stmt] = PreResult(T, MNone())

  val PostResultStmtReturn: MOption[Stmt] = MNone()

  val PreResultStmtExpr: PreResult[Stmt] = PreResult(T, MNone())

  val PostResultStmtExpr: MOption[Stmt] = MNone()

  val PreResultStmtFact: PreResult[Stmt.Spec] = PreResult(T, MNone())

  val PostResultStmtFact: MOption[Stmt.Spec] = MNone()

  val PreResultStmtInv: PreResult[Stmt.Spec] = PreResult(T, MNone())

  val PostResultStmtInv: MOption[Stmt.Spec] = MNone()

  val PreResultStmtTheorem: PreResult[Stmt.Spec] = PreResult(T, MNone())

  val PostResultStmtTheorem: MOption[Stmt.Spec] = MNone()

  val PreResultStmtDataRefinement: PreResult[Stmt.Spec] = PreResult(T, MNone())

  val PostResultStmtDataRefinement: MOption[Stmt.Spec] = MNone()

  val PreResultStmtSpecLabel: PreResult[Stmt.Spec] = PreResult(T, MNone())

  val PostResultStmtSpecLabel: MOption[Stmt.Spec] = MNone()

  val PreResultStmtSpecBlock: PreResult[Stmt.Spec] = PreResult(T, MNone())

  val PostResultStmtSpecBlock: MOption[Stmt.Spec] = MNone()

  val PreResultStmtDeduceSequent: PreResult[Stmt.Spec] = PreResult(T, MNone())

  val PostResultStmtDeduceSequent: MOption[Stmt.Spec] = MNone()

  val PreResultStmtDeduceSteps: PreResult[Stmt.Spec] = PreResult(T, MNone())

  val PostResultStmtDeduceSteps: MOption[Stmt.Spec] = MNone()

  val PreResultStmtHavoc: PreResult[Stmt.Spec] = PreResult(T, MNone())

  val PostResultStmtHavoc: MOption[Stmt.Spec] = MNone()

  val PreResultMethodContractSimple: PreResult[MethodContract] = PreResult(T, MNone())

  val PostResultMethodContractSimple: MOption[MethodContract] = MNone()

  val PreResultMethodContractCases: PreResult[MethodContract] = PreResult(T, MNone())

  val PostResultMethodContractCases: MOption[MethodContract] = MNone()

  val PreResultMethodContractCase: PreResult[MethodContract.Case] = PreResult(T, MNone())

  val PostResultMethodContractCase: MOption[MethodContract.Case] = MNone()

  val PreResultSequent: PreResult[Sequent] = PreResult(T, MNone())

  val PostResultSequent: MOption[Sequent] = MNone()

  val PreResultProof: PreResult[Proof] = PreResult(T, MNone())

  val PostResultProof: MOption[Proof] = MNone()

  val PreResultProofStepRegular: PreResult[Proof.Step] = PreResult(T, MNone())

  val PostResultProofStepRegular: MOption[Proof.Step] = MNone()

  val PreResultProofStepAssume: PreResult[Proof.Step] = PreResult(T, MNone())

  val PostResultProofStepAssume: MOption[Proof.Step] = MNone()

  val PreResultProofStepAssert: PreResult[Proof.Step] = PreResult(T, MNone())

  val PostResultProofStepAssert: MOption[Proof.Step] = MNone()

  val PreResultProofStepSubProof: PreResult[Proof.Step] = PreResult(T, MNone())

  val PostResultProofStepSubProof: MOption[Proof.Step] = MNone()

  val PreResultProofStepLet: PreResult[Proof.Step] = PreResult(T, MNone())

  val PostResultProofStepLet: MOption[Proof.Step] = MNone()

  val PreResultProofStepLetParam: PreResult[Proof.Step.Let.Param] = PreResult(T, MNone())

  val PostResultProofStepLetParam: MOption[Proof.Step.Let.Param] = MNone()

  val PreResultProofStepStructInduction: PreResult[Proof.Step] = PreResult(T, MNone())

  val PostResultProofStepStructInduction: MOption[Proof.Step] = MNone()

  val PreResultProofStepStructInductionMatchCase: PreResult[Proof.Step.StructInduction.MatchCase] = PreResult(T, MNone())

  val PostResultProofStepStructInductionMatchCase: MOption[Proof.Step.StructInduction.MatchCase] = MNone()

  val PreResultProofStepStructInductionMatchDefault: PreResult[Proof.Step.StructInduction.MatchDefault] = PreResult(T, MNone())

  val PostResultProofStepStructInductionMatchDefault: MOption[Proof.Step.StructInduction.MatchDefault] = MNone()

  val PreResultProofStepJustification: PreResult[Proof.Step.Justification] = PreResult(T, MNone())

  val PostResultProofStepJustification: MOption[Proof.Step.Justification] = MNone()

  val PreResultCase: PreResult[Case] = PreResult(T, MNone())

  val PostResultCase: MOption[Case] = MNone()

  val PreResultEnumGenRangeExpr: PreResult[EnumGen.Range] = PreResult(T, MNone())

  val PostResultEnumGenRangeExpr: MOption[EnumGen.Range] = MNone()

  val PreResultEnumGenRangeStep: PreResult[EnumGen.Range] = PreResult(T, MNone())

  val PostResultEnumGenRangeStep: MOption[EnumGen.Range] = MNone()

  val PreResultEnumGenFor: PreResult[EnumGen.For] = PreResult(T, MNone())

  val PostResultEnumGenFor: MOption[EnumGen.For] = MNone()

  val PreResultTypeNamed: PreResult[Type] = PreResult(T, MNone())

  val PostResultTypeNamed: MOption[Type] = MNone()

  val PreResultTypeFun: PreResult[Type] = PreResult(T, MNone())

  val PostResultTypeFun: MOption[Type] = MNone()

  val PreResultTypeTuple: PreResult[Type] = PreResult(T, MNone())

  val PostResultTypeTuple: MOption[Type] = MNone()

  val PreResultPatternLiteral: PreResult[Pattern] = PreResult(T, MNone())

  val PostResultPatternLiteral: MOption[Pattern] = MNone()

  val PreResultPatternLitInterpolate: PreResult[Pattern] = PreResult(T, MNone())

  val PostResultPatternLitInterpolate: MOption[Pattern] = MNone()

  val PreResultPatternRef: PreResult[Pattern] = PreResult(T, MNone())

  val PostResultPatternRef: MOption[Pattern] = MNone()

  val PreResultPatternVarBinding: PreResult[Pattern] = PreResult(T, MNone())

  val PostResultPatternVarBinding: MOption[Pattern] = MNone()

  val PreResultPatternWildcard: PreResult[Pattern] = PreResult(T, MNone())

  val PostResultPatternWildcard: MOption[Pattern] = MNone()

  val PreResultPatternSeqWildcard: PreResult[Pattern] = PreResult(T, MNone())

  val PostResultPatternSeqWildcard: MOption[Pattern] = MNone()

  val PreResultPatternStructure: PreResult[Pattern] = PreResult(T, MNone())

  val PostResultPatternStructure: MOption[Pattern] = MNone()

  val PreResultExpLitB: PreResult[Exp] = PreResult(T, MNone())

  val PostResultExpLitB: MOption[Exp] = MNone()

  val PreResultExpLitC: PreResult[Exp] = PreResult(T, MNone())

  val PostResultExpLitC: MOption[Exp] = MNone()

  val PreResultExpLitZ: PreResult[Exp] = PreResult(T, MNone())

  val PostResultExpLitZ: MOption[Exp] = MNone()

  val PreResultExpLitF32: PreResult[Exp] = PreResult(T, MNone())

  val PostResultExpLitF32: MOption[Exp] = MNone()

  val PreResultExpLitF64: PreResult[Exp] = PreResult(T, MNone())

  val PostResultExpLitF64: MOption[Exp] = MNone()

  val PreResultExpLitR: PreResult[Exp] = PreResult(T, MNone())

  val PostResultExpLitR: MOption[Exp] = MNone()

  val PreResultExpLitString: PreResult[Exp] = PreResult(T, MNone())

  val PostResultExpLitString: MOption[Exp] = MNone()

  val PreResultExpStringInterpolate: PreResult[Exp] = PreResult(T, MNone())

  val PostResultExpStringInterpolate: MOption[Exp] = MNone()

  val PreResultExpThis: PreResult[Exp] = PreResult(T, MNone())

  val PostResultExpThis: MOption[Exp] = MNone()

  val PreResultExpSuper: PreResult[Exp] = PreResult(T, MNone())

  val PostResultExpSuper: MOption[Exp] = MNone()

  val PreResultExpUnary: PreResult[Exp] = PreResult(T, MNone())

  val PostResultExpUnary: MOption[Exp] = MNone()

  val PreResultExpBinary: PreResult[Exp] = PreResult(T, MNone())

  val PostResultExpBinary: MOption[Exp] = MNone()

  val PreResultExpIdent: PreResult[Exp] = PreResult(T, MNone())

  val PostResultExpIdent: MOption[Exp] = MNone()

  val PreResultExpEta: PreResult[Exp] = PreResult(T, MNone())

  val PostResultExpEta: MOption[Exp] = MNone()

  val PreResultExpTuple: PreResult[Exp] = PreResult(T, MNone())

  val PostResultExpTuple: MOption[Exp] = MNone()

  val PreResultExpSelect: PreResult[Exp] = PreResult(T, MNone())

  val PostResultExpSelect: MOption[Exp] = MNone()

  val PreResultExpInvoke: PreResult[Exp] = PreResult(T, MNone())

  val PostResultExpInvoke: MOption[Exp] = MNone()

  val PreResultExpInvokeNamed: PreResult[Exp] = PreResult(T, MNone())

  val PostResultExpInvokeNamed: MOption[Exp] = MNone()

  val PreResultExpIf: PreResult[Exp] = PreResult(T, MNone())

  val PostResultExpIf: MOption[Exp] = MNone()

  val PreResultExpFunParam: PreResult[Exp.Fun.Param] = PreResult(T, MNone())

  val PostResultExpFunParam: MOption[Exp.Fun.Param] = MNone()

  val PreResultExpFun: PreResult[Exp] = PreResult(T, MNone())

  val PostResultExpFun: MOption[Exp] = MNone()

  val PreResultExpForYield: PreResult[Exp] = PreResult(T, MNone())

  val PostResultExpForYield: MOption[Exp] = MNone()

  val PreResultExpQuantType: PreResult[Exp.Quant] = PreResult(T, MNone())

  val PostResultExpQuantType: MOption[Exp.Quant] = MNone()

  val PreResultExpQuantRange: PreResult[Exp.Quant] = PreResult(T, MNone())

  val PostResultExpQuantRange: MOption[Exp.Quant] = MNone()

  val PreResultExpQuantEach: PreResult[Exp.Quant] = PreResult(T, MNone())

  val PostResultExpQuantEach: MOption[Exp.Quant] = MNone()

  val PreResultExpInput: PreResult[Exp.Spec] = PreResult(T, MNone())

  val PostResultExpInput: MOption[Exp.Spec] = MNone()

  val PreResultExpOldVal: PreResult[Exp.Spec] = PreResult(T, MNone())

  val PostResultExpOldVal: MOption[Exp.Spec] = MNone()

  val PreResultExpAtLoc: PreResult[Exp.Spec] = PreResult(T, MNone())

  val PostResultExpAtLoc: MOption[Exp.Spec] = MNone()

  val PreResultExpStateSeq: PreResult[Exp.Spec] = PreResult(T, MNone())

  val PostResultExpStateSeq: MOption[Exp.Spec] = MNone()

  val PreResultExpStateSeqFragment: PreResult[Exp.StateSeq.Fragment] = PreResult(T, MNone())

  val PostResultExpStateSeqFragment: MOption[Exp.StateSeq.Fragment] = MNone()

  val PreResultExpResult: PreResult[Exp.Spec] = PreResult(T, MNone())

  val PostResultExpResult: MOption[Exp.Spec] = MNone()

  val PreResultNamedArg: PreResult[NamedArg] = PreResult(T, MNone())

  val PostResultNamedArg: MOption[NamedArg] = MNone()

  val PreResultId: PreResult[Id] = PreResult(T, MNone())

  val PostResultId: MOption[Id] = MNone()

  val PreResultName: PreResult[Name] = PreResult(T, MNone())

  val PostResultName: MOption[Name] = MNone()

  val PreResultBody: PreResult[Body] = PreResult(T, MNone())

  val PostResultBody: MOption[Body] = MNone()

  val PreResultAdtParam: PreResult[AdtParam] = PreResult(T, MNone())

  val PostResultAdtParam: MOption[AdtParam] = MNone()

  val PreResultMethodSig: PreResult[MethodSig] = PreResult(T, MNone())

  val PostResultMethodSig: MOption[MethodSig] = MNone()

  val PreResultParam: PreResult[Param] = PreResult(T, MNone())

  val PostResultParam: MOption[Param] = MNone()

  val PreResultTypeParam: PreResult[TypeParam] = PreResult(T, MNone())

  val PostResultTypeParam: MOption[TypeParam] = MNone()

  val PreResultTypedName: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedName: MOption[Typed] = MNone()

  val PreResultTypedTuple: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedTuple: MOption[Typed] = MNone()

  val PreResultTypedFun: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedFun: MOption[Typed] = MNone()

  val PreResultTypedTypeVar: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedTypeVar: MOption[Typed] = MNone()

  val PreResultTypedPackage: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedPackage: MOption[Typed] = MNone()

  val PreResultTypedObject: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedObject: MOption[Typed] = MNone()

  val PreResultTypedEnum: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedEnum: MOption[Typed] = MNone()

  val PreResultTypedMethod: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedMethod: MOption[Typed] = MNone()

  val PreResultTypedMethods: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedMethods: MOption[Typed] = MNone()

  val PreResultTypedFact: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedFact: MOption[Typed] = MNone()

  val PreResultTypedTheorem: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedTheorem: MOption[Typed] = MNone()

  val PreResultTypedInv: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedInv: MOption[Typed] = MNone()

  val PreResultAttr: PreResult[Attr] = PreResult(T, MNone())

  val PostResultAttr: MOption[Attr] = MNone()

  val PreResultTypedAttr: PreResult[TypedAttr] = PreResult(T, MNone())

  val PostResultTypedAttr: MOption[TypedAttr] = MNone()

  val PreResultResolvedAttr: PreResult[ResolvedAttr] = PreResult(T, MNone())

  val PostResultResolvedAttr: MOption[ResolvedAttr] = MNone()

  val PreResultResolvedInfoBuiltIn: PreResult[ResolvedInfo] = PreResult(T, MNone())

  val PostResultResolvedInfoBuiltIn: MOption[ResolvedInfo] = MNone()

  val PreResultResolvedInfoPackage: PreResult[ResolvedInfo] = PreResult(T, MNone())

  val PostResultResolvedInfoPackage: MOption[ResolvedInfo] = MNone()

  val PreResultResolvedInfoEnum: PreResult[ResolvedInfo] = PreResult(T, MNone())

  val PostResultResolvedInfoEnum: MOption[ResolvedInfo] = MNone()

  val PreResultResolvedInfoEnumElement: PreResult[ResolvedInfo] = PreResult(T, MNone())

  val PostResultResolvedInfoEnumElement: MOption[ResolvedInfo] = MNone()

  val PreResultResolvedInfoObject: PreResult[ResolvedInfo] = PreResult(T, MNone())

  val PostResultResolvedInfoObject: MOption[ResolvedInfo] = MNone()

  val PreResultResolvedInfoVar: PreResult[ResolvedInfo] = PreResult(T, MNone())

  val PostResultResolvedInfoVar: MOption[ResolvedInfo] = MNone()

  val PreResultResolvedInfoMethod: PreResult[ResolvedInfo] = PreResult(T, MNone())

  val PostResultResolvedInfoMethod: MOption[ResolvedInfo] = MNone()

  val PreResultResolvedInfoMethods: PreResult[ResolvedInfo] = PreResult(T, MNone())

  val PostResultResolvedInfoMethods: MOption[ResolvedInfo] = MNone()

  val PreResultResolvedInfoTuple: PreResult[ResolvedInfo] = PreResult(T, MNone())

  val PostResultResolvedInfoTuple: MOption[ResolvedInfo] = MNone()

  val PreResultResolvedInfoLocalVar: PreResult[ResolvedInfo] = PreResult(T, MNone())

  val PostResultResolvedInfoLocalVar: MOption[ResolvedInfo] = MNone()

  val PreResultResolvedInfoFact: PreResult[ResolvedInfo] = PreResult(T, MNone())

  val PostResultResolvedInfoFact: MOption[ResolvedInfo] = MNone()

  val PreResultResolvedInfoTheorem: PreResult[ResolvedInfo] = PreResult(T, MNone())

  val PostResultResolvedInfoTheorem: MOption[ResolvedInfo] = MNone()

  val PreResultResolvedInfoInv: PreResult[ResolvedInfo] = PreResult(T, MNone())

  val PostResultResolvedInfoInv: MOption[ResolvedInfo] = MNone()

  val PreResultTruthTableRow: PreResult[TruthTable.Row] = PreResult(T, MNone())

  val PostResultTruthTableRow: MOption[TruthTable.Row] = MNone()

  val PreResultTruthTableAssignment: PreResult[TruthTable.Assignment] = PreResult(T, MNone())

  val PostResultTruthTableAssignment: MOption[TruthTable.Assignment] = MNone()

  val PreResultTruthTableConclusionValidity: PreResult[TruthTable.Conclusion] = PreResult(T, MNone())

  val PostResultTruthTableConclusionValidity: MOption[TruthTable.Conclusion] = MNone()

  val PreResultTruthTableConclusionTautology: PreResult[TruthTable.Conclusion] = PreResult(T, MNone())

  val PostResultTruthTableConclusionTautology: MOption[TruthTable.Conclusion] = MNone()

  val PreResultTruthTableConclusionContradictory: PreResult[TruthTable.Conclusion] = PreResult(T, MNone())

  val PostResultTruthTableConclusionContradictory: MOption[TruthTable.Conclusion] = MNone()

  val PreResultTruthTableConclusionContingent: PreResult[TruthTable.Conclusion] = PreResult(T, MNone())

  val PostResultTruthTableConclusionContingent: MOption[TruthTable.Conclusion] = MNone()

}

import MTransformer._

@msig trait MTransformer {

  def preTopUnit(o: TopUnit): PreResult[TopUnit] = {
    o match {
      case o: TopUnit.Program => return preTopUnitProgram(o)
      case o: TopUnit.SequentUnit => return preTopUnitSequentUnit(o)
      case o: TopUnit.TruthTableUnit => return preTopUnitTruthTableUnit(o)
    }
  }

  def preTopUnitProgram(o: TopUnit.Program): PreResult[TopUnit] = {
    return PreResultTopUnitProgram
  }

  def preTopUnitSequentUnit(o: TopUnit.SequentUnit): PreResult[TopUnit] = {
    return PreResultTopUnitSequentUnit
  }

  def preTopUnitTruthTableUnit(o: TopUnit.TruthTableUnit): PreResult[TopUnit] = {
    return PreResultTopUnitTruthTableUnit
  }

  def preStmt(o: Stmt): PreResult[Stmt] = {
    o match {
      case o: Stmt.Import => return preStmtImport(o)
      case o: Stmt.Var => return preStmtVar(o)
      case o: Stmt.VarPattern => return preStmtVarPattern(o)
      case o: Stmt.SpecVar => return preStmtSpecVar(o)
      case o: Stmt.Method => return preStmtMethod(o)
      case o: Stmt.ExtMethod => return preStmtExtMethod(o)
      case o: Stmt.SpecMethod => return preStmtSpecMethod(o)
      case o: Stmt.Enum => return preStmtEnum(o)
      case o: Stmt.SubZ => return preStmtSubZ(o)
      case o: Stmt.Object => return preStmtObject(o)
      case o: Stmt.Sig => return preStmtSig(o)
      case o: Stmt.Adt => return preStmtAdt(o)
      case o: Stmt.TypeAlias => return preStmtTypeAlias(o)
      case o: Stmt.Assign => return preStmtAssign(o)
      case o: Stmt.Block => return preStmtBlock(o)
      case o: Stmt.If => return preStmtIf(o)
      case o: Stmt.Match => return preStmtMatch(o)
      case o: Stmt.While => return preStmtWhile(o)
      case o: Stmt.DoWhile => return preStmtDoWhile(o)
      case o: Stmt.For => return preStmtFor(o)
      case o: Stmt.Return => return preStmtReturn(o)
      case o: Stmt.Expr => return preStmtExpr(o)
      case o: Stmt.Fact =>
        val r: PreResult[Stmt] = preStmtFact(o) match {
         case PreResult(continu, MSome(r: Stmt)) => PreResult(continu, MSome[Stmt](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type Stmt")
         case PreResult(continu, _) => PreResult(continu, MNone[Stmt]())
        }
        return r
      case o: Stmt.Inv =>
        val r: PreResult[Stmt] = preStmtInv(o) match {
         case PreResult(continu, MSome(r: Stmt)) => PreResult(continu, MSome[Stmt](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type Stmt")
         case PreResult(continu, _) => PreResult(continu, MNone[Stmt]())
        }
        return r
      case o: Stmt.Theorem =>
        val r: PreResult[Stmt] = preStmtTheorem(o) match {
         case PreResult(continu, MSome(r: Stmt)) => PreResult(continu, MSome[Stmt](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type Stmt")
         case PreResult(continu, _) => PreResult(continu, MNone[Stmt]())
        }
        return r
      case o: Stmt.DataRefinement =>
        val r: PreResult[Stmt] = preStmtDataRefinement(o) match {
         case PreResult(continu, MSome(r: Stmt)) => PreResult(continu, MSome[Stmt](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type Stmt")
         case PreResult(continu, _) => PreResult(continu, MNone[Stmt]())
        }
        return r
      case o: Stmt.SpecLabel =>
        val r: PreResult[Stmt] = preStmtSpecLabel(o) match {
         case PreResult(continu, MSome(r: Stmt)) => PreResult(continu, MSome[Stmt](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type Stmt")
         case PreResult(continu, _) => PreResult(continu, MNone[Stmt]())
        }
        return r
      case o: Stmt.SpecBlock =>
        val r: PreResult[Stmt] = preStmtSpecBlock(o) match {
         case PreResult(continu, MSome(r: Stmt)) => PreResult(continu, MSome[Stmt](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type Stmt")
         case PreResult(continu, _) => PreResult(continu, MNone[Stmt]())
        }
        return r
      case o: Stmt.DeduceSequent =>
        val r: PreResult[Stmt] = preStmtDeduceSequent(o) match {
         case PreResult(continu, MSome(r: Stmt)) => PreResult(continu, MSome[Stmt](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type Stmt")
         case PreResult(continu, _) => PreResult(continu, MNone[Stmt]())
        }
        return r
      case o: Stmt.DeduceSteps =>
        val r: PreResult[Stmt] = preStmtDeduceSteps(o) match {
         case PreResult(continu, MSome(r: Stmt)) => PreResult(continu, MSome[Stmt](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type Stmt")
         case PreResult(continu, _) => PreResult(continu, MNone[Stmt]())
        }
        return r
      case o: Stmt.Havoc =>
        val r: PreResult[Stmt] = preStmtHavoc(o) match {
         case PreResult(continu, MSome(r: Stmt)) => PreResult(continu, MSome[Stmt](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type Stmt")
         case PreResult(continu, _) => PreResult(continu, MNone[Stmt]())
        }
        return r
    }
  }

  def preHasModifies(o: HasModifies): PreResult[HasModifies] = {
    o match {
      case o: Stmt.While =>
        val r: PreResult[HasModifies] = preStmtWhile(o) match {
         case PreResult(continu, MSome(r: HasModifies)) => PreResult(continu, MSome[HasModifies](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type HasModifies")
         case PreResult(continu, _) => PreResult(continu, MNone[HasModifies]())
        }
        return r
      case o: Stmt.DoWhile =>
        val r: PreResult[HasModifies] = preStmtDoWhile(o) match {
         case PreResult(continu, MSome(r: HasModifies)) => PreResult(continu, MSome[HasModifies](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type HasModifies")
         case PreResult(continu, _) => PreResult(continu, MNone[HasModifies]())
        }
        return r
      case o: Stmt.For =>
        val r: PreResult[HasModifies] = preStmtFor(o) match {
         case PreResult(continu, MSome(r: HasModifies)) => PreResult(continu, MSome[HasModifies](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type HasModifies")
         case PreResult(continu, _) => PreResult(continu, MNone[HasModifies]())
        }
        return r
      case o: MethodContract.Simple =>
        val r: PreResult[HasModifies] = preMethodContractSimple(o) match {
         case PreResult(continu, MSome(r: HasModifies)) => PreResult(continu, MSome[HasModifies](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type HasModifies")
         case PreResult(continu, _) => PreResult(continu, MNone[HasModifies]())
        }
        return r
      case o: MethodContract.Cases =>
        val r: PreResult[HasModifies] = preMethodContractCases(o) match {
         case PreResult(continu, MSome(r: HasModifies)) => PreResult(continu, MSome[HasModifies](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type HasModifies")
         case PreResult(continu, _) => PreResult(continu, MNone[HasModifies]())
        }
        return r
    }
  }

  def preStmtImport(o: Stmt.Import): PreResult[Stmt] = {
    return PreResultStmtImport
  }

  def preStmtImportImporter(o: Stmt.Import.Importer): PreResult[Stmt.Import.Importer] = {
    return PreResultStmtImportImporter
  }

  def preStmtImportSelector(o: Stmt.Import.Selector): PreResult[Stmt.Import.Selector] = {
    o match {
      case o: Stmt.Import.MultiSelector => return preStmtImportMultiSelector(o)
      case o: Stmt.Import.WildcardSelector => return preStmtImportWildcardSelector(o)
    }
  }

  def preStmtImportMultiSelector(o: Stmt.Import.MultiSelector): PreResult[Stmt.Import.Selector] = {
    return PreResultStmtImportMultiSelector
  }

  def preStmtImportWildcardSelector(o: Stmt.Import.WildcardSelector): PreResult[Stmt.Import.Selector] = {
    return PreResultStmtImportWildcardSelector
  }

  def preStmtImportNamedSelector(o: Stmt.Import.NamedSelector): PreResult[Stmt.Import.NamedSelector] = {
    return PreResultStmtImportNamedSelector
  }

  def preStmtVar(o: Stmt.Var): PreResult[Stmt] = {
    return PreResultStmtVar
  }

  def preStmtVarPattern(o: Stmt.VarPattern): PreResult[Stmt] = {
    return PreResultStmtVarPattern
  }

  def preStmtSpecVar(o: Stmt.SpecVar): PreResult[Stmt] = {
    return PreResultStmtSpecVar
  }

  def preStmtMethod(o: Stmt.Method): PreResult[Stmt] = {
    return PreResultStmtMethod
  }

  def preStmtExtMethod(o: Stmt.ExtMethod): PreResult[Stmt] = {
    return PreResultStmtExtMethod
  }

  def preStmtSpecMethod(o: Stmt.SpecMethod): PreResult[Stmt] = {
    return PreResultStmtSpecMethod
  }

  def preStmtEnum(o: Stmt.Enum): PreResult[Stmt] = {
    return PreResultStmtEnum
  }

  def preStmtSubZ(o: Stmt.SubZ): PreResult[Stmt] = {
    return PreResultStmtSubZ
  }

  def preStmtObject(o: Stmt.Object): PreResult[Stmt] = {
    return PreResultStmtObject
  }

  def preStmtSig(o: Stmt.Sig): PreResult[Stmt] = {
    return PreResultStmtSig
  }

  def preStmtAdt(o: Stmt.Adt): PreResult[Stmt] = {
    return PreResultStmtAdt
  }

  def preStmtTypeAlias(o: Stmt.TypeAlias): PreResult[Stmt] = {
    return PreResultStmtTypeAlias
  }

  def preStmtAssign(o: Stmt.Assign): PreResult[Stmt] = {
    return PreResultStmtAssign
  }

  def preStmtBlock(o: Stmt.Block): PreResult[Stmt] = {
    return PreResultStmtBlock
  }

  def preStmtIf(o: Stmt.If): PreResult[Stmt] = {
    return PreResultStmtIf
  }

  def preStmtMatch(o: Stmt.Match): PreResult[Stmt] = {
    return PreResultStmtMatch
  }

  def preStmtLoop(o: Stmt.Loop): PreResult[Stmt.Loop] = {
    o match {
      case o: Stmt.While =>
        val r: PreResult[Stmt.Loop] = preStmtWhile(o) match {
         case PreResult(continu, MSome(r: Stmt.Loop)) => PreResult(continu, MSome[Stmt.Loop](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type Stmt.Loop")
         case PreResult(continu, _) => PreResult(continu, MNone[Stmt.Loop]())
        }
        return r
      case o: Stmt.DoWhile =>
        val r: PreResult[Stmt.Loop] = preStmtDoWhile(o) match {
         case PreResult(continu, MSome(r: Stmt.Loop)) => PreResult(continu, MSome[Stmt.Loop](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type Stmt.Loop")
         case PreResult(continu, _) => PreResult(continu, MNone[Stmt.Loop]())
        }
        return r
      case o: Stmt.For =>
        val r: PreResult[Stmt.Loop] = preStmtFor(o) match {
         case PreResult(continu, MSome(r: Stmt.Loop)) => PreResult(continu, MSome[Stmt.Loop](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type Stmt.Loop")
         case PreResult(continu, _) => PreResult(continu, MNone[Stmt.Loop]())
        }
        return r
    }
  }

  def preStmtWhile(o: Stmt.While): PreResult[Stmt] = {
    return PreResultStmtWhile
  }

  def preStmtDoWhile(o: Stmt.DoWhile): PreResult[Stmt] = {
    return PreResultStmtDoWhile
  }

  def preStmtFor(o: Stmt.For): PreResult[Stmt] = {
    return PreResultStmtFor
  }

  def preStmtReturn(o: Stmt.Return): PreResult[Stmt] = {
    return PreResultStmtReturn
  }

  def preStmtExpr(o: Stmt.Expr): PreResult[Stmt] = {
    return PreResultStmtExpr
  }

  def preStmtSpec(o: Stmt.Spec): PreResult[Stmt.Spec] = {
    o match {
      case o: Stmt.Fact => return preStmtFact(o)
      case o: Stmt.Inv => return preStmtInv(o)
      case o: Stmt.Theorem => return preStmtTheorem(o)
      case o: Stmt.DataRefinement => return preStmtDataRefinement(o)
      case o: Stmt.SpecLabel => return preStmtSpecLabel(o)
      case o: Stmt.SpecBlock => return preStmtSpecBlock(o)
      case o: Stmt.DeduceSequent => return preStmtDeduceSequent(o)
      case o: Stmt.DeduceSteps => return preStmtDeduceSteps(o)
      case o: Stmt.Havoc => return preStmtHavoc(o)
    }
  }

  def preStmtFact(o: Stmt.Fact): PreResult[Stmt.Spec] = {
    return PreResultStmtFact
  }

  def preStmtInv(o: Stmt.Inv): PreResult[Stmt.Spec] = {
    return PreResultStmtInv
  }

  def preStmtTheorem(o: Stmt.Theorem): PreResult[Stmt.Spec] = {
    return PreResultStmtTheorem
  }

  def preStmtDataRefinement(o: Stmt.DataRefinement): PreResult[Stmt.Spec] = {
    return PreResultStmtDataRefinement
  }

  def preStmtSpecLabel(o: Stmt.SpecLabel): PreResult[Stmt.Spec] = {
    return PreResultStmtSpecLabel
  }

  def preStmtSpecBlock(o: Stmt.SpecBlock): PreResult[Stmt.Spec] = {
    return PreResultStmtSpecBlock
  }

  def preStmtDeduceSequent(o: Stmt.DeduceSequent): PreResult[Stmt.Spec] = {
    return PreResultStmtDeduceSequent
  }

  def preStmtDeduceSteps(o: Stmt.DeduceSteps): PreResult[Stmt.Spec] = {
    return PreResultStmtDeduceSteps
  }

  def preStmtHavoc(o: Stmt.Havoc): PreResult[Stmt.Spec] = {
    return PreResultStmtHavoc
  }

  def preMethodContract(o: MethodContract): PreResult[MethodContract] = {
    o match {
      case o: MethodContract.Simple => return preMethodContractSimple(o)
      case o: MethodContract.Cases => return preMethodContractCases(o)
    }
  }

  def preMethodContractSimple(o: MethodContract.Simple): PreResult[MethodContract] = {
    return PreResultMethodContractSimple
  }

  def preMethodContractCases(o: MethodContract.Cases): PreResult[MethodContract] = {
    return PreResultMethodContractCases
  }

  def preMethodContractCase(o: MethodContract.Case): PreResult[MethodContract.Case] = {
    return PreResultMethodContractCase
  }

  def preSequent(o: Sequent): PreResult[Sequent] = {
    return PreResultSequent
  }

  def preProof(o: Proof): PreResult[Proof] = {
    return PreResultProof
  }

  def preProofStep(o: Proof.Step): PreResult[Proof.Step] = {
    o match {
      case o: Proof.Step.Regular => return preProofStepRegular(o)
      case o: Proof.Step.Assume => return preProofStepAssume(o)
      case o: Proof.Step.Assert => return preProofStepAssert(o)
      case o: Proof.Step.SubProof => return preProofStepSubProof(o)
      case o: Proof.Step.Let => return preProofStepLet(o)
      case o: Proof.Step.StructInduction => return preProofStepStructInduction(o)
    }
  }

  def preProofStepRegular(o: Proof.Step.Regular): PreResult[Proof.Step] = {
    return PreResultProofStepRegular
  }

  def preProofStepAssume(o: Proof.Step.Assume): PreResult[Proof.Step] = {
    return PreResultProofStepAssume
  }

  def preProofStepAssert(o: Proof.Step.Assert): PreResult[Proof.Step] = {
    return PreResultProofStepAssert
  }

  def preProofStepSubProof(o: Proof.Step.SubProof): PreResult[Proof.Step] = {
    return PreResultProofStepSubProof
  }

  def preProofStepLet(o: Proof.Step.Let): PreResult[Proof.Step] = {
    return PreResultProofStepLet
  }

  def preProofStepLetParam(o: Proof.Step.Let.Param): PreResult[Proof.Step.Let.Param] = {
    return PreResultProofStepLetParam
  }

  def preProofStepStructInduction(o: Proof.Step.StructInduction): PreResult[Proof.Step] = {
    return PreResultProofStepStructInduction
  }

  def preProofStepStructInductionMatchCase(o: Proof.Step.StructInduction.MatchCase): PreResult[Proof.Step.StructInduction.MatchCase] = {
    return PreResultProofStepStructInductionMatchCase
  }

  def preProofStepStructInductionMatchDefault(o: Proof.Step.StructInduction.MatchDefault): PreResult[Proof.Step.StructInduction.MatchDefault] = {
    return PreResultProofStepStructInductionMatchDefault
  }

  def preProofStepJustification(o: Proof.Step.Justification): PreResult[Proof.Step.Justification] = {
    return PreResultProofStepJustification
  }

  def preAssignExp(o: AssignExp): PreResult[AssignExp] = {
    o match {
      case o: Stmt.Block =>
        val r: PreResult[AssignExp] = preStmtBlock(o) match {
         case PreResult(continu, MSome(r: AssignExp)) => PreResult(continu, MSome[AssignExp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type AssignExp")
         case PreResult(continu, _) => PreResult(continu, MNone[AssignExp]())
        }
        return r
      case o: Stmt.If =>
        val r: PreResult[AssignExp] = preStmtIf(o) match {
         case PreResult(continu, MSome(r: AssignExp)) => PreResult(continu, MSome[AssignExp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type AssignExp")
         case PreResult(continu, _) => PreResult(continu, MNone[AssignExp]())
        }
        return r
      case o: Stmt.Match =>
        val r: PreResult[AssignExp] = preStmtMatch(o) match {
         case PreResult(continu, MSome(r: AssignExp)) => PreResult(continu, MSome[AssignExp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type AssignExp")
         case PreResult(continu, _) => PreResult(continu, MNone[AssignExp]())
        }
        return r
      case o: Stmt.Return =>
        val r: PreResult[AssignExp] = preStmtReturn(o) match {
         case PreResult(continu, MSome(r: AssignExp)) => PreResult(continu, MSome[AssignExp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type AssignExp")
         case PreResult(continu, _) => PreResult(continu, MNone[AssignExp]())
        }
        return r
      case o: Stmt.Expr =>
        val r: PreResult[AssignExp] = preStmtExpr(o) match {
         case PreResult(continu, MSome(r: AssignExp)) => PreResult(continu, MSome[AssignExp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type AssignExp")
         case PreResult(continu, _) => PreResult(continu, MNone[AssignExp]())
        }
        return r
    }
  }

  def preCase(o: Case): PreResult[Case] = {
    return PreResultCase
  }

  def preEnumGenRange(o: EnumGen.Range): PreResult[EnumGen.Range] = {
    o match {
      case o: EnumGen.Range.Expr => return preEnumGenRangeExpr(o)
      case o: EnumGen.Range.Step => return preEnumGenRangeStep(o)
    }
  }

  def preEnumGenRangeExpr(o: EnumGen.Range.Expr): PreResult[EnumGen.Range] = {
    return PreResultEnumGenRangeExpr
  }

  def preEnumGenRangeStep(o: EnumGen.Range.Step): PreResult[EnumGen.Range] = {
    return PreResultEnumGenRangeStep
  }

  def preEnumGenFor(o: EnumGen.For): PreResult[EnumGen.For] = {
    return PreResultEnumGenFor
  }

  def preType(o: Type): PreResult[Type] = {
    o match {
      case o: Type.Named => return preTypeNamed(o)
      case o: Type.Fun => return preTypeFun(o)
      case o: Type.Tuple => return preTypeTuple(o)
    }
  }

  def preTypeNamed(o: Type.Named): PreResult[Type] = {
    return PreResultTypeNamed
  }

  def preTypeFun(o: Type.Fun): PreResult[Type] = {
    return PreResultTypeFun
  }

  def preTypeTuple(o: Type.Tuple): PreResult[Type] = {
    return PreResultTypeTuple
  }

  def prePattern(o: Pattern): PreResult[Pattern] = {
    o match {
      case o: Pattern.Literal => return prePatternLiteral(o)
      case o: Pattern.LitInterpolate => return prePatternLitInterpolate(o)
      case o: Pattern.Ref => return prePatternRef(o)
      case o: Pattern.VarBinding => return prePatternVarBinding(o)
      case o: Pattern.Wildcard => return prePatternWildcard(o)
      case o: Pattern.SeqWildcard => return prePatternSeqWildcard(o)
      case o: Pattern.Structure => return prePatternStructure(o)
    }
  }

  def prePatternLiteral(o: Pattern.Literal): PreResult[Pattern] = {
    return PreResultPatternLiteral
  }

  def prePatternLitInterpolate(o: Pattern.LitInterpolate): PreResult[Pattern] = {
    return PreResultPatternLitInterpolate
  }

  def prePatternRef(o: Pattern.Ref): PreResult[Pattern] = {
    return PreResultPatternRef
  }

  def prePatternVarBinding(o: Pattern.VarBinding): PreResult[Pattern] = {
    return PreResultPatternVarBinding
  }

  def prePatternWildcard(o: Pattern.Wildcard): PreResult[Pattern] = {
    return PreResultPatternWildcard
  }

  def prePatternSeqWildcard(o: Pattern.SeqWildcard): PreResult[Pattern] = {
    return PreResultPatternSeqWildcard
  }

  def prePatternStructure(o: Pattern.Structure): PreResult[Pattern] = {
    return PreResultPatternStructure
  }

  def preExp(o: Exp): PreResult[Exp] = {
    o match {
      case o: Exp.LitB => return preExpLitB(o)
      case o: Exp.LitC => return preExpLitC(o)
      case o: Exp.LitZ => return preExpLitZ(o)
      case o: Exp.LitF32 => return preExpLitF32(o)
      case o: Exp.LitF64 => return preExpLitF64(o)
      case o: Exp.LitR => return preExpLitR(o)
      case o: Exp.LitString => return preExpLitString(o)
      case o: Exp.StringInterpolate => return preExpStringInterpolate(o)
      case o: Exp.This => return preExpThis(o)
      case o: Exp.Super => return preExpSuper(o)
      case o: Exp.Unary => return preExpUnary(o)
      case o: Exp.Binary => return preExpBinary(o)
      case o: Exp.Ident => return preExpIdent(o)
      case o: Exp.Eta => return preExpEta(o)
      case o: Exp.Tuple => return preExpTuple(o)
      case o: Exp.Select => return preExpSelect(o)
      case o: Exp.Invoke => return preExpInvoke(o)
      case o: Exp.InvokeNamed => return preExpInvokeNamed(o)
      case o: Exp.If => return preExpIf(o)
      case o: Exp.Fun => return preExpFun(o)
      case o: Exp.ForYield => return preExpForYield(o)
      case o: Exp.QuantType =>
        val r: PreResult[Exp] = preExpQuantType(o) match {
         case PreResult(continu, MSome(r: Exp)) => PreResult(continu, MSome[Exp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type Exp")
         case PreResult(continu, _) => PreResult(continu, MNone[Exp]())
        }
        return r
      case o: Exp.QuantRange =>
        val r: PreResult[Exp] = preExpQuantRange(o) match {
         case PreResult(continu, MSome(r: Exp)) => PreResult(continu, MSome[Exp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type Exp")
         case PreResult(continu, _) => PreResult(continu, MNone[Exp]())
        }
        return r
      case o: Exp.QuantEach =>
        val r: PreResult[Exp] = preExpQuantEach(o) match {
         case PreResult(continu, MSome(r: Exp)) => PreResult(continu, MSome[Exp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type Exp")
         case PreResult(continu, _) => PreResult(continu, MNone[Exp]())
        }
        return r
      case o: Exp.Input =>
        val r: PreResult[Exp] = preExpInput(o) match {
         case PreResult(continu, MSome(r: Exp)) => PreResult(continu, MSome[Exp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type Exp")
         case PreResult(continu, _) => PreResult(continu, MNone[Exp]())
        }
        return r
      case o: Exp.OldVal =>
        val r: PreResult[Exp] = preExpOldVal(o) match {
         case PreResult(continu, MSome(r: Exp)) => PreResult(continu, MSome[Exp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type Exp")
         case PreResult(continu, _) => PreResult(continu, MNone[Exp]())
        }
        return r
      case o: Exp.AtLoc =>
        val r: PreResult[Exp] = preExpAtLoc(o) match {
         case PreResult(continu, MSome(r: Exp)) => PreResult(continu, MSome[Exp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type Exp")
         case PreResult(continu, _) => PreResult(continu, MNone[Exp]())
        }
        return r
      case o: Exp.StateSeq =>
        val r: PreResult[Exp] = preExpStateSeq(o) match {
         case PreResult(continu, MSome(r: Exp)) => PreResult(continu, MSome[Exp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type Exp")
         case PreResult(continu, _) => PreResult(continu, MNone[Exp]())
        }
        return r
      case o: Exp.Result =>
        val r: PreResult[Exp] = preExpResult(o) match {
         case PreResult(continu, MSome(r: Exp)) => PreResult(continu, MSome[Exp](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type Exp")
         case PreResult(continu, _) => PreResult(continu, MNone[Exp]())
        }
        return r
    }
  }

  def preLit(o: Lit): PreResult[Lit] = {
    o match {
      case o: Exp.LitB =>
        val r: PreResult[Lit] = preExpLitB(o) match {
         case PreResult(continu, MSome(r: Lit)) => PreResult(continu, MSome[Lit](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type Lit")
         case PreResult(continu, _) => PreResult(continu, MNone[Lit]())
        }
        return r
      case o: Exp.LitC =>
        val r: PreResult[Lit] = preExpLitC(o) match {
         case PreResult(continu, MSome(r: Lit)) => PreResult(continu, MSome[Lit](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type Lit")
         case PreResult(continu, _) => PreResult(continu, MNone[Lit]())
        }
        return r
      case o: Exp.LitZ =>
        val r: PreResult[Lit] = preExpLitZ(o) match {
         case PreResult(continu, MSome(r: Lit)) => PreResult(continu, MSome[Lit](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type Lit")
         case PreResult(continu, _) => PreResult(continu, MNone[Lit]())
        }
        return r
      case o: Exp.LitF32 =>
        val r: PreResult[Lit] = preExpLitF32(o) match {
         case PreResult(continu, MSome(r: Lit)) => PreResult(continu, MSome[Lit](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type Lit")
         case PreResult(continu, _) => PreResult(continu, MNone[Lit]())
        }
        return r
      case o: Exp.LitF64 =>
        val r: PreResult[Lit] = preExpLitF64(o) match {
         case PreResult(continu, MSome(r: Lit)) => PreResult(continu, MSome[Lit](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type Lit")
         case PreResult(continu, _) => PreResult(continu, MNone[Lit]())
        }
        return r
      case o: Exp.LitR =>
        val r: PreResult[Lit] = preExpLitR(o) match {
         case PreResult(continu, MSome(r: Lit)) => PreResult(continu, MSome[Lit](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type Lit")
         case PreResult(continu, _) => PreResult(continu, MNone[Lit]())
        }
        return r
      case o: Exp.LitString =>
        val r: PreResult[Lit] = preExpLitString(o) match {
         case PreResult(continu, MSome(r: Lit)) => PreResult(continu, MSome[Lit](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type Lit")
         case PreResult(continu, _) => PreResult(continu, MNone[Lit]())
        }
        return r
    }
  }

  def preExpLitB(o: Exp.LitB): PreResult[Exp] = {
    return PreResultExpLitB
  }

  def preExpLitC(o: Exp.LitC): PreResult[Exp] = {
    return PreResultExpLitC
  }

  def preExpLitZ(o: Exp.LitZ): PreResult[Exp] = {
    return PreResultExpLitZ
  }

  def preExpLitF32(o: Exp.LitF32): PreResult[Exp] = {
    return PreResultExpLitF32
  }

  def preExpLitF64(o: Exp.LitF64): PreResult[Exp] = {
    return PreResultExpLitF64
  }

  def preExpLitR(o: Exp.LitR): PreResult[Exp] = {
    return PreResultExpLitR
  }

  def preExpLitString(o: Exp.LitString): PreResult[Exp] = {
    return PreResultExpLitString
  }

  def preExpStringInterpolate(o: Exp.StringInterpolate): PreResult[Exp] = {
    return PreResultExpStringInterpolate
  }

  def preExpThis(o: Exp.This): PreResult[Exp] = {
    return PreResultExpThis
  }

  def preExpSuper(o: Exp.Super): PreResult[Exp] = {
    return PreResultExpSuper
  }

  def preExpUnary(o: Exp.Unary): PreResult[Exp] = {
    return PreResultExpUnary
  }

  def preExpRef(o: Exp.Ref): PreResult[Exp.Ref] = {
    o match {
      case o: Exp.Ident =>
        val r: PreResult[Exp.Ref] = preExpIdent(o) match {
         case PreResult(continu, MSome(r: Exp.Ref)) => PreResult(continu, MSome[Exp.Ref](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type Exp.Ref")
         case PreResult(continu, _) => PreResult(continu, MNone[Exp.Ref]())
        }
        return r
      case o: Exp.Select =>
        val r: PreResult[Exp.Ref] = preExpSelect(o) match {
         case PreResult(continu, MSome(r: Exp.Ref)) => PreResult(continu, MSome[Exp.Ref](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type Exp.Ref")
         case PreResult(continu, _) => PreResult(continu, MNone[Exp.Ref]())
        }
        return r
    }
  }

  def preExpBinary(o: Exp.Binary): PreResult[Exp] = {
    return PreResultExpBinary
  }

  def preExpIdent(o: Exp.Ident): PreResult[Exp] = {
    return PreResultExpIdent
  }

  def preExpEta(o: Exp.Eta): PreResult[Exp] = {
    return PreResultExpEta
  }

  def preExpTuple(o: Exp.Tuple): PreResult[Exp] = {
    return PreResultExpTuple
  }

  def preExpSelect(o: Exp.Select): PreResult[Exp] = {
    return PreResultExpSelect
  }

  def preExpInvoke(o: Exp.Invoke): PreResult[Exp] = {
    return PreResultExpInvoke
  }

  def preExpInvokeNamed(o: Exp.InvokeNamed): PreResult[Exp] = {
    return PreResultExpInvokeNamed
  }

  def preExpIf(o: Exp.If): PreResult[Exp] = {
    return PreResultExpIf
  }

  def preExpFunParam(o: Exp.Fun.Param): PreResult[Exp.Fun.Param] = {
    return PreResultExpFunParam
  }

  def preExpFun(o: Exp.Fun): PreResult[Exp] = {
    return PreResultExpFun
  }

  def preExpForYield(o: Exp.ForYield): PreResult[Exp] = {
    return PreResultExpForYield
  }

  def preExpSpec(o: Exp.Spec): PreResult[Exp.Spec] = {
    o match {
      case o: Exp.QuantType =>
        val r: PreResult[Exp.Spec] = preExpQuantType(o) match {
         case PreResult(continu, MSome(r: Exp.Spec)) => PreResult(continu, MSome[Exp.Spec](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type Exp.Spec")
         case PreResult(continu, _) => PreResult(continu, MNone[Exp.Spec]())
        }
        return r
      case o: Exp.QuantRange =>
        val r: PreResult[Exp.Spec] = preExpQuantRange(o) match {
         case PreResult(continu, MSome(r: Exp.Spec)) => PreResult(continu, MSome[Exp.Spec](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type Exp.Spec")
         case PreResult(continu, _) => PreResult(continu, MNone[Exp.Spec]())
        }
        return r
      case o: Exp.QuantEach =>
        val r: PreResult[Exp.Spec] = preExpQuantEach(o) match {
         case PreResult(continu, MSome(r: Exp.Spec)) => PreResult(continu, MSome[Exp.Spec](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type Exp.Spec")
         case PreResult(continu, _) => PreResult(continu, MNone[Exp.Spec]())
        }
        return r
      case o: Exp.Input => return preExpInput(o)
      case o: Exp.OldVal => return preExpOldVal(o)
      case o: Exp.AtLoc => return preExpAtLoc(o)
      case o: Exp.StateSeq => return preExpStateSeq(o)
      case o: Exp.Result => return preExpResult(o)
    }
  }

  def preExpQuant(o: Exp.Quant): PreResult[Exp.Quant] = {
    o match {
      case o: Exp.QuantType => return preExpQuantType(o)
      case o: Exp.QuantRange => return preExpQuantRange(o)
      case o: Exp.QuantEach => return preExpQuantEach(o)
    }
  }

  def preExpQuantType(o: Exp.QuantType): PreResult[Exp.Quant] = {
    return PreResultExpQuantType
  }

  def preExpQuantRange(o: Exp.QuantRange): PreResult[Exp.Quant] = {
    return PreResultExpQuantRange
  }

  def preExpQuantEach(o: Exp.QuantEach): PreResult[Exp.Quant] = {
    return PreResultExpQuantEach
  }

  def preExpInput(o: Exp.Input): PreResult[Exp.Spec] = {
    return PreResultExpInput
  }

  def preExpOldVal(o: Exp.OldVal): PreResult[Exp.Spec] = {
    return PreResultExpOldVal
  }

  def preExpAtLoc(o: Exp.AtLoc): PreResult[Exp.Spec] = {
    return PreResultExpAtLoc
  }

  def preExpStateSeq(o: Exp.StateSeq): PreResult[Exp.Spec] = {
    return PreResultExpStateSeq
  }

  def preExpStateSeqFragment(o: Exp.StateSeq.Fragment): PreResult[Exp.StateSeq.Fragment] = {
    return PreResultExpStateSeqFragment
  }

  def preExpResult(o: Exp.Result): PreResult[Exp.Spec] = {
    return PreResultExpResult
  }

  def preNamedArg(o: NamedArg): PreResult[NamedArg] = {
    return PreResultNamedArg
  }

  def preId(o: Id): PreResult[Id] = {
    return PreResultId
  }

  def preName(o: Name): PreResult[Name] = {
    return PreResultName
  }

  def preBody(o: Body): PreResult[Body] = {
    return PreResultBody
  }

  def preAdtParam(o: AdtParam): PreResult[AdtParam] = {
    return PreResultAdtParam
  }

  def preMethodSig(o: MethodSig): PreResult[MethodSig] = {
    return PreResultMethodSig
  }

  def preParam(o: Param): PreResult[Param] = {
    return PreResultParam
  }

  def preTypeParam(o: TypeParam): PreResult[TypeParam] = {
    return PreResultTypeParam
  }

  def preTyped(o: Typed): PreResult[Typed] = {
    o match {
      case o: Typed.Name => return preTypedName(o)
      case o: Typed.Tuple => return preTypedTuple(o)
      case o: Typed.Fun => return preTypedFun(o)
      case o: Typed.TypeVar => return preTypedTypeVar(o)
      case o: Typed.Package => return preTypedPackage(o)
      case o: Typed.Object => return preTypedObject(o)
      case o: Typed.Enum => return preTypedEnum(o)
      case o: Typed.Method => return preTypedMethod(o)
      case o: Typed.Methods => return preTypedMethods(o)
      case o: Typed.Fact => return preTypedFact(o)
      case o: Typed.Theorem => return preTypedTheorem(o)
      case o: Typed.Inv => return preTypedInv(o)
    }
  }

  def preTypedName(o: Typed.Name): PreResult[Typed] = {
    return PreResultTypedName
  }

  def preTypedTuple(o: Typed.Tuple): PreResult[Typed] = {
    return PreResultTypedTuple
  }

  def preTypedFun(o: Typed.Fun): PreResult[Typed] = {
    return PreResultTypedFun
  }

  def preTypedTypeVar(o: Typed.TypeVar): PreResult[Typed] = {
    return PreResultTypedTypeVar
  }

  def preTypedPackage(o: Typed.Package): PreResult[Typed] = {
    return PreResultTypedPackage
  }

  def preTypedObject(o: Typed.Object): PreResult[Typed] = {
    return PreResultTypedObject
  }

  def preTypedEnum(o: Typed.Enum): PreResult[Typed] = {
    return PreResultTypedEnum
  }

  def preTypedMethod(o: Typed.Method): PreResult[Typed] = {
    return PreResultTypedMethod
  }

  def preTypedMethods(o: Typed.Methods): PreResult[Typed] = {
    return PreResultTypedMethods
  }

  def preTypedFact(o: Typed.Fact): PreResult[Typed] = {
    return PreResultTypedFact
  }

  def preTypedTheorem(o: Typed.Theorem): PreResult[Typed] = {
    return PreResultTypedTheorem
  }

  def preTypedInv(o: Typed.Inv): PreResult[Typed] = {
    return PreResultTypedInv
  }

  def preAttr(o: Attr): PreResult[Attr] = {
    return PreResultAttr
  }

  def preTypedAttr(o: TypedAttr): PreResult[TypedAttr] = {
    return PreResultTypedAttr
  }

  def preResolvedAttr(o: ResolvedAttr): PreResult[ResolvedAttr] = {
    return PreResultResolvedAttr
  }

  def preResolvedInfo(o: ResolvedInfo): PreResult[ResolvedInfo] = {
    o match {
      case o: ResolvedInfo.BuiltIn => return preResolvedInfoBuiltIn(o)
      case o: ResolvedInfo.Package => return preResolvedInfoPackage(o)
      case o: ResolvedInfo.Enum => return preResolvedInfoEnum(o)
      case o: ResolvedInfo.EnumElement => return preResolvedInfoEnumElement(o)
      case o: ResolvedInfo.Object => return preResolvedInfoObject(o)
      case o: ResolvedInfo.Var => return preResolvedInfoVar(o)
      case o: ResolvedInfo.Method => return preResolvedInfoMethod(o)
      case o: ResolvedInfo.Methods => return preResolvedInfoMethods(o)
      case o: ResolvedInfo.Tuple => return preResolvedInfoTuple(o)
      case o: ResolvedInfo.LocalVar => return preResolvedInfoLocalVar(o)
      case o: ResolvedInfo.Fact => return preResolvedInfoFact(o)
      case o: ResolvedInfo.Theorem => return preResolvedInfoTheorem(o)
      case o: ResolvedInfo.Inv => return preResolvedInfoInv(o)
    }
  }

  def preResolvedInfoBuiltIn(o: ResolvedInfo.BuiltIn): PreResult[ResolvedInfo] = {
    return PreResultResolvedInfoBuiltIn
  }

  def preResolvedInfoPackage(o: ResolvedInfo.Package): PreResult[ResolvedInfo] = {
    return PreResultResolvedInfoPackage
  }

  def preResolvedInfoEnum(o: ResolvedInfo.Enum): PreResult[ResolvedInfo] = {
    return PreResultResolvedInfoEnum
  }

  def preResolvedInfoEnumElement(o: ResolvedInfo.EnumElement): PreResult[ResolvedInfo] = {
    return PreResultResolvedInfoEnumElement
  }

  def preResolvedInfoObject(o: ResolvedInfo.Object): PreResult[ResolvedInfo] = {
    return PreResultResolvedInfoObject
  }

  def preResolvedInfoVar(o: ResolvedInfo.Var): PreResult[ResolvedInfo] = {
    return PreResultResolvedInfoVar
  }

  def preResolvedInfoMethod(o: ResolvedInfo.Method): PreResult[ResolvedInfo] = {
    return PreResultResolvedInfoMethod
  }

  def preResolvedInfoMethods(o: ResolvedInfo.Methods): PreResult[ResolvedInfo] = {
    return PreResultResolvedInfoMethods
  }

  def preResolvedInfoTuple(o: ResolvedInfo.Tuple): PreResult[ResolvedInfo] = {
    return PreResultResolvedInfoTuple
  }

  def preResolvedInfoLocalVar(o: ResolvedInfo.LocalVar): PreResult[ResolvedInfo] = {
    return PreResultResolvedInfoLocalVar
  }

  def preResolvedInfoFact(o: ResolvedInfo.Fact): PreResult[ResolvedInfo] = {
    return PreResultResolvedInfoFact
  }

  def preResolvedInfoTheorem(o: ResolvedInfo.Theorem): PreResult[ResolvedInfo] = {
    return PreResultResolvedInfoTheorem
  }

  def preResolvedInfoInv(o: ResolvedInfo.Inv): PreResult[ResolvedInfo] = {
    return PreResultResolvedInfoInv
  }

  def preTruthTableRow(o: TruthTable.Row): PreResult[TruthTable.Row] = {
    return PreResultTruthTableRow
  }

  def preTruthTableAssignment(o: TruthTable.Assignment): PreResult[TruthTable.Assignment] = {
    return PreResultTruthTableAssignment
  }

  def preTruthTableConclusion(o: TruthTable.Conclusion): PreResult[TruthTable.Conclusion] = {
    o match {
      case o: TruthTable.Conclusion.Validity => return preTruthTableConclusionValidity(o)
      case o: TruthTable.Conclusion.Tautology => return preTruthTableConclusionTautology(o)
      case o: TruthTable.Conclusion.Contradictory => return preTruthTableConclusionContradictory(o)
      case o: TruthTable.Conclusion.Contingent => return preTruthTableConclusionContingent(o)
    }
  }

  def preTruthTableConclusionValidity(o: TruthTable.Conclusion.Validity): PreResult[TruthTable.Conclusion] = {
    return PreResultTruthTableConclusionValidity
  }

  def preTruthTableConclusionTautology(o: TruthTable.Conclusion.Tautology): PreResult[TruthTable.Conclusion] = {
    return PreResultTruthTableConclusionTautology
  }

  def preTruthTableConclusionContradictory(o: TruthTable.Conclusion.Contradictory): PreResult[TruthTable.Conclusion] = {
    return PreResultTruthTableConclusionContradictory
  }

  def preTruthTableConclusionContingent(o: TruthTable.Conclusion.Contingent): PreResult[TruthTable.Conclusion] = {
    return PreResultTruthTableConclusionContingent
  }

  def postTopUnit(o: TopUnit): MOption[TopUnit] = {
    o match {
      case o: TopUnit.Program => return postTopUnitProgram(o)
      case o: TopUnit.SequentUnit => return postTopUnitSequentUnit(o)
      case o: TopUnit.TruthTableUnit => return postTopUnitTruthTableUnit(o)
    }
  }

  def postTopUnitProgram(o: TopUnit.Program): MOption[TopUnit] = {
    return PostResultTopUnitProgram
  }

  def postTopUnitSequentUnit(o: TopUnit.SequentUnit): MOption[TopUnit] = {
    return PostResultTopUnitSequentUnit
  }

  def postTopUnitTruthTableUnit(o: TopUnit.TruthTableUnit): MOption[TopUnit] = {
    return PostResultTopUnitTruthTableUnit
  }

  def postStmt(o: Stmt): MOption[Stmt] = {
    o match {
      case o: Stmt.Import => return postStmtImport(o)
      case o: Stmt.Var => return postStmtVar(o)
      case o: Stmt.VarPattern => return postStmtVarPattern(o)
      case o: Stmt.SpecVar => return postStmtSpecVar(o)
      case o: Stmt.Method => return postStmtMethod(o)
      case o: Stmt.ExtMethod => return postStmtExtMethod(o)
      case o: Stmt.SpecMethod => return postStmtSpecMethod(o)
      case o: Stmt.Enum => return postStmtEnum(o)
      case o: Stmt.SubZ => return postStmtSubZ(o)
      case o: Stmt.Object => return postStmtObject(o)
      case o: Stmt.Sig => return postStmtSig(o)
      case o: Stmt.Adt => return postStmtAdt(o)
      case o: Stmt.TypeAlias => return postStmtTypeAlias(o)
      case o: Stmt.Assign => return postStmtAssign(o)
      case o: Stmt.Block => return postStmtBlock(o)
      case o: Stmt.If => return postStmtIf(o)
      case o: Stmt.Match => return postStmtMatch(o)
      case o: Stmt.While => return postStmtWhile(o)
      case o: Stmt.DoWhile => return postStmtDoWhile(o)
      case o: Stmt.For => return postStmtFor(o)
      case o: Stmt.Return => return postStmtReturn(o)
      case o: Stmt.Expr => return postStmtExpr(o)
      case o: Stmt.Fact =>
        val r: MOption[Stmt] = postStmtFact(o) match {
         case MSome(result: Stmt) => MSome[Stmt](result)
         case MSome(_) => halt("Can only produce object of type Stmt")
         case _ => MNone[Stmt]()
        }
        return r
      case o: Stmt.Inv =>
        val r: MOption[Stmt] = postStmtInv(o) match {
         case MSome(result: Stmt) => MSome[Stmt](result)
         case MSome(_) => halt("Can only produce object of type Stmt")
         case _ => MNone[Stmt]()
        }
        return r
      case o: Stmt.Theorem =>
        val r: MOption[Stmt] = postStmtTheorem(o) match {
         case MSome(result: Stmt) => MSome[Stmt](result)
         case MSome(_) => halt("Can only produce object of type Stmt")
         case _ => MNone[Stmt]()
        }
        return r
      case o: Stmt.DataRefinement =>
        val r: MOption[Stmt] = postStmtDataRefinement(o) match {
         case MSome(result: Stmt) => MSome[Stmt](result)
         case MSome(_) => halt("Can only produce object of type Stmt")
         case _ => MNone[Stmt]()
        }
        return r
      case o: Stmt.SpecLabel =>
        val r: MOption[Stmt] = postStmtSpecLabel(o) match {
         case MSome(result: Stmt) => MSome[Stmt](result)
         case MSome(_) => halt("Can only produce object of type Stmt")
         case _ => MNone[Stmt]()
        }
        return r
      case o: Stmt.SpecBlock =>
        val r: MOption[Stmt] = postStmtSpecBlock(o) match {
         case MSome(result: Stmt) => MSome[Stmt](result)
         case MSome(_) => halt("Can only produce object of type Stmt")
         case _ => MNone[Stmt]()
        }
        return r
      case o: Stmt.DeduceSequent =>
        val r: MOption[Stmt] = postStmtDeduceSequent(o) match {
         case MSome(result: Stmt) => MSome[Stmt](result)
         case MSome(_) => halt("Can only produce object of type Stmt")
         case _ => MNone[Stmt]()
        }
        return r
      case o: Stmt.DeduceSteps =>
        val r: MOption[Stmt] = postStmtDeduceSteps(o) match {
         case MSome(result: Stmt) => MSome[Stmt](result)
         case MSome(_) => halt("Can only produce object of type Stmt")
         case _ => MNone[Stmt]()
        }
        return r
      case o: Stmt.Havoc =>
        val r: MOption[Stmt] = postStmtHavoc(o) match {
         case MSome(result: Stmt) => MSome[Stmt](result)
         case MSome(_) => halt("Can only produce object of type Stmt")
         case _ => MNone[Stmt]()
        }
        return r
    }
  }

  def postHasModifies(o: HasModifies): MOption[HasModifies] = {
    o match {
      case o: Stmt.While =>
        val r: MOption[HasModifies] = postStmtWhile(o) match {
         case MSome(result: HasModifies) => MSome[HasModifies](result)
         case MSome(_) => halt("Can only produce object of type HasModifies")
         case _ => MNone[HasModifies]()
        }
        return r
      case o: Stmt.DoWhile =>
        val r: MOption[HasModifies] = postStmtDoWhile(o) match {
         case MSome(result: HasModifies) => MSome[HasModifies](result)
         case MSome(_) => halt("Can only produce object of type HasModifies")
         case _ => MNone[HasModifies]()
        }
        return r
      case o: Stmt.For =>
        val r: MOption[HasModifies] = postStmtFor(o) match {
         case MSome(result: HasModifies) => MSome[HasModifies](result)
         case MSome(_) => halt("Can only produce object of type HasModifies")
         case _ => MNone[HasModifies]()
        }
        return r
      case o: MethodContract.Simple =>
        val r: MOption[HasModifies] = postMethodContractSimple(o) match {
         case MSome(result: HasModifies) => MSome[HasModifies](result)
         case MSome(_) => halt("Can only produce object of type HasModifies")
         case _ => MNone[HasModifies]()
        }
        return r
      case o: MethodContract.Cases =>
        val r: MOption[HasModifies] = postMethodContractCases(o) match {
         case MSome(result: HasModifies) => MSome[HasModifies](result)
         case MSome(_) => halt("Can only produce object of type HasModifies")
         case _ => MNone[HasModifies]()
        }
        return r
    }
  }

  def postStmtImport(o: Stmt.Import): MOption[Stmt] = {
    return PostResultStmtImport
  }

  def postStmtImportImporter(o: Stmt.Import.Importer): MOption[Stmt.Import.Importer] = {
    return PostResultStmtImportImporter
  }

  def postStmtImportSelector(o: Stmt.Import.Selector): MOption[Stmt.Import.Selector] = {
    o match {
      case o: Stmt.Import.MultiSelector => return postStmtImportMultiSelector(o)
      case o: Stmt.Import.WildcardSelector => return postStmtImportWildcardSelector(o)
    }
  }

  def postStmtImportMultiSelector(o: Stmt.Import.MultiSelector): MOption[Stmt.Import.Selector] = {
    return PostResultStmtImportMultiSelector
  }

  def postStmtImportWildcardSelector(o: Stmt.Import.WildcardSelector): MOption[Stmt.Import.Selector] = {
    return PostResultStmtImportWildcardSelector
  }

  def postStmtImportNamedSelector(o: Stmt.Import.NamedSelector): MOption[Stmt.Import.NamedSelector] = {
    return PostResultStmtImportNamedSelector
  }

  def postStmtVar(o: Stmt.Var): MOption[Stmt] = {
    return PostResultStmtVar
  }

  def postStmtVarPattern(o: Stmt.VarPattern): MOption[Stmt] = {
    return PostResultStmtVarPattern
  }

  def postStmtSpecVar(o: Stmt.SpecVar): MOption[Stmt] = {
    return PostResultStmtSpecVar
  }

  def postStmtMethod(o: Stmt.Method): MOption[Stmt] = {
    return PostResultStmtMethod
  }

  def postStmtExtMethod(o: Stmt.ExtMethod): MOption[Stmt] = {
    return PostResultStmtExtMethod
  }

  def postStmtSpecMethod(o: Stmt.SpecMethod): MOption[Stmt] = {
    return PostResultStmtSpecMethod
  }

  def postStmtEnum(o: Stmt.Enum): MOption[Stmt] = {
    return PostResultStmtEnum
  }

  def postStmtSubZ(o: Stmt.SubZ): MOption[Stmt] = {
    return PostResultStmtSubZ
  }

  def postStmtObject(o: Stmt.Object): MOption[Stmt] = {
    return PostResultStmtObject
  }

  def postStmtSig(o: Stmt.Sig): MOption[Stmt] = {
    return PostResultStmtSig
  }

  def postStmtAdt(o: Stmt.Adt): MOption[Stmt] = {
    return PostResultStmtAdt
  }

  def postStmtTypeAlias(o: Stmt.TypeAlias): MOption[Stmt] = {
    return PostResultStmtTypeAlias
  }

  def postStmtAssign(o: Stmt.Assign): MOption[Stmt] = {
    return PostResultStmtAssign
  }

  def postStmtBlock(o: Stmt.Block): MOption[Stmt] = {
    return PostResultStmtBlock
  }

  def postStmtIf(o: Stmt.If): MOption[Stmt] = {
    return PostResultStmtIf
  }

  def postStmtMatch(o: Stmt.Match): MOption[Stmt] = {
    return PostResultStmtMatch
  }

  def postStmtLoop(o: Stmt.Loop): MOption[Stmt.Loop] = {
    o match {
      case o: Stmt.While =>
        val r: MOption[Stmt.Loop] = postStmtWhile(o) match {
         case MSome(result: Stmt.Loop) => MSome[Stmt.Loop](result)
         case MSome(_) => halt("Can only produce object of type Stmt.Loop")
         case _ => MNone[Stmt.Loop]()
        }
        return r
      case o: Stmt.DoWhile =>
        val r: MOption[Stmt.Loop] = postStmtDoWhile(o) match {
         case MSome(result: Stmt.Loop) => MSome[Stmt.Loop](result)
         case MSome(_) => halt("Can only produce object of type Stmt.Loop")
         case _ => MNone[Stmt.Loop]()
        }
        return r
      case o: Stmt.For =>
        val r: MOption[Stmt.Loop] = postStmtFor(o) match {
         case MSome(result: Stmt.Loop) => MSome[Stmt.Loop](result)
         case MSome(_) => halt("Can only produce object of type Stmt.Loop")
         case _ => MNone[Stmt.Loop]()
        }
        return r
    }
  }

  def postStmtWhile(o: Stmt.While): MOption[Stmt] = {
    return PostResultStmtWhile
  }

  def postStmtDoWhile(o: Stmt.DoWhile): MOption[Stmt] = {
    return PostResultStmtDoWhile
  }

  def postStmtFor(o: Stmt.For): MOption[Stmt] = {
    return PostResultStmtFor
  }

  def postStmtReturn(o: Stmt.Return): MOption[Stmt] = {
    return PostResultStmtReturn
  }

  def postStmtExpr(o: Stmt.Expr): MOption[Stmt] = {
    return PostResultStmtExpr
  }

  def postStmtSpec(o: Stmt.Spec): MOption[Stmt.Spec] = {
    o match {
      case o: Stmt.Fact => return postStmtFact(o)
      case o: Stmt.Inv => return postStmtInv(o)
      case o: Stmt.Theorem => return postStmtTheorem(o)
      case o: Stmt.DataRefinement => return postStmtDataRefinement(o)
      case o: Stmt.SpecLabel => return postStmtSpecLabel(o)
      case o: Stmt.SpecBlock => return postStmtSpecBlock(o)
      case o: Stmt.DeduceSequent => return postStmtDeduceSequent(o)
      case o: Stmt.DeduceSteps => return postStmtDeduceSteps(o)
      case o: Stmt.Havoc => return postStmtHavoc(o)
    }
  }

  def postStmtFact(o: Stmt.Fact): MOption[Stmt.Spec] = {
    return PostResultStmtFact
  }

  def postStmtInv(o: Stmt.Inv): MOption[Stmt.Spec] = {
    return PostResultStmtInv
  }

  def postStmtTheorem(o: Stmt.Theorem): MOption[Stmt.Spec] = {
    return PostResultStmtTheorem
  }

  def postStmtDataRefinement(o: Stmt.DataRefinement): MOption[Stmt.Spec] = {
    return PostResultStmtDataRefinement
  }

  def postStmtSpecLabel(o: Stmt.SpecLabel): MOption[Stmt.Spec] = {
    return PostResultStmtSpecLabel
  }

  def postStmtSpecBlock(o: Stmt.SpecBlock): MOption[Stmt.Spec] = {
    return PostResultStmtSpecBlock
  }

  def postStmtDeduceSequent(o: Stmt.DeduceSequent): MOption[Stmt.Spec] = {
    return PostResultStmtDeduceSequent
  }

  def postStmtDeduceSteps(o: Stmt.DeduceSteps): MOption[Stmt.Spec] = {
    return PostResultStmtDeduceSteps
  }

  def postStmtHavoc(o: Stmt.Havoc): MOption[Stmt.Spec] = {
    return PostResultStmtHavoc
  }

  def postMethodContract(o: MethodContract): MOption[MethodContract] = {
    o match {
      case o: MethodContract.Simple => return postMethodContractSimple(o)
      case o: MethodContract.Cases => return postMethodContractCases(o)
    }
  }

  def postMethodContractSimple(o: MethodContract.Simple): MOption[MethodContract] = {
    return PostResultMethodContractSimple
  }

  def postMethodContractCases(o: MethodContract.Cases): MOption[MethodContract] = {
    return PostResultMethodContractCases
  }

  def postMethodContractCase(o: MethodContract.Case): MOption[MethodContract.Case] = {
    return PostResultMethodContractCase
  }

  def postSequent(o: Sequent): MOption[Sequent] = {
    return PostResultSequent
  }

  def postProof(o: Proof): MOption[Proof] = {
    return PostResultProof
  }

  def postProofStep(o: Proof.Step): MOption[Proof.Step] = {
    o match {
      case o: Proof.Step.Regular => return postProofStepRegular(o)
      case o: Proof.Step.Assume => return postProofStepAssume(o)
      case o: Proof.Step.Assert => return postProofStepAssert(o)
      case o: Proof.Step.SubProof => return postProofStepSubProof(o)
      case o: Proof.Step.Let => return postProofStepLet(o)
      case o: Proof.Step.StructInduction => return postProofStepStructInduction(o)
    }
  }

  def postProofStepRegular(o: Proof.Step.Regular): MOption[Proof.Step] = {
    return PostResultProofStepRegular
  }

  def postProofStepAssume(o: Proof.Step.Assume): MOption[Proof.Step] = {
    return PostResultProofStepAssume
  }

  def postProofStepAssert(o: Proof.Step.Assert): MOption[Proof.Step] = {
    return PostResultProofStepAssert
  }

  def postProofStepSubProof(o: Proof.Step.SubProof): MOption[Proof.Step] = {
    return PostResultProofStepSubProof
  }

  def postProofStepLet(o: Proof.Step.Let): MOption[Proof.Step] = {
    return PostResultProofStepLet
  }

  def postProofStepLetParam(o: Proof.Step.Let.Param): MOption[Proof.Step.Let.Param] = {
    return PostResultProofStepLetParam
  }

  def postProofStepStructInduction(o: Proof.Step.StructInduction): MOption[Proof.Step] = {
    return PostResultProofStepStructInduction
  }

  def postProofStepStructInductionMatchCase(o: Proof.Step.StructInduction.MatchCase): MOption[Proof.Step.StructInduction.MatchCase] = {
    return PostResultProofStepStructInductionMatchCase
  }

  def postProofStepStructInductionMatchDefault(o: Proof.Step.StructInduction.MatchDefault): MOption[Proof.Step.StructInduction.MatchDefault] = {
    return PostResultProofStepStructInductionMatchDefault
  }

  def postProofStepJustification(o: Proof.Step.Justification): MOption[Proof.Step.Justification] = {
    return PostResultProofStepJustification
  }

  def postAssignExp(o: AssignExp): MOption[AssignExp] = {
    o match {
      case o: Stmt.Block =>
        val r: MOption[AssignExp] = postStmtBlock(o) match {
         case MSome(result: AssignExp) => MSome[AssignExp](result)
         case MSome(_) => halt("Can only produce object of type AssignExp")
         case _ => MNone[AssignExp]()
        }
        return r
      case o: Stmt.If =>
        val r: MOption[AssignExp] = postStmtIf(o) match {
         case MSome(result: AssignExp) => MSome[AssignExp](result)
         case MSome(_) => halt("Can only produce object of type AssignExp")
         case _ => MNone[AssignExp]()
        }
        return r
      case o: Stmt.Match =>
        val r: MOption[AssignExp] = postStmtMatch(o) match {
         case MSome(result: AssignExp) => MSome[AssignExp](result)
         case MSome(_) => halt("Can only produce object of type AssignExp")
         case _ => MNone[AssignExp]()
        }
        return r
      case o: Stmt.Return =>
        val r: MOption[AssignExp] = postStmtReturn(o) match {
         case MSome(result: AssignExp) => MSome[AssignExp](result)
         case MSome(_) => halt("Can only produce object of type AssignExp")
         case _ => MNone[AssignExp]()
        }
        return r
      case o: Stmt.Expr =>
        val r: MOption[AssignExp] = postStmtExpr(o) match {
         case MSome(result: AssignExp) => MSome[AssignExp](result)
         case MSome(_) => halt("Can only produce object of type AssignExp")
         case _ => MNone[AssignExp]()
        }
        return r
    }
  }

  def postCase(o: Case): MOption[Case] = {
    return PostResultCase
  }

  def postEnumGenRange(o: EnumGen.Range): MOption[EnumGen.Range] = {
    o match {
      case o: EnumGen.Range.Expr => return postEnumGenRangeExpr(o)
      case o: EnumGen.Range.Step => return postEnumGenRangeStep(o)
    }
  }

  def postEnumGenRangeExpr(o: EnumGen.Range.Expr): MOption[EnumGen.Range] = {
    return PostResultEnumGenRangeExpr
  }

  def postEnumGenRangeStep(o: EnumGen.Range.Step): MOption[EnumGen.Range] = {
    return PostResultEnumGenRangeStep
  }

  def postEnumGenFor(o: EnumGen.For): MOption[EnumGen.For] = {
    return PostResultEnumGenFor
  }

  def postType(o: Type): MOption[Type] = {
    o match {
      case o: Type.Named => return postTypeNamed(o)
      case o: Type.Fun => return postTypeFun(o)
      case o: Type.Tuple => return postTypeTuple(o)
    }
  }

  def postTypeNamed(o: Type.Named): MOption[Type] = {
    return PostResultTypeNamed
  }

  def postTypeFun(o: Type.Fun): MOption[Type] = {
    return PostResultTypeFun
  }

  def postTypeTuple(o: Type.Tuple): MOption[Type] = {
    return PostResultTypeTuple
  }

  def postPattern(o: Pattern): MOption[Pattern] = {
    o match {
      case o: Pattern.Literal => return postPatternLiteral(o)
      case o: Pattern.LitInterpolate => return postPatternLitInterpolate(o)
      case o: Pattern.Ref => return postPatternRef(o)
      case o: Pattern.VarBinding => return postPatternVarBinding(o)
      case o: Pattern.Wildcard => return postPatternWildcard(o)
      case o: Pattern.SeqWildcard => return postPatternSeqWildcard(o)
      case o: Pattern.Structure => return postPatternStructure(o)
    }
  }

  def postPatternLiteral(o: Pattern.Literal): MOption[Pattern] = {
    return PostResultPatternLiteral
  }

  def postPatternLitInterpolate(o: Pattern.LitInterpolate): MOption[Pattern] = {
    return PostResultPatternLitInterpolate
  }

  def postPatternRef(o: Pattern.Ref): MOption[Pattern] = {
    return PostResultPatternRef
  }

  def postPatternVarBinding(o: Pattern.VarBinding): MOption[Pattern] = {
    return PostResultPatternVarBinding
  }

  def postPatternWildcard(o: Pattern.Wildcard): MOption[Pattern] = {
    return PostResultPatternWildcard
  }

  def postPatternSeqWildcard(o: Pattern.SeqWildcard): MOption[Pattern] = {
    return PostResultPatternSeqWildcard
  }

  def postPatternStructure(o: Pattern.Structure): MOption[Pattern] = {
    return PostResultPatternStructure
  }

  def postExp(o: Exp): MOption[Exp] = {
    o match {
      case o: Exp.LitB => return postExpLitB(o)
      case o: Exp.LitC => return postExpLitC(o)
      case o: Exp.LitZ => return postExpLitZ(o)
      case o: Exp.LitF32 => return postExpLitF32(o)
      case o: Exp.LitF64 => return postExpLitF64(o)
      case o: Exp.LitR => return postExpLitR(o)
      case o: Exp.LitString => return postExpLitString(o)
      case o: Exp.StringInterpolate => return postExpStringInterpolate(o)
      case o: Exp.This => return postExpThis(o)
      case o: Exp.Super => return postExpSuper(o)
      case o: Exp.Unary => return postExpUnary(o)
      case o: Exp.Binary => return postExpBinary(o)
      case o: Exp.Ident => return postExpIdent(o)
      case o: Exp.Eta => return postExpEta(o)
      case o: Exp.Tuple => return postExpTuple(o)
      case o: Exp.Select => return postExpSelect(o)
      case o: Exp.Invoke => return postExpInvoke(o)
      case o: Exp.InvokeNamed => return postExpInvokeNamed(o)
      case o: Exp.If => return postExpIf(o)
      case o: Exp.Fun => return postExpFun(o)
      case o: Exp.ForYield => return postExpForYield(o)
      case o: Exp.QuantType =>
        val r: MOption[Exp] = postExpQuantType(o) match {
         case MSome(result: Exp) => MSome[Exp](result)
         case MSome(_) => halt("Can only produce object of type Exp")
         case _ => MNone[Exp]()
        }
        return r
      case o: Exp.QuantRange =>
        val r: MOption[Exp] = postExpQuantRange(o) match {
         case MSome(result: Exp) => MSome[Exp](result)
         case MSome(_) => halt("Can only produce object of type Exp")
         case _ => MNone[Exp]()
        }
        return r
      case o: Exp.QuantEach =>
        val r: MOption[Exp] = postExpQuantEach(o) match {
         case MSome(result: Exp) => MSome[Exp](result)
         case MSome(_) => halt("Can only produce object of type Exp")
         case _ => MNone[Exp]()
        }
        return r
      case o: Exp.Input =>
        val r: MOption[Exp] = postExpInput(o) match {
         case MSome(result: Exp) => MSome[Exp](result)
         case MSome(_) => halt("Can only produce object of type Exp")
         case _ => MNone[Exp]()
        }
        return r
      case o: Exp.OldVal =>
        val r: MOption[Exp] = postExpOldVal(o) match {
         case MSome(result: Exp) => MSome[Exp](result)
         case MSome(_) => halt("Can only produce object of type Exp")
         case _ => MNone[Exp]()
        }
        return r
      case o: Exp.AtLoc =>
        val r: MOption[Exp] = postExpAtLoc(o) match {
         case MSome(result: Exp) => MSome[Exp](result)
         case MSome(_) => halt("Can only produce object of type Exp")
         case _ => MNone[Exp]()
        }
        return r
      case o: Exp.StateSeq =>
        val r: MOption[Exp] = postExpStateSeq(o) match {
         case MSome(result: Exp) => MSome[Exp](result)
         case MSome(_) => halt("Can only produce object of type Exp")
         case _ => MNone[Exp]()
        }
        return r
      case o: Exp.Result =>
        val r: MOption[Exp] = postExpResult(o) match {
         case MSome(result: Exp) => MSome[Exp](result)
         case MSome(_) => halt("Can only produce object of type Exp")
         case _ => MNone[Exp]()
        }
        return r
    }
  }

  def postLit(o: Lit): MOption[Lit] = {
    o match {
      case o: Exp.LitB =>
        val r: MOption[Lit] = postExpLitB(o) match {
         case MSome(result: Lit) => MSome[Lit](result)
         case MSome(_) => halt("Can only produce object of type Lit")
         case _ => MNone[Lit]()
        }
        return r
      case o: Exp.LitC =>
        val r: MOption[Lit] = postExpLitC(o) match {
         case MSome(result: Lit) => MSome[Lit](result)
         case MSome(_) => halt("Can only produce object of type Lit")
         case _ => MNone[Lit]()
        }
        return r
      case o: Exp.LitZ =>
        val r: MOption[Lit] = postExpLitZ(o) match {
         case MSome(result: Lit) => MSome[Lit](result)
         case MSome(_) => halt("Can only produce object of type Lit")
         case _ => MNone[Lit]()
        }
        return r
      case o: Exp.LitF32 =>
        val r: MOption[Lit] = postExpLitF32(o) match {
         case MSome(result: Lit) => MSome[Lit](result)
         case MSome(_) => halt("Can only produce object of type Lit")
         case _ => MNone[Lit]()
        }
        return r
      case o: Exp.LitF64 =>
        val r: MOption[Lit] = postExpLitF64(o) match {
         case MSome(result: Lit) => MSome[Lit](result)
         case MSome(_) => halt("Can only produce object of type Lit")
         case _ => MNone[Lit]()
        }
        return r
      case o: Exp.LitR =>
        val r: MOption[Lit] = postExpLitR(o) match {
         case MSome(result: Lit) => MSome[Lit](result)
         case MSome(_) => halt("Can only produce object of type Lit")
         case _ => MNone[Lit]()
        }
        return r
      case o: Exp.LitString =>
        val r: MOption[Lit] = postExpLitString(o) match {
         case MSome(result: Lit) => MSome[Lit](result)
         case MSome(_) => halt("Can only produce object of type Lit")
         case _ => MNone[Lit]()
        }
        return r
    }
  }

  def postExpLitB(o: Exp.LitB): MOption[Exp] = {
    return PostResultExpLitB
  }

  def postExpLitC(o: Exp.LitC): MOption[Exp] = {
    return PostResultExpLitC
  }

  def postExpLitZ(o: Exp.LitZ): MOption[Exp] = {
    return PostResultExpLitZ
  }

  def postExpLitF32(o: Exp.LitF32): MOption[Exp] = {
    return PostResultExpLitF32
  }

  def postExpLitF64(o: Exp.LitF64): MOption[Exp] = {
    return PostResultExpLitF64
  }

  def postExpLitR(o: Exp.LitR): MOption[Exp] = {
    return PostResultExpLitR
  }

  def postExpLitString(o: Exp.LitString): MOption[Exp] = {
    return PostResultExpLitString
  }

  def postExpStringInterpolate(o: Exp.StringInterpolate): MOption[Exp] = {
    return PostResultExpStringInterpolate
  }

  def postExpThis(o: Exp.This): MOption[Exp] = {
    return PostResultExpThis
  }

  def postExpSuper(o: Exp.Super): MOption[Exp] = {
    return PostResultExpSuper
  }

  def postExpUnary(o: Exp.Unary): MOption[Exp] = {
    return PostResultExpUnary
  }

  def postExpRef(o: Exp.Ref): MOption[Exp.Ref] = {
    o match {
      case o: Exp.Ident =>
        val r: MOption[Exp.Ref] = postExpIdent(o) match {
         case MSome(result: Exp.Ref) => MSome[Exp.Ref](result)
         case MSome(_) => halt("Can only produce object of type Exp.Ref")
         case _ => MNone[Exp.Ref]()
        }
        return r
      case o: Exp.Select =>
        val r: MOption[Exp.Ref] = postExpSelect(o) match {
         case MSome(result: Exp.Ref) => MSome[Exp.Ref](result)
         case MSome(_) => halt("Can only produce object of type Exp.Ref")
         case _ => MNone[Exp.Ref]()
        }
        return r
    }
  }

  def postExpBinary(o: Exp.Binary): MOption[Exp] = {
    return PostResultExpBinary
  }

  def postExpIdent(o: Exp.Ident): MOption[Exp] = {
    return PostResultExpIdent
  }

  def postExpEta(o: Exp.Eta): MOption[Exp] = {
    return PostResultExpEta
  }

  def postExpTuple(o: Exp.Tuple): MOption[Exp] = {
    return PostResultExpTuple
  }

  def postExpSelect(o: Exp.Select): MOption[Exp] = {
    return PostResultExpSelect
  }

  def postExpInvoke(o: Exp.Invoke): MOption[Exp] = {
    return PostResultExpInvoke
  }

  def postExpInvokeNamed(o: Exp.InvokeNamed): MOption[Exp] = {
    return PostResultExpInvokeNamed
  }

  def postExpIf(o: Exp.If): MOption[Exp] = {
    return PostResultExpIf
  }

  def postExpFunParam(o: Exp.Fun.Param): MOption[Exp.Fun.Param] = {
    return PostResultExpFunParam
  }

  def postExpFun(o: Exp.Fun): MOption[Exp] = {
    return PostResultExpFun
  }

  def postExpForYield(o: Exp.ForYield): MOption[Exp] = {
    return PostResultExpForYield
  }

  def postExpSpec(o: Exp.Spec): MOption[Exp.Spec] = {
    o match {
      case o: Exp.QuantType =>
        val r: MOption[Exp.Spec] = postExpQuantType(o) match {
         case MSome(result: Exp.Spec) => MSome[Exp.Spec](result)
         case MSome(_) => halt("Can only produce object of type Exp.Spec")
         case _ => MNone[Exp.Spec]()
        }
        return r
      case o: Exp.QuantRange =>
        val r: MOption[Exp.Spec] = postExpQuantRange(o) match {
         case MSome(result: Exp.Spec) => MSome[Exp.Spec](result)
         case MSome(_) => halt("Can only produce object of type Exp.Spec")
         case _ => MNone[Exp.Spec]()
        }
        return r
      case o: Exp.QuantEach =>
        val r: MOption[Exp.Spec] = postExpQuantEach(o) match {
         case MSome(result: Exp.Spec) => MSome[Exp.Spec](result)
         case MSome(_) => halt("Can only produce object of type Exp.Spec")
         case _ => MNone[Exp.Spec]()
        }
        return r
      case o: Exp.Input => return postExpInput(o)
      case o: Exp.OldVal => return postExpOldVal(o)
      case o: Exp.AtLoc => return postExpAtLoc(o)
      case o: Exp.StateSeq => return postExpStateSeq(o)
      case o: Exp.Result => return postExpResult(o)
    }
  }

  def postExpQuant(o: Exp.Quant): MOption[Exp.Quant] = {
    o match {
      case o: Exp.QuantType => return postExpQuantType(o)
      case o: Exp.QuantRange => return postExpQuantRange(o)
      case o: Exp.QuantEach => return postExpQuantEach(o)
    }
  }

  def postExpQuantType(o: Exp.QuantType): MOption[Exp.Quant] = {
    return PostResultExpQuantType
  }

  def postExpQuantRange(o: Exp.QuantRange): MOption[Exp.Quant] = {
    return PostResultExpQuantRange
  }

  def postExpQuantEach(o: Exp.QuantEach): MOption[Exp.Quant] = {
    return PostResultExpQuantEach
  }

  def postExpInput(o: Exp.Input): MOption[Exp.Spec] = {
    return PostResultExpInput
  }

  def postExpOldVal(o: Exp.OldVal): MOption[Exp.Spec] = {
    return PostResultExpOldVal
  }

  def postExpAtLoc(o: Exp.AtLoc): MOption[Exp.Spec] = {
    return PostResultExpAtLoc
  }

  def postExpStateSeq(o: Exp.StateSeq): MOption[Exp.Spec] = {
    return PostResultExpStateSeq
  }

  def postExpStateSeqFragment(o: Exp.StateSeq.Fragment): MOption[Exp.StateSeq.Fragment] = {
    return PostResultExpStateSeqFragment
  }

  def postExpResult(o: Exp.Result): MOption[Exp.Spec] = {
    return PostResultExpResult
  }

  def postNamedArg(o: NamedArg): MOption[NamedArg] = {
    return PostResultNamedArg
  }

  def postId(o: Id): MOption[Id] = {
    return PostResultId
  }

  def postName(o: Name): MOption[Name] = {
    return PostResultName
  }

  def postBody(o: Body): MOption[Body] = {
    return PostResultBody
  }

  def postAdtParam(o: AdtParam): MOption[AdtParam] = {
    return PostResultAdtParam
  }

  def postMethodSig(o: MethodSig): MOption[MethodSig] = {
    return PostResultMethodSig
  }

  def postParam(o: Param): MOption[Param] = {
    return PostResultParam
  }

  def postTypeParam(o: TypeParam): MOption[TypeParam] = {
    return PostResultTypeParam
  }

  def postTyped(o: Typed): MOption[Typed] = {
    o match {
      case o: Typed.Name => return postTypedName(o)
      case o: Typed.Tuple => return postTypedTuple(o)
      case o: Typed.Fun => return postTypedFun(o)
      case o: Typed.TypeVar => return postTypedTypeVar(o)
      case o: Typed.Package => return postTypedPackage(o)
      case o: Typed.Object => return postTypedObject(o)
      case o: Typed.Enum => return postTypedEnum(o)
      case o: Typed.Method => return postTypedMethod(o)
      case o: Typed.Methods => return postTypedMethods(o)
      case o: Typed.Fact => return postTypedFact(o)
      case o: Typed.Theorem => return postTypedTheorem(o)
      case o: Typed.Inv => return postTypedInv(o)
    }
  }

  def postTypedName(o: Typed.Name): MOption[Typed] = {
    return PostResultTypedName
  }

  def postTypedTuple(o: Typed.Tuple): MOption[Typed] = {
    return PostResultTypedTuple
  }

  def postTypedFun(o: Typed.Fun): MOption[Typed] = {
    return PostResultTypedFun
  }

  def postTypedTypeVar(o: Typed.TypeVar): MOption[Typed] = {
    return PostResultTypedTypeVar
  }

  def postTypedPackage(o: Typed.Package): MOption[Typed] = {
    return PostResultTypedPackage
  }

  def postTypedObject(o: Typed.Object): MOption[Typed] = {
    return PostResultTypedObject
  }

  def postTypedEnum(o: Typed.Enum): MOption[Typed] = {
    return PostResultTypedEnum
  }

  def postTypedMethod(o: Typed.Method): MOption[Typed] = {
    return PostResultTypedMethod
  }

  def postTypedMethods(o: Typed.Methods): MOption[Typed] = {
    return PostResultTypedMethods
  }

  def postTypedFact(o: Typed.Fact): MOption[Typed] = {
    return PostResultTypedFact
  }

  def postTypedTheorem(o: Typed.Theorem): MOption[Typed] = {
    return PostResultTypedTheorem
  }

  def postTypedInv(o: Typed.Inv): MOption[Typed] = {
    return PostResultTypedInv
  }

  def postAttr(o: Attr): MOption[Attr] = {
    return PostResultAttr
  }

  def postTypedAttr(o: TypedAttr): MOption[TypedAttr] = {
    return PostResultTypedAttr
  }

  def postResolvedAttr(o: ResolvedAttr): MOption[ResolvedAttr] = {
    return PostResultResolvedAttr
  }

  def postResolvedInfo(o: ResolvedInfo): MOption[ResolvedInfo] = {
    o match {
      case o: ResolvedInfo.BuiltIn => return postResolvedInfoBuiltIn(o)
      case o: ResolvedInfo.Package => return postResolvedInfoPackage(o)
      case o: ResolvedInfo.Enum => return postResolvedInfoEnum(o)
      case o: ResolvedInfo.EnumElement => return postResolvedInfoEnumElement(o)
      case o: ResolvedInfo.Object => return postResolvedInfoObject(o)
      case o: ResolvedInfo.Var => return postResolvedInfoVar(o)
      case o: ResolvedInfo.Method => return postResolvedInfoMethod(o)
      case o: ResolvedInfo.Methods => return postResolvedInfoMethods(o)
      case o: ResolvedInfo.Tuple => return postResolvedInfoTuple(o)
      case o: ResolvedInfo.LocalVar => return postResolvedInfoLocalVar(o)
      case o: ResolvedInfo.Fact => return postResolvedInfoFact(o)
      case o: ResolvedInfo.Theorem => return postResolvedInfoTheorem(o)
      case o: ResolvedInfo.Inv => return postResolvedInfoInv(o)
    }
  }

  def postResolvedInfoBuiltIn(o: ResolvedInfo.BuiltIn): MOption[ResolvedInfo] = {
    return PostResultResolvedInfoBuiltIn
  }

  def postResolvedInfoPackage(o: ResolvedInfo.Package): MOption[ResolvedInfo] = {
    return PostResultResolvedInfoPackage
  }

  def postResolvedInfoEnum(o: ResolvedInfo.Enum): MOption[ResolvedInfo] = {
    return PostResultResolvedInfoEnum
  }

  def postResolvedInfoEnumElement(o: ResolvedInfo.EnumElement): MOption[ResolvedInfo] = {
    return PostResultResolvedInfoEnumElement
  }

  def postResolvedInfoObject(o: ResolvedInfo.Object): MOption[ResolvedInfo] = {
    return PostResultResolvedInfoObject
  }

  def postResolvedInfoVar(o: ResolvedInfo.Var): MOption[ResolvedInfo] = {
    return PostResultResolvedInfoVar
  }

  def postResolvedInfoMethod(o: ResolvedInfo.Method): MOption[ResolvedInfo] = {
    return PostResultResolvedInfoMethod
  }

  def postResolvedInfoMethods(o: ResolvedInfo.Methods): MOption[ResolvedInfo] = {
    return PostResultResolvedInfoMethods
  }

  def postResolvedInfoTuple(o: ResolvedInfo.Tuple): MOption[ResolvedInfo] = {
    return PostResultResolvedInfoTuple
  }

  def postResolvedInfoLocalVar(o: ResolvedInfo.LocalVar): MOption[ResolvedInfo] = {
    return PostResultResolvedInfoLocalVar
  }

  def postResolvedInfoFact(o: ResolvedInfo.Fact): MOption[ResolvedInfo] = {
    return PostResultResolvedInfoFact
  }

  def postResolvedInfoTheorem(o: ResolvedInfo.Theorem): MOption[ResolvedInfo] = {
    return PostResultResolvedInfoTheorem
  }

  def postResolvedInfoInv(o: ResolvedInfo.Inv): MOption[ResolvedInfo] = {
    return PostResultResolvedInfoInv
  }

  def postTruthTableRow(o: TruthTable.Row): MOption[TruthTable.Row] = {
    return PostResultTruthTableRow
  }

  def postTruthTableAssignment(o: TruthTable.Assignment): MOption[TruthTable.Assignment] = {
    return PostResultTruthTableAssignment
  }

  def postTruthTableConclusion(o: TruthTable.Conclusion): MOption[TruthTable.Conclusion] = {
    o match {
      case o: TruthTable.Conclusion.Validity => return postTruthTableConclusionValidity(o)
      case o: TruthTable.Conclusion.Tautology => return postTruthTableConclusionTautology(o)
      case o: TruthTable.Conclusion.Contradictory => return postTruthTableConclusionContradictory(o)
      case o: TruthTable.Conclusion.Contingent => return postTruthTableConclusionContingent(o)
    }
  }

  def postTruthTableConclusionValidity(o: TruthTable.Conclusion.Validity): MOption[TruthTable.Conclusion] = {
    return PostResultTruthTableConclusionValidity
  }

  def postTruthTableConclusionTautology(o: TruthTable.Conclusion.Tautology): MOption[TruthTable.Conclusion] = {
    return PostResultTruthTableConclusionTautology
  }

  def postTruthTableConclusionContradictory(o: TruthTable.Conclusion.Contradictory): MOption[TruthTable.Conclusion] = {
    return PostResultTruthTableConclusionContradictory
  }

  def postTruthTableConclusionContingent(o: TruthTable.Conclusion.Contingent): MOption[TruthTable.Conclusion] = {
    return PostResultTruthTableConclusionContingent
  }

  def transformTopUnit(o: TopUnit): MOption[TopUnit] = {
    val preR: PreResult[TopUnit] = preTopUnit(o)
    val r: MOption[TopUnit] = if (preR.continu) {
      val o2: TopUnit = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[TopUnit] = o2 match {
        case o2: TopUnit.Program =>
          val r0: MOption[Name] = transformName(o2.packageName)
          val r1: MOption[Body] = transformBody(o2.body)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(packageName = r0.getOrElse(o2.packageName), body = r1.getOrElse(o2.body)))
          else
            MNone()
        case o2: TopUnit.SequentUnit =>
          val r0: MOption[Sequent] = transformSequent(o2.sequent)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(sequent = r0.getOrElse(o2.sequent)))
          else
            MNone()
        case o2: TopUnit.TruthTableUnit =>
          val r0: MOption[IS[Z, Id]] = transformISZ(o2.vars, transformId _)
          val r1: MOption[Sequent] = transformSequent(o2.sequent)
          val r2: MOption[IS[Z, TruthTable.Row]] = transformISZ(o2.rows, transformTruthTableRow _)
          val r3: MOption[Option[TruthTable.Conclusion]] = transformOption(o2.conclusionOpt, transformTruthTableConclusion _)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty)
            MSome(o2(vars = r0.getOrElse(o2.vars), sequent = r1.getOrElse(o2.sequent), rows = r2.getOrElse(o2.rows), conclusionOpt = r3.getOrElse(o2.conclusionOpt)))
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: TopUnit = r.getOrElse(o)
    val postR: MOption[TopUnit] = postTopUnit(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformStmt(o: Stmt): MOption[Stmt] = {
    val preR: PreResult[Stmt] = preStmt(o)
    val r: MOption[Stmt] = if (preR.continu) {
      val o2: Stmt = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[Stmt] = o2 match {
        case o2: Stmt.Import =>
          val r0: MOption[IS[Z, Stmt.Import.Importer]] = transformISZ(o2.importers, transformStmtImportImporter _)
          val r1: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(importers = r0.getOrElse(o2.importers), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.Var =>
          val r0: MOption[Id] = transformId(o2.id)
          val r1: MOption[Option[Type]] = transformOption(o2.tipeOpt, transformType _)
          val r2: MOption[Option[AssignExp]] = transformOption(o2.initOpt, transformAssignExp _)
          val r3: MOption[ResolvedAttr] = transformResolvedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty)
            MSome(o2(id = r0.getOrElse(o2.id), tipeOpt = r1.getOrElse(o2.tipeOpt), initOpt = r2.getOrElse(o2.initOpt), attr = r3.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.VarPattern =>
          val r0: MOption[Pattern] = transformPattern(o2.pattern)
          val r1: MOption[Option[Type]] = transformOption(o2.tipeOpt, transformType _)
          val r2: MOption[AssignExp] = transformAssignExp(o2.init)
          val r3: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty)
            MSome(o2(pattern = r0.getOrElse(o2.pattern), tipeOpt = r1.getOrElse(o2.tipeOpt), init = r2.getOrElse(o2.init), attr = r3.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.SpecVar =>
          val r0: MOption[Id] = transformId(o2.id)
          val r1: MOption[Type] = transformType(o2.tipe)
          val r2: MOption[ResolvedAttr] = transformResolvedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(id = r0.getOrElse(o2.id), tipe = r1.getOrElse(o2.tipe), attr = r2.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.Method =>
          val r0: MOption[MethodSig] = transformMethodSig(o2.sig)
          val r1: MOption[MethodContract] = transformMethodContract(o2.contract)
          val r2: MOption[Option[Body]] = transformOption(o2.bodyOpt, transformBody _)
          val r3: MOption[ResolvedAttr] = transformResolvedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty)
            MSome(o2(sig = r0.getOrElse(o2.sig), contract = r1.getOrElse(o2.contract), bodyOpt = r2.getOrElse(o2.bodyOpt), attr = r3.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.ExtMethod =>
          val r0: MOption[MethodSig] = transformMethodSig(o2.sig)
          val r1: MOption[MethodContract] = transformMethodContract(o2.contract)
          val r2: MOption[ResolvedAttr] = transformResolvedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(sig = r0.getOrElse(o2.sig), contract = r1.getOrElse(o2.contract), attr = r2.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.SpecMethod =>
          val r0: MOption[MethodSig] = transformMethodSig(o2.sig)
          val r1: MOption[ResolvedAttr] = transformResolvedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(sig = r0.getOrElse(o2.sig), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.Enum =>
          val r0: MOption[Id] = transformId(o2.id)
          val r1: MOption[IS[Z, Id]] = transformISZ(o2.elements, transformId _)
          val r2: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(id = r0.getOrElse(o2.id), elements = r1.getOrElse(o2.elements), attr = r2.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.SubZ =>
          val r0: MOption[Id] = transformId(o2.id)
          val r1: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(id = r0.getOrElse(o2.id), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.Object =>
          val r0: MOption[Id] = transformId(o2.id)
          val r1: MOption[IS[Z, Stmt]] = transformISZ(o2.stmts, transformStmt _)
          val r2: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(id = r0.getOrElse(o2.id), stmts = r1.getOrElse(o2.stmts), attr = r2.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.Sig =>
          val r0: MOption[Id] = transformId(o2.id)
          val r1: MOption[IS[Z, TypeParam]] = transformISZ(o2.typeParams, transformTypeParam _)
          val r2: MOption[IS[Z, Type.Named]] = transformISZ(o2.parents, transformTypeNamed _)
          val r3: MOption[IS[Z, Stmt]] = transformISZ(o2.stmts, transformStmt _)
          val r4: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty || r4.nonEmpty)
            MSome(o2(id = r0.getOrElse(o2.id), typeParams = r1.getOrElse(o2.typeParams), parents = r2.getOrElse(o2.parents), stmts = r3.getOrElse(o2.stmts), attr = r4.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.Adt =>
          val r0: MOption[Id] = transformId(o2.id)
          val r1: MOption[IS[Z, TypeParam]] = transformISZ(o2.typeParams, transformTypeParam _)
          val r2: MOption[IS[Z, AdtParam]] = transformISZ(o2.params, transformAdtParam _)
          val r3: MOption[IS[Z, Type.Named]] = transformISZ(o2.parents, transformTypeNamed _)
          val r4: MOption[IS[Z, Stmt]] = transformISZ(o2.stmts, transformStmt _)
          val r5: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty || r4.nonEmpty || r5.nonEmpty)
            MSome(o2(id = r0.getOrElse(o2.id), typeParams = r1.getOrElse(o2.typeParams), params = r2.getOrElse(o2.params), parents = r3.getOrElse(o2.parents), stmts = r4.getOrElse(o2.stmts), attr = r5.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.TypeAlias =>
          val r0: MOption[Id] = transformId(o2.id)
          val r1: MOption[IS[Z, TypeParam]] = transformISZ(o2.typeParams, transformTypeParam _)
          val r2: MOption[Type] = transformType(o2.tipe)
          val r3: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty)
            MSome(o2(id = r0.getOrElse(o2.id), typeParams = r1.getOrElse(o2.typeParams), tipe = r2.getOrElse(o2.tipe), attr = r3.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.Assign =>
          val r0: MOption[Exp] = transformExp(o2.lhs)
          val r1: MOption[AssignExp] = transformAssignExp(o2.rhs)
          val r2: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(lhs = r0.getOrElse(o2.lhs), rhs = r1.getOrElse(o2.rhs), attr = r2.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.Block =>
          val r0: MOption[Body] = transformBody(o2.body)
          val r1: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(body = r0.getOrElse(o2.body), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.If =>
          val r0: MOption[Exp] = transformExp(o2.cond)
          val r1: MOption[Body] = transformBody(o2.thenBody)
          val r2: MOption[Body] = transformBody(o2.elseBody)
          val r3: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty)
            MSome(o2(cond = r0.getOrElse(o2.cond), thenBody = r1.getOrElse(o2.thenBody), elseBody = r2.getOrElse(o2.elseBody), attr = r3.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.Match =>
          val r0: MOption[Exp] = transformExp(o2.exp)
          val r1: MOption[IS[Z, Case]] = transformISZ(o2.cases, transformCase _)
          val r2: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(exp = r0.getOrElse(o2.exp), cases = r1.getOrElse(o2.cases), attr = r2.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.While =>
          val r0: MOption[Exp] = transformExp(o2.cond)
          val r1: MOption[IS[Z, Exp]] = transformISZ(o2.invariants, transformExp _)
          val r2: MOption[IS[Z, Exp.Ident]] = transformISZ(o2.modifies, transformExpIdent _)
          val r3: MOption[Body] = transformBody(o2.body)
          val r4: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty || r4.nonEmpty)
            MSome(o2(cond = r0.getOrElse(o2.cond), invariants = r1.getOrElse(o2.invariants), modifies = r2.getOrElse(o2.modifies), body = r3.getOrElse(o2.body), attr = r4.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.DoWhile =>
          val r0: MOption[Exp] = transformExp(o2.cond)
          val r1: MOption[IS[Z, Exp]] = transformISZ(o2.invariants, transformExp _)
          val r2: MOption[IS[Z, Exp.Ident]] = transformISZ(o2.modifies, transformExpIdent _)
          val r3: MOption[Body] = transformBody(o2.body)
          val r4: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty || r4.nonEmpty)
            MSome(o2(cond = r0.getOrElse(o2.cond), invariants = r1.getOrElse(o2.invariants), modifies = r2.getOrElse(o2.modifies), body = r3.getOrElse(o2.body), attr = r4.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.For =>
          val r0: MOption[IS[Z, EnumGen.For]] = transformISZ(o2.enumGens, transformEnumGenFor _)
          val r1: MOption[IS[Z, Exp]] = transformISZ(o2.invariants, transformExp _)
          val r2: MOption[IS[Z, Exp.Ident]] = transformISZ(o2.modifies, transformExpIdent _)
          val r3: MOption[Body] = transformBody(o2.body)
          val r4: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty || r4.nonEmpty)
            MSome(o2(enumGens = r0.getOrElse(o2.enumGens), invariants = r1.getOrElse(o2.invariants), modifies = r2.getOrElse(o2.modifies), body = r3.getOrElse(o2.body), attr = r4.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.Return =>
          val r0: MOption[Option[Exp]] = transformOption(o2.expOpt, transformExp _)
          val r1: MOption[TypedAttr] = transformTypedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(expOpt = r0.getOrElse(o2.expOpt), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.Expr =>
          val r0: MOption[Exp] = transformExp(o2.exp)
          val r1: MOption[TypedAttr] = transformTypedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(exp = r0.getOrElse(o2.exp), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.Fact =>
          val r0: MOption[Id] = transformId(o2.id)
          val r1: MOption[IS[Z, TypeParam]] = transformISZ(o2.typeParams, transformTypeParam _)
          val r2: MOption[Option[Exp.LitString]] = transformOption(o2.descOpt, transformExpLitString _)
          val r3: MOption[IS[Z, Exp]] = transformISZ(o2.claims, transformExp _)
          val r4: MOption[ResolvedAttr] = transformResolvedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty || r4.nonEmpty)
            MSome(o2(id = r0.getOrElse(o2.id), typeParams = r1.getOrElse(o2.typeParams), descOpt = r2.getOrElse(o2.descOpt), claims = r3.getOrElse(o2.claims), attr = r4.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.Inv =>
          val r0: MOption[Id] = transformId(o2.id)
          val r1: MOption[IS[Z, Exp]] = transformISZ(o2.claims, transformExp _)
          val r2: MOption[ResolvedAttr] = transformResolvedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(id = r0.getOrElse(o2.id), claims = r1.getOrElse(o2.claims), attr = r2.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.Theorem =>
          val r0: MOption[Id] = transformId(o2.id)
          val r1: MOption[IS[Z, TypeParam]] = transformISZ(o2.typeParams, transformTypeParam _)
          val r2: MOption[Option[Exp.LitString]] = transformOption(o2.descOpt, transformExpLitString _)
          val r3: MOption[Exp] = transformExp(o2.claim)
          val r4: MOption[Proof] = transformProof(o2.proof)
          val r5: MOption[ResolvedAttr] = transformResolvedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty || r4.nonEmpty || r5.nonEmpty)
            MSome(o2(id = r0.getOrElse(o2.id), typeParams = r1.getOrElse(o2.typeParams), descOpt = r2.getOrElse(o2.descOpt), claim = r3.getOrElse(o2.claim), proof = r4.getOrElse(o2.proof), attr = r5.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.DataRefinement =>
          val r0: MOption[Exp.Ident] = transformExpIdent(o2.rep)
          val r1: MOption[IS[Z, Exp.Ident]] = transformISZ(o2.refs, transformExpIdent _)
          val r2: MOption[IS[Z, Exp]] = transformISZ(o2.claims, transformExp _)
          val r3: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty)
            MSome(o2(rep = r0.getOrElse(o2.rep), refs = r1.getOrElse(o2.refs), claims = r2.getOrElse(o2.claims), attr = r3.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.SpecLabel =>
          val r0: MOption[Id] = transformId(o2.id)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(id = r0.getOrElse(o2.id)))
          else
            MNone()
        case o2: Stmt.SpecBlock =>
          val r0: MOption[Stmt.Block] = transformStmtBlock(o2.block)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(block = r0.getOrElse(o2.block)))
          else
            MNone()
        case o2: Stmt.DeduceSequent =>
          val r0: MOption[Option[Exp.LitString]] = transformOption(o2.justOpt, transformExpLitString _)
          val r1: MOption[IS[Z, Sequent]] = transformISZ(o2.sequents, transformSequent _)
          val r2: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(justOpt = r0.getOrElse(o2.justOpt), sequents = r1.getOrElse(o2.sequents), attr = r2.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.DeduceSteps =>
          val r0: MOption[IS[Z, Proof.Step]] = transformISZ(o2.steps, transformProofStep _)
          val r1: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(steps = r0.getOrElse(o2.steps), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.Havoc =>
          val r0: MOption[IS[Z, Exp.Ident]] = transformISZ(o2.args, transformExpIdent _)
          val r1: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(args = r0.getOrElse(o2.args), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Stmt = r.getOrElse(o)
    val postR: MOption[Stmt] = postStmt(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformHasModifies(o: HasModifies): MOption[HasModifies] = {
    val preR: PreResult[HasModifies] = preHasModifies(o)
    val r: MOption[HasModifies] = if (preR.continu) {
      val o2: HasModifies = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[HasModifies] = o2 match {
        case o2: Stmt.While =>
          val r0: MOption[Exp] = transformExp(o2.cond)
          val r1: MOption[IS[Z, Exp]] = transformISZ(o2.invariants, transformExp _)
          val r2: MOption[IS[Z, Exp.Ident]] = transformISZ(o2.modifies, transformExpIdent _)
          val r3: MOption[Body] = transformBody(o2.body)
          val r4: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty || r4.nonEmpty)
            MSome(o2(cond = r0.getOrElse(o2.cond), invariants = r1.getOrElse(o2.invariants), modifies = r2.getOrElse(o2.modifies), body = r3.getOrElse(o2.body), attr = r4.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.DoWhile =>
          val r0: MOption[Exp] = transformExp(o2.cond)
          val r1: MOption[IS[Z, Exp]] = transformISZ(o2.invariants, transformExp _)
          val r2: MOption[IS[Z, Exp.Ident]] = transformISZ(o2.modifies, transformExpIdent _)
          val r3: MOption[Body] = transformBody(o2.body)
          val r4: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty || r4.nonEmpty)
            MSome(o2(cond = r0.getOrElse(o2.cond), invariants = r1.getOrElse(o2.invariants), modifies = r2.getOrElse(o2.modifies), body = r3.getOrElse(o2.body), attr = r4.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.For =>
          val r0: MOption[IS[Z, EnumGen.For]] = transformISZ(o2.enumGens, transformEnumGenFor _)
          val r1: MOption[IS[Z, Exp]] = transformISZ(o2.invariants, transformExp _)
          val r2: MOption[IS[Z, Exp.Ident]] = transformISZ(o2.modifies, transformExpIdent _)
          val r3: MOption[Body] = transformBody(o2.body)
          val r4: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty || r4.nonEmpty)
            MSome(o2(enumGens = r0.getOrElse(o2.enumGens), invariants = r1.getOrElse(o2.invariants), modifies = r2.getOrElse(o2.modifies), body = r3.getOrElse(o2.body), attr = r4.getOrElse(o2.attr)))
          else
            MNone()
        case o2: MethodContract.Simple =>
          val r0: MOption[IS[Z, Exp.Ident]] = transformISZ(o2.reads, transformExpIdent _)
          val r1: MOption[IS[Z, Exp]] = transformISZ(o2.requires, transformExp _)
          val r2: MOption[IS[Z, Exp.Ident]] = transformISZ(o2.modifies, transformExpIdent _)
          val r3: MOption[IS[Z, Exp]] = transformISZ(o2.ensures, transformExp _)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty)
            MSome(o2(reads = r0.getOrElse(o2.reads), requires = r1.getOrElse(o2.requires), modifies = r2.getOrElse(o2.modifies), ensures = r3.getOrElse(o2.ensures)))
          else
            MNone()
        case o2: MethodContract.Cases =>
          val r0: MOption[IS[Z, Exp.Ident]] = transformISZ(o2.reads, transformExpIdent _)
          val r1: MOption[IS[Z, Exp.Ident]] = transformISZ(o2.modifies, transformExpIdent _)
          val r2: MOption[IS[Z, MethodContract.Case]] = transformISZ(o2.cases, transformMethodContractCase _)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(reads = r0.getOrElse(o2.reads), modifies = r1.getOrElse(o2.modifies), cases = r2.getOrElse(o2.cases)))
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: HasModifies = r.getOrElse(o)
    val postR: MOption[HasModifies] = postHasModifies(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformStmtImportImporter(o: Stmt.Import.Importer): MOption[Stmt.Import.Importer] = {
    val preR: PreResult[Stmt.Import.Importer] = preStmtImportImporter(o)
    val r: MOption[Stmt.Import.Importer] = if (preR.continu) {
      val o2: Stmt.Import.Importer = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Name] = transformName(o2.name)
      val r1: MOption[Option[Stmt.Import.Selector]] = transformOption(o2.selectorOpt, transformStmtImportSelector _)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty)
        MSome(o2(name = r0.getOrElse(o2.name), selectorOpt = r1.getOrElse(o2.selectorOpt)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Stmt.Import.Importer = r.getOrElse(o)
    val postR: MOption[Stmt.Import.Importer] = postStmtImportImporter(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformStmtImportSelector(o: Stmt.Import.Selector): MOption[Stmt.Import.Selector] = {
    val preR: PreResult[Stmt.Import.Selector] = preStmtImportSelector(o)
    val r: MOption[Stmt.Import.Selector] = if (preR.continu) {
      val o2: Stmt.Import.Selector = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[Stmt.Import.Selector] = o2 match {
        case o2: Stmt.Import.MultiSelector =>
          val r0: MOption[IS[Z, Stmt.Import.NamedSelector]] = transformISZ(o2.selectors, transformStmtImportNamedSelector _)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(selectors = r0.getOrElse(o2.selectors)))
          else
            MNone()
        case o2: Stmt.Import.WildcardSelector =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Stmt.Import.Selector = r.getOrElse(o)
    val postR: MOption[Stmt.Import.Selector] = postStmtImportSelector(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformStmtImportNamedSelector(o: Stmt.Import.NamedSelector): MOption[Stmt.Import.NamedSelector] = {
    val preR: PreResult[Stmt.Import.NamedSelector] = preStmtImportNamedSelector(o)
    val r: MOption[Stmt.Import.NamedSelector] = if (preR.continu) {
      val o2: Stmt.Import.NamedSelector = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Id] = transformId(o2.from)
      val r1: MOption[Id] = transformId(o2.to)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty)
        MSome(o2(from = r0.getOrElse(o2.from), to = r1.getOrElse(o2.to)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Stmt.Import.NamedSelector = r.getOrElse(o)
    val postR: MOption[Stmt.Import.NamedSelector] = postStmtImportNamedSelector(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformStmtLoop(o: Stmt.Loop): MOption[Stmt.Loop] = {
    val preR: PreResult[Stmt.Loop] = preStmtLoop(o)
    val r: MOption[Stmt.Loop] = if (preR.continu) {
      val o2: Stmt.Loop = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[Stmt.Loop] = o2 match {
        case o2: Stmt.While =>
          val r0: MOption[Exp] = transformExp(o2.cond)
          val r1: MOption[IS[Z, Exp]] = transformISZ(o2.invariants, transformExp _)
          val r2: MOption[IS[Z, Exp.Ident]] = transformISZ(o2.modifies, transformExpIdent _)
          val r3: MOption[Body] = transformBody(o2.body)
          val r4: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty || r4.nonEmpty)
            MSome(o2(cond = r0.getOrElse(o2.cond), invariants = r1.getOrElse(o2.invariants), modifies = r2.getOrElse(o2.modifies), body = r3.getOrElse(o2.body), attr = r4.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.DoWhile =>
          val r0: MOption[Exp] = transformExp(o2.cond)
          val r1: MOption[IS[Z, Exp]] = transformISZ(o2.invariants, transformExp _)
          val r2: MOption[IS[Z, Exp.Ident]] = transformISZ(o2.modifies, transformExpIdent _)
          val r3: MOption[Body] = transformBody(o2.body)
          val r4: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty || r4.nonEmpty)
            MSome(o2(cond = r0.getOrElse(o2.cond), invariants = r1.getOrElse(o2.invariants), modifies = r2.getOrElse(o2.modifies), body = r3.getOrElse(o2.body), attr = r4.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.For =>
          val r0: MOption[IS[Z, EnumGen.For]] = transformISZ(o2.enumGens, transformEnumGenFor _)
          val r1: MOption[IS[Z, Exp]] = transformISZ(o2.invariants, transformExp _)
          val r2: MOption[IS[Z, Exp.Ident]] = transformISZ(o2.modifies, transformExpIdent _)
          val r3: MOption[Body] = transformBody(o2.body)
          val r4: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty || r4.nonEmpty)
            MSome(o2(enumGens = r0.getOrElse(o2.enumGens), invariants = r1.getOrElse(o2.invariants), modifies = r2.getOrElse(o2.modifies), body = r3.getOrElse(o2.body), attr = r4.getOrElse(o2.attr)))
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Stmt.Loop = r.getOrElse(o)
    val postR: MOption[Stmt.Loop] = postStmtLoop(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformStmtSpec(o: Stmt.Spec): MOption[Stmt.Spec] = {
    val preR: PreResult[Stmt.Spec] = preStmtSpec(o)
    val r: MOption[Stmt.Spec] = if (preR.continu) {
      val o2: Stmt.Spec = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[Stmt.Spec] = o2 match {
        case o2: Stmt.Fact =>
          val r0: MOption[Id] = transformId(o2.id)
          val r1: MOption[IS[Z, TypeParam]] = transformISZ(o2.typeParams, transformTypeParam _)
          val r2: MOption[Option[Exp.LitString]] = transformOption(o2.descOpt, transformExpLitString _)
          val r3: MOption[IS[Z, Exp]] = transformISZ(o2.claims, transformExp _)
          val r4: MOption[ResolvedAttr] = transformResolvedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty || r4.nonEmpty)
            MSome(o2(id = r0.getOrElse(o2.id), typeParams = r1.getOrElse(o2.typeParams), descOpt = r2.getOrElse(o2.descOpt), claims = r3.getOrElse(o2.claims), attr = r4.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.Inv =>
          val r0: MOption[Id] = transformId(o2.id)
          val r1: MOption[IS[Z, Exp]] = transformISZ(o2.claims, transformExp _)
          val r2: MOption[ResolvedAttr] = transformResolvedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(id = r0.getOrElse(o2.id), claims = r1.getOrElse(o2.claims), attr = r2.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.Theorem =>
          val r0: MOption[Id] = transformId(o2.id)
          val r1: MOption[IS[Z, TypeParam]] = transformISZ(o2.typeParams, transformTypeParam _)
          val r2: MOption[Option[Exp.LitString]] = transformOption(o2.descOpt, transformExpLitString _)
          val r3: MOption[Exp] = transformExp(o2.claim)
          val r4: MOption[Proof] = transformProof(o2.proof)
          val r5: MOption[ResolvedAttr] = transformResolvedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty || r4.nonEmpty || r5.nonEmpty)
            MSome(o2(id = r0.getOrElse(o2.id), typeParams = r1.getOrElse(o2.typeParams), descOpt = r2.getOrElse(o2.descOpt), claim = r3.getOrElse(o2.claim), proof = r4.getOrElse(o2.proof), attr = r5.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.DataRefinement =>
          val r0: MOption[Exp.Ident] = transformExpIdent(o2.rep)
          val r1: MOption[IS[Z, Exp.Ident]] = transformISZ(o2.refs, transformExpIdent _)
          val r2: MOption[IS[Z, Exp]] = transformISZ(o2.claims, transformExp _)
          val r3: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty)
            MSome(o2(rep = r0.getOrElse(o2.rep), refs = r1.getOrElse(o2.refs), claims = r2.getOrElse(o2.claims), attr = r3.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.SpecLabel =>
          val r0: MOption[Id] = transformId(o2.id)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(id = r0.getOrElse(o2.id)))
          else
            MNone()
        case o2: Stmt.SpecBlock =>
          val r0: MOption[Stmt.Block] = transformStmtBlock(o2.block)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(block = r0.getOrElse(o2.block)))
          else
            MNone()
        case o2: Stmt.DeduceSequent =>
          val r0: MOption[Option[Exp.LitString]] = transformOption(o2.justOpt, transformExpLitString _)
          val r1: MOption[IS[Z, Sequent]] = transformISZ(o2.sequents, transformSequent _)
          val r2: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(justOpt = r0.getOrElse(o2.justOpt), sequents = r1.getOrElse(o2.sequents), attr = r2.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.DeduceSteps =>
          val r0: MOption[IS[Z, Proof.Step]] = transformISZ(o2.steps, transformProofStep _)
          val r1: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(steps = r0.getOrElse(o2.steps), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.Havoc =>
          val r0: MOption[IS[Z, Exp.Ident]] = transformISZ(o2.args, transformExpIdent _)
          val r1: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(args = r0.getOrElse(o2.args), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Stmt.Spec = r.getOrElse(o)
    val postR: MOption[Stmt.Spec] = postStmtSpec(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformMethodContract(o: MethodContract): MOption[MethodContract] = {
    val preR: PreResult[MethodContract] = preMethodContract(o)
    val r: MOption[MethodContract] = if (preR.continu) {
      val o2: MethodContract = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[MethodContract] = o2 match {
        case o2: MethodContract.Simple =>
          val r0: MOption[IS[Z, Exp.Ident]] = transformISZ(o2.reads, transformExpIdent _)
          val r1: MOption[IS[Z, Exp]] = transformISZ(o2.requires, transformExp _)
          val r2: MOption[IS[Z, Exp.Ident]] = transformISZ(o2.modifies, transformExpIdent _)
          val r3: MOption[IS[Z, Exp]] = transformISZ(o2.ensures, transformExp _)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty)
            MSome(o2(reads = r0.getOrElse(o2.reads), requires = r1.getOrElse(o2.requires), modifies = r2.getOrElse(o2.modifies), ensures = r3.getOrElse(o2.ensures)))
          else
            MNone()
        case o2: MethodContract.Cases =>
          val r0: MOption[IS[Z, Exp.Ident]] = transformISZ(o2.reads, transformExpIdent _)
          val r1: MOption[IS[Z, Exp.Ident]] = transformISZ(o2.modifies, transformExpIdent _)
          val r2: MOption[IS[Z, MethodContract.Case]] = transformISZ(o2.cases, transformMethodContractCase _)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(reads = r0.getOrElse(o2.reads), modifies = r1.getOrElse(o2.modifies), cases = r2.getOrElse(o2.cases)))
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: MethodContract = r.getOrElse(o)
    val postR: MOption[MethodContract] = postMethodContract(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformMethodContractCase(o: MethodContract.Case): MOption[MethodContract.Case] = {
    val preR: PreResult[MethodContract.Case] = preMethodContractCase(o)
    val r: MOption[MethodContract.Case] = if (preR.continu) {
      val o2: MethodContract.Case = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Exp.LitString] = transformExpLitString(o2.label)
      val r1: MOption[IS[Z, Exp]] = transformISZ(o2.requires, transformExp _)
      val r2: MOption[IS[Z, Exp]] = transformISZ(o2.ensures, transformExp _)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
        MSome(o2(label = r0.getOrElse(o2.label), requires = r1.getOrElse(o2.requires), ensures = r2.getOrElse(o2.ensures)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: MethodContract.Case = r.getOrElse(o)
    val postR: MOption[MethodContract.Case] = postMethodContractCase(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformSequent(o: Sequent): MOption[Sequent] = {
    val preR: PreResult[Sequent] = preSequent(o)
    val r: MOption[Sequent] = if (preR.continu) {
      val o2: Sequent = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[IS[Z, Exp]] = transformISZ(o2.premises, transformExp _)
      val r1: MOption[Exp] = transformExp(o2.conclusion)
      val r2: MOption[IS[Z, Proof.Step]] = transformISZ(o2.steps, transformProofStep _)
      val r3: MOption[Attr] = transformAttr(o2.attr)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty)
        MSome(o2(premises = r0.getOrElse(o2.premises), conclusion = r1.getOrElse(o2.conclusion), steps = r2.getOrElse(o2.steps), attr = r3.getOrElse(o2.attr)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Sequent = r.getOrElse(o)
    val postR: MOption[Sequent] = postSequent(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformProof(o: Proof): MOption[Proof] = {
    val preR: PreResult[Proof] = preProof(o)
    val r: MOption[Proof] = if (preR.continu) {
      val o2: Proof = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[IS[Z, Proof.Step]] = transformISZ(o2.steps, transformProofStep _)
      val r1: MOption[Attr] = transformAttr(o2.attr)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty)
        MSome(o2(steps = r0.getOrElse(o2.steps), attr = r1.getOrElse(o2.attr)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Proof = r.getOrElse(o)
    val postR: MOption[Proof] = postProof(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformProofStep(o: Proof.Step): MOption[Proof.Step] = {
    val preR: PreResult[Proof.Step] = preProofStep(o)
    val r: MOption[Proof.Step] = if (preR.continu) {
      val o2: Proof.Step = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[Proof.Step] = o2 match {
        case o2: Proof.Step.Regular =>
          val r0: MOption[Exp.LitZ] = transformExpLitZ(o2.no)
          val r1: MOption[Exp] = transformExp(o2.claim)
          val r2: MOption[Proof.Step.Justification] = transformProofStepJustification(o2.just)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(no = r0.getOrElse(o2.no), claim = r1.getOrElse(o2.claim), just = r2.getOrElse(o2.just)))
          else
            MNone()
        case o2: Proof.Step.Assume =>
          val r0: MOption[Exp.LitZ] = transformExpLitZ(o2.no)
          val r1: MOption[Exp] = transformExp(o2.claim)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(no = r0.getOrElse(o2.no), claim = r1.getOrElse(o2.claim)))
          else
            MNone()
        case o2: Proof.Step.Assert =>
          val r0: MOption[Exp.LitZ] = transformExpLitZ(o2.no)
          val r1: MOption[Exp] = transformExp(o2.claim)
          val r2: MOption[IS[Z, Proof.Step]] = transformISZ(o2.steps, transformProofStep _)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(no = r0.getOrElse(o2.no), claim = r1.getOrElse(o2.claim), steps = r2.getOrElse(o2.steps)))
          else
            MNone()
        case o2: Proof.Step.SubProof =>
          val r0: MOption[Exp.LitZ] = transformExpLitZ(o2.no)
          val r1: MOption[IS[Z, Proof.Step]] = transformISZ(o2.steps, transformProofStep _)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(no = r0.getOrElse(o2.no), steps = r1.getOrElse(o2.steps)))
          else
            MNone()
        case o2: Proof.Step.Let =>
          val r0: MOption[Exp.LitZ] = transformExpLitZ(o2.no)
          val r1: MOption[IS[Z, Proof.Step.Let.Param]] = transformISZ(o2.params, transformProofStepLetParam _)
          val r2: MOption[IS[Z, Proof.Step]] = transformISZ(o2.steps, transformProofStep _)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(no = r0.getOrElse(o2.no), params = r1.getOrElse(o2.params), steps = r2.getOrElse(o2.steps)))
          else
            MNone()
        case o2: Proof.Step.StructInduction =>
          val r0: MOption[Exp.LitZ] = transformExpLitZ(o2.no)
          val r1: MOption[Exp] = transformExp(o2.claim)
          val r2: MOption[Exp] = transformExp(o2.exp)
          val r3: MOption[IS[Z, Proof.Step.StructInduction.MatchCase]] = transformISZ(o2.cases, transformProofStepStructInductionMatchCase _)
          val r4: MOption[Option[Proof.Step.StructInduction.MatchDefault]] = transformOption(o2.defaultOpt, transformProofStepStructInductionMatchDefault _)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty || r4.nonEmpty)
            MSome(o2(no = r0.getOrElse(o2.no), claim = r1.getOrElse(o2.claim), exp = r2.getOrElse(o2.exp), cases = r3.getOrElse(o2.cases), defaultOpt = r4.getOrElse(o2.defaultOpt)))
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Proof.Step = r.getOrElse(o)
    val postR: MOption[Proof.Step] = postProofStep(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformProofStepLetParam(o: Proof.Step.Let.Param): MOption[Proof.Step.Let.Param] = {
    val preR: PreResult[Proof.Step.Let.Param] = preProofStepLetParam(o)
    val r: MOption[Proof.Step.Let.Param] = if (preR.continu) {
      val o2: Proof.Step.Let.Param = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Id] = transformId(o2.id)
      val r1: MOption[Option[Type]] = transformOption(o2.tipeOpt, transformType _)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty)
        MSome(o2(id = r0.getOrElse(o2.id), tipeOpt = r1.getOrElse(o2.tipeOpt)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Proof.Step.Let.Param = r.getOrElse(o)
    val postR: MOption[Proof.Step.Let.Param] = postProofStepLetParam(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformProofStepStructInductionMatchCase(o: Proof.Step.StructInduction.MatchCase): MOption[Proof.Step.StructInduction.MatchCase] = {
    val preR: PreResult[Proof.Step.StructInduction.MatchCase] = preProofStepStructInductionMatchCase(o)
    val r: MOption[Proof.Step.StructInduction.MatchCase] = if (preR.continu) {
      val o2: Proof.Step.StructInduction.MatchCase = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Pattern.Structure] = transformPatternStructure(o2.pattern)
      val r1: MOption[Option[Proof.Step.Assume]] = transformOption(o2.hypoOpt, transformProofStepAssume _)
      val r2: MOption[IS[Z, Proof.Step]] = transformISZ(o2.steps, transformProofStep _)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
        MSome(o2(pattern = r0.getOrElse(o2.pattern), hypoOpt = r1.getOrElse(o2.hypoOpt), steps = r2.getOrElse(o2.steps)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Proof.Step.StructInduction.MatchCase = r.getOrElse(o)
    val postR: MOption[Proof.Step.StructInduction.MatchCase] = postProofStepStructInductionMatchCase(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformProofStepStructInductionMatchDefault(o: Proof.Step.StructInduction.MatchDefault): MOption[Proof.Step.StructInduction.MatchDefault] = {
    val preR: PreResult[Proof.Step.StructInduction.MatchDefault] = preProofStepStructInductionMatchDefault(o)
    val r: MOption[Proof.Step.StructInduction.MatchDefault] = if (preR.continu) {
      val o2: Proof.Step.StructInduction.MatchDefault = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Option[Proof.Step.Assume]] = transformOption(o2.hypoOpt, transformProofStepAssume _)
      val r1: MOption[IS[Z, Proof.Step]] = transformISZ(o2.steps, transformProofStep _)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty)
        MSome(o2(hypoOpt = r0.getOrElse(o2.hypoOpt), steps = r1.getOrElse(o2.steps)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Proof.Step.StructInduction.MatchDefault = r.getOrElse(o)
    val postR: MOption[Proof.Step.StructInduction.MatchDefault] = postProofStepStructInductionMatchDefault(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformProofStepJustification(o: Proof.Step.Justification): MOption[Proof.Step.Justification] = {
    val preR: PreResult[Proof.Step.Justification] = preProofStepJustification(o)
    val r: MOption[Proof.Step.Justification] = if (preR.continu) {
      val o2: Proof.Step.Justification = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Id] = transformId(o2.id)
      val r1: MOption[IS[Z, Exp]] = transformISZ(o2.args, transformExp _)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty)
        MSome(o2(id = r0.getOrElse(o2.id), args = r1.getOrElse(o2.args)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Proof.Step.Justification = r.getOrElse(o)
    val postR: MOption[Proof.Step.Justification] = postProofStepJustification(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformAssignExp(o: AssignExp): MOption[AssignExp] = {
    val preR: PreResult[AssignExp] = preAssignExp(o)
    val r: MOption[AssignExp] = if (preR.continu) {
      val o2: AssignExp = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[AssignExp] = o2 match {
        case o2: Stmt.Block =>
          val r0: MOption[Body] = transformBody(o2.body)
          val r1: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(body = r0.getOrElse(o2.body), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.If =>
          val r0: MOption[Exp] = transformExp(o2.cond)
          val r1: MOption[Body] = transformBody(o2.thenBody)
          val r2: MOption[Body] = transformBody(o2.elseBody)
          val r3: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty)
            MSome(o2(cond = r0.getOrElse(o2.cond), thenBody = r1.getOrElse(o2.thenBody), elseBody = r2.getOrElse(o2.elseBody), attr = r3.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.Match =>
          val r0: MOption[Exp] = transformExp(o2.exp)
          val r1: MOption[IS[Z, Case]] = transformISZ(o2.cases, transformCase _)
          val r2: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(exp = r0.getOrElse(o2.exp), cases = r1.getOrElse(o2.cases), attr = r2.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.Return =>
          val r0: MOption[Option[Exp]] = transformOption(o2.expOpt, transformExp _)
          val r1: MOption[TypedAttr] = transformTypedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(expOpt = r0.getOrElse(o2.expOpt), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Stmt.Expr =>
          val r0: MOption[Exp] = transformExp(o2.exp)
          val r1: MOption[TypedAttr] = transformTypedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(exp = r0.getOrElse(o2.exp), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: AssignExp = r.getOrElse(o)
    val postR: MOption[AssignExp] = postAssignExp(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformCase(o: Case): MOption[Case] = {
    val preR: PreResult[Case] = preCase(o)
    val r: MOption[Case] = if (preR.continu) {
      val o2: Case = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Pattern] = transformPattern(o2.pattern)
      val r1: MOption[Option[Exp]] = transformOption(o2.condOpt, transformExp _)
      val r2: MOption[Body] = transformBody(o2.body)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
        MSome(o2(pattern = r0.getOrElse(o2.pattern), condOpt = r1.getOrElse(o2.condOpt), body = r2.getOrElse(o2.body)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Case = r.getOrElse(o)
    val postR: MOption[Case] = postCase(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformEnumGenRange(o: EnumGen.Range): MOption[EnumGen.Range] = {
    val preR: PreResult[EnumGen.Range] = preEnumGenRange(o)
    val r: MOption[EnumGen.Range] = if (preR.continu) {
      val o2: EnumGen.Range = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[EnumGen.Range] = o2 match {
        case o2: EnumGen.Range.Expr =>
          val r0: MOption[Exp] = transformExp(o2.exp)
          val r1: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(exp = r0.getOrElse(o2.exp), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
        case o2: EnumGen.Range.Step =>
          val r0: MOption[Exp] = transformExp(o2.start)
          val r1: MOption[Exp] = transformExp(o2.end)
          val r2: MOption[Option[Exp]] = transformOption(o2.byOpt, transformExp _)
          val r3: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty)
            MSome(o2(start = r0.getOrElse(o2.start), end = r1.getOrElse(o2.end), byOpt = r2.getOrElse(o2.byOpt), attr = r3.getOrElse(o2.attr)))
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: EnumGen.Range = r.getOrElse(o)
    val postR: MOption[EnumGen.Range] = postEnumGenRange(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformEnumGenFor(o: EnumGen.For): MOption[EnumGen.For] = {
    val preR: PreResult[EnumGen.For] = preEnumGenFor(o)
    val r: MOption[EnumGen.For] = if (preR.continu) {
      val o2: EnumGen.For = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Option[Id]] = transformOption(o2.idOpt, transformId _)
      val r1: MOption[EnumGen.Range] = transformEnumGenRange(o2.range)
      val r2: MOption[Option[Exp]] = transformOption(o2.condOpt, transformExp _)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
        MSome(o2(idOpt = r0.getOrElse(o2.idOpt), range = r1.getOrElse(o2.range), condOpt = r2.getOrElse(o2.condOpt)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: EnumGen.For = r.getOrElse(o)
    val postR: MOption[EnumGen.For] = postEnumGenFor(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformType(o: Type): MOption[Type] = {
    val preR: PreResult[Type] = preType(o)
    val r: MOption[Type] = if (preR.continu) {
      val o2: Type = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[Type] = o2 match {
        case o2: Type.Named =>
          val r0: MOption[Name] = transformName(o2.name)
          val r1: MOption[IS[Z, Type]] = transformISZ(o2.typeArgs, transformType _)
          val r2: MOption[TypedAttr] = transformTypedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(name = r0.getOrElse(o2.name), typeArgs = r1.getOrElse(o2.typeArgs), attr = r2.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Type.Fun =>
          val r0: MOption[IS[Z, Type]] = transformISZ(o2.args, transformType _)
          val r1: MOption[Type] = transformType(o2.ret)
          val r2: MOption[TypedAttr] = transformTypedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(args = r0.getOrElse(o2.args), ret = r1.getOrElse(o2.ret), attr = r2.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Type.Tuple =>
          val r0: MOption[IS[Z, Type]] = transformISZ(o2.args, transformType _)
          val r1: MOption[TypedAttr] = transformTypedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(args = r0.getOrElse(o2.args), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Type = r.getOrElse(o)
    val postR: MOption[Type] = postType(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformPattern(o: Pattern): MOption[Pattern] = {
    val preR: PreResult[Pattern] = prePattern(o)
    val r: MOption[Pattern] = if (preR.continu) {
      val o2: Pattern = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[Pattern] = o2 match {
        case o2: Pattern.Literal =>
          val r0: MOption[Lit] = transformLit(o2.lit)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(lit = r0.getOrElse(o2.lit)))
          else
            MNone()
        case o2: Pattern.LitInterpolate =>
          val r0: MOption[TypedAttr] = transformTypedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(attr = r0.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Pattern.Ref =>
          val r0: MOption[Name] = transformName(o2.name)
          val r1: MOption[ResolvedAttr] = transformResolvedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(name = r0.getOrElse(o2.name), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Pattern.VarBinding =>
          val r0: MOption[Id] = transformId(o2.id)
          val r1: MOption[Option[Type]] = transformOption(o2.tipeOpt, transformType _)
          val r2: MOption[TypedAttr] = transformTypedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(id = r0.getOrElse(o2.id), tipeOpt = r1.getOrElse(o2.tipeOpt), attr = r2.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Pattern.Wildcard =>
          val r0: MOption[Option[Type]] = transformOption(o2.typeOpt, transformType _)
          val r1: MOption[TypedAttr] = transformTypedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(typeOpt = r0.getOrElse(o2.typeOpt), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Pattern.SeqWildcard =>
          val r0: MOption[TypedAttr] = transformTypedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(attr = r0.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Pattern.Structure =>
          val r0: MOption[Option[Id]] = transformOption(o2.idOpt, transformId _)
          val r1: MOption[Option[Name]] = transformOption(o2.nameOpt, transformName _)
          val r2: MOption[IS[Z, Pattern]] = transformISZ(o2.patterns, transformPattern _)
          val r3: MOption[ResolvedAttr] = transformResolvedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty)
            MSome(o2(idOpt = r0.getOrElse(o2.idOpt), nameOpt = r1.getOrElse(o2.nameOpt), patterns = r2.getOrElse(o2.patterns), attr = r3.getOrElse(o2.attr)))
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Pattern = r.getOrElse(o)
    val postR: MOption[Pattern] = postPattern(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformExp(o: Exp): MOption[Exp] = {
    val preR: PreResult[Exp] = preExp(o)
    val r: MOption[Exp] = if (preR.continu) {
      val o2: Exp = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[Exp] = o2 match {
        case o2: Exp.LitB =>
          val r0: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(attr = r0.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.LitC =>
          val r0: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(attr = r0.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.LitZ =>
          val r0: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(attr = r0.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.LitF32 =>
          val r0: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(attr = r0.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.LitF64 =>
          val r0: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(attr = r0.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.LitR =>
          val r0: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(attr = r0.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.LitString =>
          val r0: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(attr = r0.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.StringInterpolate =>
          val r0: MOption[IS[Z, Exp.LitString]] = transformISZ(o2.lits, transformExpLitString _)
          val r1: MOption[IS[Z, Exp]] = transformISZ(o2.args, transformExp _)
          val r2: MOption[TypedAttr] = transformTypedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(lits = r0.getOrElse(o2.lits), args = r1.getOrElse(o2.args), attr = r2.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.This =>
          val r0: MOption[TypedAttr] = transformTypedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(attr = r0.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.Super =>
          val r0: MOption[Option[Id]] = transformOption(o2.idOpt, transformId _)
          val r1: MOption[TypedAttr] = transformTypedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(idOpt = r0.getOrElse(o2.idOpt), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.Unary =>
          val r0: MOption[Exp] = transformExp(o2.exp)
          val r1: MOption[ResolvedAttr] = transformResolvedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(exp = r0.getOrElse(o2.exp), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.Binary =>
          val r0: MOption[Exp] = transformExp(o2.left)
          val r1: MOption[Exp] = transformExp(o2.right)
          val r2: MOption[ResolvedAttr] = transformResolvedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(left = r0.getOrElse(o2.left), right = r1.getOrElse(o2.right), attr = r2.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.Ident =>
          val r0: MOption[Id] = transformId(o2.id)
          val r1: MOption[ResolvedAttr] = transformResolvedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(id = r0.getOrElse(o2.id), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.Eta =>
          val r0: MOption[Exp.Ref] = transformExpRef(o2.ref)
          val r1: MOption[TypedAttr] = transformTypedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(ref = r0.getOrElse(o2.ref), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.Tuple =>
          val r0: MOption[IS[Z, Exp]] = transformISZ(o2.args, transformExp _)
          val r1: MOption[TypedAttr] = transformTypedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(args = r0.getOrElse(o2.args), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.Select =>
          val r0: MOption[Option[Exp]] = transformOption(o2.receiverOpt, transformExp _)
          val r1: MOption[Id] = transformId(o2.id)
          val r2: MOption[IS[Z, Type]] = transformISZ(o2.targs, transformType _)
          val r3: MOption[ResolvedAttr] = transformResolvedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty)
            MSome(o2(receiverOpt = r0.getOrElse(o2.receiverOpt), id = r1.getOrElse(o2.id), targs = r2.getOrElse(o2.targs), attr = r3.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.Invoke =>
          val r0: MOption[Option[Exp]] = transformOption(o2.receiverOpt, transformExp _)
          val r1: MOption[Exp.Ident] = transformExpIdent(o2.ident)
          val r2: MOption[IS[Z, Type]] = transformISZ(o2.targs, transformType _)
          val r3: MOption[IS[Z, Exp]] = transformISZ(o2.args, transformExp _)
          val r4: MOption[ResolvedAttr] = transformResolvedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty || r4.nonEmpty)
            MSome(o2(receiverOpt = r0.getOrElse(o2.receiverOpt), ident = r1.getOrElse(o2.ident), targs = r2.getOrElse(o2.targs), args = r3.getOrElse(o2.args), attr = r4.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.InvokeNamed =>
          val r0: MOption[Option[Exp]] = transformOption(o2.receiverOpt, transformExp _)
          val r1: MOption[Exp.Ident] = transformExpIdent(o2.ident)
          val r2: MOption[IS[Z, Type]] = transformISZ(o2.targs, transformType _)
          val r3: MOption[IS[Z, NamedArg]] = transformISZ(o2.args, transformNamedArg _)
          val r4: MOption[ResolvedAttr] = transformResolvedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty || r4.nonEmpty)
            MSome(o2(receiverOpt = r0.getOrElse(o2.receiverOpt), ident = r1.getOrElse(o2.ident), targs = r2.getOrElse(o2.targs), args = r3.getOrElse(o2.args), attr = r4.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.If =>
          val r0: MOption[Exp] = transformExp(o2.cond)
          val r1: MOption[Exp] = transformExp(o2.thenExp)
          val r2: MOption[Exp] = transformExp(o2.elseExp)
          val r3: MOption[TypedAttr] = transformTypedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty)
            MSome(o2(cond = r0.getOrElse(o2.cond), thenExp = r1.getOrElse(o2.thenExp), elseExp = r2.getOrElse(o2.elseExp), attr = r3.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.Fun =>
          val r0: MOption[IS[Z, Exp.Fun.Param]] = transformISZ(o2.params, transformExpFunParam _)
          val r1: MOption[AssignExp] = transformAssignExp(o2.exp)
          val r2: MOption[TypedAttr] = transformTypedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(params = r0.getOrElse(o2.params), exp = r1.getOrElse(o2.exp), attr = r2.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.ForYield =>
          val r0: MOption[IS[Z, EnumGen.For]] = transformISZ(o2.enumGens, transformEnumGenFor _)
          val r1: MOption[Exp] = transformExp(o2.exp)
          val r2: MOption[TypedAttr] = transformTypedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(enumGens = r0.getOrElse(o2.enumGens), exp = r1.getOrElse(o2.exp), attr = r2.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.QuantType =>
          val r0: MOption[Exp.Fun] = transformExpFun(o2.fun)
          val r1: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(fun = r0.getOrElse(o2.fun), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.QuantRange =>
          val r0: MOption[Exp] = transformExp(o2.lo)
          val r1: MOption[Exp] = transformExp(o2.hi)
          val r2: MOption[Exp.Fun] = transformExpFun(o2.fun)
          val r3: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty)
            MSome(o2(lo = r0.getOrElse(o2.lo), hi = r1.getOrElse(o2.hi), fun = r2.getOrElse(o2.fun), attr = r3.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.QuantEach =>
          val r0: MOption[Exp] = transformExp(o2.seq)
          val r1: MOption[Exp.Fun] = transformExpFun(o2.fun)
          val r2: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(seq = r0.getOrElse(o2.seq), fun = r1.getOrElse(o2.fun), attr = r2.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.Input =>
          val r0: MOption[Exp] = transformExp(o2.exp)
          val r1: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(exp = r0.getOrElse(o2.exp), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.OldVal =>
          val r0: MOption[Exp] = transformExp(o2.exp)
          val r1: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(exp = r0.getOrElse(o2.exp), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.AtLoc =>
          val r0: MOption[Option[Id]] = transformOption(o2.idOpt, transformId _)
          val r1: MOption[Exp] = transformExp(o2.exp)
          val r2: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(idOpt = r0.getOrElse(o2.idOpt), exp = r1.getOrElse(o2.exp), attr = r2.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.StateSeq =>
          val r0: MOption[Id] = transformId(o2.id)
          val r1: MOption[IS[Z, Exp.StateSeq.Fragment]] = transformISZ(o2.fragments, transformExpStateSeqFragment _)
          val r2: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(id = r0.getOrElse(o2.id), fragments = r1.getOrElse(o2.fragments), attr = r2.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.Result =>
          val r0: MOption[Option[Type]] = transformOption(o2.tipeOpt, transformType _)
          val r1: MOption[TypedAttr] = transformTypedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(tipeOpt = r0.getOrElse(o2.tipeOpt), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Exp = r.getOrElse(o)
    val postR: MOption[Exp] = postExp(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformLit(o: Lit): MOption[Lit] = {
    val preR: PreResult[Lit] = preLit(o)
    val r: MOption[Lit] = if (preR.continu) {
      val o2: Lit = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[Lit] = o2 match {
        case o2: Exp.LitB =>
          val r0: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(attr = r0.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.LitC =>
          val r0: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(attr = r0.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.LitZ =>
          val r0: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(attr = r0.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.LitF32 =>
          val r0: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(attr = r0.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.LitF64 =>
          val r0: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(attr = r0.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.LitR =>
          val r0: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(attr = r0.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.LitString =>
          val r0: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(attr = r0.getOrElse(o2.attr)))
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Lit = r.getOrElse(o)
    val postR: MOption[Lit] = postLit(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformExpRef(o: Exp.Ref): MOption[Exp.Ref] = {
    val preR: PreResult[Exp.Ref] = preExpRef(o)
    val r: MOption[Exp.Ref] = if (preR.continu) {
      val o2: Exp.Ref = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[Exp.Ref] = o2 match {
        case o2: Exp.Ident =>
          val r0: MOption[Id] = transformId(o2.id)
          val r1: MOption[ResolvedAttr] = transformResolvedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(id = r0.getOrElse(o2.id), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.Select =>
          val r0: MOption[Option[Exp]] = transformOption(o2.receiverOpt, transformExp _)
          val r1: MOption[Id] = transformId(o2.id)
          val r2: MOption[IS[Z, Type]] = transformISZ(o2.targs, transformType _)
          val r3: MOption[ResolvedAttr] = transformResolvedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty)
            MSome(o2(receiverOpt = r0.getOrElse(o2.receiverOpt), id = r1.getOrElse(o2.id), targs = r2.getOrElse(o2.targs), attr = r3.getOrElse(o2.attr)))
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Exp.Ref = r.getOrElse(o)
    val postR: MOption[Exp.Ref] = postExpRef(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformExpFunParam(o: Exp.Fun.Param): MOption[Exp.Fun.Param] = {
    val preR: PreResult[Exp.Fun.Param] = preExpFunParam(o)
    val r: MOption[Exp.Fun.Param] = if (preR.continu) {
      val o2: Exp.Fun.Param = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Option[Id]] = transformOption(o2.idOpt, transformId _)
      val r1: MOption[Option[Type]] = transformOption(o2.tipeOpt, transformType _)
      val r2: MOption[Option[Typed]] = transformOption(o2.typedOpt, transformTyped _)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
        MSome(o2(idOpt = r0.getOrElse(o2.idOpt), tipeOpt = r1.getOrElse(o2.tipeOpt), typedOpt = r2.getOrElse(o2.typedOpt)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Exp.Fun.Param = r.getOrElse(o)
    val postR: MOption[Exp.Fun.Param] = postExpFunParam(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformExpSpec(o: Exp.Spec): MOption[Exp.Spec] = {
    val preR: PreResult[Exp.Spec] = preExpSpec(o)
    val r: MOption[Exp.Spec] = if (preR.continu) {
      val o2: Exp.Spec = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[Exp.Spec] = o2 match {
        case o2: Exp.QuantType =>
          val r0: MOption[Exp.Fun] = transformExpFun(o2.fun)
          val r1: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(fun = r0.getOrElse(o2.fun), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.QuantRange =>
          val r0: MOption[Exp] = transformExp(o2.lo)
          val r1: MOption[Exp] = transformExp(o2.hi)
          val r2: MOption[Exp.Fun] = transformExpFun(o2.fun)
          val r3: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty)
            MSome(o2(lo = r0.getOrElse(o2.lo), hi = r1.getOrElse(o2.hi), fun = r2.getOrElse(o2.fun), attr = r3.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.QuantEach =>
          val r0: MOption[Exp] = transformExp(o2.seq)
          val r1: MOption[Exp.Fun] = transformExpFun(o2.fun)
          val r2: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(seq = r0.getOrElse(o2.seq), fun = r1.getOrElse(o2.fun), attr = r2.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.Input =>
          val r0: MOption[Exp] = transformExp(o2.exp)
          val r1: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(exp = r0.getOrElse(o2.exp), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.OldVal =>
          val r0: MOption[Exp] = transformExp(o2.exp)
          val r1: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(exp = r0.getOrElse(o2.exp), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.AtLoc =>
          val r0: MOption[Option[Id]] = transformOption(o2.idOpt, transformId _)
          val r1: MOption[Exp] = transformExp(o2.exp)
          val r2: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(idOpt = r0.getOrElse(o2.idOpt), exp = r1.getOrElse(o2.exp), attr = r2.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.StateSeq =>
          val r0: MOption[Id] = transformId(o2.id)
          val r1: MOption[IS[Z, Exp.StateSeq.Fragment]] = transformISZ(o2.fragments, transformExpStateSeqFragment _)
          val r2: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(id = r0.getOrElse(o2.id), fragments = r1.getOrElse(o2.fragments), attr = r2.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.Result =>
          val r0: MOption[Option[Type]] = transformOption(o2.tipeOpt, transformType _)
          val r1: MOption[TypedAttr] = transformTypedAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(tipeOpt = r0.getOrElse(o2.tipeOpt), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Exp.Spec = r.getOrElse(o)
    val postR: MOption[Exp.Spec] = postExpSpec(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformExpQuant(o: Exp.Quant): MOption[Exp.Quant] = {
    val preR: PreResult[Exp.Quant] = preExpQuant(o)
    val r: MOption[Exp.Quant] = if (preR.continu) {
      val o2: Exp.Quant = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[Exp.Quant] = o2 match {
        case o2: Exp.QuantType =>
          val r0: MOption[Exp.Fun] = transformExpFun(o2.fun)
          val r1: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(fun = r0.getOrElse(o2.fun), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.QuantRange =>
          val r0: MOption[Exp] = transformExp(o2.lo)
          val r1: MOption[Exp] = transformExp(o2.hi)
          val r2: MOption[Exp.Fun] = transformExpFun(o2.fun)
          val r3: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty)
            MSome(o2(lo = r0.getOrElse(o2.lo), hi = r1.getOrElse(o2.hi), fun = r2.getOrElse(o2.fun), attr = r3.getOrElse(o2.attr)))
          else
            MNone()
        case o2: Exp.QuantEach =>
          val r0: MOption[Exp] = transformExp(o2.seq)
          val r1: MOption[Exp.Fun] = transformExpFun(o2.fun)
          val r2: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(seq = r0.getOrElse(o2.seq), fun = r1.getOrElse(o2.fun), attr = r2.getOrElse(o2.attr)))
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Exp.Quant = r.getOrElse(o)
    val postR: MOption[Exp.Quant] = postExpQuant(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformExpStateSeqFragment(o: Exp.StateSeq.Fragment): MOption[Exp.StateSeq.Fragment] = {
    val preR: PreResult[Exp.StateSeq.Fragment] = preExpStateSeqFragment(o)
    val r: MOption[Exp.StateSeq.Fragment] = if (preR.continu) {
      val o2: Exp.StateSeq.Fragment = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Id] = transformId(o2.id)
      val r1: MOption[Exp] = transformExp(o2.exp)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty)
        MSome(o2(id = r0.getOrElse(o2.id), exp = r1.getOrElse(o2.exp)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Exp.StateSeq.Fragment = r.getOrElse(o)
    val postR: MOption[Exp.StateSeq.Fragment] = postExpStateSeqFragment(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformNamedArg(o: NamedArg): MOption[NamedArg] = {
    val preR: PreResult[NamedArg] = preNamedArg(o)
    val r: MOption[NamedArg] = if (preR.continu) {
      val o2: NamedArg = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Id] = transformId(o2.id)
      val r1: MOption[Exp] = transformExp(o2.arg)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty)
        MSome(o2(id = r0.getOrElse(o2.id), arg = r1.getOrElse(o2.arg)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: NamedArg = r.getOrElse(o)
    val postR: MOption[NamedArg] = postNamedArg(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformId(o: Id): MOption[Id] = {
    val preR: PreResult[Id] = preId(o)
    val r: MOption[Id] = if (preR.continu) {
      val o2: Id = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Attr] = transformAttr(o2.attr)
      if (hasChanged || r0.nonEmpty)
        MSome(o2(attr = r0.getOrElse(o2.attr)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Id = r.getOrElse(o)
    val postR: MOption[Id] = postId(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformName(o: Name): MOption[Name] = {
    val preR: PreResult[Name] = preName(o)
    val r: MOption[Name] = if (preR.continu) {
      val o2: Name = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[IS[Z, Id]] = transformISZ(o2.ids, transformId _)
      val r1: MOption[Attr] = transformAttr(o2.attr)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty)
        MSome(o2(ids = r0.getOrElse(o2.ids), attr = r1.getOrElse(o2.attr)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Name = r.getOrElse(o)
    val postR: MOption[Name] = postName(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformBody(o: Body): MOption[Body] = {
    val preR: PreResult[Body] = preBody(o)
    val r: MOption[Body] = if (preR.continu) {
      val o2: Body = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[IS[Z, Stmt]] = transformISZ(o2.stmts, transformStmt _)
      val r1: MOption[IS[Z, ResolvedInfo.LocalVar]] = transformISZ(o2.undecls, transformResolvedInfoLocalVar _)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty)
        MSome(o2(stmts = r0.getOrElse(o2.stmts), undecls = r1.getOrElse(o2.undecls)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Body = r.getOrElse(o)
    val postR: MOption[Body] = postBody(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformAdtParam(o: AdtParam): MOption[AdtParam] = {
    val preR: PreResult[AdtParam] = preAdtParam(o)
    val r: MOption[AdtParam] = if (preR.continu) {
      val o2: AdtParam = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Id] = transformId(o2.id)
      val r1: MOption[Type] = transformType(o2.tipe)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty)
        MSome(o2(id = r0.getOrElse(o2.id), tipe = r1.getOrElse(o2.tipe)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: AdtParam = r.getOrElse(o)
    val postR: MOption[AdtParam] = postAdtParam(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformMethodSig(o: MethodSig): MOption[MethodSig] = {
    val preR: PreResult[MethodSig] = preMethodSig(o)
    val r: MOption[MethodSig] = if (preR.continu) {
      val o2: MethodSig = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Id] = transformId(o2.id)
      val r1: MOption[IS[Z, TypeParam]] = transformISZ(o2.typeParams, transformTypeParam _)
      val r2: MOption[IS[Z, Param]] = transformISZ(o2.params, transformParam _)
      val r3: MOption[Type] = transformType(o2.returnType)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty)
        MSome(o2(id = r0.getOrElse(o2.id), typeParams = r1.getOrElse(o2.typeParams), params = r2.getOrElse(o2.params), returnType = r3.getOrElse(o2.returnType)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: MethodSig = r.getOrElse(o)
    val postR: MOption[MethodSig] = postMethodSig(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformParam(o: Param): MOption[Param] = {
    val preR: PreResult[Param] = preParam(o)
    val r: MOption[Param] = if (preR.continu) {
      val o2: Param = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Id] = transformId(o2.id)
      val r1: MOption[Type] = transformType(o2.tipe)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty)
        MSome(o2(id = r0.getOrElse(o2.id), tipe = r1.getOrElse(o2.tipe)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Param = r.getOrElse(o)
    val postR: MOption[Param] = postParam(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformTypeParam(o: TypeParam): MOption[TypeParam] = {
    val preR: PreResult[TypeParam] = preTypeParam(o)
    val r: MOption[TypeParam] = if (preR.continu) {
      val o2: TypeParam = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Id] = transformId(o2.id)
      if (hasChanged || r0.nonEmpty)
        MSome(o2(id = r0.getOrElse(o2.id)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: TypeParam = r.getOrElse(o)
    val postR: MOption[TypeParam] = postTypeParam(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformTyped(o: Typed): MOption[Typed] = {
    val preR: PreResult[Typed] = preTyped(o)
    val r: MOption[Typed] = if (preR.continu) {
      val o2: Typed = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[Typed] = o2 match {
        case o2: Typed.Name =>
          val r0: MOption[IS[Z, Typed]] = transformISZ(o2.args, transformTyped _)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(args = r0.getOrElse(o2.args)))
          else
            MNone()
        case o2: Typed.Tuple =>
          val r0: MOption[IS[Z, Typed]] = transformISZ(o2.args, transformTyped _)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(args = r0.getOrElse(o2.args)))
          else
            MNone()
        case o2: Typed.Fun =>
          val r0: MOption[IS[Z, Typed]] = transformISZ(o2.args, transformTyped _)
          val r1: MOption[Typed] = transformTyped(o2.ret)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(args = r0.getOrElse(o2.args), ret = r1.getOrElse(o2.ret)))
          else
            MNone()
        case o2: Typed.TypeVar =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: Typed.Package =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: Typed.Object =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: Typed.Enum =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: Typed.Method =>
          val r0: MOption[Typed.Fun] = transformTypedFun(o2.tpe)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(tpe = r0.getOrElse(o2.tpe)))
          else
            MNone()
        case o2: Typed.Methods =>
          val r0: MOption[IS[Z, Typed.Method]] = transformISZ(o2.methods, transformTypedMethod _)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(methods = r0.getOrElse(o2.methods)))
          else
            MNone()
        case o2: Typed.Fact =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: Typed.Theorem =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: Typed.Inv =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Typed = r.getOrElse(o)
    val postR: MOption[Typed] = postTyped(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformAttr(o: Attr): MOption[Attr] = {
    val preR: PreResult[Attr] = preAttr(o)
    val r: MOption[Attr] = if (preR.continu) {
      val o2: Attr = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      if (hasChanged)
        MSome(o2)
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Attr = r.getOrElse(o)
    val postR: MOption[Attr] = postAttr(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformTypedAttr(o: TypedAttr): MOption[TypedAttr] = {
    val preR: PreResult[TypedAttr] = preTypedAttr(o)
    val r: MOption[TypedAttr] = if (preR.continu) {
      val o2: TypedAttr = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Option[Typed]] = transformOption(o2.typedOpt, transformTyped _)
      if (hasChanged || r0.nonEmpty)
        MSome(o2(typedOpt = r0.getOrElse(o2.typedOpt)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: TypedAttr = r.getOrElse(o)
    val postR: MOption[TypedAttr] = postTypedAttr(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformResolvedAttr(o: ResolvedAttr): MOption[ResolvedAttr] = {
    val preR: PreResult[ResolvedAttr] = preResolvedAttr(o)
    val r: MOption[ResolvedAttr] = if (preR.continu) {
      val o2: ResolvedAttr = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Option[ResolvedInfo]] = transformOption(o2.resOpt, transformResolvedInfo _)
      val r1: MOption[Option[Typed]] = transformOption(o2.typedOpt, transformTyped _)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty)
        MSome(o2(resOpt = r0.getOrElse(o2.resOpt), typedOpt = r1.getOrElse(o2.typedOpt)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: ResolvedAttr = r.getOrElse(o)
    val postR: MOption[ResolvedAttr] = postResolvedAttr(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformResolvedInfo(o: ResolvedInfo): MOption[ResolvedInfo] = {
    val preR: PreResult[ResolvedInfo] = preResolvedInfo(o)
    val r: MOption[ResolvedInfo] = if (preR.continu) {
      val o2: ResolvedInfo = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[ResolvedInfo] = o2 match {
        case o2: ResolvedInfo.BuiltIn =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: ResolvedInfo.Package =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: ResolvedInfo.Enum =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: ResolvedInfo.EnumElement =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: ResolvedInfo.Object =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: ResolvedInfo.Var =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: ResolvedInfo.Method =>
          val r0: MOption[Option[Typed.Fun]] = transformOption(o2.tpeOpt, transformTypedFun _)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(tpeOpt = r0.getOrElse(o2.tpeOpt)))
          else
            MNone()
        case o2: ResolvedInfo.Methods =>
          val r0: MOption[IS[Z, ResolvedInfo.Method]] = transformISZ(o2.methods, transformResolvedInfoMethod _)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(methods = r0.getOrElse(o2.methods)))
          else
            MNone()
        case o2: ResolvedInfo.Tuple =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: ResolvedInfo.LocalVar =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: ResolvedInfo.Fact =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: ResolvedInfo.Theorem =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: ResolvedInfo.Inv =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: ResolvedInfo = r.getOrElse(o)
    val postR: MOption[ResolvedInfo] = postResolvedInfo(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformTruthTableRow(o: TruthTable.Row): MOption[TruthTable.Row] = {
    val preR: PreResult[TruthTable.Row] = preTruthTableRow(o)
    val r: MOption[TruthTable.Row] = if (preR.continu) {
      val o2: TruthTable.Row = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[TruthTable.Assignment] = transformTruthTableAssignment(o2.assignment)
      val r1: MOption[TruthTable.Assignment] = transformTruthTableAssignment(o2.values)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty)
        MSome(o2(assignment = r0.getOrElse(o2.assignment), values = r1.getOrElse(o2.values)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: TruthTable.Row = r.getOrElse(o)
    val postR: MOption[TruthTable.Row] = postTruthTableRow(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformTruthTableAssignment(o: TruthTable.Assignment): MOption[TruthTable.Assignment] = {
    val preR: PreResult[TruthTable.Assignment] = preTruthTableAssignment(o)
    val r: MOption[TruthTable.Assignment] = if (preR.continu) {
      val o2: TruthTable.Assignment = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[IS[Z, Exp.LitB]] = transformISZ(o2.values, transformExpLitB _)
      val r1: MOption[Attr] = transformAttr(o2.attr)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty)
        MSome(o2(values = r0.getOrElse(o2.values), attr = r1.getOrElse(o2.attr)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: TruthTable.Assignment = r.getOrElse(o)
    val postR: MOption[TruthTable.Assignment] = postTruthTableAssignment(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformTruthTableConclusion(o: TruthTable.Conclusion): MOption[TruthTable.Conclusion] = {
    val preR: PreResult[TruthTable.Conclusion] = preTruthTableConclusion(o)
    val r: MOption[TruthTable.Conclusion] = if (preR.continu) {
      val o2: TruthTable.Conclusion = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[TruthTable.Conclusion] = o2 match {
        case o2: TruthTable.Conclusion.Validity =>
          val r0: MOption[IS[Z, TruthTable.Assignment]] = transformISZ(o2.assignments, transformTruthTableAssignment _)
          val r1: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(assignments = r0.getOrElse(o2.assignments), attr = r1.getOrElse(o2.attr)))
          else
            MNone()
        case o2: TruthTable.Conclusion.Tautology =>
          val r0: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(attr = r0.getOrElse(o2.attr)))
          else
            MNone()
        case o2: TruthTable.Conclusion.Contradictory =>
          val r0: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(attr = r0.getOrElse(o2.attr)))
          else
            MNone()
        case o2: TruthTable.Conclusion.Contingent =>
          val r0: MOption[IS[Z, TruthTable.Assignment]] = transformISZ(o2.trueAssignments, transformTruthTableAssignment _)
          val r1: MOption[IS[Z, TruthTable.Assignment]] = transformISZ(o2.falseAssignments, transformTruthTableAssignment _)
          val r2: MOption[Attr] = transformAttr(o2.attr)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(trueAssignments = r0.getOrElse(o2.trueAssignments), falseAssignments = r1.getOrElse(o2.falseAssignments), attr = r2.getOrElse(o2.attr)))
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: TruthTable.Conclusion = r.getOrElse(o)
    val postR: MOption[TruthTable.Conclusion] = postTruthTableConclusion(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformTypeNamed(o: Type.Named): MOption[Type.Named] = {
    val preR: PreResult[Type.Named] = preTypeNamed(o) match {
     case PreResult(continu, MSome(r: Type.Named)) => PreResult(continu, MSome[Type.Named](r))
     case PreResult(_, MSome(_)) => halt("Can only produce object of type Type.Named")
     case PreResult(continu, _) => PreResult(continu, MNone[Type.Named]())
    }
    val r: MOption[Type.Named] = if (preR.continu) {
      val o2: Type.Named = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Name] = transformName(o2.name)
      val r1: MOption[IS[Z, Type]] = transformISZ(o2.typeArgs, transformType _)
      val r2: MOption[TypedAttr] = transformTypedAttr(o2.attr)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
        MSome(o2(name = r0.getOrElse(o2.name), typeArgs = r1.getOrElse(o2.typeArgs), attr = r2.getOrElse(o2.attr)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Type.Named = r.getOrElse(o)
    val postR: MOption[Type.Named] = postTypeNamed(o2) match {
     case MSome(result: Type.Named) => MSome[Type.Named](result)
     case MSome(_) => halt("Can only produce object of type Type.Named")
     case _ => MNone[Type.Named]()
    }
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformExpIdent(o: Exp.Ident): MOption[Exp.Ident] = {
    val preR: PreResult[Exp.Ident] = preExpIdent(o) match {
     case PreResult(continu, MSome(r: Exp.Ident)) => PreResult(continu, MSome[Exp.Ident](r))
     case PreResult(_, MSome(_)) => halt("Can only produce object of type Exp.Ident")
     case PreResult(continu, _) => PreResult(continu, MNone[Exp.Ident]())
    }
    val r: MOption[Exp.Ident] = if (preR.continu) {
      val o2: Exp.Ident = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Id] = transformId(o2.id)
      val r1: MOption[ResolvedAttr] = transformResolvedAttr(o2.attr)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty)
        MSome(o2(id = r0.getOrElse(o2.id), attr = r1.getOrElse(o2.attr)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Exp.Ident = r.getOrElse(o)
    val postR: MOption[Exp.Ident] = postExpIdent(o2) match {
     case MSome(result: Exp.Ident) => MSome[Exp.Ident](result)
     case MSome(_) => halt("Can only produce object of type Exp.Ident")
     case _ => MNone[Exp.Ident]()
    }
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformExpLitString(o: Exp.LitString): MOption[Exp.LitString] = {
    val preR: PreResult[Exp.LitString] = preExpLitString(o) match {
     case PreResult(continu, MSome(r: Exp.LitString)) => PreResult(continu, MSome[Exp.LitString](r))
     case PreResult(_, MSome(_)) => halt("Can only produce object of type Exp.LitString")
     case PreResult(continu, _) => PreResult(continu, MNone[Exp.LitString]())
    }
    val r: MOption[Exp.LitString] = if (preR.continu) {
      val o2: Exp.LitString = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Attr] = transformAttr(o2.attr)
      if (hasChanged || r0.nonEmpty)
        MSome(o2(attr = r0.getOrElse(o2.attr)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Exp.LitString = r.getOrElse(o)
    val postR: MOption[Exp.LitString] = postExpLitString(o2) match {
     case MSome(result: Exp.LitString) => MSome[Exp.LitString](result)
     case MSome(_) => halt("Can only produce object of type Exp.LitString")
     case _ => MNone[Exp.LitString]()
    }
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformStmtBlock(o: Stmt.Block): MOption[Stmt.Block] = {
    val preR: PreResult[Stmt.Block] = preStmtBlock(o) match {
     case PreResult(continu, MSome(r: Stmt.Block)) => PreResult(continu, MSome[Stmt.Block](r))
     case PreResult(_, MSome(_)) => halt("Can only produce object of type Stmt.Block")
     case PreResult(continu, _) => PreResult(continu, MNone[Stmt.Block]())
    }
    val r: MOption[Stmt.Block] = if (preR.continu) {
      val o2: Stmt.Block = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Body] = transformBody(o2.body)
      val r1: MOption[Attr] = transformAttr(o2.attr)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty)
        MSome(o2(body = r0.getOrElse(o2.body), attr = r1.getOrElse(o2.attr)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Stmt.Block = r.getOrElse(o)
    val postR: MOption[Stmt.Block] = postStmtBlock(o2) match {
     case MSome(result: Stmt.Block) => MSome[Stmt.Block](result)
     case MSome(_) => halt("Can only produce object of type Stmt.Block")
     case _ => MNone[Stmt.Block]()
    }
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformExpLitZ(o: Exp.LitZ): MOption[Exp.LitZ] = {
    val preR: PreResult[Exp.LitZ] = preExpLitZ(o) match {
     case PreResult(continu, MSome(r: Exp.LitZ)) => PreResult(continu, MSome[Exp.LitZ](r))
     case PreResult(_, MSome(_)) => halt("Can only produce object of type Exp.LitZ")
     case PreResult(continu, _) => PreResult(continu, MNone[Exp.LitZ]())
    }
    val r: MOption[Exp.LitZ] = if (preR.continu) {
      val o2: Exp.LitZ = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Attr] = transformAttr(o2.attr)
      if (hasChanged || r0.nonEmpty)
        MSome(o2(attr = r0.getOrElse(o2.attr)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Exp.LitZ = r.getOrElse(o)
    val postR: MOption[Exp.LitZ] = postExpLitZ(o2) match {
     case MSome(result: Exp.LitZ) => MSome[Exp.LitZ](result)
     case MSome(_) => halt("Can only produce object of type Exp.LitZ")
     case _ => MNone[Exp.LitZ]()
    }
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformPatternStructure(o: Pattern.Structure): MOption[Pattern.Structure] = {
    val preR: PreResult[Pattern.Structure] = prePatternStructure(o) match {
     case PreResult(continu, MSome(r: Pattern.Structure)) => PreResult(continu, MSome[Pattern.Structure](r))
     case PreResult(_, MSome(_)) => halt("Can only produce object of type Pattern.Structure")
     case PreResult(continu, _) => PreResult(continu, MNone[Pattern.Structure]())
    }
    val r: MOption[Pattern.Structure] = if (preR.continu) {
      val o2: Pattern.Structure = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Option[Id]] = transformOption(o2.idOpt, transformId _)
      val r1: MOption[Option[Name]] = transformOption(o2.nameOpt, transformName _)
      val r2: MOption[IS[Z, Pattern]] = transformISZ(o2.patterns, transformPattern _)
      val r3: MOption[ResolvedAttr] = transformResolvedAttr(o2.attr)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty)
        MSome(o2(idOpt = r0.getOrElse(o2.idOpt), nameOpt = r1.getOrElse(o2.nameOpt), patterns = r2.getOrElse(o2.patterns), attr = r3.getOrElse(o2.attr)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Pattern.Structure = r.getOrElse(o)
    val postR: MOption[Pattern.Structure] = postPatternStructure(o2) match {
     case MSome(result: Pattern.Structure) => MSome[Pattern.Structure](result)
     case MSome(_) => halt("Can only produce object of type Pattern.Structure")
     case _ => MNone[Pattern.Structure]()
    }
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformProofStepAssume(o: Proof.Step.Assume): MOption[Proof.Step.Assume] = {
    val preR: PreResult[Proof.Step.Assume] = preProofStepAssume(o) match {
     case PreResult(continu, MSome(r: Proof.Step.Assume)) => PreResult(continu, MSome[Proof.Step.Assume](r))
     case PreResult(_, MSome(_)) => halt("Can only produce object of type Proof.Step.Assume")
     case PreResult(continu, _) => PreResult(continu, MNone[Proof.Step.Assume]())
    }
    val r: MOption[Proof.Step.Assume] = if (preR.continu) {
      val o2: Proof.Step.Assume = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Exp.LitZ] = transformExpLitZ(o2.no)
      val r1: MOption[Exp] = transformExp(o2.claim)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty)
        MSome(o2(no = r0.getOrElse(o2.no), claim = r1.getOrElse(o2.claim)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Proof.Step.Assume = r.getOrElse(o)
    val postR: MOption[Proof.Step.Assume] = postProofStepAssume(o2) match {
     case MSome(result: Proof.Step.Assume) => MSome[Proof.Step.Assume](result)
     case MSome(_) => halt("Can only produce object of type Proof.Step.Assume")
     case _ => MNone[Proof.Step.Assume]()
    }
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformExpFun(o: Exp.Fun): MOption[Exp.Fun] = {
    val preR: PreResult[Exp.Fun] = preExpFun(o) match {
     case PreResult(continu, MSome(r: Exp.Fun)) => PreResult(continu, MSome[Exp.Fun](r))
     case PreResult(_, MSome(_)) => halt("Can only produce object of type Exp.Fun")
     case PreResult(continu, _) => PreResult(continu, MNone[Exp.Fun]())
    }
    val r: MOption[Exp.Fun] = if (preR.continu) {
      val o2: Exp.Fun = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[IS[Z, Exp.Fun.Param]] = transformISZ(o2.params, transformExpFunParam _)
      val r1: MOption[AssignExp] = transformAssignExp(o2.exp)
      val r2: MOption[TypedAttr] = transformTypedAttr(o2.attr)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
        MSome(o2(params = r0.getOrElse(o2.params), exp = r1.getOrElse(o2.exp), attr = r2.getOrElse(o2.attr)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Exp.Fun = r.getOrElse(o)
    val postR: MOption[Exp.Fun] = postExpFun(o2) match {
     case MSome(result: Exp.Fun) => MSome[Exp.Fun](result)
     case MSome(_) => halt("Can only produce object of type Exp.Fun")
     case _ => MNone[Exp.Fun]()
    }
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformResolvedInfoLocalVar(o: ResolvedInfo.LocalVar): MOption[ResolvedInfo.LocalVar] = {
    val preR: PreResult[ResolvedInfo.LocalVar] = preResolvedInfoLocalVar(o) match {
     case PreResult(continu, MSome(r: ResolvedInfo.LocalVar)) => PreResult(continu, MSome[ResolvedInfo.LocalVar](r))
     case PreResult(_, MSome(_)) => halt("Can only produce object of type ResolvedInfo.LocalVar")
     case PreResult(continu, _) => PreResult(continu, MNone[ResolvedInfo.LocalVar]())
    }
    val r: MOption[ResolvedInfo.LocalVar] = if (preR.continu) {
      val o2: ResolvedInfo.LocalVar = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      if (hasChanged)
        MSome(o2)
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: ResolvedInfo.LocalVar = r.getOrElse(o)
    val postR: MOption[ResolvedInfo.LocalVar] = postResolvedInfoLocalVar(o2) match {
     case MSome(result: ResolvedInfo.LocalVar) => MSome[ResolvedInfo.LocalVar](result)
     case MSome(_) => halt("Can only produce object of type ResolvedInfo.LocalVar")
     case _ => MNone[ResolvedInfo.LocalVar]()
    }
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformTypedFun(o: Typed.Fun): MOption[Typed.Fun] = {
    val preR: PreResult[Typed.Fun] = preTypedFun(o) match {
     case PreResult(continu, MSome(r: Typed.Fun)) => PreResult(continu, MSome[Typed.Fun](r))
     case PreResult(_, MSome(_)) => halt("Can only produce object of type Typed.Fun")
     case PreResult(continu, _) => PreResult(continu, MNone[Typed.Fun]())
    }
    val r: MOption[Typed.Fun] = if (preR.continu) {
      val o2: Typed.Fun = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[IS[Z, Typed]] = transformISZ(o2.args, transformTyped _)
      val r1: MOption[Typed] = transformTyped(o2.ret)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty)
        MSome(o2(args = r0.getOrElse(o2.args), ret = r1.getOrElse(o2.ret)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Typed.Fun = r.getOrElse(o)
    val postR: MOption[Typed.Fun] = postTypedFun(o2) match {
     case MSome(result: Typed.Fun) => MSome[Typed.Fun](result)
     case MSome(_) => halt("Can only produce object of type Typed.Fun")
     case _ => MNone[Typed.Fun]()
    }
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformTypedMethod(o: Typed.Method): MOption[Typed.Method] = {
    val preR: PreResult[Typed.Method] = preTypedMethod(o) match {
     case PreResult(continu, MSome(r: Typed.Method)) => PreResult(continu, MSome[Typed.Method](r))
     case PreResult(_, MSome(_)) => halt("Can only produce object of type Typed.Method")
     case PreResult(continu, _) => PreResult(continu, MNone[Typed.Method]())
    }
    val r: MOption[Typed.Method] = if (preR.continu) {
      val o2: Typed.Method = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Typed.Fun] = transformTypedFun(o2.tpe)
      if (hasChanged || r0.nonEmpty)
        MSome(o2(tpe = r0.getOrElse(o2.tpe)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Typed.Method = r.getOrElse(o)
    val postR: MOption[Typed.Method] = postTypedMethod(o2) match {
     case MSome(result: Typed.Method) => MSome[Typed.Method](result)
     case MSome(_) => halt("Can only produce object of type Typed.Method")
     case _ => MNone[Typed.Method]()
    }
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformResolvedInfoMethod(o: ResolvedInfo.Method): MOption[ResolvedInfo.Method] = {
    val preR: PreResult[ResolvedInfo.Method] = preResolvedInfoMethod(o) match {
     case PreResult(continu, MSome(r: ResolvedInfo.Method)) => PreResult(continu, MSome[ResolvedInfo.Method](r))
     case PreResult(_, MSome(_)) => halt("Can only produce object of type ResolvedInfo.Method")
     case PreResult(continu, _) => PreResult(continu, MNone[ResolvedInfo.Method]())
    }
    val r: MOption[ResolvedInfo.Method] = if (preR.continu) {
      val o2: ResolvedInfo.Method = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Option[Typed.Fun]] = transformOption(o2.tpeOpt, transformTypedFun _)
      if (hasChanged || r0.nonEmpty)
        MSome(o2(tpeOpt = r0.getOrElse(o2.tpeOpt)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: ResolvedInfo.Method = r.getOrElse(o)
    val postR: MOption[ResolvedInfo.Method] = postResolvedInfoMethod(o2) match {
     case MSome(result: ResolvedInfo.Method) => MSome[ResolvedInfo.Method](result)
     case MSome(_) => halt("Can only produce object of type ResolvedInfo.Method")
     case _ => MNone[ResolvedInfo.Method]()
    }
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformExpLitB(o: Exp.LitB): MOption[Exp.LitB] = {
    val preR: PreResult[Exp.LitB] = preExpLitB(o) match {
     case PreResult(continu, MSome(r: Exp.LitB)) => PreResult(continu, MSome[Exp.LitB](r))
     case PreResult(_, MSome(_)) => halt("Can only produce object of type Exp.LitB")
     case PreResult(continu, _) => PreResult(continu, MNone[Exp.LitB]())
    }
    val r: MOption[Exp.LitB] = if (preR.continu) {
      val o2: Exp.LitB = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Attr] = transformAttr(o2.attr)
      if (hasChanged || r0.nonEmpty)
        MSome(o2(attr = r0.getOrElse(o2.attr)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Exp.LitB = r.getOrElse(o)
    val postR: MOption[Exp.LitB] = postExpLitB(o2) match {
     case MSome(result: Exp.LitB) => MSome[Exp.LitB](result)
     case MSome(_) => halt("Can only produce object of type Exp.LitB")
     case _ => MNone[Exp.LitB]()
    }
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

}