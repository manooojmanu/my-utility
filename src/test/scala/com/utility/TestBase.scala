package com.utility

import com.typesafe.scalalogging.LazyLogging
import com.utility.model.json.BaseJsonProtocol
import com.utility.util.{Randomization, ScalaTestFutures}
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

trait TestBase extends AnyWordSpec with Randomization with MockFactory with Matchers with ScalaTestFutures with LazyLogging with BaseJsonProtocol {

}
