package am9.olbcore.onebot.platform

trait Platform {
  def sendGroup(groupId: Long, message: String): Unit
}
