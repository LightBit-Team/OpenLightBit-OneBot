package am9.olbcore.onebot
package feature

import am9.olbcore.onebot.feature
import cn.hutool.json.{JSONObject, JSONUtil}

object Parser {
  /*
  刚链接上第一个返回类似于：
  {
    "time": unix时间戳,
    "self_id": 10001,
    "post_type": "meta_event",
    "meta_event_type": "lifecycle",
    "sub_type": "connect"
  }

   */
  def parse(str: String): Unit = {
    //str.replaceAll("\\\\n", "\n")
    val json: JSONObject = JSONUtil.parseObj(str)
    //Main.logger.debug(json.toStringPretty)
    //Main.logger.debug(json)
    try {
      json.getStr("post_type") match
        case "message" =>
          if (json.getStr("message_type") == "private") {
            Main.logger.info("get private message")
          } else {
            if (json.getStr("message_format") == "array") {
              //Main.logger.debug(json.toStringPretty)
              Main.logger.info("use cq code pls :)")
            } else {
              //Main.logger.info(json.getStr("message"))
              if (json.getStr("message").contains("!")) {
                parseGroupMessage(
                  json.getLong("user_id"),
                  json.getLong("group_id"),
                  json.getLong("message_id"),
                  json.getStr("message")
                )
              }
            }
          }
          BreadFactory.expReward(json.getLong("group_id"))
        case "meta_event" => {
          json.getStr("meta_event_type") match
            case "lifecycle" =>
              json.getStr("sub_type") match
                case "connect" => Main.logger.info("Hello, OpenLightBit!")
                case "enable" => Main.logger.info("机器人已启用")
                case "disable" => Main.logger.info("机器人已禁用")
                case _ => Main.logger.info(str)
            case "heartbeat" => Main.logger.info("We are still alive!")
            case _ => Main.logger.info(str)
        }
        case "notice" => {
          json.getStr("notice_type") match
            case "group_upload" => Main.logger.info("群文件上传")
            case "group_admin" => Main.logger.info("群管理员变动")
            case "group_decrease" => Main.logger.info("群成员减少")
            case "group_increase" => Main.logger.info("群成员增加")
            case "group_ban" => Main.logger.info("群禁言")
            case "friend_add" => Main.logger.info("添加好友")
            case "friend_recall" => Main.logger.info("好友撤回")
            case "group_recall" => Main.logger.info("群消息撤回")
            case "notify" => Main.logger.info("提醒")
            case "poke" => feature.OnPoke.doIt(json.getLong("group_id"))
            case "lucky_king" => Main.logger.info("群红包运气王")
            case "honor" => Main.logger.info("群荣誉变更")
            case _ => Main.logger.info(str)
        }
        case _ =>
          if (!(json.getStr("status").equals("ok") || json.getStr("status").equals("await"))) {
            Main.logger.info(str)
          }
    } catch {
      case e: MatchError => Main.logger.info("invalid event", e)
    }
  }
  private def parseGroupMessage(senderId: Long, groupId: Long, msgId: Long, str: String): Unit = {
    try {
      if (!Admin.isDisabled(groupId)) {
        if (str.startsWith("!test")) {
          Sender.sendGroup(groupId, "Hello, World!")
        }
        if (str.startsWith("!version")) {
          Sender.sendGroup(groupId,
            """OpenLightBit version 0.0.1.dev
              |更新内容：啥都没有
              |------------
              |不想做，不会做，做不了，脑壳疼""".stripMargin)
        }
        if (str.startsWith("!help")) {
          Sender.sendGroup(groupId,
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
        if (str.startsWith("!ping")) {
          Sender.sendGroup(groupId, "Pong!")
        }
        if (str.startsWith("!echo")) {
          val args = str.split(" ")
          Sender.sendGroup(groupId, args.apply(1))
        }
        if (str.startsWith("!op")) {
          val args = str.split(" ")
          if (args.length == 2) {
            Admin.op(args.apply(1).toLong, groupId, senderId)
          } else {
            Sender.sendGroup(groupId, "格式错误")
          }
        }
        if (str.startsWith("!deop")) {
          val args = str.split(" ")
          if (args.length == 2) {
            Admin.deop(args.apply(1).toLong, groupId, senderId)
          } else {
            Sender.sendGroup(groupId, "格式错误")
          }
        }
        if (str.startsWith("!disable")) {
          Admin.disable(groupId, senderId)
        }
        if (str.startsWith("!bread")) {
          val args = str.split(" ")
          try {
            args.apply(1) match
              case "build_factory" => BreadFactory.init(groupId)
              case "get" => BreadFactory.getBread(groupId, args.apply(2).toInt)
              case "info" => BreadFactory.getInfo(groupId)
              case "upgrade" => BreadFactory.upgrade(groupId)
          } catch {
            case e: IndexOutOfBoundsException => Sender.sendGroup(groupId, "格式错误")
            case e: NumberFormatException => Sender.sendGroup(groupId, "格式错误")
            case e: MatchError => Sender.sendGroup(groupId, "格式错误")
          }
        }
        if (str.startsWith("!icp")) {
          WebThings.checkICP(str.split(" ").apply(1), groupId)
        }
        if (str.startsWith("!short_link")) {
          WebThings.getShortLink(str.split(" ").apply(1), groupId)
        }
        if (str.startsWith("!query_http")) {
          WebThings.webQuery(str.split(" ").apply(1))
        }
        if (str.startsWith("!reload")) {
          //Main.restart(groupId)
        }
        if (str.startsWith("!shutdown")) {
          Main.shutdown()
        }
        if (str.startsWith("!hitokoto")) {
          WebThings.hitokoto(groupId)
        }
      }
      if (str.startsWith("!enable")) {
        Admin.enable(groupId, senderId)
      }
    } catch {
      case e: Throwable =>
        ErrorProcess.logGroup(groupId, e)
    }
  }
}
