package am9.olbcore.onebot.feature

import am9.olbcore.onebot.Main
import am9.olbcore.onebot.platform.onebot.Segment
import cn.hutool.core.thread.ThreadUtil

import java.util

object Broadcast {
  def broadcast(owner: Long, group: Long, msg: String, groups: util.List[Long]): Unit = {
    ThreadUtil.execute(new Runnable(){
      override def run(): Unit = {
        if (Admin.isOwner(owner)) {
          groups.forEach(i => {
            Main.oneBot.sendGroup(i, msg)
          })
          if (group != 0) {
            Main.oneBot.sendGroupWithSegments(group, util.List.of[Segment](
              new Segment("at", new util.HashMap[String, String]() {
                put("qq", owner.toString)
              }),
              new Segment("text", new util.HashMap[String, String]() {
                put("text", " 发送成功！")
              })
            ))
          } else {
            Main.oneBot.sendFriend(owner, "发送成功！")
          }
        } else {
          if (group != 0) {
            Main.oneBot.sendGroupWithSegments(group, util.List.of[Segment](
              new Segment("at", new util.HashMap[String, String]() {
                put("qq", owner.toString)
              }),
              new Segment("text", new util.HashMap[String, String]() {
                put("text", " 权限不足！")
              })
            ))
          } else {
            Main.oneBot.sendFriend(owner, "权限不足！")
          }
        }
      }
    })
  }
}
