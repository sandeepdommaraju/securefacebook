package Nodes

/**
  * Created by sunito on 11/26/15.
  */
case class Post (id : Int,
                 authorId : Int,      //either pageId or userId (user can post only on his wall)
                                      // if this Post is a pic -- ???
                 postOnPage : Boolean, // true if post is on page; false if post is on wall
                 comments : List[Int],// List of commentIds
                 likes : List[Int],   // List of userIds who liked
                 post_msg : String)   // postOrPic
           extends basic

//Pictures in album can be viewed as Posts as well