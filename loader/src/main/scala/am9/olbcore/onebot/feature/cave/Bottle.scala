package am9.olbcore.onebot.feature.cave

import am9.olbcore.onebot.Main
import com.google.gson.annotations.Expose

class Bottle {
  @Expose
  var sender: Long = 0
  @Expose
  var content: String = null
  @Expose
  var comments: java.util.List[Comment] = null
  //index从1开始
  def showComments(group: Long, index: Int): Unit = {
    val page = if (index - 1 < 0 || index - 1 > Math.floor(comments.size() / 3)) 0 else index - 1
    if (comments.size() == 0) {
      Main.oneBot.sendGroup(group, "没有评论")
    } else {
      try {
        val sb = new StringBuilder()
        sb.append("该漂流瓶的评论：")
        for (i <- 0 until 3) {
          sb.append(s"\n${comments.get(i + page * 3).sender}: ${comments.get(i + page * 3).content}")
        }
        sb.append(s"\n$index / ${Math.floor(comments.size() / 3) + 1} 页")
        Main.oneBot.sendGroup(group, sb.toString)
      } catch {
        case e: IndexOutOfBoundsException => Main.oneBot.sendGroup(group, "该页没有评论！")
      }
    }
  }
}
