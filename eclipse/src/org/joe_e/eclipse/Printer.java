// Copyright 2008 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Akshay Krishnamurthy
 * @author Kanav Arora
 * @author Adrian Mettler
 */

package org.joe_e.eclipse;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.joe_e.eclipse.SourceLocationConverter;

public class Printer {
    static int printErrors(IProject project) throws CoreException {
        int totalErrors = 0;
        for (IResource child : project.members()) {
            totalErrors += printErrors(child);
        }
        return totalErrors;
    }
    
    static int printErrors(IResource resource) throws CoreException {
	    int errors = 0;
	    if (resource instanceof IFolder) {
	        IFolder folder = (IFolder) resource;
	        for (IResource child : folder.members()) {
	            errors += printErrors(child);
	        }
	    } else if (resource instanceof IFile) {
	        IFile file = (IFile) resource;
	        errors += printMarkers(
	            resource.findMarkers(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER,
	                                 true, IResource.DEPTH_ZERO), file);
	        errors += printMarkers(
	                resource.findMarkers(Builder.MARKER_TYPE, true, 
	                                     IResource.DEPTH_ZERO), file);

	    }
	    
	    return errors;
	}
	
	private static int printMarkers(IMarker[] markers, IFile file) throws CoreException{
        int numProblems = 0;
        int numErrors = 0;
        for (IMarker marker : markers) {
            String severityString = "";
        	switch ((Integer) marker.getAttribute(IMarker.SEVERITY)) {
        	case IMarker.SEVERITY_ERROR:
        		severityString = "ERROR";
        		++numErrors;
        		break;
        	case IMarker.SEVERITY_INFO:
        		severityString = "INFO";
        		break;
        	case IMarker.SEVERITY_WARNING:
        		severityString = "WARNING";
        		break;
        	}
        	SourceLocationConverter slc = new SourceLocationConverter(file);
        	System.out.println("----------");
        	System.out.println("" + ++numProblems + ". " + severityString +
        	     " in " + file.getFullPath().lastSegment() + " (at line " +
        	     marker.getAttribute(IMarker.LINE_NUMBER) + ")");
        	String sourceString = 
        	     slc.getSourceCode((Integer) marker.getAttribute(IMarker.CHAR_START));
        	System.out.println("\t" + sourceString);
        	System.out.println("\t" +
        	     slc.makeDashes(sourceString,
        	         (Integer) marker.getAttribute(IMarker.CHAR_START),
        	         (Integer) marker.getAttribute(IMarker.CHAR_END)));
        	System.out.println(marker.getAttribute(IMarker.MESSAGE));
       	}
        return numErrors;
	}
}
