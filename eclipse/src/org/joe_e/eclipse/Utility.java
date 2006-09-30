// Copyright 2005-06 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e.eclipse;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;

public class Utility {

	/**
	 * Tests whether the Eclipse signature specified is for a class type
	 * (as opposed to a primitive or array type).
	 * @param sig An Eclipse type signature
	 * @return true if sig is a class type
	 */
	static boolean signatureIsClass(String sig)
	{
		return sig.charAt(0) == 'Q';
	}
	
	/*
	 * Looks up the IType for a source type
	 * 
	 * @param n1 An eclipse Signature string indicating an object type, possibly
	 * 			 with type parameters
	 * @param context where to look up the type specified by n1
	 * @return the type specified by n1, or null if it can't be found.  This can
	 * 		   occur, for example, if n1 is a generic type specifier
	 * @throws JavaModelException
	 * @deprecated doesn't work with a local type as context.
     */
	static IType lookupType(String n1, IType context) throws JavaModelException
	{
        int typeEnd = n1.indexOf("<");
        if (typeEnd < 0) {
            typeEnd = n1.indexOf(";");
        }
        assert (typeEnd > 0);
        
        // The type in the source, with any generic type specifier(s) removed
        String sourceType = n1.substring(1, typeEnd);
        
  		String[][] typePaths = context.resolveType(sourceType);
		if (typePaths == null) {
			// Probably a parameterized type
			// TODO: This does not correctly handle type parameters that shadow real
			//   types (ugggh).  Fix.
			ITypeParameter itp = context.getTypeParameter(sourceType);
			if (itp == null) {
				return null; // this shouldn't happen
				
			} else { // For now just return the FIRST declared type bound, if any
					 // TODO: THIS IS WRONG (not because we want to handle multiple
                     // type bounds, but because bounds can't be trusted!).  FIX!
				String[] bounds = itp.getBounds();
				if (bounds.length == 0) {
					typePaths = context.resolveType("java.lang.Object");
				} else {
					typePaths = context.resolveType(bounds[0]);
				}
			}
		}

	    return context.getJavaProject().findType(typePaths[0][0], typePaths[0][1]);
	}
    
    /*
    static String stripGenerics(String typeName) 
    {
        int loc = typeName.indexOf("<");
        if (loc < 0) {
            return typeName;
        } else {
            return typeName.substring(0, loc);
        }
    }
    */
}
