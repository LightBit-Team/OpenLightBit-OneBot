package am9.olbcore.onebot.feature

import am9.olbcore.onebot.Main
import cn.hutool.core.date.DateUtil
import cn.hutool.system.SystemUtil
import cn.hutool.system.oshi.OshiUtil

object Info {
  def showInfo(group: Long): Unit = {
    val timeNow = System.currentTimeMillis()
    val jvmInfo = SystemUtil.getJvmInfo
    val osInfo = OshiUtil.getOs
    val cpuInfo = OshiUtil.getCpuInfo
    val globalMemory = OshiUtil.getMemory
    val runtimeInfo = SystemUtil.getRuntimeInfo
    val timeElapsed = timeNow - Main.startTime
    Main.oneBot.sendGroup(group,
      s"""OpenLightBit运行信息
        |系统：${osInfo.getFamily} ${osInfo.getVersionInfo.getVersion} (${osInfo.getVersionInfo.getCodeName}) ${osInfo.getBitness}位
        |运行环境：${jvmInfo.getVendor} ${jvmInfo.getVersion}
        |运行时长：${DateUtil.formatBetween(timeElapsed)}
        |CPU：GENERIC * ${cpuInfo.getCpuNum}
        |内存：${globalMemory.getAvailable / 1048576} MiB / ${globalMemory.getTotal / 1048576} MiB
        |""".stripMargin)
  }
}
