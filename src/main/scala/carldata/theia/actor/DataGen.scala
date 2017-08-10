package carldata.theia.actor

import java.time.LocalDateTime

import akka.actor.{Actor, ActorRef, Props}
import carldata.hs.Data.DataJsonProtocol._
import carldata.hs.Data.DataRecord
import carldata.theia.actor.Messages.{KMessage, Tick}
import spray.json._


/** Generate data events */
object DataGen {
  def props(sinkActor: ActorRef): Props = Props(new DataGen(sinkActor))
}

class DataGen(sinkActor: ActorRef) extends Actor {

  def receive: Actor.Receive = {

    case Tick =>
      val r = DataRecord("theia-in-1", LocalDateTime.now(), 1f)
      sinkActor ! KMessage(r.channel, r.toJson.compactPrint)

  }
}