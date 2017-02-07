object par {

  import scala.util.Try

  def spawn(codeBlock: => Unit) = {
    val t = new Thread(new Runnable { def run { codeBlock } })
    t.start
    t
  }

  def spawnLoop(codeBlock: => Unit) = spawn { while (true) codeBlock }
  
  def spawnLoopRecover(codeBlock: => Unit)(onError: Throwable => Unit) = spawn {
    var continue = true
    while (continue) Try {codeBlock} recover { case e => 
      continue = false
      onError(e)
    }
  }
}

object io {
  import java.io.{DataInputStream, DataOutputStream}
  import java.io.{BufferedInputStream, BufferedOutputStream}
  import java.net.Socket  
  
  def streamsFromSocket(s: Socket): (DataInputStream, DataOutputStream) = (
    new DataInputStream(new BufferedInputStream(s.getInputStream)),
    new DataOutputStream(new BufferedOutputStream(s.getOutputStream)))

  def write(dos: DataOutputStream, msg: String) { dos.writeUTF(msg); dos.flush }

  case class Channel(sock: Socket, dis: DataInputStream, dos: DataOutputStream){
    def read: String = dis.readUTF
    def write(msg: String): Unit = io.write(dos, msg)
  }
  
  def write[A](fileName: String, obj: A): Unit = {
    val f = new java.io.File(fileName)
    val os = new java.io.ObjectOutputStream(new java.io.FileOutputStream(f))
    try { os.writeObject(obj) } finally os.close
  }
  
  def read[A](fileName: String): A = {
    val f = new java.io.File(fileName)
    val is = new java.io.ObjectInputStream(new java.io.FileInputStream(f))
    try { is.readObject.asInstanceOf[A] } finally is.close
  }
  
  def create(fileNames: String *): Unit = fileNames foreach { f =>
    val file = new java.io.File(f)
    file.getParentFile.mkdirs
    file.createNewFile
  }

  
}

object err {
  import scala.util.Try

  def terminate = sys.exit(0)

  def abort(e: Throwable) = { e.printStackTrace; sys.exit(1) }
  
  def alert(e: Throwable) = println(s"ERROR: $e")
    
  def abortAlert(e: Throwable) = { alert(e) ; abort(e) }
  
  def default[T]: T = {class X { var x: T = _}; (new X).x}
  
  def tryOrElse[T](block: => T)(onError: Throwable => T): T = 
    try { block } catch { case e: Throwable => onError(e)}
    
  def tryOrDo[T](block: => T)(onError: Throwable => Unit): T = 
    try { block } catch { case e: Throwable => onError(e); default[T]}
 
  def tryOrThrow[T](block: => T)(onError: Throwable => Unit): T = 
    try { block } catch { case e: Throwable => onError(e); throw new Error(e)}
    
  def tryOrAbort[T](block: => T): T = tryOrDo(block)(abort) 
  
  def tryOrAlert[T](block: => T): T = tryOrDo(block)(alert)
  
}

object terminal {
  val reader = new jline.console.ConsoleReader
  
  def readln(prompt: String): String = reader.readLine(prompt)
  def readln: String = readln("")
  
  def readpwd(prompt: String): String = reader.readLine(prompt,'*') 
  def readpwd: String = readpwd("Password: ") 
}
