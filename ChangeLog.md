Note that this change log is incomplete, as it focuses on the changes that impact the install and use of Joe-E only.

# New in version 2.2 #

  * Joe-E is now bundled in a single tarball.  The installation process is thus made easier, see GettingStarted.
  * Eclipse 3.4 "Ganymede" is supported.
  * A command-line version is provided, which is useful for other IDEs than Eclipse (eg Emacs).  You still need a copy of Eclipse to run the verifier though, even in command-line mode!

# New in version 2.1 or before #

  * the Plug-in name has been changed to be more standard -- from "Joe\_E" to "org.joe\_e".  **We recommend uninstalling before upgrading to the new one, otherwise you will have two Joe-E plugins with different names.**  Sorry for the confusion.
  * **Joe-E code is now identified by whether or not the package is annotated with the org.joe\_e.IsJoeE annotation type!**  This can be added to a package by creating a file named package-info.java in the package directory containing the line "@org.joe\_e.IsJoeE package package.name;" where package.name is the name of the package. Once you've created this file once, you can copy and paste the file to other projects; Eclipse is smart enough to fix the package name.  This approach allows Joe-E code to be recognized efficiently at runtime and eliminates the need to add Joe-E code to the taming database if shared between projects.