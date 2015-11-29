package routers

import Data.FirstClassData
import spray.http.MediaTypes
import spray.routing.SimpleRoutingApp
import spray.json._
import Nodes._


/**
  * Created by sunito on 11/22/15.
  */

trait UserRouter extends SimpleRoutingApp
with FirstClassData {

  import common.JsonImplicits._

 lazy val userRouter =

    get {
      path ("users"  / IntNumber) {
        id => {
          respondWithMediaType(MediaTypes.`application/json`) {
            complete {
              val usr : User = userMap.get(id)
              if (usr != null) {
                val userJSON = usr.toJson
                "" + userJSON
              } else
                " Cannot get User: " + id
            }
          }
        }
      }
    }~
    post {
      path ("users" / "save") {
        entity(as[User]) {
          person => complete {
            userMap.put(person.id, person)
            "added user: " + person.id
          }
        }
      }
    }~
    delete {
      path ("users" / IntNumber) {
        id => {
          complete {
            if (userMap.containsKey(id)) {
              userMap.remove(id)
              "deleted user: " + id
            } else {
              "Cannot delete user: " + id
            }
          }
        }
      }
    }

}