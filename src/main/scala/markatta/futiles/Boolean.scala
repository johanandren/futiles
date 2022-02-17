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

import scala.concurrent.{ExecutionContext, Future}

/** Boolean algebra composition operations for Future[Boolean]
  *
  * Credit goes to http://stackoverflow.com/users/3235823/dwickern for the idea to do this.
  */
object Boolean {

  /** @return
    *   A new future that will be true if both futures are true, if this one is false the second one will not be
    *   evaluated (just like with regular booleans).
    */
  def and(fb: Future[Boolean], other: => Future[Boolean])(implicit ec: ExecutionContext): Future[Boolean] =
    fb.flatMap(b => if (!b) Future.successful(false) else other)

  /** @return
    *   A new future that will be true if either future is true, if this one is true the second one will not be
    *   evaluated (just like with regular booleans).
    */
  def or(fb: Future[Boolean], other: => Future[Boolean])(implicit ec: ExecutionContext): Future[Boolean] =
    fb.flatMap(b => if (b) Future.successful(true) else other)

  def negate(fb: Future[Boolean])(implicit ec: ExecutionContext): Future[Boolean] =
    fb.map(!_)

  object Implicits {

    implicit class FutureBooleanDecorator(val fb: Future[Boolean]) extends AnyVal {

      /** @return
        *   A new future that will be true if both futures are true, if this one is false the second one will not be
        *   evaluated (just like with regular booleans).
        */
      def &&(other: => Future[Boolean])(implicit ec: ExecutionContext): Future[Boolean] =
        and(fb, other)(ec)

      /** @return
        *   A new future that will be true if either future is true, if this one is true the second one will not be
        *   evaluated (just like with regular booleans).
        */
      def ||(other: => Future[Boolean])(implicit ec: ExecutionContext): Future[Boolean] =
        or(fb, other)

      def unary_!(implicit ec: ExecutionContext): Future[Boolean] = negate(fb)(ec)

    }

  }
}
