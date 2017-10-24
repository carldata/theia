package carldata.theia

import akka.actor.{ActorRef, ActorSystem}
import carldata.theia.actor.Messages.Tick
import carldata.theia.actor.{BatchJobGen, DataGen, KafkaSink}
import com.timgroup.statsd.{NonBlockingStatsDClient, StatsDClient}
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Random

object Main {

  private val logger = LoggerFactory.getLogger(Main.getClass)

  case class Params(kafkaBroker: String, prefix: String, eventsPerSecond: Int, channels: Int, statSDHost: String)

  val system: ActorSystem = ActorSystem("Theia")

  /** Command line parser */
  def parseArgs(args: Array[String]): Params = {
    val kafka = args.find(_.contains("--kafka=")).map(_.substring(8)).getOrElse("localhost:9092")
    val prefix = args.find(_.contains("--prefix=")).map(_.substring(9)).getOrElse("")
    val eventsPerSecond = args.find(_.contains("--eps=")).map(_.substring(6)).getOrElse("1").trim().toInt
    val channels = args.find(_.contains("--channels=")).map(_.substring(11)).getOrElse("1").trim().toInt
    val statSDHost = args.find(_.contains("--statSDHost=")).map(_.substring(13)).getOrElse("none")
    Params(kafka, prefix, if (eventsPerSecond <= 0) 1 else eventsPerSecond, if (channels <= 0) 1 else channels, statSDHost)
  }

  def initStatsD(host: String): Option[StatsDClient] = {
    if (host == "none") None
    else Some( new NonBlockingStatsDClient("theia", host, 8125 ))
  }

  /** Main application. Creates topology and runs generators */
  def main(args: Array[String]): Unit = {
    val params = parseArgs(args)
    val statsDCClient = initStatsD(params.statSDHost)
    // Kafka sinks
    val dataSink = system.actorOf(KafkaSink.props(params.prefix + "data", params.kafkaBroker, statsDCClient), "data-sink")
    val batchSink = system.actorOf(KafkaSink.props(params.prefix + "hydra-batch", params.kafkaBroker, statsDCClient), "batch-sink")

    // Data generators
    for (i <- 1.to(params.channels)) yield mkDataGen(i, dataSink, params.eventsPerSecond)
    val batchGen = system.actorOf(BatchJobGen.props(batchSink), s"batch-job-gen")
    // Generate batch job every 10 seconds
    system.scheduler.schedule(0.millis, 10.second, batchGen, Tick)

    logger.info("Application started")
  }

  /** Create Data Generator Actor */
  def mkDataGen(id: Int, dataSink: ActorRef, eps: Int): ActorRef = {
    val channelId = s"theia-in-$id"
    val actor = system.actorOf(DataGen.props(channelId, dataSink, eps), s"data-gen-$id")
    logger.info(s"Create channel $channelId with throughput $eps eps")
    // Send data every 1 second
    val startTime = Random.nextInt(1000)
    val resolution = 1
    system.scheduler.schedule(startTime.milliseconds, resolution.second, actor, Tick)
    actor
  }
}
