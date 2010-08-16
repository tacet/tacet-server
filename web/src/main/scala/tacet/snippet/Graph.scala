package tacet.snippet

import xml.NodeSeq
import tacet.model.Measure
import net.liftweb.http.{S, DispatchSnippet}
import net.liftweb.util.Helpers._
import com.osinka.mongodb._

class Graph extends DispatchSnippet {
  def dispatch = {
    case kind => graph(kind) _
  }

  def graph(kind: String)(xhtml: NodeSeq): NodeSeq = {
    <div>{kind}</div> ++ (Measure.where(Measure.kind is kind) in Measure.Collection).toList.flatMap {
      measure =>
        <div>
          {measure}
        </div>
    }
  }
}