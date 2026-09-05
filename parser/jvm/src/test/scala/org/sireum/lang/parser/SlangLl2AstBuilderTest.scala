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
package org.sireum.lang.parser

import org.sireum._
import org.sireum.test._


class SlangLl2AstBuilderTest extends SireumRcSpec {

  private def migrate(source: Predef.String): lang.ast.TopUnit.Program = {
    val reporter = message.Reporter.create
    val program = Parser(String(source)).parseTopUnit[lang.ast.TopUnit.Program](T, F, None(), reporter)
    assert(program.nonEmpty && !reporter.hasError, reporter.messages.toString)
    val printed = lang.ast.SlangLl2PrettyPrinter.prettyPrint(program.get).render
    val tree = SlangLl2Parser.parse(None(), printed, reporter)
    assert(tree.nonEmpty && !reporter.hasError, printed.native + "\n" + reporter.messages.toString)
    val result = lang.ast.SlangLl2AstBuilder.build(None(), tree.get, reporter)
    assert(result.nonEmpty && !reporter.hasError, printed.native + "\n" + reporter.messages.toString)
    return result.get
  }

  registerTest("LL(2) migration preserves empty and named invocations") {
    val program = migrate("""// #Sireum
      |import org.sireum._
      |def empty(): ISZ[Z] = { return ISZ[Z]() }
      |def named(): Pair = { return Pair(left = 1, right = 2) }
      |def invoke(): Z = { return empty().size }
      |""".stripMargin)
    val methods = program.body.stmts.elements.collect { case m: lang.ast.Stmt.Method => m }
    val empty = methods(0).bodyOpt.get.stmts(0).asInstanceOf[lang.ast.Stmt.Return].expOpt.get
    assert(empty.isInstanceOf[lang.ast.Exp.Invoke])
    assert(empty.asInstanceOf[lang.ast.Exp.Invoke].args.isEmpty)
    val named = methods(1).bodyOpt.get.stmts(0).asInstanceOf[lang.ast.Stmt.Return].expOpt.get
      .asInstanceOf[lang.ast.Exp.InvokeNamed]
    assert(named.args.map(_.id.value) == ISZ(String("left"), String("right")))
    val select = methods(2).bodyOpt.get.stmts(0).asInstanceOf[lang.ast.Stmt.Return].expOpt.get
      .asInstanceOf[lang.ast.Exp.Select]
    assert(select.receiverOpt.get.isInstanceOf[lang.ast.Exp.Invoke])
  }

  registerTest("LL(2) migration preserves strict pure value blocks") {
    val program = migrate("""// #Sireum
      |import org.sireum._
      |@strictpure def choose(x: Z): Z = x match { case 0 => 1; case _ => 2 }
      |@strictpure def block(x: Z): Z = { val y: Z = x + 1; y match { case 0 => 1; case _ => y } }
      |def chooseLocal(x: Z): Z = { val y: Z = x match { case 0 => 1; case _ => 2 }; return y }
      |""".stripMargin)
    val methods = program.body.stmts.elements.collect { case m: lang.ast.Stmt.Method => m }
    assert(methods.size == 3)
    assert(methods(0).purity == lang.ast.Purity.StrictPure)
    assert(methods(1).purity == lang.ast.Purity.StrictPure)
    val init = methods(0).bodyOpt.get.stmts(0).asInstanceOf[lang.ast.Stmt.Var].initOpt.get
    val matched = init.asInstanceOf[lang.ast.Stmt.Match]
    assert(matched.cases.elements.forall(_.body.stmts.elements.last.isInstanceOf[lang.ast.Stmt.Expr]))
    val block = methods(1).bodyOpt.get.stmts(0).asInstanceOf[lang.ast.Stmt.Var].initOpt.get
      .asInstanceOf[lang.ast.Stmt.Block]
    assert(block.body.stmts.elements.last.isInstanceOf[lang.ast.Stmt.Match])
    for (purity <- scala.Seq("strictpure", "abs")) {
      val reporter = message.Reporter.create
      val text = String(s"def @$purity reject(): Z = {\n  var x: Z = 1\n  \\ x\n}")
      val tree = SlangLl2Parser.parse(None(), text, reporter)
      assert(tree.nonEmpty && !reporter.hasError)
      lang.ast.SlangLl2AstBuilder.build(None(), tree.get, reporter)
      assert(reporter.hasError, s"@$purity accepted a mutable local")
    }
  }

  registerTest("LL(2) migration preserves datatype constructor fields") {
    val program = migrate("""// #Sireum
      |import org.sireum._
      |@datatype class Payload(@hidden val label: String, val count: Z)
      |@record class Counter(val label: String, var count: Z)
      |@datatype trait Root
      |""".stripMargin)
    val adts = program.body.stmts.elements.collect { case a: lang.ast.Stmt.Adt => a }
    assert(adts.size == 3)
    assert(adts(0).params.map(_.id.value) == ISZ(String("label"), String("count")))
    assert(adts(0).params(0).isHidden)
    assert(adts(0).params.elements.forall(_.isVal.value))
    assert(adts(1).params(0).isVal && !adts(1).params(1).isVal)
    assert(adts(2).isRoot && adts(2).params.isEmpty)
  }

  registerTest("LL(2) migration preserves string template segments") {
    val triple = "\"" * 3
    val source = "// #Sireum\nimport org.sireum._\n" +
      "def quoted(name: String): ST = { return st\"\\\"${name}\\\"\\\\tail\\n$$\" }\n" +
      "def multi(name: String): ST = { return st" + triple + "first ${name}\n  |second $$" + triple + " }\n" +
      "def literal(): ST = { return st\"$$literal\" }\n"
    val reporter = message.Reporter.create
    val original = Parser(String(source)).parseTopUnit[lang.ast.TopUnit.Program](T, F, None(), reporter).get
    assert(!reporter.hasError)
    val migrated = migrate(source)
    def templates(program: lang.ast.TopUnit.Program): scala.Seq[lang.ast.Exp.StringInterpolate] =
      program.body.stmts.elements.collect { case method: lang.ast.Stmt.Method =>
        method.bodyOpt.get.stmts(0).asInstanceOf[lang.ast.Stmt.Return].expOpt.get
          .asInstanceOf[lang.ast.Exp.StringInterpolate]
      }
    val expected = templates(original)
    val actual = templates(migrated)
    assert(actual.size == expected.size)
    for ((a, e) <- actual.zip(expected)) {
      assert(a.prefix == e.prefix)
      assert(a.lits.map(_.value) == e.lits.map(_.value))
      assert(a.args.size == e.args.size)
    }
  }

  registerTest("LL(2) migration preserves comprehension operands") {
    val program = migrate("""// #Sireum
      |import org.sireum._
      |def append(xs: ISZ[Z], ys: ISZ[Z]): ISZ[Z] = {
      |  return (for (x <- xs) yield x + 1) ++ (for (y <- ys) yield y - 1)
      |}
      |""".stripMargin)
    val method = program.body.stmts.elements.collect { case m: lang.ast.Stmt.Method => m }.head
    val result = method.bodyOpt.get.stmts(0).asInstanceOf[lang.ast.Stmt.Return].expOpt.get
      .asInstanceOf[lang.ast.Exp.Binary]
    assert(result.left.isInstanceOf[lang.ast.Exp.ForYield])
    assert(result.right.isInstanceOf[lang.ast.Exp.ForYield])
  }

  registerTest("LL(2) migration preserves symbolic operator precedence") {
    val program = migrate("""// #Sireum
      |import org.sireum._
      |def insert(m: HashSMap[String, Z]): HashSMap[String, Z] = { return m + ("x" ~> 1) }
      |def equivalent(a: Z, b: Z, c: Z): B = { return (a ≡ b) & (b ≢ c) }
      |""".stripMargin)
    val methods = program.body.stmts.elements.collect { case m: lang.ast.Stmt.Method => m }
    val insert = methods(0).bodyOpt.get.stmts(0).asInstanceOf[lang.ast.Stmt.Return].expOpt.get
      .asInstanceOf[lang.ast.Exp.Binary]
    assert(insert.op == String("+"))
    assert(insert.right.asInstanceOf[lang.ast.Exp.Binary].op == String("~>"))
    val equivalent = methods(1).bodyOpt.get.stmts(0).asInstanceOf[lang.ast.Stmt.Return].expOpt.get
      .asInstanceOf[lang.ast.Exp.Binary]
    assert(equivalent.op == String("&"))
    assert(equivalent.left.asInstanceOf[lang.ast.Exp.Binary].op == String("≡"))
    assert(equivalent.right.asInstanceOf[lang.ast.Exp.Binary].op == String("≢"))
  }

  registerTest("LL(2) migration preserves pattern aliases") {
    val program = migrate("""// #Sireum
      |import org.sireum._
      |def keep(value: Option[Z]): Option[Z] = {
      |  value match { case r@Some(_) => return r; case _ => return None() }
      |}
      |""".stripMargin)
    val method = program.body.stmts.elements.collect { case m: lang.ast.Stmt.Method => m }.head
    val matched = method.bodyOpt.get.stmts(0).asInstanceOf[lang.ast.Stmt.Match]
    val pattern = matched.cases(0).pattern.asInstanceOf[lang.ast.Pattern.Structure]
    assert(pattern.idOpt.get.value == String("r"))
    assert(pattern.nameOpt.get.ids.map(_.value) == ISZ(String("Some")))
    assert(pattern.patterns(0).isInstanceOf[lang.ast.Pattern.Wildcard])
  }

  registerTest("LL(2) migration preserves halting value branches") {
    val program = migrate("""// #Sireum
      |import org.sireum._
      |@strictpure def choose(x: Z): Z = x match { case 0 => 1; case _ => halt("invalid") }
      |def local(x: Z): Z = {
      |  val result: Z = x match { case 0 => 1; case _ => halt("invalid") }
      |  return result
      |}
      |""".stripMargin)
    val methods = program.body.stmts.elements.collect { case m: lang.ast.Stmt.Method => m }
    assert(methods.size == 2)
    for (method <- methods) {
      val matched = method.bodyOpt.get.stmts(0).asInstanceOf[lang.ast.Stmt.Var].initOpt.get
        .asInstanceOf[lang.ast.Stmt.Match]
      val halted = matched.cases(1).body.stmts(0).asInstanceOf[lang.ast.Stmt.Expr].exp
        .asInstanceOf[lang.ast.Exp.Invoke]
      assert(halted.ident.id.value == String("halt") && halted.receiverOpt.isEmpty)
      assert(halted.args(0).asInstanceOf[lang.ast.Exp.LitString].value == String("invalid"))
    }
  }

  registerTest("LL(2) string segments follow their dollar grammar") {
    val unicodeDollar = "\\" + "u0024"
    val source = String(
      "val x: Z = 1\n" +
        "val plain = #a$$b\n" +
        "val single = st\"a$$b\"\n" +
        "val singleUnicode = st\"" + unicodeDollar + unicodeDollar + "\"\n" +
        "val singleInterp = st\"a$$b$x$c$$d$x$e$$f\"\n" +
        "val multi = st#a$$b\n" +
        "val multiEscape = st#a\\nb\n" +
        "val multiInterp = st#a$$b${x}$c$$d${x}$e$$f\n")
    val reporter = message.Reporter.create
    val tree = SlangLl2Parser.parse(None(), source, reporter).get
    val program = lang.ast.SlangLl2AstBuilder.build(None(), tree, reporter).get
    assert(!reporter.hasError)

    def exp(id: String): lang.ast.Exp = {
      for (stmt <- program.body.stmts) {
        stmt match {
          case v: lang.ast.Stmt.Var if v.id.value == id =>
            return v.initOpt.get.asInstanceOf[lang.ast.Stmt.Expr].exp
          case _ =>
        }
      }
      throw new AssertionError(s"missing LL(2) variable: $id")
    }

    assert(exp(String("plain")).asInstanceOf[lang.ast.Exp.LitString].value == String("a$$b\n"))
    val single = exp(String("single")).asInstanceOf[lang.ast.Exp.StringInterpolate]
    assert(single.lits.map((l: lang.ast.Exp.LitString) => l.value) == ISZ(String("a$b")))
    val singleUnicode = exp(String("singleUnicode")).asInstanceOf[lang.ast.Exp.StringInterpolate]
    assert(singleUnicode.lits.map((l: lang.ast.Exp.LitString) => l.value) == ISZ(String("$$")))
    val singleInterp = exp(String("singleInterp")).asInstanceOf[lang.ast.Exp.StringInterpolate]
    assert(singleInterp.lits.map((l: lang.ast.Exp.LitString) => l.value) ==
      ISZ(String("a$b"), String("c$d"), String("e$f")))
    assert(singleInterp.args.size == 2)
    val multi = exp(String("multi")).asInstanceOf[lang.ast.Exp.StringInterpolate]
    assert(multi.lits.map((l: lang.ast.Exp.LitString) => l.value) == ISZ(String("a$b\n")))
    val multiEscape = exp(String("multiEscape")).asInstanceOf[lang.ast.Exp.StringInterpolate]
    assert(multiEscape.lits.map((l: lang.ast.Exp.LitString) => l.value) == ISZ(String("a\\nb\n")))
    val multiInterp = exp(String("multiInterp")).asInstanceOf[lang.ast.Exp.StringInterpolate]
    assert(multiInterp.lits.map((l: lang.ast.Exp.LitString) => l.value) ==
      ISZ(String("a$b"), String("c$d"), String("e$f\n")))
    assert(multiInterp.args.size == 2)
  }

  def shouldIgnore(name: Predef.String, isSimplified: Boolean): Boolean = false

  def textResources: scala.collection.SortedMap[scala.Vector[Predef.String], Predef.String] = {
    val m = $internal.RC.text(Vector(
      "../../../../../../../../../../logika-examples/src/programming/manual/",
      "../../../../../../../../../logika-examples/src")
    ) { (p, _) => T }
    implicit val ordering: Ordering[Vector[Predef.String]] = m.ordering
    for ((k, v) <- m; pair <- {
      var r = Vector[(Vector[Predef.String], Predef.String)]()
      if (!shouldIgnore(k.last, F)) {
        r = r :+ (k, v)
      }
      r
    }) yield pair
  }

  def check(path: scala.Vector[Predef.String], content: Predef.String): scala.Boolean = {
    val uriOpt = Some(st"${(path, "/")}".render)
    val reporter = message.Reporter.create
    // Step 1: Parse Slang source -> AST
    lang.parser.Parser(content).parseTopUnit[lang.ast.TopUnit.Program](isWorksheet = T, isDiet = F, uriOpt, reporter) match {
      case Some(program) if !reporter.hasIssue =>
        try {
          // Step 2: Pretty print AST -> LL(2) text
          val ll2 = lang.ast.SlangLl2PrettyPrinter.prettyPrint(program).render

          // Step 3: Parse LL(2) -> ParseTree
          val parseTreeOpt = SlangLl2Parser.parse(uriOpt, ll2, reporter)
          if (reporter.hasError || parseTreeOpt.isEmpty) {
            reporter.printMessages()
            return false
          }

          // Step 4: Build AST from ParseTree
          val builtProgramOpt = lang.ast.SlangLl2AstBuilder.build(uriOpt, parseTreeOpt.get, reporter)
          if (reporter.hasError || builtProgramOpt.isEmpty) {
            reporter.printMessages()
            return false
          }

          // Step 5: Pretty print built AST -> LL(2) text
          val ll2Built = lang.ast.SlangLl2PrettyPrinter.prettyPrint(builtProgramOpt.get).render

          // Step 6: Assert both LL(2) texts are equal
          if (ll2 != ll2Built) {
            println("=== Expected ===")
            println(ll2)
            println("=== Got ===")
            println(ll2Built)
            println("=== Diff ===")
            val nl = conversions.String.toCis("\n")(0)
            val ll2Lines = ops.StringOps(ll2).split((c: C) => c == nl)
            val builtLines = ops.StringOps(ll2Built).split((c: C) => c == nl)
            val maxLines: Z = if (ll2Lines.size > builtLines.size) ll2Lines.size else builtLines.size
            for (i <- 0 until maxLines) {
              val l1: String = if (i < ll2Lines.size) ll2Lines(i) else String("<missing>")
              val l2: String = if (i < builtLines.size) builtLines(i) else String("<missing>")
              if (l1 != l2) {
                println(st"Line $i differs:".render)
                println(st"  expected: $l1".render)
                println(st"  got:      $l2".render)
              }
            }
            return false
          }
        } catch {
          case e: Throwable =>
            println(s"Exception: ${e.getMessage}")
            e.printStackTrace()
            return false
        }
      case _ =>
    }
    if (reporter.hasIssue) {
      reporter.printMessages()
    }
    !reporter.hasError
  }

}
