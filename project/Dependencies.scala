
import sbt.*

object Dependencies {
  private object Version {
    val akka = "2.5.32"
    val akkaHttp = "10.2.10"
    val couchBaseScalaClient = "1.4.9"
    val kamon = "2.6.3"
    val sparkVersion = "3.3.2"
  }

  val compile: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-actor" % Version.akka,
    "com.typesafe.akka" %% "akka-stream" % Version.akka,
    "com.typesafe.akka" %% "akka-slf4j" % Version.akka,
    "com.typesafe.akka" %% "akka-http" % Version.akkaHttp,
    "com.typesafe.akka" %% "akka-http-spray-json" % Version.akkaHttp,
    "com.couchbase.client" %% "scala-client" % Version.couchBaseScalaClient,
    "com.beachape" %% "enumeratum" % "1.7.0",
    "io.kamon" %% "kamon-bundle" % Version.kamon,
    "com.roundeights" %% "hasher" % "1.2.0",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
    "ch.qos.logback" % "logback-classic" % "1.2.10",
    "ch.qos.logback.contrib" % "logback-json-core" % "0.1.5",
    "ch.qos.logback.contrib" % "logback-json-classic" % "0.1.5",
    "ch.qos.logback.contrib" % "logback-jackson" % "0.1.5",
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.1.3",
    "com.github.pureconfig" %% "pureconfig" % "0.17.1",
    "eu.timepit" %% "refined-pureconfig" % "0.10.1",
    "com.danielasfregola" %% "random-data-generator" % "2.9",
    "com.github.wookietreiber" %% "scala-chart" % "0.5.1",
    "org.apache.kafka" %% "kafka" % "2.8.0",
    "org.apache.spark" %% "spark-core" % Version.sparkVersion,
    "org.apache.spark" %% "spark-sql" % Version.sparkVersion,
    "org.apache.spark" %% "spark-streaming" % Version.sparkVersion,
    "org.apache.hadoop" % "hadoop-common" % Version.sparkVersion,
    "org.apache.hadoop" % "hadoop-client" % Version.sparkVersion,
    "org.apache.hadoop" % "hadoop-aws" % Version.sparkVersion,
    "io.delta" %% "delta-core" % "2.3.0",
    "com.typesafe.akka" %% "akka-stream-kafka" % "1.1.0"
    )


  val test: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % "3.2.15",
    "com.typesafe.akka" %% "akka-testkit" % Version.akka,
    "com.typesafe.akka" %% "akka-http-testkit" % Version.akkaHttp,
    "org.scalacheck" %% "scalacheck" % "1.17.0",
    "com.propensive" %% "magnolia" % "0.17.0",
    "org.scalamock" %% "scalamock" % "5.2.0"
    ).map(_ % Test)

}
