package org.joe_e.eclipse;

import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;
//import java.util.HashSet;

import org.eclipse.jdt.core.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
//import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;

public class Builder extends IncrementalProjectBuilder {
	public static final String BUILDER_ID = "Joe_E.JoeEBuilder";
	private static final String MARKER_TYPE = "Joe_E.JoeEProblem";
	private static final QualifiedName INTERESTED_PROP = new QualifiedName("Joe_E", "interested-classes");
	
	
	static JavaCore jc = JavaCore.getJavaCore();
	
	BuildState state = null;	// empty until first full build
	
	class DeltaVisitor implements IResourceDeltaVisitor {
		//HashSet<String> interested; // classes that are affected by visited deltas and thus must be re-verified.
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 */
		
		/**
		 * includes a data structure for storing classes that must be re-verified.
		 */
		DeltaVisitor() {
			super();
			//this.interested = interested;
		}

		
		public boolean visit(IResourceDelta delta) throws CoreException {
			System.out.println("Delta!" + delta.toString());
			IResource resource = delta.getResource();
			
			if (delta.getKind() == IResourceDelta.REMOVED) {
				// handle removed resource
				// TODO: need to remove markers?? apparently not?
				// NOT reverifying if an interesting class has been removed; compilation will fail anyway
			} else {  // ADDED or CHANGED 
				// handle changed resource
				checkAndUpdateProblems(resource);
				
				/*
				String interestedClasses = resource.getPersistentProperty(INTERESTED_PROP);
				if (interestedClasses != null && interestedClasses.length() > 0) {
					int curIndex = 0;
					int nextSpace = interestedClasses.indexOf(' ');
					do {
						interested.add(interestedClasses.substring(curIndex, nextSpace));
						curIndex = nextSpace + 1;
						nextSpace = interestedClasses.indexOf(' ', curIndex);
					} while (nextSpace > 0);
				}
				*/
			}
			
			//return true to continue visiting children.
			return true;
		}
	}

	class SampleResourceVisitor implements IResourceVisitor {
		public boolean visit(IResource resource) {
			checkAndUpdateProblems(resource);
			
			//return true to continue visiting children.
			return true;
		}
	}

	
	class SourceLocationConverter{
		Integer[] lineStarts; 
		
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
	
	/**
	 * Invoke the Joe-E verifier on a resource and update the markers for Joe-E problems.
	 * (First removes old problems, then runs verifier to generate new problems.)
	 * Will silently ignore resources, such as directories, to which the verifier does not apply.
	 * @param resource the resource to check
	 */
	private void checkAndUpdateProblems(IResource resource) {
		if (resource instanceof IFile && resource.getName().endsWith(".java")) {
			IFile file = (IFile) resource;
			deleteMarkers(file);
			List<Problem> problems = Verifier.checkJavaFile(file);
			System.out.println("checkAndAddProblems: " + problems);
			//TODO: use CompilationUnit's built-in line number finder?
			SourceLocationConverter slc = new SourceLocationConverter(file);
			ListIterator<Problem> i = problems.listIterator();
			while(i.hasNext()) {
				addMarker(file, i.next(), slc);
			}
		}
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
			System.out.println("added marker " + marker.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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
		case INCREMENTAL_BUILD:
		case AUTO_BUILD:
			fullBuild(monitor);
			break;
		
		/*
		case AUTO_BUILD:
			// don't launch a full build, they may be slow(?).
			if (state == null || getDelta(getProject()) == null) {
				break;
			}
		*/
			// otherwise fall through
		/*
		case INCREMENTAL_BUILD:
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
		*/
		
		default:
			// this should never happen: all values enumerated above
			throw new IllegalArgumentException("Invalid kind of build: " + kind);
		}
		
		return null;
	}


	private void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}

	protected void fullBuild(final IProgressMonitor monitor) 
		throws CoreException {
		state = new BuildState(); // clear build state
		
		try {			
			getProject().accept(new SampleResourceVisitor());
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor)
		throws CoreException {
		// the visitor does the work.
		
		// TODO: include re-verification necessitated by dependencies or always
		// do a full build!
		delta.accept(new DeltaVisitor());
		
		// re-check interested classes
		/*
		for (String i : recheck) {
			// = something;
			
			
			//if (delta.findMember(path) == null) {
				// get IResource
				//checkAndUpdateProblems(resource);
			//}
		}
		*/
		// see if they are already included with delta.findMember(), else re-verify them
	
	}
}
