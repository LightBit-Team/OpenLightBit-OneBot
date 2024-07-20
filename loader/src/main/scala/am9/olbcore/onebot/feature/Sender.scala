package am9.olbcore.onebot.feature

import am9.olbcore.onebot.Main
import cn.hutool.json.{JSONObject, JSONUtil}

@SuppressWarnings(Array("deprecation"))
object Sender {
  val groupSendTemplate: JSONObject = JSONUtil.createObj()
    .put("action", "send_group_msg_rate_limited")
    .put("params", null)
    .put("auto_escape", false)
  def sendGroup(groupId: Long, message: String): Unit = {
    val json = groupSendTemplate.clone()
    json.put("params", new JSONObject(true) {
      put("group_id", groupId)
      put("message", new JSONObject(true) {
        put("type", "text")
        put("data", new JSONObject(true) {
          put("text", message)
        })
      })
      //json.put("message", message)
    }
    )
    //Main.logger.info("sending" + json.toStringPretty)
    Main.oneBotWS.send(json.toString)
  }
  def sendGroupWithCqCode(groupId: Long, message: String): Unit = {
    val json = groupSendTemplate.clone()
    json.put("params", new JSONObject(true) {
      put("group_id", groupId)
      put("message", message)
    })
    Main.oneBotWS.send(json.toString)
  }
}
