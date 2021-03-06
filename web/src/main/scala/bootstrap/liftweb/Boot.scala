package bootstrap.liftweb

import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.http._
import js.jquery.JQuery14Artifacts
import net.liftweb.http.provider._
import net.liftweb.sitemap._
import net.liftweb.sitemap.Loc._
import Helpers._
import tacet.rest._
import tacet.model.MongoDB
import com.mongodb.Mongo
import net.liftweb.widgets.flot.Flot

class Boot {
  def boot {
    LiftRules.addToPackages("tacet")
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    LiftRules.statelessDispatchTable.append(RestMeasurements)
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    def menus = List(
      Menu("Home") / "index",
      Menu("Call") / "call")

    LiftRules.setSiteMapFunc(() => SiteMap(menus :_*))

    val _host = Props.get("mongo.host")
    val _port = Props.getInt("mongo.port")
    val _db = Props.get("mongo.db", "tacet")

    val db = ((_host, _port) match {
      case (Full(h), Full(p)) =>
        new Mongo(h, p)
      case (Full(h), Empty) =>
        new Mongo(h)
      case (Empty, Empty) =>
        new Mongo()
      case f =>
        error(f.toString)
    }).getDB(_db)

    MongoDB.db = db

    Flot.init
  }
}

