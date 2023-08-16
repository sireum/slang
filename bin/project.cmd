::/*#! 2> /dev/null                                 #
@ 2>/dev/null # 2>nul & echo off & goto BOF         #
if [ -z ${SIREUM_HOME} ]; then                      #
  echo "Please set SIREUM_HOME env var"             #
  exit -1                                           #
fi                                                  #
exec ${SIREUM_HOME}/bin/sireum slang run "$0" "$@"  #
:BOF
setlocal
if not defined SIREUM_HOME (
  echo Please set SIREUM_HOME env var
  exit /B -1
)
%SIREUM_HOME%\bin\sireum.bat slang run "%0" %*
exit /B %errorlevel%
::!#*/
// #Sireum

import org.sireum._
import org.sireum.project.ProjectUtil._
import org.sireum.project.Project

val library = "library"

val slang = "slang"
val ast = "ast"
val parser = "parser"
val tipe = "tipe"
val frontend = "frontend"

val homeDir = Os.slashDir.up.canon

val astShared = moduleSharedPub(
  id = s"$slang-$ast",
  baseDir = homeDir / ast,
  sharedDeps = sharedId(library),
  sharedIvyDeps = ISZ(),
  pubOpt = pub(
    desc = "Slang Abstract Syntax Trees (AST)",
    url = "github.com/sireum/slang",
    licenses = bsd2,
    devs = ISZ(robby)
  )
)

val (parserShared, parserJvm) = moduleSharedJvmPub(
  baseId = s"$slang-$parser",
  baseDir = homeDir / parser,
  sharedDeps = ISZ(astShared.id),
  sharedIvyDeps = ISZ("org.scalameta::scalameta::"),
  jvmDeps = ISZ(library),
  jvmIvyDeps = ISZ(),
  pubOpt = pub(
    desc = "Slang Parser",
    url = "github.com/sireum/slang",
    licenses = bsd2,
    devs = ISZ(robby)
  )
)

val tipeShared = moduleSharedPub(
  id = s"$slang-$tipe",
  baseDir = homeDir / tipe,
  sharedDeps = ISZ(astShared.id),
  sharedIvyDeps = ISZ(),
  pubOpt = pub(
    desc = "Slang Parser",
    url = "github.com/sireum/slang",
    licenses = bsd2,
    devs = ISZ(robby)
  )
)

val frontendShared = moduleSharedPub(
  id = s"$slang-$frontend",
  baseDir = homeDir / frontend,
  sharedDeps = ISZ(parserShared.id, tipeShared.id),
  sharedIvyDeps = ISZ(),
  pubOpt = pub(
    desc = "Slang Frontend",
    url = "github.com/sireum/slang",
    licenses = bsd2,
    devs = ISZ(robby)
  )
)

val project = Project.empty + astShared + parserShared + tipeShared + frontendShared + parserJvm

projectCli(Os.cliArgs, project)