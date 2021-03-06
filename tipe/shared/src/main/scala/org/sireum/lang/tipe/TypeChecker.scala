// #Sireum
/*
 Copyright (c) 2017-2021, Robby, Kansas State University
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

object TypeChecker {

  @enum object BasicKind {
    "B"
    "C"
    "Z"
    "Range"
    "Bits"
    "F32"
    "F64"
    "R"
    "String"
  }

  @enum object BuiltInKind {
    "Print"
    "Assertume"
    "Halt"
  }

  @enum object TypeRelation {
    "Subtype"
    "Equal"
    "Supertype"
  }

  @enum object ModeContext {
    "Code"
    "Spec"
    "SpecPost"
  }

  @datatype class TypeFinder(val th: TypeHierarchy, val tname: QName) extends AST.Transformer.PrePost[B] {
    override def preTypedName(ctx: B, o: AST.Typed.Name): AST.Transformer.PreResult[B, AST.Typed] = {
      return if (tname == o.ids || th.poset.isChildOf(tname, o.ids)) AST.Transformer.PreResult(T, T, None())
      else AST.Transformer.PreResult(ctx, T, None())
    }
  }
  @record class StrictPureChecker(val typeVarMutable: B, val th: TypeHierarchy, val reporter: Reporter) extends AST.MTransformer {
    def errVars(posOpt: Option[Position]): Unit = {
      reporter.error(posOpt, TypeChecker.typeCheckerKind, "@strictpure methods cannot refer to vars")
    }
    def checkType(t: AST.Typed, posOpt: Option[Position]): Unit = {
      if (th.isMutable(t, typeVarMutable)) {
        reporter.error(posOpt, TypeChecker.typeCheckerKind, "@strictpure methods cannot refer to outer vals of mutable type")
      }
    }
    override def postResolvedAttr(o: AST.ResolvedAttr): MOption[AST.ResolvedAttr] = {
      o.resOpt match {
        case Some(res) =>
          res match {
            case res: AST.ResolvedInfo.Var if res.isInObject =>
              if (!res.isVal) {
                errVars(o.posOpt)
              } else {
                checkType(o.typedOpt.get, o.posOpt)
              }
            case res: AST.ResolvedInfo.LocalVar if res.scope == AST.ResolvedInfo.LocalVar.Scope.Closure && res.context.isEmpty =>
              reporter.error(o.posOpt, TypeChecker.typeCheckerKind, "Worksheet @strictpure methods cannot refer to top-level vars/vals")
            case _ =>
          }
        case _ =>
      }
      return AST.MTransformer.PostResultResolvedAttr
    }

    def checkResOpt(attr: AST.ResolvedAttr): Unit = {
      attr.resOpt.get match {
        case res: AST.ResolvedInfo.Var if !res.isInObject =>
          if (!res.isVal) {
            errVars(attr.posOpt)
          } else {
            checkType(attr.typedOpt.get, attr.posOpt)
          }
        case _ =>
      }
    }

    override def postExpSelect(o: AST.Exp.Select): MOption[AST.Exp] = {
      o.receiverOpt match {
        case Some(_: AST.Exp.This) => checkResOpt(o.attr)
        case _ =>
      }
      return AST.MTransformer.PostResultExpSelect
    }

    override def postExpIdent(o: AST.Exp.Ident): MOption[AST.Exp] = {
      checkResOpt(o.attr)
      return AST.MTransformer.PostResultExpIdent
    }
  }

  val typeCheckerKind: String = "Type Checker"
  val errType: AST.Typed = AST.Typed.Name(ISZ(), ISZ())

  val sTypeParams: ISZ[String] = ISZ("I", "V")

  val builtInMethods: HashSet[String] =
    HashSet ++ ISZ("assert", "assume", "println", "print", "cprintln", "cprint", "eprintln", "eprint", "halt")
  val assertResOpt: Option[AST.ResolvedInfo] = Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Assert))

  val assertMsgResOpt: Option[AST.ResolvedInfo] = Some(
    AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.AssertMsg)
  )
  val assumeResOpt: Option[AST.ResolvedInfo] = Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Assume))

  val assumeMsgResOpt: Option[AST.ResolvedInfo] = Some(
    AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.AssumeMsg)
  )
  val assertumeTypedOpt: Option[AST.Typed] = Some(AST.Typed.Fun(F, F, ISZ(AST.Typed.b), AST.Typed.unit))

  val assertumeMsgTypedOpt: Option[AST.Typed] = Some(
    AST.Typed.Fun(F, F, ISZ(AST.Typed.b, AST.Typed.string), AST.Typed.unit)
  )
  val printlnResOpt: Option[AST.ResolvedInfo] = Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Println))
  val printResOpt: Option[AST.ResolvedInfo] = Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Print))
  val cprintlnResOpt: Option[AST.ResolvedInfo] = Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Cprintln))
  val cprintResOpt: Option[AST.ResolvedInfo] = Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Cprint))
  val eprintlnResOpt: Option[AST.ResolvedInfo] = Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Eprintln))
  val eprintResOpt: Option[AST.ResolvedInfo] = Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Eprint))
  val haltResOpt: Option[AST.ResolvedInfo] = Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Halt))

  val haltTypedOpt: Option[AST.Typed] = Some(AST.Typed.Fun(F, F, ISZ(AST.Typed.string), AST.Typed.nothing))
  val nativeResOpt: Option[AST.ResolvedInfo] = Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Native))
  val nativeCTypedOpt: Option[AST.Typed] = Some(AST.Typed.Fun(F, T, ISZ(), AST.Typed.c))
  val nativeStringTypedOpt: Option[AST.Typed] = Some(AST.Typed.Fun(F, T, ISZ(), AST.Typed.string))
  val nativeF32TypedOpt: Option[AST.Typed] = Some(AST.Typed.Fun(F, T, ISZ(), AST.Typed.f32))
  val nativeF64TypedOpt: Option[AST.Typed] = Some(AST.Typed.Fun(F, T, ISZ(), AST.Typed.f64))
  val applyResOpt: Option[AST.ResolvedInfo] = Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Apply))
  val stringResOpt: Option[AST.ResolvedInfo] = Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.String))
  val hashResOpt: Option[AST.ResolvedInfo] = Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Hash))

  val isInstanceOfResOpt: Option[AST.ResolvedInfo] = Some(
    AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.IsInstanceOf)
  )

  val asInstanceOfResOpt: Option[AST.ResolvedInfo] = Some(
    AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.AsInstanceOf)
  )

  val minResOpt: Option[AST.ResolvedInfo] = Some(
    AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Min)
  )

  val maxResOpt: Option[AST.ResolvedInfo] = Some(
    AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Max)
  )

  val randomResOpt: Option[AST.ResolvedInfo] = Some(
    AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Random)
  )

  val indicesResOpt: Option[AST.ResolvedInfo] = Some(
    AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Indices)
  )

  val unopResOpt: HashMap[AST.Exp.UnaryOp.Type, Option[AST.ResolvedInfo]] = HashMap ++ ISZ[
    (AST.Exp.UnaryOp.Type, Option[AST.ResolvedInfo])
  ](
    AST.Exp.UnaryOp.Plus ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.UnaryPlus)),
    AST.Exp.UnaryOp.Minus ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.UnaryMinus)),
    AST.Exp.UnaryOp.Not ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.UnaryNot)),
    AST.Exp.UnaryOp.Complement ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.UnaryComplement))
  )

  val binopResOpt: HashMap[String, Option[AST.ResolvedInfo]] = HashMap ++ ISZ[(String, Option[AST.ResolvedInfo])](
    string"+" ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryAdd)),
    string"-" ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinarySub)),
    string"*" ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryMul)),
    string"/" ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryDiv)),
    string"%" ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryRem)),
    string"==" ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryEq)),
    string"===" ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryEq)),
    string"!=" ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryNe)),
    string"=!=" ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryNe)),
    string"<<" ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryShl)),
    string">>" ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryShr)),
    string">>>" ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryUshr)),
    string"<" ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryLt)),
    string"<=" ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryLe)),
    string">" ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryGt)),
    string">=" ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryGe)),
    string"&" ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryAnd)),
    string"|" ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryOr)),
    string"|^" ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryXor)),
    string"->:" ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryImply)),
    string"imply_:" ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryImply)),
    string"&&" ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryCondAnd)),
    string"||" ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryCondOr)),
    string"-->:" ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryCondImply)),
    string"simply_:" ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryCondImply)),
    string"~>" ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryMapsTo))
  )

  val emptySubstMap: HashMap[String, AST.Typed] = HashMap.empty

  def buildTypeSubstMap(
    name: QName,
    posOpt: Option[Position],
    typeParams: ISZ[AST.TypeParam],
    args: ISZ[AST.Typed],
    reporter: Reporter
  ): Option[HashMap[String, AST.Typed]] = {
    if (typeParams.size != args.size) {
      reporter.error(
        posOpt,
        typeCheckerKind,
        st"Type ${(name, ".")} requires ${typeParams.size} type arguments, but ${args.size} is supplied.".render
      )
      return None()
    }
    var substMap = emptySubstMap
    for (i <- z"0" until args.size) {
      substMap = substMap + typeParams(i).id.value ~> args(i)
    }
    return Some(substMap)
  }

  def buildMethodSubstMap(
    m: AST.Typed.Method,
    posOpt: Option[Position],
    args: ISZ[AST.Typed],
    reporter: Reporter
  ): Option[HashMap[String, AST.Typed]] = {
    if (m.typeParams.size != args.size) {
      reporter.error(
        posOpt,
        typeCheckerKind,
        st"$m requires ${m.typeParams.size} type arguments, but only ${args.size} is supplied.".render
      )
      return None()
    }
    var substMap = HashMap.empty[String, AST.Typed]
    for (i <- z"0" until args.size) {
      substMap = substMap + m.typeParams(i) ~> args(i)
    }
    return Some(substMap)
  }

  def checkComponents(par: B, strictAliasing: B, th: TypeHierarchy, nameMap: NameMap, typeMap: TypeMap, reporter: Reporter): TypeHierarchy = {
    var jobs = ISZ[() => (TypeHierarchy => (TypeHierarchy, ISZ[Message]) @pure) @pure]()
    for (info <- typeMap.values) {
      info match {
        case info: TypeInfo.Sig if !info.typeChecked =>
          jobs = jobs :+ (() => TypeChecker(th, info.name, !info.ast.isImmutable, TypeChecker.ModeContext.Code, strictAliasing).checkSig(info))
        case info: TypeInfo.Adt if !info.typeChecked =>
          jobs = jobs :+ (() => TypeChecker(th, info.name, !info.ast.isDatatype, TypeChecker.ModeContext.Code, strictAliasing).checkAdt(info))
        case _ =>
      }
    }
    for (info <- nameMap.values) {
      info match {
        case info: Info.Object =>
          jobs = jobs :+ (() => TypeChecker(th, info.name, F, TypeChecker.ModeContext.Code, strictAliasing).checkObject(info))
        case _ =>
      }
    }
    val init = (th, ISZ[Message]())
    val p: (TypeHierarchy, ISZ[Message]) =
      if (par) ops.ISZOps(ops.ISZOps(jobs).parMap((f: () => (TypeHierarchy => (TypeHierarchy, ISZ[Message]) @pure)@pure) => f())).
        foldLeft(TypeHierarchy.combine _, init)
      else ops.ISZOps(ops.ISZOps(jobs).map((f: () => (TypeHierarchy => (TypeHierarchy, ISZ[Message])@pure)@pure) => f())).
        foldLeft(TypeHierarchy.combine _, init)
    var r = p._1
    def reconstructObject(info: Info): Unit = {
      info match {
        case info: Info.Object =>
          var newStmts = ISZ[AST.Stmt]()
          for (stmt <- info.ast.stmts) {
            stmt match {
              case stmt: AST.Stmt.Adt =>
                r.typeMap.get(info.name :+ stmt.id.value) match {
                  case Some(adtInfo: TypeInfo.Adt) => newStmts = newStmts :+ adtInfo.ast
                  case _ => halt(s"Unexpected situation when type checking object @datatype/@record members")
                }
              case stmt: AST.Stmt.Sig =>
                r.typeMap.get(info.name :+ stmt.id.value) match {
                  case Some(sigInfo: TypeInfo.Sig) => newStmts = newStmts :+ sigInfo.ast
                  case _ => halt(s"Unexpected situation when type checking object @datatype/@record members")
                }
              case stmt: AST.Stmt.TypeAlias =>
                r.typeMap.get(info.name :+ stmt.id.value) match {
                  case Some(taInfo: TypeInfo.TypeAlias) => newStmts = newStmts :+ taInfo.ast
                  case _ => halt(s"Unexpected situation when type checking object type alias members")
                }
              case stmt: AST.Stmt.Object =>
                reconstructObject(r.nameMap.get(info.name :+ stmt.id.value).get)
              case _ => newStmts = newStmts :+ stmt
            }
          }
          r = r(nameMap = r.nameMap + info.name ~> info(typeChecked = T, ast = info.ast(stmts = newStmts)))
        case _ =>
      }
    }
    for (name <- nameMap.keys) {
      r.nameMap.get(name) match {
        case Some(info) => reconstructObject(info)
        case _ =>
      }
    }
    reporter.reports(p._2)
    return r
  }

  @pure def unifyCombine(
    r: HashMap[String, AST.Typed],
    m: HashMap[String, AST.Typed]
  ): Option[HashMap[String, AST.Typed]] = {
    var res = r
    for (e <- m.entries) {
      val (key, value) = e
      res.get(key) match {
        case Some(v) =>
          if (value != v) {
            return None()
          }
        case _ => res = res + key ~> value
      }
    }
    return Some(res)
  }

  def unify(
    th: TypeHierarchy,
    posOpt: Option[Position],
    typeRel: TypeRelation.Type,
    expected: AST.Typed,
    tpe: AST.Typed,
    reporter: Reporter
  ): Option[HashMap[String, AST.Typed]] = {

    def findAncestor(ancestor: AST.Typed.Name, t: AST.Typed.Name): Option[AST.Typed.Name] = {
      val (ancestors, substMap): (ISZ[AST.Typed.Name], HashMap[String, AST.Typed]) = th.typeMap.get(t.ids) match {
        case Some(info: TypeInfo.Adt) =>
          val smOpt = buildTypeSubstMap(t.ids, posOpt, info.ast.typeParams, t.args, reporter)
          smOpt match {
            case Some(sm) => (info.ancestors, sm)
            case _ => return None()
          }
        case Some(info: TypeInfo.Sig) =>
          val smOpt = buildTypeSubstMap(t.ids, posOpt, info.ast.typeParams, t.args, reporter)
          smOpt match {
            case Some(sm) => (info.ancestors, sm)
            case _ => return None()
          }
        case _ => halt(s"Unexpected situation when trying to unify $ancestor and $t")
      }
      var r = t
      var found = F
      for (a <- ancestors if !found && a.ids == ancestor.ids) {
        r = a
        found = T
      }
      return if (found) Some(r.subst(substMap)) else None()
    }

    def err(): Unit = {
      reporter.error(posOpt, typeCheckerKind, s"Could not unify type '$expected' with '$tpe'.")
    }

    (expected, tpe) match {
      case (_, tpe: AST.Typed.TypeVar) =>
        return Some(HashMap.empty[String, AST.Typed] + tpe.id ~> expected)
      case (expected: AST.Typed.Name, tpe: AST.Typed.Name) =>
        val sameIds = tpe.ids == expected.ids
        val rt: AST.Typed.Name =
          typeRel match {
            case TypeRelation.Subtype if !sameIds =>
              val aOpt = findAncestor(expected, tpe)
              aOpt match {
                case Some(a) => a
                case _ => err(); return None()
              }
            case TypeRelation.Supertype if !sameIds =>
              val aOpt = findAncestor(tpe, expected)
              aOpt match {
                case Some(a) => a
                case _ => err(); return None()
              }
            case _ =>
              if (!sameIds) {
                err()
                return None()
              }
              if (typeRel == TypeRelation.Subtype) tpe else expected
          }
        val size = rt.args.size
        var i = 0
        var r = HashMap.empty[String, AST.Typed]
        while (i < size) {
          val mOpt: Option[HashMap[String, AST.Typed]] =
            if (typeRel == TypeRelation.Subtype)
              unify(th, posOpt, TypeRelation.Equal, expected.args(i), rt.args(i), reporter)
            else unify(th, posOpt, TypeRelation.Equal, rt.args(i), tpe.args(i), reporter)

          mOpt match {
            case Some(m) =>
              unifyCombine(r, m) match {
                case Some(c) => r = c
                case _ => err(); return None()
              }
            case _ => return None()
          }
          i = i + 1
        }
        return Some(r)
      case (expected: AST.Typed.Tuple, tpe: AST.Typed.Tuple) =>
        val size = expected.args.size
        if (size != tpe.args.size) {
          err()
          return None()
        }
        var i = 0
        var r = HashMap.empty[String, AST.Typed]
        while (i < size) {
          val mOpt = unify(th, posOpt, TypeRelation.Equal, expected.args(i), tpe.args(i), reporter)
          mOpt match {
            case Some(m) =>
              unifyCombine(r, m) match {
                case Some(c) => r = c
                case _ => err(); return None()
              }
            case _ => return None()
          }
          i = i + 1
        }
        return Some(r)
      case (expected: AST.Typed.Fun, _) if expected.isByName =>
        val r = unify(th, posOpt, typeRel, expected.ret, tpe, reporter)
        return r
      case (_, tpe: AST.Typed.Fun) if tpe.isByName =>
        val r = unify(th, posOpt, typeRel, expected, tpe.ret, reporter)
        return r
      case (expected: AST.Typed.Fun, tpe: AST.Typed.Fun) =>
        val r = unifyFun(typeCheckerKind, th, posOpt, TypeRelation.Equal, expected, tpe, reporter)
        return r
      case _ => return None()
    }
  }

  def unifyFun(
    kind: String,
    th: TypeHierarchy,
    posOpt: Option[Position],
    typeRel: TypeRelation.Type,
    expected: AST.Typed.Fun,
    tpe: AST.Typed.Fun,
    reporter: Reporter
  ): Option[HashMap[String, AST.Typed]] = {
    def err(): Unit = {
      reporter.error(posOpt, kind, s"Could not unify type '$expected' with '$tpe'.")
    }
    val size = expected.args.size
    if (size != tpe.args.size) {
      err()
      return None()
    }
    var i = 0
    var r = HashMap.empty[String, AST.Typed]
    val mode: TypeRelation.Type = typeRel match {
      case TypeRelation.Subtype => TypeRelation.Supertype
      case TypeRelation.Supertype => TypeRelation.Subtype
      case TypeRelation.Equal => typeRel
    }
    while (i < size) {
      val mOpt = unify(th, posOpt, mode, expected.args(i), tpe.args(i), reporter)
      mOpt match {
        case Some(m) =>
          unifyCombine(r, m) match {
            case Some(c) => r = c
            case _ => err(); return None()
          }
        case _ => return None()
      }
      i = i + 1
    }
    val mOpt = unify(th, posOpt, typeRel, expected.ret, tpe.ret, reporter)
    mOpt match {
      case Some(m) =>
        unifyCombine(r, m) match {
          case Some(c) => r = c
          case _ => err(); return None()
        }
      case _ => return None()
    }
    return Some(r)
  }

  def unifies(
    th: TypeHierarchy,
    posOpt: Option[Position],
    typeRel: TypeRelation.Type,
    expected: ISZ[AST.Typed],
    tpe: ISZ[AST.Typed],
    reporter: Reporter
  ): Option[HashMap[String, AST.Typed]] = {
    def err(): Unit = {
      reporter.error(
        posOpt,
        typeCheckerKind,
        s"Could not unify type '${AST.Typed.Tuple(expected)}' with '${AST.Typed.Tuple(tpe)}'."
      )
    }

    val size = expected.size
    if (size != tpe.size) {
      return None()
    }
    var r = HashMap.empty[String, AST.Typed]
    var i = 0
    while (i < size) {
      val mOpt = unify(th, posOpt, typeRel, expected(i), tpe(i), reporter)
      mOpt match {
        case Some(m) =>
          unifyCombine(r, m) match {
            case Some(c) => r = c
            case _ => err(); return None()
          }
        case _ => return None()
      }
      i = i + 1
    }
    return Some(r)
  }

  def checkTypeUsage(posOpt: Option[Position], t: AST.Typed, reporter: Reporter): Unit = {
    def err(): Unit = {
      reporter.error(posOpt, typeCheckerKind, s"Invalid usage of type '$t'.")
    }
    t match {
      case _: AST.Typed.Name => // skip
      case _: AST.Typed.Enum => err()
      case _: AST.Typed.Fun => // skip
      case _: AST.Typed.Method => err()
      case _: AST.Typed.Methods => err()
      case _: AST.Typed.Object => err()
      case _: AST.Typed.Package => err()
      case _: AST.Typed.Tuple => // skip
      case _: AST.Typed.TypeVar => // skip
      case _: AST.Typed.Fact => err()
      case _: AST.Typed.Theorem => err()
      case _: AST.Typed.Inv => err()
    }
  }

  def methodScope(typeHierarchy: TypeHierarchy, context: QName, sc: Scope, sig: AST.MethodSig, reporter: Reporter): (B, Scope.Local) = {
    val typeParams = typeParamMap(sig.typeParams, reporter)
    var scope = Scope.Local.create(typeParams.map, sc)
    sig.returnType.typedOpt match {
      case tOpt @ Some(_) => scope = scope(methodReturnOpt = tOpt)
      case _ => halt("Unexpected situation when type checking method.")
    }
    var ok = T
    for (p <- sig.params) {
      val id = p.id.value
      scope.resolveName(typeHierarchy.nameMap, ISZ(id)) match {
        case Some(_: Info.LocalVar) =>
          reporter.error(
            p.id.attr.posOpt,
            TypeChecker.typeCheckerKind,
            s"Identifier '$id' shadows a declaration in the enclosing context."
          )
          ok = F
        case _ =>
          scope = scope(
            nameMap = scope.nameMap + id ~> Info.LocalVar(
              context :+ id,
              T,
              p.id,
              p.tipe.typedOpt,
              Some(AST.ResolvedInfo.LocalVar(context, AST.ResolvedInfo.LocalVar.Scope.Current, F, T, id))
            )
          )
      }
    }
    return (ok, scope)
  }

  @pure def createNewScope(scope: Scope): Scope.Local = {
    return Scope.Local.create(HashMap.empty, scope)
  }

  def checkMethodContractSequent(strictAliasing: B, th: TypeHierarchy, context: QName, scope: Scope, isMutableContext: B, stmt: AST.Stmt.Method, reporter: Reporter): AST.Stmt.Method = {
    val (ok, sc) = TypeChecker.methodScope(th, context, scope, stmt.sig, reporter)
    if (ok) {
      val tc = TypeChecker(th, context, isMutableContext, TypeChecker.ModeContext.Spec, strictAliasing)
      val newStmt: AST.Stmt.Method = if (stmt.mcontract.isEmpty) {
        val bstmts = stmt.bodyOpt.get.stmts
        val ds = bstmts(0).asInstanceOf[AST.Stmt.DeduceSequent]
        val sequent = ds.sequents(0)
        val newPremises: ISZ[AST.Exp] = for (premise <- sequent.premises) yield
          tc.checkExp(Some(AST.Typed.b), sc, premise, reporter)._1
        val newSequent = sequent(premises = newPremises,
          conclusion = tc.checkExp(Some(AST.Typed.b), sc, sequent.conclusion, reporter)._1)
        stmt(bodyOpt = Some(stmt.bodyOpt.get(stmts = ds(sequents = ISZ(newSequent)) +: ops.ISZOps(bstmts).drop(1))))
      } else {
        stmt(mcontract = tc.checkMethodContract(sc, stmt.contract, reporter))
      }
      if (reporter.hasError) {
        return newStmt
      }
      val reads: ISZ[AST.ResolvedInfo] = for (r <- newStmt.contract.reads) yield r.attr.resOpt.get
      val writes: ISZ[AST.ResolvedInfo] = for (w <- newStmt.contract.modifies) yield w.attr.resOpt.get
      val methodRes = stmt.attr.resOpt.get.asInstanceOf[AST.ResolvedInfo.Method]
      return newStmt(attr = newStmt.attr(resOpt = Some(methodRes(reads = reads, writes = writes))))
    } else {
      return stmt
    }
  }

  def checkStrictPureMethod(strictAliasing: B, th: TypeHierarchy, context: QName, scope: Scope, isMutableContext: B, stmt: AST.Stmt.Method, reporter: Reporter): AST.Stmt.Method = {
    assert(stmt.purity == AST.Purity.StrictPure)
    val tc = TypeChecker(th, context, isMutableContext, TypeChecker.ModeContext.Code, strictAliasing)
    return tc.checkMethod(scope, stmt, reporter)
  }

  def checkFactStmt(strictAliasing: B, th: TypeHierarchy, context: QName, scope: Scope, stmt: AST.Stmt.Fact, reporter: Reporter): AST.Stmt.Fact = {
    val tc = TypeChecker(th, context, F, ModeContext.Spec, strictAliasing)
    val bExpectedOpt: Option[AST.Typed] = Some(AST.Typed.b)
    val typeParams = typeParamMap(stmt.typeParams, reporter)
    val sc = Scope.Local.create(typeParams.map, scope)
    return stmt(claims = for (claim <- stmt.claims) yield tc.checkExp(bExpectedOpt, sc, claim, reporter)._1)
  }

  def checkTheoremStmt(strictAliasing: B, th: TypeHierarchy, context: QName, scope: Scope, stmt: AST.Stmt.Theorem, reporter: Reporter): AST.Stmt.Theorem = {
    val tc = TypeChecker(th, context, F, ModeContext.Spec, strictAliasing)
    val bExpectedOpt: Option[AST.Typed] = Some(AST.Typed.b)
    val typeParams = typeParamMap(stmt.typeParams, reporter)
    val sc = Scope.Local.create(typeParams.map, scope)
    if (stmt.isFun) {
      val qclaim = stmt.claim.asInstanceOf[AST.Exp.QuantType]
      val (newFun, tOpt, proofScope) = tc.checkFun(None(), sc, qclaim.fun, reporter)
      tOpt match {
        case Some(t: AST.Typed.Fun) if t.ret != AST.Typed.b =>
          reporter.error(qclaim.fun.posOpt, TypeChecker.typeCheckerKind, s"Expecting type 'B', but '${t.ret}' found.")
        case _ =>
      }
      return stmt(claim = qclaim(fun = newFun.asInstanceOf[AST.Exp.Fun]),
        proof = stmt.proof(steps = for (step <- stmt.proof.steps) yield tc.checkStep(proofScope, step, reporter)))
    } else {
      return stmt(claim = tc.checkExp(bExpectedOpt, sc, stmt.claim, reporter)._1,
        proof = stmt.proof(steps = for (step <- stmt.proof.steps) yield tc.checkStep(sc, step, reporter)))
    }
  }

  def checkInvStmt(strictAliasing: B, th: TypeHierarchy, context: QName, scope: Scope, stmt: AST.Stmt.Inv, reporter: Reporter): AST.Stmt.Inv = {
    val tc = TypeChecker(th, context, F, ModeContext.Spec, strictAliasing)
    val bExpectedOpt: Option[AST.Typed] = Some(AST.Typed.b)
    return stmt(claims = for (claim <- stmt.claims) yield tc.checkExp(bExpectedOpt, scope, claim, reporter)._1,
      attr = stmt.attr(typedOpt = AST.Typed.unitOpt))
  }

  def checkDataRefinement(strictAliasing: B, th: TypeHierarchy, context: QName, scope: Scope, stmt: AST.Stmt.DataRefinement, reporter: Reporter): AST.Stmt.DataRefinement = {
    val tc = TypeChecker(th, context, F, ModeContext.Spec, strictAliasing)
    tc.checkDataRefinement(scope, stmt, reporter)
  }
}

import TypeChecker._

@datatype class TypeChecker(val typeHierarchy: TypeHierarchy, val context: QName, val isInMutableContext: B, val mode: ModeContext.Type, val strictAliasing: B) {

  @pure def inSpec: B = {
    mode match {
      case ModeContext.Code => return F
      case ModeContext.Spec => return T
      case ModeContext.SpecPost => return T
    }
  }

  def basicKind(scope: Scope, tpe: AST.Typed, posOpt: Option[Position], reporter: Reporter): Option[BasicKind.Type] = {
    tpe match {
      case tpe: AST.Typed.Name =>
        if (tpe.args.nonEmpty) {
          return None()
        }
        if (tpe.ids.size == z"3") {
          tpe.ids match {
            case AST.Typed.bName => return Some(BasicKind.B)
            case AST.Typed.zName => return Some(BasicKind.Z)
            case AST.Typed.cName => return Some(BasicKind.C)
            case AST.Typed.f32Name => return Some(BasicKind.F32)
            case AST.Typed.f64Name => return Some(BasicKind.F64)
            case AST.Typed.rName => return Some(BasicKind.R)
            case AST.Typed.stringName => return Some(BasicKind.String)
            case _ =>
          }
        }
        scope.resolveType(typeMap, tpe.ids) match {
          case Some(ti: TypeInfo.SubZ) =>
            return Some(if (ti.ast.isBitVector) BasicKind.Bits else BasicKind.Range)
          case _ =>
        }
      case _ =>
    }
    return None()
  }

  @pure def typeMap: TypeMap = {
    return typeHierarchy.typeMap
  }

  @pure def nameMap: NameMap = {
    return typeHierarchy.nameMap
  }

  def checkInfo(scopeOpt: Option[Scope], info: Info,
                posOpt: Option[Position], reporter: Reporter): (Option[AST.Typed], Option[AST.ResolvedInfo]) = {
    info match {
      case info: Info.LocalVar =>
        @pure def rCurrent(): (Option[AST.Typed], Option[AST.ResolvedInfo]) = {
          return (info.typedOpt, info.resOpt)
        }
        val res: AST.ResolvedInfo.LocalVar = info.resOpt match {
          case Some(r: AST.ResolvedInfo.LocalVar) => r
          case _ => return rCurrent()
        }
        if (res.isSpec && !inSpec) {
          reporter.error(posOpt, typeCheckerKind, "Cannot access @spec var in non-spec context.")
          return (None(), None())
        }
        if (context != res.context) {
          return (info.typedOpt, Some(res(scope = AST.ResolvedInfo.LocalVar.Scope.Closure)))
        } else {
          scopeOpt match {
            case Some(scope: Scope.Local) if scope.nameMap.contains(info.ast.value) => return rCurrent()
            case _ => return (info.typedOpt, Some(res(scope = AST.ResolvedInfo.LocalVar.Scope.Outer)))
          }
        }
      case info: Info.Package => return (info.typedOpt, info.resOpt)
      case info: Info.Object => return (info.typedOpt, info.resOpt)
      case info: Info.Enum => return (info.typedOpt, info.resOpt)
      case info: Info.EnumElement => return (info.typedOpt, info.resOpt)
      case info: Info.Method =>
        if (inSpec && info.ast.purity == AST.Purity.Impure) {
          reporter.error(posOpt, typeCheckerKind, "Cannot access impure method in spec context.")
          return (None(), None())
        } else {
          return (info.typedOpt, info.resOpt)
        }
      case info: Info.ExtMethod =>
        if (inSpec && !info.ast.isPure) {
          reporter.error(posOpt, typeCheckerKind, "Cannot access impure @ext method in spec context.")
          return (None(), None())
        } else {
          return (info.typedOpt, info.resOpt)
        }
      case info: Info.JustMethod =>
        if (!inSpec) {
          reporter.error(posOpt, typeCheckerKind, "Cannot access @just method in non-spec context.")
          return (None(), None())
        } else {
          return (info.typedOpt, info.resOpt)
        }
      case info: Info.Var => return (info.typedOpt, info.resOpt)
      case info: Info.SpecMethod =>
        if (!inSpec) {
          reporter.error(posOpt, typeCheckerKind, "Cannot access @spec method in non-spec context.")
          return (None(), None())
        } else {
          return (info.typedOpt, info.resOpt)
        }
      case info: Info.SpecVar =>
        if (!inSpec) {
          reporter.error(posOpt, typeCheckerKind, "Cannot access @spec var in non-spec context.")
          return (None(), None())
        } else {
          return (info.typedOpt, info.resOpt)
        }
      case info: Info.Inv =>
        if (!inSpec) {
          reporter.error(posOpt, typeCheckerKind, "Cannot access Invariant in non-spec context.")
          return (None(), None())
        } else {
          return (info.typedOpt, info.resOpt)
        }
      case info: Info.Fact =>
        if (!inSpec) {
          reporter.error(posOpt, typeCheckerKind, "Cannot access Fact in non-spec context.")
          return (None(), None())
        } else {
          return (info.typedOpt, info.resOpt)
        }
      case info: Info.Theorem =>
        if (!inSpec) {
          reporter.error(posOpt, typeCheckerKind, "Cannot access Theorem in non-spec context.")
          return (None(), None())
        } else {
          return (info.typedOpt, info.resOpt)
        }
    }
  }

  def checkInfoOpt(
    scopeOpt: Option[Scope],
    infoOpt: Option[Info],
    posOpt: Option[Position],
    reporter: Reporter
  ): (Option[AST.Typed], Option[AST.ResolvedInfo]) = {
    infoOpt match {
      case Some(info) => return checkInfo(scopeOpt, info, posOpt, reporter)
      case _ => return (None(), None())
    }
  }

  def checkStringInterpolator(
    posOpt: Option[Position],
    scope: Scope,
    prefix: String,
    value: String,
    reporter: Reporter
  ): Option[AST.Typed] = {
    def checkRange(n: Z, ast: AST.Stmt.SubZ): Unit = {
      if (ast.hasMin) {
        if (n < ast.min) {
          reporter.error(posOpt, typeCheckerKind, s"Literal $value is lower than $prefix's minimum of ${ast.min}.")
        }
      }
      if (ast.hasMax) {
        if (n > ast.max) {
          reporter.error(posOpt, typeCheckerKind, s"Literal $value is higher than $prefix's maximum of ${ast.max}.")
        }
      }
    }
    scope.resolveName(typeHierarchy.nameMap, ISZ(prefix)) match {
      case Some(info: Info.Object) =>
        scope.resolveType(typeHierarchy.typeMap, info.owner) match {
          case Some(typeInfo: TypeInfo.SubZ) =>
            checkRange(Z(value).get, typeInfo.ast)
            return Some(AST.Typed.Name(typeInfo.name, ISZ()))
          case _ =>
        }
      case Some(info: Info.LocalVar) =>
        info.posOpt match {
          case Some(pos) => reporter.error(posOpt, typeCheckerKind, s"Could not resolve literal interpolator for '$prefix' due to name conflict with a local variable declared at [${pos.beginLine}, ${pos.beginColumn}].")
          case _ => reporter.error(posOpt, typeCheckerKind, s"Could not resolve literal interpolator for '$prefix' due to name conflict with a local variable.")
        }
        return None()
      case _ =>
    }
    reporter.error(posOpt, typeCheckerKind, s"Could not resolve literal interpolator for '$prefix'.")
    return None()
  }

  def checkUnboundTypeVar(
    posOpt: Option[Position],
    t: AST.Typed,
    sm: HashMap[String, AST.Typed],
    typeParams: ISZ[String],
    reporter: Reporter
  ): B = {
    var unbound = ISZ[String]()
    for (tp <- typeParams) {
      sm.get(tp) match {
        case Some(_) =>
        case _ => unbound = unbound :+ tp
      }
    }
    if (unbound.nonEmpty) {
      reporter
        .error(posOpt, typeCheckerKind, st"Could not infer type parameter(s): '${(unbound, "', '")}' for $t.".render)
      return F
    }
    return T
  }

  def checkIndexType(posOpt: Option[Position], t: AST.Typed, reporter: Reporter): Unit = {
    if (t == AST.Typed.z) {
      return
    }
    t match {
      case t: AST.Typed.Name =>
        typeHierarchy.typeMap.get(t.ids) match {
          case Some(_: TypeInfo.SubZ) => return
          case _ =>
        }
      case _ =>
    }

    reporter
      .error(posOpt, typeCheckerKind, s"Expecting either type '${AST.Typed.z}', @bits, or @range, but '$t' found.")
  }

  def checkEnumGens(
    checkMixed: B,
    sc: Scope,
    enumGens: ISZ[AST.EnumGen.For],
    reporter: Reporter
  ): (Option[Scope.Local], ISZ[AST.EnumGen.For], Option[AST.Typed], B) = {

    var scope = createNewScope(sc)
    var ok = T
    var isISOpt: Option[B] = None()
    var indexTypeOpt: Option[AST.Typed] = None()

    def declId(idOpt: Option[AST.Id], tOpt: Option[AST.Typed]): Unit = {
      idOpt match {
        case Some(id) =>
          val key = id.value
          if (scope.nameMap.contains(key)) {
            reporter.error(
              id.attr.posOpt,
              typeCheckerKind,
              s"Cannot declare '$key' because the identifier has already been previously declared."
            )
            ok = F
          } else {
            scope = scope(
              nameMap = scope.nameMap + key ~> Info.LocalVar(
                context :+ key,
                F,
                id,
                tOpt,
                Some(AST.ResolvedInfo.LocalVar(context, AST.ResolvedInfo.LocalVar.Scope.Current, F, T, key))
              )
            )
          }
        case _ =>
      }
    }

    def checkIS(posOpt: Option[Position], b: B): B = {
      if (!checkMixed) {
        return T
      }
      isISOpt match {
        case Some(prev) =>
          if (b != prev) {
            reporter.error(
              posOpt,
              typeCheckerKind,
              st"For-yield expressions cannot involve both immutable and mutable sequence/generator.".render
            )
            return F
          }
        case _ => isISOpt = Some(b)
      }
      return T
    }

    def checkEnumGen(enumGen: AST.EnumGen.For): AST.EnumGen.For = {
      enumGen.range match {
        case range: AST.EnumGen.Range.Step =>
          ok = checkIS(range.attr.posOpt, T)
          val (newStartExp, startExpTypeOpt) = checkExp(None(), scope, range.start, reporter)
          val (newEndExp, endExpTypeOpt) = checkExp(startExpTypeOpt, scope, range.end, reporter)
          val newByOpt: Option[AST.Exp] = range.byOpt match {
            case Some(by) =>
              val (newByExp, _) = checkExp(Some(AST.Typed.z), scope, by, reporter)
              Some(newByExp)
            case _ => None()
          }
          (startExpTypeOpt, endExpTypeOpt) match {
            case (Some(startExpType), Some(_)) if newByOpt.nonEmpty || range.byOpt.isEmpty =>
              checkIndexType(range.start.posOpt, startExpType, reporter)
              indexTypeOpt = Some(AST.Typed.z)
              declId(enumGen.idOpt, Some(startExpType))
              val newCondOpt: Option[AST.Exp] = enumGen.condOpt match {
                case Some(cond) if ok =>
                  val (newCondExp, _) = checkExp(AST.Typed.bOpt, scope, cond, reporter)
                  Some(newCondExp)
                case _ => None()
              }
              val thisL = this
              val (newInvs, _) = thisL(mode = ModeContext.Spec).checkLoopInv(scope, enumGen.invariants, ISZ(), reporter)
              return enumGen(
                range = range(start = newStartExp, end = newEndExp, byOpt = newByOpt),
                condOpt = newCondOpt,
                invariants = newInvs
              )
            case _ =>
              ok = F
              return enumGen(range = range(start = newStartExp, end = newEndExp, byOpt = newByOpt))
          }
        case range: AST.EnumGen.Range.Expr =>
          def reportErrType(t: ST): Unit = {
            reporter.error(
              range.exp.posOpt,
              typeCheckerKind,
              st"Expecting either type of 'org.sireum.{IS, MS, Jen, MJen}', but '$t' found.".render
            )
          }
          val (newExp, expTypeOpt) = checkExp(None(), scope, range.exp, reporter)
          expTypeOpt match {
            case Some(expType: AST.Typed.Name) =>
              expType.ids match {
                case AST.Typed.isName =>
                  ok = checkIS(range.attr.posOpt, T)
                  val indexType = expType.args(0)
                  indexTypeOpt = Some(indexType)
                  enumGen.idOpt match {
                    case Some(id) => scope = scope(indexMap = scope.indexMap + id.value ~> indexType)
                    case _ =>
                  }
                case AST.Typed.msName =>
                  ok = checkIS(range.attr.posOpt, F)
                  val indexType = expType.args(0)
                  indexTypeOpt = Some(indexType)
                  enumGen.idOpt match {
                    case Some(id) => scope = scope(indexMap = scope.indexMap + id.value ~> indexType)
                    case _ =>
                  }
                case AST.Typed.jenName =>
                  ok = checkIS(range.attr.posOpt, T)
                  indexTypeOpt = None()
                case AST.Typed.mjenName =>
                  ok = checkIS(range.attr.posOpt, F)
                  indexTypeOpt = None()
                case ids =>
                  reportErrType(st"${(ids, ".")}")
                  ok = F
              }
              val newCondOpt: Option[AST.Exp] = if (ok) {
                declId(enumGen.idOpt,
                  Some(if (indexTypeOpt.isEmpty) expType.args(0) else expType.args(1)))
                enumGen.condOpt match {
                  case Some(cond) =>
                    val (newCond, _) = checkExp(AST.Typed.bOpt, scope, cond, reporter)
                    Some(newCond)
                  case _ => None()
                }
              } else {
                None()
              }
              val thisL = this
              val (newInvs, _) = thisL(mode = ModeContext.Spec).checkLoopInv(scope, enumGen.invariants, ISZ(), reporter)
              return enumGen(range = range(exp = newExp), condOpt = newCondOpt, invariants = newInvs)
            case Some(expType) =>
              reportErrType(st"$expType")
              ok = F
              return enumGen(range = range(exp = newExp))
            case _ =>
              ok = F
              return enumGen(range = range(exp = newExp))
          }
      }
    }

    var newEnumGens = ISZ[AST.EnumGen.For]()
    var i = 0
    val size = enumGens.size
    while (ok && i < size) {
      val newEnumGen = checkEnumGen(enumGens(i))
      newEnumGens = newEnumGens :+ newEnumGen
      i = i + 1
    }

    while (i < size) {
      newEnumGens = newEnumGens :+ enumGens(i)
      i = i + 1
    }

    return (if (ok) Some(scope) else None(), newEnumGens, indexTypeOpt, isISOpt.getOrElse(T))
  }

  def checkFun(
    expectedOpt: Option[AST.Typed],
    sc: Scope,
    exp: AST.Exp.Fun,
    reporter: Reporter
  ): (AST.Exp, Option[AST.Typed], Scope.Local) = {
    var scope = createNewScope(sc)
    var ok = T

    def declId(idOpt: Option[AST.Id], tOpt: Option[AST.Typed]): Unit = {
      idOpt match {
        case Some(id) =>
          val key = id.value
          if (scope.nameMap.contains(key)) {
            reporter.error(
              id.attr.posOpt,
              typeCheckerKind,
              s"Cannot declare '$key' because the identifier has already been previously declared."
            )
            ok = F
          } else {
            scope = scope(
              nameMap = scope.nameMap + key ~> Info.LocalVar(
                context :+ key,
                F,
                id,
                tOpt,
                Some(AST.ResolvedInfo.LocalVar(context, AST.ResolvedInfo.LocalVar.Scope.Current, F, T, key))
              )
            )
          }
        case _ =>
      }
    }

    val untypedParam = ops.ISZOps(exp.params).exists(p => p.tipeOpt.isEmpty)
    if (untypedParam) {
      expectedOpt match {
        case Some(expected: AST.Typed.Fun) =>
          val size = expected.args.size
          if (size != exp.params.size) {
            reporter.error(exp.posOpt, typeCheckerKind, s"Expecting $size parameters, but ${exp.params.size} found.")
            return (exp, None(), scope)
          }

          var newParams = ISZ[AST.Exp.Fun.Param]()
          var i = 0
          while (ok && i < size) {
            val p = exp.params(i)
            val expectedType = expected.args(i)
            declId(p.idOpt, Some(expectedType))
            val (newTipeOpt, newTypedOpt): (Option[AST.Type], Option[AST.Typed]) = p.tipeOpt match {
              case Some(tipe) =>
                val tOpt = typeHierarchy.typed(scope, tipe, reporter)
                tOpt match {
                  case Some(t) if t.typedOpt.nonEmpty =>
                    if (t.typedOpt.get != expectedType) {
                      reporter.error(
                        tipe.posOpt,
                        typeCheckerKind,
                        s"Expecting type '$expectedType', but '${t.typedOpt.get}' found."
                      )
                    }
                    (tOpt, t.typedOpt)
                  case _ =>
                    (tOpt, None())
                }
              case _ => (None(), Some(expectedType))
            }
            newParams = newParams :+ p(tipeOpt = newTipeOpt, typedOpt = newTypedOpt)
            i = i + 1
          }
          if (!ok) {
            return (exp, None(), scope)
          }
          val (newExp, _) = checkAssignExp(Some(expected.ret), scope, exp.exp, reporter)
          val tOpt: Option[AST.Typed] = Some(expected)
          return (exp(context = context, params = newParams, exp = newExp, attr = exp.attr(typedOpt = tOpt)), tOpt, scope)
        case _ =>
          for (p <- exp.params if p.tipeOpt.isEmpty) {
            val posOpt: Option[Position] = if (p.idOpt.isEmpty) None() else p.idOpt.get.attr.posOpt
            reporter.error(posOpt, typeCheckerKind, "Explicit type for the lambda expression is required.")
          }
          return (exp, None(), scope)
      }
    } else {
      val expectedRetOpt: Option[AST.Typed] = expectedOpt match {
        case Some(expected: AST.Typed.Fun) => Some(expected.ret)
        case _ => None()
      }
      var newParams = ISZ[AST.Exp.Fun.Param]()
      var paramTypes = ISZ[AST.Typed]()
      for (p <- exp.params if ok) {
        val newTipeOpt: Option[AST.Type] = p.tipeOpt match {
          case Some(tipe) =>
            val tOpt = typeHierarchy.typed(scope, tipe, reporter)
            tOpt
          case _ => None()
        }
        val typedOpt: Option[AST.Typed] = newTipeOpt match {
          case Some(newTipe) if newTipe.typedOpt.nonEmpty =>
            val tOpt = newTipe.typedOpt
            val t = tOpt.get
            paramTypes = paramTypes :+ t
            declId(p.idOpt, tOpt)
            tOpt
          case _ =>
            ok = F
            None()
        }
        newParams = newParams :+ p(tipeOpt = newTipeOpt, typedOpt = typedOpt)
      }
      if (!ok) {
        return (exp, None(), scope)
      }
      val (newExp, retTypeOpt) = checkAssignExp(expectedRetOpt, scope, exp.exp, reporter)
      val tOpt: Option[AST.Typed] = retTypeOpt match {
        case Some(retType) => Some(AST.Typed.Fun(T, F, paramTypes, retType))
        case _ => None()
      }
      return (exp(context = context, params = newParams, exp = newExp, attr = exp.attr(typedOpt = tOpt)), tOpt, scope)
    }
  }

  def checkSelectH(
    receiverType: AST.Typed,
    ident: AST.Id,
    typeArgs: ISZ[AST.Typed],
    reporter: Reporter
  ): (Option[AST.Typed], Option[AST.ResolvedInfo], ISZ[AST.Typed]) = {
    val id = ident.value
    id.native match {
      case "apply" => return (Some(receiverType), applyResOpt, typeArgs)
      case _ =>
    }

    @pure def noResult: (Option[AST.Typed], Option[AST.ResolvedInfo], ISZ[AST.Typed]) = {
      return (None(), None(), typeArgs)
    }

    def errAccess(t: AST.Typed): Unit = {
      reporter.error(ident.attr.posOpt, typeCheckerKind, s"'$id' is not a member of type '$t'.")
    }

    def checkAccess(t: AST.Typed): (Option[AST.Typed], Option[AST.ResolvedInfo], ISZ[AST.Typed]) = {
      id.native match {
        case "string" if typeArgs.isEmpty => return (AST.Typed.stringOpt, stringResOpt, typeArgs)
        case "hash" if typeArgs.isEmpty => return (AST.Typed.zOpt, hashResOpt, typeArgs)
        case "asInstanceOf" if typeArgs.size == z"1" =>
          val asT = typeArgs(0)
          if (t == asT) {
            reporter.warn(ident.attr.posOpt, typeCheckerKind, s"Useless 'asInstanceOf' on same type '$t'.")
          }
          if (!t.isInstanceOf[AST.Typed.Name]) {
            reporter.error(ident.attr.posOpt, typeCheckerKind, s"Cannot use 'asInstanceOf' on '$t'.")
          } else if (!typeHierarchy.isSubType(t, asT) && !typeHierarchy.isSubType(asT, t)) {
            reporter.error(
              ident.attr.posOpt,
              typeCheckerKind,
              s"Cannot use 'asInstanceOf' on unrelated types '$t' and '$asT'."
            )
          }
          return (Some(asT), asInstanceOfResOpt, ISZ())
        case "isInstanceOf" if typeArgs.size == z"1" =>
          val asT = typeArgs(0)
          if (t == asT) {
            reporter
              .warn(ident.attr.posOpt, typeCheckerKind, s"Useless 'isInstanceOf' on same type '$t' (trivially true).")
          }
          if (!t.isInstanceOf[AST.Typed.Name]) {
            reporter.error(ident.attr.posOpt, typeCheckerKind, s"Cannot use 'isInstanceOf' on '$t'.")
          } else if (!typeHierarchy.isSubType(t, asT) && !typeHierarchy.isSubType(asT, t)) {
            reporter.error(
              ident.attr.posOpt,
              typeCheckerKind,
              s"Cannot use 'isInstanceOf' on unrelated types '$t' and '$asT'."
            )
          }
          return (AST.Typed.bOpt, isInstanceOfResOpt, ISZ())
        case _ =>
          t match {
            case t: AST.Typed.Name =>
              if (t.args.isEmpty) {
                typeHierarchy.typeMap.get(t.ids) match {
                  case Some(info: TypeInfo.Enum) =>
                    id.native match {
                      case "name" => return (info.nameTypedOpt, TypeInfo.Enum.nameResOpt, typeArgs)
                      case "ordinal" => return (info.ordinalTypedOpt, TypeInfo.Enum.ordinalResOpt, typeArgs)
                      case _ =>
                    }
                  case _ =>
                }
              } else if (id == "indices" && (t.ids == AST.Typed.isName || t.ids == AST.Typed.msName)) {
                return (Some(AST.Typed.Name(AST.Typed.isName, ISZ(AST.Typed.z, t.args(0)))), indicesResOpt, typeArgs)
              }
            case _ =>
          }
          errAccess(t)
          return noResult
      }
    }

    def errTypeArgs(t: AST.Typed): Unit = {
      reporter.error(ident.attr.posOpt, typeCheckerKind, s"Member '$id' of type '$t' does not accept type arguments.")
    }

    def errSpecImpureAccess(t: AST.Typed): Unit = {
      reporter.error(ident.attr.posOpt, typeCheckerKind, s"Cannot access non-pure member '$id' of type '$t' in contract specifications.")
    }

    receiverType match {
      case receiverType: AST.Typed.Name =>
        typeHierarchy.typeMap.get(receiverType.ids).get match {
          case info: TypeInfo.Sig =>
            val r = info.typeRes(id, inSpec)
            r match {
              case (T, Some(rt), _) =>
                val smOpt =
                  buildTypeSubstMap(info.name, ident.attr.posOpt, info.ast.typeParams, receiverType.args, reporter)
                smOpt match {
                  case Some(sm) => return (Some(rt.subst(sm)), AST.ResolvedInfo.substOpt(r._3, sm), typeArgs)
                  case _ => return noResult
                }
              case (T, _, _) => val res = checkAccess(receiverType); return res
              case (_, _, _) =>
                errSpecImpureAccess(receiverType)
                return noResult
            }
          case info: TypeInfo.Adt =>
            val r = info.typeRes(id, inSpec)
            r match {
              case (T, Some(rt), _) =>
                val smOpt =
                  buildTypeSubstMap(info.name, ident.attr.posOpt, info.ast.typeParams, receiverType.args, reporter)
                smOpt match {
                  case Some(sm) => return (Some(rt.subst(sm)), AST.ResolvedInfo.substOpt(r._3, sm), typeArgs)
                  case _ => return noResult
                }
              case (T, _, _) => val res = checkAccess(receiverType); return res
              case (_, _, _) =>
                errSpecImpureAccess(receiverType)
                return noResult
            }
          case _ => val res = checkAccess(receiverType); return res
        }
      case receiverType: AST.Typed.Tuple =>
        if (id.size == z"0" || ops.StringOps(id).first != c"_") {
          val res = checkAccess(receiverType)
          return res
        }
        Z(ops.StringOps(id).substring(1, id.size)) match {
          case Some(n) =>
            val size = receiverType.args.size
            if (!(0 < n && n <= size)) {
              errAccess(receiverType)
              return noResult
            }
            if (typeArgs.nonEmpty) {
              errTypeArgs(receiverType)
              return noResult
            }
            return (Some(receiverType.args(n - 1)), Some(AST.ResolvedInfo.Tuple(size, n)), typeArgs)
          case _ =>
            errAccess(receiverType)
            return noResult
        }
      case receiverType: AST.Typed.Fun =>
        errAccess(receiverType)
        return noResult
      case receiverType: AST.Typed.Package =>
        val r = checkInfoOpt(None(), typeHierarchy.nameMap.get(receiverType.name :+ id), ident.attr.posOpt, reporter)
        if (r._1.isEmpty) {
          reporter.error(
            ident.attr.posOpt,
            typeCheckerKind,
            st"'$id' is not a member of package '${(receiverType.name, ".")}'.".render
          )
        }
        return (r._1, r._2, typeArgs)
      case receiverType: AST.Typed.Object =>
        typeHierarchy.typeMap.get(receiverType.name) match {
          case Some(info: TypeInfo.SubZ) =>
            id.native match {
              case "Max" if info.ast.hasMax && typeArgs.isEmpty => return (info.typedOpt, minResOpt, typeArgs)
              case "Min" if info.ast.hasMin && typeArgs.isEmpty => return (info.typedOpt, maxResOpt, typeArgs)
              case "random" if typeArgs.isEmpty => return (info.typedOpt, randomResOpt, typeArgs)
              case _ =>
            }
          case _ =>
        }
        val r = checkInfoOpt(None(), typeHierarchy.nameMap.get(receiverType.name :+ normalizeSpecId(id)), ident.attr.posOpt, reporter)
        if (r._1.isEmpty) {
          reporter.error(
            ident.attr.posOpt,
            typeCheckerKind,
            st"'$id' is not a member of object '${(receiverType.name, ".")}'.".render
          )
        }
        return (r._1, r._2, typeArgs)
      case receiverType: AST.Typed.Enum =>
        typeHierarchy.nameMap.get(receiverType.name) match {
          case Some(info: Info.Enum) =>
            id.native match {
              case "byName" => return (info.byNameTypedOpt, Info.Enum.byNameResOpt, typeArgs)
              case "byOrdinal" => return (info.byOrdinalTypedOpt, Info.Enum.byOrdinalResOpt, typeArgs)
              case "elements" => return (info.elementsTypedOpt, Info.Enum.elementsResOpt, typeArgs)
              case "numOfElements" => return (info.numOfElementsTypedOpt, Info.Enum.numOfElementsResOpt, typeArgs)
              case _ =>
            }
            info.elements.get(id) match {
              case Some(res) =>
                if (typeArgs.nonEmpty) {
                  errTypeArgs(receiverType)
                  return noResult
                }
                return (info.elementTypedOpt, Some(res), typeArgs)
              case _ =>
                errAccess(receiverType)
                return noResult
            }
          case _ =>
            halt("Unexpected situation while type checking enum access.")
        }
      case receiverType: AST.Typed.Method =>
        errAccess(receiverType)
        return noResult
      case receiverType: AST.Typed.Methods =>
        errAccess(receiverType)
        return noResult
      case receiverType: AST.Typed.TypeVar => val res = checkAccess(receiverType); return res
      case receiverType: AST.Typed.Fact =>
        errAccess(receiverType)
        return noResult
      case receiverType: AST.Typed.Theorem =>
        errAccess(receiverType)
        return noResult
      case receiverType: AST.Typed.Inv =>
        errAccess(receiverType)
        return noResult
    }
  }

  @pure def normalizeSpecId(id: String): String = {
    if (!inSpec) {
      return id
    }
    val idOps = ops.StringOps(id)
    if (idOps.endsWith("_in")) {
      return idOps.substring(0, id.size - 3)
    } else if (idOps.endsWith("_old")) {
      return idOps.substring(0, id.size - 4)
    }
    return id
  }

  def checkId(scope: Scope, id: AST.Id, reporter: Reporter): (Option[AST.Typed], Option[AST.ResolvedInfo]) = {
    val nid = ISZ(normalizeSpecId(id.value))
    val infoOpt = scope.resolveName(typeHierarchy.nameMap, nid)
    infoOpt match {
      case Some(info: Info.LocalVar) => return checkInfo(Some(scope), info, id.attr.posOpt, reporter)
      case _ =>
        scope.thisOpt match {
          case Some(t) =>
            val rep = Reporter.create
            rep.setIgnore(T)
            val r = checkSelectH(t, id, ISZ(), rep)
            if (r._1.nonEmpty) {
              return (r._1, r._2)
            }
          case _ =>
        }
    }
    infoOpt match {
      case Some(info) => return checkInfo(Some(scope), info, id.attr.posOpt, reporter)
      case _ =>
    }
    reporter.error(id.attr.posOpt, typeCheckerKind, s"Could not resolve '${id.value}'.")
    return (None(), None())
  }

  def checkExp(
    expectedOpt: Option[AST.Typed],
    scope: Scope,
    exp: AST.Exp,
    reporter: Reporter
  ): (AST.Exp, Option[AST.Typed]) = {

    def checkAssertume(
      resOpt: Option[AST.ResolvedInfo],
      assertumeExp: AST.Exp.Invoke,
      cond: AST.Exp,
      msgOpt: Option[AST.Exp]
    ): (AST.Exp, Option[AST.Typed]) = {
      val (newCondExp, _) = checkExp(AST.Typed.bOpt, scope, cond, reporter)

      val ident: AST.Exp.Ident = msgOpt match {
        case Some(_) =>
          assertumeExp.ident(
            attr = assertumeExp.ident
              .attr(posOpt = assertumeExp.ident.id.attr.posOpt, resOpt = resOpt, typedOpt = assertumeMsgTypedOpt)
          )
        case _ =>
          assertumeExp.ident(
            attr = assertumeExp.ident
              .attr(posOpt = assertumeExp.ident.id.attr.posOpt, resOpt = resOpt, typedOpt = assertumeTypedOpt)
          )
      }

      msgOpt match {
        case Some(msg) =>
          val (newMsg, _) = checkExp(AST.Typed.stringOpt, scope, msg, reporter)
          val attr = assertumeExp.attr(typedOpt = AST.Typed.unitOpt, resOpt = resOpt)
          return (assertumeExp(ident = ident, args = ISZ(newCondExp, newMsg), attr = attr), AST.Typed.unitOpt)
        case _ =>
          val attr = assertumeExp.attr(typedOpt = AST.Typed.unitOpt, resOpt = resOpt)
          return (assertumeExp(ident = ident, args = ISZ(newCondExp), attr = attr), AST.Typed.unitOpt)
      }
    }

    def checkPrint(
      resOpt: Option[AST.ResolvedInfo],
      printExp: AST.Exp.Invoke,
      args: ISZ[AST.Exp]
    ): (AST.Exp, Option[AST.Typed]) = {
      var newArgs = ISZ[AST.Exp]()
      var argTypes = ISZ[AST.Typed]()
      for (arg <- args) {
        val (newArg, argTypeOpt) = checkExp(None(), scope, arg, reporter)
        argTypeOpt match {
          case Some(argType) =>
            argTypes = argTypes :+ argType
            checkTypeUsage(arg.posOpt, argType, reporter)
          case _ =>
        }
        newArgs = newArgs :+ newArg
      }
      val ident = printExp.ident(
        attr = printExp.attr(
          posOpt = printExp.ident.id.attr.posOpt,
          resOpt = resOpt,
          typedOpt = Some(AST.Typed.Fun(F, F, argTypes, AST.Typed.unit))
        )
      )
      val attr = printExp.attr(typedOpt = AST.Typed.unitOpt, resOpt = resOpt)
      return (printExp(ident = ident, args = newArgs, attr = attr), AST.Typed.unitOpt)
    }

    def checkHalt(haltExp: AST.Exp.Invoke, args: ISZ[AST.Exp]): (AST.Exp, Option[AST.Typed]) = {
      if (args.size != z"1") {
        reporter.error(haltExp.posOpt, typeCheckerKind, s"Expecting one argument, but ${args.size} found.")
        return (haltExp(attr = haltExp.attr(resOpt = haltResOpt, typedOpt = haltTypedOpt)), AST.Typed.nothingOpt)
      }
      val (newArg, argTypeOpt) = checkExp(AST.Typed.stringOpt, scope, args(0), reporter)
      val ident: AST.Exp.Ident = argTypeOpt match {
        case Some(argType) =>
          checkTypeUsage(newArg.posOpt, argType, reporter)
          haltExp.ident(
            attr = haltExp.attr(
              posOpt = haltExp.ident.id.attr.posOpt,
              resOpt = haltResOpt,
              typedOpt = Some(AST.Typed.Fun(F, F, ISZ(argType), AST.Typed.nothing))
            )
          )
        case _ => haltExp.ident
      }
      return (
        haltExp(ident = ident, args = ISZ(newArg), attr = haltExp.attr(resOpt = haltResOpt, typedOpt = haltTypedOpt)),
        AST.Typed.nothingOpt
      )
    }

    def checkUnary(unaryExp: AST.Exp.Unary): (AST.Exp, Option[AST.Typed]) = {
      val (newExp, expTypeOpt) = checkExp(None(), scope, unaryExp.exp, reporter)
      val newUnaryExp = unaryExp(exp = newExp)
      expTypeOpt match {
        case Some(expType) =>
          val kindOpt = basicKind(scope, expType, unaryExp.posOpt, reporter)
          kindOpt match {
            case Some(kind) =>
              if (unaryExp.op == AST.Exp.UnaryOp.Not && kind != BasicKind.B) {
                reporter.error(unaryExp.posOpt, typeCheckerKind, st"Undefined unary operation ! on '$expType'.".render)
                return (newUnaryExp, None())
              }
              if (unaryExp.op == AST.Exp.UnaryOp.Complement &&
                !(kind == BasicKind.B || kind == BasicKind.Bits)) {
                reporter.error(unaryExp.posOpt, typeCheckerKind, st"Undefined unary operation ~ on '$expType'.".render)
                return (newUnaryExp, None())
              }
              var r = newUnaryExp
              val tOpt: Option[AST.Typed] = Some(expType)
              r = r(attr = r.attr(typedOpt = tOpt, resOpt = unopResOpt.get(unaryExp.op).get))
              return (r, tOpt)
            case _ =>
              val (typedOpt, resOpt, _) =
                checkSelectH(expType, AST.Id(AST.Util.unopId(unaryExp.op), AST.Attr(unaryExp.posOpt)), ISZ(), reporter)
              return (unaryExp(exp = newExp, attr = unaryExp.attr(resOpt = resOpt, typedOpt = typedOpt)), typedOpt)
          }
        case _ =>
      }
      return (newExp, None())
    }

    def checkBinary(binaryExp: AST.Exp.Binary): (AST.Exp, Option[AST.Typed]) = {
      def checkAsInvoke(): (AST.Exp, Option[AST.Typed]) = {
        if (ops.StringOps(binaryExp.op).endsWith(":")) {
          val (newInvoke, tOpt) = checkInvoke(
            AST.Exp.Invoke(
              Some(binaryExp.right),
              AST.Exp.Ident(
                AST.Id(binaryExp.op, AST.Attr(binaryExp.posOpt)),
                AST.ResolvedAttr(binaryExp.posOpt, None(), None())
              ),
              ISZ(),
              ISZ(binaryExp.left),
              binaryExp.attr
            )
          )
          newInvoke match {
            case newInvoke: AST.Exp.Invoke =>
              return (
                binaryExp(left = newInvoke.args(0), right = newInvoke.receiverOpt.get, attr = newInvoke.attr),
                tOpt
              )
            case _ => halt("Unexpected situation when type checking binary expression.")
          }
        } else {
          val (newInvoke, tOpt) = checkInvoke(
            AST.Exp.Invoke(
              Some(binaryExp.left),
              AST.Exp.Ident(
                AST.Id(binaryExp.op, AST.Attr(binaryExp.posOpt)),
                AST.ResolvedAttr(binaryExp.posOpt, None(), None())
              ),
              ISZ(),
              ISZ(binaryExp.right),
              binaryExp.attr
            )
          )
          newInvoke match {
            case newInvoke: AST.Exp.Invoke =>
              return (
                binaryExp(left = newInvoke.receiverOpt.get, right = newInvoke.args(0), attr = newInvoke.attr),
                tOpt
              )
            case _ => halt("Unexpected situation when type checking binary expression.")
          }
        }
      }
      if (binaryExp.op == AST.Exp.BinaryOp.MapsTo) {
        val r = checkTuple(
          AST.Exp
            .Tuple(ISZ(binaryExp.left, binaryExp.right), AST.TypedAttr(binaryExp.attr.posOpt, binaryExp.attr.typedOpt))
        )
        r match {
          case (tuple: AST.Exp.Tuple, tOpt) if tuple.args.size == z"2" =>
            return (
              binaryExp(
                left = tuple.args(0),
                right = tuple.args(1),
                attr = binaryExp.attr(
                  posOpt = tuple.attr.posOpt,
                  typedOpt = tuple.attr.typedOpt,
                  resOpt = binopResOpt.get(binaryExp.op).get
                )
              ),
              tOpt
            )
          case _ => halt("Unexpected situation when type checking ~>")
        }
      }

      val rep = Reporter.create
      val (newLeft, leftTypeOpt) = checkExp(None(), scope, binaryExp.left, rep)
      if (leftTypeOpt.isEmpty) {
        reporter.reports(rep.messages)
        return (binaryExp(left = newLeft), None())
      }
      val leftType = leftTypeOpt.get

      def errIncompat(rt: AST.Typed): Unit = {
        reporter.error(
          binaryExp.posOpt,
          typeCheckerKind,
          st"Incompatible types for binary operation '$leftType' ${binaryExp.op} '$rt'.".render
        )
      }

      if (binaryExp.op == AST.Exp.BinaryOp.Eq || binaryExp.op == AST.Exp.BinaryOp.Ne) {
        reporter.reports(rep.messages)
        val (right, rightTypeOpt) = checkExp(None(), scope, binaryExp.right, reporter)
        rightTypeOpt match {
          case Some(rightType) =>
            val isCompat = typeHierarchy.isCompatible(leftType, rightType)
            if (!isCompat) {
              errIncompat(rightType)
            }
          case _ =>
        }
        return (
          binaryExp(
            left = newLeft,
            right = right,
            attr = binaryExp.attr(resOpt = binopResOpt.get(binaryExp.op).get, typedOpt = AST.Typed.bOpt)
          ),
          AST.Typed.bOpt
        )
      }

      val lOpt = basicKind(scope, leftType, binaryExp.left.posOpt, reporter)

      lOpt match {
        case Some(leftKind) if leftKind != BasicKind.String || AST.Util.isCompareBinop(binaryExp.op) =>
          reporter.reports(rep.messages)
          val (newRight, rightTypeOpt) = checkExp(None(), scope, binaryExp.right, reporter)

          rightTypeOpt match {
            case Some(rightType) =>
              def errUndef(): Unit = {
                reporter.error(
                  binaryExp.posOpt,
                  typeCheckerKind,
                  st"Undefined binary operation ${binaryExp.op} on '$leftType'".render
                )
              }

              def normOp(op: String): String = {
                op match {
                  case AST.Exp.BinaryOp.Eq3 => return AST.Exp.BinaryOp.Eq
                  case AST.Exp.BinaryOp.Ne3 => return AST.Exp.BinaryOp.Ne
                  case _ => return op
                }
              }

              val rOpt = basicKind(scope, rightType, binaryExp.right.posOpt, reporter)
              val (op, tOpt): (String, Option[AST.Typed]) = rOpt match {
                case Some(rightKind) =>
                  if (leftKind != rightKind) {
                    errIncompat(rightType)
                    (binaryExp.op, None())
                  } else if ((leftKind == BasicKind.B && AST.Util.isBoolBinop(binaryExp.op)) ||
                    (AST.Util.isArithBinop(binaryExp.op) && leftKind != BasicKind.B) ||
                    (AST.Util.isBitsBinop(binaryExp.op) && (leftKind == BasicKind.Bits || leftKind == BasicKind.C))) {
                    (normOp(binaryExp.op), Some(leftType))
                  } else if (AST.Util.isCompareBinop(binaryExp.op) && leftKind != BasicKind.B) {
                    (normOp(binaryExp.op), Some(AST.Typed.b))
                  } else {
                    errUndef()
                    (binaryExp.op, None())
                  }
                case _ => return checkAsInvoke()
              }
              return (
                binaryExp(
                  left = newLeft,
                  op = op,
                  right = newRight,
                  attr = binaryExp.attr(resOpt = binopResOpt.get(binaryExp.op).getOrElse(None()), typedOpt = tOpt)
                ),
                tOpt
              )
            case _ => return (binaryExp(left = newLeft, right = newRight), None())
          }
        case _ => return checkAsInvoke()
      }
    }

    def checkTuple(tupleExp: AST.Exp.Tuple): (AST.Exp, Option[AST.Typed]) = {
      val expecteds: ISZ[Option[AST.Typed]] = expectedOpt match {
        case Some(expected: AST.Typed.Tuple) =>
          if (tupleExp.args.size == expected.args.size) {
            for (i <- z"0" until tupleExp.args.size) yield Option.some(expected.args(i))
          } else {
            reporter.error(
              tupleExp.posOpt,
              typeCheckerKind,
              s"Expecting a tuple of size ${expected.args.size}, but ${tupleExp.args.size} found."
            )
            return (tupleExp, None())
          }
        case Some(expected) =>
          reporter.error(
            tupleExp.posOpt,
            typeCheckerKind,
            s"Expecting '$expected', but a tuple of size ${tupleExp.args.size} found."
          )
          return (tupleExp, None())
        case _ => for (_ <- z"0" until tupleExp.args.size) yield Option.none()
      }
      var newArgs = ISZ[AST.Exp]()
      var argTypes = ISZ[AST.Typed]()
      var ok = T
      var i = 0
      for (arg <- tupleExp.args) {
        val (newArg, argTypeOpt) = checkExp(expecteds(i), scope, arg, reporter)
        newArgs = newArgs :+ newArg
        argTypeOpt match {
          case Some(argType) =>
            checkTypeUsage(arg.posOpt, argType, reporter)
            argTypes = argTypes :+ argType
          case _ => ok = F
        }
        i = i + 1
      }
      var r = tupleExp
      r = r(args = newArgs)
      if (!ok) {
        return (r, None())
      }
      val t: AST.Typed = expectedOpt match {
        case Some(expected) => expected
        case _ => AST.Typed.Tuple(argTypes)
      }
      val tOpt: Option[AST.Typed] = Some(t)
      r = r(attr = r.attr(typedOpt = tOpt))
      return (r, tOpt)
    }

    def checkStringInterpolate(siExp: AST.Exp.StringInterpolate): (AST.Exp, Option[AST.Typed]) = {
      var args = ISZ[AST.Exp]()
      var argTypes = ISZ[AST.Typed]()
      var ok = T
      for (arg <- siExp.args) {
        val (newArg, argTypeOpt) = checkExp(None(), scope, arg, reporter)
        args = args :+ newArg
        argTypeOpt match {
          case Some(argType) => argTypes = argTypes :+ argType
          case _ => ok = F
        }
      }
      siExp.prefix.native match {
        case "s" =>
          return (siExp(args = args, attr = siExp.attr(typedOpt = AST.Typed.stringOpt)), Some(AST.Typed.string))
        case "st" => return (siExp(args = args, attr = siExp.attr(typedOpt = AST.Typed.stOpt)), Some(AST.Typed.st))
        case "proc" => return (siExp(args = args, attr = siExp.attr(typedOpt = AST.Typed.procOpt)), Some(AST.Typed.proc))
        case _ =>
          val tOpt = checkStringInterpolator(siExp.posOpt, scope, siExp.prefix, siExp.lits(0).value, reporter)
          tOpt match {
            case Some(_) => return (siExp(args = args, attr = siExp.attr(typedOpt = tOpt)), tOpt)
            case _ => return (siExp(args = args), None())
          }
      }
    }

    def checkEtaH(
      t: AST.Typed,
      ref: AST.Exp.Ref,
      typeArgs: ISZ[AST.Typed],
      etaParentOpt: Option[AST.Exp.Eta]
    ): (AST.Exp, Option[AST.Typed]) = {

      val refExp = ref.asExp

      def noResult: (AST.Exp, Option[AST.Typed]) = {
        return (refExp, None())
      }

      t match {
        case t: AST.Typed.Method =>
          val substMap: HashMap[String, AST.Typed] =
            if (typeArgs.isEmpty && t.typeParams.nonEmpty && t.tpe.isByName) {
              expectedOpt match {
                case Some(expected) =>
                  val smOpt = unify(typeHierarchy, refExp.posOpt, TypeRelation.Subtype, expected, t.tpe.ret, reporter)
                  smOpt match {
                    case Some(sm) =>
                      val ok = checkUnboundTypeVar(refExp.posOpt, t, sm, t.typeParams, reporter)
                      if (!ok) {
                        return noResult
                      }
                      sm
                    case _ => return noResult
                  }
                case _ =>
                  reporter.error(refExp.posOpt, typeCheckerKind, s"Explicit type arguments are required.")
                  return noResult
              }
            } else {
              if (t.typeParams.size != typeArgs.size) {
                reporter.error(
                  refExp.posOpt,
                  typeCheckerKind,
                  s"Expecting ${t.typeParams.size} type arguments, but ${typeArgs.size} found."
                )
                return noResult
              }
              var sm = HashMap.emptyInit[String, AST.Typed](typeArgs.size)
              val size = typeArgs.size
              var i = 0
              while (i < size) {
                sm = sm + t.typeParams(i) ~> typeArgs(i)
                i = i + 1
              }
              sm
            }
          if (typeArgs.nonEmpty) {
            etaParentOpt match {
              case Some(etaParent) =>
                val tpe: AST.Typed =
                  if (t.tpe.isByName) t.tpe(isByName = F) else t.tpe
                val tOpt = Some(tpe.subst(substMap))
                return (etaParent(ref = ref.subst(substMap), attr = etaParent.attr(typedOpt = tOpt)), tOpt)
              case _ if t.tpe.isByName =>
                return (ref.subst(substMap).asExp, Some(t.tpe.ret.subst(substMap)))
              case _ =>
                reporter.error(
                  refExp.posOpt,
                  typeCheckerKind,
                  "Method access has to be explicitly eta-expanded to become a function using '_'."
                )
                return noResult
            }
          } else {
            etaParentOpt match {
              case Some(etaParent) =>
                val tOpt: Option[AST.Typed] = Some(if (t.tpe.isByName) t.tpe(isByName = F) else t.tpe)
                return (etaParent(ref = ref.subst(substMap), attr = etaParent.attr(typedOpt = tOpt)), tOpt)
              case _ if t.tpe.isByName => return (ref.subst(substMap).asExp, Some(t.tpe.ret.subst(substMap)))
              case _ =>
                reporter.error(
                  refExp.posOpt,
                  typeCheckerKind,
                  "Method access has to be explicitly eta-expanded to become a function using '_'."
                )
                return noResult
            }
          }
        case _: AST.Typed.Fact if inSpec =>
          etaParentOpt match {
            case Some(etaParent) =>
              val tOpt: Option[AST.Typed] = Some(t)
               return (etaParent(ref = ref, attr = etaParent.attr(typedOpt = tOpt)), tOpt)
            case _ =>
              reporter.error(ref.asExp.posOpt, typeCheckerKind, "Reference to a Fact has to be eta-expanded using '_'.")
              return noResult
          }
        case _: AST.Typed.Theorem if inSpec =>
          etaParentOpt match {
            case Some(etaParent) =>
              val tOpt: Option[AST.Typed] = Some(t)
              return (etaParent(ref = ref, attr = etaParent.attr(typedOpt = tOpt)), tOpt)
            case _ =>
              reporter.error(ref.asExp.posOpt, typeCheckerKind, "Reference to a Theorem has to be eta-expanded using '_'.")
              return noResult
          }
        case _: AST.Typed.Inv if inSpec =>
          etaParentOpt match {
            case Some(etaParent) =>
              val tOpt: Option[AST.Typed] = Some(t)
              return (etaParent(ref = ref, attr = etaParent.attr(typedOpt = tOpt)), tOpt)
            case _ =>
              reporter.error(ref.asExp.posOpt, typeCheckerKind, "Reference to an Invariant has to be eta-expanded using '_'.")
              return noResult
          }
        case _ =>
          if (typeArgs.nonEmpty) {
            reporter.error(refExp.posOpt, typeCheckerKind, s"Cannot supply type arguments on '$t'.")
            return noResult
          } else {
            etaParentOpt match {
              case Some(_) =>
                reporter.error(refExp.posOpt, typeCheckerKind, s"Cannot eta-expand non-method '$t'.")
                return noResult
              case _ =>
                t match {
                  case t: AST.Typed.Fun if t.isByName => return (refExp, Some(t.ret))
                  case _ => return (refExp, Some(t))
                }

            }
          }
      }
    }

    def checkIdent(identExp: AST.Exp.Ident, etaParentOpt: Option[AST.Exp.Eta]): (AST.Exp, Option[AST.Typed]) = {
      val (typedOpt, resOpt) = checkId(scope, identExp.id, reporter)
      val newExp = identExp(attr = identExp.attr(typedOpt = typedOpt, resOpt = resOpt))
      if (typedOpt.isEmpty) {
        return (newExp, typedOpt)
      }
      val r = checkEtaH(typedOpt.get, newExp, ISZ(), etaParentOpt)
      return r
    }

    def checkTypeArgs(tas: ISZ[AST.Type]): Option[(ISZ[AST.Typed], ISZ[AST.Type])] = {
      var typeArgs = ISZ[AST.Typed]()
      var newTargs = ISZ[AST.Type]()
      for (targ <- tas) {
        val tipeOpt = typeHierarchy.typed(scope, targ, reporter)
        tipeOpt match {
          case Some(tipe) if tipe.typedOpt.nonEmpty =>
            typeArgs = typeArgs :+ tipe.typedOpt.get
            newTargs = newTargs :+ tipe
          case _ =>
            return None()
        }
      }
      return Some((typeArgs, newTargs))
    }

    def checkSelect(selectExp: AST.Exp.Select, etaParentOpt: Option[AST.Exp.Eta]): (AST.Exp, Option[AST.Typed]) = {
      val (typeArgs, newTargs): (ISZ[AST.Typed], ISZ[AST.Type]) = {
        val pOpt = checkTypeArgs(selectExp.targs)
        if (pOpt.nonEmpty) {
          pOpt.get
        } else {
          return (selectExp, None())
        }
      }

      val tr: (AST.Exp.Select, Option[AST.Typed], ISZ[AST.Typed]) = {
        selectExp.receiverOpt match {
          case Some(receiver) =>
            val (newReceiver, receiverTypeOpt) = checkExp(None(), scope, receiver, reporter)
            receiverTypeOpt match {
              case Some(receiverType) =>
                val t = checkSelectH(receiverType, selectExp.id, typeArgs, reporter)

                (
                  selectExp(
                    targs = newTargs,
                    receiverOpt = Some(newReceiver),
                    attr = selectExp.attr(typedOpt = t._1, resOpt = t._2)
                  ),
                  t._1,
                  t._3
                )
              case _ =>
                (selectExp(targs = newTargs, receiverOpt = Some(newReceiver)), None[AST.Typed](), typeArgs)
            }
          case _ =>
            val (typedOpt, resOpt) = checkId(scope, selectExp.id, reporter)
            (
              selectExp(targs = newTargs, attr = selectExp.attr(typedOpt = typedOpt, resOpt = resOpt)),
              typedOpt,
              typeArgs
            )
        }
      }

      val (newExp, typedOpt, tArgs) = tr

      if (typedOpt.isEmpty) {
        return (newExp, typedOpt)
      }

      val r = checkEtaH(typedOpt.get, newExp, tArgs, etaParentOpt)
      return r
    }

    def checkInvokeType(
      posOpt: Option[Position],
      resOpt: Option[AST.ResolvedInfo],
      typed: AST.Typed,
      typeArgs: ISZ[AST.Typed],
      numOfArgs: Z,
      argNames: ISZ[String]
    ): (Option[AST.Typed], Option[AST.ResolvedInfo], ISZ[AST.Typed]) = {
      val (tpe, newTypeArgs): (AST.Typed, ISZ[AST.Typed]) = typed match {
        case typed: AST.Typed.Method if typed.tpe.isByName =>
          val smOpt = buildMethodSubstMap(typed, posOpt, typeArgs, reporter)
          smOpt match {
            case Some(sm) => (typed.tpe.ret.subst(sm), ISZ())
            case _ => return (None(), None(), typeArgs)
          }
        case _ => (typed, typeArgs)
      }
      tpe match {
        case tpe: AST.Typed.Object =>
          AST.Typed.basicConstructorMap.get(tpe.name) match {
            case Some(r @ (_, _)) => return (r._1, r._2, newTypeArgs)
            case _ =>
              tpe.name match {
                case AST.Typed.`isName` =>
                  val indexTypeVar = AST.Typed.TypeVar("I")
                  val valueTypeVar = AST.Typed.TypeVar("V")
                  val argTypes: ISZ[AST.Typed] = for (_ <- z"0" until numOfArgs)
                    yield valueTypeVar
                  val constructorType =
                    AST.Typed.Fun(T, F, argTypes, AST.Typed.Name(AST.Typed.isName, ISZ(indexTypeVar, valueTypeVar)))
                  return (
                    Some(
                      AST.Typed.Method(
                        T,
                        AST.MethodMode.Constructor,
                        sTypeParams,
                        AST.Typed.sireumName,
                        "IS",
                        ISZ(),
                        constructorType
                      )
                    ),
                    Some(
                      AST.ResolvedInfo.Method(
                        T,
                        AST.MethodMode.Constructor,
                        sTypeParams,
                        AST.Typed.sireumName,
                        "IS",
                        ISZ(),
                        Some(constructorType),
                        ISZ(),
                        ISZ()
                      )
                    ),
                    newTypeArgs
                  )
                case AST.Typed.`msName` =>
                  val indexTypeVar = AST.Typed.TypeVar("I")
                  val valueTypeVar = AST.Typed.TypeVar("V")
                  val argTypes: ISZ[AST.Typed] =
                    for (_ <- z"0" until numOfArgs) yield valueTypeVar
                  val constructorType: AST.Typed.Fun =
                    AST.Typed.Fun(T, F, argTypes, AST.Typed.Name(AST.Typed.msName, ISZ(indexTypeVar, valueTypeVar)))
                  return (
                    Some(
                      AST.Typed.Method(
                        T,
                        AST.MethodMode.Constructor,
                        sTypeParams,
                        AST.Typed.sireumName,
                        "MS",
                        ISZ(),
                        constructorType
                      )
                    ),
                    Some(
                      AST.ResolvedInfo.Method(
                        T,
                        AST.MethodMode.Constructor,
                        sTypeParams,
                        AST.Typed.sireumName,
                        "MS",
                        ISZ(),
                        Some(constructorType),
                        ISZ(),
                        ISZ()
                      )
                    ),
                    newTypeArgs
                  )
                case AST.Typed.`iszName` =>
                  val valueTypeVar = AST.Typed.TypeVar("V")
                  val argTypes: ISZ[AST.Typed] =
                    for (_ <- z"0" until numOfArgs) yield valueTypeVar
                  val constructorType: AST.Typed.Fun =
                    AST.Typed.Fun(T, F, argTypes, AST.Typed.Name(AST.Typed.isName, ISZ(AST.Typed.z, valueTypeVar)))
                  val typeParams = ISZ[String]("V")
                  return (
                    Some(
                      AST.Typed.Method(
                        T,
                        AST.MethodMode.Constructor,
                        typeParams,
                        AST.Typed.sireumName,
                        "IS",
                        ISZ(),
                        constructorType
                      )
                    ),
                    Some(
                      AST.ResolvedInfo.Method(
                        T,
                        AST.MethodMode.Constructor,
                        typeParams,
                        AST.Typed.sireumName,
                        "IS",
                        ISZ(),
                        Some(constructorType),
                        ISZ(),
                        ISZ()
                      )
                    ),
                    newTypeArgs
                  )
                case AST.Typed.`mszName` =>
                  val valueTypeVar = AST.Typed.TypeVar("V")
                  val argTypes: ISZ[AST.Typed] =
                    for (_ <- z"0" until numOfArgs) yield valueTypeVar
                  val constructorType: AST.Typed.Fun =
                    AST.Typed.Fun(T, F, argTypes, AST.Typed.Name(AST.Typed.msName, ISZ(AST.Typed.z, valueTypeVar)))
                  val typeParams = ISZ[String]("V")
                  return (
                    Some(
                      AST.Typed.Method(
                        T,
                        AST.MethodMode.Constructor,
                        typeParams,
                        AST.Typed.sireumName,
                        "MS",
                        ISZ(),
                        constructorType
                      )
                    ),
                    Some(
                      AST.ResolvedInfo.Method(
                        T,
                        AST.MethodMode.Constructor,
                        typeParams,
                        AST.Typed.sireumName,
                        "MS",
                        ISZ(),
                        Some(constructorType),
                        ISZ(),
                        ISZ()
                      )
                    ),
                    newTypeArgs
                  )
                case AST.Typed.`zsName` =>
                  val argTypes: ISZ[AST.Typed] =
                    for (_ <- z"0" until numOfArgs) yield AST.Typed.z
                  val constructorType: AST.Typed.Fun =
                    AST.Typed.Fun(T, F, argTypes, AST.Typed.Name(AST.Typed.msName, ISZ(AST.Typed.z, AST.Typed.z)))
                  return (
                    Some(
                      AST.Typed.Method(
                        T,
                        AST.MethodMode.Constructor,
                        ISZ(),
                        AST.Typed.sireumName,
                        "IS",
                        ISZ(),
                        constructorType
                      )
                    ),
                    Some(
                      AST.ResolvedInfo.Method(
                        T,
                        AST.MethodMode.Constructor,
                        ISZ(),
                        AST.Typed.sireumName,
                        "IS",
                        ISZ(),
                        Some(constructorType),
                        ISZ(),
                        ISZ()
                      )
                    ),
                    newTypeArgs
                  )
                case _ =>
                  typeHierarchy.typeMap.get(tpe.name) match {
                    case Some(info) =>
                      info match {
                        case info: TypeInfo.SubZ =>
                          val t = AST.Typed.Name(tpe.name, ISZ())
                          val constructorType: AST.Typed.Fun =
                            AST.Typed.Fun(T, F, ISZ(AST.Typed.string), AST.Typed.Name(AST.Typed.optionName, ISZ(t)))
                          return (
                            Some(
                              AST.Typed.Method(
                                T,
                                AST.MethodMode.Constructor,
                                ISZ(),
                                info.owner,
                                info.ast.id.value,
                                ISZ(),
                                constructorType
                              )
                            ),
                            Some(
                              AST.ResolvedInfo.Method(
                                T,
                                AST.MethodMode.Constructor,
                                ISZ(),
                                info.owner,
                                info.ast.id.value,
                                ISZ(),
                                Some(constructorType),
                                ISZ(),
                                ISZ()
                              )
                            ),
                            newTypeArgs
                          )

                        case info: TypeInfo.Adt if !info.ast.isRoot =>
                          info.constructorTypeOpt match {
                            case Some(constructorType) =>
                              return (Some(constructorType), info.constructorResOpt, newTypeArgs)
                            case _ =>
                              reporter.error(
                                posOpt,
                                typeCheckerKind,
                                st"Cannot create an object of type ${(tpe.name, ".")}.".render
                              )
                              return (None(), None(), newTypeArgs)
                          }
                        case _ =>
                      }
                    case _ =>
                  }
              }
          }
        case tpe: AST.Typed.Name =>
          tpe.ids match {
            case AST.Typed.`isName` if tpe.args.size == z"2" =>
              if (numOfArgs == z"1") {
                val indexType = tpe.args(0)
                val valueType = tpe.args(1)
                val tupleVars = AST.Typed.Tuple(ISZ(indexType, valueType))
                val storeType = AST.Typed.Fun(T, F, ISZ(tupleVars), tpe)
                val selectType = AST.Typed.Fun(T, F, ISZ(indexType), valueType)
                return (
                  Some(
                    AST.Typed.Methods(
                      ISZ(
                        AST.Typed
                          .Method(F, AST.MethodMode.Select, ISZ(), AST.Typed.sireumName, "IS", ISZ(), selectType),
                        AST.Typed.Method(F, AST.MethodMode.Store, ISZ(), AST.Typed.sireumName, "IS", ISZ(), storeType)
                      )
                    )
                  ),
                  Some(
                    AST.ResolvedInfo.Methods(
                      ISZ(
                        AST.ResolvedInfo
                          .Method(F, AST.MethodMode.Select, ISZ(), AST.Typed.sireumName, "IS", ISZ(), Some(selectType), ISZ(), ISZ()),
                        AST.ResolvedInfo
                          .Method(F, AST.MethodMode.Store, ISZ(), AST.Typed.sireumName, "IS", ISZ(), Some(storeType), ISZ(), ISZ())
                      )
                    )
                  ),
                  newTypeArgs
                )
              } else {
                val indexType = tpe.args(0)
                val valueType = tpe.args(1)
                val tupleVars = AST.Typed.Tuple(ISZ(indexType, valueType))
                val argTypes: ISZ[AST.Typed] = for (_ <- z"0" until numOfArgs)
                  yield tupleVars
                val storeType = AST.Typed.Fun(T, F, argTypes, tpe)
                return (
                  Some(AST.Typed.Method(F, AST.MethodMode.Store, ISZ(), AST.Typed.sireumName, "IS", ISZ(), storeType)),
                  Some(
                    AST.ResolvedInfo
                      .Method(F, AST.MethodMode.Store, ISZ(), AST.Typed.sireumName, "IS", ISZ(), Some(storeType), ISZ(), ISZ())
                  ),
                  newTypeArgs
                )
              }
            case AST.Typed.`msName` if tpe.args.size == z"2" =>
              if (numOfArgs == z"1") {
                val indexType = tpe.args(0)
                val valueType = tpe.args(1)
                val tupleVars = AST.Typed.Tuple(ISZ(indexType, valueType))
                val storeType = AST.Typed.Fun(T, F, ISZ(tupleVars), tpe)
                val selectType = AST.Typed.Fun(T, F, ISZ(indexType), valueType)
                return (
                  Some(
                    AST.Typed.Methods(
                      ISZ(
                        AST.Typed
                          .Method(F, AST.MethodMode.Select, ISZ(), AST.Typed.sireumName, "MS", ISZ(), selectType),
                        AST.Typed.Method(F, AST.MethodMode.Store, ISZ(), AST.Typed.sireumName, "MS", ISZ(), storeType)
                      )
                    )
                  ),
                  Some(
                    AST.ResolvedInfo.Methods(
                      ISZ(
                        AST.ResolvedInfo
                          .Method(F, AST.MethodMode.Select, ISZ(), AST.Typed.sireumName, "MS", ISZ(), Some(selectType), ISZ(), ISZ()),
                        AST.ResolvedInfo
                          .Method(F, AST.MethodMode.Store, ISZ(), AST.Typed.sireumName, "MS", ISZ(), Some(storeType), ISZ(), ISZ())
                      )
                    )
                  ),
                  newTypeArgs
                )
              } else {
                val indexType = tpe.args(0)
                val valueType = tpe.args(1)
                val tupleVars = AST.Typed.Tuple(ISZ(indexType, valueType))
                val argTypes: ISZ[AST.Typed] = for (_ <- z"0" until numOfArgs)
                  yield tupleVars
                val storeType = AST.Typed.Fun(T, F, argTypes, tpe)
                return (
                  Some(AST.Typed.Method(F, AST.MethodMode.Store, ISZ(), AST.Typed.sireumName, "MS", ISZ(), storeType)),
                  Some(
                    AST.ResolvedInfo
                      .Method(F, AST.MethodMode.Store, ISZ(), AST.Typed.sireumName, "MS", ISZ(), Some(storeType), ISZ(), ISZ())
                  ),
                  newTypeArgs
                )
              }
            case _ =>
              typeHierarchy.typeMap.get(tpe.ids) match {
                case Some(info: TypeInfo.Adt) if !info.ast.isRoot =>
                  val params: ISZ[AST.AdtParam] = if (argNames.isEmpty) {
                    info.ast.params
                  } else {
                    val argNameSet = HashSSet.emptyInit[String](argNames.size) ++ argNames
                    val ps = info.ast.params.filter(p => argNameSet.contains(p.id.value))
                    if (argNameSet.size != ps.size) {
                      val names = argNameSet -- info.ast.params.map[String](p => p.id.value)
                      reporter.error(
                        posOpt,
                        typeCheckerKind,
                        st"Could not find parameter(s) '${(names.elements, "', '")}' in '$tpe'.".render
                      )
                      return (None(), resOpt, newTypeArgs)
                    } else if (ps.size == z"0" && info.ast.params.size != z"0") {
                      reporter
                        .error(posOpt, typeCheckerKind, st"Cannot perform copy of '$tpe' without argument.".render)
                      return (None(), resOpt, newTypeArgs)
                    }
                    ps
                  }
                  val paramNames = params.map[String](p => p.id.value)
                  val paramTypes = params.map[AST.Typed](p => p.tipe.typedOpt.get)
                  val smOpt = unify(typeHierarchy, posOpt, TypeRelation.Equal, tpe, info.tpe, reporter)
                  smOpt match {
                    case Some(sm) =>
                      val copyType = AST.Typed.Fun(T, F, paramTypes, tpe).subst(sm)
                      val id = info.ast.id.value
                      return (
                        Some(AST.Typed.Method(F, AST.MethodMode.Copy, ISZ(), info.owner, id, paramNames, copyType)),
                        Some(
                          AST.ResolvedInfo
                            .Method(F, AST.MethodMode.Copy, ISZ(), info.owner, id, paramNames, Some(copyType), ISZ(), ISZ())
                        ),
                        newTypeArgs
                      )
                    case _ => return (None(), resOpt, newTypeArgs)
                  }
                case _ =>
              }
          }
        case _ =>
      }
      return (Some(tpe), resOpt, newTypeArgs)
    }

    def checkInvokeGenH(
      m: AST.Typed.Method,
      expId: AST.Id,
      expArgs: ISZ[AST.Exp],
      typeArgs: ISZ[AST.Typed],
      rep: Reporter,
      make: (ISZ[AST.Exp], Option[AST.Typed], AST.Typed.Fun) => AST.Exp @pure
    ): (AST.Exp, Option[AST.Typed]) = {

      @pure def partResult: (AST.Exp, Option[AST.Typed]) = {
        return (make(expArgs, None(), AST.Typed.Fun(F, F, ISZ(), AST.Typed.nothing)), None())
      }

      def checkH(sm: HashMap[String, AST.Typed]): (AST.Exp, Option[AST.Typed]) = {
        val funType = m.tpe.subst(sm)
        var i = 0
        val size = expArgs.size
        if (m.paramNames.isEmpty) {
          var args = ISZ[AST.Exp]()
          while (i < size) {
            val (newArg, _) =
              checkExp(Some(funType.args(i)), scope, expArgs(i), rep)
            args = args :+ newArg
            i = i + 1
          }
          return (make(args, Some(funType.ret), funType), Some(funType.ret))
        } else {
          var args = Map.empty[String, AST.Exp]
          while (i < size) {
            val (newArg, _) =
              checkExp(Some(funType.args(i)), scope, expArgs(i), rep)
            args = args + m.paramNames(i) ~> newArg
            i = i + 1
          }
          val tOpt = Some(funType.ret)
          return (make(args.values, tOpt, funType), tOpt)
        }
      }

      if (typeArgs.isEmpty && m.typeParams.nonEmpty) {
        val repExpected = Reporter.create
        def tryExpected(): (AST.Exp, Option[AST.Typed]) = {
          expectedOpt match {
            case Some(expected) =>
              val smOpt =
                unify(typeHierarchy, expId.attr.posOpt, TypeRelation.Subtype, expected, m.tpe.ret, repExpected)
              smOpt match {
                case Some(sm) =>
                  val ok = checkUnboundTypeVar(expId.attr.posOpt, m, sm, m.typeParams, repExpected)
                  if (!ok) {
                    return partResult
                  }
                  val r = checkH(sm)
                  return r
                case _ => return partResult
              }
            case _ => return partResult
          }
        }
        val rExpected = tryExpected()
        if (rExpected._2.nonEmpty) {
          return rExpected
        }
        val repArgs = Reporter.create
        def tryArgs(): (AST.Exp, Option[AST.Typed]) = {
          var newArgs = ISZ[AST.Exp]()
          var argTypes = ISZ[AST.Typed]()
          for (e <- expArgs) {
            val (newArg, argTypeOpt) = checkExp(None(), scope, e, repArgs)
            newArgs = newArgs :+ newArg
            argTypeOpt match {
              case Some(argType) => argTypes = argTypes :+ argType
              case _ =>
            }
          }

          val smOpt = unifies(typeHierarchy, expId.attr.posOpt, TypeRelation.Supertype, argTypes, m.tpe.args, repArgs)
          smOpt match {
            case Some(sm) =>
              val ok = checkUnboundTypeVar(expId.attr.posOpt, m, sm, m.typeParams, repArgs)
              if (!ok) {
                return (make(newArgs, None(), AST.Typed.Fun(F, F, ISZ(), AST.Typed.nothing)), None())
              }
              val funType = m.tpe.subst(sm)
              return (make(newArgs, Some(funType.ret), funType), Some(funType.ret))
            case _ =>
              return (make(newArgs, None(), AST.Typed.Fun(F, F, ISZ(), AST.Typed.nothing)), None())
          }
        }
        val rArgs = tryArgs()
        if (rArgs._2.nonEmpty) {
          return rArgs
        }
        if (repArgs.hasIssue) {
          rep.reports(repArgs.messages)
        } else {
          rep.reports(repExpected.messages)
        }
        return rArgs
      } else {
        val smOpt = buildMethodSubstMap(m, expId.attr.posOpt, typeArgs, rep)
        smOpt match {
          case Some(sm) =>
            val ok = checkUnboundTypeVar(expId.attr.posOpt, m, sm, m.typeParams, rep)
            if (!ok) {
              return partResult
            }
            val r = checkH(sm)
            return r
          case _ => return partResult
        }
      }
    }

    def checkInvokeArgs(args: ISZ[AST.Exp], accesses: ISZ[AST.ResolvedInfo]): Unit = {
      def varAccess(isId: B, resOpt: Option[AST.ResolvedInfo]): Option[ISZ[String]] = {
        resOpt match {
          case Some(res: AST.ResolvedInfo.Var) =>
            return if (res.isInObject) Some(res.owner :+ res.id) else if (isId) Some(ISZ("this", res.id)) else None()
          case Some(res: AST.ResolvedInfo.LocalVar) =>
            return if (res.scope == AST.ResolvedInfo.LocalVar.Scope.Closure) Some(res.context :+ res.id) else Some(ISZ(res.id))
          case _ => return None()
        }
      }
      def mVarAccess(thisAccessOpt: Option[ISZ[String]], resOpt: Option[AST.ResolvedInfo]): Option[ISZ[String]] = {
        resOpt match {
          case Some(res: AST.ResolvedInfo.Var) =>
            return if (res.isInObject) Some(res.owner :+ res.id) else Some(thisAccessOpt.get :+ res.id)
          case Some(res: AST.ResolvedInfo.LocalVar) =>
            return if (res.scope == AST.ResolvedInfo.LocalVar.Scope.Closure) Some(res.context :+ res.id) else Some(ISZ(res.id))
          case _ => return None()
        }
      }
      def expPath(e: AST.Exp, acc: ISZ[String]): Option[ISZ[String]] = {
        e match {
          case e: AST.Exp.Ident =>
            varAccess(T, e.attr.resOpt) match {
              case Some(name) => return Some(name ++ acc)
              case _ => return None()
            }
          case e: AST.Exp.Select =>
            varAccess(F, e.attr.resOpt) match {
              case Some(name) => return Some(name ++ acc)
              case _ =>
                e.receiverOpt match {
                  case Some(receiver) => return expPath(receiver, e.id.value +: acc)
                  case _ => halt(s"Infeasible: $e")
                }
            }
          case e: AST.Exp.Invoke =>
            e.attr.resOpt.get match {
              case res: AST.ResolvedInfo.Method if res.mode == AST.MethodMode.Select =>
                varAccess(e.receiverOpt.isEmpty, e.ident.attr.resOpt) match {
                  case Some(name) => return Some((name :+ "@") ++ acc)
                  case _ =>
                    e.receiverOpt match {
                      case Some(receiver) =>
                        return if (e.ident.id.value == "apply") expPath(receiver, "@" +: acc)
                        else expPath(receiver, e.ident.id.value +: "@" +: acc)
                      case _ =>
                        halt(s"Infeasible: $e")
                    }
                }
              case _ => return None()
            }
          case _ => return None()
        }
      }
      var argPaths = ISZ[Option[ISZ[String]]]()
      for (arg <- args) {
        arg.typedOpt match {
          case Some(t) if typeHierarchy.isMutable(t, T) => argPaths = argPaths :+ expPath(arg, ISZ())
          case _ => argPaths = argPaths :+ None()
        }
      }
      for (access <- accesses) {
        argPaths = argPaths :+ mVarAccess(if (args.nonEmpty) expPath(args(0), ISZ()) else Some(ISZ("this")), Some(access))
      }
      for (i <- 0 until args.size) {
        for (j <- i + 1 until args.size) {
          (argPaths(i), argPaths(j)) match {
            case (Some(path1), Some(path2)) =>
              val size: Z = if (path1.size > path2.size) path2.size else path1.size
              if (ops.ISZOps(path1).slice(0, size) == ops.ISZOps(path2).slice(0, size)) {
                if (i < args.size) {
                  val pos = args(i).posOpt.get
                  reporter.error(args(j).posOpt, typeCheckerKind,
                    s"Cannot pass a mutable object as an argument with a similar access path with the expression at [${pos.beginLine}, ${pos.beginColumn}]")
                } else {
                  reporter.error(args(j).posOpt, typeCheckerKind,
                    s"Cannot pass a mutable object as an argument with a similar access path to the method reads/modifies variables of mutable type")
                }
              }
            case (_, _) =>
          }
        }
      }
    }

    def checkInvoke(invokeExp: AST.Exp.Invoke): (AST.Exp, Option[AST.Typed]) = {

      def checkInvokeH(
        tOpt: Option[AST.Typed],
        rOpt: Option[AST.ResolvedInfo],
        receiverOpt: Option[AST.Exp],
        ident: AST.Exp.Ident,
        targs: ISZ[AST.Type],
        typeArguments: ISZ[AST.Typed]
      ): (AST.Exp, Option[AST.Typed]) = {

        @pure def partResultH: (AST.Exp, Option[AST.Typed]) = {
          return (invokeExp(targs = targs, receiverOpt = receiverOpt), None())
        }

        val (t, resOpt, typeArgs): (AST.Typed, Option[AST.ResolvedInfo], ISZ[AST.Typed]) = tOpt match {
          case Some(tpe) =>
            val (t2Opt, newResOpt, targs) =
              checkInvokeType(invokeExp.ident.attr.posOpt, rOpt, tpe, typeArguments, invokeExp.args.size, ISZ())
            t2Opt match {
              case Some(t2) => (t2, newResOpt, targs)
              case _ => return partResultH
            }
          case _ => return partResultH
        }

        def checkInvokeMethod(
          m: AST.Typed.Method,
          mResOpt: Option[AST.ResolvedInfo],
          rep: Reporter
        ): (AST.Exp, Option[AST.Typed]) = {
          if (m.tpe.isByName) {
            reporter.error(invokeExp.ident.attr.posOpt, typeCheckerKind, s"$m does not accept any argument.")
            return partResultH
          } else if (m.tpe.args.size != invokeExp.args.size) {
            reporter.error(
              invokeExp.ident.attr.posOpt,
              typeCheckerKind,
              s"$m is expecting ${m.tpe.args.size} argument(s), but ${invokeExp.args.size} found."
            )
            return partResultH
          }

          @pure def make(eArgs: ISZ[AST.Exp], tpeOpt: Option[AST.Typed], funType: AST.Typed.Fun): AST.Exp = {
            val ro: Option[AST.ResolvedInfo] = mResOpt.get match {
              case r: AST.ResolvedInfo.Method =>
                if (strictAliasing && (r.mode == AST.MethodMode.Method || r.mode == AST.MethodMode.Ext)) {
                  checkInvokeArgs(
                    receiverOpt.map((rcv: AST.Exp) => ISZ(rcv)).getOrElseEager(ISZ()) ++ eArgs,
                    (HashSSet ++ r.reads ++ r.writes).elements
                  )
                }
                if (funType == r.tpeOpt.get) Some(r) else Some(r(tpeOpt = Some(funType)))
              case _: AST.ResolvedInfo.BuiltIn => mResOpt
              case _ => halt(s"Unexpected symbol info: ${resOpt.get}")
            }

            return invokeExp(
              receiverOpt = receiverOpt,
              ident = ident,
              targs = targs,
              args = eArgs,
              attr = invokeExp.attr(typedOpt = tpeOpt, resOpt = ro)
            )
          }

          val r = checkInvokeGenH(m, invokeExp.ident.id, invokeExp.args, typeArgs, rep, make _)
          return r
        }

        t match {
          case m: AST.Typed.Methods =>
            val res: AST.ResolvedInfo.Methods = resOpt match {
              case Some(r: AST.ResolvedInfo.Methods) if r.methods.size == m.methods.size =>
                r
              case _ =>
                halt(s"Unexpected situation when invoking IS/MS store/select.")
            }
            var messages = ISZ[Message]()
            var found = F
            var i = 0
            val size = m.methods.size
            var r = partResultH
            while (!found && i < size) {
              val rep = Reporter.create
              r = checkInvokeMethod(m.methods(i), Some(res.methods(i)), rep)
              if (!rep.hasIssue) {
                found = T
              } else {
                messages = messages ++ rep.messages
              }
              i = i + 1
            }
            if (found) {
              return r
            } else {
              reporter.reports(messages)
              return partResultH
            }
          case m: AST.Typed.Method =>
            val r = checkInvokeMethod(m, resOpt, reporter); return r
          case t: AST.Typed.Fun =>
            if (invokeExp.targs.nonEmpty) {
              reporter.error(
                invokeExp.ident.attr.posOpt,
                typeCheckerKind,
                s"Cannot supply type arguments when applying a function."
              )
              return partResultH
            }
            val size = invokeExp.args.size
            if (t.args.size != size) {
              reporter.error(
                invokeExp.ident.attr.posOpt,
                typeCheckerKind,
                s"Function '$t' is expecting ${t.args.size} arguments, but $size found."
              )
              return partResultH
            }
            var i = 0
            var newArgs = ISZ[AST.Exp]()
            while (i < size) {
              val (newArg, _) =
                checkExp(Some(t.args(i)), scope, invokeExp.args(i), reporter)
              newArgs = newArgs :+ newArg
              i = i + 1
            }
            return (
              invokeExp(
                ident = ident,
                args = newArgs,
                receiverOpt = receiverOpt,
                attr = invokeExp.attr(resOpt = resOpt, typedOpt = Some(t))
              ),
              Some(t.ret)
            )
          case _ =>
            reporter.error(invokeExp.ident.attr.posOpt, typeCheckerKind, s"Cannot invoke on '$t'.")
            return partResultH
        }
      }

      val (typeArgs, newTargs): (ISZ[AST.Typed], ISZ[AST.Type]) = {
        val pOpt = checkTypeArgs(invokeExp.targs)
        if (pOpt.nonEmpty) {
          pOpt.get
        } else {
          return (invokeExp, None())
        }
      }

      invokeExp.receiverOpt match {
        case Some(receiver) =>
          val (newReceiver, receiverTypeOpt) = checkExp(None(), scope, receiver, reporter)
          val receiverType: AST.Typed = receiverTypeOpt match {
            case Some(t) => t
            case _ =>
              return (invokeExp(targs = newTargs, receiverOpt = Some(newReceiver)), None())
          }
          val (typedOpt, resOpt, tArgs2) =
            checkSelectH(receiverType, invokeExp.ident.id, typeArgs, reporter)
          val r = checkInvokeH(
            typedOpt,
            resOpt,
            Some(newReceiver),
            invokeExp.ident(
              attr = invokeExp.ident.attr(posOpt = invokeExp.ident.id.attr.posOpt, resOpt = resOpt, typedOpt = typedOpt)
            ),
            newTargs,
            tArgs2
          )
          return r
        case _ =>
          val (typedOpt, resOpt) = checkId(scope, invokeExp.ident.id, reporter)
          val r = checkInvokeH(
            typedOpt,
            resOpt,
            None(),
            invokeExp.ident(
              attr = invokeExp.ident.attr(posOpt = invokeExp.ident.id.attr.posOpt, resOpt = resOpt, typedOpt = typedOpt)
            ),
            newTargs,
            typeArgs
          )
          return r
      }
    }

    def checkInvokeNamed(invokeExp: AST.Exp.InvokeNamed): (AST.Exp, Option[AST.Typed]) = {

      def checkInvokeNamedH(
        tOpt: Option[AST.Typed],
        rOpt: Option[AST.ResolvedInfo],
        receiverOpt: Option[AST.Exp],
        ident: AST.Exp.Ident,
        targs: ISZ[AST.Type],
        typeArguments: ISZ[AST.Typed]
      ): (AST.Exp, Option[AST.Typed]) = {

        @pure def partResultNamedH: (AST.Exp, Option[AST.Typed]) = {
          return (invokeExp(targs = targs, receiverOpt = receiverOpt), None())
        }

        val (t, resOpt, typeArgs): (AST.Typed, Option[AST.ResolvedInfo], ISZ[AST.Typed]) = tOpt match {
          case Some(tpe) =>
            val (t2Opt, newResOpt, targs) =
              checkInvokeType(
                invokeExp.ident.attr.posOpt,
                rOpt,
                tpe,
                typeArguments,
                invokeExp.args.size,
                invokeExp.args.map(na => na.id.value)
              )
            t2Opt match {
              case Some(t2) => (t2, newResOpt, targs)
              case _ => return partResultNamedH
            }
          case _ => return partResultNamedH
        }

        t match {
          case m: AST.Typed.Method =>
            if (m.tpe.args.size != invokeExp.args.size) {
              reporter.error(
                invokeExp.ident.attr.posOpt,
                typeCheckerKind,
                s"$m is expecting ${m.tpe.args.size} arguments, but ${invokeExp.args.size} found."
              )
              return partResultNamedH
            }

            val nameToIndexMap: HashMap[String, Z] = {
              var r = HashMap.emptyInit[String, Z](m.paramNames.size)
              var i = 0
              for (name <- m.paramNames) {
                r = r + name ~> i
                i = i + 1
              }
              r
            }

            val expArgs: ISZ[AST.Exp] = {
              val size = invokeExp.args.size
              val r = MSZ.create[AST.Exp](size, invokeExp)
              var ok = T
              var defined = HashSet.emptyInit[String](size)
              for (na <- invokeExp.args) {
                val name = na.id.value
                if (defined.contains(name)) {
                  ok = F
                  reporter.error(
                    na.id.attr.posOpt,
                    typeCheckerKind,
                    s"An argument for parameter '$name' has previously been supplied."
                  )
                }
                defined = defined + name
                nameToIndexMap.get(name) match {
                  case Some(n) => r(n) = na.arg
                  case _ =>
                    ok = F
                    reporter.error(na.id.attr.posOpt, typeCheckerKind, s"Could not find parameter '$name' in '$m'.")
                }
                if (!ok) {
                  return partResultNamedH
                }
              }
              r.toIS
            }

            @pure def makeNamed(eArgs: ISZ[AST.Exp], tpeOpt: Option[AST.Typed], funType: AST.Typed.Fun): AST.Exp = {
              val args: ISZ[AST.NamedArg] =
                if (eArgs.size == expArgs.size) {
                  var r = ISZ[AST.NamedArg]()
                  for (na <- invokeExp.args) {
                    val name = na.id.value
                    val index = nameToIndexMap.get(name).get
                    r = r :+ na(arg = eArgs(index), index = index)
                  }
                  r
                } else {
                  invokeExp.args
                }
              val ro: Option[AST.ResolvedInfo] = resOpt.get match {
                case r: AST.ResolvedInfo.Method =>
                  if (strictAliasing && (r.mode == AST.MethodMode.Method || r.mode == AST.MethodMode.Ext)) {
                    checkInvokeArgs(receiverOpt.map((rcv: AST.Exp) => ISZ(rcv)).getOrElseEager(ISZ()) ++
                      (for (arg <- args) yield arg.arg), (HashSSet ++ r.reads ++ r.writes).elements)
                  }
                  if (r.tpeOpt.get == funType) Some(r) else Some(r(tpeOpt = Some(funType)))
                case _: AST.ResolvedInfo.BuiltIn => resOpt
                case _ => halt(s"Unexpected symbol info: ${resOpt.get}")
              }
              return invokeExp(
                receiverOpt = receiverOpt,
                ident = ident,
                targs = targs,
                args = args,
                attr = invokeExp.attr(typedOpt = tpeOpt, resOpt = ro)
              )
            }

            val r =
              checkInvokeGenH(m, invokeExp.ident.id, expArgs, typeArgs, reporter, makeNamed _)
            return r
          case _: AST.Typed.Fun =>
            reporter.error(
              invokeExp.ident.attr.posOpt,
              typeCheckerKind,
              s"Cannot supply named arguments when applying a function."
            )
            return partResultNamedH
          case _ =>
            reporter.error(invokeExp.ident.attr.posOpt, typeCheckerKind, s"Cannot invoke with named arguments on '$t'.")
            return partResultNamedH
        }
      }

      val (typeArgs, newTargs): (ISZ[AST.Typed], ISZ[AST.Type]) = {
        val pOpt = checkTypeArgs(invokeExp.targs)
        if (pOpt.nonEmpty) {
          pOpt.get
        } else {
          return (invokeExp, None())
        }
      }

      invokeExp.receiverOpt match {
        case Some(receiver) =>
          val (newReceiver, receiverTypeOpt) =
            checkExp(None(), scope, receiver, reporter)
          val receiverType: AST.Typed = receiverTypeOpt match {
            case Some(t) => t
            case _ =>
              return (invokeExp(targs = newTargs, receiverOpt = Some(newReceiver)), None())
          }
          val (typedOpt, resOpt, tArgs) =
            checkSelectH(receiverType, invokeExp.ident.id, typeArgs, reporter)
          val r = checkInvokeNamedH(
            typedOpt,
            resOpt,
            Some(newReceiver),
            invokeExp.ident(
              attr = invokeExp.ident.attr(posOpt = invokeExp.ident.id.attr.posOpt, resOpt = resOpt, typedOpt = typedOpt)
            ),
            newTargs,
            tArgs
          )
          return r
        case _ =>
          val (typedOpt, resOpt) = checkId(scope, invokeExp.ident.id, reporter)
          val r = checkInvokeNamedH(
            typedOpt,
            resOpt,
            None(),
            invokeExp.ident(
              attr = invokeExp.ident.attr(posOpt = invokeExp.ident.id.attr.posOpt, resOpt = resOpt, typedOpt = typedOpt)
            ),
            newTargs,
            typeArgs
          )
          return r
      }
    }

    def checkIfExp(ifExp: AST.Exp.If): (AST.Exp, Option[AST.Typed]) = {
      val (newCond, _) = checkExp(AST.Typed.bOpt, scope, ifExp.cond, reporter)
      expectedOpt match {
        case Some(expected) =>
          val (newThenExp, _) = checkExp(Some(expected), scope, ifExp.thenExp, reporter)
          val (newElseExp, _) = checkExp(Some(expected), scope, ifExp.elseExp, reporter)
          return (
            ifExp(
              cond = newCond,
              thenExp = newThenExp,
              elseExp = newElseExp,
              attr = ifExp.attr(typedOpt = expectedOpt)
            ),
            expectedOpt
          )
        case _ =>
          val (newThenExp, thenTypeOpt) = checkExp(expectedOpt, scope, ifExp.thenExp, reporter)
          val (newElseExp, elseTypeOpt) = checkExp(expectedOpt, scope, ifExp.elseExp, reporter)
          (thenTypeOpt, elseTypeOpt) match {
            case (Some(thenType), Some(elseType)) =>
              val tOpt = typeHierarchy.lub(ISZ(thenType, elseType))
              tOpt match {
                case Some(_) =>
                case _ =>
                  reporter.error(
                    ifExp.posOpt,
                    typeCheckerKind,
                    st"Could not find a common ancestor for: { '${(ISZ(thenType, elseType), "', '")}' }.".render
                  )
              }
              return (
                ifExp(cond = newCond, thenExp = newThenExp, elseExp = newElseExp, attr = ifExp.attr(typedOpt = tOpt)),
                tOpt
              )
            case _ => return (ifExp(cond = newCond, thenExp = newThenExp, elseExp = newElseExp), None())
          }
      }
    }

    def checkForYield(forExp: AST.Exp.ForYield): (AST.Exp, Option[AST.Typed]) = {
      val (newScopeOpt, newEnumGens, indexTypeOpt, isImm) = checkEnumGens(T, scope, forExp.enumGens, reporter)
      newScopeOpt match {
        case Some(newScope) =>
          val expectedValueOpt: Option[AST.Typed] = expectedOpt match {
            case Some(expected: AST.Typed.Name)
                if expected.ids == AST.Typed.isName || expected.ids == AST.Typed.msName =>
              Some(expected.args(1))
            case _ => None()
          }
          val (newExp, expTypeOpt) = checkExp(expectedValueOpt, newScope, forExp.exp, reporter)
          expTypeOpt match {
            case Some(t) =>
              val tOpt: Option[AST.Typed] = expectedValueOpt match {
                case Some(expectedValue) =>
                  indexTypeOpt match {
                    case Some(indexType) =>
                      Some(AST.Typed.Name(if (isImm) AST.Typed.isName else AST.Typed.msName, ISZ(indexType, expectedValue)))
                    case _ =>
                      Some(AST.Typed.Name(if (isImm) AST.Typed.jenName else AST.Typed.mjenName, ISZ(expectedValue)))
                  }
                case _ =>
                  indexTypeOpt match {
                    case Some(indexType) =>
                      Some(AST.Typed.Name(if (isImm) AST.Typed.isName else AST.Typed.msName, ISZ(indexType, t)))
                    case _ =>
                      Some(AST.Typed.Name(if (isImm) AST.Typed.jenName else AST.Typed.mjenName, ISZ(t)))
                  }
              }
              return (forExp(enumGens = newEnumGens, exp = newExp, attr = forExp.attr(typedOpt = tOpt)), tOpt)
            case _ => return (forExp(enumGens = newEnumGens, exp = newExp), None())
          }
        case _ => return (forExp(enumGens = newEnumGens), None())
      }
    }

    def checkThis(thisExp: AST.Exp.This): (AST.Exp, Option[AST.Typed]) = {
      scope.thisOpt match {
        case tOpt @ Some(_) => return (thisExp(attr = thisExp.attr(typedOpt = tOpt)), tOpt)
        case _ =>
          reporter.error(thisExp.posOpt, typeCheckerKind, "Cannot access 'this' at this location.")
          return (thisExp, None())
      }
    }

    def checkSuper(superExp: AST.Exp.Super): (AST.Exp, Option[AST.Typed]) = {
      scope.thisOpt match {
        case Some(t: AST.Typed.Name) =>
          val parents: ISZ[AST.Typed] = typeHierarchy.typeMap.get(t.ids) match {
            case Some(info: TypeInfo.Adt) => info.ast.parents.map(p => p.typedOpt.get)
            case Some(info: TypeInfo.Sig) => info.ast.parents.map(p => p.typedOpt.get)
            case _ => halt("Unexpected situation when type checking super.")
          }
          superExp.idOpt match {
            case Some(id) =>
              var i: Z = 0
              for (p <- parents) {
                p match {
                  case p: AST.Typed.Name if p.ids(p.ids.size - 1) == id.value =>
                    for (j <- i + 1 until parents.size) {
                      parents(j) match {
                        case otherP: AST.Typed.Name if otherP.ids(otherP.ids.size - 1) == id.value =>
                          reporter
                            .error(superExp.posOpt, typeCheckerKind, s"Ambiguous super type qualifier '${id.value}'.")
                          return (superExp, None())
                        case _ =>
                      }
                    }
                    val tOpt: Option[AST.Typed] = Some(p)
                    return (superExp(attr = superExp.attr(typedOpt = tOpt)), tOpt)
                  case _ =>
                }
                i = i + 1
              }
              reporter.error(superExp.posOpt, typeCheckerKind, s"Could not find super type '${id.value}'.")
              return (superExp, None())
            case _ =>
              if (parents.size != z"1") {
                reporter
                  .error(superExp.posOpt, typeCheckerKind, "Explicit super type identifier is required: super[〈ID〉].")
                return (superExp, None())
              } else {
                val tOpt: Option[AST.Typed] = Some(parents(0))
                return (superExp(attr = superExp.attr(typedOpt = tOpt)), tOpt)
              }
          }
        case _ =>
          reporter.error(superExp.posOpt, typeCheckerKind, "Cannot access 'super' at this location.")
          return (superExp, None())
      }
    }

    def checkQuant(quant: AST.Exp.Quant): (AST.Exp, Option[AST.Typed]) = {
      quant match {
        case quant: AST.Exp.QuantType =>
          if (inSpec) {
            val (newFun, ftOpt) = checkExp(None(), scope, quant.fun, reporter)
            ftOpt match {
              case Some(ft: AST.Typed.Fun) =>
                if (ft.ret != AST.Typed.b) {
                  reporter.error(quant.fun.exp.asStmt.posOpt, typeCheckerKind, s"Expecting type 'B' but '${ft.ret}' found.")
                }
              case _ =>
            }
            return (quant(fun = newFun.asInstanceOf[AST.Exp.Fun]), Some(AST.Typed.b))
          } else {
            reporter.error(quant.posOpt, typeCheckerKind, "Quantifications over types are only available in contracts.")
            return (quant, None())
          }

        case quant: AST.Exp.QuantRange =>
          val (newLo, tLoOpt) = checkExp(None(), scope, quant.lo, reporter)
          val (newHi, _) = checkExp(tLoOpt, scope, quant.hi, reporter)
          tLoOpt match {
            case Some(tLo) =>
              val (newFun, _) = checkExp(Some(AST.Typed.Fun(F, F, ISZ(tLo), AST.Typed.b)), scope, quant.fun, reporter)
              checkIndexType(quant.lo.posOpt, tLo, reporter)
              val newFunExp = newFun.asInstanceOf[AST.Exp.Fun]
              val res = AST.ResolvedInfo.LocalVar(newFunExp.context, AST.ResolvedInfo.LocalVar.Scope.Current, F, T,
                quant.fun.params(0).idOpt.get.value)
              return (
                quant(lo = newLo, hi = newHi, fun = newFunExp, attr = quant.attr(typedOpt = Some(tLo), resOpt = Some(res))),
                Some(AST.Typed.b)
              )
            case _ => return (quant(lo = newLo, hi = newHi), Some(AST.Typed.b))
          }

        case quant: AST.Exp.QuantEach =>
          val (newSeq, stOpt) = checkExp(None(), scope, quant.seq, reporter)
          stOpt match {
            case Some(st) =>
              st match {
                case st: AST.Typed.Name if st.ids == AST.Typed.isName || st.ids == AST.Typed.msName =>
                  val eType = st.args(1)
                  val (newFun, _) = checkExp(Some(AST.Typed.Fun(F, F, ISZ(eType), AST.Typed.b)), scope, quant.fun, reporter)
                  val newFunExp = newFun.asInstanceOf[AST.Exp.Fun]
                  val res = AST.ResolvedInfo.LocalVar(newFunExp.context,
                    AST.ResolvedInfo.LocalVar.Scope.Current, F, T, quant.fun.params(0).idOpt.get.value)
                  return (
                    quant(seq = newSeq, fun = newFunExp, attr = quant.attr(typedOpt = Some(eType), resOpt = Some(res))),
                    Some(AST.Typed.b)
                  )
                case st =>
                  reporter.error(quant.seq.posOpt, typeCheckerKind, s"Expecting an IS or MS, but '$st' found.")
              }
            case _ =>
          }
          return (quant(seq = newSeq), Some(AST.Typed.b))
      }
    }

    def checkResult(res: AST.Exp.Result): (AST.Exp, Option[AST.Typed]) = {
      if (mode == ModeContext.SpecPost) {
        val r = scope.returnOpt
        r match {
          case Some(t) =>
            res.tipeOpt match {
              case Some(tipe) =>
                typeHierarchy.typed(scope, tipe, reporter) match {
                  case Some(newTipe) if newTipe.typedOpt.nonEmpty && t != newTipe.typedOpt.get =>
                    reporter.error(tipe.posOpt, typeCheckerKind, s"Expecting type '$t', but '${newTipe.typedOpt.get}' found.")
                  case newTipeOpt =>
                    return (res(tipeOpt = newTipeOpt, attr = res.attr(typedOpt = r)), r)
                }
              case _ =>
            }
            return (res(attr = res.attr(typedOpt = r)), r)
          case _ =>
            reporter.error(res.posOpt, typeCheckerKind, "Expected a return type for Res, but none found.")
        }
      } else {
        reporter.error(res.posOpt, typeCheckerKind, "Res can only be used inside Ensures (post-condition).")
      }
      return (res, None())
    }

    def checkLoopIndex(idx: AST.Exp.LoopIndex): (AST.Exp, Option[AST.Typed]) = {
      if (inSpec) {
        val id = idx.exp.id.value
        scope.resolveIndex(id) match {
          case Some(indexType) =>
            val newExp = checkExp(None(), scope, idx.exp, reporter)._1.asInstanceOf[AST.Exp.Ident]
            idx.tipeOpt match {
              case Some(tipe) =>
                val nto = typeHierarchy.typed(scope, tipe, reporter)
                nto match {
                  case Some(newTipe) =>
                    if (newTipe.typedOpt.nonEmpty && indexType == newTipe.typedOpt.get) {
                      val tOpt = newTipe.typedOpt
                      return (idx(tipeOpt = nto, exp = newExp, attr = idx.attr(typedOpt = tOpt)), tOpt)
                    } else {
                      reporter.error(tipe.posOpt, typeCheckerKind, s"Expecting type '$indexType', but '${newTipe.typedOpt.get}' found.")
                    }
                  case _ =>
                }
                return (idx(exp = newExp), None())
              case _ => return (idx(exp = newExp), idx.typedOpt)
            }
          case _ =>
            reporter.error(idx.posOpt, typeCheckerKind, s"Could not resolve 'Idx($id)'.")
        }
      } else {
        reporter.error(idx.posOpt, typeCheckerKind, "Idx can only be used inside specification context.")
      }
      return (idx, None())
    }

    def checkExpH(): (AST.Exp, Option[AST.Typed]) = {
      exp match {

        case exp: AST.Exp.Binary => return checkBinary(exp)

        case exp: AST.Exp.Eta =>
          exp.ref match {
            case ref: AST.Exp.Ident => return checkIdent(ref, Some(exp))
            case ref: AST.Exp.Select => return checkSelect(ref, Some(exp))
          }

        case exp: AST.Exp.ForYield => return checkForYield(exp)

        case exp: AST.Exp.Fun =>
          val pos: Position = exp.posOpt match {
            case Some(p) => p
            case _ =>
              reporter.error(
                exp.posOpt,
                typeCheckerKind,
                "Could not generate lambda expression name due to lack of positional information."
              )
              return (exp, None())
          }
          val (newExp, expTypedOpt, _) = TypeChecker(typeHierarchy, context :+ s"$$${pos.beginLine}_${pos.beginColumn}",
            isInMutableContext, mode, strictAliasing).checkFun(expectedOpt, scope, exp, reporter)
          return (newExp, expTypedOpt)

        case exp: AST.Exp.Ident => return checkIdent(exp, None())

        case exp: AST.Exp.If => return checkIfExp(exp)

        case exp: AST.Exp.Invoke =>
          exp match {
            case exp @ AST.Exp.Invoke(None(), AST.Exp.Ident(AST.Id(name)), targs, args)
                if targs.isEmpty && builtInMethods.contains(name) =>
              val (kind, resOpt): (BuiltInKind.Type, Option[AST.ResolvedInfo]) =
                name.native match {
                  case "assert" => (BuiltInKind.Assertume, if (args.size == z"1") assertResOpt else assertMsgResOpt)
                  case "assume" => (BuiltInKind.Assertume, if (args.size == z"1") assumeResOpt else assumeMsgResOpt)
                  case "println" => (BuiltInKind.Print, printlnResOpt)
                  case "print" => (BuiltInKind.Print, printResOpt)
                  case "cprintln" => (BuiltInKind.Print, cprintlnResOpt)
                  case "cprint" => (BuiltInKind.Print, cprintResOpt)
                  case "eprintln" => (BuiltInKind.Print, eprintlnResOpt)
                  case "eprint" => (BuiltInKind.Print, eprintResOpt)
                  case "halt" => (BuiltInKind.Halt, haltResOpt)
                  case _ => halt(s"Unimplemented built-in method $name.")
                }
              kind match {
                case BuiltInKind.Assertume =>
                  args.size match {
                    case z"1" => return checkAssertume(resOpt, exp, args(0), None())
                    case z"2" => return checkAssertume(resOpt, exp, args(0), Some(args(1)))
                    case _ =>
                      reporter
                        .error(exp.posOpt, typeCheckerKind, s"Invalid number of arguments (${args.size}) for $name.")
                      return (exp, None())
                  }
                case BuiltInKind.Print => return checkPrint(resOpt, exp, args)
                case BuiltInKind.Halt => return checkHalt(exp, args)
              }
            case _ => return checkInvoke(exp)
          }

        case exp: AST.Exp.InvokeNamed =>
          exp match {
            case exp @ AST.Exp.InvokeNamed(None(), AST.Exp.Ident(AST.Id(name)), targs, _)
                if targs.isEmpty && builtInMethods.contains(name) =>
              reporter.error(exp.posOpt, typeCheckerKind, s"Cannot invoke '$name' with named argument(s).")
              return (exp, None())
            case _ => return checkInvokeNamed(exp)
          }

        case exp: AST.Exp.LitB => return (exp, exp.typedOpt)

        case exp: AST.Exp.LitC => return (exp, exp.typedOpt)

        case exp: AST.Exp.LitF32 => return (exp, exp.typedOpt)

        case exp: AST.Exp.LitF64 => return (exp, exp.typedOpt)

        case exp: AST.Exp.LitR => return (exp, exp.typedOpt)

        case exp: AST.Exp.LitString => return (exp, exp.typedOpt)

        case exp: AST.Exp.LitZ => return (exp, exp.typedOpt)

        case exp: AST.Exp.LitStepId =>
          if (!inSpec) {
            reporter.error(exp.posOpt, typeCheckerKind, "Can only use step name literal in proof context")
          }
          return (exp, exp.typedOpt)

        case exp: AST.Exp.Select => return checkSelect(exp, None())

        case exp: AST.Exp.StringInterpolate => return checkStringInterpolate(exp)

        case exp: AST.Exp.Super => return checkSuper(exp)

        case exp: AST.Exp.This => return checkThis(exp)

        case exp: AST.Exp.Tuple => return checkTuple(exp)

        case exp: AST.Exp.Unary => return checkUnary(exp)

        case exp: AST.Exp.Quant => return checkQuant(exp)

        case exp: AST.Exp.Input =>
          if (inSpec) {
            val (e, tOpt) = checkExp(expectedOpt, scope, exp.exp, reporter)
            return (exp(exp = e), tOpt)
          } else {
            reporter.error(exp.posOpt, typeCheckerKind, "Input can only be used inside specification context.")
            return (exp, None())
          }

        case exp: AST.Exp.OldVal =>
          if (inSpec) {
            val (e, tOpt) = checkExp(expectedOpt, scope, exp.exp, reporter)
            return (exp(exp = e), tOpt)
          } else {
            reporter.error(exp.posOpt, typeCheckerKind, "Old can only be used inside specification context.")
            return (exp, None())
          }

        case _: AST.Exp.StateSeq => halt("Unimplemented") // TODO

        case exp: AST.Exp.Result => return checkResult(exp)

        case exp: AST.Exp.LoopIndex => return checkLoopIndex(exp)
      }
    }

    val r = checkExpH()
    r._2 match {
      case Some(t) =>
        expectedOpt match {
          case Some(expected) if expected == t =>
          case Some(expected: AST.Typed.Fun) if expected.isByName =>
            if (!typeHierarchy.isSubType(t, expected.ret)) {
              reporter.error(exp.posOpt, typeCheckerKind, s"Expecting type '$expected', but '$t' found.")
              return (r._1, None())
            }
          case Some(expected) =>
            if (!typeHierarchy.isSubType(t, expected)) {
              reporter.error(exp.posOpt, typeCheckerKind, s"Expecting type '$expected', but '$t' found.")
              return (r._1, None())
            }
          case _ =>
        }
      case _ =>
    }
    return r
  }

  def checkAssignExp(
    expectedOpt: Option[AST.Typed],
    scope: Scope.Local,
    aexp: AST.AssignExp,
    reporter: Reporter
  ): (AST.AssignExp, Option[AST.Typed]) = {

    val newStmt: AST.Stmt = aexp match {
      case aexp: AST.Stmt.Expr =>
        val r = checkExpr(expectedOpt, scope, aexp, reporter)
        return (r, r.typedOpt)
      case aexp: AST.Stmt.If => checkIf(expectedOpt, scope, aexp, reporter)
      case aexp: AST.Stmt.Block => checkBlock(expectedOpt, scope, aexp, reporter)
      case aexp: AST.Stmt.Match => checkMatch(expectedOpt, scope, aexp, reporter)
      case aexp: AST.Stmt.Return => checkStmt(scope, aexp, reporter)
    }

    val newAssignExp = newStmt.asAssignExp
    if (expectedOpt.isEmpty) {
      val exprs = newAssignExp.exprs
      if (exprs.size === 1) {
        return (newAssignExp, exprs(0).exp.typedOpt)
      }
    }
    return (newAssignExp, expectedOpt)
  }

  def checkStmts(
    scopes: ISZ[Scope.Local],
    expectedOpt: Option[AST.Typed],
    stmts: ISZ[AST.Stmt],
    reporter: Reporter
  ): ISZ[AST.Stmt] = {
    var newStmts = ISZ[AST.Stmt]()
    val size = stmts.size - 1
    for (i <- z"0" until size if !reporter.hasError) {
      val newStmt = checkStmt(if (scopes.size === 1) scopes(0) else scopes(i), stmts(i), reporter)
      newStmts = newStmts :+ newStmt
    }

    if (reporter.hasError) {
      return newStmts ++ ops.ISZOps(stmts).slice(newStmts.size, stmts.size)
    }

    if (size < 0) {
      return newStmts
    }

    val stmt = stmts(size)
    val newScope: Scope.Local = if (scopes.size === 1) scopes(0) else scopes(size)

    expectedOpt match {
      case Some(_) =>
        val (r, _) = checkAssignExp(expectedOpt, newScope, stmt.asAssignExp, reporter)
        return newStmts :+ r.asStmt
      case _ =>
        val newStmt = checkStmt(newScope, stmt, reporter)
        return newStmts :+ newStmt
    }

    return newStmts
  }

  def checkStmtOpts(
    scope: Scope.Local,
    stmtOpts: ISZ[Option[AST.Stmt]],
    reporter: Reporter
  ): ISZ[Option[AST.Stmt]] = {
    var newScope = scope
    var newStmtOpts = ISZ[Option[AST.Stmt]]()
    for (i <- z"0" until stmtOpts.size) {
      stmtOpts(i) match {
        case Some(stmt) =>
          val newStmt = checkStmt(newScope, stmt, reporter)
          newStmtOpts = newStmtOpts :+ Some(newStmt)
        case _ => newStmtOpts = newStmtOpts :+ None()
      }
    }

    return newStmtOpts
  }

  def checkImport(scope: Scope.Local, stmt: AST.Stmt.Import, reporter: Reporter): (Option[Scope.Local], AST.Stmt) = {
    @pure def addImport(s: Scope.Local): Scope.Local = {
      s.outerOpt match {
        case Some(outer: Scope.Local) => s(outerOpt = Some(addImport(outer)))
        case Some(outer: Scope.Global) =>
          s(outerOpt = Some(outer(imports = outer.imports :+ stmt)))
        case _ => halt("Unexpected local scope without an outer scope.")
      }
    }

    Resolver.checkImport(scope.packageName, stmt, typeHierarchy.nameMap, typeHierarchy.typeMap, reporter)

    return (Some(addImport(scope)), stmt)
  }

  def checkBody(isWorksheet: B, expectedOpt: Option[AST.Typed], sc: Scope.Local, body: AST.Body,
                reporter: Reporter): (Scope.Local, AST.Body) = {
    val to = TypeOutliner(typeHierarchy)
    var ok = T
    var scope = sc
    def checkLocalId(id: String, posOpt: Option[Position]): Unit = {
      scope.nameMap.get(id) match {
        case Some(_) =>
          reporter.error(posOpt, typeCheckerKind, s"Cannot redeclare '$id'.")
          ok = F
        case _ =>
      }
      scope.resolveName(typeHierarchy.nameMap, ISZ(id)) match {
        case Some(info) if !info.isInstanceOf[Info.Package] =>
          reporter.error(
            posOpt,
            typeCheckerKind,
            s"Cannot declare method '$id' as it has been previously declared in the enclosing context."
          )
          ok = F
        case _ =>
      }
      scope.thisOpt match {
        case Some(thisType: AST.Typed.Name) =>
          val hasMethod: B = typeHierarchy.typeMap.get(thisType.ids) match {
            case Some(info: TypeInfo.Sig) => info.hasId(id)
            case Some(info: TypeInfo.Adt) => info.hasId(id)
            case _ => F
          }
          if (hasMethod) {
            reporter.error(
              posOpt,
              typeCheckerKind,
              s"Cannot declare method '$id' as it has been previously declared in the enclosing context."
            )
            ok = F
          }
        case _ =>
      }
    }
    var stmts = ISZ[AST.Stmt]()
    for (stmt <- body.stmts) {
      stmt match {
        case stmt: AST.Stmt.Import =>
          val (newScopeOpt, newStmt) = checkImport(scope, stmt, reporter)
          newScopeOpt match {
            case Some(newScope) => scope = newScope
            case _ => return (scope, body)
          }
          stmts = stmts :+ newStmt
        case stmt: AST.Stmt.Method =>
          val id = stmt.sig.id.value
          checkLocalId(id, stmt.posOpt)
          val infoOpt = to.outlineMethod(
            Info.Method(
              context,
              isWorksheet,
              scope,
              T,
              stmt(
                attr = stmt.attr(
                  resOpt = Some(
                    AST.ResolvedInfo.Method(
                      isWorksheet,
                      AST.MethodMode.Method,
                      stmt.sig.typeParams.map(tp => tp.id.value),
                      context,
                      id,
                      stmt.sig.params.map(p => p.id.value),
                      None(),
                      ISZ(),
                      ISZ()
                    )
                  )
                )
              )
            ),
            reporter
          )
          infoOpt match {
            case Some(info: Info.Method) =>
              scope = scope(nameMap = scope.nameMap + id ~> info)
              stmts = stmts :+ info.ast
            case _ => ok = F
          }
        case stmt: AST.Stmt.SpecMethod =>
          val id = stmt.sig.id.value
          checkLocalId(id, stmt.posOpt)
          val infoOpt = to.outlineSpecMethod(
            Info.SpecMethod(
              context,
              isWorksheet,
              scope,
              stmt(
                attr = stmt.attr(
                  resOpt = Some(
                    AST.ResolvedInfo.Method(
                      isWorksheet,
                      AST.MethodMode.Method,
                      stmt.sig.typeParams.map(tp => tp.id.value),
                      context,
                      id,
                      stmt.sig.params.map(p => p.id.value),
                      None(),
                      ISZ(),
                      ISZ()
                    )
                  )
                )
              )
            ),
            reporter
          )
          infoOpt match {
            case Some(info: Info.SpecMethod) =>
              scope = scope(nameMap = scope.nameMap + id ~> info)
              stmts = stmts :+ info.ast
            case _ => ok = F
          }
        case _ => stmts = stmts :+ stmt
      }
    }
    if (!ok) {
      return (scope, body)
    }
    scope = sc(nameMap = scope.nameMap)
    var nameMap = scope.nameMap
    var stmts2 = ISZ[AST.Stmt]()
    val mscopes = MSZ.create(stmts.size, scope)
    for (i <- z"0" until stmts.size) {
      mscopes(i) = scope
      val stmt = stmts(i)
      stmt match {
        case stmt: AST.Stmt.Import =>
          val (newScopeOpt, newStmt) = checkImport(scope, stmt, reporter)
          scope = newScopeOpt.get
          stmts2 = stmts2 :+ newStmt
        case stmt: AST.Stmt.Method if stmt.hasContract =>
          val id = stmt.sig.id.value
          val newStmt = TypeChecker.checkMethodContractSequent(strictAliasing, typeHierarchy, context :+ id, scope,
            isInMutableContext, stmt, reporter)
          stmts2 = stmts2 :+ newStmt
          val info = nameMap.get(id).get.asInstanceOf[Info.Method]
          nameMap = nameMap + id ~> info(ast = newStmt)
        case stmt: AST.Stmt.Fact =>
          val id = stmt.id.value
          val name = context :+ id
          val newStmt = TypeChecker.checkFactStmt(strictAliasing, typeHierarchy, name, scope, stmt, reporter)
          stmts2 = stmts2 :+ newStmt
          val info = typeHierarchy.nameMap.get(name).get.asInstanceOf[Info.Fact]
          nameMap = nameMap + id ~> info(ast = newStmt)
        case stmt: AST.Stmt.Theorem =>
          val id = stmt.id.value
          val name = context :+ id
          val newStmt = TypeChecker.checkTheoremStmt(strictAliasing, typeHierarchy, name, scope, stmt, reporter)
          stmts2 = stmts2 :+ newStmt
          val info = typeHierarchy.nameMap.get(name).get.asInstanceOf[Info.Theorem]
          nameMap = nameMap + id ~> info(ast = newStmt)
        case stmt: AST.Stmt.Inv =>
          val id = stmt.id.value
          val name = context :+ id
          val newStmt = TypeChecker.checkInvStmt(strictAliasing, typeHierarchy, name, scope, stmt, reporter)
          stmts2 = stmts2 :+ newStmt
          val info = typeHierarchy.nameMap.get(name).get.asInstanceOf[Info.Inv]
          nameMap = nameMap + id ~> info(ast = newStmt)
        case stmt: AST.Stmt.Var =>
          val (newScopeOpt, newStmt) = checkVarStmt(scope, stmt, reporter)
          newScopeOpt match {
            case Some(newScope) => scope = newScope
            case _ => return (sc, body)
          }
          stmts2 = stmts2 :+ newStmt
        case stmt: AST.Stmt.VarPattern =>
          val (newScopeOpt, newStmt) = checkVarPatternStmt(scope, stmt, reporter)
          newScopeOpt match {
            case Some(newScope) => scope = newScope
            case _ => return (sc, body)
          }
          stmts2 = stmts2 :+ newStmt
        case _ =>
          stmts2 = stmts2 :+ stmt
      }
    }
    val scopes = mscopes.toIS
    val newStmts = checkStmts(scopes, expectedOpt, stmts2, reporter)
    val newScope: Scope.Local = if (scopes.isEmpty) scope else scopes(scopes.size - 1)
    val undecls: ISZ[AST.ResolvedInfo.LocalVar] = {
      var r = ISZ[AST.ResolvedInfo.LocalVar]()
      for (e <- newScope.nameMap.entries) {
        e._2 match {
          case _: Info.Method =>
          case _: Info.SpecMethod =>
          case Info.LocalVar(_, _, _, _, Some(res: AST.ResolvedInfo.LocalVar)) => r = r :+ res
          case _ => halt("Infeasible")
        }
      }
      r
    }
    return (scope, body(stmts = newStmts, undecls = undecls))
  }

  def checkPattern(
    isSpec: B,
    unrefinedIdOpt: Option[AST.Id],
    expected: AST.Typed,
    sc: Scope.Local,
    pat: AST.Pattern,
    reporter: Reporter
  ): (Option[Scope.Local], AST.Pattern) = {

    var scope = createNewScope(sc)
    var ok = T

    def declId(checkMutable: B, idOpt: Option[AST.Id], tOpt: Option[AST.Typed]): Unit = {
      idOpt match {
        case Some(id) =>
          val key = id.value
          if (scope.nameMap.contains(key)) {
            reporter.error(
              id.attr.posOpt,
              typeCheckerKind,
              s"Cannot declare '$key' because the identifier has already been previously declared."
            )
            ok = F
          } else {
            if (checkMutable) {
              val t = tOpt.get
              val refined: B = unrefinedIdOpt match {
                case Some(uid) => uid.value == id.value
                case _ => F
              }
              if (!refined && typeHierarchy.isMutable(t, T)) {
                val possibly: String = if (t.hasTypeVars) " possibly" else ""
                reporter.error(
                  id.attr.posOpt,
                  typeCheckerKind,
                  s"Cannot introduce a new name '$key' for$possibly mutable type '$t'."
                )
              }
            }
            scope = scope(
              nameMap = scope.nameMap + key ~> Info.LocalVar(
                context :+ key,
                F,
                id,
                tOpt,
                Some(AST.ResolvedInfo.LocalVar(context, AST.ResolvedInfo.LocalVar.Scope.Current, isSpec, T, key))
              )
            )
          }
        case _ =>
      }
    }

    def checkTipe(expectedType: AST.Typed, tipe: AST.Type): AST.Type = {
      val newTipeOpt = typeHierarchy.typed(scope, tipe, reporter)
      newTipeOpt match {
        case Some(newTipe) if newTipe.typedOpt.nonEmpty =>
          val t = newTipe.typedOpt.get
          if (t != expectedType && typeHierarchy.isSubType(expectedType, t)) {
            // OK
          } else {
            if (t == expectedType) {
              reporter.warn(
                tipe.posOpt,
                typeCheckerKind,
                s"Unnecessary type matching because it is always going to be successful (i.e.,  $t ≡ $expectedType)."
              )
            } else if (typeHierarchy.glb(ISZ(expectedType, t)).isEmpty) {
              reporter.error(
                tipe.posOpt,
                typeCheckerKind,
                s"Fruitless type matching because it is always going to be unsuccessful (i.e., $t and $expectedType do not have a common subtype)."
              )
              ok = F
            }
          }
          return newTipe
        case _ => return tipe
      }
    }

    def checkPatternH(expectedType: AST.Typed, pattern: AST.Pattern): AST.Pattern = {
      pattern match {
        case pattern: AST.Pattern.Wildcard =>
          pattern.typeOpt match {
            case Some(tipe) =>
              val newTipe = checkTipe(expectedType, tipe)
              return pattern(typeOpt = Some(newTipe), attr = pattern.attr(typedOpt = Some(expectedType)))
            case _ => return pattern(attr = pattern.attr(typedOpt = Some(expectedType)))
          }
        case pattern: AST.Pattern.SeqWildcard =>
          return pattern(attr = pattern.attr(typedOpt = Some(expectedType)))
        case pattern: AST.Pattern.Ref =>
          val refName = pattern.name.ids.map[String](id => id.value)
          checkInfoOpt(Some(scope), scope.resolveName(typeHierarchy.nameMap, refName), pattern.posOpt, reporter) match {
            case (Some(t), resOpt) =>
              if (typeHierarchy.glb(ISZ(expectedType, t)).isEmpty) {
                reporter.error(
                  pattern.posOpt,
                  typeCheckerKind,
                  s"Fruitless matching because it is always going to be unsuccessful (i.e., $t and $expectedType do not have a common subtype)."
                )
                ok = F
              }
              return pattern(attr = pattern.attr(typedOpt = Some(t), resOpt = resOpt))
            case _ =>
              reporter.error(pattern.posOpt, typeCheckerKind, st"Could not resolve '${(refName, ".")}'.".render)
              return pattern
          }
        case pattern: AST.Pattern.Literal =>
          val t = pattern.lit.typedOpt.get
          if (t != expectedType) {
            reporter.error(pattern.posOpt, typeCheckerKind, s"Cannot match $t with $expectedType.")
          }
          return pattern
        case pattern: AST.Pattern.LitInterpolate =>
          val t: AST.Typed = pattern.prefix.native match {
            case "z" => AST.Typed.z
            case "r" => AST.Typed.r
            case "c" => AST.Typed.c
            case "f32" => AST.Typed.f32
            case "f64" => AST.Typed.f64
            case "string" => AST.Typed.string
            case _ =>
              val tpeOpt = checkStringInterpolator(pattern.posOpt, scope, pattern.prefix, pattern.value, reporter)
              tpeOpt match {
                case Some(tpe) => tpe
                case _ => return pattern
              }
          }
          if (t != expectedType) {
            reporter.error(pattern.posOpt, typeCheckerKind, s"Cannot match $t with $expectedType.")
          }
          return pattern(attr = pattern.attr(typedOpt = Some(t)))
        case pattern: AST.Pattern.VarBinding =>
          pattern.tipeOpt match {
            case Some(tipe) =>
              val newTipe = checkTipe(expectedType, tipe)
              if (newTipe.typedOpt.nonEmpty) {
                val t = newTipe.typedOpt.get
                val tOpt: Option[AST.Typed] = Some(t)
                declId(T, Some(pattern.id), tOpt)
                return pattern(tipeOpt = Some(newTipe), attr = pattern.attr(typedOpt = tOpt))
              }
              return pattern(tipeOpt = Some(newTipe))
            case _ =>
              val tOpt: Option[AST.Typed] = Some(expectedType)
              declId(F, Some(pattern.id), tOpt)
              return pattern(attr = pattern.attr(typedOpt = tOpt))
          }
        case pattern: AST.Pattern.Structure =>
          pattern.nameOpt match {
            case Some(nm) =>
              val ti: TypeInfo = scope.resolveType(typeHierarchy.typeMap, nm.ids.map(id => id.value)) match {
                case Some(info) => info
                case nms =>
                  reporter
                    .error(pattern.posOpt, typeCheckerKind, st"Could not resolve type named ${(nms, ".")}.".render)
                  return pattern
              }

              val name = ti.name

              def s(valueType: AST.Typed): AST.Pattern = {
                var newPatterns = ISZ[AST.Pattern]()
                for (p <- pattern.patterns) {
                  val newPattern = checkPatternH(valueType, p)
                  newPatterns = newPatterns :+ newPattern
                }
                val tOpt: Option[AST.Typed] = Some(expectedType)
                declId(T, pattern.idOpt, tOpt)
                return pattern(
                  patterns = newPatterns,
                  attr = pattern.attr(typedOpt = tOpt, resOpt = AST.Typed.unapplySeqResOpt)
                )
              }
              (ti.name, expectedType) match {
                case (AST.Typed.isName, AST.Typed.Name(AST.Typed.isName, argTypes)) => val r = s(argTypes(1)); return r
                case (AST.Typed.msName, AST.Typed.Name(AST.Typed.msName, argTypes)) => val r = s(argTypes(1)); return r
                case (AST.Typed.iszName, AST.Typed.Name(AST.Typed.isName, argTypes)) =>
                  if (argTypes(0) != AST.Typed.z) {
                    reporter.error(
                      pattern.posOpt,
                      typeCheckerKind,
                      st"Expecting an '${(AST.Typed.isName, ".")}' with index type '${AST.Typed.z}', but index type '${argTypes(0)}' found.".render
                    )
                    return pattern
                  }
                  val r = s(argTypes(1))
                  return r
                case (AST.Typed.mszName, AST.Typed.Name(AST.Typed.msName, argTypes)) =>
                  if (argTypes(0) != AST.Typed.z) {
                    reporter.error(
                      pattern.posOpt,
                      typeCheckerKind,
                      st"Expecting an '${(AST.Typed.msName, ".")}' with index type '${AST.Typed.z}', but index type '${argTypes(0)}' found.".render
                    )
                    return pattern
                  }
                  val r = s(argTypes(1))
                  return r
                case (AST.Typed.zsName, AST.Typed.Name(AST.Typed.msName, argTypes)) =>
                  var ok2 = T
                  if (argTypes(0) != AST.Typed.z) {
                    reporter.error(
                      pattern.posOpt,
                      typeCheckerKind,
                      st"Expecting an '${(AST.Typed.msName, ".")}' with index type '${AST.Typed.z}', but index type '${argTypes(0)}' found.".render
                    )
                    ok2 = F
                  }
                  if (argTypes(1) != AST.Typed.z) {
                    reporter.error(
                      pattern.posOpt,
                      typeCheckerKind,
                      st"Expecting an '${(AST.Typed.msName, ".")}' with element type '${AST.Typed.z}', but element type '${argTypes(1)}' found.".render
                    )
                    ok2 = F
                  }
                  if (!ok2) {
                    ok = F
                    return pattern
                  }
                  val r = s(argTypes(1))
                  return r
                case (_, expected: AST.Typed.Name) =>
                  scope.resolveType(typeHierarchy.typeMap, name) match {
                    case Some(info: TypeInfo.Adt) if !info.ast.isRoot =>
                      def partialResult: AST.Pattern = {
                        return pattern(attr = pattern.attr(resOpt = info.extractorResOpt))
                      }

                      val size = info.extractorTypeMap.size
                      if (size != pattern.patterns.size) {
                        reporter.error(
                          pattern.posOpt,
                          typeCheckerKind,
                          s"Expecting $size patterns, but ${pattern.patterns.size} found."
                        )
                        ok = F
                        return partialResult
                      }
                      val smOpt =
                        unify(typeHierarchy, pattern.posOpt, TypeRelation.Subtype, expected, info.tpe, reporter)
                      smOpt match {
                        case Some(sm) =>
                          val ok2 = checkUnboundTypeVar(
                            pattern.posOpt,
                            info.tpe,
                            sm,
                            info.ast.typeParams.map(tp => tp.id.value),
                            reporter
                          )
                          if (!ok2) {
                            ok = F
                            return partialResult
                          }
                          val typedOpt: Option[AST.Typed] = Some(info.tpe.subst(sm))
                          declId(T, pattern.idOpt, typedOpt)
                          var newPatterns = ISZ[AST.Pattern]()
                          var i = 0
                          val exts = info.extractorTypeMap.values
                          while (i < size) {
                            val newPattern = checkPatternH(exts(i).subst(sm), pattern.patterns(i))
                            newPatterns = newPatterns :+ newPattern
                            i = i + 1
                          }
                          return pattern(
                            patterns = newPatterns,
                            attr = pattern.attr(typedOpt = typedOpt, resOpt = info.extractorResOpt)
                          )
                        case _ => return partialResult
                      }
                    case Some(_) =>
                      reporter.error(
                        pattern.posOpt,
                        typeCheckerKind,
                        st"Cannot pattern match on type $expected using ${(name, ".")}.".render
                      )
                      ok = F
                      return pattern
                    case _ =>
                  }
                case _ =>
                  expectedType match {
                    case _: AST.Typed.Name =>
                      reporter.error(
                        pattern.posOpt,
                        typeCheckerKind,
                        st"Cannot pattern match on type $expectedType using ${(name, ".")}.".render
                      )
                      ok = F
                    case _ =>
                  }
              }
              reporter
                .error(pattern.posOpt, typeCheckerKind, st"Undefined type ${(name, ".")} for pattern matching.".render)
              ok = F
              return pattern
            case _ =>
              expectedType match {
                case expected: AST.Typed.Tuple =>
                  val size = expected.args.size
                  if (size != pattern.patterns.size) {
                    reporter.error(
                      pattern.posOpt,
                      typeCheckerKind,
                      s"Expecting $size patterns, but ${pattern.patterns.size} found."
                    )
                    ok = F
                    return pattern
                  }
                  var newPatterns = ISZ[AST.Pattern]()
                  var i = 0
                  while (i < size) {
                    val p = checkPatternH(expected.args(i), pattern.patterns(i))
                    newPatterns = newPatterns :+ p
                    i = i + 1
                  }
                  declId(T, pattern.idOpt, Some(expected))
                  return pattern(
                    patterns = newPatterns,
                    attr = pattern.attr(typedOpt = Some(expected), resOpt = AST.Typed.unapplyTupleResOpt)
                  )
                case _ =>
                  reporter.error(pattern.posOpt, typeCheckerKind, "Cannot match non-tuple type with tuple extractor.")
                  ok = F
                  return pattern
              }
          }
      }
    }
    val r = checkPatternH(expected, pat)
    return if (ok) (Some(scope), r) else (None(), r)
  }

  def checkExpr(
    expectedOpt: Option[AST.Typed],
    scope: Scope.Local,
    stmt: AST.Stmt.Expr,
    reporter: Reporter
  ): AST.Stmt.Expr = {
    val (newExp, typedOpt) = checkExp(expectedOpt, scope, stmt.exp, reporter)
    return stmt(exp = newExp, attr = stmt.attr(typedOpt = typedOpt))
  }

  def checkBlock(
    expectedOpt: Option[AST.Typed],
    scope: Scope.Local,
    stmt: AST.Stmt.Block,
    reporter: Reporter
  ): AST.Stmt = {
    val (_, newBody) = checkBody(F, expectedOpt, createNewScope(scope), stmt.body, reporter)
    return stmt(body = newBody)
  }

  def checkIf(expectedOpt: Option[AST.Typed], scope: Scope.Local, stmt: AST.Stmt.If, reporter: Reporter): AST.Stmt = {
    val (newCond, _) = checkExp(AST.Typed.bOpt, scope, stmt.cond, reporter)
    val (_, tBody) = checkBody(F, expectedOpt, createNewScope(scope), stmt.thenBody, reporter)
    val (_, eBody) = checkBody(F, expectedOpt, createNewScope(scope), stmt.elseBody, reporter)
    return stmt(cond = newCond, thenBody = tBody, elseBody = eBody)
  }

  def checkMatch(
    expectedOpt: Option[AST.Typed],
    scope: Scope.Local,
    stmt: AST.Stmt.Match,
    reporter: Reporter
  ): AST.Stmt = {

    def checkSelectNative(exp: AST.Exp.Select): (AST.Exp, Option[AST.Typed]) = {
      val receiver: AST.Exp = exp.receiverOpt.get
      val (newExp, expTypeOpt) = checkExp(None(), scope, receiver, reporter)

      def partResult: (AST.Exp, Option[AST.Typed]) = {
        return (exp(receiverOpt = Some(newExp)), None())
      }

      expTypeOpt match {
        case Some(t) =>
          t match {
            case AST.Typed.`c` =>
              return (
                exp(receiverOpt = Some(newExp), attr = exp.attr(resOpt = nativeResOpt, typedOpt = nativeCTypedOpt)),
                Some(t)
              )
            case AST.Typed.`string` =>
              return (
                exp(
                  receiverOpt = Some(newExp),
                  attr = exp.attr(resOpt = nativeResOpt, typedOpt = nativeStringTypedOpt)
                ),
                Some(t)
              )
            case AST.Typed.`f32` =>
              return (
                exp(receiverOpt = Some(newExp), attr = exp.attr(resOpt = nativeResOpt, typedOpt = nativeF32TypedOpt)),
                Some(t)
              )
            case AST.Typed.`f64` =>
              return (
                exp(receiverOpt = Some(newExp), attr = exp.attr(resOpt = nativeResOpt, typedOpt = nativeF64TypedOpt)),
                Some(t)
              )
            case _ =>
              reporter.error(
                receiver.posOpt,
                typeCheckerKind,
                s"Selector native is only usable from type C, String, F32, and F64, but '$t' found."
              )
              return partResult
          }
        case _ => return partResult
      }
    }

    val (newExp, expTypeOpt): (AST.Exp, Option[AST.Typed]) = stmt.exp match {
      case exp @ AST.Exp.Select(Some(_), AST.Id(string"native"), _) =>
        val p = checkSelectNative(exp)
        p
      case _ =>
        val p = checkExp(None(), scope, stmt.exp, reporter)
        p
    }
    val expType: AST.Typed = expTypeOpt match {
      case Some(et) => et
      case _ => return stmt(exp = newExp)
    }

    var newCases = ISZ[AST.Case]()
    val unrefinedIdOpt: Option[AST.Id] = stmt.exp match {
      case AST.Exp.Ident(id) => Some(id)
      case _ => None()
    }
    for (c <- stmt.cases) {
      val (newScopeOpt, newPattern) = checkPattern(F, unrefinedIdOpt, expType, scope, c.pattern, reporter)
      newScopeOpt match {
        case Some(newScope) =>
          val newCondOpt: Option[AST.Exp] = c.condOpt match {
            case Some(cond) =>
              val (newCond, _) = checkExp(AST.Typed.bOpt, newScope, cond, reporter)
              Some(newCond)
            case o => o
          }
          val (_, newBody) = checkBody(F, expectedOpt, newScope, c.body, reporter)
          newCases = newCases :+ c(pattern = newPattern, condOpt = newCondOpt, body = newBody)
        case _ => newCases = newCases :+ c(pattern = newPattern)
      }
    }

    return stmt(exp = newExp, cases = newCases)
  }

  def checkVarStmt(scope: Scope.Local, stmt: AST.Stmt.Var, reporter: Reporter): (Option[Scope.Local], AST.Stmt) = {
    stmt.attr.resOpt match {
      case Some(_: AST.ResolvedInfo.LocalVar) => return (None(), stmt)
      case _ =>
    }
    val key = stmt.id.value
    val name = context :+ stmt.id.value

    def err(): Unit = {
      reporter.error(
        stmt.id.attr.posOpt,
        typeCheckerKind,
        s"Cannot declare '$key' because the identifier has already been previously declared."
      )
    }

    var r = stmt
    val resOpt: Option[AST.ResolvedInfo] = scope.resolveName(typeHierarchy.nameMap, ISZ(key)) match {
      case Some(info: Info.Var) => info.resOpt
      case Some(_: Info.LocalVar) =>
        err()
        return (None(), r)
      case _ => Some(AST.ResolvedInfo.LocalVar(context, AST.ResolvedInfo.LocalVar.Scope.Current, stmt.isSpec,
        stmt.isVal, key))
    }
    val expectedOpt: Option[AST.Typed] = stmt.tipeOpt match {
      case Some(tipe) =>
        tipe.typedOpt match {
          case tOpt @ Some(_) => tOpt
          case _ =>
            val newTipeOpt = typeHierarchy.typed(scope, tipe, reporter)
            newTipeOpt match {
              case Some(newTipe) if newTipe.typedOpt.nonEmpty =>
                r = r(tipeOpt = newTipeOpt)
                newTipe.typedOpt
              case _ => return (None(), r)
            }
        }
      case _ => None()
    }
    val (rhsOpt, tOpt): (Option[AST.AssignExp], Option[AST.Typed]) = stmt.initOpt match {
      case Some(init) =>
        var tc = this
        if (stmt.isSpec) {
          tc = tc(mode = ModeContext.Spec)
        }
        val (rhs, to) = tc.checkAssignExp(expectedOpt, scope, init, reporter)
        (Some(rhs), to)
      case _ => (None(), expectedOpt)
    }
    tOpt match {
      case Some(_) =>
        val typedOpt: Option[AST.Typed] = expectedOpt match {
          case Some(_) => expectedOpt
          case _ => tOpt
        }
        val newScope = scope(
          nameMap = scope.nameMap + key ~> Info.LocalVar(name, stmt.isVal, r.id, typedOpt, resOpt)
        )
        val newStmt = r(initOpt = rhsOpt, attr = r.attr(typedOpt = typedOpt, resOpt = resOpt))
        return (Some(newScope), newStmt)
      case _ =>
        return (None(), r)
    }
  }

  def checkVarPatternStmt(scope: Scope.Local, stmt: AST.Stmt.VarPattern, reporter: Reporter): (Option[Scope.Local], AST.Stmt) = {
    var newTipeOpt = stmt.tipeOpt
    val expectedOpt: Option[AST.Typed] = newTipeOpt match {
      case Some(tipe) =>
        newTipeOpt = typeHierarchy.typed(scope, tipe, reporter)
        newTipeOpt match {
          case Some(newTipe) if newTipe.typedOpt.nonEmpty => newTipe.typedOpt
          case _ => return (None(), stmt(tipeOpt = newTipeOpt))
        }
      case _ => None()
    }
    var tc = this
    if (stmt.isSpec) {
      tc = tc(mode = ModeContext.Spec)
    }
    val (newInit, initTypeOpt) = tc.checkAssignExp(expectedOpt, scope, stmt.init, reporter)
    initTypeOpt match {
      case Some(initType) =>
        val expected: AST.Typed = if (expectedOpt.nonEmpty) expectedOpt.get else initType
        val (scopeOpt, newPattern) = checkPattern(stmt.isSpec, None(), expected, scope, stmt.pattern, reporter)
        return (scopeOpt, stmt(pattern = newPattern, tipeOpt = newTipeOpt, init = newInit))
      case _ =>
        if (expectedOpt.isEmpty) {
          val varText: String = if (stmt.isVal) "val" else "var"
          reporter.error(
            stmt.pattern.posOpt,
            typeCheckerKind,
            s"Could not infer the expected type for $varText pattern to match to."
          )
        }
        return (None(), stmt(tipeOpt = newTipeOpt, init = newInit))
    }
  }

  def checkStmt(scope: Scope.Local, stmt: AST.Stmt, reporter: Reporter): AST.Stmt = {

    def checkAssign(assignStmt: AST.Stmt.Assign): AST.Stmt = {
      assignStmt.lhs match {
        case lhs: AST.Exp.Ident =>
          def checkAssignH(expectedOpt: Option[AST.Typed], resOpt: Option[AST.ResolvedInfo]): AST.Stmt = {
            val (newRhs, _) = checkAssignExp(expectedOpt, scope, assignStmt.rhs, reporter)
            val newResOpt: Option[AST.ResolvedInfo] = resOpt match {
              case Some(res: AST.ResolvedInfo.LocalVar) if !scope.nameMap.contains(res.id) =>
                if (res.context != context) Some(res(scope = AST.ResolvedInfo.LocalVar.Scope.Closure))
                else Some(res(scope = AST.ResolvedInfo.LocalVar.Scope.Outer))
              case _ => resOpt
            }
            return assignStmt(lhs = lhs(attr = lhs.attr(resOpt = newResOpt, typedOpt = expectedOpt)), rhs = newRhs)
          }
          def partResultIdent(): AST.Stmt = {
            val (newRhs, _) = checkAssignExp(None(), scope, assignStmt.rhs, reporter)
            return assignStmt(rhs = newRhs)
          }
          def checkVarInfo(info: Info.Var): AST.Stmt = {
            if (inSpec) {
              reporter.error(lhs.posOpt, typeCheckerKind, s"Cannot assign to variable '${lhs.id.value}' in spec context.")
            }
            if (info.ast.isVal) {
              reporter.error(lhs.posOpt, typeCheckerKind, s"Cannot assign to read-only variable '${lhs.id.value}'.")
            }
            val r = checkAssignH(info.typedOpt, info.resOpt)
            return r
          }
          def checkSpecVarInfo(info: Info.SpecVar): AST.Stmt = {
            if (!inSpec) {
              reporter.error(lhs.posOpt, typeCheckerKind, s"Cannot assign to variable '${lhs.id.value}' in non-spec context.")
            }
            if (info.ast.isVal) {
              reporter.error(lhs.posOpt, typeCheckerKind, s"Cannot assign to read-only variable '${lhs.id.value}'.")
            }
            val r = checkAssignH(info.typedOpt, info.resOpt)
            return r
          }
          scope.resolveName(typeHierarchy.nameMap, ISZ(lhs.id.value)) match {
            case Some(info: Info.LocalVar) =>
              if (info.isVal) {
                reporter.error(lhs.posOpt, typeCheckerKind, s"Cannot assign to read-only variable '${lhs.id.value}'.")
              }
              val r = checkAssignH(info.typedOpt, info.resOpt)
              return r
            case Some(info: Info.Var) => return checkVarInfo(info)
            case Some(info: Info.SpecVar) => return checkSpecVarInfo(info)
            case Some(info) =>
              reporter.error(lhs.posOpt, typeCheckerKind, st"Cannot assign to '${(info.name, ".")}'.".render)
              val (newRhs, _) = checkAssignExp(None(), scope, assignStmt.rhs, reporter)
              return assignStmt(rhs = newRhs)
            case _ =>
              scope.thisOpt match {
                case Some(t: AST.Typed.Name) =>
                  typeHierarchy.typeMap.get(t.ids) match {
                    case Some(info: TypeInfo.Adt) =>
                      info.vars.get(lhs.id.value) match {
                        case Some(varInfo) => val r = checkVarInfo(varInfo); return r
                        case _ =>
                      }
                      info.specVars.get(lhs.id.value) match {
                        case Some(varInfo) => val r = checkSpecVarInfo(varInfo); return r
                        case _ =>
                      }
                    case Some(info: TypeInfo.Sig) =>
                      info.specVars.get(lhs.id.value) match {
                        case Some(varInfo) => val r = checkSpecVarInfo(varInfo); return r
                        case _ =>
                      }
                    case _ =>
                  }
                case _ =>
              }
              reporter.error(lhs.id.attr.posOpt, typeCheckerKind, s"Could not resolve '${lhs.id.value}'.")
              val r = partResultIdent()
              return r
          }
        case lhs: AST.Exp.Select =>
          val (newReceiver, receiverTypeOpt) = checkExp(None(), scope, lhs.receiverOpt.get, reporter)
          def partResultSelect(): AST.Stmt = {
            val (newRhs, _) = checkAssignExp(None(), scope, assignStmt.rhs, reporter)
            return assignStmt(lhs = lhs(receiverOpt = Some(newReceiver)), rhs = newRhs)
          }
          def checkSelectAssignH(isVal: B, owner: QName, typedOpt: Option[AST.Typed], resOpt: Option[AST.ResolvedInfo],
                                 substMap: HashMap[String, AST.Typed]): AST.Stmt = {
            if (isVal) {
              reporter.error(
                lhs.id.attr.posOpt,
                typeCheckerKind,
                st"Cannot assign to val '${lhs.id.value}' of '${(owner, ".")}'.".render
              )
            }
            val expected = typedOpt.get.subst(substMap)
            val (newRhs, _) = checkAssignExp(Some(expected), scope, assignStmt.rhs, reporter)
            return assignStmt(
              lhs = lhs(
                receiverOpt = Some(newReceiver),
                attr = lhs.attr(resOpt = resOpt, typedOpt = Some(expected))
              ),
              rhs = newRhs
            )
          }
          def checkSelectVarInfo(t: AST.Typed.Name, typeParams: ISZ[AST.TypeParam], info: Info.Var): AST.Stmt = {
            if (inSpec) {
              reporter.error(lhs.posOpt, typeCheckerKind, s"Cannot assign to variable '${lhs.id.value}' in spec context.")
            }
            if (info.ast.isVal) {
              reporter.error(lhs.posOpt, typeCheckerKind, s"Cannot assign to read-only variable '${lhs.id.value}'.")
            }
            val smOpt = buildTypeSubstMap(t.ids, lhs.id.attr.posOpt, typeParams, t.args, reporter)
            smOpt match {
              case Some(sm) => val r = checkSelectAssignH(info.ast.isVal, t.ids, info.typedOpt, info.resOpt, sm); return r
              case _ =>
            }
            return partResultSelect()
          }
          def checkSelectSpecVarInfo(t: AST.Typed.Name, typeParams: ISZ[AST.TypeParam], info: Info.SpecVar): AST.Stmt = {
            if (!inSpec) {
              reporter.error(lhs.posOpt, typeCheckerKind, s"Cannot assign to variable '${lhs.id.value}' in non-spec context.")
            }
            if (info.ast.isVal) {
              reporter.error(lhs.posOpt, typeCheckerKind, s"Cannot assign to read-only variable '${lhs.id.value}'.")
            }
            val smOpt = buildTypeSubstMap(t.ids, lhs.id.attr.posOpt, typeParams, t.args, reporter)
            smOpt match {
              case Some(sm) => val r = checkSelectAssignH(info.ast.isVal, t.ids, info.typedOpt, info.resOpt, sm); return r
              case _ =>
            }
            return partResultSelect()
          }
          receiverTypeOpt match {
            case Some(t: AST.Typed.Name) =>
              typeHierarchy.typeMap.get(t.ids) match {
                case Some(info: TypeInfo.Adt) =>
                  info.vars.get(lhs.id.value) match {
                    case Some(varInfo) => return checkSelectVarInfo(t, info.ast.typeParams, varInfo)
                    case _ =>
                  }
                  info.specVars.get(lhs.id.value) match {
                    case Some(varInfo) => return checkSelectSpecVarInfo(t, info.ast.typeParams, varInfo)
                    case _ =>
                  }
                  reporter.error(
                    lhs.id.attr.posOpt,
                    typeCheckerKind,
                    st"'${lhs.id.value}' is not a var of '${(info.name, ".")}'.".render
                  )
                case Some(info: TypeInfo.Sig) =>
                  info.specVars.get(lhs.id.value) match {
                    case Some(varInfo) => return checkSelectSpecVarInfo(t, info.ast.typeParams, varInfo)
                    case _ =>
                  }
                  reporter.error(
                    lhs.id.attr.posOpt,
                    typeCheckerKind,
                    st"'${lhs.id.value}' is not a var of '${(info.name, ".")}'.".render
                  )
                case _ =>
              }
              val r = partResultSelect()
              return r
            case Some(t: AST.Typed.Object) =>
              typeHierarchy.nameMap.get(t.owner :+ t.id :+ lhs.id.value) match {
                case Some(varInfo: Info.Var) =>
                  val r = checkSelectAssignH(varInfo.ast.isVal, t.name, varInfo.typedOpt, varInfo.resOpt, emptySubstMap)
                  return r
                case _ =>
                  reporter.error(
                    lhs.id.attr.posOpt,
                    typeCheckerKind,
                    st"'${lhs.id.value}' is not a var of '${(t.owner :+ t.id, ".")}'.".render
                  )
                  val r = partResultSelect()
                  return r
              }
            case Some(t) =>
              reporter.error(lhs.id.attr.posOpt, typeCheckerKind, st"'${lhs.id.value}' is not a var of '$t'.".render)
              val r = partResultSelect()
              return r
            case _ => val r = partResultSelect(); return r
          }
        case lhs: AST.Exp.Invoke =>
          val receiver = lhs.receiverOpt.get
          val (newReceiver, receiverTypeOpt) = checkExp(None(), scope, receiver, reporter)
          receiverTypeOpt match {
            case Some(AST.Typed.Name(AST.Typed.msName, args)) =>
              val (newArg, _) = checkExp(Some(args(0)), scope, lhs.args(0), reporter)
              val (newRhs, _) = checkAssignExp(Some(args(1)), scope, assignStmt.rhs, reporter)
              val resOpt: Option[AST.ResolvedInfo] = Some(
                AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Apply)
              )
              return assignStmt(
                lhs = lhs(
                  receiverOpt = Some(newReceiver),
                  ident = lhs.ident(
                    attr =
                      lhs.ident.attr(posOpt = lhs.ident.id.attr.posOpt, resOpt = resOpt, typedOpt = receiverTypeOpt)
                  ),
                  args = ISZ(newArg),
                  attr = lhs.attr(resOpt = resOpt, typedOpt = Some(args(1)))
                ),
                rhs = newRhs
              )
            case Some(lhsType) =>
              reporter.error(lhs.posOpt, typeCheckerKind, s"Cannot perform index update on type '$lhsType'.")
            case _ =>
          }
          return assignStmt(lhs = lhs(receiverOpt = Some(newReceiver)))
        case _ => halt("Unexpected situation when type checking assignment.")
      }
    }

    def checkFor(forStmt: AST.Stmt.For): AST.Stmt = {
      val (newScopeOpt, newEnumGens, _, _) = checkEnumGens(F, scope, forStmt.enumGens, reporter)
      newScopeOpt match {
        case Some(newScope) =>
          val thisL = this
          val (_, newMods) = thisL(mode = ModeContext.Spec).checkLoopInv(newScope, ISZ(), forStmt.modifies, reporter)
          val (_, newBody) = checkBody(F, None(), newScope, forStmt.body, reporter)
          return forStmt(context = context, enumGens = newEnumGens, modifies = newMods, body = newBody)
        case _ => return forStmt(context = context, enumGens = newEnumGens)
      }
    }

    def checkDoWhile(doWhileStmt: AST.Stmt.DoWhile): AST.Stmt = {
      val thisL = this
      val (newInvs, newMods) = thisL(mode = ModeContext.Spec).
        checkLoopInv(scope, doWhileStmt.invariants, doWhileStmt.modifies, reporter)
      val (_, newBody) = checkBody(F, None(), createNewScope(scope), doWhileStmt.body, reporter)
      val (newCond, _) = checkExp(AST.Typed.bOpt, scope, doWhileStmt.cond, reporter)
      return doWhileStmt(context = context, cond = newCond, invariants = newInvs, modifies = newMods, body = newBody)
    }

    def checkWhile(whileStmt: AST.Stmt.While): AST.Stmt = {
      val (newCond, _) = checkExp(AST.Typed.bOpt, scope, whileStmt.cond, reporter)
      val thisL = this
      val (newInvs, newMods) = thisL(mode = ModeContext.Spec).
        checkLoopInv(scope, whileStmt.invariants, whileStmt.modifies, reporter)
      val (_, newBody) = checkBody(F, None(), createNewScope(scope), whileStmt.body, reporter)
      return whileStmt(context = context, cond = newCond, invariants = newInvs, modifies = newMods, body = newBody)
    }

    def checkReturn(returnStmt: AST.Stmt.Return): AST.Stmt = {
      (scope.returnOpt, returnStmt.expOpt) match {
        case (tOpt @ Some(_), Some(exp)) =>
          val (newExp, expTypeCond) = checkExp(tOpt, scope, exp, reporter)
          expTypeCond match {
            case Some(t) =>
              if (!typeHierarchy.isSubType(t, tOpt.get)) {
                reporter.error(
                  exp.posOpt,
                  typeCheckerKind,
                  s"Expecting type '${tOpt.get}', but incompatible type '$t' found."
                )
              }
              return returnStmt(expOpt = Some(newExp), attr = returnStmt.attr(typedOpt = tOpt))
            case _ => return returnStmt(expOpt = Some(newExp))
          }
        case (Some(t), _) =>
          if (t != AST.Typed.unit) {
            reporter.error(returnStmt.posOpt, typeCheckerKind, s"Expecting type '$t', but none found.")
            return returnStmt
          } else {
            return returnStmt(attr = returnStmt.attr(typedOpt = AST.Typed.unitOpt))
          }
        case (_, Some(exp)) =>
          val (newExp, _) = checkExp(None(), scope, exp, reporter)
          reporter.error(exp.posOpt, typeCheckerKind, s"Unexpected return expression.")
          return returnStmt(expOpt = Some(newExp))
        case _ =>
          return returnStmt
      }
    }

    stmt match {

      case stmt: AST.Stmt.Adt =>
        typeHierarchy.typeMap.get(context :+ stmt.id.value) match {
          case Some(info: TypeInfo.Adt) => return info.ast
          case _ => halt("Unexpected situation when type checking statement.")
        }

      case stmt: AST.Stmt.Assign => return checkAssign(stmt)

      case stmt: AST.Stmt.Block => return checkBlock(None(), scope, stmt, reporter)

      case stmt: AST.Stmt.DoWhile => return checkDoWhile(stmt)

      case stmt: AST.Stmt.Enum => return stmt

      case stmt: AST.Stmt.Expr => return checkExpr(None(), scope, stmt, reporter)

      case stmt: AST.Stmt.ExtMethod => return stmt

      case stmt: AST.Stmt.JustMethod =>
        stmt.etaOpt match {
          case Some(eta) =>
            val res = stmt.attr.resOpt.get.asInstanceOf[AST.ResolvedInfo.Method]
            val name = res.owner :+ eta.value
            typeHierarchy.nameMap.get(name) match {
              case Some(info: Info.Method) =>
                val ast = info.ast
                if (ast.purity != AST.Purity.Pure) {
                  reporter.error(eta.posOpt, typeCheckerKind, st"Object method ${(name, ".")} has to be @pure".render)
                }
                if (stmt.sig.typeParams != ast.sig.typeParams) {
                  reporter.error(eta.posOpt, typeCheckerKind, st"Object method ${(name, ".")}'s type parameters are not equivalent with ${(res.owner :+ res.id, ".")}'s".render)
                }
                if (info.methodType.tpe.ret != AST.Typed.unit) {
                  reporter.error(eta.posOpt, typeCheckerKind, st"Object method ${(name, ".")}'s return type should be Unit".render)
                }
                if (ast.contract.isEmpty) {
                  ast.bodyOpt match {
                    case Some(AST.Body(ISZ(AST.Stmt.DeduceSequent(_, ISZ(sequent)), _*))) if stmt.sig.params.size != sequent.premises.size =>
                      reporter.error(eta.posOpt, typeCheckerKind, st"Mismatch number of object method ${(name, ".")}'s sequent premises with ${(res.owner :+ res.id, ".")}'s number of parameters'".render)
                    case Some(_) =>
                    case _ =>
                      reporter.error(eta.posOpt, typeCheckerKind, st"Expecting a simple contract or a single sequent for object method ${(name, ".")}".render)
                  }
                } else {
                  ast.contract match {
                    case contract: AST.MethodContract.Simple if stmt.sig.params.size != contract.requires.size =>
                      reporter.error(eta.posOpt, typeCheckerKind, st"Mismatch number of object method ${(name, ".")}'s preconditions with ${(res.owner :+ res.id, ".")}'s number of parameters'".render)
                    case _ =>
                  }
                }
              case _ =>
                reporter.error(eta.posOpt, typeCheckerKind, st"Could not find object method ${(name, ".")}".render)
            }
          case _ =>
        }
        return stmt

      case stmt: AST.Stmt.For => return checkFor(stmt)

      case stmt: AST.Stmt.If => return checkIf(None(), scope, stmt, reporter)

      case stmt: AST.Stmt.Import => return checkImport(scope, stmt, reporter)._2

      case stmt: AST.Stmt.Match => return checkMatch(None(), scope, stmt, reporter)

      case stmt: AST.Stmt.Method =>
        if (stmt.typeChecked) {
          return stmt
        }
        val tc = TypeChecker(typeHierarchy, context :+ stmt.sig.id.value, isInMutableContext, mode, strictAliasing)
        val r = tc.checkMethod(scope, stmt, reporter)
        return r

      case stmt: AST.Stmt.Object =>
        typeHierarchy.nameMap.get(context :+ stmt.id.value) match {
          case Some(info: Info.Object) => return info.ast
          case _ => halt("Unexpected situation when type checking statement.")
        }

      case stmt: AST.Stmt.Return => return checkReturn(stmt)

      case stmt: AST.Stmt.Sig =>
        typeHierarchy.typeMap.get(context :+ stmt.id.value) match {
          case Some(info: TypeInfo.Sig) => return info.ast
          case _ => halt("Unexpected situation when type checking statement.")
        }

      case stmt: AST.Stmt.SpecMethod => return stmt

      case stmt: AST.Stmt.SpecVar => return stmt

      case stmt: AST.Stmt.SubZ => return stmt

      case stmt: AST.Stmt.TypeAlias => return stmt

      case stmt: AST.Stmt.Var => return checkVarStmt(scope, stmt, reporter)._2

      case stmt: AST.Stmt.VarPattern => return stmt

      case stmt: AST.Stmt.While => return checkWhile(stmt)

      case stmt: AST.Stmt.SpecLabel => return stmt

      case stmt: AST.Stmt.SpecBlock =>
        val tc = TypeChecker(typeHierarchy, context, isInMutableContext, ModeContext.Spec, strictAliasing)
        return tc.checkStmt(scope, stmt.block, reporter)

      case stmt: AST.Stmt.DeduceSequent =>
        val tc = TypeChecker(typeHierarchy, context, isInMutableContext, ModeContext.Spec, strictAliasing)
        return stmt(sequents = for (sequent <- stmt.sequents) yield tc.checkSequent(scope, sequent, reporter))

      case stmt: AST.Stmt.DeduceSteps =>
        val tc = TypeChecker(typeHierarchy, context, isInMutableContext, ModeContext.Spec, strictAliasing)
        return stmt(steps = for (step <- stmt.steps) yield tc.checkStep(scope, step, reporter))

      case stmt: AST.Stmt.Havoc =>
        val tc = TypeChecker(typeHierarchy, context, isInMutableContext, ModeContext.Spec, strictAliasing)
        return stmt(args = tc.checkModifiesH("havoc", scope, stmt.args, reporter))

      case stmt: AST.Stmt.Fact => return stmt

      case stmt: AST.Stmt.Theorem => return stmt

      case stmt: AST.Stmt.Inv => return stmt

      case stmt: AST.Stmt.DataRefinement => return stmt
    }
  }

  def checkSequent(scope: Scope.Local, sequent: AST.Sequent, reporter: Reporter): AST.Sequent = {
    val bExpectedOpt: Option[AST.Typed] = Some(AST.Typed.b)
    return sequent(
      premises = for (premise <- sequent.premises) yield checkExp(bExpectedOpt, scope, premise, reporter)._1,
      conclusion = checkExp(bExpectedOpt, scope, sequent.conclusion, reporter)._1,
      steps = for (step <- sequent.steps) yield checkStep(scope, step, reporter)
    )
  }

  def checkStep(scope: Scope.Local, step: AST.ProofAst.Step, reporter: Reporter): AST.ProofAst.Step = {
    def checkJustification(just: AST.ProofAst.Step.Justification): AST.ProofAst.Step.Justification = {
      val errMessage = "Expecting an object @just/@pure method with Unit return type"
      just match {
        case just: AST.ProofAst.Step.Justification.Apply =>
          val (newId, _) = checkExp(None(), scope, just.id, reporter)
          val newJust = just(id = newId, args = for (arg <- just.args) yield checkExp(None(), scope, arg, reporter)._1)
          newJust.resOpt match {
            case Some(res: AST.ResolvedInfo.Method) if res.mode == AST.MethodMode.Just =>
            case Some(_) => reporter.error(newId.posOpt, typeCheckerKind, "Expecting an object @just method")
            case _ =>
          }
          return newJust
        case just: AST.ProofAst.Step.Justification.Incept =>
          val newInvoke = checkExp(None(), scope, just.invoke, reporter)._1.asInstanceOf[AST.Exp.Invoke]
          newInvoke.ident.typedOpt match {
            case Some(t: AST.Typed.Method) if t.isInObject && (t.tpe.isPure || t.mode == AST.MethodMode.Just) && t.tpe.ret == AST.Typed.unit =>
            case Some(_) => reporter.error(newInvoke.posOpt, typeCheckerKind, errMessage)
            case _ =>
          }
          return just(invoke = newInvoke)
        case just: AST.ProofAst.Step.Justification.InceptNamed =>
          val newInvoke = checkExp(None(), scope, just.invoke, reporter)._1.asInstanceOf[AST.Exp.InvokeNamed]
          newInvoke.ident.typedOpt match {
            case Some(t: AST.Typed.Method) if t.isInObject && (t.tpe.isPure || t.mode == AST.MethodMode.Just) && t.tpe.ret == AST.Typed.unit =>
            case Some(_) => reporter.error(newInvoke.posOpt, typeCheckerKind, errMessage)
            case _ =>
          }
          return just(invoke = newInvoke)
        case just: AST.ProofAst.Step.Justification.InceptEta =>
          val newEta = checkExp(None(), scope, just.eta, reporter)._1.asInstanceOf[AST.Exp.Eta]
          newEta.ref.asExp.typedOpt match {
            case Some(t: AST.Typed.Method) if t.isInObject && (t.tpe.isPure || t.mode == AST.MethodMode.Just) && t.tpe.ret == AST.Typed.unit =>
            case Some(_) => reporter.error(newEta.posOpt, typeCheckerKind, errMessage)
            case _ =>
          }
          return just(eta = newEta)
      }
    }
    val bExpectedOpt: Option[AST.Typed] = Some(AST.Typed.b)
    step match {
      case step: AST.ProofAst.Step.Regular =>
        return step(claim = checkExp(bExpectedOpt, scope, step.claim, reporter)._1,
          just = checkJustification(step.just))
      case step: AST.ProofAst.Step.SubProof =>
        return step(steps = for (s <- step.steps) yield checkStep(scope, s, reporter))
      case step: AST.ProofAst.Step.Let =>
        var ok = T
        var sc = createNewScope(scope)
        def declId(id: AST.Id, tOpt: Option[AST.Typed]): Unit = {
          val key = id.value
          if (sc.nameMap.contains(key)) {
            reporter.error(
              id.attr.posOpt,
              typeCheckerKind,
              s"Cannot declare '$key' because the identifier has already been previously declared."
            )
            ok = F
          } else {
            sc = sc(
              nameMap = sc.nameMap + key ~> Info.LocalVar(
                context :+ key,
                F,
                id,
                tOpt,
                Some(AST.ResolvedInfo.LocalVar(context, AST.ResolvedInfo.LocalVar.Scope.Current, T, T, key))
              )
            )
          }
        }
        var newParams = ISZ[AST.ProofAst.Step.Let.Param]()
        for (p <- step.params) {
          val typedOpt: Option[AST.Typed] = p.tipeOpt match {
            case Some(tipe) =>
              val ntOpt = typeHierarchy.typed(scope, tipe, reporter)
              newParams = newParams :+ p(tipeOpt = ntOpt)
              if (ntOpt.isEmpty) None() else ntOpt.get.typedOpt
            case _ =>
              reporter.error(p.id.attr.posOpt, typeCheckerKind, s"${p.id.value} has to be explicitly typed")
              newParams = newParams :+ p
              None()
          }
          declId(p.id, typedOpt)
        }
        return step(params = newParams, steps = for (s <- step.steps) yield checkStep(sc, s, reporter))
      case step: AST.ProofAst.Step.Assume =>
        return step(claim = checkExp(bExpectedOpt, scope, step.claim, reporter)._1)
      case step: AST.ProofAst.Step.Assert =>
        return step(claim = checkExp(bExpectedOpt, scope, step.claim, reporter)._1,
          steps = for (s <- step.steps) yield checkStep(scope, s, reporter))
      case step: AST.ProofAst.Step.StructInduction =>
        val newClaim = checkExp(bExpectedOpt, scope, step.claim, reporter)._1
        val (newExp, tOpt) = checkExp(None(), scope, step.exp, reporter)
        tOpt match {
          case Some(t) =>
            val unrefinedIdOpt: Option[AST.Id] = newExp match {
              case newExp: AST.Exp.Ident => Some(newExp.id)
              case _ => None()
            }
            var newCases = ISZ[AST.ProofAst.Step.StructInduction.MatchCase]()
            for (cas <- step.cases) {
              val (scOpt, newPattern) = checkPattern(T, unrefinedIdOpt, t, scope, cas.pattern, reporter)
              scOpt match {
                case Some(sc) =>
                  val newHypoOpt: Option[AST.ProofAst.Step.Assume] = cas.hypoOpt match {
                    case Some(hypo) => Some(checkStep(sc, hypo, reporter).asInstanceOf[AST.ProofAst.Step.Assume])
                    case _ => None()
                  }
                  newCases = newCases :+ cas(
                    pattern = newPattern.asInstanceOf[AST.Pattern.Structure],
                    hypoOpt = newHypoOpt,
                    steps = for (s <- cas.steps) yield checkStep(sc, s, reporter)
                  )
                case _ =>
                  newCases = newCases :+ cas(
                    pattern = newPattern.asInstanceOf[AST.Pattern.Structure]
                  )
              }
            }
            val newDefaultOpt: Option[AST.ProofAst.Step.StructInduction.MatchDefault] = step.defaultOpt match {
              case Some(default) =>
                val newHypoOpt: Option[AST.ProofAst.Step.Assume] = default.hypoOpt match {
                  case Some(hypo) => Some(checkStep(scope, hypo, reporter).asInstanceOf[AST.ProofAst.Step.Assume])
                  case _ => None()
                }
                Some(default(hypoOpt = newHypoOpt, steps = for (s <- default.steps) yield checkStep(scope, s, reporter)))
              case _ => None()
            }
            return step(claim = newClaim, exp = newExp, cases = newCases, defaultOpt = newDefaultOpt)
          case _ =>
            return step(claim = newClaim, exp = newExp)
        }
    }
  }

  def checkMethodContract(scope: Scope, contract: AST.MethodContract, reporter: Reporter): AST.MethodContract = {
    assert(inSpec)
    def checkReads(reads: AST.MethodContract.Accesses): AST.MethodContract.Accesses = {
      return reads(idents = for (read <- reads.idents) yield checkExp(None(), scope, read, reporter)._1.asInstanceOf[AST.Exp.Ident])
    }
    def checkRequires(requires: AST.MethodContract.Claims): AST.MethodContract.Claims = {
      return requires(claims = for (require <- requires.claims) yield checkExp(Some(AST.Typed.b), scope, require, reporter)._1)
    }
    def checkEnsures(ensures: AST.MethodContract.Claims): AST.MethodContract.Claims = {
      val tc = TypeChecker(typeHierarchy, context, isInMutableContext, ModeContext.SpecPost, strictAliasing)
      return ensures(claims = for (ensure <- ensures.claims) yield tc.checkExp(Some(AST.Typed.b), scope, ensure, reporter)._1)
    }
    def checkCase(cas: AST.MethodContract.Case): AST.MethodContract.Case = {
      return AST.MethodContract.Case(cas.label, checkRequires(cas.requiresClause), checkEnsures(cas.ensuresClause))
    }
    contract match {
      case contract: AST.MethodContract.Simple =>
        return AST.MethodContract.Simple(
          checkReads(contract.readsClause), checkRequires(contract.requiresClause),
          checkModifies("modify", scope, contract.modifiesClause, reporter), checkEnsures(contract.ensuresClause),
          contract.attr
        )
      case contract: AST.MethodContract.Cases =>
        val newReads = checkReads(contract.readsClause)
        val newModifies = checkModifies("modify", scope, contract.modifiesClause, reporter)
        var newCases = ISZ[AST.MethodContract.Case]()
        var labelMap = HashMap.empty[String, Option[Position]]
        for (cas <- contract.cases) {
          newCases = newCases :+ checkCase(cas)
          val label = cas.label.value
          if (label != "") {
            labelMap.get(label) match {
              case Some(Some(pos)) =>
                reporter.error(cas.label.attr.posOpt, typeCheckerKind,
                  s"Contract case with label '$label' was previously defined at [${pos.beginLine}, ${pos.beginColumn}].")
              case Some(_) =>
                reporter.error(cas.label.attr.posOpt, typeCheckerKind,
                  s"Contract case with label '$label' was previously defined.")
              case _ =>
                labelMap = labelMap + label ~> cas.label.attr.posOpt
            }
          }
        }
        return AST.MethodContract.Cases(newReads, newModifies, newCases, contract.attr)
    }
  }

  def checkLoopInv(scope: Scope, invs: ISZ[AST.Exp], modifies: ISZ[AST.Exp.Ident],
                   reporter: Reporter): (ISZ[AST.Exp], ISZ[AST.Exp.Ident]) = {
    val newInvs: ISZ[AST.Exp] = for (inv <- invs) yield checkExp(AST.Typed.bOpt, scope, inv, reporter)._1
    return (newInvs, checkModifiesH("modify", scope, modifies, reporter))
  }

  def checkModifies(title: String, scope: Scope, modifies: AST.MethodContract.Accesses, reporter: Reporter): AST.MethodContract.Accesses = {
    return modifies(idents = checkModifiesH(title, scope, modifies.idents, reporter))
  }

  def checkModifiesH(title: String, scope: Scope, modifies: ISZ[AST.Exp.Ident], reporter: Reporter): ISZ[AST.Exp.Ident] = {
    return for (modify <- modifies) yield checkModifyExp(title, scope, modify, reporter)
  }

  def checkModifyExp(title: String, sc: Scope, exp: AST.Exp.Ident, reporter: Reporter): AST.Exp.Ident = {
    def checkLocalVar(res: AST.ResolvedInfo.LocalVar, t: AST.Typed): Unit = {
      if (!typeHierarchy.isModifiable(t, T) && res.isVal) {
        reporter.error(exp.posOpt, typeCheckerKind, s"Cannot $title variable '${res.id}'")
      }
    }
    def checkVar(res: AST.ResolvedInfo.Var, t: AST.Typed): Unit = {
      if (!typeHierarchy.isModifiable(t, T) && res.isVal) {
        reporter.error(exp.posOpt, typeCheckerKind, st"Cannot $title field '${(res.owner, ".")}.${res.id}'".render)
      }
    }
    def err(): Unit = {
      reporter.error(exp.posOpt, typeCheckerKind, s"Can only $title a variable or a field")
    }
    val (newExp, tOpt) = checkExp(None(), sc, exp, reporter)
    tOpt match {
      case Some(t) =>
        newExp match {
          case newExp: AST.Exp.Ident =>
            newExp.attr.resOpt.get match {
              case res: AST.ResolvedInfo.LocalVar => checkLocalVar(res, t)
              case res: AST.ResolvedInfo.Var => checkVar(res, t)
              case _ => err()
            }
            return newExp
          case _ => err()
        }
      case _ => err()
    }
    return exp
  }

  def checkMethod(sc: Scope, stmt: AST.Stmt.Method, reporter: Reporter): AST.Stmt.Method = {
    def checkMethodH(): AST.Stmt.Method = {
      if (stmt.bodyOpt.isEmpty) {
        return stmt
      }
      val (ok, scope) = methodScope(typeHierarchy, context, sc, stmt.sig, reporter)
      if (ok) {
        val (_, newBody) = checkBody(F, None(), scope, stmt.bodyOpt.get, reporter)
        return stmt(typeChecked = T, bodyOpt = Some(newBody))
      } else {
        return stmt
      }
    }
    val r = checkMethodH()
    if (!reporter.hasError && r.purity == AST.Purity.StrictPure) {
      val spc = TypeChecker.StrictPureChecker(isInMutableContext, typeHierarchy, Reporter.create)
      spc.transformStmt(r)
      reporter.reports(spc.reporter.messages)
    }
    return r
  }

  def checkDataRefinement(scope: Scope, stmt: AST.Stmt.DataRefinement, reporter: Reporter): AST.Stmt.DataRefinement = {
    val rep = checkModifyExp("represent data using", scope, stmt.rep, reporter)
    val refs = checkModifiesH("refine data using", scope, stmt.refs, reporter)
    var claims = ISZ[AST.Exp]()
    val expectedOpt: Option[AST.Typed] = Some(AST.Typed.b)
    for (claim <- stmt.claims) {
      val (newClaim, _) = checkExp(expectedOpt, scope, claim, reporter)
      claims = claims :+ newClaim
    }
    return stmt(rep = rep, refs = refs, claims = claims)
  }

  def checkAdt(info: TypeInfo.Adt): TypeHierarchy => (TypeHierarchy, ISZ[Message]) @pure = {
    assert(info.outlined, st"${(info.name, ".")} is not outlined".render)
    val reporter = Reporter.create
    val typeParams = typeParamMap(info.ast.typeParams, reporter)
    var scope = Scope.Local.create(typeParams.map, info.scope)
    scope = scope(localThisOpt = Some(info.tpe))
    val newStmts = checkStmts(ISZ(scope), None(), info.ast.stmts, reporter)
    var specVars: HashSMap[String, Info.SpecVar] = info.specVars
    var vars: HashSMap[String, Info.Var] = info.vars
    var specMethods: HashSMap[String, Info.SpecMethod] = info.specMethods
    var methods: HashSMap[String, Info.Method] = info.methods
    for (stmt <- newStmts) {
      stmt match {
        case stmt: AST.Stmt.Var =>
          val id = stmt.id.value
          val vInfo = info.vars.get(id).get
          vars = vars + id ~> vInfo(ast = stmt)
        case stmt: AST.Stmt.SpecVar =>
          val id = stmt.id.value
          val svInfo = info.specVars.get(id).get
          specVars = specVars + id ~> svInfo(ast = stmt)
        case stmt: AST.Stmt.Method =>
          val id = stmt.sig.id.value
          val mInfo = info.methods.get(id).get
          methods = methods + id ~> mInfo(ast = stmt)
        case stmt: AST.Stmt.SpecMethod =>
          val id = stmt.sig.id.value
          val smInfo = info.specMethods.get(id).get
          specMethods = specMethods + id ~> smInfo(ast = stmt)
        case _ =>
      }
    }
    if (info.ast.isDatatype) {
      val transformer = AST.Transformer(TypeChecker.TypeFinder(typeHierarchy, info.name))
      for (v <- vars.values if !info.extractorTypeMap.contains(v.ast.id.value)) {
        v.typedOpt match {
          case Some(t) =>
            val r = transformer.transformTyped(F, t)
            if (r.ctx) {
              reporter.error(v.ast.tipeOpt.get.posOpt, TypeChecker.typeCheckerKind,
                st"@datatype class ${(info.name, ".")} cannot have a non-constructor field whose type contains ${(info.name, ".")} or its super-type.".render)
            }
          case _ =>
        }
      }
    }
    val messages = reporter.messages
    val newInfo = info(
      typeChecked = T,
      ast = info.ast(stmts = newStmts),
      vars = vars,
      specVars = specVars,
      methods = methods,
      specMethods = specMethods
    )
    return (th: TypeHierarchy) => (th(typeMap = th.typeMap + info.name ~> newInfo), messages)
  }

  def checkSig(info: TypeInfo.Sig): TypeHierarchy => (TypeHierarchy, ISZ[Message]) @pure = {
    assert(info.outlined, st"${(info.name, ".")} is not outlined".render)
    val reporter = Reporter.create
    val typeParams = typeParamMap(info.ast.typeParams, reporter)
    var scope = Scope.Local.create(typeParams.map, info.scope)
    scope = scope(localThisOpt = Some(info.tpe))
    val newStmts = checkStmts(ISZ(scope), None(), info.ast.stmts, reporter)
    var specVars: HashSMap[String, Info.SpecVar] = info.specVars
    var specMethods: HashSMap[String, Info.SpecMethod] = info.specMethods
    var methods: HashSMap[String, Info.Method] = info.methods
    for (stmt <- newStmts) {
      stmt match {
        case stmt: AST.Stmt.SpecVar =>
          val id = stmt.id.value
          val svInfo = info.specVars.get(id).get
          specVars = specVars + id ~> svInfo(ast = stmt)
        case stmt: AST.Stmt.Method =>
          val id = stmt.sig.id.value
          val mInfo = info.methods.get(id).get
          methods = methods + id ~> mInfo(ast = stmt)
        case stmt: AST.Stmt.SpecMethod =>
          val id = stmt.sig.id.value
          val smInfo = info.specMethods.get(id).get
          specMethods = specMethods + id ~> smInfo(ast = stmt)
        case _ =>
      }
    }
    val messages = reporter.messages
    val newInfo = info(
      typeChecked = T,
      ast = info.ast(stmts = newStmts),
      specVars = specVars,
      methods = methods,
      specMethods = specMethods
    )
    return (th: TypeHierarchy) => (th(typeMap = th.typeMap + info.name ~> newInfo), messages)
  }

  def checkObject(info: Info.Object): TypeHierarchy => (TypeHierarchy, ISZ[Message]) @pure = {
    assert(info.outlined, st"${(info.name, ".")} is not outlined".render)
    val name = info.name
    @pure def getInfo(id: String): Info = {
      return typeHierarchy.nameMap.get(name :+ id).get
    }
    val reporter = Reporter.create
    var scope = createNewScope(info.scope(enclosingName = name))
    scope = scope(localThisOpt = Some(AST.Typed.Object(info.owner, info.ast.id.value)))
    var stmtOpts = ISZ[Option[AST.Stmt]]()
    for (stmt <- info.ast.stmts) {
      stmt match {
        case stmt: AST.Stmt.Var => stmtOpts = stmtOpts :+ (if (info.ast.extNameOpt.nonEmpty) None() else Some(stmt))
        case stmt: AST.Stmt.SpecVar => stmtOpts = stmtOpts :+ Some(stmt)
        case stmt: AST.Stmt.Method => stmtOpts = stmtOpts :+ Some(stmt)
        case stmt: AST.Stmt.SpecMethod => stmtOpts = stmtOpts :+ Some(stmt)
        case stmt: AST.Stmt.ExtMethod => stmtOpts = stmtOpts :+ Some(stmt)
        case stmt: AST.Stmt.JustMethod => stmtOpts = stmtOpts :+ Some(stmt)
        case _: AST.Stmt.Fact =>  stmtOpts = stmtOpts :+ None()
        case _: AST.Stmt.Theorem =>  stmtOpts = stmtOpts :+ None()
        case _: AST.Stmt.Inv =>  stmtOpts = stmtOpts :+ None()
        case _: AST.Stmt.Sig => stmtOpts = stmtOpts :+ None()
        case _: AST.Stmt.Adt => stmtOpts = stmtOpts :+ None()
        case _: AST.Stmt.Object => stmtOpts = stmtOpts :+ None()
        case _ => stmtOpts = stmtOpts :+ Some(stmt)
      }
    }
    val newStmtOpts = checkStmtOpts(scope, stmtOpts, reporter)
    var i = 0
    var newStmts = ISZ[AST.Stmt]()
    var nameEntries = ISZ[(QName, Info)]()
    for (stmtOpt <- newStmtOpts) {
      stmtOpt match {
        case Some(stmt) =>
          stmt match {
            case stmt: AST.Stmt.Var =>
              val v = getInfo(stmt.id.value).asInstanceOf[Info.Var]
              nameEntries = nameEntries :+ (v.name ~> v(ast = stmt))
            case stmt: AST.Stmt.SpecVar =>
              val sv = getInfo(stmt.id.value).asInstanceOf[Info.SpecVar]
              nameEntries = nameEntries :+ (sv.name ~> sv(ast = stmt))
            case stmt: AST.Stmt.Method =>
              val m = getInfo(stmt.sig.id.value).asInstanceOf[Info.Method]
              nameEntries = nameEntries :+ (m.name ~> m(ast = stmt))
            case stmt: AST.Stmt.SpecMethod =>
              val sm = getInfo(stmt.sig.id.value).asInstanceOf[Info.SpecMethod]
              nameEntries = nameEntries :+ (sm.name ~> sm(ast = stmt))
            case stmt: AST.Stmt.ExtMethod =>
              val em = getInfo(stmt.sig.id.value).asInstanceOf[Info.ExtMethod]
              nameEntries = nameEntries :+ (em.name ~> em(ast = stmt))
            case stmt: AST.Stmt.JustMethod =>
              val em = getInfo(stmt.sig.id.value).asInstanceOf[Info.JustMethod]
              nameEntries = nameEntries :+ (em.name ~> em(ast = stmt))
            case _ =>
          }
          newStmts = newStmts :+ stmt
        case _ => newStmts = newStmts :+ info.ast.stmts(i)
      }
      i = i + 1
    }
    nameEntries = nameEntries :+ (info.name ~> info(ast = info.ast(stmts = newStmts)))
    val messages = reporter.messages
    val newInfos = nameEntries
    return (th: TypeHierarchy) => (th(nameMap = th.nameMap ++ newInfos), messages)
  }
}
