package client

import java.security.PublicKey
import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Props, ActorSystem}
import security.DigitalSignature
import service.GetUserBasicInfo

/**
  * Created by sunito on 12/11/15.
  */
object Client extends App {

  var serverIP : String = "localhost"
  var serverPort : Int = 8080

  val totalUsers : Int = 10
  val runTime : Int = 300 // in seconds

  var picIdGEN = new AtomicInteger(500000)
  var postIdGEN = new AtomicInteger(600000)


  val system = ActorSystem("SecureFacebookClient")

  val simulator = system.actorOf(Props(new Simulator(totalUsers, serverIP, serverPort, runTime)), name="SimulatorActor")

  simulator ! LoginUsers // send user public key, get server public key, add user basic info, send user profile, send page profile, get page profile

  var startTime = System.currentTimeMillis()
  while(System.currentTimeMillis() < startTime + 10*1000){

  }

  simulator ! AddFriends //add friends, add sharable, get sharable

  startTime = System.currentTimeMillis()
  while(System.currentTimeMillis() < startTime + 10*1000){

  }

  simulator ! AddAlbums //add album in user profile, get album

  simulator ! AddPicsInUserProfile //add pic in user-profile-album

  println("Finished Simulation")


  def uuid = {
    java.util.UUID.randomUUID.toString
  }

}
