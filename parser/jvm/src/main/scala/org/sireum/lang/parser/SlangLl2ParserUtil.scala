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
import org.sireum.U64._
import org.antlr.v4.runtime.{BaseErrorListener, CharStreams, CommonTokenStream, RecognitionException, Recognizer}

import java.io.StringReader

object SlangLl2ParserUtil {
  def parse(uriOpt: Option[String],
            input: String,
            reporter: message.Reporter): B = {
    val docInfo = message.DocInfo.create(uriOpt, input)
    val cs = CharStreams.fromReader(new StringReader(input.value))
    val lexer = new antlrv4.SlangLl2Lexer(cs)
    val errorListener = new BaseErrorListener {
      override def syntaxError(recognizer: Recognizer[_, _], offendingSymbol: Object, line: Int,
                               charPositionInLine: Int, msg: Predef.String, e: RecognitionException): Unit = {
        if (reporter.hasIssue) return
        val offset = conversions.Z.toU64(docInfo.lineOffsets(line - 1).toZ + charPositionInLine) << u64"32"
        val length = conversions.Z.toU64(offendingSymbol.toString.length)
        val pos = message.PosInfo(docInfo, offset | length)
        reporter.error(Some(pos), "Slang LL2 Parser", msg)
      }
    }
    lexer.removeErrorListeners()
    lexer.addErrorListener(errorListener)
    val tokens = new CommonTokenStream(lexer)
    val parser = new antlrv4.SlangLl2Parser(tokens)
    parser.removeErrorListeners()
    parser.addErrorListener(errorListener)
    parser.file()
    reporter.hasIssue
  }
}