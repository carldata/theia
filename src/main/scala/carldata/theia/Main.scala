package carldata.theia

import akka.actor.ActorSystem
import carldata.theia.topology.Config.JsonDataGen
import carldata.theia.utils.StatsD
import org.slf4j.LoggerFactory

object Main {

  private val logger = LoggerFactory.getLogger(Main.getClass)

  case class Params(kafkaBrokers: String, statsDHost: String)

  def stringArg(args: Array[String], key: String, default: String): String = {
    val name = "--" + key + "="
    args.find(_.contains(name)).map(_.substring(name.length)).getOrElse(default).trim
  }

  /** Command line parser */
  def parseArgs(args: Array[String]): Params = {
    val kafka = stringArg(args, "kafka", "localhost:9092")
    val statsDHost = stringArg(args, "statsDHost", "none")
    Params(kafka, statsDHost)
  }

  def buildTopology(): Unit = {
    val channelDataGen = new JsonDataGen("{}")
  }

  /** Main application. Creates topology and runs generators */
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("Theia")
    val params = parseArgs(args)
    StatsD.init("theia", params.statsDHost)
    logger.info("Application started")
  }

}
