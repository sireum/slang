// #Sireum
/*
 Copyright (c) 2019, Robby, Kansas State University
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
import org.sireum.lang.{ast => AST}
import org.sireum.lang.symbol._
import org.sireum.lang.symbol.Resolver._
import org.sireum.lang.tipe.TypeOutliner.ExpTypedSubst

object TypeOutliner {

  @datatype class ExpTypedSubst(substMap: HashMap[String, AST.Typed]) extends AST.Transformer.PrePost[B] {
    override def preTypedTypeVar(ctx: B, o: AST.Typed.TypeVar): AST.Transformer.PreResult[B, AST.Typed] = {
      val id = o.id
      substMap.get(id) match {
        case Some(t) => return AST.Transformer.PreResult(T, F, Some(t.subst(substMap)))
        case _ => return AST.Transformer.PreResult(T, F, None())
      }
    }

    override def preTypeNamed(ctx: B, o: AST.Type.Named): AST.Transformer.PreResult[B, AST.Type] = {
      if (o.name.ids.size == 1) {
        val id = o.name.ids(0).value
        substMap.get(id) match {
          case Some(t) => return AST.Transformer.PreResult(T, F, Some(o.typed(t)))
          case _ =>
        }
      }
      return AST.Transformer.PreResult(T, F, None())
    }
  }


  @record class TypeMembers(var specVars: HashSMap[String, Info.SpecVar],
                            var vars: HashSMap[String, Info.Var],
                            var specMethods: HashMap[String, Info.SpecMethod],
                            var methods: HashMap[String, Info.Method],
                            var invariants: HashMap[String, Info.Inv],
                            var drs: ISZ[AST.Stmt.DataRefinement],
                            var stmts: ISZ[AST.Stmt])

  def checkOutline(par: B, strictAliasing: B, typeHierarchy: TypeHierarchy, reporter: Reporter): TypeHierarchy = {
    def parentsOutlined(name: QName, typeMap: TypeMap): B = {
      def isOutlined(ids: QName): B = {
        typeMap.get(ids).get match {
          case ti: TypeInfo.Sig => return ti.outlined
          case ti: TypeInfo.Adt => return ti.outlined
          case _ => return T
        }
      }

      var r = T
      for (p <- typeHierarchy.poset.parentsOf(name).elements if r) {
        typeMap.get(p).get match {
          case ti: TypeInfo.TypeAlias =>
            val t = typeHierarchy.dealiasInit(None(), AST.Typed.Name(ti.name, ISZ()), reporter).get
            r = isOutlined(t.ids)
          case ti =>
            r = isOutlined(ti.name)
        }
      }
      return r
    }

    var th = typeHierarchy

    def outlineNonObject(): Unit = {
      var workList = typeHierarchy.rootTypeNames()
      var jobs = ISZ[() => (TypeHierarchy => (TypeHierarchy, ISZ[Message])@pure)@pure]()

      def addTypeAliases(): Unit = {
        var typeAliases: ISZ[TypeInfo.TypeAlias] = ISZ()
        for (typeInfo <- typeHierarchy.typeMap.values) {
          typeInfo match {
            case typeInfo: TypeInfo.TypeAlias => typeAliases = typeAliases :+ typeInfo
            case _ =>
          }
        }
        jobs = jobs :+ (() => TypeOutliner(th).outlineTypeAliases(typeAliases))
      }

      def addJob(name: QName, acc: ISZ[QName]): ISZ[QName] = {
        var r = acc
        val ti = th.typeMap.get(name).get
        var ok: B = F
        val to = TypeOutliner(th)
        ti match {
          case ti: TypeInfo.Sig =>
            if (!ti.outlined) {
              val po = parentsOutlined(ti.name, th.typeMap)
              if (po) {
                jobs = jobs :+ (() => to.outlineSig(ti))
                ok = T
              }
            } else {
              ok = T
            }
          case ti: TypeInfo.Adt =>
            if (!ti.outlined) {
              val po = parentsOutlined(ti.name, th.typeMap)
              if (po) {
                jobs = jobs :+ (() => to.outlineAdt(ti))
                ok = T
              }
            } else {
              ok = T
            }
          case _ =>
        }
        if (ok) {
          val children = typeHierarchy.poset.childrenOf(name).elements
          for (n <- children) {
            r = r :+ n
          }
        }
        return r
      }

      addTypeAliases()

      while (workList.nonEmpty && !reporter.hasError) {
        var l = ISZ[QName]()
        for (name <- workList) {
          l = addJob(name, l)
        }
        val init = (th, ISZ[Message]())
        val r: (TypeHierarchy, ISZ[Message]) =
          if (par) ops.ISZOps(ops.ISZOps(jobs).parMap((f: () => (TypeHierarchy => (TypeHierarchy, ISZ[Message])@pure)@pure) => f())).
            foldLeft(TypeHierarchy.combine _, init)
          else ops.ISZOps(ops.ISZOps(jobs).map((f: () => (TypeHierarchy => (TypeHierarchy, ISZ[Message])@pure)@pure) => f())).
            foldLeft(TypeHierarchy.combine _, init)
        reporter.reports(r._2)
        th = r._1
        workList = l
        jobs = ISZ()
      }
    }

    def outlineObject(): Unit = {
      var jobs = ISZ[() => (TypeHierarchy => (TypeHierarchy, ISZ[Message])@pure)@pure]()
      val to = TypeOutliner(th)
      for (info <- th.nameMap.values) {
        info match {
          case info: Info.Object if !info.outlined => jobs = jobs :+ (() => to.outlineObject(info))
          case _ =>
        }
      }
      val init = (th, ISZ[Message]())
      val r: (TypeHierarchy, ISZ[Message]) =
        if (par) ops.ISZOps(ops.ISZOps(jobs).parMap((f: () => (TypeHierarchy => (TypeHierarchy, ISZ[Message])@pure)@pure) => f())).
          foldLeft(TypeHierarchy.combine _, init)
        else ops.ISZOps(ops.ISZOps(jobs).map((f: () => (TypeHierarchy => (TypeHierarchy, ISZ[Message])@pure)@pure) => f())).
          foldLeft(TypeHierarchy.combine _, init)
      reporter.reports(r._2)
      th = r._1
      var gnm = th.nameMap
      val to2 = TypeOutliner(th)
      for (info <- gnm.values) {
        val infoOpt: Option[Info] = info match {
          case info: Info.Var if !info.outlined => to2.outlineVar(info, reporter)
          case info: Info.SpecVar if !info.outlined => to2.outlineSpecVar(info, reporter)
          case info: Info.Method if !info.outlined => to2.outlineMethod(info, reporter)
          case info: Info.SpecMethod if !info.outlined => to2.outlineSpecMethod(info, reporter)
          case _ => None()
        }
        infoOpt match {
          case Some(inf) => gnm = gnm + info.name ~> inf
          case _ =>
        }
      }
      th = th(nameMap = gnm)
    }

    def checkContract(): Unit = {
      var jobs = ISZ[() => (TypeHierarchy => (TypeHierarchy, ISZ[Message])@pure)@pure]()
      val to3 = TypeOutliner(th)
      for (info <- th.nameMap.values) {
        info match {
          case info: Info.Object if !info.contractOutlined =>
            jobs = jobs :+ (() => to3.checkObjectContract(strictAliasing, info))
          case _ =>
        }
      }
      for (info <- th.typeMap.values) {
        info match {
          case info: TypeInfo.Adt if !info.contractOutlined =>
            jobs = jobs :+ (() => to3.checkAdtContract(strictAliasing, info))
          case info: TypeInfo.Sig if !info.contractOutlined =>
            jobs = jobs :+ (() => to3.checkSigContract(strictAliasing, info))
          case _ =>
        }
      }

      val init = (th, ISZ[Message]())
      val r: (TypeHierarchy, ISZ[Message]) =
        if (par) ops.ISZOps(ops.ISZOps(jobs).parMap((f: () => (TypeHierarchy => (TypeHierarchy, ISZ[Message])@pure)@pure) => f())).
          foldLeft(TypeHierarchy.combine _, init)
        else ops.ISZOps(ops.ISZOps(jobs).map((f: () => (TypeHierarchy => (TypeHierarchy, ISZ[Message])@pure)@pure) => f())).
          foldLeft(TypeHierarchy.combine _, init)
      reporter.reports(r._2)
      th = r._1
    }

    outlineNonObject()

    if (reporter.hasError) {
      return th
    }

    outlineObject()

    if (reporter.hasError) {
      return th
    }

    checkContract()

    return th
  }

}

@datatype class TypeOutliner(typeHierarchy: TypeHierarchy) {

  def outlineSpecVar(info: Info.SpecVar, reporter: Reporter): Option[Info] = {
    val sv = info.ast
    val newTipeOpt = typeHierarchy.typed(info.scope, sv.tipe, reporter)
    newTipeOpt match {
      case Some(newTipe) =>
        return Some(info(ast = sv(tipe = newTipe, attr = sv.attr(typedOpt = newTipe.typedOpt))))
      case _ => return None()
    }
  }

  def outlineVar(info: Info.Var, reporter: Reporter): Option[Info] = {
    val v = info.ast
    val tpe = v.tipeOpt.get
    val newTipeOpt = typeHierarchy.typed(info.scope, tpe, reporter)
    newTipeOpt match {
      case Some(newTipe) =>
        return Some(info(ast = v(tipeOpt = newTipeOpt, attr = v.attr(typedOpt = newTipe.typedOpt))))
      case _ => return None()
    }
  }

  def outlineSpecMethod(info: Info.SpecMethod, reporter: Reporter): Option[Info] = {
    val sm = info.ast
    val sigOpt = outlineMethodSig(info.scope, sm.sig, reporter)
    sigOpt match {
      case Some((sig, tVars)) =>
        val res = info.methodRes
        val tOpt: Option[AST.Typed] = Some(
          AST.Typed.Method(
            T,
            AST.MethodMode.Spec,
            tVars,
            info.owner,
            sig.id.value,
            sig.params.map(p => p.id.value),
            sig.funType
          )
        )
        if (sig.funType.ret == AST.Typed.unit) {
          reporter.error(sm.sig.returnType.posOpt, TypeChecker.typeCheckerKind, "@spec methods cannot have Unit as their return type")
        }
        return Some(
          info(ast = sm(sig = sig, attr = sm.attr(typedOpt = tOpt, resOpt = Some(res(tpeOpt = Some(sig.funType))))))
        )
      case _ => return None()
    }
  }

  def outlineMethod(info: Info.Method, reporter: Reporter): Option[Info] = {
    val m = info.ast
    val sigOpt = outlineMethodSig(info.scope, m.sig, reporter)
    sigOpt match {
      case Some((sig, tVars)) =>
        val res = info.methodRes
        val tOpt: Option[AST.Typed] = Some(
          AST.Typed.Method(
            T,
            AST.MethodMode.Method,
            tVars,
            info.owner,
            sig.id.value,
            sig.params.map(p => p.id.value),
            sig.funType
          )
        )
        if (info.ast.purity == AST.Purity.StrictPure && sig.funType.ret == AST.Typed.unit) {
          reporter.error(m.sig.returnType.posOpt, TypeChecker.typeCheckerKind, "@strictpure methods cannot have Unit as their return type")
        }
        return Some(
          info(ast = m(sig = sig, attr = m.attr(typedOpt = tOpt, resOpt = Some(res(tpeOpt = Some(sig.funType))))))
        )
      case _ => return None()
    }
  }

  def outlineExtMethod(info: Info.ExtMethod, reporter: Reporter): Option[Info] = {
    val m = info.ast
    val sigOpt = outlineMethodSig(info.scope, m.sig, reporter)
    sigOpt match {
      case Some((sig, tVars)) =>
        val res = info.methodRes
        val tOpt: Option[AST.Typed] = Some(
          AST.Typed.Method(
            T,
            AST.MethodMode.Ext,
            tVars,
            info.owner,
            sig.id.value,
            sig.params.map(p => p.id.value),
            sig.funType
          )
        )
        return Some(
          info(ast = m(sig = sig, attr = m.attr(typedOpt = tOpt, resOpt = Some(res(tpeOpt = Some(sig.funType))))))
        )
      case _ => return None()
    }
  }

  def outlineJustMethod(info: Info.JustMethod, reporter: Reporter): Option[Info] = {
    val m = info.ast
    val sigOpt = outlineMethodSig(info.scope, m.sig, reporter)
    sigOpt match {
      case Some((sig, tVars)) =>
        if (sig.funType.ret != AST.Typed.unit) {
          reporter.error(m.sig.returnType.posOpt, TypeChecker.typeCheckerKind,
            "Expecting Unit return type for a @just plugin method")
        }
        if (m.etaOpt.nonEmpty) {
          for (p <- sig.params) {
            p.tipe.typedOpt match {
              case Some(t) if t != AST.Typed.z =>
                reporter.error(m.sig.returnType.posOpt, TypeChecker.typeCheckerKind,
                  "Expecting parameter type Z for a @just forwarding method")
              case _ =>
            }
          }
        }
        val res = info.methodRes
        val tOpt: Option[AST.Typed] = Some(
          AST.Typed.Method(
            T,
            AST.MethodMode.Just,
            tVars,
            info.owner,
            sig.id.value,
            sig.params.map(p => p.id.value),
            sig.funType
          )
        )
        return Some(
          info(ast = m(sig = sig, attr = m.attr(typedOpt = tOpt, resOpt = Some(res(tpeOpt = Some(sig.funType))))))
        )
      case _ => return None()
    }
  }

  @pure def outlineObject(info: Info.Object): TypeHierarchy => (TypeHierarchy, ISZ[Message])@pure = {
    val reporter = Reporter.create

    var infos = ISZ[(QName, Info)]()
    var newStmts = ISZ[AST.Stmt]()
    for (stmt <- info.ast.stmts) {
      val idOpt: Option[String] = stmt match {
        case stmt: AST.Stmt.SpecVar => Some(stmt.id.value)
        case stmt: AST.Stmt.Var => Some(stmt.id.value)
        case stmt: AST.Stmt.SpecMethod => Some(stmt.sig.id.value)
        case stmt: AST.Stmt.Method => Some(stmt.sig.id.value)
        case stmt: AST.Stmt.ExtMethod => Some(stmt.sig.id.value)
        case stmt: AST.Stmt.JustMethod => Some(stmt.sig.id.value)
        case _ => None()
      }
      idOpt match {
        case Some(id) =>
          typeHierarchy.nameMap.get(info.name :+ id).get match {
            case inf: Info.SpecVar =>
              val rOpt = outlineSpecVar(inf, reporter)
              rOpt match {
                case Some(r: Info.SpecVar) =>
                  infos = infos :+ ((r.name, r))
                  newStmts = newStmts :+ r.ast
                case _ => newStmts = newStmts :+ stmt
              }
            case inf: Info.Var =>
              val rOpt = outlineVar(inf, reporter)
              rOpt match {
                case Some(r: Info.Var) =>
                  infos = infos :+ ((r.name, r))
                  newStmts = newStmts :+ r.ast
                case _ => newStmts = newStmts :+ stmt
              }
            case inf: Info.SpecMethod =>
              val rOpt = outlineSpecMethod(inf, reporter)
              rOpt match {
                case Some(r: Info.SpecMethod) =>
                  infos = infos :+ ((r.name, r))
                  newStmts = newStmts :+ r.ast
                case _ => newStmts = newStmts :+ stmt
              }
            case inf: Info.Method =>
              val rOpt = outlineMethod(inf, reporter)
              rOpt match {
                case Some(r: Info.Method) =>
                  infos = infos :+ ((r.name, r))
                  newStmts = newStmts :+ r.ast
                case _ => newStmts = newStmts :+ stmt
              }
            case inf: Info.ExtMethod =>
              val rOpt = outlineExtMethod(inf, reporter)
              rOpt match {
                case Some(r: Info.ExtMethod) =>
                  infos = infos :+ ((r.name, r))
                  newStmts = newStmts :+ r.ast
                case _ => newStmts = newStmts :+ stmt
              }
            case inf: Info.JustMethod =>
              val rOpt = outlineJustMethod(inf, reporter)
              rOpt match {
                case Some(r: Info.JustMethod) =>
                  infos = infos :+ ((r.name, r))
                  newStmts = newStmts :+ r.ast
                case _ => newStmts = newStmts :+ stmt
              }
            case _ => newStmts = newStmts :+ stmt
          }
        case _ => newStmts = newStmts :+ stmt
      }
    }

    val messages = reporter.messages
    val newAst = info.ast(stmts = newStmts)
    val newInfos = infos
    return (th: TypeHierarchy) => (th(nameMap = th.nameMap ++ newInfos + info.name ~> info(outlined = T, ast = newAst)), messages)
  }

  @pure def outlineTypeAliases(infos: ISZ[TypeInfo.TypeAlias]): TypeHierarchy => (TypeHierarchy, ISZ[Message])@pure = {
    val reporter = Reporter.create
    var r = ISZ[(QName, TypeInfo)]()
    for (info <- infos) {
      val tm = typeParamMap(info.ast.typeParams, reporter)
      val scope = Scope.Local.create(tm.map, info.scope)
      val newTipeOpt = typeHierarchy.typed(scope, info.ast.tipe, reporter)
      val newInfo: TypeInfo.TypeAlias = newTipeOpt match {
        case Some(newTipe) => info(ast = info.ast(tipe = newTipe))
        case _ => info
      }
      r = r :+ ((info.name, newInfo))
    }
    val messages = reporter.messages
    return (th: TypeHierarchy) => (th(typeMap = th.typeMap ++ r), messages)
  }

  @pure def outlineSig(info: TypeInfo.Sig): TypeHierarchy => (TypeHierarchy, ISZ[Message])@pure = {
    val reporter = Reporter.create
    val tm = typeParamMap(info.ast.typeParams, reporter)
    val scope = Scope.Local.create(tm.map, info.scope)
    val members = outlineMembers(
      TypeInfo.Members(
        info.specVars,
        HashSMap.empty,
        info.specMethods,
        info.methods,
        info.refinements,
        info.invariants
      ),
      scope,
      reporter
    )
    val (
      TypeInfo.Members(specVars, _, specMethods, methods, refinements, invariants),
      ancestors,
      newParents
      ) =
      outlineInheritedMembers(info.owner, info.ast.id.value, info.name, info.ast.parents, scope, members, reporter)
    val newInfo = info(
      outlined = T,
      ancestors = ancestors,
      ast = info.ast(parents = newParents),
      specVars = specVars,
      specMethods = specMethods,
      methods = methods,
      refinements = refinements,
      invariants = invariants
    )
    val messages = reporter.messages
    return (th: TypeHierarchy) => (th(typeMap = th.typeMap + info.name ~> newInfo), messages)
  }

  @pure def outlineAdt(info: TypeInfo.Adt): TypeHierarchy => (TypeHierarchy, ISZ[Message])@pure = {
    val reporter = Reporter.create
    val tm = typeParamMap(info.ast.typeParams, reporter)
    val scope = Scope.Local.create(tm.map, info.scope)
    val members = outlineMembers(
      TypeInfo.Members(
        info.specVars,
        info.vars,
        info.specMethods,
        info.methods,
        info.refinements,
        info.invariants
      ),
      scope,
      reporter
    )
    val (
      TypeInfo.Members(specVars, vars, specMethods, methods, refinements, invariants),
      ancestors,
      newParents
      ) =
      outlineInheritedMembers(info.owner, info.ast.id.value, info.name, info.ast.parents, scope, members, reporter)
    var newParams = ISZ[AST.AdtParam]()
    var paramTypes = ISZ[AST.Typed]()
    var extractorTypeMap = Map.empty[String, AST.Typed]
    for (p <- info.ast.params) {
      val newTipeOpt: Option[AST.Type] =
        vars.get(p.id.value) match {
          case Some(v) => v.ast.tipeOpt
          case _ =>
            val r = typeHierarchy.typed(scope, p.tipe, reporter)
            r
        }
      newTipeOpt match {
        case Some(newTipe) if newTipe.typedOpt.nonEmpty =>
          val t = newTipe.typedOpt.get
          paramTypes = paramTypes :+ t
          newParams = newParams :+ p(tipe = newTipe)
          if (!p.isHidden) {
            extractorTypeMap = extractorTypeMap + p.id.value ~> t
          }
        case _ =>
      }
    }
    val newInfo: TypeInfo.Adt =
      if (info.ast.isRoot) {
        info(
          outlined = T,
          ancestors = ancestors,
          ast = info.ast(parents = newParents),
          specVars = specVars,
          vars = vars,
          specMethods = specMethods,
          methods = methods,
          refinements = refinements,
          invariants = invariants
        )
      } else {
        val constructorFun = AST.Typed.Fun(T, F, paramTypes, info.tpe)
        val constructorRes = info.constructorResOpt.get.asInstanceOf[AST.ResolvedInfo.Method]
        info(
          outlined = T,
          ancestors = ancestors,
          constructorTypeOpt = Some(
            AST.Typed.Method(
              T,
              AST.MethodMode.Constructor,
              tm.keys,
              info.owner,
              info.ast.id.value,
              info.ast.params.map(p => p.id.value),
              constructorFun
            )
          ),
          constructorResOpt = Some(constructorRes(tpeOpt = Some(constructorFun))),
          extractorTypeMap = extractorTypeMap,
          specVars = specVars,
          vars = vars,
          specMethods = specMethods,
          methods = methods,
          refinements = refinements,
          invariants = invariants,
          ast = info.ast(params = newParams, parents = newParents)
        )
      }
    val messages = reporter.messages
    return (th: TypeHierarchy) => (th(typeMap = th.typeMap + info.name ~> newInfo), messages)
  }

  def outlineMethodSig(scope: Scope, sig: AST.MethodSig, reporter: Reporter): Option[(AST.MethodSig, ISZ[String])] = {
    val typeParams = sig.typeParams
    for (tp <- typeParams) {
      scope.resolveType(typeHierarchy.typeMap, ISZ(tp.id.value)) match {
        case Some(info) if info.name.size == z"1" =>
          reporter
            .error(tp.id.attr.posOpt, TypeChecker.typeCheckerKind, s"Cannot redeclare type parameter ${tp.id.value}.")
          return None()
        case _ =>
      }
    }
    val tm = typeParamMap(typeParams, reporter)
    val mScope = Scope.Local.create(tm.map, scope)
    var params = ISZ[AST.Param]()
    for (p <- sig.params) {
      val tipeOpt = typeHierarchy.typed(mScope, p.tipe, reporter)
      tipeOpt match {
        case Some(tipe) if tipe.typedOpt.nonEmpty => params = params :+ p(tipe = tipe)
        case _ => return None()
      }
    }
    val returnTypeOpt = typeHierarchy.typed(mScope, sig.returnType, reporter)
    returnTypeOpt match {
      case Some(returnType) if returnTypeOpt.nonEmpty =>
        return Some((sig(params = params, returnType = returnType), tm.keys))
      case _ => return None()
    }
  }

  def outlineMembers(
                      info: TypeInfo.Members,
                      scope: Scope,
                      reporter: Reporter
                    ): TypeInfo.Members = {
    var specVars = HashSMap.empty[String, Info.SpecVar]
    var vars = HashSMap.empty[String, Info.Var]
    var specMethods = HashMap.empty[String, Info.SpecMethod]
    var methods = HashMap.empty[String, Info.Method]
    var invariants = HashMap.empty[String, Info.Inv]

    def isDeclared(id: String): B = {
      return specVars.contains(id) || vars.contains(id) || specMethods.contains(id) || methods.contains(id) || invariants.contains(id)
    }

    def checkSpecVar(svInfo: Info.SpecVar): Unit = {
      val sv = svInfo.ast
      val id = sv.id.value
      if (isDeclared(id)) {
        reporter.error(sv.attr.posOpt, TypeChecker.typeCheckerKind, s"Cannot redeclare $id.")
        return
      }
      val tipeOpt = typeHierarchy.typed(scope, sv.tipe, reporter)
      tipeOpt match {
        case Some(tipe) if tipe.typedOpt.nonEmpty =>
          specVars = specVars + id ~> svInfo(ast = sv(tipe = tipe, attr = sv.attr(typedOpt = tipe.typedOpt)))
        case _ =>
      }
    }

    def checkVar(vInfo: Info.Var): Unit = {
      val v = vInfo.ast
      val id = v.id.value
      if (isDeclared(id)) {
        reporter.error(v.attr.posOpt, TypeChecker.typeCheckerKind, s"Cannot redeclare $id.")
        return
      }
      val tpe = v.tipeOpt.get
      val tipeOpt = typeHierarchy.typed(scope, tpe, reporter)
      tipeOpt match {
        case Some(tipe) =>
          vars = vars + id ~> vInfo(ast = v(tipeOpt = Some(tipe), attr = v.attr(typedOpt = tipe.typedOpt)))
        case _ =>
      }
    }

    def checkSpecMethod(smInfo: Info.SpecMethod): Unit = {
      val sm = smInfo.ast
      val id = sm.sig.id.value
      if (isDeclared(id)) {
        reporter.error(sm.sig.id.attr.posOpt, TypeChecker.typeCheckerKind, s"Cannot redeclare $id.")
        return
      }
      val sigOpt = outlineMethodSig(scope, sm.sig, reporter)
      sigOpt match {
        case Some((sig, tVars)) =>
          val res = smInfo.methodRes
          val tOpt: Option[AST.Typed] = Some(
            AST.Typed
              .Method(F, AST.MethodMode.Spec, tVars, smInfo.owner, id, sig.params.map(p => p.id.value), sig.funType)
          )
          specMethods = specMethods + id ~> smInfo(
            ast = sm(sig = sig, attr = sm.attr(typedOpt = tOpt, resOpt = Some(res(tpeOpt = Some(sig.funType)))))
          )
        case _ =>
      }
    }

    def checkMethod(mInfo: Info.Method): Unit = {
      val m = mInfo.ast
      val id = m.sig.id.value
      if (isDeclared(id)) {
        reporter.error(m.sig.id.attr.posOpt, TypeChecker.typeCheckerKind, s"Cannot redeclare $id.")
        return
      }
      val sigOpt = outlineMethodSig(scope, m.sig, reporter)
      sigOpt match {
        case Some((sig, tVars)) =>
          val res = mInfo.methodRes
          val tOpt: Option[AST.Typed] = Some(
            AST.Typed
              .Method(F, AST.MethodMode.Method, tVars, mInfo.owner, id, sig.params.map(p => p.id.value), sig.funType)
          )
          methods = methods + id ~> mInfo(
            ast = m(sig = sig, attr = m.attr(typedOpt = tOpt, resOpt = Some(res(tpeOpt = Some(sig.funType)))))
          )
        case _ =>
      }
    }

    def checkInv(invInfo: Info.Inv): Unit = {
      val v = invInfo.ast
      val id = v.id.value
      if (isDeclared(id)) {
        reporter.error(v.attr.posOpt, TypeChecker.typeCheckerKind, s"Cannot redeclare $id.")
        return
      }
      invariants = invariants + id ~> invInfo
    }

    for (p <- info.specVars.values) {
      checkSpecVar(p)
    }

    for (p <- info.vars.values) {
      checkVar(p)
    }

    for (p <- info.specMethods.values) {
      checkSpecMethod(p)
    }

    for (p <- info.methods.values) {
      checkMethod(p)
    }

    for (p <- info.invariants.values) {
      checkInv(p)
    }

    return TypeInfo
      .Members(specVars, vars, specMethods, methods, HashMap.empty, invariants)
  }

  def outlineInheritedMembers(
                               nameOwner: QName,
                               nameId: String,
                               name: QName,
                               parents: ISZ[AST.Type.Named],
                               scope: Scope,
                               info: TypeInfo.Members,
                               reporter: Reporter
                             ): (TypeInfo.Members, ISZ[AST.Typed.Name], ISZ[AST.Type.Named]) = {
    val vars = info.vars
    var specVars = info.specVars
    var specMethods = info.specMethods
    var methods = info.methods
    var refinements = info.refinements
    var invariants = info.invariants

    def checkSpecInherit(id: String, tname: QName, posOpt: Option[Position]): B = {
      specVars.get(id) match {
        case Some(otherInfo) =>
          if (name != tname) {
            reporter.error(
              posOpt,
              TypeChecker.typeCheckerKind,
              st"Cannot inherit $id because it has been previously inherited from ${(otherInfo.owner, ".")}.".render
            )
          } else {
            reporter.error(
              posOpt,
              TypeChecker.typeCheckerKind,
              s"Cannot inherit $id because it has been previously declared."
            )
          }
          return F
        case _ =>
      }
      specMethods.get(id) match {
        case Some(otherInfo) =>
          if (name != tname) {
            reporter.error(
              posOpt,
              TypeChecker.typeCheckerKind,
              st"Cannot inherit $id because it has been previously inherited from ${(otherInfo.owner, ".")}.".render
            )
          } else {
            reporter.error(
              posOpt,
              TypeChecker.typeCheckerKind,
              s"Cannot inherit $id because it has been previously declared."
            )
          }
          return F
        case _ =>
      }
      return T
    }

    def checkInherit(id: String, tname: QName, posOpt: Option[Position]): B = {
      val ok = checkSpecInherit(id, tname, posOpt)
      if (!ok) {
        return ok
      }
      vars.get(id) match {
        case Some(otherInfo) =>
          if (name != tname) {
            reporter.error(
              posOpt,
              TypeChecker.typeCheckerKind,
              st"Cannot inherit $id because it has been previously inherited from ${(otherInfo.owner, ".")}.".render
            )
          } else {
            reporter.error(
              posOpt,
              TypeChecker.typeCheckerKind,
              s"Cannot inherit $id because it has been previously declared."
            )
          }
          return F
        case _ =>
      }
      methods.get(id) match {
        case Some(otherInfo) =>
          if (name != tname) {
            reporter.error(
              posOpt,
              TypeChecker.typeCheckerKind,
              st"Cannot inherit $id because it has been previously inherited from ${(otherInfo.owner, ".")}.".render
            )
          } else {
            reporter.error(
              posOpt,
              TypeChecker.typeCheckerKind,
              s"Cannot inherit $id because it has been previously declared."
            )
          }
          return F
        case _ =>
      }
      return T
    }

    def inheritSpecVar(svInfo: Info.SpecVar, posOpt: Option[Position], substMap: HashMap[String, AST.Typed]): Unit = {
      val owner = svInfo.owner
      var sv = svInfo.ast
      val id = sv.id.value
      val ok = checkInherit(id, owner, posOpt)
      if (ok) {
        if (substMap.isEmpty) {
          specVars = specVars + id ~> svInfo
        } else {
          sv = sv(tipe = sv.tipe.typed(sv.tipe.typedOpt.get.subst(substMap)))
          specVars = specVars + id ~> svInfo(ast = sv(attr = sv.attr(typedOpt = sv.tipe.typedOpt)))
        }
      }
    }

    def inheritSpecMethod(
                           smInfo: Info.SpecMethod,
                           posOpt: Option[Position],
                           substMap: HashMap[String, AST.Typed]
                         ): Unit = {
      val owner = smInfo.owner
      var sm = smInfo.ast
      val id = sm.sig.id.value
      val ok = checkInherit(id, owner, posOpt)
      if (ok) {
        if (substMap.isEmpty) {
          specMethods = specMethods + id ~> smInfo(ast = sm)
        } else {
          var params = ISZ[AST.Param]()
          for (param <- sm.sig.params) {
            params = params :+ param(tipe = param.tipe.typed(param.tipe.typedOpt.get.subst(substMap)))
          }
          sm = sm(
            sig = sm.sig(
              params = params,
              returnType = sm.sig.returnType.typed(sm.sig.returnType.typedOpt.get.subst(substMap))
            )
          )
          specMethods = specMethods + id ~> smInfo(ast = sm(attr = sm.attr(typedOpt = Some(sm.sig.funType))))
        }
      }
    }

    def inheritInv(invInfo: Info.Inv, posOpt: Option[Position], substMap: HashMap[String, AST.Typed]): Unit = {
      val transformer = AST.Transformer[B](ExpTypedSubst(substMap))

      @pure def transform(exp: AST.Exp): AST.Exp = {
        transformer.transformExp(T, exp).resultOpt match {
          case Some(result) => return result
          case _ => return exp
        }
      }

      val owner = invInfo.owner
      val inv = invInfo.ast
      val id = inv.id.value
      val ok = checkInherit(id, owner, posOpt)
      if (ok) {
        if (substMap.isEmpty) {
          invariants = invariants + id ~> invInfo
        } else {
          val newAst = invInfo.ast(claims = for (claim <- invInfo.ast.claims) yield transform(claim),
            attr = invInfo.ast.attr(resOpt = Some(AST.ResolvedInfo.Inv(F, nameOwner, nameId)),
              typedOpt = Some(AST.Typed.Inv(F, nameOwner, nameId))))
          invariants = invariants + id ~> invInfo(ast = newAst)
        }
      }
    }

    def checkMethodEquality(
                             m1: Info.Method,
                             m2: Info.Method,
                             substMap: HashMap[String, AST.Typed]
                           ): (B, AST.Typed, AST.Typed) = {
      val t1 = m1.typedOpt.get.asInstanceOf[AST.Typed.Method].tpe.deBruijn
      val t2 = m2.typedOpt.get.asInstanceOf[AST.Typed.Method].tpe.subst(substMap).deBruijn
      return (t1 == t2, t1, t2)
    }

    def checkMethodRefinement(
                               m: Info.Method,
                               supM: Info.Method,
                               substMap: HashMap[String, AST.Typed]
                             ): (B, AST.Typed, AST.Typed) = {
      val t1 = m.typedOpt.get.deBruijn
      val t2 = supM.typedOpt.get.subst(substMap).deBruijn
      return (typeHierarchy.isRefinement(t1, t2), t1, t2)
    }

    def inheritMethod(mInfo: Info.Method, posOpt: Option[Position], substMap: HashMap[String, AST.Typed]): Unit = {
      val tname = mInfo.owner
      val pm = mInfo.ast
      val id = pm.sig.id.value

      var ok = checkSpecInherit(id, tname, posOpt)
      if (!ok) {
        return
      }
      vars.get(id) match {
        case Some(otherInfo) =>
          if (name != otherInfo.owner) {
            reporter.error(
              posOpt,
              TypeChecker.typeCheckerKind,
              st"Cannot inherit $id from ${(tname, ".")} because it has been previously inherited from ${(name, ".")}.".render
            )
            return
          } else if (!(!mInfo.ast.sig.hasParams && typeHierarchy.isSubType(
            otherInfo.typedOpt.get,
            mInfo.ast.sig.returnType.typedOpt.get
          ))) {
            reporter.error(
              posOpt,
              TypeChecker.typeCheckerKind,
              st"Cannot inherit $id from ${(tname, ".")} because it is declared with incompatible type.".render
            )
            return
          }
          return
        case _ =>
      }

      def inherit(): Unit = {
        if (substMap.isEmpty) {
          methods = methods + id ~>
            (if (mInfo.ast.bodyOpt.nonEmpty) mInfo(ast = mInfo.ast(bodyOpt = None())) else mInfo)
        } else {
          var m = pm
          var params = ISZ[AST.Param]()
          for (param <- m.sig.params) {
            params = params :+ param(tipe = param.tipe.typed(param.tipe.typedOpt.get.subst(substMap)))
          }
          m = m(
            bodyOpt = None(),
            sig = m.sig(
              params = params,
              returnType = m.sig.returnType.typed(m.sig.returnType.typedOpt.get.subst(substMap))
            )
          )
          methods = methods + id ~> mInfo(ast = m(attr = m.attr(typedOpt = Some(mInfo.typedOpt.get.subst(substMap)))))
        }
      }

      methods.get(id) match {
        case Some(otherInfo) =>
          if (name != otherInfo.owner) {
            val (isEqual, t1, t2) = checkMethodEquality(otherInfo, mInfo, substMap)
            if (isEqual) {
              if ((otherInfo.ast.bodyOpt.nonEmpty || otherInfo.ast.contract.nonEmpty) &&
                (mInfo.ast.bodyOpt.nonEmpty || mInfo.ast.contract.nonEmpty)) {
                if (otherInfo.owner != mInfo.owner) {
                  reporter.error(
                    posOpt,
                    TypeChecker.typeCheckerKind,
                    st"Explicit declaration of $id in ${(name, ".")} (or its contract) is required because it is inherited from ${(otherInfo.owner, ".")} and ${(mInfo.owner, ".")}.".render
                  )
                } else if (mInfo.methodType.collectTypeVars.nonEmpty) {
                  reporter.error(
                    posOpt,
                    TypeChecker.typeCheckerKind,
                    st"Explicit declaration of $id in ${(name, ".")} (or its contract) is required because it is polymorphic and it is inherited from ${(otherInfo.owner, ".")} more than once.".render
                  )
                }
              } else {
                if (mInfo.ast.bodyOpt.nonEmpty) {
                  inherit()
                }
              }
            } else {
              reporter.error(
                posOpt,
                TypeChecker.typeCheckerKind,
                st"Cannot inherit $id from ${(tname, ".")} because it has been previously inherited from ${(otherInfo.owner, ".")} with differing type ($t1 != $t2).".render
              )
            }
            return
          } else {
            val (refined, t1, t2) = checkMethodRefinement(otherInfo, mInfo, substMap)
            ok = refined
            if (ok) {
              refinements = refinements + id ~> TypeInfo.Name(mInfo.owner)
            } else {
              reporter.error(
                posOpt,
                TypeChecker.typeCheckerKind,
                st"Cannot inherit $id from ${(tname, ".")} because it is declared with incompatible type ($t2 does not refine $t1).".render
              )
              return
            }
            if (mInfo.hasBody && !otherInfo.hasBody) {
              reporter.error(
                posOpt,
                TypeChecker.typeCheckerKind,
                st"Cannot inherit $id from ${(tname, ".")} with implementation because it is declared but unimplemented.".render
              )
              return
            }
          }
        case _ =>
          inherit()
      }
    }

    var ancestors = HashSSet.empty[AST.Typed.Name]
    var newParents = ISZ[AST.Type.Named]()
    for (parent <- parents) {
      val tipeOpt = typeHierarchy.typed(scope, parent, reporter)
      tipeOpt match {
        case Some(tipe: AST.Type.Named) =>
          newParents = newParents :+ tipe
          tipe.typedOpt match {
            case Some(t: AST.Typed.Name) =>
              typeHierarchy.typeMap.get(t.ids) match {
                case Some(ti: TypeInfo.Sig) =>
                  val substMapOpt =
                    TypeChecker.buildTypeSubstMap(ti.name, parent.posOpt, ti.ast.typeParams, t.args, reporter)
                  substMapOpt match {
                    case Some(substMap) =>
                      val typedParent = ti.tpe.subst(substMap)
                      ancestors = ancestors + typedParent
                      val posOpt = parent.attr.posOpt
                      for (tpe <- ti.ancestors) {
                        ancestors = ancestors + tpe.subst(substMap)
                      }
                      for (p <- ti.specVars.values) {
                        inheritSpecVar(p, posOpt, substMap)
                      }
                      for (p <- ti.specMethods.values) {
                        inheritSpecMethod(p, posOpt, substMap)
                      }
                      for (p <- ti.methods.values) {
                        inheritMethod(p, posOpt, substMap)
                      }
                      for (p <- ti.invariants.values) {
                        inheritInv(p, posOpt, substMap)
                      }
                    case _ =>
                  }
                case Some(ti: TypeInfo.Adt) =>
                  val substMapOpt =
                    TypeChecker.buildTypeSubstMap(ti.name, parent.posOpt, ti.ast.typeParams, t.args, reporter)
                  substMapOpt match {
                    case Some(substMap) =>
                      val typedParent = ti.tpe.subst(substMap)
                      ancestors = ancestors + typedParent
                      val posOpt = parent.attr.posOpt
                      for (tpe <- ti.ancestors) {
                        ancestors = ancestors + tpe.subst(substMap)
                      }
                      for (p <- ti.specVars.values) {
                        inheritSpecVar(p, posOpt, substMap)
                      }
                      for (p <- ti.specMethods.values) {
                        inheritSpecMethod(p, posOpt, substMap)
                      }
                      for (p <- ti.methods.values) {
                        inheritMethod(p, posOpt, substMap)
                      }
                      for (p <- ti.invariants.values) {
                        inheritInv(p, posOpt, substMap)
                      }
                    case _ =>
                  }
                case _ =>
              }
            case _ => halt("Infeasible: type hierarchy phase should have checked type parents should be a typed name.")
          }
        case Some(_) => halt("Infeasible.")
        case _ =>
      }
    }
    return (
      TypeInfo.Members(specVars, vars, specMethods, methods, refinements, invariants),
      ancestors.elements,
      newParents,
    )
  }

  @pure def checkObjectContract(strictAliasing: B, info: Info.Object): TypeHierarchy => (TypeHierarchy, ISZ[Message])@pure = {
    val reporter = Reporter.create
    var newStmts = ISZ[AST.Stmt]()
    var nameEntries = ISZ[(QName, Info)]()
    var scope = TypeChecker.createNewScope(info.scope(enclosingName = info.name))
    scope = scope(localThisOpt = Some(AST.Typed.Object(info.owner, info.ast.id.value)))
    for (stmt <- info.ast.stmts) {
      stmt match {
        case stmt: AST.Stmt.Var =>
          val id = stmt.id.value
          val sInfo = typeHierarchy.nameMap.get(info.name :+ id).get.asInstanceOf[Info.Var]
          newStmts = newStmts :+ sInfo.ast
        case stmt: AST.Stmt.SpecVar =>
          val id = stmt.id.value
          val sInfo = typeHierarchy.nameMap.get(info.name :+ id).get.asInstanceOf[Info.SpecVar]
          newStmts = newStmts :+ sInfo.ast
        case stmt: AST.Stmt.SpecMethod =>
          val id = stmt.sig.id.value
          val sInfo = typeHierarchy.nameMap.get(info.name :+ id).get.asInstanceOf[Info.SpecMethod]
          newStmts = newStmts :+ sInfo.ast
        case stmt: AST.Stmt.JustMethod =>
          val id = stmt.sig.id.value
          val sInfo = typeHierarchy.nameMap.get(info.name :+ id).get.asInstanceOf[Info.JustMethod]
          newStmts = newStmts :+ sInfo.ast
        case stmt: AST.Stmt.Method =>
          val id = stmt.sig.id.value
          val context = info.name :+ id
          val mInfo = typeHierarchy.nameMap.get(info.name :+ id).get.asInstanceOf[Info.Method]
          if (stmt.purity == AST.Purity.StrictPure) {
            val newStmt = TypeChecker.checkStrictPureMethod(strictAliasing, typeHierarchy, context, scope, F, mInfo.ast,
              reporter)
            newStmts = newStmts :+ newStmt
            nameEntries = nameEntries :+ ((mInfo.name, mInfo(ast = newStmt)))
          } else if (stmt.hasContract) {
            val newStmt = TypeChecker.checkMethodContractSequent(strictAliasing, typeHierarchy, context, scope, F,
              mInfo.ast, reporter)
            newStmts = newStmts :+ newStmt
            nameEntries = nameEntries :+ ((mInfo.name, mInfo(ast = newStmt)))
          } else {
            newStmts = newStmts :+ mInfo.ast
          }
        case stmt: AST.Stmt.Fact =>
          val id = stmt.id.value
          val context = info.name :+ id
          val fInfo = typeHierarchy.nameMap.get(info.name :+ id).get.asInstanceOf[Info.Fact]
          val newStmt = TypeChecker.checkFactStmt(strictAliasing, typeHierarchy, context, scope, fInfo.ast, reporter)
          newStmts = newStmts :+ newStmt
          nameEntries = nameEntries :+ ((fInfo.name, fInfo(ast = newStmt)))
        case stmt: AST.Stmt.Theorem =>
          val id = stmt.id.value
          val context = info.name :+ id
          val tInfo = typeHierarchy.nameMap.get(info.name :+ id).get.asInstanceOf[Info.Theorem]
          val newStmt = TypeChecker.checkTheoremStmt(strictAliasing, typeHierarchy, context, scope, tInfo.ast, reporter)
          newStmts = newStmts :+ newStmt
          nameEntries = nameEntries :+ ((tInfo.name, tInfo(ast = newStmt)))
        case stmt: AST.Stmt.Inv =>
          val id = stmt.id.value
          val context = info.name :+ id
          val iInfo = typeHierarchy.nameMap.get(info.name :+ id).get.asInstanceOf[Info.Inv]
          val newStmt = TypeChecker.checkInvStmt(strictAliasing, typeHierarchy, context, scope, iInfo.ast, reporter)
          newStmts = newStmts :+ newStmt
          nameEntries = nameEntries :+ ((iInfo.name, iInfo(ast = newStmt)))
        case stmt: AST.Stmt.ExtMethod if stmt.contract.nonEmpty =>
          val id = stmt.sig.id.value
          val mInfo = typeHierarchy.nameMap.get(info.name :+ id).get.asInstanceOf[Info.ExtMethod]
          val mstmt = mInfo.ast
          val context = info.name :+ id
          val (ok, sc) = TypeChecker.methodScope(typeHierarchy, context, info.scope, mstmt.sig, reporter)
          if (ok) {
            val tc = TypeChecker(typeHierarchy, context, F, TypeChecker.ModeContext.Spec, strictAliasing)
            var newStmt = mstmt(contract = tc.checkMethodContract(sc, mstmt.contract, reporter))
            val reads: ISZ[AST.ResolvedInfo] = for (r <- newStmt.contract.reads) yield r.attr.resOpt.get
            val writes: ISZ[AST.ResolvedInfo] = for (w <- newStmt.contract.modifies) yield w.attr.resOpt.get
            newStmt = newStmt(attr = newStmt.attr(resOpt = Some(mInfo.methodRes(reads = reads, writes = writes))))
            newStmts = newStmts :+ newStmt
            nameEntries = nameEntries :+ ((mInfo.name, mInfo(ast = newStmt)))
          } else {
            newStmts = newStmts :+ mstmt
          }
        case _ => newStmts = newStmts :+ stmt
      }
    }
    val newInfo = info(contractOutlined = T, ast = info.ast(stmts = newStmts))
    val messages = reporter.messages
    val entries = nameEntries
    return (th: TypeHierarchy) => (th(nameMap = th.nameMap ++ entries + info.name ~> newInfo), messages)
  }

  def checkContract(strictAliasing: B, isMutableContext: B, name: QName, scope: Scope, members: TypeOutliner.TypeMembers,
                    reporter: Reporter): Unit = {
    var newStmts = ISZ[AST.Stmt]()
    for (astmt <- members.stmts) {
      astmt match {
        case astmt: AST.Stmt.SpecVar =>
          val id = astmt.id.value
          newStmts = newStmts :+ members.specVars.get(id).get.ast
        case astmt: AST.Stmt.Var =>
          val id = astmt.id.value
          newStmts = newStmts :+ members.vars.get(id).get.ast
        case astmt: AST.Stmt.SpecMethod =>
          val id = astmt.sig.id.value
          newStmts = newStmts :+ members.specMethods.get(id).get.ast
        case astmt: AST.Stmt.Method =>
          val id = astmt.sig.id.value
          val mInfo = members.methods.get(id).get
          if (astmt.purity == AST.Purity.StrictPure) {
            val stmt = mInfo.ast
            val context = name :+ stmt.sig.id.value
            val newStmt = TypeChecker.checkStrictPureMethod(strictAliasing, typeHierarchy, context, scope,
              isMutableContext, stmt, reporter)
            newStmts = newStmts :+ newStmt
            members.methods = members.methods + id ~> mInfo(ast = newStmt)
          } else if (astmt.hasContract) {
            val stmt = mInfo.ast
            val context = name :+ stmt.sig.id.value
            val newStmt = TypeChecker.checkMethodContractSequent(strictAliasing, typeHierarchy, context, scope,
              isMutableContext, stmt, reporter)
            newStmts = newStmts :+ newStmt
            members.methods = members.methods + id ~> mInfo(ast = newStmt)
          } else {
            newStmts = newStmts :+ mInfo.ast
          }
        case astmt: AST.Stmt.Inv =>
          val id = astmt.id.value
          val iInfo = members.invariants.get(id).get
          val stmt = iInfo.ast
          val context = name :+ id
          val newStmt = TypeChecker.checkInvStmt(strictAliasing, typeHierarchy, context, scope, stmt, reporter)
          newStmts = newStmts :+ newStmt
          members.invariants = members.invariants + id ~> iInfo(ast = newStmt)
        case astmt: AST.Stmt.DataRefinement =>
          val dr = TypeChecker.checkDataRefinement(strictAliasing, typeHierarchy, name, scope, astmt, reporter)
          members.drs = members.drs :+ dr
          newStmts = newStmts :+ dr
        case _ => newStmts = newStmts :+ astmt
      }
    }
    members.stmts = newStmts
  }

  @pure def checkAdtContract(strictAliasing: B, info: TypeInfo.Adt): TypeHierarchy => (TypeHierarchy, ISZ[Message])@pure = {
    val reporter = Reporter.create
    val typeParams = typeParamMap(info.ast.typeParams, reporter)
    var scope = Scope.Local.create(typeParams.map, info.scope)
    scope = scope(localThisOpt = Some(info.tpe))
    val members = TypeOutliner.TypeMembers(info.specVars, info.vars, info.specMethods, info.methods, info.invariants,
      info.dataRefinements, info.ast.stmts)
    checkContract(strictAliasing, !info.ast.isDatatype, info.name, scope, members, reporter)
    val newInfo = info(
        contractOutlined = T,
        specVars = members.specVars,
        vars = members.vars,
        specMethods = members.specMethods,
        methods = members.methods,
        invariants = members.invariants,
        dataRefinements = members.drs,
        ast = info.ast(stmts = members.stmts))
    val messages = reporter.messages
    return (th: TypeHierarchy) => (th(typeMap = th.typeMap + info.name ~> newInfo), messages)
  }

  @pure def checkSigContract(strictAliasing: B, info: TypeInfo.Sig): TypeHierarchy => (TypeHierarchy, ISZ[Message])@pure = {
    val reporter = Reporter.create
    val typeParams = typeParamMap(info.ast.typeParams, reporter)
    var scope = Scope.Local.create(typeParams.map, info.scope)
    scope = scope(localThisOpt = Some(info.tpe))
    val members = TypeOutliner.TypeMembers(info.specVars, HashSMap.empty, info.specMethods, info.methods, info.invariants,
      info.dataRefinements, info.ast.stmts)
    checkContract(strictAliasing, !info.ast.isImmutable, info.name, scope, members, reporter)
    val newInfo = info(
      contractOutlined = T,
      specVars = members.specVars,
      specMethods = members.specMethods,
      methods = members.methods,
      invariants = members.invariants,
      dataRefinements = members.drs,
      ast = info.ast(stmts = members.stmts))
    val messages = reporter.messages
    return (th: TypeHierarchy) => (th(typeMap = th.typeMap + info.name ~> newInfo), messages)
  }

}
