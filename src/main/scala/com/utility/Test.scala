package com.utility

object Test extends App {
  def getMaxProfit(prices: Array[Int]) = {
    var max = Int.MinValue
    prices.zipWithIndex.foreach {
      case (value, i) =>
        var j = i + 1
        while (j < prices.length) {
          val diff = prices(j) - value
          if (diff > max)
            max = diff
          j += 1
        }
    }
    max
  }

  def getMaxProfit1(prices: Array[Int]): Int = {
    var minPrice = Int.MaxValue
    var maxProfit = 0

    prices.foreach { price =>
      if (price < minPrice)
        minPrice = price
      val profit = price - minPrice
      if (profit > maxProfit)
        maxProfit = profit
    }

    maxProfit
  }

  println(getMaxProfit1(Array(7, 1, 5, 3, 6, 4)))

}
