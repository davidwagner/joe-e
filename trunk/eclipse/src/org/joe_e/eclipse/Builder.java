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

public class Builder extends IncrementalProjectBuilder {
	public static final String BUILDER_ID = "Joe_E.JoeEBuilder";
	private static final String MARKER_TYPE = "Joe_E.JoeEProblem";
	
    private BuildState state = null;	// empty until first full build
	private Taming taming = null;
    private Verifier verifier = null;

//	private Set<ICompilationUnit> completed;
//	private Queue<ICompilationUnit> workList;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		System.out.println("Build request issued.");
		
		switch (kind) {
		case CLEAN_BUILD:
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
			// this should never happen: all values enumerated above
			throw new IllegalArgumentException("Invalid kind of build: " + kind);
		}
		
		return null;
	}
	


	/**
	 * Invoke the Joe-E verifier on a compilation unit and update the markers for Joe-E problems.
	 * (First removes old problems, then runs verifier to generate new problems.)
	 * @param icu the compilation unit to check
     * @return additional ICompilationUnits that must be re-verified due to changes in this
     *  compilation unit
	 */
	private Collection<ICompilationUnit> checkAndUpdateProblems(ICompilationUnit icu) 
            throws JavaModelException {
		IFile file = (IFile) icu.getCorrespondingResource();
		deleteMarkers(file);
		List<Problem> problems = new LinkedList<Problem>();
        Collection<ICompilationUnit> recheck = verifier.checkICU(icu, problems);
		System.out.println("checkAndAddProblems: " + problems);
		//TODO: use CompilationUnit's built-in line number finder?
		SourceLocationConverter slc = new SourceLocationConverter(file);
		ListIterator<Problem> i = problems.listIterator();
		while(i.hasNext()) {
			addMarker(file, i.next(), slc);
		}
        
        return recheck;
	}
	
	private void addMarker(IFile file, Problem problem, SourceLocationConverter slc) {
		try {
			IMarker marker = file.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, problem.getMessage());
			marker.setAttribute(IMarker.SEVERITY, problem.getSeverity());
			marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
			marker.setAttribute(IMarker.LINE_NUMBER, slc.getLine(problem.getStart()));
			marker.setAttribute(IMarker.CHAR_START, problem.getStart());
			marker.setAttribute(IMarker.CHAR_END, problem.getEnd());
			// System.out.println("added marker " + marker.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}

	/**
	 * Rebuild everything in the project.
	 * TODO: Is this the correct semantics for "FULL_BUILD"?  "CLEAN_BUILD"?
	 */
	protected void fullBuild(final IProgressMonitor monitor) 
		throws CoreException {
		state = new BuildState(); // clear build state
        IJavaProject jp = JavaCore.create(getProject());
        taming = new Taming(new java.io.File("/home/adrian/taming"), jp);
        verifier = new Verifier(jp, state, taming);
		
		try {			
		    ResourceVisitor rv = new ResourceVisitor();
            getProject().accept(rv);
            Set<ICompilationUnit> inBuild = rv.inBuild; 
            Queue<ICompilationUnit> workQueue = new LinkedList<ICompilationUnit>(inBuild);
            
            while (!workQueue.isEmpty()) {
                ICompilationUnit current = workQueue.remove();
                // for full build, ignore dependency-induced build requests --
                // we should have everything
                checkAndUpdateProblems(current);
            }
            
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
    
    /**
     * Used by full build
     */
    class ResourceVisitor implements IResourceVisitor {
        Set<ICompilationUnit> inBuild = new HashSet<ICompilationUnit>();
        
        public boolean visit(IResource resource) {
            if (resource instanceof IFile) {
                IFile file = (IFile) resource;
                if (file.getName().endsWith(".java")) {
                    ICompilationUnit icu = (ICompilationUnit) JavaCore.create(file);
                    inBuild.add(icu);
                }
            }
            
            //return true to continue visiting children.
            return true;
        }
    }
    
    
	protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor)
		throws CoreException {
 
        try {           
            DeltaVisitor dv = new DeltaVisitor();
            delta.accept(dv);
            Set<ICompilationUnit> inBuild = dv.inBuild; 
            Queue<ICompilationUnit> workQueue = new LinkedList<ICompilationUnit>(inBuild);
            
            while (!workQueue.isEmpty()) {
                ICompilationUnit current = workQueue.remove();
                // additional units to build
                Collection<ICompilationUnit> additional = checkAndUpdateProblems(current);
                for (ICompilationUnit i : additional) {
                    // add to build and to work queue if it's not already part of the build
                    if (inBuild.add(i)) {
                        workQueue.add(i);
                    }
                }
            } 
        } catch (CoreException e) {
            e.printStackTrace();
        }		
	}
    
    /**
     * Visitor that extracts a set of changed ICompilationUnits from 
     * an IResourceDelta
     */
    class DeltaVisitor implements IResourceDeltaVisitor {
        final Set<ICompilationUnit> inBuild = new HashSet<ICompilationUnit>();
              
        public boolean visit(IResourceDelta delta) throws CoreException {
            System.out.println("Delta! " + delta.toString());
            IResource resource = delta.getResource();
            
            if (delta.getKind() == IResourceDelta.REMOVED) {
                // handle removed resource
                // TODO: need to remove markers?? apparently not?
                // NOT reverifying if an interesting class has been removed; 
            	//  compilation will fail anyway
            } else {  // ADDED or CHANGED 
                if (resource instanceof IFile) {
                    IFile file = (IFile) resource;
                    if (file.getName().endsWith(".java")) {
                        ICompilationUnit icu = (ICompilationUnit) JavaCore.create(file);
                        inBuild.add(icu);
                    }
                }
            }
            
            //return true to continue visiting children.
            return true;
        }
    }
    
    
	/**
	 * Computes line numbers for all characters in a file.  Lines are numbered
	 * starting with 1.
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
		SourceLocationConverter(IFile file) {
			List<Integer> newLines = new LinkedList<Integer>();
			newLines.add(0);
			try {
				java.io.InputStream contents = file.getContents();
				
				int nextByte = contents.read();
				for (int i = 0; nextByte >= 0; ++i) {
					if (nextByte == '\n')
						newLines.add(i);
					nextByte = contents.read();
				}
			} catch (Exception e) {
				e.printStackTrace();
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
