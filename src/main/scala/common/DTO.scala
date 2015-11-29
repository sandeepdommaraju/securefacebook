package common

/**
  * Created by sunito on 11/28/15.
  */

case class UserDTO  ( id : Int,
                      handle : String,
                      first_name : String,
                      last_name : String,
                      sex : String,
                      birthday : String)

case class UserProfileDTO ( id : Int,
                            userOrPageId : Int,
                            userOrPage : Boolean,
                            description : String,
                            email : String,
                            pic : String)