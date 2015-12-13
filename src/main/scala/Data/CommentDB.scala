package Data

import java.util.concurrent.ConcurrentHashMap

import Nodes.Comment

/**
  * Created by sunito on 12/10/15.
  */
trait CommentDB {

  var commentMap : ConcurrentHashMap[Int, Comment] = new ConcurrentHashMap()
}
