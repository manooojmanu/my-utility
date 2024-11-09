package com.utility.util

import spray.json._

import java.nio.charset.StandardCharsets
import java.util.Base64
import scala.util.Try

object Base64Decoder {

  def decode(encodedString: String): String = {
    val bytes = encodedString.getBytes(StandardCharsets.UTF_8)
    new String(Base64.getDecoder.decode(bytes), StandardCharsets.UTF_8)
  }

  def decodeTo[T: JsonReader](encodedString: String): T = {
    decode(encodedString).parseJson.convertTo[T]
  }

  def decodeOrFallBack(encodedString: String): String = {
    Try(decode(encodedString)).toOption.getOrElse(encodedString)
  }
}
