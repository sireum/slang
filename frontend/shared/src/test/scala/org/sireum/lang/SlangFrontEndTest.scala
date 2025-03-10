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

package org.sireum.lang

import org.sireum.message._
import org.sireum.test._
import org.sireum.{ISZ, HashSMap => SHashSMap, None => SNone, String => SString}
import org.sireum.lang.{ast => AST}
import org.sireum.lang.parser.SlangParser
import org.sireum.lang.symbol._
import org.sireum.lang.tipe._

object SlangFrontEndTest {

  def parse(text: String, isWorksheet: Boolean, reporter: Reporter): SlangParser.Result =
    SlangParser(isWorksheet, isDiet = false, SNone(), text, reporter)

  def passingCheck(
    text: String,
    addImport: Boolean = true,
    isWorksheet: Boolean = false,
    isPrelude: Boolean = false,
    checkJSON: Boolean = true,
    checkMsgPack: Boolean = true
  ): Boolean = {
    val reporter = Reporter.create
    val prelude = if (isPrelude) "" else "// #Sireum\n"
    val impor = if (addImport) "import org.sireum._; " else ""
    val r = parse(
      s"$prelude$impor$text",
      isWorksheet,
      reporter
    )
    var b = r.unitOpt.nonEmpty && !reporter.hasIssue
    if (!b) report(r, reporter)
    else {
      val gdr = GlobalDeclarationResolver(SHashSMap.empty, SHashSMap.empty, reporter)
      reporter.reports(gdr.reporter.messages)
      if (reporter.hasIssue) report(r, reporter)
      r.unitOpt.foreach {
        case p: AST.TopUnit.Program =>
          gdr.resolveProgram(p)
          if (reporter.hasIssue) {
            report(r, reporter)
            b = false
          }

          if (checkJSON) {
            val s = JSON.from_astTopUnit(p, true)
            //println(s)
            val org.sireum.Either.Left(r) = JSON.to_astTopUnit(s)
            assert(r == p)
          }

          if (checkMsgPack) {
            //val bin = AST.MsgPack.fromTopUnit(p, true)
            val bin = CustomMessagePack.fromTopUnit(p)
            //println(bin.size)
            //val org.sireum.Either.Left(r) = AST.MsgPack.toTopUnit(bin)
            val r = CustomMessagePack.toTopUnit(bin)
            assert(r == p)

          }
        case _ => b = false
      }
    }
    b
  }

  def passing(
    text: String,
    addImport: Boolean = true,
    isWorksheet: Boolean = false,
    isPrelude: Boolean = true,
    checkJson: Boolean = true
  ): Unit =
    assert(passingCheck(text, addImport, isWorksheet, isPrelude, checkJson))

  def failing(
    text: String,
    msg: String,
    addImport: Boolean = true,
    isWorksheet: Boolean = false,
    isPrelude: Boolean = false
  ): Unit =
    assert({
      val reporter = Reporter.create
      val prelude = if (isPrelude) "" else "// #Sireum\n"
      val impor = if (addImport) "import org.sireum._; " else ""
      val r = parse(
        s"$prelude$impor$text",
        isWorksheet,
        reporter
      )
      val b = reporter.issues.elements.exists(_.text.value.contains(msg))
      if (!b) report(r, reporter)
      b
    })

  def failingPos(
    text: String,
    begin: (org.sireum.Z, org.sireum.Z),
    end: (org.sireum.Z, org.sireum.Z),
    addImport: Boolean = true,
    isWorksheet: Boolean = false,
    isPrelude: Boolean = false
  ): Unit =
    assert({
      val reporter = Reporter.create
      val prelude = if (isPrelude) "" else "// #Sireum\n"
      val impor = if (addImport) "import org.sireum._; " else ""
      val r = parse(
        s"$prelude$impor$text",
        isWorksheet,
        reporter
      )
      val b = reporter.issues.elements.exists(
        _.posOpt.exists(
          info =>
            info.beginLine == begin._1 && info.beginColumn == begin._2 &&
              info.endLine == end._1 && info.endColumn == end._2
        )
      )
      if (!b) report(r, reporter)
      b
    })

  def report(r: SlangParser.Result, reporter: Reporter): Unit = {
    reporter.printMessages()
    Console.err.println(r.text)
    Console.err.println(r.unitOpt)
  }

}

import SlangFrontEndTest._

class SlangFrontEndTest extends TestSuite {

  val tests = Tests {

    "Passing" - {

      * - passing("", addImport = false)

      * - passing("import org.sireum._", addImport = false)

      * - passing("package a.b.c; import org.sireum._", addImport = false)

      "Var/Val" - {

        * - passing("val x: Z = 4", isWorksheet = true)

        * - passing("var x: Z = 4", isWorksheet = true)

        * - passing("object Foo { @spec val x: Z = $ }", isWorksheet = true)

        * - passing("object Foo { @spec var x: Z = $ }", isWorksheet = true)

        * - passing("@ext object Foo { var x: Z = 4 }")

        * - passing("@ext object Foo { var x: Z = $ }")

      }

      "Method" - {

        * - passing("def f: Z = {}", isWorksheet = true)

        * - passing("def f(x: Z): Z = {}", isWorksheet = true)

        * - passing("@spec def f(x: Z): Z = $", isWorksheet = true)

        * - passing("@pure def f(x: Z): Z = {}", isWorksheet = true)

        * - passing("def f(x: => Z): Z = {}", isWorksheet = true)

      }

      "Object" - {

        * - passing("object Foo")

        * - passing("object Bar", isWorksheet = true)

        * - passing("object Foo { val x: Z = 10; var y: Z = 11 }")

      }

      "Type" - {

        * - passing("def f(x: ISZ[Z]): Z = {}", isWorksheet = true)

        * - passing("def f(x: A[Z, Z]): Z = {}", isWorksheet = true)

        * - passing("def f(x: (Z, Z)): Z = {}", isWorksheet = true)

        * - passing("def f(x: (Z, Z, (Z))): Z = {}", isWorksheet = true)

        * - passing("def f(x: (Z, Z, (Z, Y))): Z = {}", isWorksheet = true)

        * - passing("def f(x: Z => Z): Z = {}", isWorksheet = true)

        * - passing("def f(x: (Z) => Z): Z = {}", isWorksheet = true)

        * - passing("def f(x: (Z, Z) => Z): Z = {}", isWorksheet = true)

      }

      "Type Param" - {

        * - passing("def f[T](x: T, y: Z): T = {}", isWorksheet = true)

        * - passing("def f[T](x: Z): Z = {}", isWorksheet = true)

      }

      "Ext Object" - {

        * - passing("@ext object Foo")

        "Method" - {

          * - passing("@ext object Foo { def f: Z = $ }")

          * - passing("@ext object Foo { @pure def f: Z = $ }")

          * - passing("@ext object Foo { def f: Z = Contract.Only(Modifies(g)) }")
        }
      }

    }

    "Failing" - {

      val packageFirstMember = "first member of packages"

      * - failing("package a.b.c", packageFirstMember, addImport = false)

      * - failing("package a.b.c; object Foo", packageFirstMember, addImport = false)

      * - failing("object Foo", "first statement", addImport = false)

      "Val/Var" - {

        val illegalStart = "illegal start"

        * - failing("package a; import org.sireum._; val x: Z = 4", illegalStart, addImport = false)

        * - failing("package a; import org.sireum._; var x: Z = 4", illegalStart, addImport = false)

        val dollar = "'$' is only allowed"

        * - failing("val x: Z = $", dollar, isWorksheet = true)

        * - failing("var x: Z = $", dollar, isWorksheet = true)

        val enclosing = "inside methods or code blocks"

        * - failing("object O { val x = 5 }", enclosing)

        * - failing("object O { val x = 5 }", enclosing)

        val multiple = "Cannot define multiple"

        * - failing("val x, y: Z = 5", multiple, isWorksheet = true)

        * - failing("var x, y: Z = 5", multiple, isWorksheet = true)

        * - failing("@spec val x, y: Z = $", multiple, isWorksheet = true)

        * - failing("@spec var x, y: Z = $", multiple, isWorksheet = true)

        val specForm = "should have the form: '@spec"

        * - failing("object Foo { @spec val x: Z = 5 }", specForm, isWorksheet = true)

        * - failing("object Foo { @spec var x: Z = 5 }", specForm, isWorksheet = true)

        * - failing("var x: Z = _", "Uninitialized", isWorksheet = true)

        * - failing("var x = _", "unbound placeholder parameter", isWorksheet = true)

        * - failing("@spec @spec var x: Z = $", "Redundant @spec", isWorksheet = true)

        * - failing("private var x: Z = $", "Only the @spec modifier", isWorksheet = true)
      }

      "Method" - {

        * - failing("@spec def t:foo = Invariant(v == s32\"1\")", "explicit return type", isWorksheet = true)

        * - failing("def f(x: Z)(y: Z): Z = {}", "multiple parameter tuples", isWorksheet = true)

        * - failing("def f(x: Z) = {}", "explicit return type", isWorksheet = true)

        * - failing("def f: Z = 4", "Only block", isWorksheet = true)

        val mod = "modifier @pure"

        * - failing("@ext def f(x: Z): Z = {}", mod, isWorksheet = true)

        val pureSpec = "both @pure and @spec"

        * - failing("@spec @pure def f(x: Z): Z = {}", pureSpec, isWorksheet = true)

        * - failing("@pure @spec def f(x: Z): Z = {}", pureSpec, isWorksheet = true)

        * - failing("@pure @pure def f(x: Z): Z = {}", "Redundant @pure", isWorksheet = true)

        * - failing("@spec @spec def f(x: Z): Z = {}", "Redundant @spec", isWorksheet = true)

        * - failing("@spec def f: Z = 4", "@spec method expression", isWorksheet = true)

        "Param" - {

          val paramTypeForms = "parameter should have the form"

          * - failing("def f(x: Z = 5): Z = {}", paramTypeForms, isWorksheet = true)

          * - failing("def f(x: Z*): Z = {}", "Repeated types", isWorksheet = true)
        }
      }

      "Object" - {

        "Modifier" - {

          val errMsg = "modifiers other than"

          * - failing("final object A", errMsg)

          * - failing("private object A", errMsg)
        }

        * - failing("object A extends { val x: Z = 5 } with B", "have to be of the form")

        * - failing("object A extends B(5)", "have to be of the form")

      }

      "Ext Object" - {

        * - failing("@ext @ext object Foo", "Redundant @ext")

        * - failing("@ext object Foo { def f: Z = 4 }", "extension method expression")

      }

      "Type" - {

        * - failing("@sig trait A { _: B => }", "Self type", isWorksheet = true)

        * - failing("@sig trait A { x: B => }", "Self type", isWorksheet = true)

      }

      "Type Param" - {

        val typeParamForms = "type parameters of the forms"

        * - failing("def f[A, B <: A](x: A, y: Z): B = {}", typeParamForms, isWorksheet = true)

        * - failing("def f[T >: B](x: Z): Z = {}", typeParamForms, isWorksheet = true)

        * - failing("def f[T <% B](x: Z): Z = {}", typeParamForms, isWorksheet = true)

        * - failing("def f[T : TT](x: Z): Z = {}", typeParamForms, isWorksheet = true)

      }
    }
  }
}
