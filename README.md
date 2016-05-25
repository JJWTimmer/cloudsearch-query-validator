# CloudSearch structured query validator

This is a CloudSearch structured query that parses queries using the [the FastParse library](https://lihaoyi.github.io/fastparse/). It's useful to validate the syntax of Gnip rules before submitting them and applying them to your stream.

## Usage
Add the dependency to your build.sbt
```scala
libraryDependencies += "com.github.jjwtimmer" %% "cloudsearch-query-validator" % "0.1"
```
Use it!
```scala
import com.github.jjwtimmer.cloudsearch.validation.CloudSearchQueryValidator
import scala.util.{Success, Failure}

// successful parse example
CloudSearchQueryValidator("(and (field author 'kafka') title:'I forgot')")

// failed parse example
CloudSearchQueryValidator("a the https")

// pattern matching example
CloudSearchQueryValidator("(not (or(author:'jjwtimmer' author:'jrosenberg'))") match {
  case Success(result) => println(s"Parsed: $result")
  case Failure(error) => throw error
}
```

## Disclaimer
The documented part of the [CloudSearch structured query syntax](http://docs.aws.amazon.com/cloudsearch/latest/developerguide/search-api.html#structured-search-syntax) is now supported:

0. VALUE: either single-quoted string, date, integer, fraction, boundingbox or range
1. fieldname:VALUE
2. (field FIELD VALUE)
3. (and OTHER1 OTHER2 ...)
4. (or OTHER1 OTHER2 ...)
5. (not OTHER)
6. matchall
7. (phrase boost=FRACTION field=FIELD 'string value')
8. (prefix boost=FRACTION field=FIELD 'string value')
9. (range field=FIELD {,'2016-05-23T23:34:33.324Z'])
10. (term field=FIELD 2000)

The implementation is very naieve, no checking if an option is specified multiple times within the expression, for example, or if a date is a valid date.

## Contributing
Pull requests are always welcome

Not sure if that typo is worth a pull request? Found a bug and know how to fix it? Do it! We will appreciate it. Any significant improvement should be documented as a [GitHub issue](https://github.com/jeroenr/gnip-rule-validator/issues) before anybody starts working on it.

I'm always thrilled to receive pull requests and will try to process them quickly. If your pull request is not accepted on the first try, don't get discouraged!
