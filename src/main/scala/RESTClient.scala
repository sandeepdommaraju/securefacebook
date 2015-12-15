/**
  * Created by sunito on 11/29/15.
  */

import java.security.MessageDigest
import java.util.concurrent.atomic.AtomicInteger

import Nodes.Profile
import akka.actor.{Cancellable, ActorSystem}
import akka.event.Logging
import akka.io.IO
import akka.pattern.ask
import common._
import spray.can.Http
import spray.client.pipelining._
import spray.httpx.SprayJsonSupport
import spray.json.{JsObject, DefaultJsonProtocol}
import spray.util._

import scala.concurrent.duration._
import scala.io.Source
import scala.util.{Failure, Success}


//Json Parser
import net.liftweb.json.DefaultFormats
import net.liftweb.json.JsonParser._
import common.JsonImplicits._



object RESTClient extends App {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("Facebook-RESTclient")
  //  implicit val timeout = Timeout(30 seconds)

  import system.dispatcher

  // execution context for futures below
  val log = Logging(system, getClass)

  log.info("Requesting Securefacebook ...")

  import SprayJsonSupport._

  val jsonDirPath : String = "/home/sunito/Desktop/"
  implicit val formats = DefaultFormats

  val pipeline = sendReceive ~> unmarshal[String]

  post_Users()                  //userRouter~
  //get_Users()

  post_UserProfiles()           //userProfileRouter~
  get_UserProfiles()

  //post_Friends()                //friendRouter~
  //get_Friends()

  post_Pages()                  //pageRouter~
  get_Pages()

  post_PageProfiles()           //pageProfileRouter~
  get_PageProfiles()

  //post_PagePosts()              //pagePostRouter~
  // get_PagePosts()

  //post_UserPosts()              //userPostRouter~
  //get_UserPosts()

  post_UserProfileAlbums()      //albumRouter~
  //get_UserProfileAlbums()

  //post_UserProfileAlbumPics()   //userAlbumPicRouter~
  // get_UserProfileAlbumPics()

  /*post_UserPostComments()       //userPostCommentRouter
  get_UserPostComments()
  */

  /**
    * Load initial data : Users, UserProfiles, Pages, PageProfiles, Friends
    */

  /**
    * Start Simulation
    *
    * operations during simulation:
    *  1. Active users (1 to 1000) post on their wall
    *  2. Active users (1000 to 1500) post pics on their albums
    *  3. Active users (1 to 1000) get posts on their wall
    *  4. Active users (1000 to 1500) get pics on their albums
    *  5. Active users (1 to 1000) post comments on their posts
    */

  startSimulation()

  stopSimulation()

  var WritePostOnWallCancel:Cancellable = new Cancellable {override def isCancelled: Boolean = false
    override def cancel(): Boolean = false
  }

  var WritePostOnPageCancel:Cancellable = new Cancellable {override def isCancelled: Boolean = false
    override def cancel(): Boolean = false
  }

  var WritePicInAlbumCancel:Cancellable = new Cancellable {override def isCancelled: Boolean = false
    override def cancel(): Boolean = false
  }

  var ReadPostsonWallCancel:Cancellable = new Cancellable {override def isCancelled: Boolean = false
    override def cancel(): Boolean = false
  }

  var ReadPostsOnPageCancel:Cancellable = new Cancellable {override def isCancelled: Boolean = false
    override def cancel(): Boolean = false
  }

  var ReadPicsInAlbumCancel:Cancellable = new Cancellable {override def isCancelled: Boolean = false
    override def cancel(): Boolean = false
  }

  var WritePostOnWallCancel_m:Cancellable = new Cancellable {override def isCancelled: Boolean = false
    override def cancel(): Boolean = false
  }

  var WritePostOnPageCancel_m:Cancellable = new Cancellable {override def isCancelled: Boolean = false
    override def cancel(): Boolean = false
  }

  var WritePicInAlbumCancel_m:Cancellable = new Cancellable {override def isCancelled: Boolean = false
    override def cancel(): Boolean = false
  }

  var ReadPostsonWallCancel_m:Cancellable = new Cancellable {override def isCancelled: Boolean = false
    override def cancel(): Boolean = false
  }

  var ReadPostsOnPageCancel_m:Cancellable = new Cancellable {override def isCancelled: Boolean = false
    override def cancel(): Boolean = false
  }

  var ReadPicsInAlbumCancel_m:Cancellable = new Cancellable {override def isCancelled: Boolean = false
    override def cancel(): Boolean = false
  }


  def stopSimulation(): Unit = {
    var runTime = 4*60
    val startTime = System.currentTimeMillis()
    while(System.currentTimeMillis() < startTime + runTime*1000){

    }
    cancelAll()
  }

  def cancelAll() = {
    WritePostOnWallCancel.cancel()
    WritePostOnWallCancel.cancel()
    WritePostOnWallCancel.cancel()

    ReadPostsonWallCancel.cancel()
    ReadPostsOnPageCancel.cancel()
    ReadPicsInAlbumCancel.cancel()
  }


  def startSimulation() = {
    //val min_id : Int = 100000
    //val max_id : Int = 100500
   // var activeUsers_write_posts_on_wall = system.scheduler.schedule(0 millisecond, 5 milliseconds)(writePosts())


    println("SIMULATION START")
    println("SIMULATION START")
    println("SIMULATION START")
    println("SIMULATION START")
    println("SIMULATION START")


    for (activeUserId <- 100000 to 100250) {

      WritePostOnWallCancel = system.scheduler.schedule(1 second, 5 seconds)(f = writePostOnWall(activeUserId))
      WritePostOnPageCancel = system.scheduler.schedule(2 seconds, 10 seconds) (f = writePostOnPage(activeUserId))
      //WritePicInAlbumCancel = system.scheduler.schedule(3 milliseconds, 30 milliseconds) (writePicInAlbum(activeUserId))

      ReadPostsonWallCancel = system.scheduler.schedule(2 seconds, 3 seconds) (readPostsonWall(activeUserId))
      ReadPostsOnPageCancel = system.scheduler.schedule(5 seconds, 5 seconds) (readPostsOnPage(activeUserId))
      //ReadPicsInAlbumCancel = system.scheduler.schedule(6 milliseconds, 25 milliseconds) (readPicsInAlbum(activeUserId))

    }

    for (activeUserId <- 100500 to 100750) {

      WritePostOnWallCancel_m = system.scheduler.schedule(1 second, 5 seconds)(f = writePostOnWall(activeUserId))
      WritePostOnPageCancel_m = system.scheduler.schedule(2 seconds, 10 seconds) (f = writePostOnPage(activeUserId))
      //WritePicInAlbumCancel = system.scheduler.schedule(3 milliseconds, 30 milliseconds) (writePicInAlbum(activeUserId))

      ReadPostsonWallCancel_m = system.scheduler.schedule(2 seconds, 3 seconds) (readPostsonWall(activeUserId))
      ReadPostsOnPageCancel_m = system.scheduler.schedule(5 seconds, 5 seconds) (readPostsOnPage(activeUserId))
      //ReadPicsInAlbumCancel = system.scheduler.schedule(6 milliseconds, 25 milliseconds) (readPicsInAlbum(activeUserId))

    }


  }

  def writePostOnWall( userId : Int) = {
    val postDTO : PostDTO = new PostDTO(userId + 310000, userId, false, "post of user on wall: " + userId )
    val responseFuture = pipeline {
      println("SIMULATION:--------------- write")
      Post("http://localhost:8080/user/posts/save/" + userId, List(postDTO))
    }
    responseFuture onComplete {
      case Success(t) =>
        println("SIMULATION: User Wrote on Wall: " + t)

      case Success(somethingUnexpected) =>
        log.warning("Something unexpected in MasterServer: '{}'.", somethingUnexpected)

      case Failure(error) =>
        log.error(error, "Failure in posting WallPost SIMULATION!!!")
    }
  }

  def writePostOnPage(userId : Int) = {
      println("SIM: write post on page")

    val postId = post_id_gen.addAndGet(1)
    val postDTO : PostDTO = new PostDTO(postId, userId, false, "post of user on his page: " + userId )
    val responseFuture = pipeline {
      println("SIMULATION:--------------- write")
      Post("http://localhost:8080/page/posts/save/" + (userId + 200000), List(postDTO))
    }
    responseFuture onComplete {
      case Success(t) =>
        println("SIMULATION: User Wrote on Page: " + t)

      case Success(somethingUnexpected) =>
        log.warning("Something unexpected in MasterServer: '{}'.", somethingUnexpected)

      case Failure(error) =>
        log.error(error, "Failure in posting PAge SIMULATION!!!")
    }


  }

  def writePicInAlbum( userId : Int) = {
    println("SIM: write Pic in album")
  }

  def readPostsonWall( userId : Int) = {
    val responseFuture = pipeline {
      Get("http://localhost:8080/user/posts/" + userId)
    }
    responseFuture onComplete {
      case Success(t) =>
        println("SIMULATION: Get UserWallPosts:" + t)

      case Success(somethingUnexpected) =>
        log.warning("SIMULATION: Something unexpected in MasterServer: '{}'.", somethingUnexpected)

      case Failure(error) =>
        log.error(error, "SIMULATION: Failure in getting userWallPosts: " + userId)
    }

  }

  def readPostsOnPage( userId : Int) = {
    println("SIM: read post on page")
    val responseFuture = pipeline {
      Get("http://localhost:8080/page/posts/" + (userId + 200000))
    }
    responseFuture onComplete {
      case Success(t) =>
        println("SIMULATION: Get UserWallPosts:" + t)

      case Success(somethingUnexpected) =>
        log.warning("SIMULATION: Something unexpected in MasterServer: '{}'.", somethingUnexpected)

      case Failure(error) =>
        log.error(error, "SIMULATION: Failure in getting userWallPosts: " + userId)
    }
  }

  def readPicsInAlbum( userId : Int) = {
    println("SIM: read pic on album")
  }




  val active_users_wallposts_atomic_gen = new AtomicInteger(410000)
  val active_users_atomic_gen = new AtomicInteger(100000)

  def writePosts() = {
    val userId : Int = active_users_atomic_gen.addAndGet(1)
    val postDTO : PostDTO = new PostDTO(active_users_wallposts_atomic_gen.addAndGet(1), userId, false, "blah blah post by user: " + userId)
    println("writing post: " + postDTO)
    Post("http://localhost:8080/user/posts/save/" + userId, List(postDTO))
  }

  val post_id_gen = new AtomicInteger(1000)
  val read_post_id_gen = new AtomicInteger(1000)


  def post_Users() = {

    //val pipeline = sendReceive ~> unmarshal[String]

    val fileName = jsonDirPath + "userDTO.json" //"C:\\Users\\Samantha\\tmp\\userdto.json"
    val lines = Source.fromFile(fileName).mkString

    val userObjs = parse(lines).extract[List[UserDTO]]
    //Post - Save User
    for (cred <- userObjs) {

      import common.JsonImplicits._

      val newUser: UserDTO = new UserDTO(cred.id, cred.handle, cred.first_name, cred.last_name, cred.sex, cred.birthday)

      val responseFuture = pipeline {
        Post("http://localhost:8080/users/save", newUser)
      }
      responseFuture onComplete {
        case Success(t) =>
         //println("The user: " + t)

        case Success(somethingUnexpected) =>
          log.warning("Something unexpected in MasterServer: '{}'.", somethingUnexpected)

        case Failure(error) =>
          log.error(error, "Failure in posting UserBasicDetails!!!")
      }

    }


  }


  def get_Users() = {

    //val pipeline = sendReceive ~> unmarshal[String]

    //Get User
    for (i <- 100000 to 100004) {

      import common.JsonImplicits._

      //val newUser: UserDTO = new UserDTO(cred.id, cred.handle, cred.first_name, cred.last_name, cred.sex, cred.birthday)

      val responseFuture = pipeline {
        Get("http://localhost:8080/users/" + i)
      }
      responseFuture onComplete {
        case Success(t) =>
          println("Get User:" + t)
          //val temp  = t.parseJson.asInstanceOf[JsObject]
          //println (temp.getFields("handle").last)

        case Success(somethingUnexpected) =>
          log.warning("Something unexpected in MasterServer:: '{}'.", somethingUnexpected)

        case Failure(error) =>
          log.error(error, "Failure in getting UserBasicDetails!!!")
      }

    }

  }


  def post_UserProfiles() = {

    //val pipeline1 = sendReceive ~> unmarshal[String]

    val profileName = jsonDirPath + "userProfileDTO.json" //"C:\\Users\\Samantha\\tmp\\userprofiledto.json"
    val lines = Source.fromFile(profileName).mkString

    val userProfiles = parse(lines).extract[List[Profile]]


    for (userProfile <- userProfiles) {

      import common.JsonImplicits._


      val responseFuture = pipeline {
        Post("http://localhost:8080/users/profile/save", userProfile)
      }
      responseFuture onComplete {
        case Success(t) =>
           //println("The userprofile: " + t)

        case Success(somethingUnexpected) =>
          log.warning("Something unexpected in MasterServer:: '{}'.", somethingUnexpected)

        case Failure(error) =>
          log.error(error, "Failure in saving UserProfile!!!")
      }
    }

  }

  def get_UserProfiles() = {

    //val pipeline = sendReceive ~> unmarshal[String]

    for (userId <- 100000 to 100004) {

      import common.JsonImplicits._

      val responseFuture = pipeline {
        Get("http://localhost:8080/users/profile/" + userId)
      }
      responseFuture onComplete {
        case Success(t) =>
          println("Get UserProfile:" + t)

        case Success(somethingUnexpected) =>
          log.warning("Something unexpected in MasterServer: '{}'.", somethingUnexpected)

        case Failure(error) =>
          log.error(error, "Failure in getting UserProfile!!!")
      }

    }
  }


  def post_Friends() = {

    //val pipeline2 = sendReceive ~> unmarshal[String]
    for (i <- 100001 to 100900) {

      //import common.JsonImplicits._
      //val frnd = i + 1
      val newfrnd1:FriendDTO = new FriendDTO(i+1,"myfrnd")
      val newfrnd2:FriendDTO = new FriendDTO(i+2,"myfrnd")
      val newfrnd3:FriendDTO = new FriendDTO(i+3,"myfrnd")

      val responseFuture = pipeline {
        Post("http://localhost:8080/users/friends/save/"+i, List(newfrnd1,newfrnd2,newfrnd3))
      }
      responseFuture onComplete {
        case Success(t) =>
          //println("Sent myFrnd: " + t)

        case Success(somethingUnexpected) =>
          log.warning("Something unexpected in MasterServer: '{}'.", somethingUnexpected)

        case Failure(error) =>
          log.error(error, "Failure in saving Frnds!!!")
      }
    }

  }

  def get_Friends() = {
    //val pipeline = sendReceive ~> unmarshal[String]

    for (userId <- 100001 to 100004) {

      import common.JsonImplicits._

      val responseFuture = pipeline {
        Get("http://localhost:8080/users/friends/" + userId)
      }
      responseFuture onComplete {
        case Success(t) =>
          println("Get Friends:" + t)

        case Success(somethingUnexpected) =>
          log.warning("Something unexpected in MasterServer: '{}'.", somethingUnexpected)

        case Failure(error) =>
          log.error(error, "Failure in getting Friends: " + userId)
      }

    }
  }

  def post_Pages() = {

    //val pipeline = sendReceive ~> unmarshal[String]

    val pageName = jsonDirPath + "pageDTO.json" //"C:\\Users\\Samantha\\tmp\\pagedto.json"
    val pagelines = Source.fromFile(pageName).mkString

    val pageObjs = parse(pagelines).extract[List[PageDTO]]


    for (pageObj <- pageObjs) {


        import common.JsonImplicits._

        val responseFuture = pipeline {
          Post("http://localhost:8080/users/pages/save/" + pageObj.owner_user_id, pageObj)
        }
        responseFuture onComplete {
          case Success(t) =>
          //println("The user: " + t)

          case Success(somethingUnexpected) =>
            log.warning("The Facebook API call was successful but returned something unexpected: '{}'.", somethingUnexpected)

          case Failure(error) =>
            log.error(error, "warning in saving Pages!!!")
        }
      }
  }

  def get_Pages() = {
    //val pipeline = sendReceive ~> unmarshal[String]

    for (userId <- 100001 to 100004) {

      import common.JsonImplicits._

      val responseFuture = pipeline {
        Get("http://localhost:8080/users/pages/" + userId)
      }
      responseFuture onComplete {
        case Success(t) =>
          println("Get Pages:" + t)

        case Success(somethingUnexpected) =>
          log.warning("Something unexpected in MasterServer: '{}'.", somethingUnexpected)

        case Failure(error) =>
          log.error(error, "Failure in getting PageBasicDetails: " + userId)
      }

    }
  }

  def post_PageProfiles() = {
    //val pipeline3 = sendReceive ~> unmarshal[String]

    val pageName = jsonDirPath + "pageProfileDTO.json" //"C:\\Users\\Samantha\\tmp\\pagedto.json"
    val pagelines = Source.fromFile(pageName).mkString

    val profileObjs = parse(pagelines).extract[List[Profile]]


    for (profileObj <- profileObjs) {

      import common.JsonImplicits._


        println("saving pageProfile: " + profileObj)

        val responseFuture = pipeline {
          Post("http://localhost:8080/pages/profile/save", profileObj)
        }
        responseFuture onComplete {
          case Success(t) =>
            println("post PageProfile: " + t)

          case Success(somethingUnexpected) =>
            log.warning("Something unexpected in MasterServer: '{}'.", somethingUnexpected)

          case Failure(error) =>
            log.error(error, "Error in saving PageProfiles!!!")
        }
      }
  }

  def get_PageProfiles() = {
    //val pipeline = sendReceive ~> unmarshal[String]

    for (pageId <- 300000 to 300001) {

      import common.JsonImplicits._

      val responseFuture = pipeline {
        Get("http://localhost:8080/pages/profile/" + pageId)
      }
      responseFuture onComplete {
        case Success(t) =>
          println("Get PageProfile:" + t)

        case Success(somethingUnexpected) =>
          log.warning("Something unexpected in MasterServer: '{}'.", somethingUnexpected)

        case Failure(error) =>
          log.error(error, "Failure in getting PageProfiles: " + pageId)
      }

    }
  }

  def post_PagePosts() = {

    val pageName = jsonDirPath + "pagePost.json" //"C:\\Users\\Samantha\\tmp\\pagedto.json"
    val pagelines = Source.fromFile(pageName).mkString

    val postObjs = parse(pagelines).extract[List[PostDTO]]


    for (postObj <- postObjs) {

      import common.JsonImplicits._



        println("saving pagePost: " + postObj)

        val responseFuture = pipeline {
          Post("http://localhost:8080/page/posts/save/" + postObj.authorId, List(postObj))
        }
        responseFuture onComplete {
          case Success(t) =>
            println("wrote PagePost: " + t)

          case Success(somethingUnexpected) =>
            log.warning("Something unexpected in MasterServer: '{}'.", somethingUnexpected)

          case Failure(error) =>
            log.error(error, "Error in writing PagePost!!!")
        }
      }

  }

  def get_PagePosts() = {

    for (pageId <- 300000 to 300001) {

      import common.JsonImplicits._

      val responseFuture = pipeline {
        Get("http://localhost:8080/page/posts/" + pageId)
      }
      responseFuture onComplete {
        case Success(t) =>
          println("Get PagePosts:" + t)

        case Success(somethingUnexpected) =>
          log.warning("Something unexpected in MasterServer: '{}'.", somethingUnexpected)

        case Failure(error) =>
          log.error(error, "Failure in getting PagePosts: " + pageId)
      }

    }
  }


  def post_UserPosts() = {

    val pageName = jsonDirPath + "userPost.json" //"C:\\Users\\Samantha\\tmp\\pagedto.json"
    val pagelines = Source.fromFile(pageName).mkString

    val postObjs = parse(pagelines).extract[List[PostDTO]]


    for (postObj <- postObjs) {

      import common.JsonImplicits._



        println("saving userWallPost: " + postObj)

        val responseFuture = pipeline {
          Post("http://localhost:8080/user/posts/save/" + postObj.authorId, List(postObj))
        }
        responseFuture onComplete {
          case Success(t) =>
            println("wrote UserWallPost: " + t)

          case Success(somethingUnexpected) =>
            log.warning("Something unexpected in MasterServer: '{}'.", somethingUnexpected)

          case Failure(error) =>
            log.error(error, "Error in writing UserWallPost!!!")
        }
      }

  }

  def get_UserPosts() = {

    for (userId <- 100000 to 100005) {

      import common.JsonImplicits._

      val responseFuture = pipeline {
        Get("http://localhost:8080/user/posts/" + userId)
      }
      responseFuture onComplete {
        case Success(t) =>
          println("Get UserWallPosts:" + t)

        case Success(somethingUnexpected) =>
          log.warning("Something unexpected in MasterServer: '{}'.", somethingUnexpected)

        case Failure(error) =>
          log.error(error, "Failure in getting userWallPosts: " + userId)
      }

    }
  }


  def post_UserProfileAlbums() = {

    val pageName = jsonDirPath + "userProfileAlbum.json" //"C:\\Users\\Samantha\\tmp\\pagedto.json"
    val pagelines = Source.fromFile(pageName).mkString

    val albumObjs = parse(pagelines).extract[List[AlbumDTO]]

    //var count = 0

    for (albumObj <- albumObjs) {

      import common.JsonImplicits._

      //count = count + 1

      //if (count < 5) {

        println("saving userWallPost: " + albumObj)

        val responseFuture = pipeline {
          Post("http://localhost:8080/users/profile/album/save/" + (albumObj.id - 521000) + "/" + (albumObj.id - 411000), albumObj)
        }
        responseFuture onComplete {
          case Success(t) =>
            println("wrote UserProfileAlbum: " + t)

          case Success(somethingUnexpected) =>
            log.warning("Something unexpected in MasterServer: '{}'.", somethingUnexpected)

          case Failure(error) =>
            log.error(error, "Error in writing UserProfileAlbum!!!")
        }
      //}
    }

  }

  def get_UserProfileAlbums() = {

    for (userId <- 100000 to 100005) {

      import common.JsonImplicits._

      val responseFuture = pipeline {
        Get("http://localhost:8080/users/profile/album/" + userId + "/" + (userId + 100000))
      }
      responseFuture onComplete {
        case Success(t) =>
          println("Get UserProfileAlbums:" + t)

        case Success(somethingUnexpected) =>
          log.warning("Something unexpected in MasterServer: '{}'.", somethingUnexpected)

        case Failure(error) =>
          log.error(error, "Failure in getting UserProfileAlbums: " + userId)
      }

    }
  }

  def post_UserProfileAlbumPics() = {

    val pageName = jsonDirPath + "userAlbumPics.json" //"C:\\Users\\Samantha\\tmp\\pagedto.json"
    val pagelines = Source.fromFile(pageName).mkString

    val picObjs = parse(pagelines).extract[List[PicDTO]]

    var count = 0

    for (picObj <- picObjs) {

      import common.JsonImplicits._

      count = count + 1

      if (count < 5) {

        println("saving userAlbumPic: " + picObj)

        val albumId = 300//picObj.album_id
        val userId = albumId - 521000
        val profileId = albumId - 411000

        val responseFuture = pipeline {
          Post("http://localhost:8080/users/profile/album/pics/save/" + userId + "/" + profileId, List(picObj))
        }
        responseFuture onComplete {
          case Success(t) =>
            println("wrote UserProfileAlbum: " + t)

          case Success(somethingUnexpected) =>
            log.warning("Something unexpected in MasterServer: '{}'.", somethingUnexpected)

          case Failure(error) =>
            log.error(error, "Error in writing UserProfileAlbum!!!")
        }
      }
    }


  }


  def get_UserProfileAlbumPics() = {

    for (userId <- 100000 to 100005) {

      import common.JsonImplicits._

      val responseFuture = pipeline {
        Get("http://localhost:8080/users/profile/album/pics/" + userId + "/" + (userId + 100000))
      }
      responseFuture onComplete {
        case Success(t) =>
          println("Get UserAlbumPics:" + t)

        case Success(somethingUnexpected) =>
          log.warning("Something unexpected in MasterServer: '{}'.", somethingUnexpected)

        case Failure(error) =>
          log.error(error, "Failure in getting UserAlbumsPic: " + userId)
      }

    }
  }


}