package carldata.theia.actor

import java.time.LocalDateTime

import akka.actor.{Actor, ActorRef, Props}
import carldata.hs.DeleteData.DeleteDataJsonProtocol._
import carldata.hs.DeleteData.DeleteDataRecord
import carldata.theia.actor.Messages.{KMessage, Tick}
import spray.json._


object DeleteJobGen {
  def props(sinkActor: ActorRef): Props = Props(new DeleteJobGen(sinkActor))
}

class DeleteJobGen(sinkActor: ActorRef) extends Actor {

  def mkJob(): DeleteDataRecord = {
    val now = LocalDateTime.now()
    val startDate = now.minusMinutes(5)
    val endDate = now.minusMinutes(4)
    DeleteDataRecord("theia", "theia-in-1", startDate, endDate)
  }

  def receive: Actor.Receive = {
    case Tick =>
      val jobMsg = mkJob().toJson.compactPrint
      sinkActor ! KMessage("theia", Seq(jobMsg))
  }

}