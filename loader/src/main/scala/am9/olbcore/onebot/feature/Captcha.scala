package am9.olbcore.onebot.feature

import am9.olbcore.onebot.Main
import am9.olbcore.onebot.onebot.Segment
import cn.hutool.captcha.{AbstractCaptcha, CaptchaUtil, ICaptcha}
import cn.hutool.core.io.FileUtil
import cn.hutool.core.util.RandomUtil

import java.util

object Captcha {
  private val captchas = new util.HashMap[Long, ICaptcha]()
  def get(group: Long, qq: Long): Unit = {
    val captcha: AbstractCaptcha = RandomUtil.randomInt(0, 3) match
      case 0 => CaptchaUtil.createLineCaptcha(100, 50)
      case 1 => CaptchaUtil.createCircleCaptcha(100, 50)
      case 2 => CaptchaUtil.createShearCaptcha(100, 50)
    captcha.write(FileUtil.file("temp/captcha.png"))
    captchas.put(qq, captcha)
    val segmentList = util.List.of[Segment](
      new Segment("at", new util.HashMap[String, String](){
        put("qq", qq.toString)
      }),
      new Segment("image", new util.HashMap[String, String](){
        put("file", Main.mediaServer.getFilePath("captcha.png"))
      })
    )
    Main.oneBot.sendGroupWithSegments(group, segmentList)
  }
  def check(group: Long, qq: Long, code: String): Unit = {
    if (captchas.containsKey(qq)) {
      val captcha = captchas.get(qq)
      if (captcha.verify(code)) {
        captchas.remove(qq)
        Main.oneBot.sendGroupWithSegments(group, util.List.of[Segment](
          new Segment("at", new util.HashMap[String, String](){
            put("qq", qq.toString)
          }),
          new Segment("text", new util.HashMap[String, String](){
            put("text", " 验证成功")
          })
        ))
      } else {
        Main.oneBot.sendGroupWithSegments(group, util.List.of[Segment](
          new Segment("at", new util.HashMap[String, String](){
            put("qq", qq.toString)
          }),
          new Segment("text", new util.HashMap[String, String](){
            put("text", " 验证失败")
          })
        ))
      }
    } else {
      Main.oneBot.sendGroup(group, "还没有生成验证码！")
    }
  }
}
