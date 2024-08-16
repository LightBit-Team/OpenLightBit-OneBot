package am9.olbcore.onebot.feature.parser

import am9.olbcore.onebot.feature.*
import am9.olbcore.onebot.onebot.event.{FriendMessage, GroupMessage}
import am9.olbcore.onebot.{Main, Terminal}
import cn.hutool.core.thread.ThreadUtil
import cn.hutool.json.{JSONObject, JSONUtil}
import com.google.gson.internal.LinkedTreeMap

object MessageParser {
  def parse(str: String): Unit = {
    ThreadUtil.execute(new Runnable() {
      override def run(): Unit = {
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
                  CommandParser.parseCommand(
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
                case "heartbeat" => Terminal.debug("接收到心跳！")
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
    })
  }
}
