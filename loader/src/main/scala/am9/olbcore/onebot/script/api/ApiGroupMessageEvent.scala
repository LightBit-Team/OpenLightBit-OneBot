package am9.olbcore.onebot.script.api

import am9.olbcore.onebot.Main
import am9.olbcore.onebot.platform.onebot.{Segment, Sender}
import am9.olbcore.onebot.platform.onebot.event.GroupMessage
import com.google.gson.internal.LinkedTreeMap

import java.util

class ApiGroupMessageEvent(oneBotEvent: GroupMessage) extends ApiEvent {
  def reply(message: String): Unit = {
    Main.oneBot.sendGroupWithSegments(oneBotEvent.group_id, util.List.of[Segment](
      new Segment("at", new util.HashMap[String, String](){
        put("qq", oneBotEvent.user_id.toString)
      }),
      new Segment("text", new util.HashMap[String, String](){
        put("text", " " + message)
      })
    ))
  }
  def getSender: Sender = oneBotEvent.sender
  def getMessage: String = {
    if (oneBotEvent.message_format == "array") {
      oneBotEvent.message.asInstanceOf[java.util.List[LinkedTreeMap[String, AnyRef]]].get(0).get("data").asInstanceOf[java.util.Map[String, String]].get("text")
    } else {
      oneBotEvent.message.toString
    }
  }
  def getGroupId: Long = oneBotEvent.group_id
  def getMessageId: Long = oneBotEvent.message_id
}
