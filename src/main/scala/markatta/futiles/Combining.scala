package markatta.futiles

import scala.concurrent.{ExecutionContext, Future}

object Combining {

  /**
   * Combine two future values into a tuple when they arrive, or fail if either fails
   * (just an alias for Future.zip)
   */
  def product[A,B](fa: Future[A], fb: Future[B])(implicit ec: ExecutionContext): Future[(A, B)] =
    fa.zip(fb)

  /**
   * Combine three future values into a tuple when they arrive, or fail if either fails
   */
  def product[A, B, C](fa: Future[A], fb: Future[B], fc: Future[C])(implicit ec: ExecutionContext): Future[(A, B, C)] =
    for {
      a <- fa
      b <- fb
      c <- fc
    } yield (a, b, c)

  /**
   * Combine four future values into a tuple when they arrive, or fail if either fails
   */
  def product[A, B, C, D](fa: Future[A], fb: Future[B], fc: Future[C], fd: Future[D])(implicit ec: ExecutionContext): Future[(A, B, C, D)] =
    for {
      a <- fa
      b <- fb
      c <- fc
      d <- fd
    } yield (a, b, c, d)

  /**
   * Combine five future values into a tuple when they arrive, or fail if either fails
   */
  def product[A, B, C, D, E](fa: Future[A], fb: Future[B], fc: Future[C], fd: Future[D], fe: Future[E])(implicit ec: ExecutionContext): Future[(A, B, C, D, E)] =
    for {
      a <- fa
      b <- fb
      c <- fc
      d <- fd
      e <- fe
    } yield (a, b, c, d, e)

  /**
   * Combine six future values into a tuple when they arrive, or fail if either fails
   */
  def product[A, B, C, D, E, F](fa: Future[A], fb: Future[B], fc: Future[C], fd: Future[D], fe: Future[E], ff: Future[F])(implicit ec: ExecutionContext): Future[(A, B, C, D, E, F)] =
    for {
      a <- fa
      b <- fb
      c <- fc
      d <- fd
      e <- fe
      f <- ff
    } yield (a, b, c, d, e, f)


  def map2[A, B, R](fa: Future[A], fb: Future[B])(f: (A, B) => R)(implicit ec: ExecutionContext): Future[R] =
    product(fa, fb).map(f.tupled)

  def map3[A, B, C, R](fa: Future[A], fb: Future[B], fc: Future[C])(f: (A, B, C) => R)(implicit ec: ExecutionContext): Future[R] =
    product(fa, fb, fc).map(f.tupled)

  def map4[A, B, C, D, R](fa: Future[A], fb: Future[B], fc: Future[C], fd: Future[D])(f: (A, B, C, D) => R)(implicit ec: ExecutionContext): Future[R] =
    product(fa, fb, fc, fd).map(f.tupled)

  def map5[A, B, C, D, E, R](fa: Future[A], fb: Future[B], fc: Future[C], fd: Future[D], fe: Future[E])(f: (A, B, C, D, E) => R)(implicit ec: ExecutionContext): Future[R] =
    product(fa, fb, fc, fd, fe).map(f.tupled)

  def map6[A, B, C, D, E, F, R](fa: Future[A], fb: Future[B], fc: Future[C], fd: Future[D], fe: Future[E], ff: Future[F])(f: (A, B, C, D, E, F) => R)(implicit ec: ExecutionContext): Future[R] =
    product(fa, fb, fc, fd, fe, ff).map(f.tupled)



  def flatMap2[A, B, R](fa: Future[A], fb: Future[B])(f: (A, B) => Future[R])(implicit ec: ExecutionContext): Future[R] =
    product(fa, fb).flatMap(f.tupled)

  def flatMap3[A, B, C, R](fa: Future[A], fb: Future[B], fc: Future[C])(f: (A, B, C) => Future[R])(implicit ec: ExecutionContext): Future[R] =
    product(fa, fb, fc).flatMap(f.tupled)

  def flatMap4[A, B, C, D, R](fa: Future[A], fb: Future[B], fc: Future[C], fd: Future[D])(f: (A, B, C, D) => Future[R])(implicit ec: ExecutionContext): Future[R] =
    product(fa, fb, fc, fd).flatMap(f.tupled)

  def flatMap5[A, B, C, D, E, R](fa: Future[A], fb: Future[B], fc: Future[C], fd: Future[D], fe: Future[E])(f: (A, B, C, D, E) => Future[R])(implicit ec: ExecutionContext): Future[R] =
    product(fa, fb, fc, fd, fe).flatMap(f.tupled)

  def flatMap6[A, B, C, D, E, F, R](fa: Future[A], fb: Future[B], fc: Future[C], fd: Future[D], fe: Future[E], ff: Future[F])(f: (A, B, C, D, E, F) => Future[R])(implicit ec: ExecutionContext): Future[R] =
    product(fa, fb, fc, fd, fe, ff).flatMap(f.tupled)

}
