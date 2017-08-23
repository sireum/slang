/*
 Copyright (c) 2017, Robby, Kansas State University
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

import org.sireum.$internal.RC
import org.sireum.lang.ast.TopUnit
import org.sireum.lang.logika.TruthTableVerifier
import org.sireum.lang.parser.LParser
import org.sireum.lang.test.TestUtil
import org.sireum.lang.util.AccumulatingReporter
import org.sireum.{ISZ, None => SNone, Some => SSome}
import org.sireum.test.SireumRcSpec

class TruthTableVerifierTest extends SireumRcSpec {
  lazy val textResources: scala.collection.Map[scala.Seq[Predef.String], Predef.String] = RC.text {
    (p, _) => p.head == "truthtable" && p.last.endsWith(".slang")
  }

  def check(path: scala.Seq[Predef.String], content: Predef.String): Boolean = {
    LParser[Boolean](content, AccumulatingReporter(ISZ())) { (p, reporter) =>
      val r = p.truthTable(SNone())
      val status = TestUtil.check(reporter) && r.unitOpt.exists(_.isInstanceOf[TopUnit.TruthTableUnit])
      if (!status) {
        return false
      }
      r.unitOpt match {
        case SSome(tt: TopUnit.TruthTableUnit) => TruthTableVerifier.verify(tt, reporter)
        case _ =>
      }
      TestUtil.check(reporter)
    }
  }
}
