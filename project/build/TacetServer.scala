import sbt._

class TacetServer(info: ProjectInfo) extends ParentProject(info) with IdeaProject {
  lazy val web = project("web", "web", new Web(_))

  class Web(info: ProjectInfo) extends DefaultWebProject(info)
          with IdeaProject
          with AutoCompilerPlugins {

    override def managedStyle = ManagedStyle.Maven
//    override def jettyWebappPath = webappPath
    override def scanDirectories = Nil

    def lift(name:String) = "net.liftweb" %% ("lift-"+name) % "2.1-M1"

    val webkit = lift("webkit")
    val record = lift("record")
    val couchdb = lift("couchdb")

    val jetty6 = "org.mortbay.jetty" % "jetty" % "6.1.21" % "test"
    val servlet = "javax.servlet" % "servlet-api" % "2.5" % "provided"

    val sxr = compilerPlugin("org.scala-tools.sxr" %% "sxr" % "0.2.6")
    override def compileOptions = CompileOption("-P:sxr:base-directory:" + mainScalaSourcePath.absolutePath) :: super.compileOptions
  }
}
