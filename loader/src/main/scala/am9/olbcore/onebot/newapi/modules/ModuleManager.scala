package am9.olbcore.onebot.newapi.modules

import am9.olbcore.onebot.Main
import am9.olbcore.onebot.newapi.{AbstractModule, ApiGroupMessageEvent}

class ModuleManager extends AbstractModule(
  "ModuleManager", "模块管理", "0.4.0", java.util.List.of("Emerald-AM9")
) {
  override def onEnable(): Unit = {
    this.registerEvent {
      case groupMessageEvent: ApiGroupMessageEvent =>
        val argArray = groupMessageEvent.getMessage.split(" ")
        if (argArray.nonEmpty) {
          if (argArray.apply(0) == "!module") {
            if (argArray.length == 1) {
              listModule(groupMessageEvent.getGroupId)
            } else {
              argArray.apply(1) match {
                case "list" => listModule(groupMessageEvent.getGroupId)
                case _ =>
              }
            }
          }
        }
      case _ =>
    }
  }
  private def listModule(group: Long): Unit = {
    val sb = new StringBuilder()
    sb.append("当前可用模块：")
    Main.modules.forEach(module => sb.append("\n" + module.name))
    Main.oneBot.sendGroup(group, sb.toString)
  }
}
