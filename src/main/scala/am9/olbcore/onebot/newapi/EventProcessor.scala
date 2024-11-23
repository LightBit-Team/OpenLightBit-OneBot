package am9.olbcore.onebot.newapi

trait EventProcessor {
  def onEvent(event: ApiEvent): Unit
}