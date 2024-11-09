import scala.annotation.tailrec

@tailrec
def reverse[T](s: List[T], acc: List[T] = Nil): List[T] = {
  s match {
    case Nil =>
      acc
    case head :: tail =>
      val res = head :: acc
      reverse(tail, res)
  }
}


def reverseStr(s: String) = {
  reverse(s.toCharArray.toList).mkString("")
}


reverseStr("abcdef")