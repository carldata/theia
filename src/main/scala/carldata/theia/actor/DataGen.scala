package carldata.theia.actor

import java.time.LocalDateTime

import akka.actor.{Actor, ActorRef, Props}
import carldata.hs.Data.DataJsonProtocol._
import carldata.hs.Data.DataRecord
import carldata.theia.actor.Messages.{KMessage, Tick}
import spray.json._

import scala.util.Random


/** Generate data events */
object DataGen {
  def props(channelId: String, sinkActor: ActorRef): Props =
    Props(new DataGen(channelId, sinkActor))
}

class DataGen(channelId: String, sinkActor: ActorRef) extends Actor {

  def receive: Actor.Receive = {

    case Tick =>
      val v = Random.nextFloat() * 1000f
      val r = DataRecord(channelId, LocalDateTime.now(), v)
      sinkActor ! KMessage(r.channelId, r.toJson.compactPrint)

  }
}