package am9.olbcore.onebot.onebot

import am9.olbcore.onebot.Main
import am9.olbcore.onebot.feature.Parser
import am9.olbcore.onebot.onebot.action.params.{SendGroupMsgParams, SendPrivateMsgParams}
import cn.hutool.http.HttpUtil
import cn.hutool.http.server.SimpleServer

import java.nio.charset.StandardCharsets
import java.util

class OneBotHttp(getUrl: String, postPort: Int) extends OneBot{
  private val server: SimpleServer = HttpUtil.createServer(postPort).addAction("onebot", (request, response) => {
    Parser.parse(request.getBody(StandardCharsets.UTF_8))
  })
  server.start()
  override def sendGroup(groupId: Long, message: String): Unit = {
    val segment = new Segment("text", new java.util.HashMap[String, String](){
      put("text", message)
    })
    val sendGroupMsgParams = new SendGroupMsgParams(groupId, segment, false)
    HttpUtil.post(getUrl + "send_group_msg", Main.json.toJson(sendGroupMsgParams))
  }
  override def sendGroupWithCqCode(groupId: Long, message: String): Unit = {
    val sendGroupMsgParams = new SendGroupMsgParams(groupId, message, false)
    HttpUtil.post(getUrl + "send_group_msg", Main.json.toJson(sendGroupMsgParams))
  }
  override def sendFriend(uid: Long, message: String): Unit = {
    val segment = new Segment("text", new java.util.HashMap[String, String](){
      put("text", message)
    })
    val sendPrivateMsgParams = new SendPrivateMsgParams(uid, segment, false)
    HttpUtil.post(getUrl + "send_private_msg", Main.json.toJson(sendPrivateMsgParams))
  }

  override def sendGroupRecord(groupId: Long, fileName: String): Unit = {
    val configMap = Main.config.getData
    val segment = new Segment("record", new java.util.HashMap[String, String](){
      put("file", s"http://${configMap.get("media-server-host")}:${configMap.get("media-server-port")}/$fileName")
    })
    val sendGroupMsgParams = new SendGroupMsgParams(groupId, segment, false)
    HttpUtil.post(getUrl + "send_group_msg", Main.json.toJson(sendGroupMsgParams))
  }

  override def sendGroupWithSegments(groupId: Long, segments: util.List[Segment]): Unit = {
    val sendGroupMsgParams = new SendGroupMsgParams(groupId, segments, false)
    HttpUtil.post(getUrl + "send_group_msg", Main.json.toJson(sendGroupMsgParams))
  }
}
