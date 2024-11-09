object Test {
  def getMaxProfit(prices: Array[Int]) = {
    var max = Int.MinValue
    prices.zipWithIndex.foreach {
      case (value, i) =>
        var j = i + 1
        while (j <= prices.length) {
          val diff = prices(j) - value
          if (diff > max)
            max = diff
        }
        j += 1

    }
    max
  }
}

Test.getMaxProfit(Array(7, 1, 5, 3, 6, 4))