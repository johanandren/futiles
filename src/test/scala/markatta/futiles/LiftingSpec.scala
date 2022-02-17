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
import scala.concurrent.Future.{successful, failed}
import scala.util.{Try, Success, Failure}

class LiftingSpec extends Spec {

  import Lifting._

  describe("The lifting utilities for futures") {

    describe("the Try lifter") {

      it("lifts a failed future") {
        val exception                   = new RuntimeException("error")
        val result: Future[Try[String]] = liftTry(failed[String](exception))

        result.futureValue should be(Failure(exception))
      }

      it("lifts a successful future") {
        val result: Future[Try[String]] = liftTry(successful("Success"))
        result.futureValue should be(Success("Success"))
      }

    }

    describe("the Option unlifter") {

      it("unlifts None into an UnliftException") {
        val result = unliftOption(successful[Option[String]](None), "missing!")
        liftTry(result).futureValue should be(Failure(new UnliftException("missing!")))
      }

      it("unlifts a Some(value) into value") {
        val result = unliftOption(successful[Option[String]](Some("woho")), "missing")
        result.futureValue should be("woho")
      }

    }

    describe("the implicit option unlifter") {
      import Lifting.Implicits._
      it("unlifts None into an UnliftException") {
        val result = successful[Option[String]](None).unlift("missing!")
        liftTry(result).futureValue should be(Failure(new UnliftException("missing!")))
      }

      it("unlifts None into a custom exception") {
        val ex     = new RuntimeException("dang")
        val result = successful[Option[String]](None).unliftEx(ex)
        liftTry(result).futureValue should be(Failure(ex))
      }

      it("unlifts a Some(value) into value") {
        val result = successful[Option[String]](Some("woho")).unlift("missing!")
        result.futureValue should be("woho")
      }
    }

    describe("the Left unlifting") {

      it("unlifts a Left into its value") {
        val result = unliftL(successful(Left("woho")), "missing")
        result.futureValue should be("woho")
      }

      it("unlifts a Right into an exception") {
        val result = liftTry(unliftL(successful(Right("woho")), "missing"))
        result.futureValue should be(Failure(new UnliftException("missing")))
      }

    }

    describe("the implicit left unlifting") {
      import Lifting.Implicits.FutureEitherDecorator
      it("unlifts a Left into its value") {
        val result = successful(Left("woho")).unliftL("missing")
        result.futureValue should be("woho")
      }

      it("unlifts a Right into an exception") {
        val result = liftTry(successful(Right("woho")).unliftL("missing"))
        result.futureValue should be(Failure(new UnliftException("missing")))
      }

      it("unlifts a Right into a custom exception") {
        val ex     = new RuntimeException("darnit")
        val result = liftTry(successful(Right("woho")).unliftLEx(ex))
        result.futureValue should be(Failure(ex))
      }
    }

    describe("the Right unlifting") {

      it("unlifts a Right into its value") {
        val result = unliftR(successful(Right("woho")), "missing")
        result.futureValue should be("woho")
      }

      it("unlifts a Left into an exception") {
        val result = liftTry(unliftR(successful(Left("woho")), "missing"))
        result.futureValue should be(Failure(new UnliftException("missing")))
      }
    }

    describe("the implicit Right unlifting") {
      import Lifting.Implicits.FutureEitherDecorator
      it("unlifts a Right into its value") {
        val result = successful(Right("woho")).unliftR("missing")
        result.futureValue should be("woho")
      }

      it("unlifts a Left into an exception") {
        val result = liftTry(successful(Left("woho")).unliftR("missing"))
        result.futureValue should be(Failure(new UnliftException("missing")))
      }

      it("unlifts a Left into a custom exception") {
        val ex     = new RuntimeException("bork bork")
        val result = liftTry(successful(Left("woho")).unliftREx(ex))
        result.futureValue should be(Failure(ex))
      }
    }

  }
}
