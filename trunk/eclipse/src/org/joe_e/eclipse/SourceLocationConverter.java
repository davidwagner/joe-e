package org.joe_e.eclipse;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaModelStatusConstants;
import org.eclipse.jdt.core.JavaModelException;

public class SourceLocationConverter {
    Integer[] lineStarts;
    IFile file;
	
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
    
        this.file = file; 
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
    
    String getSourceCode(int charNumber) throws CoreException {
    	int lineNumber = getLine(charNumber);
    	int start = lineStarts[lineNumber-1];
    	int end = lineStarts[lineNumber];
    	int length = end - start - 1;
    	
        java.io.InputStream contents = file.getContents();
        byte[] arr = new byte[length];
        try {
        	contents.skip(start+1);
        	contents.read(arr, 0, length);
        } catch (IOException e) {
        	return "";
        }
        return new String(arr);
        
    }
    
    String makeDashes(String source, int start, int end) {
    	String toReturn = "";
    	int lineNumber = getLine(start);
    	start = start - lineStarts[lineNumber-1]-1;
    	end = end - lineStarts[lineNumber-1]-1;
    	
    	for (int i = 0; i < source.length() && i < end; i++) {
    		if (i >= start) {
    			toReturn += "^";
    		}
    		else if (source.charAt(i) == '\t') {
    			toReturn += "\t";
    		}
    		else {
    			toReturn += " ";
    		}
    	}
    	return toReturn;
    }
}
