package carldata.theia.topology

/**
  * Model system and build test infrastructure based on this model
  */
object Config {

  trait DataGen {
    def next(): String
  }

  class JsonDataGen(spec: String) extends DataGen {
    override def next(): String = spec
  }
}
