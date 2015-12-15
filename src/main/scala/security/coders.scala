package security

import org.apache.commons.codec.binary.Base64

/**
  * Created by sunito on 12/15/15.
  */
trait coders {

  def encodeBASE64(bytes: Array[Byte]): String = {
    return Base64.encodeBase64String(bytes)
  }

  def decodeBASE64(text:String) : Array[Byte] = {
    return Base64.decodeBase64(text)
  }
}
