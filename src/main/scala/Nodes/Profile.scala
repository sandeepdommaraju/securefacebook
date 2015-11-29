package Nodes

/**
  * Created by sunito on 11/26/15.
  */
case class Profile ( id : Int,
                     userOrPageId : Int,
                     userOrPage : Boolean,
                     description : String,
                     email : String,
                     pic : String) // profile pic is a String !!
                   extends basic
