package Nodes

/**
  * Created by sunito on 11/30/15.
  */
case class Pic ( id : Int,
                 album_id : Int,
                 description : String,
                 activity : Option[Int]
               ) extends basic
