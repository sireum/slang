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

package org.sireum.lang.parser

import org.sireum.{B, F, ISZ, T, Z, None => SNone, Option => SOption, Some => SSome, String => SString}
import org.sireum.message
import org.sireum.message.Reporter

import scala.collection.immutable.ListSet
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
  KwCase,
  KwDef,
  KwIf,
  LeftBrace,
  LeftBracket,
  LeftParen,
  RightBrace,
  RightBracket,
  RightParen
}
import org.sireum.lang.{ast => AST}

object LParser {

  val forallTokens = ListSet("∀", "A", "all", "forall")
  val existsTokens = ListSet("∃", "E", "some", "exists")
  val quantTokens: ListSet[String] = forallTokens ++ existsTokens
  val lStmtFirst = ListSet("requires", "theorem", "fact")
  val implyInternalSym = "$->:"

  val internalOpMap = Map(
    "→" -> implyInternalSym,
    "->" -> implyInternalSym,
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
    val input = Input.String(text.replaceAllLiterally("\r\n", "\n")) // WORKAROUND: scalameta crlf issues
    val dialect = SlangParser.scalaDialect(isWorksheet = false)
    val slangParser = new SlangParser(
      input = input,
      dialect = dialect,
      allowSireumPackage = false,
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

  val justOps: List[() => Option[(String, Int)]] = List(
    () => {
      var text = ""
      val r = (isIdentOf(implyInternalSym) || isIdentOf("&") || isIdentOf("|") ||
        isIdentOf("!") || isIdentOf("¬") || isIdentOf("~") || isIdentOf("∀") ||
        isIdentOf("∃") || isIdentOf("F")) && {
        text = token.asInstanceOf[Ident].value.
          replaceAllLiterally("!", "¬").
          replaceAllLiterally("~", "¬").
          replaceAllLiterally("&", "∧").
          replaceAllLiterally("|", "∨").
          replaceAllLiterally("F", "⊥").
          replaceAllLiterally(implyInternalSym, "→")
        ahead {
          text += token.text
          token.is[Ident]
        }
      }
      if (r) Some((text, 5)) else None
    },
    () => {
      var text = "⊥"
      val r = {
        val t1 = token
        isIdentOf("_") && ahead {
          val t2 = token
          isIdentOf("|") && t2.pos.start == t1.pos.start + 1 && aheadNF {
            val t3 = token
            isIdentOf("_") && t3.pos.start == t2.pos.start + 1 &&
            aheadNF {
              text += token.text
              token.is[Ident]
            }
          }
        }
      }
      if (r) Some((text, 7)) else None
    }
  )

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

  def findJust(): (Int, String, Int) = {
    def continue: Boolean =
      if (token.is[RightBrace] || token.is[EOF]) false
      else if (TokensHelper.isNl(token) && ahead(token.is[Token.Constant.Int] && aheadNF(token.is[Dot]))) false
      else true

    def find(): Option[(String, Int)] = {
      for (jp <- justOps) {
        val r = jp()
        if (r.isDefined)
          return r
      }
      token match {
        case Ident(value) => Some((value, 4))
        case _ => None
      }
    }

    val oldIn = in
    try {
      in = oldIn.fork
      var just: Option[(String, Int)] = None
      while (continue && just.isEmpty) {
        next()
        if (token.is[Dot] & ahead(token.is[Dot] && aheadNF(token.is[Dot]))) {
          ahead {
            aheadNF {
              aheadNF {
                just = find()
              }
            }
          }
        }
      }
      if (just.isDefined) (in.asInstanceOf[LimitingTokenIterator].i, just.get._1, just.get._2) else (-1, "", 0)
    } finally in = oldIn
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

  /** {{{
    *  SimplExpr      ::= QuantExpr
    *                    |  ... super.simpleExpr()
    *  }}}
    */
  override def simpleExpr(allowRepeated: Boolean): Term = token match {
    case Ident(value) if quantTokens.contains(value) => quantExpr()
    case _ => super.simpleExpr(allowRepeated)
  }

  /** {{{
    *  QuantExpr      ::= QuantOp Idents [ `:' Domain ] [nl] PostfixExpr
    *
    *  Idents         ::= Ident { `,' Ident }
    *
    *  QuantOp        ::= Ident<∀> | Ident<A> | Ident<all> | Ident<forall> | Ident<∃> | Ident<E> | Ident<some> | Ident<exists>
    *
    *  Domain         ::= ( `(' | `<' | `[' ) Expr ',' Expr ( `)' | `>' | `]' )
    *                   |  Type
    *  }}}
    */
  private def quantExpr(): Term = autoPos {
    def acceptKw(kws: ListSet[String]): String = token match {
      case Ident(value) if kws.contains(value) =>
        next()
        value
      case _ =>
        reporter
          .syntaxError(s"Either ${kws.mkString(" or ")} expected but ${TokensHelper.name(token)} found", at = token)
    }

    val isForAll = forallTokens.contains(acceptKw(quantTokens))

    val ids: Term = {
      var r = List[Term.Name](autoPos {
        Term.Name(acceptToken[Ident].value)
      })

      while (token.is[Comma]) {
        accept[Comma]
        r ::= autoPos {
          Term.Name(acceptToken[Ident].value)
        }
      }
      if (r.size > 1) Term.Tuple(r.reverse) else r.head
    }

    if (token.isNot[Colon]) {
      newLineOpt()
      val e = expr()
      Term.Apply(Term.Name("L$Quant"), List[Term](Lit.Boolean(isForAll), ids, e))
    } else {
      next()
      domainRaw() match {
        case Left(t) =>
          newLineOpt()
          val e = expr()
          Term.Apply(Term.Name("L$Quant"), List[Term](Lit.Boolean(isForAll), Term.Ascribe(ids, t), e))
        case Right(q) =>
          newLineOpt()
          val e = expr()
          Term.Apply(Term.Name("L$Quant"), List[Term](Lit.Boolean(isForAll), ids, q, e))
      }
    }
  }

  def domainRaw(): Either[Type, Term.Tuple] =
    if (token.is[LeftParen] || token.is[LeftBracket])
      Right(autoPos {
        val loExact = token.is[LeftBracket]
        next()
        val lo = expr()
        accept[Comma]
        val hi = expr()
        val hiExact = token.is[RightBracket]
        if (hiExact) next() else accept[RightParen]
        Term.Tuple(List(lo, Lit.Boolean(loExact), hi, Lit.Boolean(hiExact)))
      })
    else Left(loutPattern.typ())

  def domain(): AST.Domain = domainRaw() match {
    case Left(t) => AST.Domain.Type(sparser.translateType(t), sparser.typedAttr(t.pos))
    case Right(tt @ Term.Tuple(Seq(lo, loExact: Lit.Boolean, hi, hiExact: Lit.Boolean, _))) =>
      AST.Domain.Range(
        sparser.translateExp(lo),
        if (loExact.value) T else F,
        sparser.translateExp(hi),
        if (hiExact.value) T else F,
        sparser.typedAttr(tt.pos)
      )
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

  /** {{{
    *  DefContract    ::= BOF {nl}
    *                     [ Ident<requires> {nl} OptNamedExprs ]
    *                     [ Ident<modifies> {nl} Expr { `,' {nl} Expr } ] {nl}
    *                     [ Ident<ensures> {nl} OptNamedExprs ]
    *                     { SubContract {nl} }
    *                     EOF
    *                   | BOF {nl}
    *                     DefContractCase {nl}
    *                     { DefContractCase {nl} }
    *                     { SubContract {nl} }
    *                     EOF
    *
    *  ContractCase   ::= `case' Ident `:'
    *                     [ Ident<requires> {nl} OptNamedExprs ]
    *                     [ Ident<modifies> {nl} Expr { `,' {nl} Expr } ] {nl}
    *                     [ Ident<ensures> {nl} OptNamedExprs ]
    *
    *  Exprnls        ::= Expr {nl} { Expr {nl} }
    *
    *  NamedExprs     ::= NamedExpr {nl} { NamedExpr {nl} }
    *
    *  NamedExpr      ::= Ident `:' {nl} Expr
    *
    *  SubContract    ::= `def' Ident `(' Ident {`,' Ident} `)' {nl} DefContract
    *
    *  PureOpt        ::= [ `@' Ident<pure> ]
    *  }}}
    */
  def defContract(): AST.Contract = {
    def subContract(): List[AST.SubContract] = {
      var r = List[AST.SubContract]()
      while (token.isNot[EOF]) {
        accept[KwDef]
        val ident = id(acceptToken[Ident])
        accept[LeftParen]
        val params = if (token.isNot[RightParen]) {
          var ps = List(id(acceptToken[Ident]))
          while (token.is[Comma]) {
            next()
            ps ::= id(acceptToken[Ident])
          }
          ps
        } else List()
        accept[RightParen]
        newLinesOpt()
        r ::= AST.SubContract(ident, isz(params.reverse), defContract())
      }
      r.reverse
    }

    def contractCase(idOpt: SOption[AST.Id]): AST.ContractCase = {
      val requires: List[AST.Exp] = if (isIdentOf("requires")) {
        next()
        newLinesOpt()
        exprnls(
          !(isIdentOf("modifies") || isIdentOf("ensures") || token.is[KwCase] || token.is[KwDef] || token.is[EOF])
        )
      } else List()
      val mods: List[AST.Exp] = modifies()
      val ensures: List[AST.Exp] = if (isIdentOf("ensures")) {
        next()
        newLinesOpt()
        exprnls(!(token.is[KwDef] || token.is[KwCase] || token.is[EOF]))
      } else List()
      AST.ContractCase(idOpt, isz(requires), isz(mods), isz(ensures))
    }

    accept[BOF]
    newLinesOpt()
    val cases: ISZ[AST.ContractCase] =
      if (token.is[KwCase]) {
        var cs = List[AST.ContractCase]()
        while (token.is[KwCase]) {
          next()
          val ident = acceptToken[Ident]
          acceptToken[Colon]
          newLinesOpt()
          cs ::= contractCase(SSome(id(ident)))
        }
        isz(cs.reverse)
      } else ISZ(contractCase(SNone()))
    newLinesOpt()
    val r = AST.Contract(cases, isz(subContract()))
    accept[EOF]
    r
  }

  def modifies(): List[AST.Exp] =
    if (isIdentOf("modifies")) {
      next()
      newLinesOpt()
      var r = List(sparser.translateExp(expr()))
      while (token.is[Comma]) {
        next()
        newLinesOpt()
        r ::= sparser.translateExp(expr())
      }
      newLinesOpt()
      r.reverse
    } else List()

  def namedExpr(): AST.NamedExp = {
      val ident = acceptToken[Ident]
      acceptToken[Colon]
      newLinesOpt()
      AST.NamedExp(id(ident), sparser.translateExp(expr()))
  }

  def optNamedExpr(): (SOption[AST.Id], AST.Exp) =
    if (token.is[Ident] && ahead(token.is[Colon])) {
      val ident = acceptToken[Ident]
      acceptToken[Colon]
      newLinesOpt()
      (SSome(id(ident)), sparser.translateExp(expr()))
    } else {
      (SNone(), sparser.translateExp(expr()))
    }

  def namedExprs(continue: => Boolean): List[AST.NamedExp] = {
    var r = List(namedExpr())
    newLinesOpt()
    while (continue) {
      r ::= namedExpr()
      newLinesOpt()
    }
    r.reverse
  }

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
    *  SpecDefs       ::= BOF
    *                     {nl} SpecDef {nl} { SpecDef {nl} }
    *                     EOF
    *
    *  SpecDef        ::= `=' NamedExpr [`,' ( [ case Pattern ] [ if PostfixExpr ] | Ident<otherwise> ) ]
    *  }}}
    */
  def specDefs(): List[AST.SpecDef] = {
    accept[BOF]
    newLinesOpt()
    var r = List(specDef())
    newLinesOpt()
    while (token.isNot[EOF]) {
      r ::= specDef()
      newLinesOpt()
    }
    accept[EOF]
    r
  }

  def specDef(): AST.SpecDef = {
    accept[Token.Equals]
    val (idOpt, e) = optNamedExpr()
    val (isOtherwise, patternOpt, guardOpt) = if (token.is[Comma]) {
      next()
      val pOpt: SOption[AST.Pattern] = if (token.is[KwCase]) {
        next()
        SSome(sparser.translatePattern(pattern()))
      } else SNone()
      val gOpt: SOption[AST.Exp] = if (token.is[KwIf]) {
        next()
        SSome(sparser.translateExp(postfixExpr(allowRepeated = false)))
      } else SNone()
      (F, pOpt, gOpt)
    } else if (isIdentOf("otherwise")) (T, SNone[AST.Pattern](), SNone[AST.Exp]())
    else (F, SNone[AST.Pattern](), SNone[AST.Exp]())
    AST.SpecDef(idOpt, e, isOtherwise, patternOpt, guardOpt)
  }

  /** {{{
    *  LoopInvMod     ::= BOF {nl}
    *                     [ Ident<invariant> {nl} NamedExprs ]
    *                     [ Ident<modifies> {nl} Expr {`,' {nl} Expr} ] {nl}
    *                     EOF
    *  }}}
    */
  def loopInvMode(): (List[AST.NamedExp], List[AST.Exp]) = {
    accept[BOF]
    val is = if (isIdentOf("invariant")) {
      next()
      newLinesOpt()
      namedExprs(token.isNot[EOF] || !isIdentOf("modifies"))
    } else List()
    val mods = modifies()
    accept[EOF]
    (is, mods)
  }

  /** {{{
    *  LClause        ::= BOF {nl}
    *                     ( Invariants
    *                     | Facts
    *                     | Theorems
    *                     | Sequent
    *                     | Proof
    *                     ) EOF
    *  }}}
    */
  def lClause(): AST.LClause = {
    accept[BOF]
    newLinesOpt()
    val r: AST.LClause =
      if (isIdentOf("invariant")) invariants()
      else if (isIdentOf("fact")) facts()
      else if (isIdentOf("theorem")) theorems()
      else if (token.is[LeftBrace]) proof()
      else {
        val i = findTokenPos(isSequentToken, t => t.isNot[LeftBrace])
        if (i < 0) reporter.syntaxError(s"Either invariant, fact, theorem, { ... }, or ... ⊢ ... expected", at = token)
        sequent(i)
      }
    accept[EOF]
    r
  }

  /** {{{
    *  Invariants     ::= Ident<invariant> {nl} NamedExprs
    *  }}}
    */
  private def invariants(): AST.LClause.Invariants = {
    next()
    newLinesOpt()
    AST.LClause.Invariants(isz(namedExprs(token.isNot[EOF])))
  }

  /** {{{
    *  Facts          ::= Ident<fact> {nl} Fact {nl} { Fact {nl} }
    *
    *  Fact           ::= Ident `:' {nl} Expr
    *  }}}
    */
  private def facts(): AST.LClause.Facts = {
    def fact(): AST.NamedExp = {
      val ident = id(acceptToken[Ident])
      accept[Colon]
      newLinesOpt()
      AST.NamedExp(ident, sparser.translateExp(expr()))
    }

    next()
    newLinesOpt()
    var fs = List(fact())
    newLinesOpt()
    while (token.is[Ident] && ahead(token.is[Colon])) {
      fs ::= fact()
    }
    AST.LClause.Facts(isz(fs.reverse))
  }

  /** {{{
    *  Theorems       ::= Ident<theorem> {nl} Theorem {nl} { Theorem {nl} }
    *
    *  Theorem        ::= Ident `:' {nl} Expr {nl} Proof
    *  }}}
    */
  private def theorems(): AST.LClause.Theorems = {
    def theorem(): AST.LClause.Theorem = {
      val ident = id(acceptToken[Ident])
      accept[Colon]
      newLinesOpt()
      val oldIn = in.asInstanceOf[LimitingTokenIterator]
      val proofIndex = findTokenPos(t => t.is[LeftBrace], _ => true)
      if (proofIndex < 0) {
        reporter.syntaxError(s"A proof { ... } is required for a theorem", at = token)
      } else {
        in = new LimitingTokenIterator(oldIn.i, proofIndex - 1)
        val e = sparser.translateExp(expr())
        in = new LimitingTokenIterator(proofIndex, oldIn.end)
        AST.LClause.Theorem(ident, e, SSome(proof()))
      }
    }

    next()
    newLinesOpt()
    var ts = List(theorem())
    newLinesOpt()
    while (token.is[Ident]) {
      ts ::= theorem()
      newLinesOpt()
    }
    AST.LClause.Theorems(isz(ts.reverse))
  }

  /** {{{
    *  Sequent       ::= [ Exprs ] {nl} ( Ident<|-> | Ident<⊢> ) {nl} Exprs {nl} [ Proof ]
    *
    *  Exprs         ::= Expr {`,' {nl} Expr}
    *  }}}
    */
  def sequent(
    sequentIndex: Int = findTokenPos(isSequentToken, t => t.isNot[LeftBrace]),
    noProof: Boolean = false
  ): AST.LClause.Sequent = {
    if (sequentIndex < 0) reporter.syntaxError(s"... ⊢ ... expected", at = token)
    val emptyPremises = tokenCurrOrAfterNl(isSequentToken(token))
    val oldIn = in.asInstanceOf[LimitingTokenIterator]
    val premises =
      if (emptyPremises) List()
      else {
        in = new LimitingTokenIterator(oldIn.i, sequentIndex - 1)
        exprs()
      }
    in = new LimitingTokenIterator(sequentIndex + 1, oldIn.end)
    newLinesOpt()
    val proofIndex = if (noProof) -1 else findTokenPos(t => t.is[LeftBrace], _ => true)
    if (proofIndex < 0) {
      val conclusions = exprs()
      newLinesOpt()
      AST.LClause.Sequent(isz(premises), isz(conclusions), SNone())
    } else {
      in = new LimitingTokenIterator(oldIn.i, proofIndex - 1)
      val conclusions = exprs()
      in = new LimitingTokenIterator(proofIndex, oldIn.end)
      AST.LClause.Sequent(isz(premises), isz(conclusions), SSome(proof()))
    }
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
    *  Proof          ::= `{' { {nl} ProofStep } {nl} `}' {nl}
    *
    *  ProofStep      ::= Int `.' Expr `.' `.' `.' Just
    *                   |  Int `.' SubProof
    *
    *  SubProof       ::= `{' {nl} AssumeStep { {nl} ProofStep } {nl} `}'
    *
    *  AssumeStep     ::= Int `.' {nl} Expr {nl} `.' `.' `.' Ident<assume>
    *                   |  Int `.' {nl} [ {nl} Idents {nl} ]
    *                   |  Int `.' {nl} [ {nl} Idents `:' Domain { {nl} Idents `:' Domain } {nl} ]
    *                   |  Int `.' {nl} [ {nl} Idents {nl} ] {nl} Expr {nl} `.' `.' `.' Ident<assume>
    *                   |  Int `.' {nl} [ {nl} Idents `:' Domain { {nl} Idents `:' Domain } {nl} ] {nl} Expr {nl} `.' `.' `.' Ident<assume>
    *
    *  Just           ::= [ JustOp ] Ident [ `(' {nl} Expr { {nl} `,' {nl} Expr } {nl} `)' ]
    *
    *  JustOp         ::= `∧' | `^' | `&' | `∨' | `|' | `⊻' | `|^' | `→' | `->'
    *                   | `¬` | `~' | `!' | `⊥' | `_' `|' `_' | `∀' | `∃'
    *  }}}
    */
  private def proof(): AST.LClause.Proof = {
    def just(kind: String, offset: Int): AST.Just = {
      val input = token.pos.input
      next()
      next()
      next()
      val startPoint = token.pos.start
      (0 until offset - 4).foreach(_ => next())
      val endPoint = token.pos.end
      next()
      if (token.is[LeftParen]) {
        var args = List[AST.Exp]()
        acceptToken[LeftParen]
        newLinesOpt()
        args ::= sparser.translateExp(expr())
        newLinesOpt()
        while (token.is[Comma]) {
          next()
          newLinesOpt()
          args ::= sparser.translateExp(expr())
          newLinesOpt()
        }
        val t = acceptToken[RightParen]
        AST.Just(kind, isz(args.reverse), sparser.attr(Position.Range(input, startPoint, t.pos.end)))
      } else {
        AST.Just(kind, ISZ(), sparser.attr(Position.Range(input, startPoint, endPoint)))
      }
    }

    def exprJustH(justPos: Int, justKind: String, justOffset: Int): (AST.Exp, AST.Just) = {
      in = new LimitingTokenIterator(in.asInstanceOf[LimitingTokenIterator].i, justPos - 1)
      val ex = expr()
      val e = try {
        sparser.translateExp(ex)
      } catch {
        case exc: ArrayIndexOutOfBoundsException =>
          println(ex.syntax)
          throw exc
      }
      in = new LimitingTokenIterator(justPos, parserTokens.length - 1)
      (e, just(justKind, justOffset))
    }

    def exprJust(stepNo: AST.Exp.LitZ): (AST.Exp, AST.Just) = {
      val (justPos, justKind, justOffset) = findJust()
      if (justPos == -1) reporter.syntaxError(s"No justification found for proof step ${stepNo.value}.", at = token)
      exprJustH(justPos, justKind, justOffset)
    }

    def assumeStep(): AST.AssumeProofStep = {
      def varFragments(): ISZ[AST.VarFragment] = {
        acceptToken[LeftBracket]
        newLinesOpt()
        val ids = idents().map(id)
        newLinesOpt()
        val r = if (token.is[Colon]) {
          next()
          var fresh = List(AST.VarFragment(isz(ids), SSome(domain())))
          while (tokenCurrOrAfterNl(token.is[Ident] && aheadNF(token.is[Comma] || token.is[Colon]))) {
            newLinesOpt()
            fresh ::= AST.VarFragment(isz(idents().map(id)), SSome(domain()))
          }
          isz(fresh.reverse)
        } else {
          ISZ(AST.VarFragment(isz(ids), SNone()))
        }
        newLinesOpt()
        acceptToken[RightBracket]
        newLinesOpt()
        r
      }

      val n = acceptInt
      accept[Dot]
      val (justPos, justKind, _) = findJust()

      newLinesOpt()

      if (justPos < 0) {
        AST.AssumeProofStep.ForallIntroAps(n, varFragments())
      } else if ("assume" != justKind)
        reporter.syntaxError(s"Expected assume but $justKind found", at = parserTokens(justPos))
      else {
        val oldIn = in.asInstanceOf[LimitingTokenIterator]
        in = new LimitingTokenIterator(oldIn.i, justPos - 1)
        val r =
          if (token.is[LeftBracket]) AST.AssumeProofStep.ExistsElimAps(n, varFragments(), sparser.translateExp(expr()))
          else AST.AssumeProofStep.Regular(n, sparser.translateExp(expr()))
        in = new LimitingTokenIterator(justPos, oldIn.end)
        acceptToken[Dot]
        acceptToken[Dot]
        acceptToken[Dot]
        acceptIdent("assume")
        r
      }
    }

    def subProof(step: AST.Exp.LitZ): AST.ProofStep.SubProof = {
      next()
      newLinesOpt()
      val as = assumeStep()
      var pss = List[AST.ProofStep]()
      while (tokenCurrOrAfterNl(token.isNot[RightBrace]) && token.isNot[EOF]) {
        newLinesOpt()
        pss ::= proofStep()
      }
      newLinesOpt()
      accept[RightBrace]
      AST.ProofStep.SubProof(step, as, isz(pss.reverse))
    }

    def proofStep(): AST.ProofStep = {
      val n = acceptInt
      accept[Dot]
      if (token.is[LeftBrace]) {
        subProof(n)
      } else {
        val (e, j) = exprJust(n)
        AST.ProofStep.Basic(n, e, j)
      }
    }

    accept[LeftBrace]
    var pss = List[AST.ProofStep]()
    while (tokenCurrOrAfterNl(token.isNot[RightBrace]) && token.isNot[EOF]) {
      newLinesOpt()
      pss ::= proofStep()
    }
    newLinesOpt()
    accept[RightBrace]
    newLinesOpt()
    AST.LClause.Proof(isz(pss.reverse))
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
    var formula: AST.LClause.Sequent = null
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
        if (i < 0) AST.LClause.Sequent(ISZ(), ISZ(sparser.translateExp(expr())), SNone())
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

  def sequentFile(fileUriOpt: SOption[SString]): SlangParser.Result = {
    accept[BOF]
    newLinesOpt()
    val r = sequent()
    accept[EOF]
    SlangParser.Result(input.text, hashSireum = false, SSome(AST.TopUnit.SequentUnit(fileUriOpt, r)))
  }
}
