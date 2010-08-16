package tacet.rest

import net.liftweb.http._
import net.liftweb.http.rest._
import net.liftweb.common._
import net.liftweb.util.Helpers._
import tacet.model._
import net.liftweb.json.JsonAST.JArray


object RestReports extends RestHelper {
  override def suplimentalJsonResponse_?(req: Req) = true

//  serve {
//    case JsonGet("api" :: "list" :: Nil, _) =>
//      Full(JArray(Measures.map(Measure.toJson).toList))
//
//    case JsonGet("api" :: "kinds" :: kind :: Nil, _) =>
//      val ofKind =
//        Measure.where{ Measure.kind is kind } in Measures
//      Full(JArray(ofKind.map(Measure.toJson).toList))
//
//    case JsonGet("api" :: "tags" :: tags, _) =>
//      val withTags =
//        Measure.where{ Measure.tags hasAll tags } in Measures
//      Full(JArray(withTags.map(Measure.toJson).toList))
//
//  }
}

