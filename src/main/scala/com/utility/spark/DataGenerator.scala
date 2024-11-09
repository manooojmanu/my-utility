package com.utility.spark

object DataGenerator {

}

import spray.json._

import java.io.PrintWriter
import scala.util.Random

// Define case class for our data structure
case class Record(id: Int, category: String, subcategory: String, name: String, value: Double)

// Implement JSON formatting for the case class
object Record extends DefaultJsonProtocol {
  implicit val recordFormat = jsonFormat5(Record.apply)
}


object LargeJsonFileGenerator extends App {
  val numRecords = 100000
  val fileName = "large_data.json"

  // Sample data fields
  val categories = Seq("A", "B", "C", "D")
  val subcategories = Seq("X", "Y", "Z")
  val names = Seq("Alice", "Bob", "Charlie", "David", "Eve")

  // Generate random data
  val data: List[Record] = (1 to numRecords).toList.map { _ =>
    Record(
      id = Random.nextInt(10000),
      category = categories(Random.nextInt(categories.size)),
      subcategory = subcategories(Random.nextInt(subcategories.size)),
      name = names(Random.nextInt(names.size)),
      value = Random.nextDouble() * 1000
      )
  }

  // Convert data to JSON and write to file
  val json = data.toJson.prettyPrint
  val writer = new PrintWriter(fileName)
  writer.write(json)
  writer.close()

  println(s"Generated $numRecords records in $fileName")
}


import spray.json.DefaultJsonProtocol._

// Define case class for student record
case class StudentRecord(
                          studentId: Int,
                          name: String,
                          age: Int,
                          gender: String,
                          subject: String,
                          score: Double,
                          grade: String
                        )

// Define JSON formatting for the case class
object StudentRecord extends DefaultJsonProtocol {
  implicit val studentRecordFormat = jsonFormat7(StudentRecord.apply)
}

object StudentJsonFileGenerator extends App {
  val numRecords = 100000
  val fileName = "student_records.json"

  // Sample data fields
  val names = Seq(
    "Aarav", "Vivaan", "Aditya", "Vihaan", "Arjun",
    "Sai", "Reyansh", "Ayaan", "Krishna", "Lakshmi",
    "Nisha", "Aditi", "Ananya", "Saanvi", "Diya",
    "Ishaan", "Riya", "Tara", "Sneha", "Priya",
    "Rohan", "Rahul", "Karan", "Shivam", "Ritika",
    "Tanvi", "Pooja", "Neha", "Divya", "Meera",
    "Mohit", "Siddharth", "Akshay", "Kavya", "Simran",
    "Ravi", "Nikita", "Avantika", "Harsh", "Shreya",
    "Kiran", "Bhavya", "Ira", "Anjali", "Chaitanya",
    "Sakshi", "Siddhi", "Aarohi", "Navya", "Parineeti"
    )
  val ages = 21 to 36
  val genders = Seq("Male", "Female", "Other")
  val subjects = Seq("Math", "Science", "History", "Literature", "Art", "Physical Education", "Biology", "Chemistry")
  val grades = Seq("A", "B", "C", "D", "E", "F")

  // Use a map to ensure consistency
  val studentData = scala.collection.mutable.Map[Int, (String, Int, String)]()

  // Generate random student data
  val data = (1 to numRecords).map { _ =>
    // Generate a unique studentId
    val studentId = Random.nextInt(10000)

    // Check if studentId already exists
    if (!studentData.contains(studentId)) {
      // If not, randomly generate name, age, and gender
      val name = names(Random.nextInt(names.size))
      val age = ages(Random.nextInt(ages.size))
      val gender = genders(Random.nextInt(genders.size))
      // Store the data for this studentId
      studentData(studentId) = (name, age, gender)
    }

    // Retrieve the name, age, and gender for the studentId
    val (name, age, gender) = studentData(studentId)

    StudentRecord(
      studentId = studentId,
      name = name,
      age = age,
      gender = gender,
      subject = subjects(Random.nextInt(subjects.size)),
      score = Random.nextDouble() * 100, // Score between 0 and 100
      grade = grades(Random.nextInt(grades.size))
      )
  }


  val writer = new PrintWriter(fileName)

  data.foreach {
    record => writer.println(record.toJson.compactPrint)
  }

  writer.close()

  println(s"Generated $numRecords student records in $fileName")
}
