package carldata.theia

import scala.concurrent.duration.FiniteDuration

/**
  * Generators run in its own tread and generates data with the constant time delta.
  */
abstract class Generator(resolution: FiniteDuration) {

  type Message = String
  type Listener = Message => Unit

  protected var listeners: List[Listener] = List()

  /** Run generator thread */
  def start(): Unit = {
    val thread = new Thread {
      override def run() {
        while(true) {
          generate()
          Thread.sleep(resolution.toMillis)
        }
      }
    }
    thread.start()
  }

  /** Generate single data point */
  def generate(): Unit

  /** Write data generated to the given output */
  def to(w: Listener): Unit = {
    listeners = w :: listeners
  }

  /** Confirm message */
  def confirm(msg: Message): Unit
}


class HealthCheckGen(resolution: FiniteDuration) extends Generator(resolution){

  override def generate(): Unit = {
    val msg = System.currentTimeMillis()
    listeners.foreach(f => f(msg))
  }

  override def confirm(msg: String): Unit = {
    println("confirmed: " + msg)
  }
}