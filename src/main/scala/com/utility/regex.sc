import scala.util.matching.Regex
import scala.util.matching.Regex.Match

// Define the pattern to match
val pattern = """([a-fA-F0-9\-]{36})_(\d+)_(\d+)_(\d+)_([a-fA-F0-9\-]{36})_(\d+)""".r

// Define the input JSON-like string
val input = """{"1639c61a-2190-419f-b46c-760ceae0433c_0_0_0_358bb2a2-72b9-487c-9f74-4a6c953301f7_1":"mean([00f7a6a2-07bd-4d4e-be08-a50978b507fc_0_1_0_358bb2a2-72b9-487c-9f74-4a6c953301f7_1,00f7a6a2-07bd-4d4e-be08-a50978b507fc_0_2_0_358bb2a2-72b9-487c-9f74-4a6c953301f7_1,00f7a6a2-07bd-4d4e-be08-a50978b507fc_0_3_0_358bb2a2-72b9-487c-9f74-4a6c953301f7_1,00f7a6a2-07bd-4d4e-be08-a50978b507fc_0_4_0_358bb2a2-72b9-487c-9f74-4a6c953301f7_1,00f7a6a2-07bd-4d4e-be08-a50978b507fc_0_5_0_358bb2a2-72b9-487c-9f74-4a6c953301f7_1,00f7a6a2-07bd-4d4e-be08-a50978b507fc_0_6_0_358bb2a2-72b9-487c-9f74-4a6c953301f7_1,00f7a6a2-07bd-4d4e-be08-a50978b507fc_0_7_0_358bb2a2-72b9-487c-9f74-4a6c953301f7_1,00f7a6a2-07bd-4d4e-be08-a50978b507fc_0_8_0_358bb2a2-72b9-487c-9f74-4a6c953301f7_1])"}"""

// Define the replacement logic
val replaceFunction: Match => String = { m =>
  val uuid1 = m.group(1)
  val int1 = m.group(2)
  val int2 = m.group(3)
  val int3 = m.group(4)
  val uuid2 = m.group(5)
  val int4 = m.group(6)

  // Define your replacement logic here
  System.currentTimeMillis().toString
}

// Perform replacement
val result = pattern.replaceAllIn(input, m => replaceFunction(m))

println(result)
