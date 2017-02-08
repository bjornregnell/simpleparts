package simpleparts

object Par {

  import scala.util.Try

  def spawn(codeBlock: => Unit) = {
    val t = new Thread(new Runnable { def run = codeBlock })
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
