package com.utility.json

import com.typesafe.scalalogging.LazyLogging
import com.utility.model.json.JsonDataStreaming
import spray.json._

import java.util.UUID
import scala.util.{Failure, Success, Try}

object ProcessStreamingJson extends LazyLogging {

  def processHistoryResponse(inputString: String, assetUUIDs: Seq[UUID] = List.empty): List[JsonDataStreaming] = {

    val jsonObjects = inputString
      .split("\\}\\s*\\{")
      .toList
    val sizeJson = jsonObjects.size
    jsonObjects.zipWithIndex
      .map {
        case (jsonString, i) =>
          if (sizeJson == 1)
            jsonString
          else {
            if (i == 0) {
              jsonString + "}"
            }
            else if (i == (sizeJson - 1))
              "{" + jsonString
            else
              "{" + jsonString + "}"
          }
      }
      .map {
        s =>
          Try(s.parseJson.convertTo[JsonDataStreaming]) match {
            case Success(v) =>
              v
            case Failure(ex) =>
              logger.error(s"Exception occurred while parsing history response for for shells:${assetUUIDs}")
              throw ex
          }
      }
  }

}
