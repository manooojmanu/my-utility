package com.utility.kleio

import com.utility.spark.SparkContextBuilder
import org.apache.spark.sql.functions.{col, count}

object DeltaLakeExample extends SparkContextBuilder with App {

  // read fom csv and create delta lake table
  //  private val df = sparkSession.read.option("header", "true").csv("sparkExamples/data/data_sample.csv")
  //  df.write.format("delta").save("sparkExample/deltaLake")

  ()

  private val deltaLake = sparkSession.read.format("delta").load("sparkExample/deltaLake")

  deltaLake.show()
  deltaLake.groupBy(col("uuid")).agg(count("*")).show()

  sparkSession.close()

}
