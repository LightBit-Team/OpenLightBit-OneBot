package am9.olbcore.onebot.feature.woodenfish

import am9.olbcore.onebot.Main
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
  var woodenfishes: util.Map[Long, Woodenfish] = new util.HashMap[Long, Woodenfish]()
  def read(dir: File): Unit = {
    val rawJson = FileUtil.readString(dir, StandardCharsets.UTF_8)
    if (rawJson.nonEmpty) {
      val t = new TypeToken[util.LinkedHashMap[String, Woodenfish]](){}.getType
      Main.json.fromJson[util.LinkedHashMap[String, Woodenfish]](rawJson, t).forEach((k, v) => {
        woodenfishes.put(java.lang.Long.parseLong(k), v)
      })
    }
  }
  def write(dir: File): Unit = {
    FileUtil.writeString(Main.json.toJson(woodenfishes), dir, StandardCharsets.UTF_8)
  }
  @Nullable
  def getWoodenfish(qq: Long): Woodenfish = {
    try {
      woodenfishes.get(qq)
    } catch {
      case e: Throwable => null
    }
  }
  def gongdeLeaderboard(group: Long): Unit = {
    if (!woodenfishes.isEmpty) {
      var resultEe: util.Map[Long, Double] = new util.HashMap[Long, Double]()
      var resultE: util.Map[Long, Double] = new util.HashMap[Long, Double]()
      var resultRaw: util.Map[Long, Int] = new util.HashMap[Long, Int]()
      woodenfishes.forEach((k, v) => {
        if (v.ee > 0) {
          resultEe.put(k, v.ee)
        } else if (v.e > 0) {
          resultE.put(k, v.e)
        } else if (v.gongde > 0) {
          resultRaw.put(k, v.gongde)
        }
      })
      resultEe = MapUtil.sort[Long, Double](resultEe)
      resultE = MapUtil.sort[Long, Double](resultE)
      resultRaw = MapUtil.sort[Long, Int](resultRaw)
      val stringBuilder = new StringBuilder()
      stringBuilder.append("功德榜\n赛博账号 --- 功德")
      resultEe.forEach((k, v) => {
        stringBuilder.append("\n" + k + " --- ee" + Math.floor(v * 10000) / 10000)
      })
      resultE.forEach((k, v) => {
        stringBuilder.append("\n" + k + " --- e" + Math.floor(v * 10000) / 10000)
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
    if (!woodenfishes.isEmpty) {
      var result: util.Map[Long, Int] = new util.HashMap[Long, Int]()
      woodenfishes.forEach((k, v) => {
        if (v.total_ban > 0) {
          result.put(k, v.total_ban)
        }
      })
      result = MapUtil.sort[Long, Int](result)
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
    if (!woodenfishes.isEmpty) {
      var result: util.Map[Long, Double] = new util.HashMap[Long, Double]()
      woodenfishes.forEach((k, v) => {
        if (v.nirvana > 1) {
          result.put(k, v.nirvana)
        }
      })
      result = MapUtil.sort[Long, Double](result)
      val stringBuilder = new StringBuilder()
      stringBuilder.append("涅槃榜\n赛博账号 --- 涅槃值")
      result.forEach((k, v) => {
        stringBuilder.append("\n" + k + " --- " + v)
      })
      Main.oneBot.sendGroup(group, stringBuilder.toString)
    } else {
      Main.oneBot.sendGroup(group, "还没有人注册赛博账号！")
    }
  }

  val autoSave: TimerTask = new TimerTask {
    override def run(): Unit = {
      write(new File("woodenfish.json"))
    }
  }
}
