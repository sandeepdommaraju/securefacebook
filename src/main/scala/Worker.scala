import Data.FirstClassData
import Nodes.{Profile, User}
import akka.actor.{Actor, ActorSystem}
import common.UserDTO

/**
  * Created by sunito on 11/28/15.
  */

case class getUser(id : Int)
case class saveUser(userDTO : UserDTO) //id : Int, handle : String, first_name : String, last_name : String, sex : String, birthday : String
case class deleteUser(id : Int)

case class getUserProfile(id : Int)
case class saveUserProfile(profile: Profile)
case class deleteUserProfile(id : Int)


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
          =>  val user : User = new User(id, handle, first_name, last_name, sex, birthday, null, null, null, null, null, null)
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
             val m_user = user.copy(u_profile_id = Some(id))
             userMap.put(userId, m_user)
             profileMap.put(id, new Profile(id, userOrPageId, userOrPage, description, email, pic))
             //println(profileMap)
             sender ! "saved user profile: " + id

    case deleteUserProfile(id : Int)
          =>  profileMap.remove(id)
              sender ! "deleted user profile: " + id


    case default => println("Default message")
  }
}
