package com.utility.util


import com.typesafe.scalalogging.LazyLogging
import spray.json.{JsValue, _}

import java.io.FileWriter
import scala.io.Source


trait DeletionUtility extends DefaultJsonProtocol with LazyLogging {
  val fileName: String
  val outPutFileName: String = "deletion_response.txt"
  lazy val contentAsStr: String = DeletionUtility.readFileAsString(fileName)
  lazy val contentAsJsonList: List[JsValue] = contentAsStr.parseJson.convertTo[List[JsValue]].distinct

  def writeStringToFile(content: String): Unit = {
    DeletionUtility.writeStringToFile(content, outPutFileName)
  }

  val response: String


}


object DeletionUtility extends LazyLogging {
  def writeStringToFile(content: String, fileName: String): Unit = {
    val writer = new FileWriter(fileName)
    try {
      writer.write(content)
      logger.info("Written to file:deletion_response.txt")
    } finally {
      writer.close()
    }
  }

  def readFileAsString(fileName: String): String = {
    val source = Source.fromFile(fileName)
    try {
      source.getLines().mkString.stripMargin.trim.replaceAll("\uFEFF", "")
    } finally {
      source.close()
    }
  }
}

