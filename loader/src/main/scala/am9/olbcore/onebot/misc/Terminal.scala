package am9.olbcore.onebot.misc

import am9.olbcore.onebot.Main
import am9.olbcore.onebot.Main.logger
import org.jetbrains.annotations.NotNull

object Terminal {
  def serverLauncherWarn(): Unit = {
    logger.warn("等等！")
    logger.warn("请不要使用任何 JAR启动器/Java启动器 运行本程序")
    logger.warn("您的启动器可能会对本程序（或其数据/配置文件）进行非法改动")
    logger.warn("由您的启动器造成的问题我们一概不负责！")
    logger.warn("如果您认为您的启动器不会对文件造成非法修改，请忽略")
    logger.warn("请参阅https://essentialsx.net/do-not-use-mohist.html")
  }
  def isRunningOnServerLauncher: Boolean = {
    val location = this.getClass.getProtectionDomain.getCodeSource.getLocation.toString
    location.contains("Server") || location.contains("version") || location.contains("launcher") ||
      location.contains("data")
  }
  def debug(@NotNull msg: AnyRef): Unit = {
    if (Main.config.getData.get("debug-enabled").toString.toBoolean) {
      Main.logger.debug(msg.toString)
    }
  }
}