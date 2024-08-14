package am9.olbcore.onebot
package onebot

import am9.olbcore.onebot.feature.Parser
import am9.olbcore.onebot.onebot.action.SendGroupMsg
import cn.hutool.json.{JSONObject, JSONUtil}
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake

import java.net.URI
import java.util

class OneBotWS(serverUri: URI) extends WebSocketClient(serverUri), OneBot{
  val groupSendTemplate: JSONObject = JSONUtil.createObj()
    .put("action", "send_group_msg_rate_limited")
    .put("params", null)
    .put("auto_escape", false)
  override def onOpen(handshakedata: ServerHandshake): Unit = {
    Main.logger.info("Connected OneBot-11 WS")
  }
  override def onMessage(message: String): Unit = {
    Parser.parse(message)
  }
  override def onClose(code: Int, reason: String, remote: Boolean): Unit = {
    Main.logger.info("Disconnected OneBot-11 WS")
  }
  override def onError(ex: Exception): Unit = {
    Main.logger.error("Error in OneBot-11 WS", ex)
  }
  override def sendGroup(groupId: Long, message: String): Unit = {
    val segment = new Segment("text", new util.HashMap[String, String](){
      put("text", message)
    })
    val sendGroupMsg = new SendGroupMsg(groupId, segment, false)
    send(Main.json.toJson(sendGroupMsg))
  }

  override def sendGroupWithCqCode(groupId: Long, message: String): Unit = {
    val sendGroupMsg = new SendGroupMsg(groupId, message, false)
    send(Main.json.toJson(sendGroupMsg))
  }
  
  override def sendFriend(uid: Long, message: String): Unit = {
    val segment = new Segment("text", new util.HashMap[String, String](){
      put("text", message)
    })
  }
}
