package am9.olbcore.onebot.newapi

import am9.olbcore.onebot.Main

abstract class AbstractModule(val name: String,
                      val description: String,
                      val version: String,
                      val authors: java.util.List[String]) {
  def onEnable(): Unit = {}
  def onDisable(): Unit = {}
  def onLoad(): Unit = {}
  final def registerEvent(processor: EventProcessor): Unit = {
    Main.eventProcessors.add(processor)
    Main.logger.info(s"${this.name}模块注册了事件处理器${processor.getClass.getName}")
  }
}
object AbstractModule {
  def loadModule(clazz: Class[?]): Unit = {
    try {
      clazz match
        case module1: Class[AbstractModule] =>
          Main.modules.add(module1.getDeclaredConstructor().newInstance())
        case null => Main.logger.warn(s"$clazz 不是一个合法的模块类")    
    } catch {
      case _: MatchError => Main.logger.warn(s"$clazz 不是一个合法的模块类")
    }
  }

  def loadModule(clazz: String): Unit = loadModule(Class.forName(clazz))
}

  
