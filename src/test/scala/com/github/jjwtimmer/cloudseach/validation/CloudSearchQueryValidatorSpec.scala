package com.github.jjwtimmer.cloudseach.validation

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
  }

}
