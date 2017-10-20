package carldata.theia.actor

import java.util.Properties
import java.util.logging.Logger

import akka.actor.{Actor, Props}
import carldata.theia.actor.Messages.KMessage
import com.timgroup.statsd.{NonBlockingStatsDClient, StatsDClient}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}


object KafkaSink {
  private val logger = Logger.getLogger("Theia")

  def props(topic: String, broker: String, statSDHost: String): Props = Props(new KafkaSink(topic, broker, statSDHost))

  def initProps(brokers: String): Properties = {
    val props = new Properties()
    props.put("bootstrap.servers", brokers)
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props
  }
}

class KafkaSink(topic: String, brokers: String, statSDHost: String) extends Actor {

  import KafkaSink._

  private val statsDCClient: StatsDClient = new NonBlockingStatsDClient(
    "theia",
    statSDHost,
    8125
  )

  logger.info("Create KafkaSink on topic: " + topic)
  val producer = new KafkaProducer[String, String](initProps(brokers))

  def receive: Actor.Receive = {
    case KMessage(k, v) =>
      v.map(recVal => {
        val data = new ProducerRecord[String, String](topic, k, recVal)
        statsDCClient.incrementCounter("events.sent")
        producer.send(data)
      })
  }

  override def postStop(): Unit = {
    println("close sink: " + topic)
    producer.close()
    statsDCClient.stop()
  }
}
