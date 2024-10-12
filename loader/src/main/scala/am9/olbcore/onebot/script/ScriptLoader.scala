package am9.olbcore.onebot.script

import am9.olbcore.onebot.Terminal
import am9.olbcore.onebot.script.api.ApiEvent
import cn.hutool.core.io.FileUtil

import java.io.{File, FileFilter}
import java.nio.charset.StandardCharsets
import java.util

object ScriptLoader {
  def getAllScripts: java.util.List[File] = {
    val scriptFolder = new File("scripts")
    if (!FileUtil.exist(scriptFolder)) FileUtil.mkdir(scriptFolder)
    val suffixFilter = new FileFilter {
      override def accept(pathname: File): Boolean = {
        pathname.getName.endsWith(".groovy")
      }
    }
    val ret = new util.ArrayList[File]()
    scriptFolder.listFiles(suffixFilter).foreach(i => {
      ret.add(i)
    })
    ret
  }
  @throws[RuntimeException]
  def loadScript(apiEvent: ApiEvent, scripts: util.List[File]): Unit = {
    ???
  }
}
