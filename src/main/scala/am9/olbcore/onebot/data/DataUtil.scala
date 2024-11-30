package am9.olbcore.onebot.data

import am9.olbcore.onebot.Main

import java.util

object DataUtil {
  def getDataUtil: util.AbstractMap[String, util.AbstractMap[String, AnyRef]] = {
    Main.config.getData.get("data-format") match {
      case "json" => new JsonDataUtil()
      case "yaml" => new YamlDataUtil()
      case "xml" => new XmlDataUtil()
      case "toml" => new TomlDataUtil()
      case _ => throw new IllegalArgumentException("Unsupported data format")
    }
  }
}
