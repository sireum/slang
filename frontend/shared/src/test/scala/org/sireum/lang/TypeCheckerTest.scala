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

package org.sireum.lang

import org.sireum._
import org.sireum.message._
import org.sireum.lang.ast._
import org.sireum.lang.parser._
import org.sireum.lang.symbol._
import org.sireum.lang.tipe._
import org.sireum.test._

class TypeCheckerTest extends TestSuite {

  lazy val typeChecker: TypeChecker = LibraryTypeCheckingTest.tc

  val tests = Tests {

    "unify combine" in {
      val empty = HashMap.empty[String, Typed]
      val left = empty + ("A" ~> Typed.z)
      val right = empty + ("B" ~> Typed.b)
      val both = left + ("B" ~> Typed.b)

      assert(TypeChecker.unifyCombine(empty, right) == Some(right))
      assert(TypeChecker.unifyCombine(left, empty) == Some(left))
      assert(TypeChecker.unifyCombine(left, left) == Some(left))
      assert(TypeChecker.unifyCombine(left, right) == Some(both))
      assert(TypeChecker.unifyCombine(left, empty + ("A" ~> Typed.b)).isEmpty)
    }

    "typed equality, type variables, and substitution" in {
      val typeVar = Typed.TypeVar("T", Typed.VarKind.Immutable)
      val named = Typed.Name(ISZ("A"), None(), ISZ(typeVar, Typed.z))
      val fun = Typed.Fun(Purity.Pure, F, ISZ(named), Typed.b)

      assert(named.isEqual(named))
      assert(!named.isEqual(Typed.Name(ISZ("A"), None(), ISZ(typeVar, Typed.b))))
      assert(fun.isEqual(fun))
      assert(!fun.isEqual(Typed.Fun(Purity.Pure, F, ISZ(Typed.z), Typed.b)))
      assert(named.hasTypeVars)
      assert(fun.hasTypeVars)
      assert(!Typed.Name(ISZ("A"), None(), ISZ(Typed.z)).hasTypeVars)

      val method = ResolvedInfo.Method(T, MethodMode.Method, ISZ("T"), ISZ("A"), "f", ISZ("x"),
        Some(Typed.Fun(Purity.Pure, F, ISZ(typeVar), typeVar)), ISZ(), ISZ(), None())
      val substMap = HashMap.empty[String, Typed] + ("T" ~> Typed.z)
      val Some(substed: ResolvedInfo.Method) = ResolvedInfo.substOpt(Some(method), substMap)
      assert(substed.tpeOpt == Some(Typed.Fun(Purity.Pure, F, ISZ(Typed.z), Typed.z)))
    }

    "generic member substitution" in {
      passingWorksheet(
        """import org.sireum._
          |@sig trait Box[T] {
          |  def value: T
          |}
          |def f(box: Box[Z]): Z = {
          |  return box.value
          |}""".stripMargin)
    }

    "Passing" - {

      "Worksheet" - {

        "parallel object checking preserves serial results" in {
          val methods = (0 until 16).map(i => s"def f$i(x: Z): Z = { return x + $i }").mkString("\n")
          val reporter = Reporter.create
          val Some(program) = Parser(s"import org.sireum._\nobject A {\n$methods\n}").
            parseTopUnit[TopUnit.Program](isWorksheet = T, isDiet = F, None(), reporter)
          assert(!reporter.hasIssue)

          val serialReporter = Reporter.create
          val serial = FrontEnd.checkWorksheet(1, Some(typeChecker.typeHierarchy), program, serialReporter)
          val parallelReporter = Reporter.create
          val parallel = FrontEnd.checkWorksheet(4, Some(typeChecker.typeHierarchy), program, parallelReporter)

          assert(serialReporter.messages == parallelReporter.messages)
          assert(serial == parallel)
        }

        * - passingWorksheet(
          """import org.sireum._
            |
            |object A {
            |  @spec def aFact = Fact(
            |    T
            |  )
            |
            |  @spec def aTheorem = Theorem(
            |    T
            |  )
            |
            |  @pure def amTheorem(): Unit = {
            |    Contract(
            |      Ensures(T)
            |    )
            |  }
            |
            |  @rw val a = RS(aFact _, aTheorem _)
            |  @rw val b: RS = a + RS(amTheorem _)
            |  @rw val C = b + ~RS(amTheorem _)
            |}""".stripMargin
        )

        * - passingWorksheet(
          """import org.sireum._
            |@sig trait Base {
            |  def name: String
            |}
            |
            |@sig trait U {
            |  def uName: String
            |}
            |
            |@datatype class Ai (val name: String,
            |                    val uName: String) extends Base with U
            |
            |@datatype class Bi (val name: String) extends Base
            |
            |val bases: ISZ[Base] = ISZ(Ai("a", "au"), Bi("b"))
            |
            |for(b <- bases) {
            |  if(b.isInstanceOf[U]) {
            |    println(s"${b} is a U")
            |  } else {
            |    println(s"${b} not is a U")
            |  }
            |}
            |
            |for(b <- bases) {
            |  b match {
            |    case u: U => println(s"${u} is a U")
            |    case _ => println(s"${b} is not a U")
            |  }
            |}
            |""".stripMargin)


        * - passingWorksheet(
          """import org.sireum._
            |object Foo {
            |  val x: ZS = ZS(1, 2, 3)
            |  val y: ZS = ZS(4, 5, 6)
            |}
            |def foo(s1: MSZ[Z], s2: MSZ[Z]): Unit = {}
            |
            |foo(Foo.x, Foo.y)
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |
            |@datatype trait Foo {
            |  @pure def id: String
            |}
            |
            |def bar(foo: Foo): Unit = {
            |  Contract(Requires(foo.id != ""))
            |}""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |val a = ISZ(1, 2, 3)
            |for (e <- a) {
            |  Invariant(
            |    (Idx[Z](e) != a.size - 1) ->: (e < a(Idx[Z](e) + 1))
            |  )
            |}""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |@spec def bar(x: Z): Z = $
            |@spec def barAx1 = Fact(
            |  ∀{x: Z => (x >= 0) ->: (bar(x) >= 0)}
            |)""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |@msig trait F
            |@record class Foo(x: Z) extends F
            |val f: F = Foo(4)
            |f match {
            |  case f: Foo =>
            |}""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |def foo[T](opt: Option[T]): String = {
            |  return opt.string
            |}
            |foo(Some("a"))""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |@datatype trait Or[L, R]
            |@datatype class Left[L, R](value: L) extends Or[L, R]
            |@datatype class Right[L, R](value: R) extends Or[L, R]
            |val left = Left[Z, B](5)
            |val or = left.asInstanceOf[Or[Z, B]]
            |val left2 = or.asInstanceOf[Right[Z, B]]
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |@datatype trait Or[L, R]
            |@datatype class Left[L, R](value: L) extends Or[L, R]
            |@datatype class Right[L, R](value: R) extends Or[L, R]
            |val or: Or[Z, B] = Left(5)
            |or match {
            |  case Left(n) => assert(n == 5)
            |  case Right(b) => halt("Impossible")
            |}
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |@enum object Three {
            |  'One
            |  'Two
            |  'Three
            |}
            |val twoOpt: Option[Three.Type] = Three.byOrdinal(1)
            |assert(twoOpt == Some(Three.Two))
            |val threeOpt: Option[Three.Type] = Three.byName("Three")
            |assert(threeOpt == Some(Three.Three))
            |assert(Three.One.ordinal == 0)
            |assert(Three.Two.name == "Two")
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |object L1 {
            |  @enum object E { 'A }
            |  object L2 {
            |    def bar(): Unit = {
            |      def foo(e: E.Type): Unit = {
            |        e match {
            |          case E.A =>
            |        }
            |      }
            |    }
            |  }
            |}
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |
            |object FooLike {
            |  @sig trait T {
            |    def fooLike(): Z = {
            |      return 10
            |    }
            |  }
            |}
            |
            |@datatype class Foo(x: Z) extends FooLike.T {
            |  @pure def foo(): Z = {
            |    return x
            |  }
            |}
            |
            |@msig trait BarLike {
            |  @pure def bar: Z = {
            |    return Z.random
            |  }
            |}
            |
            |@record class Bar(var x: Z, foo: Foo) extends BarLike
            |
            |val bar = Bar(4, Foo(3))
            |bar.x = bar.bar
            |assert(bar.foo.foo() != bar.foo.fooLike())
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |@pure def foo(x: Z): Z = { return x + 1 }
            |assert(foo(4) > 4)
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |val b = Bag.empty[Z] + 1 +# 2 ~> 3
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |val b = Bag ++ ISZ(1, 2, 3)
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |val zs: ZS = ZS(1, 2, 3).map[Z](x => x + 1).map(x => x + 2)
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |import org.sireum.Z8._
            |val z8s: ISZ[Z8] = ISZ(z8"1", z8"2", z8"3").map((x: Z8) => x + z8"1")
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |val zs: ZS = ZS(1, 2, 3).map[Z](x => x + 1)
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |val bs: MSZ[MSZ[B]] = for (s <- MSZ(ZS(0), ZS(1), ZS(3)); n <- s) yield MSZ(n == 0)
            |bs(0)(1) = bs(1)(0)
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |val bs: ISZ[Z] = for (s <- for (i <- 0 until 10) yield ISZ(i); n <- s if n % 2 == 1) yield n
            |""".stripMargin
        )

        * - passingWorksheet(
          """import org.sireum._
            |val bs: MS[Z8, B] = for (b <- MS[Z8, B](T, F)) yield b
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |val bs: ISZ[B] = for (b <- ISZ(T, F)) yield b
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |for (i <- 0 until 10 if i % 2 == 0; j <- i until 10) {
            |  assert(j >= i)
            |}
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |val xOpt: Option[B] = if (B.random) Some(T) else None()
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |assert((if (B.random) Some(T) else None[B]()).nonEmpty)
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |val xOpt: Option[Z] = Some(4)
            |val Some(x) = xOpt
            |assert(x > 0)
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |val poset = Poset[String](HashSMap.empty, ISZ(), HashSMap.empty, HashSMap.empty)
            |val Poset(_, _, parents, children) = poset
            |val parentsTyped: HashSMap[Z, HashSSet[Z]] = parents
            |val childrenTyped: HashSMap[Z, HashSSet[Z]] = children
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |val poset = Poset[String](HashSMap.empty, ISZ(), HashSMap.empty, HashSMap.empty)
            |poset match {
            |  case Poset(_, _, parents, _) if parents.nonEmpty =>
            |    val psTyped: HashSMap[Z, HashSSet[Z]] = parents
            |  case _ =>
            |}
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |ISZ(1, 2, 3) match {
            | case IS(n1, n2, n3, _*) =>
            | case _ => halt("impossible")
            |}
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |val x: Option[Z] = None()
            |x match {
            | case Some(_) => halt("impossible")
            | case _ =>
            |}
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |5 match {
            | case z"1" => halt("impossible")
            | case 10 => halt("impossible")
            | case _ =>
            |}
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |"abc".native match {
            | case "abcd" => halt("impossible")
            | case _ =>
            |}
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |'A' match {
            | case c"A" =>
            | case _ => halt("impossible")
            |}
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |val poset = Poset[String](HashSMap.empty, ISZ(), HashSMap.empty, HashSMap.empty)
            |val upPoset = poset(parents = poset.parents + 3 ~> (HashSSet ++ ISZ(1, 2, 3)))
            |val upPosetTyped: Poset[String] = upPoset
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |val poset = Poset[String](HashSMap.empty, ISZ(), HashSMap.empty, HashSMap.empty)
            |val upPoset = poset(parents = poset.parents + 3 ~> HashSSet.empty[Z])
            |val upPosetTyped: Poset[String] = upPoset
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |val a = ISZ(1, 2, 3)
            |val first = a(0)
            |val firstTyped: Z = first
            |val upFirst = a(0 ~> 5)
            |val upFirstTyped: ISZ[Z] = upFirst
            |val upAll = a(0 ~> 5, 1 ~> 6, 2 ~> 7)
            |val upAllTyped: IS[Z, Z] = upAll
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |val a = ZS(1, 2, 3)
            |val first = a(0)
            |val firstTyped: Z = first
            |val upFirst = a(0 ~> 5)
            |val upFirstTyped: ZS = upFirst
            |val upAll = a(0 ~> 5, 1 ~> 6, 2 ~> 7)
            |val upAllTyped: MS[Z, Z] = upAll
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |val x: Either[B, Z] = Either.Left(T)
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |val x: ISZ[Z] = IS[Z, Z](1, 2, 3)
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |val x: MEither[B, Z] = MEither.Right(value = 5)
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |println((Map.empty[String, Z] + "A" ~> 1).get(key = "B").getOrElse(default = 0))
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |println(Z("0").getOrElse(1))
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |val x: Either[B, Z] = Either.Left(T)
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |println((Map.empty[String, Z] + "A" ~> 1).get("B").getOrElse(0))
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |import org.sireum.F32._
            |val x: F32 = f32"0.0"
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |var x: Z = 1
            |while (x > 0) {
            |  println("x is positive")
            |  x = 0
            |}
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |val x: Z = 1
            |if (x > 0) {
            |  println("x is positive")
            |}
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |val x: Z = 1
            |val y = x + 1
            |assert(y > x)
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |assert(3 > 0)
            |""".stripMargin)

        * - passingWorksheet(
          """import org.sireum._
            |def foo: Unit = { ISZ(1).map((x: Z) => { x + 1 }) }""".stripMargin)

      }

      "LubGlb" - {

        "subtype equality None vs Option in Ensures" in {
          passingWorksheet(
            """import org.sireum._
              |@pure def foo[T2](): Option[T2] = {
              |  Contract(Ensures(None[T2]() ≡ Res))
              |  return None()
              |}""".stripMargin)
        }

        "subtype equality MNone vs MOption" in {
          passingWorksheet(
            """import org.sireum._
              |def f[T](x: MOption[T], y: MNone[T]): B = {
              |  return x == y
              |}""".stripMargin)
        }

        "same enum equality" in {
          passingWorksheet(
            """import org.sireum._
              |@enum object Colour {
              |  "Red"
              |  "Green"
              |}
              |def f(c1: Colour.Type, c2: Colour.Type): B = {
              |  return c1 == c2
              |}
              |assert(f(Colour.Red, Colour.Green) || T)""".stripMargin)
        }

        "if-else lub of sibling datatypes" in {
          passingWorksheet(
            """import org.sireum._
              |@datatype trait Base
              |@datatype class Ai() extends Base
              |@datatype class Bi() extends Base
              |assert((if (B.random) Ai() else Bi()).string.size >= 0)""".stripMargin)
        }

        "if-else lub of Some and None" in {
          passingWorksheet(
            """import org.sireum._
              |assert((if (B.random) Some(z"1") else None[Z]()).nonEmpty || T)""".stripMargin)
        }

        "asInstanceOf downcast to subclass" in {
          passingWorksheet(
            """import org.sireum._
              |@datatype trait Base
              |@datatype class Ai(val n: Z) extends Base
              |val b: Base = Ai(1)
              |val a = b.asInstanceOf[Ai]
              |assert(a.n == 1)""".stripMargin)
        }

        "asInstanceOf upcast to supertype" in {
          passingWorksheet(
            """import org.sireum._
              |@datatype trait Base
              |@datatype class Ai(val n: Z) extends Base
              |val a = Ai(1)
              |val b = a.asInstanceOf[Base]""".stripMargin)
        }

        "asInstanceOf generic downcast" in {
          passingWorksheet(
            """import org.sireum._
              |@datatype trait Or[L, R]
              |@datatype class Left[L, R](value: L) extends Or[L, R]
              |@datatype class Right[L, R](value: R) extends Or[L, R]
              |val or: Or[Z, B] = Left(5)
              |val left = or.asInstanceOf[Left[Z, B]]
              |assert(left.value == 5)""".stripMargin)
        }

        "asInstanceOf of IS with different element types" in {
          passingWorksheet(
            """import org.sireum._
              |@datatype trait Base
              |@datatype class Ai() extends Base
              |val xs: ISZ[Ai] = ISZ(Ai())
              |val ys: ISZ[Base] = xs.asInstanceOf[ISZ[Base]]""".stripMargin)
        }

        "match and asInstanceOf of independent traits with a common subtype" in {
          passingWorksheet(
            """import org.sireum._
              |@sig trait TA
              |@sig trait TB
              |@datatype class TC() extends TA with TB
              |def f(a: TA): Z = {
              |  a match {
              |    case _: TB => return 1
              |    case _ => return 0
              |  }
              |}
              |def g(a: TA): TB = {
              |  return a.asInstanceOf[TB]
              |}""".stripMargin)
        }

        "Z equality with Z" in {
          passingWorksheet(
            """import org.sireum._
              |def f(x: Z, y: Z): B = {
              |  return x == y
              |}
              |assert(f(z"1", z"1"))""".stripMargin)
        }

      }

      "Stmt" - {

        * - passingStmt("assert(true)")

        * - passingStmt("assume(1 + 3 > 2)")

        * - passingStmt("""assert(1 + 3 > 2, "ok")""")

        * - passingStmt("""println(1, 2, "a", "b")""")

        * - passingStmt("""eprintln(1, 2, "a", "b")""")

        * - passingStmt("""print(1, 2, "a", "b")""")

        * - passingStmt("""eprint(1, 2, "a", "b")""")

        * - passingStmt("val x: org.sireum.Z = 1")

        * - passingStmt("assert(org.sireum.T)")

        * - passingStmt("assert(!org.sireum.F)")

        * - passingStmt("""halt("foo")""")
      }
    }

    "Failing" - {

      "Worksheet" - {

        * - failingWorksheet(
          """import org.sireum._
            |val x = 3
            |val (x, y) = (2, 3)""".stripMargin, "the identifier has already been previously declared")

        * - failingWorksheet(
          """import org.sireum._
            |import org.sireum.U32._
            |import org.sireum.S32._
            |assert(u32"0" < s32"0")""".stripMargin, "Incompatible")

        * - failingWorksheet(
          """import org.sireum._
            |val template: ST = st"abc"
            |assert(template == template)""".stripMargin, "ST values do not support equality")

        * - failingWorksheet(
          """import org.sireum._
            |val text: String = "abc"
            |val template: ST = st"abc"
            |assert(text === template)""".stripMargin, "ST values do not support equality")

        * - failingWorksheet(
          """import org.sireum._
            |val text: String = "abc"
            |val template: ST = st"abc"
            |assert(template ≡ text)""".stripMargin, "ST values do not support equality")

        * - failingWorksheet(
          """import org.sireum._
            |val text: String = "abc"
            |val template: ST = st"abc"
            |assert(text != template)""".stripMargin, "ST values do not support equality")

        * - failingWorksheet(
          """import org.sireum._
            |val text: String = "abc"
            |val template: ST = st"abc"
            |assert(template =!= text)""".stripMargin, "ST values do not support equality")

        * - failingWorksheet(
          """import org.sireum._
            |val text: String = "abc"
            |val template: ST = st"abc"
            |assert(text ≢ template)""".stripMargin, "ST values do not support equality")

        * - failingWorksheet(
          """import org.sireum._
            |def foo(lits: ISZ[String], i: Z): Unit = {
            |  ISZ(ops.StringOps(lits(i).value).escapeST)
            |}""".stripMargin, "'value' is not a member")

        * - failingWorksheet(
          """import org.sireum._
            |@record class Bar(var y: MSZ[Baz]) {
            |  def foo(b: Baz): Unit = {}
            |}
            |@record class Baz()
            |object Foo {
            |  val x: MSZ[Bar] = MSZ(Bar(MSZ(Baz())), Bar(MSZ(Baz(), Baz())))
            |}
            |
            |Foo.x(1).foo(Foo.x(0).y(0))
            |""".stripMargin, "Cannot pass a mutable object")

        * - failingWorksheet(
          """import org.sireum._
            |@record class Bar(var y: MSZ[Baz])
            |@record class Baz()
            |object Foo {
            |  val x: MSZ[Bar] = MSZ(Bar(MSZ(Baz())), Bar(MSZ(Baz(), Baz())))
            |}
            |def foo(b1: Baz, b2: Bar): Unit = {}
            |
            |foo(Foo.x(0).y(0), Foo.x(1))
            |""".stripMargin, "Cannot pass a mutable object")

        * - failingWorksheet(
          """import org.sireum._
            |@record class Bar(var y: MSZ[Baz])
            |@record class Baz()
            |object Foo {
            |  val x: MSZ[Bar] = MSZ(Bar(MSZ(Baz())), Bar(MSZ(Baz(), Baz())))
            |}
            |def foo(b1: Baz, b2: Bar): Unit = {}
            |
            |foo(Foo.x(0).y(0), Foo.x(1))
            |""".stripMargin, "Cannot pass a mutable object")

        * - failingWorksheet(
          """import org.sireum._
            |@record class Bar()
            |object Foo {
            |  val x: MSZ[Bar] = MSZ(Bar(), Bar())
            |}
            |def foo(b1: Bar, b2: Bar): Unit = {}
            |
            |foo(Foo.x(0), Foo.x(1))
            |""".stripMargin, "Cannot pass a mutable object")

        * - failingWorksheet(
          """import org.sireum._
            |@record class Bar
            |object Foo {
            |  val x: MSZ[Bar] = MSZ(Bar(), Bar())
            |}
            |def foo(s: MSZ[Bar], b: Bar): Unit = {}
            |
            |foo(Foo.x, Foo.x(0))
            |""".stripMargin, "Cannot pass a mutable object")

        * - failingWorksheet(
          """import org.sireum._
            |@record class Bar()
            |object Foo {
            |  val x: MSZ[Bar] = MSZ(Bar(), Bar())
            |}
            |def foo(b: Bar, s: MSZ[Bar]): Unit = {}
            |
            |foo(Foo.x(0), Foo.x)
            |""".stripMargin, "Cannot pass a mutable object")

        * - failingWorksheet(
          """import org.sireum._
            |object Foo {
            |  val x: ZS = ZS(1, 2, 3)
            |}
            |def foo(s1: MSZ[Z], s2: MSZ[Z]): Unit = {}
            |
            |foo(Foo.x, Foo.x)
            |""".stripMargin, "Cannot pass a mutable object")

        * - failingWorksheet(
          """import org.sireum._
            |val p = ZS(1, 2, 3)
            |def foo(s1: MSZ[Z], s2: MSZ[Z]): Unit = {}
            |
            |foo(p, p)
            |""".stripMargin, "Cannot pass a mutable object")

        * - failingWorksheet(
          """import org.sireum._
            |
            |@datatype trait Foo {
            |  def id: String
            |}
            |
            |def bar(foo: Foo): Unit = {
            |  Contract(Requires(foo.id != ""))
            |}""".stripMargin, "non-pure")

        * - failingWorksheet(
          """import org.sireum._
            |@record class Foo
            |val a: Option[Foo] = Some(Foo())""".stripMargin, "mutable")

        * - failingWorksheet(
          """import org.sireum._
            |@msig trait F
            |@record class Foo(x: Z) extends F
            |val f: F = Foo(4)
            |f match {
            |  case f2: Foo =>
            |}""".stripMargin, "mutable")

        * - failingWorksheet(
          """import org.sireum._
            |@pure def foo(): Z = { return 4 }
            |assert(foo - 4)
            |""".stripMargin, "eta")

        * - failingWorksheet(
          """import org.sireum._
            |@pure def foo(x: Z): Z = { return }
            |assert(foo(4) > 4)
            |""".stripMargin, "none found")

        * - failingWorksheet(
          """import org.sireum._
            |@pure def foo(x: Z): Unit = { return x + 1 }
            |foo(4)
            |""".stripMargin, "Expecting type 'Unit'")

        * - failingWorksheet(
          """import org.sireum._
            |val zs = ZS(1, 2, 3).map(x => x + 1)
            |""".stripMargin, "Explicit type")

        * - failingWorksheet(
          """import org.sireum._
            |val x: Option[Z] = None()
            |x match {
            | case Some(_: Z) => halt("impossible")
            | case _ =>
            |}
            |""".stripMargin, "Unnecessary")

        * - failingWorksheet(
          """import org.sireum._
            |val poset = Poset[String](HashSMap.empty, ISZ(), HashSMap.empty, HashSMap.empty)
            |val upPoset = poset(parents = poset.parents + 3 ~> (HashSSet.empty ++ ISZ(1, 2, 3)))
            |val upPosetTyped: Poset[String] = upPoset
            |""".stripMargin, "Explicit type")

        * - failingWorksheet(
          """import org.sireum._
            |val x: MEither[B, Z] = MEither.Right(value = T)
            |""".stripMargin, "but 'B'")

        * - failingWorksheet(
          """import org.sireum._
            |val x = Either.Left(T)
            |""".stripMargin, "type parameter(s): 'R'")

        * - failingWorksheet(
          """import org.sireum._
            |println(Z(s = "0").getOrElse(1))
            |""".stripMargin, "Could not find parameter 's'")

        * - failingWorksheet(
          """import org.sireum._
            |val m: HashSMap[String, Z] = HashSMap.empty[String, Z]()
            |""".stripMargin, "does NOT take parens")

      }

      "LubGlb" - {

        "unrelated enum equality" in {
          failingWorksheet(
            """import org.sireum._
              |@enum object Colour {
              |  "Red"
              |  "Green"
              |}
              |@enum object Size {
              |  "Small"
              |  "Large"
              |}
              |def f(c: Colour.Type, s: Size.Type, p: B): B = {
              |  return (c == s) && p
              |}""".stripMargin, "Incompatible")
        }

        "Z equality with B" in {
          failingWorksheet(
            """import org.sireum._
              |def f(x: Z, y: B, p: B): B = {
              |  return (x == y) && p
              |}""".stripMargin, "Incompatible")
        }

        "Z equality with String" in {
          failingWorksheet(
            """import org.sireum._
              |def f(x: Z, y: String): B = {
              |  return x == y
              |}""".stripMargin, "Incompatible")
        }

        "Z equality with F32" in {
          failingWorksheet(
            """import org.sireum._
              |def f(x: Z, y: F32): B = {
              |  return x == y
              |}""".stripMargin, "Incompatible")
        }

        "unrelated datatype equality" in {
          failingWorksheet(
            """import org.sireum._
              |@datatype class P(val n: Z)
              |@datatype class Q(val n: Z)
              |def f(p: P, q: Q): B = {
              |  return p == q
              |}""".stripMargin, "Incompatible")
        }

        "enum equality with Z" in {
          failingWorksheet(
            """import org.sireum._
              |@enum object Colour {
              |  "Red"
              |}
              |def f(c: Colour.Type, z: Z): B = {
              |  return c == z
              |}""".stripMargin, "Incompatible")
        }

        "if-else lub of Some[Z] and None[B]" in {
          failingWorksheet(
            """import org.sireum._
              |assert((if (B.random) Some(z"1") else None[B]()).nonEmpty)""".stripMargin, "common ancestor")
        }

        "if-else lub of unrelated datatypes" in {
          failingWorksheet(
            """import org.sireum._
              |@datatype class P()
              |@datatype class Q()
              |assert((if (B.random) P() else Q()).string.size >= 0)""".stripMargin, "common ancestor")
        }

        "fruitless asInstanceOf Z to B" in {
          failingWorksheet(
            """import org.sireum._
              |def f(x: Z): B = {
              |  return x.asInstanceOf[B]
              |}""".stripMargin, "Fruitless")
        }

        "fruitless asInstanceOf unrelated datatypes" in {
          failingWorksheet(
            """import org.sireum._
              |@datatype class P()
              |@datatype class Q()
              |def f(p: P): Q = {
              |  return p.asInstanceOf[Q]
              |}""".stripMargin, "Fruitless")
        }

        "Option[Z] equality with Option[B]" in {
          failingWorksheet(
            """import org.sireum._
              |def f(x: Option[Z], y: Option[B]): B = {
              |  return x == y
              |}""".stripMargin, "Incompatible")
        }

      }

      "Stmt" - {

        * - failingStmt("assert(1)", "but 'Z' found")

        * - failingStmt("""halt(1)""", "but 'Z' found")

        * - failingStmt("""println(org.sireum.Map.empty)""", "Explicit type")

        * - failingStmt("""println(org.sireum.Map.empty[org.sireum.Z])""", "Expecting 2 type arg")

      }
    }
  }

  def passingStmt(input: Predef.String): Unit =
    assert(testStmt(input, isPassing = true))

  def failingStmt(input: Predef.String, msg: Predef.String): Unit =
    assert(testStmt(input, isPassing = false, msg))

  def passingWorksheet(input: Predef.String): Unit =
    assert(testWorksheet(input, isPassing = true))

  def failingWorksheet(input: Predef.String, msg: Predef.String): Unit =
    assert(testWorksheet(input, isPassing = false, msg))

  def testWorksheet(input: Predef.String, isPassing: Boolean, msg: Predef.String = ""): Boolean = {
    val reporter = Reporter.create
    Parser(input).parseTopUnit[TopUnit.Program](isWorksheet = T, isDiet = F, None(), reporter) match {
      case Some(program) if !reporter.hasIssue =>
        val p = FrontEnd.checkWorksheet(0, Some(typeChecker.typeHierarchy), program, reporter)
        if (reporter.hasIssue) {
          if (isPassing) {
            reporter.printMessages()
            return false
          } else {
            return reporter.messages.elements.exists(_.text.value.contains(msg))
          }
        }
        assert(isPassing)
        PostTipeAttrChecker.checkProgram(p._2, reporter)
      case _ =>
    }
    if (reporter.hasIssue) {
      reporter.printMessages()
    }
    !reporter.hasIssue
  }

  def testStmt(input: Predef.String, isPassing: Boolean, msg: Predef.String = ""): Boolean = {
    val stmt = Parser(input).parseStmt[Stmt]
    val scope = Scope.Local.create(HashMap.empty, Scope.Global(ISZ(), ISZ(), ISZ()))
    val reporter = Reporter.create
    typeChecker.checkStmt(scope, stmt, reporter) match {
      case checkedStmt if isPassing =>
        if (reporter.hasIssue) {
          reporter.printMessages()
          return false
        }
        PostTipeAttrChecker.checkStmt(checkedStmt, reporter)
        reporter.printMessages()
        !reporter.hasIssue
      case _ =>
        !isPassing && reporter.errors.elements.exists(_.text.value.contains(msg))
    }
  }

  def errIf(b: Boolean): Unit = {
    if (b)
      new Throwable().printStackTrace()
    assert(!b)
  }
}
