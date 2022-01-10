organization := "com.example"

scalaVersion := "2.13.7"

enablePlugins(AkkaserverlessPlugin, JavaAppPackaging, DockerPlugin)
dockerBaseImage := "docker.io/library/adoptopenjdk:11-jre-hotspot"
dockerUsername := sys.props.get("docker.username")
dockerRepository := sys.props.get("docker.registry")
dockerUpdateLatest := true
ThisBuild / dynverSeparator := "-"
run / fork := true
// local with other service deployed
// run / javaOptions += "-Dconfig.resource=local-against-deployed.conf"
// both local with ports remapped
run / javaOptions += "-Dconfig.resource=local-against-local.conf"

Compile / scalacOptions ++= Seq(
  "-target:11",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xlog-reflective-calls",
  "-Xlint")
Compile / javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation", "-parameters" // for Jackson
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.7" % Test
)
