package am9.olbcore.onebot.optimize

class OptimizeReturn extends Serializable{
  private var platformName = ""
  private var optimizePlatform = false
  private var downloadOptimized = false
  def getPlatformName: String = platformName
  def setPlatformName(platformName: String): Unit = this.platformName = platformName
  def isOptimizePlatform: Boolean = optimizePlatform
  def setOptimizePlatform(optimizePlatform: Boolean): Unit = this.optimizePlatform = optimizePlatform
  def isDownloadOptimized: Boolean = downloadOptimized
  def setDownloadOptimized(downloadOptimized: Boolean): Unit = this.downloadOptimized = downloadOptimized
}
