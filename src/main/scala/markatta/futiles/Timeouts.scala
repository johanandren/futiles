/*
 * Copyright 2015 Heiko Seeberger
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

import java.util.{TimerTask, Timer}

import scala.concurrent.{Future, Promise, ExecutionContext}
import scala.concurrent.duration.FiniteDuration
import scala.util.Try

object Timeouts {

  private val timer = new Timer()

  /**
   * When ```waitFor``` has passed, evaluate ```what``` on the given execution context and complete the future
   */
  def timeout[A](waitFor: FiniteDuration)(what: => A)(implicit ec: ExecutionContext): Future[A] = {
    val promise = Promise[A]()
    timer.schedule(new TimerTask {
      override def run(): Unit = {
        // make sure we do not block the timer thread
        Future {
          promise.complete(Try{ what })
        }
      }
    }, waitFor.toMillis)

    promise.future
  }



}
