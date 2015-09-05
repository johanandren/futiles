/*
 * Copyright 2015 Johan Andrén
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package markatta.futiles

import java.util.concurrent.CountDownLatch

import scala.concurrent.Future

class TraversalSpec extends Spec {

  import Traversal._

  describe("the sequential traversal") {

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

  describe("the sequential foldLeft") {

    it("executes the futures sequentially") {
      val latch = new CountDownLatch(3)
      val result = foldLeftSequentially(List(1,2,3))(Seq.empty[Int]) { (acc, n) =>
        Future {
          latch.countDown()
          val l = latch.getCount.toInt
          acc :+ l
        }
      }

      result.futureValue should be (List(2, 1, 0))

    }

    it("executes fails if one of the futures fails") {
      val latch = new CountDownLatch(3)
      val result = foldLeftSequentially(List(1,2,3))(Seq.empty[Int]) { (acc, n) =>
        Future {
          latch.countDown()
          val l = latch.getCount.toInt
          if (l == 1) throw new RuntimeException("fail")
          acc :+ l
        }
      }

      Lifting.liftTry(result).futureValue.isFailure should be (true)

    }
  }
}
