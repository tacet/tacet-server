package tacet
package rest

import net.liftweb.util.Helpers._
import net.liftweb.http.rest.RestHelper
import net.liftweb.http.{NoContentResponse, Req}
import java.util.Date
import net.liftweb.json.{DefaultFormats, DateFormat}
import net.liftweb.json.JsonAST.{JValue}
import model._
import util.Random

object RestMeasurements extends RestHelper {
  override def suplimentalJsonResponse_?(req: Req) = true

  implicit val WebFormats = new DefaultFormats {
    override val dateFormat = new DateFormat {
      def parse(s: String) =
        (for{
          l <- tryo {s.toLong}
        } yield new Date(l)).toOption

      def format(d: Date) = "" + d.getTime
    }
  }

  object RootNode {
    def from(json: JValue) = tryo {json.extract[RootNode]}
  }

  case class RootNode(
          source: String,
          date: Date,
          children: List[Measurement]) {
  }

  case class Measurement(
          kind: String,
          name: String,
          value: Long,
          tags: List[String],
          properties: Map[String, String],
          children: List[Measurement]) {
  }

  def measures(root: RootNode) = {
    def measure(m: Measurement): Measure =
      Measure(root.source, m.kind, m.name, m.value, m.tags, root.date, m.children.map(measure))
    root.children.map(measure)
  }

  serve {
    case JsonPut("api" :: "measurements" :: Nil, json -> _) =>
      for{
        root <- RootNode from json
      } yield {
        measures(root).foreach(Measure.save)
        NoContentResponse()
      }

    case JsonGet("api" :: "cpu" :: Nil, _) =>
      Generator ! Generate(1.minute.later, 1 second, () => Measure.save(Measure("lift", "cpu", "cpu1", Random.nextInt(100), Nil, now, Nil)))
      NoContentResponse()
  }
}