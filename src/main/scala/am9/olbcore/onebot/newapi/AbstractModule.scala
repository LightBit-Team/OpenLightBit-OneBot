package am9.olbcore.onebot.newapi

import am9.olbcore.onebot.Main
import scala.collection.JavaConverters.collectionAsScalaIterableConverter

abstract class AbstractModule(val name: String,
                      val description: String,
                      val version: String,
                      val authors: List[String]) {
  def this(name: String, description: String, version: String, authors: java.util.List[String]) = {
    this(name, description, version, authors.asScala.toList)
  }
  def onEnable(): Unit = {}
  def onDisable(): Unit = {}
  def onLoad(): Unit = {}
  final def registerEvent(processor: EventProcessor): Unit = {
    Main.eventProcessors.add(processor)
    //Main.logger.info(s"${this.name}模块注册了事件处理器${processor.getClass.getName}")
  }
}
object AbstractModule {
  def loadModule(clazz: Class[?]): Unit = {
    try {
      Main.modules.add(clazz.asInstanceOf[Class[AbstractModule]].getDeclaredConstructor().newInstance())
    } catch {
      case _: ClassCastException => Main.logger.warn(s"$clazz 不是一个合法的模块类")
    }
  }

  def loadModule(clazz: String): Unit = loadModule(Class.forName(clazz))
}

  
