// #Sireum
/*
 Copyright (c) 2017-2026,Robby, Kansas State University
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

package org.sireum.lang

import org.sireum._
import org.sireum.lang.symbol.TypeInfo
import org.sireum.lang.tipe.TypeHierarchy
import org.sireum.lang.{ast => AST}
import org.sireum.U32._

object IRTranslator {
  @enum object PatternOwnerKind {
    "Adt"
    "Other"
    "SubZ"
  }

  @datatype class PatternField(val id: String, val tipe: AST.Typed)

  @datatype class PatternOwner(val kind: PatternOwnerKind.Type,
                               val typeParamIds: ISZ[String],
                               val visibleParams: ISZ[PatternField])

  @datatype class PatternDeclFacts(val owners: HashSMap[ISZ[String], PatternOwner])

  @pure def visiblePatternFields(info: TypeInfo.Adt): ISZ[PatternField] = {
    val fields = Buffer.create[PatternField]()
    for (param <- info.ast.params if !param.isHidden) {
      val fieldType: AST.Typed = info.vars.get(param.id.value) match {
        case Some(field) => field.typedOpt match {
          case Some(tipe) => tipe
          case _ => halt("Pattern declaration facts require a resolved ADT field")
        }
        case _ => halt("Pattern declaration facts require ADT field metadata")
      }
      fields.append(PatternField(param.id.value, fieldType))
    }
    return fields.toIS
  }

  @pure def freezePatternDeclFacts(th: TypeHierarchy): PatternDeclFacts = {
    val entries = Buffer.create[(ISZ[String], PatternOwner)]()
    for (entry <- th.typeMap.entries) {
      entry._2 match {
        case info: TypeInfo.Adt =>
          if (!info.typeChecked) {
            halt("Pattern declaration facts require a checked ADT owner")
          }
          val typeParamIds: ISZ[String] = for (typeParam <- info.ast.typeParams) yield typeParam.id.value
          entries.append((entry._1, PatternOwner(PatternOwnerKind.Adt, typeParamIds, visiblePatternFields(info))))
        case _: TypeInfo.Sig =>
          entries.append((entry._1, PatternOwner(PatternOwnerKind.Other, ISZ[String](), ISZ[PatternField]())))
        case _: TypeInfo.Enum =>
          entries.append((entry._1, PatternOwner(PatternOwnerKind.Other, ISZ[String](), ISZ[PatternField]())))
        case _: TypeInfo.SubZ =>
          entries.append((entry._1, PatternOwner(PatternOwnerKind.SubZ, ISZ[String](), ISZ[PatternField]())))
        case _ =>
      }
    }
    return PatternDeclFacts(HashSMap.empty[ISZ[String], PatternOwner] ++ entries.toIS)
  }

  @msig trait Fresh {
    def setLabel(n: Z): Unit
    def setTemp(n: Z): Unit
    def label(): Z
    def temp(): Z
  }

  @ext("IRTranslatorFreshAtomic") object Ext {
    @pure def createFresh: Fresh = $
  }

  @strictpure def createFresh: Fresh = Ext.createFresh

  @record class ClosureCaptureCollector(var captures: HashSMap[(ISZ[String], String), (ISZ[String], B, String, AST.Typed)],
                                        var capturesThis: B)
    extends AST.MTransformer {
    var lambdaContexts: Stack[ISZ[String]] = Stack.empty

    def isLambdaContext(context: ISZ[String]): B = {
      for (c <- lambdaContexts.elements) {
        if (c == context) {
          return T
        }
      }
      return F
    }

    override def preExpFun(o: AST.Exp.Fun): AST.MTransformer.PreResult[AST.Exp] = {
      lambdaContexts = lambdaContexts.push(o.context)
      return AST.MTransformer.PreResultExpFun
    }

    override def postExpFun(o: AST.Exp.Fun): MOption[AST.Exp] = {
      lambdaContexts.pop match {
        case Some((_, rest)) => lambdaContexts = rest
        case _ => halt("Infeasible: empty lambda context stack")
      }
      return AST.MTransformer.PostResultExpFun
    }

    override def postResolvedAttr(o: AST.ResolvedAttr): MOption[AST.ResolvedAttr] = {
      o.resOpt match {
        case Some(res: AST.ResolvedInfo.LocalVar) if res.scope == AST.ResolvedInfo.LocalVar.Scope.Closure && !isLambdaContext(res.context) =>
          val key = (res.context, res.id)
          if (!captures.contains(key)) {
            captures = captures + key ~> ((res.context, res.isVal, res.id, o.typedOpt.get))
          }
        case _ =>
      }
      return AST.MTransformer.PostResultResolvedAttr
    }
    override def postExpThis(o: AST.Exp.This): MOption[AST.Exp] = {
      capturesThis = T
      return AST.MTransformer.PostResultExpThis
    }
    override def postExpSuper(o: AST.Exp.Super): MOption[AST.Exp] = {
      capturesThis = T
      return AST.MTransformer.PostResultExpSuper
    }
    override def postExpIdent(o: AST.Exp.Ident): MOption[AST.Exp] = {
      o.resOpt match {
        case Some(res: AST.ResolvedInfo.Var) if !res.isInObject => capturesThis = T
        case Some(res: AST.ResolvedInfo.Method) if !res.isInObject => capturesThis = T
        case _ =>
      }
      return AST.MTransformer.PostResultExpIdent
    }
    override def postPatternRef(o: AST.Pattern.Ref): MOption[AST.Pattern] = {
      o.attr.resOpt match {
        case Some(res: AST.ResolvedInfo.Var) if !res.isInObject => capturesThis = T
        case _ =>
      }
      return AST.MTransformer.PostResultPatternRef
    }
    override def postExpSelect(o: AST.Exp.Select): MOption[AST.Exp] = {
      if (o.receiverOpt.isEmpty) {
        o.resOpt match {
          case Some(res: AST.ResolvedInfo.Var) if !res.isInObject => capturesThis = T
          case Some(res: AST.ResolvedInfo.Method) if !res.isInObject => capturesThis = T
          case _ =>
        }
      }
      return AST.MTransformer.PostResultExpSelect
    }
    override def postExpInvoke(o: AST.Exp.Invoke): MOption[AST.Exp] = {
      if (o.receiverOpt.isEmpty) {
        o.attr.resOpt match {
          case Some(res: AST.ResolvedInfo.Var) if !res.isInObject => capturesThis = T
          case Some(res: AST.ResolvedInfo.Method) if !res.isInObject => capturesThis = T
          case _ =>
        }
      }
      return AST.MTransformer.PostResultExpInvoke
    }
    override def postExpInvokeNamed(o: AST.Exp.InvokeNamed): MOption[AST.Exp] = {
      if (o.receiverOpt.isEmpty) {
        o.attr.resOpt match {
          case Some(res: AST.ResolvedInfo.Var) if !res.isInObject => capturesThis = T
          case Some(res: AST.ResolvedInfo.Method) if !res.isInObject => capturesThis = T
          case _ =>
        }
      }
      return AST.MTransformer.PostResultExpInvokeNamed
    }
    override def transformExpInvoke(o: AST.Exp.Invoke): MOption[AST.Exp.Invoke] = {
      o.receiverOpt match {
        case Some(receiver) => transformExp(receiver)
        case _ =>
      }
      for (arg <- o.args) {
        transformExp(arg)
      }
      transformResolvedAttr(o.attr)
      postExpInvoke(o)
      return MNone[AST.Exp.Invoke]()
    }
    override def transformExpInvokeNamed(o: AST.Exp.InvokeNamed): MOption[AST.Exp.InvokeNamed] = {
      o.receiverOpt match {
        case Some(receiver) => transformExp(receiver)
        case _ =>
      }
      for (arg <- o.args) {
        transformExp(arg.arg)
      }
      transformResolvedAttr(o.attr)
      postExpInvokeNamed(o)
      return MNone[AST.Exp.InvokeNamed]()
    }
  }

  @record class NestedMethodCallCollector(val calls: Buffer[ISZ[String]], var seen: HashSSet[ISZ[String]]) extends AST.MTransformer {
    def record(res: AST.ResolvedInfo.Method): Unit = {
      if (res.mode == AST.MethodMode.Method) {
        val key = res.owner :+ res.id
        if (!seen.contains(key)) {
          seen = seen + key
          calls.append(key)
        }
      }
      return
    }
    override def preStmtMethod(o: AST.Stmt.Method): AST.MTransformer.PreResult[AST.Stmt] = {
      return AST.MTransformer.PreResult(F, MNone())
    }
    override def postExpInvoke(o: AST.Exp.Invoke): MOption[AST.Exp] = {
      o.attr.resOpt match {
        case Some(res: AST.ResolvedInfo.Method) => record(res)
        case _ =>
      }
      return AST.MTransformer.PostResultExpInvoke
    }
    override def postExpInvokeNamed(o: AST.Exp.InvokeNamed): MOption[AST.Exp] = {
      o.attr.resOpt match {
        case Some(res: AST.ResolvedInfo.Method) => record(res)
        case _ =>
      }
      return AST.MTransformer.PostResultExpInvokeNamed
    }
    override def postExpIdent(o: AST.Exp.Ident): MOption[AST.Exp] = {
      o.resOpt match {
        case Some(res: AST.ResolvedInfo.Method) => record(res)
        case _ =>
      }
      return AST.MTransformer.PostResultExpIdent
    }
  }

  @record class NestedMethodDeclarationCollector(val methods: Buffer[AST.Stmt.Method],
                                                 var seen: HashSSet[ISZ[String]]) extends AST.MTransformer {
    override def preStmtMethod(o: AST.Stmt.Method): AST.MTransformer.PreResult[AST.Stmt] = {
      o.bodyOpt match {
        case Some(_) =>
          o.attr.resOpt match {
            case Some(res: AST.ResolvedInfo.Method) =>
              val key = res.owner :+ res.id
              if (!seen.contains(key)) {
                seen = seen + key
                methods.append(o)
              }
            case _ =>
          }
        case _ =>
      }
      return AST.MTransformer.PreResultStmtMethod
    }
  }

}

@record class IRTranslator(val spec: B,
                           val threeAddressCode: B,
                           val threeAddressExpF: AST.IR.Exp => B @pure,
                           val th: TypeHierarchy,
                           val fresh: IRTranslator.Fresh) {

  var methodContext: AST.IR.MethodContext = AST.IR.MethodContext.empty
  var stmts: ISZ[AST.IR.Stmt] = ISZ()
  var liftedProcedures: ISZ[AST.IR.Procedure] = ISZ()
  var nestedMethodCaptures: HashMap[ISZ[String], ISZ[(B, String, AST.Typed)]] = HashMap.empty
  var nestedMethodCaptureInfo: HashMap[ISZ[String], ISZ[(ISZ[String], B, String, AST.Typed)]] = HashMap.empty
  var varCaptureSet: HashSet[String] = HashSet.empty
  var capturedThisTypeOpt: Option[AST.Typed] = None()
  var currentThisExpOpt: Option[AST.IR.Exp] = None()
  var extMethodAccum: HashSSet[(B, ISZ[String], String)] = HashSSet.empty
  var expDepth: Z = 0

  def resetTemp(): Unit = {
    if (expDepth == 0) {
      fresh.setTemp(0)
    }
  }

  def recordAndResolveExt(res: AST.ResolvedInfo.Method, applyIsInObject: B): ISZ[String] = {
    if (res.mode == AST.MethodMode.Ext) {
      extMethodAccum = extMethodAccum + ((applyIsInObject, res.owner, res.id))
    }
    return res.owner
  }

  @strictpure def liftedNestedMethodId(res: AST.ResolvedInfo.Method): String =
    st"$$nested.${(res.owner, ".")}.${res.id}".render

  @strictpure def mboxType(t: AST.Typed): AST.Typed.Name =
    AST.Typed.Name(AST.Typed.sireumName :+ "MBox", None(), ISZ(t))

  def loweredMBoxType(t: AST.Typed): AST.Typed.Name = {
    return mboxType(lowerByNameType(t))
  }

  def collectTypeVarIds(t: AST.Typed, seen: HashSet[String]): (ISZ[String], HashSet[String]) = {
    t match {
      case tv: AST.Typed.TypeVar =>
        if (seen.contains(tv.id)) {
          return (ISZ(), seen)
        }
        return (ISZ(tv.id), seen + tv.id)
      case n: AST.Typed.Name =>
        var ids = ISZ[String]()
        var s = seen
        for (a <- n.args) {
          val p = collectTypeVarIds(a, s)
          ids = ids ++ p._1
          s = p._2
        }
        return (ids, s)
      case f: AST.Typed.Fun =>
        var ids = ISZ[String]()
        var s = seen
        for (a <- f.args) {
          val p = collectTypeVarIds(a, s)
          ids = ids ++ p._1
          s = p._2
        }
        val p = collectTypeVarIds(f.ret, s)
        ids = ids ++ p._1
        s = p._2
        return (ids, s)
      case tu: AST.Typed.Tuple =>
        var ids = ISZ[String]()
        var s = seen
        for (a <- tu.args) {
          val p = collectTypeVarIds(a, s)
          ids = ids ++ p._1
          s = p._2
        }
        return (ids, s)
      case _ =>
        return (ISZ(), seen)
    }
  }

  @strictpure def byNameValueTypeOpt(t: AST.Typed): Option[AST.Typed] = t match {
    case ft: AST.Typed.Fun if ft.isByName && ft.args.isEmpty => Some(ft.ret)
    case _ => None()
  }

  def lowerByNameType(t: AST.Typed): AST.Typed = {
    t match {
      case ft: AST.Typed.Fun =>
        var newArgs = ISZ[AST.Typed]()
        for (arg <- ft.args) {
          newArgs = newArgs :+ lowerByNameType(arg)
        }
        val newRet = lowerByNameType(ft.ret)
        if (ft.isByName && ft.args.isEmpty) {
          return AST.Typed.Fun(ft.purity, F, ISZ(), newRet)
        }
        return ft(args = newArgs, ret = newRet)
      case tn: AST.Typed.Name =>
        var newArgs = ISZ[AST.Typed]()
        for (arg <- tn.args) {
          newArgs = newArgs :+ lowerByNameType(arg)
        }
        return tn(args = newArgs)
      case tu: AST.Typed.Tuple =>
        var newArgs = ISZ[AST.Typed]()
        for (arg <- tu.args) {
          newArgs = newArgs :+ lowerByNameType(arg)
        }
        return tu(args = newArgs)
      case _ => return t
    }
  }

  def lowerByNameFunType(t: AST.Typed.Fun): AST.Typed.Fun = {
    return lowerByNameType(t).asInstanceOf[AST.Typed.Fun]
  }

  def nestedCaptureExp(capture: (B, String, AST.Typed), pos: message.Position): AST.IR.Exp = {
    val captureId = capture._2
    val captureType = capture._3
    if (captureId == "this") {
      return thiz(pos)
    }
    if (!capture._1 || varCaptureSet.contains(captureId)) {
      return AST.IR.Exp.LocalVarRef(capture._1, methodContext, captureId, loweredMBoxType(captureType), pos)
    }
    return AST.IR.Exp.LocalVarRef(capture._1, methodContext, captureId, lowerByNameType(captureType), pos)
  }

  def currentThisTypeOpt: Option[AST.Typed] = {
    currentThisExpOpt match {
      case Some(exp) => return Some(exp.tipe)
      case _ =>
    }
    capturedThisTypeOpt match {
      case Some(t) => return Some(t)
      case _ =>
    }
    if (!methodContext.isInObject) {
      return Some(methodContext.receiverType)
    }
    return None()
  }

  def prependThisCapture(captures: ISZ[(B, String, AST.Typed)], capturesThis: B): ISZ[(B, String, AST.Typed)] = {
    if (capturesThis && captureThisTypeOpt(captures).isEmpty) {
      currentThisTypeOpt match {
        case Some(t) => return (T, "this", t) +: captures
        case _ =>
      }
    }
    return captures
  }

  @pure def captureThisTypeOpt(captures: ISZ[(B, String, AST.Typed)]): Option[AST.Typed] = {
    for (capture <- captures if capture._2 == "this") {
      return Some(capture._3)
    }
    return None()
  }

  def addCaptureTypeParams(typeParams: ISZ[String], captureTypes: ISZ[AST.Typed]): ISZ[String] = {
    var r = typeParams
    var seen = HashSet.empty[String]
    for (tp <- typeParams) {
      seen = seen + tp
    }
    for (ct <- captureTypes) {
      val p = collectTypeVarIds(ct, seen)
      r = r ++ p._1
      seen = p._2
    }
    return r
  }

  def collectCapturesExp(exp: AST.Exp): (ISZ[(ISZ[String], B, String, AST.Typed)], B) = {
    val collector = IRTranslator.ClosureCaptureCollector(HashSMap.empty, F)
    collector.transformExp(exp)
    val calls = collectNestedMethodCallsExp(exp)
    return (augmentNestedCaptureInfo(collector.captures.values, calls), collector.capturesThis)
  }

  def collectNestedMethodCalls(body: AST.Body): ISZ[ISZ[String]] = {
    val collector = IRTranslator.NestedMethodCallCollector(Buffer.create[ISZ[String]](), HashSSet.empty[ISZ[String]])
    collector.transformBody(body)
    return collector.calls.toIS
  }

  def collectNestedMethodCallsExp(exp: AST.Exp): ISZ[ISZ[String]] = {
    val collector = IRTranslator.NestedMethodCallCollector(Buffer.create[ISZ[String]](), HashSSet.empty[ISZ[String]])
    collector.transformExp(exp)
    return collector.calls.toIS
  }

  def collectNestedMethodCallsAssignExp(exp: AST.AssignExp): ISZ[ISZ[String]] = {
    val collector = IRTranslator.NestedMethodCallCollector(Buffer.create[ISZ[String]](), HashSSet.empty[ISZ[String]])
    collector.transformAssignExp(exp)
    return collector.calls.toIS
  }

  def augmentNestedCaptureInfo(captures: ISZ[(ISZ[String], B, String, AST.Typed)], calls: ISZ[ISZ[String]]): ISZ[(ISZ[String], B, String, AST.Typed)] = {
    val result = Buffer.create[(ISZ[String], B, String, AST.Typed)]()
    var ids = HashSet.empty[String]
    for (capture <- captures) {
      result.append(capture)
      ids = ids + capture._3
    }
    for (key <- calls) {
      nestedMethodCaptureInfo.get(key) match {
        case Some(nestedCaptures) =>
          for (capture <- nestedCaptures) {
            if (!ids.contains(capture._3)) {
              result.append(capture)
              ids = ids + capture._3
            }
          }
        case _ =>
      }
    }
    return result.toIS
  }

  def prependThisCaptureInfo(captures: ISZ[(ISZ[String], B, String, AST.Typed)], capturesThis: B): ISZ[(ISZ[String], B, String, AST.Typed)] = {
    if (capturesThis) {
      currentThisTypeOpt match {
        case Some(t) => return ((methodContext.owner :+ methodContext.id), T, "this", t) +: captures
        case _ =>
      }
    }
    return captures
  }

  def makeByNameClosure(arg: AST.Exp, byNameType: AST.Typed.Fun, pos: message.Position): AST.IR.Exp = {
    val thunkType = lowerByNameFunType(byNameType)
    val closureName = st"$$byname.${pos.beginLine}.${pos.beginColumn}.${fresh.temp()}".render
    val owner = methodContext.owner
    val captureInfo = collectCapturesExp(arg)
    val captures = prependThisCapture(for (capture <- captureInfo._1) yield (capture._2, capture._3, capture._4), captureInfo._2)

    var captureNames = ISZ[String]()
    var captureTypes = ISZ[AST.Typed]()
    var captureExprs = ISZ[AST.IR.Exp]()
    var liftedVarCaptureSet = HashSet.empty[String]
    for (capture <- captures) {
      val captureIsVal = capture._1
      val captureId = capture._2
      val captureType = capture._3
      val loweredCaptureType = lowerByNameType(captureType)
      captureNames = captureNames :+ captureId
      if (captureId == "this") {
        captureTypes = captureTypes :+ loweredCaptureType
        captureExprs = captureExprs :+ thiz(pos)
      } else if (!captureIsVal || varCaptureSet.contains(captureId)) {
        val mt = mboxType(loweredCaptureType)
        captureTypes = captureTypes :+ mt
        captureExprs = captureExprs :+ AST.IR.Exp.LocalVarRef(T, methodContext, captureId, mt, pos)
        liftedVarCaptureSet = liftedVarCaptureSet + captureId
      } else {
        captureTypes = captureTypes :+ loweredCaptureType
        captureExprs = captureExprs :+ AST.IR.Exp.LocalVarRef(T, methodContext, captureId, loweredCaptureType, pos)
      }
    }

    val savedMethodContext = methodContext
    val savedStmts = stmts
    val savedVarCaptureSet = varCaptureSet
    val savedNestedMethodCaptures = nestedMethodCaptures
    val savedNestedMethodCaptureInfo = nestedMethodCaptureInfo
    val savedCapturedThisTypeOpt = capturedThisTypeOpt
    val savedCurrentThisExpOpt = currentThisExpOpt

    methodContext = AST.IR.MethodContext(
      isInObject = T,
      owner = owner,
      id = closureName,
      t = thunkType(args = captureTypes ++ thunkType.args)
    )
    stmts = ISZ()
    varCaptureSet = liftedVarCaptureSet
    capturedThisTypeOpt = captureThisTypeOpt(captures)
    currentThisExpOpt = None()

    val bodyPos = arg.posOpt.get
    if (thunkType.ret == AST.Typed.unit) {
      translateStmt(AST.Stmt.Expr(arg, ISZ(), AST.TypedAttr(arg.posOpt, arg.typedOpt)), None())
      stmts = stmts :+ AST.IR.Stmt.Return(None(), bodyPos)
    } else {
      val r = translateExp(arg)
      stmts = stmts :+ AST.IR.Stmt.Return(Some(r), bodyPos)
    }
    val liftedBody = AST.IR.Body.Block(AST.IR.Stmt.Block(stmts, pos))

    var closureTypeVarIds = ISZ[String]()
    var closureTypeVarSeen = HashSet.empty[String]
    for (ct <- captureTypes) {
      val p = collectTypeVarIds(ct, closureTypeVarSeen)
      closureTypeVarIds = closureTypeVarIds ++ p._1
      closureTypeVarSeen = p._2
    }

    val liftedProc = AST.IR.Procedure(
      isInObject = T,
      rTypeParams = ISZ(),
      typeParams = closureTypeVarIds,
      owner = owner,
      id = closureName,
      paramNames = captureNames,
      tipe = thunkType(args = captureTypes ++ thunkType.args),
      body = liftedBody,
      pos = pos
    )
    liftedProcedures = liftedProcedures :+ liftedProc

    methodContext = savedMethodContext
    stmts = savedStmts
    varCaptureSet = savedVarCaptureSet
    nestedMethodCaptures = savedNestedMethodCaptures
    nestedMethodCaptureInfo = savedNestedMethodCaptureInfo
    capturedThisTypeOpt = savedCapturedThisTypeOpt
    currentThisExpOpt = savedCurrentThisExpOpt

    return norm3AC(AST.IR.Exp.ClosureRef(
      owner = owner,
      id = closureName,
      captures = captureExprs,
      tipe = thunkType,
      pos = pos
    ))
  }

  def translateMethodH(isBasic: B,
                       receiverTypeOpt: Option[AST.Typed],
                       owner: ISZ[String],
                       id: String,
                       typeParams: ISZ[String],
                       params: ISZ[String],
                       funType: AST.Typed.Fun,
                       pos: message.Position,
                       bodyOpt: Option[AST.Body]): AST.IR.Procedure = {
    val isInObject = receiverTypeOpt.isEmpty
    var t: AST.Typed.Fun = funType
    var paramNames = params
    if (!isInObject) {
      paramNames = "this" +: paramNames
      t = t(args = lowerByNameType(receiverTypeOpt.get) +: t.args)
    }
    val oldCapturedThisTypeOpt = capturedThisTypeOpt
    capturedThisTypeOpt = None()
    val oldCurrentThisExpOpt = currentThisExpOpt
    currentThisExpOpt = None()
    methodContext = AST.IR.MethodContext(isInObject, owner, id, t)
    val methodCaptureContext = owner :+ id
    val isWorksheetMain = owner.isEmpty && id == "main"
    val oldVarCaptureSet = varCaptureSet
    varCaptureSet = HashSet.empty
    bodyOpt match {
      case Some(b) =>
        val collector = IRTranslator.ClosureCaptureCollector(HashSMap.empty, F)
        collector.transformBody(b)
        for (capture <- collector.captures.values if
          (capture._1 == methodCaptureContext || (isWorksheetMain && capture._1.isEmpty)) && !capture._2) {
          varCaptureSet = varCaptureSet + capture._3
        }
      case _ =>
    }
    var body: AST.IR.Body = bodyOpt match {
      case Some(body) =>
        val oldStmts = stmts
        stmts = ISZ()
        translateBody(body, None())
        val b = AST.IR.Body.Block(AST.IR.Stmt.Block(stmts, pos))
        stmts = oldStmts
        b
      case _ => AST.IR.Body.Block(AST.IR.Stmt.Block(ISZ(), pos))
    }
    varCaptureSet = oldVarCaptureSet
    capturedThisTypeOpt = oldCapturedThisTypeOpt
    currentThisExpOpt = oldCurrentThisExpOpt
    if (isBasic) {
      body = toBasic(body.asInstanceOf[AST.IR.Body.Block], pos)
    }
    return AST.IR.Procedure(isInObject, ISZ(), typeParams, owner, id, paramNames, t, body, pos)
  }

  def translateMethod(isBasic: B,
                      receiverTypeOpt: Option[AST.Typed],
                      owner: ISZ[String],
                      method: AST.Stmt.Method): AST.IR.Procedure = {
    val typeParams: ISZ[String] = for (tp <- method.sig.typeParams) yield tp.id.value
    val paramNames: ISZ[String] = for (p <- method.sig.params) yield p.id.value
    return translateMethodH(isBasic, receiverTypeOpt, owner, method.sig.id.value,
      typeParams, paramNames, method.sig.funType, method.sig.id.attr.posOpt.get, method.bodyOpt)
  }

  @pure def simplifyAssignPattern(stmt: AST.IR.Stmt.AssignPattern): AST.IR.Stmt.Block = {
    return simplifyAssignPatternH(stmt, None())
  }

  @pure def simplifyAssignPatternH(stmt: AST.IR.Stmt.AssignPattern,
                                   patternFactsOpt: Option[IRTranslator.PatternDeclFacts]): AST.IR.Stmt.Block = {
    val pos = stmt.pos
    val initId = assignExpId("$pattern.", None(), pos)
    val initType = stmt.rhs.tipe
    val init = AST.IR.Exp.LocalVarRef(T, stmt.context, initId, initType, pos)
    var assignStmts = ISZ[AST.IR.Stmt](
      AST.IR.Stmt.Decl(F, T, F, stmt.context, ISZ(AST.IR.Stmt.Decl.Local(initId, initType)), pos),
      AST.IR.Stmt.Assign.Local(stmt.context, initId, initType, stmt.rhs, pos))
    val (_, lMap) = translatePatternH(init, stmt.pattern, HashSMap.empty, patternFactsOpt)
    for (e <- lMap.entries) {
      assignStmts = assignStmts :+ AST.IR.Stmt.Assign.Local(stmt.context, e._1._2, e._2.tipe, e._2, e._2.pos)
    }
    return AST.IR.Stmt.Block(assignStmts, pos)
  }

  @pure def simplifyMatch(stmt: AST.IR.Stmt.Match): AST.IR.Stmt.Block = {
    return simplifyMatchH(stmt, None())
  }

  @pure def simplifyMatchH(stmt: AST.IR.Stmt.Match,
                            patternFactsOpt: Option[IRTranslator.PatternDeclFacts]): AST.IR.Stmt.Block = {
    val matchCondId = matchExpId(stmt.exp.pos)
    val pos = stmt.pos
    var stmts = ISZ[AST.IR.Stmt](
      AST.IR.Stmt.Decl(F, F, F, methodContext, ISZ(AST.IR.Stmt.Decl.Local(matchCondId, AST.Typed.b)), pos),
      AST.IR.Stmt.Assign.Local(methodContext, matchCondId, AST.Typed.b, AST.IR.Exp.Bool(F, pos), pos)
    )
    var first = T
    for (cas <- stmt.cases) {
      val (cs, lMap) = translatePatternH(stmt.exp, cas.pattern, HashSMap.empty, patternFactsOpt)
      val casPos = cas.pattern.pos
      var bindingStmts = ISZ[AST.IR.Stmt]()
      if (lMap.nonEmpty) {
        bindingStmts = bindingStmts :+ AST.IR.Stmt.Decl(F, T, F, methodContext,
          for (e <- lMap.entries) yield AST.IR.Stmt.Decl.Local(e._1._2, e._2.tipe), pos)
        for (e <- lMap.entries) {
          bindingStmts = bindingStmts :+ AST.IR.Stmt.Assign.Local(methodContext, e._1._2, e._2.tipe, e._2, e._2.pos)
        }
      }
      val acceptedStmts = ISZ[AST.IR.Stmt](AST.IR.Stmt.Assign.Local(methodContext, matchCondId, AST.Typed.b,
        AST.IR.Exp.Bool(T, casPos), casPos)) ++ cas.body.stmts
      val matchedBlock: AST.IR.Stmt.Block = cas.condOpt match {
        case Some(cond) =>
          val guardedBody = AST.IR.Stmt.If(cond.exp, cas.body(stmts = acceptedStmts),
            AST.IR.Stmt.Block(ISZ(), cond.exp.pos), cond.exp.pos)
          AST.IR.Stmt.Block(bindingStmts ++ cond.stmts :+ guardedBody, cas.body.pos)
        case _ =>
          cas.body(stmts = bindingStmts ++ acceptedStmts)
      }
      var r: AST.IR.Stmt = if (cs.isEmpty) {
        matchedBlock
      } else {
        val last = cs(cs.size - 1)
        var ifStmt = AST.IR.Stmt.If(last, matchedBlock,
          AST.IR.Stmt.Block(ISZ(), last.pos), last.pos)
        for (i <- cs.size - 2 to 0 by -1) {
          val cond = cs(i)
          ifStmt = AST.IR.Stmt.If(cond, AST.IR.Stmt.Block(ISZ(ifStmt), ifStmt.pos), AST.IR.Stmt.Block(ISZ(), cond.pos), cond.pos)
        }
        ifStmt
      }
      if (first) {
        first = F
      } else {
        val cond = AST.IR.Exp.Unary(AST.Typed.b, AST.Exp.UnaryOp.Not,
          AST.IR.Exp.LocalVarRef(F, methodContext, matchCondId, AST.Typed.b, pos), pos)
        r = AST.IR.Stmt.If(cond, AST.IR.Stmt.Block(ISZ(r), r.pos), AST.IR.Stmt.Block(ISZ(), cond.pos), cond.pos)
      }
      stmts = stmts :+ r
    }
    return AST.IR.Stmt.Block(stmts, pos)
  }

  def scalarSwitchCaseValue(pattern: AST.IR.Pattern): Option[AST.IR.Exp] = {
    pattern match {
      case _: AST.IR.Pattern.Wildcard => return None()
      case pattern: AST.IR.Pattern.Literal => return Some(AST.IR.Pattern.directLiteral(pattern.exp))
      case _ => halt(s"Infeasible: $pattern")
    }
  }

  def preparePatternBody(body: AST.IR.Body.Block): AST.IR.Body.Block = {
    return preparePatternBodyH(body, None())
  }

  def preparePatternBodyWithPatternFacts(body: AST.IR.Body.Block,
                                         patternFacts: IRTranslator.PatternDeclFacts): AST.IR.Body.Block = {
    return preparePatternBodyH(body, Some(patternFacts))
  }

  def preparePatternBodyH(body: AST.IR.Body.Block,
                          patternFactsOpt: Option[IRTranslator.PatternDeclFacts]): AST.IR.Body.Block = {
    def prepareExpBlock(expBlock: AST.IR.ExpBlock): AST.IR.ExpBlock = {
      val prepared = prepareBlock(AST.IR.Stmt.Block(expBlock.stmts, expBlock.exp.pos))
      return AST.IR.ExpBlock(prepared._1.stmts, expBlock.exp)
    }

    def prepareStmt(stmt: AST.IR.Stmt): (AST.IR.Stmt, B) = {
      stmt match {
        case s: AST.IR.Stmt.Block =>
          val prepared = prepareBlock(s)
          return (prepared._1, prepared._2)
        case s: AST.IR.Stmt.Match =>
          if (isScalarWithPatternFacts(s.exp.tipe, patternFactsOpt) && ops.ISZOps(s.cases).forall((c : AST.IR.Stmt.Match.Case) =>
            c.condOpt.isEmpty && c.decl.locals.isEmpty && (c.pattern.isInstanceOf[AST.IR.Pattern.Literal] ||
              c.pattern.isInstanceOf[AST.IR.Pattern.Wildcard]))) {
            val cases = Buffer.create[AST.IR.Stmt.Switch.Case]()
            for (c <- s.cases) {
              val preparedBody = prepareBlock(c.body)
              cases.append(AST.IR.Stmt.Switch.Case(scalarSwitchCaseValue(c.pattern), preparedBody._1))
            }
            return (AST.IR.Stmt.Switch(s.exp, cases.toIS, s.pos), T)
          }
          return prepareStmt(simplifyMatchH(s, patternFactsOpt))
        case s: AST.IR.Stmt.AssignPattern =>
          return prepareStmt(simplifyAssignPatternH(s, patternFactsOpt))
        case s: AST.IR.Stmt.If =>
          val thenPrepared = prepareBlock(s.thenBlock)
          val elsePrepared = prepareBlock(s.elseBlock)
          return (AST.IR.Stmt.If(s.cond, thenPrepared._1, elsePrepared._1, s.pos),
            thenPrepared._2 || elsePrepared._2)
        case s: AST.IR.Stmt.While =>
          val condPrepared = prepareBlock(AST.IR.Stmt.Block(s.cond.stmts, s.cond.exp.pos))
          val cond = AST.IR.ExpBlock(condPrepared._1.stmts, s.cond.exp)
          if (!condPrepared._2) {
            return (AST.IR.Stmt.While(cond, AST.IR.Stmt.Block(ISZ(), s.block.pos), s.pos), F)
          }
          val bodyPrepared = prepareBlock(s.block)
          return (AST.IR.Stmt.While(cond, bodyPrepared._1, s.pos), T)
        case s: AST.IR.Stmt.For =>
          val condOpt: Option[AST.IR.ExpBlock] = s.condOpt match {
            case Some(cond) => Some(prepareExpBlock(cond))
            case _ => None()
          }
          val bodyPrepared = prepareBlock(s.block)
          return (AST.IR.Stmt.For(s.context, s.idOpt, s.range, condOpt, bodyPrepared._1, s.pos), T)
        case s: AST.IR.Stmt.Assertume =>
          val messageOpt: Option[AST.IR.ExpBlock] = s.messageOpt match {
            case Some(message) => Some(prepareExpBlock(message))
            case _ => None()
          }
          return (AST.IR.Stmt.Assertume(s.isAssert, s.cond, messageOpt, s.pos), T)
        case s: AST.IR.Stmt.Switch =>
          val cases = Buffer.create[AST.IR.Stmt.Switch.Case]()
          for (c <- s.cases) {
            cases.append(AST.IR.Stmt.Switch.Case(c.valueOpt, prepareBlock(c.body)._1))
          }
          return (AST.IR.Stmt.Switch(s.exp, cases.toIS, s.pos), T)
        case _: AST.IR.Stmt.Halt => return (stmt, F)
        case _: AST.IR.Stmt.Return => return (stmt, F)
        case _ => return (stmt, T)
      }
    }

    def prepareBlock(block: AST.IR.Stmt.Block): (AST.IR.Stmt.Block, B) = {
      val stmts = Buffer.create[AST.IR.Stmt]()
      var continues = T
      var i: Z = 0
      while (i < block.stmts.size && continues) {
        val prepared = prepareStmt(block.stmts(i))
        stmts.append(prepared._1)
        continues = prepared._2
        i = i + 1
      }
      return (AST.IR.Stmt.Block(stmts.toIS, block.pos), continues)
    }

    return AST.IR.Body.Block(prepareBlock(body.block)._1)
  }

  def toBasic(body: AST.IR.Body.Block, pos: message.Position): AST.IR.Body.Basic = {

    val blocks = Buffer.create[AST.IR.BasicBlock]()
    var grounds = ISZ[AST.IR.Stmt.Ground]()
    var decls = ISZ[AST.IR.Stmt.Decl]()

    def addGround(g: AST.IR.Stmt.Ground): Unit = {
      grounds = grounds :+ g
    }

    val initLabel = fresh.label()

    @pure def basicBlock(label: Z, stmts: ISZ[AST.IR.Stmt.Ground], jump: AST.IR.Jump): AST.IR.BasicBlock = {
      return AST.IR.BasicBlock(label, stmts, jump)
    }

    def stmtToBasic(label: Z, stmt: AST.IR.Stmt, blocksBuf: Buffer[AST.IR.BasicBlock]): Option[Z] = {
      stmt match {
        case stmt: AST.IR.Stmt.Block =>
          return blockToBasic(label, stmt, blocksBuf)
        case stmt: AST.IR.Stmt.Expr =>
          addGround(stmt)
          return Some(label)
        case stmt: AST.IR.Stmt.Decl =>
          addGround(stmt)
          decls = decls :+ stmt
          return Some(label)
        case stmt: AST.IR.Stmt.Assign =>
          addGround(stmt)
          return Some(label)
        case stmt: AST.IR.Stmt.Assertume =>
          val tLabel = fresh.label()
          var fLabel = fresh.label()
          blocksBuf.append(AST.IR.BasicBlock(label, grounds, AST.IR.Jump.If(stmt.cond, tLabel, fLabel, stmt.pos)))
          grounds = ISZ()
          var addF = T
          stmt.messageOpt match {
            case Some(m) =>
              stmtToBasic(fLabel, AST.IR.Stmt.Block(m.stmts, m.exp.pos), blocksBuf) match {
                case Some(l) =>
                  fLabel = l
                  stmtToBasic(fLabel, AST.IR.Stmt.Print(AST.IR.Stmt.Print.Kind.Err, T, ISZ(m.exp), m.exp.pos), blocksBuf) match {
                    case Some(l2) => fLabel = l2
                    case _ => addF = F
                  }
                case _ => addF = F
              }
            case _ =>
          }
          if (addF) {
            blocksBuf.append(AST.IR.BasicBlock(fLabel, grounds, AST.IR.Jump.Halt(pos)))
          }
          grounds = ISZ()
          return Some(tLabel)
        case stmt: AST.IR.Stmt.Halt =>
          stmt.message match {
            case m: AST.IR.Exp.String if m.value.size == 0 =>
              blocksBuf.append(AST.IR.BasicBlock(label, grounds, AST.IR.Jump.Halt(pos)))
              grounds = ISZ()
            case _ =>
              stmtToBasic(label, AST.IR.Stmt.Print(AST.IR.Stmt.Print.Kind.Err, T, ISZ(stmt.message), pos), blocksBuf) match {
                case Some(l) =>
                  blocksBuf.append(AST.IR.BasicBlock(l, grounds, AST.IR.Jump.Halt(pos)))
                  grounds = ISZ()
                case _ =>
              }
          }
          return None()
        case stmt: AST.IR.Stmt.Print =>
          var args = ISZ[AST.IR.Exp]()
          var i: Z = 0
          val id: String = stmt.kind match {
            case AST.IR.Stmt.Print.Kind.Out => "print"
            case AST.IR.Stmt.Print.Kind.Err => "eprint"
            case AST.IR.Stmt.Print.Kind.OutErr =>
               args = args :+ stmt.args(0)
               i = 1
              "cprint"
          }
          for (j <- i until stmt.args.size) {
            val arg = stmt.args(j)
            grounds = grounds :+ AST.IR.Stmt.Expr(AST.IR.Exp.Apply(T, AST.Typed.sireumName, id, AST.Typed.emptyRTypes, args :+ stmt.args(j),
              AST.Typed.Fun(AST.Purity.Impure, F, ISZ(arg.tipe), AST.Typed.unit), arg.pos, F))
          }
          if (stmt.line) {
            grounds = grounds :+ AST.IR.Stmt.Expr(AST.IR.Exp.Apply(T, AST.Typed.sireumName, id, AST.Typed.emptyRTypes, args :+
              AST.IR.Exp.Int(AST.Typed.c, 10, stmt.pos), AST.Typed.Fun(AST.Purity.Impure, F, ISZ(AST.Typed.c),
              AST.Typed.unit), stmt.pos, F))
          }
          return Some(label)
        case stmt: AST.IR.Stmt.Match =>
          if (isScalar(stmt.exp.tipe) && ops.ISZOps(stmt.cases).forall((c : AST.IR.Stmt.Match.Case) =>
            c.condOpt.isEmpty && c.decl.locals.isEmpty && (c.pattern.isInstanceOf[AST.IR.Pattern.Literal] ||
              c.pattern.isInstanceOf[AST.IR.Pattern.Wildcard]))) {
            val values = Buffer.create[Option[AST.IR.Exp]]()
            val bodies = Buffer.create[AST.IR.Stmt.Block]()
            for (c <- stmt.cases) {
              values.append(scalarSwitchCaseValue(c.pattern))
              bodies.append(c.body)
            }
            return switchToBasic(label, stmt.exp, values.toIS, bodies.toIS, pos, blocksBuf)
          } else {
            return stmtToBasic(label, simplifyMatch(stmt), blocksBuf)
          }
        case stmt: AST.IR.Stmt.Switch =>
          val values = Buffer.create[Option[AST.IR.Exp]]()
          val bodies = Buffer.create[AST.IR.Stmt.Block]()
          for (c <- stmt.cases) {
            values.append(c.valueOpt)
            bodies.append(c.body)
          }
          return switchToBasic(label, stmt.exp, values.toIS, bodies.toIS, pos, blocksBuf)
        case stmt: AST.IR.Stmt.AssignPattern =>
          return stmtToBasic(label, simplifyAssignPattern(stmt), blocksBuf)
        case stmt: AST.IR.Stmt.For => halt(s"TODO: $stmt")
        case stmt: AST.IR.Stmt.If =>
          val t = fresh.label()
          val e = fresh.label()
          val f: Z = if (stmt.elseBlock.stmts.isEmpty) e else fresh.label()
          blocksBuf.append(basicBlock(label, grounds, AST.IR.Jump.If(stmt.cond, t, f, stmt.pos)))
          grounds = ISZ()
          var allReturn = T
          blockToBasic(t, stmt.thenBlock, blocksBuf) match {
            case Some(l) =>
              blocksBuf.append(basicBlock(l, grounds, AST.IR.Jump.Goto(e, stmt.pos)))
              allReturn = F
            case _ =>
          }
          if (stmt.elseBlock.stmts.nonEmpty) {
            grounds = ISZ()
            blockToBasic(f, stmt.elseBlock, blocksBuf) match {
              case Some(l) =>
                blocksBuf.append(basicBlock(l, grounds, AST.IR.Jump.Goto(e, stmt.pos)))
                allReturn = F
              case _ =>
            }
          } else {
            allReturn = F
          }
          grounds = ISZ()
          return if (allReturn) None() else Some(e)
        case stmt: AST.IR.Stmt.While =>
          val n = fresh.label()
          blocksBuf.append(basicBlock(label, grounds, AST.IR.Jump.Goto(n, stmt.pos)))
          grounds = ISZ()
          blockToBasic(n, AST.IR.Stmt.Block(stmt.cond.stmts, stmt.cond.exp.pos), blocksBuf) match {
            case Some(l) =>
              val t = fresh.label()
              val e = fresh.label()
              blocksBuf.append(basicBlock(l, grounds, AST.IR.Jump.If(stmt.cond.exp, t, e, stmt.pos)))
              grounds = ISZ()
              blockToBasic(t, stmt.block, blocksBuf) match {
                case Some(l2) => blocksBuf.append(basicBlock(l2, grounds, AST.IR.Jump.Goto(n, stmt.pos)))
                case _ =>
              }
              grounds = ISZ()
              return Some(e)
            case _ =>
              return None()
          }
        case stmt: AST.IR.Stmt.Return =>
          blocksBuf.append(basicBlock(label, grounds, AST.IR.Jump.Return(stmt.expOpt, pos)))
          grounds = ISZ()
          return None()
        case stmt: AST.IR.Stmt.Intrinsic =>
          addGround(stmt)
          return Some(label)
      }
    }

    def switchToBasic(label: Z,
                      exp: AST.IR.Exp,
                      values: ISZ[Option[AST.IR.Exp]],
                      bodies: ISZ[AST.IR.Stmt.Block],
                      switchPos: message.Position,
                      blocksBuf: Buffer[AST.IR.BasicBlock]): Option[Z] = {
      val labels: ISZ[Z] = for (_ <- values.indices) yield fresh.label()
      val end = fresh.label()
      val cases = Buffer.create[AST.IR.Jump.Switch.Case]()
      var defaultOpt = Option.none[Z]()
      for (i <- labels.indices) {
        values(i) match {
          case Some(caseExp) => cases.append(AST.IR.Jump.Switch.Case(caseExp, labels(i)))
          case _ => defaultOpt = Some(labels(i))
        }
      }
      blocksBuf.append(AST.IR.BasicBlock(label, grounds,
        AST.IR.Jump.Switch(exp, cases.toIS, defaultOpt, switchPos)))
      for (i <- labels.indices) {
        grounds = ISZ()
        stmtToBasic(labels(i), bodies(i), blocksBuf) match {
          case Some(l) => blocksBuf.append(AST.IR.BasicBlock(l, grounds, AST.IR.Jump.Goto(end, switchPos)))
          case _ =>
        }
      }
      grounds = ISZ()
      return Some(end)
    }

    def blockToBasic(label: Z, block: AST.IR.Stmt.Block, blocksBuf: Buffer[AST.IR.BasicBlock]): Option[Z] = {
      val oldDecls = decls
      decls = ISZ()
      var l = label
      for (stmt <- block.stmts) {
        stmtToBasic(l, stmt, blocksBuf) match {
          case Some(next) => l = next
          case _ =>
            decls = oldDecls
            return None()
        }
      }
      for (d <- decls) {
        addGround(d.undeclare)
      }
      decls = oldDecls
      return Some(l)
    }

    blockToBasic(initLabel, body.block, blocks) match {
      case Some(l) => blocks.append(basicBlock(l, grounds, AST.IR.Jump.Return(None(), pos)))
      case _ =>
    }
    return AST.IR.Body.Basic(blocks.toIS)
  }

  def translateExpBlock(exp: AST.Exp): AST.IR.ExpBlock = {
    val oldStmts = stmts
    stmts = ISZ()
    val e = translateExp(exp)
    val r = AST.IR.ExpBlock(stmts, e)
    resetTemp()
    stmts = oldStmts
    return r
  }

  def translateStmt(stmt: AST.Stmt, localOpt: Option[(String, AST.Typed)]): Unit = {
    def assignRhs(lhsType: AST.Typed, rhs: AST.AssignExp): AST.IR.Exp = {
      rhs match {
        case rhs: AST.Stmt.Expr => return translateExp(rhs.exp)
        case _ =>
          val aePos = rhs.asStmt.posOpt.get
          val prefix: String = rhs match {
            case _: AST.Stmt.Match => "match$"
            case _ => ""
          }
          val id = assignExpId(prefix, None(), aePos)
          stmts = stmts :+ AST.IR.Stmt.Decl(F, T, F, methodContext, ISZ(AST.IR.Stmt.Decl.Local(id, lhsType)), aePos)
          translateAssignExp(rhs, (id, lhsType))
          return AST.IR.Exp.LocalVarRef(T, methodContext, id, lhsType, aePos)
      }
    }
    val pos = stmt.posOpt.get
    stmt match {
      case _: AST.Stmt.Spec if !spec => return
      case stmt: AST.Stmt.Var =>
        if (stmt.isSpec && !spec) {
          return
        }
        val init = stmt.initOpt.get
        var oldStmts = stmts
        stmts = ISZ()
        val t = stmt.attr.typedOpt.get
        val varRhs: AST.IR.Exp = init match {
          case init: AST.Stmt.Expr => translateExp(init.exp)
          case _ =>
            val aePos = init.asStmt.posOpt.get
            val prefix: String = init match {
              case _: AST.Stmt.Match => "match$"
              case _ => ""
            }
            val id = assignExpId(prefix, Some(stmt.id.value), aePos)
            stmts = stmts :+ AST.IR.Stmt.Decl(F, T, F, methodContext, ISZ(AST.IR.Stmt.Decl.Local(id, t)), aePos)
            translateAssignExp(init, (id, t))
            AST.IR.Exp.LocalVarRef(T, methodContext, id, t, aePos)
        }
        if (varCaptureSet.contains(stmt.id.value)) {
          val mt = loweredMBoxType(t)
          stmts = stmts :+ AST.IR.Stmt.Assign.Local(methodContext, stmt.id.value, mt,
            AST.IR.Exp.Construct(mt, AST.Typed.emptyRTypes, ISZ(varRhs), pos), pos)
          oldStmts = oldStmts :+ AST.IR.Stmt.Decl(F, T, F, methodContext,
            ISZ(AST.IR.Stmt.Decl.Local(stmt.id.value, mt)), pos)
        } else {
          stmts = stmts :+ AST.IR.Stmt.Assign.Local(methodContext, stmt.id.value, t, varRhs, pos)
          oldStmts = oldStmts :+ AST.IR.Stmt.Decl(F, stmt.isVal, F, methodContext,
            ISZ(AST.IR.Stmt.Decl.Local(stmt.id.value, t)), pos)
        }
        stmts = oldStmts ++ stmts
        resetTemp()
      case stmt: AST.Stmt.Assign =>
        val oldStmts = stmts
        stmts = ISZ()
        stmt.lhs match {
          case lhs: AST.Exp.Ident =>
            lhs.resOpt.get match {
              case _: AST.ResolvedInfo.LocalVar =>
                val rhs = assignRhs(lhs.typedOpt.get, stmt.rhs)
                if (varCaptureSet.contains(lhs.id.value)) {
                  val t = lhs.typedOpt.get
                  val valueT = lowerByNameType(t)
                  val mt = mboxType(valueT)
                  val mboxRef = AST.IR.Exp.LocalVarRef(T, methodContext, lhs.id.value, mt, pos)
                  stmts = stmts :+ AST.IR.Stmt.Assign.Field(mboxRef, "value", valueT, rhs, pos)
                } else {
                  stmts = stmts :+ AST.IR.Stmt.Assign.Local(methodContext, lhs.id.value, lhs.typedOpt.get, rhs, pos)
                }
              case res: AST.ResolvedInfo.Var =>
                if (res.isInObject) {
                  val rhs = assignRhs(lhs.typedOpt.get, stmt.rhs)
                  stmts = stmts :+ AST.IR.Stmt.Assign.Global(res.owner :+ res.id, lhs.typedOpt.get, rhs, pos)
                } else {
                  val receiverPos = lhs.posOpt.get
                  val thizExp = thiz(receiverPos)
                  val receiver: AST.IR.Exp = if (threeAddressCode) {
                    val n = fresh.temp()
                    stmts = stmts :+ AST.IR.Stmt.Assign.Temp(n, thizExp, receiverPos)
                    AST.IR.Exp.Temp(n, thizExp.tipe, receiverPos)
                  } else {
                    thizExp
                  }
                  val rhs = assignRhs(lhs.typedOpt.get, stmt.rhs)
                  stmts = stmts :+ AST.IR.Stmt.Assign.Field(receiver, lhs.id.value, lhs.typedOpt.get, rhs, pos)
                }
              case res => halt(s"Infeasible: $res")
            }
          case lhs: AST.Exp.Select =>
            def selectRhs(): AST.IR.Exp = {
              stmt.rhs match {
                case rhs: AST.Stmt.Expr => return translateExp(rhs.exp)
                case _ =>
                  val aePos = stmt.rhs.asStmt.posOpt.get
                  val prefix: String = stmt.rhs match {
                    case _: AST.Stmt.Match => "match$"
                    case _ => ""
                  }
                  val id = assignExpId(prefix, None(), aePos)
                  val t = stmt.lhs.typedOpt.get
                  stmts = stmts :+ AST.IR.Stmt.Decl(F, T, F, methodContext, ISZ(AST.IR.Stmt.Decl.Local(id, t)), aePos)
                  translateAssignExp(stmt.rhs, (id, t))
                  return AST.IR.Exp.LocalVarRef(T, methodContext, id, t, aePos)
              }
            }

            lhs.resOpt.get match {
              case res: AST.ResolvedInfo.Var if res.isInObject =>
                val rhs = selectRhs()
                stmts = stmts :+ AST.IR.Stmt.Assign.Global(res.owner :+ res.id, lhs.typedOpt.get, rhs, pos)
              case _ =>
                val receiver: AST.IR.Exp = lhs.receiverOpt match {
                  case Some(rcv) => translateExp(rcv)
                  case _ => thiz(pos)
                }
                val rhs = selectRhs()
                stmts = stmts :+ AST.IR.Stmt.Assign.Field(receiver, lhs.id.value, lhs.typedOpt.get, rhs, pos)
            }
          case lhs: AST.Exp.Invoke =>
            val rcv = lhs.receiverOpt.get
            val receiver = translateExp(rcv)
            val index = translateExp(lhs.args(0))
            val invokeRhs: AST.IR.Exp = stmt.rhs match {
              case rhs: AST.Stmt.Expr => translateExp(rhs.exp)
              case _ =>
                val aePos = stmt.rhs.asStmt.posOpt.get
                val prefix: String = stmt.rhs match {
                  case _: AST.Stmt.Match => "match$"
                  case _ => ""
                }
                val id = assignExpId(prefix, None(), aePos)
                val t = stmt.lhs.typedOpt.get
                stmts = stmts :+ AST.IR.Stmt.Decl(F, T, F, methodContext, ISZ(AST.IR.Stmt.Decl.Local(id, t)), aePos)
                translateAssignExp(stmt.rhs, (id, t))
                AST.IR.Exp.LocalVarRef(T, methodContext, id, t, aePos)
            }
            stmts = stmts :+ AST.IR.Stmt.Assign.Index(receiver, index, invokeRhs, pos)
          case _ => halt("Infeasible")
        }
        stmts = oldStmts ++ stmts
        resetTemp()
      case stmt: AST.Stmt.If =>
        val oldStmts = stmts
        stmts = ISZ()
        val cond = translateExp(stmt.cond)
        val condStmts = stmts
        resetTemp()
        stmts = ISZ()
        translateBody(stmt.thenBody, localOpt)
        val thenPos = bodyPos(stmt.thenBody, pos)
        val thenStmts = stmts
        resetTemp()
        stmts = ISZ()
        translateBody(stmt.elseBody, localOpt)
        val elsePos = bodyPos(stmt.elseBody, pos)
        val elseStmts = stmts
        stmts = oldStmts ++ condStmts :+
          AST.IR.Stmt.If(cond, AST.IR.Stmt.Block(thenStmts, thenPos), AST.IR.Stmt.Block(elseStmts, elsePos), pos)
        resetTemp()
      case stmt: AST.Stmt.While =>
        val cond = translateExpBlock(stmt.cond)
        val oldStmts = stmts
        stmts = ISZ()
        translateBody(stmt.body, None())
        val bPos = bodyPos(stmt.body, pos)
        stmts = oldStmts :+ AST.IR.Stmt.While(cond, AST.IR.Stmt.Block(stmts, bPos), pos)
        resetTemp()
      case stmt: AST.Stmt.Expr =>
        stmt.exp match {
          case exp: AST.Exp.Tuple if exp.args.isEmpty =>
            resetTemp()
            return
          case _ =>
        }
        stmt.exp match {
          case e: AST.Exp.Invoke =>
            var isPrint: B = F
            var isLine: B = F
            var printKind: AST.IR.Stmt.Print.Kind.Type = AST.IR.Stmt.Print.Kind.Out
            var isAssert: B = F
            var isAssume: B = F
            var isHalt: B = F
            e.ident.attr.resOpt.get match {
              case AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Print) =>
                isPrint = T
              case AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Println) =>
                isPrint = T
                isLine = T
              case AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Eprint) =>
                isPrint = T
                printKind = AST.IR.Stmt.Print.Kind.Err
              case AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Eprintln) =>
                isPrint = T
                isLine = T
                printKind = AST.IR.Stmt.Print.Kind.Err
              case AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Cprint) =>
                isPrint = T
                printKind = AST.IR.Stmt.Print.Kind.OutErr
              case AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Cprintln) =>
                isPrint = T
                isLine = T
                printKind = AST.IR.Stmt.Print.Kind.OutErr
              case AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Assert) =>
                isAssert = T
              case AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.AssertMsg) =>
                isAssert = T
              case AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Assume) =>
                isAssume = T
              case AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.AssumeMsg) =>
                isAssume = T
              case AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Halt) =>
                isHalt = T
              case _ =>
            }
            if (isPrint) {
              var args = ISZ[AST.IR.Exp]()
              for (j <- e.args.indices) {
                val arg = translateExp(e.args(j))
                args = args :+ arg
              }
              stmts = stmts :+ AST.IR.Stmt.Print(printKind, isLine, args, pos)
              resetTemp()
              return
            } else if (isAssert || isAssume) {
              val cond = translateExp(e.args(0))
              resetTemp()
              val messageOpt: Option[AST.IR.ExpBlock] =
                if (e.args.size == 2) Some(translateExpBlock(e.args(1)))
                else None()
              stmts = stmts :+ AST.IR.Stmt.Assertume(isAssert, cond, messageOpt, pos)
              resetTemp()
              return
            } else if (isHalt) {
              val msg = translateExp(e.args(0))
              stmts = stmts :+ AST.IR.Stmt.Halt(msg, pos)
              resetTemp()
              return
            }
          case _ =>
        }
        val e = translateExp(stmt.exp)
        if (e.tipe == AST.Typed.unit || e.tipe == AST.Typed.nothing || norm3AC(e) == e) {
          e match {
            case applyExp: AST.IR.Exp.Apply =>
              stmts = stmts :+ AST.IR.Stmt.Expr(applyExp)
            case _ =>
              // Non-Apply void expression (e.g., ApplyClosure): assign to discarded temp
              stmts = stmts :+ AST.IR.Stmt.Assign.Temp(fresh.temp(), e, pos)
          }
        } else {
          halt("Infeasible")
        }
        resetTemp()
      case stmt: AST.Stmt.Return =>
        stmt.expOpt match {
          case Some(exp: AST.Exp.Tuple) if exp.args.isEmpty =>
            stmts = stmts :+ AST.IR.Stmt.Return(None(), pos)
          case Some(exp) =>
            val r = translateExp(exp)
            stmts = stmts :+ AST.IR.Stmt.Return(Some(r), pos)
          case _ =>
            stmts = stmts :+ AST.IR.Stmt.Return(None(), pos)
        }
        resetTemp()
      case stmt: AST.Stmt.Block =>
        val oldStmts = stmts
        stmts = ISZ()
        translateBody(stmt.body, localOpt)
        stmts = oldStmts :+ AST.IR.Stmt.Block(stmts, stmt.posOpt.get)
        resetTemp()
      case stmt: AST.Stmt.Match =>
        val exp = translateExp(stmt.exp)
        resetTemp()
        var cases = ISZ[AST.IR.Stmt.Match.Case]()
        val oldStmts = stmts
        for (c <- stmt.cases) {
          stmts = ISZ()
          val pattern = resolvePattern(c.pattern)
          val decl = patternDecl(methodContext, pattern)
          c.condOpt match {
            case Some(cond) =>
              val condExp = translateExpBlock(cond)
              translateBody(c.body, localOpt)
              val block = AST.IR.Stmt.Block(stmts, pos)
              resetTemp()
              cases = cases :+ AST.IR.Stmt.Match.Case(decl, pattern, Some(condExp), block)
            case _ =>
              translateBody(c.body, localOpt)
              val block = AST.IR.Stmt.Block(stmts, stmt.posOpt.get)
              resetTemp()
              cases = cases :+ AST.IR.Stmt.Match.Case(decl, pattern, None(), block)
          }
        }
        val matchStmt = AST.IR.Stmt.Match(exp, cases, pos)
        var hasValueRef = F
        for (c <- cases if !hasValueRef) {
          hasValueRef = hasValueRefPattern(c.pattern)
        }
        if (hasValueRef) {
          val id = assignExpId("$pattern.", None(), pos)
          val value = AST.IR.Exp.LocalVarRef(T, methodContext, id, exp.tipe, pos)
          val body = simplifyMatch(matchStmt(exp = value))
          stmts = oldStmts ++ ISZ[AST.IR.Stmt](
            AST.IR.Stmt.Decl(F, T, F, methodContext,
              ISZ(AST.IR.Stmt.Decl.Local(id, exp.tipe)), pos),
            AST.IR.Stmt.Assign.Local(methodContext, id, exp.tipe, exp, pos),
            body)
        } else {
          stmts = oldStmts :+ matchStmt
        }
      case stmt: AST.Stmt.For =>
        val fPos = stmt.posOpt.get
        def translateForEnumGen(i: Z): AST.IR.Stmt.For = {
          val enumGen = stmt.enumGens(i)
          val idOpt: Option[String] = enumGen.idOpt match {
            case Some(id) => Some(id.value)
            case _ => None()
          }
          val range: AST.IR.Stmt.For.Range = enumGen.range match {
            case r: AST.EnumGen.Range.Expr =>
              val rangeExp = translateExp(r.exp)
              AST.IR.Stmt.For.Range.Expr(rangeExp, r.attr.posOpt.getOrElse(fPos))
            case r: AST.EnumGen.Range.Step =>
              val rPos = r.attr.posOpt.getOrElse(fPos)
              val start = translateExp(r.start)
              val end = translateExp(r.end)
              val byOpt: Option[AST.IR.Exp] = r.byOpt match {
                case Some(by) => Some(translateExp(by))
                case _ => None()
              }
              AST.IR.Stmt.For.Range.Step(r.isInclusive, start, end, byOpt, rPos)
          }
          val condOpt: Option[AST.IR.ExpBlock] = enumGen.condOpt match {
            case Some(cond) => Some(translateExpBlock(cond))
            case _ => None()
          }
          val innerBlock: AST.IR.Stmt.Block = if (i < stmt.enumGens.size - 1) {
            val oldStmts2 = stmts
            stmts = ISZ()
            val nested = translateForEnumGen(i + 1)
            val nestedStmts = stmts :+ nested
            stmts = oldStmts2
            resetTemp()
            AST.IR.Stmt.Block(nestedStmts, fPos)
          } else {
            val oldStmts2 = stmts
            stmts = ISZ()
            translateBody(stmt.body, None())
            val bodyStmts = stmts
            stmts = oldStmts2
            resetTemp()
            AST.IR.Stmt.Block(bodyStmts, fPos)
          }
          return AST.IR.Stmt.For(methodContext, idOpt, range, condOpt, innerBlock, fPos)
        }
        val forStmt = translateForEnumGen(0)
        stmts = stmts :+ forStmt
        resetTemp()
      case stmt: AST.Stmt.VarPattern =>
        val oldStmts = stmts
        stmts = ISZ()
        val init = assignRhs(stmt.pattern.typedOpt.get, stmt.init)
        val pattern = resolvePattern(stmt.pattern)
        val initId = assignExpId("$pattern.", None(), pos)
        val initType = init.tipe
        stmts = stmts :+ AST.IR.Stmt.Decl(F, T, F, methodContext,
          ISZ(AST.IR.Stmt.Decl.Local(initId, initType)), pos)
        stmts = stmts :+ AST.IR.Stmt.Assign.Local(methodContext, initId, initType,
          init, pos)
        val patternInit = AST.IR.Exp.LocalVarRef(T, methodContext, initId, initType, pos)
        stmts = stmts :+ patternDecl(methodContext, pattern)
        val (_, lMap) = translatePattern(patternInit, pattern, HashSMap.empty)
        for (e <- lMap.entries) {
          stmts = stmts :+ AST.IR.Stmt.Assign.Local(methodContext, e._1._2, e._2.tipe, e._2, e._2.pos)
        }
        stmts = oldStmts ++ stmts
      case _: AST.Stmt.SubZ => // skip
      case stmt: AST.Stmt.Method =>
        stmt.bodyOpt match {
          case Some(body) =>
            val res = stmt.attr.resOpt.get.asInstanceOf[AST.ResolvedInfo.Method]
            val nestedKey = res.owner :+ res.id

            // Collect captures from the body
            val collector = IRTranslator.ClosureCaptureCollector(HashSMap.empty, F)
            collector.transformBody(body)
            val captures = collector.captures.values
            // Filter out captures from the nested method's own or descendant scopes.
            if (!nestedMethodCaptures.contains(nestedKey)) {
              registerNestedMethods(AST.Body(ISZ(stmt), ISZ()))
            }
            val captureList = nestedMethodCaptures.get(nestedKey).get

            // Build capture param names and types
            val captureParamNames: ISZ[String] = for (c <- captureList) yield c._2
            val captureParamTypes: ISZ[AST.Typed] = for (c <- captureList) yield
              if (c._2 != "this" && (!c._1 || varCaptureSet.contains(c._2))) loweredMBoxType(c._3)
              else lowerByNameType(c._3)

            // Original param names and funType
            val typeParams: ISZ[String] = for (tp <- stmt.sig.typeParams) yield tp.id.value
            val origParamNames: ISZ[String] = for (p <- stmt.sig.params) yield p.id.value
            val origFunType = stmt.sig.funType
            val liftedTypeParams = addCaptureTypeParams(typeParams, captureParamTypes)

            // Lifted procedure has captures as extra leading params
            val liftedParamNames = captureParamNames ++ origParamNames
            val liftedFunType = lowerByNameFunType(origFunType(args = captureParamTypes ++ origFunType.args))

            // Save state
            val savedMethodContext = methodContext
            val savedStmts = stmts
            val savedVarCaptureSet = varCaptureSet
            val savedNestedMethodCaptures = nestedMethodCaptures
            val savedNestedMethodCaptureInfo = nestedMethodCaptureInfo
            val savedCapturedThisTypeOpt = capturedThisTypeOpt
            val savedCurrentThisExpOpt = currentThisExpOpt

            // Set up new methodContext for the nested method
            // Owner is the enclosing type/package FQN (methodContext.owner), not local scope
            val liftedOwner = methodContext.owner
            val liftedId = liftedNestedMethodId(res)
            methodContext = AST.IR.MethodContext(T, liftedOwner, liftedId, liftedFunType)
            varCaptureSet = HashSet.empty
            capturedThisTypeOpt = captureThisTypeOpt(captureList)
            currentThisExpOpt = None()

            // Check for var captures that need MBox wrapping in the nested method's body
            for (capture <- captures if capture._1 == nestedKey && !capture._2) {
              varCaptureSet = varCaptureSet + capture._3
            }
            for (capture <- captureList if capture._2 != "this" && savedVarCaptureSet.contains(capture._2)) {
              varCaptureSet = varCaptureSet + capture._2
            }

            // Translate the body
            stmts = ISZ()
            translateBody(body, None())
            val irBody = AST.IR.Body.Block(AST.IR.Stmt.Block(stmts, pos))

            val liftedProc = AST.IR.Procedure(
              isInObject = T,
              rTypeParams = ISZ(),
              typeParams = liftedTypeParams,
              owner = liftedOwner,
              id = liftedId,
              paramNames = liftedParamNames,
              tipe = liftedFunType,
              body = irBody,
              pos = pos)
            liftedProcedures = liftedProcedures :+ liftedProc

            // Restore state
            methodContext = savedMethodContext
            stmts = savedStmts
            varCaptureSet = savedVarCaptureSet
            nestedMethodCaptures = savedNestedMethodCaptures
            nestedMethodCaptureInfo = savedNestedMethodCaptureInfo
            capturedThisTypeOpt = savedCapturedThisTypeOpt
            currentThisExpOpt = savedCurrentThisExpOpt
          case _ => // abstract nested method — skip
        }
      case _: AST.Stmt.ExtMethod => // skip
      case _: AST.Stmt.Enum => // skip
      case _: AST.Stmt.Sig => // skip
      case _: AST.Stmt.Adt => // skip
      case _: AST.Stmt.Object => // skip
      case _: AST.Stmt.Import => // skip
      case _: AST.Stmt.TypeAlias => // skip
      case _: AST.Stmt.Spec => // skip
    }

  }

  def patternLiteralExp(lit: AST.Lit): AST.IR.Exp = {
    lit match {
      case lit: AST.Exp.LitB => return AST.IR.Exp.Bool(lit.value, lit.posOpt.get)
      case lit: AST.Exp.LitC => return AST.IR.Exp.Int(AST.Typed.c, lit.value.toZ, lit.posOpt.get)
      case lit: AST.Exp.LitZ => return AST.IR.Exp.Int(AST.Typed.z, lit.value, lit.posOpt.get)
      case lit: AST.Exp.LitF32 => return AST.IR.Exp.F32(lit.value, lit.posOpt.get)
      case lit: AST.Exp.LitF64 => return AST.IR.Exp.F64(lit.value, lit.posOpt.get)
      case lit: AST.Exp.LitR => return AST.IR.Exp.R(lit.value, lit.posOpt.get)
      case lit: AST.Exp.LitString => return AST.IR.Exp.String(lit.value, lit.posOpt.get)
      case _ => halt(s"Infeasible pattern literal: $lit")
    }
  }

  def patternInterpolateExp(pattern: AST.Pattern.LitInterpolate): AST.IR.Exp = {
    val pos = pattern.posOpt.get
    val t = pattern.attr.typedOpt.get
    pattern.prefix match {
      case string"string" => return AST.IR.Exp.String(pattern.value, pos)
      case string"c" => return AST.IR.Exp.Int(AST.Typed.c, conversions.String.toCis(pattern.value)(0).toZ, pos)
      case string"z" => return AST.IR.Exp.Int(AST.Typed.z, Z(pattern.value).get, pos)
      case string"f32" => return AST.IR.Exp.F32(F32(pattern.value).get, pos)
      case string"f64" => return AST.IR.Exp.F64(F64(pattern.value).get, pos)
      case string"r" => return AST.IR.Exp.R(R(pattern.value).get, pos)
      case _ if isSubZ(t) => return AST.IR.Exp.Int(t, Z(pattern.value).get, pos)
      case _ => halt(s"Infeasible pattern interpolation: $pattern")
    }
  }

  def resolvePattern(pattern: AST.Pattern): AST.IR.Pattern = {
    val pos = pattern.posOpt.get
    pattern match {
      case p: AST.Pattern.Literal =>
        return AST.IR.Pattern.Literal(patternLiteralExp(p.lit))
      case p: AST.Pattern.LitInterpolate =>
        return AST.IR.Pattern.Literal(patternInterpolateExp(p))
      case p: AST.Pattern.Wildcard =>
        val guardTipeOpt: Option[AST.Typed] = p.typeOpt match {
          case Some(t) => Some(t.typedOpt.get)
          case _ => None()
        }
        return AST.IR.Pattern.Wildcard(guardTipeOpt, p.typedOpt.get, pos)
      case p: AST.Pattern.SeqWildcard =>
        return AST.IR.Pattern.SeqWildcard(p.typedOpt.get, pos)
      case p: AST.Pattern.VarBinding =>
        val guardTipeOpt: Option[AST.Typed] = p.tipeOpt match {
          case Some(t) => Some(t.typedOpt.get)
          case _ => None()
        }
        return AST.IR.Pattern.VarBinding(p.id.value, guardTipeOpt, p.typedOpt.get, p.idContext, pos)
      case p: AST.Pattern.Structure =>
        val patterns = Buffer.create[AST.IR.Pattern]()
        for (sub <- p.patterns) {
          patterns.append(resolvePattern(sub))
        }
        val idOpt: Option[String] = p.idOpt match {
          case Some(id) => Some(id.value)
          case _ => None()
        }
        return AST.IR.Pattern.Structure(idOpt, p.typedOpt.get, patterns.toIS, p.idContext, pos)
      case p: AST.Pattern.Ref =>
        val t = p.typedOpt.get
        p.attr.resOpt match {
          case Some(res: AST.ResolvedInfo.LocalVar) =>
            return AST.IR.Pattern.LocalRef(res.isVal, res.id, t, pos)
          case Some(res: AST.ResolvedInfo.Var) if res.isInObject =>
            return AST.IR.Pattern.GlobalRef(res.owner, res.id, t, pos)
          case Some(res: AST.ResolvedInfo.Var) =>
            return AST.IR.Pattern.FieldRef(res.id, t, pos)
          case Some(res: AST.ResolvedInfo.EnumElement) =>
            return AST.IR.Pattern.EnumElementRef(res.owner, res.name, res.ordinal, t, pos)
          case _ =>
            halt(s"Infeasible pattern reference: $pattern")
        }
    }
  }

  def patternDecl(context: AST.IR.MethodContext, pattern: AST.IR.Pattern): AST.IR.Stmt.Decl = {
    val r = Buffer.create[AST.IR.Stmt.Decl.Local]()
    def rec(p: AST.IR.Pattern): Unit = {
      p match {
        case p: AST.IR.Pattern.VarBinding => r.append(AST.IR.Stmt.Decl.Local(p.id, p.tipe))
        case p: AST.IR.Pattern.Structure =>
          p.idOpt match {
            case Some(id) => r.append(AST.IR.Stmt.Decl.Local(id, p.tipe))
            case _ =>
          }
          for (sub <- p.patterns) {
            rec(sub)
          }
        case _: AST.IR.Pattern.LocalRef =>
        case _: AST.IR.Pattern.FieldRef =>
        case _: AST.IR.Pattern.GlobalRef =>
        case _: AST.IR.Pattern.EnumElementRef =>
        case _: AST.IR.Pattern.Literal =>
        case _: AST.IR.Pattern.Wildcard =>
        case _: AST.IR.Pattern.SeqWildcard =>
      }
    }
    rec(pattern)
    return AST.IR.Stmt.Decl(F, T, F, context, r.toIS, pattern.pos)
  }

  @pure def bodyPos(body: AST.Body, default: message.Position): message.Position = {
    if (body.stmts.isEmpty) {
      return default
    }
    return body.stmts(0).posOpt.get.to(body.stmts(body.stmts.size - 1).posOpt.get)
  }

  def translateBody(body: AST.Body, localOpt: Option[(String, AST.Typed)]): Unit = {
    registerNestedMethods(body)
    val stmts = body.stmts
    localOpt match {
      case Some((_, _)) =>
        for (i <- 0 until stmts.size - 1) {
          translateStmt(stmts(i), None())
        }
        translateAssignExp(stmts(stmts.size - 1).asAssignExp, localOpt.get)
      case _ =>
        for (stmt <- body.stmts) {
          translateStmt(stmt, None())
        }
    }
  }

  @pure def hasValueRefPattern(pattern: AST.IR.Pattern): B = {
    pattern match {
      case _: AST.IR.Pattern.LocalRef => return T
      case _: AST.IR.Pattern.FieldRef => return T
      case _: AST.IR.Pattern.GlobalRef => return T
      case p: AST.IR.Pattern.Structure =>
        for (sub <- p.patterns) {
          if (hasValueRefPattern(sub)) {
            return T
          }
        }
      case _ =>
    }
    return F
  }

  def registerNestedMethods(body: AST.Body): Unit = {
    val methods = Buffer.create[ISZ[String]]()
    var calls = HashMap.empty[ISZ[String], ISZ[ISZ[String]]]
    val declarationCollector = IRTranslator.NestedMethodDeclarationCollector(
      Buffer.create[AST.Stmt.Method](), HashSSet.empty[ISZ[String]])
    declarationCollector.transformBody(body)
    for (m <- declarationCollector.methods.toIS) {
      val res = m.attr.resOpt.get.asInstanceOf[AST.ResolvedInfo.Method]
      val key = res.owner :+ res.id
      val collector = IRTranslator.ClosureCaptureCollector(HashSMap.empty, F)
      collector.transformBody(m.bodyOpt.get)
      val captures = prependThisCaptureInfo(
        for (c <- collector.captures.values if !isContextPrefix(key, c._1)) yield c,
        collector.capturesThis)
      methods.append(key)
      nestedMethodCaptureInfo = nestedMethodCaptureInfo + key ~> captures
      calls = calls + key ~> collectNestedMethodCalls(m.bodyOpt.get)
    }
    val keys = methods.toIS
    var changed = T
    while (changed) {
      changed = F
      for (key <- keys) {
        val oldCaptures = nestedMethodCaptureInfo.get(key).get
        val captures: ISZ[(ISZ[String], B, String, AST.Typed)] = for (c <- augmentNestedCaptureInfo(oldCaptures, calls.get(key).get)
          if !isContextPrefix(key, c._1)) yield c
        if (captures != oldCaptures) {
          nestedMethodCaptureInfo = nestedMethodCaptureInfo + key ~> captures
          changed = T
        }
      }
    }
    for (key <- keys) {
      val captures: ISZ[(B, String, AST.Typed)] = for (c <- nestedMethodCaptureInfo.get(key).get) yield (c._2, c._3, c._4)
      nestedMethodCaptures = nestedMethodCaptures + key ~> captures
    }
  }

  @strictpure def isHalt(stmt: AST.Stmt.Expr): B = stmt match {
    case AST.Stmt.Expr(e: AST.Exp.Invoke, _) =>
      e.attr.resOpt.get match {
        case res: AST.ResolvedInfo.BuiltIn if res.kind == AST.ResolvedInfo.BuiltIn.Kind.Halt => T
        case _ => F
      }
    case _ => F
  }

  def translateAssignExp(stmt: AST.AssignExp, local: (String, AST.Typed)): Unit = {
    val pos = stmt.asStmt.posOpt.get
    stmt match {
      case stmt: AST.Stmt.Expr =>
        if (isHalt(stmt)) {
          translateStmt(stmt, Some(local))
          return
        }
        val exp = translateExp(stmt.exp)
        stmts = stmts :+ AST.IR.Stmt.Assign.Local(methodContext, local._1, local._2, exp, pos)
      case _ => translateStmt(stmt.asStmt, Some(local))
    }
  }

  @memoize def isSubZ(t: AST.Typed): B = {
    t match {
      case t: AST.Typed.Name if t.args.isEmpty =>
        th.typeMap.get(t.ids) match {
          case Some(_: TypeInfo.SubZ) => return T
          case _ =>
        }
      case _ =>
    }
    return F
  }

  @pure def isSubZWithPatternFacts(t: AST.Typed,
                                   patternFactsOpt: Option[IRTranslator.PatternDeclFacts]): B = {
    patternFactsOpt match {
      case Some(patternFacts) =>
        t match {
          case tn: AST.Typed.Name if tn.args.isEmpty =>
            patternFacts.owners.get(tn.ids) match {
              case Some(owner) => return owner.kind == IRTranslator.PatternOwnerKind.SubZ
              case _ =>
            }
          case _ =>
        }
        return F
      case _ => return isSubZ(t)
    }
  }

  @memoize def isScalar(t: AST.Typed): B = {
    return isScalarWithPatternFacts(t, None())
  }

  @pure def isScalarWithPatternFacts(t: AST.Typed,
                                     patternFactsOpt: Option[IRTranslator.PatternDeclFacts]): B = {
    t match {
      case AST.Typed.b =>
      case AST.Typed.c =>
      case AST.Typed.z =>
      case AST.Typed.f32 =>
      case AST.Typed.f64 =>
      case AST.Typed.r =>
      case _ => return isSubZWithPatternFacts(t, patternFactsOpt)
    }
    return T
  }

  @memoize def isSeq(t: AST.Typed): B = {
    t match {
      case t: AST.Typed.Name => return t.ids == AST.Typed.isName || t.ids == AST.Typed.msName
      case _ =>
    }
    return F
  }

  @memoize def shouldCopy(t: AST.Typed): B = {
    if (isScalar(t)) {
      return F
    }
    t match {
      case t: AST.Typed.Name =>
        t.ids match {
          case AST.Typed.msName => return T
          case AST.Typed.isName =>
          case _ =>
        }
        th.typeMap.get(t.ids) match {
          case Some(info: TypeInfo.Adt) => return !info.ast.isDatatype
          case Some(info: TypeInfo.Sig) => return !info.ast.isImmutable
          case _ =>
        }
      case _ =>
    }
    return F
  }

  def thiz(pos: message.Position): AST.IR.Exp = {
    currentThisExpOpt match {
      case Some(exp) => return norm3AC(exp)
      case _ =>
    }
    capturedThisTypeOpt match {
      case Some(t) => return norm3AC(AST.IR.Exp.LocalVarRef(T, methodContext, "this", t, pos))
      case _ =>
    }
    return norm3AC(AST.IR.Exp.LocalVarRef(T, methodContext, "this", methodContext.receiverType, pos))
  }

  def ownerTypedName(owner: ISZ[String]): AST.Typed = {
    val args: ISZ[AST.Typed] = th.typeMap.get(owner) match {
      case Some(info: TypeInfo.Adt) =>
        for (tp <- info.ast.typeParams) yield AST.Typed.TypeVar(tp.id.value, tp.kind)
      case Some(info: TypeInfo.Sig) =>
        for (tp <- info.ast.typeParams) yield AST.Typed.TypeVar(tp.id.value, tp.kind)
      case _ =>
        ISZ()
    }
    return AST.Typed.Name(owner, AST.Typed.noRType, args)
  }

  def liftedThiz(owner: ISZ[String], pos: message.Position): AST.IR.Exp = {
    return thiz(pos)
  }

  def liftedThizFromReceiver(receiver: AST.Exp, owner: ISZ[String], pos: message.Position): AST.IR.Exp = {
    receiver match {
      case _: AST.Exp.This => return liftedThiz(owner, pos)
      case _ => return translateExp(receiver)
    }
  }

  def norm3AC(r: AST.IR.Exp): AST.IR.Exp = {
    val e: AST.IR.Exp = r match {
      case r: AST.IR.Exp.GlobalVarRef =>
        if (r.name == AST.Typed.sireumName :+ "T") AST.IR.Exp.Bool(T, r.pos)
        else if (r.name == AST.Typed.sireumName :+ "F") AST.IR.Exp.Bool(F, r.pos)
        else r
      case _ => r
    }
    if (threeAddressCode && e.tipe != AST.Typed.unit && e.tipe != AST.Typed.nothing) {
      if (threeAddressExpF(e)) {
        val n = fresh.temp()
        stmts = stmts :+ AST.IR.Stmt.Assign.Temp(n, e, e.pos)
        return AST.IR.Exp.Temp(n, e.tipe, e.pos)
      }
    }
    return e
  }

  def collectCaptures(assignExp: AST.AssignExp): (ISZ[(ISZ[String], B, String, AST.Typed)], B) = {
    val collector = IRTranslator.ClosureCaptureCollector(HashSMap.empty, F)
    collector.transformAssignExp(assignExp)
    val calls = collectNestedMethodCallsAssignExp(assignExp)
    return (augmentNestedCaptureInfo(collector.captures.values, calls), collector.capturesThis)
  }

  @pure def isContextPrefix(prefix: ISZ[String], context: ISZ[String]): B = {
    if (prefix.size > context.size) {
      return F
    }
    var i: Z = 0
    while (i < prefix.size) {
      if (prefix(i) != context(i)) {
        return F
      }
      i = i + 1
    }
    return T
  }

  def translateMethodInvoke(res: AST.ResolvedInfo.Method,
                            receiverOpt: Option[AST.Exp],
                            expArgs: ISZ[AST.Exp],
                            namedIndicesOpt: Option[ISZ[Z]],
                            pos: message.Position): AST.IR.Exp = {
    val named = namedIndicesOpt.nonEmpty
    val oldStmts = stmts
    val argStmts = Buffer.create[AST.IR.Stmt]()
    if (named) {
      stmts = ISZ()
    }

    def snapshot(e: AST.IR.Exp): AST.IR.Exp = {
      for (stmt <- stmts) {
        argStmts.append(stmt)
      }
      stmts = ISZ()
      val n = fresh.temp()
      argStmts.append(AST.IR.Stmt.Assign.Temp(n, e, e.pos))
      return AST.IR.Exp.Temp(n, e.tipe, e.pos)
    }

    val args = Buffer.create[AST.IR.Exp]()
    val originalMethodType = res.tpeOpt.get
    var methodType = originalMethodType
    val isExt = res.mode == AST.MethodMode.Ext
    val nestedKey = res.owner :+ res.id
    val nestedCaptureListOpt: Option[ISZ[(B, String, AST.Typed)]] =
      if (isExt) None() else nestedMethodCaptures.get(nestedKey)
    var isSuper = F
    receiverOpt match {
      case Some(receiver: AST.Exp.Super) if !res.isInObject && !isExt =>
        val receiverExp = thiz(pos)
        args.append(if (named) snapshot(receiverExp) else receiverExp)
        methodType = methodType(args = lowerByNameType(receiver.typedOpt.get) +: methodType.args)
        isSuper = T
      case Some(receiver) if !res.isInObject && (isExt || nestedCaptureListOpt.isEmpty) =>
        val receiverExp = translateExp(receiver)
        args.append(if (named) snapshot(receiverExp) else receiverExp)
        methodType = methodType(args = lowerByNameType(receiver.typedOpt.get) +: methodType.args)
      case _ if !isExt && !res.isInObject && nestedCaptureListOpt.isEmpty =>
        val receiverExp = thiz(pos)
        args.append(if (named) snapshot(receiverExp) else receiverExp)
        methodType = methodType(args = lowerByNameType(receiverExp.tipe) +: methodType.args)
      case _ =>
    }

    // Check if this is a call to a nested method with captures
    var applyOwner = res.owner
    var applyIsInObject = res.isInObject
    var applyId = res.id
    nestedCaptureListOpt match {
      case Some(captureList) =>
        // Prepend capture LocalVarRef expressions to args
        val captureTypes: ISZ[AST.Typed] = for (c <- captureList) yield
          if (c._2 != "this" && (!c._1 || varCaptureSet.contains(c._2))) loweredMBoxType(c._3)
          else lowerByNameType(c._3)
        for (c <- captureList) {
          val captureExp = nestedCaptureExp(c, pos)
          args.append(if (named) snapshot(captureExp) else captureExp)
        }
        // Update method type to include capture types as leading params
        methodType = methodType(args = captureTypes ++ methodType.args)
        // The lifted procedure's owner is the enclosing type/package
        applyOwner = methodContext.owner
        applyIsInObject = T
        applyId = liftedNestedMethodId(res)
      case _ =>
    }
    namedIndicesOpt match {
      case Some(namedIndices) =>
        val namedArgs = MSZ.create[Option[AST.IR.Exp]](expArgs.size, None())
        for (i <- z"0" until expArgs.size) {
          val formalIndex = namedIndices(i)
          val formalT = originalMethodType.args(formalIndex)
          val arg: AST.IR.Exp = byNameValueTypeOpt(formalT) match {
            case Some(_) => makeByNameClosure(expArgs(i), formalT.asInstanceOf[AST.Typed.Fun], pos)
            case _ => translateExp(expArgs(i))
          }
          namedArgs(formalIndex) = Some(snapshot(arg))
        }
        stmts = oldStmts ++ argStmts.toIS
        for (arg <- namedArgs.toIS[Option[AST.IR.Exp]]) {
          args.append(arg.get)
        }
      case _ =>
        for (i <- z"0" until expArgs.size) {
          val formalT = originalMethodType.args(i)
          byNameValueTypeOpt(formalT) match {
            case Some(_) =>
              args.append(makeByNameClosure(expArgs(i), formalT.asInstanceOf[AST.Typed.Fun], pos))
            case _ =>
              args.append(translateExp(expArgs(i)))
          }
        }
    }
    if (isExt) {
      methodType = lowerByNameFunType(methodType)
      applyOwner = recordAndResolveExt(res, T)
      applyIsInObject = T
    }
    return norm3AC(AST.IR.Exp.Apply(applyIsInObject, applyOwner, applyId, AST.Typed.emptyRTypes, args.toIS, methodType, pos, isSuper))
  }

  def translateExp(exp: AST.Exp): AST.IR.Exp = {
    expDepth = expDepth + 1
    val r = translateExpH(exp)
    expDepth = expDepth - 1
    return r
  }

  def translateExpH(exp: AST.Exp): AST.IR.Exp = {

    val pos = exp.posOpt.get
    exp match {
      case exp: AST.Exp.Tuple if exp.args.isEmpty =>
        halt(s"Unit marker reached value translation at ${exp.posOpt}. It should have been erased before IR expression translation.")
      case exp: AST.Exp.LitB => return norm3AC(AST.IR.Exp.Bool(exp.value, pos))
      case exp: AST.Exp.LitC => return norm3AC(AST.IR.Exp.Int(AST.Typed.c, exp.value.toZ, pos))
      case exp: AST.Exp.LitZ => return norm3AC(AST.IR.Exp.Int(AST.Typed.z, exp.value, pos))
      case exp: AST.Exp.LitF32 => return norm3AC(AST.IR.Exp.F32(exp.value, pos))
      case exp: AST.Exp.LitF64 => return norm3AC(AST.IR.Exp.F64(exp.value, pos))
      case exp: AST.Exp.LitR => return norm3AC(AST.IR.Exp.R(exp.value, pos))
      case exp: AST.Exp.LitString => return norm3AC(AST.IR.Exp.String(exp.value, pos))
      case exp: AST.Exp.StringInterpolate =>
        if (isScalar(exp.typedOpt.get)) {
          val t = exp.typedOpt.get.asInstanceOf[AST.Typed.Name]
          val value = Z(exp.lits(0).value).get
          return norm3AC(AST.IR.Exp.Int(t, value, pos))
        } else {
          val irArgs: ISZ[AST.IR.Exp] = for (arg <- exp.args) yield translateExp(arg)
          val litStrings: ISZ[String] = for (lit <- exp.lits) yield lit.value
          return norm3AC(AST.IR.Exp.StringInterpolate(
            prefix = exp.prefix,
            lits = litStrings,
            args = irArgs,
            tipe = exp.typedOpt.get,
            pos = pos))
        }
      case _: AST.Exp.This => return thiz(pos)
      case exp: AST.Exp.Ident =>
        val t = exp.typedOpt.get
        exp.resOpt.get match {
          case res: AST.ResolvedInfo.LocalVar =>
            if (varCaptureSet.contains(res.id)) {
              val valueT = lowerByNameType(t)
              val mt = mboxType(valueT)
              val mboxRef = AST.IR.Exp.LocalVarRef(T, methodContext, res.id, mt, pos)
              return norm3AC(AST.IR.Exp.FieldVarRef(mboxRef, "value", valueT, pos))
            }
            byNameValueTypeOpt(t) match {
              case Some(valueT) =>
                val ref = AST.IR.Exp.LocalVarRef(res.isVal, methodContext, res.id, lowerByNameType(t), pos)
                return norm3AC(AST.IR.Exp.ApplyClosure(ref, ISZ(), valueT, pos))
              case _ =>
                return norm3AC(AST.IR.Exp.LocalVarRef(res.isVal, methodContext, res.id, lowerByNameType(t), pos))
            }
          case res: AST.ResolvedInfo.Var =>
            if (res.isInObject) {
              return norm3AC(AST.IR.Exp.GlobalVarRef(res.owner :+ res.id, lowerByNameType(t), pos))
            } else {
              return norm3AC(AST.IR.Exp.FieldVarRef(liftedThiz(res.owner, pos), res.id, lowerByNameType(t), pos))
            }
          case res: AST.ResolvedInfo.EnumElement =>
            return norm3AC(AST.IR.Exp.EnumElementRef(res.owner, res.name, res.ordinal, pos))
          case res: AST.ResolvedInfo.Method =>
            val nestedKey = res.owner :+ res.id
            if (nestedMethodCaptures.contains(nestedKey)) {
              return translateMethodInvoke(res, None(), ISZ(), None(), pos)
            }
            val methodType = res.tpeOpt.get
            val owner = recordAndResolveExt(res, res.isInObject)
            if (res.isInObject) {
              return norm3AC(AST.IR.Exp.Apply(T, owner, res.id, AST.Typed.emptyRTypes, ISZ(), methodType, pos, F))
            } else {
              val receiver = thiz(pos)
              return norm3AC(AST.IR.Exp.Apply(F, owner, res.id, AST.Typed.emptyRTypes, ISZ(receiver),
                methodType(args = receiver.tipe +: methodType.args), pos, F))
            }
          case _ => halt(s"Infeasible: $exp")
        }
      case exp: AST.Exp.Select =>
        val t = exp.typedOpt.get
        exp.resOpt.get match {
          case res: AST.ResolvedInfo.Var =>
            if (res.isInObject) {
              return norm3AC(AST.IR.Exp.GlobalVarRef(res.owner :+ res.id, lowerByNameType(t), pos))
            } else {
              val rcv: AST.IR.Exp = exp.receiverOpt match {
                case Some(receiver) => liftedThizFromReceiver(receiver, res.owner, pos)
                case _ => liftedThiz(res.owner, pos)
              }
              return norm3AC(AST.IR.Exp.FieldVarRef(rcv, res.id, lowerByNameType(t), pos))
            }
          case res: AST.ResolvedInfo.EnumElement =>
            return norm3AC(AST.IR.Exp.EnumElementRef(res.owner, res.name, res.ordinal, pos))
          case res: AST.ResolvedInfo.Method =>
            exp.receiverOpt match {
              case Some(receiver) =>
                val receiverType = receiver.typedOpt.get
                if (receiverType == AST.Typed.string || isSeq(receiverType)) {
                  val rcv = translateExp(receiver)
                  return norm3AC(AST.IR.Exp.FieldVarRef(rcv, res.id, lowerByNameType(res.tpeOpt.get.ret), pos))
                } else {
                  return translateExp(AST.Exp.Invoke(exp.receiverOpt, AST.Exp.Ident(exp.id, exp.attr), ISZ(), ISZ(), ISZ(),
                    exp.attr(typedOpt = Some(exp.typedOpt.get.asInstanceOf[AST.Typed.Method].tpe.ret))))
                }
              case _ =>
                return translateExp(AST.Exp.Invoke(None(), AST.Exp.Ident(exp.id, exp.attr), ISZ(), ISZ(), ISZ(),
                  exp.attr(typedOpt = Some(exp.typedOpt.get.asInstanceOf[AST.Typed.Method].tpe.ret))))
            }
          case AST.ResolvedInfo.BuiltIn(kind) if kind == AST.ResolvedInfo.BuiltIn.Kind.AsInstanceOf ||
          kind == AST.ResolvedInfo.BuiltIn.Kind.IsInstanceOf =>
            val receiver = translateExp(exp.receiverOpt.get)
            return norm3AC(AST.IR.Exp.Type(kind == AST.ResolvedInfo.BuiltIn.Kind.IsInstanceOf, receiver,
              exp.targs(0).typedOpt.get.asInstanceOf[AST.Typed.Name], exp.posOpt.get))
          case res: AST.ResolvedInfo.Tuple =>
            val receiver = translateExp(exp.receiverOpt.get)
            return norm3AC(AST.IR.Exp.FieldVarRef(receiver, s"_${res.index}", t, pos))
          case AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.String) =>
            val receiverExp = exp.receiverOpt.get
            val receiver: AST.IR.Exp = receiverExp match {
              case _: AST.Exp.Super => thiz(pos)
              case _ => translateExp(receiverExp)
            }
            val receiverType = receiverExp.typedOpt.get
            val owner: ISZ[String] = receiverType match {
              case tn: AST.Typed.Name => tn.ids
              case _ => ISZ[String]()
            }
            val methodType = AST.Typed.Fun(AST.Purity.Impure, F, ISZ(receiverType), AST.Typed.string)
            val isSuper: B = receiverExp match {
              case _: AST.Exp.Super => T
              case _ => F
            }
            return norm3AC(AST.IR.Exp.Apply(F, owner, "string", AST.Typed.emptyRTypes, ISZ(receiver), methodType, pos, isSuper))
          case AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Hash) =>
            val receiverExp = exp.receiverOpt.get
            val receiver: AST.IR.Exp = receiverExp match {
              case _: AST.Exp.Super => thiz(pos)
              case _ => translateExp(receiverExp)
            }
            val receiverType = receiverExp.typedOpt.get
            val owner: ISZ[String] = receiverType match {
              case tn: AST.Typed.Name => tn.ids
              case _ => ISZ[String]()
            }
            val methodType = AST.Typed.Fun(AST.Purity.Impure, F, ISZ(receiverType), AST.Typed.z)
            val isSuper: B = receiverExp match {
              case _: AST.Exp.Super => T
              case _ => F
            }
            return norm3AC(AST.IR.Exp.Apply(F, owner, "hash", AST.Typed.emptyRTypes, ISZ(receiver), methodType, pos, isSuper))
          case res => halt(s"TODO: $res")
        }
      case exp: AST.Exp.Unary =>
        val t = exp.typedOpt.get
        if (isScalar(t)) {
          val e = translateExp(exp.exp)
          return norm3AC(AST.IR.Exp.Unary(t, exp.op, e, pos))
        } else {
          halt(s"TODO: $exp")
        }
      case exp: AST.Exp.UnaryTemporal =>
        val t = exp.typedOpt.get
        if (isScalar(t)) {
          val e = translateExp(exp.exp)
          return norm3AC(AST.IR.Exp.UnaryTemporal(t, exp.op, e, exp.intvl, pos))
        } else {
          halt(s"TODO: $exp")
        }
      case exp: AST.Exp.BinaryTemporal =>
        val t = exp.typedOpt.get
        if (isScalar(t)) {
          val left = translateExp(exp.left)
          val right = translateExp(exp.right)
          return norm3AC(AST.IR.Exp.BinaryTemporal(t, left, exp.op, exp.intvl, right, pos))
        } else {
          halt(s"TODO: $exp")
        }
      case exp: AST.Exp.Binary =>
        val t = exp.typedOpt.get
        if (isScalar(t)) {
          val kind: AST.IR.Exp.Binary.Op.Type = exp.attr.resOpt.get.asInstanceOf[AST.ResolvedInfo.BuiltIn].kind match {
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryAdd => AST.IR.Exp.Binary.Op.Add
            case AST.ResolvedInfo.BuiltIn.Kind.BinarySub => AST.IR.Exp.Binary.Op.Sub
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryMul => AST.IR.Exp.Binary.Op.Mul
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryDiv => AST.IR.Exp.Binary.Op.Div
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryRem => AST.IR.Exp.Binary.Op.Rem
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryAnd => AST.IR.Exp.Binary.Op.And
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryOr => AST.IR.Exp.Binary.Op.Or
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryImply => AST.IR.Exp.Binary.Op.Imply
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryXor => AST.IR.Exp.Binary.Op.Xor
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryEq => AST.IR.Exp.Binary.Op.Eq
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryNe => AST.IR.Exp.Binary.Op.Ne
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryEquiv => AST.IR.Exp.Binary.Op.Eq
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryInequiv => AST.IR.Exp.Binary.Op.Ne
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryFpEq => AST.IR.Exp.Binary.Op.FpEq
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryFpNe => AST.IR.Exp.Binary.Op.FpNe
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryGe => AST.IR.Exp.Binary.Op.Ge
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryGt => AST.IR.Exp.Binary.Op.Gt
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryLe => AST.IR.Exp.Binary.Op.Le
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryLt => AST.IR.Exp.Binary.Op.Lt
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryShr => AST.IR.Exp.Binary.Op.Shr
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryUshr => AST.IR.Exp.Binary.Op.Ushr
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryShl => AST.IR.Exp.Binary.Op.Shl
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryCondAnd =>
              if (threeAddressCode) {
                return translateExp(AST.Exp.If(exp.left, exp.right, AST.Exp.LitB(F, AST.Attr(exp.posOpt)),
                  AST.TypedAttr(exp.posOpt, AST.Typed.bOpt)))
              }
              AST.IR.Exp.Binary.Op.CondAnd
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryCondOr =>
              if (threeAddressCode) {
                return translateExp(AST.Exp.If(exp.left, AST.Exp.LitB(T, AST.Attr(exp.posOpt)), exp.right,
                  AST.TypedAttr(exp.posOpt, AST.Typed.bOpt)))
              }
              AST.IR.Exp.Binary.Op.CondOr
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryCondImply =>
              if (threeAddressCode) {
                return translateExp(AST.Exp.If(exp.left, exp.right, AST.Exp.LitB(T, AST.Attr(exp.posOpt)),
                  AST.TypedAttr(exp.posOpt, AST.Typed.bOpt)))
              }
              AST.IR.Exp.Binary.Op.CondImply
            case _ => halt(s"Infeasible: ${exp.attr.resOpt.get}")
          }
          val left = translateExp(exp.left)
          val right = translateExp(exp.right)
          return norm3AC(AST.IR.Exp.Binary(t, left, kind, right, pos))
        }
        if (isSeq(t)) {
          val kindOpt: Option[AST.IR.Exp.Binary.Op.Type] = exp.op match {
            case AST.Exp.BinaryOp.Append => Some(AST.IR.Exp.Binary.Op.Append)
            case AST.Exp.BinaryOp.Prepend => Some(AST.IR.Exp.Binary.Op.Prepend)
            case AST.Exp.BinaryOp.AppendAll => Some(AST.IR.Exp.Binary.Op.AppendAll)
            case _ => None()
          }
          kindOpt match {
            case Some(kind) =>
              val left = translateExp(exp.left)
              val right = translateExp(exp.right)
              return norm3AC(AST.IR.Exp.Binary(t, left, kind, right, pos))
            case _ =>
          }
        }
        // MapsTo (~>): lower to Tuple.of(left, right)
        exp.attr.resOpt.get match {
          case AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.BinaryMapsTo) =>
            val left = translateExp(exp.left)
            val right = translateExp(exp.right)
            val tupleIds = ISZ[String]("org", "sireum", "Tuple")
            val leftType = exp.left.typedOpt.get
            val rightType = exp.right.typedOpt.get
            val methodType = AST.Typed.Fun(AST.Purity.Impure, F, ISZ(leftType, rightType), t)
            return norm3AC(AST.IR.Exp.Apply(T, tupleIds, "of", AST.Typed.emptyRTypes, ISZ(left, right), methodType, pos, F))
          case _ =>
        }
        // Non-scalar, non-seq binary op: lower to method call on left operand
        exp.attr.resOpt.get match {
          case res: AST.ResolvedInfo.Method =>
            if (res.isInObject) {
              return translateMethodInvoke(res, None(), ISZ(exp.right), None(), pos)
            }
            val left = translateExp(exp.left)
            val right = translateExp(exp.right)
            val receiverType: AST.Typed = exp.left.typedOpt.get
            val owner: ISZ[String] = receiverType match {
              case tn: AST.Typed.Name => tn.ids
              case _ => ISZ[String]()
            }
            // Use resolved method type (with proper type substitution) + prepend receiver
            var methodType = res.tpeOpt.get
            methodType = methodType(args = receiverType +: methodType.args)
            return norm3AC(AST.IR.Exp.Apply(F, owner, res.id, AST.Typed.emptyRTypes, ISZ(left, right), methodType, pos, F))
          case _ =>
        }
        halt(s"TODO: $exp")
      case exp: AST.Exp.If =>
        val t = exp.typedOpt.get
        val cond = translateExp(exp.cond)
        if (!threeAddressCode) {
          val thenExp = translateExp(exp.thenExp)
          val elseExp = translateExp(exp.elseExp)
          return AST.IR.Exp.If(cond, thenExp, elseExp, t, pos)
        }
        val n = fresh.temp()
        val oldStmts = stmts
        stmts = ISZ()
        val thenExp = translateExp(exp.thenExp)
        val thenStmts = stmts
        val thenPos = exp.thenExp.posOpt.get
        stmts = ISZ()
        val elseExp = translateExp(exp.elseExp)
        val elseStmts = stmts
        val elsePos = exp.elseExp.posOpt.get
        stmts = oldStmts
        stmts = stmts :+ AST.IR.Stmt.If(cond,
          AST.IR.Stmt.Block(thenStmts :+ AST.IR.Stmt.Assign.Temp(n, thenExp, thenPos), thenPos),
          AST.IR.Stmt.Block(elseStmts :+ AST.IR.Stmt.Assign.Temp(n, elseExp, elsePos), elsePos), pos)
        return AST.IR.Exp.Temp(n, t, pos)
      case exp: AST.Exp.Invoke =>
        exp.attr.resOpt.get match {
          case res: AST.ResolvedInfo.Method =>
            res.mode match {
              case AST.MethodMode.Method =>
                return translateMethodInvoke(res, exp.receiverOpt, exp.args, None(), pos)
              case AST.MethodMode.Ext =>
                return translateMethodInvoke(res, exp.receiverOpt, exp.args, None(), pos)
              case AST.MethodMode.Select =>
                val rcv: AST.IR.Exp = exp.receiverOpt match {
                  case Some(receiver) =>
                    if (exp.ident.id.value == "apply") {
                      translateExp(receiver)
                    } else {
                      val e = AST.Exp.Select(Some(receiver), exp.ident.id, exp.targs, exp.ident.attr)
                      translateExp(e)
                    }
                  case _ => translateExp(exp.ident)
                }
                val index = translateExp(exp.args(0))
                return norm3AC(AST.IR.Exp.Indexing(rcv, index, pos))
              case AST.MethodMode.Constructor =>
                var args = ISZ[AST.IR.Exp]()
                for (arg <- exp.args) {
                  args = args :+ translateExp(arg)
                }
                return norm3AC(AST.IR.Exp.Construct(exp.typedOpt.get.asInstanceOf[AST.Typed.Name], AST.Typed.emptyRTypes, args, pos))
              case AST.MethodMode.Copy =>
                val t = exp.typedOpt.get.asInstanceOf[AST.Typed.Name]
                var args = ISZ[AST.IR.Exp]()
                for (arg <- exp.args) {
                  args = args :+ translateExp(arg)
                }
                return norm3AC(AST.IR.Exp.Construct(t, AST.Typed.emptyRTypes, args, pos))
              case AST.MethodMode.Store =>
                // IS/MS functional update: is(p1, p2, ...) where each pi is `idx ~> val`.
                // Lower to a chain of Apply(IS/MS, "functionalUpdate", [rcv', idx_i, val_i])
                // so we correctly handle any number of ~> arguments (not just the first).
                if (exp.args.isEmpty) {
                  halt(s"Store-mode invoke with no ~> arguments at ${exp.posOpt}: $exp")
                }
                val seqType = exp.typedOpt.get.asInstanceOf[AST.Typed.Name]
                val elemType = seqType.args(1)
                val methodType = AST.Typed.Fun(AST.Purity.Impure, F, ISZ(seqType, AST.Typed.z, elemType), seqType)
                var rcv: AST.IR.Exp = exp.receiverOpt match {
                  case Some(receiver) =>
                    if (exp.ident.id.value == "apply") {
                      translateExp(receiver)
                    } else {
                      val e = AST.Exp.Select(Some(receiver), exp.ident.id, exp.targs, exp.ident.attr)
                      translateExp(e)
                    }
                  case _ => translateExp(exp.ident)
                }
                for (arg <- exp.args) {
                  val (index, value): (AST.IR.Exp, AST.IR.Exp) = arg match {
                    case b: AST.Exp.Binary => (translateExp(b.left), translateExp(b.right))
                    case t: AST.Exp.Tuple if t.args.size == z"2" => (translateExp(t.args(0)), translateExp(t.args(1)))
                    case _ => halt(s"Unexpected Store arg at ${arg.posOpt}: $arg")
                  }
                  rcv = norm3AC(AST.IR.Exp.Apply(T, seqType.ids, "functionalUpdate",
                    AST.Typed.emptyRTypes, ISZ(rcv, index, value), methodType, pos, F))
                }
                return rcv
              case _ => halt(s"TODO: $exp")
            }
          case res: AST.ResolvedInfo.LocalVar =>
            // Calling a function-typed local variable, e.g. f(10) where f: Z => Z
            val originalFunType = exp.ident.attr.typedOpt.get.asInstanceOf[AST.Typed.Fun]
            val loweredFunType = lowerByNameFunType(originalFunType)
            val closureVar: AST.IR.Exp = exp.receiverOpt match {
              case Some(receiver) => translateExp(receiver)
              case _ =>
                AST.IR.Exp.LocalVarRef(res.isVal, methodContext, res.id, loweredFunType, pos)
            }
            var args = ISZ[AST.IR.Exp]()
            for (i <- z"0" until exp.args.size) {
              val formalT = originalFunType.args(i)
              byNameValueTypeOpt(formalT) match {
                case Some(_) =>
                  args = args :+ makeByNameClosure(exp.args(i), formalT.asInstanceOf[AST.Typed.Fun], pos)
                case _ =>
                  args = args :+ translateExp(exp.args(i))
              }
            }
            val retType = exp.typedOpt.get
            return norm3AC(AST.IR.Exp.ApplyClosure(closureVar, args, retType, pos))
          case res: AST.ResolvedInfo.Var =>
            // Calling a function-typed field/global variable, e.g. this.p(10) where p: T => B
            val originalFunType = exp.ident.attr.typedOpt.get.asInstanceOf[AST.Typed.Fun]
            val loweredFunType = lowerByNameFunType(originalFunType)
            val closureVar: AST.IR.Exp =
              if (res.isInObject) {
                AST.IR.Exp.GlobalVarRef(res.owner :+ res.id, loweredFunType, pos)
              } else {
                exp.receiverOpt match {
                  case Some(receiver) =>
                    val receiverExp = liftedThizFromReceiver(receiver, res.owner, pos)
                    AST.IR.Exp.FieldVarRef(receiverExp, res.id, loweredFunType, pos)
                  case _ =>
                    AST.IR.Exp.FieldVarRef(liftedThiz(res.owner, pos), res.id, loweredFunType, pos)
                }
              }
            var args = ISZ[AST.IR.Exp]()
            for (i <- z"0" until exp.args.size) {
              val formalT = originalFunType.args(i)
              byNameValueTypeOpt(formalT) match {
                case Some(_) =>
                  args = args :+ makeByNameClosure(exp.args(i), formalT.asInstanceOf[AST.Typed.Fun], pos)
                case _ =>
                  args = args :+ translateExp(exp.args(i))
              }
            }
            val retType = exp.typedOpt.get
            return norm3AC(AST.IR.Exp.ApplyClosure(closureVar, args, retType, pos))
          case res => halt(s"TODO: $exp (res: $res)")
        }
      case exp: AST.Exp.InvokeNamed =>
        exp.attr.resOpt.get match {
          case res: AST.ResolvedInfo.Method if res.mode == AST.MethodMode.Method || res.mode == AST.MethodMode.Ext =>
            val expArgs: ISZ[AST.Exp] = for (narg <- exp.args) yield narg.arg
            val namedIndices: ISZ[Z] = for (narg <- exp.args) yield narg.index
            return translateMethodInvoke(res, exp.receiverOpt, expArgs, Some(namedIndices), pos)
          case res: AST.ResolvedInfo.Method if res.mode == AST.MethodMode.Constructor =>
            val oldStmts = stmts
            stmts = ISZ()
            val argStmts = Buffer.create[AST.IR.Stmt]()
            val args = MSZ.create[Option[AST.IR.Exp]](exp.args.size, None())
            for (narg <- exp.args) {
              val arg = translateExp(narg.arg)
              for (stmt <- stmts) {
                argStmts.append(stmt)
              }
              stmts = ISZ()
              val n = fresh.temp()
              argStmts.append(AST.IR.Stmt.Assign.Temp(n, arg, arg.pos))
              args(narg.index) = Some(AST.IR.Exp.Temp(n, arg.tipe, arg.pos))
            }
            stmts = oldStmts ++ argStmts.toIS
            val orderedArgs: ISZ[AST.IR.Exp] = for (arg <- args.toIS[Option[AST.IR.Exp]]) yield arg.get
            return norm3AC(AST.IR.Exp.Construct(exp.typedOpt.get.asInstanceOf[AST.Typed.Name], AST.Typed.emptyRTypes,
              orderedArgs, pos))
          case res: AST.ResolvedInfo.Method if res.mode == AST.MethodMode.Copy =>
            val t = exp.typedOpt.get.asInstanceOf[AST.Typed.Name]
            val adt = th.typeMap.get(t.ids).get.asInstanceOf[TypeInfo.Adt]
            val sm = tipe.TypeChecker.buildTypeSubstMap(t.ids, exp.posOpt, adt.ast.typeParams, t.args,
              message.Reporter.create).get
            val oldStmts = stmts
            stmts = ISZ()
            val copyStmts = Buffer.create[AST.IR.Stmt]()
            // A named copy such as `outer.inner(b = T)` retains `outer` as the
            // invocation receiver and `inner` as the typed identifier, so
            // reconstruct the selected field rather than copying fields directly
            // from `outer`.
            val targetExp: AST.IR.Exp = exp.receiverOpt match {
              case Some(recv) =>
                if (exp.ident.id.value == "apply") {
                  translateExp(recv)
                } else {
                  val target = AST.Exp.Select(
                    receiverOpt = Some(recv),
                    id = exp.ident.id,
                    targs = exp.targs,
                    attr = exp.ident.attr)
                  translateExp(target)
                }
              case _ => translateExp(AST.Exp.Ident(exp.ident.id, exp.ident.attr))
            }
            for (stmt <- stmts) {
              copyStmts.append(stmt)
            }
            stmts = ISZ()
            val targetN = fresh.temp()
            copyStmts.append(AST.IR.Stmt.Assign.Temp(targetN, targetExp, targetExp.pos))
            val copyTarget: AST.IR.Exp = AST.IR.Exp.Temp(targetN, targetExp.tipe, targetExp.pos)
            val namedArgs = MSZ.create[Option[AST.IR.Exp]](adt.ast.params.size, None())
            for (narg <- exp.args) {
              val arg = translateExp(narg.arg)
              for (stmt <- stmts) {
                copyStmts.append(stmt)
              }
              stmts = ISZ()
              val n = fresh.temp()
              copyStmts.append(AST.IR.Stmt.Assign.Temp(n, arg, arg.pos))
              namedArgs(narg.index) = Some(AST.IR.Exp.Temp(n, arg.tipe, arg.pos))
            }
            stmts = oldStmts ++ copyStmts.toIS
            // Assemble in param order: use named arg if provided, else copy from the target
            val args = Buffer.create[AST.IR.Exp]()
            for (i <- adt.ast.params.indices) {
              namedArgs(i) match {
                case Some(arg) => args.append(arg)
                case _ =>
                  val param = adt.ast.params(i)
                  val pt = param.tipe.typedOpt.get.subst(sm)
                  args.append(norm3AC(AST.IR.Exp.FieldVarRef(copyTarget, param.id.value, pt, pos)))
              }
            }
            return norm3AC(AST.IR.Exp.Construct(t, AST.Typed.emptyRTypes, args.toIS, pos))
          case _ =>
            halt(s"TODO: $exp")
        }
      case exp: AST.Exp.Tuple =>
        val tupleType = exp.typedOpt.get
        val tupleIds = ISZ[String]("org", "sireum", "Tuple")
        var args = ISZ[AST.IR.Exp]()
        var argTypes = ISZ[AST.Typed]()
        for (arg <- exp.args) {
          args = args :+ translateExp(arg)
          argTypes = argTypes :+ arg.typedOpt.get
        }
        val methodType = AST.Typed.Fun(AST.Purity.Impure, F, argTypes, tupleType)
        return norm3AC(AST.IR.Exp.Apply(T, tupleIds, "of", AST.Typed.emptyRTypes, args, methodType, pos, F))
      case exp: AST.Exp.ForYield =>
        val resultType = exp.typedOpt.get.asInstanceOf[AST.Typed.Name]
        val resultId = st"$$forYield.${pos.beginLine}.${pos.beginColumn}.${sha3(pos.string)}".render
        stmts = stmts :+ AST.IR.Stmt.Decl(F, F, F, methodContext,
          ISZ(AST.IR.Stmt.Decl.Local(resultId, resultType)), pos)
        stmts = stmts :+ AST.IR.Stmt.Assign.Local(methodContext, resultId, resultType,
          AST.IR.Exp.Construct(resultType, AST.Typed.emptyRTypes, ISZ(), pos), pos)
        def translateEnumGen(i: Z): AST.IR.Stmt.For = {
          val enumGen = exp.enumGens(i)
          val idOpt: Option[String] = enumGen.idOpt match {
            case Some(id) => Some(id.value)
            case _ => None()
          }
          val range: AST.IR.Stmt.For.Range = enumGen.range match {
            case r: AST.EnumGen.Range.Expr =>
              val rangeExp = translateExp(r.exp)
              AST.IR.Stmt.For.Range.Expr(rangeExp, r.attr.posOpt.getOrElse(pos))
            case r: AST.EnumGen.Range.Step =>
              val rPos = r.attr.posOpt.getOrElse(pos)
              val start = translateExp(r.start)
              val end = translateExp(r.end)
              val byOpt: Option[AST.IR.Exp] = r.byOpt match {
                case Some(by) => Some(translateExp(by))
                case _ => None()
              }
              AST.IR.Stmt.For.Range.Step(r.isInclusive, start, end, byOpt, rPos)
          }
          val condOpt: Option[AST.IR.ExpBlock] = enumGen.condOpt match {
            case Some(cond) => Some(translateExpBlock(cond))
            case _ => None()
          }
          val innerBlock: AST.IR.Stmt.Block = if (i < exp.enumGens.size - 1) {
            val oldStmts2 = stmts
            stmts = ISZ()
            val nested = translateEnumGen(i + 1)
            val nestedStmts = stmts :+ nested
            stmts = oldStmts2
            resetTemp()
            AST.IR.Stmt.Block(nestedStmts, pos)
          } else {
            val oldStmts2 = stmts
            stmts = ISZ()
            val yieldExp = translateExp(exp.exp)
            val yieldPos = exp.exp.posOpt.get
            val resultRef = AST.IR.Exp.LocalVarRef(F, methodContext, resultId, resultType, pos)
            val appendExp = AST.IR.Exp.Binary(resultType, resultRef,
              AST.IR.Exp.Binary.Op.Append, yieldExp, yieldPos)
            val bodyStmts = stmts :+ AST.IR.Stmt.Assign.Local(methodContext, resultId, resultType,
              appendExp, yieldPos)
            stmts = oldStmts2
            resetTemp()
            AST.IR.Stmt.Block(bodyStmts, yieldPos)
          }
          return AST.IR.Stmt.For(methodContext, idOpt, range, condOpt, innerBlock, pos)
        }
        val forStmt = translateEnumGen(0)
        stmts = stmts :+ forStmt
        return AST.IR.Exp.LocalVarRef(F, methodContext, resultId, resultType, pos)
      case exp: AST.Exp.Eta =>
        val funType = exp.attr.typedOpt.get.asInstanceOf[AST.Typed.Fun]
        exp.ref.resOpt.get match {
          case res: AST.ResolvedInfo.Method =>
            val nestedKey = res.owner :+ res.id
            val nestedCaptureListOpt: Option[ISZ[(B, String, AST.Typed)]] =
              if (res.mode == AST.MethodMode.Ext) None() else nestedMethodCaptures.get(nestedKey)
            nestedCaptureListOpt match {
              case Some(captureList) =>
                val captureExps = Buffer.create[AST.IR.Exp]()
                for (capture <- captureList) {
                  captureExps.append(nestedCaptureExp(capture, pos))
                }
                return norm3AC(AST.IR.Exp.ClosureRef(
                  owner = methodContext.owner,
                  id = liftedNestedMethodId(res),
                  captures = captureExps.toIS,
                  tipe = funType,
                  pos = pos
                ))
              case _ =>
            }
            val closureType: AST.Typed.Fun = if (res.mode == AST.MethodMode.Ext) lowerByNameFunType(funType) else funType
            val captures: ISZ[AST.IR.Exp] = if (res.isInObject) {
              ISZ()
            } else {
              exp.ref match {
                case ref: AST.Exp.Select =>
                  ref.receiverOpt match {
                    case Some(receiver) => ISZ(liftedThizFromReceiver(receiver, res.owner, pos))
                    case _ => ISZ(liftedThiz(res.owner, pos))
                  }
                case _: AST.Exp.Ident => ISZ(liftedThiz(res.owner, pos))
                case _ => ISZ(liftedThiz(res.owner, pos))
              }
            }
            val owner = recordAndResolveExt(res, res.isInObject)
            return norm3AC(AST.IR.Exp.ClosureRef(
              owner = owner,
              id = res.id,
              captures = captures,
              tipe = closureType,
              pos = pos
            ))
          case res: AST.ResolvedInfo.LocalVar =>
            if (varCaptureSet.contains(res.id)) {
              val valueT = lowerByNameType(funType)
              val mt = mboxType(valueT)
              val mboxRef = AST.IR.Exp.LocalVarRef(T, methodContext, res.id, mt, pos)
              return norm3AC(AST.IR.Exp.FieldVarRef(mboxRef, "value", valueT, pos))
            }
            return norm3AC(AST.IR.Exp.LocalVarRef(
              isVal = res.isVal,
              context = methodContext,
              id = res.id,
              tipe = funType,
              pos = pos
            ))
          case res =>
            halt(s"TODO: Eta with $res")
        }
      case exp: AST.Exp.Fun =>
        val originalFunType = exp.attr.typedOpt.get.asInstanceOf[AST.Typed.Fun]

        // Step 1: Compute closure name and owner
        val closureName = st"$$closure.${pos.beginLine}.${pos.beginColumn}.${sha3(pos.string)}".render
        val owner = methodContext.owner

        // Step 2: Collect captures from the lambda body
        val captureInfo = collectCaptures(exp.exp)
        val captureBuf = Buffer.create[(B, String, AST.Typed)]()
        for (capture <- captureInfo._1 if capture._1 != exp.context) {
          captureBuf.append((capture._2, capture._3, capture._4))
        }
        val captures = prependThisCapture(captureBuf.toIS, captureInfo._2)
        val ownVarCaptureNames: ISZ[String] = for (capture <- captureInfo._1 if
          capture._1 == exp.context && !capture._2) yield capture._3
        val liftedCaptureNames: ISZ[String] = for (capture <- captures if
          capture._2 != "this" && (!capture._1 || varCaptureSet.contains(capture._2))) yield capture._2
        var liftedVarCaptureSet = HashSet.empty[String] ++ ownVarCaptureNames ++ liftedCaptureNames

        // Step 3: Build capture names/types and lambda param names/types
        var captureNames = ISZ[String]()
        var captureTypes = ISZ[AST.Typed]()
        var captureExprs = ISZ[AST.IR.Exp]()
        for (capture <- captures) {
          val captureIsVal = capture._1
          val captureId = capture._2
          val captureType = capture._3
          val loweredCaptureType = lowerByNameType(captureType)
          captureNames = captureNames :+ captureId
          if (captureId == "this") {
            captureTypes = captureTypes :+ loweredCaptureType
            captureExprs = captureExprs :+ thiz(pos)
          } else if (!captureIsVal || varCaptureSet.contains(captureId)) {
            // var capture: already MBox-wrapped in enclosing scope
            val mt = mboxType(loweredCaptureType)
            captureTypes = captureTypes :+ mt
            captureExprs = captureExprs :+ AST.IR.Exp.LocalVarRef(T, methodContext, captureId, mt, pos)
          } else {
            // val capture: pass value directly
            captureTypes = captureTypes :+ loweredCaptureType
            captureExprs = captureExprs :+ AST.IR.Exp.LocalVarRef(T, methodContext, captureId, loweredCaptureType, pos)
          }
        }

        val lambdaParamNames: ISZ[String] = for (p <- exp.params) yield p.idOpt.get.value
        val fullParamNames = captureNames ++ lambdaParamNames

        // Step 4: Build lifted fun type (captures ++ lambda args -> ret)
        val liftedFunType = AST.Typed.Fun(
          purity = originalFunType.purity,
          isByName = F,
          args = captureTypes ++ originalFunType.args,
          ret = originalFunType.ret
        )

        // Step 5: Save current state
        val savedMethodContext = methodContext
        val savedStmts = stmts
        val savedVarCaptureSet = varCaptureSet
        val savedNestedMethodCaptures = nestedMethodCaptures
        val savedNestedMethodCaptureInfo = nestedMethodCaptureInfo
        val savedCapturedThisTypeOpt = capturedThisTypeOpt
        val savedCurrentThisExpOpt = currentThisExpOpt

        // Step 6: Set fresh state for lifted body
        methodContext = AST.IR.MethodContext(
          isInObject = T,
          owner = owner,
          id = closureName,
          t = liftedFunType
        )
        stmts = ISZ()
        varCaptureSet = liftedVarCaptureSet
        capturedThisTypeOpt = captureThisTypeOpt(captures)
        currentThisExpOpt = None()

        // Step 7: Translate lambda body
        // Exp.Fun.exp is an AssignExp; translate it as the return value
        val retType = originalFunType.ret
        val bodyPos = exp.exp.asStmt.posOpt.get
        exp.exp match {
          case bodyExpr: AST.Stmt.Expr =>
            if (isHalt(bodyExpr)) {
              translateStmt(bodyExpr, None())
            } else if (retType == AST.Typed.unit) {
              translateStmt(bodyExpr, None())
              stmts = stmts :+ AST.IR.Stmt.Return(None(), bodyPos)
            } else {
              val r = translateExp(bodyExpr.exp)
              stmts = stmts :+ AST.IR.Stmt.Return(Some(r), bodyPos)
            }
          case _ =>
            if (retType == AST.Typed.unit) {
              // Unit-returning lambda: translate body as statements, return Unit singleton
              exp.exp match {
                case block: AST.Stmt.Block =>
                  for (s <- block.body.stmts) {
                    translateStmt(s, None())
                  }
                case _ =>
                  translateStmt(exp.exp.asStmt, None())
              }
              stmts = stmts :+ AST.IR.Stmt.Return(None(), bodyPos)
            } else {
              val retId = assignExpId("", Some("$closureRet"), bodyPos)
              stmts = stmts :+ AST.IR.Stmt.Decl(F, T, F, methodContext,
                ISZ(AST.IR.Stmt.Decl.Local(retId, retType)), bodyPos)
              translateAssignExp(exp.exp, (retId, retType))
              val retRef = AST.IR.Exp.LocalVarRef(T, methodContext, retId, retType, bodyPos)
              stmts = stmts :+ AST.IR.Stmt.Return(Some(retRef), bodyPos)
            }
        }
        val liftedBody = AST.IR.Body.Block(AST.IR.Stmt.Block(stmts, pos))

        // Step 8: Collect free TypeVars from capture types so step13 adds typeOps params
        var closureTypeVarIds = ISZ[String]()
        var closureTypeVarSeen = HashSet.empty[String]
        for (ct <- captureTypes) {
          collectTypeVarIds(ct, closureTypeVarSeen) match {
            case (ids, seen) =>
              closureTypeVarIds = closureTypeVarIds ++ ids
              closureTypeVarSeen = seen
          }
        }

        // Step 9: Create the lifted IR.Procedure and append to liftedProcedures
        val liftedProc = AST.IR.Procedure(
          isInObject = T,
          rTypeParams = ISZ(),
          typeParams = closureTypeVarIds,
          owner = owner,
          id = closureName,
          paramNames = fullParamNames,
          tipe = liftedFunType,
          body = liftedBody,
          pos = pos
        )
        liftedProcedures = liftedProcedures :+ liftedProc

        // Step 9: Restore saved state
        methodContext = savedMethodContext
        stmts = savedStmts
        varCaptureSet = savedVarCaptureSet
        nestedMethodCaptures = savedNestedMethodCaptures
        nestedMethodCaptureInfo = savedNestedMethodCaptureInfo
        capturedThisTypeOpt = savedCapturedThisTypeOpt
        currentThisExpOpt = savedCurrentThisExpOpt

        // Step 10: Return ClosureRef with capture expressions and original fun type
        return norm3AC(AST.IR.Exp.ClosureRef(
          owner = owner,
          id = closureName,
          captures = captureExprs,
          tipe = originalFunType,
          pos = pos
        ))
      case exp: AST.Exp.QuantEach => halt(s"TODO: $exp")
      case exp: AST.Exp.QuantRange => halt(s"TODO: $exp")
      case exp: AST.Exp.StrictPureBlock => halt(s"TODO: $exp")
      case exp: AST.Exp.Super => halt(s"TODO: $exp")
      case exp: AST.Exp.Labeled => return translateExp(exp.exp)
      case exp: AST.Exp.QuantType => halt(s"Infeasible: $exp")
      case exp: AST.Exp.AssertAgree => halt(s"Infeasible: $exp")
      case exp: AST.Exp.AssumeAgree => halt(s"Infeasible: $exp")
      case exp: AST.Exp.At => halt(s"Infeasible: $exp")
      case exp: AST.Exp.InfoFlowInvariant => halt(s"Infeasible: $exp")
      case exp: AST.Exp.Input => halt(s"Infeasible: $exp")
      case exp: AST.Exp.LoopIndex => halt(s"Infeasible: $exp")
      case exp: AST.Exp.Old => halt(s"Infeasible: $exp")
      case exp: AST.Exp.RS => halt(s"Infeasible: $exp")
      case exp: AST.Exp.Result => halt(s"Infeasible: $exp")
      case exp: AST.Exp.StateSeq => halt(s"Infeasible: $exp")
      case exp: AST.Exp.Sym => halt(s"Infeasible: $exp")
      case exp: AST.Exp.TypeCond => halt(s"Infeasible: $exp")
      case exp: AST.ProofAst.StepId => halt(s"Infeasible: $exp")
    }
  }

  @pure def translatePattern(exp: AST.IR.Exp,
                             pattern: AST.IR.Pattern,
                             localMap: HashSMap[(ISZ[String], String), AST.IR.Exp]): (ISZ[AST.IR.Exp], HashSMap[(ISZ[String], String), AST.IR.Exp]) = {
    return translatePatternH(exp, pattern, localMap, None())
  }

  @pure def translatePatternWithPatternFacts(exp: AST.IR.Exp,
                                             pattern: AST.IR.Pattern,
                                             localMap: HashSMap[(ISZ[String], String), AST.IR.Exp],
                                             patternFacts: IRTranslator.PatternDeclFacts): (ISZ[AST.IR.Exp], HashSMap[(ISZ[String], String), AST.IR.Exp]) = {
    return translatePatternH(exp, pattern, localMap, Some(patternFacts))
  }

  @pure def translatePatternH(exp: AST.IR.Exp,
                              pattern: AST.IR.Pattern,
                              localMap: HashSMap[(ISZ[String], String), AST.IR.Exp],
                              patternFactsOpt: Option[IRTranslator.PatternDeclFacts]): (ISZ[AST.IR.Exp], HashSMap[(ISZ[String], String), AST.IR.Exp]) = {
    var r = ISZ[AST.IR.Exp]()
    var lMap = localMap
    val pos = pattern.pos
    pattern match {
      case pattern: AST.IR.Pattern.Wildcard =>
        pattern.guardTipeOpt match {
          case Some(tipe) =>
            if (tipe != exp.tipe) {
              r = r :+ AST.IR.Exp.Type(T, exp, tipe.asInstanceOf[AST.Typed.Name], pos)
            }
          case _ =>
        }
      case pattern: AST.IR.Pattern.Literal =>
        val right = AST.IR.Pattern.directLiteral(pattern.exp)
        r = r :+ AST.IR.Exp.Binary(AST.Typed.b, exp, AST.IR.Exp.Binary.Op.Eq, right, pos)
      case pattern: AST.IR.Pattern.VarBinding =>
        var boundExp = exp
        pattern.guardTipeOpt match {
          case Some(tipe) =>
            if (tipe != exp.tipe) {
              r = r :+ AST.IR.Exp.Type(T, exp, tipe.asInstanceOf[AST.Typed.Name], pos)
              boundExp = AST.IR.Exp.Type(F, exp, tipe.asInstanceOf[AST.Typed.Name], pos)
            }
          case _ =>
        }
        lMap = lMap + (pattern.idContext, pattern.id) ~> boundExp
      case pattern: AST.IR.Pattern.Structure =>
        val t = pattern.tipe
        val baseExp: AST.IR.Exp = t match {
          case tn: AST.Typed.Name if exp.tipe != t => AST.IR.Exp.Type(F, exp, tn, pos)
          case _ => exp
        }
        lMap = pattern.idOpt match {
          case Some(id) => localMap + (pattern.idContext, id) ~> baseExp
          case _ => localMap
        }
        t match {
          case t: AST.Typed.Tuple =>
            var i = 0
            var conds = ISZ[AST.IR.Exp]()
            for (j <- 0 until t.args.size) {
              val pat = pattern.patterns(i)
              val f = AST.IR.Exp.FieldVarRef(baseExp, s"_${j + 1}", t.args(j), pat.pos)
              val (pconds, lMap2) = translatePatternH(f, pat, lMap, patternFactsOpt)
              conds = conds ++ pconds
              lMap = lMap2
              i = i + 1
            }
            r = r :+ AST.IR.bigAnd(conds, pos)
          case t: AST.Typed.Name =>
            var conds = ISZ[AST.IR.Exp]()
            if (t.ids == AST.Typed.isName || t.ids == AST.Typed.msName) {
              val hasWildcard = pattern.patterns.size > 0 && pattern.patterns(pattern.patterns.size - 1).
                isInstanceOf[AST.IR.Pattern.SeqWildcard]
              val (size, op): (Z, AST.IR.Exp.Binary.Op.Type) = if (hasWildcard) (pattern.patterns.size - 1, AST.IR.Exp.Binary.Op.Ge)
              else (pattern.patterns.size, AST.IR.Exp.Binary.Op.Eq)
              conds = conds :+ AST.IR.Exp.Binary(AST.Typed.b, AST.IR.Exp.FieldVarRef(baseExp, "size", AST.Typed.z, pos), op,
                AST.IR.Exp.Int(AST.Typed.z, size, pos), pos)
              val indexType = t.args(0)
              for (i <- 0 until pattern.patterns.size - (if (hasWildcard) 1 else 0)) {
                val pat = pattern.patterns(i)
                val f = AST.IR.Exp.Indexing(baseExp, AST.IR.Exp.Int(indexType, i, pos), pat.pos)
                val (pconds, lMap2) = translatePatternH(f, pat, lMap, patternFactsOpt)
                conds = conds ++ pconds
                lMap = lMap2
              }
            } else {
              var typeParamIds = ISZ[String]()
              var visibleParams = ISZ[IRTranslator.PatternField]()
              patternFactsOpt match {
                case Some(patternFacts) =>
                  patternFacts.owners.get(t.ids) match {
                    case Some(owner) if owner.kind == IRTranslator.PatternOwnerKind.Adt =>
                      typeParamIds = owner.typeParamIds
                      visibleParams = owner.visibleParams
                    case _ => halt(s"Infeasible pattern owner: ${(t.ids, ".")}")
                  }
                case _ =>
                  val adt = th.typeMap.get(t.ids).get.asInstanceOf[TypeInfo.Adt]
                  typeParamIds = for (typeParam <- adt.ast.typeParams) yield typeParam.id.value
                  visibleParams = IRTranslator.visiblePatternFields(adt)
              }
              val subst = tipe.TypeChecker.buildTypeSubstMapFromIds(t.ids, Some(pattern.pos),
                typeParamIds, t.args, message.Reporter.create).get
              var i = 0
              for (p <- visibleParams) {
                val pat = pattern.patterns(i)
                val fieldType = p.tipe.subst(subst)
                val f = AST.IR.Exp.FieldVarRef(baseExp, p.id, fieldType, pat.pos)
                val (pconds, lMap2) = translatePatternH(f, pat, lMap, patternFactsOpt)
                conds = conds ++ pconds
                lMap = lMap2
                i = i + 1
              }
            }
            r = r :+ AST.IR.condAnd(AST.IR.Exp.Type(T, exp, t, pos), AST.IR.bigAnd(conds, pos), pos)
          case _ => halt("Infeasible")
        }
      case pattern: AST.IR.Pattern.FieldRef =>
        r = r :+ AST.IR.Exp.Binary(AST.Typed.b, exp,
          AST.IR.Exp.Binary.Op.Eq, AST.IR.Exp.FieldVarRef(thiz(pos), pattern.id, pattern.tipe, pos), pos)
      case pattern: AST.IR.Pattern.GlobalRef =>
        r = r :+ AST.IR.Exp.Binary(AST.Typed.b, exp,
          AST.IR.Exp.Binary.Op.Eq, AST.IR.Exp.GlobalVarRef(pattern.owner :+ pattern.id, pattern.tipe, pos), pos)
      case pattern: AST.IR.Pattern.LocalRef =>
        val right: AST.IR.Exp = if (varCaptureSet.contains(pattern.id)) {
          val valueT = lowerByNameType(pattern.tipe)
          val mt = mboxType(valueT)
          val mboxRef = AST.IR.Exp.LocalVarRef(T, methodContext, pattern.id, mt, pos)
          AST.IR.Exp.FieldVarRef(mboxRef, "value", valueT, pos)
        } else {
          AST.IR.Exp.LocalVarRef(pattern.isVal, methodContext, pattern.id, pattern.tipe, pos)
        }
        r = r :+ AST.IR.Exp.Binary(AST.Typed.b, exp, AST.IR.Exp.Binary.Op.Eq, right, pos)
      case pattern: AST.IR.Pattern.EnumElementRef =>
        val right = AST.IR.Exp.EnumElementRef(pattern.owner, pattern.id, pattern.ordinal, pos)
        r = r :+ AST.IR.Exp.Binary(AST.Typed.b, exp, AST.IR.Exp.Binary.Op.Eq, right, pos)
      case _: AST.IR.Pattern.SeqWildcard => halt("Infeasible")
    }
    return (r, lMap)
  }


  @pure def sha3(s: String): U32 = {
    val sha = crypto.SHA3.init512
    sha.update(conversions.String.toU8is(s))
    val bs = sha.finalise()
    return conversions.U8.toU32(bs(0)) << u32"24" | conversions.U8.toU32(bs(1)) << u32"16" |
      conversions.U8.toU32(bs(2)) << u32"8" | conversions.U8.toU32(bs(3))
  }

  @strictpure def assignExpId(prefix: String, idOpt: Option[String], pos: message.Position): String = {
    st"${prefix}${idOpt.getOrElse("$ae")}.${pos.beginLine}.${pos.beginColumn}.${sha3(pos.string)}".render
  }

  @strictpure def matchExpId(pos: message.Position): String = {
    st"$$match.${pos.beginLine}.${pos.beginColumn}.${sha3(pos.string)}".render
  }
}
