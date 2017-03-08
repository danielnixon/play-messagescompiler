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

import com.tegonal.resourceparser.parser._
import sbt.File

object ResourceToScalaGenerator {

  /**
   * Generate Scala source code from a properties file to enable compile safe keys.
   *
   * @param input the input string of a given resource property file
   * @param packageName desired package name of the generated Scala source file, defaults to `com.tegonal.resourceparser`
   * @param objectName desired object name of the generated Scala `object` holding the implicits.
   * @return generated Scala code.
   */
  def generateSource(input: String, srcFile: Option[File], packageName: String = "com.tegonal.resourceparser", objectName: String = "ResourceBundleImplicits"): Either[xsbti.Problem, String] = {
    ResourceParser.parse(input, srcFile).right map { parsed =>
      s"""${open(packageName, objectName)}
         |${generate(ResourceBundleTree.create(parsed))}
         |$close""".stripMargin
    }
  }

  private def generate(resourceNode: ResourceNode): String = resourceNode match {
    case ResourceNode(Nil, children, false, _) => children.map(generate).mkString("\n")

    case ResourceNode(path, children, isProperty, args) => createNodeCode(path, children, isProperty, args)
  }

  private def createNodeCode(path: Seq[String], children: List[ResourceNode], isProperty: Boolean, args: List[Arg]): String = {

    val caseObject = s"""
       |protected case object ${objectName(path)} extends PathElement("${path.last}")${if (isProperty) " with ResourcePath" else ""} {
       |  ${pathElements(path, isProperty)}
       |
       |  ${childDefs(children)}
       |
       |  ${applyMethod(isProperty, args)}
       |}
       |""".stripMargin

    val topLevelDef = path match {
      case p :: Nil =>
        if (isProperty && children.isEmpty) {
          "def " + escapeReservedWord(p) + argumentList(args) + ": String = " + objectName(p) + parameterList(args)
        } else {
          s"val ${escapeReservedWord(p)} = ${objectName(p)}"
        }
      case _ => ""
    }

    s"""
       |$caseObject
       |$topLevelDef
       |${children.map(generate).mkString}
       |""".stripMargin
  }

  private def pathElements(path: Seq[String], isProperty: Boolean) = {
    if (isProperty)
      "def pathElements: Seq[PathElement] = " + path.zipWithIndex.map { case (p, i) => "__" + (0 to i).toList.map(path(_).capitalize).mkString }.mkString(" :: ") + " :: Nil"
    else
      ""
  }

  private def childDefs(children: List[ResourceNode]) = {
    val defs = children.map { c =>
      if (c.isProperty && c.children.isEmpty) {
        "def " + escapeReservedWord(c.path.last) + argumentList(c.args) + ": String = " + objectName(c.path) + parameterList(c.args)
      } else {
        s"val ${escapeReservedWord(c.path.last)} = ${objectName(c.path)}"
      }
    }

    defs.mkString("\n\n  ")
  }

  private def applyMethod(isProperty: Boolean, args: List[Arg]) = {
    if (isProperty)
      "def apply" + argumentList(args) + ": String = resourceString" + parameterList(args)
    else
      ""
  }

  private def objectName(path: Seq[String]): String = objectName(path.map(_.capitalize).mkString)

  private def objectName(path: String): String = "__" + path.capitalize

  private def argumentList(args: List[Arg], param: String = ": Any", implicits: String = "(implicit provider: MessagesProvider)") =
    s"(${args map (a => s"arg${a.index}$param") mkString (", ")})$implicits"

  private def parameterList(args: List[Arg]) =
    argumentList(args, "", "")

  def open(packageName: String, objectName: String): String = s"""package $packageName
                |
                |import play.api.i18n._
                |import scala.language.implicitConversions
                |
                |object $objectName {
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
                |""".stripMargin

  val close = "}"

  private def escapeReservedWord(word: String) =
    if (reservedWords.contains(word)) s"`$word`" else word

  private val reservedWords = Set(
    "abstract",
    "case",
    "catch",
    "class",
    "def",
    "do",
    "else",
    "extends",
    "false",
    "final",
    "finally",
    "for",
    "forSome",
    "if",
    "implicit",
    "import",
    "lazy",
    "match",
    "new",
    "null",
    "object",
    "override",
    "package",
    "private",
    "protected",
    "requires",
    "return",
    "sealed",
    "super",
    "this",
    "throw",
    "trait",
    "try",
    "true",
    "type",
    "val",
    "var",
    "while",
    "with",
    "yield",
    "_",
    ":",
    "=",
    "=>",
    "<-",
    "<:",
    "<%",
    ">:",
    "#",
    "@",
    "⇒",
    "←")
}