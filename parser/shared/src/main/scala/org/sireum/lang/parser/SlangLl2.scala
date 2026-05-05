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
import org.sireum.S32._
import org.sireum.lang.{ast => AST}

object SlangLl2 {
  @strictpure def is(fileUriOpt: Option[String]): B = fileUriOpt match {
    case Some(uri) =>
      val cis = conversions.String.toCis(uri)
      ops.StringOps.endsWith(cis, conversions.String.toCis(".slang")) || ops.StringOps.endsWith(cis, conversions.String.toCis(".sl"))
    case _ => F
  }

  def isPostfixMatchReceiverEnd(token: parser.Token): B = {
    token.ruleName.native match {
      case "ID" => return T
      case "TRUE" => return T
      case "FALSE" => return T
      case "NULL" => return T
      case "THIS" => return T
      case "SUPER" => return T
      case "INT" => return T
      case "HEX" => return T
      case "BIN" => return T
      case "REAL" => return T
      case "STRING" => return T
      case "CHAR" => return T
      case "MSTR" => return T
      case "MSTRP" => return T
      case "MSTRPE" => return T
      case "RPAREN" => return T
      case "RSQUARE" => return T
      case _ => return F
    }
  }

  def reportPostfixMatchValueForm(fileUriOpt: Option[String], content: String, reporter: message.Reporter): B = {
    val chars = Indexable.Ext.fromString(fileUriOpt, content)
    val (errorIndex, tokens) = SlangLl2Parser.lexerDfas.tokens(chars, T)
    if (errorIndex >= s32"0") {
      return F
    }
    var i = s32"1"
    while (i < tokens.sizeS32) {
      val token = tokens.atS32(i)
      if (token.ruleName == "MATCH" && isPostfixMatchReceiverEnd(tokens.atS32(i - s32"1"))) {
        reporter.error(token.posOpt, "SlangLl2",
          st"""Postfix 'exp match { ... }' is not LL(2) value-producing syntax.
              |Use prefix match with '\' on each value-producing case:
              |val r: T = match exp {
              |  case p1 => \ v1
              |  case p2 => \ v2
              |}""".render)
        return T
      }
      i = i + s32"1"
    }
    return F
  }

  def reportCommonGotcha(fileUriOpt: Option[String], content: String, reporter: message.Reporter): B = {
    val chars = Indexable.Ext.fromString(fileUriOpt, content)
    val (errorIndex, tokens) = SlangLl2Parser.lexerDfas.tokens(chars, T)
    if (errorIndex >= s32"0") {
      val sops = ops.StringOps(content)
      if (sops.contains("@ext") && sops.contains("def") && sops.contains("= $")) {
        reporter.error(chars.posOptS32(errorIndex, s32"1"), "SlangLl2",
          "An `@ext` def must be a bare signature: `def foo(): T` (no `=` and no body); the trailing `$` was mis-tokenized.")
        return T
      }
      return F
    }

    var i = s32"0"
    while (i < tokens.sizeS32) {
      val token = tokens.atS32(i)
      if ((token.ruleName == "IF" || token.ruleName == "WHILE") && i + s32"1" < tokens.sizeS32 &&
          tokens.atS32(i + s32"1").ruleName == "LPAREN") {
        reporter.error(tokens.atS32(i + s32"1").posOpt, "SlangLl2",
          "LL(2) `if` / `while` do not take parens around the condition. Rewrite as: `if c { }` / `while c { }`.")
        return T
      }
      if (token.ruleName == "FOR" && i + s32"1" < tokens.sizeS32 && tokens.atS32(i + s32"1").ruleName == "LPAREN") {
        reporter.error(tokens.atS32(i + s32"1").posOpt, "SlangLl2",
          "LL(2) for-loop is `for x: xs { ... }`. Rewrite as: `for x: xs { ... }`.")
        return T
      }
      if (token.ruleName == "AT" && i + s32"2" < tokens.sizeS32 &&
          tokens.atS32(i + s32"1").ruleName == "ID" && tokens.atS32(i + s32"2").ruleName == "DEF") {
        reporter.error(token.posOpt, "SlangLl2",
          "LL(2) annotation goes after `def`: `def @pure foo(...)`.")
        return T
      }
      if (token.ruleName == "SPB" && i + s32"1" < tokens.sizeS32 && tokens.atS32(i + s32"1").ruleName == "LBRACE") {
        reporter.error(tokens.atS32(i + s32"1").posOpt, "SlangLl2",
          "Single-line `s\"...\"` interpolation uses `$expr$` (no braces). Use `${expr}$` only inside multi-line `s#...` / `st#...` templates.")
        return T
      }
      i = i + s32"1"
    }
    return F
  }

  def parseRule(fileUriOpt: Option[String], content: String, ruleName: String, reporter: message.Reporter): Option[parser.ParseTree] = {
    val parseReporter = message.Reporter.create
    SlangLl2Parser.parseRule(fileUriOpt, content, ruleName, parseReporter) match {
      case Some(t) =>
        reporter.reports(parseReporter.messages)
        return Some(t)
      case _ =>
        if (reportCommonGotcha(fileUriOpt, content, reporter)) {
          return None()
        }
        if (reportPostfixMatchValueForm(fileUriOpt, content, reporter)) {
          return None()
        }
        reporter.reports(parseReporter.messages)
        return None()
    }
  }

  def parse(fileUriOpt: Option[String], content: String, reporter: message.Reporter): Option[AST.TopUnit.Program] = {
    parseRule(fileUriOpt, content, "file", reporter) match {
      case Some(t) => return AST.SlangLl2AstBuilder.build(fileUriOpt, t, reporter)
      case _ => return None()
    }
  }
  def parseExp(fileUriOpt: Option[String], content: String, reporter: message.Reporter): Option[AST.Exp] = {
    parseRule(fileUriOpt, content, "expFile", reporter) match {
      case Some(t: parser.ParseTree.Node) => return Some(AST.SlangLl2AstBuilder.buildExp(t, reporter))
      case _ => return None()
    }
  }
  def parseStmt(fileUriOpt: Option[String], content: String, reporter: message.Reporter): Option[AST.Stmt] = {
    parseRule(fileUriOpt, content, "stmtFile", reporter) match {
      case Some(t: parser.ParseTree.Node) => return Some(AST.SlangLl2AstBuilder.buildStmt(t, reporter, F))
      case _ => return None()
    }
  }
}
