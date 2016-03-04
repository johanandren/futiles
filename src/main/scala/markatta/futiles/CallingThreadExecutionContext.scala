/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.typesafe.com>
 */
package markatta.futiles

import scala.concurrent.ExecutionContext

/**
 * This is a common optimization that allows us to avoid scheduling future transformations
 * for execution on the execution context but instead just run them on the calling thread.
 * Should never be used for anything but simple quick transformations.
 */
private[futiles] object CallingThreadExecutionContext extends ExecutionContext {
  override def execute(runnable: Runnable): Unit = runnable.run()
  override def reportFailure(cause: Throwable): Unit = throw cause
  implicit def Implicit: ExecutionContext = this
}
