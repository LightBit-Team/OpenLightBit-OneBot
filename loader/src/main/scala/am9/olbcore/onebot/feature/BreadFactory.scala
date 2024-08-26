package am9.olbcore.onebot.feature

import am9.olbcore.onebot.Main
import cn.hutool.json.JSONObject

import java.io.File
import java.util
import java.util.TimerTask

object BreadFactory {
  def init(groupId: Long): Unit = {
    val breadMap = Main.bread.getData
    if (breadMap.get(groupId.toString) == null) {
      breadMap.put(groupId.toString, new java.util.HashMap[String, AnyRef](){
        put("exp", Integer.valueOf(0))
        put("level", Integer.valueOf(1))
        put("bread", Integer.valueOf(0))
        put("flour", Integer.valueOf(0))
        put("yeast", Integer.valueOf(0))
        put("mode", "default")
        put("bread_max", Integer.valueOf(300))
      })
      Main.bread.setData(breadMap)
      Main.bread.write(new File("bread.json"))
      Main.oneBot.sendGroup(groupId, "执行成功！")
    } else {
      Main.oneBot.sendGroup(groupId, "本群已经有面包厂了！")
    }
  }
  def getBread(groupId: Long, number: Int): Unit = {
    val breadMap = Main.bread.getData
    if (breadMap.get(groupId.toString) != null) {
      val json = breadMap.get(groupId.toString).asInstanceOf[java.util.HashMap[String, AnyRef]]
      if (json.get("mode") == "default") {
        if (json.get("bread").asInstanceOf[Int] >= number && number < 100) {
          json.put("bread", Integer.valueOf(json.get("bread").asInstanceOf[Int] - number))
          Main.oneBot.sendGroup(groupId, s"成功获取${number}个面包！")
        } else {
          Main.oneBot.sendGroup(groupId, "面包不够！")
        }
      } else {
        Main.oneBot.sendGroup(groupId, "本群没有面包厂！")
      }
    }
  }
  def getInfo(groupId: Long): Unit = {
    val breadMap = Main.bread.getData
    if (breadMap.get(groupId.toString) != null) {
      val json = breadMap.get(groupId.toString).asInstanceOf[util.Map[String, AnyRef]]
      if (json.get("mode").toString == "default") {
        Main.oneBot.sendGroup(groupId,
          s"""群号：$groupId
             |面包数量：${json.get("bread").toString}
             |面包最大值：${json.get("bread_max").toString}
             |面粉：${json.get("flour").toString}
             |酵母：${json.get("yeast").toString}
             |等级：${json.get("level")}
             |经验：${json.get("exp")}
             |升级所需经验：${(Integer.parseInt(json.get("level").toString) + 1) * 300}
             |""".stripMargin)
      }
    } else {
      Main.oneBot.sendGroup(groupId, "本群没有面包厂！")
    }
  }
  def upgrade(groupId: Long): Unit = {
    val breadMap = Main.bread.getData
    if (breadMap.get(groupId.toString) != null) {
      val json = breadMap.get(groupId.toString).asInstanceOf[util.HashMap[String, AnyRef]]
      if (json.get("mode").toString == "default" && Integer.parseInt(json.get("level").toString) < 5) {
        if (Integer.parseInt(json.get("exp").toString) >= (Integer.parseInt(json.get("level").toString) + 1).*(300)) {
          json.put("level", (Integer.parseInt(json.get("level").toString) + 1).toString)
          json.put("bread_max", (Integer.parseInt(json.get("level").toString) * 300).toString)
        } else {
          Main.oneBot.sendGroup(groupId, s"没有足够经验，要求${(Integer.parseInt(json.get("level").toString) + 1).*(300)}")
        }
      } else {
        Main.oneBot.sendGroup(groupId, "不支持的操作！！")
      }
      Main.bread.setData(breadMap)
      Main.bread.write(new File("bread.json"))
      Main.oneBot.sendGroup(groupId, "升级成功！")
    } else {
      Main.oneBot.sendGroup(groupId, "本群没有面包厂！")
    }
  }
  def expReward(groupId: Long): Unit = {
    val breadMap = Main.bread.getData
    if (breadMap.get(groupId.toString) != null) {
      val json = breadMap.get(groupId.toString).asInstanceOf[util.HashMap[String, AnyRef]]
      if (json.get("mode").toString == "default") {
        json.put("exp", (Integer.parseInt(json.get("exp").toString) + 2).toString)
        Main.bread.setData(breadMap)
        Main.bread.write(new File("bread.json"))
      }
    }
  }
  //todo
  val makeBread: TimerTask = new TimerTask {
    override def run(): Unit = {
      val newData = new util.HashMap[String, AnyRef]()
      Main.bread.getData.forEach((groupId, info) => {
        val json = info.asInstanceOf[JSONObject]
        if (json.getStr("mode") == "default"
          && json.getInt("yeast") > 0
          && json.getInt("flour") > 3) {
          json.set("bread", json.getInt("bread") + 1)
          json.set("yeast", json.getInt("yeast") - 1)
          json.set("flour", json.getInt("flour") - 4)
          if (json.getInt("bread") > json.getInt("bread_max")) {
            json.set("bread", json.getInt("bread_max"))
          }
        }
        newData.put(groupId, json)
      })
      Main.bread.setData(newData)
      Main.bread.write(new File("bread.json"))
    }
  }
  val getMaterial: TimerTask = new TimerTask {
    override def run(): Unit = {
      val newData = new util.HashMap[String, AnyRef]()
      Main.bread.getData.forEach((groupId, info) => {
        val json = info.asInstanceOf[JSONObject]
        json.set("yeast", json.getInt("yeast") + 2)
        json.set("flour", json.getInt("flour") + 6)
        newData.put(groupId, json)
      })
      Main.bread.setData(newData)
      Main.bread.write(new File("bread.json"))
    }
  }
}
