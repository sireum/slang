// #Sireum
/*
 Copyright (c) 2017-2022, Robby, Kansas State University
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

@datatype trait TopUnit {
  @pure def fileUriOpt: Option[String]
}

object TopUnit {

  object Program {

    @memoize def empty: Program = {
      return TopUnit.Program(None(), Name(ISZ(), Attr(None())), Body(ISZ(), ISZ()))
    }

  }

  @datatype class Program(val fileUriOpt: Option[String], val packageName: Name, val body: Body) extends TopUnit

  @datatype class SequentUnit(val fileUriOpt: Option[String], val sequent: Sequent) extends TopUnit

  @datatype class TruthTableUnit(val fileUriOpt: Option[String],
                                 val stars: ISZ[Position],
                                 val vars: ISZ[Id],
                                 val separator: Position,
                                 val isSequent: B,
                                 val sequent: Sequent,
                                 val rows: ISZ[TruthTable.Row],
                                 val conclusionOpt: Option[TruthTable.Conclusion]) extends TopUnit

}

@datatype trait Stmt {

  def posOpt: Option[Position]

  def asAssignExp: AssignExp = {
    halt(s"Invalid operation 'asAssignExp' on $this.")
  }

  @pure def leaves: ISZ[Option[Stmt]] = {
    return ISZ(Some(this))
  }
}

@sig trait HasModifies {

  @pure def modifies: ISZ[Exp.Ident]

  @pure def modifiedLocalVars(receiverLocalTypeOpt: Option[(ResolvedInfo.LocalVar, Typed)]): (B, HashSMap[ResolvedInfo.LocalVar, (Typed, Position)]) = {
    var r = HashSMap.empty[ResolvedInfo.LocalVar, (Typed, Position)]
    var modThisPosOpt: Option[Position] = None()
    for (exp <- modifies) {
      exp.attr.resOpt match {
        case Some(res: ResolvedInfo.LocalVar) => r = r + res ~> ((exp.typedOpt.get, exp.posOpt.get))
        case Some(res: ResolvedInfo.Var) if !res.isInObject => modThisPosOpt = exp.posOpt
        case _ =>
      }
    }
    var receiverModified: B = F
    modThisPosOpt match {
      case Some(modThisPos) =>
        val (lv, t) = receiverLocalTypeOpt.get
        r = r + lv ~> ((t, modThisPos))
        receiverModified = T
      case _ =>
    }
    return (receiverModified, r)
  }

  @pure def modifiedObjectVars: HashSMap[ResolvedInfo.Var, (Typed, Position)] = {
    @pure def filterObjectVar(exp: Exp.Ident): ISZ[(ResolvedInfo.Var, (Typed, Position))] = {
      exp.attr.resOpt match {
        case Some(res: ResolvedInfo.Var) if res.isInObject => return ISZ((res, (exp.typedOpt.get, exp.posOpt.get)))
        case _ => return ISZ()
      }
    }

    return HashSMap.empty[ResolvedInfo.Var, (Typed, Position)] ++
      (for (mod <- modifies; res <- filterObjectVar(mod)) yield res)
  }


  @pure def modifiedInstanceVars: HashSMap[ResolvedInfo.Var, (Typed, Position)] = {
    @pure def filterInstanceVar(exp: Exp.Ident): ISZ[(ResolvedInfo.Var, (Typed, Position))] = {
      exp.attr.resOpt match {
        case Some(res: ResolvedInfo.Var) if !res.isInObject => return ISZ((res, (exp.typedOpt.get, exp.posOpt.get)))
        case _ => return ISZ()
      }
    }

    return HashSMap.empty[ResolvedInfo.Var, (Typed, Position)] ++
      (for (mod <- modifies; res <- filterInstanceVar(mod)) yield res)
  }

}

object LoopContract {
  @strictpure def empty: LoopContract = LoopContract(ISZ(), ISZ(), None())
}

@datatype class LoopContract(val invariants: ISZ[Exp],
                             val modifies: ISZ[Exp.Ident],
                             val maxItOpt: Option[Exp.LitZ]) extends HasModifies

object Stmt {

  @datatype class Import(val importers: ISZ[Import.Importer], @hidden val attr: Attr) extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  object Import {

    @datatype class Importer(val name: Name, val selectorOpt: Option[Selector])

    @datatype trait Selector

    @datatype class MultiSelector(val selectors: ISZ[NamedSelector]) extends Selector

    @datatype class WildcardSelector extends Selector

    @datatype class NamedSelector(val from: Id, val to: Id)

  }

  @datatype class Var(val isSpec: B, val isVal: B, val id: Id, val tipeOpt: Option[Type], val initOpt: Option[AssignExp], @hidden val attr: ResolvedAttr)
    extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class VarPattern(val isSpec: B, val isVal: B, val pattern: Pattern, val tipeOpt: Option[Type], val init: AssignExp, @hidden val attr: Attr)
    extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class SpecVar(val isVal: B, val id: Id, val tipe: Type, @hidden val attr: ResolvedAttr) extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class Method(val typeChecked: B,
                         val purity: Purity.Type,
                         val hasOverride: B,
                         val isHelper: B,
                         val sig: MethodSig,
                         val mcontract: MethodContract,
                         val bodyOpt: Option[Body],
                         @hidden val attr: ResolvedAttr) extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure def hasContract: B = {
      if (mcontract.nonEmpty) {
        return T
      }
      bodyOpt match {
        case Some(Body(ISZ(Stmt.DeduceSequent(_, ISZ(_)), _*))) => return T
        case _ =>
      }
      return F
    }

    @pure def contract: MethodContract = {
      if (mcontract.isEmpty) {
        bodyOpt match {
          case Some(Body(ISZ(Stmt.DeduceSequent(_, ISZ(sequent)), _*))) =>
            val reqAttr: Attr = if (sequent.premises.isEmpty) {
              Attr(None())
            } else {
              val pos1 = sequent.premises(0).posOpt.get
              val pos2 = sequent.premises(sequent.premises.size - 1).posOpt.get
              Attr(Some(pos1.to(pos2)))
            }
            return MethodContract.Simple(MethodContract.Accesses.empty,
              MethodContract.Claims(sequent.premises, reqAttr),
              MethodContract.Accesses.empty,
              MethodContract.Claims(ISZ(sequent.conclusion), Attr(sequent.conclusion.posOpt)),
                Attr(sequent.attr.posOpt))
          case _ =>
        }
      }
      return mcontract
    }
  }

  @datatype class ExtMethod(val isPure: B, val sig: MethodSig, val contract: MethodContract, @hidden val attr: ResolvedAttr) extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class JustMethod(val etaOpt: Option[Exp.LitString], val sig: MethodSig, @hidden val attr: ResolvedAttr) extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class SpecMethod(val sig: MethodSig, @hidden val attr: ResolvedAttr) extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class Enum(val id: Id, val elements: ISZ[Id], @hidden val attr: Attr) extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class SubZ(val id: Id,
                       val isSigned: B,
                       val isBitVector: B,
                       val isWrapped: B,
                       val hasMin: B,
                       val hasMax: B,
                       val bitWidth: Z,
                       val min: Z,
                       val max: Z,
                       val isIndex: B,
                       val index: Z,
                       @hidden val attr: Attr) extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    def isZeroIndex: B = {
      return index == 0
    }
  }

  @datatype class Object(val isApp: B, val extNameOpt: Option[String], val id: Id, val stmts: ISZ[Stmt], @hidden val attr: Attr) extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class Sig(val isImmutable: B,
                      val isExt: B,
                      val id: Id,
                      val typeParams: ISZ[TypeParam],
                      val parents: ISZ[Type.Named],
                      val stmts: ISZ[Stmt],
                      @hidden val attr: Attr) extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class Adt(val isRoot: B,
                      val isDatatype: B,
                      val id: Id,
                      val typeParams: ISZ[TypeParam],
                      val params: ISZ[AdtParam],
                      val parents: ISZ[Type.Named],
                      val stmts: ISZ[Stmt],
                      @hidden val attr: Attr) extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class TypeAlias(val id: Id, val typeParams: ISZ[TypeParam], val tipe: Type, @hidden val attr: Attr) extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class Assign(val lhs: Exp, val rhs: AssignExp, @hidden val attr: Attr) extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class Block(val body: Body, @hidden val attr: Attr) extends Stmt with AssignExp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def asAssignExp: AssignExp = {
      return this
    }

    @pure override def asStmt: Stmt = {
      return this
    }

    @pure override def leaves: ISZ[Option[Stmt]] = {
      return body.leaves
    }
  }

  @datatype class If(val cond: Exp, val thenBody: Body, val elseBody: Body, @hidden val attr: Attr) extends Stmt with AssignExp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def asAssignExp: AssignExp = {
      return this
    }

    @pure override def asStmt: Stmt = {
      return this
    }

    @pure override def leaves: ISZ[Option[Stmt]] = {
      return thenBody.leaves ++ elseBody.leaves
    }
  }

  @datatype class Match(val exp: Exp, val cases: ISZ[Case], @hidden val attr: Attr) extends Stmt with AssignExp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def asAssignExp: AssignExp = {
      return this
    }

    @pure override def asStmt: Stmt = {
      return this
    }

    @pure override def leaves: ISZ[Option[Stmt]] = {
      return for (c <- cases; leaf <- c.body.leaves) yield leaf
    }

  }

  @datatype class While(val context: ISZ[String],
                        val cond: Exp,
                        val contract: LoopContract,
                        val body: Body,
                        @hidden val attr: Attr) extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @strictpure def invariants: ISZ[Exp] = contract.invariants

    @strictpure def modifies: ISZ[Exp.Ident] = contract.modifies

  }

  @datatype class DoWhile(val context: ISZ[String],
                          val cond: Exp,
                          val contract: LoopContract,
                          val body: Body,
                          @hidden val attr: Attr) extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @strictpure def invariants: ISZ[Exp] = contract.invariants

    @strictpure def modifies: ISZ[Exp.Ident] = contract.modifies

  }

  @datatype class For(val context: ISZ[String],
                      val enumGens: ISZ[EnumGen.For],
                      val body: Body,
                      @hidden val attr: Attr) extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @strictpure def modifies: ISZ[Exp.Ident] = enumGens(0).contract.modifies

  }

  @datatype class Return(val expOpt: Option[Exp], @hidden val attr: TypedAttr) extends Stmt with AssignExp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def asStmt: Stmt = {
      return this
    }

    @pure override def asAssignExp: AssignExp = {
      return this
    }

  }

  @datatype class Expr(val exp: Exp, @hidden val attr: TypedAttr) extends Stmt with AssignExp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }

    @pure override def asAssignExp: AssignExp = {
      return this
    }

    @pure override def asStmt: Stmt = {
      return this
    }

  }

  @datatype trait Spec extends Stmt

  @datatype class Fact(val id: Id,
                       val typeParams: ISZ[TypeParam],
                       val descOpt: Option[Exp.LitString],
                       val claims: ISZ[Exp],
                       val isFun: B,
                       @hidden val attr: ResolvedAttr) extends Spec {
    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }
  }

  @datatype class Inv(val id: Id,
                      val claims: ISZ[Exp],
                      @hidden val attr: ResolvedAttr) extends Spec {
    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }
  }

  @datatype class Theorem(val isLemma: B,
                          val id: Id,
                          val typeParams: ISZ[TypeParam],
                          val descOpt: Option[Exp.LitString],
                          val claim: Exp,
                          val isFun: B,
                          val proof: ProofAst,
                          @hidden val attr: ResolvedAttr) extends Spec {
    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }
  }

  @datatype class DataRefinement(val rep: Exp.Ident,
                                 val refs: ISZ[Exp.Ident],
                                 val claims: ISZ[Exp],
                                 @hidden val attr: Attr) extends Spec {
    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }
  }

  @datatype class SpecLabel(val id: Id) extends Spec {
    @pure override def posOpt: Option[Position] = {
      return id.attr.posOpt
    }
  }

  @datatype class SpecBlock(val block: Block) extends Spec {
    @pure override def posOpt: Option[Position] = {
      return block.posOpt
    }
  }

  @datatype class DeduceSequent(val justOpt: Option[Exp.LitString], val sequents: ISZ[Sequent], @hidden val attr: Attr) extends Spec {
    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }
  }

  @datatype class DeduceSteps(val steps: ISZ[ProofAst.Step], @hidden val attr: Attr) extends Spec {
    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }
  }

  @datatype class Havoc(val args: ISZ[Exp.Ident], @hidden val attr: Attr) extends Spec {
    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }
  }

}

@datatype trait MethodContract extends HasModifies {
  @pure def isEmpty: B
  @pure def nonEmpty: B = {
    return !isEmpty
  }
  @pure def reads: ISZ[Exp.Ident]
  @pure def modifies: ISZ[Exp.Ident]
}

object MethodContract {

  object Accesses {
    @strictpure def empty: Accesses = Accesses(ISZ(), Attr(None()))
  }

  @datatype class Accesses(val idents: ISZ[Exp.Ident], val attr: Attr)

  object Claims {
    @strictpure def empty: Claims = Claims(ISZ(), Attr(None()))
  }

  @datatype class Claims(val claims: ISZ[Exp], val attr: Attr)

  object Simple {
    @strictpure def empty: Simple = Simple(Accesses.empty, Claims.empty, Accesses.empty, Claims.empty, Attr(None()))
  }
  @datatype class Simple(val readsClause: Accesses,
                         val requiresClause: Claims,
                         val modifiesClause: Accesses,
                         val ensuresClause: Claims,
                         val attr: Attr) extends MethodContract {
    @strictpure override def reads: ISZ[Exp.Ident] = readsClause.idents
    @strictpure def requires: ISZ[Exp] = requiresClause.claims
    @strictpure override def modifies: ISZ[Exp.Ident] = modifiesClause.idents
    @strictpure def ensures: ISZ[Exp] = ensuresClause.claims

    @pure override def isEmpty: B = {
      return reads.isEmpty && requires.isEmpty && modifies.isEmpty && ensures.isEmpty
    }
  }

  @datatype class Cases(val readsClause: Accesses,
                        val modifiesClause: Accesses,
                        val cases: ISZ[Case],
                        val attr: Attr) extends MethodContract {
    @strictpure override def reads: ISZ[Exp.Ident] = readsClause.idents
    @strictpure override def modifies: ISZ[Exp.Ident] = modifiesClause.idents

    @pure override def isEmpty: B = {
      return reads.isEmpty && modifies.isEmpty && cases.isEmpty
    }
  }

  @datatype class Case(val label: Exp.LitString,
                       val requiresClause: Claims,
                       val ensuresClause: Claims) {
    @strictpure def requires: ISZ[Exp] = requiresClause.claims
    @strictpure def ensures: ISZ[Exp] = ensuresClause.claims
  }
}

@datatype class Sequent(val premises: ISZ[Exp],
                        val conclusion: Exp,
                        val steps: ISZ[ProofAst.Step],
                        @hidden val attr: Attr)

@datatype class ProofAst(val steps: ISZ[ProofAst.Step], @hidden val attr: Attr)

object ProofAst {

  @datatype trait Step {
    def id: StepId
  }

  @datatype trait StepId {
    @pure def attr: Attr
    @strictpure def posOpt: Option[Position] = attr.posOpt
  }

  object StepId {
    @datatype class Num(val no: Z, @hidden val attr: Attr) extends StepId {
      override def string: String = {
        return s"#$no"
      }
    }
    @datatype class Str(val value: String, @hidden val attr: Attr) extends StepId {
      override def string: String = {
        return s""""${ops.StringOps(value).escapeST.render}""""
      }
    }
  }

  object Step {

    @datatype class Regular(val id: StepId, val claim: Exp, val just: Justification) extends Step {
      @strictpure def claimNorm: Exp = Util.normalizeExp(claim)
    }

    @datatype class Assume(val id: StepId, val claim: Exp) extends Step {
      @strictpure def claimNorm: Exp = Util.normalizeExp(claim)
    }

    @datatype class Assert(val id: StepId, val claim: Exp, val steps: ISZ[Step]) extends Step {
      @strictpure def claimNorm: Exp = Util.normalizeExp(claim)
    }

    @datatype class SubProof(val id: StepId, val steps: ISZ[Step]) extends Step

    @datatype class Let(val id: StepId, val params: ISZ[Let.Param], val steps: ISZ[Step]) extends Step

    object Let {

      @datatype class Param(val id: Id, val tipeOpt: Option[Type])

    }

    @datatype class StructInduction(val id: StepId,
                                    val claim: Exp,
                                    val exp: Exp,
                                    val cases: ISZ[StructInduction.MatchCase],
                                    val defaultOpt: Option[StructInduction.MatchDefault]) extends Step {
      @strictpure def claimNorm: Exp = Util.normalizeExp(claim)
    }

    object StructInduction {

      @datatype class MatchCase(val pattern: Pattern.Structure,
                                val hypoOpt: Option[Assume],
                                val steps: ISZ[Step])

      @datatype class MatchDefault(val hypoOpt: Option[Assume],
                                   val steps: ISZ[Step])
    }

    @datatype trait Justification {
      @pure def posOpt: Option[Position]
    }

    @datatype trait Inception extends Justification {
      @pure def witnesses: ISZ[ProofAst.StepId]
    }

    object Justification {

      @datatype class Ref(val id: Exp.Ref) extends Justification {
        @strictpure override def posOpt: Option[Position] = id.asExp.posOpt
        @pure def idString: String = {
          id match {
            case id: Exp.Ident => return id.id.value
            case id: Exp.Select => return id.id.value
            case _ => halt("Infeasible")
          }
        }
        @pure def isOwnedBy(name: ISZ[String]): B = {
          id match {
            case id: Exp.Ident => return id.attr.resOpt.get.asInstanceOf[ResolvedInfo.Method].owner == name
            case id: Exp.Select => return id.attr.resOpt.get.asInstanceOf[ResolvedInfo.Method].owner == name
            case _ => halt("Infeasible")
          }
        }
        @pure def resOpt: Option[ResolvedInfo] = {
          id match {
            case id: Exp.Ident => return id.attr.resOpt
            case id: Exp.Select => return id.attr.resOpt
            case _ => halt("Infeasible")
          }
        }
      }

      @datatype class Apply(val invoke: Exp.Invoke, val witnesses: ISZ[ProofAst.StepId]) extends Inception {
        @strictpure def invokeIdent: Exp.Ident = invoke.ident
        @strictpure def args: ISZ[Exp] = invoke.args
        @strictpure override def posOpt: Option[Position] = invoke.ident.posOpt
      }

      @datatype class ApplyNamed(val invoke: Exp.InvokeNamed, val witnesses: ISZ[ProofAst.StepId]) extends Inception {
        @strictpure def invokeIdent: Exp.Ident = invoke.ident
        @pure def args: ISZ[Exp] = {
          val r = MSZ.create[Option[Exp]](invoke.args.size, None())
          for (e <- invoke.args) {
            r(e.index) = Some(e.arg)
          }
          return for (arg <- r.toIS) yield arg.get
        }
        @strictpure override def posOpt: Option[Position] = invoke.ident.posOpt
      }

      @datatype class ApplyEta(val eta: Exp.Eta, val witnesses: ISZ[ProofAst.StepId]) extends Inception {
        @strictpure override def posOpt: Option[Position] = eta.posOpt
      }

    }

  }

}

@sig sealed trait AssignExp {

  @pure def asStmt: Stmt

  @pure def exprs: ISZ[Stmt.Expr] = {
    this match {
      case stmt: Stmt.Expr => return ISZ(stmt)
      case stmt: Stmt.Block =>
        return ops.ISZOps(stmt.body.stmts).last.asAssignExp.exprs
      case stmt: Stmt.If =>
        return ops.ISZOps(stmt.thenBody.stmts).last.asAssignExp.exprs ++
          ops.ISZOps(stmt.elseBody.stmts).last.asAssignExp.exprs
      case stmt: Stmt.Match =>
        return for (c <- stmt.cases;
                    e <- ops.ISZOps(c.body.stmts).last.asAssignExp.exprs)
          yield e
      case _: Stmt.Return => return ISZ()
    }
  }

}

@enum object Purity {
  "Impure"
  "Pure"
  "Memoize"
  "StrictPure"
}

@datatype class Case(val pattern: Pattern, val condOpt: Option[Exp], val body: Body)

object EnumGen {

  @datatype trait Range {
    @pure def prettyST: ST
  }

  object Range {

    @datatype class Expr(val exp: Exp, @hidden val attr: Attr) extends Range {
      @pure override def prettyST: ST = {
        return exp.prettyST
      }
    }

    @datatype class Step(val isInclusive: B, val start: Exp, val end: Exp, val byOpt: Option[Exp], @hidden val attr: Attr) extends Range {
      @pure override def prettyST: ST = {
        val bOpt: Option[ST] = byOpt match {
          case Some(by) => Some(st" by ${by.prettyST}")
          case _ => None()
        }
        return st"${start.prettyST} ${if (isInclusive) "to" else "until"} ${end.prettyST}$bOpt"
      }
    }

  }

  @datatype class For(val idOpt: Option[Id],
                      val range: Range,
                      val condOpt: Option[Exp],
                      val contract: LoopContract) {
    @pure def prettyST: ST = {
      val id: String = idOpt match {
        case Some(id) => id.value
        case _ => "_"
      }
      val cOpt: Option[ST] = condOpt match {
        case Some(cond) => Some(st" if ${cond.prettyST}")
        case _ => None()
      }
      return st"$id <- ${range.prettyST}$cOpt"
    }
  }

}

@datatype trait Type {

  @pure def posOpt: Option[Position]

  @pure def typedOpt: Option[Typed]

  @pure def typed(t: Typed): Type

  @pure def prettyST: ST

}

object Type {

  @datatype class Named(val name: Name, val typeArgs: ISZ[Type], @hidden val attr: TypedAttr) extends Type {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }

    @pure override def typed(t: Typed): Named = {
      return this (name, typeArgs, attr(typedOpt = Some(t)))
    }

    @pure def isEqual(other: Named): B = {
      (typedOpt, other.typedOpt) match {
        case (Some(t1), Some(t2)) => return t1 == t2
        case _ => return name == other.name && typeArgs == other.typeArgs
      }
    }

    @pure override def hash: Z = {
      typedOpt match {
        case Some(t) => return t.hash
        case _ => return (name, typeArgs).hash
      }
    }

    @pure override def prettyST: ST = {
      val typeArgsOpts: Option[ST] =
        if (typeArgs.isEmpty) None() else Some(st"[${(for (ta <- typeArgs) yield ta.prettyST, ", ")}]")
      return st"${(for (id <- name.ids) yield id.value, ".")}$typeArgsOpts"
    }

    override def string: String = {
      typedOpt match {
        case Some(t) => return t.string
        case _ => return st"Named($name, $typeArgs)".render
      }
    }

  }

  @datatype class Fun(val isPure: B, val isByName: B, val args: ISZ[Type], val ret: Type, @hidden val attr: TypedAttr) extends Type {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }

    @pure override def typed(t: Typed): Fun = {
      return this (isPure, isByName, args, ret, attr(typedOpt = Some(t)))
    }

    @pure def isEqual(other: Fun): B = {
      (typedOpt, other.typedOpt) match {
        case (Some(t1), Some(t2)) => return isPure == other.isPure && isByName == other.isByName && t1 == t2
        case _ => return isPure == other.isPure && isByName == other.isByName && args == other.args && ret == other.ret
      }
    }

    @pure override def hash: Z = {
      typedOpt match {
        case Some(t) => return (isPure, isByName, t).hash
        case _ => return (isPure, isByName, args, ret).hash
      }
    }

    @pure override def prettyST: ST = {
      if (isByName) {
        return st"=> ${ret.prettyST}"
      }
      val pureOpt: Option[ST] = if (isPure) Some(st" @pure") else None()
      return st"(${(for (arg <- args) yield arg.prettyST, ", ")}) => ${ret.prettyST} $pureOpt"
    }
  }

  @datatype class Tuple(val args: ISZ[Type], @hidden val attr: TypedAttr) extends Type {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }

    @pure override def typed(t: Typed): Tuple = {
      return this (args, attr(typedOpt = Some(t)))
    }

    @pure def isEqual(other: Tuple): B = {
      (typedOpt, other.typedOpt) match {
        case (Some(t1), Some(t2)) => return t1 == t2
        case _ => return args == other.args
      }
    }

    @pure override def hash: Z = {
      typedOpt match {
        case Some(t) => return t.hash
        case _ => return args.hash
      }
    }

    @pure override def prettyST: ST = {
      return st"(${(for (arg <- args) yield arg.prettyST, ", ")})"
    }
  }

}

@datatype trait Pattern {
  @pure def posOpt: Option[Position]
  @pure def typedOpt: Option[Typed]
}

object Pattern {

  @datatype class Literal(val lit: Lit) extends Pattern {

    @pure override def posOpt: Option[Position] = {
      return lit.posOpt
    }

    @strictpure override def typedOpt: Option[Typed] = lit.typedOpt

  }

  @datatype class LitInterpolate(val prefix: String, val value: String, @hidden val attr: TypedAttr) extends Pattern {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt

  }

  @datatype class Ref(val name: Name, @hidden val attr: ResolvedAttr) extends Pattern {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt

  }

  @datatype class VarBinding(val id: Id, val tipeOpt: Option[Type], @hidden val attr: TypedAttr) extends Pattern {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt
  }

  @datatype class Wildcard(val typeOpt: Option[Type], @hidden val attr: TypedAttr) extends Pattern {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt
  }

  @datatype class SeqWildcard(@hidden val attr: TypedAttr) extends Pattern {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt
  }

  @datatype class Structure(val idOpt: Option[Id],
                            val nameOpt: Option[Name],
                            val patterns: ISZ[Pattern],
                            @hidden val attr: ResolvedAttr) extends Pattern {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt

  }

}

@datatype trait Exp {

  @pure def posOpt: Option[Position]

  @pure def typedOpt: Option[Typed]

  @pure def prettyST: ST

  override def string: String = {
    return prettyST.render
  }
}

@sig sealed trait Lit {

  @pure def posOpt: Option[Position]

  @pure def typedOpt: Option[Typed]

}

object Exp {

  @datatype class LitB(val value: B, @hidden val attr: Attr) extends Exp with Lit {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return Typed.bOpt
    }

    @pure override def prettyST: ST = {
      return if (value) st"T" else st"F"
    }
  }

  @datatype class LitC(val value: C, @hidden val attr: Attr) extends Exp with Lit {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return Typed.cOpt
    }

    @pure override def prettyST: ST = {
      return st"'${ops.COps(value).escapeString}'"
    }
  }

  @datatype class LitZ(val value: Z, @hidden val attr: Attr) extends Exp with Lit {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return Typed.zOpt
    }

    @pure override def prettyST: ST = {
      return st"${value}"
    }
  }

  @datatype class LitF32(val value: F32, @hidden val attr: Attr) extends Exp with Lit {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return Typed.f32Opt
    }

    @pure override def prettyST: ST = {
      return st"${value}f"
    }
  }

  @datatype class LitF64(val value: F64, @hidden val attr: Attr) extends Exp with Lit {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return Typed.f64Opt
    }

    @pure override def prettyST: ST = {
      return st"${value}d"
    }
  }

  @datatype class LitR(val value: R, @hidden val attr: Attr) extends Exp with Lit {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return Typed.rOpt
    }

    @pure override def prettyST: ST = {
      return st"""r"$value""""
    }
  }

  @datatype class LitString(val value: String, @hidden val attr: Attr) extends Exp with Lit {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return Typed.stringOpt
    }

    @pure override def prettyST: ST = {
      return st""""${ops.StringOps(value).escapeST}""""
    }
  }

  @datatype class LitStepId(val value: String, @hidden val attr: Attr) extends Exp with Lit {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return Typed.zOpt
    }

    @pure override def prettyST: ST = {
      return st"""sn"${ops.StringOps(value).escapeST}""""
    }
  }

  @datatype class StringInterpolate(val prefix: String, val lits: ISZ[LitString], val args: ISZ[Exp], @hidden val attr: TypedAttr)
    extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }

    @pure override def prettyST: ST = {
      var sts = ISZ[ST]()
      var i: Z = 0
      while (i < lits.size || i < args.size) {
        if (i < lits.size) {
          sts = sts :+ ops.StringOps(lits(i).value).escapeST
        }
        if (i < args.size) {
          sts = sts :+ st"$${${args(i).prettyST}}"
        }
        i = i + 1
      }
      return st"""$prefix"${(sts, "")}""""
    }
  }

  @datatype class This(@hidden val attr: TypedAttr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }

    @pure override def prettyST: ST = {
      return st"this"
    }
  }

  @datatype class Super(val idOpt: Option[Id], @hidden val attr: TypedAttr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }

    @pure override def prettyST: ST = {
      idOpt match {
        case Some(id) => return st"super[${id.value}]"
        case _ => return st"super"
      }
    }
  }

  @enum object UnaryOp {
    "Not"
    "Plus"
    "Minus"
    "Complement"
  }

  @datatype class Unary(val op: UnaryOp.Type, val exp: Exp, @hidden val attr: ResolvedAttr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }

    @strictpure def opString: String = op match {
      case Exp.UnaryOp.Complement => "~"
      case Exp.UnaryOp.Minus => "-"
      case Exp.UnaryOp.Not => "!"
      case Exp.UnaryOp.Plus => "+"
    }

    @pure override def prettyST: ST = {
      val paren: B = exp match {
        case _: Exp.Ident => F
        case _: Lit => F
        case _ => T
      }
      return if (paren) st"$opString(${exp.prettyST})" else st"$opString${exp.prettyST}"
    }
  }

  object BinaryOp {
    val Add: String = "+"
    val Sub: String = "-"
    val Mul: String = "*"
    val Div: String = "/"
    val Rem: String = "%"
    val Eq: String = "=="
    val Eq3: String = "==="
    val Ne: String = "!="
    val Ne3: String = "=!="
    val Shl: String = "<<"
    val Shr: String = ">>"
    val Ushr: String = ">>>"
    val Lt: String = "<"
    val Le: String = "<="
    val Gt: String = ">"
    val Ge: String = ">="
    val And: String = "&"
    val Or: String = "|"
    val Xor: String = "|^"
    val Imply: String = "->:"
    val CondAnd: String = "&&"
    val CondOr: String = "||"
    val CondImply: String = "-->:"
    val Append: String = ":+"
    val Prepend: String = "+:"
    val AppendAll: String = "++"
    val RemoveAll: String = "--"
    val MapsTo: String = "~>"

    def precendenceLevel(op: String): Z = {
      val c = conversions.String.toCis(op)(0)
      c match {
        case '*' => return 2
        case '/' => return 2
        case '%' => return 2
        case '+' => return 3
        case '-' => return 3
        case ':' => return 4
        case '=' => return 5
        case '!' => return 5
        case '<' => return 6
        case '>' => return 6
        case '&' => return 7
        case '^' => return 8
        case '|' => return 9
        case _ if ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') => return 10
        case _ => return 1
      }
    }
  }

  @sig sealed trait Ref {
    @pure def targs: ISZ[Type]

    @pure def asExp: Exp

    @pure def subst(substMap: HashMap[String, Typed]): Ref
  }

  @datatype class Binary(val left: Exp, val op: String, val right: Exp, @hidden val attr: ResolvedAttr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }

    @pure override def prettyST: ST = {
      val l = BinaryOp.precendenceLevel(op)
      val leftST: ST = left match {
        case left: Binary if BinaryOp.precendenceLevel(left.op) > l => st"(${left.prettyST})"
        case _ => left.prettyST
      }
      val rightST: ST = right match {
        case right: Binary if BinaryOp.precendenceLevel(right.op) > l => st"(${right.prettyST})"
        case _ => right.prettyST
      }
      return st"$leftST $op $rightST"
    }
  }

  object Ident {

    val targs: ISZ[Type] = ISZ()

  }

  @datatype class Ident(val id: Id, @hidden val attr: ResolvedAttr) extends Exp with Ref {

    @pure override def targs: ISZ[Type] = {
      return Ident.targs
    }

    @pure override def asExp: Exp = {
      return this
    }

    @pure override def hash: Z = {
      attr.resOpt match {
        case Some(res) => return res.hash
        case _ => return id.hash
      }
    }

    @pure def isEqual(other: Ident): B = {
      (attr.resOpt, other.attr.resOpt) match {
        case (Some(res), Some(otherRes)) => return res == otherRes
        case _ => return id == other.id
      }
    }

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }

    @pure def subst(substMap: HashMap[String, Typed]): Ref = {
      if (substMap.isEmpty) {
        return this
      }
      return Ident(
        id,
        ResolvedAttr(posOpt, ResolvedInfo.substOpt(attr.resOpt, substMap), Typed.substOpt(attr.typedOpt, substMap))
      )
    }

    @pure override def prettyST: ST = {
      return st"${id.value}"
    }
  }

  @datatype class Eta(val ref: Ref, @hidden val attr: TypedAttr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }

    @pure override def prettyST: ST = {
      return st"${ref.asExp.prettyST} _"
    }
  }

  @datatype class Tuple(val args: ISZ[Exp], @hidden val attr: TypedAttr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }

    @pure override def prettyST: ST = {
      return st"(${(for (arg <- args) yield arg.prettyST, ", ")})"
    }
  }

  @datatype class Select(val receiverOpt: Option[Exp], val id: Id, @hidden val targs: ISZ[Type], @hidden val attr: ResolvedAttr)
    extends Exp with Ref {

    @pure override def asExp: Exp = {
      return this
    }

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }

    @pure def subst(substMap: HashMap[String, Typed]): Ref = {
      return Select(
        receiverOpt,
        id,
        targs,
        ResolvedAttr(attr.posOpt, ResolvedInfo.substOpt(attr.resOpt, substMap), Typed.substOpt(attr.typedOpt, substMap))
      )
    }

    @pure override def prettyST: ST = {
      val targsOpt: Option[ST] = if (targs.isEmpty) None() else Some(st"[${(targs, "")}]")
      return st"${receiverOpt.map((rcv: Exp) => st"${rcv.prettyST}.")}${id.value}$targsOpt"
    }

  }

  @datatype class Invoke(val receiverOpt: Option[Exp],
                         val ident: Ident,
                         @hidden val targs: ISZ[Type],
                         val args: ISZ[Exp],
                         @hidden val attr: ResolvedAttr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }

    @pure override def prettyST: ST = {
      val targsOpt: Option[ST] = if (targs.isEmpty) None() else Some(st"[${(targs, "")}]")
      val as = st"(${(for (arg <- args) yield arg.prettyST, ", ")})"
      return st"${receiverOpt.map((rcv: Exp) => st"${rcv.prettyST}.")}${ident.id.value}$targsOpt$as"
    }
  }

  @datatype class InvokeNamed(val receiverOpt: Option[Exp],
                              val ident: Ident,
                              @hidden val targs: ISZ[Type],
                              val args: ISZ[NamedArg],
                              @hidden val attr: ResolvedAttr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }

    @pure override def prettyST: ST = {
      val targsOpt: Option[ST] = if (targs.isEmpty) None() else Some(st"[${(for (targ <- targs) yield targ.prettyST, "")}]")
      val as = st"(${(for (arg <- args) yield st"${arg.id.value} = ${arg.arg.prettyST}", ", ")})"
      return st"${receiverOpt.map((rcv: Exp) => st"${rcv.prettyST}.")}${ident.id.value}$targsOpt$as"
    }
  }

  @datatype class If(val cond: Exp, val thenExp: Exp, val elseExp: Exp, @hidden val attr: TypedAttr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }

    @pure override def prettyST: ST = {
      return st"if (${cond.prettyST}) ${thenExp.prettyST} else ${elseExp.prettyST}"
    }
  }

  @datatype class TypeCond(val args: ISZ[Exp], val fun: Exp.Fun, @hidden val attr: Attr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return Typed.bOpt
    }

    @pure override def prettyST: ST = {
      return st"?(${(for (arg <- args) yield arg.prettyST, ", ")} ${fun.prettySTH(F)}"
    }
  }

  @datatype class Sym(val num: Z, @hidden val attr: TypedAttr) extends Exp {
    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }

    @pure override def prettyST: ST = {
      return st"cx!$num"
    }
  }

  object Fun {

    @datatype class Param(val idOpt: Option[Id], val tipeOpt: Option[Type], val typedOpt: Option[Typed]) {
      @pure def isEqual(other: Param): B = {
        if (idOpt != other.idOpt) {
          return F
        }
        (typedOpt, other.typedOpt) match {
          case (Some(t1), Some(t2)) => return t1 == t2
          case _ => return tipeOpt == other.tipeOpt
        }
      }
      @strictpure override def hash: Z = if (typedOpt.nonEmpty) (idOpt, typedOpt).hash else (idOpt, tipeOpt).hash
    }

  }

  @datatype class Fun(val context: ISZ[String],
                      val params: ISZ[Fun.Param],
                      val exp: AssignExp,
                      @hidden val attr: TypedAttr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }

    @pure def prettySTH(isParen: B): ST = {
      @pure def paramST(p: Fun.Param): ST = {
        val id: String = p.idOpt match {
          case Some(id) => id.value
          case _ => "_"
        }
        p.tipeOpt match {
          case Some(tipe) => return st"$id: ${tipe.prettyST}"
          case _ => return st"$id"
        }
      }
      val ps = st"(${(for (p <- params) yield paramST(p), ", ")})"
      exp match {
        case exp: Stmt.Expr =>
          return if (isParen)
            st"($ps => ${exp.exp.prettyST})" else
            st"""{ $ps =>
                |  ${exp.exp.prettyST}
                |}"""
        case _ => return st"$ps => { ... }"
      }
    }

    @pure override def prettyST: ST = {
      return prettySTH(T)
    }
  }

  @datatype class ForYield(val enumGens: ISZ[EnumGen.For], val exp: Exp, @hidden val attr: TypedAttr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }

    @pure override def prettyST: ST = {
      return st"for (${(for (enumGen <- enumGens) yield enumGen.prettyST, "; ")}) yield ${exp.prettyST}"
    }
  }

  @datatype trait Quant extends Exp {
    @pure def isForall: B
    @pure def fun: Exp.Fun
  }

  @datatype class QuantType(val isForall: B, val fun: Exp.Fun, @hidden val attr: Attr) extends Quant {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return Typed.bOpt
    }

    @pure override def prettyST: ST = {
      return st"${if (isForall) "∀" else "∃"}${fun.prettyST}"
    }

  }

  @datatype class QuantRange(val isForall: B, val lo: Exp, val hi: Exp, val hiExact: B, val fun: Exp.Fun, @hidden val attr: ResolvedAttr) extends Quant {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return Typed.bOpt
    }

    @pure override def prettyST: ST = {
      return st"${if (isForall) "∀" else "∃"}(${lo.prettyST} ${if (hiExact) "to" else "until"} ${hi.prettyST}})${fun.prettyST}"
    }

  }

  @datatype class QuantEach(val isForall: B, val seq: Exp, val fun: Exp.Fun, @hidden val attr: ResolvedAttr) extends Quant {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return Typed.bOpt
    }

    @pure override def prettyST: ST = {
      return st"${if (isForall) "∀" else "∃"}(${seq.prettyST})${fun.prettyST}"
    }
  }

  @datatype class Input(val exp: Exp, @hidden val attr: Attr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return exp.typedOpt
    }

    @pure override def prettyST: ST = {
      return st"In(${exp.prettyST})"
    }
  }

  @datatype class OldVal(val exp: Exp, @hidden val attr: Attr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return exp.typedOpt
    }

    @pure override def prettyST: ST = {
      return st"Old(${exp.prettyST})"
    }
  }

  @datatype class LoopIndex(val tipeOpt: Option[Type], val exp: Exp.Ident, @hidden val attr: TypedAttr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      tipeOpt match {
        case Some(tipe) => return tipe.typedOpt
        case _ => return attr.typedOpt
      }
    }

    @pure override def prettyST: ST = {
      val tOpt: Option[ST] = tipeOpt match {
        case Some(t) => Some(st"[${t.prettyST}]")
        case _ => None()
      }
      return st"Idx$tOpt(${exp.prettyST})"
    }
  }

  @datatype class StateSeq(val id: Id, val fragments: ISZ[StateSeq.Fragment], @hidden val attr: Attr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return Typed.bOpt
    }

    @pure override def prettyST: ST = {
      return st"${id.value} ~ ${(for (f <- fragments) yield f.prettyST, " ~ ")}"
    }

  }

  object StateSeq {

    @datatype class Fragment(val id: Id, val exp: Exp) {
      @pure def prettyST: ST = {
        return st"${id.value} ~ ${exp.prettyST}"
      }
    }

  }

  @datatype class Result(val tipeOpt: Option[Type], @hidden val attr: TypedAttr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }

    @pure override def prettyST: ST = {
      val tOpt: Option[ST] = tipeOpt match {
        case Some(t) => Some(st"[${t.prettyST}]")
        case _ => None()
      }
      return st"Res$tOpt"
    }
  }

}

@datatype class NamedArg(val id: Id, val arg: Exp, val index: Z)

@datatype class Id(val value: String, @hidden val attr: Attr)

@datatype class Name(val ids: ISZ[Id], @hidden val attr: Attr)

@datatype class Body(val stmts: ISZ[Stmt], @hidden val undecls: ISZ[ResolvedInfo.LocalVar]) {

  @pure def leaves: ISZ[Option[Stmt]] = {
    return if (stmts.isEmpty) ISZ(None()) else stmts(stmts.size - 1).leaves
  }

  @memoize def allReturns: B = {
    for (lOpt <- leaves) {
      lOpt match {
        case Some(_: Stmt.Return) =>
        case _ => return F
      }
    }
    return T
  }
}

@datatype class AdtParam(val isHidden: B, val isVal: B, val id: Id, val tipe: Type)

@datatype class MethodSig(val isPure: B,
                          val id: Id,
                          val typeParams: ISZ[TypeParam],
                          val hasParams: B,
                          val params: ISZ[Param],
                          val returnType: Type) {

  @strictpure def paramIdTypes: ISZ[(Id, Typed)] = for (p <- params) yield (p.id, p.tipe.typedOpt.get)

  @pure def funType: Typed.Fun = {
    var pts = ISZ[Typed]()
    for (p <- params) {
      pts = pts :+ p.tipe.typedOpt.get
    }
    return Typed.Fun(isPure, !hasParams, pts, returnType.typedOpt.get)
  }
}

@datatype class Param(val isHidden: B, val id: Id, val tipe: Type)

@datatype class TypeParam(val id: Id)

@datatype class Attr(val posOpt: Option[Position])

@datatype class TypedAttr(val posOpt: Option[Position], val typedOpt: Option[Typed])

@datatype class ResolvedAttr(@hidden val posOpt: Option[Position], val resOpt: Option[ResolvedInfo], val typedOpt: Option[Typed])

@datatype trait ResolvedInfo {

  @pure def subst(substMap: HashMap[String, Typed]): ResolvedInfo = {
    return this
  }
}

object ResolvedInfo {

  object BuiltIn {

    @enum object Kind {
      "Apply"
      "AsInstanceOf"
      "Assert"
      "AssertMsg"
      "Assume"
      "AssumeMsg"
      "BinaryAdd"
      "BinarySub"
      "BinaryMul"
      "BinaryDiv"
      "BinaryRem"
      "BinaryEq"
      "BinaryNe"
      "BinaryFpEq"
      "BinaryFpNe"
      "BinaryLt"
      "BinaryLe"
      "BinaryGt"
      "BinaryGe"
      "BinaryShl"
      "BinaryShr"
      "BinaryUshr"
      "BinaryAnd"
      "BinaryOr"
      "BinaryXor"
      "BinaryImply"
      "BinaryCondAnd"
      "BinaryCondOr"
      "BinaryCondImply"
      "BinaryMapsTo"
      "Cprint"
      "Cprintln"
      "EnumByName"
      "EnumByOrdinal"
      "EnumElements"
      "EnumNumOfElements"
      "EnumName"
      "EnumOrdinal"
      "Eprint"
      "Eprintln"
      "Halt"
      "Hash"
      "IsInstanceOf"
      "Indices"
      "Min"
      "Max"
      "Random"
      "Native"
      "Print"
      "Println"
      "String"
      "UnapplySeq"
      "UnapplyTuple"
      "UnaryPlus"
      "UnaryMinus"
      "UnaryNot"
      "UnaryComplement"
    }

  }

  @datatype class BuiltIn(val kind: BuiltIn.Kind.Type) extends ResolvedInfo

  @datatype class Package(val name: ISZ[String]) extends ResolvedInfo

  @datatype class Enum(val name: ISZ[String]) extends ResolvedInfo

  @datatype class EnumElement(val owner: ISZ[String], val name: String, val ordinal: Z) extends ResolvedInfo

  @datatype class Object(val name: ISZ[String]) extends ResolvedInfo

  @datatype class Var(val isInObject: B, val isSpec: B, val isVal: B, val owner: ISZ[String], val id: String) extends ResolvedInfo

  @datatype class Method(val isInObject: B,
                         val mode: MethodMode.Type,
                         val typeParams: ISZ[String],
                         val owner: ISZ[String],
                         val id: String,
                         val paramNames: ISZ[String],
                         val tpeOpt: Option[Typed.Fun],
                         val reads: ISZ[ResolvedInfo],
                         val writes: ISZ[ResolvedInfo]) extends ResolvedInfo {

    @pure override def subst(substMap: HashMap[String, Typed]): ResolvedInfo = {
      tpeOpt match {
        case Some(tpe) => return Method(isInObject, mode, typeParams, owner, id, paramNames, Some(tpe.subst(substMap)),
          for (r <- reads) yield r.subst(substMap), for (w <- writes) yield w.subst(substMap))
        case _ => return this
      }
    }

  }

  @datatype class Methods(val methods: ISZ[Method]) extends ResolvedInfo

  @datatype class Tuple(val size: Z, val index: Z) extends ResolvedInfo

  object LocalVar {

    @enum object Scope {
      "Current"
      "Outer"
      "Closure"
    }

  }

  @datatype class LocalVar(val context: ISZ[String], val scope: ResolvedInfo.LocalVar.Scope.Type, val isSpec: B, val isVal: B, val id: String)
    extends ResolvedInfo {
    @strictpure def isEqual(other: LocalVar): B = id == other.id && context == other.context
    @strictpure override def hash: Z = (context :+ id).hash
  }

  @datatype class Fact(val name: ISZ[String]) extends ResolvedInfo

  @datatype class Theorem(val name: ISZ[String]) extends ResolvedInfo

  @datatype class Inv(val isInObject: B, val owner: ISZ[String], val id: String) extends ResolvedInfo

  @pure def substOpt(resOpt: Option[ResolvedInfo], substMap: HashMap[String, Typed]): Option[ResolvedInfo] = {
    resOpt match {
      case Some(res) if substMap.nonEmpty =>
        val newRes = res.subst(substMap)
        return if (newRes == res) resOpt else Some(newRes)
      case _ => return resOpt
    }
  }
}

object TruthTable {

  @datatype class Row(val assignment: Assignment, val separator: Position, val values: Assignment)

  @datatype class Assignment(val values: ISZ[Exp.LitB], @hidden val attr: Attr)

  @datatype trait Conclusion {
    def attr: Attr
  }

  object Conclusion {

    @datatype class Validity(val isValid: B, val assignments: ISZ[Assignment], @hidden val attr: Attr) extends Conclusion

    @datatype class Tautology(@hidden val attr: Attr) extends Conclusion

    @datatype class Contradictory(@hidden val attr: Attr) extends Conclusion

    @datatype class Contingent(val trueAssignments: ISZ[Assignment],
                               val falseAssignments: ISZ[Assignment],
                               @hidden val attr: Attr) extends Conclusion

  }

}
