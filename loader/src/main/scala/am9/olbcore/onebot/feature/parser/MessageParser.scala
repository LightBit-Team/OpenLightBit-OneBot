package am9.olbcore.onebot.feature.parser

import am9.olbcore.onebot.feature.*
import am9.olbcore.onebot.platform.onebot.event.{FriendMessage, GroupMessage}
import am9.olbcore.onebot.{Main, Terminal}
import cn.hutool.core.thread.ThreadUtil
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken

object MessageParser {
  def parse(str: String): Unit = {
    ThreadUtil.execute(new Runnable() {
      override def run(): Unit = {
        val json = Main.json.fromJson[LinkedTreeMap[String, AnyRef]](str, new TypeToken[LinkedTreeMap[String, AnyRef]](){})
        try {
          json.get("post_type") match
            case "message" =>
              if (json.get("message_type").toString == "private") {
                val friendMessage: FriendMessage = Main.json.fromJson[FriendMessage](str, classOf[FriendMessage])
                var message: String = null
                if (json.get("message_format").toString == "array") {
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
                if (json.get("message_format").toString == "array") {
                  val list = groupMessage.message.asInstanceOf[java.util.List[LinkedTreeMap[String, AnyRef]]]
                  Zhuan.zhuan(groupMessage.group_id, groupMessage.user_id, list, groupMessage.sender.role)
                  if (list.get(0).get("type").toString == "text") {
                    list.get(0).get("data").asInstanceOf[java.util.Map[String, String]].forEach((k, v) => {
                      message = v
                    })
                  }
                  if (message == null) message = "null"
                } else {
                  Zhuan.zhuan(groupMessage.group_id, groupMessage.user_id, message, groupMessage.sender.role)
                  message = groupMessage.message.toString
                }
                if (message.contains("!")) {
                  CommandParser.parseCommand(
                    java.lang.Double.parseDouble(json.get("user_id").toString).toLong,
                    java.lang.Double.parseDouble(json.get("group_id").toString).toLong,
                    java.lang.Double.parseDouble(json.get("message_id").toString).toLong,
                    message
                  )
                }
              }
              BreadFactory.expReward(java.lang.Double.parseDouble(json.get("group_id").toString).toLong)
            case "meta_event" =>
              json.get("meta_event_type") match
                case "lifecycle" =>
                  json.get("sub_type") match
                    case "connect" => Main.logger.info("Hello, OpenLightBit!")
                    case "enable" => Main.logger.info("机器人已启用")
                    case "disable" => Main.logger.info("机器人已禁用")
                    case _ => Main.logger.info(str)
                case "heartbeat" => Terminal.debug("Heartbeat")
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
              //  case "poke" => Terminal.debug("1")
              //  case _ => Terminal.debug(str)
            case _ =>
              if (!(json.get("status").equals("ok") || json.get("status").equals("await"))) {
                Main.logger.warn(str)
              }
        } catch {
          case e: MatchError => Main.logger.info("invalid event", e)
        }
      }
    })
  }
}
