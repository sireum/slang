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

  @record class ClosureCaptureCollector(var captures: HashSMap[(ISZ[String], String), (ISZ[String], B, String, AST.Typed)])
    extends AST.MTransformer {
    override def postResolvedAttr(o: AST.ResolvedAttr): MOption[AST.ResolvedAttr] = {
      o.resOpt match {
        case Some(res: AST.ResolvedInfo.LocalVar) if res.scope == AST.ResolvedInfo.LocalVar.Scope.Closure =>
          val key = (res.context, res.id)
          if (!captures.contains(key)) {
            captures = captures + key ~> ((res.context, res.isVal, res.id, o.typedOpt.get))
          }
        case _ =>
      }
      return AST.MTransformer.PostResultResolvedAttr
    }
  }

}

@record class IRTranslator(val spec: B,
                           val threeAddressCode: B,
                           val threeAddressExpF: AST.IR.Exp => B @pure,
                           val th: TypeHierarchy,
                           val fresh: IRTranslator.Fresh) {

  var methodContext: AST.IR.MethodContext = AST.IR.MethodContext.empty
  var stmts: ISZ[AST.IR.Stmt] = ISZ()
  var liftedProcedures: ISZ[AST.IR.Procedure] = ISZ()
  var nestedMethodCaptures: HashMap[ISZ[String], ISZ[(B, String, AST.Typed)]] = HashMap.empty
  var varCaptureSet: HashSet[String] = HashSet.empty

  @strictpure def extOwner(owner: ISZ[String]): ISZ[String] =
    if (owner.nonEmpty) ops.ISZOps(owner).dropRight(1) :+ s"${owner(owner.size - 1)}_Ext"
    else owner

  @strictpure def resolveOwner(res: AST.ResolvedInfo.Method): ISZ[String] =
    if (res.mode == AST.MethodMode.Ext) extOwner(res.owner) else res.owner

  @strictpure def mboxType(t: AST.Typed): AST.Typed.Name =
    AST.Typed.Name(AST.Typed.sireumName :+ "MBox", None(), ISZ(t))

  def collectTypeVarIds(t: AST.Typed, seen: HashSet[String]): (ISZ[String], HashSet[String]) = {
    t match {
      case tv: AST.Typed.TypeVar =>
        if (seen.contains(tv.id)) {
          return (ISZ(), seen)
        }
        return (ISZ(tv.id), seen + tv.id)
      case n: AST.Typed.Name =>
        var ids = ISZ[String]()
        var s = seen
        for (a <- n.args) {
          val p = collectTypeVarIds(a, s)
          ids = ids ++ p._1
          s = p._2
        }
        return (ids, s)
      case f: AST.Typed.Fun =>
        var ids = ISZ[String]()
        var s = seen
        for (a <- f.args) {
          val p = collectTypeVarIds(a, s)
          ids = ids ++ p._1
          s = p._2
        }
        val p = collectTypeVarIds(f.ret, s)
        ids = ids ++ p._1
        s = p._2
        return (ids, s)
      case tu: AST.Typed.Tuple =>
        var ids = ISZ[String]()
        var s = seen
        for (a <- tu.args) {
          val p = collectTypeVarIds(a, s)
          ids = ids ++ p._1
          s = p._2
        }
        return (ids, s)
      case _ =>
        return (ISZ(), seen)
    }
  }

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
    val oldVarCaptureSet = varCaptureSet
    varCaptureSet = HashSet.empty
    bodyOpt match {
      case Some(b) =>
        val collector = IRTranslator.ClosureCaptureCollector(HashSMap.empty)
        collector.transformBody(b)
        for (capture <- collector.captures.values if !capture._2) {
          varCaptureSet = varCaptureSet + capture._3
        }
      case _ =>
    }
    var body: AST.IR.Body = bodyOpt match {
      case Some(body) =>
        val oldStmts = stmts
        stmts = ISZ()
        translateBody(body, None())
        val b = AST.IR.Body.Block(AST.IR.Stmt.Block(stmts, pos))
        stmts = oldStmts
        b
      case _ => AST.IR.Body.Block(AST.IR.Stmt.Block(ISZ(), pos))
    }
    varCaptureSet = oldVarCaptureSet
    if (isBasic) {
      body = toBasic(body.asInstanceOf[AST.IR.Body.Block], pos)
    }
    return AST.IR.Procedure(isInObject, ISZ(), typeParams, owner, id, paramNames, t, body, pos)
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

  @pure def simplifyMatch(stmt: AST.IR.Stmt.Match): AST.IR.Stmt.Block = {
    val matchCondId = matchExpId(stmt.exp.pos)
    val pos = stmt.pos
    var stmts = ISZ[AST.IR.Stmt](
      AST.IR.Stmt.Decl(F, F, F, methodContext, ISZ(AST.IR.Stmt.Decl.Local(matchCondId, AST.Typed.b)), pos),
      AST.IR.Stmt.Assign.Local(methodContext, matchCondId, AST.Typed.b, AST.IR.Exp.Bool(F, pos), pos)
    )
    var first = T
    for (cas <- stmt.cases) {
      val (cs, lMap) = translatePattern(stmt.exp, cas.pattern, HashSMap.empty)
      val casPos = cas.pattern.posOpt.get
      var prefixStmts = ISZ[AST.IR.Stmt](AST.IR.Stmt.Assign.Local(methodContext, matchCondId, AST.Typed.b,
        AST.IR.Exp.Bool(T, casPos), casPos))
      if (lMap.nonEmpty) {
        prefixStmts = prefixStmts :+ AST.IR.Stmt.Decl(F, T, F, methodContext,
          for (e <- lMap.entries) yield AST.IR.Stmt.Decl.Local(e._1._2, e._2.tipe), pos)
        for (e <- lMap.entries) {
          prefixStmts = prefixStmts :+ AST.IR.Stmt.Assign.Local(methodContext, e._1._2, e._2.tipe, e._2, e._2.pos)
        }
      }
      var r: AST.IR.Stmt = if (cs.isEmpty) {
        cas.body(stmts = prefixStmts ++ cas.body.stmts)
      } else {
        val last = cs(cs.size - 1)
        var ifStmt = AST.IR.Stmt.If(last, cas.body(stmts = prefixStmts ++ cas.body.stmts),
          AST.IR.Stmt.Block(ISZ(), last.pos), last.pos)
        for (i <- cs.size - 2 to 0 by -1) {
          val cond = cs(i)
          ifStmt = AST.IR.Stmt.If(cond, AST.IR.Stmt.Block(ISZ(ifStmt), ifStmt.pos), AST.IR.Stmt.Block(ISZ(), cond.pos), cond.pos)
        }
        ifStmt
      }
      if (first) {
        first = F
      } else {
        val cond = AST.IR.Exp.Unary(AST.Typed.b, AST.Exp.UnaryOp.Not,
          AST.IR.Exp.LocalVarRef(F, methodContext, matchCondId, AST.Typed.b, pos), pos)
        r = AST.IR.Stmt.If(cond, AST.IR.Stmt.Block(ISZ(r), r.pos), AST.IR.Stmt.Block(ISZ(), cond.pos), cond.pos)
      }
      stmts = stmts :+ r
    }
    return AST.IR.Stmt.Block(stmts, pos)
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
                    case Some(l2) => fLabel = l2
                    case _ => addF = F
                  }
                case _ => addF = F
              }
            case _ =>
          }
          if (addF) {
            blocks = blocks :+ AST.IR.BasicBlock(fLabel, grounds, AST.IR.Jump.Halt(pos))
          }
          grounds = ISZ()
          return Some(tLabel)
        case stmt: AST.IR.Stmt.Halt =>
          stmt.message match {
            case m: AST.IR.Exp.String if m.value.size == 0 =>
              blocks = blocks :+ AST.IR.BasicBlock(label, grounds, AST.IR.Jump.Halt(pos))
              grounds = ISZ()
            case _ =>
              stmtToBasic(label, AST.IR.Stmt.Print(AST.IR.Stmt.Print.Kind.Err, T, ISZ(stmt.message), pos)) match {
                case Some(l) =>
                  blocks = blocks :+ AST.IR.BasicBlock(l, grounds, AST.IR.Jump.Halt(pos))
                  grounds = ISZ()
                case _ =>
              }
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
            grounds = grounds :+ AST.IR.Stmt.Expr(AST.IR.Exp.Apply(T, AST.Typed.sireumName, id, AST.Typed.emptyRTypes, args :+ stmt.args(j),
              AST.Typed.Fun(AST.Purity.Impure, F, ISZ(arg.tipe), AST.Typed.unit), arg.pos))
          }
          if (stmt.line) {
            grounds = grounds :+ AST.IR.Stmt.Expr(AST.IR.Exp.Apply(T, AST.Typed.sireumName, id, AST.Typed.emptyRTypes, args :+
              AST.IR.Exp.Int(AST.Typed.c, 10, stmt.pos), AST.Typed.Fun(AST.Purity.Impure, F, ISZ(AST.Typed.c),
              AST.Typed.unit), stmt.pos))
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
            grounds = ISZ()
            return Some(end)
          } else {
            return stmtToBasic(label, simplifyMatch(stmt))
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
                case Some(l2) => blocks = blocks :+ basicBlock(l2, grounds, AST.IR.Jump.Goto(n, stmt.pos))
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
          val prefix: String = rhs match {
            case _: AST.Stmt.Match => "match$"
            case _ => ""
          }
          val id = assignExpId(prefix, None(), aePos)
          stmts = stmts :+ AST.IR.Stmt.Decl(F, T, F, methodContext, ISZ(AST.IR.Stmt.Decl.Local(id, lhsType)), aePos)
          translateAssignExp(rhs, (id, lhsType))
          return AST.IR.Exp.LocalVarRef(T, methodContext, id, lhsType, aePos)
      }
    }
    val pos = stmt.posOpt.get
    stmt match {
      case _: AST.Stmt.Spec if !spec => return
      case stmt: AST.Stmt.Var =>
        if (stmt.isSpec && !spec) {
          return
        }
        val init = stmt.initOpt.get
        var oldStmts = stmts
        stmts = ISZ()
        val t = stmt.attr.typedOpt.get
        val varRhs: AST.IR.Exp = init match {
          case init: AST.Stmt.Expr => translateExp(init.exp)
          case _ =>
            val aePos = init.asStmt.posOpt.get
            val prefix: String = init match {
              case _: AST.Stmt.Match => "match$"
              case _ => ""
            }
            val id = assignExpId(prefix, Some(stmt.id.value), aePos)
            stmts = stmts :+ AST.IR.Stmt.Decl(F, T, F, methodContext, ISZ(AST.IR.Stmt.Decl.Local(id, t)), aePos)
            translateAssignExp(init, (id, t))
            AST.IR.Exp.LocalVarRef(T, methodContext, id, t, aePos)
        }
        if (varCaptureSet.contains(stmt.id.value)) {
          val mt = mboxType(t)
          stmts = stmts :+ AST.IR.Stmt.Assign.Local(methodContext, stmt.id.value, mt,
            AST.IR.Exp.Construct(mt, AST.Typed.emptyRTypes, ISZ(varRhs), pos), pos)
          oldStmts = oldStmts :+ AST.IR.Stmt.Decl(F, T, F, methodContext,
            ISZ(AST.IR.Stmt.Decl.Local(stmt.id.value, mt)), pos)
        } else {
          stmts = stmts :+ AST.IR.Stmt.Assign.Local(methodContext, stmt.id.value, t, varRhs, pos)
          oldStmts = oldStmts :+ AST.IR.Stmt.Decl(F, stmt.isVal, F, methodContext,
            ISZ(AST.IR.Stmt.Decl.Local(stmt.id.value, t)), pos)
        }
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
                if (varCaptureSet.contains(lhs.id.value)) {
                  val t = lhs.typedOpt.get
                  val mt = mboxType(t)
                  val mboxRef = AST.IR.Exp.LocalVarRef(T, methodContext, lhs.id.value, mt, pos)
                  stmts = stmts :+ AST.IR.Stmt.Assign.Field(mboxRef, "value", t, rhs, pos)
                } else {
                  stmts = stmts :+ AST.IR.Stmt.Assign.Local(methodContext, lhs.id.value, lhs.typedOpt.get, rhs, pos)
                }
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
                  val prefix: String = stmt.rhs match {
                    case _: AST.Stmt.Match => "match$"
                    case _ => ""
                  }
                  val id = assignExpId(prefix, None(), aePos)
                  val t = stmt.lhs.typedOpt.get
                  stmts = stmts :+ AST.IR.Stmt.Decl(F, T, F, methodContext, ISZ(AST.IR.Stmt.Decl.Local(id, t)), aePos)
                  translateAssignExp(stmt.rhs, (id, t))
                  return AST.IR.Exp.LocalVarRef(T, methodContext, id, t, aePos)
              }
            }

            lhs.resOpt.get match {
              case res: AST.ResolvedInfo.Var if res.isInObject =>
                val rhs = selectRhs()
                stmts = stmts :+ AST.IR.Stmt.Assign.Global(res.owner :+ res.id, lhs.typedOpt.get, rhs, pos)
              case _ =>
                val rcv = lhs.receiverOpt.get
                val receiver = translateExp(rcv)
                val rhs = selectRhs()
                stmts = stmts :+ AST.IR.Stmt.Assign.Field(receiver, lhs.id.value, lhs.typedOpt.get, rhs, pos)
            }
          case lhs: AST.Exp.Invoke =>
            val rcv = lhs.receiverOpt.get
            val receiver = translateExp(rcv)
            val index = translateExp(lhs.args(0))
            val invokeRhs: AST.IR.Exp = stmt.rhs match {
              case rhs: AST.Stmt.Expr => translateExp(rhs.exp)
              case _ =>
                val aePos = stmt.rhs.asStmt.posOpt.get
                val prefix: String = stmt.rhs match {
                  case _: AST.Stmt.Match => "match$"
                  case _ => ""
                }
                val id = assignExpId(prefix, None(), aePos)
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
        if (e.tipe == AST.Typed.unit || e.tipe == AST.Typed.nothing || norm3AC(e) == e) {
          e match {
            case applyExp: AST.IR.Exp.Apply =>
              stmts = stmts :+ AST.IR.Stmt.Expr(applyExp)
            case _ =>
              // Non-Apply void expression (e.g., ApplyClosure): assign to discarded temp
              stmts = stmts :+ AST.IR.Stmt.Assign.Temp(fresh.temp(), e, pos)
          }
        } else {
          halt("Infeasible")
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
      case stmt: AST.Stmt.For =>
        val fPos = stmt.posOpt.get
        def translateForEnumGen(i: Z): AST.IR.Stmt.For = {
          val enumGen = stmt.enumGens(i)
          val idOpt: Option[String] = enumGen.idOpt match {
            case Some(id) => Some(id.value)
            case _ => None()
          }
          val range: AST.IR.Stmt.For.Range = enumGen.range match {
            case r: AST.EnumGen.Range.Expr =>
              val rangeExp = translateExp(r.exp)
              AST.IR.Stmt.For.Range.Expr(rangeExp, r.attr.posOpt.getOrElse(fPos))
            case r: AST.EnumGen.Range.Step =>
              val rPos = r.attr.posOpt.getOrElse(fPos)
              val start = translateExp(r.start)
              val end = translateExp(r.end)
              val byOpt: Option[AST.IR.Exp] = r.byOpt match {
                case Some(by) => Some(translateExp(by))
                case _ => None()
              }
              AST.IR.Stmt.For.Range.Step(r.isInclusive, start, end, byOpt, rPos)
          }
          val condOpt: Option[AST.IR.ExpBlock] = enumGen.condOpt match {
            case Some(cond) => Some(translateExpBlock(cond))
            case _ => None()
          }
          val innerBlock: AST.IR.Stmt.Block = if (i < stmt.enumGens.size - 1) {
            val oldStmts2 = stmts
            stmts = ISZ()
            val nested = translateForEnumGen(i + 1)
            val nestedStmts = stmts :+ nested
            stmts = oldStmts2
            fresh.setTemp(0)
            AST.IR.Stmt.Block(nestedStmts, fPos)
          } else {
            val oldStmts2 = stmts
            stmts = ISZ()
            translateBody(stmt.body, None())
            val bodyStmts = stmts
            stmts = oldStmts2
            fresh.setTemp(0)
            AST.IR.Stmt.Block(bodyStmts, fPos)
          }
          return AST.IR.Stmt.For(methodContext, idOpt, range, condOpt, innerBlock, fPos)
        }
        stmts = stmts :+ translateForEnumGen(0)
        fresh.setTemp(0)
      case stmt: AST.Stmt.VarPattern =>
        val oldStmts = stmts
        stmts = ISZ()
        val init = assignRhs(stmt.pattern.typedOpt.get, stmt.init)
        stmts = stmts :+ patternDecl(methodContext, stmt.pattern)
        stmts = stmts :+ AST.IR.Stmt.AssignPattern(methodContext, stmt.pattern, init, pos)
        stmts = oldStmts ++ stmts
      case _: AST.Stmt.SubZ => // skip
      case stmt: AST.Stmt.Method =>
        stmt.bodyOpt match {
          case Some(body) =>
            val res = stmt.attr.resOpt.get.asInstanceOf[AST.ResolvedInfo.Method]
            val nestedKey = res.owner :+ res.id

            // Collect captures from the body
            val collector = IRTranslator.ClosureCaptureCollector(HashSMap.empty)
            collector.transformBody(body)
            val captures = collector.captures.values
            // Filter out captures from the nested method's own scope — those are
            // lambda-internal captures handled by lambda lifting, not captures
            // the nested method needs from the enclosing scope
            val captureList: ISZ[(B, String, AST.Typed)] =
              for (c <- captures if c._1 != nestedKey) yield (c._2, c._3, c._4)

            // Register captures for call-site rewriting
            nestedMethodCaptures = nestedMethodCaptures + nestedKey ~> captureList

            // Build capture param names and types
            val captureParamNames: ISZ[String] = for (c <- captureList) yield c._2
            val captureParamTypes: ISZ[AST.Typed] = for (c <- captureList) yield c._3

            // Original param names and funType
            val typeParams: ISZ[String] = for (tp <- stmt.sig.typeParams) yield tp.id.value
            val origParamNames: ISZ[String] = for (p <- stmt.sig.params) yield p.id.value
            val origFunType = stmt.sig.funType

            // Lifted procedure has captures as extra leading params
            val liftedParamNames = captureParamNames ++ origParamNames
            val liftedFunType = origFunType(args = captureParamTypes ++ origFunType.args)

            // Save state
            val savedMethodContext = methodContext
            val savedStmts = stmts
            val savedVarCaptureSet = varCaptureSet
            val savedNestedMethodCaptures = nestedMethodCaptures

            // Set up new methodContext for the nested method
            // Owner is the enclosing type/package FQN (methodContext.owner), not local scope
            val liftedOwner = methodContext.owner
            methodContext = AST.IR.MethodContext(T, liftedOwner, res.id, liftedFunType)
            varCaptureSet = HashSet.empty

            // Check for var captures that need MBox wrapping in the nested method's body
            for (capture <- captures if !capture._2) {
              varCaptureSet = varCaptureSet + capture._3
            }

            // Translate the body
            stmts = ISZ()
            translateBody(body, None())
            val irBody = AST.IR.Body.Block(AST.IR.Stmt.Block(stmts, pos))

            val liftedProc = AST.IR.Procedure(
              isInObject = T,
              rTypeParams = ISZ(),
              typeParams = typeParams,
              owner = liftedOwner,
              id = res.id,
              paramNames = liftedParamNames,
              tipe = liftedFunType,
              body = irBody,
              pos = pos)
            liftedProcedures = liftedProcedures :+ liftedProc

            // Restore state
            methodContext = savedMethodContext
            stmts = savedStmts
            varCaptureSet = savedVarCaptureSet
            nestedMethodCaptures = savedNestedMethodCaptures
          case _ => // abstract nested method — skip
        }
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

  @strictpure def isHalt(stmt: AST.Stmt.Expr): B = stmt match {
    case AST.Stmt.Expr(e: AST.Exp.Invoke, _) =>
      e.attr.resOpt.get match {
        case res: AST.ResolvedInfo.BuiltIn if res.kind == AST.ResolvedInfo.BuiltIn.Kind.Halt => T
        case _ => F
      }
    case _ => F
  }

  def translateAssignExp(stmt: AST.AssignExp, local: (String, AST.Typed)): Unit = {
    val pos = stmt.asStmt.posOpt.get
    stmt match {
      case stmt: AST.Stmt.Expr =>
        if (isHalt(stmt)) {
          translateStmt(stmt, Some(local))
          return
        }
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

  def thiz(pos: message.Position): AST.IR.Exp = {
    return norm3AC(AST.IR.Exp.LocalVarRef(T, methodContext, "this", methodContext.receiverType, pos))
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
      if (threeAddressExpF(e)) {
        val n = fresh.temp()
        stmts = stmts :+ AST.IR.Stmt.Assign.Temp(n, e, e.pos)
        return AST.IR.Exp.Temp(n, e.tipe, e.pos)
      }
    }
    return e
  }

  def collectCaptures(assignExp: AST.AssignExp): ISZ[(ISZ[String], B, String, AST.Typed)] = {
    val collector = IRTranslator.ClosureCaptureCollector(HashSMap.empty)
    collector.transformAssignExp(assignExp)
    return collector.captures.values
  }

  def translateExp(exp: AST.Exp): AST.IR.Exp = {

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
          val irArgs: ISZ[AST.IR.Exp] = for (arg <- exp.args) yield translateExp(arg)
          val litStrings: ISZ[String] = for (lit <- exp.lits) yield lit.value
          return norm3AC(AST.IR.Exp.StringInterpolate(
            prefix = exp.prefix,
            lits = litStrings,
            args = irArgs,
            tipe = exp.typedOpt.get,
            pos = pos))
        }
      case _: AST.Exp.This => return thiz(pos)
      case exp: AST.Exp.Ident =>
        val t = exp.typedOpt.get
        exp.resOpt.get match {
          case res: AST.ResolvedInfo.LocalVar =>
            if (varCaptureSet.contains(res.id)) {
              val mt = mboxType(t)
              val mboxRef = AST.IR.Exp.LocalVarRef(T, methodContext, res.id, mt, pos)
              return norm3AC(AST.IR.Exp.FieldVarRef(mboxRef, "value", t, pos))
            }
            return norm3AC(AST.IR.Exp.LocalVarRef(res.isVal, methodContext, res.id, t, pos))
          case res: AST.ResolvedInfo.Var =>
            if (res.isInObject) {
              return norm3AC(AST.IR.Exp.GlobalVarRef(res.owner :+ res.id, t, pos))
            } else {
              return norm3AC(AST.IR.Exp.FieldVarRef(thiz(pos), res.id, t, pos))
            }
          case res: AST.ResolvedInfo.EnumElement =>
            return norm3AC(AST.IR.Exp.EnumElementRef(res.owner, res.name, res.ordinal, pos))
          case res: AST.ResolvedInfo.Method =>
            val methodType = res.tpeOpt.get
            val owner = resolveOwner(res)
            if (res.isInObject) {
              return norm3AC(AST.IR.Exp.Apply(T, owner, res.id, AST.Typed.emptyRTypes, ISZ(), methodType, pos))
            } else {
              return norm3AC(AST.IR.Exp.Apply(F, owner, res.id, AST.Typed.emptyRTypes, ISZ(thiz(pos)),
                methodType(args = thiz(pos).tipe +: methodType.args), pos))
            }
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
            val receiverType = exp.receiverOpt.get.typedOpt.get
            if (receiverType == AST.Typed.string || isSeq(receiverType)) {
              val receiver = exp.receiverOpt.get
              val rcv = translateExp(receiver)
              return norm3AC(AST.IR.Exp.FieldVarRef(rcv, res.id, res.tpeOpt.get.ret, pos))
            } else {
              return translateExp(AST.Exp.Invoke(exp.receiverOpt, AST.Exp.Ident(exp.id, exp.attr), ISZ(), ISZ(), ISZ(),
                exp.attr(typedOpt = Some(exp.typedOpt.get.asInstanceOf[AST.Typed.Method].tpe.ret))))
            }
          case AST.ResolvedInfo.BuiltIn(kind) if kind == AST.ResolvedInfo.BuiltIn.Kind.AsInstanceOf ||
          kind == AST.ResolvedInfo.BuiltIn.Kind.IsInstanceOf =>
            val receiver = translateExp(exp.receiverOpt.get)
            return norm3AC(AST.IR.Exp.Type(kind == AST.ResolvedInfo.BuiltIn.Kind.IsInstanceOf, receiver,
              exp.targs(0).typedOpt.get.asInstanceOf[AST.Typed.Name], exp.posOpt.get))
          case res: AST.ResolvedInfo.Tuple =>
            val receiver = translateExp(exp.receiverOpt.get)
            return norm3AC(AST.IR.Exp.FieldVarRef(receiver, s"_${res.index}", t, pos))
          case AST.ResolvedInfo.BuiltIn(AST.ResolvedInfo.BuiltIn.Kind.String) =>
            val receiver = translateExp(exp.receiverOpt.get)
            val receiverType = exp.receiverOpt.get.typedOpt.get
            val owner: ISZ[String] = receiverType match {
              case tn: AST.Typed.Name => tn.ids
              case _ => ISZ[String]()
            }
            val methodType = AST.Typed.Fun(AST.Purity.Impure, F, ISZ(receiverType), AST.Typed.string)
            return norm3AC(AST.IR.Exp.Apply(F, owner, "string", AST.Typed.emptyRTypes, ISZ(receiver), methodType, pos))
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
                return translateExp(AST.Exp.If(exp.left, exp.right, AST.Exp.LitB(F, AST.Attr(exp.posOpt)),
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
                return translateExp(AST.Exp.If(exp.left, exp.right, AST.Exp.LitB(T, AST.Attr(exp.posOpt)),
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
        // Non-scalar, non-seq binary op: lower to method call on left operand
        exp.attr.resOpt.get match {
          case res: AST.ResolvedInfo.Method =>
            val left = translateExp(exp.left)
            val right = translateExp(exp.right)
            val receiverType: AST.Typed = exp.left.typedOpt.get
            val owner: ISZ[String] = receiverType match {
              case tn: AST.Typed.Name => tn.ids
              case _ => ISZ[String]()
            }
            // Use resolved method type (with proper type substitution) + prepend receiver
            var methodType = res.tpeOpt.get
            methodType = methodType(args = receiverType +: methodType.args)
            return norm3AC(AST.IR.Exp.Apply(F, owner, res.id, AST.Typed.emptyRTypes, ISZ(left, right), methodType, pos))
          case _ =>
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
                  case Some(receiver) if !res.isInObject =>
                    args = args :+ translateExp(receiver)
                    methodType = methodType(args = receiver.typedOpt.get +: methodType.args)
                  case _ =>
                }
                // Check if this is a call to a nested method with captures
                val nestedKey = res.owner :+ res.id
                var applyOwner = res.owner
                var applyIsInObject = res.isInObject
                nestedMethodCaptures.get(nestedKey) match {
                  case Some(captureList) =>
                    // Prepend capture LocalVarRef expressions to args
                    val captureTypes: ISZ[AST.Typed] = for (c <- captureList) yield c._3
                    for (c <- captureList) {
                      val captureId = c._2
                      val captureTyped = c._3
                      val isVal = c._1
                      if (varCaptureSet.contains(captureId)) {
                        // Captured var is MBox-wrapped in the enclosing scope
                        args = args :+ AST.IR.Exp.LocalVarRef(isVal, methodContext, captureId,
                          mboxType(captureTyped), pos)
                      } else {
                        args = args :+ AST.IR.Exp.LocalVarRef(isVal, methodContext, captureId, captureTyped, pos)
                      }
                    }
                    // Update method type to include capture types as leading params
                    methodType = methodType(args = captureTypes ++ methodType.args)
                    // The lifted procedure's owner is the enclosing type/package
                    applyOwner = methodContext.owner
                    applyIsInObject = T
                  case _ =>
                }
                for (arg <- exp.args) {
                  args = args :+ translateExp(arg)
                }
                return norm3AC(AST.IR.Exp.Apply(applyIsInObject, applyOwner, res.id, AST.Typed.emptyRTypes, args, methodType, pos))
              case AST.MethodMode.Ext =>
                var args = ISZ[AST.IR.Exp]()
                for (arg <- exp.args) {
                  args = args :+ translateExp(arg)
                }
                return norm3AC(AST.IR.Exp.Apply(T, resolveOwner(res), res.id, AST.Typed.emptyRTypes, args, res.tpeOpt.get, pos))
              case AST.MethodMode.Select =>
                val rcv: AST.IR.Exp = exp.receiverOpt match {
                  case Some(receiver) =>
                    if (exp.ident.id.value == "apply") {
                      translateExp(receiver)
                    } else {
                      val e = AST.Exp.Select(Some(receiver), exp.ident.id, exp.targs, exp.ident.attr)
                      translateExp(e)
                    }
                  case _ => translateExp(exp.ident)
                }
                val index = translateExp(exp.args(0))
                return norm3AC(AST.IR.Exp.Indexing(rcv, index, pos))
              case AST.MethodMode.Constructor =>
                var args = ISZ[AST.IR.Exp]()
                for (arg <- exp.args) {
                  args = args :+ translateExp(arg)
                }
                return norm3AC(AST.IR.Exp.Construct(exp.typedOpt.get.asInstanceOf[AST.Typed.Name], AST.Typed.emptyRTypes, args, pos))
              case _ => halt(s"TODO: $exp")
            }
          case res: AST.ResolvedInfo.LocalVar =>
            // Calling a function-typed local variable, e.g. f(10) where f: Z => Z
            val closureVar: AST.IR.Exp = exp.receiverOpt match {
              case Some(receiver) => translateExp(receiver)
              case _ =>
                val funType = exp.ident.attr.typedOpt.get
                AST.IR.Exp.LocalVarRef(res.isVal, methodContext, res.id, funType, pos)
            }
            var args = ISZ[AST.IR.Exp]()
            for (arg <- exp.args) {
              args = args :+ translateExp(arg)
            }
            val retType = exp.typedOpt.get
            return norm3AC(AST.IR.Exp.ApplyClosure(closureVar, args, retType, pos))
          case res => halt(s"TODO: $exp (res: $res)")
        }
      case exp: AST.Exp.InvokeNamed =>
        exp.attr.resOpt.get match {
          case res: AST.ResolvedInfo.Method if res.mode == AST.MethodMode.Copy =>
            val t = exp.typedOpt.get.asInstanceOf[AST.Typed.Name]
            val adt = th.typeMap.get(t.ids).get.asInstanceOf[TypeInfo.Adt]
            val sm = tipe.TypeChecker.buildTypeSubstMap(t.ids, exp.posOpt, adt.ast.typeParams, t.args,
              message.Reporter.create).get
            // Evaluate receiver first (preserves evaluation order)
            val receiver: AST.IR.Exp = exp.receiverOpt match {
              case Some(recv) => translateExp(recv)
              case _ => translateExp(AST.Exp.Ident(exp.ident.id, exp.ident.attr))
            }
            // Evaluate named args in source order (preserves side-effect order)
            var map = HashMap.empty[Z, AST.IR.Exp]
            for (narg <- exp.args) {
              map = map + narg.index ~> translateExp(narg.arg)
            }
            // Assemble in param order: use named arg if provided, else copy from receiver
            var args = ISZ[AST.IR.Exp]()
            for (i <- adt.ast.params.indices) {
              map.get(i) match {
                case Some(arg) => args = args :+ arg
                case _ =>
                  val param = adt.ast.params(i)
                  val pt = param.tipe.typedOpt.get.subst(sm)
                  args = args :+ norm3AC(AST.IR.Exp.FieldVarRef(receiver, param.id.value, pt, pos))
              }
            }
            return norm3AC(AST.IR.Exp.Construct(t, AST.Typed.emptyRTypes, args, pos))
          case _ =>
            halt(s"TODO: $exp")
        }
      case exp: AST.Exp.Tuple =>
        val tupleType = exp.typedOpt.get
        val tupleIds = ISZ[String]("org", "sireum", "Tuple")
        var args = ISZ[AST.IR.Exp]()
        var argTypes = ISZ[AST.Typed]()
        for (arg <- exp.args) {
          args = args :+ translateExp(arg)
          argTypes = argTypes :+ arg.typedOpt.get
        }
        val methodType = AST.Typed.Fun(AST.Purity.Impure, F, argTypes, tupleType)
        return norm3AC(AST.IR.Exp.Apply(T, tupleIds, "of", AST.Typed.emptyRTypes, args, methodType, pos))
      case exp: AST.Exp.ForYield =>
        val resultType = exp.typedOpt.get.asInstanceOf[AST.Typed.Name]
        val resultId = st"$$forYield.${pos.beginLine}.${pos.beginColumn}.${sha3(pos.string)}".render
        stmts = stmts :+ AST.IR.Stmt.Decl(F, F, F, methodContext,
          ISZ(AST.IR.Stmt.Decl.Local(resultId, resultType)), pos)
        stmts = stmts :+ AST.IR.Stmt.Assign.Local(methodContext, resultId, resultType,
          AST.IR.Exp.Construct(resultType, AST.Typed.emptyRTypes, ISZ(), pos), pos)
        def translateEnumGen(i: Z): AST.IR.Stmt.For = {
          val enumGen = exp.enumGens(i)
          val idOpt: Option[String] = enumGen.idOpt match {
            case Some(id) => Some(id.value)
            case _ => None()
          }
          val range: AST.IR.Stmt.For.Range = enumGen.range match {
            case r: AST.EnumGen.Range.Expr =>
              val rangeExp = translateExp(r.exp)
              AST.IR.Stmt.For.Range.Expr(rangeExp, r.attr.posOpt.getOrElse(pos))
            case r: AST.EnumGen.Range.Step =>
              val rPos = r.attr.posOpt.getOrElse(pos)
              val start = translateExp(r.start)
              val end = translateExp(r.end)
              val byOpt: Option[AST.IR.Exp] = r.byOpt match {
                case Some(by) => Some(translateExp(by))
                case _ => None()
              }
              AST.IR.Stmt.For.Range.Step(r.isInclusive, start, end, byOpt, rPos)
          }
          val condOpt: Option[AST.IR.ExpBlock] = enumGen.condOpt match {
            case Some(cond) => Some(translateExpBlock(cond))
            case _ => None()
          }
          val innerBlock: AST.IR.Stmt.Block = if (i < exp.enumGens.size - 1) {
            val oldStmts2 = stmts
            stmts = ISZ()
            val nested = translateEnumGen(i + 1)
            val nestedStmts = stmts :+ nested
            stmts = oldStmts2
            fresh.setTemp(0)
            AST.IR.Stmt.Block(nestedStmts, pos)
          } else {
            val oldStmts2 = stmts
            stmts = ISZ()
            val yieldExp = translateExp(exp.exp)
            val yieldPos = exp.exp.posOpt.get
            val resultRef = AST.IR.Exp.LocalVarRef(F, methodContext, resultId, resultType, pos)
            val appendExp = AST.IR.Exp.Binary(resultType, resultRef,
              AST.IR.Exp.Binary.Op.Append, yieldExp, yieldPos)
            val bodyStmts = stmts :+ AST.IR.Stmt.Assign.Local(methodContext, resultId, resultType,
              appendExp, yieldPos)
            stmts = oldStmts2
            fresh.setTemp(0)
            AST.IR.Stmt.Block(bodyStmts, yieldPos)
          }
          return AST.IR.Stmt.For(methodContext, idOpt, range, condOpt, innerBlock, pos)
        }
        val forStmt = translateEnumGen(0)
        stmts = stmts :+ forStmt
        return AST.IR.Exp.LocalVarRef(F, methodContext, resultId, resultType, pos)
      case exp: AST.Exp.Eta =>
        val funType = exp.attr.typedOpt.get.asInstanceOf[AST.Typed.Fun]
        exp.ref.resOpt.get match {
          case res: AST.ResolvedInfo.Method =>
            val captures: ISZ[AST.IR.Exp] = if (res.isInObject) {
              ISZ()
            } else {
              exp.ref match {
                case ref: AST.Exp.Select =>
                  ref.receiverOpt match {
                    case Some(receiver) => ISZ(translateExp(receiver))
                    case _ => ISZ(thiz(pos))
                  }
                case _: AST.Exp.Ident => ISZ(thiz(pos))
                case _ => ISZ(thiz(pos))
              }
            }
            return norm3AC(AST.IR.Exp.ClosureRef(
              owner = res.owner,
              id = res.id,
              captures = captures,
              tipe = funType,
              pos = pos
            ))
          case res: AST.ResolvedInfo.LocalVar =>
            if (varCaptureSet.contains(res.id)) {
              val mt = mboxType(funType)
              val mboxRef = AST.IR.Exp.LocalVarRef(T, methodContext, res.id, mt, pos)
              return norm3AC(AST.IR.Exp.FieldVarRef(mboxRef, "value", funType, pos))
            }
            return norm3AC(AST.IR.Exp.LocalVarRef(
              isVal = res.isVal,
              context = methodContext,
              id = res.id,
              tipe = funType,
              pos = pos
            ))
          case res =>
            halt(s"TODO: Eta with $res")
        }
      case exp: AST.Exp.Fun =>
        val originalFunType = exp.attr.typedOpt.get.asInstanceOf[AST.Typed.Fun]

        // Step 1: Compute closure name and owner
        val closureName = st"$$closure.${pos.beginLine}.${pos.beginColumn}.${sha3(pos.string)}".render
        val owner = methodContext.owner

        // Step 2: Collect captures from the lambda body
        val captures = collectCaptures(exp.exp)

        // Step 3: Build capture names/types and lambda param names/types
        var captureNames = ISZ[String]()
        var captureTypes = ISZ[AST.Typed]()
        var captureExprs = ISZ[AST.IR.Exp]()
        var liftedVarCaptureSet = HashSet.empty[String]
        for (capture <- captures) {
          val captureId = capture._3
          val captureIsVal = capture._2
          val captureType = capture._4
          captureNames = captureNames :+ captureId
          if (!captureIsVal || varCaptureSet.contains(captureId)) {
            // var capture: already MBox-wrapped in enclosing scope
            val mt = mboxType(captureType)
            captureTypes = captureTypes :+ mt
            captureExprs = captureExprs :+ AST.IR.Exp.LocalVarRef(T, methodContext, captureId, mt, pos)
            liftedVarCaptureSet = liftedVarCaptureSet + captureId
          } else {
            // val capture: pass value directly
            captureTypes = captureTypes :+ captureType
            captureExprs = captureExprs :+ AST.IR.Exp.LocalVarRef(T, methodContext, captureId, captureType, pos)
          }
        }

        val lambdaParamNames: ISZ[String] = for (p <- exp.params) yield p.idOpt.get.value
        val fullParamNames = captureNames ++ lambdaParamNames

        // Step 4: Build lifted fun type (captures ++ lambda args -> ret)
        val liftedFunType = AST.Typed.Fun(
          purity = originalFunType.purity,
          isByName = F,
          args = captureTypes ++ originalFunType.args,
          ret = originalFunType.ret
        )

        // Step 5: Save current state
        val savedMethodContext = methodContext
        val savedStmts = stmts
        val savedVarCaptureSet = varCaptureSet
        val savedNestedMethodCaptures = nestedMethodCaptures

        // Step 6: Set fresh state for lifted body
        methodContext = AST.IR.MethodContext(
          isInObject = T,
          owner = owner,
          id = closureName,
          t = liftedFunType
        )
        stmts = ISZ()
        varCaptureSet = liftedVarCaptureSet
        nestedMethodCaptures = HashMap.empty

        // Step 7: Translate lambda body
        // Exp.Fun.exp is an AssignExp; translate it as the return value
        val retType = originalFunType.ret
        val bodyPos = exp.exp.asStmt.posOpt.get
        exp.exp match {
          case bodyExpr: AST.Stmt.Expr if isHalt(bodyExpr) =>
            translateStmt(bodyExpr, None())
          case bodyExpr: AST.Stmt.Expr =>
            val r = translateExp(bodyExpr.exp)
            stmts = stmts :+ AST.IR.Stmt.Return(Some(r), bodyPos)
          case _ =>
            if (retType == AST.Typed.unit) {
              // Unit-returning lambda: translate body as statements, return Unit singleton
              exp.exp match {
                case block: AST.Stmt.Block =>
                  for (s <- block.body.stmts) {
                    translateStmt(s, None())
                  }
                case _ =>
                  translateStmt(exp.exp.asStmt, None())
              }
              stmts = stmts :+ AST.IR.Stmt.Return(None(), bodyPos)
            } else {
              val retId = assignExpId("", Some("$closureRet"), bodyPos)
              stmts = stmts :+ AST.IR.Stmt.Decl(F, T, F, methodContext,
                ISZ(AST.IR.Stmt.Decl.Local(retId, retType)), bodyPos)
              translateAssignExp(exp.exp, (retId, retType))
              val retRef = AST.IR.Exp.LocalVarRef(T, methodContext, retId, retType, bodyPos)
              stmts = stmts :+ AST.IR.Stmt.Return(Some(retRef), bodyPos)
            }
        }
        val liftedBody = AST.IR.Body.Block(AST.IR.Stmt.Block(stmts, pos))

        // Step 8: Collect free TypeVars from capture types so step13 adds typeOps params
        var closureTypeVarIds = ISZ[String]()
        var closureTypeVarSeen = HashSet.empty[String]
        for (ct <- captureTypes) {
          collectTypeVarIds(ct, closureTypeVarSeen) match {
            case (ids, seen) =>
              closureTypeVarIds = closureTypeVarIds ++ ids
              closureTypeVarSeen = seen
          }
        }

        // Step 9: Create the lifted IR.Procedure and append to liftedProcedures
        val liftedProc = AST.IR.Procedure(
          isInObject = T,
          rTypeParams = ISZ(),
          typeParams = closureTypeVarIds,
          owner = owner,
          id = closureName,
          paramNames = fullParamNames,
          tipe = liftedFunType,
          body = liftedBody,
          pos = pos
        )
        liftedProcedures = liftedProcedures :+ liftedProc

        // Step 9: Restore saved state
        methodContext = savedMethodContext
        stmts = savedStmts
        varCaptureSet = savedVarCaptureSet
        nestedMethodCaptures = savedNestedMethodCaptures

        // Step 10: Return ClosureRef with capture expressions and original fun type
        return norm3AC(AST.IR.Exp.ClosureRef(
          owner = owner,
          id = closureName,
          captures = captureExprs,
          tipe = originalFunType,
          pos = pos
        ))
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

  @pure def translatePattern(exp: AST.IR.Exp,
                             pattern: AST.Pattern,
                             localMap: HashSMap[(ISZ[String], String), AST.IR.Exp]): (ISZ[AST.IR.Exp], HashSMap[(ISZ[String], String), AST.IR.Exp]) = {
    var r = ISZ[AST.IR.Exp]()
    var lMap = localMap
    val pos = pattern.posOpt.get
    pattern match {
      case _: AST.Pattern.Wildcard => // skip
      case pattern: AST.Pattern.Literal =>
        r = r :+ AST.IR.Exp.Binary(AST.Typed.b, exp, AST.IR.Exp.Binary.Op.Eq, translateExp(pattern.lit), pos)
      case pattern: AST.Pattern.VarBinding =>
        pattern.tipeOpt match {
          case Some(tipe) =>
            val t = tipe.typedOpt.get
            if (t != exp.tipe) {
              r = r :+ AST.IR.Exp.Type(T, exp, t.asInstanceOf[AST.Typed.Name], pos)
            }
          case _ =>
        }
        lMap = lMap + (pattern.idContext, pattern.id.value) ~> exp
      case pattern: AST.Pattern.Structure =>
        val t = pattern.typedOpt.get
        lMap = pattern.idOpt match {
          case Some(id) => localMap + (pattern.idContext, id.value) ~> exp
          case _ => localMap
        }
        t match {
          case t: AST.Typed.Tuple =>
            var i = 0
            var conds = ISZ[AST.IR.Exp]()
            for (j <- 0 until t.args.size) {
              val pat = pattern.patterns(i)
              val f = AST.IR.Exp.FieldVarRef(exp, s"_${j + 1}", pat.typedOpt.get, pat.posOpt.get)
              val (pconds, lMap2) = translatePattern(f, pat, lMap)
              conds = conds ++ pconds
              lMap = lMap2
              i = i + 1
            }
            r = r :+ AST.IR.bigAnd(conds, pos)
          case t: AST.Typed.Name =>
            var conds = ISZ[AST.IR.Exp]()
            if (t.ids == AST.Typed.isName || t.ids == AST.Typed.msName) {
              val hasWildcard = pattern.patterns.size > 0 && pattern.patterns(pattern.patterns.size - 1).
                isInstanceOf[AST.Pattern.SeqWildcard]
              val (size, op): (Z, AST.IR.Exp.Binary.Op.Type) = if (hasWildcard) (pattern.patterns.size - 1, AST.IR.Exp.Binary.Op.Ge)
              else (pattern.patterns.size, AST.IR.Exp.Binary.Op.Eq)
              conds = conds :+ AST.IR.Exp.Binary(AST.Typed.b, AST.IR.Exp.FieldVarRef(exp, "size", AST.Typed.z, pos), op,
                AST.IR.Exp.Int(AST.Typed.z, size, pos), pos)
              val indexType = t.args(0)
              var n: Z = th.typeMap.get(t.args(0).asInstanceOf[AST.Typed.Name].ids).get match {
                case ti: TypeInfo.SubZ =>
                  if (ti.ast.isZeroIndex) 0 else ti.ast.index
                case _ => 0
              }
              for (i <- 0 until pattern.patterns.size - (if (hasWildcard) 1 else 0)) {
                val pat = pattern.patterns(i)
                val f = AST.IR.Exp.Indexing(exp, AST.IR.Exp.Int(indexType, i, pos), pat.posOpt.get)
                val (pconds, lMap2) = translatePattern(f, pat, lMap)
                conds = conds ++ pconds
                lMap = lMap2
                n = n + 1
              }
            } else {
              val adt = th.typeMap.get(t.ids).get.asInstanceOf[TypeInfo.Adt]
              var i = 0
              for (p <- adt.ast.params if !p.isHidden) {
                val pat = pattern.patterns(i)
                val f = AST.IR.Exp.FieldVarRef(exp, p.id.value, pat.typedOpt.get, pat.posOpt.get)
                val (pconds, lMap2) = translatePattern(f, pat, lMap)
                conds = conds ++ pconds
                lMap = lMap2
                i = i + 1
              }
            }
            r = r :+ AST.IR.condAnd(AST.IR.Exp.Type(T, exp, t, pos), AST.IR.bigAnd(conds, pos), pos)
          case _ => halt("Infeasible")
        }
      case pattern: AST.Pattern.Ref =>
        val right: AST.IR.Exp = pattern.attr.resOpt.get match {
          case res: AST.ResolvedInfo.Var =>
            if (res.isInObject) {
              val ids = pattern.name.ids
              val owner: ISZ[String] = for (i <- 0 until pattern.name.ids.size - 1) yield ids(i).value
              AST.IR.Exp.GlobalVarRef(owner :+ ids(ids.size - 1).value, pattern.typedOpt.get, pos)
            } else {
              AST.IR.Exp.FieldVarRef(thiz(pos), res.id, pattern.typedOpt.get, pos)
            }
          case res: AST.ResolvedInfo.LocalVar =>
            if (varCaptureSet.contains(res.id)) {
              val pt = pattern.typedOpt.get
              val mt = mboxType(pt)
              val mboxRef = AST.IR.Exp.LocalVarRef(T, methodContext, res.id, mt, pos)
              AST.IR.Exp.FieldVarRef(mboxRef, "value", pt, pos)
            } else {
              AST.IR.Exp.LocalVarRef(res.isVal, methodContext, res.id, pattern.typedOpt.get, pos)
            }
          case res: AST.ResolvedInfo.EnumElement =>
            AST.IR.Exp.EnumElementRef(res.owner, res.name, res.ordinal, pos)
          case _ => halt("Infeasible")
        }
        r = r :+ AST.IR.Exp.Binary(AST.Typed.b, exp, AST.IR.Exp.Binary.Op.Eq, right, pos)
      case pattern: AST.Pattern.LitInterpolate =>
        pattern.prefix match {
          case string"string" =>
            val right = AST.IR.Exp.String(pattern.value, pos)
            r = r :+ AST.IR.Exp.Binary(AST.Typed.b, exp, AST.IR.Exp.Binary.Op.Eq, right, pos)
          case string"c" =>
            val right = AST.IR.Exp.Int(AST.Typed.c, conversions.String.toCis(pattern.value)(0).toZ, pos)
            r = r :+ AST.IR.Exp.Binary(AST.Typed.b, exp, AST.IR.Exp.Binary.Op.Eq, right, pos)
          case string"z" =>
            val right = AST.IR.Exp.Int(AST.Typed.z, Z(pattern.value).get, pos)
            r = r :+ AST.IR.Exp.Binary(AST.Typed.b, exp, AST.IR.Exp.Binary.Op.Eq, right, pos)
          case string"f32" =>
            val right = AST.IR.Exp.F32(F32(pattern.value).get, pos)
            r = r :+ AST.IR.Exp.Binary(AST.Typed.b, exp, AST.IR.Exp.Binary.Op.Eq, right, pos)
          case string"f64" =>
            val right = AST.IR.Exp.F64(F64(pattern.value).get, pos)
            r = r :+ AST.IR.Exp.Binary(AST.Typed.b, exp, AST.IR.Exp.Binary.Op.Eq, right, pos)
          case string"r" =>
            val right = AST.IR.Exp.R(R(pattern.value).get, pos)
            r = r :+ AST.IR.Exp.Binary(AST.Typed.b, exp, AST.IR.Exp.Binary.Op.Eq, right, pos)
          case _ =>
            val t = pattern.typedOpt.get
            val right = AST.IR.Exp.Int(t, Z(pattern.value).get, pos)
            r = r :+ AST.IR.Exp.Binary(AST.Typed.b, exp, AST.IR.Exp.Binary.Op.Eq, right, pos)
        }
      case _: AST.Pattern.SeqWildcard => halt("Infeasible")
    }
    return (r, lMap)
  }


  @pure def sha3(s: String): U32 = {
    val sha = crypto.SHA3.init512
    sha.update(conversions.String.toU8is(s))
    val bs = sha.finalise()
    return conversions.U8.toU32(bs(0)) << u32"24" | conversions.U8.toU32(bs(1)) << u32"16" |
      conversions.U8.toU32(bs(2)) << u32"8" | conversions.U8.toU32(bs(3))
  }

  @strictpure def assignExpId(prefix: String, idOpt: Option[String], pos: message.Position): String = {
    st"${prefix}${idOpt.getOrElse("$ae")}.${pos.beginLine}.${pos.beginColumn}.${sha3(pos.string)}".render
  }

  @strictpure def matchExpId(pos: message.Position): String = {
    st"$$match.${pos.beginLine}.${pos.beginColumn}.${sha3(pos.string)}".render
  }
}
