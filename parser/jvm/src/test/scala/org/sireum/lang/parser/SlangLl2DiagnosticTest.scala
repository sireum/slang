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
package org.sireum.lang.parser

import org.sireum._
import org.sireum.test._


object SlangLl2DiagnosticTest {

  def parseMessages(content: Predef.String): scala.Vector[Predef.String] = {
    val reporter = message.Reporter.create
    SlangLl2.parse(Some(String("diagnostic.slang")), String(content), reporter)
    return reporter.issues.elements.map(_.text.value).toVector
  }

  def hasIssue(content: Predef.String, expected: Predef.String): scala.Boolean = {
    val messages = parseMessages(content)
    val r = messages.exists(_.contains(expected))
    if (!r) {
      Console.err.println(content)
      for (m <- messages) {
        Console.err.println(m)
      }
    }
    return r
  }

  def hasIssueWithout(content: Predef.String, expected: Predef.String, unexpected: Predef.String): scala.Boolean = {
    val messages = parseMessages(content)
    val r = messages.exists(_.contains(expected)) && !messages.exists(_.contains(unexpected))
    if (!r) {
      Console.err.println(content)
      for (m <- messages) {
        Console.err.println(m)
      }
    }
    return r
  }

  def passes(content: Predef.String): scala.Boolean = {
    val messages = parseMessages(content)
    val r = messages.isEmpty
    if (!r) {
      Console.err.println(content)
      for (m <- messages) {
        Console.err.println(m)
      }
    }
    return r
  }
}

import SlangLl2DiagnosticTest._

class SlangLl2DiagnosticTest extends TestSuite {

  val tests = Tests {

    "Return value-form diagnostics" - {

      * - hasIssue(
        """def f(x: Z): Z = {
          |  return match x {
          |    case 0 => \ 1
          |    case _ => \ 2
          |  }
          |}""".stripMargin,
        "value-form 'match' syntax")

      * - hasIssue(
        """def f(x: Z): Z = {
          |  return if x == 0 {
          |    \ 1
          |  } else {
          |    \ 2
          |  }
          |}""".stripMargin,
        "value-form 'if' syntax")

      * - hasIssue(
        """def f(x: Z): Z = {
          |  return match x {
          |    case 0 => \ 1
          |    case _ => \ 2
          |  }
          |}""".stripMargin,
        "case p1 => return v1")
    }

    "Postfix match diagnostics" - {

      * - hasIssueWithout(
        """def f(x: Z): Z = {
          |  val r: Z = x match {
          |    case 0 => \ 1
          |    case _ => \ 2
          |  }
          |  return r
          |}""".stripMargin,
        "Postfix 'exp match { ... }' is not LL(2) value-producing syntax",
        "Expecting RBRACE")

      * - hasIssue(
        """def f(x: Z): Z = {
          |  val r: Z = (x) match {
          |    case 0 => \ 1
          |    case _ => \ 2
          |  }
          |  return r
          |}""".stripMargin,
        "val r: T = match exp")

      * - passes(
        """def f(x: Z): Z = {
          |  val r: Z = match x {
          |    case 0 => \ 1
          |    case _ => \ 2
          |  }
          |  return r
          |}""".stripMargin)
    }
  }
}
