package Nodes

import common.PicDTO

/**
  * Created by sunito on 11/30/15.
  */
case class Pic ( id : Int,
                 album_id : Int,
                 description : String,
                 data : String,
                 activity : Option[Int]
               ) extends basic {

  def getDTO() = {
    PicDTO(this.id, this.album_id, this.description, this.data)
  }

}
