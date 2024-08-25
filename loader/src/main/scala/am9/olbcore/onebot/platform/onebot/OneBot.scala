package am9.olbcore.onebot.platform.onebot

import am9.olbcore.onebot.platform.Platform

trait OneBot extends Platform {
  def sendFriend(uid: Long, message: String): Unit
  def sendGroupWithCqCode(groupId: Long, message: String): Unit
  def sendGroupWithSegments(groupId: Long, segments: java.util.List[Segment]): Unit
  def sendGroupRecord(groupId: Long, fileName: String): Unit
}
