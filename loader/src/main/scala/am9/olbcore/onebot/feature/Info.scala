package am9.olbcore.onebot.feature

import am9.olbcore.onebot.Main
import cn.hutool.core.date.DateUtil
import cn.hutool.system.SystemUtil

object Info {
  def showInfo(group: Long): Unit = {
    val timeNow = System.currentTimeMillis()
    val jvmInfo = SystemUtil.getJvmInfo
    val osInfo = SystemUtil.getOsInfo
    val runtimeInfo = SystemUtil.getRuntimeInfo
    val timeElapsed = timeNow - Main.startTime
    throw new RuntimeException("未实现")
    //Main.oneBot.sendGroup(group,
    //  s"""OpenLightBit运行信息
    //    |运行环境版本：${jvmInfo.getName} ${jvmInfo.getVersion} by ${jvmInfo.getVendor}
    //    |系统版本：${osInfo.getName} ${osInfo.getVersion} ${osInfo.getArch}
    //    |剩余RAM：${runtimeInfo.getFreeMemory / 1048576} / ${runtimeInfo.getUsableMemory / 1048576} MB
    //    |机器人已运行：${DateUtil.format(DateUtil.date(timeElapsed), "HH时mm分ss秒")}""".stripMargin)
  }
}
