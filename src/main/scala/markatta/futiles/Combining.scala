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

object Combining {

  import CallingThreadExecutionContext.Implicit

  /**
   * Combine two future values into a tuple when they arrive, or fail if either fails
   * (just an alias for Future.zip)
   */
  def product[A,B](fa: Future[A], fb: Future[B]): Future[(A, B)] =
    fa.zip(fb)

  /**
   * Combine three future values into a tuple when they arrive, or fail if either fails
   */
  def product[A, B, C](fa: Future[A], fb: Future[B], fc: Future[C]): Future[(A, B, C)] =
    for {
      a <- fa
      b <- fb
      c <- fc
    } yield (a, b, c)


  /**
   * Combine four future values into a tuple when they arrive, or fail if either fails
   */
  def product[A, B, C, D](fa: Future[A], fb: Future[B], fc: Future[C], fd: Future[D]): Future[(A, B, C, D)] =
    for {
      a <- fa
      b <- fb
      c <- fc
      d <- fd
    } yield (a, b, c, d)


  /**
   * Combine five future values into a tuple when they arrive, or fail if either fails
   */
  def product[A, B, C, D, E](fa: Future[A], fb: Future[B], fc: Future[C], fd: Future[D], fe: Future[E]): Future[(A, B, C, D, E)] =
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
  def product[A, B, C, D, E, F](fa: Future[A], fb: Future[B], fc: Future[C], fd: Future[D], fe: Future[E], ff: Future[F]): Future[(A, B, C, D, E, F)] =
    for {
      a <- fa
      b <- fb
      c <- fc
      d <- fd
      e <- fe
      f <- ff
    } yield (a, b, c, d, e, f)


  def map2[A, B, R](fa: Future[A], fb: Future[B])(f: (A, B) => R): Future[R] =
    product(fa, fb).map(f.tupled)

  def map3[A, B, C, R](fa: Future[A], fb: Future[B], fc: Future[C])(f: (A, B, C) => R): Future[R] =
    product(fa, fb, fc).map(f.tupled)

  def map4[A, B, C, D, R](fa: Future[A], fb: Future[B], fc: Future[C], fd: Future[D])(f: (A, B, C, D) => R): Future[R] =
    product(fa, fb, fc, fd).map(f.tupled)

  def map5[A, B, C, D, E, R](fa: Future[A], fb: Future[B], fc: Future[C], fd: Future[D], fe: Future[E])(f: (A, B, C, D, E) => R): Future[R] =
    product(fa, fb, fc, fd, fe).map(f.tupled)

  def map6[A, B, C, D, E, F, R](fa: Future[A], fb: Future[B], fc: Future[C], fd: Future[D], fe: Future[E], ff: Future[F])(f: (A, B, C, D, E, F) => R): Future[R] =
    product(fa, fb, fc, fd, fe, ff).map(f.tupled)



  def flatMap2[A, B, R](fa: Future[A], fb: Future[B])(f: (A, B) => Future[R]): Future[R] =
    product(fa, fb).flatMap(f.tupled)

  def flatMap3[A, B, C, R](fa: Future[A], fb: Future[B], fc: Future[C])(f: (A, B, C) => Future[R]): Future[R] =
    product(fa, fb, fc).flatMap(f.tupled)

  def flatMap4[A, B, C, D, R](fa: Future[A], fb: Future[B], fc: Future[C], fd: Future[D])(f: (A, B, C, D) => Future[R]): Future[R] =
    product(fa, fb, fc, fd).flatMap(f.tupled)

  def flatMap5[A, B, C, D, E, R](fa: Future[A], fb: Future[B], fc: Future[C], fd: Future[D], fe: Future[E])(f: (A, B, C, D, E) => Future[R]): Future[R] =
    product(fa, fb, fc, fd, fe).flatMap(f.tupled)

  def flatMap6[A, B, C, D, E, F, R](fa: Future[A], fb: Future[B], fc: Future[C], fd: Future[D], fe: Future[E], ff: Future[F])(f: (A, B, C, D, E, F) => Future[R]): Future[R] =
    product(fa, fb, fc, fd, fe, ff).flatMap(f.tupled)

}
