package com.utility.util

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.time.format.DateTimeFormatter

object InstantOps {

  implicit class InstantOps(instant: Instant) {
    def toEpochNanos: Long = instant.toEpochMilli * 1000000L

  }

  def parseLocal(localDateStr: String): Instant = {
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    // Parse the string to a LocalDateTime object
    val localDateTime = LocalDateTime.parse(localDateStr, formatter)

    localDateTime.toInstant(ZoneOffset.UTC)
  }

}
