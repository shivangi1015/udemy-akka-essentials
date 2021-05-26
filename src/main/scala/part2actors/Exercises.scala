package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import part2actors.Exercises.BankAccount
import part2actors.Exercises.BankAccount.Deposit
import part2actors.Exercises.Counter.{Decrement, Increment, Print}
import part2actors.Exercises.Person.LiveTheLife

object Exercises extends App {

  /**
   * 1. Counter actor
   *  - Increment
   *  - Decrement
   *  - Print
   */

  //Domain of the counter
  object Counter {
    case object Increment
    case object Decrement
    case object Print
  }
  class Counter extends Actor {
    import Counter._
    var counter = 0
    override def receive: Receive = {
      case Increment => counter += 1
      case Decrement => counter -= 1
      case Print => println(s"My current counter is $counter")
    }
  }

  /*val system = ActorSystem("Counter")
  val counter = system.actorOf(Props[Counter], "counterActor")

  (1 to 10).foreach(_ => counter ! Increment)
  (1 to 5).foreach(_ => counter ! Decrement)
  counter ! Print*/

  /**
   * Bank account
   * receives -
   *  - Deposit an amount
   *  - Withdraw an amount
   *  - Statement
   *  replies with
   *  - Success
   *  - Failure
   *
   *  interact  with some other type of user
   */

  object BankAccount {
    case class Deposit(amount: Int)
    case class Withdraw(amount: Int)
    case object Statement

    case class TransactionSuccess(msg: String)
    case class TransactionFailure(msg: String)
  }
  class BankAccount extends Actor {
    import BankAccount._
    var funds = 0
    override def receive: Receive = {
      case Deposit(amount) =>
        if(amount < 0)
          sender() ! TransactionFailure("Insufficient deposit amount!")
        else {
          funds += amount
          sender() ! TransactionSuccess(s"Successfully deposited $amount in your account.")
        }
      case Withdraw(amount) =>
        if(amount <= 0)
          sender() ! TransactionFailure("Invalid withdraw amount.")
        else if(funds < amount)
          sender() ! TransactionFailure("Insufficient amount in the account!")
        else {
          funds -= amount
          sender() ! TransactionSuccess(s"Successfully withdrew amount $amount from your account.")
        }
      case Statement => sender() ! s"Your account contains $funds"
    }
  }

  val bankAccountSystem = ActorSystem("BankAccount")
  val bankAccount = bankAccountSystem.actorOf(Props[BankAccount], "BankAccountActor")

  bankAccount ! Deposit(0)

  object Person {
    case class LiveTheLife(account: ActorRef)
  }
  class Person extends Actor {
    import Person._
    import BankAccount._
    override def receive: Receive = {
      case LiveTheLife(account) =>
        account ! Deposit(10000)
        account ! Deposit(90000)
        account ! Withdraw(5000)
        account ! Withdraw(0)
        account ! Statement
      case message => println(message.toString)
    }
  }

  val person = bankAccountSystem.actorOf(Props[Person], "person")
  person ! LiveTheLife(bankAccount)
}
