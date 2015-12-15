package Nodes

import common.PostDTO

/**
  * Created by sunito on 11/26/15.
  */
case class Post (id : Int,
                 authorId : Int,       //either pageId or userId (user can post only on his wall)
                                       // if this Post is a pic -- ???
                 postOnPage : Boolean, // true if post is on page; false if post is on wall
                 post_msg : String,    // postOrPic
                 //comments : Option[List[Int]], // List of commentIds
                 //likes : Option[List[Int]]     // List of userIds who liked
                 var activity : Option[Int]
                 )
           extends basic {

  //Pictures in album can be viewed as Posts as well

  def getDTO() = {
    PostDTO(this.id, this.authorId, this.postOnPage, this.post_msg)
  }

  def createActivity = {
    this.activity = Some(50000)

  }

}

case class SharablePost (postId : Int, sharableId : Int, ivector : String, post: Post)