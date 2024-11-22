package am9.olbcore.onebot.newapi.modules

import am9.olbcore.onebot.Main
import am9.olbcore.onebot.newapi.{AbstractModule, ApiGroupMessageEvent}
import cn.hutool.core.thread.ThreadUtil
import cn.hutool.core.util.RandomUtil

import scala.collection.mutable

class Kousuan extends AbstractModule(
  "Kousuan", "Kousuan", "0.4.0", java.util.List.of("Emerald-AM9")
){
  private val games: mutable.Map[Long, KousuanGame] = mutable.Map()
  override def onLoad(): Unit = {
    registerEvent match {
      case groupMessageEvent: ApiGroupMessageEvent =>
        val str = groupMessageEvent.getMessage
        if (str.startsWith(s"${Main.config.getData.get("command-prefix").toString}kousuan")) {
          val args = str.split(" ")
          if (args.length < 2) {
            groupMessageEvent.reply("格式错误")
          } else {
            args.apply(1) match {
              case "create" => create(groupMessageEvent.getGroupId)
              case ">" => moreThan(groupMessageEvent.getGroupId)
              case "<" => lessThan(groupMessageEvent.getGroupId)
              case "=" => eq(groupMessageEvent.getGroupId)
              case _ => groupMessageEvent.reply("格式错误")
            }
          }
        }
    }
  }
  private def create(group: Long): Unit = {
    val game: KousuanGame = new KousuanGame()
    games.put(group, game)
    val problem = game.append
    Main.oneBot.sendGroup(group,
      s"""比大小
        |${problem.int1} ( ) ${problem.int2}
        |使用 !kousuan (>, <, =)""".stripMargin)
  }
  private def lessThan(group: Long): Unit = {
    val game: KousuanGame = games(group)
    val currentProblem = game.currentProblem
    if (currentProblem.int1 < currentProblem.int2) {
      ThreadUtil.execAsync(() => {
        Main.oneBot.sendGroup(group, "回答正确")
        Thread.sleep(2000)
        if (game.count == 10) {
          games.remove(group)
          Main.oneBot.sendGroup(group, "游戏结束")
        } else {
          val problem = game.append
          games.put(group, game)
          Main.oneBot.sendGroup(group, s"${problem.int1} ( ) ${problem.int2}".stripMargin)
        }
      }.asInstanceOf[Runnable])
    } else {
      Main.oneBot.sendGroup(group, "回答错误")
    }
  }
  private def moreThan(group: Long): Unit = {
    val game: KousuanGame = games(group)
    val currentProblem = game.currentProblem
    if (currentProblem.int1 > currentProblem.int2) {
      ThreadUtil.execAsync(() => {
        Main.oneBot.sendGroup(group, "回答正确")
        Thread.sleep(2000)
        if (game.count == 10) {
          games.remove(group)
          Main.oneBot.sendGroup(group, "游戏结束")
        } else {
          val problem = game.append
          games.put(group, game)
          Main.oneBot.sendGroup(group, s"${problem.int1} ( ) ${problem.int2}".stripMargin)
        }
      }.asInstanceOf[Runnable])
    } else {
      Main.oneBot.sendGroup(group, "回答错误")
    }
  }
  private def eq(group: Long): Unit = {
    val game: KousuanGame = games(group)
    val currentProblem = game.currentProblem
    if (currentProblem.int1 == currentProblem.int2) {
      ThreadUtil.execAsync(() => {
        Main.oneBot.sendGroup(group, "回答正确")
        Thread.sleep(2000)
        if (game.count == 10) {
          games.remove(group)
          Main.oneBot.sendGroup(group, "游戏结束")
        } else {
          val problem = game.append
          games.put(group, game)
          Main.oneBot.sendGroup(group, s"${problem.int1} ( ) ${problem.int2}".stripMargin)
        }
      }.asInstanceOf[Runnable])
    } else {
      Main.oneBot.sendGroup(group, "回答错误")
    }
  }
  private class KousuanGame {
    var count: Int = 0
    var currentProblem: Problem = Problem(0, 0)
    def append: Problem = {
      count += 1
      currentProblem = Problem(RandomUtil.randomInt(0, 21), RandomUtil.randomInt(0, 21))   
      currentProblem 
    }
  }
  private case class Problem(int1: Int, int2: Int) {
  }
}