package routers

import Data.UserDB._
import security.DigitalSignature
import spray.routing.{Directive0, HttpServiceActor}

/**
  * Created by sunito on 12/12/15.
  */
trait AuthRouter extends HttpServiceActor with DigitalSignature{

  def authenticateUser( userId : Int, msg : String) : Directive0 = {
      val userPublicKey= pubKeyMap.get(userId)
      /*println("in AuthRouter: " + userId)
      println("in AuthRouter: " + pubKeyMap)
      println("in AuthRouter: msg: " + msg)
      println("in AuthRouter: userPublicKey: "+ userPublicKey)*/
      if (verify(msg, userPublicKey))
        pass
      else
        reject
  }
}
