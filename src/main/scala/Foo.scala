//import spray.json._
//
///**
//  * Created by sunito on 11/27/15.
//  */
//object Foo /*extends App*/{
//
// case class Country(name : String, states : List[State], capital : Capital)
//
//  case class State(stateName : String, cm : String)
//
//  case class Capital(name : String, id : Int)
//
//  val ap = State("andhra" , "cbn")
//
//  val ama = Capital("amaravathi", 101)
//
//  val tel = State("telangana", "kcr")
//
//  val hyd = Capital("hyderabad", 102)
//
//  val india = Country("india", List(ap, tel), hyd)
//
//  object MyJsonProtocol extends DefaultJsonProtocol {
//    implicit val stateFormat = jsonFormat2(State)
//
//    implicit val capitalFormat = jsonFormat2(Capital)
//
//    implicit val countryFormat = jsonFormat3(Country)
//  }
//
//  import MyJsonProtocol._
//
//  val apJSON = ap.toJson
//
//  val telJSON = tel.toJson
//
//  val indiaJSON = india.toJson
//
//  println(apJSON)
//
//  println(telJSON)
//
//  println(indiaJSON)
//
//  val flJSON = """{ "stateName" : "Florida", "cm" : "Martson" }"""
//
//  val gaJSON = """{ "stateName" : "Georgia", "cm" : "Martin" }"""
//
//  val flAST = flJSON.parseJson
//
//  val gaAST = gaJSON.parseJson
//
//  val fl = flAST.convertTo[State]
//
//  val ga = gaAST.convertTo[State]
//
//  val usa = Country(name = "USA", List(fl, ga), ama)
//
//  val usaJSON = usa.toJson
//
//  println(usaJSON)
//
//  println("Finish")
//
//}

object Foo extends App {

  case class Car (val name : String, val drivers : Option[List[String]])

  val honda : Car = new Car("civic", None)

  //val d : List[String] = honda.drivers.getOrElse(null)

  val someDrivers = honda.drivers

  someDrivers match {
    case Some(v) => println(v)
    case None => println("no drivers")
  }


}