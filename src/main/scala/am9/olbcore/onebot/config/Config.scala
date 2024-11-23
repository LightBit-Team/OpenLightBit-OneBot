package am9.olbcore.onebot
package config

import org.dromara.hutool.core.io.file.FileUtil
import org.dromara.hutool.setting.props.Props

import java.io.{File, IOException}
import java.nio.file.FileAlreadyExistsException
import java.util

class Config {
  private var data: util.Map[String, AnyRef] = new util.TreeMap[String, AnyRef]() {
    put("config-version", "3")
    put("bot-name", "OLB")
    put("logger-name", "OpenLightBit")
    put("debug-enabled", "true")
    put("owner", "10001")
    put("data-format", "json")
    put("command-prefix", "!")
    put("onebot-address", "localhost")
    put("onebot-port", "3000")
    put("onebot-secret", "")
    put("onebot-protocol", "ws")
    //仅http（用于接收事件）/反向ws
    put("onebot-post-port", "1145")
    //仅http（用于接收事件）/反向ws
    put("onebot-path", "/onebot")
    put("enable-media-server", "true")
    put("media-server-host", "localhost")
    put("media-server-port", "19198")
  }

  def getData: util.Map[String, AnyRef] = data
  def setData(map: util.Map[String, AnyRef]): Unit = this.data = map
  def read(dir: File): this.type = {
    if (dir.exists()) {
      data.clear()
      new Props(dir).forEach((k, v) => {
        data.put(k.toString, v)
      })
      this
    } else {
      throw new NullPointerException("Config file not found!")
    }
  }
  def write(dir: File): Unit = {
    try {
      if (!dir.exists()) FileUtil.touch(dir)
      val props = new Props(dir)
      data.forEach((k, v) => {
        props.put(k, v)
      })
      props.store(dir.getPath)
    } catch {
      case e: FileAlreadyExistsException => Main.logger.info("文件已存在，将不覆写")
      case e: IOException => Main.logger.error("写入文件失败", e)
    }
  }
}
