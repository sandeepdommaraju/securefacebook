
import java.security.MessageDigest

import akka.actor.ActorSystem
import akka.event.Logging
import akka.io.IO
import akka.pattern.ask
import common.UserDTO
import spray.can.Http
import spray.client.pipelining._
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import spray.util._

import scala.concurrent.duration._
import scala.io.Source
import scala.util.{Failure, Success}


//Json Parser
import net.liftweb.json.DefaultFormats
import net.liftweb.json.JsonParser._
import common.JsonImplicits._



object Main extends App {
  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("facebook-simple-spray-client")
  //  implicit val timeout = Timeout(30 seconds)

  import system.dispatcher

  // execution context for futures below
  val log = Logging(system, getClass)

  log.info("Requesting facebook info...")

  import SprayJsonSupport._


  println("i m starting")

  //val fileName = "src/main/resources/data.json"
  //val lines = Source.fromFile(fileName).mkString

  implicit val formats = DefaultFormats
  val pipeline = sendReceive ~> unmarshal[String]

    val newUser:UserDTO = new UserDTO( 1,  "sandom",  "sandeep", "dommaraju" , "Male" , "05-05-1988")

    val responseFuture = pipeline {
      Post("http://localhost:8080/users/save", newUser)
    }
    responseFuture onComplete{
      case Success(t) =>
        println("The user: " + t)

      case Success(somethingUnexpected) =>
        log.warning("The Facebook API call was successful but returned something unexpected: '{}'.", somethingUnexpected)

      case Failure(error) =>
        log.error(error, "Failure!!!")
    }
}