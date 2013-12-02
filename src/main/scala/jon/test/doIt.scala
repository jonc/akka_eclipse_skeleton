package jon.test
import akka.actor.ActorRef
import akka.actor.Actor
import akka.actor.Props


class Main extends Actor {

  def receive = {
    case count: Int => 
      println(s"count was ${count}")
    
    case _ => 
      println("another call?")
      context.stop(self)
  }
  
  val counter = context.actorOf(Props[Counter3], "george")
  
  counter ! "incr"
  counter ! "get"
  counter ! "incr"
  counter ! "incr"
  counter ! "get"
  //self ! "give up"

}

class Counter extends Actor {
  var count = 0

  def receive = {
    case "incr" => count += 1
    case ("get", customer: ActorRef) => customer ! count
  }
}

class Counter2 extends Actor {
  var count = 0

  def receive = {
    case "incr" => count += 1
    case "get" => sender ! count
  }
}

class Counter3 extends Actor {

  def counter(count: Int): Receive = {
    case "incr" => context.become(counter(count + 1))
    case "get" => sender ! count
  }

  def receive = counter(0)
}
