package org.joe_e.eclipse;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.joe_e.eclipse.SourceLocationConverter;

public class Printer {

	static int totalErrors = 0;
	
	static void printErrors(IFile file) throws CoreException {
        totalErrors += printArrayOfMarkers(
        						file.findMarkers(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER, true, 
        						IResource.DEPTH_ZERO), file);
        totalErrors += printArrayOfMarkers(
        		file.findMarkers(Builder.MARKER_TYPE, true, IResource.DEPTH_ZERO), file);
        				
        return;
	}
	
	private static int printArrayOfMarkers(IMarker[] markers, IFile file) throws CoreException{
        int numProblems = 0;
        for (IMarker marker : markers) {
        	if (Main.commandLine) {
        		String severityString = "";
        		switch ((Integer) marker.getAttribute(IMarker.SEVERITY)) {
        		case IMarker.SEVERITY_ERROR:
        			severityString = "ERROR";
        			Main.errors = true;
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
        		System.out.println("" + ++numProblems + ". " + severityString + " in " + file.getFullPath().lastSegment() + " (at line " + marker.getAttribute(IMarker.LINE_NUMBER) + ")");
        		String sourceString = slc.getSourceCode((Integer) marker.getAttribute(IMarker.CHAR_START)) ;
        		System.out.println("\t" + sourceString);
        		System.out.println("\t" + slc.makeDashes(sourceString, (Integer) marker.getAttribute(IMarker.CHAR_START), (Integer) marker.getAttribute(IMarker.CHAR_END)));

        		System.out.println(marker.getAttribute(IMarker.MESSAGE));
        	}
        }
        return numProblems;
	}
}
