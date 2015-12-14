package client

import java.security.KeyPair

import Nodes.{Sharable, Viewer}
import akka.actor.Actor
import akka.event.Logging
import common.JsonImplicits._
import common.{PageDTO, ProfileDTO, UserDTO}
import security.{AES, DigitalSignature}
import spray.client.pipelining._
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

case object Login

case class SetUserId(uid: Int)

case object AddPage

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
    case default => "default msg in client worker"
  }

  def sendPublicKey = {
    //val newUser : UserDTO = new UserDTO(1, self.path.name, "firstName" +workerId, "lastName" + workerId, "Male", "birthday" + workerId)

    println("sending publicKey")
    val f = pipeline {
      Post(baseURL + "user/public-key/save", pubKey + "#sepdata#" + aesKey)
    }

    f onComplete {
      case Success(uid_severPubKey: String)
      => val pairs = uid_severPubKey.split("#sepdata#")
        userId = pairs(0).toInt
        serverPubKey = pairs(1)
        sharableId = pairs(2).toInt
        clientWorkerLog.info("Successful Login, sent public-key => got userId: " + userId + " serverPubKey: " + serverPubKey)
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
      => if (Client.verifyServer(t, getPublicKey(decodeBASE64(serverPubKey))))
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
    clientWorkerLog.info("Sending POST request: createUserBasicDetails: " + userId)
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
      Post(baseURL + "profile/user/save/" + userId, msg)
    }
    //handleGenericFuture(f, "in createUserProfile")

    f onComplete {
      case Success(t) => getProfile
      case Failure(err) => clientWorkerLog.error(err.toString)
    }
  }

  def getProfile = {
    println("Sending GET request: getProfile: " + userId)
    val f = pipeline {
      Get(baseURL + "profile/user/" + userId)
    }
    handleGenericGetFuture(f, "in GetUserProfile")
  }

  def getBasicDetails = {
    println("Sending GET request: getBasicDetails: " + userId)
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
    val pageDTO = new PageDTO(1,userId, "pagecreatedby: " + workerId)
    val pageprofileDTO = new ProfileDTO(1,1,false,"PagecreatedBy"+ userId, "test@gmail.com","PicString")

    val data = pageDTO.toJson.toString() + "#sepdata#" + pageprofileDTO.toJson.toString()
    val msg : String = sign(data, keyPair.getPrivate)
    clientWorkerLog.info("Sending POST request: createAndSendPageProfile: " + userId)
    val f = pipeline {
      Post(baseURL + "user/page/save/" + userId, msg)
    }

    handleGenericPostFuture(f, "in POST PageProfile")

    /*f onComplete {
      case Success(t) => println("PageCreated")//getPageProfile
      case Failure(err) => clientWorkerLog.error(err.toString)
    }*/
  }

  def handleGenericGetFuture(f: Future[String], customMsg: String) = {
    f onComplete {
      case Success(msg: String)
      =>if (Client.verifyServer(msg, getPublicKey(decodeBASE64(serverPubKey))))
        clientWorkerLog.info("Success: " + customMsg + " -> " + msg)
      else
        clientWorkerLog.error("Failed to verify Server's DigitalSignature " + customMsg)

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
    println("Sending GET request: AddFriends: " + userId)
    val f = pipeline {
      Get(baseURL + "user/add/friends/" + userId)
    }
    //handleGenericFuture(f, "in AddFriends")
    f onComplete {
      case Success(msg: String)
      =>if (Client.verifyServer(msg, getPublicKey(decodeBASE64(serverPubKey)))) {
          val datamsg : String = msg.split("#sep#")(1)
          val friendIds: List[Int] = datamsg.split("#sepdata#")(0).parseJson.convertTo[List[Int]]
          val friendPubKeys: List[String] = datamsg.split("#sepdata#")(1).parseJson.convertTo[List[String]]
          var viewers: List[Viewer] = List()
          var u = 0
          for (u <- 0 to friendIds.length - 1) {
            val viewer: Viewer = new Viewer(friendIds(u), encryptRSA(aesKey, getPublicKey(decodeBASE64(friendPubKeys(u)))))
            viewers = viewers :+ viewer
          }
          val sharable: Sharable = new Sharable(sharableId, viewers)
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
    handleGenericPostFuture(f, "in AddSharable: " + userId)
  }

}
