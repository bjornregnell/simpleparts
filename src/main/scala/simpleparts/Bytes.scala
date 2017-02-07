package simpleparts

object Bytes {
  def toObject[T](bytes: Array[Byte]): T = {
    val bis = new java.io.ByteArrayInputStream(bytes)
    var ois = new java.io.ObjectInputStream(bis)
    try ois.readObject.asInstanceOf[T] finally ois.close
  }

  def fromObject[T](obj: T): Array[Byte] = {
    val bos = new java.io.ByteArrayOutputStream
    val oos = new java.io.ObjectOutputStream(bos)
    try {
      oos.writeObject(obj)
      bos.toByteArray
    } finally oos.close
  }
}
