package am9.olbcore.onebot.config

import am9.olbcore.onebot.Main
import cn.hutool.core.io.FileUtil
import cn.hutool.setting.dialect.Props

import java.io.{File, IOException}
import java.util

class GuildConfig {
  private var data: Props = new Props()
  def read(dir: File): this.type = {
    data = new Props(dir)
    this
  }
  def write(dir: File): Unit = {
    try {
      if (!dir.exists()) FileUtil.touch(dir)
      if (data.isEmpty) {
        data.put("bot-appid", "0")
        data.put("bot-token", "null")
      }
      data.store(dir.getPath)
    } catch {
      case e: IOException => Main.logger.error("写入文件失败", e)
    }
  }
  def getData: util.Map[String, String] = {
    val ret = new util.HashMap[String, String]()
    data.forEach((k, v) => {
      ret.put(k.toString, v.toString)
    })
    ret
  }
  def setData(map: util.Map[String, String]): Unit = {
    map.forEach((k, v) => {
      data.put(k, v)
    })
  }
}


