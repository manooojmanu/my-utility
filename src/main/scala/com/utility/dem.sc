import scala.collection.immutable

val layouts: immutable.Seq[(List[Int], List[Int])] = List(
  (List(1, 2, 4),
    List(2, 4, 6)),
  (List(3, 6, 9),
    List(4, 5, 6))
)

val mappings = List((List(2, 4, 6), List(1, 2, 4)))


def isLayoutsContaindInMappings(layouts: List[Int], mappings: List[Int]): Boolean = {
  layouts.forall(mappings.contains)
}