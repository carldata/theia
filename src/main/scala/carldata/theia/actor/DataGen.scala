package carldata.theia.actor

import java.time.LocalDateTime

import akka.actor.{Actor, ActorRef, Props}
import carldata.hs.Data.DataJsonProtocol._
import carldata.hs.Data.DataRecord
import carldata.theia.actor.Messages.{KMessage, Tick}
import spray.json._


/** Generate data events */
object DataGen {
  def props(channelId: String, sinkActor: ActorRef, eps: Int): Props =
    Props(new DataGen(channelId, sinkActor,eps))
}

class DataGen(channelId: String, sinkActor: ActorRef, eps: Int) extends Actor {

  def receive: Actor.Receive = {
    case Tick =>
      val res = (1 to eps).map(i => {
        val r = DataRecord(channelId, LocalDateTime.now().plusNanos(1000000 * i), i)
        r.toJson.compactPrint
      })
      sinkActor ! KMessage(channelId, res)
  }
}