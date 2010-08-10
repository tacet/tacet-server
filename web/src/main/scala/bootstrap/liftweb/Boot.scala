package bootstrap.liftweb

import net.liftweb.http.LiftRules
import tacet.rest.RestCallNode
import net.liftweb.couchdb._
import net.liftweb.util.Props
import net.liftweb.util.Helpers._
import tacet.model.CallNode
import dispatch.{:/, StatusCode, Http}
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL._

class Boot {
  def boot {
    LiftRules.addToPackages("tacet")
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    LiftRules.statelessDispatchTable.append(RestCallNode)

    if (Props.devMode) {
      Couch.drop
    }

    Couch.init

    if (Props.devMode) {
      Couch.testData
    }
  }
}

object Couch {
  val database = new Database(:/("127.0.0.1", 5984) <:< Map("Content-Type" -> "application/json"), "tacet")
  CouchDB.defaultDatabase = database

  def drop {
    try {
      Http(CouchDB.defaultDatabase.delete)
    } catch {
      case StatusCode(404, _) => ()
    }
  }

  def init {
    database.createIfNotCreated(Http)

    val count =
    ("language" -> "javascript") ~
            ("views" ->
                    ("by_name" ->
                            ("map" -> "function(doc) {function rec(d){emit(d.name, 1);d.subnodes.forEach(rec);};rec(doc);}") ~
                            ("reduce" -> """function(keys, values){ return sum(values); }""")))

    Http(database.design("count") put count)

    val find =
    ("language" -> "javascript") ~
            ("views" ->
                    ("by_name" ->
                            ("map" -> "function(doc){ emit(doc.name, doc);}")) ~
                    ("by_name_recursive" ->
                            ("map" -> "function(doc){ function rec(d){ emit(d.name, d); d.subnodes.forEach(rec);} rec(doc);}")) ~
                    ("by_time" ->
                            ("map" -> "function(doc){ emit(doc.stop - doc.start, doc); }")) ~
                    ("by_time_recursive" ->
                            ("map" -> "function(doc){ function rec(d){ emit(d.stop - d.start, d); d.subnodes.forEach(rec); }; rec(doc); }")))

    Http(database.design("find") put find)
  }

  def testData {
    val subs = List(
      CallNode.createRecord.name("cpu").start(1L).stop(2L),
      CallNode.createRecord.name("memory").start(3L).stop(4L)
      )

    CallNode.createRecord
            .name("memory")
            .start(9L)
            .stop(20L)
            .subnodes(subs)
            .properties(JObject(List(JField("value", 10))))
            .save

    CallNode.createRecord
            .name("memory")
            .start(20L)
            .stop(30L)
            .subnodes(subs)
            .properties(JObject(List(JField("value", 20))))
            .save

    CallNode.createRecord
            .name("cpu")
            .start(20L)
            .stop(30L)
            .subnodes(subs)
            .properties(JObject(List(JField("value", 70))))
            .save
  }
}