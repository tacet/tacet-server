package tacet.model

import net.liftweb.couchdb._
import net.liftweb.record.field._
import net.liftweb.common._
import net.liftweb.util.Helpers._
import net.liftweb.json.DefaultFormats
import net.liftweb.record.Field
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL._

object CallNode extends CallNode with CouchMetaRecord[CallNode] {
  implicit val formats = DefaultFormats

  def createRecord = new CallNode

  def names: Box[List[(String, Int)]] = for{
    resultBox <- tryo(http(database.design("count").view("by_name").group.query))
    result <- resultBox
  } yield {
      for{
        row <- result.rows.toList
        value <- row.value
        val key = row.key
      } yield (key.extract[String] -> value.extract[Int])
    }

  def by_name(name:String) =
    queryView("find", "by_name", _.key(JString(name)))

  def by_name_recursive(name:String) =
    queryView("find", "by_name_recursive", _.key(JString(name)))
}

class CallNode extends CouchRecord[CallNode] {
  def meta = CallNode

  object name extends StringField(this, 1000)
  object subnodes extends JSONSubRecordArrayField(this, CallNode)
  object start extends LongField(this) {
    override def defaultValue = -1L
  }
  object stop extends LongField(this) {
    override def defaultValue = -1L
  }

  object properties extends JSONBasicField(this)
}