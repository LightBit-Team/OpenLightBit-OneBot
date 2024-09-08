package am9.olbcore.onebot.feature

import am9.olbcore.onebot.Main
import am9.olbcore.onebot.platform.onebot.Segment

import java.io.File
import java.util

object Fish {
  def fish(group: Long, qq: Long): Unit = {
    Main.mediaServer.addRemoteFile("https://api.vvhan.com/api/moyu", new File("temp/yu.png"))
    Main.oneBot.sendGroupWithSegments(group, util.List.of[Segment](
      new Segment("at", new util.HashMap[String, String]() {
        put("qq", qq.toString)
      }),
      new Segment("image", new util.HashMap[String, String]() {
        put("file", Main.mediaServer.getFilePath("yu.png"))
      })
    ))
  }
}
