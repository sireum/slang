/*
 Copyright (c) 2017-2023, Robby, Kansas State University
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

import org.sireum.message
import org.sireum.message.Reporter
import org.sireum.lang.{ast => AST}
import org.sireum.{B, ISZ, None, Option, Some, String, Z}

import scala.meta._
import scala.meta.internal.parsers.ScalametaParser
import scala.util.{Success, Try}

object SlangParser {

  val messageKind = "Slang Parser"

  val unops: Map[String, AST.Exp.UnaryOp.Type] = Map(
    String("!") -> AST.Exp.UnaryOp.Not,
    String("+") -> AST.Exp.UnaryOp.Plus,
    String("-") -> AST.Exp.UnaryOp.Minus,
    String("~") -> AST.Exp.UnaryOp.Complement,
    String("¬") -> AST.Exp.UnaryOp.Not
  )

  val builtinPrefix: Seq[String] = Seq("z", "r", "c", "string", "f32", "f64")

  val disallowedTypeIds: Seq[String] = Seq(
    "Contract",
    "Deduce",
    "Invariant",
    "In",
    "At",
    "Old",
    "Idx",
    "Res",
    "All",
    "Exists",
    "∀",
    "∃"
  )

  val disallowedVarIds: Seq[String] = Seq(
    "T",
    "F"
  )

  val disallowedMethodIds: Seq[String] = Seq(
    "assert",
    "assume",
    "print",
    "println",
    "eprint",
    "eprintln",
    "halt",
    "native",
    "apply",
    "unapply",
    "unapplySeq",
    "Contract",
    "Deduce",
    "Invariant",
    "In",
    "At",
    "Old",
    "Idx",
    "Res",
    "All",
    "Exists",
    "∀",
    "∃",
    "~>",
    "~",
    "==",
    "!=",
    "^"
  )

  val impInternalSym: Predef.String = "__>:"

  val simpInternalSym: Predef.String = "___>:"

  val disallowedMethodIdEndings: Seq[String] = Seq()

  val unaryMethods: Seq[String] = Seq("unary_!", "unary_+", "unary_-", "unary_~")

  val topLevelMethodsIds: Seq[String] = Seq("assert", "assume", "print", "println", "eprint", "eprintln", "halt")

  val infixSymbols: Map[Predef.String, Predef.String] = Map(
    simpInternalSym -> AST.Exp.BinaryOp.CondImply.value,
    impInternalSym -> AST.Exp.BinaryOp.Imply.value
  )

  val quantSymbols: Set[String] = Set(
    "∀",
    "All",
    "∃",
    "Exists"
  )

  def scalaDialect(isWorksheet: B): Dialect =
    if (isWorksheet) scala.meta.dialects.Scala213.withAllowToplevelTerms(true)
    else scala.meta.dialects.Scala213

  case class Result(text: Predef.String, hashSireum: Boolean, unitOpt: Option[AST.TopUnit])

  def detectSlang(fileUriOpt: Option[String], txt: String): (Boolean, Predef.String, Predef.String) = {
    var text = txt.value.replace("\r\n", " \n") // WORKAROUND: scalameta crlf issues
    def compactLine(k: Int): Predef.String = {
      val sb = new _root_.java.lang.StringBuilder
      var i = k
      while (i < text.length && text(i).isWhitespace) i += 1
      var found = false
      while (i < text.length && !found) {
        text(i) match {
          case '\n' => found = true
          case c => if (!c.isWhitespace) sb.append(c)
        }
        i += 1
      }
      sb.toString
    }
    var firstLine = compactLine(0)
    var hashSireum = false
    fileUriOpt match {
      case Some(fileUri) =>
        if ((fileUri.value.endsWith(".scala") || fileUri.value.endsWith(".sc")) && firstLine.contains("#Sireum")) {
          hashSireum = true
        } else if (fileUri.value.endsWith(".slang") || fileUri.value.endsWith(".logika")) {
          hashSireum = false
        } else if (fileUri.value.endsWith(".cmd") && firstLine.startsWith("::#!")) {
          var found = false
          var i = 4
          while (i + 4 < text.length && !found) {
            if (text(i) == ':' && text(i + 1) == ':' && text(i + 2) == '!' && text(i + 3) == '#') {
              found = true
              i = i + 4
              while (i < text.length && text(i).isWhitespace) i += 1
              firstLine = compactLine(i)
              if (firstLine.contains("#Sireum")) {
                hashSireum = true
                val cs = text.toCharArray
                for (j <- 0 until i if cs(j) != '\n') cs(j) = ' '
                text = new Predef.String(cs)
              }
            } else {
              i = i + 1
            }
          }
        } else if (fileUri.value.endsWith(".cmd") && firstLine.startsWith("::/*#!")) {
          var found = false
          var i = 4
          while (i + 6 < text.length && !found) {
            if (text(i) == ':' && text(i + 1) == ':' && text(i + 2) == '!' && text(i + 3) == '#' && text(i + 4) == '*' && text(i + 5) == '/') {
              found = true
              i = i + 6
              while (i < text.length && text(i).isWhitespace) i += 1
              firstLine = compactLine(i)
              if (firstLine.contains("#Sireum")) {
                hashSireum = true
                val cs = text.toCharArray
                for (j <- 0 until i if cs(j) != '\n') cs(j) = ' '
                text = new Predef.String(cs)
              }
            } else {
              i = i + 1
            }
          }
        }
      case _ => hashSireum = firstLine.contains("#Sireum")
    }
    (hashSireum, firstLine, text)
  }

  def apply(
    isWorksheet: B,
    isDiet: B,
    fileUriOpt: Option[String],
    txt: String,
    reporter: Reporter
  ): Result = {
    val (hashSireum, _, text) = detectSlang(fileUriOpt, txt)
    val (dialect, input) = scalaDialect(isWorksheet)(text)
    val r = new SlangParser(text, input, dialect, hashSireum, isWorksheet, isDiet, fileUriOpt, reporter)
      .parseTopUnit()
    r
  }

  def isDollar(t: Term): Boolean = t match {
    case t: Term.Name if t.value == "$" => true
    case _ => false
  }

  def hasSelfType(p: Self): Boolean = p.name.value != "" || p.decltpe.nonEmpty

  private[SlangParser] lazy val emptyAttr = AST.Attr(posOpt = None())
  private[SlangParser] lazy val emptyTypedAttr = AST.TypedAttr(posOpt = None(), typedOpt = None())
  private[SlangParser] lazy val emptyResolvedAttr =
    AST.ResolvedAttr(posOpt = None(), resOpt = None(), typedOpt = None())

  private[SlangParser] lazy val rDollarId = AST.Id("$", emptyAttr)
  private[SlangParser] lazy val rExp = AST.Exp.Ident(rDollarId, emptyResolvedAttr)
  private[SlangParser] lazy val rStmt = AST.Stmt.Expr(rExp, emptyTypedAttr)
  private[SlangParser] lazy val emptyContract = AST.MethodContract.Simple.empty
  private[SlangParser] lazy val emptyProofStep = AST.ProofAst.Step.SubProof(AST.ProofAst.StepId.Num(42, emptyAttr), ISZ())

}

import SlangParser._

class SlangParser(
  text: Predef.String,
  input: Input,
  dialect: Dialect,
  hashSireum: Boolean,
  isWorksheet: Boolean,
  isDiet: Boolean,
  fileUriOpt: Option[String],
  reporter: Reporter
) {
  var lPointOpt: scala.Option[Int] = scala.None

  val fileInfo = message.DocInfo(uriOpt = fileUriOpt, lineOffsets = {
    val util = new scala.meta.InputUtil(input)
    var line = 0
    import org.sireum._
    var r = ISZ[U32]()
    try while (true) {
      r = r :+ U32(util.lineToOffset(line))
      line = line + 1
    } catch { case _: Throwable => }
    r
  })

  def parseTopUnit(): Result = {
    try {
      val fileUri = fileUriOpt.getOrElse("").value
      if (fileUri.endsWith(".scala") || fileUri.endsWith(".sc") || fileUri.endsWith(".slang") || fileUri.endsWith(".cmd") || (fileUri == "" && hashSireum)) {
        if (hashSireum || fileUri.endsWith(".slang")) {
          val parser = new ScalametaParser(input)(dialect)
          translateSource(parser.parseSource())
        } else Result(text, hashSireum, None())
      } else if (fileUriOpt.isEmpty || fileUri.endsWith(".logika")) {
        if (input.text.contains("-----")) {
          SlangTruthTableParser.parse(fileUriOpt, input.text, reporter) match {
            case Some(tt) => Result(text, hashSireum, Some(tt))
            case _ => Result(text, hashSireum, None())
          }
        } else {
          val parser = new ScalametaParser(input)(dialect)
          val oldIn = parser.in
          parser.in = oldIn.fork
          parser.next()
          parser.newLinesOpt()
          parser.in = oldIn
          val source = parser.parseSource()
          translateSource(source)
        }
      } else Result(text, hashSireum, None())
    } catch {
      case e: ParseException =>
        error(e.pos, e.shortMessage)
        Result(text, hashSireum, None())
      case e: TokenizeException =>
        error(e.pos, e.shortMessage)
        Result(text, hashSireum, None())
      case e: Throwable =>
        e.printStackTrace()
        reporter.error(None(), messageKind, s"Parsing error: ${e.getMessage}.")
        Result(text, hashSireum, None())
    }
  }

  def posInfo(pos: Position): message.Position = {
    val startOffset = lPointOpt.getOrElse(0)
    //val range = Position.Range(input, startOffset + pos.start, startOffset + pos.end)
    //val beginLine = range.startLine + 1
    //val beginColumn = range.startColumn + 1
    //val endLine = range.endLine + 1
    //val endColumn = range.endColumn
    val offset = startOffset + pos.start //range.start
    val length = startOffset + pos.end - offset //range.end - range.start

    import org.sireum._
    import org.sireum.U64._

    val off = conversions.Z.toU64(offset) << u64"32"
    val len = conversions.Z.toU64(length)
    val offsetLength = off | len

    val r = message.PosInfo(info = fileInfo, offsetLength = offsetLength)
    //assert(r.offset == offset, s"Pos offset: ${r.offset} != $offset")
    //assert(r.length == length, s"Pos len: ${r.length} == $length")
    //assert(r.beginLine == beginLine, s"Pos begin line: ${r.beginLine} == $beginLine")
    //assert(r.beginColumn == beginColumn, s"Pos begin column: ${r.beginColumn} == $beginColumn")
    //assert(r.endLine == endLine, s"Pos end line: ${r.endLine} == $endLine")
    //assert(r.endColumn == endColumn, s"Pos end column: ${r.endColumn} == $endColumn")
    r
  }

  def posOpt(pos: Position): Option[message.Position] = if (pos == Position.None) None() else Some(posInfo(pos))

  def error(pos: Position, message: Predef.String): Unit =
    reporter.error(posOpt(pos), SlangParser.messageKind, message)

  def warn(pos: Position, message: Predef.String): Unit =
    reporter.warn(posOpt(pos), SlangParser.messageKind, message)

  val unitType = AST.Type.Named(AST.Name(ISZ(AST.Id("Unit", emptyAttr)), emptyAttr), ISZ(), emptyTypedAttr)

  def errorNotSlang(pos: Position, message: Predef.String): Unit =
    error(pos, message + " not in Slang.")

  def errorInSlang(pos: Position, message: Predef.String): Unit =
    error(pos, message + " in Slang.")

  def translateSource(source: Source): Result = {
    def topF(rest: List[Stat]): Result = {
      val shouldParse = fileUriOpt.forall(
        fileUri =>
          fileUri.value.endsWith(".logika") ||
            fileUri.value.endsWith(".slang") ||
            (hashSireum && (fileUri.value.endsWith(".scala") ||
              fileUri.value.endsWith(".sc") || fileUri.value.endsWith(".cmd")))
      )
      if (shouldParse) {
        var stmts = checkMemberStmts(ISZ(rest.map(translateStat(Enclosing.Top)): _*))
        Result(
          text,
          hashSireum,
          Some(
            AST.TopUnit.Program(
              fileUriOpt,
              AST.Name(ISZ(), emptyAttr),
              bodyCheck(stmts, ISZ())
            )
          )
        )
      } else
        Result(text, hashSireum, None())
    }

    source.stats match {
      case List(pkg: Pkg) =>
        val ref = pkg.ref
        val stats = pkg.stats
        val name = AST.Name(ref2IS(ref), attr(ref.pos))
        def process(): Result = {
          def packageF(rest: List[Stat]) =
            Result(
              text,
              hashSireum,
              Some(
                AST.TopUnit.Program(
                  fileUriOpt,
                  name,
                  bodyCheck(checkMemberStmts(ISZ(rest.map(translateStat(Enclosing.Package)): _*)), ISZ())
                )
              )
            )

          stats match {
            case q"import org.sireum._" :: _ => packageF(stats)
            case q"import org.sireum.logika._" :: _ => packageF(stats)
            case _ if ref.syntax == "org.sireum" => packageF(stats)
            case _ =>
              errorInSlang(ref.pos, "The first member of packages should be 'import org.sireum._'")
              Result(text, hashSireum, None())
          }
        }
        if (hashSireum || fileUriOpt.isEmpty || fileUriOpt.get.value.endsWith(".slang")) {
          process()
        } else Result(text, hashSireum, None())
      case q"import org.sireum._" :: _ => topF(source.stats)
      case q"import org.sireum.logika._" :: _ => topF(source.stats)
      case Nil =>
        Result(
          text,
          hashSireum,
          Some(AST.TopUnit.Program(fileUriOpt, AST.Name(ISZ(), emptyAttr), bodyCheck(ISZ(), ISZ())))
        )
      case stats =>
        if (hashSireum)
          errorInSlang(stats.head.pos, "The first statement should be either 'package <name>' or 'import org.sireum._'")
        Result(text, hashSireum, None())
    }
  }

  def translateStat(enclosing: Enclosing.Type)(stat: Stat): AST.Stmt = {
    val r: AST.Stmt = stat match {
      case stat: Import => translateImport(enclosing, stat)
      case stat: Defn.Val => translateVal(enclosing, stat)
      case stat: Defn.Var => translateVar(enclosing, stat)
      case stat: Decl.Def =>
        checkReservedId(true, stat.name.pos, stat.name.value)
        translateDef(enclosing, stat)
      case stat: Defn.Def =>
        checkReservedId(true, stat.name.pos, stat.name.value)
        translateDef(enclosing, stat)
      case stat: Defn.Object =>
        checkTypeId(stat.name.pos, stat.name.value)
        translateObject(enclosing, stat)
      case stat: Defn.Trait =>
        checkTypeId(stat.name.pos, stat.name.value)
        for (mod <- stat.mods.headOption) mod match {
          case mod"@sig" | mod"@msig" | mod"@ext" => return translateSig(enclosing, stat)
          case mod"@datatype" => return translateDatatype(enclosing, stat)
          case mod"@record" => return translateRecord(enclosing, stat)
          case _ =>
        }
        errorNotSlang(stat.pos, s"Statement '${syntax(stat)}' is")
        rStmt
      case stat: Defn.Class =>
        checkTypeId(stat.name.pos, stat.name.value)
        stat match {
          case stat @ q"@range(..$_) class $_" => return translateRangeType(enclosing, stat)
          case stat @ q"@bits(..$_) class $_" => return translateBitsType(enclosing, stat)
          case _ =>
        }
        for (mod <- stat.mods.headOption) mod match {
          case mod"@datatype" => return translateDatatype(enclosing, stat)
          case mod"@record" => return translateRecord(enclosing, stat)
          case _ =>
        }
        errorNotSlang(stat.pos, s"Statement '${syntax(stat)}' is")
        rStmt
      case stat: Defn.Type => translateTypeAlias(enclosing, stat)
      case stat: Term.Assign =>
        stat.lhs match {
          case q"$fun(...${argss: List[List[Term]]})" if argss.nonEmpty =>
            translateAssign(enclosing, fun, argss, stat.rhs, stat)
          case _ => translateAssign(enclosing, stat)
        }
      case stat: Term.Block => translateBlock(enclosing, stat, isAssignExp = false)
      case stat: Term.If => translateIfStmt(enclosing, stat, isAssignExp = false)
      case stat: Term.Match => translateMatch(enclosing, stat, isAssignExp = false)
      case stat: Term.While => translateWhile(enclosing, stat)
      case stat: Term.Do => translateDoWhile(enclosing, stat)
      case stat: Term.For => translateFor(enclosing, stat)
      case stat: Term.Return => translateReturn(enclosing, stat)
      case q"Spec(${_: Lit.String})" => translateSpecLabel(enclosing, stat)
      case q"Spec { ..$_ }" => translateSpecBlock(enclosing, stat)
      case q"Deduce(..$_)" => translateDeduce(enclosing, stat)
      case q"Contract(DataRefinement($_)(..$_)(..$_))" => translateDataRefinement(enclosing, stat)
      case q"Contract.Havoc(..$args)" => translateHavoc(enclosing, stat)
      case _: Term.Apply | _: Term.ApplyInfix =>
        val term = stat.asInstanceOf[Term]
        stmtCheck(enclosing, term, s"${syntax(stat)}")
        AST.Stmt.Expr(translateExp(term), typedAttr(stat.pos))
      case _ =>
        errorNotSlang(stat.pos, s"Statement '${stat.syntax}' is")
        rStmt
    }

    lazy val transformer: AST.Transformer[Unit] = new AST.Transformer(new AST.Transformer.PrePost[Unit] {
      override def string: String = ""
      val stopPreResult: AST.Transformer.PreResult[Unit, AST.Stmt] = AST.Transformer.PreResult((), false, None())

      override def preStmtExpr(ctx: Unit, o: AST.Stmt.Expr): AST.Transformer.PreResult[Unit, AST.Stmt] = {
        o.exp match {
          case AST.Exp.Invoke(expOpt, AST.Exp.Ident(AST.Id(id)), _, args) if topLevelMethodsIds.contains(id) =>
            expOpt.foreach(e => transformer.transformExp((), e))
            for (arg <- args) {
              transformer.transformExp((), arg)
            }
            stopPreResult
          case _ => super.preStmtExpr(ctx, o)
        }
      }

      override def preExpInvoke(ctx: Unit, o: AST.Exp.Invoke): AST.Transformer.PreResult[Unit, AST.Exp] = {
        if (topLevelMethodsIds.contains(o.ident.id.value)) {
          reporter.error(
            o.ident.attr.posOpt,
            SlangParser.messageKind,
            s"Method '${o.ident.id.value}' can only be called at the statement level."
          )
        }
        super.preExpInvoke(ctx, o)
      }

      override def preExpInvokeNamed(ctx: Unit, o: AST.Exp.InvokeNamed): AST.Transformer.PreResult[Unit, AST.Exp] = {
        if (topLevelMethodsIds.contains(o.ident.id.value)) {
          reporter.error(
            o.ident.attr.posOpt,
            SlangParser.messageKind,
            s"Method '${o.ident.id.value}' cannot be called using named arguments."
          )
        }
        super.preExpInvokeNamed(ctx, o)
      }

    })

    transformer.transformStmt((), r)

    r
  }

  def translateImport(enclosing: Enclosing.Type, stat: Import): AST.Stmt = {
    enclosing match {
      case Enclosing.Top | Enclosing.Package | Enclosing.Method =>
      case _ =>
        if (isWorksheet) errorInSlang(stat.pos, "Imports can only appear at the top-level or inside packages or methods")
        else errorInSlang(stat.pos, "Imports can only appear inside packages or methods")
    }
    stat.importers match {
      case Seq(Importer(ref: Term.Ref, Seq(Importee.Wildcard()))) =>
        AST.Stmt.Import(
          ISZ(AST.Stmt.Import.Importer(AST.Name(ref2IS(ref), attr(ref.pos)), Some(AST.Stmt.Import.WildcardSelector()))),
          attr(stat.pos)
        )
      case _ =>
        var importers = ISZ[AST.Stmt.Import.Importer]()
        for (importer <- stat.importers) {
          val ref = ref2IS(importer.ref)
          val name = AST.Name(ref, attr(importer.ref.pos))
          var sels = ISZ[AST.Stmt.Import.NamedSelector]()
          for (importee <- importer.importees) importee match {
            case importee"$finame => $tiname" =>
              sels +:= AST.Stmt.Import.NamedSelector(cid(finame), cid(tiname))
            case importee"${iname: Name.Indeterminate}" =>
              val id = cid(iname)
              sels +:= AST.Stmt.Import.NamedSelector(id, id)
            case _ => errorNotSlang(importee.pos, s"Importee '${importee.syntax}' from ${importer.ref.syntax} is")
          }
          importers +:= AST.Stmt.Import.Importer(name, Some(AST.Stmt.Import.MultiSelector(sels)))
        }
        AST.Stmt.Import(importers, attr(stat.pos))
    }
  }

  def translateVal(enclosing: Enclosing.Type, stat: Defn.Val): AST.Stmt = {
    var hasError = false
    val mods = stat.mods
    var hasSpec = false
    for (mod <- mods) mod match {
      case mod"@spec" =>
        if (hasSpec) {
          hasError = true
          error(mod.pos, "Redundant @spec.")
        }
        hasSpec = true
      case _ =>
        hasError = true
        error(mod.pos, "Only the @spec modifier is allowed for val declarations.")
    }
    enclosing match {
      case Enclosing.Top | Enclosing.Object | Enclosing.ExtObject | Enclosing.DatatypeClass | Enclosing.RecordClass |
          Enclosing.Method | Enclosing.Block =>
      case Enclosing.Sig | Enclosing.MSig | Enclosing.DatatypeTrait | Enclosing.RecordTrait if hasSpec =>
      case _ =>
        hasError = true
        if (isWorksheet)
          errorInSlang(
            stat.pos,
            "Val declarations can only appear at the top-level, inside objects, classes, methods, or code blocks"
          )
        else errorInSlang(stat.pos, "Val declarations can only appear inside objects, classes, methods, or code blocks")
    }
    val patsnel = stat.pats
    val tpeopt = stat.decltpe
    val expr = stat.rhs
    val isDollarExpr = isDollar(expr)
    val isLocal: B = enclosing match {
      case Enclosing.Top => true
      case Enclosing.Method => true
      case Enclosing.Block => true
      case _ => false
    }
    if (tpeopt.isEmpty && !(enclosing match {
        case Enclosing.Top | Enclosing.Method | Enclosing.Block => true
        case _ => false
      })) {
      hasError = true
      if (isWorksheet)
        errorInSlang(
          stat.pos,
          "Untyped val declarations can only appear at the top-level, inside methods, or code blocks"
        )
      else errorInSlang(stat.pos, "Untyped val declarations can only appear inside methods or code blocks")
    } else if (patsnel.size != 1) {
      hasError = true
      errorInSlang(stat.pos, "Cannot define multiple vals in a single statement")
    } else if (isDollarExpr && !((hasSpec && !isLocal) || enclosing == Enclosing.ExtObject)) {
      hasError = true
      error(stat.pos, "'$' is only allowed in a Slang @ext object or @spec val/var field expression.")
    } else if (hasSpec && !isLocal && (!isDollarExpr || !patsnel.head.isInstanceOf[Pat.Var] || tpeopt.isEmpty)) {
      hasError = true
      errorInSlang(stat.pos, "@spec val field declarations should have the form: '@spec val〈ID〉:〈type〉= $'")
    }
    if (hasError) rStmt
    else if (hasSpec && !isLocal)
      AST.Stmt.SpecVar(isVal = true, cid(patsnel.head.asInstanceOf[Pat.Var]), translateType(tpeopt.get), resolvedAttr(stat.pos))
    else
      patsnel.head match {
        case x: Pat.Var =>
          checkReservedId(true, x.name.pos, x.name.value)
          val r = AST.Stmt.Var(
            isSpec = hasSpec,
            isVal = true,
            cid(x),
            opt(tpeopt.map(translateType)),
            if (isDiet && tpeopt.nonEmpty) None()
            else if (isDollarExpr) None()
            else Some(translateAssignExp(expr)),
            resolvedAttr(stat.pos)
          )
          if (tpeopt.isEmpty) checkTyped(expr.pos, r.initOpt)
          r
        case pattern: Pat =>
          enclosing match {
            case Enclosing.Top | Enclosing.Method | Enclosing.Block =>
            case _ =>
              if (isWorksheet)
                errorInSlang(stat.pos, "Val pattern can only appear at the top-level, inside methods, or code blocks")
              else errorInSlang(pattern.pos, "Val pattern can only appear inside methods or code blocks")
          }
          val pat = translatePattern(pattern)
          pat match {
            case _: AST.Pattern.Structure =>
            case _ => error(pattern.pos, s"Unallowable val pattern: '${pattern.syntax}'")
          }
          val exp = translateAssignExp(expr)
          val r = AST.Stmt.VarPattern(isSpec = hasSpec, isVal = true, pat, opt(tpeopt.map(translateType)), exp, attr(stat.pos))
          if (tpeopt.isEmpty) checkTyped(expr.pos, Some(r.init))
          r
      }
  }

  def translateVar(enclosing: Enclosing.Type, stat: Defn.Var): AST.Stmt = {
    var hasError = false
    val mods = stat.mods
    var hasSpec = false
    for (mod <- mods) mod match {
      case mod"@spec" =>
        if (hasSpec) {
          hasError = true
          error(mod.pos, "Redundant @spec.")
        }
        hasSpec = true
      case _ =>
        hasError = true
        error(mod.pos, "Only the @spec modifier is allowed for var declarations.")
    }
    enclosing match {
      case Enclosing.Top | Enclosing.Object | Enclosing.ExtObject | Enclosing.RecordClass | Enclosing.Method |
          Enclosing.Block =>
      case Enclosing.Sig | Enclosing.MSig | Enclosing.DatatypeTrait | Enclosing.RecordTrait | Enclosing.DatatypeClass if hasSpec =>
      case _ =>
        hasError = true
        if (isWorksheet)
          errorInSlang(
            stat.pos,
            "Var declarations can only appear at the top-level, inside objects, classes, methods, or code blocks"
          )
        else
          errorInSlang(
            stat.pos,
            "Var declarations can only appear inside objects, @record classes, methods, or code blocks"
          )
    }
    val patsnel = stat.pats
    val tpeopt = stat.decltpe
    val expropt: scala.Option[Term] = if (stat.body.is[Term.Placeholder]) scala.None else scala.Some(stat.body)
    val isDollarExpr = expropt.exists(isDollar)
    val isLocal: B = enclosing match {
      case Enclosing.Top => true
      case Enclosing.Method => true
      case Enclosing.Block => true
      case _ => false
    }
    if (tpeopt.isEmpty && !(enclosing match {
        case Enclosing.Top | Enclosing.Method | Enclosing.Block => true
        case _ => false
      })) {
      hasError = true
      if (isWorksheet)
        errorInSlang(
          stat.pos,
          "Untyped var declarations can only appear at the top-level, inside methods, or code blocks"
        )
      else errorInSlang(stat.pos, "Untyped var declarations can only appear inside methods or code blocks")
    } else if (patsnel.size != 1) {
      hasError = true
      errorInSlang(stat.pos, "Cannot define multiple vars in a single statement")
    } else if (isDollarExpr && !((hasSpec && !isLocal) || enclosing == Enclosing.ExtObject)) {
      hasError = true
      error(stat.pos, "'$' is only allowed in a Slang @ext object or @spec val/var field expression.")
    } else if (hasSpec && !isLocal && (!isDollarExpr || !patsnel.head.isInstanceOf[Pat.Var] || tpeopt.isEmpty)) {
      hasError = true
      errorInSlang(stat.pos, "Non-local @spec val declarations should have the form: '@spec var〈ID〉:〈type〉= $'")
    } else if (expropt.isEmpty) {
      hasError = true
      errorInSlang(stat.pos, "Uninitialized '_' var declarations are disallowed")
    }
    if (hasError) rStmt
    else if (hasSpec && !isLocal)
      AST.Stmt
        .SpecVar(isVal = false, cid(patsnel.head.asInstanceOf[Pat.Var]), translateType(tpeopt.get), resolvedAttr(stat.pos))
    else
      patsnel.head match {
        case x: Pat.Var =>
          checkReservedId(true, x.name.pos, x.name.value)
          val r = AST.Stmt.Var(
            isSpec = hasSpec,
            isVal = false,
            cid(x),
            opt(tpeopt.map(translateType)),
            if (isDiet && tpeopt.nonEmpty) None()
            else if (isDollarExpr) None()
            else opt(expropt.map(translateAssignExp)),
            resolvedAttr(stat.pos)
          )
          if (tpeopt.isEmpty) checkTyped(expropt.get.pos, r.initOpt)
          r
        case pattern: Pat =>
          enclosing match {
            case Enclosing.Top | Enclosing.Method | Enclosing.Block =>
            case _ =>
              if (isWorksheet)
                errorInSlang(
                  pattern.pos,
                  "Var pattern can only appear at the top-level, inside methods, or code blocks"
                )
              else errorInSlang(pattern.pos, "Var pattern can only appear inside methods or code blocks")
          }
          if (tpeopt.nonEmpty)
            errorInSlang(pattern.pos, "Var pattern cannot be explicitly typed")
          if (expropt.isEmpty)
            errorInSlang(pattern.pos, "Var pattern has to be initialized")
          val pat = translatePattern(pattern)
          pat match {
            case _: AST.Pattern.Structure =>
            case _ => error(pattern.pos, s"Unallowable var pattern: '${pattern.syntax}'")
          }
          val exp = expropt.map(translateAssignExp).getOrElse(AST.Stmt.Expr(rExp, typedAttr(pattern.pos)))
          val r = AST.Stmt.VarPattern(isSpec = hasSpec, isVal = false, pat, opt(tpeopt.map(translateType)), exp, attr(stat.pos))
          if (tpeopt.isEmpty) checkTyped(expropt.get.pos, Some(r.init))
          r
      }
  }

  def checkTyped(pos: => Position, initOpt: Option[AST.AssignExp]): Unit = {
    var hasError = false

    def check(stmt: Any): Unit = stmt match {
      case AST.Stmt.Expr(_: AST.Exp.If) => hasError = true
      case AST.Stmt.Expr(_: AST.Exp.ForYield) => hasError = true
      case AST.Stmt.Expr(_) =>
      case _ => hasError = true
    }

    initOpt match {
      case Some(s) => check(s)
      case _ =>
    }

    if (hasError) errorInSlang(pos, "Complex initialization should be explicitly typed")
  }

  def translateDef(enclosing: Enclosing.Type, stat: Decl.Def): AST.Stmt = {
    enclosing match {
      case Enclosing.DatatypeTrait | Enclosing.RecordTrait | Enclosing.Sig | Enclosing.MSig =>
      case _ => errorInSlang(stat.pos, "Method declarations without a body can only appear inside traits")
    }
    val mods = stat.mods
    val name = stat.name
    val tparams = for (pcg <- stat.paramClauseGroups; tp <- pcg.tparamClause.values) yield tp
    val paramss = stat.paramClauses.map(_.values)
    val tpe = stat.decltpe
    var hasError = false
    if (paramss.size > 1) {
      hasError = true
      errorNotSlang(name.pos, "Methods with multiple parameter tuples are")
    }
    var isPure = false
    var isStrictPure = false
    var hasOverride = false
    var isHelper = false
    for (mod <- mods) mod match {
      case mod"@pure" =>
        if (isPure) {
          hasError = true
          error(mod.pos, "Redundant @pure.")
        }
        isPure = true
      case mod"@strictpure" =>
        if (isStrictPure) {
          hasError = true
          error(mod.pos, "Redundant @strictpure.")
        }
        isStrictPure = true
      case mod"@helper" =>
        if (isHelper) {
          hasError = true
          error(mod.pos, "Redundant @helper.")
        }
        isHelper = true
      case mod"override" =>
        if (hasOverride) {
          hasError = true
          error(mod.pos, "Redundant override.")
        }
        hasOverride = true
      case _ =>
        hasError = true
        errorInSlang(mod.pos, s"Only the @pure, @strictpure, @helper and/or override method modifiers are allowed for method declarations")
    }
    if (isPure && isStrictPure) {
      hasError = true
      errorInSlang(
        mods.head.pos,
        s"Methods cannot be annotated with both @strictpure and @pure (@strictpure methods are always @pure)"
      )
    }
    val (hasParams, params) = paramss.headOption match {
      case scala.Some(ps) => (true, ISZ[AST.Param](ps.map(translateParam(isMemoize = false)): _*))
      case _ => (false, ISZ[AST.Param]())
    }
    val purity = if (isStrictPure) AST.Purity.StrictPure else if (isPure) AST.Purity.Pure else AST.Purity.Impure
    val sig =
      AST.MethodSig(isPure || isStrictPure, cid(name), ISZ(tparams.map(translateTypeParam(AST.Typed.VarKind.Immutable, true)): _*), hasParams, params, translateType(tpe))
    AST.Stmt.Method(false, purity, hasOverride, isHelper, sig, emptyContract, None(), resolvedAttr(stat.pos))
  }

  val specDefn: Set[Predef.String] = Set(
    "Theorem", "Lemma", "Fact"
  )
  val specDefnInv: Set[Predef.String] = specDefn + "Invariant"

  def translateDef(enclosing: Enclosing.Type, tree: Defn.Def): AST.Stmt = {
    val tparams = for (pcg <- tree.paramClauseGroups; tp <- pcg.tparamClause.values) yield tp
    if (tree.paramClauses.size <= 1 && tree.mods.exists({
      case mod"@spec" => true
      case _ => false
    })) {
      tree.body match {
        case q"${id: Term.Name}(..$_)" if specDefnInv.contains(id.value) =>
          def checkReturnType(): Unit = {
            tree.decltpe match {
              case scala.Some(decltpe) => errorInSlang(decltpe.pos, s"${id.value}s cannot have an explicit return type")
              case _ =>
            }
          }
          id.value match {
            case "Fact" =>
              checkReturnType()
              return translateFact(enclosing, tree)
            case "Theorem" =>
              checkReturnType()
              return translateTheoremLemma(false, enclosing, tree)
            case "Lemma" =>
              checkReturnType()
              return translateTheoremLemma(true, enclosing, tree)
            case "Invariant" =>
              if (tparams.nonEmpty) {
                errorInSlang(id.pos, "Invariants cannot have type parameters")
              }
              if (tree.paramClauses.nonEmpty) {
                errorInSlang(id.pos, "Invariants cannot have parameters")
              }
              checkReturnType()
              return translateInvariant(enclosing, tree)
            case _ =>
          }
        case _ =>
      }
    }
    val mods = tree.mods
    val name = tree.name
    val paramss = tree.paramClauses.map(_.values)
    val tpeopt = tree.decltpe
    val exp = tree.body
    var hasError = false
    if (paramss.size > 1) {
      hasError = true
      errorNotSlang(name.pos, "Methods with multiple parameter tuples are")
    }
    if (tpeopt.isEmpty) {
      hasError = true
      errorInSlang(name.pos, "Methods have to be given an explicit return type")
    }
    var isPure = false
    var isSpec = false
    var isStrictPure = false
    var hasOverride = false
    var isMemoize = false
    var isHelper = false
    var isJust = false
    var etaOpt: Option[AST.Exp.LitString] = None()
    for (mod <- mods) mod match {
      case mod"@pure" =>
        if (isPure) {
          hasError = true
          error(mod.pos, "Redundant @pure.")
        }
        isPure = true
      case mod"@strictpure" =>
        if (isStrictPure) {
          hasError = true
          error(mod.pos, "Redundant @strictpure.")
        }
        isStrictPure = true
      case mod"@helper" =>
        if (isHelper) {
          hasError = true
          error(mod.pos, "Redundant @helper.")
        }
        isHelper = true
      case mod"@spec" =>
        if (isSpec) {
          hasError = true
          error(mod.pos, "Redundant @spec.")
        }
        isSpec = true
      case mod"@memoize" =>
        if (isMemoize) {
          hasError = true
          error(mod.pos, "Redundant @memoize.")
        }
        isMemoize = true
      case mod"override" =>
        if (hasOverride) {
          hasError = true
          error(mod.pos, "Redundant override.")
        }
        hasOverride = true
      case mod"@just(name = ${term: Term})" =>
        if (isJust) {
          hasError = true
          error(mod.pos, "Redundant @just.")
        }
        term match {
          case term: Lit.String => etaOpt = Some(translateLit(term).asInstanceOf[AST.Exp.LitString])
          case _ => errorInSlang(term.pos, s"Only string literal is allowed as @just argument")
        }
        isJust = true
      case mod"@just(..$terms)" =>
        if (isJust) {
          hasError = true
          error(mod.pos, "Redundant @just.")
        }
        if (terms.size > 1) {
          hasError = true
          error(mod.pos, "@just only accepts a single string literal argument")
        } else if (terms.size == 1) {
          val term = terms.head
          term match {
            case q"name = ${lit: Lit.String}" => etaOpt = Some(translateLit(lit).asInstanceOf[AST.Exp.LitString])
            case term: Lit.String => etaOpt = Some(translateLit(term).asInstanceOf[AST.Exp.LitString])
            case _ => errorInSlang(term.pos, s"Only string literal is allowed as @just argument")
          }
        }
        isJust = true
      case mod"@just" =>
        if (isJust) {
          hasError = true
          error(mod.pos, "Redundant @just.")
        }
        etaOpt = None()
        isJust = true
      case _ =>
        hasError = true
        errorInSlang(
          mod.pos,
          s"Only either method modifier @pure, @strictpure, @memoize, @helper, @spec, @just, and/or override is allowed for method definitions"
        )
    }
    if (isJust && enclosing != Enclosing.Object) {
      hasError = true
      errorInSlang(
        mods.head.pos,
        s"Methods annotated with @just can only appear inside an object"
      )
    }
    if (isPure && isMemoize) {
      hasError = true
      errorInSlang(
        mods.head.pos,
        s"Methods cannot be annotated with both @memoize and @pure (@memoize methods are always @pure)"
      )
    }
    if (isPure && isStrictPure) {
      hasError = true
      errorInSlang(
        mods.head.pos,
        s"Methods cannot be annotated with both @strictpure and @pure (@strictpure methods are always @pure)"
      )
    }
    if (isMemoize && isStrictPure) {
      hasError = true
      errorInSlang(
        mods.head.pos,
        s"Methods cannot be annotated with both @strictpure and @memoize"
      )
    }
    if (isPure && isSpec) {
      hasError = true
      errorInSlang(
        mods.head.pos,
        s"Methods cannot be annotated with both @pure and @spec (@spec methods are always @pure)"
      )
    }
    if (isStrictPure && isSpec) {
      hasError = true
      errorInSlang(
        mods.head.pos,
        s"Methods cannot be annotated with both @strictpure and @spec (@spec methods are always @strictpure)"
      )
    }
    if (isMemoize && isSpec) {
      hasError = true
      errorInSlang(mods.head.pos, s"Methods cannot be annotated with both @memoize and @spec")
    }
    if (hasOverride && isSpec) {
      hasError = true
      errorInSlang(mods.head.pos, s"@spec methods cannot have an override modifier")
    }
    if (hasOverride && isMemoize) {
      hasError = true
      errorInSlang(mods.head.pos, s"@memoize methods cannot have an override modifier")
    }
    if (isSpec && isHelper) {
      hasError = true
      errorInSlang(mods.head.pos, s"@spec methods cannot have a @helper modifier")
    }
    val purity =
      if (isMemoize) AST.Purity.Memoize
      else if (isPure) AST.Purity.Pure
      else if (isStrictPure) AST.Purity.StrictPure
      else AST.Purity.Impure
    val (hasParams, params) = paramss.headOption match {
      case scala.Some(ps) => (true, ISZ[AST.Param](ps.map(translateParam(isMemoize)): _*))
      case _ => (false, ISZ[AST.Param]())
    }
    val sig = AST.MethodSig(
      isMemoize || isPure || isStrictPure || isSpec,
      cid(name),
      ISZ(tparams.map(translateTypeParam(AST.Typed.VarKind.Immutable, true)): _*),
      hasParams,
      params,
      tpeopt.map(translateType).getOrElse(unitType)
    )

    def body(): AST.Stmt.Method = {
      def err(): AST.Stmt.Method = {
        if (isStrictPure) {
          errorInSlang(exp.pos, "Ill-formed @strictpure method body")
        } else {
          errorInSlang(exp.pos, "Only block '{ ... }' is allowed for a method body")
        }
        AST.Stmt.Method(false, purity, hasOverride, isHelper, sig, emptyContract, None(), resolvedAttr(tree.pos))
      }

      exp match {
        case exp: Term.Block if !isStrictPure =>
          val (mc, bodyOpt) = exp.stats.headOption match {
            case scala.Some(c@q"Contract(..$exprs)") =>
              (
                translateMethodContract(exprs, attr(c.pos)),
                if (isDiet) None[AST.Body]()
                else Some(bodyCheck(ISZ(exp.stats.tail.map(translateStat(Enclosing.Method)): _*), ISZ()))
              )
            case _ =>
              (
                emptyContract,
                if (isDiet) None[AST.Body]()
                else Some(bodyCheck(ISZ(exp.stats.map(translateStat(Enclosing.Method)): _*), ISZ()))
              )
          }
          AST.Stmt.Method(false, purity, hasOverride, isHelper, sig, mc, bodyOpt, resolvedAttr(tree.pos))
        case q"Contract.Only(..$exprs)" =>
          enclosing match {
            case Enclosing.Sig | Enclosing.MSig | Enclosing.DatatypeTrait | Enclosing.RecordTrait =>
              if (isMemoize) {
                errorInSlang(
                  exp.pos,
                  "@memoize can only be used for @datatype/@record classes."
                )
              }
              AST.Stmt.Method(false, purity, hasOverride, isHelper, sig, translateMethodContract(exprs, attr(exp.pos)),
                None(), resolvedAttr(tree.pos))
            case _ => err()
          }
        case _ =>
          if (isStrictPure && !hasError) {
            val (mc, newExp) = exp match {
              case exp: Term.Block =>
                exp.stats.headOption match {
                  case scala.Some(c@q"Contract(..$exprs)") =>
                    (translateMethodContract(exprs, attr(c.pos)), exp.copy(stats = exp.stats.tail))
                  case _ => (emptyContract, exp)
                }
              case _ => (emptyContract, exp)
            }
            val expAttr = attr(exp.pos)
            var stmt1 = translateStat(Enclosing.Block)(q"val _r_ : ${tpeopt.get} = $newExp").asInstanceOf[AST.Stmt.Var]
            val init = stmt1.initOpt.get match {
              case in: AST.Stmt.Block => in(attr = expAttr)
              case in => in
            }
            stmt1 = stmt1(id = stmt1.id(attr = expAttr), initOpt = Some(init), attr = stmt1.attr(posOpt = expAttr.posOpt))
            var stmt2 = translateStat(Enclosing.Block)(q"return _r_").asInstanceOf[AST.Stmt.Return]
            val ident = stmt2.expOpt.get.asInstanceOf[AST.Exp.Ident]
            stmt2 = stmt2(expOpt = Some(ident(id = stmt1.id, attr = ident.attr(posOpt = expAttr.posOpt))),
              attr = stmt2.attr(posOpt = expAttr.posOpt))

            AST.Stmt.Method(false, purity, hasOverride, isHelper, sig, mc,
              Some(AST.Body(ISZ(stmt1, stmt2), ISZ())), resolvedAttr(tree.pos))
          } else err()
      }
    }

    if (isJust) {
      if (checkSymbol(sig.id.value.value)) {
        error(name.pos, s"@just methods cannot be named with an identifier starting with a symbol")
      }
      if (isPure || isSpec || isStrictPure || hasOverride || isMemoize || isHelper) {
        error(name.pos, s"@just methods cannot have other annotations")
      }
      exp match {
        case exp: Term.Name if exp.value == "$" =>
        case _ =>
          hasError = true
          error(exp.pos, "Only '$' is allowed as Slang @just method expression.")
      }
      AST.Stmt.JustMethod(etaOpt, sig, resolvedAttr(tree.pos))
    } else if (isSpec) {
      if (checkSymbol(sig.id.value.value)) {
        error(name.pos, s"@spec methods cannot be named with an identifier starting with a symbol")
      }
      exp match {
        case exp: Term.Name if exp.value == "$" =>
          AST.Stmt.SpecMethod(sig, resolvedAttr(tree.pos))
        case _ =>
          hasError = true
          error(exp.pos, "Only '$' is allowed as Slang @spec method expression.")
          AST.Stmt.SpecMethod(sig, resolvedAttr(tree.pos))
      }
    } else if (enclosing == Enclosing.ExtObject) {
      if (checkSymbol(sig.id.value.value)) {
        error(name.pos, s"Extension methods cannot be named with an identifier starting with a symbol")
      }
      if (isHelper)
        errorInSlang(exp.pos, s"Extension methods cannot have a @helper modifier")
      if (hasOverride)
        errorInSlang(exp.pos, s"Extension methods cannot have an override modifier")
      if (isMemoize)
        errorInSlang(exp.pos, s"Extension methods cannot have a @memoize modifier")
      exp match {
        case exp: Term.Name if exp.value == "$" =>
          AST.Stmt.ExtMethod(isPure, sig, emptyContract, resolvedAttr(tree.pos))
        case q"Contract.Only(..$exprs)" =>
          AST.Stmt.ExtMethod(isPure, sig, translateMethodContract(exprs, attr(exp.pos)), resolvedAttr(tree.pos))
        case _ =>
          hasError = true
          error(exp.pos, "Only '$' or 'Contract.Only(...)' are allowed as Slang extension method expression.")
          AST.Stmt.ExtMethod(isPure, sig, emptyContract, resolvedAttr(tree.pos))
      }
    } else body()
  }

  def extractStaticZ(exp: Term): Z = {
    def extractString(s: Predef.String): Z = {
      Try(Z.$String(s)) match {
        case Success(n) => n
        case _ => error(exp.pos, s"Invalid Slang Z literal '$s'"); 0
      }
    }

    exp match {
      case Lit.Int(n) => n
      case Lit.Long(n) => n
      case q"Z(${n: Lit.Int})" => n.value
      case q"Z(${n: Lit.Long})" => n.value
      case q"Z(${n: Lit.String})" => extractString(n.value)
      case q"StringContext(${s: Lit.String}).z()" => extractString(s.value)
      case exp: Term.Interpolate if exp.prefix.value == "z" && exp.args.isEmpty && exp.parts.size == 1 =>
        exp.parts.head match {
          case Lit.String(s) => extractString(s)
          case _ => error(exp.pos, s"Invalid Slang Z literal '${syntax(exp)}'"); 0
        }
      case _ => error(exp.pos, s"Expecting Z literal, but '${syntax(exp)}' found."); 0
    }
  }

  def extractStaticB(exp: Term): B = exp match {
    case Lit.Boolean(b) => b
    case Term.Name("T") => true
    case Term.Name("F") => false
    case _ => error(exp.pos, s"Expecting B literal, but '${syntax(exp)}' found."); false
  }

  def translateRangeType(enclosing: Enclosing.Type, stat: Defn.Class): AST.Stmt = {
    enclosing match {
      case Enclosing.Top | Enclosing.Package | Enclosing.Object =>
      case _ =>
        if (isWorksheet)
          errorInSlang(
            stat.pos,
            "@range class declarations can only appear at the top-level, package-level, or inside objects"
          )
        else errorInSlang(stat.pos, "@range class declarations can only appear at package-level or inside objects")
    }
    val q"@range(..${modparams: List[Term]}) class $tname" = stat
    var hasMin = false
    var hasMax = false
    var index = false
    var min: Z = 0
    var max: Z = 0
    for (p <- modparams) p match {
      case q"min = ${exp: Term}" =>
        hasMin = true
        min = extractStaticZ(exp)
      case q"max = ${exp: Term}" =>
        hasMax = true
        max = extractStaticZ(exp)
      case q"index = ${exp: Term}" =>
        index = extractStaticB(exp)
      case t => error(t.pos, s"Invalid Slang @range argument '${syntax(t)}'.")
    }

    val isSigned = !hasMin || min < 0
    val isBitVector = false
    val bitWidth = 0
    val isWrapped = false

    if (index && !hasMin) error(stat.pos, "Slang @range index requires a min.")
    if (hasMin && hasMax && min > max)
      error(stat.pos, s"Slang @range min ($min) should not be greater than its max ($max).")

    AST.Stmt.SubZ(
      cid(tname),
      isSigned,
      isBitVector,
      isWrapped,
      hasMin,
      hasMax,
      bitWidth,
      min,
      max,
      index,
      if (index) min else 0,
      attr(stat.pos)
    )
  }

  def translateBitsType(enclosing: Enclosing.Type, stat: Defn.Class): AST.Stmt = {
    enclosing match {
      case Enclosing.Top | Enclosing.Package | Enclosing.Object =>
      case _ =>
        if (isWorksheet)
          errorInSlang(
            stat.pos,
            "@range class declarations can only appear at the top-level, package-level, or inside objects"
          )
        else errorInSlang(stat.pos, "@range class declarations can only appear at package-level or inside objects")
    }
    val q"@bits(..${modparams: List[Term]}) class $tname" = stat

    var signedOpt: Option[B] = None()
    var min: Z = 0
    var max: Z = 0
    var index = false
    var hasMin = false
    var hasMax = false
    var width = 0
    for (p <- modparams) p match {
      case q"signed = ${exp: Term}" =>
        signedOpt = Some(extractStaticB(exp))
      case t @ q"width = ${exp: Term}" =>
        width = extractStaticZ(exp) match {
          case Z.Int(8) => 8
          case Z.Int(16) => 16
          case Z.Int(32) => 32
          case Z.Int(64) => 64
          case _ =>
            error(
              t.pos,
              s"Invalid Slang @bits width argument '${syntax(exp)}' (only 8, 16, 32, or 64 are currently supported)."
            ); 64
        }
      case q"min = ${exp: Term}" =>
        min = extractStaticZ(exp)
        hasMin = true
      case q"max = ${exp: Term}" =>
        max = extractStaticZ(exp)
        hasMax = true
      case q"index = ${exp: Term}" =>
        index = extractStaticB(exp)
      case t => error(t.pos, s"Invalid Slang @bits argument '${syntax(t)}'.")
    }

    val isSigned: B = signedOpt match {
      case Some(x) => x
      case _ => if (hasMin) min < 0 else true
    }

    if (width == 0) {
      width = if (hasMin && hasMax) {
        if (isSigned) {
          if (Z(Byte.MinValue) <= min && max <= Z(Byte.MaxValue)) 8
          else if (Z(Short.MinValue) <= min && max <= Z(Short.MaxValue)) 16
          else if (Z(Int.MinValue) <= min && max <= Z(Int.MaxValue)) 32
          else if (Z(Long.MinValue) <= min && max <= Z(Long.MaxValue)) 64
          else {
            error(stat.pos, s"Invalid Slang @bits: min/max do not fit into signed integer 8, 16, 32, or 64 bits.")
            64
          }
        } else {
          if (min >= 0 && max <= Z(Byte.MaxValue) - Z(Byte.MinValue)) 8
          else if (min >= 0 && max <= Z(Short.MaxValue) - Z(Short.MinValue)) 16
          else if (min >= 0 && max <= Z(Int.MaxValue) - Z(Int.MinValue)) 32
          else if (min >= 0 && max <= Z(Long.MaxValue) - Z(Long.MinValue)) 64
          else {
            error(stat.pos, s"Invalid Slang @bits: min/max do not fit into unsigned integer 8, 16, 32, or 64 bits.")
            64
          }
        }
      } else {
        error(
          stat.pos,
          s"Slang @bits ${stat.name.value} requires either width or min/max to be specified."
        )
        64
      }
    }

    val signedString = if (isSigned) "signed" else "unsigned"

    val (wMin, wMax) =
      if (isSigned) (Z(BigInt(-2).pow(width - 1)), Z(BigInt(2).pow(width - 1) - 1))
      else (Z(0), Z(BigInt(2).pow(width) - 1))
    if (index && !hasMin) error(stat.pos, "Slang @bits index requires a min.")
    if (hasMin && hasMax && min > max)
      error(stat.pos, s"Slang @range min ($min) should not be greater than its max ($max).")
    if (hasMin && min < wMin)
      error(stat.pos, s"Slang @bits min ($min) should not be less than its $signedString bit-width minimum ($wMin).")
    if (hasMax && max > wMax)
      error(stat.pos, s"Slang @bits max ($max) should not be greater than its $signedString bit-width maximum ($wMax).")
    if (!hasMin) min = wMin
    if (!hasMax) max = wMax

    val isWrapped = min == wMin && max == wMax
    val isBitVector = true

    AST.Stmt.SubZ(
      cid(tname),
      isSigned,
      isBitVector,
      isWrapped,
      hasMin,
      hasMax,
      width,
      min,
      max,
      index,
      if (index) min else 0,
      attr(stat.pos)
    )
  }

  def translateObject(enclosing: Enclosing.Type, stat: Defn.Object): AST.Stmt = {
    var hasError = false
    enclosing match {
      case Enclosing.Top | Enclosing.Package | Enclosing.Object =>
      case _ =>
        hasError = true
        if (isWorksheet)
          errorInSlang(
            stat.pos,
            "Object declarations can only appear at the top-level, package-level, or inside other objects"
          )
        else errorInSlang(stat.pos, "Object declarations can only appear at the package-level or inside other objects")
    }
    val mods = stat.mods
    val name = stat.name
    val estats = stat.templ.early
    val ctorcalls = stat.templ.inits
    val stats = stat.templ.stats
    val self = stat.templ.self
    var extNameOpt: Option[String] = None()
    var hasEnum = false
    var hasApp = false
    for (mod <- mods) mod match {
      case mod"@ext(name = ${arg: Lit.String})" =>
        if (extNameOpt.nonEmpty) {
          hasError = true
          error(mods.head.pos, "Redundant @ext.")
        }
        extNameOpt = Some(arg.value)
      case mod"@ext(${arg: Lit.String})" =>
        if (extNameOpt.nonEmpty) {
          hasError = true
          error(mods.head.pos, "Redundant @ext.")
        }
        extNameOpt = Some(arg.value)
      case mod"@ext" =>
        if (extNameOpt.nonEmpty) {
          hasError = true
          error(mods.head.pos, "Redundant @ext.")
        }
        extNameOpt = Some("")
      case mod"@enum" =>
        if (hasEnum) {
          hasError = true
          error(mod.pos, "Redundant @enum.")
        }
        hasEnum = true
      case _ =>
        hasError = true
        errorNotSlang(mods.head.pos, "Object modifiers other than @ext are")
    }
    ctorcalls match {
      case List(init"App") => hasApp = true
      case List() =>
      case _ =>
        error(name.pos, "Slang @ext objects have to be of the form '@ext object〈ID〉[ extends App ] { ... }'.")
    }
    if (!((extNameOpt.nonEmpty.value, hasEnum, hasApp) match {
        case (true, false, false) => true
        case (false, true, false) => true
        case (false, false, true) => true
        case (false, false, false) => true
        case _ => false
      })) {
      error(name.pos, "Slang @ext, @enum, or App extension cannot be used together.")
    }
    if (estats.nonEmpty) {
      hasError = true
      if (extNameOpt.nonEmpty)
        error(name.pos, "Slang @ext objects have to be of the form '@ext[(...)] object〈ID〉[ extends App ] { ... }'.")
      else error(name.pos, "Slang objects have to be of the form 'object〈ID〉〉[ extends App ] { ... }'.")
    } else if (hasSelfType(self)) {
      hasError = true
      errorNotSlang(self.pos, s"Self type: ${syntax(self)} is")
    }
    if (hasEnum) {
      val elements: Seq[AST.Id] = (for (stat <- stats)
        yield
          stat match {
            case Lit.Symbol(symbol) => scala.Some(cid(symbol.name, stat.pos))
            case Lit.String(value) => scala.Some(cid(value, stat.pos))
            case _ =>
              error(stat.pos, s"An @enum element should be a String or a single quote immediately followed by〈ID〉(i.e., a symbol).")
              scala.None
          }).flatten

      AST.Stmt.Enum(cid(name), ISZ(elements: _*), attr(stat.pos))
    } else if (!hasError) {
      val tstat = if (extNameOpt.nonEmpty) translateStat(Enclosing.ExtObject) _ else translateStat(Enclosing.Object) _
      val oattr = attr(stat.pos)
      AST.Stmt.Object(hasApp, extNameOpt, cid(name), checkMemberStmts(ISZ(stats.map(tstat): _*)), oattr)
    } else AST.Stmt.Object(hasApp, extNameOpt, cid(name), ISZ(), attr(stat.pos))
  }

  def translateSig(enclosing: Enclosing.Type, stat: Defn.Trait): AST.Stmt = {
    val mods = stat.mods
    val tname = stat.name
    val tparams = stat.tparamClause.values
    val estats = stat.templ.early
    val ctorcalls = stat.templ.inits
    val self = stat.templ.self
    val stats = stat.templ.stats

    if (hasSelfType(self)) {
      errorNotSlang(tname.pos, s"Self type: ${syntax(self)} is")
    }

    var hasSig = false
    var hasMSig = false
    var hasSealed = false
    var hasExt = false
    var m = ""
    for (mod <- mods) mod match {
      case mod"@sig" =>
        if (hasSig) {
          error(mod.pos, "Redundant '@sig'.")
        }
        hasSig = true
        m = "@sig"
      case mod"@msig" =>
        if (hasSig) {
          error(mod.pos, "Redundant '@msig'.")
        }
        hasMSig = true
        m = "@msig"
      case mod"@ext" =>
        if (hasExt) {
          error(mod.pos, "Redundant '@ext'.")
        }
        hasExt = true
        m = "@ext"
      case mod"sealed" =>
        if (hasSealed) {
          error(mod.pos, "Redundant 'sealed'.")
        }
        hasSealed = true
      case _ =>
        if (hasSig) error(mod.pos, "Only the 'sealed' modifier is allowed for Slang @sig traits.")
        else if (hasMSig) error(mod.pos, "Only the 'sealed' modifier is allowed for Slang @msig traits.")
        else error(mod.pos, "No modifier is allowed for Slang @ext traits.")
    }

    if (hasSig && hasMSig) error(stat.pos, "Slang @sig and @msig cannot be used together.")

    if (hasExt && (hasSig || hasMSig || hasSealed))
      error(stat.pos, "Slang @ext cannot be used together with other modifiers.")

    enclosing match {
      case Enclosing.Top | Enclosing.Package | Enclosing.Object =>
      case _ =>
        if (isWorksheet)
          errorInSlang(
            stat.pos,
            s"$m trait declarations can only appear at the top-level, package-level, or inside objects"
          )
        else errorInSlang(stat.pos, s"$m trait declarations can only appear at the package-level or inside objects")
    }
    if (estats.nonEmpty)
      error(tname.pos, s"Slang $m traits have to be of the form '$m trait〈ID〉... { ... }'.")

    AST.Stmt.Sig(
      hasSig,
      hasSealed,
      hasExt,
      cid(tname),
      ISZ(tparams.map(translateTypeParam(if (hasSig) AST.Typed.VarKind.Immutable else AST.Typed.VarKind.Mutable, !hasSig)): _*),
      ISZ(ctorcalls.map(translateExtend): _*),
      checkMemberStmts(ISZ(stats.map(translateStat(if (hasSig) Enclosing.Sig else Enclosing.MSig)): _*)),
      attr(stat.pos)
    )
  }

  def translateDatatype(enclosing: Enclosing.Type, stat: Defn.Trait): AST.Stmt = {
    enclosing match {
      case Enclosing.Top | Enclosing.Package | Enclosing.Object =>
      case _ =>
        if (isWorksheet)
          errorInSlang(
            stat.pos,
            "@datatype trait declarations can only appear at the top-level, package-level, or inside objects"
          )
        else
          errorInSlang(stat.pos, "@datatype trait declarations can only appear at the package-level or inside objects")
    }
    val mods = stat.mods
    val tname = stat.name
    val tparams = stat.tparamClause.values
    val estats = stat.templ.early
    val ctorcalls = stat.templ.inits
    val self = stat.templ.self
    val stats = stat.templ.stats
    if (estats.nonEmpty || hasSelfType(self))
      error(tname.pos, "Slang @datatype traits have to be of the form '@datatype trait〈ID〉... { ... }'.")
    var hasDatatype = false
    for (mod <- mods) mod match {
      case mod"@datatype" =>
        if (hasDatatype) {
          error(mod.pos, "Redundant @datatype.")
        }
        hasDatatype = true
      case _ =>
        error(mod.pos, "No modifiers are allowed for Slang @datatype traits.")
    }
    AST.Stmt.Adt(
      isRoot = true,
      isDatatype = true,
      cid(tname),
      ISZ(tparams.map(translateTypeParam(AST.Typed.VarKind.Immutable, false)): _*),
      ISZ(),
      ISZ(ctorcalls.map(translateExtend): _*),
      checkMemberStmts(ISZ(stats.map(translateStat(Enclosing.DatatypeTrait)): _*)),
      attr(stat.pos)
    )
  }

  def translateDatatype(enclosing: Enclosing.Type, stat: Defn.Class): AST.Stmt = {
    enclosing match {
      case Enclosing.Top | Enclosing.Package | Enclosing.Object =>
      case _ =>
        if (isWorksheet)
          errorInSlang(
            stat.pos,
            "@datatype class declarations can only appear at the top-level, package-level, or inside objects"
          )
        else errorInSlang(stat.pos, "@datatype class declarations can only appear at package-level or inside objects")
    }
    val mods = stat.mods
    val tname = stat.name
    val tparams = stat.tparamClause.values
    val ctorMods = stat.ctor.mods
    val paramss = stat.ctor.paramClauses.map(_.values)
    val estats = stat.templ.early
    val ctorcalls = stat.templ.inits
    val self = stat.templ.self
    val stats = stat.templ.stats
    if (ctorMods.nonEmpty || paramss.size > 1 || estats.nonEmpty || hasSelfType(self)) {
      error(tname.pos, "Slang @datatype classes have to be of the form '@datatype class〈ID〉... (...) ... { ... }'.")
    }
    var hasDatatype = false
    for (mod <- mods) mod match {
      case mod"@datatype" =>
        if (hasDatatype) {
          error(mod.pos, "Redundant @datatype.")
        }
        hasDatatype = true
      case _ =>
        error(mod.pos, "No modifiers are allowed for Slang @datatype classes.")
    }
    val params = ISZ(paramss.flatMap(_.map(translateAdtParam(isDatatype = true))): _*)
    AST.Stmt.Adt(
      isRoot = false,
      isDatatype = true,
      cid(tname),
      ISZ(tparams.map(translateTypeParam(AST.Typed.VarKind.Immutable, false)): _*),
      params,
      ISZ(ctorcalls.map(translateExtend): _*),
      checkMemberStmts(ISZ(stats.map(translateStat(Enclosing.DatatypeClass)): _*)),
      attr(stat.pos)
    )
  }

  def translateRecord(enclosing: Enclosing.Type, stat: Defn.Trait): AST.Stmt = {
    enclosing match {
      case Enclosing.Top | Enclosing.Package | Enclosing.Object =>
      case _ =>
        if (isWorksheet)
          errorInSlang(
            stat.pos,
            "@record trait declarations can only appear at the top-level, package-level, or inside objects"
          )
        else errorInSlang(stat.pos, "@rcord trait declarations can only appear at the package-level or inside objects")
    }
    val mods = stat.mods
    val tname = stat.name
    val tparams = stat.tparamClause.values
    val estats = stat.templ.early
    val ctorcalls = stat.templ.inits
    val self = stat.templ.self
    val stats = stat.templ.stats
    if (estats.nonEmpty || hasSelfType(self))
      error(tname.pos, "Slang @record traits have to be of the form '@record trait〈ID〉... { ... }'.")
    var hasRecord = false
    for (mod <- mods) mod match {
      case mod"@record" =>
        if (hasRecord) {
          error(mod.pos, "Redundant @record.")
        }
        hasRecord = true
      case _ =>
        error(mod.pos, "No modifiers are allowed for Slang @record traits.")
    }
    AST.Stmt.Adt(
      isRoot = true,
      isDatatype = false,
      cid(tname),
      ISZ(tparams.map(translateTypeParam(AST.Typed.VarKind.Mutable, true)): _*),
      ISZ(),
      ISZ(ctorcalls.map(translateExtend): _*),
      checkMemberStmts(ISZ(stats.map(translateStat(Enclosing.RecordTrait)): _*)),
      attr(stat.pos)
    )
  }

  def translateRecord(enclosing: Enclosing.Type, stat: Defn.Class): AST.Stmt = {
    enclosing match {
      case Enclosing.Top | Enclosing.Package | Enclosing.Object =>
      case _ =>
        if (isWorksheet)
          errorInSlang(
            stat.pos,
            "@record class declarations can only appear at the top-level, package-level, or inside objects"
          )
        else errorInSlang(stat.pos, "@record class declarations can only appear at the package-level or inside objects")
    }
    val mods = stat.mods
    val tname = stat.name
    val tparams = stat.tparamClause.values
    val ctorMods = stat.ctor.mods
    val paramss = stat.ctor.paramClauses.map(_.values)
    val estats = stat.templ.early
    val ctorcalls = stat.templ.inits
    val self = stat.templ.self
    val stats = stat.templ.stats
    if (ctorMods.nonEmpty || paramss.size > 1 || estats.nonEmpty || hasSelfType(self)) {
      error(tname.pos, "Slang @record classes have to be of the form '@record class〈ID〉(...) { ... }'.")
    }
    var hasRecord = false
    for (mod <- mods) mod match {
      case mod"@record" =>
        if (hasRecord) {
          error(mod.pos, "Redundant @record.")
        }
        hasRecord = true
      case _ =>
        error(mod.pos, "No modifiers are allowed for Slang @record classes.")
    }
    val params = ISZ(paramss.flatMap(_.map(translateAdtParam(isDatatype = false))): _*)
    AST.Stmt.Adt(
      isRoot = false,
      isDatatype = false,
      cid(tname),
      ISZ(tparams.map(translateTypeParam(AST.Typed.VarKind.Mutable, true)): _*),
      params,
      ISZ(ctorcalls.map(translateExtend): _*),
      checkMemberStmts(ISZ(stats.map(translateStat(Enclosing.RecordClass)): _*)),
      attr(stat.pos)
    )
  }

  def translateTypeAlias(enclosing: Enclosing.Type, stat: Defn.Type): AST.Stmt = {
    val mods = stat.mods
    val tname = stat.name
    val tparams = stat.tparamClause.values
    val tpe = stat.body
    if (mods.nonEmpty) {
      error(stat.pos, "Slang type definitions should be of the form: 'type〈ID〉... =〈type〉'.")
    }
    AST.Stmt.TypeAlias(cid(tname), ISZ(tparams.map(translateTypeParam(AST.Typed.VarKind.Immutable, true)): _*), translateType(tpe), attr(stat.pos))
  }

  def translateType(t: Type): AST.Type = {
    def f(t: Term): ISZ[AST.Id] = t match {
      case q"$expr.$name" => f(expr) :+ cid(name)
      case q"${name: Term.Name}" => ISZ(cid(name))
      case _ =>
        errorInSlang(t.pos, s"Invalid type reference '${t.syntax}'")
        ISZ(rDollarId)
    }

    t match {
      case t"$ref.$tname[..$tpesnel]" =>
        AST.Type
          .Named(AST.Name(f(ref) :+ cid(tname), attr(t.pos)), ISZ(tpesnel.map(translateType): _*), typedAttr(t.pos))
      case t"${name: Type.Name}[..$tpesnel]" =>
        AST.Type.Named(AST.Name(ISZ(cid(name)), attr(name.pos)), ISZ(tpesnel.map(translateType): _*), typedAttr(t.pos))
      case t"${name: Type.Name}" =>
        AST.Type.Named(AST.Name(ISZ(cid(name)), attr(name.pos)), ISZ(), typedAttr(t.pos))
      case t"(..$tpesnel)" =>
        AST.Type.Tuple(ISZ(tpesnel.map(translateType): _*), typedAttr(t.pos))
      case t"$ref.$tname" =>
        AST.Type.Named(AST.Name(f(ref) :+ cid(tname), attr(t.pos)), ISZ(), typedAttr(t.pos))
      case t: Type.Function =>
        val (isPure, ret) = t.res match {
          case res: Type.Annotate =>
            var hasPure = false
            for (a <- res.annots) {
              a match {
                case mod"@pure" => hasPure = true
                case _ => errorInSlang(a.pos, s"Only @pure is allowed as function type annotation")
              }
            }
            (hasPure, res.tpe)
          case _ => (false, t.res)
        }
        AST.Type.Fun(
          isPure,
          false,
          ISZ(t.paramClause.values.map(t => translateTypeArg(allowByName = false)(t)): _*),
          translateType(ret),
          typedAttr(t.pos)
        )
      case _ =>
        errorNotSlang(t.pos, s"Type '${syntax(t)}' is")
        unitType
    }
  }

  def translateTypeArg(allowByName: Boolean)(ta: Type): AST.Type = ta match {
    case _: Type.Repeated =>
      errorNotSlang(ta.pos, "Repeated types '〈type〉*' are")
      unitType
    case ta: Type.ByName =>
      if (allowByName) {
        val (isPure, tpe) = ta.tpe match {
          case Type.Annotate(t, anns) =>
            var hasPure = false
            for (a <- anns) {
              a match {
                case mod"@pure" => hasPure = true
                case _ => errorInSlang(a.pos, s"Only @pure is allowed as by-name type annotation")
              }
            }
            (hasPure, t)
          case _ => (false, ta.tpe)
        }
        AST.Type.Fun(isPure, true, ISZ(), translateType(tpe), typedAttr(ta.pos))
      } else {
        errorInSlang(ta.pos, "By name types '=> 〈type〉' are only allowed on (non-@memoize) method parameters")
        unitType
      }
    case _ => translateType(ta)
  }

  def translateAssignExp(exp: Term): AST.Stmt with AST.AssignExp = exp match {
    case exp: Term.Block => translateBlock(Enclosing.Block, exp, isAssignExp = true)
    case exp: Term.If if exp.thenp.isInstanceOf[Term.Block] =>
      translateIfStmt(Enclosing.Block, exp, isAssignExp = true)
    case exp: Term.Match => translateMatch(Enclosing.Block, exp, isAssignExp = true)
    case exp: Term.Return => translateReturn(Enclosing.Block, exp)
    case _ => AST.Stmt.Expr(translateExp(exp), typedAttr(exp.pos))
  }

  def translateStmtsExp(pos: Position, stats: Seq[Stat]): ISZ[AST.Stmt] = {
    stats.lastOption match {
      case scala.Some(exp: Term) =>
        ISZ(stats.dropRight(1).map(translateStat(Enclosing.Block)) :+ translateAssignExp(exp): _*)
      case scala.Some(stat) =>
        error(stat.pos, s"Expecting an expression but '${syntax(stat)}' found.")
        ISZ()
      case _ =>
        error(pos, s"Expecting an expression but none found.")
        ISZ()
    }
  }

  def translatePattern(pat: Pat): AST.Pattern = {
    pat match {
      case p"${pname: Pat.Var} @ $ref(..${apats: List[Pat]})" =>
        AST.Pattern.Structure(
          Some(cid(pname.name)),
          Some(AST.Name(ref2IS(ref), attr(ref.pos))),
          ISZ(apats.map(translatePattern): _*),
          ISZ(),
          resolvedAttr(pat.pos)
        )
      case p"${pname: Pat.Var} @ (..$patsnel)" =>
        AST.Pattern
          .Structure(Some(cid(pname.name)), None(), ISZ(patsnel.map(translatePattern): _*), ISZ(), resolvedAttr(pat.pos))
      case p"$ref(..${apats: List[Pat]})" =>
        AST.Pattern.Structure(
          None(),
          Some(AST.Name(ref2IS(ref), attr(ref.pos))),
          ISZ(apats.map(translatePattern): _*),
          ISZ(),
          resolvedAttr(pat.pos)
        )
      case p"(..$patsnel)" if patsnel.size > 1 =>
        AST.Pattern.Structure(None(), None(), ISZ(patsnel.map(translatePattern): _*), ISZ(), resolvedAttr(pat.pos))
      case p"${ref: Term.Ref}.$ename" =>
        AST.Pattern.Ref(true, AST.Name(ref2IS(ref) :+ cid(ename), attr(pat.pos)), resolvedAttr(pat.pos))
      case pat: Term.Name =>
        checkReservedId(false, pat.pos, pat.value)
        AST.Pattern.Ref(false, AST.Name(ISZ(cid(pat)), attr(pat.pos)), resolvedAttr(pat.pos))
      case p"${name: Pat.Var} : $tpe" =>
        checkReservedId(true, name.name.pos, name.name.value)
        AST.Pattern.VarBinding(cid(name), Some(translateType(tpe)), ISZ(), typedAttr(pat.pos))
      case q"${name: Pat.Var}" =>
        checkReservedId(true, name.name.pos, name.name.value)
        AST.Pattern.VarBinding(cid(name), None(), ISZ(), typedAttr(pat.pos))
      case p"_ : $tpe" => AST.Pattern.Wildcard(Some(translateType(tpe)), typedAttr(pat.pos))
      case p"_" => AST.Pattern.Wildcard(None(), typedAttr(pat.pos))
      case p"${lit: Pat.Interpolate}" => translateLit(lit)
      case p"${lit: Lit}" => AST.Pattern.Literal(translateLit(lit))
      case _: Pat.SeqWildcard => AST.Pattern.SeqWildcard(typedAttr(pat.pos))
      case _ =>
        errorInSlang(pat.pos, s"Invalid pattern: '${syntax(pat)}'")
        AST.Pattern.Wildcard(None(), typedAttr(pat.pos))
    }
  }

  def translatePattern(pat: Term): AST.Pattern = {
    pat match {
      case q"$expr(...${aexprssnel: List[List[Term]]})" =>
        if (aexprssnel.size == 1) {
          val args = aexprssnel.head
          expr match {
            case expr: Term.Ref =>
              return AST.Pattern.Structure(
                None(),
                Some(AST.Name(ref2IS(expr), attr(expr.pos))),
                ISZ(args.map(translatePattern): _*),
                ISZ(),
                resolvedAttr(pat.pos)
              )
            case _ =>
          }
        }
        errorInSlang(pat.pos, s"Invalid pattern: '${syntax(pat)}'")
        AST.Pattern.Wildcard(None(), typedAttr(pat.pos))
      case q"(..$exprsnel)" if exprsnel.size > 1 =>
        AST.Pattern.Structure(None(), None(), ISZ(exprsnel.map(translatePattern): _*), ISZ(), resolvedAttr(pat.pos))
      case q"${name: Term.Name}" => AST.Pattern.VarBinding(cid(name), None(), ISZ(), typedAttr(pat.pos))
      case p"${lit: Lit}" => AST.Pattern.Literal(translateLit(lit))
      case _ =>
        errorInSlang(pat.pos, s"Invalid pattern: '${syntax(pat)}'")
        AST.Pattern.Wildcard(None(), typedAttr(pat.pos))
    }
  }

  def translateTypeParam(defaultKind: AST.Typed.VarKind.Type, canBeMutable: Boolean)(tp: Type.Param): AST.TypeParam = tp match {
    case tparam"..$mods $_[..$tparams] >: ${stpeopt: scala.Option[Type]} <: ${tpeopt: scala.Option[Type]} <% ..$tpes : ..$tpes2" =>
      if (tparams.nonEmpty || stpeopt.nonEmpty || tpeopt.nonEmpty || tpes.nonEmpty || tpes2.nonEmpty)
        errorInSlang(tp.pos, "Only type parameters of the forms ' ⸨ @imm | @mut ⸩?〈ID〉' are")
      checkTypeId(tp.name.pos, tp.name.value)
      var hasImm = false
      var hasMut = false
      var hasIndex = false
      var kind = defaultKind
      for (mod <- mods) mod match {
        case mod"@index" =>
          if (hasIndex) {
            error(mod.pos, "Redundant @index.")
          }
          if (hasImm) {
            error(mod.pos, "Cannot have both @immut and @index")
          }
          if (hasMut) {
            error(mod.pos, "Cannot have both @mut and @index")
          }
          hasIndex = true
          kind = AST.Typed.VarKind.Index
        case mod"@imm" =>
          if (hasIndex) {
            error(mod.pos, "Cannot have both @index and @imm")
          }
          if (hasImm) {
            error(mod.pos, "Redundant @imm.")
          }
          if (hasMut) {
            error(mod.pos, "Cannot have both @mut and @imm")
          }
          hasImm = true
          kind = AST.Typed.VarKind.Immutable
        case mod"@mut" =>
          if (hasIndex) {
            error(mod.pos, "Cannot have both @index and @mut")
          }
          if (hasMut) {
            error(mod.pos, "Redundant @mut.")
          }
          if (hasImm) {
            error(mod.pos, "Cannot have both @imm and @mut")
          }
          if (!canBeMutable) {
            error(mod.pos, "Cannot have @mut")
          }
          hasMut = true
          kind = AST.Typed.VarKind.Mutable
        case _ =>
          error(mod.pos, s"Unallowed modifier '${syntax(mod)}' for a type parameter.")
      }
      AST.TypeParam(cid(tp.name), kind)
  }

  def translateParam(isMemoize: Boolean)(tp: Term.Param): AST.Param = {
    val mods = tp.mods
    val atpeopt = tp.decltpe
    val expropt = tp.default
    var hasHidden = false
    for (mod <- mods) mod match {
      case mod"@hidden" =>
        if (isMemoize) {
          if (hasHidden) {
            error(mod.pos, "Redundant @hidden.")
          }
          hasHidden = true
        } else errorInSlang(mod.pos, "@hidden is only allowed for @memoize methods")
      case _ =>
        error(mod.pos, s"Unallowed modifier '${syntax(mod)}' for a Slang method.")
    }
    if (atpeopt.isEmpty || expropt.nonEmpty) {
      val mod = if (hasHidden) "@hidden " else ""
      errorInSlang(tp.pos, s"The parameter should have the form '$mod〈ID〉:〈type〉'")
    }
    atpeopt
      .map(ta => AST.Param(hasHidden, cid(tp.name), translateTypeArg(!isMemoize)(ta)))
      .getOrElse(AST.Param(hasHidden, cid(tp.name), unitType))
  }

  def translateFunParam(tp: Term.Param): AST.Exp.Fun.Param = {
    val mods = tp.mods
    val atpeopt = tp.decltpe
    val expropt = tp.default
    for (mod <- mods) mod match {
      case _ =>
        error(mod.pos, s"Unallowed modifier '${syntax(mod)}' for a Slang fun expression.")
    }
    if (expropt.nonEmpty) {
      errorInSlang(tp.pos, s"The parameter should have the form '〈ID〉⸨ :〈type〉⸩?'")
    }
    atpeopt
      .map(ta => AST.Exp.Fun.Param(if ("_" == tp.name.value) None() else Some(cid(tp.name)), Some(translateTypeArg(allowByName = false)(ta)), None()))
      .getOrElse(AST.Exp.Fun.Param(if ("_" == tp.name.value) None() else Some(cid(tp.name)), None(), None()))
  }

  def translateBlock(enclosing: Enclosing.Type, stat: Term.Block, isAssignExp: Boolean): AST.Stmt.Block = {
    enclosing match {
      case Enclosing.Top | Enclosing.Object | Enclosing.DatatypeClass | Enclosing.RecordClass | Enclosing.Method |
          Enclosing.Block =>
        val stmts =
          if (isAssignExp) translateStmtsExp(stat.pos, stat.stats)
          else ISZ(stat.stats.map(translateStat(Enclosing.Block)): _*)
        AST.Stmt.Block(bodyCheck(stmts, ISZ()), attr(stat.pos))
      case _ =>
        if (isWorksheet)
          errorInSlang(
            stat.pos,
            "Code-blocks can only appear at the top-level, inside @datatype classes, @record classes, methods, or other code blocks"
          )
        else
          errorInSlang(
            stat.pos,
            "Code-blocks can only appear inside @datatype classes, @record classes, methods or other code blocks"
          )
        AST.Stmt.Block(bodyCheck(ISZ(), ISZ()), emptyAttr)
    }
  }

  def translateExtend(init: Init): AST.Type.Named = {
    if (init.argClauses.nonEmpty) errorInSlang(init.pos, "Cannot supply arguments for extends")
    translateType(init.tpe) match {
      case r: AST.Type.Named => r
      case _ =>
        errorInSlang(init.pos, "Invalid type for extends")
        AST.Type.Named(AST.Name(ISZ(), emptyAttr), ISZ(), emptyTypedAttr)
    }
  }

  def translateAdtParam(isDatatype: Boolean)(tp: Term.Param): AST.AdtParam = {
    val mods = tp.mods
    val paramname = tp.name
    val atpeopt = tp.decltpe
    val expropt = tp.default
    var hasError = false
    var hasHidden = false
    var isVar = false
    checkReservedId(true, tp.name.pos, tp.name.value)
    for (mod <- mods) mod match {
      case mod"@hidden" =>
        if (hasHidden) {
          hasError = true
          error(mod.pos, "Redundant @hidden.")
        }
        hasHidden = true
      case mod"varparam" if !isDatatype => isVar = true
      case mod"valparam" =>
      case _ =>
        hasError = true
        if (isDatatype) error(mod.pos, s"Unallowed modifier '${syntax(mod)}' for a Slang @datatype class.")
        else error(mod.pos, s"Unallowed modifier '${syntax(mod)}' for a Slang @record class.")
    }
    if (atpeopt.isEmpty || expropt.nonEmpty) {
      hasError = true
      val hidden = if (hasHidden) "@hidden " else ""
      errorInSlang(tp.pos, s"The abstract dataype parameter should have the form '$hidden〈ID〉:〈type〉'")
    }
    if (hasError) AST.AdtParam(hasHidden, !isVar, cid(paramname), unitType)
    else
      AST.AdtParam(hasHidden, !isVar, cid(paramname), translateTypeArg(allowByName = false)(atpeopt.get))
  }

  def bodyCheck(stmts: ISZ[AST.Stmt], undecls: ISZ[AST.ResolvedInfo.LocalVar]): AST.Body = {
    var i = 0
    for (stmt <- stmts) {
      val (ret, hlt) = stmt match {
        case _: AST.Stmt.Return => (true, false)
        case AST.Stmt.Expr(AST.Exp.Invoke(_, AST.Exp.Ident(AST.Id(id)), _, _)) if id.value == "halt" => (false, true)
        case _ => (false, false)
      }
      if ((ret || hlt) && Z(i) != stmts.size - 1) {
        val text = if (ret) "return" else "halt"
        for (j <- Z(i + 1) until stmts.size) {
          reporter.error(stmts(j).posOpt, SlangParser.messageKind, s"No statements are allowed after $text.")
        }
      }
      i = i + 1
    }
    return AST.Body(stmts, undecls)
  }

  def stmtCheck(enclosing: Enclosing.Type, stat: Term, kind: Predef.String): Boolean = enclosing match {
    case Enclosing.Top | Enclosing.Method | Enclosing.Block => false
    case _ =>
      if (isWorksheet) errorInSlang(stat.pos, s"$kind can only appear at the top-level, inside methods, or code blocks")
      else errorInSlang(stat.pos, s"$kind can only appear inside methods or code blocks")
      true
  }

  def checkLhs(lhs: AST.Exp): AST.Exp = {
    lhs match {
      case _: AST.Exp.Ident =>
      case lhs: AST.Exp.Select if lhs.targs.isEmpty && lhs.receiverOpt.nonEmpty =>
        lhs.receiverOpt.foreach(receiver => checkLhs(receiver))
      case lhs: AST.Exp.Invoke
          if lhs.ident.id.value.value == "apply" && lhs.receiverOpt.nonEmpty && lhs.targs.isEmpty && lhs.args.size == Z(
            1
          ) =>
        lhs.receiverOpt.foreach(receiver => checkLhs(receiver))
      case lhs: AST.Exp.Invoke if lhs.receiverOpt.isEmpty && lhs.targs.isEmpty && lhs.args.size == Z(1) =>
        return AST.Exp.Invoke(Some(lhs.ident), AST.Exp.Ident(AST.Id("apply", lhs.ident.id.attr), lhs.ident.attr), lhs.targs, lhs.args, lhs.attr)
      case _ =>
        reporter.error(lhs.posOpt, SlangParser.messageKind, s"Invalid assignment left-hand-side form in Slang: $lhs")
    }
    lhs
  }

  def translateAssign(enclosing: Enclosing.Type, stat: Term.Assign): AST.Stmt = {
    stmtCheck(enclosing, stat, "Assignments")
    val lhs = translateExp(stat.lhs)
    AST.Stmt.Assign(checkLhs(lhs), translateAssignExp(stat.rhs), attr(stat.pos))
  }

  def translateAssign(
    enclosing: Enclosing.Type,
    fun: Term,
    argss: List[List[Term]],
    rhs: Term,
    stat: Term
  ): AST.Stmt = {
    val pos = stat.pos

    stmtCheck(enclosing, stat, "Assignments")
    var lhs = translateExp(fun)
    val prevPos = fun.pos
    if (argss.nonEmpty) {
      for (args <- argss) {
        val pos =
          if (args.nonEmpty) Position.Range(args.head.pos.input, args.head.pos.start, args.last.pos.end)
          else prevPos
        lhs = translateApply(lhs, args, pos)
      }
    } else {
      errorInSlang(pos, s"Invalid update form: '${syntax(stat)}'")
    }
    AST.Stmt.Assign(checkLhs(lhs), translateAssignExp(rhs), attr(pos))
  }

  def translateIfStmt(enclosing: Enclosing.Type, stat: Term.If, isAssignExp: Boolean): AST.Stmt.If = {
    var hasError = stmtCheck(enclosing, stat, "If-statements")
    if (!stat.thenp.isInstanceOf[Term.Block]) {
      hasError = true
      errorInSlang(stat.thenp.pos, "If-then part should be a code block")
    }
    stat.elsep match {
      case _: Term.Block | _: Term.If | _: Lit.Unit =>
      case _ =>
        hasError = true
        errorInSlang(stat.elsep.pos, "If-else part should be either a code block or another if-conditional.")
    }
    val cond = translateExp(stat.cond)
    if (hasError) AST.Stmt.If(cond, bodyCheck(ISZ(), ISZ()), bodyCheck(ISZ(), ISZ()), emptyAttr)
    else
      ((stat.thenp, stat.elsep): @unchecked) match {
        case (thenp: Term.Block, elsep: Term.Block) =>
          AST.Stmt.If(
            cond,
            bodyCheck(
              if (isAssignExp) translateStmtsExp(thenp.pos, thenp.stats)
              else ISZ(thenp.stats.map(translateStat(enclosing)): _*),
              ISZ()
            ),
            bodyCheck(
              if (isAssignExp) translateStmtsExp(elsep.pos, elsep.stats)
              else ISZ(elsep.stats.map(translateStat(enclosing)): _*),
              ISZ()
            ),
            attr(stat.pos)
          )
        case (thenp: Term.Block, elsep: Term.If) =>
          AST.Stmt.If(
            cond,
            bodyCheck(
              if (isAssignExp) translateStmtsExp(thenp.pos, thenp.stats)
              else ISZ(thenp.stats.map(translateStat(enclosing)): _*),
              ISZ()
            ),
            bodyCheck(ISZ(translateIfStmt(Enclosing.Block, elsep, isAssignExp)), ISZ()),
            attr(stat.pos)
          )
        case (thenp: Term.Block, elsep: Lit.Unit) =>
          if (isAssignExp) error(elsep.pos, "Expecting a code block with an expression.")
          AST.Stmt.If(
            cond,
            bodyCheck(ISZ(thenp.stats.map(translateStat(enclosing)): _*), ISZ()),
            bodyCheck(ISZ(), ISZ()),
            attr(stat.pos)
          )
      }
  }

  def translateMatch(enclosing: Enclosing.Type, stat: Term.Match, isAssignExp: Boolean): AST.Stmt.Match = {
    def translateCase(stat: Case): AST.Case = {
      AST
        .Case(translatePattern(stat.pat), opt(stat.cond.map(translateExp)), stat.body match {
          case b: Term.Block =>
            bodyCheck(
              if (isAssignExp) translateStmtsExp(b.pos, b.stats)
              else ISZ(b.stats.map(translateStat(Enclosing.Block)): _*),
              ISZ()
            )
          case b: Term if isAssignExp =>
            bodyCheck(
              if (isAssignExp) ISZ(translateAssignExp(b))
              else ISZ(translateStat(Enclosing.Block)(b)),
              ISZ()
            )
          case b if isAssignExp =>
            error(stat.body.pos, s"Expecting an expression but '${syntax(b)}' found.")
            bodyCheck(ISZ(), ISZ())
          case b => bodyCheck(ISZ(translateStat(Enclosing.Block)(b)), ISZ())
        })
    }

    stmtCheck(enclosing, stat, "Match-statements")
    val exp = translateExp(stat.expr)
    val cases = stat.cases.map(translateCase)
    exp match {
      case AST.Exp.Select(_, AST.Id(String("native")), _) =>
        var i = 0
        for (c <- cases) {
          c.pattern match {
            case _: AST.Pattern.LitInterpolate =>
              error(stat.cases(i).pat.pos, s"Cannot use a literal interpolator when matching using 'native'.")
            case _ =>
          }
          i = i + 1
        }
      case _ =>
    }
    AST.Stmt.Match(exp, ISZ(cases: _*), attr(stat.pos))
  }

  def translateWhile(enclosing: Enclosing.Type, stat: Term.While): AST.Stmt = {
    var hasError = stmtCheck(enclosing, stat, "While-statements")
    var invariants: ISZ[AST.Exp] = ISZ()
    var maxItOpt: Option[AST.Exp.LitZ] = None()
    var mods: ISZ[AST.Exp.Ref] = ISZ()
    var stats: Seq[Stat] = Seq()
    stat.body match {
      case body: Term.Block =>
        body.stats match {
          case q"Invariant(..$exprs)" :: rest =>
            val (is, ms, mio) = translateLoopInvariant(exprs, body.stats.head.pos)
            invariants = is
            mods = ms
            stats = rest
            maxItOpt = mio
          case _ =>
            stats = body.stats
        }
      case _ =>
        hasError = true
        errorInSlang(stat.body.pos, "While-loop body should be a code block")
    }
    if (hasError) rStmt
    else
      AST.Stmt.While(
        ISZ(),
        translateExp(stat.expr),
        AST.LoopContract(invariants, mods, maxItOpt),
        bodyCheck(ISZ(stats.map(translateStat(Enclosing.Block)): _*), ISZ()),
        attr(stat.pos)
      )
  }

  def translateDoWhile(enclosing: Enclosing.Type, stat: Term.Do): AST.Stmt = {
    warn(stat.pos, "Do-while-loop is deprecated in Slang and will be removed in the near future")
    var hasError = stmtCheck(enclosing, stat, "Do-while-statements")
    var modifies: ISZ[AST.Exp.Ref] = ISZ()
    var invariants: ISZ[AST.Exp] = ISZ()
    var maxItOpt: Option[AST.Exp.LitZ] = None()
    var stats: Seq[Stat] = Seq()
    stat.body match {
      case body: Term.Block =>
        body.stats match {
          case q"Invariant(..$exprs)" :: rest =>
            val (is, ms, mio) = translateLoopInvariant(exprs, body.stats.head.pos)
            modifies = ms
            invariants = is
            maxItOpt = mio
            stats = rest
          case _ =>
            stats = body.stats
        }
      case _ =>
        hasError = true
        errorInSlang(stat.body.pos, "Do-while-loop body should be a code block")
    }
    if (hasError) rStmt
    else
      AST.Stmt.DoWhile(
        ISZ(),
        translateExp(stat.expr),
        AST.LoopContract(invariants, modifies, maxItOpt),
        bodyCheck(ISZ(stats.map(translateStat(Enclosing.Block)): _*), ISZ()),
        attr(stat.pos)
      )
  }

  def translateFor(enclosing: Enclosing.Type, stat: Term.For): AST.Stmt = {
    var hasError = stmtCheck(enclosing, stat, "For-statements")
    var modifies: ISZ[AST.Exp.Ref] = ISZ()
    var invariants = ISZ[(ISZ[AST.Exp], Option[AST.Exp.LitZ])]()
    var stats: Seq[Stat] = Seq()
    stat.body match {
      case body: Term.Block =>
        var i = 0
        var stop = false
        while (i < body.stats.size && !stop) {
          body.stats(i) match {
            case q"Invariant(..$exprs)" if !stop =>
              val (is, ms, mio) = translateLoopInvariant(exprs, body.stats.head.pos)
              modifies = ms
              invariants = invariants :+ ((is, mio))
              if (ms.nonEmpty && i > 1) {
                error(body.stats(i).pos, "Modifies clause has to appear in the first invariant specification")
                hasError = true
              }
              i = i + 1
            case _ => stop = true
          }
        }
        for (j <- i until body.stats.size) {
          stats = stats :+ body.stats(j)
        }
      case _ =>
        hasError = true
        errorInSlang(stat.body.pos, "For-loop body should be a code block")
    }
    if (hasError) rStmt
    else {
      val enumGens = translateEnumGens(stat.enums)

      AST.Stmt.For(
        ISZ(),
        for (i <- Z(0) until enumGens.size) yield
          if (i < invariants.size) enumGens(i)(contract = AST.LoopContract(invariants(i)._1,
            if (i == 0) modifies else ISZ(), invariants(i)._2)) else enumGens(i),
        bodyCheck(ISZ(stats.map(translateStat(Enclosing.Block)): _*), ISZ()),
        attr(stat.pos)
      )
    }
  }

  def translateRange(r: Term): AST.EnumGen.Range = {
    r match {
      case q"$start until $end by $by" =>
        AST.EnumGen.Range
          .Step(isInclusive = false, translateExp(start), translateExp(end), Some(translateExp(by)), attr(r.pos))
      case q"$start to $end by $by" =>
        AST.EnumGen.Range
          .Step(isInclusive = true, translateExp(start), translateExp(end), Some(translateExp(by)), attr(r.pos))
      case q"$start until $end" =>
        AST.EnumGen.Range.Step(isInclusive = false, translateExp(start), translateExp(end), None(), attr(r.pos))
      case q"$start to $end" =>
        AST.EnumGen.Range.Step(isInclusive = true, translateExp(start), translateExp(end), None(), attr(r.pos))
      case _ => AST.EnumGen.Range.Expr(translateExp(r), attr(r.pos))
    }
  }

  def translateEnumGens(enums: Seq[Enumerator]): ISZ[AST.EnumGen.For] = enums match {
    case head :: enumerator"if $cond" :: rest =>
      head match {
        case enumerator"${_: Pat.Wildcard} <- $expr" =>
          AST.EnumGen.For(None(), translateRange(expr), Some(translateExp(cond)), AST.LoopContract.empty) +: translateEnumGens(rest)
        case enumerator"${id: Pat.Var} <- $expr" =>
          checkReservedId(true, id.name.pos, id.name.value)
          AST.EnumGen.For(Some(cid(id)), translateRange(expr), Some(translateExp(cond)), AST.LoopContract.empty) +: translateEnumGens(rest)
        case _ =>
          errorNotSlang(head.pos, s"For-loop enumerator: '${syntax(head)}'")
          ISZ()
      }
    case enumerator"${_: Pat.Wildcard} <- $expr" :: rest =>
      AST.EnumGen.For(None(), translateRange(expr), None(), AST.LoopContract.empty) +: translateEnumGens(rest)
    case enumerator"${id: Pat.Var} <- $expr" :: rest =>
      checkReservedId(true, id.name.pos, id.name.value)
      AST.EnumGen.For(Some(cid(id)), translateRange(expr), None(), AST.LoopContract.empty) +: translateEnumGens(rest)
    case Nil => ISZ()
    case _ =>
      errorNotSlang(enums.head.pos, s"For-loop enumerator: '${syntax(enums.head)}'")
      ISZ()
  }

  def translateReturn(enclosing: Enclosing.Type, stat: Term.Return): AST.Stmt.Return = {
    stmtCheck(enclosing, stat, "Return-statements")
    stat.expr match {
      case _: Lit.Unit => AST.Stmt.Return(None(), typedAttr(stat.pos))
      case _ => AST.Stmt.Return(Some(translateExp(stat.expr)), typedAttr(stat.pos))
    }
  }

  def translateExp(exp: Term): AST.Exp = {
    def quantType(qid: Predef.String, f: Term.Function): AST.Exp.QuantType = {
      val isForall = qid == "All" || qid == "∀"
      val f = exp.asInstanceOf[Term.Apply].argClause.values.head
      AST.Exp.QuantType(isForall, translateExp(f).asInstanceOf[AST.Exp.Fun], attr(exp.pos))
    }

    def quant(qid: Predef.String, d: Term, f: Term.Function): AST.Exp.Quant = {
      val isForall = qid == "All" || qid == "∀"
      val fExp = translateExp(f).asInstanceOf[AST.Exp.Fun]
      if (fExp.params.size != 1) {
        error(exp.pos, s"Expecting a single parameter, but ${fExp.params.size} found.")
      }
      fExp.exp match {
        case _: AST.Stmt.Expr =>
        case _: AST.Stmt.Block => error(exp.pos, "Expecting an expression, but a block found.")
        case _: AST.Stmt.If => error(exp.pos, "Expecting an expression, but a conditional found.")
        case _: AST.Stmt.Match => error(exp.pos, "Expecting an expression, but a match found.")
        case _: AST.Stmt.Return => error(exp.pos, "Expecting an expression, but a return found.")
      }
      d match {
        case q"$lo until $hi" => AST.Exp.QuantRange(isForall, translateExp(lo), translateExp(hi), false, fExp, resolvedAttr(exp.pos))
        case q"$lo to $hi" => AST.Exp.QuantRange(isForall, translateExp(lo), translateExp(hi), true, fExp, resolvedAttr(exp.pos))
        case _ => AST.Exp.QuantEach(isForall, translateExp(d), fExp, resolvedAttr(exp.pos))
      }
    }

    def typeCond(pos: Position, margs: List[Term], mf: Term.Function): AST.Exp = {
      var ok = true
      if (margs.size != mf.paramClause.size) {
        error(mf.pos, s"Expecting ${margs.size} number of function parameters, but found ${mf.paramClause.size}.")
        ok = false
      }
      for (p <- mf.paramClause.values) {
        if (p.name.value == "") {
          error(p.pos, "Parameter of ? function operator should be explicitly named")
          ok = false
        }
        if (p.decltpe.isEmpty) {
          error(p.pos, "Parameter of ? function operator should be explicitly typed")
          ok = false
        }
      }
      val args = ISZ[AST.Exp]((for (marg <- margs) yield translateExp(marg)): _*)
      val f = translateFun(mf).asInstanceOf[AST.Exp.Fun]
      if (!f.exp.isInstanceOf[AST.Stmt.Expr]) {
        reporter.error(f.exp.asStmt.posOpt, messageKind, "Expecting an expression body")
        ok = false
      }
      if (ok) {
        return AST.Exp.TypeCond(args, f, attr(exp.pos))
      } else {
        return rExp
      }
    }

    exp match {
      case exp: Lit => translateLit(exp)
      case exp: Term.Interpolate =>
        val prefix: String = exp.prefix.value
        if (builtinPrefix.contains(prefix)) translateLit(exp)
        else translateStringInterpolate(exp)
      case exp: Term.Name =>
        if (exp.value == "Res") {
          AST.Exp.Result(None(), typedAttr(exp.pos))
        } else {
          AST.Exp.Ident(cid(exp), resolvedAttr(exp.pos))
        }
      case exp: Term.This => AST.Exp.This(typedAttr(exp.pos))
      case exp: Term.Super if exp.thisp.value == "" =>
        if (exp.superp.value != "") AST.Exp.Super(Some(cid(exp.superp)), typedAttr(exp.pos))
        else AST.Exp.Super(None(), typedAttr(exp.pos))
      case exp: Term.Eta =>
        val ref: AST.Exp.Ref = translateExp(exp.expr) match {
          case r: AST.Exp.Ident => r
          case r: AST.Exp.Select => r
          case _ =>
            error(exp.expr.pos, s"Expecting an identifier or an access expression, but found ${syntax(exp.expr)}.")
            rExp
        }
        AST.Exp.Eta(ref, typedAttr(exp.pos))
      case exp: Term.Tuple => AST.Exp.Tuple(ISZ(exp.args.map(translateExp): _*), typedAttr(exp.pos))
      case q"${res@Term.Name("Res")}[$t]" =>
        val pos = if (exp.pos == Position.None) res.pos else exp.pos
        AST.Exp.Result(Some(translateType(t)), typedAttr(pos))
      case q"${op@Term.Name("?")}(..$margs)(${mf: Term.Function})" if margs.nonEmpty => return typeCond(op.pos, margs, mf)
      case q"${op@Term.Name("?")}(..$margs)(${Term.Block(List(mf: Term.Function))})" if margs.nonEmpty => return typeCond(op.pos, margs, mf)
      case q"Idx[$t]($arg)" =>
        translateIdent("Idx", arg) match {
          case Some(idx) => AST.Exp.LoopIndex(Some(translateType(t)), idx, typedAttr(exp.pos))
          case _ => rExp
        }
      case q"Idx($arg)" =>
        error(exp.pos, s"Idx requires an explicit type parameter of the sequence index type.")
        rExp
      case q"${Term.Name(qid)}($d)(${f: Term.Function})" if quantSymbols.contains(qid) => quant(qid, d, f)
      case q"${Term.Name(qid)}($d)({ ${f: Term.Function} })" if quantSymbols.contains(qid) => quant(qid, d, f)
      case q"${Term.Name(qid)}(${Term.Block(List(f: Term.Function))})" if quantSymbols.contains(qid) => quantType(qid, f)
      case q"${Term.Name(qid)}(${f: Term.Function})" if quantSymbols.contains(qid) => quantType(qid, f)
      case exp: Term.ApplyUnary => translateUnaryExp(exp)
      case exp: Term.ApplyInfix => translateBinaryExp(exp)

      case q"InfoFlowInvariant(..$rexprs)" =>
        val infoFlows = AST.MethodContract.InfoFlows(translateInfoFlows(rexprs), attr(exp.pos))
        val flowCases: ISZ[AST.MethodContract.InfoFlowCase] = {
          var cases = ISZ[AST.MethodContract.InfoFlowCase]()
          for(i <- 0 until infoFlows.flows.elements.size) {
            infoFlows.flows.elements(i) match {
              case c: AST.MethodContract.InfoFlowCase => cases = cases :+ c
              case e: AST.MethodContract.InfoFlowFlow =>
                error(rexprs(i).pos, s"InfoFlow loop invariants cannot currently contain flow cases.")
              case e: AST.MethodContract.InfoFlowGroup =>
                error(rexprs(i).pos, s"InfoFlow loop invariants cannot currently contain flow groups.")
            }
          }
          cases
        }
        AST.Exp.InfoFlowInvariant(flowInvariants = flowCases, attr = attr(exp.pos))

      case q"${name: Term.Name}(..$aaexprs)" if name.value == "AssumeAgree" =>
        var channel: AST.Exp.LitString = AST.Exp.LitString("invalid", AST.Attr(None()))
        var i = 0
        val length = aaexprs.length
        if (length > 0) {
          aaexprs(i) match {
            case l: Lit.String =>
              channel = AST.Exp.LitString(l.value, attr(l.pos))
              i += 1
            case _ =>
              error(name.pos, s"The first argument to ${name.value} must be an existing channel")
          }
        } else {
          error(name.pos, s"${name.value} requires at least the channel")
        }
        var requires = AST.MethodContract.Claims.empty
        if (i < length) {
          aaexprs(i) match {
            case q"Requires(..$rexprs)" =>
              requires = AST.MethodContract.Claims(translateExps(rexprs), attr(aaexprs(i).pos))
              i += 1
            case _ =>
          }
        }
        var inAgrees = AST.MethodContract.Claims.empty
        if (i < length) {
          aaexprs(i) match {
            case q"InAgree(..$rexprs)" =>
              inAgrees = AST.MethodContract.Claims(translateExps(rexprs), attr(aaexprs(i).pos))
              i += 1
            case _ =>
          }
        }
        for (j <- i until length) {
          val expr = aaexprs(j)
          error(expr.pos, s"Unrecognized ${name.value} argument.")
        }
        AST.Exp.AssumeAgree(channel, requires, inAgrees, attr(if (exp.pos == Position.None) name.pos else exp.pos))

      case q"${name: Term.Name}(..$aaexprs)" if name.value == "AssertAgree" =>
        var channel: AST.Exp.LitString = AST.Exp.LitString("invalid", AST.Attr(None()))
        var i = 0
        val length = aaexprs.length
        if (length > 0) {
          aaexprs(i) match {
            case l: Lit.String =>
              channel = AST.Exp.LitString(l.value, attr(l.pos))
              i += 1
            case _ =>
              error(name.pos, s"The first argument to ${name.value} must be the channel to be checked")
          }
        } else {
          error(name.pos, s"${name.value} requires at least the channel that should be checked")
        }
        var outAgrees = AST.MethodContract.Claims.empty
        if (i < length) {
          aaexprs(i) match {
            case q"OutAgree(..$rexprs)" =>
              outAgrees = AST.MethodContract.Claims(translateExps(rexprs), attr(aaexprs(i).pos))
              i += 1
            case _ =>
          }
        }
        for (j <- i until length) {
          val expr = aaexprs(j)
          error(expr.pos, "Unrecognized AssertAgree argument.")
        }
        AST.Exp.AssertAgree(channel, outAgrees, attr(if (exp.pos == Position.None) name.pos else exp.pos))

      case q"${name: Term.Name}($arg)" if name.value == "In" || name.value == "Old" =>
        val e = translateExp(arg)
        e match {
          case _: AST.Exp.Ref =>
          case _: AST.Exp.This =>
          case _ =>
            errorInSlang(arg.pos, "In(...) argument has to be a variable reference or this")
            return rExp
        }
        if (name.value == "In") AST.Exp.Input(e, attr(if (exp.pos == Position.None) name.pos else exp.pos))
        else AST.Exp.Old(e, attr(if (exp.pos == Position.None) name.pos else exp.pos))
      case q"${name: Term.Name}[$tpe]($arg, ..$args)" if name.value == "At" =>
        var freshLines = ISZ[AST.Exp.LitZ]()
        var num = AST.Exp.LitZ(0, attr(exp.pos))
        var i = 2
        for (a <- args) {
          translateExp(a) match {
            case e: AST.Exp.LitZ =>
              if (i == 2) num = e
              else freshLines = freshLines :+ e
            case _ => errorInSlang(a.pos, s"At[...](...) #$i argument has to be a Z literal")
          }
          i = i + 1
        }
        if (args.isEmpty) {
          errorInSlang(exp.pos, s"At[...](...) requires at least two arguments")
        }
        def err(): Unit = {
          errorInSlang(arg.pos, "The first At[...](...) argument has to be a string literal simple/qualified name")
        }
        translateExp(arg) match {
          case e: AST.Exp.LitString =>
            val ids = e.value.value.split('.').map(_.trim)
            if (ids.isEmpty) {
              err()
            }
            AST.Exp.At(Some(translateType(tpe)), e, num, freshLines, attr(if (exp.pos == Position.None) name.pos else exp.pos))
          case _ =>
            errorInSlang(arg.pos, "The first At[...](...) argument has to be a string literal")
            rExp
        }
      case q"${name: Term.Name}($arg, ..$args)" if name.value == "At" =>
        var freshLines = ISZ[AST.Exp.LitZ]()
        var num = AST.Exp.LitZ(0, attr(exp.pos))
        var i = 2
        for (a <- args) {
          translateExp(a) match {
            case e: AST.Exp.LitZ =>
              if (i == 2) num = e
              else freshLines = freshLines :+ e
            case _ => errorInSlang(a.pos, s"At(...) #$i argument has to be a Z literal")
          }
          i = i + 1
        }
        if (args.isEmpty) {
          errorInSlang(exp.pos, s"At(...) requires at least two arguments")
        }
        translateExp(arg) match {
          case e: AST.Exp.Ref => AST.Exp.At(None(), e.asExp, num, freshLines,
            attr(if (exp.pos == Position.None) name.pos else exp.pos))
          case e: AST.Exp.This => AST.Exp.At(None(), e, num, freshLines,
            attr(if (exp.pos == Position.None) name.pos else exp.pos))
          case _ =>
            errorInSlang(arg.pos, "The first At(...) argument has to be a variable reference or this")
            rExp
        }
      case q"$expr.$name[..$tpes](...${aexprssnel: List[List[Term]]})" if tpes.nonEmpty && aexprssnel.nonEmpty =>
        translateInvoke(
          scala.Some(expr),
          cid(name),
          name.pos,
          tpes,
          aexprssnel,
          Position.Range(expr.pos.input, name.pos.start, exp.pos.end)
        )
      case q"$expr.$name(...${aexprssnel: List[List[Term]]})" if aexprssnel.nonEmpty =>
        translateInvoke(
          scala.Some(expr),
          cid(name),
          name.pos,
          List(),
          aexprssnel,
          Position.Range(expr.pos.input, name.pos.start, exp.pos.end)
        )
      case q"${name: Term.Name}[..$tpes](...${aexprssnel: List[List[Term]]})" if aexprssnel.nonEmpty =>
        name.value match {
          case "Res" =>
            val rcv = Term.ApplyType(name, Type.ArgClause(tpes.toList))
            translateInvoke(scala.Some(rcv), AST.Id("apply", attr(name.pos)), name.pos, List(), aexprssnel, exp.pos)
          case "At" =>
            val rcv = Term.ApplyType(name, Type.ArgClause(tpes.toList))
            translateInvoke(scala.Some(rcv), AST.Id("apply", attr(name.pos)), name.pos, List(), aexprssnel.tail, exp.pos)
          case _ => translateInvoke(scala.None, cid(name), name.pos, tpes, aexprssnel, exp.pos)
        }
      case q"${name: Term.Name}(...${aexprssnel: List[List[Term]]})" if aexprssnel.nonEmpty =>
        name.value match {
          case "Res" => translateInvoke(scala.Some(name), AST.Id("apply", attr(name.pos)), name.pos, List(), aexprssnel, exp.pos)
          case "In" | "At" | "Old" =>
            val receiver = q"$name(..${aexprssnel.head})"
            translateInvoke(scala.Some(receiver), AST.Id("apply", attr(name.pos)), name.pos, List(), aexprssnel.tail, exp.pos)
          case _ => translateInvoke(scala.None, cid(name), name.pos, List(), aexprssnel, exp.pos)
        }
      case q"${fun @ q"this"}(...${aexprssnel: List[List[Term]]})" if aexprssnel.nonEmpty =>
        translateInvoke(scala.Some(fun), cidNoCheck("apply", fun.pos), fun.pos, List(), aexprssnel, exp.pos)
      case q"$expr.$name[..$tpes]" if tpes.nonEmpty =>
        translateSelect(expr, name, tpes, Position.Range(exp.pos.input, name.pos.start, exp.pos.end))
      case q"$expr.$name" => translateSelect(expr, name, List(), name.pos)
      case q"${name: Term.Name}[..$tpes]" if tpes.nonEmpty =>
        translateSelect(name, tpes, Position.Range(exp.pos.input, name.pos.start, exp.pos.end))
      case exp: Term.If => translateIfExp(exp)
      case exp: Term.Function => translateFun(exp)
      case Term.Block(List(fn: Term.Function)) => translateFun(fn)
      case exp: Term.ForYield => translateForYield(exp)
      case exp: Term.Block =>
        val b = translateBlock(Enclosing.Block, exp, isAssignExp = true)
        val r = AST.Exp.StrictPureBlock(b, AST.TypedAttr(b.posOpt, None()))
        r
      case q"$expr: @l(${name: Lit.Int})" =>
        AST.Exp.Labeled(Some(translateLit(name).asInstanceOf[AST.Exp.LitZ]), translateExp(expr), attr(exp.pos))
      case q"$expr: @l" =>
        AST.Exp.Labeled(None(), translateExp(expr), attr(exp.pos))
      case _ =>
        errorNotSlang(exp.pos, s"Expression '${syntax(exp)}' is")
        rExp
    }
  }

  def translateLit(lit: Lit): AST.Lit = lit match {
    case Lit.Boolean(value) => AST.Exp.LitB(value, attr(lit.pos))
    case Lit.Char(value) => AST.Exp.LitC(value, attr(lit.pos))
    case Lit.Int(value) => AST.Exp.LitZ(value, attr(lit.pos))
    case Lit.Long(value) => AST.Exp.LitZ(value, attr(lit.pos))
    case Lit.Float(value) =>
      try AST.Exp.LitF32(value.toFloat, attr(lit.pos))
      catch {
        case _: NumberFormatException =>
          error(lit.pos, "Invalid 32-bit float number form.")
          AST.Exp.LitF32(0.0f, attr(lit.pos))
      }
    case Lit.Double(value) =>
      try AST.Exp.LitF64(value.toDouble, attr(lit.pos))
      catch {
        case _: NumberFormatException =>
          error(lit.pos, "Invalid 64-bit double number form.")
          AST.Exp.LitF64(0.0d, attr(lit.pos))
      }
    case Lit.String(value) => AST.Exp.LitString(value, attr(lit.pos))
    case _ =>
      errorNotSlang(lit.pos, s"Literal '${syntax(lit)}' is")
      AST.Exp.LitB(false, attr(lit.pos))
  }

  def translateLit(prefix: Term.Name, args: Seq[_], parts: Seq[Lit], pos: Position, syntx: => Predef.String): AST.Exp with AST.Lit = {
    def errR = AST.Exp.LitB(false, attr(pos))

    if (text.substring(pos.start, pos.end).startsWith(prefix.value + "\"\"\"")) {
      error(pos, "'" + prefix.value + "\"...\"' should be used instead of '" + prefix.value + "\"\"...\"\"\"'.")
      return errR
    }
    if (args.nonEmpty || !(parts match {
        case List(Lit.String(_)) => true
        case _ => false
      })) {
      errorNotSlang(pos, s"Literal '$syntx' is")
      return errR
    }
    val List(Lit.String(value)) = parts
    val r = prefix.value match {
      case "z" =>
        try AST.Exp.LitZ(Z.$String(value), attr(pos))
        catch {
          case _: Throwable =>
            error(pos, s"Invalid Z literal '$value'.")
            AST.Exp.LitZ(0, attr(pos))
        }
      case "r" =>
        try AST.Exp.LitR(org.sireum.R.$String(value), attr(pos))
        catch {
          case _: Throwable =>
            error(pos, s"Invalid R literal '$value'.")
            AST.Exp.LitR(org.sireum.R.$String("0"), attr(pos))
        }
      case "c" =>
        val c = StringContext.processEscapes(value)
        if (c.length != 1) {
          error(pos, s"Invalid C literal '$value'.")
          AST.Exp.LitC('?', attr(pos))
        } else AST.Exp.LitC(c.head, attr(pos))
      case "f32" =>
        try AST.Exp.LitF32(value.toFloat, attr(pos))
        catch {
          case _: Throwable =>
            error(pos, "Invalid 32-bit float number form.")
            AST.Exp.LitF32(0.0f, attr(pos))
        }
      case "f64" =>
        try AST.Exp.LitF64(value.toDouble, attr(pos))
        catch {
          case _: Throwable =>
            error(pos, "Invalid 64-bit double number form.")
            AST.Exp.LitF64(0.0, attr(pos))
        }
      case "string" => AST.Exp.LitString(value, attr(pos))
    }
    return r
  }

  def translateLit(lit: Pat.Interpolate): AST.Pattern.LitInterpolate = {
    if (lit.args.nonEmpty || lit.parts.size != 1) {
      errorInSlang(lit.pos, s"Literal pattern interpolation cannot have arguments")
      AST.Pattern.LitInterpolate(lit.prefix.value, "", typedAttr(lit.pos))
    } else {
      val List(Lit.String(value)) = lit.parts
      lit.prefix.value match {
        case "z" =>
          try Z.$String(value)
          catch {
            case _: Throwable => error(lit.pos, s"Invalid Z literal '$value'.")
          }
        case "r" =>
          try org.sireum.R.$String(value)
          catch {
            case _: Throwable => error(lit.pos, s"Invalid R literal '$value'.")
          }
        case "c" =>
          if ((StringContext.processEscapes(value)).size != 1) {
            error(lit.pos, s"Invalid C literal '$value'.")
          }
        case "f32" =>
          try value.toFloat
          catch {
            case _: Throwable => error(lit.pos, s"Invalid F32 literal '$value'.")
          }
        case "f64" =>
          try value.toDouble
          catch {
            case _: Throwable => error(lit.pos, s"Invalid F64 literal '$value'.")
          }
        case "string" =>
        case prefix =>
          try Z.$String(value)
          catch {
            case _: Throwable =>
              error(lit.pos, s"Invalid ${prefix.head.toUpper}${prefix.substring(1)} literal '$value'.")
          }
      }
      AST.Pattern.LitInterpolate(lit.prefix.value, lit.parts.head.value.toString, typedAttr(lit.pos))
    }
  }

  def translateLit(lit: Term.Interpolate): AST.Exp with AST.Lit =
    translateLit(lit.prefix, lit.args, lit.parts, lit.pos, syntax(lit))

  def translateStringInterpolate(s: Term.Interpolate): AST.Exp.StringInterpolate = {
    s.prefix.value match {
      case "s" | "st" | "proc" =>
      case _ =>
        if (s.args.nonEmpty || s.parts.size != 1) {
          errorInSlang(s.pos, s"Literal pattern interpolation cannot have arguments.")
        } else {
          val List(Lit.String(value)) = s.parts
          try Z.$String(value)
          catch {
            case _: Throwable =>
              error(s.pos, s"Invalid ${s.prefix.value.head.toUpper}${s.prefix.value.substring(1)} literal '$value'.")
          }

        }
    }
    AST.Exp.StringInterpolate(s.prefix.value, ISZ(s.parts.map({
      case Lit.String(value) => AST.Exp.LitString(value, attr(s.pos))
      case _ =>
        error(s.pos, s"Invalid string interpolation: '${syntax(s)}'")
        AST.Exp.LitString("", attr(s.pos))
    }): _*), ISZ(s.args.map(translateExp): _*), typedAttr(s.pos))
  }

  def translateUnaryExp(t: Term.ApplyUnary): AST.Exp = {
    unops.get(t.op.value) match {
      case scala.Some(op) => AST.Exp.Unary(op, translateExp(t.arg), resolvedAttr(t.op.pos))
      case _ =>
        errorInSlang(t.op.pos, s"'${t.op.value}' is not a unary operator")
        rExp
    }
  }

  def translateBinaryExp(t: Term.ApplyInfix): AST.Exp = {
    t match {
      case q"${left: Term.Name} ~ $term ~ $right" =>
        def rexp(term: Term): AST.Exp = {
          val e = translateExp(term)
          e match {
            case _: AST.Exp.Invoke =>
            case _: AST.Exp.InvokeNamed =>
            case _ => error(term.pos, "Expecting an invocation for state sequencing.")
          }
          return e
        }
        def rec(rt: Term, rr: Term): ISZ[AST.Exp.StateSeq.Fragment] = {
          rr match {
            case r: Term.Name => return ISZ(AST.Exp.StateSeq.Fragment(cid(r), rexp(rt)))
            case q"${rleft: Term.Name} ~ $re ~ $rright" =>
              return AST.Exp.StateSeq.Fragment(cid(rleft), rexp(re)) +: rec(re, rright)
          }
        }
        AST.Exp.StateSeq(cid(left), rec(term, right), attr(t.pos))
      case _ =>
        if (t.targClause.nonEmpty)
          errorInSlang(t.targClause.values.head.pos, "Binary operations cannot have type arguments")

        val id = infixSymbols.getOrElse(t.op.value, t.op.value)
        if (!checkSymbol(id) && !infixSymbols.contains(id)) {
          error(
            t.op.pos,
            s"Cannot use infix expression notation to invoke '${t.op.value}' in Slang (use dot invoke notation instead: $t)."
          )
        }
        t.argClause.values match {
          case List(right) => AST.Exp.Binary(translateExp(t.lhs), id, translateExp(right), resolvedAttr(t.op.pos))
          case values =>
            AST.Exp.Binary(translateExp(t.lhs), id,
              AST.Exp.Tuple(ISZ((for (value <- values) yield translateExp(value)): _*), typedAttr(t.argClause.pos)),
              resolvedAttr(t.op.pos))
        }
    }
  }

  def translateInvoke(
    receiverOpt: scala.Option[Term],
    name: AST.Id,
    namePos: Position,
    tpes: Seq[Type],
    argss: Seq[Seq[Term]],
    pos: Position
  ): AST.Exp = {
    def translateArgss(argss: Seq[Seq[Term]]): Either[ISZ[AST.NamedArg], ISZ[AST.Exp]] = {
      if (argss.isEmpty) return Right(ISZ())
      translateArgs(argss.head)
    }

    val id = name.value.value
    if (checkSymbol(id) && !quantSymbols.contains(id)) {
      reporter.error(
        name.attr.posOpt,
        SlangParser.messageKind,
        s"Cannot use dot invocation notation for $id in Slang (use infix expression notation instead)."
      )
    }

    var r: AST.Exp = translateArgss(argss) match {
      case Left(args) =>
        AST.Exp.InvokeNamed(
          opt(receiverOpt.map(translateExp)),
          AST.Exp.Ident(name, resolvedAttr(namePos)),
          ISZ(tpes.map(translateType): _*),
          args,
          resolvedAttr(pos)
        )
      case Right(args) =>
        AST.Exp.Invoke(
          opt(receiverOpt.map(translateExp)),
          AST.Exp.Ident(name, resolvedAttr(namePos)),
          ISZ(tpes.map(translateType): _*),
          args,
          resolvedAttr(pos)
        )
    }
    var prevPos = namePos
    for (i <- 1 until argss.size) {
      val args = argss(i)
      val pos =
        if (args.nonEmpty) Position.Range(args.head.pos.input, args.head.pos.start, args.last.pos.end)
        else prevPos
      prevPos = pos
      r = translateApply(r, argss(i), pos)
    }
    r
  }

  def translateSelect(receiver: Term, name: Term.Name, tpes: Seq[Type], pos: Position): AST.Exp = {
    if (name.value == "native" && tpes.nonEmpty) {
      error(name.pos, "Selector 'native' does not accept type arguments.")
    }
    AST.Exp.Select(Some(translateExp(receiver)), cid(name), ISZ(tpes.map(translateType): _*), resolvedAttr(pos))
  }

  def translateSelect(name: Term.Name, tpes: Seq[Type], pos: Position): AST.Exp = {
    AST.Exp.Select(None(), cid(name), ISZ(tpes.map(translateType): _*), resolvedAttr(pos))
  }

  def translateIfExp(exp: Term.If): AST.Exp = {
    var hasError = false
    if (exp.thenp.isInstanceOf[Term.Block]) {
      hasError = true
      errorInSlang(exp.thenp.pos, "If-then expression should not be a code block")
    }
    exp.elsep match {
      case elsep: Term.Block =>
        hasError = true
        errorInSlang(elsep.pos, "If-else expression should not be a code block")
      case elsep: Lit.Unit =>
        hasError = true
        errorInSlang(elsep.pos, "If-else expression should not be empty")
      case _ =>
    }
    if (hasError) rExp
    else AST.Exp.If(translateExp(exp.cond), translateExp(exp.thenp), translateExp(exp.elsep), typedAttr(exp.pos))
  }

  def translateForYield(exp: Term.ForYield): AST.Exp = {
    val enums = translateEnumGens(exp.enums)
    var hasError = enums.isEmpty
    exp.body match {
      case body: Term.Block =>
        hasError = true
        errorInSlang(body.pos, "For-yield expression should not be a code block")
      case body: Lit.Unit =>
        hasError = true
        errorInSlang(body.pos, "For-yield expression should not be empty")
      case _ =>
    }
    if (hasError) rExp
    else AST.Exp.ForYield(enums, translateExp(exp.body), typedAttr(exp.pos))
  }

  def translateFun(exp: Term.Function): AST.Exp = {
    val ps = ISZ(exp.paramClause.values.map(translateFunParam): _*)

    val body = translateAssignExp(exp.body)
    AST.Exp.Fun(ISZ(), ps, body, typedAttr(exp.pos))
  }

  def translateArgs(args: Seq[Term]): Either[ISZ[AST.NamedArg], ISZ[AST.Exp]] = {
    def expArg(arg: Term): AST.Exp = arg match {
      case q"$e1 -> $e2" => AST.Exp.Tuple(ISZ(translateExp(e1), expArg(e2)), typedAttr(arg.pos))
      case _ => translateExp(arg)
    }

    def namedArg(arg: Term): AST.NamedArg = arg match {
      case q"${name: Term.Name} = ${expr: Term}" => AST.NamedArg(cid(name), expArg(expr), -1)
      case _ =>
        errorNotSlang(arg.pos, s"Argument ${syntax(arg)} is")
        AST.NamedArg(rDollarId, rExp, -1)
    }

    var isNamed = false
    var isPositional = false
    for (arg <- args) arg match {
      case _: Term.Repeated =>
        errorInSlang(arg.pos, s"Repeated argument: '${syntax(arg)}'")
        return Right(ISZ())
      case _: Term.Assign => isNamed = true
      case _ => isPositional = true
    }
    if (isNamed && isPositional) {
      errorInSlang(args.head.pos, "Cannot mix positional and named arguments")
      Right(ISZ())
    } else if (isNamed) Left(ISZ(args.map(namedArg): _*))
    else Right(ISZ(args.map(expArg): _*))
  }

  def translateApply(fun: AST.Exp, termArgs: Seq[Term], pos: Position): AST.Exp = {
    translateArgs(termArgs) match {
      case Left(args) =>
        AST.Exp.InvokeNamed(
          Some(fun),
          AST.Exp.Ident(cidNoCheck("apply", pos), resolvedAttr(pos)),
          ISZ(),
          args,
          resolvedAttr(pos)
        )
      case Right(args) =>
        AST.Exp
          .Invoke(Some(fun), AST.Exp.Ident(cidNoCheck("apply", pos), resolvedAttr(pos)), ISZ(), args, resolvedAttr(pos))
    }
  }

  def translateIdent(construct: String, expr: Term): Option[AST.Exp.Ident] = {
    expr match {
      case expr: Term.Name => Some(AST.Exp.Ident(cid(expr.value, expr.pos), resolvedAttr(expr.pos)))
      case _ =>
        error(expr.pos, s"Only simple identifier is supported for $construct argument.")
        None()
    }
  }

  def translateIdents(construct: String, exprs: Seq[Term]): ISZ[AST.Exp.Ident] = {
    var r = ISZ[AST.Exp.Ident]()
    for (expr <- exprs) {
      translateIdent(construct, expr) match {
        case Some(e) => r = r :+ e
        case _ =>
      }
    }
    r
  }

  def translateRef(construct: String, expr: Term): Option[AST.Exp.Ref] = {
    translateExp(expr) match {
      case e: AST.Exp.Ref => Some(e)
      case _ =>
        error(expr.pos, s"Only variable reference is supported for $construct argument.")
        None()
    }
  }

  def translateRefs(construct: String, exprs: Seq[Term]): ISZ[AST.Exp.Ref] = {
    var r = ISZ[AST.Exp.Ref]()
    for (expr <- exprs) {
      translateRef(construct, expr) match {
        case Some(e) => r = r :+ e
        case _ =>
      }
    }
    r
  }

  def translateExps(exprs: Seq[Term]): ISZ[AST.Exp] = {
    var r = ISZ[AST.Exp]()
    for (expr <- exprs) {
      r = r :+ translateExp(expr)
    }
    r
  }

  def translateContractCase(exprs: Seq[Term]): AST.MethodContract.Case = {
    var label: AST.Exp.LitString = AST.Exp.LitString("", emptyAttr)
    var i = 0
    val length = exprs.length
    if (i < length) {
      exprs(i) match {
        case expr: Lit.String =>
          label = AST.Exp.LitString(expr.value, attr(expr.pos))
          i += 1
        case _ =>
      }
    }
    var requires = AST.MethodContract.Claims.empty
    if (i < length) {
      exprs(i) match {
        case q"Requires(..$rexprs)" =>
          requires = AST.MethodContract.Claims(translateExps(rexprs), attr(exprs(i).pos))
          i += 1
        case _ =>
      }
    }
    var ensures = AST.MethodContract.Claims.empty
    if (i < length) {
      exprs(i) match {
        case q"Ensures(..$eexprs)" =>
          ensures = AST.MethodContract.Claims(translateExps(eexprs), attr(exprs(i).pos))
          i += 1
        case _ =>
      }
    }
    for (j <- i until length) {
      val expr = exprs(j)
      error(expr.pos, "Unrecognized Contract Case argument.")
    }
    AST.MethodContract.Case(label, requires, ensures)
  }

  def translateContractCases(exprs: Seq[Term], ccAttr: AST.Attr): AST.MethodContract.Cases = {
    val length = exprs.length
    var i = 0
    var reads = AST.MethodContract.Accesses.empty
    exprs(i) match {
      case q"Reads(..$rexprs)" =>
        reads = AST.MethodContract.Accesses(translateRefs("Reads", rexprs), attr(exprs(i).pos))
        i += 1
      case _ =>
    }
    var modifies = AST.MethodContract.Accesses.empty
    if (i < length) {
      exprs(i) match {
        case q"Modifies(..$rexprs)" =>
          modifies = AST.MethodContract.Accesses(translateRefs("Modifies", rexprs), attr(exprs(i).pos))
          i += 1
        case _ =>
      }
    }
    var cases = ISZ[AST.MethodContract.Case]()
    while (i < length) {
      exprs(i) match {
        case q"Case(..$cexprs)" => cases = cases :+ translateContractCase(cexprs)
        case expr => error(expr.pos, "Unrecognized Contract argument.")
      }
      i += 1
    }
    AST.MethodContract.Cases(reads, modifies, cases, ccAttr)
  }

  def translateContractSimple(exprs: Seq[Term], csAttr: AST.Attr): AST.MethodContract.Simple = {
    val length = exprs.length
    var i = 0
    var reads = AST.MethodContract.Accesses.empty
    if (i < length) {
      exprs(i) match {
        case q"Reads(..$rexprs)" =>
          reads = AST.MethodContract.Accesses(translateRefs("Reads", rexprs), attr(exprs(i).pos))
          i += 1
        case _ =>
      }
    }
    var requires = AST.MethodContract.Claims.empty
    if (i < length) {
      exprs(i) match {
        case q"Requires(..$rexprs)" =>
          requires = AST.MethodContract.Claims(translateExps(rexprs), attr(exprs(i).pos))
          i += 1
        case _ =>
      }
    }
    var modifies = AST.MethodContract.Accesses.empty
    if (i < length) {
      exprs(i) match {
        case q"Modifies(..$mexprs)" =>
          modifies = AST.MethodContract.Accesses(translateRefs("Modifies", mexprs), attr(exprs(i).pos))
          i += 1
        case _ =>
      }
    }
    var ensures = AST.MethodContract.Claims.empty
    if (i < length) {
      exprs(i) match {
        case q"Ensures(..$rexprs)" =>
          ensures = AST.MethodContract.Claims(translateExps(rexprs), attr(exprs(i).pos))
          i += 1
        case _ =>
      }
    }

    var infoFlows = AST.MethodContract.InfoFlows.empty
    if (i < length) {
      exprs(i) match {
        case q"InfoFlows(..$rexprs)" =>
          infoFlows = AST.MethodContract.InfoFlows(translateInfoFlows(rexprs), attr(exprs(i).pos))
          i += 1
        case _ =>
      }
    }

    for (j <- i until length) {
      val expr = exprs(j)
      error(expr.pos, "Unrecognized Contract argument.")
    }
    AST.MethodContract.Simple(reads, requires, modifies, ensures, infoFlows, csAttr)
  }

  def translateInfoFlows(exprs: Seq[Term]): ISZ[AST.MethodContract.InfoFlowElement] = {
    val length = exprs.length
    var i = 0
    var infoFlows: ISZ[AST.MethodContract.InfoFlowElement] = ISZ()
    while (i < length) {
      exprs(i) match {
        case q"Flow(..$cexprs)" => infoFlows = infoFlows :+ translateInfoFlowFlow(cexprs)
        case q"FlowCase(..$cexprs)" => infoFlows = infoFlows :+ translateInfoFlowCase(cexprs)
        case q"Groups(..$cexprs)" => infoFlows = infoFlows :+ translateInfoFlowGroup(cexprs)
        case expr => error(expr.pos, "Unrecognized InfoFlow flow argument")
      }
      i += 1
    }
    return infoFlows
  }

  def translateInfoFlowGroup(exprs: Seq[Term]): AST.MethodContract.InfoFlowElement = {
    var label: AST.Exp.LitString = AST.Exp.LitString("", emptyAttr)
    val length = exprs.length
    var i = 0
    if (i < length) {
      exprs(i) match {
        case expr: Lit.String =>
          label = AST.Exp.LitString(expr.value, attr(expr.pos))
          i += 1
        case _ =>
      }
    }

    var members = AST.MethodContract.Claims.empty
    if (i < length) {
      exprs(i) match {
        case q"Vars(..$iexprs)" =>
          members = AST.MethodContract.Claims(translateExps(iexprs), attr(exprs(i).pos))
          i += 1
      }
    }
    for (j <- i until length) {
      val expr = exprs(j)
      error(expr.pos, "Unrecognized InfoFlow Group argument")
    }
    return AST.MethodContract.InfoFlowGroup(label, members)
  }

  def translateInfoFlowFlow(exprs: Seq[Term]): AST.MethodContract.InfoFlowElement = {
    var label: AST.Exp.LitString = AST.Exp.LitString("", emptyAttr)
    val length = exprs.length
    var i = 0
    if (i < length) {
      exprs(i) match {
        case expr: Lit.String =>
          label = AST.Exp.LitString(expr.value, attr(expr.pos))
          i += 1
        case _ =>
      }
    }
    var froms = AST.MethodContract.Claims.empty
    if (i < length) {
      exprs(i) match {
        case q"From(..$iexprs)" =>
          froms = AST.MethodContract.Claims(translateExps(iexprs), attr(exprs(i).pos))
          i += 1
      }
    }
    var tos = AST.MethodContract.Claims.empty
    if (i < length) {
      exprs(i) match {
        case q"To(..$iexprs)" =>
          tos = AST.MethodContract.Claims(translateExps(iexprs), attr(exprs(i).pos))
          i += 1
      }
    }
    for (j <- i until length) {
      val expr = exprs(j)
      error(expr.pos, "Unrecognized InfoFlow Flow argument")
    }
    return AST.MethodContract.InfoFlowFlow(label, froms, tos)
  }

  def translateInfoFlowCase(exprs: Seq[Term]): AST.MethodContract.InfoFlowElement = {
    var label: AST.Exp.LitString = AST.Exp.LitString("", emptyAttr)
    val length = exprs.length
    var i = 0
    if (i < length) {
      exprs(i) match {
        case expr: Lit.String =>
          label = AST.Exp.LitString(expr.value, attr(expr.pos))
          i += 1
        case _ =>
      }
    }
    var requires = AST.MethodContract.Claims.empty
    if (i < length) {
      exprs(i) match {
        case q"Requires(..$iexprs)" =>
          requires = AST.MethodContract.Claims(translateExps(iexprs), attr(exprs(i).pos))
          i += 1
        case _ =>
      }
    }
    var inAgrees = AST.MethodContract.Claims.empty
    if (i < length) {
      exprs(i) match {
        case q"InAgree(..$iexprs)" =>
          inAgrees = AST.MethodContract.Claims(translateExps(iexprs), attr(exprs(i).pos))
          i += 1
      }
    }
    var outAgrees = AST.MethodContract.Claims.empty
    if (i < length) {
      exprs(i) match {
        case q"OutAgree(..$iexprs)" =>
          outAgrees = AST.MethodContract.Claims(translateExps(iexprs), attr(exprs(i).pos))
          i += 1
      }
    }
    for (j <- i until length) {
      val expr = exprs(j)
      error(expr.pos, "Unrecognized InfoFlow Case argument")
    }
    return AST.MethodContract.InfoFlowCase(label, requires, inAgrees, outAgrees)
  }

  def translateMethodContract(exprs: Seq[Term], mcAttr: AST.Attr): AST.MethodContract = {
    val r = if (exprs.exists({
      case q"Case(..$_)" => true
      case _ => false
    })) translateContractCases(exprs, mcAttr) else translateContractSimple(exprs, mcAttr)
    val cfc = AST.Util.ContractFormChecker(messageKind, Reporter.create)
    cfc.transformMethodContract(r)
    reporter.reports(cfc.reporter.messages)
    r
  }

  def translateInvariant(enclosing: Enclosing.Type, stat: Defn.Def): AST.Stmt.Inv = {
    def isInvariantContext: Boolean = enclosing match {
      case Enclosing.Top | Enclosing.Object | Enclosing.ExtObject | Enclosing.DatatypeTrait | Enclosing.DatatypeClass |
           Enclosing.RecordTrait | Enclosing.RecordClass | Enclosing.Sig | Enclosing.MSig =>
        true
      case _ => false
    }
    if (!isInvariantContext) {
      error(stat.pos, "Invalid invariant location")
    }
    var claims = ISZ[AST.Exp]()
    stat.body match {
      case q"Invariant(..$iexprs)" =>
        for (iexpr <- iexprs) {
          claims = claims :+ translateExp(iexpr)
        }
      case _ =>
    }
    AST.Stmt.Inv(cid(stat.name), claims, resolvedAttr(stat.pos))
  }

  def translateFact(enclosing: Enclosing.Type, stat: Defn.Def): AST.Stmt.Fact = {
    def isFactContext: Boolean = enclosing match {
      case Enclosing.Top | Enclosing.Object | Enclosing.ExtObject => true
      case _ => false
    }
    if (!isFactContext) {
      if (isWorksheet) error(stat.pos, "Fact can only appear at the top-level, inside objects, or @ext objects.")
      else error(stat.pos, "Fact can only appear inside objects or @ext objects.")
    }
    val typeArgs = ISZ((for (pcg <- stat.paramClauseGroups; tp <- pcg.tparamClause.values) yield tp).map(translateTypeParam(AST.Typed.VarKind.Immutable, false)): _*)
    val q"Fact(..${fexprs: Term.ArgClause})" = stat.body
    val (descOpt, exprs: Seq[Term]) = fexprs.headOption match {
      case scala.Some(lit: Lit.String) => (Some(translateLit(lit).asInstanceOf[AST.Exp.LitString]), fexprs.values.tail)
      case _ => (None[AST.Exp.LitString](), fexprs.values)
    }
    var claims = ISZ[AST.Exp]()
    var params = ISZ[AST.Exp.Fun.Param]()
    stat.paramClauses.size match {
      case 0 =>
      case 1 =>
        for (p <- stat.paramClauses.head.values) {
          params = params :+ translateFunParam(p)
        }
      case _ => error(stat.pos, "Cannot have more than one list of parameters")
    }
    for (iexpr <- exprs) {
      val claim = translateExp(iexpr)
      val tattr = typedAttr(iexpr.pos)
      claims = claims :+ (
        if (params.nonEmpty) AST.Exp.QuantType(true, AST.Exp.Fun(ISZ(), params,
          AST.Stmt.Expr(claim, tattr), tattr), attr(iexpr.pos))
        else claim)
    }
    AST.Stmt.Fact(cid(stat.name), typeArgs, descOpt, claims, params.nonEmpty, resolvedAttr(stat.pos))
  }

  def translateDeduce(enclosing: Enclosing.Type, stat: Stat): AST.Stmt.Spec = {
    def isDeduceContext: Boolean = enclosing match {
      case Enclosing.Top | Enclosing.Method | Enclosing.Block => true
      case _ => false
    }
    if (!isDeduceContext) {
      if (isWorksheet) error(stat.pos, "Deduce can only appear at the top-level, inside methods, or code blocks.")
      else error(stat.pos, "Deduce can only appear inside methods or code blocks.")
    }
    val q"Deduce(..$dexprs)" = stat
    val isProofStep = dexprs.headOption match {
      case scala.Some(head: Term) => !(head.pos.text.contains("|-") || head.pos.text.contains("⊢"))
      case _ => false
    }
    if (isProofStep) {
      AST.Stmt.DeduceSteps(translateAndCheckProofSteps(dexprs), attr(stat.pos))
    } else {
      val (justOpt, args): (Option[AST.Exp.LitString], Seq[Term]) = dexprs.headOption match {
        case scala.Some(s: Lit.String) => (Some(translateLit(s).asInstanceOf[AST.Exp.LitString]), dexprs.tail)
        case _ => (None(), dexprs)
      }
      AST.Stmt.DeduceSequent(justOpt, ISZ(args.map(translateSequent): _*), attr(stat.pos))
    }
  }

  def translateDataRefinement(enclosing: Enclosing.Type, stat: Stat): AST.Stmt.DataRefinement = {
    def isDataRefinementContext: Boolean = enclosing match {
      case Enclosing.MSig | Enclosing.RecordTrait | Enclosing.RecordClass => true
      case _ => false
    }
    if (!isDataRefinementContext) {
      error(stat.pos, "DataRefinement can only appear inside @msig traits, @record traits, or @record classes.")
    }
    val q"Contract(DataRefinement($rep)(..$refs)(..$claims))" = stat
    AST.Stmt.DataRefinement(
      translateIdent("DataRefinement", rep).getOrElse(rExp),
      ISZ[AST.Exp.Ref](refs.map(ref => translateIdent("DataRefinement", ref).getOrElse(rExp)): _*),
      ISZ(claims.map(translateExp): _*),
      attr(stat.pos)
    )
  }

  def translateHavoc(enclosing: Enclosing.Type, stat: Stat): AST.Stmt.Spec = {
    def isHavocContext: Boolean = enclosing match {
      case Enclosing.Top | Enclosing.Method | Enclosing.Block => true
      case _ => false
    }
    if (!isHavocContext) {
      if (isWorksheet) error(stat.pos, "Contract.Havoc can only appear at the top-level, inside methods, or code blocks.")
      else error(stat.pos, "Contract.Havoc can only appear inside methods or code blocks.")
    }
    val q"Contract.Havoc(..$hexprs)" = stat
    AST.Stmt.Havoc(translateRefs("Contract.Havoc", hexprs), attr(stat.pos))
  }

  def translateSequent(sequent: Term): AST.Sequent = {
    def isSequent(t: Term): Boolean = t match {
      case t: Term.Name => t.value == "|-" || t.value == "⊢"
      case _ => false
    }
    sequent match {
      case q"$sym $conclusion Proof(..$pexprs)" if isSequent(sym) =>
        AST.Sequent(ISZ(), translateExp(conclusion),
          translateAndCheckProofSteps(pexprs), attr(sequent.pos))
      case q"$sym $conclusion" if isSequent(sym) =>
        AST.Sequent(ISZ(), translateExp(conclusion),
          ISZ(), attr(sequent.pos))
      case q"$sym ($conclusion) Proof(..$pexprs)" if isSequent(sym) =>
        AST.Sequent(ISZ(), translateExp(conclusion),
          translateAndCheckProofSteps(pexprs), attr(sequent.pos))
      case q"$sym ($conclusion)" if isSequent(sym) =>
        AST.Sequent(ISZ(), translateExp(conclusion),
          ISZ(), attr(sequent.pos))
      case q"$premise $sym $conclusion Proof(..$pexprs)" if isSequent(sym) =>
        premise match {
          case premise: Term.Tuple =>
            AST.Sequent(ISZ(premise.args.map(translateExp): _*), translateExp(conclusion),
              translateAndCheckProofSteps(pexprs), attr(sequent.pos))
          case _ =>
            AST.Sequent(ISZ(translateExp(premise)), translateExp(conclusion),
              translateAndCheckProofSteps(pexprs), attr(sequent.pos))
        }
      case q"$premise $sym $conclusion" if isSequent(sym) =>
        premise match {
          case premise: Term.Tuple =>
            AST.Sequent(ISZ(premise.args.map(translateExp): _*), translateExp(conclusion),
              ISZ(), attr(sequent.pos))
          case _ =>
            AST.Sequent(ISZ(translateExp(premise)), translateExp(conclusion),
              ISZ(), attr(sequent.pos))
        }
      case q"$premise $sym ($conclusion) Proof(..$pexprs)" if isSequent(sym) =>
        premise match {
          case premise: Term.Tuple =>
            AST.Sequent(ISZ(premise.args.map(translateExp): _*), translateExp(conclusion),
              translateAndCheckProofSteps(pexprs), attr(sequent.pos))
          case _ =>
            AST.Sequent(ISZ(translateExp(premise)), translateExp(conclusion),
              translateAndCheckProofSteps(pexprs), attr(sequent.pos))
        }
      case q"$premise $sym ($conclusion)" if isSequent(sym) =>
        premise match {
          case premise: Term.Tuple =>
            AST.Sequent(ISZ(premise.args.map(translateExp): _*), translateExp(conclusion),
              ISZ(), attr(sequent.pos))
          case _ =>
            AST.Sequent(ISZ(translateExp(premise)), translateExp(conclusion),
              ISZ(), attr(sequent.pos))
        }
      case _ =>
        error(sequent.pos, "Expecting '... |- ... Proof(...)' or '... |- ...'.")
        AST.Sequent(ISZ(), AST.Exp.LitB(false, emptyAttr), ISZ(), attr(sequent.pos))
    }
  }

  def translateSpecLabel(enclosing: Enclosing.Type, stat: Stat): AST.Stmt.SpecLabel = {
    def isLabelContext: Boolean = enclosing match {
      case Enclosing.Top | Enclosing.Method | Enclosing.Block => true
      case _ => false
    }
    if (!isLabelContext) {
      if (isWorksheet) error(stat.pos, s"Spec label can only appear at the top-level or inside code blocks.")
      else error(stat.pos, s"Spec label can only appear inside code blocks.")
    }
    val q"Spec(${s: Lit.String})" = stat
    AST.Stmt.SpecLabel(cid(s.value, s.pos))
  }

  def translateSpecBlock(enclosing: Enclosing.Type, stat: Stat): AST.Stmt.SpecBlock = {
    val q"Spec(${b: Term.Block})" = stat
    AST.Stmt.SpecBlock(translateBlock(enclosing, b, isAssignExp = false))
  }

  def translateStructuralInduction(n: AST.ProofAst.StepId, claim: AST.Exp, m: Term): AST.ProofAst.Step.StructInduction = {
    def hypoSteps(steps: Seq[Term]): (Option[AST.ProofAst.Step.Assume], ISZ[AST.ProofAst.Step]) = {
      steps.headOption match {
        case scala.Some(q"$no #> Assume($claim)") if isStepId(no) =>
          (Some(AST.ProofAst.Step.Assume(toStepId(no), translateExp(claim))),
            ISZ(steps.tail.map(translateProofStep(false)): _*))
        case _ =>
          (None(), ISZ(steps.map(translateProofStep(false)): _*))
      }
    }
    m match {
      case q"$e match { ..case $cs }" =>
        var cases = ISZ[AST.ProofAst.Step.StructInduction.MatchCase]()
        val (defaultOpt, rest): (Option[AST.ProofAst.Step.StructInduction.MatchDefault], Seq[Case]) =
          cs.lastOption match {
            case scala.Some(p"case _ => SubProof(..$pexprs)") =>
              val (hypoOpt, proofSteps) = hypoSteps(pexprs)
              (Some(AST.ProofAst.Step.StructInduction.MatchDefault(hypoOpt, proofSteps)), cs.dropRight(1))
            case _ => (None(), cs)
          }
        for (c <- rest) {
          c match {
            case p"case $ref(..$pats) => SubProof(..$steps)" =>
              var patterns = ISZ[AST.Pattern]()
              for (pat <- pats) {
                pat match {
                  case pat: Pat.Var => patterns = patterns :+ translatePattern(pat)
                  case _ => error(pat.pos, "Only pattern variable is allowed.")
                }
              }
              val (hypoOpt, proofSteps) = hypoSteps(steps)
              cases = cases :+ AST.ProofAst.Step.StructInduction.MatchCase(
                AST.Pattern.Structure(None(), Some(AST.Name(ref2IS(ref), attr(ref.pos))),
                  patterns, ISZ(), resolvedAttr(c.pat.pos)), hypoOpt, proofSteps
              )
            case _ => error(c.pos, "Expecting 'case ...(...) => SubProof(...)'.")
          }
        }
        AST.ProofAst.Step.StructInduction(n, claim, translateExp(e), cases, defaultOpt)
      case _ =>
        error(m.pos, "Expecting '... match { case ...(...) => SubProof(...) ... }'.")
        AST.ProofAst.Step.StructInduction(n, claim, AST.Exp.LitB(false, emptyAttr), ISZ(), None())
    }
  }

  def toLitZ(n: Lit.Int): AST.Exp.LitZ = AST.Exp.LitZ(n.value, attr(n.pos))

  def translateAssumeSubClaims(claims: Seq[Term]): ISZ[AST.ProofAst.Step] =
    if (claims.nonEmpty)
      translateProofStep(true)(claims.head) +:
        ISZ((for (i <- 1 until claims.length) yield translateProofStep(false)(claims(i))): _*)
    else ISZ()

  def isStepId(term: Term): Boolean = {
    term match {
      case _: Lit.Int => return true
      case _: Lit.String => return true
      case _ => return false
    }
  }
  def toStepId(term: Term): AST.ProofAst.StepId = {
    term match {
      case term: Lit.Int => return AST.ProofAst.StepId.Num(term.value, attr(term.pos))
      case term: Lit.String => return AST.ProofAst.StepId.Str(false, term.value, attr(term.pos))
    }
  }
  def toStepId(pos: Position): AST.ProofAst.StepId = {
    val at = attr(pos)
    val atpos = at.posOpt.get
    return AST.ProofAst.StepId.Str(true, s"[${atpos.beginLine}, ${atpos.beginColumn}]", at)
  }

  def translateProofStep(allowAssume: B)(proofStep: Term): AST.ProofAst.Step = {
    def err(): AST.ProofAst.Step = {
      error(proofStep.pos, s"Invalid proof step form: '$proofStep'.")
      emptyProofStep
    }

    def justRef(no: Either[Term, Position], claim: Term, just: Term.Ref, hasWitness: Boolean,
                witnesses: Seq[Term]): AST.ProofAst.Step = {
      val stepNo = no match {
        case Left(t) => toStepId(t)
        case Right(p) => toStepId(p)
      }
      val stepClaim = translateExp(claim)
      val justExp: AST.Exp.Ref = translateExp(just) match {
        case e: AST.Exp.Ref => e
        case _ => return err()
      }
      AST.ProofAst.Step.Regular(stepNo, stepClaim,
        AST.ProofAst.Step.Justification.Ref(justExp, hasWitness, translateWitnesses(witnesses)))
    }
    def justApply(no: Either[Term, Position], claim: Term, just: Term.Apply, hasWitness: Boolean,
                  witnesses: Seq[Term]): AST.ProofAst.Step.Regular = {
      val stepNo = no match {
        case Left(t) => toStepId(t)
        case Right(p) => toStepId(p)
      }
      val stepClaim = translateExp(claim)
      val justExp = translateExp(just)
      justExp match {
        case tExp: AST.Exp.Invoke =>
          AST.ProofAst.Step.Regular(stepNo, stepClaim,
            AST.ProofAst.Step.Justification.Apply(tExp, hasWitness, translateWitnesses(witnesses)))
        case tExp: AST.Exp.InvokeNamed =>
          AST.ProofAst.Step.Regular(stepNo, stepClaim,
            AST.ProofAst.Step.Justification.ApplyNamed(tExp, hasWitness, translateWitnesses(witnesses)))
        case _ =>
          reporter.error(justExp.posOpt, messageKind, s"Expecting a method invocation but found '${just.syntax}'")
          AST.ProofAst.Step.Regular(stepNo, stepClaim,
            AST.ProofAst.Step.Justification.Ref(AST.Exp.Ident(AST.Id("?", emptyAttr), emptyResolvedAttr), false, ISZ()))
      }
    }
    def justApplyEta(no: Either[Term, Position], claim: Term, just: Term.Eta, hasWitness: Boolean,
                     witnesses: Seq[Term]): AST.ProofAst.Step.Regular = {
      val stepNo = no match {
        case Left(t) => toStepId(t)
        case Right(p) => toStepId(p)
      }
      val stepClaim = translateExp(claim)
      val justExp = translateExp(just).asInstanceOf[AST.Exp.Eta]
      AST.ProofAst.Step.Regular(stepNo, stepClaim,
        AST.ProofAst.Step.Justification.ApplyEta(justExp, hasWitness, translateWitnesses(witnesses)))
    }

    def translateLetParam(param: Term.Param): AST.ProofAst.Step.Let.Param = {
      if (param.mods.nonEmpty) {
        for (mod <- param.mods) {
          error(mod.pos, "Cannot have Let parameter modifier.")
        }
      }
      if (param.default.nonEmpty) {
        error(param.default.get.pos, "Cannot have Let parameter default value.")
      }
      AST.ProofAst.Step.Let.Param(cid(param.name), opt(param.decltpe.map(translateType)))
    }
    def translateWitnesses(terms: Seq[Term]): ISZ[AST.ProofAst.StepId] = {
      val ws = ISZ[AST.Exp](terms.map(translateExp): _*)
      var witnesses = ISZ[AST.ProofAst.StepId]()
      for (i <- Z(0) until ws.size) {
        val w = ws(i)
        w match {
          case w: AST.Exp.LitZ => witnesses = witnesses :+ AST.ProofAst.StepId.Num(w.value, w.attr)
          case w: AST.Exp.LitString => witnesses = witnesses :+ AST.ProofAst.StepId.Str(false, w.value, w.attr)
          case _ =>
            reporter.error(w.posOpt, messageKind, s"Expecting a proof step id but found '${terms(i.toInt).syntax}'")
        }
      }
      witnesses
    }
    val r: AST.ProofAst.Step = proofStep match {
      case q"$no #> $claim by ${just: Term} and (..$witnesses)" if isStepId(no) =>
        just match {
          case just: Term.Eta => justApplyEta(Left(no), claim, just, hasWitness = true, witnesses)
          case just: Term.Apply => justApply(Left(no), claim, just, hasWitness = true, witnesses)
          case just: Term.Ref => justRef(Left(no), claim, just, hasWitness = true, witnesses)
          case _ => err()
        }
      case q"$no #> $claim by ${just: Term} * (..$witnesses)" if isStepId(no) =>
        just match {
          case just: Term.Eta => justApplyEta(Left(no), claim, just, hasWitness = true, witnesses)
          case just: Term.Apply => justApply(Left(no), claim, just, hasWitness = true, witnesses)
          case just: Term.Ref => justRef(Left(no), claim, just, hasWitness = true, witnesses)
          case _ => err()
        }
      case q"$no #> $claim by ${just: Term} T" if isStepId(no) =>
        just match {
          case just: Term.Eta => justApplyEta(Left(no), claim, just, hasWitness = true, Seq())
          case just: Term.Apply => justApply(Left(no), claim, just, hasWitness = true, Seq())
          case just: Term.Ref => justRef(Left(no), claim, just, hasWitness = true, Seq())
          case _ => err()
        }
      case q"$no #> $claim by ${just: Term}" if isStepId(no) =>
        just match {
          case just: Term.Apply => justApply(Left(no), claim, just, hasWitness = false, Seq())
          case just: Term.Ref => justRef(Left(no), claim, just, hasWitness = false, Seq())
          case _ => err()
        }
      case q"$no ($claim) by ${just: Term} and (..$witnesses)" if isStepId(no) =>
        just match {
          case just: Term.Eta => justApplyEta(Left(no), claim, just, hasWitness = true, witnesses)
          case just: Term.Apply => justApply(Left(no), claim, just, hasWitness = true, witnesses)
          case just: Term.Ref => justRef(Left(no), claim, just, hasWitness = true, witnesses)
          case _ => err()
        }
      case q"$no ($claim) by ${just: Term} * (..$witnesses)" if isStepId(no) =>
        just match {
          case just: Term.Eta => justApplyEta(Left(no), claim, just, hasWitness = true, witnesses)
          case just: Term.Apply => justApply(Left(no), claim, just, hasWitness = true, witnesses)
          case just: Term.Ref => justRef(Left(no), claim, just, hasWitness = true, witnesses)
          case _ => err()
        }
      case q"$no ($claim) by ${just: Term} T" if isStepId(no) =>
        just match {
          case just: Term.Eta => justApplyEta(Left(no), claim, just, hasWitness = true, Seq())
          case just: Term.Apply => justApply(Left(no), claim, just, hasWitness = true, Seq())
          case just: Term.Ref => justRef(Left(no), claim, just, hasWitness = true, Seq())
          case _ => err()
        }
      case q"$no ($claim) by ${just: Term}" if isStepId(no) =>
        just match {
          case just: Term.Apply => justApply(Left(no), claim, just, hasWitness = false, Seq())
          case just: Term.Ref => justRef(Left(no), claim, just, hasWitness = false, Seq())
          case _ => err()
        }
      case q"$claim by ${just: Term} and (..$witnesses)" =>
        just match {
          case just: Term.Eta => justApplyEta(Right(claim.pos), claim, just, hasWitness = true, witnesses)
          case just: Term.Apply => justApply(Right(claim.pos), claim, just, hasWitness = true, witnesses)
          case just: Term.Ref => justRef(Right(claim.pos), claim, just, hasWitness = true, witnesses)
          case _ => err()
        }
      case q"$claim by ${just: Term} * (..$witnesses)" =>
        just match {
          case just: Term.Eta => justApplyEta(Right(claim.pos), claim, just, hasWitness = true, witnesses)
          case just: Term.Apply => justApply(Right(claim.pos), claim, just, hasWitness = true, witnesses)
          case just: Term.Ref => justRef(Right(claim.pos), claim, just, hasWitness = true, witnesses)
          case _ => err()
        }
      case q"$claim by ${just: Term} T" =>
        just match {
          case just: Term.Eta => justApplyEta(Right(claim.pos), claim, just, hasWitness = true, Seq())
          case just: Term.Apply => justApply(Right(claim.pos), claim, just, hasWitness = true, Seq())
          case just: Term.Ref => justRef(Right(claim.pos), claim, just, hasWitness = true, Seq())
          case _ => err()
        }
      case q"$claim by ${just: Term}" =>
        just match {
          case just: Term.Apply => justApply(Right(claim.pos), claim, just, hasWitness = false, Seq())
          case just: Term.Ref => justRef(Right(claim.pos), claim, just, hasWitness = false, Seq())
          case _ => err()
        }
      case q"$no #> Assume($claim)" if isStepId(no) =>
        val stepNo = toStepId(no)
        if (!allowAssume) {
          reporter.error(stepNo.posOpt, messageKind, "Assume justification cannot be used at this location")
        }
        AST.ProofAst.Step.Assume(stepNo, translateExp(claim))
      case q"$no #> Assert($claim, SubProof(..$claims))" if isStepId(no) =>
        AST.ProofAst.Step.Assert(toStepId(no), translateExp(claim), ISZ(claims.map(translateProofStep(false)): _*))
      case q"$no #> SubProof(..$claims)" if isStepId(no) =>
        val stepNo = toStepId(no)
        val subClaims = translateAssumeSubClaims(claims)
        if (subClaims.nonEmpty && !subClaims(0).isInstanceOf[AST.ProofAst.Step.Assume]) {
          reporter.error(subClaims(0).id.posOpt, messageKind, "Expecting an Assume(...) claim")
        }
        AST.ProofAst.Step.SubProof(stepNo, subClaims)
      case q"$no #> Let ((..$params) => SubProof(..$claims))" if isStepId(no) =>
        AST.ProofAst.Step.Let(toStepId(no), ISZ(params.map(translateLetParam): _*), translateAssumeSubClaims(claims))
      case q"$no #> Let {(..$params) => SubProof(..$claims)}" if isStepId(no) =>
        AST.ProofAst.Step.Let(toStepId(no), ISZ(params.map(translateLetParam): _*), translateAssumeSubClaims(claims))
      case q"$no #> $claim by StructuralInduction($m)" if isStepId(no) =>
        translateStructuralInduction(toStepId(no), translateExp(claim), m)
      case q"$no Assume($claim)" if isStepId(no) =>
        val stepNo = toStepId(no)
        if (!allowAssume) {
          reporter.error(stepNo.posOpt, messageKind, "Assume justification cannot be used at this location")
        }
        AST.ProofAst.Step.Assume(stepNo, translateExp(claim))
      case q"$no Assert($claim, SubProof(..$claims))" if isStepId(no) =>
        AST.ProofAst.Step.Assert(toStepId(no), translateExp(claim), ISZ(claims.map(translateProofStep(false)): _*))
      case q"$no SubProof(..$claims)" if isStepId(no) =>
        val stepNo = toStepId(no)
        val subClaims = translateAssumeSubClaims(claims)
        if (subClaims.nonEmpty && !subClaims(0).isInstanceOf[AST.ProofAst.Step.Assume]) {
          reporter.error(subClaims(0).id.posOpt, messageKind, "Expecting an Assume(...) claim")
        }
        AST.ProofAst.Step.SubProof(stepNo, subClaims)
      case q"$no Let ((..$params) => SubProof(..$claims))" if isStepId(no) =>
        AST.ProofAst.Step.Let(toStepId(no), ISZ(params.map(translateLetParam): _*), translateAssumeSubClaims(claims))
      case q"$no Let {(..$params) => SubProof(..$claims)}" if isStepId(no) =>
        AST.ProofAst.Step.Let(toStepId(no), ISZ(params.map(translateLetParam): _*), translateAssumeSubClaims(claims))
      case _ => err()
    }
    r.id match {
      case id: AST.ProofAst.StepId.Num if id.no < 1 =>
        reporter.error(id.posOpt, messageKind, "Proof step # has to be positive")
      case _ =>
    }
    r
  }

  def translateProof(proof: Term): AST.ProofAst = {
    proof match {
      case q"Proof(..$pexprs)" =>
        AST.ProofAst(translateAndCheckProofSteps(pexprs), attr(proof.pos))
      case _ =>
        error(proof.pos, s"Expecting 'Proof(...)' but found '$proof'.")
        return AST.ProofAst(ISZ(), attr(proof.pos))
    }
  }

  def translateAndCheckProofSteps(steps: Seq[Term]): ISZ[AST.ProofAst.Step] = {
    val r = ISZ(steps.map(translateProofStep(false)): _*)
    val checker = new AST.Util.ProofStepUniquenessChecker(org.sireum.HashMap.empty, messageKind, Reporter.create)
    for (step <- r) {
      checker.transformProofAstStep(step)
    }
    reporter.reports(checker.reporter.messages)
    return r
  }

  def translateTheoremLemma(isLemma: B, enclosing: Enclosing.Type, stat: Defn.Def): AST.Stmt.Theorem = {
    def isTheoremContext: Boolean = enclosing match {
      case Enclosing.Top | Enclosing.Object | Enclosing.ExtObject => true
      case _ => false
    }
    if (!isTheoremContext) {
      val desc = if (isLemma) "Lemma" else "Theorem"
      if (isWorksheet) error(stat.pos, s"$desc can only appear at the top-level, inside object, or @ext object.")
      else error(stat.pos, s"$desc can only appear inside object or @ext object.")
    }
    val typeArgs = ISZ((for (pcg <- stat.paramClauseGroups; tp <- pcg.tparamClause.values) yield tp).map(translateTypeParam(AST.Typed.VarKind.Immutable, false)): _*)
    val (descOpt, claim, proof) = stat.body match {
      case q"${_: Term.Name}(${d: Lit.String}, $e, $p)" =>
        (Some(translateLit(d).asInstanceOf[AST.Exp.LitString]), translateExp(e), translateProof(p))
      case q"${_: Term.Name}($e, $p)" => (None[AST.Exp.LitString](), translateExp(e), translateProof(p))
    }
    var params = ISZ[AST.Exp.Fun.Param]()
    stat.paramClauses.size match {
      case 0 =>
      case 1 =>
        for (p <- stat.paramClauses.head.values) {
          params = params :+ translateFunParam(p)
        }
      case _ => error(stat.pos, "Cannot have more than one list of parameters")
    }
    val tattr = AST.TypedAttr(claim.posOpt, None())
    val (isFun, body): (B, AST.Exp) =
      if (params.isEmpty) (false, claim)
      else (true, AST.Exp.QuantType(true, AST.Exp.Fun(ISZ(), params, AST.Stmt.Expr(claim, tattr), tattr), AST.Attr(claim.posOpt)))
    AST.Stmt.Theorem(isLemma, cid(stat.name), typeArgs, descOpt, body, isFun, proof, resolvedAttr(stat.pos))
  }

  def translateLoopInvariant(exprs: Seq[Term], loopPos: Position): (ISZ[AST.Exp], ISZ[AST.Exp.Ref], Option[AST.Exp.LitZ]) = {
    var mods = ISZ[AST.Exp.Ref]()
    var invs = ISZ[AST.Exp]()
    var maxItOpt: Option[AST.Exp.LitZ] = None()
    var rest = exprs
    rest = rest match {
      case (expr@q"MaxIt(..$mexprs)") :: tail =>
        if (mexprs.size != 1) {
          error(expr.pos, "MaxIt expects a single positive integer literal argument.")
        } else {
          translateExp(mexprs.head) match {
            case exp: AST.Exp.LitZ if exp.value > 0 => maxItOpt = Some(exp)
            case _ =>
              error(mexprs.head.pos, "MaxIt expects a single positive integer literal argument.")
          }
        }
        tail
      case _ => rest
    }
    rest = rest match {
      case q"Modifies(..$mexprs)" :: tail =>
        for (mexpr <- mexprs) {
          translateExp(mexpr) match {
            case e: AST.Exp.Ident => mods = mods :+ e
            case e: AST.Exp.Select => mods = mods :+ e
            case _ => error(mexpr.pos, "Modifies argument should be a variable reference.")
          }
        }
        tail
      case _ => rest
    }
    for (exp <- rest) {
      invs = invs :+ translateExp(exp)
    }
    return (invs, mods, maxItOpt)
  }

  def checkMemberStmts(stmts: ISZ[AST.Stmt]): ISZ[AST.Stmt] = {
    for (stmt <- stmts) stmt match {
      case stmt: AST.Stmt.Method => checkNestedMethods(stmt)
      case _ =>
    }
    stmts
  }

  def checkNestedMethods(m: AST.Stmt.Method): Unit = {
    var set = scala.collection.immutable.HashSet[Predef.String]()
    val transformer = new AST.Transformer[Unit](new AST.Transformer.PrePost[Unit] {
      def string: String = ""

      override def preStmtMethod(ctx: Unit, o: AST.Stmt.Method): AST.Transformer.PreResult[Unit, AST.Stmt] = {
        val id = o.sig.id.value.value
        if (set.contains(id)) {
          reporter
            .error(o.sig.id.attr.posOpt, SlangParser.messageKind, s"Cannot redeclare nested method '$id' in Slang.")
        }
        if (o.sig.typeParams.nonEmpty) {
          reporter
            .error(o.sig.id.attr.posOpt, SlangParser.messageKind, "Cannot declare generic nested methods in Slang.")
        }
        set = set + id
        super.preStmtMethod(ctx, o)
      }
    })
    m.bodyOpt match {
      case Some(body) =>
        for (stmt <- body.stmts) {
          transformer.transformStmt((), stmt)
        }
      case _ =>
    }

  }

  def cid(t: Pat.Var): AST.Id = cid(t.name.value, t.pos)

  def cid(name: Name): AST.Id = cid(name.value, name.pos)

  def cidNoCheck(id: Predef.String, pos: Position): AST.Id =
    AST.Id(id, attr(pos))

  def cid(id: Predef.String, pos: Position): AST.Id = {
    if (id.contains('$') || id.endsWith("_="))
      errorInSlang(pos, s"'$id' is not a valid identifier form")
    cidNoCheck(id, pos)
  }

  def checkTypeId(pos: Position, id: Predef.String): Unit = {
    if (checkSymbol(id) || disallowedTypeIds.contains(id)) {
      errorInSlang(pos, s"Type identifier '$id' is disallowed")
    }
  }

  def isSymbolFirstChar(c: Char): Boolean = {
    c match {
      case '*' | '/' | '%' | '+' | '-' | ':' | '=' | '!' | '<' | '>' | '&' | '^' | '|' => true
      case _ => Character.getType(c) == Character.MATH_SYMBOL || Character.getType(c) == Character.OTHER_SYMBOL
    }
  }

  def isSymbolFollowChar(c: Char): Boolean = {
    c match {
      case '@' | '#' | '?' => true
      case _ => isSymbolFirstChar(c)
    }
  }

  def checkSymbol(id: Predef.String): Boolean = {
    id.headOption.exists(isSymbolFirstChar)
  }

  def checkReservedId(checkVarId: B, pos: Position, id: String): Unit = {
    def err(): Unit = errorInSlang(pos, s"'$id' is a reserved identifier")

    val idv = id.value
    if (checkVarId && disallowedVarIds.contains(id)) {
      err()
    } else if (disallowedMethodIds.contains(id)) {
      err()
    } else if (unaryMethods.contains(id)) {
      return
    } else {
      val i = idv.lastIndexOf('_')
      if (i >= 0 && i != idv.length - 1) {
        val ending: String = idv.substring(i + 1)
        if (disallowedMethodIdEndings.contains(ending)) {
          err()
        }
      }
    }
    if (idv.headOption.exists(isSymbolFirstChar)) {
      if (!idv.tail.forall(isSymbolFollowChar)) {
        errorInSlang(pos, s"Cannot mix symbol and non-symbol characters for identifier starting with a symbol")
      }
    }
  }

  def attr(pos: Position): AST.Attr = AST.Attr(posOpt = posOpt(pos))

  def typedAttr(pos: Position): AST.TypedAttr = AST.TypedAttr(posOpt = posOpt(pos), typedOpt = None())

  def resolvedAttr(pos: Position): AST.ResolvedAttr =
    AST.ResolvedAttr(posOpt = posOpt(pos), resOpt = None(), typedOpt = None())

  def ref2IS(ref: Term): ISZ[AST.Id] = {
    def f(t: Term): ISZ[AST.Id] = t match {
      case name: Term.Name => ISZ(cid(name))
      case q"$expr.$name" => f(expr) :+ cid(name)
      case _ =>
        errorInSlang(t.pos, s"Invalid name reference '${ref.syntax}'")
        ISZ(rDollarId)
    }

    f(ref)
  }

  def syntax(t: Tree, max: Int = 20): Predef.String = {
    val startOffset = lPointOpt.getOrElse(0)
    val text = this.text.substring(startOffset + t.pos.start, startOffset + t.pos.end)
    (if (text.length < max) text else text.substring(0, max) + "...").map {
      case '\r' => ' '
      case '\t' => ' '
      case '\n' => ' '
      case c => c
    }
  }

  def opt[T](opt: scala.Option[T]): Option[T] = opt match {
    case scala.Some(x) => Some(x)
    case _ => None()
  }
}
