package am9.olbcore.onebot.feature.cave

import com.google.gson.annotations.Expose

class Comment {
  @Expose
  var sender: Long = 0
  @Expose
  var content: String = null
}
