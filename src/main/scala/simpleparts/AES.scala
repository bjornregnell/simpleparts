package simpleparts

object AES {
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

  def decryptSealedObject[T](sealedObject: SealedObject, password: String): T =
    sealedObject.getObject(makeDecrypter(password)).asInstanceOf[T]

  def encryptObjectToString[T](obj: T, password: String): String = {
    val bytes = Bytes.fromObject(obj)
    val encodedString = Base64.encodeToString(bytes)
    val sealedObject = encryptSerializable(encodedString, password)
    Base64.encodeToString(Bytes.fromObject(sealedObject))
  }

  def decryptObjectFromString[T](encrypted: String, password: String): String = {
    val bytes = Base64.decodeToBytes(encrypted)
    val sealedObject = Bytes.toObject[SealedObject](bytes)
    val encodedString = decryptSealedObject[String](sealedObject, password)
    Bytes.toObject[String](Base64.decodeToBytes(encodedString))
  }

  def encryptString(secret: String, password: String): String = {
    val sealedObject = encryptSerializable(secret, password)
    Base64.encodeToString(Bytes.fromObject(sealedObject))
  }

  def decryptString(encrypted: String, password: String): String = {
    val bytes = Base64.decodeToBytes(encrypted)
    val sealedObject = Bytes.toObject[SealedObject](bytes)
    decryptSealedObject[String](sealedObject, password)
  }
}
