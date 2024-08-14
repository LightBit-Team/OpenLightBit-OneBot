package am9.olbcore.onebot.onebot

import am9.olbcore.onebot.Main
import java.net.URI

object Connect {

  def getConnection: OneBot = {
    Main.config.getData.get("onebot-protocol") match
      case "ws" =>
        val oneBotWS = new OneBotWS(new URI(s"ws://${Main.config.getData.get("onebot-address")}:${Main.config.getData.get("onebot-port")}/"))
        oneBotWS.connect()
        oneBotWS
      case _ => throw new IllegalArgumentException("不支持的协议")
  }
}
