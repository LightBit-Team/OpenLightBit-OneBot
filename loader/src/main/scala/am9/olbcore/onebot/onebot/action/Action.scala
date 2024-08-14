package am9.olbcore.onebot.onebot.action

import am9.olbcore.onebot.onebot.action.params.Params
import com.google.gson.annotations.Expose

abstract class Action {
  @Expose var action: String = ""
  @Expose var params: Params = null
}
