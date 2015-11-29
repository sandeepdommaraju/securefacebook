package Data

import Nodes._
import java.util.concurrent.ConcurrentHashMap

/**
  * Created by sunito on 11/26/15.
  */
trait FirstClassData {

  var userMap : ConcurrentHashMap[Int, User] = new ConcurrentHashMap()

  var profileMap : ConcurrentHashMap[Int, Profile] = new ConcurrentHashMap()

  var friendRequestMap : ConcurrentHashMap[Int, FriendRequest] = new ConcurrentHashMap()

  var pageMap : ConcurrentHashMap[Int, Page] = new ConcurrentHashMap()

  var postMap : ConcurrentHashMap[Int, Post] = new ConcurrentHashMap()

  var commentMap : ConcurrentHashMap[Int, Comment] = new ConcurrentHashMap()
}
