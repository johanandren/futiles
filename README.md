# Futiles

[![Join the chat at https://gitter.im/johanandren/futiles](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/johanandren/futiles?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/johanandren/futiles.svg)](https://travis-ci.org/johanandren/futiles)
[![Coverage Status](https://coveralls.io/repos/johanandren/futiles/badge.svg?branch=master)](https://coveralls.io/r/johanandren/futiles?branch=master)

The missing utils for working with Scala Futures

Throughout a few different Scala projects I have written these utility functions for working with futures
over and over again.

## Quick start
The artifact is on maven central and can be used by adding it to your project dependencies
in sbt:
```scala
libraryDependencies += "com.markatta" %% "futiles" % "1.1.0"
```

## Examples

### Sequencing futures
The utilities inside of ```markatta.futiles.Sequencing``` allows
you to sequence other functor types than collections, just like the built in
```scala.concurrent.Future.sequence``` function does.

#### Options
Make a future option value out of an option future value:

```scala
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Future.successful
import markatta.futiles.Sequencing.sequenceOpt
val optionFuture: Option[Future[String]] = Some(successful("woho!"))
val futureOption: Future[Option[String]] = sequenceOpt(optionFuture)
```

#### Eithers
The either sequencing allows you to go from one or two futures inside of an ```Either``` to
a future with an either inside of it:

 * ```sequenceEither``` allows you to go from ```Either[Future[L], Future[R]]``` to ```Future[Either[L, R]]```
 * ```sequenceL``` allows you to go from ```Either[Future[L], R]``` to ```Future[Either[L, R]]```
 * ```sequenceR``` allows you to go from ```Either[L, Future[R]]``` to ```Future[Either[L, R]]```


#### Tries
```sequenceTries``` takes a collection of ```Future[A]``` and turn them into a future collection of
each succeeded of failed future. So compared to the build in sequence it will not fail with the first
failed future but rather collect all those and let you handle them when all has arrived.

```scala
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Future.{successful, failed}
import scala.util.Try
import markatta.futiles.Sequencing.sequenceTries

val futures: Seq[Future[String]] = Seq(successful("woho"), failed(new RuntimeException("bork")))

val allOfEm: Future[Seq[Try[String]]] = sequenceTries(futures)
```


### Traversal
```scala.concurrent.Future.traverse``` allows you to have a collection of ```A```s,
apply a function ```A => Future[B]``` to each of them and then sequence the resulting collection
into a future collection of ```B```.

Futiles contains one addition to that concept which basically does the same, but applies the
function sequentially, so that at any time only one future is executing and the next one will
not be done until it completes. If any future fails, it will stop and return a failed future
with that exception.

**Example:**
```scala
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future.{successful, failed}
import markatta.futiles.Traversal.traverseSequentially
import java.util.concurrent.CountDownLatch

val latch = new CountDownLatch(3)
val result = traverseSequentially(List(1,2,3)) { n =>
  Future {
    latch.countDown()
    (n , latch.getCount)
  }
}

// result will always be Future(List((1, 2L), (2, 1L), (3, 0L)))
```

### Combining futures

#### multiple futures into one future tuple
Combine up to 6 futures into a tuple with the value of each future using ```product```. If either fails the result
will be a failed future. Or apply a function using ```mapN``` or ```flatMapN``` while you are at it

Example:
```scala
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Future.successful
import markatta.futiles.Combining._

val fTuple: Future[(Int, String)] = product(successful(1), successful("woho"))

val mapped: Future[String] = map2(successful(1), successful("woho"))((a, b) => a.toString + b)

val flatMapped: Future[String] = flatMap2(successful(1), successful("woho"))((a, b) => successful(a.toString + b)) 
```


### Lifting and unlifting container types inside of futures

#### Lift the implicit try into the future
If you want to lift a failed future into a successful future with a failed try inside, you
can do that with ```markatta.futiles.Lifting.liftTry```. This is probably not very interesting
except for in the context of ```sequenceTries``` which is described above.


#### Options
A common problem when working with futures is that you have a nested option, and this
makes it impossible to do clean for comprehensions, since you need to handle both ```Some(value)```
and ```None``` somehow.

```scala
val result =
  for {
    user <- userDao.findById(id): Future[Option[User]]
    cart <- shoppingCartDao.findByUserId(...oh noes, user is an option...)
  } yield (...oh noes, so is cart, what to do!? ...)
```

One easy way out is to "unlift" the value if it is a ```None``` into a failed future, but using
unapply in the for comprehension gives us a ```MatchError``` with little info about what went wrong.
```scala
val result =
  for {
    Some(user) <- userDao.findById(id): Future[Option[User]]
    Some(cart) <- shoppingCartDao.findByUserId(user.id)
  } yield (user, cart)
// hard to know that it wasn't some other MatchError here
```

Futiles contains two methods ```Lifting.unliftOption``` and ```Lifting.unliftOptionEx```
for dealing with this, one where you decide what exception to fail the future with and
one where you just provide a text, and an ```UnliftException``` is used.

To make it really concise an implicit class is provided that
 will decorate ```Future[Option[A]]``` with corresponding methods called ```unlift```

**Example:**
```scala
import scala.concurrent.ExecutionContext.Implicits.global
import markatta.futiles.UnliftException
import markatta.futiles.Lifting.Implicits._

val result =
  for {
    user <- userDao.findById(id).unlift(s"No user with $id")
    cart <- shoppingCartDao.findByUserId(user.id).unlift(s"No cart for user $user")
  } yield (user, cart)

result.recover {
  case UnliftException(msg) if msg.startsWith("No user") => ...
  case UnliftException(msg) if msg.startsWith("No cart") => ...
}
```

#### Eithers
Much like the option unlifting above ```unliftL``` and ```unliftLEx``` will fail the future if the value is a ```Right``` and succeed
with the value inside of a ```Left``` while ```unliftR``` and ```unliftREx``` does the exact opposite.

There is also implicit decoration for the two:
```scala
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import markatta.futiles.Lifting.Implicits._

val futureEither: Future[Either[String, Int]] = ???
val result: Future[Int] = futureEither.unliftR("Danger Danger!")
```

### Timeouts
Some times you want to wait a specific amount of time before triggering a future, or you want
it to complete at a specific timeout with a value you already have. This is available for example
in the play framework future libraries, but maybe you would want to do that without depending
on play.

If an exception is thrown by the by-name-parameter the future will be failed instead of
completed when the timeout is reached.

**Example:**
```scala
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import markatta.futiles.Timeouts.timeout

val timeoutF = timeout(1.seconds)("Oh noes, it was too slow, you get default instead")
val result = Future.firstCompletedOf(Seq(doItForReals(), timeoutF))
```

### Retrying failed futures
A common scenario is that you use Futures to interact with remote systems, but what if the
remote system is down exactly when the request is done, or the network cable was disconnected
by your little brother.

Futiles contains two flavours of retry for futures:

* Retry right away with ```markatta.futiles.Retry.retry```
* Retry with an exponential back off, waiting longer and longer before each retry
 
Both methods allows you to specify the maximum number of retries to perform and a predicate function
```Throwable => Boolean``` that will be given any exception and decides if it should lead to a retry or not.
 
By default all throwables lead to retry.

**Example of the simple retry**
```scala
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import markatta.futiles.Retry._

val result: Future[Int] = retry(5) {
  callThatService(): Future[Int]
}
```


Exponential back off additionally takes a back off time unit, which decides a base for the calculation
```max(try * 2 ^ time_unit, 1) * jitter```

**Example with exponential back off**
```scala
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import markatta.futiles.Retry._

val result: Future[Int] = retryWithBackOff(5, 5.seconds) {
  callThatService(): Future[Int]
}
```
