package am9.olbcore.onebot.feature.woodenfish

import am9.olbcore.onebot.Main
import cn.hutool.core.date.DateUtil
import cn.hutool.core.util.RandomUtil
import org.jetbrains.annotations.{NonNls, Nullable}

import java.util

// 2kbit-intp woodenfish java-licious edition

class Woodenfish {
  var playerid: Long = 0L
  var time: Long = 0L
  var level: Int = 1
  var gongde: Long = 0L
  var e: Double = 0D
  var ee: Double = 0D
  var nirvana: Double = 1D
  var ban: Int = 0
  var dt: Long = 946656000
  var end_time: Long = 946656000
  var hit_count: Int = 0
  var info_time: Long = 946656000
  var info_count: Int = 0
  var info_ctrl: Long = 946656000
  var total_ban: Int = 0
  def genValue(id: Long): Unit = {
    playerid = id
    time = DateUtil.date().toTimestamp.getTime
  }
  def hit(group: Long): Unit = {
    @Nullable val woodenfish = Woodenfishes.getWoodenfish(playerid)
    if (Woodenfishes.getWoodenfish(playerid) != null) {
      val timeNow = DateUtil.date().toTimestamp.getTime
      if (ban == 0) {
        val add = util.List.of[Int](1, 4, 5)
        val r = RandomUtil.randomInt(0, add.size())
        if ((timeNow - end_time) <= 3) {
          hit_count += 1
        } else {
          hit_count = 1
          end_time = timeNow
        }
        if ((timeNow - end_time) <= 3 && hit_count > 5 && total_ban < 4) {
          ban = 2
          hit_count = 0
          total_ban += 1
          gongde = (gongde * 0.5).toLong
          ee *= 0.5
          e *= 0.5
          dt = timeNow + 5400
          Main.oneBot.sendGroup(group, "DoS佛祖是吧？这就给你封了（恼）（你被封禁90分钟，功德扣掉50%）")
        } else if ((timeNow - end_time) <= 3 && hit_count > 5 && total_ban == 4) {
          ban = 1
          hit_count = 0
          total_ban = 5
          gongde = 0
          ee = 0
          e = 0
          level = 1
          nirvana = 1
        } else {
          Main.oneBot.sendGroup(group, s"功德+${add.get(r)}")
        }
        Woodenfishes.woodenfishes.remove(woodenfish)
        Woodenfishes.woodenfishes.add(this)
      } else {
        Main.oneBot.sendGroup(group, "banned")
      }
    } else {
      Main.oneBot.sendGroup(group, "not_reg")
    }
  }
  def conversion(): Unit = {
    if (Math.log10(gongde.toDouble) >= 6 && e <= 200) {
      Woodenfishes.woodenfishes.remove(this)
      e = Math.log10(Math.pow(10D, e) + gongde.toDouble)
      gongde = 0
      Woodenfishes.woodenfishes.add(this)
    }
    if (Math.log10(e) >= 2 && ee <= 200) {
      Woodenfishes.woodenfishes.remove(this)
      ee = Math.log10(Math.pow(10, ee) + e)
      e = 0
      Woodenfishes.woodenfishes.add(this)
    }
  }
  def getExpression: Array[String] = {
    // todo
    var expression = ""
    var expressionLow = ""
    Array[String](expression, expressionLow)
  }
  def info(group: Long): Unit = {
    ???
  }
}
