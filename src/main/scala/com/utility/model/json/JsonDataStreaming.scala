package com.utility.model.json

import spray.json.RootJsonFormat
import com.utility.model.json.BaseJsonProtocol._


case class JsonDataStreaming(status: String, message: String, result: JsonResult)

object JsonDataStreaming {
  implicit val jsonDataFormat: RootJsonFormat[JsonDataStreaming] = jsonFormat3(JsonDataStreaming.apply)
}

case class JsonResult(columns: List[String], tags: Option[Map[String, String]], values: List[List[Any]])

object JsonResult {
  implicit val jsonDataFormat: RootJsonFormat[JsonResult] = jsonFormat3(JsonResult.apply)
}
