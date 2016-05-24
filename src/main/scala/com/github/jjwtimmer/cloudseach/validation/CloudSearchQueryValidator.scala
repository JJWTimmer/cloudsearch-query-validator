package com.github.jjwtimmer.cloudseach.validation

import fastparse.WhitespaceApi
import org.slf4j.LoggerFactory

import scala.language.postfixOps

class CloudSearchQueryParser {
  val White = WhitespaceApi.Wrapper {
    import fastparse.all._
    NoTrace(CharIn(Seq(' ', '\t', '\n', '\r', '\f')).rep)
  }

  import White._
  import fastparse.noApi._

  implicit class RichParser[T](p: Parser[T]) {
    def + = p.rep(min = 1)

    def ++ = p.repX(min = 1)

    def * = p.rep

    def ** = p.repX
  }

  private val digit = P(CharIn('0' to '9')!)
  private val wordChar = P(CharIn('a' to 'z', 'A' to 'Z', "_", '0' to '9'))
  private val numeric = P(((digit++) ~ (("." ~ (digit ++))?))!)

  private val name = P(wordChar.rep)

  private val string = P("'" ~ wordChar.rep ~  "'")
  private val year = P(("1"|"2") ~ digit.rep(min=3,max=3))
  private val month = P(CharIn("0", "1") ~ digit)
  private val day = P(CharIn('0' to '3') ~ digit)
  private val hour = P(CharIn('0' to '2') ~ digit)
  private val minute = P(CharIn('0' to '5') ~ digit)
  private val second = P(CharIn('0' to '5') ~ digit)
  private val date = P("'" ~ year ~ "-" ~ month ~ "-" ~ day ~ "T" ~ hour ~ ":" ~ minute ~ ":" ~ second ~ ("." ~ digit.rep(min=3, max=3)).? ~ "Z'")
  private val range = P(("["|"{") ~ (numeric | date).? ~ "," ~ (numeric | date).? ~ ("}"|"]") )

  private val value = P(numeric | range | date)

  private val field = P((name ~ ":" ~ (string | value)) | ("(" ~ "field" ~ name ~ (string | value) ~ ")") )
  private val andOp = P("(" ~ "and" ~ ("boost" ~ "=" ~ digit.rep(1)).? ~ queryOp.rep(1) ~ ")")
  private val orOp = P("(" ~ "or" ~ ("boost" ~ "=" ~ digit.rep(1)).? ~ queryOp.rep(1) ~ ")")
  private val notOp = P("(" ~ "not" ~ ("boost" ~ "=" ~ digit.rep(1)).? ~ queryOp.rep(1) ~ ")")
  private val matchallOp = P("matchall")
  private val nearOp = P("(" ~ "near" ~ ("boost" ~ "=" ~ digit.rep(1)).? ~ queryOp.rep(1) ~ ")")
  private val phraseOp = P("(" ~ "phrase" ~ ("boost" ~ "=" ~ digit.rep(1)).? ~ queryOp.rep(1) ~ ")")
  private val prefixOp = P("(" ~ "prefix" ~ ("boost" ~ "=" ~ digit.rep(1)).? ~ queryOp.rep(1) ~ ")")
  private val rangeOp = P("(" ~ "range" ~ ("boost" ~ "=" ~ digit.rep(1)).? ~ queryOp.rep(1) ~ ")")
  private val termOp = P("(" ~ "term" ~ ("boost" ~ "=" ~ digit.rep(1)).? ~ queryOp.rep(1) ~ ")")

  private def queryOp : Parser[Any] = P(field | andOp | orOp | notOp | nearOp | phraseOp | prefixOp | rangeOp | termOp)
  
  private val query = P(matchallOp | queryOp)

  def parse(rule: String) = P(Start ~ query ~ End).parse(rule)

}

object CloudSearchQueryValidator {

  import fastparse.core.ParseError
  import fastparse.core.Parsed._

  val log = LoggerFactory.getLogger(getClass)

  def apply(rule: String) = new CloudSearchQueryParser().parse(rule) match {
    case Success(matched, index) =>
      log.debug(s"Matched: $matched")
      scala.util.Success(matched)
    case f@Failure(lastParser, index, extra) =>
      val parseError = ParseError(f)
      log.warn(s"Failed to parse rule, expected '$lastParser'. Trace: ${parseError.getMessage}")
      scala.util.Failure(parseError)
  }
}
