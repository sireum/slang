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
import org.sireum.lang.ast.{IR, Stmt, Typed}
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
