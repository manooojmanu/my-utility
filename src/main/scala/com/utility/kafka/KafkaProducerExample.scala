package com.utility.kafka

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.Source
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import akka.stream.ActorMaterializer
import org.apache.kafka.clients.producer.ProducerConfig

object KafkaProducerExample extends App {

  implicit val system = ActorSystem("KafkaProducer")
  implicit val materializer = ActorMaterializer()

  import system.dispatcher

  val producerSettings = ProducerSettings(system, new StringSerializer, new StringSerializer)
    .withBootstrapServers("localhost:9092")
    .withProperty(ProducerConfig.ACKS_CONFIG, "all") // Ensure message durability

  // Define messages to send
  val done = Source(1 to 100000)
    .map { number =>
      val key = s"key-$number"
      val value = s"value-$number"
      new ProducerRecord[String, String]("test-topic", key, value)
    }
    .runWith(Producer.plainSink(producerSettings))

  // Terminate the system when the producer is done
  done.onComplete { result =>
    println(s"Producer finished with result: $result")
    system.terminate()
  }
}

