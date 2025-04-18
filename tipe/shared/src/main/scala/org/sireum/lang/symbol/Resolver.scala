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
import org.sireum.message._
import org.sireum.lang.{ast => AST}

object Resolver {

  type QName = ISZ[String]

  type NameMap = HashSMap[QName, Info]

  type TypeMap = HashSMap[QName, TypeInfo]

  val resolverKind: String = "Slang Resolver"

  val rootPackageInfo: Info.Package =
    Info.Package(ISZ(), Some(AST.Typed.Package(ISZ())), Some(AST.ResolvedInfo.Package(ISZ())))

  @pure def addBuiltIns(nameMap: NameMap, typeMap: TypeMap): (NameMap, TypeMap) = {
    if (typeMap.contains(AST.Typed.iszName)) {
      return (nameMap, typeMap)
    }

    val emptyAttr = AST.Attr(None[Position]())
    val emptyTypedAttr = AST.TypedAttr(None(), None())

    @pure def id(name: String): AST.Id = {
      return AST.Id(name, emptyAttr)
    }

    @pure def tname(name: String, args: ISZ[AST.Type]): AST.Type.Named = {
      return AST.Type.Named(AST.Name(ISZ(id(name)), emptyAttr), args, emptyTypedAttr)
    }

    val dollar = AST.Exp
      .Ident(AST.Id("$", emptyAttr), AST.ResolvedAttr(None[Position](), None[AST.ResolvedInfo](), None[AST.Typed]()))
    val dollarAssignExp = AST.Stmt.Expr(dollar, AST.TypedAttr(None(), None()))
    val scope = Scope.Global(AST.Typed.sireumName, ISZ[AST.Stmt.Import](), ISZ[String]())

    val tName = AST.Typed.sireumName :+ "T"
    var nm = nameMap + tName ~> Info.Var(
      AST.Typed.sireumName,
      T,
      scope,
      AST.Stmt.Var(F, T, id("T"), Some(tname("B", ISZ())), Some(dollarAssignExp),
        AST.ResolvedAttr(None[Position](),
          Some[AST.ResolvedInfo](AST.ResolvedInfo.Var(T, F, T, AST.Typed.sireumName, "T")), AST.Typed.bOpt)))

    val fName = AST.Typed.sireumName :+ "F"
    nm = nm + fName ~> Info.Var(
      AST.Typed.sireumName,
      T,
      scope,
      AST.Stmt.Var(F, T, id("F"), Some(tname("B", ISZ())), Some(dollarAssignExp),
        AST.ResolvedAttr(None[Position](),
          Some[AST.ResolvedInfo](AST.ResolvedInfo.Var(T, F, T, AST.Typed.sireumName, "F")), AST.Typed.bOpt)))

    val randomIntId = "randomInt"
    val randomIntTyped = AST.Typed.Fun(AST.Purity.Impure, F, ISZ(), AST.Typed.z)
    val randomIntName = AST.Typed.sireumName :+ randomIntId
    val zType = AST.Type.Named(AST.Name(for (id <- AST.Typed.zName) yield AST.Id(id, emptyAttr), emptyAttr), ISZ(),
      AST.TypedAttr(None(), Some(AST.Typed.z)))
    nm = nm + randomIntName ~> Info.ExtMethod(AST.Typed.sireumName, scope,
      AST.Stmt.ExtMethod(F, AST.MethodSig(AST.Purity.Impure, ISZ(), AST.Id(randomIntId, emptyAttr), ISZ(), F, ISZ(), zType),
        AST.MethodContract.Simple.empty,
        AST.ResolvedAttr(None[Position](), Some[AST.ResolvedInfo](AST.ResolvedInfo.Method(
          T, AST.MethodMode.Ext, ISZ(), AST.Typed.sireumName, randomIntId, ISZ(), Some(randomIntTyped), ISZ(), ISZ())),
          Some(AST.Typed.Method(T, AST.MethodMode.Ext, ISZ(), AST.Typed.sireumName, randomIntId, ISZ(), randomIntTyped)))))

    val seqIndexValidSizeId = "seqIndexValidSize"
    val seqIndexValidSizeTyped = AST.Typed.Fun(AST.Purity.Pure, F, ISZ(AST.Typed.z), AST.Typed.b)
    val seqIndexValidSizeName = AST.Typed.sireumName :+ seqIndexValidSizeId
    val seqIndexValidSizeTypeParamId = "I"
    val seqIndexValidSizeParamId = "size"
    val bType = AST.Type.Named(AST.Name(for (id <- AST.Typed.bName) yield AST.Id(id, emptyAttr), emptyAttr), ISZ(),
      AST.TypedAttr(None(), Some(AST.Typed.b)))
    nm = nm + seqIndexValidSizeName ~> Info.SpecMethod(AST.Typed.sireumName, T, scope,
      AST.Stmt.SpecMethod(
        AST.MethodSig(AST.Purity.Pure, ISZ(), AST.Id(seqIndexValidSizeId, emptyAttr), ISZ(
          AST.TypeParam(AST.Id(seqIndexValidSizeTypeParamId, emptyAttr), AST.Typed.VarKind.Immutable)), F, ISZ(
          AST.Param(F, AST.Id(seqIndexValidSizeParamId, emptyAttr), zType)), bType),
        AST.ResolvedAttr(None[Position](), Some[AST.ResolvedInfo](AST.ResolvedInfo.Method(T, AST.MethodMode.Spec,
          ISZ(seqIndexValidSizeTypeParamId), AST.Typed.sireumName, seqIndexValidSizeId, ISZ(seqIndexValidSizeParamId),
          Some(seqIndexValidSizeTyped), ISZ(), ISZ())), Some(AST.Typed.Method(T, AST.MethodMode.Spec,
          ISZ(seqIndexValidSizeTypeParamId), AST.Typed.sireumName, seqIndexValidSizeId, ISZ(seqIndexValidSizeParamId),
            seqIndexValidSizeTyped)))))

    var tm = typeMap + AST.Typed.iszName ~>
      TypeInfo.TypeAlias(
        AST.Typed.iszName,
        scope,
        AST.Stmt.TypeAlias(
          id("ISZ"),
          ISZ(AST.TypeParam(id("T"), AST.Typed.VarKind.Immutable)),
          tname("IS", ISZ(tname("Z", ISZ()), tname("T", ISZ()))),
          emptyAttr
        )
      )

    tm = tm + AST.Typed.mszName ~>
      TypeInfo.TypeAlias(
        AST.Typed.mszName,
        scope,
        AST.Stmt.TypeAlias(
          id("MSZ"),
          ISZ(AST.TypeParam(id("T"), AST.Typed.VarKind.Mutable)),
          tname("MS", ISZ(tname("Z", ISZ()), tname("T", ISZ()))),
          emptyAttr
        )
      )

    tm = tm + AST.Typed.zsName ~>
      TypeInfo.TypeAlias(
        AST.Typed.zsName,
        scope,
        AST.Stmt.TypeAlias(id("ZS"), ISZ(), tname("MS", ISZ(tname("Z", ISZ()), tname("Z", ISZ()))), emptyAttr)
      )

    tm = tm + AST.Typed.unit.ids ~> TypeInfo.Adt(
      AST.Typed.sireumName,
      T,
      T,
      T,
      AST.Typed.unit,
      None(),
      None(),
      Map.empty,
      None(),
      ISZ(),
      HashSMap.empty,
      HashSMap.empty,
      HashSMap.empty,
      HashSMap.empty,
      HashSMap.empty,
      HashSMap.empty,
      ISZ(),
      scope,
      AST.Stmt.Adt(T, T, F, AST.Id("Unit", emptyAttr), ISZ(), ISZ(), ISZ(), ISZ(), emptyAttr)
    )

    tm = tm + AST.Typed.nothing.ids ~> TypeInfo.Adt(
      AST.Typed.sireumName,
      T,
      T,
      T,
      AST.Typed.nothing,
      None(),
      None(),
      Map.empty,
      None(),
      ISZ(),
      HashSMap.empty,
      HashSMap.empty,
      HashSMap.empty,
      HashSMap.empty,
      HashSMap.empty,
      HashSMap.empty,
      ISZ(),
      scope,
      AST.Stmt.Adt(T, T, F, AST.Id("Nothing", emptyAttr), ISZ(), ISZ(), ISZ(), ISZ(), emptyAttr)
    )

    return (nm, tm)

  }

  @pure def ltTypeInfo(uriLt: (String, String) => B @pure): (TypeInfo, TypeInfo) => B @pure = {
    return { (ti1: TypeInfo, ti2: TypeInfo) =>
      (ti1.posOpt, ti2.posOpt) match {
        case (Some(pos1), Some(pos2)) =>
          (pos1.uriOpt, pos2.uriOpt) match {
            case (Some(uri1), Some(uri2)) =>
              if (uriLt(uri1, uri2)) T
              else if (uriLt(uri2, uri1)) F
              else pos1.offset < pos2.offset
            case _ => pos1.offset < pos2.offset
          }
        case _ => ti1.name.size < ti2.name.size
      }
    }
  }

  @pure def uriLt(uri1: String, uri2: String): B = {
    return uri1.size < uri2.size
  }

  @pure def uriLtOrder(uris: ISZ[String]): (String, String) => B @pure = {
    @pure def lt(map: HashMap[String, Z]): (String, String) => B @pure = {
      return (uri1: String, uri2: String) => map.get(uri1).get < map.get(uri2).get
    }
    var map = HashMap.emptyInit[String, Z](uris.size)
    for (uri <- uris) {
      map = map + uri ~> map.size
    }
    return lt(map)
  }

  @pure def sortedGlobalTypesUriLt(globalTypeMap: TypeMap, uriLt: (String, String) => B @pure): ISZ[TypeInfo] = {
    return ISZOps(globalTypeMap.values).sortWith(ltTypeInfo(uriLt))
  }

  @pure def sortedGlobalTypes(globalTypeMap: TypeMap): ISZ[TypeInfo] = {
    return ISZOps(globalTypeMap.values).sortWith(ltTypeInfo(uriLt _))
  }

  @pure def typePoset(globalTypeMap: TypeMap, globalTypes: ISZ[TypeInfo], reporter: Reporter): Poset[QName] = {
    var r = Poset.empty[QName]

    for (ti <- globalTypes) {
      ti match {
        case ti: TypeInfo.Adt if ti.ast.isRoot => r = r.addNode(ti.name)
        case ti: TypeInfo.Sig => r = r.addNode(ti.name)
        case _ =>
      }
    }
    for (ti <- globalTypes) {
      ti match {
        case ti: TypeInfo.Adt =>
          for (t <- ti.ast.parents) {
            ti.scope.resolveType(globalTypeMap, AST.Util.ids2strings(t.name.ids)) match {
              case Some(parent: TypeInfo.Adt) if parent.ast.isDatatype == ti.ast.isDatatype =>
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

  def checkImport(packageName: QName, stmt: AST.Stmt.Import, nameMap: NameMap, typeMap: TypeMap,
                  reporter: Reporter): Unit = {
    def resolve(posOpt: Option[Position], ids: ISZ[String]): Unit = {
      if (typeMap.get(ids).isEmpty && typeMap.get(packageName ++ ids).isEmpty && nameMap.get(ids).isEmpty &&
        nameMap.get(packageName ++ ids).isEmpty) {
        reporter.error(posOpt, resolverKind, st"Could not resolve '${(ids, ".")}'.".render)
      }
    }
    for (importer <- stmt.importers) {
      val name = AST.Util.ids2strings(importer.name.ids)
      importer.selectorOpt match {
        case Some(selector: AST.Stmt.Import.MultiSelector) =>
          for (ns <- selector.selectors) {
            resolve(ns.from.attr.posOpt, name :+ ns.from.value)
          }
        case Some(_: AST.Stmt.Import.WildcardSelector) =>
        case _ =>
          resolve(importer.name.attr.posOpt, name)
      }
    }
  }

  @pure def relQName(name: QName, ids: QName, shorten: B): QName = {
    val sz = name.size
    if (ids.size <= sz) {
      return ids
    }
    var i = z"0"
    while (i < name.size) {
      if (ids(i) != name(i)) {
        return if (shorten) "_" +: ISZOps(ids).drop(i) else ids
      }
      i = i + 1
    }
    return ISZOps(ids).drop(sz)
  }

  def typeString(name: QName, t: AST.Type, reporter: Reporter): ST = {
    t match {
      case t: AST.Type.Named => return typeNameString(name, relQName(name, AST.Util.ids2strings(t.name.ids), F))
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
    return Scope.Local.create(typeMap, scope)
  }

  @pure def typeNameString(name: QName, ids: QName): ST = {
    return st"${(relQName(name, ids, F), ".")}"
  }

  @pure def typeName(name: QName, ids: QName): ST = {
    return st"${relQName(name, ids, T)}"
  }

  @pure def isPosUriSuffixEq(posOpt1: Option[Position], posOpt2: Option[Position]): B = {
    (posOpt1, posOpt2) match {
      case (Some(pos1), Some(pos2)) =>
        if (pos1.uriOpt.nonEmpty && pos2.uriOpt.nonEmpty) {
          val uri1 = pos1.uriOpt.get
          val uri2 = pos2.uriOpt.get
          return ops.StringOps(uri2).endsWith(uri1)
        }
      case (_, _) =>
    }
    return F
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

}
