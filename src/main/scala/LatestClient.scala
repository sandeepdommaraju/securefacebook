package common

import java.security.MessageDigest
import Nodes.{Page, Profile}
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
import spray.json._

import scala.concurrent.duration._
import scala.io.Source
import scala.util.{Failure, Success}
import spray.httpx.marshalling._


//Json Parser
import net.liftweb.json.DefaultFormats
import net.liftweb.json.JsonParser._
import common.JsonImplicits._


object LatestClient extends App {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("facebook-simple-spray-client")
  //  implicit val timeout = Timeout(30 seconds)

  import system.dispatcher

  // execution context for futures below
  val log = Logging(system, getClass)
  log.info("Requesting facebook info...")

  import SprayJsonSupport._
  println("Client started")

  val jsonDirPath : String = "/home/sunito/Desktop/"
  implicit val formats = DefaultFormats
  val pipeline = sendReceive ~> unmarshal[String]

  val fileName = jsonDirPath + "userDTO.json" //"C:\\Users\\Samantha\\tmp\\userdto.json"
  val lines = Source.fromFile(fileName).mkString

  val credentials = parse(lines).extract[List[UserDTO]]
  //Post - Save User
  for (cred <- credentials) {

    import common.JsonImplicits._

    val newUser: UserDTO = new UserDTO(cred.id, cred.handle, cred.first_name, cred.last_name, cred.sex, cred.birthday)

    val responseFuture = pipeline {
      Post("http://localhost:8080/users/save", newUser)
    }
    responseFuture onComplete {
      case Success(t) =>
      //println("The user: " + t)

      case Success(somethingUnexpected) =>
        log.warning("The Facebook API call was successful but returned something unexpected: '{}'.", somethingUnexpected)

      case Failure(error) =>
        log.error(error, "Failure!!!")
    }

  }
  //Get User
  for (i <- 100000 to 100004) {

    import common.JsonImplicits._

    //val newUser: UserDTO = new UserDTO(cred.id, cred.handle, cred.first_name, cred.last_name, cred.sex, cred.birthday)

    val responseFuture = pipeline {
      Get("http://localhost:8080/users/" + i)
    }
    responseFuture onComplete {
      case Success(t) =>
      // println("Get:" + t)
      //val temp  = t.parseJson.asInstanceOf[JsObject]
      //  println (temp.getFields("handle").last)

      case Success(somethingUnexpected) =>
        log.warning("The Facebook API call was successful but returned something unexpected: '{}'.", somethingUnexpected)

      case Failure(error) =>
        log.error(error, "Failure!!!")
    }

  }

  //Profiles
  val pipeline1 = sendReceive ~> unmarshal[String]

  val profileName = jsonDirPath + "userProfileDTO.json" //"C:\\Users\\Samantha\\tmp\\userprofiledto.json"
  val profilelines = Source.fromFile(profileName).mkString

  val profilecredentials = parse(profilelines).extract[List[Profile]]


  for (cred <- profilecredentials) {

    import common.JsonImplicits._

    //val newprofile: UserProfileDTO = new UserProfileDTO(cred.id, cred.userOrPageId, cred.userOrPage, cred.description, cred.email, cred.pic)

    val responseFuture = pipeline1 {
      Post("http://localhost:8080/users/profile/save", cred)
    }
    responseFuture onComplete {
      case Success(t) =>
      // println("The user: " + t)

      case Success(somethingUnexpected) =>
        log.warning("The Facebook API call was successful but returned something unexpected: '{}'.", somethingUnexpected)

      case Failure(error) =>
        log.error(error, "Failure in saving UserProfile!!!")
    }
  }

  //Friends
  val pipeline2 = sendReceive ~> unmarshal[String]
  for (i <- 100001 to 100900) {

    //import common.JsonImplicits._
    //val frnd = i + 1
    val newfrnd1:FriendDTO = new FriendDTO(i+1,"myfrnd")
    val newfrnd2:FriendDTO = new FriendDTO(i+2,"myfrnd")
    val newfrnd3:FriendDTO = new FriendDTO(i+3,"myfrnd")

    val responseFuture = pipeline1 {
      Post("http://localhost:8080/users/friends/save/"+i, List(newfrnd1,newfrnd2,newfrnd3))
    }
    responseFuture onComplete {
      case Success(t) =>
      // println("Sent myFrnd: " + t)

      case Success(somethingUnexpected) =>
        log.warning("The Facebook API call was successful but returned something unexpected: '{}'.", somethingUnexpected)

      case Failure(error) =>
        log.error(error, "Failure in saving Frnds!!!")
    }
  }
  //Page
  val pipeline3 = sendReceive ~> unmarshal[String]

  val pageName = jsonDirPath + "pageDTO.json" //"C:\\Users\\Samantha\\tmp\\pagedto.json"
  val pagelines = Source.fromFile(pageName).mkString

  val pagecredentials = parse(pagelines).extract[List[PageDTO]]

  for (cred <- pagecredentials) {

    import common.JsonImplicits._

    //val newprofile: UserProfileDTO = new UserProfileDTO(cred.id, cred.userOrPageId, cred.userOrPage, cred.description, cred.email, cred.pic)
    println(cred)
    val responseFuture = pipeline1 {
      Post("http://localhost:8080/users/pages/save/"+cred.owner_user_id, cred)
    }
    responseFuture onComplete {
      case Success(t) =>
      //    println("The user: " + t)

      case Success(somethingUnexpected) =>
        log.warning("The Facebook API call was successful but returned something unexpected: '{}'.", somethingUnexpected)

      case Failure(error) =>
        log.error(error, "warning in saving Pages!!!")
    }
  }

  /*
    //User Posts
    val pipeline4 = sendReceive ~> unmarshal[String]

    val postName = "C:\\Users\\Samantha\\tmp\\userpost.json"
    val postlines = Source.fromFile(profileName).mkString

    val postcredentials = parse(postlines).extract[List[Post]]

    for (cred <- postcredentials) {

      import common.JsonImplicits._

      //val newprofile: UserProfileDTO = new UserProfileDTO(cred.id, cred.userOrPageId, cred.userOrPage, cred.description, cred.email, cred.pic)

      val responseFuture = pipeline1 {
        Post("http://localhost:8080/user/posts/save", cred)
      }
      responseFuture onComplete {
        case Success(t) =>
         println("The userposts: " + t)

        case Success(somethingUnexpected) =>
          log.warning("The Facebook API call was successful but returned something unexpected: '{}'.", somethingUnexpected)

        case Failure(error) =>
          log.error(error, "Failure in saving UserPosts!!!")
      }
    }*/

 // var post_pics_on_userProfileAlbum_Scheduler = system.scheduler.schedule(0 millisecond, 5 milliseconds)(postPicsOnUserProfileAlbum())


}