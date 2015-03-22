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

import scala.collection.generic.CanBuildFrom
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import scala.language.higherKinds

/**
 * Functions for transforming something with a future inside into a future with something inside
 */
object Sequencing {

  /**
   * Turn an option of a future inside out, much like Future.sequence but keep it an Option
   */
  def sequenceOpt[A](f: Option[Future[A]])(implicit ec: ExecutionContext): Future[Option[A]] =
    f.map(_.map(Some(_))).getOrElse(Future.successful(None))


  /**
   * @return a Future(Left(l)) for a Left(Future(l)) and a Future(Right(r)) for a Right(Future(r))
   */
  def sequenceEither[L, R](either: Either[Future[L], Future[R]])(implicit ec: ExecutionContext): Future[Either[L, R]] =
    either.fold(
      lf => lf.map(l => Left[L, R](l)),
      rf => rf.map(r => Right[L, R](r))
    )

  /**
   * @return a Future(Left(l)) for a Left(Future(l)) and a Future(Right(r)) for a Right(r)
   */
  def sequenceL[L, R](either: Either[Future[L], R])(implicit ec: ExecutionContext): Future[Either[L, R]] =
    sequenceEither(either.right.map(Future.successful))

  /**
   * @return a Future(Left(l)) for a Left(l) and a Future(Right(r)) for a Right(Future(r))
   */
  def sequenceR[L, R](either: Either[L, Future[R]])(implicit ec: ExecutionContext): Future[Either[L, R]] =
    sequenceEither(either.left.map(Future.successful))


  /**
   * Like [[Future.sequence()]] but instead of failing the result future when one future fails
   * it will collect each success or failure and complete once all futures has either failed or succeeded
   *
   * @return A future that completes once all the given futures has completed, successfully or failed, so
   *         that all of the failures can be handled, reported etc.
   * @see [[Lifting.liftTry()]]
   */
  def sequenceTries[A, M[X] <: TraversableOnce[X]](
    fas: M[Future[A]]
  )(
    implicit ec: ExecutionContext, cbf: CanBuildFrom[M[Future[A]], Try[A], M[Try[A]]]
  ): Future[M[Try[A]]] = {
    val fts = fas.foldLeft(Future.successful(cbf())) { (facc, fa) =>
      for {
        acc <- facc
        ta <- Lifting.liftTry(fa)
      } yield acc += ta
    }
    fts.map(_.result())
  }




}
