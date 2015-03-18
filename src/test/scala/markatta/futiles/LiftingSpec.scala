package markatta.futiles

import scala.concurrent.Future
import scala.util.{Try, Success, Failure}

class LiftingSpec extends Spec {

  import Lifting._

  describe("The lifting utilities for futures") {


    describe("the Try lifter") {

      it("lifts a failed future") {
        val exception = new RuntimeException("error")
        val result: Future[Try[String]] = liftTry(Future.failed[String](exception))

        result.futureValue should be (Failure(exception))
      }

      it("lifts a successful future") {
        val result: Future[Try[String]] = liftTry(Future.successful("Success"))
        result.futureValue should be (Success("Success"))
      }

    }


    describe("the Option unlifter") {

      it("unlifts None into an UnliftException") {
        val result = unliftOption(Future.successful[Option[String]](None), "missing!")
        liftTry(result).futureValue should be (Failure(new UnliftException("missing!")))
      }

      it("unlifts a Some(value) into value") {
        val result = unliftOption(
          Future.successful[Option[String]](Some("woho")),
          "missing"
        )

        result.futureValue should be ("woho")
      }

    }


    describe("the Left unlifting") {

      it("unlifts a Left into its value") {
        val result = unliftL(Future(Left("woho")), "missing")
        result.futureValue should be ("woho")
      }

      it("unlifts a Right into an exception") {
        val result = liftTry(unliftL(Future(Right("woho")), "missing"))
        result.futureValue should be (Failure(new UnliftException("missing")))
      }

    }


    describe("the Right unlifting") {

      it("unlifts a Right into its value") {
        val result = unliftR(Future(Right("woho")), "missing")
        result.futureValue should be ("woho")
      }

      it("unlifts a Left into an exception") {
        val result = liftTry(unliftR(Future(Left("woho")), "missing"))
        result.futureValue should be (Failure(new UnliftException("missing")))
      }
    }

  }
}
