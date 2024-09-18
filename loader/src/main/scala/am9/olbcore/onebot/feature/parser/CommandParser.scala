package am9.olbcore.onebot.feature.parser

import am9.olbcore.onebot.feature.cave.Cave
import am9.olbcore.onebot.feature.woodenfish.{Woodenfish, Woodenfishes}
import am9.olbcore.onebot.feature.*
import am9.olbcore.onebot.feature.helps.{HelpMenu, Helps}
import am9.olbcore.onebot.feature.moyu.Fish
import am9.olbcore.onebot.{Main, Terminal}
import cn.hutool.core.date.{DateTime, DateUtil}
import cn.hutool.core.thread.ThreadUtil
import cn.hutool.core.util.RandomUtil
import org.jetbrains.annotations.Nullable

import java.{lang, util}

object CommandParser {

  def parseCommand(senderId: Long, groupId: Long, msgId: Long, str: String): Unit = {
    val p = Main.config.getData.get("command-prefix").toString
    Terminal.debug(s"$senderId（$groupId）执行了命令$str")
    try {
      if (!Admin.isDisabled(groupId)) {
        if (str.startsWith(s"${p}test")) {
          ThreadUtil.execAsync(new Runnable(){
            override def run(): Unit = {
              Main.oneBot.sendGroup(groupId, "Hello, World!")
              Thread.sleep(1000)
              Main.oneBot.sendGroup(groupId, "Hello, World!")
            }
          })
        }
        if (str.startsWith(s"${p}version")) {
          val buildInfoMap = Terminal.readBuildInfo
          val buildTimeString = DateUtil.formatDateTime(new DateTime(lang.Long.parseLong(buildInfoMap.get("build-time").toString)))
          val version = buildInfoMap.get("version").toString
          val prettyName = buildInfoMap.get("pretty-name").toString
          Main.oneBot.sendGroup(groupId,
            s"""OpenLightBit version $version($prettyName)
               |构建时间：$buildTimeString
               |更新内容：${Main.changelog}
               |------------
               |${Main.splashes.get(RandomUtil.randomInt(0, Main.splashes.size))}""".stripMargin)
        }
        if (str.startsWith(s"${p}help")) {
          try {
            HelpMenu.helpMenu(groupId, Helps.valueOf(str.split(" ").apply(1).toUpperCase))
          } catch {
            case e: IndexOutOfBoundsException => HelpMenu.helpMenu(groupId, Helps.HELP)
            case e: MatchError => HelpMenu.helpMenu(groupId, Helps.HELP)
          }
        }
        if (str.startsWith(s"${p}ping")) {
          Main.oneBot.sendGroup(groupId, "Pong!")
        }
        if (str.startsWith(s"${p}echo")) {
          val args = str.split(" ")
          Main.oneBot.sendGroup(groupId, args.apply(1))
        }
        if (str.startsWith(s"${p}op")) {
          val args = str.split(" ")
          if (args.length == 2) {
            Admin.op(args.apply(1).toLong, groupId, senderId)
          } else {
            Main.oneBot.sendGroup(groupId, "格式错误")
          }
        }
        if (str.startsWith(s"${p}deop")) {
          val args = str.split(" ")
          if (args.length == 2) {
            Admin.deop(args.apply(1).toLong, groupId, senderId)
          } else {
            Main.oneBot.sendGroup(groupId, "格式错误")
          }
        }
        if (str.startsWith(s"${p}disable")) {
          Admin.disable(groupId, senderId)
        }
        if (str.startsWith(s"${p}bread")) {
          val args = str.split(" ")
          try {
            args.apply(1) match
              case "build_factory" => BreadFactory.init(groupId)
              case "get" => BreadFactory.getBread(groupId, args.apply(2).toInt)
              case "info" => BreadFactory.getInfo(groupId)
              case "upgrade" => BreadFactory.upgrade(groupId)
          } catch {
            case e: IndexOutOfBoundsException => Main.oneBot.sendGroup(groupId, "格式错误")
            case e: NumberFormatException => Main.oneBot.sendGroup(groupId, "格式错误")
            case e: MatchError => Main.oneBot.sendGroup(groupId, "格式错误")
          }
        }
        if (str.startsWith(s"${p}hitokoto")) {
          WebThings.hitokoto(groupId)
        }
        if (str.startsWith(s"${p}gotrend")) {
          val args = str.split(" ")
          if (args.length < 2) {
            Main.oneBot.sendGroup(groupId, "格式错误")
            return
          } else {
            WebThings.goTrend(groupId, Integer.parseInt(args.apply(1)))
          }
        }
        if (str.startsWith(s"${p}info")) {
          val args = str.split(" ")
          if (args.length < 2) {
            Main.oneBot.sendGroup(groupId, "格式错误")
            return
          } else {
            args.apply(1) match
              case "src" => Main.oneBot.sendGroup(groupId, "OpenLightBit")
              case "copyright" => Main.oneBot.sendGroup(groupId, Main.copyright)
              case _ => Main.oneBot.sendGroup(groupId, "格式错误")
          }
        }
        if (str.startsWith(s"${p}woodenfish")) {
          val args = str.split(" ")
          if (args.length < 2) {
            Main.oneBot.sendGroup(groupId, "格式错误")
            return
          } else {
            @Nullable var woodenfish = Woodenfishes.getWoodenfish(senderId)
            if (woodenfish == null) woodenfish = new Woodenfish()
            args.apply(1) match
              case "hit" => woodenfish.hit(groupId)
              case "info" => woodenfish.info(groupId)
              case "upgrade" => woodenfish.upgrade(groupId, if (args.length < 3) null else Integer.parseInt(args.apply(2)))
              case "nirvana" => woodenfish.nirvanaNotGetter(groupId)
              case "jue" => woodenfish.jue(groupId)
              case "leaderboard" => Woodenfishes.gongdeLeaderboard(groupId)
              case "nirvana_leaderboard" => Woodenfishes.nirvanaLeaderboard(groupId)
              case "ban_leaderboard" => Woodenfishes.banLeaderboard(groupId)
              case "reg" =>
                woodenfish = new Woodenfish()
                woodenfish.register(senderId, groupId)
              case _ => Main.oneBot.sendGroup(groupId,
                """木鱼帮助
                  |!woodenfish reg：注册赛博账号（给我木鱼）
                  |!woodenfish hit：敲木鱼
                  |!woodenfish info：查询木鱼信息
                  |!woodenfish upgrade <等级数，默认为1>：升级木鱼
                  |!woodenfish nirvana：涅槃重生
                  |!woodenfish jue：撅佛祖
                  |!woodenfish leaderboard：功德榜
                  |!woodenfish ban_leaderboard：封禁榜
                  |!woodenfish nirvana_leaderboard：涅槃榜""".stripMargin)
          }
        }
        if (str.startsWith(s"${p}music")) {
          val args = str.split(" ")
          if (args.length < 2) {
            Main.oneBot.sendGroup(groupId, "格式错误")
            return
          } else {
            args.apply(1) match
              case "search" =>
                if (args.length < 3) {
                  Main.oneBot.sendGroup(groupId, "格式错误")
                  return
                }
                GetMusic.searchMusic(groupId, args.apply(2), if (args.length < 4) 1 else Integer.parseInt(args.apply(3)))
              case "play" => GetMusic.getMusic(groupId, java.lang.Long.parseLong(args.apply(2)))
              case _ => Main.oneBot.sendGroup(groupId, "格式错误")
          }
        }
        if (str.startsWith(s"${p}captcha")) {
          val args = str.split(" ")
          if (args.length < 3) {
            Main.oneBot.sendGroup(groupId, "格式错误")
            return
          } else {
            args.apply(1) match
              case "get" => Captcha.get(groupId, java.lang.Long.parseLong(args.apply(2)))
              case "check" => Captcha.check(groupId, senderId, args.apply(2))
          }
        }
        if (str.startsWith(s"${p}cave")) {
          val args = str.split(" ")
          if (args.length < 2) {
            Cave.getRandom(groupId)
          } else {
            args.apply(1) match
              case "get" => Cave.get(groupId, java.lang.Integer.parseInt(args.apply(2)))
              case "get_comment" => Cave.getBottle(java.lang.Integer.parseInt(args.apply(2))).showComments(groupId, if (args.length == 3) 1 else java.lang.Integer.parseInt(args.apply(3)))
              case "write" => Cave.add(groupId, senderId, args.apply(2))
              case "write_comment" => Cave.addComment(groupId, senderId, java.lang.Integer.parseInt(args.apply(2)), args.apply(3))
          }
        }
        if (str.startsWith(s"${p}broadcast")) {
          val args = str.split(" ")
          if (args.length < 3) {
            Main.oneBot.sendGroup(groupId, "格式错误")
            return
          }
          val groupList: util.List[Long] = new util.ArrayList[Long]()
          for (i <- 2 until args.length - 1) {
            groupList.add(lang.Long.parseLong(args.apply(i)))
          }
          Broadcast.broadcast(senderId, groupId, args.apply(1), groupList)
        }
        if (str.startsWith(s"${p}status")) {
          Info.showInfo(groupId)
        }
        if (str.startsWith(s"${p}add_listen_group")) {
          Zhuan.addListenGroup(lang.Long.parseLong(str.split(" ").apply(1)), groupId)
        }
        if (str.startsWith(s"${p}del_listen_group")) {
          Zhuan.removeListenGroup(lang.Long.parseLong(str.split(" ").apply(1)), groupId)
        }
        if (str.startsWith(s"${p}add_target_group")) {
          Zhuan.addTargetGroup(lang.Long.parseLong(str.split(" ").apply(1)), groupId)
        }
        if (str.startsWith(s"${p}del_target_group")) {
          Zhuan.removeTargetGroup(lang.Long.parseLong(str.split(" ").apply(1)), groupId)
        }
        if (str.startsWith(s"${p}fish")) {
          Fish.fish(groupId, senderId)
        }
        if (RandomUtil.randomInt(0, 10) == 9) {
          System.gc()
        }
      }
      if (str.startsWith(s"${p}enable")) {
        Admin.enable(groupId, senderId)
      }
    } catch {
        case e: Throwable =>
          ErrorProcess.logGroup(groupId, e)
    }
  }
}
