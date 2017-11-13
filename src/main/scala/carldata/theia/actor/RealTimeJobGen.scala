package carldata.theia.actor

import java.time.LocalDateTime

import akka.actor.{Actor, ActorRef, Props}
import carldata.hs.RealTime.{AddRealTimeJob, RealTimeJob}
import carldata.hs.RealTime.RealTimeJsonProtocol._
import carldata.theia.actor.Messages.{KMessage, Tick}
import spray.json._


object RealTimeJobGen {
  def props(sinkActor: ActorRef): Props = Props(new RealTimeJobGen(sinkActor))
}

class RealTimeJobGen(sinkActor: ActorRef) extends Actor {

  val script: String =
    """
      |def f(a: Number): Number = a+1
      |def main(xs: TimeSeries): TimeSeries = map(xs, f)
    """.stripMargin.trim

  def mkJob(): RealTimeJob = {
    val now = LocalDateTime.now()
    val startDate = now.minusMinutes(2)
    val endDate = now.minusMinutes(1)
    AddRealTimeJob(now.toString, script, Seq("theia-in-1"), "theia-out-1", startDate, endDate)
  }

  def receive: Actor.Receive = {
    case Tick =>
      val jobMsg = mkJob().toJson.compactPrint
      sinkActor ! KMessage("theia", Seq(jobMsg))
  }

}