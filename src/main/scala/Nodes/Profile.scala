package Nodes

import common.ProfileDTO

/**
  * Created by sunito on 11/26/15.
  */
case class Profile ( id : Int,
                     userOrPageId : Int,
                     userOrPage : Boolean,
                     description : String,
                     email : String,
                     pic : String, // profile pic is a String !!
                     album : Option[Int])
                   extends basic {

  def getDTO() = {
    ProfileDTO(this.id, this.userOrPageId, this.userOrPage, this.description, this.email, this.pic)
  }

}
