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

import markatta.futiles.Sequencing._

import scala.concurrent.Future
import scala.util.{Failure, Success}

class SequencingSpec extends Spec {

  describe("The sequencing utilities for collections of futures") {

    describe("the option sequencer") {

      it("sequences a some future value into a future some value") {
        val result = sequenceOpt(Some(Future.successful("woho")))
        result.futureValue should be (Some("woho"))
      }

      it("sequences no future value into a future no value") {
        val result = sequenceOpt(None)
        result.futureValue should be (None)
      }

    }


    describe("the left sequencer") {

      it("sequences a left future value into a future left value"){
        val result = sequenceL(Left(Future.successful("woho")))
        result.futureValue should be (Left("woho"))
      }


      it("sequences a right value into a future right value"){
        val result = sequenceL(Right("woho"))
        result.futureValue should be (Right("woho"))
      }

    }


    describe("the right sequencer") {

      it("sequences a left value into a future left value"){
        val result = sequenceR(Left("woho"))
        result.futureValue should be (Left("woho"))
      }


      it("sequences a right future value into a future right value"){
        val result = sequenceR(Right(Future.successful("woho")))
        result.futureValue should be (Right("woho"))
      }

    }


    describe("the either sequencer") {

      it("sequences a left future value into a future left value"){
        val result = sequenceEither(Left(Future.successful("woho")))
        result.futureValue should be (Left("woho"))
      }


      it("sequences a right future value into a future right value"){
        val result = sequenceEither(Right(Future.successful("woho")))
        result.futureValue should be (Right("woho"))
      }

    }


    describe("the try sequencer") {

      it("sequences a list of successful futures into a list of successes") {
        val result = sequenceTries(List(1, 2).map(Future.successful))
        result.futureValue should be (List(1, 2).map(Success.apply))
      }

      it("sequences a list of with failed futures into a list of failures") {
        val exception = new RuntimeException("fel")
        val result = sequenceTries(List(1, 2).map(n => Future.failed[Int](exception)))
        result.futureValue should be (List(Failure[Int](exception), Failure[Int](exception)))
      }

    }

  }

}
