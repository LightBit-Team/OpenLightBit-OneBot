package am9.olbcore.onebot.onebot

import com.google.gson.annotations.Expose

class Sender {
  @Expose
  var user_id: Long = 0
  @Expose
  var nickname: String = null
  @Expose
  var sex: String = null
  @Expose
  var age: Int = 0
}
