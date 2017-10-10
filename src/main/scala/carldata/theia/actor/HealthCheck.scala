package carldata.theia.actor

import java.util.logging.Logger

import akka.actor.{Actor, ActorRef, Props}
import carldata.theia.actor.Messages.{KMessage, Tick}


/** HealthCheck generates event */
object HealthCheck {
  def props(sinkActor: ActorRef): Props = Props(new HealthCheck(sinkActor))
}

class HealthCheck(sinkActor: ActorRef) extends Actor {

  private val logger = Logger.getLogger("Theia")
  var waitMessage: Long = 0

  def receive: Actor.Receive = {

    case Tick =>
      val v = System.currentTimeMillis()
      if(waitMessage > 0) logger.warning("No response for HealthCheck message. Is Kafka alive?")
      waitMessage = v
      sinkActor ! KMessage("", v.toString)

    case KMessage(_, v) =>
      waitMessage = 0L
  }
}
