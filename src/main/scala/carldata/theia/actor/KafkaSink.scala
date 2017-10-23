package carldata.theia.actor

import java.util.Properties
import java.util.logging.Logger

import akka.actor.{Actor, Props}
import carldata.theia.actor.Messages.KMessage
import com.timgroup.statsd.StatsDClient
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}


object KafkaSink {
  private val logger = Logger.getLogger("Theia")

  def props(topic: String, broker: String, statsDClient: Option[StatsDClient]): Props = Props(new KafkaSink(topic, broker, statsDClient))

  def initProps(brokers: String): Properties = {
    val props = new Properties()
    props.put("bootstrap.servers", brokers)
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props
  }
}

class KafkaSink(topic: String, brokers: String, statsDCClient: Option[StatsDClient]) extends Actor {

  import KafkaSink._


  logger.info("Create KafkaSink on topic: " + topic)
  val producer = new KafkaProducer[String, String](initProps(brokers))

  def receive: Actor.Receive = {
    case KMessage(k, v) =>
      v.map(recVal => {
        val data = new ProducerRecord[String, String](topic, k, recVal)
        if (statsDCClient.isDefined) statsDCClient.get.incrementCounter("events.sent")
        producer.send(data)
      })
  }

  override def postStop(): Unit = {
    println("close sink: " + topic)
    producer.close()
    if (statsDCClient.isDefined) statsDCClient.get.stop()
  }
}
