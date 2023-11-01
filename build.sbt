Global / excludeLintKeys += test / fork
Global / excludeLintKeys += run / mainClass

val scala2Version = "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "Random Walker Attacker",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala2Version,

    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.4.3",
      "org.scalactic" %% "scalactic" % "3.2.17",
      "org.scalatest" %% "scalatest" % "3.2.17" % "test",
      "org.slf4j" % "slf4j-simple" % "2.0.5",
    ),

    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % "3.5.0",
      "org.apache.spark" %% "spark-graphx" % "3.5.0",
      "org.apache.spark" %% "spark-sql" % "3.5.0",

    )
  )

compileOrder := CompileOrder.JavaThenScala
test / fork := true
run / fork := true
assembly / javaOptions ++= Seq(
  "-Xms8G",
  "-Xmx500G",
  "-XX:+UseG1GC"
)

Compile / mainClass := Some("com.lsc.Main")
test / mainClass := Some("com.lsc.Main")
run / mainClass := Some("com.lsc.Main")