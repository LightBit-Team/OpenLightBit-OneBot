package am9.olbcore.onebot.feature.cave

import am9.olbcore.onebot.Main
import cn.hutool.core.io.FileUtil
import cn.hutool.core.util.RandomUtil
import com.google.gson.reflect.TypeToken

import java.io.File
import java.nio.charset.StandardCharsets

object Cave {
  private var bottles: java.util.List[Bottle] = new java.util.ArrayList[Bottle]()
  def get(group: Long, index: Int): Unit = {
    if (bottles.size() == 0) {
      Main.oneBot.sendGroup(group, "没有漂流瓶")
    } else {
      try {
        val bottle = bottles.get(index - 1)
        Main.oneBot.sendGroup(group, bottle.sender.toString + ": " + bottle.content)
        bottle.showComments(group, 0)
      } catch {
        case e: IndexOutOfBoundsException => Main.oneBot.sendGroup(group, "该页没有漂流瓶！")
      }
    }
  }
  def getRandom(group: Long): Unit = {
    get(group, RandomUtil.randomInt(1, bottles.size() + 1))
  }
  def add(group: Long, user: Long, content: String): Unit = {
    if (content.length() > 100) {
      Main.oneBot.sendGroup(group, "漂流瓶内容过长！")
    } else {
      val bottle = new Bottle()
      bottle.sender = user
      bottle.content = content
      bottle.comments = new java.util.ArrayList[Comment]()
      bottles.add(bottle)
      save(new File("cave.json"))
      Main.oneBot.sendGroup(group, "添加成功！")
    }
  }
  def save(file: File): Unit = {
    FileUtil.writeString(Main.json.toJson(bottles), file, StandardCharsets.UTF_8)
  }

  def read(file: File): Unit = {
    bottles = Main.json.fromJson(FileUtil.readString(file, StandardCharsets.UTF_8), new TypeToken[java.util.List[Bottle]](){}.getType)
  }

}
