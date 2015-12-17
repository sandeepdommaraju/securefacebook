package routers

/**
  * Created by sunito on 12/10/15.
  */


import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import service.{SavePicInUserProfileAlbum, GetProfile, ProfileService, SaveUserProfile}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class ProfileRouter extends AuthRouter {

  implicit val timeout = Timeout(500.millis)
  val profileService = context.actorOf(Props[ProfileService], name = "profileService")

  def receive = runRoute(profileRoute)

  val profileRoute = {

    get {
      path("user" / IntNumber) {
        userId => {
          val f = (profileService ? GetProfile(userId)).mapTo[String].map(s => s"$s")
          complete(f)
        }
      }
    } ~
      post {
          path("user" / "save" / IntNumber) {
            userId => {
              entity(as[String]) {
                msg =>
                  authenticateUser(userId, msg) {
                    val f = (profileService ? SaveUserProfile(userId, msg.split("#sep#")(1))).mapTo[String].map(s => s"$s")
                    complete(f)
                      //"Saved UserProfile: " + userId
                  }
              }
            }
          }
      }~
      post {
        path("user" / "album" / "pic" / "save" / IntNumber / IntNumber) {
          (userId, picId) => {
            entity(as[String]) {
              msg =>
                authenticateUser(userId, msg) {
                  val f = (profileService ? SavePicInUserProfileAlbum(userId, picId, msg.split("#sep#")(1))).mapTo[String].map(s => s"$s")
                  complete(f)
                    //"Saved PicInUserProfileAlbum: " + userId
                }
            }
          }
        }
      }
  }
}
