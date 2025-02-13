// #Sireum
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

package org.sireum.lang.tipe

import org.sireum._
import org.sireum.message._
import org.sireum.lang.ast._
import org.sireum.lang.symbol._
import org.sireum.lang.symbol.Resolver._

object PostTipeAttrChecker {
  val AttrResult: MTransformer.PreResult[Attr] = MTransformer.PreResult[Attr](F, MNone())
  val ExpResult: MTransformer.PreResult[Exp] = MTransformer.PreResult[Exp](T, MNone())
  val SkipExpResult: MTransformer.PreResult[Exp] = MTransformer.PreResult[Exp](F, MNone())
  val ResolvedResult: MTransformer.PreResult[ResolvedAttr] = MTransformer.PreResult[ResolvedAttr](F, MNone())
  val TypedResult: MTransformer.PreResult[TypedAttr] = MTransformer.PreResult[TypedAttr](F, MNone())
  val avoidCheckNames: HashSet[QName] = HashSet ++ ISZ(
    Typed.sireumName :+ "T", Typed.sireumName :+ "F"
  )
  def checkNameTypeMaps(nameMap: NameMap, typeMap: TypeMap, reporter: Reporter): Unit = {
    val t = PostTipeAttrChecker(HashSSet.empty)
    for (info <- nameMap.values) {
      info match {
        case info: Info.Object =>
          for (stmt <- info.ast.stmts) {
            stmt match {
              case _: Stmt.Object =>
              case _ => t.transformStmt(stmt)
            }
          }
        case info: Info.Var if !avoidCheckNames.contains(info.owner :+ info.ast.id.value) => t.transformStmt(info.ast)
        case info: Info.SpecVar => t.transformStmt(info.ast)
        case info: Info.Method => t.transformStmt(info.ast)
        case info: Info.SpecMethod => t.transformStmt(info.ast)
        case info: Info.ExtMethod => t.transformStmt(info.ast)
        case _ =>
      }
    }
    for (info <- typeMap.values) {
      info match {
        case info: TypeInfo.Sig => t.transformStmt(info.ast)
        case info: TypeInfo.Adt => t.transformStmt(info.ast)
        case _ =>
      }
    }
    reporter.reports(t.messages.elements)
  }

  def checkStmt(stmt: Stmt, reporter: Reporter): Unit = {
    val t = PostTipeAttrChecker(HashSSet.empty)
    t.transformStmt(stmt)
    reporter.reports(t.messages.elements)
  }

  def checkProgram(program: TopUnit.Program, reporter: Reporter): Unit = {
    val t = PostTipeAttrChecker(HashSSet.empty)
    t.transformTopUnit(program)
    reporter.reports(t.messages.elements)
  }
}

@record class PostTipeAttrChecker(var messages: HashSSet[Message]) extends MTransformer {

  override def preExp(o: Exp): MTransformer.PreResult[Exp] = {
    o match {
      case _: Exp.StateSeq => return PostTipeAttrChecker.SkipExpResult // TODO
      case _ => return PostTipeAttrChecker.ExpResult
    }
  }

  override def preAttr(o: Attr): MTransformer.PreResult[Attr] = {
    return PostTipeAttrChecker.AttrResult
  }

  override def preResolvedAttr(o: ResolvedAttr): MTransformer.PreResult[ResolvedAttr] = {
    if (o.typedOpt.isEmpty) {
      emptyError(T, o.posOpt)
    }
    if (o.resOpt.isEmpty) {
      emptyError(F, o.posOpt)
    }
    return PostTipeAttrChecker.ResolvedResult
  }

  override def preTypedAttr(o: TypedAttr): MTransformer.PreResult[TypedAttr] = {
    if (o.typedOpt.isEmpty) {
      emptyError(T, o.posOpt)
    }
    return PostTipeAttrChecker.TypedResult
  }

  def emptyError(isType: B, posOpt: Option[Position]): Unit = {
    val m = Message(
      Level.InternalError,
      posOpt,
      TypeChecker.typeCheckerKind,
      s"Empty ${if (isType) "type" else "symbol"} information after type checking phase"
    )
    if (m.fileUriOpt.isEmpty || !messages.contains(m)) {
      messages = messages + m
    }
  }
}
