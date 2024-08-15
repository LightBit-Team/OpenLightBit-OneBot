package am9.olbcore.onebot
package onebot

trait OneBot {
  def sendGroup(groupId: Long, message: String): Unit
  def sendGroupWithCqCode(groupId: Long, message: String): Unit
  def sendGroupWithSegments(groupId: Long, segments: java.util.List[Segment]): Unit
  def sendFriend(uid: Long, message: String): Unit
  def sendGroupRecord(groupId: Long, fileName: String): Unit
}
