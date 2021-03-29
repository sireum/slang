::#! 2> /dev/null                                             #
@ 2>/dev/null # 2>nul & echo off & goto BOF                   #
if [ -f "$0.com" ] && [ "$0.com" -nt "$0" ]; then             #
  exec "$0.com" "$@"                                          #
fi                                                            #
rm -f "$0.com"                                                #
if [ -z ${SIREUM_HOME} ]; then                                #
  echo "Please set SIREUM_HOME env var"                       #
  exit -1                                                     #
fi                                                            #
exec ${SIREUM_HOME}/bin/sireum slang run -n "$0" "$@"         #
:BOF
setlocal
if not defined SIREUM_HOME (
  echo Please set SIREUM_HOME env var
  exit /B -1
)
set NEWER=False
if exist %~dpnx0.com for /f %%i in ('powershell -noprofile -executionpolicy bypass -command "(Get-Item %~dpnx0.com).LastWriteTime -gt (Get-Item %~dpnx0).LastWriteTime"') do @set NEWER=%%i
if "%NEWER%" == "True" goto native
del "%~dpnx0.com" > nul 2>&1
%SIREUM_HOME%\bin\sireum.bat slang run -n "%0" %*
exit /B %errorlevel%
:native
%~dpnx0.com %*
exit /B %errorlevel%
::!#
// #Sireum

import org.sireum._
import org.sireum.project.ProjectUtil._
import org.sireum.project.{JSON, Project}

val library = "library"
val test = "test"

val slang = "slang"
val ast = "ast"
val parser = "parser"
val tipe = "tipe"
val frontend = "frontend"

val homeDir = Os.slashDir.up.canon

val astShared = moduleShared(
  id = s"$slang-$ast",
  baseDir = homeDir / ast,
  sharedDeps = sharedId(library),
  sharedIvyDeps = ISZ()
)

val parserShared = moduleShared(
  id = s"$slang-$parser",
  baseDir = homeDir / parser,
  sharedDeps = ISZ(astShared.id, test),
  sharedIvyDeps = ISZ("org.scalameta::scalameta::")
)

val tipeShared = moduleShared(
  id = s"$slang-$tipe",
  baseDir = homeDir / tipe,
  sharedDeps = ISZ(astShared.id),
  sharedIvyDeps = ISZ()
)

val frontendShared = moduleShared(
  id = s"$slang-$frontend",
  baseDir = homeDir / frontend,
  sharedDeps = ISZ(parserShared.id, tipeShared.id),
  sharedIvyDeps = ISZ()
)

val project = Project.empty + astShared + parserShared + tipeShared + frontendShared

projectCli(Os.cliArgs, project)