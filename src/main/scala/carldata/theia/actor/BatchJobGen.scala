package carldata.theia.actor

import java.time.LocalDateTime

import akka.actor.{Actor, ActorRef, Props}
import carldata.hs.Batch.BatchRecord
import carldata.hs.Batch.BatchRecordJsonProtocol._
import carldata.theia.actor.Messages.{KMessage, Tick}
import spray.json._


object BatchJobGen {
  def props(sinkActor: ActorRef): Props = Props(new BatchJobGen(sinkActor))
}

class BatchJobGen(sinkActor: ActorRef) extends Actor {

  val script: String =
    """
      |def f(a: Number): Number = a+1
      |def main(xs: TimeSeries): TimeSeries = map(xs, f)
    """.stripMargin.trim

  def mkJob(): BatchRecord = {
    val now = LocalDateTime.now()
    val startDate = now.minusMinutes(2)
    val endDate = now.minusMinutes(1)
    BatchRecord(now.toString, script, Seq("theia-in-1"), "theia-out-1", startDate, endDate)
  }

  def receive: Actor.Receive = {
    case Tick =>
      val jobMsg = mkJob().toJson.compactPrint
      sinkActor ! KMessage("theia", Seq(jobMsg))
  }

}