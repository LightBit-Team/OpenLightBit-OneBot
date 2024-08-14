package am9.olbcore.onebot.onebot.action

import am9.olbcore.onebot.onebot.action.params.SendGroupMsgParams

class SendGroupMsg(groupId: Long, message: AnyRef, asPlainText: Boolean) extends Action {
  action = "send_group_msg"
  params = new SendGroupMsgParams(groupId, message, asPlainText)
}
