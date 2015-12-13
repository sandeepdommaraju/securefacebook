package client

import akka.actor.{Props, ActorRef, Actor}

case object LoginUsers
case object GetUserBasicDetails

/**
  * Created by sunito on 12/11/15.
  */
class Simulator( totalUsers: Int, ServerIP : String, ServerPort : Int, runTime : Int) extends Actor{

  val baseURL : String = "http://" + ServerIP + ":" + ServerPort + "/"

  var workerPool = new Array[ActorRef](totalUsers + 1)

  var i = 0

  for ( i <- 1 to totalUsers) {
    workerPool(i) = context.actorOf(Props(new ClientWorker(baseURL)), name="worker" + i)
  }


  def receive = {
    case LoginUsers => loginUsers
    //case GetUserBasicDetails => getUserDetails
    case default => "default msg"
  }

  def loginUsers = {
    var u = 0

    for (u <- 1 to totalUsers) {
      println("logging in actor: " + u)
      (workerPool(u) ! Login)
    }
  }

  /*def getUserDetails = {
    var u = 0
    for (u <- 1 to totalUsers) {
      println("logging in actor: " + u)
      (workerPool(u) ! GetUserBasicDetails)
    }
  }*/

}
