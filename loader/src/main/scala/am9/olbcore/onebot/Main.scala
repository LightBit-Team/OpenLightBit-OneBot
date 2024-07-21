package am9.olbcore.onebot

import am9.olbcore.onebot.feature.BreadFactory
import am9.olbcore.onebot.misc.{Terminal, YuShengJun}
import cn.hutool.log.{Log, LogFactory}
import config.{AdminData, Bread, Config}
import onebot.{Connect, OneBotWS}
import org.jetbrains.annotations.Nullable

import java.io.File
import java.util
import java.util.Timer

object Main {
  var logger: Log = LogFactory.get(YuShengJun().getClass)
  var oneBotWS: OneBotWS = null
  var config: Config = new Config()
  var adminData: AdminData = new AdminData()
  var bread: Bread = new Bread()
  val version = "0.1.0-rc.1"
  val changelog: String = "第一个release"
  val splashes: util.List[String] = util.List.of(
    "也试试KuoHuBit罢！Also try KuoHuBit!",
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
  val copyright =
    """OpenLightBit-OneBot
      |版权所有（C）2024 Emerald-AM9
      |本程序为自由软件，在自由软件联盟发布的GNU通用公共许可协议的约束下，你可以对其进行再发布及修改。协议版本为第三版或（随你）更新的版本。
      |我们希望发布的这款程序有用，但不保证，甚至不保证它有经济价值和适合特定用途。详情参见GNU通用公共许可协议。
      |你理当已收到一份GNU通用公共许可协议的副本，如果没有，请查阅<http://www.gnu.org/licenses/>
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