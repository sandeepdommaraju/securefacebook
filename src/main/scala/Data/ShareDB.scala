package Data

import java.util.concurrent.ConcurrentHashMap

import Nodes.Share

/**
  * Created by sunito on 12/10/15.
  */
trait ShareDB {

  val shareMap : ConcurrentHashMap[Int, Share] = new ConcurrentHashMap()
}
