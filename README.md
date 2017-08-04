# Theia

[![Build status](https://travis-ci.org/carldata/theia.svg?branch=master)](https://travis-ci.org/carldata/theia)

Application for testing services connected to the Kafka.
Base on tescases defined in testcase folder, this application will put data into the Kafka
and check if the response equals the expected value.

 
## Running test
 
 ```bash
sbt assembly
java -jar target/scala-2.12/theia-assembly-0.1.0.jar 
 ```
 
# Join in!

We are happy to receive bug reports, fixes, documentation enhancements,
and other improvements.

Please report bugs via the
[github issue tracker](http://github.com/carldata/theia/issues).



# Redistributing

Theia source code is distributed under the Apache-2.0 license.

**Contributions**

Unless you explicitly state otherwise, any contribution intentionally submitted
for inclusion in the work by you, as defined in the Apache-2.0 license, shall be
licensed as above, without any additional terms or conditions.
