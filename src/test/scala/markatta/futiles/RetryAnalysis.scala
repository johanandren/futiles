/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package markatta.futiles

import java.io.File

import scala.concurrent.duration._

object RetryAnalysis extends App {

  val file = new File("./data.tsv")
  val p = new java.io.PrintWriter(file)

  val range = (1 to 10)
  val unit = 1.second

  val result = range.flatMap { n =>
    (0 to 1000).map(
      _ => p.println(s"$n\t${Retry.nextBackOff(n, unit).toMillis}")
    )
  }

  println(s"Wrote $file")

}
