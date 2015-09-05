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
