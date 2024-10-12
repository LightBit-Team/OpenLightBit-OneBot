package am9.olbcore.onebot.platform.onebot

import am9.olbcore.onebot.Main
import am9.olbcore.onebot.feature.parser.MessageParser
import am9.olbcore.onebot.platform.onebot.action.{SendGroupMsg, SendPrivateMsg}
import com.neovisionaries.ws.client.{WebSocket, WebSocketAdapter, WebSocketFactory}

import java.util.concurrent.CompletableFuture
import java.net.URI
import java.util

class OneBotWS(serverUri: URI) extends OneBot {
  private val websocket: WebSocket = new WebSocketFactory().createSocket(serverUri).addListener(new LightbitAdapter()).connect()
  override def sendGroup(groupId: Long, message: String): Unit = {
    val segment = new Segment("text", new util.HashMap[String, String](){
      put("text", message)
    })
    val sendGroupMsg = new SendGroupMsg(groupId, segment, false)
    websocket.sendText(Main.json.toJson(sendGroupMsg))
  }

  override def sendGroupWithCqCode(groupId: Long, message: String): Unit = {
    val sendGroupMsg = new SendGroupMsg(groupId, message, false)
    websocket.sendText(Main.json.toJson(sendGroupMsg))
  }
  
  override def sendFriend(uid: Long, message: String): Unit = {
    val segment = new Segment("text", new util.HashMap[String, String](){
      put("text", message)
    })
    val sendPrivateMsg = new SendPrivateMsg(uid, segment, false)
    websocket.sendText(Main.json.toJson(sendPrivateMsg))
  }
  override def sendGroupRecord(groupId: Long, fileName: String): Unit = {
    val configMap = Main.config.getData
    val segment = new Segment("record", new java.util.HashMap[String, String]() {
      put("file", s"http://${configMap.get("media-server-host")}:${configMap.get("media-server-port")}/$fileName")
    })
    val sendGroupMsg = new SendGroupMsg(groupId, segment, false)
    websocket.sendText(Main.json.toJson(sendGroupMsg))
  }

  override def sendGroupWithSegments(groupId: Long, segments: util.List[Segment]): Unit = {
    val sendGroupMsg = new SendGroupMsg(groupId, segments, false)
    websocket.sendText(Main.json.toJson(sendGroupMsg))
  }
}

private class LightbitAdapter extends WebSocketAdapter {
  override def onTextMessage(websocket: WebSocket, text: String): Unit = {
    MessageParser.parse(text)
  }
}
