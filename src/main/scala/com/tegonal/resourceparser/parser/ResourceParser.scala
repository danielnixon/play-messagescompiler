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
package com.tegonal.resourceparser.parser

import sbt.File
import xsbti.{Maybe, Position, Severity}

import scala.util.matching.Regex
import scala.util.parsing.combinator._

class ResourceParser extends JavaTokenParsers {

  override def skipWhitespace = false

  val ARG: Regex = """\{(\d+)(,\s*\w+)?\}""".r

  /**
   * Convenient entry method
   */
  def parse(input: String): ParseResult[ResourceBundle] = parseAll(resourceBundle, input.trim)

  /**
   * The top level entry point
   */
  def resourceBundle: Parser[ResourceBundle] =
    repsep(property | comment, whiteSpace) ^^ (x => ResourceBundle(x))

  /**
   * Comment of the form #comment text
   */
  def comment: Parser[Comment] =
    "#" ~> """([^\n\r]+)""".r ^^ (text => Comment(text))

  /**
   * Property of the form path=value
   */
  def property: Parser[Property] =
    path ~ """\s*=""".r ~ value ^^ { case lhs ~ eq ~ rhs => Property(lhs, rhs) }

  /**
   * Path of the form path.path.path
   */
  def path: Parser[Path] =
    pathElement ~ rep("." ~> pathElement) ^^ { case lhs ~ rhs => Path(lhs :: rhs) }

  /**
   * Path elements must consist of word characters
   */
  def pathElement: Parser[PathElement] =
    """(\w+)""".r ^^ (x => PathElement(x))

  /**
   * Every character is allowed except new lines
   */
  def value: Parser[PropertyValue] =
    """([^\n\r]*)""".r ^^ (x => PropertyValue(x, extractArgs(x)))

  /**
   * Extract args with their index
   */
  private def extractArgs(x: String) =
    (ARG findAllIn x map { case ARG(i, _) => PropertyValueArg(i.toInt) }).toList
}

object ResourceParser {

  /**
   * @param input multi-line string of the resource file
   * @return the resulting AST if the parsing was successful, else None.
   */
  def parse(input: String, srcFile: Option[File]): Either[xsbti.Problem, ResourceBundle] = {
    val parser = new ResourceParser
    val parseResult = parser.parse(input)

    parseResult match {
      case parser.Success(r: ResourceBundle, _) => Right(r)
      case parser.NoSuccess(noSuccessMessage, next) => Left(new xsbti.Problem {
        override def position(): Position = new Position {
          override def pointer(): Maybe[Integer] = Maybe.nothing[Integer]

          override def line(): Maybe[Integer] = Maybe.just(next.pos.line)

          override def sourcePath(): Maybe[String] = Maybe.nothing[String]

          override def offset(): Maybe[Integer] = Maybe.just(next.pos.column)

          override def sourceFile(): Maybe[File] = srcFile match {
            case Some(f) => Maybe.just(f)
            case None => Maybe.nothing[File]
          }

          override def pointerSpace(): Maybe[String] = Maybe.nothing[String]

          override def lineContent(): String = next.pos.longString
        }

        override def category(): String = ""

        override def severity(): Severity = Severity.Error

        override def message(): String = noSuccessMessage
      })
    }
  }

}