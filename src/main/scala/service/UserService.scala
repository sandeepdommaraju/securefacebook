package service

import java.security.PublicKey

import Data.UserDB._
import Nodes.User
import akka.actor.Actor
import common.UserDTO
import security.DigitalSignature
import server.Server
import common.JsonImplicits._
import spray.json._
import akka.event.Logging

/**
  * Created by sunito on 12/11/15.
  */

case class AddPublicKey(pubKey : String)
case class getUserPublicKey(userId : Int)
case class SaveUserBasicInfo(userId : Int, data : String)
case class GetUserBasicInfo(userId : Int)

class UserService extends Actor with DigitalSignature{

  val userServiceLog = Logging(context.system, this)

  def receive = {
    case AddPublicKey(pubKeyStr)
      => val newUserId : Int = Server.userIdGEN.addAndGet(1)
         val userPubKey : PublicKey = getPublicKey(decodeBASE64(pubKeyStr))
         pubKeyMap.put(newUserId, userPubKey)
         userServiceLog.info("Added userPublicKey -> replying with userId: " + newUserId + " ServerPubKey: " + Server.pubKey + " UserPubKey: " + userPubKey)
         userServiceLog.info("pubKeyMap: " + pubKeyMap)
         sender ! (newUserId + "#sep#" + Server.pubKey)

    case getUserPublicKey(userId)
      => sender ! encodeBASE64(pubKeyMap.get(userId).getEncoded)

    case SaveUserBasicInfo(userId, data)
      => val u : UserDTO = data.parseJson.convertTo[UserDTO]
         userMap.put(userId, new User(u.id, u.handle, u.first_name, u.last_name, u.sex, u.birthday, null, null, null, null, null, null))
         userServiceLog.info("SavedUserBasicInfo: " + u.id)

    case GetUserBasicInfo(userId)
      => if (userMap.containsKey(userId))
            sender ! userMap.get(userId).getDTO().toJson.toString()
         else
            sender ! "Invalid userId"

    case default => "default"
  }
}
