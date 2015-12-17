package service

import Nodes.{User, Album, Profile}
import akka.actor.Actor
import common.ProfileDTO
import security.DigitalSignature
import common.JsonImplicits._
import spray.json._
import akka.event.Logging
import server.Server
import Data.ProfileDB.profileMap
import Data.UserDB._
import Data.AlbumDB._

/**
  * Created by sunito on 12/12/15.
  */

case class SaveUserProfile(userId: Int, data: String)
case class GetProfile(userId: Int)


class ProfileService extends Actor with DigitalSignature {

  val profileServiceLog = Logging(context.system, this)

  def receive = {
    /*case SaveUserProfile(userId, data)
    => val p: ProfileDTO = data.parseJson.convertTo[ProfileDTO]
      val newProfileId: Int = Server.profileIdGEN.addAndGet(1)
      val user : User = userMap.get(userId)
      //println(user)
      userMap.put(userId, user.copy(u_profile_id = Some(newProfileId)))
      profileMap.put(p.userOrPageId, new Profile(newProfileId, p.userOrPageId, p.userOrPage, p.description, p.email, p.pic, null))
      //profileServiceLog.info("SavedUserProfile: " + p.userOrPageId)
      sender ! "Saved UserProfile: " + userId

    case GetProfile(userId)
    => if (profileMap.containsKey(userId))
      sender ! Server.serverSign(profileMap.get(userId).getDTO().toJson.toString())
    else
      sender ! "Invalid userId to get Profile"

    case SavePicInUserProfileAlbum(userId, picId, data)
    => val profileId: Int = userMap.get(userId).u_profile_id.getOrElse(-1)
      val albumId: Int = profileMap.get(profileId).album.getOrElse(-1)
      val album: Album = albumMap.get(albumId)
      val mPics: List[Int] = album.pics.getOrElse(List()) :+ picId
      albumMap.put(albumId, album.copy(pics = Some(mPics)))
      picMap.put(picId, data)
      profileServiceLog.info("SavedPicInUserProfileAlbum: userId:" + userId + " picId: " + picId)
      sender ! "Saved PicInUserProfileAlbum: " + userId
      */

    case default => "default"
  }

}
