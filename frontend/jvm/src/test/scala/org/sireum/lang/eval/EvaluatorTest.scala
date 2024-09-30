/*
 Copyright (c) 2017-2024, Robby, Kansas State University
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

package org.sireum.lang.eval

import org.sireum._
import org.sireum.lang.{FrontEnd, LibraryTypeCheckingTest}
import org.sireum.lang.ast._
import org.sireum.lang.parser.Parser
import org.sireum.lang.tipe._
import org.sireum.message._
import org.sireum.test._

class EvaluatorTest extends TestSuite {

  lazy val typeChecker: TypeChecker = LibraryTypeCheckingTest.tc

  val tests = Tests {

    "Passing" - {

      "Worksheet" - {

        * - passingWorksheet(
          """import org.sireum._
            |println("Hello World! ", 7 * 6)""".stripMargin)

      }
    }

    "Failing" - {

      "Worksheet" - {

        * - failingWorksheet(
          """import org.sireum._
            |println("Hello World! ", 7 / 0)""".stripMargin, "Division by zero")

      }
    }
  }

  def passingWorksheet(input: Predef.String): Unit =
    assert(testWorksheet(input, isPassing = true))

  def failingWorksheet(input: Predef.String, msg: Predef.String): Unit =
    assert(testWorksheet(input, isPassing = false, msg))

  def testWorksheet(input: Predef.String, isPassing: Boolean, msg: Predef.String = ""): Boolean = {
    val reporter = Reporter.create
    def end(): Boolean = {
      if (reporter.hasIssue) {
        if (isPassing) {
          reporter.printMessages()
          return false
        } else {
          return reporter.messages.elements.exists(_.text.value.contains(msg))
        }
      }
      assert(isPassing)

      !reporter.hasIssue
    }
    val (th, unit) = Parser(input).parseTopUnit[TopUnit.Program](isWorksheet = T, isDiet = F, None(), reporter) match {
      case Some(program) if !reporter.hasIssue =>
        val pair = FrontEnd.checkWorksheet(0, Some(typeChecker.typeHierarchy), program, reporter)
        if (reporter.hasIssue) {
          return end()
        }
        pair
      case _ => return end()
    }

    System.setProperty("org.sireum.silenthalt", "true")

    val ev = Evaluator(th, State.empty(1024), ISZ(LibJvmUtil.Ext.create), message.Reporter.create)
    try {
      ev.evalWorksheet(unit)
    } catch {
      case _: Throwable =>
    }
    reporter.reports(ev.reporter.messages)
    end()
  }
}
