package carldata.theia

import java.util.logging.Logger

import akka.actor.{ActorRef, ActorSystem}
import carldata.theia.actor.HealthCheck.Tick
import carldata.theia.actor.{HealthCheck, KafkaSink}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.io.StdIn



object Main {

  private val logger = Logger.getLogger("Hydra")
  case class Params(kafkaBroker: String, prefix: String)
  val system: ActorSystem = ActorSystem("Theia")

  /** Command line parser */
  def parseArgs(args: Array[String]): Params = {
    Params(kafkaBroker = "localhost:9092", prefix = "")
  }

  /** Main application. Creates topology and runs generators */
  def main(args: Array[String]): Unit = {
    val params = parseArgs(args)
    try {
      val theiaSink: ActorRef = system.actorOf(KafkaSink.props("theia", params.kafkaBroker), "health-check-sink")
      val healthCheck: ActorRef = system.actorOf(HealthCheck.props(theiaSink), "health-check-gen")

      // check Health every 5 seconds
      system.scheduler.schedule(0.milliseconds, 5.second, healthCheck, Tick)
      // Listen on theia topic
      val theiaBolt = new KafkaReader(params.kafkaBroker, "theia", healthCheck)
      theiaBolt.start()

      println(">>> Press ENTER to exit <<<")
      StdIn.readLine()
    } finally {
      system.terminate()
    }
  }
}
