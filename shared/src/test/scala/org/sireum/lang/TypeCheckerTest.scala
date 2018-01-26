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
import org.sireum.lang.ast.{ResolvedAttr, Transformer, TypedAttr}
import org.sireum.lang.parser._
import org.sireum.lang.symbol._
import org.sireum.lang.tipe._
import org.sireum.lang.util.AccumulatingReporter
import org.sireum.test.SireumSpec

class TypeCheckerTest extends SireumSpec {

  {

    implicit val _spec: SireumSpec = this

    "Passing" - {

      "Stmt" - {

        *(passingStmt("assert(true)"))

        *(passingStmt("assume(1 + 3 > 2)"))

        *(passingStmt("""assert(1 + 3 > 2, "ok")"""))

        *(passingStmt("""println(1, 2, "a", "b")"""))

        *(passingStmt("""eprintln(1, 2, "a", "b")"""))

        *(passingStmt("""print(1, 2, "a", "b")"""))

        *(passingStmt("""eprint(1, 2, "a", "b")"""))
      }

      "Worksheet" - {

        *(passingWorksheet(
          """import org.sireum._
            |assert(3 > 0)
          """.stripMargin))
      }
    }

    "Failing" - {

      "Stmt" - {

        *(failingStmt("assert(1)", "but found org.sireum.Z"))
      }

    }
  }

  def passingStmt(input: Predef.String): Boolean = testStmt(input, isPassing = true)

  def failingStmt(input: Predef.String, msg: Predef.String): Boolean = testStmt(input, isPassing = false, msg)

  def passingWorksheet(input: Predef.String): Boolean = testWorksheet(input, isPassing = true)

  def failingWorksheet(input: Predef.String, msg: Predef.String): Boolean = testWorksheet(input, isPassing = false, msg)

  def testWorksheet(input: Predef.String, isPassing: Boolean, msg: Predef.String = ""): Boolean = {
    val reporter = AccumulatingReporter.create
    Parser(input).parseTopUnit[ast.TopUnit.Program](
      allowSireum = F, isWorksheet = T, isDiet = F, None(), reporter) match {
      case Some(program) =>
        TypeChecker.checkWorksheet(program, reporter)
      case _ =>
    }
    !reporter.hasIssue
  }

  def testStmt(input: Predef.String, isPassing: Boolean, msg: Predef.String = ""): Boolean = {
    val (th, rep) = TypeChecker.typeHierarchyReporter
    if (rep.hasIssue) {
      rep.printMessages()
      return false
    }
    val typeChecker = TypeChecker(th)
    val stmt = Parser(input).parseStmt[ast.Stmt]
    val scope = Resolver.Scope.Local(HashMap.empty, HashMap.empty, Some(Resolver.Scope.Global(ISZ(), ISZ(), ISZ())))
    val reporter = AccumulatingReporter.create
    val (_, checkedStmt) = typeChecker.checkStmt(scope, stmt, reporter)
    if (isPassing) {
      val t = ast.Transformer(new ast.Transformer.PrePost[Unit] {
        override def preTypedAttr(ctx: Unit, o: TypedAttr): Transformer.PreResult[Unit, TypedAttr] = {
          assert(o.typedOpt.nonEmpty)
          super.preTypedAttr(ctx, o)
        }

        override def preResolvedAttr(ctx: Unit, o: ResolvedAttr): Transformer.PreResult[Unit, ResolvedAttr] = {
          assert(o.resOpt.nonEmpty && o.typedOpt.nonEmpty)
          super.preResolvedAttr(ctx, o)
        }

        def string: String = ""
      })
      t.transformStmt((), checkedStmt)
      !reporter.hasIssue
    } else {
      reporter.hasIssue && reporter.errors.elements.exists(_.message.value.contains(msg))
    }
  }
}