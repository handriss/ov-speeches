name := "ov-speeches-akka-worker"

version := "1.0"

scalaVersion := "2.11.0"

resolvers += "spray repo" at "http://repo.spray.io"

mainClass := Some("Main")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"    % "10.1.0-RC2",
  "com.typesafe.akka" %% "akka-stream"  % "2.5.9",
  "net.ruippeixotog" %% "scala-scraper" % "2.1.0",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.0-RC2",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "com.typesafe.akka" % "akka-slf4j_2.11" % "2.5.9",
  "io.spray" %%  "spray-json" % "1.3.3"
)

assemblySettings