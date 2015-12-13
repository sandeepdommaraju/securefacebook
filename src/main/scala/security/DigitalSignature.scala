package security

import java.security.{MessageDigest, PublicKey, PrivateKey}
import javax.crypto.Cipher

/**
  * Created by sunito on 12/12/15.
  */
trait DigitalSignature extends RSA{

  val sha : MessageDigest = MessageDigest.getInstance("SHA-256")

  def encryptWithPrivateKey (plainText : String, priKey : PrivateKey) : String = {
    val cipher : Cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    cipher.init(Cipher.ENCRYPT_MODE, priKey)
    val cipherText : Array[Byte] = cipher.doFinal(plainText.getBytes("UTF8"))
    return encodeBASE64(cipherText)
  }

  def decryptWithPublicKey (cipherText : String, pubKey : PublicKey) : String = {
    val cipher : Cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    cipher.init(Cipher.DECRYPT_MODE, pubKey)
    val plaintext: Array[Byte] = cipher.doFinal(decodeBASE64(cipherText))
    return new String(plaintext, "UTF8")
  }

  def sign(data : String, priKey : PrivateKey) : String = {
    encryptWithPrivateKey(encodeBASE64(sha.digest(data.getBytes("UTF-8"))), priKey) + "#sep#" + data
  }

  def verify(dataFromNetwork : String, pubKey : PublicKey) : Boolean = {
    val pair = dataFromNetwork.split("#sep#")
    val hashedData = encodeBASE64(sha.digest(pair(1).getBytes("UTF-8")))
    val decryptedData = decryptWithPublicKey(pair(0), pubKey)
    return hashedData.equals(decryptedData)
  }
}
