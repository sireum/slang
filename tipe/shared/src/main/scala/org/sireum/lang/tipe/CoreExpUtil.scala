// #Sireum
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
package org.sireum.lang.tipe

import org.sireum._
import org.sireum.lang.{ast => AST}

object CoreExpUtil {
  type FunStack = Stack[(String, AST.Typed)]

  @pure def translate(th: TypeHierarchy, exp: AST.Exp): AST.CoreExp = {
    @pure def recAssignExp(ae: AST.AssignExp, funStack: FunStack): AST.CoreExp = {
      ae match {
        case ae: AST.Stmt.Expr => return rec(ae.exp, funStack)
        case ae => halt(s"TODO: $ae")
      }
    }
    @pure def rec(e: AST.Exp, funStack: FunStack): AST.CoreExp = {
      e match {
        case e: AST.Exp.LitB => return AST.CoreExp.LitB(e.value)
        case e: AST.Exp.LitZ => return AST.CoreExp.LitZ(e.value)
        case e: AST.Exp.LitC => return AST.CoreExp.LitC(e.value)
        case e: AST.Exp.LitString => return AST.CoreExp.LitString(e.value)
        case e: AST.Exp.LitR => return AST.CoreExp.LitR(e.value)
        case e: AST.Exp.LitF32 => return AST.CoreExp.LitF32(e.value)
        case e: AST.Exp.LitF64 => return AST.CoreExp.LitF64(e.value)
        case e: AST.Exp.StringInterpolate =>
          e.typedOpt match {
            case Some(t: AST.Typed.Name) =>
              th.typeMap.get(t.ids).get match {
                case ti: lang.symbol.TypeInfo.SubZ =>
                  if (ti.ast.isBitVector) {
                    return AST.CoreExp.LitBits(e.lits(0).value, t)
                  } else {
                    return AST.CoreExp.LitRange(Z(e.lits(0).value).get, t)
                  }
                case _ => halt(s"TODO: $e")
              }
            case _ => halt(s"Infeasible: expected typed expression")
          }
        case e: AST.Exp.Tuple =>
          if (e.args.size == 1) {
            return rec(e.args(0), funStack)
          } else {
            return AST.CoreExp.Tuple(for (arg <- e.args) yield rec(arg, funStack))
          }
        case e: AST.Exp.Ident =>
          e.resOpt.get match {
            case res: AST.ResolvedInfo.LocalVar =>
              val id = res.id
              val stackSize = funStack.size
              for (i <- stackSize - 1 to 0 by -1) {
                val p = funStack.elements(i)
                if (p._1 == id) {
                  return AST.CoreExp.ParamVarRef(stackSize - i, id, p._2)
                }
              }
              return AST.CoreExp.LocalVarRef(id, e.typedOpt.get)
            case res: AST.ResolvedInfo.Var if res.isInObject =>
              return AST.CoreExp.ObjectVarRef(res.owner, res.id, e.typedOpt.get)
            case _ => halt(s"TODO: $e")
          }
        case e: AST.Exp.Unary =>
          val t = e.typedOpt.get
          return AST.CoreExp.Apply(AST.CoreExp.ObjectVarRef(AST.CoreExp.unaryOwner, e.opString, t),
            ISZ(rec(e.exp, funStack)), t)
        case e: AST.Exp.Binary =>
          val t = e.typedOpt.get
          e.attr.resOpt.get match {
            case _: AST.ResolvedInfo.BuiltIn =>
              return AST.CoreExp.Apply(AST.CoreExp.ObjectVarRef(AST.CoreExp.binaryOwner, e.op, t),
                ISZ(rec(e.left, funStack), rec(e.right, funStack)), t)
            case _ => halt(s"TODO: $e")
          }
        case e: AST.Exp.Fun =>
          val params: ISZ[(String, AST.Typed)] = for (p <- e.params) yield
            (p.idOpt.get.value, p.typedOpt.get)
          var stack = funStack
          for (p <- params) {
            stack = stack.push(p)
          }
          val last = params(params.size - 1)
          var r = AST.CoreExp.Fun(AST.CoreExp.Param(last._1, last._2), recAssignExp(e.exp, stack))
          for (i <- params.size - 2 to 0 by -1) {
            val p = params(i)
            r = AST.CoreExp.Fun(AST.CoreExp.Param(p._1, p._2), r)
          }
          return r
        case e: AST.Exp.Quant =>
          val kind: AST.CoreExp.Quant.Kind.Type =
            if (e.isForall) AST.CoreExp.Quant.Kind.ForAll
            else AST.CoreExp.Quant.Kind.Exists
          val params: ISZ[(String, AST.Typed)] = for (i <- 0 until e.fun.params.size) yield
            (e.fun.params(i).idOpt.get.value, e.fun.params(i).typedOpt.get)
          var stack = funStack
          for (p <- params) {
            stack = stack.push(p)
          }
          val last = params(params.size - 1)
          var r = AST.CoreExp.Quant(kind, AST.CoreExp.Param(last._1, last._2),
            recAssignExp(e.fun.exp, stack))
          for (i <- params.size - 2 to 0 by -1) {
            val p = params(i)
            r = AST.CoreExp.Quant(kind, AST.CoreExp.Param(p._1, p._2), r)
          }
          return r
        case e => halt(s"TODO: $e")
      }
    }
    return rec(exp, Stack.empty)
  }
}
