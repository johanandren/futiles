package markatta.futiles

import java.util.concurrent.atomic.AtomicBoolean
import scala.concurrent.CancellationException

class CancellableFutureSpec extends Spec {
  describe("The cancellable utility") {

    describe("without cancellation") {

      it("works as a normal Future") {
        val cancellable = CancellableFuture {
          ()
        }

        cancellable.futureValue shouldEqual ()
      }

      it("throws an Exception correctly") {
        val cancellable = CancellableFuture {
          throw new IllegalArgumentException
        }

        val exception = cancellable.failed.futureValue
        exception shouldBe an[IllegalArgumentException]
      }

    }

    describe("with cancellation") {

      it("prevents Future from completing") {
        val atomicBoolean = new AtomicBoolean(true)

        val cancellable = CancellableFuture {
          Thread.sleep(100)
          atomicBoolean.set(false)
        }

        Thread.sleep(50)
        cancellable.cancel()
        Thread.sleep(100)
        atomicBoolean.get() shouldEqual true
      }

      it("throws a CancellationException exception") {
        val cancellable = CancellableFuture {
          Thread.sleep(100)
        }
        cancellable.cancel()
        cancellable.failed.futureValue shouldBe an[CancellationException]
      }

    }

  }

}
