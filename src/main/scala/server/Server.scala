package server

import java.security.KeyPair
import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{ActorSystem, Props}
import akka.routing._
import akka.util.Timeout
import routers._
import security.RSA
import spray.routing.SimpleRoutingApp

import scala.concurrent.duration._

/**
  * Created by sunito on 12/10/15.
  */
object Server extends  SimpleRoutingApp with RSA{

  implicit val system = ActorSystem("SecureFacebookServer")

  val userRouter = system.actorOf(Props[UserRouter].withRouter(RoundRobinPool(1)))
  val profileRouter = system.actorOf(Props[ProfileRouter].withRouter(RoundRobinPool(1)))
  /*val pageRouter = system.actorOf(Props[PageRouter].withRouter(RoundRobinPool(5)))
  val friendRouter = system.actorOf(Props[FriendRouter].withRouter(RoundRobinPool(5)))
  val postRouter = system.actorOf(Props[PostRouter].withRouter(RoundRobinPool(5)))
  val albumRouter = system.actorOf(Props[AlbumRouter].withRouter(RoundRobinPool(5)))
  val picRouter = system.actorOf(Props[PicRouter].withRouter(RoundRobinPool(5)))
  val commentRouter = system.actorOf(Props[CommentRouter].withRouter(RoundRobinPool(5)))
  val likeRouter = system.actorOf(Props[LikeRouter].withRouter(RoundRobinPool(5)))
  val shareRouter = system.actorOf(Props[ShareRouter].withRouter(RoundRobinPool(5)))
  val activityRouter = system.actorOf(Props[ActivityRouter].withRouter(RoundRobinPool(5)))*/

  implicit val timeout = Timeout(5000.millis)

  var keyPair: KeyPair = null
  var pubKey: String = null

  var userIdGEN = new AtomicInteger(100000)
  var pageIdGEN = new AtomicInteger(200000)
  var profileIdGEN = new AtomicInteger(300000)

  def main(args: Array[String]) {

    println("Starting SecureFacebookServer !!")

    keyPair = getKeyPair
    pubKey = encodeBASE64(keyPair.getPublic.getEncoded)


    startServer(interface = "localhost", port=8080) {

      pathPrefix("user") { rte => userRouter ! rte }~
      pathPrefix("profile") { rte => profileRouter ! rte }
      /*pathPrefix("page") { rte => pageRouter ! rte }~
      pathPrefix("friend") { rte => friendRouter ! rte }~
      pathPrefix("post") { rte => postRouter ! rte }~
      pathPrefix("album") { rte => albumRouter ! rte }~
      pathPrefix("pic") { rte => picRouter ! rte }~
      pathPrefix("comment") { rte => commentRouter ! rte }~
      pathPrefix("like") { rte => likeRouter ! rte }~
      pathPrefix("share") { rte => shareRouter ! rte }~
      pathPrefix("activity") { rte => activityRouter ! rte }*/
    }

  }
}
