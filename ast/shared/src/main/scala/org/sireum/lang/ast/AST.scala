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

  @datatype class Program(val fileUriOpt: Option[String], val packageName: Name, val body: Body) extends TopUnit {
    @pure override def string: String = {
      return if (packageName.ids.isEmpty)
        st"""// #Sireum
            |
            |${(body.prettySTs, "\n")}""".render
      else
        st"""// #Sireum
            |package ${packageName.prettyST}
            |
            |${(body.prettySTs, "\n")}""".render
    }

  }

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
  @strictpure def posOpt: Option[Position]
  @pure def prettyST: ST
  @strictpure def leaves: ISZ[Option[Stmt]] = ISZ(Some(this))
  @pure override def string: String = {
    return prettyST.render
  }
  @strictpure def asAssignExp: AssignExp = halt(s"Invalid operation 'asAssignExp' on $this.")
  @strictpure def isInstruction: B
  @memoize def hasReturnMemoized: B = {
    return hasReturn
  }
  @strictpure def hasReturn: B
  @pure def compNum: Z
}

@sig trait HasModifies {

  @strictpure def modifies: ISZ[Exp.Ref]

  @pure def modifiedLocalVars(receiverLocalTypeOpt: Option[(ResolvedInfo.LocalVar, Typed)],
                              typeSubstMap: HashMap[String, Typed]): (B, HashSMap[ResolvedInfo.LocalVar, (Typed, Position)]) = {
    var r = HashSMap.empty[ResolvedInfo.LocalVar, (Typed, Position)]
    var modThisPosOpt: Option[Position] = None()
    for (ref <- modifies) {
      ref.resOpt match {
        case Some(res: ResolvedInfo.LocalVar) => r = r + res ~> ((ref.typedOpt.get.subst(typeSubstMap), ref.posOpt.get))
        case Some(res: ResolvedInfo.Var) if !res.isInObject => modThisPosOpt = ref.posOpt
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
    @pure def filterObjectVar(exp: Exp.Ref): ISZ[(ResolvedInfo.Var, (Typed, Position))] = {
      exp.resOpt match {
        case Some(res: ResolvedInfo.Var) if res.isInObject => return ISZ((res, (exp.typedOpt.get, exp.posOpt.get)))
        case _ => return ISZ()
      }
    }

    return HashSMap.empty[ResolvedInfo.Var, (Typed, Position)] ++
      (for (mod <- modifies; res <- filterObjectVar(mod)) yield res)
  }


  @pure def modifiedInstanceVars: HashSMap[ResolvedInfo.Var, (Typed, Position)] = {
    @pure def filterInstanceVar(ref: Exp.Ref): ISZ[(ResolvedInfo.Var, (Typed, Position))] = {
      ref.resOpt match {
        case Some(res: ResolvedInfo.Var) if !res.isInObject => return ISZ((res, (ref.typedOpt.get, ref.posOpt.get)))
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
                             val modifies: ISZ[Exp.Ref],
                             val maxItOpt: Option[Exp.LitZ]) extends HasModifies {
  @strictpure def isEmpty: B = invariants.isEmpty && modifies.isEmpty && maxItOpt.isEmpty
  @pure def prettySTOpt: Option[ST] = {
    if (isEmpty) {
      return None()
    } else {
      val invs: ISZ[ST] =
        (if (modifies.isEmpty) ISZ[ST]() else ISZ(st"Modifies(${(for (m <- modifies) yield m.asExp.prettyST, ", ")})")) ++
          (for (e <- invariants) yield e.prettyST) ++
          (if (maxItOpt.nonEmpty) ISZ(st"MaxIt(${maxItOpt.get.prettyST})") else ISZ[ST]())
      return Some(
        st"""Invariant(
            |  ${(invs, ",\n")})"""
      )
    }
  }
}

object Stmt {

  @datatype class Import(val importers: ISZ[Import.Importer], @hidden val attr: Attr) extends Stmt {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def prettyST: ST = st"import ${(for (i <- importers) yield i.prettyST, ", ")}"
    @strictpure override def isInstruction: B = F
    @strictpure override def hasReturn: B = F
    @strictpure def compNum: Z = 0
  }

  object Import {

    @datatype class Importer(val name: Name, val selectorOpt: Option[Selector]) {
      @strictpure def prettyST: ST = selectorOpt match {
        case Some(selector) => st"${name.prettyST}.${selector.prettyST}"
        case _ => name.prettyST
      }
    }

    @datatype trait Selector {
      @strictpure def prettyST: ST
    }

    @datatype class MultiSelector(val selectors: ISZ[NamedSelector]) extends Selector {
      @strictpure override def prettyST: ST = st"{${(for (s <- selectors) yield st"${s.from.prettyST} => ${s.to.prettyST}", ", ")}}"
    }

    @datatype class WildcardSelector extends Selector {
      @strictpure override def prettyST: ST = st"_"
    }

    @datatype class NamedSelector(val from: Id, val to: Id)

  }

  @datatype class Var(val isSpec: B,
                      val isVal: B,
                      val id: Id,
                      val tipeOpt: Option[Type],
                      val initOpt: Option[AssignExp],
                      @hidden val attr: ResolvedAttr) extends Stmt {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @pure override def prettyST: ST = {
      val specOpt: Option[String] = if (isSpec) Some("@spec ") else None()
      val valVar: String = if (isVal) "val" else "var"
      val tOpt: Option[ST] = if (tipeOpt.isEmpty) None() else Some(st": ${tipeOpt.get.prettyST}")
      val iOpt: Option[ST] = if (initOpt.isEmpty) None() else Some(st" = ${initOpt.get.prettyST}")
      return st"$specOpt$valVar ${id.prettyST}$tOpt$iOpt"
    }
    @strictpure override def isInstruction: B = initOpt.nonEmpty
    @strictpure override def hasReturn: B = if (initOpt.nonEmpty) initOpt.get.hasReturn else F
    @strictpure def compNum: Z = initOpt match {
      case Some(init) => 1 + init.asStmt.compNum
      case _ => 1
    }
  }

  @datatype class VarPattern(val isSpec: B,
                             val isVal: B,
                             val pattern: Pattern,
                             val tipeOpt: Option[Type],
                             val init: AssignExp,
                             @hidden val attr: Attr) extends Stmt {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @pure override def prettyST: ST = {
      val spec: String = if (isSpec) "@spec " else ""
      val valVar: String = if (isVal) "val" else "var"
      val tOpt: Option[ST] = if (tipeOpt.isEmpty) None() else Some(st": ${tipeOpt.get.prettyST}")
      return st"$spec$valVar ${pattern.prettyST}$tOpt = ${init.prettyST}}"
    }
    @strictpure override def isInstruction: B = T
    @strictpure override def hasReturn: B = init.hasReturn
    @strictpure def compNum: Z = 1 + init.asStmt.compNum
  }

  @datatype class SpecVar(val isVal: B,
                          val id: Id,
                          val tipe: Type,
                          @hidden val attr: ResolvedAttr) extends Spec {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def prettyST: ST = {
      val valVar: String = if (isVal) "val" else "var"
      st"@spec $valVar ${id.prettyST}: ${tipe.prettyST}}"
    }
    @strictpure override def isInstruction: B = F
    @strictpure override def hasReturn: B = F
    @strictpure def compNum: Z = 0
  }

  @datatype class RsVal(val id: Id,
                        val init: Exp,
                        @hidden val attr: ResolvedAttr) extends Spec {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @pure override def prettyST: ST = {
      return st"@rw val ${id.prettyST}: RS = ${init.prettyST}"
    }
    @strictpure override def isInstruction: B = F
    @strictpure override def hasReturn: B = F
    @strictpure def compNum: Z = 0
  }

  @datatype class Method(val typeChecked: B,
                         val purity: Purity.Type,
                         val modifiers: ISZ[String],
                         val sig: MethodSig,
                         val mcontract: MethodContract,
                         val bodyOpt: Option[Body],
                         @hidden val attr: ResolvedAttr) extends Stmt {

    @strictpure def isStrictPure: B = purity == Purity.StrictPure || purity == Purity.Abs
    @memoize def hasOverride: B = { return ops.ISZOps(modifiers).contains("override") }
    @memoize def isHelper: B = {
      for (m <- modifiers) {
        m.native match {
          case "@helper" => return T
          case "@inline" => return T
          case _ =>
        }
      }
      return F
    }
    @memoize def hasInline: B = { return ops.ISZOps(modifiers).contains("@inline") }

    @strictpure override def posOpt: Option[Position] = attr.posOpt
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
              MethodContract.InfoFlows.empty,
              Attr(sequent.attr.posOpt))
          case _ =>
        }
      }
      return mcontract
    }

    @pure override def prettyST: ST = {
      val helper: String = if (isHelper) "@helper " else ""
      val pure: String = purity match {
        case Purity.Impure => ""
        case Purity.Pure => "@pure "
        case Purity.StrictPure => "@strictpure "
        case Purity.Abs => "@abs "
        case Purity.Memoize => "@memoize "
      }
      val overr: String = if (hasOverride) "override " else ""
      val bOpt: Option[ST] =
        if (bodyOpt.isEmpty)
          if (mcontract.isEmpty) None()
          else Some(
            st""" = {
                |  ${mcontract.prettySTOpt(bodyOpt.isEmpty)}
                |}""")
        else Some(
          st""" = {
              |  ${mcontract.prettySTOpt(bodyOpt.isEmpty)}
              |  ${(bodyOpt.get.prettySTs, "\n")}
              |}""")
      return st"${(modifiers, " ")}${if (modifiers.nonEmpty) " "  else ""}def ${sig.prettyST}$bOpt"
    }
    @strictpure override def isInstruction: B = F
    @strictpure override def hasReturn: B = T
    @pure def compNum: Z = {
      bodyOpt match {
        case Some(body) => return body.compNum
        case _ => return 0
      }
    }
  }

  @datatype class ExtMethod(val isPure: B,
                            val sig: MethodSig,
                            val contract: MethodContract,
                            @hidden val attr: ResolvedAttr) extends Stmt {

    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def prettyST: ST = {
      val helper: String = if (isPure) "@pure " else ""
      val bOpt: Option[ST] = if (contract.isEmpty) None() else Some(st" = ${contract.prettySTOpt(T)}")
      st"${helper}def ${sig.prettyST}$bOpt"
    }
    @strictpure override def isInstruction: B = F
    @strictpure override def hasReturn: B = F
    @strictpure def compNum: Z = 0
  }

  @datatype class JustMethod(val etaOpt: Option[Exp.LitString],
                             val sig: MethodSig,
                             @hidden val attr: ResolvedAttr) extends Spec {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @pure override def prettyST: ST = {
      val just: ST = if (etaOpt.isEmpty) st"@just " else st"@just(${etaOpt.get.prettyST}}) "
      return st"${just}def ${sig.prettyST}"
    }
    @strictpure override def isInstruction: B = F
    @strictpure override def hasReturn: B = F
    @strictpure def compNum: Z = 0
  }

  @datatype class SpecMethod(val sig: MethodSig, @hidden val attr: ResolvedAttr) extends Spec {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def prettyST: ST = st"@spec def ${sig.prettyST}"
    @strictpure override def isInstruction: B = F
    @strictpure override def hasReturn: B = F
    @strictpure def compNum: Z = 0
  }

  @datatype class Enum(val id: Id, val elements: ISZ[Id], @hidden val attr: Attr) extends Stmt {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def prettyST: ST =
      st"""@enum object ${id.prettyST} {
          |  ${(for (e <- elements) yield st""""${id.prettyST}"""", "\n")}
          |}"""
    @strictpure override def isInstruction: B = F
    @strictpure override def hasReturn: B = F
    @strictpure def compNum: Z = 0
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
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure def isZeroIndex: B = index == 0
    @strictpure override def prettyST: ST = {
      val args = ISZ[ST]() ++
        (if (isBitVector) ISZ(st"signed = ${if (isSigned) "T" else "F"}", st"width = $bitWidth") else ISZ[ST]()) ++
        (if (hasMin) ISZ(st"""min = z"$min"""") else ISZ[ST]()) ++
        (if (hasMax) ISZ(st"""max = z"$max"""") else ISZ[ST]()) ++
        (if (isIndex) ISZ(st"index = $isIndex") else ISZ[ST]())
      val rb: ST = if (isBitVector) st"@bits(${(args, ", ")})" else st"@range(${(args, ", ")})"
      st"$rb class ${id.prettyST}"
    }
    @strictpure override def isInstruction: B = F
    @strictpure override def hasReturn: B = F
    @strictpure def capacityOpt: Option[Z] = if (hasMax) {
      if (hasMin && isIndex) {
        Some(max - min + 1)
      } else {
        Some(max + 1)
      }
    } else {
      None()
    }
    @strictpure def compNum: Z = 0
  }

  @datatype class Object(val isApp: B,
                         val extNameOpt: Option[String],
                         val id: Id,
                         val stmts: ISZ[Stmt],
                         @hidden val attr: Attr) extends Stmt {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @pure override def prettyST: ST = {
      val app: String = if (isApp) " extends App" else ""
      val ext: ST = if (extNameOpt.isEmpty) st"" else if (extNameOpt.get == "") st"@ext " else st"""@ext("${extNameOpt.get}")"""
      return st"""${ext}object ${id.prettyST}$app {
                 |  ${(for (stmt <- stmts) yield stmt.prettyST, "\n")}
                 |}"""
    }
    @strictpure override def isInstruction: B = F
    @strictpure override def hasReturn: B = F
    @strictpure def compNum: Z = 0
  }

  @datatype class Sig(val isImmutable: B,
                      val isSealed: B,
                      val isExt: B,
                      val id: Id,
                      val typeParams: ISZ[TypeParam],
                      val parents: ISZ[Type.Named],
                      val stmts: ISZ[Stmt],
                      @hidden val attr: Attr) extends Stmt {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @pure override def prettyST: ST = {
      val ext: String = if (isExt) "@ext " else ""
      val sig: String = if (isImmutable) "@sig " else "@msig "
      val seal: String = if (isSealed) " sealed" else ""
      val extend: Option[ST] =
        if (parents.isEmpty) None() else Some(st" extends ${(for (n <- parents) yield n.prettyST, ", ")}")
      return st"""$ext$sig$seal trait ${id.prettyST}${TypeParam.stOpt(typeParams)}$extend {
                 |  ${(for (stmt <- stmts) yield stmt.prettyST, "\n")}
                 |}"""
    }
    @strictpure override def isInstruction: B = F
    @strictpure override def hasReturn: B = F
    @strictpure def compNum: Z = 0
  }

  @datatype class Adt(val isRoot: B,
                      val isDatatype: B,
                      val isUnclonable: B,
                      val id: Id,
                      val typeParams: ISZ[TypeParam],
                      val params: ISZ[AdtParam],
                      val parents: ISZ[Type.Named],
                      val stmts: ISZ[Stmt],
                      @hidden val attr: Attr) extends Stmt {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @pure override def prettyST: ST = {
      val rd: ST = if (isDatatype) st"@datatype " else st"@record ${if (isUnclonable) "@unclonable " else ""}"
      val root: String = if (isRoot) "trait" else "class"
      val param: Option[ST] = if (params.isEmpty) None() else Some(st"(${(for (p <- params) yield p.prettyST, ", ")})")
      val extend: Option[ST] =
        if (parents.isEmpty) None() else Some(st" extends ${(for (n <- parents) yield n.prettyST, ", ")}")
      return st"""$rd $root ${id.prettyST}${TypeParam.stOpt(typeParams)}$param$extend {
                 |  ${(for (stmt <- stmts) yield stmt.prettyST, "\n")}
                 |}"""
    }
    @strictpure override def isInstruction: B = F
    @strictpure override def hasReturn: B = F
    @strictpure def compNum: Z = 0
  }

  @datatype class TypeAlias(val id: Id, val typeParams: ISZ[TypeParam], val tipe: Type, @hidden val attr: Attr) extends Stmt {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def prettyST: ST = st"type ${id.prettyST}${TypeParam.stOpt(typeParams)} = ${tipe.prettyST}"
    @strictpure override def isInstruction: B = F
    @strictpure override def hasReturn: B = F
    @strictpure def compNum: Z = 0
  }

  @datatype class Assign(val lhs: Exp, val rhs: AssignExp, @hidden val attr: Attr) extends Stmt {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @pure override def prettyST: ST = {
      return st"${lhs.prettyST} = ${rhs.prettyST}"
    }
    @strictpure override def isInstruction: B = T
    @strictpure override def hasReturn: B = rhs.hasReturn
    @strictpure def compNum: Z = 1 + rhs.asStmt.compNum
  }

  @datatype class Block(val contract: MethodContract, val body: Body, @hidden val attr: Attr) extends Stmt with AssignExp {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def asAssignExp: AssignExp = this
    @strictpure override def asStmt: Stmt = this
    @strictpure override def leaves: ISZ[Option[Stmt]] = body.leaves
    @pure override def prettyST: ST = {
      return st"""{
                 |  ${(body.prettySTs, "\n")}
                 |}"""
    }
    @strictpure override def isInstruction: B = T
    @strictpure override def hasReturn: B = body.hasReturn
    @strictpure override def typedOpt: Option[Typed] = body.stmts(body.stmts.size - 1) match {
      case ae: AssignExp => ae.typedOpt
      case _ => Typed.unitOpt
    }
    @pure def compNum: Z = {
      return body.compNum
    }
  }

  @datatype class If(val cond: Exp, val thenBody: Body, val elseBody: Body, @hidden val attr: TypedAttr) extends Stmt with AssignExp {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def asAssignExp: AssignExp = this
    @strictpure override def asStmt: Stmt = this
    @strictpure override def leaves: ISZ[Option[Stmt]] = thenBody.leaves ++ elseBody.leaves
    @pure override def prettyST: ST = {
      return if (elseBody.stmts.isEmpty)
        st"""if (${cond.prettyST}) {
            |  ${(thenBody.prettySTs, "\n")}
            |}"""
      else if (elseBody.stmts.size == 1 && elseBody.stmts(0).isInstanceOf[Stmt.If])
        st"""if (${cond.prettyST}) {
            |  ${(thenBody.prettySTs, "\n")}
            |} else ${elseBody.stmts(0).prettyST}"""
      else
        st"""if (${cond.prettyST}) {
            |  ${(thenBody.prettySTs, "\n")}
            |} else {
            |  ${(elseBody.prettySTs, "\n")}
            |}"""
    }
    @strictpure override def isInstruction: B = T
    @strictpure override def hasReturn: B = thenBody.hasReturn || elseBody.hasReturn
    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt
    @pure def compNum: Z = {
      return 1 + thenBody.compNum + elseBody.compNum
    }
  }

  @datatype class Induct(val exp: Exp, val context: ISZ[String], val locals: ISZ[String], @hidden val attr: Attr) extends Spec {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def hasReturn: B = F
    @strictpure override def isInstruction: B = T
    @pure override def prettyST: ST = {
      return if (Exp.shouldParenthesize(exp)) st"((${exp.prettyST}): @induct)" else st"(${exp.prettyST}: @induct)"
    }
    @strictpure def compNum: Z = 1
  }

  @datatype class Match(val isInduct: B, val exp: Exp, val cases: ISZ[Case], @hidden val attr: TypedAttr) extends Stmt with AssignExp {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def asAssignExp: AssignExp = this
    @strictpure override def asStmt: Stmt = this
    @strictpure override def leaves: ISZ[Option[Stmt]] = for (c <- cases; leaf <- c.body.leaves) yield leaf
    @pure override def prettyST: ST = {
      val expST: ST = if (isInduct && Exp.shouldParenthesize(exp)) st"((${exp.prettyST}): @induct)" else st"(${exp.prettyST}: @induct)"
      if (cases.isEmpty) {
        return st"$expST"
      } else {
        return st"""$expST match {
                   |  ${(for (cas <- cases) yield cas.prettyST, "\n")}
                   |}"""
      }
    }
    @strictpure override def isInstruction: B = T
    @strictpure override def hasReturn: B = ops.ISZOps(cases).exists((c: Case) => c.body.hasReturn)
    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt
    @pure def compNum: Z = {
      var r: Z = 1
      for (cas <- cases) {
        r = r + cas.compNum
      }
      return r
    }
  }

  @datatype class While(val context: ISZ[String],
                        val cond: Exp,
                        val contract: LoopContract,
                        val body: Body,
                        @hidden val attr: Attr) extends Stmt {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure def invariants: ISZ[Exp] = contract.invariants
    @strictpure def modifies: ISZ[Exp.Ref] = contract.modifies
    @pure override def prettyST: ST = {
      return st"""while (${cond.prettyST}) {
                 |  ${contract.prettySTOpt}
                 |  ${(body.prettySTs, "\n")}
                 |}"""
    }
    @strictpure override def isInstruction: B = T
    @strictpure override def hasReturn: B = body.hasReturn
    @pure def compNum: Z = {
      return 1 + body.compNum + contract.invariants.size + contract.modifies.size
    }
  }

  @datatype class For(val context: ISZ[String],
                      val enumGens: ISZ[EnumGen.For],
                      val body: Body,
                      @hidden val attr: Attr) extends Stmt {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure def modifies: ISZ[Exp.Ref] = enumGens(0).contract.modifies
    @pure def prettyST: ST = {
      val invs: ISZ[ST] = for (eg <- enumGens;
                               pst <- if (eg.contract.isEmpty) ISZ[ST]() else ISZ(eg.contract.prettySTOpt.get)) yield pst
      return if (enumGens.size == 1)
        st"""for (${enumGens(0).prettyST}) {
            |  ${(invs ++ body.prettySTs, "\n")}
            |}"""
      else
        st"""for (
            |  ${(for (eg <- enumGens) yield eg.prettyST, "\n")}
            |) {
            |  ${(invs ++ body.prettySTs, "\n")}
            |}"""
    }
    @strictpure override def isInstruction: B = T
    @strictpure override def hasReturn: B = body.hasReturn
    @pure def compNum: Z = {
      var r = 1 + body.compNum
      for (enumGen <- enumGens) {
        r = r + 1 + enumGen.contract.invariants.size + enumGen.contract.modifies.size
      }
      return r
    }
  }

  @datatype class Return(val expOpt: Option[Exp], @hidden val attr: TypedAttr) extends Stmt with AssignExp {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def asStmt: Stmt = this
    @strictpure override def asAssignExp: AssignExp = this
    @pure override def prettyST: ST = {
      return if (expOpt.isEmpty) st"return" else st"return ${expOpt.get.prettyST}"
    }
    @strictpure override def isInstruction: B = T
    @strictpure override def hasReturn: B = T
    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt
    @strictpure def compNum: Z = 1
  }

  @datatype class Expr(val exp: Exp, @hidden val attr: TypedAttr) extends Stmt with AssignExp {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure def typedOpt: Option[Typed] = attr.typedOpt
    @strictpure override def asAssignExp: AssignExp = this
    @strictpure override def asStmt: Stmt = this
    @pure override def prettyST: ST = {
      return exp.prettyST
    }
    @strictpure override def isInstruction: B = T
    @strictpure override def hasReturn: B = F
    @strictpure def kind: Expr.Kind.Type = exp match {
      case exp: Exp.Invoke =>
        exp.attr.resOpt match {
          case Some(res: ResolvedInfo.BuiltIn) =>
            res.kind match {
              case ResolvedInfo.BuiltIn.Kind.Assert => Expr.Kind.Assert
              case ResolvedInfo.BuiltIn.Kind.AssertMsg => Expr.Kind.AssertMsg
              case ResolvedInfo.BuiltIn.Kind.Assume => Expr.Kind.Assume
              case ResolvedInfo.BuiltIn.Kind.AssumeMsg => Expr.Kind.AssumeMsg
              case ResolvedInfo.BuiltIn.Kind.Cprint => Expr.Kind.Cprint
              case ResolvedInfo.BuiltIn.Kind.Cprintln => Expr.Kind.Cprintln
              case ResolvedInfo.BuiltIn.Kind.Eprint => Expr.Kind.Eprint
              case ResolvedInfo.BuiltIn.Kind.Eprintln => Expr.Kind.Eprintln
              case ResolvedInfo.BuiltIn.Kind.Print => Expr.Kind.Print
              case ResolvedInfo.BuiltIn.Kind.Println => Expr.Kind.Println
              case ResolvedInfo.BuiltIn.Kind.SetOptions => Expr.Kind.SetOptions
              case _ => Expr.Kind.General
            }
          case _ => Expr.Kind.General
        }
      case _ => halt("This method can only be used on resolved/type checked AST")
    }
    @strictpure def compNum: Z = 1
  }

  object Expr {
    @enum object Kind {
      "General"
      "Assert"
      "AssertMsg"
      "Assume"
      "AssumeMsg"
      "Cprint"
      "Cprintln"
      "Eprint"
      "Eprintln"
      "Print"
      "Println"
      "SetOptions"
    }
  }

  @datatype trait Spec extends Stmt

  @datatype class Fact(val id: Id,
                       val typeParams: ISZ[TypeParam],
                       val descOpt: Option[Exp.LitString],
                       val claims: ISZ[Exp],
                       val isFun: B,
                       @hidden val attr: ResolvedAttr) extends Spec {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @pure override def prettyST: ST = {
      return st"""@spec def ${id.prettyST}${TypeParam.stOpt(typeParams)} = Fact(
                 |  ${(for (claim <- (if (descOpt.isEmpty) ISZ[Exp]() else ISZ[Exp](descOpt.get)) ++ claims) yield claim.prettyST, ",\n")}
                 |)"""
    }
    @strictpure override def isInstruction: B = F
    @strictpure override def hasReturn: B = F
    @strictpure def compNum: Z = 0
  }

  @datatype class Inv(val id: Id,
                      val claims: ISZ[Exp],
                      @hidden val attr: ResolvedAttr) extends Spec {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @pure override def prettyST: ST = {
      return st"""@spec def ${id.prettyST} = Invariant(
                 |  ${(for (claim <- claims) yield claim.prettyST, ",\n")}
                 |)"""
    }
    @strictpure override def isInstruction: B = F
    @strictpure override def hasReturn: B = F
    @strictpure def compNum: Z = 0
  }

  @datatype class Theorem(val isLemma: B,
                          val id: Id,
                          val typeParams: ISZ[TypeParam],
                          val descOpt: Option[Exp.LitString],
                          val claim: Exp,
                          val isFun: B,
                          val proof: ProofAst,
                          @hidden val attr: ResolvedAttr) extends Spec {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @pure override def prettyST: ST = {
      return st"""@spec def ${id.prettyST}${TypeParam.stOpt(typeParams)} = ${if (isLemma) "Lemma" else "Theorem"}(
                 |  ${(if (descOpt.isEmpty) ISZ[ST](claim.prettyST) else ISZ[ST](descOpt.get.prettyST, claim.prettyST, proof.prettyST), ",\n")}
                 |)"""
    }
    @strictpure override def isInstruction: B = F
    @strictpure override def hasReturn: B = F
    @strictpure def compNum: Z = 0
  }

  @datatype class DataRefinement(val rep: Exp.Ref,
                                 val refs: ISZ[Exp.Ref],
                                 val claims: ISZ[Exp],
                                 @hidden val attr: Attr) extends Spec {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @pure override def prettyST: ST = {
      return st"Contract(DataRefinement(${rep.asExp.prettyST})(${(for (ref <- refs) yield ref.asExp.prettyST, ", ")})(${(for (claim <- claims) yield claim.prettyST, ", ")}))"
    }
    @strictpure override def isInstruction: B = F
    @strictpure override def hasReturn: B = F
    @strictpure def compNum: Z = 0
  }

  @datatype class SpecLabel(val id: Id) extends Spec {
    @strictpure override def posOpt: Option[Position] = id.attr.posOpt
    @strictpure override def prettyST: ST = st"""Spec("${id.prettyST}")"""
    @strictpure override def isInstruction: B = T
    @strictpure override def hasReturn: B = F
    @strictpure def compNum: Z = 0
  }

  @datatype class SpecBlock(val block: Block) extends Spec {
    @strictpure override def posOpt: Option[Position] = block.posOpt
    @pure override def prettyST: ST = {
      return st"Spec ${block.prettyST}"
    }
    @strictpure override def isInstruction: B = T
    @strictpure override def hasReturn: B = F
    @pure def compNum: Z = {
      return block.compNum
    }
  }

  @datatype class DeduceSequent(val justOpt: Option[Exp.LitString], val sequents: ISZ[Sequent], @hidden val attr: Attr) extends Spec {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @pure override def prettyST: ST = {
      return st"""Deduce(
                 |  ${(if (justOpt.isEmpty) ISZ[ST]() else ISZ(justOpt.get.prettyST) ++ (for (s <- sequents) yield s.prettyST), ",\n")}
                 |)"""
    }
    @strictpure override def isInstruction: B = T
    @strictpure override def hasReturn: B = F
    @pure def compNum: Z = {
      var r: Z = 0
      for (sequent <- sequents) {
        r = r + sequent.compNum
      }
      return r
    }
  }

  @datatype class DeduceSteps(val steps: ISZ[ProofAst.Step], @hidden val attr: Attr) extends Spec {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @pure override def prettyST: ST = {
      return st"""Deduce(
                 |  ${(for (step <- steps) yield step.prettyST, ",\n")}
                 |)"""
    }
    @strictpure override def isInstruction: B = T
    @strictpure override def hasReturn: B = F
    @pure def compNum: Z = {
      var r: Z = 0
      for (step <- steps) {
        r = r + step.compNum
      }
      return r
    }
  }

  @datatype class Havoc(val args: ISZ[Exp.Ref], @hidden val attr: Attr) extends Spec {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @pure override def prettyST: ST = {
      return st"Havoc(${(for (arg <- args) yield arg.asExp.prettyST, ", ")})"
    }
    @strictpure override def isInstruction: B = T
    @strictpure override def hasReturn: B = F
    @strictpure def compNum: Z = 0
  }

}

@datatype trait MethodContract extends HasModifies {
  @strictpure def isEmpty: B
  @strictpure def nonEmpty: B = !isEmpty
  @strictpure def reads: ISZ[Exp.Ref]
  @strictpure def modifies: ISZ[Exp.Ref]
  @strictpure def prettySTOpt(isOnly: B): Option[ST]
}

object MethodContract {

  object Accesses {
    @strictpure def empty: Accesses = Accesses(ISZ(), Attr(None()))
  }

  @datatype class Accesses(val refs: ISZ[Exp.Ref], val attr: Attr) {
    @strictpure def isEmpty: B = refs.isEmpty
    @strictpure def nonEmpty: B = !refs.isEmpty
    @pure def prettySTs(kind: String): ISZ[ST] = {
      return if (isEmpty) ISZ[ST]() else ISZ(st"$kind(${(for (ref <- refs) yield ref.asExp.prettyST, ", ")})")
    }
  }

  object Claims {
    @strictpure def empty: Claims = Claims(ISZ(), Attr(None()))
  }

  @datatype class Claims(val claims: ISZ[Exp], val attr: Attr) {
    @strictpure def isEmpty: B = claims.isEmpty
    @strictpure def nonEmpty: B = !claims.isEmpty
    @pure def prettySTs(kind: String): ISZ[ST] = {
      return if (isEmpty) ISZ[ST]() else ISZ(
        st"""$kind(
            |  ${(for (claim <- claims) yield claim.prettyST, ", ")}
            |)"""
      )
    }
  }

  object Simple {
    @strictpure def empty: Simple = Simple(Accesses.empty, Claims.empty, Accesses.empty, Claims.empty, InfoFlows.empty, Attr(None()))
  }
  @datatype class Simple(val readsClause: Accesses,
                         val requiresClause: Claims,
                         val modifiesClause: Accesses,
                         val ensuresClause: Claims,
                         val infoFlowsClause: InfoFlows,
                         val attr: Attr) extends MethodContract {
    @strictpure override def reads: ISZ[Exp.Ref] = readsClause.refs
    @strictpure def requires: ISZ[Exp] = requiresClause.claims
    @strictpure override def modifies: ISZ[Exp.Ref] = modifiesClause.refs
    @strictpure def ensures: ISZ[Exp] = ensuresClause.claims
    @strictpure def infoFlows: ISZ[InfoFlowElement] = infoFlowsClause.flows
    @strictpure def cases: ISZ[Case] = ISZ(Case(Exp.LitString("", Attr(None())), requiresClause, ensuresClause))

    @strictpure override def isEmpty: B =
      reads.isEmpty && requires.isEmpty && modifies.isEmpty && ensures.isEmpty && infoFlows.isEmpty
    @pure override def prettySTOpt(isOnly: B): Option[ST] = {
      if (isEmpty) {
        return None()
      } else {
        val sts = ISZ[ST]() ++
          readsClause.prettySTs("Reads") ++
          requiresClause.prettySTs("Requires") ++
          modifiesClause.prettySTs("Modifies") ++
          ensuresClause.prettySTs("Ensures")
        // TODO: InfoFlows
        return Some(
          st"""Contract${if (isOnly) ".Only" else ""}(
              |  ${(sts, ",\n")}
              |)"""
        )
      }
    }
  }

  @datatype class Cases(val readsClause: Accesses,
                        val modifiesClause: Accesses,
                        val cases: ISZ[Case],
                        val attr: Attr) extends MethodContract {
    @strictpure override def reads: ISZ[Exp.Ref] = readsClause.refs
    @strictpure override def modifies: ISZ[Exp.Ref] = modifiesClause.refs

    @strictpure override def isEmpty: B = reads.isEmpty && modifies.isEmpty && cases.isEmpty

    @pure override def prettySTOpt(isOnly: B): Option[ST] = {
      if (isEmpty) {
        return None()
      } else {
        val sts = ISZ[ST]() ++
          readsClause.prettySTs("Reads") ++
          modifiesClause.prettySTs("Modifies") ++
          ((for (c <- cases) yield c.prettyST))
        return Some(
          st"""Contract${if (isOnly) ".Only" else ""}(
              |  ${(sts, ",\n")}
              |)"""
        )

      }
    }
  }

  @datatype class Case(val label: Exp.LitString,
                       val requiresClause: Claims,
                       val ensuresClause: Claims) {
    @strictpure def requires: ISZ[Exp] = requiresClause.claims
    @strictpure def ensures: ISZ[Exp] = ensuresClause.claims
    @pure def prettyST: ST = {
      val sts = ISZ[ST]() ++
        (if (label.value =!= "") ISZ(label.prettyST) else ISZ[ST]()) ++
        requiresClause.prettySTs("Requires") ++
        ensuresClause.prettySTs("Ensures")
      return st"""Case(
                 |  ${(sts, ",\n")}
                 |)"""
    }
  }

  object InfoFlows {
    @strictpure def empty: InfoFlows = InfoFlows(ISZ(), Attr(None()))
  }

  @datatype trait InfoFlowElement

  @datatype class InfoFlows(val flows: ISZ[InfoFlowElement],
                            val attr: Attr) {
    @strictpure def isEmpty: B = flows.isEmpty
  }

  @datatype class InfoFlowGroup(val label: Exp.LitString,
                                val membersClause: Claims) extends InfoFlowElement {
    @strictpure def members: ISZ[Exp] = membersClause.claims
    @pure def prettyST: ST = {
      val _members = members.map((e: Exp) => e.prettyST)
      return st"Groups(${label.prettyST}, Vars(${(_members, ", ")}))"
    }
  }

  @datatype class InfoFlowFlow(val label: Exp.LitString,
                               val fromClause: Claims,
                               val toClause: Claims) extends InfoFlowElement {
    @strictpure def froms: ISZ[Exp] = fromClause.claims
    @strictpure def tos: ISZ[Exp] = toClause.claims
    @pure def prettyST: ST = {
      val _froms = froms.map((e: Exp) => e.prettyST)
      val _tos = tos.map((e: Exp) => e.prettyST)
      return st"Flows(${label.prettyST}, From(${(_froms, ", ")}), To(${(_tos, ", ")}))"
    }
  }

  @datatype class InfoFlowCase(val label: Exp.LitString,
                               val requiresClause: Claims,
                               val inAgreeClause: Claims,
                               val outAgreeClause: Claims) extends InfoFlowElement {
    @strictpure def requires: ISZ[Exp] = requiresClause.claims
    @strictpure def inAgrees: ISZ[Exp] = inAgreeClause.claims
    @strictpure def outAgrees: ISZ[Exp] = outAgreeClause.claims
    @pure def prettyST: ST = {
      val _requires = requires.map((e: Exp) => e.prettyST)
      val _inAgrees = inAgrees.map((e: Exp) => e.prettyST)
      val _outAgrees = outAgrees.map((e: Exp) => e.prettyST)
      return st"FlowCase(${label.prettyST}, Requires(${(_requires, ", ")}), InAgree(${(_inAgrees, ", ")}), OutAgree(${(_outAgrees, ", ")}))"
    }
  }
}

@datatype class Sequent(val premises: ISZ[Exp],
                        val conclusion: Exp,
                        val steps: ISZ[ProofAst.Step],
                        @hidden val attr: Attr) {
  @pure def prettyST: ST = {
    val ps: ISZ[ST] = for (p <- premises) yield p.prettyST
    return st"""${if (ps.size == 1) ps(0) else st"(${(ps, ", ")})"} ⊢ ${conclusion.prettyST} Proof(
               |  ${(for (step <- steps) yield step.prettyST, ", \n")}
               |)"""
  }
  @pure def compNum: Z = {
    var r = premises.size + 1
    for (step <- steps) {
      r = r + step.compNum
    }
    return r
  }
}

@datatype class ProofAst(val steps: ISZ[ProofAst.Step], @hidden val attr: Attr) extends {
  @pure def prettyST: ST = {
    return st"""Proof(
               |  ${(for (step <- steps) yield step.prettyST, ",\n")}
               |)"""
  }
}

object ProofAst {

  @datatype trait Step {
    @strictpure def id: StepId
    @pure def prettyST: ST
    @pure def attr: Attr
    @pure def compNum: Z
  }

  @datatype trait StepId extends Lit {
    @strictpure def attr: Attr
    @strictpure def posOpt: Option[Position] = attr.posOpt
    @strictpure def isSynthetic: B
    @strictpure def prettyST: ST
    @strictpure def isPremise: B
  }

  object StepId {
    @datatype class Num(val no: Z, @hidden val attr: Attr) extends StepId {
      override def string: String = {
        return if (no < 0) s"Premise#${-no}" else s"#$no"
      }
      @strictpure def isSynthetic: B = F
      @strictpure override def prettyST: ST = if (no < 0) st"Premise#${-no}" else st"$no"
      @strictpure def typedOpt: Option[Typed] = Typed.stepIdOpt
      @strictpure def isPremise: B = no < 0
    }
    @datatype class Str(val isSynthetic: B, val value: String, @hidden val attr: Attr) extends StepId {
      override def string: String = {
        return s""""${ops.StringOps(value).escapeST.render}""""
      }
      @strictpure override def prettyST: ST = st""""$value""""
      @strictpure def typedOpt: Option[Typed] = Typed.stepIdOpt
      @strictpure def isPremise: B = F
    }
  }

  object Step {

    @datatype class Regular(val id: StepId, val claim: Exp, val just: Justification, @hidden val attr: Attr) extends Step {
      @pure override def prettyST: ST = {
        return if (id.isSynthetic)
          st"(${claim.prettyST}) by ${just.prettyST}"
        else
          st"${id.prettyST}  (${claim.prettyST}) by ${just.prettyST}"
      }
      @strictpure def compNum: Z = 1
    }

    @datatype class Assume(val id: StepId, val claim: Exp, @hidden val attr: Attr) extends Step {
      @pure override def prettyST: ST = {
        return st"${id.prettyST}  Assume(${claim.prettyST})"
      }
      @strictpure def compNum: Z = 1
    }

    @datatype class Assert(val id: StepId, val claim: Exp, val steps: ISZ[Step], @hidden val attr: Attr) extends Step {
      @pure override def prettyST: ST = {
        return st"""${id.prettyST}  Assert(${claim.prettyST}, SubProof(
                   |  ${(for (step <- steps) yield step.prettyST, ", ")}
                   |))"""
      }
      @pure def compNum: Z = {
        var r: Z = 1
        for (step <- steps) {
          r = r + step.compNum
        }
        return r
      }
    }

    @datatype class SubProof(val id: StepId, val steps: ISZ[Step], @hidden val attr: Attr) extends Step {
      @pure override def prettyST: ST = {
        return st"""${id.prettyST}  SubProof(
                   |  ${(for (step <- steps) yield step.prettyST, ", ")}
                   |)"""
      }
      @pure def compNum: Z = {
        var r: Z = 0
        for (step <- steps) {
          r = r + step.compNum
        }
        return r
      }
    }

    @datatype class Let(val id: StepId, val params: ISZ[Let.Param], val steps: ISZ[Step], val context: ISZ[String],
                        @hidden val attr: Attr) extends Step {
      @pure override def prettyST: ST = {
        return st"""${id.prettyST}  SubProof {(${(for (p <- params) yield p.prettyST, ", ")}) => (
                   |  ${(for (step <- steps) yield step.prettyST, ", ")}
                   |)}"""
      }
      @pure def compNum: Z = {
        var r: Z = params.size
        for (step <- steps) {
          r = r + step.compNum
        }
        return r
      }
    }

    object Let {

      @datatype class Param(val id: Id, val tipeOpt: Option[Type]) {
        @strictpure def prettyST: ST = tipeOpt match {
          case Some(tipe) => st"${id.prettyST}: ${tipe.prettyST}"
          case _ => id.prettyST
        }
      }

    }

    @datatype trait Justification {
      @strictpure def id: Id
      @strictpure def posOpt: Option[Position]
      @strictpure def prettyST: ST
      @strictpure def hasWitness: B
      @strictpure def witnesses: ISZ[ProofAst.StepId]
      @strictpure def witnessesStOpt: Option[ST] =
        if (hasWitness) None()
        else if (witnesses.size == 0) Some(st" T")
        else if (witnesses.size == 1) Some(st" and ${witnesses(0).prettyST}")
        else Some(st" and (${(for (w <- witnesses) yield w.prettyST, ", ")})")
    }

    object Justification {

      @datatype class Ref(val ref: Exp.Ref,
                          val hasWitness: B,
                          val witnesses: ISZ[ProofAst.StepId]) extends Justification {
        @strictpure override def posOpt: Option[Position] = ref.asExp.posOpt
        @strictpure def id: Id = ref match {
          case ref: Exp.Ident => ref.id
          case ref: Exp.Select => ref.id
          case _ => halt("Infeasible")
        }
        @strictpure def typeArgs: ISZ[Type] = ref match {
          case ref: Exp.Ident => ISZ()
          case ref: Exp.Select => ref.targs
          case _ => halt("Infeasible")
        }
        @strictpure def isOwnedBy(name: ISZ[String]): B = {
          ref match {
            case ref: Exp.Ident => ref.attr.resOpt.get.asInstanceOf[ResolvedInfo.Method].owner == name
            case ref: Exp.Select => ref.attr.resOpt.get.asInstanceOf[ResolvedInfo.Method].owner == name
            case _ => halt("Infeasible")
          }
        }
        @strictpure def resOpt: Option[ResolvedInfo] = {
          ref match {
            case ref: Exp.Ident => ref.attr.resOpt
            case ref: Exp.Select => ref.attr.resOpt
            case _ => halt("Infeasible")
          }
        }
        @pure override def prettyST: ST = {
          return st"${ref.asExp.prettyST}$witnessesStOpt"
        }
      }

      @datatype class Apply(val invoke: Exp.Invoke,
                            val hasWitness: B,
                            val witnesses: ISZ[ProofAst.StepId]) extends Justification {
        @strictpure def id: Id = invokeIdent.id
        @strictpure def invokeIdent: Exp.Ident = invoke.ident
        @strictpure def args: ISZ[Exp] = invoke.args
        @strictpure override def posOpt: Option[Position] = invoke.ident.posOpt
        @pure override def prettyST: ST = {
          return st"${invoke.prettyST}$witnessesStOpt"
        }
      }

      @datatype class ApplyNamed(val invoke: Exp.InvokeNamed,
                                 val hasWitness: B,
                                 val witnesses: ISZ[ProofAst.StepId]) extends Justification {
        @strictpure def id: Id = invokeIdent.id
        @strictpure def invokeIdent: Exp.Ident = invoke.ident
        @pure def args: ISZ[Exp] = {
          val r = MSZ.create[Option[Exp]](invoke.args.size, None())
          for (e <- invoke.args) {
            r(e.index) = Some(e.arg)
          }
          return for (arg <- r.toIS[Option[Exp]]) yield arg.get
        }
        @strictpure override def posOpt: Option[Position] = invoke.ident.posOpt
        @pure override def prettyST: ST = {
          return st"${invoke.prettyST}$witnessesStOpt"
        }
      }

      @datatype class ApplyEta(val eta: Exp.Eta,
                               val hasWitness: B,
                               val witnesses: ISZ[ProofAst.StepId]) extends Justification {
        @strictpure def id: Id = eta.ref match {
          case ref: Exp.Ident => ref.id
          case ref: Exp.Select => ref.id
        }
        @strictpure override def posOpt: Option[Position] = eta.posOpt
        @pure override def prettyST: ST = {
          return st"${eta.prettyST}$witnessesStOpt"
        }
      }

    }

  }

}

@sig sealed trait AssignExp {

  @pure def prettyST: ST = {
    return asStmt.prettyST
  }

  @strictpure def asStmt: Stmt

  @strictpure def hasReturn: B

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

  @pure def typedOpt: Option[Typed]
}

@datatype class Case(val pattern: Pattern, val condOpt: Option[Exp], val body: Body) {
  @pure def prettyST: ST = {
    return st"""case ${pattern.prettyST}${if (condOpt.nonEmpty) st" if ${condOpt.get.prettyST}" else st""} =>
               |  ${(body.prettySTs, "\n")}"""
  }
  @pure def compNum: Z = {
    return 1 + body.compNum
  }
}

object EnumGen {

  @datatype trait Range {
    @strictpure def prettyST: ST
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

  @strictpure def posOpt: Option[Position]

  @strictpure def typedOpt: Option[Typed]

  @strictpure def typed(t: Typed): Type

  @strictpure def prettyST: ST

  override def string: String = {
    typedOpt match {
      case Some(t) => return t.string
      case _ => return prettyST.render
    }
  }

}

object Type {

  @datatype class Named(val name: Name, val typeArgs: ISZ[Type], @hidden val attr: TypedAttr) extends Type {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt
    @strictpure override def typed(t: Typed): Named = this (name, typeArgs, attr(typedOpt = Some(t)))

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

    @strictpure override def prettyST: ST = {
      val typeArgsOpts: Option[ST] =
        if (typeArgs.isEmpty) None() else Some(st"[${(for (ta <- typeArgs) yield ta.prettyST, ", ")}]")
      st"${(for (id <- name.ids) yield id.value, ".")}$typeArgsOpts"
    }

  }

  @datatype class Fun(val isPure: B, val isByName: B, val args: ISZ[Type], val ret: Type, @hidden val attr: TypedAttr) extends Type {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt
    @strictpure override def typed(t: Typed): Fun = this(isPure, isByName, args, ret, attr(typedOpt = Some(t)))

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

    @strictpure override def prettyST: ST = if (isByName) {
      st"=> ${ret.prettyST}"
    } else {
      val pureOpt: Option[ST] = if (isPure) Some(st" @pure") else None()
      st"(${(for (arg <- args) yield arg.prettyST, ", ")}) => ${ret.prettyST} $pureOpt"
    }
  }

  @datatype class Tuple(val args: ISZ[Type], @hidden val attr: TypedAttr) extends Type {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt
    @strictpure override def typed(t: Typed): Tuple = this (args, attr(typedOpt = Some(t)))

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

    @strictpure override def prettyST: ST = st"(${(for (arg <- args) yield arg.prettyST, ", ")})"
  }

}

@datatype trait Pattern {
  @strictpure def posOpt: Option[Position]
  @strictpure def typedOpt: Option[Typed]
  @strictpure def prettyST: ST
}

object Pattern {

  @datatype class Literal(val lit: Lit) extends Pattern {
    @strictpure override def posOpt: Option[Position] = lit.posOpt
    @strictpure override def typedOpt: Option[Typed] = lit.typedOpt
    @pure override def prettyST: ST = {
      return lit.prettyST
    }
  }

  @datatype class LitInterpolate(val prefix: String, val value: String, @hidden val attr: TypedAttr) extends Pattern {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt
    @strictpure override def prettyST: ST = st"""$prefix"$value""""
  }

  @datatype class Ref(val isAccess: B, val name: Name, val receiverTipeOpt: Option[Typed.Name], @hidden val idContext: ISZ[String], @hidden val attr: ResolvedAttr) extends Pattern {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt
    @strictpure override def prettyST: ST = if (isAccess) name.prettyST else st"`${name.prettyST}`"
  }

  @datatype class VarBinding(val id: Id, val tipeOpt: Option[Type], @hidden val idContext: ISZ[String],
                             @hidden val attr: TypedAttr) extends Pattern {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt
    @strictpure override def prettyST: ST = tipeOpt match {
      case Some(tipe) => st"${id.prettyST}: ${tipe.prettyST}"
      case _ => id.prettyST
    }
  }

  @datatype class Wildcard(val typeOpt: Option[Type], @hidden val attr: TypedAttr) extends Pattern {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt
    @strictpure override def prettyST: ST = typeOpt match {
      case Some(tipe) => st"_: ${tipe.prettyST}"
      case _ => st"_"
    }

  }

  @datatype class SeqWildcard(@hidden val attr: TypedAttr) extends Pattern {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt
    @strictpure override def prettyST: ST = st"_*"
  }

  @datatype class Structure(val idOpt: Option[Id],
                            val nameOpt: Option[Name],
                            val patterns: ISZ[Pattern],
                            @hidden val idContext: ISZ[String],
                            @hidden val attr: ResolvedAttr) extends Pattern {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt
    @strictpure override def prettyST: ST = {
      val binding: Option[ST] = if (idOpt.isEmpty) None() else Some(st"${idOpt.get.prettyST}@")
      val name: Option[ST] = if (nameOpt.isEmpty) None() else Some(nameOpt.get.prettyST)
      st"$binding$name(${(for (p <- patterns) yield p.prettyST, ", ")})"
    }

  }

}

@datatype trait Exp {

  @strictpure def posOpt: Option[Position]
  @strictpure def typedOpt: Option[Typed]
  @pure def prettyST: ST
  @pure def fullPosOpt: Option[Position] = {
    return posOpt
  }

  override def string: String = {
    return prettyST.render
  }
}

@datatype trait Lit extends Exp

object Exp {

  @datatype class LitB(val value: B, @hidden val attr: Attr) extends Lit {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = Typed.bOpt
    @strictpure override def prettyST: ST = if (value) st"T" else st"F"
  }

  @datatype class LitC(val value: C, @hidden val attr: Attr) extends Lit {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = Typed.cOpt
    @pure override def prettyST: ST = {
      return st"'${ops.COps(value).escapeString}'"
    }
  }

  @datatype class LitZ(val value: Z, @hidden val attr: Attr) extends Lit {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = Typed.zOpt
    @strictpure override def prettyST: ST = st"${value}"
  }

  @datatype class LitF32(val value: F32, @hidden val attr: Attr) extends Lit {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = Typed.f32Opt
    @strictpure override def prettyST: ST = st"${value}f"
  }

  @datatype class LitF64(val value: F64, @hidden val attr: Attr) extends Lit {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = Typed.f64Opt
    @strictpure override def prettyST: ST = st"${value}d"
  }

  @datatype class LitR(val value: R, @hidden val attr: Attr) extends Lit {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = Typed.rOpt
    @strictpure override def prettyST: ST = st"""r"$value""""
  }

  @datatype class LitString(val value: String, @hidden val attr: Attr) extends Lit {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = Typed.stringOpt
    @pure override def prettyST: ST = {
      return st""""${ops.StringOps(value).escapeST}""""
    }
  }

  @datatype class StringInterpolate(val prefix: String, val lits: ISZ[LitString], val args: ISZ[Exp], @hidden val attr: TypedAttr)
    extends Exp {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt
    @pure override def prettyST: ST = {
      val size: Z = if (lits.size < args.size) args.size else lits.size
      val sts: ISZ[ST] = for (i <- 0 until size;
                              e <- (if (i < lits.size) ISZ(ops.StringOps(lits(i).value).escapeST) else ISZ[ST]()) ++
                                (if (i < args.size) ISZ(st"$${${args(i).prettyST}}") else ISZ[ST]())) yield e
      return st"""$prefix"${(sts, "")}""""
    }
  }

  @datatype class This(val owner: ISZ[String], @hidden val attr: TypedAttr) extends Exp {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt
    @strictpure override def prettyST: ST = st"this"
  }

  @datatype class Super(val idOpt: Option[Id], @hidden val attr: TypedAttr) extends Exp {

    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt
    @strictpure override def prettyST: ST = {
      idOpt match {
        case Some(id) => st"super[${id.value}]"
        case _ => st"super"
      }
    }
  }

  @enum object UnaryOp {
    "Not"
    "Plus"
    "Minus"
    "Complement"
  }

  @datatype class Unary(val op: UnaryOp.Type, val exp: Exp, @hidden val attr: ResolvedAttr, @hidden val opPosOpt: Option[Position]) extends Exp {
    @strictpure override def posOpt: Option[Position] = opPosOpt
    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt
    @strictpure def opString: String = op match {
      case Exp.UnaryOp.Complement => "~"
      case Exp.UnaryOp.Minus => "-"
      case Exp.UnaryOp.Not => "!"
      case Exp.UnaryOp.Plus => "+"
    }
    @pure override def fullPosOpt: Option[Position] = {
      return attr.posOpt
    }
    @pure override def prettyST: ST = {
      val paren: B = exp match {
        case _: Exp.Ident => F
        case exp: LitZ if exp.value >= 0 => F
        case exp: LitF32 if exp.value >= 0f => F
        case exp: LitF64 if exp.value >= 0d => F
        case exp: LitR if exp.value >= r"0" => F
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
    val Equiv: String = "==="
    val EquivUni: String = "≡"
    val Ne: String = "!="
    val Inequiv: String = "=!="
    val InequivUni: String = "≢"
    val FpEq: String = "~~"
    val FpNe: String = "!~"
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
    val Imply: String = "__>:"
    val CondAnd: String = "&&"
    val CondOr: String = "||"
    val CondImply: String = "___>:"
    val Append: String = ":+"
    val Prepend: String = "+:"
    val AppendAll: String = "++"
    val RemoveAll: String = "--"
    val MapsTo: String = "~>"
    val Arrow: String = "=>:"

    def precendenceLevel(op: String): Z = {
      val c = conversions.String.toCis(op)(0)
      c match {
        case '*' => return 2
        case '/' => return 2
        case '%' => return 2
        case '+' => return 3
        case '-' => return 3
        case ':' => return 4
        case '<' => return 5
        case '>' => return 5
        case '=' => return 6
        case '!' => return 6
        case '&' => return 7
        case '^' => return 8
        case '|' => return 9
        case '$' => return 10
        case '_' => return 10
        case _ =>
          ops.COps(c).category match {
            case ops.COps.Category.Ll => return 10
            case ops.COps.Category.Lu => return 10
            case ops.COps.Category.Lt => return 10
            case ops.COps.Category.Lo => return 10
            case ops.COps.Category.Nl => return 10
            case _ => return 1
          }
      }
    }
  }

  @sig sealed trait Ref {
    @strictpure def targs: ISZ[Type]
    @strictpure def asExp: Exp
    @strictpure def posOpt: Option[Position] = asExp.posOpt
    @strictpure def resOpt: Option[ResolvedInfo]
    @strictpure def typedOpt: Option[Typed] = asExp.typedOpt
    @pure def subst(substMap: HashMap[String, Typed]): Ref
  }

  object Binary {
    @pure def shouldParenthesize(level: Z, cop: String, isRight: B, isRightAssoc: B): B = {
      val c = BinaryOp.precendenceLevel(cop)
      if (c > level) {
        return T
      }
      if (c == level) {
        return isRight != isRightAssoc
      }
      return F
    }

    @pure def prettyST(op: String, isOpRightAssoc: B,
                       left: ST, leftOpOpt: Option[String], isLeftIf: B,
                       right: ST, rightOpOpt: Option[String], isRightIf: B): ST = {
      val l = BinaryOp.precendenceLevel(op)
      var singleLine = T
      val leftST: ST = leftOpOpt match {
        case Some(leftOp) =>
          if (l > 6 || op == "___>:" || op == "-->:") {
            singleLine = F
          }
          if (Binary.shouldParenthesize(l, leftOp, F, isOpRightAssoc)) st"($left)" else left
        case _ if isLeftIf =>
          singleLine = F
          st"($left)"
        case _ =>
          left
      }
      val rightST: ST = rightOpOpt match {
        case Some(rightOp) =>
          if (l > 6 || op == "___>:" || op == "-->:") {
            singleLine = F
          }
          if (Binary.shouldParenthesize(l, rightOp, T, isOpRightAssoc)) st"($right)" else right
        case _ if isRightIf =>
          singleLine = F
          st"($right)"
        case _ =>
          right
      }
      val r: ST = if (singleLine || op == BinaryOp.MapsTo) st"$leftST $op $rightST" else
        st"""$leftST $op
            |  $rightST"""
      return r
    }
  }

  @datatype class Binary(val left: Exp, val op: String, val right: Exp, @hidden val attr: ResolvedAttr, @hidden val opPosOpt: Option[Position]) extends Exp {

    @memoize def isRightAssoc: B = {
      return ops.StringOps(op).endsWith(":")
    }

    @strictpure override def posOpt: Option[Position] = opPosOpt
    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt

    @pure override def fullPosOpt: Option[Position] = {
      return attr.posOpt
    }

    @pure override def prettyST: ST = {
      val leftOpOpt: Option[String] = left match {
        case left: Binary => Some(left.op)
        case _ => None()
      }
      val rightOpOpt: Option[String] = right match {
        case right: Binary => Some(right.op)
        case _ => None()
      }
      return Binary.prettyST(op, isRightAssoc, left.prettyST, leftOpOpt, left.isInstanceOf[If], right.prettyST,
        rightOpOpt, right.isInstanceOf[If])
    }
  }

  object Ident {

    val targs: ISZ[Type] = ISZ()

  }

  @datatype class Ident(val id: Id, @hidden val attr: ResolvedAttr) extends Exp with Ref {

    @strictpure override def targs: ISZ[Type] = Ident.targs
    @strictpure override def asExp: Exp = this

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

    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt
    @strictpure override def resOpt: Option[ResolvedInfo] = attr.resOpt

    @pure def subst(substMap: HashMap[String, Typed]): Ref = {
      if (substMap.isEmpty) {
        return this
      }
      return Ident(
        id,
        ResolvedAttr(posOpt, ResolvedInfo.substOpt(attr.resOpt, substMap), Typed.substOpt(attr.typedOpt, substMap))
      )
    }

    @strictpure override def prettyST: ST = id.prettyST
  }

  @datatype class Eta(val ref: Ref, @hidden val attr: TypedAttr) extends Exp {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt
    @pure override def prettyST: ST = {
      return st"${ref.asExp.prettyST} _"
    }
  }

  @datatype class Tuple(val args: ISZ[Exp], @hidden val attr: TypedAttr) extends Exp {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt
    @pure override def prettyST: ST = {
      return st"(${(for (arg <- args) yield arg.prettyST, ", ")})"
    }
  }

  @datatype class Select(val receiverOpt: Option[Exp], val id: Id, val targs: ISZ[Type], @hidden val attr: ResolvedAttr)
    extends Exp with Ref {

    @strictpure override def asExp: Exp = this
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def resOpt: Option[ResolvedInfo] = attr.resOpt
    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt

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
      return st"${receiverOptST(posOpt, receiverOpt)}${id.value}$targsOpt"
    }

    @strictpure override def hash: Z = (id.value, attr.resOpt).hash
    @pure def isEqual(other: Select): B = {
      val skipReceiver: B = attr.resOpt match {
        case Some(res) => res match {
          case res: ResolvedInfo.Var => res.isInObject
          case res: ResolvedInfo.Method => res.isInObject
          case _: ResolvedInfo.EnumElement => T
          case _: ResolvedInfo.Object => T
          case _ => F
        }
        case _ => F
      }
      if (skipReceiver) {
        return id.value == other.id.value && resOpt == other.resOpt
      }
      return receiverOpt == other.receiverOpt && id.value == other.id.value
    }
  }

  @datatype class Invoke(val receiverOpt: Option[Exp],
                         val ident: Ident,
                         val targs: ISZ[Type],
                         val args: ISZ[Exp],
                         @hidden val attr: ResolvedAttr) extends Exp {

    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt
    @pure override def prettyST: ST = {
      val targsOpt: Option[ST] = if (targs.isEmpty) None() else Some(st"[${(targs, ", ")}]")
      val as = st"(${(for (arg <- args) yield arg.prettyST, ", ")})"
      ident.attr.resOpt match {
        case Some(ResolvedInfo.BuiltIn(ResolvedInfo.BuiltIn.Kind.Apply)) =>
          return st"${receiverOpt.get.prettyST}$targsOpt$as"
        case _ =>
          return st"${receiverOptST(posOpt, receiverOpt)}${ident.id.value}$targsOpt$as"
      }
    }
    @strictpure override def hash: Z = (ident, args).hash
    @pure def isEqual(other: Invoke): B = {
      val skipReceiver: B = ident.resOpt match {
        case Some(res) => res match {
          case res: ResolvedInfo.Var => res.isInObject
          case res: ResolvedInfo.Method if res.isInObject => res.isInObject
          case _: ResolvedInfo.Object => T
          case _ => F
        }
        case _ => F
      }
      if (skipReceiver) {
        return ident == other.ident && args == other.args
      }
      return receiverOpt == other.receiverOpt && ident == other.ident && args == other.args
    }
  }

  @datatype class InvokeNamed(val receiverOpt: Option[Exp],
                              val ident: Ident,
                              val targs: ISZ[Type],
                              val args: ISZ[NamedArg],
                              @hidden val attr: ResolvedAttr) extends Exp {

    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt
    @pure override def prettyST: ST = {
      val targsOpt: Option[ST] = if (targs.isEmpty) None() else Some(st"[${(for (targ <- targs) yield targ.prettyST, ", ")}]")
      val as = st"(${(for (arg <- args) yield st"${arg.id.value} = ${arg.arg.prettyST}", ", ")})"
      ident.attr.resOpt match {
        case Some(ResolvedInfo.BuiltIn(ResolvedInfo.BuiltIn.Kind.Apply)) =>
          return st"${receiverOpt.map((rcv: Exp) => st"${rcv.prettyST}")}$targsOpt$as"
        case _ =>
          return st"${receiverOptST(posOpt, receiverOpt)}${ident.id.value}$targsOpt$as"
      }
    }
    @strictpure override def hash: Z = (ident, args).hash
    @pure def isEqual(other: InvokeNamed): B = {
      val skipReceiver: B = ident.resOpt match {
        case Some(res) => res match {
          case res: ResolvedInfo.Var => res.isInObject
          case res: ResolvedInfo.Method if res.isInObject => res.isInObject
          case _: ResolvedInfo.Object => T
          case _ => F
        }
        case _ => F
      }
      if (skipReceiver) {
        return ident == other.ident && args == other.args
      }
      return receiverOpt == other.receiverOpt && ident == other.ident && args == other.args
    }
  }

  @datatype class If(val cond: Exp, val thenExp: Exp, val elseExp: Exp, @hidden val attr: TypedAttr) extends Exp {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt
    @pure override def prettyST: ST = {
      return st"if (${cond.prettyST}) ${thenExp.prettyST} else ${elseExp.prettyST}"
    }
  }

  @datatype class TypeCond(val args: ISZ[Exp], val fun: Exp.Fun, @hidden val attr: Attr) extends Exp {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = Typed.bOpt
    @pure override def prettyST: ST = {
      return st"?(${(for (arg <- args) yield arg.prettyST, ", ")} ${fun.prettySTH(F)}"
    }
  }

  @datatype class Sym(val num: Z, @hidden val attr: TypedAttr) extends Exp {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt
    @strictpure override def prettyST: ST = st"cx$$$num"
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

    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt

    @pure def prettySTH(isParen: B): ST = {
      @strictpure def paramST(p: Fun.Param): ST = {
        val id: String = p.idOpt match {
          case Some(id) => id.value
          case _ => "_"
        }
        p.tipeOpt match {
          case Some(tipe) => st"$id: ${tipe.prettyST}"
          case _ => st"$id"
        }
      }

      val ps: ST =
        if (params.size == 1 && params(0).tipeOpt.isEmpty) st"${paramST(params(0))}"
        else st"(${(for (p <- params) yield paramST(p), ", ")})"
      exp match {
        case exp: Stmt.Expr =>
          if (isParen) {
            return st"($ps => ${exp.exp.prettyST})"
          } else {
            return st"""{ $ps =>
                       |  ${exp.exp.prettyST}
                       |}"""
          }
        case _ => return st"$ps => { ... }"
      }
    }

    @pure override def prettyST: ST = {
      return prettySTH(T)
    }
  }

  @datatype class ForYield(val enumGens: ISZ[EnumGen.For], val exp: Exp, @hidden val attr: TypedAttr) extends Exp {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt
    @pure override def prettyST: ST = {
      return st"for (${(for (enumGen <- enumGens) yield enumGen.prettyST, "; ")}) yield ${exp.prettyST}"
    }
  }

  @datatype trait Quant extends Exp {
    @strictpure def isForall: B
    @strictpure def fun: Exp.Fun
  }

  @datatype class QuantType(val isForall: B, val fun: Exp.Fun, @hidden val attr: Attr) extends Quant {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = Typed.bOpt
    @pure override def prettyST: ST = {
      return st"${if (isForall) "∀" else "∃"}${fun.prettyST}"
    }
  }

  @datatype class QuantRange(val isForall: B, val lo: Exp, val hi: Exp, val hiExact: B, val fun: Exp.Fun, @hidden val attr: ResolvedAttr) extends Quant {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = Typed.bOpt
    @pure override def prettyST: ST = {
      return st"${if (isForall) "∀" else "∃"}(${lo.prettyST} ${if (hiExact) "to" else "until"} ${hi.prettyST})${fun.prettyST}"
    }
  }

  @datatype class QuantEach(val isForall: B, val seq: Exp, val fun: Exp.Fun, @hidden val attr: ResolvedAttr) extends Quant {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = Typed.bOpt
    @pure override def prettyST: ST = {
      return st"${if (isForall) "∀" else "∃"}(${seq.prettyST})${fun.prettyST}"
    }
  }

  @datatype class Input(val exp: Exp, @hidden val attr: Attr) extends Exp {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = exp.typedOpt
    @pure override def prettyST: ST = {
      return st"In(${exp.prettyST})"
    }
  }

  @datatype class Old(val exp: Exp, @hidden val attr: Attr) extends Exp {
    @strictpure override def posOpt: Option[Position] = attr.posOpt

    @strictpure override def typedOpt: Option[Typed] = exp.typedOpt

    @pure override def prettyST: ST = {
      return st"Old(${exp.prettyST})"
    }
  }

  @datatype class RS(val rightToLeft: B, val refs: ISZ[Exp.Ref], @hidden val attr: Attr) extends Exp {
    @strictpure override def posOpt: Option[Position] = attr.posOpt

    @strictpure override def typedOpt: Option[Typed] = Typed.rsOpt

    @pure override def prettyST: ST = {
      return st"${if (rightToLeft) "~" else ""}RS(${(for (ref <- refs) yield ref.asExp.prettyST, ", ")})"
    }
  }

  @datatype class At(val tipeOpt: Option[Type],
                     val exp: Exp,
                     val num: Exp.LitZ,
                     val linesFresh: ISZ[Exp.LitZ],
                     @hidden val attr: Attr) extends Exp {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = tipeOpt match {
      case Some(tipe) => tipe.typedOpt
      case _ => exp.typedOpt
    }
    @pure override def prettyST: ST = {
      tipeOpt match {
        case Some(tipe) =>
          return st"At[${tipe.prettyST}](${exp.prettyST}, ${(for (n <- num +: linesFresh) yield n.prettyST, ", ")})"
        case _ => return st"At(${exp.prettyST}, ${(for (n <- num +: linesFresh) yield n.prettyST, ", ")})"
      }
    }
  }

  @datatype class LoopIndex(val tipeOpt: Option[Type], val exp: Exp.Ident, @hidden val attr: TypedAttr) extends Exp {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = tipeOpt match {
      case Some(tipe) => tipe.typedOpt
      case _ => attr.typedOpt
    }
    @strictpure override def prettyST: ST = {
      val tOpt: Option[ST] = tipeOpt match {
        case Some(t) => Some(st"[${t.prettyST}]")
        case _ => None()
      }
      st"Idx$tOpt(${exp.prettyST})"
    }
  }

  @datatype class StateSeq(val id: Id, val fragments: ISZ[StateSeq.Fragment], @hidden val attr: Attr) extends Exp {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = Typed.bOpt
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
    @strictpure def isEqual(other: Result): B = typedOpt == other.typedOpt

    @strictpure override def hash: Z = {
      typedOpt match {
        case Some(t) => ("Result", t).hash
        case _ => "Result".hash
      }
    }

    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt
    @strictpure override def prettyST: ST = {
      val tOpt: Option[ST] = tipeOpt match {
        case Some(t) => Some(st"[${t.prettyST}]")
        case _ => None()
      }
      st"Res$tOpt"
    }
  }

  @datatype class StrictPureBlock(val block: Stmt.Block,
                                  @hidden val attr: TypedAttr) extends Exp {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = attr.typedOpt
    @pure override def prettyST: ST = {
      return block.prettyST
    }
  }

  @datatype class Labeled(val numOpt: Option[LitZ], val exp: Exp, @hidden val attr: Attr) extends Exp {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = exp.typedOpt
    @pure override def prettyST: ST = {
      numOpt match {
        case Some(num) => return st"""(${exp.prettyST}: @l(${num.prettyST}))"""
        case _ => return st"""(${exp.prettyST}: @l)"""
      }
    }
  }

  @datatype class AssumeAgree(val channel: LitString,
                              val requiresClause: MethodContract.Claims,
                              val inAgreeClause: MethodContract.Claims, @hidden val attr: Attr) extends Exp {
    @strictpure def requires: ISZ[Exp] = requiresClause.claims
    @strictpure def inAgrees: ISZ[Exp] = inAgreeClause.claims
    @strictpure def posOpt: Option[Position] = attr.posOpt
    @strictpure def typedOpt: Option[Typed] = Typed.bOpt
    @pure def prettyST: ST = {
      val optRequires: Option[ST] =
        if (requires.nonEmpty) Some(st", Requires(${(requires.map((e: Exp) => e.prettyST), ", ")})")
        else None()
      val optInAgree: Option[ST] =
        if (inAgrees.nonEmpty) Some(st", InAgree(${(inAgrees.map((e: Exp) => e.prettyST), ", ")})")
        else None()
      return st"AssumeAgree($channel$optInAgree)"
    }
  }

  @datatype class AssertAgree(val channel: LitString, val outAgreeClause: MethodContract.Claims, @hidden val attr: Attr) extends Exp {
    @strictpure def outAgrees: ISZ[Exp] = outAgreeClause.claims
    @strictpure def posOpt: Option[Position] = attr.posOpt
    @strictpure def typedOpt: Option[Typed] = Typed.bOpt
    @pure def prettyST: ST = {
      val optArgs: Option[ST] =
        if (outAgrees.nonEmpty) Some(st", OutAgree(${(outAgrees.map((e: Exp) => e.prettyST), ", ")})")
        else None()
      return st"AssertAgree($channel$optArgs)"
    }
  }

  @datatype class InfoFlowInvariant(val flowInvariants: ISZ[MethodContract.InfoFlowCase], @hidden val attr: Attr) extends Exp {
    @strictpure override def posOpt: Option[Position] = attr.posOpt
    @strictpure override def typedOpt: Option[Typed] = Typed.bOpt
    @pure override def prettyST: ST = {
      val cases = flowInvariants.map((m: MethodContract.InfoFlowCase) => m.prettyST)
      return st"InfoFlowInvariant(${(cases, ",\n")})"
    }
  }

  @pure def shouldParenthesize(exp: Exp): B = {
    exp match {
      case _: Exp.Ref =>
      case _: Exp.Invoke =>
      case _: Exp.InvokeNamed =>
      case _: Lit =>
      case _: Exp.Tuple =>
      case _ => return T
    }
    return F
  }

  @pure def receiverOptST(posOpt: Option[Position], receiverOpt: Option[Exp]): Option[ST] = {
    if (receiverOpt.isEmpty) {
      return None()
    }
    val exp = receiverOpt.get
    exp match {
      case exp: Exp.This if posOpt.nonEmpty && exp.posOpt == posOpt => return None()
      case exp => return if (shouldParenthesize(exp)) Some(st"(${exp.prettyST}).") else Some(st"${exp.prettyST}.")
    }
  }
}

@datatype class NamedArg(val id: Id, val arg: Exp, val index: Z)

@datatype class Id(val value: String, @hidden val attr: Attr) {
  @strictpure def prettyST: ST = st"$value"
}

@datatype class Name(val ids: ISZ[Id], @hidden val attr: Attr) {
  @strictpure def prettyST: ST = st"${(for (id <- ids) yield id.prettyST, ".")}"
}

@datatype class Body(val stmts: ISZ[Stmt], @hidden val undecls: ISZ[ResolvedInfo.LocalVar]) {

  @strictpure def leaves: ISZ[Option[Stmt]] = if (stmts.isEmpty) ISZ(None()) else stmts(stmts.size - 1).leaves

  @memoize def allReturns: B = {
    for (lOpt <- leaves) {
      lOpt match {
        case Some(_: Stmt.Return) =>
        case _ => return F
      }
    }
    return T
  }

  @pure def prettySTs: ISZ[ST] = {
    return for (stmt <- stmts) yield if (stmt.isInstanceOf[Stmt.Block]) st";${stmt.prettyST}" else stmt.prettyST
  }
  @strictpure def hasReturn: B = ops.ISZOps(stmts).exists((stmt: Stmt) => stmt.hasReturnMemoized)
  @pure def compNum: Z = {
    var r: Z = 0
    for (stmt <- stmts) {
      r = r + stmt.compNum
    }
    return r
  }
}

@datatype class AdtParam(val isHidden: B, val isVal: B, val id: Id, val tipe: Type) {
  @strictpure def prettyST: ST = {
    val hidden: String = if (isHidden) "@hidden " else ""
    val valVar: String = if (isVal) "val" else "var"
    st"$hidden$valVar ${id.prettyST}: ${tipe.prettyST}"
  }
}

@datatype class Annotation(val name: ISZ[String], val args: ISZ[Lit])

@datatype class MethodSig(val purity: Purity.Type,
                          val annotations: ISZ[Annotation],
                          val id: Id,
                          val typeParams: ISZ[TypeParam],
                          val hasParams: B,
                          val params: ISZ[Param],
                          val returnType: Type) {

  @strictpure def paramIdTypes: ISZ[(Id, Typed)] = for (p <- params) yield (p.id, p.tipe.typedOpt.get)

  @strictpure def funType: Typed.Fun = {
    val pts: ISZ[Typed] = for (p <- params) yield p.tipe.typedOpt.get
    Typed.Fun(purity, !hasParams, pts, returnType.typedOpt.get)
  }

  @strictpure def prettyST: ST = {
    val tpOpt: Option[ST] = if (typeParams.isEmpty) None() else Some(
      st"[${(for (tp <- typeParams) yield tp.prettyST, ", ")}]"
    )
    val psOpt: Option[ST] = if (hasParams) Some(
      st"(${(for (p <- params) yield p.prettyST, ", ")})"
    ) else None()
    st"${id.prettyST}$tpOpt$psOpt: ${returnType.prettyST}"
  }
}

@datatype class Param(val isHidden: B, val id: Id, val tipe: Type) {
  @strictpure def prettyST: ST = {
    val hidden: String = if (isHidden) "@hidden " else ""
    st"$hidden${id.prettyST}: ${tipe.prettyST}"
  }
}

@datatype class TypeParam(val id: Id, val kind: Typed.VarKind.Type) {
  @strictpure def isImmutable: B = kind != Typed.VarKind.Mutable
  @strictpure def isIndex: B = kind == Typed.VarKind.Index
  @strictpure def prettyST: ST = {
    val k: String = kind match {
      case Typed.VarKind.Index => "@index"
      case Typed.VarKind.Mutable => "@mut"
      case Typed.VarKind.Immutable => "@imm"
    }
    st"$k ${id.prettyST}"
  }
}

object TypeParam {
  @strictpure def stOpt(typeParams: ISZ[TypeParam]): Option[ST] = if (typeParams.isEmpty) None() else Some(
    st"[${(for (tp <- typeParams) yield tp.prettyST, ", ")}]"
  )
}

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
      "Arrow"
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
      "BinaryEquiv"
      "BinaryNe"
      "BinaryInequiv"
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
      "Eprint"
      "Eprintln"
      "Halt"
      "Hash"
      "IsInstanceOf"
      "Indices"
      "Min"
      "Max"
      "Native"
      "Print"
      "Println"
      "SetOptions"
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

  @datatype class LocalVar(val context: ISZ[String],
                           val scope: ResolvedInfo.LocalVar.Scope.Type,
                           val isSpec: B,
                           val isVal: B,
                           val id: String) extends ResolvedInfo {
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
