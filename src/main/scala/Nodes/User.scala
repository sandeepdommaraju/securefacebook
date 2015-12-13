package Nodes

import common.{UserDTO, FriendDTO}

/**
  * Created by sunito on 11/26/15.
  */

case class User ( id : Int,
                  handle : String,
                  first_name : String,
                  last_name : String,
                  sex : String,
                  birthday : String,
                  u_profile_id : Option[Int],
                  u_pages : Option[List[Int]],           //List of pages he is a member of
                  u_friends : Option[List[Int]],         //List of friends
                  u_friend_requests : Option[List[Int]], //List of pending FriendRequests
                  u_wall : Option[List[Int]],            //List of posts on user wall -- timeline
                  u_newsFeed : Option[List[Int]])        //List of posts of my friends on their walls
              extends basic {

  def getDTO() = {
    UserDTO(this.id, this.handle, this.first_name, this.last_name, this.sex, this.birthday)
  }


}

