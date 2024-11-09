package com.utility.util

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}

trait ScalaTestFutures extends ScalaFutures {
  override implicit val patienceConfig: PatienceConfig = super.patienceConfig.copy(timeout = scaled(Span(150, Seconds)))
}
