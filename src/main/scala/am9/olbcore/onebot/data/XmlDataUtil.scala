package am9.olbcore.onebot.data

import am9.olbcore.onebot.Main
import org.dromara.hutool.core.io.file.FileUtil
import org.dromara.hutool.core.xml
import org.dromara.hutool.core.xml.XmlUtil
import org.jetbrains.annotations.NotNull
import org.w3c.dom.NamedNodeMap

import java.nio.charset.StandardCharsets
import java.util
import scala.collection.JavaConverters.collectionAsScalaIterableConverter
import scala.collection.mutable
import scala.util.control.Breaks.breakable

//todo
class XmlDataUtil extends util.AbstractMap[String, util.AbstractMap[String, AnyRef]] {

  override def entrySet(): util.Set[util.Map.Entry[String, util.AbstractMap[String, AnyRef]]] = {
    val ret = new util.HashSet[util.Map.Entry[String, util.AbstractMap[String, AnyRef]]]()
    breakable {
      for (i <- FileUtil.loopFiles("./data/").asScala) {
        if (i.getName.contains(".json")) {
          val tableName = i.getName.split(".")(0)
          ret.add(new util.Map.Entry[String, util.AbstractMap[String, AnyRef]](){

            override def getKey: String = tableName

            override def getValue: util.AbstractMap[String, AnyRef] = new util.AbstractMap[String, AnyRef]{

              override def entrySet(): util.Set[util.Map.Entry[String, AnyRef]] = null
                //Main.json.fromJson(FileUtil.readUtf8String(i), new TypeToken[util.HashMap[String, AnyRef]](){}).entrySet()

              override def put(key: String, value: AnyRef): AnyRef = {
                //val map = Main.json.fromJson(FileUtil.readUtf8String(i), new TypeToken[util.HashMap[String, AnyRef]](){})
                //map.put(key, value)
                //FileUtil.writeUtf8String(Main.json.toJson(map), i)
                value
              }
            }

            override def setValue(v: util.AbstractMap[String, AnyRef]): util.AbstractMap[String, AnyRef] = {
              //FileUtil.writeUtf8String(Main.json.toJson(v), i)
              v
            }
          })
        }
      }
    }
    ret
  }

  override def put(key: String, value: util.AbstractMap[String, AnyRef]): util.AbstractMap[String, AnyRef] = {
    //XmlUtil.toStr(NamedNodeMap, true)
    value
  }

  private def xml2Map(@NotNull xml: String): mutable.Map[String, AnyRef] = {
    val ret: mutable.Map[String, AnyRef] = mutable.Map()
    val element = XmlUtil.parseXml(xml).getDocumentElement
    if (element != null) {
      val children = element.getChildNodes
      for (i <- 0 until children.getLength) {
        val child = children.item(i)
        ret.put(child.getNodeName, child.getTextContent)
      }
    }
    ret
  }
}
