import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class TestActor extends Actor {
  override def receive: Receive = {
    case s: String => println(s" Received msg: $s")
  }
}

val system = ActorSystem("Test-System")

val actRef: ActorRef = system.actorOf(Props[TestActor], "TestActor")

actRef ! "Hi"

implicit val timeOut = Timeout(10 seconds)
val res: Future[Any] = actRef.ask("Hi")

