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
import org.sireum.lang.symbol.TypeInfo

object CoreExpTranslator {
  type FunStack = Stack[(String, AST.Typed)]
  type Local = (ISZ[String], String)
  type LocalMap = HashSMap[Local, AST.CoreExp.Base]
  type LocalPatternSet = HashSSet[Local]

  @enum object Mode {
    "Base"
    "BasePattern"
    "Extended"
  }
}

import CoreExpTranslator._

@datatype class CoreExpTranslator(val th: TypeHierarchy, val mode: Mode.Type) {
  val isPattern: B = mode == Mode.BasePattern

  @pure def translateBody(body: AST.Body, posOpt: Option[message.Position], funStack: FunStack, localMap: LocalMap): AST.CoreExp.Base = {
    if (mode == Mode.Extended) {
      return AST.CoreExp.Extended.AssignExp(AST.Stmt.Block(body, AST.Attr(posOpt)), funStack, localMap)
    }
    val stmts = body.stmts
    var m = localMap
    for (i <- 0 until stmts.size - 1) {
      m = translateStmt(stmts(i), funStack, m)._2
    }
    return translateAssignExp(stmts(stmts.size - 1).asAssignExp, funStack, m)
  }
  @pure def translatePattern(exp: AST.CoreExp.Base, pattern: AST.Pattern, funStack: FunStack, localMap: LocalMap): (ISZ[AST.CoreExp.Base], LocalMap) = {
    var r = ISZ[AST.CoreExp.Base]()
    var lMap = localMap
    pattern match {
      case _: AST.Pattern.Wildcard => // skip
      case pattern: AST.Pattern.Literal =>
        r = r :+ AST.CoreExp.Binary(exp, AST.Exp.BinaryOp.EquivUni,
          translateExp(pattern.lit, funStack, localMap), AST.Typed.b)
      case pattern: AST.Pattern.VarBinding =>
        lMap = lMap + (pattern.idContext, pattern.id.value) ~> exp
      case pattern: AST.Pattern.Structure =>
        val t = pattern.typedOpt.get
        lMap = pattern.idOpt match {
          case Some(id) => localMap + (pattern.idContext, id.value) ~> exp
          case _ => localMap
        }
        t match {
          case t: AST.Typed.Tuple =>
            var i = 0
            var conds = ISZ[AST.CoreExp.Base]()
            for (j <- 0 until t.args.size) {
              val pat = pattern.patterns(i)
              val f = AST.CoreExp.Select(exp, s"_${j + 1}", pat.typedOpt.get)
              val (pconds, lMap2) = translatePattern(f, pat, funStack, lMap)
              conds = conds ++ pconds
              lMap = lMap2
              i = i + 1
            }
            r = r :+ AST.CoreExp.condAnd(AST.CoreExp.InstanceOfExp(T, exp, t), AST.CoreExp.bigAnd(conds))
          case t: AST.Typed.Name =>
            var conds = ISZ[AST.CoreExp.Base]()
            if (t.ids == AST.Typed.isName || t.ids == AST.Typed.msName) {
              val hasWildcard = pattern.patterns.size > 0 && pattern.patterns(pattern.patterns.size - 1).
                isInstanceOf[AST.Pattern.SeqWildcard]
              val (size, op): (Z, String) = if (hasWildcard) (pattern.patterns.size - 1, AST.Exp.BinaryOp.Ge)
              else (pattern.patterns.size, AST.Exp.BinaryOp.EquivUni)
              var mk: Z => AST.CoreExp.Lit @pure = (i: Z) => AST.CoreExp.LitZ(i)
              var n: Z = th.typeMap.get(t.args(0).asInstanceOf[AST.Typed.Name].ids).get match {
                case ti: TypeInfo.SubZ =>
                  if (ti.ast.isBitVector) {
                    mk = (i: Z) => AST.CoreExp.LitBits(i.string, ti.typedOpt.get)
                  } else {
                    mk = (i: Z) => AST.CoreExp.LitRange(i, ti.typedOpt.get)
                  }
                  if (ti.ast.isZeroIndex) 0 else ti.ast.index
                case _ => 0
              }
              conds = conds :+ AST.CoreExp.Binary(AST.CoreExp.Select(exp, "size", AST.Typed.z), op,
                AST.CoreExp.LitZ(size), AST.Typed.b)
              for (i <- 0 until pattern.patterns.size - (if (hasWildcard) 1 else 0)) {
                val pat = pattern.patterns(i)
                val f = AST.CoreExp.Indexing(exp, mk(n), pat.typedOpt.get)
                val (pconds, lMap2) = translatePattern(f, pat, funStack, lMap)
                conds = conds ++ pconds
                lMap = lMap2
                n = n + 1
              }
            } else {
              val adt = th.typeMap.get(t.ids).get.asInstanceOf[TypeInfo.Adt]
              var i = 0
              for (p <- adt.ast.params if !p.isHidden) {
                val pat = pattern.patterns(i)
                val f = AST.CoreExp.Select(exp, p.id.value, pat.typedOpt.get)
                val (pconds, lMap2) = translatePattern(f, pat, funStack, lMap)
                conds = conds ++ pconds
                lMap = lMap2
                i = i + 1
              }
            }
            r = r :+ AST.CoreExp.condAnd(AST.CoreExp.InstanceOfExp(T, exp, t), AST.CoreExp.bigAnd(conds))
          case _ => halt("Infeasible")
        }
      case pattern: AST.Pattern.Ref =>
        val right: AST.CoreExp.Base = pattern.attr.resOpt.get match {
          case res: AST.ResolvedInfo.Var =>
            if (res.isInObject) {
              val ids = pattern.name.ids
              val owner: ISZ[String] = for (i <- 0 until pattern.name.ids.size - 1) yield ids(i).value
              AST.CoreExp.ObjectVarRef(owner, ids(ids.size - 1).value, pattern.typedOpt.get)
            } else {
              AST.CoreExp.Select(
                AST.CoreExp.LocalVarRef(F, pattern.idContext, "this", pattern.receiverTipeOpt.get),
                res.id, pattern.typedOpt.get)
            }
          case res: AST.ResolvedInfo.LocalVar =>
            translateLocalInfo(res, pattern.typedOpt.get, funStack, localMap)
          case res: AST.ResolvedInfo.EnumElement =>
            AST.CoreExp.LitEnum(res.owner, res.name, res.ordinal)
          case _ => halt("Infeasible")
        }
        r = r :+ AST.CoreExp.Binary(exp, AST.Exp.BinaryOp.EquivUni, right, AST.Typed.b)
      case pattern: AST.Pattern.LitInterpolate => halt("TODO")
      case _: AST.Pattern.SeqWildcard => halt("Infeasible")
    }
    return (r, lMap)
  }
  @pure def translateLocalInfo(res: AST.ResolvedInfo.LocalVar, t: AST.Typed, funStack: FunStack, localMap: LocalMap): AST.CoreExp.Base = {
    localMap.get((res.context, res.id)) match {
      case Some(r) =>
        if (mode == Mode.Extended) {
          return AST.CoreExp.LocalVarRef(F, res.context, res.id, t)
        }
        return r
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
    return AST.CoreExp.LocalVarRef(isPattern, res.context, id, t)
  }
  @pure def translateStmt(stmt: AST.Stmt, funStack: FunStack, localMap: LocalMap): (Option[AST.CoreExp.Base], LocalMap) = {
    stmt match {
      case stmt: AST.Stmt.Expr =>
        return (Some(translateExp(stmt.exp, funStack, localMap)), localMap)
      case stmt: AST.Stmt.Var =>
        val res = stmt.attr.resOpt.get.asInstanceOf[AST.ResolvedInfo.LocalVar]
        return (None(), localMap + (res.context, res.id) ~>
          translateAssignExp(stmt.initOpt.get, funStack, localMap))
      case stmt: AST.Stmt.Block =>
        return (Some(translateBody(stmt.body, stmt.attr.posOpt, funStack, localMap)), localMap)
      case stmt: AST.Stmt.If =>
        val condExp = translateExp(stmt.cond, funStack, localMap)
        val tExp = translateBody(stmt.thenBody, stmt.attr.posOpt, funStack, localMap)
        val fExp = translateBody(stmt.elseBody, stmt.attr.posOpt, funStack, localMap)
        return (Some(AST.CoreExp.If(condExp, tExp, fExp, stmt.attr.typedOpt.get)), localMap)
      case stmt: AST.Stmt.VarPattern =>
        val exp = translateAssignExp(stmt.init, funStack, localMap)
        val (conds, lMap) = translatePattern(exp, stmt.pattern, funStack, localMap)
        val cond = AST.CoreExp.bigAnd(conds)
        var lMap2 = lMap
        for (p <- lMap.entries if localMap.get(p._1) != Some(p._2)) {
          lMap2 = lMap2 + p._1 ~> AST.CoreExp.ite(cond, p._2, AST.CoreExp.Abort, p._2.tipe)
        }
        return (None(), lMap2)
      case stmt: AST.Stmt.Match =>
        if (mode == Mode.Extended) {
          return (Some(AST.CoreExp.Extended.AssignExp(stmt, funStack, localMap)), localMap)
        }
        val exp = translateExp(stmt.exp, funStack, localMap)
        var condBodyPairs = ISZ[(AST.CoreExp.Base, AST.CoreExp.Base)]()
        for (cas <- stmt.cases) {
          val (conds, lMap) = translatePattern(exp, cas.pattern, funStack, localMap)
          val conds2: ISZ[AST.CoreExp.Base] = cas.condOpt match {
            case Some(cond) => conds :+ translateExp(cond, funStack, lMap)
            case _ => conds
          }
          val body = translateBody(cas.body, stmt.posOpt, funStack, lMap)
          condBodyPairs = condBodyPairs :+ (AST.CoreExp.bigAnd(conds2), body)
        }
        val t = stmt.typedOpt.get
        val (lastCond, lastBody) = condBodyPairs(condBodyPairs.size - 1)
        var r = AST.CoreExp.ite(
          AST.CoreExp.bigAnd((for (j <- 0 until condBodyPairs.size - 1) yield
            AST.CoreExp.Unary(AST.Exp.UnaryOp.Not, condBodyPairs(j)._1).asInstanceOf[AST.CoreExp.Base]) :+ lastCond),
          lastBody, AST.CoreExp.Abort, t)
        for (i <- condBodyPairs.size - 2 to 0 by -1) {
          val (cond, body) = condBodyPairs(i)
          r = AST.CoreExp.ite(
            AST.CoreExp.bigAnd((for (j <- 0 until i) yield
              AST.CoreExp.Unary(AST.Exp.UnaryOp.Not, condBodyPairs(j)._1).asInstanceOf[AST.CoreExp.Base]) :+ cond),
            body, r, t)
        }
        return (Some(r), localMap)
      case _ => halt(s"Infeasible: $stmt")
    }
  }
  @pure def translateAssignExp(ae: AST.AssignExp, funStack: FunStack, localMap: LocalMap): AST.CoreExp.Base = {
    if (mode == Mode.Extended) {
      return AST.CoreExp.Extended.AssignExp(ae, funStack, localMap)
    } else {
      val (Some(r), _) = translateStmt(ae.asStmt, funStack, localMap)
      return r
    }
  }
  @pure def translateExp(e: AST.Exp, funStack: FunStack, localMap: LocalMap): AST.CoreExp.Base = {
    @strictpure def asAssignExp: AST.CoreExp.Base = {
      assert(mode == Mode.Extended)
      AST.CoreExp.Extended.AssignExp(AST.Stmt.Expr(e, AST.TypedAttr(e.posOpt, e.typedOpt)), funStack, localMap)
    }

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
              case _ => return AST.CoreExp.StringInterpolate(e.prefix, for (lit <- e.lits) yield lit.value,
                for (arg <- e.args) yield translateExp(arg, funStack, localMap), e.typedOpt.get)
            }
          case _ => halt(s"Infeasible: expected typed expression")
        }
      case e: AST.Exp.Tuple =>
        return if (e.args.size == 1) translateExp(e.args(0), funStack, localMap)
        else AST.CoreExp.Constructor(e.typedOpt.get, for (arg <- e.args) yield translateExp(arg, funStack, localMap))
      case e: AST.Exp.This =>
        return AST.CoreExp.LocalVarRef(F, e.owner, "this", e.typedOpt.get)
      case e: AST.Exp.Ident =>
        e.resOpt.get match {
          case res: AST.ResolvedInfo.LocalVar =>
            return translateLocalInfo(res, e.typedOpt.get, funStack, localMap)
          case res: AST.ResolvedInfo.Var if res.isInObject =>
            if (res.owner == AST.Typed.sireumName) {
              res.id match {
                case "T" => return AST.CoreExp.True
                case "F" => return AST.CoreExp.False
                case _ =>
              }
            }
            return AST.CoreExp.ObjectVarRef(res.owner, res.id, e.typedOpt.get)
          case res: AST.ResolvedInfo.Method if res.isInObject =>
            return AST.CoreExp.ObjectVarRef(res.owner, res.id, e.typedOpt.get)
          case _ =>
            halt(s"Infeasible: $e")
        }
      case e: AST.Exp.Unary =>
        val op: AST.Exp.UnaryOp.Type = if (e.typedOpt.get == AST.Typed.b && e.op == AST.Exp.UnaryOp.Complement)
          AST.Exp.UnaryOp.Not else e.op
        return AST.CoreExp.Unary(op, translateExp(e.exp, funStack, localMap))
      case e: AST.Exp.Binary =>
        e.attr.resOpt.get match {
          case res: AST.ResolvedInfo.BuiltIn =>
            val left = translateExp(e.left, funStack, localMap)
            val right = translateExp(e.right, funStack, localMap)
            val op: String = res.kind match {
              case AST.ResolvedInfo.BuiltIn.Kind.BinaryCondAnd =>
                return AST.CoreExp.condAnd(left, right)
              case AST.ResolvedInfo.BuiltIn.Kind.BinaryCondOr =>
                return AST.CoreExp.condOr(left, right)
              case AST.ResolvedInfo.BuiltIn.Kind.BinaryCondImply =>
                return AST.CoreExp.condImply(left, right)
              case AST.ResolvedInfo.BuiltIn.Kind.BinaryEquiv => AST.Exp.BinaryOp.EquivUni
              case AST.ResolvedInfo.BuiltIn.Kind.BinaryEq if th.isSubstitutableWithoutSpecVars(left.tipe) => AST.Exp.BinaryOp.EquivUni
              case AST.ResolvedInfo.BuiltIn.Kind.BinaryInequiv => AST.Exp.BinaryOp.InequivUni
              case AST.ResolvedInfo.BuiltIn.Kind.BinaryNe if th.isSubstitutableWithoutSpecVars(left.tipe) => AST.Exp.BinaryOp.InequivUni
              case _ => e.op
            }
            return AST.CoreExp.Binary(left, op, right, e.typedOpt.get)
          case res: AST.ResolvedInfo.Method =>
            return AST.CoreExp.Apply(AST.CoreExp.Select(translateExp(e.left, funStack, localMap), e.op,
              AST.Typed.Method(res.isInObject, res.mode, res.typeParams, res.owner, res.id, res.paramNames,
                res.tpeOpt.get)), ISZ(translateExp(e.right, funStack, localMap)), e.typedOpt.get)
          case _ => halt(s"Infeasible: $e")
        }
      case e: AST.Exp.Select =>
        e.resOpt.get match {
          case res: AST.ResolvedInfo.EnumElement => return AST.CoreExp.LitEnum(res.owner, res.name, res.ordinal)
          case res: AST.ResolvedInfo.Method if res.isInObject => return AST.CoreExp.ObjectVarRef(res.owner, res.id, e.typedOpt.get)
          case _ =>
        }
        e.receiverOpt match {
          case Some(receiver) => return AST.CoreExp.Select(translateExp(receiver, funStack, localMap), e.id.value, e.typedOpt.get)
          case _ => return translateExp(AST.Exp.Ident(e.id, e.attr), funStack, localMap)
        }
      case e: AST.Exp.If =>
        return AST.CoreExp.ite(translateExp(e.cond, funStack, localMap), translateExp(e.thenExp, funStack, localMap),
          translateExp(e.elseExp, funStack, localMap), e.typedOpt.get)
      case e: AST.Exp.Fun =>
        val params: ISZ[(String, AST.Typed)] = for (p <- e.params) yield
          (p.idOpt.get.value, p.typedOpt.get)
        var stack = funStack
        for (p <- params) {
          stack = stack.push(p)
        }
        val last = params(params.size - 1)
        var r = AST.CoreExp.Fun(AST.CoreExp.Param(last._1, last._2), translateAssignExp(e.exp, stack, localMap))
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
          translateAssignExp(e.fun.exp, stack, localMap))
        for (i <- params.size - 2 to 0 by -1) {
          val p = params(i)
          r = AST.CoreExp.Quant(kind, AST.CoreExp.Param(p._1, p._2), r)
        }
        return r
      case e: AST.Exp.StrictPureBlock =>
        return translateStmt(e.block, funStack, localMap)._1.get
      case e: AST.Exp.Invoke =>
        def args: ISZ[AST.CoreExp.Base] = {
          return for (arg <- e.args) yield translateExp(arg, funStack, localMap)
        }
        e.attr.resOpt.get match {
          case res: AST.ResolvedInfo.Method =>
            res.mode match {
              case AST.MethodMode.Spec =>
              case AST.MethodMode.Method =>
              case AST.MethodMode.Constructor =>
                return AST.CoreExp.Constructor(e.typedOpt.get, args)
              case AST.MethodMode.Select =>
                e.receiverOpt match {
                  case Some(receiver) => return AST.CoreExp.Indexing(translateExp(receiver, funStack, localMap),
                    translateExp(e.args(0), funStack, localMap), e.typedOpt.get)
                  case _ => return AST.CoreExp.Indexing(translateExp(e.ident, funStack, localMap),
                    translateExp(e.args(0), funStack, localMap), e.typedOpt.get)
                }
              case AST.MethodMode.Store =>
                val ie = translateExp(e.args(0), funStack, localMap)
                val tuple = ie.tipe.asInstanceOf[AST.Typed.Tuple]
                val (index, value): (AST.CoreExp.Base, AST.CoreExp.Base) = ie match {
                  case AST.CoreExp.Constructor(_, ISZ(i, v)) => (i, v)
                  case AST.CoreExp.Binary(i, AST.Exp.BinaryOp.MapsTo, v, _) => (i, v)
                  case _ => (AST.CoreExp.Select(ie, "_1", tuple.args(0)), AST.CoreExp.Select(ie, "_2", tuple.args(1)))
                }
                e.receiverOpt match {
                  case Some(receiver) => return AST.CoreExp.IndexingUpdate(translateExp(receiver, funStack, localMap),
                    index, value, e.typedOpt.get)
                  case _ => return AST.CoreExp.IndexingUpdate(translateExp(e.ident, funStack, localMap),
                    index, value, e.typedOpt.get)
                }
              case AST.MethodMode.Ext => halt("TODO")
              case AST.MethodMode.Extractor => halt("Infeasible")
              case AST.MethodMode.ObjectConstructor => halt("Infeasible")
              case AST.MethodMode.Just => halt("Infeasible")
              case AST.MethodMode.Copy => halt("Infeasible")
            }
          case AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Halt) =>
            return AST.CoreExp.Abort
          case _ =>
        }
        val inObject: B = e.ident.resOpt.get match {
          case res: AST.ResolvedInfo.Method => res.isInObject
          case res: AST.ResolvedInfo.Var => res.isInObject
          case _: AST.ResolvedInfo.Enum => T
          case _ => F
        }
        e.receiverOpt match {
          case Some(receiver) if !inObject =>
            return AST.CoreExp.Apply(AST.CoreExp.Select(translateExp(receiver, funStack, localMap), e.ident.id.value,
              e.ident.typedOpt.get), args, e.typedOpt.get)
          case _ =>
            return AST.CoreExp.Apply(translateExp(e.ident, funStack, localMap),
              args, e.typedOpt.get)
        }
      case e: AST.Exp.InvokeNamed =>
        def getArgs: ISZ[AST.CoreExp.Base] = {
          val args = MS.create[Z, AST.CoreExp.Base](e.args.size, AST.CoreExp.False)
          for (arg <- e.args) {
            args(arg.index) = translateExp(arg.arg, funStack, localMap)
          }
          return args.toISZ
        }
        e.attr.resOpt.get match {
          case res: AST.ResolvedInfo.Method =>
            res.mode match {
              case AST.MethodMode.Constructor =>
                return AST.CoreExp.Constructor(e.typedOpt.get, getArgs)
              case AST.MethodMode.Spec =>
              case AST.MethodMode.Method =>
              case AST.MethodMode.Copy =>
                var r: AST.CoreExp.Base = e.receiverOpt match {
                  case Some(receiver) => translateExp(receiver, funStack, localMap)
                  case _ => translateExp(e.ident, funStack, localMap)
                }
                val t = e.typedOpt.get
                for (arg <- e.args) {
                  r = AST.CoreExp.Update(r, arg.id.value, translateExp(arg.arg, funStack, localMap), t)
                }
                return r
              case AST.MethodMode.Ext => halt("TODO")
              case AST.MethodMode.Extractor => halt("Infeasible")
              case AST.MethodMode.ObjectConstructor => halt("Infeasible")
              case AST.MethodMode.Just => halt("Infeasible")
              case AST.MethodMode.Select => halt("Infeasible")
              case AST.MethodMode.Store => halt("Infeasible")
            }
          case _ =>
        }
        val inObject: B = e.ident.resOpt.get match {
          case res: AST.ResolvedInfo.Method => res.isInObject
          case res: AST.ResolvedInfo.Var => res.isInObject
          case _: AST.ResolvedInfo.Enum => T
          case _ => F
        }
        e.receiverOpt match {
          case Some(receiver) if !inObject =>
            return AST.CoreExp.Apply(AST.CoreExp.Select(translateExp(receiver, funStack, localMap), e.ident.id.value,
              e.ident.typedOpt.get), getArgs, e.typedOpt.get)
          case _ => return AST.CoreExp.Apply(translateExp(e.ident, funStack, localMap),
            getArgs, e.typedOpt.get)
        }
      case e: AST.Exp.Labeled =>
        val numOpt: Option[Z] = e.numOpt match {
          case Some(num) => Some(num.value)
          case _ => None()
        }
        return AST.CoreExp.Labeled(numOpt, translateExp(e.exp, funStack, localMap))
      case _: AST.Exp.ForYield => return asAssignExp
      case _: AST.Exp.Eta => return asAssignExp
      case _: AST.Exp.At => return asAssignExp
      case _: AST.Exp.Old => return asAssignExp
      case _: AST.Exp.Result => return asAssignExp
      case _: AST.Exp.Super => return asAssignExp
      case _: AST.Exp.TypeCond => return asAssignExp
      case _: AST.Exp.Input => return asAssignExp
      case _: AST.Exp.LoopIndex => halt(s"Infeasible: $e")
      case _: AST.Exp.InfoFlowInvariant => halt(s"Infeasible: $e")
      case _: AST.Exp.AssertAgree => halt(s"Infeasible: $e")
      case _: AST.Exp.AssumeAgree => halt(s"Infeasible: $e")
      case _: AST.Exp.StateSeq => halt(s"Infeasible: $e")
      case _: AST.Exp.Sym => halt(s"Infeasible: $e")
      case _: AST.Exp.RS => halt(s"Infeasible: $e")
      case _: AST.ProofAst.StepId.Num => halt(s"Infeasible: $e")
      case _: AST.ProofAst.StepId.Str => halt(s"Infeasible: $e")
    }
  }
}

