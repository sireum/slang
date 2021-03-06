// #Sireum
/*
 Copyright (c) 2017-2021, Robby, Kansas State University
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
import org.sireum.message._
import org.sireum.lang.parser._
import org.sireum.lang.{ast => AST}
import org.sireum.lang.symbol._
import org.sireum.lang.symbol.Resolver._
import org.sireum.lang.tipe._

object FrontEnd {

  @enum object Rewrite {
    "InsertConstructorVals"
    "RenumberProofSteps"
    "ReplaceEnumSymbols"
  }

  @datatype class Input(val content: String,
                        val fileUriOpt: Option[String],
                        val timestamp: Z)

  @datatype class ParseResult(val content: String,
                              val fileUriOpt: Option[String],
                              val timestamp: Z,
                              val program: AST.TopUnit.Program,
                              val nameMap: NameMap,
                              val typeMap: TypeMap,
                              val messages: ISZ[Message])

  object ParseResultMap {
    @strictpure def empty: ParseResultMap = ParseResultMap(HashMap.empty)
  }

  @datatype class ParseResultMap(val map: HashMap[String, ParseResult]) {
    def update(input: Input): ParseResultMap = {
      val pr = parseGloballyResolve(input)
      return ParseResultMap(map + pr.fileUriOpt.get ~> pr)
    }

    def updates(par: B, inputs: ISZ[Input]): ParseResultMap = {
      val prs: ISZ[ParseResult] = if (par) ops.ISZOps(inputs).parMap(parseGloballyResolve _) else ops.ISZOps(inputs).map(parseGloballyResolve _)
      var m = map
      for (pr <- prs) {
        m = m + pr.fileUriOpt.get ~> pr
      }
      return ParseResultMap(m)
    }

    def merge(nameMap: NameMap, typeMap: TypeMap): (ISZ[Message], ISZ[AST.TopUnit.Program], NameMap, TypeMap) = {
      return ops.ISZOps(map.values).foldLeft(combineParseResult _, (ISZ[Message](), ISZ[AST.TopUnit.Program](), nameMap, typeMap))
    }
  }

  def rewrite(kind: Rewrite.Type, isWorksheet: B, fileUriOpt: Option[String], text: String, reporter: Reporter): Option[(String, Z)] = {
    Parser.parseTopUnit[AST.TopUnit](text, isWorksheet, F, fileUriOpt, reporter) match {
      case Some(program) if !reporter.hasError =>
        kind match {
          case Rewrite.InsertConstructorVals => return Some(AST.Util.insertConstructorVal(text, program))
          case Rewrite.RenumberProofSteps => return Some(AST.Util.renumberProofSteps(text, program, reporter))
          case Rewrite.ReplaceEnumSymbols => return Some(AST.Util.replaceEnumSymbols(text, program))
        }
      case _ => return None()
    }
  }

  @pure def parseGloballyResolve(input: Input): ParseResult = {
    val reporter = Reporter.create
    val topUnitOpt = Parser(input.content).parseTopUnit[AST.TopUnit](F, F, input.fileUriOpt, reporter)
    val nameMap = HashMap.empty[QName, Info]
    val typeMap = HashMap.empty[QName, TypeInfo]
    if (reporter.hasError) {
      return ParseResult(input.content, input.fileUriOpt, input.timestamp, AST.TopUnit.Program.empty, nameMap, typeMap,
        reporter.messages)
    }
    topUnitOpt match {
      case Some(program: AST.TopUnit.Program) =>
        val gdr = GlobalDeclarationResolver(nameMap, typeMap, Reporter.create)
        gdr.resolveProgram(program)
        reporter.reports(gdr.reporter.messages)
        return ParseResult(input.content, input.fileUriOpt, input.timestamp, program, gdr.globalNameMap,
          gdr.globalTypeMap, reporter.messages)
      case _ =>
        return ParseResult(input.content, input.fileUriOpt, input.timestamp, AST.TopUnit.Program.empty, nameMap,
          typeMap, reporter.messages)
    }
  }

  @pure def combineParseResult(r: (ISZ[Message], ISZ[AST.TopUnit.Program], NameMap, TypeMap),
                               u: ParseResult): (ISZ[Message], ISZ[AST.TopUnit.Program], NameMap, TypeMap) = {
    var rNameMap = r._3
    var rTypeMap = r._4
    val uNameMap = u.nameMap
    val uTypeMap = u.typeMap
    val reporter = Reporter.create
    for (p <- uNameMap.entries) {
      val name = p._1
      val uInfo = p._2
      rNameMap.get(name) match {
        case Some(rInfo) if !isPosUriSuffixEq(rInfo.posOpt, uInfo.posOpt) =>
          (rInfo, uInfo) match {
            case (_: Info.Package, _: Info.Package) =>
            case _ =>
              rInfo.posOpt match {
                case Some(pos) =>
                  val file: String = pos.uriOpt match {
                    case Some(fileUri) => s" in $fileUri"
                    case _ => ""
                  }
                  reporter.error(
                    uInfo.posOpt,
                    resolverKind,
                    st"Name '${(name, ".")}' has already been declared at [${pos.beginLine}, ${pos.beginColumn}]$file".render
                  )
                case _ =>
              }
          }
        case _ => rNameMap = rNameMap + name ~> uInfo
      }
    }
    for (p <- uTypeMap.entries) {
      val name = p._1
      val uInfo = p._2
      rTypeMap.get(name) match {
        case Some(rInfo) if !isPosUriSuffixEq(rInfo.posOpt, uInfo.posOpt) =>
          rInfo.posOpt match {
            case Some(pos) =>
              val file: String = pos.uriOpt match {
                case Some(fileUri) => s" in $fileUri"
                case _ => ""
              }
              reporter.error(
                uInfo.posOpt,
                resolverKind,
                st"Type named '${(name, ".")}' has already been declared at [${pos.beginLine}, ${pos.beginColumn}]$file".render
              )
            case _ =>
          }
        case _ => rTypeMap = rTypeMap + name ~> uInfo
      }
    }
    val programs: ISZ[AST.TopUnit.Program] = if (u.program == AST.TopUnit.Program.empty) r._2 else r._2 :+ u.program
    return (r._1 ++ u.messages ++ reporter.messages, programs, rNameMap, rTypeMap)
  }

  @pure def parseProgramAndGloballyResolve(par: B,
                                           inputs: ISZ[Input],
                                           globalNameMap: NameMap,
                                           globalTypeMap: TypeMap): (Reporter, ISZ[AST.TopUnit.Program], NameMap, TypeMap) = {
    val prm = ParseResultMap.empty.updates(par, inputs)
    val t = prm.merge(globalNameMap, globalTypeMap)
    val p = addBuiltIns(t._3, t._4)
    val reporter = Reporter.create
    reporter.reports(t._1)
    val nameMap = p._1
    val typeMap = p._2
    for (program <- t._2) {
      for (stmt <- program.body.stmts) {
        stmt match {
          case stmt: AST.Stmt.Import =>
            Resolver.checkImport(AST.Util.ids2strings(program.packageName.ids), stmt, nameMap, typeMap, reporter)
          case _ =>
        }
      }
    }
    return (reporter, t._2, nameMap, typeMap)
  }

  def libraryReporter: (TypeChecker, Reporter) = {
    val (initNameMap, initTypeMap) =
      Resolver.addBuiltIns(HashMap.empty, HashMap.empty)
    val (reporter, _, nameMap, typeMap) =
      parseProgramAndGloballyResolve(T, for (f <- Library.files) yield Input(f._2, f._1, 0), initNameMap, initTypeMap)
    val th =
      TypeHierarchy.build(TypeHierarchy(nameMap, typeMap, Poset.empty, HashMap.empty), reporter)
    val thOutlined = TypeOutliner.checkOutline(T, T, th, reporter)
    val tc = TypeChecker(thOutlined, ISZ(), F, TypeChecker.ModeContext.Code, T)
    val r = (tc, reporter)
    return r
  }

  @memoize def checkedLibraryReporter: (TypeChecker, Reporter) = {
    val (tc, reporter) = libraryReporter
    val th = tc.typeHierarchy
    val th2 = TypeChecker.checkComponents(T, T, th, th.nameMap, th.typeMap, reporter)
    return (TypeChecker(th2, ISZ(), F, TypeChecker.ModeContext.Code, T), reporter)
  }

  def checkWorksheet(
    par: B,
    thOpt: Option[TypeHierarchy],
    program: AST.TopUnit.Program,
    reporter: Reporter
  ): (TypeHierarchy, AST.TopUnit.Program) = {

    AST.Util.checkScript(program, reporter)

    if (reporter.hasError) {
      return (TypeHierarchy.empty, program)
    }

    val th: TypeHierarchy = thOpt match {
      case Some(thi) => thi
      case _ =>
        val (tc, rep) = libraryReporter
        if (rep.hasIssue) {
          reporter.reports(rep.messages)
          return (tc.typeHierarchy, program)
        }
        tc.typeHierarchy
    }

    val gdr = GlobalDeclarationResolver(HashMap.empty, HashMap.empty, Reporter.create)
    gdr.resolveProgram(
      program(
        body = program.body(
          stmts = program.body.stmts.filter(
            (stmt: AST.Stmt) =>
              stmt match {
                case _: AST.Stmt.Method => F
                case _: AST.Stmt.SpecMethod => F
                case _: AST.Stmt.Var => F
                case _: AST.Stmt.VarPattern => F
                case _: AST.Stmt.SpecVar => F
                case _ => T
            }
          )
        )
      )
    )

    if (gdr.reporter.hasError) {
      reporter.reports(gdr.reporter.messages)
      return (th, program)
    }

    val th2: TypeHierarchy = {
      val (messages, _, nameMap, typeMap) =
        combineParseResult(
          (ISZ(), ISZ(), th.nameMap, th.typeMap),
          ParseResult("", None(), 0, AST.TopUnit.Program.empty, gdr.globalNameMap, gdr.globalTypeMap, ISZ())
        )
      reporter.reports(messages)
      if (reporter.hasIssue) {
        return (th, program)
      }

      TypeHierarchy.build(th(nameMap = nameMap, typeMap = typeMap), reporter)
    }

    if (reporter.hasError) {
      return (th2, program)
    }

    val th3 = TypeOutliner.checkOutline(par, T, th2, reporter)
    if (reporter.hasError) {
      return (th3, program)
    }

    var nameMap: NameMap = HashMap.empty
    var typeMap: TypeMap = HashMap.empty

    for (name <- gdr.globalNameMap.keys) {
      nameMap = nameMap + name ~> th3.nameMap.get(name).get
    }

    for (name <- gdr.globalTypeMap.keys) {
      typeMap = typeMap + name ~> th3.typeMap.get(name).get
    }

    val th4 = TypeChecker.checkComponents(par, T, th3, nameMap, typeMap, reporter)
    if (reporter.hasError) {
      return (th4, program)
    }

    val typeChecker = TypeChecker(th4, ISZ(), F, TypeChecker.ModeContext.Code, T)
    val scope = Scope.Local.create(HashMap.empty, Scope.Global(ISZ(), ISZ(), ISZ()))
    val (newScope, newBody) = typeChecker.checkBody(T, None(), scope, program.body, reporter)

    nameMap = typeChecker.nameMap

    var newStmts = ISZ[AST.Stmt]()
    for (stmt <- newBody.stmts) {
      stmt match {
        case stmt: AST.Stmt.Method =>
          val id = stmt.sig.id.value
          newScope.nameMap.get(id) match {
            case Some(info: Info.Method) => nameMap = nameMap + ISZ(id) ~> info(ast = stmt)
            case _ =>
          }
          newStmts = newStmts :+ stmt
        case stmt: AST.Stmt.SpecMethod =>
          val id = stmt.sig.id.value
          newScope.nameMap.get(id) match {
            case Some(info: Info.SpecMethod) => nameMap = nameMap + ISZ(id) ~> info(ast = stmt)
            case _ =>
          }
          newStmts = newStmts :+ stmt
        case stmt: AST.Stmt.Fact =>
          val id = stmt.id.value
          gdr.globalNameMap.get(ISZ(id)) match {
            case Some(info: Info.Fact) =>
              val newStmt = stmt(attr = stmt.attr(resOpt = info.ast.attr.resOpt))
              nameMap = nameMap + ISZ(id) ~> info(ast = newStmt)
            case _ =>
              newStmts = newStmts :+ stmt
          }
        case stmt: AST.Stmt.Theorem =>
          val id = stmt.id.value
          gdr.globalNameMap.get(ISZ(id)) match {
            case Some(info: Info.Theorem) =>
              val newStmt = stmt(attr = stmt.attr(resOpt = info.ast.attr.resOpt))
              nameMap = nameMap + ISZ(id) ~> info(ast = newStmt)
            case _ =>
              newStmts = newStmts :+ stmt
          }
        case stmt: AST.Stmt.Inv =>
          val id = stmt.id.value
          gdr.globalNameMap.get(ISZ(id)) match {
            case Some(info: Info.Inv) =>
              val newStmt = stmt(attr = stmt.attr(resOpt = info.ast.attr.resOpt))
              nameMap = nameMap + ISZ(id) ~> info(ast = newStmt)
              newStmts = newStmts :+ newStmt
            case _ =>
              newStmts = newStmts :+ stmt
          }
        case stmt: AST.Stmt.TypeAlias =>
          val id = stmt.id.value
          th4.typeMap.get(ISZ(id)) match {
            case Some(info: TypeInfo.TypeAlias) =>
              val newStmt = info.ast
              newStmts = newStmts :+ newStmt
            case _ =>
              newStmts = newStmts :+ stmt
          }
        case _ => newStmts = newStmts :+ stmt
      }
    }

    return (typeChecker.typeHierarchy(nameMap = nameMap), program(body = newBody(stmts = newStmts)))
  }

}
