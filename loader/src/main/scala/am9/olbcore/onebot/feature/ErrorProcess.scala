package am9.olbcore.onebot.feature

import am9.olbcore.onebot.Main

object ErrorProcess {
  def logGroup(groupId: Long, e: Throwable): Unit = {
    var msg = e.getMessage
    var lineNumber = 0
    e.getStackTrace.foreach(i => {
      if (lineNumber < 3) {
        msg += "\n于" + i.toString
      }
      lineNumber = lineNumber + 1
    })
    if (lineNumber > 3) {
      Main.oneBot.sendGroup(groupId, "出现错误：" + msg + s"\n已折叠 ${lineNumber - 3} 条，请在控制台查看完整信息")
      e.getStackTrace.foreach(x => Main.logger.error(x.toString))
    } else {
      Main.oneBot.sendGroup(groupId, "出现错误：" + msg)
    }
  }
}
