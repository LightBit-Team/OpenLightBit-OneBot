package am9.olbcore.onebot.config

import cn.hutool.setting.dialect.Props

import java.io.File
import java.nio.file.{Files, Paths}
import java.{lang, util}

class ZhuanJson {
  var data: util.HashMap[String, util.List[Long]] = new util.HashMap[String, util.List[Long]](){
    put("listen-group", new util.ArrayList[Long]())
    put("target-group", new util.ArrayList[Long]())
  }
  def getData: util.HashMap[String, util.List[Long]] = data
  def setData(data: util.HashMap[String, util.List[Long]]): Unit = this.data = data
  def read(dir: File): this.type = {
    var json: String = null
    if (dir.exists()) {
      try {
        data.clear()
        new Props(dir).forEach((k, v) => {
          k.toString match {
            case "listen-group" | "target-group" =>
              val numberList = new util.ArrayList[Long]()
              v.toString.split("and").foreach(i => {
                if (i.nonEmpty) numberList.add(lang.Long.parseLong(i))
              })
              data.put(k.toString, numberList)
          }
        })
      } catch {
        case e: NumberFormatException =>
          throw new RuntimeException(e)
        case e: MatchError =>
          throw new RuntimeException(e)
      }
      this
    } else {
      throw new NullPointerException("Config file not found!")
    }
  }
  def write(dir: File): Unit = {
    if (dir.exists()) Files.delete(Paths.get(dir.getPath))
    Files.createFile(Paths.get(dir.getPath))
    val props = new Props()
    data.forEach((k, v) => {
      props.put(k, String.join("and", v.stream().map(i => i.toString).toList))
    })
    props.store(dir.getPath)
  }
}
