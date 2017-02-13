package simpleparts

object Terminal {
  val reader = new jline.console.ConsoleReader

  final val CtrlD = "\u0004"  // End Of Transmission

  private def replaceNull(s: String): String = if (s == null) CtrlD else s

  def get(prompt: String = ""): String = replaceNull(reader.readLine(prompt))

  def getSecret(prompt: String = "Enter secret: "): String =
    replaceNull(reader.readLine(prompt,'*'))

  def isOk(msg: String = ""): Boolean = get(s"$msg (Y/n): ") == "Y"

  def put(s: String): Unit = reader.println(s)

  def addCompletions(strings: String*): Unit = {
    val sc = new jline.console.completer.StringsCompleter(strings: _*)
    reader.addCompleter(sc)
  }
}
