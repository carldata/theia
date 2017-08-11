package carldata.theia

import java.util.logging.Logger

import akka.actor.{ActorRef, ActorSystem}
import carldata.theia.actor.Messages.Tick
import carldata.theia.actor.{DataGen, HealthCheck, KafkaSink}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.io.StdIn



object Main {

  private val logger = Logger.getLogger("Hydra")
  case class Params(kafkaBroker: String, prefix: String)
  val system: ActorSystem = ActorSystem("Theia")

  /** Command line parser */
  def parseArgs(args: Array[String]): Params = {
    val kafka = args.find(_.contains("--kafka=")).map(_.substring(8)).getOrElse("localhost:9092")
    val prefix = args.find(_.contains("--prefix=")).map(_.substring(9)).getOrElse("")
    Params(kafka, prefix)
  }

  /** Main application. Creates topology and runs generators */
  def main(args: Array[String]): Unit = {
    val params = parseArgs(args)
    try {
      // Create actors
      val theiaSink: ActorRef = system.actorOf(KafkaSink.props("theia", params.kafkaBroker), "health-check-sink")
      val dataSink: ActorRef = system.actorOf(KafkaSink.props("data", params.kafkaBroker), "data-sink")
      val healthCheck: ActorRef = system.actorOf(HealthCheck.props(theiaSink), "health-check-gen")
      val dataGen: ActorRef = system.actorOf(DataGen.props(dataSink), "data-gen")
      val theiaBolt = new KafkaReader(params.kafkaBroker, "theia", healthCheck)

      // Start Kafka listeners
      theiaBolt.start()

      // Check Health every 5 seconds
      system.scheduler.schedule(0.milliseconds, 5.second, healthCheck, Tick)
      // Send data every 1 second
      system.scheduler.schedule(0.milliseconds, 1.second, dataGen, Tick)

      println(">>> Press ENTER to exit <<<")
      StdIn.readLine()
      theiaBolt.shutdown()
    } finally {
      system.terminate()
    }
  }
}
