package carldata.theia.actor

import java.util.Properties

import akka.actor.{Actor, Props}
import carldata.theia.actor.Messages.KMessage
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}


object KafkaSink {
  def props(topic: String, broker: String): Props = Props(new KafkaSink(topic, broker))

  def initProps(brokers: String): Properties = {
    val props = new Properties()
    props.put("bootstrap.servers", brokers)
    props.put("client.id", "Theia")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props
  }
}

class KafkaSink(topic: String, brokers: String) extends Actor {
  import KafkaSink._

  println("Create KafkaSink on topic: " + topic)
  val producer = new KafkaProducer[String, String](initProps(brokers))

  def receive: Actor.Receive = {
    case KMessage(k, v) =>
      val data = new ProducerRecord[String, String](topic, k, v)
      producer.send(data)
  }

  override def postStop(): Unit = {
    println("close sink: " + topic)
    producer.close()
  }
}
