package am9.olbcore.onebot.platform.onebot

import am9.olbcore.onebot.Main
import am9.olbcore.onebot.feature.parser.MessageParser
import am9.olbcore.onebot.platform.onebot.action.{SendGroupMsg, SendPrivateMsg}
import java.net.http.HttpClient
import java.net.http.WebSocket
import java.util.concurrent.CompletionStage

import java.net.URI
import java.util

class OneBotWS(serverUri: URI) extends OneBot {
  private val mySb = new StringBuilder()
  private val websocket = HttpClient.newHttpClient().newWebSocketBuilder().buildAsync(serverUri, LightbitAdapter).join()
  override def sendGroup(groupId: Long, message: String): Unit = {
    val segment = new Segment("text", new util.HashMap[String, String](){
      put("text", message)
    })
    val sendGroupMsg = new SendGroupMsg(groupId, segment, false)
    websocket.sendText(Main.json.toJson(sendGroupMsg), true)
  }

  override def sendGroupWithCqCode(groupId: Long, message: String): Unit = {
    val sendGroupMsg = new SendGroupMsg(groupId, message, false)
    websocket.sendText(Main.json.toJson(sendGroupMsg), true)
  }
  
  override def sendFriend(uid: Long, message: String): Unit = {
    val segment = new Segment("text", new util.HashMap[String, String](){
      put("text", message)
    })
    val sendPrivateMsg = new SendPrivateMsg(uid, segment, false)
    websocket.sendText(Main.json.toJson(sendPrivateMsg), true)
  }
  override def sendGroupRecord(groupId: Long, fileName: String): Unit = {
    val configMap = Main.config.getData
    val segment = new Segment("record", new java.util.HashMap[String, String]() {
      put("file", s"http://${configMap.get("media-server-host")}:${configMap.get("media-server-port")}/$fileName")
    })
    val sendGroupMsg = new SendGroupMsg(groupId, segment, false)
    websocket.sendText(Main.json.toJson(sendGroupMsg), true)
  }

  override def sendGroupWithSegments(groupId: Long, segments: util.List[Segment]): Unit = {
    val sendGroupMsg = new SendGroupMsg(groupId, segments, false)
    websocket.sendText(Main.json.toJson(sendGroupMsg), true)
  }

  override def stop(): Unit = {
    websocket.sendClose(1000, "Bye")
  }
  private object LightbitAdapter extends WebSocket.Listener {
    override def onText(websocket: WebSocket, text: CharSequence, last: Boolean): CompletionStage[Unit] = {
      websocket.request(1)
      mySb.append(text)
      if (last) {
        MessageParser.parse(mySb.toString())
        mySb.clear()
      }
      null
    }
  }
}
