package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import part2actors.ActorBehaviorExercise.Counter.{Decrement, Increment, Print}

object ActorBehaviorExercise extends App {

  /**
   * 1 - recreate the counter actor with context.become and no mutable state
   */

  object Counter {
    case object Increment
    case object Decrement
    case object Print
  }

  class Counter extends Actor {
    override def receive: Receive = countReceiver(0)

    def countReceiver(currentCount: Int): Receive = {
      case Increment => context.become(countReceiver(currentCount + 1))
      case Decrement => context.become(countReceiver(currentCount - 1))
      case Print => println(s"[count] The current counter is $currentCount")
    }

  }

  import Counter._
  val system = ActorSystem("ActorSystem")
  /*val counter = system.actorOf(Props[Counter], "CounterActor")
  (1 to 20).foreach(_ => counter ! Increment)
  (1 to 10).foreach(_ => counter ! Decrement)
  counter ! Print*/

  /**
   * 2 - simplified voting system
   */

  case class Voter(candidate: String)
  case object VoteStatusRequest
  case class VoteStatusReply(candidate:Option[String])
  class Citizen extends Actor {
    override def receive: Receive = ???
  }
/*
  case class AggregateVotes(citizens: Set[ActorRef])
  class VoteAggregator extends Actor {
    override def receive: Receive = ???
  }

  val alice = system.actorOf(Props[Citizen])
  val bob = system.actorOf(Props[Citizen])
  val charlie = system.actorOf(Props[Citizen])
  val daniel = system.actorOf(Props[Citizen])

  alice !*/
}
