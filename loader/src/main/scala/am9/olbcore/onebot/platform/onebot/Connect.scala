package am9.olbcore.onebot.platform.onebot

import am9.olbcore.onebot.Main
import am9.olbcore.onebot.config.GuildConfig
import am9.olbcore.onebot.platform.other.QQGuild
import cn.hutool.core.thread.ThreadUtil
import me.zhenxin.qqbot.core.BotCore
import me.zhenxin.qqbot.entity.AccessInfo

import java.io.File
import java.net.URI

object Connect {

  def getConnection: OneBot = {
    Main.config.getData.get("onebot-protocol") match
      case "ws" =>
        val oneBotWS = new OneBotWS(new URI(s"ws://${Main.config.getData.get("onebot-address")}:${Main.config.getData.get("onebot-port")}/"))
        oneBotWS.connect()
        oneBotWS
      case "http" =>
        val oneBotHttp = new OneBotHttp(s"http://${Main.config.getData.get("onebot-address")}:${Main.config.getData.get("onebot-port")}/",
          Integer.parseInt(Main.config.getData.get("onebot-post-port").toString))
        oneBotHttp
      case "official" =>
        Main.logger.warn("仍在测试阶段！")
        val qqGuild: QQGuild = new QQGuild()
        val guildConfigFile = new File("guild.json")
        var guildConfig: GuildConfig = new GuildConfig()
        if (!guildConfigFile.exists()) {
          guildConfig.write(guildConfigFile)
          Main.logger.warn("请填写guild.json！")
          System.exit(0)
        }
        guildConfig = guildConfig.read(guildConfigFile)
        ThreadUtil.execute(new Runnable() {
          override def run(): Unit = {
            val accessInfo = new AccessInfo()
            accessInfo.setBotAppId(Integer.parseInt(guildConfig.getData.get("bot-appid")))
            accessInfo.setBotToken(guildConfig.getData.get("bot-token"))
            accessInfo.useSandBoxMode()
            val botCore = new BotCore(accessInfo)
            val apiManager = botCore.getApiManager
            botCore.registerAtMessageEvent()
            botCore.setEventHandler(qqGuild)
            botCore.start()
          }
        })
        qqGuild
      case _ => throw new IllegalArgumentException("不支持的协议")
  }
}
