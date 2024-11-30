package am9.olbcore.onebot.data

import org.dromara.hutool.core.io.file
import org.dromara.hutool.core.io.file.FileUtil
import org.dromara.hutool.core.xml
import org.dromara.hutool.core.xml.XmlUtil
import org.jetbrains.annotations.NotNull

import java.util
import javax.xml.parsers.DocumentBuilderFactory
import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.collection.mutable
import scala.jdk.CollectionConverters.{MapHasAsScala, MutableMapHasAsJava}
import scala.util.control.Breaks.breakable

class XmlDataUtil extends util.AbstractMap[String, util.AbstractMap[String, AnyRef]] {

  override def entrySet(): util.Set[util.Map.Entry[String, util.AbstractMap[String, AnyRef]]] = {
    val ret = new util.HashSet[util.Map.Entry[String, util.AbstractMap[String, AnyRef]]]()
    breakable {
      for (i <- FileUtil.loopFiles("./data/").asScala) {
        if (i.getName.contains(".xml")) {
          val tableName = i.getName.split(".")(0)
          ret.add(new util.Map.Entry[String, util.AbstractMap[String, AnyRef]](){

            override def getKey: String = tableName

            override def getValue: util.AbstractMap[String, AnyRef] = new util.AbstractMap[String, AnyRef]{

              override def entrySet(): util.Set[util.Map.Entry[String, AnyRef]] = xml2Map(FileUtil.readUtf8String(i)).asJava.entrySet()

              override def put(key: String, value: AnyRef): AnyRef = {
                val map = xml2Map(FileUtil.readUtf8String(i))
                map.put(key, value)
                FileUtil.writeUtf8String(map2Xml(map.toMap[String, Any]), i)
                value
              }
            }

            override def setValue(v: util.AbstractMap[String, AnyRef]): util.AbstractMap[String, AnyRef] = {
              FileUtil.writeUtf8String(map2Xml(v.asScala.toMap[String, Any]), i)
              v
            }
          })
        }
      }
    }
    ret
  }

  override def put(key: String, value: util.AbstractMap[String, AnyRef]): util.AbstractMap[String, AnyRef] = {
    FileUtil.writeUtf8String(map2Xml(value.asScala.toMap[String, Any]), s"./data/$key.xml")
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
  private def map2Xml(@NotNull map: Map[String, Any]): String = {
    val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument
    val root = document.createElement("xml")
    document.appendChild(root)
    for ((k, v) <- map) {
      val element = document.createElement(k)
      element.appendChild(document.createCDATASection(v match {
        case i: String => i
        case i: Boolean => i.toString
        case i: Number => i.toString
        case _ => throw new IllegalArgumentException("Unsupported type by W3C DOM")
      }))
      root.appendChild(element)
    }
    XmlUtil.toStr(document, true)
  }
}
