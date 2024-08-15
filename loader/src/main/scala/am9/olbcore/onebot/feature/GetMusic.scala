package am9.olbcore.onebot.feature

import am9.olbcore.onebot.Main
import am9.olbcore.onebot.feature.neteasemusic.SearchResponse
import am9.olbcore.onebot.media.MediaServer
import cn.hutool.http.HttpUtil
import org.jetbrains.annotations.NotNull

import java.io.File
import java.util

object GetMusic {
  def getMusic(group: Long, @NotNull musicId: Long): Unit = {
    Main.mediaServer.addRemoteFile(s"http://music.163.com/song/media/outer/url?id=$musicId.mp3", new File("temp/music.mp3")) 
    Main.oneBot.sendGroupRecord(group, "music.mp3")
  }
  def searchMusic(group: Long, musicName: String, page: Int): Unit = {
    val result: SearchResponse = Main.json.fromJson[SearchResponse](
      HttpUtil.get("http://music.163.com/api/search/get/web", new util.HashMap[String, AnyRef](){
        put("csrf_token", null)
        put("hlpretag", null)
        put("hlposttag", null)
        put("s", musicName)
        put("type", "1")
        put("offset", "0")
        put("total", "true")
        put("limit", (3 * page).toString)
    }), classOf[SearchResponse])
    val sb: StringBuilder = new StringBuilder()
    sb.append(s"歌曲名${musicName}的搜索结果如下：")
    sb.append("\n名称 --- 播放命令")
    for (i <- (page - 1) * 3 until page * 3) {
      val song = result.result.songs.get(i)
      sb.append(s"\n${song.name} --- !music ${song.id}")
    }
    Main.oneBot.sendGroup(group, sb.toString)
  }
}
