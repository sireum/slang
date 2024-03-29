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
%SIREUM_HOME%\bin\sireum.bat slang run -n "%0" %*
exit /B %errorlevel%
::!#*/
// #Sireum

import org.sireum._
import org.sireum.project.ProjectUtil._
import org.sireum.project.JSON


def usage(): Unit = {
  println("Usage: [ json ]")
}

var isDot = T

Os.cliArgs match {
  case ISZ(string"json") => isDot = F
  case ISZ(string"-h") =>
    usage()
    Os.exit(0)
  case ISZ() =>
  case _ =>
    usage()
    Os.exit(-1)
}

val homeDir = Os.slashDir.up.canon

val projects = ISZ(
  homeDir,
  homeDir / "runtime",
)

loadFromBaseDirs(projects) match {
  case Some(project) =>
    if (isDot) {
      val projectDot = homeDir / "project.dot"
      projectDot.writeOver(toDot(project))
      println(s"Wrote $projectDot")
    } else {
      println(JSON.fromProject(project, T))
    }
  case _ =>
    Os.exit(-1)
}