// Copyright 2005-06 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e;

public class Utility {

	static public boolean instanceOf(Object obj, Class<?> type) {
		return isSubtypeOf(obj.getClass(), type);
    }

	static public boolean isSubtypeOf(Class<?> c1, Class<?> c2) {
	    if (c2.isAssignableFrom(c1)) {
            return true;
        } else {
            return Honoraries.honorarilyImplements(c1, c2);
        }
	}
	
	
	/*
	static public boolean instanceOf(String n1, IType t2, IType context)
	{
		try {
			if (n1.charAt(0) == 'Q')
				IType type1 = lookupType(t1, context);
			
				
		try {
		if (t1.charAt(0) == 'Q')
		{
			IType type2 = lookupType(t2, context);
			ITypeHierarchy sth = type1.newSupertypeHierarchy(null);
					}
		
		if (honoraryInstanceOf(t1, t2, context)) {
			return true;
		} else {
			return false; // fixme
		}
		} catch (JavaModelException jme) {
			jme.printStackTrace();
		}
	}
	
	static public boolean honoraryInstanceOf(String t1, String t2, IType context)
	{
		try {
		if (t2.equals("org.joe_e.Incapable")) {
			if (t1.length() == 1) { // primitive type
				return true;
			} else if (t1.charAt(0) == '[') { // array
				return false;
			} else if (t1.charAt(0) == 'Q') {
				// look up honorary stuff
				return false;
			} else {
				System.out.println("Unknown type kind! " + t1);
				return false;
			}
		} else if (t2.equals("org.joe_e.DeepFrozen")) {
			if (t1.length() == 1) { // primitive type
				return true;
			} else if (t1.charAt(0) == '[') { // array
				return false;
			} else if (t1.charAt(0) == 'Q') {
				String sourceType = t1.substring(1, t1.length() - 1);
				String[][] typePaths = context.resolveType(sourceType);
				// look up honorary stuff
				return false;
			} else {
				System.out.println("Unknown type kind! " + t1);
				return false;
			}
		} else {
			// nothing else can be honorary, right?
			return false;
		}	
		} catch (JavaModelException jme) {
			jme.printStackTrace();
			return false;
		}
	}
	
	
	static public boolean instanceOf(Type t1, Type t2)
	{
		ITypeBinding tb1 = t1.resolveBinding();
		ITypeBinding tb2 = t2.resolveBinding();
		if (tb1.isSubTypeCompatible(tb2)) {	
			return true;
		} else if (t2.isSimpleType()) {
			SimpleType st2 = (SimpleType) t2;
			String name2 = st2.getName().getFullyQualifiedName();
			
			return (honoraryInstanceOf(t1, name2));
		} else {
			return false;
		}
	}
	*/
}
