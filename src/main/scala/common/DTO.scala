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

// user can have a profile, page can have a profile
case class ProfileDTO(id : Int,
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

case class AlbumDTO ( id : Int,
                      profile_id : Int,
                      name : String,
                      description : String)

case class PicDTO ( id : Int,
                    description : String,
                    data : String)

case class ActivityDTO( id : Int,
                        owner_id : Int,
                        owner_type : String)