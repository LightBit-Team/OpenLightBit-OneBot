package am9.olbcore.onebot.feature

import am9.olbcore.onebot.Main
import am9.olbcore.onebot.platform.onebot.Segment
import com.google.gson.internal.LinkedTreeMap

import java.io.File
import java.util

object Zhuan {
  def zhuan(group: Long, sender: Long, msg: util.List[LinkedTreeMap[String, AnyRef]], role: String): Unit = {
    if (Main.zhuanJson.getData.get("listen-group").contains(group)) {
      val newSegments = new util.ArrayList[Segment]()
      val roleText = role match
        case "owner" => "群主"
        case "admin" => "管理员"
        case "member" => "群友"
      newSegments.add(
        new Segment("text", new util.HashMap[String, String]() {
          put("text", s"${group}的$roleText${sender}发送了消息：\n")
        })
      )
      msg.forEach(i => {
        val segment = new Segment(i.get("type").toString, i.get("data").asInstanceOf[util.Map[String, String]])
        newSegments.add(segment)
      })
      Main.zhuanJson.getData.get("target-group").forEach(i => {
        Main.oneBot.sendGroupWithSegments(i, newSegments)
      })
    }
  }
  def zhuan(group: Long, sender: Long, msg: String, role: String): Unit = {
    if (Main.zhuanJson.getData.get("listen-group").contains(group)) {
      val roleText = role match
        case "owner" => "群主"
        case "admin" => "管理员"
        case "member" => "群友"
      val content = s"${group}的$roleText${sender}发送了消息：\n$msg"
      Main.zhuanJson.getData.get("target-group").forEach(i => {
        Main.oneBot.sendGroupWithCqCode(i, content)
      })
    }
  }
  def addListenGroup(group: Long, sendGroup: Long): Unit = {
    val map = Main.zhuanJson.getData
    val listenGroups = map.get("listen-group")
    if (!listenGroups.contains(group)) {
      listenGroups.add(group)
      map.put("listen-group", listenGroups)
      Main.zhuanJson.write(new File("zhuan.properties"))
      Main.oneBot.sendGroupWithCqCode(sendGroup, s"已添加被转发群：$group")
    }
  }
  def removeListenGroup(group: Long, sendGroup: Long): Unit = {
    val map = Main.zhuanJson.getData
    val listenGroups = map.get("listen-group")
    if (listenGroups.contains(group)) {
      listenGroups.remove(group)
      map.put("listen-group", listenGroups)
      Main.zhuanJson.write(new File("zhuan.properties"))
      Main.oneBot.sendGroupWithCqCode(sendGroup, s"已删除被转发群：$group")
    }
  }
  def addTargetGroup(group: Long, sendGroup: Long): Unit = {
    val map = Main.zhuanJson.getData
    val targetGroups = map.get("target-group")
    if (!targetGroups.contains(group)) {
      targetGroups.add(group)
      map.put("target-group", targetGroups)
      Main.zhuanJson.write(new File("zhuan.properties"))
      Main.oneBot.sendGroupWithCqCode(sendGroup, s"已添加转发群：$group")
    }
  }
  def removeTargetGroup(group: Long, sendGroup: Long): Unit = {
    val map = Main.zhuanJson.getData
    val targetGroups = map.get("target-group")
    if (targetGroups.contains(group)) {
      targetGroups.remove(group)
      map.put("target-group", targetGroups)
      Main.zhuanJson.write(new File("zhuan.properties"))
      Main.oneBot.sendGroupWithCqCode(sendGroup, s"已删除转发群：$group")
    }
  }
}
