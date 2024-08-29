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
    ThreadUtil.execute(new Runnable() {
      override def run(): Unit = {
        val json = HttpUtil.get(s"${neteaseCloudMusicApi}/song/url", new util.HashMap[String, AnyRef](){
          put("id", musicId.toString)
        })
        val response = Main.json.fromJson[URLResponse](json, classOf[URLResponse])
        if (response.code != 200) {
          Main.oneBot.sendGroup(group, "获取歌曲失败！")
          return
        }
        FileUtil.del("temp/music.mp3")
        Main.mediaServer.addRemoteFile(response.data.get(0).url, new File("temp/music.mp3"))
        Main.oneBot.sendGroupRecord(group, "music.mp3")
      }
    })
  }
  def searchMusic(group: Long, musicName: String, page: Int): Unit = {
    ThreadUtil.execute(new Runnable() {
      override def run(): Unit = {
        val result: SearchResponse = Main.json.fromJson[SearchResponse](
          HttpUtil.get(s"${neteaseCloudMusicApi}/search", new util.HashMap[String, AnyRef]() {
            put("keywords", musicName)
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
  }
}
