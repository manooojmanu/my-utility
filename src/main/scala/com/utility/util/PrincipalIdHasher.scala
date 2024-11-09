package com.utility.util

import com.roundeights.hasher.Digest.digest2string
import com.roundeights.hasher.Implicits._
import com.utility.util.HashType.HashType

private object HashType extends Enumeration {
  type HashType = Value

  val SHA1 = Value
  val SHA256 = Value
  val SHA512 = Value
  val MD5 = Value

}
object PrincipalIdHasher {
  import HashType._
  val sha1 = new PrincipalIdHasher(SHA1)
  val sha256 = new PrincipalIdHasher(SHA256)
  val sha512 = new PrincipalIdHasher(SHA512)
  val md5 = new PrincipalIdHasher(MD5)
}

class PrincipalIdHasher(hashType: HashType) {
  import HashType._
  implicit def hash(text: String): String = hashType match {
    case SHA1   => text.toLowerCase.sha1.toLowerCase
    case SHA256 => text.toLowerCase.sha256.toLowerCase
    case SHA512 => text.toLowerCase.sha512.toLowerCase
    case MD5    => text.toLowerCase.md5.toLowerCase

  }
}
