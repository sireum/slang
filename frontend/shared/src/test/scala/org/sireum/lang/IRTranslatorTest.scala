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
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sireum.lang

import org.sireum._
import org.sireum.lang.ast.{IR, IRTransformer, Stmt, Typed}
import org.sireum.lang.parser.Parser
import org.sireum.message.Reporter
import org.sireum.test._
import org.sireum.{String => SString}

class IRTranslatorTest extends TestSuite {

  def checked(input: String): (tipe.TypeHierarchy, ast.TopUnit.Program) = {
    val reporter = Reporter.create
    val Some(program) = Parser(input).parseTopUnit[ast.TopUnit.Program](T, F, None(), reporter)
    assert(!reporter.hasIssue)
    val r = FrontEnd.checkWorksheet(0, Some(LibraryTypeCheckingTest.tc.typeHierarchy), program, reporter)
    assert(!reporter.hasIssue)
    return r
  }

  def findMethod(program: ast.TopUnit.Program, typeId: String, methodId: String): (Stmt.Adt, Stmt.Method) = {
    for (stmt <- program.body.stmts) {
      stmt match {
        case adt: Stmt.Adt if adt.id.value == typeId =>
          for (member <- adt.stmts) {
            member match {
              case method: Stmt.Method if method.sig.id.value == methodId => return (adt, method)
              case _ =>
            }
          }
        case _ =>
      }
    }
    halt(st"Unable to find $typeId.$methodId")
  }

  def translated(input: String, typeId: String, methodId: String): (IRTranslator, IR.Procedure) = {
    val (th, program) = checked(input)
    val (adt, method) = findMethod(program, typeId, methodId)
    val receiverType = Typed.Name(ISZ(typeId), None(), ISZ())
    val translator = IRTranslator(F, F, (_: IR.Exp) => F, th, IRTranslator.createFresh)
    val procedure = translator.translateMethod(F, Some(receiverType), ISZ(typeId), method)
    return (translator, procedure)
  }

  def hasNestedMethod(input: String, typeId: String, methodId: String): B = {
    val (_, program) = checked(input)
    val (_, method) = findMethod(program, typeId, methodId)
    method.bodyOpt match {
      case Some(body) =>
        for (stmt <- body.stmts) {
          stmt match {
            case _: Stmt.Method => return T
            case _ =>
          }
        }
      case _ =>
    }
    return F
  }

  def returnStmt(procedure: IR.Procedure): IR.Stmt.Return = {
    val block = procedure.body.asInstanceOf[IR.Body.Block].block
    for (stmt <- block.stmts) {
      stmt match {
        case ret: IR.Stmt.Return => return ret
        case _ =>
      }
    }
    halt("Unable to find return statement")
  }

  def findSuper(exp: IR.Exp): Option[IR.Exp.Apply] = {
    exp match {
      case apply: IR.Exp.Apply if apply.isSuper => return Some(apply)
      case apply: IR.Exp.Apply =>
        for (arg <- apply.args) {
          findSuper(arg) match {
            case Some(result) => return Some(result)
            case _ =>
          }
        }
      case binary: IR.Exp.Binary =>
        findSuper(binary.left) match {
          case Some(result) => return Some(result)
          case _ =>
        }
        findSuper(binary.right) match {
          case Some(result) => return Some(result)
          case _ =>
        }
      case construct: IR.Exp.Construct =>
        for (arg <- construct.args) {
          findSuper(arg) match {
            case Some(result) => return Some(result)
            case _ =>
          }
        }
      case _ =>
    }
    return None()
  }

  def firstSuper(procedure: IR.Procedure): IR.Exp.Apply = {
    returnStmt(procedure).expOpt match {
      case Some(exp) =>
        findSuper(exp) match {
          case Some(result) => return result
          case _ =>
        }
      case _ =>
    }
    halt("Unable to find direct super apply")
  }

  def assertTempPrefix(defs: ISZ[IR.Stmt.Assign.Temp], exps: ISZ[IR.Exp]): Unit = {
    var defined = ISZ[Z]()
    val checker = new IRTransformer.PrePost[ISZ[Z]] {
      override def string: org.sireum.String = org.sireum.String("IRTranslatorTest.TempChecker")

      override def preIRExpTemp(ctx: ISZ[Z], o: IR.Exp.Temp): IRTransformer.PreResult[ISZ[Z], IR.Exp] = {
        assert(ops.ISZOps(ctx).contains(o.n), st"Temporary $o is used before its definition".render)
        return IRTransformer.PreResult(ctx, T, None())
      }
    }
    val transformer = new IRTransformer[ISZ[Z]](checker)
    for (temp <- defs) {
      assert(!ops.ISZOps(defined).contains(temp.lhs), st"Temporary ${temp.lhs} is redefined in one prefix".render)
      transformer.transformIRExp(defined, temp.rhs)
      defined = defined :+ temp.lhs
    }
    for (exp <- exps) {
      transformer.transformIRExp(defined, exp)
    }
  }

  def assertGeneratorTemps(procedure: IR.Procedure): Unit = {
    val block = procedure.body.asInstanceOf[IR.Body.Block].block
    var pending = ISZ[IR.Stmt.Assign.Temp]()
    var found = 0
    for (stmt <- block.stmts) {
      stmt match {
        case temp: IR.Stmt.Assign.Temp => pending = pending :+ temp
        case loop: IR.Stmt.For =>
          val exps: ISZ[IR.Exp] = loop.range match {
            case range: IR.Stmt.For.Range.Expr => ISZ(range.exp)
            case range: IR.Stmt.For.Range.Step =>
              var r = ISZ[IR.Exp](range.start, range.end)
              range.byOpt match {
                case Some(by) => r = r :+ by
                case _ =>
              }
              r
          }
          assertTempPrefix(pending, exps)
          loop.condOpt match {
            case Some(cond) =>
              var condDefs = ISZ[IR.Stmt.Assign.Temp]()
              for (condStmt <- cond.stmts) {
                condStmt match {
                  case temp: IR.Stmt.Assign.Temp => condDefs = condDefs :+ temp
                  case _ =>
                }
              }
              assertTempPrefix(condDefs, ISZ(cond.exp))
            case _ =>
          }
          found = found + 1
          pending = ISZ()
        case _ => pending = ISZ()
      }
    }
    assert(found == 2)
  }

  registerTest("super method calls carry direct dispatch metadata") {
      val input =
        """import org.sireum._
          |@msig trait Parent {
          |  @pure def value(x: Z, y: B): Z = { return x }
          |}
          |@record class Child() extends Parent {
          |  @pure override def value(x: Z, y: B): Z = {
          |    return super.value(y = y, x = x) + helper()
          |  }
          |  @pure def helper(): Z = { return 22 }
          |}""".stripMargin
      val reporter = Reporter.create
      val Some(program) = Parser(input).parseTopUnit[ast.TopUnit.Program](T, F, None(), reporter)
      assert(!reporter.hasIssue)
      val (th, checkedProgram) = FrontEnd.checkWorksheet(0, Some(LibraryTypeCheckingTest.tc.typeHierarchy), program, reporter)
      assert(!reporter.hasIssue)

      var childOpt: Option[Stmt.Adt] = None()
      for (stmt <- checkedProgram.body.stmts) {
        stmt match {
          case child: Stmt.Adt if child.id.value.value == "Child" => childOpt = Some(child)
          case _ =>
        }
      }
      val child = childOpt.get
      var methodOpt: Option[Stmt.Method] = None()
      for (stmt <- child.stmts) {
        stmt match {
              case method: Stmt.Method if method.sig.id.value.value == "value" => methodOpt = Some(method)
          case _ =>
        }
      }
      val method = methodOpt.get
      val childType = Typed.Name(ISZ(SString("Child")), None(), ISZ())
      val procedure = IRTranslator(F, F, (_: IR.Exp) => F, th, IRTranslator.createFresh).
        translateMethod(F, Some(childType), ISZ(SString("Child")), method)
      val block = procedure.body.asInstanceOf[IR.Body.Block].block
      var returnOpt: Option[IR.Stmt.Return] = None()
      for (stmt <- block.stmts) {
        stmt match {
          case ret: IR.Stmt.Return => returnOpt = Some(ret)
          case _ =>
        }
      }
      val sum = returnOpt.get.expOpt.get.asInstanceOf[IR.Exp.Binary]
      val superApply = sum.left.asInstanceOf[IR.Exp.Apply]
      val ordinaryApply = sum.right.asInstanceOf[IR.Exp.Apply]
      assert(superApply.isSuper)
      assert(!ordinaryApply.isSuper)
      assert(!superApply.isInObject)
      assert(superApply.owner == ISZ(SString("Parent")))
      assert(superApply.id.value == "value")
      assert(superApply.args.size == 3)
      assert(superApply.methodType.args.size == 3)
      assert(superApply.methodType.args(0) == Typed.Name(ISZ(SString("Parent")), None(), ISZ()))
      assert(superApply.methodType.args(1) == Typed.z)
      assert(superApply.methodType.args(2) == Typed.b)
      assert(superApply.args(0).tipe == childType)
      assert(superApply.args(1).tipe == Typed.z)
      assert(superApply.args(2).tipe == Typed.b)
      assert(ordinaryApply.owner == ISZ(SString("Child")))
      assert(ordinaryApply.id.value == "helper")
      assert(ordinaryApply.args.size == 1)
      assert(!ordinaryApply.isInObject)
      assert(ops.StringOps(superApply.prettyST(IR.Printer.Empty()).render).startsWith("super.value"))
  }

  registerTest("nested super method calls capture this") {
    val input =
      """import org.sireum._
        |@msig trait Parent {
        |  @pure def value(): Z = { return 20 }
        |}
        |@record class Child() extends Parent {
        |  @pure def nested(): Z = {
        |    @pure def inner(): Z = { return super.value() + 1 }
        |    return inner()
        |  }
        |}""".stripMargin
    val (translator, _) = translated(input, "Child", "nested")
    assert(hasNestedMethod(input, "Child", "nested"))
    assert(!translator.liftedProcedures.isEmpty)
    val lifted = translator.liftedProcedures(0)
    assert(lifted.tipe.args(0) == Typed.Name(ISZ(SString("Child")), None(), ISZ()))
    assert(lifted.paramNames(0).value == "this")
    val superApply = firstSuper(lifted)
    assert(superApply.isSuper)
    assert(superApply.owner == ISZ(SString("Parent")))
  }

  registerTest("lambda super method calls capture this") {
    val input =
      """import org.sireum._
        |@msig trait Parent {
        |  @pure def value(): Z = { return 20 }
        |}
        |@record class Child() extends Parent {
        |  @pure def nested(): Z = {
        |    val f: () => Z = () => super.value()
        |    return f()
        |  }
        |}""".stripMargin
    val (translator, _) = translated(input, "Child", "nested")
    assert(!translator.liftedProcedures.isEmpty)
    val lifted = translator.liftedProcedures(0)
    assert(lifted.tipe.args(0) == Typed.Name(ISZ(SString("Child")), None(), ISZ()))
    assert(lifted.paramNames(0).value == "this")
    assert(firstSuper(lifted).isSuper)
  }

  registerTest("by-name super method calls capture this") {
    val input =
      """import org.sireum._
        |@msig trait Parent {
        |  @pure def value(): Z = { return 20 }
        |}
        |@record class Child() extends Parent {
        |  @pure def consume(x: => Z): Z = { return x }
        |  @pure def nested(): Z = { return consume(super.value()) }
        |}""".stripMargin
    val (translator, _) = translated(input, "Child", "nested")
    assert(!translator.liftedProcedures.isEmpty)
    var found = F
    for (lifted <- translator.liftedProcedures) {
      if (lifted.paramNames.nonEmpty && lifted.paramNames(0).value == "this") {
        findSuper(returnStmt(lifted).expOpt.get) match {
          case Some(_) => found = T
          case _ =>
        }
      }
    }
    assert(found)
  }

  registerTest("super builtin selects carry direct dispatch metadata") {
    val input =
      """import org.sireum._
        |@msig trait Parent {
        |  @pure def value(): Z = { return 20 }
        |}
        |@record class Child() extends Parent {
        |  @pure def stringValue(): String = { return super.string }
        |  @pure def hashValue(): Z = { return super.hash }
        |}""".stripMargin
    val (translator, stringProcedure) = translated(input, "Child", "stringValue")
    val stringApply = firstSuper(stringProcedure)
    assert(stringApply.isSuper)
    assert(stringApply.id.value == "string")
    assert(stringApply.owner == ISZ(SString("Parent")))
    assert(stringApply.methodType.args(0) == Typed.Name(ISZ(SString("Parent")), None(), ISZ()))
    assert(stringApply.args(0).tipe == Typed.Name(ISZ(SString("Child")), None(), ISZ()))
    val hashProcedure = translated(input, "Child", "hashValue")._2
    val hashApply = firstSuper(hashProcedure)
    assert(hashApply.isSuper)
    assert(hashApply.id.value == "hash")
    assert(hashApply.owner == ISZ(SString("Parent")))
    assert(translator.liftedProcedures.isEmpty)
  }

  registerTest("record copy snapshots target and named arguments") {
    val input =
      """import org.sireum._
        |@record class Box(a: Z, b: Z) {
        |  @pure def make(): Box = { return Box(1, 2) }
        |  @pure def aValue(): Z = { return 3 }
        |  @pure def bValue(): Z = { return 4 }
        |  @pure def copyCase(): Box = { return make()(b = bValue(), a = aValue()) }
        |  @pure def copyPartial(): Box = { return make()(b = bValue()) }
        |}""".stripMargin
    val (_, procedure) = translated(input, "Box", "copyCase")
    val block = procedure.body.asInstanceOf[IR.Body.Block].block
    var temps = ISZ[IR.Exp]()
    for (stmt <- block.stmts) {
      stmt match {
        case temp: IR.Stmt.Assign.Temp => temps = temps :+ temp.rhs
        case _ =>
      }
    }
    assert(temps.size == 3)
    assert(temps(0).asInstanceOf[IR.Exp.Apply].id.value == "make")
    assert(temps(1).asInstanceOf[IR.Exp.Apply].id.value == "bValue")
    assert(temps(2).asInstanceOf[IR.Exp.Apply].id.value == "aValue")
    val constructed = returnStmt(procedure).expOpt.get.asInstanceOf[IR.Exp.Construct]
    assert(constructed.args(0).isInstanceOf[IR.Exp.Temp])
    assert(constructed.args(1).isInstanceOf[IR.Exp.Temp])
    assert(constructed.args(0).asInstanceOf[IR.Exp.Temp].n == constructed.args(1).asInstanceOf[IR.Exp.Temp].n + 1)
    val (_, partialProcedure) = translated(input, "Box", "copyPartial")
    val partialBlock = partialProcedure.body.asInstanceOf[IR.Body.Block].block
    var partialTemps = ISZ[IR.Exp]()
    for (stmt <- partialBlock.stmts) {
      stmt match {
        case temp: IR.Stmt.Assign.Temp => partialTemps = partialTemps :+ temp.rhs
        case _ =>
      }
    }
    assert(partialTemps.size == 2)
    assert(partialTemps(0).asInstanceOf[IR.Exp.Apply].id.value == "make")
    assert(partialTemps(1).asInstanceOf[IR.Exp.Apply].id.value == "bValue")
    val partialConstructed = returnStmt(partialProcedure).expOpt.get.asInstanceOf[IR.Exp.Construct]
    assert(partialConstructed.args(0).isInstanceOf[IR.Exp.FieldVarRef])
    assert(partialConstructed.args(0).asInstanceOf[IR.Exp.FieldVarRef].receiver.isInstanceOf[IR.Exp.Temp])
    assert(partialConstructed.args(1).isInstanceOf[IR.Exp.Temp])
  }

  registerTest("static companion binary operators omit the type receiver") {
    val input =
      """import org.sireum._
        |@record class Box() {
        |  @pure def staticMap(): HashMap[String, Z] = {
        |    val entries: ISZ[(String, Z)] = ISZ("x" ~> 7)
        |    return HashMap ++ entries
        |  }
        |  @pure def instanceMap(): HashMap[String, Z] = {
        |    val base: HashMap[String, Z] = HashMap.empty[String, Z]
        |    val entries: ISZ[(String, Z)] = ISZ("x" ~> 7)
        |    return base ++ entries
        |  }
        |}""".stripMargin
    val (_, staticProcedure) = translated(input, "Box", "staticMap")
    val staticApply = returnStmt(staticProcedure).expOpt.get.asInstanceOf[IR.Exp.Apply]
    assert(staticApply.isInObject)
    assert(staticApply.owner == ISZ(SString("org"), SString("sireum"), SString("HashMap")))
    assert(staticApply.id.value == "++")
    assert(staticApply.args.size == 1)
    assert(staticApply.args(0).isInstanceOf[IR.Exp.LocalVarRef])
    assert(staticApply.methodType.args.size == 1)

    val (_, instanceProcedure) = translated(input, "Box", "instanceMap")
    val instanceApply = returnStmt(instanceProcedure).expOpt.get.asInstanceOf[IR.Exp.Apply]
    assert(!instanceApply.isInObject)
    assert(instanceApply.owner == ISZ(SString("org"), SString("sireum"), SString("HashMap")))
    assert(instanceApply.id.value == "++")
    assert(instanceApply.args.size == 2)
    assert(instanceApply.args(0).isInstanceOf[IR.Exp.LocalVarRef])
    assert(instanceApply.args(1).isInstanceOf[IR.Exp.LocalVarRef])
    assert(instanceApply.methodType.args.size == 2)
  }

  registerTest("hidden-first named constructors retain full parameter indices") {
    val input =
      """import org.sireum._
        |@datatype class HiddenFirst(@hidden first: Option[Z], second: Option[Z], third: Option[Z]) {
        |  @pure def make(): HiddenFirst = {
        |    return HiddenFirst(third = Some(33), first = Some(11), second = Some(22))
        |  }
        |}""".stripMargin
    val (th, program) = checked(input)
    val info = th.typeMap.get(ISZ("HiddenFirst")).get.asInstanceOf[org.sireum.lang.symbol.TypeInfo.Adt]
    val constructor = info.constructorTypeOpt.get.asInstanceOf[Typed.Method]
    assert(constructor.paramNames == ISZ[String]("first", "second", "third"))
    assert(info.extractorTypeMap.get("first").isEmpty)
    assert(info.extractorTypeMap.get("second").nonEmpty)
    assert(info.extractorTypeMap.get("third").nonEmpty)

    val (_, method) = findMethod(program, "HiddenFirst", "make")
    val ret = method.bodyOpt.get.stmts(0).asInstanceOf[Stmt.Return]
    val invoke = ret.expOpt.get.asInstanceOf[ast.Exp.InvokeNamed]
    assert(invoke.args.map(_.index) == ISZ[Z](2, 0, 1))

    def valueOf(exp: IR.Exp): Z = exp match {
      case lit: IR.Exp.Int => return lit.value
      case construct: IR.Exp.Construct =>
        assert(construct.args.size == 1)
        return valueOf(construct.args(0))
      case _ => halt(st"Unexpected constructor argument $exp".render)
    }

    val (_, procedure) = translated(input, "HiddenFirst", "make")
    val block = procedure.body.asInstanceOf[IR.Body.Block].block
    var tempRhs = HashMap.empty[Z, IR.Exp]
    for (stmt <- block.stmts) {
      stmt match {
        case temp: IR.Stmt.Assign.Temp => tempRhs = tempRhs + temp.lhs ~> temp.rhs
        case _ =>
      }
    }
    val constructed = returnStmt(procedure).expOpt.get.asInstanceOf[IR.Exp.Construct]
    val values = for (arg <- constructed.args) yield valueOf(tempRhs.get(arg.asInstanceOf[IR.Exp.Temp].n).get)
    assert(values == ISZ[Z](11, 22, 33))
  }

  registerTest("for generators preserve range and guard temporary prefixes") {
    val input =
      """import org.sireum._
        |@record class Call(inObject: B, owner: ISZ[String], args: ISZ[Z], direct: B)
        |@record class Bounds(start: Z, end: Z)
        |@record class Box() {
        |  def probe(): Unit = {
        |    val call = Call(F, ISZ("p"), ISZ(1), T)
        |    val bounds = Bounds(0, 1)
        |    for (invalid <- ISZ[Call](call(inObject = T), call(owner = ISZ[String]()), call(args = ISZ[Z]())) if call(inObject = T).direct) {
        |      println(invalid.direct)
        |    }
        |    for (i <- bounds(start = 0).start until bounds(end = 1).end) {
        |      println(i)
        |    }
        |  }
        |}""".stripMargin
    val (_, procedure) = translated(input, "Box", "probe")
    assertGeneratorTemps(procedure)
  }

  registerTest("inherited super calls keep parent ABI and owner") {
    val input =
      """import org.sireum._
        |@msig trait GrandParent {
        |  @pure def value(x: Z): Z = { return x }
        |}
        |@msig trait Parent extends GrandParent
        |@record class Child() extends Parent {
        |  @pure override def value(x: Z): Z = { return super.value(x) }
        |}""".stripMargin
    val (_, procedure) = translated(input, "Child", "value")
    val superApply = firstSuper(procedure)
    assert(superApply.owner == ISZ(SString("Parent")))
    assert(superApply.methodType.args(0) == Typed.Name(ISZ(SString("Parent")), None(), ISZ()))
    assert(superApply.args(0).tipe == Typed.Name(ISZ(SString("Child")), None(), ISZ()))
    assert(superApply.args(1).tipe == Typed.z)
  }
}
