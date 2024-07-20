package am9.olbcore.onebot
package config

import cn.hutool.json.{JSONObject, JSONUtil}

import java.io.{File, FileNotFoundException, IOException}
import java.nio.file.{FileAlreadyExistsException, Files, NoSuchFileException, Paths}
import java.util

@SuppressWarnings(Array("deprecation"))
class AdminData {
  private var data: util.Map[String, AnyRef] = new util.TreeMap[String, AnyRef]() {
    put("admin", util.ArrayList[Long]())
    put("black_list", util.ArrayList[Long]())
    put("white_list", util.ArrayList[Long]())
    put("ignore_list", util.ArrayList[Long]())
    put("disabled_group", util.ArrayList[Long]())
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
      Files.delete(Paths.get(dir.getPath))
      Files.createFile(Paths.get(dir.getPath))
      Files.write(Paths.get(dir.getPath), getJson.getBytes)
    } catch {
      case e: NoSuchFileException =>
        Main.logger.error("文件不存在", e)
        Files.createFile(Paths.get(dir.getPath))
        Files.write(Paths.get(dir.getPath), getJson.getBytes)
      case e: IOException => Main.logger.error("写入文件失败", e)
    }
  }
}
