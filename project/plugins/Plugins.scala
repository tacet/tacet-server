import sbt._

class Plugins(info:ProjectInfo) extends PluginDefinition(info){
  val mpeltonen = "mpeltonen" at "http://mpeltonen.github.com/maven"
  val ideaPlugin = "com.github.mpeltonen" % "sbt-idea-plugin" % "0.1-SNAPSHOT"
}
