package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ChildActors extends App {

object Parent {
  case class CreateChild(name: String)
  case class ForwardToChild(message: String)
}

//  var child: ActorRef = null   => mutable state
  class Parent extends Actor {

  import Parent._
    override def receive: Receive = {
      case CreateChild(name) =>
        println(s"${self.path} creating child")
        val childRef = context.actorOf(Props[ChildActor], name)
//        child = childRef
        context.become(withChild(childRef))
    }

  def withChild(childRef: ActorRef): Receive = {
    case ForwardToChild(message) =>
      if(childRef != null)
        childRef forward message
  }
  }

  class ChildActor extends Actor {
    override def receive: Receive = {
      case message: String => println(s"${self.path} I got message: $message")
    }
  }

  import Parent._
  val system = ActorSystem("ParentChildDemo")
  val parent = system.actorOf(Props[Parent], "parent")
  parent ! CreateChild("child")
  parent ! ForwardToChild("Hey kid!")
}
