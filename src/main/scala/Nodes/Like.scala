package Nodes

/**
  * Created by sunito on 11/30/15.
  */
case class Like ( id : Int,
                  like_author : Int, // the one who clicks like
                  activity_id : Int
                )
  extends basic
