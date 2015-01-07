Summary
-------
This Java library includes utilities that are based on the probabilistic data structures and primarily designed for multi-threaded environment. Particularly, it provides the following:
- Linear counters for counting cardinality:
  - _WriteConcurrentLinearCounter_ - a counter that is lock-free for counting values but requires exclusive access for reading
  - _AggregateLinearCounter_ - a counter for aggregating results from other linear counters
- _CardinalityMeter_ - a lock-free metric that provides cardinality of the observed values over a period of time.

Requirements
------------
The library requires Java 1.7 and higher.
Documentation
-------------
API documentation is available [here](http://yshahun.github.io/probabilistic-util/apidocs/index.html).
Installation
------------
Run Maven command to build a JAR file:

`mvn clean install`
License
-------
Licensed under the Apache License 2.0.
