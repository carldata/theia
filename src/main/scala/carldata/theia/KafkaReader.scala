package carldata.theia

import java.util.concurrent.Executors
import java.util.{Collections, Properties}

import akka.actor.ActorRef
import carldata.theia.actor.Messages.KMessage
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord, KafkaConsumer}

import scala.collection.JavaConverters._
import scala.concurrent.duration._

/**
  * Read data from Kafka topic
  */
class KafkaReader(val brokers: String, val topic: String, val actor: ActorRef) {

  val consumer = new KafkaConsumer[String, String](initProps)
  var isRunning = true

  def shutdown(): Unit = {
    isRunning = false
  }

  def initProps: Properties = {
    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "theia")
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000")
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000")
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props
  }

  def start(): Unit = {
    consumer.subscribe(Collections.singletonList(topic))

    Executors.newSingleThreadExecutor.execute(() => {
      while (isRunning) {
        val records = consumer.poll(1.second.toMillis).asScala.map(r => r.value()).toSeq
        val msg = KMessage("", records)
        actor ! msg
      }
      consumer.close()
    })
  }
}