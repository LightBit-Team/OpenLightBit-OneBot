package am9.olbcore.onebot.onebot.action.params

import am9.olbcore.onebot.onebot.Segment
import com.google.gson.annotations.Expose

class SendGroupMsgParams(
                          gid: Long,
                          msg: AnyRef,
                          autoEscape: Boolean
                        ) extends Params {
  @Expose
  val group_id: Long = gid
  @Expose
  val message: AnyRef = msg
  @Expose
  val auto_escape: Boolean = autoEscape
  if (!message.isInstanceOf[String] && !message.isInstanceOf[Segment] && !message.isInstanceOf[java.util.List[?]]) {
    throw new IllegalArgumentException("message must be String, Segment or List<Segment>")
  }
  if (message.isInstanceOf[java.util.List[?]]) {
    message.asInstanceOf[java.util.List[?]].forEach(segment => {
      if (!segment.isInstanceOf[Segment]) {
        throw new IllegalArgumentException("message must be List<Segment>")
      }
    })
  }
}