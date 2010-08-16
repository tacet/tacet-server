import sbt._

class TacetServer(info: ProjectInfo) extends ParentProject(info) {
  lazy val web = project("web", "web", new Web(_))

  class Web(info: ProjectInfo) extends DefaultWebProject(info)
          with AutoCompilerPlugins {

    override def managedStyle = ManagedStyle.Maven
    override def scanDirectories = Nil

    def lift(name:String) = "net.liftweb" %% ("lift-"+name) % "2.1-M1"

    val webkit = lift("webkit")
    val widgets = lift("widgets")
    val scala_mongo_driver = "com.osinka" %% "mongo-scala-driver" % "0.8.2"

    val jetty6 = "org.mortbay.jetty" % "jetty" % "6.1.21" % "test"
    val servlet = "javax.servlet" % "servlet-api" % "2.5" % "provided"

    val specs = "org.scala-tools.testing" %% "specs" % "1.6.5" % "test"

    val sxr = compilerPlugin("org.scala-tools.sxr" %% "sxr" % "0.2.6")
    override def compileOptions = CompileOption("-P:sxr:base-directory:" + mainScalaSourcePath.absolutePath) :: super.compileOptions
  }
}
