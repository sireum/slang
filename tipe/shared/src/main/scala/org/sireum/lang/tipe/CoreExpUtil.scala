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
  type LocalMap = HashSMap[(ISZ[String], String), AST.CoreExp]
  type LocalPatternSet = HashSSet[(ISZ[String], String)]
  type UnificationResult = Either[HashSMap[(ISZ[String], String), AST.CoreExp], ISZ[String]]

  @record class CoreExpSubsitutor(val map: HashMap[AST.CoreExp, AST.CoreExp.ParamVarRef]) extends AST.MCoreExpTransformer {
    override def transformCoreExp(o: AST.CoreExp): MOption[AST.CoreExp] = {
      map.get(o) match {
        case Some(pvr) => return MSome(pvr)
        case _ => return super.transformCoreExp(o)
      }
    }
  }

  @pure def translate(th: TypeHierarchy, exp: AST.Exp): AST.CoreExp = {
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
                  return AST.CoreExp.ParamVarRef(stackSize - i, id, p._2)
                }
              }
              return AST.CoreExp.LocalVarRef(res.context, id, e.typedOpt.get)
            case res: AST.ResolvedInfo.Var if res.isInObject =>
              return AST.CoreExp.ObjectVarRef(res.owner, res.id, e.typedOpt.get)
            case res: AST.ResolvedInfo.Method => halt(s"TODO: $e")
            case _ => halt(s"Infeasible: $e")
          }
        case e: AST.Exp.Unary =>
          val t = e.typedOpt.get
          return AST.CoreExp.Apply(AST.CoreExp.ObjectVarRef(AST.CoreExp.unaryOwner, e.opString, t),
            ISZ(rec(e.exp, funStack, localMap)), t)
        case e: AST.Exp.Binary =>
          val t = e.typedOpt.get
          e.attr.resOpt.get match {
            case _: AST.ResolvedInfo.BuiltIn =>
              return AST.CoreExp.Apply(AST.CoreExp.ObjectVarRef(AST.CoreExp.binaryOwner, e.op, t),
                ISZ(rec(e.left, funStack, localMap), rec(e.right, funStack, localMap)), t)
            case _ => halt(s"TODO: $e")
          }
        case e: AST.Exp.If =>
          return AST.CoreExp.If(rec(e.cond, funStack, localMap), rec(e.elseExp, funStack, localMap),
            rec(e.elseExp, funStack, localMap), e.typedOpt.get)
        case e: AST.Exp.Fun =>
          val params: ISZ[(String, AST.Typed)] = for (p <- e.params) yield
            (p.idOpt.get.value, p.typedOpt.get)
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
            (e.fun.params(i).idOpt.get.value, e.fun.params(i).typedOpt.get)
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
                  e.ident.typedOpt.get), args, e.typedOpt.get)
            case _ => return AST.CoreExp.Apply(rec(e.ident, funStack, localMap),
              args, e.typedOpt.get)
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
                  e.ident.typedOpt.get),
                args.toISZ, e.typedOpt.get)
            case _ => return AST.CoreExp.Apply(rec(e.ident, funStack, localMap),
              args.toISZ, e.typedOpt.get)
          }
        case e => halt(s"TODO: $e")
      }
    }
    return rec(exp, Stack.empty, HashSMap.empty)
  }

  @pure def unify(th: TypeHierarchy, localPatterns: LocalPatternSet, pattern: AST.CoreExp, exp: AST.CoreExp): UnificationResult = {
    @pure def rootLocalPatternOpt(e: AST.CoreExp.Apply, args: ISZ[AST.CoreExp]): Option[(ISZ[String], String, AST.Typed, ISZ[AST.CoreExp])] = {
      e.exp match {
        case e2: AST.CoreExp.LocalVarRef =>
          val p = (e2.context, e2.id)
          return if (localPatterns.contains(p)) Some((p._1, p._2, e2.tipe, args)) else None()
        case e2: AST.CoreExp.Apply => return rootLocalPatternOpt(e2, e2.args ++ args)
        case _ => return None()
      }
    }
    var map = HashSMap.empty[(ISZ[String], String), HashSSet[AST.CoreExp]]
    var errorMessages = ISZ[String]()
    def err(p: AST.CoreExp, e: AST.CoreExp): Unit = {
      errorMessages = errorMessages :+
        st"""Could not unify '${p.prettyST}' with '${e.prettyST}' in
            |${pattern.prettyST}, and
            |${exp.prettyST}""".render
    }
    def matchPatternLocals(p: AST.CoreExp, e: AST.CoreExp): Unit = {
      if (errorMessages.nonEmpty) {
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
            val s = map.get(key).getOrElse(HashSSet.empty)
            map = map + key ~> (s + e)
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
              val argTypes = getArgTypes(f.curried, ISZ())
              if (args.size == argTypes.size) {
                var substMap = HashMap.empty[AST.CoreExp, AST.CoreExp.ParamVarRef]
                for (i <- 0 until args.size) {
                  val n = i + 1
                  substMap = substMap + args(0) ~> AST.CoreExp.ParamVarRef(n, s"_$n", argTypes(i))
                }
                val se = CoreExpSubsitutor(substMap).transformCoreExp(e).getOrElse(e)
                var r: AST.CoreExp = AST.CoreExp.Fun(AST.CoreExp.Param(s"_${args.size - 1}", argTypes(args.size - 1)), se)
                for (i <- args.size - 2 to 0 by -1) {
                  r = AST.CoreExp.Fun(AST.CoreExp.Param(s"_$i", argTypes(i)), r)
                }
                val key = (context, id)
                val s = map.get(key).getOrElse(HashSSet.empty)
                map = map + key ~> (s + r)
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

    if (errorMessages.nonEmpty) {
      return Either.Right(errorMessages)
    }

    for (p <- map.entries) {
      val ((_, id), s) = p
      if (s.size > 1) {
        errorMessages = errorMessages :+
          st"""Could not unify local pattern '$id' with multiple expressions:
              |* ${(for (ce <- s.elements) yield ce.prettyST, "\n* ")}""".render
      }
    }

    if (errorMessages.nonEmpty) {
      return Either.Right(errorMessages)
    }

    return Either.Left(HashSMap ++ (for (p <- map.entries) yield (p._1, p._2.elements(0))))
  }

  @pure def simplify(exp: AST.CoreExp): Option[AST.CoreExp] = {
    return None() // TODO
  }
}
