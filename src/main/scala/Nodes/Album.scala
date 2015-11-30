package Nodes

import common.AlbumDTO

/**
  * Created by sunito on 11/30/15.
  */
case class Album ( id : Int,
                   profile_id : Int,      // albums are present in profile
                   name : String,
                   description : String,
                   pics : Option[List[Int]],      // pics inside album
                   activity : Option[Int])
  extends  basic {

  def getDTO() = {
    AlbumDTO(this.id, this.profile_id, this.name, this.description)
  }
}