package am9.olbcore.onebot.misc

object Misc {
  def cond[T](bool: Boolean, value1: T, value2: T): T = {
    if (bool) value1 else value2
  }
}
