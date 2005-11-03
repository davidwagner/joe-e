package org.joe_e.eclipse;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class Utility {

	/**
	 * Looks up the IType for a source type
	 * 
	 * @param n1 An eclipse Signature string indicating an object type, possibly
	 * 			 with type parameters
	 * @param context where to look up the type specified by n1
	 * @return the type specified by n1, or null if it can't be found.  This can
	 * 		   occur, for example, if n1 is a generic type specifier
	 * @throws JavaModelException
	 */
	static IType lookupType(String n1, IType context) throws JavaModelException
	{
		String sourceType = n1.substring(1, n1.length() - 1);
		// resolveType seems to just ignore type parameters and succeed correctly.
		// Warning: this appears to be undocumented and thus may be brittle!
		String[][] typePaths = context.resolveType(sourceType);
		if (typePaths == null)
			return null; // possibly a generic type?
		else
			return context.getJavaProject().findType(typePaths[0][0],
													 typePaths[0][1]);
	}
	
	static String stripGenerics(String typeName) 
	{
		int loc = typeName.indexOf("<");
		if (loc < 0) {
			return typeName;
		} else {
			return typeName.substring(0, loc);
		}
	}

}
