package am9.olbcore.onebot.feature.helps

import am9.olbcore.onebot.Main

object HelpMenu {
  def helpMenu(group: Long, helps: Helps): Unit = {
    val string = helps match {
      case Helps.HELP =>
        """OpenLightBit帮助
          |!help    !music
          |!echo    !captcha
          |!version !broadcast
          |!op      !listen
          |!bread   !web
          |!woodenfish""".stripMargin
      case Helps.ECHO =>
        "!echo <string> - 复读内容"
      case Helps.VERSION =>
        "!version - 查询机器人版本"
      case Helps.OP =>
        "todo"
      case Helps.BREAD =>
        "todo"
      case Helps.WOODENFISH =>
        "todo"
      case Helps.MUSIC =>
        """!music search <string> <int> - 搜索歌曲（歌名不带空格）
          |!music play <int> - 播放指定id的歌曲""".stripMargin
      case Helps.CAPTCHA =>
        "todo"
      case Helps.BROADCAST =>
        "todo"
      case Helps.CAVE => 
        "todo"
      case Helps.LISTEN =>
        "todo"
      case Helps.WEB =>
        "todo"
    }
    Main.oneBot.sendGroup(group, string)
  }

}
