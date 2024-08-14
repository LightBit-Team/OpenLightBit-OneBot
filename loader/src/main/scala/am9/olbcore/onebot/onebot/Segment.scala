package am9.olbcore.onebot.onebot

import com.google.gson.annotations.{Expose, SerializedName}

class Segment (
                         @SerializedName("type") 
                         @Expose 
                         theType: String, 
                         @Expose 
                         map: java.util.Map[String, String]
                       ) {
  @SerializedName("type")
  @Expose
  val theRealType: String = theType
  @Expose
  val data: java.util.Map[String, String] = map
}
