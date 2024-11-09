import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.col

val sparkSession = SparkSession.builder()
  .appName("TestApp").master("local[*]")
  .getOrCreate()

import sparkSession.implicits._

val str =
  """
    |{
    |  "user": {
    |    "user_id": "001",
    |    "name": "Alice Smith",
    |    "email": "alice.smith@example.com",
    |    "order": {
    |      "order_id": "A123",
    |      "date": "2024-11-05",
    |      "status": "Shipped",
    |      "items": [
    |        {
    |          "item_id": "P001",
    |          "name": "Wireless Headphones",
    |          "quantity": 1,
    |          "price": 99.99
    |        },
    |        {
    |          "item_id": "P002",
    |          "name": "Laptop Stand",
    |          "quantity": 1,
    |          "price": 25.99
    |        }
    |      ],
    |      "total": 125.98
    |    }
    |  }
    |}
    |
    |""".stripMargin

val json = sparkSession.read.json(List(str).toDS())
json.printSchema()

json.select(col("user"))




json.show()

sparkSession.close()