### News: Joe-E 2.1 released! ###

Downloadable now from eclipse.joe-e.org; new library and taming database at the right.

Some important notes on the new release:

A package is now declared to be Joe-E code by the **package annotation `@org.joe_e.IsJoeE`.**  Yes, amazingly enough, packages in Java can have annotations; they are specified in a file called `package-info.java` in the package's directory.  The following one-liner declares the package `pkg.name` to be Joe-E:
```
@org.joe_e.IsJoeE package pkg.name;
```

You'll need to do this manually at present, but you can copy the package-info.java file between packages and Eclipse will change the package specified by the file accordingly.

This means that there is no need to generate safej files for Joe-E code (hooray); the runtime allows reflective calls to all methods defined in Joe-E packages.  This change also makes it easier to use Joe-E for applications that span multiple projects, which used to require taming files for Joe-E code, but no longer do.

The second change you should be aware of is that the plug-in ID (and corresponding nature and builder IDs) has changed to be more standard (it's now org.joe\_e).  This doesn't have a direct effect on the UI, but it means that Eclipse thinks that this is a different plugin.  For this reason, I recommend that you **disable the plugin in any existing projects** (uncheck Enable Joe-E verifier) and then uninstall it before installing the new version.
Otherwise, some stuff will remain from the older version cluttering up the project metadata.

Sorry for the hassle; but it seemed to be the right thing to do, and I wanted to do it earlier rather than later.