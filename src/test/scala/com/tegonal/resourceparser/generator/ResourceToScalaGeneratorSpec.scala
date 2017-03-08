/*   __                          __                                          *\
*   / /____ ___ ____  ___  ___ _/ /       resourceparser                      *
*  / __/ -_) _ `/ _ \/ _ \/ _ `/ /        contributed by tegonal              *
*  \__/\__/\_, /\___/_//_/\_,_/_/         http://tegonal.com/                 *
*         /___/                                                               *
*                                                                             *
* This program is free software: you can redistribute it and/or modify it     *
* under the terms of the GNU Lesser General Public License as published by    *
* the Free Software Foundation, either version 3 of the License,              *
* or (at your option) any later version.                                      *
*                                                                             *
* This program is distributed in the hope that it will be useful, but         *
* WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY  *
* or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for *
* more details.                                                               *
*                                                                             *
* You should have received a copy of the GNU General Public License along     *
* with this program. If not, see http://www.gnu.org/licenses/                 *
*                                                                             *
\*                                                                           */
package com.tegonal.resourceparser.generator

import org.specs2.mutable._

class ResourceToScalaGeneratorSpec extends Specification {
  val resourceFile = """items.details=Item details
                       |items.list.title=Items
                       |orders.list.title=Orders
                       |
                       |
                       |orders.details.title=Order
                       |""".stripMargin

  val keywordsResourceFile = """type=Type"""

  val resourceFileWithArgs = """home.title=The list {0} contains {1} elements
                               |other=Another argument {0}""".stripMargin

  val expected = """package com.tegonal.resourceparser
                   |
                   |import play.api.i18n._
                   |import scala.language.implicitConversions
                   |
                   |object ResourceBundleImplicits {
                   |
                   |/**
                   |  * Definitions
                   |  */
                   |abstract class PathElement(val identifier: String)
                   |
                   |trait ResourcePath {
                   |  def pathElements: Seq[PathElement]
                   |
                   |  def resourceString(args: Any*)(implicit provider: MessagesProvider) = Messages(pathElements.map(_.identifier).mkString("."), args: _*)
                   |
                   |  def msg(args: Any*)(implicit provider: MessagesProvider) = resourceString(args)
                   |}
                   |
                   |/**
                   |  * implicit conversion from resource path to String
                   |  */
                   |implicit def resourcePath2String(resourcePath: ResourcePath)(implicit provider: MessagesProvider): String =
                   |  resourcePath.resourceString()
                   |
                   |
                   |
                   |
                   |protected case object __Items extends PathElement("items") {
                   |
                   |
                   |  def details()(implicit provider: MessagesProvider): String = __ItemsDetails()
                   |
                   |  val list = __ItemsList
                   |
                   |
                   |}
                   |
                   |val items = __Items
                   |
                   |
                   |protected case object __ItemsDetails extends PathElement("details") with ResourcePath {
                   |  def pathElements: Seq[PathElement] = __Items :: __ItemsDetails :: Nil
                   |
                   |
                   |
                   |  def apply()(implicit provider: MessagesProvider): String = resourceString()
                   |}
                   |
                   |
                   |
                   |
                   |
                   |protected case object __ItemsList extends PathElement("list") {
                   |
                   |
                   |  def title()(implicit provider: MessagesProvider): String = __ItemsListTitle()
                   |
                   |
                   |}
                   |
                   |
                   |
                   |
                   |protected case object __ItemsListTitle extends PathElement("title") with ResourcePath {
                   |  def pathElements: Seq[PathElement] = __Items :: __ItemsList :: __ItemsListTitle :: Nil
                   |
                   |
                   |
                   |  def apply()(implicit provider: MessagesProvider): String = resourceString()
                   |}
                   |
                   |
                   |
                   |
                   |
                   |
                   |
                   |
                   |protected case object __Orders extends PathElement("orders") {
                   |
                   |
                   |  val list = __OrdersList
                   |
                   |  val details = __OrdersDetails
                   |
                   |
                   |}
                   |
                   |val orders = __Orders
                   |
                   |
                   |protected case object __OrdersList extends PathElement("list") {
                   |
                   |
                   |  def title()(implicit provider: MessagesProvider): String = __OrdersListTitle()
                   |
                   |
                   |}
                   |
                   |
                   |
                   |
                   |protected case object __OrdersListTitle extends PathElement("title") with ResourcePath {
                   |  def pathElements: Seq[PathElement] = __Orders :: __OrdersList :: __OrdersListTitle :: Nil
                   |
                   |
                   |
                   |  def apply()(implicit provider: MessagesProvider): String = resourceString()
                   |}
                   |
                   |
                   |
                   |
                   |
                   |
                   |protected case object __OrdersDetails extends PathElement("details") {
                   |
                   |
                   |  def title()(implicit provider: MessagesProvider): String = __OrdersDetailsTitle()
                   |
                   |
                   |}
                   |
                   |
                   |
                   |
                   |protected case object __OrdersDetailsTitle extends PathElement("title") with ResourcePath {
                   |  def pathElements: Seq[PathElement] = __Orders :: __OrdersDetails :: __OrdersDetailsTitle :: Nil
                   |
                   |
                   |
                   |  def apply()(implicit provider: MessagesProvider): String = resourceString()
                   |}
                   |
                   |
                   |
                   |
                   |
                   |
                   |}
                   |""".stripMargin

  val keywordsExpected = """package com.tegonal.resourceparser
                           |
                           |import play.api.i18n._
                           |import scala.language.implicitConversions
                           |
                           |object ResourceBundleImplicits {
                           |
                           |/**
                           |  * Definitions
                           |  */
                           |abstract class PathElement(val identifier: String)
                           |
                           |trait ResourcePath {
                           |  def pathElements: Seq[PathElement]
                           |
                           |  def resourceString(args: Any*)(implicit provider: MessagesProvider) = Messages(pathElements.map(_.identifier).mkString("."), args: _*)
                           |
                           |  def msg(args: Any*)(implicit provider: MessagesProvider) = resourceString(args)
                           |}
                           |
                           |/**
                           |  * implicit conversion from resource path to String
                           |  */
                           |implicit def resourcePath2String(resourcePath: ResourcePath)(implicit provider: MessagesProvider): String =
                           |  resourcePath.resourceString()
                           |
                           |
                           |
                           |
                           |protected case object __Type extends PathElement("type") with ResourcePath {
                           |  def pathElements: Seq[PathElement] = __Type :: Nil
                           |
                           |
                           |
                           |  def apply()(implicit provider: MessagesProvider): String = resourceString()
                           |}
                           |
                           |def `type`()(implicit provider: MessagesProvider): String = __Type()
                           |
                           |
                           |}""".stripMargin

  val argsExpected = """package com.tegonal.resourceparser
                       |
                       |import play.api.i18n._
                       |import scala.language.implicitConversions
                       |
                       |object ResourceBundleImplicits {
                       |
                       |/**
                       |  * Definitions
                       |  */
                       |abstract class PathElement(val identifier: String)
                       |
                       |trait ResourcePath {
                       |  def pathElements: Seq[PathElement]
                       |
                       |  def resourceString(args: Any*)(implicit provider: MessagesProvider) = Messages(pathElements.map(_.identifier).mkString("."), args: _*)
                       |
                       |  def msg(args: Any*)(implicit provider: MessagesProvider) = resourceString(args)
                       |}
                       |
                       |/**
                       |  * implicit conversion from resource path to String
                       |  */
                       |implicit def resourcePath2String(resourcePath: ResourcePath)(implicit provider: MessagesProvider): String =
                       |  resourcePath.resourceString()
                       |
                       |
                       |
                       |
                       |protected case object __Home extends PathElement("home") {
                       |
                       |
                       |  def title(arg0: Any, arg1: Any)(implicit provider: MessagesProvider): String = __HomeTitle(arg0, arg1)
                       |
                       |
                       |}
                       |
                       |val home = __Home
                       |
                       |
                       |protected case object __HomeTitle extends PathElement("title") with ResourcePath {
                       |  def pathElements: Seq[PathElement] = __Home :: __HomeTitle :: Nil
                       |
                       |
                       |
                       |  def apply(arg0: Any, arg1: Any)(implicit provider: MessagesProvider): String = resourceString(arg0, arg1)
                       |}
                       |
                       |
                       |
                       |
                       |
                       |
                       |
                       |protected case object __Other extends PathElement("other") with ResourcePath {
                       |  def pathElements: Seq[PathElement] = __Other :: Nil
                       |
                       |
                       |
                       |  def apply(arg0: Any)(implicit provider: MessagesProvider): String = resourceString(arg0)
                       |}
                       |
                       |def other(arg0: Any)(implicit provider: MessagesProvider): String = __Other(arg0)
                       |
                       |
                       |}""".stripMargin

  "The generator" should {
    "generate Scala source code" in {
      val result = ResourceToScalaGenerator.generateSource(resourceFile, None).right.toOption.get
      result.replaceAll("""[\n|\s]""", "") === expected.replaceAll("""[\n|\s]""", "")
    }

    "generate Scala keyword safe code" in {
      val result = ResourceToScalaGenerator.generateSource(keywordsResourceFile, None).right.toOption.get
      result.replaceAll("""[\n|\s]""", "") === keywordsExpected.replaceAll("""[\n|\s]""", "")
    }

    "generate Scala code with arguments" in {
      val result = ResourceToScalaGenerator.generateSource(resourceFileWithArgs, None).right.toOption.get
      result.replaceAll("""[\n|\s]""", "") === argsExpected.replaceAll("""[\n|\s]""", "")
    }
  }
}

