package common

import java.security.{MessageDigest, PublicKey}
import java.util.concurrent.atomic.AtomicInteger

import Nodes._
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol

/**
  * Created by sunito on 11/27/15.
  */
class Util {

  var IDGen : AtomicInteger = new AtomicInteger()

}

/*object shaObj {
  val sha: MessageDigest = MessageDigest.getInstance("SHA-256")
}*/

object JsonImplicits extends DefaultJsonProtocol with SprayJsonSupport{

  implicit val commentFormat = jsonFormat4(Comment)

  implicit val friendRequestFormat = jsonFormat3(FriendRequest)

  implicit val postFormat = jsonFormat5(Post)

  implicit val pageFormat = jsonFormat6(Page)

  implicit val profileFormat = jsonFormat7(Profile)

  implicit val friendDTOFormat = jsonFormat2(FriendDTO)

  implicit val userFormat = jsonFormat12(User)

  implicit val userDTOFormat = jsonFormat6(UserDTO)

  implicit val profileDTOFormat = jsonFormat6(ProfileDTO)

  implicit val pageDTOFormat = jsonFormat3(PageDTO)

  implicit val pagePostsDTOFormat = jsonFormat4(PostDTO)

  implicit val albumDTOFormat = jsonFormat4(AlbumDTO)

  implicit val picDTOFormat = jsonFormat3(PicDTO)

  implicit val viewerFormat = jsonFormat2(Viewer)

  implicit val sharableFormat = jsonFormat2(Sharable)

  implicit val sharablePicFormat = jsonFormat4(SharablePic)

  implicit val friendSharablePicFormat = jsonFormat3(FriendSharablePic)

}