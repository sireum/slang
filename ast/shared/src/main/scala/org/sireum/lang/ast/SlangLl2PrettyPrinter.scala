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
import org.sireum.lang.{ast => AST}

object SlangLl2PrettyPrinter {
  @pure def prettyPrint(ast: AST.TopUnit.Program): ST = {
    val lineSep = "\n"
    @strictpure def printName(o: AST.Name): ST = st"${(for (id <- o.ids) yield id.value, ".")}"
    @strictpure def printNamedSelector(o: AST.Stmt.Import.NamedSelector): ST = st"${o.from.value} => ${o.to.value}"
    @strictpure def printSelector(selector: AST.Stmt.Import.Selector): ST = selector match {
      case selector: AST.Stmt.Import.MultiSelector => st"{${(for (ns <- selector.selectors) yield printNamedSelector(ns), ", ")}}"
      case _: AST.Stmt.Import.WildcardSelector => st"_"
    }
    @strictpure def printImporter(o: AST.Stmt.Import.Importer): ST =
      st"import ${printName(o.name)}${if (o.selectorOpt.isEmpty) st"" else st".${printSelector(o.selectorOpt.get)}"}"
    @strictpure def isSireumImport(i: AST.Stmt.Import.Importer): B = i.selectorOpt match {
      case Some(_: AST.Stmt.Import.WildcardSelector) => i.name.ids.size == 2 && i.name.ids(0).value == "org" && i.name.ids(1).value == "sireum"
      case _ => F
    }
    @strictpure def printImport(o: AST.Stmt.Import): ST = st"${(for (i <- o.importers if !isSireumImport(i)) yield printImporter(i), lineSep)}"
    @strictpure def printType(o: AST.Type): ST = o match {
      case o: AST.Type.Named => st"${printName(o.name)}${if (o.typeArgs.isEmpty) st"" else st"[${(for (t <- o.typeArgs) yield printType(t), ", ")}]"}"
      case o: AST.Type.Tuple => st"(${(for (t <- o.args) yield printType(t), ", ")})"
      case o: AST.Type.Fun =>
        if (o.isByName) {
          st"=> ${if (o.isPure) st"@pure " else st""}${printType(o.ret)}"
        } else if (o.args.size == 1) {
          val arg: ST = if (o.args(0).isInstanceOf[AST.Type.Fun]) st"(${printType(o.args(0))})" else printType(o.args(0))
          st"$arg => ${if (o.isPure) st"@pure " else st""}${printType(o.ret)}"
        } else {
          st"(${(for (t <- o.args) yield printType(t), ", ")}) => ${if (o.isPure) st"@pure " else st""}${printType(o.ret)}"
        }
    }
    @strictpure def printInvoke(o: AST.Exp.Invoke): ST = st"${if (o.receiverOpt.isEmpty) st"" else st"${printExp(o.receiverOpt.get)}."}${o.ident.id.value}${if (o.targs.isEmpty) st"" else st"[${(for (t <- o.targs) yield printType(t), ", ")}]"}${if (o.args.isEmpty) st"" else st"(${(for (e <- o.args) yield printExp(e), ", ")})"}"
    @strictpure def printNamedArg(o: AST.NamedArg): ST = st"${o.id.value} = ${printExp(o.arg)}"
    @strictpure def printInvokeNamed(o: AST.Exp.InvokeNamed): ST = st"${if (o.receiverOpt.isEmpty) st"" else st"${printExp(o.receiverOpt.get)}."}${o.ident.id.value}${if (o.targs.isEmpty) st"" else st"[${(for (t <- o.targs) yield printType(t), ", ")}]"}${if (o.args.isEmpty) st"" else st"(${(for (na <- o.args) yield printNamedArg(na), ", ")})"}"
    @strictpure def printBinary(o: AST.Exp.Binary): ST = {
      val leftOpOpt: Option[String] = o.left match {
        case left: AST.Exp.Binary => Some(left.op)
        case _ => None()
      }
      val rightOpOpt: Option[String] = o.right match {
        case right: AST.Exp.Binary => Some(right.op)
        case _ => None()
      }
      val op: String = o.op match {
        case string"->:" => "->"
        case string"-->:" => "-->"
        case string"__>:" => "->"
        case string"___>:" => "-->"
        case _ => o.op
      }
      AST.Exp.Binary.prettyST(op, o.isRightAssoc, printExp(o.left), leftOpOpt, o.left.isInstanceOf[AST.Exp.If],
        printExp(o.right), rightOpOpt, o.right.isInstanceOf[AST.Exp.If])
    }
    @strictpure def printExp(o: AST.Exp): ST = o match {
      case o: AST.Exp.AssertAgree => halt(s"TODO: $o")
      case o: AST.Exp.AssumeAgree => halt(s"TODO: $o")
      case o: AST.Exp.At => halt(s"TODO: $o")
      case o: AST.Exp.Binary => printBinary(o)
      case o: AST.Exp.Eta => st"${printExp(o.ref.asExp)} _"
      case o: AST.Exp.ForYield => halt(s"TODO: $o")
      case o: AST.Exp.Fun => halt(s"TODO: $o")
      case o: AST.Exp.Ident => st"${o.id.value}"
      case o: AST.Exp.If => st"(${printExp(o.cond)}? ${printExp(o.thenExp)} : ${printExp(o.elseExp)})"
      case o: AST.Exp.InfoFlowInvariant => halt(s"TODO: $o")
      case o: AST.Exp.Input => st"In(${printExp(o.exp)})"
      case o: AST.Exp.Invoke => printInvoke(o)
      case o: AST.Exp.InvokeNamed => printInvokeNamed(o)
      case o: AST.Exp.Labeled => halt(s"TODO: $o")
      case o: AST.Exp.LitB => o.prettyST
      case o: AST.Exp.LitC => o.prettyST
      case o: AST.Exp.LitF32 => o.prettyST
      case o: AST.Exp.LitF64 => o.prettyST
      case o: AST.Exp.LitR => o.prettyST
      case o: AST.Exp.LitString => o.prettyST
      case o: AST.Exp.LitZ => o.prettyST
      case o: AST.Exp.LoopIndex => halt(s"TODO: $o")
      case o: AST.Exp.Old => st"Old(${printExp(o.exp)})"
      case o: AST.Exp.QuantEach => st"${if (o.isForall) "∀" else "∃"} ${(for (p <- o.fun.params) yield st"${p.idOpt.get.value}: ${printExp(o.seq)}", ", ")} => ${printAssignExp(o.fun.exp)}"
      case o: AST.Exp.QuantRange => st"${if (o.isForall) "∀" else "∃"} ${(for (p <- o.fun.params) yield st"${p.idOpt.get.value}: ${printExp(o.lo)} ${if (o.hiExact) ".." else "..<"} ${printExp(o.hi)}", ", ")} => ${printAssignExp(o.fun.exp)}"
      case o: AST.Exp.QuantType => st"${if (o.isForall) "∀" else "∃"} ${(for (p <- o.fun.params) yield st"${p.idOpt.get.value}: ${printType(p.tipeOpt.get)}", ", ")} => ${printAssignExp(o.fun.exp)}"
      case _: AST.Exp.Result => st"Res"
      case o: AST.Exp.RS => halt(s"TODO: $o")
      case o: AST.Exp.Select => st"${if (o.receiverOpt.isEmpty) st"" else st"${printExp(o.receiverOpt.get)}."}${o.id.value}${if (o.targs.isEmpty) st"" else st"[${(for (t <- o.targs) yield printType(t), ", ")}]"}"
      case o: AST.Exp.StateSeq => halt(s"TODO: $o")
      case o: AST.Exp.StrictPureBlock => halt(s"TODO: $o")
      case o: AST.Exp.StringInterpolate => halt(s"TODO: $o")
      case _: AST.Exp.Super => st"super"
      case o: AST.Exp.Sym => halt(s"TODO: $o")
      case _: AST.Exp.This => st"this"
      case o: AST.Exp.Tuple => st"(${(for (exp <- o.args) yield printExp(exp), ", ")})"
      case o: AST.Exp.TypeCond => halt(s"TODO: $o")
      case o: AST.Exp.Unary =>
        val paren: B = o.exp match {
          case _: Exp.Ident => F
          case exp: AST.Exp.LitZ if exp.value >= 0 => F
          case exp: AST.Exp.LitF32 if exp.value >= 0f => F
          case exp: AST.Exp.LitF64 if exp.value >= 0d => F
          case exp: AST.Exp.LitR if exp.value >= r"0" => F
          case _ => T
        }
        if (paren) st"${o.opString}(${printExp(o.exp)})" else st"${o.opString}${printExp(o.exp)}"
      case o: AST.ProofAst.StepId.Num => o.prettyST
      case o: AST.ProofAst.StepId.Str => o.prettyST
    }
    @strictpure def printAssignExp(o: AST.AssignExp): ST = printStmt(T, o.asStmt)
    @strictpure def printWitnesses(o: AST.ProofAst.Step.Justification): ST = if (o.hasWitness) st" ${(for (w <- o.witnesses) yield w.prettyST, " ")}." else st"."
    @strictpure def isNatDedId(id: String): B = id match {
      case string"Subst_>" => T
      case string"Subst_<" => T
      case string"AndI" => T
      case string"AndE1" => T
      case string"AndE2" => T
      case string"OrI1" => T
      case string"OrI2" => T
      case string"OrE" => T
      case string"ImplyI" => T
      case string"ImplyE" => T
      case string"NegI" => T
      case string"NegE" => T
      case string"BottomE" => T
      case string"PbC" => T
      case string"SAndI" => T
      case string"SAndE1" => T
      case string"SAndE2" => T
      case string"SOrI1" => T
      case string"SOrI2" => T
      case string"SOrE" => T
      case string"SImplyI" => T
      case string"SImplyE" => T
      case string"AllI" => T
      case string"AllE" => T
      case string"ExistsI" => T
      case string"ExistsE" => T
      case _ => F
    }
    @strictpure def printTypeArgs(args: ISZ[AST.Type]): ST = if (args.isEmpty) st"" else st"[${(for (t <- args) yield printType(t), ", ")}]"
    @strictpure def printJustExp(o: AST.Exp): ST = o match {
      case o: AST.Exp.Invoke if isNatDedId(o.ident.id.value) =>
        st"${o.ident.id.value}${printTypeArgs(o.targs)} ${(for (arg <- o.args) yield printExp(arg), " ")}"
      case _ => printExp(o)
    }
    @strictpure def printJust(o: AST.ProofAst.Step.Justification): ST = o match {
      case o: AST.ProofAst.Step.Justification.Apply => st"by ${printJustExp(o.invoke)}${printWitnesses(o)}"
      case o: AST.ProofAst.Step.Justification.ApplyEta => st"by ${printExp(o.eta)}${printWitnesses(o)}"
      case o: AST.ProofAst.Step.Justification.ApplyNamed => st"by ${printExp(o.invoke)}${printWitnesses(o)}"
      case o: AST.ProofAst.Step.Justification.Ref => st"by ${printJustExp(o.ref.asExp)}${printWitnesses(o)}"
    }
    @strictpure def printProofStep(o: AST.ProofAst.Step): ST = o match {
      case o: AST.ProofAst.Step.Assert => st"${o.id.prettyST}. assert(${printExp(o.claim)})"
      case o: AST.ProofAst.Step.Assume => st"${o.id.prettyST}. assume(${printExp(o.claim)})"
      case o: AST.ProofAst.Step.Let =>
        st"""${o.id.prettyST}. {
            |  ${(for (param <- o.params) yield st"${param.id.value}${if (param.tipeOpt.isEmpty) st"" else st": ${printType(param.tipeOpt.get)}"}", "  ")}
            |  ${(for (step <- o.steps) yield printProofStep(step), lineSep)}
            |}"""
      case o: AST.ProofAst.Step.Regular => st"${o.id.prettyST}. ${printExp(o.claim)}  ${printJust(o.just)}"
      case o: AST.ProofAst.Step.SubProof =>
        st"""${o.id.prettyST}. {
            |  ${(for (step <- o.steps) yield printProofStep(step), lineSep)}
            |}"""
    }
    @strictpure def printDeduceSteps(o: AST.Stmt.DeduceSteps): ST =
      st"""deduce {
          |  ${(for (step <- o.steps) yield printProofStep(step), lineSep)}
          |}"""
    @strictpure def shouldAddDo(o: AST.Exp): B = o match {
      case _: AST.Exp.Invoke => F
      case _: AST.Exp.InvokeNamed => F
      case _: AST.Exp.Select => F
      case _ => T
    }
    @strictpure def printIfElse(elseBody: AST.Body): ST = {
      elseBody.stmts match {
        case ISZ() => st"}"
        case ISZ(ifStmt: AST.Stmt.If) =>
          st"""} else if ${printExp(ifStmt.cond)} {
              |  ${(for (stmt <- ifStmt.thenBody.stmts) yield printStmt(F, stmt), lineSep)}
              |}${printIfElse(ifStmt.elseBody)}"""
        case _ =>
          st"""} else {
              |  ${(for (stmt <- elseBody.stmts) yield printStmt(F, stmt), lineSep)}
              |}"""
      }
    }
    @strictpure def printPurity(o: AST.Purity.Type): ST = o match {
      case AST.Purity.Abs => st" @abs"
      case AST.Purity.Impure => st""
      case AST.Purity.Memoize => st" @memoize"
      case AST.Purity.Pure => st" @pure"
      case AST.Purity.StrictPure => st" @strictpure"
    }
    @strictpure def printTypeParam(o: AST.TypeParam): ST = {
      val ann: String = if (o.isImmutable) "@imm" else if (o.isIndex) "@index" else "@mut"
      st"$ann ${o.id.value}"
    }
    @strictpure def printTypeParams(o: ISZ[AST.TypeParam]): ST = if (o.isEmpty) st"" else st"[${(for (tp <- o) yield printTypeParam(tp), ", ")}]"
    @strictpure def printParam(o: AST.Param): ST = st"${if (o.isHidden) "@hidden " else ""}${o.id.value}: ${printType(o.tipe)}"
    @strictpure def printParams(hasParams: B, o: ISZ[AST.Param]): ST = if (hasParams) st"(${(for (p <- o) yield printParam(p), ", ")})" else st""
    @strictpure def printAccesses(id: String, o: AST.MethodContract.Accesses): ISZ[ST] =
      if (o.isEmpty) ISZ() else ISZ(st"$id(${(for (ref <- o.refs) yield printExp(ref.asExp), ", ")})")
    @strictpure def printClaims(id: String, o: AST.MethodContract.Claims): ISZ[ST] =
      if (o.isEmpty) ISZ() else ISZ(st"$id(${(for (claim <- o.claims) yield printExp(claim), ", ")})")
    @pure def printMethodContract(o: AST.MethodContract): ISZ[ST] = {
      o match {
        case o: AST.MethodContract.Simple =>
          return printAccesses("Reads", o.readsClause) ++
            printClaims("Requires", o.requiresClause) ++
            printAccesses("Modifies", o.modifiesClause) ++
            printClaims("Ensures", o.ensuresClause)
        case o: AST.MethodContract.Cases =>
          var r = printAccesses("Reads", o.readsClause) ++ printAccesses("Modifies", o.modifiesClause)
          for (cs <- o.cases) {
            r = r :+
              st"""Case(${if (cs.label.value.size == 0) st"" else st"${cs.label.prettyST},"}
                  |  ${(printClaims("Requires", cs.requiresClause) ++ printClaims("Ensures", cs.ensuresClause), s",$lineSep")}
                  |)"""
          }
          r = ISZ(
            st"""Cases(
                |  ${(r, s",$lineSep")}
                |)"""
          )
          return r
      }
    }
    @strictpure def printLiteral(o: AST.Lit): ST = o.prettyST
    @strictpure def printPattern(o: AST.Pattern): ST = o match {
      case o: AST.Pattern.Literal => printLiteral(o.lit)
      case o: AST.Pattern.LitInterpolate =>
        val attr = AST.Attr(o.posOpt)
        o.prefix match {
          case string"string" => AST.Exp.LitString(o.value, attr).prettyST
          case string"s" => AST.Exp.LitString(o.value, attr).prettyST
          case string"z" => AST.Exp.LitZ(Z(o.value).get, attr).prettyST
          case string"r" => AST.Exp.LitR(R(o.value).get, attr).prettyST
          case string"c" => AST.Exp.LitC(ops.StringOps(o.value).first, attr).prettyST
          case prefix => st"${o.value}$prefix"
        }
      case o: AST.Pattern.Ref => st".${printName(o.name)}"
      case _: AST.Pattern.SeqWildcard => st"*"
      case o: AST.Pattern.Structure =>
        val id: ST = o.idOpt match {
          case Some(idAt) => st"$idAt@"
          case _ => st""
        }
        val name: ST = o.nameOpt match {
          case Some(n) => printName(n)
          case _ => st""
        }
        st"$id$name(${(for (p <- o.patterns) yield printPattern(p), ", ")})"
      case o: AST.Pattern.VarBinding => o.tipeOpt match {
        case Some(t) => st"${o.id.value}: ${printType(t)}"
        case _ => st"${o.id.value}"
      }
      case o: AST.Pattern.Wildcard => o.typeOpt match {
        case Some(t) => st"_: ${printType(t)}"
        case _ => st"_"
      }
    }
    @strictpure def printCase(isExp: B, cas: AST.Case): ST = {
      val cond: ST = cas.condOpt match {
        case Some(cond) => st" if ${printExp(cond)}"
        case _ => st""
      }
      st"""case ${printPattern(cas.pattern)}$cond =>
          |  ${(for (stmt <- cas.body.stmts) yield printStmt(isExp, stmt), "\n")}"""
    }
    @strictpure def printStmt(isExp: B, o: AST.Stmt): ST = o match {
      case o: AST.Stmt.Adt => halt(s"TODO: $o")
      case o: AST.Stmt.Assign => st"${printExp(o.lhs)} = ${printAssignExp(o.rhs)}"
      case o: AST.Stmt.Block =>
        st"""${if (isExp) "" else "do "}{
            |  ${(for (stmt <- o.body.stmts) yield printStmt(F, stmt), lineSep)}
            |}"""
      case o: AST.Stmt.DataRefinement => halt(s"TODO: $o")
      case o: AST.Stmt.DeduceSequent => halt(s"TODO: $o")
      case o: AST.Stmt.DeduceSteps => printDeduceSteps(o)
      case o: AST.Stmt.Enum =>
        st"""type @enum ${o.id.value}: {
            |  ${(for (element <- o.elements) yield element.value, "\n")}
            |}"""
      case o: AST.Stmt.Expr => if (!isExp && shouldAddDo(o.exp)) st"do ${printExp(o.exp)}" else printExp(o.exp)
      case o: AST.Stmt.ExtMethod => halt(s"TODO: $o")
      case o: AST.Stmt.Fact =>
        val tparams: ST = printTypeParams(o.typeParams)
        val desc: ST = o.descOpt match {
          case Some(d) =>
            st""" @[Desc(
                |  ${d.prettyST}
                |)]"""
          case _ => st""
        }
        val (params, claims): (ST, ISZ[ST]) = if (o.isFun) {
          val first = o.claims(0).asInstanceOf[AST.Exp.Quant]
          (st"(${(for (p <- first.fun.params) yield st"${p.idOpt.get.value}: ${printType(p.tipeOpt.get)}", ", ")})", ISZ(printAssignExp(first.fun.exp)))
        } else {
          (st"", for (claim <- o.claims) yield printExp(claim))
        }
        st"""def @fact ${o.id.value}$tparams$params$desc = (
            |  ${(claims, ",\n")}
            |)"""
      case o: AST.Stmt.For => halt(s"TODO: $o")
      case o: AST.Stmt.Havoc => halt(s"TODO: $o")
      case o: AST.Stmt.If =>
        st"""if ${printExp(o.cond)} {
            |  ${(for (stmt <- o.thenBody.stmts) yield printStmt(F, stmt), lineSep)}
            |${printIfElse(o.elseBody)}"""
      case o: AST.Stmt.Import => printImport(o)
      case o: AST.Stmt.Induct => halt(s"TODO: $o")
      case o: AST.Stmt.Inv => halt(s"TODO: $o")
      case o: AST.Stmt.JustMethod => halt(s"TODO: $o")
      case o: AST.Stmt.Match =>
        val cases: ISZ[ST] = for (c <- o.cases) yield printCase(isExp, c)
        if (isExp) {
          st"""(${printExp(o.exp)}? {
              |  ${(cases, "\n")}
              |})"""
        } else {
          st"""match ${printExp(o.exp)} {
              |  ${(cases, "\n")}
              |}"""
        }
      case o: AST.Stmt.Method =>
        val tparams: ST = printTypeParams(o.sig.typeParams)
        val params: ST = printParams(o.sig.hasParams, o.sig.params)
        val sig = st"def${printPurity(o.purity)} ${o.sig.id.value}$tparams$params: ${printType(o.sig.returnType)}"
        val header: ST = if (o.contract.isEmpty) sig else
          st"""$sig @[
              |  ${(printMethodContract(o.contract), s",$lineSep")}
              |]"""
        o.bodyOpt match {
          case Some(body) =>
            st"""$header = {
                |  ${(for (stmt <- body.stmts) yield printStmt(F, stmt), lineSep)}
                |}"""
          case _ => header
        }
      case o: AST.Stmt.Object => halt(s"TODO: $o")
      case o: AST.Stmt.Return => st"return${if (o.expOpt.isEmpty) st"" else st" ${printExp(o.expOpt.get)}"}"
      case o: AST.Stmt.RsVal => halt(s"TODO: $o")
      case o: AST.Stmt.Sig => halt(s"TODO: $o")
      case o: AST.Stmt.SpecBlock =>
        st"""do @spec {
            |  ${(for (stmt <- o.block.body.stmts) yield printStmt(F, stmt), "\n")}
            |}"""
      case o: AST.Stmt.SpecLabel => halt(s"TODO: $o")
      case o: AST.Stmt.SpecMethod =>
        val tparams: ST = printTypeParams(o.sig.typeParams)
        val params: ST = printParams(o.sig.hasParams, o.sig.params)
        st"def @spec ${o.sig.id.value}$tparams$params: ${printType(o.sig.returnType)}"
      case o: AST.Stmt.SpecVar => halt(s"TODO: $o")
      case o: AST.Stmt.SubZ => halt(s"TODO: $o")
      case o: AST.Stmt.Theorem =>
        val tparams: ST = printTypeParams(o.typeParams)
        val desc: ST = o.descOpt match {
          case Some(d) =>
            st""" @[Desc(
                |  ${d.prettyST}
                |)]"""
          case _ => st""
        }
        val (params, claim): (ST, ST) = if (o.isFun) {
          val first = o.claim.asInstanceOf[AST.Exp.Quant]
          (st"(${(for (p <- first.fun.params) yield st"${p.idOpt.get.value}: ${printType(p.tipeOpt.get)}", ", ")})", printAssignExp(first.fun.exp))
        } else {
          (st"", printExp(o.claim))
        }
        st"""def ${if (o.isLemma) "@lemma" else "@theorem"} ${o.id.value}$tparams$params$desc =
            |  $claim"""
      case o: AST.Stmt.TypeAlias =>
        val tparams: ST = printTypeParams(o.typeParams)
        st"type @alias ${o.id.value}$tparams = ${printType(o.tipe)}"
      case o: AST.Stmt.Var => st"${if (o.isVal) "val" else "var"}${if (o.isSpec) " @spec" else ""} ${o.id.value}${if (o.tipeOpt.isEmpty) st"" else st": ${printType(o.tipeOpt.get)}"}${if (o.initOpt.isEmpty) "" else st" = ${printAssignExp(o.initOpt.get)}"}"
      case o: AST.Stmt.VarPattern =>
        val tipe: ST = o.tipeOpt match {
          case Some(t) => st": ${printType(t)}"
          case _ => st""
        }
        st"var ${printPattern(o.pattern)}$tipe = ${printAssignExp(o.init)}"
      case o: AST.Stmt.While =>
        if (o.contract.isEmpty) {
          st"""while ${printExp(o.cond)} {
              |  ${(for (stmt <- o.body.stmts) yield printStmt(F, stmt), lineSep)}
              |}"""
        } else {
          val modifies: ISZ[ST] = for (m <- o.contract.modifies) yield printExp(m.asExp)
          val contracts: ISZ[ST] = (if (modifies.isEmpty) ISZ[ST]() else ISZ(st"Modifies(${(modifies, ", ")})")) ++
            (for (inv <- o.contract.invariants) yield printExp(inv))
          st"""while ${printExp(o.cond)} @[
              |  ${(contracts, s",$lineSep")}
              |] {
              |  ${(for (stmt <- o.body.stmts) yield printStmt(F, stmt), lineSep)}
              |}"""
        }
    }
    val packageOpt: Option[ST] = if (ast.packageName.ids.isEmpty) None() else Some(st"package ${printName(ast.packageName)}")
    val r =
      st"""$packageOpt
          |
          |${(for (stmt <- ast.body.stmts) yield printStmt(F, stmt), lineSep)}"""

    return r
  }

}