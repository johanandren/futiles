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

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FunSpec, Matchers}

import scala.concurrent.duration._

abstract class Spec extends FunSpec with Matchers with ScalaFutures {
  implicit val ec = scala.concurrent.ExecutionContext.Implicits.global
  override implicit def patienceConfig: PatienceConfig = PatienceConfig(1.second, 100.millis)
}
