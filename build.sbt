
name := "theia"

version := "0.1.0"

scalaVersion := "2.12.3"

lazy val akkaVersion = "2.5.3"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "org.apache.kafka" % "kafka-clients" % "0.11.0.0",
  "org.slf4j" % "slf4j-log4j12" % "1.8.0-alpha2",
  "io.github.carldata" %% "hydra-streams" % "0.3.0",

  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

assemblyJarName in assembly := "theia.jar"