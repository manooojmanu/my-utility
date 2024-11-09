package com.utility.interview

object MergeSortedArray extends App {
  def merge(array1: Array[Int], array2: Array[Int]) = {

    val mergedArray = Array.ofDim[Int](array1.length + array2.length)

    var i = 0
    var j = 0
    var k = 0

    while (i < array1.length && j < array2.length) {
      if (array1(i) < array2(j)) {
        mergedArray(k) = array1(i)
        i += 1
      } else {
        mergedArray(k) = array2(j)
        j += 1
      }
      k += 1
    }
    while (i < array1.length) {
      mergedArray(k) = array1(i)
      i += i
      k += 1
    }

    while (j < array2.length) {
      mergedArray(k) = array2(j)
      j += 1
      k += 1
    }
    mergedArray
  }
  val result = merge(Array(1, 5, 7), Array(3, 8, 9)).mkString(",")
  println(result)
}
