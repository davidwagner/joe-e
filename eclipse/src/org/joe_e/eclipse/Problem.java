// Copyright 2005-06 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e.eclipse;

import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.core.resources.IMarker;

public class Problem {
    private final String message;
    private final int start;
    private final int length;
    private final int severity;	// uses IMarker severity levels
	
    public Problem(String message) {
        this.message = message;
        this.start = 0;
        this.length = 0;
        this.severity = IMarker.SEVERITY_ERROR;
    }
	
    public Problem(String message, int severity) {
        this.message = message;
        this.start = 0;
        this.length = 0;
        this.severity = severity;
    }
	
    public Problem(String message, ISourceRange location)
    {
        this.message = message;
        this.start = location.getOffset();
        this.length = location.getLength();
        this.severity = IMarker.SEVERITY_ERROR;
    }
	
    public Problem(String message, int start, int length)
    {
        this.message = message;
        this.start = start;
        this.length = length;
        this.severity = IMarker.SEVERITY_ERROR;
    }
	
    public String getMessage() {
        return message;
    }
	
    public int getSeverity() {
        return severity;
    }
    
    public int getStart() {
        return start;
    }
	
    public int getEnd() {
        return start + length;
    }
}