// #Sireum
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

package org.sireum.lang

import org.sireum._
import org.sireum.lang.symbol.TypeInfo
import org.sireum.lang.tipe.TypeHierarchy
import org.sireum.lang.{ast => AST}
import org.sireum.U32._

object IRTranslator {
  @msig trait Fresh {
    def setLabel(n: Z): Unit
    def setTemp(n: Z): Unit
    def label(): Z
    def temp(): Z
  }

  @ext("IRTranslatorFreshAtomic") object Ext {
    @pure def createFresh: Fresh = $
  }

  @strictpure def createFresh: Fresh = Ext.createFresh
}

@record class IRTranslator(val threeAddressCode: B,
                           val threeAddressCodeLit: B,
                           val th: TypeHierarchy,
                           val fresh: IRTranslator.Fresh) {

  var methodContext: AST.IR.MethodContext = AST.IR.MethodContext.empty
  var stmts: ISZ[AST.IR.Stmt] = ISZ()

  def translateMethodH(isBasic: B,
                       receiverTypeOpt: Option[AST.Typed],
                       owner: ISZ[String],
                       id: String,
                       typeParams: ISZ[String],
                       params: ISZ[String],
                       funType: AST.Typed.Fun,
                       pos: message.Position,
                       bodyOpt: Option[AST.Body]): AST.IR.Procedure = {
    val isInObject = receiverTypeOpt.isEmpty
    var t: AST.Typed.Fun = funType
    var paramNames = params
    if (!isInObject) {
      paramNames = "this" +: paramNames
      t = t(args = receiverTypeOpt.get +: t.args)
    }
    methodContext = AST.IR.MethodContext(isInObject, owner, id, t)
    var body: AST.IR.Body = bodyOpt match {
      case Some(body) =>
        val oldStmts = stmts
        stmts = ISZ()
        translateBody(body, None())
        val b = AST.IR.Body.Block(AST.IR.Stmt.Block(stmts, bodyPos(body, pos)))
        stmts = oldStmts
        b
      case _ => AST.IR.Body.Block(AST.IR.Stmt.Block(ISZ(), pos))
    }
    if (isBasic) {
      body = toBasic(body.asInstanceOf[AST.IR.Body.Block], pos)
    }
    return AST.IR.Procedure(isInObject, typeParams, owner, id, paramNames, t, body, pos)
  }

  def translateMethod(isBasic: B,
                      receiverTypeOpt: Option[AST.Typed],
                      owner: ISZ[String],
                      method: AST.Stmt.Method): AST.IR.Procedure = {
    val typeParams: ISZ[String] = for (tp <- method.sig.typeParams) yield tp.id.value
    val paramNames: ISZ[String] = for (p <- method.sig.params) yield p.id.value
    return translateMethodH(isBasic, receiverTypeOpt, owner, method.sig.id.value,
      typeParams, paramNames, method.sig.funType, method.sig.id.attr.posOpt.get, method.bodyOpt)
  }

  def toBasic(body: AST.IR.Body.Block, pos: message.Position): AST.IR.Body.Basic = {

    var blocks = ISZ[AST.IR.BasicBlock]()
    var grounds = ISZ[AST.IR.Stmt.Ground]()
    var decls = ISZ[AST.IR.Stmt.Decl]()

    def addGround(g: AST.IR.Stmt.Ground): Unit = {
      grounds = grounds :+ g
    }

    val initLabel = fresh.label()

    @pure def basicBlock(label: Z, stmts: ISZ[AST.IR.Stmt.Ground], jump: AST.IR.Jump): AST.IR.BasicBlock = {
      return AST.IR.BasicBlock(label, stmts, jump)
    }

    def stmtToBasic(label: Z, stmt: AST.IR.Stmt): Option[Z] = {
      stmt match {
        case stmt: AST.IR.Stmt.Block =>
          return blockToBasic(label, stmt)
        case stmt: AST.IR.Stmt.Expr =>
          addGround(stmt)
          return Some(label)
        case stmt: AST.IR.Stmt.Decl =>
          addGround(stmt)
          decls = decls :+ stmt
          return Some(label)
        case stmt: AST.IR.Stmt.Assign =>
          addGround(stmt)
          return Some(label)
        case stmt: AST.IR.Stmt.Assertume =>
          val tLabel = fresh.label()
          var fLabel = fresh.label()
          blocks = blocks :+ AST.IR.BasicBlock(label, grounds, AST.IR.Jump.If(stmt.cond, tLabel, fLabel, stmt.pos))
          grounds = ISZ()
          var addF = T
          stmt.messageOpt match {
            case Some(m) =>
              stmtToBasic(fLabel, AST.IR.Stmt.Block(m.stmts, m.exp.pos)) match {
                case Some(l) =>
                  fLabel = l
                  stmtToBasic(fLabel, AST.IR.Stmt.Print(AST.IR.Stmt.Print.Kind.Err, T, ISZ(m.exp), m.exp.pos)) match {
                    case Some(l) => fLabel = l
                    case _ => addF = F
                  }
                case _ => addF = F
              }
            case _ =>
          }
          if (addF) {
            blocks = blocks :+ AST.IR.BasicBlock(fLabel, grounds, AST.IR.Jump.Halt(pos))
          }
          return Some(tLabel)
        case stmt: AST.IR.Stmt.Halt =>
          stmtToBasic(label, AST.IR.Stmt.Print(AST.IR.Stmt.Print.Kind.Err, T, ISZ(stmt.message), pos)) match {
            case Some(l) =>
              blocks = blocks :+ AST.IR.BasicBlock(l, grounds, AST.IR.Jump.Halt(pos))
              grounds = ISZ()
            case _ =>
          }
          return None()
        case stmt: AST.IR.Stmt.Print =>
          var args = ISZ[AST.IR.Exp]()
          var i: Z = 0
          val id: String = stmt.kind match {
            case AST.IR.Stmt.Print.Kind.Out => "print"
            case AST.IR.Stmt.Print.Kind.Err => "eprint"
            case AST.IR.Stmt.Print.Kind.OutErr =>
               args = args :+ stmt.args(0)
               i = 1
              "cprint"
          }
          for (j <- i until stmt.args.size) {
            val arg = stmt.args(j)
            grounds = grounds :+ AST.IR.Stmt.Expr(AST.IR.Exp.Apply(T, AST.Typed.sireumName, id, args :+ stmt.args(j),
              AST.Typed.Fun(AST.Purity.Impure, F, ISZ(arg.tipe), AST.Typed.unit), AST.Typed.unit, arg.pos))
          }
          if (stmt.line) {
            grounds = grounds :+ AST.IR.Stmt.Expr(AST.IR.Exp.Apply(T, AST.Typed.sireumName, id, args :+
              AST.IR.Exp.Int(AST.Typed.c, 10, stmt.pos), AST.Typed.Fun(AST.Purity.Impure, F, ISZ(AST.Typed.c),
              AST.Typed.unit), AST.Typed.unit, stmt.pos))
          }
          return Some(label)
        case stmt: AST.IR.Stmt.Match =>
          if (isScalar(stmt.exp.tipe) && ops.ISZOps(stmt.cases).forall((c : AST.IR.Stmt.Match.Case) =>
            c.condOpt.isEmpty && c.decl.locals.isEmpty && (c.pattern.isInstanceOf[AST.Pattern.LitInterpolate] ||
              c.pattern.isInstanceOf[AST.Pattern.Literal] || c.pattern.isInstanceOf[AST.Pattern.Wildcard]))) {
            def litOf(pattern: AST.Pattern): Option[AST.IR.Exp] = {
              pattern match {
                case _: AST.Pattern.Wildcard => return None()
                case pattern: AST.Pattern.Literal => return Some(translateExp(pattern.lit))
                case pattern: AST.Pattern.LitInterpolate =>
                  val t = pattern.attr.typedOpt.get
                  val ppos = pattern.posOpt.get
                  t match {
                    case AST.Typed.z => return Some(AST.IR.Exp.Int(t, Z(pattern.value).get, ppos))
                    case AST.Typed.c => return Some(AST.IR.Exp.Int(t, conversions.String.toCis(pattern.value)(0).toZ, ppos))
                    case AST.Typed.f32 => return Some(AST.IR.Exp.F32(F32(pattern.value).get, ppos))
                    case AST.Typed.f64 => return Some(AST.IR.Exp.F64(F64(pattern.value).get, ppos))
                    case AST.Typed.r => return Some(AST.IR.Exp.R(R(pattern.value).get, ppos))
                    case _ if isSubZ(t) => return Some(AST.IR.Exp.Int(t, Z(pattern.value).get, ppos))
                    case _ => halt(s"Infeasible: $pattern")
                  }
                case _ => halt(s"Infeasible: $pattern")
              }
            }
            val labels: ISZ[Z] = for (_ <- stmt.cases.indices) yield fresh.label()
            val end = fresh.label()
            var cases = ISZ[AST.IR.Jump.Switch.Case]()
            var defaultOpt = Option.none[Z]()
            for (i <- labels.indices) {
              litOf(stmt.cases(i).pattern) match {
                case Some(caseExp) => cases = cases :+ AST.IR.Jump.Switch.Case(caseExp, labels(i))
                case _ => defaultOpt = Some(labels(i))
              }
            }
            blocks = blocks :+ AST.IR.BasicBlock(label, grounds, AST.IR.Jump.Switch(stmt.exp, cases, defaultOpt, pos))
            for (i <- labels.indices) {
              grounds = ISZ()
              stmtToBasic(labels(i), stmt.cases(i).body) match {
                case Some(l) => blocks = blocks :+ AST.IR.BasicBlock(l, grounds, AST.IR.Jump.Goto(end, pos))
                case _ =>
              }
            }
            return Some(end)
          } else {
            halt(s"TODO: $stmt")
          }
        case stmt: AST.IR.Stmt.AssignPattern => halt(s"TODO: $stmt")
        case stmt: AST.IR.Stmt.For => halt(s"TODO: $stmt")
        case stmt: AST.IR.Stmt.If =>
          val t = fresh.label()
          val e = fresh.label()
          val f: Z = if (stmt.elseBlock.stmts.isEmpty) e else fresh.label()
          blocks = blocks :+ basicBlock(label, grounds, AST.IR.Jump.If(stmt.cond, t, f, stmt.pos))
          grounds = ISZ()
          var allReturn = T
          blockToBasic(t, stmt.thenBlock) match {
            case Some(l) =>
              blocks = blocks :+ basicBlock(l, grounds, AST.IR.Jump.Goto(e, stmt.pos))
              allReturn = F
            case _ =>
          }
          if (stmt.elseBlock.stmts.nonEmpty) {
            grounds = ISZ()
            blockToBasic(f, stmt.elseBlock) match {
              case Some(l) =>
                blocks = blocks :+ basicBlock(l, grounds, AST.IR.Jump.Goto(e, stmt.pos))
                allReturn = F
              case _ =>
            }
          } else {
            allReturn = F
          }
          grounds = ISZ()
          return if (allReturn) None() else Some(e)
        case stmt: AST.IR.Stmt.While =>
          val n = fresh.label()
          blocks = blocks :+ basicBlock(label, grounds, AST.IR.Jump.Goto(n, stmt.pos))
          grounds = ISZ()
          blockToBasic(n, AST.IR.Stmt.Block(stmt.cond.stmts, stmt.cond.exp.pos)) match {
            case Some(l) =>
              val t = fresh.label()
              val e = fresh.label()
              blocks = blocks :+ basicBlock(l, grounds, AST.IR.Jump.If(stmt.cond.exp, t, e, stmt.pos))
              grounds = ISZ()
              blockToBasic(t, stmt.block) match {
                case Some(l) => blocks = blocks :+ basicBlock(l, grounds, AST.IR.Jump.Goto(n, stmt.pos))
                case _ =>
              }
              grounds = ISZ()
              return Some(e)
            case _ =>
              return None()
          }
        case stmt: AST.IR.Stmt.Return =>
          blocks = blocks :+ basicBlock(label, grounds, AST.IR.Jump.Return(stmt.expOpt, pos))
          grounds = ISZ()
          return None()
        case stmt: AST.IR.Stmt.Intrinsic =>
          addGround(stmt)
          return Some(label)
      }
    }

    def blockToBasic(label: Z, block: AST.IR.Stmt.Block): Option[Z] = {
      val oldDecls = decls
      decls = ISZ()
      var l = label
      for (stmt <- block.stmts) {
        stmtToBasic(l, stmt) match {
          case Some(next) => l = next
          case _ => return None()
        }
      }
      for (d <- decls) {
        addGround(d.undeclare)
      }
      decls = oldDecls
      return Some(l)
    }

    blockToBasic(initLabel, body.block) match {
      case Some(l) => blocks = blocks :+ basicBlock(l, grounds, AST.IR.Jump.Return(None(), pos))
      case _ =>
    }
    return AST.IR.Body.Basic(blocks)
  }

  def translateExpBlock(exp: AST.Exp): AST.IR.ExpBlock = {
    val oldStmts = stmts
    stmts = ISZ()
    val e = translateExp(exp)
    val r = AST.IR.ExpBlock(stmts, e)
    fresh.setTemp(0)
    stmts = oldStmts
    return r
  }

  def translateStmt(stmt: AST.Stmt, localOpt: Option[(String, AST.Typed)]): Unit = {
    def assignRhs(lhsType: AST.Typed, rhs: AST.AssignExp): AST.IR.Exp = {
      rhs match {
        case rhs: AST.Stmt.Expr => return translateExp(rhs.exp)
        case _ =>
          val aePos = rhs.asStmt.posOpt.get
          val id = assignExpId(None(), aePos)
          stmts = stmts :+ AST.IR.Stmt.Decl(F, T, F, methodContext, ISZ(AST.IR.Stmt.Decl.Local(id, lhsType)), aePos)
          translateAssignExp(rhs, (id, lhsType))
          return AST.IR.Exp.LocalVarRef(T, methodContext, id, lhsType, aePos)
      }
    }
    val pos = stmt.posOpt.get
    stmt match {
      case stmt: AST.Stmt.Var =>
        val init = stmt.initOpt.get
        var oldStmts = stmts
        stmts = ISZ()
        val t = stmt.attr.typedOpt.get
        val varRhs: AST.IR.Exp = init match {
          case init: AST.Stmt.Expr => translateExp(init.exp)
          case _ =>
            val aePos = init.asStmt.posOpt.get
            val id = assignExpId(Some(stmt.id.value), aePos)
            stmts = stmts :+ AST.IR.Stmt.Decl(F, T, F, methodContext, ISZ(AST.IR.Stmt.Decl.Local(id, t)), aePos)
            translateAssignExp(init, (id, t))
            AST.IR.Exp.LocalVarRef(T, methodContext, id, t, aePos)
        }
        stmts = stmts :+ AST.IR.Stmt.Assign.Local(methodContext, stmt.id.value, t, varRhs, pos)
        oldStmts = oldStmts :+ AST.IR.Stmt.Decl(F, stmt.isVal, F, methodContext, ISZ(AST.IR.Stmt.Decl.Local(stmt.id.value, t)), pos)
        stmts = oldStmts ++ stmts
        fresh.setTemp(0)
      case stmt: AST.Stmt.Assign =>
        val oldStmts = stmts
        stmts = ISZ()
        stmt.lhs match {
          case lhs: AST.Exp.Ident =>
            lhs.resOpt.get match {
              case _: AST.ResolvedInfo.LocalVar =>
                val rhs = assignRhs(lhs.typedOpt.get, stmt.rhs)
                stmts = stmts :+ AST.IR.Stmt.Assign.Local(methodContext, lhs.id.value, lhs.typedOpt.get, rhs, pos)
              case res: AST.ResolvedInfo.Var =>
                if (res.isInObject) {
                  val rhs = assignRhs(lhs.typedOpt.get, stmt.rhs)
                  stmts = stmts :+ AST.IR.Stmt.Assign.Global(res.owner :+ res.id, lhs.typedOpt.get, rhs, pos)
                } else {
                  val receiverPos = lhs.posOpt.get
                  val thiz = AST.IR.Exp.LocalVarRef(T, methodContext, "this", methodContext.receiverType, receiverPos)
                  val receiver: AST.IR.Exp = if (threeAddressCode) {
                    val n = fresh.temp()
                    stmts = stmts :+ AST.IR.Stmt.Assign.Temp(n, thiz, receiverPos)
                    AST.IR.Exp.Temp(n, methodContext.receiverType, receiverPos)
                  } else {
                    thiz
                  }
                  val rhs = assignRhs(lhs.typedOpt.get, stmt.rhs)
                  stmts = stmts :+ AST.IR.Stmt.Assign.Field(receiver, lhs.id.value, lhs.typedOpt.get, rhs, pos)
                }
              case res => halt(s"Infeasible: $res")
            }
          case lhs: AST.Exp.Select =>
            def selectRhs(): AST.IR.Exp = {
              stmt.rhs match {
                case rhs: AST.Stmt.Expr => return translateExp(rhs.exp)
                case _ =>
                  val aePos = stmt.rhs.asStmt.posOpt.get
                  val id = assignExpId(None(), aePos)
                  val t = stmt.lhs.typedOpt.get
                  stmts = stmts :+ AST.IR.Stmt.Decl(F, T, F, methodContext, ISZ(AST.IR.Stmt.Decl.Local(id, t)), aePos)
                  translateAssignExp(stmt.rhs, (id, t))
                  return AST.IR.Exp.LocalVarRef(T, methodContext, id, t, aePos)
              }
            }

            lhs.resOpt.get match {
              case res: AST.ResolvedInfo.Var if res.isInObject =>
                stmts = stmts :+ AST.IR.Stmt.Assign.Global(res.owner :+ res.id, lhs.typedOpt.get, selectRhs(), pos)
              case _ =>
                val rcv = lhs.receiverOpt.get
                val receiver = translateExp(rcv)
                stmts = stmts :+ AST.IR.Stmt.Assign.Field(receiver, lhs.id.value, lhs.typedOpt.get, selectRhs(), pos)
            }
          case lhs: AST.Exp.Invoke =>
            val rcv = lhs.receiverOpt.get
            val receiver = translateExp(rcv)
            val index = translateExp(lhs.args(0))
            val invokeRhs: AST.IR.Exp = stmt.rhs match {
              case rhs: AST.Stmt.Expr => translateExp(rhs.exp)
              case _ =>
                val aePos = stmt.rhs.asStmt.posOpt.get
                val id = assignExpId(None(), aePos)
                val t = stmt.lhs.typedOpt.get
                stmts = stmts :+ AST.IR.Stmt.Decl(F, T, F, methodContext, ISZ(AST.IR.Stmt.Decl.Local(id, t)), aePos)
                translateAssignExp(stmt.rhs, (id, t))
                AST.IR.Exp.LocalVarRef(T, methodContext, id, t, aePos)
            }
            stmts = stmts :+ AST.IR.Stmt.Assign.Index(receiver, index, invokeRhs, pos)
          case _ => halt("Infeasible")
        }
        stmts = oldStmts ++ stmts
        fresh.setTemp(0)
      case stmt: AST.Stmt.If =>
        val oldStmts = stmts
        stmts = ISZ()
        val cond = translateExp(stmt.cond)
        val condStmts = stmts
        fresh.setTemp(0)
        stmts = ISZ()
        translateBody(stmt.thenBody, localOpt)
        val thenPos = bodyPos(stmt.thenBody, pos)
        val thenStmts = stmts
        fresh.setTemp(0)
        stmts = ISZ()
        translateBody(stmt.elseBody, localOpt)
        val elsePos = bodyPos(stmt.elseBody, pos)
        val elseStmts = stmts
        stmts = oldStmts ++ condStmts :+
          AST.IR.Stmt.If(cond, AST.IR.Stmt.Block(thenStmts, thenPos), AST.IR.Stmt.Block(elseStmts, elsePos), pos)
        fresh.setTemp(0)
      case stmt: AST.Stmt.While =>
        val cond = translateExpBlock(stmt.cond)
        val oldStmts = stmts
        stmts = ISZ()
        translateBody(stmt.body, None())
        val bPos = bodyPos(stmt.body, pos)
        stmts = oldStmts :+ AST.IR.Stmt.While(cond, AST.IR.Stmt.Block(stmts, bPos), pos)
        fresh.setTemp(0)
      case stmt: AST.Stmt.Expr =>
        stmt.exp match {
          case e: AST.Exp.Invoke =>
            var isPrint: B = F
            var isLine: B = F
            var printKind: AST.IR.Stmt.Print.Kind.Type = AST.IR.Stmt.Print.Kind.Out
            var isAssert: B = F
            var isAssume: B = F
            var isHalt: B = F
            e.ident.attr.resOpt.get match {
              case AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Print) =>
                isPrint = T
              case AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Println) =>
                isPrint = T
                isLine = T
              case AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Eprint) =>
                isPrint = T
                printKind = AST.IR.Stmt.Print.Kind.Err
              case AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Eprintln) =>
                isPrint = T
                isLine = T
                printKind = AST.IR.Stmt.Print.Kind.Err
              case AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Cprint) =>
                isPrint = T
                printKind = AST.IR.Stmt.Print.Kind.OutErr
              case AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Cprintln) =>
                isPrint = T
                isLine = T
                printKind = AST.IR.Stmt.Print.Kind.OutErr
              case AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Assert) =>
                isAssert = T
              case AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.AssertMsg) =>
                isAssert = T
              case AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Assume) =>
                isAssume = T
              case AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.AssumeMsg) =>
                isAssume = T
              case AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.Halt) =>
                isHalt = T
              case _ =>
            }
            if (isPrint) {
              var args = ISZ[AST.IR.Exp]()
              for (j <- e.args.indices) {
                val arg = translateExp(e.args(j))
                args = args :+ arg
              }
              stmts = stmts :+ AST.IR.Stmt.Print(printKind, isLine, args, pos)
              fresh.setTemp(0)
              return
            } else if (isAssert || isAssume) {
              val cond = translateExp(e.args(0))
              fresh.setTemp(0)
              val messageOpt: Option[AST.IR.ExpBlock] =
                if (e.args.size == 2) Some(translateExpBlock(e.args(1)))
                else None()
              stmts = stmts :+ AST.IR.Stmt.Assertume(isAssert, cond, messageOpt, pos)
              fresh.setTemp(0)
              return
            } else if (isHalt) {
              val msg = translateExp(e.args(0))
              stmts = stmts :+ AST.IR.Stmt.Halt(msg, pos)
              fresh.setTemp(0)
              return
            }
          case _ =>
        }
        val e = translateExp(stmt.exp)
        if (e.tipe == AST.Typed.unit || e.tipe == AST.Typed.nothing) {
          stmts = stmts :+ AST.IR.Stmt.Expr(e.asInstanceOf[AST.IR.Exp.Apply])
        }
        fresh.setTemp(0)
      case stmt: AST.Stmt.Return =>
        stmt.expOpt match {
          case Some(exp) =>
            val r = translateExp(exp)
            stmts = stmts :+ AST.IR.Stmt.Return(Some(r), pos)
          case _ =>
            stmts = stmts :+ AST.IR.Stmt.Return(None(), pos)
        }
        fresh.setTemp(0)
      case stmt: AST.Stmt.Block =>
        val oldStmts = stmts
        stmts = ISZ()
        translateBody(stmt.body, localOpt)
        stmts = oldStmts :+ AST.IR.Stmt.Block(stmts, stmt.posOpt.get)
        fresh.setTemp(0)
      case stmt: AST.Stmt.Match =>
        val exp = translateExp(stmt.exp)
        fresh.setTemp(0)
        var cases = ISZ[AST.IR.Stmt.Match.Case]()
        val oldStmts = stmts
        for (c <- stmt.cases) {
          stmts = ISZ()
          val decl = patternDecl(methodContext, c.pattern)
          c.condOpt match {
            case Some(cond) =>
              val condExp = translateExpBlock(cond)
              translateBody(c.body, localOpt)
              val block = AST.IR.Stmt.Block(stmts, pos)
              fresh.setTemp(0)
              cases = cases :+ AST.IR.Stmt.Match.Case(decl, c.pattern, Some(condExp), block)
            case _ =>
              translateBody(c.body, localOpt)
              val block = AST.IR.Stmt.Block(stmts, stmt.posOpt.get)
              fresh.setTemp(0)
              cases = cases :+ AST.IR.Stmt.Match.Case(decl, c.pattern, None(), block)
          }
        }
        stmts = oldStmts :+ AST.IR.Stmt.Match(exp, cases, pos)
      case stmt: AST.Stmt.For => halt(s"TODO: $stmt")
      case stmt: AST.Stmt.VarPattern =>
        val oldStmts = stmts
        stmts = ISZ()
        val init = assignRhs(stmt.pattern.typedOpt.get, stmt.init)
        stmts = stmts :+ patternDecl(methodContext, stmt.pattern)
        stmts = stmts :+ AST.IR.Stmt.AssignPattern(methodContext, stmt.pattern, init, pos)
        stmts = oldStmts ++ stmts
      case _: AST.Stmt.SubZ => // skip
      case _: AST.Stmt.Method => // skip
      case _: AST.Stmt.ExtMethod => // skip
      case _: AST.Stmt.Enum => // skip
      case _: AST.Stmt.Sig => // skip
      case _: AST.Stmt.Adt => // skip
      case _: AST.Stmt.Object => // skip
      case _: AST.Stmt.Import => // skip
      case _: AST.Stmt.TypeAlias => // skip
      case _: AST.Stmt.Spec => // skip
    }

  }

  def patternDecl(context: AST.IR.MethodContext, pattern: AST.Pattern): AST.IR.Stmt.Decl = {
    var r = ISZ[AST.IR.Stmt.Decl.Local]()
    def rec(p: AST.Pattern): Unit = {
      p match {
        case p: AST.Pattern.VarBinding => r = r :+ AST.IR.Stmt.Decl.Local(p.id.value, p.attr.typedOpt.get)
        case p: AST.Pattern.Structure =>
          p.idOpt match {
            case Some(id) => r = r :+ AST.IR.Stmt.Decl.Local(id.value, p.attr.typedOpt.get)
            case _ =>
          }
          for (sub <- p.patterns) {
            rec(sub)
          }
        case _: AST.Pattern.Ref => // skip
        case _: AST.Pattern.Literal => // skip
        case _: AST.Pattern.Wildcard => // skip
        case _: AST.Pattern.LitInterpolate => // skip
        case _: AST.Pattern.SeqWildcard => // skip
      }
    }
    rec(pattern)
    return AST.IR.Stmt.Decl(F, T, F, context, r, pattern.posOpt.get)
  }

  @pure def bodyPos(body: AST.Body, default: message.Position): message.Position = {
    if (body.stmts.isEmpty) {
      return default
    }
    return body.stmts(0).posOpt.get.to(body.stmts(body.stmts.size - 1).posOpt.get)
  }

  def translateBody(body: AST.Body, localOpt: Option[(String, AST.Typed)]): Unit = {
    val stmts = body.stmts
    localOpt match {
      case Some((_, _)) =>
        for (i <- 0 until stmts.size - 1) {
          translateStmt(stmts(i), None())
        }
        translateAssignExp(stmts(stmts.size - 1).asAssignExp, localOpt.get)
      case _ =>
        for (stmt <- body.stmts) {
          translateStmt(stmt, None())
        }
    }
  }

  def translateAssignExp(stmt: AST.AssignExp, local: (String, AST.Typed)): Unit = {
    val pos = stmt.asStmt.posOpt.get
    stmt match {
      case stmt: AST.Stmt.Expr =>
        val exp = translateExp(stmt.exp)
        stmts = stmts :+ AST.IR.Stmt.Assign.Local(methodContext, local._1, local._2, exp, pos)
      case _ => translateStmt(stmt.asStmt, Some(local))
    }
  }

  @memoize def isSubZ(t: AST.Typed): B = {
    t match {
      case t: AST.Typed.Name if t.args.isEmpty =>
        th.typeMap.get(t.ids) match {
          case Some(_: TypeInfo.SubZ) => return T
          case _ =>
        }
      case _ =>
    }
    return F
  }

  @memoize def isScalar(t: AST.Typed): B = {
    t match {
      case AST.Typed.b =>
      case AST.Typed.c =>
      case AST.Typed.z =>
      case AST.Typed.f32 =>
      case AST.Typed.f64 =>
      case AST.Typed.r =>
      case _ => return isSubZ(t)
    }
    return T
  }

  @memoize def isSeq(t: AST.Typed): B = {
    t match {
      case t: AST.Typed.Name => return t.ids == AST.Typed.isName || t.ids == AST.Typed.msName
      case _ =>
    }
    return F
  }

  @memoize def shouldCopy(t: AST.Typed): B = {
    if (isScalar(t)) {
      return F
    }
    t match {
      case t: AST.Typed.Name =>
        t.ids match {
          case AST.Typed.msName => return T
          case AST.Typed.isName =>
          case _ =>
        }
        th.typeMap.get(t.ids) match {
          case Some(info: TypeInfo.Adt) => return !info.ast.isDatatype
          case Some(info: TypeInfo.Sig) => return !info.ast.isImmutable
          case _ =>
        }
      case _ =>
    }
    return F
  }

  def translateExp(exp: AST.Exp): AST.IR.Exp = {
    @strictpure def isLit(e: AST.IR.Exp): B = e match {
      case _: AST.IR.Exp.Bool => T
      case _: AST.IR.Exp.Int => T
      case _: AST.IR.Exp.F32 => T
      case _: AST.IR.Exp.F64 => T
      case _: AST.IR.Exp.R => T
      case _: AST.IR.Exp.String => T
      case _ => F
    }
    def norm3AC(r: AST.IR.Exp): AST.IR.Exp = {
      val e: AST.IR.Exp = r match {
        case r: AST.IR.Exp.GlobalVarRef =>
          if (r.name == AST.Typed.sireumName :+ "T") AST.IR.Exp.Bool(T, r.pos)
          else if (r.name == AST.Typed.sireumName :+ "F") AST.IR.Exp.Bool(F, r.pos)
          else r
        case _ => r
      }
      if (threeAddressCode && e.tipe != AST.Typed.unit && e.tipe != AST.Typed.nothing) {
        if (isLit(e) && threeAddressCodeLit) {
          val n = fresh.temp()
          stmts = stmts :+ AST.IR.Stmt.Assign.Temp(n, e, e.pos)
          return AST.IR.Exp.Temp(n, e.tipe, e.pos)
        }
      }
      return e
    }

    def thiz(pos: message.Position): AST.IR.Exp = {
      return norm3AC(AST.IR.Exp.LocalVarRef(T, methodContext, "this", methodContext.receiverType, pos))
    }

    val pos = exp.posOpt.get
    exp match {
      case exp: AST.Exp.LitB => return norm3AC(AST.IR.Exp.Bool(exp.value, pos))
      case exp: AST.Exp.LitC => return norm3AC(AST.IR.Exp.Int(AST.Typed.c, exp.value.toZ, pos))
      case exp: AST.Exp.LitZ => return norm3AC(AST.IR.Exp.Int(AST.Typed.z, exp.value, pos))
      case exp: AST.Exp.LitF32 => return norm3AC(AST.IR.Exp.F32(exp.value, pos))
      case exp: AST.Exp.LitF64 => return norm3AC(AST.IR.Exp.F64(exp.value, pos))
      case exp: AST.Exp.LitR => return norm3AC(AST.IR.Exp.R(exp.value, pos))
      case exp: AST.Exp.LitString => return norm3AC(AST.IR.Exp.String(exp.value, pos))
      case exp: AST.Exp.StringInterpolate =>
        if (isScalar(exp.typedOpt.get)) {
          val t = exp.typedOpt.get.asInstanceOf[AST.Typed.Name]
          val value = Z(exp.lits(0).value).get
          return norm3AC(AST.IR.Exp.Int(t, value, pos))
        } else {
          halt(s"TODO: $exp")
        }
      case _: AST.Exp.This => return thiz(pos)
      case exp: AST.Exp.Ident =>
        val t = exp.typedOpt.get
        exp.resOpt.get match {
          case res: AST.ResolvedInfo.LocalVar =>
            return norm3AC(AST.IR.Exp.LocalVarRef(res.isVal, methodContext, res.id, t, pos))
          case res: AST.ResolvedInfo.Var =>
            if (res.isInObject) {
              return norm3AC(AST.IR.Exp.GlobalVarRef(res.owner :+ res.id, t, pos))
            } else {
              return norm3AC(AST.IR.Exp.FieldVarRef(thiz(pos), res.id, t, pos))
            }
          case res: AST.ResolvedInfo.EnumElement =>
            return norm3AC(AST.IR.Exp.EnumElementRef(res.owner, res.name, res.ordinal, pos))
          case _ => halt(s"Infeasible: $exp")
        }
      case exp: AST.Exp.Select =>
        val t = exp.typedOpt.get
        exp.resOpt.get match {
          case res: AST.ResolvedInfo.Var =>
            if (res.isInObject) {
              return norm3AC(AST.IR.Exp.GlobalVarRef(res.owner :+ res.id, t, pos))
            } else {
              val receiver = exp.receiverOpt.get
              val rcv = translateExp(receiver)
              return norm3AC(AST.IR.Exp.FieldVarRef(rcv, res.id, t, pos))
            }
          case res: AST.ResolvedInfo.EnumElement =>
            return norm3AC(AST.IR.Exp.EnumElementRef(res.owner, res.name, res.ordinal, pos))
          case res: AST.ResolvedInfo.Method =>
            if (isSeq(exp.receiverOpt.get.typedOpt.get)) {
              val receiver = exp.receiverOpt.get
              val rcv = translateExp(receiver)
              return norm3AC(AST.IR.Exp.FieldVarRef(rcv, res.id, res.tpeOpt.get.ret, pos))
            } else {
              return translateExp(AST.Exp.Invoke(exp.receiverOpt, AST.Exp.Ident(exp.id, exp.attr), ISZ(), ISZ(),
                exp.attr(typedOpt = Some(exp.typedOpt.get.asInstanceOf[AST.Typed.Method].tpe.ret))))
            }
          case AST.ResolvedInfo.BuiltIn(kind) if kind == AST.ResolvedInfo.BuiltIn.Kind.AsInstanceOf ||
          kind == AST.ResolvedInfo.BuiltIn.Kind.IsInstanceOf =>
            val receiver = translateExp(exp.receiverOpt.get)
            return norm3AC(AST.IR.Exp.Type(kind == AST.ResolvedInfo.BuiltIn.Kind.IsInstanceOf, receiver,
              exp.targs(0).typedOpt.get.asInstanceOf[AST.Typed.Name], exp.posOpt.get))
          case res => halt(s"TODO: $res")
        }
      case exp: AST.Exp.Unary =>
        val t = exp.typedOpt.get
        if (isScalar(t)) {
          val e = translateExp(exp.exp)
          return norm3AC(AST.IR.Exp.Unary(t, exp.op, e, pos))
        } else {
          halt(s"TODO: $exp")
        }
      case exp: AST.Exp.Binary =>
        val t = exp.typedOpt.get
        if (isScalar(t)) {
          val kind: AST.IR.Exp.Binary.Op.Type = exp.attr.resOpt.get.asInstanceOf[AST.ResolvedInfo.BuiltIn].kind match {
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryAdd => AST.IR.Exp.Binary.Op.Add
            case AST.ResolvedInfo.BuiltIn.Kind.BinarySub => AST.IR.Exp.Binary.Op.Sub
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryMul => AST.IR.Exp.Binary.Op.Mul
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryDiv => AST.IR.Exp.Binary.Op.Div
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryRem => AST.IR.Exp.Binary.Op.Rem
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryAnd => AST.IR.Exp.Binary.Op.And
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryOr => AST.IR.Exp.Binary.Op.Or
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryImply => AST.IR.Exp.Binary.Op.Imply
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryXor => AST.IR.Exp.Binary.Op.Xor
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryEq => AST.IR.Exp.Binary.Op.Eq
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryNe => AST.IR.Exp.Binary.Op.Ne
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryEquiv => AST.IR.Exp.Binary.Op.Eq
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryInequiv => AST.IR.Exp.Binary.Op.Ne
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryFpEq => AST.IR.Exp.Binary.Op.FpEq
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryFpNe => AST.IR.Exp.Binary.Op.FpNe
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryGe => AST.IR.Exp.Binary.Op.Ge
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryGt => AST.IR.Exp.Binary.Op.Gt
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryLe => AST.IR.Exp.Binary.Op.Le
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryLt => AST.IR.Exp.Binary.Op.Lt
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryShr => AST.IR.Exp.Binary.Op.Shr
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryUshr => AST.IR.Exp.Binary.Op.Ushr
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryShl => AST.IR.Exp.Binary.Op.Shl
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryCondAnd =>
              if (threeAddressCode) {
                return translateExp(AST.Exp.If(exp.left, exp.right, AST.Exp.LitB(T, AST.Attr(exp.posOpt)),
                  AST.TypedAttr(exp.posOpt, AST.Typed.bOpt)))
              }
              AST.IR.Exp.Binary.Op.CondAnd
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryCondOr =>
              if (threeAddressCode) {
                return translateExp(AST.Exp.If(exp.left, AST.Exp.LitB(T, AST.Attr(exp.posOpt)), exp.right,
                  AST.TypedAttr(exp.posOpt, AST.Typed.bOpt)))
              }
              AST.IR.Exp.Binary.Op.CondOr
            case AST.ResolvedInfo.BuiltIn.Kind.BinaryCondImply =>
              if (threeAddressCode) {
                return translateExp(AST.Exp.If(exp.left, exp.right, AST.Exp.LitB(F, AST.Attr(exp.posOpt)),
                  AST.TypedAttr(exp.posOpt, AST.Typed.bOpt)))
              }
              AST.IR.Exp.Binary.Op.CondImply
            case _ => halt(s"Infeasible: ${exp.attr.resOpt.get}")
          }
          val left = translateExp(exp.left)
          val right = translateExp(exp.right)
          return norm3AC(AST.IR.Exp.Binary(t, left, kind, right, pos))
        }
        if (isSeq(t)) {
          val kindOpt: Option[AST.IR.Exp.Binary.Op.Type] = exp.op match {
            case AST.Exp.BinaryOp.Append => Some(AST.IR.Exp.Binary.Op.Append)
            case AST.Exp.BinaryOp.Prepend => Some(AST.IR.Exp.Binary.Op.Prepend)
            case AST.Exp.BinaryOp.AppendAll => Some(AST.IR.Exp.Binary.Op.AppendAll)
            case _ => None()
          }
          kindOpt match {
            case Some(kind) =>
              val left = translateExp(exp.left)
              val right = translateExp(exp.right)
              return norm3AC(AST.IR.Exp.Binary(t, left, kind, right, pos))
            case _ =>
          }
        }
        halt(s"TODO: $exp")
      case exp: AST.Exp.If =>
        val t = exp.typedOpt.get
        val cond = translateExp(exp.cond)
        if (!threeAddressCode) {
          val thenExp = translateExp(exp.thenExp)
          val elseExp = translateExp(exp.elseExp)
          return AST.IR.Exp.If(cond, thenExp, elseExp, t, pos)
        }
        val n = fresh.temp()
        val oldStmts = stmts
        stmts = ISZ()
        val thenExp = translateExp(exp.thenExp)
        val thenStmts = stmts
        val thenPos = exp.thenExp.posOpt.get
        stmts = ISZ()
        val elseExp = translateExp(exp.elseExp)
        val elseStmts = stmts
        val elsePos = exp.elseExp.posOpt.get
        stmts = oldStmts
        stmts = stmts :+ AST.IR.Stmt.If(cond,
          AST.IR.Stmt.Block(thenStmts :+ AST.IR.Stmt.Assign.Temp(n, thenExp, thenPos), thenPos),
          AST.IR.Stmt.Block(elseStmts :+ AST.IR.Stmt.Assign.Temp(n, elseExp, elsePos), elsePos), pos)
        return AST.IR.Exp.Temp(n, t, pos)
      case exp: AST.Exp.Invoke =>
        exp.attr.resOpt.get match {
          case res: AST.ResolvedInfo.Method =>
            res.mode match {
              case AST.MethodMode.Method =>
                var args = ISZ[AST.IR.Exp]()
                var methodType = res.tpeOpt.get
                exp.receiverOpt match {
                  case Some(receiver) =>
                    args = args :+ translateExp(receiver)
                    methodType = methodType(args = receiver.typedOpt.get +: methodType.args)
                  case _ =>
                }
                for (arg <- exp.args) {
                  args = args :+ translateExp(arg)
                }
                return norm3AC(AST.IR.Exp.Apply(res.isInObject, res.owner, res.id, args, methodType, exp.typedOpt.get, pos))
              case AST.MethodMode.Ext =>
                var args = ISZ[AST.IR.Exp]()
                for (arg <- exp.args) {
                  args = args :+ translateExp(arg)
                }
                return norm3AC(AST.IR.Exp.Apply(T, res.owner, res.id, args, res.tpeOpt.get, exp.typedOpt.get, pos))
              case AST.MethodMode.Select =>
                val (rcv, rcvType): (AST.IR.Exp, AST.Typed.Name) = exp.receiverOpt match {
                  case Some(receiver) =>
                    if (exp.ident.id.value == "apply") {
                      (translateExp(receiver), receiver.typedOpt.get.asInstanceOf[AST.Typed.Name])
                    } else {
                      val e = AST.Exp.Select(Some(receiver), exp.ident.id, exp.targs, exp.ident.attr)
                      (translateExp(e), e.typedOpt.get.asInstanceOf[AST.Typed.Name])
                    }
                  case _ => (translateExp(exp.ident), exp.ident.typedOpt.get.asInstanceOf[AST.Typed.Name])
                }
                val index = translateExp(exp.args(0))
                return norm3AC(AST.IR.Exp.Indexing(rcv, rcvType, index, pos))
              case AST.MethodMode.Constructor =>
                var args = ISZ[AST.IR.Exp]()
                for (arg <- exp.args) {
                  args = args :+ translateExp(arg)
                }
                return norm3AC(AST.IR.Exp.Construct(exp.typedOpt.get.asInstanceOf[AST.Typed.Name], args, exp.posOpt.get))
              case _ => halt(s"TODO: $exp")
            }
          case _ => halt(s"TODO: $exp")
        }
      case exp: AST.Exp.InvokeNamed => halt(s"TODO: $exp")
      case exp: AST.Exp.Tuple => halt(s"TODO: $exp")
      case exp: AST.Exp.ForYield => halt(s"TODO: $exp")
      case exp: AST.Exp.Eta => halt(s"TODO: $exp")
      case exp: AST.Exp.Fun => halt(s"TODO: $exp")
      case exp: AST.Exp.QuantEach => halt(s"TODO: $exp")
      case exp: AST.Exp.QuantRange => halt(s"TODO: $exp")
      case exp: AST.Exp.StrictPureBlock => halt(s"TODO: $exp")
      case exp: AST.Exp.Super => halt(s"TODO: $exp")
      case exp: AST.Exp.Labeled => return translateExp(exp.exp)
      case exp: AST.Exp.QuantType => halt(s"Infeasible: $exp")
      case exp: AST.Exp.AssertAgree => halt(s"Infeasible: $exp")
      case exp: AST.Exp.AssumeAgree => halt(s"Infeasible: $exp")
      case exp: AST.Exp.At => halt(s"Infeasible: $exp")
      case exp: AST.Exp.InfoFlowInvariant => halt(s"Infeasible: $exp")
      case exp: AST.Exp.Input => halt(s"Infeasible: $exp")
      case exp: AST.Exp.LoopIndex => halt(s"Infeasible: $exp")
      case exp: AST.Exp.Old => halt(s"Infeasible: $exp")
      case exp: AST.Exp.RS => halt(s"Infeasible: $exp")
      case exp: AST.Exp.Result => halt(s"Infeasible: $exp")
      case exp: AST.Exp.StateSeq => halt(s"Infeasible: $exp")
      case exp: AST.Exp.Sym => halt(s"Infeasible: $exp")
      case exp: AST.Exp.TypeCond => halt(s"Infeasible: $exp")
      case exp: AST.ProofAst.StepId => halt(s"Infeasible: $exp")
    }
  }

  @pure def sha3(s: String): U32 = {
    val sha = crypto.SHA3.init512
    sha.update(conversions.String.toU8is(s))
    val bs = sha.finalise()
    return conversions.U8.toU32(bs(0)) << u32"24" | conversions.U8.toU32(bs(1)) << u32"16" |
      conversions.U8.toU32(bs(2)) << u32"8" | conversions.U8.toU32(bs(3))
  }

  @strictpure def assignExpId(idOpt: Option[String], pos: message.Position): String = {
    st"${idOpt.getOrElse("$ae")}.${pos.beginLine}.${pos.beginColumn}.${sha3(pos.string)}".render
  }
}
