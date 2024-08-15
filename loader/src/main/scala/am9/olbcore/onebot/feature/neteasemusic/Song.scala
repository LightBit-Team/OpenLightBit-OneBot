package am9.olbcore.onebot.feature.neteasemusic

import com.google.gson.annotations.Expose

class Song {
  @Expose
  var id: Long = 0
  @Expose
  var name: String = null
  @Expose
  var artists: java.util.List[Artist] = null
  //todo
}
