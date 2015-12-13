package client

import java.security.KeyPair

import akka.actor.Actor
import akka.event.Logging
import common.JsonImplicits._
import common.{ProfileDTO, UserDTO}
import security.DigitalSignature
import spray.client.pipelining._
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

case object Login

case class SetUserId(uid: Int)

/**
  * Created by sunito on 12/11/15.
  */
class ClientWorker(baseURL: String) extends Actor with DigitalSignature {

  var keyPair: KeyPair = null
  var pubKey: String = null
  var serverPubKey : String = null
  val pipeline = sendReceive ~> unmarshal[String]
  val workerId: Int = self.path.name.substring(6).toInt
  var userId: Int = 0

  val clientWorkerLog = Logging(context.system, this)

  def receive = {
    case Login => login
    case SetUserId(uid) => userId = uid
    //case GetUserBasicDetails => getBasicDetails
    case default => "default msg in client worker"
  }

  def sendPublicKey = {
    //val newUser : UserDTO = new UserDTO(1, self.path.name, "firstName" +workerId, "lastName" + workerId, "Male", "birthday" + workerId)

    println("sending publicKey")
    val f = pipeline {
      Post(baseURL + "user/public-key/save", pubKey)
    }

    f onComplete {
      case Success(uid_severPubKey: String)
      => val pairs = uid_severPubKey.split("#sep#")
        userId = pairs(0).toInt
        serverPubKey = pairs(1)
        clientWorkerLog.info("Successful Login, sent public-key => got userId: " + userId + " serverPubKey: " + serverPubKey)
        createAndSendBasicDetails

      case Failure(err)
      => clientWorkerLog.error("ERR: " + err)
    }

  }

  def getPublicKey(uid: Int) = {
    val f = pipeline {
      Get(baseURL + "user/public-key/" + userId)
    }

    f onComplete {
      case Success(t)
      => clientWorkerLog.info("userId: " + uid + " has public-key: " + t)
      case Failure(err)
      => clientWorkerLog.error("ERR: " + err)
    }
  }

  def createAndSendBasicDetails = {

    //getPublicKey(userId)

    val userDTO = new UserDTO(userId, "userhandle" + workerId , "first_name" + workerId, "last_name" + workerId, "male", "2000-01-01")
    //DS msg with user private key
    val data = userDTO.toJson.toString()
    val msg : String = sign(data, keyPair.getPrivate)
    clientWorkerLog.info("Sending POST request: createUserBasicDetails: " + userId)
    val f = pipeline {
      Post(baseURL + "user/basic-details/save/"+userId, msg)
    }

    f onComplete {
      case Success(t) => createAndSendUserProfile
      case Failure(err) => clientWorkerLog.error(err.toString)
    }
  }

  def createAndSendUserProfile = {
    val profileDTO = new ProfileDTO(1, userId, true, "description: " + workerId, "email", "profile-pic:"+userId)

    val data = profileDTO.toJson.toString()
    val msg : String = sign(data, keyPair.getPrivate)
    clientWorkerLog.info("Sending POST request: createUserProfile: " + userId)
    val f = pipeline {
      Post(baseURL + "profile/user/save/"+userId, msg)
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
    handleGenericFuture(f, "in GetUserProfile")
  }

  def getBasicDetails = {
    println("Sending GET request: getBasicDetails: " + userId)
    val f = pipeline {
      Get(baseURL + "user/basic-details/" + userId)
    }
    handleGenericFuture(f, "in GetUserBasicDetails")
  }

  def login = {
    keyPair = getKeyPair
    pubKey = encodeBASE64(keyPair.getPublic.getEncoded)
    sendPublicKey
  }

  def handleGenericFuture ( f : Future[String], customMsg : String) = {
    f onComplete {
      case Success(s: String)
      => clientWorkerLog.info("Success: " + customMsg + " -> " + s)

      case Failure(err)
      => clientWorkerLog.error("ERR: " + customMsg + " -> " + err)
    }
  }
}
