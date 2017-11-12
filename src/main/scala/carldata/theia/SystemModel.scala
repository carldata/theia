package carldata.theia


/**
  * Model system and build test infrastructure based on this model
  */
object SystemModel {

  trait Sink

  trait Probe

  trait DataGen {
    def next(): String
  }

  case class System(generators: Seq[Sink], probes: Seq[Probe])

  /**
    * @param topic     - Data will send to this topic
    * @param dataGen   - Data generator
    * @param deltaTime - Time in milliseconds between messages
    */
  case class KafkaSink(topic: String, dataGen: DataGen, deltaTime: Long) extends Sink

  class JsonDataGen(spec: String) extends DataGen {
    override def next(): String = spec
  }
}
