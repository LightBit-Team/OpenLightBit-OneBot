package am9.olbcore.onebot
package feature

import am9.olbcore.onebot.feature.woodenfish.{Woodenfish, Woodenfishes}
import am9.olbcore.onebot.onebot.event.{FriendMessage, GroupMessage}
import cn.hutool.json.{JSONObject, JSONUtil}
import com.google.gson.internal.LinkedTreeMap
import org.jetbrains.annotations.Nullable

import java.util.Random

object Parser {
  def parse(str: String): Unit = {
    val json: JSONObject = JSONUtil.parseObj(str)
    try {
      json.getStr("post_type") match
        case "message" =>
          if (json.getStr("message_type") == "private") {
            val friendMessage: FriendMessage = Main.json.fromJson[FriendMessage](str, classOf[FriendMessage])
            var message: String = null
            if (json.getStr("message_format") == "array") {
              val list = friendMessage.message.asInstanceOf[java.util.List[LinkedTreeMap[String, AnyRef]]]
              if (list.get(0).get("type").toString == "text") {
                list.get(0).get("data").asInstanceOf[java.util.Map[String, String]].forEach((k, v) => {
                  message = v
                })
              }
              if (message == null) message = "null"
            } else {
              message = friendMessage.message.toString
            }
            Rulai.sendRu(friendMessage.user_id)
          } else {
            val groupMessage: GroupMessage = Main.json.fromJson[GroupMessage](str, classOf[GroupMessage])
            var message: String = null
            if (json.getStr("message_format") == "array") {
              val list = groupMessage.message.asInstanceOf[java.util.List[LinkedTreeMap[String, AnyRef]]]
              if (list.get(0).get("type").toString == "text") {
                list.get(0).get("data").asInstanceOf[java.util.Map[String, String]].forEach((k, v) => {
                  message = v
                })
              }
              if (message == null) message = "null"
            } else {
              message = groupMessage.message.toString
            }
            if (message.contains("!")) {
              parseGroupMessage(
                json.getLong("user_id"),
                json.getLong("group_id"),
                json.getLong("message_id"),
                message
              )
            }
          }
          BreadFactory.expReward(json.getLong("group_id"))
        case "meta_event" =>
          json.getStr("meta_event_type") match
            case "lifecycle" =>
              json.getStr("sub_type") match
                case "connect" => Main.logger.info("Hello, OpenLightBit!")
                case "enable" => Main.logger.info("机器人已启用")
                case "disable" => Main.logger.info("机器人已禁用")
                case _ => Main.logger.info(str)
            case "heartbeat" => Terminal.debug("We are still alive!")
            case _ => Terminal.debug(str)
        case "notice" =>
          //json.getStr("notice_type") match
          //  case "group_upload" => Terminal.debug("群文件上传")
          //  case "group_admin" => Terminal.debug("群管理员变动")
          //  case "group_decrease" => Terminal.debug("群成员减少")
          //  case "group_increase" => Terminal.debug("群成员增加")
          //  case "group_ban" => Terminal.debug("群禁言")
          //  case "friend_add" => Terminal.debug("添加好友")
          //  case "friend_recall" => Terminal.debug("好友撤回")
          //  case "group_recall" => Terminal.debug("群消息撤回")
          //  case "notify" => Terminal.debug("提醒")
          //  case "poke" => feature.OnPoke.doIt(json.getLong("group_id"))
          //  case "lucky_king" => Terminal.debug("群红包运气王")
          //  case "honor" => Terminal.debug("群荣誉变更")
          //  case _ => Terminal.debug(str)
          if (json.getStr("notice_type") == "poke") {
            OnPoke.doIt(json.getLong("group_id"))
          }
        case _ =>
          if (!(json.getStr("status").equals("ok") || json.getStr("status").equals("await"))) {
            Main.logger.warn(str)
          }
    } catch {
      case e: MatchError => Main.logger.info("invalid event", e)
    }
  }

  private def parseGroupMessage(senderId: Long, groupId: Long, msgId: Long, str: String): Unit = {
    val p = Main.config.getData.get("command-prefix").toString
    try {
      if (!Admin.isDisabled(groupId)) {
        if (str.startsWith(s"${p}test")) {
          Main.oneBot.sendGroup(groupId, "Hello, World!")
        }
        if (str.startsWith(s"${p}version")) {
          val rd = new Random()
          Main.oneBot.sendGroup(groupId,
            s"""OpenLightBit version ${Main.version}
               |更新内容：${Main.changelog}
               |------------
               |${Main.splashes.get((rd.nextDouble() * (Main.splashes.size() - 1)).round.toInt)}""".stripMargin)
        }
        if (str.startsWith(s"${p}help")) {
          Main.oneBot.sendGroup(groupId,
            """OpenLightBit 帮助
              |------------
              |!version    !deop
              |!help       !disable
              |!ping       !bread
              |!reload     !short_link
              |!shutdown   !query_http
              |!echo
              |!op
              |------------
              |欢迎使用！""".stripMargin)
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
        if (str.startsWith(s"${p}icp")) {
          WebThings.checkICP(str.split(" ").apply(1), groupId)
        }
        if (str.startsWith(s"${p}short_link")) {
          WebThings.getShortLink(str.split(" ").apply(1), groupId)
        }
        if (str.startsWith(s"${p}hitokoto")) {
          WebThings.hitokoto(groupId)
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
        if (str.startsWith(s"${p}enable")) {
          Admin.enable(groupId, senderId)
        }
      }
    } catch {
        case e: Throwable =>
          ErrorProcess.logGroup(groupId, e)
    }
  }
}
