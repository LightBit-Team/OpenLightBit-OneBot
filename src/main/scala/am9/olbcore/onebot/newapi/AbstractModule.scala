package am9.olbcore.onebot.newapi

import am9.olbcore.onebot.Main
import com.google.gson.Gson
import org.dromara.hutool.core.io.file.FileUtil
import org.dromara.hutool.log.Log

import java.io.FileFilter
import java.util.jar.{JarEntry, JarInputStream}
import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.util.control.Breaks.{break, breakable}

abstract class AbstractModule(val name: String,
                      val description: String,
                      val version: String,
                      val authors: List[String]) {
  def this(name: String, description: String, version: String, authors: java.util.List[String]) = {
    this(name, description, version, authors.asScala.toList)
  }
  def this(name: String) = {
    this(name, "", "", List())
  }
  def onEnable(): Unit = {}
  def onDisable(): Unit = {}
  def onLoad(): Unit = {}
  final def registerEvent(processor: EventProcessor): Unit = {
    Main.eventProcessors.add(processor)
    //Main.logger.info(s"${this.name}模块注册了事件处理器${processor.getClass.getName}")
  }
  final def getLog: Log = Main.logger
  final def getGson: Gson = Main.json
}
object AbstractModule {
  def loadModule(clazz: Class[?]): Unit = {
    try {
      val module = clazz.asInstanceOf[Class[AbstractModule]].getDeclaredConstructor().newInstance()
      Main.logger.info(s"加载模块${module.name}")
      Main.modules.add(module)
    } catch {
      case _: ClassCastException => Main.logger.warn(s"$clazz 不是一个合法的模块类")
    }
  }

  def loadModule(clazz: String): Unit = loadModule(Class.forName(clazz))

  @throws[RuntimeException]
  def getModules: List[Class[?]] = {
    import org.virtuslab.yaml.*
    val ret = new scala.collection.mutable.ListBuffer[Class[?]]()

    if (FileUtil.exists("modules")) {
      for (file <- FileUtil.loopFiles("modules", _.getName.endsWith(".jar")).asScala) {
        val stream = FileUtil.getInputStream(file)
        val jarStream = new JarInputStream(stream)

        try {
          var entry: JarEntry = jarStream.getNextJarEntry
          breakable({
            while (entry != null) {
              if (entry.getName == "module.yml") {
                val content = scala.io.Source.fromInputStream(jarStream).mkString
                content.as[Map[String, Any]] match {
                  case Left(error) => throw error
                  case Right(yaml) =>
                    val name = yaml("name").toString
                    val clazz = yaml("main").toString
                    ret += Class.forName(clazz)
                }
                jarStream.closeEntry()
                break
              }
              jarStream.closeEntry()
              entry = jarStream.getNextJarEntry
            }
          })
        } catch {
          case e: Exception =>
            throw new RuntimeException(e)
          // 处理异常，例如记录日志或返回空列表
        } finally {
          jarStream.close()
          stream.close()
        }
      }
    }
    ret.toList
  }

  def loadModules(): Unit = {
    for (clazz <- getModules) {
      loadModule(clazz)
    }
  }
}

  
