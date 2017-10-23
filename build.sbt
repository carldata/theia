
name := "theia"

version := "0.2.0"

scalaVersion := "2.12.3"

lazy val akkaVersion = "2.5.3"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "org.apache.kafka" % "kafka-clients" % "0.11.0.1",
  "org.slf4j" % "slf4j-api" % "1.7.25",
  "de.siegmar" % "logback-gelf" % "1.0.4",
  "ch.qos.logback" % "logback-classic" % "1.2.3" % Runtime,
  "io.github.carldata" %% "hydra-streams" % "0.4.3",

  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

assemblyJarName in assembly := "theia.jar"