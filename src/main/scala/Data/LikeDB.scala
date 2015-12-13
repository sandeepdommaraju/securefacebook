package Data

import java.util.concurrent.ConcurrentHashMap

import Nodes.Like

/**
  * Created by sunito on 12/10/15.
  */
trait LikeDB {

  val likeMap : ConcurrentHashMap[Int, Like] = new ConcurrentHashMap()
}
