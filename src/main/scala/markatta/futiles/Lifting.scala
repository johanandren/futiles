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
  def liftTry[A](future: Future[A])(implicit ec: ExecutionContext): Future[Try[A]] =
    future
      .map(Success.apply)
      .recover {
        case x: Exception => Failure(x)
      }


  /**
   * Unlifts a Future(Some(a)) into Future(a) and Future(None) into a failed future with a
   * [[UnliftException]]
   * @param exceptionMessageOnNone The message put in the exception
   */
  def unliftOption[A](future: Future[Option[A]], exceptionMessageOnNone: => String)(implicit ec: ExecutionContext): Future[A] =
    future
      .map(_.getOrElse(throw new UnliftException(exceptionMessageOnNone)))

  /**
   * Unlifts Future(Left(a)) into Future(a) and Future(Right(_)) into a future failed with [[UnliftException]]
   * @param exceptionMessageOnNone The message to put in the exception
   */
  def unliftL[A, B](future: Future[Either[A, B]], exceptionMessageOnNone: => String)(implicit ec: ExecutionContext): Future[A] =
    future.map(_.fold(
      identity,
      _ => throw new UnliftException(exceptionMessageOnNone)
    ))

  /**
   * Unlifts Future(Left(_)) into a future failed with UnliftException and and Future(Right(b)) into a Future(b)
   * @param exceptionMessageOnNone The message to put in the exception
   */
  def unliftR[A, B](future: Future[Either[A, B]], exceptionMessageOnNone: => String)(implicit ec: ExecutionContext): Future[B] =
    future.map(_.fold(
      _ => throw new UnliftException(exceptionMessageOnNone),
      identity
    ))


}
