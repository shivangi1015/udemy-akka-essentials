package part2actors

import akka.actor.{Actor, ActorSystem, Props}

object ActorsIntro extends App {

  //Part-1 Actor system
  val actorSystem = ActorSystem("firstActorSystem")
  println(actorSystem.name)

  //part-2 creating actors
  //word count actor

  class WordCountActor extends Actor {
    override def receive: Receive = {
      case msg: String => println("[word count] " + msg.split(" ").length)
      case _ => println("unknown message")
    }
  }

  //part-3 instantiate our actor
  val wordCountActor = actorSystem.actorOf(Props[WordCountActor], "wordCountActor")

  //part-4 communicate

  wordCountActor ! "I am learning akka"
  wordCountActor ! "A different message"

  object Person {
    def props(name: String) = Props(new Person(name))
  }
  class Person(name: String) extends Actor {
    override def receive: Receive = {
      case "hi" => println(s"Hi! My name is $name")
      case _ => println("Unknown message received!" )
    }
  }

  val personActor = actorSystem.actorOf(Person.props("Shivangi"), "shivangiPerson")
  personActor ! "hi"
  personActor ! "hello"


}
