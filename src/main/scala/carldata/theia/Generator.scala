package carldata.theia

/**
  * Generators run in its own tread and generates data with the constant time delta.
  */
trait Generator[T] {

  /** Write data generated to the given output */
  def to(w: T => Unit): Unit
}
