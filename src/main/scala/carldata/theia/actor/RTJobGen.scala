package carldata.theia.actor

import java.io.File

import akka.actor.{Actor, ActorRef, Props}
import carldata.hs.RealTime.RealTimeJsonProtocol._
import carldata.hs.RealTime.{AddAction, RealTimeRecord}
import carldata.theia.actor.Messages.{KMessage, Tick}
import spray.json._

import scala.io.Source


/** Generate jobs which should run in real time. */
object RTJobGen {
  def props(sinkActor: ActorRef): Props = Props(new RTJobGen(sinkActor))
}

class RTJobGen(sinkActor: ActorRef) extends Actor {

  val jobs: Seq[RealTimeRecord] = {
    new File("config")
      .listFiles
      .filter(x => x.isFile && x.getName.endsWith(".rt"))
      .flatMap(f => parseConfig(f).toList)
  }

  def receive: Actor.Receive = {

    case Tick =>
      jobs.foreach { j =>
        println("Send RealTime Job on channel " + j.trigger)
        sinkActor ! KMessage("theia", j.toJson.compactPrint)
      }

  }

  def parseConfig(f: File): Option[RealTimeRecord] = {
    val lines = Source.fromFile(f).getLines().filter(!_.startsWith("#")).toList
    val calculationId = "theia-1"
    val trigger = lines.headOption.getOrElse("")
    val output = lines.drop(1).headOption.getOrElse("")
    val script = lines.drop(2).mkString("\n")
    Some(RealTimeRecord(AddAction, calculationId, script, trigger, output))
  }
}