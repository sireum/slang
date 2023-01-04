// #Sireum
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

package org.sireum.lang.symbol

import org.sireum._
import org.sireum.message._
import Resolver._
import org.sireum.lang.ast.Sequent
import org.sireum.lang.ast._

object SequentResolver {

  @datatype class QScope(val nameMap: HashMap[String, Id], val outerOpt: Option[QScope]) {

    @pure def resolve(name: String): Option[Id] = {
      nameMap.get(name) match {
        case Some(id) => return Some(id)
        case _ =>
          outerOpt match {
            case Some(outer) => return outer.resolve(name)
            case _ => return None()
          }
      }
    }
  }

  @record class DeclResolver(
    var scope: QScope,
    var freeVarMap: HashMap[String, (Id, Z)],
    var hasQuant: B,
    val reporter: Reporter
  ) extends MTransformer {

    override def preExpQuantType(o: Exp.QuantType): MTransformer.PreResult[Exp.Quant] = {
      hasQuant = T
      var newScope = QScope(HashMap.empty, Some(scope))
      for (p <- o.fun.params) {
        p.tipeOpt match {
          case Some(t) =>
            reporter.error(
              t.posOpt,
              resolverKind,
              s"Predicate logic sequents cannot have quantified variable domains."
            )
          case _ =>
        }
        p.idOpt match {
          case Some(id) =>
            val key = id.value
            newScope.resolve(key) match {
              case Some(_) => reporter.error(id.attr.posOpt, resolverKind, s"$key has been previously declared.")
              case _ => newScope = newScope(nameMap = newScope.nameMap + key ~> id)
            }
          case _ =>
        }
      }

      scope = newScope
      return MTransformer.PreResult(T, MNone())
    }

    override def postExpQuantType(o: Exp.QuantType): MOption[Exp.Quant] = {
      scope.outerOpt match {
        case Some(outer) => scope = outer
        case _ =>
          reporter
            .internalError(o.attr.posOpt, resolverKind, s"Unexpected scoping situation when resolving quantification.")
      }
      return MNone()
    }

    override def preExpInvoke(o: Exp.Invoke): MTransformer.PreResult[Exp] = {
      val id = o.ident.id
      val k = id.value
      scope.resolve(k) match {
        case Some(_) =>
          reporter
            .error(o.attr.posOpt, resolverKind, s"Quantified variable '$k' cannot be used as a function/predicate.")
        case _ =>
          freeVarMap.get(k) match {
            case Some((_, n)) =>
              if (n != o.args.size) {
                reporter.error(
                  o.attr.posOpt,
                  resolverKind,
                  s"Inconsistent usage of '$k' with different numbers of arguments."
                )
              }
            case _ => freeVarMap = freeVarMap + k ~> ((id, o.args.size))
          }
      }
      for (arg <- o.args) {
        val p = resolveDeclExp(scope, freeVarMap, reporter, arg)
        hasQuant = hasQuant || p._1
        freeVarMap = p._2
      }
      return MTransformer.PreResult(F, MNone())
    }

    override def preExpIdent(o: Exp.Ident): MTransformer.PreResult[Exp] = {
      val id = o.id
      val k = id.value
      scope.resolve(k) match {
        case Some(_) =>
        case _ =>
          freeVarMap.get(k) match {
            case Some((_, n)) =>
              if (n != z"0") {
                reporter.error(
                  o.attr.posOpt,
                  resolverKind,
                  s"Inconsistent usage of '$k' as both a variable and a function/predicate."
                )
              }
            case _ => freeVarMap = freeVarMap + k ~> ((id, 0))
          }
      }
      return MTransformer.PreResult(F, MNone())
    }
  }

  def resolveDeclExp(
    scope: QScope,
    freeVarMap: HashMap[String, (Id, Z)],
    reporter: Reporter,
    e: Exp
  ): (B, HashMap[String, (Id, Z)]) = {
    val dr = DeclResolver(scope, freeVarMap, F, reporter)
    dr.transformExp(e)
    return (dr.hasQuant, dr.freeVarMap)
  }

  def resolveDecl(sequent: Sequent, reporter: Reporter): (B, HashMap[String, (Id, Z)]) = {
    var freeVarMap = HashMap.empty[String, (Id, Z)]
    val scope = QScope(HashMap.empty, None())
    var hasQuant = F
    for (e <- sequent.premises) {
      val (hq, fvm) = resolveDeclExp(scope, freeVarMap, reporter, e)
      freeVarMap = fvm
      hasQuant = hasQuant || hq
    }
    val (hq, fvm) = resolveDeclExp(scope, freeVarMap, reporter, sequent.conclusion)
    freeVarMap = fvm
    hasQuant = hasQuant || hq
    return (hasQuant, freeVarMap)
  }
}
