// Copyright 2005-06 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e.eclipse;

import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Set;
import java.util.HashSet;

import org.eclipse.jdt.core.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
//import org.eclipse.core.runtime.Path;
//import org.eclipse.core.runtime.QualifiedName;

/**
 * The main class for the Joe-E verifier implementation.  The Eclipse framework
 * creates an instance of this class for each project that has the Joe-E
 * nature.  These objects keep track of the build state, taming state, and
 * verifier object for the current build.  In the case of a clean re-build,
 * these three objects are re-generated from scratch and all files in the
 * project are rebuilt.
 * 
 * This class is responsible for keeping track of the set of resources that
 * need to be verified, and invoking the Verifier on each one.  It also
 * posts the Problems that are encounted during verification to the Eclipse
 * compilation error reporting framework.  All of the rules of the actual
 * verification, and the cases in which changes to one file require
 * reverification of another are handled in Verifier and BuildState.
 * 
 * Note: for the release version, it may be unnecessary to re-compute the
 * taming database on a clean rebuild if we wish to disallow modification of
 * the database anyway.
 */
public class Builder extends IncrementalProjectBuilder {
	public static final String BUILDER_ID = "Joe_E.JoeEBuilder";
	private static final String MARKER_TYPE = "Joe_E.JoeEProblem";
	
    private BuildState state = null;	// empty until first full build
	private Taming taming = null;
    private Verifier verifier = null;

    /**
     * Build or re-build the project.  This will check all modified Joe-E
     * source files (and any other files that depend on the contents of the
     * changed files) and update the set of compilation errors and warnings
     * accordingly.
     * 
     * Will invoke a full build if the build kind is CLEAN_BUILD or FULL_BUILD,
     * or if there is no build state established from a previous build.  Will
     * cause an incremental build the build kind is INCREMENTAL_BUILD or
     * AUTO_BUILD given a valid delta and a build state established by a prior
     * build.
     * 
     * @param kind  
     *          the type of build to perform, see static members of 
     *          the superclass, 
     *          org.eclipse.core.resources.IncrementalProjectBuilder
     * @param args
     *          not needed for this Builder; ignored.
     * @param monitor
     *          used for reporting on the progress of the build
     * 
     * @return  always null for now
     *          (TODO: should return projects for any out-of-project
     *                 dependencies)
     *          
     * @throws CoreException
     *          if a problem arises during the build that cannot be indicated
     *          by adding an Error marker to a source file being built.
     */
    protected IProject[] build(int kind, Map args, IProgressMonitor monitor) 
        throws CoreException {
        //TODO: more disciplined debugging?
		System.out.println("Build request issued.");
		
		switch (kind) {
		case FULL_BUILD:
			fullBuild(monitor);
			break;
			
		case INCREMENTAL_BUILD:
		case AUTO_BUILD:
			if (state == null) {
				fullBuild(monitor);
			} else {
				IResourceDelta delta = getDelta(getProject());
				if (delta == null) {
					fullBuild(monitor);
				} else {
					incrementalBuild(delta, monitor);
				}
			}
			break;
		
		default:
			// this should never happen: all values enumerated above except
            // for CLEAN_BUILD, which instead invokes clean().
			throw new IllegalArgumentException("Invalid kind of build: "
                                               + kind);
		}
		
		return null;
	}

    /**
     * Clean up all build state. Should *not* trigger a rebuild, should just
     * clean everything up (like "build clean" in make).
     */
    protected void clean(IProgressMonitor monitor) throws CoreException {
        state = null;
        getProject().deleteMarkers("Joe_E.JoeEProblem", 
                true, IResource.DEPTH_INFINITE);
    }
        
	/**
	 * Invoke the Joe-E verifier on a compilation unit and update the markers
     * for Joe-E problems. (First removes old problems, then runs verifier to
     * generate new problems.)
	 * @param icu 
     *          the compilation unit to check
     * @return          
     *          additional ICompilationUnits that must be re-verified due
     *          to changes in this compilation unit
	 */
	private Collection<ICompilationUnit> 
            checkAndUpdateProblems(ICompilationUnit icu) throws CoreException {
        IFile file = (IFile) icu.getCorrespondingResource();
       
        System.out.println("Checking file " + file.getFullPath() + ":");
        
        deleteMarkers(file);
		List<Problem> problems = new LinkedList<Problem>();
       
        Collection<ICompilationUnit> recheck = 
            verifier.checkICU(icu, problems);
		
        System.out.println("... found " + problems.size() + " problem" 
                           + (problems.size() == 1 ? "." : "s."));
        
        // System.out.println(state);
        
		//TODO: use CompilationUnit's built-in line number finder?
		SourceLocationConverter slc = new SourceLocationConverter(file);
		ListIterator<Problem> i = problems.listIterator();
		while(i.hasNext()) {
			addMarker(file, i.next(), slc);
		}
        
        return recheck;
	}
	
    /**
     * Adds a Problem as a warning or error marker for a file in Eclipse's 
     * marker framework.
     * 
     * @param file 
     *          the resource to which to add the marker
     * @param problem 
     *          the problem to add
     * @param slc 
     *          a converter from byte offsets to line numbers that has been
     *          initialized for the given file
     */
	private void addMarker(IFile file, Problem problem, 
                           SourceLocationConverter slc) throws CoreException {
		IMarker marker = file.createMarker(MARKER_TYPE);
		marker.setAttribute(IMarker.MESSAGE, problem.getMessage());
		marker.setAttribute(IMarker.SEVERITY, problem.getSeverity());
		marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
		marker.setAttribute(IMarker.LINE_NUMBER, 
                            slc.getLine(problem.getStart()));
		marker.setAttribute(IMarker.CHAR_START, problem.getStart());
		marker.setAttribute(IMarker.CHAR_END, problem.getEnd());
		// System.out.println("added marker " + marker.toString());
	}

    /**
     * Removes all Joe-E created markers from a file
     *  
     * @param file
     *          the file from which to remove markers
     */
	private void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
            ce.printStackTrace(); // TODO: classier debug here
            // No need to rethrow an exception here: failure to delete a marker
            // can never result in an invalid build proceeding without error.
		}
	}

	/**
	 * Rebuild everything in the project.  Currently does not support 
     * cancellation of the build.  
     * 
     * @param monitor
     *          progress monitor to report progress of the build.
	 */
	protected void fullBuild(IProgressMonitor monitor) throws CoreException {
		state = new BuildState(); // clear build state
        IJavaProject jp = JavaCore.create(getProject());
        
        taming = new Taming(new java.io.File(Preferences.getTamingPath()), jp);
        
        verifier = new Verifier(state, taming);
	
        ResourceVisitor rv = new ResourceVisitor();
        getProject().accept(rv);
        Set<ICompilationUnit> inBuild = rv.inBuild; 
        Queue<ICompilationUnit> workQueue = 
            new LinkedList<ICompilationUnit>(inBuild);
            
        monitor.beginTask("Joe-E full build", workQueue.size());
            
        while (!workQueue.isEmpty()) {
            ICompilationUnit current = workQueue.remove();
            // for full build, ignore dependency-induced build requests --
            // everything should already be included.
            monitor.subTask("Running Verifier on " + current.getElementName());
            checkAndUpdateProblems(current);
            monitor.worked(1);
        }
        
        monitor.done();       
	}
    
    /**
     * Visitor that extracts the set of all ICompilationUnits from an 
     * IResourceDelta.  Iterates through the resources in the project, 
     * converting all Java source file resources into compilation units.
     */
    class ResourceVisitor implements IResourceVisitor {
        Set<ICompilationUnit> inBuild = new HashSet<ICompilationUnit>();
        
        /**
         * Visit method to add compilation units for Java files encountered.
         * @param resource
         *          The resource to visit
         * @return
         *          true in order to visit children of this resource
         */
        public boolean visit(IResource resource) {
            if (resource instanceof IFile) {
                IFile file = (IFile) resource;
                if (file.getName().endsWith(".java")) {
                    ICompilationUnit icu = 
                        (ICompilationUnit) JavaCore.create(file);
                    if (icu.exists()) {
                        // .java files in weird locations don't exist
                        inBuild.add(icu);
                    }
                }
            }
            
            //return true to continue visiting children.
            return true;
        }
    }
    
    /**
     * Rebuild all compilation units in the specified delta, as well as files 
     * determined to need rebuilding after these compilation units are built.
     * Currently does not support cancellation of the build.  
     * 
     * @param delta
     *          The delta specifying which compilation units have changed or
     *          are newly created and require verification.
     * @param monitor
     *          progress monitor to report progress of the build.
     * @throws CoreException
     *          when an error occurs that prevents invocation of the verifier
     *          or a problem is discovered for which a Marker cannot be
     *          created.
     */
	protected void incrementalBuild(IResourceDelta delta, 
                                    IProgressMonitor monitor)
        throws CoreException {     
        DeltaVisitor dv = new DeltaVisitor();
        delta.accept(dv);
        Set<ICompilationUnit> inBuild = dv.inBuild;
        Queue<ICompilationUnit> workQueue = 
            new LinkedList<ICompilationUnit>(inBuild);
            
        int fewestTasksRemaining = inBuild.size();
        monitor.beginTask("Joe-E incremental build", fewestTasksRemaining);
            
        while (!workQueue.isEmpty()) {
            ICompilationUnit current = workQueue.remove();
            monitor.subTask("Running Verifier on " + current.getElementName());
            // additional units to build
            Collection<ICompilationUnit> additional = 
                checkAndUpdateProblems(current);
            for (ICompilationUnit i : additional) {
                // add to build and to work queue if it's not already part of
                // the build
                if (inBuild.add(i)) {
                    workQueue.add(i);
                }
            }
            // Report progress if we reach a new low-water-mark of
            // compilation units remaining to check.  (This will always be
            // a decrease of 1).  Could be made more accurate; incremental
            // builds are likely to be fast anyway, though.
            if (workQueue.size() < fewestTasksRemaining) {
                monitor.worked(1);
                --fewestTasksRemaining;
            }
        } 
        monitor.done();
	}
    
    /**
     * Visitor that extracts the set of changed ICompilationUnits from an 
     * IResourceDelta.  Iterates through the resources in the delta, converting
     * Java source file resources into compilation units.
     */
    class DeltaVisitor implements IResourceDeltaVisitor {
        final Set<ICompilationUnit> inBuild = new HashSet<ICompilationUnit>();
              
        /**
         * Visit method to add compilation units for Java files encountered.
         * @param delta
         *          The delta to visit
         * @return
         *          true in order to visit children of this delta
         */
        public boolean visit(IResourceDelta delta) throws CoreException {
            System.out.println("Delta! " + delta.toString());
            IResource resource = delta.getResource();
            
            if (delta.getKind() == IResourceDelta.REMOVED) {
                // handle removed resource
                // TODO: I'm assuming there is no reason to remove existing
                //    markers here.  This could conceivably be a memory leak.
                // NOT reverifying anything if an interesting class has been
                //    removed; compilation will fail anyway
            } else {  // ADDED or CHANGED 
                if (resource instanceof IFile) {
                    IFile file = (IFile) resource;
                    if (file.getName().endsWith(".java")) {
                        ICompilationUnit icu = 
                            (ICompilationUnit) JavaCore.create(file);
                        if (icu.exists()) {
                            inBuild.add(icu);
                        }
                    }
                }
            }
            
            //return true to continue visiting children.
            return true;
        }
    }
    
    
    /**
     * Class for computing line numbers for all characters in a file.  Lines
	 * are numbered starting with 1.
     */
    private class SourceLocationConverter{
	    Integer[] lineStarts; 
		
		/**
		 * Create a source location converter for the specified file.  The
		 * constructor reads the file and records the location of newlines
		 * to make calls to newLine() fast.
         * 
         * @param file the file for which to compute line numbers
         */
		SourceLocationConverter(IFile file) throws CoreException {
		    List<Integer> newLines = new LinkedList<Integer>();
		    newLines.add(0);
		    try {
		        java.io.InputStream contents = file.getContents();
		        
		        int nextByte = contents.read();
		        for (int i = 0; nextByte >= 0; ++i) {
		            if (nextByte == '\n') {
		                newLines.add(i);
		            }
					nextByte = contents.read();
				}
			} catch (java.io.IOException e) {
                // Wrap the IOException in a CoreException.
                throw new JavaModelException
                             (e, IJavaModelStatusConstants.IO_EXCEPTION);
			}

			lineStarts = newLines.toArray(new Integer[]{});
		}
		
		/**
		 * Get the line number for the specified character in the file
		 * associated with this SourceLocationConverter instance.
		 * 
		 * @param charNumber the position in the file for which to search
		 * @return the number of the line containing this character, starting
		 * 		   with line 1
		 */
		int getLine(int charNumber) {
			int low = 1;
			int hi = lineStarts.length;
			
			// invariant: low <= answer <= hi
			while (low < hi) {
				//System.out.println("hi " + hi + ", low " + low);
				int mid = (low + hi) / 2;
				if (charNumber < lineStarts[mid]) {
					hi = mid;
				} else { 
				    low = mid + 1;
				}
			}
			
			return hi;
		}
	}
}
