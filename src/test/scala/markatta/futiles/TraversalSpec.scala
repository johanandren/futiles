package markatta.futiles

import java.util.concurrent.CountDownLatch

import markatta.futiles.Sequencing._

import scala.concurrent.Future
import scala.util.{Failure, Success}

class TraversalSpec extends Spec {

  import Traversal._

  describe("the traversal utitlities") {

    it("executes the futures sequentially") {
      val latch = new CountDownLatch(3)
      val result = traverseSequentially(List(1,2,3)) { n =>
        Future {
          latch.countDown()
          val l = latch.getCount.toInt
          (n, l)
        }
      }

      result.futureValue should be (List((1,2), (2,1), (3,0)))
    }

    it("fails if one of the futures fails") {
      val latch = new CountDownLatch(2)
      val result = traverseSequentially(List(1,2,3)) { n =>
        Future {
          latch.countDown()
          val l = latch.getCount.toInt
          if (l == 0) throw new RuntimeException("fail")
          (n, l)
        }
      }

      Lifting.liftTry(result).futureValue.isFailure should be (true)
    }

  }
}
