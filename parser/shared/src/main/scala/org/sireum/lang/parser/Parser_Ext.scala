/*
 Copyright (c) 2017-2025, Robby, Kansas State University
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
import org.sireum.message._

import scala.meta._
import scala.meta.internal.parsers.ScalametaParser
import scala.util._

object Parser_Ext {

  def parseStmt[T](text: String): T = {
    val reporter = Reporter.create
    val (dialect, input) = SlangParser.scalaDialect(isWorksheet = false)(text.value)
    val metap = new ScalametaParser(input)(dialect)
    val stat = Try(metap.parseStat()) match {
      case Success(s) => s
      case Failure(e) => err(e.getMessage)
    }
    val stmt = new SlangParser(
      text.value,
      input,
      dialect,
      hashSireum = true,
      isWorksheet = false,
      isDiet = false,
      isTruthTable = false,
      None(),
      reporter
    ).translateStat(Enclosing.Method)(stat)
    reporter.printMessages()
    if (reporter.hasError) err()
    stmt.asInstanceOf[T]
  }

  def parseExp[T](text: String): T = {
    val reporter = Reporter.create
    val (dialect, input) = SlangParser.scalaDialect(isWorksheet = false)(text.value)
    val metap = new ScalametaParser(input)(dialect)
    val term = Try(metap.parseTerm()) match {
      case Success(t) => t
      case Failure(e) => err(e.getMessage)
    }
    val exp = new SlangParser(
      text.value,
      input,
      dialect,
      hashSireum = true,
      isWorksheet = false,
      isDiet = false,
      isTruthTable = false,
      None(),
      reporter
    ).translateExp(term)
    reporter.printMessages()
    if (reporter.hasError) err()
    exp.asInstanceOf[T]
  }

  def parseExpOpt(fileUriOpt: Option[String], text: String, isTruthTable: B, reporter: Reporter): Option[lang.ast.Exp] = {
    val (dialect, input) = SlangParser.scalaDialect(isWorksheet = false)(text.value)
    val metap = new ScalametaParser(input)(dialect)
    val term = Try(metap.parseTerm()) match {
      case Success(t) => t
      case Failure(e) => err(e.getMessage)
    }
    val exp = new SlangParser(
      text.value,
      input,
      dialect,
      hashSireum = true,
      isWorksheet = false,
      isDiet = false,
      isTruthTable = isTruthTable,
      fileUriOpt,
      reporter
    ).translateExp(term)
    if (reporter.hasError) None() else Some(exp)
  }

  def parseSequentOpt(fileUriOpt: Option[String], text: String, isTruthTable: B, reporter: Reporter): Option[lang.ast.Sequent] = {
    val (dialect, input) = SlangParser.scalaDialect(isWorksheet = false)(text.value)
    val metap = new ScalametaParser(input)(dialect)
    val term = Try(metap.parseTerm()) match {
      case Success(t) => t
      case Failure(e) => err(e.getMessage)
    }
    val sequent = new SlangParser(
      text.value,
      input,
      dialect,
      hashSireum = true,
      isWorksheet = false,
      isDiet = false,
      isTruthTable = isTruthTable,
      fileUriOpt,
      reporter
    ).translateSequent(term)
    if (reporter.hasError) None() else Some(sequent)
  }

  def parseTopUnit[T](
    text: String,
    isWorksheet: B,
    isDiet: B,
    fileUriOpt: Option[String],
    reporter: Reporter
  ): Option[T] =
    SlangParser(isWorksheet, isDiet, fileUriOpt, text.value, reporter).unitOpt.map(_.asInstanceOf[T])

  @pure def detectSlang(fileUriOpt: Option[String], txt: String): (B, String, String) = {
    val t = SlangParser.detectSlang(fileUriOpt, txt)
    return (t._1, t._2, t._3)
  }

  def err(s: Predef.String = "Can only be used for non-erroneous Slang statement."): Nothing = halt(s)
}
