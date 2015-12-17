package client

import java.security.{PublicKey, KeyFactory, KeyPair}

import Nodes.{FriendSharablePic, SharablePic, Sharable, Viewer}
import akka.actor.Actor
import akka.event.Logging
import common.JsonImplicits._
import common._
import security.{AES, DigitalSignature}
import spray.client.pipelining._
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

case object Login

case class SetUserId(uid: Int)

case object AddPage

case object AddAlbum

case object AddPicInUserProfile

/**
  * Created by sunito on 12/11/15.
  */
class ClientWorker(baseURL: String) extends Actor with DigitalSignature with AES {

  var keyPair: KeyPair = null
  var pubKey: String = null
  var aesKey: String = null
  var serverPubKey: String = null
  val pipeline = sendReceive ~> unmarshal[String]
  val workerId: Int = self.path.name.substring(6).toInt
  var userId: Int = 0
  var sharableId: Int = 0
  var sharable_AES_Map: Map[Int, String] = Map() //sharableId vs aesKey

  val clientWorkerLog = Logging(context.system, this)

  def receive = {
    case Login => login
    case SetUserId(uid) => userId = uid
    //case GetUserBasicDetails => getBasicDetails
    case AddFriends => addFriends
    case AddPage => createAndSendPageProfile
    case AddAlbum => createAndSendAlbum
    case AddPicInUserProfile => createAndSendPicInUserProfileAlbum
    case default => "default msg in client worker"
  }

  def sendPublicKey = {
    //val newUser : UserDTO = new UserDTO(1, self.path.name, "firstName" +workerId, "lastName" + workerId, "Male", "birthday" + workerId)

    //println("sending publicKey")
    val f = pipeline {
      Post(baseURL + "user/public-key/save", pubKey + "#sepdata#" + aesKey)
    }

    f onComplete {
      case Success(uid_severPubKey: String)
      => val pairs = uid_severPubKey.split("#sepdata#")
        userId = pairs(0).toInt
        serverPubKey = pairs(1)
        sharableId = pairs(2).toInt
        //clientWorkerLog.info("Successful Login, sent public-key => got userId: " + userId + " serverPubKey: " + serverPubKey)
        createAndSendBasicDetails

      case Failure(err)
      => clientWorkerLog.error("ERR: " + err)
    }

  }

  def getUserPublicKey(uid: Int) = {
    val f = pipeline {
      Get(baseURL + "user/public-key/" + userId)
    }

    f onComplete {
      case Success(t)
      => if (verifyServer(t, getPublicKey(decodeBASE64(serverPubKey))))
        clientWorkerLog.info("userId: " + uid + " has public-key: " + t)
      else
        clientWorkerLog.info("Failed to verify Server's DigitalSignature in GET user/public-key/")
      case Failure(err)
      => clientWorkerLog.error("ERR: " + err)
    }
  }

  def createAndSendBasicDetails = {

    //getUserPublicKey(userId)

    val userDTO = new UserDTO(userId, "userhandle" + workerId, "first_name" + workerId, "last_name" + workerId, "male", "2000-01-01")
    //DS msg with user private key
    val data = userDTO.toJson.toString()
    val msg: String = sign(data, keyPair.getPrivate)
    //clientWorkerLog.info("Sending POST request: createUserBasicDetails: " + userId)
    val f = pipeline {
      Post(baseURL + "user/basic-details/save/" + userId, msg)
    }

    f onComplete {
      case Success(t) => createAndSendUserProfile
      case Failure(err) => clientWorkerLog.error(err.toString)
    }
  }

  def createAndSendUserProfile = {
    val profileDTO = new ProfileDTO(1, userId, true, "description: " + workerId, "email", "profile-pic:" + userId)

    val data = profileDTO.toJson.toString()
    val msg: String = sign(data, keyPair.getPrivate)
    clientWorkerLog.info("Sending POST request: createUserProfile: " + userId)
    val f = pipeline {
      Post(baseURL + "user/profile/save/" + userId, msg)
    }
    //handleGenericFuture(f, "in createUserProfile")

    f onComplete {
      case Success(t) => clientWorkerLog.info("Succ: " + t)
        createAndSendPageProfile
        //getProfile
      case Failure(err) => clientWorkerLog.error("ERR: " + err.toString)
    }
  }

  def getProfile = {
    //println("Sending GET request: getProfile: " + userId)
    val f = pipeline {
      Get(baseURL + "user/profile/" + userId)
    }
    handleGenericGetFuture(f, "in GetUserProfile: userId: " + userId)
  }

  def getBasicDetails = {
    //println("Sending GET request: getBasicDetails: " + userId)
    val f = pipeline {
      Get(baseURL + "user/basic-details/" + userId)
    }
    handleGenericGetFuture(f, "in GetUserBasicDetails")
  }

  def login = {
    keyPair = getKeyPair
    aesKey = encodeBASE64(generateAESKey.getEncoded)
    pubKey = encodeBASE64(keyPair.getPublic.getEncoded)
    sendPublicKey
  }

  //Sam
  def createAndSendPageProfile = {
    val pageDTO = new PageDTO(1, userId, "pagecreatedby: " + workerId)
    val pageprofileDTO = new ProfileDTO(1, 1, false, "PagecreatedBy" + userId, "test@gmail.com", "PicString")

    val data = pageDTO.toJson.toString() + "#sepdata#" + pageprofileDTO.toJson.toString()
    val msg: String = sign(data, keyPair.getPrivate)
    clientWorkerLog.info("Sending POST request: createAndSendPageProfile: " + userId)
    val f = pipeline {
      Post(baseURL + "user/page/save/" + userId, msg)
    }

    //handleGenericPostFuture(f, "in POST PageProfile")

    f onComplete {
      case Success(t) => getPageProfile //println("PageCreated")//
      case Failure(err) => clientWorkerLog.error(err.toString)
    }
  }

  def getPageProfile = {
    //println("Sending GET request: getPageProfile: " + userId)
    val f = pipeline {
      Get(baseURL + "user/pageprofile/" + userId)
    }
    handleGenericGetFuture(f, "in GetPageProfile: userId: " + userId)
  }

  def handleGenericGetFuture(f: Future[String], customMsg: String) = {
    f onComplete {
      case Success(msg: String)
      => if (verifyServer(msg, getPublicKey(decodeBASE64(serverPubKey))))
        clientWorkerLog.info("Success: " + customMsg + " -> " + msg)
      else
        clientWorkerLog.error("Failed to verify Server's DigitalSignature " + customMsg + " msg: " + msg)

      case Failure(err)
      => clientWorkerLog.error("ERR: " + customMsg + " -> " + err)
    }
  }

  def handleGenericPostFuture(f: Future[String], customMsg: String) = {
    f onComplete {
      case Success(msg: String)
      => clientWorkerLog.info("Success: " + customMsg + " -> " + msg)
      case Failure(err)
      => clientWorkerLog.error("ERR: " + customMsg + " -> " + err)
    }
  }

  def addFriends = {
    // add 5 random friends
    //println("Sending GET request: AddFriends: " + userId)
    val f = pipeline {
      Get(baseURL + "user/add/friends/" + userId)
    }
    //handleGenericFuture(f, "in AddFriends")
    f onComplete {
      case Success(msg: String)
      => if (verifyServer(msg, getPublicKey(decodeBASE64(serverPubKey)))) {
        val datamsg: String = msg.split("#sep#")(1)
        val friendIds: List[Int] = datamsg.split("#sepdata#")(0).parseJson.convertTo[List[Int]]
        val friendPubKeys: List[String] = datamsg.split("#sepdata#")(1).parseJson.convertTo[List[String]]
        var viewers: List[Viewer] = List()
        var u = 0
        for (u <- 0 to friendIds.length - 1) {
          val viewer: Viewer = new Viewer(friendIds(u), encryptRSA(aesKey, getPublicKey(decodeBASE64(friendPubKeys(u)))))
          viewers = viewers :+ viewer
        }
        val sharable: Sharable = new Sharable(sharableId, viewers)
        clientWorkerLog.info("Success: in addFriends: userId: " + userId)
        sendSharable(sharable)
      }
      else
        clientWorkerLog.error("Failed to verify Server's DigitalSignature " + " in GET user/add/friends/" + userId)
      case Failure(err)
      => clientWorkerLog.error("ERR: " + "in GET user/add/friends/" + userId + " -> " + err)
      //val friendIds : List[Int] = msg.split(0).
    }
  }

  def sendSharable(sharable: Sharable) = {
    val data = sharable.toJson.toString()
    val msg: String = sign(data, keyPair.getPrivate)
    clientWorkerLog.info("Sending POST request: AddSharable: " + userId)
    val f = pipeline {
      Post(baseURL + "user/sharable/save/" + userId, msg)
    }
    //handleGenericPostFuture(f, "in AddSharable: " + userId)
    f onComplete {
      case Success(t)
      => getSharable
      case Failure(err)
      => clientWorkerLog.error("ERR: in sendSharable: " + userId)
    }
  }

  def getSharable = {
    val f = pipeline {
      Get(baseURL + "user/sharable/" + userId)
    }
    handleGenericGetFuture(f, "in GetSharable: userId: " + userId)
  }

  def createAndSendAlbum = {

    val albumDTO: AlbumDTO = new AlbumDTO(1, 1, "albumname: " + userId, "description-yada")
    val data = albumDTO.toJson.toString()
    val msg: String = sign(data, keyPair.getPrivate)
    clientWorkerLog.info("Sending POST request: AddAlbum: " + userId)
    val f = pipeline {
      Post(baseURL + "user/album/save/" + userId, msg)
    }
    //handleGenericPostFuture(f, "in AddAlbum: " + userId)

    f onComplete {
      case Success(t)
        => getUserAlbum
      case Failure(err)
        => clientWorkerLog.error("ERR: in createAndSendUserAlbum: userId: " + userId)
    }
  }

  def getUserAlbum = {
    val f = pipeline {
      Get(baseURL + "user/album/" + userId) //user default album from user-profile
    }
    handleGenericGetFuture(f, "in GetUserAlbum: userId: " + userId)
  }

  def createAndSendPicInUserProfileAlbum = {
    val picId: Int = Client.picIdGEN.addAndGet(1)
    val picDTO: PicDTO = new PicDTO(picId, "pic-description: " + picId, "pic-clear-data")
    val iVector: String = Client.uuid
    val encPic: String = encodeBASE64(encryptAES(getAESSecretKey(aesKey), picDTO.toJson.toString(), iVector))
    val sharablePic : SharablePic = new SharablePic(picId, 0, iVector, encPic)
    val data : String = sharablePic.toJson.toString()
    val msg: String = sign(data, keyPair.getPrivate)
    clientWorkerLog.info("Sending POST request: AddPicInUserProifle: " + userId)
    val f = pipeline {
      Post(baseURL + "user/profile/album/pic/save/" + userId + "/" + picId, msg) //Ideally we need to pass albumId
    }
    handleGenericPostFuture(f, "in AddPicInUserAlbum: " + userId)

    f onComplete {
      case Success(t) => getPicsInMyUserProfileAlbum
      case Failure(err) => clientWorkerLog.error("ERR: in createAndSendPicInUserProfile: userId: " + userId + " picId: " + picId)
    }
  }

  def getPicsInMyUserProfileAlbum = {
    val f = pipeline {
      Get(baseURL + "user/profile/album/pics/" + userId) //user pics in his default album from user-profile
    }

    val customMsg : String = " in getPicsInMyUserProfileAlbum: "

    f onComplete {
      case Success(msg: String)
      => if (verifyServer(msg, getPublicKey(decodeBASE64(serverPubKey)))) {
        clientWorkerLog.info("Success: " + customMsg + " -> " + msg)
        val sharablePics: List[SharablePic] = msg.split("#sep#")(1).parseJson.convertTo[List[SharablePic]]
        for (sharablePic <- sharablePics){
          //println("sharablePic: " + sharablePic +" userId: " + userId)
          val decPic : String = decryptAES(decodeBASE64(sharablePic.encPic), getAESSecretKey(aesKey), sharablePic.ivector)
          println("myUserProfilePic: " + new String(decodeBASE64(decPic)) + " userId: " + userId)
        }
        getMyFriendsPicsinTheirProfileAlbum
      }
      else
        clientWorkerLog.error("Failed to verify Server's DigitalSignature " + customMsg + " msg: " + msg)

      case Failure(err)
      => clientWorkerLog.error("ERR: " + customMsg + " -> " + err)
    }

    //handleGenericGetFuture(f, "in GetPicsInUserProfileAlbum: userId: " + userId)
  }

  def getMyFriendsPicsinTheirProfileAlbum = {
    val f = pipeline {
      Get(baseURL + "user/all-friends/profile/album/pics/" + userId) //user pics in his default album from user-profile
    }

    val customMsg : String = " in getMyFriendsProfilePics: "

    f onComplete {
      case Success(msg: String)
      => if (verifyServer(msg, getPublicKey(decodeBASE64(serverPubKey)))) {
        clientWorkerLog.info("Success: " + customMsg + " -> " + msg)
        //TODO
        val friendSharablePics : List[FriendSharablePic] = msg.split("#sep#")(1).parseJson.convertTo[List[FriendSharablePic]]
        for (friendSharablePic <- friendSharablePics){
          val friendId : Int = friendSharablePic.friendId
          val sharablePic : SharablePic = friendSharablePic.sharablePic
          val friendSharableAESKey : String = decryptRSA(friendSharablePic.encAES, keyPair.getPrivate)
          val decPic : String = decryptAES(decodeBASE64(sharablePic.encPic), getAESSecretKey(friendSharableAESKey), sharablePic.ivector)
          println("myFriend " + friendId + " ProfilePic: says " + new String(decodeBASE64(decPic)) + " myId: " + userId)
        }
      }
      else
        clientWorkerLog.error("Failed to verify Server's DigitalSignature " + customMsg + " msg: " + msg)

      case Failure(err)
      => clientWorkerLog.error("ERR: " + customMsg + " -> " + err)
    }

  }

  def verifyServer(msg: String, serverPubKey: PublicKey) = {
    //println("verify msg: " + msg + " serverPubKey: " + serverPubKey)
    verify(msg, serverPubKey)
  }

}
