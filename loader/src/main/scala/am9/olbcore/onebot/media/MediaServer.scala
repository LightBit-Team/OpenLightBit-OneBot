package am9.olbcore.onebot.media

import am9.olbcore.onebot.Main
import cn.hutool.core.io.FileUtil
import cn.hutool.http.HttpUtil

class MediaServer(port: Int) {
  private val server = HttpUtil.createServer(port).setRoot("temp")
  private var started: Boolean = false

  def isStarted: Boolean = started

  def start(): Unit = {
    if (java.lang.Boolean.parseBoolean(Main.config.getData.get("enable-media-server").toString)) {
      FileUtil.del("temp")
      FileUtil.mkdir("temp")
      server.start()
      started = true
    }
  }
  def addRemoteFile(httpAddress: String, file: java.io.File): Unit = {
    if (!java.lang.Boolean.parseBoolean(Main.config.getData.get("enable-media-server").toString)) {
      throw new IllegalStateException("媒体服务器未启用")
    }
    HttpUtil.downloadFile(httpAddress, file)
  }
  def getFilePath(fileName: String): String = {
    s"http://${Main.config.getData.get("media-server-host")}:${Main.config.getData.get("media-server-port")}/$fileName"
  }
}
