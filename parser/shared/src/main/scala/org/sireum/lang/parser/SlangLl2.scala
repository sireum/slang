// #Sireum
/*
 Copyright (c) 2017-2026, Robby, Kansas State University
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
import org.sireum.lang.{ast => AST}

object SlangLl2 {
  @strictpure def is(fileUriOpt: Option[String]): B = fileUriOpt match {
    case Some(uri) =>
      val cis = conversions.String.toCis(uri)
      ops.StringOps.endsWith(cis, conversions.String.toCis(".slang")) || ops.StringOps.endsWith(cis, conversions.String.toCis(".sl"))
    case _ => F
  }
  def parse(fileUriOpt: Option[String], content: String, reporter: message.Reporter): Option[AST.TopUnit.Program] = {
    SlangLl2Parser.parse(fileUriOpt, content, reporter) match {
      case Some(t) => return AST.SlangLl2AstBuilder.build(fileUriOpt, t, reporter)
      case _ => return None()
    }
  }
  def parseExp(fileUriOpt: Option[String], content: String, reporter: message.Reporter): Option[AST.Exp] = {
    SlangLl2Parser.parseRule(fileUriOpt, content, "expFile", reporter) match {
      case Some(t: parser.ParseTree.Node) => return Some(AST.SlangLl2AstBuilder.buildExp(t, reporter))
      case _ => return None()
    }
  }
  def parseStmt(fileUriOpt: Option[String], content: String, reporter: message.Reporter): Option[AST.Stmt] = {
    SlangLl2Parser.parseRule(fileUriOpt, content, "stmtFile", reporter) match {
      case Some(t: parser.ParseTree.Node) => return Some(AST.SlangLl2AstBuilder.buildStmt(t, reporter, F))
      case _ => return None()
    }
  }
}
