package am9.olbcore.onebot.platform.onebot

import am9.olbcore.onebot.Main
import am9.olbcore.onebot.config.GuildConfig
import cn.hutool.core.thread.ThreadUtil

import java.io.File
import java.net.URI

object Connect {

  def getConnection: OneBot = {
    Main.config.getData.get("onebot-protocol") match
      case "ws" =>
        val oneBotWS = new OneBotWS(new URI(s"ws://${Main.config.getData.get("onebot-address")}:${Main.config.getData.get("onebot-port")}/"))
        oneBotWS
      case "http" =>
        val oneBotHttp = new OneBotHttp(s"http://${Main.config.getData.get("onebot-address")}:${Main.config.getData.get("onebot-port")}/",
          Integer.parseInt(Main.config.getData.get("onebot-post-port").toString))
        oneBotHttp
      case _ => throw new IllegalArgumentException("不支持的协议")
  }
}
