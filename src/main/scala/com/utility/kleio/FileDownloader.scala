package com.utility.kleio

import com.utility.model.json.AnyJsonProtocol
import com.utility.util.DeletionUtility
import spray.json.{DefaultJsonProtocol, _}

import java.io._
import java.net.URL
import java.util.zip.GZIPInputStream
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.util.Random

object FileDownloader extends DefaultJsonProtocol with AnyJsonProtocol {
  def main(args: Array[String]): Unit = {
    // URL of the .gz file

    val fileContent = DeletionUtility.readFileAsString("download_request.json")
    val urls = fileContent.parseJson.asJsObject.fields("result").convertTo[JsArray].elements.map(_.asJsObject.fields("data").convertTo[JsArray].elements.map(_.convertTo[String])).flatten
    println(s"Downloading ${urls.length} files")
    val resF = Future.sequence(urls.zipWithIndex.map { case (url, i) =>
      val fileName = s"downloaded/${url.split('/').last.split('?').head.split(".csv").head + "_" + Random.nextInt() + ".csv"}"
      downloadFile(url, fileName, i)
        .map(_ => println(s"$i  Downloaded file: " + fileName))
    }
                               )


    Await.result(resF, 10000 seconds)
  }

  def downloadFile(url: String, fileName: String, i: Int): Future[Unit] = {
    Future {
      try {
        val connection = new URL(url).openConnection()
        val inputStream = new GZIPInputStream(connection.getInputStream)
        val fileOutputStream = new FileOutputStream(fileName)
        val buffer = new Array[Byte](1024)
        var bytesRead = inputStream.readAllBytes()


        fileOutputStream.write(bytesRead)

        fileOutputStream.close()
        inputStream.close()
      }
      catch {
        case e: Exception => println(s"Error downloading file: $e")
      }
    }

  }
}



