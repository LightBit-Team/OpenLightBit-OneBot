package am9.olbcore.onebot.onebot.event

import am9.olbcore.onebot.onebot.Sender
import com.google.gson.annotations.Expose

class FriendMessage extends Event {
  post_type = "message"
  @Expose
  var message_type: String = "private"
  @Expose
  var sub_type: String = null
  @Expose
  var message_id: Int = 0
  @Expose
  var user_id: Long = 0
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
