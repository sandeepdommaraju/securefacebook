import Nodes.User
import akka.actor.{Actor, ActorSystem, _}
import akka.util.Timeout
import common.UserDTO
import spray.httpx.SprayJsonSupport
import spray.json.AdditionalFormats
import scala.util.Failure
import scala.util.Success
import net.liftweb.json.DefaultFormats
import net.liftweb.json.JsonParser._
import SprayJsonSupport._


import scala.concurrent.duration._
import spray.client.pipelining._

/**
  * Created by sunito on 11/21/15.
  */
object TestClient extends App{

    val server_ip : String = "localhost" //args(0)

    val server_port : String = "8080" //args(1)

    //implicit val system = ActorSystem("SecureFacebook")

  implicit val formats = DefaultFormats

   // val clientWorker = system.actorOf(Props(new ClientWorker(server_ip, server_port)), "clientWorker" + 1)

    implicit val system = ActorSystem("ClientWorker")

    implicit val timeout = Timeout(500.millis)

    import system.dispatcher

    println("New Code !!!")

  import common.JsonImplicits._

    val pipeline = sendReceive ~> unmarshal[String]

    val newUser : UserDTO = new UserDTO(1, "kobe", "kobey", "bryant" , "Male" , "05-05-1988")

    val responseFuture = pipeline {
      Post("http://localhost:8080/users/save", newUser)
    }


  responseFuture onComplete {
    case Success(t) => println(t)

    case Success(somethingUnexpected) =>
      println("The Facebook API call was successful but returned something unexpected: '{}'.", somethingUnexpected)

    case Failure(error) =>
      println(error, "Failure!!!")

  }
}