package carldata.theia.actor

import java.util.Properties

import akka.actor.{Actor, Props}
import carldata.theia.actor.Messages.KMessage
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.slf4j.LoggerFactory


object KafkaSink {
  private val logger = LoggerFactory.getLogger(KafkaSink.getClass)

  def props(topic: String, broker: String): Props = Props(new KafkaSink(topic, broker))

  def initProps(brokers: String): Properties = {
    val props = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    props
  }
}

class KafkaSink(topic: String, brokers: String) extends Actor {

  import KafkaSink._

  logger.info("Create KafkaSink on topic: " + topic)
  val producer = new KafkaProducer[String, String](initProps(brokers))

  def receive: Actor.Receive = {
    case KMessage(k, v) =>
      v.map(recVal => {
        val data = new ProducerRecord[String, String](topic, k, recVal)
        producer.send(data)
      })
  }

  override def postStop(): Unit = {
    logger.info("close sink: " + topic)
    producer.close()
  }
}
