package am9.olbcore.onebot.feature

object OnPoke {
  def doIt(groupId: Long): Unit = {
    Sender.sendGroup(groupId, "你  戳  你  吗")
  }
}
