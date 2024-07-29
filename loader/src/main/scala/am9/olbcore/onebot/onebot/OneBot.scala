package am9.olbcore.onebot
package onebot

import cn.hutool.json.{JSONObject, JSONUtil}

trait OneBot {
  def sendGroup(groupId: Long, message: String): Unit
  def sendGroupWithCqCode(groupId: Long, message: String): Unit
}
