package Nodes

/**
  * Created by sunito on 11/26/15.
  */
case class Post (id : Int,
                 authorId : Int,
                 postOnPage : Boolean,
                 comments : List[Int],
                 likes : List[Int],
                 postOrPic : String)
           extends basic

//Pictures in album can be viewed as Posts as well