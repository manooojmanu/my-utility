
//List(2,4,6,7) and you have to generate  List of List in such way that inner List would contain all factors of outer element

val input = List(2, 4, 6, 7)
val output = List(List(1, 2), List(1, 2, 4), List(1, 2, 3, 6), List(1, 7))

def getFactors(input: List[Int]): List[List[Int]] = {
  input.map {
    value =>
      val numbersToCheck = (1 to  value).toList
      numbersToCheck.collect {
        case i if value % i == 0 => i
      }
  }
}

getFactors(input)