package am9.olbcore.onebot.platform.onebot.action

import am9.olbcore.onebot.platform.onebot.action.params.SendPrivateMsgParams

class SendPrivateMsg(uid: Long, message: AnyRef, asPlainText: Boolean) extends Action {
  action = "send_private_msg"
  params = new SendPrivateMsgParams(uid, message, asPlainText)
}
