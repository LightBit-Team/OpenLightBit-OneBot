package am9.olbcore.onebot.feature

import am9.olbcore.onebot.Main
import am9.olbcore.onebot.media.MediaServer
import org.jetbrains.annotations.NotNull

import java.io.File

object GetMusic {
  def getMusic(group: Long, @NotNull musicId: Long): Unit = {
    Main.mediaServer.addRemoteFile(s"http://music.163.com/song/media/outer/url?id=$musicId.mp3", new File("temp/music.mp3")) 
    Main.oneBot.sendGroupRecord(group, "music.mp3")
  }
}
