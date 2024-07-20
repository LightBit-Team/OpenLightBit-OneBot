package am9.olbcore.onebot
package config

import cn.hutool.json.{JSONObject, JSONUtil}

import java.io.{File, IOException}
import java.nio.file.{FileAlreadyExistsException, Files, Paths}
import java.util

//import am9.olbcore.onebot.config.JsonStorage

@SuppressWarnings(Array("deprecation"))
class Config {
  private var data: util.Map[String, AnyRef] = new util.TreeMap[String, AnyRef]() {
    put("config-version", "1")
    put("bot-name", "OLB")
    put("logger-name", "OLB")
    put("debug-enabled", "true")
    put("owner", "10001")
    put("token", "")
    put("command-prefix", "!")
    put("command-aliases", new JSONObject(true) {
      put("help", "h")
      put("ping", "p")
      put("reload", "r")
      put("shutdown", "s")
    })
    put("onebot-address", "localhost")
    put("onebot-port", "8080")
    put("onebot-secret", "")
    put("onebot-protocol", "ws")
  }

  def getData: util.Map[String, AnyRef] = data
  def setData(map: util.Map[String, AnyRef]): Unit = this.data = map
  def getJson: String = JSONUtil.toJsonPrettyStr(data)
  def read(dir: File): this.type = {
    var json: String = null
    if (dir.exists()) {
      json = new String(Files.readAllBytes(Paths.get(dir.getPath)))
      this.setData(JSONUtil.parseObj(json).getRaw)
      this
    } else {
      throw new NullPointerException("Config file not found!")
    }
  }
  def write(dir: File): Unit = {
    try {
      Files.createFile(Paths.get(dir.getPath))
      Files.write(Paths.get(dir.getPath), getJson.getBytes)
    } catch {
      case e: FileAlreadyExistsException => Main.logger.info("文件已存在，将不覆写")
      case e: IOException => Main.logger.error("写入文件失败", e)
    }
  }
}
