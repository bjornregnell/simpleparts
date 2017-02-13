package simpleparts

import scala.util.Try

object AES { //https://en.wikipedia.org/wiki/Advanced_Encryption_Standard
  import javax.crypto.spec.SecretKeySpec
  import javax.crypto.{Cipher, SealedObject}

  private val (algorithm, keyLength) = ("AES", 128)

  private def keySpec(password: String): SecretKeySpec = {
    val key = SHA.sha.digest(Base64.encodeToBytes(password)).take(keyLength/8)
    new SecretKeySpec(key, algorithm)
  }

  private def makeEncrypter(password: String): Cipher= {
    val enc = Cipher.getInstance(algorithm)
    enc.init(Cipher.ENCRYPT_MODE, keySpec(password))
    enc
  }

  private def makeDecrypter(password: String): Cipher= {
    val enc = Cipher.getInstance(algorithm)
    enc.init(Cipher.DECRYPT_MODE, keySpec(password))
    enc
  }

  def encryptSerializable(obj: java.io.Serializable, password: String): SealedObject =
    new SealedObject(obj, makeEncrypter(password))

  def decryptSealedObject[T](sealedObject: SealedObject, password: String): Option[T] =
    Try{ sealedObject.getObject(makeDecrypter(password)).asInstanceOf[T] }.toOption

  def encryptObjectToString[T](obj: T, password: String): String = {
    val bytes = Bytes.fromObject(obj)
    val b64 = Base64.encodeToString(bytes)
    val sealedObject = encryptSerializable(b64, password)
    val bytesOfSealed = Bytes.fromObject(sealedObject)
    val encrypted   = Base64.encodeToString(bytesOfSealed)
    encrypted
  }

  def decryptObjectFromString[T](encrypted: String, password: String): Option[T] =
    Try {
      val bytesOfSealed = Base64.decodeToBytes(encrypted)
      val sealedObject  = Bytes.toObject[SealedObject](bytesOfSealed)
      val b64 = decryptSealedObject[String](sealedObject, password).get
      val bytes = Base64.decodeToBytes(b64)
      val obj = Bytes.toObject[T](bytes)
      obj
    }.toOption

  def encryptString(secret: String, password: String): String = {
    val sealedObject = encryptSerializable(secret, password)
    Base64.encodeToString(Bytes.fromObject(sealedObject))
  }

  def decryptString(encrypted: String, password: String): Option[String] = Try {
    val bytes = Base64.decodeToBytes(encrypted)
    val sealedObject = Bytes.toObject[SealedObject](bytes)
    decryptSealedObject[String](sealedObject, password).get
  }.toOption
}
