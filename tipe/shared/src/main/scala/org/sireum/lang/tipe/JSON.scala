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

// This file is auto-generated from Info.scala, AST.scala

package org.sireum.lang.tipe

import org.sireum._
import org.sireum.Json.Printer._

object JSON {

  object Printer {

    @pure def print_symbolScope(o: org.sireum.lang.symbol.Scope): ST = {
      o match {
        case o: org.sireum.lang.symbol.Scope.Local => return print_symbolScopeLocal(o)
        case o: org.sireum.lang.symbol.Scope.Global => return print_symbolScopeGlobal(o)
      }
    }

    @pure def print_symbolScopeLocal(o: org.sireum.lang.symbol.Scope.Local): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.symbol.Scope.Local""""),
        ("nameMap", printHashMap(F, o.nameMap, printString _, print_symbolInfo _)),
        ("typeMap", printHashMap(F, o.typeMap, printString _, print_symbolTypeInfo _)),
        ("localThisOpt", printOption(F, o.localThisOpt, print_astTyped _)),
        ("methodReturnOpt", printOption(F, o.methodReturnOpt, print_astTyped _)),
        ("outerOpt", printOption(F, o.outerOpt, print_symbolScope _))
      ))
    }

    @pure def print_symbolScopeGlobal(o: org.sireum.lang.symbol.Scope.Global): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.symbol.Scope.Global""""),
        ("packageName", printISZ(T, o.packageName, printString _)),
        ("imports", printISZ(F, o.imports, print_astStmtImport _)),
        ("enclosingName", printISZ(T, o.enclosingName, printString _))
      ))
    }

    @pure def print_symbolInfo(o: org.sireum.lang.symbol.Info): ST = {
      o match {
        case o: org.sireum.lang.symbol.Info.Package => return print_symbolInfoPackage(o)
        case o: org.sireum.lang.symbol.Info.Var => return print_symbolInfoVar(o)
        case o: org.sireum.lang.symbol.Info.SpecVar => return print_symbolInfoSpecVar(o)
        case o: org.sireum.lang.symbol.Info.Method => return print_symbolInfoMethod(o)
        case o: org.sireum.lang.symbol.Info.SpecMethod => return print_symbolInfoSpecMethod(o)
        case o: org.sireum.lang.symbol.Info.Object => return print_symbolInfoObject(o)
        case o: org.sireum.lang.symbol.Info.ExtMethod => return print_symbolInfoExtMethod(o)
        case o: org.sireum.lang.symbol.Info.Enum => return print_symbolInfoEnum(o)
        case o: org.sireum.lang.symbol.Info.EnumElement => return print_symbolInfoEnumElement(o)
        case o: org.sireum.lang.symbol.Info.LocalVar => return print_symbolInfoLocalVar(o)
        case o: org.sireum.lang.symbol.Info.QuantVar => return print_symbolInfoQuantVar(o)
      }
    }

    @pure def print_symbolInfoPackage(o: org.sireum.lang.symbol.Info.Package): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.symbol.Info.Package""""),
        ("name", printISZ(T, o.name, printString _)),
        ("typedOpt", printOption(F, o.typedOpt, print_astTyped _)),
        ("resOpt", printOption(F, o.resOpt, print_astResolvedInfo _))
      ))
    }

    @pure def print_symbolInfoVar(o: org.sireum.lang.symbol.Info.Var): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.symbol.Info.Var""""),
        ("owner", printISZ(T, o.owner, printString _)),
        ("isInObject", printB(o.isInObject)),
        ("scope", print_symbolScope(o.scope)),
        ("ast", print_astStmtVar(o.ast))
      ))
    }

    @pure def print_symbolInfoSpecVar(o: org.sireum.lang.symbol.Info.SpecVar): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.symbol.Info.SpecVar""""),
        ("owner", printISZ(T, o.owner, printString _)),
        ("isInObject", printB(o.isInObject)),
        ("scope", print_symbolScope(o.scope)),
        ("ast", print_astStmtSpecVar(o.ast))
      ))
    }

    @pure def print_symbolInfoMethod(o: org.sireum.lang.symbol.Info.Method): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.symbol.Info.Method""""),
        ("owner", printISZ(T, o.owner, printString _)),
        ("isInObject", printB(o.isInObject)),
        ("scope", print_symbolScope(o.scope)),
        ("hasBody", printB(o.hasBody)),
        ("ast", print_astStmtMethod(o.ast))
      ))
    }

    @pure def print_symbolInfoSpecMethod(o: org.sireum.lang.symbol.Info.SpecMethod): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.symbol.Info.SpecMethod""""),
        ("owner", printISZ(T, o.owner, printString _)),
        ("isInObject", printB(o.isInObject)),
        ("scope", print_symbolScope(o.scope)),
        ("ast", print_astStmtSpecMethod(o.ast))
      ))
    }

    @pure def print_symbolInfoObject(o: org.sireum.lang.symbol.Info.Object): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.symbol.Info.Object""""),
        ("owner", printISZ(T, o.owner, printString _)),
        ("isSynthetic", printB(o.isSynthetic)),
        ("scope", print_symbolScopeGlobal(o.scope)),
        ("outlined", printB(o.outlined)),
        ("typeChecked", printB(o.typeChecked)),
        ("ast", print_astStmtObject(o.ast)),
        ("typedOpt", printOption(F, o.typedOpt, print_astTyped _)),
        ("resOpt", printOption(F, o.resOpt, print_astResolvedInfo _)),
        ("constructorRes", print_astResolvedInfoMethod(o.constructorRes))
      ))
    }

    @pure def print_symbolInfoExtMethod(o: org.sireum.lang.symbol.Info.ExtMethod): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.symbol.Info.ExtMethod""""),
        ("owner", printISZ(T, o.owner, printString _)),
        ("scope", print_symbolScopeGlobal(o.scope)),
        ("ast", print_astStmtExtMethod(o.ast))
      ))
    }

    @pure def print_symbolInfoEnum(o: org.sireum.lang.symbol.Info.Enum): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.symbol.Info.Enum""""),
        ("name", printISZ(T, o.name, printString _)),
        ("elements", printMap(F, o.elements, printString _, print_astResolvedInfo _)),
        ("typedOpt", printOption(F, o.typedOpt, print_astTyped _)),
        ("resOpt", printOption(F, o.resOpt, print_astResolvedInfo _)),
        ("elementTypedOpt", printOption(F, o.elementTypedOpt, print_astTyped _)),
        ("posOpt", printOption(F, o.posOpt, printPosition _))
      ))
    }

    @pure def print_symbolInfoEnumElement(o: org.sireum.lang.symbol.Info.EnumElement): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.symbol.Info.EnumElement""""),
        ("owner", printISZ(T, o.owner, printString _)),
        ("id", printString(o.id)),
        ("typedOpt", printOption(F, o.typedOpt, print_astTyped _)),
        ("resOpt", printOption(F, o.resOpt, print_astResolvedInfo _)),
        ("posOpt", printOption(F, o.posOpt, printPosition _))
      ))
    }

    @pure def print_symbolInfoLocalVar(o: org.sireum.lang.symbol.Info.LocalVar): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.symbol.Info.LocalVar""""),
        ("name", printISZ(T, o.name, printString _)),
        ("isVal", printB(o.isVal)),
        ("ast", print_astId(o.ast)),
        ("typedOpt", printOption(F, o.typedOpt, print_astTyped _)),
        ("resOpt", printOption(F, o.resOpt, print_astResolvedInfo _))
      ))
    }

    @pure def print_symbolInfoQuantVar(o: org.sireum.lang.symbol.Info.QuantVar): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.symbol.Info.QuantVar""""),
        ("name", printISZ(T, o.name, printString _)),
        ("ast", print_astId(o.ast)),
        ("typedOpt", printOption(F, o.typedOpt, print_astTyped _)),
        ("resOpt", printOption(F, o.resOpt, print_astResolvedInfo _))
      ))
    }

    @pure def print_symbolTypeInfo(o: org.sireum.lang.symbol.TypeInfo): ST = {
      o match {
        case o: org.sireum.lang.symbol.TypeInfo.SubZ => return print_symbolTypeInfoSubZ(o)
        case o: org.sireum.lang.symbol.TypeInfo.Enum => return print_symbolTypeInfoEnum(o)
        case o: org.sireum.lang.symbol.TypeInfo.Sig => return print_symbolTypeInfoSig(o)
        case o: org.sireum.lang.symbol.TypeInfo.Adt => return print_symbolTypeInfoAdt(o)
        case o: org.sireum.lang.symbol.TypeInfo.TypeAlias => return print_symbolTypeInfoTypeAlias(o)
        case o: org.sireum.lang.symbol.TypeInfo.TypeVar => return print_symbolTypeInfoTypeVar(o)
      }
    }

    @pure def print_symbolTypeInfoSubZ(o: org.sireum.lang.symbol.TypeInfo.SubZ): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.symbol.TypeInfo.SubZ""""),
        ("owner", printISZ(T, o.owner, printString _)),
        ("ast", print_astStmtSubZ(o.ast))
      ))
    }

    @pure def print_symbolTypeInfoEnum(o: org.sireum.lang.symbol.TypeInfo.Enum): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.symbol.TypeInfo.Enum""""),
        ("owner", printISZ(T, o.owner, printString _)),
        ("elements", printMap(F, o.elements, printString _, print_astResolvedInfo _)),
        ("posOpt", printOption(F, o.posOpt, printPosition _))
      ))
    }

    @pure def print_symbolTypeInfoSig(o: org.sireum.lang.symbol.TypeInfo.Sig): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.symbol.TypeInfo.Sig""""),
        ("owner", printISZ(T, o.owner, printString _)),
        ("outlined", printB(o.outlined)),
        ("typeChecked", printB(o.typeChecked)),
        ("tpe", print_astTypedName(o.tpe)),
        ("ancestors", printISZ(F, o.ancestors, print_astTypedName _)),
        ("specVars", printHashSMap(F, o.specVars, printString _, print_symbolInfoSpecVar _)),
        ("specMethods", printHashMap(F, o.specMethods, printString _, print_symbolInfoSpecMethod _)),
        ("methods", printHashMap(F, o.methods, printString _, print_symbolInfoMethod _)),
        ("refinements", printHashMap(F, o.refinements, printString _, print_symbolTypeInfoName _)),
        ("scope", print_symbolScopeGlobal(o.scope)),
        ("ast", print_astStmtSig(o.ast))
      ))
    }

    @pure def print_symbolTypeInfoName(o: org.sireum.lang.symbol.TypeInfo.Name): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.symbol.TypeInfo.Name""""),
        ("ids", printISZ(T, o.ids, printString _))
      ))
    }

    @pure def print_symbolTypeInfoAdt(o: org.sireum.lang.symbol.TypeInfo.Adt): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.symbol.TypeInfo.Adt""""),
        ("owner", printISZ(T, o.owner, printString _)),
        ("outlined", printB(o.outlined)),
        ("typeChecked", printB(o.typeChecked)),
        ("tpe", print_astTypedName(o.tpe)),
        ("constructorTypeOpt", printOption(F, o.constructorTypeOpt, print_astTyped _)),
        ("constructorResOpt", printOption(F, o.constructorResOpt, print_astResolvedInfo _)),
        ("extractorTypeMap", printMap(F, o.extractorTypeMap, printString _, print_astTyped _)),
        ("extractorResOpt", printOption(F, o.extractorResOpt, print_astResolvedInfo _)),
        ("ancestors", printISZ(F, o.ancestors, print_astTypedName _)),
        ("specVars", printHashSMap(F, o.specVars, printString _, print_symbolInfoSpecVar _)),
        ("vars", printHashSMap(F, o.vars, printString _, print_symbolInfoVar _)),
        ("specMethods", printHashMap(F, o.specMethods, printString _, print_symbolInfoSpecMethod _)),
        ("methods", printHashMap(F, o.methods, printString _, print_symbolInfoMethod _)),
        ("refinements", printHashMap(F, o.refinements, printString _, print_symbolTypeInfoName _)),
        ("scope", print_symbolScopeGlobal(o.scope)),
        ("ast", print_astStmtAdt(o.ast))
      ))
    }

    @pure def print_symbolTypeInfoTypeAlias(o: org.sireum.lang.symbol.TypeInfo.TypeAlias): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.symbol.TypeInfo.TypeAlias""""),
        ("name", printISZ(T, o.name, printString _)),
        ("scope", print_symbolScopeGlobal(o.scope)),
        ("ast", print_astStmtTypeAlias(o.ast))
      ))
    }

    @pure def print_symbolTypeInfoTypeVar(o: org.sireum.lang.symbol.TypeInfo.TypeVar): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.symbol.TypeInfo.TypeVar""""),
        ("id", printString(o.id)),
        ("ast", print_astTypeParam(o.ast))
      ))
    }

    @pure def print_symbolTypeInfoMembers(o: org.sireum.lang.symbol.TypeInfo.Members): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.symbol.TypeInfo.Members""""),
        ("specVars", printHashSMap(F, o.specVars, printString _, print_symbolInfoSpecVar _)),
        ("vars", printHashSMap(F, o.vars, printString _, print_symbolInfoVar _)),
        ("specMethods", printHashMap(F, o.specMethods, printString _, print_symbolInfoSpecMethod _)),
        ("methods", printHashMap(F, o.methods, printString _, print_symbolInfoMethod _)),
        ("refinements", printHashMap(F, o.refinements, printString _, print_symbolTypeInfoName _))
      ))
    }

    @pure def print_astTopUnit(o: org.sireum.lang.ast.TopUnit): ST = {
      o match {
        case o: org.sireum.lang.ast.TopUnit.Program => return print_astTopUnitProgram(o)
        case o: org.sireum.lang.ast.TopUnit.SequentUnit => return print_astTopUnitSequentUnit(o)
        case o: org.sireum.lang.ast.TopUnit.TruthTableUnit => return print_astTopUnitTruthTableUnit(o)
      }
    }

    @pure def print_astTopUnitProgram(o: org.sireum.lang.ast.TopUnit.Program): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.TopUnit.Program""""),
        ("fileUriOpt", printOption(T, o.fileUriOpt, printString _)),
        ("packageName", print_astName(o.packageName)),
        ("body", print_astBody(o.body))
      ))
    }

    @pure def print_astTopUnitSequentUnit(o: org.sireum.lang.ast.TopUnit.SequentUnit): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.TopUnit.SequentUnit""""),
        ("fileUriOpt", printOption(T, o.fileUriOpt, printString _)),
        ("sequent", print_astSequent(o.sequent))
      ))
    }

    @pure def print_astTopUnitTruthTableUnit(o: org.sireum.lang.ast.TopUnit.TruthTableUnit): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.TopUnit.TruthTableUnit""""),
        ("fileUriOpt", printOption(T, o.fileUriOpt, printString _)),
        ("stars", printISZ(F, o.stars, printPosition _)),
        ("vars", printISZ(F, o.vars, print_astId _)),
        ("separator", printPosition(o.separator)),
        ("isSequent", printB(o.isSequent)),
        ("sequent", print_astSequent(o.sequent)),
        ("rows", printISZ(F, o.rows, print_astTruthTableRow _)),
        ("conclusionOpt", printOption(F, o.conclusionOpt, print_astTruthTableConclusion _))
      ))
    }

    @pure def print_astStmt(o: org.sireum.lang.ast.Stmt): ST = {
      o match {
        case o: org.sireum.lang.ast.Stmt.Import => return print_astStmtImport(o)
        case o: org.sireum.lang.ast.Stmt.Var => return print_astStmtVar(o)
        case o: org.sireum.lang.ast.Stmt.VarPattern => return print_astStmtVarPattern(o)
        case o: org.sireum.lang.ast.Stmt.SpecVar => return print_astStmtSpecVar(o)
        case o: org.sireum.lang.ast.Stmt.Method => return print_astStmtMethod(o)
        case o: org.sireum.lang.ast.Stmt.ExtMethod => return print_astStmtExtMethod(o)
        case o: org.sireum.lang.ast.Stmt.SpecMethod => return print_astStmtSpecMethod(o)
        case o: org.sireum.lang.ast.Stmt.Enum => return print_astStmtEnum(o)
        case o: org.sireum.lang.ast.Stmt.SubZ => return print_astStmtSubZ(o)
        case o: org.sireum.lang.ast.Stmt.Object => return print_astStmtObject(o)
        case o: org.sireum.lang.ast.Stmt.Sig => return print_astStmtSig(o)
        case o: org.sireum.lang.ast.Stmt.Adt => return print_astStmtAdt(o)
        case o: org.sireum.lang.ast.Stmt.TypeAlias => return print_astStmtTypeAlias(o)
        case o: org.sireum.lang.ast.Stmt.Assign => return print_astStmtAssign(o)
        case o: org.sireum.lang.ast.Stmt.Block => return print_astStmtBlock(o)
        case o: org.sireum.lang.ast.Stmt.If => return print_astStmtIf(o)
        case o: org.sireum.lang.ast.Stmt.Match => return print_astStmtMatch(o)
        case o: org.sireum.lang.ast.Stmt.While => return print_astStmtWhile(o)
        case o: org.sireum.lang.ast.Stmt.DoWhile => return print_astStmtDoWhile(o)
        case o: org.sireum.lang.ast.Stmt.For => return print_astStmtFor(o)
        case o: org.sireum.lang.ast.Stmt.Return => return print_astStmtReturn(o)
        case o: org.sireum.lang.ast.Stmt.Expr => return print_astStmtExpr(o)
        case o: org.sireum.lang.ast.Stmt.Fact => return print_astStmtFact(o)
        case o: org.sireum.lang.ast.Stmt.Invariant => return print_astStmtInvariant(o)
        case o: org.sireum.lang.ast.Stmt.Theorem => return print_astStmtTheorem(o)
        case o: org.sireum.lang.ast.Stmt.SpecLabel => return print_astStmtSpecLabel(o)
        case o: org.sireum.lang.ast.Stmt.SpecBlock => return print_astStmtSpecBlock(o)
        case o: org.sireum.lang.ast.Stmt.Deduce => return print_astStmtDeduce(o)
        case o: org.sireum.lang.ast.Stmt.DeduceSteps => return print_astStmtDeduceSteps(o)
      }
    }

    @pure def print_astStmtImport(o: org.sireum.lang.ast.Stmt.Import): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.Import""""),
        ("importers", printISZ(F, o.importers, print_astStmtImportImporter _)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astStmtImportImporter(o: org.sireum.lang.ast.Stmt.Import.Importer): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.Import.Importer""""),
        ("name", print_astName(o.name)),
        ("selectorOpt", printOption(F, o.selectorOpt, print_astStmtImportSelector _))
      ))
    }

    @pure def print_astStmtImportSelector(o: org.sireum.lang.ast.Stmt.Import.Selector): ST = {
      o match {
        case o: org.sireum.lang.ast.Stmt.Import.MultiSelector => return print_astStmtImportMultiSelector(o)
        case o: org.sireum.lang.ast.Stmt.Import.WildcardSelector => return print_astStmtImportWildcardSelector(o)
      }
    }

    @pure def print_astStmtImportMultiSelector(o: org.sireum.lang.ast.Stmt.Import.MultiSelector): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.Import.MultiSelector""""),
        ("selectors", printISZ(F, o.selectors, print_astStmtImportNamedSelector _))
      ))
    }

    @pure def print_astStmtImportWildcardSelector(o: org.sireum.lang.ast.Stmt.Import.WildcardSelector): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.Import.WildcardSelector"""")
      ))
    }

    @pure def print_astStmtImportNamedSelector(o: org.sireum.lang.ast.Stmt.Import.NamedSelector): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.Import.NamedSelector""""),
        ("from", print_astId(o.from)),
        ("to", print_astId(o.to))
      ))
    }

    @pure def print_astStmtVar(o: org.sireum.lang.ast.Stmt.Var): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.Var""""),
        ("isVal", printB(o.isVal)),
        ("id", print_astId(o.id)),
        ("tipeOpt", printOption(F, o.tipeOpt, print_astType _)),
        ("initOpt", printOption(F, o.initOpt, print_astAssignExp _)),
        ("attr", print_astResolvedAttr(o.attr))
      ))
    }

    @pure def print_astStmtVarPattern(o: org.sireum.lang.ast.Stmt.VarPattern): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.VarPattern""""),
        ("isVal", printB(o.isVal)),
        ("pattern", print_astPattern(o.pattern)),
        ("tipeOpt", printOption(F, o.tipeOpt, print_astType _)),
        ("init", print_astAssignExp(o.init)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astStmtSpecVar(o: org.sireum.lang.ast.Stmt.SpecVar): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.SpecVar""""),
        ("isVal", printB(o.isVal)),
        ("id", print_astId(o.id)),
        ("tipe", print_astType(o.tipe)),
        ("attr", print_astResolvedAttr(o.attr))
      ))
    }

    @pure def print_astStmtMethod(o: org.sireum.lang.ast.Stmt.Method): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.Method""""),
        ("purity", print_astPurityType(o.purity)),
        ("hasOverride", printB(o.hasOverride)),
        ("isHelper", printB(o.isHelper)),
        ("sig", print_astMethodSig(o.sig)),
        ("contract", print_astMethodContract(o.contract)),
        ("bodyOpt", printOption(F, o.bodyOpt, print_astBody _)),
        ("attr", print_astResolvedAttr(o.attr))
      ))
    }

    @pure def print_astStmtExtMethod(o: org.sireum.lang.ast.Stmt.ExtMethod): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.ExtMethod""""),
        ("isPure", printB(o.isPure)),
        ("sig", print_astMethodSig(o.sig)),
        ("contract", print_astMethodContract(o.contract)),
        ("attr", print_astResolvedAttr(o.attr))
      ))
    }

    @pure def print_astStmtSpecMethod(o: org.sireum.lang.ast.Stmt.SpecMethod): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.SpecMethod""""),
        ("sig", print_astMethodSig(o.sig)),
        ("attr", print_astResolvedAttr(o.attr))
      ))
    }

    @pure def print_astStmtEnum(o: org.sireum.lang.ast.Stmt.Enum): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.Enum""""),
        ("id", print_astId(o.id)),
        ("elements", printISZ(F, o.elements, print_astId _)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astStmtSubZ(o: org.sireum.lang.ast.Stmt.SubZ): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.SubZ""""),
        ("id", print_astId(o.id)),
        ("isSigned", printB(o.isSigned)),
        ("isBitVector", printB(o.isBitVector)),
        ("isWrapped", printB(o.isWrapped)),
        ("hasMin", printB(o.hasMin)),
        ("hasMax", printB(o.hasMax)),
        ("bitWidth", printZ(o.bitWidth)),
        ("min", printZ(o.min)),
        ("max", printZ(o.max)),
        ("index", printZ(o.index)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astStmtObject(o: org.sireum.lang.ast.Stmt.Object): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.Object""""),
        ("isApp", printB(o.isApp)),
        ("extNameOpt", printOption(T, o.extNameOpt, printString _)),
        ("id", print_astId(o.id)),
        ("stmts", printISZ(F, o.stmts, print_astStmt _)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astStmtSig(o: org.sireum.lang.ast.Stmt.Sig): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.Sig""""),
        ("isImmutable", printB(o.isImmutable)),
        ("isExt", printB(o.isExt)),
        ("id", print_astId(o.id)),
        ("typeParams", printISZ(F, o.typeParams, print_astTypeParam _)),
        ("parents", printISZ(F, o.parents, print_astTypeNamed _)),
        ("stmts", printISZ(F, o.stmts, print_astStmt _)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astStmtAdt(o: org.sireum.lang.ast.Stmt.Adt): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.Adt""""),
        ("isRoot", printB(o.isRoot)),
        ("isDatatype", printB(o.isDatatype)),
        ("id", print_astId(o.id)),
        ("typeParams", printISZ(F, o.typeParams, print_astTypeParam _)),
        ("params", printISZ(F, o.params, print_astAdtParam _)),
        ("parents", printISZ(F, o.parents, print_astTypeNamed _)),
        ("stmts", printISZ(F, o.stmts, print_astStmt _)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astStmtTypeAlias(o: org.sireum.lang.ast.Stmt.TypeAlias): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.TypeAlias""""),
        ("id", print_astId(o.id)),
        ("typeParams", printISZ(F, o.typeParams, print_astTypeParam _)),
        ("tipe", print_astType(o.tipe)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astStmtAssign(o: org.sireum.lang.ast.Stmt.Assign): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.Assign""""),
        ("lhs", print_astExp(o.lhs)),
        ("rhs", print_astAssignExp(o.rhs)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astStmtBlock(o: org.sireum.lang.ast.Stmt.Block): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.Block""""),
        ("body", print_astBody(o.body)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astStmtIf(o: org.sireum.lang.ast.Stmt.If): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.If""""),
        ("cond", print_astExp(o.cond)),
        ("thenBody", print_astBody(o.thenBody)),
        ("elseBody", print_astBody(o.elseBody)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astStmtMatch(o: org.sireum.lang.ast.Stmt.Match): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.Match""""),
        ("exp", print_astExp(o.exp)),
        ("cases", printISZ(F, o.cases, print_astCase _)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astStmtLoop(o: org.sireum.lang.ast.Stmt.Loop): ST = {
      o match {
        case o: org.sireum.lang.ast.Stmt.While => return print_astStmtWhile(o)
        case o: org.sireum.lang.ast.Stmt.DoWhile => return print_astStmtDoWhile(o)
        case o: org.sireum.lang.ast.Stmt.For => return print_astStmtFor(o)
      }
    }

    @pure def print_astStmtWhile(o: org.sireum.lang.ast.Stmt.While): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.While""""),
        ("context", printISZ(T, o.context, printString _)),
        ("cond", print_astExp(o.cond)),
        ("invariants", printISZ(F, o.invariants, print_astExp _)),
        ("modifies", printISZ(F, o.modifies, print_astExpIdent _)),
        ("body", print_astBody(o.body)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astStmtDoWhile(o: org.sireum.lang.ast.Stmt.DoWhile): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.DoWhile""""),
        ("context", printISZ(T, o.context, printString _)),
        ("cond", print_astExp(o.cond)),
        ("invariants", printISZ(F, o.invariants, print_astExp _)),
        ("modifies", printISZ(F, o.modifies, print_astExpIdent _)),
        ("body", print_astBody(o.body)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astStmtFor(o: org.sireum.lang.ast.Stmt.For): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.For""""),
        ("context", printISZ(T, o.context, printString _)),
        ("enumGens", printISZ(F, o.enumGens, print_astEnumGenFor _)),
        ("invariants", printISZ(F, o.invariants, print_astExp _)),
        ("modifies", printISZ(F, o.modifies, print_astExpIdent _)),
        ("body", print_astBody(o.body)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astStmtReturn(o: org.sireum.lang.ast.Stmt.Return): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.Return""""),
        ("expOpt", printOption(F, o.expOpt, print_astExp _)),
        ("attr", print_astTypedAttr(o.attr))
      ))
    }

    @pure def print_astStmtExpr(o: org.sireum.lang.ast.Stmt.Expr): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.Expr""""),
        ("exp", print_astExp(o.exp)),
        ("attr", print_astTypedAttr(o.attr))
      ))
    }

    @pure def print_astStmtSpec(o: org.sireum.lang.ast.Stmt.Spec): ST = {
      o match {
        case o: org.sireum.lang.ast.Stmt.Fact => return print_astStmtFact(o)
        case o: org.sireum.lang.ast.Stmt.Invariant => return print_astStmtInvariant(o)
        case o: org.sireum.lang.ast.Stmt.Theorem => return print_astStmtTheorem(o)
        case o: org.sireum.lang.ast.Stmt.SpecLabel => return print_astStmtSpecLabel(o)
        case o: org.sireum.lang.ast.Stmt.SpecBlock => return print_astStmtSpecBlock(o)
        case o: org.sireum.lang.ast.Stmt.Deduce => return print_astStmtDeduce(o)
        case o: org.sireum.lang.ast.Stmt.DeduceSteps => return print_astStmtDeduceSteps(o)
      }
    }

    @pure def print_astStmtFact(o: org.sireum.lang.ast.Stmt.Fact): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.Fact""""),
        ("id", print_astId(o.id)),
        ("typeArgs", printISZ(F, o.typeArgs, print_astTypeParam _)),
        ("descOpt", printOption(F, o.descOpt, print_astExpLitString _)),
        ("claims", printISZ(F, o.claims, print_astExp _)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astStmtInvariant(o: org.sireum.lang.ast.Stmt.Invariant): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.Invariant""""),
        ("id", print_astId(o.id)),
        ("claims", printISZ(F, o.claims, print_astExp _)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astStmtTheorem(o: org.sireum.lang.ast.Stmt.Theorem): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.Theorem""""),
        ("isLemma", printB(o.isLemma)),
        ("id", print_astId(o.id)),
        ("typeArgs", printISZ(F, o.typeArgs, print_astTypeParam _)),
        ("descOpt", printOption(F, o.descOpt, print_astExpLitString _)),
        ("claim", print_astExp(o.claim)),
        ("proof", print_astProof(o.proof)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astStmtSpecLabel(o: org.sireum.lang.ast.Stmt.SpecLabel): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.SpecLabel""""),
        ("id", print_astId(o.id))
      ))
    }

    @pure def print_astStmtSpecBlock(o: org.sireum.lang.ast.Stmt.SpecBlock): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.SpecBlock""""),
        ("block", print_astStmtBlock(o.block))
      ))
    }

    @pure def print_astStmtDeduce(o: org.sireum.lang.ast.Stmt.Deduce): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.Deduce""""),
        ("sequents", printISZ(F, o.sequents, print_astSequent _)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astStmtDeduceSteps(o: org.sireum.lang.ast.Stmt.DeduceSteps): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Stmt.DeduceSteps""""),
        ("steps", printISZ(F, o.steps, print_astProofStep _)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astMethodContract(o: org.sireum.lang.ast.MethodContract): ST = {
      o match {
        case o: org.sireum.lang.ast.MethodContract.Simple => return print_astMethodContractSimple(o)
        case o: org.sireum.lang.ast.MethodContract.Cases => return print_astMethodContractCases(o)
      }
    }

    @pure def print_astMethodContractSimple(o: org.sireum.lang.ast.MethodContract.Simple): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.MethodContract.Simple""""),
        ("reads", printISZ(F, o.reads, print_astExpIdent _)),
        ("requires", printISZ(F, o.requires, print_astExp _)),
        ("modifies", printISZ(F, o.modifies, print_astExpIdent _)),
        ("ensures", printISZ(F, o.ensures, print_astExp _))
      ))
    }

    @pure def print_astMethodContractCases(o: org.sireum.lang.ast.MethodContract.Cases): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.MethodContract.Cases""""),
        ("reads", printISZ(F, o.reads, print_astExpIdent _)),
        ("modifies", printISZ(F, o.modifies, print_astExpIdent _)),
        ("cases", printISZ(F, o.cases, print_astMethodContractCase _))
      ))
    }

    @pure def print_astMethodContractCase(o: org.sireum.lang.ast.MethodContract.Case): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.MethodContract.Case""""),
        ("label", printString(o.label)),
        ("requires", printISZ(F, o.requires, print_astExp _)),
        ("ensures", printISZ(F, o.ensures, print_astExp _))
      ))
    }

    @pure def print_astSequent(o: org.sireum.lang.ast.Sequent): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Sequent""""),
        ("premises", printISZ(F, o.premises, print_astExp _)),
        ("conclusion", print_astExp(o.conclusion)),
        ("steps", printISZ(F, o.steps, print_astProofStep _)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astProof(o: org.sireum.lang.ast.Proof): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Proof""""),
        ("steps", printISZ(F, o.steps, print_astProofStep _)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astProofStep(o: org.sireum.lang.ast.Proof.Step): ST = {
      o match {
        case o: org.sireum.lang.ast.Proof.Step.Regular => return print_astProofStepRegular(o)
        case o: org.sireum.lang.ast.Proof.Step.Assume => return print_astProofStepAssume(o)
        case o: org.sireum.lang.ast.Proof.Step.Assert => return print_astProofStepAssert(o)
        case o: org.sireum.lang.ast.Proof.Step.SubProof => return print_astProofStepSubProof(o)
        case o: org.sireum.lang.ast.Proof.Step.Let => return print_astProofStepLet(o)
        case o: org.sireum.lang.ast.Proof.Step.StructInduction => return print_astProofStepStructInduction(o)
      }
    }

    @pure def print_astProofStepRegular(o: org.sireum.lang.ast.Proof.Step.Regular): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Proof.Step.Regular""""),
        ("no", print_astExpLitZ(o.no)),
        ("claim", print_astExp(o.claim)),
        ("just", print_astProofStepJustification(o.just))
      ))
    }

    @pure def print_astProofStepAssume(o: org.sireum.lang.ast.Proof.Step.Assume): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Proof.Step.Assume""""),
        ("no", print_astExpLitZ(o.no)),
        ("claim", print_astExp(o.claim))
      ))
    }

    @pure def print_astProofStepAssert(o: org.sireum.lang.ast.Proof.Step.Assert): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Proof.Step.Assert""""),
        ("no", print_astExpLitZ(o.no)),
        ("claim", print_astExp(o.claim)),
        ("steps", printISZ(F, o.steps, print_astProofStep _))
      ))
    }

    @pure def print_astProofStepSubProof(o: org.sireum.lang.ast.Proof.Step.SubProof): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Proof.Step.SubProof""""),
        ("no", print_astExpLitZ(o.no)),
        ("steps", printISZ(F, o.steps, print_astProofStep _))
      ))
    }

    @pure def print_astProofStepLet(o: org.sireum.lang.ast.Proof.Step.Let): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Proof.Step.Let""""),
        ("no", print_astExpLitZ(o.no)),
        ("params", printISZ(F, o.params, print_astProofStepLetParam _)),
        ("steps", printISZ(F, o.steps, print_astProofStep _))
      ))
    }

    @pure def print_astProofStepLetParam(o: org.sireum.lang.ast.Proof.Step.Let.Param): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Proof.Step.Let.Param""""),
        ("id", print_astId(o.id)),
        ("tipeOpt", printOption(F, o.tipeOpt, print_astType _))
      ))
    }

    @pure def print_astProofStepStructInduction(o: org.sireum.lang.ast.Proof.Step.StructInduction): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Proof.Step.StructInduction""""),
        ("no", print_astExpLitZ(o.no)),
        ("claim", print_astExp(o.claim)),
        ("exp", print_astExp(o.exp)),
        ("cases", printISZ(F, o.cases, print_astProofStepStructInductionMatchCase _)),
        ("defaultOpt", printOption(F, o.defaultOpt, print_astProofStepStructInductionMatchDefault _))
      ))
    }

    @pure def print_astProofStepStructInductionMatchCase(o: org.sireum.lang.ast.Proof.Step.StructInduction.MatchCase): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Proof.Step.StructInduction.MatchCase""""),
        ("pattern", print_astPatternStructure(o.pattern)),
        ("hypoOpt", printOption(F, o.hypoOpt, print_astProofStepAssume _)),
        ("steps", printISZ(F, o.steps, print_astProofStep _))
      ))
    }

    @pure def print_astProofStepStructInductionMatchDefault(o: org.sireum.lang.ast.Proof.Step.StructInduction.MatchDefault): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Proof.Step.StructInduction.MatchDefault""""),
        ("hypoOpt", printOption(F, o.hypoOpt, print_astProofStepAssume _)),
        ("steps", printISZ(F, o.steps, print_astProofStep _))
      ))
    }

    @pure def print_astProofStepJustification(o: org.sireum.lang.ast.Proof.Step.Justification): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Proof.Step.Justification""""),
        ("id", print_astId(o.id)),
        ("args", printISZ(F, o.args, print_astExp _))
      ))
    }

    @pure def print_astAssignExp(o: org.sireum.lang.ast.AssignExp): ST = {
      o match {
        case o: org.sireum.lang.ast.Stmt.Block => return print_astStmtBlock(o)
        case o: org.sireum.lang.ast.Stmt.If => return print_astStmtIf(o)
        case o: org.sireum.lang.ast.Stmt.Match => return print_astStmtMatch(o)
        case o: org.sireum.lang.ast.Stmt.Return => return print_astStmtReturn(o)
        case o: org.sireum.lang.ast.Stmt.Expr => return print_astStmtExpr(o)
      }
    }

    @pure def print_astPurityType(o: org.sireum.lang.ast.Purity.Type): ST = {
      val value: String = o match {
        case org.sireum.lang.ast.Purity.Impure => "Impure"
        case org.sireum.lang.ast.Purity.Pure => "Pure"
        case org.sireum.lang.ast.Purity.Memoize => "Memoize"
        case org.sireum.lang.ast.Purity.StrictPure => "StrictPure"
      }
      return printObject(ISZ(
        ("type", printString("org.sireum.lang.ast.Purity")),
        ("value", printString(value))
      ))
    }

    @pure def print_astCase(o: org.sireum.lang.ast.Case): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Case""""),
        ("pattern", print_astPattern(o.pattern)),
        ("condOpt", printOption(F, o.condOpt, print_astExp _)),
        ("body", print_astBody(o.body))
      ))
    }

    @pure def print_astEnumGenRange(o: org.sireum.lang.ast.EnumGen.Range): ST = {
      o match {
        case o: org.sireum.lang.ast.EnumGen.Range.Expr => return print_astEnumGenRangeExpr(o)
        case o: org.sireum.lang.ast.EnumGen.Range.Step => return print_astEnumGenRangeStep(o)
      }
    }

    @pure def print_astEnumGenRangeExpr(o: org.sireum.lang.ast.EnumGen.Range.Expr): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.EnumGen.Range.Expr""""),
        ("exp", print_astExp(o.exp)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astEnumGenRangeStep(o: org.sireum.lang.ast.EnumGen.Range.Step): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.EnumGen.Range.Step""""),
        ("isInclusive", printB(o.isInclusive)),
        ("start", print_astExp(o.start)),
        ("end", print_astExp(o.end)),
        ("byOpt", printOption(F, o.byOpt, print_astExp _)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astEnumGenFor(o: org.sireum.lang.ast.EnumGen.For): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.EnumGen.For""""),
        ("idOpt", printOption(F, o.idOpt, print_astId _)),
        ("range", print_astEnumGenRange(o.range)),
        ("condOpt", printOption(F, o.condOpt, print_astExp _))
      ))
    }

    @pure def print_astType(o: org.sireum.lang.ast.Type): ST = {
      o match {
        case o: org.sireum.lang.ast.Type.Named => return print_astTypeNamed(o)
        case o: org.sireum.lang.ast.Type.Fun => return print_astTypeFun(o)
        case o: org.sireum.lang.ast.Type.Tuple => return print_astTypeTuple(o)
      }
    }

    @pure def print_astTypeNamed(o: org.sireum.lang.ast.Type.Named): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Type.Named""""),
        ("name", print_astName(o.name)),
        ("typeArgs", printISZ(F, o.typeArgs, print_astType _)),
        ("attr", print_astTypedAttr(o.attr))
      ))
    }

    @pure def print_astTypeFun(o: org.sireum.lang.ast.Type.Fun): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Type.Fun""""),
        ("isPure", printB(o.isPure)),
        ("isByName", printB(o.isByName)),
        ("args", printISZ(F, o.args, print_astType _)),
        ("ret", print_astType(o.ret)),
        ("attr", print_astTypedAttr(o.attr))
      ))
    }

    @pure def print_astTypeTuple(o: org.sireum.lang.ast.Type.Tuple): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Type.Tuple""""),
        ("args", printISZ(F, o.args, print_astType _)),
        ("attr", print_astTypedAttr(o.attr))
      ))
    }

    @pure def print_astPattern(o: org.sireum.lang.ast.Pattern): ST = {
      o match {
        case o: org.sireum.lang.ast.Pattern.Literal => return print_astPatternLiteral(o)
        case o: org.sireum.lang.ast.Pattern.LitInterpolate => return print_astPatternLitInterpolate(o)
        case o: org.sireum.lang.ast.Pattern.Ref => return print_astPatternRef(o)
        case o: org.sireum.lang.ast.Pattern.VarBinding => return print_astPatternVarBinding(o)
        case o: org.sireum.lang.ast.Pattern.Wildcard => return print_astPatternWildcard(o)
        case o: org.sireum.lang.ast.Pattern.SeqWildcard => return print_astPatternSeqWildcard(o)
        case o: org.sireum.lang.ast.Pattern.Structure => return print_astPatternStructure(o)
      }
    }

    @pure def print_astPatternLiteral(o: org.sireum.lang.ast.Pattern.Literal): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Pattern.Literal""""),
        ("lit", print_astLit(o.lit))
      ))
    }

    @pure def print_astPatternLitInterpolate(o: org.sireum.lang.ast.Pattern.LitInterpolate): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Pattern.LitInterpolate""""),
        ("prefix", printString(o.prefix)),
        ("value", printString(o.value)),
        ("attr", print_astTypedAttr(o.attr))
      ))
    }

    @pure def print_astPatternRef(o: org.sireum.lang.ast.Pattern.Ref): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Pattern.Ref""""),
        ("name", print_astName(o.name)),
        ("attr", print_astResolvedAttr(o.attr))
      ))
    }

    @pure def print_astPatternVarBinding(o: org.sireum.lang.ast.Pattern.VarBinding): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Pattern.VarBinding""""),
        ("id", print_astId(o.id)),
        ("tipeOpt", printOption(F, o.tipeOpt, print_astType _)),
        ("attr", print_astTypedAttr(o.attr))
      ))
    }

    @pure def print_astPatternWildcard(o: org.sireum.lang.ast.Pattern.Wildcard): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Pattern.Wildcard""""),
        ("typeOpt", printOption(F, o.typeOpt, print_astType _)),
        ("attr", print_astTypedAttr(o.attr))
      ))
    }

    @pure def print_astPatternSeqWildcard(o: org.sireum.lang.ast.Pattern.SeqWildcard): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Pattern.SeqWildcard""""),
        ("attr", print_astTypedAttr(o.attr))
      ))
    }

    @pure def print_astPatternStructure(o: org.sireum.lang.ast.Pattern.Structure): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Pattern.Structure""""),
        ("idOpt", printOption(F, o.idOpt, print_astId _)),
        ("nameOpt", printOption(F, o.nameOpt, print_astName _)),
        ("patterns", printISZ(F, o.patterns, print_astPattern _)),
        ("attr", print_astResolvedAttr(o.attr))
      ))
    }

    @pure def print_astExp(o: org.sireum.lang.ast.Exp): ST = {
      o match {
        case o: org.sireum.lang.ast.Exp.LitB => return print_astExpLitB(o)
        case o: org.sireum.lang.ast.Exp.LitC => return print_astExpLitC(o)
        case o: org.sireum.lang.ast.Exp.LitZ => return print_astExpLitZ(o)
        case o: org.sireum.lang.ast.Exp.LitF32 => return print_astExpLitF32(o)
        case o: org.sireum.lang.ast.Exp.LitF64 => return print_astExpLitF64(o)
        case o: org.sireum.lang.ast.Exp.LitR => return print_astExpLitR(o)
        case o: org.sireum.lang.ast.Exp.LitString => return print_astExpLitString(o)
        case o: org.sireum.lang.ast.Exp.StringInterpolate => return print_astExpStringInterpolate(o)
        case o: org.sireum.lang.ast.Exp.This => return print_astExpThis(o)
        case o: org.sireum.lang.ast.Exp.Super => return print_astExpSuper(o)
        case o: org.sireum.lang.ast.Exp.Unary => return print_astExpUnary(o)
        case o: org.sireum.lang.ast.Exp.Binary => return print_astExpBinary(o)
        case o: org.sireum.lang.ast.Exp.Ident => return print_astExpIdent(o)
        case o: org.sireum.lang.ast.Exp.Eta => return print_astExpEta(o)
        case o: org.sireum.lang.ast.Exp.Tuple => return print_astExpTuple(o)
        case o: org.sireum.lang.ast.Exp.Select => return print_astExpSelect(o)
        case o: org.sireum.lang.ast.Exp.Invoke => return print_astExpInvoke(o)
        case o: org.sireum.lang.ast.Exp.InvokeNamed => return print_astExpInvokeNamed(o)
        case o: org.sireum.lang.ast.Exp.If => return print_astExpIf(o)
        case o: org.sireum.lang.ast.Exp.Fun => return print_astExpFun(o)
        case o: org.sireum.lang.ast.Exp.ForYield => return print_astExpForYield(o)
        case o: org.sireum.lang.ast.Exp.Quant => return print_astExpQuant(o)
      }
    }

    @pure def print_astLit(o: org.sireum.lang.ast.Lit): ST = {
      o match {
        case o: org.sireum.lang.ast.Exp.LitB => return print_astExpLitB(o)
        case o: org.sireum.lang.ast.Exp.LitC => return print_astExpLitC(o)
        case o: org.sireum.lang.ast.Exp.LitZ => return print_astExpLitZ(o)
        case o: org.sireum.lang.ast.Exp.LitF32 => return print_astExpLitF32(o)
        case o: org.sireum.lang.ast.Exp.LitF64 => return print_astExpLitF64(o)
        case o: org.sireum.lang.ast.Exp.LitR => return print_astExpLitR(o)
        case o: org.sireum.lang.ast.Exp.LitString => return print_astExpLitString(o)
      }
    }

    @pure def print_astExpLitB(o: org.sireum.lang.ast.Exp.LitB): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Exp.LitB""""),
        ("value", printB(o.value)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astExpLitC(o: org.sireum.lang.ast.Exp.LitC): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Exp.LitC""""),
        ("value", printC(o.value)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astExpLitZ(o: org.sireum.lang.ast.Exp.LitZ): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Exp.LitZ""""),
        ("value", printZ(o.value)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astExpLitF32(o: org.sireum.lang.ast.Exp.LitF32): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Exp.LitF32""""),
        ("value", printF32(o.value)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astExpLitF64(o: org.sireum.lang.ast.Exp.LitF64): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Exp.LitF64""""),
        ("value", printF64(o.value)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astExpLitR(o: org.sireum.lang.ast.Exp.LitR): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Exp.LitR""""),
        ("value", printR(o.value)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astExpLitString(o: org.sireum.lang.ast.Exp.LitString): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Exp.LitString""""),
        ("value", printString(o.value)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astExpStringInterpolate(o: org.sireum.lang.ast.Exp.StringInterpolate): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Exp.StringInterpolate""""),
        ("prefix", printString(o.prefix)),
        ("lits", printISZ(F, o.lits, print_astExpLitString _)),
        ("args", printISZ(F, o.args, print_astExp _)),
        ("attr", print_astTypedAttr(o.attr))
      ))
    }

    @pure def print_astExpThis(o: org.sireum.lang.ast.Exp.This): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Exp.This""""),
        ("attr", print_astTypedAttr(o.attr))
      ))
    }

    @pure def print_astExpSuper(o: org.sireum.lang.ast.Exp.Super): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Exp.Super""""),
        ("idOpt", printOption(F, o.idOpt, print_astId _)),
        ("attr", print_astTypedAttr(o.attr))
      ))
    }

    @pure def print_astExpUnaryOpType(o: org.sireum.lang.ast.Exp.UnaryOp.Type): ST = {
      val value: String = o match {
        case org.sireum.lang.ast.Exp.UnaryOp.Not => "Not"
        case org.sireum.lang.ast.Exp.UnaryOp.Plus => "Plus"
        case org.sireum.lang.ast.Exp.UnaryOp.Minus => "Minus"
        case org.sireum.lang.ast.Exp.UnaryOp.Complement => "Complement"
      }
      return printObject(ISZ(
        ("type", printString("org.sireum.lang.ast.Exp.UnaryOp")),
        ("value", printString(value))
      ))
    }

    @pure def print_astExpUnary(o: org.sireum.lang.ast.Exp.Unary): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Exp.Unary""""),
        ("op", print_astExpUnaryOpType(o.op)),
        ("exp", print_astExp(o.exp)),
        ("attr", print_astResolvedAttr(o.attr))
      ))
    }

    @pure def print_astExpRef(o: org.sireum.lang.ast.Exp.Ref): ST = {
      o match {
        case o: org.sireum.lang.ast.Exp.Ident => return print_astExpIdent(o)
        case o: org.sireum.lang.ast.Exp.Select => return print_astExpSelect(o)
      }
    }

    @pure def print_astExpBinary(o: org.sireum.lang.ast.Exp.Binary): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Exp.Binary""""),
        ("left", print_astExp(o.left)),
        ("op", printString(o.op)),
        ("right", print_astExp(o.right)),
        ("attr", print_astResolvedAttr(o.attr))
      ))
    }

    @pure def print_astExpIdent(o: org.sireum.lang.ast.Exp.Ident): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Exp.Ident""""),
        ("id", print_astId(o.id)),
        ("attr", print_astResolvedAttr(o.attr))
      ))
    }

    @pure def print_astExpEta(o: org.sireum.lang.ast.Exp.Eta): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Exp.Eta""""),
        ("ref", print_astExpRef(o.ref)),
        ("attr", print_astTypedAttr(o.attr))
      ))
    }

    @pure def print_astExpTuple(o: org.sireum.lang.ast.Exp.Tuple): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Exp.Tuple""""),
        ("args", printISZ(F, o.args, print_astExp _)),
        ("attr", print_astTypedAttr(o.attr))
      ))
    }

    @pure def print_astExpSelect(o: org.sireum.lang.ast.Exp.Select): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Exp.Select""""),
        ("receiverOpt", printOption(F, o.receiverOpt, print_astExp _)),
        ("id", print_astId(o.id)),
        ("targs", printISZ(F, o.targs, print_astType _)),
        ("attr", print_astResolvedAttr(o.attr))
      ))
    }

    @pure def print_astExpInvoke(o: org.sireum.lang.ast.Exp.Invoke): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Exp.Invoke""""),
        ("receiverOpt", printOption(F, o.receiverOpt, print_astExp _)),
        ("ident", print_astExpIdent(o.ident)),
        ("targs", printISZ(F, o.targs, print_astType _)),
        ("args", printISZ(F, o.args, print_astExp _)),
        ("attr", print_astResolvedAttr(o.attr))
      ))
    }

    @pure def print_astExpInvokeNamed(o: org.sireum.lang.ast.Exp.InvokeNamed): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Exp.InvokeNamed""""),
        ("receiverOpt", printOption(F, o.receiverOpt, print_astExp _)),
        ("ident", print_astExpIdent(o.ident)),
        ("targs", printISZ(F, o.targs, print_astType _)),
        ("args", printISZ(F, o.args, print_astNamedArg _)),
        ("attr", print_astResolvedAttr(o.attr))
      ))
    }

    @pure def print_astExpIf(o: org.sireum.lang.ast.Exp.If): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Exp.If""""),
        ("cond", print_astExp(o.cond)),
        ("thenExp", print_astExp(o.thenExp)),
        ("elseExp", print_astExp(o.elseExp)),
        ("attr", print_astTypedAttr(o.attr))
      ))
    }

    @pure def print_astExpFunParam(o: org.sireum.lang.ast.Exp.Fun.Param): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Exp.Fun.Param""""),
        ("id", print_astId(o.id)),
        ("tipeOpt", printOption(F, o.tipeOpt, print_astType _)),
        ("typedOpt", printOption(F, o.typedOpt, print_astTyped _))
      ))
    }

    @pure def print_astExpFun(o: org.sireum.lang.ast.Exp.Fun): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Exp.Fun""""),
        ("context", printISZ(T, o.context, printString _)),
        ("params", printISZ(F, o.params, print_astExpFunParam _)),
        ("exp", print_astAssignExp(o.exp)),
        ("attr", print_astTypedAttr(o.attr))
      ))
    }

    @pure def print_astExpForYield(o: org.sireum.lang.ast.Exp.ForYield): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Exp.ForYield""""),
        ("enumGens", printISZ(F, o.enumGens, print_astEnumGenFor _)),
        ("exp", print_astExp(o.exp)),
        ("attr", print_astTypedAttr(o.attr))
      ))
    }

    @pure def print_astExpSpec(o: org.sireum.lang.ast.Exp.Spec): ST = {
      o match {
        case o: org.sireum.lang.ast.Exp.Quant => return print_astExpQuant(o)
      }
    }

    @pure def print_astExpQuant(o: org.sireum.lang.ast.Exp.Quant): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Exp.Quant""""),
        ("isForall", printB(o.isForall)),
        ("varFragments", printISZ(F, o.varFragments, print_astVarFragment _)),
        ("exp", print_astExp(o.exp)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astNamedArg(o: org.sireum.lang.ast.NamedArg): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.NamedArg""""),
        ("id", print_astId(o.id)),
        ("arg", print_astExp(o.arg)),
        ("index", printZ(o.index))
      ))
    }

    @pure def print_astVarFragment(o: org.sireum.lang.ast.VarFragment): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.VarFragment""""),
        ("id", print_astId(o.id)),
        ("domainOpt", printOption(F, o.domainOpt, print_astDomain _))
      ))
    }

    @pure def print_astDomain(o: org.sireum.lang.ast.Domain): ST = {
      o match {
        case o: org.sireum.lang.ast.Domain.Type => return print_astDomainType(o)
        case o: org.sireum.lang.ast.Domain.Range => return print_astDomainRange(o)
        case o: org.sireum.lang.ast.Domain.Each => return print_astDomainEach(o)
      }
    }

    @pure def print_astDomainType(o: org.sireum.lang.ast.Domain.Type): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Domain.Type""""),
        ("tipe", print_astType(o.tipe)),
        ("attr", print_astTypedAttr(o.attr))
      ))
    }

    @pure def print_astDomainRange(o: org.sireum.lang.ast.Domain.Range): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Domain.Range""""),
        ("lo", print_astExp(o.lo)),
        ("hi", print_astExp(o.hi)),
        ("hiExact", printB(o.hiExact)),
        ("attr", print_astTypedAttr(o.attr))
      ))
    }

    @pure def print_astDomainEach(o: org.sireum.lang.ast.Domain.Each): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Domain.Each""""),
        ("exp", print_astExp(o.exp)),
        ("attr", print_astTypedAttr(o.attr))
      ))
    }

    @pure def print_astId(o: org.sireum.lang.ast.Id): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Id""""),
        ("value", printString(o.value)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astName(o: org.sireum.lang.ast.Name): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Name""""),
        ("ids", printISZ(F, o.ids, print_astId _)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astBody(o: org.sireum.lang.ast.Body): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Body""""),
        ("stmts", printISZ(F, o.stmts, print_astStmt _)),
        ("undecls", printISZ(F, o.undecls, print_astResolvedInfoLocalVar _))
      ))
    }

    @pure def print_astAdtParam(o: org.sireum.lang.ast.AdtParam): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.AdtParam""""),
        ("isHidden", printB(o.isHidden)),
        ("isVal", printB(o.isVal)),
        ("id", print_astId(o.id)),
        ("tipe", print_astType(o.tipe))
      ))
    }

    @pure def print_astMethodSig(o: org.sireum.lang.ast.MethodSig): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.MethodSig""""),
        ("isPure", printB(o.isPure)),
        ("id", print_astId(o.id)),
        ("typeParams", printISZ(F, o.typeParams, print_astTypeParam _)),
        ("hasParams", printB(o.hasParams)),
        ("params", printISZ(F, o.params, print_astParam _)),
        ("returnType", print_astType(o.returnType))
      ))
    }

    @pure def print_astParam(o: org.sireum.lang.ast.Param): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Param""""),
        ("isHidden", printB(o.isHidden)),
        ("id", print_astId(o.id)),
        ("tipe", print_astType(o.tipe))
      ))
    }

    @pure def print_astTypeParam(o: org.sireum.lang.ast.TypeParam): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.TypeParam""""),
        ("id", print_astId(o.id))
      ))
    }

    @pure def print_astTyped(o: org.sireum.lang.ast.Typed): ST = {
      o match {
        case o: org.sireum.lang.ast.Typed.Name => return print_astTypedName(o)
        case o: org.sireum.lang.ast.Typed.Tuple => return print_astTypedTuple(o)
        case o: org.sireum.lang.ast.Typed.Fun => return print_astTypedFun(o)
        case o: org.sireum.lang.ast.Typed.TypeVar => return print_astTypedTypeVar(o)
        case o: org.sireum.lang.ast.Typed.Package => return print_astTypedPackage(o)
        case o: org.sireum.lang.ast.Typed.Object => return print_astTypedObject(o)
        case o: org.sireum.lang.ast.Typed.Enum => return print_astTypedEnum(o)
        case o: org.sireum.lang.ast.Typed.Method => return print_astTypedMethod(o)
        case o: org.sireum.lang.ast.Typed.Methods => return print_astTypedMethods(o)
      }
    }

    @pure def print_astMethodModeType(o: org.sireum.lang.ast.MethodMode.Type): ST = {
      val value: String = o match {
        case org.sireum.lang.ast.MethodMode.Method => "Method"
        case org.sireum.lang.ast.MethodMode.Spec => "Spec"
        case org.sireum.lang.ast.MethodMode.Ext => "Ext"
        case org.sireum.lang.ast.MethodMode.Constructor => "Constructor"
        case org.sireum.lang.ast.MethodMode.Copy => "Copy"
        case org.sireum.lang.ast.MethodMode.Extractor => "Extractor"
        case org.sireum.lang.ast.MethodMode.ObjectConstructor => "ObjectConstructor"
        case org.sireum.lang.ast.MethodMode.Select => "Select"
        case org.sireum.lang.ast.MethodMode.Store => "Store"
      }
      return printObject(ISZ(
        ("type", printString("org.sireum.lang.ast.MethodMode")),
        ("value", printString(value))
      ))
    }

    @pure def print_astTypedName(o: org.sireum.lang.ast.Typed.Name): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Typed.Name""""),
        ("ids", printISZ(T, o.ids, printString _)),
        ("args", printISZ(F, o.args, print_astTyped _))
      ))
    }

    @pure def print_astTypedTuple(o: org.sireum.lang.ast.Typed.Tuple): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Typed.Tuple""""),
        ("args", printISZ(F, o.args, print_astTyped _))
      ))
    }

    @pure def print_astTypedFun(o: org.sireum.lang.ast.Typed.Fun): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Typed.Fun""""),
        ("isPure", printB(o.isPure)),
        ("isByName", printB(o.isByName)),
        ("args", printISZ(F, o.args, print_astTyped _)),
        ("ret", print_astTyped(o.ret))
      ))
    }

    @pure def print_astTypedTypeVar(o: org.sireum.lang.ast.Typed.TypeVar): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Typed.TypeVar""""),
        ("id", printString(o.id))
      ))
    }

    @pure def print_astTypedPackage(o: org.sireum.lang.ast.Typed.Package): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Typed.Package""""),
        ("name", printISZ(T, o.name, printString _))
      ))
    }

    @pure def print_astTypedObject(o: org.sireum.lang.ast.Typed.Object): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Typed.Object""""),
        ("owner", printISZ(T, o.owner, printString _)),
        ("id", printString(o.id))
      ))
    }

    @pure def print_astTypedEnum(o: org.sireum.lang.ast.Typed.Enum): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Typed.Enum""""),
        ("name", printISZ(T, o.name, printString _))
      ))
    }

    @pure def print_astTypedMethod(o: org.sireum.lang.ast.Typed.Method): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Typed.Method""""),
        ("isInObject", printB(o.isInObject)),
        ("mode", print_astMethodModeType(o.mode)),
        ("typeParams", printISZ(T, o.typeParams, printString _)),
        ("owner", printISZ(T, o.owner, printString _)),
        ("name", printString(o.name)),
        ("paramNames", printISZ(T, o.paramNames, printString _)),
        ("tpe", print_astTypedFun(o.tpe))
      ))
    }

    @pure def print_astTypedMethods(o: org.sireum.lang.ast.Typed.Methods): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Typed.Methods""""),
        ("methods", printISZ(F, o.methods, print_astTypedMethod _))
      ))
    }

    @pure def print_astAttr(o: org.sireum.lang.ast.Attr): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.Attr""""),
        ("posOpt", printOption(F, o.posOpt, printPosition _))
      ))
    }

    @pure def print_astTypedAttr(o: org.sireum.lang.ast.TypedAttr): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.TypedAttr""""),
        ("posOpt", printOption(F, o.posOpt, printPosition _)),
        ("typedOpt", printOption(F, o.typedOpt, print_astTyped _))
      ))
    }

    @pure def print_astResolvedAttr(o: org.sireum.lang.ast.ResolvedAttr): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.ResolvedAttr""""),
        ("posOpt", printOption(F, o.posOpt, printPosition _)),
        ("resOpt", printOption(F, o.resOpt, print_astResolvedInfo _)),
        ("typedOpt", printOption(F, o.typedOpt, print_astTyped _))
      ))
    }

    @pure def print_astResolvedInfo(o: org.sireum.lang.ast.ResolvedInfo): ST = {
      o match {
        case o: org.sireum.lang.ast.ResolvedInfo.BuiltIn => return print_astResolvedInfoBuiltIn(o)
        case o: org.sireum.lang.ast.ResolvedInfo.Package => return print_astResolvedInfoPackage(o)
        case o: org.sireum.lang.ast.ResolvedInfo.Enum => return print_astResolvedInfoEnum(o)
        case o: org.sireum.lang.ast.ResolvedInfo.EnumElement => return print_astResolvedInfoEnumElement(o)
        case o: org.sireum.lang.ast.ResolvedInfo.Object => return print_astResolvedInfoObject(o)
        case o: org.sireum.lang.ast.ResolvedInfo.Var => return print_astResolvedInfoVar(o)
        case o: org.sireum.lang.ast.ResolvedInfo.Method => return print_astResolvedInfoMethod(o)
        case o: org.sireum.lang.ast.ResolvedInfo.Methods => return print_astResolvedInfoMethods(o)
        case o: org.sireum.lang.ast.ResolvedInfo.Tuple => return print_astResolvedInfoTuple(o)
        case o: org.sireum.lang.ast.ResolvedInfo.LocalVar => return print_astResolvedInfoLocalVar(o)
      }
    }

    @pure def print_astResolvedInfoBuiltInKindType(o: org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.Type): ST = {
      val value: String = o match {
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.Apply => "Apply"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.AsInstanceOf => "AsInstanceOf"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.Assert => "Assert"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.AssertMsg => "AssertMsg"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.Assume => "Assume"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.AssumeMsg => "AssumeMsg"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.BinaryAdd => "BinaryAdd"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.BinarySub => "BinarySub"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.BinaryMul => "BinaryMul"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.BinaryDiv => "BinaryDiv"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.BinaryRem => "BinaryRem"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.BinaryEq => "BinaryEq"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.BinaryNe => "BinaryNe"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.BinaryLt => "BinaryLt"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.BinaryLe => "BinaryLe"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.BinaryGt => "BinaryGt"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.BinaryGe => "BinaryGe"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.BinaryShl => "BinaryShl"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.BinaryShr => "BinaryShr"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.BinaryUshr => "BinaryUshr"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.BinaryAnd => "BinaryAnd"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.BinaryOr => "BinaryOr"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.BinaryXor => "BinaryXor"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.BinaryImply => "BinaryImply"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.BinaryCondAnd => "BinaryCondAnd"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.BinaryCondOr => "BinaryCondOr"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.BinaryCondImply => "BinaryCondImply"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.BinaryAppend => "BinaryAppend"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.BinaryPrepend => "BinaryPrepend"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.BinaryAppendAll => "BinaryAppendAll"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.BinaryRemoveAll => "BinaryRemoveAll"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.BinaryMapsTo => "BinaryMapsTo"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.Cprint => "Cprint"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.Cprintln => "Cprintln"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.EnumByName => "EnumByName"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.EnumByOrdinal => "EnumByOrdinal"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.EnumElements => "EnumElements"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.EnumNumOfElements => "EnumNumOfElements"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.EnumName => "EnumName"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.EnumOrdinal => "EnumOrdinal"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.Eprint => "Eprint"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.Eprintln => "Eprintln"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.Halt => "Halt"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.Hash => "Hash"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.IsInstanceOf => "IsInstanceOf"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.Indices => "Indices"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.Min => "Min"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.Max => "Max"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.Native => "Native"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.Print => "Print"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.Println => "Println"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.String => "String"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.UnapplySeq => "UnapplySeq"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.UnapplyTuple => "UnapplyTuple"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.UnaryPlus => "UnaryPlus"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.UnaryMinus => "UnaryMinus"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.UnaryNot => "UnaryNot"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.UnaryComplement => "UnaryComplement"
        case org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.Update => "Update"
      }
      return printObject(ISZ(
        ("type", printString("org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind")),
        ("value", printString(value))
      ))
    }

    @pure def print_astResolvedInfoBuiltIn(o: org.sireum.lang.ast.ResolvedInfo.BuiltIn): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.ResolvedInfo.BuiltIn""""),
        ("kind", print_astResolvedInfoBuiltInKindType(o.kind))
      ))
    }

    @pure def print_astResolvedInfoPackage(o: org.sireum.lang.ast.ResolvedInfo.Package): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.ResolvedInfo.Package""""),
        ("name", printISZ(T, o.name, printString _))
      ))
    }

    @pure def print_astResolvedInfoEnum(o: org.sireum.lang.ast.ResolvedInfo.Enum): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.ResolvedInfo.Enum""""),
        ("name", printISZ(T, o.name, printString _))
      ))
    }

    @pure def print_astResolvedInfoEnumElement(o: org.sireum.lang.ast.ResolvedInfo.EnumElement): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.ResolvedInfo.EnumElement""""),
        ("owner", printISZ(T, o.owner, printString _)),
        ("name", printString(o.name)),
        ("ordinal", printZ(o.ordinal))
      ))
    }

    @pure def print_astResolvedInfoObject(o: org.sireum.lang.ast.ResolvedInfo.Object): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.ResolvedInfo.Object""""),
        ("name", printISZ(T, o.name, printString _))
      ))
    }

    @pure def print_astResolvedInfoVar(o: org.sireum.lang.ast.ResolvedInfo.Var): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.ResolvedInfo.Var""""),
        ("isInObject", printB(o.isInObject)),
        ("isSpec", printB(o.isSpec)),
        ("isVal", printB(o.isVal)),
        ("owner", printISZ(T, o.owner, printString _)),
        ("id", printString(o.id))
      ))
    }

    @pure def print_astResolvedInfoMethod(o: org.sireum.lang.ast.ResolvedInfo.Method): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.ResolvedInfo.Method""""),
        ("isInObject", printB(o.isInObject)),
        ("mode", print_astMethodModeType(o.mode)),
        ("typeParams", printISZ(T, o.typeParams, printString _)),
        ("owner", printISZ(T, o.owner, printString _)),
        ("id", printString(o.id)),
        ("paramNames", printISZ(T, o.paramNames, printString _)),
        ("tpeOpt", printOption(F, o.tpeOpt, print_astTypedFun _))
      ))
    }

    @pure def print_astResolvedInfoMethods(o: org.sireum.lang.ast.ResolvedInfo.Methods): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.ResolvedInfo.Methods""""),
        ("methods", printISZ(F, o.methods, print_astResolvedInfoMethod _))
      ))
    }

    @pure def print_astResolvedInfoTuple(o: org.sireum.lang.ast.ResolvedInfo.Tuple): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.ResolvedInfo.Tuple""""),
        ("size", printZ(o.size)),
        ("index", printZ(o.index))
      ))
    }

    @pure def print_astResolvedInfoLocalVarScopeType(o: org.sireum.lang.ast.ResolvedInfo.LocalVar.Scope.Type): ST = {
      val value: String = o match {
        case org.sireum.lang.ast.ResolvedInfo.LocalVar.Scope.Current => "Current"
        case org.sireum.lang.ast.ResolvedInfo.LocalVar.Scope.Outer => "Outer"
        case org.sireum.lang.ast.ResolvedInfo.LocalVar.Scope.Closure => "Closure"
      }
      return printObject(ISZ(
        ("type", printString("org.sireum.lang.ast.ResolvedInfo.LocalVar.Scope")),
        ("value", printString(value))
      ))
    }

    @pure def print_astResolvedInfoLocalVar(o: org.sireum.lang.ast.ResolvedInfo.LocalVar): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.ResolvedInfo.LocalVar""""),
        ("context", printISZ(T, o.context, printString _)),
        ("scope", print_astResolvedInfoLocalVarScopeType(o.scope)),
        ("isVal", printB(o.isVal)),
        ("id", printString(o.id))
      ))
    }

    @pure def print_astTruthTableRow(o: org.sireum.lang.ast.TruthTable.Row): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.TruthTable.Row""""),
        ("assignment", print_astTruthTableAssignment(o.assignment)),
        ("separator", printPosition(o.separator)),
        ("values", print_astTruthTableAssignment(o.values))
      ))
    }

    @pure def print_astTruthTableAssignment(o: org.sireum.lang.ast.TruthTable.Assignment): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.TruthTable.Assignment""""),
        ("values", printISZ(F, o.values, print_astExpLitB _)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astTruthTableConclusion(o: org.sireum.lang.ast.TruthTable.Conclusion): ST = {
      o match {
        case o: org.sireum.lang.ast.TruthTable.Conclusion.Validity => return print_astTruthTableConclusionValidity(o)
        case o: org.sireum.lang.ast.TruthTable.Conclusion.Tautology => return print_astTruthTableConclusionTautology(o)
        case o: org.sireum.lang.ast.TruthTable.Conclusion.Contradictory => return print_astTruthTableConclusionContradictory(o)
        case o: org.sireum.lang.ast.TruthTable.Conclusion.Contingent => return print_astTruthTableConclusionContingent(o)
      }
    }

    @pure def print_astTruthTableConclusionValidity(o: org.sireum.lang.ast.TruthTable.Conclusion.Validity): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.TruthTable.Conclusion.Validity""""),
        ("isValid", printB(o.isValid)),
        ("assignments", printISZ(F, o.assignments, print_astTruthTableAssignment _)),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astTruthTableConclusionTautology(o: org.sireum.lang.ast.TruthTable.Conclusion.Tautology): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.TruthTable.Conclusion.Tautology""""),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astTruthTableConclusionContradictory(o: org.sireum.lang.ast.TruthTable.Conclusion.Contradictory): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.TruthTable.Conclusion.Contradictory""""),
        ("attr", print_astAttr(o.attr))
      ))
    }

    @pure def print_astTruthTableConclusionContingent(o: org.sireum.lang.ast.TruthTable.Conclusion.Contingent): ST = {
      return printObject(ISZ(
        ("type", st""""org.sireum.lang.ast.TruthTable.Conclusion.Contingent""""),
        ("trueAssignments", printISZ(F, o.trueAssignments, print_astTruthTableAssignment _)),
        ("falseAssignments", printISZ(F, o.falseAssignments, print_astTruthTableAssignment _)),
        ("attr", print_astAttr(o.attr))
      ))
    }

  }

  @record class Parser(input: String) {
    val parser: Json.Parser = Json.Parser.create(input)

    def errorOpt: Option[Json.ErrorMsg] = {
      return parser.errorOpt
    }

    def parse_symbolScope(): org.sireum.lang.symbol.Scope = {
      val t = parser.parseObjectTypes(ISZ("org.sireum.lang.symbol.Scope.Local", "org.sireum.lang.symbol.Scope.Global"))
      t.native match {
        case "org.sireum.lang.symbol.Scope.Local" => val r = parse_symbolScopeLocalT(T); return r
        case "org.sireum.lang.symbol.Scope.Global" => val r = parse_symbolScopeGlobalT(T); return r
        case _ => val r = parse_symbolScopeGlobalT(T); return r
      }
    }

    def parse_symbolScopeLocal(): org.sireum.lang.symbol.Scope.Local = {
      val r = parse_symbolScopeLocalT(F)
      return r
    }

    def parse_symbolScopeLocalT(typeParsed: B): org.sireum.lang.symbol.Scope.Local = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.symbol.Scope.Local")
      }
      parser.parseObjectKey("nameMap")
      val nameMap = parser.parseHashMap(parser.parseString _, parse_symbolInfo _)
      parser.parseObjectNext()
      parser.parseObjectKey("typeMap")
      val typeMap = parser.parseHashMap(parser.parseString _, parse_symbolTypeInfo _)
      parser.parseObjectNext()
      parser.parseObjectKey("localThisOpt")
      val localThisOpt = parser.parseOption(parse_astTyped _)
      parser.parseObjectNext()
      parser.parseObjectKey("methodReturnOpt")
      val methodReturnOpt = parser.parseOption(parse_astTyped _)
      parser.parseObjectNext()
      parser.parseObjectKey("outerOpt")
      val outerOpt = parser.parseOption(parse_symbolScope _)
      parser.parseObjectNext()
      return org.sireum.lang.symbol.Scope.Local(nameMap, typeMap, localThisOpt, methodReturnOpt, outerOpt)
    }

    def parse_symbolScopeGlobal(): org.sireum.lang.symbol.Scope.Global = {
      val r = parse_symbolScopeGlobalT(F)
      return r
    }

    def parse_symbolScopeGlobalT(typeParsed: B): org.sireum.lang.symbol.Scope.Global = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.symbol.Scope.Global")
      }
      parser.parseObjectKey("packageName")
      val packageName = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("imports")
      val imports = parser.parseISZ(parse_astStmtImport _)
      parser.parseObjectNext()
      parser.parseObjectKey("enclosingName")
      val enclosingName = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      return org.sireum.lang.symbol.Scope.Global(packageName, imports, enclosingName)
    }

    def parse_symbolInfo(): org.sireum.lang.symbol.Info = {
      val t = parser.parseObjectTypes(ISZ("org.sireum.lang.symbol.Info.Package", "org.sireum.lang.symbol.Info.Var", "org.sireum.lang.symbol.Info.SpecVar", "org.sireum.lang.symbol.Info.Method", "org.sireum.lang.symbol.Info.SpecMethod", "org.sireum.lang.symbol.Info.Object", "org.sireum.lang.symbol.Info.ExtMethod", "org.sireum.lang.symbol.Info.Enum", "org.sireum.lang.symbol.Info.EnumElement", "org.sireum.lang.symbol.Info.LocalVar", "org.sireum.lang.symbol.Info.QuantVar"))
      t.native match {
        case "org.sireum.lang.symbol.Info.Package" => val r = parse_symbolInfoPackageT(T); return r
        case "org.sireum.lang.symbol.Info.Var" => val r = parse_symbolInfoVarT(T); return r
        case "org.sireum.lang.symbol.Info.SpecVar" => val r = parse_symbolInfoSpecVarT(T); return r
        case "org.sireum.lang.symbol.Info.Method" => val r = parse_symbolInfoMethodT(T); return r
        case "org.sireum.lang.symbol.Info.SpecMethod" => val r = parse_symbolInfoSpecMethodT(T); return r
        case "org.sireum.lang.symbol.Info.Object" => val r = parse_symbolInfoObjectT(T); return r
        case "org.sireum.lang.symbol.Info.ExtMethod" => val r = parse_symbolInfoExtMethodT(T); return r
        case "org.sireum.lang.symbol.Info.Enum" => val r = parse_symbolInfoEnumT(T); return r
        case "org.sireum.lang.symbol.Info.EnumElement" => val r = parse_symbolInfoEnumElementT(T); return r
        case "org.sireum.lang.symbol.Info.LocalVar" => val r = parse_symbolInfoLocalVarT(T); return r
        case "org.sireum.lang.symbol.Info.QuantVar" => val r = parse_symbolInfoQuantVarT(T); return r
        case _ => val r = parse_symbolInfoQuantVarT(T); return r
      }
    }

    def parse_symbolInfoPackage(): org.sireum.lang.symbol.Info.Package = {
      val r = parse_symbolInfoPackageT(F)
      return r
    }

    def parse_symbolInfoPackageT(typeParsed: B): org.sireum.lang.symbol.Info.Package = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.symbol.Info.Package")
      }
      parser.parseObjectKey("name")
      val name = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("typedOpt")
      val typedOpt = parser.parseOption(parse_astTyped _)
      parser.parseObjectNext()
      parser.parseObjectKey("resOpt")
      val resOpt = parser.parseOption(parse_astResolvedInfo _)
      parser.parseObjectNext()
      return org.sireum.lang.symbol.Info.Package(name, typedOpt, resOpt)
    }

    def parse_symbolInfoVar(): org.sireum.lang.symbol.Info.Var = {
      val r = parse_symbolInfoVarT(F)
      return r
    }

    def parse_symbolInfoVarT(typeParsed: B): org.sireum.lang.symbol.Info.Var = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.symbol.Info.Var")
      }
      parser.parseObjectKey("owner")
      val owner = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("isInObject")
      val isInObject = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("scope")
      val scope = parse_symbolScope()
      parser.parseObjectNext()
      parser.parseObjectKey("ast")
      val ast = parse_astStmtVar()
      parser.parseObjectNext()
      return org.sireum.lang.symbol.Info.Var(owner, isInObject, scope, ast)
    }

    def parse_symbolInfoSpecVar(): org.sireum.lang.symbol.Info.SpecVar = {
      val r = parse_symbolInfoSpecVarT(F)
      return r
    }

    def parse_symbolInfoSpecVarT(typeParsed: B): org.sireum.lang.symbol.Info.SpecVar = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.symbol.Info.SpecVar")
      }
      parser.parseObjectKey("owner")
      val owner = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("isInObject")
      val isInObject = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("scope")
      val scope = parse_symbolScope()
      parser.parseObjectNext()
      parser.parseObjectKey("ast")
      val ast = parse_astStmtSpecVar()
      parser.parseObjectNext()
      return org.sireum.lang.symbol.Info.SpecVar(owner, isInObject, scope, ast)
    }

    def parse_symbolInfoMethod(): org.sireum.lang.symbol.Info.Method = {
      val r = parse_symbolInfoMethodT(F)
      return r
    }

    def parse_symbolInfoMethodT(typeParsed: B): org.sireum.lang.symbol.Info.Method = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.symbol.Info.Method")
      }
      parser.parseObjectKey("owner")
      val owner = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("isInObject")
      val isInObject = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("scope")
      val scope = parse_symbolScope()
      parser.parseObjectNext()
      parser.parseObjectKey("hasBody")
      val hasBody = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("ast")
      val ast = parse_astStmtMethod()
      parser.parseObjectNext()
      return org.sireum.lang.symbol.Info.Method(owner, isInObject, scope, hasBody, ast)
    }

    def parse_symbolInfoSpecMethod(): org.sireum.lang.symbol.Info.SpecMethod = {
      val r = parse_symbolInfoSpecMethodT(F)
      return r
    }

    def parse_symbolInfoSpecMethodT(typeParsed: B): org.sireum.lang.symbol.Info.SpecMethod = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.symbol.Info.SpecMethod")
      }
      parser.parseObjectKey("owner")
      val owner = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("isInObject")
      val isInObject = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("scope")
      val scope = parse_symbolScope()
      parser.parseObjectNext()
      parser.parseObjectKey("ast")
      val ast = parse_astStmtSpecMethod()
      parser.parseObjectNext()
      return org.sireum.lang.symbol.Info.SpecMethod(owner, isInObject, scope, ast)
    }

    def parse_symbolInfoObject(): org.sireum.lang.symbol.Info.Object = {
      val r = parse_symbolInfoObjectT(F)
      return r
    }

    def parse_symbolInfoObjectT(typeParsed: B): org.sireum.lang.symbol.Info.Object = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.symbol.Info.Object")
      }
      parser.parseObjectKey("owner")
      val owner = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("isSynthetic")
      val isSynthetic = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("scope")
      val scope = parse_symbolScopeGlobal()
      parser.parseObjectNext()
      parser.parseObjectKey("outlined")
      val outlined = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("typeChecked")
      val typeChecked = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("ast")
      val ast = parse_astStmtObject()
      parser.parseObjectNext()
      parser.parseObjectKey("typedOpt")
      val typedOpt = parser.parseOption(parse_astTyped _)
      parser.parseObjectNext()
      parser.parseObjectKey("resOpt")
      val resOpt = parser.parseOption(parse_astResolvedInfo _)
      parser.parseObjectNext()
      parser.parseObjectKey("constructorRes")
      val constructorRes = parse_astResolvedInfoMethod()
      parser.parseObjectNext()
      return org.sireum.lang.symbol.Info.Object(owner, isSynthetic, scope, outlined, typeChecked, ast, typedOpt, resOpt, constructorRes)
    }

    def parse_symbolInfoExtMethod(): org.sireum.lang.symbol.Info.ExtMethod = {
      val r = parse_symbolInfoExtMethodT(F)
      return r
    }

    def parse_symbolInfoExtMethodT(typeParsed: B): org.sireum.lang.symbol.Info.ExtMethod = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.symbol.Info.ExtMethod")
      }
      parser.parseObjectKey("owner")
      val owner = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("scope")
      val scope = parse_symbolScopeGlobal()
      parser.parseObjectNext()
      parser.parseObjectKey("ast")
      val ast = parse_astStmtExtMethod()
      parser.parseObjectNext()
      return org.sireum.lang.symbol.Info.ExtMethod(owner, scope, ast)
    }

    def parse_symbolInfoEnum(): org.sireum.lang.symbol.Info.Enum = {
      val r = parse_symbolInfoEnumT(F)
      return r
    }

    def parse_symbolInfoEnumT(typeParsed: B): org.sireum.lang.symbol.Info.Enum = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.symbol.Info.Enum")
      }
      parser.parseObjectKey("name")
      val name = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("elements")
      val elements = parser.parseMap(parser.parseString _, parse_astResolvedInfo _)
      parser.parseObjectNext()
      parser.parseObjectKey("typedOpt")
      val typedOpt = parser.parseOption(parse_astTyped _)
      parser.parseObjectNext()
      parser.parseObjectKey("resOpt")
      val resOpt = parser.parseOption(parse_astResolvedInfo _)
      parser.parseObjectNext()
      parser.parseObjectKey("elementTypedOpt")
      val elementTypedOpt = parser.parseOption(parse_astTyped _)
      parser.parseObjectNext()
      parser.parseObjectKey("posOpt")
      val posOpt = parser.parseOption(parser.parsePosition _)
      parser.parseObjectNext()
      return org.sireum.lang.symbol.Info.Enum(name, elements, typedOpt, resOpt, elementTypedOpt, posOpt)
    }

    def parse_symbolInfoEnumElement(): org.sireum.lang.symbol.Info.EnumElement = {
      val r = parse_symbolInfoEnumElementT(F)
      return r
    }

    def parse_symbolInfoEnumElementT(typeParsed: B): org.sireum.lang.symbol.Info.EnumElement = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.symbol.Info.EnumElement")
      }
      parser.parseObjectKey("owner")
      val owner = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("id")
      val id = parser.parseString()
      parser.parseObjectNext()
      parser.parseObjectKey("typedOpt")
      val typedOpt = parser.parseOption(parse_astTyped _)
      parser.parseObjectNext()
      parser.parseObjectKey("resOpt")
      val resOpt = parser.parseOption(parse_astResolvedInfo _)
      parser.parseObjectNext()
      parser.parseObjectKey("posOpt")
      val posOpt = parser.parseOption(parser.parsePosition _)
      parser.parseObjectNext()
      return org.sireum.lang.symbol.Info.EnumElement(owner, id, typedOpt, resOpt, posOpt)
    }

    def parse_symbolInfoLocalVar(): org.sireum.lang.symbol.Info.LocalVar = {
      val r = parse_symbolInfoLocalVarT(F)
      return r
    }

    def parse_symbolInfoLocalVarT(typeParsed: B): org.sireum.lang.symbol.Info.LocalVar = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.symbol.Info.LocalVar")
      }
      parser.parseObjectKey("name")
      val name = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("isVal")
      val isVal = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("ast")
      val ast = parse_astId()
      parser.parseObjectNext()
      parser.parseObjectKey("typedOpt")
      val typedOpt = parser.parseOption(parse_astTyped _)
      parser.parseObjectNext()
      parser.parseObjectKey("resOpt")
      val resOpt = parser.parseOption(parse_astResolvedInfo _)
      parser.parseObjectNext()
      return org.sireum.lang.symbol.Info.LocalVar(name, isVal, ast, typedOpt, resOpt)
    }

    def parse_symbolInfoQuantVar(): org.sireum.lang.symbol.Info.QuantVar = {
      val r = parse_symbolInfoQuantVarT(F)
      return r
    }

    def parse_symbolInfoQuantVarT(typeParsed: B): org.sireum.lang.symbol.Info.QuantVar = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.symbol.Info.QuantVar")
      }
      parser.parseObjectKey("name")
      val name = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("ast")
      val ast = parse_astId()
      parser.parseObjectNext()
      parser.parseObjectKey("typedOpt")
      val typedOpt = parser.parseOption(parse_astTyped _)
      parser.parseObjectNext()
      parser.parseObjectKey("resOpt")
      val resOpt = parser.parseOption(parse_astResolvedInfo _)
      parser.parseObjectNext()
      return org.sireum.lang.symbol.Info.QuantVar(name, ast, typedOpt, resOpt)
    }

    def parse_symbolTypeInfo(): org.sireum.lang.symbol.TypeInfo = {
      val t = parser.parseObjectTypes(ISZ("org.sireum.lang.symbol.TypeInfo.SubZ", "org.sireum.lang.symbol.TypeInfo.Enum", "org.sireum.lang.symbol.TypeInfo.Sig", "org.sireum.lang.symbol.TypeInfo.Adt", "org.sireum.lang.symbol.TypeInfo.TypeAlias", "org.sireum.lang.symbol.TypeInfo.TypeVar"))
      t.native match {
        case "org.sireum.lang.symbol.TypeInfo.SubZ" => val r = parse_symbolTypeInfoSubZT(T); return r
        case "org.sireum.lang.symbol.TypeInfo.Enum" => val r = parse_symbolTypeInfoEnumT(T); return r
        case "org.sireum.lang.symbol.TypeInfo.Sig" => val r = parse_symbolTypeInfoSigT(T); return r
        case "org.sireum.lang.symbol.TypeInfo.Adt" => val r = parse_symbolTypeInfoAdtT(T); return r
        case "org.sireum.lang.symbol.TypeInfo.TypeAlias" => val r = parse_symbolTypeInfoTypeAliasT(T); return r
        case "org.sireum.lang.symbol.TypeInfo.TypeVar" => val r = parse_symbolTypeInfoTypeVarT(T); return r
        case _ => val r = parse_symbolTypeInfoTypeVarT(T); return r
      }
    }

    def parse_symbolTypeInfoSubZ(): org.sireum.lang.symbol.TypeInfo.SubZ = {
      val r = parse_symbolTypeInfoSubZT(F)
      return r
    }

    def parse_symbolTypeInfoSubZT(typeParsed: B): org.sireum.lang.symbol.TypeInfo.SubZ = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.symbol.TypeInfo.SubZ")
      }
      parser.parseObjectKey("owner")
      val owner = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("ast")
      val ast = parse_astStmtSubZ()
      parser.parseObjectNext()
      return org.sireum.lang.symbol.TypeInfo.SubZ(owner, ast)
    }

    def parse_symbolTypeInfoEnum(): org.sireum.lang.symbol.TypeInfo.Enum = {
      val r = parse_symbolTypeInfoEnumT(F)
      return r
    }

    def parse_symbolTypeInfoEnumT(typeParsed: B): org.sireum.lang.symbol.TypeInfo.Enum = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.symbol.TypeInfo.Enum")
      }
      parser.parseObjectKey("owner")
      val owner = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("elements")
      val elements = parser.parseMap(parser.parseString _, parse_astResolvedInfo _)
      parser.parseObjectNext()
      parser.parseObjectKey("posOpt")
      val posOpt = parser.parseOption(parser.parsePosition _)
      parser.parseObjectNext()
      return org.sireum.lang.symbol.TypeInfo.Enum(owner, elements, posOpt)
    }

    def parse_symbolTypeInfoSig(): org.sireum.lang.symbol.TypeInfo.Sig = {
      val r = parse_symbolTypeInfoSigT(F)
      return r
    }

    def parse_symbolTypeInfoSigT(typeParsed: B): org.sireum.lang.symbol.TypeInfo.Sig = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.symbol.TypeInfo.Sig")
      }
      parser.parseObjectKey("owner")
      val owner = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("outlined")
      val outlined = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("typeChecked")
      val typeChecked = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("tpe")
      val tpe = parse_astTypedName()
      parser.parseObjectNext()
      parser.parseObjectKey("ancestors")
      val ancestors = parser.parseISZ(parse_astTypedName _)
      parser.parseObjectNext()
      parser.parseObjectKey("specVars")
      val specVars = parser.parseHashSMap(parser.parseString _, parse_symbolInfoSpecVar _)
      parser.parseObjectNext()
      parser.parseObjectKey("specMethods")
      val specMethods = parser.parseHashMap(parser.parseString _, parse_symbolInfoSpecMethod _)
      parser.parseObjectNext()
      parser.parseObjectKey("methods")
      val methods = parser.parseHashMap(parser.parseString _, parse_symbolInfoMethod _)
      parser.parseObjectNext()
      parser.parseObjectKey("refinements")
      val refinements = parser.parseHashMap(parser.parseString _, parse_symbolTypeInfoName _)
      parser.parseObjectNext()
      parser.parseObjectKey("scope")
      val scope = parse_symbolScopeGlobal()
      parser.parseObjectNext()
      parser.parseObjectKey("ast")
      val ast = parse_astStmtSig()
      parser.parseObjectNext()
      return org.sireum.lang.symbol.TypeInfo.Sig(owner, outlined, typeChecked, tpe, ancestors, specVars, specMethods, methods, refinements, scope, ast)
    }

    def parse_symbolTypeInfoName(): org.sireum.lang.symbol.TypeInfo.Name = {
      val r = parse_symbolTypeInfoNameT(F)
      return r
    }

    def parse_symbolTypeInfoNameT(typeParsed: B): org.sireum.lang.symbol.TypeInfo.Name = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.symbol.TypeInfo.Name")
      }
      parser.parseObjectKey("ids")
      val ids = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      return org.sireum.lang.symbol.TypeInfo.Name(ids)
    }

    def parse_symbolTypeInfoAdt(): org.sireum.lang.symbol.TypeInfo.Adt = {
      val r = parse_symbolTypeInfoAdtT(F)
      return r
    }

    def parse_symbolTypeInfoAdtT(typeParsed: B): org.sireum.lang.symbol.TypeInfo.Adt = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.symbol.TypeInfo.Adt")
      }
      parser.parseObjectKey("owner")
      val owner = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("outlined")
      val outlined = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("typeChecked")
      val typeChecked = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("tpe")
      val tpe = parse_astTypedName()
      parser.parseObjectNext()
      parser.parseObjectKey("constructorTypeOpt")
      val constructorTypeOpt = parser.parseOption(parse_astTyped _)
      parser.parseObjectNext()
      parser.parseObjectKey("constructorResOpt")
      val constructorResOpt = parser.parseOption(parse_astResolvedInfo _)
      parser.parseObjectNext()
      parser.parseObjectKey("extractorTypeMap")
      val extractorTypeMap = parser.parseMap(parser.parseString _, parse_astTyped _)
      parser.parseObjectNext()
      parser.parseObjectKey("extractorResOpt")
      val extractorResOpt = parser.parseOption(parse_astResolvedInfo _)
      parser.parseObjectNext()
      parser.parseObjectKey("ancestors")
      val ancestors = parser.parseISZ(parse_astTypedName _)
      parser.parseObjectNext()
      parser.parseObjectKey("specVars")
      val specVars = parser.parseHashSMap(parser.parseString _, parse_symbolInfoSpecVar _)
      parser.parseObjectNext()
      parser.parseObjectKey("vars")
      val vars = parser.parseHashSMap(parser.parseString _, parse_symbolInfoVar _)
      parser.parseObjectNext()
      parser.parseObjectKey("specMethods")
      val specMethods = parser.parseHashMap(parser.parseString _, parse_symbolInfoSpecMethod _)
      parser.parseObjectNext()
      parser.parseObjectKey("methods")
      val methods = parser.parseHashMap(parser.parseString _, parse_symbolInfoMethod _)
      parser.parseObjectNext()
      parser.parseObjectKey("refinements")
      val refinements = parser.parseHashMap(parser.parseString _, parse_symbolTypeInfoName _)
      parser.parseObjectNext()
      parser.parseObjectKey("scope")
      val scope = parse_symbolScopeGlobal()
      parser.parseObjectNext()
      parser.parseObjectKey("ast")
      val ast = parse_astStmtAdt()
      parser.parseObjectNext()
      return org.sireum.lang.symbol.TypeInfo.Adt(owner, outlined, typeChecked, tpe, constructorTypeOpt, constructorResOpt, extractorTypeMap, extractorResOpt, ancestors, specVars, vars, specMethods, methods, refinements, scope, ast)
    }

    def parse_symbolTypeInfoTypeAlias(): org.sireum.lang.symbol.TypeInfo.TypeAlias = {
      val r = parse_symbolTypeInfoTypeAliasT(F)
      return r
    }

    def parse_symbolTypeInfoTypeAliasT(typeParsed: B): org.sireum.lang.symbol.TypeInfo.TypeAlias = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.symbol.TypeInfo.TypeAlias")
      }
      parser.parseObjectKey("name")
      val name = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("scope")
      val scope = parse_symbolScopeGlobal()
      parser.parseObjectNext()
      parser.parseObjectKey("ast")
      val ast = parse_astStmtTypeAlias()
      parser.parseObjectNext()
      return org.sireum.lang.symbol.TypeInfo.TypeAlias(name, scope, ast)
    }

    def parse_symbolTypeInfoTypeVar(): org.sireum.lang.symbol.TypeInfo.TypeVar = {
      val r = parse_symbolTypeInfoTypeVarT(F)
      return r
    }

    def parse_symbolTypeInfoTypeVarT(typeParsed: B): org.sireum.lang.symbol.TypeInfo.TypeVar = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.symbol.TypeInfo.TypeVar")
      }
      parser.parseObjectKey("id")
      val id = parser.parseString()
      parser.parseObjectNext()
      parser.parseObjectKey("ast")
      val ast = parse_astTypeParam()
      parser.parseObjectNext()
      return org.sireum.lang.symbol.TypeInfo.TypeVar(id, ast)
    }

    def parse_symbolTypeInfoMembers(): org.sireum.lang.symbol.TypeInfo.Members = {
      val r = parse_symbolTypeInfoMembersT(F)
      return r
    }

    def parse_symbolTypeInfoMembersT(typeParsed: B): org.sireum.lang.symbol.TypeInfo.Members = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.symbol.TypeInfo.Members")
      }
      parser.parseObjectKey("specVars")
      val specVars = parser.parseHashSMap(parser.parseString _, parse_symbolInfoSpecVar _)
      parser.parseObjectNext()
      parser.parseObjectKey("vars")
      val vars = parser.parseHashSMap(parser.parseString _, parse_symbolInfoVar _)
      parser.parseObjectNext()
      parser.parseObjectKey("specMethods")
      val specMethods = parser.parseHashMap(parser.parseString _, parse_symbolInfoSpecMethod _)
      parser.parseObjectNext()
      parser.parseObjectKey("methods")
      val methods = parser.parseHashMap(parser.parseString _, parse_symbolInfoMethod _)
      parser.parseObjectNext()
      parser.parseObjectKey("refinements")
      val refinements = parser.parseHashMap(parser.parseString _, parse_symbolTypeInfoName _)
      parser.parseObjectNext()
      return org.sireum.lang.symbol.TypeInfo.Members(specVars, vars, specMethods, methods, refinements)
    }

    def parse_astTopUnit(): org.sireum.lang.ast.TopUnit = {
      val t = parser.parseObjectTypes(ISZ("org.sireum.lang.ast.TopUnit.Program", "org.sireum.lang.ast.TopUnit.SequentUnit", "org.sireum.lang.ast.TopUnit.TruthTableUnit"))
      t.native match {
        case "org.sireum.lang.ast.TopUnit.Program" => val r = parse_astTopUnitProgramT(T); return r
        case "org.sireum.lang.ast.TopUnit.SequentUnit" => val r = parse_astTopUnitSequentUnitT(T); return r
        case "org.sireum.lang.ast.TopUnit.TruthTableUnit" => val r = parse_astTopUnitTruthTableUnitT(T); return r
        case _ => val r = parse_astTopUnitTruthTableUnitT(T); return r
      }
    }

    def parse_astTopUnitProgram(): org.sireum.lang.ast.TopUnit.Program = {
      val r = parse_astTopUnitProgramT(F)
      return r
    }

    def parse_astTopUnitProgramT(typeParsed: B): org.sireum.lang.ast.TopUnit.Program = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.TopUnit.Program")
      }
      parser.parseObjectKey("fileUriOpt")
      val fileUriOpt = parser.parseOption(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("packageName")
      val packageName = parse_astName()
      parser.parseObjectNext()
      parser.parseObjectKey("body")
      val body = parse_astBody()
      parser.parseObjectNext()
      return org.sireum.lang.ast.TopUnit.Program(fileUriOpt, packageName, body)
    }

    def parse_astTopUnitSequentUnit(): org.sireum.lang.ast.TopUnit.SequentUnit = {
      val r = parse_astTopUnitSequentUnitT(F)
      return r
    }

    def parse_astTopUnitSequentUnitT(typeParsed: B): org.sireum.lang.ast.TopUnit.SequentUnit = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.TopUnit.SequentUnit")
      }
      parser.parseObjectKey("fileUriOpt")
      val fileUriOpt = parser.parseOption(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("sequent")
      val sequent = parse_astSequent()
      parser.parseObjectNext()
      return org.sireum.lang.ast.TopUnit.SequentUnit(fileUriOpt, sequent)
    }

    def parse_astTopUnitTruthTableUnit(): org.sireum.lang.ast.TopUnit.TruthTableUnit = {
      val r = parse_astTopUnitTruthTableUnitT(F)
      return r
    }

    def parse_astTopUnitTruthTableUnitT(typeParsed: B): org.sireum.lang.ast.TopUnit.TruthTableUnit = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.TopUnit.TruthTableUnit")
      }
      parser.parseObjectKey("fileUriOpt")
      val fileUriOpt = parser.parseOption(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("stars")
      val stars = parser.parseISZ(parser.parsePosition _)
      parser.parseObjectNext()
      parser.parseObjectKey("vars")
      val vars = parser.parseISZ(parse_astId _)
      parser.parseObjectNext()
      parser.parseObjectKey("separator")
      val separator = parser.parsePosition()
      parser.parseObjectNext()
      parser.parseObjectKey("isSequent")
      val isSequent = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("sequent")
      val sequent = parse_astSequent()
      parser.parseObjectNext()
      parser.parseObjectKey("rows")
      val rows = parser.parseISZ(parse_astTruthTableRow _)
      parser.parseObjectNext()
      parser.parseObjectKey("conclusionOpt")
      val conclusionOpt = parser.parseOption(parse_astTruthTableConclusion _)
      parser.parseObjectNext()
      return org.sireum.lang.ast.TopUnit.TruthTableUnit(fileUriOpt, stars, vars, separator, isSequent, sequent, rows, conclusionOpt)
    }

    def parse_astStmt(): org.sireum.lang.ast.Stmt = {
      val t = parser.parseObjectTypes(ISZ("org.sireum.lang.ast.Stmt.Import", "org.sireum.lang.ast.Stmt.Var", "org.sireum.lang.ast.Stmt.VarPattern", "org.sireum.lang.ast.Stmt.SpecVar", "org.sireum.lang.ast.Stmt.Method", "org.sireum.lang.ast.Stmt.ExtMethod", "org.sireum.lang.ast.Stmt.SpecMethod", "org.sireum.lang.ast.Stmt.Enum", "org.sireum.lang.ast.Stmt.SubZ", "org.sireum.lang.ast.Stmt.Object", "org.sireum.lang.ast.Stmt.Sig", "org.sireum.lang.ast.Stmt.Adt", "org.sireum.lang.ast.Stmt.TypeAlias", "org.sireum.lang.ast.Stmt.Assign", "org.sireum.lang.ast.Stmt.Block", "org.sireum.lang.ast.Stmt.If", "org.sireum.lang.ast.Stmt.Match", "org.sireum.lang.ast.Stmt.While", "org.sireum.lang.ast.Stmt.DoWhile", "org.sireum.lang.ast.Stmt.For", "org.sireum.lang.ast.Stmt.Return", "org.sireum.lang.ast.Stmt.Expr", "org.sireum.lang.ast.Stmt.Fact", "org.sireum.lang.ast.Stmt.Invariant", "org.sireum.lang.ast.Stmt.Theorem", "org.sireum.lang.ast.Stmt.SpecLabel", "org.sireum.lang.ast.Stmt.SpecBlock", "org.sireum.lang.ast.Stmt.Deduce", "org.sireum.lang.ast.Stmt.DeduceSteps"))
      t.native match {
        case "org.sireum.lang.ast.Stmt.Import" => val r = parse_astStmtImportT(T); return r
        case "org.sireum.lang.ast.Stmt.Var" => val r = parse_astStmtVarT(T); return r
        case "org.sireum.lang.ast.Stmt.VarPattern" => val r = parse_astStmtVarPatternT(T); return r
        case "org.sireum.lang.ast.Stmt.SpecVar" => val r = parse_astStmtSpecVarT(T); return r
        case "org.sireum.lang.ast.Stmt.Method" => val r = parse_astStmtMethodT(T); return r
        case "org.sireum.lang.ast.Stmt.ExtMethod" => val r = parse_astStmtExtMethodT(T); return r
        case "org.sireum.lang.ast.Stmt.SpecMethod" => val r = parse_astStmtSpecMethodT(T); return r
        case "org.sireum.lang.ast.Stmt.Enum" => val r = parse_astStmtEnumT(T); return r
        case "org.sireum.lang.ast.Stmt.SubZ" => val r = parse_astStmtSubZT(T); return r
        case "org.sireum.lang.ast.Stmt.Object" => val r = parse_astStmtObjectT(T); return r
        case "org.sireum.lang.ast.Stmt.Sig" => val r = parse_astStmtSigT(T); return r
        case "org.sireum.lang.ast.Stmt.Adt" => val r = parse_astStmtAdtT(T); return r
        case "org.sireum.lang.ast.Stmt.TypeAlias" => val r = parse_astStmtTypeAliasT(T); return r
        case "org.sireum.lang.ast.Stmt.Assign" => val r = parse_astStmtAssignT(T); return r
        case "org.sireum.lang.ast.Stmt.Block" => val r = parse_astStmtBlockT(T); return r
        case "org.sireum.lang.ast.Stmt.If" => val r = parse_astStmtIfT(T); return r
        case "org.sireum.lang.ast.Stmt.Match" => val r = parse_astStmtMatchT(T); return r
        case "org.sireum.lang.ast.Stmt.While" => val r = parse_astStmtWhileT(T); return r
        case "org.sireum.lang.ast.Stmt.DoWhile" => val r = parse_astStmtDoWhileT(T); return r
        case "org.sireum.lang.ast.Stmt.For" => val r = parse_astStmtForT(T); return r
        case "org.sireum.lang.ast.Stmt.Return" => val r = parse_astStmtReturnT(T); return r
        case "org.sireum.lang.ast.Stmt.Expr" => val r = parse_astStmtExprT(T); return r
        case "org.sireum.lang.ast.Stmt.Fact" => val r = parse_astStmtFactT(T); return r
        case "org.sireum.lang.ast.Stmt.Invariant" => val r = parse_astStmtInvariantT(T); return r
        case "org.sireum.lang.ast.Stmt.Theorem" => val r = parse_astStmtTheoremT(T); return r
        case "org.sireum.lang.ast.Stmt.SpecLabel" => val r = parse_astStmtSpecLabelT(T); return r
        case "org.sireum.lang.ast.Stmt.SpecBlock" => val r = parse_astStmtSpecBlockT(T); return r
        case "org.sireum.lang.ast.Stmt.Deduce" => val r = parse_astStmtDeduceT(T); return r
        case "org.sireum.lang.ast.Stmt.DeduceSteps" => val r = parse_astStmtDeduceStepsT(T); return r
        case _ => val r = parse_astStmtDeduceStepsT(T); return r
      }
    }

    def parse_astStmtImport(): org.sireum.lang.ast.Stmt.Import = {
      val r = parse_astStmtImportT(F)
      return r
    }

    def parse_astStmtImportT(typeParsed: B): org.sireum.lang.ast.Stmt.Import = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.Import")
      }
      parser.parseObjectKey("importers")
      val importers = parser.parseISZ(parse_astStmtImportImporter _)
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Stmt.Import(importers, attr)
    }

    def parse_astStmtImportImporter(): org.sireum.lang.ast.Stmt.Import.Importer = {
      val r = parse_astStmtImportImporterT(F)
      return r
    }

    def parse_astStmtImportImporterT(typeParsed: B): org.sireum.lang.ast.Stmt.Import.Importer = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.Import.Importer")
      }
      parser.parseObjectKey("name")
      val name = parse_astName()
      parser.parseObjectNext()
      parser.parseObjectKey("selectorOpt")
      val selectorOpt = parser.parseOption(parse_astStmtImportSelector _)
      parser.parseObjectNext()
      return org.sireum.lang.ast.Stmt.Import.Importer(name, selectorOpt)
    }

    def parse_astStmtImportSelector(): org.sireum.lang.ast.Stmt.Import.Selector = {
      val t = parser.parseObjectTypes(ISZ("org.sireum.lang.ast.Stmt.Import.MultiSelector", "org.sireum.lang.ast.Stmt.Import.WildcardSelector"))
      t.native match {
        case "org.sireum.lang.ast.Stmt.Import.MultiSelector" => val r = parse_astStmtImportMultiSelectorT(T); return r
        case "org.sireum.lang.ast.Stmt.Import.WildcardSelector" => val r = parse_astStmtImportWildcardSelectorT(T); return r
        case _ => val r = parse_astStmtImportWildcardSelectorT(T); return r
      }
    }

    def parse_astStmtImportMultiSelector(): org.sireum.lang.ast.Stmt.Import.MultiSelector = {
      val r = parse_astStmtImportMultiSelectorT(F)
      return r
    }

    def parse_astStmtImportMultiSelectorT(typeParsed: B): org.sireum.lang.ast.Stmt.Import.MultiSelector = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.Import.MultiSelector")
      }
      parser.parseObjectKey("selectors")
      val selectors = parser.parseISZ(parse_astStmtImportNamedSelector _)
      parser.parseObjectNext()
      return org.sireum.lang.ast.Stmt.Import.MultiSelector(selectors)
    }

    def parse_astStmtImportWildcardSelector(): org.sireum.lang.ast.Stmt.Import.WildcardSelector = {
      val r = parse_astStmtImportWildcardSelectorT(F)
      return r
    }

    def parse_astStmtImportWildcardSelectorT(typeParsed: B): org.sireum.lang.ast.Stmt.Import.WildcardSelector = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.Import.WildcardSelector")
      }
      return org.sireum.lang.ast.Stmt.Import.WildcardSelector()
    }

    def parse_astStmtImportNamedSelector(): org.sireum.lang.ast.Stmt.Import.NamedSelector = {
      val r = parse_astStmtImportNamedSelectorT(F)
      return r
    }

    def parse_astStmtImportNamedSelectorT(typeParsed: B): org.sireum.lang.ast.Stmt.Import.NamedSelector = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.Import.NamedSelector")
      }
      parser.parseObjectKey("from")
      val from = parse_astId()
      parser.parseObjectNext()
      parser.parseObjectKey("to")
      val to = parse_astId()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Stmt.Import.NamedSelector(from, to)
    }

    def parse_astStmtVar(): org.sireum.lang.ast.Stmt.Var = {
      val r = parse_astStmtVarT(F)
      return r
    }

    def parse_astStmtVarT(typeParsed: B): org.sireum.lang.ast.Stmt.Var = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.Var")
      }
      parser.parseObjectKey("isVal")
      val isVal = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("id")
      val id = parse_astId()
      parser.parseObjectNext()
      parser.parseObjectKey("tipeOpt")
      val tipeOpt = parser.parseOption(parse_astType _)
      parser.parseObjectNext()
      parser.parseObjectKey("initOpt")
      val initOpt = parser.parseOption(parse_astAssignExp _)
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astResolvedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Stmt.Var(isVal, id, tipeOpt, initOpt, attr)
    }

    def parse_astStmtVarPattern(): org.sireum.lang.ast.Stmt.VarPattern = {
      val r = parse_astStmtVarPatternT(F)
      return r
    }

    def parse_astStmtVarPatternT(typeParsed: B): org.sireum.lang.ast.Stmt.VarPattern = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.VarPattern")
      }
      parser.parseObjectKey("isVal")
      val isVal = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("pattern")
      val pattern = parse_astPattern()
      parser.parseObjectNext()
      parser.parseObjectKey("tipeOpt")
      val tipeOpt = parser.parseOption(parse_astType _)
      parser.parseObjectNext()
      parser.parseObjectKey("init")
      val init = parse_astAssignExp()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Stmt.VarPattern(isVal, pattern, tipeOpt, init, attr)
    }

    def parse_astStmtSpecVar(): org.sireum.lang.ast.Stmt.SpecVar = {
      val r = parse_astStmtSpecVarT(F)
      return r
    }

    def parse_astStmtSpecVarT(typeParsed: B): org.sireum.lang.ast.Stmt.SpecVar = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.SpecVar")
      }
      parser.parseObjectKey("isVal")
      val isVal = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("id")
      val id = parse_astId()
      parser.parseObjectNext()
      parser.parseObjectKey("tipe")
      val tipe = parse_astType()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astResolvedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Stmt.SpecVar(isVal, id, tipe, attr)
    }

    def parse_astStmtMethod(): org.sireum.lang.ast.Stmt.Method = {
      val r = parse_astStmtMethodT(F)
      return r
    }

    def parse_astStmtMethodT(typeParsed: B): org.sireum.lang.ast.Stmt.Method = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.Method")
      }
      parser.parseObjectKey("purity")
      val purity = parse_astPurityType()
      parser.parseObjectNext()
      parser.parseObjectKey("hasOverride")
      val hasOverride = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("isHelper")
      val isHelper = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("sig")
      val sig = parse_astMethodSig()
      parser.parseObjectNext()
      parser.parseObjectKey("contract")
      val contract = parse_astMethodContract()
      parser.parseObjectNext()
      parser.parseObjectKey("bodyOpt")
      val bodyOpt = parser.parseOption(parse_astBody _)
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astResolvedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Stmt.Method(purity, hasOverride, isHelper, sig, contract, bodyOpt, attr)
    }

    def parse_astStmtExtMethod(): org.sireum.lang.ast.Stmt.ExtMethod = {
      val r = parse_astStmtExtMethodT(F)
      return r
    }

    def parse_astStmtExtMethodT(typeParsed: B): org.sireum.lang.ast.Stmt.ExtMethod = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.ExtMethod")
      }
      parser.parseObjectKey("isPure")
      val isPure = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("sig")
      val sig = parse_astMethodSig()
      parser.parseObjectNext()
      parser.parseObjectKey("contract")
      val contract = parse_astMethodContract()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astResolvedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Stmt.ExtMethod(isPure, sig, contract, attr)
    }

    def parse_astStmtSpecMethod(): org.sireum.lang.ast.Stmt.SpecMethod = {
      val r = parse_astStmtSpecMethodT(F)
      return r
    }

    def parse_astStmtSpecMethodT(typeParsed: B): org.sireum.lang.ast.Stmt.SpecMethod = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.SpecMethod")
      }
      parser.parseObjectKey("sig")
      val sig = parse_astMethodSig()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astResolvedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Stmt.SpecMethod(sig, attr)
    }

    def parse_astStmtEnum(): org.sireum.lang.ast.Stmt.Enum = {
      val r = parse_astStmtEnumT(F)
      return r
    }

    def parse_astStmtEnumT(typeParsed: B): org.sireum.lang.ast.Stmt.Enum = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.Enum")
      }
      parser.parseObjectKey("id")
      val id = parse_astId()
      parser.parseObjectNext()
      parser.parseObjectKey("elements")
      val elements = parser.parseISZ(parse_astId _)
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Stmt.Enum(id, elements, attr)
    }

    def parse_astStmtSubZ(): org.sireum.lang.ast.Stmt.SubZ = {
      val r = parse_astStmtSubZT(F)
      return r
    }

    def parse_astStmtSubZT(typeParsed: B): org.sireum.lang.ast.Stmt.SubZ = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.SubZ")
      }
      parser.parseObjectKey("id")
      val id = parse_astId()
      parser.parseObjectNext()
      parser.parseObjectKey("isSigned")
      val isSigned = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("isBitVector")
      val isBitVector = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("isWrapped")
      val isWrapped = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("hasMin")
      val hasMin = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("hasMax")
      val hasMax = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("bitWidth")
      val bitWidth = parser.parseZ()
      parser.parseObjectNext()
      parser.parseObjectKey("min")
      val min = parser.parseZ()
      parser.parseObjectNext()
      parser.parseObjectKey("max")
      val max = parser.parseZ()
      parser.parseObjectNext()
      parser.parseObjectKey("index")
      val index = parser.parseZ()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Stmt.SubZ(id, isSigned, isBitVector, isWrapped, hasMin, hasMax, bitWidth, min, max, index, attr)
    }

    def parse_astStmtObject(): org.sireum.lang.ast.Stmt.Object = {
      val r = parse_astStmtObjectT(F)
      return r
    }

    def parse_astStmtObjectT(typeParsed: B): org.sireum.lang.ast.Stmt.Object = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.Object")
      }
      parser.parseObjectKey("isApp")
      val isApp = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("extNameOpt")
      val extNameOpt = parser.parseOption(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("id")
      val id = parse_astId()
      parser.parseObjectNext()
      parser.parseObjectKey("stmts")
      val stmts = parser.parseISZ(parse_astStmt _)
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Stmt.Object(isApp, extNameOpt, id, stmts, attr)
    }

    def parse_astStmtSig(): org.sireum.lang.ast.Stmt.Sig = {
      val r = parse_astStmtSigT(F)
      return r
    }

    def parse_astStmtSigT(typeParsed: B): org.sireum.lang.ast.Stmt.Sig = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.Sig")
      }
      parser.parseObjectKey("isImmutable")
      val isImmutable = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("isExt")
      val isExt = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("id")
      val id = parse_astId()
      parser.parseObjectNext()
      parser.parseObjectKey("typeParams")
      val typeParams = parser.parseISZ(parse_astTypeParam _)
      parser.parseObjectNext()
      parser.parseObjectKey("parents")
      val parents = parser.parseISZ(parse_astTypeNamed _)
      parser.parseObjectNext()
      parser.parseObjectKey("stmts")
      val stmts = parser.parseISZ(parse_astStmt _)
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Stmt.Sig(isImmutable, isExt, id, typeParams, parents, stmts, attr)
    }

    def parse_astStmtAdt(): org.sireum.lang.ast.Stmt.Adt = {
      val r = parse_astStmtAdtT(F)
      return r
    }

    def parse_astStmtAdtT(typeParsed: B): org.sireum.lang.ast.Stmt.Adt = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.Adt")
      }
      parser.parseObjectKey("isRoot")
      val isRoot = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("isDatatype")
      val isDatatype = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("id")
      val id = parse_astId()
      parser.parseObjectNext()
      parser.parseObjectKey("typeParams")
      val typeParams = parser.parseISZ(parse_astTypeParam _)
      parser.parseObjectNext()
      parser.parseObjectKey("params")
      val params = parser.parseISZ(parse_astAdtParam _)
      parser.parseObjectNext()
      parser.parseObjectKey("parents")
      val parents = parser.parseISZ(parse_astTypeNamed _)
      parser.parseObjectNext()
      parser.parseObjectKey("stmts")
      val stmts = parser.parseISZ(parse_astStmt _)
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Stmt.Adt(isRoot, isDatatype, id, typeParams, params, parents, stmts, attr)
    }

    def parse_astStmtTypeAlias(): org.sireum.lang.ast.Stmt.TypeAlias = {
      val r = parse_astStmtTypeAliasT(F)
      return r
    }

    def parse_astStmtTypeAliasT(typeParsed: B): org.sireum.lang.ast.Stmt.TypeAlias = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.TypeAlias")
      }
      parser.parseObjectKey("id")
      val id = parse_astId()
      parser.parseObjectNext()
      parser.parseObjectKey("typeParams")
      val typeParams = parser.parseISZ(parse_astTypeParam _)
      parser.parseObjectNext()
      parser.parseObjectKey("tipe")
      val tipe = parse_astType()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Stmt.TypeAlias(id, typeParams, tipe, attr)
    }

    def parse_astStmtAssign(): org.sireum.lang.ast.Stmt.Assign = {
      val r = parse_astStmtAssignT(F)
      return r
    }

    def parse_astStmtAssignT(typeParsed: B): org.sireum.lang.ast.Stmt.Assign = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.Assign")
      }
      parser.parseObjectKey("lhs")
      val lhs = parse_astExp()
      parser.parseObjectNext()
      parser.parseObjectKey("rhs")
      val rhs = parse_astAssignExp()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Stmt.Assign(lhs, rhs, attr)
    }

    def parse_astStmtBlock(): org.sireum.lang.ast.Stmt.Block = {
      val r = parse_astStmtBlockT(F)
      return r
    }

    def parse_astStmtBlockT(typeParsed: B): org.sireum.lang.ast.Stmt.Block = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.Block")
      }
      parser.parseObjectKey("body")
      val body = parse_astBody()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Stmt.Block(body, attr)
    }

    def parse_astStmtIf(): org.sireum.lang.ast.Stmt.If = {
      val r = parse_astStmtIfT(F)
      return r
    }

    def parse_astStmtIfT(typeParsed: B): org.sireum.lang.ast.Stmt.If = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.If")
      }
      parser.parseObjectKey("cond")
      val cond = parse_astExp()
      parser.parseObjectNext()
      parser.parseObjectKey("thenBody")
      val thenBody = parse_astBody()
      parser.parseObjectNext()
      parser.parseObjectKey("elseBody")
      val elseBody = parse_astBody()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Stmt.If(cond, thenBody, elseBody, attr)
    }

    def parse_astStmtMatch(): org.sireum.lang.ast.Stmt.Match = {
      val r = parse_astStmtMatchT(F)
      return r
    }

    def parse_astStmtMatchT(typeParsed: B): org.sireum.lang.ast.Stmt.Match = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.Match")
      }
      parser.parseObjectKey("exp")
      val exp = parse_astExp()
      parser.parseObjectNext()
      parser.parseObjectKey("cases")
      val cases = parser.parseISZ(parse_astCase _)
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Stmt.Match(exp, cases, attr)
    }

    def parse_astStmtLoop(): org.sireum.lang.ast.Stmt.Loop = {
      val t = parser.parseObjectTypes(ISZ("org.sireum.lang.ast.Stmt.While", "org.sireum.lang.ast.Stmt.DoWhile", "org.sireum.lang.ast.Stmt.For"))
      t.native match {
        case "org.sireum.lang.ast.Stmt.While" => val r = parse_astStmtWhileT(T); return r
        case "org.sireum.lang.ast.Stmt.DoWhile" => val r = parse_astStmtDoWhileT(T); return r
        case "org.sireum.lang.ast.Stmt.For" => val r = parse_astStmtForT(T); return r
        case _ => val r = parse_astStmtForT(T); return r
      }
    }

    def parse_astStmtWhile(): org.sireum.lang.ast.Stmt.While = {
      val r = parse_astStmtWhileT(F)
      return r
    }

    def parse_astStmtWhileT(typeParsed: B): org.sireum.lang.ast.Stmt.While = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.While")
      }
      parser.parseObjectKey("context")
      val context = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("cond")
      val cond = parse_astExp()
      parser.parseObjectNext()
      parser.parseObjectKey("invariants")
      val invariants = parser.parseISZ(parse_astExp _)
      parser.parseObjectNext()
      parser.parseObjectKey("modifies")
      val modifies = parser.parseISZ(parse_astExpIdent _)
      parser.parseObjectNext()
      parser.parseObjectKey("body")
      val body = parse_astBody()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Stmt.While(context, cond, invariants, modifies, body, attr)
    }

    def parse_astStmtDoWhile(): org.sireum.lang.ast.Stmt.DoWhile = {
      val r = parse_astStmtDoWhileT(F)
      return r
    }

    def parse_astStmtDoWhileT(typeParsed: B): org.sireum.lang.ast.Stmt.DoWhile = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.DoWhile")
      }
      parser.parseObjectKey("context")
      val context = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("cond")
      val cond = parse_astExp()
      parser.parseObjectNext()
      parser.parseObjectKey("invariants")
      val invariants = parser.parseISZ(parse_astExp _)
      parser.parseObjectNext()
      parser.parseObjectKey("modifies")
      val modifies = parser.parseISZ(parse_astExpIdent _)
      parser.parseObjectNext()
      parser.parseObjectKey("body")
      val body = parse_astBody()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Stmt.DoWhile(context, cond, invariants, modifies, body, attr)
    }

    def parse_astStmtFor(): org.sireum.lang.ast.Stmt.For = {
      val r = parse_astStmtForT(F)
      return r
    }

    def parse_astStmtForT(typeParsed: B): org.sireum.lang.ast.Stmt.For = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.For")
      }
      parser.parseObjectKey("context")
      val context = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("enumGens")
      val enumGens = parser.parseISZ(parse_astEnumGenFor _)
      parser.parseObjectNext()
      parser.parseObjectKey("invariants")
      val invariants = parser.parseISZ(parse_astExp _)
      parser.parseObjectNext()
      parser.parseObjectKey("modifies")
      val modifies = parser.parseISZ(parse_astExpIdent _)
      parser.parseObjectNext()
      parser.parseObjectKey("body")
      val body = parse_astBody()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Stmt.For(context, enumGens, invariants, modifies, body, attr)
    }

    def parse_astStmtReturn(): org.sireum.lang.ast.Stmt.Return = {
      val r = parse_astStmtReturnT(F)
      return r
    }

    def parse_astStmtReturnT(typeParsed: B): org.sireum.lang.ast.Stmt.Return = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.Return")
      }
      parser.parseObjectKey("expOpt")
      val expOpt = parser.parseOption(parse_astExp _)
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astTypedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Stmt.Return(expOpt, attr)
    }

    def parse_astStmtExpr(): org.sireum.lang.ast.Stmt.Expr = {
      val r = parse_astStmtExprT(F)
      return r
    }

    def parse_astStmtExprT(typeParsed: B): org.sireum.lang.ast.Stmt.Expr = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.Expr")
      }
      parser.parseObjectKey("exp")
      val exp = parse_astExp()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astTypedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Stmt.Expr(exp, attr)
    }

    def parse_astStmtSpec(): org.sireum.lang.ast.Stmt.Spec = {
      val t = parser.parseObjectTypes(ISZ("org.sireum.lang.ast.Stmt.Fact", "org.sireum.lang.ast.Stmt.Invariant", "org.sireum.lang.ast.Stmt.Theorem", "org.sireum.lang.ast.Stmt.SpecLabel", "org.sireum.lang.ast.Stmt.SpecBlock", "org.sireum.lang.ast.Stmt.Deduce", "org.sireum.lang.ast.Stmt.DeduceSteps"))
      t.native match {
        case "org.sireum.lang.ast.Stmt.Fact" => val r = parse_astStmtFactT(T); return r
        case "org.sireum.lang.ast.Stmt.Invariant" => val r = parse_astStmtInvariantT(T); return r
        case "org.sireum.lang.ast.Stmt.Theorem" => val r = parse_astStmtTheoremT(T); return r
        case "org.sireum.lang.ast.Stmt.SpecLabel" => val r = parse_astStmtSpecLabelT(T); return r
        case "org.sireum.lang.ast.Stmt.SpecBlock" => val r = parse_astStmtSpecBlockT(T); return r
        case "org.sireum.lang.ast.Stmt.Deduce" => val r = parse_astStmtDeduceT(T); return r
        case "org.sireum.lang.ast.Stmt.DeduceSteps" => val r = parse_astStmtDeduceStepsT(T); return r
        case _ => val r = parse_astStmtDeduceStepsT(T); return r
      }
    }

    def parse_astStmtFact(): org.sireum.lang.ast.Stmt.Fact = {
      val r = parse_astStmtFactT(F)
      return r
    }

    def parse_astStmtFactT(typeParsed: B): org.sireum.lang.ast.Stmt.Fact = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.Fact")
      }
      parser.parseObjectKey("id")
      val id = parse_astId()
      parser.parseObjectNext()
      parser.parseObjectKey("typeArgs")
      val typeArgs = parser.parseISZ(parse_astTypeParam _)
      parser.parseObjectNext()
      parser.parseObjectKey("descOpt")
      val descOpt = parser.parseOption(parse_astExpLitString _)
      parser.parseObjectNext()
      parser.parseObjectKey("claims")
      val claims = parser.parseISZ(parse_astExp _)
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Stmt.Fact(id, typeArgs, descOpt, claims, attr)
    }

    def parse_astStmtInvariant(): org.sireum.lang.ast.Stmt.Invariant = {
      val r = parse_astStmtInvariantT(F)
      return r
    }

    def parse_astStmtInvariantT(typeParsed: B): org.sireum.lang.ast.Stmt.Invariant = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.Invariant")
      }
      parser.parseObjectKey("id")
      val id = parse_astId()
      parser.parseObjectNext()
      parser.parseObjectKey("claims")
      val claims = parser.parseISZ(parse_astExp _)
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Stmt.Invariant(id, claims, attr)
    }

    def parse_astStmtTheorem(): org.sireum.lang.ast.Stmt.Theorem = {
      val r = parse_astStmtTheoremT(F)
      return r
    }

    def parse_astStmtTheoremT(typeParsed: B): org.sireum.lang.ast.Stmt.Theorem = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.Theorem")
      }
      parser.parseObjectKey("isLemma")
      val isLemma = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("id")
      val id = parse_astId()
      parser.parseObjectNext()
      parser.parseObjectKey("typeArgs")
      val typeArgs = parser.parseISZ(parse_astTypeParam _)
      parser.parseObjectNext()
      parser.parseObjectKey("descOpt")
      val descOpt = parser.parseOption(parse_astExpLitString _)
      parser.parseObjectNext()
      parser.parseObjectKey("claim")
      val claim = parse_astExp()
      parser.parseObjectNext()
      parser.parseObjectKey("proof")
      val proof = parse_astProof()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Stmt.Theorem(isLemma, id, typeArgs, descOpt, claim, proof, attr)
    }

    def parse_astStmtSpecLabel(): org.sireum.lang.ast.Stmt.SpecLabel = {
      val r = parse_astStmtSpecLabelT(F)
      return r
    }

    def parse_astStmtSpecLabelT(typeParsed: B): org.sireum.lang.ast.Stmt.SpecLabel = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.SpecLabel")
      }
      parser.parseObjectKey("id")
      val id = parse_astId()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Stmt.SpecLabel(id)
    }

    def parse_astStmtSpecBlock(): org.sireum.lang.ast.Stmt.SpecBlock = {
      val r = parse_astStmtSpecBlockT(F)
      return r
    }

    def parse_astStmtSpecBlockT(typeParsed: B): org.sireum.lang.ast.Stmt.SpecBlock = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.SpecBlock")
      }
      parser.parseObjectKey("block")
      val block = parse_astStmtBlock()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Stmt.SpecBlock(block)
    }

    def parse_astStmtDeduce(): org.sireum.lang.ast.Stmt.Deduce = {
      val r = parse_astStmtDeduceT(F)
      return r
    }

    def parse_astStmtDeduceT(typeParsed: B): org.sireum.lang.ast.Stmt.Deduce = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.Deduce")
      }
      parser.parseObjectKey("sequents")
      val sequents = parser.parseISZ(parse_astSequent _)
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Stmt.Deduce(sequents, attr)
    }

    def parse_astStmtDeduceSteps(): org.sireum.lang.ast.Stmt.DeduceSteps = {
      val r = parse_astStmtDeduceStepsT(F)
      return r
    }

    def parse_astStmtDeduceStepsT(typeParsed: B): org.sireum.lang.ast.Stmt.DeduceSteps = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Stmt.DeduceSteps")
      }
      parser.parseObjectKey("steps")
      val steps = parser.parseISZ(parse_astProofStep _)
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Stmt.DeduceSteps(steps, attr)
    }

    def parse_astMethodContract(): org.sireum.lang.ast.MethodContract = {
      val t = parser.parseObjectTypes(ISZ("org.sireum.lang.ast.MethodContract.Simple", "org.sireum.lang.ast.MethodContract.Cases"))
      t.native match {
        case "org.sireum.lang.ast.MethodContract.Simple" => val r = parse_astMethodContractSimpleT(T); return r
        case "org.sireum.lang.ast.MethodContract.Cases" => val r = parse_astMethodContractCasesT(T); return r
        case _ => val r = parse_astMethodContractCasesT(T); return r
      }
    }

    def parse_astMethodContractSimple(): org.sireum.lang.ast.MethodContract.Simple = {
      val r = parse_astMethodContractSimpleT(F)
      return r
    }

    def parse_astMethodContractSimpleT(typeParsed: B): org.sireum.lang.ast.MethodContract.Simple = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.MethodContract.Simple")
      }
      parser.parseObjectKey("reads")
      val reads = parser.parseISZ(parse_astExpIdent _)
      parser.parseObjectNext()
      parser.parseObjectKey("requires")
      val requires = parser.parseISZ(parse_astExp _)
      parser.parseObjectNext()
      parser.parseObjectKey("modifies")
      val modifies = parser.parseISZ(parse_astExpIdent _)
      parser.parseObjectNext()
      parser.parseObjectKey("ensures")
      val ensures = parser.parseISZ(parse_astExp _)
      parser.parseObjectNext()
      return org.sireum.lang.ast.MethodContract.Simple(reads, requires, modifies, ensures)
    }

    def parse_astMethodContractCases(): org.sireum.lang.ast.MethodContract.Cases = {
      val r = parse_astMethodContractCasesT(F)
      return r
    }

    def parse_astMethodContractCasesT(typeParsed: B): org.sireum.lang.ast.MethodContract.Cases = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.MethodContract.Cases")
      }
      parser.parseObjectKey("reads")
      val reads = parser.parseISZ(parse_astExpIdent _)
      parser.parseObjectNext()
      parser.parseObjectKey("modifies")
      val modifies = parser.parseISZ(parse_astExpIdent _)
      parser.parseObjectNext()
      parser.parseObjectKey("cases")
      val cases = parser.parseISZ(parse_astMethodContractCase _)
      parser.parseObjectNext()
      return org.sireum.lang.ast.MethodContract.Cases(reads, modifies, cases)
    }

    def parse_astMethodContractCase(): org.sireum.lang.ast.MethodContract.Case = {
      val r = parse_astMethodContractCaseT(F)
      return r
    }

    def parse_astMethodContractCaseT(typeParsed: B): org.sireum.lang.ast.MethodContract.Case = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.MethodContract.Case")
      }
      parser.parseObjectKey("label")
      val label = parser.parseString()
      parser.parseObjectNext()
      parser.parseObjectKey("requires")
      val requires = parser.parseISZ(parse_astExp _)
      parser.parseObjectNext()
      parser.parseObjectKey("ensures")
      val ensures = parser.parseISZ(parse_astExp _)
      parser.parseObjectNext()
      return org.sireum.lang.ast.MethodContract.Case(label, requires, ensures)
    }

    def parse_astSequent(): org.sireum.lang.ast.Sequent = {
      val r = parse_astSequentT(F)
      return r
    }

    def parse_astSequentT(typeParsed: B): org.sireum.lang.ast.Sequent = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Sequent")
      }
      parser.parseObjectKey("premises")
      val premises = parser.parseISZ(parse_astExp _)
      parser.parseObjectNext()
      parser.parseObjectKey("conclusion")
      val conclusion = parse_astExp()
      parser.parseObjectNext()
      parser.parseObjectKey("steps")
      val steps = parser.parseISZ(parse_astProofStep _)
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Sequent(premises, conclusion, steps, attr)
    }

    def parse_astProof(): org.sireum.lang.ast.Proof = {
      val r = parse_astProofT(F)
      return r
    }

    def parse_astProofT(typeParsed: B): org.sireum.lang.ast.Proof = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Proof")
      }
      parser.parseObjectKey("steps")
      val steps = parser.parseISZ(parse_astProofStep _)
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Proof(steps, attr)
    }

    def parse_astProofStep(): org.sireum.lang.ast.Proof.Step = {
      val t = parser.parseObjectTypes(ISZ("org.sireum.lang.ast.Proof.Step.Regular", "org.sireum.lang.ast.Proof.Step.Assume", "org.sireum.lang.ast.Proof.Step.Assert", "org.sireum.lang.ast.Proof.Step.SubProof", "org.sireum.lang.ast.Proof.Step.Let", "org.sireum.lang.ast.Proof.Step.StructInduction"))
      t.native match {
        case "org.sireum.lang.ast.Proof.Step.Regular" => val r = parse_astProofStepRegularT(T); return r
        case "org.sireum.lang.ast.Proof.Step.Assume" => val r = parse_astProofStepAssumeT(T); return r
        case "org.sireum.lang.ast.Proof.Step.Assert" => val r = parse_astProofStepAssertT(T); return r
        case "org.sireum.lang.ast.Proof.Step.SubProof" => val r = parse_astProofStepSubProofT(T); return r
        case "org.sireum.lang.ast.Proof.Step.Let" => val r = parse_astProofStepLetT(T); return r
        case "org.sireum.lang.ast.Proof.Step.StructInduction" => val r = parse_astProofStepStructInductionT(T); return r
        case _ => val r = parse_astProofStepStructInductionT(T); return r
      }
    }

    def parse_astProofStepRegular(): org.sireum.lang.ast.Proof.Step.Regular = {
      val r = parse_astProofStepRegularT(F)
      return r
    }

    def parse_astProofStepRegularT(typeParsed: B): org.sireum.lang.ast.Proof.Step.Regular = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Proof.Step.Regular")
      }
      parser.parseObjectKey("no")
      val no = parse_astExpLitZ()
      parser.parseObjectNext()
      parser.parseObjectKey("claim")
      val claim = parse_astExp()
      parser.parseObjectNext()
      parser.parseObjectKey("just")
      val just = parse_astProofStepJustification()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Proof.Step.Regular(no, claim, just)
    }

    def parse_astProofStepAssume(): org.sireum.lang.ast.Proof.Step.Assume = {
      val r = parse_astProofStepAssumeT(F)
      return r
    }

    def parse_astProofStepAssumeT(typeParsed: B): org.sireum.lang.ast.Proof.Step.Assume = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Proof.Step.Assume")
      }
      parser.parseObjectKey("no")
      val no = parse_astExpLitZ()
      parser.parseObjectNext()
      parser.parseObjectKey("claim")
      val claim = parse_astExp()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Proof.Step.Assume(no, claim)
    }

    def parse_astProofStepAssert(): org.sireum.lang.ast.Proof.Step.Assert = {
      val r = parse_astProofStepAssertT(F)
      return r
    }

    def parse_astProofStepAssertT(typeParsed: B): org.sireum.lang.ast.Proof.Step.Assert = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Proof.Step.Assert")
      }
      parser.parseObjectKey("no")
      val no = parse_astExpLitZ()
      parser.parseObjectNext()
      parser.parseObjectKey("claim")
      val claim = parse_astExp()
      parser.parseObjectNext()
      parser.parseObjectKey("steps")
      val steps = parser.parseISZ(parse_astProofStep _)
      parser.parseObjectNext()
      return org.sireum.lang.ast.Proof.Step.Assert(no, claim, steps)
    }

    def parse_astProofStepSubProof(): org.sireum.lang.ast.Proof.Step.SubProof = {
      val r = parse_astProofStepSubProofT(F)
      return r
    }

    def parse_astProofStepSubProofT(typeParsed: B): org.sireum.lang.ast.Proof.Step.SubProof = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Proof.Step.SubProof")
      }
      parser.parseObjectKey("no")
      val no = parse_astExpLitZ()
      parser.parseObjectNext()
      parser.parseObjectKey("steps")
      val steps = parser.parseISZ(parse_astProofStep _)
      parser.parseObjectNext()
      return org.sireum.lang.ast.Proof.Step.SubProof(no, steps)
    }

    def parse_astProofStepLet(): org.sireum.lang.ast.Proof.Step.Let = {
      val r = parse_astProofStepLetT(F)
      return r
    }

    def parse_astProofStepLetT(typeParsed: B): org.sireum.lang.ast.Proof.Step.Let = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Proof.Step.Let")
      }
      parser.parseObjectKey("no")
      val no = parse_astExpLitZ()
      parser.parseObjectNext()
      parser.parseObjectKey("params")
      val params = parser.parseISZ(parse_astProofStepLetParam _)
      parser.parseObjectNext()
      parser.parseObjectKey("steps")
      val steps = parser.parseISZ(parse_astProofStep _)
      parser.parseObjectNext()
      return org.sireum.lang.ast.Proof.Step.Let(no, params, steps)
    }

    def parse_astProofStepLetParam(): org.sireum.lang.ast.Proof.Step.Let.Param = {
      val r = parse_astProofStepLetParamT(F)
      return r
    }

    def parse_astProofStepLetParamT(typeParsed: B): org.sireum.lang.ast.Proof.Step.Let.Param = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Proof.Step.Let.Param")
      }
      parser.parseObjectKey("id")
      val id = parse_astId()
      parser.parseObjectNext()
      parser.parseObjectKey("tipeOpt")
      val tipeOpt = parser.parseOption(parse_astType _)
      parser.parseObjectNext()
      return org.sireum.lang.ast.Proof.Step.Let.Param(id, tipeOpt)
    }

    def parse_astProofStepStructInduction(): org.sireum.lang.ast.Proof.Step.StructInduction = {
      val r = parse_astProofStepStructInductionT(F)
      return r
    }

    def parse_astProofStepStructInductionT(typeParsed: B): org.sireum.lang.ast.Proof.Step.StructInduction = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Proof.Step.StructInduction")
      }
      parser.parseObjectKey("no")
      val no = parse_astExpLitZ()
      parser.parseObjectNext()
      parser.parseObjectKey("claim")
      val claim = parse_astExp()
      parser.parseObjectNext()
      parser.parseObjectKey("exp")
      val exp = parse_astExp()
      parser.parseObjectNext()
      parser.parseObjectKey("cases")
      val cases = parser.parseISZ(parse_astProofStepStructInductionMatchCase _)
      parser.parseObjectNext()
      parser.parseObjectKey("defaultOpt")
      val defaultOpt = parser.parseOption(parse_astProofStepStructInductionMatchDefault _)
      parser.parseObjectNext()
      return org.sireum.lang.ast.Proof.Step.StructInduction(no, claim, exp, cases, defaultOpt)
    }

    def parse_astProofStepStructInductionMatchCase(): org.sireum.lang.ast.Proof.Step.StructInduction.MatchCase = {
      val r = parse_astProofStepStructInductionMatchCaseT(F)
      return r
    }

    def parse_astProofStepStructInductionMatchCaseT(typeParsed: B): org.sireum.lang.ast.Proof.Step.StructInduction.MatchCase = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Proof.Step.StructInduction.MatchCase")
      }
      parser.parseObjectKey("pattern")
      val pattern = parse_astPatternStructure()
      parser.parseObjectNext()
      parser.parseObjectKey("hypoOpt")
      val hypoOpt = parser.parseOption(parse_astProofStepAssume _)
      parser.parseObjectNext()
      parser.parseObjectKey("steps")
      val steps = parser.parseISZ(parse_astProofStep _)
      parser.parseObjectNext()
      return org.sireum.lang.ast.Proof.Step.StructInduction.MatchCase(pattern, hypoOpt, steps)
    }

    def parse_astProofStepStructInductionMatchDefault(): org.sireum.lang.ast.Proof.Step.StructInduction.MatchDefault = {
      val r = parse_astProofStepStructInductionMatchDefaultT(F)
      return r
    }

    def parse_astProofStepStructInductionMatchDefaultT(typeParsed: B): org.sireum.lang.ast.Proof.Step.StructInduction.MatchDefault = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Proof.Step.StructInduction.MatchDefault")
      }
      parser.parseObjectKey("hypoOpt")
      val hypoOpt = parser.parseOption(parse_astProofStepAssume _)
      parser.parseObjectNext()
      parser.parseObjectKey("steps")
      val steps = parser.parseISZ(parse_astProofStep _)
      parser.parseObjectNext()
      return org.sireum.lang.ast.Proof.Step.StructInduction.MatchDefault(hypoOpt, steps)
    }

    def parse_astProofStepJustification(): org.sireum.lang.ast.Proof.Step.Justification = {
      val r = parse_astProofStepJustificationT(F)
      return r
    }

    def parse_astProofStepJustificationT(typeParsed: B): org.sireum.lang.ast.Proof.Step.Justification = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Proof.Step.Justification")
      }
      parser.parseObjectKey("id")
      val id = parse_astId()
      parser.parseObjectNext()
      parser.parseObjectKey("args")
      val args = parser.parseISZ(parse_astExp _)
      parser.parseObjectNext()
      return org.sireum.lang.ast.Proof.Step.Justification(id, args)
    }

    def parse_astAssignExp(): org.sireum.lang.ast.AssignExp = {
      val t = parser.parseObjectTypes(ISZ("org.sireum.lang.ast.Stmt.Block", "org.sireum.lang.ast.Stmt.If", "org.sireum.lang.ast.Stmt.Match", "org.sireum.lang.ast.Stmt.Return", "org.sireum.lang.ast.Stmt.Expr"))
      t.native match {
        case "org.sireum.lang.ast.Stmt.Block" => val r = parse_astStmtBlockT(T); return r
        case "org.sireum.lang.ast.Stmt.If" => val r = parse_astStmtIfT(T); return r
        case "org.sireum.lang.ast.Stmt.Match" => val r = parse_astStmtMatchT(T); return r
        case "org.sireum.lang.ast.Stmt.Return" => val r = parse_astStmtReturnT(T); return r
        case "org.sireum.lang.ast.Stmt.Expr" => val r = parse_astStmtExprT(T); return r
        case _ => val r = parse_astStmtExprT(T); return r
      }
    }

    def parse_astPurityType(): org.sireum.lang.ast.Purity.Type = {
      val r = parse_astPurityT(F)
      return r
    }

    def parse_astPurityT(typeParsed: B): org.sireum.lang.ast.Purity.Type = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Purity")
      }
      parser.parseObjectKey("value")
      var i = parser.offset
      val s = parser.parseString()
      parser.parseObjectNext()
      org.sireum.lang.ast.Purity.byName(s) match {
        case Some(r) => return r
        case _ =>
          parser.parseException(i, s"Invalid element name '$s' for org.sireum.lang.ast.Purity.")
          return org.sireum.lang.ast.Purity.byOrdinal(0).get
      }
    }

    def parse_astCase(): org.sireum.lang.ast.Case = {
      val r = parse_astCaseT(F)
      return r
    }

    def parse_astCaseT(typeParsed: B): org.sireum.lang.ast.Case = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Case")
      }
      parser.parseObjectKey("pattern")
      val pattern = parse_astPattern()
      parser.parseObjectNext()
      parser.parseObjectKey("condOpt")
      val condOpt = parser.parseOption(parse_astExp _)
      parser.parseObjectNext()
      parser.parseObjectKey("body")
      val body = parse_astBody()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Case(pattern, condOpt, body)
    }

    def parse_astEnumGenRange(): org.sireum.lang.ast.EnumGen.Range = {
      val t = parser.parseObjectTypes(ISZ("org.sireum.lang.ast.EnumGen.Range.Expr", "org.sireum.lang.ast.EnumGen.Range.Step"))
      t.native match {
        case "org.sireum.lang.ast.EnumGen.Range.Expr" => val r = parse_astEnumGenRangeExprT(T); return r
        case "org.sireum.lang.ast.EnumGen.Range.Step" => val r = parse_astEnumGenRangeStepT(T); return r
        case _ => val r = parse_astEnumGenRangeStepT(T); return r
      }
    }

    def parse_astEnumGenRangeExpr(): org.sireum.lang.ast.EnumGen.Range.Expr = {
      val r = parse_astEnumGenRangeExprT(F)
      return r
    }

    def parse_astEnumGenRangeExprT(typeParsed: B): org.sireum.lang.ast.EnumGen.Range.Expr = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.EnumGen.Range.Expr")
      }
      parser.parseObjectKey("exp")
      val exp = parse_astExp()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.EnumGen.Range.Expr(exp, attr)
    }

    def parse_astEnumGenRangeStep(): org.sireum.lang.ast.EnumGen.Range.Step = {
      val r = parse_astEnumGenRangeStepT(F)
      return r
    }

    def parse_astEnumGenRangeStepT(typeParsed: B): org.sireum.lang.ast.EnumGen.Range.Step = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.EnumGen.Range.Step")
      }
      parser.parseObjectKey("isInclusive")
      val isInclusive = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("start")
      val start = parse_astExp()
      parser.parseObjectNext()
      parser.parseObjectKey("end")
      val end = parse_astExp()
      parser.parseObjectNext()
      parser.parseObjectKey("byOpt")
      val byOpt = parser.parseOption(parse_astExp _)
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.EnumGen.Range.Step(isInclusive, start, end, byOpt, attr)
    }

    def parse_astEnumGenFor(): org.sireum.lang.ast.EnumGen.For = {
      val r = parse_astEnumGenForT(F)
      return r
    }

    def parse_astEnumGenForT(typeParsed: B): org.sireum.lang.ast.EnumGen.For = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.EnumGen.For")
      }
      parser.parseObjectKey("idOpt")
      val idOpt = parser.parseOption(parse_astId _)
      parser.parseObjectNext()
      parser.parseObjectKey("range")
      val range = parse_astEnumGenRange()
      parser.parseObjectNext()
      parser.parseObjectKey("condOpt")
      val condOpt = parser.parseOption(parse_astExp _)
      parser.parseObjectNext()
      return org.sireum.lang.ast.EnumGen.For(idOpt, range, condOpt)
    }

    def parse_astType(): org.sireum.lang.ast.Type = {
      val t = parser.parseObjectTypes(ISZ("org.sireum.lang.ast.Type.Named", "org.sireum.lang.ast.Type.Fun", "org.sireum.lang.ast.Type.Tuple"))
      t.native match {
        case "org.sireum.lang.ast.Type.Named" => val r = parse_astTypeNamedT(T); return r
        case "org.sireum.lang.ast.Type.Fun" => val r = parse_astTypeFunT(T); return r
        case "org.sireum.lang.ast.Type.Tuple" => val r = parse_astTypeTupleT(T); return r
        case _ => val r = parse_astTypeTupleT(T); return r
      }
    }

    def parse_astTypeNamed(): org.sireum.lang.ast.Type.Named = {
      val r = parse_astTypeNamedT(F)
      return r
    }

    def parse_astTypeNamedT(typeParsed: B): org.sireum.lang.ast.Type.Named = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Type.Named")
      }
      parser.parseObjectKey("name")
      val name = parse_astName()
      parser.parseObjectNext()
      parser.parseObjectKey("typeArgs")
      val typeArgs = parser.parseISZ(parse_astType _)
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astTypedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Type.Named(name, typeArgs, attr)
    }

    def parse_astTypeFun(): org.sireum.lang.ast.Type.Fun = {
      val r = parse_astTypeFunT(F)
      return r
    }

    def parse_astTypeFunT(typeParsed: B): org.sireum.lang.ast.Type.Fun = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Type.Fun")
      }
      parser.parseObjectKey("isPure")
      val isPure = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("isByName")
      val isByName = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("args")
      val args = parser.parseISZ(parse_astType _)
      parser.parseObjectNext()
      parser.parseObjectKey("ret")
      val ret = parse_astType()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astTypedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Type.Fun(isPure, isByName, args, ret, attr)
    }

    def parse_astTypeTuple(): org.sireum.lang.ast.Type.Tuple = {
      val r = parse_astTypeTupleT(F)
      return r
    }

    def parse_astTypeTupleT(typeParsed: B): org.sireum.lang.ast.Type.Tuple = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Type.Tuple")
      }
      parser.parseObjectKey("args")
      val args = parser.parseISZ(parse_astType _)
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astTypedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Type.Tuple(args, attr)
    }

    def parse_astPattern(): org.sireum.lang.ast.Pattern = {
      val t = parser.parseObjectTypes(ISZ("org.sireum.lang.ast.Pattern.Literal", "org.sireum.lang.ast.Pattern.LitInterpolate", "org.sireum.lang.ast.Pattern.Ref", "org.sireum.lang.ast.Pattern.VarBinding", "org.sireum.lang.ast.Pattern.Wildcard", "org.sireum.lang.ast.Pattern.SeqWildcard", "org.sireum.lang.ast.Pattern.Structure"))
      t.native match {
        case "org.sireum.lang.ast.Pattern.Literal" => val r = parse_astPatternLiteralT(T); return r
        case "org.sireum.lang.ast.Pattern.LitInterpolate" => val r = parse_astPatternLitInterpolateT(T); return r
        case "org.sireum.lang.ast.Pattern.Ref" => val r = parse_astPatternRefT(T); return r
        case "org.sireum.lang.ast.Pattern.VarBinding" => val r = parse_astPatternVarBindingT(T); return r
        case "org.sireum.lang.ast.Pattern.Wildcard" => val r = parse_astPatternWildcardT(T); return r
        case "org.sireum.lang.ast.Pattern.SeqWildcard" => val r = parse_astPatternSeqWildcardT(T); return r
        case "org.sireum.lang.ast.Pattern.Structure" => val r = parse_astPatternStructureT(T); return r
        case _ => val r = parse_astPatternStructureT(T); return r
      }
    }

    def parse_astPatternLiteral(): org.sireum.lang.ast.Pattern.Literal = {
      val r = parse_astPatternLiteralT(F)
      return r
    }

    def parse_astPatternLiteralT(typeParsed: B): org.sireum.lang.ast.Pattern.Literal = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Pattern.Literal")
      }
      parser.parseObjectKey("lit")
      val lit = parse_astLit()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Pattern.Literal(lit)
    }

    def parse_astPatternLitInterpolate(): org.sireum.lang.ast.Pattern.LitInterpolate = {
      val r = parse_astPatternLitInterpolateT(F)
      return r
    }

    def parse_astPatternLitInterpolateT(typeParsed: B): org.sireum.lang.ast.Pattern.LitInterpolate = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Pattern.LitInterpolate")
      }
      parser.parseObjectKey("prefix")
      val prefix = parser.parseString()
      parser.parseObjectNext()
      parser.parseObjectKey("value")
      val value = parser.parseString()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astTypedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Pattern.LitInterpolate(prefix, value, attr)
    }

    def parse_astPatternRef(): org.sireum.lang.ast.Pattern.Ref = {
      val r = parse_astPatternRefT(F)
      return r
    }

    def parse_astPatternRefT(typeParsed: B): org.sireum.lang.ast.Pattern.Ref = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Pattern.Ref")
      }
      parser.parseObjectKey("name")
      val name = parse_astName()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astResolvedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Pattern.Ref(name, attr)
    }

    def parse_astPatternVarBinding(): org.sireum.lang.ast.Pattern.VarBinding = {
      val r = parse_astPatternVarBindingT(F)
      return r
    }

    def parse_astPatternVarBindingT(typeParsed: B): org.sireum.lang.ast.Pattern.VarBinding = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Pattern.VarBinding")
      }
      parser.parseObjectKey("id")
      val id = parse_astId()
      parser.parseObjectNext()
      parser.parseObjectKey("tipeOpt")
      val tipeOpt = parser.parseOption(parse_astType _)
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astTypedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Pattern.VarBinding(id, tipeOpt, attr)
    }

    def parse_astPatternWildcard(): org.sireum.lang.ast.Pattern.Wildcard = {
      val r = parse_astPatternWildcardT(F)
      return r
    }

    def parse_astPatternWildcardT(typeParsed: B): org.sireum.lang.ast.Pattern.Wildcard = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Pattern.Wildcard")
      }
      parser.parseObjectKey("typeOpt")
      val typeOpt = parser.parseOption(parse_astType _)
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astTypedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Pattern.Wildcard(typeOpt, attr)
    }

    def parse_astPatternSeqWildcard(): org.sireum.lang.ast.Pattern.SeqWildcard = {
      val r = parse_astPatternSeqWildcardT(F)
      return r
    }

    def parse_astPatternSeqWildcardT(typeParsed: B): org.sireum.lang.ast.Pattern.SeqWildcard = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Pattern.SeqWildcard")
      }
      parser.parseObjectKey("attr")
      val attr = parse_astTypedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Pattern.SeqWildcard(attr)
    }

    def parse_astPatternStructure(): org.sireum.lang.ast.Pattern.Structure = {
      val r = parse_astPatternStructureT(F)
      return r
    }

    def parse_astPatternStructureT(typeParsed: B): org.sireum.lang.ast.Pattern.Structure = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Pattern.Structure")
      }
      parser.parseObjectKey("idOpt")
      val idOpt = parser.parseOption(parse_astId _)
      parser.parseObjectNext()
      parser.parseObjectKey("nameOpt")
      val nameOpt = parser.parseOption(parse_astName _)
      parser.parseObjectNext()
      parser.parseObjectKey("patterns")
      val patterns = parser.parseISZ(parse_astPattern _)
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astResolvedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Pattern.Structure(idOpt, nameOpt, patterns, attr)
    }

    def parse_astExp(): org.sireum.lang.ast.Exp = {
      val t = parser.parseObjectTypes(ISZ("org.sireum.lang.ast.Exp.LitB", "org.sireum.lang.ast.Exp.LitC", "org.sireum.lang.ast.Exp.LitZ", "org.sireum.lang.ast.Exp.LitF32", "org.sireum.lang.ast.Exp.LitF64", "org.sireum.lang.ast.Exp.LitR", "org.sireum.lang.ast.Exp.LitString", "org.sireum.lang.ast.Exp.StringInterpolate", "org.sireum.lang.ast.Exp.This", "org.sireum.lang.ast.Exp.Super", "org.sireum.lang.ast.Exp.Unary", "org.sireum.lang.ast.Exp.Binary", "org.sireum.lang.ast.Exp.Ident", "org.sireum.lang.ast.Exp.Eta", "org.sireum.lang.ast.Exp.Tuple", "org.sireum.lang.ast.Exp.Select", "org.sireum.lang.ast.Exp.Invoke", "org.sireum.lang.ast.Exp.InvokeNamed", "org.sireum.lang.ast.Exp.If", "org.sireum.lang.ast.Exp.Fun", "org.sireum.lang.ast.Exp.ForYield", "org.sireum.lang.ast.Exp.Quant"))
      t.native match {
        case "org.sireum.lang.ast.Exp.LitB" => val r = parse_astExpLitBT(T); return r
        case "org.sireum.lang.ast.Exp.LitC" => val r = parse_astExpLitCT(T); return r
        case "org.sireum.lang.ast.Exp.LitZ" => val r = parse_astExpLitZT(T); return r
        case "org.sireum.lang.ast.Exp.LitF32" => val r = parse_astExpLitF32T(T); return r
        case "org.sireum.lang.ast.Exp.LitF64" => val r = parse_astExpLitF64T(T); return r
        case "org.sireum.lang.ast.Exp.LitR" => val r = parse_astExpLitRT(T); return r
        case "org.sireum.lang.ast.Exp.LitString" => val r = parse_astExpLitStringT(T); return r
        case "org.sireum.lang.ast.Exp.StringInterpolate" => val r = parse_astExpStringInterpolateT(T); return r
        case "org.sireum.lang.ast.Exp.This" => val r = parse_astExpThisT(T); return r
        case "org.sireum.lang.ast.Exp.Super" => val r = parse_astExpSuperT(T); return r
        case "org.sireum.lang.ast.Exp.Unary" => val r = parse_astExpUnaryT(T); return r
        case "org.sireum.lang.ast.Exp.Binary" => val r = parse_astExpBinaryT(T); return r
        case "org.sireum.lang.ast.Exp.Ident" => val r = parse_astExpIdentT(T); return r
        case "org.sireum.lang.ast.Exp.Eta" => val r = parse_astExpEtaT(T); return r
        case "org.sireum.lang.ast.Exp.Tuple" => val r = parse_astExpTupleT(T); return r
        case "org.sireum.lang.ast.Exp.Select" => val r = parse_astExpSelectT(T); return r
        case "org.sireum.lang.ast.Exp.Invoke" => val r = parse_astExpInvokeT(T); return r
        case "org.sireum.lang.ast.Exp.InvokeNamed" => val r = parse_astExpInvokeNamedT(T); return r
        case "org.sireum.lang.ast.Exp.If" => val r = parse_astExpIfT(T); return r
        case "org.sireum.lang.ast.Exp.Fun" => val r = parse_astExpFunT(T); return r
        case "org.sireum.lang.ast.Exp.ForYield" => val r = parse_astExpForYieldT(T); return r
        case "org.sireum.lang.ast.Exp.Quant" => val r = parse_astExpQuantT(T); return r
        case _ => val r = parse_astExpQuantT(T); return r
      }
    }

    def parse_astLit(): org.sireum.lang.ast.Lit = {
      val t = parser.parseObjectTypes(ISZ("org.sireum.lang.ast.Exp.LitB", "org.sireum.lang.ast.Exp.LitC", "org.sireum.lang.ast.Exp.LitZ", "org.sireum.lang.ast.Exp.LitF32", "org.sireum.lang.ast.Exp.LitF64", "org.sireum.lang.ast.Exp.LitR", "org.sireum.lang.ast.Exp.LitString"))
      t.native match {
        case "org.sireum.lang.ast.Exp.LitB" => val r = parse_astExpLitBT(T); return r
        case "org.sireum.lang.ast.Exp.LitC" => val r = parse_astExpLitCT(T); return r
        case "org.sireum.lang.ast.Exp.LitZ" => val r = parse_astExpLitZT(T); return r
        case "org.sireum.lang.ast.Exp.LitF32" => val r = parse_astExpLitF32T(T); return r
        case "org.sireum.lang.ast.Exp.LitF64" => val r = parse_astExpLitF64T(T); return r
        case "org.sireum.lang.ast.Exp.LitR" => val r = parse_astExpLitRT(T); return r
        case "org.sireum.lang.ast.Exp.LitString" => val r = parse_astExpLitStringT(T); return r
        case _ => val r = parse_astExpLitStringT(T); return r
      }
    }

    def parse_astExpLitB(): org.sireum.lang.ast.Exp.LitB = {
      val r = parse_astExpLitBT(F)
      return r
    }

    def parse_astExpLitBT(typeParsed: B): org.sireum.lang.ast.Exp.LitB = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Exp.LitB")
      }
      parser.parseObjectKey("value")
      val value = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Exp.LitB(value, attr)
    }

    def parse_astExpLitC(): org.sireum.lang.ast.Exp.LitC = {
      val r = parse_astExpLitCT(F)
      return r
    }

    def parse_astExpLitCT(typeParsed: B): org.sireum.lang.ast.Exp.LitC = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Exp.LitC")
      }
      parser.parseObjectKey("value")
      val value = parser.parseC()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Exp.LitC(value, attr)
    }

    def parse_astExpLitZ(): org.sireum.lang.ast.Exp.LitZ = {
      val r = parse_astExpLitZT(F)
      return r
    }

    def parse_astExpLitZT(typeParsed: B): org.sireum.lang.ast.Exp.LitZ = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Exp.LitZ")
      }
      parser.parseObjectKey("value")
      val value = parser.parseZ()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Exp.LitZ(value, attr)
    }

    def parse_astExpLitF32(): org.sireum.lang.ast.Exp.LitF32 = {
      val r = parse_astExpLitF32T(F)
      return r
    }

    def parse_astExpLitF32T(typeParsed: B): org.sireum.lang.ast.Exp.LitF32 = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Exp.LitF32")
      }
      parser.parseObjectKey("value")
      val value = parser.parseF32()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Exp.LitF32(value, attr)
    }

    def parse_astExpLitF64(): org.sireum.lang.ast.Exp.LitF64 = {
      val r = parse_astExpLitF64T(F)
      return r
    }

    def parse_astExpLitF64T(typeParsed: B): org.sireum.lang.ast.Exp.LitF64 = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Exp.LitF64")
      }
      parser.parseObjectKey("value")
      val value = parser.parseF64()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Exp.LitF64(value, attr)
    }

    def parse_astExpLitR(): org.sireum.lang.ast.Exp.LitR = {
      val r = parse_astExpLitRT(F)
      return r
    }

    def parse_astExpLitRT(typeParsed: B): org.sireum.lang.ast.Exp.LitR = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Exp.LitR")
      }
      parser.parseObjectKey("value")
      val value = parser.parseR()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Exp.LitR(value, attr)
    }

    def parse_astExpLitString(): org.sireum.lang.ast.Exp.LitString = {
      val r = parse_astExpLitStringT(F)
      return r
    }

    def parse_astExpLitStringT(typeParsed: B): org.sireum.lang.ast.Exp.LitString = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Exp.LitString")
      }
      parser.parseObjectKey("value")
      val value = parser.parseString()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Exp.LitString(value, attr)
    }

    def parse_astExpStringInterpolate(): org.sireum.lang.ast.Exp.StringInterpolate = {
      val r = parse_astExpStringInterpolateT(F)
      return r
    }

    def parse_astExpStringInterpolateT(typeParsed: B): org.sireum.lang.ast.Exp.StringInterpolate = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Exp.StringInterpolate")
      }
      parser.parseObjectKey("prefix")
      val prefix = parser.parseString()
      parser.parseObjectNext()
      parser.parseObjectKey("lits")
      val lits = parser.parseISZ(parse_astExpLitString _)
      parser.parseObjectNext()
      parser.parseObjectKey("args")
      val args = parser.parseISZ(parse_astExp _)
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astTypedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Exp.StringInterpolate(prefix, lits, args, attr)
    }

    def parse_astExpThis(): org.sireum.lang.ast.Exp.This = {
      val r = parse_astExpThisT(F)
      return r
    }

    def parse_astExpThisT(typeParsed: B): org.sireum.lang.ast.Exp.This = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Exp.This")
      }
      parser.parseObjectKey("attr")
      val attr = parse_astTypedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Exp.This(attr)
    }

    def parse_astExpSuper(): org.sireum.lang.ast.Exp.Super = {
      val r = parse_astExpSuperT(F)
      return r
    }

    def parse_astExpSuperT(typeParsed: B): org.sireum.lang.ast.Exp.Super = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Exp.Super")
      }
      parser.parseObjectKey("idOpt")
      val idOpt = parser.parseOption(parse_astId _)
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astTypedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Exp.Super(idOpt, attr)
    }

    def parse_astExpUnaryOpType(): org.sireum.lang.ast.Exp.UnaryOp.Type = {
      val r = parse_astExpUnaryOpT(F)
      return r
    }

    def parse_astExpUnaryOpT(typeParsed: B): org.sireum.lang.ast.Exp.UnaryOp.Type = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Exp.UnaryOp")
      }
      parser.parseObjectKey("value")
      var i = parser.offset
      val s = parser.parseString()
      parser.parseObjectNext()
      org.sireum.lang.ast.Exp.UnaryOp.byName(s) match {
        case Some(r) => return r
        case _ =>
          parser.parseException(i, s"Invalid element name '$s' for org.sireum.lang.ast.Exp.UnaryOp.")
          return org.sireum.lang.ast.Exp.UnaryOp.byOrdinal(0).get
      }
    }

    def parse_astExpUnary(): org.sireum.lang.ast.Exp.Unary = {
      val r = parse_astExpUnaryT(F)
      return r
    }

    def parse_astExpUnaryT(typeParsed: B): org.sireum.lang.ast.Exp.Unary = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Exp.Unary")
      }
      parser.parseObjectKey("op")
      val op = parse_astExpUnaryOpType()
      parser.parseObjectNext()
      parser.parseObjectKey("exp")
      val exp = parse_astExp()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astResolvedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Exp.Unary(op, exp, attr)
    }

    def parse_astExpRef(): org.sireum.lang.ast.Exp.Ref = {
      val t = parser.parseObjectTypes(ISZ("org.sireum.lang.ast.Exp.Ident", "org.sireum.lang.ast.Exp.Select"))
      t.native match {
        case "org.sireum.lang.ast.Exp.Ident" => val r = parse_astExpIdentT(T); return r
        case "org.sireum.lang.ast.Exp.Select" => val r = parse_astExpSelectT(T); return r
        case _ => val r = parse_astExpSelectT(T); return r
      }
    }

    def parse_astExpBinary(): org.sireum.lang.ast.Exp.Binary = {
      val r = parse_astExpBinaryT(F)
      return r
    }

    def parse_astExpBinaryT(typeParsed: B): org.sireum.lang.ast.Exp.Binary = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Exp.Binary")
      }
      parser.parseObjectKey("left")
      val left = parse_astExp()
      parser.parseObjectNext()
      parser.parseObjectKey("op")
      val op = parser.parseString()
      parser.parseObjectNext()
      parser.parseObjectKey("right")
      val right = parse_astExp()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astResolvedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Exp.Binary(left, op, right, attr)
    }

    def parse_astExpIdent(): org.sireum.lang.ast.Exp.Ident = {
      val r = parse_astExpIdentT(F)
      return r
    }

    def parse_astExpIdentT(typeParsed: B): org.sireum.lang.ast.Exp.Ident = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Exp.Ident")
      }
      parser.parseObjectKey("id")
      val id = parse_astId()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astResolvedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Exp.Ident(id, attr)
    }

    def parse_astExpEta(): org.sireum.lang.ast.Exp.Eta = {
      val r = parse_astExpEtaT(F)
      return r
    }

    def parse_astExpEtaT(typeParsed: B): org.sireum.lang.ast.Exp.Eta = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Exp.Eta")
      }
      parser.parseObjectKey("ref")
      val ref = parse_astExpRef()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astTypedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Exp.Eta(ref, attr)
    }

    def parse_astExpTuple(): org.sireum.lang.ast.Exp.Tuple = {
      val r = parse_astExpTupleT(F)
      return r
    }

    def parse_astExpTupleT(typeParsed: B): org.sireum.lang.ast.Exp.Tuple = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Exp.Tuple")
      }
      parser.parseObjectKey("args")
      val args = parser.parseISZ(parse_astExp _)
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astTypedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Exp.Tuple(args, attr)
    }

    def parse_astExpSelect(): org.sireum.lang.ast.Exp.Select = {
      val r = parse_astExpSelectT(F)
      return r
    }

    def parse_astExpSelectT(typeParsed: B): org.sireum.lang.ast.Exp.Select = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Exp.Select")
      }
      parser.parseObjectKey("receiverOpt")
      val receiverOpt = parser.parseOption(parse_astExp _)
      parser.parseObjectNext()
      parser.parseObjectKey("id")
      val id = parse_astId()
      parser.parseObjectNext()
      parser.parseObjectKey("targs")
      val targs = parser.parseISZ(parse_astType _)
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astResolvedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Exp.Select(receiverOpt, id, targs, attr)
    }

    def parse_astExpInvoke(): org.sireum.lang.ast.Exp.Invoke = {
      val r = parse_astExpInvokeT(F)
      return r
    }

    def parse_astExpInvokeT(typeParsed: B): org.sireum.lang.ast.Exp.Invoke = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Exp.Invoke")
      }
      parser.parseObjectKey("receiverOpt")
      val receiverOpt = parser.parseOption(parse_astExp _)
      parser.parseObjectNext()
      parser.parseObjectKey("ident")
      val ident = parse_astExpIdent()
      parser.parseObjectNext()
      parser.parseObjectKey("targs")
      val targs = parser.parseISZ(parse_astType _)
      parser.parseObjectNext()
      parser.parseObjectKey("args")
      val args = parser.parseISZ(parse_astExp _)
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astResolvedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Exp.Invoke(receiverOpt, ident, targs, args, attr)
    }

    def parse_astExpInvokeNamed(): org.sireum.lang.ast.Exp.InvokeNamed = {
      val r = parse_astExpInvokeNamedT(F)
      return r
    }

    def parse_astExpInvokeNamedT(typeParsed: B): org.sireum.lang.ast.Exp.InvokeNamed = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Exp.InvokeNamed")
      }
      parser.parseObjectKey("receiverOpt")
      val receiverOpt = parser.parseOption(parse_astExp _)
      parser.parseObjectNext()
      parser.parseObjectKey("ident")
      val ident = parse_astExpIdent()
      parser.parseObjectNext()
      parser.parseObjectKey("targs")
      val targs = parser.parseISZ(parse_astType _)
      parser.parseObjectNext()
      parser.parseObjectKey("args")
      val args = parser.parseISZ(parse_astNamedArg _)
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astResolvedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Exp.InvokeNamed(receiverOpt, ident, targs, args, attr)
    }

    def parse_astExpIf(): org.sireum.lang.ast.Exp.If = {
      val r = parse_astExpIfT(F)
      return r
    }

    def parse_astExpIfT(typeParsed: B): org.sireum.lang.ast.Exp.If = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Exp.If")
      }
      parser.parseObjectKey("cond")
      val cond = parse_astExp()
      parser.parseObjectNext()
      parser.parseObjectKey("thenExp")
      val thenExp = parse_astExp()
      parser.parseObjectNext()
      parser.parseObjectKey("elseExp")
      val elseExp = parse_astExp()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astTypedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Exp.If(cond, thenExp, elseExp, attr)
    }

    def parse_astExpFunParam(): org.sireum.lang.ast.Exp.Fun.Param = {
      val r = parse_astExpFunParamT(F)
      return r
    }

    def parse_astExpFunParamT(typeParsed: B): org.sireum.lang.ast.Exp.Fun.Param = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Exp.Fun.Param")
      }
      parser.parseObjectKey("id")
      val id = parse_astId()
      parser.parseObjectNext()
      parser.parseObjectKey("tipeOpt")
      val tipeOpt = parser.parseOption(parse_astType _)
      parser.parseObjectNext()
      parser.parseObjectKey("typedOpt")
      val typedOpt = parser.parseOption(parse_astTyped _)
      parser.parseObjectNext()
      return org.sireum.lang.ast.Exp.Fun.Param(id, tipeOpt, typedOpt)
    }

    def parse_astExpFun(): org.sireum.lang.ast.Exp.Fun = {
      val r = parse_astExpFunT(F)
      return r
    }

    def parse_astExpFunT(typeParsed: B): org.sireum.lang.ast.Exp.Fun = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Exp.Fun")
      }
      parser.parseObjectKey("context")
      val context = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("params")
      val params = parser.parseISZ(parse_astExpFunParam _)
      parser.parseObjectNext()
      parser.parseObjectKey("exp")
      val exp = parse_astAssignExp()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astTypedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Exp.Fun(context, params, exp, attr)
    }

    def parse_astExpForYield(): org.sireum.lang.ast.Exp.ForYield = {
      val r = parse_astExpForYieldT(F)
      return r
    }

    def parse_astExpForYieldT(typeParsed: B): org.sireum.lang.ast.Exp.ForYield = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Exp.ForYield")
      }
      parser.parseObjectKey("enumGens")
      val enumGens = parser.parseISZ(parse_astEnumGenFor _)
      parser.parseObjectNext()
      parser.parseObjectKey("exp")
      val exp = parse_astExp()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astTypedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Exp.ForYield(enumGens, exp, attr)
    }

    def parse_astExpSpec(): org.sireum.lang.ast.Exp.Spec = {
      val t = parser.parseObjectTypes(ISZ("org.sireum.lang.ast.Exp.Quant"))
      t.native match {
        case "org.sireum.lang.ast.Exp.Quant" => val r = parse_astExpQuantT(T); return r
        case _ => val r = parse_astExpQuantT(T); return r
      }
    }

    def parse_astExpQuant(): org.sireum.lang.ast.Exp.Quant = {
      val r = parse_astExpQuantT(F)
      return r
    }

    def parse_astExpQuantT(typeParsed: B): org.sireum.lang.ast.Exp.Quant = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Exp.Quant")
      }
      parser.parseObjectKey("isForall")
      val isForall = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("varFragments")
      val varFragments = parser.parseISZ(parse_astVarFragment _)
      parser.parseObjectNext()
      parser.parseObjectKey("exp")
      val exp = parse_astExp()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Exp.Quant(isForall, varFragments, exp, attr)
    }

    def parse_astNamedArg(): org.sireum.lang.ast.NamedArg = {
      val r = parse_astNamedArgT(F)
      return r
    }

    def parse_astNamedArgT(typeParsed: B): org.sireum.lang.ast.NamedArg = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.NamedArg")
      }
      parser.parseObjectKey("id")
      val id = parse_astId()
      parser.parseObjectNext()
      parser.parseObjectKey("arg")
      val arg = parse_astExp()
      parser.parseObjectNext()
      parser.parseObjectKey("index")
      val index = parser.parseZ()
      parser.parseObjectNext()
      return org.sireum.lang.ast.NamedArg(id, arg, index)
    }

    def parse_astVarFragment(): org.sireum.lang.ast.VarFragment = {
      val r = parse_astVarFragmentT(F)
      return r
    }

    def parse_astVarFragmentT(typeParsed: B): org.sireum.lang.ast.VarFragment = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.VarFragment")
      }
      parser.parseObjectKey("id")
      val id = parse_astId()
      parser.parseObjectNext()
      parser.parseObjectKey("domainOpt")
      val domainOpt = parser.parseOption(parse_astDomain _)
      parser.parseObjectNext()
      return org.sireum.lang.ast.VarFragment(id, domainOpt)
    }

    def parse_astDomain(): org.sireum.lang.ast.Domain = {
      val t = parser.parseObjectTypes(ISZ("org.sireum.lang.ast.Domain.Type", "org.sireum.lang.ast.Domain.Range", "org.sireum.lang.ast.Domain.Each"))
      t.native match {
        case "org.sireum.lang.ast.Domain.Type" => val r = parse_astDomainTypeT(T); return r
        case "org.sireum.lang.ast.Domain.Range" => val r = parse_astDomainRangeT(T); return r
        case "org.sireum.lang.ast.Domain.Each" => val r = parse_astDomainEachT(T); return r
        case _ => val r = parse_astDomainEachT(T); return r
      }
    }

    def parse_astDomainType(): org.sireum.lang.ast.Domain.Type = {
      val r = parse_astDomainTypeT(F)
      return r
    }

    def parse_astDomainTypeT(typeParsed: B): org.sireum.lang.ast.Domain.Type = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Domain.Type")
      }
      parser.parseObjectKey("tipe")
      val tipe = parse_astType()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astTypedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Domain.Type(tipe, attr)
    }

    def parse_astDomainRange(): org.sireum.lang.ast.Domain.Range = {
      val r = parse_astDomainRangeT(F)
      return r
    }

    def parse_astDomainRangeT(typeParsed: B): org.sireum.lang.ast.Domain.Range = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Domain.Range")
      }
      parser.parseObjectKey("lo")
      val lo = parse_astExp()
      parser.parseObjectNext()
      parser.parseObjectKey("hi")
      val hi = parse_astExp()
      parser.parseObjectNext()
      parser.parseObjectKey("hiExact")
      val hiExact = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astTypedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Domain.Range(lo, hi, hiExact, attr)
    }

    def parse_astDomainEach(): org.sireum.lang.ast.Domain.Each = {
      val r = parse_astDomainEachT(F)
      return r
    }

    def parse_astDomainEachT(typeParsed: B): org.sireum.lang.ast.Domain.Each = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Domain.Each")
      }
      parser.parseObjectKey("exp")
      val exp = parse_astExp()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astTypedAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Domain.Each(exp, attr)
    }

    def parse_astId(): org.sireum.lang.ast.Id = {
      val r = parse_astIdT(F)
      return r
    }

    def parse_astIdT(typeParsed: B): org.sireum.lang.ast.Id = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Id")
      }
      parser.parseObjectKey("value")
      val value = parser.parseString()
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Id(value, attr)
    }

    def parse_astName(): org.sireum.lang.ast.Name = {
      val r = parse_astNameT(F)
      return r
    }

    def parse_astNameT(typeParsed: B): org.sireum.lang.ast.Name = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Name")
      }
      parser.parseObjectKey("ids")
      val ids = parser.parseISZ(parse_astId _)
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Name(ids, attr)
    }

    def parse_astBody(): org.sireum.lang.ast.Body = {
      val r = parse_astBodyT(F)
      return r
    }

    def parse_astBodyT(typeParsed: B): org.sireum.lang.ast.Body = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Body")
      }
      parser.parseObjectKey("stmts")
      val stmts = parser.parseISZ(parse_astStmt _)
      parser.parseObjectNext()
      parser.parseObjectKey("undecls")
      val undecls = parser.parseISZ(parse_astResolvedInfoLocalVar _)
      parser.parseObjectNext()
      return org.sireum.lang.ast.Body(stmts, undecls)
    }

    def parse_astAdtParam(): org.sireum.lang.ast.AdtParam = {
      val r = parse_astAdtParamT(F)
      return r
    }

    def parse_astAdtParamT(typeParsed: B): org.sireum.lang.ast.AdtParam = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.AdtParam")
      }
      parser.parseObjectKey("isHidden")
      val isHidden = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("isVal")
      val isVal = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("id")
      val id = parse_astId()
      parser.parseObjectNext()
      parser.parseObjectKey("tipe")
      val tipe = parse_astType()
      parser.parseObjectNext()
      return org.sireum.lang.ast.AdtParam(isHidden, isVal, id, tipe)
    }

    def parse_astMethodSig(): org.sireum.lang.ast.MethodSig = {
      val r = parse_astMethodSigT(F)
      return r
    }

    def parse_astMethodSigT(typeParsed: B): org.sireum.lang.ast.MethodSig = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.MethodSig")
      }
      parser.parseObjectKey("isPure")
      val isPure = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("id")
      val id = parse_astId()
      parser.parseObjectNext()
      parser.parseObjectKey("typeParams")
      val typeParams = parser.parseISZ(parse_astTypeParam _)
      parser.parseObjectNext()
      parser.parseObjectKey("hasParams")
      val hasParams = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("params")
      val params = parser.parseISZ(parse_astParam _)
      parser.parseObjectNext()
      parser.parseObjectKey("returnType")
      val returnType = parse_astType()
      parser.parseObjectNext()
      return org.sireum.lang.ast.MethodSig(isPure, id, typeParams, hasParams, params, returnType)
    }

    def parse_astParam(): org.sireum.lang.ast.Param = {
      val r = parse_astParamT(F)
      return r
    }

    def parse_astParamT(typeParsed: B): org.sireum.lang.ast.Param = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Param")
      }
      parser.parseObjectKey("isHidden")
      val isHidden = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("id")
      val id = parse_astId()
      parser.parseObjectNext()
      parser.parseObjectKey("tipe")
      val tipe = parse_astType()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Param(isHidden, id, tipe)
    }

    def parse_astTypeParam(): org.sireum.lang.ast.TypeParam = {
      val r = parse_astTypeParamT(F)
      return r
    }

    def parse_astTypeParamT(typeParsed: B): org.sireum.lang.ast.TypeParam = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.TypeParam")
      }
      parser.parseObjectKey("id")
      val id = parse_astId()
      parser.parseObjectNext()
      return org.sireum.lang.ast.TypeParam(id)
    }

    def parse_astTyped(): org.sireum.lang.ast.Typed = {
      val t = parser.parseObjectTypes(ISZ("org.sireum.lang.ast.Typed.Name", "org.sireum.lang.ast.Typed.Tuple", "org.sireum.lang.ast.Typed.Fun", "org.sireum.lang.ast.Typed.TypeVar", "org.sireum.lang.ast.Typed.Package", "org.sireum.lang.ast.Typed.Object", "org.sireum.lang.ast.Typed.Enum", "org.sireum.lang.ast.Typed.Method", "org.sireum.lang.ast.Typed.Methods"))
      t.native match {
        case "org.sireum.lang.ast.Typed.Name" => val r = parse_astTypedNameT(T); return r
        case "org.sireum.lang.ast.Typed.Tuple" => val r = parse_astTypedTupleT(T); return r
        case "org.sireum.lang.ast.Typed.Fun" => val r = parse_astTypedFunT(T); return r
        case "org.sireum.lang.ast.Typed.TypeVar" => val r = parse_astTypedTypeVarT(T); return r
        case "org.sireum.lang.ast.Typed.Package" => val r = parse_astTypedPackageT(T); return r
        case "org.sireum.lang.ast.Typed.Object" => val r = parse_astTypedObjectT(T); return r
        case "org.sireum.lang.ast.Typed.Enum" => val r = parse_astTypedEnumT(T); return r
        case "org.sireum.lang.ast.Typed.Method" => val r = parse_astTypedMethodT(T); return r
        case "org.sireum.lang.ast.Typed.Methods" => val r = parse_astTypedMethodsT(T); return r
        case _ => val r = parse_astTypedMethodsT(T); return r
      }
    }

    def parse_astMethodModeType(): org.sireum.lang.ast.MethodMode.Type = {
      val r = parse_astMethodModeT(F)
      return r
    }

    def parse_astMethodModeT(typeParsed: B): org.sireum.lang.ast.MethodMode.Type = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.MethodMode")
      }
      parser.parseObjectKey("value")
      var i = parser.offset
      val s = parser.parseString()
      parser.parseObjectNext()
      org.sireum.lang.ast.MethodMode.byName(s) match {
        case Some(r) => return r
        case _ =>
          parser.parseException(i, s"Invalid element name '$s' for org.sireum.lang.ast.MethodMode.")
          return org.sireum.lang.ast.MethodMode.byOrdinal(0).get
      }
    }

    def parse_astTypedName(): org.sireum.lang.ast.Typed.Name = {
      val r = parse_astTypedNameT(F)
      return r
    }

    def parse_astTypedNameT(typeParsed: B): org.sireum.lang.ast.Typed.Name = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Typed.Name")
      }
      parser.parseObjectKey("ids")
      val ids = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("args")
      val args = parser.parseISZ(parse_astTyped _)
      parser.parseObjectNext()
      return org.sireum.lang.ast.Typed.Name(ids, args)
    }

    def parse_astTypedTuple(): org.sireum.lang.ast.Typed.Tuple = {
      val r = parse_astTypedTupleT(F)
      return r
    }

    def parse_astTypedTupleT(typeParsed: B): org.sireum.lang.ast.Typed.Tuple = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Typed.Tuple")
      }
      parser.parseObjectKey("args")
      val args = parser.parseISZ(parse_astTyped _)
      parser.parseObjectNext()
      return org.sireum.lang.ast.Typed.Tuple(args)
    }

    def parse_astTypedFun(): org.sireum.lang.ast.Typed.Fun = {
      val r = parse_astTypedFunT(F)
      return r
    }

    def parse_astTypedFunT(typeParsed: B): org.sireum.lang.ast.Typed.Fun = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Typed.Fun")
      }
      parser.parseObjectKey("isPure")
      val isPure = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("isByName")
      val isByName = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("args")
      val args = parser.parseISZ(parse_astTyped _)
      parser.parseObjectNext()
      parser.parseObjectKey("ret")
      val ret = parse_astTyped()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Typed.Fun(isPure, isByName, args, ret)
    }

    def parse_astTypedTypeVar(): org.sireum.lang.ast.Typed.TypeVar = {
      val r = parse_astTypedTypeVarT(F)
      return r
    }

    def parse_astTypedTypeVarT(typeParsed: B): org.sireum.lang.ast.Typed.TypeVar = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Typed.TypeVar")
      }
      parser.parseObjectKey("id")
      val id = parser.parseString()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Typed.TypeVar(id)
    }

    def parse_astTypedPackage(): org.sireum.lang.ast.Typed.Package = {
      val r = parse_astTypedPackageT(F)
      return r
    }

    def parse_astTypedPackageT(typeParsed: B): org.sireum.lang.ast.Typed.Package = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Typed.Package")
      }
      parser.parseObjectKey("name")
      val name = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      return org.sireum.lang.ast.Typed.Package(name)
    }

    def parse_astTypedObject(): org.sireum.lang.ast.Typed.Object = {
      val r = parse_astTypedObjectT(F)
      return r
    }

    def parse_astTypedObjectT(typeParsed: B): org.sireum.lang.ast.Typed.Object = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Typed.Object")
      }
      parser.parseObjectKey("owner")
      val owner = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("id")
      val id = parser.parseString()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Typed.Object(owner, id)
    }

    def parse_astTypedEnum(): org.sireum.lang.ast.Typed.Enum = {
      val r = parse_astTypedEnumT(F)
      return r
    }

    def parse_astTypedEnumT(typeParsed: B): org.sireum.lang.ast.Typed.Enum = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Typed.Enum")
      }
      parser.parseObjectKey("name")
      val name = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      return org.sireum.lang.ast.Typed.Enum(name)
    }

    def parse_astTypedMethod(): org.sireum.lang.ast.Typed.Method = {
      val r = parse_astTypedMethodT(F)
      return r
    }

    def parse_astTypedMethodT(typeParsed: B): org.sireum.lang.ast.Typed.Method = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Typed.Method")
      }
      parser.parseObjectKey("isInObject")
      val isInObject = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("mode")
      val mode = parse_astMethodModeType()
      parser.parseObjectNext()
      parser.parseObjectKey("typeParams")
      val typeParams = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("owner")
      val owner = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("name")
      val name = parser.parseString()
      parser.parseObjectNext()
      parser.parseObjectKey("paramNames")
      val paramNames = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("tpe")
      val tpe = parse_astTypedFun()
      parser.parseObjectNext()
      return org.sireum.lang.ast.Typed.Method(isInObject, mode, typeParams, owner, name, paramNames, tpe)
    }

    def parse_astTypedMethods(): org.sireum.lang.ast.Typed.Methods = {
      val r = parse_astTypedMethodsT(F)
      return r
    }

    def parse_astTypedMethodsT(typeParsed: B): org.sireum.lang.ast.Typed.Methods = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Typed.Methods")
      }
      parser.parseObjectKey("methods")
      val methods = parser.parseISZ(parse_astTypedMethod _)
      parser.parseObjectNext()
      return org.sireum.lang.ast.Typed.Methods(methods)
    }

    def parse_astAttr(): org.sireum.lang.ast.Attr = {
      val r = parse_astAttrT(F)
      return r
    }

    def parse_astAttrT(typeParsed: B): org.sireum.lang.ast.Attr = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.Attr")
      }
      parser.parseObjectKey("posOpt")
      val posOpt = parser.parseOption(parser.parsePosition _)
      parser.parseObjectNext()
      return org.sireum.lang.ast.Attr(posOpt)
    }

    def parse_astTypedAttr(): org.sireum.lang.ast.TypedAttr = {
      val r = parse_astTypedAttrT(F)
      return r
    }

    def parse_astTypedAttrT(typeParsed: B): org.sireum.lang.ast.TypedAttr = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.TypedAttr")
      }
      parser.parseObjectKey("posOpt")
      val posOpt = parser.parseOption(parser.parsePosition _)
      parser.parseObjectNext()
      parser.parseObjectKey("typedOpt")
      val typedOpt = parser.parseOption(parse_astTyped _)
      parser.parseObjectNext()
      return org.sireum.lang.ast.TypedAttr(posOpt, typedOpt)
    }

    def parse_astResolvedAttr(): org.sireum.lang.ast.ResolvedAttr = {
      val r = parse_astResolvedAttrT(F)
      return r
    }

    def parse_astResolvedAttrT(typeParsed: B): org.sireum.lang.ast.ResolvedAttr = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.ResolvedAttr")
      }
      parser.parseObjectKey("posOpt")
      val posOpt = parser.parseOption(parser.parsePosition _)
      parser.parseObjectNext()
      parser.parseObjectKey("resOpt")
      val resOpt = parser.parseOption(parse_astResolvedInfo _)
      parser.parseObjectNext()
      parser.parseObjectKey("typedOpt")
      val typedOpt = parser.parseOption(parse_astTyped _)
      parser.parseObjectNext()
      return org.sireum.lang.ast.ResolvedAttr(posOpt, resOpt, typedOpt)
    }

    def parse_astResolvedInfo(): org.sireum.lang.ast.ResolvedInfo = {
      val t = parser.parseObjectTypes(ISZ("org.sireum.lang.ast.ResolvedInfo.BuiltIn", "org.sireum.lang.ast.ResolvedInfo.Package", "org.sireum.lang.ast.ResolvedInfo.Enum", "org.sireum.lang.ast.ResolvedInfo.EnumElement", "org.sireum.lang.ast.ResolvedInfo.Object", "org.sireum.lang.ast.ResolvedInfo.Var", "org.sireum.lang.ast.ResolvedInfo.Method", "org.sireum.lang.ast.ResolvedInfo.Methods", "org.sireum.lang.ast.ResolvedInfo.Tuple", "org.sireum.lang.ast.ResolvedInfo.LocalVar"))
      t.native match {
        case "org.sireum.lang.ast.ResolvedInfo.BuiltIn" => val r = parse_astResolvedInfoBuiltInT(T); return r
        case "org.sireum.lang.ast.ResolvedInfo.Package" => val r = parse_astResolvedInfoPackageT(T); return r
        case "org.sireum.lang.ast.ResolvedInfo.Enum" => val r = parse_astResolvedInfoEnumT(T); return r
        case "org.sireum.lang.ast.ResolvedInfo.EnumElement" => val r = parse_astResolvedInfoEnumElementT(T); return r
        case "org.sireum.lang.ast.ResolvedInfo.Object" => val r = parse_astResolvedInfoObjectT(T); return r
        case "org.sireum.lang.ast.ResolvedInfo.Var" => val r = parse_astResolvedInfoVarT(T); return r
        case "org.sireum.lang.ast.ResolvedInfo.Method" => val r = parse_astResolvedInfoMethodT(T); return r
        case "org.sireum.lang.ast.ResolvedInfo.Methods" => val r = parse_astResolvedInfoMethodsT(T); return r
        case "org.sireum.lang.ast.ResolvedInfo.Tuple" => val r = parse_astResolvedInfoTupleT(T); return r
        case "org.sireum.lang.ast.ResolvedInfo.LocalVar" => val r = parse_astResolvedInfoLocalVarT(T); return r
        case _ => val r = parse_astResolvedInfoLocalVarT(T); return r
      }
    }

    def parse_astResolvedInfoBuiltInKindType(): org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.Type = {
      val r = parse_astResolvedInfoBuiltInKindT(F)
      return r
    }

    def parse_astResolvedInfoBuiltInKindT(typeParsed: B): org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.Type = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind")
      }
      parser.parseObjectKey("value")
      var i = parser.offset
      val s = parser.parseString()
      parser.parseObjectNext()
      org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.byName(s) match {
        case Some(r) => return r
        case _ =>
          parser.parseException(i, s"Invalid element name '$s' for org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.")
          return org.sireum.lang.ast.ResolvedInfo.BuiltIn.Kind.byOrdinal(0).get
      }
    }

    def parse_astResolvedInfoBuiltIn(): org.sireum.lang.ast.ResolvedInfo.BuiltIn = {
      val r = parse_astResolvedInfoBuiltInT(F)
      return r
    }

    def parse_astResolvedInfoBuiltInT(typeParsed: B): org.sireum.lang.ast.ResolvedInfo.BuiltIn = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.ResolvedInfo.BuiltIn")
      }
      parser.parseObjectKey("kind")
      val kind = parse_astResolvedInfoBuiltInKindType()
      parser.parseObjectNext()
      return org.sireum.lang.ast.ResolvedInfo.BuiltIn(kind)
    }

    def parse_astResolvedInfoPackage(): org.sireum.lang.ast.ResolvedInfo.Package = {
      val r = parse_astResolvedInfoPackageT(F)
      return r
    }

    def parse_astResolvedInfoPackageT(typeParsed: B): org.sireum.lang.ast.ResolvedInfo.Package = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.ResolvedInfo.Package")
      }
      parser.parseObjectKey("name")
      val name = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      return org.sireum.lang.ast.ResolvedInfo.Package(name)
    }

    def parse_astResolvedInfoEnum(): org.sireum.lang.ast.ResolvedInfo.Enum = {
      val r = parse_astResolvedInfoEnumT(F)
      return r
    }

    def parse_astResolvedInfoEnumT(typeParsed: B): org.sireum.lang.ast.ResolvedInfo.Enum = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.ResolvedInfo.Enum")
      }
      parser.parseObjectKey("name")
      val name = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      return org.sireum.lang.ast.ResolvedInfo.Enum(name)
    }

    def parse_astResolvedInfoEnumElement(): org.sireum.lang.ast.ResolvedInfo.EnumElement = {
      val r = parse_astResolvedInfoEnumElementT(F)
      return r
    }

    def parse_astResolvedInfoEnumElementT(typeParsed: B): org.sireum.lang.ast.ResolvedInfo.EnumElement = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.ResolvedInfo.EnumElement")
      }
      parser.parseObjectKey("owner")
      val owner = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("name")
      val name = parser.parseString()
      parser.parseObjectNext()
      parser.parseObjectKey("ordinal")
      val ordinal = parser.parseZ()
      parser.parseObjectNext()
      return org.sireum.lang.ast.ResolvedInfo.EnumElement(owner, name, ordinal)
    }

    def parse_astResolvedInfoObject(): org.sireum.lang.ast.ResolvedInfo.Object = {
      val r = parse_astResolvedInfoObjectT(F)
      return r
    }

    def parse_astResolvedInfoObjectT(typeParsed: B): org.sireum.lang.ast.ResolvedInfo.Object = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.ResolvedInfo.Object")
      }
      parser.parseObjectKey("name")
      val name = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      return org.sireum.lang.ast.ResolvedInfo.Object(name)
    }

    def parse_astResolvedInfoVar(): org.sireum.lang.ast.ResolvedInfo.Var = {
      val r = parse_astResolvedInfoVarT(F)
      return r
    }

    def parse_astResolvedInfoVarT(typeParsed: B): org.sireum.lang.ast.ResolvedInfo.Var = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.ResolvedInfo.Var")
      }
      parser.parseObjectKey("isInObject")
      val isInObject = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("isSpec")
      val isSpec = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("isVal")
      val isVal = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("owner")
      val owner = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("id")
      val id = parser.parseString()
      parser.parseObjectNext()
      return org.sireum.lang.ast.ResolvedInfo.Var(isInObject, isSpec, isVal, owner, id)
    }

    def parse_astResolvedInfoMethod(): org.sireum.lang.ast.ResolvedInfo.Method = {
      val r = parse_astResolvedInfoMethodT(F)
      return r
    }

    def parse_astResolvedInfoMethodT(typeParsed: B): org.sireum.lang.ast.ResolvedInfo.Method = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.ResolvedInfo.Method")
      }
      parser.parseObjectKey("isInObject")
      val isInObject = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("mode")
      val mode = parse_astMethodModeType()
      parser.parseObjectNext()
      parser.parseObjectKey("typeParams")
      val typeParams = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("owner")
      val owner = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("id")
      val id = parser.parseString()
      parser.parseObjectNext()
      parser.parseObjectKey("paramNames")
      val paramNames = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("tpeOpt")
      val tpeOpt = parser.parseOption(parse_astTypedFun _)
      parser.parseObjectNext()
      return org.sireum.lang.ast.ResolvedInfo.Method(isInObject, mode, typeParams, owner, id, paramNames, tpeOpt)
    }

    def parse_astResolvedInfoMethods(): org.sireum.lang.ast.ResolvedInfo.Methods = {
      val r = parse_astResolvedInfoMethodsT(F)
      return r
    }

    def parse_astResolvedInfoMethodsT(typeParsed: B): org.sireum.lang.ast.ResolvedInfo.Methods = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.ResolvedInfo.Methods")
      }
      parser.parseObjectKey("methods")
      val methods = parser.parseISZ(parse_astResolvedInfoMethod _)
      parser.parseObjectNext()
      return org.sireum.lang.ast.ResolvedInfo.Methods(methods)
    }

    def parse_astResolvedInfoTuple(): org.sireum.lang.ast.ResolvedInfo.Tuple = {
      val r = parse_astResolvedInfoTupleT(F)
      return r
    }

    def parse_astResolvedInfoTupleT(typeParsed: B): org.sireum.lang.ast.ResolvedInfo.Tuple = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.ResolvedInfo.Tuple")
      }
      parser.parseObjectKey("size")
      val size = parser.parseZ()
      parser.parseObjectNext()
      parser.parseObjectKey("index")
      val index = parser.parseZ()
      parser.parseObjectNext()
      return org.sireum.lang.ast.ResolvedInfo.Tuple(size, index)
    }

    def parse_astResolvedInfoLocalVarScopeType(): org.sireum.lang.ast.ResolvedInfo.LocalVar.Scope.Type = {
      val r = parse_astResolvedInfoLocalVarScopeT(F)
      return r
    }

    def parse_astResolvedInfoLocalVarScopeT(typeParsed: B): org.sireum.lang.ast.ResolvedInfo.LocalVar.Scope.Type = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.ResolvedInfo.LocalVar.Scope")
      }
      parser.parseObjectKey("value")
      var i = parser.offset
      val s = parser.parseString()
      parser.parseObjectNext()
      org.sireum.lang.ast.ResolvedInfo.LocalVar.Scope.byName(s) match {
        case Some(r) => return r
        case _ =>
          parser.parseException(i, s"Invalid element name '$s' for org.sireum.lang.ast.ResolvedInfo.LocalVar.Scope.")
          return org.sireum.lang.ast.ResolvedInfo.LocalVar.Scope.byOrdinal(0).get
      }
    }

    def parse_astResolvedInfoLocalVar(): org.sireum.lang.ast.ResolvedInfo.LocalVar = {
      val r = parse_astResolvedInfoLocalVarT(F)
      return r
    }

    def parse_astResolvedInfoLocalVarT(typeParsed: B): org.sireum.lang.ast.ResolvedInfo.LocalVar = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.ResolvedInfo.LocalVar")
      }
      parser.parseObjectKey("context")
      val context = parser.parseISZ(parser.parseString _)
      parser.parseObjectNext()
      parser.parseObjectKey("scope")
      val scope = parse_astResolvedInfoLocalVarScopeType()
      parser.parseObjectNext()
      parser.parseObjectKey("isVal")
      val isVal = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("id")
      val id = parser.parseString()
      parser.parseObjectNext()
      return org.sireum.lang.ast.ResolvedInfo.LocalVar(context, scope, isVal, id)
    }

    def parse_astTruthTableRow(): org.sireum.lang.ast.TruthTable.Row = {
      val r = parse_astTruthTableRowT(F)
      return r
    }

    def parse_astTruthTableRowT(typeParsed: B): org.sireum.lang.ast.TruthTable.Row = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.TruthTable.Row")
      }
      parser.parseObjectKey("assignment")
      val assignment = parse_astTruthTableAssignment()
      parser.parseObjectNext()
      parser.parseObjectKey("separator")
      val separator = parser.parsePosition()
      parser.parseObjectNext()
      parser.parseObjectKey("values")
      val values = parse_astTruthTableAssignment()
      parser.parseObjectNext()
      return org.sireum.lang.ast.TruthTable.Row(assignment, separator, values)
    }

    def parse_astTruthTableAssignment(): org.sireum.lang.ast.TruthTable.Assignment = {
      val r = parse_astTruthTableAssignmentT(F)
      return r
    }

    def parse_astTruthTableAssignmentT(typeParsed: B): org.sireum.lang.ast.TruthTable.Assignment = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.TruthTable.Assignment")
      }
      parser.parseObjectKey("values")
      val values = parser.parseISZ(parse_astExpLitB _)
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.TruthTable.Assignment(values, attr)
    }

    def parse_astTruthTableConclusion(): org.sireum.lang.ast.TruthTable.Conclusion = {
      val t = parser.parseObjectTypes(ISZ("org.sireum.lang.ast.TruthTable.Conclusion.Validity", "org.sireum.lang.ast.TruthTable.Conclusion.Tautology", "org.sireum.lang.ast.TruthTable.Conclusion.Contradictory", "org.sireum.lang.ast.TruthTable.Conclusion.Contingent"))
      t.native match {
        case "org.sireum.lang.ast.TruthTable.Conclusion.Validity" => val r = parse_astTruthTableConclusionValidityT(T); return r
        case "org.sireum.lang.ast.TruthTable.Conclusion.Tautology" => val r = parse_astTruthTableConclusionTautologyT(T); return r
        case "org.sireum.lang.ast.TruthTable.Conclusion.Contradictory" => val r = parse_astTruthTableConclusionContradictoryT(T); return r
        case "org.sireum.lang.ast.TruthTable.Conclusion.Contingent" => val r = parse_astTruthTableConclusionContingentT(T); return r
        case _ => val r = parse_astTruthTableConclusionContingentT(T); return r
      }
    }

    def parse_astTruthTableConclusionValidity(): org.sireum.lang.ast.TruthTable.Conclusion.Validity = {
      val r = parse_astTruthTableConclusionValidityT(F)
      return r
    }

    def parse_astTruthTableConclusionValidityT(typeParsed: B): org.sireum.lang.ast.TruthTable.Conclusion.Validity = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.TruthTable.Conclusion.Validity")
      }
      parser.parseObjectKey("isValid")
      val isValid = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("assignments")
      val assignments = parser.parseISZ(parse_astTruthTableAssignment _)
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.TruthTable.Conclusion.Validity(isValid, assignments, attr)
    }

    def parse_astTruthTableConclusionTautology(): org.sireum.lang.ast.TruthTable.Conclusion.Tautology = {
      val r = parse_astTruthTableConclusionTautologyT(F)
      return r
    }

    def parse_astTruthTableConclusionTautologyT(typeParsed: B): org.sireum.lang.ast.TruthTable.Conclusion.Tautology = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.TruthTable.Conclusion.Tautology")
      }
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.TruthTable.Conclusion.Tautology(attr)
    }

    def parse_astTruthTableConclusionContradictory(): org.sireum.lang.ast.TruthTable.Conclusion.Contradictory = {
      val r = parse_astTruthTableConclusionContradictoryT(F)
      return r
    }

    def parse_astTruthTableConclusionContradictoryT(typeParsed: B): org.sireum.lang.ast.TruthTable.Conclusion.Contradictory = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.TruthTable.Conclusion.Contradictory")
      }
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.TruthTable.Conclusion.Contradictory(attr)
    }

    def parse_astTruthTableConclusionContingent(): org.sireum.lang.ast.TruthTable.Conclusion.Contingent = {
      val r = parse_astTruthTableConclusionContingentT(F)
      return r
    }

    def parse_astTruthTableConclusionContingentT(typeParsed: B): org.sireum.lang.ast.TruthTable.Conclusion.Contingent = {
      if (!typeParsed) {
        parser.parseObjectType("org.sireum.lang.ast.TruthTable.Conclusion.Contingent")
      }
      parser.parseObjectKey("trueAssignments")
      val trueAssignments = parser.parseISZ(parse_astTruthTableAssignment _)
      parser.parseObjectNext()
      parser.parseObjectKey("falseAssignments")
      val falseAssignments = parser.parseISZ(parse_astTruthTableAssignment _)
      parser.parseObjectNext()
      parser.parseObjectKey("attr")
      val attr = parse_astAttr()
      parser.parseObjectNext()
      return org.sireum.lang.ast.TruthTable.Conclusion.Contingent(trueAssignments, falseAssignments, attr)
    }

    def eof(): B = {
      val r = parser.eof()
      return r
    }

  }

  def to[T](s: String, f: Parser => T): Either[T, Json.ErrorMsg] = {
    val parser = Parser(s)
    val r = f(parser)
    parser.eof()
    parser.errorOpt match {
      case Some(e) => return Either.Right(e)
      case _ => return Either.Left(r)
    }
  }

  def from_symbolScope(o: org.sireum.lang.symbol.Scope, isCompact: B): String = {
    val st = Printer.print_symbolScope(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_symbolScope(s: String): Either[org.sireum.lang.symbol.Scope, Json.ErrorMsg] = {
    def f_symbolScope(parser: Parser): org.sireum.lang.symbol.Scope = {
      val r = parser.parse_symbolScope()
      return r
    }
    val r = to(s, f_symbolScope _)
    return r
  }

  def from_symbolScopeLocal(o: org.sireum.lang.symbol.Scope.Local, isCompact: B): String = {
    val st = Printer.print_symbolScopeLocal(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_symbolScopeLocal(s: String): Either[org.sireum.lang.symbol.Scope.Local, Json.ErrorMsg] = {
    def f_symbolScopeLocal(parser: Parser): org.sireum.lang.symbol.Scope.Local = {
      val r = parser.parse_symbolScopeLocal()
      return r
    }
    val r = to(s, f_symbolScopeLocal _)
    return r
  }

  def from_symbolScopeGlobal(o: org.sireum.lang.symbol.Scope.Global, isCompact: B): String = {
    val st = Printer.print_symbolScopeGlobal(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_symbolScopeGlobal(s: String): Either[org.sireum.lang.symbol.Scope.Global, Json.ErrorMsg] = {
    def f_symbolScopeGlobal(parser: Parser): org.sireum.lang.symbol.Scope.Global = {
      val r = parser.parse_symbolScopeGlobal()
      return r
    }
    val r = to(s, f_symbolScopeGlobal _)
    return r
  }

  def from_symbolInfo(o: org.sireum.lang.symbol.Info, isCompact: B): String = {
    val st = Printer.print_symbolInfo(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_symbolInfo(s: String): Either[org.sireum.lang.symbol.Info, Json.ErrorMsg] = {
    def f_symbolInfo(parser: Parser): org.sireum.lang.symbol.Info = {
      val r = parser.parse_symbolInfo()
      return r
    }
    val r = to(s, f_symbolInfo _)
    return r
  }

  def from_symbolInfoPackage(o: org.sireum.lang.symbol.Info.Package, isCompact: B): String = {
    val st = Printer.print_symbolInfoPackage(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_symbolInfoPackage(s: String): Either[org.sireum.lang.symbol.Info.Package, Json.ErrorMsg] = {
    def f_symbolInfoPackage(parser: Parser): org.sireum.lang.symbol.Info.Package = {
      val r = parser.parse_symbolInfoPackage()
      return r
    }
    val r = to(s, f_symbolInfoPackage _)
    return r
  }

  def from_symbolInfoVar(o: org.sireum.lang.symbol.Info.Var, isCompact: B): String = {
    val st = Printer.print_symbolInfoVar(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_symbolInfoVar(s: String): Either[org.sireum.lang.symbol.Info.Var, Json.ErrorMsg] = {
    def f_symbolInfoVar(parser: Parser): org.sireum.lang.symbol.Info.Var = {
      val r = parser.parse_symbolInfoVar()
      return r
    }
    val r = to(s, f_symbolInfoVar _)
    return r
  }

  def from_symbolInfoSpecVar(o: org.sireum.lang.symbol.Info.SpecVar, isCompact: B): String = {
    val st = Printer.print_symbolInfoSpecVar(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_symbolInfoSpecVar(s: String): Either[org.sireum.lang.symbol.Info.SpecVar, Json.ErrorMsg] = {
    def f_symbolInfoSpecVar(parser: Parser): org.sireum.lang.symbol.Info.SpecVar = {
      val r = parser.parse_symbolInfoSpecVar()
      return r
    }
    val r = to(s, f_symbolInfoSpecVar _)
    return r
  }

  def from_symbolInfoMethod(o: org.sireum.lang.symbol.Info.Method, isCompact: B): String = {
    val st = Printer.print_symbolInfoMethod(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_symbolInfoMethod(s: String): Either[org.sireum.lang.symbol.Info.Method, Json.ErrorMsg] = {
    def f_symbolInfoMethod(parser: Parser): org.sireum.lang.symbol.Info.Method = {
      val r = parser.parse_symbolInfoMethod()
      return r
    }
    val r = to(s, f_symbolInfoMethod _)
    return r
  }

  def from_symbolInfoSpecMethod(o: org.sireum.lang.symbol.Info.SpecMethod, isCompact: B): String = {
    val st = Printer.print_symbolInfoSpecMethod(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_symbolInfoSpecMethod(s: String): Either[org.sireum.lang.symbol.Info.SpecMethod, Json.ErrorMsg] = {
    def f_symbolInfoSpecMethod(parser: Parser): org.sireum.lang.symbol.Info.SpecMethod = {
      val r = parser.parse_symbolInfoSpecMethod()
      return r
    }
    val r = to(s, f_symbolInfoSpecMethod _)
    return r
  }

  def from_symbolInfoObject(o: org.sireum.lang.symbol.Info.Object, isCompact: B): String = {
    val st = Printer.print_symbolInfoObject(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_symbolInfoObject(s: String): Either[org.sireum.lang.symbol.Info.Object, Json.ErrorMsg] = {
    def f_symbolInfoObject(parser: Parser): org.sireum.lang.symbol.Info.Object = {
      val r = parser.parse_symbolInfoObject()
      return r
    }
    val r = to(s, f_symbolInfoObject _)
    return r
  }

  def from_symbolInfoExtMethod(o: org.sireum.lang.symbol.Info.ExtMethod, isCompact: B): String = {
    val st = Printer.print_symbolInfoExtMethod(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_symbolInfoExtMethod(s: String): Either[org.sireum.lang.symbol.Info.ExtMethod, Json.ErrorMsg] = {
    def f_symbolInfoExtMethod(parser: Parser): org.sireum.lang.symbol.Info.ExtMethod = {
      val r = parser.parse_symbolInfoExtMethod()
      return r
    }
    val r = to(s, f_symbolInfoExtMethod _)
    return r
  }

  def from_symbolInfoEnum(o: org.sireum.lang.symbol.Info.Enum, isCompact: B): String = {
    val st = Printer.print_symbolInfoEnum(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_symbolInfoEnum(s: String): Either[org.sireum.lang.symbol.Info.Enum, Json.ErrorMsg] = {
    def f_symbolInfoEnum(parser: Parser): org.sireum.lang.symbol.Info.Enum = {
      val r = parser.parse_symbolInfoEnum()
      return r
    }
    val r = to(s, f_symbolInfoEnum _)
    return r
  }

  def from_symbolInfoEnumElement(o: org.sireum.lang.symbol.Info.EnumElement, isCompact: B): String = {
    val st = Printer.print_symbolInfoEnumElement(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_symbolInfoEnumElement(s: String): Either[org.sireum.lang.symbol.Info.EnumElement, Json.ErrorMsg] = {
    def f_symbolInfoEnumElement(parser: Parser): org.sireum.lang.symbol.Info.EnumElement = {
      val r = parser.parse_symbolInfoEnumElement()
      return r
    }
    val r = to(s, f_symbolInfoEnumElement _)
    return r
  }

  def from_symbolInfoLocalVar(o: org.sireum.lang.symbol.Info.LocalVar, isCompact: B): String = {
    val st = Printer.print_symbolInfoLocalVar(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_symbolInfoLocalVar(s: String): Either[org.sireum.lang.symbol.Info.LocalVar, Json.ErrorMsg] = {
    def f_symbolInfoLocalVar(parser: Parser): org.sireum.lang.symbol.Info.LocalVar = {
      val r = parser.parse_symbolInfoLocalVar()
      return r
    }
    val r = to(s, f_symbolInfoLocalVar _)
    return r
  }

  def from_symbolInfoQuantVar(o: org.sireum.lang.symbol.Info.QuantVar, isCompact: B): String = {
    val st = Printer.print_symbolInfoQuantVar(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_symbolInfoQuantVar(s: String): Either[org.sireum.lang.symbol.Info.QuantVar, Json.ErrorMsg] = {
    def f_symbolInfoQuantVar(parser: Parser): org.sireum.lang.symbol.Info.QuantVar = {
      val r = parser.parse_symbolInfoQuantVar()
      return r
    }
    val r = to(s, f_symbolInfoQuantVar _)
    return r
  }

  def from_symbolTypeInfo(o: org.sireum.lang.symbol.TypeInfo, isCompact: B): String = {
    val st = Printer.print_symbolTypeInfo(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_symbolTypeInfo(s: String): Either[org.sireum.lang.symbol.TypeInfo, Json.ErrorMsg] = {
    def f_symbolTypeInfo(parser: Parser): org.sireum.lang.symbol.TypeInfo = {
      val r = parser.parse_symbolTypeInfo()
      return r
    }
    val r = to(s, f_symbolTypeInfo _)
    return r
  }

  def from_symbolTypeInfoSubZ(o: org.sireum.lang.symbol.TypeInfo.SubZ, isCompact: B): String = {
    val st = Printer.print_symbolTypeInfoSubZ(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_symbolTypeInfoSubZ(s: String): Either[org.sireum.lang.symbol.TypeInfo.SubZ, Json.ErrorMsg] = {
    def f_symbolTypeInfoSubZ(parser: Parser): org.sireum.lang.symbol.TypeInfo.SubZ = {
      val r = parser.parse_symbolTypeInfoSubZ()
      return r
    }
    val r = to(s, f_symbolTypeInfoSubZ _)
    return r
  }

  def from_symbolTypeInfoEnum(o: org.sireum.lang.symbol.TypeInfo.Enum, isCompact: B): String = {
    val st = Printer.print_symbolTypeInfoEnum(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_symbolTypeInfoEnum(s: String): Either[org.sireum.lang.symbol.TypeInfo.Enum, Json.ErrorMsg] = {
    def f_symbolTypeInfoEnum(parser: Parser): org.sireum.lang.symbol.TypeInfo.Enum = {
      val r = parser.parse_symbolTypeInfoEnum()
      return r
    }
    val r = to(s, f_symbolTypeInfoEnum _)
    return r
  }

  def from_symbolTypeInfoSig(o: org.sireum.lang.symbol.TypeInfo.Sig, isCompact: B): String = {
    val st = Printer.print_symbolTypeInfoSig(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_symbolTypeInfoSig(s: String): Either[org.sireum.lang.symbol.TypeInfo.Sig, Json.ErrorMsg] = {
    def f_symbolTypeInfoSig(parser: Parser): org.sireum.lang.symbol.TypeInfo.Sig = {
      val r = parser.parse_symbolTypeInfoSig()
      return r
    }
    val r = to(s, f_symbolTypeInfoSig _)
    return r
  }

  def from_symbolTypeInfoName(o: org.sireum.lang.symbol.TypeInfo.Name, isCompact: B): String = {
    val st = Printer.print_symbolTypeInfoName(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_symbolTypeInfoName(s: String): Either[org.sireum.lang.symbol.TypeInfo.Name, Json.ErrorMsg] = {
    def f_symbolTypeInfoName(parser: Parser): org.sireum.lang.symbol.TypeInfo.Name = {
      val r = parser.parse_symbolTypeInfoName()
      return r
    }
    val r = to(s, f_symbolTypeInfoName _)
    return r
  }

  def from_symbolTypeInfoAdt(o: org.sireum.lang.symbol.TypeInfo.Adt, isCompact: B): String = {
    val st = Printer.print_symbolTypeInfoAdt(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_symbolTypeInfoAdt(s: String): Either[org.sireum.lang.symbol.TypeInfo.Adt, Json.ErrorMsg] = {
    def f_symbolTypeInfoAdt(parser: Parser): org.sireum.lang.symbol.TypeInfo.Adt = {
      val r = parser.parse_symbolTypeInfoAdt()
      return r
    }
    val r = to(s, f_symbolTypeInfoAdt _)
    return r
  }

  def from_symbolTypeInfoTypeAlias(o: org.sireum.lang.symbol.TypeInfo.TypeAlias, isCompact: B): String = {
    val st = Printer.print_symbolTypeInfoTypeAlias(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_symbolTypeInfoTypeAlias(s: String): Either[org.sireum.lang.symbol.TypeInfo.TypeAlias, Json.ErrorMsg] = {
    def f_symbolTypeInfoTypeAlias(parser: Parser): org.sireum.lang.symbol.TypeInfo.TypeAlias = {
      val r = parser.parse_symbolTypeInfoTypeAlias()
      return r
    }
    val r = to(s, f_symbolTypeInfoTypeAlias _)
    return r
  }

  def from_symbolTypeInfoTypeVar(o: org.sireum.lang.symbol.TypeInfo.TypeVar, isCompact: B): String = {
    val st = Printer.print_symbolTypeInfoTypeVar(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_symbolTypeInfoTypeVar(s: String): Either[org.sireum.lang.symbol.TypeInfo.TypeVar, Json.ErrorMsg] = {
    def f_symbolTypeInfoTypeVar(parser: Parser): org.sireum.lang.symbol.TypeInfo.TypeVar = {
      val r = parser.parse_symbolTypeInfoTypeVar()
      return r
    }
    val r = to(s, f_symbolTypeInfoTypeVar _)
    return r
  }

  def from_symbolTypeInfoMembers(o: org.sireum.lang.symbol.TypeInfo.Members, isCompact: B): String = {
    val st = Printer.print_symbolTypeInfoMembers(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_symbolTypeInfoMembers(s: String): Either[org.sireum.lang.symbol.TypeInfo.Members, Json.ErrorMsg] = {
    def f_symbolTypeInfoMembers(parser: Parser): org.sireum.lang.symbol.TypeInfo.Members = {
      val r = parser.parse_symbolTypeInfoMembers()
      return r
    }
    val r = to(s, f_symbolTypeInfoMembers _)
    return r
  }

  def from_astTopUnit(o: org.sireum.lang.ast.TopUnit, isCompact: B): String = {
    val st = Printer.print_astTopUnit(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astTopUnit(s: String): Either[org.sireum.lang.ast.TopUnit, Json.ErrorMsg] = {
    def f_astTopUnit(parser: Parser): org.sireum.lang.ast.TopUnit = {
      val r = parser.parse_astTopUnit()
      return r
    }
    val r = to(s, f_astTopUnit _)
    return r
  }

  def from_astTopUnitProgram(o: org.sireum.lang.ast.TopUnit.Program, isCompact: B): String = {
    val st = Printer.print_astTopUnitProgram(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astTopUnitProgram(s: String): Either[org.sireum.lang.ast.TopUnit.Program, Json.ErrorMsg] = {
    def f_astTopUnitProgram(parser: Parser): org.sireum.lang.ast.TopUnit.Program = {
      val r = parser.parse_astTopUnitProgram()
      return r
    }
    val r = to(s, f_astTopUnitProgram _)
    return r
  }

  def from_astTopUnitSequentUnit(o: org.sireum.lang.ast.TopUnit.SequentUnit, isCompact: B): String = {
    val st = Printer.print_astTopUnitSequentUnit(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astTopUnitSequentUnit(s: String): Either[org.sireum.lang.ast.TopUnit.SequentUnit, Json.ErrorMsg] = {
    def f_astTopUnitSequentUnit(parser: Parser): org.sireum.lang.ast.TopUnit.SequentUnit = {
      val r = parser.parse_astTopUnitSequentUnit()
      return r
    }
    val r = to(s, f_astTopUnitSequentUnit _)
    return r
  }

  def from_astTopUnitTruthTableUnit(o: org.sireum.lang.ast.TopUnit.TruthTableUnit, isCompact: B): String = {
    val st = Printer.print_astTopUnitTruthTableUnit(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astTopUnitTruthTableUnit(s: String): Either[org.sireum.lang.ast.TopUnit.TruthTableUnit, Json.ErrorMsg] = {
    def f_astTopUnitTruthTableUnit(parser: Parser): org.sireum.lang.ast.TopUnit.TruthTableUnit = {
      val r = parser.parse_astTopUnitTruthTableUnit()
      return r
    }
    val r = to(s, f_astTopUnitTruthTableUnit _)
    return r
  }

  def from_astStmt(o: org.sireum.lang.ast.Stmt, isCompact: B): String = {
    val st = Printer.print_astStmt(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmt(s: String): Either[org.sireum.lang.ast.Stmt, Json.ErrorMsg] = {
    def f_astStmt(parser: Parser): org.sireum.lang.ast.Stmt = {
      val r = parser.parse_astStmt()
      return r
    }
    val r = to(s, f_astStmt _)
    return r
  }

  def from_astStmtImport(o: org.sireum.lang.ast.Stmt.Import, isCompact: B): String = {
    val st = Printer.print_astStmtImport(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtImport(s: String): Either[org.sireum.lang.ast.Stmt.Import, Json.ErrorMsg] = {
    def f_astStmtImport(parser: Parser): org.sireum.lang.ast.Stmt.Import = {
      val r = parser.parse_astStmtImport()
      return r
    }
    val r = to(s, f_astStmtImport _)
    return r
  }

  def from_astStmtImportImporter(o: org.sireum.lang.ast.Stmt.Import.Importer, isCompact: B): String = {
    val st = Printer.print_astStmtImportImporter(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtImportImporter(s: String): Either[org.sireum.lang.ast.Stmt.Import.Importer, Json.ErrorMsg] = {
    def f_astStmtImportImporter(parser: Parser): org.sireum.lang.ast.Stmt.Import.Importer = {
      val r = parser.parse_astStmtImportImporter()
      return r
    }
    val r = to(s, f_astStmtImportImporter _)
    return r
  }

  def from_astStmtImportSelector(o: org.sireum.lang.ast.Stmt.Import.Selector, isCompact: B): String = {
    val st = Printer.print_astStmtImportSelector(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtImportSelector(s: String): Either[org.sireum.lang.ast.Stmt.Import.Selector, Json.ErrorMsg] = {
    def f_astStmtImportSelector(parser: Parser): org.sireum.lang.ast.Stmt.Import.Selector = {
      val r = parser.parse_astStmtImportSelector()
      return r
    }
    val r = to(s, f_astStmtImportSelector _)
    return r
  }

  def from_astStmtImportMultiSelector(o: org.sireum.lang.ast.Stmt.Import.MultiSelector, isCompact: B): String = {
    val st = Printer.print_astStmtImportMultiSelector(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtImportMultiSelector(s: String): Either[org.sireum.lang.ast.Stmt.Import.MultiSelector, Json.ErrorMsg] = {
    def f_astStmtImportMultiSelector(parser: Parser): org.sireum.lang.ast.Stmt.Import.MultiSelector = {
      val r = parser.parse_astStmtImportMultiSelector()
      return r
    }
    val r = to(s, f_astStmtImportMultiSelector _)
    return r
  }

  def from_astStmtImportWildcardSelector(o: org.sireum.lang.ast.Stmt.Import.WildcardSelector, isCompact: B): String = {
    val st = Printer.print_astStmtImportWildcardSelector(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtImportWildcardSelector(s: String): Either[org.sireum.lang.ast.Stmt.Import.WildcardSelector, Json.ErrorMsg] = {
    def f_astStmtImportWildcardSelector(parser: Parser): org.sireum.lang.ast.Stmt.Import.WildcardSelector = {
      val r = parser.parse_astStmtImportWildcardSelector()
      return r
    }
    val r = to(s, f_astStmtImportWildcardSelector _)
    return r
  }

  def from_astStmtImportNamedSelector(o: org.sireum.lang.ast.Stmt.Import.NamedSelector, isCompact: B): String = {
    val st = Printer.print_astStmtImportNamedSelector(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtImportNamedSelector(s: String): Either[org.sireum.lang.ast.Stmt.Import.NamedSelector, Json.ErrorMsg] = {
    def f_astStmtImportNamedSelector(parser: Parser): org.sireum.lang.ast.Stmt.Import.NamedSelector = {
      val r = parser.parse_astStmtImportNamedSelector()
      return r
    }
    val r = to(s, f_astStmtImportNamedSelector _)
    return r
  }

  def from_astStmtVar(o: org.sireum.lang.ast.Stmt.Var, isCompact: B): String = {
    val st = Printer.print_astStmtVar(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtVar(s: String): Either[org.sireum.lang.ast.Stmt.Var, Json.ErrorMsg] = {
    def f_astStmtVar(parser: Parser): org.sireum.lang.ast.Stmt.Var = {
      val r = parser.parse_astStmtVar()
      return r
    }
    val r = to(s, f_astStmtVar _)
    return r
  }

  def from_astStmtVarPattern(o: org.sireum.lang.ast.Stmt.VarPattern, isCompact: B): String = {
    val st = Printer.print_astStmtVarPattern(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtVarPattern(s: String): Either[org.sireum.lang.ast.Stmt.VarPattern, Json.ErrorMsg] = {
    def f_astStmtVarPattern(parser: Parser): org.sireum.lang.ast.Stmt.VarPattern = {
      val r = parser.parse_astStmtVarPattern()
      return r
    }
    val r = to(s, f_astStmtVarPattern _)
    return r
  }

  def from_astStmtSpecVar(o: org.sireum.lang.ast.Stmt.SpecVar, isCompact: B): String = {
    val st = Printer.print_astStmtSpecVar(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtSpecVar(s: String): Either[org.sireum.lang.ast.Stmt.SpecVar, Json.ErrorMsg] = {
    def f_astStmtSpecVar(parser: Parser): org.sireum.lang.ast.Stmt.SpecVar = {
      val r = parser.parse_astStmtSpecVar()
      return r
    }
    val r = to(s, f_astStmtSpecVar _)
    return r
  }

  def from_astStmtMethod(o: org.sireum.lang.ast.Stmt.Method, isCompact: B): String = {
    val st = Printer.print_astStmtMethod(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtMethod(s: String): Either[org.sireum.lang.ast.Stmt.Method, Json.ErrorMsg] = {
    def f_astStmtMethod(parser: Parser): org.sireum.lang.ast.Stmt.Method = {
      val r = parser.parse_astStmtMethod()
      return r
    }
    val r = to(s, f_astStmtMethod _)
    return r
  }

  def from_astStmtExtMethod(o: org.sireum.lang.ast.Stmt.ExtMethod, isCompact: B): String = {
    val st = Printer.print_astStmtExtMethod(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtExtMethod(s: String): Either[org.sireum.lang.ast.Stmt.ExtMethod, Json.ErrorMsg] = {
    def f_astStmtExtMethod(parser: Parser): org.sireum.lang.ast.Stmt.ExtMethod = {
      val r = parser.parse_astStmtExtMethod()
      return r
    }
    val r = to(s, f_astStmtExtMethod _)
    return r
  }

  def from_astStmtSpecMethod(o: org.sireum.lang.ast.Stmt.SpecMethod, isCompact: B): String = {
    val st = Printer.print_astStmtSpecMethod(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtSpecMethod(s: String): Either[org.sireum.lang.ast.Stmt.SpecMethod, Json.ErrorMsg] = {
    def f_astStmtSpecMethod(parser: Parser): org.sireum.lang.ast.Stmt.SpecMethod = {
      val r = parser.parse_astStmtSpecMethod()
      return r
    }
    val r = to(s, f_astStmtSpecMethod _)
    return r
  }

  def from_astStmtEnum(o: org.sireum.lang.ast.Stmt.Enum, isCompact: B): String = {
    val st = Printer.print_astStmtEnum(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtEnum(s: String): Either[org.sireum.lang.ast.Stmt.Enum, Json.ErrorMsg] = {
    def f_astStmtEnum(parser: Parser): org.sireum.lang.ast.Stmt.Enum = {
      val r = parser.parse_astStmtEnum()
      return r
    }
    val r = to(s, f_astStmtEnum _)
    return r
  }

  def from_astStmtSubZ(o: org.sireum.lang.ast.Stmt.SubZ, isCompact: B): String = {
    val st = Printer.print_astStmtSubZ(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtSubZ(s: String): Either[org.sireum.lang.ast.Stmt.SubZ, Json.ErrorMsg] = {
    def f_astStmtSubZ(parser: Parser): org.sireum.lang.ast.Stmt.SubZ = {
      val r = parser.parse_astStmtSubZ()
      return r
    }
    val r = to(s, f_astStmtSubZ _)
    return r
  }

  def from_astStmtObject(o: org.sireum.lang.ast.Stmt.Object, isCompact: B): String = {
    val st = Printer.print_astStmtObject(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtObject(s: String): Either[org.sireum.lang.ast.Stmt.Object, Json.ErrorMsg] = {
    def f_astStmtObject(parser: Parser): org.sireum.lang.ast.Stmt.Object = {
      val r = parser.parse_astStmtObject()
      return r
    }
    val r = to(s, f_astStmtObject _)
    return r
  }

  def from_astStmtSig(o: org.sireum.lang.ast.Stmt.Sig, isCompact: B): String = {
    val st = Printer.print_astStmtSig(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtSig(s: String): Either[org.sireum.lang.ast.Stmt.Sig, Json.ErrorMsg] = {
    def f_astStmtSig(parser: Parser): org.sireum.lang.ast.Stmt.Sig = {
      val r = parser.parse_astStmtSig()
      return r
    }
    val r = to(s, f_astStmtSig _)
    return r
  }

  def from_astStmtAdt(o: org.sireum.lang.ast.Stmt.Adt, isCompact: B): String = {
    val st = Printer.print_astStmtAdt(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtAdt(s: String): Either[org.sireum.lang.ast.Stmt.Adt, Json.ErrorMsg] = {
    def f_astStmtAdt(parser: Parser): org.sireum.lang.ast.Stmt.Adt = {
      val r = parser.parse_astStmtAdt()
      return r
    }
    val r = to(s, f_astStmtAdt _)
    return r
  }

  def from_astStmtTypeAlias(o: org.sireum.lang.ast.Stmt.TypeAlias, isCompact: B): String = {
    val st = Printer.print_astStmtTypeAlias(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtTypeAlias(s: String): Either[org.sireum.lang.ast.Stmt.TypeAlias, Json.ErrorMsg] = {
    def f_astStmtTypeAlias(parser: Parser): org.sireum.lang.ast.Stmt.TypeAlias = {
      val r = parser.parse_astStmtTypeAlias()
      return r
    }
    val r = to(s, f_astStmtTypeAlias _)
    return r
  }

  def from_astStmtAssign(o: org.sireum.lang.ast.Stmt.Assign, isCompact: B): String = {
    val st = Printer.print_astStmtAssign(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtAssign(s: String): Either[org.sireum.lang.ast.Stmt.Assign, Json.ErrorMsg] = {
    def f_astStmtAssign(parser: Parser): org.sireum.lang.ast.Stmt.Assign = {
      val r = parser.parse_astStmtAssign()
      return r
    }
    val r = to(s, f_astStmtAssign _)
    return r
  }

  def from_astStmtBlock(o: org.sireum.lang.ast.Stmt.Block, isCompact: B): String = {
    val st = Printer.print_astStmtBlock(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtBlock(s: String): Either[org.sireum.lang.ast.Stmt.Block, Json.ErrorMsg] = {
    def f_astStmtBlock(parser: Parser): org.sireum.lang.ast.Stmt.Block = {
      val r = parser.parse_astStmtBlock()
      return r
    }
    val r = to(s, f_astStmtBlock _)
    return r
  }

  def from_astStmtIf(o: org.sireum.lang.ast.Stmt.If, isCompact: B): String = {
    val st = Printer.print_astStmtIf(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtIf(s: String): Either[org.sireum.lang.ast.Stmt.If, Json.ErrorMsg] = {
    def f_astStmtIf(parser: Parser): org.sireum.lang.ast.Stmt.If = {
      val r = parser.parse_astStmtIf()
      return r
    }
    val r = to(s, f_astStmtIf _)
    return r
  }

  def from_astStmtMatch(o: org.sireum.lang.ast.Stmt.Match, isCompact: B): String = {
    val st = Printer.print_astStmtMatch(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtMatch(s: String): Either[org.sireum.lang.ast.Stmt.Match, Json.ErrorMsg] = {
    def f_astStmtMatch(parser: Parser): org.sireum.lang.ast.Stmt.Match = {
      val r = parser.parse_astStmtMatch()
      return r
    }
    val r = to(s, f_astStmtMatch _)
    return r
  }

  def from_astStmtLoop(o: org.sireum.lang.ast.Stmt.Loop, isCompact: B): String = {
    val st = Printer.print_astStmtLoop(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtLoop(s: String): Either[org.sireum.lang.ast.Stmt.Loop, Json.ErrorMsg] = {
    def f_astStmtLoop(parser: Parser): org.sireum.lang.ast.Stmt.Loop = {
      val r = parser.parse_astStmtLoop()
      return r
    }
    val r = to(s, f_astStmtLoop _)
    return r
  }

  def from_astStmtWhile(o: org.sireum.lang.ast.Stmt.While, isCompact: B): String = {
    val st = Printer.print_astStmtWhile(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtWhile(s: String): Either[org.sireum.lang.ast.Stmt.While, Json.ErrorMsg] = {
    def f_astStmtWhile(parser: Parser): org.sireum.lang.ast.Stmt.While = {
      val r = parser.parse_astStmtWhile()
      return r
    }
    val r = to(s, f_astStmtWhile _)
    return r
  }

  def from_astStmtDoWhile(o: org.sireum.lang.ast.Stmt.DoWhile, isCompact: B): String = {
    val st = Printer.print_astStmtDoWhile(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtDoWhile(s: String): Either[org.sireum.lang.ast.Stmt.DoWhile, Json.ErrorMsg] = {
    def f_astStmtDoWhile(parser: Parser): org.sireum.lang.ast.Stmt.DoWhile = {
      val r = parser.parse_astStmtDoWhile()
      return r
    }
    val r = to(s, f_astStmtDoWhile _)
    return r
  }

  def from_astStmtFor(o: org.sireum.lang.ast.Stmt.For, isCompact: B): String = {
    val st = Printer.print_astStmtFor(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtFor(s: String): Either[org.sireum.lang.ast.Stmt.For, Json.ErrorMsg] = {
    def f_astStmtFor(parser: Parser): org.sireum.lang.ast.Stmt.For = {
      val r = parser.parse_astStmtFor()
      return r
    }
    val r = to(s, f_astStmtFor _)
    return r
  }

  def from_astStmtReturn(o: org.sireum.lang.ast.Stmt.Return, isCompact: B): String = {
    val st = Printer.print_astStmtReturn(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtReturn(s: String): Either[org.sireum.lang.ast.Stmt.Return, Json.ErrorMsg] = {
    def f_astStmtReturn(parser: Parser): org.sireum.lang.ast.Stmt.Return = {
      val r = parser.parse_astStmtReturn()
      return r
    }
    val r = to(s, f_astStmtReturn _)
    return r
  }

  def from_astStmtExpr(o: org.sireum.lang.ast.Stmt.Expr, isCompact: B): String = {
    val st = Printer.print_astStmtExpr(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtExpr(s: String): Either[org.sireum.lang.ast.Stmt.Expr, Json.ErrorMsg] = {
    def f_astStmtExpr(parser: Parser): org.sireum.lang.ast.Stmt.Expr = {
      val r = parser.parse_astStmtExpr()
      return r
    }
    val r = to(s, f_astStmtExpr _)
    return r
  }

  def from_astStmtSpec(o: org.sireum.lang.ast.Stmt.Spec, isCompact: B): String = {
    val st = Printer.print_astStmtSpec(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtSpec(s: String): Either[org.sireum.lang.ast.Stmt.Spec, Json.ErrorMsg] = {
    def f_astStmtSpec(parser: Parser): org.sireum.lang.ast.Stmt.Spec = {
      val r = parser.parse_astStmtSpec()
      return r
    }
    val r = to(s, f_astStmtSpec _)
    return r
  }

  def from_astStmtFact(o: org.sireum.lang.ast.Stmt.Fact, isCompact: B): String = {
    val st = Printer.print_astStmtFact(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtFact(s: String): Either[org.sireum.lang.ast.Stmt.Fact, Json.ErrorMsg] = {
    def f_astStmtFact(parser: Parser): org.sireum.lang.ast.Stmt.Fact = {
      val r = parser.parse_astStmtFact()
      return r
    }
    val r = to(s, f_astStmtFact _)
    return r
  }

  def from_astStmtInvariant(o: org.sireum.lang.ast.Stmt.Invariant, isCompact: B): String = {
    val st = Printer.print_astStmtInvariant(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtInvariant(s: String): Either[org.sireum.lang.ast.Stmt.Invariant, Json.ErrorMsg] = {
    def f_astStmtInvariant(parser: Parser): org.sireum.lang.ast.Stmt.Invariant = {
      val r = parser.parse_astStmtInvariant()
      return r
    }
    val r = to(s, f_astStmtInvariant _)
    return r
  }

  def from_astStmtTheorem(o: org.sireum.lang.ast.Stmt.Theorem, isCompact: B): String = {
    val st = Printer.print_astStmtTheorem(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtTheorem(s: String): Either[org.sireum.lang.ast.Stmt.Theorem, Json.ErrorMsg] = {
    def f_astStmtTheorem(parser: Parser): org.sireum.lang.ast.Stmt.Theorem = {
      val r = parser.parse_astStmtTheorem()
      return r
    }
    val r = to(s, f_astStmtTheorem _)
    return r
  }

  def from_astStmtSpecLabel(o: org.sireum.lang.ast.Stmt.SpecLabel, isCompact: B): String = {
    val st = Printer.print_astStmtSpecLabel(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtSpecLabel(s: String): Either[org.sireum.lang.ast.Stmt.SpecLabel, Json.ErrorMsg] = {
    def f_astStmtSpecLabel(parser: Parser): org.sireum.lang.ast.Stmt.SpecLabel = {
      val r = parser.parse_astStmtSpecLabel()
      return r
    }
    val r = to(s, f_astStmtSpecLabel _)
    return r
  }

  def from_astStmtSpecBlock(o: org.sireum.lang.ast.Stmt.SpecBlock, isCompact: B): String = {
    val st = Printer.print_astStmtSpecBlock(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtSpecBlock(s: String): Either[org.sireum.lang.ast.Stmt.SpecBlock, Json.ErrorMsg] = {
    def f_astStmtSpecBlock(parser: Parser): org.sireum.lang.ast.Stmt.SpecBlock = {
      val r = parser.parse_astStmtSpecBlock()
      return r
    }
    val r = to(s, f_astStmtSpecBlock _)
    return r
  }

  def from_astStmtDeduce(o: org.sireum.lang.ast.Stmt.Deduce, isCompact: B): String = {
    val st = Printer.print_astStmtDeduce(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtDeduce(s: String): Either[org.sireum.lang.ast.Stmt.Deduce, Json.ErrorMsg] = {
    def f_astStmtDeduce(parser: Parser): org.sireum.lang.ast.Stmt.Deduce = {
      val r = parser.parse_astStmtDeduce()
      return r
    }
    val r = to(s, f_astStmtDeduce _)
    return r
  }

  def from_astStmtDeduceSteps(o: org.sireum.lang.ast.Stmt.DeduceSteps, isCompact: B): String = {
    val st = Printer.print_astStmtDeduceSteps(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astStmtDeduceSteps(s: String): Either[org.sireum.lang.ast.Stmt.DeduceSteps, Json.ErrorMsg] = {
    def f_astStmtDeduceSteps(parser: Parser): org.sireum.lang.ast.Stmt.DeduceSteps = {
      val r = parser.parse_astStmtDeduceSteps()
      return r
    }
    val r = to(s, f_astStmtDeduceSteps _)
    return r
  }

  def from_astMethodContract(o: org.sireum.lang.ast.MethodContract, isCompact: B): String = {
    val st = Printer.print_astMethodContract(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astMethodContract(s: String): Either[org.sireum.lang.ast.MethodContract, Json.ErrorMsg] = {
    def f_astMethodContract(parser: Parser): org.sireum.lang.ast.MethodContract = {
      val r = parser.parse_astMethodContract()
      return r
    }
    val r = to(s, f_astMethodContract _)
    return r
  }

  def from_astMethodContractSimple(o: org.sireum.lang.ast.MethodContract.Simple, isCompact: B): String = {
    val st = Printer.print_astMethodContractSimple(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astMethodContractSimple(s: String): Either[org.sireum.lang.ast.MethodContract.Simple, Json.ErrorMsg] = {
    def f_astMethodContractSimple(parser: Parser): org.sireum.lang.ast.MethodContract.Simple = {
      val r = parser.parse_astMethodContractSimple()
      return r
    }
    val r = to(s, f_astMethodContractSimple _)
    return r
  }

  def from_astMethodContractCases(o: org.sireum.lang.ast.MethodContract.Cases, isCompact: B): String = {
    val st = Printer.print_astMethodContractCases(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astMethodContractCases(s: String): Either[org.sireum.lang.ast.MethodContract.Cases, Json.ErrorMsg] = {
    def f_astMethodContractCases(parser: Parser): org.sireum.lang.ast.MethodContract.Cases = {
      val r = parser.parse_astMethodContractCases()
      return r
    }
    val r = to(s, f_astMethodContractCases _)
    return r
  }

  def from_astMethodContractCase(o: org.sireum.lang.ast.MethodContract.Case, isCompact: B): String = {
    val st = Printer.print_astMethodContractCase(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astMethodContractCase(s: String): Either[org.sireum.lang.ast.MethodContract.Case, Json.ErrorMsg] = {
    def f_astMethodContractCase(parser: Parser): org.sireum.lang.ast.MethodContract.Case = {
      val r = parser.parse_astMethodContractCase()
      return r
    }
    val r = to(s, f_astMethodContractCase _)
    return r
  }

  def from_astSequent(o: org.sireum.lang.ast.Sequent, isCompact: B): String = {
    val st = Printer.print_astSequent(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astSequent(s: String): Either[org.sireum.lang.ast.Sequent, Json.ErrorMsg] = {
    def f_astSequent(parser: Parser): org.sireum.lang.ast.Sequent = {
      val r = parser.parse_astSequent()
      return r
    }
    val r = to(s, f_astSequent _)
    return r
  }

  def from_astProof(o: org.sireum.lang.ast.Proof, isCompact: B): String = {
    val st = Printer.print_astProof(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astProof(s: String): Either[org.sireum.lang.ast.Proof, Json.ErrorMsg] = {
    def f_astProof(parser: Parser): org.sireum.lang.ast.Proof = {
      val r = parser.parse_astProof()
      return r
    }
    val r = to(s, f_astProof _)
    return r
  }

  def from_astProofStep(o: org.sireum.lang.ast.Proof.Step, isCompact: B): String = {
    val st = Printer.print_astProofStep(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astProofStep(s: String): Either[org.sireum.lang.ast.Proof.Step, Json.ErrorMsg] = {
    def f_astProofStep(parser: Parser): org.sireum.lang.ast.Proof.Step = {
      val r = parser.parse_astProofStep()
      return r
    }
    val r = to(s, f_astProofStep _)
    return r
  }

  def from_astProofStepRegular(o: org.sireum.lang.ast.Proof.Step.Regular, isCompact: B): String = {
    val st = Printer.print_astProofStepRegular(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astProofStepRegular(s: String): Either[org.sireum.lang.ast.Proof.Step.Regular, Json.ErrorMsg] = {
    def f_astProofStepRegular(parser: Parser): org.sireum.lang.ast.Proof.Step.Regular = {
      val r = parser.parse_astProofStepRegular()
      return r
    }
    val r = to(s, f_astProofStepRegular _)
    return r
  }

  def from_astProofStepAssume(o: org.sireum.lang.ast.Proof.Step.Assume, isCompact: B): String = {
    val st = Printer.print_astProofStepAssume(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astProofStepAssume(s: String): Either[org.sireum.lang.ast.Proof.Step.Assume, Json.ErrorMsg] = {
    def f_astProofStepAssume(parser: Parser): org.sireum.lang.ast.Proof.Step.Assume = {
      val r = parser.parse_astProofStepAssume()
      return r
    }
    val r = to(s, f_astProofStepAssume _)
    return r
  }

  def from_astProofStepAssert(o: org.sireum.lang.ast.Proof.Step.Assert, isCompact: B): String = {
    val st = Printer.print_astProofStepAssert(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astProofStepAssert(s: String): Either[org.sireum.lang.ast.Proof.Step.Assert, Json.ErrorMsg] = {
    def f_astProofStepAssert(parser: Parser): org.sireum.lang.ast.Proof.Step.Assert = {
      val r = parser.parse_astProofStepAssert()
      return r
    }
    val r = to(s, f_astProofStepAssert _)
    return r
  }

  def from_astProofStepSubProof(o: org.sireum.lang.ast.Proof.Step.SubProof, isCompact: B): String = {
    val st = Printer.print_astProofStepSubProof(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astProofStepSubProof(s: String): Either[org.sireum.lang.ast.Proof.Step.SubProof, Json.ErrorMsg] = {
    def f_astProofStepSubProof(parser: Parser): org.sireum.lang.ast.Proof.Step.SubProof = {
      val r = parser.parse_astProofStepSubProof()
      return r
    }
    val r = to(s, f_astProofStepSubProof _)
    return r
  }

  def from_astProofStepLet(o: org.sireum.lang.ast.Proof.Step.Let, isCompact: B): String = {
    val st = Printer.print_astProofStepLet(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astProofStepLet(s: String): Either[org.sireum.lang.ast.Proof.Step.Let, Json.ErrorMsg] = {
    def f_astProofStepLet(parser: Parser): org.sireum.lang.ast.Proof.Step.Let = {
      val r = parser.parse_astProofStepLet()
      return r
    }
    val r = to(s, f_astProofStepLet _)
    return r
  }

  def from_astProofStepLetParam(o: org.sireum.lang.ast.Proof.Step.Let.Param, isCompact: B): String = {
    val st = Printer.print_astProofStepLetParam(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astProofStepLetParam(s: String): Either[org.sireum.lang.ast.Proof.Step.Let.Param, Json.ErrorMsg] = {
    def f_astProofStepLetParam(parser: Parser): org.sireum.lang.ast.Proof.Step.Let.Param = {
      val r = parser.parse_astProofStepLetParam()
      return r
    }
    val r = to(s, f_astProofStepLetParam _)
    return r
  }

  def from_astProofStepStructInduction(o: org.sireum.lang.ast.Proof.Step.StructInduction, isCompact: B): String = {
    val st = Printer.print_astProofStepStructInduction(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astProofStepStructInduction(s: String): Either[org.sireum.lang.ast.Proof.Step.StructInduction, Json.ErrorMsg] = {
    def f_astProofStepStructInduction(parser: Parser): org.sireum.lang.ast.Proof.Step.StructInduction = {
      val r = parser.parse_astProofStepStructInduction()
      return r
    }
    val r = to(s, f_astProofStepStructInduction _)
    return r
  }

  def from_astProofStepStructInductionMatchCase(o: org.sireum.lang.ast.Proof.Step.StructInduction.MatchCase, isCompact: B): String = {
    val st = Printer.print_astProofStepStructInductionMatchCase(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astProofStepStructInductionMatchCase(s: String): Either[org.sireum.lang.ast.Proof.Step.StructInduction.MatchCase, Json.ErrorMsg] = {
    def f_astProofStepStructInductionMatchCase(parser: Parser): org.sireum.lang.ast.Proof.Step.StructInduction.MatchCase = {
      val r = parser.parse_astProofStepStructInductionMatchCase()
      return r
    }
    val r = to(s, f_astProofStepStructInductionMatchCase _)
    return r
  }

  def from_astProofStepStructInductionMatchDefault(o: org.sireum.lang.ast.Proof.Step.StructInduction.MatchDefault, isCompact: B): String = {
    val st = Printer.print_astProofStepStructInductionMatchDefault(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astProofStepStructInductionMatchDefault(s: String): Either[org.sireum.lang.ast.Proof.Step.StructInduction.MatchDefault, Json.ErrorMsg] = {
    def f_astProofStepStructInductionMatchDefault(parser: Parser): org.sireum.lang.ast.Proof.Step.StructInduction.MatchDefault = {
      val r = parser.parse_astProofStepStructInductionMatchDefault()
      return r
    }
    val r = to(s, f_astProofStepStructInductionMatchDefault _)
    return r
  }

  def from_astProofStepJustification(o: org.sireum.lang.ast.Proof.Step.Justification, isCompact: B): String = {
    val st = Printer.print_astProofStepJustification(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astProofStepJustification(s: String): Either[org.sireum.lang.ast.Proof.Step.Justification, Json.ErrorMsg] = {
    def f_astProofStepJustification(parser: Parser): org.sireum.lang.ast.Proof.Step.Justification = {
      val r = parser.parse_astProofStepJustification()
      return r
    }
    val r = to(s, f_astProofStepJustification _)
    return r
  }

  def from_astAssignExp(o: org.sireum.lang.ast.AssignExp, isCompact: B): String = {
    val st = Printer.print_astAssignExp(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astAssignExp(s: String): Either[org.sireum.lang.ast.AssignExp, Json.ErrorMsg] = {
    def f_astAssignExp(parser: Parser): org.sireum.lang.ast.AssignExp = {
      val r = parser.parse_astAssignExp()
      return r
    }
    val r = to(s, f_astAssignExp _)
    return r
  }

  def from_astCase(o: org.sireum.lang.ast.Case, isCompact: B): String = {
    val st = Printer.print_astCase(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astCase(s: String): Either[org.sireum.lang.ast.Case, Json.ErrorMsg] = {
    def f_astCase(parser: Parser): org.sireum.lang.ast.Case = {
      val r = parser.parse_astCase()
      return r
    }
    val r = to(s, f_astCase _)
    return r
  }

  def from_astEnumGenRange(o: org.sireum.lang.ast.EnumGen.Range, isCompact: B): String = {
    val st = Printer.print_astEnumGenRange(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astEnumGenRange(s: String): Either[org.sireum.lang.ast.EnumGen.Range, Json.ErrorMsg] = {
    def f_astEnumGenRange(parser: Parser): org.sireum.lang.ast.EnumGen.Range = {
      val r = parser.parse_astEnumGenRange()
      return r
    }
    val r = to(s, f_astEnumGenRange _)
    return r
  }

  def from_astEnumGenRangeExpr(o: org.sireum.lang.ast.EnumGen.Range.Expr, isCompact: B): String = {
    val st = Printer.print_astEnumGenRangeExpr(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astEnumGenRangeExpr(s: String): Either[org.sireum.lang.ast.EnumGen.Range.Expr, Json.ErrorMsg] = {
    def f_astEnumGenRangeExpr(parser: Parser): org.sireum.lang.ast.EnumGen.Range.Expr = {
      val r = parser.parse_astEnumGenRangeExpr()
      return r
    }
    val r = to(s, f_astEnumGenRangeExpr _)
    return r
  }

  def from_astEnumGenRangeStep(o: org.sireum.lang.ast.EnumGen.Range.Step, isCompact: B): String = {
    val st = Printer.print_astEnumGenRangeStep(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astEnumGenRangeStep(s: String): Either[org.sireum.lang.ast.EnumGen.Range.Step, Json.ErrorMsg] = {
    def f_astEnumGenRangeStep(parser: Parser): org.sireum.lang.ast.EnumGen.Range.Step = {
      val r = parser.parse_astEnumGenRangeStep()
      return r
    }
    val r = to(s, f_astEnumGenRangeStep _)
    return r
  }

  def from_astEnumGenFor(o: org.sireum.lang.ast.EnumGen.For, isCompact: B): String = {
    val st = Printer.print_astEnumGenFor(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astEnumGenFor(s: String): Either[org.sireum.lang.ast.EnumGen.For, Json.ErrorMsg] = {
    def f_astEnumGenFor(parser: Parser): org.sireum.lang.ast.EnumGen.For = {
      val r = parser.parse_astEnumGenFor()
      return r
    }
    val r = to(s, f_astEnumGenFor _)
    return r
  }

  def from_astType(o: org.sireum.lang.ast.Type, isCompact: B): String = {
    val st = Printer.print_astType(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astType(s: String): Either[org.sireum.lang.ast.Type, Json.ErrorMsg] = {
    def f_astType(parser: Parser): org.sireum.lang.ast.Type = {
      val r = parser.parse_astType()
      return r
    }
    val r = to(s, f_astType _)
    return r
  }

  def from_astTypeNamed(o: org.sireum.lang.ast.Type.Named, isCompact: B): String = {
    val st = Printer.print_astTypeNamed(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astTypeNamed(s: String): Either[org.sireum.lang.ast.Type.Named, Json.ErrorMsg] = {
    def f_astTypeNamed(parser: Parser): org.sireum.lang.ast.Type.Named = {
      val r = parser.parse_astTypeNamed()
      return r
    }
    val r = to(s, f_astTypeNamed _)
    return r
  }

  def from_astTypeFun(o: org.sireum.lang.ast.Type.Fun, isCompact: B): String = {
    val st = Printer.print_astTypeFun(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astTypeFun(s: String): Either[org.sireum.lang.ast.Type.Fun, Json.ErrorMsg] = {
    def f_astTypeFun(parser: Parser): org.sireum.lang.ast.Type.Fun = {
      val r = parser.parse_astTypeFun()
      return r
    }
    val r = to(s, f_astTypeFun _)
    return r
  }

  def from_astTypeTuple(o: org.sireum.lang.ast.Type.Tuple, isCompact: B): String = {
    val st = Printer.print_astTypeTuple(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astTypeTuple(s: String): Either[org.sireum.lang.ast.Type.Tuple, Json.ErrorMsg] = {
    def f_astTypeTuple(parser: Parser): org.sireum.lang.ast.Type.Tuple = {
      val r = parser.parse_astTypeTuple()
      return r
    }
    val r = to(s, f_astTypeTuple _)
    return r
  }

  def from_astPattern(o: org.sireum.lang.ast.Pattern, isCompact: B): String = {
    val st = Printer.print_astPattern(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astPattern(s: String): Either[org.sireum.lang.ast.Pattern, Json.ErrorMsg] = {
    def f_astPattern(parser: Parser): org.sireum.lang.ast.Pattern = {
      val r = parser.parse_astPattern()
      return r
    }
    val r = to(s, f_astPattern _)
    return r
  }

  def from_astPatternLiteral(o: org.sireum.lang.ast.Pattern.Literal, isCompact: B): String = {
    val st = Printer.print_astPatternLiteral(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astPatternLiteral(s: String): Either[org.sireum.lang.ast.Pattern.Literal, Json.ErrorMsg] = {
    def f_astPatternLiteral(parser: Parser): org.sireum.lang.ast.Pattern.Literal = {
      val r = parser.parse_astPatternLiteral()
      return r
    }
    val r = to(s, f_astPatternLiteral _)
    return r
  }

  def from_astPatternLitInterpolate(o: org.sireum.lang.ast.Pattern.LitInterpolate, isCompact: B): String = {
    val st = Printer.print_astPatternLitInterpolate(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astPatternLitInterpolate(s: String): Either[org.sireum.lang.ast.Pattern.LitInterpolate, Json.ErrorMsg] = {
    def f_astPatternLitInterpolate(parser: Parser): org.sireum.lang.ast.Pattern.LitInterpolate = {
      val r = parser.parse_astPatternLitInterpolate()
      return r
    }
    val r = to(s, f_astPatternLitInterpolate _)
    return r
  }

  def from_astPatternRef(o: org.sireum.lang.ast.Pattern.Ref, isCompact: B): String = {
    val st = Printer.print_astPatternRef(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astPatternRef(s: String): Either[org.sireum.lang.ast.Pattern.Ref, Json.ErrorMsg] = {
    def f_astPatternRef(parser: Parser): org.sireum.lang.ast.Pattern.Ref = {
      val r = parser.parse_astPatternRef()
      return r
    }
    val r = to(s, f_astPatternRef _)
    return r
  }

  def from_astPatternVarBinding(o: org.sireum.lang.ast.Pattern.VarBinding, isCompact: B): String = {
    val st = Printer.print_astPatternVarBinding(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astPatternVarBinding(s: String): Either[org.sireum.lang.ast.Pattern.VarBinding, Json.ErrorMsg] = {
    def f_astPatternVarBinding(parser: Parser): org.sireum.lang.ast.Pattern.VarBinding = {
      val r = parser.parse_astPatternVarBinding()
      return r
    }
    val r = to(s, f_astPatternVarBinding _)
    return r
  }

  def from_astPatternWildcard(o: org.sireum.lang.ast.Pattern.Wildcard, isCompact: B): String = {
    val st = Printer.print_astPatternWildcard(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astPatternWildcard(s: String): Either[org.sireum.lang.ast.Pattern.Wildcard, Json.ErrorMsg] = {
    def f_astPatternWildcard(parser: Parser): org.sireum.lang.ast.Pattern.Wildcard = {
      val r = parser.parse_astPatternWildcard()
      return r
    }
    val r = to(s, f_astPatternWildcard _)
    return r
  }

  def from_astPatternSeqWildcard(o: org.sireum.lang.ast.Pattern.SeqWildcard, isCompact: B): String = {
    val st = Printer.print_astPatternSeqWildcard(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astPatternSeqWildcard(s: String): Either[org.sireum.lang.ast.Pattern.SeqWildcard, Json.ErrorMsg] = {
    def f_astPatternSeqWildcard(parser: Parser): org.sireum.lang.ast.Pattern.SeqWildcard = {
      val r = parser.parse_astPatternSeqWildcard()
      return r
    }
    val r = to(s, f_astPatternSeqWildcard _)
    return r
  }

  def from_astPatternStructure(o: org.sireum.lang.ast.Pattern.Structure, isCompact: B): String = {
    val st = Printer.print_astPatternStructure(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astPatternStructure(s: String): Either[org.sireum.lang.ast.Pattern.Structure, Json.ErrorMsg] = {
    def f_astPatternStructure(parser: Parser): org.sireum.lang.ast.Pattern.Structure = {
      val r = parser.parse_astPatternStructure()
      return r
    }
    val r = to(s, f_astPatternStructure _)
    return r
  }

  def from_astExp(o: org.sireum.lang.ast.Exp, isCompact: B): String = {
    val st = Printer.print_astExp(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astExp(s: String): Either[org.sireum.lang.ast.Exp, Json.ErrorMsg] = {
    def f_astExp(parser: Parser): org.sireum.lang.ast.Exp = {
      val r = parser.parse_astExp()
      return r
    }
    val r = to(s, f_astExp _)
    return r
  }

  def from_astLit(o: org.sireum.lang.ast.Lit, isCompact: B): String = {
    val st = Printer.print_astLit(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astLit(s: String): Either[org.sireum.lang.ast.Lit, Json.ErrorMsg] = {
    def f_astLit(parser: Parser): org.sireum.lang.ast.Lit = {
      val r = parser.parse_astLit()
      return r
    }
    val r = to(s, f_astLit _)
    return r
  }

  def from_astExpLitB(o: org.sireum.lang.ast.Exp.LitB, isCompact: B): String = {
    val st = Printer.print_astExpLitB(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astExpLitB(s: String): Either[org.sireum.lang.ast.Exp.LitB, Json.ErrorMsg] = {
    def f_astExpLitB(parser: Parser): org.sireum.lang.ast.Exp.LitB = {
      val r = parser.parse_astExpLitB()
      return r
    }
    val r = to(s, f_astExpLitB _)
    return r
  }

  def from_astExpLitC(o: org.sireum.lang.ast.Exp.LitC, isCompact: B): String = {
    val st = Printer.print_astExpLitC(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astExpLitC(s: String): Either[org.sireum.lang.ast.Exp.LitC, Json.ErrorMsg] = {
    def f_astExpLitC(parser: Parser): org.sireum.lang.ast.Exp.LitC = {
      val r = parser.parse_astExpLitC()
      return r
    }
    val r = to(s, f_astExpLitC _)
    return r
  }

  def from_astExpLitZ(o: org.sireum.lang.ast.Exp.LitZ, isCompact: B): String = {
    val st = Printer.print_astExpLitZ(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astExpLitZ(s: String): Either[org.sireum.lang.ast.Exp.LitZ, Json.ErrorMsg] = {
    def f_astExpLitZ(parser: Parser): org.sireum.lang.ast.Exp.LitZ = {
      val r = parser.parse_astExpLitZ()
      return r
    }
    val r = to(s, f_astExpLitZ _)
    return r
  }

  def from_astExpLitF32(o: org.sireum.lang.ast.Exp.LitF32, isCompact: B): String = {
    val st = Printer.print_astExpLitF32(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astExpLitF32(s: String): Either[org.sireum.lang.ast.Exp.LitF32, Json.ErrorMsg] = {
    def f_astExpLitF32(parser: Parser): org.sireum.lang.ast.Exp.LitF32 = {
      val r = parser.parse_astExpLitF32()
      return r
    }
    val r = to(s, f_astExpLitF32 _)
    return r
  }

  def from_astExpLitF64(o: org.sireum.lang.ast.Exp.LitF64, isCompact: B): String = {
    val st = Printer.print_astExpLitF64(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astExpLitF64(s: String): Either[org.sireum.lang.ast.Exp.LitF64, Json.ErrorMsg] = {
    def f_astExpLitF64(parser: Parser): org.sireum.lang.ast.Exp.LitF64 = {
      val r = parser.parse_astExpLitF64()
      return r
    }
    val r = to(s, f_astExpLitF64 _)
    return r
  }

  def from_astExpLitR(o: org.sireum.lang.ast.Exp.LitR, isCompact: B): String = {
    val st = Printer.print_astExpLitR(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astExpLitR(s: String): Either[org.sireum.lang.ast.Exp.LitR, Json.ErrorMsg] = {
    def f_astExpLitR(parser: Parser): org.sireum.lang.ast.Exp.LitR = {
      val r = parser.parse_astExpLitR()
      return r
    }
    val r = to(s, f_astExpLitR _)
    return r
  }

  def from_astExpLitString(o: org.sireum.lang.ast.Exp.LitString, isCompact: B): String = {
    val st = Printer.print_astExpLitString(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astExpLitString(s: String): Either[org.sireum.lang.ast.Exp.LitString, Json.ErrorMsg] = {
    def f_astExpLitString(parser: Parser): org.sireum.lang.ast.Exp.LitString = {
      val r = parser.parse_astExpLitString()
      return r
    }
    val r = to(s, f_astExpLitString _)
    return r
  }

  def from_astExpStringInterpolate(o: org.sireum.lang.ast.Exp.StringInterpolate, isCompact: B): String = {
    val st = Printer.print_astExpStringInterpolate(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astExpStringInterpolate(s: String): Either[org.sireum.lang.ast.Exp.StringInterpolate, Json.ErrorMsg] = {
    def f_astExpStringInterpolate(parser: Parser): org.sireum.lang.ast.Exp.StringInterpolate = {
      val r = parser.parse_astExpStringInterpolate()
      return r
    }
    val r = to(s, f_astExpStringInterpolate _)
    return r
  }

  def from_astExpThis(o: org.sireum.lang.ast.Exp.This, isCompact: B): String = {
    val st = Printer.print_astExpThis(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astExpThis(s: String): Either[org.sireum.lang.ast.Exp.This, Json.ErrorMsg] = {
    def f_astExpThis(parser: Parser): org.sireum.lang.ast.Exp.This = {
      val r = parser.parse_astExpThis()
      return r
    }
    val r = to(s, f_astExpThis _)
    return r
  }

  def from_astExpSuper(o: org.sireum.lang.ast.Exp.Super, isCompact: B): String = {
    val st = Printer.print_astExpSuper(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astExpSuper(s: String): Either[org.sireum.lang.ast.Exp.Super, Json.ErrorMsg] = {
    def f_astExpSuper(parser: Parser): org.sireum.lang.ast.Exp.Super = {
      val r = parser.parse_astExpSuper()
      return r
    }
    val r = to(s, f_astExpSuper _)
    return r
  }

  def from_astExpUnary(o: org.sireum.lang.ast.Exp.Unary, isCompact: B): String = {
    val st = Printer.print_astExpUnary(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astExpUnary(s: String): Either[org.sireum.lang.ast.Exp.Unary, Json.ErrorMsg] = {
    def f_astExpUnary(parser: Parser): org.sireum.lang.ast.Exp.Unary = {
      val r = parser.parse_astExpUnary()
      return r
    }
    val r = to(s, f_astExpUnary _)
    return r
  }

  def from_astExpRef(o: org.sireum.lang.ast.Exp.Ref, isCompact: B): String = {
    val st = Printer.print_astExpRef(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astExpRef(s: String): Either[org.sireum.lang.ast.Exp.Ref, Json.ErrorMsg] = {
    def f_astExpRef(parser: Parser): org.sireum.lang.ast.Exp.Ref = {
      val r = parser.parse_astExpRef()
      return r
    }
    val r = to(s, f_astExpRef _)
    return r
  }

  def from_astExpBinary(o: org.sireum.lang.ast.Exp.Binary, isCompact: B): String = {
    val st = Printer.print_astExpBinary(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astExpBinary(s: String): Either[org.sireum.lang.ast.Exp.Binary, Json.ErrorMsg] = {
    def f_astExpBinary(parser: Parser): org.sireum.lang.ast.Exp.Binary = {
      val r = parser.parse_astExpBinary()
      return r
    }
    val r = to(s, f_astExpBinary _)
    return r
  }

  def from_astExpIdent(o: org.sireum.lang.ast.Exp.Ident, isCompact: B): String = {
    val st = Printer.print_astExpIdent(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astExpIdent(s: String): Either[org.sireum.lang.ast.Exp.Ident, Json.ErrorMsg] = {
    def f_astExpIdent(parser: Parser): org.sireum.lang.ast.Exp.Ident = {
      val r = parser.parse_astExpIdent()
      return r
    }
    val r = to(s, f_astExpIdent _)
    return r
  }

  def from_astExpEta(o: org.sireum.lang.ast.Exp.Eta, isCompact: B): String = {
    val st = Printer.print_astExpEta(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astExpEta(s: String): Either[org.sireum.lang.ast.Exp.Eta, Json.ErrorMsg] = {
    def f_astExpEta(parser: Parser): org.sireum.lang.ast.Exp.Eta = {
      val r = parser.parse_astExpEta()
      return r
    }
    val r = to(s, f_astExpEta _)
    return r
  }

  def from_astExpTuple(o: org.sireum.lang.ast.Exp.Tuple, isCompact: B): String = {
    val st = Printer.print_astExpTuple(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astExpTuple(s: String): Either[org.sireum.lang.ast.Exp.Tuple, Json.ErrorMsg] = {
    def f_astExpTuple(parser: Parser): org.sireum.lang.ast.Exp.Tuple = {
      val r = parser.parse_astExpTuple()
      return r
    }
    val r = to(s, f_astExpTuple _)
    return r
  }

  def from_astExpSelect(o: org.sireum.lang.ast.Exp.Select, isCompact: B): String = {
    val st = Printer.print_astExpSelect(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astExpSelect(s: String): Either[org.sireum.lang.ast.Exp.Select, Json.ErrorMsg] = {
    def f_astExpSelect(parser: Parser): org.sireum.lang.ast.Exp.Select = {
      val r = parser.parse_astExpSelect()
      return r
    }
    val r = to(s, f_astExpSelect _)
    return r
  }

  def from_astExpInvoke(o: org.sireum.lang.ast.Exp.Invoke, isCompact: B): String = {
    val st = Printer.print_astExpInvoke(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astExpInvoke(s: String): Either[org.sireum.lang.ast.Exp.Invoke, Json.ErrorMsg] = {
    def f_astExpInvoke(parser: Parser): org.sireum.lang.ast.Exp.Invoke = {
      val r = parser.parse_astExpInvoke()
      return r
    }
    val r = to(s, f_astExpInvoke _)
    return r
  }

  def from_astExpInvokeNamed(o: org.sireum.lang.ast.Exp.InvokeNamed, isCompact: B): String = {
    val st = Printer.print_astExpInvokeNamed(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astExpInvokeNamed(s: String): Either[org.sireum.lang.ast.Exp.InvokeNamed, Json.ErrorMsg] = {
    def f_astExpInvokeNamed(parser: Parser): org.sireum.lang.ast.Exp.InvokeNamed = {
      val r = parser.parse_astExpInvokeNamed()
      return r
    }
    val r = to(s, f_astExpInvokeNamed _)
    return r
  }

  def from_astExpIf(o: org.sireum.lang.ast.Exp.If, isCompact: B): String = {
    val st = Printer.print_astExpIf(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astExpIf(s: String): Either[org.sireum.lang.ast.Exp.If, Json.ErrorMsg] = {
    def f_astExpIf(parser: Parser): org.sireum.lang.ast.Exp.If = {
      val r = parser.parse_astExpIf()
      return r
    }
    val r = to(s, f_astExpIf _)
    return r
  }

  def from_astExpFunParam(o: org.sireum.lang.ast.Exp.Fun.Param, isCompact: B): String = {
    val st = Printer.print_astExpFunParam(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astExpFunParam(s: String): Either[org.sireum.lang.ast.Exp.Fun.Param, Json.ErrorMsg] = {
    def f_astExpFunParam(parser: Parser): org.sireum.lang.ast.Exp.Fun.Param = {
      val r = parser.parse_astExpFunParam()
      return r
    }
    val r = to(s, f_astExpFunParam _)
    return r
  }

  def from_astExpFun(o: org.sireum.lang.ast.Exp.Fun, isCompact: B): String = {
    val st = Printer.print_astExpFun(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astExpFun(s: String): Either[org.sireum.lang.ast.Exp.Fun, Json.ErrorMsg] = {
    def f_astExpFun(parser: Parser): org.sireum.lang.ast.Exp.Fun = {
      val r = parser.parse_astExpFun()
      return r
    }
    val r = to(s, f_astExpFun _)
    return r
  }

  def from_astExpForYield(o: org.sireum.lang.ast.Exp.ForYield, isCompact: B): String = {
    val st = Printer.print_astExpForYield(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astExpForYield(s: String): Either[org.sireum.lang.ast.Exp.ForYield, Json.ErrorMsg] = {
    def f_astExpForYield(parser: Parser): org.sireum.lang.ast.Exp.ForYield = {
      val r = parser.parse_astExpForYield()
      return r
    }
    val r = to(s, f_astExpForYield _)
    return r
  }

  def from_astExpSpec(o: org.sireum.lang.ast.Exp.Spec, isCompact: B): String = {
    val st = Printer.print_astExpSpec(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astExpSpec(s: String): Either[org.sireum.lang.ast.Exp.Spec, Json.ErrorMsg] = {
    def f_astExpSpec(parser: Parser): org.sireum.lang.ast.Exp.Spec = {
      val r = parser.parse_astExpSpec()
      return r
    }
    val r = to(s, f_astExpSpec _)
    return r
  }

  def from_astExpQuant(o: org.sireum.lang.ast.Exp.Quant, isCompact: B): String = {
    val st = Printer.print_astExpQuant(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astExpQuant(s: String): Either[org.sireum.lang.ast.Exp.Quant, Json.ErrorMsg] = {
    def f_astExpQuant(parser: Parser): org.sireum.lang.ast.Exp.Quant = {
      val r = parser.parse_astExpQuant()
      return r
    }
    val r = to(s, f_astExpQuant _)
    return r
  }

  def from_astNamedArg(o: org.sireum.lang.ast.NamedArg, isCompact: B): String = {
    val st = Printer.print_astNamedArg(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astNamedArg(s: String): Either[org.sireum.lang.ast.NamedArg, Json.ErrorMsg] = {
    def f_astNamedArg(parser: Parser): org.sireum.lang.ast.NamedArg = {
      val r = parser.parse_astNamedArg()
      return r
    }
    val r = to(s, f_astNamedArg _)
    return r
  }

  def from_astVarFragment(o: org.sireum.lang.ast.VarFragment, isCompact: B): String = {
    val st = Printer.print_astVarFragment(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astVarFragment(s: String): Either[org.sireum.lang.ast.VarFragment, Json.ErrorMsg] = {
    def f_astVarFragment(parser: Parser): org.sireum.lang.ast.VarFragment = {
      val r = parser.parse_astVarFragment()
      return r
    }
    val r = to(s, f_astVarFragment _)
    return r
  }

  def from_astDomain(o: org.sireum.lang.ast.Domain, isCompact: B): String = {
    val st = Printer.print_astDomain(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astDomain(s: String): Either[org.sireum.lang.ast.Domain, Json.ErrorMsg] = {
    def f_astDomain(parser: Parser): org.sireum.lang.ast.Domain = {
      val r = parser.parse_astDomain()
      return r
    }
    val r = to(s, f_astDomain _)
    return r
  }

  def from_astDomainType(o: org.sireum.lang.ast.Domain.Type, isCompact: B): String = {
    val st = Printer.print_astDomainType(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astDomainType(s: String): Either[org.sireum.lang.ast.Domain.Type, Json.ErrorMsg] = {
    def f_astDomainType(parser: Parser): org.sireum.lang.ast.Domain.Type = {
      val r = parser.parse_astDomainType()
      return r
    }
    val r = to(s, f_astDomainType _)
    return r
  }

  def from_astDomainRange(o: org.sireum.lang.ast.Domain.Range, isCompact: B): String = {
    val st = Printer.print_astDomainRange(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astDomainRange(s: String): Either[org.sireum.lang.ast.Domain.Range, Json.ErrorMsg] = {
    def f_astDomainRange(parser: Parser): org.sireum.lang.ast.Domain.Range = {
      val r = parser.parse_astDomainRange()
      return r
    }
    val r = to(s, f_astDomainRange _)
    return r
  }

  def from_astDomainEach(o: org.sireum.lang.ast.Domain.Each, isCompact: B): String = {
    val st = Printer.print_astDomainEach(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astDomainEach(s: String): Either[org.sireum.lang.ast.Domain.Each, Json.ErrorMsg] = {
    def f_astDomainEach(parser: Parser): org.sireum.lang.ast.Domain.Each = {
      val r = parser.parse_astDomainEach()
      return r
    }
    val r = to(s, f_astDomainEach _)
    return r
  }

  def from_astId(o: org.sireum.lang.ast.Id, isCompact: B): String = {
    val st = Printer.print_astId(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astId(s: String): Either[org.sireum.lang.ast.Id, Json.ErrorMsg] = {
    def f_astId(parser: Parser): org.sireum.lang.ast.Id = {
      val r = parser.parse_astId()
      return r
    }
    val r = to(s, f_astId _)
    return r
  }

  def from_astName(o: org.sireum.lang.ast.Name, isCompact: B): String = {
    val st = Printer.print_astName(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astName(s: String): Either[org.sireum.lang.ast.Name, Json.ErrorMsg] = {
    def f_astName(parser: Parser): org.sireum.lang.ast.Name = {
      val r = parser.parse_astName()
      return r
    }
    val r = to(s, f_astName _)
    return r
  }

  def from_astBody(o: org.sireum.lang.ast.Body, isCompact: B): String = {
    val st = Printer.print_astBody(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astBody(s: String): Either[org.sireum.lang.ast.Body, Json.ErrorMsg] = {
    def f_astBody(parser: Parser): org.sireum.lang.ast.Body = {
      val r = parser.parse_astBody()
      return r
    }
    val r = to(s, f_astBody _)
    return r
  }

  def from_astAdtParam(o: org.sireum.lang.ast.AdtParam, isCompact: B): String = {
    val st = Printer.print_astAdtParam(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astAdtParam(s: String): Either[org.sireum.lang.ast.AdtParam, Json.ErrorMsg] = {
    def f_astAdtParam(parser: Parser): org.sireum.lang.ast.AdtParam = {
      val r = parser.parse_astAdtParam()
      return r
    }
    val r = to(s, f_astAdtParam _)
    return r
  }

  def from_astMethodSig(o: org.sireum.lang.ast.MethodSig, isCompact: B): String = {
    val st = Printer.print_astMethodSig(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astMethodSig(s: String): Either[org.sireum.lang.ast.MethodSig, Json.ErrorMsg] = {
    def f_astMethodSig(parser: Parser): org.sireum.lang.ast.MethodSig = {
      val r = parser.parse_astMethodSig()
      return r
    }
    val r = to(s, f_astMethodSig _)
    return r
  }

  def from_astParam(o: org.sireum.lang.ast.Param, isCompact: B): String = {
    val st = Printer.print_astParam(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astParam(s: String): Either[org.sireum.lang.ast.Param, Json.ErrorMsg] = {
    def f_astParam(parser: Parser): org.sireum.lang.ast.Param = {
      val r = parser.parse_astParam()
      return r
    }
    val r = to(s, f_astParam _)
    return r
  }

  def from_astTypeParam(o: org.sireum.lang.ast.TypeParam, isCompact: B): String = {
    val st = Printer.print_astTypeParam(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astTypeParam(s: String): Either[org.sireum.lang.ast.TypeParam, Json.ErrorMsg] = {
    def f_astTypeParam(parser: Parser): org.sireum.lang.ast.TypeParam = {
      val r = parser.parse_astTypeParam()
      return r
    }
    val r = to(s, f_astTypeParam _)
    return r
  }

  def from_astTyped(o: org.sireum.lang.ast.Typed, isCompact: B): String = {
    val st = Printer.print_astTyped(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astTyped(s: String): Either[org.sireum.lang.ast.Typed, Json.ErrorMsg] = {
    def f_astTyped(parser: Parser): org.sireum.lang.ast.Typed = {
      val r = parser.parse_astTyped()
      return r
    }
    val r = to(s, f_astTyped _)
    return r
  }

  def from_astTypedName(o: org.sireum.lang.ast.Typed.Name, isCompact: B): String = {
    val st = Printer.print_astTypedName(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astTypedName(s: String): Either[org.sireum.lang.ast.Typed.Name, Json.ErrorMsg] = {
    def f_astTypedName(parser: Parser): org.sireum.lang.ast.Typed.Name = {
      val r = parser.parse_astTypedName()
      return r
    }
    val r = to(s, f_astTypedName _)
    return r
  }

  def from_astTypedTuple(o: org.sireum.lang.ast.Typed.Tuple, isCompact: B): String = {
    val st = Printer.print_astTypedTuple(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astTypedTuple(s: String): Either[org.sireum.lang.ast.Typed.Tuple, Json.ErrorMsg] = {
    def f_astTypedTuple(parser: Parser): org.sireum.lang.ast.Typed.Tuple = {
      val r = parser.parse_astTypedTuple()
      return r
    }
    val r = to(s, f_astTypedTuple _)
    return r
  }

  def from_astTypedFun(o: org.sireum.lang.ast.Typed.Fun, isCompact: B): String = {
    val st = Printer.print_astTypedFun(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astTypedFun(s: String): Either[org.sireum.lang.ast.Typed.Fun, Json.ErrorMsg] = {
    def f_astTypedFun(parser: Parser): org.sireum.lang.ast.Typed.Fun = {
      val r = parser.parse_astTypedFun()
      return r
    }
    val r = to(s, f_astTypedFun _)
    return r
  }

  def from_astTypedTypeVar(o: org.sireum.lang.ast.Typed.TypeVar, isCompact: B): String = {
    val st = Printer.print_astTypedTypeVar(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astTypedTypeVar(s: String): Either[org.sireum.lang.ast.Typed.TypeVar, Json.ErrorMsg] = {
    def f_astTypedTypeVar(parser: Parser): org.sireum.lang.ast.Typed.TypeVar = {
      val r = parser.parse_astTypedTypeVar()
      return r
    }
    val r = to(s, f_astTypedTypeVar _)
    return r
  }

  def from_astTypedPackage(o: org.sireum.lang.ast.Typed.Package, isCompact: B): String = {
    val st = Printer.print_astTypedPackage(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astTypedPackage(s: String): Either[org.sireum.lang.ast.Typed.Package, Json.ErrorMsg] = {
    def f_astTypedPackage(parser: Parser): org.sireum.lang.ast.Typed.Package = {
      val r = parser.parse_astTypedPackage()
      return r
    }
    val r = to(s, f_astTypedPackage _)
    return r
  }

  def from_astTypedObject(o: org.sireum.lang.ast.Typed.Object, isCompact: B): String = {
    val st = Printer.print_astTypedObject(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astTypedObject(s: String): Either[org.sireum.lang.ast.Typed.Object, Json.ErrorMsg] = {
    def f_astTypedObject(parser: Parser): org.sireum.lang.ast.Typed.Object = {
      val r = parser.parse_astTypedObject()
      return r
    }
    val r = to(s, f_astTypedObject _)
    return r
  }

  def from_astTypedEnum(o: org.sireum.lang.ast.Typed.Enum, isCompact: B): String = {
    val st = Printer.print_astTypedEnum(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astTypedEnum(s: String): Either[org.sireum.lang.ast.Typed.Enum, Json.ErrorMsg] = {
    def f_astTypedEnum(parser: Parser): org.sireum.lang.ast.Typed.Enum = {
      val r = parser.parse_astTypedEnum()
      return r
    }
    val r = to(s, f_astTypedEnum _)
    return r
  }

  def from_astTypedMethod(o: org.sireum.lang.ast.Typed.Method, isCompact: B): String = {
    val st = Printer.print_astTypedMethod(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astTypedMethod(s: String): Either[org.sireum.lang.ast.Typed.Method, Json.ErrorMsg] = {
    def f_astTypedMethod(parser: Parser): org.sireum.lang.ast.Typed.Method = {
      val r = parser.parse_astTypedMethod()
      return r
    }
    val r = to(s, f_astTypedMethod _)
    return r
  }

  def from_astTypedMethods(o: org.sireum.lang.ast.Typed.Methods, isCompact: B): String = {
    val st = Printer.print_astTypedMethods(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astTypedMethods(s: String): Either[org.sireum.lang.ast.Typed.Methods, Json.ErrorMsg] = {
    def f_astTypedMethods(parser: Parser): org.sireum.lang.ast.Typed.Methods = {
      val r = parser.parse_astTypedMethods()
      return r
    }
    val r = to(s, f_astTypedMethods _)
    return r
  }

  def from_astAttr(o: org.sireum.lang.ast.Attr, isCompact: B): String = {
    val st = Printer.print_astAttr(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astAttr(s: String): Either[org.sireum.lang.ast.Attr, Json.ErrorMsg] = {
    def f_astAttr(parser: Parser): org.sireum.lang.ast.Attr = {
      val r = parser.parse_astAttr()
      return r
    }
    val r = to(s, f_astAttr _)
    return r
  }

  def from_astTypedAttr(o: org.sireum.lang.ast.TypedAttr, isCompact: B): String = {
    val st = Printer.print_astTypedAttr(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astTypedAttr(s: String): Either[org.sireum.lang.ast.TypedAttr, Json.ErrorMsg] = {
    def f_astTypedAttr(parser: Parser): org.sireum.lang.ast.TypedAttr = {
      val r = parser.parse_astTypedAttr()
      return r
    }
    val r = to(s, f_astTypedAttr _)
    return r
  }

  def from_astResolvedAttr(o: org.sireum.lang.ast.ResolvedAttr, isCompact: B): String = {
    val st = Printer.print_astResolvedAttr(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astResolvedAttr(s: String): Either[org.sireum.lang.ast.ResolvedAttr, Json.ErrorMsg] = {
    def f_astResolvedAttr(parser: Parser): org.sireum.lang.ast.ResolvedAttr = {
      val r = parser.parse_astResolvedAttr()
      return r
    }
    val r = to(s, f_astResolvedAttr _)
    return r
  }

  def from_astResolvedInfo(o: org.sireum.lang.ast.ResolvedInfo, isCompact: B): String = {
    val st = Printer.print_astResolvedInfo(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astResolvedInfo(s: String): Either[org.sireum.lang.ast.ResolvedInfo, Json.ErrorMsg] = {
    def f_astResolvedInfo(parser: Parser): org.sireum.lang.ast.ResolvedInfo = {
      val r = parser.parse_astResolvedInfo()
      return r
    }
    val r = to(s, f_astResolvedInfo _)
    return r
  }

  def from_astResolvedInfoBuiltIn(o: org.sireum.lang.ast.ResolvedInfo.BuiltIn, isCompact: B): String = {
    val st = Printer.print_astResolvedInfoBuiltIn(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astResolvedInfoBuiltIn(s: String): Either[org.sireum.lang.ast.ResolvedInfo.BuiltIn, Json.ErrorMsg] = {
    def f_astResolvedInfoBuiltIn(parser: Parser): org.sireum.lang.ast.ResolvedInfo.BuiltIn = {
      val r = parser.parse_astResolvedInfoBuiltIn()
      return r
    }
    val r = to(s, f_astResolvedInfoBuiltIn _)
    return r
  }

  def from_astResolvedInfoPackage(o: org.sireum.lang.ast.ResolvedInfo.Package, isCompact: B): String = {
    val st = Printer.print_astResolvedInfoPackage(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astResolvedInfoPackage(s: String): Either[org.sireum.lang.ast.ResolvedInfo.Package, Json.ErrorMsg] = {
    def f_astResolvedInfoPackage(parser: Parser): org.sireum.lang.ast.ResolvedInfo.Package = {
      val r = parser.parse_astResolvedInfoPackage()
      return r
    }
    val r = to(s, f_astResolvedInfoPackage _)
    return r
  }

  def from_astResolvedInfoEnum(o: org.sireum.lang.ast.ResolvedInfo.Enum, isCompact: B): String = {
    val st = Printer.print_astResolvedInfoEnum(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astResolvedInfoEnum(s: String): Either[org.sireum.lang.ast.ResolvedInfo.Enum, Json.ErrorMsg] = {
    def f_astResolvedInfoEnum(parser: Parser): org.sireum.lang.ast.ResolvedInfo.Enum = {
      val r = parser.parse_astResolvedInfoEnum()
      return r
    }
    val r = to(s, f_astResolvedInfoEnum _)
    return r
  }

  def from_astResolvedInfoEnumElement(o: org.sireum.lang.ast.ResolvedInfo.EnumElement, isCompact: B): String = {
    val st = Printer.print_astResolvedInfoEnumElement(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astResolvedInfoEnumElement(s: String): Either[org.sireum.lang.ast.ResolvedInfo.EnumElement, Json.ErrorMsg] = {
    def f_astResolvedInfoEnumElement(parser: Parser): org.sireum.lang.ast.ResolvedInfo.EnumElement = {
      val r = parser.parse_astResolvedInfoEnumElement()
      return r
    }
    val r = to(s, f_astResolvedInfoEnumElement _)
    return r
  }

  def from_astResolvedInfoObject(o: org.sireum.lang.ast.ResolvedInfo.Object, isCompact: B): String = {
    val st = Printer.print_astResolvedInfoObject(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astResolvedInfoObject(s: String): Either[org.sireum.lang.ast.ResolvedInfo.Object, Json.ErrorMsg] = {
    def f_astResolvedInfoObject(parser: Parser): org.sireum.lang.ast.ResolvedInfo.Object = {
      val r = parser.parse_astResolvedInfoObject()
      return r
    }
    val r = to(s, f_astResolvedInfoObject _)
    return r
  }

  def from_astResolvedInfoVar(o: org.sireum.lang.ast.ResolvedInfo.Var, isCompact: B): String = {
    val st = Printer.print_astResolvedInfoVar(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astResolvedInfoVar(s: String): Either[org.sireum.lang.ast.ResolvedInfo.Var, Json.ErrorMsg] = {
    def f_astResolvedInfoVar(parser: Parser): org.sireum.lang.ast.ResolvedInfo.Var = {
      val r = parser.parse_astResolvedInfoVar()
      return r
    }
    val r = to(s, f_astResolvedInfoVar _)
    return r
  }

  def from_astResolvedInfoMethod(o: org.sireum.lang.ast.ResolvedInfo.Method, isCompact: B): String = {
    val st = Printer.print_astResolvedInfoMethod(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astResolvedInfoMethod(s: String): Either[org.sireum.lang.ast.ResolvedInfo.Method, Json.ErrorMsg] = {
    def f_astResolvedInfoMethod(parser: Parser): org.sireum.lang.ast.ResolvedInfo.Method = {
      val r = parser.parse_astResolvedInfoMethod()
      return r
    }
    val r = to(s, f_astResolvedInfoMethod _)
    return r
  }

  def from_astResolvedInfoMethods(o: org.sireum.lang.ast.ResolvedInfo.Methods, isCompact: B): String = {
    val st = Printer.print_astResolvedInfoMethods(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astResolvedInfoMethods(s: String): Either[org.sireum.lang.ast.ResolvedInfo.Methods, Json.ErrorMsg] = {
    def f_astResolvedInfoMethods(parser: Parser): org.sireum.lang.ast.ResolvedInfo.Methods = {
      val r = parser.parse_astResolvedInfoMethods()
      return r
    }
    val r = to(s, f_astResolvedInfoMethods _)
    return r
  }

  def from_astResolvedInfoTuple(o: org.sireum.lang.ast.ResolvedInfo.Tuple, isCompact: B): String = {
    val st = Printer.print_astResolvedInfoTuple(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astResolvedInfoTuple(s: String): Either[org.sireum.lang.ast.ResolvedInfo.Tuple, Json.ErrorMsg] = {
    def f_astResolvedInfoTuple(parser: Parser): org.sireum.lang.ast.ResolvedInfo.Tuple = {
      val r = parser.parse_astResolvedInfoTuple()
      return r
    }
    val r = to(s, f_astResolvedInfoTuple _)
    return r
  }

  def from_astResolvedInfoLocalVar(o: org.sireum.lang.ast.ResolvedInfo.LocalVar, isCompact: B): String = {
    val st = Printer.print_astResolvedInfoLocalVar(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astResolvedInfoLocalVar(s: String): Either[org.sireum.lang.ast.ResolvedInfo.LocalVar, Json.ErrorMsg] = {
    def f_astResolvedInfoLocalVar(parser: Parser): org.sireum.lang.ast.ResolvedInfo.LocalVar = {
      val r = parser.parse_astResolvedInfoLocalVar()
      return r
    }
    val r = to(s, f_astResolvedInfoLocalVar _)
    return r
  }

  def from_astTruthTableRow(o: org.sireum.lang.ast.TruthTable.Row, isCompact: B): String = {
    val st = Printer.print_astTruthTableRow(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astTruthTableRow(s: String): Either[org.sireum.lang.ast.TruthTable.Row, Json.ErrorMsg] = {
    def f_astTruthTableRow(parser: Parser): org.sireum.lang.ast.TruthTable.Row = {
      val r = parser.parse_astTruthTableRow()
      return r
    }
    val r = to(s, f_astTruthTableRow _)
    return r
  }

  def from_astTruthTableAssignment(o: org.sireum.lang.ast.TruthTable.Assignment, isCompact: B): String = {
    val st = Printer.print_astTruthTableAssignment(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astTruthTableAssignment(s: String): Either[org.sireum.lang.ast.TruthTable.Assignment, Json.ErrorMsg] = {
    def f_astTruthTableAssignment(parser: Parser): org.sireum.lang.ast.TruthTable.Assignment = {
      val r = parser.parse_astTruthTableAssignment()
      return r
    }
    val r = to(s, f_astTruthTableAssignment _)
    return r
  }

  def from_astTruthTableConclusion(o: org.sireum.lang.ast.TruthTable.Conclusion, isCompact: B): String = {
    val st = Printer.print_astTruthTableConclusion(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astTruthTableConclusion(s: String): Either[org.sireum.lang.ast.TruthTable.Conclusion, Json.ErrorMsg] = {
    def f_astTruthTableConclusion(parser: Parser): org.sireum.lang.ast.TruthTable.Conclusion = {
      val r = parser.parse_astTruthTableConclusion()
      return r
    }
    val r = to(s, f_astTruthTableConclusion _)
    return r
  }

  def from_astTruthTableConclusionValidity(o: org.sireum.lang.ast.TruthTable.Conclusion.Validity, isCompact: B): String = {
    val st = Printer.print_astTruthTableConclusionValidity(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astTruthTableConclusionValidity(s: String): Either[org.sireum.lang.ast.TruthTable.Conclusion.Validity, Json.ErrorMsg] = {
    def f_astTruthTableConclusionValidity(parser: Parser): org.sireum.lang.ast.TruthTable.Conclusion.Validity = {
      val r = parser.parse_astTruthTableConclusionValidity()
      return r
    }
    val r = to(s, f_astTruthTableConclusionValidity _)
    return r
  }

  def from_astTruthTableConclusionTautology(o: org.sireum.lang.ast.TruthTable.Conclusion.Tautology, isCompact: B): String = {
    val st = Printer.print_astTruthTableConclusionTautology(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astTruthTableConclusionTautology(s: String): Either[org.sireum.lang.ast.TruthTable.Conclusion.Tautology, Json.ErrorMsg] = {
    def f_astTruthTableConclusionTautology(parser: Parser): org.sireum.lang.ast.TruthTable.Conclusion.Tautology = {
      val r = parser.parse_astTruthTableConclusionTautology()
      return r
    }
    val r = to(s, f_astTruthTableConclusionTautology _)
    return r
  }

  def from_astTruthTableConclusionContradictory(o: org.sireum.lang.ast.TruthTable.Conclusion.Contradictory, isCompact: B): String = {
    val st = Printer.print_astTruthTableConclusionContradictory(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astTruthTableConclusionContradictory(s: String): Either[org.sireum.lang.ast.TruthTable.Conclusion.Contradictory, Json.ErrorMsg] = {
    def f_astTruthTableConclusionContradictory(parser: Parser): org.sireum.lang.ast.TruthTable.Conclusion.Contradictory = {
      val r = parser.parse_astTruthTableConclusionContradictory()
      return r
    }
    val r = to(s, f_astTruthTableConclusionContradictory _)
    return r
  }

  def from_astTruthTableConclusionContingent(o: org.sireum.lang.ast.TruthTable.Conclusion.Contingent, isCompact: B): String = {
    val st = Printer.print_astTruthTableConclusionContingent(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_astTruthTableConclusionContingent(s: String): Either[org.sireum.lang.ast.TruthTable.Conclusion.Contingent, Json.ErrorMsg] = {
    def f_astTruthTableConclusionContingent(parser: Parser): org.sireum.lang.ast.TruthTable.Conclusion.Contingent = {
      val r = parser.parse_astTruthTableConclusionContingent()
      return r
    }
    val r = to(s, f_astTruthTableConclusionContingent _)
    return r
  }

}