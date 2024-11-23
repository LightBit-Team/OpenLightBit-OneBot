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
        """木鱼帮助
          |!woodenfish reg：注册赛博账号（给我木鱼）
          |!woodenfish hit：敲木鱼
          |!woodenfish info：查询木鱼信息
          |!woodenfish upgrade <等级数，默认为1>：升级木鱼
          |!woodenfish nirvana：涅槃重生
          |!woodenfish jue：撅佛祖
          |!woodenfish leaderboard：功德榜
          |!woodenfish ban_leaderboard：封禁榜
          |!woodenfish nirvana_leaderboard：涅槃榜""".stripMargin
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
