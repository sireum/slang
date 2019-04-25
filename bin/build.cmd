::#! 2> /dev/null                                                                                           #
@ 2>/dev/null # 2>nul & echo off & goto BOF                                                                 #
export SIREUM_HOME=$(cd -P $(dirname "$0")/.. && pwd -P)                                                    #
if [ ! -z ${SIREUM_PROVIDED_SCALA++} ]; then                                                                #
  SIREUM_PROVIDED_JAVA=true                                                                                 #
fi                                                                                                          #
if [ ! -f "${SIREUM_HOME}/bin/sireum.jar" ]; then                                                           #
  "${SIREUM_HOME}/bin/init.sh"                                                                              #
elif [ "${SIREUM_HOME}/versions.properties" -nt "${SIREUM_HOME}/bin/sireum.jar" ]; then                     #
  "${SIREUM_HOME}/bin/init.sh"                                                                              #
fi                                                                                                          #
if [ -n "$COMSPEC" -a -x "$COMSPEC" ]; then                                                                 #
  PLATFORM="win"                                                                                            #
  export SIREUM_HOME=$(cygpath -C OEM -w -a ${SIREUM_HOME})                                                 #
  if [ -z ${SIREUM_PROVIDED_JAVA++} ]; then                                                                 #
    export JAVA_HOME="${SIREUM_HOME}\\bin\\win\\java"                                                       #
    export Z3_HOME="${SIREUM_HOME}\\bin\\win\\z3"                                                           #
    export PATH="${SIREUM_HOME}/bin/win/java":"${SIREUM_HOME}/bin/win/z3":$PATH                             #
    export PATH="$(cygpath -C OEM -w -a ${JAVA_HOME}/bin)":"$(cygpath -C OEM -w -a ${Z3_HOME}/bin)":$PATH   #
  fi                                                                                                        #
elif [ "$(uname)" = "Darwin" ]; then                                                                        #
  PLATFORM="mac"                                                                                            #
  if [ -z ${SIREUM_PROVIDED_JAVA++} ]; then                                                                 #
    export JAVA_HOME="${SIREUM_HOME}/bin/mac/java"                                                          #
    export Z3_HOME="${SIREUM_HOME}/bin/mac/z3"                                                              #
    export PATH="${JAVA_HOME}/bin":"${Z3_HOME}/bin":$PATH                                                   #
  fi                                                                                                        #
elif [ "$(expr substr $(uname -s) 1 5)" = "Linux" ]; then                                                   #
  PLATFORM="linux"                                                                                          #
  if [ -z ${SIREUM_PROVIDED_JAVA++} ]; then                                                                 #
    export JAVA_HOME="${SIREUM_HOME}/bin/linux/java"                                                        #
    export Z3_HOME="${SIREUM_HOME}/bin/linux/z3"                                                            #
    export PATH="${JAVA_HOME}/bin":"${Z3_HOME}/bin":$PATH                                                   #
  fi                                                                                                        #
fi                                                                                                          #
if [ -f "$0.com" ] && [ "$0.com" -nt "$0" ]; then                                                           #
  exec "$0.com" "$@"                                                                                        #
else                                                                                                        #
  rm -fR "$0.com"                                                                                           #
  exec "${SIREUM_HOME}/bin/sireum" slang run -s -n "$0" "$@"                                                #
fi                                                                                                          #
:BOF
if defined SIREUM_PROVIDED_SCALA set SIREUM_PROVIDED_JAVA=true
if not exist "%~dp0sireum.jar" call "%~dp0init.bat"
if not defined SIREUM_PROVIDED_JAVA set PATH=%~dp0win\java\bin;%~dp0win\z3\bin;%PATH%
"%~dp0sireum.bat" slang run -s "%0" %*
exit /B %errorlevel%
::!#
// #Sireum
import org.sireum._


def usage(): Unit = {
  println("Sireum Language (Slang) /build")
  println("Usage: ( compile | test | test-js | m2 | jitpack )+")
}


if (Os.cliArgs.isEmpty) {
  usage()
  Os.exit(0)
}


val homeBin = Os.slashDir
val home = homeBin.up
val sireumJar = homeBin / "sireum.jar"
val mill = homeBin / "mill.bat"
var didTipe = F
var didCompile = F
var didM2 = F


def downloadMill(): Unit = {
  if (!mill.exists) {
    println("Downloading mill ...")
    mill.downloadFrom("http://files.sireum.org/mill-standalone")
    mill.chmod("+x")
    println()
  }
}


def clone(repo: String): Unit = {
  if (!(home / repo).exists) {
    Os.proc(ISZ("git", "clone", "--depth=1", s"https://github.com/sireum/$repo")).at(home).console.runCheck()
  } else {
    Os.proc(ISZ("git", "pull", "--recurse-submodules")).at(home / repo).console.runCheck()
  }
  println()
}


def tipe(): Unit = {
  if (!didTipe) {
    didTipe = T
    println("Slang type checking ...")
    Os.proc(ISZ("java", "-jar", sireumJar.string, "slang", "tipe", "--verbose", "-r", "-s", home.string)).
      at(home).console.runCheck()
    println()
  }
}


def compile(): Unit = {
  if (!didCompile) {
    didCompile = T
    if (didM2) {
      didM2 = F
      (home / "out").removeAll()
    }
    tipe()
    println("Compiling ...")
    mill.call(ISZ("all", "slang.frontend.shared.tests.compile",
      "slang.frontend.js.tests.compile")).at(home).console.runCheck()
    println()
  }
}


def test(): Unit = {
  compile()
  println("Running shared tests ...")
  mill.call(ISZ("all", "slang.parser.shared.tests", "slang.frontend.shared.tests")).at(home).console.runCheck()
  println()
}


def testJs(): Unit = {
  compile()
  println("Running js tests ...")
  mill.call(ISZ("all", "slang.parser.js.tests", "slang.frontend.js.tests")).at(home).console.runCheck()
  println()
}


def jitpack(): Unit = {
  println("Triggering jitpack ...")
  val r = mill.call(ISZ("jitPack", "--owner", "sireum", "--repo", "slang", "--lib", "frontend")).
    at(home).console.run()
  r match {
    case r: Os.Proc.Result.Normal =>
      println(r.out)
      println(r.err)
      if (!r.ok) {
        eprintln(s"Exit code: ${r.exitCode}")
      }
    case r: Os.Proc.Result.Exception =>
      eprintln(s"Exception: ${r.err}")
    case _: Os.Proc.Result.Timeout =>
      eprintln("Timeout")
      eprintln()
  }
  println()
}


def m2(): Unit = {
  didM2 = T
  didCompile = F

  val m2s: ISZ[ISZ[String]] =
    for (pkg <- ISZ("ast", "parser", "tipe", "frontend"); plat <- ISZ("shared", "js"))
      yield ISZ("slang", pkg, plat, "m2")

  val m2Paths: ISZ[Os.Path] =
    for (cd <- for (m2 <- m2s) yield st"${(m2, Os.fileSep)}".render) yield  home / "out" / cd

  for (m2p <- m2Paths) {
    m2p.removeAll()
  }

  (home / "out").removeAll()

  Os.proc(ISZ[String](mill.string, "all") ++ (for (m2 <- m2s) yield st"${(m2, ".")}".render)).
    at(home).env(ISZ("SIREUM_SOURCE_BUILD" ~> "false")).console.runCheck()

  val repository = Os.home / ".m2" / "repository"
  repository.removeAll()

  println()
  println("Artifacts")
  for (m2p <- m2Paths; p <- (m2p / "dest").overlayMove(repository, F, F, _ => T, T).values) {
    println(s"* $p")
  }
  println()
}


downloadMill()

clone("runtime")

for (i <- 0 until Os.cliArgs.size) {
  Os.cliArgs(i) match {
    case string"compile" => compile()
    case string"test" => test()
    case string"test-js" => testJs()
    case string"m2" => m2()
    case string"jitpack" => jitpack()
    case cmd =>
      usage()
      eprintln(s"Unrecognized command: $cmd")
      Os.exit(-1)
  }
}