package am9.olbcore.onebot.platform.onebot

import am9.olbcore.onebot.Main
import am9.olbcore.onebot.feature.parser.MessageParser
import am9.olbcore.onebot.platform.onebot.action.{SendGroupMsg, SendPrivateMsg}
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake

import java.net.URI
import java.util

class OneBotWS(serverUri: URI) extends WebSocketClient(serverUri), OneBot{
  override def onOpen(handshakedata: ServerHandshake): Unit = {}
  override def onMessage(message: String): Unit = {
    MessageParser.parse(message)
  }
  override def onClose(code: Int, reason: String, remote: Boolean): Unit = {
    Main.logger.info("WebSocket连接中止")
  }
  override def onError(ex: Exception): Unit = {
    if (!isOpen) {
      Main.logger.warn("无法连接到WebSocket服务端，将尝试5秒后重新连接...")
      Thread.sleep(5000)
      connect()
    }
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
    val sendPrivateMsg = new SendPrivateMsg(uid, segment, false)
    send(Main.json.toJson(sendPrivateMsg))
  }
  override def sendGroupRecord(groupId: Long, fileName: String): Unit = {
    val configMap = Main.config.getData
    val segment = new Segment("record", new java.util.HashMap[String, String]() {
      put("file", s"http://${configMap.get("media-server-host")}:${configMap.get("media-server-port")}/$fileName")
    })
    val sendGroupMsg = new SendGroupMsg(groupId, segment, false)
    send(Main.json.toJson(sendGroupMsg))
  }

  override def sendGroupWithSegments(groupId: Long, segments: util.List[Segment]): Unit = {
    val sendGroupMsg = new SendGroupMsg(groupId, segments, false)
    send(Main.json.toJson(sendGroupMsg))
  }
}
