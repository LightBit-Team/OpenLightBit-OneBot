package am9.olbcore.onebot.feature

import am9.olbcore.onebot.Main
import cn.hutool.http.HttpUtil
import cn.hutool.json.{JSONObject, JSONUtil}
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken

import java.nio.charset.StandardCharsets
import java.{lang, util}

object WebThings {
  @Deprecated
  def webQuery(url: String): String = {
    HttpUtil.get(url, StandardCharsets.UTF_8)
  }
  def checkICP(domain: String, group: Long): Unit = {
    try {
      val response = HttpUtil.get(s"https://api.leafone.cn/api/icp", new util.HashMap[String, AnyRef](){
        put("name", domain)
      })
      val json = JSONUtil.parseObj(response)
      if (!json.getStr("code").equals("200")) {
        if (json.getStr("code").equals("404")) {
          Main.oneBot.sendGroup(group, "未备案")
        } else {
          throw new RuntimeException(s"Request failed ${json.getStr("msg")}")
        }
      } else {
        val info = json.getJSONObject("data").get("list").asInstanceOf[java.util.List[JSONObject]].get(0)
        Main.oneBot.sendGroup(group,
          s"""${info.getStr("domain")}的备案信息如下：
            |备案号：${info.getStr("mainLicence")}""".stripMargin)
      }
    } catch {
      case e: Throwable =>
        ErrorProcess.logGroup(group, e)
    }
  }
  def getShortLink(url: String, group: Long): Unit = {
    try {
      val response = HttpUtil.get(s"https://api.uomg.com/api/long2dwz", new util.HashMap[String, AnyRef](){
        put("dwzapi", "dwzcn")
        put("url", url)
      })
      val json = JSONUtil.parseObj(response)
      if ((!json.getStr("code").equals("1")) || json.getStr("msg").contains("维护")) {
        throw new RuntimeException(s"Request failed ${json.getStr("msg")}")
      } else {
        Main.oneBot.sendGroup(group, "短链接为" + json.getStr("ae_url"))
      }
    } catch {
      case e: Throwable =>
        ErrorProcess.logGroup(group, e)
    }
  }
  def hitokoto(group: Long): Unit = {
    try {
      val response = HttpUtil.get("https://v1.hitokoto.cn/", StandardCharsets.UTF_8)
      val json = JSONUtil.parseObj(response)
      var ret: String = null
      if (json.getStr("from") == null) {
        ret = s"${json.getStr("hitokoto")}    ——${json.getStr("from_who")}"
      } else if (json.getStr("from_who") == null) {
        ret = s"${json.getStr("hitokoto")}    ——《${json.getStr("from")}》"
      } else {
        ret = s"${json.getStr("hitokoto")}    ——《${json.getStr("from")}》${json.getStr("from_who")}"
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