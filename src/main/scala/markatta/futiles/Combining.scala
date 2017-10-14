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

import java.util.concurrent.atomic.AtomicReference

import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

object Combining {

  import CallingThreadExecutionContext.Implicit

  /**
   * Combine two future values into a tuple when they arrive, or fail as soon as either of them fails
   * (just an alias for Future.zip)
   */
  def product[A,B](fa: Future[A], fb: Future[B]): Future[(A, B)] = {
    val promise = Promise[(A, B)]()
    val ref = new AtomicReference[(A, B)]((null.asInstanceOf[A], null.asInstanceOf[B]))
    def tryUpdate(mod: (A, B) => (A, B)): Unit = {
      val original = ref.get()
      val updated = mod.tupled(original)
      if (updated._1 != null && updated._2 != null) promise.trySuccess(updated)
      else if (ref.compareAndSet(original, updated)) ()
      else tryUpdate(mod)
    }

    fa.onComplete {
      case Success(a) => tryUpdate((_, b) => (a, b))
      case Failure(ex) => promise.tryFailure(ex)
    }

    fb.onComplete {
      case Success(b) => tryUpdate((a, _) => (a, b))
      case Failure(ex) => promise.tryFailure(ex)
    }

    promise.future
  }

  /**
   * Combine three future values into a tuple when they arrive, or fail as soon as any one of them fails
   */
  def product[A, B, C](fa: Future[A], fb: Future[B], fc: Future[C]): Future[(A, B, C)] = {
    val promise = Promise[(A, B, C)]()
    val ref = new AtomicReference[(A, B, C)]((null.asInstanceOf[A], null.asInstanceOf[B], null.asInstanceOf[C]))
    def tryUpdate(mod: (A, B, C) => (A, B, C)): Unit = {
      val original = ref.get()
      val updated = mod.tupled(original)
      if (updated._1 != null && updated._2 != null && updated._3 != null) promise.trySuccess(updated)
      else if (ref.compareAndSet(original, updated)) ()
      else tryUpdate(mod)
    }

    fa.onComplete {
      case Success(a) => tryUpdate((_, b, c) => (a, b, c))
      case Failure(ex) => promise.tryFailure(ex)
    }

    fb.onComplete {
      case Success(b) => tryUpdate((a, _, c) => (a, b, c))
      case Failure(ex) => promise.tryFailure(ex)
    }

    fc.onComplete {
      case Success(c) => tryUpdate((a, b, _) => (a, b, c))
      case Failure(ex) => promise.tryFailure(ex)
    }

    promise.future
  }


  /**
   * Combine four future values into a tuple when they arrive, or fail as soon as any one of them fails
   */
  def product[A, B, C, D](fa: Future[A], fb: Future[B], fc: Future[C], fd: Future[D]): Future[(A, B, C, D)] = {
    val promise = Promise[(A, B, C, D)]()
    val ref = new AtomicReference[(A, B, C, D)]((null.asInstanceOf[A], null.asInstanceOf[B], null.asInstanceOf[C], null.asInstanceOf[D]))
    def tryUpdate(mod: (A, B, C, D) => (A, B, C, D)): Unit = {
      val original = ref.get()
      val updated = mod.tupled(original)
      if (updated._1 != null && updated._2 != null && updated._3 != null && updated._4 != null) promise.trySuccess(updated)
      else if (ref.compareAndSet(original, updated)) ()
      else tryUpdate(mod)
    }

    fa.onComplete {
      case Success(a) => tryUpdate((_, b, c, d) => (a, b, c, d))
      case Failure(ex) => promise.tryFailure(ex)
    }

    fb.onComplete {
      case Success(b) => tryUpdate((a, _, c, d) => (a, b, c, d))
      case Failure(ex) => promise.tryFailure(ex)
    }

    fc.onComplete {
      case Success(c) => tryUpdate((a, b, _, d) => (a, b, c, d))
      case Failure(ex) => promise.tryFailure(ex)
    }

    fd.onComplete {
      case Success(d) => tryUpdate((a, b, c, _) => (a, b, c, d))
      case Failure(ex) => promise.tryFailure(ex)
    }

    promise.future
  }



  /**
   * Combine five future values into a tuple when they arrive, or fail as soon as any one of them fails
   */
  def product[A, B, C, D, E](fa: Future[A], fb: Future[B], fc: Future[C], fd: Future[D], fe: Future[E]): Future[(A, B, C, D, E)] = {
    val promise = Promise[(A, B, C, D, E)]()
    val ref = new AtomicReference[(A, B, C, D, E)]((null.asInstanceOf[A], null.asInstanceOf[B], null.asInstanceOf[C], null.asInstanceOf[D], null.asInstanceOf[E]))
    def tryUpdate(mod: (A, B, C, D, E) => (A, B, C, D, E)): Unit = {
      val original = ref.get()
      val updated = mod.tupled(original)
      if (updated._1 != null && updated._2 != null && updated._3 != null && updated._4 != null && updated._5 != null) promise.trySuccess(updated)
      else if (ref.compareAndSet(original, updated)) ()
      else tryUpdate(mod)
    }

    fa.onComplete {
      case Success(a) => tryUpdate((_, b, c, d, e) => (a, b, c, d, e))
      case Failure(ex) => promise.tryFailure(ex)
    }

    fb.onComplete {
      case Success(b) => tryUpdate((a, _, c, d, e) => (a, b, c, d, e))
      case Failure(ex) => promise.tryFailure(ex)
    }

    fc.onComplete {
      case Success(c) => tryUpdate((a, b, _, d, e) => (a, b, c, d, e))
      case Failure(ex) => promise.tryFailure(ex)
    }

    fd.onComplete {
      case Success(d) => tryUpdate((a, b, c, _, e) => (a, b, c, d, e))
      case Failure(ex) => promise.tryFailure(ex)
    }

    fe.onComplete {
      case Success(e) => tryUpdate((a, b, c, d, _) => (a, b, c, d, e))
      case Failure(ex) => promise.tryFailure(ex)
    }

    promise.future
  }


  /**
   * Combine six future values into a tuple when they arrive, or fail as soon as any one of them fails
   */
  def product[A, B, C, D, E, F](fa: Future[A], fb: Future[B], fc: Future[C], fd: Future[D], fe: Future[E], ff: Future[F]): Future[(A, B, C, D, E, F)] ={
    val promise = Promise[(A, B, C, D, E, F)]()
    val ref = new AtomicReference[(A, B, C, D, E, F)]((null.asInstanceOf[A], null.asInstanceOf[B], null.asInstanceOf[C], null.asInstanceOf[D], null.asInstanceOf[E], null.asInstanceOf[F]))
    def tryUpdate(mod: (A, B, C, D, E, F) => (A, B, C, D, E, F)): Unit = {
      val original = ref.get()
      val updated = mod.tupled(original)
      if (updated._1 != null && updated._2 != null && updated._3 != null && updated._4 != null && updated._5 != null && updated._6 != null) promise.trySuccess(updated)
      else if (ref.compareAndSet(original, updated)) ()
      else tryUpdate(mod)
    }

    fa.onComplete {
      case Success(a) => tryUpdate((_, b, c, d, e, f) => (a, b, c, d, e, f))
      case Failure(ex) => promise.tryFailure(ex)
    }

    fb.onComplete {
      case Success(b) => tryUpdate((a, _, c, d, e, f) => (a, b, c, d, e, f))
      case Failure(ex) => promise.tryFailure(ex)
    }

    fc.onComplete {
      case Success(c) => tryUpdate((a, b, _, d, e, f) => (a, b, c, d, e, f))
      case Failure(ex) => promise.tryFailure(ex)
    }

    fd.onComplete {
      case Success(d) => tryUpdate((a, b, c, _, e, f) => (a, b, c, d, e, f))
      case Failure(ex) => promise.tryFailure(ex)
    }

    fe.onComplete {
      case Success(e) => tryUpdate((a, b, c, d, _, f) => (a, b, c, d, e, f))
      case Failure(ex) => promise.tryFailure(ex)
    }

    ff.onComplete {
      case Success(f) => tryUpdate((a, b, c, d, e, _) => (a, b, c, d, e, f))
      case Failure(ex) => promise.tryFailure(ex)
    }

    promise.future
  }


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
