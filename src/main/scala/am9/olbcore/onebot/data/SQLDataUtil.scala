package am9.olbcore.onebot.data

import am9.olbcore.onebot.Terminal
import org.dromara.hutool.core.io.file.FileUtil
import org.jetbrains.annotations.Nullable

import java.io.Closeable
import java.sql.{Connection, DriverManager, ResultSet, Statement}
import java.util
import java.util.Map
import java.util.function.BiConsumer
import scala.util.control.Breaks.breakable
import scala.jdk.CollectionConverters.CollectionHasAsScala

//todo
abstract class SQLDataUtil
(driver: String,
 url: String,
 @Nullable user: String,
 @Nullable password: String,
 listTables: String)
  extends util.AbstractMap[String, util.AbstractMap[String, AnyRef]] {
  Class.forName(driver)
  private var connection: Option[Connection] = None
    //if (user == null && password == null) DriverManager.getConnection(url) else DriverManager.getConnection(url, user, password)
  override def entrySet(): util.Set[util.Map.Entry[String, util.AbstractMap[String, AnyRef]]] = {
    val ret = new util.HashSet[util.Map.Entry[String, util.AbstractMap[String, AnyRef]]]()
    connection match {
      case Some(conn) => if (conn.isClosed) connection = Some(if (user == null && password == null)
        DriverManager.getConnection(url)
      else DriverManager.getConnection(url, user, password))
      case None => connection = Some(if (user == null && password == null)
        DriverManager.getConnection(url)
      else DriverManager.getConnection(url, user, password))
    }
    use[Connection](connection.get, i => {
      use[Statement](i.createStatement(), j => {
        use[ResultSet](j.executeQuery(listTables), rs => {
          while (rs.next()) {
            val name = rs.getString("TABLE_NAME")
            ret.add(new util.AbstractMap.SimpleEntry[String, util.AbstractMap[String, AnyRef]](
              name, new util.AbstractMap[String, AnyRef]{

                override def entrySet(): util.Set[Map.Entry[String, AnyRef]] = {
                  val ret = new util.HashSet[Map.Entry[String, AnyRef]]()
                  use[ResultSet](j.executeQuery(s"SELECT * FROM $name"), rs2 => {
                    while (rs2.next()) {
                      ret.add(new util.AbstractMap.SimpleEntry[String, AnyRef](rs2.getString("key"), rs2.getObject("value")))
                    }
                  })
                  ret
                }

                override def put(key: String, value: AnyRef): AnyRef = {
                  null
                }
              }))
          }
        })
      })
    })
    //val ret = new util.HashSet[util.Map.Entry[String, util.AbstractMap[String, AnyRef]]]()
    ret
  }

  override def put(key: String, value: util.AbstractMap[String, AnyRef]): util.AbstractMap[String, AnyRef] = {
    //FileUtil.writeUtf8String(Main.json.toJson(value), s"./data/$key.json")
    value
  }

  private def use[T <: AutoCloseable](closeable: T, function: T => Unit): Unit = {
    function.apply(closeable)
    closeable.close()
  }
}

