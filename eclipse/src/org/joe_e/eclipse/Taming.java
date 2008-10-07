// Copyright 2005-08 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e.eclipse;

import java.io.File;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
//import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IProject;

import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IAnnotationBinding;

public class Taming {
    /*
     * Static utility methods that are independent of any taming data
     */
    /**
     * Check whether a type should be included in the taming database; i.e.
     * whether it may be visible to a type in another package.  This is true
     * when all of a type's components (there is only one for top-level types)
     * are visible.  A protected type is considered to be visible if its
     * enclosing type is not final.  Members of interfaces are implicitly
     * public and are correctly handled by this method.
     * 
     * @param type the type to check
     * @return true if the type is relevant to taming
     */
    static boolean isRelevant(IType type) throws JavaModelException {
        IType current = type;
        while (current != null) {
            int flags = current.getFlags();
            IType parent = current.getDeclaringType();
            if (Flags.isProtected(flags) && parent != null) {
                if (Flags.isFinal(parent.getFlags())) {
                    return false;
                }
            }
            else if (!Flags.isPublic(flags) && !Flags.isProtected(flags)
                     && (parent == null || !parent.isInterface())) {
                return false;
            }
            current = parent;
        }
        return true;
    }

    /**
     * Check whether a field should be included in the taming database; i.e.
     * whether it may be visible to a type in another package.  This is true
     * if the field is public, or is protected and the type is non-final.
     * Members of interfaces are implicitly public and are correctly handled
     * by this method.  Synthetic fields are considered irrelevant.
     * 
     * @param type the type containing the field: it is assumed that
     *      isRelevant(type) is true
     * @param field the field to test
     * @return true if the field is relevant to taming
     */    
    static boolean isRelevant(IType type, IField field) 
                                        throws JavaModelException {
        int typeFlags = type.getFlags();
        int flags = field.getFlags();
        return ((type.isInterface() || Flags.isPublic(flags) 
                 || !Flags.isFinal(typeFlags) && Flags.isProtected(flags))
                && !Flags.isSynthetic(flags));
    }
    

    /**
     * Check whether a field should be included in the taming database; i.e.
     * whether it may be visible to a type in another package.  This is true
     * if the field is public, or is protected and the type is non-final.
     * Members of interfaces are implicitly public and are correctly handled
     * by this method.  Synthetic methods and the class initializer 
     * "<clinit>" for binary types are considered irrelevant.
     * 
     * @param type the type containing the field: it is assumed that
     *      isRelevant(type) is true
     * @param field the field to test
     * @return true if the field is relevant to taming
     */
    static boolean isRelevant(IType type, IMethod method) 
                                        throws JavaModelException {
        int typeFlags = type.getFlags();
        int flags = method.getFlags();
        return ((type.isInterface() || Flags.isPublic(flags)
                 || !Flags.isFinal(typeFlags) && Flags.isProtected(flags))
                && !Flags.isSynthetic(flags)
                && !method.getElementName().equals("<clinit>"));
    }
    
    /**
     * Returns a flat signature for a method.  This includes the argument types
     * but not the return type or any thrown Exceptions.  The signature is 
     * "flat" in that all argument types are unqualified (including member
     * types).
     * @param method
     * @return the signature of the method
     */
    static String getFlatSignature(IMethod method) {
        StringBuilder flatSigBuilder = 
            new StringBuilder(method.getElementName() + "(");
        boolean first = true;
        for (String paramSig : method.getParameterTypes()) {
            if (first) {
                first = false;
            } else {
                flatSigBuilder.append(", ");
            }
            
            // Can replace $ with % to avoid turning them into dots and thus
            // preserve them.  Unfortunately, the $'s aren't always present to
            // begin with, so it appears to be a lost cause.
            //String readableType = 
            //    Signature.toString(paramSig.replace('$', '%'));
            String readableType = Signature.toString(paramSig);
            String flatReadableType = 
                readableType.replaceAll("[^<> ]*\\.([^<> \\.]*)", "$1");
            //flatSigBuilder.append(flatReadableType.replace('%', '$'));
            flatSigBuilder.append(flatReadableType);
        }
        // special handling for varargs?
        //if () {
        //    flatSigBuilder.replace(flatSigBuilder.length() - 2, 
        //                           flatSigBuilder.length(), "...");
        //}
        return flatSigBuilder.append(")").toString();
    }
    
    // map of file -> types for removing extra safejs
    // final HashMap<IFile, Set<IType>> types;
    final Map<IType, Entry> db;
    final IJavaProject project;
    // final ProjectSafeJBuild sjbuild;
    final IFile policyFile;
    
    /*
     *  Types with significance to the verifier.
     */
    // final IType OBJECT;
    // final IType ENUM;
    final IType IS_JOE_E;
    
    final IType SELFLESS;
    final IType IMMUTABLE;
    final IType POWERLESS;
    // final IType DATA;
    final IType EQUATABLE;
    final IType TOKEN;
       
    Taming(File persistentDB, IJavaProject project) throws CoreException {
        this.project = project;
        
        /* HUGE HACK! TEMPORARY! what to do here? */
        //SafeJBuild hack = new SafeJBuild(System.err, new File("/home/adrian/taming/safej-dump"));
        //hack.processLibraryPackage("java.util", project);
        
        /*
         * Project SafeJ Builder
         *
        if (ProjectProperties.isSafejOutputEnabled(project.getProject())){           
            IFolder tamingFolder = project.getProject().getFolder("taming");
            if (tamingFolder.exists()) {
                // clean up contents
                // TODO: don't clobber if we want custom taming decisions also!
                for (IResource ir : tamingFolder.members()) {
                    ir.delete(false, null);
                }
            } else { 
                // create taming folder
                tamingFolder.create(false, true, null);
                tamingFolder.setDerived(true);
            }
            sjbuild = new ProjectSafeJBuild(System.err, tamingFolder);
        } else {
            sjbuild = null;
        }
        */
        
        if (ProjectProperties.isPolicyOutputEnabled(project.getProject())) {
            policyFile = findPolicyFile();
        } else {
            policyFile = null;
        }
                
        /* 
         * Sanity check for Joe-E library
         */
        // The following may not be found if the Joe-E library is not reachable.
        IS_JOE_E = project.findType("org.joe_e.IsJoeE");
        
        SELFLESS = project.findType("org.joe_e.Selfless");
        IMMUTABLE = project.findType("org.joe_e.Immutable");
        POWERLESS = project.findType("org.joe_e.Powerless");
        // RECORD = project.findType("org.joe_e.Record");
        // DATA = project.findType("org.joe_e.Data");
        EQUATABLE = project.findType("org.joe_e.Equatable");   
        TOKEN = project.findType("org.joe_e.Token");     
        
        if (IS_JOE_E == null || SELFLESS == null || IMMUTABLE == null 
            || POWERLESS == null || EQUATABLE == null || TOKEN == null) {
            System.out.println("FATAL: Could not find Joe-E library classes!");
            throw new CoreException(
                new Status(IStatus.ERROR, Plugin.PLUGIN_ID, 10, 
                           "Joe-E library not found!\nThe Joe-E library " +
                           "classes (org.joe_e.*) must be in the project's " +
                           "build path\nin order to use the Joe-E verifier.", 
                           null));
        }
        
        // this.types = new HashMap<IFile, Set<IType>>();
        this.db = new HashMap<IType, Entry>();
        
        /*
         * Import project-external taming database
         */
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
        
        SafeJImport.importTaming(System.err, persistentDB, this, 
                                         project);
    }            
    
    IFile findPolicyFile() throws CoreException {
        /*
         * Project runtime taming policy file builder
         */
        IContainer srcContainer = null;
        IPackageFragmentRoot[] roots = project.getPackageFragmentRoots();
        for (IPackageFragmentRoot r : roots) {
            IResource resource = r.getCorrespondingResource();
            if (resource instanceof IFolder || resource instanceof IProject) {
                srcContainer = (IContainer) resource;
                break;
            }
        }
        
        if (srcContainer == null) {
            throw new CoreException(
                new Status(IStatus.ERROR, Plugin.PLUGIN_ID, 10, 
                           "Couldn't find source directory for output of " +
                           "org.joe_e.taming.Policy.", null));
        }
        
        IFolder current = srcContainer.getFolder(new Path("org"));       
        if (!current.exists()) {
            current.create(false, true, null);
            current.setDerived(true);
        } else {
            // in case user adds a org.something package
            if (current.members().length > 1) {
                current.setDerived(false);
            }
        }
        String[] restOfPath = new String[]{"joe_e", "taming"};
        for (String pkg : restOfPath) {
            current = current.getFolder(pkg);
            if (!current.exists()) {
                current.create(false, true, null);
                current.setDerived(true);
            }
        }
        
        return current.getFile("Policy.java");
    }
    
       
    class Entry {
        final boolean enabled;
        final String comment;
        final Map<IField, String> allowedFields;
        final Map<IMethod, String> allowedMethods;
        final Map<IField, String> disabledFields;
        final Map<IMethod, String> disabledMethods;
        final Set<IType> honoraries;
        // final Set<IType> deemings;        
        
        
        /**
         * Create an entry for an enabled class
         */
        Entry(String comment, Map<IField, String> allowedFields,
              Map<IMethod, String> allowedMethods,
              Map<IField, String> disabledFields,
              Map<IMethod, String> disabledMethods,
              Set<IType> honoraries /*, Set<IType> deemings*/) {
            enabled = true;
            this.comment = comment;
            this.allowedFields = allowedFields;
            this.allowedMethods = allowedMethods;
            this.disabledFields = disabledFields;
            this.disabledMethods = disabledMethods;
            this.honoraries = honoraries;
            // this.deemings = deemings;
        }
        
        /**
         * Create an entry for a Joe-E class
         */
        Entry(Map<IField, String> allowedFields,
              Map<IMethod, String> allowedMethods) {
              enabled = true;
              comment = null;
              this.allowedFields = allowedFields;
              this.allowedMethods = allowedMethods;
              disabledFields = null;
              disabledMethods = null;
              honoraries = null;
              // this.deemings = deemings;
        }
        
        /**
         * Create an entry for an explicitly disabled class
         */
        Entry(String comment) {
            enabled = false;
            this.comment = comment;
            allowedFields = disabledFields = null;
            allowedMethods = disabledMethods = null;
            honoraries = null;
        }
    }
    
    /*
     * Query methods on the database
     */
    
    Collection<IType> getHonorariesFor(IType type) {
        Entry e = db.get(type);
        if (e == null || e.honoraries == null) {
            return new LinkedList<IType>();
        } else {
            return e.honoraries;
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
    
    boolean isJoeE(ITypeBinding itb) {
        for (IAnnotationBinding iab : itb.getPackage().getAnnotations()) {
            if (iab.getAnnotationType().getQualifiedName()
                    .equals("org.joe_e.IsJoeE")) {
                return true;
            }
        }
        
        return false;
    }
    
    boolean isTamed(ITypeBinding itb) {
        if (Preferences.isTamingEnabled()) {
            Entry e = db.get((IType) itb.getJavaElement());
            return e != null && e.enabled;
        } else {
            return true;
        }
    }   
       
    boolean isAllowed(ITypeBinding classBinding, IVariableBinding fieldBinding) {
        if (Preferences.isTamingEnabled()) {
            Entry e = db.get((IType) classBinding.getJavaElement());
            IField field = (IField) fieldBinding.getJavaElement();
            return ((e != null) && e.allowedFields.containsKey(field));
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
            return ((e != null) && e.allowedMethods.containsKey(method));
        } else {
            return true;
        }    
    }

    /**
     * Get the class taming comment (if any) for a class.
     * @param itb
     * @return the comment, or <code>null</code> if no comment, or
     *      "no policy specified for this class" if no taming policy exists.
     */
    String getTamingComment(ITypeBinding itb) {
        Entry e = db.get((IType) itb.getJavaElement());
        if (e == null) {
            return "no policy specified for this class";
        } else {
            return e.comment;
        }
    }
    
    /**
     * Get the taming comment (if any) for a disabled field
     * @param classBinding the class containing the disabled field
     * @param fieldBinding the disabled field
     * @return the taming comment for the disabled field, or <code>null</code>
     *         if none.
     */
    String getTamingComment(ITypeBinding classBinding, 
                            IVariableBinding fieldBinding) {
        Entry e = db.get((IType) classBinding.getJavaElement());
        IField field = (IField) fieldBinding.getJavaElement();
        if (e == null || e.disabledFields == null) {
            return null;
        } else {
            return e.disabledFields.get(field);
        }
    }

    /**
     * Get the taming comment (if any) for a disabled method
     * @param classBinding the class containing the disabled method
     * @param methodBinding the disabled method
     * @return the taming comment for the disabled method, or <code>null</code>
     *         if none.
     */
    String getTamingComment(ITypeBinding classBinding, 
                            IMethodBinding methodBinding) {
        Entry e = db.get((IType) classBinding.getJavaElement());
        IMethod method = (IMethod) methodBinding.getJavaElement();
        if (e == null || e.disabledMethods == null) {
            return null;
        } else {
            return e.disabledMethods.get(method);
        }
    }    
    
    /*
     * Mutating methods on the database for use with classes in the project.
     * These also auto-generate safej files for the project.
     *
    void addType(IFile file, IType type) {
        Set<IType> typesFound = types.get(file);
        if (typesFound == null) {
            types.put(file, new HashSet<IType>());
        }
        types.get(file).add(type);
    }

    void removeTypesFor(IFile file) {
        if (types.containsKey(file)) {
            for (IType toRemove : types.get(file)) {
                db.remove(toRemove);
                // sjbuild.removeType(toRemove);
            }
        }
    }
    */
    
    /**
     * Add taming info about a type to the verifier's runtime database.
     * This is used to populate the Policy class.
     * 
     * Also write a safej for the type.
     * 
     * Both actions are conditional on whether the type is relevant for
     * taming (see taming.isRelevant(IType)); this method does nothing for
     * irrelevant types.
     * 
     * @param type the type to process
     * @throws JavaModelException probably a bug if it does
     */
    /*
    void processJoeEType(IType type) throws JavaModelException {
        if (isRelevant(type)) {      
            Map<IField, String> allowedFields = 
                new HashMap<IField, String>();
            Map<IMethod, String> allowedMethods = 
                new HashMap<IMethod, String>();

            for (IField f : type.getFields()) {
                if (Taming.isRelevant(type, f)) {
                    allowedFields.put(f, null);
                }
            }
        
            for (IMethod m : type.getMethods()) {
                if (Taming.isRelevant(type, m)) {
                    allowedMethods.put(m, null);
                }
            }
        
            db.put(type, new Entry(allowedFields, allowedMethods));
        
            // only if enabled
            if (sjbuild != null) {
                // TODO: handle state transition of this option better -- this
                // test requires clean build and reconstruction of this class
                // for the change to take effect
                sjbuild.processJoeEType(type);
            }
        }
    }
    */
    
    void outputRuntimeDatabase() {
        if (policyFile != null) {
            // TODO: handle state transition of this option better -- this
            // test requires clean build and reconstruction of this class
            // for the change to take effect
            PolicyWriter.write(db, policyFile);
        }
    }
}