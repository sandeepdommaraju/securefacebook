package Data

import java.security.PublicKey
import java.util.concurrent.ConcurrentHashMap

import Nodes.User

/**
  * Created by sunito on 12/10/15.
  */
object UserDB {

  var userMap : ConcurrentHashMap[Int, User] = new ConcurrentHashMap()

  var pubKeyMap : ConcurrentHashMap[Int, PublicKey] = new ConcurrentHashMap() //user public keys
}
