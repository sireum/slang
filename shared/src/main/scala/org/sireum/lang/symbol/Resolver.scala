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
import org.sireum.lang.parser.Parser
import org.sireum.ops._
import org.sireum.lang.util.{AccumulatingReporter, Reporter}
import org.sireum.lang.{ast => AST}

object Resolver {

  type QName = ISZ[String]

  type NameMap = HashMap[QName, Info]

  type TypeMap = HashMap[QName, TypeInfo]

  val resolverKind: String = "Slang Resolver"

  val rootPackageInfo: Info.Package =
    Info.Package(ISZ(), Some(AST.Typed.Package(ISZ())), Some(AST.ResolvedInfo.Package(ISZ())))

  @pure def ltTypeInfo(ti1: TypeInfo, ti2: TypeInfo): B = {
    (ti1.posOpt, ti2.posOpt) match {
      case (Some(pos1), Some(pos2)) => return pos1.offset < pos2.offset
      case _ => return F
    }
  }

  @pure def sortedGlobalTypes(globalTypeMap: TypeMap): ISZ[TypeInfo] = {
    return ISZOps(globalTypeMap.values).sortWith(ltTypeInfo)
  }

  @pure def typePoset(globalTypeMap: TypeMap, globalTypes: ISZ[TypeInfo], reporter: Reporter): Poset[QName] = {
    var r = Poset.empty[QName]

    for (ti <- globalTypes) {
      ti match {
        case ti: TypeInfo.AbstractDatatype if ti.ast.isRoot => r = r.addNode(ti.name)
        case ti: TypeInfo.Sig => r = r.addNode(ti.name)
        case _ =>
      }
    }
    for (ti <- globalTypes) {
      ti match {
        case ti: TypeInfo.AbstractDatatype if !ti.ast.isRoot =>
          for (t <- ti.ast.parents) {
            ti.scope.resolveType(globalTypeMap, AST.Util.ids2strings(t.name.ids)) match {
              case Some(parent: TypeInfo.AbstractDatatype) if parent.ast.isDatatype == ti.ast.isDatatype =>
                r = r.addChildren(parent.name, ISZ(ti.name))
              case Some(parent: TypeInfo.Sig) if parent.ast.isImmutable == ti.ast.isDatatype =>
                r = r.addChildren(parent.name, ISZ(ti.name))
              case Some(_) =>
                reporter.error(
                  t.attr.posOpt,
                  resolverKind,
                  st"${(ti.name, ".")} cannot extend ${(AST.Util.ids2strings(t.name.ids), ".")}.".render
                )
              case _ =>
                reporter.error(
                  t.attr.posOpt,
                  resolverKind,
                  st"Could not find ${(ti.name, ".")}'s super type ${(AST.Util.ids2strings(t.name.ids), ".")}.".render
                )
            }
          }
        case ti: TypeInfo.Sig =>
          for (t <- ti.ast.parents) {
            ti.scope.resolveType(globalTypeMap, AST.Util.ids2strings(t.name.ids)) match {
              case Some(parent: TypeInfo.Sig) if parent.ast.isImmutable == ti.ast.isImmutable =>
                r = r.addChildren(parent.name, ISZ(ti.name))
              case Some(_) =>
                reporter.error(
                  t.attr.posOpt,
                  resolverKind,
                  st"${(ti.name, ".")} cannot extend ${(AST.Util.ids2strings(t.name.ids), ".")}.".render
                )
              case _ =>
                reporter.error(
                  t.attr.posOpt,
                  resolverKind,
                  st"Could not find ${(ti.name, ".")}'s super type ${(AST.Util.ids2strings(t.name.ids), ".")}.".render
                )
            }
          }
        case _ =>
      }
    }
    return r
  }

  @pure def relQName(name: QName, ids: QName): QName = {
    val sz = name.size
    if (ids.size <= sz) {
      return ids
    }
    var i = z"0"
    while (i < name.size) {
      if (ids(i) != name(i)) {
        return ids
      }
      i = i + 1
    }
    return ISZOps(ids).drop(sz)
  }

  def typeString(name: QName, t: AST.Type, reporter: Reporter): ST = {
    t match {
      case t: AST.Type.Named => return typeNameString(name, relQName(name, AST.Util.ids2strings(t.name.ids)))
      case _ =>
        reporter.internalError(t.posOpt, resolverKind, s"Unexpected type $t.")
        return st""
    }
  }

  def typeParamsScope(tps: ISZ[AST.TypeParam], scope: Scope, reporter: Reporter): Scope = {
    var typeMap = HashMap.empty[String, TypeInfo]
    for (tp <- tps) {
      val id = tp.id.value
      if (typeMap.contains(id)) {
        reporter.error(tp.id.attr.posOpt, resolverKind, s"Redeclaration of type parameter '$id'.")
      } else {
        typeMap = typeMap + id ~> TypeInfo.TypeVar(id, tp)
      }
    }
    return Scope.Local(HashMap.empty, typeMap, None(), None(), Some(scope))
  }

  @pure def typeNameString(name: QName, ids: QName): ST = {
    return st"${(relQName(name, ids), ".")}"
  }

  @pure def typeName(name: QName, ids: QName): ST = {
    return st"${relQName(name, ids)}"
  }

  @pure def addBuiltIns(nameMap: NameMap, typeMap: TypeMap): (NameMap, TypeMap) = {
    if (typeMap.contains(AST.Typed.iszName)) {
      return (nameMap, typeMap)
    }

    val emptyAttr = AST.Attr(None[AST.PosInfo]())
    val dollar = AST.Exp
      .Ident(AST.Id("$", emptyAttr), AST.ResolvedAttr(None[AST.PosInfo](), None[AST.ResolvedInfo](), None[AST.Typed]()))

    val dollarAssignExp = AST.Stmt.Expr(dollar, AST.TypedAttr(None(), None()))

    val scope = Scope.Global(AST.Typed.sireumName, ISZ[AST.Stmt.Import](), ISZ[String]())

    var tm = typeMap + AST.Typed.iszName ~>
      TypeInfo.TypeAlias(AST.Typed.iszName, scope, Parser("type ISZ[T] = IS[Z, T]").parseStmt[AST.Stmt.TypeAlias])

    tm = tm + AST.Typed.mszName ~>
      TypeInfo.TypeAlias(AST.Typed.mszName, scope, Parser("type MSZ[T] = MS[Z, T]").parseStmt[AST.Stmt.TypeAlias])

    tm = tm + AST.Typed.zsName ~>
      TypeInfo.TypeAlias(AST.Typed.zsName, scope, Parser("type ZS = MS[Z, Z]").parseStmt[AST.Stmt.TypeAlias])

    val tName = AST.Typed.sireumName :+ "T"
    var nm = nameMap + tName ~> Info.Var(
      AST.Typed.sireumName,
      T,
      scope,
      Parser("val T: B = true").parseStmt[AST.Stmt.Var](initOpt = Some(dollarAssignExp)),
      None(),
      Some(AST.ResolvedInfo.Var(T, F, tName, "T"))
    )

    val fName = AST.Typed.sireumName :+ "F"
    nm = nm + fName ~> Info.Var(
      AST.Typed.sireumName,
      T,
      scope,
      Parser("val F: B = false").parseStmt[AST.Stmt.Var](initOpt = Some(dollarAssignExp)),
      None(),
      Some(AST.ResolvedInfo.Var(T, F, tName, "F"))
    )

    tm = tm + AST.Typed.unit.ids ~> TypeInfo.AbstractDatatype(
      AST.Typed.sireumName,
      T,
      T,
      AST.Typed.unit,
      None(),
      None(),
      Map.empty,
      None(),
      ISZ(),
      HashMap.empty,
      HashMap.empty,
      HashMap.empty,
      HashMap.empty,
      scope,
      AST.Stmt.AbstractDatatype(T, T, AST.Id("Unit", emptyAttr), ISZ(), ISZ(), ISZ(), ISZ(), emptyAttr)
    )

    tm = tm + AST.Typed.nothing.ids ~> TypeInfo.AbstractDatatype(
      AST.Typed.sireumName,
      T,
      T,
      AST.Typed.nothing,
      None(),
      None(),
      Map.empty,
      None(),
      ISZ(),
      HashMap.empty,
      HashMap.empty,
      HashMap.empty,
      HashMap.empty,
      scope,
      AST.Stmt.AbstractDatatype(T, T, AST.Id("Nothing", emptyAttr), ISZ(), ISZ(), ISZ(), ISZ(), emptyAttr)
    )

    return (nm, tm)

  }

  @pure def combine(
    r: (AccumulatingReporter, NameMap, TypeMap),
    u: (AccumulatingReporter, NameMap, TypeMap)
  ): (AccumulatingReporter, NameMap, TypeMap) = {
    var rNameMap = r._2
    var rTypeMap = r._3
    val uNameMap = u._2
    val uTypeMap = u._3
    val reporter = AccumulatingReporter.combine(r._1, u._1)
    for (p <- uNameMap.entries) {
      val name = p._1
      val uInfo = p._2
      rNameMap.get(name) match {
        case Some(rInfo) =>
          (rInfo, uInfo) match {
            case (_: Info.Package, _: Info.Package) =>
            case _ =>
              rInfo.posOpt match {
                case Some(pos) =>
                  val file: String = pos.fileUriOpt match {
                    case Some(fileUri) => s" in $fileUri"
                    case _ => ""
                  }
                  reporter.error(
                    uInfo.posOpt,
                    resolverKind,
                    st"Name '${(name, ".")}' has already been declared at [${pos.beginLine}, ${pos.beginColumn}]$file".render
                  )
                case _ =>
              }
          }
        case _ => rNameMap = rNameMap + name ~> uInfo
      }
    }
    for (p <- uTypeMap.entries) {
      val name = p._1
      val uInfo = p._2
      rTypeMap.get(name) match {
        case Some(rInfo) =>
          rInfo.posOpt match {
            case Some(pos) =>
              val file: String = pos.fileUriOpt match {
                case Some(fileUri) => s" in $fileUri"
                case _ => ""
              }
              reporter.error(
                uInfo.posOpt,
                resolverKind,
                st"Type named '${(name, ".")}' has already been declared at [${pos.beginLine}, ${pos.beginColumn}]$file".render
              )
            case _ =>
          }
        case _ => rTypeMap = rTypeMap + name ~> uInfo
      }
    }
    return (reporter, rNameMap, rTypeMap)
  }

  def parseProgramAndGloballyResolve(
    sources: ISZ[(Option[String], String)],
    nameMap: NameMap,
    typeMap: TypeMap
  ): (AccumulatingReporter, NameMap, TypeMap) = {
    def parseGloballyResolve(p: (Option[String], String)): (AccumulatingReporter, NameMap, TypeMap) = {
      val reporter = AccumulatingReporter.create
      val r = Parser(p._2).parseTopUnit[AST.TopUnit.Program](T, F, F, p._1, reporter)
      val nameMap = HashMap.empty[QName, Info]
      val typeMap = HashMap.empty[QName, TypeInfo]
      if (reporter.hasError) {
        return (reporter, nameMap, typeMap)
      }
      r match {
        case Some(program) =>
          val gdr = GlobalDeclarationResolver(nameMap, typeMap, AccumulatingReporter.create)
          gdr.resolveProgram(program)
          reporter.reports(gdr.reporter.messages)
          return (reporter, gdr.globalNameMap, gdr.globalTypeMap)
        case _ => return (reporter, nameMap, typeMap)
      }
    }

    val t = ISZOps(sources)
      .mParMapFoldLeft[(AccumulatingReporter, NameMap, TypeMap), (AccumulatingReporter, NameMap, TypeMap)](
        parseGloballyResolve _,
        combine _,
        (AccumulatingReporter.create, nameMap, typeMap)
      )
    val p = addBuiltIns(t._2, t._3)
    return (t._1, p._1, p._2)
  }

  def typeParamMap(typeParams: ISZ[AST.TypeParam], reporter: Reporter): HashSMap[String, TypeInfo] = {
    val r = typeParamMapInit(typeParams, HashSMap.empty[String, TypeInfo], reporter)
    return r
  }

  def typeParamMapInit(
    typeParams: ISZ[AST.TypeParam],
    init: HashSMap[String, TypeInfo],
    reporter: Reporter
  ): HashSMap[String, TypeInfo] = {
    var r = init
    for (tp <- typeParams) {
      val id = tp.id.value
      if (r.contains(id)) {
        reporter.error(tp.id.attr.posOpt, resolverKind, s"Redeclaration of type parameter $id.")
      }
      r = r + id ~> TypeInfo.TypeVar(id, tp)
    }
    return r
  }

  @pure def localTypeScope(typeMap: HashMap[String, TypeInfo], outer: Scope): Scope.Local = {
    return Scope.Local(HashMap.empty[String, Info], typeMap, None(), None(), Some(outer))
  }

}
