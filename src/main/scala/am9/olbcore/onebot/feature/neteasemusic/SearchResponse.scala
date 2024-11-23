package am9.olbcore.onebot.feature.neteasemusic

import com.google.gson.annotations.Expose

class SearchResponse extends Serializable{
  @Expose
  var result: Result = null
  @Expose
  var code: Int = 0
}
