package carldata.theia.actor

import akka.actor.{Actor, ActorRef, Props}
import carldata.hs.RealTime.RealTimeJsonProtocol._
import carldata.hs.RealTime.{AddAction, RealTimeRecord}
import carldata.theia.actor.Messages.{KMessage, Tick}
import spray.json._


/** Generate jobs which should run in real time. */
object RTJobGen {
  def props(sinkActor: ActorRef): Props = Props(new RTJobGen(sinkActor))
}

class RTJobGen(sinkActor: ActorRef) extends Actor {

  /** Id script. Return the same value */
  val script: String =
    """
      |module Theia1
      |
      |def main(t: DateTime, v: Number): Number = v+1
    """.stripMargin

  def receive: Actor.Receive = {

    case Tick =>
      val r = RealTimeRecord(AddAction, "theia-1", script, "theia-in-1", "theia-out-1")
      println("Send RealTime Job on channel" + r.trigger)
      sinkActor ! KMessage("theia", r.toJson.compactPrint)

  }
}