package Data

import java.util.concurrent.ConcurrentHashMap

import Nodes.Activity

/**
  * Created by sunito on 12/10/15.
  */
object ActivityDB {

  val activityMap : ConcurrentHashMap[Int, Activity] = new ConcurrentHashMap() //pic or post vs Activity
}
