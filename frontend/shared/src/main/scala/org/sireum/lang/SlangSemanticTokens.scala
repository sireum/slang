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
import org.sireum.lang.{ast => AST}
import org.sireum.lang.tipe.TypeHierarchy
import org.sireum.message.Position

object SlangSemanticTokens {

  @enum object TokenType {
    "Variable"
    "Property"
    "Function"
    "Method"
    "Type"
    "Namespace"
    "Enum"
    "EnumMember"
  }

  @enum object TokenModifier {
    "Declaration"
    "Readonly"
    "Static"
    "DefaultLibrary"
  }

  @datatype class SemanticToken(
    val line: Z,
    val column: Z,
    val length: Z,
    val tokenType: TokenType.Type,
    val modifiers: ISZ[TokenModifier.Type]
  )

  @record class Collector(var tokens: ISZ[SemanticToken]) extends AST.MTransformer {

    def addToken(posOpt: Option[Position],
                 defPosOpt: Option[Position],
                 tt: TokenType.Type,
                 baseMods: ISZ[TokenModifier.Type]): Unit = {
      posOpt match {
        case Some(pos) =>
          val mods: ISZ[TokenModifier.Type] =
            if (posOpt == defPosOpt) baseMods :+ TokenModifier.Declaration
            else baseMods
          tokens = tokens :+ SemanticToken(pos.beginLine, pos.beginColumn, pos.length, tt, mods)
        case _ =>
      }
    }

    def addTokenFromResolved(posOpt: Option[Position], resOpt: Option[AST.ResolvedInfo]): Unit = {
      resOpt match {
        case Some(res) =>
          res match {
            case res: AST.ResolvedInfo.LocalVar =>
              val mods: ISZ[TokenModifier.Type] = if (res.isVal) ISZ(TokenModifier.Readonly) else ISZ()
              addToken(posOpt, res.defPosOpt, TokenType.Variable, mods)
            case res: AST.ResolvedInfo.Var =>
              val baseMods: ISZ[TokenModifier.Type] =
                if (res.isInObject && res.isVal) ISZ(TokenModifier.Static, TokenModifier.Readonly)
                else if (res.isInObject) ISZ(TokenModifier.Static)
                else ISZ()
              addToken(posOpt, res.defPosOpt, TokenType.Property, baseMods)
            case res: AST.ResolvedInfo.Method =>
              val tt: TokenType.Type = if (res.isInObject) TokenType.Function else TokenType.Method
              val baseMods: ISZ[TokenModifier.Type] =
                if (res.isInObject) ISZ(TokenModifier.Static) else ISZ()
              addToken(posOpt, res.defPosOpt, tt, baseMods)
            case res: AST.ResolvedInfo.BuiltIn =>
              addToken(posOpt, res.defPosOpt, TokenType.Function, ISZ(TokenModifier.DefaultLibrary))
            case res: AST.ResolvedInfo.Object =>
              addToken(posOpt, res.defPosOpt, TokenType.Namespace, ISZ())
            case res: AST.ResolvedInfo.Package =>
              addToken(posOpt, res.defPosOpt, TokenType.Namespace, ISZ())
            case res: AST.ResolvedInfo.Enum =>
              addToken(posOpt, res.defPosOpt, TokenType.Enum, ISZ())
            case res: AST.ResolvedInfo.EnumElement =>
              addToken(posOpt, res.defPosOpt, TokenType.EnumMember, ISZ())
            case res: AST.ResolvedInfo.Fact =>
              addToken(posOpt, res.defPosOpt, TokenType.Function, ISZ(TokenModifier.Readonly))
            case res: AST.ResolvedInfo.Theorem =>
              addToken(posOpt, res.defPosOpt, TokenType.Function, ISZ(TokenModifier.Readonly))
            case res: AST.ResolvedInfo.Inv =>
              addToken(posOpt, res.defPosOpt, TokenType.Function, ISZ(TokenModifier.Readonly))
            case _ =>
          }
        case _ =>
      }
    }

    override def postExpIdent(o: AST.Exp.Ident): MOption[AST.Exp] = {
      addTokenFromResolved(o.id.attr.posOpt, o.attr.resOpt)
      return AST.MTransformer.PostResultExpIdent
    }

    override def postExpSelect(o: AST.Exp.Select): MOption[AST.Exp] = {
      addTokenFromResolved(o.id.attr.posOpt, o.attr.resOpt)
      return AST.MTransformer.PostResultExpSelect
    }

    override def postTypeNamed(o: AST.Type.Named): MOption[AST.Type] = {
      // Emit a Type token for the last id in the qualified name
      if (o.name.ids.nonEmpty) {
        val lastId = o.name.ids(o.name.ids.size - 1)
        addToken(lastId.attr.posOpt, None(), TokenType.Type, ISZ())
      }
      return AST.MTransformer.PostResultTypeNamed
    }

    override def postPatternRef(o: AST.Pattern.Ref): MOption[AST.Pattern] = {
      addTokenFromResolved(o.attr.posOpt, o.attr.resOpt)
      return AST.MTransformer.PostResultPatternRef
    }

    override def postPatternVarBinding(o: AST.Pattern.VarBinding): MOption[AST.Pattern] = {
      // Pattern variable bindings are always val-bound declarations
      addToken(o.id.attr.posOpt, None(), TokenType.Variable,
        ISZ(TokenModifier.Declaration, TokenModifier.Readonly))
      return AST.MTransformer.PostResultPatternVarBinding
    }

    override def postStmtVar(o: AST.Stmt.Var): MOption[AST.Stmt] = {
      val mods: ISZ[TokenModifier.Type] =
        if (o.isVal) ISZ(TokenModifier.Declaration, TokenModifier.Readonly)
        else ISZ(TokenModifier.Declaration)
      addToken(o.id.attr.posOpt, None(), TokenType.Variable, mods)
      return AST.MTransformer.PostResultStmtVar
    }

    override def postStmtSpecVar(o: AST.Stmt.SpecVar): MOption[AST.Stmt.Spec] = {
      val mods: ISZ[TokenModifier.Type] =
        if (o.isVal) ISZ(TokenModifier.Declaration, TokenModifier.Readonly)
        else ISZ(TokenModifier.Declaration)
      addToken(o.id.attr.posOpt, None(), TokenType.Variable, mods)
      return AST.MTransformer.PostResultStmtSpecVar
    }

    override def postStmtMethod(o: AST.Stmt.Method): MOption[AST.Stmt] = {
      addToken(o.sig.id.attr.posOpt, None(), TokenType.Function, ISZ(TokenModifier.Declaration))
      return AST.MTransformer.PostResultStmtMethod
    }

    override def postStmtExtMethod(o: AST.Stmt.ExtMethod): MOption[AST.Stmt] = {
      addToken(o.sig.id.attr.posOpt, None(), TokenType.Function, ISZ(TokenModifier.Declaration))
      return AST.MTransformer.PostResultStmtExtMethod
    }

    override def postStmtJustMethod(o: AST.Stmt.JustMethod): MOption[AST.Stmt.Spec] = {
      addToken(o.sig.id.attr.posOpt, None(), TokenType.Function, ISZ(TokenModifier.Declaration))
      return AST.MTransformer.PostResultStmtJustMethod
    }

    override def postStmtSpecMethod(o: AST.Stmt.SpecMethod): MOption[AST.Stmt.Spec] = {
      addToken(o.sig.id.attr.posOpt, None(), TokenType.Function, ISZ(TokenModifier.Declaration))
      return AST.MTransformer.PostResultStmtSpecMethod
    }

    override def postStmtEnum(o: AST.Stmt.Enum): MOption[AST.Stmt] = {
      addToken(o.id.attr.posOpt, None(), TokenType.Enum, ISZ(TokenModifier.Declaration))
      for (elem <- o.elements) {
        addToken(elem.attr.posOpt, None(), TokenType.EnumMember, ISZ(TokenModifier.Declaration))
      }
      return AST.MTransformer.PostResultStmtEnum
    }

    override def postStmtObject(o: AST.Stmt.Object): MOption[AST.Stmt] = {
      addToken(o.id.attr.posOpt, None(), TokenType.Namespace, ISZ(TokenModifier.Declaration))
      return AST.MTransformer.PostResultStmtObject
    }

    override def postStmtSig(o: AST.Stmt.Sig): MOption[AST.Stmt] = {
      addToken(o.id.attr.posOpt, None(), TokenType.Type, ISZ(TokenModifier.Declaration))
      return AST.MTransformer.PostResultStmtSig
    }

    override def postStmtAdt(o: AST.Stmt.Adt): MOption[AST.Stmt] = {
      addToken(o.id.attr.posOpt, None(), TokenType.Type, ISZ(TokenModifier.Declaration))
      return AST.MTransformer.PostResultStmtAdt
    }

    override def postStmtSubZ(o: AST.Stmt.SubZ): MOption[AST.Stmt] = {
      addToken(o.id.attr.posOpt, None(), TokenType.Type, ISZ(TokenModifier.Declaration))
      return AST.MTransformer.PostResultStmtSubZ
    }

    override def postStmtTypeAlias(o: AST.Stmt.TypeAlias): MOption[AST.Stmt] = {
      addToken(o.id.attr.posOpt, None(), TokenType.Type, ISZ(TokenModifier.Declaration))
      return AST.MTransformer.PostResultStmtTypeAlias
    }

    override def postStmtFact(o: AST.Stmt.Fact): MOption[AST.Stmt.Spec] = {
      addToken(o.id.attr.posOpt, None(), TokenType.Function,
        ISZ(TokenModifier.Declaration, TokenModifier.Readonly))
      return AST.MTransformer.PostResultStmtFact
    }

    override def postStmtInv(o: AST.Stmt.Inv): MOption[AST.Stmt.Spec] = {
      addToken(o.id.attr.posOpt, None(), TokenType.Function,
        ISZ(TokenModifier.Declaration, TokenModifier.Readonly))
      return AST.MTransformer.PostResultStmtInv
    }

    override def postStmtTheorem(o: AST.Stmt.Theorem): MOption[AST.Stmt.Spec] = {
      addToken(o.id.attr.posOpt, None(), TokenType.Function,
        ISZ(TokenModifier.Declaration, TokenModifier.Readonly))
      return AST.MTransformer.PostResultStmtTheorem
    }

    override def postAdtParam(o: AST.AdtParam): MOption[AST.AdtParam] = {
      val mods: ISZ[TokenModifier.Type] =
        if (o.isVal) ISZ(TokenModifier.Declaration, TokenModifier.Readonly)
        else ISZ(TokenModifier.Declaration)
      addToken(o.id.attr.posOpt, None(), TokenType.Property, mods)
      return AST.MTransformer.PostResultAdtParam
    }

  }

  def compute(program: AST.TopUnit.Program, th: TypeHierarchy): ISZ[SemanticToken] = {
    val collector = Collector(ISZ())
    collector.transformTopUnit(program)
    val sorted = ops.ISZOps(collector.tokens).sortWith(
      (a: SemanticToken, b: SemanticToken) =>
        if (a.line != b.line) a.line < b.line
        else a.column <= b.column
    )
    return sorted
  }

}
