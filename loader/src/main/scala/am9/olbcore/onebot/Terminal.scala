package am9.olbcore.onebot

import am9.olbcore.onebot.Main.logger
import kotlin.jvm.Throws
import org.jetbrains.annotations.NotNull

import java.util

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
      Main.logger.debug(msg.toString)
    }
  }
  @Throws(exceptionClasses = Array[Class[? <: Throwable]](classOf[IllegalAccessException]))
  def bean2Map(obj: AnyRef): util.Map[String, AnyRef] = {
    val map = new util.HashMap[String, AnyRef]()
    val cls = obj.getClass
    cls.getDeclaredFields.foreach(i => {
      i.setAccessible(true)
      val value = i.get(obj)
      if (value != null) {
        map.put(i.getName, value)
      }
    })
    map
  }
  def iAmNotACompiler(input: Byte): Byte = {
    var a: Byte = input
    var b: Byte = 5
    var c: Byte = 0
    while (a > 0) {
      if (a >= (b ^ 2)) {
        a = (a - (b ^ 2)).toByte
        c = (c + 1).toByte
      } else {
        b = (b - 2).toByte
      }
    }
    c
  }
}