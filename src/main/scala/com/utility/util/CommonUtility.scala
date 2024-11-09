package com.utility.util

import java.math.BigInteger
import java.security.MessageDigest
import java.time.Instant
import java.util.UUID

object CommonUtility {


  def hash256String(input: String): String = {
    val md = MessageDigest.getInstance("SHA-256")
    val bytes = md.digest(input.getBytes("UTF-8"))
    val bigInt = new BigInteger(1, bytes)
    val hashValue = bigInt.toString(16)
    hashValue
  }

  def createCombinedHashKey(
                             assetUUID: UUID,
                             uuid: UUID,
                             expiry_time: Long,
                             payload: String,
                             actionClassUuid: UUID
                           ): String = {
    val combinedKey =
      uuid.toString + assetUUID.toString + actionClassUuid.toString + expiry_time.toString + payload
    hash256String(combinedKey)
  }

  def currentTimeInNanoSeconds(): Long = System.currentTimeMillis() * 1000000


  def toNano(instant: Instant) = instant.getEpochSecond * 1000000000 + instant.getNano

  def toMilliSeconds(instant: Instant) = instant.getEpochSecond * 1000 + instant.getNano / 1000000
}
