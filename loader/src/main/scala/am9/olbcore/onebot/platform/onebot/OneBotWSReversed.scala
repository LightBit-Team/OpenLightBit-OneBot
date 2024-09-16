package am9.olbcore.onebot.platform.onebot

import am9.olbcore.onebot.Main
import am9.olbcore.onebot.feature.parser.MessageParser
import org.java_websocket.WebSocket
import org.java_websocket.handshake.{ClientHandshake, Handshakedata}
import org.java_websocket.server.WebSocketServer

import java.net.InetSocketAddress
import java.util

class OneBotWSReversed(port: Int) extends WebSocketServer(new InetSocketAddress(port)), OneBot{
  override def sendFriend(uid: Long, message: String): Unit = {
    
  }

  override def sendGroupWithSegments(groupId: Long, segments: util.List[Segment]): Unit = {

  }

  override def sendGroup(groupId: Long, message: String): Unit = {
    
  }
  override def sendGroupWithCqCode(groupId: Long, message: String): Unit = {
    
  }
  override def sendGroupRecord(groupId: Long, fileName: String): Unit = {
    
  }

  override def onStart(): Unit = {}

  override def onClose(webSocket: WebSocket, i: Int, s: String, b: Boolean): Unit = {
    Main.logger.info("WebSocket连接中止")
  }
  override def onMessage(webSocket: WebSocket, s: String): Unit = {
    MessageParser.parse(s)
  }

  override def onOpen(webSocket: WebSocket, clientHandshake: ClientHandshake): Unit = {}
  override def onError(webSocket: WebSocket, e: Exception): Unit = {
    throw new RuntimeException(webSocket.toString, e)
  }
}
