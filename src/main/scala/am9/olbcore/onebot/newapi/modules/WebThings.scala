package am9.olbcore.onebot.newapi.modules

import am9.olbcore.onebot.newapi.{AbstractModule, ApiGroupMessageEvent}
import am9.olbcore.onebot.Main
import am9.olbcore.onebot.feature.ErrorProcess
import org.dromara.hutool.http.HttpUtil
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken

import java.nio.charset.StandardCharsets
import java.{lang, util}

class WebThings extends AbstractModule(
  "WebThings",
  "WebThings",
  "0.4.0",
  java.util.List.of("Emerald-AM9")
){
  override def onEnable(): Unit = {
    registerEvent {
      case groupMessageEvent: ApiGroupMessageEvent =>
        val str = groupMessageEvent.getRawMessage
        if (str.startsWith("!hitokoto")) {
          hitokoto(groupMessageEvent.getGroupId)
        }
        if (str.startsWith("!gotrend")) {
          val args = str.split(" ")
          if (args.length < 2) {
            Main.oneBot.sendGroup(groupMessageEvent.getGroupId, "格式错误")
          } else {
            goTrend(groupMessageEvent.getGroupId, Integer.parseInt(args.apply(1)))
          }
        }
      case _ =>
    }
  }
  private def hitokoto(group: Long): Unit = {
    try {
      val response = HttpUtil.get("https://v1.hitokoto.cn/", StandardCharsets.UTF_8)
      val json = Main.json.fromJson[util.HashMap[String, String]](response, new TypeToken[util.HashMap[String, String]](){}.getType)
      var ret: String = null
      if (json.get("from") == null) {
        ret = s"${json.get("hitokoto")}    ——${json.get("from_who")}"
      } else if (json.get("from_who") == null) {
        ret = s"${json.get("hitokoto")}    ——《${json.get("from")}》"
      } else {
        ret = s"${json.get("hitokoto")}    ——《${json.get("from")}》${json.get("from_who")}"
      }
      Main.oneBot.sendGroup(group, ret)
    } catch {
      case e: Throwable =>
        ErrorProcess.logGroup(group, e)
    }
  }
  def goTrend(group: Long, skip: Int): Unit = {
    val contents = HttpUtil.get("https://goproxy.cn/stats/trends/latest")
    val jsonDict = Main.json.fromJson[util.List[LinkedTreeMap[String, String]]](contents, new TypeToken[util.List[LinkedTreeMap[String, String]]](){}.getType)
    val messageContent = new StringBuilder()
    messageContent.append("Golang模块排行榜")
    var skips = skip
    try {
      if (skips < 0) {
        skips = 0
        Main.oneBot.sendGroup(group, "页码无效！（将默认为第一页）")
      } else if (skips > 99) {
        skips = 99
        Main.oneBot.sendGroup(group, "页码无效！（将默认为最后一页）")
      }
    } catch {
      case e: NumberFormatException =>
        skips = 0
        Main.oneBot.sendGroup(group, "页码无效！（将默认为第一页）")
      case e: IllegalArgumentException =>
        skips = 0
        Main.oneBot.sendGroup(group, "页码无效！（将默认为第一页）")
    }
    for (i <- 0 until 10) {
      messageContent.append(s"\n第 ${i + skips * 10 + 1} 名" +
      s"\n模块路径：${jsonDict.get(i + skips * 10).get("module_path")}" +
      s"\n下载次数：${jsonDict.get(i + skips * 10).get("download_count")}")
    }
    Main.oneBot.sendGroup(group, messageContent.toString)
  }

}
