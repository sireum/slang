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

object SlangDefinition {

  // -----------------------------------------------------------------------
  // Public entry point
  // -----------------------------------------------------------------------

  def findDefinition(
    program: AST.TopUnit.Program,
    th: TypeHierarchy,
    line: Z,
    column: Z
  ): Option[Position] = {
    val walker = DefWalker(th = th, line = line, column = column,
      bestSpan = -1, bestResult = None())
    walker.transformTopUnit(program)
    return walker.bestResult
  }

  // -----------------------------------------------------------------------
  // Span helpers
  // -----------------------------------------------------------------------

  // Returns the total line-weighted span used to pick the *innermost* node.
  // Smaller is better (tighter containment).  We use (endLine - beginLine)
  // as a coarse primary key so a single-line node beats a multi-line one.
  // A value of -1 means "no candidate yet" (always replaced).
  @strictpure def spanOf(pos: Position): Z =
    (pos.endLine - pos.beginLine) * 100000 + (pos.endColumn - pos.beginColumn)

  // True when (line, column) falls within `pos`.
  def containsCursor(pos: Position, line: Z, column: Z): B = {
    if (line < pos.beginLine || line > pos.endLine) {
      return F
    }
    if (pos.beginLine == pos.endLine) {
      // single-line span: check column range (columns are 1-based)
      return column >= pos.beginColumn && column <= pos.endColumn
    }
    if (line == pos.beginLine) {
      return column >= pos.beginColumn
    }
    if (line == pos.endLine) {
      return column <= pos.endColumn
    }
    return T
  }

  // -----------------------------------------------------------------------
  // Definition extraction from a resolved expression
  // -----------------------------------------------------------------------

  def defPosFromExp(exp: AST.Exp, th: TypeHierarchy): Option[Position] = {
    exp match {
      // Invoke / InvokeNamed: the defining info is on `ident`
      case invoke: AST.Exp.Invoke =>
        return defPosFromResOpt(invoke.ident.attr.resOpt, invoke.ident.attr.typedOpt, th)
      case invoke: AST.Exp.InvokeNamed =>
        return defPosFromResOpt(invoke.ident.attr.resOpt, invoke.ident.attr.typedOpt, th)
      // Binary / Unary: BuiltIn resolved against receiver type
      case binary: AST.Exp.Binary =>
        return defPosFromResOpt(binary.attr.resOpt, binary.left.typedOpt, th)
      case unary: AST.Exp.Unary =>
        return defPosFromResOpt(unary.attr.resOpt, unary.exp.typedOpt, th)
      // Select carries its own resOpt
      case sel: AST.Exp.Select =>
        return defPosFromResOpt(sel.attr.resOpt, sel.attr.typedOpt, th)
      // Ident carries its own resOpt
      case ident: AST.Exp.Ident =>
        return defPosFromResOpt(ident.attr.resOpt, ident.attr.typedOpt, th)
      // Eta reference
      case eta: AST.Exp.Eta =>
        return defPosFromRef(eta.ref, th)
      case _ =>
        return None()
    }
  }

  def defPosFromRef(ref: AST.Exp.Ref, th: TypeHierarchy): Option[Position] = {
    ref match {
      case ident: AST.Exp.Ident =>
        return defPosFromResOpt(ident.attr.resOpt, ident.attr.typedOpt, th)
      case sel: AST.Exp.Select =>
        return defPosFromResOpt(sel.attr.resOpt, sel.attr.typedOpt, th)
    }
  }

  // Core resolver: given resOpt + optional receiver type, produce a definition Position.
  def defPosFromResOpt(
    resOpt: Option[AST.ResolvedInfo],
    receiverTypedOpt: Option[AST.Typed],
    th: TypeHierarchy
  ): Option[Position] = {
    resOpt match {
      case Some(res) =>
        res match {
          case bi: AST.ResolvedInfo.BuiltIn =>
            if (bi.defPosOpt.nonEmpty) {
              return bi.defPosOpt
            }
            val receiverIdsOpt: Option[ISZ[String]] = receiverTypedOpt match {
              case Some(t: AST.Typed.Name) => Some(t.ids)
              case _ => None()
            }
            return th.builtInDefPosOpt(bi.kind, receiverIdsOpt)
          case _: AST.ResolvedInfo.Package =>
            return None()
          case ri: AST.ResolvedInfo.Enum =>
            return ri.defPosOpt
          case ri: AST.ResolvedInfo.EnumElement =>
            return ri.defPosOpt
          case ri: AST.ResolvedInfo.Object =>
            return ri.defPosOpt
          case ri: AST.ResolvedInfo.Var =>
            return ri.defPosOpt
          case ri: AST.ResolvedInfo.Method =>
            return ri.defPosOpt
          case ri: AST.ResolvedInfo.Methods =>
            if (ri.methods.isEmpty) {
              return None()
            }
            return ri.methods(0).defPosOpt
          case ri: AST.ResolvedInfo.Tuple =>
            return ri.defPosOpt
          case ri: AST.ResolvedInfo.LocalVar =>
            return ri.defPosOpt
          case ri: AST.ResolvedInfo.Fact =>
            return ri.defPosOpt
          case ri: AST.ResolvedInfo.Theorem =>
            return ri.defPosOpt
          case ri: AST.ResolvedInfo.Inv =>
            return ri.defPosOpt
        }
      case _ =>
        return None()
    }
  }

  // -----------------------------------------------------------------------
  // Definition extraction from a Type.Named node
  // -----------------------------------------------------------------------

  def defPosFromTypedNamed(tipe: AST.Type.Named, th: TypeHierarchy): Option[Position] = {
    tipe.attr.typedOpt match {
      case Some(t: AST.Typed.Name) =>
        th.typeMap.get(t.ids) match {
          case Some(info) => return info.posOpt
          case _ => return None()
        }
      case _ => return None()
    }
  }

  // -----------------------------------------------------------------------
  // MTransformer-based walker
  // -----------------------------------------------------------------------

  @record class DefWalker(
    val th: TypeHierarchy,
    val line: Z,
    val column: Z,
    var bestSpan: Z,
    var bestResult: Option[Position]
  ) extends AST.MTransformer {

    // Called on every Exp node.  We check containment using `fullPosOpt`
    // (which covers the entire expression, not just the operator token for
    // Binary/Unary).  When contained, we try to extract a definition position
    // and record this node if its span is tighter than the current best.
    override def preExp(o: AST.Exp): AST.MTransformer.PreResult[AST.Exp] = {
      val spanPos: Option[Position] = o.fullPosOpt
      spanPos match {
        case Some(pos) =>
          if (containsCursor(pos, line, column)) {
            val span = spanOf(pos)
            if (bestSpan < 0 || span <= bestSpan) {
              val defOpt = defPosFromExp(o, th)
              defOpt match {
                case Some(_) =>
                  bestSpan = span
                  bestResult = defOpt
                case _ =>
              }
            }
            // Continue into children — a child node may be a tighter match
            return AST.MTransformer.PreResult(T, MNone())
          } else {
            // Cursor not in this node; prune — children cannot contain the cursor
            // if the parent does not (positions are contiguous in well-formed AST)
            return AST.MTransformer.PreResult(F, MNone())
          }
        case _ =>
          // No position info: continue traversal
          return AST.MTransformer.PreResult(T, MNone())
      }
    }

    // Type references: check Type.Named nodes for their containment.
    override def preTypeNamed(o: AST.Type.Named): AST.MTransformer.PreResult[AST.Type] = {
      o.attr.posOpt match {
        case Some(pos) =>
          if (containsCursor(pos, line, column)) {
            val span = spanOf(pos)
            if (bestSpan < 0 || span <= bestSpan) {
              val defOpt = defPosFromTypedNamed(o, th)
              defOpt match {
                case Some(_) =>
                  bestSpan = span
                  bestResult = defOpt
                case _ =>
              }
            }
            // Continue into children (e.g. type arguments)
            return AST.MTransformer.PreResult(T, MNone())
          } else {
            return AST.MTransformer.PreResult(F, MNone())
          }
        case _ =>
          return AST.MTransformer.PreResult(T, MNone())
      }
    }

    // Prune Stmt subtrees that don't contain the cursor to avoid traversing
    // the entire program when the match is in a small region.
    override def preStmt(o: AST.Stmt): AST.MTransformer.PreResult[AST.Stmt] = {
      o.posOpt match {
        case Some(pos) =>
          if (containsCursor(pos, line, column)) {
            return AST.MTransformer.PreResult(T, MNone())
          } else {
            return AST.MTransformer.PreResult(F, MNone())
          }
        case _ =>
          return AST.MTransformer.PreResult(T, MNone())
      }
    }

    // Skip annotation subtrees entirely (they don't carry definition info).
    override def preAnnotation(o: AST.Annotation): AST.MTransformer.PreResult[AST.Annotation] = {
      return AST.MTransformer.PreResult(F, MNone())
    }

  }

}
