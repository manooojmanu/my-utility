package com.utility.chart

class SampleChart {

}

import scalax.chart.api._

object Main extends App {
  // Sample data
  val data = Seq((1, 10), (2, 20), (3, 30), (4, 40), (5, 50))

  // Convert the data into XY series
  val series = data.toXYSeries("Sample")

  // Create a chart
  val chart = XYLineChart(series)

  // Save the chart to a file
  chart.saveAsPNG("chart.png")

  // Display the chart
  chart.show()
}
