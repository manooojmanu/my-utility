import scala.annotation.tailrec

@tailrec
def search[T](str: T, list: List[T]): Option[T] = {

  list match {
    case Nil => None
    case head :: tail =>
      if (head == str)
        Some(str)
      else search(str, tail)
  }

}


search("Hi", List("Hello", "something", "Hi"))

val lists = List(List(1, 2, 3, 4), List(5, 6), List(5, 6, 7, 8, 9))

lists.sortBy(_.length)(Ordering[Int].reverse)
lists.flatten

case class Person(name: String, age: Int)

implicit val personOrdering: Ordering[Person] = Ordering.by(s => s).reverse