// #Sireum
/*
 Copyright (c) 2020, Robby, Kansas State University
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

  @datatype class Program(val fileUriOpt: Option[String], packageName: Name, body: Body) extends TopUnit

  @datatype class SequentUnit(val fileUriOpt: Option[String], sequent: Sequent) extends TopUnit

  @datatype class TruthTableUnit(val fileUriOpt: Option[String],
                                 stars: ISZ[Position],
                                 vars: ISZ[Id],
                                 separator: Position,
                                 isSequent: B,
                                 sequent: Sequent,
                                 rows: ISZ[TruthTable.Row],
                                 conclusionOpt: Option[TruthTable.Conclusion]) extends TopUnit

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

  @pure def modifiedLocalVars: HashSMap[ResolvedInfo.LocalVar, (Typed, Position)] = {
    @pure def filterLocalVar(exp: Exp.Ident): ISZ[(ResolvedInfo.LocalVar, (Typed, Position))] = {
      exp.attr.resOpt match {
        case Some(res: ResolvedInfo.LocalVar) => return ISZ((res, (exp.typedOpt.get, exp.posOpt.get)))
        case _ => return ISZ()
      }
    }

    return HashSMap.empty[ResolvedInfo.LocalVar, (Typed, Position)] ++
      (for (mod <- modifies; res <- filterLocalVar(mod)) yield res)
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


  @pure def modifiedRecordVars: HashSMap[ResolvedInfo.Var, (Typed, Position)] = {
    @pure def filterRecordVar(exp: Exp.Ident): ISZ[(ResolvedInfo.Var, (Typed, Position))] = {
      exp.attr.resOpt match {
        case Some(res: ResolvedInfo.Var) if !res.isInObject => return ISZ((res, (exp.typedOpt.get, exp.posOpt.get)))
        case _ => return ISZ()
      }
    }

    return HashSMap.empty[ResolvedInfo.Var, (Typed, Position)] ++
      (for (mod <- modifies; res <- filterRecordVar(mod)) yield res)
  }

}

object Stmt {

  @datatype class Import(importers: ISZ[Import.Importer], @hidden attr: Attr) extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  object Import {

    @datatype class Importer(name: Name, selectorOpt: Option[Selector])

    @datatype trait Selector

    @datatype class MultiSelector(selectors: ISZ[NamedSelector]) extends Selector

    @datatype class WildcardSelector extends Selector

    @datatype class NamedSelector(from: Id, to: Id)

  }

  @datatype class Var(isSpec: B, isVal: B, id: Id, tipeOpt: Option[Type], initOpt: Option[AssignExp], @hidden attr: ResolvedAttr)
    extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class VarPattern(isSpec: B, isVal: B, pattern: Pattern, tipeOpt: Option[Type], init: AssignExp, @hidden attr: Attr)
    extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class SpecVar(isVal: B, id: Id, tipe: Type, @hidden attr: ResolvedAttr) extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class Method(typeChecked: B,
                         purity: Purity.Type,
                         hasOverride: B,
                         isHelper: B,
                         sig: MethodSig,
                         mcontract: MethodContract,
                         bodyOpt: Option[Body],
                         @hidden attr: ResolvedAttr) extends Stmt {

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

  @datatype class ExtMethod(isPure: B, sig: MethodSig, contract: MethodContract, @hidden attr: ResolvedAttr) extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class JustMethod(etaOpt: Option[Exp.LitString], sig: MethodSig, @hidden attr: ResolvedAttr) extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class SpecMethod(sig: MethodSig, @hidden attr: ResolvedAttr) extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class Enum(id: Id, elements: ISZ[Id], @hidden attr: Attr) extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class SubZ(id: Id,
                       isSigned: B,
                       isBitVector: B,
                       isWrapped: B,
                       hasMin: B,
                       hasMax: B,
                       bitWidth: Z,
                       min: Z,
                       max: Z,
                       isIndex: B,
                       index: Z,
                       @hidden attr: Attr) extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    def isZeroIndex: B = {
      return index == 0
    }
  }

  @datatype class Object(isApp: B, extNameOpt: Option[String], id: Id, stmts: ISZ[Stmt], @hidden attr: Attr) extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class Sig(isImmutable: B,
                      isExt: B,
                      id: Id,
                      typeParams: ISZ[TypeParam],
                      parents: ISZ[Type.Named],
                      stmts: ISZ[Stmt],
                      @hidden attr: Attr) extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class Adt(isRoot: B,
                      isDatatype: B,
                      id: Id,
                      typeParams: ISZ[TypeParam],
                      params: ISZ[AdtParam],
                      parents: ISZ[Type.Named],
                      stmts: ISZ[Stmt],
                      @hidden attr: Attr) extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class TypeAlias(id: Id, typeParams: ISZ[TypeParam], tipe: Type, @hidden attr: Attr) extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class Assign(lhs: Exp, rhs: AssignExp, @hidden attr: Attr) extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class Block(body: Body, @hidden attr: Attr) extends Stmt with AssignExp {

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

  @datatype class If(cond: Exp, thenBody: Body, elseBody: Body, @hidden attr: Attr) extends Stmt with AssignExp {

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

  @datatype class Match(exp: Exp, cases: ISZ[Case], @hidden attr: Attr) extends Stmt with AssignExp {

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

  @sig trait Loop extends HasModifies {
    @pure def context: ISZ[String]

    @pure def invariants: ISZ[Exp]

    @pure def maxItOpt: Option[Exp.LitZ]
  }

  @datatype class While(val context: ISZ[String],
                        val cond: Exp,
                        val invariants: ISZ[Exp],
                        val modifies: ISZ[Exp.Ident],
                        val maxItOpt: Option[Exp.LitZ],
                        val body: Body,
                        @hidden attr: Attr) extends Stmt with Loop {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class DoWhile(val context: ISZ[String],
                          val cond: Exp,
                          val invariants: ISZ[Exp],
                          val modifies: ISZ[Exp.Ident],
                          val maxItOpt: Option[Exp.LitZ],
                          val body: Body,
                          @hidden attr: Attr) extends Stmt with Loop {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class For(val context: ISZ[String],
                      val enumGens: ISZ[EnumGen.For],
                      val modifies: ISZ[Exp.Ident],
                      val body: Body,
                      @hidden attr: Attr) extends Stmt with Loop {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @strictpure override def invariants: ISZ[Exp] =
      for (enumGen <- enumGens; inv <- enumGen.invariants) yield inv

    @strictpure override def maxItOpt: Option[Exp.LitZ] = None()
  }

  @datatype class Return(expOpt: Option[Exp], @hidden attr: TypedAttr) extends Stmt with AssignExp {

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

  @datatype class Expr(exp: Exp, @hidden attr: TypedAttr) extends Stmt with AssignExp {

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

  @datatype class Fact(id: Id,
                       typeParams: ISZ[TypeParam],
                       descOpt: Option[Exp.LitString],
                       claims: ISZ[Exp],
                       @hidden attr: ResolvedAttr) extends Spec {
    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }
  }

  @datatype class Inv(id: Id,
                      claims: ISZ[Exp],
                      @hidden attr: ResolvedAttr) extends Spec {
    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }
  }

  @datatype class Theorem(isLemma: B,
                          id: Id,
                          typeParams: ISZ[TypeParam],
                          descOpt: Option[Exp.LitString],
                          claim: Exp,
                          isFun: B,
                          proof: ProofAst,
                          @hidden attr: ResolvedAttr) extends Spec {
    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }
  }

  @datatype class DataRefinement(rep: Exp.Ident,
                                 refs: ISZ[Exp.Ident],
                                 claims: ISZ[Exp],
                                 @hidden attr: Attr) extends Spec {
    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }
  }

  @datatype class SpecLabel(id: Id) extends Spec {
    @pure override def posOpt: Option[Position] = {
      return id.attr.posOpt
    }
  }

  @datatype class SpecBlock(block: Block) extends Spec {
    @pure override def posOpt: Option[Position] = {
      return block.posOpt
    }
  }

  @datatype class DeduceSequent(justOpt: Option[Exp.LitString], sequents: ISZ[Sequent], @hidden attr: Attr) extends Spec {
    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }
  }

  @datatype class DeduceSteps(steps: ISZ[ProofAst.Step], @hidden attr: Attr) extends Spec {
    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }
  }

  @datatype class Havoc(args: ISZ[Exp.Ident], @hidden attr: Attr) extends Spec {
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

@datatype class Sequent(premises: ISZ[Exp],
                        conclusion: Exp,
                        steps: ISZ[ProofAst.Step],
                        @hidden attr: Attr)

@datatype class ProofAst(steps: ISZ[ProofAst.Step], @hidden attr: Attr)

object ProofAst {

  @datatype trait Step {
    def no: Exp.LitZ
  }

  object Step {

    @datatype class Regular(val no: Exp.LitZ, val claim: Exp, val just: Justification) extends Step {
      @strictpure def claimDeBruijn: Exp = Util.deBruijn(claim)
    }

    @datatype class Assume(val no: Exp.LitZ, val claim: Exp) extends Step {
      @strictpure def claimDeBruijn: Exp = Util.deBruijn(claim)
    }

    @datatype class Assert(val no: Exp.LitZ, val claim: Exp, val steps: ISZ[Step]) extends Step {
      @strictpure def claimDeBruijn: Exp = Util.deBruijn(claim)
    }

    @datatype class SubProof(val no: Exp.LitZ, val steps: ISZ[Step]) extends Step

    @datatype class Let(val no: Exp.LitZ, val params: ISZ[Let.Param], val steps: ISZ[Step]) extends Step

    object Let {

      @datatype class Param(id: Id, tipeOpt: Option[Type])

    }

    @datatype class StructInduction(val no: Exp.LitZ,
                                    val claim: Exp,
                                    val exp: Exp,
                                    val cases: ISZ[StructInduction.MatchCase],
                                    val defaultOpt: Option[StructInduction.MatchDefault]) extends Step {
      @strictpure def claimDeBruijn: Exp = Util.deBruijn(claim)
    }

    object StructInduction {

      @datatype class MatchCase(pattern: Pattern.Structure,
                                hypoOpt: Option[Assume],
                                steps: ISZ[Step])

      @datatype class MatchDefault(hypoOpt: Option[Assume],
                                   steps: ISZ[Step])
    }

    @datatype trait Justification {
      @pure def posOpt: Option[Position]
    }

    @datatype trait Inception extends Justification {
      @pure def witnesses: ISZ[Exp.LitZ]
    }

    object Justification {

      @datatype class Apply(val id: Exp, val args: ISZ[Exp]) extends Justification {
        @strictpure override def posOpt: Option[Position] = id.posOpt
        @pure def idString: String = {
          id match {
            case id: Exp.LitString => return id.value
            case id: Exp.Ident => return id.id.value
            case id: Exp.Select => return id.id.value
            case _ => halt("Infeasible")
          }
        }
        @pure def isOwnedBy(name: ISZ[String]): B = {
          id match {
            case id: Exp.Ident => return id.attr.resOpt.get.asInstanceOf[ResolvedInfo.Method].owner == name
            case id: Exp.Select => return id.attr.resOpt.get.asInstanceOf[ResolvedInfo.Method].owner == name
            case _: Exp.LitString => return T
            case _ => halt("Infeasible")
          }
        }
        @pure def resOpt: Option[ResolvedInfo] = {
          id match {
            case id: Exp.Ident => return id.attr.resOpt
            case id: Exp.Select => return id.attr.resOpt
            case _: Exp.LitString => return None()
            case _ => halt("Infeasible")
          }
        }
      }

      @datatype class Incept(val invoke: Exp.Invoke, val witnesses: ISZ[Exp.LitZ]) extends Inception {
        @strictpure def invokeIdent: Exp.Ident = invoke.ident
        @strictpure def args: ISZ[Exp] = invoke.args
        @strictpure override def posOpt: Option[Position] = invoke.ident.posOpt
      }

      @datatype class InceptNamed(val invoke: Exp.InvokeNamed, val witnesses: ISZ[Exp.LitZ]) extends Inception {
        @strictpure def invokeIdent: Exp.Ident = invoke.ident
        @pure def args: ISZ[Exp] = {
          var r = MSZ.create[Option[Exp]](invoke.args.size, None())
          for (e <- invoke.args) {
            r(e.index) = Some(e.arg)
          }
          return for (arg <- r.toIS) yield arg.get
        }
        @strictpure override def posOpt: Option[Position] = invoke.ident.posOpt
      }

      @datatype class InceptEta(val eta: Exp.Eta, val witnesses: ISZ[Exp.LitZ]) extends Inception {
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
  'Impure
  'Pure
  'Memoize
  'StrictPure
}

@datatype class Case(pattern: Pattern, condOpt: Option[Exp], body: Body)

object EnumGen {

  @datatype trait Range

  object Range {

    @datatype class Expr(exp: Exp, @hidden attr: Attr) extends Range

    @datatype class Step(isInclusive: B, start: Exp, end: Exp, byOpt: Option[Exp], @hidden attr: Attr) extends Range

  }

  @datatype class For(idOpt: Option[Id],
                      range: Range,
                      condOpt: Option[Exp],
                      invariants: ISZ[Exp],
                      maxItOpt: Option[Exp.LitZ])

}

@datatype trait Type {

  @pure def posOpt: Option[Position]

  @pure def typedOpt: Option[Typed]

  @pure def typed(t: Typed): Type
}

object Type {

  @datatype class Named(name: Name, typeArgs: ISZ[Type], @hidden attr: TypedAttr) extends Type {

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

  }

  @datatype class Fun(isPure: B, isByName: B, args: ISZ[Type], ret: Type, @hidden attr: TypedAttr) extends Type {

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

  }

  @datatype class Tuple(args: ISZ[Type], @hidden attr: TypedAttr) extends Type {

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

  }

}

@datatype trait Pattern {
  @pure def posOpt: Option[Position]
  @pure def typedOpt: Option[Typed]
}

object Pattern {

  @datatype class Literal(lit: Lit) extends Pattern {

    @pure override def posOpt: Option[Position] = {
      return lit.posOpt
    }

    @strictpure override def typedOpt: Option[Typed] = lit.typedOpt

  }

  @datatype class LitInterpolate(prefix: String, value: String, @hidden attr: TypedAttr) extends Pattern {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt

  }

  @datatype class Ref(name: Name, @hidden attr: ResolvedAttr) extends Pattern {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt

  }

  @datatype class VarBinding(id: Id, tipeOpt: Option[Type], @hidden attr: TypedAttr) extends Pattern {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt
  }

  @datatype class Wildcard(typeOpt: Option[Type], @hidden attr: TypedAttr) extends Pattern {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt
  }

  @datatype class SeqWildcard(@hidden attr: TypedAttr) extends Pattern {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt
  }

  @datatype class Structure(idOpt: Option[Id],
                            nameOpt: Option[Name],
                            patterns: ISZ[Pattern],
                            @hidden attr: ResolvedAttr) extends Pattern {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt

  }

}

@datatype trait Exp {

  @pure def posOpt: Option[Position]

  @pure def typedOpt: Option[Typed]

}

@sig sealed trait Lit {

  @pure def posOpt: Option[Position]

  @pure def typedOpt: Option[Typed]

}

object Exp {

  @datatype class LitB(val value: B, @hidden attr: Attr) extends Exp with Lit {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return Typed.bOpt
    }
  }

  @datatype class LitC(val value: C, @hidden attr: Attr) extends Exp with Lit {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return Typed.cOpt
    }
  }

  @datatype class LitZ(val value: Z, @hidden attr: Attr) extends Exp with Lit {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return Typed.zOpt
    }
  }

  @datatype class LitF32(val value: F32, @hidden attr: Attr) extends Exp with Lit {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return Typed.f32Opt
    }
  }

  @datatype class LitF64(val value: F64, @hidden attr: Attr) extends Exp with Lit {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return Typed.f64Opt
    }
  }

  @datatype class LitR(val value: R, @hidden attr: Attr) extends Exp with Lit {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return Typed.rOpt
    }
  }

  @datatype class LitString(val value: String, @hidden attr: Attr) extends Exp with Lit {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return Typed.stringOpt
    }
  }

  @datatype class StringInterpolate(prefix: String, lits: ISZ[LitString], args: ISZ[Exp], @hidden attr: TypedAttr)
    extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }
  }

  @datatype class This(@hidden attr: TypedAttr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }
  }

  @datatype class Super(idOpt: Option[Id], @hidden attr: TypedAttr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }
  }

  @enum object UnaryOp {
    'Not
    'Plus
    'Minus
    'Complement
  }

  @datatype class Unary(op: UnaryOp.Type, exp: Exp, @hidden attr: ResolvedAttr) extends Exp {

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
  }

  @sig sealed trait Ref {
    @pure def targs: ISZ[Type]

    @pure def asExp: Exp

    @pure def subst(substMap: HashMap[String, Typed]): Ref
  }

  @datatype class Binary(left: Exp, op: String, right: Exp, @hidden attr: ResolvedAttr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }
  }

  object Ident {

    val targs: ISZ[Type] = ISZ()

  }

  @datatype class Ident(id: Id, @hidden attr: ResolvedAttr) extends Exp with Ref {

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
  }

  @datatype class Eta(ref: Ref, @hidden attr: TypedAttr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }
  }

  @datatype class Tuple(args: ISZ[Exp], @hidden attr: TypedAttr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }
  }

  @datatype class Select(receiverOpt: Option[Exp], id: Id, val targs: ISZ[Type], @hidden attr: ResolvedAttr)
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

  }

  @datatype class Invoke(receiverOpt: Option[Exp],
                         ident: Ident,
                         targs: ISZ[Type],
                         args: ISZ[Exp],
                         @hidden attr: ResolvedAttr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }
  }

  @datatype class InvokeNamed(receiverOpt: Option[Exp],
                              ident: Ident,
                              targs: ISZ[Type],
                              args: ISZ[NamedArg],
                              @hidden attr: ResolvedAttr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }
  }

  @datatype class If(cond: Exp, thenExp: Exp, elseExp: Exp, @hidden attr: TypedAttr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }
  }

  object Fun {

    @datatype class Param(idOpt: Option[Id], tipeOpt: Option[Type], typedOpt: Option[Typed])

  }

  @datatype class Fun(context: ISZ[String],
                      params: ISZ[Fun.Param],
                      exp: AssignExp,
                      @hidden attr: TypedAttr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }
  }

  @datatype class ForYield(enumGens: ISZ[EnumGen.For], exp: Exp, @hidden attr: TypedAttr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
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

  }

  @datatype class QuantRange(val isForall: B, lo: Exp, hi: Exp, hiExact: B, val fun: Exp.Fun, @hidden attr: ResolvedAttr) extends Quant {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return Typed.bOpt
    }

  }

  @datatype class QuantEach(val isForall: B, seq: Exp, val fun: Exp.Fun, @hidden attr: ResolvedAttr) extends Quant {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return Typed.bOpt
    }
  }

  @datatype class Input(exp: Exp, @hidden attr: Attr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return exp.typedOpt
    }
  }

  @datatype class OldVal(exp: Exp, @hidden attr: Attr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return exp.typedOpt
    }
  }

  @datatype class AtLoc(line: Z, idOpt: Option[Id], exp: Exp, @hidden attr: Attr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return exp.typedOpt
    }
  }

  @datatype class LoopIndex(tipeOpt: Option[Type], exp: Exp.Ident, @hidden attr: TypedAttr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      tipeOpt match {
        case Some(tipe) => return tipe.typedOpt
        case _ => return attr.typedOpt
      }
    }
  }

  @datatype class StateSeq(id: Id, fragments: ISZ[StateSeq.Fragment], @hidden attr: Attr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return Typed.bOpt
    }

  }

  object StateSeq {

    @datatype class Fragment(id: Id, exp: Exp)

  }

  @datatype class Result(tipeOpt: Option[Type], @hidden attr: TypedAttr) extends Exp {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }
  }

}

@datatype class NamedArg(id: Id, arg: Exp, index: Z)

@datatype class Id(value: String, @hidden attr: Attr)

@datatype class Name(ids: ISZ[Id], @hidden attr: Attr)

@datatype class Body(stmts: ISZ[Stmt], @hidden undecls: ISZ[ResolvedInfo.LocalVar]) {

  @pure def leaves: ISZ[Option[Stmt]] = {
    return if (stmts.isEmpty) ISZ(None()) else stmts(stmts.size - 1).leaves
  }
}

@datatype class AdtParam(isHidden: B, isVal: B, id: Id, tipe: Type)

@datatype class MethodSig(isPure: B,
                          id: Id,
                          typeParams: ISZ[TypeParam],
                          hasParams: B,
                          params: ISZ[Param],
                          returnType: Type) {

  @strictpure def paramIdTypes: ISZ[(Id, Typed)] = for (p <- params) yield (p.id, p.tipe.typedOpt.get)

  @pure def funType: Typed.Fun = {
    var pts = ISZ[Typed]()
    for (p <- params) {
      pts = pts :+ p.tipe.typedOpt.get
    }
    return Typed.Fun(isPure, !hasParams, pts, returnType.typedOpt.get)
  }
}

@datatype class Param(isHidden: B, id: Id, tipe: Type)

@datatype class TypeParam(id: Id)

@datatype class Attr(posOpt: Option[Position])

@datatype class TypedAttr(posOpt: Option[Position], typedOpt: Option[Typed])

@datatype class ResolvedAttr(posOpt: Option[Position], resOpt: Option[ResolvedInfo], typedOpt: Option[Typed])

@datatype trait ResolvedInfo {

  @pure def subst(substMap: HashMap[String, Typed]): ResolvedInfo = {
    return this
  }
}

object ResolvedInfo {

  object BuiltIn {

    @enum object Kind {
      'Apply
      'AsInstanceOf
      'Assert
      'AssertMsg
      'Assume
      'AssumeMsg
      'BinaryAdd
      'BinarySub
      'BinaryMul
      'BinaryDiv
      'BinaryRem
      'BinaryEq
      'BinaryNe
      'BinaryLt
      'BinaryLe
      'BinaryGt
      'BinaryGe
      'BinaryShl
      'BinaryShr
      'BinaryUshr
      'BinaryAnd
      'BinaryOr
      'BinaryXor
      'BinaryImply
      'BinaryCondAnd
      'BinaryCondOr
      'BinaryCondImply
      'BinaryMapsTo
      'Cprint
      'Cprintln
      'EnumByName
      'EnumByOrdinal
      'EnumElements
      'EnumNumOfElements
      'EnumName
      'EnumOrdinal
      'Eprint
      'Eprintln
      'Halt
      'Hash
      'IsInstanceOf
      'Indices
      'Min
      'Max
      'Random
      'Native
      'Print
      'Println
      'String
      'UnapplySeq
      'UnapplyTuple
      'UnaryPlus
      'UnaryMinus
      'UnaryNot
      'UnaryComplement
    }

  }

  @datatype class BuiltIn(kind: BuiltIn.Kind.Type) extends ResolvedInfo

  @datatype class Package(name: ISZ[String]) extends ResolvedInfo

  @datatype class Enum(name: ISZ[String]) extends ResolvedInfo

  @datatype class EnumElement(owner: ISZ[String], name: String, ordinal: Z) extends ResolvedInfo

  @datatype class Object(name: ISZ[String]) extends ResolvedInfo

  @datatype class Var(isInObject: B, isSpec: B, isVal: B, owner: ISZ[String], id: String) extends ResolvedInfo

  @datatype class Method(isInObject: B,
                         mode: MethodMode.Type,
                         typeParams: ISZ[String],
                         owner: ISZ[String],
                         id: String,
                         paramNames: ISZ[String],
                         tpeOpt: Option[Typed.Fun],
                         reads: ISZ[ResolvedInfo],
                         writes: ISZ[ResolvedInfo]) extends ResolvedInfo {

    @pure override def subst(substMap: HashMap[String, Typed]): ResolvedInfo = {
      tpeOpt match {
        case Some(tpe) => return Method(isInObject, mode, typeParams, owner, id, paramNames, Some(tpe.subst(substMap)),
          for (r <- reads) yield r.subst(substMap), for (w <- writes) yield w.subst(substMap))
        case _ => return this
      }
    }

  }

  @datatype class Methods(methods: ISZ[Method]) extends ResolvedInfo

  @datatype class Tuple(size: Z, index: Z) extends ResolvedInfo

  object LocalVar {

    @enum object Scope {
      'Current
      'Outer
      'Closure
    }

  }

  @datatype class LocalVar(context: ISZ[String], scope: ResolvedInfo.LocalVar.Scope.Type, isSpec: B, isVal: B, id: String)
    extends ResolvedInfo {
    @strictpure def isEqual(other: LocalVar): B = id == other.id && context == other.context
    @strictpure override def hash: Z = (context :+ id).hash
  }

  @datatype class Fact(name: ISZ[String]) extends ResolvedInfo

  @datatype class Theorem(name: ISZ[String]) extends ResolvedInfo

  @datatype class Inv(isInObject: B, owner: ISZ[String], id: String) extends ResolvedInfo

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

  @datatype class Row(assignment: Assignment, separator: Position, values: Assignment)

  @datatype class Assignment(values: ISZ[Exp.LitB], @hidden attr: Attr)

  @datatype trait Conclusion {
    def attr: Attr
  }

  object Conclusion {

    @datatype class Validity(isValid: B, assignments: ISZ[Assignment], @hidden val attr: Attr) extends Conclusion

    @datatype class Tautology(@hidden val attr: Attr) extends Conclusion

    @datatype class Contradictory(@hidden val attr: Attr) extends Conclusion

    @datatype class Contingent(trueAssignments: ISZ[Assignment],
                               falseAssignments: ISZ[Assignment],
                               @hidden val attr: Attr) extends Conclusion

  }

}
