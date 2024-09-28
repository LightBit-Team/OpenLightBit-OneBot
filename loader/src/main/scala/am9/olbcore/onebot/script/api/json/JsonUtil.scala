package am9.olbcore.onebot.script.api.json

import am9.olbcore.onebot.Main
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken

import java.util
import scala.annotation.tailrec

object JsonUtil {
  def objectToJson(obj: AnyRef): String = {
    Main.json.toJson(obj)
  }
  def jsonToMap(json: String): util.Map[String, AnyRef] = {
    jsonToObject[LinkedTreeMap[String, AnyRef]](json)
  }
  def jsonToList[T](json: String): util.List[T] = {
    jsonToObject[util.List[T]](json)
  }
  def jsonToObject[T](json: String): T = {
    Main.json.fromJson(json, new TypeToken[T](){})
  }
}
