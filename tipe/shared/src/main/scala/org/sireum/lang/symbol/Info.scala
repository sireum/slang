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
import org.sireum.message._
import org.sireum.lang.{ast => AST}

@datatype trait Scope {

  @pure def packageName: ISZ[String]

  @pure def outerOpt: Option[Scope]

  @pure def resolveName(globalNameMap: Resolver.NameMap, name: ISZ[String]): Option[Info]

  @pure def resolveType(globalTypeMap: Resolver.TypeMap, name: ISZ[String]): Option[TypeInfo]

  @pure def resolveIndex(id: String): Option[AST.Typed]

  @pure def returnOpt: Option[AST.Typed]

  @pure def thisOpt: Option[AST.Typed]
}

object Scope {

  object Local {
    @pure def create(typeMap: HashMap[String, TypeInfo], outer: Scope): Local = {
      return Local(HashMap.empty, typeMap, None(), None(), HashMap.empty, Some(outer))
    }
  }

  @datatype class Local(val nameMap: HashMap[String, Info],
                        val typeMap: HashMap[String, TypeInfo],
                        val localThisOpt: Option[AST.Typed],
                        val methodReturnOpt: Option[AST.Typed],
                        val indexMap: HashMap[String, AST.Typed],
                        val outerOpt: Option[Scope]) extends Scope {

    @pure override def packageName: ISZ[String] = {
      outerOpt match {
        case Some(outer) => return outer.packageName
        case _ => return ISZ()
      }
    }

    @pure override def thisOpt: Option[AST.Typed] = {
      localThisOpt match {
        case r@Some(_) => return r
        case _ =>
          outerOpt match {
            case Some(outer) => return outer.thisOpt
            case _ => return None()
          }
      }
    }

    @pure override def returnOpt: Option[AST.Typed] = {
      methodReturnOpt match {
        case r@Some(_) => return r
        case _ =>
          outerOpt match {
            case Some(outer) => return outer.returnOpt
            case _ => return None()
          }
      }
    }

    @pure override def resolveName(globalNameMap: Resolver.NameMap, name: ISZ[String]): Option[Info] = {
      if (name.size == z"1") {
        val infoOpt = nameMap.get(name(0))
        if (infoOpt.nonEmpty) {
          return infoOpt
        }
      }
      outerOpt match {
        case Some(scope) => return scope.resolveName(globalNameMap, name)
        case _ => return None()
      }
    }

    @pure override def resolveType(globalTypeMap: Resolver.TypeMap,
                                   name: ISZ[String]): Option[TypeInfo] = {
      if (name.size == z"1") {
        val typeInfoOpt = typeMap.get(name(0))
        if (typeInfoOpt.nonEmpty) {
          return typeInfoOpt
        }
      }
      outerOpt match {
        case Some(scope) => return scope.resolveType(globalTypeMap, name)
        case _ => return None()
      }
    }

    @pure override def resolveIndex(id: String): Option[AST.Typed] = {
      val rOpt = indexMap.get(id)
      if (rOpt.nonEmpty) {
        return rOpt
      }
      outerOpt match {
        case Some(scope) => return scope.resolveIndex(id)
        case _ => return None()
      }
    }

    @pure def localIds: ISZ[String] = {
      outerOpt match {
        case Some(outer: Scope.Local) => return outer.localIds ++ nameMap.keys
        case _ => return nameMap.keys
      }
    }
  }

  @datatype class Global(val packageName: ISZ[String], val imports: ISZ[AST.Stmt.Import], val enclosingName: ISZ[String])
    extends Scope {

    @pure override def outerOpt: Option[Scope] = {
      return None()
    }

    @pure override def thisOpt: Option[AST.Typed] = {
      return None()
    }

    @pure override def returnOpt: Option[AST.Typed] = {
      return None()
    }

    @pure override def resolveName(globalNameMap: Resolver.NameMap, name: ISZ[String]): Option[Info] = {
      return resolveNameMemoized(globalNameMap, name)
    }

    @pure def resolveImported(globalNameMap: Resolver.NameMap, name: ISZ[String]): Option[Info] = {
      for (i <- imports.size - 1 to 0 by -1) {
        val impor = imports(i)
        val importers = impor.importers
        for (j <- importers.size - 1 to 0 by -1) {
          val importer = importers(j)
          val contextName = AST.Util.ids2strings(importer.name.ids)
          importer.selectorOpt match {
            case Some(selector) =>
              selector match {
                case selector: AST.Stmt.Import.MultiSelector =>
                  val nss = selector.selectors
                  val name0 = name(0)
                  for (k <- nss.size - 1 to 0 by -1) {
                    val ns = nss(k)
                    if (name0 == ns.to.value) {
                      val n = (contextName :+ ns.from.value) ++ ops.ISZOps(name).drop(1)
                      val rOpt = globalNameMap.get(packageName ++ n)
                      if (rOpt.nonEmpty) {
                        return rOpt
                      }
                      val rGlobalOpt = globalNameMap.get(n)
                      if (rGlobalOpt.nonEmpty) {
                        return rGlobalOpt
                      }
                    }
                  }
                case _: AST.Stmt.Import.WildcardSelector =>
                  val n = contextName ++ name
                  val rOpt = globalNameMap.get(n)
                  if (rOpt.nonEmpty) {
                    return rOpt
                  }
                  val rGlobalOpt = globalNameMap.get(packageName ++ n)
                  if (rGlobalOpt.nonEmpty) {
                    return rGlobalOpt
                  }
              }
            case _ =>
              val name0 = name(0)
              val contextLast = contextName(contextName.size - 1)
              if (contextLast == name0) {
                val n = contextName ++ ops.ISZOps(name).drop(1)
                val rOpt = globalNameMap.get(packageName ++ n)
                if (rOpt.nonEmpty) {
                  return rOpt
                }
                val rGlobalOpt = globalNameMap.get(n)
                if (rGlobalOpt.nonEmpty) {
                  return rGlobalOpt
                }
              }
          }
        }
      }
      return None()
    }

    @pure override def resolveType(globalTypeMap: Resolver.TypeMap,
                                   name: ISZ[String]): Option[TypeInfo] = {
      return resolveTypeMemoized(globalTypeMap, name)
    }

    @pure def resolveImportedType(globalTypeMap: Resolver.TypeMap,
                                  name: ISZ[String]): Option[TypeInfo] = {
      for (i <- imports.size - 1 to 0 by -1) {
        val impor = imports(i)
        val importers = impor.importers
        for (j <- importers.size - 1 to 0 by -1) {
          val importer = importers(j)
          val contextName = AST.Util.ids2strings(importer.name.ids)
          importer.selectorOpt match {
            case Some(selector) =>
              selector match {
                case selector: AST.Stmt.Import.MultiSelector =>
                  val nss = selector.selectors
                  val name0 = name(0)
                  for (k <- nss.size - 1 to 0 by -1) {
                    val ns = nss(k)
                    if (name0 == ns.to.value) {
                      val n = (contextName :+ ns.from.value) ++ ops.ISZOps(name).drop(1)
                      val rOpt = globalTypeMap.get(packageName ++ n)
                      if (rOpt.nonEmpty) {
                        return rOpt
                      }
                      val rGlobalOpt = globalTypeMap.get(n)
                      if (rGlobalOpt.nonEmpty) {
                        return rGlobalOpt
                      }
                    }
                  }
                case _: AST.Stmt.Import.WildcardSelector =>
                  val n = contextName ++ name
                  val rOpt = globalTypeMap.get(packageName ++ n)
                  if (rOpt.nonEmpty) {
                    return rOpt
                  }
                  val rGlobalOpt = globalTypeMap.get(n)
                  if (rGlobalOpt.nonEmpty) {
                    return rGlobalOpt
                  }
              }
            case _ =>
              val name0 = name(0)
              val contextLast = contextName(contextName.size - 1)
              if (contextLast == name0) {
                val n = contextName ++ ops.ISZOps(name).drop(1)
                val rOpt = globalTypeMap.get(packageName ++ n)
                if (rOpt.nonEmpty) {
                  return rOpt
                }
                val rGlobalOpt = globalTypeMap.get(n)
                if (rGlobalOpt.nonEmpty) {
                  return rGlobalOpt
                }
              }
          }
        }
      }
      return None()
    }

    @memoize def resolveNameMemoized(@hidden globalNameMap: Resolver.NameMap,
                                     name: ISZ[String]): Option[Info] = {
      val globalOpt = globalNameMap.get(name)
      if (globalOpt.nonEmpty) {
        return globalOpt
      }

      var en = enclosingName
      while (en.size >= packageName.size && en != packageName) {
        val enclosedOpt = globalNameMap.get(en ++ name)
        if (enclosedOpt.nonEmpty) {
          return enclosedOpt
        }
        en = ops.ISZOps(en).dropRight(1)
      }

      val importedOpt = resolveImported(globalNameMap, name)
      if (importedOpt.nonEmpty) {
        return importedOpt
      }

      return globalNameMap.get(packageName ++ name)
    }

    @memoize def resolveTypeMemoized(@hidden globalTypeMap: Resolver.TypeMap,
                                     name: ISZ[String]): Option[TypeInfo] = {
      val globalTypeOpt = globalTypeMap.get(name)
      if (globalTypeOpt.nonEmpty) {
        return globalTypeOpt
      }

      var en = enclosingName
      while (en.size >= packageName.size && en != packageName) {
        val enclosedTypeOpt = globalTypeMap.get(en ++ name)
        if (enclosedTypeOpt.nonEmpty) {
          return enclosedTypeOpt
        }
        en = ops.ISZOps(en).dropRight(1)
      }

      val importedTypeOpt = resolveImportedType(globalTypeMap, name)
      if (importedTypeOpt.nonEmpty) {
        return importedTypeOpt
      }

      return globalTypeMap.get(packageName ++ name)
    }

    @pure override def resolveIndex(id: String): Option[AST.Typed] = {
      return None()
    }

  }

}

@datatype trait Info {
  @pure def name: ISZ[String]

  @pure def posOpt: Option[Position]

  def mtransform(t: AST.MTransformer): Unit
}

object Info {

  @datatype class Package(val name: ISZ[String], val typedOpt: Option[AST.Typed], val resOpt: Option[AST.ResolvedInfo])
    extends Info {

    @pure override def posOpt: Option[Position] = {
      return None[Position]()
    }

    def mtransform(t: AST.MTransformer): Unit = {
    }
  }

  @datatype class Var(val owner: ISZ[String],
                      val isInObject: B,
                      val scope: Scope,
                      val ast: AST.Stmt.Var) extends Info {

    @pure override def posOpt: Option[Position] = {
      return ast.attr.posOpt
    }

    @pure def outlined: B = {
      ast.tipeOpt match {
        case Some(t) => return t.typedOpt.nonEmpty
        case _ => return T
      }
    }

    @pure override def name: ISZ[String] = {
      return owner :+ ast.id.value
    }

    @pure def typedOpt: Option[AST.Typed] = {
      return ast.attr.typedOpt
    }

    @pure def resOpt: Option[AST.ResolvedInfo] = {
      return ast.attr.resOpt
    }

    def mtransform(t: AST.MTransformer): Unit = {
      t.transformStmt(ast)
    }
  }

  @datatype class SpecVar(val owner: ISZ[String],
                          val isInObject: B,
                          val scope: Scope,
                          val ast: AST.Stmt.SpecVar) extends Info {

    @pure override def posOpt: Option[Position] = {
      return ast.attr.posOpt
    }

    @pure def outlined: B = {
      return ast.tipe.typedOpt.nonEmpty
    }

    @pure override def name: ISZ[String] = {
      return owner :+ ast.id.value
    }

    @pure def typedOpt: Option[AST.Typed] = {
      return ast.attr.typedOpt
    }

    @pure def resOpt: Option[AST.ResolvedInfo] = {
      return ast.attr.resOpt
    }

    def mtransform(t: AST.MTransformer): Unit = {
      t.transformStmt(ast)
    }
  }

  @datatype class RsVal(val owner: ISZ[String],
                        val scope: Scope,
                        val ast: AST.Stmt.RsVal) extends Info {

    @pure override def posOpt: Option[Position] = {
      return ast.attr.posOpt
    }

    @pure def outlined: B = {
      return ast.init.typedOpt.nonEmpty
    }

    @pure override def name: ISZ[String] = {
      return owner :+ ast.id.value
    }

    @pure def typedOpt: Option[AST.Typed] = {
      return ast.attr.typedOpt
    }

    @pure def resOpt: Option[AST.ResolvedInfo] = {
      return ast.attr.resOpt
    }

    def mtransform(t: AST.MTransformer): Unit = {
      t.transformStmt(ast)
    }
  }

  @datatype class Method(val owner: ISZ[String], val isInObject: B, val scope: Scope, val hasBody: B, val ast: AST.Stmt.Method)
    extends Info {

    @pure override def posOpt: Option[Position] = {
      return ast.attr.posOpt
    }

    @pure def outlined: B = {
      return ast.sig.returnType.typedOpt.nonEmpty
    }

    @pure override def name: ISZ[String] = {
      return owner :+ ast.sig.id.value
    }

    @pure def typedOpt: Option[AST.Typed] = {
      return ast.attr.typedOpt
    }

    @pure def methodType: AST.Typed.Method = {
      return typedOpt.get.asInstanceOf[AST.Typed.Method]
    }

    @pure def resOpt: Option[AST.ResolvedInfo] = {
      return ast.attr.resOpt
    }

    @pure def methodRes: AST.ResolvedInfo.Method = {
      return resOpt.get.asInstanceOf[AST.ResolvedInfo.Method]
    }

    @pure def builtInRes: AST.ResolvedInfo.BuiltIn = {
      return resOpt.get.asInstanceOf[AST.ResolvedInfo.BuiltIn]
    }

    def mtransform(t: AST.MTransformer): Unit = {
      t.transformStmt(ast)
    }
  }

  @datatype class SpecMethod(val owner: ISZ[String], val isInObject: B, val scope: Scope, val ast: AST.Stmt.SpecMethod)
    extends Info {

    @pure override def posOpt: Option[Position] = {
      return ast.attr.posOpt
    }

    @pure def outlined: B = {
      return ast.sig.returnType.typedOpt.nonEmpty
    }

    @pure override def name: ISZ[String] = {
      return owner :+ ast.sig.id.value
    }

    @pure def typedOpt: Option[AST.Typed] = {
      return ast.attr.typedOpt
    }

    @pure def resOpt: Option[AST.ResolvedInfo] = {
      return ast.attr.resOpt
    }

    @pure def methodRes: AST.ResolvedInfo.Method = {
      return resOpt.get.asInstanceOf[AST.ResolvedInfo.Method]
    }

    def mtransform(t: AST.MTransformer): Unit = {
      t.transformStmt(ast)
    }
  }

  @datatype class Object(val owner: ISZ[String],
                         val isSynthetic: B,
                         val scope: Scope.Global,
                         val outlined: B,
                         val contractOutlined: B,
                         val typeChecked: B,
                         val ast: AST.Stmt.Object,
                         val typedOpt: Option[AST.Typed],
                         val resOpt: Option[AST.ResolvedInfo],
                         val constructorRes: AST.ResolvedInfo.Method) extends Info {

    @pure override def name: ISZ[String] = {
      return owner :+ ast.id.value
    }

    @pure override def posOpt: Option[Position] = {
      return ast.attr.posOpt
    }

    def mtransform(t: AST.MTransformer): Unit = {
      t.transformStmt(ast)
    }
  }

  @datatype class ExtMethod(val owner: ISZ[String], val scope: Scope.Global, val ast: AST.Stmt.ExtMethod) extends Info {

    @pure override def posOpt: Option[Position] = {
      return ast.attr.posOpt
    }

    @pure override def name: ISZ[String] = {
      return owner :+ ast.sig.id.value
    }

    @pure def outlined: B = {
      return ast.sig.returnType.typedOpt.nonEmpty
    }

    @pure def typedOpt: Option[AST.Typed] = {
      return ast.attr.typedOpt
    }

    @pure def resOpt: Option[AST.ResolvedInfo] = {
      return ast.attr.resOpt
    }

    @pure def methodRes: AST.ResolvedInfo.Method = {
      return resOpt.get.asInstanceOf[AST.ResolvedInfo.Method]
    }

    def mtransform(t: AST.MTransformer): Unit = {
      t.transformStmt(ast)
    }
  }

  @datatype class JustMethod(val owner: ISZ[String], val scope: Scope.Global, val ast: AST.Stmt.JustMethod) extends Info {

    @pure override def posOpt: Option[Position] = {
      return ast.attr.posOpt
    }

    @pure override def name: ISZ[String] = {
      return owner :+ ast.sig.id.value
    }

    @pure def outlined: B = {
      return ast.sig.returnType.typedOpt.nonEmpty
    }

    @pure def typedOpt: Option[AST.Typed] = {
      return ast.attr.typedOpt
    }

    @pure def resOpt: Option[AST.ResolvedInfo] = {
      return ast.attr.resOpt
    }

    @pure def methodRes: AST.ResolvedInfo.Method = {
      return resOpt.get.asInstanceOf[AST.ResolvedInfo.Method]
    }

    def mtransform(t: AST.MTransformer): Unit = {
      t.transformStmt(ast)
    }
  }

  object Enum {

    val elementTypeSuffix: String = "Type"

  }

  @datatype class Enum(val name: ISZ[String],
                       val elements: Map[String, AST.ResolvedInfo],
                       val typedOpt: Option[AST.Typed],
                       val resOpt: Option[AST.ResolvedInfo],
                       val elementTypedOpt: Option[AST.Typed],
                       val posOpt: Option[Position]) extends Info {

    val byNameTypedOpt: Option[AST.Typed] = Some(
      AST.Typed.Method(
        T,
        AST.MethodMode.Ext,
        ISZ(),
        name,
        "byName",
        ISZ(),
        AST.Typed.Fun(AST.Purity.Pure, F, ISZ(AST.Typed.string), AST.Typed.Name(AST.Typed.optionName, ISZ(elementTypedOpt.get)))
      )
    )

    val byOrdinalTypedOpt: Option[AST.Typed] = Some(
      AST.Typed.Method(
        T,
        AST.MethodMode.Ext,
        ISZ(),
        name,
        "byOrdinal",
        ISZ(),
        AST.Typed.Fun(AST.Purity.Pure, F, ISZ(AST.Typed.z), AST.Typed.Name(AST.Typed.optionName, ISZ(elementTypedOpt.get)))
      )
    )

    val elementsTypedOpt: Option[AST.Typed] = Some(
      AST.Typed.Method(
        T,
        AST.MethodMode.Ext,
        ISZ(),
        name,
        "elements",
        ISZ(),
        AST.Typed.Fun(AST.Purity.Pure, T, ISZ(), AST.Typed.Name(AST.Typed.isName, ISZ(AST.Typed.z, elementTypedOpt.get)))
      )
    )

    val numOfElementsTypedOpt: Option[AST.Typed] = Some(
      AST.Typed.Method(
        T,
        AST.MethodMode.Ext,
        ISZ(),
        name,
        "numOfElements",
        ISZ(),
        AST.Typed.Fun(AST.Purity.Pure, T, ISZ(), AST.Typed.z)
      )
    )

    val byNameResOpt: Option[AST.ResolvedInfo] = Some(
      AST.ResolvedInfo.Method(
        T,
        AST.MethodMode.Ext,
        ISZ(),
        name,
        "byName",
        ISZ(),
        Some(AST.Typed.Fun(AST.Purity.Pure, F, ISZ(AST.Typed.string), AST.Typed.Name(AST.Typed.optionName, ISZ(elementTypedOpt.get)))),
        ISZ(),
        ISZ()
      )
    )

    val byOrdinalResOpt: Option[AST.ResolvedInfo] = Some(
      AST.ResolvedInfo.Method(
        T,
        AST.MethodMode.Ext,
        ISZ(),
        name,
        "byOrdinal",
        ISZ(),
        Some(AST.Typed.Fun(AST.Purity.Pure, F, ISZ(AST.Typed.z), AST.Typed.Name(AST.Typed.optionName, ISZ(elementTypedOpt.get)))),
        ISZ(),
        ISZ()
      )
    )

    val elementsResOpt: Option[AST.ResolvedInfo] = Some(
      AST.ResolvedInfo.Method(
        T,
        AST.MethodMode.Ext,
        ISZ(),
        name,
        "elements",
        ISZ(),
        Some(AST.Typed.Fun(AST.Purity.Pure, T, ISZ(), AST.Typed.Name(AST.Typed.isName, ISZ(AST.Typed.z, elementTypedOpt.get)))),
        ISZ(),
        ISZ()
      )
    )

    val numOfElementsResOpt: Option[AST.ResolvedInfo] = Some(
      AST.ResolvedInfo.Method(
        T,
        AST.MethodMode.Ext,
        ISZ(),
        name,
        "numOfElements",
        ISZ(),
        Some(AST.Typed.Fun(AST.Purity.Pure, T, ISZ(), AST.Typed.z)),
        ISZ(),
        ISZ()
      )
    )

    def mtransform(t: AST.MTransformer): Unit = {
    }
  }

  @datatype class EnumElement(val owner: ISZ[String],
                              val id: String,
                              val typedOpt: Option[AST.Typed],
                              val resOpt: Option[AST.ResolvedInfo],
                              val posOpt: Option[Position]) extends Info {

    @pure override def name: ISZ[String] = {
      return owner :+ id
    }

    def mtransform(t: AST.MTransformer): Unit = {
    }
  }

  @datatype class LocalVar(val name: ISZ[String],
                           val isVal: B,
                           val ast: AST.Id,
                           val typedOpt: Option[AST.Typed],
                           val initOpt: Option[AST.AssignExp],
                           val resOpt: Option[AST.ResolvedInfo]) extends Info {

    @pure override def posOpt: Option[Position] = {
      return ast.attr.posOpt
    }

    def mtransform(t: AST.MTransformer): Unit = {
      t.transformId(ast)
      initOpt match {
        case Some(init) => t.transformAssignExp(init)
        case _ =>
      }
    }
  }

  @datatype class Fact(val owner: ISZ[String],
                       val id: String,
                       val scope: Scope.Global,
                       val ast: AST.Stmt.Fact) extends Info {

    val typedOpt: Option[AST.Typed] = Some(AST.Typed.Fact(owner, id))

    @pure def resOpt: Option[AST.ResolvedInfo] = {
      return ast.attr.resOpt
    }

    @pure override def name: ISZ[String] = {
      return owner :+ id
    }

    @pure override def posOpt: Option[Position] = {
      return ast.attr.posOpt
    }

    def mtransform(t: AST.MTransformer): Unit = {
      t.transformStmt(ast)
    }
  }

  @datatype class Theorem(val owner: ISZ[String],
                          val id: String,
                          val scope: Scope.Global,
                          val ast: AST.Stmt.Theorem) extends Info {

    val typedOpt: Option[AST.Typed] = Some(AST.Typed.Theorem(owner, id))

    @pure def resOpt: Option[AST.ResolvedInfo] = {
      return ast.attr.resOpt
    }

    @pure override def name: ISZ[String] = {
      return owner :+ id
    }

    @pure override def posOpt: Option[Position] = {
      return ast.attr.posOpt
    }

    def mtransform(t: AST.MTransformer): Unit = {
      t.transformStmt(ast)
    }
  }

  @datatype class Inv(val owner: ISZ[String],
                      val id: String,
                      val scope: Scope,
                      val ast: AST.Stmt.Inv) extends Info {

    @memoize def typedOpt: Option[AST.Typed] = {
      return Some(AST.Typed.Inv(ast.attr.resOpt.get.asInstanceOf[AST.ResolvedInfo.Inv].isInObject, owner, id))
    }

    @pure def resOpt: Option[AST.ResolvedInfo] = {
      return ast.attr.resOpt
    }

    @pure override def name: ISZ[String] = {
      return owner :+ id
    }

    @pure override def posOpt: Option[Position] = {
      return ast.attr.posOpt
    }

    def mtransform(t: AST.MTransformer): Unit = {
      t.transformStmt(ast)
    }
  }

  @pure def substMethod(m: Method, substMap: HashMap[String, AST.Typed]): Method = {
    if (substMap.nonEmpty) {
      val astOpt = AST.Transformer(AST.Util.TypePrePostSubstitutor(substMap)).transformStmt(F, m.ast).resultOpt
      astOpt match {
        case Some(newAst: AST.Stmt.Method) => return m(ast = newAst)
        case _ => return m
      }
    } else {
      return m
    }
  }

}

@datatype trait TypeInfo {

  @pure def name: ISZ[String]

  @pure def canHaveCompanion: B

  @pure def posOpt: Option[Position]

  @pure def tpe: AST.Typed
}

object TypeInfo {

  @datatype class SubZ(val owner: ISZ[String], val ast: AST.Stmt.SubZ) extends TypeInfo {

    val typedOpt: Option[AST.Typed] = Some(AST.Typed.Name(name, ISZ()))

    @pure override def name: ISZ[String] = {
      return owner :+ ast.id.value
    }

    @pure override def canHaveCompanion: B = {
      return F
    }

    @pure override def posOpt: Option[Position] = {
      return ast.attr.posOpt
    }

    @strictpure override def tpe: AST.Typed = typedOpt.get

  }

  @datatype class Enum(val owner: ISZ[String], val elements: Map[String, AST.ResolvedInfo], val posOpt: Option[Position])
    extends TypeInfo {

    val typedOpt: Option[AST.Typed] = Some(AST.Typed.Name(name, ISZ()))

    val nameTypedOpt: Option[AST.Typed] = Some(
      AST.Typed
        .Method(F, AST.MethodMode.Ext, ISZ(), name, "name", ISZ(), AST.Typed.Fun(AST.Purity.Pure, T, ISZ(), AST.Typed.string))
    )

    val ordinalTypedOpt: Option[AST.Typed] = Some(
      AST.Typed.Method(F, AST.MethodMode.Ext, ISZ(), name, "ordinal", ISZ(), AST.Typed.Fun(AST.Purity.Pure, T, ISZ(), AST.Typed.z))
    )

    val nameResOpt: Option[AST.ResolvedInfo] = Some(
      AST.ResolvedInfo.Method(F, AST.MethodMode.Ext, ISZ(), name, "name", ISZ(), Some(AST.Typed.Fun(AST.Purity.Pure, T, ISZ(),
        AST.Typed.string)), ISZ(), ISZ())
    )

    val ordinalResOpt: Option[AST.ResolvedInfo] = Some(
      AST.ResolvedInfo.Method(F, AST.MethodMode.Ext, ISZ(), name, "ordinal", ISZ(), Some(AST.Typed.Fun(AST.Purity.Pure, T, ISZ(),
        AST.Typed.z)), ISZ(), ISZ())
    )

    @pure override def name: ISZ[String] = {
      return owner :+ Info.Enum.elementTypeSuffix
    }

    @pure override def canHaveCompanion: B = {
      return F
    }
    @strictpure override def tpe: AST.Typed = typedOpt.get
  }

  @datatype class Sig(val owner: ISZ[String],
                      val outlined: B,
                      val contractOutlined: B,
                      val typeChecked: B,
                      val tpe: AST.Typed.Name,
                      val ancestors: ISZ[AST.Typed.Name],
                      val specVars: HashSMap[String, Info.SpecVar],
                      val specMethods: HashSMap[String, Info.SpecMethod],
                      val methods: HashSMap[String, Info.Method],
                      val refinements: HashSMap[String, Name],
                      val invariants: HashSMap[String, Info.Inv],
                      val dataRefinements: ISZ[AST.Stmt.DataRefinement],
                      val scope: Scope.Global,
                      val ast: AST.Stmt.Sig) extends TypeInfo {

    @pure override def name: ISZ[String] = {
      return owner :+ ast.id.value
    }

    @pure override def canHaveCompanion: B = {
      return T
    }

    @pure override def posOpt: Option[Position] = {
      return ast.attr.posOpt
    }

    @pure def hasId(id: String): B = {
      return specVars.contains(id) || specMethods.contains(id) || methods.contains(id) || invariants.contains(id)
    }

    @pure def typeRes(id: String, inSpec: B): (B, Option[AST.Typed], Option[AST.ResolvedInfo]) = {

      @pure def noResult: (B, Option[AST.Typed], Option[AST.ResolvedInfo]) = {
        return (T, None(), None())
      }

      @pure def errResult: (B, Option[AST.Typed], Option[AST.ResolvedInfo]) = {
        return (F, None(), None())
      }

      methods.get(id) match {
        case Some(m) =>
          return if (inSpec && m.ast.purity == AST.Purity.Impure) errResult
          else (T, m.typedOpt, m.resOpt)
        case _ =>
      }

      if (!inSpec) {
        return noResult
      }

      specMethods.get(id) match {
        case Some(sm) => return (T, sm.typedOpt, sm.resOpt)
        case _ =>
      }

      specVars.get(id) match {
        case Some(sv) => return (T, sv.typedOpt, sv.resOpt)
        case _ =>
      }

      return noResult
    }

    @memoize def parents: ISZ[AST.Typed.Name] = {
      var r = ISZ[AST.Typed.Name]()
      for (p <- ast.parents) {
        p.typedOpt match {
          case Some(t: AST.Typed.Name) => r = r :+ t
          case _ =>
        }
      }
      return r
    }
  }

  @datatype class Name(val ids: ISZ[String])

  @datatype class Adt(val owner: ISZ[String],
                      val outlined: B,
                      val contractOutlined: B,
                      val typeChecked: B,
                      val tpe: AST.Typed.Name,
                      val constructorTypeOpt: Option[AST.Typed],
                      val constructorResOpt: Option[AST.ResolvedInfo],
                      val extractorTypeMap: Map[String, AST.Typed],
                      val extractorResOpt: Option[AST.ResolvedInfo],
                      val ancestors: ISZ[AST.Typed.Name],
                      val specVars: HashSMap[String, Info.SpecVar],
                      val vars: HashSMap[String, Info.Var],
                      val specMethods: HashSMap[String, Info.SpecMethod],
                      val methods: HashSMap[String, Info.Method],
                      val refinements: HashSMap[String, Name],
                      val invariants: HashSMap[String, Info.Inv],
                      val dataRefinements: ISZ[AST.Stmt.DataRefinement],
                      val scope: Scope.Global,
                      val ast: AST.Stmt.Adt) extends TypeInfo {

    @pure override def canHaveCompanion: B = {
      return T
    }

    @pure override def name: ISZ[String] = {
      return owner :+ ast.id.value
    }

    @pure override def posOpt: Option[Position] = {
      return ast.attr.posOpt
    }

    @pure def hasId(id: String): B = {
      return specVars.contains(id) || specMethods.contains(id) || methods.contains(id) || invariants.contains(id)
    }

    @pure def typeRes(id: String, inSpec: B): (B, Option[AST.Typed], Option[AST.ResolvedInfo]) = {

      @pure def noResult: (B, Option[AST.Typed], Option[AST.ResolvedInfo]) = {
        return (T, None(), None())
      }

      @pure def errResult: (B, Option[AST.Typed], Option[AST.ResolvedInfo]) = {
        return (F, None(), None())
      }

      vars.get(id) match {
        case Some(v) => return (T, v.typedOpt, v.resOpt)
        case _ =>
      }

      methods.get(id) match {
        case Some(m) =>
          return if (inSpec && m.ast.purity == AST.Purity.Impure) errResult
          else (T, m.typedOpt, m.resOpt)
        case _ =>
      }

      if (!inSpec) {
        return noResult
      }

      specMethods.get(id) match {
        case Some(sm) => return (T, sm.typedOpt, sm.resOpt)
        case _ =>
      }

      specVars.get(id) match {
        case Some(sv) => return (T, sv.typedOpt, sv.resOpt)
        case _ =>
      }

      invariants.get(id) match {
        case Some(inv) => return (T, inv.typedOpt, inv.resOpt)
        case _ =>
      }

      return noResult
    }

    @memoize def parents: ISZ[AST.Typed.Name] = {
      var r = ISZ[AST.Typed.Name]()
      for (p <- ast.parents) {
        p.typedOpt match {
          case Some(t: AST.Typed.Name) => r = r :+ t
          case _ =>
        }
      }
      return r
    }
  }

  @datatype class TypeAlias(val name: ISZ[String], val scope: Scope.Global, val ast: AST.Stmt.TypeAlias) extends TypeInfo {

    @pure def outlined: B = {
      return ast.tipe.typedOpt.nonEmpty
    }

    @pure override def canHaveCompanion: B = {
      return T
    }

    @pure override def posOpt: Option[Position] = {
      return ast.attr.posOpt
    }
    @strictpure override def tpe: AST.Typed = ast.tipe.typedOpt.get
  }

  @datatype class TypeVar(val id: String, val ast: AST.TypeParam) extends TypeInfo {

    @pure override def name: ISZ[String] = {
      return ISZ(id)
    }

    @pure override def canHaveCompanion: B = {
      return F
    }

    @pure override def posOpt: Option[Position] = {
      return ast.id.attr.posOpt
    }
    @strictpure override def tpe: AST.Typed = AST.Typed.TypeVar(id, ast.kind)
  }

  @datatype class Members(val specVars: HashSMap[String, Info.SpecVar],
                          val vars: HashSMap[String, Info.Var],
                          val specMethods: HashSMap[String, Info.SpecMethod],
                          val methods: HashSMap[String, Info.Method],
                          val refinements: HashSMap[String, Name],
                          val invariants: HashSMap[String, Info.Inv])

}
