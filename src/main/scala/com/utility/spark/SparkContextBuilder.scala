package com.utility.spark

import com.utility.spark.SparkContextBuilder.createSparkContext
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

import java.util.Properties
import scala.collection.JavaConverters._
trait SparkContextBuilder{
  def createSparkContext(appName: String, mode: String): SparkSession = {
    val sparkConf = new SparkConf
    if (mode == "local") {
      val properties = new Properties
      properties.load(scala.io.Source.fromFile("spark.conf").bufferedReader())
      properties.asScala.foreach(kv => sparkConf.set(kv._1, kv._2))
      sparkConf.set("spark.sql.objectHashAggregate.sortBased.fallbackThreshold", "2024")
      sparkConf.setAppName(appName).setMaster("local[4]")
    } else {
      sparkConf.set("spark.ui.prometheus.enabled", "true")
      sparkConf.set("spark.databricks.delta.retentionDurationCheck.enabled", "false")
      sparkConf.set("spark.sql.sources.parallelPartitionDiscovery.parallelism", "1000")
      sparkConf.set("spark.databricks.delta.vacuum.parallelDelete.enabled", "true")
      sparkConf.set("spark.sql.streaming.metricsEnabled", "true")
      sparkConf.set("spark.sql.streaming.fileSource.log.compactInterval", "1")
      sparkConf.set("spark.sql.streaming.fileSource.cleaner.numThreads", "320")
      sparkConf.set("spark.sql.extensions", "io.delta.sql.DeltaSparkSessionExtension")
      sparkConf.set("spark.sql.catalog.spark_catalog", "org.apache.spark.sql.delta.catalog.DeltaCatalog")
      sparkConf.set("delta.logRetentionDuration", "interval 35 days")
      //      sparkConf.set("spark.sql.objectHashAggregate.sortBased.fallbackThreshold", "2024")
      sparkConf.setAppName(appName)
    }
    val spark = SparkSession.builder().config(sparkConf)
      .config("spark.driver.extraJavaOptions", "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED")
      .config("spark.executor.extraJavaOptions", "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED")
      .getOrCreate()
    val sc = spark.sparkContext
    sc.hadoopConfiguration.set("fs.s3.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
    spark
  }
 protected val sparkSession: SparkSession = createSparkContext("VDIBatching", "local")
}
object SparkContextBuilder extends SparkContextBuilder{




}
