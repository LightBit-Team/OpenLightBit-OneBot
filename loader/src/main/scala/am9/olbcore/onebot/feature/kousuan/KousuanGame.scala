package am9.olbcore.onebot.feature.kousuan

import cn.hutool.core.util.RandomUtil

class KousuanGame {
  var count: Int = 0
  var currentProblem: Problem = null
  def append: Problem = {
    count += 1
    currentProblem = new Problem(RandomUtil.randomInt(0, 21), RandomUtil.randomInt(0, 21))   
    currentProblem 
  }
}
