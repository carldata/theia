package carldata.theia

import java.time.{LocalDateTime, ZoneOffset}

import org.apache.http.HttpHost
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.{RequestOptions, RestClient, RestHighLevelClient}
import org.elasticsearch.common.xcontent.XContentType
import org.slf4j.LoggerFactory


class Elastic(app: String, elasticSearchUrl: String, elasticSearchPort: Int) {
  private val Log = LoggerFactory.getLogger("Elastic")

  val client = new RestHighLevelClient(RestClient.builder(
    new HttpHost(elasticSearchUrl, elasticSearchPort, "http")))

  def increment(m: String): Unit = increment(m, 1)

  def increment(m: String, i: Int): Unit = {
    val request = new IndexRequest(app)
    val jsonString =
      s"""
         |{
         | "postDate": "${LocalDateTime.now(ZoneOffset.UTC).toString}",
         | "$m" : $i
         |}
      """.stripMargin.stripLineEnd
    request.source(jsonString, XContentType.JSON)
    client.index(request, RequestOptions.DEFAULT)

  }

  def increment2(m: String, i: Int): Unit = {
    val request = new IndexRequest(app + "." + m)
    val jsonString =
      s"""
         |{
         | "postDate": "${LocalDateTime.now(ZoneOffset.UTC).toString}",
         | "message" : $i
         |}
      """.stripMargin.stripLineEnd
    request.source(jsonString, XContentType.JSON)
    client.index(request, RequestOptions.DEFAULT)

  }

}
