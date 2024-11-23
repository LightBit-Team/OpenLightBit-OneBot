package am9.olbcore.onebot

import am9.olbcore.onebot.Main.logger
import cn.hutool.core.io.FileUtil
import cn.hutool.core.io.resource.ClassPathResource
import cn.hutool.setting.dialect.Props
import org.jetbrains.annotations.NotNull

import java.nio.charset.StandardCharsets
import java.util
import java.util.Objects

object Terminal {
  def serverLauncherWarn(): Unit = {
    logger.warn("等等！")
    logger.warn("请不要使用任何 JAR启动器/Java启动器 运行本程序")
    logger.warn("您的启动器可能会对本程序（或其数据/配置文件）进行非法改动")
    logger.warn("由您的启动器造成的问题我们一概不负责！")
    logger.warn("如果您认为您的启动器不会对文件造成非法修改，请忽略")
  }

  def isRunningOnServerLauncher: Boolean = {
    val location = this.getClass.getProtectionDomain.getCodeSource.getLocation.toString
    location.contains("Server") || location.contains("version") || location.contains("launcher") ||
      location.contains("data")
  }

  def debug(@NotNull msg: AnyRef): Unit = {
    if (Main.config.getData.get("debug-enabled").toString.toBoolean) {
      Main.loggerDebug(msg.toString)
    }
  }

  def readBuildInfo: Props = {
    val resource = new ClassPathResource("META-INF/b-info")
    val props = new Props()
    props.load(resource.getStream)
    props
  }

  def readClasspathFile(path: String): String = {
    val resource = new ClassPathResource(path)
    resource.readStr(StandardCharsets.UTF_8)
  }

  def saveCan(e: Throwable): Unit = {
    val time = System.currentTimeMillis
    if (!FileUtil.exist("cans")) {
      FileUtil.mkdir("cans")
    }
    if (!FileUtil.exist(s"cans/$time.can.json")) {
      val json = Main.json.toJson(new java.util.HashMap[String, AnyRef](){
        put("time", time.toString)
        put("error", e.getClass.toString)
        put("message", Objects.requireNonNullElse(e.getMessage, "null"))
        put("cause", try {
          new java.util.HashMap[String, AnyRef](){
            put("error", e.getCause.getClass.toString)
            put("message", e.getCause.getMessage)
            val stackTrace = new util.ArrayList[String]()
            for (i <- e.getCause.getStackTrace) {
              stackTrace.add(i.toString)
            }
            put("stackTrace", stackTrace)
          }
        } catch {
          case _: Exception => null
        })
        val stackTrace = new util.ArrayList[String]()
        for (i <- e.getStackTrace) {
          stackTrace.add(i.toString)
        }
        put("stackTrace", stackTrace)
      })
      FileUtil.newFile("./cans/$time.can.json")
      FileUtil.writeString(json, s"./cans/$time.can.json", StandardCharsets.UTF_8)
    }
  }
}
