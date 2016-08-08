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
import java.util.{Timer, TimerTask}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.concurrent.duration.FiniteDuration
import scala.util.Try
import scala.collection.immutable.Seq

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

  object Implicits {
    final implicit class FutureTimeoutDecorator[T](future: Future[T]) {

      /**
       * If this future takes more than `atMost` it will instead be failed with a [[java.util.concurrent.TimeoutException]]
       *
       * Note that the original future will always complete at some point,
       * so this does in no way cancel the future if it times out.
       */
      def withTimeoutError(atMost: FiniteDuration)(implicit ec: ExecutionContext): Future[T] =
        Future.firstCompletedOf[T](Seq(
          future,
          timeout[T](atMost)(throw new TimeoutException(s"Timed out after $atMost"))))

      /**
       * If this future takes more than `atMost` it will instead be completed with a the given default
       *
       * Note that the original future will always complete at some point,
       * so this does in no way cancel the future if it times out.
       */
      def withTimeoutDefault[U >: T](atMost: FiniteDuration, default: => U)(implicit ec: ExecutionContext): Future[U] =
        Future.firstCompletedOf[U](Seq(
          future,
          timeout(atMost)(default)))

    }
  }

}
