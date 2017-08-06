package carldata.theia.formats

import java.time.LocalDateTime

/**
  * Data object is send to Kafka as a time series data point.
  */
class Data(channel: String, ts: LocalDateTime, value: Float) {

}
