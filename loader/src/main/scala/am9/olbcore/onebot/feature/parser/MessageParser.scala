package am9.olbcore.onebot.feature.parser

import am9.olbcore.onebot.feature.*
import am9.olbcore.onebot.feature.event.NameChange
import am9.olbcore.onebot.platform.onebot.event.{FriendMessage, GroupMessage}
import am9.olbcore.onebot.script.ScriptLoader
import am9.olbcore.onebot.script.api.ApiGroupMessageEvent
import am9.olbcore.onebot.{Main, Terminal}
import cn.hutool.core.thread.ThreadUtil
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken

object MessageParser {
  def parse(str: String): Unit = {
    ThreadUtil.execute(() => {
      val json = Main.json.fromJson[LinkedTreeMap[String, AnyRef]](str, new TypeToken[LinkedTreeMap[String, AnyRef]]() {})
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
              NameChange.check(groupMessage.group_id, groupMessage.sender)
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
          case _ =>
            if (!(json.get("status").equals("ok") || json.get("status").equals("await"))) {
              Main.logger.warn(str)
            }
      } catch {
        case e: MatchError => Main.logger.info("invalid event", e)
      }
    })
  }
}
