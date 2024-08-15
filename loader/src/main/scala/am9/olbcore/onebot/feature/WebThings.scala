package am9.olbcore.onebot.feature

import am9.olbcore.onebot.Main
import cn.hutool.http.HttpUtil
import cn.hutool.json.{JSONObject, JSONUtil}

import java.nio.charset.StandardCharsets
import java.util

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
}