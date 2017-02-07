package simpleparts

// https://javadigest.wordpress.com/2012/08/26/rsa-encryption-example/
// http://stackoverflow.com/questions/9755057/converting-strings-to-encryption-keys-and-vice-versa-java

/*
  usage:
  import simpleparts.RSA
  val secret = "The Earth is Flat"
  val pk = RSA.PersistentKeys.loadOrCreate(fileName = "keys.ser")
  val (publ, priv) = (pk.publicKey, pk.privateKey)
  val encrypted = RSA.encryptString(secret, publ)
  val decrypted = RSA.decryptString(encrypted, priv)
*/

object RSA {
  import java.security.{PublicKey, PrivateKey, KeyPair}
  private val (algorithm, bitLength) = ("RSA", 2048)
  private val keyFactory = java.security.KeyFactory.getInstance(algorithm)

  private def generateNewKeyPair: KeyPair = {
    val keyGen = java.security.KeyPairGenerator.getInstance(algorithm)
    keyGen.initialize(bitLength)
    keyGen.generateKeyPair
  }

  private def privateKeyFromString(privateKeyString: String): PrivateKey = {
    val bytes = java.util.Base64.getDecoder.decode(privateKeyString)
    val spec = new java.security.spec.PKCS8EncodedKeySpec(bytes)
    keyFactory.generatePrivate(spec)
  }

  private def publicKeyFromString(publicKeyString: String): PublicKey = {
    val bytes = java.util.Base64.getDecoder.decode(publicKeyString)
    val spec = new java.security.spec.X509EncodedKeySpec(bytes)
    keyFactory.generatePublic(spec)
  }

  private def publicKeyStringFromKeyPair(kp: KeyPair): String = {
    val bytes: Array[Byte] = kp.getPublic.getEncoded
    Base64.encodeToString(bytes)
  }

  private def privateKeyStringFromKeyPair(kp: KeyPair): String = {
    val bytes: Array[Byte] = kp.getPrivate.getEncoded
    Base64.encodeToString(bytes)
  }


  trait Keys {
    def publicKey: String
    def privateKey: String
  }

  def generateKeys(): Keys = {
    val kp = generateNewKeyPair
    new Keys {
      override val publicKey = publicKeyStringFromKeyPair(kp)
      override val privateKey = privateKeyStringFromKeyPair(kp)
    }
  }

  object PersistentKeys {
    def loadOrCreate(fileName: String = "key.ser"): PersistentKeys = {
      val keyPair =
        if (Disk.isExisting(fileName))
          Disk.loadObject[KeyPair](fileName)
        else {
          val kp = generateNewKeyPair
          Disk.saveObject(kp, fileName)
          kp
        }
      new PersistentKeys(fileName, keyPair)
    }
  }

  class PersistentKeys private (
          val fileName: String,
          @volatile private var myKeyPair: KeyPair) extends Keys {

    def updateKey(): Unit = {
      myKeyPair = generateNewKeyPair
      Disk.saveObject(myKeyPair, fileName)
    }

    override def publicKey: String = publicKeyStringFromKeyPair(myKeyPair)
    override def privateKey: String = privateKeyStringFromKeyPair(myKeyPair)
  }

  def decryptObject[T](encrypted: String, privateKey: String): T = {
    val key = privateKeyFromString(privateKey)
    val encryptedBytes = java.util.Base64.getDecoder.decode(encrypted)
    val cipher = javax.crypto.Cipher.getInstance(algorithm);
    cipher.init(javax.crypto.Cipher.DECRYPT_MODE, key)
    val secretBytes = cipher.doFinal(encryptedBytes)
    Bytes.toObject[T](secretBytes)
  }

  def decryptString(encrypted: String, privateKey: String): String =
    decryptObject[String](encrypted, privateKey)

  def encryptObject[T](secret: T, publicKey: String): String = {
    val key = publicKeyFromString(publicKey)
    val secretBytes = Bytes.fromObject(secret)
    val cipher = javax.crypto.Cipher.getInstance(algorithm);
    cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key)
    val encryptedBytes = cipher.doFinal(secretBytes)
    java.util.Base64.getEncoder.encodeToString(encryptedBytes)
  }

  def encryptString(secret: String, publicKey: String): String =
    encryptObject[String](secret, publicKey)
}
