// Copyright 2007 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e.eclipse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.IOException;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.core.*;

public class SafeJBuild {
    /*
    
    static File dir = new File("/home/adrian/taming/safej/java/lang");
    static String[] makeThese = {"java.lang.Object", "java.lang.String", 
                                 "java.lang.Byte", "java.lang.Short",
                                 "java.lang.Integer", "java.lang.Long",
                                 "java.lang.Float", "java.lang.Double",
                                 "java.lang.Short", "java.lang.Object",
                                 "java.lang.String", "java.lang.Short",
                                 "java.lang.Object", "java.lang.String",
                                 "java.lang.Short", "java.lang.Object",
                                 "java.lang.String", "java.lang.Short",
                                 "java.lang.Object", "java.lang.String",
                                 "java.lang.Short", "java.lang."};
    */
    private final File safejBase;
    private final PrintStream err;

    SafeJBuild(PrintStream err, File safejBase) {
        this.err = err;
        this.safejBase = safejBase;
    }
    
    public void processLibraryPackage(String makeForPackage, 
                                      IJavaProject project) {
        try {       
            for (IPackageFragment frag : project.getPackageFragments()) {
                if (frag.getElementName().equals(makeForPackage)) {
                    for (IClassFile classFile : frag.getClassFiles()) {
                        IType type = classFile.getType();
                        if (Taming.isRelevant(type)) {
                            process(classFile.getType(), false);
                        }
                    }
                }
            }
        } catch (JavaModelException jme) {
            jme.printStackTrace(err);
        }
    }
    
    public void processJoeEType(IType type) throws JavaModelException {
        process(type, true);
    }
    
    /**
     * SIDE-AFFECTS THE FILESYSTEM by creating the directory if it doesn't 
     * already exist!
     * @param type
     */
    private File directoryFor(IType type) throws FileNotFoundException {
        String pkg = type.getPackageFragment().getElementName();
        String[] parts = pkg.split("\\.");
        File currentDir = safejBase;
        for (String part : parts) {
            File next = new File(currentDir, part);
            if (!next.isDirectory() && !next.mkdir()) {
                err.println("ERROR: couldn't create directory " + next);
                throw new FileNotFoundException();
            }
            currentDir = next;
        }
        return currentDir;
    }
    
    private void process(IType type, boolean joeE) throws JavaModelException {
        File dir;
        try {
            dir = directoryFor(type);
        } catch (FileNotFoundException fnfe) {
            err.println("couldn't find safej output directory for " 
                        + type.getFullyQualifiedName());
            return;
        }   
        File outFile = new File(dir, type.getTypeQualifiedName() + ".safej");
        try { 
            outFile.createNewFile(); 
            
                // ignoring return value = replace if present?
            PrintStream out = new PrintStream(new FileOutputStream(outFile));
            if (joeE) {
                out.println("# auto-generated safej for Joe-E code: allow everything.");
            } else {
                out.println("# auto-generated safej: default deny everything");
            }
            out.println("class(\"" + type.getFullyQualifiedName() + "\"");
           
            Set<String> constructors = new TreeSet<String>();
            Set<String> staticMethods = new TreeSet<String>();
            Set<String> instanceMethods = new TreeSet<String>();            
            
            for (IMethod m : type.getMethods()) {
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
        } catch (IOException ioe) {
            err.println("failure on " + outFile.getName() + 
                        ": maybe file already exists?");
            ioe.printStackTrace(err);
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
