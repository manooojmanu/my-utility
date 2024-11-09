package org.scalacheck

import magnolia.{CaseClass, Magnolia, SealedTrait}

import scala.language.experimental.macros

trait ArbitraryDerivation {

  type Typeclass[T] = Arbitrary[T]

  def combine[T](ctx: CaseClass[Arbitrary, T]): Arbitrary[T] = Arbitrary {
    Gen.gen[T] { (params, seed) =>
      val seeds = Stream.iterate(seed)(_.next).iterator

      val result = ctx.construct { p =>
        p.typeclass.arbitrary.pureApply(params, seeds.next())
      }
      Gen.r(Some(result), seeds.next())
    }
  }

  def dispatch[T](ctx: SealedTrait[Arbitrary, T]): Arbitrary[T] = Arbitrary {
    for {
      n <- Gen.oneOf(ctx.subtypes)
      instance <- n.typeclass.arbitrary
    } yield instance

  }

  implicit def gen[T]: Arbitrary[T] = macro Magnolia.gen[T]
}

object ArbitraryDerivation extends ArbitraryDerivation
