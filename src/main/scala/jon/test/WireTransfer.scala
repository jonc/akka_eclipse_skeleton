package jon.test

import akka.actor.ActorRef
import akka.actor.Actor
import akka.event.LoggingReceive

class WireTransfer extends Actor {
	import WireTransfer._
	
	def receive = LoggingReceive {
	  case Transfer( from, to, amount ) =>
	    from ! BankAccount.Withdraw(amount)
	    context.become(awaitWithdraw(to, amount, sender))
	}
	
	def awaitWithdraw(to:ActorRef, amount:BigInt, client:ActorRef): Receive = LoggingReceive {
	  case BankAccount.Done =>
	    to ! BankAccount.Deposit(amount)
	    context become awaitDeposit(client)
	  case BankAccount.Failed =>
	    client ! Failed
	    context stop self
	}
	
	def awaitDeposit( client:ActorRef): Receive = LoggingReceive {
	  case BankAccount.Done =>
	    client ! Done
	    context stop self
	}
}

object WireTransfer {
  case class Transfer( from:ActorRef, to:ActorRef, amount:BigInt ) {
    require(amount > 0)
  }
  case object Done
  case object Failed
  
}