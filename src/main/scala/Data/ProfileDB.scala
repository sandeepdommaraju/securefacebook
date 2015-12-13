package Data

import java.util.concurrent.ConcurrentHashMap

import Nodes.Profile

/**
  * Created by sunito on 12/10/15.
  */
object ProfileDB {

  var profileMap : ConcurrentHashMap[Int, Profile] = new ConcurrentHashMap()
}
