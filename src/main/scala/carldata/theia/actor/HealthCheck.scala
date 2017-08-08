package carldata.theia.actor

import akka.actor.{Actor, ActorRef, Props}
import carldata.theia.actor.KafkaSink.Message


/** HealthCheck generates event */
object HealthCheck {
  def props(printerActor: ActorRef): Props = Props(new HealthCheck(printerActor))
  final case class WhoToGreet(who: String)
  case object Tick
}

class HealthCheck(sinkActor: ActorRef) extends Actor {
  import HealthCheck._

  def receive: Actor.Receive = {
    case Tick =>
      val v = System.currentTimeMillis().toString
      sinkActor ! Message(v, v)
  }
}
