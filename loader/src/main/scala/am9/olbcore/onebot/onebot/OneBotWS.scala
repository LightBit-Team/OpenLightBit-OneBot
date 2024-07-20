package am9.olbcore.onebot
package onebot

import feature.{BreadFactory, Parser}
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake

import java.net.URI

class OneBotWS(serverUri: URI) extends WebSocketClient(serverUri){
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
}
