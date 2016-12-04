scasci
======

[![Build Status](https://secure.travis-ci.org/whily/scasci.png)](http://travis-ci.org/whily/scasci)

Scasci is a self-contained Scala library for scientific
computation. The target is to provide a NumPy/SciPy like Scala library
for both desktop and mobile (Android) platforms. As such, libraries
like breeze is not used, and the wheel is re-invented. Similarly, the
library does not contain visualization tools, which might be a
separate project.

Features to include:

* [Linear algebra](doc/Linear-Algebra.md)
* Artificial intelligence
* Utilities
  * Random number generator (Uniform Int/Double, Gaussian distribution)

For more information about Scasci, please go to
  <https://github.com/whily/scasci>

Wiki pages can be found at
  <https://wiki.github.com/whily/scasci>

Development
-----------

The following tools are needed to build Scasci from source:

* JDK version 6/7/8 from <http://www.java.com> if Java is not available.
  Note that JDK is preinstalled on Mac OS X and available via package manager
  on many Linux systems.
* Scala (2.11.8)
* sbt (0.13.13)

The project follows general sbt architecture, therefore normal sbt
commands can be used to build the library: compile, doc, test,
etc. For details, please refer
<http://scala.micronauticsresearch.com/sbt/useful-sbt-commands>.

Currently the library is not published to any public repository
yet. To use this library with your project, you need to download the
source code, and run `sbt publish-local` in your command line. Then,
include following line in your sbt configuration file.

          libraryDependencies += "net.whily" %% "scasci" % "0.0.1-SNAPSHOT"
