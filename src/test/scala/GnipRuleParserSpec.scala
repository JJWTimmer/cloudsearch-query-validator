
import org.scalatest._

class GnipRuleParserSpec extends WordSpec with MustMatchers with TryValues {

  "Gnip rule parser" should {
    "accept single character" in {
      GnipRuleParser("h").success
    }
    "accept single word" in {
      GnipRuleParser("hello").success
    }
    "accept special characters in word" in {
      GnipRuleParser("hello!%&\'*+-./;<=>?,#@world").success
    }
    "accept single hashtag" in {
      GnipRuleParser("#yolo").success
    }
    "not accept special characters in beginning of word" in {
      GnipRuleParser("!hello").failure
    }
    "accept multiple words" in {
      GnipRuleParser("hello? beautiful world!").success
    }
    "accept multiple words with negation" in {
      GnipRuleParser("hello? -beautiful world! -foo -bar bla -lol").success
    }
    "accept quoted word" in {
      GnipRuleParser("\"hello\"").success
    }
    "accept quoted words" in {
      GnipRuleParser("\"hello world!\"").success
    }
    "accept quoted negated words" in {
      GnipRuleParser("\"hello -world!\"").success
    }
    "accept all combinations of optional negation and quoted words" in {
      GnipRuleParser("\"hello world?\" bla -bla \"lol!\" bla").success
    }
    "not accept single negated word" in {
      GnipRuleParser("-hello").failure
    }
    "not accept only negated words" in {
      GnipRuleParser("-hello -world").failure
    }
    "not accept unfinished quotes" in {
      GnipRuleParser("\"-hello world\" bla \"lol bla bla").failure
    }
    "not accept empty string" in {
      GnipRuleParser("").failure
    }
    "not accept single stop word" in {
      GnipRuleParser("the").failure
    }
    "not accept single stop word 2" in {
      GnipRuleParser("at").failure
    }
    "accept stop word combined with non stop word" in {
      GnipRuleParser("the boat").success
    }
    "not accept only stop words" in {
      GnipRuleParser("a an and at but by com from http https if in is it its me my or rt the this to too via we www you").failure
    }
    "accept group" in {
      GnipRuleParser("(the boat)").success
    }
    "accept groups" in {
      GnipRuleParser("(the boat) (the other boat)").success
    }
    "accept nested groups" in {
      GnipRuleParser("((bla bla))").success
    }
    "accept nested groups with terms before" in {
      GnipRuleParser("(the boat (bla bla))").success
    }
    "accept nested groups with terms after" in {
      GnipRuleParser("((bla bla) lol lol)").success
    }
    "accept nested groups with terms before AND after" in {
      GnipRuleParser("(lol lol (\"bla\" bla) -lol lol)").success
    }
    "accept groups combined with non-groups" in {
      GnipRuleParser("the boat (bla bla)").success
    }
    "accept negated groups" in {
      GnipRuleParser("the boat -(bla bla)").success
    }
    "accept quoted keywords in groups" in {
      GnipRuleParser("(\"bla\" \"bla\")").success
    }
    "not accept unclosed groups" in {
      GnipRuleParser("(hello (world) bla").failed
    }
    "accept single powertrack operator" in {
      GnipRuleParser("lang:en").success
    }
    "accept proximity operator" in {
      GnipRuleParser("\"happy birthday\"~3").success
    }
    "accept powertrack operator with terms before" in {
      GnipRuleParser("bla lang:en").success
    }
    "accept powertrack operator with terms in parentheses before" in {
      GnipRuleParser("(bla bla) lang:en").success
    }
    "accept negated powertrack operator" in {
      GnipRuleParser("-lang:en").success
    }
    "accept multiple powertrack operators" in {
      GnipRuleParser("lang:en has:links from:8744 contains:help url_contains:foo").success
    }

    //    "accept full syntax" in {
    //      GnipRuleParser("(gnip OR from:688583 OR @gnip) (\"powertrack operators\" OR \"streaming code\"~4) contains:help bio_contains:developer has:links url_contains:github source:web (friends_count:1 OR followers_count:2000 OR listed_count:500 OR statuses_count:1000 OR is:verified OR klout_score:50) (country_code:US OR bio_location:CO OR bio_location_contains:Boulder OR time_zone:\"Mountain Time (US & Canada)\") -is_retweet (lang:en OR twitter_lang:en)").success
    //    }
  }

}
