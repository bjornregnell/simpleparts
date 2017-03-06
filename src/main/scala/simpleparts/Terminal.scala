package simpleparts

object Terminal {
  val reader = new jline.console.ConsoleReader
  //val completionHdlr = new jline.console.completer.CandidateListCompletionHandler
  //completionHdlr.setPrintSpaceAfterFullCompletion(false)
  //reader.setCompletionHandler(completionHdlr)

  final val CtrlD = "\u0004"  // End Of Transmission

  private def replaceNull(s: String): String = if (s == null) CtrlD else s

  def get(prompt: String = "", default: String = ""): String =
    replaceNull(reader.readLine(prompt, null, default))

  def getSecret(prompt: String = "Enter secret: "): String =
    replaceNull(reader.readLine(prompt,'*'))

  def isOk(msg: String = ""): Boolean = get(s"$msg (Y/n): ") == "Y"

  def put(s: String): Unit = reader.println(s)

  def removeCompletions(): Unit = {
    reader.getCompleters.toArray.foreach { c =>
      reader.removeCompleter(c.asInstanceOf[jline.console.completer.Completer])
    }
  }

  def setCompletions(first: Seq[String], second: Seq[String]): Unit = {
    removeCompletions()
    val sc1 = new jline.console.completer.StringsCompleter(first: _*)
    val sc2 = new jline.console.completer.StringsCompleter(second: _*)
    val ac = new jline.console.completer.ArgumentCompleter(sc1, sc2)
    reader.addCompleter(ac)
  }

  def toClipboard(s: String): Unit = {
    val stringSelection = new java.awt.datatransfer.StringSelection(s)
    val clipboard = java.awt.Toolkit.getDefaultToolkit.getSystemClipboard
    clipboard.setContents(stringSelection, null);
  }
}
