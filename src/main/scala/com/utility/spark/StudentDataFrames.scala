package com.utility.spark

import org.apache.spark.sql.functions._

object StudentDataFrames extends App with SparkContextBuilder {
  """
    |{"age":28,"gender":"Female","grade":"D","name":"Ayaan","score":71.39411850924151,"studentId":9718,"subject":"Science"}
    |""".stripMargin

  import sparkSession.implicits._

  val studentsDF = sparkSession.read.option("inferSchema", "true")
    .json("C:\\Users\\DU84CL8\\code\\utility\\MyUtility\\student_records.json")

  studentsDF.createOrReplaceTempView("students")

  studentsDF.show()


  run(
    """
      |Select * from (
      |Select *,
      |ROW_NUMBER() over (partition by subject order by score asc) as rank
      |from students ) as temp where rank=1
      |
      |
      |""".stripMargin)

  run(
    """
      |Select name, studentId ,
      | Sum(score) as totalMarksScored,
      | Count(subject) * 100 as totalMarks,
      | (Sum(score) / (Count(subject) * 100)) * 100 as percentage
      | from students
      |group by studentId, name
      |
      |""".stripMargin)


  run(
    """
      | Select name, studentId,
      | Count(subject) as totalSubjects
      | from students
      | group by studentId, name
      |""".stripMargin)

  run(
    """
      |Select Count(
      |""".stripMargin)


  def run(query: String) = {
    sparkSession.sql(query).show()
  }
}
