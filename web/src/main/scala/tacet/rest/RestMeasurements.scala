package tacet
package rest

import net.liftweb.util.Helpers._
import net.liftweb.http.rest.RestHelper
import net.liftweb.http.{NoContentResponse, Req}
import java.util.Date
import model._
import util.Random
import net.liftweb.common.Full
import net.liftweb.json._
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL._

object RestMeasurements extends RestHelper {
  override def suplimentalJsonResponse_?(req: Req) = true

  implicit val WebFormats = DefaultFormats + (new Serializer[Date]{
    val DateClass = classOf[Date]

    def deserialize(implicit format: Formats) = {
      case (TypeInfo(DateClass, _), JInt(num)) => new Date(num.toLong)
    }

    def serialize(implicit format: Formats) = {
      case date:Date => JInt(date.getTime)
    }
  })

  object RootNode {
    def from(json: JValue) = tryo {json.extract[RootNode]}
  }

  case class RootNode(
          source: String,
          date: Date,
          measurements: List[Measurement]) {
  }

  case class Measurement(
          kind: String,
          name: String,
          value: Double,
          tags: Option[List[String]],
          properties: Option[Map[String, String]],
          children: Option[List[Measurement]]) {
  }

  def measures(root: RootNode) = {
    def measure(m: Measurement): Measure =
      Measure(
        root.source,
        m.kind,
        m.name,
        m.value,
        m.tags getOrElse Nil,
        root.date,
        m.properties getOrElse Map.empty,
        m.children.getOrElse(Nil).map(measure))
    root.measurements.map(measure)
  }

  serve {
    case JsonPut("api" :: "measurements" :: Nil, json -> _) =>
      println(pretty(render(json)))
      for{
        root <- (RootNode from json) ~> 400 ?~ ("Not valid input") 
      } yield {
        measures(root).foreach(Measure.save)
        NoContentResponse()
      }

    case JsonGet("api" :: "example" :: Nil, _) =>
      Full(Extraction.decompose(
        RootNode("server", now, List(
          Measurement("java", "Hello.world", 10.3, Some(List("a", "b")), Some(Map("a" -> "b")), Some(List(
            Measurement("java", "system.out.println", 2.0, Some(List("a", "b")), Some(Map("a" -> "b")), None)
            )))))))

    case JsonGet("api" :: "cpu" :: name :: Nil, _) =>
      Generator ! Generate(1.minute.later, 1 second, () => Measure.save(Measure("lift", "cpu", name, Random.nextDouble * 100, Nil, now, Map.empty, Nil)))
      NoContentResponse()

    case JsonDelete("api" :: "db" :: Nil, _) =>
      MongoDB.db.dropDatabase
      NoContentResponse()
  }
}