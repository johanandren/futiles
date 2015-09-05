package markatta.futiles

import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.Future
import scala.concurrent.Future.{failed, successful}
import scala.util.Failure
import scala.concurrent.duration._

class RetrySpec extends Spec {

  import Lifting._
  import Retry._

  describe("The retry utilities") {

    describe("Basic retry") {

      it("retries failed futures the maximum number of times") {
        val count = new AtomicInteger(0)

        val result = retry(5) {
          count.incrementAndGet()
          failed(new RuntimeException("bork"))
        }

        whenReady(liftTry(result)) { fail =>
          count.get() should be(5)
          fail shouldBe a[Failure[_]]
        }
      }

      it("returns the first success") {
        val count = new AtomicInteger(3)

        val result = retry(5) {
          if (count.decrementAndGet() > 1) successful("woo")
          else failed(new RuntimeException("bork"))
        }

        result.futureValue should be("woo")
      }
    }

    describe("The exponential back off retry") {

      it("retries failed futures the maximum number of times") {
        val count = new AtomicInteger(0)

        val result = retryWithBackOff(5, 5.millisecond) {
          count.incrementAndGet()
          failed(new RuntimeException("bork"))
        }

        whenReady(liftTry(result)) { fail =>
          count.get() should be(5)
          fail shouldBe a[Failure[_]]
        }
      }

      it("returns the first success but has backed off") {
        val count = new AtomicInteger(0)
        val before = System.currentTimeMillis
        val result = retryWithBackOff(5, 5.millisecond) {
          if (count.incrementAndGet() > 2) successful(System.currentTimeMillis)
          else failed(new RuntimeException("bork"))
        }

        val timeFromStart = result.futureValue - before
        timeFromStart should (be >= 5L and be < 125L)
      }

      it("produces a failed future if the future-creation block fails") {
        val invocations = new AtomicInteger(0)
        def fail(): Future[Int] = {
          invocations.incrementAndGet()
          throw new RuntimeException("Oh noes, failure, FAILURE!")
        }

        val result = retryWithBackOff(5, 5.milliseconds)(fail())
        whenReady(liftTry(result)) { fail =>
          fail shouldBe a[Failure[_]]
        }
        invocations.get() shouldBe (1)
      }
    }

  }



}
