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

package org.sireum.lang.symbol

import org.sireum._
import org.sireum.ops._
import Resolver._
import org.sireum.message._
import org.sireum.lang.{ast => AST}

object GlobalDeclarationResolver {

  val disallowedTypeIds: HashSet[String] = HashSet ++ ISZ("Option", "MOption", "Either", "MEither")
}

import GlobalDeclarationResolver._

@record class GlobalDeclarationResolver(var globalNameMap: NameMap, var globalTypeMap: TypeMap, val reporter: Reporter) {

  var packageName: QName = ISZ()
  var currentName: QName = ISZ()
  var currentImports: ISZ[AST.Stmt.Import] = ISZ()

  def resolveProgram(program: AST.TopUnit.Program): Unit = {
    declarePackageName(program.packageName) match {
      case Some(info) =>
        packageName = info.name
        currentName = packageName
      case _ =>
    }

    for (stmt <- program.body.stmts) {
      resolveGlobalStmt(stmt)
    }

    for (info <- globalTypeMap.values) {

      @pure def helper: (ISZ[String], String, B, String, Option[Position], Scope.Global) = {
        val emptyScope = Scope.Global(ISZ(), ISZ(), ISZ())
        info match {
          case info: TypeInfo.Sig =>
            return (
              info.owner,
              info.ast.id.value,
              T,
              if (info.ast.isImmutable) "@sig trait" else "@msig trait",
              info.ast.id.attr.posOpt,
              info.scope
            )
          case info: TypeInfo.Adt =>
            return (
              info.owner,
              info.ast.id.value,
              T,
              if (info.ast.isRoot)
                if (info.ast.isDatatype) "@datatype trait" else "@record trait"
              else if (info.ast.isDatatype) "@datatype class"
              else "@record class",
              info.ast.id.attr.posOpt,
              info.scope
            )
          case info: TypeInfo.SubZ =>
            return (
              info.owner,
              info.ast.id.value,
              T,
              if (info.ast.isBitVector) "@bits class"
              else "@range class",
              info.ast.id.attr.posOpt,
              emptyScope
            )
          case _ => return (ISZ(), "", F, "", None(), emptyScope)
        }
      }

      val (owner, id, ok, desc, posOpt, scope) = helper
      val name = owner :+ id
      globalNameMap.get(name) match {
        case Some(_: Info.Object) =>
        case Some(_) =>
          reporter.error(
            info.posOpt,
            resolverKind,
            s"Cannot declare ${(info.name, ".")} as a different entity as it was used for a name of a $desc."
          )
        case _ =>
          if (ok) {
            val attr = AST.Attr(posOpt)
            globalNameMap = globalNameMap + name ~> Info.Object(
              owner,
              T,
              scope,
              T,
              T,
              T,
              AST.Stmt.Object(F, None(), AST.Id(id, attr), ISZ(), attr),
              Some(AST.Typed.Object(owner, id)),
              Some(AST.ResolvedInfo.Object(name)),
              AST.ResolvedInfo.Method(F, AST.MethodMode.ObjectConstructor, ISZ(), owner, id, ISZ(), None(), ISZ(), ISZ()),
            )
          }
      }
    }
  }

  @memoize def scope(pName: QName, imports: ISZ[AST.Stmt.Import], name: QName): Scope.Global = {
    return Scope.Global(pName, imports, ISZOps(name).dropRight(1))
  }

  def checkParams(params: ISZ[AST.Param]): ISZ[String] = {
    var paramSet = HashSSet.empty[String]
    for (p <- params) {
      val id = p.id.value
      if (paramSet.contains(id)) {
        reporter.error(p.id.attr.posOpt, resolverKind, s"Cannot redeclare parameter '$id'.")
      }
      paramSet = paramSet + id
    }
    return paramSet.elements
  }

  def resolveGlobalStmt(stmt: AST.Stmt): Unit = {
    stmt match {
      case stmt: AST.Stmt.Import =>
        currentImports = currentImports :+ stmt
      case stmt: AST.Stmt.Var =>
        val name = currentName :+ stmt.id.value
        declareName(
          if (stmt.isVal) "val" else "var",
          name,
          Info.Var(
            currentName,
            T,
            scope(packageName, currentImports, name),
            stmt(attr = stmt.attr(resOpt = Some(AST.ResolvedInfo.Var(T, F, stmt.isVal, currentName, stmt.id.value))))
          ),
          stmt.attr.posOpt
        )
      case stmt: AST.Stmt.SpecVar =>
        val name = currentName :+ stmt.id.value
        declareName(
          if (stmt.isVal) "val" else "var",
          name,
          Info.SpecVar(
            currentName,
            T,
            scope(packageName, currentImports, name),
            stmt(attr = stmt.attr(resOpt = Some(AST.ResolvedInfo.Var(T, T, stmt.isVal, currentName, stmt.id.value))))
          ),
          stmt.attr.posOpt
        )
      case stmt: AST.Stmt.RsVal =>
        val name = currentName :+ stmt.id.value
        declareName(
          "val",
          name,
          Info.RsVal(
            currentName,
            scope(packageName, currentImports, name),
            stmt(attr = stmt.attr(resOpt = Some(AST.ResolvedInfo.Var(T, T, T, currentName, stmt.id.value))))
          ),
          stmt.attr.posOpt
        )
      case stmt: AST.Stmt.Method =>
        val id = stmt.sig.id.value
        val name = currentName :+ id
        val params = checkParams(stmt.sig.params)
        declareName(
          "method",
          name,
          Info.Method(
            currentName,
            T,
            scope(packageName, currentImports, name),
            stmt.bodyOpt.nonEmpty,
            stmt(
              attr = stmt.attr(
                resOpt = Some(
                  AST.ResolvedInfo.Method(
                    T,
                    AST.MethodMode.Method,
                    stmt.sig.typeParams.map(tp => tp.id.value),
                    currentName,
                    id,
                    params,
                    None(),
                    ISZ(),
                    ISZ()
                  )
                )
              )
            )
          ),
          stmt.attr.posOpt
        )
      case stmt: AST.Stmt.ExtMethod =>
        val id = stmt.sig.id.value
        val name = currentName :+ stmt.sig.id.value
        val params = checkParams(stmt.sig.params)
        declareName(
          "extension method",
          name,
          Info.ExtMethod(
            currentName,
            scope(packageName, currentImports, name),
            stmt(
              attr = stmt.attr(
                resOpt = Some(
                  AST.ResolvedInfo.Method(
                    T,
                    AST.MethodMode.Ext,
                    stmt.sig.typeParams.map(tp => tp.id.value),
                    currentName,
                    id,
                    params,
                    None(),
                    ISZ(),
                    ISZ()
                  )
                )
              )
            )
          ),
          stmt.attr.posOpt
        )
      case stmt: AST.Stmt.JustMethod =>
        val id = stmt.sig.id.value
        val name = currentName :+ stmt.sig.id.value
        val params = checkParams(stmt.sig.params)
        declareName(
          "@just method",
          name,
          Info.JustMethod(
            currentName,
            scope(packageName, currentImports, name),
            stmt(
              attr = stmt.attr(
                resOpt = Some(
                  AST.ResolvedInfo.Method(
                    T,
                    AST.MethodMode.Just,
                    stmt.sig.typeParams.map(tp => tp.id.value),
                    currentName,
                    id,
                    params,
                    None(),
                    ISZ(),
                    ISZ()
                  )
                )
              )
            )
          ),
          stmt.attr.posOpt
        )
      case stmt: AST.Stmt.SpecMethod =>
        val id = stmt.sig.id.value
        val name = currentName :+ id
        val params = checkParams(stmt.sig.params)
        declareName(
          "specification method",
          name,
          Info.SpecMethod(
            currentName,
            T,
            scope(packageName, currentImports, name),
            stmt(
              attr = stmt.attr(
                resOpt = Some(
                  AST.ResolvedInfo.Method(
                    T,
                    AST.MethodMode.Spec,
                    stmt.sig.typeParams.map(tp => tp.id.value),
                    currentName,
                    id,
                    params,
                    None(),
                    ISZ(),
                    ISZ()
                  )
                )
              )
            )
          ),
          stmt.attr.posOpt
        )
      case stmt: AST.Stmt.SubZ =>
        val id = stmt.id.value
        val stringInterpolator = ops.StringOps(id).firstToLower
        val name = currentName :+ id
        val sc = scope(packageName, currentImports, name)
        val stringInterpolatorName = name :+ stringInterpolator
        val entity: String = if (stmt.isBitVector) "bits" else "range"
        declareType(entity, name, TypeInfo.SubZ(currentName, stmt), stmt.attr.posOpt)
        declareName(
          entity,
          stringInterpolatorName,
          Info.Object(
            name,
            T,
            sc,
            T,
            T,
            T,
            AST.Stmt.Object(F, None(), AST.Id(stringInterpolator, stmt.id.attr), ISZ(), stmt.attr),
            Some(AST.Typed.Object(name, stringInterpolator)),
            Some(AST.ResolvedInfo.Object(stringInterpolatorName)),
            AST.ResolvedInfo.Method(F, AST.MethodMode.ObjectConstructor, ISZ(), currentName, id, ISZ(), None(), ISZ(), ISZ())
          ),
          stmt.attr.posOpt
        )
      case stmt: AST.Stmt.Enum =>
        val name = currentName :+ stmt.id.value
        var elements = Map.empty[String, AST.ResolvedInfo]
        val elementTypeName = name :+ Info.Enum.elementTypeSuffix
        val elementTypedOpt: Option[AST.Typed] = Some(AST.Typed.Name(elementTypeName, ISZ()))
        var elementPosOpts: ISZ[Option[Position]] = ISZ()
        var ordinal = 0
        for (e <- stmt.elements) {
          if (elements.contains(e.value)) {
            reporter.error(e.attr.posOpt, resolverKind, s"Redeclaration of @enum element ${e.value}.")
          } else {
            elements = elements + e.value ~> AST.ResolvedInfo.EnumElement(name, e.value, ordinal)
            elementPosOpts = elementPosOpts :+ e.attr.posOpt
          }
          ordinal = ordinal + 1
        }
        declareName(
          "enumeration",
          name,
          Info.Enum(
            name,
            elements,
            Some(AST.Typed.Enum(name)),
            Some(AST.ResolvedInfo.Enum(name)),
            elementTypedOpt,
            stmt.attr.posOpt
          ),
          stmt.attr.posOpt
        )
        declareType("enumeration", elementTypeName, TypeInfo.Enum(name, elements, stmt.attr.posOpt), stmt.attr.posOpt)
        var i = 0
        for (e <- elements.entries) {
          val posOpt = elementPosOpts(i)
          declareName(
            "enumeration element",
            name :+ e._1,
            Info.EnumElement(name, e._1, elementTypedOpt, Some(e._2), posOpt),
            posOpt
          )
          i = i + 1
        }
      case stmt: AST.Stmt.Object =>
        val id = stmt.id.value
        val name = currentName :+ id
        val sc = scope(packageName, currentImports, name)
        globalTypeMap.get(name) match {
          case Some(info) =>
            val posOpt = stmt.attr.posOpt
            if (!info.canHaveCompanion) {
              reporter.error(
                posOpt,
                resolverKind,
                st"Cannot declare companion object for ${(info.name, ".")}.".render
              )
            } else if (stmt.extNameOpt.nonEmpty) {
              reporter.error(
                posOpt,
                resolverKind,
                s"Cannot declare extension object because the name has already been declared previously."
              )
            } else if (!AST.Util.fileUriOptEq(posOpt, info.posOpt)) {
              reporter.error(
                posOpt,
                resolverKind,
                s"Cannot declare companion object for a type defined in a different compilation unit."
              )
            }
          case _ =>
        }

        declareName(
          if (stmt.extNameOpt.nonEmpty) "extension object" else "object",
          name,
          Info.Object(
            currentName,
            F,
            sc,
            F,
            F,
            F,
            stmt,
            Some(AST.Typed.Object(currentName, stmt.id.value)),
            Some(AST.ResolvedInfo.Object(name)),
            AST.ResolvedInfo.Method(F, AST.MethodMode.ObjectConstructor, ISZ(), currentName, id, ISZ(), None(), ISZ(), ISZ())
          ),
          stmt.attr.posOpt
        )
        val oldName = currentName
        currentName = name
        for (s <- stmt.stmts) {
          resolveGlobalStmt(s)
        }
        currentName = oldName
      case stmt: AST.Stmt.Sig =>
        val name = currentName :+ stmt.id.value
        val sc = scope(packageName, currentImports, name)
        val members = resolveMembers(name, sc, stmt.stmts, HashSMap.empty)
        assert(members.vars.isEmpty)
        val tpe = AST.Typed.Name(
          name,
          for (p <- typeParamMap(stmt.typeParams, reporter).entries)
            yield AST.Typed.TypeVar(p._1, p._2.asInstanceOf[TypeInfo.TypeVar].ast.kind)
        )
        declareType(
          "sig",
          name,
          TypeInfo.Sig(
            currentName,
            F,
            F,
            F,
            tpe,
            ISZ(),
            members.specVars,
            members.specMethods,
            members.methods,
            members.refinements,
            members.invariants,
            ISZ(),
            sc,
            stmt
          ),
          stmt.attr.posOpt
        )
      case stmt: AST.Stmt.Adt =>
        val name = currentName :+ stmt.id.value
        val sc = scope(packageName, currentImports, name)
        var paramVars = HashSMap.empty[String, Info.Var]
        var constructorParamVars = ISZ[String]()
        var extractorParamVars = ISZ[String]()
        for (p <- stmt.params) {
          val id = p.id.value
          constructorParamVars = constructorParamVars :+ id
          if (!p.isHidden) {
            extractorParamVars = extractorParamVars :+ id
          }
          if (paramVars.contains(id)) {
            reporter.error(p.id.attr.posOpt, resolverKind, s"Cannot redeclare parameter '$id'.")
          }
          val paramResInfoOpt = Some[AST.ResolvedInfo](AST.ResolvedInfo.Var(F, F, p.isVal, name, id))
          paramVars = paramVars + id ~> Info.Var(
            name,
            F,
            sc,
            AST.Stmt.Var(F, p.isVal, p.id, Some(p.tipe), None(),
              AST.ResolvedAttr(posOpt = p.id.attr.posOpt, resOpt = paramResInfoOpt, typedOpt = None()))
          )
        }
        val members = resolveMembers(name, sc, stmt.stmts, paramVars)
        val typeParams = typeParamMap(stmt.typeParams, reporter)
        val typeVars = typeParams.keys
        val tpe = AST.Typed.Name(name, for (p <- typeParams.entries) yield AST.Typed.TypeVar(p._1, p._2.asInstanceOf[TypeInfo.TypeVar].ast.kind))
        val constructorResOpt: Option[AST.ResolvedInfo] = Some(
          AST.ResolvedInfo
            .Method(F, AST.MethodMode.Constructor, typeVars, currentName, stmt.id.value, constructorParamVars, None(), ISZ(), ISZ())
        )
        val extractorResOpt: Option[AST.ResolvedInfo] = Some(
          AST.ResolvedInfo
            .Method(F, AST.MethodMode.Extractor, typeVars, currentName, stmt.id.value, extractorParamVars, None(), ISZ(), ISZ())
        )
        declareType(
          if (stmt.isDatatype) "datatype" else "record",
          name,
          TypeInfo.Adt(
            currentName,
            F,
            F,
            F,
            tpe,
            None(),
            constructorResOpt,
            Map.empty,
            extractorResOpt,
            ISZ(),
            members.specVars,
            members.vars,
            members.specMethods,
            members.methods,
            members.refinements,
            members.invariants,
            ISZ(),
            sc,
            stmt
          ),
          stmt.attr.posOpt
        )
      case stmt: AST.Stmt.TypeAlias =>
        val name = currentName :+ stmt.id.value
        declareType(
          "type alias",
          name,
          TypeInfo.TypeAlias(name, scope(packageName, currentImports, name), stmt),
          stmt.attr.posOpt
        )
      case stmt: AST.Stmt.Fact =>
        val name = currentName :+ stmt.id.value
        declareName(
          "Fact",
          name,
          Info.Fact(
            currentName,
            stmt.id.value,
            scope(packageName, currentImports, name),
            stmt(attr = stmt.attr(resOpt = Some(AST.ResolvedInfo.Fact(name)),
              typedOpt = Some(AST.Typed.Fact(currentName, stmt.id.value))))
          ),
          stmt.attr.posOpt
        )
      case stmt: AST.Stmt.Theorem =>
        val name = currentName :+ stmt.id.value
        declareName(
          if (stmt.isLemma) "Lemma" else "Theorem",
          name,
          Info.Theorem(
            currentName,
            stmt.id.value,
            scope(packageName, currentImports, name),
            stmt(attr = stmt.attr(resOpt = Some(AST.ResolvedInfo.Theorem(name)),
              typedOpt = Some(AST.Typed.Theorem(currentName, stmt.id.value))))
          ),
          stmt.attr.posOpt
        )
      case stmt: AST.Stmt.Inv =>
        val name = currentName :+ stmt.id.value
        declareName(
          "Invariant",
          name,
          Info.Inv(
            currentName,
            stmt.id.value,
            scope(packageName, currentImports, name),
            stmt(attr = stmt.attr(resOpt = Some(AST.ResolvedInfo.Inv(T, currentName, stmt.id.value)),
              typedOpt = Some(AST.Typed.Inv(T, currentName, stmt.id.value))))
          ),
          stmt.attr.posOpt
        )
      case _ =>
    }
  }

  def resolveMembers(
    owner: QName,
    scope: Scope,
    stmts: ISZ[AST.Stmt],
    vs: HashSMap[String, Info.Var]
  ): TypeInfo.Members = {
    var specVars = HashSMap.empty[String, Info.SpecVar]
    var vars = vs
    var specMethods = HashSMap.empty[String, Info.SpecMethod]
    var methods = HashSMap.empty[String, Info.Method]
    var invs = HashSMap.empty[String, Info.Inv]

    @pure def checkId(id: AST.Id): Unit = {
      val name = id.value
      val declared: B =
        if (specVars.contains(name)) T
        else if (vars.contains(name)) T
        else if (specMethods.contains(name)) T
        else if (methods.contains(name)) T
        else F
      if (declared) {
        reporter.error(id.attr.posOpt, resolverKind, s"Member $name has been previously declared.")
      }
    }

    for (stmt <- stmts) {
      stmt match {
        case stmt: AST.Stmt.Var =>
          checkId(stmt.id)
          val id = stmt.id.value
          vars = vars + id ~> Info.Var(owner, F, scope,
            stmt(attr = stmt.attr(resOpt = Some(AST.ResolvedInfo.Var(F, F, stmt.isVal, owner, id)))))
        case stmt: AST.Stmt.SpecVar =>
          checkId(stmt.id)
          val id = stmt.id.value
          specVars =
            specVars + id ~> Info.SpecVar(owner, F, scope,
              stmt(attr = stmt.attr(resOpt = Some(AST.ResolvedInfo.Var(F, T, stmt.isVal, owner, id)))))
        case stmt: AST.Stmt.Method =>
          checkId(stmt.sig.id)
          val id = stmt.sig.id.value
          val params = checkParams(stmt.sig.params)
          methods = methods + id ~> Info.Method(
            owner,
            F,
            scope,
            stmt.bodyOpt.nonEmpty,
            stmt(
              attr = stmt.attr(
                resOpt = Some(
                  AST.ResolvedInfo.Method(
                    F,
                    AST.MethodMode.Method,
                    stmt.sig.typeParams.map(tp => tp.id.value),
                    owner,
                    id,
                    params,
                    None(),
                    ISZ(),
                    ISZ()
                  )
                )
              )
            )
          )

        case stmt: AST.Stmt.SpecMethod =>
          checkId(stmt.sig.id)
          val id = stmt.sig.id.value
          val params = checkParams(stmt.sig.params)
          specMethods = specMethods + id ~> Info.SpecMethod(
            owner,
            F,
            scope,
            stmt(
              attr = stmt.attr(
                resOpt = Some(
                  AST.ResolvedInfo.Method(
                    F,
                    AST.MethodMode.Spec,
                    stmt.sig.typeParams.map(tp => tp.id.value),
                    owner,
                    id,
                    params,
                    None(),
                    ISZ(),
                    ISZ()
                  )
                )
              )
            )
          )
        case stmt: AST.Stmt.Inv =>
          checkId(stmt.id)
          val id = stmt.id.value
          invs = invs + id ~> Info.Inv(
            owner,
            id,
            scope,
            stmt(attr = stmt.attr(resOpt = Some(AST.ResolvedInfo.Inv(F, owner, id)),
              typedOpt = Some(AST.Typed.Inv(F, owner, id))))
          )

        case _ =>
      }
    }
    return TypeInfo.Members(specVars, vars, specMethods, methods, HashSMap.empty, invs)
  }

  def declareName(entity: String, name: QName, info: Info, posOpt: Option[Position]): Unit = {
    assert(name == info.name)
    globalNameMap.get(name) match {
      case Some(_) =>
        reporter
          .error(posOpt, resolverKind, s"Cannot declare $entity because the name has already been declared previously.")
      case _ => globalNameMap = globalNameMap + name ~> info
    }
  }

  def declareType(entity: String, name: QName, info: TypeInfo, posOpt: Option[Position]): Unit = {
    assert(name == info.name)
    if (disallowedTypeIds.contains(name(name.size - 1)) && ops.ISZOps(name).dropRight(1) != AST.Typed.sireumName) {
      reporter.error(
        posOpt,
        resolverKind,
        s"Cannot declare $entity because '${name(name.size - 1)}' is a reserved type identifier in Slang."
      )
    }
    globalNameMap.get(name) match {
      case Some(_: Info.Object) =>
        if (!info.canHaveCompanion) {
          reporter.error(
            posOpt,
            resolverKind,
            s"Cannot declare $entity because the name has already been declared previously."
          )
        } else if (!AST.Util.fileUriOptEq(posOpt, info.posOpt)) {
          reporter.error(
            posOpt,
            resolverKind,
            s"Cannot declare $entity whose object companion is in a different compilation unit."
          )
        }
      case Some(_) =>
        reporter
          .error(posOpt, resolverKind, s"Cannot declare $entity because the name has already been declared previously.")
      case _ =>
    }
    globalTypeMap.get(name) match {
      case Some(_) =>
        reporter
          .error(posOpt, resolverKind, s"Cannot declare $entity because the name has already been declared previously.")
      case _ => globalTypeMap = globalTypeMap + name ~> info
    }
  }

  def declarePackage(name: QName, posOpt: Option[Position]): Unit = {
    globalNameMap.get(name) match {
      case Some(_: Info.Package) =>
      case Some(_) =>
        reporter.error(
          posOpt,
          resolverKind,
          "Cannot declare package because the name has already been used for a non-package entity."
        )
      case _ =>
        globalNameMap = globalNameMap + name ~> Info
          .Package(name, Some(AST.Typed.Package(name)), Some(AST.ResolvedInfo.Package(name)))
    }
  }

  def declarePackageName(name: AST.Name): Option[Info.Package] = {
    val ids = name.ids
    if (name.ids.isEmpty) {
      return Some(rootPackageInfo)
    }
    var currName = ISZ(ids(0).value)
    var currentPosOpt = ids(0).attr.posOpt
    declarePackage(currName, currentPosOpt)

    for (i <- z"1" until ids.size) {
      currName = currName :+ ids(i).value
      currentPosOpt = ids(i).attr.posOpt
      declarePackage(currName, currentPosOpt)
    }

    globalNameMap.get(currName) match {
      case Some(info: Info.Package) => return Some(info)
      case _ => return None()
    }
  }
}
