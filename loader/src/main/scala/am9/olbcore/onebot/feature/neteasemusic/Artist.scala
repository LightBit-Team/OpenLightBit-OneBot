package am9.olbcore.onebot.feature.neteasemusic

import com.google.gson.annotations.Expose

class Artist extends Serializable{
  @Expose
  var id: Long = 0
  @Expose
  var name: String = null
}
