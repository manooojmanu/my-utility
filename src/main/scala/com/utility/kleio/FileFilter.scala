package com.utility.kleio

import com.typesafe.scalalogging.LazyLogging
import com.utility.spark.SparkContextBuilder.createSparkContext
import com.utility.util.CommonUtility.{toMilliSeconds, toNano}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{col, from_unixtime, udf}
import org.apache.spark.sql.types.{IntegerType, LongType, StringType, StructField, StructType}

import java.time.{Instant, ZoneId}
import java.time.format.DateTimeFormatter
import scala.util.{Failure, Success, Try}

object FileFilter extends LazyLogging {
  def main(args: Array[String]): Unit = {

    // Path to your input CSV file
    val inputFilePath = "C:\\Users\\DU84CL8\\Downloads\\Stena Forwarder\\PS\\Data"


    // Path to your output folder
    val outputFolderPath = "C:\\Hadoop\\filtered"

    //    System.setProperty("hadoop.home.dir", "C:\\hadoop")


    // Create a SparkSession
    val spark = Try(createSparkContext("VDIBatching", "local")) match {
      case Success(value) => value
      case Failure(exception) =>
        logger.error("Error in creating spark context.", exception)
        throw exception
    }

    val timestampFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")


    val startEpochNanos: Long = toMilliSeconds(Instant.parse("2023-09-11T00:00:00.000Z"))
    val endEpochNanos: Long = toMilliSeconds(Instant.parse("2023-09-11T23:00:00.000Z"))
    val schema = StructType(Array(
      StructField("state", LongType, true),
      StructField("quality", IntegerType, true),
      StructField("value", LongType, true),
      StructField("timestamp", LongType, true),
      StructField("uuid", StringType, true)
    ))
    // Read the CSV file into a DataFrame
    val dataInputDataFrame: DataFrame = spark.readStream
      .option("header", "true") // Use "true" if the CSV file has a header row
      .option("inferSchema", "false") // Use "true" to infer the schema automatically
      .format("csv")
      .schema(schema)
      .load(inputFilePath)
    val toHumanReadableUDF = udf((timestamp: Long) => {
      Instant.ofEpochMilli(timestamp).toString
    })


    val filteredDF: DataFrame = dataInputDataFrame
//      .filter(col("timestamp").cast("long").between(startEpochNanos, endEpochNanos))
      .withColumn("time_stamp_hrf", toHumanReadableUDF(col("timestamp")))
      .withColumn("day", from_unixtime(col("timestamp") / 1000, "dd").cast("int"))
      .withColumn("hour", from_unixtime(col("timestamp") / 1000, "HH").cast("int"))


    filteredDF.coalesce(1)
      .writeStream
      .format("csv")
      .outputMode("append")
      .option("header", "true")
//      .partitionBy("day", "hour")
      .option("checkpointLocation", outputFolderPath + "/checkpoint")
      .start(outputFolderPath)
//      .awaitTermination()

    Thread.sleep(20000)
    spark.stop()
  }
}
