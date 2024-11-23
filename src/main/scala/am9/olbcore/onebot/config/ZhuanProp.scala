package am9.olbcore.onebot.config

import am9.olbcore.onebot.Main
import cn.hutool.core.io.FileUtil
import com.google.gson.reflect.TypeToken

import java.io.File
import java.nio.charset.StandardCharsets
import java.{lang, util}

class ZhuanProp {
  private var data: util.HashMap[String, util.List[Long]] = new util.HashMap[String, util.List[Long]](){
    put("listen-group", new util.ArrayList[Long]())
    put("target-group", new util.ArrayList[Long]())
  }
  def getData: util.HashMap[String, util.List[Long]] = data
  def setData(data: util.HashMap[String, util.List[Long]]): Unit = this.data = data
  def read(dir: File): this.type = {
    if (dir.exists()) {
      data = Main.json.fromJson[util.HashMap[String, util.List[Long]]](
        FileUtil.readString(dir, StandardCharsets.UTF_8),
        new TypeToken[util.HashMap[String, util.List[Long]]](){}
      )
      this
    } else {
      throw new NullPointerException("Config file not found!")
    }
  }
  def write(dir: File): Unit = {
    if (dir.exists()) FileUtil.del(dir)
    FileUtil.touch(dir)
    val jsonString = Main.json.toJson(data)
    Main.json.fromJson(jsonString, new TypeToken[util.HashMap[String, util.List[String]]](){}).values().forEach(i => {
      if (i.contains("E")) {
        throw new RuntimeException("群号包含双精度小数！")
      }
    })
    FileUtil.writeString(jsonString, dir, StandardCharsets.UTF_8)
  }
}
