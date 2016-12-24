name := """Todo"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
	.enablePlugins(PlayScala, DebianPlugin, UpstartPlugin)

libraryDependencies ++= Seq(
	"org.reactivemongo" %% "play2-reactivemongo" % "0.12.0",
	"net.codingwell" %% "scala-guice" % "4.1.0",
	"org.mockito" % "mockito-core" % "2.2.11" % "test"
)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

/// eclipse plugin
EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource
EclipseKeys.eclipseOutput := Some(".target")
EclipseKeys.withSource := true
EclipseKeys.withJavadoc := true
EclipseKeys.skipParents in ThisBuild := false
// avoid some scala specific source directories
unmanagedSourceDirectories in Compile := Seq((javaSource in Compile).value)
unmanagedSourceDirectories in Test := Seq((javaSource in Test).value)

/// sbt native package setting
maintainer in Linux := "nooksys <nooksys@gmail.com>"
packageSummary in Linux := "admin service"
packageDescription := "admin service rest api"

javaOptions in Universal ++= Seq(
  // JVM memory tuning
  "-J-Xmx128m",
  "-J-Xms64m",

  // Since play uses separate pidfile we have to provide it with a proper path
  // name of the pid file must be play.pid
  s"-Dpidfile.path=/var/run/${packageName.value}/play.pid",

  // alternative, you can remove the PID file
  // s"-Dpidfile.path=/dev/null",

  // Use separate configuration file for production environment
  s"-Dconfig.file=/usr/share/${packageName.value}/conf/production.conf",

  // Use separate logger configuration file for production environment
  s"-Dlogger.file=/usr/share/${packageName.value}/conf/production-logger.xml"

  // You may also want to include this setting if you use play evolutions
  // "-DapplyEvolutions.default=true"
)