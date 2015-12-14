package routers

import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import service._
import spray.routing.HttpServiceActor

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


/**
  * Created by sunito on 11/22/15.
  */

class UserRouter extends HttpServiceActor with AuthRouter {

  implicit val timeout = Timeout(500.millis)
  val userService = context.actorOf(Props[UserService], name = "userService")

  def receive = runRoute(userRoute)

  val userRoute = get {
    path("public-key" / IntNumber) {
      userId => {
        val f = (userService ? getUserPublicKey(userId)).mapTo[String].map(s => s"${s}")
        complete(f)
      }
    }
  } ~
    post {
      path("public-key" / "save") {
        entity(as[String]) {
          pubKey => {
            val f = (userService ? AddPublicKey(pubKey)).mapTo[String].map(s => s"${s}")
            complete(f)
          }
        }
      }
    } ~
    get {
        path("basic-details" / IntNumber) {
          userId => {
            println("in router: GET basic-details: " + userId)
            val f = (userService ? GetUserBasicInfo(userId)).mapTo[String].map(s => s"{$s}")
            complete(f)
          }
        }
    } ~
    post {
      path("basic-details" / "save" / IntNumber) {
        userId => {
          entity(as[String]) {
            msg => {
              authenticateUser(userId, msg) {
                complete {
                  userService ! SaveUserBasicInfo(userId, msg.split("#sep#")(1))
                  "Posted user basic details: " + userId
                }
              }
            }
          }
        }
      }
    }~
    post {
      path("page" / "save" / IntNumber) {
        userId => {
          entity(as[String]) {
            msg => {
              authenticateUser(userId, msg) {
                complete {
                  userService ! SavePageAndPageProfile(userId, msg.split("#sep#")(1))
                  "Posted page details for:" + userId
                }
              }
            }
          }
        }
      }
    }~
    get {
      path("add" / "friends" / IntNumber) {
        userId => {
          println("in router: GET AddFriends: " + userId)
          val f = (userService ? AddFriends(userId)).mapTo[String].map(s => s"${s}")
          complete(f)
        }
      }
    }~
    post {
      path("sharable" / "save" / IntNumber) {
        userId => {
          entity(as[String]) {
            msg => {
              authenticateUser(userId, msg) {
                complete {
                  userService ! SaveUserSharable(userId, msg.split("#sep#")(1))
                  "Posted user basic details: " + userId
                }
              }
            }
          }
        }
      }
    }
}