package Nodes

/**
  * Created by sunito on 11/28/15.
  */
case class Comment (id : Int,
                    postId : Int,
                    comment_author : Int,
                    comment : String)
            extends basic
