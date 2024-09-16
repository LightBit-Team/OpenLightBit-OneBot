package am9.olbcore.onebot.feature.event

import am9.olbcore.onebot.Main
import am9.olbcore.onebot.platform.onebot.Sender

import java.util
import java.io.File
import java.util.Objects

object NameChange {
  def check(group: Long, sender: Sender): Unit = {
    var found = false
    Objects.requireNonNullElse(Main.groupDataConfig.getData.get(group.toString), new util.ArrayList[Sender]()).forEach(i => {
      if (i.user_id == sender.user_id) {
        if (i.nickname == sender.nickname) {
          found = true
        } else {
          Main.oneBot.sendGroup(group,
            s"""${sender.user_id}更改了名称！
              |原名称：${i.nickname}
              |新名称：${sender.nickname}""".stripMargin)
        }
      }
    })
    if (!found) {
      val list = Main.groupDataConfig.getData.getOrDefault(group.toString, new util.ArrayList[Sender]())
      list.add(sender)
      val map = Main.groupDataConfig.getData
      map.put(group.toString, list)
      Main.groupDataConfig.setData(map)
      Main.groupDataConfig.write(new File("group_data.json"))
    }
  }
}
