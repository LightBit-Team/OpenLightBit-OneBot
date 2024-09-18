package am9.olbcore.onebot

import am9.olbcore.onebot.config.group.GroupDataConfig
import am9.olbcore.onebot.config.{AdminData, Bread, Config, ZhuanProp}
import am9.olbcore.onebot.feature.BreadFactory
import am9.olbcore.onebot.feature.cave.Cave
import am9.olbcore.onebot.feature.woodenfish.Woodenfishes
import am9.olbcore.onebot.media.MediaServer
import am9.olbcore.onebot.platform.onebot.{Connect, OneBot}
import cn.hutool.core.io.FileUtil
import cn.hutool.core.thread.ThreadUtil
import com.google.gson.reflect.TypeToken
import com.google.gson.{Gson, GsonBuilder}
import org.jetbrains.annotations.{NonNls, NotNull, Nullable}
import org.slf4j.LoggerFactory

import java.io.File
import java.nio.charset.StandardCharsets
import java.util
import java.util.{Objects, Timer}

object Main {
  var logger: org.slf4j.Logger = null
  private val jb: GsonBuilder = new GsonBuilder()
  var json: Gson = jb.setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create()
  var mediaServer: MediaServer = null
  var oneBot: OneBot = null
  var config: Config = new Config()
  var adminData: AdminData = new AdminData()
  var bread: Bread = new Bread()
  var zhuanProp: ZhuanProp = new ZhuanProp()
  var groupDataConfig: GroupDataConfig = new GroupDataConfig()
  val startTime: Long = System.currentTimeMillis
  @NonNls
  val version = "0.3.0 (QingZhu)"
  val changelog: String = "null"
  @NonNls
  val splashes: util.List[String] = util.List.of(
    "也试试KuoHuBit罢！Also try KuoHuBit!",
    "也试试2kbit罢！Also try 2kbit!",
    "使用Scala编写！",
    "誓死捍卫微软苏维埃！",
    "要把反革命分子的恶臭思想，扫进历史的垃圾堆！",
    "PHP是世界上最好的编程语言（雾）",
    "社会主义好，社会主义好~",
    "Minecraft很好玩，但也可以试试Terraria！",
    "So Nvidia, f**k you!",
    "战无不胜的马克思列宁主义万岁！",
    "Bug是杀不完的，你杀死了一个Bug，就会有千千万万个Bug站起来！",
    "跟张浩扬博士一起来学Jvav罢！",
    "哼哼哼，啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊",
    "你知道吗？其实你什么都不知道！",
    "Tips:这是一条烫...烫..烫知识（）",
    "你知道成功的秘诀吗？我告诉你成功的秘诀就是：我操你妈的大臭逼",
    "有时候ctmd不一定是骂人 可能是传统美德",
    "python不一定是编程语言 也可能是屁眼通红",
    "这条标语虽然没有用，但是是有用的，因为他被加上了标语",
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
  def start(@NotNull lg: org.slf4j.Logger): Unit = {
    try {
      val configFile = new File("config.properties")
      val adminConfigFile = new File("admin.json")
      val breadFile = new File("bread.json")
      val zhuanFile = new File("zhuan.json")
      val groupDataFile = new File("group_data.json")
      logger = lg
      if (configFile.exists()) {
        config = config.read(configFile)
        if (Integer.parseInt(config.getData.get("config-version").toString) < 2) {
          logger.warn("配置文件版本过低，请重新生成配置文件！")
          System.exit(0)
        }
        if (Integer.parseInt(config.getData.get("config-version").toString) > 2) {
          logger.error("配置文件版本过高，请重新生成配置文件！")
          System.exit(0)
        }
      } else {
        if (FileUtil.exist("config.json")) {
          val map = json.fromJson[util.HashMap[String, AnyRef]](
            FileUtil.readString(FileUtil.file("config.json"), StandardCharsets.UTF_8),
            new TypeToken[util.HashMap[String, AnyRef]](){}.getType)
          map.put("config-version", "2")
          map.put("onebot-post-port", "1145")
          map.put("onebot-path", "/onebot")
          map.put("enable-media-server", "true")
          map.put("media-server-host", "localhost")
          map.put("media-server-port", "19198")
          config.setData(map)
          config.write(configFile)
          FileUtil.del("config.json")
          logger.info("已升级配置文件！")
        } else {
          config.write(configFile)
          logger.error("请填写配置文件！")
          System.exit(0)
        }
      }
      if (!adminConfigFile.exists()) {
        adminData.write(adminConfigFile)
      }
      adminData = adminData.read(adminConfigFile)
      if (!breadFile.exists()) {
        bread.write(breadFile)
      }
      bread = bread.read(breadFile)
      if (!zhuanFile.exists()) {
        zhuanProp.write(zhuanFile)
      }
      zhuanProp = zhuanProp.read(zhuanFile)
      if (!groupDataFile.exists()) {
        groupDataConfig.write(groupDataFile)
      }
      groupDataConfig = groupDataConfig.read(groupDataFile)
      oneBot = Connect.getConnection
      if (new File("woodenfish.json").exists()) {
        Woodenfishes.read(new File("woodenfish.json"))
      }
      if (new File("cave.json").exists()) {
        Cave.read(new File("cave.json"))
      }
      ThreadUtil.execute(() => {
        val timer = new Timer()
        timer.schedule(BreadFactory.makeBread, 20000)
        timer.schedule(BreadFactory.getMaterial, 25000)
        timer.schedule(Woodenfishes.autoSave, 120000)
      })
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
  def main(args: Array[String]): Unit = {
    start(LoggerFactory.getLogger(this.getClass))
  }
}