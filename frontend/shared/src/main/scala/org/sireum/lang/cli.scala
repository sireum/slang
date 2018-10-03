// #Sireum
/*
 Copyright (c) 2018, Robby, Kansas State University
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
  val slangTipe: Tool = Tool(
    name = "slangTipe",
    command = "tipe",
    description = "Slang type checker",
    header = "Slang Type Checker",
    usage = "<option>* [<slang-file>]",
    opts = ISZ(
      Opt(name = "sourcepath", longKey = "sourcepath", shortKey = Some('s'),
        tpe = Type.Path(T, None()),
        description = "Sourcepath of Slang .scala files"),
      Opt(name = "outline", longKey = "outline", shortKey = Some('o'),
        tpe = Type.Flag(F), description = "Perform type outlining only for files in the sourcepath"),
      Opt(name = "force", longKey = "force", shortKey = Some('f'),
        tpe = Type.Str(Some(','), None()),
        description = "Fully qualified names of traits, classes, and objects to force full type checking on when type outlining is enabled"),
      Opt(name = "verbose", longKey = "verbose", shortKey = None(),
        tpe = Type.Flag(F), description = "Enable verbose mode"),
      Opt(name = "noRuntime", longKey = "no-runtime", shortKey = Some('r'),
        tpe = Type.Flag(F), description = "Do not use built-in runtime (use runtime in sourcepath)")
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
    description = "Slang toolbox",
    header = "The Sireum Language (Slang) Toolbox",
    unlisted = F,
    subs = ISZ(slangTipe)
  )

}
