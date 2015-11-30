
import akka.actor.{Props, ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import common.JsonImplicits._
import common._
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
          userProfileRouter~
          friendRouter~
          pageRouter~
          pageProfileRouter~
          pagePostRouter

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

        lazy val friendRouter = {

          get {
            // get list of friends
            path("users" / "friends" / IntNumber) {
              id => {
                respondWithMediaType(MediaTypes.`application/json`) {
                  complete {
                    val friends : List[FriendDTO] = Await.result(workerList(0) ? getFriendList(id), timeout.duration).asInstanceOf[List[FriendDTO]]
                    val friendsJson = friends.toJson
                    friendsJson.toString()
                  }
                }
              }
            }
          } ~
          post {
            // save list of friends
            path("users" / "friends" / "save" / IntNumber) {
              id => {
                entity(as[List[FriendDTO]]) {
                  friends => {
                    val status = Await.result(workerList(0) ? saveFriendList(id, friends), timeout.duration).asInstanceOf[String]
                    complete {
                      status.toString
                    }

                  }
                }
              }
            }
          }
        }

        lazy val pageRouter = {

          get {
            path("users" / "pages" / IntNumber) {
              userId => {
                respondWithMediaType(MediaTypes.`application/json`) {
                  complete {
                    val pages = Await.result(workerList(0) ? getUserPages(userId), timeout.duration).asInstanceOf[List[PageDTO]]
                    val pagesJson = pages.toJson
                    pagesJson.toString()
                  }
                }
              }
            }
          } ~
            post {
              path("users" / "pages" / "save" / IntNumber) {
                userId => {
                  entity(as[PageDTO]) {
                    pageDTO => {

                      val status = Await.result(workerList(0) ? savePage(pageDTO), timeout.duration).asInstanceOf[String]
                      complete {
                        status.toString
                      }

                    }
                  }
                }
              }
            }
            /*delete {
              path("users" / "pages" / IntNumber) {
                pageId => {
                  complete {
                    val status = Await.result(workerList(0) ? deleteUser(id), timeout.duration).asInstanceOf[String]
                    status.toString
                  }
                }
              }
            }*/

        }

        lazy val pageProfileRouter = {

            get {
              path("pages" / "profile" / IntNumber) {
                pageId => {
                  respondWithMediaType(MediaTypes.`application/json`) {
                    complete {
                      val profile : Profile = Await.result(workerList(0) ? getPageProfile(pageId), timeout.duration).asInstanceOf[Profile]
                      val userProfileJson = profile.toJson
                      userProfileJson.toString()
                    }
                  }
                }
              }
            } ~
              post {
                path("pages" / "profile" / "save") {
                  entity(as[Profile]) {
                    profile =>  {

                      val status = Await.result(workerList(0) ? savePageProfile(Profile(profile.id, profile.userOrPageId, false, profile.description, profile.email, profile.pic)), timeout.duration).asInstanceOf[String]
                      complete {
                        status.toString
                      }

                    }
                  }
                }
              } ~
              delete {
                path("pages"/ "profile" / IntNumber) {
                  pageId => {
                    complete {
                      val status = Await.result(workerList(0) ? deletePageProfile(pageId), timeout.duration).asInstanceOf[String]
                      status.toString
                    }
                  }
                }
              }
          }

        lazy val pagePostRouter = {
          get {
            path("page" / "posts" / IntNumber) {
              pageId => {
                respondWithMediaType(MediaTypes.`application/json`) {
                  complete {
                    val posts : List[PostDTO] = Await.result(workerList(0) ? getPagePosts(pageId), timeout.duration).asInstanceOf[List[PostDTO]]
                    val postsJson = posts.toJson
                    postsJson.toString()
                  }
                }
              }
            }
          } ~
            post {
              path("page" / "posts" / "save" / IntNumber) {
                pageId => {
                  entity(as[List[PostDTO]]) {
                    posts => {

                      val status = Await.result(workerList(0) ? savePagePosts(pageId, posts), timeout.duration).asInstanceOf[String]
                      complete {
                        status.toString
                      }

                    }
                  }
                }
              }
            } ~
            delete {
              path("page" / "posts" / IntNumber) {
                pageId => {
                  complete {
                    val status = Await.result(workerList(0) ? deletePagePosts(pageId), timeout.duration).asInstanceOf[String]
                    status.toString
                  }
                }
              }
            }
        }

      }
}