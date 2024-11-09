package com.utility.model.json

import spray.json._

trait AnyJsonProtocol {
  implicit val anyFormat: RootJsonFormat[Any] = new RootJsonFormat[Any] {
    def write(any: Any): JsValue = any match {
      case n: Int => JsNumber(n)
      case s: String => JsString(s)
      case b: Boolean => JsBoolean(b)
      case l: Long => JsNumber(l)
      case d: Double => JsNumber(d)
      case f: Float => JsNumber(f.toDouble)
      case null => JsNull
      case _ => throw deserializationError("Can't serialize this value to JSON")
    }

    def read(json: JsValue): Any = json match {
      case JsNumber(n) if n.isValidInt => n.intValue()
      case JsNumber(n) if n.isValidLong => n.longValue()
      case JsNumber(n) => n.doubleValue()
      case JsString(s) => s
      case JsBoolean(b) => b
      case JsNull => null
      case _ => throw deserializationError("Can't deserialize this JSON to a value")
    }
  }

}

object AnyJsonProtocol extends AnyJsonProtocol
