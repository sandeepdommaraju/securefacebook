package Nodes

import common.PageDTO

/**
  * Created by sunito on 11/26/15.
  */
case class Page (id : Int,                   // unique page id
                 owner_user_id : Int,        // owner of the page
                 page_name : String,          // page name
                 page_profile : Option[Int], // pageProfile
                 posts : Option[List[Int]],  // list of posts in this page (assumption: only owner can post in page)
                 activity : Option[Int])  // list of userIds who liked this page
        extends  basic {

  def getDTO() = {
    PageDTO(this.id, this.owner_user_id, this.page_name)
  }
}
