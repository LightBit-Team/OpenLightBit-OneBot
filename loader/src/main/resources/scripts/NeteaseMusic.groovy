package scripts

import am9.olbcore.onebot.Main
import am9.olbcore.onebot.feature.ErrorProcess
import am9.olbcore.onebot.script.api.ApiGroupMessageEvent
import am9.olbcore.onebot.script.api.async.AsyncUtil
import cn.hutool.core.io.FileUtil
import cn.hutool.http.HttpUtil

@SuppressWarnings("unused")
class Artist {
    Long id
    String name
}

@SuppressWarnings("unused")
class Data {
    Long id
    String url
    Integer fee
}

@SuppressWarnings("unused")
class Result {
    List<Song> songs
    Integer songCount
}

@SuppressWarnings("unused")
class SearchResponse {
    Result result
    Integer code
}

@SuppressWarnings("unused")
class Song {
    Long id
    String name
    List<Artist> artists
}

@SuppressWarnings("unused")
class URLResponse {
    List<Data> data
    Integer code
}

static getMusic(Long group, Long musicId) {
    try {
        AsyncUtil.async {
            final def request = HttpUtil.createGet("http://zm.armoe.cn/song/url?id=$musicId", 
                    true).execute(true)
            Thread.sleep(1000)
            final def response = Main.json().fromJson(request.body(), URLResponse.class)
            if (response.code != 200) {
                ((ApiGroupMessageEvent) apiEvent).reply("获取歌曲失败！")
                return
            }
            if (response.data[0].fee == 1) {
                ((ApiGroupMessageEvent) apiEvent).reply("不支持完整播放收费歌曲！")
                return
            }
            FileUtil.del("temp/music.mp3")
            Main.mediaServer().addRemoteFile(response.data[0].url, new File("temp/music.mp3"))
            Main.oneBot().sendGroupRecord(group, "music.mp3")
        }
    } catch (Exception e) {
        ErrorProcess.logGroup(group, e)
    }
}

static searchMusic(Long group, String keyword, Integer page) {
    try {
        AsyncUtil.async {
            final def searchResponse = Main.json().fromJson(
                    HttpUtil.get("http://music.163.com/api/search/get/web", [
                            "csrf_token": null,
                            "hlpretag": null,
                            "hlposttag": null,
                            "s": keyword,
                            "type": "1",
                            "offset": "0",
                            "total": "true",
                            "limit": (3 * page).toString()
                    ])
                    , SearchResponse.class)
            final def sb = new StringBuilder()
        }
    } catch (Exception e) {
        ErrorProcess.logGroup(group, e)
    }
}
