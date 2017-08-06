package carldata.theia

import java.util.Properties
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.apache.kafka.streams.kstream.{KStream, KStreamBuilder}

object Main {

  private val logger = Logger.getLogger("Hydra")

  case class Params(kafkaBroker: String, prefix: String)

  def buildConfig(params: Params): Properties = {
    val p = new Properties()
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, "hydra")
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, params.kafkaBroker)
    p.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass)
    p.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass)
    p
  }

  def parseArgs(args: Array[String]): Params = {
    Params(kafkaBroker="localhost:9092", prefix="")
  }

  def main(args: Array[String]): Unit = {
    val params = parseArgs(args)
    val config = buildConfig(params)
    val builder: KStreamBuilder = new KStreamBuilder()
    val dataStream: KStream[String, String] = builder.stream("Data")
    dataStream.foreach((_, value) => println(value))

    val streams: KafkaStreams = new KafkaStreams(builder, config)
    streams.start()

    Runtime.getRuntime.addShutdownHook(new Thread(() => {
      streams.close(10, TimeUnit.SECONDS)
    }))
  }
}
