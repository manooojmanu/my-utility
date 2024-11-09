//Given an array of end of the day stock prices for ICICI Bank – [ 23, 7, 8, 6, 9, 7, 22, 5, 12 ]
//Write a method maxProfit which will take this array and print when to buy (buyIndex),
//when to sell (sellIndex) and maxProfit obtained. Note than you have to buy first before you can sell.
//  For example, in the above array, you have to buy at 6 and sell 22 to get the maxProfit of 16
//


def getMaxProfit(data: Array[Int]) = {
  var max = Int.MinValue
  var min = Int.MaxValue
  var buyIndex = 0
  var finalBuyIndex = 0
  var sellIndex = 0
  data.zipWithIndex.foreach {
    case (price, i) =>
      if (price < min) {
        buyIndex = i
        min = price
      }
      val diff = price - min
      if (diff > max) {
        sellIndex = i
        finalBuyIndex = buyIndex
        max = diff
      }
  }
  (max, finalBuyIndex, sellIndex)


}


def getMaxProfit(list: List[Int], max: Int = Int.MinValue, min: Int = Int.MaxValue, index: Int = -1, buyIndex: Int = 0, finalBuyIndex: Int = 0)={
  
}

val d = getMaxProfit(Array(23, 7, 8, 6, 9, 7, 22, 5, 12, 25))