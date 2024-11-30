package am9.olbcore.onebot.data

import org.dromara.hutool.core.io.file.FileUtil
import org.dromara.hutool.core.io.resource.FileResource
import org.dromara.hutool.setting.toml.Toml

import java.nio.charset.StandardCharsets
import java.util
import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.util.control.Breaks.breakable

class TomlDataUtil extends util.AbstractMap[String, util.AbstractMap[String, AnyRef]] {

  override def entrySet(): util.Set[util.Map.Entry[String, util.AbstractMap[String, AnyRef]]] = {
    val ret = new util.HashSet[util.Map.Entry[String, util.AbstractMap[String, AnyRef]]]()
    breakable {
      for (i <- FileUtil.loopFiles("./data/").asScala) {
        if (i.getName.contains(".toml")) {
          val tableName = i.getName.split(".")(0)
          ret.add(new util.Map.Entry[String, util.AbstractMap[String, AnyRef]](){

            override def getKey: String = tableName

            override def getValue: util.AbstractMap[String, AnyRef] = new util.AbstractMap[String, AnyRef]{

              override def entrySet: util.Set[util.Map.Entry[String, AnyRef]] =
                Toml.read(FileResource(i)).entrySet()

              override def put(key: String, value: AnyRef): AnyRef = {
                val map = Toml.read(FileResource(i))
                map.put(key, value)
                Toml.write(map, FileUtil.getWriter(i, StandardCharsets.UTF_8, false))
                value
              }
            }

            override def setValue(v: util.AbstractMap[String, AnyRef]): util.AbstractMap[String, AnyRef] = {
              Toml.write(v, FileUtil.getWriter(i, StandardCharsets.UTF_8, false))
              v
            }
          })
        }
      }
    }
    ret
  }

  override def put(key: String, value: util.AbstractMap[String, AnyRef]): util.AbstractMap[String, AnyRef] = {
    Toml.write(value, FileUtil.getWriter(s"./data/$key.toml", StandardCharsets.UTF_8, false))
    value
  }
}
