package Data

import java.util.concurrent.ConcurrentHashMap

import Nodes.FriendRequest

/**
  * Created by sunito on 12/10/15.
  */
trait FriendRequestDB {

  var friendRequestMap : ConcurrentHashMap[Int, FriendRequest] = new ConcurrentHashMap()
}
