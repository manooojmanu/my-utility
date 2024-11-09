package com.utility.interview

case class Node[T](var data: T, var next: Option[Node[T]] = None) {

}

class LinkedList[T] {
  var head: Option[Node[T]] = None

  var last: Option[Node[T]] = None

  def insertHead(value: T): Unit = {
    val node = new Node[T](value, head)
    head = Some(node)
    last = node.next
  }


  def deleteAtHead() = {
    head match {
      case Some(node) => head = node.next
      case None => head
    }
  }

  def display(): Unit = {
    var current = head
    while (current.isDefined) {
      print(current.get.data + " -> ")
      current = current.get.next
    }
    println("None")
  }

}

object LinkedList {
  def apply[T](elements: T*): LinkedList[T] = {
    val list = new LinkedList[T]()
    elements.reverse.foreach {
      elm => list.insertHead(elm)
    }
    list
  }
}


object Test1 extends App {
  val list = LinkedList(1 to 3: _*)

  list.display()

  list.deleteAtHead()

  list.display()

}