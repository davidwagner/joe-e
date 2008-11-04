// Copyright 2005-06 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Akshay Krishnamurthy
 * @author Kanav Arora
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

/**
 * Main class of the Joe-E command line verifier tool.
 * When run, the specified project is verified using the standard Joe-E verifier and
 * any java or Joe-E warnings and errors are displayed on the command line.
 * Successful verifications return 0 and unsuccessful verifications return 1.
 * 
 * @author Akshay Krishnamurthy
 * @author Kanav Arora
 */
public class Main implements IApplication {

	String projectRoot;				// the root directory name of the project we're verifying
	boolean markAsJoeE = false; 	// for debugging, but also a command line option. default false.
	boolean failIfNotJoeE = false; 	// command line option to fail if any package isn't marked as joe-e
	boolean build = true;			// should we build or not? changed to false if any package isn't marked as joe-e and fail is set
	boolean help = false;			// if set to true then print usageString and exit.
	static boolean commandLine = false; // default false. We'll manually set to commandLine if Main is run.
	static boolean errors = false;		// are there errors?
	String tamingPath = "";				// where is the taming database
	String libraryJar = "";				// where is the joe-e library
	static String usageString = "options:\n\t --project x. The path to the Project to be verified. (required)\n\t --taming x. Set the location of the taming database. (specified in shell script)\n\t --library x. Set the location of the Joe-E library. (specified in shell script)\n\t --markasjoee mark all packages as joe-e packages\n\t --fail. Fail if any package isn't joe-e"; 

	/**
	 * Main method of the command line Joe-E verifier
	 * Command line arguments are
	 * input folder - the location of where the project to verify is.
	 * taming database - the location of where the Joe-E taming database is
	 * markasjoe-e - should the verify mark everything in the project as Joe-E (used mostly for debugging)
	 * fail - fail if any packages isn't marked as Joe-E
	 */
	public Object start(IApplicationContext context) throws Exception {

		// Get command line args from the ApplicationContext
		String[] args = (String[]) context.getArguments().get("application.args");
		
		// parse arguments
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("--help")) {
				help = true;
			} else if (args[i].equals("--taming")) {
				if(i+1 < args.length && !(args[i+1].startsWith("--"))) {
					File taming = new File(args[i+1]);
					if (taming.exists() && taming.isDirectory()) {
						tamingPath = taming.getAbsolutePath();
						i++;
					} else {
						System.out.println("ERROR: did you specify your taming database correctly?");
					}
				}
			} else if (args[i].equals("--markasjoee")) {
				markAsJoeE = true;
			} else if (args[i].equals("--fail")) {
				failIfNotJoeE = true;
			} else if (args[i].equals("--library")) {
				if (i+1 < args.length && !(args[i+1].startsWith("--"))) {
					File library = new File(args[i+1]);
					String ext = library.toString().substring(library.toString().lastIndexOf('.')+1, library.toString().length());
					if (library.exists() && ext.trim().equals("jar")){
						libraryJar = new File(args[i+1]).getAbsolutePath();
						i++;
					} else {
						System.out.println("ERROR: is your library file the correct jar file?");
					}
				}
			} else if (args[i].equals("--project")) {
				if (i+1 < args.length && !(args[i+1].startsWith("--"))) {
					projectRoot = args[i+1] + ((args[i+1].charAt(args[i+1].length() - 1) == File.separatorChar) ? "" : File.separatorChar);
					i++;
				}
			} else {
				// command line args are incorrect so we should print usage string
				help = true;
			}
		}
		
		// Verify args and don't run if required args are not passed in.
		if (projectRoot == null) {
			System.out.println("ERROR: no project specified.");
		}
		if (tamingPath == null || tamingPath.equals("")) {
			System.out.println("ERROR: taming database not specified or specified incorrectly. Check the variables in verify.sh");
		}
		if (libraryJar == null || libraryJar.equals("")) {
			System.out.println("ERROR: Joe-E library not specified or specified incorrectly. Check the variables in verify.sh");
		}
		
		if (help || projectRoot == null || 
				projectRoot.equals("") || 
				tamingPath.equals("") || 
				libraryJar.equals("")) {
				// then the user needs some help
			System.out.println (usageString);
			return 1;
		}
		
		// Now we can run.
		System.out.println("Running Verifier on: " + projectRoot); // starting

		if (markAsJoeE && failIfNotJoeE) {
			markAsJoeE = false; // these two options are contradictory, so if both are on turn markAsJoeE off.
		}
		Main.commandLine = true; // we are running the command line version
		
		File f = new File (projectRoot);
		
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			
			// we're going to make a random project in the workspace and move everything into that.
			IProject proj = root.getProject("verification" + Math.random());
			if (proj.exists()) {
				//proj.build(IncrementalProjectBuilder.FULL_BUILD, null);
				// the project shouldn't exist
			}
			else {
				proj.create(null); // create the project
			}
			
			if (proj.exists() && !proj.isOpen()) {
				proj.open(null); // open the project
			}
			
			for (File child : f.listFiles()) {
				putFolderIntoProject(proj, child); // recursively move everything from projectRoot to the project
			}
			
			modifyDotProjectFile(proj); //HACK, assign the appropriate builders to the project
			modifyDotClasspathFile(proj); //HACK, add necessary library files to the project
			
			// Refresh project
			proj.close(null);
			proj.open(null);
			
			IPreferenceStore store = Plugin.getDefault().getPreferenceStore();
			store.putValue(Preferences.P_TAMING_PATH, tamingPath); // update the tamingPath in the PreferenceStore

			if (build) {
				// if we haven't had any problems so far, build the project
				proj.build(IncrementalProjectBuilder.FULL_BUILD, null);
			} else {
				// we've had some problems so don't build, delete the project, and return failure.
				System.out.println("Project not built because some packages weren't marked as joe-e.");
				proj.delete(true, true, null);
				workspace.save(true, null);
				return 1;
			}
			// refresh the project
			proj.close(null);
			proj.open(null);
			
			// delete the project, save the workspace and then return.
			proj.delete(true, true, null);
			workspace.save(true, null);
			
			
		} catch (Throwable t) {
			System.out.println(t);
			t.printStackTrace();
		}
		
		if (Main.errors) {
			System.out.println("Build terminated with " + Printer.totalErrors + " errors");
			return 1;
		}
		else {
			System.out.println("Build terminated with no errors");
			return 0;
		}
	}

	public void stop() {
		// do nothing.
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
				hasPackageInfo = (sub.getName().equals("package-info.java")) ? true : hasPackageInfo;
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
	 * adds a package-info.java file to the given folder. The file has only the Joe-E annotation.
	 * 
	 * @param f
	 * @param packagename
	 * @throws IOException
	 * @throws CoreException
	 */
	public void addPackageInfo(IFolder f, String packagename) throws IOException, CoreException {
		String contents = "@org.joe_e.IsJoeE package " + packagename + ";";
		IFile newFile = f.getFile("package-info.java");
		newFile.create(new ByteArrayInputStream(contents.getBytes()), true, null);
	}
	
	/**
	 * HACK!
	 * adds the java builder and the Joe-E builder to this projects build configuration
	 * 
	 * @param project
	 * @throws IOException
	 */
	public void modifyDotProjectFile(IProject project) throws IOException {
		String contents ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\t<projectDescription>\n\t\t<name>" + project.getName() + "</name><comment></comment><projects></projects><buildSpec><buildCommand>";
		contents += "<name>org.eclipse.jdt.core.javabuilder</name><arguments></arguments></buildCommand>";
		contents += "<buildCommand><name>org.joe_e.JoeEBuilder</name><arguments></arguments></buildCommand>";
		contents += "</buildSpec><natures>";
		contents += "<nature>org.eclipse.jdt.core.javanature</nature>";
		contents += "<nature>org.joe_e.JoeENature</nature>";
		contents += "</natures></projectDescription>";
		IPath location = project.getLocation();
		BufferedWriter out = new BufferedWriter(new FileWriter(location.toOSString() + "/.project"));
		out.write(contents);
		out.close();
	}

	/**
	 * HACK!
	 * adds the Joe-E library jar file, and the runtime JRE_CONTAINER to the projects classpath
	 * 
	 * @param proj
	 * @throws IOException
	 */
	public void modifyDotClasspathFile(IProject proj) throws IOException {
		String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		contents += "<classpath>\n";
		contents += "<classpathentry kind=\"src\" path=\"\"/>\n";
		contents += "<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER\"/>\n";
		contents += "<classpathentry kind=\"output\" path=\"\"/>\n";
		contents += "<classpathentry kind=\"lib\" path=\"" + libraryJar + "\"/>\n";
		contents += "</classpath>\n";
		
		IPath location = proj.getLocation();
		location = proj.getLocation();
		BufferedWriter out = new BufferedWriter(new FileWriter(location.toOSString() + "/.classpath"));
		out.write(contents);
		out.close();
	}
}
