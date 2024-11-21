package am9.olbcore.onebot.newapi.modules

import am9.olbcore.onebot.newapi.{AbstractModule, ApiEvent, ApiGroupMessageEvent, EventProcessor}

class XiuXian extends AbstractModule(
  "xiuxian", "null", "0.4.0", java.util.List.of("Emerald-AM9")
){
  override def onLoad(): Unit = {
    this.registerEvent(XiuXianEventProcessor)
  }

  override def onEnable(): Unit = {

  }

  override def onDisable(): Unit = {

  }

  private object XiuXianEventProcessor extends EventProcessor {
    override def onEvent(event: ApiEvent): Unit = {
      event match {
        case groupMessageEvent: ApiGroupMessageEvent => {
          val argArray = groupMessageEvent.getMessage.split(" ")
          if (argArray.apply(1) == "xiuxian") {
            groupMessageEvent.reply("Hello, world!")
          }
        }
        case _ =>
      }
    }
  }

}
