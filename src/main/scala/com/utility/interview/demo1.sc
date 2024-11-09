val list: List[Int] = List(1,  2, 3, 4, 2, 6, 8, 3)


def findTarget(list: List[Int], target: Int, index: Int = -1, noOccorance: Map[Int, Int] = Map.empty): Option[Int] = {

  list match {
    case Nil if noOccorance.contains(target) && noOccorance(target) == 2 => Some(index)
    case Nil => None
    case head :: tail =>
      if (noOccorance.contains(target) && noOccorance(target) == 2) {
        Some(index)
      } else {
        val updated = noOccorance.updated(head, noOccorance.get(head).map(_ + 1).getOrElse(1))
        findTarget(tail, target, index + 1, updated)
      }
  }

}

findTarget(list, 3)

def add(i:Int)(j:Int) = i +j

val addI:Int => Int = add(1)

addI(2)