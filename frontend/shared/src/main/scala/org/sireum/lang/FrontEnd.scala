// #Sireum
/*
 Copyright (c) 2018, Robby, Kansas State University
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

  @pure def parseProgramAndGloballyResolve(
    sources: ISZ[(Option[String], String)],
    globalNameMap: NameMap,
    globalTypeMap: TypeMap
  ): (Reporter, ISZ[AST.TopUnit.Program], NameMap, TypeMap) = {
    @pure def parseGloballyResolve(p: (Option[String], String)): (Reporter, AST.TopUnit.Program, NameMap, TypeMap) = {
      val reporter = Reporter.create
      val r = Parser(p._2).parseTopUnit[AST.TopUnit.Program](T, F, F, p._1, reporter)
      val nameMap = HashMap.empty[QName, Info]
      val typeMap = HashMap.empty[QName, TypeInfo]
      if (reporter.hasError) {
        return (reporter, AST.TopUnit.Program.empty, nameMap, typeMap)
      }
      r match {
        case Some(program) =>
          val gdr = GlobalDeclarationResolver(nameMap, typeMap, Reporter.create)
          gdr.resolveProgram(program)
          reporter.reports(gdr.reporter.messages)
          return (reporter, program, gdr.globalNameMap, gdr.globalTypeMap)
        case _ => return (reporter, AST.TopUnit.Program.empty, nameMap, typeMap)
      }
    }

    val t = ops
      .ISZOps(sources)
      .parMapFoldLeft[
        (Reporter, AST.TopUnit.Program, NameMap, TypeMap),
        (Reporter, ISZ[AST.TopUnit.Program], NameMap, TypeMap)
      ](parseGloballyResolve _, combine _, (Reporter.create, ISZ(), globalNameMap, globalTypeMap))
    val p = addBuiltIns(t._3, t._4)
    return (t._1, t._2, p._1, p._2)
  }

  def libraryReporter: (TypeChecker, Reporter) = {
    val (initNameMap, initTypeMap) =
      Resolver.addBuiltIns(HashMap.empty, HashMap.empty)
    val (reporter, _, nameMap, typeMap) =
      parseProgramAndGloballyResolve(Library.files, initNameMap, initTypeMap)
    val th =
      TypeHierarchy.build(TypeHierarchy(nameMap, typeMap, Poset.empty, HashMap.empty), reporter)
    val thOutlined = TypeOutliner.checkOutline(th, reporter)
    val tc = TypeChecker(thOutlined, ISZ(), F)
    val r = (tc, reporter)
    return r
  }

  @memoize def checkedLibraryReporter: (TypeChecker, Reporter) = {
    val (tc, reporter) = libraryReporter
    val th = tc.typeHierarchy
    val th2 = TypeChecker.checkComponents(th, th.nameMap, th.typeMap, reporter)
    return (TypeChecker(th2, ISZ(), F), reporter)
  }

  def checkWorksheet(
    thOpt: Option[TypeHierarchy],
    program: AST.TopUnit.Program,
    reporter: Reporter
  ): (TypeHierarchy, AST.TopUnit.Program) = {
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
          stmts = program.body.stmts.withFilter(
            stmt =>
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
      val (rep, _, nameMap, typeMap) =
        Resolver.combine(
          (Reporter.create, ISZ(), th.nameMap, th.typeMap),
          (Reporter.create, AST.TopUnit.Program.empty, gdr.globalNameMap, gdr.globalTypeMap)
        )

      if (rep.hasIssue) {
        reporter.reports(rep.messages)
        return (th, program)
      }

      TypeHierarchy.build(th(nameMap = nameMap, typeMap = typeMap), reporter)
    }

    if (reporter.hasError) {
      return (th2, program)
    }

    val th3 = TypeOutliner.checkOutline(th2, reporter)
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

    val th4 = TypeChecker.checkComponents(th3, nameMap, typeMap, reporter)
    if (reporter.hasError) {
      return (th4, program)
    }

    val typeChecker = TypeChecker(th4, ISZ(), F)
    val scope = Scope.Local(HashMap.empty, HashMap.empty, None(), None(), Some(Scope.Global(ISZ(), ISZ(), ISZ())))
    val newBody = typeChecker.checkBody(None(), scope, program.body, reporter)
    return (th4, program(body = newBody))
  }

}
