::#! 2> /dev/null                                              #
@ 2>/dev/null # 2>nul & echo off & goto BOF                    #
exec $(cd `dirname $0` && pwd)/sireum slang run -s "$0" "$@"   #
:BOF
%~dp0sireum.bat slang run -s "%0" %*
exit /B %errorlevel%
::!#
// #Sireum
import org.sireum._

def usage(): Unit = {
  println("Sireum Language (Slang) /build")
  println("Usage: ( compile | test | test-js | m2 | jitpack )+")
}

if (Os.cliArgs.size < 2) {
  usage()
  Os.exit(0)
}

val homeBin = Os.path(Os.cliArgs(0))
val home = homeBin.up
val sireumJar = homeBin / "sireum.jar"
val mill = homeBin / "mill.bat"

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

def jitpack(): Unit = {
  println("Triggering jitpack ...")
  val r = Os.proc(ISZ(mill.string, "jitPack", "--owner", "sireum", "--repo", "slang", "--lib", "frontend")).
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

def compile(): Unit = {

  def tipe(): Unit = {
    println("Slang type checking ...")
    Os.proc(ISZ("java", "-jar", sireumJar.string, "slang", "tipe", "--verbose", "-r", "-s", home.string)).
      at(home).console.runCheck()
    println()
  }

  if (Os.isMac) {
    jitpack()
  }
  tipe()
  println("Compiling ...")
  Os.proc(ISZ(mill.string, "all", "slang.frontend.shared.tests.compile",
    "slang.frontend.js.tests.compile")).at(home).console.runCheck()
  println()
}

def test(): Unit = {
  compile()
  println("Running shared tests ...")
  Os.proc(ISZ(mill.string, "all", "slang.parser.shared.tests", "slang.frontend.shared.tests")).at(home).console.runCheck()
  println()
}

def testJs(): Unit = {
  compile()
  println("Running js tests ...")
  Os.proc(ISZ(mill.string, "all", "slang.parser.js.tests", "slang.frontend.js.tests")).at(home).console.runCheck()
  println()
}

def m2(): Unit = {
  val m2s: ISZ[ISZ[String]] =
    for (pkg <- ISZ("ast", "parser", "tipe", "frontend"); plat <- ISZ("shared", "js"))
      yield ISZ("slang", pkg, plat, "m2")

  val m2Paths: ISZ[Os.Path] =
    for (cd <- for (m2 <- m2s) yield st"${(m2, Os.fileSep)}".render) yield  Os.cwd.up / "out" / cd

  for (m2p <- m2Paths) {
    m2p.removeAll()
  }


  Os.proc(ISZ[String](mill.string, "all") ++ (for (m2 <- m2s) yield st"${(m2, ".")}".render)).
    at(home).env(ISZ("SIREUM_SOURCE_BUILD" ~> "false")).console.runCheck()

  val repository = Os.home / ".m2" / "repository"

  println()
  println("Artifacts")
  for (m2p <- m2Paths; p <- (m2p / "dest").overlayMove(repository, F, F, _ => T, T).keys) {
    println(s"* $p")
  }
  println()
}


downloadMill()

clone("runtime")

for (i <- 1 until Os.cliArgs.size) {
  Os.cliArgs(i) match {
    case string"compile" => compile()
    case string"test" => test()
    case string"test-js" => test()
    case string"m2" => m2()
    case string"jitpack" => jitpack()
    case cmd =>
      usage()
      eprintln(s"Unrecognized command: $cmd")
      Os.exit(-1)
  }
}