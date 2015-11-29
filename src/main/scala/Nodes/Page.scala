package Nodes

/**
  * Created by sunito on 11/26/15.
  */
case class Page (id : Int,
                 owner_user_id : Int,
                 page_profile : Option[Int],
                 posts : Option[List[Int]],
                 likes : Option[List[Int]])
        extends  basic
