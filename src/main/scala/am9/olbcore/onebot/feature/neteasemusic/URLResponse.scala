package am9.olbcore.onebot.feature.neteasemusic

import com.google.gson.annotations.Expose

class URLResponse extends Serializable{
  @Expose
  var data: java.util.List[Data] = null
  @Expose
  var code: Int = 0
}
