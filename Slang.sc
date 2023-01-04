/*
 Copyright (c) 2017-2023, Robby, Kansas State University
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

import mill._
import mill.scalalib._
import org.sireum.mill.SireumModule._

trait Module extends CrossJvmJsJitPack {

  final override def subUrl: String = "slang"

  final override def developers = Seq(Developers.robby)

  final override def jvmDeps = Seq()

  final override def jsDeps = Seq()

  final override def scalacPluginIvyDeps = Agg(
    ivy"org.sireum::scalac-plugin:$scalacPluginVersion"
  )

  final override def testDeps =
    if (isSourceDep) Seq(testObject.shared) else Seq()

  final override def testIvyDeps =
    if (isSourceDep) Agg.empty
    else Agg(jpLatest(isCross = true, "sireum", "runtime", "test"))

  final override def jvmTestIvyDeps = Agg.empty

  final override def jsTestIvyDeps = Agg.empty

  final override def testScalacPluginIvyDeps = scalacPluginIvyDeps

  final override def jvmTestFrameworks = Seq("org.scalatest.tools.Framework")

  final override def jsTestFrameworks = jvmTestFrameworks

  def testObject: CrossJvmJsPublish
}

object Module {

  trait Ast extends Module {

    final override def description: String = "Slang AST"

    final override def artifactName = s"$subUrl-ast"

    final override def ivyDeps =
      if (isSourceDep) Agg.empty
      else Agg(jpLatest(isCross = true, "sireum", "runtime", "library"))

    final override def deps =
      if (isSourceDep) Seq(libraryObject) else Seq()

    def libraryObject: CrossJvmJsPublish
  }

  trait Parser extends Module {

    final override def description: String = "Slang Parser"

    final override def artifactName = s"$subUrl-parser"

    final override def ivyDeps = Agg(
      ivy"org.scalameta::scalameta::$scalaMetaVersion"
        .excludeOrg("com.google.protobuf", "com.thesamet.scalapb")
    )

    final override def deps = Seq(astObject)

    def astObject: Ast

  }

  trait Tipe extends Module {

    final override def description: String = "Slang Type Checker"

    final override def artifactName = s"$subUrl-tipe"

    final override def ivyDeps = Agg.empty

    final override def deps = Seq(astObject)

    def astObject: Ast

  }

  trait FrontEnd extends Module {

    final override def description: String = "Slang Front-End"

    final override def artifactName = s"$subUrl-frontend"

    final override def ivyDeps = Agg.empty

    final override def deps = Seq(parserObject, tipeObject)

    def parserObject: Parser

    def tipeObject: Tipe

  }

}

