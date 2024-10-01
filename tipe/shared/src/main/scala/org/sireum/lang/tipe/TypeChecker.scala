// #Sireum
/*
 Copyright (c) 2017-2024, Robby, Kansas State University
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
    "SetOptions"
  }

  @enum object TypeRelation {
    "Subtype"
    "Equal"
    "Supertype"
  }

  @enum object ModeContext {
    "Code"
    "TheoremCode"
    "Spec"
    "SpecPost"
    "RS"
  }

  @record class StrictPureChecker(val isMethod: B,
                                  val messageKind: String,
                                  val th: TypeHierarchy,
                                  val reporter: Reporter) extends AST.MTransformer {
    val messagePrefix: String = if (isMethod) "@strictpure methods" else "Strict-pure blocks"

    def errVars(posOpt: Option[Position]): Unit = {
      reporter.error(posOpt, TypeChecker.typeCheckerKind, "@strictpure methods cannot refer to vars")
    }
    def checkType(t: AST.Typed, posOpt: Option[Position]): Unit = {
      if (th.isMutable(t)) {
        reporter.error(posOpt, TypeChecker.typeCheckerKind, "@strictpure methods cannot refer to outer vals of mutable type")
      }
    }

    override def postStmtMethod(o: AST.Stmt.Method): MOption[AST.Stmt] = {
      reporter.error(o.posOpt, messageKind, s"$messagePrefix methods cannot define nested methods")
      return AST.MTransformer.PostResultStmtMethod
    }

    override def postStmtVar(o: AST.Stmt.Var): MOption[AST.Stmt] = {
      if (!o.isVal) {
        reporter.error(o.posOpt, messageKind, s"$messagePrefix methods cannot define vars")
      }
      return AST.MTransformer.PostResultStmtVar
    }

    override def postStmtWhile(o: AST.Stmt.While): MOption[AST.Stmt] = {
      reporter.error(o.posOpt, messageKind, s"$messagePrefix cannot use while-loops")
      return AST.MTransformer.PostResultStmtWhile
    }

    override def postStmtFor(o: AST.Stmt.For): MOption[AST.Stmt] = {
      reporter.error(o.posOpt, messageKind, s"$messagePrefix cannot use for-loops")
      return AST.MTransformer.PostResultStmtFor
    }

    override def postStmtVarPattern(o: AST.Stmt.VarPattern): MOption[AST.Stmt] = {
      if (!o.isVal) {
        reporter.error(o.posOpt, messageKind, s"$messagePrefix cannot define vars")
      }
      return AST.MTransformer.PostResultStmtVarPattern
    }

    override def postStmtSpecVar(o: AST.Stmt.SpecVar): MOption[AST.Stmt.Spec] = {
      reporter.error(o.posOpt, messageKind, s"$messagePrefix cannot define @spec val/var")
      return AST.MTransformer.PostResultStmtSpecVar
    }

    override def postStmtSpecBlock(o: AST.Stmt.SpecBlock): MOption[AST.Stmt.Spec] = {
      reporter.error(o.posOpt, messageKind, s"$messagePrefix cannot use Spec { ... } blocks")
      return AST.MTransformer.PostResultStmtSpecBlock
    }

    override def postStmtSpecLabel(o: AST.Stmt.SpecLabel): MOption[AST.Stmt.Spec] = {
      reporter.error(o.posOpt, messageKind, s"$messagePrefix cannot use spec labels")
      return AST.MTransformer.PostResultStmtSpecLabel
    }

    override def postStmtAssign(o: AST.Stmt.Assign): MOption[AST.Stmt] = {
      reporter.error(o.posOpt, messageKind, s"$messagePrefix cannot use assignments")
      return AST.MTransformer.PostResultStmtAssign
    }

    override def postStmtReturn(o: AST.Stmt.Return): MOption[AST.Stmt] = {
      reporter.error(o.posOpt, messageKind, s"$messagePrefix cannot have returns")
      return AST.MTransformer.PostResultStmtReturn
    }

    override def postResolvedAttr(o: AST.ResolvedAttr): MOption[AST.ResolvedAttr] = {
      o.resOpt match {
        case Some(res) =>
          res match {
            case res: AST.ResolvedInfo.Var if res.isInObject =>
              if (isMethod) {
                if (!res.isVal) {
                  errVars(o.posOpt)
                } else {
                  checkType(o.typedOpt.get, o.posOpt)
                }
              }
            case res: AST.ResolvedInfo.Method =>
              def err(): Unit = {
                reporter.error(o.posOpt, TypeChecker.typeCheckerKind, s"$messagePrefix cannot invoke impure methods")
              }
              if (res.isInObject) {
                res.owner match {
                  case AST.Typed.msName =>
                  case AST.Typed.isName =>
                  case AST.Typed.iszName =>
                  case AST.Typed.mszName =>
                  case _ =>
                    th.nameMap.get(res.owner :+ res.id) match {
                      case Some(info: Info.Method) if info.ast.purity == AST.Purity.Impure => err()
                      case Some(info: Info.ExtMethod) if !info.ast.isPure => err()
                      case _ =>
                    }
                }
              } else {
                th.typeMap.get(res.owner) match {
                  case Some(info: TypeInfo.Sig) =>
                    info.methods.get(res.id) match {
                      case Some(minfo) =>
                        if (minfo.ast.purity == AST.Purity.Impure) {
                          err()
                        }
                      case _ =>
                    }
                  case Some(info: TypeInfo.Adt) =>
                    info.methods.get(res.id) match {
                      case Some(minfo) if minfo.ast.purity == AST.Purity.Impure => err()
                      case _ =>
                    }
                  case _ =>
                }
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
          if (isMethod) {
            if (!res.isVal) {
              errVars(attr.posOpt)
            } else {
              checkType(attr.typedOpt.get, attr.posOpt)
            }
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

  val sTypeParams: ISZ[String] = ISZ("I", "V")

  val builtInMethods: HashSet[String] =
    HashSet ++ ISZ("assert", "assume", "println", "print", "cprintln", "cprint", "eprintln", "eprint", "halt", "setOptions")
  val assertResOpt: Option[AST.ResolvedInfo] = Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Assert))

  val assertMsgResOpt: Option[AST.ResolvedInfo] = Some(
    AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.AssertMsg)
  )
  val assumeResOpt: Option[AST.ResolvedInfo] = Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Assume))

  val assumeMsgResOpt: Option[AST.ResolvedInfo] = Some(
    AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.AssumeMsg)
  )
  val assertumeTypedOpt: Option[AST.Typed] = Some(AST.Typed.Fun(AST.Purity.Impure,F, ISZ(AST.Typed.b), AST.Typed.unit))

  val assertumeMsgTypedOpt: Option[AST.Typed] = Some(
    AST.Typed.Fun(AST.Purity.Impure,F, ISZ(AST.Typed.b, AST.Typed.string), AST.Typed.unit)
  )
  val printlnResOpt: Option[AST.ResolvedInfo] = Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Println))
  val printResOpt: Option[AST.ResolvedInfo] = Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Print))
  val cprintlnResOpt: Option[AST.ResolvedInfo] = Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Cprintln))
  val cprintResOpt: Option[AST.ResolvedInfo] = Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Cprint))
  val eprintlnResOpt: Option[AST.ResolvedInfo] = Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Eprintln))
  val eprintResOpt: Option[AST.ResolvedInfo] = Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Eprint))
  val haltResOpt: Option[AST.ResolvedInfo] = Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Halt))

  val haltTypedOpt: Option[AST.Typed] = Some(AST.Typed.Fun(AST.Purity.Impure,F, ISZ(AST.Typed.string), AST.Typed.nothing))
  val nativeResOpt: Option[AST.ResolvedInfo] = Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Native))
  val nativeCTypedOpt: Option[AST.Typed] = Some(AST.Typed.Fun(AST.Purity.Impure,T, ISZ(), AST.Typed.c))
  val nativeStringTypedOpt: Option[AST.Typed] = Some(AST.Typed.Fun(AST.Purity.Impure,T, ISZ(), AST.Typed.string))
  val nativeF32TypedOpt: Option[AST.Typed] = Some(AST.Typed.Fun(AST.Purity.Impure,T, ISZ(), AST.Typed.f32))
  val nativeF64TypedOpt: Option[AST.Typed] = Some(AST.Typed.Fun(AST.Purity.Impure,T, ISZ(), AST.Typed.f64))
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

  val indicesResOpt: Option[AST.ResolvedInfo] = Some(
    AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Indices)
  )

  val unopResOpt: HashMap[AST.Exp.UnaryOp.Type, Option[AST.ResolvedInfo]] = HashMap ++ ISZ[(AST.Exp.UnaryOp.Type, Option[AST.ResolvedInfo])](
    AST.Exp.UnaryOp.Plus ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.UnaryPlus)),
    AST.Exp.UnaryOp.Minus ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.UnaryMinus)),
    AST.Exp.UnaryOp.Not ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.UnaryNot)),
    AST.Exp.UnaryOp.Complement ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.UnaryComplement))
  )

  val binopResOpt: HashMap[String, Option[AST.ResolvedInfo]] = HashMap ++ ISZ[(String, Option[AST.ResolvedInfo])](
    AST.Exp.BinaryOp.Add ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryAdd)),
    AST.Exp.BinaryOp.Sub ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinarySub)),
    AST.Exp.BinaryOp.Mul ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryMul)),
    AST.Exp.BinaryOp.Div ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryDiv)),
    AST.Exp.BinaryOp.Rem ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryRem)),
    AST.Exp.BinaryOp.Eq ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryEq)),
    AST.Exp.BinaryOp.Equiv ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryEquiv)),
    AST.Exp.BinaryOp.EquivUni ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryEquiv)),
    AST.Exp.BinaryOp.Ne ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryNe)),
    AST.Exp.BinaryOp.Inequiv ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryInequiv)),
    AST.Exp.BinaryOp.InequivUni ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryInequiv)),
    AST.Exp.BinaryOp.FpEq ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryFpEq)),
    AST.Exp.BinaryOp.FpNe ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryFpNe)),
    AST.Exp.BinaryOp.Shl ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryShl)),
    AST.Exp.BinaryOp.Shr ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryShr)),
    AST.Exp.BinaryOp.Ushr ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryUshr)),
    AST.Exp.BinaryOp.Lt ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryLt)),
    AST.Exp.BinaryOp.Le ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryLe)),
    AST.Exp.BinaryOp.Gt ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryGt)),
    AST.Exp.BinaryOp.Ge ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryGe)),
    AST.Exp.BinaryOp.And ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryAnd)),
    AST.Exp.BinaryOp.Or ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryOr)),
    AST.Exp.BinaryOp.Xor ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryXor)),
    AST.Exp.BinaryOp.Imply ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryImply)),
    string"->:" ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryImply)),
    AST.Exp.BinaryOp.CondAnd ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryCondAnd)),
    AST.Exp.BinaryOp.CondOr ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryCondOr)),
    AST.Exp.BinaryOp.CondImply ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryCondImply)),
    string"-->:" ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryCondImply)),
    AST.Exp.BinaryOp.MapsTo ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryMapsTo)),
    AST.Exp.BinaryOp.Arrow ~> Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Arrow))
  )

  val eqBinops: HashSet[String] = HashSet ++ ISZ[String](
    AST.Exp.BinaryOp.Eq, AST.Exp.BinaryOp.Equiv, AST.Exp.BinaryOp.EquivUni,
    AST.Exp.BinaryOp.Ne, AST.Exp.BinaryOp.Inequiv, AST.Exp.BinaryOp.InequivUni
  )

  val emptySubstMap: HashMap[String, AST.Typed] = HashMap.empty

  val setOptionsResOpt: Option[AST.ResolvedInfo] = Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.SetOptions))
  val setOptionsTypedOpt: Option[AST.Typed] = Some(AST.Typed.Fun(AST.Purity.Impure,F, ISZ(AST.Typed.string, AST.Typed.string), AST.Typed.unit))

  @strictpure def extResOpt(isInObject: B, owner: ISZ[String], id: String, paramNames: ISZ[String],
                            tpe: AST.Typed.Fun): Option[AST.ResolvedInfo] = Some(
    AST.ResolvedInfo.Method(isInObject, AST.MethodMode.Ext, ISZ(), owner, id, paramNames, Some(tpe), ISZ(), ISZ()))

  def sConstructorTypedResOpt(name: ISZ[String], numOfArgs: Z): (Option[AST.Typed], Option[AST.ResolvedInfo]) = {
    name match {
      case AST.Typed.`isName` =>
        val indexTypeVar = AST.Typed.TypeVar("I", AST.Typed.VarKind.Index)
        val valueTypeVar = AST.Typed.TypeVar("V", AST.Typed.VarKind.Immutable)
        val argTypes: ISZ[AST.Typed] = for (_ <- z"0" until numOfArgs) yield valueTypeVar
        val constructorType = AST.Typed.Fun(AST.Purity.Pure,F, argTypes, AST.Typed.Name(AST.Typed.isName, ISZ(indexTypeVar, valueTypeVar)))
        return (
          Some(AST.Typed.Method(T, AST.MethodMode.Constructor, sTypeParams, AST.Typed.sireumName, "IS", ISZ(),
            constructorType)),
          Some(AST.ResolvedInfo.Method(T, AST.MethodMode.Constructor, sTypeParams, AST.Typed.sireumName, "IS", ISZ(),
            Some(constructorType), ISZ(), ISZ()))
        )
      case AST.Typed.`msName` =>
        val indexTypeVar = AST.Typed.TypeVar("I", AST.Typed.VarKind.Index)
        val valueTypeVar = AST.Typed.TypeVar("V", AST.Typed.VarKind.Mutable)
        val argTypes: ISZ[AST.Typed] = for (_ <- z"0" until numOfArgs) yield valueTypeVar
        val constructorType = AST.Typed.Fun(AST.Purity.Pure,F, argTypes, AST.Typed.Name(AST.Typed.msName, ISZ(indexTypeVar, valueTypeVar)))
        return (
          Some(AST.Typed.Method(T, AST.MethodMode.Constructor, sTypeParams, AST.Typed.sireumName, "MS", ISZ(), constructorType)),
          Some(AST.ResolvedInfo.Method(T, AST.MethodMode.Constructor, sTypeParams, AST.Typed.sireumName, "MS", ISZ(),
            Some(constructorType), ISZ(), ISZ()))
        )
      case AST.Typed.`iszName` =>
        val valueTypeVar = AST.Typed.TypeVar("V", AST.Typed.VarKind.Immutable)
        val argTypes: ISZ[AST.Typed] = for (_ <- z"0" until numOfArgs) yield valueTypeVar
        val constructorType = AST.Typed.Fun(AST.Purity.Pure,F, argTypes, AST.Typed.Name(AST.Typed.isName, ISZ(AST.Typed.z, valueTypeVar)))
        val typeParams = ISZ[String]("V")
        return (
          Some(AST.Typed.Method(T, AST.MethodMode.Constructor, typeParams, AST.Typed.sireumName, "IS", ISZ(), constructorType)),
          Some(AST.ResolvedInfo.Method(T, AST.MethodMode.Constructor, typeParams, AST.Typed.sireumName, "IS", ISZ(),
            Some(constructorType), ISZ(), ISZ()))
        )
      case AST.Typed.`mszName` =>
        val valueTypeVar = AST.Typed.TypeVar("V", AST.Typed.VarKind.Mutable)
        val argTypes: ISZ[AST.Typed] = for (_ <- z"0" until numOfArgs) yield valueTypeVar
        val constructorType = AST.Typed.Fun(AST.Purity.Pure,F, argTypes, AST.Typed.Name(AST.Typed.msName, ISZ(AST.Typed.z, valueTypeVar)))
        val typeParams = ISZ[String]("V")
        return (
          Some(AST.Typed.Method(T, AST.MethodMode.Constructor, typeParams, AST.Typed.sireumName, "MS", ISZ(), constructorType)),
          Some(AST.ResolvedInfo.Method(T, AST.MethodMode.Constructor, typeParams, AST.Typed.sireumName, "MS", ISZ(),
            Some(constructorType), ISZ(), ISZ()))
        )
      case AST.Typed.`zsName` =>
        val argTypes: ISZ[AST.Typed] = for (_ <- z"0" until numOfArgs) yield AST.Typed.z
        val constructorType = AST.Typed.Fun(AST.Purity.Pure,F, argTypes, AST.Typed.Name(AST.Typed.msName, ISZ(AST.Typed.z, AST.Typed.z)))
        return (
          Some(AST.Typed.Method(T, AST.MethodMode.Constructor, ISZ(), AST.Typed.sireumName, "IS", ISZ(), constructorType)),
          Some(AST.ResolvedInfo.Method(T, AST.MethodMode.Constructor, ISZ(), AST.Typed.sireumName, "IS", ISZ(),
            Some(constructorType), ISZ(), ISZ()))
        )
      case _ => return (None(), None())
    }
  }

  def adtCopyTypedResOpt(isCode: B, typeHierarchy: TypeHierarchy, posOpt: Option[Position], tpe: AST.Typed.Name,
                         argNames: ISZ[String], reporter: Reporter): (Option[AST.Typed], Option[AST.ResolvedInfo], B, ISZ[String]) = {
    if (!isCode && argNames.isEmpty) {
      reporter.error(posOpt, typeCheckerKind, st"Cannot perform copy of '$tpe' without argument.".render)
      return (None(), None(), T, ISZ())
    }
    val (t, paramNames, paramTypes): (AST.Typed.Name, ISZ[String], ISZ[AST.Typed]) = typeHierarchy.typeMap.get(tpe.ids) match {
      case Some(info: TypeInfo.Adt) =>
        if (info.ast.isRoot) {
          if (isCode) {
            return (None(), None(), F, ISZ())
          }
          val pns = info.specVars.keys
          val pts: ISZ[AST.Typed] = for (v <- info.specVars.values) yield v.typedOpt.get
          (info.tpe, pns, pts)
        } else {
          if (isCode) {
            val pns: ISZ[String] = for (p <- info.ast.params) yield p.id.value
            val pts: ISZ[AST.Typed] = for (p <- info.ast.params) yield p.tipe.typedOpt.get
            (info.tpe, pns, pts)
          } else {
            var pns: ISZ[String] = for (p <- info.ast.params) yield p.id.value
            var pts: ISZ[AST.Typed] = for (p <- info.ast.params) yield p.tipe.typedOpt.get
            for (v <- (info.vars -- pns).values if !v.ast.isVal) {
              pns = pns :+ v.ast.id.value
              pts = pts :+ v.typedOpt.get
            }
            for (v <- (info.specVars -- pns).values if !v.ast.isVal) {
              pns = pns :+ v.ast.id.value
              pts = pts :+ v.typedOpt.get
            }
            (info.tpe, pns, pts)
          }
        }
      case Some(info: TypeInfo.Sig) if !isCode =>
        val pns = info.specVars.keys
        val pts: ISZ[AST.Typed] = for (v <- info.specVars.values) yield v.typedOpt.get
        (info.tpe, pns, pts)
      case _ => return (None(), None(), F, ISZ())
    }
    val paramNameIndexMap = HashMap.empty[String, Z] ++ (for (i <- paramNames.indices) yield (paramNames(i), i))
    var pns = ISZ[String]()
    var pts = ISZ[AST.Typed]()
    for (argName <- if (isCode && argNames.isEmpty) paramNames else argNames) {
      paramNameIndexMap.get(argName) match {
        case Some(i) =>
          pns = pns :+ argName
          pts = pts :+ paramTypes(i)
        case _ =>
          reporter.error(posOpt, typeCheckerKind, st"Could not find parameter '$argName' in '$tpe'.".render)
      }
    }
    if (pns.isEmpty && paramNames.nonEmpty) {
      reporter.error(posOpt, typeCheckerKind, st"Cannot perform copy of '$tpe' without argument.".render)
      return (None(), None(), T, ISZ())
    }
    val smOpt = unify(typeHierarchy, posOpt, TypeRelation.Equal, tpe, t, reporter)
    smOpt match {
      case Some(sm) =>
        val copyType = AST.Typed.Fun(AST.Purity.Pure,F, pts, tpe).subst(sm)
        val owner = ops.ISZOps(t.ids).dropRight(1)
        val id = t.ids(t.ids.size - 1)
        return (
          Some(AST.Typed.Method(F, AST.MethodMode.Copy, ISZ(), owner, id, pns, copyType)),
          Some(AST.ResolvedInfo.Method(F, AST.MethodMode.Copy, ISZ(), owner, id, pns,
            Some(copyType), ISZ(), ISZ())),
          T,
          paramNames
        )
      case _ => return (None(), None(), T, ISZ())
    }
  }

  def sStoreTypedResOpt(tpe: AST.Typed.Name, numOfArgs: Z): (Option[AST.Typed], Option[AST.ResolvedInfo]) = {
    val indexType = tpe.args(0)
    val valueType = tpe.args(1)
    val tupleVars = AST.Typed.Tuple(ISZ(indexType, valueType))
    val argTypes: ISZ[AST.Typed] = for (_ <- z"0" until numOfArgs)
      yield tupleVars
    val storeType = AST.Typed.Fun(AST.Purity.Pure,F, argTypes, tpe)
    val id = tpe.ids(tpe.ids.size - 1)
    return (
      Some(AST.Typed.Method(F, AST.MethodMode.Store, ISZ(), AST.Typed.sireumName, id, ISZ(), storeType)),
      Some(AST.ResolvedInfo.Method(F, AST.MethodMode.Store, ISZ(), AST.Typed.sireumName, id, ISZ(), Some(storeType),
        ISZ(), ISZ()))
    )
  }

  def sSelectTypedResOpt(tpe: AST.Typed.Name, storeSelect: B): (Option[AST.Typed], Option[AST.ResolvedInfo]) = {
    val indexType = tpe.args(0)
    val valueType = tpe.args(1)
    val tupleVars = AST.Typed.Tuple(ISZ(indexType, valueType))
    val storeType = AST.Typed.Fun(AST.Purity.Pure,F, ISZ(tupleVars), tpe)
    val selectType = AST.Typed.Fun(AST.Purity.Pure,F, ISZ(indexType), valueType)
    val id = tpe.ids(tpe.ids.size - 1)
    val selectTypeMethod = AST.Typed.Method(F, AST.MethodMode.Select, ISZ(), AST.Typed.sireumName, id, ISZ(), selectType)
    val selectResMethod = AST.ResolvedInfo.Method(F, AST.MethodMode.Select, ISZ(), AST.Typed.sireumName, id, ISZ(),
      Some(selectType), ISZ(), ISZ())
    if (storeSelect) {
      return (
        Some(AST.Typed.Methods(ISZ(
          selectTypeMethod,
          AST.Typed.Method(F, AST.MethodMode.Store, ISZ(), AST.Typed.sireumName, id, ISZ(), storeType)))),
        Some(AST.ResolvedInfo.Methods(ISZ(
          selectResMethod,
          AST.ResolvedInfo.Method(F, AST.MethodMode.Store, ISZ(), AST.Typed.sireumName, id, ISZ(),
            Some(storeType), ISZ(), ISZ()))))
      )
    } else {
      return (Some(selectTypeMethod), Some(selectResMethod))
    }
  }

  def buildTypeSubstMap(
    name: QName,
    posOpt: Option[Position],
    typeParams: ISZ[AST.TypeParam],
    args: ISZ[AST.Typed],
    reporter: Reporter
  ): Option[HashMap[String, AST.Typed]] = {
    if (typeParams.size != args.size) {
      reporter.error(posOpt, typeCheckerKind,
        st"Type ${(name, ".")} requires ${typeParams.size} type arguments, but ${args.size} is supplied.".render)
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
      reporter.error(posOpt, typeCheckerKind,
        st"$m requires ${m.typeParams.size} type arguments, but only ${args.size} is supplied.".render)
      return None()
    }
    var substMap = HashMap.empty[String, AST.Typed]
    for (i <- z"0" until args.size) {
      substMap = substMap + m.typeParams(i) ~> args(i)
    }
    return Some(substMap)
  }

  def checkComponents(par: Z, strictAliasing: B, th: TypeHierarchy, nameMap: NameMap, typeMap: TypeMap, reporter: Reporter): TypeHierarchy = {
    var jobs = ISZ[() => (TypeHierarchy => (TypeHierarchy, ISZ[Message]) @pure) @pure]()
    for (info <- typeMap.values) {
      info match {
        case info: TypeInfo.Sig if !info.typeChecked =>
          jobs = jobs :+ (() => TypeChecker(th, info.name, !info.ast.isImmutable, TypeChecker.ModeContext.Code,
            strictAliasing).checkSig(info))
        case info: TypeInfo.Adt if !info.typeChecked =>
          jobs = jobs :+ (() => TypeChecker(th, info.name, !info.ast.isDatatype, TypeChecker.ModeContext.Code,
            strictAliasing).checkAdt(info))
        case _ =>
      }
    }
    for (info <- nameMap.values) {
      info match {
        case info: Info.Object =>
          jobs = jobs :+ (() => TypeChecker(th, info.name, F, TypeChecker.ModeContext.Code, strictAliasing).
            checkObject(info))
        case _ =>
      }
    }
    val init = (th, ISZ[Message]())
    val p = ops.ISZOps(jobs).parMapFoldLeftCores(
      (f: () => (TypeHierarchy => (TypeHierarchy, ISZ[Message]) @pure)@pure) => f(), TypeHierarchy.combine _, init, par)
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
                val name = info.name :+ stmt.id.value
                reconstructObject(r.nameMap.get(name).get)
                r.nameMap.get(name) match {
                  case Some(objectInfo: Info.Object) => newStmts = newStmts :+ objectInfo.ast
                  case _ => halt(s"Unexpected situation when type checking object members of object")
                }
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
        if (tpe.isImmutable && th.isMutable(expected)) {
          reporter.error(posOpt, typeCheckerKind, s"Could not unify mutable type '$expected' with immutable type variable '$tpe'")
          return None()
        }
        if (tpe.isIndex && !th.isIndexType(expected)) {
          reporter.error(posOpt, typeCheckerKind, s"Could not unify type '$expected' with index type variable '$tpe'")
          return None()
        }
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
      reporter.error(posOpt, typeCheckerKind,
        s"Could not unify type '${AST.Typed.Tuple(expected)}' with '${AST.Typed.Tuple(tpe)}'.")
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
          reporter.error(p.id.attr.posOpt, TypeChecker.typeCheckerKind,
            s"Identifier '$id' shadows a declaration in the enclosing context.")
          ok = F
        case _ =>
          scope = scope(nameMap = scope.nameMap + id ~> Info.LocalVar(context :+ id, T, p.id, p.tipe.typedOpt,
            None(), Some(AST.ResolvedInfo.LocalVar(context, AST.ResolvedInfo.LocalVar.Scope.Current, F, T, id))))
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
      val reads: ISZ[AST.ResolvedInfo] = for (r <- newStmt.contract.reads) yield r.resOpt.get
      val writes: ISZ[AST.ResolvedInfo] = for (w <- newStmt.contract.modifies) yield w.resOpt.get
      val methodRes = stmt.attr.resOpt.get.asInstanceOf[AST.ResolvedInfo.Method]
      return newStmt(attr = newStmt.attr(resOpt = Some(methodRes(reads = reads, writes = writes))))
    } else {
      return stmt
    }
  }

  def checkStrictPureMethod(strictAliasing: B, th: TypeHierarchy, context: QName, scope: Scope.Local, isMutableContext: B, stmt: AST.Stmt.Method, reporter: Reporter): AST.Stmt.Method = {
    assert(stmt.isStrictPure)
    var r = stmt
    val tc = TypeChecker(th, context, isMutableContext, TypeChecker.ModeContext.Code, strictAliasing)
    if (r.mcontract.nonEmpty) {
      val (ok, sc) = TypeChecker.methodScope(th, context, scope, stmt.sig, reporter)
      if (ok) {
        r = stmt(mcontract = tc(mode = TypeChecker.ModeContext.Spec).checkMethodContract(sc, stmt.mcontract, reporter))
      }
    }
    return tc.checkMethod(scope, r, reporter)
  }

  def checkFactStmt(strictAliasing: B, th: TypeHierarchy, context: QName, scope: Scope, stmt: AST.Stmt.Fact, reporter: Reporter): AST.Stmt.Fact = {
    val tc = TypeChecker(th, context, F, ModeContext.Spec, strictAliasing)
    val bExpectedOpt: Option[AST.Typed] = Some(AST.Typed.b)
    val typeParams = typeParamMap(stmt.typeParams, reporter)
    val sc = Scope.Local.create(typeParams.map, scope)
    var newClaims = ISZ[AST.Exp]()
    if (stmt.isFun) {
      for (claim <- stmt.claims) {
        val qclaim = claim.asInstanceOf[AST.Exp.QuantType]
        val (newFun, tOpt, _) = tc.checkFun(None(), sc, qclaim.fun(context = context), reporter)
        tOpt match {
          case Some(t: AST.Typed.Fun) if t.ret != AST.Typed.b =>
            reporter.error(qclaim.fun.posOpt, TypeChecker.typeCheckerKind, s"Expecting type 'B', but '${t.ret}' found.")
          case _ =>
        }
        newClaims = newClaims :+ qclaim(fun = newFun.asInstanceOf[AST.Exp.Fun])
      }
    } else {
      for (claim <- stmt.claims) {
        newClaims = newClaims :+ tc.checkExp(bExpectedOpt, sc, claim, reporter)._1
      }
    }
    return stmt(claims = newClaims)
  }

  def checkTheoremStmt(strictAliasing: B, th: TypeHierarchy, context: QName, scope: Scope, stmt: AST.Stmt.Theorem, reporter: Reporter): AST.Stmt.Theorem = {
    val tc = TypeChecker(th, context, F, ModeContext.Spec, strictAliasing)
    val bExpectedOpt: Option[AST.Typed] = Some(AST.Typed.b)
    val typeParams = typeParamMap(stmt.typeParams, reporter)
    val sc = Scope.Local.create(typeParams.map, scope)
    if (stmt.isFun) {
      val qclaim = stmt.claim.asInstanceOf[AST.Exp.QuantType]
      val (newFun, tOpt, proofScope) = tc.checkFun(None(), sc, qclaim.fun(context = context), reporter)
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

  def checkInvStmt(strictAliasing: B, th: TypeHierarchy, context: QName, scope: Scope.Local, stmt: AST.Stmt.Inv, reporter: Reporter): AST.Stmt.Inv = {
    val tc = TypeChecker(th, context, F, ModeContext.Spec, strictAliasing)
    val bExpectedOpt: Option[AST.Typed] = Some(AST.Typed.b)
    return stmt(claims = for (claim <- stmt.claims) yield tc.checkExp(bExpectedOpt, scope, claim, reporter)._1,
      attr = stmt.attr(typedOpt = AST.Typed.unitOpt))
  }

  def checkRsValStmt(strictAliasing: B, th: TypeHierarchy, context: QName, scope: Scope.Local, stmt: AST.Stmt.RsVal, reporter: Reporter): AST.Stmt.RsVal = {
    val tc = TypeChecker(th, context, F, ModeContext.Spec, strictAliasing)
    val expectedOpt: Option[AST.Typed] = AST.Typed.rsOpt
    val newInit = tc.checkExp(expectedOpt, scope, stmt.init, reporter)._1
    return stmt(init = newInit, attr = stmt.attr(typedOpt = AST.Typed.rsOpt))
  }

  def checkDataRefinement(strictAliasing: B, th: TypeHierarchy, context: QName, scope: Scope.Local, stmt: AST.Stmt.DataRefinement, reporter: Reporter): AST.Stmt.DataRefinement = {
    val tc = TypeChecker(th, context, F, ModeContext.Spec, strictAliasing)
    return tc.checkDataRefinement(scope, stmt, reporter)
  }
}

import TypeChecker._

@datatype class TypeChecker(val typeHierarchy: TypeHierarchy,
                            val context: QName,
                            val isInMutableContext: B,
                            val mode: ModeContext.Type,
                            val strictAliasing: B) {

  @pure def inSpec: B = {
    mode match {
      case ModeContext.Code => return F
      case ModeContext.TheoremCode => return F
      case ModeContext.Spec => return T
      case ModeContext.SpecPost => return T
      case ModeContext.RS => return T
    }
  }

  def basicKind(scope: Scope, tpe: AST.Typed): Option[BasicKind.Type] = {
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
      case info: Info.RsVal =>
        if (!inSpec) {
          reporter.error(posOpt, typeCheckerKind, "Cannot access @rw val in non-spec context.")
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
          case Some(pos) =>  reporter.error(posOpt, typeCheckerKind,
            s"Could not resolve literal interpolator for '$prefix' due to name conflict with a local variable declared at [${pos.beginLine}, ${pos.beginColumn}].")
          case _ => reporter.error(posOpt, typeCheckerKind,
            s"Could not resolve literal interpolator for '$prefix' due to name conflict with a local variable.")
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
      reporter.error(posOpt, typeCheckerKind, st"Could not infer type parameter(s): '${(unbound, "', '")}' for $t.".render)
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

    reporter.error(posOpt, typeCheckerKind, s"Expecting either type '${AST.Typed.z}', @bits, or @range, but '$t' found.")
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
            reporter.error(id.attr.posOpt, typeCheckerKind,
              s"Cannot declare '$key' because the identifier has already been previously declared.")
            ok = F
          } else {
            scope = scope(nameMap = scope.nameMap + key ~> Info.LocalVar(context :+ key, F, id, tOpt, None(),
              Some(AST.ResolvedInfo.LocalVar(context, AST.ResolvedInfo.LocalVar.Scope.Current, F, T, key))))
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
            reporter.error(posOpt, typeCheckerKind,
              st"For-yield expressions cannot involve both immutable and mutable sequence/generator.".render)
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
              val (newInvs, newMods) = thisL(mode = ModeContext.Spec).checkLoopInv(scope,
                enumGen.contract.invariants, enumGen.contract.modifies, reporter)
              return enumGen(
                range = range(start = newStartExp, end = newEndExp, byOpt = newByOpt),
                condOpt = newCondOpt,
                contract = enumGen.contract(invariants = newInvs, modifies = newMods)
              )
            case _ =>
              ok = F
              return enumGen(range = range(start = newStartExp, end = newEndExp, byOpt = newByOpt))
          }
        case range: AST.EnumGen.Range.Expr =>
          def reportErrType(t: ST): Unit = {
            reporter.error(range.exp.posOpt, typeCheckerKind,
              st"Expecting either type of 'org.sireum.{IS, MS, Jen, MJen}', but '$t' found.".render)
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
              val (newInvs, newMods) = thisL(mode = ModeContext.Spec).
                checkLoopInv(scope, enumGen.contract.invariants, enumGen.contract.modifies, reporter)
              return enumGen(range = range(exp = newExp), condOpt = newCondOpt,
                contract = enumGen.contract(invariants = newInvs, modifies = newMods))
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
            reporter.error(id.attr.posOpt, typeCheckerKind,
              s"Cannot declare '$key' because the identifier has already been previously declared.")
            ok = F
          } else {
            scope = scope(nameMap = scope.nameMap + key ~> Info.LocalVar(context :+ key, F, id, tOpt, None(),
              Some(AST.ResolvedInfo.LocalVar(context, AST.ResolvedInfo.LocalVar.Scope.Current, F, T, key))))
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
                      reporter.error(tipe.posOpt, typeCheckerKind,
                        s"Expecting type '$expectedType', but '${t.typedOpt.get}' found.")
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
        case Some(retType) => Some(AST.Typed.Fun(AST.Purity.Pure,F, paramTypes, retType))
        case _ => None()
      }
      return (exp(context = context, params = newParams, exp = newExp, attr = exp.attr(typedOpt = tOpt)), tOpt, scope)
    }
  }

  def checkSelectH(
    scope: Scope,
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
      if (!reporter.ignore) {
        reporter.error(ident.attr.posOpt, typeCheckerKind, s"'$id' is not a member of type '$t'.")
      }
    }

    def checkAccess(t: AST.Typed): (Option[AST.Typed], Option[AST.ResolvedInfo], ISZ[AST.Typed]) = {
      id.native match {
        case "string" if typeArgs.isEmpty => return (AST.Typed.stringOpt, stringResOpt, typeArgs)
        case "hash" if typeArgs.isEmpty => return (AST.Typed.zOpt, hashResOpt, typeArgs)
        case "asInstanceOf" if typeArgs.size == z"1" =>
          val asT = typeArgs(0)
          if (!t.isInstanceOf[AST.Typed.Name]) {
            reporter.error(ident.attr.posOpt, typeCheckerKind, s"Cannot use 'asInstanceOf' on '$t'.")
          } else {
            checkTypeRelation("asInstanceOf", ident.attr.posOpt, t, asT, reporter)
          }
          return (Some(asT), asInstanceOfResOpt, ISZ())
        case "isInstanceOf" if typeArgs.size == z"1" =>
          val asT = typeArgs(0)
          if (!t.isInstanceOf[AST.Typed.Name]) {
            reporter.error(ident.attr.posOpt, typeCheckerKind, s"Cannot use 'isInstanceOf' on '$t'.")
          } else {
            checkTypeRelation("isInstanceOf", ident.attr.posOpt, t, asT, reporter)
          }
          return (AST.Typed.bOpt, isInstanceOfResOpt, ISZ())
        case _ =>
          t match {
            case t: AST.Typed.Name =>
              if (t.args.isEmpty) {
                typeHierarchy.typeMap.get(t.ids) match {
                  case Some(info: TypeInfo.Enum) =>
                    id.native match {
                      case "name" => return (info.nameTypedOpt, info.nameResOpt, typeArgs)
                      case "ordinal" => return (info.ordinalTypedOpt, info.ordinalResOpt, typeArgs)
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
                val smOpt = buildTypeSubstMap(info.name, ident.attr.posOpt, info.ast.typeParams, receiverType.args, reporter)
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
                val smOpt = buildTypeSubstMap(info.name, ident.attr.posOpt, info.ast.typeParams, receiverType.args, reporter)
                smOpt match {
                  case Some(sm) => return (Some(rt.subst(sm)), AST.ResolvedInfo.substOpt(r._3, sm), typeArgs)
                  case _ => return noResult
                }
              case (T, _, _) => val res = checkAccess(receiverType); return res
              case (_, _, _) =>
                errSpecImpureAccess(receiverType)
                return noResult
            }
          case _: TypeInfo.SubZ =>
            id.native match {
              case "toZ" if typeArgs.isEmpty => return (AST.Typed.zOpt, extResOpt(F, receiverType.ids, id, ISZ(),
                AST.Typed.Fun(AST.Purity.Pure,T, ISZ(), AST.Typed.z)), typeArgs)
              case _ => val res = checkAccess(receiverType); return res
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
          case Some(info: TypeInfo.SubZ) if typeArgs.isEmpty =>
            id.native match {
              case "Max" if info.ast.hasMax || info.ast.isBitVector => return (info.typedOpt, maxResOpt, typeArgs)
              case "Min" if info.ast.hasMin || info.ast.isBitVector => return (info.typedOpt, minResOpt, typeArgs)
              case "random" =>
                val t = AST.Typed.Name(info.name, ISZ())
                val f = AST.Typed.Fun(AST.Purity.Impure,T, ISZ(), t)
                return (
                  Some(AST.Typed.Method(T, AST.MethodMode.Ext, ISZ(), info.name, id, ISZ(), f)),
                  extResOpt(T, info.name, id, ISZ(), f),
                  typeArgs
                )
              case "randomSeed" =>
                val t = AST.Typed.Name(info.name, ISZ())
                val f = AST.Typed.Fun(AST.Purity.Pure,F, ISZ(AST.Typed.z), t)
                val paramNames = ISZ[String]("seed")
                return (
                  Some(AST.Typed.Method(T, AST.MethodMode.Ext, ISZ(), info.name, id, paramNames, f)),
                  extResOpt(T, info.name, id, paramNames, f),
                  typeArgs
                )
              case "randomBetween" =>
                val t = AST.Typed.Name(info.name, ISZ())
                val f = AST.Typed.Fun(AST.Purity.Impure,F, ISZ(t, t), t)
                val paramNames = ISZ[String]("min", "max")
                return (
                  Some(AST.Typed.Method(T, AST.MethodMode.Ext, ISZ(), info.name, id, paramNames, f)),
                  extResOpt(T, info.name, id, paramNames, f),
                  typeArgs
                )
              case "randomSeedBetween" =>
                val t = AST.Typed.Name(info.name, ISZ())
                val f = AST.Typed.Fun(AST.Purity.Pure,F, ISZ(AST.Typed.z, t, t), t)
                val paramNames = ISZ[String]("seed", "min", "max")
                return (
                  Some(AST.Typed.Method(T, AST.MethodMode.Ext, ISZ(), info.name, id, paramNames, f)),
                  extResOpt(T, info.name, id, paramNames, f),
                  typeArgs
                )
              case "fromZ" =>
                val t = AST.Typed.Name(info.name, ISZ())
                val f = AST.Typed.Fun(AST.Purity.Pure,F, ISZ(AST.Typed.z), t)
                val paramNames = ISZ[String]("n")
                return (
                  Some(AST.Typed.Method(T, AST.MethodMode.Ext, ISZ(), info.name, id, paramNames, f)),
                  extResOpt(T, info.name, id, paramNames, f),
                  typeArgs
                )
              case _ =>
            }
          case Some(info) if mode == ModeContext.RS && id == "$$" && (info.isInstanceOf[TypeInfo.Adt] ||
            info.isInstanceOf[TypeInfo.Sig]) =>
            val t = info.tpe
            if (typeArgs.nonEmpty) {
              reporter.error(ident.attr.posOpt, typeCheckerKind, s"$id should be used without type arguments" )
            }
            val ft = AST.Typed.Fun(AST.Purity.Pure, T, ISZ(), t)
            return (
              Some(AST.Typed.Method(T, AST.MethodMode.Ext, ISZ(), receiverType.name, id, ISZ(), ft)),
              Some(AST.ResolvedInfo.Method(T, AST.MethodMode.Ext, ISZ(), receiverType.name, id, ISZ(), Some(ft), ISZ(), ISZ())),
              ISZ()
            )
          case _ =>
        }
        val r = checkInfoOpt(None(), typeHierarchy.nameMap.get(receiverType.name :+ normalizeSpecId(id)),
          ident.attr.posOpt, reporter)
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
          case Some(info: Info.Enum) if typeArgs.isEmpty =>
            id.native match {
              case "byName" => return (info.byNameTypedOpt, info.byNameResOpt, typeArgs)
              case "byOrdinal" => return (info.byOrdinalTypedOpt, info.byOrdinalResOpt, typeArgs)
              case "elements" => return (info.elementsTypedOpt, info.elementsResOpt, typeArgs)
              case "numOfElements" => return (info.numOfElementsTypedOpt, info.numOfElementsResOpt, typeArgs)
              case "random" =>
                val t = AST.Typed.Name(info.name :+ Info.Enum.elementTypeSuffix, ISZ())
                val f = AST.Typed.Fun(AST.Purity.Impure,T, ISZ(), t)
                return (
                  Some(AST.Typed.Method(T, AST.MethodMode.Ext, ISZ(), info.name, id, ISZ(), f)),
                  extResOpt(T, info.name, id, ISZ(), f),
                  typeArgs
                )
              case "randomSeed" =>
                val t = AST.Typed.Name(info.name :+ Info.Enum.elementTypeSuffix, ISZ())
                val f = AST.Typed.Fun(AST.Purity.Pure,F, ISZ(AST.Typed.z), t)
                val paramNames = ISZ[String]("seed")
                return (
                  Some(AST.Typed.Method(T, AST.MethodMode.Ext, ISZ(), info.name, id, paramNames, f)),
                  extResOpt(T, info.name, id, paramNames, f),
                  typeArgs
                )
              case "randomBetween" =>
                val t = AST.Typed.Name(info.name :+ Info.Enum.elementTypeSuffix, ISZ())
                val f = AST.Typed.Fun(AST.Purity.Impure,F, ISZ(t, t), t)
                val paramNames = ISZ[String]("min", "max")
                return (
                  Some(AST.Typed.Method(T, AST.MethodMode.Ext, ISZ(), info.name, id, paramNames, f)),
                  extResOpt(T, info.name, id, paramNames, f),
                  typeArgs
                )
              case "randomSeedBetween" =>
                val t = AST.Typed.Name(info.name :+ Info.Enum.elementTypeSuffix, ISZ())
                val f = AST.Typed.Fun(AST.Purity.Pure,F, ISZ(AST.Typed.z, t, t), t)
                val paramNames = ISZ[String]("seed", "min", "max")
                return (
                  Some(AST.Typed.Method(T, AST.MethodMode.Ext, ISZ(), info.name, id, paramNames, f)),
                  extResOpt(T, info.name, id, paramNames, f),
                  typeArgs
                )
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
      case receiverType: AST.Typed.TypeVar if receiverType.isIndex && typeArgs.isEmpty =>
        id.native match {
          case "toZ" => return (AST.Typed.zOpt, extResOpt(F, ISZ(receiverType.id), id, ISZ(),
            AST.Typed.Fun(AST.Purity.Pure,T, ISZ(), AST.Typed.z)), typeArgs)
          case _ => val res = checkAccess(receiverType); return res
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

  @strictpure def normalizeSpecId(id: String): String = id

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
            val r = checkSelectH(scope, t, id, ISZ(), rep)
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
    scope: Scope.Local,
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
          assertumeExp.ident(attr = assertumeExp.ident.
            attr(posOpt = assertumeExp.ident.id.attr.posOpt, resOpt = resOpt, typedOpt = assertumeMsgTypedOpt))
        case _ =>
          assertumeExp.ident(attr = assertumeExp.ident.
            attr(posOpt = assertumeExp.ident.id.attr.posOpt, resOpt = resOpt, typedOpt = assertumeTypedOpt))
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
          typedOpt = Some(AST.Typed.Fun(AST.Purity.Impure,F, argTypes, AST.Typed.unit))
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
          haltExp.ident(attr = haltExp.attr(posOpt = haltExp.ident.id.attr.posOpt, resOpt = haltResOpt,
            typedOpt = haltTypedOpt))
        case _ => haltExp.ident
      }
      return (
        haltExp(ident = ident, args = ISZ(newArg), attr = haltExp.attr(resOpt = haltResOpt, typedOpt = haltTypedOpt)),
        AST.Typed.nothingOpt
      )
    }

    def checkSetOptions(setOptionsExp: AST.Exp.Invoke, args: ISZ[AST.Exp]): (AST.Exp, Option[AST.Typed]) = {
      if (args.size != z"2") {
        reporter.error(setOptionsExp.posOpt, typeCheckerKind, s"Expecting two arguments, but ${args.size} found.")
        return (setOptionsExp(attr = setOptionsExp.attr(resOpt = setOptionsResOpt, typedOpt = setOptionsTypedOpt)), AST.Typed.unitOpt)
      }
      val ident = setOptionsExp.ident(attr = setOptionsExp.attr(posOpt = setOptionsExp.ident.id.attr.posOpt,
        resOpt = setOptionsResOpt, typedOpt = setOptionsTypedOpt))
      if (!args(0).isInstanceOf[AST.Exp.LitString]) {
         reporter.error(args(0).posOpt, typeCheckerKind, s"Expecting a string literal for setOptions' tool argument")
      }
      val (arg1, _) = checkExp(AST.Typed.stringOpt, scope, args(1), reporter)
      arg1 match {
        case _: AST.Exp.LitString =>
        case AST.Exp.Select(Some(_: AST.Exp.LitString), AST.Id(string"stripMargin"), ISZ()) =>
        case _ =>
          reporter.error(args(1).posOpt, typeCheckerKind, s"Expecting a string literal (with optional .stripMargin) for setOptions' options argument")
      }
      return (
        setOptionsExp(ident = ident, args = ISZ(args(0), arg1), attr = setOptionsExp.attr(resOpt = setOptionsResOpt,
          typedOpt = setOptionsTypedOpt)), AST.Typed.unitOpt
      )
    }

    def checkUnary(unaryExp: AST.Exp.Unary): (AST.Exp, Option[AST.Typed]) = {
      val (newExp, expTypeOpt) = checkExp(None(), scope, unaryExp.exp, reporter)
      val newUnaryExp = unaryExp(exp = newExp)
      expTypeOpt match {
        case Some(expType) =>
          val kindOpt = basicKind(scope, expType)
          kindOpt match {
            case Some(kind) =>
              if (unaryExp.op == AST.Exp.UnaryOp.Not && kind != BasicKind.B) {
                reporter.error(unaryExp.posOpt, typeCheckerKind, st"Undefined unary operation ! on '$expType'.".render)
                return (newUnaryExp, None())
              }
              if (unaryExp.op == AST.Exp.UnaryOp.Complement &&
                !(kind == BasicKind.B || kind == BasicKind.C || kind == BasicKind.Bits)) {
                reporter.error(unaryExp.posOpt, typeCheckerKind, st"Undefined unary operation ~ on '$expType'.".render)
                return (newUnaryExp, None())
              }
              var r = newUnaryExp
              val tOpt: Option[AST.Typed] = Some(expType)
              r = r(attr = r.attr(typedOpt = tOpt, resOpt = unopResOpt.get(unaryExp.op).get))
              return (r, tOpt)
            case _ =>
              val (typedOpt, resOpt, _) =
                checkSelectH(scope, expType, AST.Id(AST.Util.unopId(unaryExp.op), AST.Attr(unaryExp.posOpt)), ISZ(), reporter)
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
        val r = checkTuple(AST.Exp.Tuple(ISZ(binaryExp.left, binaryExp.right), AST.TypedAttr(binaryExp.attr.posOpt,
          binaryExp.attr.typedOpt)))
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
        reporter.error(binaryExp.posOpt, typeCheckerKind,
          st"Incompatible types for binary operation '$leftType' ${binaryExp.op} '$rt'.".render)
      }

      if (eqBinops.contains(binaryExp.op)) {
        reporter.reports(rep.messages)
        val (right, rightTypeOpt): (AST.Exp, Option[AST.Typed]) =
          if (leftType.isInstanceOf[AST.Typed.Tuple]) checkExp(leftTypeOpt, scope, binaryExp.right, reporter)
          else checkExp(None(), scope, binaryExp.right, reporter)
        rightTypeOpt match {
          case Some(rightType) =>
            val isCompat = typeHierarchy.isCompatible(leftType, rightType)
            if (!isCompat) {
              errIncompat(rightType)
            }
          case _ =>
        }
        val resOpt = binopResOpt.get(binaryExp.op).get
        return (
          binaryExp(left = newLeft, right = right, attr = binaryExp.attr(resOpt = resOpt, typedOpt = AST.Typed.bOpt)),
          AST.Typed.bOpt
        )
      }

      val lOpt = basicKind(scope, leftType)

      lOpt match {
        case Some(leftKind) if leftKind != BasicKind.String || AST.Util.isCompareBinop(binaryExp.op) =>
          reporter.reports(rep.messages)
          val (newRight, rightTypeOpt) = checkExp(None(), scope, binaryExp.right, reporter)

          rightTypeOpt match {
            case Some(rightType) =>
              def errUndef(): Unit = {
                reporter.error(binaryExp.posOpt, typeCheckerKind,
                  st"Undefined binary operation ${binaryExp.op} on '$leftType'".render)
              }

              val rOpt = basicKind(scope, rightType)
              val (op, tOpt): (String, Option[AST.Typed]) = rOpt match {
                case Some(rightKind) =>
                  if (leftKind != rightKind) {
                    errIncompat(rightType)
                    (binaryExp.op, None())
                  } else if ((leftKind == BasicKind.B && AST.Util.isBoolBinop(binaryExp.op)) ||
                    (AST.Util.isArithBinop(binaryExp.op) && leftKind != BasicKind.B) ||
                    (AST.Util.isBitsBinop(binaryExp.op) && (leftKind == BasicKind.Bits || leftKind == BasicKind.C))) {
                    (binaryExp.op, Some(leftType))
                  } else if (AST.Util.isCompareBinop(binaryExp.op) && leftKind != BasicKind.B) {
                    (binaryExp.op, Some(AST.Typed.b))
                  } else if (AST.Util.isFpEqBinop(binaryExp.op) && AST.Typed.floatingPointTypes.contains(leftType)) {
                    (binaryExp.op, Some(AST.Typed.b))
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
        etaParentOpt match {
          case Some(eta) => return (eta, None())
          case _ => return (refExp, None())
        }
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
              if (t.typeParams.size != typeArgs.size && mode != ModeContext.RS) {
                reporter.error(refExp.posOpt, typeCheckerKind,
                  s"Expecting ${t.typeParams.size} type arguments, but ${typeArgs.size} found.")
                return noResult
              }
              var sm = HashMap.emptyInit[String, AST.Typed](typeArgs.size)
              if (mode != ModeContext.RS) {
                val size = typeArgs.size
                var i = 0
                while (i < size) {
                  sm = sm + t.typeParams(i) ~> typeArgs(i)
                  i = i + 1
                }
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
                reporter.error(refExp.posOpt, typeCheckerKind,
                  "Method access has to be explicitly eta-expanded to become a function using '_'.")
                return noResult
            }
          } else {
            etaParentOpt match {
              case Some(etaParent) =>
                val tOpt: Option[AST.Typed] = Some(if (t.tpe.isByName) t.tpe(isByName = F) else t.tpe)
                return (etaParent(ref = ref.subst(substMap), attr = etaParent.attr(typedOpt = tOpt)), tOpt)
              case _ if t.tpe.isByName => return (ref.subst(substMap).asExp, Some(t.tpe.ret.subst(substMap)))
              case _ =>
                reporter.error(refExp.posOpt, typeCheckerKind,
                  "Method access has to be explicitly eta-expanded to become a function using '_'.")
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
      val newIdentExp = identExp(attr = identExp.attr(typedOpt = typedOpt, resOpt = resOpt))
      var newExp: AST.Exp.Ref = newIdentExp
      if (typedOpt.isEmpty) {
        return (newExp.asExp, typedOpt)
      }
      newIdentExp.resOpt match {
        case Some(res: AST.ResolvedInfo.Var) if !res.isInObject =>
          newExp = AST.Exp.Select(Some(AST.Exp.This(context, AST.TypedAttr(newExp.posOpt, scope.thisOpt))), newIdentExp.id,
            newIdentExp.targs, AST.ResolvedAttr(newExp.posOpt, newIdentExp.resOpt, newExp.typedOpt))
        case Some(res: AST.ResolvedInfo.Method) if !res.isInObject && typeHierarchy.typeMap.get(res.owner).nonEmpty =>
          newExp = AST.Exp.Select(Some(AST.Exp.This(context, AST.TypedAttr(newExp.posOpt, scope.thisOpt))), newIdentExp.id,
            newIdentExp.targs, AST.ResolvedAttr(newExp.posOpt, newIdentExp.resOpt, newExp.typedOpt))
        case _ =>
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
                val t = checkSelectH(scope, receiverType, selectExp.id, typeArgs, reporter)
                (selectExp(targs = newTargs, receiverOpt = Some(newReceiver),
                  attr = selectExp.attr(typedOpt = t._1, resOpt = t._2)), t._1, t._3)
              case _ =>
                (selectExp(targs = newTargs, receiverOpt = Some(newReceiver)), None[AST.Typed](), typeArgs)
            }
          case _ =>
            val (typedOpt, resOpt) = checkId(scope, selectExp.id, reporter)
            (selectExp(targs = newTargs, attr = selectExp.attr(typedOpt = typedOpt, resOpt = resOpt)), typedOpt, typeArgs)
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
    ): (Option[AST.Typed], Option[AST.ResolvedInfo], ISZ[AST.Typed], ISZ[String]) = {
      val (tpe, newTypeArgs): (AST.Typed, ISZ[AST.Typed]) = typed match {
        case typed: AST.Typed.Method if typed.tpe.isByName =>
          val smOpt = buildMethodSubstMap(typed, posOpt, typeArgs, reporter)
          smOpt match {
            case Some(sm) => (typed.tpe.ret.subst(sm), ISZ())
            case _ => return (None(), None(), typeArgs, ISZ())
          }
        case _ => (typed, typeArgs)
      }
      tpe match {
        case tpe: AST.Typed.Object =>
          AST.Typed.basicConstructorMap.get(tpe.name) match {
            case Some(r @ (_, _)) => return (r._1, r._2, newTypeArgs, ISZ())
            case _ =>
              sConstructorTypedResOpt(tpe.name, numOfArgs) match {
                case (typedOpt@Some(_), resOpt@Some(_)) => return (typedOpt, resOpt, newTypeArgs, ISZ())
                case _ =>
                  typeHierarchy.typeMap.get(tpe.name) match {
                    case Some(info) =>
                      info match {
                        case info: TypeInfo.SubZ =>
                          val t = AST.Typed.Name(tpe.name, ISZ())
                          val constructorType: AST.Typed.Fun =
                            AST.Typed.Fun(AST.Purity.Pure,F, ISZ(AST.Typed.string), AST.Typed.Name(AST.Typed.optionName, ISZ(t)))
                          return (
                            Some(AST.Typed.Method(T, AST.MethodMode.Constructor, ISZ(), info.owner, info.ast.id.value,
                              ISZ(), constructorType)),
                            Some(AST.ResolvedInfo.Method(T, AST.MethodMode.Constructor, ISZ(), info.owner,
                              info.ast.id.value, ISZ(), Some(constructorType), ISZ(), ISZ())),
                            newTypeArgs,
                            ISZ()
                          )

                        case info: TypeInfo.Adt if !info.ast.isRoot =>
                          info.constructorTypeOpt match {
                            case Some(constructorType) =>
                              return (Some(constructorType), info.constructorResOpt, newTypeArgs,
                                for (p <- info.ast.params if !p.isHidden) yield p.id.value)
                            case _ =>
                              reporter.error(posOpt, typeCheckerKind,
                                st"Cannot create an object of type ${(tpe.name, ".")}.".render)
                              return (None(), None(), newTypeArgs, ISZ())
                          }
                        case _ =>
                      }
                    case _ =>
                  }
              }
          }
        case tpe: AST.Typed.Name =>
          if (tpe.args.size == z"2" && (tpe.ids == AST.Typed.isName || tpe.ids == AST.Typed.msName)) {
            if (numOfArgs == z"1") {
              val p = sSelectTypedResOpt(tpe, T)
              return (p._1, p._2, newTypeArgs, ISZ())
            } else {
              val p = sStoreTypedResOpt(tpe, numOfArgs)
              return (p._1, p._2, newTypeArgs, ISZ())
            }
          } else {
            val p = adtCopyTypedResOpt(mode == TypeChecker.ModeContext.Code, typeHierarchy, posOpt, tpe, argNames, reporter)
            if (p._3) {
              return (p._1, p._2, newTypeArgs, p._4)
            }
          }
        case _ =>
      }
      return (Some(tpe), resOpt, newTypeArgs, ISZ())
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
        return (make(expArgs, None(), AST.Typed.Fun(AST.Purity.Impure,F, ISZ(), AST.Typed.nothing)), None())
      }

      def checkH(sm: HashMap[String, AST.Typed]): (AST.Exp, Option[AST.Typed]) = {
        val funType = m.tpe.subst(sm)
        var i = 0
        val size = expArgs.size
        if (m.paramNames.isEmpty) {
          var args = ISZ[AST.Exp]()
          while (i < size) {
            val pt = funType.args(i)
            val at = expArgs(i)
            (pt, at) match {
              case (AST.Typed.stepId, lit: AST.Exp.LitZ) =>
                args = args :+ AST.ProofAst.StepId.Num(lit.value, lit.attr)
              case (AST.Typed.stepId, lit: AST.Exp.LitString) =>
                args = args :+ AST.ProofAst.StepId.Str(F, lit.value, lit.attr)
              case _ =>
                val (newArg, _) = checkExp(Some(pt), scope, at, rep)
                args = args :+ newArg
            }
            i = i + 1
          }
          return (make(args, Some(funType.ret), funType), Some(funType.ret))
        } else {
          var args = Map.empty[String, AST.Exp]
          while (i < size) {
            val pt = funType.args(i)
            val at = expArgs(i)
            (pt, at) match {
              case (AST.Typed.stepId, lit: AST.Exp.LitZ) =>
                args = args + m.paramNames(i) ~> AST.ProofAst.StepId.Num(lit.value, lit.attr)
              case (AST.Typed.stepId, lit: AST.Exp.LitString) =>
                args = args + m.paramNames(i) ~> AST.ProofAst.StepId.Str(F, lit.value, lit.attr)
              case _ =>
                val (newArg, _) = checkExp(Some(pt), scope, at, rep)
                args = args + m.paramNames(i) ~> newArg
            }
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
                return (make(newArgs, None(), AST.Typed.Fun(AST.Purity.Impure,F, ISZ(), AST.Typed.nothing)), None())
              }
              val funType = m.tpe.subst(sm)
              return (make(newArgs, Some(funType.ret), funType), Some(funType.ret))
            case _ =>
              repArgs.error(expId.attr.posOpt, typeCheckerKind, st"Could not unify (${(argTypes, ", ")}) with (${(m.tpe.args, ", ")})".render)
              return (make(newArgs, None(), AST.Typed.Fun(AST.Purity.Impure,F, ISZ(), AST.Typed.nothing)), None())
          }
        }
        val rArgs = tryArgs()
        if (rArgs._2.nonEmpty && !repArgs.hasError) {
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
            return if (res.isInObject) Some(res.owner :+ res.id) else
              Some((if (thisAccessOpt.isEmpty) ISZ[String]("this") else thisAccessOpt.get) :+ res.id)
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
          case Some(t) if typeHierarchy.isMutable(t) => argPaths = argPaths :+ expPath(arg, ISZ())
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
            val (t2Opt, newResOpt, targs, _) =
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

        def checkFactTheorem(kind: String, name: ISZ[String], typeParams: ISZ[AST.TypeParam], claim: AST.Exp.Quant): (AST.Exp, Option[AST.Typed]) = {
          val size = invokeExp.args.size
          if (claim.fun.params.size != size) {
            reporter.error(
              invokeExp.ident.attr.posOpt,
              typeCheckerKind,
              st"$kind $name is expecting ${claim.fun.params.size} arguments, but $size found.".render
            )
            return partResultH
          }
          var newArgs = ISZ[AST.Exp]()
          if (typeParams.nonEmpty) {
            if (targs.nonEmpty) {
              val smOpt = TypeChecker.buildTypeSubstMap(name, invokeExp.ident.posOpt, typeParams, typeArgs, reporter)
              smOpt match {
                case Some(sm) =>
                  var i = 0
                  var argTypes = ISZ[AST.Typed]()
                  while (i < size) {
                    val (newArg, typedOpt) = checkExp(Some(claim.fun.params(i).typedOpt.get.subst(sm)), scope, invokeExp.args(i), reporter)
                    if (typedOpt.isEmpty) {
                      return partResultH
                    }
                    argTypes = argTypes :+ typedOpt.get
                    newArgs = newArgs :+ newArg
                    i = i + 1
                  }
                case _ => return partResultH
              }
            } else {
              var i = 0
              var argTypes = ISZ[AST.Typed]()
              while (i < size) {
                val (newArg, typedOpt) = checkExp(None(), scope, invokeExp.args(i), reporter)
                if (typedOpt.isEmpty) {
                  return partResultH
                }
                argTypes = argTypes :+ typedOpt.get
                newArgs = newArgs :+ newArg
                i = i + 1
              }
              TypeChecker.unifies(typeHierarchy, invokeExp.posOpt, TypeRelation.Subtype,
                for (p <- claim.fun.params) yield p.typedOpt.get, for (arg <- newArgs) yield arg.typedOpt.get,
                reporter)
            }
          } else {
            if (targs.isEmpty) {
              var i = 0
              var argTypes = ISZ[AST.Typed]()
              while (i < size) {
                val (newArg, typedOpt) = checkExp(Some(claim.fun.params(i).typedOpt.get), scope, invokeExp.args(i), reporter)
                if (typedOpt.isEmpty) {
                  return partResultH
                }
                argTypes = argTypes :+ typedOpt.get
                newArgs = newArgs :+ newArg
                i = i + 1
              }
            } else {
              reporter.error(
                invokeExp.ident.attr.posOpt,
                typeCheckerKind,
                st"$kind ${(name, ".")} does not accept type parameters.".render
              )
              return partResultH
            }
          }
          return (
            invokeExp(
              ident = ident,
              targs = targs,
              args = newArgs,
              receiverOpt = receiverOpt,
              attr = invokeExp.attr(resOpt = resOpt, typedOpt = Some(t))
            ),
            Some(AST.Typed.unit)
          )
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
          case t: AST.Typed.Fact if inSpec =>
            val info = scope.resolveName(typeHierarchy.nameMap, t.name).get.asInstanceOf[Info.Fact]
            info.ast.claims(0) match {
              case claim: AST.Exp.Quant if info.ast.isFun =>
                return checkFactTheorem("Fact", info.name, info.ast.typeParams, claim)
              case _ =>
                reporter.error(
                  invokeExp.ident.attr.posOpt,
                  typeCheckerKind,
                  st"Cannot invoke on Fact ${(info.name, ".")}.".render
                )
                return partResultH
            }
          case t: AST.Typed.Theorem if inSpec =>
            val info = scope.resolveName(typeHierarchy.nameMap, t.name).get.asInstanceOf[Info.Theorem]
            val kind: String = if (info.ast.isLemma) "Lemma" else "Theorem"
            info.ast.claim match {
              case claim: AST.Exp.Quant if info.ast.isFun =>
                return checkFactTheorem(kind, info.name, info.ast.typeParams, claim)
              case _ =>
                reporter.error(
                  invokeExp.ident.attr.posOpt,
                  typeCheckerKind,
                  st"Cannot invoke on $kind ${(info.name, ".")}.".render
                )
                return partResultH
            }
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
                attr = invokeExp.attr(resOpt = resOpt, typedOpt = Some(t.ret))
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
            checkSelectH(scope, receiverType, invokeExp.ident.id, typeArgs, reporter)
          val r = checkInvokeH(typedOpt, resOpt, Some(newReceiver), invokeExp.ident(
            attr = invokeExp.ident.attr(posOpt = invokeExp.ident.id.attr.posOpt, resOpt = resOpt, typedOpt = typedOpt)),
            newTargs, tArgs2)
          return r
        case _ =>
          val (typedOpt, resOpt) = checkId(scope, invokeExp.ident.id, reporter)
          val r = checkInvokeH(typedOpt, resOpt, None(), invokeExp.ident(
            attr = invokeExp.ident.attr(posOpt = invokeExp.ident.id.attr.posOpt, resOpt = resOpt, typedOpt = typedOpt)),
            newTargs, typeArgs)
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

        val (t, resOpt, typeArgs, paramNames): (AST.Typed, Option[AST.ResolvedInfo], ISZ[AST.Typed], ISZ[String]) = tOpt match {
          case Some(tpe) =>
            val (t2Opt, newResOpt, targs, pns) = checkInvokeType(invokeExp.ident.attr.posOpt, rOpt, tpe, typeArguments,
              invokeExp.args.size, invokeExp.args.map(na => na.id.value))
            t2Opt match {
              case Some(t2) => (t2, newResOpt, targs, pns)
              case _ => return partResultNamedH
            }
          case _ => return partResultNamedH
        }

        t match {
          case m: AST.Typed.Method =>
            if (m.tpe.args.size != invokeExp.args.size) {
              reporter.error(invokeExp.ident.attr.posOpt, typeCheckerKind,
                s"$m is expecting ${m.tpe.args.size} arguments, but ${invokeExp.args.size} found.")
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
                  reporter.error(na.id.attr.posOpt, typeCheckerKind,
                    s"An argument for parameter '$name' has previously been supplied.")
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
              val mi = HashMap ++ (for (i <- 0 until paramNames.size) yield (paramNames(i), i))
              val args: ISZ[AST.NamedArg] =
                if (eArgs.size == expArgs.size) {
                  var r = ISZ[AST.NamedArg]()
                  for (na <- invokeExp.args) {
                    val name = na.id.value
                    val index = nameToIndexMap.get(name).get
                    r = r :+ na(arg = eArgs(index), index = mi.get(na.id.value).getOrElse(index))
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
            case _ => return (invokeExp(targs = newTargs, receiverOpt = Some(newReceiver)), None())
          }
          val (typedOpt, resOpt, tArgs) =
            checkSelectH(scope, receiverType, invokeExp.ident.id, typeArgs, reporter)
          val r = checkInvokeNamedH(typedOpt, resOpt, Some(newReceiver), invokeExp.ident(
            attr = invokeExp.ident.attr(posOpt = invokeExp.ident.id.attr.posOpt, resOpt = resOpt, typedOpt = typedOpt)),
            newTargs, tArgs)
          return r
        case _ =>
          val (typedOpt, resOpt) = checkId(scope, invokeExp.ident.id, reporter)
          val r = checkInvokeNamedH(typedOpt, resOpt, None(), invokeExp.ident(
            attr = invokeExp.ident.attr(posOpt = invokeExp.ident.id.attr.posOpt, resOpt = resOpt, typedOpt = typedOpt)),
            newTargs, typeArgs)
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
        case tOpt @ Some(_) => return (thisExp(owner = context, attr = thisExp.attr(typedOpt = tOpt)), tOpt)
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
              val (newFun, _) = checkExp(Some(AST.Typed.Fun(AST.Purity.Impure,F, ISZ(tLo), AST.Typed.b)), scope, quant.fun, reporter)
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
                  val (newFun, _) = checkExp(Some(AST.Typed.Fun(AST.Purity.Impure,F, ISZ(eType), AST.Typed.b)), scope, quant.fun, reporter)
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

    def checkStrictPureBlock(spBlock: AST.Exp.StrictPureBlock): (AST.Exp, Option[AST.Typed]) = {
      if (!inSpec) {
        reporter.error(spBlock.posOpt, typeCheckerKind, "Strict-pure blocks can only be used inside specification context.")
      }
      val (newBlock, typedOpt) = checkAssignExp(expectedOpt, scope, spBlock.block, reporter)
      if (reporter.hasError) {
        return (AST.Exp.StrictPureBlock(newBlock.asInstanceOf[AST.Stmt.Block], spBlock.attr), None())
      }
      val spc = StrictPureChecker(F, typeCheckerKind, typeHierarchy, Reporter.create)
      spc.transformAssignExp(newBlock)
      reporter.reports(spc.reporter.messages)
      val tOpt: Option[AST.Typed] = if (typedOpt.nonEmpty) {
        typedOpt
      } else {
        val exprs = newBlock.exprs
        var ts = ISZ[AST.Typed]()
        var ok = T
        for (i <- 0 until exprs.size if ok) {
          exprs(i).typedOpt match {
            case Some(t) => ts = ts :+ t
            case _ => ok = F
          }
        }
        if (ok) typeHierarchy.lub(ts) else None[AST.Typed]()
      }
      var newBlock2 = newBlock.asInstanceOf[AST.Stmt.Block]
      val newStmts = newBlock2.body.stmts
      val lastIndex = newStmts.size - 1
      newStmts(lastIndex) match {
        case stmt: AST.Stmt.If => newBlock2 = newBlock2(body = newBlock2.body(stmts =
          newStmts(lastIndex ~> stmt(attr = stmt.attr(typedOpt = tOpt)))))
        case stmt: AST.Stmt.Match => newBlock2 = newBlock2(body = newBlock2.body(stmts =
          newStmts(lastIndex ~> stmt(attr = stmt.attr(typedOpt = tOpt)))))
        case _ =>
      }
      return (AST.Exp.StrictPureBlock(newBlock2, spBlock.attr(typedOpt = tOpt)), tOpt)
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

    def checkInput(input: AST.Exp.Input): (AST.Exp, Option[AST.Typed]) = {
      if (inSpec) {
        val (e, tOpt) = checkExp(expectedOpt, scope, input.exp, reporter)
        e match {
          case e: AST.Exp.Ref =>
            e.resOpt match {
              case Some(_: AST.ResolvedInfo.LocalVar) =>
              case Some(_: AST.ResolvedInfo.Var) =>
              case _ => reporter.error(e.posOpt, typeCheckerKind, "Input can only refer to a variable or this.")
            }
          case _: AST.Exp.This =>
          case _ =>
        }
        return (input(exp = e), tOpt)
      } else {
        reporter.error(exp.posOpt, typeCheckerKind, "Input can only be used inside specification context.")
        return (input, None())
      }
    }

    def checkOld(old: AST.Exp.Old): (AST.Exp, Option[AST.Typed]) = {
      if (inSpec) {
        val (e, tOpt) = checkExp(expectedOpt, scope, old.exp, reporter)
        val ok: B = e match {
          case e: AST.Exp.Ref =>
            e.resOpt match {
              case Some(_: AST.ResolvedInfo.LocalVar) => T
              case Some(res: AST.ResolvedInfo.Var) => T
              case _ => F
            }
          case _: AST.Exp.This => T
          case _ => F
        }
        if (!ok) {
          reporter.error(e.posOpt, typeCheckerKind, st"Old can only refer to a variable.".render)
        }
        return (old(exp = e), tOpt)
      } else {
        reporter.error(exp.posOpt, typeCheckerKind, "Old can only be used inside a deduce statement.")
        return (old, None())
      }
    }

    def checkAt(at: AST.Exp.At): (AST.Exp, Option[AST.Typed]) = {
      def checkAtForm(e: AST.Exp): Unit = {
        e match {
          case e: AST.Exp.Ref =>
            e.resOpt match {
              case Some(_: AST.ResolvedInfo.LocalVar) =>
              case Some(res: AST.ResolvedInfo.Var) =>
                if (!(res.isInObject || res.owner.isEmpty)) {
                  reporter.error(e.posOpt, typeCheckerKind, "At(...) cannot refer to an instance field.")
                }
              case _ => reporter.error(e.posOpt, typeCheckerKind, "At(...) can only refer to a variable or this.")
            }
          case _: AST.Exp.This =>
          case _: AST.Exp.LitString =>
          case _ =>
        }
      }
      if (inSpec) {
        at.tipeOpt match {
          case Some(tipe) =>
            val tipeOpt = typeHierarchy.typed(scope, tipe, reporter)
            tipeOpt match {
              case Some(newTipe) =>
                val (e, _) = checkExp(AST.Typed.stringOpt, scope, at.exp, reporter)
                checkAtForm(e)
                return (at(exp = e, tipeOpt = tipeOpt), newTipe.typedOpt)
              case _ =>
                return (at(tipeOpt = tipeOpt), None())
            }
          case _ =>
            val (e, tOpt) = checkExp(expectedOpt, scope, at.exp, reporter)
            checkAtForm(e)
            return (at(exp = e), tOpt)
        }
      } else {
        reporter.error(exp.posOpt, typeCheckerKind, "At can only be used inside specification context.")
        return (exp, None())
      }
    }

    def checkRS(rs: AST.Exp.RS): (AST.Exp, Option[AST.Typed]) = {
      var thiz = this
      thiz = thiz(mode = ModeContext.RS)
      var newRefs = ISZ[AST.Exp.Ref]()
      for (ref <- rs.refs) {
        thiz.checkExp(None(), scope, AST.Exp.Eta(ref, AST.TypedAttr(ref.posOpt, ref.typedOpt)), reporter)._1 match {
          case eta: AST.Exp.Eta =>
            val newRef = eta.ref
            newRefs = newRefs :+ newRef
            var ok = F
            newRef.resOpt match {
              case Some(_: AST.ResolvedInfo.Fact) => ok = T
              case Some(_: AST.ResolvedInfo.Theorem) => ok = T
              case Some(res: AST.ResolvedInfo.Method) =>
                res.tpeOpt match {
                  case Some(tpe) if tpe.purity == AST.Purity.Abs ||
                    (tpe.ret == AST.Typed.unit && tpe.isPureFun && res.writes.isEmpty) => ok = T
                  case _ =>
                }
              case _ =>
            }
            if (!ok) {
              reporter.error(newRef.posOpt, typeCheckerKind, "Expecting a theorem/lemma, fact, method theorem/lemma, or @abs method")
            }
          case _ =>
        }
      }
      return (rs(refs = newRefs), AST.Typed.rsOpt)
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
            case exp @ AST.Exp.Invoke(None(), AST.Exp.Ident(AST.Id(name)), _, args)
                if exp.targs.isEmpty && builtInMethods.contains(name) =>
              val (kind, resOpt): (BuiltInKind.Type, Option[AST.ResolvedInfo]) =
                name match {
                  case string"assert" => (BuiltInKind.Assertume, if (args.size == z"1") assertResOpt else assertMsgResOpt)
                  case string"assume" => (BuiltInKind.Assertume, if (args.size == z"1") assumeResOpt else assumeMsgResOpt)
                  case string"println" => (BuiltInKind.Print, printlnResOpt)
                  case string"print" => (BuiltInKind.Print, printResOpt)
                  case string"cprintln" => (BuiltInKind.Print, cprintlnResOpt)
                  case string"cprint" => (BuiltInKind.Print, cprintResOpt)
                  case string"eprintln" => (BuiltInKind.Print, eprintlnResOpt)
                  case string"eprint" => (BuiltInKind.Print, eprintResOpt)
                  case string"halt" => (BuiltInKind.Halt, haltResOpt)
                  case LibUtil.setOptions => (BuiltInKind.SetOptions, setOptionsResOpt)
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
                case BuiltInKind.SetOptions => return checkSetOptions(exp, args)
              }
            case _ =>
              val (newExp, tOpt) = checkInvoke(exp)
              var r = newExp.asInstanceOf[AST.Exp.Invoke]
              if (r.receiverOpt.isEmpty) {
                def updateR(): Unit = {
                  r = r(receiverOpt = Some(AST.Exp.This(context, AST.TypedAttr(r.ident.posOpt, scope.thisOpt))))
                }
                r.ident.resOpt match {
                  case Some(res: AST.ResolvedInfo.Var) if !res.isInObject => updateR()
                  case Some(res: AST.ResolvedInfo.Method) if !res.isInObject &&
                    typeHierarchy.typeMap.get(res.owner).nonEmpty => updateR()
                  case _ =>
                }
              }
              return (r, tOpt)
          }

        case exp: AST.Exp.InvokeNamed =>
          exp match {
            case exp @ AST.Exp.InvokeNamed(None(), AST.Exp.Ident(AST.Id(name)), _, _)
                if exp.targs.isEmpty && builtInMethods.contains(name) =>
              reporter.error(exp.posOpt, typeCheckerKind, s"Cannot invoke '$name' with named argument(s).")
              return (exp, None())
            case _ =>
              val (newExp, tOpt) = checkInvokeNamed(exp)
              var r = newExp.asInstanceOf[AST.Exp.InvokeNamed]
              if (r.receiverOpt.isEmpty) {
                def updateNamedR(): Unit = {
                  r = r(receiverOpt = Some(AST.Exp.This(context, AST.TypedAttr(r.ident.posOpt, scope.thisOpt))))
                }
                r.ident.resOpt match {
                  case Some(res: AST.ResolvedInfo.Var) if !res.isInObject => updateNamedR()
                  case Some(res: AST.ResolvedInfo.Method) if !res.isInObject &&
                    typeHierarchy.typeMap.get(res.owner).nonEmpty => updateNamedR()
                  case _ =>
                }
              }
              return (r, tOpt)
          }

        case exp: AST.Exp.LitB => return (exp, exp.typedOpt)

        case exp: AST.Exp.LitC => return (exp, exp.typedOpt)

        case exp: AST.Exp.LitF32 => return (exp, exp.typedOpt)

        case exp: AST.Exp.LitF64 => return (exp, exp.typedOpt)

        case exp: AST.Exp.LitR => return (exp, exp.typedOpt)

        case exp: AST.Exp.LitString => return (exp, exp.typedOpt)

        case exp: AST.Exp.LitZ => return (exp, exp.typedOpt)

        case exp: AST.Exp.Select => return checkSelect(exp, None())

        case exp: AST.Exp.StringInterpolate => return checkStringInterpolate(exp)

        case exp: AST.Exp.Super => return checkSuper(exp)

        case exp: AST.Exp.This => return checkThis(exp)

        case exp: AST.Exp.Tuple => return checkTuple(exp)

        case exp: AST.Exp.Unary => return checkUnary(exp)

        case exp: AST.Exp.Quant => return checkQuant(exp)

        case exp: AST.Exp.Input => return checkInput(exp)

        case exp: AST.Exp.Old => return checkOld(exp)

        case exp: AST.Exp.At => return checkAt(exp)

        case exp: AST.Exp.RS => return checkRS(exp)

        case _: AST.Exp.StateSeq => halt("Unimplemented") // TODO

        case exp: AST.Exp.Result => return checkResult(exp)

        case exp: AST.Exp.StrictPureBlock => return checkStrictPureBlock(exp)

        case exp: AST.Exp.AssumeAgree =>
          return (
            exp(channel = exp.channel,
              requiresClause = exp.requiresClause(claims = for (e <- exp.requires) yield checkExp(None(), scope, e, reporter)._1),
              inAgreeClause = exp.inAgreeClause(claims = for (e <- exp.inAgrees) yield checkExp(None(), scope, e, reporter)._1)),
            exp.typedOpt)

        case exp: AST.Exp.AssertAgree =>
          return (
            exp(channel = exp.channel,
              outAgreeClause = exp.outAgreeClause(claims = for (e <- exp.outAgrees) yield checkExp(None(), scope, e, reporter)._1)),
            exp.typedOpt)

        case exp: AST.Exp.InfoFlowInvariant =>
          def checkInfoFlow(infoFlow: AST.MethodContract.InfoFlowCase): AST.MethodContract.InfoFlowCase = {
            val requires = infoFlow.requiresClause(claims = for (exp <- infoFlow.requires) yield checkExp(None(), scope, exp, reporter)._1)
            val inAgrees = infoFlow.inAgreeClause(claims = for (exp <- infoFlow.inAgrees) yield checkExp(None(), scope, exp, reporter)._1)
            val outAgrees = infoFlow.outAgreeClause(claims = for (exp <- infoFlow.outAgrees) yield checkExp(None(), scope, exp, reporter)._1)
            return AST.MethodContract.InfoFlowCase(infoFlow.label, requires, inAgrees, outAgrees)
          }

          val flows: ISZ[AST.MethodContract.InfoFlowCase] = for (infoFlow <- exp.flowInvariants) yield checkInfoFlow(infoFlow)
          return (exp(flowInvariants = flows), exp.typedOpt)

        case exp: AST.Exp.LoopIndex => return checkLoopIndex(exp)

        case exp: AST.Exp.Labeled =>
          if (!inSpec) {
            reporter.error(exp.posOpt, typeCheckerKind, s"Labeled expression can only be used inside specification context.")
          }
          val (newExp, tOpt) = checkExp(expectedOpt, scope, exp.exp, reporter)
          return (exp(exp = newExp), tOpt)

        case _: AST.Exp.Sym => halt("Infeasible")

        case exp: AST.Exp.TypeCond =>
          if (inSpec) {
            var newArgs = ISZ[AST.Exp]()
            for (arg <- exp.args) {
              val (newArg, tOpt) = checkExp(None(), scope, arg, reporter)
              tOpt match {
                case Some(t) =>
                  newArgs = newArgs :+ newArg
                  var ok = F
                  t match {
                    case t: AST.Typed.Name => typeHierarchy.typeMap.get(t.ids) match {
                      case Some(_: TypeInfo.Sig) => ok = T
                      case Some(_: TypeInfo.Adt) => ok = T
                      case _ =>
                    }
                    case _ =>
                  }
                  if (!ok) {
                    reporter.error(newArg.posOpt, typeCheckerKind, "Expecting @sig, @msig, @datatype, or @record types")
                  }
                case _ =>
              }
            }
            val (newFun, tOpt) = checkExp(None(), scope, exp.fun, reporter)
            val fun = newFun.asInstanceOf[AST.Exp.Fun]
            if (tOpt.isEmpty || newArgs.size != exp.args.size) {
              return (exp(args = newArgs, fun = fun), None())
            }
            for (p <- ops.ISZOps(fun.params).zip(newArgs)) {
              val (param, arg) = p
              val paramType = param.typedOpt.get
              val argType = arg.typedOpt.get
              if (paramType == argType || !typeHierarchy.isSubType(paramType, argType)) {
                reporter.error(param.tipeOpt.get.posOpt, typeCheckerKind, s"Parameter ${param.idOpt.get.value}'s type should be a proper subtype of '$argType', instead '$paramType was declared'")
              }
            }
            return (exp(args = newArgs, fun = fun), AST.Typed.bOpt)
          } else {
            reporter.error(exp.posOpt, typeCheckerKind, "?(...) { (...) => ... } can only be used inside specification context.")
            return (exp, None())
          }
        case _: AST.ProofAst.StepId => return (exp, exp.typedOpt)
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
        val r = checkExpr(F, expectedOpt, scope, aexp, reporter)
        return (r, r.typedOpt)
      case aexp: AST.Stmt.If => checkIf(T, expectedOpt, scope, aexp, reporter)
      case aexp: AST.Stmt.Block => checkBlock(T, expectedOpt, scope, aexp, reporter)
      case aexp: AST.Stmt.Match => checkMatch(T, expectedOpt, scope, aexp, reporter)
      case aexp: AST.Stmt.Return => checkStmt(scope, aexp, reporter)
    }

    val newAssignExp = newStmt.asAssignExp
    var tOpt = expectedOpt
    if (expectedOpt.isEmpty) {
      val exprs = newAssignExp.exprs
      if (exprs.size == 1) {
        tOpt = exprs(0).exp.typedOpt
      }
    }
    newAssignExp match {
      case newAssignExp: AST.Stmt.If =>
        return (newAssignExp(attr = newAssignExp.attr(typedOpt = tOpt)), tOpt)
      case newAssignExp: AST.Stmt.Match =>
        return (newAssignExp(attr = newAssignExp.attr(typedOpt = tOpt)), tOpt)
      case _ => return (newAssignExp, tOpt)
    }
  }

  def checkStmts(
    isAssignExp: B,
    scopes: ISZ[Scope.Local],
    expectedOpt: Option[AST.Typed],
    stmts: ISZ[AST.Stmt],
    reporter: Reporter
  ): ISZ[AST.Stmt] = {
    var newStmts = ISZ[AST.Stmt]()

    def checkStmtH(i: Z): AST.Stmt = {
      val scope: Scope.Local = if (scopes.size == 1) scopes(0) else scopes(i)
      return checkStmt(scope, stmts(i), reporter)
    }

    val size = stmts.size - 1
    for (i <- z"0" until size if !reporter.hasError) {
      val newStmt = checkStmtH(i)
      newStmts = newStmts :+ newStmt
    }

    if (reporter.hasError) {
      return newStmts ++ ops.ISZOps(stmts).slice(newStmts.size, stmts.size)
    }

    if (size < 0) {
      return newStmts
    }

    if (isAssignExp) {
      val stmt = stmts(size)
      val newScope: Scope.Local = if (scopes.size == 1) scopes(0) else scopes(size)
      val (r, _) = checkAssignExp(expectedOpt, newScope, stmt.asAssignExp, reporter)
      val newStmt = r.asStmt
      return newStmts :+ newStmt
    } else {
      val newStmt = checkStmtH(size)
      return newStmts :+ newStmt
    }
  }

  def checkStmtOpts(
    scope: Scope.Local,
    stmtOpts: ISZ[Option[AST.Stmt]],
    reporter: Reporter
  ): ISZ[Option[AST.Stmt]] = {
    var newStmtOpts = ISZ[Option[AST.Stmt]]()
    for (i <- z"0" until stmtOpts.size) {
      stmtOpts(i) match {
        case Some(stmt) =>
          val newStmt = checkStmt(scope, stmt, reporter)
          newStmtOpts = newStmtOpts :+ Some(newStmt)
        case _ => newStmtOpts = newStmtOpts :+ None()
      }
    }

    return newStmtOpts
  }

  def checkImport(scope: Scope.Local, stmt: AST.Stmt.Import, reporter: Reporter): (Option[Scope.Local], AST.Stmt) = {
    @pure def addImport(s: Scope.Local): Scope.Local = {
      s.outerOpt match {
        case Some(outer: Scope.Local) => return s(outerOpt = Some(addImport(outer)))
        case Some(outer: Scope.Global) => return s(outerOpt = Some(outer(imports = outer.imports :+ stmt)))
        case _ => halt("Unexpected local scope without an outer scope.")
      }
    }

    Resolver.checkImport(scope.packageName, stmt, typeHierarchy.nameMap, typeHierarchy.typeMap, reporter)

    return (Some(addImport(scope)), stmt)
  }

  def checkBody(isWorksheet: B, isAssignExp: B, expectedOpt: Option[AST.Typed], sc: Scope.Local, body: AST.Body,
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
          reporter.error(posOpt, typeCheckerKind,
            s"Cannot declare method '$id' as it has been previously declared in the enclosing context.")
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
            reporter.error(posOpt, typeCheckerKind,
              s"Cannot declare method '$id' as it has been previously declared in the enclosing context.")
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
          val infoOpt = to.outlineMethod(Info.Method(context, isWorksheet, scope, T, stmt(attr = stmt.attr(
            resOpt = Some(AST.ResolvedInfo.Method(isWorksheet, AST.MethodMode.Method,
              stmt.sig.typeParams.map(tp => tp.id.value), context, id, stmt.sig.params.map(p => p.id.value),
              None(), ISZ(), ISZ()))))),
            reporter)
          infoOpt match {
            case Some(info: Info.Method) =>
              scope = scope(nameMap = scope.nameMap + id ~> info)
              stmts = stmts :+ info.ast
            case _ => ok = F
          }
        case stmt: AST.Stmt.SpecMethod =>
          val id = stmt.sig.id.value
          checkLocalId(id, stmt.posOpt)
          val infoOpt = to.outlineSpecMethod(Info.SpecMethod(context, isWorksheet, scope, stmt(attr = stmt.attr(
            resOpt = Some(AST.ResolvedInfo.Method(isWorksheet, AST.MethodMode.Spec,
              stmt.sig.typeParams.map(tp => tp.id.value), context, id, stmt.sig.params.map(p => p.id.value), None(),
              ISZ(), ISZ()))))),
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
    var tcNameMap = typeHierarchy.nameMap
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
          val newStmt = TypeChecker.checkFactStmt(strictAliasing, typeHierarchy, name, scope,
            stmt(attr = stmt.attr(resOpt = Some(AST.ResolvedInfo.Fact(name)))), reporter)
          stmts2 = stmts2 :+ newStmt
          val info = typeHierarchy.nameMap.get(name).get.asInstanceOf[Info.Fact]
          val newInfo = info(ast = newStmt)
          nameMap = nameMap + id ~> newInfo
          tcNameMap = tcNameMap + name ~> newInfo
        case stmt: AST.Stmt.Theorem =>
          val id = stmt.id.value
          val name = context :+ id
          val newStmt = TypeChecker.checkTheoremStmt(strictAliasing, typeHierarchy, name, scope,
            stmt(attr = stmt.attr(resOpt = Some(AST.ResolvedInfo.Theorem(name)))), reporter)
          stmts2 = stmts2 :+ newStmt
          val info = typeHierarchy.nameMap.get(name).get.asInstanceOf[Info.Theorem]
          val newInfo = info(ast = newStmt)
          nameMap = nameMap + id ~> newInfo
          tcNameMap = tcNameMap + name ~> newInfo
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
    val scopes = mscopes.toIS[Scope.Local]
    val thisL = this
    val newStmts = thisL(typeHierarchy = typeHierarchy(nameMap = tcNameMap)).checkStmts(isAssignExp, scopes,
      expectedOpt, stmts2, reporter)
    val newScope: Scope.Local = if (scopes.isEmpty) scope else scopes(scopes.size - 1)
    val undecls: ISZ[AST.ResolvedInfo.LocalVar] = {
      var r = ISZ[AST.ResolvedInfo.LocalVar]()
      for (e <- newScope.nameMap.entries) {
        e._2 match {
          case _: Info.Method =>
          case _: Info.SpecMethod =>
          case _: Info.SpecVar =>
          case _: Info.Var =>
          case Info.LocalVar(_, _, _, _, _, Some(res: AST.ResolvedInfo.LocalVar)) => r = r :+ res
          case _: Info.LocalVar =>
          case _ => halt(s"Infeasible: ${e._2}")
        }
      }
      r
    }
    return (scope, body(stmts = newStmts, undecls = undecls))
  }

  def checkTypeRelation(title: String, posOpt: Option[Position], expectedType: AST.Typed, t: AST.Typed, reporter: Reporter): B = {
    if (t != expectedType && typeHierarchy.isSubType(expectedType, t)) {
      // OK
    } else {
      if (t == expectedType) {
        reporter.warn(posOpt, typeCheckerKind,
          s"Unnecessary $title because it is always going to be successful (i.e.,  $t ≡ $expectedType).")
        return F
      } else if (typeHierarchy.glb(ISZ(expectedType, t)).isEmpty) {
        reporter.error(posOpt, typeCheckerKind,
          s"Fruitless $title because it is always going to be unsuccessful (i.e., $t and $expectedType do not have a common subtype).")
        return F
      }
    }
    return T
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
            reporter.error(id.attr.posOpt, typeCheckerKind,
              s"Cannot declare '$key' because the identifier has already been previously declared.")
            ok = F
          } else {
            if (checkMutable) {
              val t = tOpt.get
              val refined: B = unrefinedIdOpt match {
                case Some(uid) => uid.value == id.value
                case _ => F
              }
              if (!refined && typeHierarchy.isMutable(t)) {
                val possibly: String = if (t.hasTypeVars) " possibly" else ""
                reporter.error(id.attr.posOpt, typeCheckerKind,
                  s"Cannot introduce a new name '$key' for$possibly mutable type '$t'.")
              }
            }
            scope = scope(nameMap = scope.nameMap + key ~> Info.LocalVar(context :+ key, F, id, tOpt, None(),
              Some(AST.ResolvedInfo.LocalVar(context, AST.ResolvedInfo.LocalVar.Scope.Current, isSpec, T, key))))
          }
        case _ =>
      }
    }

    def checkTipe(expectedType: AST.Typed, tipe: AST.Type): AST.Type = {
      val newTipeOpt = typeHierarchy.typed(scope, tipe, reporter)
      newTipeOpt match {
        case Some(newTipe) if newTipe.typedOpt.nonEmpty =>
          val t = newTipe.typedOpt.get
          if (!checkTypeRelation("type matching", tipe.posOpt, expectedType, t, reporter)) {
            ok = F
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
                reporter.error(pattern.posOpt, typeCheckerKind,
                  s"Fruitless matching because it is always going to be unsuccessful (i.e., $t and $expectedType do not have a common subtype).")
                ok = F
              }
              val (rcvOpt, ctx): (Option[AST.Typed.Name], ISZ[String]) = resOpt match {
                case Some(res: AST.ResolvedInfo.Var) if !res.isInObject =>
                  (Some(scope.thisOpt.get.asInstanceOf[AST.Typed.Name]), context)
                case _ => (None(), ISZ())
              }
              return pattern(receiverTipeOpt = rcvOpt, idContext = ctx, attr = pattern.attr(typedOpt = Some(t), resOpt = resOpt))
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
                return pattern(tipeOpt = Some(newTipe), idContext = context, attr = pattern.attr(typedOpt = tOpt))
              }
              return pattern(tipeOpt = Some(newTipe), idContext = context)
            case _ =>
              val tOpt: Option[AST.Typed] = Some(expectedType)
              declId(F, Some(pattern.id), tOpt)
              return pattern(idContext = context, attr = pattern.attr(typedOpt = tOpt))
          }
        case pattern: AST.Pattern.Structure =>
          pattern.nameOpt match {
            case Some(nm) =>
              val ti: TypeInfo = scope.resolveType(typeHierarchy.typeMap, nm.ids.map(id => id.value)) match {
                case Some(info) => info
                case nms =>
                  reporter.error(pattern.posOpt, typeCheckerKind, st"Could not resolve type named ${(nms, ".")}.".render)
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
                  idContext = context,
                  attr = pattern.attr(typedOpt = tOpt, resOpt = AST.Typed.unapplySeqResOpt)
                )
              }
              (ti.name, expectedType) match {
                case (AST.Typed.isName, AST.Typed.Name(AST.Typed.isName, argTypes)) => val r = s(argTypes(1)); return r
                case (AST.Typed.msName, AST.Typed.Name(AST.Typed.msName, argTypes)) => val r = s(argTypes(1)); return r
                case (AST.Typed.iszName, AST.Typed.Name(AST.Typed.isName, argTypes)) =>
                  if (argTypes(0) != AST.Typed.z) {
                    reporter.error(pattern.posOpt, typeCheckerKind,
                      st"Expecting an '${(AST.Typed.isName, ".")}' with index type '${AST.Typed.z}', but index type '${argTypes(0)}' found.".render)
                    return pattern
                  }
                  val r = s(argTypes(1))
                  return r
                case (AST.Typed.mszName, AST.Typed.Name(AST.Typed.msName, argTypes)) =>
                  if (argTypes(0) != AST.Typed.z) {
                    reporter.error(pattern.posOpt, typeCheckerKind,
                      st"Expecting an '${(AST.Typed.msName, ".")}' with index type '${AST.Typed.z}', but index type '${argTypes(0)}' found.".render)
                    return pattern
                  }
                  val r = s(argTypes(1))
                  return r
                case (AST.Typed.zsName, AST.Typed.Name(AST.Typed.msName, argTypes)) =>
                  var ok2 = T
                  if (argTypes(0) != AST.Typed.z) {
                    reporter.error(pattern.posOpt, typeCheckerKind,
                      st"Expecting an '${(AST.Typed.msName, ".")}' with index type '${AST.Typed.z}', but index type '${argTypes(0)}' found.".render)
                    ok2 = F
                  }
                  if (argTypes(1) != AST.Typed.z) {
                    reporter.error(pattern.posOpt, typeCheckerKind,
                      st"Expecting an '${(AST.Typed.msName, ".")}' with element type '${AST.Typed.z}', but element type '${argTypes(1)}' found.".render)
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
                        return pattern(idContext = context, attr = pattern.attr(resOpt = info.extractorResOpt))
                      }

                      val size = info.extractorTypeMap.size
                      if (size != pattern.patterns.size) {
                        reporter.error(pattern.posOpt, typeCheckerKind,
                          s"Expecting $size patterns, but ${pattern.patterns.size} found.")
                        ok = F
                        return partialResult
                      }
                      val smOpt = unify(typeHierarchy, pattern.posOpt, TypeRelation.Subtype, expected, info.tpe, reporter)
                      smOpt match {
                        case Some(sm) =>
                          val ok2 = checkUnboundTypeVar(pattern.posOpt, info.tpe, sm,
                            info.ast.typeParams.map(tp => tp.id.value), reporter)
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
                            idContext = context,
                            attr = pattern.attr(typedOpt = typedOpt, resOpt = info.extractorResOpt)
                          )
                        case _ => return partialResult
                      }
                    case Some(_) =>
                      reporter.error(pattern.posOpt, typeCheckerKind,
                        st"Cannot pattern match on type $expected using ${(name, ".")}.".render)
                      ok = F
                      return pattern
                    case _ =>
                  }
                case _ =>
                  expectedType match {
                    case _: AST.Typed.Name =>
                      reporter.error(pattern.posOpt, typeCheckerKind,
                        st"Cannot pattern match on type $expectedType using ${(name, ".")}.".render)
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
                    reporter.error(pattern.posOpt, typeCheckerKind,
                      s"Expecting $size patterns, but ${pattern.patterns.size} found.")
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
                    idContext = context,
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
    isStmt: B,
    expectedOpt: Option[AST.Typed],
    scope: Scope.Local,
    stmt: AST.Stmt.Expr,
    reporter: Reporter
  ): AST.Stmt.Expr = {
    val (newExp, typedOpt) = checkExp(expectedOpt, scope, stmt.exp, reporter)
    if (isStmt) {
      newExp match {
        case newExp: AST.Exp.Binary =>
          newExp.attr.resOpt match {
            case Some(AST.ResolvedInfo.BuiltIn(kind)) =>
              val ok: B = kind match {
                case AST.ResolvedInfo.BuiltIn.Kind.Apply => T
                case AST.ResolvedInfo.BuiltIn.Kind.AsInstanceOf => F
                case AST.ResolvedInfo.BuiltIn.Kind.Assert => T
                case AST.ResolvedInfo.BuiltIn.Kind.AssertMsg => T
                case AST.ResolvedInfo.BuiltIn.Kind.Assume => T
                case AST.ResolvedInfo.BuiltIn.Kind.AssumeMsg => T
                case AST.ResolvedInfo.BuiltIn.Kind.BinaryAdd => F
                case AST.ResolvedInfo.BuiltIn.Kind.BinarySub => F
                case AST.ResolvedInfo.BuiltIn.Kind.BinaryMul => F
                case AST.ResolvedInfo.BuiltIn.Kind.BinaryDiv => F
                case AST.ResolvedInfo.BuiltIn.Kind.BinaryRem => F
                case AST.ResolvedInfo.BuiltIn.Kind.BinaryEq => F
                case AST.ResolvedInfo.BuiltIn.Kind.BinaryEquiv => F
                case AST.ResolvedInfo.BuiltIn.Kind.BinaryNe => F
                case AST.ResolvedInfo.BuiltIn.Kind.BinaryInequiv => F
                case AST.ResolvedInfo.BuiltIn.Kind.BinaryFpEq => F
                case AST.ResolvedInfo.BuiltIn.Kind.BinaryFpNe => F
                case AST.ResolvedInfo.BuiltIn.Kind.BinaryLt => F
                case AST.ResolvedInfo.BuiltIn.Kind.BinaryLe => F
                case AST.ResolvedInfo.BuiltIn.Kind.BinaryGt => F
                case AST.ResolvedInfo.BuiltIn.Kind.BinaryGe => F
                case AST.ResolvedInfo.BuiltIn.Kind.BinaryShl => F
                case AST.ResolvedInfo.BuiltIn.Kind.BinaryShr => F
                case AST.ResolvedInfo.BuiltIn.Kind.BinaryUshr => F
                case AST.ResolvedInfo.BuiltIn.Kind.BinaryAnd => F
                case AST.ResolvedInfo.BuiltIn.Kind.BinaryOr => F
                case AST.ResolvedInfo.BuiltIn.Kind.BinaryXor => F
                case AST.ResolvedInfo.BuiltIn.Kind.BinaryImply => F
                case AST.ResolvedInfo.BuiltIn.Kind.BinaryCondAnd => F
                case AST.ResolvedInfo.BuiltIn.Kind.BinaryCondOr => F
                case AST.ResolvedInfo.BuiltIn.Kind.BinaryCondImply => F
                case AST.ResolvedInfo.BuiltIn.Kind.BinaryMapsTo => F
                case AST.ResolvedInfo.BuiltIn.Kind.Cprint => T
                case AST.ResolvedInfo.BuiltIn.Kind.Cprintln => T
                case AST.ResolvedInfo.BuiltIn.Kind.Eprint => T
                case AST.ResolvedInfo.BuiltIn.Kind.Eprintln => T
                case AST.ResolvedInfo.BuiltIn.Kind.Halt => T
                case AST.ResolvedInfo.BuiltIn.Kind.Hash => F
                case AST.ResolvedInfo.BuiltIn.Kind.IsInstanceOf => F
                case AST.ResolvedInfo.BuiltIn.Kind.Indices => F
                case AST.ResolvedInfo.BuiltIn.Kind.Min => F
                case AST.ResolvedInfo.BuiltIn.Kind.Max => F
                case AST.ResolvedInfo.BuiltIn.Kind.Native => F
                case AST.ResolvedInfo.BuiltIn.Kind.Print => T
                case AST.ResolvedInfo.BuiltIn.Kind.Println => T
                case AST.ResolvedInfo.BuiltIn.Kind.SetOptions => T
                case AST.ResolvedInfo.BuiltIn.Kind.String => F
                case AST.ResolvedInfo.BuiltIn.Kind.UnapplySeq => F
                case AST.ResolvedInfo.BuiltIn.Kind.UnapplyTuple => F
                case AST.ResolvedInfo.BuiltIn.Kind.UnaryPlus => F
                case AST.ResolvedInfo.BuiltIn.Kind.UnaryMinus => F
                case AST.ResolvedInfo.BuiltIn.Kind.UnaryNot => F
                case AST.ResolvedInfo.BuiltIn.Kind.UnaryComplement => F
                case AST.ResolvedInfo.BuiltIn.Kind.Arrow => F
              }
              if (!ok) {
                reporter.error(newExp.posOpt, typeCheckerKind, s"Ill-formed Slang statement")
              }
            case _ =>
          }
        case _ =>
      }
    }
    return stmt(exp = newExp, attr = stmt.attr(typedOpt = typedOpt))
  }

  def checkBlock(
    isAssignExp: B,
    expectedOpt: Option[AST.Typed],
    scope: Scope.Local,
    stmt: AST.Stmt.Block,
    reporter: Reporter
  ): AST.Stmt = {
    val (_, newBody) = checkBody(F, isAssignExp, expectedOpt, createNewScope(scope), stmt.body, reporter)
    return stmt(body = newBody)
  }

  def checkIf(isAssignExp: B, expectedOpt: Option[AST.Typed], scope: Scope.Local, stmt: AST.Stmt.If, reporter: Reporter): AST.Stmt = {
    val (newCond, _) = checkExp(AST.Typed.bOpt, scope, stmt.cond, reporter)
    val (_, tBody) = checkBody(F, isAssignExp, expectedOpt, createNewScope(scope), stmt.thenBody, reporter)
    val (_, eBody) = checkBody(F, isAssignExp, expectedOpt, createNewScope(scope), stmt.elseBody, reporter)
    return stmt(cond = newCond, thenBody = tBody, elseBody = eBody,
      attr = if (isAssignExp) stmt.attr else stmt.attr(typedOpt = AST.Typed.unitOpt))
  }

  def checkMatch(
    isAssignExp: B,
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
              reporter.error(receiver.posOpt, typeCheckerKind,
                s"Selector native is only usable from type C, String, F32, and F64, but '$t' found.")
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

    if (stmt.isInduct) {
      if (mode != ModeContext.TheoremCode) {
        reporter.error(stmt.exp.posOpt, typeCheckerKind, s"@induct can only be used in a theorem/lemma @pure method returning the unit type")
      }
      if (newExp.typedOpt.nonEmpty) {
        typeHierarchy.induct(T, HashSet.empty, context, AST.Util.trueLit, newExp, newExp.posOpt.get) match {
          case Some(_) =>
          case _ => reporter.error(newExp.posOpt, typeCheckerKind, s"Cannot @induct over ${newExp.prettyST}")
        }
      }
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
          val (_, newBody) = checkBody(F, isAssignExp, expectedOpt, newScope, c.body, reporter)
          newCases = newCases :+ c(pattern = newPattern, condOpt = newCondOpt, body = newBody)
        case _ => newCases = newCases :+ c(pattern = newPattern)
      }
    }

    return stmt(exp = newExp, cases = newCases,
      attr = if (isAssignExp) stmt.attr else stmt.attr(typedOpt = AST.Typed.unitOpt))
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
          nameMap = scope.nameMap + key ~> Info.LocalVar(name, stmt.isVal, r.id, typedOpt, rhsOpt, resOpt)
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
          reporter.error(stmt.pattern.posOpt, typeCheckerKind,
            s"Could not infer the expected type for $varText pattern to match to.")
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
              reporter.error(lhs.id.attr.posOpt, typeCheckerKind,
                st"Cannot assign to val '${lhs.id.value}' of '${(owner, ".")}'.".render)
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
                  reporter.error(lhs.id.attr.posOpt, typeCheckerKind,
                    st"'${lhs.id.value}' is not a var of '${(info.name, ".")}'.".render)
                case Some(info: TypeInfo.Sig) =>
                  info.specVars.get(lhs.id.value) match {
                    case Some(varInfo) => return checkSelectSpecVarInfo(t, info.ast.typeParams, varInfo)
                    case _ =>
                  }
                  reporter.error(lhs.id.attr.posOpt, typeCheckerKind,
                    st"'${lhs.id.value}' is not a var of '${(info.name, ".")}'.".render)
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
                  reporter.error(lhs.id.attr.posOpt, typeCheckerKind,
                    st"'${lhs.id.value}' is not a var of '${(t.owner :+ t.id, ".")}'.".render)
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
                  ident = lhs.ident(attr = lhs.ident.attr(posOpt = lhs.ident.id.attr.posOpt, resOpt = resOpt, typedOpt = receiverTypeOpt)),
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
          val (_, newBody) = checkBody(F, F, None(), newScope, forStmt.body, reporter)
          return forStmt(context = context, enumGens = newEnumGens, body = newBody)
        case _ => return forStmt(context = context, enumGens = newEnumGens)
      }
    }

    def checkDoWhile(doWhileStmt: AST.Stmt.DoWhile): AST.Stmt = {
      val thisL = this
      val (newInvs, newMods) = thisL(mode = ModeContext.Spec).
        checkLoopInv(scope, doWhileStmt.contract.invariants, doWhileStmt.contract.modifies, reporter)
      val (_, newBody) = checkBody(F, F, None(), createNewScope(scope), doWhileStmt.body, reporter)
      val (newCond, _) = checkExp(AST.Typed.bOpt, scope, doWhileStmt.cond, reporter)
      return doWhileStmt(context = context, cond = newCond,
        contract = doWhileStmt.contract(invariants = newInvs, modifies = newMods), body = newBody)
    }

    def checkWhile(whileStmt: AST.Stmt.While): AST.Stmt = {
      val (newCond, _) = checkExp(AST.Typed.bOpt, scope, whileStmt.cond, reporter)
      val thisL = this
      val (newInvs, newMods) = thisL(mode = ModeContext.Spec).
        checkLoopInv(scope, whileStmt.contract.invariants, whileStmt.contract.modifies, reporter)
      val (_, newBody) = checkBody(F, F, None(), createNewScope(scope), whileStmt.body, reporter)
      return whileStmt(context = context, cond = newCond,
        contract = whileStmt.contract(invariants = newInvs, modifies = newMods), body = newBody)
    }

    def checkReturn(returnStmt: AST.Stmt.Return): AST.Stmt = {
      (scope.returnOpt, returnStmt.expOpt) match {
        case (tOpt @ Some(_), Some(exp)) =>
          val (newExp, expTypeCond) = checkExp(tOpt, scope, exp, reporter)
          expTypeCond match {
            case Some(t) =>
              if (!typeHierarchy.isSubType(t, tOpt.get)) {
                reporter.error(exp.posOpt, typeCheckerKind,
                  s"Expecting type '${tOpt.get}', but incompatible type '$t' found.")
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

      case stmt: AST.Stmt.Block => return checkBlock(F, None(), scope, stmt, reporter)

      case stmt: AST.Stmt.DoWhile => return checkDoWhile(stmt)

      case stmt: AST.Stmt.Enum => return stmt

      case stmt: AST.Stmt.Expr => return checkExpr(T, None(), scope, stmt, reporter)

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

      case stmt: AST.Stmt.If => return checkIf(F, None(), scope, stmt, reporter)

      case stmt: AST.Stmt.Import => return checkImport(scope, stmt, reporter)._2

      case stmt: AST.Stmt.Match => return checkMatch(F, None(), scope, stmt, reporter)

      case stmt: AST.Stmt.Method =>
        if (stmt.typeChecked) {
          return stmt
        }
        val m: ModeContext.Type =
          if (stmt.sig.purity == AST.Purity.Pure && stmt.sig.returnType.typedOpt == AST.Typed.unitOpt)
            ModeContext.TheoremCode
          else mode
        val tc = TypeChecker(typeHierarchy, context :+ stmt.sig.id.value, isInMutableContext, m, strictAliasing)
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

      case stmt: AST.Stmt.RsVal => return stmt

      case stmt: AST.Stmt.SpecBlock =>
        val tc = TypeChecker(typeHierarchy, context, isInMutableContext, ModeContext.Spec, strictAliasing)
        return stmt(block = tc.checkStmt(scope, stmt.block, reporter).asInstanceOf[AST.Stmt.Block])

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

      case stmt: AST.Stmt.Induct =>
        val newExp = checkExp(None(), scope, stmt.exp, reporter)._1
        val locals = ops.ISZOps(scope.localIds).sortWith((s1: String, s2: String) => s1 <= s2)
        if (newExp.typedOpt.nonEmpty) {
          typeHierarchy.induct(T, HashSet.empty, context, AST.Util.trueLit, newExp, newExp.posOpt.get) match {
            case Some(_) =>
            case _ => reporter.error(newExp.posOpt, typeCheckerKind, s"Cannot @induct over ${newExp.prettyST}")
          }
        }
        return stmt(exp = newExp, context = context, locals = locals)
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
      val errMessage = "Expecting an object @just/@pure method with Unit return type, fact or theorem"
      just match {
        case just: AST.ProofAst.Step.Justification.Ref =>
          val (newRef, _) = checkExp(None(), scope, just.ref.asExp, reporter)
          val newJust = just(ref = newRef.asInstanceOf[AST.Exp.Ref])
          newJust.resOpt match {
            case Some(res: AST.ResolvedInfo.Method) if res.mode == AST.MethodMode.Just =>
            case Some(_) => reporter.error(newRef.posOpt, typeCheckerKind, "Expecting an object @just method")
            case _ =>
          }
          return newJust
        case just: AST.ProofAst.Step.Justification.Apply =>
          val newInvoke = checkExp(None(), scope, just.invoke, reporter)._1.asInstanceOf[AST.Exp.Invoke]
          newInvoke.ident.typedOpt match {
            case Some(t: AST.Typed.Method) if t.isInObject && (t.tpe.isPureFun || t.mode == AST.MethodMode.Just) && t.tpe.ret == AST.Typed.unit =>
            case Some(_: AST.Typed.Fact) =>
            case Some(_: AST.Typed.Theorem) =>
            case Some(_) => reporter.error(newInvoke.posOpt, typeCheckerKind, errMessage)
            case _ =>
          }
          return just(invoke = newInvoke)
        case just: AST.ProofAst.Step.Justification.ApplyNamed =>
          val (newExp, tOpt) = checkExp(None(), scope, just.invoke, reporter)
          val r = just(invoke = newExp.asInstanceOf[AST.Exp.InvokeNamed])
          if (tOpt.isEmpty) {
            return r
          }
          r.invoke.ident.typedOpt match {
            case Some(t: AST.Typed.Method) if t.isInObject && (t.tpe.isPureFun || t.mode == AST.MethodMode.Just) &&
              t.tpe.ret == AST.Typed.unit =>
            case Some(_) => reporter.error(r.invoke.posOpt, typeCheckerKind, errMessage)
            case _ =>
          }
          return AST.ProofAst.Step.Justification.Apply(
            invoke = AST.Exp.Invoke(r.invoke.receiverOpt, r.invoke.ident, r.invoke.targs, r.args, r.invoke.attr),
            hasWitness = r.hasWitness, witnesses = r.witnesses)
        case just: AST.ProofAst.Step.Justification.ApplyEta =>
          val newEta = checkExp(None(), scope, just.eta, reporter)._1.asInstanceOf[AST.Exp.Eta]
          newEta.ref.asExp.typedOpt match {
            case Some(t: AST.Typed.Method) if t.isInObject && (t.tpe.isPureFun || t.mode == AST.MethodMode.Just) && t.tpe.ret == AST.Typed.unit =>
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
            reporter.error(id.attr.posOpt, typeCheckerKind,
              s"Cannot declare '$key' because the identifier has already been previously declared.")
            ok = F
          } else {
            sc = sc(nameMap = sc.nameMap + key ~> Info.LocalVar(context :+ key, F, id, tOpt, None(),
              Some(AST.ResolvedInfo.LocalVar(context, AST.ResolvedInfo.LocalVar.Scope.Current, T, T, key))))
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
    }
  }

  def checkMethodContract(scope: Scope.Local, contract: AST.MethodContract, reporter: Reporter): AST.MethodContract = {
    assert(inSpec)
    def checkReads(reads: AST.MethodContract.Accesses): AST.MethodContract.Accesses = {
      def err(posOpt: Option[Position]): Unit = {
        reporter.error(posOpt, typeCheckerKind, s"Can only read a variable or an object field (qualified name)")
      }
      var r = ISZ[AST.Exp.Ref]()
      for (read <- reads.refs) {
        val newRef = checkExp(None(), scope, read.asExp, reporter)._1.asInstanceOf[AST.Exp.Ref]
        newRef.resOpt match {
          case Some(_: AST.ResolvedInfo.LocalVar) =>
          case Some(_: AST.ResolvedInfo.Var) =>
          case Some(_) => err(newRef.posOpt)
          case _ =>
        }
        newRef match {
          case newRef: AST.Exp.Select =>
            newRef.receiverOpt match {
              case Some(receiver: AST.Exp.Ref) =>
                receiver.resOpt match {
                  case Some(_: AST.ResolvedInfo.Package) =>
                  case Some(_: AST.ResolvedInfo.Object) =>
                  case Some(_) => err(newRef.posOpt)
                  case _ =>
                }
              case _ =>
            }
          case _ =>
        }
        r = r :+ newRef
      }
      return reads(refs = r)
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
    def checkInfoFlows(infoFlows: AST.MethodContract.InfoFlows): AST.MethodContract.InfoFlows = {
      val tcSpecPost = TypeChecker(typeHierarchy, context, isInMutableContext, ModeContext.SpecPost, strictAliasing)

      def checkInfoFlow(e: AST.MethodContract.InfoFlowElement): AST.MethodContract.InfoFlowElement = {
        e match {
          case infoFlow: AST.MethodContract.InfoFlowFlow =>
            val froms = infoFlow.fromClause(claims = for (exp <- infoFlow.froms) yield checkExp(None(), scope, exp, reporter)._1)
            val tos = infoFlow.toClause(claims = for (exp <- infoFlow.tos) yield tcSpecPost.checkExp(None(), scope, exp, reporter)._1)
            return AST.MethodContract.InfoFlowFlow(infoFlow.label, froms, tos)
          case infoFlow: AST.MethodContract.InfoFlowCase =>
            val requires = infoFlow.requiresClause(claims = for (exp <- infoFlow.requires) yield checkExp(None(), scope, exp, reporter)._1)
            val inAgrees = infoFlow.inAgreeClause(claims = for (exp <- infoFlow.inAgrees) yield checkExp(None(), scope, exp, reporter)._1)
            val outAgrees = infoFlow.outAgreeClause(claims = for (exp <- infoFlow.outAgrees) yield tcSpecPost.checkExp(None(), scope, exp, reporter)._1)
            return AST.MethodContract.InfoFlowCase(infoFlow.label, requires, inAgrees, outAgrees)
          case infoFlow: AST.MethodContract.InfoFlowGroup =>
            val members = infoFlow.membersClause(claims = for (exp <- infoFlow.members) yield checkExp(None(), scope, exp, reporter)._1)
            return AST.MethodContract.InfoFlowGroup(infoFlow.label, members)
        }
      }
      return AST.MethodContract.InfoFlows(for(infoFlow <- infoFlows.flows) yield checkInfoFlow(infoFlow), infoFlows.attr)
    }

    contract match {
      case contract: AST.MethodContract.Simple =>
        return AST.MethodContract.Simple(
          checkReads(contract.readsClause), checkRequires(contract.requiresClause),
          checkModifies("modify", scope, contract.modifiesClause, reporter), checkEnsures(contract.ensuresClause),
          checkInfoFlows(contract.infoFlowsClause), contract.attr
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

  def checkLoopInv(scope: Scope.Local, invs: ISZ[AST.Exp], modifies: ISZ[AST.Exp.Ref],
                   reporter: Reporter): (ISZ[AST.Exp], ISZ[AST.Exp.Ref]) = {
    val newInvs: ISZ[AST.Exp] = for (inv <- invs) yield checkExp(AST.Typed.bOpt, scope, inv, reporter)._1
    return (newInvs, checkModifiesH("modify", scope, modifies, reporter))
  }

  def checkModifies(title: String, scope: Scope.Local, modifies: AST.MethodContract.Accesses, reporter: Reporter): AST.MethodContract.Accesses = {
    return modifies(refs = checkModifiesH(title, scope, modifies.refs, reporter))
  }

  def checkModifiesH(title: String, scope: Scope.Local, modifies: ISZ[AST.Exp.Ref], reporter: Reporter): ISZ[AST.Exp.Ref] = {
    return for (modify <- modifies) yield checkModifyExp(title, scope, modify, reporter)
  }

  def checkModifyExp(title: String, sc: Scope.Local, ref: AST.Exp.Ref, reporter: Reporter): AST.Exp.Ref = {
    def checkLocalVar(res: AST.ResolvedInfo.LocalVar, t: AST.Typed): Unit = {
      if (!typeHierarchy.isModifiable(t) && res.isVal) {
        reporter.error(ref.posOpt, typeCheckerKind, s"Cannot $title variable '${res.id}'")
      }
    }
    def checkVar(res: AST.ResolvedInfo.Var, t: AST.Typed): Unit = {
      if (!typeHierarchy.isModifiable(t) && res.isVal) {
        reporter.error(ref.posOpt, typeCheckerKind, st"Cannot $title field '${(res.owner :+ res.id, ".")}'".render)
      }
    }
    def err(): Unit = {
      reporter.error(ref.posOpt, typeCheckerKind, s"Can only $title a variable or an object field (qualified name)")
    }
    val (newExp, tOpt) = checkExp(None(), sc, ref.asExp, reporter)
    tOpt match {
      case Some(t) =>
        val newRef = newExp.asInstanceOf[AST.Exp.Ref]
        newRef.resOpt.get match {
          case res: AST.ResolvedInfo.LocalVar => checkLocalVar(res, t)
          case res: AST.ResolvedInfo.Var => checkVar(res, t)
          case _ => err()
        }
        newRef match {
          case newRef: AST.Exp.Select =>
            newRef.receiverOpt match {
              case Some(receiver: AST.Exp.Ref) =>
                receiver.resOpt.get match {
                  case _: AST.ResolvedInfo.Package =>
                  case _: AST.ResolvedInfo.Object =>
                  case _ => err()
                }
              case _ =>
            }
          case _ =>
        }
        return newRef
      case _ => err()
    }
    return ref
  }

  def checkMethod(sc: Scope, stmt: AST.Stmt.Method, reporter: Reporter): AST.Stmt.Method = {

    def checkMethodH(): AST.Stmt.Method = {
      if (stmt.bodyOpt.isEmpty) {
        return stmt
      }
      val (ok, scope) = methodScope(typeHierarchy, context, sc, stmt.sig, reporter)
      if (ok) {
        val (_, newBody) = checkBody(F, F, None(), scope, stmt.bodyOpt.get, reporter)
        return stmt(typeChecked = T, bodyOpt = Some(newBody))
      } else {
        return stmt
      }
    }
    val r = checkMethodH()
    if (!reporter.hasError) {
      if (r.purity != AST.Purity.StrictPure) {
        if (r.sig.returnType.typedOpt.get != AST.Typed.unit) {
          r.bodyOpt match {
            case Some(body) =>
              for (lOpt <- body.leaves) {
                lOpt match {
                  case Some(_: AST.Stmt.Return) =>
                  case Some(AST.Stmt.Expr(exp: AST.Exp.Invoke)) =>
                    exp.attr.resOpt match {
                      case Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Halt)) =>
                      case _ =>
                        reporter.error(exp.posOpt, TypeChecker.typeCheckerKind,
                          s"Non-unit methods should end with a return/halt statement")
                    }
                  case Some(stmt2) =>
                    reporter.error(stmt2.posOpt, TypeChecker.typeCheckerKind,
                      s"Non-unit methods should end with a return/halt statement")
                  case _ =>
                    val posOpt: Option[Position] =
                      if (body.stmts.isEmpty) r.sig.id.attr.posOpt else body.stmts(body.stmts.size - 1).posOpt
                    reporter.error(posOpt,
                      TypeChecker.typeCheckerKind, s"Non-unit methods should end with a return/halt statement")
                }
              }
            case _ =>
          }
        }
      } else {
        r.bodyOpt match {
          case Some(body) if !reporter.hasError =>
            val spc = TypeChecker.StrictPureChecker(T, TypeChecker.typeCheckerKind, typeHierarchy, Reporter.create)
            body.stmts match {
              case ISZ(stmt: AST.Stmt.Var, _: AST.Stmt.Return) => spc.transformAssignExp(stmt.initOpt.get)
              case ISZ(stmt: AST.Stmt.Return) => stmt.expOpt match {
                case Some(exp) => spc.transformExp(exp)
                case _ =>
              }
              case _ => spc.transformBody(body)
            }
            reporter.reports(spc.reporter.messages)
          case _ =>
        }
      }
    }
    return r
  }

  def checkDataRefinement(scope: Scope.Local, stmt: AST.Stmt.DataRefinement, reporter: Reporter): AST.Stmt.DataRefinement = {
    val rep = checkModifyExp("represent data using", scope, stmt.rep, reporter)
    val refs: ISZ[AST.Exp.Ref] = for (m <- stmt.refs) yield checkModifyExp("refine data using", scope, m, reporter)
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
    var nameMap = HashMap.empty[String, Info]
    for (p <- info.vars.map.entries) {
      nameMap = nameMap + p._1 ~> p._2
    }
    for (p <- info.specVars.map.entries) {
      nameMap = nameMap + p._1 ~> p._2
    }
    scope = scope(localThisOpt = Some(info.tpe), nameMap = nameMap)
    val newStmts = checkStmts(F, ISZ(scope), None(), info.ast.stmts, reporter)
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
    var nameMap = HashMap.empty[String, Info]
    for (p <- info.specVars.entries) {
      nameMap = nameMap + p._1 ~> p._2
    }
    scope = scope(localThisOpt = Some(info.tpe), nameMap = nameMap)
    val newStmts = checkStmts(F, ISZ(scope), None(), info.ast.stmts, reporter)
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
    var nameMap = HashMap.empty[String, Info]
    for (stmt <- info.ast.stmts) {
      stmt match {
        case stmt: AST.Stmt.Var => nameMap = nameMap + stmt.id.value ~> typeHierarchy.nameMap.get(info.name :+ stmt.id.value).get
        case stmt: AST.Stmt.SpecVar => nameMap = nameMap + stmt.id.value ~> typeHierarchy.nameMap.get(info.name :+ stmt.id.value).get
        case _ =>
      }
    }
    scope = scope(localThisOpt = Some(AST.Typed.Object(info.owner, info.ast.id.value)), nameMap = nameMap)
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
