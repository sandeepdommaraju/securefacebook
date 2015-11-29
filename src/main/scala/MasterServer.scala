
import akka.actor.{Props, ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import common.JsonImplicits._
import common.{UserProfileDTO, UserDTO}
import spray.http.MediaTypes
import spray.routing._
import spray.http.MediaTypes
import spray.routing.SimpleRoutingApp
import spray.json._
import Nodes.{Profile, Comment, User}

/**
  * Created by sunito on 11/22/15.
  */
object MasterServer extends SimpleRoutingApp {

      implicit val system = ActorSystem("SecureFacebook")

      implicit val timeout = Timeout(500.millis)

      var workerList : List[ActorRef] = List()

      var numberOfWorkers : Int = 1

      def main(args : Array[String]): Unit = {

        for (x <- 0 to numberOfWorkers - 1) {
          val worker = system.actorOf(Props(new Worker(system)), "myworker" + x)
          workerList = workerList :+ worker
        }

        for (worker <- workerList) {
          worker ! "init"
        }

        startServer(interface = "localhost", port = 8080) {

          userRouter~
          userProfileRouter

        }

        //var timer = system.actorOf(Props(new timerActor()), "")


        lazy val userRouter = {

          get {
            path("users" / IntNumber) {
              id => {
                respondWithMediaType(MediaTypes.`application/json`) {
                  complete {
                    val userDTO = Await.result(workerList(0) ? getUser(id), timeout.duration).asInstanceOf[UserDTO]
                    val userDTOJson = userDTO.toJson
                    userDTOJson.toString()
                  }
                }
              }
            }
          } ~
            post {
              path("users" / "save") {
                entity(as[User]) {
                  person =>  {

                      val status = Await.result(workerList(0) ? saveUser(UserDTO(person.id, person.handle, person.first_name, person.last_name, person.sex, person.birthday)), timeout.duration).asInstanceOf[String]
                      complete {
                        status.toString
                      }

                  }
                }
              }
            } ~
            delete {
              path("users" / IntNumber) {
                id => {
                  complete {
                    val status = Await.result(workerList(0) ? deleteUser(id), timeout.duration).asInstanceOf[String]
                    status.toString
                  }
                }
              }
            }
        }

        lazy val userProfileRouter = {
          get {
            path("users" / "profile" / IntNumber) {
              id => {
                respondWithMediaType(MediaTypes.`application/json`) {
                  complete {
                    val profile : Profile = Await.result(workerList(0) ? getUserProfile(id), timeout.duration).asInstanceOf[Profile]
                    val userProfileJson = profile.toJson
                    userProfileJson.toString()
                  }
                }
              }
            }
          } ~
            post {
              path("users" / "profile" / "save") {
                entity(as[Profile]) {
                  profile =>  {

                    val status = Await.result(workerList(0) ? saveUserProfile(Profile(profile.id, profile.userOrPageId, true, profile.description, profile.email, profile.pic)), timeout.duration).asInstanceOf[String]
                    complete {
                      status.toString
                    }

                  }
                }
              }
            } ~
            delete {
              path("users" / "profile" / IntNumber) {
                id => {
                  complete {
                    val status = Await.result(workerList(0) ? deleteUserProfile(id), timeout.duration).asInstanceOf[String]
                    status.toString
                  }
                }
              }
            }
        }


      }
}