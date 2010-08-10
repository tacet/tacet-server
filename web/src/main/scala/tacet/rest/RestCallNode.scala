package tacet.rest

import net.liftweb.http._
import net.liftweb.http.rest._
import net.liftweb.common._
import net.liftweb.util.Helpers._
import tacet.model._
import net.liftweb.json.Extraction
import bootstrap.liftweb.Couch
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL._
import net.liftweb.couchdb.{QueryRow, View, CouchDB}
import dispatch.Http

object RestCallNode extends RestHelper {
  override def suplimentalJsonResponse_?(req: Req) = true

  serve {
    case JsonGet("api" :: "reset" :: Nil, _) =>
      Couch.drop
      Couch.init
      Couch.testData
      NoContentResponse()

    case JsonGet("api" :: "callnode" :: "single" :: node :: Nil, _) =>
      CallNode.fetch(node).map(_.asJValue)

    case JsonGet("api" :: "callnode" :: "names" :: Nil, _) =>
      for{
        names <- CallNode.names
      } yield JArray(names.map {case (name, num) => JField(name, num)})

    case JsonGet("api" :: "callnode" :: "by_name" :: name :: Nil, _) =>
      list(CallNode.by_name(name))

    case JsonGet("api" :: "callnode" :: "by_name_recursive" :: name :: Nil, _) =>
      list(CallNode.by_name_recursive(name))

    case JsonGet("api" :: "callnode" :: "by_time" :: Nil, _) =>
      DesignView("find", "by_time", x => x, q => q.value.map(v => JField(q.key.extract[String], v)))

    case JsonGet("api" :: "callnode" :: "by_time_recursive" :: Nil, _) =>
      DesignView("find", "by_time_recursive", x => x, q => q.value.map(v => JField(q.key.extract[String], v)))
  }

  def list(box:Box[Seq[CallNode]]) =
    for{
      seq <- box
    } yield JArray(seq.map(_.asJValue).toList)
}

object DesignView {
  def apply[T <: JValue](design:String, view:String, filter:View => View, project:QueryRow => Box[T]):Box[JArray] = {
    for{
      resultBox <- tryo(Http(filter(CouchDB.defaultDatabase.design(design).view(view)).query))
      result <- resultBox
    } yield JArray(result.rows.flatMap(r => project(r)).toList)
  }
}