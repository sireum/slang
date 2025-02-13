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
import org.sireum.lang.symbol._
import org.sireum.lang.tipe._
import org.sireum.test._

class SlangCodebaseTest extends TestSuite {

  val tests = Tests {
    * - {
      val (initNameMap, initTypeMap) = Resolver.addBuiltIns(HashSMap.empty, HashSMap.empty)
      val (reporter, _, globalNameMap, globalTypeMap) = FrontEnd.parseProgramAndGloballyResolve(
        0,
        for (p <- org.sireum.Library.files) yield FrontEnd.Input(p._2, p._1),
        initNameMap,
        initTypeMap
      )

      def report(): Boolean = {
        if (reporter.hasIssue) {
          reporter.printMessages()
          assert(F)
          return false
        }
        true
      }

      report()
      var th = TypeHierarchy.build(F, TypeHierarchy(globalNameMap, globalTypeMap, Poset.empty, HashSMap.empty), reporter)
      report()
      th = TypeOutliner.checkOutline(0, T, th, reporter)
      report()

      def nameInfo(th: TypeHierarchy, name: Predef.String): Resolver.NameMap = {
        val ids = ISZ(name.split('.').toIndexedSeq.map(s => s: String): _*)
        return HashSMap ++ ISZ((ids, th.nameMap.get(ids).get))
      }

      def typeInfo(th: TypeHierarchy, name: Predef.String): Resolver.TypeMap = {
        val ids = ISZ(name.split('.').toIndexedSeq.map(s => s: String): _*)
        th.typeMap.get(ids) match {
          case Some(info) => return HashSMap ++ ISZ((ids, info))
          case _ => return HashSMap.empty
        }
      }

      val all = T
      val name = ""
      val nameMap: Resolver.NameMap = if (all) th.nameMap else nameInfo(th, name)
      val typeMap: Resolver.TypeMap = if (all) th.typeMap else typeInfo(th, name)
      th = TypeChecker.checkComponents(0, T, th, nameMap, typeMap, reporter)
      if (!reporter.hasError) {
        if (all) {
          PostTipeAttrChecker.checkNameTypeMaps(th.nameMap, th.typeMap, reporter)
        } else {
          PostTipeAttrChecker.checkNameTypeMaps(nameInfo(th, name), typeInfo(th, name), reporter)
        }
      }
      report()
      val bin = CustomMessagePack.fromTypeHierarchy(th)
      val Either.Left(th2) = CustomMessagePack.toTypeHierarchy(bin)
      assert(th == th2)
    }
  }
}
