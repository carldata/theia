package carldata.theia.actor

import java.io.File

import akka.actor.{Actor, ActorRef, Props}
import carldata.hs.RealTime.RealTimeJsonProtocol._
import carldata.hs.RealTime.{AddAction, RealTimeJobRecord}
import carldata.theia.actor.Messages.{KMessage, Tick}
import spray.json._

import scala.io.Source


/** Generate jobs which should run in real time. */
object RTJobGen {
  def props(sinkActor: ActorRef): Props = Props(new RTJobGen(sinkActor))
}

class RTJobGen(sinkActor: ActorRef) extends Actor {

  val jobs: Seq[RealTimeJobRecord] = {
    new File("config")
      .listFiles
      .filter(x => x.isFile && x.getName.endsWith(".rt"))
      .flatMap(f => parseConfig(f).toList)
  }

  def receive: Actor.Receive = {

    case Tick =>
      jobs.foreach { j =>
        sinkActor ! KMessage("theia", List(j.toJson.compactPrint))
      }

  }

  def parseConfig(f: File): Option[RealTimeJobRecord] = {
    val lines = Source.fromFile(f).getLines().filter(!_.startsWith("#")).toList
    val calculationId = "theia-1"
    val inputChannels = Seq(lines.headOption.getOrElse(""))
    val output = lines.drop(1).headOption.getOrElse("")
    val script = lines.drop(2).mkString("\n")
    Some(RealTimeJobRecord(AddAction, calculationId, script, inputChannels, output))
  }
}