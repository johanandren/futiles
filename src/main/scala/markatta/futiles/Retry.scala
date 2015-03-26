package markatta.futiles

import java.util.concurrent.ThreadLocalRandom

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Future, ExecutionContext}
import scala.util.Random

object Retry {

  private val alwaysRetry: Throwable => Boolean = _ => true

  /**
   * Evaluate a block that creates a future up to a specific number of times, if the future fails, decide if to retry
   * using the shouldRetry predicate, if it returns true - retry else return the failed future.
   *
   * Default is to retry for all throwables.
   *
   * Any exception in the block creating the future will never be retried but always be returned as a failed future
   */
  def retry[A](times: Int, shouldRetry: Throwable => Boolean = alwaysRetry)(fBlock: => Future[A])(implicit ec: ExecutionContext): Future[A] =
    try {
      if (times <= 1) fBlock
      else
        fBlock.recoverWith {
          case x: Throwable if shouldRetry(x) => retry(times - 1, shouldRetry)(fBlock)
        }
    } catch {
      // failure to actually create the future
      case x: Throwable => Future.failed(x)
    }


  /**
   * Evaluate a block that creates a future up to a specific number of times, if the future fails, decide
   * about retrying using a predicate, if it should retry an exponential back off is applied so that the
   * retry waits longer and longer for every retry it makes. A jitter is also added so that the exact timing of
   * the retry isn't exactly the same for all calls with the same backOffUnit
   *
   * Any exception in the block creating the future will also be returned as a failed future
   * Default is to retry for all throwables.
   *
   * Based on this wikipedia article:
   * http://en.wikipedia.org/wiki/Truncated_binary_exponential_backoff
   */
  def retryWithBackOff[A](times: Int, backOffUnit: FiniteDuration, shouldRetry: Throwable => Boolean = alwaysRetry)(fBlock: => Future[A])(implicit ec: ExecutionContext): Future[A] =
    try {
      if (times <= 1) fBlock
      else retryWithBackOffLoop(times, 1, backOffUnit, shouldRetry)(fBlock)
    } catch {
      // failure to actually create the future
      case x: Throwable => Future.failed(x)
    }


  private def retryWithBackOffLoop[A](totalTimes: Int, timesTried: Int, backOffUnit: FiniteDuration, shouldRetry: Throwable => Boolean = alwaysRetry)(fBlock: => Future[A])(implicit ec: ExecutionContext): Future[A] =
    if (totalTimes <= timesTried) fBlock
    else fBlock.recoverWith {
      case ex: Throwable if shouldRetry(ex) =>
        val timesTriedNow = timesTried + 1
        val backOff = nextBackOff(timesTriedNow, backOffUnit)
        Timeouts.timeout(backOff)(Unit).flatMap(_ =>
          retryWithBackOffLoop(totalTimes, timesTriedNow, backOffUnit, shouldRetry)(fBlock)
        )
    }


  private def nextBackOff(tries: Int, backOffUnit: FiniteDuration): FiniteDuration = {
    val rng = new Random(ThreadLocalRandom.current())
    // jitter between 0.5 and 1.5
    val jitter = 0.5 + rng.nextFloat()
    val k = (math.max(1, 2 ^ tries - 1) * jitter).toInt
    backOffUnit * k
  }

}
