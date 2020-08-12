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
                      val modifies: ISZ[Exp.Ident],
                      body: Body,
                      @hidden attr: Attr) extends Stmt with Loop {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @strictpure override def invariants: ISZ[Exp] =
      for (enumGen <- enumGens; inv <- enumGen.invariants) yield inv

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

  @datatype class Simple(val reads: ISZ[Exp.Ident],
                         requires: ISZ[Exp],
                         val modifies: ISZ[Exp.Ident],
                         ensures: ISZ[Exp]) extends MethodContract {
    @pure override def isEmpty: B = {
      return reads.isEmpty && requires.isEmpty && modifies.isEmpty && ensures.isEmpty
    }
  }

  @datatype class Cases(val reads: ISZ[Exp.Ident],
                        val modifies: ISZ[Exp.Ident],
                        cases: ISZ[Case]) extends MethodContract {
    @pure override def isEmpty: B = {
      return reads.isEmpty && modifies.isEmpty && cases.isEmpty
    }
  }

  @datatype class Case(label: Exp.LitString,
                       requires: ISZ[Exp],
                       ensures: ISZ[Exp])

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

  @datatype class For(idOpt: Option[Id], range: Range, condOpt: Option[Exp], invariants: ISZ[Exp])

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

  @datatype trait Spec extends Exp

  @datatype trait Quant extends Spec {
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

  @datatype class Input(exp: Exp, @hidden attr: Attr) extends Spec {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return exp.typedOpt
    }
  }

  @datatype class OldVal(exp: Exp, @hidden attr: Attr) extends Spec {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return exp.typedOpt
    }
  }

  @datatype class AtLoc(line: Z, idOpt: Option[Id], exp: Exp, @hidden attr: Attr) extends Spec {

    @pure override def posOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def typedOpt: Option[Typed] = {
      return exp.typedOpt
    }
  }

  @datatype class LoopIndex(tipeOpt: Option[Type], exp: Exp.Ident, @hidden attr: TypedAttr) extends Spec {

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

  @datatype class StateSeq(id: Id, fragments: ISZ[StateSeq.Fragment], @hidden attr: Attr) extends Spec {

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

  @datatype class Result(tipeOpt: Option[Type], @hidden attr: TypedAttr) extends Spec {

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

  @datatype class LocalVar(context: ISZ[String], scope: ResolvedInfo.LocalVar.Scope.Type, isSpec: B, isVal: B, id: String)
    extends ResolvedInfo

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
