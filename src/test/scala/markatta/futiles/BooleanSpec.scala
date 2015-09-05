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

import scala.concurrent.Future

class BooleanSpec extends Spec {

  import Boolean.Implicits._

  describe("the future boolean operations") {

    it("gives the expected result from 'and' with two booleans") {
      val t = Future.successful(true)
      val f = Future.successful(false)

      (t && t).futureValue should be (true)
      (t && f).futureValue should be (false)
      (f && t).futureValue should be (false)
      (f && f).futureValue should be (false)
    }


    it("gives the expected result from 'or' with two booleans") {
      val t = Future.successful(true)
      val f = Future.successful(false)

      (t || t).futureValue should be (true)
      (t || f).futureValue should be (true)
      (f || t).futureValue should be (true)
      (f || f).futureValue should be (false)
    }

    it("can negate a boolean") {
      val t = Future.successful(true)
      val f = Future.successful(false)

      (!t).futureValue should be (false)
      (!f).futureValue should be (true)
    }

  }

}
