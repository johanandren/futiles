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

import java.util.concurrent.TimeoutException

import scala.concurrent.{Future, Promise}
import scala.concurrent.duration._
import Timeouts.Implicits._

class TimeoutsSpec extends Spec {

  describe("The timeout utils") {

    it("runs a future after a given timeout") {
      val result = Timeouts.timeout(2.millis)("success")
      result.futureValue should be ("success")
    }

    it("decorates a future with an error timeout") {
      val future = Promise[String]().future
      val result = future.withTimeoutError(2.millis)

      val ex = result.failed.futureValue

      ex.getClass shouldEqual classOf[TimeoutException]
      ex.getMessage shouldEqual "Timed out after 2 milliseconds"
    }

    it("completes a future rathern than fail it if it does not timeout") {
      Future.successful("success").withTimeoutError(10.seconds).futureValue should be ("success")
    }

    it("decorates a future with an default timeout") {
      val future = Promise[String]().future
      val result = future.withTimeoutDefault(2.millis, "banana")

      result.futureValue shouldEqual "banana"
    }

    it("completes a future rathern than default it if it does not timeout") {
      Future.successful("success").withTimeoutDefault(10.seconds, "default").futureValue should be ("success")
    }
  }
}
