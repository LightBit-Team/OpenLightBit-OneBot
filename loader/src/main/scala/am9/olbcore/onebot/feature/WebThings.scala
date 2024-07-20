package am9.olbcore.onebot.feature

import cn.hutool.json.{JSONObject, JSONUtil}

import java.io.{BufferedReader, InputStreamReader}
import java.net.{HttpURLConnection, URL}

import java.nio.charset.StandardCharsets

object WebThings {
  def webQuery(url: String): String = {
    val c: HttpURLConnection = new URL(url).openConnection().asInstanceOf[HttpURLConnection]
    c.setRequestMethod("GET")
    c.connect()
    if (c.getResponseCode < 400) {
      val reader = new BufferedReader(new InputStreamReader(c.getInputStream, StandardCharsets.UTF_8))
      var ret = ""
      var line = reader.readLine()
      while (line != null) {
        ret += line
        line = reader.readLine()
      }
      reader.close()
      ret
    } else {
      throw new RuntimeException(s"Request failed ${c.getResponseCode}")
    }
  }
  def checkICP(domain: String, group: Long): Unit = {
    try {
      val json = JSONUtil.parseObj(webQuery(s"https://api.leafone.cn/api/icp?name=${domain}"))
      if (!json.getStr("code").equals("200")) {
        throw new RuntimeException(s"Request failed ${json.getStr("msg")}")
      } else {
        val info = json.getJSONObject("data").get("data").asInstanceOf[java.util.List[JSONObject]].get(0)
        Sender.sendGroup(group,
          s"""${info.getStr("domain")}的备案信息如下：
            |备案号：${json.getStr("mainLicence")}""".stripMargin)
      }
    } catch {
      case e: Throwable =>
        ErrorProcess.logGroup(group, e)
    }
  }
  def getShortLink(url: String, group: Long): Unit = {
    try {
      val json = JSONUtil.parseObj(webQuery(s"https://api.uomg.com/api/long2dwz?dwzapi=dwzcn&url=${url}"))
      if ((!json.getStr("code").equals("1")) || json.getStr("msg").contains("维护")) {
        throw new RuntimeException(s"Request failed ${json.getStr("msg")}")
      } else {
        Sender.sendGroup(group, "短链接为" + json.getStr("ae_url"))
      }
    } catch {
      case e: Throwable =>
        ErrorProcess.logGroup(group, e)
    }
  }
  def hitokoto(group: Long): Unit = {
    try {
      val json = JSONUtil.parseObj(webQuery("https://v1.hitokoto.cn/"))
      var ret: String = null
      if (json.getStr("from") == null) {
        ret = s"${json.getStr("hitokoto")}    ——${json.getStr("from_who")}"
      } else if (json.getStr("from_who") == null) {
        ret = s"${json.getStr("hitokoto")}    ——《${json.getStr("from")}》"
      } else {
        ret = s"${json.getStr("hitokoto")}    ——《${json.getStr("from")}》${json.getStr("from_who")}"
      }
      Sender.sendGroup(group, ret)
    } catch {
      case e: Throwable =>
        ErrorProcess.logGroup(group, e)
    }
  }
}