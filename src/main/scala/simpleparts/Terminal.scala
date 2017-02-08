object Terminal {
  val reader = new jline.console.ConsoleReader

  def readln(prompt: String): String = reader.readLine(prompt)
  def readln: String = readln("")

  def readpwd(prompt: String): String = reader.readLine(prompt,'*')
  def readpwd: String = readpwd("Password: ")
}
