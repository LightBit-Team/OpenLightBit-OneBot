package am9.olbcore.onebot.feature.woodenfish

import am9.olbcore.onebot.Main
import am9.olbcore.onebot.misc.{Misc, Terminal, YuShengJun}
import cn.hutool.core.date.DateUtil
import cn.hutool.core.util.RandomUtil
import com.google.gson.annotations.Expose
import org.jetbrains.annotations.Nullable

import java.util
import scala.util.control.Breaks.break

class Woodenfish extends YuShengJun {
  @Expose private var playerid: Long = 0L
  @Expose private var time: Long = 0L
  @Expose private var level: Int = 1
  var gongde: Long = 0L
  var e: Double = 0D
  var ee: Double = 0D
  var nirvana: Double = 1D
  @Expose private var ban: Int = 0
  @Expose private var dt: Long = 946656000
  @Expose private var end_time: Long = 946656000
  @Expose private var hit_count: Int = 0
  @Expose private var info_time: Long = 946656000
  @Expose private var info_count: Int = 0
  @Expose private var info_ctrl: Long = 946656000
  private val p = Main.config.getData.get("command-prefix").toString
  var total_ban: Int = 0
  def register(id: Long, group: Long): Unit = {
    if (Woodenfishes.getWoodenfish(id) == null) {
      playerid = id
      time = System.currentTimeMillis
      Woodenfishes.woodenfishes.put(playerid, this)
      Main.oneBot.sendGroup(group, "注册成功")
    } else {
      Main.oneBot.sendGroup(group, "你已注册过，无需重复注册")
    }
  }
  def hit(group: Long): Unit = {
    @Nullable val woodenfish = Woodenfishes.getWoodenfish(playerid)
    if (Woodenfishes.getWoodenfish(playerid) != null) {
      val timeNow = System.currentTimeMillis
      if (ban == 0) {
        val add = util.List.of[Int](1, 4, 5)
        val r = RandomUtil.randomInt(0, add.size())
        if (timeNow - end_time <= 3000) {
          hit_count += 1
        } else {
          hit_count = 1
          end_time = timeNow
        }
        if (timeNow - end_time <= 3000 && hit_count > 5 && total_ban < 4) {
          ban = 2
          hit_count = 0
          total_ban += 1
          gongde = (gongde * 0.5).toLong
          ee *= 0.5
          e *= 0.5
          dt = timeNow + 5400000
          Main.oneBot.sendGroup(group, "DoS佛祖是吧？这就给你封了（恼）（你被封禁90分钟，功德扣掉50%）")
        } else if (timeNow - end_time <= 3000 && hit_count > 5 && total_ban == 4) {
          ban = 1
          hit_count = 0
          total_ban = 5
          gongde = 0
          ee = 0
          e = 0
          level = 1
          nirvana = 1
          Main.oneBot.sendGroup(group, "多次DoS佛祖，死不悔改，罪不可赦（恼）（你被永久封禁，等级、涅槃值重置，功德清零）")
        } else {
          Main.oneBot.sendGroup(group, s"功德+${add.get(r)}")
        }
        Woodenfishes.woodenfishes.put(playerid, this)
      } else {
        Main.oneBot.sendGroup(group, "敲拟吗呢？宁踏马被佛祖封号辣（恼）")
      }
    } else {
      Main.oneBot.sendGroup(group, s"宁踏马害没注册？快发送“${p}woodenfish reg”注册罢！")
    }
  }
  private def conversion(): Unit = {
    if (Math.log10(gongde.toDouble) >= 6 && e <= 200) {
      e = Math.log10(Math.pow(10D, e) + gongde.toDouble)
      gongde = 0
      Woodenfishes.woodenfishes.put(playerid, this)
    }
    if (Math.log10(e) >= 2 && ee <= 200) {
      ee = Math.log10(Math.pow(10, ee) + e)
      e = 0
      Woodenfishes.woodenfishes.put(playerid, this)
    }
  }
  private def getExpression: Array[String] = {
    var expression = ""
    var expressionLow = ""
    var gongdeLocal = ""
    var gongdeLow = ""
    if (ee >= 1) {
      expression = "(10^10^" + Math.floor(10000 * ee) / 10000 + ")"
      expressionLow = "(10^" + Math.floor(10000 * e) / 10000 + ")"
      gongdeLocal = "ee" + Math.floor(10000 * ee) / 10000 + "（" + expression + "）"
      gongdeLow = "\ne（log10）：" + Math.floor(10000 * e) / 10000 + "（" + expressionLow + "）\n原始功德：" + gongde
    } else if (e >= 1) {
      expression = "(10^" + Math.floor(10000 * e) / 10000 + ")"
      gongdeLocal = "ee" + Math.floor(10000 * e) / 10000 + "（" + expression + "）"
      gongdeLow = "原始功德：" + gongde
    } else {
      gongdeLocal = gongde.toString
      gongdeLow = "无"
    }
    Array[String](expression, expressionLow, gongdeLocal, gongdeLow)
  }
  private def autoNirvana: Boolean = {
    if (ee >= 300) {
      if (nirvana + 0.02 < 5) {
        nirvana += 0.02
      } else {
        nirvana = 5
      }
      level = 1
      gongde = 0
      e = 0
      ee = 0
      Woodenfishes.woodenfishes.put(playerid, this)
      true
    } else {
      false
    }
  }
  private def getExperience(): Unit = {
    val timeNow = DateUtil.date().toTimestamp.getTime
    if (ban != 0) return
    val cycleSpeed = Math.ceil(60 * Math.pow(0.978, level - 1))
    val elapsedTime = timeNow - time
    if (elapsedTime < cycleSpeed) return
    val cycles = if (level + 11 > Math.floor(elapsedTime / cycleSpeed)) Math.floor(elapsedTime / cycleSpeed).toInt else level + 11
    val actualCycles = if (cycles > 120) 120 else cycles
    try {
      for (i <- 0 until actualCycles) {
        if (e >= 200) break
        e = Math.log10(Math.pow(10, e) + gongde) * Math.pow(2.7D, nirvana) + level
        gongde = Math.round(Math.pow(10, e - Math.floor(e)))
      }
    } catch {
      case e: Throwable => Terminal.debug("break是抛出的？" + e)
    }
    if (e < 6) {
      e = 0
      gongde = Math.round(Math.pow(10, e) + gongde)
    }
    time = timeNow - (elapsedTime % cycleSpeed).toLong
    Woodenfishes.woodenfishes.put(playerid, this)
  }
  def info(group: Long): Unit = {
    var status = ""
    var tips = ""
    val timeNow = System.currentTimeMillis
    if (Woodenfishes.getWoodenfish(playerid) != null) {
      getExperience()
      if (info_ctrl < timeNow) {
        ban match
          case 0 =>
            status = "正常"
            tips = "【敲电子木鱼，见机甲佛祖，取赛博真经】"
            conversion()
          case 1 =>
            status = "永久封禁中"
            tips = "【我说那个佛祖啊，我刚刚在刷功德的时候，你有在偷看罢？】"
          case 2 =>
            if (timeNow < dt) {
              val banUntil = DateUtil.date(dt).toString
              status = s"暂时封禁中（直至：$banUntil）"
              tips = "【待封禁结束后，可发送“我的木鱼”解封】"
            } else {
              ban = 0
              time = timeNow
              status = "正常"
              tips = "【敲电子木鱼，见机甲佛祖，取赛博真经】"
            }
        if (timeNow - info_time <= 10000) {
          info_count += 1
        } else {
          info_count = 1
          info_time = timeNow
        }
        if (autoNirvana) {
          Main.oneBot.sendGroup(group, "宁踏马功德太多辣（恼）（已自动涅槃重生，涅槃值+0.2）")
        }
        val expressions = getExpression
        if (timeNow - info_time <= 10 && info_count > 5) {
          info_ctrl = timeNow + 180000
          info_count = 0
          Woodenfishes.woodenfishes.put(playerid, this)
          Main.oneBot.sendGroup(group, "宁踏马3分钟之内也别想用我的木鱼辣（恼）")
        } else {
          Main.oneBot.sendGroup(group,
            s"""赛博账号：$playerid
              |账号状态：$status
              |木鱼等级：$level
              |涅槃值：$nirvana
              |当前速度：${Math.ceil(60 * Math.pow(0.978, level - 1))} 秒/周期
              |当前功德：${expressions.apply(0)} ${expressions.apply(1)}
              |低级功德储备：${expressions.apply(2)} ${expressions.apply(3)}
              |$tips""".stripMargin)
        }
      }
    } else {
      Main.oneBot.sendGroup(group, s"宁踏马害没注册？快发送“${p}woodenfish reg”注册罢！")
    }
  }
  def upgrade(group: Long, upgradingLevel: Int | Null): Unit = {
    if (Woodenfishes.getWoodenfish(playerid) != null) {
      val actualUpgradingLevel = Misc.cond[Int](upgradingLevel.isInstanceOf[Int], upgradingLevel.asInstanceOf[Int], 1)
      if (actualUpgradingLevel > 0 && ban == 0) {
        val neededE = level + actualUpgradingLevel + 2
        if (e >= neededE) {
          e -= neededE
          level += actualUpgradingLevel
          Woodenfishes.woodenfishes.put(playerid, this)
          Main.oneBot.sendGroup(group, "木鱼升级成功辣（喜）")
        } else if (Math.pow(10, ee) + e >= neededE) {
          e = 0
          ee = Math.log10(Math.pow(10, ee) + e) - neededE
          level += actualUpgradingLevel
          Woodenfishes.woodenfishes.put(playerid, this)
          Main.oneBot.sendGroup(group, "木鱼升级成功辣（喜）")
        } else {
          Main.oneBot.sendGroup(group, "升级个毛啊？宁踏马功德不够（恼）")
        }
      } else if (ban != 0) {
        Main.oneBot.sendGroup(group, "升级个毛啊？宁踏马被佛祖封号辣（恼）")
      } else if (actualUpgradingLevel <= 0) {
        //此处改为抛出异常
        throw new NumberFormatException("升级个毛啊？宁这数字踏马怎么让我理解？（恼）")
      }
    } else {
      Main.oneBot.sendGroup(group, s"宁踏马害没注册？快发送“${p}woodenfish reg”注册罢！")
    }
  }
  def nirvanaNotGetter(group: Long): Unit = {
    if (Woodenfishes.getWoodenfish(playerid) != null) {
      if (nirvana < 5) {
        if (ban == 0) {
          if (ee >= 10 + Math.floor((nirvana - 1) / 0.05) * 1.5) {
            nirvana += 0.05D
            level = 1
            ee = 0
            e = 0
            gongde = 0
            Woodenfishes.woodenfishes.put(playerid, this)
            Main.oneBot.sendGroup(group, "涅槃重生成功辣（喜）")
          } else {
            Main.oneBot.sendGroup(group, "涅槃重生个毛啊？宁踏马功德不够（恼）")
          }
        } else {
          Main.oneBot.sendGroup(group, "涅槃重生个毛啊？宁踏马被佛祖封号辣（恼）")
        }
      } else {
        Main.oneBot.sendGroup(group, "涅槃重生个毛啊？宁踏马已经不能涅槃重生辣（恼）")
      }
    } else {
      Main.oneBot.sendGroup(group, s"宁踏马害没注册？快发送“${p}woodenfish reg”注册罢！")
    }
  }
  def jue(group: Long): Unit = {
    if (Woodenfishes.getWoodenfish(playerid) != null) {
      if (ban == 0) {
        ban = 1
        Woodenfishes.woodenfishes.put(playerid, this)
        Main.oneBot.sendGroup(group, "敢撅佛祖？罪不可赦（恼）（你被永久封禁）")
      } else {
        Main.oneBot.sendGroup(group, "撅拟吗呢？宁踏马被佛祖封号辣（恼）")
      }
    } else {
      Main.oneBot.sendGroup(group, s"宁踏马害没注册？快发送“${p}woodenfish reg”注册罢！")
    }
  }

  override def toString: String = {
    val expressions = getExpression
    var status: String = null
    var tips: String = null
    ban match
      case 0 =>
        status = "正常"
        tips = "【敲电子木鱼，见机甲佛祖，取赛博真经】"
      case 1 =>
        status = "永久封禁中"
        tips = "【我说那个佛祖啊，我刚刚在刷功德的时候，你有在偷看罢？】"
      case 2 =>
        val timeNow = DateUtil.date().toTimestamp.getTime
        if (timeNow < dt) {
          val banUntil = DateUtil.date(dt).toString
          status = s"暂时封禁中（直至：$banUntil）"
          tips = "【待封禁结束后，可发送“我的木鱼”解封】"
        } else {
          ban = 0
          time = timeNow
          status = "正常"
          tips = "【敲电子木鱼，见机甲佛祖，取赛博真经】"
          Woodenfishes.woodenfishes.put(playerid, this)
        }
    s"""赛博账号：$playerid
       |账号状态：$status
       |木鱼等级：$level
       |涅槃值：$nirvana
       |当前速度：${Math.ceil(60 * Math.pow(0.978, level - 1))} 秒/周期
       |当前功德：${expressions.apply(0)} ${expressions.apply(1)}
       |低级功德储备：${expressions.apply(2)} ${expressions.apply(3)}
       |$tips""".stripMargin
  }

  override def equals(obj: Any): Boolean = this.toString == obj.toString
}
