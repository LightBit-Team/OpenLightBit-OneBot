package am9.olbcore.onebot

import am9.olbcore.onebot.config.{AdminData, Bread, Config}
import am9.olbcore.onebot.feature.BreadFactory
import am9.olbcore.onebot.feature.woodenfish.Woodenfishes
import am9.olbcore.onebot.media.MediaServer
import am9.olbcore.onebot.onebot.{Connect, OneBot}
import cn.hutool.log.{Log, LogFactory}
import com.google.gson.{Gson, GsonBuilder}
import org.jetbrains.annotations.{NonNls, Nullable}

import java.io.File
import java.util
import java.util.Timer

object Main {
  var logger: Log = LogFactory.get(this.getClass)
  private val jb: GsonBuilder = new GsonBuilder()
  var json: Gson = jb.setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create()
  var mediaServer: MediaServer = null
  var oneBot: OneBot = null
  var config: Config = new Config()
  var adminData: AdminData = new AdminData()
  var bread: Bread = new Bread()
  @NonNls
  val version = "0.3.0 (QingZhu)"
  val changelog: String = "null"
  @NonNls
  val splashes: util.List[String] = util.List.of(
    "也试试KuoHuBit罢！Also try KuoHuBit!",
    "也试试2kbit罢！Also try 2kbit!",
    "PHP是世界上最好的编程语言（雾）",
    "Minecraft很好玩，但也可以试试Terraria！",
    "So Nvidia, f**k you!",
    "Bug是杀不完的，你杀死了一个Bug，就会有千千万万个Bug站起来！",
    "跟张浩扬博士一起来学Jvav罢！",
    "哼哼哼，啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊",
    "你知道吗？其实你什么都不知道！",
    "Tips:这是一条烫...烫..烫知识（）",
    "有时候ctmd不一定是骂人 可能是传统美德",
    "这条标语虽然没有用，但是是有用的，因为他被加上了标语",
    "使用Scala编写！"
  )
  @NonNls
  val copyright: String =
    """OpenLightBit-OneBot
      |Copyright（C）2024 Emerald-AM9
      |This program is free software: you can redistribute it and/or modify
      |it under the terms of the GNU Affero General Public License as
      |published by the Free Software Foundation, either version 3 of the
      |License, or (at your option) any later version.
      |
      |This program is distributed in the hope that it will be useful,
      |but WITHOUT ANY WARRANTY; without even the implied warranty of
      |MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
      |GNU Affero General Public License for more details.
      |
      |You should have received a copy of the GNU Affero General Public License
      |along with this program.  If not, see <https://www.gnu.org/licenses/>.
      |
      |3496929815@qq.com
      |""".stripMargin
  def main(@Nullable args: Array[String]): Unit = {
    try {
      //读取配置文件
      val configFile = new File("config.json")
      val adminConfigFile = new File("admin.json")
      val breadFile = new File("bread.json")
      if (configFile.exists()) {
        logger = LogFactory.get(config.getData.get("logger-name").toString)
        config = config.read(configFile)
        if (Integer.parseInt(config.getData.get("config-version").toString) < 2) {
          logger.warn("配置文件版本过低请重新生成配置文件！")
          System.exit(0)
        }
        if (Integer.parseInt(config.getData.get("config-version").toString) > 2) {
          logger.error("配置文件版本过高，请重新生成配置文件！")
          System.exit(0)
        }
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
      oneBot = Connect.getConnection
      if (new File("woodenfish.json").exists()) {
        Woodenfishes.read(new File("woodenfish.json"))
      }
      val timer = new Timer()
      timer.schedule(BreadFactory.makeBread, 20000)
      timer.schedule(BreadFactory.getMaterial, 25000)
      timer.schedule(Woodenfishes.autoSave, 120000)
      mediaServer = new MediaServer(Integer.parseInt(config.getData.get("media-server-port").toString))
      mediaServer.start()
      if (Terminal.isRunningOnServerLauncher) {
        Terminal.serverLauncherWarn()
      }
      logger.info("恭喜！启动成功，0Error，至少目前如此，也祝你以后如此")
    } catch {
      case e: Throwable =>
        logger.warn("启动时遇到问题 ", e)
    }
  }
}