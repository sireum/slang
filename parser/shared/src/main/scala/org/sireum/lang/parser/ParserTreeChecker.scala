// #Sireum
/*
 Copyright (c) 2021, Robby, Kansas State University
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
import org.sireum.message.Reporter
import org.sireum.lang.{ast => AST}

object ParserTreeChecker {

  val messageKind: String = "Slang Parser"

  @record class StrictPureChecker(reporter: Reporter) extends AST.MTransformer {
    override def postStmtMethod(o: AST.Stmt.Method): MOption[AST.Stmt] = {
      reporter.error(o.posOpt, messageKind, "@strictpure methods cannot define nested methods")
      return super.postStmtMethod(o)
    }

    override def postStmtVar(o: AST.Stmt.Var): MOption[AST.Stmt] = {
      if (!o.isVal) {
        reporter.error(o.posOpt, messageKind, "@strictpure methods cannot define vars")
      }
      return super.postStmtVar(o)
    }

    override def postStmtWhile(o: AST.Stmt.While): MOption[AST.Stmt] = {
      reporter.error(o.posOpt, messageKind, "@strictpure methods cannot use while-loops")
      return super.postStmtWhile(o)
    }

    override def postStmtFor(o: AST.Stmt.For): MOption[AST.Stmt] = {
      reporter.error(o.posOpt, messageKind, "@strictpure methods cannot use for-loops")
      return super.postStmtFor(o)
    }

    override def postStmtVarPattern(o: AST.Stmt.VarPattern): MOption[AST.Stmt] = {
      if (!o.isVal) {
        reporter.error(o.posOpt, messageKind, "@strictpure methods cannot define vars")
      }
      return super.postStmtVarPattern(o)
    }

    override def postStmtSpecVar(o: AST.Stmt.SpecVar): MOption[AST.Stmt] = {
      reporter.error(o.posOpt, messageKind, "@strictpure methods cannot define @spec val/var")
      return super.postStmtSpecVar(o)
    }

    override def postStmtSpecBlock(o: AST.Stmt.SpecBlock): MOption[AST.Stmt.Spec] = {
      reporter.error(o.posOpt, messageKind, "@strictpure methods cannot use Spec { ... } blocks")
      return super.postStmtSpecBlock(o)
    }

    override def postStmtSpecLabel(o: AST.Stmt.SpecLabel): MOption[AST.Stmt.Spec] = {
      reporter.error(o.posOpt, messageKind, "@strictpure methods cannot use spec labels")
      return super.postStmtSpecLabel(o)
    }

    override def postStmtAssign(o: AST.Stmt.Assign): MOption[AST.Stmt] = {
      reporter.error(o.posOpt, messageKind, "@strictpure methods cannot use assignments")
      return super.postStmtAssign(o)
    }
  }
}
