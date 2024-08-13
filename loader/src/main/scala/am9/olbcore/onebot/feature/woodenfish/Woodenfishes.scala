package am9.olbcore.onebot.feature.woodenfish

import am9.olbcore.onebot.Main
import am9.olbcore.onebot.misc.Account
import cn.hutool.core.map.MapUtil

import java.util.TimerTask
//import am9.olbcore.onebot.feature.woodenfish.Woodenfish
import cn.hutool.core.io.FileUtil
import com.google.gson.reflect.TypeToken
import org.jetbrains.annotations.Nullable

import java.io.File
import java.nio.charset.StandardCharsets
import java.util

object Woodenfishes {
  var woodenfishes: util.List[Woodenfish] = new util.ArrayList[Woodenfish]()
  def read(dir: File): Unit = {
    val rawJson = FileUtil.readString(dir, StandardCharsets.UTF_8)
    val t = new TypeToken[util.List[String]](){}.getType
    woodenfishes = Main.json.fromJson[util.List[Woodenfish]](rawJson, t)
  }
  def write(dir: File): Unit = {
    FileUtil.writeString(Main.json.toJson(woodenfishes), dir, StandardCharsets.UTF_8)
  }
  @Nullable
  def getWoodenfish(qq: Long): Woodenfish = {
    woodenfishes.forEach(i => {
      if (i.playerid.equals(qq)) {
        i
      }
    })
    null
  }
  def gongdeLeaderboard(group: Long): Unit = {
    if (woodenfishes != new util.ArrayList[Woodenfish]()) {
      var resultEe: util.Map[Account, Double] = new util.HashMap[Account, Double]()
      var resultE: util.Map[Account, Double] = new util.HashMap[Account, Double]()
      var resultRaw: util.Map[Account, Long] = new util.HashMap[Account, Long]()
      woodenfishes.forEach(i => {
        if (i.ee > 0) {
          resultEe.put(new Account(i.playerid), i.ee)
        } else if (i.e > 0) {
          resultE.put(new Account(i.playerid), i.e)
        } else {
          resultRaw.put(new Account(i.playerid), i.gongde)
        }
      })
      resultEe = MapUtil.sort[Account, Double](resultEe)
      resultE = MapUtil.sort[Account, Double](resultE)
      resultRaw = MapUtil.sort[Account, Long](resultRaw)
      val stringBuilder = new StringBuilder()
      stringBuilder.append("功德榜\n赛博账号 --- 功德")
      resultEe.forEach((k, v) => {
        stringBuilder.append("\n" + k + " --- " + v)
      })
      resultE.forEach((k, v) => {
        stringBuilder.append("\n" + k + " --- " + v)
      })
      resultRaw.forEach((k, v) => {
        stringBuilder.append("\n" + k + " --- " + v)
      })
      Main.oneBot.sendGroup(group, stringBuilder.toString)
    } else {
      Main.oneBot.sendGroup(group, "还没有人注册赛博账号！")
    }
  }
  def banLeaderboard(group: Long): Unit = {
    if (woodenfishes != new util.ArrayList[Woodenfish]()) {
      var result: util.Map[Account, Int] = new util.HashMap[Account, Int]()
      woodenfishes.forEach(i => {
        if (i.total_ban > 0) {
          result.put(new Account(i.playerid), i.total_ban)
        }
      })
      result = MapUtil.sort[Account, Int](result)
      val stringBuilder = new StringBuilder()
      stringBuilder.append("封禁榜\n赛博账号 --- 累计封禁次数")
      result.forEach((k, v) => {
        stringBuilder.append("\n" + k + " --- " + v)
      })
      Main.oneBot.sendGroup(group, stringBuilder.toString)
    } else {
      Main.oneBot.sendGroup(group, "还没有人注册赛博账号！")
    }
  }
  def nirvanaLeaderboard(group: Long): Unit = {
    if (woodenfishes != new util.ArrayList[Woodenfish]()) {
      var result: util.Map[Account, Double] = new util.HashMap[Account, Double]()
      woodenfishes.forEach(i => {
        if (i.nirvana > 1) {
          result.put(new Account(i.playerid), i.nirvana)
        }
      })
      result = MapUtil.sort[Account, Double](result)
      val stringBuilder = new StringBuilder()
      stringBuilder.append("涅槃榜\\n赛博账号 --- 涅槃值")
      result.forEach((k, v) => {
        stringBuilder.append("\n" + k + " --- " + v)
      })
      Main.oneBot.sendGroup(group, stringBuilder.toString)
    } else {
      Main.oneBot.sendGroup(group, "还没有人注册赛博账号！")
    }
  }
  val autoSave = new TimerTask {
    override def run(): Unit = {
      write(new File("woodenfish.json"))
    }
  }
}
