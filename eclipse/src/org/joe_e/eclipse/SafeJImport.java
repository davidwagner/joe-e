// Copyright 2007-08 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e.eclipse;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
//import java.util.Arrays;

import java.io.PrintStream;
import java.io.File;

import org.eclipse.jdt.core.*;
import org.joe_e.safej.SafeJConsumer;
import org.joe_e.safej.SafeJParse;

//import org.joe_e.eclipse.SafeJConsumer.Member;
//import org.joe_e.eclipse.SafeJConsumer.Kind;
//import org.joe_e.eclipse.SafeJParse.ParseFailure;

public class SafeJImport {
    static class ImportFailure extends Exception {
        static final long serialVersionUID = 1;
    }
    
    static void importTaming(PrintStream err, File dir, Taming taming,
                             IJavaProject project) throws JavaModelException {
        new Consumer(err, dir, taming, project).process();
        err.println("Taming database imported.");
        /* Hack: uncomment to export safej files for a package
        SafeJBuild sjb = new SafeJBuild(System.err, new File("/tmp"));
        sjb.processLibraryPackage("java.util", project);
        */
    }
   
    static class Consumer implements SafeJConsumer {
        PrintStream err;
        File dir;
        Taming taming;
        IJavaProject project;
        
        // used for consistency check
        HashMap<IType, List<IType>> subtypes;
        HashMap<IType, Set<IType>> interfaces;
        
        Consumer(PrintStream err, File dir, Taming taming, 
                 IJavaProject project) {
            this.err = err;
            this.dir = dir;
            this.taming = taming;
            this.project = project;
        }
        
        
        void process() throws JavaModelException {
            taming.db.clear();
            
            // Do the files
            new SafeJParse(dir, err, this);
            
            //
            // check consistency
            //
            
            // Build database of subtypes and interfaces
            subtypes = new HashMap<IType, List<IType>>(); 
                // all tamed classes that have this class as their closest 
                // tamed ancestor
            interfaces = new HashMap<IType, Set<IType>>();
                // all interfaces implemented by this class except possibly
                // some of those implemented by its nearest tamed ancestor
            
            for (IType type : taming.db.keySet()) {
                
                /*if (type.isInterface()) {
                    continue;
                }*/
                
                ITypeHierarchy sth = type.newSupertypeHierarchy(null);
                IType superclass = sth.getSuperclass(type);              
                
                // This queue will contain the current type and non-tamed
                // supertypes
                Queue<IType> left = new LinkedList<IType>();
                left.add(type);
                
                // Determine nearest tamed superclass and add intermediate
                // superclasses to 'left'
                while (superclass != null 
                       && !taming.db.containsKey(superclass)) {
                    left.add(superclass);
                    warn("Type " + type.getFullyQualifiedName() +
                         "'s superclass " + superclass.getElementName() + 
                         " is not in taming database.");
                    superclass = sth.getSuperclass(superclass);
                }

                if (superclass != null) {
                    // Record superclass
                    List<IType> superEntry = subtypes.get(superclass);
                    if (superEntry == null) {
                        superEntry = new LinkedList<IType>();
                        subtypes.put(superclass, superEntry);
                    }
                    superEntry.add(type);
                   
                    // Inherited honoraries check: subclass must honorarily or
                    // actually implement any honorary interfaces from superclass
                    checkHonoraries(superclass, type, sth);                
                }
                
                // Also, must implement honoraries implemented by nearest
                // tamed superinterfaces.
                Queue<IType> superInterfaces = new LinkedList<IType>();
                for (IType ancestor : left) {
                    for (IType iface : sth.getSuperInterfaces(ancestor)) {
                        superInterfaces.add(iface);
                    }
                }
                
                while (!superInterfaces.isEmpty()) {
                    IType current = superInterfaces.remove();                   
                    if (taming.db.containsKey(current)) {
                        checkHonoraries(current, type, sth);
                    } else {
                        warn("Type " + type.getFullyQualifiedName() +
                             "'s superinterface " + current.getElementName() +
                             " is not in taming database.");
                        for (IType iface : sth.getSuperInterfaces(current)) {
                            superInterfaces.add(iface);
                        }
                    } 
                }
                
                if (!type.isInterface()) {
                    // Record all tamed interfaces implemented (possibly
                    // transitively) by this class or a non-tamed superclass
                    // (not by way of the nearest tamed superclass).
                    Set<IType> interfacesEntry = new HashSet<IType>();
                    while (!left.isEmpty()) {
                        IType current = left.remove();
                        for (IType i : sth.getSuperInterfaces(current)) {
                            if (taming.db.containsKey(i)) {
                                interfacesEntry.add(i);
                            }
                            left.add(i);
                        }
                    }          
                    interfaces.put(type, interfacesEntry);
                }
            }
            
            // Perform method inheritance check
            checkInheritance(project.findType("java.lang.Object"), 
                             new HashSet<IMethod>());
        }

        /**
         * Check that honorary interfaces declared on a public supertype
         * (superclass or superinterface) are also declared for the subtype.
         * 
         * @param supertype the supertype whose honoraries must be implemented
         * @param subtype the subtype being checked
         */
        void checkHonoraries(IType supertype, IType subtype, ITypeHierarchy sth) {
            int uninherited = taming.db.get(supertype).honoraries 
                              & ~taming.db.get(subtype).honoraries;
            
            for (IType honorary : taming.detag(uninherited)) {
                if (!sth.contains(honorary)) {
                    err.println("ERROR: type " + subtype.getFullyQualifiedName()
                                + " does not inherit honorary interface " + 
                                honorary.getElementName() + " from supertype "
                                + supertype.getElementName() + ".");
                }
            }
        }
        
        /**
         * Check that instance methods enabled in superclasses or
         * superinterfaces are enabled if defined in subclass.
         * 
         * @param current class to check, and all its subclasses
         * @param methods methods enabled in superclasses
         */
        //TODO: make sure this does the right thing for abstract classes!
        // current code doesn't work; would rely on "miranda methods" from the compiler?
        void checkInheritance(IType current, Set<IMethod> methods) throws JavaModelException {
            // check methods for this class
            // keep track of non-overriden methods to propagate to subclasses            
            Map<IMethod, String> enabled = taming.db.get(current).allowedMethods;            
            Set<IMethod> propagate = new HashSet<IMethod>();
            // local copy so we don't contaminate siblings
            Set<IMethod> methodsNeeded = new HashSet<IMethod>(methods);
            for (IType i : interfaces.get(current)) {
                Map<IMethod, String> iEnabled = taming.db.get(i).allowedMethods;
                for (IMethod m : iEnabled.keySet()) {
                    if (!Flags.isStatic(m.getFlags())) {
                        methodsNeeded.add(m);
                    }
                }
            }
            
            IMethod[] curMethods = current.getMethods();
            HashMap<String, IMethod> relevantMethods = new HashMap<String, IMethod>();
            for (IMethod m : curMethods) {
                if (!m.isConstructor() && Taming.isRelevant(current, m)) {
                    relevantMethods.put(Taming.getFlatSignature(m), m);
                }
            }
            
            for (IMethod m : methodsNeeded) {
                // TODO: may be broken for generics due to erasure?
                // TEST!
                IMethod overrider = relevantMethods.get(Taming.getFlatSignature(m));
                
                if (overrider == null) {
                    propagate.add(m);
                } else if (!enabled.containsKey(overrider)) {
                    err.println("ERROR: Type " +
                                overrider.getDeclaringType().getFullyQualifiedName()
                                + " suppresses method " + Taming.getFlatSignature(m)
                                + " enabled in supertype " +
                                m.getDeclaringType().getElementName());
                }
            }
            
            if (enabled != null) {
                for (IMethod m : enabled.keySet()) {
                    if (m != null && !Flags.isStatic(m.getFlags())) {
                        propagate.add(m);
                    }
                }
            }
            
            // recur on subclasses
            if (subtypes.containsKey(current)) {
                for (IType subtype : subtypes.get(current)) {
                    checkInheritance(subtype, propagate);
                }
            }
        }
        
        public void consumeClass(final String className, String comment, 
                                 List<String> honoraries, 
                                 List<Member> staticMembers,
                                 List<Member> instanceMembers) {
            final Map<String, IField> relevantFields = 
                new HashMap<String, IField>();
            final Map<String, IMethod> relevantMethods = 
                new HashMap<String, IMethod>();
            final Map<String, IMethod> relevantConstructors = 
                new HashMap<String, IMethod>();

            final Map<IMethod, String> allowedMethods = 
                new HashMap<IMethod, String>();
            final Map<IField, String> allowedFields = 
                new HashMap<IField, String>();
            final Map<IMethod, String> disabledMethods = 
                new HashMap<IMethod, String>();
            final Map<IField, String> disabledFields =
                new HashMap<IField, String>();
                      
            class MemberProcessor {
                private void failImporting(String problem) throws ImportFailure {
                    err.println( "ERROR: Importing " + className + " failed: " + problem);
                    throw new ImportFailure();
                }
                
                void processMember(Member m, boolean isStatic) 
                                    throws JavaModelException, ImportFailure {
                    switch (m.kind) {
                    case FIELD:
                        IField field = relevantFields.get(m.identifier);
                        relevantFields.remove(m.identifier);
                        
                        if (field == null) {
                            failImporting("Unknown or duplicate field " + 
                                          m.identifier);
                        } else {
                            boolean actuallyStatic = 
                                Flags.isStatic(field.getFlags())
                                || field.isEnumConstant();                     
                            
                            if (isStatic && !actuallyStatic) {
                                failImporting("Instance field " + m.identifier +
                                              " in static member list");
                            } else if (!isStatic && actuallyStatic) {
                                failImporting("Static field " + m.identifier +
                                              " in instance member list");
                            }
                        
                            if (m.allowed) {
                                allowedFields.put(field, m.comment);
                            } else if (m.comment != null) {
                                disabledFields.put(field, m.comment);
                            }
                        }
                        break;
                         
                    case CONSTRUCTOR:
                        IMethod ctor = relevantConstructors.get(m.identifier);
                        if (!relevantConstructors.containsKey(m.identifier)) {
                            failImporting("Unknown or duplicate " + 
                                          "constructor " + m.identifier);
                        } else if (!isStatic) {
                            failImporting("Constructor in instance " +
                                          "member list");
                        }
                        
                        relevantConstructors.remove(m.identifier);
                        
                        if (m.allowed) {
                            allowedMethods.put(ctor, m.comment);
                        } else if (m.comment != null) {
                            disabledMethods.put(ctor, m.comment);
                        }
                        break;
                          
                    case METHOD:
                        IMethod method = relevantMethods.get(m.identifier);
                        relevantMethods.remove(m.identifier);
                        
                        if (method == null) {
                            failImporting("Unknown or duplicate method "
                                          + m.identifier);
                        } else if (isStatic 
                                   && !Flags.isStatic(method.getFlags())) {
                            failImporting("Instance method " + m.identifier +
                                          " in static member list");
                        } else if (!isStatic
                                   && Flags.isStatic(method.getFlags())) {
                            failImporting("Static method " + m.identifier + 
                                          "in instance member list");
                        }
                        
                        if (m.allowed) {
                            allowedMethods.put(method, m.comment);
                        } else if (m.comment != null) {
                            disabledMethods.put(method, m.comment);
                        }
                        break;
                    }                   
                }
            }
            
            try {
                int lastDot = className.lastIndexOf('.');
                String packageName = className.substring(0, lastDot);
                String typeQualifiedName = 
                    className.substring(lastDot + 1).replace('$', '.');
                IType type = project.findType(packageName, typeQualifiedName);

                if (type == null || !type.exists()) {
                    warn("Type " + className + " not found!");
                    throw new ImportFailure();
                }
                                      
                IField[] typeFields = type.getFields();
                IMethod[] typeMethods = type.getMethods();
                        
                // Mappings of identifier -> member, by type
                for (IField f : typeFields) {
                    if (Taming.isRelevant(type, f)) {
                        relevantFields.put(f.getElementName(), f);
                    }
                }

                boolean explicitConstructor = false;
                
                for (IMethod m : typeMethods) {
                    if (m.isConstructor()) {
                        explicitConstructor = true;
                        if (Taming.isRelevant(type, m)) {
                            relevantConstructors.put(Taming.getFlatSignature(m), m);
                        }
                    } else {
                        if (Taming.isRelevant(type, m)) {
                            relevantMethods.put(Taming.getFlatSignature(m), m);
                        }
                    }
                }

                if (!explicitConstructor && type.isClass()) {
                    // requesting the IJavaElement for an implicit constructor
                    // in a source class returns null
                    relevantConstructors.put(typeQualifiedName + "()", 
                                             null);
                }
                
                MemberProcessor mp = new MemberProcessor();
                for (Member m : staticMembers) {
                    mp.processMember(m, true);
                }
                
                for (Member m : instanceMembers) {
                    mp.processMember(m, false);
                }
                
                int honoraryTypes = 0;
                for (String s : honoraries) {
                    IType honorary = project.findType(s);
                    honoraryTypes |= taming.tag(honorary);
                }
                
                /*
                IType[] normalHonoraries = new IType[] {taming.IMMUTABLE,
                    taming.SELFLESS, taming.EQUATABLE};
                honoraries: for (String s : honoraries) {
                    if (s.equals(taming.POWERLESS.getFullyQualifiedName())) {
                        honoraryTypes.add(taming.POWERLESS);
                        honoraryTypes.add(taming.IMMUTABLE);
                    } else {
                        for (IType normalHon : normalHonoraries) {
                            if (s.equals(normalHon.getFullyQualifiedName())) {
                                honoraryTypes.add(normalHon);
                                continue honoraries; 
                            }
                        }
                        err.println("Unknown honorary " + s);
                    }
                }
                */
                
                taming.db.put(type, taming.new Entry(comment, allowedFields,
                                        allowedMethods, disabledFields, 
                                        disabledMethods, honoraryTypes));
                        
                for (String f : relevantFields.keySet()) {                  
                    warn("Field " + className + "." + f + " not mentioned " +
                         "in safej.");
                }
                for (String c : relevantConstructors.keySet()) {
                    warn("Constructor " + packageName + "." + c + 
                         " not mentioned in safej.");
                }
                for (String m : relevantMethods.keySet()) {
                    warn("Method " + className + "." + m + " not mentioned " + 
                         "in safej.");
                }
            } catch (JavaModelException jme) {
                jme.printStackTrace(err);
                return;
            } catch (ImportFailure i) {
                return;
            }
        }
        
        public void disabledClass(String className, String comment) {
            try {
                IType type = project.findType(className);
         
                if (type == null) {
                    warn("Type " + className + " not found!");
                    throw new ImportFailure();
                }
                
                taming.db.put(type, taming.new Entry(comment));
            } catch (JavaModelException jme) {
                jme.printStackTrace(err);
                return;
            } catch (ImportFailure i) {
                return;
            }
        }
        
        /*
            
            for (IField f : typeFields) {                   
                try {
                    if (!Taming.isRelevant(type, f)) {
                        continue;
                    }
                } catch (JavaModelException jme) {
                    throw new AssertionError();
                }
                
                String fieldName = f.getElementName();
                                    
                if (membersAllowed.contains(fieldName)) {
                    membersAllowed.remove(fieldName);
                    allowedFields.add(f);
                } else if (membersDenied.contains(fieldName)) {
                    membersDenied.remove(fieldName);
                } else {
                    err.println("WARNING: Field " + fieldName + 
                                " not mentioned in safej.");
                }
            }          
            
            for (IMethod m : typeMethods) {
                try {
                    if (!Taming.isRelevant(type, m)) {
                        continue;
                    }
                } catch (JavaModelException jme) {
                    throw new AssertionError();
                }
                
                StringBuilder flatSigBuilder = 
                    new StringBuilder(m.getElementName() + "(");
                boolean first = true;
                for (String paramSig : m.getParameterTypes()) {
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
                String flatSig = Taming.getFlatSignature(m);
                
                if (!flatSigsSeen.add(flatSig)) {
                    err.println("ERROR: Multiple methods have matching " +
                                "flat signatures!");
                }
                                    
                if (membersAllowed.contains(flatSig)) {
                    membersAllowed.remove(flatSig);
                    allowedMethods.add(m);
                } else if (membersDenied.contains(flatSig)) {
                    membersDenied.remove(flatSig);
                } else {
                    err.println("WARNING: Method " + flatSig + 
                                " not mentioned in safej.");
                }
                
                // TODO: honoraries!
                IType[] normalHonoraries = new IType[] {taming.IMMUTABLE,
                    taming.SELFLESS, taming.EQUATABLE};
                honoraries: for (String s : honoraries) {
                    if (s.equals(taming.POWERLESS.getFullyQualifiedName())) {
                        honoraryTypes.add(taming.POWERLESS);
                        honoraryTypes.add(taming.IMMUTABLE);
                    } else {
                        for (IType normalHon : normalHonoraries) {
                            if (s.equals(normalHon.getFullyQualifiedName())) {
                                honoraryTypes.add(normalHon);
                                continue honoraries; 
                            }
                        }
                        err.println("Unknown honorary " + s);
                    }
                }
                
                taming.db.put(type, taming.new Entry(
                                allowedMethods, allowedFields, honoraryTypes));
            }
            
            
            for (String s : membersAllowed) {
                err.println("ERROR: Method " + s + " allowed in safej " + 
                            "not found in class.");
            }
            for (String s : membersDenied) {
                err.println("ERROR: Method " + s + " disabled in safej " + 
                "not found in class, or also allowed.");
            }
        }
        */
     
        private void warn(String problem) {
            if (Preferences.isDebugEnabled()) {
                err.println("WARNING: " + problem);
            }
        }
    }
}
