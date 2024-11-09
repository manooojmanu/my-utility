package com.utility

import scala.annotation.tailrec
import scala.collection.JavaConverters._

// you can write to stdout for debugging purposes, e.g.
// println("this is a debug message")

object Solution {
  def solution(stack1: Array[Int], stack2: Array[Int], stack3: Array[Int]): String = {


    @tailrec
    def findSolution(list1: List[Int], list2: List[Int], list3: List[Int], acc: List[Int]): List[Int] = {
      val allEmpty = List(list1.isEmpty, list2.isEmpty, list3.isEmpty).forall(identity)

      if (allEmpty) {
        acc
      } else {
        val list1Top = list1.lastOption
        val list2Top = list2.lastOption
        val list3Top = list3.lastOption

        val max = List(list1Top, list2Top, list3Top).flatten.max

        if (list1Top.contains(max)) {

          findSolution(list1.init, list2, list3, acc :+ 1)
        } else if (list2Top.contains(max)) {

          findSolution(list1, list2.init, list3, acc :+ 2)
        } else {
          findSolution(list1, list2, list3.init, acc :+ 3)
        }

      }
    }

    findSolution(stack1.toList, stack2.toList, stack3.toList, List.empty).mkString
  }
}
//[2, 7], [4, 5], [1]


object TestApp extends App {

  println("Solution---> " + Solution.solution(Array(2, 7), Array(4, 5), Array(1)))
}
