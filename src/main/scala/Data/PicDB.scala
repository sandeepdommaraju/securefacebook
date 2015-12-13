package Data

import java.util.concurrent.ConcurrentHashMap

import Nodes.Pic

/**
  * Created by sunito on 12/10/15.
  */
trait PicDB {

  val picMap : ConcurrentHashMap[Int, Pic] = new ConcurrentHashMap()
}
