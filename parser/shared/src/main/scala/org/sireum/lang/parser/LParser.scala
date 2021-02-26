/*
 Copyright (c) 2020, Robby, Kansas State University
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

import org.sireum.{B, F, ISZ, T, Z, None => SNone, Option => SOption, Some => SSome, String => SString}
import org.sireum.message
import org.sireum.message.Reporter

import scala.meta._
import scala.meta.inputs.Input
import scala.meta.internal.parsers.{InfixMode, ModifiedScalametaParser}
import scala.meta.internal.tokens.TokenInfo
import scala.meta.tokens.TokensHelper
import scala.meta.tokens.Token.{
  BOF,
  Colon,
  Comma,
  Dot,
  EOF,
  Ident,
  LeftBrace,
  LeftBracket,
  LeftParen,
  RightBrace,
  RightBracket,
  RightParen
}
import org.sireum.lang.{ast => AST}

object LParser {

  val impInternalSym = "imply_:"
  val simpInternalSym = "simply_:"

  val internalOpMap = Map(
    "→" -> impInternalSym,
    "->" -> impInternalSym,
    "==>" -> simpInternalSym,
    "⟹" -> simpInternalSym,
    "∧" -> "&",
    "^" -> "&",
    "∨" -> "|",
    "V" -> "|",
    "¬" -> "!",
    "≠" -> "!=",
    "≤" -> "<=",
    "≥" -> ">=",
    "⊤" -> "T",
    "⊥" -> "F",
    "≡" -> "==",
    "⊻" -> "|^",
    "⊢" -> "|-"
  )

  def apply[T](text: String, reporter: Reporter)(f: (LParser, Reporter) => T): T = {
    val input = Input.String(text.replace("\r\n", "\n")) // WORKAROUND: scalameta crlf issues
    val dialect = SlangParser.scalaDialect(isWorksheet = false)
    val slangParser = new SlangParser(
      input = input,
      dialect = dialect,
      hashSireum = false,
      isDiet = false,
      fileUriOpt = org.sireum.None(),
      isWorksheet = false,
      text = text,
      reporter = reporter
    )
    f(new LParser(input, dialect, slangParser), reporter)
  }
}

import LParser._

final class LParser(input: Input, dialect: Dialect, sparser: SlangParser)
    extends ModifiedScalametaParser(input, dialect) {

  object loutPattern extends PatternContextSensitive {
    override def infixTypeRest(t: Type, mode: InfixMode.Value): Type = {
      token match {
        case Ident(value) if value.forall(Character.isJavaIdentifierPart) => t
        case _ => super.infixTypeRest(t, mode)
      }
    }

    def argType(): Type = typ()

    def functionArgType(): Type = paramType()
  }

  class LimitingTokenIterator(var i: Int, val end: Int) extends TokenIterator {
    def hasNext: Boolean = i < parserTokens.length - 1

    def next(): Token = {
      if (!hasNext) throw new NoSuchElementException()
      i += 1
      token
    }

    def prevTokenPos: Int = if (i > 0) parserTokenPositions(Math.min(i, parserTokens.length - 1) - 1) else -1

    def tokenPos: Int = if (i > -1) parserTokenPositions(Math.min(i, parserTokens.length - 1)) else -1

    def token: Token = if (i > end) parserTokens(parserTokens.length - 1) else parserTokens(i)

    def fork: TokenIterator = new LimitingTokenIterator(i, end)
  }

  @inline def aheadNF[T](body: => T): T = {
    next()
    body
  }

  @inline def tokenCurrOrAfterNl(p: => Boolean): Boolean =
    p || TokensHelper.isNl(token) && ahead(p)

  {
    in = new LimitingTokenIterator(0, parserTokens.length - 1)

    val sTokens = TokensHelper.extractArray(scannerTokens)
    val pTokens = TokensHelper.extractArray(parserTokens)
    var iS = 0
    var iP = 0
    while (iS < sTokens.length) {
      sTokens(iS) match {
        case t @ Ident(value) =>
          internalOpMap.get(value) match {
            case Some(op) =>
              val t2 = t.copy(value = op)
              sTokens(iS) = t2
              while (t ne pTokens(iP)) iP += 1
              pTokens(iP) = t2
            case _ =>
          }
        case _ =>
      }
      iS += 1
    }
  }

  def isSequentToken(t: Token): Boolean = t match {
    case Ident(value) => "|-" == value
    case _ => false
  }

  def acceptToken[T <: Token: TokenInfo]: T = {
    val t = token
    accept[T]
    t.asInstanceOf[T]
  }

  def acceptIdent(expected: String): Ident = token match {
    case Ident(value) if value == expected =>
      val r = token.asInstanceOf[Ident]
      next()
      r
    case _ => reporter.syntaxError(s"$expected expected but ${TokensHelper.name(token)} found.", at = token)
  }

  def acceptInt: AST.Exp.LitZ = {
    val t = acceptToken[Token.Constant.Int]
    AST.Exp.LitZ(Z(t.value.toInt), sparser.attr(t.pos))
  }

  override def isInfixOp: Boolean = token match {
    case Ident(value) if value.forall(Character.isJavaIdentifierPart) || value == "∀" || value == "∃" => false
    case Ident(_) => true
    case _ => false
  }

  def idents(): List[Ident] = {
    var ids = List(acceptToken[Ident])
    while (token.is[Comma]) {
      next()
      ids ::= acceptToken[Ident]
    }
    ids.reverse
  }

  def id(ident: Ident): AST.Id = AST.Id(ident.value, sparser.attr(ident.pos))

  def isz[T](l: List[T]): ISZ[T] = ISZ(l: _*)

  def exprnls(continue: => Boolean): List[AST.Exp] = {
    var r = List(sparser.translateExp(expr()))
    newLinesOpt()
    while (continue) {
      r ::= sparser.translateExp(expr())
      newLinesOpt()
    }
    r.reverse
  }

  /** {{{
    *  Sequent       ::= [ Exprs ] {nl} ( Ident<|-> | Ident<⊢> ) {nl} Exprs {nl}
    *
    *  Exprs         ::= Expr {`,' {nl} Expr}
    *  }}}
    */
  def sequent(
    sequentIndex: Int = findTokenPos(isSequentToken, t => t.isNot[LeftBrace]),
    noProof: Boolean = false
  ): AST.Sequent = {
    if (sequentIndex < 0) reporter.syntaxError(s"... ⊢ ... expected", at = token)
    val emptyPremises = tokenCurrOrAfterNl(isSequentToken(token))
    val oldIn = in.asInstanceOf[LimitingTokenIterator]
    val premises: List[AST.Exp] =
      if (emptyPremises) List()
      else {
        in = new LimitingTokenIterator(oldIn.i, sequentIndex - 1)
        exprs()
      }
    in = new LimitingTokenIterator(sequentIndex + 1, oldIn.end)
    newLinesOpt()
    val conclusion = sparser.translateExp(expr())
    newLinesOpt()
    AST.Sequent(isz(premises), conclusion, ISZ(), AST.Attr(SNone()))
  }

  def exprs(): List[AST.Exp] = {
    var r = List(sparser.translateExp(expr()))
    while (token.is[Comma]) {
      next()
      newLinesOpt()
      r ::= sparser.translateExp(expr())
    }
    r.reverse
  }

  def findTokenPos(p: Token => Boolean, continue: Token => Boolean): Int = {
    val lti = in.fork.asInstanceOf[LimitingTokenIterator]
    var found = p(lti.token)
    while (!found && lti.token.isNot[EOF] && continue(lti.token)) {
      lti.next()
      found = p(lti.token)
    }
    if (found) lti.i else -1
  }

  /** {{{
    *  TruthTable     ::= BOF {nl}
    *                     Ident<*> { Ident<*> } {nl}
    *                     Ident<----...> {nl}
    *                     { Ident } Ident<|> ( Sequent | Expr ) {nl}   // Sequent without Proof
    *                     Ident<----...> {nl}
    *                     { TruthTableRow {nl} }
    *                     Ident<----...> {nl}
    *                     [ TruthTableConc {nl} ]
    *                     EOF
    *
    *  TruthTableRow  ::= [ BLits ] Ident<|> BLits
    *
    *  BLits          ::= BLit { BLit }
    *
    *  BLit           ::= Ident<T> | Ident<F>                          // Can be sequenced without whitespace
    *
    *  TruthTableConc ::= Ident<Valid> { {nl} `[' BLits `]' }
    *                   |  Ident<Invalid> { {nl} `[' BLits `]' }
    *                   |  Ident<Tautology>
    *                   |  Ident<Contradictory>
    *                   |  Ident<Contingent> {nl}
    *                      [ Ident<-> ] Ident<T> `:' {nl} `[' BLits `]' { {nl} `[' BLits `]' } {nl}
    *                      [ Ident<-> ] Ident<F> `:' {nl} `[' BLits `]' { {nl} `[' BLits `]' }
    *  }}}
    */
  def truthTable(fileUriOpt: SOption[SString]): SlangParser.Result = {
    def conclusion(isSequent: Boolean): SOption[AST.TruthTable.Conclusion] = {
      def assignment(): AST.TruthTable.Assignment = {
        newLinesOpt()
        accept[LeftBracket]
        val r = blits()
        accept[RightBracket]
        r
      }

      if (token.isNot[Ident]) return SNone()
      val text = token match {
        case Ident(value) => value
        case _ => return SNone()
      }
      val attr = sparser.attr(token.pos)
      val r =
        if (isSequent)
          text match {
            case "Valid" | "Invalid" =>
              next()
              val isValid = if (text == "Valid") T else F
              var assignments = List[AST.TruthTable.Assignment](assignment())
              newLinesOpt()
              while (tokenCurrOrAfterNl(token.is[LeftBracket])) {
                assignments ::= assignment()
              }
              AST.TruthTable.Conclusion.Validity(isValid, isz(assignments.reverse), attr)
            case _ => reporter.syntaxError(s"Either Valid or Invalid expected but found $text", at = token)
          } else
          text match {
            case "Tautology" =>
              next()
              AST.TruthTable.Conclusion.Tautology(attr)
            case "Contradictory" =>
              next()
              AST.TruthTable.Conclusion.Contradictory(attr)
            case "Contingent" =>
              next()
              newLinesOpt()
              if (isIdentOf("-")) next()
              if (!isIdentOf("T")) reporter.syntaxError(s"T expected but ${TokensHelper.name(token)} found", at = token)
              next()
              accept[Colon]
              var truthAssignments = List(assignment())
              while (tokenCurrOrAfterNl(token.is[LeftBracket])) truthAssignments ::= assignment()
              newLinesOpt()
              if (isIdentOf("-")) next()
              if (!isIdentOf("F")) reporter.syntaxError(s"T expected but ${TokensHelper.name(token)} found", at = token)
              next()
              accept[Colon]
              var falseAssignments = List(assignment())
              while (tokenCurrOrAfterNl(token.is[LeftBracket])) falseAssignments ::= assignment()
              AST.TruthTable.Conclusion.Contingent(isz(truthAssignments.reverse), isz(falseAssignments.reverse), attr)
            case _ =>
              reporter
                .syntaxError(s"Either Tautology, Contradictory, or Contingent expected but found $text", at = token)
          }
      newLinesOpt()
      SSome(r)
    }

    def blits(): AST.TruthTable.Assignment = {
      def isBlit: Boolean = token match {
        case Ident(value) => value.forall(c => c == 'T' || c == 'F')
        case _ => false
      }

      def blit(): List[AST.Exp.LitB] = {
        val token = acceptToken[Ident]
        val offset = token.pos.start
        val value = token.value
        (for (j <- 0 until value.length) yield {
          AST.Exp.LitB(value.charAt(j) == 'T', sparser.attr(Position.Range(input, offset + j, offset + j + 1)))
        }).toList
      }

      if (!isBlit) reporter.syntaxError(s"Either T or F expected, but ${TokensHelper.name(token)} found", at = token)
      var r = blit()
      while (isBlit) {
        r ++= blit()
      }
      AST.TruthTable.Assignment(isz(r), AST.Attr(AST.Util.posOptRange(r.head.posOpt, r.last.posOpt)))
    }

    def isHLine(t: Token): Boolean = token match {
      case Ident(value) => value.length > 3 && value.forall(_ == '-')
      case _ => false
    }

    def acceptHLine(): Unit = {
      if (isHLine(token)) next()
      else reporter.syntaxError(s"----... expected but ${TokensHelper.name(token)} found", at = token)
      newLinesOpt()
    }

    var vars = List[AST.Id]()
    var sep: message.Position = null
    var formula: AST.Sequent = null
    var isSequent: B = F

    def header(): Unit = {
      while (!isIdentOf("|") && token.isNot[EOF]) {
        vars ::= id(acceptToken[Ident])
      }
      vars = vars.reverse
      sep = sparser.posInfo(token.pos)
      acceptIdent("|")
      val i = findTokenPos(isSequentToken, t => !isHLine(t))
      formula =
        if (i < 0) AST.Sequent(ISZ(), sparser.translateExp(expr()), ISZ(), AST.Attr(SNone()))
        else {
          isSequent = T
          sequent(noProof = true)
        }
      newLinesOpt()
    }

    var stars = List[message.Position]()

    def start(): Unit = {
      if (isIdentOf("*")) {
        stars ::= sparser.posInfo(token.pos)
        next()
      } else reporter.syntaxError(s"* expected but ${TokensHelper.name(token)} found", at = token)

      while (isIdentOf("*")) {
        stars ::= sparser.posInfo(token.pos)
        next()
      }
      newLinesOpt()
      stars = stars.reverse
    }

    var rows = List[AST.TruthTable.Row]()

    def row(): Unit = {
      val assignment = if (!isIdentOf("|")) blits() else AST.TruthTable.Assignment(ISZ(), AST.Attr(SNone()))
      val t = acceptIdent("|")
      rows ::= AST.TruthTable.Row(assignment, sparser.posInfo(t.pos), blits())
      newLinesOpt()
    }

    accept[BOF]
    newLinesOpt()
    start()
    acceptHLine()
    header()
    acceptHLine()
    while (!isHLine(token) && token.isNot[EOF]) row()
    acceptHLine()
    val r = AST.TopUnit.TruthTableUnit(
      fileUriOpt,
      isz(stars),
      isz(vars),
      sep,
      isSequent,
      formula,
      isz(rows.reverse),
      conclusion(isSequent)
    )
    newLinesOpt()
    accept[EOF]
    SlangParser.Result(input.text, hashSireum = false, SSome(r))
  }
}
