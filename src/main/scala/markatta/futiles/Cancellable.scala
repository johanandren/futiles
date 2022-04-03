package markatta.futiles

import scala.concurrent.{ExecutionContext, ExecutionException, Future, Promise}
import scala.util.Try
import java.util.concurrent.{Callable, FutureTask}

class Cancellable[T](executionContext: ExecutionContext, block: => T) {
  private val promise = Promise[T]()

  /** @return
    *   The underlying [[scala.concurrent.Future]] which you can use
    */
  def future: Future[T] = promise.future

  private val jf: FutureTask[T] = new FutureTask[T](
    new Callable[T] {
      override def call(): T = block
    }
  ) {
    override def done(): Unit = promise.complete(
      Try(
        try
          get()
        catch {
          case e: ExecutionException if e.getCause != null =>
            // This is here to mirror the same behaviour that Scala's Future has, i.e. if you throw
            // an exception in a Scala Future then then Future.failed has that same exception. Java's
            // FutureTask however wraps this in an ExecutionException.
            throw e.getCause
        }
      )
    )
  }

  /** Attempts to cancel the underlying [[scala.concurrent.Future]]. Note that this is a best effort attempt
    */
  def cancel(): Unit = jf.cancel(true)

  executionContext.execute(jf)
}

object Cancellable {

  /** Allows you to put a computation inside of a [[scala.concurrent.Future]] which can later be cancelled
    * @param block
    *   The computation to run inside of the [[scala.concurrent.Future]]
    * @param executionContext
    *   The [[scala.concurrent.ExecutionContext]] to run the [[scala.concurrent.Future]] on
    * @return
    *   A [[markatta.futiles.Cancellable]] providing both the [[scala.concurrent.Future]] and a `cancel` method allowing
    *   you to terminate the [[scala.concurrent.Future]] at any time
    * @see
    *   Adapted from https://stackoverflow.com/a/39986418/1519631
    */
  def apply[T](block: => T)(implicit executionContext: ExecutionContext): Cancellable[T] =
    new Cancellable[T](executionContext, block)
}
