package markatta.futiles

import scala.collection.generic.CanBuildFrom
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

object Traversal {

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
  ): Future[M[B]] =
    tsLoop(as.toIterator, cbf())(f)

  // recursive traverse sequentially
  private def tsLoop[A, B, M[X] <: TraversableOnce[X]](
    as: Iterator[A], builder: mutable.Builder[B, M[B]]
  )(
    f: A => Future[B]
  )(
    implicit ec: ExecutionContext
  ): Future[M[B]] =
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
