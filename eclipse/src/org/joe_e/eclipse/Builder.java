package org.joe_e.eclipse;

import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.*;

public class Builder extends IncrementalProjectBuilder {
	public static final String BUILDER_ID = "Joe_E.JoeEBuilder";
	private static final String MARKER_TYPE = "Joe_E.JoeEProblem";

	static JavaCore jc = JavaCore.getJavaCore();
	
	class SampleDeltaVisitor implements IResourceDeltaVisitor {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				// handle added resource
				checkAndUpdateProblems(resource);
				break;
			case IResourceDelta.REMOVED:
				// handle removed resource
				// TODO: need to remove markers??
				break;
			case IResourceDelta.CHANGED:
				// handle changed resource
				checkAndUpdateProblems(resource);
				break;
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
		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
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
		try {
			getProject().accept(new SampleResourceVisitor());
		} catch (CoreException e) {
		}
	}

	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		// the visitor does the work.
		delta.accept(new SampleDeltaVisitor());
	}
}
