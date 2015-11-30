import Data.FirstClassData
import Nodes.{Post, Page, Profile, User}
import akka.actor.{Actor, ActorSystem}
import common.{PostDTO, PageDTO, FriendDTO, UserDTO}

/**
  * Created by sunito on 11/28/15.
  */

case class getUser(id : Int)
case class saveUser(userDTO : UserDTO) //id : Int, handle : String, first_name : String, last_name : String, sex : String, birthday : String
case class deleteUser(id : Int)

case class getUserProfile(id : Int)
case class saveUserProfile(profile: Profile)
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

class Worker ( actorSys : ActorSystem) extends Actor with FirstClassData{

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


    case saveUserProfile(Profile(id, userOrPageId, userOrPage, description, email, pic))
          => val userId = userOrPageId
             val user : User = userMap.get(userId)
             println(userMap)
             val m_user = user.copy(u_profile_id = Some(id))
             userMap.put(userId, m_user)
             profileMap.put(id, new Profile(id, userOrPageId, userOrPage, description, email, pic))
             //println(profileMap)
             sender ! "saved user profile: " + id

    case deleteUserProfile(id : Int)
          =>  profileMap.remove(id)
              sender ! "deleted user profile: " + id

    /**
      * CRUD of friend List
      */

    case getFriendList(id : Int)
          =>  val friendList : List[FriendDTO] = userMap.get(id).u_friends.getOrElse(null)
              sender ! friendList

    case saveFriendList(userId : Int, friendList : List[FriendDTO])
          =>  val user : User = userMap.get(userId)
              val m_user = user.copy(u_friends = Some(friendList))
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
              }
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
             println(userMap)
             sender ! "saved page: " + pageDTO.id +" for user: " + pageDTO.owner_user_id

    /**
      * CRUD of page profiles
      */

    case getPageProfile(pageId : Int)
          => val t_page : Page = pageMap.get(pageId)
             //println(pageMap)
             //println(t_page)
             val profileId = t_page.page_profile
             //println(profileId)
             //println(profileId.getOrElse(1))
             val profile : Profile = profileMap.get(profileId.getOrElse(1))
             //println(profileMap)
             sender ! profile

    case savePageProfile(profile : Profile)
          => val pageId = profile.userOrPageId
             profileMap.put(profile.id, profile)
             //println(profileMap)
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
                postMap.put(post.id, new Post(post.id, pageId, post.postOnPage, post.post_msg, None, None))
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

    case default => println("Default message")
  }
}
