import java.util.jar.JarFile
import java.net.{URLDecoder, URL}
import java.io.File
import scala.collection.JavaConverters._

object ClassPathFinder {

  // Function to search for the class by its simple name in a jar file
  def findClassInJar(jarPath: String, className: String): Option[String] = {
    val jarFile = new JarFile(jarPath)
    val entries = jarFile.entries().asScala

    entries.find { entry =>
      val entryName = entry.getName
      // Check if the entry ends with the class file and matches the simple class name
      entryName.endsWith(s"$className.class")
    }.map { entry =>
      // Return the fully qualified path of the class in the jar
      s"$jarPath!/${entry.getName}"
    }
  }

  // Function to search for the class in the file system (directories)
  def findClassInFileSystem(directory: File, className: String): Option[String] = {
    if (directory.exists() && directory.isDirectory) {
      val files = directory.listFiles().filter(_.isFile)
      val matchingClassFile = files.find { file =>
        file.getName == s"$className.class"
      }
      matchingClassFile.map(_.getAbsolutePath)
    } else {
      None
    }
  }

  def findClassPath(className: String): Option[String] = {
    try {
      // Get all resources in the classpath
      val resources = ClassLoader.getSystemClassLoader.getResources("")

      val classPaths = resources.asScala.flatMap { url =>
        val decodedUrl = URLDecoder.decode(url.getFile, "UTF-8")
        val file = new File(decodedUrl)

        if (file.isDirectory) {
          // Search the file system (for directories)
          findClassInFileSystem(file, className)
        } else if (decodedUrl.endsWith(".jar")) {
          // Search the jar file
          findClassInJar(decodedUrl, className)
        } else {
          None
        }
      }

      classPaths.toList.headOption
    } catch {
      case e: Exception =>
        e.printStackTrace()
        None
    }
  }

  def main(args: Array[String]): Unit = {
    val className = "TestClass" // Example class name
    findClassPath(className) match {
      case Some(path) => println(s"Class found at: $path")
      case None => println("Class not found.")
    }
  }
}
