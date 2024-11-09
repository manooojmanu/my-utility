package com.utility.kleio

import com.utility.spark.SparkContextBuilder

object DeltaLakeWriter extends SparkContextBuilder with App {
  //1 to infinity range

  while (true) {
    val df = sparkSession.read.format("delta").load("sparkExample/deltaLake")
    df.write.format("delta").mode("append").save("sparkExample/deltaLake")
    Thread.sleep(1000)
  }

}
