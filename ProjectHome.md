Joe-E is a subset of the Java programming language designed to support secure programming according to object-capability discipline.  Joe-E is intended to facilitate construction of secure systems, as well as to facilitate security reviews of systems built in Joe-E.

The language guarantees additional security properties by placing restrictions on Java code, but does not modify programs or change their meaning.  This allows programmers' existing knowledge of Java to be applied and existing compilers, debuggers, and other tools to be used with Joe-E programs.


---


The Joe-E Verifier is implemented as an Eclipse plug-in.

It may be installed via Eclipse Update.  The repository location is http://eclipse.joe-e.org.

You will also need the library and taming database, which can be downloaded here (see the links to the right).

More detailed info on [getting started](GettingStarted.md) is available in the wiki.

A [research paper](http://www.cs.berkeley.edu/~daw/papers/joe-e-ndss10.pdf) provides a detailed overview of the Joe-E project and its foundations.  Also available: [further information on Joe-E](FurtherInformation.md), including background about the object-capability approach to security.


---

### Updated library fixes filesystem isolation bug ###
Please update to the latest version of the library, 2.2.3.  The filename validation code in previous versions did not correctly handle Java file name strings that contain null bytes, leading to truncation once the file name is passed to the underlying C library routines.  This allowed the checks to ensure the path traversal directory entry ".." was not used to be circumvented.  The command line package version 2.2.3 also includes this fix.

### Taming database now supports Java 6 ###
A new version of the taming database files have been released for use with Java 6.  A tarball of the new taming database in safej form, as well as a JAR containing the updated version of the runtime policy class are available.

### Updated verifier, library, and taming files released ###
Verifier version 2.2.2 released.  This is a minor change to the previous 2.2.1 bugfix release; it fixes one soundness issue that otherwise would arise with Java 1.6 and the new taming database release.

### About the Taming database JAR ###
This JAR contains the runtime version of the taming database specified in the safej files (taming tarball); it has been generated from them using the interactive Joe-E verifier.
If you customize the taming database, you should generate a custom version of the org.joe\_e.taming.Policy class using the interactive eclipse plugin (see GettingStarted for more info).

The commandline tarballs include the org.joe\_e.taming JAR file.  See more on the [command-line version](CommandLine.md).