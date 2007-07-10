// Copyright 2005-06 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e.eclipse;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

//import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;

public class Taming {
    private final HashMap<IType, Entry> db;
    final IJavaProject project;
    
    /*
     *  Types with significance to the verifier.
     */
    // final IType OBJECT;
    // final IType ENUM;

    final IType SELFLESS;
    final IType IMMUTABLE;
    final IType POWERLESS;
    // final IType DATA;
    final IType EQUATABLE;
    final IType TOKEN;
       
    Taming(File persistentDB, IJavaProject project) throws CoreException {
        this.project = project;

        // These are in the Java library and should always be findable.
        // OBJECT = project.findType("java.lang.Object");
        // ENUM = project.findType("java.lang.Enum"); 
        
        // The following may not be found if the Joe-E library is not reachable.
        SELFLESS = project.findType("org.joe_e.Selfless");
        IMMUTABLE = project.findType("org.joe_e.Immutable");
        POWERLESS = project.findType("org.joe_e.Powerless");
        // RECORD = project.findType("org.joe_e.Record");
        // DATA = project.findType("org.joe_e.Data");
        EQUATABLE = project.findType("org.joe_e.Equatable");   
        TOKEN = project.findType("org.joe_e.Token");     
        
        // Sanity check for Joe-E library
        if (SELFLESS == null || IMMUTABLE == null || POWERLESS == null 
            || EQUATABLE == null || TOKEN == null) {
            System.out.println("FATAL: Could not find Joe-E library classes!");
            throw new CoreException(
                new Status(IStatus.ERROR, Plugin.PLUGIN_ID, 10, 
                           "Joe-E library not found!\nThe Joe-E library " +
                           "classes (org.joe_e.*) must be in the project's " +
                           "build path\nin order to use the Joe-E verifier.", 
                           null));
        }
        
        this.db = new HashMap<IType, Entry>();
        
        // Sanity check for the taming database
        if (persistentDB == null || !persistentDB.isDirectory()) {
            System.out.println("FATAL: Taming DB path \"" + persistentDB + 
                               "\" does not exist or is not a directory.");
            throw new CoreException(
                new Status(IStatus.ERROR, Plugin.PLUGIN_ID, 20, 
                           "Taming database not found!\nThe taming " +
                           "database path \"" + persistentDB + "\" does " +
                           "not exist or is not a directory.\nPlease " + 
                           "select a taming directory in the Joe-E pane " +
                           "of Window·Preferences.", null));
        }
            
        File[] files = persistentDB.listFiles();
        for (File f : files) {
            String name = f.getName();
            if (name.endsWith(".taming")) {
                String typeName = name.substring(0, name.length() 
                                                    - ".taming".length());
                IType type = project.findType(typeName);
                if (type == null) {
                    System.err.println("ERROR: Type " + typeName +
                                       " not found!");
                } else {
                    System.out.println("Reading taming data for type "
                            + typeName + " ...");
                    db.put(type, new Entry(type, f));
                }
            }
        } 
        System.out.println("Done reading taming DB");
    }  
        
    /*
     * Returns whether the type specified by itb implements the marker
     * interface mi.
     * For now assumes that base types implement all marker interfaces
     * and arrays implement none!
     * @param itb   type binding to check 
     * @param mi    A marker interface
     * @return true if n1 implements mi in the overlay type system
     */
    boolean implementsOverlay(ITypeBinding itb, IType mi)
                                        throws JavaModelException {
        if (itb.isPrimitive() || itb.isNullType()) {
            return true;
        } else if (itb.isArray()) {
            return false;
        } else if (itb.isClass() || itb.isInterface() || itb.isEnum()
                   || itb.isTypeVariable()) {
            // System.out.println("is called on type " + n1);
            IType type = (IType) (itb.getErasure()).getJavaElement();
            return implementsOverlay(type, mi);
        } else {
            throw new IllegalArgumentException("unhandled binding type!");
        }
    }
    
    boolean implementsOverlay(IType subtype, IType mi) throws JavaModelException {
        ITypeHierarchy sth = subtype.newSupertypeHierarchy(null);
        if (sth.contains(mi)) {
            return true;
        } else {
            Collection<IType> h = getHonorariesFor(subtype);
            if (h.contains(mi)) {
                return true;
            }
        }
        return false;
    }
       
    Set<IType> unimplementedHonoraries(ITypeHierarchy sth) {
        Set<IType> result = new HashSet<IType>();
        
        for (IType t : sth.getAllTypes()) {
            Collection<IType> h = getHonorariesFor(t); 
            for (IType ht : h) {
                if (!sth.contains(ht)) {
                    result.add(ht);
                }
            }
        }
        
        return result;
    }
    
    Collection<IType> getHonorariesFor(IType type) {
        Entry e = db.get(type);
        if (e == null) {
            return new LinkedList<IType>();
        } else {
            return e.honoraries;
        }
    }
    
    boolean isTamed(ITypeBinding itb) {
        if (Preferences.isTamingEnabled()) {
            return db.containsKey((IType) itb.getJavaElement());
        } else {
            return true;
        }
    }

    boolean isAllowed(ITypeBinding classBinding, IVariableBinding fieldBinding) {
        if (Preferences.isTamingEnabled()) {
            Entry e = db.get((IType) classBinding.getJavaElement());
            IField field = (IField) fieldBinding.getJavaElement();
            return ((e != null) && e.allowedFields.contains(field));
        } else {
            return true;
        }
    }

    boolean isAllowed(ITypeBinding classBinding, IMethodBinding methodBinding) {
        if (Preferences.isTamingEnabled()) {
            Entry e = db.get((IType) classBinding.getJavaElement());
            //System.out.println("binding key: " + methodBinding.getKey());
            IMethod method = (IMethod) methodBinding.getJavaElement();
            //for (IMethod m : e.allowedMethods) {
            //    System.out.println("itsakey: " + m.getKey());
            //    System.out.println("equals? " + method.equals(m));
            //}
            return ((e != null) && e.allowedMethods.contains(method));
        } else {
            return true;
        }    
    }

    class Entry {
        final Set<IType> honoraries;
        // final Set<IType> deemings;
        final Set<IField> allowedFields;
        final Set<IMethod> allowedMethods;
        
        // TODO: static file-contents based approach, rather than reading in a config file here?
        
        Entry(Set<IMethod> allowedMethods, Set<IField> allowedFields, 
              Set<IType> deemings, Set<IType> honoraries) {
            this.honoraries = honoraries;
            // this.deemings = deemings;
            this.allowedFields = allowedFields;
            this.allowedMethods = allowedMethods;
        }
        
        /*
        Entry(IMethod[] allowedMethods, IField[] allowedFields, 
              IType[] deemings, IType[] honoraries) {
            this.honoraries = new HashSet<IType>();
            this.honoraries.addAll(Arrays.asList(honoraries));
            this.allowedFields = new HashSet<IField>();
            this.allowedFields.addAll(Arrays.asList(allowedFields));
            this.allowedMethods = new HashSet<IMethod>();
            this.allowedMethods.addAll(Arrays.asList(allowedMethods));            
            // this.deemings = new HashSet<IType>();
            // this.deemings.addAll(Arrays.asList(deemings));
        }
        */
        
        Entry(IType type, File f) {
            allowedMethods = new HashSet<IMethod>();
            allowedFields  = new HashSet<IField>();
            // deemings       = new HashSet<IType>();
            honoraries     = new HashSet<IType>();
            
            try {    
                BufferedReader br = new BufferedReader(new FileReader(f));
                
                String[] firstLine = br.readLine().split(" ");
                parseMarkerInterfaces(firstLine, honoraries);
                
                // String[] secondLine = br.readLine().split(" ");
                // parseMarkerInterfaces(secondLine, deemings);
                
                String nextLine = br.readLine();
                while (nextLine != null && !nextLine.contains("(")) {
                    IField field = type.getField(nextLine);
                    if (field.exists()) {
                        // System.out.println("   f " + nextLine);
                        allowedFields.add(field);
                    } else {
                        System.out.println("*WARNING: Nonexistent field \"" +
                                           nextLine + "\" skipped.");
                    }
                    
                    nextLine = br.readLine();
                }
                
                IMethod[] methods = type.getMethods();
                Map<String, IMethod> stringsToMethods = new HashMap<String, IMethod>();
                for (IMethod im : methods) {
                    // Exclude synthetic methods; these can include extra
                    // versions of methods with the same arguments but different
                    // return types, which are not equal to the methods invoked.
                    if (!Flags.isSynthetic(im.getFlags())) {
                        // TODO: string representation is undocumented; this is
                        // fragile.  Consider using stable interfaces instead.
                        String imToString = im.toString();
                        int lparen = imToString.indexOf("(");
                        // no space found results in start = 0, just what we want
                        int start = imToString.lastIndexOf(" ", lparen) + 1;
                        int end = imToString.indexOf(")") + 1;
                        imToString = imToString.substring(start, end);
                    	//System.out.println(imToString);
                    	stringsToMethods.put(imToString, im);
                    } else {
                        //System.out.println("Synthetic method " + im
                        //                   + " ignored");
                    }
                }
                
                while (nextLine != null) {
                    IMethod allowed = stringsToMethods.get(nextLine);
                    if (allowed == null) {
                        System.out.println("*WARNING: Nonexistent method \"" +
                                           nextLine + "\" skipped.");
                    } else {
                        // System.out.println("   m " + nextLine);
                        allowedMethods.add(allowed);
                    }
                    
                    nextLine = br.readLine();
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        void parseMarkerInterfaces (String[] line, Set<IType> dest) {
            for (String s : line) {
                if (s.equals(IMMUTABLE.getElementName())) {
                    dest.add(IMMUTABLE);
                } else if (s.equals(POWERLESS.getElementName())) {
                    dest.add(IMMUTABLE);
                    dest.add(POWERLESS);
                } else if (s.equals(SELFLESS.getElementName())) {
                    dest.add(SELFLESS);
                } else if (s.equals("Data")) {
                    dest.add(IMMUTABLE);
                    dest.add(POWERLESS);
                    dest.add(SELFLESS);
                } else if (s.equals(EQUATABLE.getElementName())) {
                    dest.add(EQUATABLE);
                } else if (s.length() > 0) {
                    System.out.println("*WARNING: Unrecognized marker interface"
                                       + " \"" + s + "\" ignored.");
                }
            }
        }      
    }
}