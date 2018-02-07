// #Sireum
/*
 Copyright (c) 2017, Robby, Kansas State University
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
import org.sireum.lang.util.Reporter
import org.sireum.lang.{ast => AST}

@record class GlobalDeclarationResolver(var globalNameMap: NameMap, var globalTypeMap: TypeMap, reporter: Reporter) {

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

      @pure def helper: (ISZ[String], String, B, String, Option[AST.PosInfo]) = {
        info match {
          case info: TypeInfo.Sig =>
            return (
              info.owner,
              info.ast.id.value,
              T,
              if (info.ast.isImmutable) "@sig trait" else "@msig trait",
              info.ast.id.attr.posOpt
            )
          case info: TypeInfo.AbstractDatatype =>
            return (
              info.owner,
              info.ast.id.value,
              T,
              if (info.ast.isRoot)
                if (info.ast.isDatatype) "@datatype trait" else "@record trait"
              else if (info.ast.isDatatype) "@datatype class"
              else "@record class",
              info.ast.id.attr.posOpt
            )
          case info: TypeInfo.SubZ =>
            return (
              info.owner,
              info.ast.id.value,
              T,
              if (info.ast.isBitVector) "@bits class" else "@range class",
              info.ast.id.attr.posOpt
            )
          case _ => return (ISZ(), "", F, "", None())
        }
      }

      val (owner, id, ok, desc, posOpt) = helper
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
            globalNameMap = globalNameMap.put(
              name,
              Info.Object(
                owner,
                T,
                T,
                AST.Stmt.Object(F, AST.Id(id, attr), ISZ(), ISZ(), attr),
                Some(AST.Typed.Object(owner, id)),
                Some(AST.ResolvedInfo.Object(name))
              )
            )
          }
      }
    }
  }

  @memoize def scope(packageName: QName, imports: ISZ[AST.Stmt.Import], name: QName): Scope.Global = {
    return Scope.Global(packageName, imports, ISZOps(name).dropRight(1))
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
            stmt,
            None(),
            Some(AST.ResolvedInfo.Var(T, F, currentName, stmt.id.value))
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
            stmt,
            None(),
            Some(AST.ResolvedInfo.Var(T, T, currentName, stmt.id.value))
          ),
          stmt.attr.posOpt
        )
      case stmt: AST.Stmt.Method =>
        val id = stmt.sig.id.value
        val name = currentName :+ id
        declareName(
          "method",
          name,
          Info.Method(
            currentName,
            T,
            scope(packageName, currentImports, name),
            stmt.bodyOpt.nonEmpty,
            stmt,
            None(),
            Some(
              AST.ResolvedInfo.Method(
                T,
                AST.MethodMode.Method,
                stmt.sig.typeParams.map(tp => tp.id.value),
                currentName,
                id,
                stmt.sig.params.map(p => p.id.value),
                None()
              )
            )
          ),
          stmt.attr.posOpt
        )
      case stmt: AST.Stmt.ExtMethod =>
        val id = stmt.sig.id.value
        val name = currentName :+ stmt.sig.id.value
        declareName(
          "extension method",
          name,
          Info.ExtMethod(
            currentName,
            scope(packageName, currentImports, name),
            stmt,
            None(),
            Some(
              AST.ResolvedInfo.Method(
                T,
                AST.MethodMode.Ext,
                stmt.sig.typeParams.map(tp => tp.id.value),
                currentName,
                id,
                stmt.sig.params.map(p => p.id.value),
                None()
              )
            )
          ),
          stmt.attr.posOpt
        )
      case stmt: AST.Stmt.SpecMethod =>
        val id = stmt.sig.id.value
        val name = currentName :+ id
        declareName(
          "specification method",
          name,
          Info.SpecMethod(
            currentName,
            T,
            scope(packageName, currentImports, name),
            stmt,
            None(),
            Some(
              AST.ResolvedInfo.Method(
                T,
                AST.MethodMode.Spec,
                stmt.sig.typeParams.map(tp => tp.id.value),
                currentName,
                id,
                stmt.sig.params.map(p => p.id.value),
                None()
              )
            )
          ),
          stmt.attr.posOpt
        )
      case stmt: AST.Stmt.SubZ =>
        val name = currentName :+ stmt.id.value
        declareType(if (stmt.isBitVector) "bits" else "range", name, TypeInfo.SubZ(currentName, stmt), stmt.attr.posOpt)
      case stmt: AST.Stmt.Enum =>
        val name = currentName :+ stmt.id.value
        var elements = Map.empty[String, Option[AST.ResolvedInfo]]
        val elementTypeName = name :+ "Type"
        val elementTypedOpt: Option[AST.Typed] = Some(AST.Typed.Name(elementTypeName, ISZ()))
        var ordinal = 0
        for (e <- stmt.elements) {
          if (elements.contains(e.value)) {
            reporter.error(e.attr.posOpt, resolverKind, s"Redeclaration of @enum element ${e.value}.")
          } else {
            elements = elements.put(e.value, Some(AST.ResolvedInfo.EnumElement(name, e.value, ordinal)))
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
      case stmt: AST.Stmt.Object =>
        val name = currentName :+ stmt.id.value

        globalTypeMap.get(name) match {
          case Some(info) =>
            val posOpt = stmt.attr.posOpt
            if (stmt.isExt | !info.canHaveCompanion) {
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
          if (stmt.isExt) "extension object" else "object",
          name,
          Info.Object(
            currentName,
            F,
            F,
            stmt,
            Some(AST.Typed.Object(currentName, stmt.id.value)),
            Some(AST.ResolvedInfo.Object(name))
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
        val members = resolveMembers(name, sc, stmt.stmts, HashMap.empty)
        assert(members.vars.isEmpty)
        val tpe = AST.Typed.Name(
          name,
          for (tVar <- typeParamMap(stmt.typeParams, reporter).keys.elements)
            yield AST.Typed.TypeVar(tVar)
        )
        declareType(
          "sig",
          name,
          TypeInfo.Sig(currentName, F, tpe, ISZ(), members.specVars, members.specMethods, members.methods, sc, stmt),
          stmt.attr.posOpt
        )
      case stmt: AST.Stmt.AbstractDatatype =>
        val name = currentName :+ stmt.id.value
        val sc = scope(packageName, currentImports, name)
        var paramVars = HashMap.empty[String, Info.Var]
        for (p <- stmt.params) {
          val id = p.id.value
          paramVars = paramVars.put(
            id,
            Info.Var(
              name,
              F,
              sc,
              AST.Stmt.Var(p.isVal, p.id, Some(p.tipe), None(), p.id.attr),
              None(),
              Some(AST.ResolvedInfo.Var(F, F, name, id))
            )
          )
        }
        val members = resolveMembers(name, sc, stmt.stmts, paramVars)
        val tpe = AST.Typed.Name(
          name,
          for (tVar <- typeParamMap(stmt.typeParams, reporter).keys.elements)
            yield AST.Typed.TypeVar(tVar)
        )
        val resOpt: Option[AST.ResolvedInfo] = Some(
          AST.ResolvedInfo.Method(
            F,
            AST.MethodMode.Constructor,
            stmt.typeParams.map(tp => tp.id.value),
            currentName,
            stmt.id.value,
            stmt.params.map(p => p.id.value),
            None()
          )
        )
        declareType(
          if (stmt.isDatatype) "datatype" else "record",
          name,
          TypeInfo.AbstractDatatype(
            currentName,
            F,
            tpe,
            None(),
            resOpt,
            ISZ(),
            members.specVars,
            members.vars,
            members.specMethods,
            members.methods,
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
      case _ =>
    }
  }

  def resolveMembers(
    owner: QName,
    scope: Scope,
    stmts: ISZ[AST.Stmt],
    vs: HashMap[String, Info.Var]
  ): TypeInfo.Members = {
    var specVars = HashMap.empty[String, Info.SpecVar]
    var vars = vs
    var specMethods = HashMap.empty[String, Info.SpecMethod]
    var methods = HashMap.empty[String, Info.Method]

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
          vars = vars.put(id, Info.Var(owner, F, scope, stmt, None(), Some(AST.ResolvedInfo.Var(F, F, owner, id))))
        case stmt: AST.Stmt.SpecVar =>
          checkId(stmt.id)
          val id = stmt.id.value
          specVars =
            specVars.put(id, Info.SpecVar(owner, F, scope, stmt, None(), Some(AST.ResolvedInfo.Var(F, T, owner, id))))
        case stmt: AST.Stmt.Method =>
          checkId(stmt.sig.id)
          val id = stmt.sig.id.value
          methods = methods.put(
            id,
            Info.Method(
              owner,
              F,
              scope,
              stmt.bodyOpt.nonEmpty,
              stmt,
              None(),
              Some(
                AST.ResolvedInfo.Method(
                  F,
                  AST.MethodMode.Method,
                  stmt.sig.typeParams.map(tp => tp.id.value),
                  owner,
                  id,
                  stmt.sig.params.map(p => p.id.value),
                  None()
                )
              )
            )
          )
        case stmt: AST.Stmt.SpecMethod =>
          checkId(stmt.sig.id)
          val id = stmt.sig.id.value
          specMethods = specMethods.put(
            id,
            Info.SpecMethod(
              owner,
              F,
              scope,
              stmt,
              None(),
              Some(
                AST.ResolvedInfo.Method(
                  F,
                  AST.MethodMode.Spec,
                  stmt.sig.typeParams.map(tp => tp.id.value),
                  owner,
                  id,
                  stmt.sig.params.map(p => p.id.value),
                  None()
                )
              )
            )
          )
        case _ =>
      }
    }
    TypeInfo.Members(specVars, vars, specMethods, methods)
  }

  def declareName(entity: String, name: QName, info: Info, posOpt: Option[AST.PosInfo]): Unit = {
    globalNameMap.get(name) match {
      case Some(_) =>
        reporter
          .error(posOpt, resolverKind, s"Cannot declare $entity because the name has already been declared previously.")
      case _ => globalNameMap = globalNameMap.put(name, info)
    }
  }

  def declareType(entity: String, name: QName, info: TypeInfo, posOpt: Option[AST.PosInfo]): Unit = {
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
      case _ => globalTypeMap = globalTypeMap.put(name, info)
    }
  }

  def declarePackage(name: QName, posOpt: Option[AST.PosInfo]): Unit = {
    globalNameMap.get(name) match {
      case Some(_: Info.Package) =>
      case Some(_) =>
        reporter.error(
          posOpt,
          resolverKind,
          "Cannot declare package because the name has already been used for a non-package entity."
        )
      case _ =>
        globalNameMap = globalNameMap
          .put(name, Info.Package(name, Some(AST.Typed.Package(name)), Some(AST.ResolvedInfo.Package(name))))
    }
  }

  def declarePackageName(name: AST.Name): Option[Info.Package] = {
    val ids = name.ids
    if (name.ids.isEmpty) {
      return Some(rootPackageInfo)
    }
    var currentName = ISZ(ids(0).value)
    var currentPosOpt = ids(0).attr.posOpt
    declarePackage(currentName, currentPosOpt)

    for (i <- z"1" until ids.size) {
      currentName = currentName :+ ids(i).value
      currentPosOpt = ids(i).attr.posOpt
      declarePackage(currentName, currentPosOpt)
    }

    globalNameMap.get(currentName) match {
      case Some(info: Info.Package) => return Some(info)
      case _ => return None()
    }
  }
}
