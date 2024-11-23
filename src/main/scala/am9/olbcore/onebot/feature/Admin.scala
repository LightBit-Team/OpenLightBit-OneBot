package am9.olbcore.onebot.feature

import am9.olbcore.onebot.Main
import am9.olbcore.onebot.platform.onebot.OneBot

import java.io.File

object Admin {
  def isOwner(user: Long): Boolean = {
    val owner = java.lang.Long.parseLong(Main.config.getData.get("owner").asInstanceOf[String])
    user == owner
  }
  def isAdmin(user: Long): Boolean = {
    val adminMap = Main.adminData.getData
    val admins = adminMap.get("admin").asInstanceOf[java.util.List[Long]]
    admins.contains(user)
  }
  def isDisabled(groupId: Long): Boolean = {
    Main.adminData.getData.get("disabled_group").asInstanceOf[java.util.List[Long]].contains(groupId)
  }
  def op(userId: Long, executeGroup: Long, executor: Long): Unit = {
    try {
      if (isOwner(executor)) {
        val adminMap = Main.adminData.getData
        val admins = adminMap.get("admin").asInstanceOf[java.util.List[Long]]
        if (admins.contains(userId)) {
          Main.oneBot.sendGroup(executeGroup, "已经是管理")
        } else {
          admins.add(userId)
          adminMap.replace("admin", admins)
          Main.adminData.setData(adminMap)
          Main.adminData.write(new File("admin.json"))
          Main.oneBot.sendGroup(executeGroup, "成功执行")
        }
      } else {
        Main.oneBot.sendGroup(executeGroup, "权限不足")
      }
    } catch {
      case e: Exception =>
        ErrorProcess.logGroup(executeGroup, e)
    }
  }
  def deop(userId: Long, executeGroup: Long, executor: Long): Unit = {
    try {
      if (isOwner(executor)) {
        val adminMap = Main.adminData.getData
        val admins = adminMap.get("admin").asInstanceOf[java.util.List[Long]]
        if (admins.contains(userId)) {
          admins.remove(userId)
          adminMap.replace("admin", admins)
          Main.adminData.setData(adminMap)
          Main.adminData.write(new File("admin.json"))
          Main.oneBot.sendGroup(executeGroup, "成功执行")
        } else {
          Main.oneBot.sendGroup(executeGroup, "不是管理")
        }
      } else {
        Main.oneBot.sendGroup(executeGroup, "权限不足")
      }
    } catch {
      case e: Exception =>
        ErrorProcess.logGroup(executeGroup, e)
    }
  }
  def disable(groupId: Long, executor: Long): Unit = {
    try {
      if (isOwner(executor)) {
        val adminMap = Main.adminData.getData
        var disabledGroups = adminMap.get("disabled_group").asInstanceOf[java.util.List[Long]]
        //if (disabledGroups == null) {
        //  disabledGroups = new java.util.ArrayList[Long]()
        //}
        if (disabledGroups.contains(groupId)) {
          Main.oneBot.sendGroup(groupId, "已经是禁用状态")
        } else {
          disabledGroups.add(groupId)
          adminMap.replace("disabled_group", disabledGroups)
          Main.adminData.setData(adminMap)
          Main.adminData.write(new File("admin.json"))
          Main.oneBot.sendGroup(groupId, "成功执行")
        }
      } else {
        Main.oneBot.sendGroup(groupId, "权限不足")
      }
    } catch {
      case e: Exception =>
        ErrorProcess.logGroup(groupId, e)
    }
  }
  def enable(groupId: Long, executor: Long): Unit = {
    try {
      if (isOwner(executor)) {
        val adminMap = Main.adminData.getData
        val disabledGroups = adminMap.get("disabled_group").asInstanceOf[java.util.List[Long]]
        if (disabledGroups.contains(groupId)) {
          disabledGroups.remove(groupId)
          adminMap.replace("disabled_group", disabledGroups)
          Main.adminData.setData(adminMap)
          Main.adminData.write(new File("admin.json"))
          Main.oneBot.sendGroup(groupId, "成功执行")
        } else {
          Main.oneBot.sendGroup(groupId, "不是禁用状态")
        }
      } else {
        Main.oneBot.sendGroup(groupId, "权限不足")
      }
   } catch {
      case e: Exception =>
        ErrorProcess.logGroup(groupId, e)
    }
 }
}
