package com.utility.util

import org.scalacheck.rng.Seed
import org.scalacheck.{Arbitrary, ArbitraryDerivation, Gen}
import org.scalatest.OptionValues

import java.time._
import java.time.temporal.ChronoUnit
import java.util.UUID
import scala.util.Random

trait Randomization extends ArbitraryDerivation with OptionValues {

  private val dispatchPayload = "eyJ2ZXJzaW9ucyI6W3sibmFtZSI6ImFnZW50IiwidmVyc2lvbiI6ImFnZW50LWNvcmUtMS4yNS4xLWxpbnV4Iiwic2hhMjU2SGFzaCI6ImJkYzlhMzA0MzUyYzI4OTdkNjFiOTZhM2VjMWYzNWNhODQ0MzZlODM2M2I2MjEwMmNjMDhlOTdkY2NkMTI1ODEifSx7Im5hbWUiOiJhZ2VudHNoZWxsIiwidmVyc2lvbiI6ImFnZW50c2hlbGwtMS4yNS4xLWxpbnV4Iiwic2hhMjU2SGFzaCI6IjEwY2YyODkyNDU0OWM3NjExMTY5ZDYxYTliMTRkZGMxNTM5OWNkM2ViNDA1MjRlZGJlMmY1NTRhODczMWNjYTAifSx7Im5hbWUiOiJhZ2VudEpyZSIsInZlcnNpb24iOiJvcGVuamRrMTF1LWpyZV94NjRfbGludXhfaG90c3BvdF8xMS4wLjdfMTAtMSIsInNoYTI1Nkhhc2giOiIyY2ZiZWM2N2NkZDRmZjkwN2M1OTgyNmYxNGJhNDIyZTczNWUyOWY2NDZkZThmZWIzMTM1M2FhOWVhMjEyNTY0In0seyJuYW1lIjoiYWdlbnRTaGVsbEpyZSIsInZlcnNpb24iOiJvcGVuamRrMTF1LWpyZV94NjRfbGludXhfaG90c3BvdF8xMS4wLjdfMTAtMSIsInNoYTI1Nkhhc2giOiIyY2ZiZWM2N2NkZDRmZjkwN2M1OTgyNmYxNGJhNDIyZTczNWUyOWY2NDZkZThmZWIzMTM1M2FhOWVhMjEyNTY0In1dfQ"


  protected def randomInt: Int = randomInt()

  protected def randomNonZeroInt(max: Int = 10): Int = randomInt(1, max)

  protected def randomInt(min: Int = 0, max: Int = 10): Int = Random.nextInt(max - min) + min

  protected def randomString: String = randomString()

  protected def randomString(length: Int = 10): String =
    (Random.alphanumeric take length).mkString("")

  protected def randomUnicodeString: String = random[String]

  protected def randomNonEmptyUnicodeString: String = randomSuchThat[String](_.nonEmpty)

  protected def randomOptionString(x: Int = 10): Option[String] = Some(randomString(x))

  protected def randomUniqueString: String = UUID.randomUUID().toString

  protected def randomOptionUniqueString: Option[String] = Some(randomUniqueString)

  protected def randomUUID: UUID = UUID.randomUUID()

  protected def randomInstant: Instant = Instant.ofEpochSecond(Random.nextInt(Int.MaxValue).toLong)

  protected def randomOptionInstant: Option[Instant] = Some(randomInstant)

  protected def randomZonedDateTime: ZonedDateTime =
    ZonedDateTime.ofInstant(randomInstant, ZoneOffset.UTC)

  protected def randomOptionZonedDateTime: Option[ZonedDateTime] = Some(randomZonedDateTime)

  protected def randomDateInThePast: LocalDate = LocalDate.now.minusDays(randomInt().toLong + 1)

  protected def randomLocalDate: LocalDate = randomZonedDateTime.toLocalDate

  protected def randomLocalTime: LocalTime = randomZonedDateTime.toLocalTime

  protected def randomDateInTheFuture: LocalDate = LocalDate.now.plusDays(randomInt.toLong)

  protected def randomDateTimeInTheFuture: LocalDateTime =
    LocalDateTime.now.plusDays(randomInt.toLong)

  protected def randomPresentOrPastZonedDateTime: ZonedDateTime =
    ZonedDateTime.now.minusDays(randomInt.toLong).truncatedTo(ChronoUnit.MILLIS)

  protected def randomZonedDateTimeInTheFuture: ZonedDateTime = {
    ZonedDateTime.now.plusDays(randomInt(min = 1).toLong).truncatedTo(ChronoUnit.MILLIS)
  }

  protected def randomOptionLocalDate: Option[LocalDate] = Some(randomLocalDate)

  protected def randomPastZoneDateTme: ZonedDateTime =
    ZonedDateTime.now.minusDays(randomInt(2).toLong).truncatedTo(ChronoUnit.MILLIS)

  @deprecated(
    "Option like boolean indicate potential major branching paths which need to be tested explicitly. " +
      "This leads to fragile tests as people just rerun tests until they work. " +
      "Random.nextBoolean should also not be used for this same reason. " +
      "This now also defaults to Some so has no purpose",
    "2018-02-14"
  )
  protected def randomOption[T](block: => T): Option[T] =
    Some(block)

  protected def randomElementFromList[T](list: Iterable[T]): T = {
    if (list.isEmpty) throw new RuntimeException("cannot random select item from empty iterable")
    else Random.shuffle(list).head
  }

  protected def generateRandomListOf[T](elem: => T, min: Int = 5, max: Int = 10): List[T] = {
    Stream.continually(elem).take(randomInt(min, max)).toList
  }

  protected def random[T: Arbitrary]: T = randomSuchThat[T](_ => true)

  protected def randomSuchThat[T: Arbitrary](f: T => Boolean): T =
    implicitly[Arbitrary[T]].arbitrary
      .suchThat(f)
      .pureApply(Gen.Parameters.default, Seed.random(), 10000)
}
