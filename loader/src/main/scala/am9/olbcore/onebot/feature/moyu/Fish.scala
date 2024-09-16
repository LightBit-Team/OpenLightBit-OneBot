package am9.olbcore.onebot.feature.moyu

import am9.olbcore.onebot.Main
import cn.hutool.http.HttpUtil
import com.google.gson.JsonSyntaxException

object Fish {
  def fish(group: Long, qq: Long): Unit = {
    try {
      val fishReturn = Main.json.fromJson[FishReturn](HttpUtil.get("http://bjb.yunwj.top/php/mo-yu/php.php"), classOf[FishReturn])
      val sb = new StringBuilder()
      fishReturn.wb.split("【换行】").foreach(i => {
        if (i.contains("天后")) {
          sb.append(i.split("天后").apply(0), "天之后就是", i.split("天后").apply(1), "\n")
        }
      })
      Main.oneBot.sendGroup(group, sb.toString)
    } catch {
      case e: JsonSyntaxException =>
        throw new RuntimeException("摸鱼日历获取失败！", e)
    }
  }
}
