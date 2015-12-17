import java.security._
import java.security.spec.X509EncodedKeySpec

import security.{DigitalSignature, RSA}
import spray.json._

/**
  * Created by sunito on 11/27/15.
  */
object Foo extends App with DigitalSignature {

  /*case class Country(name : String, states : List[State], capital : Capital)

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


 */


  /*val serverPriKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKa9slC24rZSzRXHfoCTIFVCZekvybWoTsKS9Tn4CU5VwN68enzjay6ZksEJmk4DednSJuzrdAhD63oxbeaPQDKAHVjqOztb5bAACUYte4EE/rDIpdODqyw+YjnVpwv7ygPD5ZlmsSki26cPiVcU75TV2T1vxeTSqwhoZkr9GULnAgMBAAECgYAT5f16rmid04HQAI+zLlukcRkSW/aZbC2CAOGs+lG5/HfY3OtNLiKjXAZsbQXyG24WNYMZsPuW5OwcF/wCkMPbUYjWaDC5wOJJUMdCvScHIpsLHBy/evbf5+WlSJ7Pl7/ioYWIbS1V94CmEmYPw3Jt7+wQZc/IUSj/lFrNyT9gyQJBAN3WA6V+WFlfgNOUQJcRtAMNl6WyMn3WmwJ5qswGzZDDIRmKivwkj6W7i7y53XsLKRNBN45/MPpAPv9C9PgBGfsCQQDAa4l5yu5IV04SJ3Y9KOIRBowWa3nA2gFRMvx8alOcPaJdoCf0+3Wejsny/5YYKuGV0N5gRZ4+bwSL64XOuHMFAkBLQoI2MSkUGRyBq3hGSFFD/+aE5nHO03H/YvuZAG3ZQPoHykLgzB6X4YrE2mHTjrO+vo90np4lKIq22yZ0xRrlAkAM/TVii1haecpDX3aT2laoX5DFUqv9YLoGCTMSEvth6Kc1OsJ3vyHJekoXpTk0mHBx9I+OYCS2gjgEWfnGYp0hAkBP65blwFN6+Kq3mfXvyGJ876T1gz9MyU+lMVQ0hiItcKAPBk4i9vlq7ghpzol1fU0d0bpZhxSCQ1Tnx/dKXb9E"
  val sData = "{\"userOrPageId\":100004,\"email\":\"email\",\"description\":\"description: 10\",\"id\":300004,\"pic\":\"profile-pic:100004\",\"userOrPage\":true}"
  println("Before Signing !!")
  sign(sData, KeyFactory.getInstance("RSA").generatePrivate(new X509EncodedKeySpec(decodeBASE64(serverPriKey))))

  val serverPubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCmvbJQtuK2Us0Vx36AkyBVQmXpL8m1qE7CkvU5+AlOVcDevHp842sumZLBCZpOA3nZ0ibs63QIQ+t6MW3mj0AygB1Y6js7W+WwAAlGLXuBBP6wyKXTg6ssPmI51acL+8oDw+WZZrEpItunD4lXFO+U1dk9b8Xk0qsIaGZK/RlC5wIDAQAB"
  val cData = "{\"userOrPageId\":100004,\"email\":\"email\",\"description\":\"description: 10\",\"id\":300004,\"pic\":\"profile-pic:100004\",\"userOrPage\":true}"
  val sign = "OaXCK6K6tLpo3xIPEuvYPbvMKhbUfZgU3+639poEa2hynkyp+bbVNI1NTmR0gRv7vFK3UsKdQQrGCJBbdcX1ZdxhJlc+u4bftok1esl2aA7okiYQZzYvrkpRCMVpit+xMD09P3P/HfhU8El4WOlMbTroRGS+OazFuJnyLYnkC0M="

  println("Before Verifying !!")
  verify(sign + "#sep#" + cData, getPublicKey(decodeBASE64(serverPubKey)))
*/

  val shaFirst: MessageDigest = MessageDigest.getInstance("SHA-256")
  val shaSecond: MessageDigest = MessageDigest.getInstance("SHA-256")

  val fooData : String = "fooo/-b/aar"

  val hashedFirst = encodeBASE64(shaFirst.digest(fooData.getBytes("UTF-8")))
  val hashedSecond = encodeBASE64(shaSecond.digest(fooData.getBytes("UTF-8")))

  println("hashedFirst: " + hashedFirst)
  println("hashedSecond: " + hashedSecond)

  println(hashedFirst.equals(hashedSecond))

}

