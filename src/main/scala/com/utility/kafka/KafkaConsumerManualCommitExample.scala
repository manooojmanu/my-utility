package com.utility.kafka

import akka.actor.ActorSystem
import akka.kafka.ConsumerSettings
import akka.kafka.scaladsl.Consumer
import akka.stream.scaladsl.Sink
import org.apache.kafka.common.serialization.StringDeserializer
import akka.kafka.Subscriptions
import akka.kafka.scaladsl.Committer
import akka.kafka.CommitterSettings
import akka.stream.ActorMaterializer
import scala.concurrent.ExecutionContext

object KafkaConsumerManualCommitExample extends App {

  implicit val system = ActorSystem("KafkaConsumerManualCommit")
  implicit val materializer = ActorMaterializer()
  implicit val ec: ExecutionContext = system.dispatcher

  val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("manual-commit-group")
    .withProperty("auto.offset.reset", "earliest")

  val committerSettings = CommitterSettings(system)

  Consumer
    .committableSource(consumerSettings, Subscriptions.topics("test-topic"))
    .mapAsync(1) { msg =>
      println(s"Consumed: ${msg.record.value()}")
      msg.committableOffset.commitScaladsl()
    }
    .runWith(Sink.ignore)
}
