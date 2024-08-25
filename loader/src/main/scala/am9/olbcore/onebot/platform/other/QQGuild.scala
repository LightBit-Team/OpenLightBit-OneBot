package am9.olbcore.onebot.platform.other

import am9.olbcore.onebot.feature.parser.CommandParser
import am9.olbcore.onebot.platform.Platform
import am9.olbcore.onebot.platform.onebot.{OneBot, Segment}
import cn.hutool.core.util.RandomUtil
import me.zhenxin.qqbot.api.ApiManager
import me.zhenxin.qqbot.event.AtMessageEvent
import me.zhenxin.qqbot.websocket.EventHandler

import java.{lang, util}

class QQGuild extends EventHandler, OneBot {
  var api: ApiManager = null

  override def onAtMessage(e: AtMessageEvent): Unit = {
    CommandParser.parseCommand(
      lang.Long.parseLong(e.getMessage.getGuildId),
      lang.Long.parseLong(e.getMessage.getChannelId),
      lang.Long.parseLong(e.getMessage.getId), 
      e.getMessage.getContent
    )
  }
  
  override def sendGroup(groupId: Long, message: String): Unit = {
    api.getMessageApi.sendMessage(groupId.toString, message, RandomUtil.randomInt(1, 2147483647).toString)
  }

  override def sendFriend(uid: Long, message: String): Unit = ???

  override def sendGroupWithSegments(groupId: Long, segments: util.List[Segment]): Unit = ???

  override def sendGroupRecord(groupId: Long, fileName: String): Unit = ???

  override def sendGroupWithCqCode(groupId: Long, message: String): Unit = ???
}
