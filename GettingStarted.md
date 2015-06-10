**See ChangeLog if you have used Joe-E before.**



# How it works #

Joe-E behaves as a source-level code checker: it examines the source tree and checks that the all the security invariants from [the Joe-E specification](http://www.cs.berkeley.edu/~daw/joe-e/spec-20080203.pdf) are respected.

Joe-E consists of the following main parts:
  1. the Joe-E library, to link with your Joe-E application;
  1. the taming database, specifying what Java library methods are safe to call;
  1. the verifier program itself, (currently) implemented as an Eclipse plugin.

The library is an ordinary JAR file.  It provides marker interfaces and utility classes that are useful for Joe-E programs.

The taming database is a collection of files specifying policy on which Java constructors, methods, and fields are allowable for Joe-E code to access.  Since the Java libraries were not designed with capability-security in mind, many of the methods provide ambient authority or ambient nondeterminism.  Without a taming database, you will not be able to access any classes from the Java libraries.  Since java.lang.Object is part of the library, this would be very restrictive indeed.

Note that other than providing some Java code with the Joe-E library, Joe-E does not pre-process the source code or intervene in any way in the compilation process, which is carried out by the Java compiler as usual.  As a consequence, **Joe-E will not prevent you from writing insecure code** unless you take special measures otherwise (such as a unit test that systematically runs the verifier against your codebase).

# Requirements #

Joe-E requires Java 6, as of the latest version of the taming database.

Joe-E also requires [Eclipse](http://www.eclipse.org/), the popular IDE for Java. There is a command-line version of the verifier too, but it (currently) consists of a wrapper around the Eclipse runtime... In other words, **you have to download and install Eclipse, even if you are not going to use it as an IDE for Joe-E development.** Version 3.3 or greater is required for the command line version (i.e., codenames "Europa", "Galileo", "Ganymede", or "Helios"); older versions (3.x) should work with the interactive plugin.

If you don't have Eclipse yet, we recommend [Eclipse Helios IDE for Java Developers](http://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/heliossr2) which is the smallest download with everything you need.

## What to Download ##

All components of Joe-E are now available in a single tarball named something like `commandline-x.y.z.tar.gz`.  Get the most recent one from the [downloads page](http://code.google.com/p/joe-e/downloads/list) and untar it somewhere convenient.

As the name of this file hints, it includes the command-line verifier.   See the CommandLine page for more info on using this version.

# Using Joe-E with Eclipse #

The most comfortable way of using Joe-E is from within Eclipse: Joe-E then behaves as another source of compile-time errors that pop up in the edit window and other navigational tools in Eclipse.

To install Joe-E, you can use the web-based Eclipse Updates magic, or copy the plugin jar from command-line tarball into the appropriate Eclipse directory.  In either case, you will need some other files which are included in the the command-line tarball and can also be downloaded separately.

## Installing from the Update Wizard ##

Open Eclipse and from the menu bar invoke

```
Help -> Install New Software...
```

In the "Work with" box, type "http://eclipse.joe-e.org".

If you do not see the plugin listed, **at the bottom uncheck "Group items by category"**.

Select the Joe-E verifier and install.

Accept the license agreement.  (Believe it or not, it's impossible to
distribute anything via Eclipse Update _without_ a clickthrough agreement; if
you leave it blank, Eclipse refuses to install it.)

Accept the default install location (or change it), and click Finish.

After restarting Eclipse, you should have the plug-in installed.

## Installing manually ##

To manually install the plugin, copy the `org.joe_e_2.x.x.jar` file from the tarball into the `eclipse/dropins` directory.  (Eclipse 3.3 "Europa" and earlier don't have a `dropins` directory, use `eclipse/plugins` instead.)

## Configuration ##

Before enabling the plug-in for a project, the project must have the
**Joe-E library** on the classpath. This is necessary both for running the verifier from
Eclipse, and for linking and running the project's code itself.  Add
library-2.x.x.jar as an External JAR in the project's classpath.  This
is set in the project's Properties window (obtained by right-clicking
and selecting Properties).  Select "Java Build Path" on the left, the
"Libraries" tab at the top, and then "Add External JARs..." on the
right.

Before using the plugin, you must also tell it the location of the
**taming database**.  Choose "Window -> Preferences" ("Eclipse ->
Preferences" on a Mac) from the menu bar, select the Joe-E pane, and
select the directory where you placed the taming files.  This is a
global setting that doesn't have to be repeated for each project
(unless you are using custom-made taming databases, as explained below).

## Using the Verifier ##

To **enable Joe-E for a project**, right-click on it and select
"Enable Joe-E Verifier".  This is a toggle option, and is checked
when the verifier is enabled.  Enabling the verifier should trigger a full run
of the verifier.  The verifier is configured as a builder in Eclipse, so it
will be run the same way as you run the Java compiler in Eclipse.  If
Auto-Build is on (the default), it will run automatically on a file when it is
modified and saved, or when changes to other files necessitate re-verification.

Once the verifier is enabled for a project, it will still only verify packages in that project that are marked as being Joe-E code.  Non-Joe-E packages will not be looked at and thus will not generate Joe-E errors.  This allows Joe-E and unrestricted Java code to coexist in the same project.  To **mark a package as Joe-E so it will be verified**, annotate it with the `org.joe_e.IsJoeE` annotation type.  This can be done by creating a file named `package-info.java` in the package directory containing the line `@org.joe_e.IsJoeE package package.name;` where `package.name` is the name of the package. Once you've created this file once, you can copy and paste the file to other projects; Eclipse is smart enough to fix the package name.

To **run** a Joe-E program it is necessary to have a copy of the `org.joe_e.taming.Policy` class, which contains the **runtime taming database**, on the classpath.  If you are using the taming database from this site unmodified, you can just download the JAR containing this class, org.joe\_e.taming-200xxxxx.jar; it is also included in the commandline tarball.

If you make any custom taming decisions (e.g. to allow the use of a third-party library by Joe-E code), you can generate the `org.joe_e.taming.Policy` class from the taming database using the interactive verifier.  This is done by
  1. selecting a Joe-E project where you want this class to be generated
  1. right-clicking the project and selecting "Properties"
  1. selecting "Joe-E" on the left
  1. enabling the option "Automatically build Policy class"

## Quirks and Tips ##

Immediately after starting Eclipse, the plug-in may seem dead
until the first build triggers it or one UI action (which is lost) is
dispatched to it.  I think that this is a bug in Eclipse.  Just be aware that
the "Enable Joe-E Verifier" context menu item sometimes does nothing
the first time you select it other than activate itself and make the checkmark
status visible when the menu is subsequently opened.

Also note that when starting up, if the workspace remembers a previous run of
the verifier and is still displaying its results, a new run will not be
triggered until some file in the workspace changes or a "Clean Build" is
triggered.  It's always possible to trigger a rebuild of a particular file
(rather than a whole project) by adding a character and then deleting it,
making Eclipse think that the file has changed.  This should rarely be
necessary, however, as the Joe-E builder tries to automatically re-check files
if a change to some other file could affect the set of errors the verifer will
generate.  Please let me know if there is a case that isn't handled here.

In order to see the Joe-E errors, you may need to change the viewing preferences for the Problems pane.  From the downward-pointing triangle menu, select an appropriate configuration, like "All Errors", or set up a new one by selecting "Configure Contents...".  We find a configuration "All on Selection" to be the most useful.
  1. set Scope to "On selected element and its children"
  1. enable all severities
  1. select all types (or at least include "Joe-E Problem")

In older versions of Eclipse, clicking the icon with three arrows brings up the Filters dialog with similar options.

# Uninstallation #

You can completely uninstall the verifier by running the inverse maneuver that you used to install it: that is from
"Help -> Install New Software...", or by removing the copy of `org.joe_e_2.x.x.jar` file, respectively.