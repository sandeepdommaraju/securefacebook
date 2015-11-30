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

case class FriendDTO ( id : Int,
                       handle : String)

case class PageDTO ( id : Int,
                     owner_user_id : Int,
                     page_name : String)

case class PostDTO ( id : Int,
                     authorId : Int,
                     postOnPage : Boolean,
                     post_msg : String)