import java.util.concurrent.atomic.AtomicInteger

import Data.FirstClassData
import Nodes._
import akka.actor.{Actor, ActorSystem}
import common._

/**
  * Created by sunito on 11/28/15.
  */

case class getUser(id : Int)
case class saveUser(userDTO : UserDTO) //id : Int, handle : String, first_name : String, last_name : String, sex : String, birthday : String
case class deleteUser(id : Int)

case class getUserProfile(id : Int)
case class saveUserProfile(profile: ProfileDTO)
case class deleteUserProfile(id : Int)

case class getFriendList(id : Int)
case class saveFriendList(id : Int, friendList : List[FriendDTO])
//case class deleteFriend(id : Int, friend_id : Int)

case class getUserPages(userId : Int)
case class savePage(pageDTO: PageDTO)
case class deletePage(pageId : Int)

case class getPageProfile(pageId : Int)
case class savePageProfile(profile : Profile)
case class deletePageProfile(pageId : Int)

case class getPagePosts(pageId : Int)
case class savePagePosts(pageId : Int, posts : List[PostDTO])
case class deletePagePosts(pageId : Int)

// Posts on user's wall - only he can post on his wall
case class getUserPosts(userId : Int)
case class saveUserPosts(userId : Int, posts : List[PostDTO])
case class deleteUserPosts(userId : Int) // on his wall


case class getUserProfileAlbum(userId : Int, profileId : Int)
case class saveUserProfileAlbum(userId : Int, profileId : Int, albumDTO: AlbumDTO)
case class deleteUserProfileAlbum(userId : Int, profileId : Int)


case class getUserProfileAlbumPics(userId : Int, profileId : Int)
case class saveUserProfileAlbumPics(userId : Int, profileId : Int, pics : List[PicDTO])
case class deleteUserProfileAlbumPic(userId : Int, profileId : Int, picId : Int)

case class getCommentsOnUserWallPost(userId : Int, postId : Int)
case class saveCommentsOnUserWallPost(userId : Int, postId : Int, comments : List[Comment])
case class deleteCommentOnUserWallPost(userId : Int, postId : Int, commentId : Int)

class Worker ( actorSys : ActorSystem) extends Actor with FirstClassData{

  val activity_base_num = 500000
  val activity_atomic_gen = new AtomicInteger(activity_base_num)

  def getNextActivityId() : Int= {
      println("Getting next ActivityId Int!!!")
      activity_atomic_gen.addAndGet(1)
  }

  def receive = {

    case "init" => println("initialized Worker!")

    /**
      *  CRUD of user basic details
      */
    case getUser(id : Int)
          =>  val user : User = userMap.get(id)
              val userDTO : UserDTO = UserDTO(user.id, user.handle, user.first_name, user.last_name, user.sex, user.birthday)
              sender ! userDTO //UserDTO(1, "sandom", "sandeep", "dommaraju", "Male", "05-05-1988")

    case saveUser(UserDTO(id, handle, first_name, last_name, sex, birthday))
          =>  val user : User = new User(id, handle, first_name, last_name, sex, birthday, None, None, None, None, None, None)
              userMap.put(id , user)
              println("saving user: " + id)
              println(sender)
              sender ! "saved user: " + id

    case deleteUser(id : Int)
          => userMap.remove(id)
             sender ! "deleted user: " + id


    /**
      * CRUD of user profile
      */
    case getUserProfile(id : Int)
          =>  val t_user = userMap.get(id)
              val profileId = t_user.u_profile_id
              val profile : Profile = profileMap.get(profileId.getOrElse(1))
              //println(profileMap)
              sender ! profile


    case saveUserProfile(ProfileDTO(id, userOrPageId, userOrPage, description, email, pic))
          => val userId = userOrPageId
             val user : User = userMap.get(userId)
             println(userMap)
             val m_user = user.copy(u_profile_id = Some(id))
             userMap.put(userId, m_user)
             profileMap.put(id, new Profile(id, userOrPageId, true, description, email, pic, None))
             //println(profileMap)
             sender ! "saved user profile: " + id

    case deleteUserProfile(id : Int)
          =>  profileMap.remove(id)
              sender ! "deleted user profile: " + id

    /**
      * CRUD of friend List
      */

    case getFriendList(id : Int)
          =>  //println(userMap.get(id))
              //val friendList : List[FriendDTO] = userMap.get(id).u_friends.getOrElse(null)
              //sender ! friendList
              ""

    case saveFriendList(userId : Int, friendList : List[FriendDTO])
          =>  val user : User = userMap.get(userId)
              /*val m_user = user.copy(u_friends = Some(friendList))
              userMap.put(userId, m_user)
              val currUserFriendDTO : FriendDTO = new FriendDTO(userId, m_user.handle)
              for (friend <- friendList) {
                val f_id = friend.id
                val f_user : User = userMap.get(f_id)
                val f_user_friends : List[FriendDTO] = f_user.u_friends.getOrElse(null)
                if (f_user_friends == null) {
                  val fn_user = f_user.copy(u_friends = Some(List(currUserFriendDTO)))
                  userMap.put(f_id, fn_user)
                } else {
                  val fn_user_friends = f_user_friends :+ currUserFriendDTO
                  val fn_user = f_user.copy(u_friends = Some(fn_user_friends))
                  userMap.put(f_id, fn_user)
                }
              }*/
              //println(userMap)
              sender ! "saved friend list for user: " + userId

    /**
      * CRUD of user pages
      */

    case getUserPages(userId : Int)
          =>  val pageIdList : List[Int] = userMap.get(userId).u_pages.getOrElse(null)
              if (pageIdList == null) {
                sender ! List()
              } else {
                var pages: List[PageDTO] = List()
                for (pageId <- pageIdList) {
                  val page: Page = pageMap.get(pageId)
                  val pageDTO: PageDTO = page.getDTO()
                  pages = pages :+ pageDTO
                }
                println(pages)
                sender ! pages
              }

    case savePage(pageDTO: PageDTO)
          => val page : Page = new Page(pageDTO.id, pageDTO.owner_user_id, pageDTO.page_name, None, None, None)
             pageMap.put(pageDTO.id, page)
             //update user page list
             val user : User = userMap.get(pageDTO.owner_user_id)
             val pageL : List[Int] = user.u_pages.getOrElse(null)
             var t_pageL : List[Int] = List()
             if (pageL == null) {
               t_pageL = t_pageL :+ pageDTO.id
             } else {
               t_pageL = t_pageL ::: pageL
               t_pageL = t_pageL :+ pageDTO.id
             }
             val t_user : User = user.copy(u_pages = Some(t_pageL))
             userMap.put(pageDTO.owner_user_id, t_user)
             //println(userMap)
             sender ! "saved page: " + pageDTO.id +" for user: " + pageDTO.owner_user_id

    /**
      * CRUD of page profiles
      */

    case getPageProfile(pageId : Int)
          => val t_page : Page = pageMap.get(pageId)
             //println("currPageMap: " +pageMap)
             //println("currpage: " + t_page)
             val profileId = t_page.page_profile
             //println("currpage_profileId: " + profileId)
             //println("profileId_getorelse: " + profileId.getOrElse(1))
             val profile : Profile = profileMap.get(profileId.getOrElse(1))
             //println("currpage_profilemap" + profileMap)
             sender ! profile

    case savePageProfile(profile : Profile)
          => val pageId = profile.userOrPageId
             profileMap.put(profile.id, profile)
             //println("saving profile in Worker: " + profileMap)
             val page : Page = pageMap.get(pageId)
             val m_page = page.copy(page_profile = Some(profile.id))
             pageMap.put(pageId, m_page)
             sender ! "saved page profile: " + profile.id + " on page: " + pageId

    /**
      * CRUD of page posts
      */

    case getPagePosts(pageId : Int)
          =>  val postIdList : List[Int] = pageMap.get(pageId).posts.getOrElse(List())
              var posts : List[PostDTO] = List()
              for (postId <- postIdList) {
                val post : PostDTO = postMap.get(postId).getDTO()
                posts = posts :+ post
              }
              sender ! posts


    case savePagePosts(pageId : Int, posts : List[PostDTO])
          =>  val page : Page = pageMap.get(pageId)
              var postIdList : List[Int] = List()
              for (post <- posts) {
                postMap.put(post.id, new Post(post.id, pageId, post.postOnPage, post.post_msg, None))
                postIdList = postIdList :+ post.id
              }
              var curr_posts : List[Int] = page.posts.getOrElse(List())
              /*if (curr_posts == null) {
                curr_posts = postIdList
              } else {
                curr_posts = curr_posts ::: postIdList
              }*/
              curr_posts = curr_posts ::: postIdList
              pageMap.put(pageId, page.copy(posts = Some(curr_posts)))
              sender ! "saved posts list for page: " + pageId


    /**
      *CRUD of user Posts on his wall
      */

    case getUserPosts(userId : Int)
          =>  val postIdList : List[Int] = userMap.get(userId).u_wall.getOrElse(List())
              var posts : List[PostDTO] = List()
              for (postId <- postIdList) {
                val post : PostDTO = postMap.get(postId).getDTO()
                posts = posts :+ post
              }
              println("GET posts on wall: " + userId + "  ### " + posts)
              sender ! posts

    case saveUserPosts(userId : Int, posts : List[PostDTO])
          =>  val user : User = userMap.get(userId)
              var postIdList : List[Int] = List()
              for (post <- posts) {
                postMap.put(post.id, new Post(post.id, userId, post.postOnPage, post.post_msg, None))
                postIdList = postIdList :+ post.id
              }
              var curr_posts : List[Int] = user.u_wall.getOrElse(List())
              curr_posts = curr_posts ::: postIdList
              userMap.put(userId, user.copy(u_wall = Some(curr_posts)))
              println("Worker: currentPosts on Wall: " + userId + " :--> " +curr_posts)
              sender ! "saved posts list for user: " + userId

    case deleteUserPosts(userId : Int) // on his wall
          =>  "TODO"

    /**
      * CRUD of user profile Album
      */

    case getUserProfileAlbum(userId : Int, profileId : Int)
          =>  val albumId : Int = profileMap.get(profileId).album.getOrElse(0)
              val albumDTO : AlbumDTO = albumMap.get(albumId).getDTO()
              sender ! albumDTO

    case saveUserProfileAlbum(userId : Int, profileId : Int, albumDTO: AlbumDTO)
          =>  albumMap.put(albumDTO.id, new Album(albumDTO.id, albumDTO.profile_id, albumDTO.name, albumDTO.description, None, None))
              val curr_profile : Profile = profileMap.get(profileId)
              profileMap.put(profileId, curr_profile.copy(album = Some(albumDTO.id)))
              sender ! "saved user profile Album: " + albumDTO.id + " for user: " + userId + " in profile: " + profileId

    case deleteUserProfileAlbum(userId : Int, profileId : Int)
          => "TODO"

    /**
      * CRUD of user profile Album Pics
      */

    case getUserProfileAlbumPics(userId : Int, profileId : Int)
          => val albumId : Int = profileMap.get(profileId).album.getOrElse(0)
             val pics_ids : List[Int] = albumMap.get(albumId).pics.getOrElse(List())
             var pics : List[PicDTO] = List()
             for (pic_id <- pics_ids) {
               pics = pics :+ picMap.get(pic_id).getDTO()
             }
             sender ! pics

     case saveUserProfileAlbumPics(userId : Int, profileId : Int, pics : List[PicDTO])
          => val albumId : Int = profileMap.get(profileId).album.getOrElse(0)
             val album : Album = albumMap.get(albumId)
             var curr_pics : List[Int] = album.pics.getOrElse(List())
             var t_pic_ids : List[Int] = List()
             for (pic <- pics) {
               t_pic_ids = t_pic_ids :+ pic.id
               //picMap.put(pic.id, new Pic(pic.id, pic.album_id, pic.description, pic.data, None))
             }
             curr_pics = curr_pics ::: t_pic_ids
             albumMap.put(albumId, album.copy(pics = Some(curr_pics)))
             sender ! "saved user profile Album Pics: in album" + albumId + " for user: " + userId + " in profile: " + profileId

     case deleteUserProfileAlbumPic(userId : Int, profileId : Int, picId : Int)
          => "TODO"


    /**
      * CRUD of Comments on a Post on user wall
      */

    case getCommentsOnUserWallPost(userId : Int, postId : Int)
          => val post : Post = postMap.get(postId)
             val activityId : Int = post.activity.getOrElse(0)
             val activity : Activity = activityMap.get(activityId)
             val comment_ids : List[Int] = activity.comments.getOrElse(List())
             var comments : List[Comment] = List()
             for (comment_id <- comment_ids) {
               comments = comments :+ commentMap.get(comment_id)
             }
             sender ! comments

    case saveCommentsOnUserWallPost(userId : Int, postId : Int, comments : List[Comment])
          => val post : Post = postMap.get(postId)
             val activityId : Int = post.activity.getOrElse(getNextActivityId())
             //println(postMap)
             //println(activityId)
             postMap.put(post.id, post.copy(activity = Some(activityId)))
             val activity : Activity = activityMap.get(activityId)
             var curr_activity : Activity = activity
             if (activity == null) {
               curr_activity = new Activity(activityId, userId, "user", None, None, None)
             }
             var curr_comment_ids : List[Int] = curr_activity.comments.getOrElse(List())
             for (comment <- comments) {
               curr_comment_ids = curr_comment_ids :+ comment.id
               commentMap.put(comment.id, comment.copy(activity_id = activityId))
             }
             activityMap.put(activityId, curr_activity.copy(comments = Some(curr_comment_ids)))
             sender ! "saved comments on user wall on post: " + postId + " for user: " + userId


    case deleteCommentOnUserWallPost(userId : Int, postId : Int, commentId : Int)
          => ""


    case _ => println("Default message")
  }
}
