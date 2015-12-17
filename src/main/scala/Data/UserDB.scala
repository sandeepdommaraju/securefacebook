package Data

import java.security.PublicKey
import java.util.concurrent.ConcurrentHashMap

import Nodes.{Sharable, User}

/**
  * Created by sunito on 12/10/15.
  */
object UserDB {

  var userMap : ConcurrentHashMap[Int, User] = new ConcurrentHashMap()

  var pubKeyMap : ConcurrentHashMap[Int, PublicKey] = new ConcurrentHashMap() //user public keys

  var aesKeyMap : ConcurrentHashMap[Int, String] = new ConcurrentHashMap() //user AES keys encrypted with RSA user-public keys

  var userSharableMap : ConcurrentHashMap[Int, List[Int]] = new ConcurrentHashMap() //userId vs List[SharableId]

  var sharableMap : ConcurrentHashMap[Int, Sharable] = new ConcurrentHashMap()

}
