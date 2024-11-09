package com.utility.model.json

import spray.json.NullOptions

trait BaseJsonProtocol extends UUIDJsonProtocol with AnyJsonProtocol with NullOptions

object BaseJsonProtocol extends BaseJsonProtocol