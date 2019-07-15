// #Sireum
/*
 Copyright (c) 2019, Robby, Kansas State University
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

  @datatype class Var(isVal: B, id: Id, tipeOpt: Option[Type], initOpt: Option[AssignExp], @hidden attr: ResolvedAttr)
    extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class VarPattern(isVal: B, pattern: Pattern, tipeOpt: Option[Type], init: AssignExp, @hidden attr: Attr)
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

  @datatype class Method(purity: Purity.Type,
                         hasOverride: B,
                         isHelper: B,
                         sig: MethodSig,
                         contract: MethodContract,
                         bodyOpt: Option[Body],
                         @hidden attr: ResolvedAttr) extends Stmt {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class ExtMethod(isPure: B, sig: MethodSig, contract: MethodContract, @hidden attr: ResolvedAttr) extends Stmt {

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

  }

  @sig trait Loop {
    @pure def context: ISZ[String]

    @pure def invariants: ISZ[Exp]

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

    def modifiedObjectVars: HashSMap[ResolvedInfo.Var, (Typed, Position)] = {
      @pure def filterObjectVar(exp: Exp.Ident): ISZ[(ResolvedInfo.Var, (Typed, Position))] = {
        exp.attr.resOpt match {
          case Some(res: ResolvedInfo.Var) if res.isInObject => return ISZ((res, (exp.typedOpt.get, exp.posOpt.get)))
          case _ => return ISZ()
        }
      }

      return HashSMap.empty[ResolvedInfo.Var, (Typed, Position)] ++
        (for (mod <- modifies; res <- filterObjectVar(mod)) yield res)
    }


    def modifiedRecordVars: HashSMap[ResolvedInfo.Var, (Typed, Position)] = {
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

  @datatype class While(val context: ISZ[String],
                        cond: Exp,
                        val invariants: ISZ[Exp],
                        val modifies: ISZ[Exp.Ident],
                        body: Body,
                        @hidden attr: Attr) extends Stmt with Loop {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class DoWhile(val context: ISZ[String],
                          cond: Exp,
                          val invariants: ISZ[Exp],
                          val modifies: ISZ[Exp.Ident],
                          body: Body,
                          @hidden attr: Attr) extends Stmt with Loop {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class For(val context: ISZ[String],
                      enumGens: ISZ[EnumGen.For],
                      val invariants: ISZ[Exp],
                      val modifies: ISZ[Exp.Ident],
                      body: Body,
                      @hidden attr: Attr) extends Stmt with Loop {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

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
                       typeArgs: ISZ[TypeParam],
                       claims: ISZ[Exp],
                       @hidden attr: Attr) extends Spec {
    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }
  }

  @datatype class Invariant(id: Id,
                            claims: ISZ[Exp],
                            @hidden attr: Attr) extends Spec {
    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }
  }

  @datatype class Theorem(isLemma: B,
                          id: Id,
                          typeArgs: ISZ[TypeParam],
                          claim: Exp,
                          proof: Proof,
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

  @datatype class Deduce(sequents: ISZ[Sequent], @hidden attr: Attr) extends Spec {
    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }
  }

  @datatype class DeduceSteps(steps: ISZ[Proof.Step], @hidden attr: Attr) extends Spec {
    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }
  }

}

@datatype trait MethodContract

object MethodContract {

  @datatype class Simple(reads: ISZ[Exp.Ident],
                         requires: ISZ[Exp],
                         modifies: ISZ[Exp.Ident],
                         ensures: ISZ[Exp]) extends MethodContract

  @datatype class Cases(reads: ISZ[Exp.Ident],
                        modifies: ISZ[Exp.Ident],
                        cases: ISZ[Case]) extends MethodContract

  @datatype class Case(label: String,
                       requires: ISZ[Exp],
                       ensures: ISZ[Exp])

}

@datatype class Sequent(premises: ISZ[Exp],
                        conclusion: Exp,
                        steps: ISZ[Proof.Step],
                        @hidden attr: Attr)

@datatype class Proof(steps: ISZ[Proof.Step], @hidden attr: Attr)

object Proof {

  @datatype trait Step {
    def no: Exp.LitZ
  }

  object Step {

    @datatype class Regular(val no: Exp.LitZ, claim: Exp, just: Justification) extends Step

    @datatype class Assume(val no: Exp.LitZ, claim: Exp) extends Step

    @datatype class Assert(val no: Exp.LitZ, claim: Exp, steps: ISZ[Step]) extends Step

    @datatype class SubProof(val no: Exp.LitZ, steps: ISZ[Step]) extends Step

    @datatype class Let(val no: Exp.LitZ, params: ISZ[Let.Param], steps: ISZ[Step]) extends Step

    object Let {

      @datatype class Param(id: Id, tipeOpt: Option[Type])

    }

    @datatype class StructInduction(val no: Exp.LitZ,
                                    claim: Exp,
                                    exp: Exp,
                                    cases: ISZ[StructInduction.MatchCase],
                                    defaultOpt: Option[StructInduction.MatchDefault]) extends Step

    object StructInduction {

      @datatype class MatchCase(pattern: Pattern.Structure,
                                hypoOpt: Option[Assume],
                                steps: ISZ[Step])

      @datatype class MatchDefault(hypoOpt: Option[Assume],
                                   steps: ISZ[Step])
    }

    @datatype class Justification(id: Id, args: ISZ[Exp])

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

  @datatype class For(idOpt: Option[Id], range: Range, condOpt: Option[Exp])

}

@datatype trait Type {

  @pure def posOpt: Option[Position]

  @pure def typedOpt: Option[Typed]

  @pure def typed(t: Typed): Type
}

object Type {

  @datatype class Named(name: Name, typeArgs: ISZ[Type], attr: TypedAttr) extends Type {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }

    @pure override def typed(t: Typed): Named = {
      return this (name, typeArgs, attr(typedOpt = Some(t)))
    }

  }

  @datatype class Fun(isPure: B, isByName: B, args: ISZ[Type], ret: Type, attr: TypedAttr) extends Type {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }

    @pure override def typed(t: Typed): Fun = {
      return this (isPure, isByName, args, ret, attr(typedOpt = Some(t)))
    }

  }

  @datatype class Tuple(args: ISZ[Type], attr: TypedAttr) extends Type {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return attr.typedOpt
    }

    @pure override def typed(t: Typed): Tuple = {
      return this (args, attr(typedOpt = Some(t)))
    }

  }

}

@datatype trait Pattern {
  @pure def posOpt: Option[Position]
}

object Pattern {

  @datatype class Literal(lit: Lit) extends Pattern {

    @pure override def posOpt: Option[Position] = {
      return lit.posOpt
    }

  }

  @datatype class LitInterpolate(prefix: String, value: String, @hidden attr: TypedAttr) extends Pattern {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class Ref(name: Name, @hidden attr: ResolvedAttr) extends Pattern {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class VarBinding(id: Id, tipeOpt: Option[Type], @hidden attr: TypedAttr) extends Pattern {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class Wildcard(typeOpt: Option[Type], @hidden attr: TypedAttr) extends Pattern {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class SeqWildcard(@hidden attr: TypedAttr) extends Pattern {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

  }

  @datatype class Structure(
                             idOpt: Option[Id],
                             nameOpt: Option[Name],
                             patterns: ISZ[Pattern],
                             @hidden attr: ResolvedAttr
                           ) extends Pattern {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

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
  }

  object BinaryOp {
    val Add: String = "+"
    val Sub: String = "-"
    val Mul: String = "*"
    val Div: String = "/"
    val Rem: String = "%"
    val Eq: String = "=="
    val Ne: String = "!="
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
    val Imply: String = "imply_:"
    val CondAnd: String = "&&"
    val CondOr: String = "||"
    val CondImply: String = "simply_:"
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

    @datatype class Param(id: Id, tipeOpt: Option[Type], typedOpt: Option[Typed])

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

  @datatype trait Spec extends Exp

  @datatype class Quant(isForall: B, varFragments: ISZ[VarFragment], exp: Exp, @hidden attr: Attr) extends Spec {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return Typed.bOpt
    }
  }

}

@datatype class NamedArg(id: Id, arg: Exp, index: Z)

@datatype class VarFragment(id: Id, domainOpt: Option[Domain])

@datatype trait Domain {
  def attr: TypedAttr
}

object Domain {

  @datatype class Type(tipe: org.sireum.lang.ast.Type, @hidden val attr: TypedAttr) extends Domain

  @datatype class Range(lo: Exp, hi: Exp, hiExact: B, @hidden val attr: TypedAttr) extends Domain

  @datatype class Each(exp: Exp, @hidden val attr: TypedAttr) extends Domain
}

@datatype class Id(value: String, @hidden attr: Attr)

@datatype class Name(ids: ISZ[Id], @hidden attr: Attr)

@datatype class Body(stmts: ISZ[Stmt], @hidden undecls: ISZ[ResolvedInfo.LocalVar])

@datatype class AdtParam(isHidden: B, isVal: B, id: Id, tipe: Type)

@datatype class MethodSig(isPure: B,
                          id: Id,
                          typeParams: ISZ[TypeParam],
                          hasParams: B,
                          params: ISZ[Param],
                          returnType: Type) {

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

@datatype trait Typed {

  @pure def isPureFun: B

  @pure def subst(map: HashMap[String, Typed]): Typed

  @pure override def hash: Z = {
    this match {
      case t: Typed.Name => return (t.ids, t.args).hash
      case t: Typed.TypeVar => return t.id.hash
      case t: Typed.Tuple => return t.args.hash
      case t: Typed.Method => return t.name.hash
      case t: Typed.Enum => return t.name.hash
      case t: Typed.Object => return t.name.hash
      case t: Typed.Methods => return t.methods.hash
      case t: Typed.Package => return t.name.hash
      case t: Typed.Fun => return (t.args, t.ret).hash
    }
  }

  @pure def isEqual(other: Typed): B = {
    (this, other) match {
      case (t1: Typed.Name, t2: Typed.Name) =>
        if (t1.args.size != t2.args.size) {
          return F
        }
        if (t1.ids != t2.ids) {
          return F
        }
        for (i <- z"0" until t1.args.size) {
          if (t1.args(i) != t2.args(i)) {
            return F
          }
        }
        return T
      case (t1: Typed.Tuple, t2: Typed.Tuple) =>
        if (t1.args.size != t2.args.size) {
          return F
        }
        for (i <- z"0" until t1.args.size) {
          if (t1.args(i) != t2.args(i)) {
            return F
          }
        }
        return T
      case (t1: Typed.Fun, t2: Typed.Fun) =>
        if (t1.isPure != t2.isPure || t1.isByName != t2.isByName) {
          return F
        }
        if (t1.args.size != t2.args.size) {
          return F
        }
        if (t1.ret != t2.ret) {
          return F
        }
        for (i <- z"0" until t1.args.size) {
          if (t1.args(i) != t2.args(i)) {
            return F
          }
        }
        return T
      case (t1: Typed.TypeVar, t2: Typed.TypeVar) =>
        return t1.id == t2.id
      case (t1: Typed.Package, t2: Typed.Package) =>
        return t1.name == t2.name
      case (t1: Typed.Object, t2: Typed.Object) =>
        return t1.name == t2.name
      case (t1: Typed.Enum, t2: Typed.Enum) =>
        return t1.name == t2.name
      case (t1: Typed.Method, t2: Typed.Method) =>
        return t1.isInObject == t2.isInObject &&
          t1.mode == t2.mode &&
          t1.owner == t2.owner &&
          t1.name == t2.name &&
          t1.tpe == t2.tpe
      case (t1: Typed.Methods, t2: Typed.Methods) =>
        return t1.methods == t2.methods
      case _ =>
        if (this.isUnitType && other.isUnitType) {
          return T
        }
        return F
    }
  }

  @pure def deBruijn: Typed = {
    var map = HashMap.empty[String, Z]

    def dbFun(t: Typed.Fun): Typed.Fun = {
      var args = ISZ[Typed]()
      for (arg <- t.args) {
        val ta = db(arg)
        args = args :+ ta
      }
      val tr = db(t.ret)
      return t(args = args, ret = tr)
    }

    def dbMethod(t: Typed.Method): Typed.Method = {
      var newTypeParams = ISZ[String]()
      for (t <- t.typeParams) {
        val i: Z = map.get(t) match {
          case Some(n) => n
          case _ =>
            val n = map.size
            map = map + t ~> n
            n
        }
        newTypeParams = newTypeParams :+ s"$$$i"
      }
      t(typeParams = newTypeParams, tpe = dbFun(t.tpe))
    }

    def db(t: Typed): Typed = {
      t match {
        case t: Typed.Name =>
          if (t.args.nonEmpty) {
            var args = ISZ[Typed]()
            for (arg <- t.args) {
              val ta = db(arg)
              args = args :+ ta
            }
            return t(args = args)
          } else {
            return t
          }
        case t: Typed.Tuple =>
          var args = ISZ[Typed]()
          for (arg <- t.args) {
            val ta = db(arg)
            args = args :+ ta
          }
          return t(args = args)
        case t: Typed.Fun => return dbFun(t)
        case t: Typed.TypeVar =>
          val i: Z = map.get(t.id) match {
            case Some(n) => n
            case _ =>
              val n = map.size
              map = map + t.id ~> n
              n
          }
          return t(id = s"$$$i")
        case t: Typed.Enum => return t
        case t: Typed.Method => return dbMethod(t)
        case t: Typed.Object => return t
        case t: Typed.Package => return t
        case t: Typed.Methods =>
          var newMethods = ISZ[Typed.Method]()
          for (m <- t.methods) {
            val newM = dbMethod(m)
            newMethods = newMethods :+ newM
          }
          return t(newMethods)
      }
    }

    val r = db(this)
    return r
  }

  @pure def isUnitType: B = {
    this match {
      case t: Typed.Tuple if t.args.isEmpty => return T
      case t: Typed.Name => return t.args.isEmpty && t.ids == Typed.unit.ids
      case _ => return F
    }
  }

  @pure def typeVarSet: HashSSet[String] = {
    return HashSSet.empty[String] ++ collectTypeVars
  }

  @pure def collectTypeVars: ISZ[String]

  @pure def hasTypeVars: B
}

@enum object MethodMode {
  'Method
  'Spec
  'Ext
  'Constructor
  'Copy
  'Extractor
  'ObjectConstructor
  'Select
  'Store
}

object Typed {

  @datatype class Name(ids: ISZ[String], args: ISZ[Typed]) extends Typed {

    @pure override def isPureFun: B = {
      return F
    }

    @pure override def string: String = {
      return if (args.isEmpty) st"${(short(ids), ".")}".render
      else st"${(short(ids), ".")}[${(args, ", ")}]".render
    }

    @pure override def subst(m: HashMap[String, Typed]): Typed.Name = {
      if (m.isEmpty) {
        return this
      }
      return Name(ids, args.map(ta => ta.subst(m)))
    }

    @pure override def collectTypeVars: ISZ[String] = {
      return for (arg <- args; tv <- arg.collectTypeVars) yield tv
    }

    @pure override def hasTypeVars: B = {
      for (arg <- args if arg.hasTypeVars) {
        return T
      }
      return F
    }
  }

  @datatype class Tuple(args: ISZ[Typed]) extends Typed {

    @pure override def isPureFun: B = {
      return F
    }

    @pure override def string: String = {
      return st"(${(args, ", ")})".render
    }

    @pure override def subst(m: HashMap[String, Typed]): Typed.Tuple = {
      if (m.isEmpty) {
        return this
      }
      return Tuple(args.map(ta => ta.subst(m)))
    }

    @pure override def collectTypeVars: ISZ[String] = {
      return for (arg <- args; tv <- arg.collectTypeVars) yield tv
    }

    @pure override def hasTypeVars: B = {
      for (arg <- args if arg.hasTypeVars) {
        return T
      }
      return F
    }
  }

  @datatype class Fun(isPure: B, isByName: B, args: ISZ[Typed], ret: Typed) extends Typed {

    @pure override def isPureFun: B = {
      return isPure
    }

    @pure override def string: String = {
      return if (isByName) st"=> $ret".render
      else if (isPure) st"((${(args, ", ")}) => $ret @pure)".render
      else st"(${(args, ", ")}) => $ret".render
    }

    @pure override def subst(m: HashMap[String, Typed]): Typed.Fun = {
      if (m.isEmpty) {
        return this
      }
      return Fun(isPure, isByName, args.map(ta => ta.subst(m)), ret.subst(m))
    }

    @pure override def collectTypeVars: ISZ[String] = {
      return (for (arg <- args; tv <- arg.collectTypeVars) yield tv) ++ ret.collectTypeVars
    }

    @pure override def hasTypeVars: B = {
      for (arg <- args if arg.hasTypeVars) {
        return T
      }
      return ret.hasTypeVars
    }
  }

  @datatype class TypeVar(id: String) extends Typed {

    @pure override def isPureFun: B = {
      return F
    }

    @pure override def subst(m: HashMap[String, Typed]): Typed = {
      if (m.isEmpty) {
        return this
      }
      m.get(id) match {
        case Some(t2) => return t2
        case _ => return this
      }
    }

    @pure override def string: String = {
      return id
    }

    @pure override def collectTypeVars: ISZ[String] = {
      return ISZ(id)
    }

    @pure override def hasTypeVars: B = {
      return T
    }
  }

  @datatype class Package(name: ISZ[String]) extends Typed {

    @pure override def isPureFun: B = {
      return F
    }

    @pure override def string: String = {
      return st"Package ${(name, ".")}".render
    }

    @pure override def subst(m: HashMap[String, Typed]): Typed.Package = {
      return this
    }

    @pure override def collectTypeVars: ISZ[String] = {
      return ISZ()
    }

    @pure override def hasTypeVars: B = {
      return F
    }
  }

  @datatype class Object(owner: ISZ[String], id: String) extends Typed {

    @pure def name: ISZ[String] = {
      return owner :+ id
    }

    @pure override def isPureFun: B = {
      return F
    }

    @pure override def string: String = {
      return st"Object ${(name, ".")}".render
    }

    @pure override def subst(m: HashMap[String, Typed]): Typed.Object = {
      return this
    }

    @pure override def collectTypeVars: ISZ[String] = {
      return ISZ()
    }

    @pure override def hasTypeVars: B = {
      return F
    }
  }

  @datatype class Enum(name: ISZ[String]) extends Typed {

    @pure override def isPureFun: B = {
      return F
    }

    @pure override def string: String = {
      return st"@enum ${(name, ".")}".render
    }

    @pure override def subst(m: HashMap[String, Typed]): Typed.Enum = {
      return this
    }

    @pure override def collectTypeVars: ISZ[String] = {
      return ISZ()
    }

    @pure override def hasTypeVars: B = {
      return F
    }
  }

  @datatype class Method(isInObject: B,
                         mode: MethodMode.Type,
                         typeParams: ISZ[String],
                         owner: ISZ[String],
                         name: String,
                         paramNames: ISZ[String],
                         tpe: Typed.Fun) extends Typed {

    @pure override def isPureFun: B = {
      return F
    }

    @pure override def string: String = {
      val mST = st"${(owner, ".")}${if (isInObject) "." else "#"}$name"
      mode match {
        case MethodMode.Extractor => return st"extractor of $mST".render
        case MethodMode.Ext => return st"@ext object method $mST".render
        case MethodMode.Copy => return st"copy of $mST".render
        case MethodMode.Constructor => return st"constructor of $mST".render
        case MethodMode.Method => return st"method $mST".render
        case MethodMode.ObjectConstructor => return st"object constructor of $mST".render
        case MethodMode.Select => return st"indexing of $mST".render
        case MethodMode.Spec => return st"@spec method $mST".render
        case MethodMode.Store => return st"update of $mST".render
      }
    }

    @pure override def subst(m: HashMap[String, Typed]): Typed.Method = {
      if (m.isEmpty) {
        return this
      }
      return Method(isInObject, mode, typeParams, owner, name, paramNames, tpe.subst(m))
    }

    @pure override def collectTypeVars: ISZ[String] = {
      return tpe.collectTypeVars
    }

    @pure override def hasTypeVars: B = {
      return tpe.hasTypeVars
    }
  }

  @datatype class Methods(methods: ISZ[Method]) extends Typed {

    @pure override def isPureFun: B = {
      return F
    }

    @pure override def string: String = {
      return st"{ ${(methods, ", ")} }".render
    }

    @pure override def subst(m: HashMap[String, Typed]): Typed.Methods = {
      return this (methods.map(mt => mt.subst(m)))
    }

    @pure override def collectTypeVars: ISZ[String] = {
      return for (m <- methods; tv <- m.collectTypeVars) yield tv
    }

    @pure override def hasTypeVars: B = {
      for (m <- methods if m.hasTypeVars) {
        return T
      }
      return F
    }
  }

  // @formatter:off
  val sireumName: ISZ[String] = ISZ("org", "sireum")
  val nothing: Typed.Name = Typed.Name(sireumName :+ "Nothing", ISZ())
  val nothingOpt: Option[Typed] = Some(nothing)
  val unit: Typed.Name = Typed.Name(sireumName :+ "Unit", ISZ())
  val unitOpt: Option[Typed] = Some(unit)
  val bName: ISZ[String] = sireumName :+ "B"
  val b: Typed.Name = Typed.Name(bName, ISZ())
  val bOpt: Option[Typed] = Some(b)
  val cName: ISZ[String] = sireumName :+ "C"
  val c: Typed.Name = Typed.Name(cName, ISZ())
  val cOpt: Option[Typed] = Some(c)
  val zName: ISZ[String] = sireumName :+ "Z"
  val z: Typed.Name = Typed.Name(zName, ISZ())
  val zOpt: Option[Typed] = Some(z)
  val z8: Typed.Name = Typed.Name(sireumName :+ "Z8", ISZ())
  val z16: Typed.Name = Typed.Name(sireumName :+ "Z16", ISZ())
  val z32: Typed.Name = Typed.Name(sireumName :+ "Z32", ISZ())
  val z64: Typed.Name = Typed.Name(sireumName :+ "Z64", ISZ())
  val n: Typed.Name = Typed.Name(sireumName :+ "N", ISZ())
  val n8: Typed.Name = Typed.Name(sireumName :+ "N8", ISZ())
  val n16: Typed.Name = Typed.Name(sireumName :+ "N16", ISZ())
  val n32: Typed.Name = Typed.Name(sireumName :+ "N32", ISZ())
  val n64: Typed.Name = Typed.Name(sireumName :+ "N64", ISZ())
  val s8: Typed.Name = Typed.Name(sireumName :+ "S8", ISZ())
  val s16: Typed.Name = Typed.Name(sireumName :+ "S16", ISZ())
  val s32: Typed.Name = Typed.Name(sireumName :+ "S32", ISZ())
  val s64: Typed.Name = Typed.Name(sireumName :+ "S64", ISZ())
  val u8: Typed.Name = Typed.Name(sireumName :+ "U8", ISZ())
  val u16: Typed.Name = Typed.Name(sireumName :+ "U16", ISZ())
  val u32: Typed.Name = Typed.Name(sireumName :+ "U32", ISZ())
  val u64: Typed.Name = Typed.Name(sireumName :+ "U64", ISZ())
  val f32Name: ISZ[String] = sireumName :+ "F32"
  val f32: Typed.Name = Typed.Name(f32Name, ISZ())
  val f32Opt: Option[Typed] = Some(f32)
  val f64Name: ISZ[String] = sireumName :+ "F64"
  val f64: Typed.Name = Typed.Name(f64Name, ISZ())
  val f64Opt: Option[Typed] = Some(f64)
  val rName: ISZ[String] = sireumName :+ "R"
  val r: Typed.Name = Typed.Name(rName, ISZ())
  val rOpt: Option[Typed] = Some(r)
  val stringName: ISZ[String] = sireumName :+ "String"
  val string: Typed.Name = Typed.Name(stringName, ISZ())
  val stringOpt: Option[Typed] = Some(string)
  val st: Typed.Name = Typed.Name(sireumName :+ "ST", ISZ())
  val stOpt: Option[Typed] = Some(st)

  val optionName: ISZ[String] = sireumName :+ "Option"
  val someName: ISZ[String] = sireumName :+ "Some"
  val noneName: ISZ[String] = sireumName :+ "None"
  val isName: ISZ[String] = sireumName :+ "IS"
  val msName: ISZ[String] = sireumName :+ "MS"
  val iszName: ISZ[String] = sireumName :+ "ISZ"
  val mszName: ISZ[String] = sireumName :+ "MSZ"
  val zsName: ISZ[String] = sireumName :+ "ZS"
  val jenName: ISZ[String] = sireumName :+ "Jen"
  val mjenName: ISZ[String] = sireumName :+ "MJen"
  val unapplySeqResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.BuiltIn(ResolvedInfo.BuiltIn.Kind.UnapplySeq))
  val unapplyTupleResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.BuiltIn(ResolvedInfo.BuiltIn.Kind.UnapplyTuple))

  val bConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.b)))
  val bConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "B", ISZ(), Typed.bConstructorType))
  val bConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "B", ISZ(), Some(Typed.bConstructorType)))
  val cConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.c)))
  val cConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "C", ISZ(), Typed.cConstructorType))
  val cConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "C", ISZ(), Some(Typed.cConstructorType)))
  val zConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.z)))
  val zConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "Z", ISZ(), Typed.zConstructorType))
  val zConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "Z", ISZ(), Some(Typed.zConstructorType)))
  val z8ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.z8)))
  val z8ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "Z8", ISZ(), Typed.z8ConstructorType))
  val z8ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "Z8", ISZ(), Some(Typed.z8ConstructorType)))
  val z16ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.z16)))
  val z16ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "Z16", ISZ(), Typed.z16ConstructorType))
  val z16ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "Z16", ISZ(), Some(Typed.z16ConstructorType)))
  val z32ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.z32)))
  val z32ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "Z32", ISZ(), Typed.z32ConstructorType))
  val z32ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "Z32", ISZ(), Some(Typed.z32ConstructorType)))
  val z64ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.z64)))
  val z64ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "Z64", ISZ(), Typed.z64ConstructorType))
  val z64ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "Z64", ISZ(), Some(Typed.z64ConstructorType)))
  val nConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.n)))
  val nConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "N", ISZ(), Typed.nConstructorType))
  val nConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "N", ISZ(), Some(Typed.nConstructorType)))
  val n8ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.n8)))
  val n8ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "N8", ISZ(), Typed.n8ConstructorType))
  val n8ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "N8", ISZ(), Some(Typed.n8ConstructorType)))
  val n16ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.n16)))
  val n16ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "N16", ISZ(), Typed.n16ConstructorType))
  val n16ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "N16", ISZ(), Some(Typed.n16ConstructorType)))
  val n32ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.n32)))
  val n32ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "N32", ISZ(), Typed.n32ConstructorType))
  val n32ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "N32", ISZ(), Some(Typed.n32ConstructorType)))
  val n64ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.n64)))
  val n64ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "N64", ISZ(), Typed.n64ConstructorType))
  val n64ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "N64", ISZ(), Some(Typed.n64ConstructorType)))
  val s8ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.s8)))
  val s8ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "S8", ISZ(), Typed.s8ConstructorType))
  val s8ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "S8", ISZ(), Some(Typed.s8ConstructorType)))
  val s16ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.s16)))
  val s16ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "S16", ISZ(), Typed.s16ConstructorType))
  val s16ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "S16", ISZ(), Some(Typed.s16ConstructorType)))
  val s32ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.s32)))
  val s32ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "S32", ISZ(), Typed.s32ConstructorType))
  val s32ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "S32", ISZ(), Some(Typed.s32ConstructorType)))
  val s64ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.s64)))
  val s64ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "S64", ISZ(), Typed.s64ConstructorType))
  val s64ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "S64", ISZ(), Some(Typed.s64ConstructorType)))
  val u8ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.u8)))
  val u8ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "U8", ISZ(), Typed.u8ConstructorType))
  val u8ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "U8", ISZ(), Some(Typed.u8ConstructorType)))
  val u16ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.u16)))
  val u16ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "U16", ISZ(), Typed.u16ConstructorType))
  val u16ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "U16", ISZ(), Some(Typed.u16ConstructorType)))
  val u32ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.u32)))
  val u32ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "U32", ISZ(), Typed.u32ConstructorType))
  val u32ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "U32", ISZ(), Some(Typed.u32ConstructorType)))
  val u64ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.u64)))
  val u64ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "U64", ISZ(), Typed.u64ConstructorType))
  val u64ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "U64", ISZ(), Some(Typed.u64ConstructorType)))
  val f32ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.f32)))
  val f32ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "F32", ISZ(), Typed.f32ConstructorType))
  val f32ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "F32", ISZ(), Some(Typed.f32ConstructorType)))
  val f64ConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.f64)))
  val f64ConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "F64", ISZ(), Typed.f64ConstructorType))
  val f64ConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "F64", ISZ(), Some(Typed.f64ConstructorType)))
  val rConstructorType: Typed.Fun = Typed.Fun(T, F, ISZ(Typed.string), Typed.Name(optionName, ISZ(Typed.r)))
  val rConstructorMethodOpt: Option[Typed] = Some(Typed.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "R", ISZ(), Typed.rConstructorType))
  val rConstructorResOpt: Option[ResolvedInfo] = Some(ResolvedInfo.Method(T, MethodMode.Constructor, ISZ(),
    sireumName, "R", ISZ(), Some(Typed.rConstructorType)))

  val basicConstructorMap: HashMap[ISZ[String], (Option[Typed], Option[ResolvedInfo])] =
    HashMap.emptyInit[ISZ[String], (Option[Typed], Option[ResolvedInfo])](30) ++ ISZ(
      b.ids ~> ((bConstructorMethodOpt, bConstructorResOpt)),
      c.ids ~> ((cConstructorMethodOpt, cConstructorResOpt)),
      z.ids ~> ((zConstructorMethodOpt, zConstructorResOpt)),
      z8.ids ~> ((z8ConstructorMethodOpt, z8ConstructorResOpt)),
      z16.ids ~> ((z16ConstructorMethodOpt, z16ConstructorResOpt)),
      z32.ids ~> ((z32ConstructorMethodOpt, z32ConstructorResOpt)),
      z64.ids ~> ((z64ConstructorMethodOpt, z64ConstructorResOpt)),
      n.ids ~> ((nConstructorMethodOpt, nConstructorResOpt)),
      n8.ids ~> ((n8ConstructorMethodOpt, n8ConstructorResOpt)),
      n16.ids ~> ((n16ConstructorMethodOpt, n16ConstructorResOpt)),
      n32.ids ~> ((n32ConstructorMethodOpt, n32ConstructorResOpt)),
      n64.ids ~> ((n64ConstructorMethodOpt, n64ConstructorResOpt)),
      s8.ids ~> ((s8ConstructorMethodOpt, s8ConstructorResOpt)),
      s16.ids ~> ((s16ConstructorMethodOpt, s16ConstructorResOpt)),
      s32.ids ~> ((s32ConstructorMethodOpt, s32ConstructorResOpt)),
      s64.ids ~> ((s64ConstructorMethodOpt, s64ConstructorResOpt)),
      u8.ids ~> ((u8ConstructorMethodOpt, u8ConstructorResOpt)),
      u16.ids ~> ((u16ConstructorMethodOpt, u16ConstructorResOpt)),
      u32.ids ~> ((u32ConstructorMethodOpt, u32ConstructorResOpt)),
      u64.ids ~> ((u64ConstructorMethodOpt, u64ConstructorResOpt)),
      f32.ids ~> ((f32ConstructorMethodOpt, f32ConstructorResOpt)),
      f64.ids ~> ((f64ConstructorMethodOpt, f64ConstructorResOpt)),
      r.ids ~> ((rConstructorMethodOpt, rConstructorResOpt))
    )
  // @formatter:on

  @pure def short(ids: ISZ[String]): ISZ[String] = {
    return if (ids.size >= 2 && ids(0) == string"org" && ids(1) == string"sireum") ops.ISZOps(ids).drop(2)
    else ids
  }

  @pure def substOpt(tOpt: Option[Typed], substMap: HashMap[String, Typed]): Option[Typed] = {
    tOpt match {
      case Some(t) if substMap.nonEmpty =>
        val newT = t.subst(substMap)
        return if (newT == t) tOpt else Some(newT)
      case _ => return tOpt
    }
  }
}

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
      'BinaryAppend
      'BinaryPrepend
      'BinaryAppendAll
      'BinaryRemoveAll
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
      'Update
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
                         tpeOpt: Option[Typed.Fun]) extends ResolvedInfo {

    @pure override def subst(substMap: HashMap[String, Typed]): ResolvedInfo = {
      tpeOpt match {
        case Some(tpe) => return Method(isInObject, mode, typeParams, owner, id, paramNames, Some(tpe.subst(substMap)))
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

  @datatype class LocalVar(context: ISZ[String], scope: ResolvedInfo.LocalVar.Scope.Type, isVal: B, id: String)
    extends ResolvedInfo

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
