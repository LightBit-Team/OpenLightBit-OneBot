package am9.olbcore.onebot.optimize

import cn.hutool.core.io.FileUtil
import kotlin.jvm.JvmStatic

import java.util
import java.util.Scanner
import java.util.function.Consumer

class NativeOptimized {
  @JvmStatic
  def optimize(): Unit = {
    
  }
  @JvmStatic
  def use(closable: AutoCloseable, function: Consumer[AutoCloseable]): Unit = {
    function.accept(closable)
    closable.close()
  }
}
