package security

import java.security._
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher


/**
  * Created by sunito on 12/11/15.
  */
trait RSA extends coders{

  def getKeyPair : KeyPair = {
    KeyPairGenerator.getInstance("RSA").generateKeyPair()
  }

  def encryptRSA (plainText : String, pubKey : PublicKey) : String = {
    val cipher : Cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    cipher.init(Cipher.ENCRYPT_MODE, pubKey)
    val cipherText : Array[Byte] = cipher.doFinal(plainText.getBytes("UTF8"))
    return encodeBASE64(cipherText)
  }

  def decryptRSA (cipherText : String, priKey : PrivateKey) : String = {
    val cipher : Cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    cipher.init(Cipher.DECRYPT_MODE, priKey)
    val plaintext: Array[Byte] = cipher.doFinal(decodeBASE64(cipherText))
    return new String(plaintext, "UTF8")
  }

  def getPublicKey(bytes : Array[Byte]) : PublicKey = {
      KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bytes))
  }

}
