package com.utility.util

import spray.json._

object Demo extends App with DefaultJsonProtocol {
  val json = {
    """
      |{
      |    "name": "Quarterly measurement for Toben%e",
      |    "assetUuid": "f5987860-7a6e-4e6f-b479-83e0392d4de2",
      |    "startTime": 1724918889542,
      |    "endTime": 1724922489542,
      |    "measurementType": "seatrial",
      |    "fuelType": "oil",
      |    "audience": "customer",
      |    "maintenanceInterval": "quarterly",
      |    "powerLoad": 75,
      |    "comment": "",
      |    "data": [
      |        {
      |            "value": 0.0033,
      |            "uuid": "00afee39-ebed-4fa7-b3f2-adf1bb9ce1a8"
      |        }
      |    ]
      |}
      |""".stripMargin.parseJson.asJsObject


  }

  val data = (1 to 300).map {
    i =>
      JsObject("value" -> JsNumber(i), "uuid" -> JsString(java.util.UUID.randomUUID().toString))
  }.toJson.convertTo[JsArray]

  val updatedJson = json.copy(fields = json.fields + ("data" -> data))

  println(updatedJson.compactPrint.getBytes("UTF-8").length / 1024.0)
}
