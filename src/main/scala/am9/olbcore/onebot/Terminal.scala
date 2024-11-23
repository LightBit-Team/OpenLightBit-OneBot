package am9.olbcore.onebot

import org.dromara.hutool.core.io.file.FileUtil
import org.dromara.hutool.core.io.resource.ClassPathResource
import org.dromara.hutool.setting.props.Props
import org.jetbrains.annotations.NotNull

import java.io.File
import java.net.URL
import java.nio.charset.StandardCharsets
import java.security.cert.X509Certificate
import java.util
import java.util.Objects
import javax.net.ssl.{HttpsURLConnection, TrustManager, X509TrustManager}
import scala.util.control.Breaks.{break, breakable}

object Terminal {
  def isRunningOnServerLauncher: Boolean = {
    val location = this.getClass.getProtectionDomain.getCodeSource.getLocation.toString
    location.contains("Server") || location.contains("version") || location.contains("launcher") ||
      location.contains("data")
  }

  def debug(@NotNull msg: AnyRef): Unit = {
    if (Main.config.getData.get("debug-enabled").toString.toBoolean) {
      Main.loggerDebug(msg.toString)
    }
  }

  def readBuildInfo: Props = {
    val resource = new ClassPathResource("META-INF/b-info")
    val props = new Props()
    props.load(resource.getStream)
    props
  }

  def readClasspathFile(path: String): String = {
    val resource = new ClassPathResource(path)
    resource.readStr(StandardCharsets.UTF_8)
  }

  def saveCan(e: Throwable): Unit = {
    val time = System.currentTimeMillis
    if (!FileUtil.exists("cans")) {
      FileUtil.mkdir("cans")
    }
    if (!FileUtil.exists(s"cans/$time.can.json")) {
      val json = Main.json.toJson(new java.util.HashMap[String, AnyRef](){
        put("time", time.toString)
        put("error", e.getClass.toString)
        put("message", Objects.requireNonNullElse(e.getMessage, "null"))
        put("cause", try {
          new java.util.HashMap[String, AnyRef](){
            put("error", e.getCause.getClass.toString)
            put("message", e.getCause.getMessage)
            val stackTrace = new util.ArrayList[String]()
            for (i <- e.getCause.getStackTrace) {
              stackTrace.add(i.toString)
            }
            put("stackTrace", stackTrace)
          }
        } catch {
          case _: Exception => null
        })
        val stackTrace = new util.ArrayList[String]()
        for (i <- e.getStackTrace) {
          stackTrace.add(i.toString)
        }
        put("stackTrace", stackTrace)
      })
      FileUtil.newFile("./cans/$time.can.json")
      FileUtil.writeString(json, s"./cans/$time.can.json", StandardCharsets.UTF_8)
    }
  }
  def downloadFile(url: String, dest: File): Unit = {
    val connection = URL(url).openConnection().asInstanceOf[HttpsURLConnection]
    try {
      connection.setHostnameVerifier((_, _) => true)
      val sc = javax.net.ssl.SSLContext.getInstance("SSL")
      sc.init(null, Array(new X509TrustManager(){
        override def getAcceptedIssuers: Array[X509Certificate] = null
        override def checkClientTrusted(x509Certificates: Array[X509Certificate], s: String): Unit = {}
        override def checkServerTrusted(x509Certificates: Array[X509Certificate], s: String): Unit = {}
      }), null)
      connection.setSSLSocketFactory(sc.getSocketFactory)
    } catch {
      case e: Exception => throw e
    }
    val inputStream = connection.getInputStream
    val outputStream = FileUtil.getOutputStream(dest)
    val buffer = new Array[Byte](1024)
    breakable {
      while (true) {
        val read = inputStream.read(buffer)
        if (read == -1) break
        outputStream.write(buffer, 0, read)
      }
    }
    inputStream.close()
    outputStream.close()
  }

}
