package simpleparts

// https://en.wikipedia.org/wiki/Salt_(cryptography)

object Salt {
  val init: String = "wUliyZmCxzu1Ecmw7/BhC4Sfw7hr5V4+/0HwXWx08go="
  val rnd = new java.security.SecureRandom
  val saltLength = 32
  def next: String = {
    var xs = new Array[Byte](saltLength)
    rnd.nextBytes(xs)
    Base64.encodeToString(xs)
  }
}
