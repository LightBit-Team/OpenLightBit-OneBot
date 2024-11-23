package am9.olbcore.onebot.feature.moyu

import am9.olbcore.onebot.Main
import cn.hutool.http.HttpUtil
import com.google.gson.JsonSyntaxException

object Fish {
  def fish(group: Long, qq: Long): Unit = {
    try {
      val fishReturn = Main.json.fromJson[FishReturn](HttpUtil.get("http://bjb.yunwj.top/php/mo-yu/php.php"), classOf[FishReturn])
      Main.oneBot.sendGroup(group, fishReturn.wb.replaceAll("【换行】", "\n"))
    } catch {
      case e: JsonSyntaxException =>
        throw new RuntimeException("摸鱼日历获取失败！", e)
    }
  }
}
