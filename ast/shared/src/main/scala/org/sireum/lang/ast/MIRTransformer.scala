// #Sireum
// @formatter:off

/*
 Copyright (c) 2017-2025, Robby, Kansas State University
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

// This file is auto-generated from IR.scala, Typed.scala

package org.sireum.lang.ast

import org.sireum._

object MIRTransformer {

  @record class PreResult[T](val continu: B,
                             val resultOpt: MOption[T])

  val PreResultIRMethodContext: PreResult[IR.MethodContext] = PreResult(T, MNone())

  val PostResultIRMethodContext: MOption[IR.MethodContext] = MNone()

  def transformISZ[T](s: IS[Z, T], f: T => MOption[T]): MOption[IS[Z, T]] = {
    val s2: MS[Z, T] = s.toMS
    var changed: B = F
    for (i <- s2.indices) {
      val e: T = s(i)
      val r: MOption[T] = f(e)
      changed = changed || r.nonEmpty
      s2(i) = r.getOrElse(e)
    }
    if (changed) {
      return MSome(s2.toIS)
    } else {
      return MNone()
    }
  }

  val PreResultIRExpBool: PreResult[IR.Exp] = PreResult(T, MNone())

  val PostResultIRExpBool: MOption[IR.Exp] = MNone()

  val PreResultIRExpInt: PreResult[IR.Exp] = PreResult(T, MNone())

  val PostResultIRExpInt: MOption[IR.Exp] = MNone()

  val PreResultIRExpF32: PreResult[IR.Exp] = PreResult(T, MNone())

  val PostResultIRExpF32: MOption[IR.Exp] = MNone()

  val PreResultIRExpF64: PreResult[IR.Exp] = PreResult(T, MNone())

  val PostResultIRExpF64: MOption[IR.Exp] = MNone()

  val PreResultIRExpR: PreResult[IR.Exp] = PreResult(T, MNone())

  val PostResultIRExpR: MOption[IR.Exp] = MNone()

  val PreResultIRExpString: PreResult[IR.Exp] = PreResult(T, MNone())

  val PostResultIRExpString: MOption[IR.Exp] = MNone()

  val PreResultIRExpTemp: PreResult[IR.Exp] = PreResult(T, MNone())

  val PostResultIRExpTemp: MOption[IR.Exp] = MNone()

  val PreResultIRExpLocalVarRef: PreResult[IR.Exp] = PreResult(T, MNone())

  val PostResultIRExpLocalVarRef: MOption[IR.Exp] = MNone()

  val PreResultIRExpGlobalVarRef: PreResult[IR.Exp] = PreResult(T, MNone())

  val PostResultIRExpGlobalVarRef: MOption[IR.Exp] = MNone()

  val PreResultIRExpEnumElementRef: PreResult[IR.Exp] = PreResult(T, MNone())

  val PostResultIRExpEnumElementRef: MOption[IR.Exp] = MNone()

  val PreResultIRExpFieldVarRef: PreResult[IR.Exp] = PreResult(T, MNone())

  val PostResultIRExpFieldVarRef: MOption[IR.Exp] = MNone()

  val PreResultIRExpUnary: PreResult[IR.Exp] = PreResult(T, MNone())

  val PostResultIRExpUnary: MOption[IR.Exp] = MNone()

  val PreResultIRExpBinary: PreResult[IR.Exp] = PreResult(T, MNone())

  val PostResultIRExpBinary: MOption[IR.Exp] = MNone()

  val PreResultIRExpIf: PreResult[IR.Exp] = PreResult(T, MNone())

  val PostResultIRExpIf: MOption[IR.Exp] = MNone()

  val PreResultIRExpConstruct: PreResult[IR.Exp] = PreResult(T, MNone())

  val PostResultIRExpConstruct: MOption[IR.Exp] = MNone()

  val PreResultIRExpApply: PreResult[IR.Exp] = PreResult(T, MNone())

  val PostResultIRExpApply: MOption[IR.Exp] = MNone()

  val PreResultIRExpSelect: PreResult[IR.Exp] = PreResult(T, MNone())

  val PostResultIRExpSelect: MOption[IR.Exp] = MNone()

  val PreResultIRExpIndexing: PreResult[IR.Exp] = PreResult(T, MNone())

  val PostResultIRExpIndexing: MOption[IR.Exp] = MNone()

  val PreResultIRExpType: PreResult[IR.Exp] = PreResult(T, MNone())

  val PostResultIRExpType: MOption[IR.Exp] = MNone()

  def transformOption[T](option: Option[T], f: T => MOption[T]): MOption[Option[T]] = {
    option match {
      case Some(v) =>
        val r = f(v)
        r match {
          case MSome(v2) => return MSome(Some(v2))
          case _ => return MNone()
        }
      case _ => return MNone()
    }
  }

  val PreResultIRStmtAssignLocal: PreResult[IR.Stmt.Assign] = PreResult(T, MNone())

  val PostResultIRStmtAssignLocal: MOption[IR.Stmt.Assign] = MNone()

  val PreResultIRStmtAssignGlobal: PreResult[IR.Stmt.Assign] = PreResult(T, MNone())

  val PostResultIRStmtAssignGlobal: MOption[IR.Stmt.Assign] = MNone()

  val PreResultIRStmtAssignTemp: PreResult[IR.Stmt.Assign] = PreResult(T, MNone())

  val PostResultIRStmtAssignTemp: MOption[IR.Stmt.Assign] = MNone()

  val PreResultIRStmtAssignField: PreResult[IR.Stmt.Assign] = PreResult(T, MNone())

  val PostResultIRStmtAssignField: MOption[IR.Stmt.Assign] = MNone()

  val PreResultIRStmtAssignIndex: PreResult[IR.Stmt.Assign] = PreResult(T, MNone())

  val PostResultIRStmtAssignIndex: MOption[IR.Stmt.Assign] = MNone()

  val PreResultIRStmtIf: PreResult[IR.Stmt] = PreResult(T, MNone())

  val PostResultIRStmtIf: MOption[IR.Stmt] = MNone()

  val PreResultIRStmtBlock: PreResult[IR.Stmt] = PreResult(T, MNone())

  val PostResultIRStmtBlock: MOption[IR.Stmt] = MNone()

  val PreResultIRStmtWhile: PreResult[IR.Stmt] = PreResult(T, MNone())

  val PostResultIRStmtWhile: MOption[IR.Stmt] = MNone()

  val PreResultIRStmtReturn: PreResult[IR.Stmt] = PreResult(T, MNone())

  val PostResultIRStmtReturn: MOption[IR.Stmt] = MNone()

  val PreResultIRStmtDeclLocal: PreResult[IR.Stmt.Decl.Ground] = PreResult(T, MNone())

  val PostResultIRStmtDeclLocal: MOption[IR.Stmt.Decl.Ground] = MNone()

  val PreResultIRStmtDeclTemp: PreResult[IR.Stmt.Decl.Ground] = PreResult(T, MNone())

  val PostResultIRStmtDeclTemp: MOption[IR.Stmt.Decl.Ground] = MNone()

  val PreResultIRStmtDeclMultiple: PreResult[IR.Stmt.Decl] = PreResult(T, MNone())

  val PostResultIRStmtDeclMultiple: MOption[IR.Stmt.Decl] = MNone()

  val PreResultIRJumpGoto: PreResult[IR.Jump] = PreResult(T, MNone())

  val PostResultIRJumpGoto: MOption[IR.Jump] = MNone()

  val PreResultIRJumpIf: PreResult[IR.Jump] = PreResult(T, MNone())

  val PostResultIRJumpIf: MOption[IR.Jump] = MNone()

  val PreResultIRJumpReturn: PreResult[IR.Jump] = PreResult(T, MNone())

  val PostResultIRJumpReturn: MOption[IR.Jump] = MNone()

  val PreResultIRBasicBlock: PreResult[IR.BasicBlock] = PreResult(T, MNone())

  val PostResultIRBasicBlock: MOption[IR.BasicBlock] = MNone()

  val PreResultIRBodyBlock: PreResult[IR.Body] = PreResult(T, MNone())

  val PostResultIRBodyBlock: MOption[IR.Body] = MNone()

  val PreResultIRBodyBasic: PreResult[IR.Body] = PreResult(T, MNone())

  val PostResultIRBodyBasic: MOption[IR.Body] = MNone()

  val PreResultIRProcedure: PreResult[IR.Procedure] = PreResult(T, MNone())

  val PostResultIRProcedure: MOption[IR.Procedure] = MNone()

  val PreResultIRGlobal: PreResult[IR.Global] = PreResult(T, MNone())

  val PostResultIRGlobal: MOption[IR.Global] = MNone()

  val PreResultIRProgram: PreResult[IR.Program] = PreResult(T, MNone())

  val PostResultIRProgram: MOption[IR.Program] = MNone()

  val PreResultTypedName: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedName: MOption[Typed] = MNone()

  val PreResultTypedTuple: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedTuple: MOption[Typed] = MNone()

  val PreResultTypedFun: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedFun: MOption[Typed] = MNone()

  val PreResultTypedTypeVar: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedTypeVar: MOption[Typed] = MNone()

  val PreResultTypedPackage: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedPackage: MOption[Typed] = MNone()

  val PreResultTypedObject: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedObject: MOption[Typed] = MNone()

  val PreResultTypedEnum: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedEnum: MOption[Typed] = MNone()

  val PreResultTypedMethod: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedMethod: MOption[Typed] = MNone()

  val PreResultTypedMethods: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedMethods: MOption[Typed] = MNone()

  val PreResultTypedFact: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedFact: MOption[Typed] = MNone()

  val PreResultTypedTheorem: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedTheorem: MOption[Typed] = MNone()

  val PreResultTypedInv: PreResult[Typed] = PreResult(T, MNone())

  val PostResultTypedInv: MOption[Typed] = MNone()

}

import MIRTransformer._

@msig trait MIRTransformer {

  def preIRMethodContext(o: IR.MethodContext): PreResult[IR.MethodContext] = {
    return PreResultIRMethodContext
  }

  def preIRExp(o: IR.Exp): PreResult[IR.Exp] = {
    o match {
      case o: IR.Exp.Bool => return preIRExpBool(o)
      case o: IR.Exp.Int => return preIRExpInt(o)
      case o: IR.Exp.F32 => return preIRExpF32(o)
      case o: IR.Exp.F64 => return preIRExpF64(o)
      case o: IR.Exp.R => return preIRExpR(o)
      case o: IR.Exp.String => return preIRExpString(o)
      case o: IR.Exp.Temp => return preIRExpTemp(o)
      case o: IR.Exp.LocalVarRef => return preIRExpLocalVarRef(o)
      case o: IR.Exp.GlobalVarRef => return preIRExpGlobalVarRef(o)
      case o: IR.Exp.EnumElementRef => return preIRExpEnumElementRef(o)
      case o: IR.Exp.FieldVarRef => return preIRExpFieldVarRef(o)
      case o: IR.Exp.Unary => return preIRExpUnary(o)
      case o: IR.Exp.Binary => return preIRExpBinary(o)
      case o: IR.Exp.If => return preIRExpIf(o)
      case o: IR.Exp.Construct => return preIRExpConstruct(o)
      case o: IR.Exp.Apply => return preIRExpApply(o)
      case o: IR.Exp.Select => return preIRExpSelect(o)
      case o: IR.Exp.Indexing => return preIRExpIndexing(o)
      case o: IR.Exp.Type => return preIRExpType(o)
    }
  }

  def preIRExpBool(o: IR.Exp.Bool): PreResult[IR.Exp] = {
    return PreResultIRExpBool
  }

  def preIRExpInt(o: IR.Exp.Int): PreResult[IR.Exp] = {
    return PreResultIRExpInt
  }

  def preIRExpF32(o: IR.Exp.F32): PreResult[IR.Exp] = {
    return PreResultIRExpF32
  }

  def preIRExpF64(o: IR.Exp.F64): PreResult[IR.Exp] = {
    return PreResultIRExpF64
  }

  def preIRExpR(o: IR.Exp.R): PreResult[IR.Exp] = {
    return PreResultIRExpR
  }

  def preIRExpString(o: IR.Exp.String): PreResult[IR.Exp] = {
    return PreResultIRExpString
  }

  def preIRExpTemp(o: IR.Exp.Temp): PreResult[IR.Exp] = {
    return PreResultIRExpTemp
  }

  def preIRExpLocalVarRef(o: IR.Exp.LocalVarRef): PreResult[IR.Exp] = {
    return PreResultIRExpLocalVarRef
  }

  def preIRExpGlobalVarRef(o: IR.Exp.GlobalVarRef): PreResult[IR.Exp] = {
    return PreResultIRExpGlobalVarRef
  }

  def preIRExpEnumElementRef(o: IR.Exp.EnumElementRef): PreResult[IR.Exp] = {
    return PreResultIRExpEnumElementRef
  }

  def preIRExpFieldVarRef(o: IR.Exp.FieldVarRef): PreResult[IR.Exp] = {
    return PreResultIRExpFieldVarRef
  }

  def preIRExpUnary(o: IR.Exp.Unary): PreResult[IR.Exp] = {
    return PreResultIRExpUnary
  }

  def preIRExpBinary(o: IR.Exp.Binary): PreResult[IR.Exp] = {
    return PreResultIRExpBinary
  }

  def preIRExpIf(o: IR.Exp.If): PreResult[IR.Exp] = {
    return PreResultIRExpIf
  }

  def preIRExpConstruct(o: IR.Exp.Construct): PreResult[IR.Exp] = {
    return PreResultIRExpConstruct
  }

  def preIRExpApply(o: IR.Exp.Apply): PreResult[IR.Exp] = {
    return PreResultIRExpApply
  }

  def preIRExpSelect(o: IR.Exp.Select): PreResult[IR.Exp] = {
    return PreResultIRExpSelect
  }

  def preIRExpIndexing(o: IR.Exp.Indexing): PreResult[IR.Exp] = {
    return PreResultIRExpIndexing
  }

  def preIRExpType(o: IR.Exp.Type): PreResult[IR.Exp] = {
    return PreResultIRExpType
  }

  def preIRStmt(o: IR.Stmt): PreResult[IR.Stmt] = {
    o match {
      case o: IR.Stmt.Assign.Local =>
        val r: PreResult[IR.Stmt] = preIRStmtAssignLocal(o) match {
         case PreResult(continu, MSome(r: IR.Stmt)) => PreResult(continu, MSome[IR.Stmt](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type IR.Stmt")
         case PreResult(continu, _) => PreResult(continu, MNone[IR.Stmt]())
        }
        return r
      case o: IR.Stmt.Assign.Global =>
        val r: PreResult[IR.Stmt] = preIRStmtAssignGlobal(o) match {
         case PreResult(continu, MSome(r: IR.Stmt)) => PreResult(continu, MSome[IR.Stmt](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type IR.Stmt")
         case PreResult(continu, _) => PreResult(continu, MNone[IR.Stmt]())
        }
        return r
      case o: IR.Stmt.Assign.Temp =>
        val r: PreResult[IR.Stmt] = preIRStmtAssignTemp(o) match {
         case PreResult(continu, MSome(r: IR.Stmt)) => PreResult(continu, MSome[IR.Stmt](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type IR.Stmt")
         case PreResult(continu, _) => PreResult(continu, MNone[IR.Stmt]())
        }
        return r
      case o: IR.Stmt.Assign.Field =>
        val r: PreResult[IR.Stmt] = preIRStmtAssignField(o) match {
         case PreResult(continu, MSome(r: IR.Stmt)) => PreResult(continu, MSome[IR.Stmt](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type IR.Stmt")
         case PreResult(continu, _) => PreResult(continu, MNone[IR.Stmt]())
        }
        return r
      case o: IR.Stmt.Assign.Index =>
        val r: PreResult[IR.Stmt] = preIRStmtAssignIndex(o) match {
         case PreResult(continu, MSome(r: IR.Stmt)) => PreResult(continu, MSome[IR.Stmt](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type IR.Stmt")
         case PreResult(continu, _) => PreResult(continu, MNone[IR.Stmt]())
        }
        return r
      case o: IR.Stmt.If => return preIRStmtIf(o)
      case o: IR.Stmt.Block => return preIRStmtBlock(o)
      case o: IR.Stmt.While => return preIRStmtWhile(o)
      case o: IR.Stmt.Return => return preIRStmtReturn(o)
      case o: IR.Stmt.Decl.Local =>
        val r: PreResult[IR.Stmt] = preIRStmtDeclLocal(o) match {
         case PreResult(continu, MSome(r: IR.Stmt)) => PreResult(continu, MSome[IR.Stmt](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type IR.Stmt")
         case PreResult(continu, _) => PreResult(continu, MNone[IR.Stmt]())
        }
        return r
      case o: IR.Stmt.Decl.Temp =>
        val r: PreResult[IR.Stmt] = preIRStmtDeclTemp(o) match {
         case PreResult(continu, MSome(r: IR.Stmt)) => PreResult(continu, MSome[IR.Stmt](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type IR.Stmt")
         case PreResult(continu, _) => PreResult(continu, MNone[IR.Stmt]())
        }
        return r
      case o: IR.Stmt.Decl.Multiple =>
        val r: PreResult[IR.Stmt] = preIRStmtDeclMultiple(o) match {
         case PreResult(continu, MSome(r: IR.Stmt)) => PreResult(continu, MSome[IR.Stmt](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type IR.Stmt")
         case PreResult(continu, _) => PreResult(continu, MNone[IR.Stmt]())
        }
        return r
    }
  }

  def preIRStmtGround(o: IR.Stmt.Ground): PreResult[IR.Stmt.Ground] = {
    o match {
      case o: IR.Stmt.Assign.Local =>
        val r: PreResult[IR.Stmt.Ground] = preIRStmtAssignLocal(o) match {
         case PreResult(continu, MSome(r: IR.Stmt.Ground)) => PreResult(continu, MSome[IR.Stmt.Ground](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type IR.Stmt.Ground")
         case PreResult(continu, _) => PreResult(continu, MNone[IR.Stmt.Ground]())
        }
        return r
      case o: IR.Stmt.Assign.Global =>
        val r: PreResult[IR.Stmt.Ground] = preIRStmtAssignGlobal(o) match {
         case PreResult(continu, MSome(r: IR.Stmt.Ground)) => PreResult(continu, MSome[IR.Stmt.Ground](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type IR.Stmt.Ground")
         case PreResult(continu, _) => PreResult(continu, MNone[IR.Stmt.Ground]())
        }
        return r
      case o: IR.Stmt.Assign.Temp =>
        val r: PreResult[IR.Stmt.Ground] = preIRStmtAssignTemp(o) match {
         case PreResult(continu, MSome(r: IR.Stmt.Ground)) => PreResult(continu, MSome[IR.Stmt.Ground](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type IR.Stmt.Ground")
         case PreResult(continu, _) => PreResult(continu, MNone[IR.Stmt.Ground]())
        }
        return r
      case o: IR.Stmt.Assign.Field =>
        val r: PreResult[IR.Stmt.Ground] = preIRStmtAssignField(o) match {
         case PreResult(continu, MSome(r: IR.Stmt.Ground)) => PreResult(continu, MSome[IR.Stmt.Ground](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type IR.Stmt.Ground")
         case PreResult(continu, _) => PreResult(continu, MNone[IR.Stmt.Ground]())
        }
        return r
      case o: IR.Stmt.Assign.Index =>
        val r: PreResult[IR.Stmt.Ground] = preIRStmtAssignIndex(o) match {
         case PreResult(continu, MSome(r: IR.Stmt.Ground)) => PreResult(continu, MSome[IR.Stmt.Ground](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type IR.Stmt.Ground")
         case PreResult(continu, _) => PreResult(continu, MNone[IR.Stmt.Ground]())
        }
        return r
      case o: IR.Stmt.Decl.Local =>
        val r: PreResult[IR.Stmt.Ground] = preIRStmtDeclLocal(o) match {
         case PreResult(continu, MSome(r: IR.Stmt.Ground)) => PreResult(continu, MSome[IR.Stmt.Ground](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type IR.Stmt.Ground")
         case PreResult(continu, _) => PreResult(continu, MNone[IR.Stmt.Ground]())
        }
        return r
      case o: IR.Stmt.Decl.Temp =>
        val r: PreResult[IR.Stmt.Ground] = preIRStmtDeclTemp(o) match {
         case PreResult(continu, MSome(r: IR.Stmt.Ground)) => PreResult(continu, MSome[IR.Stmt.Ground](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type IR.Stmt.Ground")
         case PreResult(continu, _) => PreResult(continu, MNone[IR.Stmt.Ground]())
        }
        return r
      case o: IR.Stmt.Decl.Multiple =>
        val r: PreResult[IR.Stmt.Ground] = preIRStmtDeclMultiple(o) match {
         case PreResult(continu, MSome(r: IR.Stmt.Ground)) => PreResult(continu, MSome[IR.Stmt.Ground](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type IR.Stmt.Ground")
         case PreResult(continu, _) => PreResult(continu, MNone[IR.Stmt.Ground]())
        }
        return r
    }
  }

  def preIRStmtAssign(o: IR.Stmt.Assign): PreResult[IR.Stmt.Assign] = {
    o match {
      case o: IR.Stmt.Assign.Local => return preIRStmtAssignLocal(o)
      case o: IR.Stmt.Assign.Global => return preIRStmtAssignGlobal(o)
      case o: IR.Stmt.Assign.Temp => return preIRStmtAssignTemp(o)
      case o: IR.Stmt.Assign.Field => return preIRStmtAssignField(o)
      case o: IR.Stmt.Assign.Index => return preIRStmtAssignIndex(o)
    }
  }

  def preIRStmtAssignLocal(o: IR.Stmt.Assign.Local): PreResult[IR.Stmt.Assign] = {
    return PreResultIRStmtAssignLocal
  }

  def preIRStmtAssignGlobal(o: IR.Stmt.Assign.Global): PreResult[IR.Stmt.Assign] = {
    return PreResultIRStmtAssignGlobal
  }

  def preIRStmtAssignTemp(o: IR.Stmt.Assign.Temp): PreResult[IR.Stmt.Assign] = {
    return PreResultIRStmtAssignTemp
  }

  def preIRStmtAssignField(o: IR.Stmt.Assign.Field): PreResult[IR.Stmt.Assign] = {
    return PreResultIRStmtAssignField
  }

  def preIRStmtAssignIndex(o: IR.Stmt.Assign.Index): PreResult[IR.Stmt.Assign] = {
    return PreResultIRStmtAssignIndex
  }

  def preIRStmtIf(o: IR.Stmt.If): PreResult[IR.Stmt] = {
    return PreResultIRStmtIf
  }

  def preIRStmtBlock(o: IR.Stmt.Block): PreResult[IR.Stmt] = {
    return PreResultIRStmtBlock
  }

  def preIRStmtWhile(o: IR.Stmt.While): PreResult[IR.Stmt] = {
    return PreResultIRStmtWhile
  }

  def preIRStmtReturn(o: IR.Stmt.Return): PreResult[IR.Stmt] = {
    return PreResultIRStmtReturn
  }

  def preIRStmtDecl(o: IR.Stmt.Decl): PreResult[IR.Stmt.Decl] = {
    o match {
      case o: IR.Stmt.Decl.Local =>
        val r: PreResult[IR.Stmt.Decl] = preIRStmtDeclLocal(o) match {
         case PreResult(continu, MSome(r: IR.Stmt.Decl)) => PreResult(continu, MSome[IR.Stmt.Decl](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type IR.Stmt.Decl")
         case PreResult(continu, _) => PreResult(continu, MNone[IR.Stmt.Decl]())
        }
        return r
      case o: IR.Stmt.Decl.Temp =>
        val r: PreResult[IR.Stmt.Decl] = preIRStmtDeclTemp(o) match {
         case PreResult(continu, MSome(r: IR.Stmt.Decl)) => PreResult(continu, MSome[IR.Stmt.Decl](r))
         case PreResult(_, MSome(_)) => halt("Can only produce object of type IR.Stmt.Decl")
         case PreResult(continu, _) => PreResult(continu, MNone[IR.Stmt.Decl]())
        }
        return r
      case o: IR.Stmt.Decl.Multiple => return preIRStmtDeclMultiple(o)
    }
  }

  def preIRStmtDeclGround(o: IR.Stmt.Decl.Ground): PreResult[IR.Stmt.Decl.Ground] = {
    o match {
      case o: IR.Stmt.Decl.Local => return preIRStmtDeclLocal(o)
      case o: IR.Stmt.Decl.Temp => return preIRStmtDeclTemp(o)
    }
  }

  def preIRStmtDeclLocal(o: IR.Stmt.Decl.Local): PreResult[IR.Stmt.Decl.Ground] = {
    return PreResultIRStmtDeclLocal
  }

  def preIRStmtDeclTemp(o: IR.Stmt.Decl.Temp): PreResult[IR.Stmt.Decl.Ground] = {
    return PreResultIRStmtDeclTemp
  }

  def preIRStmtDeclMultiple(o: IR.Stmt.Decl.Multiple): PreResult[IR.Stmt.Decl] = {
    return PreResultIRStmtDeclMultiple
  }

  def preIRJump(o: IR.Jump): PreResult[IR.Jump] = {
    o match {
      case o: IR.Jump.Goto => return preIRJumpGoto(o)
      case o: IR.Jump.If => return preIRJumpIf(o)
      case o: IR.Jump.Return => return preIRJumpReturn(o)
    }
  }

  def preIRJumpGoto(o: IR.Jump.Goto): PreResult[IR.Jump] = {
    return PreResultIRJumpGoto
  }

  def preIRJumpIf(o: IR.Jump.If): PreResult[IR.Jump] = {
    return PreResultIRJumpIf
  }

  def preIRJumpReturn(o: IR.Jump.Return): PreResult[IR.Jump] = {
    return PreResultIRJumpReturn
  }

  def preIRBasicBlock(o: IR.BasicBlock): PreResult[IR.BasicBlock] = {
    return PreResultIRBasicBlock
  }

  def preIRBody(o: IR.Body): PreResult[IR.Body] = {
    o match {
      case o: IR.Body.Block => return preIRBodyBlock(o)
      case o: IR.Body.Basic => return preIRBodyBasic(o)
    }
  }

  def preIRBodyBlock(o: IR.Body.Block): PreResult[IR.Body] = {
    return PreResultIRBodyBlock
  }

  def preIRBodyBasic(o: IR.Body.Basic): PreResult[IR.Body] = {
    return PreResultIRBodyBasic
  }

  def preIRProcedure(o: IR.Procedure): PreResult[IR.Procedure] = {
    return PreResultIRProcedure
  }

  def preIRGlobal(o: IR.Global): PreResult[IR.Global] = {
    return PreResultIRGlobal
  }

  def preIRProgram(o: IR.Program): PreResult[IR.Program] = {
    return PreResultIRProgram
  }

  def preTyped(o: Typed): PreResult[Typed] = {
    o match {
      case o: Typed.Name => return preTypedName(o)
      case o: Typed.Tuple => return preTypedTuple(o)
      case o: Typed.Fun => return preTypedFun(o)
      case o: Typed.TypeVar => return preTypedTypeVar(o)
      case o: Typed.Package => return preTypedPackage(o)
      case o: Typed.Object => return preTypedObject(o)
      case o: Typed.Enum => return preTypedEnum(o)
      case o: Typed.Method => return preTypedMethod(o)
      case o: Typed.Methods => return preTypedMethods(o)
      case o: Typed.Fact => return preTypedFact(o)
      case o: Typed.Theorem => return preTypedTheorem(o)
      case o: Typed.Inv => return preTypedInv(o)
    }
  }

  def preTypedName(o: Typed.Name): PreResult[Typed] = {
    return PreResultTypedName
  }

  def preTypedTuple(o: Typed.Tuple): PreResult[Typed] = {
    return PreResultTypedTuple
  }

  def preTypedFun(o: Typed.Fun): PreResult[Typed] = {
    return PreResultTypedFun
  }

  def preTypedTypeVar(o: Typed.TypeVar): PreResult[Typed] = {
    return PreResultTypedTypeVar
  }

  def preTypedPackage(o: Typed.Package): PreResult[Typed] = {
    return PreResultTypedPackage
  }

  def preTypedObject(o: Typed.Object): PreResult[Typed] = {
    return PreResultTypedObject
  }

  def preTypedEnum(o: Typed.Enum): PreResult[Typed] = {
    return PreResultTypedEnum
  }

  def preTypedMethod(o: Typed.Method): PreResult[Typed] = {
    return PreResultTypedMethod
  }

  def preTypedMethods(o: Typed.Methods): PreResult[Typed] = {
    return PreResultTypedMethods
  }

  def preTypedFact(o: Typed.Fact): PreResult[Typed] = {
    return PreResultTypedFact
  }

  def preTypedTheorem(o: Typed.Theorem): PreResult[Typed] = {
    return PreResultTypedTheorem
  }

  def preTypedInv(o: Typed.Inv): PreResult[Typed] = {
    return PreResultTypedInv
  }

  def postIRMethodContext(o: IR.MethodContext): MOption[IR.MethodContext] = {
    return PostResultIRMethodContext
  }

  def postIRExp(o: IR.Exp): MOption[IR.Exp] = {
    o match {
      case o: IR.Exp.Bool => return postIRExpBool(o)
      case o: IR.Exp.Int => return postIRExpInt(o)
      case o: IR.Exp.F32 => return postIRExpF32(o)
      case o: IR.Exp.F64 => return postIRExpF64(o)
      case o: IR.Exp.R => return postIRExpR(o)
      case o: IR.Exp.String => return postIRExpString(o)
      case o: IR.Exp.Temp => return postIRExpTemp(o)
      case o: IR.Exp.LocalVarRef => return postIRExpLocalVarRef(o)
      case o: IR.Exp.GlobalVarRef => return postIRExpGlobalVarRef(o)
      case o: IR.Exp.EnumElementRef => return postIRExpEnumElementRef(o)
      case o: IR.Exp.FieldVarRef => return postIRExpFieldVarRef(o)
      case o: IR.Exp.Unary => return postIRExpUnary(o)
      case o: IR.Exp.Binary => return postIRExpBinary(o)
      case o: IR.Exp.If => return postIRExpIf(o)
      case o: IR.Exp.Construct => return postIRExpConstruct(o)
      case o: IR.Exp.Apply => return postIRExpApply(o)
      case o: IR.Exp.Select => return postIRExpSelect(o)
      case o: IR.Exp.Indexing => return postIRExpIndexing(o)
      case o: IR.Exp.Type => return postIRExpType(o)
    }
  }

  def postIRExpBool(o: IR.Exp.Bool): MOption[IR.Exp] = {
    return PostResultIRExpBool
  }

  def postIRExpInt(o: IR.Exp.Int): MOption[IR.Exp] = {
    return PostResultIRExpInt
  }

  def postIRExpF32(o: IR.Exp.F32): MOption[IR.Exp] = {
    return PostResultIRExpF32
  }

  def postIRExpF64(o: IR.Exp.F64): MOption[IR.Exp] = {
    return PostResultIRExpF64
  }

  def postIRExpR(o: IR.Exp.R): MOption[IR.Exp] = {
    return PostResultIRExpR
  }

  def postIRExpString(o: IR.Exp.String): MOption[IR.Exp] = {
    return PostResultIRExpString
  }

  def postIRExpTemp(o: IR.Exp.Temp): MOption[IR.Exp] = {
    return PostResultIRExpTemp
  }

  def postIRExpLocalVarRef(o: IR.Exp.LocalVarRef): MOption[IR.Exp] = {
    return PostResultIRExpLocalVarRef
  }

  def postIRExpGlobalVarRef(o: IR.Exp.GlobalVarRef): MOption[IR.Exp] = {
    return PostResultIRExpGlobalVarRef
  }

  def postIRExpEnumElementRef(o: IR.Exp.EnumElementRef): MOption[IR.Exp] = {
    return PostResultIRExpEnumElementRef
  }

  def postIRExpFieldVarRef(o: IR.Exp.FieldVarRef): MOption[IR.Exp] = {
    return PostResultIRExpFieldVarRef
  }

  def postIRExpUnary(o: IR.Exp.Unary): MOption[IR.Exp] = {
    return PostResultIRExpUnary
  }

  def postIRExpBinary(o: IR.Exp.Binary): MOption[IR.Exp] = {
    return PostResultIRExpBinary
  }

  def postIRExpIf(o: IR.Exp.If): MOption[IR.Exp] = {
    return PostResultIRExpIf
  }

  def postIRExpConstruct(o: IR.Exp.Construct): MOption[IR.Exp] = {
    return PostResultIRExpConstruct
  }

  def postIRExpApply(o: IR.Exp.Apply): MOption[IR.Exp] = {
    return PostResultIRExpApply
  }

  def postIRExpSelect(o: IR.Exp.Select): MOption[IR.Exp] = {
    return PostResultIRExpSelect
  }

  def postIRExpIndexing(o: IR.Exp.Indexing): MOption[IR.Exp] = {
    return PostResultIRExpIndexing
  }

  def postIRExpType(o: IR.Exp.Type): MOption[IR.Exp] = {
    return PostResultIRExpType
  }

  def postIRStmt(o: IR.Stmt): MOption[IR.Stmt] = {
    o match {
      case o: IR.Stmt.Assign.Local =>
        val r: MOption[IR.Stmt] = postIRStmtAssignLocal(o) match {
         case MSome(result: IR.Stmt) => MSome[IR.Stmt](result)
         case MSome(_) => halt("Can only produce object of type IR.Stmt")
         case _ => MNone[IR.Stmt]()
        }
        return r
      case o: IR.Stmt.Assign.Global =>
        val r: MOption[IR.Stmt] = postIRStmtAssignGlobal(o) match {
         case MSome(result: IR.Stmt) => MSome[IR.Stmt](result)
         case MSome(_) => halt("Can only produce object of type IR.Stmt")
         case _ => MNone[IR.Stmt]()
        }
        return r
      case o: IR.Stmt.Assign.Temp =>
        val r: MOption[IR.Stmt] = postIRStmtAssignTemp(o) match {
         case MSome(result: IR.Stmt) => MSome[IR.Stmt](result)
         case MSome(_) => halt("Can only produce object of type IR.Stmt")
         case _ => MNone[IR.Stmt]()
        }
        return r
      case o: IR.Stmt.Assign.Field =>
        val r: MOption[IR.Stmt] = postIRStmtAssignField(o) match {
         case MSome(result: IR.Stmt) => MSome[IR.Stmt](result)
         case MSome(_) => halt("Can only produce object of type IR.Stmt")
         case _ => MNone[IR.Stmt]()
        }
        return r
      case o: IR.Stmt.Assign.Index =>
        val r: MOption[IR.Stmt] = postIRStmtAssignIndex(o) match {
         case MSome(result: IR.Stmt) => MSome[IR.Stmt](result)
         case MSome(_) => halt("Can only produce object of type IR.Stmt")
         case _ => MNone[IR.Stmt]()
        }
        return r
      case o: IR.Stmt.If => return postIRStmtIf(o)
      case o: IR.Stmt.Block => return postIRStmtBlock(o)
      case o: IR.Stmt.While => return postIRStmtWhile(o)
      case o: IR.Stmt.Return => return postIRStmtReturn(o)
      case o: IR.Stmt.Decl.Local =>
        val r: MOption[IR.Stmt] = postIRStmtDeclLocal(o) match {
         case MSome(result: IR.Stmt) => MSome[IR.Stmt](result)
         case MSome(_) => halt("Can only produce object of type IR.Stmt")
         case _ => MNone[IR.Stmt]()
        }
        return r
      case o: IR.Stmt.Decl.Temp =>
        val r: MOption[IR.Stmt] = postIRStmtDeclTemp(o) match {
         case MSome(result: IR.Stmt) => MSome[IR.Stmt](result)
         case MSome(_) => halt("Can only produce object of type IR.Stmt")
         case _ => MNone[IR.Stmt]()
        }
        return r
      case o: IR.Stmt.Decl.Multiple =>
        val r: MOption[IR.Stmt] = postIRStmtDeclMultiple(o) match {
         case MSome(result: IR.Stmt) => MSome[IR.Stmt](result)
         case MSome(_) => halt("Can only produce object of type IR.Stmt")
         case _ => MNone[IR.Stmt]()
        }
        return r
    }
  }

  def postIRStmtGround(o: IR.Stmt.Ground): MOption[IR.Stmt.Ground] = {
    o match {
      case o: IR.Stmt.Assign.Local =>
        val r: MOption[IR.Stmt.Ground] = postIRStmtAssignLocal(o) match {
         case MSome(result: IR.Stmt.Ground) => MSome[IR.Stmt.Ground](result)
         case MSome(_) => halt("Can only produce object of type IR.Stmt.Ground")
         case _ => MNone[IR.Stmt.Ground]()
        }
        return r
      case o: IR.Stmt.Assign.Global =>
        val r: MOption[IR.Stmt.Ground] = postIRStmtAssignGlobal(o) match {
         case MSome(result: IR.Stmt.Ground) => MSome[IR.Stmt.Ground](result)
         case MSome(_) => halt("Can only produce object of type IR.Stmt.Ground")
         case _ => MNone[IR.Stmt.Ground]()
        }
        return r
      case o: IR.Stmt.Assign.Temp =>
        val r: MOption[IR.Stmt.Ground] = postIRStmtAssignTemp(o) match {
         case MSome(result: IR.Stmt.Ground) => MSome[IR.Stmt.Ground](result)
         case MSome(_) => halt("Can only produce object of type IR.Stmt.Ground")
         case _ => MNone[IR.Stmt.Ground]()
        }
        return r
      case o: IR.Stmt.Assign.Field =>
        val r: MOption[IR.Stmt.Ground] = postIRStmtAssignField(o) match {
         case MSome(result: IR.Stmt.Ground) => MSome[IR.Stmt.Ground](result)
         case MSome(_) => halt("Can only produce object of type IR.Stmt.Ground")
         case _ => MNone[IR.Stmt.Ground]()
        }
        return r
      case o: IR.Stmt.Assign.Index =>
        val r: MOption[IR.Stmt.Ground] = postIRStmtAssignIndex(o) match {
         case MSome(result: IR.Stmt.Ground) => MSome[IR.Stmt.Ground](result)
         case MSome(_) => halt("Can only produce object of type IR.Stmt.Ground")
         case _ => MNone[IR.Stmt.Ground]()
        }
        return r
      case o: IR.Stmt.Decl.Local =>
        val r: MOption[IR.Stmt.Ground] = postIRStmtDeclLocal(o) match {
         case MSome(result: IR.Stmt.Ground) => MSome[IR.Stmt.Ground](result)
         case MSome(_) => halt("Can only produce object of type IR.Stmt.Ground")
         case _ => MNone[IR.Stmt.Ground]()
        }
        return r
      case o: IR.Stmt.Decl.Temp =>
        val r: MOption[IR.Stmt.Ground] = postIRStmtDeclTemp(o) match {
         case MSome(result: IR.Stmt.Ground) => MSome[IR.Stmt.Ground](result)
         case MSome(_) => halt("Can only produce object of type IR.Stmt.Ground")
         case _ => MNone[IR.Stmt.Ground]()
        }
        return r
      case o: IR.Stmt.Decl.Multiple =>
        val r: MOption[IR.Stmt.Ground] = postIRStmtDeclMultiple(o) match {
         case MSome(result: IR.Stmt.Ground) => MSome[IR.Stmt.Ground](result)
         case MSome(_) => halt("Can only produce object of type IR.Stmt.Ground")
         case _ => MNone[IR.Stmt.Ground]()
        }
        return r
    }
  }

  def postIRStmtAssign(o: IR.Stmt.Assign): MOption[IR.Stmt.Assign] = {
    o match {
      case o: IR.Stmt.Assign.Local => return postIRStmtAssignLocal(o)
      case o: IR.Stmt.Assign.Global => return postIRStmtAssignGlobal(o)
      case o: IR.Stmt.Assign.Temp => return postIRStmtAssignTemp(o)
      case o: IR.Stmt.Assign.Field => return postIRStmtAssignField(o)
      case o: IR.Stmt.Assign.Index => return postIRStmtAssignIndex(o)
    }
  }

  def postIRStmtAssignLocal(o: IR.Stmt.Assign.Local): MOption[IR.Stmt.Assign] = {
    return PostResultIRStmtAssignLocal
  }

  def postIRStmtAssignGlobal(o: IR.Stmt.Assign.Global): MOption[IR.Stmt.Assign] = {
    return PostResultIRStmtAssignGlobal
  }

  def postIRStmtAssignTemp(o: IR.Stmt.Assign.Temp): MOption[IR.Stmt.Assign] = {
    return PostResultIRStmtAssignTemp
  }

  def postIRStmtAssignField(o: IR.Stmt.Assign.Field): MOption[IR.Stmt.Assign] = {
    return PostResultIRStmtAssignField
  }

  def postIRStmtAssignIndex(o: IR.Stmt.Assign.Index): MOption[IR.Stmt.Assign] = {
    return PostResultIRStmtAssignIndex
  }

  def postIRStmtIf(o: IR.Stmt.If): MOption[IR.Stmt] = {
    return PostResultIRStmtIf
  }

  def postIRStmtBlock(o: IR.Stmt.Block): MOption[IR.Stmt] = {
    return PostResultIRStmtBlock
  }

  def postIRStmtWhile(o: IR.Stmt.While): MOption[IR.Stmt] = {
    return PostResultIRStmtWhile
  }

  def postIRStmtReturn(o: IR.Stmt.Return): MOption[IR.Stmt] = {
    return PostResultIRStmtReturn
  }

  def postIRStmtDecl(o: IR.Stmt.Decl): MOption[IR.Stmt.Decl] = {
    o match {
      case o: IR.Stmt.Decl.Local =>
        val r: MOption[IR.Stmt.Decl] = postIRStmtDeclLocal(o) match {
         case MSome(result: IR.Stmt.Decl) => MSome[IR.Stmt.Decl](result)
         case MSome(_) => halt("Can only produce object of type IR.Stmt.Decl")
         case _ => MNone[IR.Stmt.Decl]()
        }
        return r
      case o: IR.Stmt.Decl.Temp =>
        val r: MOption[IR.Stmt.Decl] = postIRStmtDeclTemp(o) match {
         case MSome(result: IR.Stmt.Decl) => MSome[IR.Stmt.Decl](result)
         case MSome(_) => halt("Can only produce object of type IR.Stmt.Decl")
         case _ => MNone[IR.Stmt.Decl]()
        }
        return r
      case o: IR.Stmt.Decl.Multiple => return postIRStmtDeclMultiple(o)
    }
  }

  def postIRStmtDeclGround(o: IR.Stmt.Decl.Ground): MOption[IR.Stmt.Decl.Ground] = {
    o match {
      case o: IR.Stmt.Decl.Local => return postIRStmtDeclLocal(o)
      case o: IR.Stmt.Decl.Temp => return postIRStmtDeclTemp(o)
    }
  }

  def postIRStmtDeclLocal(o: IR.Stmt.Decl.Local): MOption[IR.Stmt.Decl.Ground] = {
    return PostResultIRStmtDeclLocal
  }

  def postIRStmtDeclTemp(o: IR.Stmt.Decl.Temp): MOption[IR.Stmt.Decl.Ground] = {
    return PostResultIRStmtDeclTemp
  }

  def postIRStmtDeclMultiple(o: IR.Stmt.Decl.Multiple): MOption[IR.Stmt.Decl] = {
    return PostResultIRStmtDeclMultiple
  }

  def postIRJump(o: IR.Jump): MOption[IR.Jump] = {
    o match {
      case o: IR.Jump.Goto => return postIRJumpGoto(o)
      case o: IR.Jump.If => return postIRJumpIf(o)
      case o: IR.Jump.Return => return postIRJumpReturn(o)
    }
  }

  def postIRJumpGoto(o: IR.Jump.Goto): MOption[IR.Jump] = {
    return PostResultIRJumpGoto
  }

  def postIRJumpIf(o: IR.Jump.If): MOption[IR.Jump] = {
    return PostResultIRJumpIf
  }

  def postIRJumpReturn(o: IR.Jump.Return): MOption[IR.Jump] = {
    return PostResultIRJumpReturn
  }

  def postIRBasicBlock(o: IR.BasicBlock): MOption[IR.BasicBlock] = {
    return PostResultIRBasicBlock
  }

  def postIRBody(o: IR.Body): MOption[IR.Body] = {
    o match {
      case o: IR.Body.Block => return postIRBodyBlock(o)
      case o: IR.Body.Basic => return postIRBodyBasic(o)
    }
  }

  def postIRBodyBlock(o: IR.Body.Block): MOption[IR.Body] = {
    return PostResultIRBodyBlock
  }

  def postIRBodyBasic(o: IR.Body.Basic): MOption[IR.Body] = {
    return PostResultIRBodyBasic
  }

  def postIRProcedure(o: IR.Procedure): MOption[IR.Procedure] = {
    return PostResultIRProcedure
  }

  def postIRGlobal(o: IR.Global): MOption[IR.Global] = {
    return PostResultIRGlobal
  }

  def postIRProgram(o: IR.Program): MOption[IR.Program] = {
    return PostResultIRProgram
  }

  def postTyped(o: Typed): MOption[Typed] = {
    o match {
      case o: Typed.Name => return postTypedName(o)
      case o: Typed.Tuple => return postTypedTuple(o)
      case o: Typed.Fun => return postTypedFun(o)
      case o: Typed.TypeVar => return postTypedTypeVar(o)
      case o: Typed.Package => return postTypedPackage(o)
      case o: Typed.Object => return postTypedObject(o)
      case o: Typed.Enum => return postTypedEnum(o)
      case o: Typed.Method => return postTypedMethod(o)
      case o: Typed.Methods => return postTypedMethods(o)
      case o: Typed.Fact => return postTypedFact(o)
      case o: Typed.Theorem => return postTypedTheorem(o)
      case o: Typed.Inv => return postTypedInv(o)
    }
  }

  def postTypedName(o: Typed.Name): MOption[Typed] = {
    return PostResultTypedName
  }

  def postTypedTuple(o: Typed.Tuple): MOption[Typed] = {
    return PostResultTypedTuple
  }

  def postTypedFun(o: Typed.Fun): MOption[Typed] = {
    return PostResultTypedFun
  }

  def postTypedTypeVar(o: Typed.TypeVar): MOption[Typed] = {
    return PostResultTypedTypeVar
  }

  def postTypedPackage(o: Typed.Package): MOption[Typed] = {
    return PostResultTypedPackage
  }

  def postTypedObject(o: Typed.Object): MOption[Typed] = {
    return PostResultTypedObject
  }

  def postTypedEnum(o: Typed.Enum): MOption[Typed] = {
    return PostResultTypedEnum
  }

  def postTypedMethod(o: Typed.Method): MOption[Typed] = {
    return PostResultTypedMethod
  }

  def postTypedMethods(o: Typed.Methods): MOption[Typed] = {
    return PostResultTypedMethods
  }

  def postTypedFact(o: Typed.Fact): MOption[Typed] = {
    return PostResultTypedFact
  }

  def postTypedTheorem(o: Typed.Theorem): MOption[Typed] = {
    return PostResultTypedTheorem
  }

  def postTypedInv(o: Typed.Inv): MOption[Typed] = {
    return PostResultTypedInv
  }

  def transformIRMethodContext(o: IR.MethodContext): MOption[IR.MethodContext] = {
    val preR: PreResult[IR.MethodContext] = preIRMethodContext(o)
    val r: MOption[IR.MethodContext] = if (preR.continu) {
      val o2: IR.MethodContext = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Typed.Fun] = transformTypedFun(o2.t)
      if (hasChanged || r0.nonEmpty)
        MSome(o2(t = r0.getOrElse(o2.t)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: IR.MethodContext = r.getOrElse(o)
    val postR: MOption[IR.MethodContext] = postIRMethodContext(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformIRExp(o: IR.Exp): MOption[IR.Exp] = {
    val preR: PreResult[IR.Exp] = preIRExp(o)
    val r: MOption[IR.Exp] = if (preR.continu) {
      val o2: IR.Exp = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[IR.Exp] = o2 match {
        case o2: IR.Exp.Bool =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: IR.Exp.Int =>
          val r0: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(tipe = r0.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: IR.Exp.F32 =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: IR.Exp.F64 =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: IR.Exp.R =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: IR.Exp.String =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: IR.Exp.Temp =>
          val r0: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(tipe = r0.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: IR.Exp.LocalVarRef =>
          val r0: MOption[IR.MethodContext] = transformIRMethodContext(o2.context)
          val r1: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(context = r0.getOrElse(o2.context), tipe = r1.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: IR.Exp.GlobalVarRef =>
          val r0: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(tipe = r0.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: IR.Exp.EnumElementRef =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: IR.Exp.FieldVarRef =>
          val r0: MOption[Typed] = transformTyped(o2.owner)
          val r1: MOption[IR.Exp] = transformIRExp(o2.receiver)
          val r2: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(owner = r0.getOrElse(o2.owner), receiver = r1.getOrElse(o2.receiver), tipe = r2.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: IR.Exp.Unary =>
          val r0: MOption[Typed] = transformTyped(o2.tipe)
          val r1: MOption[IR.Exp] = transformIRExp(o2.exp)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(tipe = r0.getOrElse(o2.tipe), exp = r1.getOrElse(o2.exp)))
          else
            MNone()
        case o2: IR.Exp.Binary =>
          val r0: MOption[Typed] = transformTyped(o2.tipe)
          val r1: MOption[IR.Exp] = transformIRExp(o2.left)
          val r2: MOption[IR.Exp] = transformIRExp(o2.right)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(tipe = r0.getOrElse(o2.tipe), left = r1.getOrElse(o2.left), right = r2.getOrElse(o2.right)))
          else
            MNone()
        case o2: IR.Exp.If =>
          val r0: MOption[IR.Exp] = transformIRExp(o2.cond)
          val r1: MOption[IR.Exp] = transformIRExp(o2.thenExp)
          val r2: MOption[IR.Exp] = transformIRExp(o2.elseExp)
          val r3: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty)
            MSome(o2(cond = r0.getOrElse(o2.cond), thenExp = r1.getOrElse(o2.thenExp), elseExp = r2.getOrElse(o2.elseExp), tipe = r3.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: IR.Exp.Construct =>
          val r0: MOption[Typed] = transformTyped(o2.tipe)
          val r1: MOption[IS[Z, IR.Exp]] = transformISZ(o2.args, transformIRExp _)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(tipe = r0.getOrElse(o2.tipe), args = r1.getOrElse(o2.args)))
          else
            MNone()
        case o2: IR.Exp.Apply =>
          val r0: MOption[IS[Z, IR.Exp]] = transformISZ(o2.args, transformIRExp _)
          val r1: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(args = r0.getOrElse(o2.args), tipe = r1.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: IR.Exp.Select =>
          val r0: MOption[IR.Exp] = transformIRExp(o2.exp)
          val r1: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(exp = r0.getOrElse(o2.exp), tipe = r1.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: IR.Exp.Indexing =>
          val r0: MOption[IR.Exp] = transformIRExp(o2.exp)
          val r1: MOption[Typed] = transformTyped(o2.tipe)
          val r2: MOption[IR.Exp] = transformIRExp(o2.index)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(exp = r0.getOrElse(o2.exp), tipe = r1.getOrElse(o2.tipe), index = r2.getOrElse(o2.index)))
          else
            MNone()
        case o2: IR.Exp.Type =>
          val r0: MOption[IR.Exp] = transformIRExp(o2.exp)
          val r1: MOption[Typed] = transformTyped(o2.t)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(exp = r0.getOrElse(o2.exp), t = r1.getOrElse(o2.t)))
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: IR.Exp = r.getOrElse(o)
    val postR: MOption[IR.Exp] = postIRExp(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformIRStmt(o: IR.Stmt): MOption[IR.Stmt] = {
    val preR: PreResult[IR.Stmt] = preIRStmt(o)
    val r: MOption[IR.Stmt] = if (preR.continu) {
      val o2: IR.Stmt = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[IR.Stmt] = o2 match {
        case o2: IR.Stmt.Assign.Local =>
          val r0: MOption[IR.MethodContext] = transformIRMethodContext(o2.context)
          val r1: MOption[IR.Exp] = transformIRExp(o2.rhs)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(context = r0.getOrElse(o2.context), rhs = r1.getOrElse(o2.rhs)))
          else
            MNone()
        case o2: IR.Stmt.Assign.Global =>
          val r0: MOption[IR.Exp] = transformIRExp(o2.rhs)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(rhs = r0.getOrElse(o2.rhs)))
          else
            MNone()
        case o2: IR.Stmt.Assign.Temp =>
          val r0: MOption[IR.Exp] = transformIRExp(o2.rhs)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(rhs = r0.getOrElse(o2.rhs)))
          else
            MNone()
        case o2: IR.Stmt.Assign.Field =>
          val r0: MOption[IR.Exp] = transformIRExp(o2.receiver)
          val r1: MOption[Typed.Name] = transformTypedName(o2.receiverType)
          val r2: MOption[IR.Exp] = transformIRExp(o2.rhs)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(receiver = r0.getOrElse(o2.receiver), receiverType = r1.getOrElse(o2.receiverType), rhs = r2.getOrElse(o2.rhs)))
          else
            MNone()
        case o2: IR.Stmt.Assign.Index =>
          val r0: MOption[IR.Exp] = transformIRExp(o2.receiver)
          val r1: MOption[Typed.Name] = transformTypedName(o2.receiverType)
          val r2: MOption[IR.Exp] = transformIRExp(o2.index)
          val r3: MOption[IR.Exp] = transformIRExp(o2.rhs)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty)
            MSome(o2(receiver = r0.getOrElse(o2.receiver), receiverType = r1.getOrElse(o2.receiverType), index = r2.getOrElse(o2.index), rhs = r3.getOrElse(o2.rhs)))
          else
            MNone()
        case o2: IR.Stmt.If =>
          val r0: MOption[IR.Exp] = transformIRExp(o2.cond)
          val r1: MOption[IR.Stmt.Block] = transformIRStmtBlock(o2.thenBlock)
          val r2: MOption[IR.Stmt.Block] = transformIRStmtBlock(o2.elseBlock)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(cond = r0.getOrElse(o2.cond), thenBlock = r1.getOrElse(o2.thenBlock), elseBlock = r2.getOrElse(o2.elseBlock)))
          else
            MNone()
        case o2: IR.Stmt.Block =>
          val r0: MOption[IS[Z, IR.Stmt]] = transformISZ(o2.stmts, transformIRStmt _)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(stmts = r0.getOrElse(o2.stmts)))
          else
            MNone()
        case o2: IR.Stmt.While =>
          val r0: MOption[IR.Stmt.Block] = transformIRStmtBlock(o2.condBlock)
          val r1: MOption[IR.Exp] = transformIRExp(o2.cond)
          val r2: MOption[IR.Stmt.Block] = transformIRStmtBlock(o2.block)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(condBlock = r0.getOrElse(o2.condBlock), cond = r1.getOrElse(o2.cond), block = r2.getOrElse(o2.block)))
          else
            MNone()
        case o2: IR.Stmt.Return =>
          val r0: MOption[Option[IR.Exp]] = transformOption(o2.expOpt, transformIRExp _)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(expOpt = r0.getOrElse(o2.expOpt)))
          else
            MNone()
        case o2: IR.Stmt.Decl.Local =>
          val r0: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(tipe = r0.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: IR.Stmt.Decl.Temp =>
          val r0: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(tipe = r0.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: IR.Stmt.Decl.Multiple =>
          val r0: MOption[IS[Z, IR.Stmt.Decl.Ground]] = transformISZ(o2.decls, transformIRStmtDeclGround _)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(decls = r0.getOrElse(o2.decls)))
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: IR.Stmt = r.getOrElse(o)
    val postR: MOption[IR.Stmt] = postIRStmt(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformIRStmtGround(o: IR.Stmt.Ground): MOption[IR.Stmt.Ground] = {
    val preR: PreResult[IR.Stmt.Ground] = preIRStmtGround(o)
    val r: MOption[IR.Stmt.Ground] = if (preR.continu) {
      val o2: IR.Stmt.Ground = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[IR.Stmt.Ground] = o2 match {
        case o2: IR.Stmt.Assign.Local =>
          val r0: MOption[IR.MethodContext] = transformIRMethodContext(o2.context)
          val r1: MOption[IR.Exp] = transformIRExp(o2.rhs)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(context = r0.getOrElse(o2.context), rhs = r1.getOrElse(o2.rhs)))
          else
            MNone()
        case o2: IR.Stmt.Assign.Global =>
          val r0: MOption[IR.Exp] = transformIRExp(o2.rhs)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(rhs = r0.getOrElse(o2.rhs)))
          else
            MNone()
        case o2: IR.Stmt.Assign.Temp =>
          val r0: MOption[IR.Exp] = transformIRExp(o2.rhs)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(rhs = r0.getOrElse(o2.rhs)))
          else
            MNone()
        case o2: IR.Stmt.Assign.Field =>
          val r0: MOption[IR.Exp] = transformIRExp(o2.receiver)
          val r1: MOption[Typed.Name] = transformTypedName(o2.receiverType)
          val r2: MOption[IR.Exp] = transformIRExp(o2.rhs)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(receiver = r0.getOrElse(o2.receiver), receiverType = r1.getOrElse(o2.receiverType), rhs = r2.getOrElse(o2.rhs)))
          else
            MNone()
        case o2: IR.Stmt.Assign.Index =>
          val r0: MOption[IR.Exp] = transformIRExp(o2.receiver)
          val r1: MOption[Typed.Name] = transformTypedName(o2.receiverType)
          val r2: MOption[IR.Exp] = transformIRExp(o2.index)
          val r3: MOption[IR.Exp] = transformIRExp(o2.rhs)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty)
            MSome(o2(receiver = r0.getOrElse(o2.receiver), receiverType = r1.getOrElse(o2.receiverType), index = r2.getOrElse(o2.index), rhs = r3.getOrElse(o2.rhs)))
          else
            MNone()
        case o2: IR.Stmt.Decl.Local =>
          val r0: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(tipe = r0.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: IR.Stmt.Decl.Temp =>
          val r0: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(tipe = r0.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: IR.Stmt.Decl.Multiple =>
          val r0: MOption[IS[Z, IR.Stmt.Decl.Ground]] = transformISZ(o2.decls, transformIRStmtDeclGround _)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(decls = r0.getOrElse(o2.decls)))
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: IR.Stmt.Ground = r.getOrElse(o)
    val postR: MOption[IR.Stmt.Ground] = postIRStmtGround(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformIRStmtAssign(o: IR.Stmt.Assign): MOption[IR.Stmt.Assign] = {
    val preR: PreResult[IR.Stmt.Assign] = preIRStmtAssign(o)
    val r: MOption[IR.Stmt.Assign] = if (preR.continu) {
      val o2: IR.Stmt.Assign = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[IR.Stmt.Assign] = o2 match {
        case o2: IR.Stmt.Assign.Local =>
          val r0: MOption[IR.MethodContext] = transformIRMethodContext(o2.context)
          val r1: MOption[IR.Exp] = transformIRExp(o2.rhs)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(context = r0.getOrElse(o2.context), rhs = r1.getOrElse(o2.rhs)))
          else
            MNone()
        case o2: IR.Stmt.Assign.Global =>
          val r0: MOption[IR.Exp] = transformIRExp(o2.rhs)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(rhs = r0.getOrElse(o2.rhs)))
          else
            MNone()
        case o2: IR.Stmt.Assign.Temp =>
          val r0: MOption[IR.Exp] = transformIRExp(o2.rhs)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(rhs = r0.getOrElse(o2.rhs)))
          else
            MNone()
        case o2: IR.Stmt.Assign.Field =>
          val r0: MOption[IR.Exp] = transformIRExp(o2.receiver)
          val r1: MOption[Typed.Name] = transformTypedName(o2.receiverType)
          val r2: MOption[IR.Exp] = transformIRExp(o2.rhs)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
            MSome(o2(receiver = r0.getOrElse(o2.receiver), receiverType = r1.getOrElse(o2.receiverType), rhs = r2.getOrElse(o2.rhs)))
          else
            MNone()
        case o2: IR.Stmt.Assign.Index =>
          val r0: MOption[IR.Exp] = transformIRExp(o2.receiver)
          val r1: MOption[Typed.Name] = transformTypedName(o2.receiverType)
          val r2: MOption[IR.Exp] = transformIRExp(o2.index)
          val r3: MOption[IR.Exp] = transformIRExp(o2.rhs)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty || r3.nonEmpty)
            MSome(o2(receiver = r0.getOrElse(o2.receiver), receiverType = r1.getOrElse(o2.receiverType), index = r2.getOrElse(o2.index), rhs = r3.getOrElse(o2.rhs)))
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: IR.Stmt.Assign = r.getOrElse(o)
    val postR: MOption[IR.Stmt.Assign] = postIRStmtAssign(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformIRStmtDecl(o: IR.Stmt.Decl): MOption[IR.Stmt.Decl] = {
    val preR: PreResult[IR.Stmt.Decl] = preIRStmtDecl(o)
    val r: MOption[IR.Stmt.Decl] = if (preR.continu) {
      val o2: IR.Stmt.Decl = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[IR.Stmt.Decl] = o2 match {
        case o2: IR.Stmt.Decl.Local =>
          val r0: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(tipe = r0.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: IR.Stmt.Decl.Temp =>
          val r0: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(tipe = r0.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: IR.Stmt.Decl.Multiple =>
          val r0: MOption[IS[Z, IR.Stmt.Decl.Ground]] = transformISZ(o2.decls, transformIRStmtDeclGround _)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(decls = r0.getOrElse(o2.decls)))
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: IR.Stmt.Decl = r.getOrElse(o)
    val postR: MOption[IR.Stmt.Decl] = postIRStmtDecl(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformIRStmtDeclGround(o: IR.Stmt.Decl.Ground): MOption[IR.Stmt.Decl.Ground] = {
    val preR: PreResult[IR.Stmt.Decl.Ground] = preIRStmtDeclGround(o)
    val r: MOption[IR.Stmt.Decl.Ground] = if (preR.continu) {
      val o2: IR.Stmt.Decl.Ground = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[IR.Stmt.Decl.Ground] = o2 match {
        case o2: IR.Stmt.Decl.Local =>
          val r0: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(tipe = r0.getOrElse(o2.tipe)))
          else
            MNone()
        case o2: IR.Stmt.Decl.Temp =>
          val r0: MOption[Typed] = transformTyped(o2.tipe)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(tipe = r0.getOrElse(o2.tipe)))
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: IR.Stmt.Decl.Ground = r.getOrElse(o)
    val postR: MOption[IR.Stmt.Decl.Ground] = postIRStmtDeclGround(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformIRJump(o: IR.Jump): MOption[IR.Jump] = {
    val preR: PreResult[IR.Jump] = preIRJump(o)
    val r: MOption[IR.Jump] = if (preR.continu) {
      val o2: IR.Jump = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[IR.Jump] = o2 match {
        case o2: IR.Jump.Goto =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: IR.Jump.If =>
          val r0: MOption[IR.Exp] = transformIRExp(o2.cond)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(cond = r0.getOrElse(o2.cond)))
          else
            MNone()
        case o2: IR.Jump.Return =>
          val r0: MOption[Option[IR.Exp]] = transformOption(o2.expOpt, transformIRExp _)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(expOpt = r0.getOrElse(o2.expOpt)))
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: IR.Jump = r.getOrElse(o)
    val postR: MOption[IR.Jump] = postIRJump(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformIRBasicBlock(o: IR.BasicBlock): MOption[IR.BasicBlock] = {
    val preR: PreResult[IR.BasicBlock] = preIRBasicBlock(o)
    val r: MOption[IR.BasicBlock] = if (preR.continu) {
      val o2: IR.BasicBlock = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[IS[Z, IR.Stmt.Ground]] = transformISZ(o2.grounds, transformIRStmtGround _)
      val r1: MOption[IR.Jump] = transformIRJump(o2.jump)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty)
        MSome(o2(grounds = r0.getOrElse(o2.grounds), jump = r1.getOrElse(o2.jump)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: IR.BasicBlock = r.getOrElse(o)
    val postR: MOption[IR.BasicBlock] = postIRBasicBlock(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformIRBody(o: IR.Body): MOption[IR.Body] = {
    val preR: PreResult[IR.Body] = preIRBody(o)
    val r: MOption[IR.Body] = if (preR.continu) {
      val o2: IR.Body = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[IR.Body] = o2 match {
        case o2: IR.Body.Block =>
          val r0: MOption[IR.Stmt.Block] = transformIRStmtBlock(o2.block)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(block = r0.getOrElse(o2.block)))
          else
            MNone()
        case o2: IR.Body.Basic =>
          val r0: MOption[IS[Z, IR.BasicBlock]] = transformISZ(o2.blocks, transformIRBasicBlock _)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(blocks = r0.getOrElse(o2.blocks)))
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: IR.Body = r.getOrElse(o)
    val postR: MOption[IR.Body] = postIRBody(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformIRProcedure(o: IR.Procedure): MOption[IR.Procedure] = {
    val preR: PreResult[IR.Procedure] = preIRProcedure(o)
    val r: MOption[IR.Procedure] = if (preR.continu) {
      val o2: IR.Procedure = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Typed.Fun] = transformTypedFun(o2.tipe)
      val r1: MOption[IR.Body] = transformIRBody(o2.body)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty)
        MSome(o2(tipe = r0.getOrElse(o2.tipe), body = r1.getOrElse(o2.body)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: IR.Procedure = r.getOrElse(o)
    val postR: MOption[IR.Procedure] = postIRProcedure(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformIRGlobal(o: IR.Global): MOption[IR.Global] = {
    val preR: PreResult[IR.Global] = preIRGlobal(o)
    val r: MOption[IR.Global] = if (preR.continu) {
      val o2: IR.Global = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Typed] = transformTyped(o2.tipe)
      if (hasChanged || r0.nonEmpty)
        MSome(o2(tipe = r0.getOrElse(o2.tipe)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: IR.Global = r.getOrElse(o)
    val postR: MOption[IR.Global] = postIRGlobal(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformIRProgram(o: IR.Program): MOption[IR.Program] = {
    val preR: PreResult[IR.Program] = preIRProgram(o)
    val r: MOption[IR.Program] = if (preR.continu) {
      val o2: IR.Program = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[IS[Z, IR.Global]] = transformISZ(o2.globals, transformIRGlobal _)
      val r1: MOption[IS[Z, IR.Procedure]] = transformISZ(o2.procedures, transformIRProcedure _)
      val r2: MOption[IS[Z, IR.Stmt]] = transformISZ(o2.stmts, transformIRStmt _)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty || r2.nonEmpty)
        MSome(o2(globals = r0.getOrElse(o2.globals), procedures = r1.getOrElse(o2.procedures), stmts = r2.getOrElse(o2.stmts)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: IR.Program = r.getOrElse(o)
    val postR: MOption[IR.Program] = postIRProgram(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformTyped(o: Typed): MOption[Typed] = {
    val preR: PreResult[Typed] = preTyped(o)
    val r: MOption[Typed] = if (preR.continu) {
      val o2: Typed = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val rOpt: MOption[Typed] = o2 match {
        case o2: Typed.Name =>
          val r0: MOption[IS[Z, Typed]] = transformISZ(o2.args, transformTyped _)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(args = r0.getOrElse(o2.args)))
          else
            MNone()
        case o2: Typed.Tuple =>
          val r0: MOption[IS[Z, Typed]] = transformISZ(o2.args, transformTyped _)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(args = r0.getOrElse(o2.args)))
          else
            MNone()
        case o2: Typed.Fun =>
          val r0: MOption[IS[Z, Typed]] = transformISZ(o2.args, transformTyped _)
          val r1: MOption[Typed] = transformTyped(o2.ret)
          if (hasChanged || r0.nonEmpty || r1.nonEmpty)
            MSome(o2(args = r0.getOrElse(o2.args), ret = r1.getOrElse(o2.ret)))
          else
            MNone()
        case o2: Typed.TypeVar =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: Typed.Package =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: Typed.Object =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: Typed.Enum =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: Typed.Method =>
          val r0: MOption[Typed.Fun] = transformTypedFun(o2.tpe)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(tpe = r0.getOrElse(o2.tpe)))
          else
            MNone()
        case o2: Typed.Methods =>
          val r0: MOption[IS[Z, Typed.Method]] = transformISZ(o2.methods, transformTypedMethod _)
          if (hasChanged || r0.nonEmpty)
            MSome(o2(methods = r0.getOrElse(o2.methods)))
          else
            MNone()
        case o2: Typed.Fact =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: Typed.Theorem =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
        case o2: Typed.Inv =>
          if (hasChanged)
            MSome(o2)
          else
            MNone()
      }
      rOpt
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Typed = r.getOrElse(o)
    val postR: MOption[Typed] = postTyped(o2)
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformTypedFun(o: Typed.Fun): MOption[Typed.Fun] = {
    val preR: PreResult[Typed.Fun] = preTypedFun(o) match {
     case PreResult(continu, MSome(r: Typed.Fun)) => PreResult(continu, MSome[Typed.Fun](r))
     case PreResult(_, MSome(_)) => halt("Can only produce object of type Typed.Fun")
     case PreResult(continu, _) => PreResult(continu, MNone[Typed.Fun]())
    }
    val r: MOption[Typed.Fun] = if (preR.continu) {
      val o2: Typed.Fun = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[IS[Z, Typed]] = transformISZ(o2.args, transformTyped _)
      val r1: MOption[Typed] = transformTyped(o2.ret)
      if (hasChanged || r0.nonEmpty || r1.nonEmpty)
        MSome(o2(args = r0.getOrElse(o2.args), ret = r1.getOrElse(o2.ret)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Typed.Fun = r.getOrElse(o)
    val postR: MOption[Typed.Fun] = postTypedFun(o2) match {
     case MSome(result: Typed.Fun) => MSome[Typed.Fun](result)
     case MSome(_) => halt("Can only produce object of type Typed.Fun")
     case _ => MNone[Typed.Fun]()
    }
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformTypedName(o: Typed.Name): MOption[Typed.Name] = {
    val preR: PreResult[Typed.Name] = preTypedName(o) match {
     case PreResult(continu, MSome(r: Typed.Name)) => PreResult(continu, MSome[Typed.Name](r))
     case PreResult(_, MSome(_)) => halt("Can only produce object of type Typed.Name")
     case PreResult(continu, _) => PreResult(continu, MNone[Typed.Name]())
    }
    val r: MOption[Typed.Name] = if (preR.continu) {
      val o2: Typed.Name = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[IS[Z, Typed]] = transformISZ(o2.args, transformTyped _)
      if (hasChanged || r0.nonEmpty)
        MSome(o2(args = r0.getOrElse(o2.args)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Typed.Name = r.getOrElse(o)
    val postR: MOption[Typed.Name] = postTypedName(o2) match {
     case MSome(result: Typed.Name) => MSome[Typed.Name](result)
     case MSome(_) => halt("Can only produce object of type Typed.Name")
     case _ => MNone[Typed.Name]()
    }
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformIRStmtBlock(o: IR.Stmt.Block): MOption[IR.Stmt.Block] = {
    val preR: PreResult[IR.Stmt.Block] = preIRStmtBlock(o) match {
     case PreResult(continu, MSome(r: IR.Stmt.Block)) => PreResult(continu, MSome[IR.Stmt.Block](r))
     case PreResult(_, MSome(_)) => halt("Can only produce object of type IR.Stmt.Block")
     case PreResult(continu, _) => PreResult(continu, MNone[IR.Stmt.Block]())
    }
    val r: MOption[IR.Stmt.Block] = if (preR.continu) {
      val o2: IR.Stmt.Block = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[IS[Z, IR.Stmt]] = transformISZ(o2.stmts, transformIRStmt _)
      if (hasChanged || r0.nonEmpty)
        MSome(o2(stmts = r0.getOrElse(o2.stmts)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: IR.Stmt.Block = r.getOrElse(o)
    val postR: MOption[IR.Stmt.Block] = postIRStmtBlock(o2) match {
     case MSome(result: IR.Stmt.Block) => MSome[IR.Stmt.Block](result)
     case MSome(_) => halt("Can only produce object of type IR.Stmt.Block")
     case _ => MNone[IR.Stmt.Block]()
    }
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

  def transformTypedMethod(o: Typed.Method): MOption[Typed.Method] = {
    val preR: PreResult[Typed.Method] = preTypedMethod(o) match {
     case PreResult(continu, MSome(r: Typed.Method)) => PreResult(continu, MSome[Typed.Method](r))
     case PreResult(_, MSome(_)) => halt("Can only produce object of type Typed.Method")
     case PreResult(continu, _) => PreResult(continu, MNone[Typed.Method]())
    }
    val r: MOption[Typed.Method] = if (preR.continu) {
      val o2: Typed.Method = preR.resultOpt.getOrElse(o)
      val hasChanged: B = preR.resultOpt.nonEmpty
      val r0: MOption[Typed.Fun] = transformTypedFun(o2.tpe)
      if (hasChanged || r0.nonEmpty)
        MSome(o2(tpe = r0.getOrElse(o2.tpe)))
      else
        MNone()
    } else if (preR.resultOpt.nonEmpty) {
      MSome(preR.resultOpt.getOrElse(o))
    } else {
      MNone()
    }
    val hasChanged: B = r.nonEmpty
    val o2: Typed.Method = r.getOrElse(o)
    val postR: MOption[Typed.Method] = postTypedMethod(o2) match {
     case MSome(result: Typed.Method) => MSome[Typed.Method](result)
     case MSome(_) => halt("Can only produce object of type Typed.Method")
     case _ => MNone[Typed.Method]()
    }
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return MSome(o2)
    } else {
      return MNone()
    }
  }

}