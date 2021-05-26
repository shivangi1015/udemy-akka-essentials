package playground

import scala.concurrent.Future

object RunnableObject extends App {

  /**
   * OOP encapsulation is only valid in single threaded environment
   * @param amount
   */
  class BankAccount(private var amount: Int) {
    override def toString: String = "Amount: " + amount
    def withdraw(money: Int) = this.synchronized {
      this.amount -= money
    }
    def deposit(money: Int) = this.synchronized {
      this.amount += money
    }

    def getAmount = amount
  }

  val bank = new BankAccount(1000)

  for(_ <- 1 to 1000) {
    new Thread(() => bank.deposit(1)).start()
  }

  for(_ <- 1 to 1000) {
    new Thread(() => bank.withdraw(1)).start()
  }

  println(bank.getAmount)

  //OOP encapsulation is broken in multi threaded environment

  //syncronization ! Locks to the rescue
  //but causes deadlocks, live locks
  //so we need a data structure that will be fully encapsulation in a multithreaded env/distributed encv without the use of locks.

  /**
   * Tracing and dealing with errors in multithreaded env
   */

  //1M numbers in between 10 threads
/*
  val futures = (0 to 9)
    .map(i => i*1000 until 1000*(i+1))
    .map(range=> Future {

    })*/
}
