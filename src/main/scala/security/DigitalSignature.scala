package security

import java.security.{MessageDigest, PublicKey, PrivateKey}
import javax.crypto.Cipher

/**
  * Created by sunito on 12/12/15.
  */
trait DigitalSignature extends RSA{


  def encryptWithPrivateKey (plainText : String, priKey : PrivateKey) : String = {
    val cipher : Cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    cipher.init(Cipher.ENCRYPT_MODE, priKey)
    //val cipherText : Array[Byte] = cipher.doFinal(plainText.getBytes("UTF8"))
    val cipherText : Array[Byte] = cipher.doFinal(decodeBASE64(plainText))
    encodeBASE64(cipherText)
  }

  def decryptWithPublicKey (cipherText : String, pubKey : PublicKey) : String = {
    val cipher : Cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    cipher.init(Cipher.DECRYPT_MODE, pubKey)
    val plaintext: Array[Byte] = cipher.doFinal(decodeBASE64(cipherText))
    encodeBASE64(plaintext)
    //return new String(plaintext, "UTF8")
    //encodeBASE64(plaintext)
  }



  def sign(data : String, priKey : PrivateKey) : String = {
    val sha: MessageDigest = MessageDigest.getInstance("SHA-256")
    val bytes = data.getBytes("UTF-8")
    sha.update(bytes)
    val shabytes = sha.digest()
    val hashedData = encodeBASE64(shabytes)
    encryptWithPrivateKey(hashedData, priKey) + "#sep#" + data
  }

  def verify(dataFromNetwork : String, pubKey : PublicKey) : Boolean = {
    val sha: MessageDigest = MessageDigest.getInstance("SHA-256")
    val pair = dataFromNetwork.split("#sep#")
    val mydata : String = pair(1)
    val bytes = mydata.getBytes("UTF-8")
    sha.update(bytes)
    val shabytes = sha.digest()
    val hashedData = encodeBASE64(shabytes)
    val decryptedData = decryptWithPublicKey(pair(0), pubKey)
    hashedData.equals(decryptedData)
  }


}
