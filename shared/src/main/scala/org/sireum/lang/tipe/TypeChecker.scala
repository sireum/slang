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

package org.sireum.lang.tipe

import org.sireum._
import org.sireum.lang.symbol.{GlobalDeclarationResolver, Resolver}
import org.sireum.lang.{ast => AST}
import org.sireum.lang.symbol.Resolver._
import org.sireum.lang.util._

object TypeChecker {

  @datatype class TypedEq(t: AST.Typed) {
    @pure override def hash: Z = {
      return t.hash
    }

    @pure def isEqual(other: TypedEq): B = {
      return TypeChecker.isEqType(t, other.t)
    }
  }

  @enum object BasicKind {
    'B
    'C
    'Z
    'Range
    'Bits
    'F32
    'F64
    'R
  }

  @enum object BuiltInKind {
    'Print
    'Assertume
    'Halt
  }

  val typeCheckerKind: String = "Type Checker"
  val errType: AST.Typed = AST.Typed.Name(ISZ(), ISZ())
  val builtInTypes: HashSet[String] = HashSet.empty[String].addAll(ISZ(
    "Unit", "Nothing"
  ))
  val builtInMethods: HashSet[String] = HashSet.empty[String].addAll(ISZ(
    "assert", "assume", "println", "print", "eprintln", "eprint", "halt"
  ))
  val assertResOpt: Option[AST.ResolvedInfo] = Some(AST.ResolvedInfo.BuiltIn("assert"))
  val assertume1TypedOpt: Option[AST.Typed] = Some(AST.Typed.Fun(F, F, ISZ(AST.Typed.b), AST.Typed.unit))
  val assertume2TypedOpt: Option[AST.Typed] = Some(AST.Typed.Fun(F, F, ISZ(AST.Typed.b, AST.Typed.string), AST.Typed.unit))
  val assumeResOpt: Option[AST.ResolvedInfo] = Some(AST.ResolvedInfo.BuiltIn("assume"))
  val printlnResOpt: Option[AST.ResolvedInfo] = Some(AST.ResolvedInfo.BuiltIn("println"))
  val printResOpt: Option[AST.ResolvedInfo] = Some(AST.ResolvedInfo.BuiltIn("print"))
  val eprintlnResOpt: Option[AST.ResolvedInfo] = Some(AST.ResolvedInfo.BuiltIn("eprintln"))
  val eprintResOpt: Option[AST.ResolvedInfo] = Some(AST.ResolvedInfo.BuiltIn("eprint"))
  val haltResOpt: Option[AST.ResolvedInfo] = Some(AST.ResolvedInfo.BuiltIn("halt"))
  val haltTypedOpt: Option[AST.Typed] = Some(AST.Typed.Fun(F, F, ISZ(AST.Typed.string), AST.Typed.nothing))

  var _typeHierarchyReporter: Option[(TypeHierarchy, AccumulatingReporter)] = None()

  def typeHierarchyReporter: (TypeHierarchy, AccumulatingReporter) = {
    if (_typeHierarchyReporter.nonEmpty) {
      return _typeHierarchyReporter.get
    }
    val (initNameMap, initTypeMap) = Resolver.addBuiltIns(HashMap.empty, HashMap.empty)
    val (reporter, nameMap, typeMap) = Resolver.parseProgramAndGloballyResolve(
      Library.files, initNameMap, initTypeMap)
    val th = TypeHierarchy.build(TypeHierarchy(nameMap, typeMap, Poset.empty, HashMap.empty), reporter)
    val thOutlined = TypeOutliner.checkOutline(th, reporter)
    val r = (thOutlined, reporter)
    _typeHierarchyReporter = Some(r)
    return r
  }

  @pure def substFunType(m: HashMap[String, AST.Typed], t: AST.Typed.Fun): AST.Typed.Fun = {
    return t(args = t.args.map(ta => substType(m, ta)), ret = substType(m, t.ret))
  }

  @pure def substType(m: HashMap[String, AST.Typed], t: AST.Typed): AST.Typed = {
    if (m.isEmpty) {
      return t
    }
    t match {
      case t: AST.Typed.Name =>
        if (t.ids.size == 1 && t.args.isEmpty) {
          m.get(t.ids(0)) match {
            case Some(t2) => return t2
            case _ =>
          }
        }
        return t(args = t.args.map(ta => substType(m, ta)))
      case t: AST.Typed.Tuple => return t(args = t.args.map(ta => substType(m, ta)))
      case t: AST.Typed.Fun => return t(args = t.args.map(ta => substType(m, ta)), ret = substType(m, t.ret))
      case t: AST.Typed.Enum => return t
      case t: AST.Typed.Method => return t
      case t: AST.Typed.Object => return t
      case t: AST.Typed.Package => return t
    }
  }

  def buildTypeSubstMap(name: QName,
                        posOpt: Option[AST.PosInfo],
                        typeParams: ISZ[AST.TypeParam],
                        args: ISZ[AST.Typed],
                        reporter: Reporter): Option[HashMap[String, AST.Typed]] = {
    if (typeParams.size != args.size) {
      reporter.error(posOpt, typeCheckerKind, st"Type ${(name, ".")} requires ${typeParams.size} type arguments, but only ${args.size} is supplied.".render)
      return None()
    }
    var substMap = HashMap.empty[String, AST.Typed]
    for (i <- z"0" until args.size) {
      substMap = substMap.put(typeParams(i).id.value, args(i))
    }
    return Some(substMap)
  }

  def buildMethodSubstMap(m: AST.Typed.Method,
                          posOpt: Option[AST.PosInfo],
                          args: ISZ[AST.Typed],
                          reporter: Reporter): Option[HashMap[String, AST.Typed]] = {
    if (m.typeParams.size != args.size) {
      reporter.error(posOpt, typeCheckerKind,
        st"$m requires ${m.typeParams.size} type arguments, but only ${args.size} is supplied.".render)
      return None()
    }
    var substMap = HashMap.empty[String, AST.Typed]
    for (i <- z"0" until args.size) {
      substMap = substMap.put(m.typeParams(i), args(i))
    }
    return Some(substMap)
  }

  @pure def isUnitType(t: AST.Typed): B = {
    t match {
      case t: AST.Typed.Tuple if t.args.isEmpty => return T
      case _ => return isEqType(t, AST.Typed.unit)
    }
  }

  @pure def isEqType(t1: AST.Typed, t2: AST.Typed): B = {
    (t1, t2) match {
      case (t1: AST.Typed.Name, t2: AST.Typed.Name) =>
        if (t1.args.size != t2.args.size) {
          return F
        }
        if (t1.ids != t2.ids) {
          return F
        }
        for (i <- z"0" until t1.args.size) {
          if (!isEqType(t1.args(i), t2.args(i))) {
            return F
          }
        }
        return T
      case (t1: AST.Typed.Tuple, t2: AST.Typed.Tuple) =>
        if (t1.args.size != t2.args.size) {
          return F
        }
        for (i <- z"0" until t1.args.size) {
          if (!isEqType(t1.args(i), t2.args(i))) {
            return F
          }
        }
        return T
      case (t1: AST.Typed.Fun, t2: AST.Typed.Fun) =>
        if (t1.isPure != t2.isPure || t1.isByName != t2.isByName) {
          return F
        }
        if (t1.args.size != t2.args.size) {
          return F
        }
        if (!isEqType(t1.ret, t2.ret)) {
          return F
        }
        for (i <- z"0" until t1.args.size) {
          if (!isEqType(t1.args(i), t2.args(i))) {
            return F
          }
        }
        return T
      case (t1: AST.Typed.Fun, _) if t1.isByName =>
        return isEqType(t1.ret, t2)
      case (_, t2: AST.Typed.Fun) if t2.isByName =>
        return isEqType(t1, t2.ret)
      case (t1: AST.Typed.Package, t2: AST.Typed.Package) =>
        return t1.name == t2.name
      case (t1: AST.Typed.Object, t2: AST.Typed.Object) =>
        return t1.name == t2.name
      case (t1: AST.Typed.Enum, t2: AST.Typed.Enum) =>
        return t1.name == t2.name
      case _ =>
        if (isUnitType(t1) && isUnitType(t2)) {
          return T
        }
        return F
    }
  }

  @pure def deBruijn(typeParams: HashSet[String], t: AST.Typed): AST.Typed = {
    var map = HashMap.empty[String, Z]

    def db(t: AST.Typed): AST.Typed = {
      t match {
        case t: AST.Typed.Name =>
          if (t.args.nonEmpty) {
            var args = ISZ[AST.Typed]()
            for (arg <- t.args) {
              val ta = db(arg)
              args = args :+ ta
            }
            return t(args = args)
          } else if (t.ids.size == 1 && typeParams.contains(t.ids(0).value)) {
            val k = t.ids(0).value
            val i: Z = map.get(k) match {
              case Some(n) => n
              case _ =>
                val n = map.size
                map = map.put(k, n)
                n
            }
            return t(ids = ISZ(s"$$$i"))
          } else {
            return t
          }
        case t: AST.Typed.Tuple =>
          var args = ISZ[AST.Typed]()
          for (arg <- t.args) {
            val ta = db(arg)
            args = args :+ ta
          }
          return t(args = args)
        case t: AST.Typed.Fun =>
          var args = ISZ[AST.Typed]()
          for (arg <- t.args) {
            val ta = db(arg)
            args = args :+ ta
          }
          val tr = db(t.ret)
          return t(args = args, ret = tr)
        case t: AST.Typed.Enum => return t
        case t: AST.Typed.Method => return t
        case t: AST.Typed.Object => return t
        case t: AST.Typed.Package => return t
      }
    }

    val r = db(t)
    return r
  }

  @pure def extractMethodType(m: AST.MethodSig): AST.Typed.Fun = {
    var pts = ISZ[AST.Typed]()
    for (p <- m.params) {
      pts = pts :+ p.tipe.typedOpt.get
    }
    val t = AST.Typed.Fun(m.isPure, !m.hasParams, pts, m.returnType.typedOpt.get)
    t match {
      case t: AST.Typed.Fun => return t
      case _ => halt("Infeasible")
    }
  }

  def checkWorksheet(program: AST.TopUnit.Program,
                     reporter: Reporter): AST.TopUnit.Program = {
    val emptyAttr = AST.Attr(None())
    val p = program(packageName = AST.Name(ISZ(AST.Id("worksheet", emptyAttr)), emptyAttr))
    val (th, rep) = typeHierarchyReporter
    if (rep.hasIssue) {
      reporter.reports(rep.messages)
      return p
    }

    val gdr = GlobalDeclarationResolver(th.nameMap, th.typeMap, reporter)
    gdr.resolveProgram(p(body = p.body(stmts = p.body.stmts.withFilter(p => p match {
      case _: AST.Stmt.Var => F
      case _: AST.Stmt.SpecVar => F
      case _ => T
    }))))
    reporter.reports(gdr.reporter.messages)
    if (reporter.hasIssue) {
      return p
    }

    val th2 = TypeHierarchy.build(th(nameMap = gdr.globalNameMap, typeMap = gdr.globalTypeMap), reporter)
    if (reporter.hasIssue) {
      return p
    }

    val th3 = TypeOutliner.checkOutline(th2, reporter)
    if (reporter.hasIssue) {
      return p
    }

    val tc = TypeChecker(th3, ISZ(), F)
    val scope = Scope.Local(HashMap.empty, HashMap.empty, None(), Some(Scope.Global(ISZ(), ISZ(), ISZ())))
    val (_, newStmts) = tc.checkStmts(scope, p.body.stmts, reporter)
    return p(body = p.body(newStmts))
  }
}

import TypeChecker._

@datatype class TypeChecker(typeHierarchy: TypeHierarchy,
                            context: QName,
                            inSpec: B) {

  def basicKind(scope: Scope, tpe: AST.Typed,
                posOpt: Option[AST.PosInfo],
                reporter: Reporter): Option[BasicKind.Type] = {
    tpe match {
      case tpe: AST.Typed.Name =>
        if (tpe.args.nonEmpty) {
          return None()
        }
        if (tpe.ids.size == 3) {
          if (tpe.ids == AST.Typed.b.ids) {
            return Some(BasicKind.B)
          }
          if (tpe.ids == AST.Typed.z.ids) {
            return Some(BasicKind.Z)
          }
          if (tpe.ids == AST.Typed.c.ids) {
            return Some(BasicKind.C)
          }
          if (tpe.ids == AST.Typed.f32.ids) {
            return Some(BasicKind.F32)
          }
          if (tpe.ids == AST.Typed.f64.ids) {
            return Some(BasicKind.F64)
          }
          if (tpe.ids == AST.Typed.r.ids) {
            return Some(BasicKind.R)
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

  def checkExp(expectedOpt: Option[AST.Typed],
               scope: Scope,
               exp: AST.Exp,
               reporter: Reporter): (AST.Exp, Option[AST.Typed]) = {

    @pure def checkExpected(t: AST.Typed): Option[AST.Typed] = {
      expectedOpt match {
        case Some(expected) =>
          if (typeHierarchy.isSubType(t, expected)) {
            return Some(t)
          } else {
            reporter.error(exp.posOpt, typeCheckerKind, s"Expected type $expected, but found $t")
            return None()
          }
        case _ =>
          return Some(t)
      }
    }

    def checkAssertume(resOpt: Option[AST.ResolvedInfo], assertumeExp: AST.Exp.Invoke,
                       cond: AST.Exp, msgOpt: Option[AST.Exp]): (AST.Exp, Option[AST.Typed]) = {
      val (newCondExp, _) = checkExp(AST.Typed.bOpt, scope, cond, reporter)

      msgOpt match {
        case Some(msg) =>
          val (newMsg, _) = checkExp(AST.Typed.stringOpt, scope, msg, reporter)
          val attr = assertumeExp.attr(typedOpt = assertume2TypedOpt, resOpt = resOpt)
          return (assertumeExp(args = ISZ(newCondExp, newMsg), attr = attr), AST.Typed.unitOpt)
        case _ =>
          val attr = assertumeExp.attr(typedOpt = assertume1TypedOpt, resOpt = resOpt)
          return (assertumeExp(args = ISZ(newCondExp), attr = attr), AST.Typed.unitOpt)
      }
    }

    def checkPrint(resOpt: Option[AST.ResolvedInfo], printExp: AST.Exp.Invoke,
                   args: ISZ[AST.Exp]): (AST.Exp, Option[AST.Typed]) = {
      var newArgs = ISZ[AST.Exp]()
      var argTypes = ISZ[AST.Typed]()
      for (arg <- args) {
        val (newArg, argTypeOpt) = checkExp(None(), scope, arg, reporter)
        argTypeOpt match {
          case Some(argType) => argTypes = argTypes :+ argType
          case _ =>
        }
        newArgs = newArgs :+ newArg
      }
      val attr = printExp.attr(typedOpt = Some(AST.Typed.Fun(F, F, argTypes, AST.Typed.unit)), resOpt = resOpt)
      return (printExp(args = newArgs, attr = attr), AST.Typed.unitOpt)
    }

    def checkHalt(haltExp: AST.Exp.Invoke, args: ISZ[AST.Exp]): (AST.Exp, Option[AST.Typed]) = {
      if (args.size != 1) {
        reporter.error(haltExp.posOpt, typeCheckerKind,
          s"Expecting one argument, but found ${args.size}.")
        return (haltExp(attr = haltExp.attr(resOpt = haltResOpt,
          typedOpt = haltTypedOpt)), AST.Typed.nothingOpt)
      }
      val (newArg, _) = checkExp(AST.Typed.stringOpt, scope, args(0), reporter)
      return (haltExp(args = ISZ(newArg), attr = haltExp.
        attr(resOpt = haltResOpt, typedOpt = haltTypedOpt)), AST.Typed.nothingOpt)
    }

    def checkUnary(exp: AST.Exp.Unary): (AST.Exp, Option[AST.Typed]) = {
      val (newExp, expTypeOpt) = checkExp(None(), scope, exp.exp, reporter)
      val newUnaryExp = exp(exp = newExp)
      expTypeOpt match {
        case Some(expType) =>
          val kindOpt = basicKind(scope, expType, exp.posOpt, reporter)
          kindOpt match {
            case Some(kind) =>
              if (exp.op == AST.Exp.UnaryOp.Not && kind != BasicKind.B) {
                reporter.error(exp.posOpt, typeCheckerKind,
                  st"Undefined unary operation ! on '$expType'.".render)
                return (newUnaryExp, None())
              }
              if (exp.op == AST.Exp.UnaryOp.Complement &&
                !(kind == BasicKind.B || kind == BasicKind.Bits)) {
                reporter.error(exp.posOpt, typeCheckerKind,
                  st"Undefined unary operation ~ on 'tpe'.".render)
                return (newUnaryExp, None())
              }
              var r = newUnaryExp
              val tOpt = checkExpected(expType)
              r = r(attr = r.attr(typedOpt = tOpt))
              return (r, tOpt)
            case _ =>
              // TODO: resolve unary_<op> on <expType>
              reporter.error(exp.posOpt, typeCheckerKind,
                st"Undefined unary operation ${AST.Util.unop(exp.op)} on '$expType'.".render)
          }
        case _ =>
      }
      return (newExp, None())
    }

    def checkBinary(exp: AST.Exp.Binary): (AST.Exp, Option[AST.Typed]) = {
      val (left, leftTypeOpt) = checkExp(None(), scope, exp.left, reporter)
      val (right, rightTypeOpt) = checkExp(None(), scope, exp.right, reporter)
      var newBinaryExp = exp(left = left, right = right)

      def noResult: (AST.Exp, Option[AST.Typed]) = {
        return (newBinaryExp, None())
      }

      (leftTypeOpt, rightTypeOpt) match {
        case (Some(leftType), Some(rightType)) =>

          def errIncompat(): Unit = {
            reporter.error(exp.posOpt, typeCheckerKind,
              st"Incompatible types for binary operation '$leftType' ${AST.Util.binop(exp.op)} '$rightType'.".render)
          }

          def errUndef(): Unit = {
            reporter.error(exp.posOpt, typeCheckerKind,
              st"Undefined binary operation ${AST.Util.binop(exp.op)} on '$leftType'".render)
          }

          if (exp.op == AST.Exp.BinaryOp.Eq || exp.op == AST.Exp.BinaryOp.Eq) {
            val isCompat = typeHierarchy.isCompatible(leftType, rightType)
            if (isCompat) {
              newBinaryExp = newBinaryExp(attr = newBinaryExp.attr(typedOpt = AST.Typed.bOpt))
              return (newBinaryExp, AST.Typed.bOpt)
            } else {
              errIncompat()
              return noResult
            }
          } else if (exp.op == AST.Exp.BinaryOp.MapsTo) {
            val tOpt: Option[AST.Typed] = Some(AST.Typed.Tuple(ISZ(leftType, rightType)))
            return (newBinaryExp(attr = newBinaryExp.attr(typedOpt = tOpt)), tOpt)
          }

          val lOpt = basicKind(scope, leftType, exp.left.posOpt, reporter)
          val rOpt = basicKind(scope, rightType, exp.right.posOpt, reporter)
          (lOpt, rOpt) match {
            case (Some(leftKind), Some(rightKind)) =>
              if (leftKind != rightKind) {
                errIncompat()
                return noResult
              }
              if ((leftKind == BasicKind.B && AST.Util.isBoolBinop(exp.op)) ||
                (AST.Util.isArithBinop(exp.op) && leftKind != BasicKind.B) ||
                (AST.Util.isBitsBinop(exp.op) && leftKind == BasicKind.Bits)) {
                val tOpt = checkExpected(leftType)
                newBinaryExp = newBinaryExp(attr = newBinaryExp.attr(typedOpt = tOpt))
                return (newBinaryExp, tOpt)
              } else if (AST.Util.isCompareBinop(exp.op) && leftKind != BasicKind.B) {
                val tOpt: Option[AST.Typed] = checkExpected(AST.Typed.b)
                newBinaryExp = newBinaryExp(attr = newBinaryExp.attr(typedOpt = tOpt))
                return (newBinaryExp, tOpt)
              } else {
                errUndef()
                return noResult
              }
            case _ =>
              halt("Unimplemented") // TODO: find <op> binary methods
          }
        case _ => return noResult
      }
    }

    def checkTuple(exp: AST.Exp.Tuple): (AST.Exp, Option[AST.Typed]) = {
      var args = ISZ[AST.Exp]()
      var argTypes = ISZ[AST.Typed]()
      var ok = T
      for (arg <- args) {
        val (newArg, argTypeOpt) = checkExp(None(), scope, arg, reporter)
        args = args :+ newArg
        argTypeOpt match {
          case Some(argType) => argTypes = argTypes :+ argType
          case _ => ok = F
        }
      }
      var r = exp
      r = r(args = args)
      if (!ok) {
        return (r, None())
      }
      val t: AST.Typed = AST.Typed.Tuple(argTypes)
      val tOpt = checkExpected(t)
      r = r(attr = r.attr(typedOpt = tOpt))
      return (r, tOpt)
    }

    @pure def checkInfo(info: Info): (Option[AST.Typed], Option[AST.ResolvedInfo]) = {
      info match {
        case info: Info.LocalVar => return (info.typedOpt, info.resOpt)
        case info: Info.Package => return (info.typedOpt, info.resOpt)
        case info: Info.Object => return (info.typedOpt, info.resOpt)
        case info: Info.Enum => return (info.typedOpt, info.resOpt)
        case info: Info.Method =>
          return if (inSpec && info.ast.purity == AST.Purity.Impure) (None(), None())
          else (info.typedOpt, info.resOpt)
        case info: Info.ExtMethod =>
          return if (inSpec && !info.ast.isPure) (None(), None())
          else (info.typedOpt, info.resOpt)
        case info: Info.Var => return (info.typedOpt, info.resOpt)
        case info: Info.QuantVar =>
          return if (inSpec) (info.typedOpt, info.resOpt) else (None(), None())
        case info: Info.SpecMethod => return if (!inSpec) (None(), None()) else (info.typedOpt, info.resOpt)
        case info: Info.SpecVar => return if (!inSpec) (None(), None()) else (info.typedOpt, info.resOpt)
      }
    }

    @pure def checkInfoOpt(infoOpt: Option[Info]): (Option[AST.Typed], Option[AST.ResolvedInfo]) = {
      infoOpt match {
        case Some(info) => return checkInfo(info)
        case _ => return (None(), None())
      }
    }

    def checkStringInterpolate(exp: AST.Exp.StringInterpolate): (AST.Exp, Option[AST.Typed]) = {
      var args = ISZ[AST.Exp]()
      var argTypes = ISZ[AST.Typed]()
      var ok = T
      for (arg <- exp.args) {
        val (newArg, argTypeOpt) = checkExp(None(), scope, arg, reporter)
        args = args :+ newArg
        argTypeOpt match {
          case Some(argType) => argTypes = argTypes :+ argType
          case _ => ok = F
        }
      }
      exp.prefix.native match {
        case "s" => return (exp(args = args), checkExpected(AST.Typed.string))
        case "st" => return (exp(args = args), checkExpected(AST.Typed.st))
        case _ => halt("Unimplemented") // TODO
      }
    }

    def checkId(id: AST.Id): (Option[AST.Typed], Option[AST.ResolvedInfo]) = {
      scope.resolveName(typeHierarchy.nameMap, ISZ(id.value)) match {
        case Some(info) => return checkInfo(info)
        case _ => halt("Unimplemented") // TODO: check this.<exp>
      }
    }

    def checkEtaH(t: AST.Typed, ref: AST.Exp.Ref, typeArgs: ISZ[AST.Typed],
                  etaParentOpt: Option[AST.Exp.Eta]): (AST.Exp, Option[AST.Typed]) = {

      val exp = ref.asExp

      def noResult: (AST.Exp, Option[AST.Typed]) = {
        return (exp, None())
      }

      t match {
        case t: AST.Typed.Method =>
          val substMap: HashMap[String, AST.Typed] =
            if (typeArgs.isEmpty && t.typeParams.nonEmpty && t.tpe.isByName) {
              expectedOpt match {
                case Some(expected) =>
                  val smOpt = unify(exp.posOpt, T, expected, t.tpe.ret, reporter)
                  smOpt match {
                    case Some(sm) => sm
                    case _ => return noResult
                  }
                case _ =>
                  reporter.error(exp.posOpt, typeCheckerKind,
                    s"Explicit type arguments are required.")
                  return noResult
              }
            } else {
              if (t.typeParams.size != typeArgs.size) {
                reporter.error(exp.posOpt, typeCheckerKind,
                  s"Expecting ${t.typeParams.size} type arguments, but found ${typeArgs.size}.")
                return noResult
              }
              var sm = HashMap.emptyInit[String, AST.Typed](typeArgs.size)
              val size = typeArgs.size
              var i = 0
              while (i < size) {
                sm = sm.put(t.typeParams(i), typeArgs(i))
                i = i + 1
              }
              sm
            }
          if (ref.targs.nonEmpty) {
            etaParentOpt match {
              case Some(etaParent) =>
                val tpe: AST.Typed = if (t.tpe.isByName) t.tpe(isByName = F) else t.tpe
                val tOpt = Some(substType(substMap, tpe))
                return (etaParent(ref = ref, attr = etaParent.attr(typedOpt = tOpt)), tOpt)
              case _ if t.tpe.isByName => return (exp, Some(substType(substMap, t.tpe.ret)))
              case _ =>
                reporter.error(exp.posOpt, typeCheckerKind,
                  "Method access has to be explicitly eta-expanded to become a function using '_'.")
                return noResult
            }
          } else {
            etaParentOpt match {
              case Some(etaParent) =>
                val tOpt: Option[AST.Typed] = Some(if (t.tpe.isByName) t.tpe(isByName = F) else t.tpe)
                return (etaParent(ref = ref, attr = etaParent.attr(typedOpt = tOpt)), tOpt)
              case _ if t.tpe.isByName => return (exp, Some(t.tpe.ret))
              case _ =>
                reporter.error(exp.posOpt, typeCheckerKind,
                  "Method access has to be explicitly eta-expanded to become a function using '_'.")
                return noResult
            }
          }
        case _ =>
          if (ref.targs.nonEmpty) {
            reporter.error(exp.posOpt, typeCheckerKind, s"Cannot supply type arguments on '$t'.")
            return noResult
          } else {
            etaParentOpt match {
              case Some(_) =>
                reporter.error(exp.posOpt, typeCheckerKind, s"Cannot eta-expand non-method '$t'.")
                return noResult
              case _ => return (exp, Some(t))
            }
          }
      }
    }

    def checkIdent(exp: AST.Exp.Ident,
                   etaParentOpt: Option[AST.Exp.Eta]): (AST.Exp, Option[AST.Typed]) = {
      val (typedOpt, resOpt) = checkId(exp.id)
      val newExp = exp(attr = exp.attr(typedOpt = typedOpt, resOpt = resOpt))
      if (typedOpt.isEmpty) {
        return (newExp, typedOpt)
      }
      val r = checkEtaH(typedOpt.get, newExp, ISZ(), etaParentOpt)
      return r
    }

    def checkSelectH(receiverType: AST.Typed, ident: AST.Id,
                     hasTypeArgs: B): (Option[AST.Typed], Option[AST.ResolvedInfo]) = {
      val id = ident.value

      def errAccess(t: AST.Typed): Unit = {
        reporter.error(ident.attr.posOpt, typeCheckerKind, s"Cannot access '$id' from type '$t'.")
      }

      def errTypeArgs(t: AST.Typed): Unit = {
        reporter.error(ident.attr.posOpt, typeCheckerKind, s"Method '$id' does not accept type arguments.")
      }

      @pure def noResult: (Option[AST.Typed], Option[AST.ResolvedInfo]) = {
        return (None(), None())
      }

      receiverType match {
        case receiverType: AST.Typed.Name =>
          val id = ident.value
          typeHierarchy.typeMap.get(receiverType.ids).get match {
            case info: TypeInfo.Sig =>
              val r = info.typeRes(id, inSpec)
              r._1 match {
                case Some(rt) =>
                  val smOpt = buildTypeSubstMap(info.name, ident.attr.posOpt, info.ast.typeParams, receiverType.args, reporter)
                  smOpt match {
                    case Some(sm) => return (Some(substType(sm, rt)), r._2)
                    case _ => return noResult
                  }
                case _ => errAccess(receiverType); return noResult
              }
            case info: TypeInfo.AbstractDatatype =>
              val r = info.typeRes(id, inSpec)
              r._1 match {
                case Some(rt) =>
                  val smOpt = buildTypeSubstMap(info.name, ident.attr.posOpt, info.ast.typeParams, receiverType.args, reporter)
                  smOpt match {
                    case Some(sm) => return (Some(substType(sm, rt)), r._2)
                    case _ => return noResult
                  }
                case _ => errAccess(receiverType); return noResult
              }
            case _ => halt("Unexpected situation while type checking method access.")
          }
        case receiverType: AST.Typed.Tuple =>
          if (id.size == 0 || ops.StringOps(id).first == '_') {
            errAccess(receiverType)
            return noResult
          }
          Z(ops.StringOps(id).substring(1, id.size)) match {
            case Some(n) =>
              val size = receiverType.args.size
              if (!(0 <= n && n < size)) {
                errAccess(receiverType)
                return noResult
              }
              if (hasTypeArgs) {
                errTypeArgs(receiverType)
                return noResult
              }
              return (Some(receiverType.args(n)), Some(AST.ResolvedInfo.Tuple(size, n)))
            case _ =>
              errAccess(receiverType)
              return noResult
          }
        case receiverType: AST.Typed.Fun =>
          errAccess(receiverType)
          return noResult
        case receiverType: AST.Typed.Package => return checkInfoOpt(typeHierarchy.nameMap.get(receiverType.name :+ id))
        case receiverType: AST.Typed.Object => return checkInfoOpt(typeHierarchy.nameMap.get(receiverType.name :+ id))
        case receiverType: AST.Typed.Enum =>
          typeHierarchy.nameMap.get(receiverType.name) match {
            case Some(info: Info.Enum) =>
              info.elements.get(id) match {
                case Some(resOpt) =>
                  if (hasTypeArgs) {
                    errTypeArgs(receiverType)
                    return noResult
                  }
                  return (info.elementTypedOpt, resOpt)
                case _ =>
                  errAccess(receiverType)
                  return noResult
              }
            case _ => halt("Unexpected situation while type checking enum access.")
          }
        case receiverType: AST.Typed.Method =>
          errAccess(receiverType)
          return noResult
      }
    }

    def checkSelect(exp: AST.Exp.Select,
                    etaParentOpt: Option[AST.Exp.Eta]): (AST.Exp, Option[AST.Typed]) = {
      var typeArgs = ISZ[AST.Typed]()
      var newTargs = ISZ[AST.Type]()
      for (targ <- exp.targs) {
        val tOpt = typeHierarchy.typed(scope, targ, reporter)
        tOpt match {
          case Some(t) =>
            typeArgs = typeArgs :+ t
            newTargs = newTargs :+ targ.typed(t)
          case _ =>
            return (exp, None())
        }
      }

      def helper: (AST.Exp.Select, Option[AST.Typed]) = {
        exp.receiverOpt match {
          case Some(receiver) =>
            val (newReceiver, receiverTypeOpt) = checkExp(None(), scope, receiver, reporter)
            receiverTypeOpt match {
              case Some(receiverType) =>
                val (typedOpt, resOpt) = checkSelectH(receiverType, exp.id, exp.targs.nonEmpty)
                val newExp = exp(targs = newTargs, receiverOpt = Some(newReceiver), attr = exp.attr(typedOpt = typedOpt, resOpt = resOpt))
                return (newExp, typedOpt)
              case _ => return (exp(targs = newTargs, receiverOpt = Some(newReceiver)), None[AST.Typed]())
            }
          case _ =>
            val (typedOpt, resOpt) = checkId(exp.id)
            val newExp = exp(targs = newTargs, attr = exp.attr(typedOpt = typedOpt, resOpt = resOpt))
            return (newExp, typedOpt)
        }
      }

      val (newExp: AST.Exp.Select, typedOpt: Option[AST.Typed]) = helper
      if (typedOpt.isEmpty) {
        return (newExp, typedOpt)
      }
      val r = checkEtaH(typedOpt.get, newExp, typeArgs, etaParentOpt)
      return r
    }

    def checkInvoke(exp: AST.Exp.Invoke): (AST.Exp, Option[AST.Typed]) = {
      var typeArgs = ISZ[AST.Typed]()
      var newTargs = ISZ[AST.Type]()
      for (ta <- exp.targs) {
        val tOpt = typeHierarchy.typed(scope, ta, reporter)
        tOpt match {
          case Some(t) =>
            typeArgs = typeArgs :+ t
            newTargs = newTargs :+ ta.typed(t)
          case _ => return (exp, None())
        }
      }

      def checkInvokeH(tOpt: Option[AST.Typed], resOpt: Option[AST.ResolvedInfo],
                       receiverOpt: Option[AST.Exp]): (AST.Exp, Option[AST.Typed]) = {
        @pure def partResult: (AST.Exp, Option[AST.Typed]) = {
          return (exp(targs = newTargs, receiverOpt = receiverOpt), None())
        }

        val t: AST.Typed = tOpt match {
          case Some(tpe) =>
            tpe match {
              case tpe: AST.Typed.Object =>
                typeHierarchy.typeMap.get(tpe.name) match {
                  case Some(info: TypeInfo.AbstractDatatype) if !info.ast.isRoot =>
                    info.constructorTypeOpt match {
                      case Some(constructorType) => constructorType
                      case _ =>
                        reporter.error(exp.id.attr.posOpt, typeCheckerKind,
                          st"Cannot create an object of type ${(tpe.name, ".")}.".render)
                        return partResult
                    }
                  case _ => return partResult
                }
              case tpe: AST.Typed.Name => halt("Unimplemented") // TODO: IS.apply, MS.apply, Z, subZ
              case _ => tpe
            }
          case _ => return partResult
        }

        t match {
          case m: AST.Typed.Method =>
            if (m.tpe.args.size != exp.args.size) {
              reporter.error(exp.id.attr.posOpt, typeCheckerKind,
                s"$m is expecting ${m.tpe.args.size} arguments, but found ${exp.args.size}.")
              return partResult
            }

            def checkH(sm: HashMap[String, AST.Typed]): (AST.Exp, Option[AST.Typed]) = {
              val funType = substFunType(sm, m.tpe)
              var i = 0
              val size = exp.args.size
              var args = Map.empty[String, AST.Exp]
              while (i < size) {
                val (newArg, _) = checkExp(Some(funType.args(i)), scope, exp.args(i), reporter)
                args = args.put(m.paramNames(i), newArg)
                i = i + 1
              }
              return (exp(targs = newTargs, args = args.values, receiverOpt = receiverOpt,
                attr = exp.attr(resOpt = resOpt, typedOpt = Some(funType))), Some(funType.ret))
            }

            if (typeArgs.isEmpty && m.typeParams.nonEmpty) {
              expectedOpt match {
                case Some(expected) =>
                  val smOpt = unify(exp.id.attr.posOpt, T, expected, m.tpe.ret, reporter)
                  smOpt match {
                    case Some(sm) => val r = checkH(sm); return r
                    case _ => return partResult
                  }
                case _ =>
                  var newArgs = ISZ[AST.Exp]()
                  var argTypes = ISZ[AST.Typed]()
                  for (e <- exp.args) {
                    val (newArg, argTypeOpt) = checkExp(None(), scope, e, reporter)
                    argTypeOpt match {
                      case Some(argType) =>
                        newArgs = newArgs :+ newArg
                        argTypes = argTypes :+ argType
                      case _ =>
                    }
                  }
                  val smOpt = unifies(exp.id.attr.posOpt, T, argTypes, m.tpe.args, reporter)
                  smOpt match {
                    case Some(sm) =>
                      val funType = substFunType(sm, m.tpe)
                      return (exp(targs = newTargs, args = newArgs, receiverOpt = receiverOpt,
                        attr = exp.attr(resOpt = resOpt, typedOpt = Some(funType))), Some(funType.ret))
                    case _ =>
                      return (exp(targs = newTargs, args = newArgs, receiverOpt = receiverOpt,
                        attr = exp.attr(resOpt = resOpt)), None())
                  }
              }
            } else {
              val smOpt = buildMethodSubstMap(m, exp.id.attr.posOpt, typeArgs, reporter)
              smOpt match {
                case Some(sm) => val r = checkH(sm); return r
                case _ => return partResult
              }
            }
          case t: AST.Typed.Fun =>
            if (exp.targs.nonEmpty) {
              reporter.error(exp.id.attr.posOpt, typeCheckerKind,
                s"Cannot supply type arguments when applying a function.")
              return partResult
            }
            val size = exp.args.size
            if (t.args.size != size) {
              reporter.error(exp.id.attr.posOpt, typeCheckerKind,
                s"Function '$t' is expecting $size arguments, but found $size.")
              return partResult
            }
            var i = 0
            var newArgs = ISZ[AST.Exp]()
            while (i < size) {
              val (newArg, _) = checkExp(Some(t.args(i)), scope, exp.args(i), reporter)
              newArgs = newArgs :+ newArg
              i = i + 1
            }
            return (exp(args = newArgs, receiverOpt = receiverOpt,
              attr = exp.attr(resOpt = resOpt, typedOpt = Some(t))), Some(t.ret))
          case _ =>
            reporter.error(exp.id.attr.posOpt, typeCheckerKind, s"Cannot invoke on '$t'.")
            return partResult
        }
      }

      exp.receiverOpt match {
        case Some(receiver) =>
          val (newReceiver, receiverTypeOpt) = checkExp(None(), scope, receiver, reporter)
          val receiverType: AST.Typed = receiverTypeOpt match {
            case Some(t) => t
            case _ => return (exp(targs = newTargs, receiverOpt = Some(newReceiver)), None())
          }
          val (typedOpt, resOpt) = checkSelectH(receiverType, exp.id, exp.targs.nonEmpty)
          val r = checkInvokeH(typedOpt, resOpt, Some(newReceiver))
          return r
        case _ =>
          val (typedOpt, resOpt) = checkId(exp.id)
          val r = checkInvokeH(typedOpt, resOpt, None())
          return r
      }
    }

    exp match {

      case exp: AST.Exp.Binary => val r = checkBinary(exp); return r

      case exp: AST.Exp.Eta =>
        exp.ref match {
          case ref: AST.Exp.Ident => val r = checkIdent(ref, Some(exp)); return r
          case ref: AST.Exp.Select => val r = checkSelect(ref, Some(exp)); return r
        }

      case exp: AST.Exp.ForYield => halt("Unimplemented") // TODO

      case exp: AST.Exp.Fun => halt("Unimplemented") // TODO

      case exp: AST.Exp.Ident => val r = checkIdent(exp, None()); return r

      case exp: AST.Exp.If => halt("Unimplemented") // TODO

      case exp: AST.Exp.Invoke =>
        exp match {
          case exp@AST.Exp.Invoke(None(), AST.Id(name), targs, args) if targs.isEmpty && builtInMethods.contains(name) =>
            val (kind: BuiltInKind.Type, resOpt: Option[AST.ResolvedInfo]) = name.native match {
              case "assert" => (BuiltInKind.Assertume, assertResOpt)
              case "assume" => (BuiltInKind.Assertume, assumeResOpt)
              case "println" => (BuiltInKind.Print, printlnResOpt)
              case "print" => (BuiltInKind.Print, printResOpt)
              case "eprintln" => (BuiltInKind.Print, eprintlnResOpt)
              case "eprint" => (BuiltInKind.Print, eprintResOpt)
              case "halt" => (BuiltInKind.Halt, haltResOpt)
              case _ => halt(s"Unimplemented built-in method $name.")
            }
            kind match {
              case BuiltInKind.Assertume =>
                args.size match {
                  case z"1" => val r = checkAssertume(resOpt, exp, args(0), None()); return r
                  case z"2" => val r = checkAssertume(resOpt, exp, args(0), Some(args(1))); return r
                  case _ =>
                    reporter.error(exp.posOpt, typeCheckerKind,
                      s"Invalid number of arguments (${args.size}) for $name.")
                    return (exp, None())
                }
              case BuiltInKind.Print => val r = checkPrint(resOpt, exp, args); return r
              case BuiltInKind.Halt => val r = checkHalt(exp, args); return r
            }
          case _ =>
            val r = checkInvoke(exp); return r
        }

      case exp: AST.Exp.InvokeNamed => halt("Unimplemented") // TODO

      case exp: AST.Exp.LitB => return (exp, checkExpected(AST.Typed.b))

      case exp: AST.Exp.LitC => return (exp, checkExpected(AST.Typed.c))

      case exp: AST.Exp.LitF32 => return (exp, checkExpected(AST.Typed.f32))

      case exp: AST.Exp.LitF64 => return (exp, checkExpected(AST.Typed.f64))

      case exp: AST.Exp.LitR => return (exp, checkExpected(AST.Typed.r))

      case exp: AST.Exp.LitString => return (exp, checkExpected(AST.Typed.string))

      case exp: AST.Exp.LitZ => return (exp, checkExpected(AST.Typed.z))

      case exp: AST.Exp.Quant => halt("Unimplemented") // TODO

      case exp: AST.Exp.Select => val r = checkSelect(exp, None()); return r

      case exp: AST.Exp.StringInterpolate => val r = checkStringInterpolate(exp); return r

      case exp: AST.Exp.Super => halt("Unimplemented") // TODO

      case exp: AST.Exp.This => halt("Unimplemented") // TODO

      case exp: AST.Exp.Tuple => val r = checkTuple(exp); return r

      case exp: AST.Exp.Unary => val r = checkUnary(exp); return r
    }
  }

  def checkAssignExp(expected: Option[AST.Typed], scope: Scope.Local, aexp: AST.AssignExp,
                     reporter: Reporter): (AST.AssignExp, Option[AST.Typed]) = {

    val (sOpt, newStmt) = checkStmt(scope, aexp.asStmt, reporter)

    val newAexp = newStmt.asAssignExp

    def noResult: (AST.AssignExp, Option[AST.Typed]) = {
      return (newAexp, None())
    }

    def errType(): Unit = {
      reporter.error(aexp.asStmt.posOpt, typeCheckerKind,
        s"Could not find a common type for leaf expressions.")
    }

    if (sOpt.isEmpty) {
      return noResult
    }

    var types = ISZ[AST.Typed]()
    for (expr <- newAexp.exprs) {
      expr.typedOpt match {
        case Some(t) => types = types :+ t
        case _ => return noResult
      }
    }

    typeHierarchy.lub(types) match {
      case r@Some(_) => return (newAexp, r)
      case _ => errType(); return noResult
    }
  }

  def checkStmts(scope: Scope.Local, stmts: ISZ[AST.Stmt],
                 reporter: Reporter): (Option[Scope.Local], ISZ[AST.Stmt]) = {
    var newScope = scope
    var newStmts = ISZ[AST.Stmt]()
    for (i <- z"0" until stmts.size) {
      val (newScope2Opt, newStmt) = checkStmt(newScope, stmts(i), reporter)
      newScope2Opt match {
        case Some(newScope2) =>
          newScope = newScope2
          newStmts = newStmts :+ newStmt
        case _ =>
          for (j <- i until stmts.size) {
            newStmts = newStmts :+ stmts(j)
          }
          return (None(), newStmts)
      }
    }
    return (Some(newScope), newStmts)
  }

  def checkBody(scope: Scope.Local, stmt: AST.Body,
                reporter: Reporter): (Option[Scope.Local], AST.Body) = {
    val (newScopeOpt, newStmts) = checkStmts(scope, stmt.stmts, reporter)
    return (newScopeOpt, stmt(stmts = newStmts))
  }

  def checkStmt(scope: Scope.Local, stmt: AST.Stmt,
                reporter: Reporter): (Option[Scope.Local], AST.Stmt) = {

    def checkImport(stmt: AST.Stmt.Import): (Option[Scope.Local], AST.Stmt) = {
      // TODO: resolve import

      @pure def addImport(s: Scope.Local): Scope.Local = {
        s.outerOpt match {
          case Some(outer: Scope.Local) => s(outerOpt = Some(addImport(outer)))
          case Some(outer: Scope.Global) => s(outerOpt = Some(outer(imports = outer.imports :+ stmt)))
          case _ => halt("Unexpected local scope without an outer scope.")
        }
      }

      return (Some(addImport(scope)), stmt)
    }

    def checkVar(stmt: AST.Stmt.Var): (Option[Scope.Local], AST.Stmt) = {
      val key = stmt.id.value

      def err(): Unit = {
        reporter.error(stmt.id.attr.posOpt, typeCheckerKind, s"Cannot declare '$key' because the name has already been previously declared.")
      }

      val name = context :+ stmt.id.value
      var r = stmt
      scope.resolveName(typeHierarchy.nameMap, ISZ(key)) match {
        case Some(_) =>
          err()
          return (None(), r)
        case _ =>
      }
      val expected: Option[AST.Typed] = stmt.tipeOpt match {
        case Some(tipe) =>
          tipe.typedOpt match {
            case tOpt@Some(_) => tOpt
            case _ =>
              val tOpt = typeHierarchy.typed(scope, tipe, reporter)
              if (tOpt.isEmpty) {
                return (None(), r)
              }
              r = r(tipeOpt = Some(tipe.typed(tOpt.get)))
              tOpt
          }
        case _ => None()
      }
      val (rhs, tOpt) = checkAssignExp(expected, scope, stmt.initOpt.get, reporter)
      tOpt match {
        case Some(t) =>
          val newScope = scope(nameMap = scope.nameMap.put(key,
            Info.LocalVar(name, r.id, tOpt, Some(AST.ResolvedInfo.LocalVar(context, key)))))
          val newStmt = r(initOpt = Some(rhs))
          return (Some(newScope), newStmt)
        case _ =>
          return (None(), r)
      }
    }

    def checkAssign(stmt: AST.Stmt.Assign): AST.Stmt = {
      stmt.lhs match {
        case lhs: AST.Exp.Ident =>
          scope.resolveName(typeHierarchy.nameMap, ISZ(lhs.id.value)) match {
            case Some(info: Info.LocalVar) =>
              val expected = info.typedOpt
              val (rhs, _) = checkAssignExp(expected, scope, stmt.rhs, reporter)
              return stmt(lhs = lhs(attr = lhs.attr(resOpt = info.resOpt, typedOpt = expected)),
                rhs = rhs)
            case Some(_) => halt("Unimplemented.") // TODO
            case _ => halt("Unimplemented.") // TODO
          }
        case _ => halt("Unimplemented.") // TODO
      }
    }

    stmt match {

      case stmt: AST.Stmt.LStmt => halt("Unimplemented.") // TODO

      case stmt: AST.Stmt.AbstractDatatype => halt("Unimplemented.") // TODO

      case stmt: AST.Stmt.Assign => val r = checkAssign(stmt); return (Some(scope), r)

      case stmt: AST.Stmt.Block =>
        val (_, newBody) = checkBody(scope, stmt.body, reporter)
        return (Some(scope), stmt(body = newBody))

      case stmt: AST.Stmt.DoWhile =>
        val (newScopeOpt, newBody) = checkBody(scope, stmt.body, reporter)
        newScopeOpt match {
          case Some(newScope) =>
            val (newCond, _) = checkExp(AST.Typed.bOpt, newScope, stmt.cond, reporter)
            return (Some(scope), stmt(cond = newCond, body = newBody))
          case _ =>
            return (None(), stmt(body = newBody))
        }

      case stmt: AST.Stmt.Enum => halt("Unimplemented.") // TODO

      case stmt: AST.Stmt.Expr =>
        val (newExp, typedOpt) = checkExp(None(), scope, stmt.exp, reporter)
        return (Some(scope), stmt(exp = newExp))

      case stmt: AST.Stmt.ExtMethod => halt("Unimplemented.") // TODO

      case stmt: AST.Stmt.For => halt("Unimplemented.") // TODO

      case stmt: AST.Stmt.If =>
        val (newCond, _) = checkExp(AST.Typed.bOpt, scope, stmt.cond, reporter)
        val (_, tBody) = checkBody(scope, stmt.thenBody, reporter)
        val (_, eBody) = checkBody(scope, stmt.elseBody, reporter)
        return (Some(scope), stmt(cond = newCond, thenBody = tBody, elseBody = eBody))

      case stmt: AST.Stmt.Import => val r = checkImport(stmt); return r

      case stmt: AST.Stmt.Match => halt("Unimplemented.") // TODO

      case stmt: AST.Stmt.Method => halt("Unimplemented.") // TODO

      case stmt: AST.Stmt.Object => halt("Unimplemented.") // TODO

      case stmt: AST.Stmt.Return =>
        (scope.returnOpt, stmt.expOpt) match {
          case (tOpt@Some(_), Some(exp)) =>
            val (newExp, expTypeCond) = checkExp(tOpt, scope, exp, reporter)
            expTypeCond match {
              case Some(t) =>
                if (typeHierarchy.isSubType(t, tOpt.get)) {
                  reporter.error(exp.posOpt, typeCheckerKind,
                    s"Expecting type '${tOpt.get}', but found incompatible type '$t'.")
                }
                return (Some(scope), stmt(expOpt = Some(newExp), attr = stmt.attr(typedOpt = tOpt)))
              case _ => return (None(), stmt(expOpt = Some(newExp)))
            }
          case (Some(t), _) =>
            reporter.error(stmt.posOpt, typeCheckerKind, s"Expecting type '$t', but none found.")
            return (None(), stmt)
          case (_, Some(exp)) =>
            val (newExp, _) = checkExp(None(), scope, exp, reporter)
            reporter.error(exp.posOpt, typeCheckerKind, s"Unexpected return expression.")
            return (None(), stmt(expOpt = Some(newExp)))
          case _ =>
            return (Some(scope), stmt)
        }

      case stmt: AST.Stmt.Sig => halt("Unimplemented.") // TODO

      case stmt: AST.Stmt.SpecMethod => halt("Unimplemented.") // TODO

      case stmt: AST.Stmt.SpecVar => halt("Unimplemented.") // TODO

      case stmt: AST.Stmt.SubZ => halt("Unimplemented.") // TODO

      case stmt: AST.Stmt.TypeAlias => halt("Unimplemented.") // TODO

      case stmt: AST.Stmt.Var => val r = checkVar(stmt); return r

      case stmt: AST.Stmt.VarPattern => halt("Unimplemented.") // TODO

      case stmt: AST.Stmt.While =>
        val (newCond, _) = checkExp(AST.Typed.bOpt, scope, stmt.cond, reporter)
        val (_, newBody) = checkBody(scope, stmt.body, reporter)
        return (Some(scope), stmt(cond = newCond, body = newBody))

    }
  }

  @pure def unifyCombine(r: HashMap[String, AST.Typed],
                         m: HashMap[String, AST.Typed]): Option[HashMap[String, AST.Typed]] = {
    var res = r
    for (e <- m.entries) {
      val (key, value) = e
      res.get(key) match {
        case Some(v) =>
          if (!isEqType(value, v)) {
            return None()
          }
        case _ => res = res.put(key, value)
      }
    }
    return Some(res)
  }

  def unify(posOpt: Option[AST.PosInfo], allowSubType: B, expected: AST.Typed, tpe: AST.Typed,
            reporter: Reporter): Option[HashMap[String, AST.Typed]] = {
    def err(): Unit = {
      reporter.error(posOpt, typeCheckerKind, s"Could not unify type $expected with $tpe.")
    }

    if (isEqType(expected, tpe)) {
      return Some(HashMap.empty)
    }
    (expected, tpe) match {
      case (expected: AST.Typed.Name, tpe: AST.Typed.Name) =>
        val name = tpe.ids
        if (name.size == 1 && !builtInTypes.contains(name(0))) {
          return Some(HashMap.empty[String, AST.Typed].put(name(0), expected))
        }
        val rt: AST.Typed.Name = if (allowSubType) {
          if (typeHierarchy.isSubType(tpe, expected)) {
            err()
            return None()
          }
          typeHierarchy.typeMap.get(expected.ids) match {
            case Some(info: TypeInfo.AbstractDatatype) =>
              AST.Typed.Name(expected.ids, info.ast.typeParams.map(tp => AST.Typed.Name(ISZ(tp.id.value), ISZ())))
            case Some(info: TypeInfo.Sig) =>
              AST.Typed.Name(expected.ids, info.ast.typeParams.map(tp => AST.Typed.Name(ISZ(tp.id.value), ISZ())))
            case _ => halt(s"Unexpected situation when trying to unify $expected and $tpe")
          }
        } else {
          if (tpe.ids != expected.ids || tpe.args.size != expected.args.size) {
            err()
            return None()
          }
          tpe
        }
        val size = rt.args.size
        var i = 0
        var r = HashMap.empty[String, AST.Typed]
        while (i < size) {
          val mOpt = unify(posOpt, F, expected.args(i), rt.args(i), reporter)
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
          val mOpt = unify(posOpt, F, expected.args(i), tpe.args(i), reporter)
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
      case (expected: AST.Typed.Fun, tpe: AST.Typed.Fun) =>
        val size = expected.args.size
        if (size != tpe.args.size) {
          err()
          return None()
        }
        var i = 0
        var r = HashMap.empty[String, AST.Typed]
        while (i < size) {
          val mOpt = unify(posOpt, F, expected.args(i), tpe.args(i), reporter)
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
        val mOpt = unify(posOpt, F, expected.ret, tpe.ret, reporter)
        mOpt match {
          case Some(m) =>
            unifyCombine(r, m) match {
              case Some(c) => r = c
              case _ => err(); return None()
            }
          case _ => return None()
        }
        return Some(r)
      case _ => return None()
    }
  }


  def unifies(posOpt: Option[AST.PosInfo], allowSubType: B, expected: ISZ[AST.Typed],
              tpe: ISZ[AST.Typed], reporter: Reporter): Option[HashMap[String, AST.Typed]] = {
    def err(): Unit = {
      reporter.error(posOpt, typeCheckerKind, s"Could not unify type ${AST.Typed.Tuple(expected)} with ${AST.Typed.Tuple(tpe)}.")
    }

    val size = expected.size
    if (size != tpe.size) {
      return None()
    }
    var r = HashMap.empty[String, AST.Typed]
    var i = 0
    while (i < size) {
      val mOpt = unify(posOpt, allowSubType, expected(i), tpe(i), reporter)
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
}
