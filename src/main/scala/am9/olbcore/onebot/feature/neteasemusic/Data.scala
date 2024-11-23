package am9.olbcore.onebot.feature.neteasemusic

import com.google.gson.annotations.Expose

class Data extends Serializable{
  @Expose
  var id: Long = 0
  @Expose
  var url: String = null
  @Expose
  var fee: Int = 0
}
