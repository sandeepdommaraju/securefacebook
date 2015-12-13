package client

import akka.actor.{Props, ActorSystem}
import service.GetUserBasicInfo

/**
  * Created by sunito on 12/11/15.
  */
object Client extends App{

  var serverIP : String = "localhost"
  var serverPort : Int = 8080

  val totalUsers : Int = 3
  val runTime : Int = 300 // in seconds

  val system = ActorSystem("SecureFacebookClient")

  val simulator = system.actorOf(Props(new Simulator(totalUsers, serverIP, serverPort, runTime)), name="SimulatorActor")

  simulator ! LoginUsers

  //simulator ! CreateBasicProfiles

  /*var startTime = System.currentTimeMillis()
  while(System.currentTimeMillis() < startTime + 2*1000){

  }*/

  //simulator ! GetUserBasicDetails

}
