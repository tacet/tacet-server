package tacet.model

import net.liftweb.actor.LiftActor
import tacet.rest.RestMeasurements.RootNode
import net.liftweb.util.ActorPing
import java.util.Date
import net.liftweb.util.Helpers._

object Generator extends LiftActor {
  protected def messageHandler = {
    case g @ Generate(until, every, call) if now.before(until) =>
      println("ping")
      call()
      ActorPing.schedule(this, g, every)
    case Generate(until, every, call) =>
      println("after")
  }
}

case class Generate(until:Date, every:Long, call:() => Any)