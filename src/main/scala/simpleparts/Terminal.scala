package simpleparts

object Terminal {
  val console = new jline.console.ConsoleReader

  final val CtrlD = "\u0004"  // End Of Transmission

  private def replaceNull(s: String): String =
    if (s == null) CtrlD else s

  def readln(prompt: String): String =
    replaceNull(console.readLine(prompt))

  def readln: String = readln("")

  def readpwd(prompt: String): String = {
    val result = replaceNull(console.readLine(prompt,'*'))
    console.resetPromptLine("", "", 0)
    result
  }

  def readpwd: String = readpwd("Enter password: ")

  def isOk(msg: String = ""): Boolean = readln(s"$msg (Y/n): ") == "Y"

  def put(s: String): Unit = {
    console.print(s"\n$s\n")
    console.redrawLine()
    console.flush()
  }

  def addCompletions(strings: String*): Unit = {
    val sc = new jline.console.completer.StringsCompleter(strings: _*)
    console.addCompleter(sc)
  }
}
