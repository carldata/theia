package carldata.theia

import java.util.logging.Logger

import akka.actor.{ActorRef, ActorSystem}
import carldata.theia.actor.Messages.Tick
import carldata.theia.actor.{DataGen, KafkaSink, RTJobGen}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.io.StdIn
import scala.util.Random


object Main {

  private val logger = Logger.getLogger("Theia")

  case class Params(kafkaBroker: String, prefix: String, eventsPerSecond: Int, statSDHost: String)

  val system: ActorSystem = ActorSystem("Theia")

  /** Command line parser */
  def parseArgs(args: Array[String]): Params = {
    val kafka = args.find(_.contains("--kafka=")).map(_.substring(8)).getOrElse("localhost:9092")
    val prefix = args.find(_.contains("--prefix=")).map(_.substring(9)).getOrElse("")
    val eventsPerSecond = args.find(_.contains("--eps=")).map(_.substring(6)).getOrElse("1").trim().toInt
    val statSDHost = args.find(_.contains("--statSDHost=")).map(_.substring(13)).getOrElse("localhost")
    Params(kafka, prefix, if (eventsPerSecond <= 0) 1 else eventsPerSecond, statSDHost)
  }

  /** Main application. Creates topology and runs generators */
  def main(args: Array[String]): Unit = {
    val params = parseArgs(args)
    // Kafka sink
    val dataSink: ActorRef = system.actorOf(KafkaSink.props(params.prefix + "data", params.kafkaBroker, params.statSDHost), "data-sink")
    val rtSink: ActorRef = system.actorOf(KafkaSink.props(params.prefix + "hydra-rt", params.kafkaBroker, params.statSDHost), "rt-sink")
    // Data generators
    //val rtJobGen: ActorRef = system.actorOf(RTJobGen.props(rtSink), "rtjob-gen")

    // Five device data generators
    for (i <- 1.to(10)) yield mkDataGen(i, dataSink, params.eventsPerSecond)

    // Send RealTime job after 5 second once
    //      system.scheduler.scheduleOnce(5.second, rtJobGen, Tick)

    println(">>> Press ENTER to exit <<<")
    StdIn.readLine()
  }

  /** Create Data Generator Actor */
  def mkDataGen(id: Int, dataSink: ActorRef, eps: Int): ActorRef = {
    val channelId = s"theia-in-$id"
    val actor = system.actorOf(DataGen.props(channelId, dataSink, eps), s"data-gen-$id")
    // Send data every 1 second
    val startTime = Random.nextInt(1000)
    val resolution = Random.nextInt(9) + 1
    system.scheduler.schedule(startTime.milliseconds, resolution.second, actor, Tick)
    actor
  }
}
