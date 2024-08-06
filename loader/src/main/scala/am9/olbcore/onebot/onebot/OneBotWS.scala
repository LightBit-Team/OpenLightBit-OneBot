package am9.olbcore.onebot
package onebot

import am9.olbcore.onebot.feature.Parser
import cn.hutool.json.{JSONObject, JSONUtil}
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake

import java.net.URI

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
    val json = groupSendTemplate.clone()
    json.put("params", new JSONObject(true) {
      put("group_id", groupId)
      put("message", new JSONObject(true) {
        put("type", "text")
        put("data", new JSONObject(true) {
          put("text", message)
        })
      })
    }
    )
    send(json.toString)
  }

  override def sendGroupWithCqCode(groupId: Long, message: String): Unit = {
    val json = groupSendTemplate.clone()
    json.put("params", new JSONObject(true) {
      put("group_id", groupId)
      put("message", message)
    })
    Main.oneBotWS.send(json.toString)
  }
}
