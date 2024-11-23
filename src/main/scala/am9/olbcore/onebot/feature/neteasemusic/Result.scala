package am9.olbcore.onebot.feature.neteasemusic

import com.google.gson.annotations.Expose

class Result extends Serializable{
  @Expose
  var songs: java.util.List[Song] = null
  @Expose
  var songCount: Int = 0

}
