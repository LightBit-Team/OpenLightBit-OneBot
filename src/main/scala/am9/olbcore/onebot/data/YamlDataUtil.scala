package am9.olbcore.onebot.data

import org.dromara.hutool.core.io.file.FileUtil
import org.virtuslab.yaml.*

import java.util
import scala.collection.JavaConverters.collectionAsScalaIterableConverter
import scala.collection.mutable
import scala.jdk.CollectionConverters.MapHasAsScala
import scala.util.control.Breaks.breakable

class YamlDataUtil extends util.AbstractMap[String, util.AbstractMap[String, AnyRef]] {

  override def entrySet(): util.Set[util.Map.Entry[String, util.AbstractMap[String, AnyRef]]] = {
    val ret = new util.HashSet[util.Map.Entry[String, util.AbstractMap[String, AnyRef]]]()
    breakable {
      for (i <- FileUtil.loopFiles("./data/").asScala) {
        if (i.getName.contains(".yaml")) {
          val tableName = i.getName.split(".")(0)
          ret.add(new util.Map.Entry[String, util.AbstractMap[String, AnyRef]](){

            override def getKey: String = tableName

            override def getValue: util.AbstractMap[String, AnyRef] = new util.AbstractMap[String, AnyRef]{

              override def entrySet(): util.Set[util.Map.Entry[String, AnyRef]] = {
                FileUtil.readUtf8String(i).as[Map[String, Any]] match {
                  case Left(error) => throw error
                  case Right(yaml) => 
                    val javaMap = new util.HashMap[String, AnyRef]()
                    for ((k, v) <- yaml) {
                      javaMap.put(k, v.asInstanceOf[AnyRef])
                    }
                    javaMap.entrySet()
                }
              }

              override def put(key: String, value: AnyRef): AnyRef = {
                val map = FileUtil.readUtf8String(i).as[Map[String, Any]] match {
                  case Left(error) => throw error
                  case Right(yaml) => 
                    val map2: mutable.Map[String, AnyRef] = mutable.Map()
                    for ((k, v) <- yaml) {
                      map2.put(k, v.asInstanceOf[AnyRef])
                    }
                    map2
                }
                map.put(key, value)
                FileUtil.writeUtf8String(mapToNoded(map.toMap[String, AnyRef]).asYaml, i)
                value
              }
            }

            override def setValue(v: util.AbstractMap[String, AnyRef]): util.AbstractMap[String, AnyRef] = {
              FileUtil.writeUtf8String(mapToNoded(v.asScala.toMap[String, AnyRef]).asYaml, i)
              v
            }
          })
        }
      }
    }
    ret
  }

  override def put(key: String, value: util.AbstractMap[String, AnyRef]): util.AbstractMap[String, AnyRef] = {
    FileUtil.writeUtf8String(mapToNoded(value.asScala.toMap[String, AnyRef]).asYaml, s"./data/$key.json")
    value
  }
  
  private def mapToNoded(map: Map[String, Any]): Node.MappingNode = {
    val willNoded: mutable.Map[Node, Node] = mutable.Map()
    for (i <- map) {
      willNoded.put(Node.ScalarNode.apply(i._1), i._2 match {
        case i: String => Node.ScalarNode.apply(i)
        case i: Number => Node.ScalarNode.apply(i.toString)
        case i: Boolean => Node.ScalarNode.apply(i.toString)
        case i: Map[_, _] => mapToNoded(i.asInstanceOf[Map[String, Any]])
        case _ => throw new IllegalArgumentException("Unsupported type by scala-yaml")
      })
    }
    Node.MappingNode.apply(mappings = willNoded.toMap[Node, Node])
  }
}
