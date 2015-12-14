package service

import Nodes.Profile
import akka.actor.Actor
import common.ProfileDTO
import security.DigitalSignature
import common.JsonImplicits._
import spray.json._
import akka.event.Logging
import server.Server
import Data.ProfileDB.profileMap

/**
  * Created by sunito on 12/12/15.
  */

case class SaveUserProfile(userId : Int, data : String)
case class GetProfile(userId : Int)

class ProfileService extends Actor with DigitalSignature{

  val profileServiceLog = Logging(context.system, this)

  def receive = {
    case SaveUserProfile(userId, data)
      => val p : ProfileDTO = data.parseJson.convertTo[ProfileDTO]
      val newProfileId : Int = Server.profileIdGEN.addAndGet(1)
      profileMap.put(p.userOrPageId, new Profile(newProfileId, p.userOrPageId, p.userOrPage, p.description, p.email, p.pic, null))
      profileServiceLog.info("SavedUserProfile: " + p.userOrPageId)

    case GetProfile(userId)
      => if (profileMap.containsKey(userId))
            sender ! Server.serverSign(profileMap.get(userId).getDTO().toJson.toString())
         else
            sender ! "Invalid userId to get Profile"

    case default => "default"
  }

}
