package common

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

object JsonImplicits extends DefaultJsonProtocol with SprayJsonSupport{

  implicit val commentFormat = jsonFormat4(Comment)

  implicit val friendRequestFormat = jsonFormat3(FriendRequest)

  implicit val postFormat = jsonFormat6(Post)

  implicit val pageFormat = jsonFormat6(Page)

  implicit val profileFormat = jsonFormat6(Profile)

  implicit val friendDTOFormat = jsonFormat2(FriendDTO)

  implicit val userFormat = jsonFormat12(User)

  implicit val userDTOFormat = jsonFormat6(UserDTO)

  implicit val pageDTOFormat = jsonFormat3(PageDTO)

  implicit val pagePostsDTOFormat = jsonFormat4(PostDTO)

}