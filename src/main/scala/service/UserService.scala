package service

import java.security.PublicKey
import java.util.concurrent.ConcurrentHashMap
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

import Data.ProfileDB._
import Data.UserDB._
import Data.PageDB._
import Data.SharableDB._
import Nodes.{Profile, Page, Sharable, User}
import akka.actor.Actor
import common.{ProfileDTO, UserDTO, PageDTO}
import security.DigitalSignature
import server.Server
import common.JsonImplicits._
import spray.json._
import akka.event.Logging

/**
  * Created by sunito on 12/11/15.
  */

case class AddPublicKey(pubKey: String)

case class getUserPublicKey(userId: Int)

case class SaveUserBasicInfo(userId: Int, data: String)

case class GetUserBasicInfo(userId: Int)

case class AddFriends(userId: Int)

case class SaveUserSharable(userId: Int, sharableData : String)

case class SavePageAndPageProfile(userId:Int, data:String)

case class GetPageAndProfile(userId:Int)

class UserService extends Actor with DigitalSignature {

  val userServiceLog = Logging(context.system, this)

  def receive = {
    case AddPublicKey(pubKeyStr)
    => val usrPubKeyStr = pubKeyStr.split("#sepdata#")(0)
      val usrAESKeyStr = pubKeyStr.split("#sepdata#")(1)
      val newUserId: Int = Server.userIdGEN.addAndGet(1)
      val userPubKey: PublicKey = getPublicKey(decodeBASE64(usrPubKeyStr))
      pubKeyMap.put(newUserId, userPubKey)
      //val aesKey : SecretKey = new SecretKeySpec(decodeBASE64(usrAESKeyStr), "AES")
      aesKeyMap.put(newUserId, encryptRSA(usrAESKeyStr, userPubKey))
      userServiceLog.info("Added userPublicKey -> replying with userId: " + newUserId + " ServerPubKey: " + Server.pubKey + " UserPubKey: " + userPubKey)
      userServiceLog.info("pubKeyMap: " + pubKeyMap)
      val newSharableId : Int = Server.sharableIDGEN.addAndGet(1)
      userSharableMap.put(newUserId, List(newSharableId))
      sender ! (newUserId + "#sepdata#" + Server.pubKey + "#sepdata#" + newSharableId)

    case getUserPublicKey(userId)
    => sender ! Server.serverSign(encodeBASE64(pubKeyMap.get(userId).getEncoded))

    case SaveUserBasicInfo(userId, data)
    => val u: UserDTO = data.parseJson.convertTo[UserDTO]
      userMap.put(userId, new User(u.id, u.handle, u.first_name, u.last_name, u.sex, u.birthday, null, null, null, null, null, null))
      userServiceLog.info("SavedUserBasicInfo: " + u.id)

    case GetUserBasicInfo(userId)
    => if (userMap.containsKey(userId))
      sender ! Server.serverSign(userMap.get(userId).getDTO().toJson.toString())
    else
      sender ! "Invalid userId"
//Sam
    case SavePageAndPageProfile(userId, data)
    => if(userMap.containsKey(userId)){
      val pageId: Int = Server.pageIdGEN.addAndGet(1)
      val profileId : Int = Server.profileIdGEN.addAndGet(1)
      val user:User = userMap.get(userId)
      userMap.put(userId, user.copy(u_pages = Some(List(pageId))))
      val pageDTO : PageDTO = data.split("#sepdata#")(0).parseJson.convertTo[PageDTO]
      val profileDTO : ProfileDTO = data.split("#sepdata#")(1).parseJson.convertTo[ProfileDTO]
      val p : PageDTO = pageDTO.copy(id = pageId)
      pageMap.put(pageId, new Page(p.id, p.owner_user_id, p.page_name, Some(profileId), null, null))
      val pr : ProfileDTO = profileDTO.copy(id = profileId)
      profileMap.put(profileId,new Profile(profileId,pageId,false,pr.description,pr.email,pr.pic,null))
    }
    case GetPageAndProfile(userId)
      => if(userMap.containsKey(userId)){
      var sendString: String = ""
      val user:User = userMap.get(userId)
      val pageId = user.u_pages.getOrElse(null).head
      if(pageMap.containsKey(pageId)){
        val pageObj : Page = pageMap.get(pageId)
        sendString = pageObj.getDTO().toJson.toString()
        val profileId : Int = pageObj.page_profile.getOrElse(0)
        if(profileMap.containsKey(profileId)) {
          sendString = sendString + "#sepdata#" + profileMap.get(profileId).getDTO().toJson.toString()
        }
      }
      println("In getPageAndProfile" + sendString)
      println("In getPageAndProfile" + sendString.toString())
      //sender ! Server.serverSign(userMap.get(userId).getDTO().toJson.toString())
      sender ! Server.serverSign(sendString.toString())
    }else{
      userServiceLog.error("CANT FIND USER in GETPAGEPROFILE")
    }



    case AddFriends(userId)
    => var friendList: List[Int] = List()
      var u = 0
      val itr = userMap.keys()
      for (u <- 1 to 6) {
        val friendId = itr.nextElement()
        if (friendId != userId)
          friendList = friendList :+ friendId
      }
      var friendsPubKeys: List[String] = List()
      for (friendId <- friendList) {
        friendsPubKeys = friendsPubKeys :+ encodeBASE64(pubKeyMap.get(friendId).getEncoded)
      }
      sender ! Server.serverSign(friendList.toJson.toString() + "#sepdata#" + friendsPubKeys.toJson.toString())
      //sender ! Server.serverSign(profileMap.get(userId).getDTO().toJson.toString())

    case SaveUserSharable(userId, sharableData)
      => val sharable : Sharable = sharableData.parseJson.convertTo[Sharable]
         val sharableId : Int = sharable.id
         userSharableMap.put(userId, List(sharableId))
         sharable_Viewers_Map.put(sharableId, sharable)

    case default => "default"
  }
}
