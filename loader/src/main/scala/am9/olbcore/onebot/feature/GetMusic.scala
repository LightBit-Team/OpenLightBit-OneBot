package am9.olbcore.onebot.feature

import am9.olbcore.onebot.Main
import am9.olbcore.onebot.feature.neteasemusic.{SearchResponse, URLResponse}
import cn.hutool.core.io.FileUtil
import cn.hutool.core.thread.ThreadUtil
import cn.hutool.http.HttpUtil
import org.jetbrains.annotations.NotNull

import java.io.File
import java.util

object GetMusic {
  private val neteaseCloudMusicApi = "http://zm.armoe.cn"
  def getMusic(group: Long, @NotNull musicId: Long): Unit = {
    try {
      ThreadUtil.execute(new Runnable() {
        override def run(): Unit = {
          val request = HttpUtil
            .createGet(s"${neteaseCloudMusicApi}/song/url?id=$musicId", true)
            .execute(true)
          ThreadUtil.safeSleep(1000)
          val json = request.body()
          val response = Main.json.fromJson[URLResponse](json, classOf[URLResponse])
          if (response.code != 200) {
            Main.oneBot.sendGroup(group, "获取歌曲失败！")
            return
          }
          if (response.data.get(0).fee == 1) {
            Main.oneBot.sendGroup(group, "不支持完整播放收费歌曲！")
          }
          FileUtil.del("temp/music.mp3")
          Main.mediaServer.addRemoteFile(response.data.get(0).url, new File("temp/music.mp3"))
          Main.oneBot.sendGroupRecord(group, "music.mp3")
        }
      })
    } catch {
      case e: Exception => ErrorProcess.logGroup(group, e)
    }
  }
  def searchMusic(group: Long, musicName: String, page: Int): Unit = {
    try {
      ThreadUtil.execute(new Runnable() {
        override def run(): Unit = {
          //val response = HttpUtil
          //  .createGet(s"${neteaseCloudMusicApi}/search?keywords=$musicName?limit=${3 * page}", true)
          //  .execute(true)
          //ThreadUtil.safeSleep(1000)
          //val json = response.body()
          //val result: SearchResponse = Main.json.fromJson[SearchResponse](json, classOf[SearchResponse])
          val result: SearchResponse = Main.json.fromJson[SearchResponse](
            HttpUtil.get("http://music.163.com/api/search/get/web", new util.HashMap[String, AnyRef]() {
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
          sb.append("\n名称 - 作者 - 歌曲id")
          for (i <- (page - 1) * 3 until page * 3) {
            val song = result.result.songs.get(i)
            sb.append(s"\n${song.name} - ")
            song.artists.forEach(artist => sb.append(artist.name).append(" "))
            sb.append(s"- ${song.id}")
          }
          Main.oneBot.sendGroup(group, sb.toString)
        }
      })
    } catch {
      case e: Exception => ErrorProcess.logGroup(group, e)
    }
  }
}
