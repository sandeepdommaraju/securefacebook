package service

import java.security.PublicKey
import java.util.concurrent.ConcurrentHashMap
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

import Data.ProfileDB._
import Data.UserDB._
import Data.PageDB._
import Data.AlbumDB._
import Data.SharableDB._
import Data.ActivityDB._
import Data.PicDB._
import Nodes._
import akka.actor.Actor
import common._
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

case class SaveUserSharable(userId: Int, sharableData: String)

case class GetUserSharable(userId: Int)

case class SavePageAndPageProfile(userId: Int, data: String)

case class GetPageAndProfile(userId: Int)

case class SaveUserAlbum(userId: Int, data: String)

case class GetUserAlbum(userId: Int)

case class SavePageAlbum(userId: Int, data: String)

case class SavePicInUserProfileAlbum(userId: Int, picId: Int, sharablePicStr: String)

case class GetPicsFromMyUserProfileAlbum(userId: Int)

case class GetAllFriendsPicsFromTheirUserProfileAlbum(userId: Int)

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
      //userServiceLog.info("Added userPublicKey -> replying with userId: " + newUserId + " ServerPubKey: " + Server.pubKey + " UserPubKey: " + userPubKey)
      //userServiceLog.info("pubKeyMap: " + pubKeyMap)
      val newSharableId: Int = Server.sharableIDGEN.addAndGet(1)
      userSharableMap.put(newUserId, List(newSharableId))
      sender ! (newUserId + "#sepdata#" + Server.pubKey + "#sepdata#" + newSharableId)

    case getUserPublicKey(userId)
    => sender ! Server.serverSign(encodeBASE64(pubKeyMap.get(userId).getEncoded))

    case SaveUserBasicInfo(userId, data)
    => val u: UserDTO = data.parseJson.convertTo[UserDTO]
      userMap.put(userId, new User(u.id, u.handle, u.first_name, u.last_name, u.sex, u.birthday, None, None, None, None, None, None))
      //userServiceLog.info("SavedUserBasicInfo: " + u.id)
      sender ! "Posted user basic details: " + userId

    case GetUserBasicInfo(userId)
    => if (userMap.containsKey(userId))
      sender ! Server.serverSign(userMap.get(userId).getDTO().toJson.toString())
    else
      sender ! "Invalid userId"
    //Sam
    case SavePageAndPageProfile(userId, data)
    => if (userMap.containsKey(userId)) {
      val pageId: Int = Server.pageIdGEN.addAndGet(1)
      val profileId: Int = Server.profileIdGEN.addAndGet(1)
      val user: User = userMap.get(userId)
      if (!userMap.containsKey(userId))
        userServiceLog.error("In SavePageAndPageProfile ::: UserId Not found in userMap!! userId: " + userId)
      userMap.put(userId, user.copy(u_pages = Some(List(pageId))))
      val pageDTO: PageDTO = data.split("#sepdata#")(0).parseJson.convertTo[PageDTO]
      val profileDTO: ProfileDTO = data.split("#sepdata#")(1).parseJson.convertTo[ProfileDTO]
      val p: PageDTO = pageDTO.copy(id = pageId)
      pageMap.put(pageId, new Page(p.id, p.owner_user_id, p.page_name, Some(profileId), None, None))
      val pr: ProfileDTO = profileDTO.copy(id = profileId)
      profileMap.put(profileId, new Profile(profileId, pageId, false, pr.description, pr.email, pr.pic, None))
      sender ! "Posted page details for:" + userId
    }

    case GetPageAndProfile(userId)
    => if (userMap.containsKey(userId)) {
      var sendString: String = ""
      val user: User = userMap.get(userId)
      val pageId = user.u_pages.getOrElse(List()).head
      if (pageMap.containsKey(pageId)) {
        val pageObj: Page = pageMap.get(pageId)
        sendString = pageObj.getDTO().toJson.toString()
        val profileId: Int = pageObj.page_profile.getOrElse(0)
        if (profileMap.containsKey(profileId)) {
          sendString = sendString + "#sepdata#" + profileMap.get(profileId).getDTO().toJson.toString()
          sender ! Server.serverSign(sendString.toString())
        } else {
          userServiceLog.error("ERR CANT FIND PAGEPROFILE in GETPAGEPROFILE")
          sender ! "Invalid profileId in profileMap during GetPageProfile userId: " + userId +" pageId: " + pageId + " profileId: " + profileId
        }
      } else {
        userServiceLog.error("ERR CANT FIND PAGE in GETPAGEPROFILE")
        sender ! "Invalid pageId in pageMap during GetPageProfile userId: " + userId +" pageId: " + pageId
      }
    } else {
      userServiceLog.error("ERR CANT FIND USER in GETPAGEPROFILE")
      sender ! "Invalid userId in userMap during GetPageProfile userId: " + userId
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
      val user : User = userMap.get(userId)
      userMap.put(userId, user.copy(u_friends = Some(friendList)))
      var friendsPubKeys: List[String] = List()
      for (friendId <- friendList) {
        if (!pubKeyMap.containsKey(friendId))
          userServiceLog.error("In AddFriends ::: friendId Not found in pubKeyMap!! userId: " + friendId)
        friendsPubKeys = friendsPubKeys :+ encodeBASE64(pubKeyMap.get(friendId).getEncoded)
      }
      sender ! Server.serverSign(friendList.toJson.toString() + "#sepdata#" + friendsPubKeys.toJson.toString())
    //sender ! Server.serverSign(profileMap.get(userId).getDTO().toJson.toString())

    case SaveUserSharable(userId, sharableData)
    => val sharable: Sharable = sharableData.parseJson.convertTo[Sharable]
      val sharableId: Int = sharable.id
      userSharableMap.put(userId, List(sharableId))
      sharableMap.put(sharableId, sharable)
      sender ! "Posted user sharable details: " + userId

    case GetUserSharable(userId)
    => if (userSharableMap.containsKey(userId)) {
      val sharables: List[Int] = userSharableMap.get(userId)
      sender ! Server.serverSign(sharables.head.toString)
    } else
      sender ! "Invalid userId in GetUserSharable"

    case SaveUserAlbum(userId, albumData)
    => val albumDTO: AlbumDTO = albumData.parseJson.convertTo[AlbumDTO]
      val albumId: Int = Server.albumIdGEN.addAndGet(1)
      val a: AlbumDTO = albumDTO.copy(id = albumId)
      if (!userMap.containsKey(userId))
        userServiceLog.error("In SaveUserAlbum ::: UserId Not found in userMap!! userId: " + userId)
      val user: User = userMap.get(userId)
      if (user == null)
        userServiceLog.error("In SaveUserAlbum ::: USER is NULL user: " + user)
      val profileId: Int = user.u_profile_id.getOrElse(-1)
      if (profileId == -1 || !profileMap.containsKey(userId))
        userServiceLog.error("In SaveUserAlbum ::: profileId Not found in profileMap!! profileId: " + profileId)
      val profile: Profile = profileMap.get(userId)
      val mProfile: Profile = profile.copy(album = Some(albumId))
      profileMap.put(userId, mProfile)
      val activityId: Int = Server.activityIdGEN.addAndGet(1)
      albumMap.put(albumId, new Album(albumId, profileId, a.name, a.description, None, Some(activityId)))
      sender ! "Posted Album on UserProfile: userId: " + userId

    case GetUserAlbum(userId)
      => val profile : Profile = profileMap.get(userId)
      val albumId : Int = profile.album.getOrElse(-1)
      if (albumId == -1 || !albumMap.containsKey(albumId))
        userServiceLog.error("In GetUserAlbum ::: albumId Not found in albumMap!! userId: " + userId + " albumId: " + albumId)
      else {
        sender ! Server.serverSign(albumMap.get(albumId).getDTO().toJson.toString())
      }

    /*case SavePageAlbum(userId, albumData)
    => val albumDTO : AlbumDTO = albumData.parseJson.convertTo[AlbumDTO]
      val albumId : Int = Server.albumIdGEN.addAndGet(1)
      val a : AlbumDTO = albumDTO.copy(id = albumId)
      val profileId : Int = userMap.get(userId).u_pages.getOrElse(0)(0)
      val profile : Profile = profileMap.get(profileId)
      val mProfile : Profile = profile.copy(album = Some(albumId))
      profileMap.put(profileId, mProfile)
      val activityId : Int = Server.activityIdGEN.addAndGet(1)
      albumMap.put(albumId, new Album(albumId, profileId, a.name, a.description, null, Some(activityId)))
      sender ! "Posted Album on PageProfile: for user: " + userId
      */

    case SavePicInUserProfileAlbum(userId, picId, sharablePicStr)
      => val albumId: Int = profileMap.get(userId).album.getOrElse(-1)
      val album: Album = albumMap.get(albumId)
      val mPics: List[Int] = album.pics.toList.flatten ++ List(picId)
      val sharablePic : SharablePic = sharablePicStr.parseJson.convertTo[SharablePic]
      albumMap.put(albumId, album.copy(pics = Some(mPics)))
      val userSharableIds : List[Int] = userSharableMap.get(userId)
      // take first sharable -> assuming only 1 sharable
      //pic is shared with a circle
      picMap.put(picId, sharablePic.copy(sharableId = userSharableIds(0)))
      userServiceLog.info("SavedPicInUserProfileAlbum: userId:" + userId + " picId: " + picId)
      val activityId : Int = Server.activityIdGEN.addAndGet(1)
      val activity : Activity = new Activity(activityId, picId, "PIC" ,None, None, None)
      activityMap.put(picId, activity)
      sender ! "Saved PicInUserProfileAlbum: " + userId

    case GetPicsFromMyUserProfileAlbum(userId)
    => val albumId: Int = profileMap.get(userId).album.getOrElse(-1)
      val album: Album = albumMap.get(albumId)
      val picIds : List[Int] = album.pics.toList.flatten
      var sPics : List[SharablePic] = List[SharablePic]()
      for (picId <- picIds) {
        sPics ::= picMap.get(picId)
      }
      sender ! Server.serverSign(sPics.toJson.toString())

    case GetAllFriendsPicsFromTheirUserProfileAlbum(myId: Int)
      => val friendIds : List[Int] = userMap.get(myId).u_friends.getOrElse(List[Int]())
      var friendSharablePics : List[FriendSharablePic] = List[FriendSharablePic]()
      for (friendId <- friendIds) {
        val whatDidMyFriendShareIds : List[Int] = userSharableMap.get(friendId)
        for (sharableId <- whatDidMyFriendShareIds){
          val sharable : Sharable = sharableMap.get(sharableId)
          val viewers : List[Viewer] = sharable.viewers
          for (viewer <- viewers) {
            if (viewer.friendId == myId){
              println("myId: " + myId + " is visible in myFriendViewers: friendId: " + friendId)
              val friendAlbumId : Int = profileMap.get(friendId).album.getOrElse(-1)
              if (friendAlbumId == -1)
                println("friendAlbumId == -1 !! should not happen")
              for (friendPicId <- albumMap.get(friendAlbumId).pics.toList.flatten) {
                if (picMap.get(friendPicId).sharableId == sharableId)
                  friendSharablePics = friendSharablePics :+ new FriendSharablePic(friendId,picMap.get(friendPicId), viewer.encryptedAES)
                else
                  println("FAILED: during picSharableId and myFriendSharableId matching !!")
              }
            }
          }
        }
      }
      userServiceLog.info("in GetAllFriendsPicsFromTheirUserProfileAlbum: myUserId: " + myId)
      sender ! Server.serverSign(friendSharablePics.toJson.toString())


    case SaveUserProfile(userId, data)
    => val p: ProfileDTO = data.parseJson.convertTo[ProfileDTO]
      val newProfileId: Int = Server.profileIdGEN.addAndGet(1)
      val user: User = userMap.get(userId)
      //println(user)
      userMap.put(userId, user.copy(u_profile_id = Some(newProfileId)))
      profileMap.put(p.userOrPageId, new Profile(newProfileId, p.userOrPageId, p.userOrPage, p.description, p.email, p.pic, None))
      userServiceLog.info("SavedUserProfile: " + p.userOrPageId)
      sender ! "Saved UserProfile: " + userId

    case GetProfile(userId)
    => if (profileMap.containsKey(userId)) {
      val sign: String = Server.serverSign(profileMap.get(userId).getDTO().toJson.toString())
      userServiceLog.info("GetProfile: userId: " + userId + " DigSignMsg: " + sign)
      sender ! sign
    }
    else
      sender ! "Invalid userId to get Profile"


    case default => "default"
  }

}
