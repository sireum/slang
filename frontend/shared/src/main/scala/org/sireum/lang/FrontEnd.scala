// #Sireum
/*
 Copyright (c) 2017-2026,Robby, Kansas State University
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
import org.sireum.lang.ast.{MTransformer, Stmt}
import org.sireum.message._
import org.sireum.lang.parser._
import org.sireum.lang.{ast => AST}
import org.sireum.lang.symbol._
import org.sireum.lang.symbol.Resolver._
import org.sireum.lang.tipe._

object FrontEnd {

  @enum object Rewrite {
    "InsertConstructorVals"
    "ReplaceEnumSymbols"
  }

  @datatype class Input(val content: String,
                        val fileUriOpt: Option[String]) {
    @memoize def fingerprint: ISZ[U8] = {
      return ops.StringOps(content).sha3(T, T)
    }

    @memoize def parseGloballyResolve: ParseResult = {
      return FrontEnd.parseGloballyResolve(this)
    }
  }

  @datatype class ParseResult(val program: AST.TopUnit.Program,
                              val nameMap: NameMap,
                              val typeMap: TypeMap,
                              val messages: ISZ[Message])

  object ParseResultMap {
    @strictpure def empty: ParseResultMap = ParseResultMap(HashMap.empty)
  }

  @datatype class ParseResultMap(val map: HashMap[String, ParseResult]) {
    def update(input: Input): ParseResultMap = {
      val pr = parseGloballyResolve(input)
      return ParseResultMap(map + pr.program.fileUriOpt.get ~> pr)
    }

    def updates(par: Z, inputs: ISZ[Input]): ParseResultMap = {
      val prs = ops.ISZOps(inputs).parMapCores(parseGloballyResolve _, par)
      var m = map
      for (pr <- prs if pr.program.fileUriOpt.nonEmpty) {
        m = m + pr.program.fileUriOpt.get ~> pr
      }
      return ParseResultMap(m)
    }

    def merge(nameMap: NameMap, typeMap: TypeMap): (ISZ[Message], ISZ[AST.TopUnit.Program], NameMap, TypeMap) = {
      return ops.ISZOps(map.values).foldLeft(combineParseResult _, (ISZ[Message](), ISZ[AST.TopUnit.Program](), nameMap, typeMap))
    }
  }

  @record class InductExpander(val th: TypeHierarchy,
                               var contract: AST.MethodContract,
                               var mPosOpt: Option[Position],
                               var map: HashMap[Z, (String, String)],
                               val reporter: Reporter) extends AST.MTransformer {
    val kind: String = "Slang Rewrite"
    def claim(contract: AST.MethodContract): AST.Exp = {
      contract match {
        case contract: AST.MethodContract.Simple =>
          return if (contract.requires.isEmpty) AST.Util.bigAnd(contract.ensures, mPosOpt)
          else AST.Util.bigImply(T, contract.requires :+ AST.Util.bigAnd(contract.ensures, mPosOpt), mPosOpt)
        case _ =>
          if (!reporter.hasError) {
            reporter.error(mPosOpt, kind, "Cannot use @induct with method case contracts")
          }
          return AST.Util.trueLit
      }
    }
    override def postStmtInduct(o: AST.Stmt.Induct): MOption[AST.Stmt.Spec] = {
      val pos = o.posOpt.get
      println(claim(contract))
      th.induct(F, HashSet ++ o.locals, o.context, claim(contract), o.exp, pos) match {
        case Some(r) =>
          var caseSTs = ISZ[ST]()
          for (cas <- r.cases) {
            val deduceOpt: Option[ST] = if (cas.premises.isEmpty) None() else Some(
              st"""Deduce(
                  |  //@formatter:off
                  |  ${(for (i <- 1 to cas.premises.size) yield st"$i (  ${cas.premises(i - 1)}  ) by Premise${if (i == cas.premises.size) "" else ","} // auto-generated", "\n")}
                  |  //@formatter:on
                  |)"""
            )
            caseSTs = caseSTs :+
              st"""case ${cas.pattern.prettyST} => {
                  |  $deduceOpt
                  |  return
                  |}"""
          }
          val lines = ops.StringOps(
            st""") match {
                |  ${(caseSTs, "\n")}
                |}""".render).split((c: C) => c == '\n' || c == '\r')
          var indent = ""
          for (_ <- 0 until pos.beginColumn) {
            indent = s" $indent"
          }
          var rs = lines(0)
          for (i <- 1 until lines.size) {
            rs = s"$rs\n$indent${lines(i)}"
          }
          map = map + pos.offset ~> ("", "(")
          map = map + (pos.offset + pos.length) ~> ("", rs)
        case _ =>
          reporter.error(o.posOpt, kind, s"Cannot @induct over ${o.exp.prettyST}")
      }
      return MNone()
    }

    var mStack: Stack[(Option[Position], AST.MethodContract)] = Stack.empty

    override def preStmtMethod(o: AST.Stmt.Method): AST.MTransformer.PreResult[AST.Stmt] = {
      mStack = mStack.push((mPosOpt, contract))

      mPosOpt = o.sig.id.attr.posOpt
      contract = o.contract
      return AST.MTransformer.PreResultStmtMethod
    }

    override def postStmtMethod(o: AST.Stmt.Method): MOption[AST.Stmt] = {
      mStack.pop match {
        case Some((p, s)) =>
          mStack = s
          mPosOpt = p._1
          contract = p._2
        case _ =>
      }

      return MNone()
    }
  }


  def rewrite(kind: Rewrite.Type, isWorksheet: B, fileUriOpt: Option[String], text: String, reporter: Reporter): Option[(String, Z)] = {
    Parser.parseTopUnit[AST.TopUnit](text, isWorksheet, F, fileUriOpt, reporter) match {
      case Some(unit) if !reporter.hasError =>
        kind match {
          case Rewrite.InsertConstructorVals => return Some(AST.Util.insertConstructorVal(text, unit))
          case Rewrite.ReplaceEnumSymbols => return Some(AST.Util.replaceEnumSymbols(text, unit))
        }
      case _ => return None()
    }
  }

  def reformatProof(isWorksheet: B, fileUriOpt: Option[String], text: String): Option[(String, Z)] = {
    def reformat(content: String): Option[(String, Z)] = {
      val reporter = Reporter.create
      Parser.parseTopUnit[AST.TopUnit](content, isWorksheet, F, fileUriOpt, reporter) match {
        case Some(unit) if !reporter.hasError => return AST.Util.reformatProof(content, unit)
        case _ => return None()
      }
    }
    var content = text
    val contentOps = ops.StringOps(content)
    val crlf = ops.StringOps(content).contains("\r\n")
    if (crlf) {
      content = contentOps.replaceAllLiterally("\r\n", "\n")
    }
    var rOpt: Option[(String, Z)] = reformat(text) match {
      case Some((r, n)) => reformat(r) match {
        case Some((r2, m)) => if (r == r2) Some((r, n)) else Some((r2, n + m))
        case _ => Some((r, n))
      }
      case _ => None()
    }
    if (crlf) {
      rOpt match {
        case Some((r, n)) => rOpt = Some((ops.StringOps(r).replaceAllLiterally("\n", "\r\n"), n))
        case _ =>
      }
    }
    return rOpt
  }

  def insertProofStep(isWorksheet: B, fileUriOpt: Option[String], text: String, insert: String, line: Z): Option[String] = {
    val reporter = Reporter.create
    var content = text
    val contentOps = ops.StringOps(content)
    val crlf = ops.StringOps(content).contains("\r\n")
    if (crlf) {
      content = contentOps.replaceAllLiterally("\r\n", "\n")
    }
    Parser.parseTopUnit[AST.TopUnit](content, isWorksheet, F, fileUriOpt, reporter) match {
      case Some(unit) if !reporter.hasError =>
        var rOpt = AST.Util.insertProofStep(content, unit, insert, line)
        if (crlf) {
          rOpt match {
            case Some(r) => rOpt = Some(ops.StringOps(r).replaceAllLiterally("\n", "\r\n"))
            case _ =>
          }
        }
        return rOpt
      case _ => return None()
    }
  }

  @pure def parseGloballyResolve(input: Input): ParseResult = {
    val reporter = Reporter.create
    val topUnitOpt = Parser(input.content).parseTopUnit[AST.TopUnit](F, F, input.fileUriOpt, reporter)
    val nameMap: NameMap = HashSMap.empty
    val typeMap: TypeMap = HashSMap.empty
    if (reporter.hasError) {
      return ParseResult(AST.TopUnit.Program.empty(fileUriOpt = input.fileUriOpt), nameMap, typeMap,
        reporter.messages)
    }
    topUnitOpt match {
      case Some(program: AST.TopUnit.Program) =>
        val gdr = GlobalDeclarationResolver(nameMap, typeMap, Reporter.create)
        gdr.resolveProgram(program)
        reporter.reports(gdr.reporter.messages)
        return ParseResult(program, gdr.globalNameMap, gdr.globalTypeMap, reporter.messages)
      case _ =>
        return ParseResult(AST.TopUnit.Program.empty, nameMap, typeMap, reporter.messages)
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

  @pure def parseProgramAndGloballyResolve(par: Z,
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

  @memoize def checkedLibraryReporter: (TypeChecker, Reporter) = {
    def libraryReporterPar(par: Z): (TypeChecker, Reporter) = {
      val (initNameMap, initTypeMap) =
        Resolver.addBuiltIns(HashSMap.empty, HashSMap.empty)
      val (reporter, _, nameMap, typeMap) =
        parseProgramAndGloballyResolve(par, for (f <- Library.files) yield Input(f._2, f._1), initNameMap, initTypeMap)
      val th =
        TypeHierarchy.build(F, TypeHierarchy(nameMap, typeMap, Poset.empty, HashSMap.empty), reporter)
      val thOutlined = TypeOutliner.checkOutline(par, T, th, reporter)
      val tc = TypeChecker(thOutlined, ISZ(), F, TypeChecker.ModeContext.Code, T)
      val r = (tc, reporter)
      return r
    }

    val par = Ext.par
    val (tc, reporter) = libraryReporterPar(par)
    if (reporter.hasError) {
      reporter.printMessages()
      halt("Erroneous Slang library detected during outlining")
    }
    val th = tc.typeHierarchy
    val th2 = TypeChecker.checkComponents(par, T, th, th.nameMap, th.typeMap, reporter)
    if (reporter.hasError) {
      reporter.printMessages()
      halt("Erroneous Slang library detected during type checking")
    }
    return (TypeChecker(th2, ISZ(), F, TypeChecker.ModeContext.Code, T), reporter)
  }

  @memoize def checkedSharedMaps: (NameMap, TypeMap) = {
    val (tc, _) = checkedLibraryReporter
    var nameMap = tc.nameMap
    var typeMap = tc.typeMap
    var uriOpts = HashSet.empty[Option[String]]
    for (p <- Library.sharedFiles) {
      uriOpts = uriOpts + p._1
    }
    for (info <- nameMap.values) {
      info.posOpt match {
        case Some(pos) =>
          if (!uriOpts.contains(pos.uriOpt)) {
            nameMap = nameMap - info.name ~> info
          }
        case _ =>
      }
    }
    for (info <- typeMap.values) {
      info.posOpt match {
        case Some(pos) =>
          if (!uriOpts.contains(pos.uriOpt)) {
            typeMap = typeMap - info.name ~> info
          }
        case _ =>
      }
    }
    return (nameMap, typeMap)
  }

  def checkWorksheet(par: Z,
                     thOpt: Option[TypeHierarchy],
                     program: AST.TopUnit.Program,
                     reporter: Reporter): (TypeHierarchy, AST.TopUnit.Program) = {

    AST.Util.checkScript(program, reporter)

    if (reporter.hasError) {
      return (TypeHierarchy.empty, program)
    }

    val gdr = GlobalDeclarationResolver(HashSMap.empty, HashSMap.empty, Reporter.create)
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

    val th: TypeHierarchy = thOpt match {
      case Some(thi) => thi
      case _ =>
        val (tc, rep) = checkedLibraryReporter
        if (rep.hasIssue) {
          reporter.reports(rep.messages)
          return (tc.typeHierarchy, program)
        }
        tc.typeHierarchy
    }

    if (gdr.reporter.hasError) {
      reporter.reports(gdr.reporter.messages)
      return (th, program)
    }

    val th2: TypeHierarchy = {
      val (messages, _, nameMap, typeMap) =
        combineParseResult(
          (ISZ(), ISZ(), th.nameMap, th.typeMap),
          ParseResult(AST.TopUnit.Program.empty, gdr.globalNameMap, gdr.globalTypeMap, ISZ())
        )
      reporter.reports(messages)
      if (reporter.hasIssue) {
        return (th, program)
      }

      TypeHierarchy.build(F, th(nameMap = nameMap, typeMap = typeMap), reporter)
    }

    if (reporter.hasError) {
      return (th2, program)
    }

    val th3 = TypeOutliner.checkOutline(par, T, th2, reporter)
    if (reporter.hasError) {
      return (th3, program)
    }

    var nameMap: NameMap = HashSMap.empty
    var typeMap: TypeMap = HashSMap.empty

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
    val (newScope, newBody) = typeChecker.checkBody(T, F, None(), scope, program.body, reporter)

    nameMap = typeChecker.nameMap

    var currScopeOpt = Option.some[Scope](newScope)
    while (currScopeOpt.nonEmpty) {
      currScopeOpt.get match {
        case scope: Scope.Local =>
          for (info <- scope.nameMap.values) {
            info match {
              case info: Info.LocalVar => nameMap = nameMap + info.name ~> info
              case _ =>
            }
          }
          currScopeOpt = scope.outerOpt
        case _: Scope.Global => currScopeOpt = None()
      }
    }
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
              val newStmt = stmt(attr = info.ast.attr)
              nameMap = nameMap + ISZ(id) ~> info(ast = newStmt)
              newStmts = newStmts :+ newStmt
            case _ =>
              newStmts = newStmts :+ stmt
          }
        case stmt: AST.Stmt.Theorem =>
          val id = stmt.id.value
          gdr.globalNameMap.get(ISZ(id)) match {
            case Some(info: Info.Theorem) =>
              val newStmt = stmt(attr = info.ast.attr)
              nameMap = nameMap + ISZ(id) ~> info(ast = newStmt)
              newStmts = newStmts :+ newStmt
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

  @ext("$SlangFiles") object Ext {
    def par: Z = $
  }
}
