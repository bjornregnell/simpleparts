package simpleparts

object Err {
  def terminate() = sys.exit(1)

  def abort(e: Throwable) = { e.printStackTrace; terminate }

  def alert(e: Throwable) = scala.Console.err.println(s"ERROR: $e")

  def abortAlert(e: Throwable) = { alert(e); abort(e) }

  def tryOrElse[T](block: => T)(onError: Throwable => T): T =
    try { block } catch { case e: Throwable => onError(e) }

  def tryOrDo[T](block: => T)(onError: Throwable => Unit): T = {
    def default[A]: A = { class X { var x: A = _ }; (new X).x }
    try { block } catch { case e: Throwable => onError(e); default[T] }
  }

  def tryOrThrow[T](block: => T)(onError: Throwable => Unit): T =
    try { block } catch { case e: Throwable => onError(e); throw new Error(e) }

  def tryOrAbort[T](block: => T): T = tryOrDo(block)(abort)

  def tryOrAlert[T](block: => T): T = tryOrDo(block)(alert)
}
