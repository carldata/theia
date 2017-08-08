package carldata.theia

import scala.concurrent.duration.FiniteDuration

/**
  * Generators run in its own tread and generates data with the constant time delta.
  */
abstract class Generator[T](resolution: FiniteDuration) {

  /** Run generator thread */
  def start(): Unit = {
    new Thread {
      override def run() {
        generate()
        Thread.sleep(resolution.toMillis)
      }
    }
  }

  /** Generate single data point */
  def generate(): Unit

  /** Write data generated to the given output */
//  def to(w: T => Unit): Unit
}


class HealthCheckGen(resolution: FiniteDuration) extends Generator[Int](resolution){

  override def generate(): Unit = {
    println(System.currentTimeMillis())
  }
}