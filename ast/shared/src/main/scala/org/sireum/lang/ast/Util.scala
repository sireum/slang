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

package org.sireum.lang.ast

import org.sireum._
import org.sireum.message._
import org.sireum.U64._

object Util {

  @msig trait InvocationSubstitutor extends MTransformer {

    override def transformExp(o: Exp): MOption[Exp] = {
      o match {
        case o: Exp.Invoke => transformExpInvoke(o) match {
          case MSome(o2) => return MSome(o2)
          case _ =>
        }
        case o: Exp.InvokeNamed => transformExpInvokeNamed(o) match {
          case MSome(o2) => return MSome(o2)
          case _ =>
        }
        case _ =>
      }
      return super.transformExp(o)
    }

    override def transformExpInvoke(o: Exp.Invoke): MOption[Exp.Invoke] = {
      var changed = F
      val newReceiverOpt: Option[Exp] = o.receiverOpt match {
        case Some(receiver) => transformExp(receiver) match {
          case MSome(r) =>
            changed = T
            Some(r)
          case _ => Some(receiver)
        }
        case _ => None()
      }
      val newIdent: Exp = transformExp(o.ident) match {
        case MSome(e) =>
          changed = T
          e
        case _ => o.ident
      }
      var newArgs = ISZ[Exp]()
      for (arg <- o.args) {
        transformExp(arg) match {
          case MSome(e) =>
            changed = T
            newArgs = newArgs :+ e
          case _ =>
            newArgs = newArgs :+ arg
        }
      }
      if (!changed) {
        return MNone()
      }
      val (isApply, nro, ni) = Util.invokeReceiverIdent(newReceiverOpt, newIdent)
      return if (isApply) MSome(o(receiverOpt = nro, ident = ni, args = newArgs, attr = ni.attr))
      else MSome(o(receiverOpt = nro, ident = ni, args = newArgs))
    }

    override def transformExpInvokeNamed(o: Exp.InvokeNamed): MOption[Exp.InvokeNamed] = {
      var changed = F
      val newReceiverOpt: Option[Exp] = o.receiverOpt match {
        case Some(receiver) => transformExp(receiver) match {
          case MSome(r) =>
            changed = T
            Some(r)
          case _ => Some(receiver)
        }
        case _ => None()
      }
      val newIdent: Exp = transformExp(o.ident) match {
        case MSome(e) =>
          changed = T
          e
        case _ => o.ident
      }
      var newArgs = ISZ[NamedArg]()
      for (arg <- o.args) {
        transformNamedArg(arg) match {
          case MSome(e) =>
            changed = T
            newArgs = newArgs :+ e
          case _ =>
            newArgs = newArgs :+ arg
        }
      }
      if (!changed) {
        return MNone()
      }
      val (isApply, nro, ni) = Util.invokeReceiverIdent(newReceiverOpt, newIdent)
      return if (isApply) MSome(o(receiverOpt = nro, ident = ni, args = newArgs, attr = ni.attr))
      else MSome(o(receiverOpt = nro, ident = ni, args = newArgs))
    }
  }

  @record class ExpSubstitutor(val map: HashMap[Exp, Exp]) extends InvocationSubstitutor {
    override def preExp(o: Exp): MTransformer.PreResult[Exp] = {
      map.get(o) match {
        case Some(o2) => return MTransformer.PreResult(F, MSome(o2))
        case _ => return MTransformer.PreResult(T, MNone())
      }
    }
  }

  @record class EnumSymbolMapper(val content: ISZ[C], var map: HashMap[Z, (String, String)]) extends MTransformer {
    override def postStmtEnum(o: Stmt.Enum): MOption[Stmt] = {
      for (id <- o.elements) {
        val offset = id.attr.posOpt.get.offset
        if (content(offset) == '\'') {
          map = map + offset ~> ((s"'${id.value}", s""""${id.value}""""))
        }
      }
      return MTransformer.PostResultStmtEnum
    }
  }

  @record class ProofStepsNumberMapper(var num: Z,
                                       var numMap: HashMap[Z, Z],
                                       var map: HashMap[Z, (String, String)],
                                       val reporter: Reporter) extends MTransformer {
    override def preStmtDeduceSequent(o: Stmt.DeduceSequent): MTransformer.PreResult[Stmt.Spec] = {
      for (sequent <- o.sequents) {
        num = 1
        numMap = HashMap.empty
        for (step <- sequent.steps) {
          transformProofAstStep(step)
        }
      }
      num = 1
      return MTransformer.PreResultStmtDeduceSequent(continu = F)
    }

    override def preStmtDeduceSteps(o: Stmt.DeduceSteps): MTransformer.PreResult[Stmt.Spec] = {
      num = 1
      numMap = HashMap.empty
      for (step <- o.steps) {
        transformProofAstStep(step)
      }
      num = 1
      return MTransformer.PreResultStmtDeduceSteps(continu = F)
    }

    override def preProofAstStep(o: ProofAst.Step): MTransformer.PreResult[ProofAst.Step] = {
      o.id match {
        case id: ProofAst.StepId.Num =>
          if (id.no != num) {
            map = map + id.posOpt.get.offset ~> ((id.no.string, num.string))
          }
          numMap = numMap + id.no ~> num
          num = num + 1
        case _ =>
      }
      return MTransformer.PreResultProofAstStepRegular
    }

    def processStepId(sid: ProofAst.StepId): Unit = {
      sid match {
        case sid: ProofAst.StepId.Num =>
          numMap.get(sid.no) match {
            case Some(newNo) =>
              if (sid.no != newNo) {
                map = map + sid.posOpt.get.offset ~> ((sid.no.string, newNo.string))
              }
            case _ =>
              reporter.error(sid.posOpt, "Slang Rewrite", s"Could not find proof step $sid")
          }
        case _ =>
      }
    }

    override def preProofAstStepJustificationApply(o: ProofAst.Step.Justification.Apply): MTransformer.PreResult[ProofAst.Step.Justification] = {
      if (o.witnesses.isEmpty) {
        for (arg <- o.args) {
          arg match {
            case arg: ProofAst.StepId => processStepId(arg)
            case _ =>
          }
        }
      } else {
        for (w <- o.witnesses) {
          processStepId(w)
        }
      }
      return MTransformer.PreResultProofAstStepJustificationApply
    }

    override def preProofAstStepJustificationApplyNamed(o: ProofAst.Step.Justification.ApplyNamed): MTransformer.PreResult[ProofAst.Step.Justification] = {
      for (w <- o.witnesses) {
        processStepId(w)
      }
      return MTransformer.PreResultProofAstStepJustificationApplyNamed
    }

    override def preProofAstStepJustificationApplyEta(o: ProofAst.Step.Justification.ApplyEta): MTransformer.PreResult[ProofAst.Step.Justification] = {
      for (w <- o.witnesses) {
        processStepId(w)
      }
      return MTransformer.PreResultProofAstStepJustificationApplyEta
    }
  }

  @record class ProofReformatter(val docInfo: DocInfo,
                                 val content: ISZ[C],
                                 var map: HashMap[Z, (String, String)]) extends MTransformer {
    override def preProofAst(o: ProofAst): MTransformer.PreResult[ProofAst] = {
      format(o.steps)
      return MTransformer.PreResultProofAst
    }

    override def preSequent(o: Sequent): MTransformer.PreResult[Sequent] = {
      format(o.steps)
      return MTransformer.PreResultSequent
    }

    override def preStmtDeduceSteps(o: Stmt.DeduceSteps): MTransformer.PreResult[Stmt.Spec] = {
      format(o.steps)
      return MTransformer.PreResultStmtDeduceSteps
    }

    def format(steps: ISZ[ProofAst.Step]): Unit = {
      @pure def getMaxColumn: Z = {
        var r: Z = -1

        def maxColumnH(step: ProofAst.Step): Unit = {
          step match {
            case step: ProofAst.Step.SubProof =>
              for (step2 <- step.steps) {
                maxColumnH(step2)
              }
            case step: ProofAst.Step.Let =>
              for (step2 <- step.steps) {
                maxColumnH(step2)
              }
            case _ =>
              val pos = step.attr.posOpt.get
              val justLine: Z = step match {
                case step: ProofAst.Step.Regular =>
                  val p = step.just.posOpt.get
                  p.beginLine
                case _ => 0
              }
              for (i <- pos.beginLine to pos.endLine) {
                val lineOffset = docInfo.lineOffsets(i - 1).toZ
                val k: Z = if (justLine == i) {
                  val claimPos = step.asInstanceOf[ProofAst.Step.Regular].claim.fullPosOpt.get
                  claimPos.offset + claimPos.length + 1
                } else {
                  var j = lineOffset
                  while (j < content.size && content(j) != '\n' && content(j) != '\r') {
                    j = j + 1
                  }
                  j = j - 1
                  while (content(j).isWhitespace) {
                    j = j - 1
                  }
                  j + 1
                }
                var column = k - lineOffset

                def subImply(imply: ISZ[C]): Unit = {
                  var l = lineOffset
                  while (l != -1 && l <= pos.offset + pos.length) {
                    l = ops.StringOps.stringIndexOfFrom(content, imply, l)
                    if (l != -1 && l <= pos.offset + pos.length) {
                      if ((imply.size == 4) ___>: (content(l - 1) != '_')) {
                        column = column - (imply.size - 1)
                      }
                      l = l + imply.size
                    }
                  }
                }

                subImply(ProofReformatter.imply)
                subImply(ProofReformatter.simply)
                if (column > r) {
                  r = column
                }
              }
          }
        }

        for (step <- steps) {
          maxColumnH(step)
        }
        return r + 3
      }

      def patchBeginning(step: ProofAst.Step): Unit = {
        var i: Z = 0
        step match {
          case _: ProofAst.Step.SubProof => return
          case _: ProofAst.Step.Let => return
          case step: ProofAst.Step.Assert =>
            val j = ops.StringOps.stringIndexOfFrom(content, ProofReformatter.asser, step.attr.posOpt.get.offset) + 6
            i = j
            while (content(i).isWhitespace) {
              i = i + 1
            }
            if (i - j > 0) {
              map = map + j ~> (ops.StringOps.substring(content, i, j), "")
            }
          case step: ProofAst.Step.Assume =>
            val j = ops.StringOps.stringIndexOfFrom(content, ProofReformatter.assum, step.attr.posOpt.get.offset) + 6
            i = j
            while (content(i).isWhitespace) {
              i = i + 1
            }
            if (i - j > 0) {
              map = map + j ~> (ops.StringOps.substring(content, i, j), "")
            }
          case _ =>
            if (step.id.isSynthetic) {
              i = step.attr.posOpt.get.offset
            } else {
              val pos = step.id.posOpt.get
              val offset = pos.offset + pos.length
              i = offset
              while (content(i).isWhitespace || content(i) == '#' || content(i) == '>') {
                i = i + 1
              }
              if (i - offset != 1) {
                map = map + offset ~> (ops.StringOps.substring(content, offset, i), " ")
              }
            }
        }
        if (content(i) == '(') {
          var j = i + 1
          while (content(j).isWhitespace) {
            j = j + 1
          }
          if (j - i != 3) {
            map = map + (i + 1) ~> (ops.StringOps.substring(content, i + 1, j), "  ")
          }
        } else {
          map = map + i ~> ("", "(  ")
        }
      }

      def patchEnd(maxColumn: Z, step: ProofAst.Step.Regular): Unit = {
        val pos = step.claim.fullPosOpt.get
        val offset = pos.offset + pos.length
        val paren: Z = {
          val end = ops.StringOps.stringIndexOfFrom(content, ProofReformatter.by, offset)
          var i = offset
          while (i < end && content(i).isWhitespace) {
            i = i + 1
          }
          if (content(i) == ')') end else -1
        }
        var add = maxColumn - pos.endColumn - 2

        def addImply(imply: ISZ[C]): Unit = {
          var i = pos.offset
          while (i != -1 && i <= offset) {
            i = ops.StringOps.stringIndexOfFrom(content, imply, i)
            if (i != -1 && i <= offset) {
              if ((imply.size == 4) ___>: (content(i - 1) != '_')) {
                add = add + (imply.size - 1)
              }
              i = i + imply.size
            }
          }
        }

        addImply(ProofReformatter.imply)
        addImply(ProofReformatter.simply)
        var spaces: ISZ[C] = for (_ <- 0 until add) yield ' '
        spaces = spaces :+ ')' :+ ' '
        val pad = conversions.String.fromCis(spaces)
        if (paren >= 0) {
          val orig = ops.StringOps.substring(content, offset, paren)
          map = map + offset ~> (orig, pad)
        } else {
          map = map + offset ~> ("", pad)
        }
        val i = ops.StringOps.stringIndexOfFrom(content, ProofReformatter.by, offset)
        map = map + (offset + 1) ~> (ops.StringOps.substring(content, offset + 1, i), "")
      }
      def patchEndClaim(pos: Position, claimPos: Position): Unit = {
        val end = pos.offset + pos.length
        val i = claimPos.offset + claimPos.length
        var j = i
        while (content(j) != ')' && j < end) {
          j = j + 1
        }
        if ((content(j) == ')' __>: (j - i != 2)) && (j - i != 3)) {
          if (content(j) != ')') {
            map = map + i ~> ("", "  )")
          } else {
            map = map + i ~> (ops.StringOps.substring(content, i, j), "  ")
          }
        }
      }

      val maxColumn = getMaxColumn

      def rec(step: ProofAst.Step): Unit = {
        patchBeginning(step)
        step match {
          case step: ProofAst.Step.Regular => patchEnd(maxColumn, step)
          case step: ProofAst.Step.Assert => patchEndClaim(step.attr.posOpt.get, step.claim.fullPosOpt.get)
          case step: ProofAst.Step.Assume => patchEndClaim(step.attr.posOpt.get, step.claim.fullPosOpt.get)
          case step: ProofAst.Step.SubProof =>
            for (step2 <- step.steps) {
              rec(step2)
            }
          case step: ProofAst.Step.Let =>
            for (step2 <- step.steps) {
              rec(step2)
            }
        }
      }

      for (step <- steps) {
        rec(step)
      }
    }
  }

  object ProofReformatter {
    val assum: ISZ[C] = ISZ[C]('A', 's', 's', 'u', 'm', 'e')
    val asser: ISZ[C] = ISZ[C]('A', 's', 's', 'e', 'r', 't')
    val imply: ISZ[C] = ISZ[C]('_', '_', '>', ':')
    val simply: ISZ[C] = ISZ[C]('_', '_', '_', '>', ':')
    val by: ISZ[C] = ISZ[C]('b', 'y')
  }

  @record class ProofStepInserter(val docInfo: DocInfo,
                                  val line: Z,
                                  val content: ISZ[C],
                                  val insert: String,
                                  var map: HashMap[Z, (String, String)]) extends MTransformer {
    def insertBeginning(no: Z, isEmpty: B, pos: Position, after: ISZ[C]): Unit = {
      var i = ops.StringOps.stringIndexOfFrom(content, ProofStepInserter.lineCommentPrefix, docInfo.lineOffsets(pos.beginLine).toZ)
      if (0 <= i && i < docInfo.lineOffsets(pos.beginLine + 1).toZ) {
        while (i < content.size && content(i) != '\n') {
          i = i + 1
        }
      } else {
        i = ops.StringOps.stringIndexOfFrom(content, after, pos.offset)
        while (i <= content.size && content(i) != '(') {
          i = i + 1
        }
        i = i + 1
      }
      insertPos(no, "", i, pos.beginColumn + 1, if (isEmpty) "" else ",")
    }

    override def preProofAst(o: ProofAst): MTransformer.PreResult[ProofAst] = {
      val r = MTransformer.PreResultProofAst(continu = F)
      val pos = o.attr.posOpt.get
      if (map.nonEmpty || !(pos.beginLine <= line && line <= pos.endLine)) {
        return r
      }
      val no = freshNum(o.steps)
      insertSteps(no, o.steps)
      if (map.nonEmpty) {
        return r
      }
      if (o.steps.nonEmpty) {
        insertBetween(no, o.steps)
      }
      if (map.isEmpty) {
        insertBeginning(no, o.steps.isEmpty, pos, ProofStepInserter.proof)
      }
      return r
    }

    override def preSequent(o: Sequent): MTransformer.PreResult[Sequent] = {
      val r = MTransformer.PreResultSequent(continu = F)
      val pos = o.attr.posOpt.get
      if (map.nonEmpty || !(pos.beginLine <= line && line <= pos.endLine)) {
        return r
      }
      val no = freshNum(o.steps)
      insertSteps(no, o.steps)
      if (map.nonEmpty) {
        return r
      }
      if (o.steps.nonEmpty) {
        insertBetween(no, o.steps)
      }
      if (map.isEmpty) {
        insertBeginning(no, o.steps.isEmpty, pos, ProofStepInserter.proof)
      }
      return r
    }

    override def preStmtDeduceSteps(o: Stmt.DeduceSteps): MTransformer.PreResult[Stmt.Spec] = {
      val r = MTransformer.PreResultStmtDeduceSteps(continu = F)
      val pos = o.attr.posOpt.get
      if (map.nonEmpty || !(pos.beginLine <= line && line <= pos.endLine)) {
        return r
      }
      val no = freshNum(o.steps)
      insertSteps(no, o.steps)
      if (map.nonEmpty) {
        return r
      }
      if (o.steps.nonEmpty) {
        insertBetween(no, o.steps)
      }
      if (map.isEmpty) {
        insertBeginning(no, o.steps.isEmpty, pos, ProofStepInserter.deduce)
      }
      return r
    }

    @pure def freshNum(steps: ISZ[ProofAst.Step]): Z = {
      var set = HashSet.empty[Z]

      def rec(step: ProofAst.Step): Unit = {
        step.id match {
          case id: ProofAst.StepId.Num => set = set + id.no
          case _ =>
        }
        step match {
          case step: ProofAst.Step.Let =>
            for (step2 <- step.steps) {
              rec(step2)
            }
          case step: ProofAst.Step.SubProof =>
            for (step2 <- step.steps) {
              rec(step2)
            }
          case _ =>
        }
      }

      for (step <- steps) {
        rec(step)
      }
      var i = 1
      while (set.contains(i)) {
        i = i + 1
      }
      return i
    }

    def insertBetween(no: Z, steps: ISZ[ProofAst.Step]): Unit = {
      for (i <- 1 until steps.size if map.isEmpty) {
        val pos1 = steps(i - 1).attr.posOpt.get
        val pos2 = steps(i).attr.posOpt.get
        if (pos1.endLine < line && line < pos2.beginLine) {
          insertStep(no, steps(i - 1))
        }
      }
      if (map.isEmpty && steps.nonEmpty) {
        val last = steps(steps.size - 1)
        val lastPos = last.attr.posOpt.get
        if (lastPos.endLine < line) {
          insertStep(no, last)
        }
      }
    }

    def insertSteps(no: Z, steps: ISZ[ProofAst.Step]): Unit = {
      for (step <- steps if map.isEmpty) {
        step match {
          case step: ProofAst.Step.SubProof =>
            insertSteps(no, step.steps)
            val pos = step.attr.posOpt.get
            if (map.isEmpty && pos.beginLine <= line && line <= pos.endLine) {
              insertBetween(no, step.steps)
              if (map.isEmpty) {
                val i = ops.StringOps.indexOfFrom(content, '(', pos.offset)
                insertPos(no, "", i + 1, pos.beginColumn + 2, if (step.steps.isEmpty) "" else ",")
              }
            }
          case step: ProofAst.Step.Let =>
            insertSteps(no, step.steps)
            val pos = step.attr.posOpt.get
            if (map.isEmpty && pos.beginLine <= line && line <= pos.endLine) {
              insertBetween(no, step.steps)
              if (map.isEmpty) {
                var i = ops.StringOps.stringIndexOfFrom(content, ProofStepInserter.rightArrow, pos.offset)
                i = ops.StringOps.indexOfFrom(content, '(', i + 2)
                insertPos(no, "", i + 1, pos.beginColumn + 2, if (step.steps.isEmpty) "" else ",")
              }
            }
          case _ =>
        }
        if (map.nonEmpty) {
          return
        }
        val pos = step.attr.posOpt.get
        if (pos.beginLine <= line && line <= pos.endLine) {
          insertStep(no, step)
        }
      }
    }

    def insertStep(no: Z, o: ProofAst.Step): Unit = {
      if (map.nonEmpty) {
        return
      }
      val pos = o.attr.posOpt.get
      val bpos: Position = if (o.id.isSynthetic) pos else o.id.posOpt.get
      insertPos(no, ",", pos.offset + pos.length, bpos.beginColumn - 1, "")
    }

    def insertPos(no: Z, comma: String, offset: Z, column: Z, endComma: String): Unit = {
      val indent: String = conversions.String.fromCis(for (_ <- 0 until column) yield ' ')
      val lines = ops.StringOps(insert).split((c: C) => c == '\n')
      var str: String = if (lines.isEmpty) "" else lines(0)
      for (i <- 1 until lines.size) {
        str = s"$str\n$indent${lines(i)}"
      }
      str = s"$comma\n$indent$no $str$endComma"
      map = map + offset ~> ("", str)
    }
  }

  object ProofStepInserter {
    val deduce: ISZ[C] = ISZ[C]('D', 'e', 'd', 'u', 'c', 'e')
    val proof: ISZ[C] = ISZ[C]('P', 'r', 'o', 'o', 'f')
    val lineCommentPrefix: ISZ[C] = ISZ[C]('/', '/')
    val rightArrow: ISZ[C] = ISZ[C]('=', '>')
  }

  @record class ConstructorValMapper(val content: ISZ[C], var map: HashMap[Z, (String, String)]) extends MTransformer {
    override def postAdtParam(o: AdtParam): MOption[AdtParam] = {
      if (o.isVal) {
        val offset = o.id.attr.posOpt.get.offset
        var i = offset - 1
        while (content(i).isWhitespace) {
          i = i - 1
        }
        if (!(content(i - 2) == 'v' && content(i - 1) == 'a' && content(i) == 'l')) {
          map = map + offset ~> (("", "val "))
        }
      }
      return MTransformer.PostResultAdtParam
    }
  }

  @record class PatternVarBindingCollector(var result: ISZ[Id]) extends MTransformer {
    override def postPatternVarBinding(o: Pattern.VarBinding): MOption[Pattern] = {
      result = result :+ o.id
      return MTransformer.PostResultPatternVarBinding
    }

    override def postPatternStructure(o: Pattern.Structure): MOption[Pattern] = {
      o.idOpt match {
        case Some(id) => result = result :+ id
        case _ =>
      }
      return MTransformer.PostResultPatternStructure
    }
  }

  @record class ProofStepUniquenessChecker(var map: HashMap[ProofAst.StepId, Position],
                                           val messageKind: String,
                                           val reporter: Reporter) extends MTransformer {
    override def preProofAstStep(o: ProofAst.Step): MTransformer.PreResult[ProofAst.Step] = {
      val no = o.id
      map.get(no) match {
        case Some(otherPos) => reporter.error(no.posOpt, messageKind,
          s"Proof step $no has been declared at [${otherPos.beginLine}, ${otherPos.beginColumn}]")
        case _ =>
          o.id.posOpt match {
            case Some(pos) => map = map + no ~> pos
            case _ =>
          }
      }
      return MTransformer.PreResultProofAstStepRegular
    }
  }

  @record class ContractFormChecker(val messageKind: String,
                                    val reporter: Reporter) extends MTransformer {
    override def preExpQuantEach(o: Exp.QuantEach): MTransformer.PreResult[Exp.Quant] = {
      transformExp(o.seq)
      transformAssignExp(o.fun.exp)
      return MTransformer.PreResult(F, MNone())
    }

    override def preExpQuantRange(o: Exp.QuantRange): MTransformer.PreResult[Exp.Quant] = {
      transformExp(o.lo)
      transformExp(o.hi)
      transformAssignExp(o.fun.exp)
      return MTransformer.PreResult(F, MNone())
    }

    override def preExpQuantType(o: Exp.QuantType): MTransformer.PreResult[Exp.Quant] = {
      transformAssignExp(o.fun.exp)
      return MTransformer.PreResult(F, MNone())
    }

    override def preExpFun(o: Exp.Fun): MTransformer.PreResult[Exp] = {
      reporter.error(o.posOpt, messageKind, "Contracts should not define function/closure")
      return MTransformer.PreResult(F, MNone())
    }
  }

  @datatype class TypePrePostSubstitutor(val substMap: HashMap[String, Typed]) extends Transformer.PrePost[B] {
    override def postTypedTypeVar(ctx: B, o: Typed.TypeVar): Transformer.TPostResult[B, Typed] = {
      substMap.get(o.id) match {
        case Some(t) => return Transformer.TPostResult(ctx, Some(t))
        case _ =>
      }
      return Transformer.TPostResult(ctx, None())
    }
  }

  @datatype class TypePrePostSubstitutorSkipRim(val substMap: HashMap[String, Typed]) extends Transformer.PrePost[B] {
    override def preResolvedInfoMethod(ctx: B, o: ResolvedInfo.Method): Transformer.PreResult[B, ResolvedInfo] = {
      return Transformer.PreResult(ctx, F, None())
    }

    override def postTypedTypeVar(ctx: B, o: Typed.TypeVar): Transformer.TPostResult[B, Typed] = {
      substMap.get(o.id) match {
        case Some(t) => return Transformer.TPostResult(ctx, Some(t))
        case _ =>
      }
      return Transformer.TPostResult(ctx, None())
    }
  }

  @record class LabeledExpMiner(val kind: String, var map: HashSMap[Z, Exp.Labeled], val reporter: Reporter) extends MTransformer {
    override def postExpLabeled(o: Exp.Labeled): MOption[Exp] = {
      val num = labeledNumOf(o)
      map.get(num) match {
        case Some(e) =>
          val pos = e.posOpt.get
          reporter.error(o.posOpt, kind, s"The same label has been declared at [${pos.beginLine}, ${pos.beginColumn}]")
        case _ =>
          map = map + num ~> o
      }
      return MNone()
    }
  }

  @datatype class LabeledExpAbstractor(nums: HashSet[Z]) extends Transformer.PrePost[B] {
    override def preExpLabeled(ctx: B, o: Exp.Labeled): Transformer.PreResult[B, Exp] = {
      val num = labeledNumOf(o)
      if (nums.contains(num)) {
        return Transformer.PreResult(ctx, T, Some(Exp.Sym(num, TypedAttr(o.posOpt, o.typedOpt))))
      } else {
        return Transformer.PreResult(ctx, T, None())
      }
    }
  }

  val nonConstantPrefixes: HashSet[String] = HashSet ++ ISZ[String]("proc")

  val symbolCharMap: HashMap[C, String] = HashMap ++ ISZ(
    '+' ~> "__plus",
    '-' ~> "__minus",
    '*' ~> "__star",
    '/' ~> "__slash",
    '%' ~> "__percent",
    '>' ~> "__gt",
    '<' ~> "__lt",
    '=' ~> "__eq",
    '!' ~> "__bang",
    '~' ~> "__tilde",
    '@' ~> "__at",
    '#' ~> "__pound",
    '$' ~> "__dollar",
    '^' ~> "__hat",
    '(' ~> "__lparen",
    ')' ~> "__rparen",
    '{' ~> "__lbrace",
    '}' ~> "__rbrace",
    '[' ~> "__lbracket",
    ']' ~> "__rbracket",
    ':' ~> "__colon",
    ';' ~> "__semi",
    '.' ~> "__dot",
    '?' ~> "__q",
    '\\' ~> "__bslash",
    ',' ~> "__comma"
  )

  object ProofStepTemplate {
    val regular: String = "(  CLAIM  ) by Premise"
    val assum: String = "Assume(  CLAIM  )"
    val asser: String = "Assert(  CLAIM, SubProof(\n))"
    val subProof: String = "SubProof(\n)"
    val let: String = "SubProof { (ID: TYPE) => (\n)}"
    val all: String = "∀((ID: TYPE) => CLAIM)"
    val exists: String = "∃((ID: TYPE) => CLAIM)"
    val allRange: String = "∀(LO until HI)(I => CLAIM)"
    val existsRange: String = "∃(LO until HI)(I => CLAIM)"
    val allEach: String = "∀(EXP)(E => CLAIM)"
    val existsEach: String = "∃(EXP)(E => CLAIM)"
    val allEachIndex: String = "∀(EXP.indices)(I => CLAIM)"
    val existsEachIndex: String = "∃(EXP.indices)(I => CLAIM)"
  }


  @pure def ids2strings(ids: ISZ[Id]): ISZ[String] = {
    val r = MSZ.create[String](ids.size, "")
    for (i <- ids.indices) {
      r(i) = ids(i).value
    }
    return r.toIS
  }

  @pure def fileUriOptEq(posOpt1: Option[Position], posOpt2: Option[Position]): B = {
    (posOpt1, posOpt2) match {
      case (Some(pos1), Some(pos2)) => return pos1.uriOpt == pos2.uriOpt
      case _ => return F
    }
  }

  @pure def beginColumn(posOpt: Option[Position]): Z = {
    //    l""" requires ∃pos: Position  posOpt == Some(pos) """
    posOpt match {
      case Some(pos) => return pos.beginColumn
      case _ => return 0
    }
  }

  @pure def beginColumnEqual(posOpt1: Option[Position], posOpt2: Option[Position]): B = {
    (posOpt1, posOpt2) match {
      case (Some(pos1), Some(pos2)) => return pos1.beginColumn == pos2.beginColumn
      case _ => return F
    }
  }

  @pure def posOptRangeA(attr1: Attr, attr2: Attr): Option[Position] = {
    return posOptRange(attr1.posOpt, attr2.posOpt)
  }

  @pure def posOptRangeTA(attr1: TypedAttr, attr2: TypedAttr): Option[Position] = {
    return posOptRange(attr1.posOpt, attr2.posOpt)
  }

  @pure def posOptRangeRA(attr1: ResolvedAttr, attr2: ResolvedAttr): Option[Position] = {
    return posOptRange(attr1.posOpt, attr2.posOpt)
  }

  @pure def posOptRange(posOpt1: Option[Position], posOpt2: Option[Position]): Option[Position] = {
    (posOpt1, posOpt2) match {
      case (Some(pos1: PosInfo), Some(pos2: PosInfo)) =>
        val offset = pos1.offset
        val length = pos2.offset + pos2.length - offset
        return Some(PosInfo(pos1.info, (conversions.Z.toU64(offset) << u64"32") | conversions.Z.toU64(length)))
      case (Some(pos1), Some(pos2)) =>
        return Some(
          FlatPos(
            pos1.uriOpt,
            conversions.Z.toU32(pos1.beginLine),
            conversions.Z.toU32(pos1.beginColumn),
            conversions.Z.toU32(pos2.endLine),
            conversions.Z.toU32(pos2.endColumn),
            conversions.Z.toU32(pos1.offset),
            conversions.Z.toU32(pos2.offset + pos2.length - pos1.length)
          )
        )
      case _ => return None()
    }
  }

  def unop(op: Exp.UnaryOp.Type): String = {
    op match {
      case Exp.UnaryOp.Not => return "!"
      case Exp.UnaryOp.Plus => return "+"
      case Exp.UnaryOp.Minus => return "-"
      case Exp.UnaryOp.Complement => return "~"
    }
  }

  def unopId(op: Exp.UnaryOp.Type): String = {
    op match {
      case Exp.UnaryOp.Not => return "unary_!"
      case Exp.UnaryOp.Plus => return "unary_+"
      case Exp.UnaryOp.Minus => return "unary_-"
      case Exp.UnaryOp.Complement => return "unary_~"
    }
  }

  @pure def isBoolBinop(op: String): B = {
    op match {
      case Exp.BinaryOp.Eq => return T
      case Exp.BinaryOp.Equiv => return T
      case Exp.BinaryOp.Ne => return T
      case Exp.BinaryOp.And => return T
      case Exp.BinaryOp.Or => return T
      case Exp.BinaryOp.Xor => return T
      case Exp.BinaryOp.Imply => return T
      case Exp.BinaryOp.CondAnd => return T
      case Exp.BinaryOp.CondOr => return T
      case Exp.BinaryOp.CondImply => return T
      case Exp.BinaryOp.Arrow => return T
      case "->:" => return T
      case "-->:" => return T
      case _ => return F
    }
  }

  @pure def isArithBinop(op: String): B = {
    op match {
      case Exp.BinaryOp.Add => return T
      case Exp.BinaryOp.Sub => return T
      case Exp.BinaryOp.Mul => return T
      case Exp.BinaryOp.Div => return T
      case Exp.BinaryOp.Rem => return T
      case _ => return F
    }
  }

  @pure def isBitsBinop(op: String): B = {
    op match {
      case Exp.BinaryOp.Shl => return T
      case Exp.BinaryOp.Shr => return T
      case Exp.BinaryOp.Ushr => return T
      case Exp.BinaryOp.And => return T
      case Exp.BinaryOp.Or => return T
      case Exp.BinaryOp.Xor => return T
      case _ => return F
    }
  }

  @pure def isCompareBinop(op: String): B = {
    op match {
      case Exp.BinaryOp.Eq => return T
      case Exp.BinaryOp.Equiv => return T
      case Exp.BinaryOp.Ne => return T
      case Exp.BinaryOp.Lt => return T
      case Exp.BinaryOp.Le => return T
      case Exp.BinaryOp.Gt => return T
      case Exp.BinaryOp.Ge => return T
      case _ => return F
    }
  }

  @pure def isFpEqBinop(op: String): B = {
    op match {
      case string"!~" => return T
      case string"~~" => return T
      case _ => return F
    }
  }

  @pure def substAssignExp(ast: AssignExp, substMap: HashMap[String, Typed]): AssignExp = {
    if (substMap.nonEmpty) {
      return Transformer(TypePrePostSubstitutor(substMap)).transformAssignExp(F, ast).resultOpt.getOrElse(ast)
    } else {
      return ast
    }
  }

  @pure def substExp(ast: Exp, substMap: HashMap[String, Typed]): Exp = {
    if (substMap.nonEmpty) {
      return Transformer(TypePrePostSubstitutor(substMap)).transformExp(F, ast).resultOpt.getOrElse(ast)
    } else {
      return ast
    }
  }

  @pure def substExpSkipResolvedInfo(ast: Exp, substMap: HashMap[String, Typed]): Exp = {
    if (substMap.nonEmpty) {
      return Transformer(TypePrePostSubstitutorSkipRim(substMap)).transformExp(F, ast).resultOpt.getOrElse(ast)
    } else {
      return ast
    }
  }

  @pure def stableTypeSig(t: Typed, width: Z): ST = {
    val max: Z = if (0 < width && width <= 64) width else 64
    val bytes = ops.ISZOps(crypto.SHA3.sum512(conversions.String.toU8is(t.string))).take(max)
    var cs = ISZ[C]()
    for (b <- bytes) {
      val c = conversions.U32.toC(conversions.U8.toU32(b))
      cs = cs :+ ops.COps.hex2c((c >>> '\u0004') & '\u000F')
      cs = cs :+ ops.COps.hex2c(c & '\u000F')
    }
    return st"$cs"
  }

  @pure def stableTypeId(t: Typed, width: Z): (ST, B) = {
    t match {
      case t: Typed.Name => return if (t.args.isEmpty) (mangleName(t.ids), F) else (st"${mangleName(t.ids)}_${stableTypeSig(t, width)}", T)
      case t: Typed.Tuple => return (st"Tuple${t.args.size}_${stableTypeSig(t, width)}", T)
      case t: Typed.Fun => return (st"Fun${t.args.size}_${stableTypeSig(t, width)}", T)
      case _ => halt(s"Infeasible: $t")
    }
  }

  @pure def mangleName(ids: ISZ[String]): ST = {
    val r: ST =
      ids.size match {
        case z"0" => st"top"
        case z"1" => st"top_${ids(0)}"
        case _ => st"${(Typed.short(ids).map(encodeId _), "_")}"
      }
    return r
  }

  @pure def encodeId(id: String): ST = {
    id match {
      case Exp.BinaryOp.Add => return st"_add"
      case Exp.BinaryOp.Sub => return st"_sub"
      case Exp.BinaryOp.Mul => return st"_mul"
      case Exp.BinaryOp.Div => return st"_div"
      case Exp.BinaryOp.Rem => return st"_rem"
      case Exp.BinaryOp.Eq => return st"_eq"
      case Exp.BinaryOp.Ne => return st"_ne"
      case Exp.BinaryOp.Shl => return st"_lt"
      case Exp.BinaryOp.Shr => return st"_le"
      case Exp.BinaryOp.Ushr => return st"_gt"
      case Exp.BinaryOp.Lt => return st"_ge"
      case Exp.BinaryOp.Le => return st"_shl"
      case Exp.BinaryOp.Gt => return st"_shr"
      case Exp.BinaryOp.Ge => return st"_ushr"
      case Exp.BinaryOp.And => return st"_and"
      case Exp.BinaryOp.Or => return st"_or"
      case Exp.BinaryOp.Xor => return st"_xor"
      case Exp.BinaryOp.Append => return st"_append"
      case Exp.BinaryOp.Prepend => return st"_prepend"
      case Exp.BinaryOp.AppendAll => return st"_appendAll"
      case Exp.BinaryOp.RemoveAll => return st"_removeAll"
      case _ =>
        val cis = conversions.String.toCis(id)
        if (ops.ISZOps(cis).exists((c: C) => symbolCharMap.contains(c))) {
          var r = ISZ[String]()
          for (c <- cis) {
            symbolCharMap.get(c) match {
              case Some(s) => r = r :+ s
              case _ => r = r :+ s"$c"
            }
          }
          return st"$r"
        } else {
          return st"$id"
        }
    }
  }

  @pure def isSeq(t: Typed): B = {
    t match {
      case t: Typed.Name => return t.ids == Typed.isName || t.ids == Typed.msName
      case _ => return F
    }
  }

  @pure def isSpec(exp: Exp): B = {
    exp match {
      case _: Exp.Quant => return T
      case _: Exp.Input => return T
      case _: Exp.At => return T
      case _: Exp.LoopIndex => return T
      case _: Exp.StateSeq => return T
      case _: Exp.Result => return T
      case _ =>
    }
    return F
  }

  def checkScript(script: TopUnit.Program, reporter: Reporter): Unit = {
    for (stmt <- script.body.stmts) {
      def checkId(id: Id): Unit = {
        if (id.value == "args") {
          reporter.error(id.attr.posOpt, "Slang front-end", "Cannot introduce a top-level entity named args in Slang script")
        }
      }

      stmt match {
        case stmt: Stmt.Method => checkId(stmt.sig.id)
        case stmt: Stmt.Var => checkId(stmt.id)
        case stmt: Stmt.SpecMethod => checkId(stmt.sig.id)
        case stmt: Stmt.Fact => checkId(stmt.id)
        case stmt: Stmt.SpecVar => checkId(stmt.id)
        case stmt: Stmt.Theorem => checkId(stmt.id)
        case stmt: Stmt.VarPattern =>
          val pvbc = Util.PatternVarBindingCollector(ISZ())
          pvbc.transformPattern(stmt.pattern)
          for (id <- pvbc.result) {
            checkId(id)
          }
        case stmt: Stmt.Object => checkId(stmt.id)
        case _ =>
      }
    }
  }

  @pure def areAllStepIds(es: ISZ[Exp]): B = {
    for (e <- es) {
      e match {
        case _: Exp.LitZ =>
        case _: Exp.LitString =>
        case _ => return F
      }
    }
    return T
  }

  @pure def toStepIds(es: ISZ[Exp], kind: String, reporter: Reporter): Option[ISZ[ProofAst.StepId]] = {
    var r = ISZ[ProofAst.StepId]()
    for (e <- es) {
      e match {
        case e: Exp.LitZ if e.value >= 0 => r = r :+ ProofAst.StepId.Num(e.value, e.attr)
        case e: Exp.LitString => r = r :+ ProofAst.StepId.Str(F, e.value, e.attr)
        case e: ProofAst.StepId => r = r :+ e
        case _ =>
          reporter.error(e.posOpt, kind, "Expecting only a non-negative integer literal or a step name literal")
      }
    }
    return if (r.size != es.size) None() else Some(r)
  }

  @pure def insertConstructorVal(text: String, topUnit: TopUnit): (String, Z) = {
    val content = conversions.String.toCis(text)
    val trans = ConstructorValMapper(content, HashMap.empty)
    trans.transformTopUnit(topUnit)
    val n = trans.map.size
    if (n == 0) {
      return (text, 0)
    }
    ops.StringOps.replace(content, trans.map) match {
      case Either.Left(value) => return (value, n)
      case Either.Right(message) => halt(s"Internal error: $message")
    }
  }

  @pure def renumberProofSteps(text: String, topUnit: TopUnit, reporter: Reporter): (String, Z) = {
    val content = conversions.String.toCis(text)
    val trans = ProofStepsNumberMapper(1, HashMap.empty, HashMap.empty, Reporter.create)
    trans.transformTopUnit(topUnit)
    reporter.reports(trans.reporter.messages)
    val n = trans.map.size
    if (n == 0 || reporter.hasError) {
      return (text, 0)
    }
    ops.StringOps.replace(content, trans.map) match {
      case Either.Left(value) => return (value, n)
      case Either.Right(message) => halt(s"Internal error: $message")
    }
  }

  @pure def replaceEnumSymbols(text: String, topUnit: TopUnit): (String, Z) = {
    val content = conversions.String.toCis(text)
    val trans = EnumSymbolMapper(content, HashMap.empty)
    trans.transformTopUnit(topUnit)
    val n = trans.map.size
    if (n == 0) {
      return (text, 0)
    }
    ops.StringOps.replace(content, trans.map) match {
      case Either.Left(value) => return (value, n)
      case Either.Right(message) => halt(s"Internal error: $message")
    }
  }

  @pure def reformatProof(text: String, topUnit: TopUnit): Option[(String, Z)] = {
    val pr = ProofReformatter(DocInfo.create(topUnit.fileUriOpt, text),
      conversions.String.toCis(text), HashMap.empty)
    pr.transformTopUnit(topUnit)
    ops.StringOps.replace(pr.content, pr.map) match {
      case Either.Left(r) => return Some((r, pr.map.size))
      case Either.Right(msg) => return None()
    }
  }

  @pure def insertProofStep(text: String, topUnit: TopUnit, insert: String, line: Z): Option[String] = {
    val psi = ProofStepInserter(DocInfo.create(topUnit.fileUriOpt, text), line, conversions.String.toCis(text),
      insert, HashMap.empty)
    psi.transformTopUnit(topUnit)
    if (psi.map.isEmpty) {
      return None()
    } else {
      ops.StringOps.replace(psi.content, psi.map) match {
        case Either.Left(r) => return Some(r)
        case Either.Right(msg) => halt(s"Infeasible: $msg")
      }
    }
  }

  @pure def constantInitOpt(initOpt: Option[AssignExp], tOpt: Option[Typed]): Option[Exp] = {
    initOpt match {
      case Some(Stmt.Expr(exp)) =>
        exp match {
          case _: Exp.LitZ =>
          case _: Exp.LitB =>
          case _: Exp.LitC =>
          case _: Exp.LitString =>
          case _: Exp.LitF32 =>
          case _: Exp.LitF64 =>
          case _: Exp.LitR =>
          case exp: Exp.Ident =>
            exp.attr.resOpt match {
              case Some(res: ResolvedInfo.Var) if res.isInObject && (res.id == "T" || res.id == "F") &&
                res.owner == Typed.sireumName =>
              case _ =>
                tOpt match {
                  case Some(Typed.b) if exp.id.value == "T" || exp.id.value == "F" =>
                    return Some(Exp.LitB(exp.id.value == "T", Attr(exp.attr.posOpt)))
                  case _ => return None()
                }
            }
          case exp: Exp.StringInterpolate if exp.args.isEmpty && !nonConstantPrefixes.contains(exp.prefix) =>
          case _ => return None()
        }
        return Some(exp)
      case _ =>
    }
    return None()
  }

  @strictpure def labeledNumOf(exp: Exp.Labeled): Z = exp.numOpt match {
    case Some(num) => num.value
    case _ => 0
  }

  def mineLabeledExps(kind: String, exp: Exp, reporter: Reporter): HashSMap[Z, Exp.Labeled] = {
    val lem = Util.LabeledExpMiner(kind, HashSMap.empty, Reporter.create)
    lem.transformExp(exp)
    reporter.reports(lem.reporter.messages)
    return lem.map
  }

  @pure def abstractLabeledExps(exp: Exp, nums: HashSet[Z]): Exp = {
    val lea = Transformer(Util.LabeledExpAbstractor(nums))
    return lea.transformExp(F, exp).resultOpt.getOrElse(exp)
  }

  @pure def invokeReceiverIdent(receiverOpt: Option[Exp], ident: Exp): (B, Option[Exp], Exp.Ident) = {
    (receiverOpt, ident) match {
      case (_, ident: Exp.Ident) => return (F, receiverOpt, ident)
      case (None(), ident: Exp.Select) => return (F, ident.receiverOpt, Exp.Ident(ident.id, ident.attr))
      case (None(), _) => return (T, Some(ident), Exp.Ident(Id("apply", Attr(ident.posOpt)), ResolvedAttr(
        ident.posOpt, Some(ResolvedInfo.BuiltIn(ResolvedInfo.BuiltIn.Kind.Apply)), ident.typedOpt)))
      case (_, _) => halt(s"Infeasible: $receiverOpt.$ident")
    }
  }

  @pure def getLhsGroundExp(thisOwner: ISZ[String], thisOpt: Option[Typed], lhs: Exp): Option[Exp] = {
    lhs match {
      case lhs: Exp.Ident =>
        lhs.resOpt match {
          case Some(_: ResolvedInfo.LocalVar) => return Some(lhs)
          case Some(res: ResolvedInfo.Var) =>
            return Some(if (res.isInObject) lhs else Exp.This(thisOwner, TypedAttr(lhs.posOpt, thisOpt)))
          case _ => return None()
        }
      case lhs: Exp.Select =>
        lhs.resOpt match {
          case Some(_: ResolvedInfo.LocalVar) => return Some(lhs)
          case Some(res: ResolvedInfo.Var) if res.isInObject => return Some(lhs)
          case _ => return getLhsGroundExp(thisOwner, thisOpt, lhs.receiverOpt.get)
        }
      case lhs: Exp.This => return Some(lhs)
      case lhs: Exp.Invoke =>
        lhs.receiverOpt match {
          case Some(rcv) => return getLhsGroundExp(thisOwner, thisOpt, rcv)
          case _ => return getLhsGroundExp(thisOwner, thisOpt, lhs.ident)
        }
      case _ => return None()
    }
  }

  val trueLit: Exp.LitB = Exp.LitB(T, Attr(None()))
  val falseLit: Exp = Exp.LitB(F, trueLit.attr)

  @pure def esToBinary(es: ISZ[Exp], dflt: Exp, op: String, tOpt: Option[Typed], res: ResolvedInfo, pOpt: Option[Position]): Exp = {
    if (es.isEmpty) {
      return dflt
    }
    var r = es(0)
    for (i <- 1 until es.size) {
      r = Exp.Binary(r, op, es(i), ResolvedAttr(pOpt, Some(res), tOpt), pOpt)
    }
    return r
  }

  @pure def bigAnd(exps: ISZ[Exp], pOpt: Option[Position]): Exp = {
    var set = HashSSet.empty[Exp]
    for (exp <- exps) {
      if (exp == trueLit) {
        // skip
      } else if (exp == falseLit) {
        return falseLit
      } else {
        set = set + exp
      }
    }
    return esToBinary(set.elements, trueLit, Exp.BinaryOp.And,
      Typed.bOpt, ResolvedInfo.BuiltIn(ResolvedInfo.BuiltIn.Kind.BinaryAnd), pOpt)
  }

  @pure def bigOr(exps: ISZ[Exp], pOpt: Option[Position]): Exp = {
    var set = HashSSet.empty[Exp]
    for (exp <- exps) {
      if (exp == trueLit) {
        return trueLit
      } else if (exp == falseLit) {
        // skip
      } else {
        set = set + exp
      }
    }
    return esToBinary(set.elements, falseLit, Exp.BinaryOp.Or,
      Typed.bOpt, ResolvedInfo.BuiltIn(ResolvedInfo.BuiltIn.Kind.BinaryOr), pOpt)
  }

  @pure def bigImply(isCond: B, exps: ISZ[Exp], pOpt: Option[Position]): Exp = {
    var es = ISZ[Exp]()
    for (i <- 0 until exps.size - 1) {
      val exp = exps(i)
      if (exp == falseLit) {
        return trueLit
      } else {
        es = es :+ exp
      }
    }
    es = es :+ exps(exps.size - 1)
    assert(es.size >= 2)
    var r = es(es.size - 1)
    val op: String = if (isCond) Exp.BinaryOp.CondImply else Exp.BinaryOp.Imply
    val res = ResolvedInfo.BuiltIn(if (isCond) ResolvedInfo.BuiltIn.Kind.BinaryCondImply else
      ResolvedInfo.BuiltIn.Kind.BinaryImply)
    var i = es.size - 2
    while (i >= 0) {
      if (r != trueLit) {
        r = Exp.Binary(es(i), op, r, ResolvedAttr(pOpt, Some(res), Typed.bOpt), pOpt)
      }
      i = i - 1
    }
    return r
  }

  @pure def ite(cond: Exp, left: Exp, right: Exp, pOpt: Option[Position], tOpt: Option[Typed]): Exp = {
    if (right == trueLit) {
      if (left == trueLit) {
        return trueLit
      }
      return Exp.Binary(cond, Exp.BinaryOp.CondImply, left, ResolvedAttr(pOpt,
        Some(ResolvedInfo.BuiltIn(ResolvedInfo.BuiltIn.Kind.BinaryCondImply)), Typed.bOpt), pOpt)
    } else if (right == falseLit) {
      return Exp.Binary(cond, Exp.BinaryOp.CondAnd, left, ResolvedAttr(pOpt,
        Some(ResolvedInfo.BuiltIn(ResolvedInfo.BuiltIn.Kind.BinaryCondAnd)), Typed.bOpt), pOpt)
    } else if (left == trueLit) {
      return Exp.Binary(cond, Exp.BinaryOp.CondOr, right, ResolvedAttr(pOpt,
        Some(ResolvedInfo.BuiltIn(ResolvedInfo.BuiltIn.Kind.BinaryCondOr)), Typed.bOpt), pOpt)
    }
    return Exp.If(cond, left, right, TypedAttr(pOpt, tOpt))
  }

  @pure def typedToType(t: Typed, pos: Position): Type = {
    val attr = Attr(Some(pos))
    val typedAttr = TypedAttr(attr.posOpt, Some(t))

    @pure def toName(ids: ISZ[String]): Name = {
      ids match {
        case ISZ(string"org", string"sireum", _*) if ids.size > 2 =>
          return org.sireum.lang.ast.Name(for (i <- 2 until ids.size) yield Id(ids(i), attr), attr)
        case _ => return org.sireum.lang.ast.Name(for (id <- ids) yield Id(id, attr), attr)
      }
    }

    t match {
      case t: Typed.Name => return Type.Named(toName(t.ids),
        for (arg <- t.args) yield typedToType(arg, pos), typedAttr)
      case t: Typed.TypeVar => return Type.Named(toName(ISZ(t.id)), ISZ(), typedAttr)
      case t: Typed.Tuple => return Type.Tuple(for (arg <- t.args) yield typedToType(arg, pos), typedAttr)
      case t: Typed.Enum => return Type.Named(toName(t.name), ISZ(), typedAttr)
      case t: Typed.Fun => return Type.Fun(t.isPureFun, t.isByName, for (arg <- t.args) yield typedToType(arg, pos),
        typedToType(t.ret, pos), typedAttr)
      case t: Typed.Method => halt(s"Infeasible: $t")
      case t: Typed.Object => halt(s"Infeasible: $t")
      case t: Typed.Methods => halt(s"Infeasible: $t")
      case t: Typed.Package => halt(s"Infeasible: $t")
      case t: Typed.Fact => halt(s"Infeasible: $t")
      case t: Typed.Theorem => halt(s"Infeasible: $t")
      case t: Typed.Inv => halt(s"Infeasible: $t")
    }
  }
}
