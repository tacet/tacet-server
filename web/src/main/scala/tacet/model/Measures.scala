package tacet.model

import com.osinka.mongodb._
import com.osinka.mongodb.shape._
import java.util.Date
import com.mongodb.DBObject

case class Measure(
        source: String,
        kind: String,
        name: String,
        value: Double,
        tags: List[String],
        date: Date,
        properties: Map[String, String],
        children: List[Measure]) extends MongoObject

object Measure extends MongoObjectShape[Measure] {
  import Field._

  def Collection = MongoDB.db.getCollection("measures") of Measure

  def source = scalar("source", _.source)
  def kind = scalar("kind", _.kind)
  def name = scalar("name", _.name)
  def value = scalar("value", _.value)
  def tags = array("tags", _.tags)
  def date = scalar("date", _.date)
  def properties = map("properties", _.properties)
  def children = arrayRef("children", Collection, _.children)

  override def * = List(source, kind, name, value, tags, date, properties, children)

  override def factory(dbo: DBObject) =
    for{
      s <- source from dbo
      k <- kind from dbo
      n <- name from dbo
      v <- value from dbo
      t <- tags from dbo
      d <- date from dbo
      p <- properties from dbo
      c <- children from dbo
    } yield Measure(s, k, n, v, t.toList, d, p, c.toList)

  def save(measure:Measure){
    measure.children.foreach(save)
    Collection += measure
  }
}