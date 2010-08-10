package net.liftweb.couchdb

import net.liftweb.json.JsonAST._
import net.liftweb.record.Field
import xml.NodeSeq
import net.liftweb.util.Helpers._
import net.liftweb.json.JsonParser
import net.liftweb.common.Box

class JSONBasicField[OwnerType <: JSONRecord[OwnerType]](rec: OwnerType)
  extends Field[JValue, OwnerType] {

  import JSONRecordHelpers._

  def owner = rec

  def toForm = NodeSeq.Empty

  def asXHtml = NodeSeq.Empty

  def asJs = asJValue

  def defaultValue = JNull

  def asJValue = value

  def setFromString(s:String) = setBox(tryo(JsonParser.parse(s)))

  def setFromAny(a:Any) = genericSetFromAny(a) 

  def setFromJValue(jvalue:JValue) = setBox(Box(jvalue))
}