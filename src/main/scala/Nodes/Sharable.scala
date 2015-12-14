package Nodes

/**
  * Created by sunito on 12/14/15.
  */
case class Viewer( friendId : Int, encryptedAES : String) //AES key of this sharable encrypted with
case class Sharable(id : Int,
                    viewers : List[Viewer])
  extends basic {

}
