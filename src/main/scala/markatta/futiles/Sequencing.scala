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

  /**
   * For each ```A``` apply the function ```f```, and wait for it to complete before continuing with the next ```A```
   *
   * @return The future all ```A```s turned into ```B``` or the first failure that occurred
   */
  def traverseSequentially[A, B, M[X] <: TraversableOnce[X]](
    as: M[A]
  )(
    f: A => Future[B]
  )(
    implicit ec: ExecutionContext, cbf: CanBuildFrom[M[A], B, M[B]]
  ): Future[M[B]] = {
    val iterator = as.toIterator
    val builder = cbf()
    tsLoop(iterator, builder)(f)
  }

  private def tsLoop[A, B, M[X] <: TraversableOnce[X]](
    as: Iterator[A], builder: mutable.Builder[B, M[B]]
  )(
    f: A => Future[B]
  )(
    implicit ec: ExecutionContext
  ): Future[M[B]] = {
    if (as.hasNext) {
      val fb: Future[B] = f(as.next())

      // do not continue to next until b arrives
      fb.flatMap[M[B]] { b =>
        builder += b
        tsLoop(as, builder)(f)
      }
    } else {
      Future.successful(builder.result())
    }
  }


}
