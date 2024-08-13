package am9.olbcore.onebot
package onebot

trait OneBot {
  def sendGroup(groupId: Long, message: String): Unit
  def sendGroupWithCqCode(groupId: Long, message: String): Unit
}
