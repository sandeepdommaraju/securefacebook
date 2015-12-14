package Data

import java.util.concurrent.ConcurrentHashMap

import Nodes.Page

/**
  * Created by sunito on 12/10/15.
  */
object PageDB {

  var pageMap : ConcurrentHashMap[Int, Page] = new ConcurrentHashMap()
}
