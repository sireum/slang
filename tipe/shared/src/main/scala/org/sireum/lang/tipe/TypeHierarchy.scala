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

package org.sireum.lang.tipe

import org.sireum._
import org.sireum.message._
import org.sireum.lang.symbol._
import org.sireum.lang.symbol.Resolver._
import org.sireum.lang.{ast => AST}

object TypeHierarchy {

  object ExpNormalizer {
    @strictpure def create(th: TypeHierarchy): AST.Transformer[Z] = AST.Transformer(ExpPrePostNormalizer(th))

    @datatype class PrePostSubstitutor(val oldContext: ISZ[String],
                                       val newContext: ISZ[String],
                                       val m: HashMap[String, String]) extends AST.Transformer.PrePost[B] {
      override def postExpIdent(ctx: B, o: AST.Exp.Ident): AST.Transformer.TPostResult[B, AST.Exp] = {
        o.attr.resOpt.get match {
          case res: AST.ResolvedInfo.LocalVar if res.context == oldContext && m.get(res.id).nonEmpty =>
            m.get(res.id) match {
              case Some(newId) =>
                val newO = o(id = o.id(value = newId), attr = o.attr(resOpt = Some(res(context = newContext, id = newId))))
                return AST.Transformer.TPostResult(ctx, Some(newO))
              case _ =>
            }
          case _ =>
        }
        return AST.Transformer.TPostResult(ctx, None())
      }
    }

    @datatype class ExpPrePostNormalizer(val th: TypeHierarchy) extends AST.Transformer.PrePost[Z] {
      override def preExpFun(ctx: Z, o: AST.Exp.Fun): AST.Transformer.PreResult[Z, AST.Exp] = {
        val num = ctx
        val newContextId: String = s".$num"
        var m = HashMap.empty[String, String]
        var i: Z = 1
        var newParams = ISZ[AST.Exp.Fun.Param]()
        for (p <- o.params) {
          p.idOpt match {
            case Some(id) =>
              val newId = id(value = s"$newContextId.$i")
              newParams = newParams :+ p(idOpt = Some(newId))
              m = m + id.value ~> newId.value
            case _ => newParams = newParams :+ p
          }
          i = i + 1
        }
        val newContext = ISZ(newContextId)
        val newExp = AST.Transformer(ExpNormalizer.PrePostSubstitutor(o.context, newContext, m)).
          transformAssignExp(F, o.exp).
          resultOpt.getOrElse(o.exp)
        val newO = o(context = newContext, params = newParams, exp = newExp)
        return AST.Transformer.PreResult(ctx + 1, T, Some(newO))
      }

      override def preExpIdent(ctx: Z, o: AST.Exp.Ident): AST.Transformer.PreResult[Z, AST.Exp] = {
        val (cond, owner) = shouldTransformIdent(o)
        if (cond) {
          return AST.Transformer.PreResult(ctx, T, Some(
            AST.Exp.Select(Some(th.nameToExp(owner, o.posOpt.get).asExp), o.id, o.targs, o.attr)
          ))
        }
        return AST.Transformer.PreResult(ctx, T, None())
      }

      override def preExpSelect(ctx: Z, o: AST.Exp.Select): AST.Transformer.PreResult[Z, AST.Exp] = {
        if (o.targs.nonEmpty) {
          return AST.Transformer.PreResult(ctx, T, Some(o(targs = ISZ())))
        } else {
          return AST.Transformer.PreResult(ctx, T, None())
        }
      }

      override def preExpInvoke(ctx: Z, o: AST.Exp.Invoke): AST.Transformer.PreResult[Z, AST.Exp] = {
        val en = ExpNormalizer.create(th)
        var newArgs = ISZ[AST.Exp]()
        var cctx = ctx
        var changed = o.targs.nonEmpty
        for (arg <- o.args) {
          val p = en.transformExp(cctx, arg)
          cctx = p.ctx
          p.resultOpt match {
            case Some(newArg) =>
              newArgs = newArgs :+ newArg
              changed = T
            case _ => newArgs = newArgs :+ arg
          }
        }
        var newReceiverOpt = Option.none[AST.Exp]()
        o.receiverOpt match {
          case Some(receiver) =>
            val p = en.transformExp(cctx, receiver)
            cctx = p.ctx
            if (p.resultOpt.nonEmpty) {
              newReceiverOpt = p.resultOpt
              changed = T
            } else {
              newReceiverOpt = o.receiverOpt
            }
          case _ =>
            val (cond, owner) = shouldTransformIdent(o.ident)
            if (cond) {
              newReceiverOpt = Some(th.nameToExp(owner, o.posOpt.get).asExp)
              changed = T
            }
        }
        return AST.Transformer.PreResult(cctx, F,
          if (changed) Some(o(receiverOpt = newReceiverOpt, targs = ISZ(), args = newArgs))
          else None())
      }

      override def postExpInvoke(ctx: Z, o: AST.Exp.Invoke): AST.Transformer.TPostResult[Z, AST.Exp] = {
        if (o.receiverOpt.nonEmpty && o.targs.isEmpty && o.args.size == 1 && ops.StringOps(o.ident.id.value).isScalaOp) {
          return AST.Transformer.TPostResult(ctx, Some(AST.Exp.Binary(o.receiverOpt.get, o.ident.id.value, o.args(0),
            o.attr, o.ident.posOpt)))
        }
        return AST.Transformer.TPostResult(ctx, None())
      }

      override def preExpInvokeNamed(ctx: Z, o: AST.Exp.InvokeNamed): AST.Transformer.PreResult[Z, AST.Exp] = {
        val en = ExpNormalizer.create(th)
        var newArgs = ISZ[AST.NamedArg]()
        var cctx = ctx
        var changed = o.targs.nonEmpty
        for (arg <- o.args) {
          val p = en.transformNamedArg(cctx, arg)
          cctx = p.ctx
          p.resultOpt match {
            case Some(newArg) =>
              newArgs = newArgs :+ newArg
              changed = T
            case _ => newArgs = newArgs :+ arg
          }
        }
        var newReceiverOpt = Option.none[AST.Exp]()
        o.receiverOpt match {
          case Some(receiver) =>
            val p = en.transformExp(cctx, receiver)
            cctx = p.ctx
            if (p.resultOpt.nonEmpty) {
              newReceiverOpt = p.resultOpt
              changed = T
            } else {
              newReceiverOpt = o.receiverOpt
            }
          case _ =>
            val (cond, owner) = shouldTransformIdent(o.ident)
            if (cond) {
              newReceiverOpt = Some(th.nameToExp(owner, o.posOpt.get).asExp)
              changed = T
            }
        }
        return AST.Transformer.PreResult(cctx, F,
          if (changed) Some(o(receiverOpt = newReceiverOpt, targs = ISZ(), args = newArgs))
          else None())
      }

      @pure def shouldTransformIdent(o: AST.Exp.Ident): (B, ISZ[String]) = {
        o.attr.resOpt.get match {
          case res: AST.ResolvedInfo.Var =>
            return (res.isInObject && res.owner.nonEmpty && res.owner != AST.Typed.sireumName, res.owner)
          case res: AST.ResolvedInfo.Method =>
            return (res.isInObject && res.owner.nonEmpty && res.owner != AST.Typed.sireumName, res.owner)
          case res: AST.ResolvedInfo.EnumElement =>
            return (T, res.owner)
          case _ => return (F, ISZ())
        }
      }

      override def preExpAt(ctx: Z, o: AST.Exp.At): AST.Transformer.PreResult[Z, AST.Exp] = {
        if (o.linesFresh.nonEmpty) {
          return AST.Transformer.PreResult(ctx, T, Some(o(linesFresh = ISZ())))
        } else {
          return AST.Transformer.PreResult(ctx, T, None())
        }
      }

      override def postExpBinary(ctx: Z, o: AST.Exp.Binary): AST.Transformer.TPostResult[Z, AST.Exp] = {
        o.op match {
          case string"->:" => return AST.Transformer.TPostResult(ctx, Some(o(op = AST.Exp.BinaryOp.Imply)))
          case string"-->:" => return AST.Transformer.TPostResult(ctx, Some(o(op = AST.Exp.BinaryOp.CondImply)))
          case AST.Exp.BinaryOp.Equiv =>
            return if (th.isGroundType(o.left.typedOpt.get))
              AST.Transformer.TPostResult(ctx, Some(o(op = AST.Exp.BinaryOp.Eq, attr = o.attr(resOpt =
                Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryEq))))))
            else AST.Transformer.TPostResult(ctx, Some(o(op = AST.Exp.BinaryOp.EquivUni)))
          case AST.Exp.BinaryOp.Inequiv =>
            return if (th.isGroundType(o.left.typedOpt.get))
              AST.Transformer.TPostResult(ctx, Some(o(op = AST.Exp.BinaryOp.Ne, attr = o.attr(resOpt =
                Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryNe))))))
            else AST.Transformer.TPostResult(ctx, Some(o(op = AST.Exp.BinaryOp.InequivUni)))
          case _ => return AST.Transformer.TPostResult(ctx, None())
        }
      }

      override def postExpLabeled(ctx: Z, o: AST.Exp.Labeled): AST.Transformer.TPostResult[Z, AST.Exp] = {
        return AST.Transformer.TPostResult(ctx, Some(o.exp))
      }
    }

    @record class LocalNormalizer(var stack: Stack[HashMap[ISZ[String], String]],
                                  var fresh: Z) extends AST.MTransformer {

      val lcontext: ISZ[String] = ISZ(".l")

      def updateLocalVar(res: AST.ResolvedInfo.LocalVar): Option[AST.ResolvedInfo.LocalVar] = {
        for (i <- stack.size - 1 to 0 by -1) {
          val map = stack.elements(i)
          map.get(res.context :+ res.id) match {
            case Some(id) => return Some(res(context = lcontext, id = id))
            case _ =>
          }
        }
        return None()
      }

      override def preExpIdent(o: AST.Exp.Ident): AST.MTransformer.PreResult[AST.Exp] = {
        o.resOpt match {
          case Some(res: AST.ResolvedInfo.LocalVar) =>
            updateLocalVar(res) match {
              case Some(newRes) =>
                return AST.MTransformer.PreResult(T, MSome(
                  o(id = o.id(value = newRes.id), attr = o.attr(resOpt = Some(newRes)))))
              case _ =>
            }
          case _ =>
        }
        return AST.MTransformer.PreResultExpIdent
      }

      override def preBody(o: AST.Body): AST.MTransformer.PreResult[AST.Body] = {
        stack = stack.push(HashMap.empty)
        return AST.MTransformer.PreResultBody
      }

      override def postStmtVar(o: AST.Stmt.Var): MOption[AST.Stmt] = {
        val res = o.attr.resOpt.get.asInstanceOf[AST.ResolvedInfo.LocalVar]
        val context = res.context
        val id = s".l.$fresh"
        fresh = fresh + 1
        val (map, stack2) = stack.pop.get
        stack = stack2.push(map + (context :+ o.id.value) ~> id)
        return MSome(o(id = o.id(value = id), attr = o.attr(resOpt = Some(res(context = lcontext, id = id)))))
      }

      override def prePatternVarBinding(o: AST.Pattern.VarBinding): AST.MTransformer.PreResult[AST.Pattern] = {
        val context = o.idContext
        val id = s".l.$fresh"
        fresh = fresh + 1
        val (map, stack2) = stack.pop.get
        stack = stack2.push(map + (context :+ o.id.value) ~> id)
        return AST.MTransformer.PreResult(T, MSome(o(id = o.id(value = id))))
      }

      override def prePatternStructure(o: AST.Pattern.Structure): AST.MTransformer.PreResult[AST.Pattern] = {
        o.idOpt match {
          case Some(oid) =>
            val context: ISZ[String] = o.idContext
            val id = s".l.$fresh"
            fresh = fresh + 1
            val (map, stack2) = stack.pop.get
            stack = stack2.push(map + (context :+ oid.value) ~> id)
            return AST.MTransformer.PreResult(T, MSome(o(idOpt = Some(o.idOpt.get(value = id)))))
          case _ =>
            return AST.MTransformer.PreResultPatternStructure
        }
      }

      override def preCase(o: AST.Case): AST.MTransformer.PreResult[AST.Case] = {
        stack = stack.push(HashMap.empty)
        return AST.MTransformer.PreResultCase
      }

      override def postCase(o: AST.Case): MOption[AST.Case] = {
        stack = stack.pop.get._2
        return MNone()
      }

      override def postBody(o: AST.Body): MOption[AST.Body] = {
        stack = stack.pop.get._2
        return if (o.undecls.nonEmpty) MSome(o(undecls = ISZ())) else MNone()
      }

      override def postResolvedInfoMethod(o: AST.ResolvedInfo.Method): MOption[AST.ResolvedInfo] = {
        return MSome(o(tpeOpt = None(), reads = ISZ(), writes = ISZ()))
      }

      override def postResolvedInfoLocalVar(o: AST.ResolvedInfo.LocalVar): MOption[AST.ResolvedInfo] = {
        if (o.scope == AST.ResolvedInfo.LocalVar.Scope.Current && !o.isVal && !o.isSpec) {
          return MSome(o(scope = AST.ResolvedInfo.LocalVar.Scope.Current, isVal = F, isSpec = F))
        } else {
          return MNone()
        }
      }
    }
  }

  @datatype class LocalVarContextPrePostSubstitutor(val oldContext: ISZ[String], val newContext: ISZ[String]) extends AST.Transformer.PrePost[B] {
    @pure override def postResolvedInfoLocalVar(ctx: B, o: AST.ResolvedInfo.LocalVar): AST.Transformer.TPostResult[B, AST.ResolvedInfo] = {
      if (o.context == oldContext) {
        return AST.Transformer.TPostResult(ctx, Some(o(context = newContext)))
      }
      return AST.Transformer.TPostResult(ctx, None())
    }
  }

  @datatype class QuantTypePrePostNormalizer extends AST.Transformer.PrePost[B] {
    override def postExpQuantType(ctx: B, o: AST.Exp.QuantType): AST.Transformer.TPostResult[B, AST.Exp.Quant] = {
      o.fun.exp match {
        case AST.Stmt.Expr(body: AST.Exp.QuantType) if o.isForall == body.isForall =>
          val params = o.fun.params ++ body.fun.params
          val exp = AST.Transformer(LocalVarContextPrePostSubstitutor(body.fun.context, o.fun.context)).
            transformAssignExp(F, body.fun.exp).resultOpt.getOrElse(body.fun.exp)
          val funType = o.fun.typedOpt.get.asInstanceOf[AST.Typed.Fun]
          val typedOpt: Option[AST.Typed] = Some(funType(args = funType.args ++ body.fun.typedOpt.get.asInstanceOf[AST.Typed.Fun].args))
          val newO = o(fun = o.fun(params = params, exp = exp, attr = o.fun.attr(typedOpt = typedOpt)))
          return AST.Transformer.TPostResult(ctx, Some(newO))
        case _ =>
          return AST.Transformer.TPostResult(ctx, None())
      }
    }

    override def postExpQuantRange(ctx: B, o: AST.Exp.QuantRange): AST.Transformer.TPostResult[B, AST.Exp.Quant] = {
      val exp: AST.Exp = o.fun.exp match {
        case stmt: AST.Stmt.Expr => stmt.exp
        case _ => return AST.Transformer.TPostResult(ctx, None())
      }
      val param = o.fun.params(0)
      val id = param.idOpt.get
      val ident = AST.Exp.Ident(id, AST.ResolvedAttr(id.attr.posOpt, Some(
        AST.ResolvedInfo.LocalVar(o.fun.context, AST.ResolvedInfo.LocalVar.Scope.Current, F, T, id.value)),
        param.typedOpt))
      val loCond = AST.Exp.Binary(o.lo, AST.Exp.BinaryOp.Le, ident, AST.ResolvedAttr(o.lo.posOpt, Some(
        AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryLe)), o.lo.typedOpt), o.lo.posOpt)
      val hiCond = AST.Exp.Binary(ident, if (o.hiExact) AST.Exp.BinaryOp.Le else AST.Exp.BinaryOp.Lt, o.hi,
        AST.ResolvedAttr(o.lo.posOpt, Some(AST.ResolvedInfo.BuiltIn(
          if (o.hiExact) AST.ResolvedInfo.BuiltIn.Kind.BinaryLe else AST.ResolvedInfo.BuiltIn.Kind.BinaryLt)),
          o.hi.typedOpt), o.lo.posOpt)
      val cond = AST.Exp.Binary(loCond, AST.Exp.BinaryOp.And, hiCond, AST.ResolvedAttr(
        o.posOpt, Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryAnd)), AST.Typed.bOpt
      ), o.posOpt)
      val fun = AST.Exp.Fun(o.fun.context, o.fun.params, AST.Stmt.Expr(
        AST.Exp.Binary(cond, AST.Exp.BinaryOp.CondImply, exp, AST.ResolvedAttr(o.posOpt,
          Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryCondImply)), AST.Typed.bOpt), o.posOpt),
        AST.TypedAttr(o.posOpt, AST.Typed.bOpt)
      ), AST.TypedAttr(o.attr.posOpt, o.attr.typedOpt))
      val newQuant = AST.Exp.QuantType(o.isForall, fun, AST.Attr(o.posOpt))
      val r = postExpQuantType(ctx, newQuant)
      return if (r.resultOpt.isEmpty) AST.Transformer.TPostResult(ctx, Some(newQuant)) else r
    }
  }

  @datatype class InductResult(val isClosed: B,
                               val cases: ISZ[InductResult.Case])

  object InductResult {
    @datatype class Case(val pattern: AST.Pattern, val premises: ISZ[AST.Exp])
  }

  val basicTypes: HashSet[AST.Typed] = HashSet ++ ISZ[AST.Typed](
    AST.Typed.b,
    AST.Typed.z,
    AST.Typed.c,
    AST.Typed.f32,
    AST.Typed.f64,
    AST.Typed.r,
    AST.Typed.string
  )

  @pure def typedInfo(info: TypeInfo): AST.Typed.Name = {
    @pure def typedParam(tp: AST.TypeParam): AST.Typed = {
      return AST.Typed.Name(ISZ(tp.id.value), ISZ())
    }

    info match {
      case info: TypeInfo.SubZ => return AST.Typed.Name(info.name, ISZ())
      case info: TypeInfo.Enum => return AST.Typed.Name(info.name, ISZ())
      case info: TypeInfo.Sig =>
        val args = info.ast.typeParams.map(typedParam _)
        return AST.Typed.Name(info.name, args)
      case info: TypeInfo.Adt =>
        val args = info.ast.typeParams.map(typedParam _)
        return AST.Typed.Name(info.name, args)
      case info: TypeInfo.TypeAlias =>
        val args = info.ast.typeParams.map(typedParam _)
        return AST.Typed.Name(info.name, args)
      case _: TypeInfo.TypeVar => halt("Infeasible")
    }
  }

  def build(force: B, init: TypeHierarchy, reporter: Reporter): TypeHierarchy = {
    val typeMap = init.typeMap

    def resolveType(scope: Scope, t: AST.Type): AST.Typed = {
      t match {
        case t: AST.Type.Named =>
          var name = AST.Util.ids2strings(t.name.ids)
          scope.resolveType(typeMap, name) match {
            case Some(ti) => name = ti.name
            case _ =>
              reporter.error(
                t.name.attr.posOpt,
                resolverKind,
                st"Could not resolve type named '${(name, ".")}'.".render
              )
          }
          val args: ISZ[AST.Typed] = {
            var as = ISZ[AST.Typed]()
            for (ta <- t.typeArgs) {
              val tas = resolveType(scope, ta)
              as = as :+ tas
            }
            as
          }
          return AST.Typed.Name(name, args)
        case t: AST.Type.Tuple =>
          val ts: ISZ[AST.Typed] = {
            var as = ISZ[AST.Typed]()
            for (a <- t.args) {
              val ta = resolveType(scope, a)
              as = as :+ ta
            }
            as
          }
          return AST.Typed.Tuple(ts)
        case t: AST.Type.Fun =>
          val ts: ISZ[AST.Typed] = {
            var as = ISZ[AST.Typed]()
            for (a <- t.args) {
              val ta = resolveType(scope, a)
              as = as :+ ta
            }
            as
          }
          val rt = resolveType(scope, t.ret)
          return AST.Typed.Fun(if (t.isPure || rt.isPureFun) AST.Purity.Pure else AST.Purity.Impure, t.isByName, ts, rt)
      }
    }

    def resolveTypeNameds(posOpt: Option[Position], scope: Scope, ts: ISZ[AST.Type.Named]): ISZ[AST.Typed.Name] = {
      var r = ISZ[AST.Typed.Name]()
      for (t <- ts) {
        val typed = resolveType(scope, t)
        typed match {
          case typed: AST.Typed.Name => r = r :+ typed
          case _ => reporter.error(posOpt, resolverKind, "Expected a named type.")
        }
      }
      return r
    }

    val zName: QName = ISZ("org", "sireum", "Z")
    var r = init

    def resolveAlias(info: TypeInfo.TypeAlias, seen: HashSet[QName]): AST.Typed = {
      if (seen.contains(info.name)) {
        reporter.error(info.posOpt, resolverKind, st"Type alias ${(info.name, ".")} is cyclic.".render)
        return AST.Typed.Name(info.name, ISZ())
      }
      val typed = typedInfo(info)
      r.aliases.get(typed.ids) match {
        case Some(rt) => return rt
        case _ =>
      }
      val scope = typeParamsScope(info.ast.typeParams, info.scope, reporter)
      val t = resolveType(scope, info.ast.tipe)
      t match {
        case t: AST.Typed.Name =>
          typeMap.get(t.ids) match {
            case Some(ti: TypeInfo.TypeAlias) => resolveAlias(ti, seen + info.name)
            case _ =>
          }
        case _ =>
      }
      r = r(aliases = r.aliases + typed.ids ~> t)
      return t
    }

    if (!typeMap.contains(zName)) {
      reporter.error(None(), resolverKind, "Could not find Z type.")
      return r
    }
    for (info <- typeMap.values) {
      info match {
        case _: TypeInfo.SubZ =>
          val typed = typedInfo(info)
          r = r(poset = r.poset.addNode(typed.ids))
        case _: TypeInfo.Enum =>
          val typed = typedInfo(info)
          r = r(poset = r.poset.addNode(typed.ids))
        case info: TypeInfo.Sig =>
          if (!info.outlined || force) {
            val typed = typedInfo(info)
            val scope = typeParamsScope(info.ast.typeParams, info.scope, reporter)
            val parents = resolveTypeNameds(info.posOpt, scope, info.ast.parents)
            var parentTypeNames = ISZ[QName]()
            for (p <- parents) {
              parentTypeNames = parentTypeNames :+ p.ids
            }
            r = r(poset = r.poset.addParents(typed.ids, parentTypeNames))
          }
        case info: TypeInfo.Adt =>
          if (!info.outlined || force) {
            val typed = typedInfo(info)
            val scope = typeParamsScope(info.ast.typeParams, info.scope, reporter)
            val parents = resolveTypeNameds(info.posOpt, scope, info.ast.parents)
            var parentTypeNames = ISZ[QName]()
            for (p <- parents) {
              parentTypeNames = parentTypeNames :+ p.ids
            }
            r = r(poset = r.poset.addParents(typed.ids, parentTypeNames))
          }
        case info: TypeInfo.TypeAlias => resolveAlias(info, HashSet.empty)
        case _: TypeInfo.TypeVar => halt("Infeasible")
      }
    }
    if (reporter.hasError) {
      return r
    }
    r.checkCyclic(reporter)
    return r
  }

  @pure def combine(
    r: (TypeHierarchy, ISZ[Message]),
    f: TypeHierarchy => (TypeHierarchy, ISZ[Message]) @pure
  ): (TypeHierarchy, ISZ[Message]) = {
    val p = f(r._1)
    return (p._1, r._2 ++ p._2)
  }

  @strictpure def empty: TypeHierarchy = TypeHierarchy(HashSMap.empty, HashSMap.empty, Poset.empty, HashSMap.empty)

}

@datatype class TypeHierarchy(
  val nameMap: NameMap,
  val typeMap: TypeMap,
  val poset: Poset[QName],
  val aliases: HashSMap[QName, AST.Typed]
) {

  @memoize def fingerprintKeepMethodBody: U64 = {
    return fingerprint(T)
  }
  @memoize def fingerprintNoMethodBody: U64 = {
    return fingerprint(F)
  }

  @pure def fingerprint(keepMethodBody: B): U64 = {
    var globalScopes = HashSMap.empty[Scope.Global, Z]
    def getScopeNum(scope: Scope): Z = {
      scope match {
        case scope: Scope.Global =>
          globalScopes.get(scope) match {
            case Some(num) => return num
            case _ =>
              globalScopes = globalScopes + scope ~> globalScopes.size
              return globalScopes.size - 1
          }
        case scope: Scope.Local => return getScopeNum(scope.outerOpt.get)
      }
    }
    var sts = ISZ[ST]()
    for (p <- nameMap.entries) {
      p._2 match {
        case info: Info.Enum => sts = sts :+ st"Enum(${(info.name, ".")}, ${info.elements.keys})"
        case info: Info.Inv => sts = sts :+ st"Inv(${(info.owner, ".")}, ${getScopeNum(info.scope)}, ${info.ast.prettyST})"
        case info: Info.Var => sts = sts :+ st"Var(${(info.owner, ".")}, ${getScopeNum(info.scope)}, ${info.ast(initOpt = None()).prettyST})"
        case info: Info.SpecVar => sts = sts :+ st"SpecVar(${(info.owner, ".")}, ${getScopeNum(info.scope)}, ${info.ast.prettyST})"
        case info: Info.RsVal => sts = sts :+ st"RsVal(${(info.owner, ".")}, ${getScopeNum(info.scope)}, ${info.ast.prettyST})"
        case info: Info.Method => sts = sts :+ st"Method(${(info.owner, ".")}, ${getScopeNum(info.scope)}, ${info.ast(mcontract = info.ast.contract, bodyOpt = if (keepMethodBody || info.ast.hasInline || (info.ast.purity == lang.ast.Purity.StrictPure && info.ast.mcontract.isEmpty)) info.ast.bodyOpt else None()).prettyST})"
        case info: Info.ExtMethod => sts = sts :+ st"ExtMethod(${(info.owner, ".")}, ${getScopeNum(info.scope)}, ${info.ast.prettyST})"
        case info: Info.Fact => sts = sts :+ st"Fact(${(info.owner, ".")}, ${getScopeNum(info.scope)}, ${info.ast.prettyST})"
        case info: Info.EnumElement => sts = sts :+ st"EnumElement(${(info.owner, ".")}, ${info.id})"
        case info: Info.JustMethod => sts = sts :+ st"JustMethod(${(info.owner, ".")}, ${getScopeNum(info.scope)}, ${info.ast.prettyST})"
        case info: Info.LocalVar => sts = sts :+ st"LocalVar(${(info.name, ".")}, ${info.ast.prettyST})"
        case info: Info.SpecMethod => sts = sts :+ st"SpecMethod(${(info.owner, ".")}, ${getScopeNum(info.scope)}, ${info.ast.prettyST})"
        case info: Info.Theorem => sts = sts :+ st"Theorem(${(info.owner, ".")}, ${getScopeNum(info.scope)}, ${info.ast.prettyST})"
        case _: Info.Package => // skip
        case _: Info.Object => // skip
      }
    }
    for (p <- typeMap.entries) {
      p._2 match {
        case info: TypeInfo.Adt =>
          val rd: String = if (info.ast.isDatatype) "@datatype " else "@record "
          val root: String = if (info.ast.isRoot) "trait" else "class"
          val param: Option[ST] = if (info.ast.params.isEmpty) None() else Some(st"(${(for (p <- info.ast.params) yield p.prettyST, ", ")})")
          val extend: Option[ST] =
            if (info.ast.parents.isEmpty) None() else Some(st" extends ${(for (n <- info.ast.parents) yield n.prettyST, ", ")}")
          val varsST: ST = if (info.vars.isEmpty) st"Vars()" else
            st"""Vars(
                |  ${(for (iv <- info.vars.entries) yield st"(${iv._1}, ${iv._2.ast(initOpt = None()).prettyST})", ", ")})"""
          val methodsST: ST = if (info.methods.isEmpty) st"Methods()" else
            st"""Methods(
                |  ${(for (iv <- info.methods.entries) yield st"(${iv._1}, ${iv._2.ast(mcontract = iv._2.ast.contract, bodyOpt = if (keepMethodBody || (iv._2.ast.purity == lang.ast.Purity.StrictPure && iv._2.ast.mcontract.isEmpty)) iv._2.ast.bodyOpt else None()).prettyST})", ", ")})"""
          val specVarsST: ST = if (info.specVars.isEmpty) st"SpecVars()" else
            st"""SpecVars(
                |  ${(for (iv <- info.specVars.entries) yield st"(${iv._1}, ${iv._2.ast.prettyST})", ", ")})"""
          val specMethodsST: ST = if (info.specMethods.isEmpty) st"SpecMethods()" else
            st"""SpecMethods(
                |  ${(for (iv <- info.specMethods.entries) yield st"(${iv._1}, ${iv._2.ast.prettyST})", ", ")})"""
          val invsST: ST = if (info.invariants.isEmpty) st"Invs()" else
            st"""Invs(
                |  ${(for (inv <- info.invariants.values) yield inv.ast.prettyST, ", ")})"""
          val refinementST: ST = if (info.refinements.isEmpty) st"Refinements()" else
            st"""Refinements(
                |  ${(for (r <- info.refinements.values) yield st"${(r.ids, ".")}", ", ")})"""
          val dataRefinementsST: ST = if (info.dataRefinements.isEmpty) st"DataRefinements()" else
            st"""DataRefinements(
                |  ${(for (r <- info.dataRefinements) yield r.prettyST, ", ")})"""
          sts = sts :+ st"Adt(${(info.owner, ".")}, ${getScopeNum(info.scope)}, $rd $root ${info.ast.id.prettyST}${lang.ast.TypeParam.stOpt(info.ast.typeParams)}$param$extend, $varsST, $methodsST, $specVarsST, $specMethodsST, $invsST, $refinementST, $dataRefinementsST)"
        case info: TypeInfo.Sig =>
          val ext: String = if (info.ast.isExt) "@ext " else ""
          val sig: String = if (info.ast.isImmutable) "@sig " else "@msig "
          val seal: String = if (info.ast.isSealed) " sealed" else ""
          val extend: Option[ST] =
            if (info.ast.parents.isEmpty) None() else Some(st" extends ${(for (n <- info.ast.parents) yield n.prettyST, ", ")}")
          val methodsST: ST = if (info.methods.isEmpty) st"Methods()" else
            st"""Methods(
                |  ${(for (iv <- info.methods.entries) yield st"(${iv._1}, ${iv._2.ast(bodyOpt = if (iv._2.ast.purity == lang.ast.Purity.StrictPure && iv._2.ast.mcontract.isEmpty) iv._2.ast.bodyOpt else None()).prettyST})", ", ")})"""
          val specVarsST: ST = if (info.specVars.isEmpty) st"SpecVars()" else
            st"""SpecVars(
                |  ${(for (iv <- info.specVars.entries) yield st"(${iv._1}, ${iv._2.ast.prettyST})", ", ")})"""
          val specMethodsST: ST = if (info.specMethods.isEmpty) st"SpecMethods()" else
            st"""SpecMethods(
                |  ${(for (iv <- info.specMethods.entries) yield st"(${iv._1}, ${iv._2.ast.prettyST})", ", ")})"""
          val invsST: ST = if (info.invariants.isEmpty) st"Invs()" else
            st"""Invs(
                |  ${(for (inv <- info.invariants.values) yield inv.ast.prettyST, ", ")})"""
          val refinementST: ST = if (info.refinements.isEmpty) st"Refinements()" else
            st"""Refinements(
                |  ${(for (r <- info.refinements.values) yield st"${(r.ids, ".")}", ", ")})"""
          val dataRefinementsST: ST = if (info.dataRefinements.isEmpty) st"DataRefinements()" else
            st"""DataRefinements(
                |  ${(for (r <- info.dataRefinements) yield r.prettyST, ", ")})"""
          sts = sts :+ st"Sig(${(info.owner, ".")}, ${getScopeNum(info.scope)}, $ext$sig$seal trait ${info.ast.id.prettyST}${lang.ast.TypeParam.stOpt(info.ast.typeParams)}$extend, $methodsST, $specVarsST, $specMethodsST, $invsST, $refinementST, $dataRefinementsST)"
        case info: TypeInfo.SubZ => sts = sts :+ st"SubZ(${(info.owner, ".")}, ${info.ast.prettyST})"
        case info: TypeInfo.Enum => sts = sts :+ st"TEnum(${(info.name, ".")}, ${info.elements.keys})"
        case info: TypeInfo.TypeVar => sts = sts :+ st"TypeVar(${info.ast.prettyST})"
        case info: TypeInfo.TypeAlias => sts = sts :+ st"TypeAlias(${(info.name, ".")}, ${getScopeNum(info.scope)}, ${info.ast.prettyST})"
      }
    }

    for (scope <- globalScopes.keys) {
      sts = sts :+ st"Scope(${(scope.packageName, ".")}, Imports(${(for (impor <- scope.imports) yield impor.prettyST, ", ")}), ${(scope.enclosingName, ".")})"
    }

    val rep =
      st"""TypeHierarchy(
          |  ${(sts, ",\n")}
          |)""".render

    return ops.StringOps(rep).sha3U64(T, T)
  }

  @pure def rootTypeNames(): ISZ[QName] = {
    return poset.rootNodes
  }

  @pure def lub(ts: ISZ[AST.Typed]): Option[AST.Typed] = {
    val types: ISZ[AST.Typed] =
      for (t <- ts if AST.Typed.nothing != t) yield t
    types.size match {
      case z"0" => return if (ts.isEmpty) None() else Some(ts(0))
      case z"1" => return Some(types(0))
      case _ =>
    }

    var typeNames = ISZ[AST.Typed.Name]()
    val first = types(0)
    var i = 1
    val size = types.size
    while (i < size) {
      types(i) match {
        case t: AST.Typed.Name =>
          typeNames = typeNames :+ t
        case t =>
          if (first != t) {
            return None()
          }
      }
      i = i + 1
    }

    if (typeNames.size != z"0" && typeNames.size + 1 != types.size) {
      return None()
    }

    if (typeNames.size < 2) {
      return Some(first)
    }

    val tns = typeNames.map((tn: AST.Typed.Name) => tn.ids)
    poset.lub(tns) match {
      case Some(lub) =>
        val tn = typeNames(0)
        val ancestors: ISZ[AST.Typed.Name] = typeMap.get(tn.ids) match {
          case Some(info: TypeInfo.Sig) => info.ancestors
          case Some(info: TypeInfo.Adt) => info.ancestors
          case _ => halt(st"Unexpected situation while computing the least upper bound of { '${(ts, "', '")}' }.".render)
        }
        var commonType = tn
        var found = F
        for (ancestor <- ancestors if !found) {
          if (ancestor.ids == lub) {
            commonType = ancestor
            found = T
          }
        }
        for (typeName <- typeNames) {
          if (!isSubType(typeName, commonType)) {
            return None()
          }
        }
        return Some(commonType)
      case _ => return None()
    }
  }

  @pure def glb(ts: ISZ[AST.Typed]): Option[AST.Typed] = {
    val types: ISZ[AST.Typed] =
      for (t <- ts if AST.Typed.nothing != t) yield t
    types.size match {
      case z"0" => return if (ts.isEmpty) None() else Some(ts(0))
      case z"1" => return Some(types(0))
      case _ =>
    }

    var typeNames = ISZ[AST.Typed.Name]()
    val first = types(0)
    var i = 1
    val size = types.size
    while (i < size) {
      types(i) match {
        case t: AST.Typed.Name =>
          typeNames = typeNames :+ t
        case t =>
          if (first != t) {
            return None()
          }
      }
      i = i + 1
    }

    if (typeNames.size != z"0" && typeNames.size + 1 != types.size) {
      return None()
    }

    if (typeNames.size < 2) {
      return Some(first)
    }

    val tns = typeNames.map((tn: AST.Typed.Name) => tn.ids)
    poset.glb(tns) match {
      case Some(glb) =>
        val (tpe, ancestors): (AST.Typed, HashSet[AST.Typed.Name]) = typeMap.get(glb) match {
          case Some(info: TypeInfo.Sig) => (info.tpe, HashSet.empty[AST.Typed.Name] ++ info.ancestors)
          case Some(info: TypeInfo.Adt) => (info.tpe, HashSet.empty[AST.Typed.Name] ++ info.ancestors)
          case _ => halt(s"Unexpected situation while computing the greatest lower bound of { '${(ts, "', '")}' }.")
        }
        for (t <- typeNames) {
          if (!ancestors.contains(t)) {
            return None()
          }
        }
        return Some(tpe)
      case _ => return None()
    }
  }

  @memoize def worksheetInvs: ISZ[Info.Inv] = {
    var r = ISZ[Info.Inv]()
    for (info <- nameMap.values) {
      info match {
        case info: Info.Inv if info.owner.isEmpty => r = r :+ info
        case _ =>
      }
    }
    return r
  }

  def dealiasInit(posOpt: Option[Position], t: AST.Typed.Name, reporter: Reporter): Option[AST.Typed.Name] = {
    aliases.get(t.ids) match {
      case Some(t2: AST.Typed.Name) =>
        val r = dealiasInit(posOpt, t2, reporter)
        return r
      case Some(_) =>
        reporter
          .error(posOpt, resolverKind, st"Expected a named type in type hiearchy but ${(t.ids, ".")} is not.".render)
        return None()
      case _ => return Some(t)
    }
  }

  def checkCyclic(reporter: Reporter): Unit = {
    var cache = HashSMap.empty[Poset.Index, HashSSet[Poset.Index]]
    for (t <- rootTypeNames()) {
      val n = poset.nodes.get(t).get
      val r = Poset.Internal.descendantsCache(poset, n, cache)
      cache = r._2
    }
    for (kv <- cache.entries) {
      val k = kv._1
      val v = kv._2
      if (v.contains(k)) {
        reporter
          .error(None(), resolverKind, st"Cyclic type hierarchy involving ${(poset.nodesInverse(k), ".")}.".render)
      }
    }
  }

  def checkTyped(posOpt: Option[Position], t: AST.Typed, reporter: Reporter): Unit = {
    if (t == AST.Typed.nothing) {
      reporter.error(posOpt, TypeChecker.typeCheckerKind, s"Cannot use type '$t'.")
    } else {
      val (tps, targs): (ISZ[AST.TypeParam], ISZ[AST.Typed]) = t match {
        case t: AST.Typed.Name =>
          if (t.args.isEmpty) {
            return
          }
          typeMap.get(t.ids).get match {
            case info: TypeInfo.Adt => (info.ast.typeParams, t.args)
            case info: TypeInfo.Sig => (info.ast.typeParams, t.args)
            case _ => (ISZ(), ISZ())
          }
        case _ => (ISZ(), ISZ())
      }
      if (tps.nonEmpty) {
        val tvs: ISZ[AST.Typed] = for (tp <- tps) yield AST.Typed.TypeVar(tp.id.value, tp.kind).asInstanceOf[AST.Typed]
        TypeChecker.unifies(this, posOpt, TypeChecker.TypeRelation.Equal, targs, tvs, reporter)
      }
    }
  }

  def typed(scope: Scope, typ: AST.Type, reporter: Reporter): Option[AST.Type] = {
    return typedWithStepId(F, scope, typ, reporter)
  }

  def typedWithStepId(allowStepId: B, scope: Scope, typ: AST.Type, reporter: Reporter): Option[AST.Type] = {
    def typedH(tipe: AST.Type): Option[AST.Type] = {
      tipe match {
        case tipe: AST.Type.Named =>
          var newTypeArgs = ISZ[AST.Type]()
          var argTypes = ISZ[AST.Typed]()
          var hasError = F
          for (arg <- tipe.typeArgs) {
            val targOpt = typedH(arg)
            targOpt match {
              case Some(targ) if targ.typedOpt.nonEmpty =>
                newTypeArgs = newTypeArgs :+ targ
                argTypes = argTypes :+ targ.typedOpt.get
              case _ => hasError = T
            }
          }
          if (hasError) {
            return None[AST.Type]()
          }
          val name = AST.Util.ids2strings(tipe.name.ids)
          scope.resolveType(typeMap, name) match {
            case Some(ti: TypeInfo.TypeAlias) =>
              val tparamSize = ti.ast.typeParams.size
              if (newTypeArgs.size != tparamSize) {
                reporter.error(
                  tipe.posOpt,
                  TypeChecker.typeCheckerKind,
                  st"Type alias ${(name, ".")} requires $tparamSize type arguments but ${newTypeArgs.size} supplied.".render
                )
                return None()
              }
              val tpe = dealias(AST.Typed.Name(ti.name, argTypes), tipe.posOpt, reporter)
              checkTyped(tipe.posOpt, tpe, reporter)
              return Some(tipe(typeArgs = newTypeArgs, attr = tipe.attr(typedOpt = Some(tpe))))
            case Some(ti: TypeInfo.TypeVar) =>
              if (newTypeArgs.nonEmpty) {
                reporter.error(
                  tipe.posOpt,
                  TypeChecker.typeCheckerKind,
                  s"Type variable ${name(0)} does not accept type arguments."
                )
                return None()
              }
              return Some(tipe(attr = tipe.attr(typedOpt = Some(AST.Typed.TypeVar(ti.name(0), ti.ast.kind)))))
            case Some(ti) =>
              val p: (String, Z, ISZ[String]) = ti match {
                case ti: TypeInfo.SubZ => (if (ti.ast.isBitVector) "@bits" else "@range", 0, ti.name)
                case _: TypeInfo.Enum => ("@enum", 0, ti.name)
                case ti: TypeInfo.Sig =>
                  (
                    if (ti.ast.isExt) "@ext" else if (ti.ast.isImmutable) "@sig" else "@msig",
                    ti.ast.typeParams.size,
                    ti.name
                  )
                case ti: TypeInfo.Adt =>
                  (if (ti.ast.isDatatype) "@datatype" else "@record", ti.ast.typeParams.size, ti.name)
                case _ => halt("Infeasible")
              }
              if (newTypeArgs.size != p._2) {
                if (p._2 == z"0") {
                  reporter.error(
                    tipe.posOpt,
                    TypeChecker.typeCheckerKind,
                    st"Slang ${p._1} ${(name, ".")} does not accept type arguments.".render
                  )
                } else {
                  reporter.error(
                    tipe.posOpt,
                    TypeChecker.typeCheckerKind,
                    st"Slang ${p._1} ${(name, ".")} requires ${p._2} type arguments but ${newTypeArgs.size} is supplied.".render
                  )
                }
                return None()
              }
              val t = AST.Typed.Name(p._3, argTypes)
              checkTyped(tipe.posOpt, t, reporter)
              return Some(tipe(typeArgs = newTypeArgs, attr = tipe.attr(typedOpt = Some(t))))
            case _ =>
              reporter
                .error(tipe.posOpt, TypeChecker.typeCheckerKind, st"Could not find a type named ${(name, ".")}.".render)
              return None()
          }
        case tipe: AST.Type.Tuple =>
          var newArgs = ISZ[AST.Type]()
          var esTypes = ISZ[AST.Typed]()
          var hasError = F
          for (arg <- tipe.args) {
            val targOpt = typedH(arg)
            targOpt match {
              case Some(targ) if targ.typedOpt.nonEmpty =>
                newArgs = newArgs :+ targ
                esTypes = esTypes :+ targ.typedOpt.get
              case _ => hasError = T
            }
          }
          if (hasError) {
            return None[AST.Type]()
          } else {
            return Some(tipe(args = newArgs, attr = tipe.attr(typedOpt = Some(AST.Typed.Tuple(esTypes)))))
          }
        case tipe: AST.Type.Fun =>
          var paramTypes = ISZ[AST.Typed]()
          var newArgs = ISZ[AST.Type]()
          var hasError = F
          for (arg <- tipe.args) {
            val targOpt = typedH(arg)
            targOpt match {
              case Some(targ) if targ.typedOpt.nonEmpty =>
                newArgs = newArgs :+ targ
                paramTypes = paramTypes :+ targ.typedOpt.get
              case _ => hasError = T
            }
          }
          val newRetOpt = typedH(tipe.ret)
          newRetOpt match {
            case Some(newRet) if !hasError && newRet.typedOpt.nonEmpty =>
              val retType = newRet.typedOpt.get
              return Some(
                tipe(
                  args = newArgs,
                  ret = newRet,
                  attr = tipe.attr(
                    typedOpt = Some(AST.Typed.Fun(if (tipe.isPure || retType.isPureFun) AST.Purity.Pure else AST.Purity.Impure, tipe.isByName, paramTypes, retType))
                  )
                )
              )
            case _ => return None[AST.Type]()
          }
      }
    }
    val r = typedH(typ)
    r match {
      case Some(t) if t.typedOpt == Some(AST.Typed.stepId.asInstanceOf[AST.Typed]) && !allowStepId =>
        reporter.error(typ.posOpt, TypeChecker.typeCheckerKind, "Can only use StepId in @just methods")
      case _ =>
    }
    return r
  }

  def applyTypeAlias(
    typed: AST.Typed.Name,
    ti: TypeInfo.TypeAlias,
    posOpt: Option[Position],
    reporter: Reporter
  ): Option[AST.Typed] = {
    val tipeOpt: Option[AST.Type] = if (!ti.outlined) {
      val tm = typeParamMap(ti.ast.typeParams, reporter)
      val scope = Scope.Local.create(tm.map, ti.scope)
      this.typed(scope, ti.ast.tipe, reporter)
    } else {
      Some(ti.ast.tipe)
    }
    tipeOpt match {
      case Some(tipe) if tipe.typedOpt.nonEmpty =>
        val substMapOpt = TypeChecker.buildTypeSubstMap(ti.name, posOpt, ti.ast.typeParams, typed.args, reporter)
        substMapOpt match {
          case Some(substMap) => return Some(tipe.typedOpt.get.subst(substMap))
          case _ => return None()
        }
      case _ => return None()
    }
  }

  def dealias(typed: AST.Typed, posOpt: Option[Position], reporter: Reporter): AST.Typed = {
    def dealiasOpt(t: AST.Typed): Option[AST.Typed] = {
      t match {
        case t: AST.Typed.Name =>
          var newArgs = ISZ[AST.Typed]()
          var changed = F
          for (arg <- t.args) {
            val newArgOpt = dealiasOpt(arg)
            newArgOpt match {
              case Some(newArg) =>
                newArgs = newArgs :+ newArg
                changed = T
              case _ => newArgs = newArgs :+ arg
            }
          }
          val typed2: AST.Typed.Name = if (changed) t(args = newArgs) else t
          typeMap.get(t.ids) match {
            case Some(ti: TypeInfo.TypeAlias) =>
              val rOpt = applyTypeAlias(typed2, ti, posOpt, reporter)
              rOpt match {
                case Some(r) =>
                  val r2 = dealias(r, posOpt, reporter)
                  return Some(r2)
                case _ => return None()
              }
            case _ =>
              return if (changed) Some(typed2) else None()
          }
        case t: AST.Typed.Tuple =>
          var newArgs = ISZ[AST.Typed]()
          var changed = F
          for (arg <- t.args) {
            val newArgOpt = dealiasOpt(arg)
            newArgOpt match {
              case Some(newArg) =>
                newArgs = newArgs :+ newArg
                changed = T
              case _ =>
                newArgs = newArgs :+ arg
            }
          }
          return if (changed) Some(t(args = newArgs)) else None()
        case t: AST.Typed.Fun =>
          var newArgs = ISZ[AST.Typed]()
          var changed = F
          for (arg <- t.args) {
            val newArgOpt = dealiasOpt(arg)
            newArgOpt match {
              case Some(newArg) =>
                newArgs = newArgs :+ newArg
                changed = T
              case _ =>
                newArgs = newArgs :+ arg
            }
          }
          val newRetOpt = dealiasOpt(t.ret)
          val newRet = newRetOpt.getOrElse(t.ret)
          changed = changed || newRetOpt.nonEmpty
          return if (changed) Some(t(args = newArgs, ret = newRet)) else None()
        case _: AST.Typed.TypeVar => return None()
        case _: AST.Typed.Enum => return None()
        case _: AST.Typed.Method => return None()
        case _: AST.Typed.Methods => return None()
        case _: AST.Typed.Object => return None()
        case _: AST.Typed.Package => return None()
        case _: AST.Typed.Fact => return None()
        case _: AST.Typed.Theorem => return None()
        case _: AST.Typed.Inv => return None()
      }
    }

    val rOpt = dealiasOpt(typed)
    return rOpt.getOrElse(typed)
  }

  @pure def isRefinement(t1: AST.Typed, t2: AST.Typed): B = {
    (t1, t2) match {
      case (t1: AST.Typed.Fun, t2: AST.Typed.Fun) =>
        if (t1.args.size != t2.args.size) {
          return F
        }
        for (i <- z"0" until t1.args.size) {
          if (!isSubType(t2.args(i), t1.args(i))) {
            return F
          }
        }
        return isSubType(t1.ret, t2.ret)
      case (t1: AST.Typed.Method, t2: AST.Typed.Method) =>
        return isRefinement(t1.tpe, t2.tpe)
      case _ => return isSubType(t1, t2)
    }
  }

  @pure def isSubType(t1: AST.Typed, t2: AST.Typed): B = {
    if (AST.Typed.nothing == t1 || t1 == t2) {
      return T
    }
    (t1, t2) match {
      case (t1: AST.Typed.Name, t2: AST.Typed.Name) =>
        if (t2.ids == AST.Typed.stepIdName && (t1.ids == AST.Typed.zName || t1.ids == AST.Typed.stringName)) {
          return T
        }

        @pure def buildSm(tps: ISZ[AST.TypeParam]): HashMap[String, AST.Typed] = {
          var i = 0
          var sm = HashMap.emptyInit[String, AST.Typed](t1.args.size)
          for (tp <- tps) {
            sm = sm + tp.id.value ~> t1.args(i)
            i = i + 1
          }
          return sm
        }
        if (!poset.ancestorsOf(t1.ids).contains(t2.ids)) {
          return F
        } else if (t2.args.isEmpty) {
          return T
        }
        val (outlined, ancestors, substMap): (B, ISZ[AST.Typed.Name], HashMap[String, AST.Typed]) =
          typeMap.get(t1.ids) match {
            case Some(info: TypeInfo.Sig) => (info.outlined, info.ancestors, buildSm(info.ast.typeParams))
            case Some(info: TypeInfo.Adt) => (info.outlined, info.ancestors, buildSm(info.ast.typeParams))
            case _ => return F
          }
        if (!outlined) {
          return T
        }
        for (ancestor <- ancestors if ancestor.ids == t2.ids && ancestor.subst(substMap) == t2) {
          return T
        }
        return F
      case (t1: AST.Typed.Fun, t2: AST.Typed.Fun) =>
        if (t1.args.size != t2.args.size) {
          return F
        }
        for (i <- 0 until t1.args.size) {
          if (!isSubType(t2.args(i), t1.args(i))) {
            return F
          }
        }
        if (t1.isByName == t2.isByName && isSubType(t1.ret, t2.ret)) {
          (t1.isPureFun, t2.isPureFun) match {
            case (F, T) => return F
            case (_, _) => return T
          }
        } else {
          return F
        }
      case _ => return t1 == t2
    }
  }

  @pure def isCompatible(t1: AST.Typed, t2: AST.Typed): B = {
    return isSubType(t1, t2) || isSubType(t2, t1)
  }

  @pure def isMutable(t: AST.Typed): B = {
    t match {
      case t: AST.Typed.Name =>
        if (t.ids == AST.Typed.msName) {
          return T
        }
        typeMap.get(t.ids) match {
          case Some(info) =>
            info match {
              case info: TypeInfo.Adt => return !info.ast.isDatatype
              case info: TypeInfo.Sig => return !info.ast.isImmutable && !info.ast.isExt
              case _ => return F
            }
          case _ => return F
        }
      case t: AST.Typed.Tuple =>
        for (arg <- t.args) {
          if (isMutable(arg)) {
            return T
          }
        }
        return F
      case t: AST.Typed.TypeVar => return !t.isImmutable
      case _ => return F
    }
  }

  @pure def isModifiable(t: AST.Typed): B = {
    t match {
      case t: AST.Typed.Name =>
        if (t.ids == AST.Typed.msName) {
          return T
        }
        typeMap.get(t.ids) match {
          case Some(info) =>
            info match {
              case info: TypeInfo.Adt =>
                if (info.ast.isDatatype) {
                  return info.specVars.nonEmpty
                } else {
                  return T
                }
              case info: TypeInfo.Sig =>
                if (info.ast.isImmutable || info.ast.isExt) {
                  return info.specVars.nonEmpty
                } else {
                  return T
                }
              case _ => return F
            }
          case _ => return F
        }
      case t: AST.Typed.Tuple =>
        for (arg <- t.args) {
          if (isModifiable(arg)) {
            return T
          }
        }
        return F
      case t: AST.Typed.TypeVar => return !t.isImmutable
      case _ => return F
    }
  }

  @memoize def isIndexType(tipe: AST.Typed): B = {
    tipe match {
      case tipe: AST.Typed.TypeVar => return tipe.isIndex
      case AST.Typed.z => return T
      case tipe: AST.Typed.Name =>
        typeMap.get(tipe.ids) match {
          case Some(_: TypeInfo.SubZ) => return T
          case _ =>
        }
      case _ =>
    }
    return F
  }

  @memoize def isGroundType(tipe: AST.Typed): B = {
    if (TypeHierarchy.basicTypes.contains(tipe)) {
      return T
    }
    tipe match {
      case tipe: AST.Typed.Name =>
        tipe.ids match {
          case AST.Typed.isName => return F
          case AST.Typed.msName => return F
          case _ => typeMap.get(tipe.ids).get match {
            case _: TypeInfo.SubZ => return T
            case _: TypeInfo.Enum => return T
            case _ => return F
          }
        }
      case _ => return F
    }
  }

  @memoize def isSubstitutable(tipe: AST.Typed): B = {
    var seen = HashSet.empty[AST.Typed]
    def isSubstitutableH(t: AST.Typed): B = {
      if (seen.contains(t)) {
        return T
      }
      seen = seen + t
      if (AST.Typed.builtInTypes.contains(t)) {
        return T
      }
      t match {
        case t: AST.Typed.Name =>
          if (t.ids == AST.Typed.isName || t.ids == AST.Typed.msName) {
            return isSubstitutableH(t.args(1))
          }
          if (!ops.ISZOps(t.args).forall((targ: AST.Typed) => isSubstitutableH(targ))) {
            return F
          }
          typeMap.get(t.ids).get match {
            case info: TypeInfo.Adt =>
              if (info.ast.isRoot) {
                val sm = TypeChecker.buildTypeSubstMap(info.name, None(), info.ast.typeParams, t.args, Reporter.create).get
                for (childName <- poset.childrenOf(info.name).elements) {
                  val childInfo = typeMap.get(childName).get.asInstanceOf[TypeInfo.Adt]
                  for (parent <- childInfo.parents if parent.ids == info.name) {
                    val csm = TypeChecker.buildTypeSubstMap(info.name, None(), info.ast.typeParams, parent.args, Reporter.create).get
                    var ctargs = ISZ[AST.Typed]()
                    for (ctp <- childInfo.ast.typeParams) {
                      val ctpid = ctp.id.value
                      csm.get(ctpid) match {
                        case Some(subst1) =>
                          sm.get(subst1.asInstanceOf[AST.Typed.TypeVar].id) match {
                            case Some(ct) => ctargs = ctargs :+ ct
                            case _ => return F
                          }
                        case _ => return F
                      }
                    }
                    if (!isSubstitutableH(AST.Typed.Name(childInfo.name, ctargs))) {
                      return F
                    }
                  }
                }
                return T
              } else {
                if (info.methods.contains("isEqual")) {
                  return F
                }
                var paramIds = HashSet.empty[String]
                for (p <- info.ast.params) {
                  if (p.isHidden) {
                    return F
                  }
                  paramIds = paramIds + p.id.value
                }
                for (vInfo <- info.vars.values) {
                  if (!paramIds.contains(vInfo.ast.id.value) || !isSubstitutableH(vInfo.typedOpt.get)) {
                    return F
                  }
                }
                return T
              }
            case _: TypeInfo.SubZ => return T
            case _: TypeInfo.Sig => return F
            case _: TypeInfo.Enum => return T
            case info: TypeInfo.TypeAlias => halt(s"Unexpected usage of isSubstitutable on $info")
            case info: TypeInfo.TypeVar => halt(s"Unexpected usage of isSubstitutable on $info")
          }
        case _: AST.Typed.Enum => return T
        case _: AST.Typed.TypeVar => return F
        case t: AST.Typed.Tuple => return ops.ISZOps(t.args).forall((et: AST.Typed) => isSubstitutable(et))
        case _: AST.Typed.Fun => return F
        case _: AST.Typed.Method => return F
        case _: AST.Typed.Methods => return F
        case _: AST.Typed.Object => return F
        case _: AST.Typed.Theorem => return F
        case _: AST.Typed.Fact => return F
        case _: AST.Typed.Inv => return F
        case _: AST.Typed.Package => return F
      }
    }

    return isSubstitutableH(tipe)
  }

  @memoize def isSubstitutableWithoutSpecVars(tipe: AST.Typed): B = {
    var seen = HashSet.empty[AST.Typed]

    def isSubstitutableWithoutSpecVarsH(t: AST.Typed): B = {
      if (seen.contains(t)) {
        return T
      }
      seen = seen + t
      if (AST.Typed.builtInTypes.contains(t)) {
        return T
      }
      t match {
        case t: AST.Typed.Name =>
          if (t.ids == AST.Typed.isName || t.ids == AST.Typed.msName) {
            return isSubstitutableWithoutSpecVarsH(t.args(1))
          }
          if (!ops.ISZOps(t.args).forall((targ: AST.Typed) => isSubstitutableWithoutSpecVarsH(targ))) {
            return F
          }
          val infoOpt = typeMap.get(t.ids)
          if (infoOpt.isEmpty) {
            return F
          }
          infoOpt.get match {
            case info: TypeInfo.Adt =>
              val sm = TypeChecker.buildTypeSubstMap(info.name, None(), info.ast.typeParams, t.args, Reporter.create).get
              if (info.ast.isRoot) {
                for (childName <- poset.childrenOf(info.name).elements) {
                  val childInfo = typeMap.get(childName).get.asInstanceOf[TypeInfo.Adt]
                  for (parent <- childInfo.parents if parent.ids == info.name) {
                    val csm = TypeChecker.buildTypeSubstMap(info.name, None(), info.ast.typeParams, parent.args, Reporter.create).get
                    var ctargs = ISZ[AST.Typed]()
                    for (ctp <- childInfo.ast.typeParams) {
                      val ctpid = ctp.id.value
                      csm.get(ctpid) match {
                        case Some(subst1) =>
                          sm.get(subst1.asInstanceOf[AST.Typed.TypeVar].id) match {
                            case Some(ct) => ctargs = ctargs :+ ct
                            case _ => return F
                          }
                        case _ => return F
                      }
                    }
                    if (!isSubstitutableWithoutSpecVarsH(AST.Typed.Name(childInfo.name, ctargs))) {
                      return F
                    }
                  }
                }
                return T
              } else {
                if (info.specVars.nonEmpty) {
                  return F
                }
                if (info.methods.contains("isEqual")) {
                  return F
                }
                var paramIds = HashSet.empty[String]
                for (p <- info.ast.params) {
                  if (p.isHidden) {
                    return F
                  }
                  paramIds = paramIds + p.id.value
                }
                for (vInfo <- info.vars.values) {
                  if (!paramIds.contains(vInfo.ast.id.value)) {
                    return F
                  }
                  if (!isSubstitutableWithoutSpecVarsH(vInfo.typedOpt.get.subst(sm))) {
                    return F
                  }
                }
                return T
              }
            case _: TypeInfo.SubZ => return T
            case info: TypeInfo.Sig =>
              if (!info.ast.isSealed) {
                return F
              }
              val sm = TypeChecker.buildTypeSubstMap(info.name, None(), info.ast.typeParams, t.args, Reporter.create).get
              for (childName <- poset.childrenOf(info.name).elements) {
                val childInfo = typeMap.get(childName).get.asInstanceOf[TypeInfo.Adt]
                for (parent <- childInfo.parents if parent.ids == info.name) {
                  val csm = TypeChecker.buildTypeSubstMap(info.name, None(), info.ast.typeParams, parent.args, Reporter.create).get
                  var ctargs = ISZ[AST.Typed]()
                  for (ctp <- childInfo.ast.typeParams) {
                    val ctpid = ctp.id.value
                    csm.get(ctpid) match {
                      case Some(subst1) =>
                        sm.get(subst1.asInstanceOf[AST.Typed.TypeVar].id) match {
                          case Some(ct) => ctargs = ctargs :+ ct
                          case _ => return F
                        }
                      case _ => return F
                    }
                  }
                  if (!isSubstitutableWithoutSpecVarsH(AST.Typed.Name(childInfo.name, ctargs))) {
                    return F
                  }
                }
              }
              return T
            case _: TypeInfo.Enum => return T
            case info: TypeInfo.TypeAlias => halt(s"Unexpected usage of isSubstitutableWithoutSpecVars on $info")
            case info: TypeInfo.TypeVar => halt(s"Unexpected usage of isSubstitutableWithoutSpecVars on $info")
          }
        case _: AST.Typed.Enum => return T
        case _: AST.Typed.TypeVar => return F
        case t: AST.Typed.Tuple => return ops.ISZOps(t.args).forall((et: AST.Typed) => isSubstitutableWithoutSpecVarsH(et))
        case _: AST.Typed.Fun => return F
        case _: AST.Typed.Method => return F
        case _: AST.Typed.Methods => return F
        case _: AST.Typed.Object => return F
        case _: AST.Typed.Theorem => return F
        case _: AST.Typed.Fact => return F
        case _: AST.Typed.Inv => return F
        case _: AST.Typed.Package => return F
      }
    }

    val r = isSubstitutableWithoutSpecVarsH(tipe)
    return r
  }

  @pure def normalizeQuantType(exp: AST.Exp): AST.Exp = {
    return AST.Transformer(TypeHierarchy.QuantTypePrePostNormalizer()).transformExp(F, exp).resultOpt.getOrElseEager(exp)
  }

  @pure def nameToExp(name: ISZ[String], p: Position): AST.Exp.Ref = {
    @pure def nameResTypeOpts(ids: ISZ[String]): (Option[AST.ResolvedInfo], Option[AST.Typed]) = {
      nameMap.get(ids).get match {
        case info: Info.Object => return (Some(AST.ResolvedInfo.Object(ids)),
          Some(AST.Typed.Object(info.owner, info.ast.id.value)))
        case _: Info.Package => return (Some(AST.ResolvedInfo.Package(ids)),
          Some(AST.Typed.Package(ids)))
        case _: Info.Enum => return (Some(AST.ResolvedInfo.Enum(ids)),
          Some(AST.Typed.Enum(ids)))
        case info: Info.EnumElement => return (nameMap.get(info.owner).get.asInstanceOf[Info.Enum].elements.get(info.id),
          Some(AST.Typed.Name(ids, ISZ())))
        case info: Info.Var => return (info.resOpt, info.typedOpt)
        case info: Info.SpecVar => return (info.resOpt, info.typedOpt)
        case info => halt(s"Infeasible: $info")
      }
    }

    val pOpt: Option[Position] = Some(p)
    val first = name(0)
    val firstResTypedOpts = nameResTypeOpts(ISZ(first))
    val attr = AST.Attr(pOpt)
    var r: AST.Exp.Ref = AST.Exp.Ident(AST.Id(first, attr),
      AST.ResolvedAttr(pOpt, firstResTypedOpts._1, firstResTypedOpts._2))
    for (i <- 1 until name.size) {
      val ids: ISZ[String] = for (j <- 0 to i) yield name(j)
      val (rOpt, tOpt) = nameResTypeOpts(ids)
      val resolvedAttr = AST.ResolvedAttr(pOpt, rOpt, tOpt)
      ids match {
        case ISZ("org", "sireum", _*) if i == 2 => r = AST.Exp.Ident(AST.Id(ids(i), attr), resolvedAttr)
        case _ => r = AST.Exp.Select(Some(r.asExp), AST.Id(ids(i), attr), ISZ(), resolvedAttr)
      }
    }
    return r
  }

  @pure def normalizeExp(exp: AST.Exp): AST.Exp = {
    val exp2 = TypeHierarchy.ExpNormalizer.create(this).transformExp(1, exp).resultOpt.getOrElseEager(exp)
    val exp4: AST.Exp = AST.Transformer(TypeHierarchy.QuantTypePrePostNormalizer()).transformExp(F, exp2).resultOpt match {
      case Some(exp3) => TypeHierarchy.ExpNormalizer.create(this).transformExp(1, exp3).resultOpt.getOrElseEager(exp3)
      case _ => exp2
    }
    val ln = TypeHierarchy.ExpNormalizer.LocalNormalizer(Stack.empty[HashMap[ISZ[String], String]].push(HashMap.empty), 0)
    return ln.transformExp(exp4).getOrElseEager(exp4)
  }

  @memoize def isSubZName(name: ISZ[String]): B = {
    typeMap.get(name) match {
      case Some(_: TypeInfo.SubZ) => return T
      case _ =>
    }
    return F
  }

  @memoize def isAdt(t: AST.Typed.Name): B = {
    if (isGroundType(t)) {
      return F
    }
    t.ids match {
      case AST.Typed.isName => return F
      case AST.Typed.msName => return F
      case _ => return T
    }
  }

  @pure def isAdtType(t: AST.Typed): B = {
    t match {
      case t: AST.Typed.Name => return isAdt(t)
      case _ => return F
    }
  }

  @memoize def isAdtLeafType(t: AST.Typed): B = {
    t match {
      case t: AST.Typed.Name if isAdt(t) =>
        typeMap.get(t.ids).get match {
          case info: TypeInfo.Adt => return !info.ast.isRoot
          case _ =>
        }
      case _ =>
    }
    return F
  }

  def substChildrenOfType(posOpt: Option[Position], t: AST.Typed.Name): Either[ISZ[AST.Typed.Name], ISZ[message.Message]] = {
    var r = ISZ[AST.Typed.Name]()
    val reporter = Reporter.create
    for (cn <- poset.childrenOf(t.ids).elements) {
      var targs = ISZ[AST.Typed]()
      var child = t
      typeMap.get(cn) match {
        case Some(ti: TypeInfo.Sig) =>
          for (parent <- ti.parents if parent.ids == t.ids) {
            targs = parent.args
            child = ti.tpe
          }
        case Some(ti: TypeInfo.Adt) =>
          for (parent <- ti.parents if parent.ids == t.ids) {
            targs = parent.args
            child = ti.tpe
          }
        case _ => halt(st"Infeasible: ${(cn, ".")}".render)
      }
      TypeChecker.unifies(this, posOpt, TypeChecker.TypeRelation.Equal, t.args, targs, reporter) match {
        case Some(sm) => r = r :+ child.subst(sm)
        case _ => return Either.Right(reporter.messages)
      }
    }
    return Either.Left(r)
  }

  def substDescendantsOfType(posOpt: Option[Position], t: AST.Typed.Name): Either[ISZ[AST.Typed.Name], ISZ[message.Message]] = {
    var r = HashSSet.empty[AST.Typed.Name]
    var workS = ISZ(t)
    while (workS.nonEmpty) {
      val ts = workS
      workS = ISZ()
      for (t <- ts) {
        substChildrenOfType(posOpt, t) match {
          case Either.Left(children) =>
            r = r ++ children
            workS = workS ++ children
          case err => return err
        }
      }
    }
    return Either.Left(r.elements)
  }

  @memoize def substLeavesOfType(posOpt: Option[Position], t: AST.Typed.Name): Either[ISZ[AST.Typed.Name], ISZ[message.Message]] = {
    substDescendantsOfType(posOpt, t) match {
      case Either.Left(ts) =>
        var r = ISZ[AST.Typed.Name]()
        for (t <- ts) {
          typeMap.get(t.ids) match {
            case Some(ti: TypeInfo.Adt) if !ti.ast.isRoot => r = r :+ t
            case _ =>
          }
        }
        return Either.Left(r)
      case err => return err
    }
  }

  @pure def induct(isPattern: B, localIds: HashSet[String], context: ISZ[String],
                   claim: AST.Exp, exp: AST.Exp, pos: Position): Option[TypeHierarchy.InductResult] = {
    val posOpt = Option.some(pos)
    val attr = AST.Attr(posOpt)
    val resolvedAttr = AST.ResolvedAttr(posOpt, None(), None())
    val typedAttr = AST.TypedAttr(posOpt, None())
    val equivResAttr = AST.ResolvedAttr(posOpt,
      Some(AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryEquiv)), AST.Typed.bOpt)

    @pure def ids2name(ids: ISZ[String]): AST.Name = {
      return AST.Name(for (id <- ids) yield AST.Id(id, attr), attr)
    }

    @pure def inductEnum(ti: TypeInfo.Enum, e: AST.Exp): TypeHierarchy.InductResult = {
      val tOpt = Option.some(e.typedOpt.get)
      var cases = ISZ[TypeHierarchy.InductResult.Case]()
      val eResAttr = resolvedAttr(typedOpt = Some(AST.Typed.Enum(ti.name)), resOpt = Some(
        AST.ResolvedInfo.Enum(ti.name)))
      for (element <- ti.elements.entries) {
        val resAttr = resolvedAttr(resOpt = Some(element._2), typedOpt = tOpt)
        val premise = AST.Exp.Binary(e, AST.Exp.BinaryOp.EquivUni, AST.Exp.Select(
          Some(AST.Exp.Ident(AST.Id(ti.name(ti.name.size - 1), attr), eResAttr)), AST.Id(element._1, attr), ISZ(), resAttr), equivResAttr, posOpt)
        cases = cases :+ TypeHierarchy.InductResult.Case(
          AST.Pattern.Ref(F, ids2name(ti.name :+ element._1), None(), context,
            resAttr), ISZ(premise))
      }
      return TypeHierarchy.InductResult(F, cases)
    }

    @pure def inductTrait(isOpen: B, e: AST.Exp, t: AST.Typed.Name): TypeHierarchy.InductResult = {
      val dollar: String = if (isPattern) "$" else ""
      @pure def fresh(id: String): String = {
        if (!localIds.contains(id)) {
          return s"$dollar$id"
        }
        var i = 2
        var r = s"$id$i"
        while (localIds.contains(r)) {
          i = i + 1
          r = s"$id$i"
        }
        return s"$dollar$r"
      }
      var cases = ISZ[TypeHierarchy.InductResult.Case]()
      for (sub <- substLeavesOfType(None(), t).left) {
        typeMap.get(sub.ids).get match {
          case ti: TypeInfo.Adt =>
            val sm = TypeChecker.buildTypeSubstMap(sub.ids, posOpt, ti.ast.typeParams, t.args, Reporter.create).get
            val vbs: ISZ[AST.Pattern] = for (p <- ti.ast.params if !p.isHidden) yield
              AST.Pattern.VarBinding(p.id(value = fresh(p.id.value)), None(), context,
                typedAttr(typedOpt = Some(p.tipe.typedOpt.get.subst(sm))))
            val args: ISZ[AST.Exp] = for (vb <- vbs) yield AST.Exp.Ident(vb.asInstanceOf[AST.Pattern.VarBinding].id,
              AST.ResolvedAttr(posOpt, Some(AST.ResolvedInfo.LocalVar(context, AST.ResolvedInfo.LocalVar.Scope.Current, F, T,
                vb.asInstanceOf[AST.Pattern.VarBinding].id.value)), vb.typedOpt))
            val right = AST.Exp.Invoke(
              None(), AST.Exp.Ident(ti.ast.id, AST.ResolvedAttr(posOpt, Some(AST.ResolvedInfo.Object(ti.name)),
                Some(AST.Typed.Object(ti.owner, ti.ast.id.value)))), for (arg <- sub.args) yield AST.Util.typedToType(arg, pos),
              args, AST.ResolvedAttr(posOpt, ti.constructorResOpt, Some(sub)))
            cases = cases :+ TypeHierarchy.InductResult.Case(AST.Pattern.Structure(None(), Some(ids2name(ti.name)), vbs, context,
              resolvedAttr(resOpt = ti.extractorResOpt, typedOpt = Some(sub))), ISZ(
              AST.Exp.Binary(e, AST.Exp.BinaryOp.EquivUni, right, equivResAttr, posOpt)))
          case _ =>
        }
      }
      return TypeHierarchy.InductResult(isOpen, cases)
    }

    @pure def substMap(pattern: AST.Pattern.Structure, expType: AST.Typed): ISZ[(AST.Exp, AST.Exp)] = {
      var r = ISZ[(AST.Exp, AST.Exp)]()
      for (p <- pattern.patterns) {
        val vb = p.asInstanceOf[AST.Pattern.VarBinding]
        val t = vb.attr.typedOpt.get
        if (isSubType(t, expType)) {
          r = r :+ exp ~> AST.Exp.Ident(vb.id, AST.ResolvedAttr(
            vb.posOpt,
            Some(AST.ResolvedInfo.LocalVar(context, AST.ResolvedInfo.LocalVar.Scope.Closure, F, T, vb.id.value)),
            Some(t)))
        }
      }
      return r
    }

    val adtInduct: TypeHierarchy.InductResult = exp.typedOpt.get match {
      case tipe: AST.Typed.Name if !AST.Typed.builtInTypes.contains(tipe) && tipe.ids != AST.Typed.isName && tipe.ids != AST.Typed.msName =>
        typeMap.get(tipe.ids).get match {
          case ti: TypeInfo.Adt =>
            if (!ti.ast.isRoot) {
              return None()
            }
            inductTrait(F, exp, tipe)
          case ti: TypeInfo.Sig => inductTrait(!ti.ast.isSealed, exp, tipe)
          case ti: TypeInfo.Enum => return Some(inductEnum(ti, exp))
          case _ => return None()
        }
      case _: AST.Typed.Tuple => halt("TODO")
      case _ => return None()
    }
    var cases = ISZ[TypeHierarchy.InductResult.Case]()
    for (cas <- adtInduct.cases) {
      val struct = cas.pattern.asInstanceOf[AST.Pattern.Structure]
      val sm = substMap(struct, exp.typedOpt.get)
      if (sm.nonEmpty) {
        var premises = cas.premises
        for (p <- sm) {
          premises = premises :+ AST.Util.ExpSubstitutor(HashMap ++ ISZ(p._1 ~> p._2)).transformExp(claim).getOrElse(claim)
        }
        cases = cases :+ cas(premises = premises)
      } else {
        cases = cases :+ cas
      }
    }
    return Some(adtInduct(cases = cases))
  }

  @pure def translateToExtendedCoreExp(exp: AST.Exp, funStack: CoreExpTranslator.FunStack,
                                       localMap: CoreExpTranslator.LocalMap): AST.CoreExp.Base = {
    return CoreExpTranslator(this, CoreExpTranslator.Mode.Extended).translateExp(exp, funStack, localMap)
  }

  @pure def translateToBaseCoreExp(exp: AST.Exp, isPattern: B): AST.CoreExp.Base = {
    return CoreExpTranslator(this, if (isPattern) CoreExpTranslator.Mode.BasePattern else CoreExpTranslator.Mode.Base).
      translateExp(exp, Stack.empty, HashSMap.empty)
  }
}
