import scala.annotation.tailrec
import scala.collection.JavaConverters._

// you can write to stdout for debugging purposes, e.g.
// println("this is a debug message")

object Solution {
  def solution(stack1: Array[Int], stack2: Array[Int], stack3: Array[Int]): String = {


    @tailrec
    def findSolution(list1: List[Int], list2:List[Int], list3:List[Int], acc:List[Int]):List[Int]={
      val allEmpty = List(list1.nonEmpty, list2.nonEmpty, list3.nonEmpty).forall(identity)

      if(allEmpty){
        acc
      } else {
        val list1Top = list1.headOption
        val list2Top = list2.headOption
        val list3Top = list3.headOption

        val max = List(list1Top, list2Top, list3Top).flatten.max


        if(list1Top.contains(max)){

          findSolution(list1.tail, list2, list3, acc :+ 1)
        } else if (list2Top.contains(max)){

          findSolution(list1, list2.tail, list3, acc :+ 2)
        } else {
          findSolution(list1, list2, list3.tail, acc :+ 3)
        }

      }
    }

   findSolution(stack1.toList, stack2.toList, stack3.toList, List.empty).mkString
  }
}
//[2, 7], [4, 5], [1]

Solution.solution(Array(2, 7), Array(4, 5), Array(1))
