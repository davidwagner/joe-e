# Introduction #
The Java library defines many static methods that have side
effects on the outside world, as well as many constructors
that create objects permitting similar effects. This is a
major source of ambient authority in Java. For example, File
has a constructor that takes a string and returns an object
representing the file with that name. The resulting object
can be used to read, write, or delete the named file. Absent
explicit access control by the Java security manager or
the operating system, this allows any Java code full control
over the filesystem. In Joe-E, we wish to ensure that
code can only have access to a file if a capability for the file
(or a superdirectory) is within that code’s dynamic scope.
Consequently, we must not allow the aforementioned File
constructor in Joe-E’s global scope.

We define a subset of the Java libraries that includes only
those constructors, methods, and fields that are compatible
with the principle that all privileges must be granted via a
capability. We call this activity taming, because it turns an
unruly class library into a capability-secure subset. The Joe-E
verifier restricts Joe-E programs so they can mention only classes,
constructors, methods, and fields in this tamed subset. If
the source code mentions anything outside of this subset,
the Joe-E verifier ﬂags this as an error.  The taming policy
specifies the subset of classes, constructors, methods, and
fields from the Java libraries that Joe-E programs are permitted
to mention.

# Taming Policy #
The Joe-E taming policy is a whitelist.
By default, everything starts off being disabled for use by Joe-E code.  We only enable methods from the Java library after manual review of their documented behavior.  So far, our effort has focused on basic, common classes, primarily in java.lang and java.util.

For libraries like data structures that don't interact with the outside world, a method can be safely enabled if:
  * it is deterministic, as defined in the [purity paper](http://www.cs.berkeley.edu/~amettler/pure-ccs08.pdf), and
  * its side effects, if any, are limited to only objects reachable from the method's arguments (including the this pointer).

For classes that represent external resources like filesystems or network connections (or Java-specific shared entities like the Java memory manager or other global- or thread-scoped resources), things are trickier. Essentially, any method that has any kind of elevated privilege (access to state or ability to cause side effects not possible otherwise) must require an explicit object reference that acts as a capability to authorize that method's invocation.  For example, it should only be possible to read or write data from a file if one has a java.io.File object designating the file to be accessed; we must make sure the taming policy does not expose any Java library method that violates this security property.

For ease of reference, we maintain [a Javadoc-style API reference for the standard Java library classes, annotated with taming information](http://www.cs.berkeley.edu/~daw/joe-e/api/).  Feel free to refer to that documentation if you find it a convenient way to examine the taming policy for a particular class or method from the Java libraries.

Our approach to taming is based upon [the use of taming in the E programming language](http://www.combex.com/papers/darpa-review/security-review.html#taming), and we adopt the same [criteria E uses for taming](http://www.erights.org/elib/legacy/taming.html).  However, our taming decisions were made independently from E; we borrow concepts, but not E's taming database.

# Taming Implementation #
Taming decisions are contained in the taming database.  The taming database consists of a hierarchy of directories representing packages in the Java libraries.  Each package's directory contains many safej files: one safej file for each class in the package that has an entry in the taming database.  For example, the taming policy for `java.io.File` can be found in `safej/java/io/File.safej`.

Each safej file records several kinds of information:
  * The static fields, static methods, and constructors that Joe-E code is allowed to mention.  These are listed in the `static(...)` portion of the safej file.
  * The instance fields and instance methods that Joe-E code is allowed to mention. These are listed in the `instance(...)` portion of the safej file.
  * Honorary implementation relationships (if any).  These are listed in the `honorary(...)` portion of the safej file.  For instance, `java.lang.String` is treated by Joe-E as honorarily Powerless and honorarily Selfless; this fact is recorded in `safej/java/lang/String.java` in the `honorary(...)` clause.

The safej files are used by the Joe-E verifier.  They are also used to generate the file `org/joe_e/Policy.java`, which is a runtime copy of the taming database.  The `Policy` class is used for runtime checks of honorary implementation relationships and by Joe-E's wrapped reflection API to verify that reflective invocations abide by the taming restrictions.  The `Policy.java` file can be generated and automatically kept up-to-date with the current content of the safej taming database by creating an Eclipse project with the option "Automatically build Policy class" enabled in it's project-specific Preferences.  When this option is enabled, Eclipse automatically generates a `Policy.java` file from the safej files.  In order to avoid defining the `Policy` class multiple times, only one project in a multi-project application should have this option enabled.

If you configure Eclipse to automatically build `Policy.java` from safej files, you should avoid using the `org.joe_e.taming-200xxxxx.jar` JAR.

## Customizing the taming database ##
Joe-E comes with a taming database that we have prepared, based upon our analysis of a small subset of the Java libraries.  However, our taming database is highly incomplete: it contains taming information for only a small subset of the Java libraries (primarily classes from java.lang and java.util).  Because we use a whitelist policy, Joe-E code won't be able to use any Java library class that is not present in our taming database.  Therefore, if you want to do serious Joe-E programming, you may quickly run into the limitations of our taming database: you may find that you need to extend or customize the taming database to allow Joe-E code to access certain classes or methods that are safe to expose to Joe-E code but that aren't included in the taming database that comes with Joe-E.

How should you customize the taming database?  Very carefully: a bad taming decision could violate the object-capability properties Joe-E ensures and enable a catastrophic security breach.  You can enable additional methods (or honorary implementation relationships) by using a custom version of the taming database, i.e., by using your own set of .safej files.  Safej files follow a simple format based on s-expressions, which can be inferred from the example safej files provided.

If no .safej file exists for a class, that class's name cannot be referenced in any way in a Joe-E verified source file.  If you'd like to make use of the class, you will need to make a .safej file for it.  This can be done manually, but is tedious and error-prone.  A better approach is to automatically generate a skeleton with a default-deny policy for all methods, and then analyze each method and edit the skeleton manually with your taming decision.  The auto-generated skeleton will list all methods, fields, and constructors, but in keeping with a principle of "safe by default", all methods, fields, and constructors will be marked as suppressed in the skeleton safej file.  You will then need to edit the skeleton safej file to allow access to methods that are safe for Joe-E code to call.  It will be your job to manually analyze the methods to determine which ones are safe and consistent with the object-capability security model.  This analysis may require detailed examination of the semantics of the method and the authority that instances of the class are intended to provide.

The Joe-E eclipse plugin has code to automatically generate a skeleton .safej file for a class or for all classes in a package, but this functionality currently lacks a user interface.  If you build the verifier from source, however, you can hack in a call to this code.  To do so, uncomment and appropriately modify the block at line 37 of SafeJImport.java in package org.joe\_e.eclipse.  When the modified version of the Joe-E plugin is run, this will cause new skeleton safej files to be generated the next time you load the verifier for a project.  (If the verifier is already running, you can force a reload by disabling and re-enabling the verifier for a project).

After editing your .safej files, you will need to re-generate and re-compile the `Policy.java` file; see above for how.  Also, if you want to generate Javadoc-style API docs for the Java class libraries, with information about your taming decisions embedded, you can use the [Joe-E Doc](https://code.google.com/p/joe-e-doc/) tool, which is separately available.

If you analyze other Java library classes and extend the taming database based upon your analysis, we encourage you to email the `e-lang` mailing list with a patch.  Send diffs to the safej files.  Make sure you add comments to the safej files justifying the taming decisions, at least for all non-obvious cases.  We suggest you release your patches under the BSD license, so that we may incorporate them into the Joe-E distribution.  If we can find someone to review and confirm your taming decisions, we may incorporate them into the Joe-E distribution so that other users can benefit from the work you have done.