/*
 Copyright (c) 2017-2023, Robby, Kansas State University
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
import org.sireum.lang.symbol.{Info, TypeInfo}

object TypeHierarchy_Ext {
  def fingerprint(th: TypeHierarchy): U64 = {
    var sts = ISZ[ST]()
    for (p <- th.nameMap.entries) {
      p._2 match {
        case info: Info.Enum => sts = sts :+ st"Enum(${(info.name, ".")}, ${info.elements.keys})"
        case info: Info.Inv => sts = sts :+ st"Inv(${(info.owner, ".")}, ${info.ast.prettyST})"
        case info: Info.Var => sts = sts :+ st"Var(${(info.owner, ".")}, ${info.ast(initOpt = None()).prettyST})"
        case info: Info.SpecVar => sts = sts :+ st"SpecVar(${(info.owner, ".")}, ${info.ast.prettyST})"
        case info: Info.Method => sts = sts :+ st"Method(${(info.owner, ".")}, ${info.ast(bodyOpt = None()).prettyST})"
        case info: Info.ExtMethod => sts = sts :+ st"ExtMethod(${(info.owner, ".")}, ${info.ast.prettyST})"
        case info: Info.Fact => sts = sts :+ st"Fact(${(info.owner, ".")}, ${info.ast.prettyST})"
        case info: Info.EnumElement => sts = sts :+ st"EnumElement(${(info.owner, ".")}, ${info.id})"
        case info: Info.JustMethod => sts = sts :+ st"JustMethod(${(info.owner, ".")}, ${info.ast.prettyST})"
        case info: Info.LocalVar => sts = sts :+ st"LocalVar(${(info.name, ".")}, ${info.ast.prettyST})"
        case info: Info.SpecMethod => sts = sts :+ st"SpecMethod(${(info.owner, ".")}, ${info.ast.prettyST})"
        case info: Info.Theorem => sts = sts :+ st"Theorem(${(info.owner, ".")}, ${info.ast.prettyST})"
        case _: Info.Package => // skip
        case _: Info.Object => // skip
      }
    }
    for (p <- th.typeMap.entries) {
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
          val refinementST: ST = if (info.refinements.isEmpty ) st"Refinements()" else
            st"""Refinements(
                |  ${(for (r <- info.refinements.values) yield st"${(r.ids, ".")}", ", ")})"""
          val dataRefinementsST: ST =  if (info.dataRefinements.isEmpty) st"DataRefinements()" else
            st"""DataRefinements(
                |  ${(for (r <- info.dataRefinements) yield r.prettyST, ", ")})"""
          sts = sts :+ st"Adt(${(info.owner, ".")}, $rd $root ${info.ast.id.prettyST}${lang.ast.TypeParam.stOpt(info.ast.typeParams)}$param$extend, $varsST, $methodsST, $specVarsST, $specMethodsST, $invsST, $refinementST, $dataRefinementsST)"
        case info: TypeInfo.Sig =>
          val ext: String = if (info.ast.isExt) "@ext " else ""
          val sig: String = if (info.ast.isImmutable) "@sig " else "@msig "
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
          sts = sts :+ st"Sig(${(info.owner, ".")}, $ext$sig trait ${info.ast.id.prettyST}${lang.ast.TypeParam.stOpt(info.ast.typeParams)}$extend, $methodsST, $specVarsST, $specMethodsST, $invsST, $refinementST, $dataRefinementsST)"
        case info: TypeInfo.SubZ => sts = sts :+ st"SubZ(${(info.owner, ".")}, ${info.ast.prettyST})"
        case info: TypeInfo.Enum => sts = sts :+ st"TEnum(${(info.name, ".")}, ${info.elements.keys})"
        case info: TypeInfo.TypeVar => sts = sts :+ st"TypeVar(${info.ast.prettyST})"
        case _: TypeInfo.TypeAlias => // skip
      }
    }

    val rep =
      st"""TypeHierarchy(
          |  ${(sts, ",\n")}
          |)""".render

    val md = _root_.java.security.MessageDigest.getInstance("SHA3-256")
    val digest = md.digest(rep.value.getBytes())
    U64((digest(0).toLong & 0xFFL) << 56 | (digest(1).toLong & 0xFFL) << 48 | (digest(2).toLong & 0xFFL) << 40 |
      (digest(3).toLong & 0xFFL) << 32 | (digest(4).toLong & 0xFFL) << 24 | (digest(5).toLong & 0xFFL) << 16 |
      (digest(6).toLong & 0xFFL) << 8 | (digest(7).toLong & 0xFFL))
  }
}
