// #Sireum
/*
 Copyright (c) 2017, Robby, Kansas State University
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

object MutatingTransformer {

  @datatype class PreResult[T](continue: B,
                               resultOpt: Option[T])

  @sig trait Pre {

    def transformProgram(program: TopUnit.Program): PreResult[TopUnit] = {
      return PreResult(T, None())
    }

    def transformSequent(sequent: TopUnit.SequentUnit): PreResult[TopUnit] = {
      return PreResult(T, None())
    }

    def transformTruthTable(truthTable: TopUnit.TruthTableUnit): PreResult[TopUnit] = {
      return PreResult(T, None())
    }

  }

  @sig trait Post {

    def transformProgram(program: TopUnit.Program): Option[TopUnit] = {
      return None()
    }

    def transformSequent(sequent: TopUnit.SequentUnit): Option[TopUnit] = {
      return None()
    }

    def transformTruthTable(truthTable: TopUnit.TruthTableUnit): Option[TopUnit] = {
      return None()
    }
  }

}

import MutatingTransformer._

@record class MutatingTransformer(pre: Pre, post: Post) {

  def transformTopUnit(topUnit: TopUnit): Option[TopUnit] = {
    val preR: PreResult[TopUnit] = topUnit match {
      case topUnit: TopUnit.Program => pre.transformProgram(topUnit)
      case topUnit: TopUnit.SequentUnit => pre.transformSequent(topUnit)
      case topUnit: TopUnit.TruthTableUnit => pre.transformTruthTable(topUnit)
    }
    val r: Option[TopUnit] = if (preR.continue) {
      transformTopUnitCont(preR.resultOpt.getOrElse(topUnit), preR.resultOpt.nonEmpty)
    } else if (preR.resultOpt.nonEmpty) {
      Some(preR.resultOpt.getOrElse(topUnit))
    } else {
      None()
    }
    val hasChanged = r.nonEmpty
    val temp = r.getOrElse(topUnit)
    val postR: Option[TopUnit] = temp match {
      case program: TopUnit.Program => post.transformProgram(program)
      case sequent: TopUnit.SequentUnit => post.transformSequent(sequent)
      case truthTable: TopUnit.TruthTableUnit => post.transformTruthTable(truthTable)
    }
    if (postR.nonEmpty) {
      return postR
    } else if (hasChanged) {
      return Some(temp)
    } else {
      return None()
    }
  }

  def transformTopUnitCont(topUnit: TopUnit, hasChanged: B): Option[TopUnit] = {
    topUnit match {
      case topUnit@TopUnit.Program(_, packageName, body) =>
        val r1 = transformName(packageName)
        val r2 = transformBody(body)
        if (hasChanged | r1.nonEmpty | r2.nonEmpty) {
          return Some(topUnit(packageName = r1.getOrElse(packageName), body = r2.getOrElse(body)))
        } else {
          return None()
        }
      case topUnit@TopUnit.SequentUnit(_, sequent) =>
        val r1 = transformSequent(sequent)
        if (hasChanged | r1.nonEmpty) {
          return Some(topUnit(sequent = r1.getOrElse(sequent)))
        } else {
          return None()
        }
      case topUnit@TopUnit.TruthTableUnit(_, _, vars, _, _, sequent, rows, conclusionOpt) =>
        val r1 = transformISZ(vars, transformId _)
        val r2 = transformSequent(sequent)
        val r3 = transformISZ(rows, transformRow _)
        val r4 = transformOption(conclusionOpt, transformConclusion _)
        if (hasChanged | r1.nonEmpty | r2.nonEmpty | r3.nonEmpty | r4.nonEmpty) {
          return Some(topUnit(vars = r1.getOrElse(vars), sequent = r2.getOrElse(sequent), rows = r3.getOrElse(rows), conclusionOpt = r4.getOrElse(conclusionOpt)))
        } else {
          return None()
        }
    }
  }

  def transformISZ[T](is: IS[Z, T], f: T => Option[T]): Option[IS[Z, T]] = {
    var is2 = ISZ[T]()
    var changed = F
    for (e <- is) {
      val r = f(e)
      changed = changed | r.nonEmpty
      is2 = is2 :+ r.getOrElse(e)
    }
    if (changed) {
      return Some(is2)
    } else {
      return None()
    }
  }

  def transformOption[T](option: Option[T], f: T => Option[T]): Option[Option[T]] = {
    option match {
      case Some(v) =>
        val r = f(v)
        r match {
          case Some(_) => return Some(r)
          case _ => return None()
        }
      case _ => return None()
    }
  }

  def transformName(name: Name): Option[Name] = {
    ???
  }

  def transformBody(body: Body): Option[Body] = {
    ???
  }

  def transformSequent(sequent: LClause.Sequent): Option[LClause.Sequent] = {
    ???
  }

  def transformId(id: Id): Option[Id] = {
    ???
  }

  def transformRow(row: TruthTable.Row): Option[TruthTable.Row] = {
    ???
  }

  def transformConclusion(conclusion: TruthTable.Conclusion): Option[TruthTable.Conclusion] = {
    ???
  }
}