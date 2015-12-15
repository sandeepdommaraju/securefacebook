package Data

import java.util.concurrent.ConcurrentHashMap

import Nodes.{SharablePost, Post}

/**
  * Created by sunito on 12/10/15.
  */
object PostDB {

  var postMap : ConcurrentHashMap[Int, SharablePost] = new ConcurrentHashMap()
}
