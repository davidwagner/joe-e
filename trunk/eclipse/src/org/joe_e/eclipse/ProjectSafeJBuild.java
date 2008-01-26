// Copyright 2007 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e.eclipse;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.CoreException;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.core.*;

/*
 * A frozen class that holds references to the IFolder for storing safej files
 * and the error stream, and which provides methods to build safej.
 */
public class ProjectSafeJBuild {   
    private final IFolder safejBase;
    private final PrintStream err;

    /**
     * The SafeJBuild object exists to keep track of the project's safej
     * directory and the error stream.
     * @param err       a stream for error reports/debugging
     * @param safejBase the location to output generated safej files
     */
    ProjectSafeJBuild(PrintStream err, IFolder safejBase) {
        this.err = err;
        this.safejBase = safejBase;
    }
    
    /**
     * Process a Joe-E type and place the output in the safej output
     * directory.  Modifies the project.
     * @param type      the type to process
     */
    public void processJoeEType(IType type) throws JavaModelException {
        process(type, true);
    }
    
    public void removeType(IType type) {
        try {
            IFolder dir = directoryFor(type.getPackageFragment(), true);
            IFile outFile = dir.getFile(type.getTypeQualifiedName() + ".safej");
            outFile.delete(false, null);
        } catch (CoreException ce) {
            err.println("couldn't delete safej file for " 
                        + type.getFullyQualifiedName());
            return;
        }
    }
    
    /**
     * remove all safej for a specified file, if it belongs to a
     * compiled package.
     * @param type
     *
    public void removeTypesFor(IFile file) {
        IFolder dir = null;       
        IContainer parent = file.getParent();
        
        try {
            if (parent instanceof IFolder) {
                IJavaElement frag = JavaCore.create((IFolder) parent);
                if (frag == null) {
                    return;
                }
                dir = directoryFor((IPackageFragment) frag);
            } else {
                IPath parentPath = (file.getFullPath().removeLastSegments(1));
                IJavaProject proj = JavaCore.create((IProject) parent);
                if (proj.findPackageFragmentRoot(parentPath) == null) {
                    return;
                }
                dir = safejBase;
            } 
        
            String typeName = file.getName();
            typeName = typeName.substring(0, typeName.indexOf('.'));
        
            for (IResource ir : dir.members()) {
                if (ir instanceof IFile) {
                    String name = ir.getName();
                    if (name.equals(typeName + ".safej")
                        || name.startsWith(typeName + "$")
                           && name.endsWith(".safej")) {
                        ir.delete(false, null);
                    }
                }
            }
        } catch (CoreException ce) {
            err.println("ERROR: couldn't list and/or delete safej files from "
                        + dir);
            ce.printStackTrace(err);
        }
    }
    */
    
    /**
     * If create is true, 
     * SIDE-AFFECTS THE PROJECT by creating the directory if it doesn't 
     * already exist!
     * @param pf        the package to locate the directory for
     * @param create    whether to create directory if it doesn't exist
     */
    private IFolder directoryFor(IPackageFragment pf, boolean create) throws CoreException {
        String pkg = pf.getElementName();
        String[] parts = pkg.split("\\.");
        IFolder currentDir = safejBase;
        for (String part : parts) {
            IFolder next = currentDir.getFolder(part);
            if (!next.exists()) {
                if (create) {
                    next.create(false, true, null);
                    next.setDerived(true);
                } else {
                    return null;
                }
            }
            currentDir = next;
        }
        return currentDir;
    }
    
    private void process(IType type, boolean joeE) throws JavaModelException {
        if (!Taming.isRelevant(type)) {
            return;
        }
        
        IFolder dir;
        try {
            dir = directoryFor(type.getPackageFragment(), true);
        } catch (CoreException ce) {
            err.println("couldn't find safej output directory for " 
                        + type.getFullyQualifiedName());
            return;
        }
        IFile outFile = dir.getFile(type.getTypeQualifiedName() + ".safej");
        
        ByteArrayOutputStream content = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(content);
        
        if (joeE) {
            out.println("# auto-generated safej for Joe-E code: allow everything.");
        } else {
            out.println("# auto-generated safej: default deny everything");
        }
        
        out.print("class(\"" + type.getFullyQualifiedName() + "\"");
          
        Set<String> constructors = new TreeSet<String>();
        Set<String> staticMethods = new TreeSet<String>();
        Set<String> instanceMethods = new TreeSet<String>();            
        
        boolean defaultConstructor = 
            type.isClass() && Flags.isPublic(type.getFlags());
        for (IMethod m : type.getMethods()) {
            if (m.isConstructor()) {
                defaultConstructor = false;
            }
            
            int flags = m.getFlags();
            if (Taming.isRelevant(type, m)) {
                if (m.isConstructor()) {
                    constructors.add(Taming.getFlatSignature(m));
                } else if (Flags.isStatic(flags)) {
                    staticMethods.add(Taming.getFlatSignature(m));
                } else {
                    instanceMethods.add(Taming.getFlatSignature(m));
                }
            }
        }
        
        if (defaultConstructor) {
            constructors.add(type.getTypeQualifiedName() + "()");
        }
            
        Set<String> staticFields = new TreeSet<String>();
        Set<String> instanceFields = new TreeSet<String>();
        for (IField f : type.getFields()) {
            int flags = f.getFlags();
            if (Taming.isRelevant(type, f)) {
                if (Flags.isStatic(flags)) {
                    staticFields.add(f.getElementName());
                } else {
                    instanceFields.add(f.getElementName());
                }
            }
        }
            
        if (!staticFields.isEmpty() || !constructors.isEmpty()
            || !staticMethods.isEmpty()) {
            out.print(",\n  static(");
            boolean first = true;
            for (String f : staticFields) {
                first = printMember(out, first, "field", f, joeE);
            }
            for (String c : constructors) {
                first = printMember(out, first, "constructor", c, joeE);
            }
            for (String m : staticMethods) {
                first = printMember(out, first, "method", m, joeE);
            }
            out.print(")");
        }

        if (!instanceMethods.isEmpty() || !instanceFields.isEmpty()) {
            out.print(",\n  instance(");
            boolean first = true;
            for (String f : instanceFields) {
                first = printMember(out, first, "field", f, joeE);
            }
            for (String m : instanceMethods) {
                first = printMember(out, first, "method", m, joeE);
            }
            out.print(")");
        }

        out.println(")");             // close class(
        
        ByteArrayInputStream stream = new ByteArrayInputStream(content.toByteArray());
        try {
            if (outFile.exists()) {
                outFile.setContents(stream, false, true, null);
            } else {
                outFile.create(stream, false, null);
            }
            outFile.setDerived(true);
        } catch (CoreException ce) {
            err.println("couldn't write safej output for " 
                    + type.getFullyQualifiedName());
            ce.printStackTrace(err);
        }
    }    
    
    boolean printMember(PrintStream out, boolean first, String kind,
                        String member, boolean joeE) {
        if (first) {
            first = false;
        } else {
            out.print(",\n    ");
        }
        if (joeE) {
            out.print(kind + "(\"" + member + "\")");
        } else {
            out.print(kind + "(suppress, \"" + member +
                      "\", comment(\"default deny\"))");           
        }
        return first;
    }
}
