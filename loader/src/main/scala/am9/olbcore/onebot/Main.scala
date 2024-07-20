package am9.olbcore.onebot

import am9.olbcore.onebot.feature.{BreadFactory, Sender}
import am9.olbcore.onebot.misc.Terminal
import cn.hutool.log.{Log, LogFactory}
import cn.hutool.json.JSON
import cn.hutool.json.JSONObject
import config.{AdminData, Bread, Config}
import onebot.{Connect, OneBotWS}
import org.jetbrains.annotations.Nullable
import org.slf4j.{Logger, LoggerFactory}

import java.io.File
import java.net.URI
import java.util
import java.util.Timer

object Main {
  var logger: Log = null
  var oneBotWS: OneBotWS = null
  var config: Config = new Config()
  var adminData: AdminData = new AdminData()
  var bread: Bread = new Bread()
  //private val timer = new Timer()
  def main(@Nullable args: Array[String]): Unit = {
    try {
      logger = LogFactory.get(this.getClass)
      //读取配置文件
      val configFile = new File("config.json")
      val adminConfigFile = new File("admin.json")
      val breadFile = new File("bread.json")
      if (configFile.exists()) {
        //logger = LogFactory.get(config.getData.get("bot-name").toString)
        config = config.read(configFile)
      } else {
        config.write(configFile)
        logger.error("请填写配置文件！")
        System.exit(0)
      }
      if (!adminConfigFile.exists()) {
        adminData.write(adminConfigFile)
      }
      adminData = adminData.read(adminConfigFile)
      if (!breadFile.exists()) {
        bread.write(breadFile)
      }
      bread = bread.read(breadFile)
      Connect.connect()
      val timer = new Timer()
      timer.schedule(BreadFactory.makeBread, 20000)
      timer.schedule(BreadFactory.getMaterial, 25000)
      logger.info("恭喜！启动成功，0Error，至少目前如此，也祝你以后如此")
      if (Terminal.isRunningOnServerLauncher) {
        Terminal.serverLauncherWarn()
      }
    } catch {
      case e: Throwable =>
        logger.warn("启动时遇到问题 ", e)
    }
  }
  def restart(groupId: Long): Unit = {
    shutdown()
  }
  def shutdown(): Unit = {
    oneBotWS.close()
  }
}