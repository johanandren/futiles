package markatta.futiles

import java.util.{TimerTask, Timer}

import scala.concurrent.{Future, Promise, ExecutionContext}
import scala.concurrent.duration.FiniteDuration
import scala.util.Try

object Timeouts {

  private val timer = new Timer()

  /**
   * When ```waitFor``` has passed, evaluate ```what``` on the given execution context and complete the future
   */
  def timeout[A](waitFor: FiniteDuration)(what: => A)(implicit ec: ExecutionContext): Future[A] = {
    val promise = Promise[A]()
    timer.schedule(new TimerTask {
      override def run(): Unit = {
        // make sure we do not block the timer thread
        Future {
          promise.complete(Try{ what })
        }
      }
    }, waitFor.toMillis)

    promise.future
  }



}
