package am9.olbcore.onebot.feature.woodenfish

import am9.olbcore.onebot.Main

import java.util.TimerTask
//import am9.olbcore.onebot.feature.woodenfish.Woodenfish
import cn.hutool.core.io.FileUtil
import com.google.gson.reflect.TypeToken
import org.jetbrains.annotations.Nullable

import java.io.File
import java.nio.charset.StandardCharsets
import java.util

import am9.olbcore.onebot.feature.woodenfish.Woodenfish

object Woodenfishes {
  var woodenfishes: util.List[Woodenfish] = new util.ArrayList[Woodenfish]()
  def read(dir: File): Unit = {
    val rawJson = FileUtil.readString(dir, StandardCharsets.UTF_8)
    val t = new TypeToken[util.List[String]](){}.getType
    woodenfishes = Main.json.fromJson[util.List[Woodenfish]](rawJson, t)
  }
  def write(dir: File): Unit = {
    FileUtil.writeString(Main.json.toJson(woodenfishes), dir, StandardCharsets.UTF_8)
  }
  @Nullable
  def getWoodenfish(qq: Long): Woodenfish = {
    woodenfishes.forEach(i => {
      if (i.playerid.equals(qq)) {
        i
      }
    })
    null
  }
  val autoSave = new TimerTask {
    override def run(): Unit = {
      write(new File("woodenfish.json"))
    }
  }
}
