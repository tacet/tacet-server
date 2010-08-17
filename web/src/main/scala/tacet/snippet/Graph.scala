package tacet.snippet

import xml.NodeSeq
import tacet.model.Measure
import net.liftweb.http.{S, DispatchSnippet}
import net.liftweb.util.Helpers._
import net.liftweb.widgets.flot._
import net.liftweb.common.Full

class Graph extends DispatchSnippet {
  def dispatch = {
    case kind => ignore => flot(kind)
  }

  def flot(kind: String): NodeSeq = {
    val ofKind = Measure.where(Measure.kind is kind) in Measure.Collection
    val byName = ofKind.groupBy(_.name)
    val series = byName.map {
      case (name, values) =>
        new FlotSerie {
          override def label = Full(name)

          override def data = values.toList.map {m => (m.date.getTime.toDouble, m.value)}
        }
    }
    val id = nextFuncName
    //TODO - remove defaults; style with css
    val width = S.attr("width").openOr("600px")
    val height = S.attr("height").openOr("300px")

    val options = new FlotOptions {
      override def xaxis = Full(new FlotAxisOptions{
        override def mode = Full("time")
      })
    }

    <div id={id} style={"width:" + width + ";height:" + height}/> ++ Flot.render(id, series.toList, options, Flot.script(NodeSeq.Empty))
  }
}