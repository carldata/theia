package carldata.theia.actor

/** Messages send between actors */
object Messages {
  /** Send by timer */
  case object Tick
  /** Kafka message */
  case class KMessage(key: String, value: String)

}
