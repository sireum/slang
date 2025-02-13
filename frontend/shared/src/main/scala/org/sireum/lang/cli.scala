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
import org.sireum.cli.CliOpt._

object cli {

  val slangTemplate: Tool = Tool(
    name = "slangTemplate",
    command = "template",
    description = "Insert Slang template",
    header = "Slang Script Template Inserter",
    usage = "<option>* <slang-file>",
    usageDescOpt = None(),
    opts = ISZ(
      Opt(name = "feedback", longKey = "feedback", shortKey = None(),
        tpe = Type.Path(multiple = F, default = None()),
        description = "Feedback output directory"),
      Opt(name = "line", longKey = "line", shortKey = Some('l'),
        tpe = Type.Num(sep = None(), default = 0, min = Some(1), max = None()),
        description = "Line location"),
      Opt(name = "column", longKey = "column", shortKey = Some('c'),
        tpe = Type.Num(sep = None(), default = 0, min = Some(1), max = None()),
        description = "Column location"),
      Opt(name = "mode", longKey = "mode", shortKey = Some('m'),
        tpe = Type.Choice(name = "mode", sep = None(), elements = ISZ("step", "assume", "assert", "subproof",
          "subproofFresh", "forall", "exists", "forallRange", "existsRange", "forallElements", "existsElements",
          "forallIndices", "existsIndices")),
        description = "Refactoring mode")
    ),
    groups = ISZ()
  )

  val slangRefactor: Tool = Tool(
    name = "slangRefactor",
    command = "refactor",
    description = "Refactor script",
    header = "Slang Script Refactoring",
    usage = "<option>* <slang-file>",
    usageDescOpt = None(),
    opts = ISZ(
      Opt(name = "feedback", longKey = "feedback", shortKey = None(),
        tpe = Type.Path(multiple = F, default = None()),
        description = "Feedback output directory"),
      Opt(name = "mode", longKey = "mode", shortKey = Some('m'),
        tpe = Type.Choice(name = "mode", sep = None(), elements = ISZ("enumSymbol", "insertVal", "renumberProof", "reformatProof", "expandInduct")),
        description = "Refactoring mode")
    ),
    groups = ISZ()
  )

  val slangRun: Tool = Tool(
    name = "slangRun",
    command = "run",
    description = "Script runner",
    header = "Slang Script Runner",
    usage = "<option>* <slang-file> <arg>*",
    usageDescOpt = None(),
    opts = ISZ(
      Opt(name = "eval", longKey = "eval", shortKey = Some('e'),
        tpe = Type.Flag(F),
        description = "Use Slang evaluator (all other options are ignored)"),
      Opt(name = "input", longKey = "input", shortKey = Some('i'),
        tpe = Type.Path(multiple = F, default = None()),
        description = "Input file for stdin (default: <slang-file>.txt, if any)"),
      Opt(name = "output", longKey = "output", shortKey = Some('o'),
        tpe = Type.Path(multiple = F, default = None()), description = "Output file for stdin & stderr"),
      Opt(name = "transformed", longKey = "transformed", shortKey = Some('t'),
        tpe = Type.Flag(F), description = "Show Scala transformed tree"),
      Opt(name = "nativ", longKey = "native", shortKey = Some('n'),
        tpe = Type.Flag(F), description = "Generate native executable")
    ),
    groups = ISZ()
  )

  val strictAliasingOpt: Opt = Opt(
    name = "strictAliasing", longKey = "strict-aliasing", shortKey = None(),
    tpe = Type.Flag(F),
    description = "Enable strict aliasing check"
  )

  val verboseOpt: Opt = Opt(name = "verbose", longKey = "verbose", shortKey = None(),
    tpe = Type.Flag(F), description = "Enable verbose mode"
  )

  val slangTipe: Tool = Tool(
    name = "slangTipe",
    command = "tipe",
    description = "Type checker",
    header = "Slang Type Checker",
    usage = "<option>* [<slang-file>]",
    usageDescOpt = None(),
    opts = ISZ(
      Opt(name = "exclude", longKey = "exclude", shortKey = Some('x'),
        tpe = Type.Str(Some(','), None()),
        description = "Sourcepath exclusion as URI segment"),
      Opt(name = "force", longKey = "force", shortKey = Some('f'),
        tpe = Type.Str(Some(','), None()),
        description = "Fully qualified names of traits, classes, and objects to force full type checking on when type outlining is enabled"),
      Opt(name = "noRuntime", longKey = "no-runtime", shortKey = Some('r'),
        tpe = Type.Flag(F), description = "Do not use built-in runtime (use runtime in sourcepath)"),
      Opt(name = "outline", longKey = "outline", shortKey = Some('o'),
        tpe = Type.Flag(F), description = "Perform type outlining only for files in the sourcepath"),
      Opt(name = "parseableMessages", longKey = "parseable-messages", shortKey = None(),
        tpe = Type.Flag(F),
        description = "Print parseable file messages"),
      Opt(name = "sourcepath", longKey = "sourcepath", shortKey = Some('s'),
        tpe = Type.Path(T, None()),
        description = "Sourcepath of Slang .scala files"),
      strictAliasingOpt,
      verboseOpt
    ),
    groups = ISZ(OptGroup(name = "Persistence", opts = ISZ(
      Opt(name = "save", longKey = "save", shortKey = None(), tpe = Type.Path(F, None()),
        description = "Path to save type information to (outline should not be enabled)"),
      Opt(name = "load", longKey = "load", shortKey = None(), tpe = Type.Path(F, None()),
        description = "Path to load type information from"),
      Opt(name = "gzip", longKey = "no-gzip", shortKey = Some('z'), tpe = Type.Flag(T),
        description = "Disable gzip compression when saving and/or loading")
    )))
  )

  val group: Group = Group(
    name = "slang",
    description = "Slang tools",
    header = "The Sireum Language (Slang) Tools",
    unlisted = F,
    subs = ISZ(slangRefactor, slangRun, slangTemplate, slangTipe)
  )

}
