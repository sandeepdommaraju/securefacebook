package Nodes

import common.ActivityDTO

/**
  * Created by sunito on 11/30/15.
  */
case class Activity ( id : Int,
                      owner_id : Int,     // Post, Album, Picture
                      owner_type : String,
                      comments : Option[Int],
                      likes : Option[Int],
                      shares : Option[Int])
    extends basic {

  def getDTO() = {
    ActivityDTO(this.id, this.owner_id, this.owner_type)
  }

}
