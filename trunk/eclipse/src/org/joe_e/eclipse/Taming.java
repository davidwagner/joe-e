// Copyright 2005-06 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e.eclipse;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;

import org.eclipse.jdt.core.*;

public class Taming {
    private final HashMap<IType, Entry> db;
    final IJavaProject project;
    
    /* Important types used by multiple modules
     */
    final IType SELFLESS;
    final IType IMMUTABLE;
    final IType POWERLESS;
    final IType TOKEN;
    final IType ENUM;
         
    class Entry {
        final Set<IMethod> allowedMethods;
        final Set<IField> allowedFields;
        final Set<IType> deemings;
        final Set<IType> honoraries;
        
        Entry(IType type, File f) {
            allowedMethods = new HashSet<IMethod>();
            allowedFields  = new HashSet<IField>();
            deemings       = new HashSet<IType>();
            honoraries     = new HashSet<IType>();
            
            try {    
                BufferedReader br = 
                    new BufferedReader(new InputStreamReader(new FileInputStream(f)));
                
                String[] firstLine = br.readLine().split(" ");
                parseMarkerInterfaces(firstLine, honoraries);
                
                String[] secondLine = br.readLine().split(" ");
                parseMarkerInterfaces(secondLine, deemings);
                
                String nextLine = br.readLine();
                
                while (nextLine != null && !nextLine.contains("(")) {
                    // TODO: what happens if this isn't a real field??
                    IField field = type.getField(nextLine);
                    allowedFields.add(field);
                    System.out.println("added field " + field);
                }
                                
                IMethod[] methods = type.getMethods();
                Map<String, IMethod> stringsToMethods = new HashMap<String, IMethod>();
                for (IMethod im : methods) {
                    String imToString = im.toString();
                    imToString = imToString.substring(0, imToString.indexOf(" ["));
                    System.out.println("itsa method <" + imToString + ">");
                    stringsToMethods.put(imToString, im);
                }
                
                while (nextLine != null) {
                    IMethod allowed = stringsToMethods.get(nextLine);
                    if (allowed == null) {
                        System.out.println("PARSE ERROR! Unrecognized method " + nextLine);
                    } else {
                        allowedMethods.add(allowed);
                    }
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        void parseMarkerInterfaces (String[] line, Set<IType> dest) {
            for (String s : line) {
                if (s.equals(SELFLESS.getElementName())) {
                    dest.add(SELFLESS);
                } else if (s.equals(IMMUTABLE.getElementName())) {
                    dest.add(IMMUTABLE);
                } else if (s.equals(POWERLESS.getElementName())) {
                    dest.add(POWERLESS);
                } else {
                    System.out.println("PARSE ERROR! Unrecognized marker interface " + s);
                }
            }
        }
        
    }
    
    Taming(File persistentDB, IJavaProject project) throws JavaModelException {
        this.db = new HashMap<IType, Entry>();
        this.project = project;
        
        SELFLESS = project.findType("org.joe_e.Selfless");
        IMMUTABLE = project.findType("org.joe_e.Immutable");
        POWERLESS = project.findType("org.joe_e.Powerless");
        TOKEN = project.findType("org.joe_e.Token");
        ENUM = project.findType("java.lang.Enum");  
        
        
        if (persistentDB != null && persistentDB.isDirectory()) {
            File[] files = persistentDB.listFiles();
            for (File f : files) {
                String name = f.getName();
                if (name.endsWith(".taming")) {
                    IType type = project.findType(
                        name.substring(0, name.length() - ".taming".length()));
                    db.put(type, new Entry(type, f)); 
                }
            }
        }
    }

    /*
     * Returns whether the class with signature sig implements the marker
     * interface mi.
     * For now assumes that base types implement all marker interfaces
     * and arrays implement none!
     * @param n1 An Eclipse Signature type
     * @param mi A marker interface
     * @param context the context in which to evaluate bindings
     * @return true if n1 implements mi in the overlay type system
     */
    boolean implementsOverlay(String n1, IType mi, IType context)
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
                    return (implementsOverlay(t1, mi));
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
    
    
    boolean implementsOverlay(IType subtype, IType mi) throws JavaModelException {
        ITypeHierarchy sth = subtype.newSupertypeHierarchy(null);
        if (sth.contains(mi)) {
            return true;
        } else {
            for (IType t : sth.getAllClasses()) {
                Collection<IType> h = getHonorariesFor(t);
                if (h.contains(mi)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    Collection<IType> getHonorariesFor(IType type) {
        Entry e = db.get(type);
        if (e == null) {
            return new LinkedList<IType>();
        } else {
            return e.honoraries;
        }
    }
    
    Set<IType> unimplementedHonoraries(ITypeHierarchy sth) {
        Set<IType> result = new HashSet<IType>();
        
        for (IType t : sth.getAllClasses()) {
            Collection<IType> h = getHonorariesFor(t); 
            for (IType ht : h) {
                if (!sth.contains(ht)) {
                    result.add(ht);
                }
            }
        }
        
        return result;
    }
    
    boolean isTamed(IType type) {
        return db.containsKey(type);
    }
    
    boolean isAllowed(IMethod method) {
        if (method.getDeclaringType().isBinary()) {
            Entry e = db.get(method.getDeclaringType());
            if (e == null) {
                return false;
            } else {
                return e.allowedMethods.contains(method);
            }       
        } else {
            return true;
        }
    }
    
    boolean isAllowed(IField field) {
        if (field.getDeclaringType().isBinary()) {
            Entry e = db.get(field.getDeclaringType());
            if (e == null) {
                return false;
            } else {
                return e.allowedFields.contains(field);
            }       
        } else {
            return true;
        }
    }
    
    
    /**
     * Returns true if the specified type is deemed to satisfy the specified interface.
     * At present, does not handle transitive case (I don't think it needs to?)
     * @param type
     *            the type to test 
     * @param mi
     *            the marker interface
     * @return
     */
    boolean isDeemed(IType type, IType mi) {
        Entry e = db.get(type);
        if (e == null) {
            return false;
        } else {
            return e.deemings.contains(mi);
        }
    }
    
    

	
	/*
	 * Checks whether a type implements the marker interface specified.
	 * The marker interface is prepended with "org.joe_e."
	 *
	static boolean is(IType t1, String mi)
	{
		try {
			ITypeHierarchy sth = t1.newSupertypeHierarchy(null);
			IType powerlessType = 
				t1.getJavaProject().findType("org.joe_e." + mi);
			if (powerlessType == null) {
				System.out.println("Powerless type not found! org.joe_e.Powerless");
				System.out.println("should be in the project or linked libraries.");
				return false;
			}
			if (sth.contains(powerlessType)) {
				return true;
			} else { 
				return isHonorarily(t1, mi);
			}
		} catch (JavaModelException jme) {
			jme.printStackTrace();
			return false;
		}
	}
	*/
    
    
	/*
	 * Checks whether the type t1 honorarily implements the interface mi
	 * 
	 * TODO: will need rewriting once real deeming mechanism is in place
	 *
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
	*/
    
	/*
	 * Get the honorary interfaces implemented by a type
	 * @param t1 the type to look up the honorary interfaces for
	 * @return a (possibly empty) array of strings representing honorary interfaces
	 *
	static String[] getHonoraries(IType t1)
	{
		String[] t1Honoraries = honoraries.get(t1.getFullyQualifiedName());
		if (t1Honoraries == null) {
			return new String[]{};
		} else {
			return t1Honoraries;
		}
	}
	
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
    */
}