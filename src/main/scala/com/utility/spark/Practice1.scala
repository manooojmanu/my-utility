package com.utility.spark

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.functions._

case class Person(id: Int, name: String, age: Int, sub_code: Int, score: Int)

object Practice1 extends App with SparkContextBuilder {

  import sparkSession.implicits._

  val se: SparkSession =SparkSession.builder().appName("").getOrCreate()

  val sc: SparkContext = sparkSession.sparkContext
  val list = (1 to 5000).toList.flatMap {
    s =>
      val name = scala.util.Random.alphanumeric.take(10).mkString
      val id = s
      val age = s + 25
      (1 to 5).map {
        si =>

          val dep = ((s + si) % 5) + 1
          val score = scala.util.Random.nextInt(100)
          Person(id, name, age, dep, score)
      }

  }

  val test = sc
  val people: RDD[Person] = sc.parallelize(list)

  val peopleDF: DataFrame = people.toDF()

  val peopleDS: Dataset[Person] = people.toDS()
  peopleDS.createOrReplaceTempView("people")
  val df: DataFrame = sparkSession.sql("Select * from people")
  df.show()


  sparkSession.sql(
    """Select *,
      |case
      | when age>35 THEN 'ADULT'
      | when age<=35 and age>20 THEN 'MATURED'
      | when age<=20 THEN 'CHILD'
      | END as type
      | From people""".stripMargin)
  //    .show()


  sparkSession.sql(
    """
      |Select * from(Select * from people
      |order by age desc
      |limit 2) as temp
      |order by age asc
      |limit 1
      |""".stripMargin)
  //    .show()

//Select * from ( Select name, age RANK() over (order by desc) as rank from people) as temp where rank = 2
  sparkSession.sql(
    """
      |Select * from
      |(Select name, age,
      |RANK() over (order by age desc) as rank
      |from people) as temp
      |where rank=2
      |""".stripMargin)
  //    .show()

  sparkSession.sql(
    """
      |Select * from (
      |Select *, ROW_NUMBER() over ( order by age desc) as row_num from people) as temp where row_num=2
      |""".stripMargin)
  //    .show()

  var query =
    """
      |Select designation, count(*) from people
      |group by designation
      |having count(*) > 1
      |""".stripMargin

  query =
    """
      |Select * from (
      SELECT
      |        id,
      |        name,
      |        age,
      |        sub_code,
      |        score,
      |        RANK() OVER (PARTITION BY sub_code ORDER BY score DESC) as rank
      |    FROM
      |        people) as temp where rank=1
      |""".stripMargin

  run(query)

  df.groupBy("id").agg(sum("score").alias("total_score_by_person")).orderBy(desc("total_score_by_person")).show()

  run(
    """
      |Select *, SUM(score) over (partition by id) as total_score from people order by total_score desc
      |""".stripMargin)


//  val groupedBy


  def run(query: String) = {
    sparkSession.sql(query).show()
  }
val d = ""
}
