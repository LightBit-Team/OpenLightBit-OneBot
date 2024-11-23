package am9.olbcore.onebot.platform.onebot.event

import am9.olbcore.onebot.platform.onebot.Sender
import com.google.gson.annotations.Expose

class GroupMessage extends Event {
  post_type = "message"
  @Expose
  var message_type: String = "group"
  @Expose
  var sub_type: String = null
  @Expose
  var message_id: Int = 0
  @Expose
  var group_id: Long = 0
  @Expose
  var user_id: Long = 0
  @Expose
  var anonymous: AnyRef = null
  @Expose
  var message_format: String = null
  @Expose
  var message: AnyRef = null
  @Expose
  var raw_message: String = null
  @Expose
  var font: Int = 0
  @Expose
  var sender: Sender = null

}
