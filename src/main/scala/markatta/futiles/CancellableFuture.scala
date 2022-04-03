package markatta.futiles

import scala.concurrent._
import scala.util.Try
import java.util.concurrent.{Callable, FutureTask}
import scala.concurrent.duration.Duration

trait CancellableFuture[T] extends Future[T] {

  /** Attempts to cancel the underlying [[scala.concurrent.Future]]. Note that this is a best effort attempt
    */
  def cancel(): Unit
}

object CancellableFuture {

  /** Allows you to run a computation inside of a [[scala.concurrent.Future]] which can later be cancelled
    *
    * @param block
    *   The computation to run inside of the [[scala.concurrent.Future]]
    * @param executionContext
    *   The [[scala.concurrent.ExecutionContext]] to run the [[scala.concurrent.Future]] on
    * @return
    *   A [[markatta.futiles.CancellableFuture]] providing a `cancel` method allowing you to terminate the
    *   [[markatta.futiles.CancellableFuture]] at any time
    * @see
    *   Adapted from https://stackoverflow.com/a/39986418/1519631
    */
  def apply[T](block: => T)(implicit executionContext: ExecutionContext): CancellableFuture[T] =
    new CancellableFutureImpl[T](executionContext, block)
}
