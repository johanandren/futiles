package markatta.futiles

import scala.concurrent.{ExecutionContext, Future}

/**
 * Boolean algebra composition operations for Future[Boolean]
 *
 * Credit goes to http://stackoverflow.com/users/3235823/dwickern for the idea to do this.
 */
object Boolean {

  /**
   * @return A new future that will be true if both futures are true, if this one
   *         is false the second one will not be evaluated (just like with regular booleans).
   */
  def and(fb: Future[Boolean], other: => Future[Boolean])(implicit ec: ExecutionContext) =
    fb.flatMap(b => if (!b) Future.successful(false) else other)

  /**
   * @return A new future that will be true if either future is true, if this one
   *         is true the second one will not be evaluated (just like with regular booleans).
   */
  def or(fb: Future[Boolean], other: => Future[Boolean])(implicit ec: ExecutionContext) =
    fb.flatMap(b => if (b) Future.successful(true) else other)

  def negate(fb: Future[Boolean])(implicit ec: ExecutionContext) =
    fb.map(!_)

  object Implicits {

    implicit class FutureBooleanDecorator(val fb: Future[Boolean]) extends AnyVal {

      /**
       * @return A new future that will be true if both futures are true, if this one
       *         is false the second one will not be evaluated (just like with regular booleans).
       */
      def &&(other: => Future[Boolean])(implicit ec: ExecutionContext) =
        and(fb, other)(ec)

      /**
       * @return A new future that will be true if either future is true, if this one
       *         is true the second one will not be evaluated (just like with regular booleans).
       */
      def ||(other: => Future[Boolean])(implicit ec: ExecutionContext) =
        or(fb, other)

      def unary_!(implicit ec: ExecutionContext) = negate(fb)(ec)

    }

  }
}
