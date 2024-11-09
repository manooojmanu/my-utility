package com.utility.util

import com.couchbase.client.scala.codec.{JsonDeserializer, JsonSerializer}
import spray.json._

import scala.util.Success

object SprayToCouchBaseJsonSerializerConverters {
  implicit def jsonSerializer[T: JsonWriter]: JsonSerializer[T] = {
    (content: T) => Success(content.toJson.compactPrint.getBytes)
  }

  implicit def jsonDeSerializer[T: JsonReader]: JsonDeserializer[T] = (bytes: Array[Byte]) => {
    Success(new String(bytes).parseJson.convertTo[T])
  }
}
