package Data

import java.util.concurrent.ConcurrentHashMap

import Nodes.Album

/**
  * Created by sunito on 12/10/15.
  */
object AlbumDB {

  var albumMap : ConcurrentHashMap[Int, Album] = new ConcurrentHashMap()

  //var picMap : ConcurrentHashMap[Int, String] = new ConcurrentHashMap()

  //var picSharableMap : ConcurrentHashMap[Int, Int] = new ConcurrentHashMap() //picId vs sharableId
}
