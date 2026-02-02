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
package org.sireum.lang.parser

import org.sireum._
import org.sireum.message._
import org.sireum.lang.{ast => AST}
import org.sireum.parser.ParseTree

object SlangTruthTableParser {
  val kind: String = "Slang Truth Table Parser"

  @record class WellformednessChecker(val idMap: HashMap[String, Position],
                                      val reporter: Reporter) extends AST.MTransformer {
    override def preExp(o: AST.Exp): AST.MTransformer.PreResult[AST.Exp] = {
      o match {
        case o: AST.Exp.Ident =>
          if (!idMap.contains(o.id.value)) {
            reporter.error(o.posOpt, kind, s"Undeclared variable '${o.id.value}''")
            return AST.MTransformer.PreResult(F, MNone())
          }
        case o: AST.Exp.Binary =>
          o.op match {
            case AST.Exp.BinaryOp.And =>
            case AST.Exp.BinaryOp.Or =>
            case "->:" =>
            case _ =>
              reporter.error(o.posOpt, kind, s"Invalid truth table binary operator '${o.op}'")
              return AST.MTransformer.PreResult(F, MNone())
          }
        case o: AST.Exp.Unary =>
          o.op match {
            case AST.Exp.UnaryOp.Not =>
            case AST.Exp.UnaryOp.Complement =>
            case _ =>
              reporter.error(o.posOpt, kind, s"Invalid truth table unary operator '${o.op}'")
              return AST.MTransformer.PreResult(F, MNone())
          }
        case _ =>
          reporter.error(o.posOpt, kind, "Invalid truth table expression")
          return AST.MTransformer.PreResult(F, MNone())
      }
      return AST.MTransformer.PreResult(T, MNone())
    }
  }

  def parse(uriOpt: Option[String], text: String,
            reporter: Reporter): Option[AST.TopUnit.TruthTableUnit] = {
    def build(tree: ParseTree): Option[AST.TopUnit.TruthTableUnit] = {
      def boolValue(t: ParseTree): AST.Exp.LitB = {
        val leaf = t.asInstanceOf[ParseTree.Leaf]
        val attr = AST.Attr(leaf.posOpt)
        leaf.text.native match {
          case "T" => return AST.Exp.LitB(T, attr)
          case "F" => return AST.Exp.LitB(F, attr)
          case "1" => return AST.Exp.LitB(T, attr)
          case "0" => return AST.Exp.LitB(F, attr)
          case _ =>
            reporter.error(attr.posOpt, kind, "Only T, F, 1, or 0 is allowed")
            return AST.Exp.LitB(F, attr)
        }
      }

      def assignment(t: ParseTree): AST.TruthTable.Assignment = {
        var values = ISZ[AST.Exp.LitB]()
        t.asInstanceOf[ParseTree.Node].children(1) match {
          case others: ParseTree.Node =>
            for (other <- others.children) {
              values = values :+ boolValue(other)
            }
          case _ =>
        }
        return AST.TruthTable.Assignment(values, AST.Attr(Some(values(0).posOpt.get.to(values(values.size - 1).posOpt.get))))
      }

      assert(tree.ruleName == "file")
      val ptableNode: ParseTree.Node = tree.asInstanceOf[ParseTree.Node].children match {
        case ISZ(pt: ParseTree.Node, _) => pt
        case ISZ(_, pt: ParseTree.Node, _) => pt
      }
      val ISZ(pstars, _, pheader, _, prows, _, _*) = ptableNode.children
      val stars: ISZ[Position] = {
        var r = ISZ[Position]()
        var i = 0
        val pstarsChildren = pstars.asInstanceOf[ParseTree.Node].children
        var end = F
        while (i < pstarsChildren.size) {
          val leaf = pstarsChildren(i).asInstanceOf[ParseTree.Leaf]
          leaf.text.native match {
            case "*" =>
              if (end) {
                reporter.error(leaf.posOpt, kind, s"All '*' have to be in the same line")
              } else {
                r = r :+ leaf.posOpt.get
              }
            case "\n" =>
              end = T
            case _ => reporter.error(leaf.posOpt, kind, s"Expecting '*', but found '${leaf.text}'")
          }
          i = i + 1
        }
        r
      }
      var idMap = HashMap.empty[String, Position]
      val (vars, separator, isSequent, sequent): (ISZ[AST.Id], Position, B, AST.Sequent) = {
        val (others1, hash, tokens): (ISZ[ParseTree], ParseTree.Leaf, ISZ[ParseTree]) = pheader.asInstanceOf[ParseTree.Node].children match {
          case ISZ(o1, hash: ParseTree.Leaf, o2, _: ParseTree.Leaf, _*) => (o1.asInstanceOf[ParseTree.Node].children, hash, o2.asInstanceOf[ParseTree.Node].children)
          case ISZ(hash: ParseTree.Leaf, o2, _: ParseTree.Leaf, _*) => (ISZ(), hash, o2.asInstanceOf[ParseTree.Node].children)
          case ISZ(o1, hash: ParseTree.Leaf, _: ParseTree.Leaf, _*) => (o1.asInstanceOf[ParseTree.Node].children, hash, ISZ())
          case ISZ(hash: ParseTree.Leaf, _: ParseTree.Leaf, _*) => (ISZ(), hash, ISZ())
        }
        var ids = ISZ[AST.Id]()
        for (x <- others1) {
          val leaf = x.asInstanceOf[ParseTree.Leaf]
          val id = leaf.text
          idMap.get(id) match {
            case Some(other) =>
              reporter.error(leaf.posOpt, kind, s"'$id' has been declared at [${other.beginLine} ,${other.beginColumn}]")
            case _ =>
              assert(id.size == 1)
              val c = ops.StringOps(id).first
              if (!(('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z'))) {
                reporter.error(leaf.posOpt, kind, s"Only [a-z, A-Z] is allowed")
              }
              idMap = idMap + id ~> leaf.posOpt.get
              ids = ids :+ AST.Id(id, AST.Attr(leaf.posOpt))
          }
        }
        @strictpure def emptySeq: AST.Sequent = AST.Sequent(ISZ(), AST.Exp.LitB(F, AST.Attr(None())), ISZ(), AST.Attr(None()))
        var iseq = F
        val sqnt: AST.Sequent = if (tokens.nonEmpty) {
          val startPos = tokens(0).asInstanceOf[ParseTree.Leaf].posOpt.get
          val start = startPos.offset
          val endPos = tokens(tokens.size - 1).asInstanceOf[ParseTree.Leaf].posOpt.get
          val end = endPos.offset + endPos.length
          val cms = conversions.String.toCms(text)
          for (i <- 0 until cms.size) {
            if ((i < start || i >= end) && !cms(i).isWhitespace) {
              cms(i) = ' '
            } else if (cms(i) == '⊢' || (cms(i) == '|' && i < cms.size - 1 && cms(i + 1) == '-')) {
              iseq = T
            }
          }
          val input = conversions.String.fromCms(cms)

          if (iseq) {
            Parser.parseSequentOpt(uriOpt, input, T, reporter) match {
              case Some(s) => s
              case _ => emptySeq
            }
          } else {
            Parser.parseExpOpt(uriOpt, input, T, reporter) match {
              case Some(e) => AST.Sequent(ISZ(), e, ISZ(), AST.Attr(e.posOpt))
              case _ => emptySeq
            }
          }
        } else {
          reporter.error(None(), kind, "Missing truth table formula or sequent")
          emptySeq
        }
        (ids, hash.posOpt.get, iseq, sqnt)
      }
      var rows = ISZ[AST.TruthTable.Row]()
      for (prow <- prows.asInstanceOf[ParseTree.Node].children) {
        val (passignment, hash, pvalues): (ISZ[ParseTree], ParseTree.Leaf, ISZ[ParseTree]) = prow.asInstanceOf[ParseTree.Node].children match {
          case ISZ(passignment, hash: ParseTree.Leaf, pvalues, _) => (passignment.asInstanceOf[ParseTree.Node].children, hash, pvalues.asInstanceOf[ParseTree.Node].children)
          case ISZ(hash: ParseTree.Leaf, pvalues, _) => (ISZ(), hash, pvalues.asInstanceOf[ParseTree.Node].children)
          case ISZ(passignment, hash: ParseTree.Leaf, _) => (passignment.asInstanceOf[ParseTree.Node].children, hash, ISZ())
          case ISZ(hash: ParseTree.Leaf, _) => (ISZ(), hash, ISZ())
        }
        var assignment = ISZ[AST.Exp.LitB]()
        for (pvalue <- passignment) {
          assignment = assignment :+ boolValue(pvalue)
        }
        val aAttr: AST.Attr = if (assignment.nonEmpty)
          AST.Attr(Some(assignment(0).posOpt.get.to(assignment(assignment.size - 1).posOpt.get)))
        else AST.Attr(None())
        var values = ISZ[AST.Exp.LitB]()
        for (pvalue <- pvalues) {
          values = values :+ boolValue(pvalue)
        }
        val vAttr: AST.Attr = if (values.nonEmpty)
          AST.Attr(Some(values(0).posOpt.get.to(values(values.size - 1).posOpt.get)))
        else AST.Attr(None())
        rows = rows :+ AST.TruthTable.Row(
          AST.TruthTable.Assignment(assignment, aAttr),
          hash.posOpt.get,
          AST.TruthTable.Assignment(values, vAttr)
        )
      }
      val conclusionOpt: Option[AST.TruthTable.Conclusion] = if (ptableNode.children(ptableNode.children.size - 1).ruleName == "conclusion") {
        val pconclusion = ptableNode.children(ptableNode.children.size - 1).asInstanceOf[ParseTree.Node].children
        val pconclusionFirst = pconclusion(0).asInstanceOf[ParseTree.Leaf]
        pconclusionFirst.ruleName match {
          case "'Tautology'" => Some(AST.TruthTable.Conclusion.Tautology(AST.Attr(pconclusionFirst.posOpt)))
          case "'Contradictory'" => Some(AST.TruthTable.Conclusion.Contradictory(AST.Attr(pconclusionFirst.posOpt)))
          case "'Contingent'" =>
            var tPosOpt = Option.none[Position]()
            var fPosOpt = Option.none[Position]()
            var tAssignments = ISZ[AST.TruthTable.Assignment]()
            var fAssignments = ISZ[AST.TruthTable.Assignment]()
            for (i <- 1 until pconclusion.size if pconclusion(i).ruleName == "cas") {
              val cas = pconclusion(i).asInstanceOf[ParseTree.Node].children
              val b = boolValue(cas(0))
              if (b.value) {
                if (tPosOpt.nonEmpty) {
                  reporter.error(b.posOpt, kind, s"T contingent assignment has been defined at [${tPosOpt.get.beginLine}, ${tPosOpt.get.beginColumn}]")
                }
                tPosOpt = b.posOpt
              } else {
                if (fPosOpt.nonEmpty) {
                  reporter.error(b.posOpt, kind, s"F contingent assignment has been defined at [${fPosOpt.get.beginLine}, ${fPosOpt.get.beginColumn}]")
                }
                fPosOpt = b.posOpt
              }
              val colon = cas(1).asInstanceOf[ParseTree.Leaf]
              if (colon.text != ":") {
                reporter.error(colon.posOpt, kind, "Expecting a colon (':')")
              }
              var assignments = ISZ[AST.TruthTable.Assignment]()
              for (j <- 2 until cas.size) {
                assignments = assignments :+ assignment(cas(j))
              }
              if (b.value) {
                tAssignments = assignments
              } else {
                fAssignments = assignments
              }
            }
            Some(AST.TruthTable.Conclusion.Contingent(tAssignments, fAssignments, AST.Attr(pconclusionFirst.posOpt)))
          case _ =>
            assert(pconclusionFirst.ruleName == "'Valid'" || pconclusionFirst.ruleName == "'Invalid'")
            var assigments = ISZ[AST.TruthTable.Assignment]()
            for (i <- 1 until pconclusion.size if pconclusion(i).ruleName == "assign") {
              assigments = assigments :+ assignment(pconclusion(i))
            }
            Some(AST.TruthTable.Conclusion.Validity(pconclusionFirst.ruleName == "'Valid'", assigments, AST.Attr(pconclusionFirst.posOpt)))
        }
      } else {
        None()
      }

      if (reporter.hasError) {
        return None()
      }

      val wc = WellformednessChecker(idMap, Reporter.create)
      wc.transformSequent(sequent)
      reporter.reports(wc.reporter.messages)

      return if (reporter.hasError) None() else Some(AST.TopUnit.TruthTableUnit(uriOpt, stars, vars, separator,
        isSequent, sequent, rows, conclusionOpt))
    }
    SlangTruthTableLl1Parser.parse(uriOpt, text, reporter) match {
      case Some(tree) => return build(tree)
      case _ => return None()
    }
  }
}
