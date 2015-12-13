package Data

import java.util.concurrent.ConcurrentHashMap

import Nodes.Post

/**
  * Created by sunito on 12/10/15.
  */
trait PostDB {

  var postMap : ConcurrentHashMap[Int, Post] = new ConcurrentHashMap()
}
