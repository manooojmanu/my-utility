package com.utility.model.json

import spray.json.{DefaultJsonProtocol, JsString, JsValue, RootJsonFormat, deserializationError}

import java.util.UUID

trait UUIDJsonProtocol extends DefaultJsonProtocol {
  implicit val uuidFormat: RootJsonFormat[UUID] = new RootJsonFormat[UUID] {
    def write(x: UUID): JsValue = JsString(x.toString)

    def read(value: JsValue): UUID = value match {
      case JsString(x) => UUID.fromString(x)
      case x => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }
}

object UUIDJsonProtocol extends UUIDJsonProtocol