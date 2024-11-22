package am9.olbcore.onebot.newapi.modules

import am9.olbcore.onebot.Main
import am9.olbcore.onebot.newapi.{AbstractModule, ApiEvent, ApiGroupMessageEvent, EventProcessor}
import cn.hutool.core.io.FileUtil
import com.google.gson.reflect.TypeToken

import java.nio.charset.StandardCharsets
import scala.collection.mutable
import scala.jdk.CollectionConverters.MapHasAsScala
import scala.jdk.CollectionConverters.MutableMapHasAsJava

class XiuXian extends AbstractModule(
  "xiuxian", "null", "0.4.0", java.util.List.of("Emerald-AM9")
){
  private var idMap: Option[mutable.Map[String, UserData]] = None
  override def onLoad(): Unit = {
  }
  override def onEnable(): Unit = {
    this.registerEvent(XiuXianEventProcessor)
  }
  private def readData(): Unit = {
    val dataFile = FileUtil.file("xiuxian.json")
    if (dataFile.exists()) {
      idMap = Some(Main.json.fromJson(FileUtil.getReader(dataFile, StandardCharsets.UTF_8),
        new TypeToken[java.util.Map[String, UserData]](){}).asScala)
    }
  }
  private def saveData(): Unit = {
    idMap match {
      case Some(map) => FileUtil.writeString(Main.json.toJson(map.asJava), "xiuxian.json", StandardCharsets.UTF_8)
      case None => //do nothing
    }
  }
  private object XiuXianEventProcessor extends EventProcessor {
    override def onEvent(event: ApiEvent): Unit = {
      event match {
        case groupMessageEvent: ApiGroupMessageEvent =>
          if (groupMessageEvent.getMessage.startsWith(
            Main.config.getData.get("command-prefix").toString + "xiuxian")) {
            val argArray = groupMessageEvent.getMessage.split(" ")
            if (argArray.length >= 2) {
              argArray(1) match {
                case "register" =>
                  idMap.get.put(groupMessageEvent.getSender.user_id.toString, new UserData)
                  saveData()
                  groupMessageEvent.reply("注册成功！")
                case "xiulian" => //pass
                case _ =>
              }
            }
          }
        case _ =>
      }
    }
  }
  private class UserData {
    var name: String = ""
    var level: Short = 0
    var exp: Int = 0
  }
}
