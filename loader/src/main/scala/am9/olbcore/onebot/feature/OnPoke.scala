package am9.olbcore.onebot.feature

import am9.olbcore.onebot.Main

object OnPoke {
  def doIt(groupId: Long): Unit = {
    Main.oneBot.sendGroup(groupId, "你  戳  你  吗")
  }
}
