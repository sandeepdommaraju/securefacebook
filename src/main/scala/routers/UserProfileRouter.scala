package routers

import Data.FirstClassData
import spray.http.MediaTypes
import spray.routing.SimpleRoutingApp
import spray.json._
import Nodes._

/**
  * Created by sunito on 11/27/15.
  */
/*trait UserProfileRouter extends SimpleRoutingApp
with FirstClassData {

  import common.JsonImplicits._

  val userProfileRouter =

    get {
      path ("users"  / "profile" / IntNumber) {
        id => {
          respondWithMediaType(MediaTypes.`application/json`) {
            complete {

              val usr : Option[User] = userMap.get(id)

              val user1 : User = usr match {
                case Some(u) => u
              }

              val userJSON = user1.toJson

              "" + userJSON
            }
          }
        }
      }
    }~
      post {
        path ("users" / "profile" / "save") {
          entity(as[Profile]) {
            profile => complete {
              // profile.userOrPage has to be true ; since it is usersProfile
              // profile.userOrPageId has to be userId
              val profileId =
              profileMap.put(profile.profileId, profile)
              "added user: " + person.u_id
            }
          }
        }
      }~
      delete {
        path ("users" / "profile" / IntNumber) {
          id => {
            complete {
              userMap -= id
              "deleted user: " + id
            }
          }
        }
      }


}
*/