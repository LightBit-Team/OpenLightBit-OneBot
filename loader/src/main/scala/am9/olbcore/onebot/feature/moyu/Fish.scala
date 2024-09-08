package am9.olbcore.onebot.feature.moyu

import am9.olbcore.onebot.Main
import cn.hutool.http.HttpUtil
import com.google.gson.JsonSyntaxException

import java.util

object Fish {
  def fish(group: Long, qq: Long): Unit = {
    try {
      val fishReturn = Main.json.fromJson[FishReturn](HttpUtil.get("http://bjb.yunwj.top/php/mo-yu/php.php"), classOf[FishReturn])
      val holidayMap = new util.HashMap[String, String]()
      for (i <- fishReturn.wb.split("【换行】")) {
        if (i.contains("天后")) {
          holidayMap.put(i.split("天后").apply(0), i.split("天后").apply(1))
        }
      }
      val sb = new StringBuilder()
      sb.append("摸鱼日历：\n")
      holidayMap.forEach((k, v) => {
        var newTime = s"${java.lang.Short.parseShort(k) * 24}个小时之后就是$v\n"
        if (java.lang.Short.parseShort(k) % 7 == 0) {
          newTime = s"再上${java.lang.Short.parseShort(k) / 7}个六天就是$v\n"
        }
        sb.append(newTime)
      })
      Main.oneBot.sendGroup(group, sb.toString())
    } catch {
      case e: JsonSyntaxException =>
        throw new RuntimeException("摸鱼日历获取失败！", e)
    }
  }
}
