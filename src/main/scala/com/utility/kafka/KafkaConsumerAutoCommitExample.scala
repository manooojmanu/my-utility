package com.utility.kafka

import akka.actor.ActorSystem
import akka.kafka.ConsumerSettings
import akka.kafka.scaladsl.Consumer
import akka.stream.scaladsl.Sink
import org.apache.kafka.common.serialization.StringDeserializer
import akka.kafka.Subscriptions
import akka.stream.ActorMaterializer
import scala.concurrent.ExecutionContext

object KafkaConsumerAutoCommitExample extends App {

  implicit val system = ActorSystem("KafkaConsumerAutoCommit")
  implicit val materializer = ActorMaterializer()
  implicit val ec: ExecutionContext = system.dispatcher

  val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("auto-commit-group")
    .withProperty("auto.offset.reset", "earliest")

  Consumer
    .plainSource(consumerSettings, Subscriptions.topics("test-topic"))
    .runWith(Sink.foreach(println))
}

