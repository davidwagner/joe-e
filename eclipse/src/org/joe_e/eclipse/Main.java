// Copyright 2008 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Akshay Krishnamurthy
 * @author Kanav Arora
 * @author Adrian Mettler
 */

package org.joe_e.eclipse;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.preference.IPreferenceStore;

import static java.io.File.separator;
import static java.io.File.pathSeparator;

/**
 * Main class of the Joe-E command line verifier tool.
 * When run, the specified project is verified using the standard Joe-E verifier
 * and any java or Joe-E warnings and errors are displayed on the command line.
 * Successful verifications return 0 and unsuccessful verifications return 1.
 * 
 * @author Akshay Krishnamurthy
 * @author Kanav Arora
 */
public class Main implements IApplication {

	String projectRoot = ".";		// the root directory name of the project
	                                //   we're verifying
	boolean markAsJoeE = false; 	// treat all packages as Joe-E
	boolean failIfNotJoeE = false; 	// fail if any package isn't marked as Joe-E
	boolean build = true;			// should we build or not? changed to
	                                //   false if any package isn't marked as
	                                //   joe-e and fail is set
	boolean debug = false;
	boolean help = false;			// if true then print usageString and exit.
	String tamingPath = null;				// where is the taming database
	String[] classPathEntries = null;   // location of the joe-e library and
	                                    //   other external classes
	static final String usageString = 
	    "Command-line options for the Joe-E verifier:\n" +
	    " -source PATH    The path to the source classes to be verified.\n" +
	    "                 Defaults to the current directory.\n" +
	    " -taming PATH    The location of the taming database.  Required.\n" +
	    " -classpath PATH_OR_JAR[" + pathSeparator + "PATH_OR_JAR]...\n" +
	    "                 The compilation classpath, which must include\n" + 
	    "                 the Joe-E library.  Required.\n" +
	    " -markasjoee     Mark all packages as Joe-E packages.\n" +
	    " -fail           Fail if any package isn't Joe-E.\n" +
	    " -verbose        Enable additional debugging output.\n\n" +
	    "The options -taming and -classpath may be set in the wrapper " +
	    "script verify.sh"; 

	/**
	 * Main method of the command-line Joe-E verifier
	 */
	public Object start(IApplicationContext context) throws Exception {

		// Get command line args from the ApplicationContext
		String[] args = 
		    (String[]) context.getArguments().get("application.args");
		
		// parse arguments
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-taming")) {
				if(i+1 < args.length && !(args[i+1].startsWith("-/-"))) {
					File taming = new File(args[i+1]);
					if (taming.exists() && taming.isDirectory()) {
						tamingPath = taming.getAbsolutePath();
						i++;
					} else {
						System.out.println("ERROR: did you specify your taming database correctly?");
					}
				}
			} else if (args[i].equals("-markasjoee")) {
				markAsJoeE = true;
			} else if (args[i].equals("-fail")) {
				failIfNotJoeE = true;
			} else if (args[i].equals("-verbose")) {
                debug = true;
            } else if (args[i].equals("-classpath")) {
				if (i + 1 < args.length && !(args[i + 1].startsWith("-"))) {
					classPathEntries = args[i + 1].split(pathSeparator);
					i++;
				}	
				/*	    
					    File library = new File(args[i+1]);
					String ext = library.toString().substring(library.toString().lastIndexOf('.')+1);
					if (library.exists() && ext.trim().equals("jar")){
						libraryJar = new File(args[i+1]).getAbsolutePath();
						i++;
					} else {
						System.out.println("ERROR: is your library file the correct jar file?");
					}
				}
				*/
			} else if (args[i].equals("-source")) {
				if (i+1 < args.length && !(args[i+1].startsWith("-"))) {
					projectRoot = args[i+1] + 
					    (args[i+1].endsWith(separator) ? "" : separator);
					i++;
				}
			} else {
				// command line args are incorrect so we should print usage string
				help = true;
			}
		}
		
		// Verify args and don't run if required args are not passed in.
		if (tamingPath == null) {
			System.out.println("ERROR: taming database location not specified");
			help = true;
		}
		if (classPathEntries == null) {
			System.out.println("ERROR: the classpath must include at least the Joe-E library");
			help = true;
		}
		
		if (help) {
				// then the user needs some help
			System.out.println (usageString);
			return 1;
		}
		
		// Now we can run.
		System.out.println("Running Verifier on: " + projectRoot); // starting

		if (markAsJoeE && failIfNotJoeE) {
			markAsJoeE = false; // these two options are contradictory, so if both are on turn markAsJoeE off.
		}
		
		File projectRootDir = new File(projectRoot);
		
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			
			// we're going to make a random project in the workspace and copy everything into that.
			IProject proj = root.getProject("verification" + new java.util.Date().getTime());
			if (proj.exists()) {
			    throw new AssertionError("project already exists");
			}
			else {
				proj.create(null); // create the project
			}
			
			if (proj.exists() && !proj.isOpen()) {
				proj.open(null); // open the project
			}
			
			for (File child : projectRootDir.listFiles()) {
				putFolderIntoProject(proj, child); 
				    // recursively copy everything from projectRoot to the project
			}
			
			modifyDotProjectFile(proj); //HACK, assign the appropriate builders to the project
			modifyDotClasspathFile(proj); //HACK, add necessary library files to the project
			
			// Refresh project
			proj.close(null);
			proj.open(null);
			
			IPreferenceStore store = Plugin.getDefault().getPreferenceStore();
			store.putValue(Preferences.P_TAMING_PATH, tamingPath); // update the tamingPath in the PreferenceStore
			store.setValue(Preferences.P_ENABLE_DEBUG, debug);
			
			if (build) {
				// if we haven't had any problems so far, build the project
				proj.build(IncrementalProjectBuilder.FULL_BUILD, null);		
				int errors = Printer.printErrors(proj);		
		        
		        // refresh the project -- why?
	            proj.close(null);
	            proj.open(null);
	            
	            // delete the project, save the workspace and then return.
	            proj.delete(true, true, null);
	            workspace.save(true, null);

                if (errors > 0) {
                    System.out.println("Build terminated with " + errors + " errors");
                    return 1;
                }
                else {
                    System.out.println("Build terminated with no errors");
                    return 0;
                }
            } else {
				// we've had some problems so don't build, delete the project, and return failure.
				System.out.println("Project not built because some packages weren't marked as Joe-E.");
				proj.delete(true, true, null);
				workspace.save(true, null);
				return 2;
			}
					
		} catch (Throwable t) {
		    System.out.println(t);
			t.printStackTrace();
			return 3;
		}	
	}

	public void stop() {
		// do nothing.  cancelling cleanly not supported.
	}
	
	
	/**
	 * Puts a folder into a IProject, recursively adding all of the sub-folders and files
	 * into the project. 
	 * @param p
	 * @param f
	 * @throws IOException
	 * @throws CoreException
	 */
	public void putFolderIntoProject(IProject p, File f) throws IOException, CoreException {
		if (f.isDirectory() && !f.isHidden()) {
			IFolder newfold = p.getFolder(f.getName());
			if (!newfold.exists()) {
				try {
					newfold.create(true, true, null);
				} catch (CoreException e) {
					System.out.println(e);
				}
			}
			
			if (failIfNotJoeE) {
				if (!TogglePackageAction.isJoeE(newfold)) {
					build = false;
					System.out.println(newfold.getName() + " is not marked as joe-e ... failing");
				}
			
			}
			boolean hasPackageInfo = false;
			for (File sub : f.listFiles()) {
				hasPackageInfo = (sub.getName().equals("package-info.java")) ? true : hasPackageInfo;
				putFileIntoFolder(newfold, sub);
			}
			if (!hasPackageInfo && markAsJoeE) {
				addPackageInfo(newfold, getPackageName(f));
				System.out.println("marking " + f.getName() + " as joe-e");
			}
		} else if (f.isFile() && !f.isHidden()) {
			IFile newfile = p.getFile(f.getName());
			if (!newfile.exists()) {
				try {
					FileInputStream fs = new FileInputStream(f);
					newfile.create(fs, false, null);
				} catch (CoreException e) {
					System.out.println(e);
				} catch (IOException e) {
					System.out.println(e);
				}
			}
			
		}
	}
	
	/**
	 * Recursively puts a File (representing a folder) into an IFolder
	 * Used to set up the new IProject
	 *  
	 * @param IFolder folder
	 * @param File f
	 * @throws IOException
	 * @throws CoreException
	 */
	public void putFolderIntoFolder(IFolder folder, File f) throws IOException, CoreException {
		if (f.isDirectory() && !f.isHidden()) {
			IFolder newfold = folder.getFolder(f.getName());
			if(!newfold.exists()) {
				try {
					newfold.create(true, true, null);
				} catch (CoreException e) {
				}
			}
			if (failIfNotJoeE) {
				if (!TogglePackageAction.isJoeE(newfold)) {
					build = false;
					System.out.println(newfold.getName() + " is not marked as joe-e ... failing");
				}
			}
			boolean hasPackageInfo = false;
			for (File sub : f.listFiles()) {
				hasPackageInfo |= (sub.getName().equals("package-info.java"));
				putFileIntoFolder(newfold, sub);
			}
			if (!hasPackageInfo && markAsJoeE) {
				addPackageInfo(newfold, getPackageName(f));
				System.out.println("marking " + f.getName() + " as joe-e");
			}
		}
		else if (f.isFile() && !f.isHidden()) {
			putFileIntoFolder(folder, f);
		}
	}
	
	/**
	 * Recursively puts this file into the folder
	 * Used to set up the IProject
	 * 
	 * @param IFolder folder
	 * @param File f
	 * @throws IOException
	 * @throws CoreException
	 */
	public void putFileIntoFolder(IFolder folder, File f) throws IOException, CoreException {
		if (f.isDirectory() && !f.isHidden()) {
			putFolderIntoFolder(folder, f);
		}
		else {
			IFile tempfile = folder.getFile(f.getName());
		
			try {
				FileInputStream fs = new FileInputStream(f);
				tempfile.create(fs, false, null);
			} catch (CoreException c) {
			} catch (IOException i) {
			}
		}
	}
	
	/**
	 * returns the package name of the given directory, does this by
	 * parsing the string of the directories name
	 * 
	 * @param f
	 * @return
	 */
	private String getPackageName(File f) {
		String[] segments = f.toString().split(File.separator);
		String sofar = "";
		for (int i = 0; i < segments.length; i++) {
			sofar += segments[i] + File.separator;
			if (sofar.equals(projectRoot)) {
				String toReturn = "";
				for (int j = i+1; j < segments.length - 1; j++) {
					toReturn += segments[j] + ".";
				}
				toReturn += segments[segments.length-1];
				return toReturn;
			}
		}
		return null;
	}
		
	/**
	 * adds a package-info.java file to the given folder. The file has only the
	 * Joe-E annotation.
	 * 
	 * @param f
	 * @param packagename
	 * @throws IOException
	 * @throws CoreException
	 */
	public void addPackageInfo(IFolder f, String packagename) 
	                                    throws IOException, CoreException {
		String contents = "@org.joe_e.IsJoeE package " + packagename + ";";
		IFile newFile = f.getFile("package-info.java");
		newFile.create(new ByteArrayInputStream(contents.getBytes()), true, null);
	}
	
	/**
	 * Adds the Java and Joe-E builders and natures to the specified
	 * project's <code>.project</code> file.
	 * 
	 * @param project
	 * @throws IOException
	 */
	public void modifyDotProjectFile(IProject project) throws IOException {
		String contents =
		    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		    "<projectDescription>\n" + 
		    "\t<name>" + project.getName() + "</name>\n" +
		    "\t<comment></comment>\n" + 
		    "\t<projects></projects>\n" +
		    "\t<buildSpec>\n" +
		    "\t\t<buildCommand>\n" +
		    "\t\t\t<name>org.eclipse.jdt.core.javabuilder</name>\n" +
		    "\t\t\t<arguments></arguments>\n" + 
		    "\t\t</buildCommand>\n" +
		    "\t\t<buildCommand>\n" + 
		    "\t\t\t<name>org.joe_e.JoeEBuilder</name>\n" +
		    "\t\t\t<arguments></arguments>\n" +
		    "\t\t</buildCommand>\n" +
		    "\t</buildSpec>\n" +
		    "\t<natures>\n" + 
		    "\t\t<nature>org.eclipse.jdt.core.javanature</nature>\n" +
		    "\t\t<nature>org.joe_e.JoeENature</nature>\n" +
		    "\t</natures>\n" +
		    "</projectDescription>";
		IPath location = project.getLocation();
		BufferedWriter out = 
		    new BufferedWriter(new FileWriter(location.toOSString()
		                                      + separator +  ".project"));
		out.write(contents);
		out.close();
	}

	/**
	 * Adds the container for the runtime JRE and entries specified with
	 * <code>-classpath</code> to the Eclipse <code>.classpath</code> file
	 * of the specified project
	 * 
	 * @param proj
	 * @throws IOException
	 */
	public void modifyDotClasspathFile(IProject proj) throws IOException {
		String contents = 
		    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		    "<classpath>\n" +
		    "\t<classpathentry kind=\"src\" path=\"\"/>\n" +
		    "\t<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER\"/>\n" +
		    "\t<classpathentry kind=\"output\" path=\"\"/>\n";
		for (String entry : classPathEntries) {
		    contents += "<classpathentry kind=\"lib\" path=\"" 
		        + new File(entry).getAbsolutePath() + "\"/>\n";
		}
		contents += "</classpath>\n";
		
		IPath location = proj.getLocation();
		location = proj.getLocation();
		BufferedWriter out = new BufferedWriter(new FileWriter(location.toOSString() + "/.classpath"));
		out.write(contents);
		out.close();
	}
}
