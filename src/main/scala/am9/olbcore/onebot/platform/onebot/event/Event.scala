package am9.olbcore.onebot.platform.onebot.event

import com.google.gson.annotations.Expose

abstract class Event {
  @Expose
  var time: Long = 0
  @Expose
  var self_id: Long = 0
  @Expose
  var post_type: String = null
}
