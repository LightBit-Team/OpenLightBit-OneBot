package am9.olbcore.onebot.misc

class Account(number: Long) {
  //val number: Long = number
  def getNumber: Long = number

  override def toString: String = getNumber.toString
  override def equals(obj: Any): Boolean = this.toString == obj.toString
}
