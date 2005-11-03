package org.joe_e.eclipse;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class Utility {

	/**
	 * Looks up the IType for a source type
	 * 
	 * @param n1 An eclipse Signature string indicating an object type
	 * @param context where to look up the type specified by n1
	 * @return the type specified by n1
	 * @throws JavaModelException
	 */
	static IType lookupType(String n1, IType context) throws JavaModelException
	{
		String sourceType = n1.substring(1, n1.length() - 1);
		String[][] typePaths = context.resolveType(sourceType);
		return context.getJavaProject().findType(typePaths[0][0],
				typePaths[0][1]);
	}

}
