package carldata.theia.actor

import akka.actor.{Actor, ActorRef, Props}
import carldata.hs.RealTime.RealTimeJsonProtocol._
import carldata.hs.RealTime.{AddAction, RealTimeJobRecord, RemoveAction}
import carldata.theia.actor.Messages.{KMessage, Tick}
import spray.json._


/** Generate jobs which should run in real time. */
object RTJobGen {
  def props(sinkActor: ActorRef): Props = Props(new RTJobGen(sinkActor))
}

class RTJobGen(sinkActor: ActorRef) extends Actor {

  val script: String =
    """
      |def f(a: Number): Number = 2*a
      |def main(xs: TimeSeries): TimeSeries = map(xs, f)
    """.stripMargin.trim

  val jobs: Seq[RealTimeJobRecord] = {
    val calculationId = "theia-1"
    val inputChannels = Seq("theia-in-1")
    val output = "theia-out-rt-1"
    List(RealTimeJobRecord(RemoveAction, calculationId, script, inputChannels, output),
      RealTimeJobRecord(AddAction, calculationId, script, inputChannels, output))
  }

  def receive: Actor.Receive = {

    case Tick =>
      sinkActor ! KMessage("theia", jobs.map(_.toJson.compactPrint))
  }

}