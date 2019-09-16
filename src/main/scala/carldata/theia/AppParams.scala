package carldata.theia

case class AppParams(kafkaBroker: String, prefix: String, eventsPerSecond: Int, channels: Int
                     , elasticSearchUrl: String, elasticSearchPort: Int)

object AppParams {
  def stringArg(args: Array[String], key: String, default: String): String = {
    val name = "--" + key + "="
    args.find(_.contains(name)).map(_.substring(name.length)).getOrElse(default).trim
  }

  /** Command line parser */
  def parseArgs(args: Array[String]): AppParams = {
    val kafka = stringArg(args, "kafka", "localhost:9092")
    val prefix = stringArg(args, "prefix", "")
    val eventsPerSecond = stringArg(args, "eps", "1").toInt
    val channels = stringArg(args, "channels", "1").toInt
    val elasticSearchUrl = stringArg(args, "elasticSearchUrl", "localhost")
    val elasticSearchPort = stringArg(args, "elasticSearchPort", "9200").toInt
    AppParams(kafka, prefix, math.max(eventsPerSecond, 1), math.max(channels, 1)
      , elasticSearchUrl, elasticSearchPort)
  }
}