// #Sireum
// @formatter:off

/*
 Copyright (c) 2017-2024, Robby, Kansas State University
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

// This file is auto-generated from CoreExp.scala

// This file is auto-generated from Typed.scala

package org.sireum.lang.ast

import org.sireum._

object CoreExpTransformer {

  @datatype class PreResult[Context, T](val ctx: Context,
                                        val continu: B,
                                        val resultOpt: Option[T])

  @datatype class TPostResult[Context, T](val ctx: Context,
                                          val resultOpt: Option[T])

  @sig trait PrePost[Context] {

    @pure def preTyped(ctx: Context, o: Typed): PreResult[Context, Typed] = {
      o match {
        case o: Typed.Name => return preTypedName(ctx, o)
        case o: Typed.Tuple => return preTypedTuple(ctx, o)
        case o: Typed.Fun => return preTypedFun(ctx, o)
        case o: Typed.TypeVar => return preTypedTypeVar(ctx, o)
        case o: Typed.Package => return preTypedPackage(ctx, o)
        case o: Typed.Object => return preTypedObject(ctx, o)
        case o: Typed.Enum => return preTypedEnum(ctx, o)
        case o: Typed.Method => return preTypedMethod(ctx, o)
        case o: Typed.Methods => return preTypedMethods(ctx, o)
        case o: Typed.Fact => return preTypedFact(ctx, o)
        case o: Typed.Theorem => return preTypedTheorem(ctx, o)
        case o: Typed.Inv => return preTypedInv(ctx, o)
      }
    }

    @pure def preTypedName(ctx: Context, o: Typed.Name): PreResult[Context, Typed] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTypedTuple(ctx: Context, o: Typed.Tuple): PreResult[Context, Typed] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTypedFun(ctx: Context, o: Typed.Fun): PreResult[Context, Typed] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTypedTypeVar(ctx: Context, o: Typed.TypeVar): PreResult[Context, Typed] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTypedPackage(ctx: Context, o: Typed.Package): PreResult[Context, Typed] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTypedObject(ctx: Context, o: Typed.Object): PreResult[Context, Typed] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTypedEnum(ctx: Context, o: Typed.Enum): PreResult[Context, Typed] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTypedMethod(ctx: Context, o: Typed.Method): PreResult[Context, Typed] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTypedMethods(ctx: Context, o: Typed.Methods): PreResult[Context, Typed] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTypedFact(ctx: Context, o: Typed.Fact): PreResult[Context, Typed] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTypedTheorem(ctx: Context, o: Typed.Theorem): PreResult[Context, Typed] = {
      return PreResult(ctx, T, None())
    }

    @pure def preTypedInv(ctx: Context, o: Typed.Inv): PreResult[Context, Typed] = {
      return PreResult(ctx, T, None())
    }

    @pure def preCoreExp(ctx: Context, o: CoreExp): PreResult[Context, CoreExp] = {
      o match {
        case o: CoreExp.LitB =>
          val r: PreResult[Context, CoreExp] = preCoreExpLitB(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: CoreExp)) => PreResult(preCtx, continu, Some[CoreExp](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type CoreExp")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[CoreExp]())
          }
          return r
        case o: CoreExp.LitC =>
          val r: PreResult[Context, CoreExp] = preCoreExpLitC(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: CoreExp)) => PreResult(preCtx, continu, Some[CoreExp](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type CoreExp")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[CoreExp]())
          }
          return r
        case o: CoreExp.LitZ =>
          val r: PreResult[Context, CoreExp] = preCoreExpLitZ(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: CoreExp)) => PreResult(preCtx, continu, Some[CoreExp](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type CoreExp")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[CoreExp]())
          }
          return r
        case o: CoreExp.LitF32 =>
          val r: PreResult[Context, CoreExp] = preCoreExpLitF32(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: CoreExp)) => PreResult(preCtx, continu, Some[CoreExp](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type CoreExp")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[CoreExp]())
          }
          return r
        case o: CoreExp.LitF64 =>
          val r: PreResult[Context, CoreExp] = preCoreExpLitF64(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: CoreExp)) => PreResult(preCtx, continu, Some[CoreExp](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type CoreExp")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[CoreExp]())
          }
          return r
        case o: CoreExp.LitR =>
          val r: PreResult[Context, CoreExp] = preCoreExpLitR(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: CoreExp)) => PreResult(preCtx, continu, Some[CoreExp](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type CoreExp")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[CoreExp]())
          }
          return r
        case o: CoreExp.LitString =>
          val r: PreResult[Context, CoreExp] = preCoreExpLitString(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: CoreExp)) => PreResult(preCtx, continu, Some[CoreExp](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type CoreExp")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[CoreExp]())
          }
          return r
        case o: CoreExp.LitRange =>
          val r: PreResult[Context, CoreExp] = preCoreExpLitRange(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: CoreExp)) => PreResult(preCtx, continu, Some[CoreExp](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type CoreExp")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[CoreExp]())
          }
          return r
        case o: CoreExp.LitBits =>
          val r: PreResult[Context, CoreExp] = preCoreExpLitBits(ctx, o) match {
           case PreResult(preCtx, continu, Some(r: CoreExp)) => PreResult(preCtx, continu, Some[CoreExp](r))
           case PreResult(_, _, Some(_)) => halt("Can only produce object of type CoreExp")
           case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[CoreExp]())
          }
          return r
        case o: CoreExp.ParamVarRef => return preCoreExpParamVarRef(ctx, o)
        case o: CoreExp.LocalVarRef => return preCoreExpLocalVarRef(ctx, o)
        case o: CoreExp.ObjectVarRef => return preCoreExpObjectVarRef(ctx, o)
        case o: CoreExp.Binary => return preCoreExpBinary(ctx, o)
        case o: CoreExp.Unary => return preCoreExpUnary(ctx, o)
        case o: CoreExp.Select => return preCoreExpSelect(ctx, o)
        case o: CoreExp.Update => return preCoreExpUpdate(ctx, o)
        case o: CoreExp.Indexing => return preCoreExpIndexing(ctx, o)
        case o: CoreExp.IndexingUpdate => return preCoreExpIndexingUpdate(ctx, o)
        case o: CoreExp.If => return preCoreExpIf(ctx, o)
        case o: CoreExp.Tuple => return preCoreExpTuple(ctx, o)
        case o: CoreExp.Apply => return preCoreExpApply(ctx, o)
        case o: CoreExp.Fun => return preCoreExpFun(ctx, o)
        case o: CoreExp.Quant => return preCoreExpQuant(ctx, o)
        case o: CoreExp.InstanceOfExp => return preCoreExpInstanceOfExp(ctx, o)
        case o: CoreExp.Arrow => return preCoreExpArrow(ctx, o)
      }
    }

    @pure def preCoreExpLit(ctx: Context, o: CoreExp.Lit): PreResult[Context, CoreExp.Lit] = {
      o match {
        case o: CoreExp.LitB => return preCoreExpLitB(ctx, o)
        case o: CoreExp.LitC => return preCoreExpLitC(ctx, o)
        case o: CoreExp.LitZ => return preCoreExpLitZ(ctx, o)
        case o: CoreExp.LitF32 => return preCoreExpLitF32(ctx, o)
        case o: CoreExp.LitF64 => return preCoreExpLitF64(ctx, o)
        case o: CoreExp.LitR => return preCoreExpLitR(ctx, o)
        case o: CoreExp.LitString => return preCoreExpLitString(ctx, o)
        case o: CoreExp.LitRange => return preCoreExpLitRange(ctx, o)
        case o: CoreExp.LitBits => return preCoreExpLitBits(ctx, o)
      }
    }

    @pure def preCoreExpLitB(ctx: Context, o: CoreExp.LitB): PreResult[Context, CoreExp.Lit] = {
      return PreResult(ctx, T, None())
    }

    @pure def preCoreExpLitC(ctx: Context, o: CoreExp.LitC): PreResult[Context, CoreExp.Lit] = {
      return PreResult(ctx, T, None())
    }

    @pure def preCoreExpLitZ(ctx: Context, o: CoreExp.LitZ): PreResult[Context, CoreExp.Lit] = {
      return PreResult(ctx, T, None())
    }

    @pure def preCoreExpLitF32(ctx: Context, o: CoreExp.LitF32): PreResult[Context, CoreExp.Lit] = {
      return PreResult(ctx, T, None())
    }

    @pure def preCoreExpLitF64(ctx: Context, o: CoreExp.LitF64): PreResult[Context, CoreExp.Lit] = {
      return PreResult(ctx, T, None())
    }

    @pure def preCoreExpLitR(ctx: Context, o: CoreExp.LitR): PreResult[Context, CoreExp.Lit] = {
      return PreResult(ctx, T, None())
    }

    @pure def preCoreExpLitString(ctx: Context, o: CoreExp.LitString): PreResult[Context, CoreExp.Lit] = {
      return PreResult(ctx, T, None())
    }

    @pure def preCoreExpLitRange(ctx: Context, o: CoreExp.LitRange): PreResult[Context, CoreExp.Lit] = {
      return PreResult(ctx, T, None())
    }

    @pure def preCoreExpLitBits(ctx: Context, o: CoreExp.LitBits): PreResult[Context, CoreExp.Lit] = {
      return PreResult(ctx, T, None())
    }

    @pure def preCoreExpParamVarRef(ctx: Context, o: CoreExp.ParamVarRef): PreResult[Context, CoreExp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preCoreExpLocalVarRef(ctx: Context, o: CoreExp.LocalVarRef): PreResult[Context, CoreExp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preCoreExpObjectVarRef(ctx: Context, o: CoreExp.ObjectVarRef): PreResult[Context, CoreExp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preCoreExpBinary(ctx: Context, o: CoreExp.Binary): PreResult[Context, CoreExp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preCoreExpUnary(ctx: Context, o: CoreExp.Unary): PreResult[Context, CoreExp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preCoreExpSelect(ctx: Context, o: CoreExp.Select): PreResult[Context, CoreExp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preCoreExpUpdate(ctx: Context, o: CoreExp.Update): PreResult[Context, CoreExp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preCoreExpIndexing(ctx: Context, o: CoreExp.Indexing): PreResult[Context, CoreExp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preCoreExpIndexingUpdate(ctx: Context, o: CoreExp.IndexingUpdate): PreResult[Context, CoreExp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preCoreExpIf(ctx: Context, o: CoreExp.If): PreResult[Context, CoreExp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preCoreExpTuple(ctx: Context, o: CoreExp.Tuple): PreResult[Context, CoreExp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preCoreExpApply(ctx: Context, o: CoreExp.Apply): PreResult[Context, CoreExp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preCoreExpFun(ctx: Context, o: CoreExp.Fun): PreResult[Context, CoreExp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preCoreExpQuant(ctx: Context, o: CoreExp.Quant): PreResult[Context, CoreExp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preCoreExpParam(ctx: Context, o: CoreExp.Param): PreResult[Context, CoreExp.Param] = {
      return PreResult(ctx, T, None())
    }

    @pure def preCoreExpInstanceOfExp(ctx: Context, o: CoreExp.InstanceOfExp): PreResult[Context, CoreExp] = {
      return PreResult(ctx, T, None())
    }

    @pure def preCoreExpArrow(ctx: Context, o: CoreExp.Arrow): PreResult[Context, CoreExp] = {
      return PreResult(ctx, T, None())
    }

    @pure def postTyped(ctx: Context, o: Typed): TPostResult[Context, Typed] = {
      o match {
        case o: Typed.Name => return postTypedName(ctx, o)
        case o: Typed.Tuple => return postTypedTuple(ctx, o)
        case o: Typed.Fun => return postTypedFun(ctx, o)
        case o: Typed.TypeVar => return postTypedTypeVar(ctx, o)
        case o: Typed.Package => return postTypedPackage(ctx, o)
        case o: Typed.Object => return postTypedObject(ctx, o)
        case o: Typed.Enum => return postTypedEnum(ctx, o)
        case o: Typed.Method => return postTypedMethod(ctx, o)
        case o: Typed.Methods => return postTypedMethods(ctx, o)
        case o: Typed.Fact => return postTypedFact(ctx, o)
        case o: Typed.Theorem => return postTypedTheorem(ctx, o)
        case o: Typed.Inv => return postTypedInv(ctx, o)
      }
    }

    @pure def postTypedName(ctx: Context, o: Typed.Name): TPostResult[Context, Typed] = {
      return TPostResult(ctx, None())
    }

    @pure def postTypedTuple(ctx: Context, o: Typed.Tuple): TPostResult[Context, Typed] = {
      return TPostResult(ctx, None())
    }

    @pure def postTypedFun(ctx: Context, o: Typed.Fun): TPostResult[Context, Typed] = {
      return TPostResult(ctx, None())
    }

    @pure def postTypedTypeVar(ctx: Context, o: Typed.TypeVar): TPostResult[Context, Typed] = {
      return TPostResult(ctx, None())
    }

    @pure def postTypedPackage(ctx: Context, o: Typed.Package): TPostResult[Context, Typed] = {
      return TPostResult(ctx, None())
    }

    @pure def postTypedObject(ctx: Context, o: Typed.Object): TPostResult[Context, Typed] = {
      return TPostResult(ctx, None())
    }

    @pure def postTypedEnum(ctx: Context, o: Typed.Enum): TPostResult[Context, Typed] = {
      return TPostResult(ctx, None())
    }

    @pure def postTypedMethod(ctx: Context, o: Typed.Method): TPostResult[Context, Typed] = {
      return TPostResult(ctx, None())
    }

    @pure def postTypedMethods(ctx: Context, o: Typed.Methods): TPostResult[Context, Typed] = {
      return TPostResult(ctx, None())
    }

    @pure def postTypedFact(ctx: Context, o: Typed.Fact): TPostResult[Context, Typed] = {
      return TPostResult(ctx, None())
    }

    @pure def postTypedTheorem(ctx: Context, o: Typed.Theorem): TPostResult[Context, Typed] = {
      return TPostResult(ctx, None())
    }

    @pure def postTypedInv(ctx: Context, o: Typed.Inv): TPostResult[Context, Typed] = {
      return TPostResult(ctx, None())
    }

    @pure def postCoreExp(ctx: Context, o: CoreExp): TPostResult[Context, CoreExp] = {
      o match {
        case o: CoreExp.LitB =>
          val r: TPostResult[Context, CoreExp] = postCoreExpLitB(ctx, o) match {
           case TPostResult(postCtx, Some(result: CoreExp)) => TPostResult(postCtx, Some[CoreExp](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type CoreExp")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[CoreExp]())
          }
          return r
        case o: CoreExp.LitC =>
          val r: TPostResult[Context, CoreExp] = postCoreExpLitC(ctx, o) match {
           case TPostResult(postCtx, Some(result: CoreExp)) => TPostResult(postCtx, Some[CoreExp](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type CoreExp")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[CoreExp]())
          }
          return r
        case o: CoreExp.LitZ =>
          val r: TPostResult[Context, CoreExp] = postCoreExpLitZ(ctx, o) match {
           case TPostResult(postCtx, Some(result: CoreExp)) => TPostResult(postCtx, Some[CoreExp](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type CoreExp")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[CoreExp]())
          }
          return r
        case o: CoreExp.LitF32 =>
          val r: TPostResult[Context, CoreExp] = postCoreExpLitF32(ctx, o) match {
           case TPostResult(postCtx, Some(result: CoreExp)) => TPostResult(postCtx, Some[CoreExp](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type CoreExp")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[CoreExp]())
          }
          return r
        case o: CoreExp.LitF64 =>
          val r: TPostResult[Context, CoreExp] = postCoreExpLitF64(ctx, o) match {
           case TPostResult(postCtx, Some(result: CoreExp)) => TPostResult(postCtx, Some[CoreExp](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type CoreExp")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[CoreExp]())
          }
          return r
        case o: CoreExp.LitR =>
          val r: TPostResult[Context, CoreExp] = postCoreExpLitR(ctx, o) match {
           case TPostResult(postCtx, Some(result: CoreExp)) => TPostResult(postCtx, Some[CoreExp](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type CoreExp")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[CoreExp]())
          }
          return r
        case o: CoreExp.LitString =>
          val r: TPostResult[Context, CoreExp] = postCoreExpLitString(ctx, o) match {
           case TPostResult(postCtx, Some(result: CoreExp)) => TPostResult(postCtx, Some[CoreExp](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type CoreExp")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[CoreExp]())
          }
          return r
        case o: CoreExp.LitRange =>
          val r: TPostResult[Context, CoreExp] = postCoreExpLitRange(ctx, o) match {
           case TPostResult(postCtx, Some(result: CoreExp)) => TPostResult(postCtx, Some[CoreExp](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type CoreExp")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[CoreExp]())
          }
          return r
        case o: CoreExp.LitBits =>
          val r: TPostResult[Context, CoreExp] = postCoreExpLitBits(ctx, o) match {
           case TPostResult(postCtx, Some(result: CoreExp)) => TPostResult(postCtx, Some[CoreExp](result))
           case TPostResult(_, Some(_)) => halt("Can only produce object of type CoreExp")
           case TPostResult(postCtx, _) => TPostResult(postCtx, None[CoreExp]())
          }
          return r
        case o: CoreExp.ParamVarRef => return postCoreExpParamVarRef(ctx, o)
        case o: CoreExp.LocalVarRef => return postCoreExpLocalVarRef(ctx, o)
        case o: CoreExp.ObjectVarRef => return postCoreExpObjectVarRef(ctx, o)
        case o: CoreExp.Binary => return postCoreExpBinary(ctx, o)
        case o: CoreExp.Unary => return postCoreExpUnary(ctx, o)
        case o: CoreExp.Select => return postCoreExpSelect(ctx, o)
        case o: CoreExp.Update => return postCoreExpUpdate(ctx, o)
        case o: CoreExp.Indexing => return postCoreExpIndexing(ctx, o)
        case o: CoreExp.IndexingUpdate => return postCoreExpIndexingUpdate(ctx, o)
        case o: CoreExp.If => return postCoreExpIf(ctx, o)
        case o: CoreExp.Tuple => return postCoreExpTuple(ctx, o)
        case o: CoreExp.Apply => return postCoreExpApply(ctx, o)
        case o: CoreExp.Fun => return postCoreExpFun(ctx, o)
        case o: CoreExp.Quant => return postCoreExpQuant(ctx, o)
        case o: CoreExp.InstanceOfExp => return postCoreExpInstanceOfExp(ctx, o)
        case o: CoreExp.Arrow => return postCoreExpArrow(ctx, o)
      }
    }

    @pure def postCoreExpLit(ctx: Context, o: CoreExp.Lit): TPostResult[Context, CoreExp.Lit] = {
      o match {
        case o: CoreExp.LitB => return postCoreExpLitB(ctx, o)
        case o: CoreExp.LitC => return postCoreExpLitC(ctx, o)
        case o: CoreExp.LitZ => return postCoreExpLitZ(ctx, o)
        case o: CoreExp.LitF32 => return postCoreExpLitF32(ctx, o)
        case o: CoreExp.LitF64 => return postCoreExpLitF64(ctx, o)
        case o: CoreExp.LitR => return postCoreExpLitR(ctx, o)
        case o: CoreExp.LitString => return postCoreExpLitString(ctx, o)
        case o: CoreExp.LitRange => return postCoreExpLitRange(ctx, o)
        case o: CoreExp.LitBits => return postCoreExpLitBits(ctx, o)
      }
    }

    @pure def postCoreExpLitB(ctx: Context, o: CoreExp.LitB): TPostResult[Context, CoreExp.Lit] = {
      return TPostResult(ctx, None())
    }

    @pure def postCoreExpLitC(ctx: Context, o: CoreExp.LitC): TPostResult[Context, CoreExp.Lit] = {
      return TPostResult(ctx, None())
    }

    @pure def postCoreExpLitZ(ctx: Context, o: CoreExp.LitZ): TPostResult[Context, CoreExp.Lit] = {
      return TPostResult(ctx, None())
    }

    @pure def postCoreExpLitF32(ctx: Context, o: CoreExp.LitF32): TPostResult[Context, CoreExp.Lit] = {
      return TPostResult(ctx, None())
    }

    @pure def postCoreExpLitF64(ctx: Context, o: CoreExp.LitF64): TPostResult[Context, CoreExp.Lit] = {
      return TPostResult(ctx, None())
    }

    @pure def postCoreExpLitR(ctx: Context, o: CoreExp.LitR): TPostResult[Context, CoreExp.Lit] = {
      return TPostResult(ctx, None())
    }

    @pure def postCoreExpLitString(ctx: Context, o: CoreExp.LitString): TPostResult[Context, CoreExp.Lit] = {
      return TPostResult(ctx, None())
    }

    @pure def postCoreExpLitRange(ctx: Context, o: CoreExp.LitRange): TPostResult[Context, CoreExp.Lit] = {
      return TPostResult(ctx, None())
    }

    @pure def postCoreExpLitBits(ctx: Context, o: CoreExp.LitBits): TPostResult[Context, CoreExp.Lit] = {
      return TPostResult(ctx, None())
    }

    @pure def postCoreExpParamVarRef(ctx: Context, o: CoreExp.ParamVarRef): TPostResult[Context, CoreExp] = {
      return TPostResult(ctx, None())
    }

    @pure def postCoreExpLocalVarRef(ctx: Context, o: CoreExp.LocalVarRef): TPostResult[Context, CoreExp] = {
      return TPostResult(ctx, None())
    }

    @pure def postCoreExpObjectVarRef(ctx: Context, o: CoreExp.ObjectVarRef): TPostResult[Context, CoreExp] = {
      return TPostResult(ctx, None())
    }

    @pure def postCoreExpBinary(ctx: Context, o: CoreExp.Binary): TPostResult[Context, CoreExp] = {
      return TPostResult(ctx, None())
    }

    @pure def postCoreExpUnary(ctx: Context, o: CoreExp.Unary): TPostResult[Context, CoreExp] = {
      return TPostResult(ctx, None())
    }

    @pure def postCoreExpSelect(ctx: Context, o: CoreExp.Select): TPostResult[Context, CoreExp] = {
      return TPostResult(ctx, None())
    }

    @pure def postCoreExpUpdate(ctx: Context, o: CoreExp.Update): TPostResult[Context, CoreExp] = {
      return TPostResult(ctx, None())
    }

    @pure def postCoreExpIndexing(ctx: Context, o: CoreExp.Indexing): TPostResult[Context, CoreExp] = {
      return TPostResult(ctx, None())
    }

    @pure def postCoreExpIndexingUpdate(ctx: Context, o: CoreExp.IndexingUpdate): TPostResult[Context, CoreExp] = {
      return TPostResult(ctx, None())
    }

    @pure def postCoreExpIf(ctx: Context, o: CoreExp.If): TPostResult[Context, CoreExp] = {
      return TPostResult(ctx, None())
    }

    @pure def postCoreExpTuple(ctx: Context, o: CoreExp.Tuple): TPostResult[Context, CoreExp] = {
      return TPostResult(ctx, None())
    }

    @pure def postCoreExpApply(ctx: Context, o: CoreExp.Apply): TPostResult[Context, CoreExp] = {
      return TPostResult(ctx, None())
    }

    @pure def postCoreExpFun(ctx: Context, o: CoreExp.Fun): TPostResult[Context, CoreExp] = {
      return TPostResult(ctx, None())
    }

    @pure def postCoreExpQuant(ctx: Context, o: CoreExp.Quant): TPostResult[Context, CoreExp] = {
      return TPostResult(ctx, None())
    }

    @pure def postCoreExpParam(ctx: Context, o: CoreExp.Param): TPostResult[Context, CoreExp.Param] = {
      return TPostResult(ctx, None())
    }

    @pure def postCoreExpInstanceOfExp(ctx: Context, o: CoreExp.InstanceOfExp): TPostResult[Context, CoreExp] = {
      return TPostResult(ctx, None())
    }

    @pure def postCoreExpArrow(ctx: Context, o: CoreExp.Arrow): TPostResult[Context, CoreExp] = {
      return TPostResult(ctx, None())
    }

  }

  @pure def transformISZ[Context, T](ctx: Context, s: IS[Z, T], f: (Context, T) => TPostResult[Context, T] @pure): TPostResult[Context, IS[Z, T]] = {
    val s2: MS[Z, T] = s.toMS
    var changed: B = F
    var ctxi = ctx
    for (i <- s2.indices) {
      val e: T = s(i)
      val r: TPostResult[Context, T] = f(ctxi, e)
      ctxi = r.ctx
      changed = changed || r.resultOpt.nonEmpty
      s2(i) = r.resultOpt.getOrElse(e)
    }
    if (changed) {
      return TPostResult(ctxi, Some(s2.toIS))
    } else {
      return TPostResult[Context, IS[Z, T]](ctxi, None[IS[Z, T]]())
    }
  }

}

import CoreExpTransformer._

@datatype class CoreExpTransformer[Context](val pp: PrePost[Context]) {

  @pure def transformTyped(ctx: Context, o: Typed): TPostResult[Context, Typed] = {
    val preR: PreResult[Context, Typed] = pp.preTyped(ctx, o)
    val r: TPostResult[Context, Typed] = if (preR.continu) {
      val o2: Typed = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: TPostResult[Context, Typed] = o2 match {
        case o2: Typed.Name =>
          val r0: TPostResult[Context, IS[Z, Typed]] = transformISZ(preR.ctx, o2.args, transformTyped _)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(args = r0.resultOpt.getOrElse(o2.args))))
          else
            TPostResult(r0.ctx, None())
        case o2: Typed.Tuple =>
          val r0: TPostResult[Context, IS[Z, Typed]] = transformISZ(preR.ctx, o2.args, transformTyped _)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(args = r0.resultOpt.getOrElse(o2.args))))
          else
            TPostResult(r0.ctx, None())
        case o2: Typed.Fun =>
          val r0: TPostResult[Context, IS[Z, Typed]] = transformISZ(preR.ctx, o2.args, transformTyped _)
          val r1: TPostResult[Context, Typed] = transformTyped(r0.ctx, o2.ret)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(args = r0.resultOpt.getOrElse(o2.args), ret = r1.resultOpt.getOrElse(o2.ret))))
          else
            TPostResult(r1.ctx, None())
        case o2: Typed.TypeVar =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: Typed.Package =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: Typed.Object =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: Typed.Enum =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: Typed.Method =>
          val r0: TPostResult[Context, Typed.Fun] = transformTypedFun(preR.ctx, o2.tpe)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(tpe = r0.resultOpt.getOrElse(o2.tpe))))
          else
            TPostResult(r0.ctx, None())
        case o2: Typed.Methods =>
          val r0: TPostResult[Context, IS[Z, Typed.Method]] = transformISZ(preR.ctx, o2.methods, transformTypedMethod _)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(methods = r0.resultOpt.getOrElse(o2.methods))))
          else
            TPostResult(r0.ctx, None())
        case o2: Typed.Fact =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: Typed.Theorem =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: Typed.Inv =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: Typed = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Typed] = pp.postTyped(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformCoreExp(ctx: Context, o: CoreExp): TPostResult[Context, CoreExp] = {
    val preR: PreResult[Context, CoreExp] = pp.preCoreExp(ctx, o)
    val r: TPostResult[Context, CoreExp] = if (preR.continu) {
      val o2: CoreExp = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: TPostResult[Context, CoreExp] = o2 match {
        case o2: CoreExp.LitB =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: CoreExp.LitC =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: CoreExp.LitZ =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: CoreExp.LitF32 =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: CoreExp.LitF64 =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: CoreExp.LitR =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: CoreExp.LitString =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: CoreExp.LitRange =>
          val r0: TPostResult[Context, Typed] = transformTyped(preR.ctx, o2.tipe)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(tipe = r0.resultOpt.getOrElse(o2.tipe))))
          else
            TPostResult(r0.ctx, None())
        case o2: CoreExp.LitBits =>
          val r0: TPostResult[Context, Typed] = transformTyped(preR.ctx, o2.tipe)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(tipe = r0.resultOpt.getOrElse(o2.tipe))))
          else
            TPostResult(r0.ctx, None())
        case o2: CoreExp.ParamVarRef =>
          val r0: TPostResult[Context, Typed] = transformTyped(preR.ctx, o2.tipe)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(tipe = r0.resultOpt.getOrElse(o2.tipe))))
          else
            TPostResult(r0.ctx, None())
        case o2: CoreExp.LocalVarRef =>
          val r0: TPostResult[Context, Typed] = transformTyped(preR.ctx, o2.tipe)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(tipe = r0.resultOpt.getOrElse(o2.tipe))))
          else
            TPostResult(r0.ctx, None())
        case o2: CoreExp.ObjectVarRef =>
          val r0: TPostResult[Context, Typed] = transformTyped(preR.ctx, o2.tipe)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(tipe = r0.resultOpt.getOrElse(o2.tipe))))
          else
            TPostResult(r0.ctx, None())
        case o2: CoreExp.Binary =>
          val r0: TPostResult[Context, CoreExp] = transformCoreExp(preR.ctx, o2.left)
          val r1: TPostResult[Context, CoreExp] = transformCoreExp(r0.ctx, o2.right)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(left = r0.resultOpt.getOrElse(o2.left), right = r1.resultOpt.getOrElse(o2.right))))
          else
            TPostResult(r1.ctx, None())
        case o2: CoreExp.Unary =>
          val r0: TPostResult[Context, CoreExp] = transformCoreExp(preR.ctx, o2.exp)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(exp = r0.resultOpt.getOrElse(o2.exp))))
          else
            TPostResult(r0.ctx, None())
        case o2: CoreExp.Select =>
          val r0: TPostResult[Context, CoreExp] = transformCoreExp(preR.ctx, o2.exp)
          val r1: TPostResult[Context, Typed] = transformTyped(r0.ctx, o2.tipe)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(exp = r0.resultOpt.getOrElse(o2.exp), tipe = r1.resultOpt.getOrElse(o2.tipe))))
          else
            TPostResult(r1.ctx, None())
        case o2: CoreExp.Update =>
          val r0: TPostResult[Context, CoreExp] = transformCoreExp(preR.ctx, o2.exp)
          val r1: TPostResult[Context, CoreExp] = transformCoreExp(r0.ctx, o2.arg)
          val r2: TPostResult[Context, Typed] = transformTyped(r1.ctx, o2.tipe)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(exp = r0.resultOpt.getOrElse(o2.exp), arg = r1.resultOpt.getOrElse(o2.arg), tipe = r2.resultOpt.getOrElse(o2.tipe))))
          else
            TPostResult(r2.ctx, None())
        case o2: CoreExp.Indexing =>
          val r0: TPostResult[Context, CoreExp] = transformCoreExp(preR.ctx, o2.exp)
          val r1: TPostResult[Context, CoreExp] = transformCoreExp(r0.ctx, o2.index)
          val r2: TPostResult[Context, Typed] = transformTyped(r1.ctx, o2.tipe)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(exp = r0.resultOpt.getOrElse(o2.exp), index = r1.resultOpt.getOrElse(o2.index), tipe = r2.resultOpt.getOrElse(o2.tipe))))
          else
            TPostResult(r2.ctx, None())
        case o2: CoreExp.IndexingUpdate =>
          val r0: TPostResult[Context, CoreExp] = transformCoreExp(preR.ctx, o2.exp)
          val r1: TPostResult[Context, CoreExp] = transformCoreExp(r0.ctx, o2.index)
          val r2: TPostResult[Context, CoreExp] = transformCoreExp(r1.ctx, o2.arg)
          val r3: TPostResult[Context, Typed] = transformTyped(r2.ctx, o2.tipe)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty || r3.resultOpt.nonEmpty)
            TPostResult(r3.ctx, Some(o2(exp = r0.resultOpt.getOrElse(o2.exp), index = r1.resultOpt.getOrElse(o2.index), arg = r2.resultOpt.getOrElse(o2.arg), tipe = r3.resultOpt.getOrElse(o2.tipe))))
          else
            TPostResult(r3.ctx, None())
        case o2: CoreExp.If =>
          val r0: TPostResult[Context, CoreExp] = transformCoreExp(preR.ctx, o2.cond)
          val r1: TPostResult[Context, CoreExp] = transformCoreExp(r0.ctx, o2.tExp)
          val r2: TPostResult[Context, CoreExp] = transformCoreExp(r1.ctx, o2.fExp)
          val r3: TPostResult[Context, Typed] = transformTyped(r2.ctx, o2.tipe)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty || r3.resultOpt.nonEmpty)
            TPostResult(r3.ctx, Some(o2(cond = r0.resultOpt.getOrElse(o2.cond), tExp = r1.resultOpt.getOrElse(o2.tExp), fExp = r2.resultOpt.getOrElse(o2.fExp), tipe = r3.resultOpt.getOrElse(o2.tipe))))
          else
            TPostResult(r3.ctx, None())
        case o2: CoreExp.Tuple =>
          val r0: TPostResult[Context, IS[Z, CoreExp]] = transformISZ(preR.ctx, o2.args, transformCoreExp _)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(args = r0.resultOpt.getOrElse(o2.args))))
          else
            TPostResult(r0.ctx, None())
        case o2: CoreExp.Apply =>
          val r0: TPostResult[Context, CoreExp] = transformCoreExp(preR.ctx, o2.exp)
          val r1: TPostResult[Context, IS[Z, CoreExp]] = transformISZ(r0.ctx, o2.args, transformCoreExp _)
          val r2: TPostResult[Context, Typed] = transformTyped(r1.ctx, o2.tipe)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty || r2.resultOpt.nonEmpty)
            TPostResult(r2.ctx, Some(o2(exp = r0.resultOpt.getOrElse(o2.exp), args = r1.resultOpt.getOrElse(o2.args), tipe = r2.resultOpt.getOrElse(o2.tipe))))
          else
            TPostResult(r2.ctx, None())
        case o2: CoreExp.Fun =>
          val r0: TPostResult[Context, CoreExp.Param] = transformCoreExpParam(preR.ctx, o2.param)
          val r1: TPostResult[Context, CoreExp] = transformCoreExp(r0.ctx, o2.exp)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(param = r0.resultOpt.getOrElse(o2.param), exp = r1.resultOpt.getOrElse(o2.exp))))
          else
            TPostResult(r1.ctx, None())
        case o2: CoreExp.Quant =>
          val r0: TPostResult[Context, CoreExp.Param] = transformCoreExpParam(preR.ctx, o2.param)
          val r1: TPostResult[Context, CoreExp] = transformCoreExp(r0.ctx, o2.exp)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(param = r0.resultOpt.getOrElse(o2.param), exp = r1.resultOpt.getOrElse(o2.exp))))
          else
            TPostResult(r1.ctx, None())
        case o2: CoreExp.InstanceOfExp =>
          val r0: TPostResult[Context, CoreExp] = transformCoreExp(preR.ctx, o2.exp)
          val r1: TPostResult[Context, Typed] = transformTyped(r0.ctx, o2.tipe)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(exp = r0.resultOpt.getOrElse(o2.exp), tipe = r1.resultOpt.getOrElse(o2.tipe))))
          else
            TPostResult(r1.ctx, None())
        case o2: CoreExp.Arrow =>
          val r0: TPostResult[Context, CoreExp] = transformCoreExp(preR.ctx, o2.left)
          val r1: TPostResult[Context, CoreExp] = transformCoreExp(r0.ctx, o2.right)
          if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
            TPostResult(r1.ctx, Some(o2(left = r0.resultOpt.getOrElse(o2.left), right = r1.resultOpt.getOrElse(o2.right))))
          else
            TPostResult(r1.ctx, None())
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: CoreExp = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, CoreExp] = pp.postCoreExp(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformCoreExpLit(ctx: Context, o: CoreExp.Lit): TPostResult[Context, CoreExp.Lit] = {
    val preR: PreResult[Context, CoreExp.Lit] = pp.preCoreExpLit(ctx, o)
    val r: TPostResult[Context, CoreExp.Lit] = if (preR.continu) {
      val o2: CoreExp.Lit = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: TPostResult[Context, CoreExp.Lit] = o2 match {
        case o2: CoreExp.LitB =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: CoreExp.LitC =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: CoreExp.LitZ =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: CoreExp.LitF32 =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: CoreExp.LitF64 =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: CoreExp.LitR =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: CoreExp.LitString =>
          if (hasChanged)
            TPostResult(preR.ctx, Some(o2))
          else
            TPostResult(preR.ctx, None())
        case o2: CoreExp.LitRange =>
          val r0: TPostResult[Context, Typed] = transformTyped(preR.ctx, o2.tipe)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(tipe = r0.resultOpt.getOrElse(o2.tipe))))
          else
            TPostResult(r0.ctx, None())
        case o2: CoreExp.LitBits =>
          val r0: TPostResult[Context, Typed] = transformTyped(preR.ctx, o2.tipe)
          if (hasChanged || r0.resultOpt.nonEmpty)
            TPostResult(r0.ctx, Some(o2(tipe = r0.resultOpt.getOrElse(o2.tipe))))
          else
            TPostResult(r0.ctx, None())
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: CoreExp.Lit = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, CoreExp.Lit] = pp.postCoreExpLit(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformCoreExpParam(ctx: Context, o: CoreExp.Param): TPostResult[Context, CoreExp.Param] = {
    val preR: PreResult[Context, CoreExp.Param] = pp.preCoreExpParam(ctx, o)
    val r: TPostResult[Context, CoreExp.Param] = if (preR.continu) {
      val o2: CoreExp.Param = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, Typed] = transformTyped(preR.ctx, o2.tipe)
      if (hasChanged || r0.resultOpt.nonEmpty)
        TPostResult(r0.ctx, Some(o2(tipe = r0.resultOpt.getOrElse(o2.tipe))))
      else
        TPostResult(r0.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: CoreExp.Param = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, CoreExp.Param] = pp.postCoreExpParam(r.ctx, o2)
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformTypedFun(ctx: Context, o: Typed.Fun): TPostResult[Context, Typed.Fun] = {
    val preR: PreResult[Context, Typed.Fun] = pp.preTypedFun(ctx, o) match {
     case PreResult(preCtx, continu, Some(r: Typed.Fun)) => PreResult(preCtx, continu, Some[Typed.Fun](r))
     case PreResult(_, _, Some(_)) => halt("Can only produce object of type Typed.Fun")
     case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[Typed.Fun]())
    }
    val r: TPostResult[Context, Typed.Fun] = if (preR.continu) {
      val o2: Typed.Fun = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, IS[Z, Typed]] = transformISZ(preR.ctx, o2.args, transformTyped _)
      val r1: TPostResult[Context, Typed] = transformTyped(r0.ctx, o2.ret)
      if (hasChanged || r0.resultOpt.nonEmpty || r1.resultOpt.nonEmpty)
        TPostResult(r1.ctx, Some(o2(args = r0.resultOpt.getOrElse(o2.args), ret = r1.resultOpt.getOrElse(o2.ret))))
      else
        TPostResult(r1.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: Typed.Fun = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Typed.Fun] = pp.postTypedFun(r.ctx, o2) match {
     case TPostResult(postCtx, Some(result: Typed.Fun)) => TPostResult(postCtx, Some[Typed.Fun](result))
     case TPostResult(_, Some(_)) => halt("Can only produce object of type Typed.Fun")
     case TPostResult(postCtx, _) => TPostResult(postCtx, None[Typed.Fun]())
    }
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

  @pure def transformTypedMethod(ctx: Context, o: Typed.Method): TPostResult[Context, Typed.Method] = {
    val preR: PreResult[Context, Typed.Method] = pp.preTypedMethod(ctx, o) match {
     case PreResult(preCtx, continu, Some(r: Typed.Method)) => PreResult(preCtx, continu, Some[Typed.Method](r))
     case PreResult(_, _, Some(_)) => halt("Can only produce object of type Typed.Method")
     case PreResult(preCtx, continu, _) => PreResult(preCtx, continu, None[Typed.Method]())
    }
    val r: TPostResult[Context, Typed.Method] = if (preR.continu) {
      val o2: Typed.Method = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: TPostResult[Context, Typed.Fun] = transformTypedFun(preR.ctx, o2.tpe)
      if (hasChanged || r0.resultOpt.nonEmpty)
        TPostResult(r0.ctx, Some(o2(tpe = r0.resultOpt.getOrElse(o2.tpe))))
      else
        TPostResult(r0.ctx, None())
    } else if (preR.resultOpt.nonEmpty) {
      TPostResult(preR.ctx, Some(preR.resultOpt.getOrElse(o)))
    } else {
      TPostResult(preR.ctx, None())
    }
    val hasChanged: B = r.resultOpt.nonEmpty
    val o2: Typed.Method = r.resultOpt.getOrElse(o)
    val postR: TPostResult[Context, Typed.Method] = pp.postTypedMethod(r.ctx, o2) match {
     case TPostResult(postCtx, Some(result: Typed.Method)) => TPostResult(postCtx, Some[Typed.Method](result))
     case TPostResult(_, Some(_)) => halt("Can only produce object of type Typed.Method")
     case TPostResult(postCtx, _) => TPostResult(postCtx, None[Typed.Method]())
    }
    if (postR.resultOpt.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return TPostResult(postR.ctx, Some(o2))
    } else {
      return TPostResult(postR.ctx, None())
    }
  }

}