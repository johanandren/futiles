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
import scala.util.control.NoStackTrace
import scala.util.{Failure, Success, Try}

final case class UnliftException(msg: String) extends RuntimeException(msg) with NoStackTrace

/**
 * For lack of a better word, lifting and unlifting is boxing or unboxing other monady types inside of futures
 */
object Lifting {

  /**
   * Lifts the Try that is inside of the Future implementation to a Try that is inside the future.
   *
   * @return A future that always is successful, that will contain any exceptions inside of the nested Try
   */
  def liftTry[A](future: Future[A]): Future[Try[A]] =
    future
      .map(Success.apply)(CallingThreadExecutionContext)
      .recover {
        case x: Exception => Failure(x)
      }(CallingThreadExecutionContext)


  /**
   * Unlifts a Future(Some(a)) into Future(a) and Future(None) into a failed future with a
   * [[UnliftException]]
   * @param exceptionMessageOnNone The message put in the exception
   */
  def unliftOption[A](future: Future[Option[A]], exceptionMessageOnNone: => String): Future[A] =
    unliftOptionEx[A](future, new UnliftException(exceptionMessageOnNone))


  /**
   * Unlifts a Future(Some(a)) into Future(a) and Future(None) into a failed future with a
   * [[UnliftException]]
   * @param exceptionBlock The exception to fail the future with for None
   */
  def unliftOptionEx[A](future: Future[Option[A]], exceptionBlock: => Exception): Future[A] =
    future.map(_.getOrElse(throw exceptionBlock))(CallingThreadExecutionContext)

  /**
   * Unlifts Future(Left(a)) into Future(a) and Future(Right(_)) into a future failed with [[UnliftException]]
   * @param exceptionMessageOnRight The message to put in the exception
   */
  def unliftL[A, B](future: Future[Either[A, B]], exceptionMessageOnRight: => String): Future[A] =
    unliftLEx(future, new UnliftException(exceptionMessageOnRight))

  /**
   * Unlifts Future(Left(a)) into Future(a) and Future(Right(_)) into a future failed with the given exception
   */
  def unliftLEx[A, B](future: Future[Either[A, B]], exceptionOnRight: => Exception): Future[A] =
    future.map(_.fold(
      identity,
      _ => throw exceptionOnRight
    ))(CallingThreadExecutionContext)

  /**
   * Unlifts Future(Left(_)) into a future failed with UnliftException and and Future(Right(b)) into a Future(b)
   * @param exceptionMessageOnLeft The message to put in the exception
   */
  def unliftR[A, B](future: Future[Either[A, B]], exceptionMessageOnLeft: => String): Future[B] =
    unliftREx(future, new UnliftException(exceptionMessageOnLeft))

  /**
   * Unlifts Future(Left(_)) into a future failed with the given exception and and Future(Right(b)) into a Future(b)
   */
  def unliftREx[A, B](future: Future[Either[A, B]], exceptionOnLeft: => Exception): Future[B] =
    future.map(_.fold(
      _ => throw exceptionOnLeft,
      identity
    ))(CallingThreadExecutionContext)


  object Implicits {

    implicit class FutureOptDecorator[A](future: Future[Option[A]]) {
      /** @return Future(a) if the option is Some(a), a failed future with the given message if None */
      def unlift(exceptionMessageOnNone: => String): Future[A] =
        unliftOption[A](future, exceptionMessageOnNone)

      /** @return Future(a) if the option is Some(a), a failed future with the given exception if None */
      def unliftEx(exceptionOnNone: => Exception): Future[A] =
        unliftOptionEx[A](future, exceptionOnNone)
    }

    implicit class FutureEitherDecorator[A, B](future: Future[Either[A, B]]) {

      /** return Future(a) if Left, exceptionMessageOnRight inside of an UnliftException if Right */
      def unliftL(exceptionMessageOnRight: => String): Future[A] =
        Lifting.unliftL(future, exceptionMessageOnRight)

      /** return Future(a) if Left, exceptionOnRight if Right */
      def unliftLEx(exceptionOnRight: => Exception): Future[A] =
        Lifting.unliftLEx(future, exceptionOnRight)

      /** return Future(b) if Right, exceptionMessageOnLeft inside of an UnliftException if Left */
      def unliftR(exceptionMessageOnLeft: => String): Future[B] =
        Lifting.unliftR(future, exceptionMessageOnLeft)

      /** return Future(b) if Right, exceptionOnLeft if Left */
      def unliftREx(exceptionOnLeft: => Exception): Future[B] =
        Lifting.unliftREx(future, exceptionOnLeft)

    }

  }


}
