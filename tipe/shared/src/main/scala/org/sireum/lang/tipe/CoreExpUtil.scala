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
import org.sireum.lang.{ast => AST}

object CoreExpUtil {
  type FunStack = Stack[(String, AST.Typed)]
  type Local = (ISZ[String], String)
  type LocalMap = HashSMap[Local, AST.CoreExp]
  type LocalPatternSet = HashSSet[Local]
  type PendingApplications = ISZ[(ISZ[String], String, ISZ[AST.CoreExp], AST.CoreExp)]
  type UnificationMap = HashSMap[Local, AST.CoreExp]
  type UnificationErrorMessages = ISZ[String]
  type UnificationResult = Either[UnificationMap, UnificationErrorMessages]

  @record class Substitutor(val map: HashMap[AST.CoreExp, AST.CoreExp.ParamVarRef]) extends AST.MCoreExpTransformer {
    override def transformCoreExp(o: AST.CoreExp): MOption[AST.CoreExp] = {
      map.get(o) match {
        case Some(pvr) => return MSome(pvr)
        case _ => return super.transformCoreExp(o)
      }
    }
  }

  @record class LocalPatternDetector(val localPatterns: LocalPatternSet, var hasLocalPattern: B) extends AST.MCoreExpTransformer {
    override def postCoreExpLocalVarRef(o: AST.CoreExp.LocalVarRef): MOption[AST.CoreExp] = {
      if (localPatterns.contains((o.context, o.id))) {
        hasLocalPattern = T
      }
      return AST.MCoreExpTransformer.PostResultCoreExpLocalVarRef
    }
  }

  @pure def translate(th: TypeHierarchy, exp: AST.Exp, sm: HashMap[String, AST.Typed]): AST.CoreExp = {
    @pure def recBody(body: AST.Body, funStack: FunStack, localMap: LocalMap): AST.CoreExp = {
      val stmts = body.stmts
      var m = localMap
      for (i <- 0 until stmts.size - 2) {
        m = recStmt(stmts(i), funStack, m)._2
      }
      return recAssignExp(stmts(stmts.size - 1).asAssignExp, funStack, m)
    }
    @pure def recStmt(stmt: AST.Stmt, funStack: FunStack, localMap: LocalMap): (Option[AST.CoreExp], LocalMap) = {
      stmt match {
        case stmt: AST.Stmt.Expr => return (Some(rec(stmt.exp, funStack, localMap)), localMap)
        case stmt: AST.Stmt.Var =>
          val res = stmt.attr.resOpt.get.asInstanceOf[AST.ResolvedInfo.LocalVar]
          return (None(), localMap + (res.context, res.id) ~>
            recAssignExp(stmt.initOpt.get, funStack, localMap))
        case stmt: AST.Stmt.Block =>
          return (Some(recBody(stmt.body, funStack, localMap)), localMap)
        case stmt: AST.Stmt.If =>
          val condExp = rec(stmt.cond, funStack, localMap)
          val tExp = recBody(stmt.thenBody, funStack, localMap)
          val fExp = recBody(stmt.elseBody, funStack, localMap)
          return (Some(AST.CoreExp.If(condExp, tExp, fExp, stmt.attr.typedOpt.get)), localMap)
        case stmt: AST.Stmt.VarPattern => halt(s"TODO: $stmt")
        case stmt: AST.Stmt.Match => halt(s"TODO: $stmt")
        case _ => halt(s"Infeasible: $stmt")
      }
    }
    @pure def recAssignExp(ae: AST.AssignExp, funStack: FunStack, localMap: LocalMap): AST.CoreExp = {
      val (Some(r), _) = recStmt(ae.asStmt, funStack, localMap)
      return r
    }
    @pure def rec(e: AST.Exp, funStack: FunStack, localMap: LocalMap): AST.CoreExp = {
      e match {
        case e: AST.Exp.LitB => return AST.CoreExp.LitB(e.value)
        case e: AST.Exp.LitZ => return AST.CoreExp.LitZ(e.value)
        case e: AST.Exp.LitC => return AST.CoreExp.LitC(e.value)
        case e: AST.Exp.LitString => return AST.CoreExp.LitString(e.value)
        case e: AST.Exp.LitR => return AST.CoreExp.LitR(e.value)
        case e: AST.Exp.LitF32 => return AST.CoreExp.LitF32(e.value)
        case e: AST.Exp.LitF64 => return AST.CoreExp.LitF64(e.value)
        case e: AST.Exp.StringInterpolate =>
          e.typedOpt match {
            case Some(t: AST.Typed.Name) =>
              th.typeMap.get(t.ids).get match {
                case ti: lang.symbol.TypeInfo.SubZ =>
                  if (ti.ast.isBitVector) {
                    return AST.CoreExp.LitBits(e.lits(0).value, t)
                  } else {
                    return AST.CoreExp.LitRange(Z(e.lits(0).value).get, t)
                  }
                case _ => halt(s"TODO: $e")
              }
            case _ => halt(s"Infeasible: expected typed expression")
          }
        case e: AST.Exp.Tuple =>
          if (e.args.size == 1) {
            return rec(e.args(0), funStack, localMap)
          } else {
            val t = e.typedOpt.get
            return AST.CoreExp.Apply(
              AST.CoreExp.ObjectVarRef(AST.CoreExp.tupleOwner, e.args.size.string, t),
              for (arg <- e.args) yield rec(arg, funStack, localMap), t)
          }
        case e: AST.Exp.Ident =>
          e.resOpt.get match {
            case res: AST.ResolvedInfo.LocalVar =>
              localMap.get((res.context, res.id)) match {
                case Some(r) => return r
                case _ =>
              }
              val id = res.id
              val stackSize = funStack.size
              for (i <- stackSize - 1 to 0 by -1) {
                val p = funStack.elements(i)
                if (p._1 == id) {
                  return AST.CoreExp.ParamVarRef(stackSize - i, id, p._2.subst(sm))
                }
              }
              return AST.CoreExp.LocalVarRef(res.context, id, e.typedOpt.get.subst(sm))
            case res: AST.ResolvedInfo.Var if res.isInObject =>
              return AST.CoreExp.ObjectVarRef(res.owner, res.id, e.typedOpt.get.subst(sm))
            case res: AST.ResolvedInfo.Method => halt(s"TODO: $e")
            case _ => halt(s"Infeasible: $e")
          }
        case e: AST.Exp.Unary =>
          val t = e.typedOpt.get.subst(sm)
          return AST.CoreExp.Apply(AST.CoreExp.ObjectVarRef(AST.CoreExp.unaryOwner, e.opString, t),
            ISZ(rec(e.exp, funStack, localMap)), t)
        case e: AST.Exp.Binary =>
          val t = e.typedOpt.get.subst(sm)
          e.attr.resOpt.get match {
            case _: AST.ResolvedInfo.BuiltIn =>
              return AST.CoreExp.Apply(AST.CoreExp.ObjectVarRef(AST.CoreExp.binaryOwner, e.op, t),
                ISZ(rec(e.left, funStack, localMap), rec(e.right, funStack, localMap)), t)
            case _ => halt(s"TODO: $e")
          }
        case e: AST.Exp.If =>
          return AST.CoreExp.If(rec(e.cond, funStack, localMap), rec(e.elseExp, funStack, localMap),
            rec(e.elseExp, funStack, localMap), e.typedOpt.get.subst(sm))
        case e: AST.Exp.Fun =>
          val params: ISZ[(String, AST.Typed)] = for (p <- e.params) yield
            (p.idOpt.get.value, p.typedOpt.get.subst(sm))
          var stack = funStack
          for (p <- params) {
            stack = stack.push(p)
          }
          val last = params(params.size - 1)
          var r = AST.CoreExp.Fun(AST.CoreExp.Param(last._1, last._2), recAssignExp(e.exp, stack, localMap))
          for (i <- params.size - 2 to 0 by -1) {
            val p = params(i)
            r = AST.CoreExp.Fun(AST.CoreExp.Param(p._1, p._2), r)
          }
          return r
        case e: AST.Exp.Quant =>
          val kind: AST.CoreExp.Quant.Kind.Type =
            if (e.isForall) AST.CoreExp.Quant.Kind.ForAll
            else AST.CoreExp.Quant.Kind.Exists
          val params: ISZ[(String, AST.Typed)] = for (i <- 0 until e.fun.params.size) yield
            (e.fun.params(i).idOpt.get.value, e.fun.params(i).typedOpt.get.subst(sm))
          var stack = funStack
          for (p <- params) {
            stack = stack.push(p)
          }
          val last = params(params.size - 1)
          var r = AST.CoreExp.Quant(kind, AST.CoreExp.Param(last._1, last._2),
            recAssignExp(e.fun.exp, stack, localMap))
          for (i <- params.size - 2 to 0 by -1) {
            val p = params(i)
            r = AST.CoreExp.Quant(kind, AST.CoreExp.Param(p._1, p._2), r)
          }
          return r
        case e: AST.Exp.StrictPureBlock =>
          return recStmt(e.block, funStack, localMap)._1.get
        case e: AST.Exp.Invoke =>
          val args: ISZ[AST.CoreExp] = for (arg <- e.args) yield rec(arg, funStack, localMap)
          e.receiverOpt match {
            case Some(receiver) =>
              return AST.CoreExp.Apply(
                AST.CoreExp.Select(rec(receiver, funStack, localMap), e.ident.id.value,
                  e.ident.typedOpt.get), args, e.typedOpt.get.subst(sm))
            case _ => return AST.CoreExp.Apply(rec(e.ident, funStack, localMap),
              args, e.typedOpt.get.subst(sm))
          }
        case e: AST.Exp.InvokeNamed =>
          val args = MS.create[Z, AST.CoreExp](e.args.size, AST.CoreExp.LitB(F))
          for (arg <- e.args) {
            args(arg.index) = rec(arg.arg, funStack, localMap)
          }
          e.receiverOpt match {
            case Some(receiver) =>
              return AST.CoreExp.Apply(
                AST.CoreExp.Select(rec(receiver, funStack, localMap), e.ident.id.value,
                  e.ident.typedOpt.get.subst(sm)),
                args.toISZ, e.typedOpt.get.subst(sm))
            case _ => return AST.CoreExp.Apply(rec(e.ident, funStack, localMap),
              args.toISZ, e.typedOpt.get.subst(sm))
          }
        case e => halt(s"TODO: $e")
      }
    }
    return rec(exp, Stack.empty, HashSMap.empty)
  }

  @pure def unifyExp(localPatterns: LocalPatternSet, pattern: AST.CoreExp, exp: AST.CoreExp,
                     init: UnificationMap,
                     pendingApplications: MBox[PendingApplications],
                     errorMessages: MBox[UnificationErrorMessages]): UnificationMap = {
    @pure def rootLocalPatternOpt(e: AST.CoreExp, args: ISZ[AST.CoreExp]): Option[(ISZ[String], String, AST.Typed, ISZ[AST.CoreExp])] = {
      e match {
        case e: AST.CoreExp.LocalVarRef =>
          val p = (e.context, e.id)
          return if (localPatterns.contains(p)) Some((p._1, p._2, e.tipe, args)) else None()
        case e: AST.CoreExp.Apply => return rootLocalPatternOpt(e.exp, e.args ++ args)
        case _ => return None()
      }
    }
    var map = init
    def err(p: AST.CoreExp, e: AST.CoreExp): Unit = {
      errorMessages.value = errorMessages.value :+
        st"""Could not unify '${p.prettyST}' with '${e.prettyST}' in
            |${pattern.prettyST}, and
            |${exp.prettyST}""".render
    }
    def err2(id: String, e1: AST.CoreExp, e2: AST.CoreExp): Unit = {
      errorMessages.value = errorMessages.value :+
        st"""Could not unify local pattern '$id' with multiple expressions:
            |* ${(ISZ(e1.prettyST, e2.prettyST), "\n* ")}""".render
    }
    def matchPatternLocals(p: AST.CoreExp, e: AST.CoreExp): Unit = {
      if (errorMessages.value.nonEmpty) {
        return
      }
      (p, e) match {
        case (p: AST.CoreExp.Lit, e) =>
          if (p != e) {
            err(p, e)
          }
        case (p: AST.CoreExp.LocalVarRef, e) =>
          val key = (p.context, p.id)
          if (localPatterns.contains(key)) {
            map.get(key) match {
              case Some(pe) =>
                if (pe != e) {
                  err2(p.id, pe, e)
                }
              case _ => map = map + key ~> e
            }
          } else if (p != e) {
            err(p, e)
          }
        case (p: AST.CoreExp.ParamVarRef, e: AST.CoreExp.ParamVarRef) =>
          if (p.deBruijn != e.deBruijn) {
            err(p, e)
          }
        case (p: AST.CoreExp.ObjectVarRef, e: AST.CoreExp.ObjectVarRef) =>
          if (!(p.id == e.id && p.owner == e.owner)) {
            err(p, e)
          }
        case (p: AST.CoreExp.Select, e: AST.CoreExp.Select) =>
          if (p.id != e.id) {
            err(p, e)
          } else {
            matchPatternLocals(p.exp, e.exp)
          }
        case (p: AST.CoreExp.If, e: AST.CoreExp.If) =>
          matchPatternLocals(p.cond, e.cond)
          matchPatternLocals(p.tExp, e.tExp)
          matchPatternLocals(p.fExp, e.fExp)
        case (p: AST.CoreExp.Apply, e) =>
          rootLocalPatternOpt(p, ISZ()) match {
            case Some((context, id, f: AST.Typed.Fun, args)) =>
              @strictpure def getArgTypes(t: AST.Typed, acc: ISZ[AST.Typed]): ISZ[AST.Typed] = t match {
                case f: AST.Typed.Fun => getArgTypes(f.ret, acc :+ f.args(0))
                case _ => acc
              }
              @strictpure def paramId(n: Z): String = s"_$n"
              val argTypes = getArgTypes(f.curried, ISZ())
              if (args.size == argTypes.size) {
                @pure def hasLocalPatternInArgs: B = {
                  val lpd = LocalPatternDetector(localPatterns, F)
                  for (arg <- args) {
                    lpd.transformCoreExp(arg)
                  }
                  return lpd.hasLocalPattern
                }
                if (hasLocalPatternInArgs) {
                  pendingApplications.value = pendingApplications.value :+ (context, id, args, e)
                } else {
                  var substMap = HashMap.empty[AST.CoreExp, AST.CoreExp.ParamVarRef]
                  for (i <- 0 until args.size) {
                    val n = args.size - i
                    substMap = substMap + args(i) ~> AST.CoreExp.ParamVarRef(n, paramId(n), argTypes(i))
                  }
                  val se = Substitutor(substMap).transformCoreExp(e).getOrElse(e)
                  var r: AST.CoreExp = AST.CoreExp.Fun(AST.CoreExp.Param(paramId(1), argTypes(args.size - 1)), se)
                  for (i <- args.size - 2 to 0 by -1) {
                    r = AST.CoreExp.Fun(AST.CoreExp.Param(paramId(args.size - i), argTypes(i)), r)
                  }
                  val key = (context, id)
                  map.get(key) match {
                    case Some(f) => err2(id, f, r)
                    case _ => map = map + key ~> r
                  }
                }
                return
              }
            case _ =>
          }
          e match {
            case e: AST.CoreExp.Apply =>
              if (p.args.size != e.args.size) {
                err(p, e)
              } else {
                matchPatternLocals(p.exp, e.exp)
                for (argPair <- ops.ISZOps(p.args).zip(e.args)) {
                  matchPatternLocals(argPair._1, argPair._2)
                }
              }
            case _ => err(p, e)
          }
        case (p: AST.CoreExp.Fun, e: AST.CoreExp.Fun) =>
          matchPatternLocals(p.exp, e.exp)
        case (p: AST.CoreExp.Quant, e: AST.CoreExp.Quant) =>
          if (p.kind != e.kind) {
            err(p, e)
          } else {
            matchPatternLocals(p.exp, e.exp)
          }
        case (_, _) =>
          err(p, e)
      }
    }

    matchPatternLocals(pattern, exp)

    return map
  }

  @pure def applyFun(exp: AST.CoreExp, args: ISZ[AST.CoreExp]): Option[AST.CoreExp] = {
    @pure def applyFunArg(fun: AST.CoreExp.Fun, arg: AST.CoreExp): AST.CoreExp = {
      @pure def applyFunRec(deBruijn: Z, e: AST.CoreExp): AST.CoreExp = {
        e match {
          case _: AST.CoreExp.Lit => return e
          case _: AST.CoreExp.LocalVarRef => return e
          case e: AST.CoreExp.ParamVarRef => return if (e.deBruijn == deBruijn) arg else e
          case _: AST.CoreExp.ObjectVarRef => return e
          case e: AST.CoreExp.Select => return e(exp = applyFunRec(deBruijn, e.exp))
          case e: AST.CoreExp.If => return e(cond = applyFunRec(deBruijn, e.cond), tExp = applyFunRec(deBruijn, e.tExp),
            fExp = applyFunRec(deBruijn, e.fExp))
          case e: AST.CoreExp.Apply => return e(exp = applyFunRec(deBruijn, e.exp), args = for (a <- e.args) yield applyFunRec(deBruijn, a))
          case e: AST.CoreExp.Fun => return e(exp = applyFunRec(deBruijn + 1, e.exp))
          case e: AST.CoreExp.Quant => return e(exp = applyFunRec(deBruijn + 1, e.exp))
          case e: AST.CoreExp.InstanceOfExp => return e(exp = applyFunRec(deBruijn, e.exp))
        }
      }
      return applyFunRec(1, fun.exp)
    }
    var e = exp
    for (arg <- args) {
      e match {
        case f: AST.CoreExp.Fun => e = applyFunArg(f, arg)
        case _ => return None()
      }
    }
    return Some(e)
  }

  @pure def unify(localPatterns: LocalPatternSet, patterns: ISZ[AST.CoreExp], exps: ISZ[AST.CoreExp]): UnificationResult = {
    val errorMessages: MBox[UnificationErrorMessages] = MBox(ISZ())
    val pendingApplications: MBox[PendingApplications] = MBox(ISZ())
    var m: UnificationMap = HashSMap.empty
    for (i <- 0 until patterns.size) {
      m = unifyExp(localPatterns, patterns(i), exps(i), m, pendingApplications, errorMessages)
      if (errorMessages.value.nonEmpty) {
        return Either.Right(errorMessages.value)
      }
    }

    while (pendingApplications.value.nonEmpty) {
      val pas = pendingApplications.value
      pendingApplications.value = ISZ()
      for (pa <- pas) {
        val (context, id, args, e) = pa
        m.get((context, id)) match {
          case Some(f: AST.CoreExp.Fun) =>
            applyFun(f, args) match {
              case Some(pattern) => m = unifyExp(localPatterns, pattern, e, m, pendingApplications, errorMessages)
              case _ => errorMessages.value = errorMessages.value :+
                st"Could not reduce '$f(${(for (arg <- args) yield arg.prettyST, ", ")})'".render
            }
          case Some(f) => errorMessages.value = errorMessages.value :+ s"Expecting to infer a function, but found '$f'"
          case _ =>
        }
        if (errorMessages.value.nonEmpty) {
          return Either.Right(errorMessages.value)
        }
      }
    }

    for (localPattern <- localPatterns.elements if !m.contains(localPattern)) {
      errorMessages.value = errorMessages.value :+ s"Could not find any matching expression for '${localPattern._2}'"
    }
    return if (errorMessages.value.nonEmpty) Either.Right(errorMessages.value)
    else Either.Left(m)
  }

  @pure def simplify(exp: AST.CoreExp): Option[AST.CoreExp] = {
    return None() // TODO
  }
}
