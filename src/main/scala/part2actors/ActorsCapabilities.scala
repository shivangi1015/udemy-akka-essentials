package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ActorsCapabilities extends App {

  class SimpleActor extends Actor {
    override def receive: Receive = {
      case "Hi" => context.sender() ! "Hello, there!"
      case message: String => println(s"${self}] I have received string: $message")
      case message: Int => println(s"[simple actor] I have received integer: $message")
      case SimpleMessage(content) => println(s"[simple actor] Received special message: $content")
      case SendMessageToItself(content) => self ! content
      case SayHiTo(ref) => ref ! "Hi"
      case WirelessPhoneMessage(content, ref) => ref forward(content + "$$$")
    }
  }

  val system = ActorSystem("ActorCapabilitiesDemo")
  val simpleActor = system.actorOf(Props[SimpleActor], "simpleActor")

  simpleActor ! "Hello! Actor!"

  //1 - he messages can be of any type -
  // a) IMMUTABLE
  // b) SERIALIZABLE
  //in practice use case class and case object

  simpleActor ! 1

  case class SimpleMessage(content: String)
  simpleActor ! SimpleMessage("SPECIALS!")

  //2 - actors have information about their context and them themselves
  //context.self == 'this' in OOP

  case class SendMessageToItself(content: String)

  simpleActor ! SendMessageToItself("I am an actor and I am proud of it!")

  //actors can reply to messages
  val alice = system.actorOf(Props[SimpleActor], "alice")
  val bob = system.actorOf(Props[SimpleActor], "bob")

  case class SayHiTo(contect: ActorRef)
  alice ! SayHiTo(bob)

  //dead letters
  alice ! "Hi"

  //forwarding messages
  // forwarding message with the original sender
  case class WirelessPhoneMessage(content: String, ref: ActorRef)
  alice ! WirelessPhoneMessage("hi", bob)
}
