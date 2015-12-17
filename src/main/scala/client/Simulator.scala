package client

import akka.actor.{Props, ActorRef, Actor}

case object LoginUsers
case object GetUserBasicDetails
case object AddFriends
case object AddPages
case object AddAlbums
case object AddPicsInUserProfile

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
    case AddFriends => addFriends
    case AddPages => addPages
    case AddAlbums => addAlbums
    case AddPicsInUserProfile => addPicsInUserProfile
    case default => "default msg"
  }

  def loginUsers = {
    var u = 0

    for (u <- 1 to totalUsers) {
      //println("logging in actor: " + u)
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

  def addFriends = {
    var u = 0
    for (u <- 1 to totalUsers){
      workerPool(u) ! AddFriends
    }
  }

  def addPages = {
    var u = 0
    for (u <- 1 to totalUsers){
      workerPool(u) ! AddPage
    }
  }

  def addAlbums = {
    var u = 0
    for (u <- 1 to totalUsers){
      workerPool(u) ! AddAlbum
    }
  }

  def addPicsInUserProfile = {
    var u = 0
    for (u <- 1 to totalUsers){
      workerPool(u) ! AddPicInUserProfile
    }
  }

}
