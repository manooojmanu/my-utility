package com.utility.interview

case class DoubleNode[T](var prev: Option[DoubleNode[T]], value: T, var next: Option[DoubleNode[T]])

class DudleyLinkedList[T] {
  var head: Option[DoubleNode[T]] = None
  var tail: Option[DoubleNode[T]] = None

  def insert(value: T): Unit = {
    val newNode = DoubleNode(None, value, head)

    // If there’s an existing head, set its previous pointer to the new node
    head.foreach(_.prev = Some(newNode))

    // Update the head to be the new node
    head = Some(newNode)

    // If the list was empty, also set the tail to be the new node
    if (tail.isEmpty) {
      tail = Some(newNode)
    }
  }

  def printList() = {
    var currentNode = head
    while (currentNode.isDefined) {
      print(currentNode.get.value)
      if (currentNode.get.next.isDefined) {
        print(" <-> ")
      }
      currentNode = currentNode.get.next
    }
  }
}


object DudleyLinkedList {
  def apply[T](elements: T*): DudleyLinkedList[T] = {
    val list = new DudleyLinkedList[T]
    elements.foreach(s => list.insert(s))
    list
  }
}

object Test extends App {
  val list = DudleyLinkedList((1 to 3): _*)
  list.printList()
}
