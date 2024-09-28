package am9.olbcore.onebot.script.api.async

import cn.hutool.core.thread.ThreadUtil

object AsyncUtil {
  def async(runnable: Runnable): Unit = {
    ThreadUtil.execAsync(runnable)
  }
}
