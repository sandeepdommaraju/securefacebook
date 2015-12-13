package Data

import java.util.concurrent.ConcurrentHashMap

import Nodes.Album

/**
  * Created by sunito on 12/10/15.
  */
trait AlbumDB {

  var albumMap : ConcurrentHashMap[Int, Album] = new ConcurrentHashMap()
}
