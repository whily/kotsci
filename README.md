scasci
======

Scasci is a self-contained Scala library for scientific
computation. The target is to provide a NumPy/SciPy like Scala library
for both desktop and mobile (Android) platforms. As such, libraries
like breeze is not used, and the wheel is re-invented. Similarly, the
library does not contain visualization tools, which might be a
separate project.

Features to include:

* Linear algebra
* Artificial intelligence 

For more information about Scasci, please go to
  <https://github.com/whily/scasci>

Wiki pages can be found at
  <https://wiki.github.com/whily/scasci>

Development
-----------

The following tools are needed to build Scasci from source:

* JDK version 6/7 from <http://www.java.com> if Java is not available. 
  Note that JDK is preinstalled on Mac OS X and available via package manager
  on many Linux systems. 
* Scala (2.10.0)
* sbt (0.12.4)
  
The project follows general sbt architecture, therefore normal sbt
commands can be used to build the library: compile, test, etc.

To use the library, include following line in your sbt configuration
(TODO). 
        
