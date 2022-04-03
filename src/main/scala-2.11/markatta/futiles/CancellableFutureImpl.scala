package markatta.futiles

import java.util.concurrent.{Callable, FutureTask}
import scala.concurrent._
import scala.concurrent.duration.Duration
import scala.util.Try

private[futiles] final class CancellableFutureImpl[T](executionContext: ExecutionContext, block: => T)
    extends CancellableFuture[T] {
  private val promise = Promise[T]()

  def delegate: Future[T] = promise.future

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

  override def onComplete[U](f: Try[T] => U)(implicit executor: ExecutionContext): Unit =
    delegate.onComplete(f)

  override def isCompleted: Boolean = delegate.isCompleted

  override def value: Option[Try[T]] = delegate.value

  override def transform[S](s: T => S, f: Throwable => Throwable)(implicit
      executor: ExecutionContext
  ): Future[S] = delegate.transform(s, f)

  override def ready(atMost: Duration)(implicit permit: CanAwait): CancellableFutureImpl.this.type = {
    delegate.ready(atMost)
    this
  }

  override def result(atMost: Duration)(implicit permit: CanAwait): T = delegate.result(atMost)

  override def cancel(): Unit = jf.cancel(true)

  executionContext.execute(jf)
}
