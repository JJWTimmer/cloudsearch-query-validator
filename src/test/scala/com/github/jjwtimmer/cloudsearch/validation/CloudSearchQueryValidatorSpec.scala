package com.github.jjwtimmer.cloudsearch.validation

import org.scalatest._

class CloudSearchQueryValidatorSpec extends WordSpec with MustMatchers with TryValues {
  "CloudSearch Query parser" should {
    "accept field query short" in {
      CloudSearchQueryValidator("selftext:'test query'").success
    }
    "accept field query long" in {
      CloudSearchQueryValidator("(field selftext 'test query')").success
    }
    "accept compound AND-query" in {
      CloudSearchQueryValidator("(and (field selftext 'buzz lightyear') (field title 'buzz lightyear'))").success
    }
    "accept matchall-query" in {
      CloudSearchQueryValidator("matchall").success
    }
    "not accept compound matchall-query" in {
      CloudSearchQueryValidator("(and (field selftext 'buzz lightyear') matchall)").failure
    }
    "accept compound OR-query" in {
      CloudSearchQueryValidator("(and (field selftext 'buzz lightyear') (field title 'buzz lightyear'))").success
    }
    "accept compound date range-query" in {
      CloudSearchQueryValidator("(field selftext {,'2016-05-20T13:57:35Z'])").success
    }
    "accept NOT-query" in {
      CloudSearchQueryValidator("(not (field selftext 'buzz lightyear'))").success
    }
    "accept phrase-query" in {
      CloudSearchQueryValidator("(phrase field=selftext boost=10 'some phrase to find')").success
    }
    "accept near-query" in {
      CloudSearchQueryValidator("(near field=selftext boost=10 distance=3 'wordle')").success
    }
    "accept prefix-query" in {
      CloudSearchQueryValidator("(prefix field=selftext 'prefi')").success
    }
    "accept another range-query" in {
      CloudSearchQueryValidator("(range field=numbers [1, 100])").success
    }
    "accept term-query" in {
      CloudSearchQueryValidator("(term field=other_field boost=0.9 500)").success
    }
    "accept reddit timestamp query" in {
      CloudSearchQueryValidator("timestamp:1464003707..1464104707").success
    }
    "accept boundingBox query" in {
      CloudSearchQueryValidator("(field place ['-50.4, 4.56', '45,-4.36'])").success
    }
    "accept this difficult query" in {
      CloudSearchQueryValidator("(and (field selftext 'hue') subreddit:'philips' (not (field author 'osram')) (or timestamp:1464003707..1464104707 (phrase field=title boost=10 'mood lighting')))").success
    }
  }

}
