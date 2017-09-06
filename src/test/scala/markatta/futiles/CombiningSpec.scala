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

import scala.concurrent.Future.{failed, successful}
import scala.concurrent.Promise
import scala.util.Failure

class CombiningSpec extends Spec {

  import Combining._
  import Lifting.liftTry

  describe("the combining utilities for futures") {

    describe("product") {

      it("combines two successful futures into a tuple2") {
        product(successful(1), successful(2)).futureValue should be((1, 2))
      }

      it("combines one successful and one failed futures into a failure") {
        val ex = new RuntimeException("bah")
        liftTry(product(successful(1), failed(ex))).futureValue should be(Failure(ex))
      }

      it("combines three successful futures into a tuple3") {
        product(successful(1), successful(2), successful(3)).futureValue should be((1, 2, 3))
      }

      it("fails fast three on combining futures into a tuple3") {
        product(Promise().future, Promise().future, failed(new RuntimeException("argh"))).failed.futureValue shouldBe a[RuntimeException]
      }

      it("combines two successful and one failed futures into a failure") {
        val ex = new RuntimeException("bah")
        liftTry(product(successful(1), successful(2), failed(ex))).futureValue should be(Failure(ex))
      }

      it("combines four successful futures into a tuple4") {
        product(successful(1), successful(2), successful(3), successful(4)).futureValue should be((1, 2, 3, 4))
      }

      it("combines three successful and one failed futures into a failure") {
        val ex = new RuntimeException("bah")
        liftTry(product(successful(1), successful(2), successful(3), failed(ex))).futureValue should be(Failure(ex))
      }

      it("combines five successful futures into a tuple5") {
        product(successful(1), successful(2), successful(3), successful(4), successful(5)).futureValue should be((1, 2, 3, 4, 5))
      }

      it("combines four successful and one failed futures into a failure") {
        val ex = new RuntimeException("bah")
        liftTry(product(successful(1), successful(2), successful(3), successful(4), failed(ex))).futureValue should be(Failure(ex))
      }

      it("combines six successful futures into a tuple6") {
        product(successful(1), successful(2), successful(3), successful(4), successful(5), successful(6)).futureValue should be((1, 2, 3, 4, 5, 6))
      }

      it("combines five successful and one failed futures into a failure") {
        val ex = new RuntimeException("bah")
        liftTry(product(successful(1), successful(2), successful(3), successful(4), successful(5), failed(ex))).futureValue should be(Failure(ex))
      }
    }

    describe("mapN") {

      it("maps two successful futures into a result using a function") {
        map2(successful(1), successful(2))((a, b) => a + b).futureValue should be(3)
      }

      it("maps three successful futures into a result using a function") {
        map3(successful(1), successful(2), successful(3))((a, b, c) => a + b + c).futureValue should be(6)
      }

      it("maps four successful futures into a result using a function") {
        map4(successful(1), successful(2), successful(3), successful(4))((a, b, c, d) => a + b + c + d).futureValue should be(10)
      }

      it("maps five successful futures into a result using a function") {
        map5(successful(1), successful(2), successful(3), successful(4), successful(5))((a, b, c, d, e) => a + b + c + d + e).futureValue should be(15)
      }

      it("maps six successful futures into a result using a function") {
        map6(successful(1), successful(2), successful(3), successful(4), successful(5), successful(6))((a, b, c, d, e, f) => a + b + c + d + e + f).futureValue should be(21)
      }

    }

    describe("flatMapN") {

      it("flatMaps two successful futures into a result using a function") {
        flatMap2(successful(1), successful(2))((a, b) => successful(a + b)).futureValue should be(3)
      }

      it("flatMaps three successful futures into a result using a function") {
        flatMap3(successful(1), successful(2), successful(3))((a, b, c) => successful(a + b + c)).futureValue should be(6)
      }

      it("flatMaps four successful futures into a result using a function") {
        flatMap4(successful(1), successful(2), successful(3), successful(4))((a, b, c, d) => successful(a + b + c + d)).futureValue should be(10)
      }

      it("flatMaps five successful futures into a result using a function") {
        flatMap5(successful(1), successful(2), successful(3), successful(4), successful(5))((a, b, c, d, e) => successful(a + b + c + d + e)).futureValue should be(15)
      }

      it("flatMaps six successful futures into a result using a function") {
        flatMap6(successful(1), successful(2), successful(3), successful(4), successful(5), successful(6))((a, b, c, d, e, f) => successful(a + b + c + d + e + f)).futureValue should be(21)
      }
    }

  }

}
