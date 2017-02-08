package simpleparts

object Socket {
  import java.io.{DataInputStream, DataOutputStream}
  import java.io.{BufferedInputStream, BufferedOutputStream}
  import java.net.Socket

  def streamsFromSocket(s: Socket): (DataInputStream, DataOutputStream) = (
    new DataInputStream(new BufferedInputStream(s.getInputStream)),
    new DataOutputStream(new BufferedOutputStream(s.getOutputStream)))

  case class Channel(sock: Socket, dis: DataInputStream, dos: DataOutputStream){
    def read: String = dis.readUTF
    def write(msg: String): Unit = { dos.writeUTF(msg); dos.flush }
  }
}
