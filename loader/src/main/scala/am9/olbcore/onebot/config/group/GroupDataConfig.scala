package am9.olbcore.onebot.config.group

import am9.olbcore.onebot.Main
import am9.olbcore.onebot.platform.onebot.Sender
import cn.hutool.core.io.FileUtil
import com.google.gson.reflect.TypeToken

import java.io.File
import java.nio.charset.StandardCharsets
import java.util

class GroupDataConfig {
  private var data: util.Map[String, util.List[Sender]] = new util.HashMap[String, util.List[Sender]]()

  def getData: util.Map[String, util.List[Sender]] = data

  def setData(map: util.Map[String, util.List[Sender]]): Unit = data = map

  def read(dir: File): this.type = {
    if (dir.exists()) {
      val json = new String(FileUtil.readString(dir, StandardCharsets.UTF_8))
      this.setData(Main.json.fromJson[util.HashMap[String, util.List[Sender]]](json, new TypeToken[util.HashMap[String, util.List[Sender]]](){}))
      this
    } else {
      throw new NullPointerException("Config file not found!")
    }
  }

  def write(dir: File): Unit = {
    if (dir.exists()) dir.delete()
    dir.createNewFile()
    FileUtil.writeString(Main.json.toJson(data), dir, StandardCharsets.UTF_8)
  }
}


