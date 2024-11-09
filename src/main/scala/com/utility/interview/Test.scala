import org.apache.spark.sql.DataFrame

object Test11 extends App {


  import org.apache.spark.sql.SparkSession
  import org.apache.spark.sql.{functions => F}

  val spark = SparkSession.builder()
    .appName("TestApp").master("local[*]")
    .getOrCreate()

  import spark.implicits._

  val str =
    """
      |{
      |  "user": {
      |    "user_id": "001",
      |    "name": "Alice Smith",
      |    "email": "alice.smith@example.com",
      |    "orders": [
      |      {
      |        "order_id": "A123",
      |        "date": "2024-11-05",
      |        "status": "Shipped",
      |        "items": [
      |          { "item_id": "P001", "name": "Wireless Headphones", "price": 99.99 },
      |          { "item_id": "P002", "name": "Laptop Stand", "price": 25.99 }
      |        ]
      |      },
      |      {
      |        "order_id": "A124",
      |        "date": "2024-11-01",
      |        "status": "Delivered",
      |        "items": [
      |          { "item_id": "P003", "name": "Smartphone Case", "price": 15.00 }
      |        ]
      |      }
      |    ]
      |  }
      |}
      |""".stripMargin

  // Read JSON string into DataFrame
  val df: DataFrame = spark.read.json(Seq(str).toDS())

  // Step 1: Flatten the "user" structure
  val userDF = df.selectExpr("user.user_id as user_id",
                             "user.name as name",
                             "user.email as email",
                             "user.orders as orders")

  // Step 2: Explode the "orders" array to create one row per order
  val ordersDF = userDF.withColumn("order", F.explode(F.col("orders")))
    .drop("orders")

  // Step 3: Flatten the "order" structure, including the "items" array
  val flattenedOrderDF = ordersDF.select(
    F.col("user_id"),
    F.col("name"),
    F.col("email"),
    F.col("order.order_id").as("order_id"),
    F.col("order.date").as("order_date"),
    F.col("order.status").as("order_status"),
    F.explode(F.col("order.items")).as("item")
    )

  // Step 4: Flatten the "item" structure
  val finalDF = flattenedOrderDF.select(
    F.col("user_id"),
    F.col("name"),
    F.col("email"),
    F.col("order_id"),
    F.col("order_date"),
    F.col("order_status"),
    F.col("item.item_id").as("item_id"),
    F.col("item.name").as("item_name"),
    F.col("item.price").as("item_price")
    )

  // Show the final flattened DataFrame
  finalDF.show(false)

  // Stop the Spark session
  spark.stop()

}
