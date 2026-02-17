// #Sireum
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

// This file is auto-generated from SlangTruthTable.g

package org.sireum.lang.parser


import org.sireum._
import org.sireum.S32._
import org.sireum.U32._
import org.sireum.U64._
import org.sireum.conversions.U32.toC
import org.sireum.parser.ParseTree

object SlangTruthTableLl1 {
  @range(min = 0, max = 10) class State
}

import SlangTruthTableLl1.State
import SlangTruthTableLl1.State._

object SlangTruthTableLl1Parser {

  @datatype class Result(val kind: Result.Kind.Type, val tree: ParseTree, val newIndex: Z) {
    def leaf: ParseTree.Leaf = {
      return tree.asInstanceOf[ParseTree.Leaf]
    }
  }

  object Result {

    @enum object Kind {
      'Normal
      'LexicalError
      'GrammaticalError
    }

    @strictpure def create(tree: ParseTree, newIndex: Z): Result =
      Result(Result.Kind.Normal, tree, newIndex)

    @strictpure def error(isLexical: B, index: Z): Result =
      Result(if (isLexical) Result.Kind.LexicalError else Result.Kind.GrammaticalError, errorLeaf, index)

  }

  @record class Context(val ruleName: String,
                        val ruleType: S32,
                        val accepting: IS[State, B],
                        var state: State,
                        var resOpt: Option[Result],
                        var j: Z,
                        var max: Z,
                        var initial: B,
                        var trees: IS[S32, ParseTree],
                        var found: B,
                        var failIndex: Z,
                        var isLexical: B) {
    def updateAcceptingEpsilon(newState: State): Unit = {
      found = T
      initial = F
      state = newState
      resOpt = Some(Result.create(ParseTree.Node(trees, ruleName, ruleType), j))
    }

    def updateTerminal(token: ParseTree.Leaf, newState: State): Unit = {
      found = T
      j = j + 1
      initial = F
      state = newState
      trees = trees :+ token
      if (accepting(state)) {
        resOpt = Some(Result.create(ParseTree.Node(trees, ruleName, ruleType), j))
      }
    }

    def updateNonTerminal(r: Result, newState: State): Unit = {
      found = T
      initial = F
      j = r.newIndex
      state = newState
      trees = trees :+ r.tree
      if (accepting(state)) {
        resOpt = Some(Result.create(ParseTree.Node(trees, ruleName, ruleType), j))
      }
    }
  }

  object Context {
    @pure def create(ruleName: String, ruleType: S32, accepts: ISZ[State], i: Z): Context = {
      val accepting = MS.create[State, B](11, F)
      for (accept <- accepts) {
        accepting(accept) = T
      }
      return Context(
        ruleName = ruleName,
        ruleType = ruleType,
        accepting = accepting.toIS,
        state = state"0",
        resOpt = None(),
        trees = IS[S32, ParseTree](),
        j = i,
        max = i,
        initial = T,
        found = F,
        failIndex = 0,
        isLexical = F
      )
    }
  }

  @record class LContext(val accepting: IS[State, B], var state: State, var j: Z, var afterAcceptIndex: Z, var found: B) {
    def update(newState: State): Unit = {
      state = newState
      found = T
      if (accepting(state)) {
        afterAcceptIndex = j + 1
      }
    }
  }

  object LContext {
    @pure def create(accepts: ISZ[State], i: Z): LContext = {
      val accepting = MS.create[State, B](11, F)
      for (accept <- accepts) {
        accepting(accept) = T
      }
      return LContext(accepting = accepting.toIS, state = state"0", j = i, afterAcceptIndex = -1, found = F)
    }
  }

  @datatype class IndexableToken(val input: Indexable.Pos[C], val skipHidden: B) extends Indexable[Result] {
    val lexer: SlangTruthTableLl1Lexer = SlangTruthTableLl1Lexer(input)

    override def at(i: Z): Result = {
      return _at(i)
    }

    override def has(i: Z): B = {
      return _has(i)
    }

    @memoize def _has(i: Z): B = {
      assert(i >= 0)
      if (i == 0) {
        return T
      }
      if (!_has(i - 1)) {
        return F
      }
      val prev = _at(i - 1)
      return prev.kind == Result.Kind.Normal && prev.newIndex != -1
    }

    @memoize def _at(i: Z): Result = {
      if (i == 0) {
        if (input.has(0)) {
          lexer.tokenize(0, skipHidden) match {
            case Some(result) => return result
            case _ =>
          }
        }
      } else {
        val prev = _at(i - 1)
        if (input.has(prev.newIndex)) {
          lexer.tokenize(prev.newIndex, skipHidden) match {
            case Some(result) => return result
            case _ =>
          }
        }
      }
      return Result(Result.Kind.Normal, eofLeaf, -1)
    }

  }

  val kind: String = "SlangTruthTableLl1Parser"

  val minChar: C = '\u0000'
  val maxChar: C = toC(u32"0x0010FFFF")

  val T_E8F861E8: S32 = s32"0xE8F861E8" /* "Tautology" */
  val T_2E50643A: S32 = s32"0x2E50643A" /* "Contradictory" */
  val T_584EF8BA: S32 = s32"0x584EF8BA" /* "Contingent" */
  val T_56000B93: S32 = s32"0x56000B93" /* "Valid" */
  val T_1E4BCCF1: S32 = s32"0x1E4BCCF1" /* "Invalid" */
  val T_HLINE: S32 = s32"0x7566CFCC"
  val T_HASH: S32 = s32"0x63CEDE8B"
  val T_NL: S32 = s32"0x6CB684C0"
  val T_LSQUARE: S32 = s32"0x951E9CFB"
  val T_RSQUARE: S32 = s32"0xA97171F1"
  val T_COMMENT: S32 = s32"0x486B464F"
  val T_WS: S32 = s32"0x0E3F5D1E"
  val T_OTHER: S32 = s32"0xFE2BCE26"
  val T_file: S32 = s32"0x31351F54"
  val T_table: S32 = s32"0x923B3672"
  val T_stars: S32 = s32"0x5886A04C"
  val T_header: S32 = s32"0xE58011D2"
  val T_hlinep: S32 = s32"0xC530B4FF"
  val T_rows: S32 = s32"0x902B3F8C"
  val T_row: S32 = s32"0x7CEE554F"
  val T_hlines: S32 = s32"0x5F12D13F"
  val T_conclusion: S32 = s32"0x67601A2E"
  val T_cas: S32 = s32"0xCD3E833F"
  val T_assign: S32 = s32"0xE5F71CFA"
  val T_others: S32 = s32"0xB759F101"
  val T_nls: S32 = s32"0x81F64CF7"

  val errorLeaf: ParseTree.Leaf = ParseTree.Leaf("<ERROR>", "<ERROR>", s32"0xE3CDEDDA", F, None())
  val eofLeaf: ParseTree.Leaf = ParseTree.Leaf("<EOF>", "EOF", s32"0xFC5CB374", F, None())

  def parse(uriOpt: Option[String], input: String, reporter: message.Reporter): Option[ParseTree] = {
    val chars = Indexable.Ext.fromString(uriOpt, input)
    val tokens = lex(chars, T, T, reporter)
    if (reporter.hasError) {
      return None()
    }
    val r = SlangTruthTableLl1Parser(Indexable.fromIsz(tokens)).parseFile(0)
    r.kind match {
      case Result.Kind.Normal => return Some(r.tree)
      case Result.Kind.LexicalError =>
        reporter.error(chars.posOpt(r.newIndex, 1), kind, s"Could not recognize token")
        return None()
      case Result.Kind.GrammaticalError =>
        val idx: Z = if (r.newIndex < 0) -r.newIndex else r.newIndex
        if (idx < tokens.size) {
          val token = tokens(idx).leaf
          reporter.error(token.posOpt, kind, s"Could not parse token: \"${ops.StringOps(token.text).escapeST.render}\"")
        } else {
          val token = tokens(idx - 1).leaf
          reporter.error(token.posOpt, kind, "Expecting more input but reached the end")
        }
        return None()
    }
  }

  def parseIndexable(input: Indexable.Pos[C], reporter: message.Reporter): Option[ParseTree] = {
    val it = IndexableToken(input, T)
    val r = SlangTruthTableLl1Parser(it).parseFile(0)
    r.kind match {
      case Result.Kind.Normal => return Some(r.tree)
      case Result.Kind.LexicalError =>
        reporter.error(input.posOpt(r.newIndex, 1), kind, s"Could not recognize token")
        return None()
      case Result.Kind.GrammaticalError =>
        val idx: Z = if (r.newIndex < 0) -r.newIndex else r.newIndex
        if (it.has(idx)) {
          val token = it.at(idx).leaf
          reporter.error(token.posOpt, kind, s"Could not parse token: \"${ops.StringOps(token.text).escapeST.render}\"")
        } else {
          val token = it.at(idx - 1).leaf
          reporter.error(token.posOpt, kind, "Expecting more input but reached the end")
        }
        return None()
    }
  }

  def lex(chars: Indexable.PosC, skipHidden: B, stopAtError: B,
          reporter: message.Reporter): ISZ[Result] = {
    return SlangTruthTableLl1Lexer(chars).tokenizeAll(skipHidden, stopAtError, reporter)
  }

  @pure def offsetLength(offset: Z, length: Z): U64 = {
    return (conversions.Z.toU64(offset) << u64"32") | (conversions.Z.toU64(length) & u64"0xFFFFFFFF")
  }

}

import SlangTruthTableLl1Parser._

@datatype class SlangTruthTableLl1Parser(tokens: Indexable[Result]) {

  @pure def parseFile(i: Z): Result = {
    val ctx = Context.create("file", s32"0x31351F54", ISZ(state"3"), i)

    while (tokens.has(ctx.j)) {
      val token: ParseTree.Leaf = {
        val result = tokens.at(ctx.j)
        if (result.kind != Result.Kind.Normal) {
          return result
        }
        result.leaf
      }
      ctx.state match {
        case state"0" =>
          ctx.found = F
          val n_nls = predictNls(ctx.j)
          val n_table = predictTable(ctx.j)
          for (n <- 1 to 1 by -1 if !ctx.found) {
            if (n_nls == n && parseNlsH(ctx, state"1")) {
              return Result.error(ctx.isLexical, ctx.failIndex)
            } else if (n_table == n && parseTableH(ctx, state"2")) {
              return Result.error(ctx.isLexical, ctx.failIndex)
            }
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"1" =>
          ctx.found = F
          val n_table = predictTable(ctx.j)
          if (n_table >= 0 && parseTableH(ctx, state"2")) {
            return Result.error(ctx.isLexical, ctx.failIndex)
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"2" =>
          ctx.found = F
          token.tipe match {
            case s32"0xFC5CB374" /* EOF */ => ctx.updateTerminal(token, state"3")
            case _ =>
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"3" => return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
        case _ => halt("Infeasible")
      }
      if (ctx.max < ctx.j) {
        ctx.max = ctx.j
      }
    }

    return retVal(ctx.j, ctx.resOpt, ctx.initial, T)
  }

  @pure def parseTable(i: Z): Result = {
    val ctx = Context.create("table", s32"0x923B3672", ISZ(state"6", state"7"), i)

    while (tokens.has(ctx.j)) {
      val token: ParseTree.Leaf = {
        val result = tokens.at(ctx.j)
        if (result.kind != Result.Kind.Normal) {
          return result
        }
        result.leaf
      }
      ctx.state match {
        case state"0" =>
          ctx.found = F
          val n_stars = predictStars(ctx.j)
          if (n_stars >= 0 && parseStarsH(ctx, state"1")) {
            return Result.error(ctx.isLexical, ctx.failIndex)
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"1" =>
          ctx.found = F
          val n_hlinep = predictHlinep(ctx.j)
          if (n_hlinep >= 0 && parseHlinepH(ctx, state"2")) {
            return Result.error(ctx.isLexical, ctx.failIndex)
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"2" =>
          ctx.found = F
          val n_header = predictHeader(ctx.j)
          if (n_header >= 0 && parseHeaderH(ctx, state"3")) {
            return Result.error(ctx.isLexical, ctx.failIndex)
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"3" =>
          ctx.found = F
          val n_hlinep = predictHlinep(ctx.j)
          if (n_hlinep >= 0 && parseHlinepH(ctx, state"4")) {
            return Result.error(ctx.isLexical, ctx.failIndex)
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"4" =>
          ctx.found = F
          val n_rows = predictRows(ctx.j)
          if (n_rows >= 0 && parseRowsH(ctx, state"5")) {
            return Result.error(ctx.isLexical, ctx.failIndex)
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"5" =>
          ctx.found = F
          val n_hlines = predictHlines(ctx.j)
          if (n_hlines >= 0 && parseHlinesH(ctx, state"6")) {
            return Result.error(ctx.isLexical, ctx.failIndex)
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"6" =>
          ctx.found = F
          val n_conclusion = predictConclusion(ctx.j)
          if (n_conclusion >= 0 && parseConclusionH(ctx, state"7")) {
            return Result.error(ctx.isLexical, ctx.failIndex)
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"7" => return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
        case _ => halt("Infeasible")
      }
      if (ctx.max < ctx.j) {
        ctx.max = ctx.j
      }
    }

    return retVal(ctx.j, ctx.resOpt, ctx.initial, T)
  }

  @pure def parseStars(i: Z): Result = {
    val ctx = Context.create("stars", s32"0x5886A04C", ISZ(state"2"), i)

    while (tokens.has(ctx.j)) {
      val token: ParseTree.Leaf = {
        val result = tokens.at(ctx.j)
        if (result.kind != Result.Kind.Normal) {
          return result
        }
        result.leaf
      }
      ctx.state match {
        case state"0" =>
          ctx.found = F
          token.tipe match {
            case s32"0xFE2BCE26" /* OTHER */ => ctx.updateTerminal(token, state"1")
            case _ =>
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"1" =>
          ctx.found = F
          token.tipe match {
            case s32"0xFE2BCE26" /* OTHER */ => ctx.updateTerminal(token, state"1")
            case s32"0x6CB684C0" /* NL */ => ctx.updateTerminal(token, state"2")
            case _ =>
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"2" =>
          ctx.found = F
          token.tipe match {
            case s32"0x6CB684C0" /* NL */ => ctx.updateTerminal(token, state"2")
            case _ =>
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case _ => halt("Infeasible")
      }
      if (ctx.max < ctx.j) {
        ctx.max = ctx.j
      }
    }

    return retVal(ctx.j, ctx.resOpt, ctx.initial, T)
  }

  @pure def parseHeader(i: Z): Result = {
    val ctx = Context.create("header", s32"0xE58011D2", ISZ(state"4"), i)

    while (tokens.has(ctx.j)) {
      val token: ParseTree.Leaf = {
        val result = tokens.at(ctx.j)
        if (result.kind != Result.Kind.Normal) {
          return result
        }
        result.leaf
      }
      ctx.state match {
        case state"0" =>
          ctx.found = F
          val n_others = predictOthers(ctx.j)
          if (n_others >= 0 && parseOthersH(ctx, state"1")) {
            return Result.error(ctx.isLexical, ctx.failIndex)
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"1" =>
          ctx.found = F
          token.tipe match {
            case s32"0x63CEDE8B" /* HASH */ => ctx.updateTerminal(token, state"2")
            case _ =>
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"2" =>
          ctx.found = F
          val n_others = predictOthers(ctx.j)
          if (n_others >= 0 && parseOthersH(ctx, state"3")) {
            return Result.error(ctx.isLexical, ctx.failIndex)
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"3" =>
          ctx.found = F
          token.tipe match {
            case s32"0x6CB684C0" /* NL */ => ctx.updateTerminal(token, state"4")
            case _ =>
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"4" =>
          ctx.found = F
          token.tipe match {
            case s32"0x6CB684C0" /* NL */ => ctx.updateTerminal(token, state"4")
            case _ =>
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case _ => halt("Infeasible")
      }
      if (ctx.max < ctx.j) {
        ctx.max = ctx.j
      }
    }

    return retVal(ctx.j, ctx.resOpt, ctx.initial, T)
  }

  @pure def parseHlinep(i: Z): Result = {
    val ctx = Context.create("hlinep", s32"0xC530B4FF", ISZ(state"2"), i)

    while (tokens.has(ctx.j)) {
      val token: ParseTree.Leaf = {
        val result = tokens.at(ctx.j)
        if (result.kind != Result.Kind.Normal) {
          return result
        }
        result.leaf
      }
      ctx.state match {
        case state"0" =>
          ctx.found = F
          token.tipe match {
            case s32"0x7566CFCC" /* HLINE */ => ctx.updateTerminal(token, state"1")
            case _ =>
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"1" =>
          ctx.found = F
          token.tipe match {
            case s32"0x6CB684C0" /* NL */ => ctx.updateTerminal(token, state"2")
            case _ =>
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"2" =>
          ctx.found = F
          token.tipe match {
            case s32"0x6CB684C0" /* NL */ => ctx.updateTerminal(token, state"2")
            case _ =>
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case _ => halt("Infeasible")
      }
      if (ctx.max < ctx.j) {
        ctx.max = ctx.j
      }
    }

    return retVal(ctx.j, ctx.resOpt, ctx.initial, T)
  }

  @pure def parseRows(i: Z): Result = {
    val ctx = Context.create("rows", s32"0x902B3F8C", ISZ(state"0"), i)

    while (tokens.has(ctx.j)) {
      val token: ParseTree.Leaf = {
        val result = tokens.at(ctx.j)
        if (result.kind != Result.Kind.Normal) {
          return result
        }
        result.leaf
      }
      ctx.state match {
        case state"0" =>
          ctx.found = F
          val n_row = predictRow(ctx.j)
          if (n_row >= 0 && parseRowH(ctx, state"0")) {
            return Result.error(ctx.isLexical, ctx.failIndex)
          }
          if (!ctx.found) {
            ctx.updateAcceptingEpsilon(state"0")
            return retVal(ctx.j, ctx.resOpt, ctx.initial, T)
          }
        case _ => halt("Infeasible")
      }
      if (ctx.max < ctx.j) {
        ctx.max = ctx.j
      }
    }

    return retVal(ctx.j, ctx.resOpt, ctx.initial, T)
  }

  @pure def parseRow(i: Z): Result = {
    val ctx = Context.create("row", s32"0x7CEE554F", ISZ(state"4"), i)

    while (tokens.has(ctx.j)) {
      val token: ParseTree.Leaf = {
        val result = tokens.at(ctx.j)
        if (result.kind != Result.Kind.Normal) {
          return result
        }
        result.leaf
      }
      ctx.state match {
        case state"0" =>
          ctx.found = F
          val n_others = predictOthers(ctx.j)
          if (n_others >= 0 && parseOthersH(ctx, state"1")) {
            return Result.error(ctx.isLexical, ctx.failIndex)
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"1" =>
          ctx.found = F
          token.tipe match {
            case s32"0x63CEDE8B" /* HASH */ => ctx.updateTerminal(token, state"2")
            case _ =>
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"2" =>
          ctx.found = F
          val n_others = predictOthers(ctx.j)
          if (n_others >= 0 && parseOthersH(ctx, state"3")) {
            return Result.error(ctx.isLexical, ctx.failIndex)
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"3" =>
          ctx.found = F
          token.tipe match {
            case s32"0x6CB684C0" /* NL */ => ctx.updateTerminal(token, state"4")
            case _ =>
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"4" => return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
        case _ => halt("Infeasible")
      }
      if (ctx.max < ctx.j) {
        ctx.max = ctx.j
      }
    }

    return retVal(ctx.j, ctx.resOpt, ctx.initial, T)
  }

  @pure def parseHlines(i: Z): Result = {
    val ctx = Context.create("hlines", s32"0x5F12D13F", ISZ(state"1"), i)

    while (tokens.has(ctx.j)) {
      val token: ParseTree.Leaf = {
        val result = tokens.at(ctx.j)
        if (result.kind != Result.Kind.Normal) {
          return result
        }
        result.leaf
      }
      ctx.state match {
        case state"0" =>
          ctx.found = F
          token.tipe match {
            case s32"0x7566CFCC" /* HLINE */ => ctx.updateTerminal(token, state"1")
            case _ =>
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"1" =>
          ctx.found = F
          token.tipe match {
            case s32"0x6CB684C0" /* NL */ => ctx.updateTerminal(token, state"1")
            case _ =>
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case _ => halt("Infeasible")
      }
      if (ctx.max < ctx.j) {
        ctx.max = ctx.j
      }
    }

    return retVal(ctx.j, ctx.resOpt, ctx.initial, T)
  }

  @pure def parseConclusion(i: Z): Result = {
    val ctx = Context.create("conclusion", s32"0x67601A2E", ISZ(state"1", state"2", state"4", state"5", state"6", state"7", state"8", state"9", state"10"), i)

    while (tokens.has(ctx.j)) {
      val token: ParseTree.Leaf = {
        val result = tokens.at(ctx.j)
        if (result.kind != Result.Kind.Normal) {
          return result
        }
        result.leaf
      }
      ctx.state match {
        case state"0" =>
          ctx.found = F
          token.tipe match {
            case s32"0xE8F861E8" /* "Tautology" */ => ctx.updateTerminal(token, state"1")
            case s32"0x2E50643A" /* "Contradictory" */ => ctx.updateTerminal(token, state"2")
            case s32"0x584EF8BA" /* "Contingent" */ => ctx.updateTerminal(token, state"3")
            case s32"0x56000B93" /* "Valid" */ => ctx.updateTerminal(token, state"7")
            case s32"0x1E4BCCF1" /* "Invalid" */ => ctx.updateTerminal(token, state"9")
            case _ =>
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"1" =>
          ctx.found = F
          token.tipe match {
            case s32"0x6CB684C0" /* NL */ => ctx.updateTerminal(token, state"1")
            case _ =>
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"2" =>
          ctx.found = F
          token.tipe match {
            case s32"0x6CB684C0" /* NL */ => ctx.updateTerminal(token, state"2")
            case _ =>
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"3" =>
          ctx.found = F
          token.tipe match {
            case s32"0x6CB684C0" /* NL */ => ctx.updateTerminal(token, state"4")
            case _ =>
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"4" =>
          ctx.found = F
          val n_cas = predictCas(ctx.j)
          if (n_cas >= 0 && parseCasH(ctx, state"5")) {
            return Result.error(ctx.isLexical, ctx.failIndex)
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"5" =>
          ctx.found = F
          token.tipe match {
            case s32"0x6CB684C0" /* NL */ => ctx.updateTerminal(token, state"6")
            case _ =>
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"6" =>
          ctx.found = F
          val n_cas = predictCas(ctx.j)
          if (n_cas >= 0 && parseCasH(ctx, state"5")) {
            return Result.error(ctx.isLexical, ctx.failIndex)
          }
          if (!ctx.found) {
            token.tipe match {
              case s32"0x6CB684C0" /* NL */ => ctx.updateTerminal(token, state"6")
              case _ =>
            }
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"7" =>
          ctx.found = F
          val n_assign = predictAssign(ctx.j)
          if (n_assign >= 0 && parseAssignH(ctx, state"7")) {
            return Result.error(ctx.isLexical, ctx.failIndex)
          }
          if (!ctx.found) {
            token.tipe match {
              case s32"0x6CB684C0" /* NL */ => ctx.updateTerminal(token, state"8")
              case _ =>
            }
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"8" =>
          ctx.found = F
          token.tipe match {
            case s32"0x6CB684C0" /* NL */ => ctx.updateTerminal(token, state"8")
            case _ =>
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"9" =>
          ctx.found = F
          val n_assign = predictAssign(ctx.j)
          if (n_assign >= 0 && parseAssignH(ctx, state"9")) {
            return Result.error(ctx.isLexical, ctx.failIndex)
          }
          if (!ctx.found) {
            token.tipe match {
              case s32"0x6CB684C0" /* NL */ => ctx.updateTerminal(token, state"10")
              case _ =>
            }
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"10" =>
          ctx.found = F
          token.tipe match {
            case s32"0x6CB684C0" /* NL */ => ctx.updateTerminal(token, state"10")
            case _ =>
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case _ => halt("Infeasible")
      }
      if (ctx.max < ctx.j) {
        ctx.max = ctx.j
      }
    }

    return retVal(ctx.j, ctx.resOpt, ctx.initial, T)
  }

  @pure def parseCas(i: Z): Result = {
    val ctx = Context.create("cas", s32"0xCD3E833F", ISZ(state"3"), i)

    while (tokens.has(ctx.j)) {
      val token: ParseTree.Leaf = {
        val result = tokens.at(ctx.j)
        if (result.kind != Result.Kind.Normal) {
          return result
        }
        result.leaf
      }
      ctx.state match {
        case state"0" =>
          ctx.found = F
          token.tipe match {
            case s32"0xFE2BCE26" /* OTHER */ => ctx.updateTerminal(token, state"1")
            case _ =>
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"1" =>
          ctx.found = F
          token.tipe match {
            case s32"0xFE2BCE26" /* OTHER */ => ctx.updateTerminal(token, state"2")
            case _ =>
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"2" =>
          ctx.found = F
          val n_assign = predictAssign(ctx.j)
          if (n_assign >= 0 && parseAssignH(ctx, state"3")) {
            return Result.error(ctx.isLexical, ctx.failIndex)
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"3" =>
          ctx.found = F
          val n_assign = predictAssign(ctx.j)
          if (n_assign >= 0 && parseAssignH(ctx, state"3")) {
            return Result.error(ctx.isLexical, ctx.failIndex)
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case _ => halt("Infeasible")
      }
      if (ctx.max < ctx.j) {
        ctx.max = ctx.j
      }
    }

    return retVal(ctx.j, ctx.resOpt, ctx.initial, T)
  }

  @pure def parseAssign(i: Z): Result = {
    val ctx = Context.create("assign", s32"0xE5F71CFA", ISZ(state"3"), i)

    while (tokens.has(ctx.j)) {
      val token: ParseTree.Leaf = {
        val result = tokens.at(ctx.j)
        if (result.kind != Result.Kind.Normal) {
          return result
        }
        result.leaf
      }
      ctx.state match {
        case state"0" =>
          ctx.found = F
          token.tipe match {
            case s32"0x951E9CFB" /* LSQUARE */ => ctx.updateTerminal(token, state"1")
            case _ =>
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"1" =>
          ctx.found = F
          val n_others = predictOthers(ctx.j)
          if (n_others >= 0 && parseOthersH(ctx, state"2")) {
            return Result.error(ctx.isLexical, ctx.failIndex)
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"2" =>
          ctx.found = F
          token.tipe match {
            case s32"0xA97171F1" /* RSQUARE */ => ctx.updateTerminal(token, state"3")
            case _ =>
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"3" => return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
        case _ => halt("Infeasible")
      }
      if (ctx.max < ctx.j) {
        ctx.max = ctx.j
      }
    }

    return retVal(ctx.j, ctx.resOpt, ctx.initial, T)
  }

  @pure def parseOthers(i: Z): Result = {
    val ctx = Context.create("others", s32"0xB759F101", ISZ(state"0"), i)

    while (tokens.has(ctx.j)) {
      val token: ParseTree.Leaf = {
        val result = tokens.at(ctx.j)
        if (result.kind != Result.Kind.Normal) {
          return result
        }
        result.leaf
      }
      ctx.state match {
        case state"0" =>
          ctx.found = F
          token.tipe match {
            case s32"0xFE2BCE26" /* OTHER */ => ctx.updateTerminal(token, state"0")
            case _ =>
          }
          if (!ctx.found) {
            ctx.updateAcceptingEpsilon(state"0")
            return retVal(ctx.j, ctx.resOpt, ctx.initial, T)
          }
        case _ => halt("Infeasible")
      }
      if (ctx.max < ctx.j) {
        ctx.max = ctx.j
      }
    }

    return retVal(ctx.j, ctx.resOpt, ctx.initial, T)
  }

  @pure def parseNls(i: Z): Result = {
    val ctx = Context.create("nls", s32"0x81F64CF7", ISZ(state"1"), i)

    while (tokens.has(ctx.j)) {
      val token: ParseTree.Leaf = {
        val result = tokens.at(ctx.j)
        if (result.kind != Result.Kind.Normal) {
          return result
        }
        result.leaf
      }
      ctx.state match {
        case state"0" =>
          ctx.found = F
          token.tipe match {
            case s32"0x6CB684C0" /* NL */ => ctx.updateTerminal(token, state"1")
            case _ =>
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case state"1" =>
          ctx.found = F
          token.tipe match {
            case s32"0x6CB684C0" /* NL */ => ctx.updateTerminal(token, state"1")
            case _ =>
          }
          if (!ctx.found) {
            return retVal(ctx.max, ctx.resOpt, ctx.initial, T)
          }
        case _ => halt("Infeasible")
      }
      if (ctx.max < ctx.j) {
        ctx.max = ctx.j
      }
    }

    return retVal(ctx.j, ctx.resOpt, ctx.initial, T)
  }

  def parseNlsH(ctx: Context, nextState: State): B = {
    val r = parseNls(ctx.j)
    r.kind match {
      case Result.Kind.Normal => ctx.updateNonTerminal(r, nextState)
      case Result.Kind.LexicalError =>
        ctx.failIndex = r.newIndex
        ctx.isLexical = T
        return T
      case Result.Kind.GrammaticalError =>
        val index = r.newIndex
        if (index < 0) {
          ctx.failIndex = index
          return T
        } else if (ctx.max < index) {
          ctx.max = index
        }
    }
    return F
  }

  def parseTableH(ctx: Context, nextState: State): B = {
    val r = parseTable(ctx.j)
    r.kind match {
      case Result.Kind.Normal => ctx.updateNonTerminal(r, nextState)
      case Result.Kind.LexicalError =>
        ctx.failIndex = r.newIndex
        ctx.isLexical = T
        return T
      case Result.Kind.GrammaticalError =>
        val index = r.newIndex
        if (index < 0) {
          ctx.failIndex = index
          return T
        } else if (ctx.max < index) {
          ctx.max = index
        }
    }
    return F
  }

  def parseStarsH(ctx: Context, nextState: State): B = {
    val r = parseStars(ctx.j)
    r.kind match {
      case Result.Kind.Normal => ctx.updateNonTerminal(r, nextState)
      case Result.Kind.LexicalError =>
        ctx.failIndex = r.newIndex
        ctx.isLexical = T
        return T
      case Result.Kind.GrammaticalError =>
        val index = r.newIndex
        if (index < 0) {
          ctx.failIndex = index
          return T
        } else if (ctx.max < index) {
          ctx.max = index
        }
    }
    return F
  }

  def parseHlinepH(ctx: Context, nextState: State): B = {
    val r = parseHlinep(ctx.j)
    r.kind match {
      case Result.Kind.Normal => ctx.updateNonTerminal(r, nextState)
      case Result.Kind.LexicalError =>
        ctx.failIndex = r.newIndex
        ctx.isLexical = T
        return T
      case Result.Kind.GrammaticalError =>
        val index = r.newIndex
        if (index < 0) {
          ctx.failIndex = index
          return T
        } else if (ctx.max < index) {
          ctx.max = index
        }
    }
    return F
  }

  def parseHeaderH(ctx: Context, nextState: State): B = {
    val r = parseHeader(ctx.j)
    r.kind match {
      case Result.Kind.Normal => ctx.updateNonTerminal(r, nextState)
      case Result.Kind.LexicalError =>
        ctx.failIndex = r.newIndex
        ctx.isLexical = T
        return T
      case Result.Kind.GrammaticalError =>
        val index = r.newIndex
        if (index < 0) {
          ctx.failIndex = index
          return T
        } else if (ctx.max < index) {
          ctx.max = index
        }
    }
    return F
  }

  def parseRowsH(ctx: Context, nextState: State): B = {
    val r = parseRows(ctx.j)
    r.kind match {
      case Result.Kind.Normal => ctx.updateNonTerminal(r, nextState)
      case Result.Kind.LexicalError =>
        ctx.failIndex = r.newIndex
        ctx.isLexical = T
        return T
      case Result.Kind.GrammaticalError =>
        val index = r.newIndex
        if (index < 0) {
          ctx.failIndex = index
          return T
        } else if (ctx.max < index) {
          ctx.max = index
        }
    }
    return F
  }

  def parseHlinesH(ctx: Context, nextState: State): B = {
    val r = parseHlines(ctx.j)
    r.kind match {
      case Result.Kind.Normal => ctx.updateNonTerminal(r, nextState)
      case Result.Kind.LexicalError =>
        ctx.failIndex = r.newIndex
        ctx.isLexical = T
        return T
      case Result.Kind.GrammaticalError =>
        val index = r.newIndex
        if (index < 0) {
          ctx.failIndex = index
          return T
        } else if (ctx.max < index) {
          ctx.max = index
        }
    }
    return F
  }

  def parseConclusionH(ctx: Context, nextState: State): B = {
    val r = parseConclusion(ctx.j)
    r.kind match {
      case Result.Kind.Normal => ctx.updateNonTerminal(r, nextState)
      case Result.Kind.LexicalError =>
        ctx.failIndex = r.newIndex
        ctx.isLexical = T
        return T
      case Result.Kind.GrammaticalError =>
        val index = r.newIndex
        if (index < 0) {
          ctx.failIndex = index
          return T
        } else if (ctx.max < index) {
          ctx.max = index
        }
    }
    return F
  }

  def parseOthersH(ctx: Context, nextState: State): B = {
    val r = parseOthers(ctx.j)
    r.kind match {
      case Result.Kind.Normal => ctx.updateNonTerminal(r, nextState)
      case Result.Kind.LexicalError =>
        ctx.failIndex = r.newIndex
        ctx.isLexical = T
        return T
      case Result.Kind.GrammaticalError =>
        val index = r.newIndex
        if (index < 0) {
          ctx.failIndex = index
          return T
        } else if (ctx.max < index) {
          ctx.max = index
        }
    }
    return F
  }

  def parseRowH(ctx: Context, nextState: State): B = {
    val r = parseRow(ctx.j)
    r.kind match {
      case Result.Kind.Normal => ctx.updateNonTerminal(r, nextState)
      case Result.Kind.LexicalError =>
        ctx.failIndex = r.newIndex
        ctx.isLexical = T
        return T
      case Result.Kind.GrammaticalError =>
        val index = r.newIndex
        if (index < 0) {
          ctx.failIndex = index
          return T
        } else if (ctx.max < index) {
          ctx.max = index
        }
    }
    return F
  }

  def parseCasH(ctx: Context, nextState: State): B = {
    val r = parseCas(ctx.j)
    r.kind match {
      case Result.Kind.Normal => ctx.updateNonTerminal(r, nextState)
      case Result.Kind.LexicalError =>
        ctx.failIndex = r.newIndex
        ctx.isLexical = T
        return T
      case Result.Kind.GrammaticalError =>
        val index = r.newIndex
        if (index < 0) {
          ctx.failIndex = index
          return T
        } else if (ctx.max < index) {
          ctx.max = index
        }
    }
    return F
  }

  def parseAssignH(ctx: Context, nextState: State): B = {
    val r = parseAssign(ctx.j)
    r.kind match {
      case Result.Kind.Normal => ctx.updateNonTerminal(r, nextState)
      case Result.Kind.LexicalError =>
        ctx.failIndex = r.newIndex
        ctx.isLexical = T
        return T
      case Result.Kind.GrammaticalError =>
        val index = r.newIndex
        if (index < 0) {
          ctx.failIndex = index
          return T
        } else if (ctx.max < index) {
          ctx.max = index
        }
    }
    return F
  }

  @pure def predictFile(j: Z): Z = {
    val tokenJ = tokens.at(j)
    if (tokenJ.kind == Result.Kind.Normal) {
      tokenJ.leaf.tipe match {
        case s32"0x6CB684C0" /* NL */ => return 1
        case s32"0xFE2BCE26" /* OTHER */ => return 1
        case _ =>
      }
    }
    return -1
  }

  @pure def predictConclusion(j: Z): Z = {
    val tokenJ = tokens.at(j)
    if (tokenJ.kind == Result.Kind.Normal) {
      tokenJ.leaf.tipe match {
        case s32"0xE8F861E8" /* "Tautology" */ => return 1
        case s32"0x2E50643A" /* "Contradictory" */ => return 1
        case s32"0x584EF8BA" /* "Contingent" */ => return 1
        case s32"0x56000B93" /* "Valid" */ => return 1
        case s32"0x1E4BCCF1" /* "Invalid" */ => return 1
        case _ =>
      }
    }
    return -1
  }

  @pure def predictNls(j: Z): Z = {
    val tokenJ = tokens.at(j)
    if (tokenJ.kind == Result.Kind.Normal) {
      tokenJ.leaf.tipe match {
        case s32"0x6CB684C0" /* NL */ => return 1
        case _ =>
      }
    }
    return -1
  }

  @pure def predictTable(j: Z): Z = {
    val tokenJ = tokens.at(j)
    if (tokenJ.kind == Result.Kind.Normal) {
      tokenJ.leaf.tipe match {
        case s32"0xFE2BCE26" /* OTHER */ => return 1
        case _ =>
      }
    }
    return -1
  }

  @pure def predictCas(j: Z): Z = {
    val tokenJ = tokens.at(j)
    if (tokenJ.kind == Result.Kind.Normal) {
      tokenJ.leaf.tipe match {
        case s32"0xFE2BCE26" /* OTHER */ => return 1
        case _ =>
      }
    }
    return -1
  }

  @pure def predictStars(j: Z): Z = {
    val tokenJ = tokens.at(j)
    if (tokenJ.kind == Result.Kind.Normal) {
      tokenJ.leaf.tipe match {
        case s32"0xFE2BCE26" /* OTHER */ => return 1
        case _ =>
      }
    }
    return -1
  }

  @pure def predictHeader(j: Z): Z = {
    val tokenJ = tokens.at(j)
    if (tokenJ.kind == Result.Kind.Normal) {
      tokenJ.leaf.tipe match {
        case s32"0x63CEDE8B" /* HASH */ => return 1
        case s32"0xFE2BCE26" /* OTHER */ => return 1
        case _ =>
      }
    }
    return -1
  }

  @pure def predictHlinep(j: Z): Z = {
    val tokenJ = tokens.at(j)
    if (tokenJ.kind == Result.Kind.Normal) {
      tokenJ.leaf.tipe match {
        case s32"0x7566CFCC" /* HLINE */ => return 1
        case _ =>
      }
    }
    return -1
  }

  @pure def predictRow(j: Z): Z = {
    val tokenJ = tokens.at(j)
    if (tokenJ.kind == Result.Kind.Normal) {
      tokenJ.leaf.tipe match {
        case s32"0x63CEDE8B" /* HASH */ => return 1
        case s32"0xFE2BCE26" /* OTHER */ => return 1
        case _ =>
      }
    }
    return -1
  }

  @pure def predictOthers(j: Z): Z = {
    val tokenJ = tokens.at(j)
    if (tokenJ.kind == Result.Kind.Normal) {
      tokenJ.leaf.tipe match {
        case s32"0xFE2BCE26" /* OTHER */ => return 1
        case _ =>
      }
    }
    return 0
  }

  @pure def predictHlines(j: Z): Z = {
    val tokenJ = tokens.at(j)
    if (tokenJ.kind == Result.Kind.Normal) {
      tokenJ.leaf.tipe match {
        case s32"0x7566CFCC" /* HLINE */ => return 1
        case _ =>
      }
    }
    return -1
  }

  @pure def predictRows(j: Z): Z = {
    val tokenJ = tokens.at(j)
    if (tokenJ.kind == Result.Kind.Normal) {
      tokenJ.leaf.tipe match {
        case s32"0x63CEDE8B" /* HASH */ => return 1
        case s32"0xFE2BCE26" /* OTHER */ => return 1
        case _ =>
      }
    }
    return 0
  }

  @pure def predictAssign(j: Z): Z = {
    val tokenJ = tokens.at(j)
    if (tokenJ.kind == Result.Kind.Normal) {
      tokenJ.leaf.tipe match {
        case s32"0x951E9CFB" /* LSQUARE */ => return 1
        case _ =>
      }
    }
    return -1
  }

  def retVal(n: Z, resOpt: Option[Result], initial: B, noBacktrack: B): Result = {
    resOpt match {
      case Some(res) => return res
      case _ => return Result.error(F, if (noBacktrack && !initial) -n else n)
    }
  }

  @pure def posOpts(docInfo: message.DocInfo,
                    posOpt1: Option[message.Position],
                    posOpt2: Option[message.Position]): Option[message.Position] = {
    val pos1 = posOpt1.get
    val pos2 = posOpt2.get
    return Some(message.PosInfo(docInfo, offsetLength(pos1.offset,
      pos2.offset + pos2.length - pos1.offset)))
  }

}

@datatype class SlangTruthTableLl1Lexer(cis: Indexable.Pos[C]) {

  def tokenizeAll(skipHidden: B, stopAtError: B, reporter: message.Reporter): ISZ[Result] = {
    var i: Z = 0
    var r = ISZ[Result]()
    var done = F
    while (!done && cis.has(i)) {
      tokenize(i, skipHidden) match {
        case Some(result) =>
          if (result.kind == Result.Kind.Normal) {
            i = result.newIndex
            r = r :+ result
          } else {
            val posOpt = cis.posOpt(i, 1)
            reporter.error(posOpt, kind, s"Could not recognize token")
            if (stopAtError) {
              return r
            }
            r = r :+ result(tree = errorLeaf(text = conversions.String.fromCis(ISZ(cis.at(i))), posOpt = posOpt))
            i = i + 1
          }
        case _ => done = T
      }
    }
    r = r :+ Result.create(eofLeaf, -1)
    return r
  }

  @pure def tokenize(i: Z, skipHidden: B): Option[Result] = {
    val r = MBox(Result.error(T, i))
    tokenizeH(r, i)
    while (skipHidden && r.value.leaf.isHidden && cis.has(r.value.newIndex)) {
      tokenizeH(r, r.value.newIndex)
    }
    return if (skipHidden && r.value.leaf.isHidden) None() else Some(r.value)
  }

  def tokenizeH(r: MBox[Result], i: Z): Unit = {
    updateToken(r, lex_Tautology(i))
    updateToken(r, lex_Contradictory(i))
    updateToken(r, lex_Contingent(i))
    updateToken(r, lex_Valid(i))
    updateToken(r, lex_Invalid(i))
    updateToken(r, lex_HLINE(i))
    updateToken(r, lex_HASH(i))
    updateToken(r, lex_NL(i))
    updateToken(r, lex_LSQUARE(i))
    updateToken(r, lex_RSQUARE(i))
    updateToken(r, lex_COMMENT(i))
    updateToken(r, lex_WS(i))
    updateToken(r, lex_OTHER(i))
  }

  def updateToken(r: MBox[Result], rOpt: Option[Result]): Unit = {
    rOpt match {
      case Some(newR) if newR.newIndex > r.value.newIndex => r.value = newR
      case _ =>
    }
  }

  @pure def lit_Tautology(i: Z): Z = {
    if (!cis.has(i + 8)) {
      return -1
    }
    if (cis.at(i) == 'T' && cis.at(i + 1) == 'a' && cis.at(i + 2) == 'u' && cis.at(i + 3) == 't' && cis.at(i + 4) == 'o' && cis.at(i + 5) == 'l' && cis.at(i + 6) == 'o' && cis.at(i + 7) == 'g' && cis.at(i + 8) == 'y') {
      return i + 9
    }
    return -1
  }

  @pure def lex_Tautology(index: Z): Option[Result] = { return lexH(index, lit_Tautology(index), """'Tautology'""", s32"0xE8F861E8" /* "Tautology" */, F) }

  @pure def lit_Contradictory(i: Z): Z = {
    if (!cis.has(i + 12)) {
      return -1
    }
    if (cis.at(i) == 'C' && cis.at(i + 1) == 'o' && cis.at(i + 2) == 'n' && cis.at(i + 3) == 't' && cis.at(i + 4) == 'r' && cis.at(i + 5) == 'a' && cis.at(i + 6) == 'd' && cis.at(i + 7) == 'i' && cis.at(i + 8) == 'c' && cis.at(i + 9) == 't' && cis.at(i + 10) == 'o' && cis.at(i + 11) == 'r' && cis.at(i + 12) == 'y') {
      return i + 13
    }
    return -1
  }

  @pure def lex_Contradictory(index: Z): Option[Result] = { return lexH(index, lit_Contradictory(index), """'Contradictory'""", s32"0x2E50643A" /* "Contradictory" */, F) }

  @pure def lit_Contingent(i: Z): Z = {
    if (!cis.has(i + 9)) {
      return -1
    }
    if (cis.at(i) == 'C' && cis.at(i + 1) == 'o' && cis.at(i + 2) == 'n' && cis.at(i + 3) == 't' && cis.at(i + 4) == 'i' && cis.at(i + 5) == 'n' && cis.at(i + 6) == 'g' && cis.at(i + 7) == 'e' && cis.at(i + 8) == 'n' && cis.at(i + 9) == 't') {
      return i + 10
    }
    return -1
  }

  @pure def lex_Contingent(index: Z): Option[Result] = { return lexH(index, lit_Contingent(index), """'Contingent'""", s32"0x584EF8BA" /* "Contingent" */, F) }

  @pure def lit_Valid(i: Z): Z = {
    if (!cis.has(i + 4)) {
      return -1
    }
    if (cis.at(i) == 'V' && cis.at(i + 1) == 'a' && cis.at(i + 2) == 'l' && cis.at(i + 3) == 'i' && cis.at(i + 4) == 'd') {
      return i + 5
    }
    return -1
  }

  @pure def lex_Valid(index: Z): Option[Result] = { return lexH(index, lit_Valid(index), """'Valid'""", s32"0x56000B93" /* "Valid" */, F) }

  @pure def lit_Invalid(i: Z): Z = {
    if (!cis.has(i + 6)) {
      return -1
    }
    if (cis.at(i) == 'I' && cis.at(i + 1) == 'n' && cis.at(i + 2) == 'v' && cis.at(i + 3) == 'a' && cis.at(i + 4) == 'l' && cis.at(i + 5) == 'i' && cis.at(i + 6) == 'd') {
      return i + 7
    }
    return -1
  }

  @pure def lex_Invalid(index: Z): Option[Result] = { return lexH(index, lit_Invalid(index), """'Invalid'""", s32"0x1E4BCCF1" /* "Invalid" */, F) }

  @pure def dfa_HLINE(i: Z): Z = {
    val ctx = LContext.create(ISZ(state"5"), i)

    while (cis.has(ctx.j)) {
      ctx.state match {
        case state"0" =>
          val c = cis.at(ctx.j)
          ctx.found = F
          if (c == '-') {
            ctx.update(state"1")
          }
          if (!ctx.found) {
            return ctx.afterAcceptIndex
          }
        case state"1" =>
          val c = cis.at(ctx.j)
          ctx.found = F
          if (c == '-') {
            ctx.update(state"2")
          }
          if (!ctx.found) {
            return ctx.afterAcceptIndex
          }
        case state"2" =>
          val c = cis.at(ctx.j)
          ctx.found = F
          if (c == '-') {
            ctx.update(state"3")
          }
          if (!ctx.found) {
            return ctx.afterAcceptIndex
          }
        case state"3" =>
          val c = cis.at(ctx.j)
          ctx.found = F
          if (c == '-') {
            ctx.update(state"4")
          }
          if (!ctx.found) {
            return ctx.afterAcceptIndex
          }
        case state"4" =>
          val c = cis.at(ctx.j)
          ctx.found = F
          if (c == '-') {
            ctx.update(state"5")
          }
          if (!ctx.found) {
            return ctx.afterAcceptIndex
          }
        case state"5" =>
          val c = cis.at(ctx.j)
          ctx.found = F
          if (c == '-') {
            ctx.update(state"5")
          }
          if (!ctx.found) {
            return ctx.afterAcceptIndex
          }
        case _ => halt("Infeasible")
      }
      ctx.j = ctx.j + 1
    }
    return ctx.afterAcceptIndex
  }

  @pure def lex_HLINE(index: Z): Option[Result] = { return lexH(index, dfa_HLINE(index), """HLINE""", s32"0x7566CFCC", F) }

  @pure def dfa_HASH(i: Z): Z = {
    val ctx = LContext.create(ISZ(state"1"), i)

    while (cis.has(ctx.j)) {
      ctx.state match {
        case state"0" =>
          val c = cis.at(ctx.j)
          ctx.found = F
          if (c == '#') {
            ctx.update(state"1")
          }
          if (!ctx.found) {
            return ctx.afterAcceptIndex
          }
        case state"1" => return ctx.afterAcceptIndex
        case _ => halt("Infeasible")
      }
      ctx.j = ctx.j + 1
    }
    return ctx.afterAcceptIndex
  }

  @pure def lex_HASH(index: Z): Option[Result] = { return lexH(index, dfa_HASH(index), """HASH""", s32"0x63CEDE8B", F) }

  @pure def dfa_NL(i: Z): Z = {
    val ctx = LContext.create(ISZ(state"1"), i)

    while (cis.has(ctx.j)) {
      ctx.state match {
        case state"0" =>
          val c = cis.at(ctx.j)
          ctx.found = F
          if (c == '\u000A') {
            ctx.update(state"1")
          }
          if (!ctx.found) {
            return ctx.afterAcceptIndex
          }
        case state"1" => return ctx.afterAcceptIndex
        case _ => halt("Infeasible")
      }
      ctx.j = ctx.j + 1
    }
    return ctx.afterAcceptIndex
  }

  @pure def lex_NL(index: Z): Option[Result] = { return lexH(index, dfa_NL(index), """NL""", s32"0x6CB684C0", F) }

  @pure def dfa_LSQUARE(i: Z): Z = {
    val ctx = LContext.create(ISZ(state"1"), i)

    while (cis.has(ctx.j)) {
      ctx.state match {
        case state"0" =>
          val c = cis.at(ctx.j)
          ctx.found = F
          if (c == '[') {
            ctx.update(state"1")
          }
          if (!ctx.found) {
            return ctx.afterAcceptIndex
          }
        case state"1" => return ctx.afterAcceptIndex
        case _ => halt("Infeasible")
      }
      ctx.j = ctx.j + 1
    }
    return ctx.afterAcceptIndex
  }

  @pure def lex_LSQUARE(index: Z): Option[Result] = { return lexH(index, dfa_LSQUARE(index), """LSQUARE""", s32"0x951E9CFB", F) }

  @pure def dfa_RSQUARE(i: Z): Z = {
    val ctx = LContext.create(ISZ(state"1"), i)

    while (cis.has(ctx.j)) {
      ctx.state match {
        case state"0" =>
          val c = cis.at(ctx.j)
          ctx.found = F
          if (c == ']') {
            ctx.update(state"1")
          }
          if (!ctx.found) {
            return ctx.afterAcceptIndex
          }
        case state"1" => return ctx.afterAcceptIndex
        case _ => halt("Infeasible")
      }
      ctx.j = ctx.j + 1
    }
    return ctx.afterAcceptIndex
  }

  @pure def lex_RSQUARE(index: Z): Option[Result] = { return lexH(index, dfa_RSQUARE(index), """RSQUARE""", s32"0xA97171F1", F) }

  @pure def dfa_COMMENT(i: Z): Z = {
    val ctx = LContext.create(ISZ(state"3"), i)

    while (cis.has(ctx.j)) {
      ctx.state match {
        case state"0" =>
          val c = cis.at(ctx.j)
          ctx.found = F
          if (c == '/') {
            ctx.update(state"1")
          }
          if (!ctx.found) {
            return ctx.afterAcceptIndex
          }
        case state"1" =>
          val c = cis.at(ctx.j)
          ctx.found = F
          if (c == '/') {
            ctx.update(state"2")
          }
          if (!ctx.found) {
            return ctx.afterAcceptIndex
          }
        case state"2" =>
          val c = cis.at(ctx.j)
          ctx.found = F
          if (minChar <= c && c <= '\u0009' || '\u000B' <= c && c <= '\u000C' || '\u000E' <= c && c <= maxChar) {
            ctx.update(state"2")
          } else if (c == '\u000A') {
            ctx.update(state"3")
          } else if (c == '\u000D') {
            ctx.update(state"4")
          }
          if (!ctx.found) {
            return ctx.afterAcceptIndex
          }
        case state"3" => return ctx.afterAcceptIndex
        case state"4" =>
          val c = cis.at(ctx.j)
          ctx.found = F
          if (c == '\u000A') {
            ctx.update(state"3")
          }
          if (!ctx.found) {
            return ctx.afterAcceptIndex
          }
        case _ => halt("Infeasible")
      }
      ctx.j = ctx.j + 1
    }
    return ctx.afterAcceptIndex
  }

  @pure def lex_COMMENT(index: Z): Option[Result] = { return lexH(index, dfa_COMMENT(index), """COMMENT""", s32"0x486B464F", T) }

  @pure def dfa_WS(i: Z): Z = {
    val ctx = LContext.create(ISZ(state"1"), i)

    while (cis.has(ctx.j)) {
      ctx.state match {
        case state"0" =>
          val c = cis.at(ctx.j)
          ctx.found = F
          if (c == '\u0009' || c == '\u000D' || c == ' ') {
            ctx.update(state"1")
          }
          if (!ctx.found) {
            return ctx.afterAcceptIndex
          }
        case state"1" =>
          val c = cis.at(ctx.j)
          ctx.found = F
          if (c == '\u0009' || c == '\u000D' || c == ' ') {
            ctx.update(state"1")
          }
          if (!ctx.found) {
            return ctx.afterAcceptIndex
          }
        case _ => halt("Infeasible")
      }
      ctx.j = ctx.j + 1
    }
    return ctx.afterAcceptIndex
  }

  @pure def lex_WS(index: Z): Option[Result] = { return lexH(index, dfa_WS(index), """WS""", s32"0x0E3F5D1E", T) }

  @pure def dfa_OTHER(i: Z): Z = {
    val ctx = LContext.create(ISZ(state"1"), i)

    while (cis.has(ctx.j)) {
      ctx.state match {
        case state"0" =>
          val c = cis.at(ctx.j)
          ctx.found = F
          if (minChar <= c && c <= maxChar) {
            ctx.update(state"1")
          }
          if (!ctx.found) {
            return ctx.afterAcceptIndex
          }
        case state"1" => return ctx.afterAcceptIndex
        case _ => halt("Infeasible")
      }
      ctx.j = ctx.j + 1
    }
    return ctx.afterAcceptIndex
  }

  @pure def lex_OTHER(index: Z): Option[Result] = { return lexH(index, dfa_OTHER(index), """OTHER""", s32"0xFE2BCE26", F) }

  @pure def hidden(i: Z): Z = {
     var j: Z = -1
     j = dfa_COMMENT(i)
     if (j > 0) {
       return j
     }
     j = dfa_WS(i)
     if (j > 0) {
       return j
     }
     return -1
  }

  @pure def lexH(index: Z, newIndex: Z, name: String, tipe: S32, isHidden: B): Option[Result] = {
    if (newIndex > 0) {
      return Some(Result.create(ParseTree.Leaf(conversions.String.fromCis(for (i <- index until newIndex) yield cis.at(i)),
        name, tipe, isHidden, cis.posOpt(index, newIndex - index)), newIndex))
    } else {
      return None()
    }
  }

}