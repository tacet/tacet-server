package tacet.model

import com.osinka.mongodb._
import com.osinka.mongodb.shape._
import java.util.Date
import com.mongodb.DBObject

case class Measure(
        source: String,
        kind: String,
        name: String,
        value: Long,
        tags: List[String],
        date: Date,
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
  def children = arrayRef("children", Collection, _.children)

  override def * = List(source, kind, name, value, tags, date, children)

  override def factory(dbo: DBObject) =
    for{
      s <- source from dbo
      k <- kind from dbo
      n <- name from dbo
      v <- value from dbo
      t <- tags from dbo
      d <- date from dbo
      c <- children from dbo
    } yield Measure(s, k, n, v, t.toList, d, c.toList)

  def save(measure:Measure){
    measure.children.foreach(save)
    Collection += measure
  }
}