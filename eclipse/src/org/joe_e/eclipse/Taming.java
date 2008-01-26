// Copyright 2005-07 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e.eclipse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.ByteArrayInputStream;

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

public class Taming {
    /*
     * Static utility methods that are independent of any taming data
     */
    
    static boolean isRelevant(IType type) throws JavaModelException {
        int flags = type.getFlags();
        // TODO: is special handling needed for protected inner classes of
        // final classes?  Would anyone declare one (protected is equivalent to 
        // the default (package) protection in such a case)
        return (Flags.isPublic(flags) || Flags.isProtected(flags));
    }
    
    static boolean isRelevant(IType type, IField field) 
                                        throws JavaModelException {
        int typeFlags = type.getFlags();
        int flags = field.getFlags();
        return ((type.isInterface() || Flags.isPublic(flags) 
                 || !Flags.isFinal(typeFlags) && Flags.isProtected(flags))
                && !Flags.isSynthetic(flags));
    }
    
    static boolean isRelevant(IType type, IMethod method) 
                                        throws JavaModelException {
        int typeFlags = type.getFlags();
        int flags = method.getFlags();
        // The method must be (a) from an interface, thus implicitly public,
        // (b) public, or (c) a protected method from a non-final class in
        // order for taming to be relevant.  In addition, synthetic methods
        // and the <clinit> pseudomethod are not relevant.
        return ((type.isInterface() || Flags.isPublic(flags)
                 || !Flags.isFinal(typeFlags) && Flags.isProtected(flags))
                && !Flags.isSynthetic(flags)
                && !method.getElementName().equals("<clinit>"));
    }
    
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
    
    /*
     * Impossible to get fully qualified names here due to Eclipse sucking
     * IMethods only know the types of their arguments as declared, thus
     * usually just the simple type names.  Maybe could do it with a method
     * that resolves against the imports of the defining class or something.
     *
    static String getSignature(IMethod method) {
        return Signature.toString(method.getSignature(), 
                                  method.getElementName(), 
                                  null, true, false);
        StringBuilder flatSigBuilder = 
            new StringBuilder(method.getElementName() + "(");
        boolean first = true;
        for (String paramSig : method.getParameterTypes()) {
            if (first) {
                first = false;
            } else {
                flatSigBuilder.append(", ");
            }
            String parameterSimpleName = 
                Signature.getSignatureSimpleName(paramSig);
            flatSigBuilder.append(parameterSimpleName);
        }
        // special handling for varargs?
        //if () {
        //    flatSigBuilder.replace(flatSigBuilder.length() - 2, 
        //                           flatSigBuilder.length(), "...");
        //}
        return flatSigBuilder.append(")").toString();
    } */
    
    /*
     * TODO: too hard? is it necessary?
     *
    static String getOverrideSignature(IMethod method) {
        StringBuilder overrideSigBuilder = 
            new StringBuilder(method.getElementName() + "(");
        boolean first = true;
        for (String paramSig : method.getParameterTypes()) {
            if (first) {
                first = false;
            } else {
                overrideSigBuilder.append(", ");
            }
            String parameterQualifiedName = 
                getQualifiedErasure(paramSig, method);
            overrideSigBuilder.append(parameterQualifiedName);
        }
        // special handling for varargs?
        //if () {
        //    flatSigBuilder.replace(flatSigBuilder.length() - 2, 
        //                           flatSigBuilder.length(), "...");
        //}
        return overrideSigBuilder.append(")").toString();
    }
       
    static String getQualifiedErasure(String paramSig, IMethod method) {
        ITypeParameter[] itps = method.getTypeParameters();       
        if (Signature.getTypeSignatureKind(paramSig) == 
            Signature.TYPE_VARIABLE_SIGNATURE) {
            for (ITypeParameter itp : itps) {
                if (itp.getElementName().equals(
                        Signature.getTypeVariable(paramSig))) {
                    String[] bounds = itp.getBounds();
                    if (bounds.length > 0) {
                        return bounds[0];
                    }
                    return "java.lang.Object";
                }
            }
            String[][] genericType = 
                method.getDeclaringType().resolveType(
                        Signature.getTypeVariable(paramSig));
            IType t = method.getJavaProject().findType(
                    genericType[0][0], genericType[0][1]);
            
            
            throw new AssertionError(); // shouldn't happen
        } else {
            return Signature.getSignatureQualifier(paramSig) + "."
                   Signature.getSignatureSimpleName(paramSig);
        }
    }
    */
    
    final HashMap<IFile, Set<IType>> types;
    final HashMap<IType, Entry> db;
    final IJavaProject project;
    final ProjectSafeJBuild sjbuild;
    final IFile policyFile;
    
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
        
        /* HUGE HACK! TEMPORARY! what to do here? */
        SafeJBuild hack = new SafeJBuild(System.err, new File("/home/adrian/taming/safej-dump"));
        hack.processLibraryPackage("java.io", project);
        
        /*
         * Project SafeJ Builder
         */
        IFolder tamingFolder = project.getProject().getFolder("taming");
        if (tamingFolder.exists()) {
            // clean up contents
            for (IResource ir : tamingFolder.members()) {
                ir.delete(false, null);
            }
        } else {
            // create taming folder
            tamingFolder.create(false, true, null);
        }
        
        sjbuild = new ProjectSafeJBuild(System.err, tamingFolder);
        
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
        }
        String[] restOfPath = new String[]{"joe_e", "taming"};
        for (String pkg : restOfPath) {
            current = current.getFolder(pkg);
            if (!current.exists()) {
                current.create(false, true, null);
            }
        }
        
        policyFile = current.getFile("Policy.java");
        
        /* 
         * Sanity check for Joe-E library
         */
        // The following may not be found if the Joe-E library is not reachable.
        SELFLESS = project.findType("org.joe_e.Selfless");
        IMMUTABLE = project.findType("org.joe_e.Immutable");
        POWERLESS = project.findType("org.joe_e.Powerless");
        // RECORD = project.findType("org.joe_e.Record");
        // DATA = project.findType("org.joe_e.Data");
        EQUATABLE = project.findType("org.joe_e.Equatable");   
        TOKEN = project.findType("org.joe_e.Token");     
        
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
        
        this.types = new HashMap<IFile, Set<IType>>();
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
    
    private boolean isFromProject(ITypeBinding itb) {
        return (itb.isFromSource() &&
                itb.getJavaElement().getJavaProject().equals(project));
    }
    
    boolean isJoeE(ITypeBinding itb) {
        if (isFromProject(itb)) {
            IContainer container = 
                itb.getJavaElement().getResource().getParent();
            return TogglePackageAction.isJoeE(container);
        } else {
            return false;
        }
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
     */
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
                sjbuild.removeType(toRemove);
            }
        }
    }
    
    void processJoeEType(IType type) throws JavaModelException {
        Map<IField, String> allowedFields = new HashMap<IField, String>();
        Map<IMethod, String> allowedMethods = new HashMap<IMethod, String>();

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
        
        sjbuild.processJoeEType(type);
    }
    
    void outputRuntimeDatabase() {
        ByteArrayOutputStream content = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(content);
        out.println("// This file is auto-generated by the Joe-E builder " +
                    "based on the taming");
        out.println("// database (safej files), and should not be edited " +
                    "directly.");
        out.println("package org.joe_e.taming;");
        out.println();
        out.println("import java.util.HashMap;");        
        out.println("import java.util.HashSet;");
        //out.println("import org.joe_e.*;");
        out.println();        
        out.println("public class Policy {");
        out.println("    private Policy() {}");
        out.println();
        out.println("    private static HashMap<String, String[]> honoraries = ");       
        out.println("        new HashMap<String, String[]>();");      
        out.println("    private static HashSet<String> fields = " 
                    + "new HashSet<String>();");
        out.println("    private static HashSet<String> constructors = "
                    + "new HashSet<String>();");
        out.println("    private static HashSet<String> methods = "
                    + "new HashSet<String>();");
        out.println();
        out.println("    public static boolean hasHonorary(String type, " +
                                                          "String honorary) {");
        out.println("        if (honoraries.containsKey(type)) {");
        out.println("            for (String hon : honoraries.get(type)) {");
        out.println("                if (hon.equals(honorary)) {");
        out.println("                    return true;");
        out.println("                }");
        out.println("            }");
        out.println("        }");
        out.println("        return false;");
        out.println("    }");
        out.println();
        out.println("    public static boolean fieldEnabled(String " + 
                                                           "fieldSig) {");
        out.println("        return fields.contains(fieldSig);");
        out.println("    }");
        out.println();
        out.println("    public static boolean constructorEnabled(String " +
                                                                 "ctorSig) {");
        out.println("        return constructors.contains(ctorSig);");
        out.println("    }");
        out.println();
        out.println("    public static boolean methodEnabled(String " +
                                                            "methodSig) {");
        out.println("        return methods.contains(methodSig);");
        out.println("    }");
        out.println();
        out.println("    static {");        
        
        boolean firstType = true;
        for (IType type : db.keySet()) {
            Entry e = db.get(type);
            String fqn = type.getFullyQualifiedName();
            
            out.println((firstType ? "" : "\n") + "        // Type " + fqn);
            firstType = false;
            
            Set<IType> honoraries = e.honoraries;
            if (honoraries != null && !honoraries.isEmpty()) {
                //out.print("        honoraries.add(" + fqn + 
                //          ".class, new Class<?>[]{");
                out.print("        honoraries.put(\"" + fqn + 
                          "\", new String[]{");
                boolean firstHon = true;
                for (IType hon : honoraries) {
                    out.print(firstHon ? "" : ", ");
                    firstHon = false;
                    out.print("\"" + hon.getFullyQualifiedName() + "\"");
                }
                out.println("});");
            }
            
            Map<IField, String> fields = e.allowedFields;
            if (fields != null) {
                for (IField f : fields.keySet()) {
                    out.println("        fields.add(\"" + fqn + "." + 
                                f.getElementName() + "\");");
                }
            }
            
            Map<IMethod, String> methods = e.allowedMethods;
            if (methods != null) {
                for (IMethod m : methods.keySet()) {
                    try {
                        if (m.isConstructor()) {
                            String flatSig = getFlatSignature(m);
                            flatSig = flatSig.substring(flatSig.indexOf('('));
                            out.println("        constructors.add(\"" + fqn +
                                        flatSig + "\");");
                        }
                    } catch (JavaModelException jme) {
                        jme.printStackTrace(System.err);
                        return;
                    }
                }
                for (IMethod m : methods.keySet()) {
                    try {
                        if (!m.isConstructor()) {
                            out.println("        methods.add(\"" + fqn + 
                                        "." + getFlatSignature(m) + "\");");
                        }
                    } catch (JavaModelException jme) {
                        jme.printStackTrace(System.err);
                        return;
                    }
                }
            }
        }
        
        out.println("    }"); // end static {
        out.println("}"); // end class {
        
        ByteArrayInputStream stream = new ByteArrayInputStream(content.toByteArray());
        try {
            if (policyFile.exists()) {
                policyFile.setContents(stream, false, true, null);
            } else {
                policyFile.create(stream, false, null);
            }
            policyFile.setDerived(true);
        } catch (CoreException ce) {
            System.err.println("couldn't write policy output file!");
            ce.printStackTrace(System.err);
        }
    }
}