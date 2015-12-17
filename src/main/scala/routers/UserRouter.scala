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

  implicit val timeout = Timeout(1500.millis)
  val userService = context.actorOf(Props[UserService], name = "userService")

  def receive = runRoute(userRoute)

  val userRoute = get {
    path("public-key" / IntNumber) {
      userId => {
        val f = (userService ? getUserPublicKey(userId)).mapTo[String].map(s => s"$s")
        complete(f)
      }
    }
  } ~
    post {
      path("public-key" / "save") {
        entity(as[String]) {
          pubKey => {
            val f = (userService ? AddPublicKey(pubKey)).mapTo[String].map(s => s"$s")
            complete(f)
          }
        }
      }
    } ~
    get {
      path("basic-details" / IntNumber) {
        userId => {
          //println("in router: GET basic-details: " + userId)
          val f = (userService ? GetUserBasicInfo(userId)).mapTo[String].map(s => s"$s")
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
                val f = (userService ? SaveUserBasicInfo(userId, msg.split("#sep#")(1))).mapTo[String].map(s => s"$s")
                complete(f)
                //"Posted user basic details: " + userId
              }
            }
          }
        }
      }
    } ~
    post {
      path("page" / "save" / IntNumber) {
        userId => {
          entity(as[String]) {
            msg => {
              authenticateUser(userId, msg) {
                val f = (userService ? SavePageAndPageProfile(userId, msg.split("#sep#")(1))).mapTo[String].map(s => s"$s")
                complete(f)
                //"Posted page details for:" + userId
              }
            }
          }
        }
      }
    } ~
    get {
      path("pageprofile" / IntNumber) {
        userId => {
          val f = (userService ? GetPageAndProfile(userId)).mapTo[String].map(s => s"${s}")
          complete(f)
        }
      }
    } ~
    get {
      path("add" / "friends" / IntNumber) {
        userId => {
          //println("in router: GET AddFriends: " + userId)
          val f = (userService ? AddFriends(userId)).mapTo[String].map(s => s"$s")
          complete(f)
        }
      }
    } ~
    get {
      path("sharable" / IntNumber) {
        userId => {
          //println("in router: GET AddFriends: " + userId)
          val f = (userService ? GetUserSharable(userId)).mapTo[String].map(s => s"$s")
          complete(f)
        }
      }
    } ~
    post {
      path("sharable" / "save" / IntNumber) {
        userId => {
          entity(as[String]) {
            msg => {
              authenticateUser(userId, msg) {
                val f = (userService ? SaveUserSharable(userId, msg.split("#sep#")(1))).mapTo[String].map(s => s"$s")
                complete(f)
              }
            }
          }
        }
      }
    } ~
    get {
      path("album" / IntNumber) {
        userId => {
          val f = (userService ? GetUserAlbum(userId)).mapTo[String].map(s => s"$s")
          complete(f)
        }
      }
    } ~
    post {
      path("album" / "save" / IntNumber) {
        userId => {
          entity(as[String]) {
            msg => {
              authenticateUser(userId, msg) {
                val f = (userService ? SaveUserAlbum(userId, msg.split("#sep#")(1))).mapTo[String].map(s => s"$s")
                complete(f)
                //"Posted Album on UserProfile: " + msg.split("#sep#")(1)
              }
            }
          }
        }
      }
    } ~
    post {
      path("page" / "album" / "save" / IntNumber) {
        userId => {
          entity(as[String]) {
            msg => {
              authenticateUser(userId, msg) {
                val f = (userService ? SavePageAlbum(userId, msg.split("#sep#")(1))).mapTo[String].map(s => s"$s")
                complete(f)
                //"Posted Album on PageProfile: for user: " + userId
              }
            }
          }
        }
      }
    } ~
    get {
      path("profile" / IntNumber) {
        userId => {
          val f = (userService ? GetProfile(userId)).mapTo[String].map(s => s"$s")
          complete(f)
        }
      }
    } ~
    post {
      path("profile" / "save" / IntNumber) {
        userId => {
          entity(as[String]) {
            msg =>
              authenticateUser(userId, msg) {
                val f = (userService ? SaveUserProfile(userId, msg.split("#sep#")(1))).mapTo[String].map(s => s"$s")
                complete(f)
                //"Saved UserProfile: " + userId
              }
          }
        }
      }
    }~
    get {
      path("profile" / "album" / "pics" / IntNumber) {
        userId => {
          val f = (userService ? GetPicsFromMyUserProfileAlbum(userId)).mapTo[String].map(s => s"$s")
          complete(f)
        }
      }
    } ~
  get {
    path("all-friends" / "profile" / "album" / "pics" / IntNumber) {
      userId => {
        val f = (userService ? GetAllFriendsPicsFromTheirUserProfileAlbum(userId)).mapTo[String].map(s => s"$s")
        complete(f)
      }
    }
  } ~
    post {
      path("profile" / "album" / "pic" / "save" / IntNumber / IntNumber) {
        (userId, picId) => {
          entity(as[String]) {
            msg =>
              authenticateUser(userId, msg) {
                val f = (userService ? SavePicInUserProfileAlbum(userId, picId, msg.split("#sep#")(1))).mapTo[String].map(s => s"$s")
                complete(f)
                //"Saved PicInUserProfileAlbum: " + userId
              }
          }
        }
      }
    }

}