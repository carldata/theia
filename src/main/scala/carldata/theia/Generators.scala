package carldata.theia

import java.time.LocalDateTime

import carldata.hs.Data.DataJsonProtocol._
import carldata.hs.Data.DataRecord
import carldata.hs.DeleteData.DeleteDataRecord
import carldata.hs.RealTime.AddRealTimeJob
import spray.json._


/**
  * Data generators
  */
object Generators {

  def channelDataGen(channelId: String)(): String = {
    val rec = DataRecord(channelId, LocalDateTime.now(), 1)
    rec.toJson.compactPrint
  }

  def realTimeJobGen(channelIn: String, channelOut: String)(): String = {
    val script =
      """
        |def f(a: Number): Number = a+1
        |def main(xs: TimeSeries): TimeSeries = map(xs, f)
      """.stripMargin.trim

    val now = LocalDateTime.now()
    val startDate = now.minusMinutes(2)
    val endDate = now.minusMinutes(1)
    val rec = AddRealTimeJob(now.toString, script, Seq(channelIn), channelOut, startDate, endDate)
    rec.toJson.compactPrint
  }

  def deleteDataJobGen(channel: String): String = {
    val now = LocalDateTime.now()
    val startDate = now.minusMinutes(5)
    val endDate = now.minusMinutes(4)
    val rec = DeleteDataRecord("theia", channel, startDate, endDate)
    rec.toJson.compactPrint
  }



}
