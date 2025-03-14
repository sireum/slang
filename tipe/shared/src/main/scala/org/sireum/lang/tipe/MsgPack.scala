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

// This file is auto-generated from Info.scala, AST.scala, Typed.scala

package org.sireum.lang.tipe

import org.sireum._

object MsgPack {

  object Constants {

    val _symbolScopeLocal: Z = -32

    val _symbolScopeGlobal: Z = -31

    val _symbolInfoPackage: Z = -30

    val _symbolInfoVar: Z = -29

    val _symbolInfoSpecVar: Z = -28

    val _symbolInfoRsVal: Z = -27

    val _symbolInfoMethod: Z = -26

    val _symbolInfoSpecMethod: Z = -25

    val _symbolInfoObject: Z = -24

    val _symbolInfoExtMethod: Z = -23

    val _symbolInfoJustMethod: Z = -22

    val _symbolInfoEnum: Z = -21

    val _symbolInfoEnumElement: Z = -20

    val _symbolInfoLocalVar: Z = -19

    val _symbolInfoFact: Z = -18

    val _symbolInfoTheorem: Z = -17

    val _symbolInfoInv: Z = -16

    val _symbolTypeInfoSubZ: Z = -15

    val _symbolTypeInfoEnum: Z = -14

    val _symbolTypeInfoSig: Z = -13

    val _symbolTypeInfoName: Z = -12

    val _symbolTypeInfoAdt: Z = -11

    val _symbolTypeInfoTypeAlias: Z = -10

    val _symbolTypeInfoTypeVar: Z = -9

    val _symbolTypeInfoMembers: Z = -8

    val _astTopUnitProgram: Z = -7

    val _astTopUnitTruthTableUnit: Z = -6

    val _astLoopContract: Z = -5

    val _astStmtImport: Z = -4

    val _astStmtImportImporter: Z = -3

    val _astStmtImportMultiSelector: Z = -2

    val _astStmtImportWildcardSelector: Z = -1

    val _astStmtImportNamedSelector: Z = 0

    val _astStmtVar: Z = 1

    val _astStmtVarPattern: Z = 2

    val _astStmtSpecVar: Z = 3

    val _astStmtRsVal: Z = 4

    val _astStmtMethod: Z = 5

    val _astStmtExtMethod: Z = 6

    val _astStmtJustMethod: Z = 7

    val _astStmtSpecMethod: Z = 8

    val _astStmtEnum: Z = 9

    val _astStmtSubZ: Z = 10

    val _astStmtObject: Z = 11

    val _astStmtSig: Z = 12

    val _astStmtAdt: Z = 13

    val _astStmtTypeAlias: Z = 14

    val _astStmtAssign: Z = 15

    val _astStmtBlock: Z = 16

    val _astStmtIf: Z = 17

    val _astStmtInduct: Z = 18

    val _astStmtMatch: Z = 19

    val _astStmtWhile: Z = 20

    val _astStmtFor: Z = 21

    val _astStmtReturn: Z = 22

    val _astStmtExpr: Z = 23

    val _astStmtFact: Z = 24

    val _astStmtInv: Z = 25

    val _astStmtTheorem: Z = 26

    val _astStmtDataRefinement: Z = 27

    val _astStmtSpecLabel: Z = 28

    val _astStmtSpecBlock: Z = 29

    val _astStmtDeduceSequent: Z = 30

    val _astStmtDeduceSteps: Z = 31

    val _astStmtHavoc: Z = 32

    val _astMethodContractAccesses: Z = 33

    val _astMethodContractClaims: Z = 34

    val _astMethodContractSimple: Z = 35

    val _astMethodContractCases: Z = 36

    val _astMethodContractCase: Z = 37

    val _astMethodContractInfoFlows: Z = 38

    val _astMethodContractInfoFlowGroup: Z = 39

    val _astMethodContractInfoFlowFlow: Z = 40

    val _astMethodContractInfoFlowCase: Z = 41

    val _astSequent: Z = 42

    val _astProofAst: Z = 43

    val _astProofAstStepIdNum: Z = 44

    val _astProofAstStepIdStr: Z = 45

    val _astProofAstStepRegular: Z = 46

    val _astProofAstStepAssume: Z = 47

    val _astProofAstStepAssert: Z = 48

    val _astProofAstStepSubProof: Z = 49

    val _astProofAstStepLet: Z = 50

    val _astProofAstStepLetParam: Z = 51

    val _astProofAstStepJustificationRef: Z = 52

    val _astProofAstStepJustificationApply: Z = 53

    val _astProofAstStepJustificationApplyNamed: Z = 54

    val _astProofAstStepJustificationApplyEta: Z = 55

    val _astCase: Z = 56

    val _astEnumGenRangeExpr: Z = 57

    val _astEnumGenRangeStep: Z = 58

    val _astEnumGenFor: Z = 59

    val _astTypeNamed: Z = 60

    val _astTypeFun: Z = 61

    val _astTypeTuple: Z = 62

    val _astPatternLiteral: Z = 63

    val _astPatternLitInterpolate: Z = 64

    val _astPatternRef: Z = 65

    val _astPatternVarBinding: Z = 66

    val _astPatternWildcard: Z = 67

    val _astPatternSeqWildcard: Z = 68

    val _astPatternStructure: Z = 69

    val _astExpLitB: Z = 70

    val _astExpLitC: Z = 71

    val _astExpLitZ: Z = 72

    val _astExpLitF32: Z = 73

    val _astExpLitF64: Z = 74

    val _astExpLitR: Z = 75

    val _astExpLitString: Z = 76

    val _astExpStringInterpolate: Z = 77

    val _astExpThis: Z = 78

    val _astExpSuper: Z = 79

    val _astExpUnary: Z = 80

    val _astExpBinary: Z = 81

    val _astExpIdent: Z = 82

    val _astExpEta: Z = 83

    val _astExpTuple: Z = 84

    val _astExpSelect: Z = 85

    val _astExpInvoke: Z = 86

    val _astExpInvokeNamed: Z = 87

    val _astExpIf: Z = 88

    val _astExpTypeCond: Z = 89

    val _astExpSym: Z = 90

    val _astExpFunParam: Z = 91

    val _astExpFun: Z = 92

    val _astExpForYield: Z = 93

    val _astExpQuantType: Z = 94

    val _astExpQuantRange: Z = 95

    val _astExpQuantEach: Z = 96

    val _astExpInput: Z = 97

    val _astExpOld: Z = 98

    val _astExpRS: Z = 99

    val _astExpAt: Z = 100

    val _astExpLoopIndex: Z = 101

    val _astExpStateSeq: Z = 102

    val _astExpStateSeqFragment: Z = 103

    val _astExpResult: Z = 104

    val _astExpStrictPureBlock: Z = 105

    val _astExpLabeled: Z = 106

    val _astExpAssumeAgree: Z = 107

    val _astExpAssertAgree: Z = 108

    val _astExpInfoFlowInvariant: Z = 109

    val _astNamedArg: Z = 110

    val _astId: Z = 111

    val _astName: Z = 112

    val _astBody: Z = 113

    val _astAdtParam: Z = 114

    val _astAnnotation: Z = 115

    val _astMethodSig: Z = 116

    val _astParam: Z = 117

    val _astTypeParam: Z = 118

    val _astAttr: Z = 119

    val _astTypedAttr: Z = 120

    val _astResolvedAttr: Z = 121

    val _astResolvedInfoBuiltIn: Z = 122

    val _astResolvedInfoPackage: Z = 123

    val _astResolvedInfoEnum: Z = 124

    val _astResolvedInfoEnumElement: Z = 125

    val _astResolvedInfoObject: Z = 126

    val _astResolvedInfoVar: Z = 127

    val _astResolvedInfoMethod: Z = 128

    val _astResolvedInfoMethods: Z = 129

    val _astResolvedInfoTuple: Z = 130

    val _astResolvedInfoLocalVar: Z = 131

    val _astResolvedInfoFact: Z = 132

    val _astResolvedInfoTheorem: Z = 133

    val _astResolvedInfoInv: Z = 134

    val _astTruthTableRow: Z = 135

    val _astTruthTableAssignment: Z = 136

    val _astTruthTableConclusionValidity: Z = 137

    val _astTruthTableConclusionTautology: Z = 138

    val _astTruthTableConclusionContradictory: Z = 139

    val _astTruthTableConclusionContingent: Z = 140

    val _astTypedName: Z = 141

    val _astTypedTuple: Z = 142

    val _astTypedFun: Z = 143

    val _astTypedTypeVar: Z = 144

    val _astTypedPackage: Z = 145

    val _astTypedObject: Z = 146

    val _astTypedEnum: Z = 147

    val _astTypedMethod: Z = 148

    val _astTypedMethods: Z = 149

    val _astTypedFact: Z = 150

    val _astTypedTheorem: Z = 151

    val _astTypedInv: Z = 152

  }

  object Writer {

    @record class Default(val writer: MessagePack.Writer.Impl) extends Writer

  }

  @msig trait Writer {

    def writer: MessagePack.Writer

    def write_symbolScope(o: org.sireum.lang.symbol.Scope): Unit = {
      o match {
        case o: org.sireum.lang.symbol.Scope.Local => write_symbolScopeLocal(o)
        case o: org.sireum.lang.symbol.Scope.Global => write_symbolScopeGlobal(o)
      }
    }

    def write_symbolScopeLocal(o: org.sireum.lang.symbol.Scope.Local): Unit = {
      writer.writeZ(Constants._symbolScopeLocal)
      writer.writeHashMap(o.nameMap, writer.writeString _, write_symbolInfo _)
      writer.writeHashMap(o.typeMap, writer.writeString _, write_symbolTypeInfo _)
      writer.writeOption(o.localThisOpt, write_astTyped _)
      writer.writeOption(o.methodReturnOpt, write_astTyped _)
      writer.writeHashMap(o.indexMap, writer.writeString _, write_astTyped _)
      writer.writeOption(o.outerOpt, write_symbolScope _)
    }

    def write_symbolScopeGlobal(o: org.sireum.lang.symbol.Scope.Global): Unit = {
      writer.writeZ(Constants._symbolScopeGlobal)
      writer.writeISZ(o.packageName, writer.writeString _)
      writer.writeISZ(o.imports, write_astStmtImport _)
      writer.writeISZ(o.enclosingName, writer.writeString _)
    }

    def write_symbolInfo(o: org.sireum.lang.symbol.Info): Unit = {
      o match {
        case o: org.sireum.lang.symbol.Info.Package => write_symbolInfoPackage(o)
        case o: org.sireum.lang.symbol.Info.Var => write_symbolInfoVar(o)
        case o: org.sireum.lang.symbol.Info.SpecVar => write_symbolInfoSpecVar(o)
        case o: org.sireum.lang.symbol.Info.RsVal => write_symbolInfoRsVal(o)
        case o: org.sireum.lang.symbol.Info.Method => write_symbolInfoMethod(o)
        case o: org.sireum.lang.symbol.Info.SpecMethod => write_symbolInfoSpecMethod(o)
        case o: org.sireum.lang.symbol.Info.Object => write_symbolInfoObject(o)
        case o: org.sireum.lang.symbol.Info.ExtMethod => write_symbolInfoExtMethod(o)
        case o: org.sireum.lang.symbol.Info.JustMethod => write_symbolInfoJustMethod(o)
        case o: org.sireum.lang.symbol.Info.Enum => write_symbolInfoEnum(o)
        case o: org.sireum.lang.symbol.Info.EnumElement => write_symbolInfoEnumElement(o)
        case o: org.sireum.lang.symbol.Info.LocalVar => write_symbolInfoLocalVar(o)
        case o: org.sireum.lang.symbol.Info.Fact => write_symbolInfoFact(o)
        case o: org.sireum.lang.symbol.Info.Theorem => write_symbolInfoTheorem(o)
        case o: org.sireum.lang.symbol.Info.Inv => write_symbolInfoInv(o)
      }
    }

    def write_symbolInfoPackage(o: org.sireum.lang.symbol.Info.Package): Unit = {
      writer.writeZ(Constants._symbolInfoPackage)
      writer.writeISZ(o.name, writer.writeString _)
      writer.writeOption(o.typedOpt, write_astTyped _)
      writer.writeOption(o.resOpt, write_astResolvedInfo _)
    }

    def write_symbolInfoVar(o: org.sireum.lang.symbol.Info.Var): Unit = {
      writer.writeZ(Constants._symbolInfoVar)
      writer.writeISZ(o.owner, writer.writeString _)
      writer.writeB(o.isInObject)
      write_symbolScope(o.scope)
      write_astStmtVar(o.ast)
    }

    def write_symbolInfoSpecVar(o: org.sireum.lang.symbol.Info.SpecVar): Unit = {
      writer.writeZ(Constants._symbolInfoSpecVar)
      writer.writeISZ(o.owner, writer.writeString _)
      writer.writeB(o.isInObject)
      write_symbolScope(o.scope)
      write_astStmtSpecVar(o.ast)
    }

    def write_symbolInfoRsVal(o: org.sireum.lang.symbol.Info.RsVal): Unit = {
      writer.writeZ(Constants._symbolInfoRsVal)
      writer.writeISZ(o.owner, writer.writeString _)
      write_symbolScope(o.scope)
      write_astStmtRsVal(o.ast)
    }

    def write_symbolInfoMethod(o: org.sireum.lang.symbol.Info.Method): Unit = {
      writer.writeZ(Constants._symbolInfoMethod)
      writer.writeISZ(o.owner, writer.writeString _)
      writer.writeB(o.isInObject)
      write_symbolScope(o.scope)
      writer.writeB(o.hasBody)
      write_astStmtMethod(o.ast)
    }

    def write_symbolInfoSpecMethod(o: org.sireum.lang.symbol.Info.SpecMethod): Unit = {
      writer.writeZ(Constants._symbolInfoSpecMethod)
      writer.writeISZ(o.owner, writer.writeString _)
      writer.writeB(o.isInObject)
      write_symbolScope(o.scope)
      write_astStmtSpecMethod(o.ast)
    }

    def write_symbolInfoObject(o: org.sireum.lang.symbol.Info.Object): Unit = {
      writer.writeZ(Constants._symbolInfoObject)
      writer.writeISZ(o.owner, writer.writeString _)
      writer.writeB(o.isSynthetic)
      write_symbolScopeGlobal(o.scope)
      writer.writeB(o.outlined)
      writer.writeB(o.contractOutlined)
      writer.writeB(o.typeChecked)
      write_astStmtObject(o.ast)
      writer.writeOption(o.typedOpt, write_astTyped _)
      writer.writeOption(o.resOpt, write_astResolvedInfo _)
      write_astResolvedInfoMethod(o.constructorRes)
    }

    def write_symbolInfoExtMethod(o: org.sireum.lang.symbol.Info.ExtMethod): Unit = {
      writer.writeZ(Constants._symbolInfoExtMethod)
      writer.writeISZ(o.owner, writer.writeString _)
      write_symbolScopeGlobal(o.scope)
      write_astStmtExtMethod(o.ast)
    }

    def write_symbolInfoJustMethod(o: org.sireum.lang.symbol.Info.JustMethod): Unit = {
      writer.writeZ(Constants._symbolInfoJustMethod)
      writer.writeISZ(o.owner, writer.writeString _)
      write_symbolScopeGlobal(o.scope)
      write_astStmtJustMethod(o.ast)
    }

    def write_symbolInfoEnum(o: org.sireum.lang.symbol.Info.Enum): Unit = {
      writer.writeZ(Constants._symbolInfoEnum)
      writer.writeISZ(o.name, writer.writeString _)
      writer.writeMap(o.elements, writer.writeString _, write_astResolvedInfo _)
      writer.writeOption(o.typedOpt, write_astTyped _)
      writer.writeOption(o.resOpt, write_astResolvedInfo _)
      writer.writeOption(o.elementTypedOpt, write_astTyped _)
      writer.writeOption(o.posOpt, writer.writePosition _)
    }

    def write_symbolInfoEnumElement(o: org.sireum.lang.symbol.Info.EnumElement): Unit = {
      writer.writeZ(Constants._symbolInfoEnumElement)
      writer.writeISZ(o.owner, writer.writeString _)
      writer.writeString(o.id)
      writer.writeOption(o.typedOpt, write_astTyped _)
      writer.writeOption(o.resOpt, write_astResolvedInfo _)
      writer.writeOption(o.posOpt, writer.writePosition _)
    }

    def write_symbolInfoLocalVar(o: org.sireum.lang.symbol.Info.LocalVar): Unit = {
      writer.writeZ(Constants._symbolInfoLocalVar)
      writer.writeISZ(o.name, writer.writeString _)
      writer.writeB(o.isVal)
      write_astId(o.ast)
      writer.writeOption(o.typedOpt, write_astTyped _)
      writer.writeOption(o.initOpt, write_astAssignExp _)
      writer.writeOption(o.resOpt, write_astResolvedInfo _)
    }

    def write_symbolInfoFact(o: org.sireum.lang.symbol.Info.Fact): Unit = {
      writer.writeZ(Constants._symbolInfoFact)
      writer.writeISZ(o.owner, writer.writeString _)
      writer.writeString(o.id)
      write_symbolScopeGlobal(o.scope)
      write_astStmtFact(o.ast)
    }

    def write_symbolInfoTheorem(o: org.sireum.lang.symbol.Info.Theorem): Unit = {
      writer.writeZ(Constants._symbolInfoTheorem)
      writer.writeISZ(o.owner, writer.writeString _)
      writer.writeString(o.id)
      write_symbolScopeGlobal(o.scope)
      write_astStmtTheorem(o.ast)
    }

    def write_symbolInfoInv(o: org.sireum.lang.symbol.Info.Inv): Unit = {
      writer.writeZ(Constants._symbolInfoInv)
      writer.writeISZ(o.owner, writer.writeString _)
      writer.writeString(o.id)
      write_symbolScope(o.scope)
      write_astStmtInv(o.ast)
    }

    def write_symbolTypeInfo(o: org.sireum.lang.symbol.TypeInfo): Unit = {
      o match {
        case o: org.sireum.lang.symbol.TypeInfo.SubZ => write_symbolTypeInfoSubZ(o)
        case o: org.sireum.lang.symbol.TypeInfo.Enum => write_symbolTypeInfoEnum(o)
        case o: org.sireum.lang.symbol.TypeInfo.Sig => write_symbolTypeInfoSig(o)
        case o: org.sireum.lang.symbol.TypeInfo.Adt => write_symbolTypeInfoAdt(o)
        case o: org.sireum.lang.symbol.TypeInfo.TypeAlias => write_symbolTypeInfoTypeAlias(o)
        case o: org.sireum.lang.symbol.TypeInfo.TypeVar => write_symbolTypeInfoTypeVar(o)
      }
    }

    def write_symbolTypeInfoSubZ(o: org.sireum.lang.symbol.TypeInfo.SubZ): Unit = {
      writer.writeZ(Constants._symbolTypeInfoSubZ)
      writer.writeISZ(o.owner, writer.writeString _)
      write_astStmtSubZ(o.ast)
    }

    def write_symbolTypeInfoEnum(o: org.sireum.lang.symbol.TypeInfo.Enum): Unit = {
      writer.writeZ(Constants._symbolTypeInfoEnum)
      writer.writeISZ(o.owner, writer.writeString _)
      writer.writeMap(o.elements, writer.writeString _, write_astResolvedInfo _)
      writer.writeOption(o.posOpt, writer.writePosition _)
    }

    def write_symbolTypeInfoSig(o: org.sireum.lang.symbol.TypeInfo.Sig): Unit = {
      writer.writeZ(Constants._symbolTypeInfoSig)
      writer.writeISZ(o.owner, writer.writeString _)
      writer.writeB(o.outlined)
      writer.writeB(o.contractOutlined)
      writer.writeB(o.typeChecked)
      write_astTypedName(o.tpe)
      writer.writeISZ(o.ancestors, write_astTypedName _)
      writer.writeHashSMap(o.specVars, writer.writeString _, write_symbolInfoSpecVar _)
      writer.writeHashSMap(o.specMethods, writer.writeString _, write_symbolInfoSpecMethod _)
      writer.writeHashSMap(o.methods, writer.writeString _, write_symbolInfoMethod _)
      writer.writeHashSMap(o.refinements, writer.writeString _, write_symbolTypeInfoName _)
      writer.writeHashSMap(o.invariants, writer.writeString _, write_symbolInfoInv _)
      writer.writeISZ(o.dataRefinements, write_astStmtDataRefinement _)
      write_symbolScopeGlobal(o.scope)
      write_astStmtSig(o.ast)
    }

    def write_symbolTypeInfoName(o: org.sireum.lang.symbol.TypeInfo.Name): Unit = {
      writer.writeZ(Constants._symbolTypeInfoName)
      writer.writeISZ(o.ids, writer.writeString _)
    }

    def write_symbolTypeInfoAdt(o: org.sireum.lang.symbol.TypeInfo.Adt): Unit = {
      writer.writeZ(Constants._symbolTypeInfoAdt)
      writer.writeISZ(o.owner, writer.writeString _)
      writer.writeB(o.outlined)
      writer.writeB(o.contractOutlined)
      writer.writeB(o.typeChecked)
      write_astTypedName(o.tpe)
      writer.writeOption(o.constructorTypeOpt, write_astTyped _)
      writer.writeOption(o.constructorResOpt, write_astResolvedInfo _)
      writer.writeMap(o.extractorTypeMap, writer.writeString _, write_astTyped _)
      writer.writeOption(o.extractorResOpt, write_astResolvedInfo _)
      writer.writeISZ(o.ancestors, write_astTypedName _)
      writer.writeHashSMap(o.specVars, writer.writeString _, write_symbolInfoSpecVar _)
      writer.writeHashSMap(o.vars, writer.writeString _, write_symbolInfoVar _)
      writer.writeHashSMap(o.specMethods, writer.writeString _, write_symbolInfoSpecMethod _)
      writer.writeHashSMap(o.methods, writer.writeString _, write_symbolInfoMethod _)
      writer.writeHashSMap(o.refinements, writer.writeString _, write_symbolTypeInfoName _)
      writer.writeHashSMap(o.invariants, writer.writeString _, write_symbolInfoInv _)
      writer.writeISZ(o.dataRefinements, write_astStmtDataRefinement _)
      write_symbolScopeGlobal(o.scope)
      write_astStmtAdt(o.ast)
    }

    def write_symbolTypeInfoTypeAlias(o: org.sireum.lang.symbol.TypeInfo.TypeAlias): Unit = {
      writer.writeZ(Constants._symbolTypeInfoTypeAlias)
      writer.writeISZ(o.name, writer.writeString _)
      write_symbolScopeGlobal(o.scope)
      write_astStmtTypeAlias(o.ast)
    }

    def write_symbolTypeInfoTypeVar(o: org.sireum.lang.symbol.TypeInfo.TypeVar): Unit = {
      writer.writeZ(Constants._symbolTypeInfoTypeVar)
      writer.writeString(o.id)
      write_astTypeParam(o.ast)
    }

    def write_symbolTypeInfoMembers(o: org.sireum.lang.symbol.TypeInfo.Members): Unit = {
      writer.writeZ(Constants._symbolTypeInfoMembers)
      writer.writeHashSMap(o.specVars, writer.writeString _, write_symbolInfoSpecVar _)
      writer.writeHashSMap(o.vars, writer.writeString _, write_symbolInfoVar _)
      writer.writeHashSMap(o.specMethods, writer.writeString _, write_symbolInfoSpecMethod _)
      writer.writeHashSMap(o.methods, writer.writeString _, write_symbolInfoMethod _)
      writer.writeHashSMap(o.refinements, writer.writeString _, write_symbolTypeInfoName _)
      writer.writeHashSMap(o.invariants, writer.writeString _, write_symbolInfoInv _)
    }

    def write_astTopUnit(o: org.sireum.lang.ast.TopUnit): Unit = {
      o match {
        case o: org.sireum.lang.ast.TopUnit.Program => write_astTopUnitProgram(o)
        case o: org.sireum.lang.ast.TopUnit.TruthTableUnit => write_astTopUnitTruthTableUnit(o)
      }
    }

    def write_astTopUnitProgram(o: org.sireum.lang.ast.TopUnit.Program): Unit = {
      writer.writeZ(Constants._astTopUnitProgram)
      writer.writeOption(o.fileUriOpt, writer.writeString _)
      write_astName(o.packageName)
      write_astBody(o.body)
    }

    def write_astTopUnitTruthTableUnit(o: org.sireum.lang.ast.TopUnit.TruthTableUnit): Unit = {
      writer.writeZ(Constants._astTopUnitTruthTableUnit)
      writer.writeOption(o.fileUriOpt, writer.writeString _)
      writer.writeISZ(o.stars, writer.writePosition _)
      writer.writeISZ(o.vars, write_astId _)
      writer.writePosition(o.separator)
      writer.writeB(o.isSequent)
      write_astSequent(o.sequent)
      writer.writeISZ(o.rows, write_astTruthTableRow _)
      writer.writeOption(o.conclusionOpt, write_astTruthTableConclusion _)
    }

    def write_astStmt(o: org.sireum.lang.ast.Stmt): Unit = {
      o match {
        case o: org.sireum.lang.ast.Stmt.Import => write_astStmtImport(o)
        case o: org.sireum.lang.ast.Stmt.Var => write_astStmtVar(o)
        case o: org.sireum.lang.ast.Stmt.VarPattern => write_astStmtVarPattern(o)
        case o: org.sireum.lang.ast.Stmt.SpecVar => write_astStmtSpecVar(o)
        case o: org.sireum.lang.ast.Stmt.RsVal => write_astStmtRsVal(o)
        case o: org.sireum.lang.ast.Stmt.Method => write_astStmtMethod(o)
        case o: org.sireum.lang.ast.Stmt.ExtMethod => write_astStmtExtMethod(o)
        case o: org.sireum.lang.ast.Stmt.JustMethod => write_astStmtJustMethod(o)
        case o: org.sireum.lang.ast.Stmt.SpecMethod => write_astStmtSpecMethod(o)
        case o: org.sireum.lang.ast.Stmt.Enum => write_astStmtEnum(o)
        case o: org.sireum.lang.ast.Stmt.SubZ => write_astStmtSubZ(o)
        case o: org.sireum.lang.ast.Stmt.Object => write_astStmtObject(o)
        case o: org.sireum.lang.ast.Stmt.Sig => write_astStmtSig(o)
        case o: org.sireum.lang.ast.Stmt.Adt => write_astStmtAdt(o)
        case o: org.sireum.lang.ast.Stmt.TypeAlias => write_astStmtTypeAlias(o)
        case o: org.sireum.lang.ast.Stmt.Assign => write_astStmtAssign(o)
        case o: org.sireum.lang.ast.Stmt.Block => write_astStmtBlock(o)
        case o: org.sireum.lang.ast.Stmt.If => write_astStmtIf(o)
        case o: org.sireum.lang.ast.Stmt.Induct => write_astStmtInduct(o)
        case o: org.sireum.lang.ast.Stmt.Match => write_astStmtMatch(o)
        case o: org.sireum.lang.ast.Stmt.While => write_astStmtWhile(o)
        case o: org.sireum.lang.ast.Stmt.For => write_astStmtFor(o)
        case o: org.sireum.lang.ast.Stmt.Return => write_astStmtReturn(o)
        case o: org.sireum.lang.ast.Stmt.Expr => write_astStmtExpr(o)
        case o: org.sireum.lang.ast.Stmt.Fact => write_astStmtFact(o)
        case o: org.sireum.lang.ast.Stmt.Inv => write_astStmtInv(o)
        case o: org.sireum.lang.ast.Stmt.Theorem => write_astStmtTheorem(o)
        case o: org.sireum.lang.ast.Stmt.DataRefinement => write_astStmtDataRefinement(o)
        case o: org.sireum.lang.ast.Stmt.SpecLabel => write_astStmtSpecLabel(o)
        case o: org.sireum.lang.ast.Stmt.SpecBlock => write_astStmtSpecBlock(o)
        case o: org.sireum.lang.ast.Stmt.DeduceSequent => write_astStmtDeduceSequent(o)
        case o: org.sireum.lang.ast.Stmt.DeduceSteps => write_astStmtDeduceSteps(o)
        case o: org.sireum.lang.ast.Stmt.Havoc => write_astStmtHavoc(o)
      }
    }

    def write_astHasModifies(o: org.sireum.lang.ast.HasModifies): Unit = {
      o match {
        case o: org.sireum.lang.ast.LoopContract => write_astLoopContract(o)
        case o: org.sireum.lang.ast.MethodContract.Simple => write_astMethodContractSimple(o)
        case o: org.sireum.lang.ast.MethodContract.Cases => write_astMethodContractCases(o)
      }
    }

    def write_astLoopContract(o: org.sireum.lang.ast.LoopContract): Unit = {
      writer.writeZ(Constants._astLoopContract)
      writer.writeISZ(o.invariants, write_astExp _)
      writer.writeISZ(o.modifies, write_astExpRef _)
      writer.writeOption(o.maxItOpt, write_astExpLitZ _)
    }

    def write_astStmtImport(o: org.sireum.lang.ast.Stmt.Import): Unit = {
      writer.writeZ(Constants._astStmtImport)
      writer.writeISZ(o.importers, write_astStmtImportImporter _)
      write_astAttr(o.attr)
    }

    def write_astStmtImportImporter(o: org.sireum.lang.ast.Stmt.Import.Importer): Unit = {
      writer.writeZ(Constants._astStmtImportImporter)
      write_astName(o.name)
      writer.writeOption(o.selectorOpt, write_astStmtImportSelector _)
    }

    def write_astStmtImportSelector(o: org.sireum.lang.ast.Stmt.Import.Selector): Unit = {
      o match {
        case o: org.sireum.lang.ast.Stmt.Import.MultiSelector => write_astStmtImportMultiSelector(o)
        case o: org.sireum.lang.ast.Stmt.Import.WildcardSelector => write_astStmtImportWildcardSelector(o)
      }
    }

    def write_astStmtImportMultiSelector(o: org.sireum.lang.ast.Stmt.Import.MultiSelector): Unit = {
      writer.writeZ(Constants._astStmtImportMultiSelector)
      writer.writeISZ(o.selectors, write_astStmtImportNamedSelector _)
    }

    def write_astStmtImportWildcardSelector(o: org.sireum.lang.ast.Stmt.Import.WildcardSelector): Unit = {
      writer.writeZ(Constants._astStmtImportWildcardSelector)
    }

    def write_astStmtImportNamedSelector(o: org.sireum.lang.ast.Stmt.Import.NamedSelector): Unit = {
      writer.writeZ(Constants._astStmtImportNamedSelector)
      write_astId(o.from)
      write_astId(o.to)
    }

    def write_astStmtVar(o: org.sireum.lang.ast.Stmt.Var): Unit = {
      writer.writeZ(Constants._astStmtVar)
      writer.writeB(o.isSpec)
      writer.writeB(o.isVal)
      write_astId(o.id)
      writer.writeOption(o.tipeOpt, write_astType _)
      writer.writeOption(o.initOpt, write_astAssignExp _)
      write_astResolvedAttr(o.attr)
    }

    def write_astStmtVarPattern(o: org.sireum.lang.ast.Stmt.VarPattern): Unit = {
      writer.writeZ(Constants._astStmtVarPattern)
      writer.writeB(o.isSpec)
      writer.writeB(o.isVal)
      write_astPattern(o.pattern)
      writer.writeOption(o.tipeOpt, write_astType _)
      write_astAssignExp(o.init)
      write_astAttr(o.attr)
    }

    def write_astStmtSpecVar(o: org.sireum.lang.ast.Stmt.SpecVar): Unit = {
      writer.writeZ(Constants._astStmtSpecVar)
      writer.writeB(o.isVal)
      write_astId(o.id)
      write_astType(o.tipe)
      write_astResolvedAttr(o.attr)
    }

    def write_astStmtRsVal(o: org.sireum.lang.ast.Stmt.RsVal): Unit = {
      writer.writeZ(Constants._astStmtRsVal)
      write_astId(o.id)
      write_astExp(o.init)
      write_astResolvedAttr(o.attr)
    }

    def write_astStmtMethod(o: org.sireum.lang.ast.Stmt.Method): Unit = {
      writer.writeZ(Constants._astStmtMethod)
      writer.writeB(o.typeChecked)
      write_astPurityType(o.purity)
      writer.writeISZ(o.modifiers, writer.writeString _)
      write_astMethodSig(o.sig)
      write_astMethodContract(o.mcontract)
      writer.writeOption(o.bodyOpt, write_astBody _)
      write_astResolvedAttr(o.attr)
    }

    def write_astStmtExtMethod(o: org.sireum.lang.ast.Stmt.ExtMethod): Unit = {
      writer.writeZ(Constants._astStmtExtMethod)
      writer.writeB(o.isPure)
      write_astMethodSig(o.sig)
      write_astMethodContract(o.contract)
      write_astResolvedAttr(o.attr)
    }

    def write_astStmtJustMethod(o: org.sireum.lang.ast.Stmt.JustMethod): Unit = {
      writer.writeZ(Constants._astStmtJustMethod)
      writer.writeOption(o.etaOpt, write_astExpLitString _)
      write_astMethodSig(o.sig)
      write_astResolvedAttr(o.attr)
    }

    def write_astStmtSpecMethod(o: org.sireum.lang.ast.Stmt.SpecMethod): Unit = {
      writer.writeZ(Constants._astStmtSpecMethod)
      write_astMethodSig(o.sig)
      write_astResolvedAttr(o.attr)
    }

    def write_astStmtEnum(o: org.sireum.lang.ast.Stmt.Enum): Unit = {
      writer.writeZ(Constants._astStmtEnum)
      write_astId(o.id)
      writer.writeISZ(o.elements, write_astId _)
      write_astAttr(o.attr)
    }

    def write_astStmtSubZ(o: org.sireum.lang.ast.Stmt.SubZ): Unit = {
      writer.writeZ(Constants._astStmtSubZ)
      write_astId(o.id)
      writer.writeB(o.isSigned)
      writer.writeB(o.isBitVector)
      writer.writeB(o.isWrapped)
      writer.writeB(o.hasMin)
      writer.writeB(o.hasMax)
      writer.writeZ(o.bitWidth)
      writer.writeZ(o.min)
      writer.writeZ(o.max)
      writer.writeB(o.isIndex)
      writer.writeZ(o.index)
      write_astAttr(o.attr)
    }

    def write_astStmtObject(o: org.sireum.lang.ast.Stmt.Object): Unit = {
      writer.writeZ(Constants._astStmtObject)
      writer.writeB(o.isApp)
      writer.writeOption(o.extNameOpt, writer.writeString _)
      write_astId(o.id)
      writer.writeISZ(o.stmts, write_astStmt _)
      write_astAttr(o.attr)
    }

    def write_astStmtSig(o: org.sireum.lang.ast.Stmt.Sig): Unit = {
      writer.writeZ(Constants._astStmtSig)
      writer.writeB(o.isImmutable)
      writer.writeB(o.isSealed)
      writer.writeB(o.isExt)
      write_astId(o.id)
      writer.writeISZ(o.typeParams, write_astTypeParam _)
      writer.writeISZ(o.parents, write_astTypeNamed _)
      writer.writeISZ(o.stmts, write_astStmt _)
      write_astAttr(o.attr)
    }

    def write_astStmtAdt(o: org.sireum.lang.ast.Stmt.Adt): Unit = {
      writer.writeZ(Constants._astStmtAdt)
      writer.writeB(o.isRoot)
      writer.writeB(o.isDatatype)
      writer.writeB(o.isUnclonable)
      write_astId(o.id)
      writer.writeISZ(o.typeParams, write_astTypeParam _)
      writer.writeISZ(o.params, write_astAdtParam _)
      writer.writeISZ(o.parents, write_astTypeNamed _)
      writer.writeISZ(o.stmts, write_astStmt _)
      write_astAttr(o.attr)
    }

    def write_astStmtTypeAlias(o: org.sireum.lang.ast.Stmt.TypeAlias): Unit = {
      writer.writeZ(Constants._astStmtTypeAlias)
      write_astId(o.id)
      writer.writeISZ(o.typeParams, write_astTypeParam _)
      write_astType(o.tipe)
      write_astAttr(o.attr)
    }

    def write_astStmtAssign(o: org.sireum.lang.ast.Stmt.Assign): Unit = {
      writer.writeZ(Constants._astStmtAssign)
      write_astExp(o.lhs)
      write_astAssignExp(o.rhs)
      write_astAttr(o.attr)
    }

    def write_astStmtBlock(o: org.sireum.lang.ast.Stmt.Block): Unit = {
      writer.writeZ(Constants._astStmtBlock)
      write_astBody(o.body)
      write_astAttr(o.attr)
    }

    def write_astStmtIf(o: org.sireum.lang.ast.Stmt.If): Unit = {
      writer.writeZ(Constants._astStmtIf)
      write_astExp(o.cond)
      write_astBody(o.thenBody)
      write_astBody(o.elseBody)
      write_astTypedAttr(o.attr)
    }

    def write_astStmtInduct(o: org.sireum.lang.ast.Stmt.Induct): Unit = {
      writer.writeZ(Constants._astStmtInduct)
      write_astExp(o.exp)
      writer.writeISZ(o.context, writer.writeString _)
      writer.writeISZ(o.locals, writer.writeString _)
      write_astAttr(o.attr)
    }

    def write_astStmtMatch(o: org.sireum.lang.ast.Stmt.Match): Unit = {
      writer.writeZ(Constants._astStmtMatch)
      writer.writeB(o.isInduct)
      write_astExp(o.exp)
      writer.writeISZ(o.cases, write_astCase _)
      write_astTypedAttr(o.attr)
    }

    def write_astStmtWhile(o: org.sireum.lang.ast.Stmt.While): Unit = {
      writer.writeZ(Constants._astStmtWhile)
      writer.writeISZ(o.context, writer.writeString _)
      write_astExp(o.cond)
      write_astLoopContract(o.contract)
      write_astBody(o.body)
      write_astAttr(o.attr)
    }

    def write_astStmtFor(o: org.sireum.lang.ast.Stmt.For): Unit = {
      writer.writeZ(Constants._astStmtFor)
      writer.writeISZ(o.context, writer.writeString _)
      writer.writeISZ(o.enumGens, write_astEnumGenFor _)
      write_astBody(o.body)
      write_astAttr(o.attr)
    }

    def write_astStmtReturn(o: org.sireum.lang.ast.Stmt.Return): Unit = {
      writer.writeZ(Constants._astStmtReturn)
      writer.writeOption(o.expOpt, write_astExp _)
      write_astTypedAttr(o.attr)
    }

    def write_astStmtExpr(o: org.sireum.lang.ast.Stmt.Expr): Unit = {
      writer.writeZ(Constants._astStmtExpr)
      write_astExp(o.exp)
      write_astTypedAttr(o.attr)
    }

    def write_astStmtExprKindType(o: org.sireum.lang.ast.Stmt.Expr.Kind.Type): Unit = {
      writer.writeZ(o.ordinal)
    }

    def write_astStmtSpec(o: org.sireum.lang.ast.Stmt.Spec): Unit = {
      o match {
        case o: org.sireum.lang.ast.Stmt.SpecVar => write_astStmtSpecVar(o)
        case o: org.sireum.lang.ast.Stmt.RsVal => write_astStmtRsVal(o)
        case o: org.sireum.lang.ast.Stmt.JustMethod => write_astStmtJustMethod(o)
        case o: org.sireum.lang.ast.Stmt.SpecMethod => write_astStmtSpecMethod(o)
        case o: org.sireum.lang.ast.Stmt.Induct => write_astStmtInduct(o)
        case o: org.sireum.lang.ast.Stmt.Fact => write_astStmtFact(o)
        case o: org.sireum.lang.ast.Stmt.Inv => write_astStmtInv(o)
        case o: org.sireum.lang.ast.Stmt.Theorem => write_astStmtTheorem(o)
        case o: org.sireum.lang.ast.Stmt.DataRefinement => write_astStmtDataRefinement(o)
        case o: org.sireum.lang.ast.Stmt.SpecLabel => write_astStmtSpecLabel(o)
        case o: org.sireum.lang.ast.Stmt.SpecBlock => write_astStmtSpecBlock(o)
        case o: org.sireum.lang.ast.Stmt.DeduceSequent => write_astStmtDeduceSequent(o)
        case o: org.sireum.lang.ast.Stmt.DeduceSteps => write_astStmtDeduceSteps(o)
        case o: org.sireum.lang.ast.Stmt.Havoc => write_astStmtHavoc(o)
      }
    }

    def write_astStmtFact(o: org.sireum.lang.ast.Stmt.Fact): Unit = {
      writer.writeZ(Constants._astStmtFact)
      write_astId(o.id)
      writer.writeISZ(o.typeParams, write_astTypeParam _)
      writer.writeOption(o.descOpt, write_astExpLitString _)
      writer.writeISZ(o.claims, write_astExp _)
      writer.writeB(o.isFun)
      write_astResolvedAttr(o.attr)
    }

    def write_astStmtInv(o: org.sireum.lang.ast.Stmt.Inv): Unit = {
      writer.writeZ(Constants._astStmtInv)
      write_astId(o.id)
      writer.writeISZ(o.claims, write_astExp _)
      write_astResolvedAttr(o.attr)
    }

    def write_astStmtTheorem(o: org.sireum.lang.ast.Stmt.Theorem): Unit = {
      writer.writeZ(Constants._astStmtTheorem)
      writer.writeB(o.isLemma)
      write_astId(o.id)
      writer.writeISZ(o.typeParams, write_astTypeParam _)
      writer.writeOption(o.descOpt, write_astExpLitString _)
      write_astExp(o.claim)
      writer.writeB(o.isFun)
      write_astProofAst(o.proof)
      write_astResolvedAttr(o.attr)
    }

    def write_astStmtDataRefinement(o: org.sireum.lang.ast.Stmt.DataRefinement): Unit = {
      writer.writeZ(Constants._astStmtDataRefinement)
      write_astExpRef(o.rep)
      writer.writeISZ(o.refs, write_astExpRef _)
      writer.writeISZ(o.claims, write_astExp _)
      write_astAttr(o.attr)
    }

    def write_astStmtSpecLabel(o: org.sireum.lang.ast.Stmt.SpecLabel): Unit = {
      writer.writeZ(Constants._astStmtSpecLabel)
      write_astId(o.id)
    }

    def write_astStmtSpecBlock(o: org.sireum.lang.ast.Stmt.SpecBlock): Unit = {
      writer.writeZ(Constants._astStmtSpecBlock)
      write_astStmtBlock(o.block)
    }

    def write_astStmtDeduceSequent(o: org.sireum.lang.ast.Stmt.DeduceSequent): Unit = {
      writer.writeZ(Constants._astStmtDeduceSequent)
      writer.writeOption(o.justOpt, write_astExpLitString _)
      writer.writeISZ(o.sequents, write_astSequent _)
      write_astAttr(o.attr)
    }

    def write_astStmtDeduceSteps(o: org.sireum.lang.ast.Stmt.DeduceSteps): Unit = {
      writer.writeZ(Constants._astStmtDeduceSteps)
      writer.writeISZ(o.steps, write_astProofAstStep _)
      write_astAttr(o.attr)
    }

    def write_astStmtHavoc(o: org.sireum.lang.ast.Stmt.Havoc): Unit = {
      writer.writeZ(Constants._astStmtHavoc)
      writer.writeISZ(o.args, write_astExpRef _)
      write_astAttr(o.attr)
    }

    def write_astMethodContract(o: org.sireum.lang.ast.MethodContract): Unit = {
      o match {
        case o: org.sireum.lang.ast.MethodContract.Simple => write_astMethodContractSimple(o)
        case o: org.sireum.lang.ast.MethodContract.Cases => write_astMethodContractCases(o)
      }
    }

    def write_astMethodContractAccesses(o: org.sireum.lang.ast.MethodContract.Accesses): Unit = {
      writer.writeZ(Constants._astMethodContractAccesses)
      writer.writeISZ(o.refs, write_astExpRef _)
      write_astAttr(o.attr)
    }

    def write_astMethodContractClaims(o: org.sireum.lang.ast.MethodContract.Claims): Unit = {
      writer.writeZ(Constants._astMethodContractClaims)
      writer.writeISZ(o.claims, write_astExp _)
      write_astAttr(o.attr)
    }

    def write_astMethodContractSimple(o: org.sireum.lang.ast.MethodContract.Simple): Unit = {
      writer.writeZ(Constants._astMethodContractSimple)
      write_astMethodContractAccesses(o.readsClause)
      write_astMethodContractClaims(o.requiresClause)
      write_astMethodContractAccesses(o.modifiesClause)
      write_astMethodContractClaims(o.ensuresClause)
      write_astMethodContractInfoFlows(o.infoFlowsClause)
      write_astAttr(o.attr)
    }

    def write_astMethodContractCases(o: org.sireum.lang.ast.MethodContract.Cases): Unit = {
      writer.writeZ(Constants._astMethodContractCases)
      write_astMethodContractAccesses(o.readsClause)
      write_astMethodContractAccesses(o.modifiesClause)
      writer.writeISZ(o.cases, write_astMethodContractCase _)
      write_astAttr(o.attr)
    }

    def write_astMethodContractCase(o: org.sireum.lang.ast.MethodContract.Case): Unit = {
      writer.writeZ(Constants._astMethodContractCase)
      write_astExpLitString(o.label)
      write_astMethodContractClaims(o.requiresClause)
      write_astMethodContractClaims(o.ensuresClause)
    }

    def write_astMethodContractInfoFlowElement(o: org.sireum.lang.ast.MethodContract.InfoFlowElement): Unit = {
      o match {
        case o: org.sireum.lang.ast.MethodContract.InfoFlowGroup => write_astMethodContractInfoFlowGroup(o)
        case o: org.sireum.lang.ast.MethodContract.InfoFlowFlow => write_astMethodContractInfoFlowFlow(o)
        case o: org.sireum.lang.ast.MethodContract.InfoFlowCase => write_astMethodContractInfoFlowCase(o)
      }
    }

    def write_astMethodContractInfoFlows(o: org.sireum.lang.ast.MethodContract.InfoFlows): Unit = {
      writer.writeZ(Constants._astMethodContractInfoFlows)
      writer.writeISZ(o.flows, write_astMethodContractInfoFlowElement _)
      write_astAttr(o.attr)
    }

    def write_astMethodContractInfoFlowGroup(o: org.sireum.lang.ast.MethodContract.InfoFlowGroup): Unit = {
      writer.writeZ(Constants._astMethodContractInfoFlowGroup)
      write_astExpLitString(o.label)
      write_astMethodContractClaims(o.membersClause)
    }

    def write_astMethodContractInfoFlowFlow(o: org.sireum.lang.ast.MethodContract.InfoFlowFlow): Unit = {
      writer.writeZ(Constants._astMethodContractInfoFlowFlow)
      write_astExpLitString(o.label)
      write_astMethodContractClaims(o.fromClause)
      write_astMethodContractClaims(o.toClause)
    }

    def write_astMethodContractInfoFlowCase(o: org.sireum.lang.ast.MethodContract.InfoFlowCase): Unit = {
      writer.writeZ(Constants._astMethodContractInfoFlowCase)
      write_astExpLitString(o.label)
      write_astMethodContractClaims(o.requiresClause)
      write_astMethodContractClaims(o.inAgreeClause)
      write_astMethodContractClaims(o.outAgreeClause)
    }

    def write_astSequent(o: org.sireum.lang.ast.Sequent): Unit = {
      writer.writeZ(Constants._astSequent)
      writer.writeISZ(o.premises, write_astExp _)
      write_astExp(o.conclusion)
      writer.writeISZ(o.steps, write_astProofAstStep _)
      write_astAttr(o.attr)
    }

    def write_astProofAst(o: org.sireum.lang.ast.ProofAst): Unit = {
      writer.writeZ(Constants._astProofAst)
      writer.writeISZ(o.steps, write_astProofAstStep _)
      write_astAttr(o.attr)
    }

    def write_astProofAstStep(o: org.sireum.lang.ast.ProofAst.Step): Unit = {
      o match {
        case o: org.sireum.lang.ast.ProofAst.Step.Regular => write_astProofAstStepRegular(o)
        case o: org.sireum.lang.ast.ProofAst.Step.Assume => write_astProofAstStepAssume(o)
        case o: org.sireum.lang.ast.ProofAst.Step.Assert => write_astProofAstStepAssert(o)
        case o: org.sireum.lang.ast.ProofAst.Step.SubProof => write_astProofAstStepSubProof(o)
        case o: org.sireum.lang.ast.ProofAst.Step.Let => write_astProofAstStepLet(o)
      }
    }

    def write_astProofAstStepId(o: org.sireum.lang.ast.ProofAst.StepId): Unit = {
      o match {
        case o: org.sireum.lang.ast.ProofAst.StepId.Num => write_astProofAstStepIdNum(o)
        case o: org.sireum.lang.ast.ProofAst.StepId.Str => write_astProofAstStepIdStr(o)
      }
    }

    def write_astProofAstStepIdNum(o: org.sireum.lang.ast.ProofAst.StepId.Num): Unit = {
      writer.writeZ(Constants._astProofAstStepIdNum)
      writer.writeZ(o.no)
      write_astAttr(o.attr)
    }

    def write_astProofAstStepIdStr(o: org.sireum.lang.ast.ProofAst.StepId.Str): Unit = {
      writer.writeZ(Constants._astProofAstStepIdStr)
      writer.writeB(o.isSynthetic)
      writer.writeString(o.value)
      write_astAttr(o.attr)
    }

    def write_astProofAstStepRegular(o: org.sireum.lang.ast.ProofAst.Step.Regular): Unit = {
      writer.writeZ(Constants._astProofAstStepRegular)
      write_astProofAstStepId(o.id)
      write_astExp(o.claim)
      write_astProofAstStepJustification(o.just)
      write_astAttr(o.attr)
    }

    def write_astProofAstStepAssume(o: org.sireum.lang.ast.ProofAst.Step.Assume): Unit = {
      writer.writeZ(Constants._astProofAstStepAssume)
      write_astProofAstStepId(o.id)
      write_astExp(o.claim)
      write_astAttr(o.attr)
    }

    def write_astProofAstStepAssert(o: org.sireum.lang.ast.ProofAst.Step.Assert): Unit = {
      writer.writeZ(Constants._astProofAstStepAssert)
      write_astProofAstStepId(o.id)
      write_astExp(o.claim)
      writer.writeISZ(o.steps, write_astProofAstStep _)
      write_astAttr(o.attr)
    }

    def write_astProofAstStepSubProof(o: org.sireum.lang.ast.ProofAst.Step.SubProof): Unit = {
      writer.writeZ(Constants._astProofAstStepSubProof)
      write_astProofAstStepId(o.id)
      writer.writeISZ(o.steps, write_astProofAstStep _)
      write_astAttr(o.attr)
    }

    def write_astProofAstStepLet(o: org.sireum.lang.ast.ProofAst.Step.Let): Unit = {
      writer.writeZ(Constants._astProofAstStepLet)
      write_astProofAstStepId(o.id)
      writer.writeISZ(o.params, write_astProofAstStepLetParam _)
      writer.writeISZ(o.steps, write_astProofAstStep _)
      writer.writeISZ(o.context, writer.writeString _)
      write_astAttr(o.attr)
    }

    def write_astProofAstStepLetParam(o: org.sireum.lang.ast.ProofAst.Step.Let.Param): Unit = {
      writer.writeZ(Constants._astProofAstStepLetParam)
      write_astId(o.id)
      writer.writeOption(o.tipeOpt, write_astType _)
    }

    def write_astProofAstStepJustification(o: org.sireum.lang.ast.ProofAst.Step.Justification): Unit = {
      o match {
        case o: org.sireum.lang.ast.ProofAst.Step.Justification.Ref => write_astProofAstStepJustificationRef(o)
        case o: org.sireum.lang.ast.ProofAst.Step.Justification.Apply => write_astProofAstStepJustificationApply(o)
        case o: org.sireum.lang.ast.ProofAst.Step.Justification.ApplyNamed => write_astProofAstStepJustificationApplyNamed(o)
        case o: org.sireum.lang.ast.ProofAst.Step.Justification.ApplyEta => write_astProofAstStepJustificationApplyEta(o)
      }
    }

    def write_astProofAstStepJustificationRef(o: org.sireum.lang.ast.ProofAst.Step.Justification.Ref): Unit = {
      writer.writeZ(Constants._astProofAstStepJustificationRef)
      write_astExpRef(o.ref)
      writer.writeB(o.hasWitness)
      writer.writeISZ(o.witnesses, write_astProofAstStepId _)
    }

    def write_astProofAstStepJustificationApply(o: org.sireum.lang.ast.ProofAst.Step.Justification.Apply): Unit = {
      writer.writeZ(Constants._astProofAstStepJustificationApply)
      write_astExpInvoke(o.invoke)
      writer.writeB(o.hasWitness)
      writer.writeISZ(o.witnesses, write_astProofAstStepId _)
    }

    def write_astProofAstStepJustificationApplyNamed(o: org.sireum.lang.ast.ProofAst.Step.Justification.ApplyNamed): Unit = {
      writer.writeZ(Constants._astProofAstStepJustificationApplyNamed)
      write_astExpInvokeNamed(o.invoke)
      writer.writeB(o.hasWitness)
      writer.writeISZ(o.witnesses, write_astProofAstStepId _)
    }

    def write_astProofAstStepJustificationApplyEta(o: org.sireum.lang.ast.ProofAst.Step.Justification.ApplyEta): Unit = {
      writer.writeZ(Constants._astProofAstStepJustificationApplyEta)
      write_astExpEta(o.eta)
      writer.writeB(o.hasWitness)
      writer.writeISZ(o.witnesses, write_astProofAstStepId _)
    }

    def write_astAssignExp(o: org.sireum.lang.ast.AssignExp): Unit = {
      o match {
        case o: org.sireum.lang.ast.Stmt.Block => write_astStmtBlock(o)
        case o: org.sireum.lang.ast.Stmt.If => write_astStmtIf(o)
        case o: org.sireum.lang.ast.Stmt.Match => write_astStmtMatch(o)
        case o: org.sireum.lang.ast.Stmt.Return => write_astStmtReturn(o)
        case o: org.sireum.lang.ast.Stmt.Expr => write_astStmtExpr(o)
      }
    }

    def write_astCase(o: org.sireum.lang.ast.Case): Unit = {
      writer.writeZ(Constants._astCase)
      write_astPattern(o.pattern)
      writer.writeOption(o.condOpt, write_astExp _)
      write_astBody(o.body)
    }

    def write_astEnumGenRange(o: org.sireum.lang.ast.EnumGen.Range): Unit = {
      o match {
        case o: org.sireum.lang.ast.EnumGen.Range.Expr => write_astEnumGenRangeExpr(o)
        case o: org.sireum.lang.ast.EnumGen.Range.Step => write_astEnumGenRangeStep(o)
      }
    }

    def write_astEnumGenRangeExpr(o: org.sireum.lang.ast.EnumGen.Range.Expr): Unit = {
      writer.writeZ(Constants._astEnumGenRangeExpr)
      write_astExp(o.exp)
      write_astAttr(o.attr)
    }

    def write_astEnumGenRangeStep(o: org.sireum.lang.ast.EnumGen.Range.Step): Unit = {
      writer.writeZ(Constants._astEnumGenRangeStep)
      writer.writeB(o.isInclusive)
      write_astExp(o.start)
      write_astExp(o.end)
      writer.writeOption(o.byOpt, write_astExp _)
      write_astAttr(o.attr)
    }

    def write_astEnumGenFor(o: org.sireum.lang.ast.EnumGen.For): Unit = {
      writer.writeZ(Constants._astEnumGenFor)
      writer.writeOption(o.idOpt, write_astId _)
      write_astEnumGenRange(o.range)
      writer.writeOption(o.condOpt, write_astExp _)
      write_astLoopContract(o.contract)
    }

    def write_astType(o: org.sireum.lang.ast.Type): Unit = {
      o match {
        case o: org.sireum.lang.ast.Type.Named => write_astTypeNamed(o)
        case o: org.sireum.lang.ast.Type.Fun => write_astTypeFun(o)
        case o: org.sireum.lang.ast.Type.Tuple => write_astTypeTuple(o)
      }
    }

    def write_astTypeNamed(o: org.sireum.lang.ast.Type.Named): Unit = {
      writer.writeZ(Constants._astTypeNamed)
      write_astName(o.name)
      writer.writeISZ(o.typeArgs, write_astType _)
      write_astTypedAttr(o.attr)
    }

    def write_astTypeFun(o: org.sireum.lang.ast.Type.Fun): Unit = {
      writer.writeZ(Constants._astTypeFun)
      writer.writeB(o.isPure)
      writer.writeB(o.isByName)
      writer.writeISZ(o.args, write_astType _)
      write_astType(o.ret)
      write_astTypedAttr(o.attr)
    }

    def write_astTypeTuple(o: org.sireum.lang.ast.Type.Tuple): Unit = {
      writer.writeZ(Constants._astTypeTuple)
      writer.writeISZ(o.args, write_astType _)
      write_astTypedAttr(o.attr)
    }

    def write_astPattern(o: org.sireum.lang.ast.Pattern): Unit = {
      o match {
        case o: org.sireum.lang.ast.Pattern.Literal => write_astPatternLiteral(o)
        case o: org.sireum.lang.ast.Pattern.LitInterpolate => write_astPatternLitInterpolate(o)
        case o: org.sireum.lang.ast.Pattern.Ref => write_astPatternRef(o)
        case o: org.sireum.lang.ast.Pattern.VarBinding => write_astPatternVarBinding(o)
        case o: org.sireum.lang.ast.Pattern.Wildcard => write_astPatternWildcard(o)
        case o: org.sireum.lang.ast.Pattern.SeqWildcard => write_astPatternSeqWildcard(o)
        case o: org.sireum.lang.ast.Pattern.Structure => write_astPatternStructure(o)
      }
    }

    def write_astPatternLiteral(o: org.sireum.lang.ast.Pattern.Literal): Unit = {
      writer.writeZ(Constants._astPatternLiteral)
      write_astLit(o.lit)
    }

    def write_astPatternLitInterpolate(o: org.sireum.lang.ast.Pattern.LitInterpolate): Unit = {
      writer.writeZ(Constants._astPatternLitInterpolate)
      writer.writeString(o.prefix)
      writer.writeString(o.value)
      write_astTypedAttr(o.attr)
    }

    def write_astPatternRef(o: org.sireum.lang.ast.Pattern.Ref): Unit = {
      writer.writeZ(Constants._astPatternRef)
      writer.writeB(o.isAccess)
      write_astName(o.name)
      writer.writeOption(o.receiverTipeOpt, write_astTypedName _)
      writer.writeISZ(o.idContext, writer.writeString _)
      write_astResolvedAttr(o.attr)
    }

    def write_astPatternVarBinding(o: org.sireum.lang.ast.Pattern.VarBinding): Unit = {
      writer.writeZ(Constants._astPatternVarBinding)
      write_astId(o.id)
      writer.writeOption(o.tipeOpt, write_astType _)
      writer.writeISZ(o.idContext, writer.writeString _)
      write_astTypedAttr(o.attr)
    }

    def write_astPatternWildcard(o: org.sireum.lang.ast.Pattern.Wildcard): Unit = {
      writer.writeZ(Constants._astPatternWildcard)
      writer.writeOption(o.typeOpt, write_astType _)
      write_astTypedAttr(o.attr)
    }

    def write_astPatternSeqWildcard(o: org.sireum.lang.ast.Pattern.SeqWildcard): Unit = {
      writer.writeZ(Constants._astPatternSeqWildcard)
      write_astTypedAttr(o.attr)
    }

    def write_astPatternStructure(o: org.sireum.lang.ast.Pattern.Structure): Unit = {
      writer.writeZ(Constants._astPatternStructure)
      writer.writeOption(o.idOpt, write_astId _)
      writer.writeOption(o.nameOpt, write_astName _)
      writer.writeISZ(o.patterns, write_astPattern _)
      writer.writeISZ(o.idContext, writer.writeString _)
      write_astResolvedAttr(o.attr)
    }

    def write_astExp(o: org.sireum.lang.ast.Exp): Unit = {
      o match {
        case o: org.sireum.lang.ast.ProofAst.StepId.Num => write_astProofAstStepIdNum(o)
        case o: org.sireum.lang.ast.ProofAst.StepId.Str => write_astProofAstStepIdStr(o)
        case o: org.sireum.lang.ast.Exp.LitB => write_astExpLitB(o)
        case o: org.sireum.lang.ast.Exp.LitC => write_astExpLitC(o)
        case o: org.sireum.lang.ast.Exp.LitZ => write_astExpLitZ(o)
        case o: org.sireum.lang.ast.Exp.LitF32 => write_astExpLitF32(o)
        case o: org.sireum.lang.ast.Exp.LitF64 => write_astExpLitF64(o)
        case o: org.sireum.lang.ast.Exp.LitR => write_astExpLitR(o)
        case o: org.sireum.lang.ast.Exp.LitString => write_astExpLitString(o)
        case o: org.sireum.lang.ast.Exp.StringInterpolate => write_astExpStringInterpolate(o)
        case o: org.sireum.lang.ast.Exp.This => write_astExpThis(o)
        case o: org.sireum.lang.ast.Exp.Super => write_astExpSuper(o)
        case o: org.sireum.lang.ast.Exp.Unary => write_astExpUnary(o)
        case o: org.sireum.lang.ast.Exp.Binary => write_astExpBinary(o)
        case o: org.sireum.lang.ast.Exp.Ident => write_astExpIdent(o)
        case o: org.sireum.lang.ast.Exp.Eta => write_astExpEta(o)
        case o: org.sireum.lang.ast.Exp.Tuple => write_astExpTuple(o)
        case o: org.sireum.lang.ast.Exp.Select => write_astExpSelect(o)
        case o: org.sireum.lang.ast.Exp.Invoke => write_astExpInvoke(o)
        case o: org.sireum.lang.ast.Exp.InvokeNamed => write_astExpInvokeNamed(o)
        case o: org.sireum.lang.ast.Exp.If => write_astExpIf(o)
        case o: org.sireum.lang.ast.Exp.TypeCond => write_astExpTypeCond(o)
        case o: org.sireum.lang.ast.Exp.Sym => write_astExpSym(o)
        case o: org.sireum.lang.ast.Exp.Fun => write_astExpFun(o)
        case o: org.sireum.lang.ast.Exp.ForYield => write_astExpForYield(o)
        case o: org.sireum.lang.ast.Exp.QuantType => write_astExpQuantType(o)
        case o: org.sireum.lang.ast.Exp.QuantRange => write_astExpQuantRange(o)
        case o: org.sireum.lang.ast.Exp.QuantEach => write_astExpQuantEach(o)
        case o: org.sireum.lang.ast.Exp.Input => write_astExpInput(o)
        case o: org.sireum.lang.ast.Exp.Old => write_astExpOld(o)
        case o: org.sireum.lang.ast.Exp.RS => write_astExpRS(o)
        case o: org.sireum.lang.ast.Exp.At => write_astExpAt(o)
        case o: org.sireum.lang.ast.Exp.LoopIndex => write_astExpLoopIndex(o)
        case o: org.sireum.lang.ast.Exp.StateSeq => write_astExpStateSeq(o)
        case o: org.sireum.lang.ast.Exp.Result => write_astExpResult(o)
        case o: org.sireum.lang.ast.Exp.StrictPureBlock => write_astExpStrictPureBlock(o)
        case o: org.sireum.lang.ast.Exp.Labeled => write_astExpLabeled(o)
        case o: org.sireum.lang.ast.Exp.AssumeAgree => write_astExpAssumeAgree(o)
        case o: org.sireum.lang.ast.Exp.AssertAgree => write_astExpAssertAgree(o)
        case o: org.sireum.lang.ast.Exp.InfoFlowInvariant => write_astExpInfoFlowInvariant(o)
      }
    }

    def write_astLit(o: org.sireum.lang.ast.Lit): Unit = {
      o match {
        case o: org.sireum.lang.ast.ProofAst.StepId.Num => write_astProofAstStepIdNum(o)
        case o: org.sireum.lang.ast.ProofAst.StepId.Str => write_astProofAstStepIdStr(o)
        case o: org.sireum.lang.ast.Exp.LitB => write_astExpLitB(o)
        case o: org.sireum.lang.ast.Exp.LitC => write_astExpLitC(o)
        case o: org.sireum.lang.ast.Exp.LitZ => write_astExpLitZ(o)
        case o: org.sireum.lang.ast.Exp.LitF32 => write_astExpLitF32(o)
        case o: org.sireum.lang.ast.Exp.LitF64 => write_astExpLitF64(o)
        case o: org.sireum.lang.ast.Exp.LitR => write_astExpLitR(o)
        case o: org.sireum.lang.ast.Exp.LitString => write_astExpLitString(o)
      }
    }

    def write_astExpLitB(o: org.sireum.lang.ast.Exp.LitB): Unit = {
      writer.writeZ(Constants._astExpLitB)
      writer.writeB(o.value)
      write_astAttr(o.attr)
    }

    def write_astExpLitC(o: org.sireum.lang.ast.Exp.LitC): Unit = {
      writer.writeZ(Constants._astExpLitC)
      writer.writeC(o.value)
      write_astAttr(o.attr)
    }

    def write_astExpLitZ(o: org.sireum.lang.ast.Exp.LitZ): Unit = {
      writer.writeZ(Constants._astExpLitZ)
      writer.writeZ(o.value)
      write_astAttr(o.attr)
    }

    def write_astExpLitF32(o: org.sireum.lang.ast.Exp.LitF32): Unit = {
      writer.writeZ(Constants._astExpLitF32)
      writer.writeF32(o.value)
      write_astAttr(o.attr)
    }

    def write_astExpLitF64(o: org.sireum.lang.ast.Exp.LitF64): Unit = {
      writer.writeZ(Constants._astExpLitF64)
      writer.writeF64(o.value)
      write_astAttr(o.attr)
    }

    def write_astExpLitR(o: org.sireum.lang.ast.Exp.LitR): Unit = {
      writer.writeZ(Constants._astExpLitR)
      writer.writeR(o.value)
      write_astAttr(o.attr)
    }

    def write_astExpLitString(o: org.sireum.lang.ast.Exp.LitString): Unit = {
      writer.writeZ(Constants._astExpLitString)
      writer.writeString(o.value)
      write_astAttr(o.attr)
    }

    def write_astExpStringInterpolate(o: org.sireum.lang.ast.Exp.StringInterpolate): Unit = {
      writer.writeZ(Constants._astExpStringInterpolate)
      writer.writeString(o.prefix)
      writer.writeISZ(o.lits, write_astExpLitString _)
      writer.writeISZ(o.args, write_astExp _)
      write_astTypedAttr(o.attr)
    }

    def write_astExpThis(o: org.sireum.lang.ast.Exp.This): Unit = {
      writer.writeZ(Constants._astExpThis)
      writer.writeISZ(o.owner, writer.writeString _)
      write_astTypedAttr(o.attr)
    }

    def write_astExpSuper(o: org.sireum.lang.ast.Exp.Super): Unit = {
      writer.writeZ(Constants._astExpSuper)
      writer.writeOption(o.idOpt, write_astId _)
      write_astTypedAttr(o.attr)
    }

    def write_astExpUnaryOpType(o: org.sireum.lang.ast.Exp.UnaryOp.Type): Unit = {
      writer.writeZ(o.ordinal)
    }

    def write_astExpUnary(o: org.sireum.lang.ast.Exp.Unary): Unit = {
      writer.writeZ(Constants._astExpUnary)
      write_astExpUnaryOpType(o.op)
      write_astExp(o.exp)
      write_astResolvedAttr(o.attr)
      writer.writeOption(o.opPosOpt, writer.writePosition _)
    }

    def write_astExpRef(o: org.sireum.lang.ast.Exp.Ref): Unit = {
      o match {
        case o: org.sireum.lang.ast.Exp.Ident => write_astExpIdent(o)
        case o: org.sireum.lang.ast.Exp.Select => write_astExpSelect(o)
      }
    }

    def write_astExpBinary(o: org.sireum.lang.ast.Exp.Binary): Unit = {
      writer.writeZ(Constants._astExpBinary)
      write_astExp(o.left)
      writer.writeString(o.op)
      write_astExp(o.right)
      write_astResolvedAttr(o.attr)
      writer.writeOption(o.opPosOpt, writer.writePosition _)
    }

    def write_astExpIdent(o: org.sireum.lang.ast.Exp.Ident): Unit = {
      writer.writeZ(Constants._astExpIdent)
      write_astId(o.id)
      write_astResolvedAttr(o.attr)
    }

    def write_astExpEta(o: org.sireum.lang.ast.Exp.Eta): Unit = {
      writer.writeZ(Constants._astExpEta)
      write_astExpRef(o.ref)
      write_astTypedAttr(o.attr)
    }

    def write_astExpTuple(o: org.sireum.lang.ast.Exp.Tuple): Unit = {
      writer.writeZ(Constants._astExpTuple)
      writer.writeISZ(o.args, write_astExp _)
      write_astTypedAttr(o.attr)
    }

    def write_astExpSelect(o: org.sireum.lang.ast.Exp.Select): Unit = {
      writer.writeZ(Constants._astExpSelect)
      writer.writeOption(o.receiverOpt, write_astExp _)
      write_astId(o.id)
      writer.writeISZ(o.targs, write_astType _)
      write_astResolvedAttr(o.attr)
    }

    def write_astExpInvoke(o: org.sireum.lang.ast.Exp.Invoke): Unit = {
      writer.writeZ(Constants._astExpInvoke)
      writer.writeOption(o.receiverOpt, write_astExp _)
      write_astExpIdent(o.ident)
      writer.writeISZ(o.targs, write_astType _)
      writer.writeISZ(o.args, write_astExp _)
      write_astResolvedAttr(o.attr)
    }

    def write_astExpInvokeNamed(o: org.sireum.lang.ast.Exp.InvokeNamed): Unit = {
      writer.writeZ(Constants._astExpInvokeNamed)
      writer.writeOption(o.receiverOpt, write_astExp _)
      write_astExpIdent(o.ident)
      writer.writeISZ(o.targs, write_astType _)
      writer.writeISZ(o.args, write_astNamedArg _)
      write_astResolvedAttr(o.attr)
    }

    def write_astExpIf(o: org.sireum.lang.ast.Exp.If): Unit = {
      writer.writeZ(Constants._astExpIf)
      write_astExp(o.cond)
      write_astExp(o.thenExp)
      write_astExp(o.elseExp)
      write_astTypedAttr(o.attr)
    }

    def write_astExpTypeCond(o: org.sireum.lang.ast.Exp.TypeCond): Unit = {
      writer.writeZ(Constants._astExpTypeCond)
      writer.writeISZ(o.args, write_astExp _)
      write_astExpFun(o.fun)
      write_astAttr(o.attr)
    }

    def write_astExpSym(o: org.sireum.lang.ast.Exp.Sym): Unit = {
      writer.writeZ(Constants._astExpSym)
      writer.writeZ(o.num)
      write_astTypedAttr(o.attr)
    }

    def write_astExpFunParam(o: org.sireum.lang.ast.Exp.Fun.Param): Unit = {
      writer.writeZ(Constants._astExpFunParam)
      writer.writeOption(o.idOpt, write_astId _)
      writer.writeOption(o.tipeOpt, write_astType _)
      writer.writeOption(o.typedOpt, write_astTyped _)
    }

    def write_astExpFun(o: org.sireum.lang.ast.Exp.Fun): Unit = {
      writer.writeZ(Constants._astExpFun)
      writer.writeISZ(o.context, writer.writeString _)
      writer.writeISZ(o.params, write_astExpFunParam _)
      write_astAssignExp(o.exp)
      write_astTypedAttr(o.attr)
    }

    def write_astExpForYield(o: org.sireum.lang.ast.Exp.ForYield): Unit = {
      writer.writeZ(Constants._astExpForYield)
      writer.writeISZ(o.enumGens, write_astEnumGenFor _)
      write_astExp(o.exp)
      write_astTypedAttr(o.attr)
    }

    def write_astExpQuant(o: org.sireum.lang.ast.Exp.Quant): Unit = {
      o match {
        case o: org.sireum.lang.ast.Exp.QuantType => write_astExpQuantType(o)
        case o: org.sireum.lang.ast.Exp.QuantRange => write_astExpQuantRange(o)
        case o: org.sireum.lang.ast.Exp.QuantEach => write_astExpQuantEach(o)
      }
    }

    def write_astExpQuantType(o: org.sireum.lang.ast.Exp.QuantType): Unit = {
      writer.writeZ(Constants._astExpQuantType)
      writer.writeB(o.isForall)
      write_astExpFun(o.fun)
      write_astAttr(o.attr)
    }

    def write_astExpQuantRange(o: org.sireum.lang.ast.Exp.QuantRange): Unit = {
      writer.writeZ(Constants._astExpQuantRange)
      writer.writeB(o.isForall)
      write_astExp(o.lo)
      write_astExp(o.hi)
      writer.writeB(o.hiExact)
      write_astExpFun(o.fun)
      write_astResolvedAttr(o.attr)
    }

    def write_astExpQuantEach(o: org.sireum.lang.ast.Exp.QuantEach): Unit = {
      writer.writeZ(Constants._astExpQuantEach)
      writer.writeB(o.isForall)
      write_astExp(o.seq)
      write_astExpFun(o.fun)
      write_astResolvedAttr(o.attr)
    }

    def write_astExpInput(o: org.sireum.lang.ast.Exp.Input): Unit = {
      writer.writeZ(Constants._astExpInput)
      write_astExp(o.exp)
      write_astAttr(o.attr)
    }

    def write_astExpOld(o: org.sireum.lang.ast.Exp.Old): Unit = {
      writer.writeZ(Constants._astExpOld)
      write_astExp(o.exp)
      write_astAttr(o.attr)
    }

    def write_astExpRS(o: org.sireum.lang.ast.Exp.RS): Unit = {
      writer.writeZ(Constants._astExpRS)
      writer.writeB(o.rightToLeft)
      writer.writeISZ(o.refs, write_astExpRef _)
      write_astAttr(o.attr)
    }

    def write_astExpAt(o: org.sireum.lang.ast.Exp.At): Unit = {
      writer.writeZ(Constants._astExpAt)
      writer.writeOption(o.tipeOpt, write_astType _)
      write_astExp(o.exp)
      write_astExpLitZ(o.num)
      writer.writeISZ(o.linesFresh, write_astExpLitZ _)
      write_astAttr(o.attr)
    }

    def write_astExpLoopIndex(o: org.sireum.lang.ast.Exp.LoopIndex): Unit = {
      writer.writeZ(Constants._astExpLoopIndex)
      writer.writeOption(o.tipeOpt, write_astType _)
      write_astExpIdent(o.exp)
      write_astTypedAttr(o.attr)
    }

    def write_astExpStateSeq(o: org.sireum.lang.ast.Exp.StateSeq): Unit = {
      writer.writeZ(Constants._astExpStateSeq)
      write_astId(o.id)
      writer.writeISZ(o.fragments, write_astExpStateSeqFragment _)
      write_astAttr(o.attr)
    }

    def write_astExpStateSeqFragment(o: org.sireum.lang.ast.Exp.StateSeq.Fragment): Unit = {
      writer.writeZ(Constants._astExpStateSeqFragment)
      write_astId(o.id)
      write_astExp(o.exp)
    }

    def write_astExpResult(o: org.sireum.lang.ast.Exp.Result): Unit = {
      writer.writeZ(Constants._astExpResult)
      writer.writeOption(o.tipeOpt, write_astType _)
      write_astTypedAttr(o.attr)
    }

    def write_astExpStrictPureBlock(o: org.sireum.lang.ast.Exp.StrictPureBlock): Unit = {
      writer.writeZ(Constants._astExpStrictPureBlock)
      write_astStmtBlock(o.block)
      write_astTypedAttr(o.attr)
    }

    def write_astExpLabeled(o: org.sireum.lang.ast.Exp.Labeled): Unit = {
      writer.writeZ(Constants._astExpLabeled)
      writer.writeOption(o.numOpt, write_astExpLitZ _)
      write_astExp(o.exp)
      write_astAttr(o.attr)
    }

    def write_astExpAssumeAgree(o: org.sireum.lang.ast.Exp.AssumeAgree): Unit = {
      writer.writeZ(Constants._astExpAssumeAgree)
      write_astExpLitString(o.channel)
      write_astMethodContractClaims(o.requiresClause)
      write_astMethodContractClaims(o.inAgreeClause)
      write_astAttr(o.attr)
    }

    def write_astExpAssertAgree(o: org.sireum.lang.ast.Exp.AssertAgree): Unit = {
      writer.writeZ(Constants._astExpAssertAgree)
      write_astExpLitString(o.channel)
      write_astMethodContractClaims(o.outAgreeClause)
      write_astAttr(o.attr)
    }

    def write_astExpInfoFlowInvariant(o: org.sireum.lang.ast.Exp.InfoFlowInvariant): Unit = {
      writer.writeZ(Constants._astExpInfoFlowInvariant)
      writer.writeISZ(o.flowInvariants, write_astMethodContractInfoFlowCase _)
      write_astAttr(o.attr)
    }

    def write_astNamedArg(o: org.sireum.lang.ast.NamedArg): Unit = {
      writer.writeZ(Constants._astNamedArg)
      write_astId(o.id)
      write_astExp(o.arg)
      writer.writeZ(o.index)
    }

    def write_astId(o: org.sireum.lang.ast.Id): Unit = {
      writer.writeZ(Constants._astId)
      writer.writeString(o.value)
      write_astAttr(o.attr)
    }

    def write_astName(o: org.sireum.lang.ast.Name): Unit = {
      writer.writeZ(Constants._astName)
      writer.writeISZ(o.ids, write_astId _)
      write_astAttr(o.attr)
    }

    def write_astBody(o: org.sireum.lang.ast.Body): Unit = {
      writer.writeZ(Constants._astBody)
      writer.writeISZ(o.stmts, write_astStmt _)
      writer.writeISZ(o.undecls, write_astResolvedInfoLocalVar _)
    }

    def write_astAdtParam(o: org.sireum.lang.ast.AdtParam): Unit = {
      writer.writeZ(Constants._astAdtParam)
      writer.writeB(o.isHidden)
      writer.writeB(o.isVal)
      write_astId(o.id)
      write_astType(o.tipe)
    }

    def write_astAnnotation(o: org.sireum.lang.ast.Annotation): Unit = {
      writer.writeZ(Constants._astAnnotation)
      writer.writeISZ(o.name, writer.writeString _)
      writer.writeISZ(o.args, write_astLit _)
    }

    def write_astMethodSig(o: org.sireum.lang.ast.MethodSig): Unit = {
      writer.writeZ(Constants._astMethodSig)
      write_astPurityType(o.purity)
      writer.writeISZ(o.annotations, write_astAnnotation _)
      write_astId(o.id)
      writer.writeISZ(o.typeParams, write_astTypeParam _)
      writer.writeB(o.hasParams)
      writer.writeISZ(o.params, write_astParam _)
      write_astType(o.returnType)
    }

    def write_astParam(o: org.sireum.lang.ast.Param): Unit = {
      writer.writeZ(Constants._astParam)
      writer.writeB(o.isHidden)
      write_astId(o.id)
      write_astType(o.tipe)
    }

    def write_astTypeParam(o: org.sireum.lang.ast.TypeParam): Unit = {
      writer.writeZ(Constants._astTypeParam)
      write_astId(o.id)
      write_astTypedVarKindType(o.kind)
    }

    def write_astAttr(o: org.sireum.lang.ast.Attr): Unit = {
      writer.writeZ(Constants._astAttr)
      writer.writeOption(o.posOpt, writer.writePosition _)
    }

    def write_astTypedAttr(o: org.sireum.lang.ast.TypedAttr): Unit = {
      writer.writeZ(Constants._astTypedAttr)
      writer.writeOption(o.posOpt, writer.writePosition _)
      writer.writeOption(o.typedOpt, write_astTyped _)
    }

    def write_astResolvedAttr(o: org.sireum.lang.ast.ResolvedAttr): Unit = {
      writer.writeZ(Constants._astResolvedAttr)
      writer.writeOption(o.posOpt, writer.writePosition _)
      writer.writeOption(o.resOpt, write_astResolvedInfo _)
      writer.writeOption(o.typedOpt, write_astTyped _)
    }

    def write_astResolvedInfo(o: org.sireum.lang.ast.ResolvedInfo): Unit = {
      o match {
        case o: org.sireum.lang.ast.ResolvedInfo.BuiltIn => write_astResolvedInfoBuiltIn(o)
        case o: org.sireum.lang.ast.ResolvedInfo.Package => write_astResolvedInfoPackage(o)
        case o: org.sireum.lang.ast.ResolvedInfo.Enum => write_astResolvedInfoEnum(o)
        case o: org.sireum.lang.ast.ResolvedInfo.EnumElement => write_astResolvedInfoEnumElement(o)
        case o: org.sireum.lang.ast.ResolvedInfo.Object => write_astResolvedInfoObject(o)
        case o: org.sireum.lang.ast.ResolvedInfo.Var => write_astResolvedInfoVar(o)
        case o: org.sireum.lang.ast.ResolvedInfo.Method => write_astResolvedInfoMethod(o)
        case o: org.sireum.lang.ast.ResolvedInfo.Methods => write_astResolvedInfoMethods(o)
        case o: org.sireum.lang.ast.ResolvedInfo.Tuple => write_astResolvedInfoTuple(o)
        case o: org.sireum.lang.ast.ResolvedInfo.LocalVar => write_astResolvedInfoLocalVar(o)
        case o: org.sireum.lang.ast.ResolvedInfo.Fact => write_astResolvedInfoFact(o)
        case o: org.sireum.lang.ast.ResolvedInfo.Theorem => write_astResolvedInfoTheorem(o)
        case o: org.sireum.lang.ast.ResolvedInfo.Inv => write_astResolvedInfoInv(o)
      }
    }

    def write_astResolvedInfoBuiltInKindType(o: org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.Type): Unit = {
      writer.writeZ(o.ordinal)
    }

    def write_astResolvedInfoBuiltIn(o: org.sireum.lang.ast.ResolvedInfo.BuiltIn): Unit = {
      writer.writeZ(Constants._astResolvedInfoBuiltIn)
      write_astResolvedInfoBuiltInKindType(o.kind)
    }

    def write_astResolvedInfoPackage(o: org.sireum.lang.ast.ResolvedInfo.Package): Unit = {
      writer.writeZ(Constants._astResolvedInfoPackage)
      writer.writeISZ(o.name, writer.writeString _)
    }

    def write_astResolvedInfoEnum(o: org.sireum.lang.ast.ResolvedInfo.Enum): Unit = {
      writer.writeZ(Constants._astResolvedInfoEnum)
      writer.writeISZ(o.name, writer.writeString _)
    }

    def write_astResolvedInfoEnumElement(o: org.sireum.lang.ast.ResolvedInfo.EnumElement): Unit = {
      writer.writeZ(Constants._astResolvedInfoEnumElement)
      writer.writeISZ(o.owner, writer.writeString _)
      writer.writeString(o.name)
      writer.writeZ(o.ordinal)
    }

    def write_astResolvedInfoObject(o: org.sireum.lang.ast.ResolvedInfo.Object): Unit = {
      writer.writeZ(Constants._astResolvedInfoObject)
      writer.writeISZ(o.name, writer.writeString _)
    }

    def write_astResolvedInfoVar(o: org.sireum.lang.ast.ResolvedInfo.Var): Unit = {
      writer.writeZ(Constants._astResolvedInfoVar)
      writer.writeB(o.isInObject)
      writer.writeB(o.isSpec)
      writer.writeB(o.isVal)
      writer.writeISZ(o.owner, writer.writeString _)
      writer.writeString(o.id)
    }

    def write_astResolvedInfoMethod(o: org.sireum.lang.ast.ResolvedInfo.Method): Unit = {
      writer.writeZ(Constants._astResolvedInfoMethod)
      writer.writeB(o.isInObject)
      write_astMethodModeType(o.mode)
      writer.writeISZ(o.typeParams, writer.writeString _)
      writer.writeISZ(o.owner, writer.writeString _)
      writer.writeString(o.id)
      writer.writeISZ(o.paramNames, writer.writeString _)
      writer.writeOption(o.tpeOpt, write_astTypedFun _)
      writer.writeISZ(o.reads, write_astResolvedInfo _)
      writer.writeISZ(o.writes, write_astResolvedInfo _)
    }

    def write_astResolvedInfoMethods(o: org.sireum.lang.ast.ResolvedInfo.Methods): Unit = {
      writer.writeZ(Constants._astResolvedInfoMethods)
      writer.writeISZ(o.methods, write_astResolvedInfoMethod _)
    }

    def write_astResolvedInfoTuple(o: org.sireum.lang.ast.ResolvedInfo.Tuple): Unit = {
      writer.writeZ(Constants._astResolvedInfoTuple)
      writer.writeZ(o.size)
      writer.writeZ(o.index)
    }

    def write_astResolvedInfoLocalVarScopeType(o: org.sireum.lang.ast.ResolvedInfo.LocalVar.Scope.Type): Unit = {
      writer.writeZ(o.ordinal)
    }

    def write_astResolvedInfoLocalVar(o: org.sireum.lang.ast.ResolvedInfo.LocalVar): Unit = {
      writer.writeZ(Constants._astResolvedInfoLocalVar)
      writer.writeISZ(o.context, writer.writeString _)
      write_astResolvedInfoLocalVarScopeType(o.scope)
      writer.writeB(o.isSpec)
      writer.writeB(o.isVal)
      writer.writeString(o.id)
    }

    def write_astResolvedInfoFact(o: org.sireum.lang.ast.ResolvedInfo.Fact): Unit = {
      writer.writeZ(Constants._astResolvedInfoFact)
      writer.writeISZ(o.name, writer.writeString _)
    }

    def write_astResolvedInfoTheorem(o: org.sireum.lang.ast.ResolvedInfo.Theorem): Unit = {
      writer.writeZ(Constants._astResolvedInfoTheorem)
      writer.writeISZ(o.name, writer.writeString _)
    }

    def write_astResolvedInfoInv(o: org.sireum.lang.ast.ResolvedInfo.Inv): Unit = {
      writer.writeZ(Constants._astResolvedInfoInv)
      writer.writeB(o.isInObject)
      writer.writeISZ(o.owner, writer.writeString _)
      writer.writeString(o.id)
    }

    def write_astTruthTableRow(o: org.sireum.lang.ast.TruthTable.Row): Unit = {
      writer.writeZ(Constants._astTruthTableRow)
      write_astTruthTableAssignment(o.assignment)
      writer.writePosition(o.separator)
      write_astTruthTableAssignment(o.values)
    }

    def write_astTruthTableAssignment(o: org.sireum.lang.ast.TruthTable.Assignment): Unit = {
      writer.writeZ(Constants._astTruthTableAssignment)
      writer.writeISZ(o.values, write_astExpLitB _)
      write_astAttr(o.attr)
    }

    def write_astTruthTableConclusion(o: org.sireum.lang.ast.TruthTable.Conclusion): Unit = {
      o match {
        case o: org.sireum.lang.ast.TruthTable.Conclusion.Validity => write_astTruthTableConclusionValidity(o)
        case o: org.sireum.lang.ast.TruthTable.Conclusion.Tautology => write_astTruthTableConclusionTautology(o)
        case o: org.sireum.lang.ast.TruthTable.Conclusion.Contradictory => write_astTruthTableConclusionContradictory(o)
        case o: org.sireum.lang.ast.TruthTable.Conclusion.Contingent => write_astTruthTableConclusionContingent(o)
      }
    }

    def write_astTruthTableConclusionValidity(o: org.sireum.lang.ast.TruthTable.Conclusion.Validity): Unit = {
      writer.writeZ(Constants._astTruthTableConclusionValidity)
      writer.writeB(o.isValid)
      writer.writeISZ(o.assignments, write_astTruthTableAssignment _)
      write_astAttr(o.attr)
    }

    def write_astTruthTableConclusionTautology(o: org.sireum.lang.ast.TruthTable.Conclusion.Tautology): Unit = {
      writer.writeZ(Constants._astTruthTableConclusionTautology)
      write_astAttr(o.attr)
    }

    def write_astTruthTableConclusionContradictory(o: org.sireum.lang.ast.TruthTable.Conclusion.Contradictory): Unit = {
      writer.writeZ(Constants._astTruthTableConclusionContradictory)
      write_astAttr(o.attr)
    }

    def write_astTruthTableConclusionContingent(o: org.sireum.lang.ast.TruthTable.Conclusion.Contingent): Unit = {
      writer.writeZ(Constants._astTruthTableConclusionContingent)
      writer.writeISZ(o.trueAssignments, write_astTruthTableAssignment _)
      writer.writeISZ(o.falseAssignments, write_astTruthTableAssignment _)
      write_astAttr(o.attr)
    }

    def write_astPurityType(o: org.sireum.lang.ast.Purity.Type): Unit = {
      writer.writeZ(o.ordinal)
    }

    def write_astMethodModeType(o: org.sireum.lang.ast.MethodMode.Type): Unit = {
      writer.writeZ(o.ordinal)
    }

    def write_astTyped(o: org.sireum.lang.ast.Typed): Unit = {
      o match {
        case o: org.sireum.lang.ast.Typed.Name => write_astTypedName(o)
        case o: org.sireum.lang.ast.Typed.Tuple => write_astTypedTuple(o)
        case o: org.sireum.lang.ast.Typed.Fun => write_astTypedFun(o)
        case o: org.sireum.lang.ast.Typed.TypeVar => write_astTypedTypeVar(o)
        case o: org.sireum.lang.ast.Typed.Package => write_astTypedPackage(o)
        case o: org.sireum.lang.ast.Typed.Object => write_astTypedObject(o)
        case o: org.sireum.lang.ast.Typed.Enum => write_astTypedEnum(o)
        case o: org.sireum.lang.ast.Typed.Method => write_astTypedMethod(o)
        case o: org.sireum.lang.ast.Typed.Methods => write_astTypedMethods(o)
        case o: org.sireum.lang.ast.Typed.Fact => write_astTypedFact(o)
        case o: org.sireum.lang.ast.Typed.Theorem => write_astTypedTheorem(o)
        case o: org.sireum.lang.ast.Typed.Inv => write_astTypedInv(o)
      }
    }

    def write_astTypedVarKindType(o: org.sireum.lang.ast.Typed.VarKind.Type): Unit = {
      writer.writeZ(o.ordinal)
    }

    def write_astTypedName(o: org.sireum.lang.ast.Typed.Name): Unit = {
      writer.writeZ(Constants._astTypedName)
      writer.writeISZ(o.ids, writer.writeString _)
      writer.writeISZ(o.args, write_astTyped _)
    }

    def write_astTypedTuple(o: org.sireum.lang.ast.Typed.Tuple): Unit = {
      writer.writeZ(Constants._astTypedTuple)
      writer.writeISZ(o.args, write_astTyped _)
    }

    def write_astTypedFun(o: org.sireum.lang.ast.Typed.Fun): Unit = {
      writer.writeZ(Constants._astTypedFun)
      write_astPurityType(o.purity)
      writer.writeB(o.isByName)
      writer.writeISZ(o.args, write_astTyped _)
      write_astTyped(o.ret)
    }

    def write_astTypedTypeVar(o: org.sireum.lang.ast.Typed.TypeVar): Unit = {
      writer.writeZ(Constants._astTypedTypeVar)
      writer.writeString(o.id)
      write_astTypedVarKindType(o.kind)
    }

    def write_astTypedPackage(o: org.sireum.lang.ast.Typed.Package): Unit = {
      writer.writeZ(Constants._astTypedPackage)
      writer.writeISZ(o.name, writer.writeString _)
    }

    def write_astTypedObject(o: org.sireum.lang.ast.Typed.Object): Unit = {
      writer.writeZ(Constants._astTypedObject)
      writer.writeISZ(o.owner, writer.writeString _)
      writer.writeString(o.id)
    }

    def write_astTypedEnum(o: org.sireum.lang.ast.Typed.Enum): Unit = {
      writer.writeZ(Constants._astTypedEnum)
      writer.writeISZ(o.name, writer.writeString _)
    }

    def write_astTypedMethod(o: org.sireum.lang.ast.Typed.Method): Unit = {
      writer.writeZ(Constants._astTypedMethod)
      writer.writeB(o.isInObject)
      write_astMethodModeType(o.mode)
      writer.writeISZ(o.typeParams, writer.writeString _)
      writer.writeISZ(o.owner, writer.writeString _)
      writer.writeString(o.name)
      writer.writeISZ(o.paramNames, writer.writeString _)
      write_astTypedFun(o.tpe)
    }

    def write_astTypedMethods(o: org.sireum.lang.ast.Typed.Methods): Unit = {
      writer.writeZ(Constants._astTypedMethods)
      writer.writeISZ(o.methods, write_astTypedMethod _)
    }

    def write_astTypedFact(o: org.sireum.lang.ast.Typed.Fact): Unit = {
      writer.writeZ(Constants._astTypedFact)
      writer.writeISZ(o.owner, writer.writeString _)
      writer.writeString(o.id)
    }

    def write_astTypedTheorem(o: org.sireum.lang.ast.Typed.Theorem): Unit = {
      writer.writeZ(Constants._astTypedTheorem)
      writer.writeISZ(o.owner, writer.writeString _)
      writer.writeString(o.id)
    }

    def write_astTypedInv(o: org.sireum.lang.ast.Typed.Inv): Unit = {
      writer.writeZ(Constants._astTypedInv)
      writer.writeB(o.isInObject)
      writer.writeISZ(o.owner, writer.writeString _)
      writer.writeString(o.id)
    }

    def result: ISZ[U8] = {
      return writer.result
    }

  }

  object Reader {

    @record class Default(val reader: MessagePack.Reader.Impl) extends Reader {
      def errorOpt: Option[MessagePack.ErrorMsg] = {
        return reader.errorOpt
      }
    }

  }

  @msig trait Reader {

    def reader: MessagePack.Reader

    def read_symbolScope(): org.sireum.lang.symbol.Scope = {
      val i = reader.curr
      val t = reader.readZ()
      t match {
        case Constants._symbolScopeLocal => val r = read_symbolScopeLocalT(T); return r
        case Constants._symbolScopeGlobal => val r = read_symbolScopeGlobalT(T); return r
        case _ =>
          reader.error(i, s"$t is not a valid type of org.sireum.lang.symbol.Scope.")
          val r = read_symbolScopeGlobalT(T)
          return r
      }
    }

    def read_symbolScopeLocal(): org.sireum.lang.symbol.Scope.Local = {
      val r = read_symbolScopeLocalT(F)
      return r
    }

    def read_symbolScopeLocalT(typeParsed: B): org.sireum.lang.symbol.Scope.Local = {
      if (!typeParsed) {
        reader.expectZ(Constants._symbolScopeLocal)
      }
      val nameMap = reader.readHashMap(reader.readString _, read_symbolInfo _)
      val typeMap = reader.readHashMap(reader.readString _, read_symbolTypeInfo _)
      val localThisOpt = reader.readOption(read_astTyped _)
      val methodReturnOpt = reader.readOption(read_astTyped _)
      val indexMap = reader.readHashMap(reader.readString _, read_astTyped _)
      val outerOpt = reader.readOption(read_symbolScope _)
      return org.sireum.lang.symbol.Scope.Local(nameMap, typeMap, localThisOpt, methodReturnOpt, indexMap, outerOpt)
    }

    def read_symbolScopeGlobal(): org.sireum.lang.symbol.Scope.Global = {
      val r = read_symbolScopeGlobalT(F)
      return r
    }

    def read_symbolScopeGlobalT(typeParsed: B): org.sireum.lang.symbol.Scope.Global = {
      if (!typeParsed) {
        reader.expectZ(Constants._symbolScopeGlobal)
      }
      val packageName = reader.readISZ(reader.readString _)
      val imports = reader.readISZ(read_astStmtImport _)
      val enclosingName = reader.readISZ(reader.readString _)
      return org.sireum.lang.symbol.Scope.Global(packageName, imports, enclosingName)
    }

    def read_symbolInfo(): org.sireum.lang.symbol.Info = {
      val i = reader.curr
      val t = reader.readZ()
      t match {
        case Constants._symbolInfoPackage => val r = read_symbolInfoPackageT(T); return r
        case Constants._symbolInfoVar => val r = read_symbolInfoVarT(T); return r
        case Constants._symbolInfoSpecVar => val r = read_symbolInfoSpecVarT(T); return r
        case Constants._symbolInfoRsVal => val r = read_symbolInfoRsValT(T); return r
        case Constants._symbolInfoMethod => val r = read_symbolInfoMethodT(T); return r
        case Constants._symbolInfoSpecMethod => val r = read_symbolInfoSpecMethodT(T); return r
        case Constants._symbolInfoObject => val r = read_symbolInfoObjectT(T); return r
        case Constants._symbolInfoExtMethod => val r = read_symbolInfoExtMethodT(T); return r
        case Constants._symbolInfoJustMethod => val r = read_symbolInfoJustMethodT(T); return r
        case Constants._symbolInfoEnum => val r = read_symbolInfoEnumT(T); return r
        case Constants._symbolInfoEnumElement => val r = read_symbolInfoEnumElementT(T); return r
        case Constants._symbolInfoLocalVar => val r = read_symbolInfoLocalVarT(T); return r
        case Constants._symbolInfoFact => val r = read_symbolInfoFactT(T); return r
        case Constants._symbolInfoTheorem => val r = read_symbolInfoTheoremT(T); return r
        case Constants._symbolInfoInv => val r = read_symbolInfoInvT(T); return r
        case _ =>
          reader.error(i, s"$t is not a valid type of org.sireum.lang.symbol.Info.")
          val r = read_symbolInfoInvT(T)
          return r
      }
    }

    def read_symbolInfoPackage(): org.sireum.lang.symbol.Info.Package = {
      val r = read_symbolInfoPackageT(F)
      return r
    }

    def read_symbolInfoPackageT(typeParsed: B): org.sireum.lang.symbol.Info.Package = {
      if (!typeParsed) {
        reader.expectZ(Constants._symbolInfoPackage)
      }
      val name = reader.readISZ(reader.readString _)
      val typedOpt = reader.readOption(read_astTyped _)
      val resOpt = reader.readOption(read_astResolvedInfo _)
      return org.sireum.lang.symbol.Info.Package(name, typedOpt, resOpt)
    }

    def read_symbolInfoVar(): org.sireum.lang.symbol.Info.Var = {
      val r = read_symbolInfoVarT(F)
      return r
    }

    def read_symbolInfoVarT(typeParsed: B): org.sireum.lang.symbol.Info.Var = {
      if (!typeParsed) {
        reader.expectZ(Constants._symbolInfoVar)
      }
      val owner = reader.readISZ(reader.readString _)
      val isInObject = reader.readB()
      val scope = read_symbolScope()
      val ast = read_astStmtVar()
      return org.sireum.lang.symbol.Info.Var(owner, isInObject, scope, ast)
    }

    def read_symbolInfoSpecVar(): org.sireum.lang.symbol.Info.SpecVar = {
      val r = read_symbolInfoSpecVarT(F)
      return r
    }

    def read_symbolInfoSpecVarT(typeParsed: B): org.sireum.lang.symbol.Info.SpecVar = {
      if (!typeParsed) {
        reader.expectZ(Constants._symbolInfoSpecVar)
      }
      val owner = reader.readISZ(reader.readString _)
      val isInObject = reader.readB()
      val scope = read_symbolScope()
      val ast = read_astStmtSpecVar()
      return org.sireum.lang.symbol.Info.SpecVar(owner, isInObject, scope, ast)
    }

    def read_symbolInfoRsVal(): org.sireum.lang.symbol.Info.RsVal = {
      val r = read_symbolInfoRsValT(F)
      return r
    }

    def read_symbolInfoRsValT(typeParsed: B): org.sireum.lang.symbol.Info.RsVal = {
      if (!typeParsed) {
        reader.expectZ(Constants._symbolInfoRsVal)
      }
      val owner = reader.readISZ(reader.readString _)
      val scope = read_symbolScope()
      val ast = read_astStmtRsVal()
      return org.sireum.lang.symbol.Info.RsVal(owner, scope, ast)
    }

    def read_symbolInfoMethod(): org.sireum.lang.symbol.Info.Method = {
      val r = read_symbolInfoMethodT(F)
      return r
    }

    def read_symbolInfoMethodT(typeParsed: B): org.sireum.lang.symbol.Info.Method = {
      if (!typeParsed) {
        reader.expectZ(Constants._symbolInfoMethod)
      }
      val owner = reader.readISZ(reader.readString _)
      val isInObject = reader.readB()
      val scope = read_symbolScope()
      val hasBody = reader.readB()
      val ast = read_astStmtMethod()
      return org.sireum.lang.symbol.Info.Method(owner, isInObject, scope, hasBody, ast)
    }

    def read_symbolInfoSpecMethod(): org.sireum.lang.symbol.Info.SpecMethod = {
      val r = read_symbolInfoSpecMethodT(F)
      return r
    }

    def read_symbolInfoSpecMethodT(typeParsed: B): org.sireum.lang.symbol.Info.SpecMethod = {
      if (!typeParsed) {
        reader.expectZ(Constants._symbolInfoSpecMethod)
      }
      val owner = reader.readISZ(reader.readString _)
      val isInObject = reader.readB()
      val scope = read_symbolScope()
      val ast = read_astStmtSpecMethod()
      return org.sireum.lang.symbol.Info.SpecMethod(owner, isInObject, scope, ast)
    }

    def read_symbolInfoObject(): org.sireum.lang.symbol.Info.Object = {
      val r = read_symbolInfoObjectT(F)
      return r
    }

    def read_symbolInfoObjectT(typeParsed: B): org.sireum.lang.symbol.Info.Object = {
      if (!typeParsed) {
        reader.expectZ(Constants._symbolInfoObject)
      }
      val owner = reader.readISZ(reader.readString _)
      val isSynthetic = reader.readB()
      val scope = read_symbolScopeGlobal()
      val outlined = reader.readB()
      val contractOutlined = reader.readB()
      val typeChecked = reader.readB()
      val ast = read_astStmtObject()
      val typedOpt = reader.readOption(read_astTyped _)
      val resOpt = reader.readOption(read_astResolvedInfo _)
      val constructorRes = read_astResolvedInfoMethod()
      return org.sireum.lang.symbol.Info.Object(owner, isSynthetic, scope, outlined, contractOutlined, typeChecked, ast, typedOpt, resOpt, constructorRes)
    }

    def read_symbolInfoExtMethod(): org.sireum.lang.symbol.Info.ExtMethod = {
      val r = read_symbolInfoExtMethodT(F)
      return r
    }

    def read_symbolInfoExtMethodT(typeParsed: B): org.sireum.lang.symbol.Info.ExtMethod = {
      if (!typeParsed) {
        reader.expectZ(Constants._symbolInfoExtMethod)
      }
      val owner = reader.readISZ(reader.readString _)
      val scope = read_symbolScopeGlobal()
      val ast = read_astStmtExtMethod()
      return org.sireum.lang.symbol.Info.ExtMethod(owner, scope, ast)
    }

    def read_symbolInfoJustMethod(): org.sireum.lang.symbol.Info.JustMethod = {
      val r = read_symbolInfoJustMethodT(F)
      return r
    }

    def read_symbolInfoJustMethodT(typeParsed: B): org.sireum.lang.symbol.Info.JustMethod = {
      if (!typeParsed) {
        reader.expectZ(Constants._symbolInfoJustMethod)
      }
      val owner = reader.readISZ(reader.readString _)
      val scope = read_symbolScopeGlobal()
      val ast = read_astStmtJustMethod()
      return org.sireum.lang.symbol.Info.JustMethod(owner, scope, ast)
    }

    def read_symbolInfoEnum(): org.sireum.lang.symbol.Info.Enum = {
      val r = read_symbolInfoEnumT(F)
      return r
    }

    def read_symbolInfoEnumT(typeParsed: B): org.sireum.lang.symbol.Info.Enum = {
      if (!typeParsed) {
        reader.expectZ(Constants._symbolInfoEnum)
      }
      val name = reader.readISZ(reader.readString _)
      val elements = reader.readMap(reader.readString _, read_astResolvedInfo _)
      val typedOpt = reader.readOption(read_astTyped _)
      val resOpt = reader.readOption(read_astResolvedInfo _)
      val elementTypedOpt = reader.readOption(read_astTyped _)
      val posOpt = reader.readOption(reader.readPosition _)
      return org.sireum.lang.symbol.Info.Enum(name, elements, typedOpt, resOpt, elementTypedOpt, posOpt)
    }

    def read_symbolInfoEnumElement(): org.sireum.lang.symbol.Info.EnumElement = {
      val r = read_symbolInfoEnumElementT(F)
      return r
    }

    def read_symbolInfoEnumElementT(typeParsed: B): org.sireum.lang.symbol.Info.EnumElement = {
      if (!typeParsed) {
        reader.expectZ(Constants._symbolInfoEnumElement)
      }
      val owner = reader.readISZ(reader.readString _)
      val id = reader.readString()
      val typedOpt = reader.readOption(read_astTyped _)
      val resOpt = reader.readOption(read_astResolvedInfo _)
      val posOpt = reader.readOption(reader.readPosition _)
      return org.sireum.lang.symbol.Info.EnumElement(owner, id, typedOpt, resOpt, posOpt)
    }

    def read_symbolInfoLocalVar(): org.sireum.lang.symbol.Info.LocalVar = {
      val r = read_symbolInfoLocalVarT(F)
      return r
    }

    def read_symbolInfoLocalVarT(typeParsed: B): org.sireum.lang.symbol.Info.LocalVar = {
      if (!typeParsed) {
        reader.expectZ(Constants._symbolInfoLocalVar)
      }
      val name = reader.readISZ(reader.readString _)
      val isVal = reader.readB()
      val ast = read_astId()
      val typedOpt = reader.readOption(read_astTyped _)
      val initOpt = reader.readOption(read_astAssignExp _)
      val resOpt = reader.readOption(read_astResolvedInfo _)
      return org.sireum.lang.symbol.Info.LocalVar(name, isVal, ast, typedOpt, initOpt, resOpt)
    }

    def read_symbolInfoFact(): org.sireum.lang.symbol.Info.Fact = {
      val r = read_symbolInfoFactT(F)
      return r
    }

    def read_symbolInfoFactT(typeParsed: B): org.sireum.lang.symbol.Info.Fact = {
      if (!typeParsed) {
        reader.expectZ(Constants._symbolInfoFact)
      }
      val owner = reader.readISZ(reader.readString _)
      val id = reader.readString()
      val scope = read_symbolScopeGlobal()
      val ast = read_astStmtFact()
      return org.sireum.lang.symbol.Info.Fact(owner, id, scope, ast)
    }

    def read_symbolInfoTheorem(): org.sireum.lang.symbol.Info.Theorem = {
      val r = read_symbolInfoTheoremT(F)
      return r
    }

    def read_symbolInfoTheoremT(typeParsed: B): org.sireum.lang.symbol.Info.Theorem = {
      if (!typeParsed) {
        reader.expectZ(Constants._symbolInfoTheorem)
      }
      val owner = reader.readISZ(reader.readString _)
      val id = reader.readString()
      val scope = read_symbolScopeGlobal()
      val ast = read_astStmtTheorem()
      return org.sireum.lang.symbol.Info.Theorem(owner, id, scope, ast)
    }

    def read_symbolInfoInv(): org.sireum.lang.symbol.Info.Inv = {
      val r = read_symbolInfoInvT(F)
      return r
    }

    def read_symbolInfoInvT(typeParsed: B): org.sireum.lang.symbol.Info.Inv = {
      if (!typeParsed) {
        reader.expectZ(Constants._symbolInfoInv)
      }
      val owner = reader.readISZ(reader.readString _)
      val id = reader.readString()
      val scope = read_symbolScope()
      val ast = read_astStmtInv()
      return org.sireum.lang.symbol.Info.Inv(owner, id, scope, ast)
    }

    def read_symbolTypeInfo(): org.sireum.lang.symbol.TypeInfo = {
      val i = reader.curr
      val t = reader.readZ()
      t match {
        case Constants._symbolTypeInfoSubZ => val r = read_symbolTypeInfoSubZT(T); return r
        case Constants._symbolTypeInfoEnum => val r = read_symbolTypeInfoEnumT(T); return r
        case Constants._symbolTypeInfoSig => val r = read_symbolTypeInfoSigT(T); return r
        case Constants._symbolTypeInfoAdt => val r = read_symbolTypeInfoAdtT(T); return r
        case Constants._symbolTypeInfoTypeAlias => val r = read_symbolTypeInfoTypeAliasT(T); return r
        case Constants._symbolTypeInfoTypeVar => val r = read_symbolTypeInfoTypeVarT(T); return r
        case _ =>
          reader.error(i, s"$t is not a valid type of org.sireum.lang.symbol.TypeInfo.")
          val r = read_symbolTypeInfoTypeVarT(T)
          return r
      }
    }

    def read_symbolTypeInfoSubZ(): org.sireum.lang.symbol.TypeInfo.SubZ = {
      val r = read_symbolTypeInfoSubZT(F)
      return r
    }

    def read_symbolTypeInfoSubZT(typeParsed: B): org.sireum.lang.symbol.TypeInfo.SubZ = {
      if (!typeParsed) {
        reader.expectZ(Constants._symbolTypeInfoSubZ)
      }
      val owner = reader.readISZ(reader.readString _)
      val ast = read_astStmtSubZ()
      return org.sireum.lang.symbol.TypeInfo.SubZ(owner, ast)
    }

    def read_symbolTypeInfoEnum(): org.sireum.lang.symbol.TypeInfo.Enum = {
      val r = read_symbolTypeInfoEnumT(F)
      return r
    }

    def read_symbolTypeInfoEnumT(typeParsed: B): org.sireum.lang.symbol.TypeInfo.Enum = {
      if (!typeParsed) {
        reader.expectZ(Constants._symbolTypeInfoEnum)
      }
      val owner = reader.readISZ(reader.readString _)
      val elements = reader.readMap(reader.readString _, read_astResolvedInfo _)
      val posOpt = reader.readOption(reader.readPosition _)
      return org.sireum.lang.symbol.TypeInfo.Enum(owner, elements, posOpt)
    }

    def read_symbolTypeInfoSig(): org.sireum.lang.symbol.TypeInfo.Sig = {
      val r = read_symbolTypeInfoSigT(F)
      return r
    }

    def read_symbolTypeInfoSigT(typeParsed: B): org.sireum.lang.symbol.TypeInfo.Sig = {
      if (!typeParsed) {
        reader.expectZ(Constants._symbolTypeInfoSig)
      }
      val owner = reader.readISZ(reader.readString _)
      val outlined = reader.readB()
      val contractOutlined = reader.readB()
      val typeChecked = reader.readB()
      val tpe = read_astTypedName()
      val ancestors = reader.readISZ(read_astTypedName _)
      val specVars = reader.readHashSMap(reader.readString _, read_symbolInfoSpecVar _)
      val specMethods = reader.readHashSMap(reader.readString _, read_symbolInfoSpecMethod _)
      val methods = reader.readHashSMap(reader.readString _, read_symbolInfoMethod _)
      val refinements = reader.readHashSMap(reader.readString _, read_symbolTypeInfoName _)
      val invariants = reader.readHashSMap(reader.readString _, read_symbolInfoInv _)
      val dataRefinements = reader.readISZ(read_astStmtDataRefinement _)
      val scope = read_symbolScopeGlobal()
      val ast = read_astStmtSig()
      return org.sireum.lang.symbol.TypeInfo.Sig(owner, outlined, contractOutlined, typeChecked, tpe, ancestors, specVars, specMethods, methods, refinements, invariants, dataRefinements, scope, ast)
    }

    def read_symbolTypeInfoName(): org.sireum.lang.symbol.TypeInfo.Name = {
      val r = read_symbolTypeInfoNameT(F)
      return r
    }

    def read_symbolTypeInfoNameT(typeParsed: B): org.sireum.lang.symbol.TypeInfo.Name = {
      if (!typeParsed) {
        reader.expectZ(Constants._symbolTypeInfoName)
      }
      val ids = reader.readISZ(reader.readString _)
      return org.sireum.lang.symbol.TypeInfo.Name(ids)
    }

    def read_symbolTypeInfoAdt(): org.sireum.lang.symbol.TypeInfo.Adt = {
      val r = read_symbolTypeInfoAdtT(F)
      return r
    }

    def read_symbolTypeInfoAdtT(typeParsed: B): org.sireum.lang.symbol.TypeInfo.Adt = {
      if (!typeParsed) {
        reader.expectZ(Constants._symbolTypeInfoAdt)
      }
      val owner = reader.readISZ(reader.readString _)
      val outlined = reader.readB()
      val contractOutlined = reader.readB()
      val typeChecked = reader.readB()
      val tpe = read_astTypedName()
      val constructorTypeOpt = reader.readOption(read_astTyped _)
      val constructorResOpt = reader.readOption(read_astResolvedInfo _)
      val extractorTypeMap = reader.readMap(reader.readString _, read_astTyped _)
      val extractorResOpt = reader.readOption(read_astResolvedInfo _)
      val ancestors = reader.readISZ(read_astTypedName _)
      val specVars = reader.readHashSMap(reader.readString _, read_symbolInfoSpecVar _)
      val vars = reader.readHashSMap(reader.readString _, read_symbolInfoVar _)
      val specMethods = reader.readHashSMap(reader.readString _, read_symbolInfoSpecMethod _)
      val methods = reader.readHashSMap(reader.readString _, read_symbolInfoMethod _)
      val refinements = reader.readHashSMap(reader.readString _, read_symbolTypeInfoName _)
      val invariants = reader.readHashSMap(reader.readString _, read_symbolInfoInv _)
      val dataRefinements = reader.readISZ(read_astStmtDataRefinement _)
      val scope = read_symbolScopeGlobal()
      val ast = read_astStmtAdt()
      return org.sireum.lang.symbol.TypeInfo.Adt(owner, outlined, contractOutlined, typeChecked, tpe, constructorTypeOpt, constructorResOpt, extractorTypeMap, extractorResOpt, ancestors, specVars, vars, specMethods, methods, refinements, invariants, dataRefinements, scope, ast)
    }

    def read_symbolTypeInfoTypeAlias(): org.sireum.lang.symbol.TypeInfo.TypeAlias = {
      val r = read_symbolTypeInfoTypeAliasT(F)
      return r
    }

    def read_symbolTypeInfoTypeAliasT(typeParsed: B): org.sireum.lang.symbol.TypeInfo.TypeAlias = {
      if (!typeParsed) {
        reader.expectZ(Constants._symbolTypeInfoTypeAlias)
      }
      val name = reader.readISZ(reader.readString _)
      val scope = read_symbolScopeGlobal()
      val ast = read_astStmtTypeAlias()
      return org.sireum.lang.symbol.TypeInfo.TypeAlias(name, scope, ast)
    }

    def read_symbolTypeInfoTypeVar(): org.sireum.lang.symbol.TypeInfo.TypeVar = {
      val r = read_symbolTypeInfoTypeVarT(F)
      return r
    }

    def read_symbolTypeInfoTypeVarT(typeParsed: B): org.sireum.lang.symbol.TypeInfo.TypeVar = {
      if (!typeParsed) {
        reader.expectZ(Constants._symbolTypeInfoTypeVar)
      }
      val id = reader.readString()
      val ast = read_astTypeParam()
      return org.sireum.lang.symbol.TypeInfo.TypeVar(id, ast)
    }

    def read_symbolTypeInfoMembers(): org.sireum.lang.symbol.TypeInfo.Members = {
      val r = read_symbolTypeInfoMembersT(F)
      return r
    }

    def read_symbolTypeInfoMembersT(typeParsed: B): org.sireum.lang.symbol.TypeInfo.Members = {
      if (!typeParsed) {
        reader.expectZ(Constants._symbolTypeInfoMembers)
      }
      val specVars = reader.readHashSMap(reader.readString _, read_symbolInfoSpecVar _)
      val vars = reader.readHashSMap(reader.readString _, read_symbolInfoVar _)
      val specMethods = reader.readHashSMap(reader.readString _, read_symbolInfoSpecMethod _)
      val methods = reader.readHashSMap(reader.readString _, read_symbolInfoMethod _)
      val refinements = reader.readHashSMap(reader.readString _, read_symbolTypeInfoName _)
      val invariants = reader.readHashSMap(reader.readString _, read_symbolInfoInv _)
      return org.sireum.lang.symbol.TypeInfo.Members(specVars, vars, specMethods, methods, refinements, invariants)
    }

    def read_astTopUnit(): org.sireum.lang.ast.TopUnit = {
      val i = reader.curr
      val t = reader.readZ()
      t match {
        case Constants._astTopUnitProgram => val r = read_astTopUnitProgramT(T); return r
        case Constants._astTopUnitTruthTableUnit => val r = read_astTopUnitTruthTableUnitT(T); return r
        case _ =>
          reader.error(i, s"$t is not a valid type of org.sireum.lang.ast.TopUnit.")
          val r = read_astTopUnitTruthTableUnitT(T)
          return r
      }
    }

    def read_astTopUnitProgram(): org.sireum.lang.ast.TopUnit.Program = {
      val r = read_astTopUnitProgramT(F)
      return r
    }

    def read_astTopUnitProgramT(typeParsed: B): org.sireum.lang.ast.TopUnit.Program = {
      if (!typeParsed) {
        reader.expectZ(Constants._astTopUnitProgram)
      }
      val fileUriOpt = reader.readOption(reader.readString _)
      val packageName = read_astName()
      val body = read_astBody()
      return org.sireum.lang.ast.TopUnit.Program(fileUriOpt, packageName, body)
    }

    def read_astTopUnitTruthTableUnit(): org.sireum.lang.ast.TopUnit.TruthTableUnit = {
      val r = read_astTopUnitTruthTableUnitT(F)
      return r
    }

    def read_astTopUnitTruthTableUnitT(typeParsed: B): org.sireum.lang.ast.TopUnit.TruthTableUnit = {
      if (!typeParsed) {
        reader.expectZ(Constants._astTopUnitTruthTableUnit)
      }
      val fileUriOpt = reader.readOption(reader.readString _)
      val stars = reader.readISZ(reader.readPosition _)
      val vars = reader.readISZ(read_astId _)
      val separator = reader.readPosition()
      val isSequent = reader.readB()
      val sequent = read_astSequent()
      val rows = reader.readISZ(read_astTruthTableRow _)
      val conclusionOpt = reader.readOption(read_astTruthTableConclusion _)
      return org.sireum.lang.ast.TopUnit.TruthTableUnit(fileUriOpt, stars, vars, separator, isSequent, sequent, rows, conclusionOpt)
    }

    def read_astStmt(): org.sireum.lang.ast.Stmt = {
      val i = reader.curr
      val t = reader.readZ()
      t match {
        case Constants._astStmtImport => val r = read_astStmtImportT(T); return r
        case Constants._astStmtVar => val r = read_astStmtVarT(T); return r
        case Constants._astStmtVarPattern => val r = read_astStmtVarPatternT(T); return r
        case Constants._astStmtSpecVar => val r = read_astStmtSpecVarT(T); return r
        case Constants._astStmtRsVal => val r = read_astStmtRsValT(T); return r
        case Constants._astStmtMethod => val r = read_astStmtMethodT(T); return r
        case Constants._astStmtExtMethod => val r = read_astStmtExtMethodT(T); return r
        case Constants._astStmtJustMethod => val r = read_astStmtJustMethodT(T); return r
        case Constants._astStmtSpecMethod => val r = read_astStmtSpecMethodT(T); return r
        case Constants._astStmtEnum => val r = read_astStmtEnumT(T); return r
        case Constants._astStmtSubZ => val r = read_astStmtSubZT(T); return r
        case Constants._astStmtObject => val r = read_astStmtObjectT(T); return r
        case Constants._astStmtSig => val r = read_astStmtSigT(T); return r
        case Constants._astStmtAdt => val r = read_astStmtAdtT(T); return r
        case Constants._astStmtTypeAlias => val r = read_astStmtTypeAliasT(T); return r
        case Constants._astStmtAssign => val r = read_astStmtAssignT(T); return r
        case Constants._astStmtBlock => val r = read_astStmtBlockT(T); return r
        case Constants._astStmtIf => val r = read_astStmtIfT(T); return r
        case Constants._astStmtInduct => val r = read_astStmtInductT(T); return r
        case Constants._astStmtMatch => val r = read_astStmtMatchT(T); return r
        case Constants._astStmtWhile => val r = read_astStmtWhileT(T); return r
        case Constants._astStmtFor => val r = read_astStmtForT(T); return r
        case Constants._astStmtReturn => val r = read_astStmtReturnT(T); return r
        case Constants._astStmtExpr => val r = read_astStmtExprT(T); return r
        case Constants._astStmtFact => val r = read_astStmtFactT(T); return r
        case Constants._astStmtInv => val r = read_astStmtInvT(T); return r
        case Constants._astStmtTheorem => val r = read_astStmtTheoremT(T); return r
        case Constants._astStmtDataRefinement => val r = read_astStmtDataRefinementT(T); return r
        case Constants._astStmtSpecLabel => val r = read_astStmtSpecLabelT(T); return r
        case Constants._astStmtSpecBlock => val r = read_astStmtSpecBlockT(T); return r
        case Constants._astStmtDeduceSequent => val r = read_astStmtDeduceSequentT(T); return r
        case Constants._astStmtDeduceSteps => val r = read_astStmtDeduceStepsT(T); return r
        case Constants._astStmtHavoc => val r = read_astStmtHavocT(T); return r
        case _ =>
          reader.error(i, s"$t is not a valid type of org.sireum.lang.ast.Stmt.")
          val r = read_astStmtHavocT(T)
          return r
      }
    }

    def read_astHasModifies(): org.sireum.lang.ast.HasModifies = {
      val i = reader.curr
      val t = reader.readZ()
      t match {
        case Constants._astLoopContract => val r = read_astLoopContractT(T); return r
        case Constants._astMethodContractSimple => val r = read_astMethodContractSimpleT(T); return r
        case Constants._astMethodContractCases => val r = read_astMethodContractCasesT(T); return r
        case _ =>
          reader.error(i, s"$t is not a valid type of org.sireum.lang.ast.HasModifies.")
          val r = read_astMethodContractCasesT(T)
          return r
      }
    }

    def read_astLoopContract(): org.sireum.lang.ast.LoopContract = {
      val r = read_astLoopContractT(F)
      return r
    }

    def read_astLoopContractT(typeParsed: B): org.sireum.lang.ast.LoopContract = {
      if (!typeParsed) {
        reader.expectZ(Constants._astLoopContract)
      }
      val invariants = reader.readISZ(read_astExp _)
      val modifies = reader.readISZ(read_astExpRef _)
      val maxItOpt = reader.readOption(read_astExpLitZ _)
      return org.sireum.lang.ast.LoopContract(invariants, modifies, maxItOpt)
    }

    def read_astStmtImport(): org.sireum.lang.ast.Stmt.Import = {
      val r = read_astStmtImportT(F)
      return r
    }

    def read_astStmtImportT(typeParsed: B): org.sireum.lang.ast.Stmt.Import = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtImport)
      }
      val importers = reader.readISZ(read_astStmtImportImporter _)
      val attr = read_astAttr()
      return org.sireum.lang.ast.Stmt.Import(importers, attr)
    }

    def read_astStmtImportImporter(): org.sireum.lang.ast.Stmt.Import.Importer = {
      val r = read_astStmtImportImporterT(F)
      return r
    }

    def read_astStmtImportImporterT(typeParsed: B): org.sireum.lang.ast.Stmt.Import.Importer = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtImportImporter)
      }
      val name = read_astName()
      val selectorOpt = reader.readOption(read_astStmtImportSelector _)
      return org.sireum.lang.ast.Stmt.Import.Importer(name, selectorOpt)
    }

    def read_astStmtImportSelector(): org.sireum.lang.ast.Stmt.Import.Selector = {
      val i = reader.curr
      val t = reader.readZ()
      t match {
        case Constants._astStmtImportMultiSelector => val r = read_astStmtImportMultiSelectorT(T); return r
        case Constants._astStmtImportWildcardSelector => val r = read_astStmtImportWildcardSelectorT(T); return r
        case _ =>
          reader.error(i, s"$t is not a valid type of org.sireum.lang.ast.Stmt.Import.Selector.")
          val r = read_astStmtImportWildcardSelectorT(T)
          return r
      }
    }

    def read_astStmtImportMultiSelector(): org.sireum.lang.ast.Stmt.Import.MultiSelector = {
      val r = read_astStmtImportMultiSelectorT(F)
      return r
    }

    def read_astStmtImportMultiSelectorT(typeParsed: B): org.sireum.lang.ast.Stmt.Import.MultiSelector = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtImportMultiSelector)
      }
      val selectors = reader.readISZ(read_astStmtImportNamedSelector _)
      return org.sireum.lang.ast.Stmt.Import.MultiSelector(selectors)
    }

    def read_astStmtImportWildcardSelector(): org.sireum.lang.ast.Stmt.Import.WildcardSelector = {
      val r = read_astStmtImportWildcardSelectorT(F)
      return r
    }

    def read_astStmtImportWildcardSelectorT(typeParsed: B): org.sireum.lang.ast.Stmt.Import.WildcardSelector = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtImportWildcardSelector)
      }
      return org.sireum.lang.ast.Stmt.Import.WildcardSelector()
    }

    def read_astStmtImportNamedSelector(): org.sireum.lang.ast.Stmt.Import.NamedSelector = {
      val r = read_astStmtImportNamedSelectorT(F)
      return r
    }

    def read_astStmtImportNamedSelectorT(typeParsed: B): org.sireum.lang.ast.Stmt.Import.NamedSelector = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtImportNamedSelector)
      }
      val from = read_astId()
      val to = read_astId()
      return org.sireum.lang.ast.Stmt.Import.NamedSelector(from, to)
    }

    def read_astStmtVar(): org.sireum.lang.ast.Stmt.Var = {
      val r = read_astStmtVarT(F)
      return r
    }

    def read_astStmtVarT(typeParsed: B): org.sireum.lang.ast.Stmt.Var = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtVar)
      }
      val isSpec = reader.readB()
      val isVal = reader.readB()
      val id = read_astId()
      val tipeOpt = reader.readOption(read_astType _)
      val initOpt = reader.readOption(read_astAssignExp _)
      val attr = read_astResolvedAttr()
      return org.sireum.lang.ast.Stmt.Var(isSpec, isVal, id, tipeOpt, initOpt, attr)
    }

    def read_astStmtVarPattern(): org.sireum.lang.ast.Stmt.VarPattern = {
      val r = read_astStmtVarPatternT(F)
      return r
    }

    def read_astStmtVarPatternT(typeParsed: B): org.sireum.lang.ast.Stmt.VarPattern = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtVarPattern)
      }
      val isSpec = reader.readB()
      val isVal = reader.readB()
      val pattern = read_astPattern()
      val tipeOpt = reader.readOption(read_astType _)
      val init = read_astAssignExp()
      val attr = read_astAttr()
      return org.sireum.lang.ast.Stmt.VarPattern(isSpec, isVal, pattern, tipeOpt, init, attr)
    }

    def read_astStmtSpecVar(): org.sireum.lang.ast.Stmt.SpecVar = {
      val r = read_astStmtSpecVarT(F)
      return r
    }

    def read_astStmtSpecVarT(typeParsed: B): org.sireum.lang.ast.Stmt.SpecVar = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtSpecVar)
      }
      val isVal = reader.readB()
      val id = read_astId()
      val tipe = read_astType()
      val attr = read_astResolvedAttr()
      return org.sireum.lang.ast.Stmt.SpecVar(isVal, id, tipe, attr)
    }

    def read_astStmtRsVal(): org.sireum.lang.ast.Stmt.RsVal = {
      val r = read_astStmtRsValT(F)
      return r
    }

    def read_astStmtRsValT(typeParsed: B): org.sireum.lang.ast.Stmt.RsVal = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtRsVal)
      }
      val id = read_astId()
      val init = read_astExp()
      val attr = read_astResolvedAttr()
      return org.sireum.lang.ast.Stmt.RsVal(id, init, attr)
    }

    def read_astStmtMethod(): org.sireum.lang.ast.Stmt.Method = {
      val r = read_astStmtMethodT(F)
      return r
    }

    def read_astStmtMethodT(typeParsed: B): org.sireum.lang.ast.Stmt.Method = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtMethod)
      }
      val typeChecked = reader.readB()
      val purity = read_astPurityType()
      val modifiers = reader.readISZ(reader.readString _)
      val sig = read_astMethodSig()
      val mcontract = read_astMethodContract()
      val bodyOpt = reader.readOption(read_astBody _)
      val attr = read_astResolvedAttr()
      return org.sireum.lang.ast.Stmt.Method(typeChecked, purity, modifiers, sig, mcontract, bodyOpt, attr)
    }

    def read_astStmtExtMethod(): org.sireum.lang.ast.Stmt.ExtMethod = {
      val r = read_astStmtExtMethodT(F)
      return r
    }

    def read_astStmtExtMethodT(typeParsed: B): org.sireum.lang.ast.Stmt.ExtMethod = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtExtMethod)
      }
      val isPure = reader.readB()
      val sig = read_astMethodSig()
      val contract = read_astMethodContract()
      val attr = read_astResolvedAttr()
      return org.sireum.lang.ast.Stmt.ExtMethod(isPure, sig, contract, attr)
    }

    def read_astStmtJustMethod(): org.sireum.lang.ast.Stmt.JustMethod = {
      val r = read_astStmtJustMethodT(F)
      return r
    }

    def read_astStmtJustMethodT(typeParsed: B): org.sireum.lang.ast.Stmt.JustMethod = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtJustMethod)
      }
      val etaOpt = reader.readOption(read_astExpLitString _)
      val sig = read_astMethodSig()
      val attr = read_astResolvedAttr()
      return org.sireum.lang.ast.Stmt.JustMethod(etaOpt, sig, attr)
    }

    def read_astStmtSpecMethod(): org.sireum.lang.ast.Stmt.SpecMethod = {
      val r = read_astStmtSpecMethodT(F)
      return r
    }

    def read_astStmtSpecMethodT(typeParsed: B): org.sireum.lang.ast.Stmt.SpecMethod = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtSpecMethod)
      }
      val sig = read_astMethodSig()
      val attr = read_astResolvedAttr()
      return org.sireum.lang.ast.Stmt.SpecMethod(sig, attr)
    }

    def read_astStmtEnum(): org.sireum.lang.ast.Stmt.Enum = {
      val r = read_astStmtEnumT(F)
      return r
    }

    def read_astStmtEnumT(typeParsed: B): org.sireum.lang.ast.Stmt.Enum = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtEnum)
      }
      val id = read_astId()
      val elements = reader.readISZ(read_astId _)
      val attr = read_astAttr()
      return org.sireum.lang.ast.Stmt.Enum(id, elements, attr)
    }

    def read_astStmtSubZ(): org.sireum.lang.ast.Stmt.SubZ = {
      val r = read_astStmtSubZT(F)
      return r
    }

    def read_astStmtSubZT(typeParsed: B): org.sireum.lang.ast.Stmt.SubZ = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtSubZ)
      }
      val id = read_astId()
      val isSigned = reader.readB()
      val isBitVector = reader.readB()
      val isWrapped = reader.readB()
      val hasMin = reader.readB()
      val hasMax = reader.readB()
      val bitWidth = reader.readZ()
      val min = reader.readZ()
      val max = reader.readZ()
      val isIndex = reader.readB()
      val index = reader.readZ()
      val attr = read_astAttr()
      return org.sireum.lang.ast.Stmt.SubZ(id, isSigned, isBitVector, isWrapped, hasMin, hasMax, bitWidth, min, max, isIndex, index, attr)
    }

    def read_astStmtObject(): org.sireum.lang.ast.Stmt.Object = {
      val r = read_astStmtObjectT(F)
      return r
    }

    def read_astStmtObjectT(typeParsed: B): org.sireum.lang.ast.Stmt.Object = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtObject)
      }
      val isApp = reader.readB()
      val extNameOpt = reader.readOption(reader.readString _)
      val id = read_astId()
      val stmts = reader.readISZ(read_astStmt _)
      val attr = read_astAttr()
      return org.sireum.lang.ast.Stmt.Object(isApp, extNameOpt, id, stmts, attr)
    }

    def read_astStmtSig(): org.sireum.lang.ast.Stmt.Sig = {
      val r = read_astStmtSigT(F)
      return r
    }

    def read_astStmtSigT(typeParsed: B): org.sireum.lang.ast.Stmt.Sig = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtSig)
      }
      val isImmutable = reader.readB()
      val isSealed = reader.readB()
      val isExt = reader.readB()
      val id = read_astId()
      val typeParams = reader.readISZ(read_astTypeParam _)
      val parents = reader.readISZ(read_astTypeNamed _)
      val stmts = reader.readISZ(read_astStmt _)
      val attr = read_astAttr()
      return org.sireum.lang.ast.Stmt.Sig(isImmutable, isSealed, isExt, id, typeParams, parents, stmts, attr)
    }

    def read_astStmtAdt(): org.sireum.lang.ast.Stmt.Adt = {
      val r = read_astStmtAdtT(F)
      return r
    }

    def read_astStmtAdtT(typeParsed: B): org.sireum.lang.ast.Stmt.Adt = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtAdt)
      }
      val isRoot = reader.readB()
      val isDatatype = reader.readB()
      val isUnclonable = reader.readB()
      val id = read_astId()
      val typeParams = reader.readISZ(read_astTypeParam _)
      val params = reader.readISZ(read_astAdtParam _)
      val parents = reader.readISZ(read_astTypeNamed _)
      val stmts = reader.readISZ(read_astStmt _)
      val attr = read_astAttr()
      return org.sireum.lang.ast.Stmt.Adt(isRoot, isDatatype, isUnclonable, id, typeParams, params, parents, stmts, attr)
    }

    def read_astStmtTypeAlias(): org.sireum.lang.ast.Stmt.TypeAlias = {
      val r = read_astStmtTypeAliasT(F)
      return r
    }

    def read_astStmtTypeAliasT(typeParsed: B): org.sireum.lang.ast.Stmt.TypeAlias = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtTypeAlias)
      }
      val id = read_astId()
      val typeParams = reader.readISZ(read_astTypeParam _)
      val tipe = read_astType()
      val attr = read_astAttr()
      return org.sireum.lang.ast.Stmt.TypeAlias(id, typeParams, tipe, attr)
    }

    def read_astStmtAssign(): org.sireum.lang.ast.Stmt.Assign = {
      val r = read_astStmtAssignT(F)
      return r
    }

    def read_astStmtAssignT(typeParsed: B): org.sireum.lang.ast.Stmt.Assign = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtAssign)
      }
      val lhs = read_astExp()
      val rhs = read_astAssignExp()
      val attr = read_astAttr()
      return org.sireum.lang.ast.Stmt.Assign(lhs, rhs, attr)
    }

    def read_astStmtBlock(): org.sireum.lang.ast.Stmt.Block = {
      val r = read_astStmtBlockT(F)
      return r
    }

    def read_astStmtBlockT(typeParsed: B): org.sireum.lang.ast.Stmt.Block = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtBlock)
      }
      val body = read_astBody()
      val attr = read_astAttr()
      return org.sireum.lang.ast.Stmt.Block(body, attr)
    }

    def read_astStmtIf(): org.sireum.lang.ast.Stmt.If = {
      val r = read_astStmtIfT(F)
      return r
    }

    def read_astStmtIfT(typeParsed: B): org.sireum.lang.ast.Stmt.If = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtIf)
      }
      val cond = read_astExp()
      val thenBody = read_astBody()
      val elseBody = read_astBody()
      val attr = read_astTypedAttr()
      return org.sireum.lang.ast.Stmt.If(cond, thenBody, elseBody, attr)
    }

    def read_astStmtInduct(): org.sireum.lang.ast.Stmt.Induct = {
      val r = read_astStmtInductT(F)
      return r
    }

    def read_astStmtInductT(typeParsed: B): org.sireum.lang.ast.Stmt.Induct = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtInduct)
      }
      val exp = read_astExp()
      val context = reader.readISZ(reader.readString _)
      val locals = reader.readISZ(reader.readString _)
      val attr = read_astAttr()
      return org.sireum.lang.ast.Stmt.Induct(exp, context, locals, attr)
    }

    def read_astStmtMatch(): org.sireum.lang.ast.Stmt.Match = {
      val r = read_astStmtMatchT(F)
      return r
    }

    def read_astStmtMatchT(typeParsed: B): org.sireum.lang.ast.Stmt.Match = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtMatch)
      }
      val isInduct = reader.readB()
      val exp = read_astExp()
      val cases = reader.readISZ(read_astCase _)
      val attr = read_astTypedAttr()
      return org.sireum.lang.ast.Stmt.Match(isInduct, exp, cases, attr)
    }

    def read_astStmtWhile(): org.sireum.lang.ast.Stmt.While = {
      val r = read_astStmtWhileT(F)
      return r
    }

    def read_astStmtWhileT(typeParsed: B): org.sireum.lang.ast.Stmt.While = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtWhile)
      }
      val context = reader.readISZ(reader.readString _)
      val cond = read_astExp()
      val contract = read_astLoopContract()
      val body = read_astBody()
      val attr = read_astAttr()
      return org.sireum.lang.ast.Stmt.While(context, cond, contract, body, attr)
    }

    def read_astStmtFor(): org.sireum.lang.ast.Stmt.For = {
      val r = read_astStmtForT(F)
      return r
    }

    def read_astStmtForT(typeParsed: B): org.sireum.lang.ast.Stmt.For = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtFor)
      }
      val context = reader.readISZ(reader.readString _)
      val enumGens = reader.readISZ(read_astEnumGenFor _)
      val body = read_astBody()
      val attr = read_astAttr()
      return org.sireum.lang.ast.Stmt.For(context, enumGens, body, attr)
    }

    def read_astStmtReturn(): org.sireum.lang.ast.Stmt.Return = {
      val r = read_astStmtReturnT(F)
      return r
    }

    def read_astStmtReturnT(typeParsed: B): org.sireum.lang.ast.Stmt.Return = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtReturn)
      }
      val expOpt = reader.readOption(read_astExp _)
      val attr = read_astTypedAttr()
      return org.sireum.lang.ast.Stmt.Return(expOpt, attr)
    }

    def read_astStmtExpr(): org.sireum.lang.ast.Stmt.Expr = {
      val r = read_astStmtExprT(F)
      return r
    }

    def read_astStmtExprT(typeParsed: B): org.sireum.lang.ast.Stmt.Expr = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtExpr)
      }
      val exp = read_astExp()
      val attr = read_astTypedAttr()
      return org.sireum.lang.ast.Stmt.Expr(exp, attr)
    }

    def read_astStmtExprKindType(): org.sireum.lang.ast.Stmt.Expr.Kind.Type = {
      val r = reader.readZ()
      return org.sireum.lang.ast.Stmt.Expr.Kind.byOrdinal(r).get
    }

    def read_astStmtSpec(): org.sireum.lang.ast.Stmt.Spec = {
      val i = reader.curr
      val t = reader.readZ()
      t match {
        case Constants._astStmtSpecVar => val r = read_astStmtSpecVarT(T); return r
        case Constants._astStmtRsVal => val r = read_astStmtRsValT(T); return r
        case Constants._astStmtJustMethod => val r = read_astStmtJustMethodT(T); return r
        case Constants._astStmtSpecMethod => val r = read_astStmtSpecMethodT(T); return r
        case Constants._astStmtInduct => val r = read_astStmtInductT(T); return r
        case Constants._astStmtFact => val r = read_astStmtFactT(T); return r
        case Constants._astStmtInv => val r = read_astStmtInvT(T); return r
        case Constants._astStmtTheorem => val r = read_astStmtTheoremT(T); return r
        case Constants._astStmtDataRefinement => val r = read_astStmtDataRefinementT(T); return r
        case Constants._astStmtSpecLabel => val r = read_astStmtSpecLabelT(T); return r
        case Constants._astStmtSpecBlock => val r = read_astStmtSpecBlockT(T); return r
        case Constants._astStmtDeduceSequent => val r = read_astStmtDeduceSequentT(T); return r
        case Constants._astStmtDeduceSteps => val r = read_astStmtDeduceStepsT(T); return r
        case Constants._astStmtHavoc => val r = read_astStmtHavocT(T); return r
        case _ =>
          reader.error(i, s"$t is not a valid type of org.sireum.lang.ast.Stmt.Spec.")
          val r = read_astStmtHavocT(T)
          return r
      }
    }

    def read_astStmtFact(): org.sireum.lang.ast.Stmt.Fact = {
      val r = read_astStmtFactT(F)
      return r
    }

    def read_astStmtFactT(typeParsed: B): org.sireum.lang.ast.Stmt.Fact = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtFact)
      }
      val id = read_astId()
      val typeParams = reader.readISZ(read_astTypeParam _)
      val descOpt = reader.readOption(read_astExpLitString _)
      val claims = reader.readISZ(read_astExp _)
      val isFun = reader.readB()
      val attr = read_astResolvedAttr()
      return org.sireum.lang.ast.Stmt.Fact(id, typeParams, descOpt, claims, isFun, attr)
    }

    def read_astStmtInv(): org.sireum.lang.ast.Stmt.Inv = {
      val r = read_astStmtInvT(F)
      return r
    }

    def read_astStmtInvT(typeParsed: B): org.sireum.lang.ast.Stmt.Inv = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtInv)
      }
      val id = read_astId()
      val claims = reader.readISZ(read_astExp _)
      val attr = read_astResolvedAttr()
      return org.sireum.lang.ast.Stmt.Inv(id, claims, attr)
    }

    def read_astStmtTheorem(): org.sireum.lang.ast.Stmt.Theorem = {
      val r = read_astStmtTheoremT(F)
      return r
    }

    def read_astStmtTheoremT(typeParsed: B): org.sireum.lang.ast.Stmt.Theorem = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtTheorem)
      }
      val isLemma = reader.readB()
      val id = read_astId()
      val typeParams = reader.readISZ(read_astTypeParam _)
      val descOpt = reader.readOption(read_astExpLitString _)
      val claim = read_astExp()
      val isFun = reader.readB()
      val proof = read_astProofAst()
      val attr = read_astResolvedAttr()
      return org.sireum.lang.ast.Stmt.Theorem(isLemma, id, typeParams, descOpt, claim, isFun, proof, attr)
    }

    def read_astStmtDataRefinement(): org.sireum.lang.ast.Stmt.DataRefinement = {
      val r = read_astStmtDataRefinementT(F)
      return r
    }

    def read_astStmtDataRefinementT(typeParsed: B): org.sireum.lang.ast.Stmt.DataRefinement = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtDataRefinement)
      }
      val rep = read_astExpRef()
      val refs = reader.readISZ(read_astExpRef _)
      val claims = reader.readISZ(read_astExp _)
      val attr = read_astAttr()
      return org.sireum.lang.ast.Stmt.DataRefinement(rep, refs, claims, attr)
    }

    def read_astStmtSpecLabel(): org.sireum.lang.ast.Stmt.SpecLabel = {
      val r = read_astStmtSpecLabelT(F)
      return r
    }

    def read_astStmtSpecLabelT(typeParsed: B): org.sireum.lang.ast.Stmt.SpecLabel = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtSpecLabel)
      }
      val id = read_astId()
      return org.sireum.lang.ast.Stmt.SpecLabel(id)
    }

    def read_astStmtSpecBlock(): org.sireum.lang.ast.Stmt.SpecBlock = {
      val r = read_astStmtSpecBlockT(F)
      return r
    }

    def read_astStmtSpecBlockT(typeParsed: B): org.sireum.lang.ast.Stmt.SpecBlock = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtSpecBlock)
      }
      val block = read_astStmtBlock()
      return org.sireum.lang.ast.Stmt.SpecBlock(block)
    }

    def read_astStmtDeduceSequent(): org.sireum.lang.ast.Stmt.DeduceSequent = {
      val r = read_astStmtDeduceSequentT(F)
      return r
    }

    def read_astStmtDeduceSequentT(typeParsed: B): org.sireum.lang.ast.Stmt.DeduceSequent = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtDeduceSequent)
      }
      val justOpt = reader.readOption(read_astExpLitString _)
      val sequents = reader.readISZ(read_astSequent _)
      val attr = read_astAttr()
      return org.sireum.lang.ast.Stmt.DeduceSequent(justOpt, sequents, attr)
    }

    def read_astStmtDeduceSteps(): org.sireum.lang.ast.Stmt.DeduceSteps = {
      val r = read_astStmtDeduceStepsT(F)
      return r
    }

    def read_astStmtDeduceStepsT(typeParsed: B): org.sireum.lang.ast.Stmt.DeduceSteps = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtDeduceSteps)
      }
      val steps = reader.readISZ(read_astProofAstStep _)
      val attr = read_astAttr()
      return org.sireum.lang.ast.Stmt.DeduceSteps(steps, attr)
    }

    def read_astStmtHavoc(): org.sireum.lang.ast.Stmt.Havoc = {
      val r = read_astStmtHavocT(F)
      return r
    }

    def read_astStmtHavocT(typeParsed: B): org.sireum.lang.ast.Stmt.Havoc = {
      if (!typeParsed) {
        reader.expectZ(Constants._astStmtHavoc)
      }
      val args = reader.readISZ(read_astExpRef _)
      val attr = read_astAttr()
      return org.sireum.lang.ast.Stmt.Havoc(args, attr)
    }

    def read_astMethodContract(): org.sireum.lang.ast.MethodContract = {
      val i = reader.curr
      val t = reader.readZ()
      t match {
        case Constants._astMethodContractSimple => val r = read_astMethodContractSimpleT(T); return r
        case Constants._astMethodContractCases => val r = read_astMethodContractCasesT(T); return r
        case _ =>
          reader.error(i, s"$t is not a valid type of org.sireum.lang.ast.MethodContract.")
          val r = read_astMethodContractCasesT(T)
          return r
      }
    }

    def read_astMethodContractAccesses(): org.sireum.lang.ast.MethodContract.Accesses = {
      val r = read_astMethodContractAccessesT(F)
      return r
    }

    def read_astMethodContractAccessesT(typeParsed: B): org.sireum.lang.ast.MethodContract.Accesses = {
      if (!typeParsed) {
        reader.expectZ(Constants._astMethodContractAccesses)
      }
      val refs = reader.readISZ(read_astExpRef _)
      val attr = read_astAttr()
      return org.sireum.lang.ast.MethodContract.Accesses(refs, attr)
    }

    def read_astMethodContractClaims(): org.sireum.lang.ast.MethodContract.Claims = {
      val r = read_astMethodContractClaimsT(F)
      return r
    }

    def read_astMethodContractClaimsT(typeParsed: B): org.sireum.lang.ast.MethodContract.Claims = {
      if (!typeParsed) {
        reader.expectZ(Constants._astMethodContractClaims)
      }
      val claims = reader.readISZ(read_astExp _)
      val attr = read_astAttr()
      return org.sireum.lang.ast.MethodContract.Claims(claims, attr)
    }

    def read_astMethodContractSimple(): org.sireum.lang.ast.MethodContract.Simple = {
      val r = read_astMethodContractSimpleT(F)
      return r
    }

    def read_astMethodContractSimpleT(typeParsed: B): org.sireum.lang.ast.MethodContract.Simple = {
      if (!typeParsed) {
        reader.expectZ(Constants._astMethodContractSimple)
      }
      val readsClause = read_astMethodContractAccesses()
      val requiresClause = read_astMethodContractClaims()
      val modifiesClause = read_astMethodContractAccesses()
      val ensuresClause = read_astMethodContractClaims()
      val infoFlowsClause = read_astMethodContractInfoFlows()
      val attr = read_astAttr()
      return org.sireum.lang.ast.MethodContract.Simple(readsClause, requiresClause, modifiesClause, ensuresClause, infoFlowsClause, attr)
    }

    def read_astMethodContractCases(): org.sireum.lang.ast.MethodContract.Cases = {
      val r = read_astMethodContractCasesT(F)
      return r
    }

    def read_astMethodContractCasesT(typeParsed: B): org.sireum.lang.ast.MethodContract.Cases = {
      if (!typeParsed) {
        reader.expectZ(Constants._astMethodContractCases)
      }
      val readsClause = read_astMethodContractAccesses()
      val modifiesClause = read_astMethodContractAccesses()
      val cases = reader.readISZ(read_astMethodContractCase _)
      val attr = read_astAttr()
      return org.sireum.lang.ast.MethodContract.Cases(readsClause, modifiesClause, cases, attr)
    }

    def read_astMethodContractCase(): org.sireum.lang.ast.MethodContract.Case = {
      val r = read_astMethodContractCaseT(F)
      return r
    }

    def read_astMethodContractCaseT(typeParsed: B): org.sireum.lang.ast.MethodContract.Case = {
      if (!typeParsed) {
        reader.expectZ(Constants._astMethodContractCase)
      }
      val label = read_astExpLitString()
      val requiresClause = read_astMethodContractClaims()
      val ensuresClause = read_astMethodContractClaims()
      return org.sireum.lang.ast.MethodContract.Case(label, requiresClause, ensuresClause)
    }

    def read_astMethodContractInfoFlowElement(): org.sireum.lang.ast.MethodContract.InfoFlowElement = {
      val i = reader.curr
      val t = reader.readZ()
      t match {
        case Constants._astMethodContractInfoFlowGroup => val r = read_astMethodContractInfoFlowGroupT(T); return r
        case Constants._astMethodContractInfoFlowFlow => val r = read_astMethodContractInfoFlowFlowT(T); return r
        case Constants._astMethodContractInfoFlowCase => val r = read_astMethodContractInfoFlowCaseT(T); return r
        case _ =>
          reader.error(i, s"$t is not a valid type of org.sireum.lang.ast.MethodContract.InfoFlowElement.")
          val r = read_astMethodContractInfoFlowCaseT(T)
          return r
      }
    }

    def read_astMethodContractInfoFlows(): org.sireum.lang.ast.MethodContract.InfoFlows = {
      val r = read_astMethodContractInfoFlowsT(F)
      return r
    }

    def read_astMethodContractInfoFlowsT(typeParsed: B): org.sireum.lang.ast.MethodContract.InfoFlows = {
      if (!typeParsed) {
        reader.expectZ(Constants._astMethodContractInfoFlows)
      }
      val flows = reader.readISZ(read_astMethodContractInfoFlowElement _)
      val attr = read_astAttr()
      return org.sireum.lang.ast.MethodContract.InfoFlows(flows, attr)
    }

    def read_astMethodContractInfoFlowGroup(): org.sireum.lang.ast.MethodContract.InfoFlowGroup = {
      val r = read_astMethodContractInfoFlowGroupT(F)
      return r
    }

    def read_astMethodContractInfoFlowGroupT(typeParsed: B): org.sireum.lang.ast.MethodContract.InfoFlowGroup = {
      if (!typeParsed) {
        reader.expectZ(Constants._astMethodContractInfoFlowGroup)
      }
      val label = read_astExpLitString()
      val membersClause = read_astMethodContractClaims()
      return org.sireum.lang.ast.MethodContract.InfoFlowGroup(label, membersClause)
    }

    def read_astMethodContractInfoFlowFlow(): org.sireum.lang.ast.MethodContract.InfoFlowFlow = {
      val r = read_astMethodContractInfoFlowFlowT(F)
      return r
    }

    def read_astMethodContractInfoFlowFlowT(typeParsed: B): org.sireum.lang.ast.MethodContract.InfoFlowFlow = {
      if (!typeParsed) {
        reader.expectZ(Constants._astMethodContractInfoFlowFlow)
      }
      val label = read_astExpLitString()
      val fromClause = read_astMethodContractClaims()
      val toClause = read_astMethodContractClaims()
      return org.sireum.lang.ast.MethodContract.InfoFlowFlow(label, fromClause, toClause)
    }

    def read_astMethodContractInfoFlowCase(): org.sireum.lang.ast.MethodContract.InfoFlowCase = {
      val r = read_astMethodContractInfoFlowCaseT(F)
      return r
    }

    def read_astMethodContractInfoFlowCaseT(typeParsed: B): org.sireum.lang.ast.MethodContract.InfoFlowCase = {
      if (!typeParsed) {
        reader.expectZ(Constants._astMethodContractInfoFlowCase)
      }
      val label = read_astExpLitString()
      val requiresClause = read_astMethodContractClaims()
      val inAgreeClause = read_astMethodContractClaims()
      val outAgreeClause = read_astMethodContractClaims()
      return org.sireum.lang.ast.MethodContract.InfoFlowCase(label, requiresClause, inAgreeClause, outAgreeClause)
    }

    def read_astSequent(): org.sireum.lang.ast.Sequent = {
      val r = read_astSequentT(F)
      return r
    }

    def read_astSequentT(typeParsed: B): org.sireum.lang.ast.Sequent = {
      if (!typeParsed) {
        reader.expectZ(Constants._astSequent)
      }
      val premises = reader.readISZ(read_astExp _)
      val conclusion = read_astExp()
      val steps = reader.readISZ(read_astProofAstStep _)
      val attr = read_astAttr()
      return org.sireum.lang.ast.Sequent(premises, conclusion, steps, attr)
    }

    def read_astProofAst(): org.sireum.lang.ast.ProofAst = {
      val r = read_astProofAstT(F)
      return r
    }

    def read_astProofAstT(typeParsed: B): org.sireum.lang.ast.ProofAst = {
      if (!typeParsed) {
        reader.expectZ(Constants._astProofAst)
      }
      val steps = reader.readISZ(read_astProofAstStep _)
      val attr = read_astAttr()
      return org.sireum.lang.ast.ProofAst(steps, attr)
    }

    def read_astProofAstStep(): org.sireum.lang.ast.ProofAst.Step = {
      val i = reader.curr
      val t = reader.readZ()
      t match {
        case Constants._astProofAstStepRegular => val r = read_astProofAstStepRegularT(T); return r
        case Constants._astProofAstStepAssume => val r = read_astProofAstStepAssumeT(T); return r
        case Constants._astProofAstStepAssert => val r = read_astProofAstStepAssertT(T); return r
        case Constants._astProofAstStepSubProof => val r = read_astProofAstStepSubProofT(T); return r
        case Constants._astProofAstStepLet => val r = read_astProofAstStepLetT(T); return r
        case _ =>
          reader.error(i, s"$t is not a valid type of org.sireum.lang.ast.ProofAst.Step.")
          val r = read_astProofAstStepLetT(T)
          return r
      }
    }

    def read_astProofAstStepId(): org.sireum.lang.ast.ProofAst.StepId = {
      val i = reader.curr
      val t = reader.readZ()
      t match {
        case Constants._astProofAstStepIdNum => val r = read_astProofAstStepIdNumT(T); return r
        case Constants._astProofAstStepIdStr => val r = read_astProofAstStepIdStrT(T); return r
        case _ =>
          reader.error(i, s"$t is not a valid type of org.sireum.lang.ast.ProofAst.StepId.")
          val r = read_astProofAstStepIdStrT(T)
          return r
      }
    }

    def read_astProofAstStepIdNum(): org.sireum.lang.ast.ProofAst.StepId.Num = {
      val r = read_astProofAstStepIdNumT(F)
      return r
    }

    def read_astProofAstStepIdNumT(typeParsed: B): org.sireum.lang.ast.ProofAst.StepId.Num = {
      if (!typeParsed) {
        reader.expectZ(Constants._astProofAstStepIdNum)
      }
      val no = reader.readZ()
      val attr = read_astAttr()
      return org.sireum.lang.ast.ProofAst.StepId.Num(no, attr)
    }

    def read_astProofAstStepIdStr(): org.sireum.lang.ast.ProofAst.StepId.Str = {
      val r = read_astProofAstStepIdStrT(F)
      return r
    }

    def read_astProofAstStepIdStrT(typeParsed: B): org.sireum.lang.ast.ProofAst.StepId.Str = {
      if (!typeParsed) {
        reader.expectZ(Constants._astProofAstStepIdStr)
      }
      val isSynthetic = reader.readB()
      val value = reader.readString()
      val attr = read_astAttr()
      return org.sireum.lang.ast.ProofAst.StepId.Str(isSynthetic, value, attr)
    }

    def read_astProofAstStepRegular(): org.sireum.lang.ast.ProofAst.Step.Regular = {
      val r = read_astProofAstStepRegularT(F)
      return r
    }

    def read_astProofAstStepRegularT(typeParsed: B): org.sireum.lang.ast.ProofAst.Step.Regular = {
      if (!typeParsed) {
        reader.expectZ(Constants._astProofAstStepRegular)
      }
      val id = read_astProofAstStepId()
      val claim = read_astExp()
      val just = read_astProofAstStepJustification()
      val attr = read_astAttr()
      return org.sireum.lang.ast.ProofAst.Step.Regular(id, claim, just, attr)
    }

    def read_astProofAstStepAssume(): org.sireum.lang.ast.ProofAst.Step.Assume = {
      val r = read_astProofAstStepAssumeT(F)
      return r
    }

    def read_astProofAstStepAssumeT(typeParsed: B): org.sireum.lang.ast.ProofAst.Step.Assume = {
      if (!typeParsed) {
        reader.expectZ(Constants._astProofAstStepAssume)
      }
      val id = read_astProofAstStepId()
      val claim = read_astExp()
      val attr = read_astAttr()
      return org.sireum.lang.ast.ProofAst.Step.Assume(id, claim, attr)
    }

    def read_astProofAstStepAssert(): org.sireum.lang.ast.ProofAst.Step.Assert = {
      val r = read_astProofAstStepAssertT(F)
      return r
    }

    def read_astProofAstStepAssertT(typeParsed: B): org.sireum.lang.ast.ProofAst.Step.Assert = {
      if (!typeParsed) {
        reader.expectZ(Constants._astProofAstStepAssert)
      }
      val id = read_astProofAstStepId()
      val claim = read_astExp()
      val steps = reader.readISZ(read_astProofAstStep _)
      val attr = read_astAttr()
      return org.sireum.lang.ast.ProofAst.Step.Assert(id, claim, steps, attr)
    }

    def read_astProofAstStepSubProof(): org.sireum.lang.ast.ProofAst.Step.SubProof = {
      val r = read_astProofAstStepSubProofT(F)
      return r
    }

    def read_astProofAstStepSubProofT(typeParsed: B): org.sireum.lang.ast.ProofAst.Step.SubProof = {
      if (!typeParsed) {
        reader.expectZ(Constants._astProofAstStepSubProof)
      }
      val id = read_astProofAstStepId()
      val steps = reader.readISZ(read_astProofAstStep _)
      val attr = read_astAttr()
      return org.sireum.lang.ast.ProofAst.Step.SubProof(id, steps, attr)
    }

    def read_astProofAstStepLet(): org.sireum.lang.ast.ProofAst.Step.Let = {
      val r = read_astProofAstStepLetT(F)
      return r
    }

    def read_astProofAstStepLetT(typeParsed: B): org.sireum.lang.ast.ProofAst.Step.Let = {
      if (!typeParsed) {
        reader.expectZ(Constants._astProofAstStepLet)
      }
      val id = read_astProofAstStepId()
      val params = reader.readISZ(read_astProofAstStepLetParam _)
      val steps = reader.readISZ(read_astProofAstStep _)
      val context = reader.readISZ(reader.readString _)
      val attr = read_astAttr()
      return org.sireum.lang.ast.ProofAst.Step.Let(id, params, steps, context, attr)
    }

    def read_astProofAstStepLetParam(): org.sireum.lang.ast.ProofAst.Step.Let.Param = {
      val r = read_astProofAstStepLetParamT(F)
      return r
    }

    def read_astProofAstStepLetParamT(typeParsed: B): org.sireum.lang.ast.ProofAst.Step.Let.Param = {
      if (!typeParsed) {
        reader.expectZ(Constants._astProofAstStepLetParam)
      }
      val id = read_astId()
      val tipeOpt = reader.readOption(read_astType _)
      return org.sireum.lang.ast.ProofAst.Step.Let.Param(id, tipeOpt)
    }

    def read_astProofAstStepJustification(): org.sireum.lang.ast.ProofAst.Step.Justification = {
      val i = reader.curr
      val t = reader.readZ()
      t match {
        case Constants._astProofAstStepJustificationRef => val r = read_astProofAstStepJustificationRefT(T); return r
        case Constants._astProofAstStepJustificationApply => val r = read_astProofAstStepJustificationApplyT(T); return r
        case Constants._astProofAstStepJustificationApplyNamed => val r = read_astProofAstStepJustificationApplyNamedT(T); return r
        case Constants._astProofAstStepJustificationApplyEta => val r = read_astProofAstStepJustificationApplyEtaT(T); return r
        case _ =>
          reader.error(i, s"$t is not a valid type of org.sireum.lang.ast.ProofAst.Step.Justification.")
          val r = read_astProofAstStepJustificationApplyEtaT(T)
          return r
      }
    }

    def read_astProofAstStepJustificationRef(): org.sireum.lang.ast.ProofAst.Step.Justification.Ref = {
      val r = read_astProofAstStepJustificationRefT(F)
      return r
    }

    def read_astProofAstStepJustificationRefT(typeParsed: B): org.sireum.lang.ast.ProofAst.Step.Justification.Ref = {
      if (!typeParsed) {
        reader.expectZ(Constants._astProofAstStepJustificationRef)
      }
      val ref = read_astExpRef()
      val hasWitness = reader.readB()
      val witnesses = reader.readISZ(read_astProofAstStepId _)
      return org.sireum.lang.ast.ProofAst.Step.Justification.Ref(ref, hasWitness, witnesses)
    }

    def read_astProofAstStepJustificationApply(): org.sireum.lang.ast.ProofAst.Step.Justification.Apply = {
      val r = read_astProofAstStepJustificationApplyT(F)
      return r
    }

    def read_astProofAstStepJustificationApplyT(typeParsed: B): org.sireum.lang.ast.ProofAst.Step.Justification.Apply = {
      if (!typeParsed) {
        reader.expectZ(Constants._astProofAstStepJustificationApply)
      }
      val invoke = read_astExpInvoke()
      val hasWitness = reader.readB()
      val witnesses = reader.readISZ(read_astProofAstStepId _)
      return org.sireum.lang.ast.ProofAst.Step.Justification.Apply(invoke, hasWitness, witnesses)
    }

    def read_astProofAstStepJustificationApplyNamed(): org.sireum.lang.ast.ProofAst.Step.Justification.ApplyNamed = {
      val r = read_astProofAstStepJustificationApplyNamedT(F)
      return r
    }

    def read_astProofAstStepJustificationApplyNamedT(typeParsed: B): org.sireum.lang.ast.ProofAst.Step.Justification.ApplyNamed = {
      if (!typeParsed) {
        reader.expectZ(Constants._astProofAstStepJustificationApplyNamed)
      }
      val invoke = read_astExpInvokeNamed()
      val hasWitness = reader.readB()
      val witnesses = reader.readISZ(read_astProofAstStepId _)
      return org.sireum.lang.ast.ProofAst.Step.Justification.ApplyNamed(invoke, hasWitness, witnesses)
    }

    def read_astProofAstStepJustificationApplyEta(): org.sireum.lang.ast.ProofAst.Step.Justification.ApplyEta = {
      val r = read_astProofAstStepJustificationApplyEtaT(F)
      return r
    }

    def read_astProofAstStepJustificationApplyEtaT(typeParsed: B): org.sireum.lang.ast.ProofAst.Step.Justification.ApplyEta = {
      if (!typeParsed) {
        reader.expectZ(Constants._astProofAstStepJustificationApplyEta)
      }
      val eta = read_astExpEta()
      val hasWitness = reader.readB()
      val witnesses = reader.readISZ(read_astProofAstStepId _)
      return org.sireum.lang.ast.ProofAst.Step.Justification.ApplyEta(eta, hasWitness, witnesses)
    }

    def read_astAssignExp(): org.sireum.lang.ast.AssignExp = {
      val i = reader.curr
      val t = reader.readZ()
      t match {
        case Constants._astStmtBlock => val r = read_astStmtBlockT(T); return r
        case Constants._astStmtIf => val r = read_astStmtIfT(T); return r
        case Constants._astStmtMatch => val r = read_astStmtMatchT(T); return r
        case Constants._astStmtReturn => val r = read_astStmtReturnT(T); return r
        case Constants._astStmtExpr => val r = read_astStmtExprT(T); return r
        case _ =>
          reader.error(i, s"$t is not a valid type of org.sireum.lang.ast.AssignExp.")
          val r = read_astStmtExprT(T)
          return r
      }
    }

    def read_astCase(): org.sireum.lang.ast.Case = {
      val r = read_astCaseT(F)
      return r
    }

    def read_astCaseT(typeParsed: B): org.sireum.lang.ast.Case = {
      if (!typeParsed) {
        reader.expectZ(Constants._astCase)
      }
      val pattern = read_astPattern()
      val condOpt = reader.readOption(read_astExp _)
      val body = read_astBody()
      return org.sireum.lang.ast.Case(pattern, condOpt, body)
    }

    def read_astEnumGenRange(): org.sireum.lang.ast.EnumGen.Range = {
      val i = reader.curr
      val t = reader.readZ()
      t match {
        case Constants._astEnumGenRangeExpr => val r = read_astEnumGenRangeExprT(T); return r
        case Constants._astEnumGenRangeStep => val r = read_astEnumGenRangeStepT(T); return r
        case _ =>
          reader.error(i, s"$t is not a valid type of org.sireum.lang.ast.EnumGen.Range.")
          val r = read_astEnumGenRangeStepT(T)
          return r
      }
    }

    def read_astEnumGenRangeExpr(): org.sireum.lang.ast.EnumGen.Range.Expr = {
      val r = read_astEnumGenRangeExprT(F)
      return r
    }

    def read_astEnumGenRangeExprT(typeParsed: B): org.sireum.lang.ast.EnumGen.Range.Expr = {
      if (!typeParsed) {
        reader.expectZ(Constants._astEnumGenRangeExpr)
      }
      val exp = read_astExp()
      val attr = read_astAttr()
      return org.sireum.lang.ast.EnumGen.Range.Expr(exp, attr)
    }

    def read_astEnumGenRangeStep(): org.sireum.lang.ast.EnumGen.Range.Step = {
      val r = read_astEnumGenRangeStepT(F)
      return r
    }

    def read_astEnumGenRangeStepT(typeParsed: B): org.sireum.lang.ast.EnumGen.Range.Step = {
      if (!typeParsed) {
        reader.expectZ(Constants._astEnumGenRangeStep)
      }
      val isInclusive = reader.readB()
      val start = read_astExp()
      val end = read_astExp()
      val byOpt = reader.readOption(read_astExp _)
      val attr = read_astAttr()
      return org.sireum.lang.ast.EnumGen.Range.Step(isInclusive, start, end, byOpt, attr)
    }

    def read_astEnumGenFor(): org.sireum.lang.ast.EnumGen.For = {
      val r = read_astEnumGenForT(F)
      return r
    }

    def read_astEnumGenForT(typeParsed: B): org.sireum.lang.ast.EnumGen.For = {
      if (!typeParsed) {
        reader.expectZ(Constants._astEnumGenFor)
      }
      val idOpt = reader.readOption(read_astId _)
      val range = read_astEnumGenRange()
      val condOpt = reader.readOption(read_astExp _)
      val contract = read_astLoopContract()
      return org.sireum.lang.ast.EnumGen.For(idOpt, range, condOpt, contract)
    }

    def read_astType(): org.sireum.lang.ast.Type = {
      val i = reader.curr
      val t = reader.readZ()
      t match {
        case Constants._astTypeNamed => val r = read_astTypeNamedT(T); return r
        case Constants._astTypeFun => val r = read_astTypeFunT(T); return r
        case Constants._astTypeTuple => val r = read_astTypeTupleT(T); return r
        case _ =>
          reader.error(i, s"$t is not a valid type of org.sireum.lang.ast.Type.")
          val r = read_astTypeTupleT(T)
          return r
      }
    }

    def read_astTypeNamed(): org.sireum.lang.ast.Type.Named = {
      val r = read_astTypeNamedT(F)
      return r
    }

    def read_astTypeNamedT(typeParsed: B): org.sireum.lang.ast.Type.Named = {
      if (!typeParsed) {
        reader.expectZ(Constants._astTypeNamed)
      }
      val name = read_astName()
      val typeArgs = reader.readISZ(read_astType _)
      val attr = read_astTypedAttr()
      return org.sireum.lang.ast.Type.Named(name, typeArgs, attr)
    }

    def read_astTypeFun(): org.sireum.lang.ast.Type.Fun = {
      val r = read_astTypeFunT(F)
      return r
    }

    def read_astTypeFunT(typeParsed: B): org.sireum.lang.ast.Type.Fun = {
      if (!typeParsed) {
        reader.expectZ(Constants._astTypeFun)
      }
      val isPure = reader.readB()
      val isByName = reader.readB()
      val args = reader.readISZ(read_astType _)
      val ret = read_astType()
      val attr = read_astTypedAttr()
      return org.sireum.lang.ast.Type.Fun(isPure, isByName, args, ret, attr)
    }

    def read_astTypeTuple(): org.sireum.lang.ast.Type.Tuple = {
      val r = read_astTypeTupleT(F)
      return r
    }

    def read_astTypeTupleT(typeParsed: B): org.sireum.lang.ast.Type.Tuple = {
      if (!typeParsed) {
        reader.expectZ(Constants._astTypeTuple)
      }
      val args = reader.readISZ(read_astType _)
      val attr = read_astTypedAttr()
      return org.sireum.lang.ast.Type.Tuple(args, attr)
    }

    def read_astPattern(): org.sireum.lang.ast.Pattern = {
      val i = reader.curr
      val t = reader.readZ()
      t match {
        case Constants._astPatternLiteral => val r = read_astPatternLiteralT(T); return r
        case Constants._astPatternLitInterpolate => val r = read_astPatternLitInterpolateT(T); return r
        case Constants._astPatternRef => val r = read_astPatternRefT(T); return r
        case Constants._astPatternVarBinding => val r = read_astPatternVarBindingT(T); return r
        case Constants._astPatternWildcard => val r = read_astPatternWildcardT(T); return r
        case Constants._astPatternSeqWildcard => val r = read_astPatternSeqWildcardT(T); return r
        case Constants._astPatternStructure => val r = read_astPatternStructureT(T); return r
        case _ =>
          reader.error(i, s"$t is not a valid type of org.sireum.lang.ast.Pattern.")
          val r = read_astPatternStructureT(T)
          return r
      }
    }

    def read_astPatternLiteral(): org.sireum.lang.ast.Pattern.Literal = {
      val r = read_astPatternLiteralT(F)
      return r
    }

    def read_astPatternLiteralT(typeParsed: B): org.sireum.lang.ast.Pattern.Literal = {
      if (!typeParsed) {
        reader.expectZ(Constants._astPatternLiteral)
      }
      val lit = read_astLit()
      return org.sireum.lang.ast.Pattern.Literal(lit)
    }

    def read_astPatternLitInterpolate(): org.sireum.lang.ast.Pattern.LitInterpolate = {
      val r = read_astPatternLitInterpolateT(F)
      return r
    }

    def read_astPatternLitInterpolateT(typeParsed: B): org.sireum.lang.ast.Pattern.LitInterpolate = {
      if (!typeParsed) {
        reader.expectZ(Constants._astPatternLitInterpolate)
      }
      val prefix = reader.readString()
      val value = reader.readString()
      val attr = read_astTypedAttr()
      return org.sireum.lang.ast.Pattern.LitInterpolate(prefix, value, attr)
    }

    def read_astPatternRef(): org.sireum.lang.ast.Pattern.Ref = {
      val r = read_astPatternRefT(F)
      return r
    }

    def read_astPatternRefT(typeParsed: B): org.sireum.lang.ast.Pattern.Ref = {
      if (!typeParsed) {
        reader.expectZ(Constants._astPatternRef)
      }
      val isAccess = reader.readB()
      val name = read_astName()
      val receiverTipeOpt = reader.readOption(read_astTypedName _)
      val idContext = reader.readISZ(reader.readString _)
      val attr = read_astResolvedAttr()
      return org.sireum.lang.ast.Pattern.Ref(isAccess, name, receiverTipeOpt, idContext, attr)
    }

    def read_astPatternVarBinding(): org.sireum.lang.ast.Pattern.VarBinding = {
      val r = read_astPatternVarBindingT(F)
      return r
    }

    def read_astPatternVarBindingT(typeParsed: B): org.sireum.lang.ast.Pattern.VarBinding = {
      if (!typeParsed) {
        reader.expectZ(Constants._astPatternVarBinding)
      }
      val id = read_astId()
      val tipeOpt = reader.readOption(read_astType _)
      val idContext = reader.readISZ(reader.readString _)
      val attr = read_astTypedAttr()
      return org.sireum.lang.ast.Pattern.VarBinding(id, tipeOpt, idContext, attr)
    }

    def read_astPatternWildcard(): org.sireum.lang.ast.Pattern.Wildcard = {
      val r = read_astPatternWildcardT(F)
      return r
    }

    def read_astPatternWildcardT(typeParsed: B): org.sireum.lang.ast.Pattern.Wildcard = {
      if (!typeParsed) {
        reader.expectZ(Constants._astPatternWildcard)
      }
      val typeOpt = reader.readOption(read_astType _)
      val attr = read_astTypedAttr()
      return org.sireum.lang.ast.Pattern.Wildcard(typeOpt, attr)
    }

    def read_astPatternSeqWildcard(): org.sireum.lang.ast.Pattern.SeqWildcard = {
      val r = read_astPatternSeqWildcardT(F)
      return r
    }

    def read_astPatternSeqWildcardT(typeParsed: B): org.sireum.lang.ast.Pattern.SeqWildcard = {
      if (!typeParsed) {
        reader.expectZ(Constants._astPatternSeqWildcard)
      }
      val attr = read_astTypedAttr()
      return org.sireum.lang.ast.Pattern.SeqWildcard(attr)
    }

    def read_astPatternStructure(): org.sireum.lang.ast.Pattern.Structure = {
      val r = read_astPatternStructureT(F)
      return r
    }

    def read_astPatternStructureT(typeParsed: B): org.sireum.lang.ast.Pattern.Structure = {
      if (!typeParsed) {
        reader.expectZ(Constants._astPatternStructure)
      }
      val idOpt = reader.readOption(read_astId _)
      val nameOpt = reader.readOption(read_astName _)
      val patterns = reader.readISZ(read_astPattern _)
      val idContext = reader.readISZ(reader.readString _)
      val attr = read_astResolvedAttr()
      return org.sireum.lang.ast.Pattern.Structure(idOpt, nameOpt, patterns, idContext, attr)
    }

    def read_astExp(): org.sireum.lang.ast.Exp = {
      val i = reader.curr
      val t = reader.readZ()
      t match {
        case Constants._astProofAstStepIdNum => val r = read_astProofAstStepIdNumT(T); return r
        case Constants._astProofAstStepIdStr => val r = read_astProofAstStepIdStrT(T); return r
        case Constants._astExpLitB => val r = read_astExpLitBT(T); return r
        case Constants._astExpLitC => val r = read_astExpLitCT(T); return r
        case Constants._astExpLitZ => val r = read_astExpLitZT(T); return r
        case Constants._astExpLitF32 => val r = read_astExpLitF32T(T); return r
        case Constants._astExpLitF64 => val r = read_astExpLitF64T(T); return r
        case Constants._astExpLitR => val r = read_astExpLitRT(T); return r
        case Constants._astExpLitString => val r = read_astExpLitStringT(T); return r
        case Constants._astExpStringInterpolate => val r = read_astExpStringInterpolateT(T); return r
        case Constants._astExpThis => val r = read_astExpThisT(T); return r
        case Constants._astExpSuper => val r = read_astExpSuperT(T); return r
        case Constants._astExpUnary => val r = read_astExpUnaryT(T); return r
        case Constants._astExpBinary => val r = read_astExpBinaryT(T); return r
        case Constants._astExpIdent => val r = read_astExpIdentT(T); return r
        case Constants._astExpEta => val r = read_astExpEtaT(T); return r
        case Constants._astExpTuple => val r = read_astExpTupleT(T); return r
        case Constants._astExpSelect => val r = read_astExpSelectT(T); return r
        case Constants._astExpInvoke => val r = read_astExpInvokeT(T); return r
        case Constants._astExpInvokeNamed => val r = read_astExpInvokeNamedT(T); return r
        case Constants._astExpIf => val r = read_astExpIfT(T); return r
        case Constants._astExpTypeCond => val r = read_astExpTypeCondT(T); return r
        case Constants._astExpSym => val r = read_astExpSymT(T); return r
        case Constants._astExpFun => val r = read_astExpFunT(T); return r
        case Constants._astExpForYield => val r = read_astExpForYieldT(T); return r
        case Constants._astExpQuantType => val r = read_astExpQuantTypeT(T); return r
        case Constants._astExpQuantRange => val r = read_astExpQuantRangeT(T); return r
        case Constants._astExpQuantEach => val r = read_astExpQuantEachT(T); return r
        case Constants._astExpInput => val r = read_astExpInputT(T); return r
        case Constants._astExpOld => val r = read_astExpOldT(T); return r
        case Constants._astExpRS => val r = read_astExpRST(T); return r
        case Constants._astExpAt => val r = read_astExpAtT(T); return r
        case Constants._astExpLoopIndex => val r = read_astExpLoopIndexT(T); return r
        case Constants._astExpStateSeq => val r = read_astExpStateSeqT(T); return r
        case Constants._astExpResult => val r = read_astExpResultT(T); return r
        case Constants._astExpStrictPureBlock => val r = read_astExpStrictPureBlockT(T); return r
        case Constants._astExpLabeled => val r = read_astExpLabeledT(T); return r
        case Constants._astExpAssumeAgree => val r = read_astExpAssumeAgreeT(T); return r
        case Constants._astExpAssertAgree => val r = read_astExpAssertAgreeT(T); return r
        case Constants._astExpInfoFlowInvariant => val r = read_astExpInfoFlowInvariantT(T); return r
        case _ =>
          reader.error(i, s"$t is not a valid type of org.sireum.lang.ast.Exp.")
          val r = read_astExpInfoFlowInvariantT(T)
          return r
      }
    }

    def read_astLit(): org.sireum.lang.ast.Lit = {
      val i = reader.curr
      val t = reader.readZ()
      t match {
        case Constants._astProofAstStepIdNum => val r = read_astProofAstStepIdNumT(T); return r
        case Constants._astProofAstStepIdStr => val r = read_astProofAstStepIdStrT(T); return r
        case Constants._astExpLitB => val r = read_astExpLitBT(T); return r
        case Constants._astExpLitC => val r = read_astExpLitCT(T); return r
        case Constants._astExpLitZ => val r = read_astExpLitZT(T); return r
        case Constants._astExpLitF32 => val r = read_astExpLitF32T(T); return r
        case Constants._astExpLitF64 => val r = read_astExpLitF64T(T); return r
        case Constants._astExpLitR => val r = read_astExpLitRT(T); return r
        case Constants._astExpLitString => val r = read_astExpLitStringT(T); return r
        case _ =>
          reader.error(i, s"$t is not a valid type of org.sireum.lang.ast.Lit.")
          val r = read_astExpLitStringT(T)
          return r
      }
    }

    def read_astExpLitB(): org.sireum.lang.ast.Exp.LitB = {
      val r = read_astExpLitBT(F)
      return r
    }

    def read_astExpLitBT(typeParsed: B): org.sireum.lang.ast.Exp.LitB = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpLitB)
      }
      val value = reader.readB()
      val attr = read_astAttr()
      return org.sireum.lang.ast.Exp.LitB(value, attr)
    }

    def read_astExpLitC(): org.sireum.lang.ast.Exp.LitC = {
      val r = read_astExpLitCT(F)
      return r
    }

    def read_astExpLitCT(typeParsed: B): org.sireum.lang.ast.Exp.LitC = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpLitC)
      }
      val value = reader.readC()
      val attr = read_astAttr()
      return org.sireum.lang.ast.Exp.LitC(value, attr)
    }

    def read_astExpLitZ(): org.sireum.lang.ast.Exp.LitZ = {
      val r = read_astExpLitZT(F)
      return r
    }

    def read_astExpLitZT(typeParsed: B): org.sireum.lang.ast.Exp.LitZ = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpLitZ)
      }
      val value = reader.readZ()
      val attr = read_astAttr()
      return org.sireum.lang.ast.Exp.LitZ(value, attr)
    }

    def read_astExpLitF32(): org.sireum.lang.ast.Exp.LitF32 = {
      val r = read_astExpLitF32T(F)
      return r
    }

    def read_astExpLitF32T(typeParsed: B): org.sireum.lang.ast.Exp.LitF32 = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpLitF32)
      }
      val value = reader.readF32()
      val attr = read_astAttr()
      return org.sireum.lang.ast.Exp.LitF32(value, attr)
    }

    def read_astExpLitF64(): org.sireum.lang.ast.Exp.LitF64 = {
      val r = read_astExpLitF64T(F)
      return r
    }

    def read_astExpLitF64T(typeParsed: B): org.sireum.lang.ast.Exp.LitF64 = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpLitF64)
      }
      val value = reader.readF64()
      val attr = read_astAttr()
      return org.sireum.lang.ast.Exp.LitF64(value, attr)
    }

    def read_astExpLitR(): org.sireum.lang.ast.Exp.LitR = {
      val r = read_astExpLitRT(F)
      return r
    }

    def read_astExpLitRT(typeParsed: B): org.sireum.lang.ast.Exp.LitR = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpLitR)
      }
      val value = reader.readR()
      val attr = read_astAttr()
      return org.sireum.lang.ast.Exp.LitR(value, attr)
    }

    def read_astExpLitString(): org.sireum.lang.ast.Exp.LitString = {
      val r = read_astExpLitStringT(F)
      return r
    }

    def read_astExpLitStringT(typeParsed: B): org.sireum.lang.ast.Exp.LitString = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpLitString)
      }
      val value = reader.readString()
      val attr = read_astAttr()
      return org.sireum.lang.ast.Exp.LitString(value, attr)
    }

    def read_astExpStringInterpolate(): org.sireum.lang.ast.Exp.StringInterpolate = {
      val r = read_astExpStringInterpolateT(F)
      return r
    }

    def read_astExpStringInterpolateT(typeParsed: B): org.sireum.lang.ast.Exp.StringInterpolate = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpStringInterpolate)
      }
      val prefix = reader.readString()
      val lits = reader.readISZ(read_astExpLitString _)
      val args = reader.readISZ(read_astExp _)
      val attr = read_astTypedAttr()
      return org.sireum.lang.ast.Exp.StringInterpolate(prefix, lits, args, attr)
    }

    def read_astExpThis(): org.sireum.lang.ast.Exp.This = {
      val r = read_astExpThisT(F)
      return r
    }

    def read_astExpThisT(typeParsed: B): org.sireum.lang.ast.Exp.This = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpThis)
      }
      val owner = reader.readISZ(reader.readString _)
      val attr = read_astTypedAttr()
      return org.sireum.lang.ast.Exp.This(owner, attr)
    }

    def read_astExpSuper(): org.sireum.lang.ast.Exp.Super = {
      val r = read_astExpSuperT(F)
      return r
    }

    def read_astExpSuperT(typeParsed: B): org.sireum.lang.ast.Exp.Super = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpSuper)
      }
      val idOpt = reader.readOption(read_astId _)
      val attr = read_astTypedAttr()
      return org.sireum.lang.ast.Exp.Super(idOpt, attr)
    }

    def read_astExpUnaryOpType(): org.sireum.lang.ast.Exp.UnaryOp.Type = {
      val r = reader.readZ()
      return org.sireum.lang.ast.Exp.UnaryOp.byOrdinal(r).get
    }

    def read_astExpUnary(): org.sireum.lang.ast.Exp.Unary = {
      val r = read_astExpUnaryT(F)
      return r
    }

    def read_astExpUnaryT(typeParsed: B): org.sireum.lang.ast.Exp.Unary = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpUnary)
      }
      val op = read_astExpUnaryOpType()
      val exp = read_astExp()
      val attr = read_astResolvedAttr()
      val opPosOpt = reader.readOption(reader.readPosition _)
      return org.sireum.lang.ast.Exp.Unary(op, exp, attr, opPosOpt)
    }

    def read_astExpRef(): org.sireum.lang.ast.Exp.Ref = {
      val i = reader.curr
      val t = reader.readZ()
      t match {
        case Constants._astExpIdent => val r = read_astExpIdentT(T); return r
        case Constants._astExpSelect => val r = read_astExpSelectT(T); return r
        case _ =>
          reader.error(i, s"$t is not a valid type of org.sireum.lang.ast.Exp.Ref.")
          val r = read_astExpSelectT(T)
          return r
      }
    }

    def read_astExpBinary(): org.sireum.lang.ast.Exp.Binary = {
      val r = read_astExpBinaryT(F)
      return r
    }

    def read_astExpBinaryT(typeParsed: B): org.sireum.lang.ast.Exp.Binary = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpBinary)
      }
      val left = read_astExp()
      val op = reader.readString()
      val right = read_astExp()
      val attr = read_astResolvedAttr()
      val opPosOpt = reader.readOption(reader.readPosition _)
      return org.sireum.lang.ast.Exp.Binary(left, op, right, attr, opPosOpt)
    }

    def read_astExpIdent(): org.sireum.lang.ast.Exp.Ident = {
      val r = read_astExpIdentT(F)
      return r
    }

    def read_astExpIdentT(typeParsed: B): org.sireum.lang.ast.Exp.Ident = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpIdent)
      }
      val id = read_astId()
      val attr = read_astResolvedAttr()
      return org.sireum.lang.ast.Exp.Ident(id, attr)
    }

    def read_astExpEta(): org.sireum.lang.ast.Exp.Eta = {
      val r = read_astExpEtaT(F)
      return r
    }

    def read_astExpEtaT(typeParsed: B): org.sireum.lang.ast.Exp.Eta = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpEta)
      }
      val ref = read_astExpRef()
      val attr = read_astTypedAttr()
      return org.sireum.lang.ast.Exp.Eta(ref, attr)
    }

    def read_astExpTuple(): org.sireum.lang.ast.Exp.Tuple = {
      val r = read_astExpTupleT(F)
      return r
    }

    def read_astExpTupleT(typeParsed: B): org.sireum.lang.ast.Exp.Tuple = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpTuple)
      }
      val args = reader.readISZ(read_astExp _)
      val attr = read_astTypedAttr()
      return org.sireum.lang.ast.Exp.Tuple(args, attr)
    }

    def read_astExpSelect(): org.sireum.lang.ast.Exp.Select = {
      val r = read_astExpSelectT(F)
      return r
    }

    def read_astExpSelectT(typeParsed: B): org.sireum.lang.ast.Exp.Select = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpSelect)
      }
      val receiverOpt = reader.readOption(read_astExp _)
      val id = read_astId()
      val targs = reader.readISZ(read_astType _)
      val attr = read_astResolvedAttr()
      return org.sireum.lang.ast.Exp.Select(receiverOpt, id, targs, attr)
    }

    def read_astExpInvoke(): org.sireum.lang.ast.Exp.Invoke = {
      val r = read_astExpInvokeT(F)
      return r
    }

    def read_astExpInvokeT(typeParsed: B): org.sireum.lang.ast.Exp.Invoke = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpInvoke)
      }
      val receiverOpt = reader.readOption(read_astExp _)
      val ident = read_astExpIdent()
      val targs = reader.readISZ(read_astType _)
      val args = reader.readISZ(read_astExp _)
      val attr = read_astResolvedAttr()
      return org.sireum.lang.ast.Exp.Invoke(receiverOpt, ident, targs, args, attr)
    }

    def read_astExpInvokeNamed(): org.sireum.lang.ast.Exp.InvokeNamed = {
      val r = read_astExpInvokeNamedT(F)
      return r
    }

    def read_astExpInvokeNamedT(typeParsed: B): org.sireum.lang.ast.Exp.InvokeNamed = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpInvokeNamed)
      }
      val receiverOpt = reader.readOption(read_astExp _)
      val ident = read_astExpIdent()
      val targs = reader.readISZ(read_astType _)
      val args = reader.readISZ(read_astNamedArg _)
      val attr = read_astResolvedAttr()
      return org.sireum.lang.ast.Exp.InvokeNamed(receiverOpt, ident, targs, args, attr)
    }

    def read_astExpIf(): org.sireum.lang.ast.Exp.If = {
      val r = read_astExpIfT(F)
      return r
    }

    def read_astExpIfT(typeParsed: B): org.sireum.lang.ast.Exp.If = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpIf)
      }
      val cond = read_astExp()
      val thenExp = read_astExp()
      val elseExp = read_astExp()
      val attr = read_astTypedAttr()
      return org.sireum.lang.ast.Exp.If(cond, thenExp, elseExp, attr)
    }

    def read_astExpTypeCond(): org.sireum.lang.ast.Exp.TypeCond = {
      val r = read_astExpTypeCondT(F)
      return r
    }

    def read_astExpTypeCondT(typeParsed: B): org.sireum.lang.ast.Exp.TypeCond = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpTypeCond)
      }
      val args = reader.readISZ(read_astExp _)
      val fun = read_astExpFun()
      val attr = read_astAttr()
      return org.sireum.lang.ast.Exp.TypeCond(args, fun, attr)
    }

    def read_astExpSym(): org.sireum.lang.ast.Exp.Sym = {
      val r = read_astExpSymT(F)
      return r
    }

    def read_astExpSymT(typeParsed: B): org.sireum.lang.ast.Exp.Sym = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpSym)
      }
      val num = reader.readZ()
      val attr = read_astTypedAttr()
      return org.sireum.lang.ast.Exp.Sym(num, attr)
    }

    def read_astExpFunParam(): org.sireum.lang.ast.Exp.Fun.Param = {
      val r = read_astExpFunParamT(F)
      return r
    }

    def read_astExpFunParamT(typeParsed: B): org.sireum.lang.ast.Exp.Fun.Param = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpFunParam)
      }
      val idOpt = reader.readOption(read_astId _)
      val tipeOpt = reader.readOption(read_astType _)
      val typedOpt = reader.readOption(read_astTyped _)
      return org.sireum.lang.ast.Exp.Fun.Param(idOpt, tipeOpt, typedOpt)
    }

    def read_astExpFun(): org.sireum.lang.ast.Exp.Fun = {
      val r = read_astExpFunT(F)
      return r
    }

    def read_astExpFunT(typeParsed: B): org.sireum.lang.ast.Exp.Fun = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpFun)
      }
      val context = reader.readISZ(reader.readString _)
      val params = reader.readISZ(read_astExpFunParam _)
      val exp = read_astAssignExp()
      val attr = read_astTypedAttr()
      return org.sireum.lang.ast.Exp.Fun(context, params, exp, attr)
    }

    def read_astExpForYield(): org.sireum.lang.ast.Exp.ForYield = {
      val r = read_astExpForYieldT(F)
      return r
    }

    def read_astExpForYieldT(typeParsed: B): org.sireum.lang.ast.Exp.ForYield = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpForYield)
      }
      val enumGens = reader.readISZ(read_astEnumGenFor _)
      val exp = read_astExp()
      val attr = read_astTypedAttr()
      return org.sireum.lang.ast.Exp.ForYield(enumGens, exp, attr)
    }

    def read_astExpQuant(): org.sireum.lang.ast.Exp.Quant = {
      val i = reader.curr
      val t = reader.readZ()
      t match {
        case Constants._astExpQuantType => val r = read_astExpQuantTypeT(T); return r
        case Constants._astExpQuantRange => val r = read_astExpQuantRangeT(T); return r
        case Constants._astExpQuantEach => val r = read_astExpQuantEachT(T); return r
        case _ =>
          reader.error(i, s"$t is not a valid type of org.sireum.lang.ast.Exp.Quant.")
          val r = read_astExpQuantEachT(T)
          return r
      }
    }

    def read_astExpQuantType(): org.sireum.lang.ast.Exp.QuantType = {
      val r = read_astExpQuantTypeT(F)
      return r
    }

    def read_astExpQuantTypeT(typeParsed: B): org.sireum.lang.ast.Exp.QuantType = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpQuantType)
      }
      val isForall = reader.readB()
      val fun = read_astExpFun()
      val attr = read_astAttr()
      return org.sireum.lang.ast.Exp.QuantType(isForall, fun, attr)
    }

    def read_astExpQuantRange(): org.sireum.lang.ast.Exp.QuantRange = {
      val r = read_astExpQuantRangeT(F)
      return r
    }

    def read_astExpQuantRangeT(typeParsed: B): org.sireum.lang.ast.Exp.QuantRange = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpQuantRange)
      }
      val isForall = reader.readB()
      val lo = read_astExp()
      val hi = read_astExp()
      val hiExact = reader.readB()
      val fun = read_astExpFun()
      val attr = read_astResolvedAttr()
      return org.sireum.lang.ast.Exp.QuantRange(isForall, lo, hi, hiExact, fun, attr)
    }

    def read_astExpQuantEach(): org.sireum.lang.ast.Exp.QuantEach = {
      val r = read_astExpQuantEachT(F)
      return r
    }

    def read_astExpQuantEachT(typeParsed: B): org.sireum.lang.ast.Exp.QuantEach = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpQuantEach)
      }
      val isForall = reader.readB()
      val seq = read_astExp()
      val fun = read_astExpFun()
      val attr = read_astResolvedAttr()
      return org.sireum.lang.ast.Exp.QuantEach(isForall, seq, fun, attr)
    }

    def read_astExpInput(): org.sireum.lang.ast.Exp.Input = {
      val r = read_astExpInputT(F)
      return r
    }

    def read_astExpInputT(typeParsed: B): org.sireum.lang.ast.Exp.Input = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpInput)
      }
      val exp = read_astExp()
      val attr = read_astAttr()
      return org.sireum.lang.ast.Exp.Input(exp, attr)
    }

    def read_astExpOld(): org.sireum.lang.ast.Exp.Old = {
      val r = read_astExpOldT(F)
      return r
    }

    def read_astExpOldT(typeParsed: B): org.sireum.lang.ast.Exp.Old = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpOld)
      }
      val exp = read_astExp()
      val attr = read_astAttr()
      return org.sireum.lang.ast.Exp.Old(exp, attr)
    }

    def read_astExpRS(): org.sireum.lang.ast.Exp.RS = {
      val r = read_astExpRST(F)
      return r
    }

    def read_astExpRST(typeParsed: B): org.sireum.lang.ast.Exp.RS = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpRS)
      }
      val rightToLeft = reader.readB()
      val refs = reader.readISZ(read_astExpRef _)
      val attr = read_astAttr()
      return org.sireum.lang.ast.Exp.RS(rightToLeft, refs, attr)
    }

    def read_astExpAt(): org.sireum.lang.ast.Exp.At = {
      val r = read_astExpAtT(F)
      return r
    }

    def read_astExpAtT(typeParsed: B): org.sireum.lang.ast.Exp.At = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpAt)
      }
      val tipeOpt = reader.readOption(read_astType _)
      val exp = read_astExp()
      val num = read_astExpLitZ()
      val linesFresh = reader.readISZ(read_astExpLitZ _)
      val attr = read_astAttr()
      return org.sireum.lang.ast.Exp.At(tipeOpt, exp, num, linesFresh, attr)
    }

    def read_astExpLoopIndex(): org.sireum.lang.ast.Exp.LoopIndex = {
      val r = read_astExpLoopIndexT(F)
      return r
    }

    def read_astExpLoopIndexT(typeParsed: B): org.sireum.lang.ast.Exp.LoopIndex = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpLoopIndex)
      }
      val tipeOpt = reader.readOption(read_astType _)
      val exp = read_astExpIdent()
      val attr = read_astTypedAttr()
      return org.sireum.lang.ast.Exp.LoopIndex(tipeOpt, exp, attr)
    }

    def read_astExpStateSeq(): org.sireum.lang.ast.Exp.StateSeq = {
      val r = read_astExpStateSeqT(F)
      return r
    }

    def read_astExpStateSeqT(typeParsed: B): org.sireum.lang.ast.Exp.StateSeq = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpStateSeq)
      }
      val id = read_astId()
      val fragments = reader.readISZ(read_astExpStateSeqFragment _)
      val attr = read_astAttr()
      return org.sireum.lang.ast.Exp.StateSeq(id, fragments, attr)
    }

    def read_astExpStateSeqFragment(): org.sireum.lang.ast.Exp.StateSeq.Fragment = {
      val r = read_astExpStateSeqFragmentT(F)
      return r
    }

    def read_astExpStateSeqFragmentT(typeParsed: B): org.sireum.lang.ast.Exp.StateSeq.Fragment = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpStateSeqFragment)
      }
      val id = read_astId()
      val exp = read_astExp()
      return org.sireum.lang.ast.Exp.StateSeq.Fragment(id, exp)
    }

    def read_astExpResult(): org.sireum.lang.ast.Exp.Result = {
      val r = read_astExpResultT(F)
      return r
    }

    def read_astExpResultT(typeParsed: B): org.sireum.lang.ast.Exp.Result = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpResult)
      }
      val tipeOpt = reader.readOption(read_astType _)
      val attr = read_astTypedAttr()
      return org.sireum.lang.ast.Exp.Result(tipeOpt, attr)
    }

    def read_astExpStrictPureBlock(): org.sireum.lang.ast.Exp.StrictPureBlock = {
      val r = read_astExpStrictPureBlockT(F)
      return r
    }

    def read_astExpStrictPureBlockT(typeParsed: B): org.sireum.lang.ast.Exp.StrictPureBlock = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpStrictPureBlock)
      }
      val block = read_astStmtBlock()
      val attr = read_astTypedAttr()
      return org.sireum.lang.ast.Exp.StrictPureBlock(block, attr)
    }

    def read_astExpLabeled(): org.sireum.lang.ast.Exp.Labeled = {
      val r = read_astExpLabeledT(F)
      return r
    }

    def read_astExpLabeledT(typeParsed: B): org.sireum.lang.ast.Exp.Labeled = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpLabeled)
      }
      val numOpt = reader.readOption(read_astExpLitZ _)
      val exp = read_astExp()
      val attr = read_astAttr()
      return org.sireum.lang.ast.Exp.Labeled(numOpt, exp, attr)
    }

    def read_astExpAssumeAgree(): org.sireum.lang.ast.Exp.AssumeAgree = {
      val r = read_astExpAssumeAgreeT(F)
      return r
    }

    def read_astExpAssumeAgreeT(typeParsed: B): org.sireum.lang.ast.Exp.AssumeAgree = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpAssumeAgree)
      }
      val channel = read_astExpLitString()
      val requiresClause = read_astMethodContractClaims()
      val inAgreeClause = read_astMethodContractClaims()
      val attr = read_astAttr()
      return org.sireum.lang.ast.Exp.AssumeAgree(channel, requiresClause, inAgreeClause, attr)
    }

    def read_astExpAssertAgree(): org.sireum.lang.ast.Exp.AssertAgree = {
      val r = read_astExpAssertAgreeT(F)
      return r
    }

    def read_astExpAssertAgreeT(typeParsed: B): org.sireum.lang.ast.Exp.AssertAgree = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpAssertAgree)
      }
      val channel = read_astExpLitString()
      val outAgreeClause = read_astMethodContractClaims()
      val attr = read_astAttr()
      return org.sireum.lang.ast.Exp.AssertAgree(channel, outAgreeClause, attr)
    }

    def read_astExpInfoFlowInvariant(): org.sireum.lang.ast.Exp.InfoFlowInvariant = {
      val r = read_astExpInfoFlowInvariantT(F)
      return r
    }

    def read_astExpInfoFlowInvariantT(typeParsed: B): org.sireum.lang.ast.Exp.InfoFlowInvariant = {
      if (!typeParsed) {
        reader.expectZ(Constants._astExpInfoFlowInvariant)
      }
      val flowInvariants = reader.readISZ(read_astMethodContractInfoFlowCase _)
      val attr = read_astAttr()
      return org.sireum.lang.ast.Exp.InfoFlowInvariant(flowInvariants, attr)
    }

    def read_astNamedArg(): org.sireum.lang.ast.NamedArg = {
      val r = read_astNamedArgT(F)
      return r
    }

    def read_astNamedArgT(typeParsed: B): org.sireum.lang.ast.NamedArg = {
      if (!typeParsed) {
        reader.expectZ(Constants._astNamedArg)
      }
      val id = read_astId()
      val arg = read_astExp()
      val index = reader.readZ()
      return org.sireum.lang.ast.NamedArg(id, arg, index)
    }

    def read_astId(): org.sireum.lang.ast.Id = {
      val r = read_astIdT(F)
      return r
    }

    def read_astIdT(typeParsed: B): org.sireum.lang.ast.Id = {
      if (!typeParsed) {
        reader.expectZ(Constants._astId)
      }
      val value = reader.readString()
      val attr = read_astAttr()
      return org.sireum.lang.ast.Id(value, attr)
    }

    def read_astName(): org.sireum.lang.ast.Name = {
      val r = read_astNameT(F)
      return r
    }

    def read_astNameT(typeParsed: B): org.sireum.lang.ast.Name = {
      if (!typeParsed) {
        reader.expectZ(Constants._astName)
      }
      val ids = reader.readISZ(read_astId _)
      val attr = read_astAttr()
      return org.sireum.lang.ast.Name(ids, attr)
    }

    def read_astBody(): org.sireum.lang.ast.Body = {
      val r = read_astBodyT(F)
      return r
    }

    def read_astBodyT(typeParsed: B): org.sireum.lang.ast.Body = {
      if (!typeParsed) {
        reader.expectZ(Constants._astBody)
      }
      val stmts = reader.readISZ(read_astStmt _)
      val undecls = reader.readISZ(read_astResolvedInfoLocalVar _)
      return org.sireum.lang.ast.Body(stmts, undecls)
    }

    def read_astAdtParam(): org.sireum.lang.ast.AdtParam = {
      val r = read_astAdtParamT(F)
      return r
    }

    def read_astAdtParamT(typeParsed: B): org.sireum.lang.ast.AdtParam = {
      if (!typeParsed) {
        reader.expectZ(Constants._astAdtParam)
      }
      val isHidden = reader.readB()
      val isVal = reader.readB()
      val id = read_astId()
      val tipe = read_astType()
      return org.sireum.lang.ast.AdtParam(isHidden, isVal, id, tipe)
    }

    def read_astAnnotation(): org.sireum.lang.ast.Annotation = {
      val r = read_astAnnotationT(F)
      return r
    }

    def read_astAnnotationT(typeParsed: B): org.sireum.lang.ast.Annotation = {
      if (!typeParsed) {
        reader.expectZ(Constants._astAnnotation)
      }
      val name = reader.readISZ(reader.readString _)
      val args = reader.readISZ(read_astLit _)
      return org.sireum.lang.ast.Annotation(name, args)
    }

    def read_astMethodSig(): org.sireum.lang.ast.MethodSig = {
      val r = read_astMethodSigT(F)
      return r
    }

    def read_astMethodSigT(typeParsed: B): org.sireum.lang.ast.MethodSig = {
      if (!typeParsed) {
        reader.expectZ(Constants._astMethodSig)
      }
      val purity = read_astPurityType()
      val annotations = reader.readISZ(read_astAnnotation _)
      val id = read_astId()
      val typeParams = reader.readISZ(read_astTypeParam _)
      val hasParams = reader.readB()
      val params = reader.readISZ(read_astParam _)
      val returnType = read_astType()
      return org.sireum.lang.ast.MethodSig(purity, annotations, id, typeParams, hasParams, params, returnType)
    }

    def read_astParam(): org.sireum.lang.ast.Param = {
      val r = read_astParamT(F)
      return r
    }

    def read_astParamT(typeParsed: B): org.sireum.lang.ast.Param = {
      if (!typeParsed) {
        reader.expectZ(Constants._astParam)
      }
      val isHidden = reader.readB()
      val id = read_astId()
      val tipe = read_astType()
      return org.sireum.lang.ast.Param(isHidden, id, tipe)
    }

    def read_astTypeParam(): org.sireum.lang.ast.TypeParam = {
      val r = read_astTypeParamT(F)
      return r
    }

    def read_astTypeParamT(typeParsed: B): org.sireum.lang.ast.TypeParam = {
      if (!typeParsed) {
        reader.expectZ(Constants._astTypeParam)
      }
      val id = read_astId()
      val kind = read_astTypedVarKindType()
      return org.sireum.lang.ast.TypeParam(id, kind)
    }

    def read_astAttr(): org.sireum.lang.ast.Attr = {
      val r = read_astAttrT(F)
      return r
    }

    def read_astAttrT(typeParsed: B): org.sireum.lang.ast.Attr = {
      if (!typeParsed) {
        reader.expectZ(Constants._astAttr)
      }
      val posOpt = reader.readOption(reader.readPosition _)
      return org.sireum.lang.ast.Attr(posOpt)
    }

    def read_astTypedAttr(): org.sireum.lang.ast.TypedAttr = {
      val r = read_astTypedAttrT(F)
      return r
    }

    def read_astTypedAttrT(typeParsed: B): org.sireum.lang.ast.TypedAttr = {
      if (!typeParsed) {
        reader.expectZ(Constants._astTypedAttr)
      }
      val posOpt = reader.readOption(reader.readPosition _)
      val typedOpt = reader.readOption(read_astTyped _)
      return org.sireum.lang.ast.TypedAttr(posOpt, typedOpt)
    }

    def read_astResolvedAttr(): org.sireum.lang.ast.ResolvedAttr = {
      val r = read_astResolvedAttrT(F)
      return r
    }

    def read_astResolvedAttrT(typeParsed: B): org.sireum.lang.ast.ResolvedAttr = {
      if (!typeParsed) {
        reader.expectZ(Constants._astResolvedAttr)
      }
      val posOpt = reader.readOption(reader.readPosition _)
      val resOpt = reader.readOption(read_astResolvedInfo _)
      val typedOpt = reader.readOption(read_astTyped _)
      return org.sireum.lang.ast.ResolvedAttr(posOpt, resOpt, typedOpt)
    }

    def read_astResolvedInfo(): org.sireum.lang.ast.ResolvedInfo = {
      val i = reader.curr
      val t = reader.readZ()
      t match {
        case Constants._astResolvedInfoBuiltIn => val r = read_astResolvedInfoBuiltInT(T); return r
        case Constants._astResolvedInfoPackage => val r = read_astResolvedInfoPackageT(T); return r
        case Constants._astResolvedInfoEnum => val r = read_astResolvedInfoEnumT(T); return r
        case Constants._astResolvedInfoEnumElement => val r = read_astResolvedInfoEnumElementT(T); return r
        case Constants._astResolvedInfoObject => val r = read_astResolvedInfoObjectT(T); return r
        case Constants._astResolvedInfoVar => val r = read_astResolvedInfoVarT(T); return r
        case Constants._astResolvedInfoMethod => val r = read_astResolvedInfoMethodT(T); return r
        case Constants._astResolvedInfoMethods => val r = read_astResolvedInfoMethodsT(T); return r
        case Constants._astResolvedInfoTuple => val r = read_astResolvedInfoTupleT(T); return r
        case Constants._astResolvedInfoLocalVar => val r = read_astResolvedInfoLocalVarT(T); return r
        case Constants._astResolvedInfoFact => val r = read_astResolvedInfoFactT(T); return r
        case Constants._astResolvedInfoTheorem => val r = read_astResolvedInfoTheoremT(T); return r
        case Constants._astResolvedInfoInv => val r = read_astResolvedInfoInvT(T); return r
        case _ =>
          reader.error(i, s"$t is not a valid type of org.sireum.lang.ast.ResolvedInfo.")
          val r = read_astResolvedInfoInvT(T)
          return r
      }
    }

    def read_astResolvedInfoBuiltInKindType(): org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.Type = {
      val r = reader.readZ()
      return org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.byOrdinal(r).get
    }

    def read_astResolvedInfoBuiltIn(): org.sireum.lang.ast.ResolvedInfo.BuiltIn = {
      val r = read_astResolvedInfoBuiltInT(F)
      return r
    }

    def read_astResolvedInfoBuiltInT(typeParsed: B): org.sireum.lang.ast.ResolvedInfo.BuiltIn = {
      if (!typeParsed) {
        reader.expectZ(Constants._astResolvedInfoBuiltIn)
      }
      val kind = read_astResolvedInfoBuiltInKindType()
      return org.sireum.lang.ast.ResolvedInfo.BuiltIn(kind)
    }

    def read_astResolvedInfoPackage(): org.sireum.lang.ast.ResolvedInfo.Package = {
      val r = read_astResolvedInfoPackageT(F)
      return r
    }

    def read_astResolvedInfoPackageT(typeParsed: B): org.sireum.lang.ast.ResolvedInfo.Package = {
      if (!typeParsed) {
        reader.expectZ(Constants._astResolvedInfoPackage)
      }
      val name = reader.readISZ(reader.readString _)
      return org.sireum.lang.ast.ResolvedInfo.Package(name)
    }

    def read_astResolvedInfoEnum(): org.sireum.lang.ast.ResolvedInfo.Enum = {
      val r = read_astResolvedInfoEnumT(F)
      return r
    }

    def read_astResolvedInfoEnumT(typeParsed: B): org.sireum.lang.ast.ResolvedInfo.Enum = {
      if (!typeParsed) {
        reader.expectZ(Constants._astResolvedInfoEnum)
      }
      val name = reader.readISZ(reader.readString _)
      return org.sireum.lang.ast.ResolvedInfo.Enum(name)
    }

    def read_astResolvedInfoEnumElement(): org.sireum.lang.ast.ResolvedInfo.EnumElement = {
      val r = read_astResolvedInfoEnumElementT(F)
      return r
    }

    def read_astResolvedInfoEnumElementT(typeParsed: B): org.sireum.lang.ast.ResolvedInfo.EnumElement = {
      if (!typeParsed) {
        reader.expectZ(Constants._astResolvedInfoEnumElement)
      }
      val owner = reader.readISZ(reader.readString _)
      val name = reader.readString()
      val ordinal = reader.readZ()
      return org.sireum.lang.ast.ResolvedInfo.EnumElement(owner, name, ordinal)
    }

    def read_astResolvedInfoObject(): org.sireum.lang.ast.ResolvedInfo.Object = {
      val r = read_astResolvedInfoObjectT(F)
      return r
    }

    def read_astResolvedInfoObjectT(typeParsed: B): org.sireum.lang.ast.ResolvedInfo.Object = {
      if (!typeParsed) {
        reader.expectZ(Constants._astResolvedInfoObject)
      }
      val name = reader.readISZ(reader.readString _)
      return org.sireum.lang.ast.ResolvedInfo.Object(name)
    }

    def read_astResolvedInfoVar(): org.sireum.lang.ast.ResolvedInfo.Var = {
      val r = read_astResolvedInfoVarT(F)
      return r
    }

    def read_astResolvedInfoVarT(typeParsed: B): org.sireum.lang.ast.ResolvedInfo.Var = {
      if (!typeParsed) {
        reader.expectZ(Constants._astResolvedInfoVar)
      }
      val isInObject = reader.readB()
      val isSpec = reader.readB()
      val isVal = reader.readB()
      val owner = reader.readISZ(reader.readString _)
      val id = reader.readString()
      return org.sireum.lang.ast.ResolvedInfo.Var(isInObject, isSpec, isVal, owner, id)
    }

    def read_astResolvedInfoMethod(): org.sireum.lang.ast.ResolvedInfo.Method = {
      val r = read_astResolvedInfoMethodT(F)
      return r
    }

    def read_astResolvedInfoMethodT(typeParsed: B): org.sireum.lang.ast.ResolvedInfo.Method = {
      if (!typeParsed) {
        reader.expectZ(Constants._astResolvedInfoMethod)
      }
      val isInObject = reader.readB()
      val mode = read_astMethodModeType()
      val typeParams = reader.readISZ(reader.readString _)
      val owner = reader.readISZ(reader.readString _)
      val id = reader.readString()
      val paramNames = reader.readISZ(reader.readString _)
      val tpeOpt = reader.readOption(read_astTypedFun _)
      val reads = reader.readISZ(read_astResolvedInfo _)
      val writes = reader.readISZ(read_astResolvedInfo _)
      return org.sireum.lang.ast.ResolvedInfo.Method(isInObject, mode, typeParams, owner, id, paramNames, tpeOpt, reads, writes)
    }

    def read_astResolvedInfoMethods(): org.sireum.lang.ast.ResolvedInfo.Methods = {
      val r = read_astResolvedInfoMethodsT(F)
      return r
    }

    def read_astResolvedInfoMethodsT(typeParsed: B): org.sireum.lang.ast.ResolvedInfo.Methods = {
      if (!typeParsed) {
        reader.expectZ(Constants._astResolvedInfoMethods)
      }
      val methods = reader.readISZ(read_astResolvedInfoMethod _)
      return org.sireum.lang.ast.ResolvedInfo.Methods(methods)
    }

    def read_astResolvedInfoTuple(): org.sireum.lang.ast.ResolvedInfo.Tuple = {
      val r = read_astResolvedInfoTupleT(F)
      return r
    }

    def read_astResolvedInfoTupleT(typeParsed: B): org.sireum.lang.ast.ResolvedInfo.Tuple = {
      if (!typeParsed) {
        reader.expectZ(Constants._astResolvedInfoTuple)
      }
      val size = reader.readZ()
      val index = reader.readZ()
      return org.sireum.lang.ast.ResolvedInfo.Tuple(size, index)
    }

    def read_astResolvedInfoLocalVarScopeType(): org.sireum.lang.ast.ResolvedInfo.LocalVar.Scope.Type = {
      val r = reader.readZ()
      return org.sireum.lang.ast.ResolvedInfo.LocalVar.Scope.byOrdinal(r).get
    }

    def read_astResolvedInfoLocalVar(): org.sireum.lang.ast.ResolvedInfo.LocalVar = {
      val r = read_astResolvedInfoLocalVarT(F)
      return r
    }

    def read_astResolvedInfoLocalVarT(typeParsed: B): org.sireum.lang.ast.ResolvedInfo.LocalVar = {
      if (!typeParsed) {
        reader.expectZ(Constants._astResolvedInfoLocalVar)
      }
      val context = reader.readISZ(reader.readString _)
      val scope = read_astResolvedInfoLocalVarScopeType()
      val isSpec = reader.readB()
      val isVal = reader.readB()
      val id = reader.readString()
      return org.sireum.lang.ast.ResolvedInfo.LocalVar(context, scope, isSpec, isVal, id)
    }

    def read_astResolvedInfoFact(): org.sireum.lang.ast.ResolvedInfo.Fact = {
      val r = read_astResolvedInfoFactT(F)
      return r
    }

    def read_astResolvedInfoFactT(typeParsed: B): org.sireum.lang.ast.ResolvedInfo.Fact = {
      if (!typeParsed) {
        reader.expectZ(Constants._astResolvedInfoFact)
      }
      val name = reader.readISZ(reader.readString _)
      return org.sireum.lang.ast.ResolvedInfo.Fact(name)
    }

    def read_astResolvedInfoTheorem(): org.sireum.lang.ast.ResolvedInfo.Theorem = {
      val r = read_astResolvedInfoTheoremT(F)
      return r
    }

    def read_astResolvedInfoTheoremT(typeParsed: B): org.sireum.lang.ast.ResolvedInfo.Theorem = {
      if (!typeParsed) {
        reader.expectZ(Constants._astResolvedInfoTheorem)
      }
      val name = reader.readISZ(reader.readString _)
      return org.sireum.lang.ast.ResolvedInfo.Theorem(name)
    }

    def read_astResolvedInfoInv(): org.sireum.lang.ast.ResolvedInfo.Inv = {
      val r = read_astResolvedInfoInvT(F)
      return r
    }

    def read_astResolvedInfoInvT(typeParsed: B): org.sireum.lang.ast.ResolvedInfo.Inv = {
      if (!typeParsed) {
        reader.expectZ(Constants._astResolvedInfoInv)
      }
      val isInObject = reader.readB()
      val owner = reader.readISZ(reader.readString _)
      val id = reader.readString()
      return org.sireum.lang.ast.ResolvedInfo.Inv(isInObject, owner, id)
    }

    def read_astTruthTableRow(): org.sireum.lang.ast.TruthTable.Row = {
      val r = read_astTruthTableRowT(F)
      return r
    }

    def read_astTruthTableRowT(typeParsed: B): org.sireum.lang.ast.TruthTable.Row = {
      if (!typeParsed) {
        reader.expectZ(Constants._astTruthTableRow)
      }
      val assignment = read_astTruthTableAssignment()
      val separator = reader.readPosition()
      val values = read_astTruthTableAssignment()
      return org.sireum.lang.ast.TruthTable.Row(assignment, separator, values)
    }

    def read_astTruthTableAssignment(): org.sireum.lang.ast.TruthTable.Assignment = {
      val r = read_astTruthTableAssignmentT(F)
      return r
    }

    def read_astTruthTableAssignmentT(typeParsed: B): org.sireum.lang.ast.TruthTable.Assignment = {
      if (!typeParsed) {
        reader.expectZ(Constants._astTruthTableAssignment)
      }
      val values = reader.readISZ(read_astExpLitB _)
      val attr = read_astAttr()
      return org.sireum.lang.ast.TruthTable.Assignment(values, attr)
    }

    def read_astTruthTableConclusion(): org.sireum.lang.ast.TruthTable.Conclusion = {
      val i = reader.curr
      val t = reader.readZ()
      t match {
        case Constants._astTruthTableConclusionValidity => val r = read_astTruthTableConclusionValidityT(T); return r
        case Constants._astTruthTableConclusionTautology => val r = read_astTruthTableConclusionTautologyT(T); return r
        case Constants._astTruthTableConclusionContradictory => val r = read_astTruthTableConclusionContradictoryT(T); return r
        case Constants._astTruthTableConclusionContingent => val r = read_astTruthTableConclusionContingentT(T); return r
        case _ =>
          reader.error(i, s"$t is not a valid type of org.sireum.lang.ast.TruthTable.Conclusion.")
          val r = read_astTruthTableConclusionContingentT(T)
          return r
      }
    }

    def read_astTruthTableConclusionValidity(): org.sireum.lang.ast.TruthTable.Conclusion.Validity = {
      val r = read_astTruthTableConclusionValidityT(F)
      return r
    }

    def read_astTruthTableConclusionValidityT(typeParsed: B): org.sireum.lang.ast.TruthTable.Conclusion.Validity = {
      if (!typeParsed) {
        reader.expectZ(Constants._astTruthTableConclusionValidity)
      }
      val isValid = reader.readB()
      val assignments = reader.readISZ(read_astTruthTableAssignment _)
      val attr = read_astAttr()
      return org.sireum.lang.ast.TruthTable.Conclusion.Validity(isValid, assignments, attr)
    }

    def read_astTruthTableConclusionTautology(): org.sireum.lang.ast.TruthTable.Conclusion.Tautology = {
      val r = read_astTruthTableConclusionTautologyT(F)
      return r
    }

    def read_astTruthTableConclusionTautologyT(typeParsed: B): org.sireum.lang.ast.TruthTable.Conclusion.Tautology = {
      if (!typeParsed) {
        reader.expectZ(Constants._astTruthTableConclusionTautology)
      }
      val attr = read_astAttr()
      return org.sireum.lang.ast.TruthTable.Conclusion.Tautology(attr)
    }

    def read_astTruthTableConclusionContradictory(): org.sireum.lang.ast.TruthTable.Conclusion.Contradictory = {
      val r = read_astTruthTableConclusionContradictoryT(F)
      return r
    }

    def read_astTruthTableConclusionContradictoryT(typeParsed: B): org.sireum.lang.ast.TruthTable.Conclusion.Contradictory = {
      if (!typeParsed) {
        reader.expectZ(Constants._astTruthTableConclusionContradictory)
      }
      val attr = read_astAttr()
      return org.sireum.lang.ast.TruthTable.Conclusion.Contradictory(attr)
    }

    def read_astTruthTableConclusionContingent(): org.sireum.lang.ast.TruthTable.Conclusion.Contingent = {
      val r = read_astTruthTableConclusionContingentT(F)
      return r
    }

    def read_astTruthTableConclusionContingentT(typeParsed: B): org.sireum.lang.ast.TruthTable.Conclusion.Contingent = {
      if (!typeParsed) {
        reader.expectZ(Constants._astTruthTableConclusionContingent)
      }
      val trueAssignments = reader.readISZ(read_astTruthTableAssignment _)
      val falseAssignments = reader.readISZ(read_astTruthTableAssignment _)
      val attr = read_astAttr()
      return org.sireum.lang.ast.TruthTable.Conclusion.Contingent(trueAssignments, falseAssignments, attr)
    }

    def read_astPurityType(): org.sireum.lang.ast.Purity.Type = {
      val r = reader.readZ()
      return org.sireum.lang.ast.Purity.byOrdinal(r).get
    }

    def read_astMethodModeType(): org.sireum.lang.ast.MethodMode.Type = {
      val r = reader.readZ()
      return org.sireum.lang.ast.MethodMode.byOrdinal(r).get
    }

    def read_astTyped(): org.sireum.lang.ast.Typed = {
      val i = reader.curr
      val t = reader.readZ()
      t match {
        case Constants._astTypedName => val r = read_astTypedNameT(T); return r
        case Constants._astTypedTuple => val r = read_astTypedTupleT(T); return r
        case Constants._astTypedFun => val r = read_astTypedFunT(T); return r
        case Constants._astTypedTypeVar => val r = read_astTypedTypeVarT(T); return r
        case Constants._astTypedPackage => val r = read_astTypedPackageT(T); return r
        case Constants._astTypedObject => val r = read_astTypedObjectT(T); return r
        case Constants._astTypedEnum => val r = read_astTypedEnumT(T); return r
        case Constants._astTypedMethod => val r = read_astTypedMethodT(T); return r
        case Constants._astTypedMethods => val r = read_astTypedMethodsT(T); return r
        case Constants._astTypedFact => val r = read_astTypedFactT(T); return r
        case Constants._astTypedTheorem => val r = read_astTypedTheoremT(T); return r
        case Constants._astTypedInv => val r = read_astTypedInvT(T); return r
        case _ =>
          reader.error(i, s"$t is not a valid type of org.sireum.lang.ast.Typed.")
          val r = read_astTypedInvT(T)
          return r
      }
    }

    def read_astTypedVarKindType(): org.sireum.lang.ast.Typed.VarKind.Type = {
      val r = reader.readZ()
      return org.sireum.lang.ast.Typed.VarKind.byOrdinal(r).get
    }

    def read_astTypedName(): org.sireum.lang.ast.Typed.Name = {
      val r = read_astTypedNameT(F)
      return r
    }

    def read_astTypedNameT(typeParsed: B): org.sireum.lang.ast.Typed.Name = {
      if (!typeParsed) {
        reader.expectZ(Constants._astTypedName)
      }
      val ids = reader.readISZ(reader.readString _)
      val args = reader.readISZ(read_astTyped _)
      return org.sireum.lang.ast.Typed.Name(ids, args)
    }

    def read_astTypedTuple(): org.sireum.lang.ast.Typed.Tuple = {
      val r = read_astTypedTupleT(F)
      return r
    }

    def read_astTypedTupleT(typeParsed: B): org.sireum.lang.ast.Typed.Tuple = {
      if (!typeParsed) {
        reader.expectZ(Constants._astTypedTuple)
      }
      val args = reader.readISZ(read_astTyped _)
      return org.sireum.lang.ast.Typed.Tuple(args)
    }

    def read_astTypedFun(): org.sireum.lang.ast.Typed.Fun = {
      val r = read_astTypedFunT(F)
      return r
    }

    def read_astTypedFunT(typeParsed: B): org.sireum.lang.ast.Typed.Fun = {
      if (!typeParsed) {
        reader.expectZ(Constants._astTypedFun)
      }
      val purity = read_astPurityType()
      val isByName = reader.readB()
      val args = reader.readISZ(read_astTyped _)
      val ret = read_astTyped()
      return org.sireum.lang.ast.Typed.Fun(purity, isByName, args, ret)
    }

    def read_astTypedTypeVar(): org.sireum.lang.ast.Typed.TypeVar = {
      val r = read_astTypedTypeVarT(F)
      return r
    }

    def read_astTypedTypeVarT(typeParsed: B): org.sireum.lang.ast.Typed.TypeVar = {
      if (!typeParsed) {
        reader.expectZ(Constants._astTypedTypeVar)
      }
      val id = reader.readString()
      val kind = read_astTypedVarKindType()
      return org.sireum.lang.ast.Typed.TypeVar(id, kind)
    }

    def read_astTypedPackage(): org.sireum.lang.ast.Typed.Package = {
      val r = read_astTypedPackageT(F)
      return r
    }

    def read_astTypedPackageT(typeParsed: B): org.sireum.lang.ast.Typed.Package = {
      if (!typeParsed) {
        reader.expectZ(Constants._astTypedPackage)
      }
      val name = reader.readISZ(reader.readString _)
      return org.sireum.lang.ast.Typed.Package(name)
    }

    def read_astTypedObject(): org.sireum.lang.ast.Typed.Object = {
      val r = read_astTypedObjectT(F)
      return r
    }

    def read_astTypedObjectT(typeParsed: B): org.sireum.lang.ast.Typed.Object = {
      if (!typeParsed) {
        reader.expectZ(Constants._astTypedObject)
      }
      val owner = reader.readISZ(reader.readString _)
      val id = reader.readString()
      return org.sireum.lang.ast.Typed.Object(owner, id)
    }

    def read_astTypedEnum(): org.sireum.lang.ast.Typed.Enum = {
      val r = read_astTypedEnumT(F)
      return r
    }

    def read_astTypedEnumT(typeParsed: B): org.sireum.lang.ast.Typed.Enum = {
      if (!typeParsed) {
        reader.expectZ(Constants._astTypedEnum)
      }
      val name = reader.readISZ(reader.readString _)
      return org.sireum.lang.ast.Typed.Enum(name)
    }

    def read_astTypedMethod(): org.sireum.lang.ast.Typed.Method = {
      val r = read_astTypedMethodT(F)
      return r
    }

    def read_astTypedMethodT(typeParsed: B): org.sireum.lang.ast.Typed.Method = {
      if (!typeParsed) {
        reader.expectZ(Constants._astTypedMethod)
      }
      val isInObject = reader.readB()
      val mode = read_astMethodModeType()
      val typeParams = reader.readISZ(reader.readString _)
      val owner = reader.readISZ(reader.readString _)
      val name = reader.readString()
      val paramNames = reader.readISZ(reader.readString _)
      val tpe = read_astTypedFun()
      return org.sireum.lang.ast.Typed.Method(isInObject, mode, typeParams, owner, name, paramNames, tpe)
    }

    def read_astTypedMethods(): org.sireum.lang.ast.Typed.Methods = {
      val r = read_astTypedMethodsT(F)
      return r
    }

    def read_astTypedMethodsT(typeParsed: B): org.sireum.lang.ast.Typed.Methods = {
      if (!typeParsed) {
        reader.expectZ(Constants._astTypedMethods)
      }
      val methods = reader.readISZ(read_astTypedMethod _)
      return org.sireum.lang.ast.Typed.Methods(methods)
    }

    def read_astTypedFact(): org.sireum.lang.ast.Typed.Fact = {
      val r = read_astTypedFactT(F)
      return r
    }

    def read_astTypedFactT(typeParsed: B): org.sireum.lang.ast.Typed.Fact = {
      if (!typeParsed) {
        reader.expectZ(Constants._astTypedFact)
      }
      val owner = reader.readISZ(reader.readString _)
      val id = reader.readString()
      return org.sireum.lang.ast.Typed.Fact(owner, id)
    }

    def read_astTypedTheorem(): org.sireum.lang.ast.Typed.Theorem = {
      val r = read_astTypedTheoremT(F)
      return r
    }

    def read_astTypedTheoremT(typeParsed: B): org.sireum.lang.ast.Typed.Theorem = {
      if (!typeParsed) {
        reader.expectZ(Constants._astTypedTheorem)
      }
      val owner = reader.readISZ(reader.readString _)
      val id = reader.readString()
      return org.sireum.lang.ast.Typed.Theorem(owner, id)
    }

    def read_astTypedInv(): org.sireum.lang.ast.Typed.Inv = {
      val r = read_astTypedInvT(F)
      return r
    }

    def read_astTypedInvT(typeParsed: B): org.sireum.lang.ast.Typed.Inv = {
      if (!typeParsed) {
        reader.expectZ(Constants._astTypedInv)
      }
      val isInObject = reader.readB()
      val owner = reader.readISZ(reader.readString _)
      val id = reader.readString()
      return org.sireum.lang.ast.Typed.Inv(isInObject, owner, id)
    }

  }

  def to[T](data: ISZ[U8], f: Reader => T): Either[T, MessagePack.ErrorMsg] = {
    val rd = Reader.Default(MessagePack.reader(data))
    rd.reader.init()
    val r = f(rd)
    rd.errorOpt match {
      case Some(e) => return Either.Right(e)
      case _ => return Either.Left(r)
    }
  }

  def from_symbolScope(o: org.sireum.lang.symbol.Scope, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_symbolScope(o)
    return w.result
  }

  def to_symbolScope(data: ISZ[U8]): Either[org.sireum.lang.symbol.Scope, MessagePack.ErrorMsg] = {
    def f_symbolScope(reader: Reader): org.sireum.lang.symbol.Scope = {
      val r = reader.read_symbolScope()
      return r
    }
    val r = to(data, f_symbolScope _)
    return r
  }

  def from_symbolScopeLocal(o: org.sireum.lang.symbol.Scope.Local, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_symbolScopeLocal(o)
    return w.result
  }

  def to_symbolScopeLocal(data: ISZ[U8]): Either[org.sireum.lang.symbol.Scope.Local, MessagePack.ErrorMsg] = {
    def f_symbolScopeLocal(reader: Reader): org.sireum.lang.symbol.Scope.Local = {
      val r = reader.read_symbolScopeLocal()
      return r
    }
    val r = to(data, f_symbolScopeLocal _)
    return r
  }

  def from_symbolScopeGlobal(o: org.sireum.lang.symbol.Scope.Global, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_symbolScopeGlobal(o)
    return w.result
  }

  def to_symbolScopeGlobal(data: ISZ[U8]): Either[org.sireum.lang.symbol.Scope.Global, MessagePack.ErrorMsg] = {
    def f_symbolScopeGlobal(reader: Reader): org.sireum.lang.symbol.Scope.Global = {
      val r = reader.read_symbolScopeGlobal()
      return r
    }
    val r = to(data, f_symbolScopeGlobal _)
    return r
  }

  def from_symbolInfo(o: org.sireum.lang.symbol.Info, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_symbolInfo(o)
    return w.result
  }

  def to_symbolInfo(data: ISZ[U8]): Either[org.sireum.lang.symbol.Info, MessagePack.ErrorMsg] = {
    def f_symbolInfo(reader: Reader): org.sireum.lang.symbol.Info = {
      val r = reader.read_symbolInfo()
      return r
    }
    val r = to(data, f_symbolInfo _)
    return r
  }

  def from_symbolInfoPackage(o: org.sireum.lang.symbol.Info.Package, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_symbolInfoPackage(o)
    return w.result
  }

  def to_symbolInfoPackage(data: ISZ[U8]): Either[org.sireum.lang.symbol.Info.Package, MessagePack.ErrorMsg] = {
    def f_symbolInfoPackage(reader: Reader): org.sireum.lang.symbol.Info.Package = {
      val r = reader.read_symbolInfoPackage()
      return r
    }
    val r = to(data, f_symbolInfoPackage _)
    return r
  }

  def from_symbolInfoVar(o: org.sireum.lang.symbol.Info.Var, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_symbolInfoVar(o)
    return w.result
  }

  def to_symbolInfoVar(data: ISZ[U8]): Either[org.sireum.lang.symbol.Info.Var, MessagePack.ErrorMsg] = {
    def f_symbolInfoVar(reader: Reader): org.sireum.lang.symbol.Info.Var = {
      val r = reader.read_symbolInfoVar()
      return r
    }
    val r = to(data, f_symbolInfoVar _)
    return r
  }

  def from_symbolInfoSpecVar(o: org.sireum.lang.symbol.Info.SpecVar, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_symbolInfoSpecVar(o)
    return w.result
  }

  def to_symbolInfoSpecVar(data: ISZ[U8]): Either[org.sireum.lang.symbol.Info.SpecVar, MessagePack.ErrorMsg] = {
    def f_symbolInfoSpecVar(reader: Reader): org.sireum.lang.symbol.Info.SpecVar = {
      val r = reader.read_symbolInfoSpecVar()
      return r
    }
    val r = to(data, f_symbolInfoSpecVar _)
    return r
  }

  def from_symbolInfoRsVal(o: org.sireum.lang.symbol.Info.RsVal, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_symbolInfoRsVal(o)
    return w.result
  }

  def to_symbolInfoRsVal(data: ISZ[U8]): Either[org.sireum.lang.symbol.Info.RsVal, MessagePack.ErrorMsg] = {
    def f_symbolInfoRsVal(reader: Reader): org.sireum.lang.symbol.Info.RsVal = {
      val r = reader.read_symbolInfoRsVal()
      return r
    }
    val r = to(data, f_symbolInfoRsVal _)
    return r
  }

  def from_symbolInfoMethod(o: org.sireum.lang.symbol.Info.Method, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_symbolInfoMethod(o)
    return w.result
  }

  def to_symbolInfoMethod(data: ISZ[U8]): Either[org.sireum.lang.symbol.Info.Method, MessagePack.ErrorMsg] = {
    def f_symbolInfoMethod(reader: Reader): org.sireum.lang.symbol.Info.Method = {
      val r = reader.read_symbolInfoMethod()
      return r
    }
    val r = to(data, f_symbolInfoMethod _)
    return r
  }

  def from_symbolInfoSpecMethod(o: org.sireum.lang.symbol.Info.SpecMethod, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_symbolInfoSpecMethod(o)
    return w.result
  }

  def to_symbolInfoSpecMethod(data: ISZ[U8]): Either[org.sireum.lang.symbol.Info.SpecMethod, MessagePack.ErrorMsg] = {
    def f_symbolInfoSpecMethod(reader: Reader): org.sireum.lang.symbol.Info.SpecMethod = {
      val r = reader.read_symbolInfoSpecMethod()
      return r
    }
    val r = to(data, f_symbolInfoSpecMethod _)
    return r
  }

  def from_symbolInfoObject(o: org.sireum.lang.symbol.Info.Object, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_symbolInfoObject(o)
    return w.result
  }

  def to_symbolInfoObject(data: ISZ[U8]): Either[org.sireum.lang.symbol.Info.Object, MessagePack.ErrorMsg] = {
    def f_symbolInfoObject(reader: Reader): org.sireum.lang.symbol.Info.Object = {
      val r = reader.read_symbolInfoObject()
      return r
    }
    val r = to(data, f_symbolInfoObject _)
    return r
  }

  def from_symbolInfoExtMethod(o: org.sireum.lang.symbol.Info.ExtMethod, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_symbolInfoExtMethod(o)
    return w.result
  }

  def to_symbolInfoExtMethod(data: ISZ[U8]): Either[org.sireum.lang.symbol.Info.ExtMethod, MessagePack.ErrorMsg] = {
    def f_symbolInfoExtMethod(reader: Reader): org.sireum.lang.symbol.Info.ExtMethod = {
      val r = reader.read_symbolInfoExtMethod()
      return r
    }
    val r = to(data, f_symbolInfoExtMethod _)
    return r
  }

  def from_symbolInfoJustMethod(o: org.sireum.lang.symbol.Info.JustMethod, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_symbolInfoJustMethod(o)
    return w.result
  }

  def to_symbolInfoJustMethod(data: ISZ[U8]): Either[org.sireum.lang.symbol.Info.JustMethod, MessagePack.ErrorMsg] = {
    def f_symbolInfoJustMethod(reader: Reader): org.sireum.lang.symbol.Info.JustMethod = {
      val r = reader.read_symbolInfoJustMethod()
      return r
    }
    val r = to(data, f_symbolInfoJustMethod _)
    return r
  }

  def from_symbolInfoEnum(o: org.sireum.lang.symbol.Info.Enum, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_symbolInfoEnum(o)
    return w.result
  }

  def to_symbolInfoEnum(data: ISZ[U8]): Either[org.sireum.lang.symbol.Info.Enum, MessagePack.ErrorMsg] = {
    def f_symbolInfoEnum(reader: Reader): org.sireum.lang.symbol.Info.Enum = {
      val r = reader.read_symbolInfoEnum()
      return r
    }
    val r = to(data, f_symbolInfoEnum _)
    return r
  }

  def from_symbolInfoEnumElement(o: org.sireum.lang.symbol.Info.EnumElement, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_symbolInfoEnumElement(o)
    return w.result
  }

  def to_symbolInfoEnumElement(data: ISZ[U8]): Either[org.sireum.lang.symbol.Info.EnumElement, MessagePack.ErrorMsg] = {
    def f_symbolInfoEnumElement(reader: Reader): org.sireum.lang.symbol.Info.EnumElement = {
      val r = reader.read_symbolInfoEnumElement()
      return r
    }
    val r = to(data, f_symbolInfoEnumElement _)
    return r
  }

  def from_symbolInfoLocalVar(o: org.sireum.lang.symbol.Info.LocalVar, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_symbolInfoLocalVar(o)
    return w.result
  }

  def to_symbolInfoLocalVar(data: ISZ[U8]): Either[org.sireum.lang.symbol.Info.LocalVar, MessagePack.ErrorMsg] = {
    def f_symbolInfoLocalVar(reader: Reader): org.sireum.lang.symbol.Info.LocalVar = {
      val r = reader.read_symbolInfoLocalVar()
      return r
    }
    val r = to(data, f_symbolInfoLocalVar _)
    return r
  }

  def from_symbolInfoFact(o: org.sireum.lang.symbol.Info.Fact, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_symbolInfoFact(o)
    return w.result
  }

  def to_symbolInfoFact(data: ISZ[U8]): Either[org.sireum.lang.symbol.Info.Fact, MessagePack.ErrorMsg] = {
    def f_symbolInfoFact(reader: Reader): org.sireum.lang.symbol.Info.Fact = {
      val r = reader.read_symbolInfoFact()
      return r
    }
    val r = to(data, f_symbolInfoFact _)
    return r
  }

  def from_symbolInfoTheorem(o: org.sireum.lang.symbol.Info.Theorem, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_symbolInfoTheorem(o)
    return w.result
  }

  def to_symbolInfoTheorem(data: ISZ[U8]): Either[org.sireum.lang.symbol.Info.Theorem, MessagePack.ErrorMsg] = {
    def f_symbolInfoTheorem(reader: Reader): org.sireum.lang.symbol.Info.Theorem = {
      val r = reader.read_symbolInfoTheorem()
      return r
    }
    val r = to(data, f_symbolInfoTheorem _)
    return r
  }

  def from_symbolInfoInv(o: org.sireum.lang.symbol.Info.Inv, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_symbolInfoInv(o)
    return w.result
  }

  def to_symbolInfoInv(data: ISZ[U8]): Either[org.sireum.lang.symbol.Info.Inv, MessagePack.ErrorMsg] = {
    def f_symbolInfoInv(reader: Reader): org.sireum.lang.symbol.Info.Inv = {
      val r = reader.read_symbolInfoInv()
      return r
    }
    val r = to(data, f_symbolInfoInv _)
    return r
  }

  def from_symbolTypeInfo(o: org.sireum.lang.symbol.TypeInfo, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_symbolTypeInfo(o)
    return w.result
  }

  def to_symbolTypeInfo(data: ISZ[U8]): Either[org.sireum.lang.symbol.TypeInfo, MessagePack.ErrorMsg] = {
    def f_symbolTypeInfo(reader: Reader): org.sireum.lang.symbol.TypeInfo = {
      val r = reader.read_symbolTypeInfo()
      return r
    }
    val r = to(data, f_symbolTypeInfo _)
    return r
  }

  def from_symbolTypeInfoSubZ(o: org.sireum.lang.symbol.TypeInfo.SubZ, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_symbolTypeInfoSubZ(o)
    return w.result
  }

  def to_symbolTypeInfoSubZ(data: ISZ[U8]): Either[org.sireum.lang.symbol.TypeInfo.SubZ, MessagePack.ErrorMsg] = {
    def f_symbolTypeInfoSubZ(reader: Reader): org.sireum.lang.symbol.TypeInfo.SubZ = {
      val r = reader.read_symbolTypeInfoSubZ()
      return r
    }
    val r = to(data, f_symbolTypeInfoSubZ _)
    return r
  }

  def from_symbolTypeInfoEnum(o: org.sireum.lang.symbol.TypeInfo.Enum, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_symbolTypeInfoEnum(o)
    return w.result
  }

  def to_symbolTypeInfoEnum(data: ISZ[U8]): Either[org.sireum.lang.symbol.TypeInfo.Enum, MessagePack.ErrorMsg] = {
    def f_symbolTypeInfoEnum(reader: Reader): org.sireum.lang.symbol.TypeInfo.Enum = {
      val r = reader.read_symbolTypeInfoEnum()
      return r
    }
    val r = to(data, f_symbolTypeInfoEnum _)
    return r
  }

  def from_symbolTypeInfoSig(o: org.sireum.lang.symbol.TypeInfo.Sig, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_symbolTypeInfoSig(o)
    return w.result
  }

  def to_symbolTypeInfoSig(data: ISZ[U8]): Either[org.sireum.lang.symbol.TypeInfo.Sig, MessagePack.ErrorMsg] = {
    def f_symbolTypeInfoSig(reader: Reader): org.sireum.lang.symbol.TypeInfo.Sig = {
      val r = reader.read_symbolTypeInfoSig()
      return r
    }
    val r = to(data, f_symbolTypeInfoSig _)
    return r
  }

  def from_symbolTypeInfoName(o: org.sireum.lang.symbol.TypeInfo.Name, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_symbolTypeInfoName(o)
    return w.result
  }

  def to_symbolTypeInfoName(data: ISZ[U8]): Either[org.sireum.lang.symbol.TypeInfo.Name, MessagePack.ErrorMsg] = {
    def f_symbolTypeInfoName(reader: Reader): org.sireum.lang.symbol.TypeInfo.Name = {
      val r = reader.read_symbolTypeInfoName()
      return r
    }
    val r = to(data, f_symbolTypeInfoName _)
    return r
  }

  def from_symbolTypeInfoAdt(o: org.sireum.lang.symbol.TypeInfo.Adt, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_symbolTypeInfoAdt(o)
    return w.result
  }

  def to_symbolTypeInfoAdt(data: ISZ[U8]): Either[org.sireum.lang.symbol.TypeInfo.Adt, MessagePack.ErrorMsg] = {
    def f_symbolTypeInfoAdt(reader: Reader): org.sireum.lang.symbol.TypeInfo.Adt = {
      val r = reader.read_symbolTypeInfoAdt()
      return r
    }
    val r = to(data, f_symbolTypeInfoAdt _)
    return r
  }

  def from_symbolTypeInfoTypeAlias(o: org.sireum.lang.symbol.TypeInfo.TypeAlias, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_symbolTypeInfoTypeAlias(o)
    return w.result
  }

  def to_symbolTypeInfoTypeAlias(data: ISZ[U8]): Either[org.sireum.lang.symbol.TypeInfo.TypeAlias, MessagePack.ErrorMsg] = {
    def f_symbolTypeInfoTypeAlias(reader: Reader): org.sireum.lang.symbol.TypeInfo.TypeAlias = {
      val r = reader.read_symbolTypeInfoTypeAlias()
      return r
    }
    val r = to(data, f_symbolTypeInfoTypeAlias _)
    return r
  }

  def from_symbolTypeInfoTypeVar(o: org.sireum.lang.symbol.TypeInfo.TypeVar, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_symbolTypeInfoTypeVar(o)
    return w.result
  }

  def to_symbolTypeInfoTypeVar(data: ISZ[U8]): Either[org.sireum.lang.symbol.TypeInfo.TypeVar, MessagePack.ErrorMsg] = {
    def f_symbolTypeInfoTypeVar(reader: Reader): org.sireum.lang.symbol.TypeInfo.TypeVar = {
      val r = reader.read_symbolTypeInfoTypeVar()
      return r
    }
    val r = to(data, f_symbolTypeInfoTypeVar _)
    return r
  }

  def from_symbolTypeInfoMembers(o: org.sireum.lang.symbol.TypeInfo.Members, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_symbolTypeInfoMembers(o)
    return w.result
  }

  def to_symbolTypeInfoMembers(data: ISZ[U8]): Either[org.sireum.lang.symbol.TypeInfo.Members, MessagePack.ErrorMsg] = {
    def f_symbolTypeInfoMembers(reader: Reader): org.sireum.lang.symbol.TypeInfo.Members = {
      val r = reader.read_symbolTypeInfoMembers()
      return r
    }
    val r = to(data, f_symbolTypeInfoMembers _)
    return r
  }

  def from_astTopUnit(o: org.sireum.lang.ast.TopUnit, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astTopUnit(o)
    return w.result
  }

  def to_astTopUnit(data: ISZ[U8]): Either[org.sireum.lang.ast.TopUnit, MessagePack.ErrorMsg] = {
    def f_astTopUnit(reader: Reader): org.sireum.lang.ast.TopUnit = {
      val r = reader.read_astTopUnit()
      return r
    }
    val r = to(data, f_astTopUnit _)
    return r
  }

  def from_astTopUnitProgram(o: org.sireum.lang.ast.TopUnit.Program, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astTopUnitProgram(o)
    return w.result
  }

  def to_astTopUnitProgram(data: ISZ[U8]): Either[org.sireum.lang.ast.TopUnit.Program, MessagePack.ErrorMsg] = {
    def f_astTopUnitProgram(reader: Reader): org.sireum.lang.ast.TopUnit.Program = {
      val r = reader.read_astTopUnitProgram()
      return r
    }
    val r = to(data, f_astTopUnitProgram _)
    return r
  }

  def from_astTopUnitTruthTableUnit(o: org.sireum.lang.ast.TopUnit.TruthTableUnit, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astTopUnitTruthTableUnit(o)
    return w.result
  }

  def to_astTopUnitTruthTableUnit(data: ISZ[U8]): Either[org.sireum.lang.ast.TopUnit.TruthTableUnit, MessagePack.ErrorMsg] = {
    def f_astTopUnitTruthTableUnit(reader: Reader): org.sireum.lang.ast.TopUnit.TruthTableUnit = {
      val r = reader.read_astTopUnitTruthTableUnit()
      return r
    }
    val r = to(data, f_astTopUnitTruthTableUnit _)
    return r
  }

  def from_astStmt(o: org.sireum.lang.ast.Stmt, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmt(o)
    return w.result
  }

  def to_astStmt(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt, MessagePack.ErrorMsg] = {
    def f_astStmt(reader: Reader): org.sireum.lang.ast.Stmt = {
      val r = reader.read_astStmt()
      return r
    }
    val r = to(data, f_astStmt _)
    return r
  }

  def from_astHasModifies(o: org.sireum.lang.ast.HasModifies, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astHasModifies(o)
    return w.result
  }

  def to_astHasModifies(data: ISZ[U8]): Either[org.sireum.lang.ast.HasModifies, MessagePack.ErrorMsg] = {
    def f_astHasModifies(reader: Reader): org.sireum.lang.ast.HasModifies = {
      val r = reader.read_astHasModifies()
      return r
    }
    val r = to(data, f_astHasModifies _)
    return r
  }

  def from_astLoopContract(o: org.sireum.lang.ast.LoopContract, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astLoopContract(o)
    return w.result
  }

  def to_astLoopContract(data: ISZ[U8]): Either[org.sireum.lang.ast.LoopContract, MessagePack.ErrorMsg] = {
    def f_astLoopContract(reader: Reader): org.sireum.lang.ast.LoopContract = {
      val r = reader.read_astLoopContract()
      return r
    }
    val r = to(data, f_astLoopContract _)
    return r
  }

  def from_astStmtImport(o: org.sireum.lang.ast.Stmt.Import, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtImport(o)
    return w.result
  }

  def to_astStmtImport(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.Import, MessagePack.ErrorMsg] = {
    def f_astStmtImport(reader: Reader): org.sireum.lang.ast.Stmt.Import = {
      val r = reader.read_astStmtImport()
      return r
    }
    val r = to(data, f_astStmtImport _)
    return r
  }

  def from_astStmtImportImporter(o: org.sireum.lang.ast.Stmt.Import.Importer, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtImportImporter(o)
    return w.result
  }

  def to_astStmtImportImporter(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.Import.Importer, MessagePack.ErrorMsg] = {
    def f_astStmtImportImporter(reader: Reader): org.sireum.lang.ast.Stmt.Import.Importer = {
      val r = reader.read_astStmtImportImporter()
      return r
    }
    val r = to(data, f_astStmtImportImporter _)
    return r
  }

  def from_astStmtImportSelector(o: org.sireum.lang.ast.Stmt.Import.Selector, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtImportSelector(o)
    return w.result
  }

  def to_astStmtImportSelector(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.Import.Selector, MessagePack.ErrorMsg] = {
    def f_astStmtImportSelector(reader: Reader): org.sireum.lang.ast.Stmt.Import.Selector = {
      val r = reader.read_astStmtImportSelector()
      return r
    }
    val r = to(data, f_astStmtImportSelector _)
    return r
  }

  def from_astStmtImportMultiSelector(o: org.sireum.lang.ast.Stmt.Import.MultiSelector, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtImportMultiSelector(o)
    return w.result
  }

  def to_astStmtImportMultiSelector(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.Import.MultiSelector, MessagePack.ErrorMsg] = {
    def f_astStmtImportMultiSelector(reader: Reader): org.sireum.lang.ast.Stmt.Import.MultiSelector = {
      val r = reader.read_astStmtImportMultiSelector()
      return r
    }
    val r = to(data, f_astStmtImportMultiSelector _)
    return r
  }

  def from_astStmtImportWildcardSelector(o: org.sireum.lang.ast.Stmt.Import.WildcardSelector, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtImportWildcardSelector(o)
    return w.result
  }

  def to_astStmtImportWildcardSelector(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.Import.WildcardSelector, MessagePack.ErrorMsg] = {
    def f_astStmtImportWildcardSelector(reader: Reader): org.sireum.lang.ast.Stmt.Import.WildcardSelector = {
      val r = reader.read_astStmtImportWildcardSelector()
      return r
    }
    val r = to(data, f_astStmtImportWildcardSelector _)
    return r
  }

  def from_astStmtImportNamedSelector(o: org.sireum.lang.ast.Stmt.Import.NamedSelector, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtImportNamedSelector(o)
    return w.result
  }

  def to_astStmtImportNamedSelector(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.Import.NamedSelector, MessagePack.ErrorMsg] = {
    def f_astStmtImportNamedSelector(reader: Reader): org.sireum.lang.ast.Stmt.Import.NamedSelector = {
      val r = reader.read_astStmtImportNamedSelector()
      return r
    }
    val r = to(data, f_astStmtImportNamedSelector _)
    return r
  }

  def from_astStmtVar(o: org.sireum.lang.ast.Stmt.Var, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtVar(o)
    return w.result
  }

  def to_astStmtVar(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.Var, MessagePack.ErrorMsg] = {
    def f_astStmtVar(reader: Reader): org.sireum.lang.ast.Stmt.Var = {
      val r = reader.read_astStmtVar()
      return r
    }
    val r = to(data, f_astStmtVar _)
    return r
  }

  def from_astStmtVarPattern(o: org.sireum.lang.ast.Stmt.VarPattern, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtVarPattern(o)
    return w.result
  }

  def to_astStmtVarPattern(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.VarPattern, MessagePack.ErrorMsg] = {
    def f_astStmtVarPattern(reader: Reader): org.sireum.lang.ast.Stmt.VarPattern = {
      val r = reader.read_astStmtVarPattern()
      return r
    }
    val r = to(data, f_astStmtVarPattern _)
    return r
  }

  def from_astStmtSpecVar(o: org.sireum.lang.ast.Stmt.SpecVar, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtSpecVar(o)
    return w.result
  }

  def to_astStmtSpecVar(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.SpecVar, MessagePack.ErrorMsg] = {
    def f_astStmtSpecVar(reader: Reader): org.sireum.lang.ast.Stmt.SpecVar = {
      val r = reader.read_astStmtSpecVar()
      return r
    }
    val r = to(data, f_astStmtSpecVar _)
    return r
  }

  def from_astStmtRsVal(o: org.sireum.lang.ast.Stmt.RsVal, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtRsVal(o)
    return w.result
  }

  def to_astStmtRsVal(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.RsVal, MessagePack.ErrorMsg] = {
    def f_astStmtRsVal(reader: Reader): org.sireum.lang.ast.Stmt.RsVal = {
      val r = reader.read_astStmtRsVal()
      return r
    }
    val r = to(data, f_astStmtRsVal _)
    return r
  }

  def from_astStmtMethod(o: org.sireum.lang.ast.Stmt.Method, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtMethod(o)
    return w.result
  }

  def to_astStmtMethod(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.Method, MessagePack.ErrorMsg] = {
    def f_astStmtMethod(reader: Reader): org.sireum.lang.ast.Stmt.Method = {
      val r = reader.read_astStmtMethod()
      return r
    }
    val r = to(data, f_astStmtMethod _)
    return r
  }

  def from_astStmtExtMethod(o: org.sireum.lang.ast.Stmt.ExtMethod, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtExtMethod(o)
    return w.result
  }

  def to_astStmtExtMethod(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.ExtMethod, MessagePack.ErrorMsg] = {
    def f_astStmtExtMethod(reader: Reader): org.sireum.lang.ast.Stmt.ExtMethod = {
      val r = reader.read_astStmtExtMethod()
      return r
    }
    val r = to(data, f_astStmtExtMethod _)
    return r
  }

  def from_astStmtJustMethod(o: org.sireum.lang.ast.Stmt.JustMethod, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtJustMethod(o)
    return w.result
  }

  def to_astStmtJustMethod(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.JustMethod, MessagePack.ErrorMsg] = {
    def f_astStmtJustMethod(reader: Reader): org.sireum.lang.ast.Stmt.JustMethod = {
      val r = reader.read_astStmtJustMethod()
      return r
    }
    val r = to(data, f_astStmtJustMethod _)
    return r
  }

  def from_astStmtSpecMethod(o: org.sireum.lang.ast.Stmt.SpecMethod, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtSpecMethod(o)
    return w.result
  }

  def to_astStmtSpecMethod(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.SpecMethod, MessagePack.ErrorMsg] = {
    def f_astStmtSpecMethod(reader: Reader): org.sireum.lang.ast.Stmt.SpecMethod = {
      val r = reader.read_astStmtSpecMethod()
      return r
    }
    val r = to(data, f_astStmtSpecMethod _)
    return r
  }

  def from_astStmtEnum(o: org.sireum.lang.ast.Stmt.Enum, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtEnum(o)
    return w.result
  }

  def to_astStmtEnum(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.Enum, MessagePack.ErrorMsg] = {
    def f_astStmtEnum(reader: Reader): org.sireum.lang.ast.Stmt.Enum = {
      val r = reader.read_astStmtEnum()
      return r
    }
    val r = to(data, f_astStmtEnum _)
    return r
  }

  def from_astStmtSubZ(o: org.sireum.lang.ast.Stmt.SubZ, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtSubZ(o)
    return w.result
  }

  def to_astStmtSubZ(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.SubZ, MessagePack.ErrorMsg] = {
    def f_astStmtSubZ(reader: Reader): org.sireum.lang.ast.Stmt.SubZ = {
      val r = reader.read_astStmtSubZ()
      return r
    }
    val r = to(data, f_astStmtSubZ _)
    return r
  }

  def from_astStmtObject(o: org.sireum.lang.ast.Stmt.Object, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtObject(o)
    return w.result
  }

  def to_astStmtObject(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.Object, MessagePack.ErrorMsg] = {
    def f_astStmtObject(reader: Reader): org.sireum.lang.ast.Stmt.Object = {
      val r = reader.read_astStmtObject()
      return r
    }
    val r = to(data, f_astStmtObject _)
    return r
  }

  def from_astStmtSig(o: org.sireum.lang.ast.Stmt.Sig, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtSig(o)
    return w.result
  }

  def to_astStmtSig(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.Sig, MessagePack.ErrorMsg] = {
    def f_astStmtSig(reader: Reader): org.sireum.lang.ast.Stmt.Sig = {
      val r = reader.read_astStmtSig()
      return r
    }
    val r = to(data, f_astStmtSig _)
    return r
  }

  def from_astStmtAdt(o: org.sireum.lang.ast.Stmt.Adt, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtAdt(o)
    return w.result
  }

  def to_astStmtAdt(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.Adt, MessagePack.ErrorMsg] = {
    def f_astStmtAdt(reader: Reader): org.sireum.lang.ast.Stmt.Adt = {
      val r = reader.read_astStmtAdt()
      return r
    }
    val r = to(data, f_astStmtAdt _)
    return r
  }

  def from_astStmtTypeAlias(o: org.sireum.lang.ast.Stmt.TypeAlias, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtTypeAlias(o)
    return w.result
  }

  def to_astStmtTypeAlias(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.TypeAlias, MessagePack.ErrorMsg] = {
    def f_astStmtTypeAlias(reader: Reader): org.sireum.lang.ast.Stmt.TypeAlias = {
      val r = reader.read_astStmtTypeAlias()
      return r
    }
    val r = to(data, f_astStmtTypeAlias _)
    return r
  }

  def from_astStmtAssign(o: org.sireum.lang.ast.Stmt.Assign, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtAssign(o)
    return w.result
  }

  def to_astStmtAssign(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.Assign, MessagePack.ErrorMsg] = {
    def f_astStmtAssign(reader: Reader): org.sireum.lang.ast.Stmt.Assign = {
      val r = reader.read_astStmtAssign()
      return r
    }
    val r = to(data, f_astStmtAssign _)
    return r
  }

  def from_astStmtBlock(o: org.sireum.lang.ast.Stmt.Block, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtBlock(o)
    return w.result
  }

  def to_astStmtBlock(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.Block, MessagePack.ErrorMsg] = {
    def f_astStmtBlock(reader: Reader): org.sireum.lang.ast.Stmt.Block = {
      val r = reader.read_astStmtBlock()
      return r
    }
    val r = to(data, f_astStmtBlock _)
    return r
  }

  def from_astStmtIf(o: org.sireum.lang.ast.Stmt.If, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtIf(o)
    return w.result
  }

  def to_astStmtIf(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.If, MessagePack.ErrorMsg] = {
    def f_astStmtIf(reader: Reader): org.sireum.lang.ast.Stmt.If = {
      val r = reader.read_astStmtIf()
      return r
    }
    val r = to(data, f_astStmtIf _)
    return r
  }

  def from_astStmtInduct(o: org.sireum.lang.ast.Stmt.Induct, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtInduct(o)
    return w.result
  }

  def to_astStmtInduct(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.Induct, MessagePack.ErrorMsg] = {
    def f_astStmtInduct(reader: Reader): org.sireum.lang.ast.Stmt.Induct = {
      val r = reader.read_astStmtInduct()
      return r
    }
    val r = to(data, f_astStmtInduct _)
    return r
  }

  def from_astStmtMatch(o: org.sireum.lang.ast.Stmt.Match, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtMatch(o)
    return w.result
  }

  def to_astStmtMatch(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.Match, MessagePack.ErrorMsg] = {
    def f_astStmtMatch(reader: Reader): org.sireum.lang.ast.Stmt.Match = {
      val r = reader.read_astStmtMatch()
      return r
    }
    val r = to(data, f_astStmtMatch _)
    return r
  }

  def from_astStmtWhile(o: org.sireum.lang.ast.Stmt.While, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtWhile(o)
    return w.result
  }

  def to_astStmtWhile(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.While, MessagePack.ErrorMsg] = {
    def f_astStmtWhile(reader: Reader): org.sireum.lang.ast.Stmt.While = {
      val r = reader.read_astStmtWhile()
      return r
    }
    val r = to(data, f_astStmtWhile _)
    return r
  }

  def from_astStmtFor(o: org.sireum.lang.ast.Stmt.For, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtFor(o)
    return w.result
  }

  def to_astStmtFor(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.For, MessagePack.ErrorMsg] = {
    def f_astStmtFor(reader: Reader): org.sireum.lang.ast.Stmt.For = {
      val r = reader.read_astStmtFor()
      return r
    }
    val r = to(data, f_astStmtFor _)
    return r
  }

  def from_astStmtReturn(o: org.sireum.lang.ast.Stmt.Return, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtReturn(o)
    return w.result
  }

  def to_astStmtReturn(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.Return, MessagePack.ErrorMsg] = {
    def f_astStmtReturn(reader: Reader): org.sireum.lang.ast.Stmt.Return = {
      val r = reader.read_astStmtReturn()
      return r
    }
    val r = to(data, f_astStmtReturn _)
    return r
  }

  def from_astStmtExpr(o: org.sireum.lang.ast.Stmt.Expr, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtExpr(o)
    return w.result
  }

  def to_astStmtExpr(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.Expr, MessagePack.ErrorMsg] = {
    def f_astStmtExpr(reader: Reader): org.sireum.lang.ast.Stmt.Expr = {
      val r = reader.read_astStmtExpr()
      return r
    }
    val r = to(data, f_astStmtExpr _)
    return r
  }

  def from_astStmtSpec(o: org.sireum.lang.ast.Stmt.Spec, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtSpec(o)
    return w.result
  }

  def to_astStmtSpec(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.Spec, MessagePack.ErrorMsg] = {
    def f_astStmtSpec(reader: Reader): org.sireum.lang.ast.Stmt.Spec = {
      val r = reader.read_astStmtSpec()
      return r
    }
    val r = to(data, f_astStmtSpec _)
    return r
  }

  def from_astStmtFact(o: org.sireum.lang.ast.Stmt.Fact, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtFact(o)
    return w.result
  }

  def to_astStmtFact(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.Fact, MessagePack.ErrorMsg] = {
    def f_astStmtFact(reader: Reader): org.sireum.lang.ast.Stmt.Fact = {
      val r = reader.read_astStmtFact()
      return r
    }
    val r = to(data, f_astStmtFact _)
    return r
  }

  def from_astStmtInv(o: org.sireum.lang.ast.Stmt.Inv, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtInv(o)
    return w.result
  }

  def to_astStmtInv(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.Inv, MessagePack.ErrorMsg] = {
    def f_astStmtInv(reader: Reader): org.sireum.lang.ast.Stmt.Inv = {
      val r = reader.read_astStmtInv()
      return r
    }
    val r = to(data, f_astStmtInv _)
    return r
  }

  def from_astStmtTheorem(o: org.sireum.lang.ast.Stmt.Theorem, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtTheorem(o)
    return w.result
  }

  def to_astStmtTheorem(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.Theorem, MessagePack.ErrorMsg] = {
    def f_astStmtTheorem(reader: Reader): org.sireum.lang.ast.Stmt.Theorem = {
      val r = reader.read_astStmtTheorem()
      return r
    }
    val r = to(data, f_astStmtTheorem _)
    return r
  }

  def from_astStmtDataRefinement(o: org.sireum.lang.ast.Stmt.DataRefinement, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtDataRefinement(o)
    return w.result
  }

  def to_astStmtDataRefinement(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.DataRefinement, MessagePack.ErrorMsg] = {
    def f_astStmtDataRefinement(reader: Reader): org.sireum.lang.ast.Stmt.DataRefinement = {
      val r = reader.read_astStmtDataRefinement()
      return r
    }
    val r = to(data, f_astStmtDataRefinement _)
    return r
  }

  def from_astStmtSpecLabel(o: org.sireum.lang.ast.Stmt.SpecLabel, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtSpecLabel(o)
    return w.result
  }

  def to_astStmtSpecLabel(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.SpecLabel, MessagePack.ErrorMsg] = {
    def f_astStmtSpecLabel(reader: Reader): org.sireum.lang.ast.Stmt.SpecLabel = {
      val r = reader.read_astStmtSpecLabel()
      return r
    }
    val r = to(data, f_astStmtSpecLabel _)
    return r
  }

  def from_astStmtSpecBlock(o: org.sireum.lang.ast.Stmt.SpecBlock, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtSpecBlock(o)
    return w.result
  }

  def to_astStmtSpecBlock(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.SpecBlock, MessagePack.ErrorMsg] = {
    def f_astStmtSpecBlock(reader: Reader): org.sireum.lang.ast.Stmt.SpecBlock = {
      val r = reader.read_astStmtSpecBlock()
      return r
    }
    val r = to(data, f_astStmtSpecBlock _)
    return r
  }

  def from_astStmtDeduceSequent(o: org.sireum.lang.ast.Stmt.DeduceSequent, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtDeduceSequent(o)
    return w.result
  }

  def to_astStmtDeduceSequent(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.DeduceSequent, MessagePack.ErrorMsg] = {
    def f_astStmtDeduceSequent(reader: Reader): org.sireum.lang.ast.Stmt.DeduceSequent = {
      val r = reader.read_astStmtDeduceSequent()
      return r
    }
    val r = to(data, f_astStmtDeduceSequent _)
    return r
  }

  def from_astStmtDeduceSteps(o: org.sireum.lang.ast.Stmt.DeduceSteps, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtDeduceSteps(o)
    return w.result
  }

  def to_astStmtDeduceSteps(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.DeduceSteps, MessagePack.ErrorMsg] = {
    def f_astStmtDeduceSteps(reader: Reader): org.sireum.lang.ast.Stmt.DeduceSteps = {
      val r = reader.read_astStmtDeduceSteps()
      return r
    }
    val r = to(data, f_astStmtDeduceSteps _)
    return r
  }

  def from_astStmtHavoc(o: org.sireum.lang.ast.Stmt.Havoc, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astStmtHavoc(o)
    return w.result
  }

  def to_astStmtHavoc(data: ISZ[U8]): Either[org.sireum.lang.ast.Stmt.Havoc, MessagePack.ErrorMsg] = {
    def f_astStmtHavoc(reader: Reader): org.sireum.lang.ast.Stmt.Havoc = {
      val r = reader.read_astStmtHavoc()
      return r
    }
    val r = to(data, f_astStmtHavoc _)
    return r
  }

  def from_astMethodContract(o: org.sireum.lang.ast.MethodContract, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astMethodContract(o)
    return w.result
  }

  def to_astMethodContract(data: ISZ[U8]): Either[org.sireum.lang.ast.MethodContract, MessagePack.ErrorMsg] = {
    def f_astMethodContract(reader: Reader): org.sireum.lang.ast.MethodContract = {
      val r = reader.read_astMethodContract()
      return r
    }
    val r = to(data, f_astMethodContract _)
    return r
  }

  def from_astMethodContractAccesses(o: org.sireum.lang.ast.MethodContract.Accesses, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astMethodContractAccesses(o)
    return w.result
  }

  def to_astMethodContractAccesses(data: ISZ[U8]): Either[org.sireum.lang.ast.MethodContract.Accesses, MessagePack.ErrorMsg] = {
    def f_astMethodContractAccesses(reader: Reader): org.sireum.lang.ast.MethodContract.Accesses = {
      val r = reader.read_astMethodContractAccesses()
      return r
    }
    val r = to(data, f_astMethodContractAccesses _)
    return r
  }

  def from_astMethodContractClaims(o: org.sireum.lang.ast.MethodContract.Claims, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astMethodContractClaims(o)
    return w.result
  }

  def to_astMethodContractClaims(data: ISZ[U8]): Either[org.sireum.lang.ast.MethodContract.Claims, MessagePack.ErrorMsg] = {
    def f_astMethodContractClaims(reader: Reader): org.sireum.lang.ast.MethodContract.Claims = {
      val r = reader.read_astMethodContractClaims()
      return r
    }
    val r = to(data, f_astMethodContractClaims _)
    return r
  }

  def from_astMethodContractSimple(o: org.sireum.lang.ast.MethodContract.Simple, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astMethodContractSimple(o)
    return w.result
  }

  def to_astMethodContractSimple(data: ISZ[U8]): Either[org.sireum.lang.ast.MethodContract.Simple, MessagePack.ErrorMsg] = {
    def f_astMethodContractSimple(reader: Reader): org.sireum.lang.ast.MethodContract.Simple = {
      val r = reader.read_astMethodContractSimple()
      return r
    }
    val r = to(data, f_astMethodContractSimple _)
    return r
  }

  def from_astMethodContractCases(o: org.sireum.lang.ast.MethodContract.Cases, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astMethodContractCases(o)
    return w.result
  }

  def to_astMethodContractCases(data: ISZ[U8]): Either[org.sireum.lang.ast.MethodContract.Cases, MessagePack.ErrorMsg] = {
    def f_astMethodContractCases(reader: Reader): org.sireum.lang.ast.MethodContract.Cases = {
      val r = reader.read_astMethodContractCases()
      return r
    }
    val r = to(data, f_astMethodContractCases _)
    return r
  }

  def from_astMethodContractCase(o: org.sireum.lang.ast.MethodContract.Case, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astMethodContractCase(o)
    return w.result
  }

  def to_astMethodContractCase(data: ISZ[U8]): Either[org.sireum.lang.ast.MethodContract.Case, MessagePack.ErrorMsg] = {
    def f_astMethodContractCase(reader: Reader): org.sireum.lang.ast.MethodContract.Case = {
      val r = reader.read_astMethodContractCase()
      return r
    }
    val r = to(data, f_astMethodContractCase _)
    return r
  }

  def from_astMethodContractInfoFlowElement(o: org.sireum.lang.ast.MethodContract.InfoFlowElement, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astMethodContractInfoFlowElement(o)
    return w.result
  }

  def to_astMethodContractInfoFlowElement(data: ISZ[U8]): Either[org.sireum.lang.ast.MethodContract.InfoFlowElement, MessagePack.ErrorMsg] = {
    def f_astMethodContractInfoFlowElement(reader: Reader): org.sireum.lang.ast.MethodContract.InfoFlowElement = {
      val r = reader.read_astMethodContractInfoFlowElement()
      return r
    }
    val r = to(data, f_astMethodContractInfoFlowElement _)
    return r
  }

  def from_astMethodContractInfoFlows(o: org.sireum.lang.ast.MethodContract.InfoFlows, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astMethodContractInfoFlows(o)
    return w.result
  }

  def to_astMethodContractInfoFlows(data: ISZ[U8]): Either[org.sireum.lang.ast.MethodContract.InfoFlows, MessagePack.ErrorMsg] = {
    def f_astMethodContractInfoFlows(reader: Reader): org.sireum.lang.ast.MethodContract.InfoFlows = {
      val r = reader.read_astMethodContractInfoFlows()
      return r
    }
    val r = to(data, f_astMethodContractInfoFlows _)
    return r
  }

  def from_astMethodContractInfoFlowGroup(o: org.sireum.lang.ast.MethodContract.InfoFlowGroup, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astMethodContractInfoFlowGroup(o)
    return w.result
  }

  def to_astMethodContractInfoFlowGroup(data: ISZ[U8]): Either[org.sireum.lang.ast.MethodContract.InfoFlowGroup, MessagePack.ErrorMsg] = {
    def f_astMethodContractInfoFlowGroup(reader: Reader): org.sireum.lang.ast.MethodContract.InfoFlowGroup = {
      val r = reader.read_astMethodContractInfoFlowGroup()
      return r
    }
    val r = to(data, f_astMethodContractInfoFlowGroup _)
    return r
  }

  def from_astMethodContractInfoFlowFlow(o: org.sireum.lang.ast.MethodContract.InfoFlowFlow, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astMethodContractInfoFlowFlow(o)
    return w.result
  }

  def to_astMethodContractInfoFlowFlow(data: ISZ[U8]): Either[org.sireum.lang.ast.MethodContract.InfoFlowFlow, MessagePack.ErrorMsg] = {
    def f_astMethodContractInfoFlowFlow(reader: Reader): org.sireum.lang.ast.MethodContract.InfoFlowFlow = {
      val r = reader.read_astMethodContractInfoFlowFlow()
      return r
    }
    val r = to(data, f_astMethodContractInfoFlowFlow _)
    return r
  }

  def from_astMethodContractInfoFlowCase(o: org.sireum.lang.ast.MethodContract.InfoFlowCase, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astMethodContractInfoFlowCase(o)
    return w.result
  }

  def to_astMethodContractInfoFlowCase(data: ISZ[U8]): Either[org.sireum.lang.ast.MethodContract.InfoFlowCase, MessagePack.ErrorMsg] = {
    def f_astMethodContractInfoFlowCase(reader: Reader): org.sireum.lang.ast.MethodContract.InfoFlowCase = {
      val r = reader.read_astMethodContractInfoFlowCase()
      return r
    }
    val r = to(data, f_astMethodContractInfoFlowCase _)
    return r
  }

  def from_astSequent(o: org.sireum.lang.ast.Sequent, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astSequent(o)
    return w.result
  }

  def to_astSequent(data: ISZ[U8]): Either[org.sireum.lang.ast.Sequent, MessagePack.ErrorMsg] = {
    def f_astSequent(reader: Reader): org.sireum.lang.ast.Sequent = {
      val r = reader.read_astSequent()
      return r
    }
    val r = to(data, f_astSequent _)
    return r
  }

  def from_astProofAst(o: org.sireum.lang.ast.ProofAst, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astProofAst(o)
    return w.result
  }

  def to_astProofAst(data: ISZ[U8]): Either[org.sireum.lang.ast.ProofAst, MessagePack.ErrorMsg] = {
    def f_astProofAst(reader: Reader): org.sireum.lang.ast.ProofAst = {
      val r = reader.read_astProofAst()
      return r
    }
    val r = to(data, f_astProofAst _)
    return r
  }

  def from_astProofAstStep(o: org.sireum.lang.ast.ProofAst.Step, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astProofAstStep(o)
    return w.result
  }

  def to_astProofAstStep(data: ISZ[U8]): Either[org.sireum.lang.ast.ProofAst.Step, MessagePack.ErrorMsg] = {
    def f_astProofAstStep(reader: Reader): org.sireum.lang.ast.ProofAst.Step = {
      val r = reader.read_astProofAstStep()
      return r
    }
    val r = to(data, f_astProofAstStep _)
    return r
  }

  def from_astProofAstStepId(o: org.sireum.lang.ast.ProofAst.StepId, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astProofAstStepId(o)
    return w.result
  }

  def to_astProofAstStepId(data: ISZ[U8]): Either[org.sireum.lang.ast.ProofAst.StepId, MessagePack.ErrorMsg] = {
    def f_astProofAstStepId(reader: Reader): org.sireum.lang.ast.ProofAst.StepId = {
      val r = reader.read_astProofAstStepId()
      return r
    }
    val r = to(data, f_astProofAstStepId _)
    return r
  }

  def from_astProofAstStepIdNum(o: org.sireum.lang.ast.ProofAst.StepId.Num, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astProofAstStepIdNum(o)
    return w.result
  }

  def to_astProofAstStepIdNum(data: ISZ[U8]): Either[org.sireum.lang.ast.ProofAst.StepId.Num, MessagePack.ErrorMsg] = {
    def f_astProofAstStepIdNum(reader: Reader): org.sireum.lang.ast.ProofAst.StepId.Num = {
      val r = reader.read_astProofAstStepIdNum()
      return r
    }
    val r = to(data, f_astProofAstStepIdNum _)
    return r
  }

  def from_astProofAstStepIdStr(o: org.sireum.lang.ast.ProofAst.StepId.Str, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astProofAstStepIdStr(o)
    return w.result
  }

  def to_astProofAstStepIdStr(data: ISZ[U8]): Either[org.sireum.lang.ast.ProofAst.StepId.Str, MessagePack.ErrorMsg] = {
    def f_astProofAstStepIdStr(reader: Reader): org.sireum.lang.ast.ProofAst.StepId.Str = {
      val r = reader.read_astProofAstStepIdStr()
      return r
    }
    val r = to(data, f_astProofAstStepIdStr _)
    return r
  }

  def from_astProofAstStepRegular(o: org.sireum.lang.ast.ProofAst.Step.Regular, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astProofAstStepRegular(o)
    return w.result
  }

  def to_astProofAstStepRegular(data: ISZ[U8]): Either[org.sireum.lang.ast.ProofAst.Step.Regular, MessagePack.ErrorMsg] = {
    def f_astProofAstStepRegular(reader: Reader): org.sireum.lang.ast.ProofAst.Step.Regular = {
      val r = reader.read_astProofAstStepRegular()
      return r
    }
    val r = to(data, f_astProofAstStepRegular _)
    return r
  }

  def from_astProofAstStepAssume(o: org.sireum.lang.ast.ProofAst.Step.Assume, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astProofAstStepAssume(o)
    return w.result
  }

  def to_astProofAstStepAssume(data: ISZ[U8]): Either[org.sireum.lang.ast.ProofAst.Step.Assume, MessagePack.ErrorMsg] = {
    def f_astProofAstStepAssume(reader: Reader): org.sireum.lang.ast.ProofAst.Step.Assume = {
      val r = reader.read_astProofAstStepAssume()
      return r
    }
    val r = to(data, f_astProofAstStepAssume _)
    return r
  }

  def from_astProofAstStepAssert(o: org.sireum.lang.ast.ProofAst.Step.Assert, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astProofAstStepAssert(o)
    return w.result
  }

  def to_astProofAstStepAssert(data: ISZ[U8]): Either[org.sireum.lang.ast.ProofAst.Step.Assert, MessagePack.ErrorMsg] = {
    def f_astProofAstStepAssert(reader: Reader): org.sireum.lang.ast.ProofAst.Step.Assert = {
      val r = reader.read_astProofAstStepAssert()
      return r
    }
    val r = to(data, f_astProofAstStepAssert _)
    return r
  }

  def from_astProofAstStepSubProof(o: org.sireum.lang.ast.ProofAst.Step.SubProof, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astProofAstStepSubProof(o)
    return w.result
  }

  def to_astProofAstStepSubProof(data: ISZ[U8]): Either[org.sireum.lang.ast.ProofAst.Step.SubProof, MessagePack.ErrorMsg] = {
    def f_astProofAstStepSubProof(reader: Reader): org.sireum.lang.ast.ProofAst.Step.SubProof = {
      val r = reader.read_astProofAstStepSubProof()
      return r
    }
    val r = to(data, f_astProofAstStepSubProof _)
    return r
  }

  def from_astProofAstStepLet(o: org.sireum.lang.ast.ProofAst.Step.Let, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astProofAstStepLet(o)
    return w.result
  }

  def to_astProofAstStepLet(data: ISZ[U8]): Either[org.sireum.lang.ast.ProofAst.Step.Let, MessagePack.ErrorMsg] = {
    def f_astProofAstStepLet(reader: Reader): org.sireum.lang.ast.ProofAst.Step.Let = {
      val r = reader.read_astProofAstStepLet()
      return r
    }
    val r = to(data, f_astProofAstStepLet _)
    return r
  }

  def from_astProofAstStepLetParam(o: org.sireum.lang.ast.ProofAst.Step.Let.Param, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astProofAstStepLetParam(o)
    return w.result
  }

  def to_astProofAstStepLetParam(data: ISZ[U8]): Either[org.sireum.lang.ast.ProofAst.Step.Let.Param, MessagePack.ErrorMsg] = {
    def f_astProofAstStepLetParam(reader: Reader): org.sireum.lang.ast.ProofAst.Step.Let.Param = {
      val r = reader.read_astProofAstStepLetParam()
      return r
    }
    val r = to(data, f_astProofAstStepLetParam _)
    return r
  }

  def from_astProofAstStepJustification(o: org.sireum.lang.ast.ProofAst.Step.Justification, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astProofAstStepJustification(o)
    return w.result
  }

  def to_astProofAstStepJustification(data: ISZ[U8]): Either[org.sireum.lang.ast.ProofAst.Step.Justification, MessagePack.ErrorMsg] = {
    def f_astProofAstStepJustification(reader: Reader): org.sireum.lang.ast.ProofAst.Step.Justification = {
      val r = reader.read_astProofAstStepJustification()
      return r
    }
    val r = to(data, f_astProofAstStepJustification _)
    return r
  }

  def from_astProofAstStepJustificationRef(o: org.sireum.lang.ast.ProofAst.Step.Justification.Ref, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astProofAstStepJustificationRef(o)
    return w.result
  }

  def to_astProofAstStepJustificationRef(data: ISZ[U8]): Either[org.sireum.lang.ast.ProofAst.Step.Justification.Ref, MessagePack.ErrorMsg] = {
    def f_astProofAstStepJustificationRef(reader: Reader): org.sireum.lang.ast.ProofAst.Step.Justification.Ref = {
      val r = reader.read_astProofAstStepJustificationRef()
      return r
    }
    val r = to(data, f_astProofAstStepJustificationRef _)
    return r
  }

  def from_astProofAstStepJustificationApply(o: org.sireum.lang.ast.ProofAst.Step.Justification.Apply, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astProofAstStepJustificationApply(o)
    return w.result
  }

  def to_astProofAstStepJustificationApply(data: ISZ[U8]): Either[org.sireum.lang.ast.ProofAst.Step.Justification.Apply, MessagePack.ErrorMsg] = {
    def f_astProofAstStepJustificationApply(reader: Reader): org.sireum.lang.ast.ProofAst.Step.Justification.Apply = {
      val r = reader.read_astProofAstStepJustificationApply()
      return r
    }
    val r = to(data, f_astProofAstStepJustificationApply _)
    return r
  }

  def from_astProofAstStepJustificationApplyNamed(o: org.sireum.lang.ast.ProofAst.Step.Justification.ApplyNamed, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astProofAstStepJustificationApplyNamed(o)
    return w.result
  }

  def to_astProofAstStepJustificationApplyNamed(data: ISZ[U8]): Either[org.sireum.lang.ast.ProofAst.Step.Justification.ApplyNamed, MessagePack.ErrorMsg] = {
    def f_astProofAstStepJustificationApplyNamed(reader: Reader): org.sireum.lang.ast.ProofAst.Step.Justification.ApplyNamed = {
      val r = reader.read_astProofAstStepJustificationApplyNamed()
      return r
    }
    val r = to(data, f_astProofAstStepJustificationApplyNamed _)
    return r
  }

  def from_astProofAstStepJustificationApplyEta(o: org.sireum.lang.ast.ProofAst.Step.Justification.ApplyEta, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astProofAstStepJustificationApplyEta(o)
    return w.result
  }

  def to_astProofAstStepJustificationApplyEta(data: ISZ[U8]): Either[org.sireum.lang.ast.ProofAst.Step.Justification.ApplyEta, MessagePack.ErrorMsg] = {
    def f_astProofAstStepJustificationApplyEta(reader: Reader): org.sireum.lang.ast.ProofAst.Step.Justification.ApplyEta = {
      val r = reader.read_astProofAstStepJustificationApplyEta()
      return r
    }
    val r = to(data, f_astProofAstStepJustificationApplyEta _)
    return r
  }

  def from_astAssignExp(o: org.sireum.lang.ast.AssignExp, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astAssignExp(o)
    return w.result
  }

  def to_astAssignExp(data: ISZ[U8]): Either[org.sireum.lang.ast.AssignExp, MessagePack.ErrorMsg] = {
    def f_astAssignExp(reader: Reader): org.sireum.lang.ast.AssignExp = {
      val r = reader.read_astAssignExp()
      return r
    }
    val r = to(data, f_astAssignExp _)
    return r
  }

  def from_astCase(o: org.sireum.lang.ast.Case, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astCase(o)
    return w.result
  }

  def to_astCase(data: ISZ[U8]): Either[org.sireum.lang.ast.Case, MessagePack.ErrorMsg] = {
    def f_astCase(reader: Reader): org.sireum.lang.ast.Case = {
      val r = reader.read_astCase()
      return r
    }
    val r = to(data, f_astCase _)
    return r
  }

  def from_astEnumGenRange(o: org.sireum.lang.ast.EnumGen.Range, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astEnumGenRange(o)
    return w.result
  }

  def to_astEnumGenRange(data: ISZ[U8]): Either[org.sireum.lang.ast.EnumGen.Range, MessagePack.ErrorMsg] = {
    def f_astEnumGenRange(reader: Reader): org.sireum.lang.ast.EnumGen.Range = {
      val r = reader.read_astEnumGenRange()
      return r
    }
    val r = to(data, f_astEnumGenRange _)
    return r
  }

  def from_astEnumGenRangeExpr(o: org.sireum.lang.ast.EnumGen.Range.Expr, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astEnumGenRangeExpr(o)
    return w.result
  }

  def to_astEnumGenRangeExpr(data: ISZ[U8]): Either[org.sireum.lang.ast.EnumGen.Range.Expr, MessagePack.ErrorMsg] = {
    def f_astEnumGenRangeExpr(reader: Reader): org.sireum.lang.ast.EnumGen.Range.Expr = {
      val r = reader.read_astEnumGenRangeExpr()
      return r
    }
    val r = to(data, f_astEnumGenRangeExpr _)
    return r
  }

  def from_astEnumGenRangeStep(o: org.sireum.lang.ast.EnumGen.Range.Step, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astEnumGenRangeStep(o)
    return w.result
  }

  def to_astEnumGenRangeStep(data: ISZ[U8]): Either[org.sireum.lang.ast.EnumGen.Range.Step, MessagePack.ErrorMsg] = {
    def f_astEnumGenRangeStep(reader: Reader): org.sireum.lang.ast.EnumGen.Range.Step = {
      val r = reader.read_astEnumGenRangeStep()
      return r
    }
    val r = to(data, f_astEnumGenRangeStep _)
    return r
  }

  def from_astEnumGenFor(o: org.sireum.lang.ast.EnumGen.For, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astEnumGenFor(o)
    return w.result
  }

  def to_astEnumGenFor(data: ISZ[U8]): Either[org.sireum.lang.ast.EnumGen.For, MessagePack.ErrorMsg] = {
    def f_astEnumGenFor(reader: Reader): org.sireum.lang.ast.EnumGen.For = {
      val r = reader.read_astEnumGenFor()
      return r
    }
    val r = to(data, f_astEnumGenFor _)
    return r
  }

  def from_astType(o: org.sireum.lang.ast.Type, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astType(o)
    return w.result
  }

  def to_astType(data: ISZ[U8]): Either[org.sireum.lang.ast.Type, MessagePack.ErrorMsg] = {
    def f_astType(reader: Reader): org.sireum.lang.ast.Type = {
      val r = reader.read_astType()
      return r
    }
    val r = to(data, f_astType _)
    return r
  }

  def from_astTypeNamed(o: org.sireum.lang.ast.Type.Named, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astTypeNamed(o)
    return w.result
  }

  def to_astTypeNamed(data: ISZ[U8]): Either[org.sireum.lang.ast.Type.Named, MessagePack.ErrorMsg] = {
    def f_astTypeNamed(reader: Reader): org.sireum.lang.ast.Type.Named = {
      val r = reader.read_astTypeNamed()
      return r
    }
    val r = to(data, f_astTypeNamed _)
    return r
  }

  def from_astTypeFun(o: org.sireum.lang.ast.Type.Fun, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astTypeFun(o)
    return w.result
  }

  def to_astTypeFun(data: ISZ[U8]): Either[org.sireum.lang.ast.Type.Fun, MessagePack.ErrorMsg] = {
    def f_astTypeFun(reader: Reader): org.sireum.lang.ast.Type.Fun = {
      val r = reader.read_astTypeFun()
      return r
    }
    val r = to(data, f_astTypeFun _)
    return r
  }

  def from_astTypeTuple(o: org.sireum.lang.ast.Type.Tuple, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astTypeTuple(o)
    return w.result
  }

  def to_astTypeTuple(data: ISZ[U8]): Either[org.sireum.lang.ast.Type.Tuple, MessagePack.ErrorMsg] = {
    def f_astTypeTuple(reader: Reader): org.sireum.lang.ast.Type.Tuple = {
      val r = reader.read_astTypeTuple()
      return r
    }
    val r = to(data, f_astTypeTuple _)
    return r
  }

  def from_astPattern(o: org.sireum.lang.ast.Pattern, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astPattern(o)
    return w.result
  }

  def to_astPattern(data: ISZ[U8]): Either[org.sireum.lang.ast.Pattern, MessagePack.ErrorMsg] = {
    def f_astPattern(reader: Reader): org.sireum.lang.ast.Pattern = {
      val r = reader.read_astPattern()
      return r
    }
    val r = to(data, f_astPattern _)
    return r
  }

  def from_astPatternLiteral(o: org.sireum.lang.ast.Pattern.Literal, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astPatternLiteral(o)
    return w.result
  }

  def to_astPatternLiteral(data: ISZ[U8]): Either[org.sireum.lang.ast.Pattern.Literal, MessagePack.ErrorMsg] = {
    def f_astPatternLiteral(reader: Reader): org.sireum.lang.ast.Pattern.Literal = {
      val r = reader.read_astPatternLiteral()
      return r
    }
    val r = to(data, f_astPatternLiteral _)
    return r
  }

  def from_astPatternLitInterpolate(o: org.sireum.lang.ast.Pattern.LitInterpolate, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astPatternLitInterpolate(o)
    return w.result
  }

  def to_astPatternLitInterpolate(data: ISZ[U8]): Either[org.sireum.lang.ast.Pattern.LitInterpolate, MessagePack.ErrorMsg] = {
    def f_astPatternLitInterpolate(reader: Reader): org.sireum.lang.ast.Pattern.LitInterpolate = {
      val r = reader.read_astPatternLitInterpolate()
      return r
    }
    val r = to(data, f_astPatternLitInterpolate _)
    return r
  }

  def from_astPatternRef(o: org.sireum.lang.ast.Pattern.Ref, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astPatternRef(o)
    return w.result
  }

  def to_astPatternRef(data: ISZ[U8]): Either[org.sireum.lang.ast.Pattern.Ref, MessagePack.ErrorMsg] = {
    def f_astPatternRef(reader: Reader): org.sireum.lang.ast.Pattern.Ref = {
      val r = reader.read_astPatternRef()
      return r
    }
    val r = to(data, f_astPatternRef _)
    return r
  }

  def from_astPatternVarBinding(o: org.sireum.lang.ast.Pattern.VarBinding, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astPatternVarBinding(o)
    return w.result
  }

  def to_astPatternVarBinding(data: ISZ[U8]): Either[org.sireum.lang.ast.Pattern.VarBinding, MessagePack.ErrorMsg] = {
    def f_astPatternVarBinding(reader: Reader): org.sireum.lang.ast.Pattern.VarBinding = {
      val r = reader.read_astPatternVarBinding()
      return r
    }
    val r = to(data, f_astPatternVarBinding _)
    return r
  }

  def from_astPatternWildcard(o: org.sireum.lang.ast.Pattern.Wildcard, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astPatternWildcard(o)
    return w.result
  }

  def to_astPatternWildcard(data: ISZ[U8]): Either[org.sireum.lang.ast.Pattern.Wildcard, MessagePack.ErrorMsg] = {
    def f_astPatternWildcard(reader: Reader): org.sireum.lang.ast.Pattern.Wildcard = {
      val r = reader.read_astPatternWildcard()
      return r
    }
    val r = to(data, f_astPatternWildcard _)
    return r
  }

  def from_astPatternSeqWildcard(o: org.sireum.lang.ast.Pattern.SeqWildcard, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astPatternSeqWildcard(o)
    return w.result
  }

  def to_astPatternSeqWildcard(data: ISZ[U8]): Either[org.sireum.lang.ast.Pattern.SeqWildcard, MessagePack.ErrorMsg] = {
    def f_astPatternSeqWildcard(reader: Reader): org.sireum.lang.ast.Pattern.SeqWildcard = {
      val r = reader.read_astPatternSeqWildcard()
      return r
    }
    val r = to(data, f_astPatternSeqWildcard _)
    return r
  }

  def from_astPatternStructure(o: org.sireum.lang.ast.Pattern.Structure, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astPatternStructure(o)
    return w.result
  }

  def to_astPatternStructure(data: ISZ[U8]): Either[org.sireum.lang.ast.Pattern.Structure, MessagePack.ErrorMsg] = {
    def f_astPatternStructure(reader: Reader): org.sireum.lang.ast.Pattern.Structure = {
      val r = reader.read_astPatternStructure()
      return r
    }
    val r = to(data, f_astPatternStructure _)
    return r
  }

  def from_astExp(o: org.sireum.lang.ast.Exp, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExp(o)
    return w.result
  }

  def to_astExp(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp, MessagePack.ErrorMsg] = {
    def f_astExp(reader: Reader): org.sireum.lang.ast.Exp = {
      val r = reader.read_astExp()
      return r
    }
    val r = to(data, f_astExp _)
    return r
  }

  def from_astLit(o: org.sireum.lang.ast.Lit, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astLit(o)
    return w.result
  }

  def to_astLit(data: ISZ[U8]): Either[org.sireum.lang.ast.Lit, MessagePack.ErrorMsg] = {
    def f_astLit(reader: Reader): org.sireum.lang.ast.Lit = {
      val r = reader.read_astLit()
      return r
    }
    val r = to(data, f_astLit _)
    return r
  }

  def from_astExpLitB(o: org.sireum.lang.ast.Exp.LitB, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpLitB(o)
    return w.result
  }

  def to_astExpLitB(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.LitB, MessagePack.ErrorMsg] = {
    def f_astExpLitB(reader: Reader): org.sireum.lang.ast.Exp.LitB = {
      val r = reader.read_astExpLitB()
      return r
    }
    val r = to(data, f_astExpLitB _)
    return r
  }

  def from_astExpLitC(o: org.sireum.lang.ast.Exp.LitC, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpLitC(o)
    return w.result
  }

  def to_astExpLitC(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.LitC, MessagePack.ErrorMsg] = {
    def f_astExpLitC(reader: Reader): org.sireum.lang.ast.Exp.LitC = {
      val r = reader.read_astExpLitC()
      return r
    }
    val r = to(data, f_astExpLitC _)
    return r
  }

  def from_astExpLitZ(o: org.sireum.lang.ast.Exp.LitZ, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpLitZ(o)
    return w.result
  }

  def to_astExpLitZ(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.LitZ, MessagePack.ErrorMsg] = {
    def f_astExpLitZ(reader: Reader): org.sireum.lang.ast.Exp.LitZ = {
      val r = reader.read_astExpLitZ()
      return r
    }
    val r = to(data, f_astExpLitZ _)
    return r
  }

  def from_astExpLitF32(o: org.sireum.lang.ast.Exp.LitF32, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpLitF32(o)
    return w.result
  }

  def to_astExpLitF32(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.LitF32, MessagePack.ErrorMsg] = {
    def f_astExpLitF32(reader: Reader): org.sireum.lang.ast.Exp.LitF32 = {
      val r = reader.read_astExpLitF32()
      return r
    }
    val r = to(data, f_astExpLitF32 _)
    return r
  }

  def from_astExpLitF64(o: org.sireum.lang.ast.Exp.LitF64, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpLitF64(o)
    return w.result
  }

  def to_astExpLitF64(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.LitF64, MessagePack.ErrorMsg] = {
    def f_astExpLitF64(reader: Reader): org.sireum.lang.ast.Exp.LitF64 = {
      val r = reader.read_astExpLitF64()
      return r
    }
    val r = to(data, f_astExpLitF64 _)
    return r
  }

  def from_astExpLitR(o: org.sireum.lang.ast.Exp.LitR, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpLitR(o)
    return w.result
  }

  def to_astExpLitR(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.LitR, MessagePack.ErrorMsg] = {
    def f_astExpLitR(reader: Reader): org.sireum.lang.ast.Exp.LitR = {
      val r = reader.read_astExpLitR()
      return r
    }
    val r = to(data, f_astExpLitR _)
    return r
  }

  def from_astExpLitString(o: org.sireum.lang.ast.Exp.LitString, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpLitString(o)
    return w.result
  }

  def to_astExpLitString(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.LitString, MessagePack.ErrorMsg] = {
    def f_astExpLitString(reader: Reader): org.sireum.lang.ast.Exp.LitString = {
      val r = reader.read_astExpLitString()
      return r
    }
    val r = to(data, f_astExpLitString _)
    return r
  }

  def from_astExpStringInterpolate(o: org.sireum.lang.ast.Exp.StringInterpolate, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpStringInterpolate(o)
    return w.result
  }

  def to_astExpStringInterpolate(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.StringInterpolate, MessagePack.ErrorMsg] = {
    def f_astExpStringInterpolate(reader: Reader): org.sireum.lang.ast.Exp.StringInterpolate = {
      val r = reader.read_astExpStringInterpolate()
      return r
    }
    val r = to(data, f_astExpStringInterpolate _)
    return r
  }

  def from_astExpThis(o: org.sireum.lang.ast.Exp.This, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpThis(o)
    return w.result
  }

  def to_astExpThis(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.This, MessagePack.ErrorMsg] = {
    def f_astExpThis(reader: Reader): org.sireum.lang.ast.Exp.This = {
      val r = reader.read_astExpThis()
      return r
    }
    val r = to(data, f_astExpThis _)
    return r
  }

  def from_astExpSuper(o: org.sireum.lang.ast.Exp.Super, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpSuper(o)
    return w.result
  }

  def to_astExpSuper(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.Super, MessagePack.ErrorMsg] = {
    def f_astExpSuper(reader: Reader): org.sireum.lang.ast.Exp.Super = {
      val r = reader.read_astExpSuper()
      return r
    }
    val r = to(data, f_astExpSuper _)
    return r
  }

  def from_astExpUnary(o: org.sireum.lang.ast.Exp.Unary, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpUnary(o)
    return w.result
  }

  def to_astExpUnary(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.Unary, MessagePack.ErrorMsg] = {
    def f_astExpUnary(reader: Reader): org.sireum.lang.ast.Exp.Unary = {
      val r = reader.read_astExpUnary()
      return r
    }
    val r = to(data, f_astExpUnary _)
    return r
  }

  def from_astExpRef(o: org.sireum.lang.ast.Exp.Ref, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpRef(o)
    return w.result
  }

  def to_astExpRef(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.Ref, MessagePack.ErrorMsg] = {
    def f_astExpRef(reader: Reader): org.sireum.lang.ast.Exp.Ref = {
      val r = reader.read_astExpRef()
      return r
    }
    val r = to(data, f_astExpRef _)
    return r
  }

  def from_astExpBinary(o: org.sireum.lang.ast.Exp.Binary, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpBinary(o)
    return w.result
  }

  def to_astExpBinary(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.Binary, MessagePack.ErrorMsg] = {
    def f_astExpBinary(reader: Reader): org.sireum.lang.ast.Exp.Binary = {
      val r = reader.read_astExpBinary()
      return r
    }
    val r = to(data, f_astExpBinary _)
    return r
  }

  def from_astExpIdent(o: org.sireum.lang.ast.Exp.Ident, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpIdent(o)
    return w.result
  }

  def to_astExpIdent(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.Ident, MessagePack.ErrorMsg] = {
    def f_astExpIdent(reader: Reader): org.sireum.lang.ast.Exp.Ident = {
      val r = reader.read_astExpIdent()
      return r
    }
    val r = to(data, f_astExpIdent _)
    return r
  }

  def from_astExpEta(o: org.sireum.lang.ast.Exp.Eta, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpEta(o)
    return w.result
  }

  def to_astExpEta(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.Eta, MessagePack.ErrorMsg] = {
    def f_astExpEta(reader: Reader): org.sireum.lang.ast.Exp.Eta = {
      val r = reader.read_astExpEta()
      return r
    }
    val r = to(data, f_astExpEta _)
    return r
  }

  def from_astExpTuple(o: org.sireum.lang.ast.Exp.Tuple, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpTuple(o)
    return w.result
  }

  def to_astExpTuple(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.Tuple, MessagePack.ErrorMsg] = {
    def f_astExpTuple(reader: Reader): org.sireum.lang.ast.Exp.Tuple = {
      val r = reader.read_astExpTuple()
      return r
    }
    val r = to(data, f_astExpTuple _)
    return r
  }

  def from_astExpSelect(o: org.sireum.lang.ast.Exp.Select, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpSelect(o)
    return w.result
  }

  def to_astExpSelect(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.Select, MessagePack.ErrorMsg] = {
    def f_astExpSelect(reader: Reader): org.sireum.lang.ast.Exp.Select = {
      val r = reader.read_astExpSelect()
      return r
    }
    val r = to(data, f_astExpSelect _)
    return r
  }

  def from_astExpInvoke(o: org.sireum.lang.ast.Exp.Invoke, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpInvoke(o)
    return w.result
  }

  def to_astExpInvoke(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.Invoke, MessagePack.ErrorMsg] = {
    def f_astExpInvoke(reader: Reader): org.sireum.lang.ast.Exp.Invoke = {
      val r = reader.read_astExpInvoke()
      return r
    }
    val r = to(data, f_astExpInvoke _)
    return r
  }

  def from_astExpInvokeNamed(o: org.sireum.lang.ast.Exp.InvokeNamed, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpInvokeNamed(o)
    return w.result
  }

  def to_astExpInvokeNamed(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.InvokeNamed, MessagePack.ErrorMsg] = {
    def f_astExpInvokeNamed(reader: Reader): org.sireum.lang.ast.Exp.InvokeNamed = {
      val r = reader.read_astExpInvokeNamed()
      return r
    }
    val r = to(data, f_astExpInvokeNamed _)
    return r
  }

  def from_astExpIf(o: org.sireum.lang.ast.Exp.If, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpIf(o)
    return w.result
  }

  def to_astExpIf(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.If, MessagePack.ErrorMsg] = {
    def f_astExpIf(reader: Reader): org.sireum.lang.ast.Exp.If = {
      val r = reader.read_astExpIf()
      return r
    }
    val r = to(data, f_astExpIf _)
    return r
  }

  def from_astExpTypeCond(o: org.sireum.lang.ast.Exp.TypeCond, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpTypeCond(o)
    return w.result
  }

  def to_astExpTypeCond(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.TypeCond, MessagePack.ErrorMsg] = {
    def f_astExpTypeCond(reader: Reader): org.sireum.lang.ast.Exp.TypeCond = {
      val r = reader.read_astExpTypeCond()
      return r
    }
    val r = to(data, f_astExpTypeCond _)
    return r
  }

  def from_astExpSym(o: org.sireum.lang.ast.Exp.Sym, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpSym(o)
    return w.result
  }

  def to_astExpSym(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.Sym, MessagePack.ErrorMsg] = {
    def f_astExpSym(reader: Reader): org.sireum.lang.ast.Exp.Sym = {
      val r = reader.read_astExpSym()
      return r
    }
    val r = to(data, f_astExpSym _)
    return r
  }

  def from_astExpFunParam(o: org.sireum.lang.ast.Exp.Fun.Param, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpFunParam(o)
    return w.result
  }

  def to_astExpFunParam(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.Fun.Param, MessagePack.ErrorMsg] = {
    def f_astExpFunParam(reader: Reader): org.sireum.lang.ast.Exp.Fun.Param = {
      val r = reader.read_astExpFunParam()
      return r
    }
    val r = to(data, f_astExpFunParam _)
    return r
  }

  def from_astExpFun(o: org.sireum.lang.ast.Exp.Fun, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpFun(o)
    return w.result
  }

  def to_astExpFun(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.Fun, MessagePack.ErrorMsg] = {
    def f_astExpFun(reader: Reader): org.sireum.lang.ast.Exp.Fun = {
      val r = reader.read_astExpFun()
      return r
    }
    val r = to(data, f_astExpFun _)
    return r
  }

  def from_astExpForYield(o: org.sireum.lang.ast.Exp.ForYield, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpForYield(o)
    return w.result
  }

  def to_astExpForYield(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.ForYield, MessagePack.ErrorMsg] = {
    def f_astExpForYield(reader: Reader): org.sireum.lang.ast.Exp.ForYield = {
      val r = reader.read_astExpForYield()
      return r
    }
    val r = to(data, f_astExpForYield _)
    return r
  }

  def from_astExpQuant(o: org.sireum.lang.ast.Exp.Quant, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpQuant(o)
    return w.result
  }

  def to_astExpQuant(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.Quant, MessagePack.ErrorMsg] = {
    def f_astExpQuant(reader: Reader): org.sireum.lang.ast.Exp.Quant = {
      val r = reader.read_astExpQuant()
      return r
    }
    val r = to(data, f_astExpQuant _)
    return r
  }

  def from_astExpQuantType(o: org.sireum.lang.ast.Exp.QuantType, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpQuantType(o)
    return w.result
  }

  def to_astExpQuantType(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.QuantType, MessagePack.ErrorMsg] = {
    def f_astExpQuantType(reader: Reader): org.sireum.lang.ast.Exp.QuantType = {
      val r = reader.read_astExpQuantType()
      return r
    }
    val r = to(data, f_astExpQuantType _)
    return r
  }

  def from_astExpQuantRange(o: org.sireum.lang.ast.Exp.QuantRange, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpQuantRange(o)
    return w.result
  }

  def to_astExpQuantRange(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.QuantRange, MessagePack.ErrorMsg] = {
    def f_astExpQuantRange(reader: Reader): org.sireum.lang.ast.Exp.QuantRange = {
      val r = reader.read_astExpQuantRange()
      return r
    }
    val r = to(data, f_astExpQuantRange _)
    return r
  }

  def from_astExpQuantEach(o: org.sireum.lang.ast.Exp.QuantEach, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpQuantEach(o)
    return w.result
  }

  def to_astExpQuantEach(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.QuantEach, MessagePack.ErrorMsg] = {
    def f_astExpQuantEach(reader: Reader): org.sireum.lang.ast.Exp.QuantEach = {
      val r = reader.read_astExpQuantEach()
      return r
    }
    val r = to(data, f_astExpQuantEach _)
    return r
  }

  def from_astExpInput(o: org.sireum.lang.ast.Exp.Input, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpInput(o)
    return w.result
  }

  def to_astExpInput(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.Input, MessagePack.ErrorMsg] = {
    def f_astExpInput(reader: Reader): org.sireum.lang.ast.Exp.Input = {
      val r = reader.read_astExpInput()
      return r
    }
    val r = to(data, f_astExpInput _)
    return r
  }

  def from_astExpOld(o: org.sireum.lang.ast.Exp.Old, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpOld(o)
    return w.result
  }

  def to_astExpOld(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.Old, MessagePack.ErrorMsg] = {
    def f_astExpOld(reader: Reader): org.sireum.lang.ast.Exp.Old = {
      val r = reader.read_astExpOld()
      return r
    }
    val r = to(data, f_astExpOld _)
    return r
  }

  def from_astExpRS(o: org.sireum.lang.ast.Exp.RS, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpRS(o)
    return w.result
  }

  def to_astExpRS(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.RS, MessagePack.ErrorMsg] = {
    def f_astExpRS(reader: Reader): org.sireum.lang.ast.Exp.RS = {
      val r = reader.read_astExpRS()
      return r
    }
    val r = to(data, f_astExpRS _)
    return r
  }

  def from_astExpAt(o: org.sireum.lang.ast.Exp.At, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpAt(o)
    return w.result
  }

  def to_astExpAt(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.At, MessagePack.ErrorMsg] = {
    def f_astExpAt(reader: Reader): org.sireum.lang.ast.Exp.At = {
      val r = reader.read_astExpAt()
      return r
    }
    val r = to(data, f_astExpAt _)
    return r
  }

  def from_astExpLoopIndex(o: org.sireum.lang.ast.Exp.LoopIndex, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpLoopIndex(o)
    return w.result
  }

  def to_astExpLoopIndex(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.LoopIndex, MessagePack.ErrorMsg] = {
    def f_astExpLoopIndex(reader: Reader): org.sireum.lang.ast.Exp.LoopIndex = {
      val r = reader.read_astExpLoopIndex()
      return r
    }
    val r = to(data, f_astExpLoopIndex _)
    return r
  }

  def from_astExpStateSeq(o: org.sireum.lang.ast.Exp.StateSeq, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpStateSeq(o)
    return w.result
  }

  def to_astExpStateSeq(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.StateSeq, MessagePack.ErrorMsg] = {
    def f_astExpStateSeq(reader: Reader): org.sireum.lang.ast.Exp.StateSeq = {
      val r = reader.read_astExpStateSeq()
      return r
    }
    val r = to(data, f_astExpStateSeq _)
    return r
  }

  def from_astExpStateSeqFragment(o: org.sireum.lang.ast.Exp.StateSeq.Fragment, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpStateSeqFragment(o)
    return w.result
  }

  def to_astExpStateSeqFragment(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.StateSeq.Fragment, MessagePack.ErrorMsg] = {
    def f_astExpStateSeqFragment(reader: Reader): org.sireum.lang.ast.Exp.StateSeq.Fragment = {
      val r = reader.read_astExpStateSeqFragment()
      return r
    }
    val r = to(data, f_astExpStateSeqFragment _)
    return r
  }

  def from_astExpResult(o: org.sireum.lang.ast.Exp.Result, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpResult(o)
    return w.result
  }

  def to_astExpResult(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.Result, MessagePack.ErrorMsg] = {
    def f_astExpResult(reader: Reader): org.sireum.lang.ast.Exp.Result = {
      val r = reader.read_astExpResult()
      return r
    }
    val r = to(data, f_astExpResult _)
    return r
  }

  def from_astExpStrictPureBlock(o: org.sireum.lang.ast.Exp.StrictPureBlock, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpStrictPureBlock(o)
    return w.result
  }

  def to_astExpStrictPureBlock(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.StrictPureBlock, MessagePack.ErrorMsg] = {
    def f_astExpStrictPureBlock(reader: Reader): org.sireum.lang.ast.Exp.StrictPureBlock = {
      val r = reader.read_astExpStrictPureBlock()
      return r
    }
    val r = to(data, f_astExpStrictPureBlock _)
    return r
  }

  def from_astExpLabeled(o: org.sireum.lang.ast.Exp.Labeled, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpLabeled(o)
    return w.result
  }

  def to_astExpLabeled(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.Labeled, MessagePack.ErrorMsg] = {
    def f_astExpLabeled(reader: Reader): org.sireum.lang.ast.Exp.Labeled = {
      val r = reader.read_astExpLabeled()
      return r
    }
    val r = to(data, f_astExpLabeled _)
    return r
  }

  def from_astExpAssumeAgree(o: org.sireum.lang.ast.Exp.AssumeAgree, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpAssumeAgree(o)
    return w.result
  }

  def to_astExpAssumeAgree(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.AssumeAgree, MessagePack.ErrorMsg] = {
    def f_astExpAssumeAgree(reader: Reader): org.sireum.lang.ast.Exp.AssumeAgree = {
      val r = reader.read_astExpAssumeAgree()
      return r
    }
    val r = to(data, f_astExpAssumeAgree _)
    return r
  }

  def from_astExpAssertAgree(o: org.sireum.lang.ast.Exp.AssertAgree, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpAssertAgree(o)
    return w.result
  }

  def to_astExpAssertAgree(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.AssertAgree, MessagePack.ErrorMsg] = {
    def f_astExpAssertAgree(reader: Reader): org.sireum.lang.ast.Exp.AssertAgree = {
      val r = reader.read_astExpAssertAgree()
      return r
    }
    val r = to(data, f_astExpAssertAgree _)
    return r
  }

  def from_astExpInfoFlowInvariant(o: org.sireum.lang.ast.Exp.InfoFlowInvariant, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astExpInfoFlowInvariant(o)
    return w.result
  }

  def to_astExpInfoFlowInvariant(data: ISZ[U8]): Either[org.sireum.lang.ast.Exp.InfoFlowInvariant, MessagePack.ErrorMsg] = {
    def f_astExpInfoFlowInvariant(reader: Reader): org.sireum.lang.ast.Exp.InfoFlowInvariant = {
      val r = reader.read_astExpInfoFlowInvariant()
      return r
    }
    val r = to(data, f_astExpInfoFlowInvariant _)
    return r
  }

  def from_astNamedArg(o: org.sireum.lang.ast.NamedArg, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astNamedArg(o)
    return w.result
  }

  def to_astNamedArg(data: ISZ[U8]): Either[org.sireum.lang.ast.NamedArg, MessagePack.ErrorMsg] = {
    def f_astNamedArg(reader: Reader): org.sireum.lang.ast.NamedArg = {
      val r = reader.read_astNamedArg()
      return r
    }
    val r = to(data, f_astNamedArg _)
    return r
  }

  def from_astId(o: org.sireum.lang.ast.Id, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astId(o)
    return w.result
  }

  def to_astId(data: ISZ[U8]): Either[org.sireum.lang.ast.Id, MessagePack.ErrorMsg] = {
    def f_astId(reader: Reader): org.sireum.lang.ast.Id = {
      val r = reader.read_astId()
      return r
    }
    val r = to(data, f_astId _)
    return r
  }

  def from_astName(o: org.sireum.lang.ast.Name, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astName(o)
    return w.result
  }

  def to_astName(data: ISZ[U8]): Either[org.sireum.lang.ast.Name, MessagePack.ErrorMsg] = {
    def f_astName(reader: Reader): org.sireum.lang.ast.Name = {
      val r = reader.read_astName()
      return r
    }
    val r = to(data, f_astName _)
    return r
  }

  def from_astBody(o: org.sireum.lang.ast.Body, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astBody(o)
    return w.result
  }

  def to_astBody(data: ISZ[U8]): Either[org.sireum.lang.ast.Body, MessagePack.ErrorMsg] = {
    def f_astBody(reader: Reader): org.sireum.lang.ast.Body = {
      val r = reader.read_astBody()
      return r
    }
    val r = to(data, f_astBody _)
    return r
  }

  def from_astAdtParam(o: org.sireum.lang.ast.AdtParam, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astAdtParam(o)
    return w.result
  }

  def to_astAdtParam(data: ISZ[U8]): Either[org.sireum.lang.ast.AdtParam, MessagePack.ErrorMsg] = {
    def f_astAdtParam(reader: Reader): org.sireum.lang.ast.AdtParam = {
      val r = reader.read_astAdtParam()
      return r
    }
    val r = to(data, f_astAdtParam _)
    return r
  }

  def from_astAnnotation(o: org.sireum.lang.ast.Annotation, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astAnnotation(o)
    return w.result
  }

  def to_astAnnotation(data: ISZ[U8]): Either[org.sireum.lang.ast.Annotation, MessagePack.ErrorMsg] = {
    def f_astAnnotation(reader: Reader): org.sireum.lang.ast.Annotation = {
      val r = reader.read_astAnnotation()
      return r
    }
    val r = to(data, f_astAnnotation _)
    return r
  }

  def from_astMethodSig(o: org.sireum.lang.ast.MethodSig, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astMethodSig(o)
    return w.result
  }

  def to_astMethodSig(data: ISZ[U8]): Either[org.sireum.lang.ast.MethodSig, MessagePack.ErrorMsg] = {
    def f_astMethodSig(reader: Reader): org.sireum.lang.ast.MethodSig = {
      val r = reader.read_astMethodSig()
      return r
    }
    val r = to(data, f_astMethodSig _)
    return r
  }

  def from_astParam(o: org.sireum.lang.ast.Param, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astParam(o)
    return w.result
  }

  def to_astParam(data: ISZ[U8]): Either[org.sireum.lang.ast.Param, MessagePack.ErrorMsg] = {
    def f_astParam(reader: Reader): org.sireum.lang.ast.Param = {
      val r = reader.read_astParam()
      return r
    }
    val r = to(data, f_astParam _)
    return r
  }

  def from_astTypeParam(o: org.sireum.lang.ast.TypeParam, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astTypeParam(o)
    return w.result
  }

  def to_astTypeParam(data: ISZ[U8]): Either[org.sireum.lang.ast.TypeParam, MessagePack.ErrorMsg] = {
    def f_astTypeParam(reader: Reader): org.sireum.lang.ast.TypeParam = {
      val r = reader.read_astTypeParam()
      return r
    }
    val r = to(data, f_astTypeParam _)
    return r
  }

  def from_astAttr(o: org.sireum.lang.ast.Attr, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astAttr(o)
    return w.result
  }

  def to_astAttr(data: ISZ[U8]): Either[org.sireum.lang.ast.Attr, MessagePack.ErrorMsg] = {
    def f_astAttr(reader: Reader): org.sireum.lang.ast.Attr = {
      val r = reader.read_astAttr()
      return r
    }
    val r = to(data, f_astAttr _)
    return r
  }

  def from_astTypedAttr(o: org.sireum.lang.ast.TypedAttr, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astTypedAttr(o)
    return w.result
  }

  def to_astTypedAttr(data: ISZ[U8]): Either[org.sireum.lang.ast.TypedAttr, MessagePack.ErrorMsg] = {
    def f_astTypedAttr(reader: Reader): org.sireum.lang.ast.TypedAttr = {
      val r = reader.read_astTypedAttr()
      return r
    }
    val r = to(data, f_astTypedAttr _)
    return r
  }

  def from_astResolvedAttr(o: org.sireum.lang.ast.ResolvedAttr, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astResolvedAttr(o)
    return w.result
  }

  def to_astResolvedAttr(data: ISZ[U8]): Either[org.sireum.lang.ast.ResolvedAttr, MessagePack.ErrorMsg] = {
    def f_astResolvedAttr(reader: Reader): org.sireum.lang.ast.ResolvedAttr = {
      val r = reader.read_astResolvedAttr()
      return r
    }
    val r = to(data, f_astResolvedAttr _)
    return r
  }

  def from_astResolvedInfo(o: org.sireum.lang.ast.ResolvedInfo, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astResolvedInfo(o)
    return w.result
  }

  def to_astResolvedInfo(data: ISZ[U8]): Either[org.sireum.lang.ast.ResolvedInfo, MessagePack.ErrorMsg] = {
    def f_astResolvedInfo(reader: Reader): org.sireum.lang.ast.ResolvedInfo = {
      val r = reader.read_astResolvedInfo()
      return r
    }
    val r = to(data, f_astResolvedInfo _)
    return r
  }

  def from_astResolvedInfoBuiltIn(o: org.sireum.lang.ast.ResolvedInfo.BuiltIn, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astResolvedInfoBuiltIn(o)
    return w.result
  }

  def to_astResolvedInfoBuiltIn(data: ISZ[U8]): Either[org.sireum.lang.ast.ResolvedInfo.BuiltIn, MessagePack.ErrorMsg] = {
    def f_astResolvedInfoBuiltIn(reader: Reader): org.sireum.lang.ast.ResolvedInfo.BuiltIn = {
      val r = reader.read_astResolvedInfoBuiltIn()
      return r
    }
    val r = to(data, f_astResolvedInfoBuiltIn _)
    return r
  }

  def from_astResolvedInfoPackage(o: org.sireum.lang.ast.ResolvedInfo.Package, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astResolvedInfoPackage(o)
    return w.result
  }

  def to_astResolvedInfoPackage(data: ISZ[U8]): Either[org.sireum.lang.ast.ResolvedInfo.Package, MessagePack.ErrorMsg] = {
    def f_astResolvedInfoPackage(reader: Reader): org.sireum.lang.ast.ResolvedInfo.Package = {
      val r = reader.read_astResolvedInfoPackage()
      return r
    }
    val r = to(data, f_astResolvedInfoPackage _)
    return r
  }

  def from_astResolvedInfoEnum(o: org.sireum.lang.ast.ResolvedInfo.Enum, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astResolvedInfoEnum(o)
    return w.result
  }

  def to_astResolvedInfoEnum(data: ISZ[U8]): Either[org.sireum.lang.ast.ResolvedInfo.Enum, MessagePack.ErrorMsg] = {
    def f_astResolvedInfoEnum(reader: Reader): org.sireum.lang.ast.ResolvedInfo.Enum = {
      val r = reader.read_astResolvedInfoEnum()
      return r
    }
    val r = to(data, f_astResolvedInfoEnum _)
    return r
  }

  def from_astResolvedInfoEnumElement(o: org.sireum.lang.ast.ResolvedInfo.EnumElement, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astResolvedInfoEnumElement(o)
    return w.result
  }

  def to_astResolvedInfoEnumElement(data: ISZ[U8]): Either[org.sireum.lang.ast.ResolvedInfo.EnumElement, MessagePack.ErrorMsg] = {
    def f_astResolvedInfoEnumElement(reader: Reader): org.sireum.lang.ast.ResolvedInfo.EnumElement = {
      val r = reader.read_astResolvedInfoEnumElement()
      return r
    }
    val r = to(data, f_astResolvedInfoEnumElement _)
    return r
  }

  def from_astResolvedInfoObject(o: org.sireum.lang.ast.ResolvedInfo.Object, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astResolvedInfoObject(o)
    return w.result
  }

  def to_astResolvedInfoObject(data: ISZ[U8]): Either[org.sireum.lang.ast.ResolvedInfo.Object, MessagePack.ErrorMsg] = {
    def f_astResolvedInfoObject(reader: Reader): org.sireum.lang.ast.ResolvedInfo.Object = {
      val r = reader.read_astResolvedInfoObject()
      return r
    }
    val r = to(data, f_astResolvedInfoObject _)
    return r
  }

  def from_astResolvedInfoVar(o: org.sireum.lang.ast.ResolvedInfo.Var, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astResolvedInfoVar(o)
    return w.result
  }

  def to_astResolvedInfoVar(data: ISZ[U8]): Either[org.sireum.lang.ast.ResolvedInfo.Var, MessagePack.ErrorMsg] = {
    def f_astResolvedInfoVar(reader: Reader): org.sireum.lang.ast.ResolvedInfo.Var = {
      val r = reader.read_astResolvedInfoVar()
      return r
    }
    val r = to(data, f_astResolvedInfoVar _)
    return r
  }

  def from_astResolvedInfoMethod(o: org.sireum.lang.ast.ResolvedInfo.Method, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astResolvedInfoMethod(o)
    return w.result
  }

  def to_astResolvedInfoMethod(data: ISZ[U8]): Either[org.sireum.lang.ast.ResolvedInfo.Method, MessagePack.ErrorMsg] = {
    def f_astResolvedInfoMethod(reader: Reader): org.sireum.lang.ast.ResolvedInfo.Method = {
      val r = reader.read_astResolvedInfoMethod()
      return r
    }
    val r = to(data, f_astResolvedInfoMethod _)
    return r
  }

  def from_astResolvedInfoMethods(o: org.sireum.lang.ast.ResolvedInfo.Methods, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astResolvedInfoMethods(o)
    return w.result
  }

  def to_astResolvedInfoMethods(data: ISZ[U8]): Either[org.sireum.lang.ast.ResolvedInfo.Methods, MessagePack.ErrorMsg] = {
    def f_astResolvedInfoMethods(reader: Reader): org.sireum.lang.ast.ResolvedInfo.Methods = {
      val r = reader.read_astResolvedInfoMethods()
      return r
    }
    val r = to(data, f_astResolvedInfoMethods _)
    return r
  }

  def from_astResolvedInfoTuple(o: org.sireum.lang.ast.ResolvedInfo.Tuple, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astResolvedInfoTuple(o)
    return w.result
  }

  def to_astResolvedInfoTuple(data: ISZ[U8]): Either[org.sireum.lang.ast.ResolvedInfo.Tuple, MessagePack.ErrorMsg] = {
    def f_astResolvedInfoTuple(reader: Reader): org.sireum.lang.ast.ResolvedInfo.Tuple = {
      val r = reader.read_astResolvedInfoTuple()
      return r
    }
    val r = to(data, f_astResolvedInfoTuple _)
    return r
  }

  def from_astResolvedInfoLocalVar(o: org.sireum.lang.ast.ResolvedInfo.LocalVar, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astResolvedInfoLocalVar(o)
    return w.result
  }

  def to_astResolvedInfoLocalVar(data: ISZ[U8]): Either[org.sireum.lang.ast.ResolvedInfo.LocalVar, MessagePack.ErrorMsg] = {
    def f_astResolvedInfoLocalVar(reader: Reader): org.sireum.lang.ast.ResolvedInfo.LocalVar = {
      val r = reader.read_astResolvedInfoLocalVar()
      return r
    }
    val r = to(data, f_astResolvedInfoLocalVar _)
    return r
  }

  def from_astResolvedInfoFact(o: org.sireum.lang.ast.ResolvedInfo.Fact, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astResolvedInfoFact(o)
    return w.result
  }

  def to_astResolvedInfoFact(data: ISZ[U8]): Either[org.sireum.lang.ast.ResolvedInfo.Fact, MessagePack.ErrorMsg] = {
    def f_astResolvedInfoFact(reader: Reader): org.sireum.lang.ast.ResolvedInfo.Fact = {
      val r = reader.read_astResolvedInfoFact()
      return r
    }
    val r = to(data, f_astResolvedInfoFact _)
    return r
  }

  def from_astResolvedInfoTheorem(o: org.sireum.lang.ast.ResolvedInfo.Theorem, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astResolvedInfoTheorem(o)
    return w.result
  }

  def to_astResolvedInfoTheorem(data: ISZ[U8]): Either[org.sireum.lang.ast.ResolvedInfo.Theorem, MessagePack.ErrorMsg] = {
    def f_astResolvedInfoTheorem(reader: Reader): org.sireum.lang.ast.ResolvedInfo.Theorem = {
      val r = reader.read_astResolvedInfoTheorem()
      return r
    }
    val r = to(data, f_astResolvedInfoTheorem _)
    return r
  }

  def from_astResolvedInfoInv(o: org.sireum.lang.ast.ResolvedInfo.Inv, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astResolvedInfoInv(o)
    return w.result
  }

  def to_astResolvedInfoInv(data: ISZ[U8]): Either[org.sireum.lang.ast.ResolvedInfo.Inv, MessagePack.ErrorMsg] = {
    def f_astResolvedInfoInv(reader: Reader): org.sireum.lang.ast.ResolvedInfo.Inv = {
      val r = reader.read_astResolvedInfoInv()
      return r
    }
    val r = to(data, f_astResolvedInfoInv _)
    return r
  }

  def from_astTruthTableRow(o: org.sireum.lang.ast.TruthTable.Row, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astTruthTableRow(o)
    return w.result
  }

  def to_astTruthTableRow(data: ISZ[U8]): Either[org.sireum.lang.ast.TruthTable.Row, MessagePack.ErrorMsg] = {
    def f_astTruthTableRow(reader: Reader): org.sireum.lang.ast.TruthTable.Row = {
      val r = reader.read_astTruthTableRow()
      return r
    }
    val r = to(data, f_astTruthTableRow _)
    return r
  }

  def from_astTruthTableAssignment(o: org.sireum.lang.ast.TruthTable.Assignment, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astTruthTableAssignment(o)
    return w.result
  }

  def to_astTruthTableAssignment(data: ISZ[U8]): Either[org.sireum.lang.ast.TruthTable.Assignment, MessagePack.ErrorMsg] = {
    def f_astTruthTableAssignment(reader: Reader): org.sireum.lang.ast.TruthTable.Assignment = {
      val r = reader.read_astTruthTableAssignment()
      return r
    }
    val r = to(data, f_astTruthTableAssignment _)
    return r
  }

  def from_astTruthTableConclusion(o: org.sireum.lang.ast.TruthTable.Conclusion, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astTruthTableConclusion(o)
    return w.result
  }

  def to_astTruthTableConclusion(data: ISZ[U8]): Either[org.sireum.lang.ast.TruthTable.Conclusion, MessagePack.ErrorMsg] = {
    def f_astTruthTableConclusion(reader: Reader): org.sireum.lang.ast.TruthTable.Conclusion = {
      val r = reader.read_astTruthTableConclusion()
      return r
    }
    val r = to(data, f_astTruthTableConclusion _)
    return r
  }

  def from_astTruthTableConclusionValidity(o: org.sireum.lang.ast.TruthTable.Conclusion.Validity, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astTruthTableConclusionValidity(o)
    return w.result
  }

  def to_astTruthTableConclusionValidity(data: ISZ[U8]): Either[org.sireum.lang.ast.TruthTable.Conclusion.Validity, MessagePack.ErrorMsg] = {
    def f_astTruthTableConclusionValidity(reader: Reader): org.sireum.lang.ast.TruthTable.Conclusion.Validity = {
      val r = reader.read_astTruthTableConclusionValidity()
      return r
    }
    val r = to(data, f_astTruthTableConclusionValidity _)
    return r
  }

  def from_astTruthTableConclusionTautology(o: org.sireum.lang.ast.TruthTable.Conclusion.Tautology, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astTruthTableConclusionTautology(o)
    return w.result
  }

  def to_astTruthTableConclusionTautology(data: ISZ[U8]): Either[org.sireum.lang.ast.TruthTable.Conclusion.Tautology, MessagePack.ErrorMsg] = {
    def f_astTruthTableConclusionTautology(reader: Reader): org.sireum.lang.ast.TruthTable.Conclusion.Tautology = {
      val r = reader.read_astTruthTableConclusionTautology()
      return r
    }
    val r = to(data, f_astTruthTableConclusionTautology _)
    return r
  }

  def from_astTruthTableConclusionContradictory(o: org.sireum.lang.ast.TruthTable.Conclusion.Contradictory, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astTruthTableConclusionContradictory(o)
    return w.result
  }

  def to_astTruthTableConclusionContradictory(data: ISZ[U8]): Either[org.sireum.lang.ast.TruthTable.Conclusion.Contradictory, MessagePack.ErrorMsg] = {
    def f_astTruthTableConclusionContradictory(reader: Reader): org.sireum.lang.ast.TruthTable.Conclusion.Contradictory = {
      val r = reader.read_astTruthTableConclusionContradictory()
      return r
    }
    val r = to(data, f_astTruthTableConclusionContradictory _)
    return r
  }

  def from_astTruthTableConclusionContingent(o: org.sireum.lang.ast.TruthTable.Conclusion.Contingent, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astTruthTableConclusionContingent(o)
    return w.result
  }

  def to_astTruthTableConclusionContingent(data: ISZ[U8]): Either[org.sireum.lang.ast.TruthTable.Conclusion.Contingent, MessagePack.ErrorMsg] = {
    def f_astTruthTableConclusionContingent(reader: Reader): org.sireum.lang.ast.TruthTable.Conclusion.Contingent = {
      val r = reader.read_astTruthTableConclusionContingent()
      return r
    }
    val r = to(data, f_astTruthTableConclusionContingent _)
    return r
  }

  def from_astTyped(o: org.sireum.lang.ast.Typed, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astTyped(o)
    return w.result
  }

  def to_astTyped(data: ISZ[U8]): Either[org.sireum.lang.ast.Typed, MessagePack.ErrorMsg] = {
    def f_astTyped(reader: Reader): org.sireum.lang.ast.Typed = {
      val r = reader.read_astTyped()
      return r
    }
    val r = to(data, f_astTyped _)
    return r
  }

  def from_astTypedName(o: org.sireum.lang.ast.Typed.Name, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astTypedName(o)
    return w.result
  }

  def to_astTypedName(data: ISZ[U8]): Either[org.sireum.lang.ast.Typed.Name, MessagePack.ErrorMsg] = {
    def f_astTypedName(reader: Reader): org.sireum.lang.ast.Typed.Name = {
      val r = reader.read_astTypedName()
      return r
    }
    val r = to(data, f_astTypedName _)
    return r
  }

  def from_astTypedTuple(o: org.sireum.lang.ast.Typed.Tuple, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astTypedTuple(o)
    return w.result
  }

  def to_astTypedTuple(data: ISZ[U8]): Either[org.sireum.lang.ast.Typed.Tuple, MessagePack.ErrorMsg] = {
    def f_astTypedTuple(reader: Reader): org.sireum.lang.ast.Typed.Tuple = {
      val r = reader.read_astTypedTuple()
      return r
    }
    val r = to(data, f_astTypedTuple _)
    return r
  }

  def from_astTypedFun(o: org.sireum.lang.ast.Typed.Fun, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astTypedFun(o)
    return w.result
  }

  def to_astTypedFun(data: ISZ[U8]): Either[org.sireum.lang.ast.Typed.Fun, MessagePack.ErrorMsg] = {
    def f_astTypedFun(reader: Reader): org.sireum.lang.ast.Typed.Fun = {
      val r = reader.read_astTypedFun()
      return r
    }
    val r = to(data, f_astTypedFun _)
    return r
  }

  def from_astTypedTypeVar(o: org.sireum.lang.ast.Typed.TypeVar, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astTypedTypeVar(o)
    return w.result
  }

  def to_astTypedTypeVar(data: ISZ[U8]): Either[org.sireum.lang.ast.Typed.TypeVar, MessagePack.ErrorMsg] = {
    def f_astTypedTypeVar(reader: Reader): org.sireum.lang.ast.Typed.TypeVar = {
      val r = reader.read_astTypedTypeVar()
      return r
    }
    val r = to(data, f_astTypedTypeVar _)
    return r
  }

  def from_astTypedPackage(o: org.sireum.lang.ast.Typed.Package, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astTypedPackage(o)
    return w.result
  }

  def to_astTypedPackage(data: ISZ[U8]): Either[org.sireum.lang.ast.Typed.Package, MessagePack.ErrorMsg] = {
    def f_astTypedPackage(reader: Reader): org.sireum.lang.ast.Typed.Package = {
      val r = reader.read_astTypedPackage()
      return r
    }
    val r = to(data, f_astTypedPackage _)
    return r
  }

  def from_astTypedObject(o: org.sireum.lang.ast.Typed.Object, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astTypedObject(o)
    return w.result
  }

  def to_astTypedObject(data: ISZ[U8]): Either[org.sireum.lang.ast.Typed.Object, MessagePack.ErrorMsg] = {
    def f_astTypedObject(reader: Reader): org.sireum.lang.ast.Typed.Object = {
      val r = reader.read_astTypedObject()
      return r
    }
    val r = to(data, f_astTypedObject _)
    return r
  }

  def from_astTypedEnum(o: org.sireum.lang.ast.Typed.Enum, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astTypedEnum(o)
    return w.result
  }

  def to_astTypedEnum(data: ISZ[U8]): Either[org.sireum.lang.ast.Typed.Enum, MessagePack.ErrorMsg] = {
    def f_astTypedEnum(reader: Reader): org.sireum.lang.ast.Typed.Enum = {
      val r = reader.read_astTypedEnum()
      return r
    }
    val r = to(data, f_astTypedEnum _)
    return r
  }

  def from_astTypedMethod(o: org.sireum.lang.ast.Typed.Method, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astTypedMethod(o)
    return w.result
  }

  def to_astTypedMethod(data: ISZ[U8]): Either[org.sireum.lang.ast.Typed.Method, MessagePack.ErrorMsg] = {
    def f_astTypedMethod(reader: Reader): org.sireum.lang.ast.Typed.Method = {
      val r = reader.read_astTypedMethod()
      return r
    }
    val r = to(data, f_astTypedMethod _)
    return r
  }

  def from_astTypedMethods(o: org.sireum.lang.ast.Typed.Methods, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astTypedMethods(o)
    return w.result
  }

  def to_astTypedMethods(data: ISZ[U8]): Either[org.sireum.lang.ast.Typed.Methods, MessagePack.ErrorMsg] = {
    def f_astTypedMethods(reader: Reader): org.sireum.lang.ast.Typed.Methods = {
      val r = reader.read_astTypedMethods()
      return r
    }
    val r = to(data, f_astTypedMethods _)
    return r
  }

  def from_astTypedFact(o: org.sireum.lang.ast.Typed.Fact, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astTypedFact(o)
    return w.result
  }

  def to_astTypedFact(data: ISZ[U8]): Either[org.sireum.lang.ast.Typed.Fact, MessagePack.ErrorMsg] = {
    def f_astTypedFact(reader: Reader): org.sireum.lang.ast.Typed.Fact = {
      val r = reader.read_astTypedFact()
      return r
    }
    val r = to(data, f_astTypedFact _)
    return r
  }

  def from_astTypedTheorem(o: org.sireum.lang.ast.Typed.Theorem, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astTypedTheorem(o)
    return w.result
  }

  def to_astTypedTheorem(data: ISZ[U8]): Either[org.sireum.lang.ast.Typed.Theorem, MessagePack.ErrorMsg] = {
    def f_astTypedTheorem(reader: Reader): org.sireum.lang.ast.Typed.Theorem = {
      val r = reader.read_astTypedTheorem()
      return r
    }
    val r = to(data, f_astTypedTheorem _)
    return r
  }

  def from_astTypedInv(o: org.sireum.lang.ast.Typed.Inv, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_astTypedInv(o)
    return w.result
  }

  def to_astTypedInv(data: ISZ[U8]): Either[org.sireum.lang.ast.Typed.Inv, MessagePack.ErrorMsg] = {
    def f_astTypedInv(reader: Reader): org.sireum.lang.ast.Typed.Inv = {
      val r = reader.read_astTypedInv()
      return r
    }
    val r = to(data, f_astTypedInv _)
    return r
  }

}