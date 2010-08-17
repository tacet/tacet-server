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
import net.liftweb.json.JsonAST.{JInt, JValue}

object RestMeasurements extends RestHelper {
  override def suplimentalJsonResponse_?(req: Req) = true

//  implicit val WebFormats = new DefaultFormats {
//    override val dateFormat = new DateFormat {
//      def parse(s: String) =
//        (for{
//          l <- tryo {s.toLong}
//        } yield new Date(l)).toOption
//
//      def format(d: Date) = "" + d.getTime
//    }
//  }

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
          children: List[Measurement]) {
  }

  case class Measurement(
          kind: String,
          name: String,
          value: Double,
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

    case JsonGet("api" :: "example" :: Nil, _) =>
      Full(Extraction.decompose(
        RootNode("server", now, List(
          Measurement("java", "Hello.world", 10.3, List("a", "b"), Map("a" -> "b"), List(
            Measurement("java", "system.out.println", 2.0, List("a", "b"), Map("a" -> "b"), Nil)
            ))))))

    case JsonGet("api" :: "cpu" :: name :: Nil, _) =>
      Generator ! Generate(1.minute.later, 1 second, () => Measure.save(Measure("lift", "cpu", name, Random.nextDouble * 100, Nil, now, Nil)))
      NoContentResponse()

    case JsonDelete("api" :: "db" :: Nil, _) =>
      MongoDB.db.dropDatabase
      NoContentResponse()
  }
}