package carldata.theia.actor

import akka.actor.{Actor, ActorRef, Props}


object KafkaSink {
  def props(topic: String): Props = Props(new KafkaSink(topic))
  case class Message(key: String, value: String)
}

class KafkaSink(topic: String) extends Actor {
  import KafkaSink._

  def receive: Actor.Receive = {
    case Message(k, v) =>
      println(s"Send to topic $topic, data ($k -> $v)")
  }
}
