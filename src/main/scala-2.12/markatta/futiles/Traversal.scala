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

import scala.collection.generic.CanBuildFrom
import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

/** Utilities that complement scala.concurrent.Future.traverse, working with creating a future out of each element in a
  * collection and collecting those into a single future.
  */
object Traversal {

  /** For each ```A``` apply the function ```f```, and wait for it to complete before continuing with the next ```A```
    *
    * @return
    *   The future all ```A```s turned into ```B``` or the first failure that occurred
    */
  def traverseSequentially[A, B, M[X] <: TraversableOnce[X]](as: M[A])(
      f: A => Future[B]
  )(implicit ec: ExecutionContext, cbf: CanBuildFrom[M[A], B, M[B]]): Future[M[B]] =
    foldLeftSequentially(as.toTraversable)(cbf())((builder, a) => f(a).map(b => builder += b)).map(_.result())

  /** Like a regular fold left, but with an operation that returns futures, each future will complete before the next
    * element in ```as``` is executed.
    *
    * @param z
    *   The zero value, if ```as``` is empty, this is returned, if not this is fed into ```f``` as the ```B``` value
    * @return
    *   The future of all ```A``` folded into ```B```s, or a future that is failed with any exception that is thrown
    *   from f
    */
  def foldLeftSequentially[A, B](
      as: Traversable[A]
  )(z: B)(f: (B, A) => Future[B])(implicit ec: ExecutionContext): Future[B] =
    if (as.isEmpty) Future.successful(z)
    else f(z, as.head).flatMap(b => foldLeftSequentially(as.tail)(b)(f))

}
