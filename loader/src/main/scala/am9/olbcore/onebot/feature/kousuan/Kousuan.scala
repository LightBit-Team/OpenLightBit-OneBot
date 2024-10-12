package am9.olbcore.onebot.feature.kousuan

import am9.olbcore.onebot.Main
import am9.olbcore.onebot.script.api.async.AsyncUtil

import java.util

object Kousuan {
  private val games: util.Map[Long, KousuanGame] = new util.HashMap[Long, KousuanGame]()
  def create(group: Long): Unit = {
    val game: KousuanGame = new KousuanGame()
    games.put(group, game)
    val problem = game.append
    Main.oneBot.sendGroup(group,
      s"""比大小
        |${problem.getInt1} ( ) ${problem.getInt2}
        |使用 !kousuan (>, <, =)""".stripMargin)
  }
  def lessThan(group: Long): Unit = {
    val game: KousuanGame = games.get(group)
    val currentProblem = game.currentProblem
    if (currentProblem.getInt1 < currentProblem.getInt2) {
      AsyncUtil.async(() => {
        Main.oneBot.sendGroup(group, "回答正确")
        Thread.sleep(2000)
        if (game.count == 10) {
          games.remove(group)
          Main.oneBot.sendGroup(group, "游戏结束")
        } else {
          val problem = game.append
          games.put(group, game)
          Main.oneBot.sendGroup(group, s"${problem.getInt1} ( ) ${problem.getInt2}".stripMargin)
        }
      })
    } else {
      Main.oneBot.sendGroup(group, "回答错误")
    }
  }
  def moreThan(group: Long): Unit = {
    val game: KousuanGame = games.get(group)
    val currentProblem = game.currentProblem
    if (currentProblem.getInt1 > currentProblem.getInt2) {
      AsyncUtil.async(() => {
        Main.oneBot.sendGroup(group, "回答正确")
        Thread.sleep(2000)
        if (game.count == 10) {
          games.remove(group)
          Main.oneBot.sendGroup(group, "游戏结束")
        } else {
          val problem = game.append
          games.put(group, game)
          Main.oneBot.sendGroup(group, s"${problem.getInt1} ( ) ${problem.getInt2}".stripMargin)
        }
      })
    } else {
      Main.oneBot.sendGroup(group, "回答错误")
    }
  }
  def eq(group: Long): Unit = {
    val game: KousuanGame = games.get(group)
    val currentProblem = game.currentProblem
    if (currentProblem.getInt1 == currentProblem.getInt2) {
      AsyncUtil.async(() => {
        Main.oneBot.sendGroup(group, "回答正确")
        Thread.sleep(2000)
        if (game.count == 10) {
          games.remove(group)
          Main.oneBot.sendGroup(group, "游戏结束")
        } else {
          val problem = game.append
          games.put(group, game)
          Main.oneBot.sendGroup(group, s"${problem.getInt1} ( ) ${problem.getInt2}".stripMargin)
        }
      })
    } else {
      Main.oneBot.sendGroup(group, "回答错误")
    }
  }
}
