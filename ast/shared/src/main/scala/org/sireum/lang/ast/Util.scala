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

package org.sireum.lang.ast

import org.sireum._
import org.sireum.message._
import org.sireum.U64._

object Util {

  @record class PatternVarBindingCollector(var result: ISZ[Id]) extends MTransformer {
    override def postPatternVarBinding(o: Pattern.VarBinding): MOption[Pattern] = {
      result = result :+ o.id
      return MTransformer.PostResultPatternVarBinding
    }

    override def postPatternStructure(o: Pattern.Structure): MOption[Pattern] = {
      o.idOpt match {
        case Some(id) => result = result :+ id
        case _ =>
      }
      return MTransformer.PostResultPatternStructure
    }
  }

  @record class ProofStepUniquenessChecker(var map: HashMap[Z, Position],
                                           val messageKind: String,
                                           val reporter: Reporter) extends MTransformer {
    override def preProofAstStep(o: ProofAst.Step): MTransformer.PreResult[ProofAst.Step] = {
      val no = o.no.value
      map.get(no) match {
        case Some(otherPos) => reporter.error(o.no.posOpt, messageKind,
          s"Proof step #$no has been declared at [${otherPos.beginLine}, ${otherPos.beginColumn}]")
        case _ => map = map + no ~> o.no.posOpt.get
      }
      return MTransformer.PreResultProofAstStepRegular
    }
  }


  @record class StrictPureChecker(val messageKind: String,
                                  val reporter: Reporter) extends MTransformer {
    override def postStmtMethod(o: Stmt.Method): MOption[Stmt] = {
      reporter.error(o.posOpt, messageKind, "@strictpure methods cannot define nested methods")
      return MTransformer.PostResultStmtMethod
    }

    override def postStmtVar(o: Stmt.Var): MOption[Stmt] = {
      if (!o.isVal) {
        reporter.error(o.posOpt, messageKind, "@strictpure methods cannot define vars")
      }
      return MTransformer.PostResultStmtVar
    }

    override def postStmtWhile(o: Stmt.While): MOption[Stmt] = {
      reporter.error(o.posOpt, messageKind, "@strictpure methods cannot use while-loops")
      return MTransformer.PostResultStmtWhile
    }

    override def postStmtFor(o: Stmt.For): MOption[Stmt] = {
      reporter.error(o.posOpt, messageKind, "@strictpure methods cannot use for-loops")
      return MTransformer.PostResultStmtFor
    }

    override def postStmtVarPattern(o: Stmt.VarPattern): MOption[Stmt] = {
      if (!o.isVal) {
        reporter.error(o.posOpt, messageKind, "@strictpure methods cannot define vars")
      }
      return MTransformer.PostResultStmtVarPattern
    }

    override def postStmtSpecVar(o: Stmt.SpecVar): MOption[Stmt] = {
      reporter.error(o.posOpt, messageKind, "@strictpure methods cannot define @spec val/var")
      return MTransformer.PostResultStmtSpecVar
    }

    override def postStmtSpecBlock(o: Stmt.SpecBlock): MOption[Stmt.Spec] = {
      reporter.error(o.posOpt, messageKind, "@strictpure methods cannot use Spec { ... } blocks")
      return MTransformer.PostResultStmtSpecBlock
    }

    override def postStmtSpecLabel(o: Stmt.SpecLabel): MOption[Stmt.Spec] = {
      reporter.error(o.posOpt, messageKind, "@strictpure methods cannot use spec labels")
      return MTransformer.PostResultStmtSpecLabel
    }

    override def postStmtAssign(o: Stmt.Assign): MOption[Stmt] = {
      reporter.error(o.posOpt, messageKind, "@strictpure methods cannot use assignments")
      return MTransformer.PostResultStmtAssign
    }
  }

  @record class ContractFormChecker(val messageKind: String,
                                    val reporter: Reporter) extends MTransformer {
    override def preExpQuantEach(o: Exp.QuantEach): MTransformer.PreResult[Exp.Quant] = {
      transformExp(o.seq)
      transformAssignExp(o.fun.exp)
      return MTransformer.PreResult(F, MNone())
    }

    override def preExpQuantRange(o: Exp.QuantRange): MTransformer.PreResult[Exp.Quant] = {
      transformExp(o.lo)
      transformExp(o.hi)
      transformAssignExp(o.fun.exp)
      return MTransformer.PreResult(F, MNone())
    }

    override def preExpQuantType(o: Exp.QuantType): MTransformer.PreResult[Exp.Quant] = {
      transformAssignExp(o.fun.exp)
      return MTransformer.PreResult(F, MNone())
    }

    override def preExpFun(o: Exp.Fun): MTransformer.PreResult[Exp] = {
      reporter.error(o.posOpt, messageKind, "Contracts should not define function/closure")
      return MTransformer.PreResult(F, MNone())
    }
  }

  @datatype class TypePrePostSubstitutor(substMap: HashMap[String, Typed]) extends Transformer.PrePost[B] {
    @pure override def preTypedTypeVar(ctx: B, o: Typed.TypeVar): Transformer.PreResult[B, Typed] = {
      substMap.get(o.id) match {
        case Some(t) => return Transformer.PreResult(ctx, T, Some(t))
        case _ =>
      }
      return Transformer.PreResult(ctx, T, None())
    }
  }

  @datatype class LocalVarContextPrePostSubstitutor(oldContext: ISZ[String], newContext: ISZ[String]) extends Transformer.PrePost[B] {
    @pure override def postResolvedInfoLocalVar(ctx: B, o: ResolvedInfo.LocalVar): Transformer.TPostResult[B, ResolvedInfo] = {
      if (o.context == oldContext) {
        return Transformer.TPostResult(ctx, Some(o(context = newContext)))
      }
      return Transformer.TPostResult(ctx, None())
    }
  }

  @datatype class QuantTypePrePostNormalizer extends Transformer.PrePost[B] {
    override def postExpQuantType(ctx: B, o: Exp.QuantType): Transformer.TPostResult[B, Exp.Quant] = {
      o.fun.exp match {
        case Stmt.Expr(body: Exp.QuantType) if o.isForall == body.isForall =>
          val params = o.fun.params ++ body.fun.params
          val exp = Transformer(LocalVarContextPrePostSubstitutor(body.fun.context, o.fun.context)).
            transformAssignExp(F, body.fun.exp).resultOpt.getOrElse(body.fun.exp)
          val funType = o.fun.typedOpt.get.asInstanceOf[Typed.Fun]
          val typedOpt: Option[Typed] = Some(funType(args = funType.args ++ body.fun.typedOpt.get.asInstanceOf[Typed.Fun].args))
          val newO = o(fun = o.fun(params = params, exp = exp, attr = o.fun.attr(typedOpt = typedOpt)))
          return Transformer.TPostResult(ctx, Some(newO))
        case _ =>
          return Transformer.TPostResult(ctx, None())
      }
    }
  }

  object FunDebruijner {
    @strictpure def create: Transformer[Z] = Transformer(FunPrePostDebruijner())

    @datatype class PrePostSubstitutor(oldContext: ISZ[String], newContext: ISZ[String], m: HashMap[String, String]) extends Transformer.PrePost[B] {
      override def postExpIdent(ctx: B, o: Exp.Ident): Transformer.TPostResult[B, Exp] = {
        o.attr.resOpt.get match {
          case res: ResolvedInfo.LocalVar if res.context == oldContext =>
            val newId = m.get(res.id).get
            val newO = o(id = o.id(value = newId), attr = o.attr(resOpt = Some(res(context = newContext, id = newId))))
            return Transformer.TPostResult(ctx, Some(newO))
          case _ =>
        }
        return Transformer.TPostResult(ctx, None())
      }
    }
  }

  @datatype class FunPrePostDebruijner extends Transformer.PrePost[Z] {
    override def preExpFun(ctx: Z, o: Exp.Fun): Transformer.PreResult[Z, Exp] = {
      val num = ctx
      val newContextId: String = s".$num"
      var m = HashMap.empty[String, String]
      var i: Z = 1
      var newParams = ISZ[Exp.Fun.Param]()
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
      val newExp = Transformer(FunDebruijner.PrePostSubstitutor(o.context, newContext, m)).transformAssignExp(F, o.exp).
        resultOpt.getOrElse(o.exp)
      val newO = o(context = newContext, params = newParams, exp = newExp)
      return Transformer.PreResult(ctx + 1, T, Some(newO))
    }
  }

  val symbolCharMap: HashMap[C, String] = HashMap ++ ISZ(
    '+' ~> "__plus",
    '-' ~> "__minus",
    '*' ~> "__star",
    '/' ~> "__slash",
    '%' ~> "__percent",
    '>' ~> "__gt",
    '<' ~> "__lt",
    '=' ~> "__eq",
    '!' ~> "__bang",
    '~' ~> "__tilde",
    '@' ~> "__at",
    '#' ~> "__pound",
    '$' ~> "__dollar",
    '^' ~> "__hat",
    '(' ~> "__lparen",
    ')' ~> "__rparen",
    '{' ~> "__lbrace",
    '}' ~> "__rbrace",
    '[' ~> "__lbracket",
    ']' ~> "__rbracket",
    ':' ~> "__colon",
    ';' ~> "__semi",
    '.' ~> "__dot",
    '?' ~> "__q",
    '\\' ~> "__bslash",
    ',' ~> "__comma",
  )

  @pure def ids2strings(ids: ISZ[Id]): ISZ[String] = {
    val r = MSZ.create[String](ids.size, "")
    for (i <- ids.indices) {
      r(i) = ids(i).value
    }
    return r.toIS
  }

  @pure def fileUriOptEq(posOpt1: Option[Position], posOpt2: Option[Position]): B = {
    (posOpt1, posOpt2) match {
      case (Some(pos1), Some(pos2)) => return pos1.uriOpt == pos2.uriOpt
      case _ => return F
    }
  }

  @pure def beginColumn(posOpt: Option[Position]): Z = {
//    l""" requires ∃pos: Position  posOpt == Some(pos) """
    posOpt match {
      case Some(pos) => return pos.beginColumn
      case _ => return 0
    }
  }

  @pure def beginColumnEqual(posOpt1: Option[Position], posOpt2: Option[Position]): B = {
    (posOpt1, posOpt2) match {
      case (Some(pos1), Some(pos2)) => return pos1.beginColumn == pos2.beginColumn
      case _ => return F
    }
  }

  @pure def posOptRangeA(attr1: Attr, attr2: Attr): Option[Position] = {
    return posOptRange(attr1.posOpt, attr2.posOpt)
  }

  @pure def posOptRangeTA(attr1: TypedAttr, attr2: TypedAttr): Option[Position] = {
    return posOptRange(attr1.posOpt, attr2.posOpt)
  }

  @pure def posOptRangeRA(attr1: ResolvedAttr, attr2: ResolvedAttr): Option[Position] = {
    return posOptRange(attr1.posOpt, attr2.posOpt)
  }

  @pure def posOptRange(posOpt1: Option[Position], posOpt2: Option[Position]): Option[Position] = {
    (posOpt1, posOpt2) match {
      case (Some(pos1: PosInfo), Some(pos2: PosInfo)) =>
        val offset = pos1.offset
        val length = pos2.offset + pos2.length - offset
        return Some(PosInfo(pos1.info, (conversions.Z.toU64(offset) << u64"32") | conversions.Z.toU64(length)))
      case (Some(pos1), Some(pos2)) =>
        return Some(
          FlatPos(
            pos1.uriOpt,
            conversions.Z.toU32(pos1.beginLine),
            conversions.Z.toU32(pos1.beginColumn),
            conversions.Z.toU32(pos2.endLine),
            conversions.Z.toU32(pos2.endColumn),
            conversions.Z.toU32(pos1.offset),
            conversions.Z.toU32(pos2.offset + pos2.length - pos1.length)
          )
        )
      case _ => return None()
    }
  }

  def unop(op: Exp.UnaryOp.Type): String = {
    op match {
      case Exp.UnaryOp.Not => return "!"
      case Exp.UnaryOp.Plus => return "+"
      case Exp.UnaryOp.Minus => return "-"
      case Exp.UnaryOp.Complement => return "~"
    }
  }

  def unopId(op: Exp.UnaryOp.Type): String = {
    op match {
      case Exp.UnaryOp.Not => return "unary_!"
      case Exp.UnaryOp.Plus => return "unary_+"
      case Exp.UnaryOp.Minus => return "unary_-"
      case Exp.UnaryOp.Complement => return "unary_~"
    }
  }

  @pure def isBoolBinop(op: String): B = {
    op match {
      case Exp.BinaryOp.Eq => return T
      case Exp.BinaryOp.Eq3 => return T
      case Exp.BinaryOp.Ne => return T
      case Exp.BinaryOp.Ne3 => return T
      case Exp.BinaryOp.And => return T
      case Exp.BinaryOp.Or => return T
      case Exp.BinaryOp.Xor => return T
      case Exp.BinaryOp.Imply => return T
      case Exp.BinaryOp.CondAnd => return T
      case Exp.BinaryOp.CondOr => return T
      case Exp.BinaryOp.CondImply => return T
      case _ => return F
    }
  }

  @pure def isArithBinop(op: String): B = {
    op match {
      case Exp.BinaryOp.Add => return T
      case Exp.BinaryOp.Sub => return T
      case Exp.BinaryOp.Mul => return T
      case Exp.BinaryOp.Div => return T
      case Exp.BinaryOp.Rem => return T
      case _ => return F
    }
  }

  @pure def isBitsBinop(op: String): B = {
    op match {
      case Exp.BinaryOp.Shl => return T
      case Exp.BinaryOp.Shr => return T
      case Exp.BinaryOp.Ushr => return T
      case Exp.BinaryOp.And => return T
      case Exp.BinaryOp.Or => return T
      case Exp.BinaryOp.Xor => return T
      case _ => return F
    }
  }

  @pure def isCompareBinop(op: String): B = {
    op match {
      case Exp.BinaryOp.Eq => return T
      case Exp.BinaryOp.Eq3 => return T
      case Exp.BinaryOp.Ne => return T
      case Exp.BinaryOp.Ne3 => return T
      case Exp.BinaryOp.Lt => return T
      case Exp.BinaryOp.Le => return T
      case Exp.BinaryOp.Gt => return T
      case Exp.BinaryOp.Ge => return T
      case _ => return F
    }
  }

  @pure def substAssignExp(ast: AssignExp, substMap: HashMap[String, Typed]): AssignExp = {
    if (substMap.nonEmpty) {
      return Transformer(TypePrePostSubstitutor(substMap)).transformAssignExp(F, ast).resultOpt.getOrElse(ast)
    } else {
      return ast
    }
  }

  @pure def substExp(ast: Exp, substMap: HashMap[String, Typed]): Exp = {
    if (substMap.nonEmpty) {
      return Transformer(TypePrePostSubstitutor(substMap)).transformExp(F, ast).resultOpt.getOrElse(ast)
    } else {
      return ast
    }
  }

  @pure def stableTypeSig(t: Typed, width: Z): ST = {
    val max: Z = if (0 < width && width <= 64) width else 64
    val bytes = ops.ISZOps(crypto.SHA3.sum512(conversions.String.toU8is(t.string))).take(max)
    var cs = ISZ[C]()
    for (b <- bytes) {
      val c = conversions.U32.toC(conversions.U8.toU32(b))
      cs = cs :+ ops.COps.hex2c((c >>> '\u0004') & '\u000F')
      cs = cs :+ ops.COps.hex2c(c & '\u000F')
    }
    return st"$cs"
  }

  @pure def stableTypeId(t: Typed, width: Z): (ST, B) = {
    t match {
      case t: Typed.Name => return if (t.args.isEmpty) (mangleName(t.ids), F) else (st"${mangleName(t.ids)}_${stableTypeSig(t, width)}", T)
      case t: Typed.Tuple => return (st"Tuple${t.args.size}_${stableTypeSig(t, width)}", T)
      case t: Typed.Fun => return (st"Fun${t.args.size}_${stableTypeSig(t, width)}", T)
      case _ => halt(s"Infeasible: $t")
    }
  }

  @pure def mangleName(ids: ISZ[String]): ST = {
    val r: ST =
      ids.size match {
        case z"0" => st"top"
        case z"1" => st"top_${ids(0)}"
        case _ => st"${(Typed.short(ids).map(encodeId _), "_")}"
      }
    return r
  }

  @pure def encodeId(id: String): ST = {
    id match {
      case Exp.BinaryOp.Add => return st"_add"
      case Exp.BinaryOp.Sub => return st"_sub"
      case Exp.BinaryOp.Mul => return st"_mul"
      case Exp.BinaryOp.Div => return st"_div"
      case Exp.BinaryOp.Rem => return st"_rem"
      case Exp.BinaryOp.Eq => return st"_eq"
      case Exp.BinaryOp.Ne => return st"_ne"
      case Exp.BinaryOp.Shl => return st"_lt"
      case Exp.BinaryOp.Shr => return st"_le"
      case Exp.BinaryOp.Ushr => return st"_gt"
      case Exp.BinaryOp.Lt => return st"_ge"
      case Exp.BinaryOp.Le => return st"_shl"
      case Exp.BinaryOp.Gt => return st"_shr"
      case Exp.BinaryOp.Ge => return st"_ushr"
      case Exp.BinaryOp.And => return st"_and"
      case Exp.BinaryOp.Or => return st"_or"
      case Exp.BinaryOp.Xor => return st"_xor"
      case Exp.BinaryOp.Append => return st"_append"
      case Exp.BinaryOp.Prepend => return st"_prepend"
      case Exp.BinaryOp.AppendAll => return st"_appendAll"
      case Exp.BinaryOp.RemoveAll => return st"_removeAll"
      case _ =>
        val cis = conversions.String.toCis(id)
        if (ops.ISZOps(cis).exists((c: C) => symbolCharMap.contains(c))) {
          var r = ISZ[String]()
          for (c <- cis) {
            symbolCharMap.get(c) match {
              case Some(s) => r = r :+ s
              case _ => r = r :+ s"$c"
            }
          }
          return st"$r"
        } else {
          return st"$id"
        }
    }
  }

  @pure def normalizeQuantType(exp: Exp): Exp = {
    return Transformer(QuantTypePrePostNormalizer()).transformExp(F, exp).resultOpt.getOrElseEager(exp)
  }

  @pure def deBruijn(exp: Exp): Exp = {
    val exp2 = FunDebruijner.create.transformExp(1, exp).resultOpt.getOrElseEager(exp)
    Transformer(QuantTypePrePostNormalizer()).transformExp(F, exp2).resultOpt match {
      case Some(exp3) => FunDebruijner.create.transformExp(1, exp3).resultOpt.getOrElseEager(exp3)
      case _ => return exp2
    }
  }

  @pure def isSeq(t: Typed): B = {
    t match {
      case t: Typed.Name => return t.ids == Typed.isName || t.ids == Typed.msName
      case _ => return F
    }
  }

  @pure def isSpec(exp: Exp): B = {
    exp match {
      case _: Exp.Quant => return T
      case _: Exp.Input => return T
      case _: Exp.OldVal => return T
      case _: Exp.LoopIndex => return T
      case _: Exp.StateSeq => return T
      case _: Exp.Result => return T
      case _ =>
    }
    return F
  }

  def checkScript(script: TopUnit.Program, reporter: Reporter): Unit = {
    for (stmt <- script.body.stmts) {
      def checkId(id: Id): Unit = {
        if (id.value === "args") {
          reporter.error(id.attr.posOpt, "Slang front-end", "Cannot introduce a top-level entity named args in Slang script")
        }
      }
      stmt match {
        case stmt: Stmt.Method => checkId(stmt.sig.id)
        case stmt: Stmt.Var => checkId(stmt.id)
        case stmt: Stmt.SpecMethod => checkId(stmt.sig.id)
        case stmt: Stmt.Fact => checkId(stmt.id)
        case stmt: Stmt.SpecVar => checkId(stmt.id)
        case stmt: Stmt.Theorem => checkId(stmt.id)
        case stmt: Stmt.VarPattern =>
          val pvbc = Util.PatternVarBindingCollector(ISZ())
          pvbc.transformPattern(stmt.pattern)
          for (id <- pvbc.result) {
            checkId(id)
          }
        case stmt: Stmt.Object => checkId(stmt.id)
        case _ =>
      }
    }
  }

}
