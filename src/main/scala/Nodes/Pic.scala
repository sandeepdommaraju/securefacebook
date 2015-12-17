package Nodes

import common.PicDTO

/**
  * Created by sunito on 11/30/15.
  */
case class Pic ( id : Int,
                 description : String,
                 data : String
               ) extends basic {

  def getDTO() = {
    PicDTO(this.id, this.description, this.data)
  }

}

case class SharablePic (picId : Int, sharableId : Int, ivector : String, encPic: String)

case class FriendSharablePic(friendId : Int, sharablePic : SharablePic, encAES : String)