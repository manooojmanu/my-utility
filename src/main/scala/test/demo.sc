import scala.collection.mutable.ArrayBuffer

val array = ArrayBuffer(5, 4, 3, 2, 0, 1312, 431, 1, 6)
def selectionSort(array: Array[Int]): Unit = {
  for (i <- array.indices) {
    var min = i

    for (j <- i + 1 until array.length) {

      if (array(j) < array(min)) {
        min = j
      }
    }
    val (minValue, iValue) = (array(min), array(i))
    array(i) = minValue
    array(min) = iValue

  }
}
println(s"Before   $array")
def bubbleSort(array: ArrayBuffer[Int]): Unit = {
  array.indices.foreach {
    i =>
      println(s"i = $i")
      for (j <- 0 until array.length - 1) {
        println(s"j = $j")
        if (array(j) > array(j + 1)) {
          val temp = array(j)
          array(j) = array(j + 1)
          array(j + 1) = temp
        }
      }
  }
}


for (i <- 1 until array.length) {
  val key = array(i)
  var j = i - 1

  while (j >= 0 && array(j) > key) {
    println(s"j == ${j}")
    array(j + 1) = array(j)
    j -= 1
  }
  println("-----------------------------")

  array(j + 1) = key

}


println(array)