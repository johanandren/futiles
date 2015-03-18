package markatta.futiles

import scala.concurrent.duration._

class TimeoutsSpec extends Spec {

  describe("The timeout utils") {

    it("runs a future after a given timeout") {
      val result = Timeouts.timeout(2.millis)("success")
      result.futureValue should be ("success")
    }
  }
}
