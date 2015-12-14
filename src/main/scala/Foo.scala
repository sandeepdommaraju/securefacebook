import java.security.{MessageDigest, PublicKey, PrivateKey, KeyPair}

import security.{DigitalSignature, RSA}
import spray.json._

/**
  * Created by sunito on 11/27/15.
  */
object Foo extends App with DigitalSignature{

 case class Country(name : String, states : List[State], capital : Capital)

  case class State(stateName : String, cm : String)

  case class Capital(name : String, id : Int)

  val ap = State("andhra" , "cbn")

  val ama = Capital("amaravathi", 101)

  val tel = State("telangana", "kcr")

  val hyd = Capital("hyderabad", 102)

  val india = Country("india", List(ap, tel), hyd)

  object MyJsonProtocol extends DefaultJsonProtocol {
    implicit val stateFormat = jsonFormat2(State)

    implicit val capitalFormat = jsonFormat2(Capital)

    implicit val countryFormat = jsonFormat3(Country)
  }

  import MyJsonProtocol._

  val apJSON = ap.toJson

  val telJSON = tel.toJson

  val indiaJSON = india.toJson

  //println(apJSON)

  //println(telJSON)

  //println(indiaJSON)

  val flJSON = """{ "stateName" : "Florida", "cm" : "Martson" }"""

  val gaJSON = """{ "stateName" : "Georgia", "cm" : "Martin" }"""

  val flAST = flJSON.parseJson

  val gaAST = gaJSON.parseJson

  val fl = flAST.convertTo[State]

  val ga = gaAST.convertTo[State]

  val usa = Country(name = "USA", List(fl, ga), ama)

  val usaJSON = usa.toJson

  //println(usaJSON)
  //println("Finish")

  val cali = """{ "stateName" : "California", "cm" : "Andy" }"""
  val cc = cali.parseJson
  val str = "digital-signature" + "#sep#" + usaJSON
  val sp = str.split("#sep#")
  println(sp(0))
  println(sp(1).parseJson.convertTo[Country])

  val data = cc.toString()  //sp(1).parseJson.convertTo[Country].toString

  println("data: " + data)

  val keyPair : KeyPair = getKeyPair
  val priKey : PrivateKey = keyPair.getPrivate
  val pubKey : PublicKey = keyPair.getPublic
/*

  val clientSideSummary  = sha.digest(data.getBytes("UTF-8"))
  val clientSideEncodedSummary : String = encodeBASE64(clientSideSummary)
  val encryptedSummary : String = encryptWithPrivateKey(clientSideEncodedSummary, priKey)

  println("clientSideData: " + data)
  println(clientSideSummary + " ==equals== " + clientSideEncodedSummary)

  val transferData : String = encryptedSummary + "#sep#" + data

  // server side
  val splitter = transferData.split("#sep#")
  val serverSideEncryptedSummary = splitter(0)
  val serverSideData = splitter(1)

  val serverSideDecryptedSummary : String = decryptWithPublicKey(serverSideEncryptedSummary, pubKey)
  val serverSideComputedSummary = encodeBASE64(sha.digest(serverSideData.getBytes("UTF-8")))

  println("serverSideData: " + serverSideData)
  println(serverSideDecryptedSummary + " ==equals== " + serverSideComputedSummary)


  val foo = data
  println("foo: " + foo + " pubKey: " + pubKey + " priKey: " + priKey)

  val signedData = sign(foo, priKey)
  println("signedData: " + signedData)

  println(verify(signedData, pubKey))
 */

  var friendIds : List[Int] = List()
  friendIds = friendIds :+ 1
 friendIds = friendIds :+ 2
 friendIds = friendIds :+ 3



 var pubKeys : List[String] = List("a", "b", "c")

 val send = friendIds.toJson+"#sep#" + pubKeys.toJson

 val fs = send.split("#sep#")(0)
 val ps = send.split("#sep#")(1)

 val fsl = fs.parseJson.convertTo[List[Int]]
 val psl = ps.parseJson.convertTo[List[String]]

 println(fsl)
 println(psl)


}

