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
  var woodenfishes: util.Map[Account, Woodenfish] = new util.HashMap[Account, Woodenfish]()
  def read(dir: File): Unit = {
    val rawJson = FileUtil.readString(dir, StandardCharsets.UTF_8)
    val t = new TypeToken[util.Map[Account, Woodenfish]](){}.getType
    woodenfishes = Main.json.fromJson[util.Map[Account, Woodenfish]](rawJson, t)
  }
  def write(dir: File): Unit = {
    FileUtil.writeString(Main.json.toJson(woodenfishes), dir, StandardCharsets.UTF_8)
  }
  @Nullable
  def getWoodenfish(qq: Long): Woodenfish = {
    woodenfishes.forEach((k, v) => {
      if (k.getNumber == qq) {
        v
      }
    })
    null
  }
  def gongdeLeaderboard(group: Long): Unit = {
    if (woodenfishes != new util.ArrayList[Woodenfish]()) {
      var resultEe: util.Map[Account, Double] = new util.HashMap[Account, Double]()
      var resultE: util.Map[Account, Double] = new util.HashMap[Account, Double]()
      var resultRaw: util.Map[Account, Long] = new util.HashMap[Account, Long]()
      woodenfishes.forEach((k, v) => {
        if (v.ee > 0) {
          resultEe.put(k, v.ee)
        } else if (v.e > 0) {
          resultE.put(k, v.e)
        } else {
          resultRaw.put(k, v.gongde)
        }
      })
      resultEe = MapUtil.sort[Account, Double](resultEe)
      resultE = MapUtil.sort[Account, Double](resultE)
      resultRaw = MapUtil.sort[Account, Long](resultRaw)
      val stringBuilder = new StringBuilder()
      stringBuilder.append("功德榜\n赛博账号 --- 功德")
      resultEe.forEach((k, v) => {
        stringBuilder.append("\n" + k.getNumber + " --- " + v)
      })
      resultE.forEach((k, v) => {
        stringBuilder.append("\n" + k.getNumber + " --- " + v)
      })
      resultRaw.forEach((k, v) => {
        stringBuilder.append("\n" + k.getNumber + " --- " + v)
      })
      Main.oneBot.sendGroup(group, stringBuilder.toString)
    } else {
      Main.oneBot.sendGroup(group, "还没有人注册赛博账号！")
    }
  }
  def banLeaderboard(group: Long): Unit = {
    if (woodenfishes != new util.ArrayList[Woodenfish]()) {
      var result: util.Map[Account, Int] = new util.HashMap[Account, Int]()
      woodenfishes.forEach((k, v) => {
        if (v.total_ban > 0) {
          result.put(k, v.total_ban)
        }
      })
      result = MapUtil.sort[Account, Int](result)
      val stringBuilder = new StringBuilder()
      stringBuilder.append("封禁榜\n赛博账号 --- 累计封禁次数")
      result.forEach((k, v) => {
        stringBuilder.append("\n" + k.getNumber + " --- " + v)
      })
      Main.oneBot.sendGroup(group, stringBuilder.toString)
    } else {
      Main.oneBot.sendGroup(group, "还没有人注册赛博账号！")
    }
  }
  def nirvanaLeaderboard(group: Long): Unit = {
    if (woodenfishes != new util.ArrayList[Woodenfish]()) {
      var result: util.Map[Account, Double] = new util.HashMap[Account, Double]()
      woodenfishes.forEach((k, v) => {
        if (v.nirvana > 1) {
          result.put(k, v.nirvana)
        }
      })
      result = MapUtil.sort[Account, Double](result)
      val stringBuilder = new StringBuilder()
      stringBuilder.append("涅槃榜\\n赛博账号 --- 涅槃值")
      result.forEach((k, v) => {
        stringBuilder.append("\n" + k.getNumber + " --- " + v)
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
