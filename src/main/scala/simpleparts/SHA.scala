package simpleparts

object SHA {
  val algorithm = "SHA-512"
  val sha = java.security.MessageDigest.getInstance(algorithm)

  def hash(s: String): String =
    Base64.encodeToString(sha.digest(Base64.encodeToBytes(s)))

  def isValidPassword(password: String, salt: String, saltedHash: String) =
    hash(password + salt) == saltedHash
}
