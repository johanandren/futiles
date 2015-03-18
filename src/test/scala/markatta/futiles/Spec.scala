package markatta.futiles

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FunSpec, Matchers}

abstract class Spec extends FunSpec with Matchers with ScalaFutures {
  implicit val ec = scala.concurrent.ExecutionContext.Implicits.global
}
