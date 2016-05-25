package com.github.jjwtimmer.cloudsearch.validation

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

  private val digit = P(CharIn('0' to '9') !)
  private val wordChar = P(CharIn('a' to 'z', 'A' to 'Z', '0' to '9'))
  private val nameChar = P(wordChar | "_")
  private val specialChar = P(!"'" ~ CharIn('!' to '/', ':' to '@', '[' to '`', '{' to '~'))
  private val numeric = P((digit ++) ~ (("." ~ (digit ++)) ?))

  private val name = P(nameChar++)

  private val string = P("'" ~ (wordChar | specialChar).rep(1) ~ "'")
  private val year = P(("1" | "2") ~ digit.rep(min = 3, max = 3))
  private val month = P(CharIn("0", "1") ~ digit)
  private val day = P(CharIn('0' to '3') ~ digit)
  private val hour = P(CharIn('0' to '2') ~ digit)
  private val minute = P(CharIn('0' to '5') ~ digit)
  private val second = P(CharIn('0' to '5') ~ digit)
  private val date = P("'" ~ year ~ "-" ~ month ~ "-" ~ day ~ "T" ~ hour ~ ":" ~ minute ~ ":" ~ second ~ ("." ~ digit.rep(min = 3, max = 3)).? ~ "Z'")
  private val range = P(("[" | "{") ~ (numeric | date).? ~ "," ~ (numeric | date).? ~ ("}" | "]"))
  private val point = P("'" ~ "-".? ~ numeric ~ "," ~ "-".? ~ numeric ~ "'")
  private val boundingBox = P("[" ~ point ~ "," ~ point ~ "]")

  private val value = P(numeric | range | date | boundingBox)

  private val boostOpt = P("boost=" ~ numeric)
  private val distanceOpt = P("distance=" ~ digit++)
  private val fieldOpt = P("field=" ~ name)

  private val field = P((name ~ ":" ~ (string | value)) | ("(" ~ "field" ~ name ~ (string | value) ~ ")"))
  private val andOp = P("(" ~ "and" ~ boostOpt.? ~ queryOp.rep(1) ~ ")")
  private val orOp = P("(" ~ "or" ~ boostOpt.? ~ queryOp.rep(1) ~ ")")
  private val notOp = P("(" ~ "not" ~ boostOpt.? ~ queryOp ~ ")")
  private val nearOp = P("(" ~ "near" ~ (boostOpt | distanceOpt | fieldOpt).rep(min = 0, max = 3) ~ string ~ ")")
  private val phraseOp = P("(" ~ "phrase" ~ (boostOpt | fieldOpt).rep ~ string ~ ")")
  private val prefixOp = P("(" ~ "prefix" ~ (boostOpt | fieldOpt).rep ~ string ~ ")")
  private val rangeOp = P("(" ~ "range" ~ (boostOpt | fieldOpt).rep ~ range ~ ")")
  private val termOp = P("(" ~ "term" ~ (boostOpt | fieldOpt).rep ~ (string | value) ~ ")")

  private val matchallOp = P("matchall")

  private def queryOp: Parser[Any] = P(field | andOp | orOp | notOp | nearOp | phraseOp | prefixOp | rangeOp | termOp)

  private def query = P(matchallOp | queryOp)

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
