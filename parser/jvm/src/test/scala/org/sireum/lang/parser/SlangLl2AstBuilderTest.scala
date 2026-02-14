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


class SlangLl2AstBuilderTest extends SireumRcSpec {

  def shouldIgnore(name: Predef.String, isSimplified: Boolean): Boolean = false

  def textResources: scala.collection.SortedMap[scala.Vector[Predef.String], Predef.String] = {
    val m = $internal.RC.text(Vector(
      "../../../../../../../../../../logika-examples/src/programming/manual/",
      "../../../../../../../../../logika-examples/src")
    ) { (p, _) => T }
    implicit val ordering: Ordering[Vector[Predef.String]] = m.ordering
    for ((k, v) <- m; pair <- {
      var r = Vector[(Vector[Predef.String], Predef.String)]()
      if (!shouldIgnore(k.last, F)) {
        r = r :+ (k, v)
      }
      r
    }) yield pair
  }

  def check(path: scala.Vector[Predef.String], content: Predef.String): scala.Boolean = {
    val uriOpt = Some(st"${(path, "/")}".render)
    val reporter = message.Reporter.create
    // Step 1: Parse Slang source -> AST
    lang.parser.Parser(content).parseTopUnit[lang.ast.TopUnit.Program](isWorksheet = T, isDiet = F, uriOpt, reporter) match {
      case Some(program) if !reporter.hasIssue =>
        try {
          // Step 2: Pretty print AST -> LL(2) text
          val ll2 = lang.ast.SlangLl2PrettyPrinter.prettyPrint(program).render

          // Step 3: Parse LL(2) -> ParseTree
          val parseTreeOpt = SlangLl2Parser.parse(uriOpt, ll2, reporter)
          if (reporter.hasError || parseTreeOpt.isEmpty) {
            reporter.printMessages()
            return false
          }

          // Step 4: Build AST from ParseTree
          val builtProgramOpt = lang.ast.SlangLl2AstBuilder.build(uriOpt, parseTreeOpt.get, reporter)
          if (reporter.hasError || builtProgramOpt.isEmpty) {
            reporter.printMessages()
            return false
          }

          // Step 5: Pretty print built AST -> LL(2) text
          val ll2Built = lang.ast.SlangLl2PrettyPrinter.prettyPrint(builtProgramOpt.get).render

          // Step 6: Assert both LL(2) texts are equal
          if (ll2 != ll2Built) {
            println("=== Expected ===")
            println(ll2)
            println("=== Got ===")
            println(ll2Built)
            println("=== Diff ===")
            val nl = conversions.String.toCis("\n")(0)
            val ll2Lines = ops.StringOps(ll2).split((c: C) => c == nl)
            val builtLines = ops.StringOps(ll2Built).split((c: C) => c == nl)
            val maxLines: Z = if (ll2Lines.size > builtLines.size) ll2Lines.size else builtLines.size
            for (i <- 0 until maxLines) {
              val l1: String = if (i < ll2Lines.size) ll2Lines(i) else String("<missing>")
              val l2: String = if (i < builtLines.size) builtLines(i) else String("<missing>")
              if (l1 != l2) {
                println(st"Line $i differs:".render)
                println(st"  expected: $l1".render)
                println(st"  got:      $l2".render)
              }
            }
            return false
          }
        } catch {
          case e: Throwable =>
            println(s"Exception: ${e.getMessage}")
            e.printStackTrace()
            return false
        }
      case _ =>
    }
    if (reporter.hasIssue) {
      reporter.printMessages()
    }
    !reporter.hasError
  }

}
