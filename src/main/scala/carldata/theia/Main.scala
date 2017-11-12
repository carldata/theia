package carldata.theia

import akka.actor.{ActorRef, ActorSystem}
import carldata.theia.actor.Messages.Tick
import carldata.theia.actor.{RealTimeJobGen, DataGen, KafkaSink}
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Random

object Main {

  private val logger = LoggerFactory.getLogger(Main.getClass)

  case class Params(kafkaBroker: String, prefix: String, eventsPerSecond: Int, channels: Int, statsDHost: String)

  val system: ActorSystem = ActorSystem("Theia")

  def stringArg(args: Array[String], key: String, default: String): String = {
    val name = "--" + key + "="
    args.find(_.contains(name)).map(_.substring(name.length)).getOrElse(default).trim
  }

  /** Command line parser */
  def parseArgs(args: Array[String]): Params = {
    val kafka = stringArg(args, "kafka", "localhost:9092")
    val prefix = stringArg(args, "prefix", "")
    val eventsPerSecond = stringArg(args, "eps", "1").toInt
    val channels = stringArg(args, "channels", "1").toInt
    val statsDHost = stringArg(args, "statsDHost", "none")
    Params(kafka, prefix, math.max(eventsPerSecond, 1), math.max(channels, 1), statsDHost)
  }

  /** Main application. Creates topology and runs generators */
  def main(args: Array[String]): Unit = {
    val params = parseArgs(args)
    StatsD.init("theia", params.statsDHost)
    // Kafka sinks
    val dataSink = system.actorOf(KafkaSink.props(params.prefix + "data", params.kafkaBroker), "data-sink")
    val realTimeSink = system.actorOf(KafkaSink.props(params.prefix + "hydra-rt", params.kafkaBroker), "real-time-sink")

    // Data generators
    for (i <- 1.to(params.channels)) yield mkDataGen(i, dataSink, params.eventsPerSecond)
    val rtjGen = system.actorOf(RealTimeJobGen.props(realTimeSink), "real-time-job-gen")
    // Generate real time job every 10 seconds
    system.scheduler.schedule(0.millis, 10.second, rtjGen, Tick)

    logger.info("Application started")
  }

  /** Create Data Generator Actor */
  def mkDataGen(id: Int, dataSink: ActorRef, eps: Int): ActorRef = {
    val channelId = s"theia-in-$id"
    val actor = system.actorOf(DataGen.props(channelId, dataSink, eps), s"data-gen-$id")
    logger.info(s"Create channel $channelId with $eps eps.")
    // Send data every 1 second
    val startTime = Random.nextInt(1000)
    val resolution = 1
    system.scheduler.schedule(startTime.milliseconds, resolution.second, actor, Tick)
    actor
  }
}
