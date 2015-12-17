package Data

import java.util.concurrent.ConcurrentHashMap

import Nodes.{SharablePic, Pic}

/**
  * Created by sunito on 12/10/15.
  */
object PicDB {

  val picMap : ConcurrentHashMap[Int, SharablePic] = new ConcurrentHashMap() //picId vs SharablePic
}
