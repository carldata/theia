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
  def props(sinkActor: ActorRef): Props = Props(new DataGen(sinkActor))
}

class DataGen(sinkActor: ActorRef) extends Actor {

  def receive: Actor.Receive = {

    case Tick =>
      val id = Random.nextInt(5)
      val r = DataRecord(s"theia-in-$id", LocalDateTime.now(), 1f)
      sinkActor ! KMessage(r.channel, r.toJson.compactPrint)

  }
}