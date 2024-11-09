package com.utility

import java.io.{File, IOException}
import java.net.URL
import scala.collection.JavaConverters._
import java.util.Enumeration

object ClassScanner {

  // Function to convert a URL into a File if it points to a file system resource
  private def urlToFile(url: URL): Option[File] = {
    try {
      val uri = url.toURI
      Some(new File(uri))
    } catch {
      case _: Exception => None
    }
  }

  // Function to recursively list all .class files in a directory
  private def listClassFiles(directory: File, packageName: String): List[String] = {
    val files = directory.listFiles()
    if (files != null) {
      files.flatMap { file =>
        if (file.isDirectory) {
          listClassFiles(file, packageName + "." + file.getName)
        } else if (file.getName.endsWith(".class")) {
          // Strip ".class" extension and return fully qualified class name
          Some(packageName + "." + file.getName.replace(".class", ""))
        } else {
          None
        }
      }.toList
    } else {
      List.empty
    }
  }

  // Main method to scan all .class files for the given package
  def getClassNamesInPackage(packageName: String): List[String] = {
    val classLoader = Thread.currentThread().getContextClassLoader
    val path = packageName.replace('.', '/')
    val resources: Enumeration[URL] = classLoader.getResources(path)

    resources.asScala.flatMap { url =>
      urlToFile(url).map { directory =>
        listClassFiles(directory, packageName)
      }
    }.flatten.toList
  }

  def main(args: Array[String]): Unit = {
    try {
      // Example: Replace 'com.company.server' with your package
      val classNames = getClassNamesInPackage("com.utility")
      classNames.foreach(println)
    } catch {
      case e: IOException => e.printStackTrace()
    }
  }
}
