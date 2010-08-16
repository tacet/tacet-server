package tacet.model

import org.specs._
import java.util.Date
import net.liftweb.common.Full
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL._
import tacet.rest._
import RestMeasurements._
import tacet.model._
import com.osinka.mongodb._

class MeasurementSpec extends Specification {

  "measures" should {

    val TestMeasures = MongoDB.db.getCollection("test_measures") of Measure
    doBefore(TestMeasures.drop)
    doLast(TestMeasures.drop)

    "save and retrieve simple" in {
      val simple = Measure("test", "a", "b", 1, Nil, new Date, Nil)
      TestMeasures += simple
      println(simple.mongoOID)
      TestMeasures.foreach(println)
    }
  }
}