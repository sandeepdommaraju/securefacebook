package security

import java.security._
import java.util
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}
import javax.crypto.{Cipher, KeyGenerator, SecretKey}

/**
  * Created by sunito on 12/13/15.
  */
trait AES extends coders{

  val ALGORITHM = "AES/CBC/PKCS5Padding"
  val CHARSET = "UTF-8"

  def encryptAES(key: SecretKey, plainText: String, iVector : String): Array[Byte] = {
    //Instantiate the cipher
    val x = iVector.getBytes("UTF-8")
    val iv:IvParameterSpec = new IvParameterSpec(util.Arrays.copyOfRange(x, 0, 16))
    val cipher = Cipher.getInstance(ALGORITHM)
    cipher.init(Cipher.ENCRYPT_MODE, key, iv)
    val encryptedTextBytes = cipher.doFinal(plainText.getBytes(CHARSET))
    encryptedTextBytes
  }

  def decryptAES(encryptedText: Array[Byte], key: SecretKey, iVector:String): String = {
    //Instantiate the cipher
    val x = iVector.getBytes("UTF-8")
    val iv:IvParameterSpec = new IvParameterSpec(util.Arrays.copyOfRange(x, 0, 16))
    val cipher = Cipher.getInstance(ALGORITHM)
    cipher.init(Cipher.DECRYPT_MODE, key, iv)

    val decryptedTextBytes = cipher.doFinal(encryptedText)
    //new String(decryptedTextBytes)
    encodeBASE64(decryptedTextBytes)
  }

  def generateAESKey: SecretKey = {
    val rand = new SecureRandom()
    val keyGen = KeyGenerator.getInstance("AES")
    keyGen.init(rand)
    keyGen.generateKey()
  }

  def getAESSecretKey (aesKeyStr : String) : SecretKey = {
    val bytes : Array[Byte] = decodeBASE64(aesKeyStr)
    val originalKey : SecretKey = new SecretKeySpec(bytes, 0, bytes.length, "AES")
    originalKey
  }


  def main(args: Array[String]) {
    val plainText = "foo-bar-foo-foo-bar-bar"
    val key = generateAESKey
    val iVector = "foo"

    println("alice: " + plainText)
    val enc = encryptAES(key, plainText, iVector)
    val text = decryptAES(enc, key, iVector)
    println("bob: " + text)

    val aesStr = encodeBASE64(key.getEncoded)
    println(aesStr)
    println(encodeBASE64(getAESSecretKey(aesStr).getEncoded))

  }


}
