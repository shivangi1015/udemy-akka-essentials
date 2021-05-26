package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ChildActorExercise extends App {

  //Distributed word counting

  /*
  Create WordCounterMaster
  Initialize(10) to wordCounterMaster
  send "Akka is awesome to wordCounterMaster
    wcm will send WordCountTask("...") to one of its children
      child replies with WordCountReply(3) to master
    master replies 3 to the sender

    requester -> wcm -> wcw
                 wcm <-

    Round robin logic
    1,2,3,4,5 and 7 tasks
   */
  object WordCounterMaster {
    case class Initialize(nChildren: Int)
    case class WordCountTask(id: Int, text: String)
    case class WordCountReply(id: Int, count: Int)
  }
  class WordCounterMaster extends Actor {

    import WordCounterMaster._

    var childRef: ActorRef = null
    override def receive: Receive = {
      case WordCountTask(id, text) => childRef ! WordCountTask(id, text)
      case Initialize(nChildren) =>
        println("[master] initialising.....")
        val childRefs = for (i <- 1 to nChildren) yield context.actorOf(Props[WordCounterWorker], s"wcw_$i")
        context.become(withChildren(childRefs, 0, 0, Map()))

        def withChildren(childRefs: Seq[ActorRef], currentChildIndex: Int, currentTaskIndex: Int, requestMap: Map[Int, ActorRef]): Receive = {
          case text: String =>
            println(s"[master] I have received a string $text - I will send it to child $currentChildIndex")
            val originalSender = sender()
            val wordCountTask = WordCountTask(currentTaskIndex, text)
            val childRef = childRefs(currentChildIndex)
            childRef ! wordCountTask
            val nextChildIndex = (currentChildIndex + 1) % childRefs.length
            println(s"[next child index check] $nextChildIndex")
            val newTaskId = currentTaskIndex + 1
            val newRequestMap = requestMap + (currentChildIndex -> originalSender)
            context.become(withChildren(childRefs, nextChildIndex, newTaskId, newRequestMap))
          case WordCountReply(id, count) =>
            //problem. sender() ?
            println(s"[master] I have received a reply for taskId $id with reply $count")
            val originalSender = requestMap(id)
            originalSender ! count
            context.become(withChildren(childRefs, currentChildIndex, currentTaskIndex, requestMap - id))
        }
    }
  }

  class WordCounterWorker extends Actor {
    import WordCounterMaster._

    override def receive: Receive = {
      case WordCountTask(id, text) =>
        println(s"[worker] I have received the task$id with text $text")
        sender() ! WordCountReply(id, text.split(" ").length)
    }
  }

  class TestActor extends Actor {
    import WordCounterMaster._

    override def receive: Receive = {
      case "go" =>
        val master = context.actorOf(Props[WordCounterMaster], "master")
        master ! Initialize(3)
        val texts = List("I love akka", "This program is great", "multithreading sucks", "I know!")
        texts.foreach(text => master ! text)
      case count: Int => println(s"[testActor] I have received a reply: $count")
    }
  }

  val system = ActorSystem("WordCounterRoundRobin")
  val testActor = system.actorOf(Props[TestActor], "testActor")
  testActor ! "go"
}
