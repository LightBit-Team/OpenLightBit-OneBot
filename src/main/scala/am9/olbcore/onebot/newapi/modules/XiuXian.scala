package am9.olbcore.onebot.newapi.modules

import am9.olbcore.onebot.Main
import am9.olbcore.onebot.feature.XiuXianData
import am9.olbcore.onebot.newapi.{AbstractModule, ApiEvent, ApiGroupMessageEvent, EventProcessor}
import org.dromara.hutool.core.io.file.FileUtil
import org.dromara.hutool.core.util.RandomUtil
import com.google.gson.reflect.TypeToken

import java.nio.charset.StandardCharsets
import scala.collection.mutable
import scala.jdk.CollectionConverters.MapHasAsScala
import scala.jdk.CollectionConverters.MutableMapHasAsJava

class XiuXian extends AbstractModule(
  "xiuxian", "null", "0.4.0", java.util.List.of("Emerald-AM9")
){
  private var idMap: Option[mutable.Map[String, XiuXianData]] = None
  override def onLoad(): Unit = {
    readData()
  }
  override def onEnable(): Unit = {
    this.registerEvent(XiuXianEventProcessor)
  }
  private def readData(): Unit = {
    val dataFile = FileUtil.file("xiuxian.json")
    if (dataFile.exists()) {
      idMap = Some(Main.json.fromJson(FileUtil.getReader(dataFile, StandardCharsets.UTF_8),
        new TypeToken[java.util.Map[String, XiuXianData]](){}).asScala)
    } else {
      idMap = Some(mutable.Map())
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
          if (groupMessageEvent.getRawMessage.startsWith("!xiuxian")) {
            val argArray = groupMessageEvent.getRawMessage.split(" ")
            if (argArray.length >= 2) {
              argArray(1) match {
                case "register" =>
                  idMap.get.put(groupMessageEvent.getSender.user_id.toString, new XiuXianData)
                  saveData()
                  groupMessageEvent.reply("注册成功！")
                case "xiulian" =>
                  val data = idMap.get(groupMessageEvent.getSender.user_id.toString)
                  val newExp = if (data.level < 10)
                    data.exp + RandomUtil.randomInt(1, 5)
                    else data.exp + data.level / 10 * RandomUtil.randomInt(1, 10 - data.level.toString.length)
                  data.exp = newExp
                  saveData()
                  groupMessageEvent.reply(s"获得${newExp - data.exp}经验！")
                case "info" =>
                  val data = idMap.get(groupMessageEvent.getSender.user_id.toString)
                  groupMessageEvent.reply(
                    s"""用户名：${groupMessageEvent.getSender.nickname}
                       |等级：${data.level}
                       |经验：${data.exp}""".stripMargin)
                case "upgrade" =>
                  val data = idMap.get(groupMessageEvent.getSender.user_id.toString)
                  val newLevel = data.level + 1
                  val exp2 = 10 * (newLevel ^ 2)
                  if (data.exp >= exp2) {
                    data.level = newLevel.toByte
                    data.exp = data.exp - exp2
                    saveData()
                    groupMessageEvent.reply(s"升级成功！当前等级为${data.level}")
                  } else {
                    groupMessageEvent.reply("升级失败！")
                  }
                case _ =>
              }
            } else {
              groupMessageEvent.reply("参数错误！")
            }
          }
        case _ =>
      }
    }
  }
}
