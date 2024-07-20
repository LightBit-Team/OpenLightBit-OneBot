package am9.olbcore.onebot.onebot

import am9.olbcore.onebot.Main
import java.net.URI

object Connect {

  def connect(): Unit = {
    Main.config.getData.get("onebot-protocol") match
      case "http" =>
        ???
      case "ws" =>
        Main.oneBotWS = new OneBotWS(new URI(s"ws://${Main.config.getData.get("onebot-address")}:${Main.config.getData.get("onebot-port")}/"))
        Main.oneBotWS.connect()
      case "ws-reserved" =>
        ???
  }
}
