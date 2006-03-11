package org.joe_e.eclipse;

import java.util.HashMap;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;

public class MarkerInterface {
	static HashMap<String, String[]> honoraries = new HashMap<String, String[]>();
	static HashMap<String, String[]> deemings = new HashMap<String, String[]>();
	
	static {
		// TODO: read from a file.
		
		honoraries.put("java.lang.String", new String[]{"Incapable"});
		honoraries.put("java.lang.Integer", new String[]{"Incapable"});
		honoraries.put("java.lang.Double", new String[]{"Incapable"});
		honoraries.put("java.lang.Enum", new String[]{"Incapable"});
		
		// as is, no need to put deemings in here, as library classes are never verified.
		// TODO: this may be an oversight.
	}
	
	/**
	 * Returns whether the class n1 implements the marker interface mi.
	 * The marker interface is prepended with "org.joe_e."
	 * For now assumes that base types implement all marker interfaces!
	 * @param n1 An Eclipse Signature type
	 * @param mi A marker interface, once prepended with "org.joe_e."
	 * @param context the context in which to evaluate bindings
	 * @return true if n1 implements mi in the overlay type system
	 */
	static boolean is(String n1, String mi, IType context)
	{
		if (n1.length() == 1) {
			return true;
		} else if (n1.charAt(0) == '[') {
			return false;
		} else if (n1.charAt(0) == 'Q') {
			System.out.println("is called on type " + n1);
			try {
				IType t1 = Utility.lookupType(n1, context);
				if (t1 == null) {
					System.out.println("type not found (shouldn't happen -- BUG)");
					return false;
				} else {
					return (is(t1, mi));
				}
			} catch (JavaModelException jme) {
				jme.printStackTrace();
				return false;
			}
		} else {
			System.out.println("unknown type kind: " + n1);
			return false;
		}
	}
	
	/**
	 * Checks whether a type implements the marker interface specified.
	 * The marker interface is prepended with "org.joe_e."
	 */
	static boolean is(IType t1, String mi)
	{
		try {
			ITypeHierarchy sth = t1.newSupertypeHierarchy(null);
			IType incapableType = 
				t1.getJavaProject().findType("org.joe_e." + mi);
			if (incapableType == null) {
				System.out.println("Incapable type not found! joe_e.org.Incapable");
				System.out.println("should be in the project or linked libraries.");
				return false;
			}
			if (sth.contains(incapableType)) {
				return true;
			} else { 
				return isHonorarily(t1, mi);
			}
		} catch (JavaModelException jme) {
			jme.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Checks whether the type t1 honorarily implements the interface mi
	 * 
	 * TODO: will need rewriting once real deeming mechanism is in place
	 */
	static boolean isHonorarily(IType t1, String mi) throws JavaModelException
	{
		String[] record = getHonoraries(t1);
		if (record == null) {
			return false;
		}
		//
		// check if interface is explicitly honorary
		//
		for (int i = 0; i < record.length; ++i) {
			if (record[i].equals(mi)) {
				return true;
			}
		}
		//
		// check if a subinterface is honorary
		//
		IType miType = t1.getJavaProject().findType("org.joe_e." + mi);
		
		for (int i = 0; i < record.length; ++i) {
			ITypeHierarchy sth = 
				t1.getJavaProject().findType("org.joe_e." + record[i])
					.newSupertypeHierarchy(null);
			if (sth.contains(miType)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Get the honorary interfaces implemented by a type
	 * @param t1 the type to look up the honorary interfaces for
	 * @return a (possibly empty) array of strings representing honorary interfaces
	 */
	static String[] getHonoraries(IType t1)
	{
		String[] t1Honoraries = honoraries.get(t1.getFullyQualifiedName());
		if (t1Honoraries == null) {
			return new String[]{};
		} else {
			return t1Honoraries;
		}
	}
	
	/**
	 * Returns true if the specified type is deemed to satisfy the specified interface.
	 * At present, does not handle transitive case (I don't think it needs to?)
	 * @param t1
	 * @param mi
	 * @return
	 * @throws JavaModelException
	 */
	static boolean isDeemed(IType t1, String mi) throws JavaModelException
	{
		String[] record = deemings.get(t1.getFullyQualifiedName());
		if (record == null) {
			return false;
		}
		
		for (int i = 0; i < record.length; ++i) {
			if (record[i].equals(mi)) {
				return true;
			}
		}
		
		return false;
	}
}