// Copyright 2007 Regents of the University of California.  May be used 
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
                    err.println("WARNING: type " + type.getElementName() +  
                                "'s superclass " + superclass.getElementName()
                                + " is not in taming database.");
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
                    checkHonoraries(superclass, type);                
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
                        checkHonoraries(current, type);
                    } else {
                        err.println("WARNING: type " + type.getElementName() +
                                    "'s superinterface " + 
                                    current.getElementName() + 
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
        void checkHonoraries(IType supertype, IType subtype) {
            for (IType honorary : taming.db.get(supertype).honoraries) {
                if (!taming.db.get(subtype).honoraries.contains(honorary)) {
                    err.println("ERROR: type " + subtype.getElementName() +
                                " does not inherit honorary interface " + 
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
                      
            for (IMethod m : methodsNeeded) {
                // TODO: may be broken for generics due to erasure?
                // TEST!
                IMethod overrider = current.getMethod(m.getElementName(),
                                                      m.getParameterTypes());
                if (!overrider.exists()) {
                    propagate.add(m);
                } else if (!enabled.containsKey(overrider)) {
                    err.println("ERROR: Enabled method " + m + " overriden " +
                                "by disabled method " + overrider);
                } 
            }
            
            for (IMethod m : enabled.keySet()) {
                if (!Flags.isStatic(m.getFlags())) {
                    propagate.add(m);
                }
            }
            
            // recur on subclasses
            if (subtypes.containsKey(current)) {
                for (IType subtype : subtypes.get(current)) {
                    checkInheritance(subtype, propagate);
                }
            }
        }
        
        public void consumeClass(String className, String comment, 
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
                void processMember(Member m, boolean isStatic) 
                                    throws JavaModelException, ImportFailure {
                    switch (m.kind) {
                    case FIELD:
                        IField field = relevantFields.get(m.identifier);
                        relevantFields.remove(m.identifier);
                        
                        if (field == null) {
                            failImporting("Error: unknown or duplicate field " + 
                                          m.identifier);
                        } else if (isStatic 
                                   && !Flags.isStatic(field.getFlags())) {
                            failImporting("Instance field " + m.identifier +
                                        " in static member list");
                        } else if (!isStatic 
                                   && Flags.isStatic(field.getFlags())) {
                            failImporting("Static field " + m.identifier +
                                          " in instance member list");
                        }
                        
                        if (m.allowed) {
                            allowedFields.put(field, m.comment);
                        } else if (m.comment != null) {
                            disabledFields.put(field, m.comment);
                        }
                        break;
                         
                    case CONSTRUCTOR:
                        IMethod ctor = relevantConstructors.get(m.identifier);
                        relevantConstructors.remove(m.identifier);
                        
                        if (ctor == null) {
                            failImporting("Error: Unknown or duplicate " + 
                                          "constructor " + m.identifier);
                        } else if (!isStatic) {
                            failImporting("Error: Constructor in instance " +
                                          "member list");
                        }
                        
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
                            failImporting("Error: unknown or duplicate method "
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
                IType type = project.findType(className);
         
                if (type == null) {
                    err.println("WARNING: Type " + className + " not found!");
                    throw new ImportFailure();
                }
                
                Set<IType> honoraryTypes = new HashSet<IType>();
                       
                IField[] typeFields = type.getFields();
                IMethod[] typeMethods = type.getMethods();
                        
                // Mappings of identifier -> member, by type
                for (IField f : typeFields) {
                    if (Taming.isRelevant(type, f)) {
                        relevantFields.put(f.getElementName(), f);
                    }
                }

                for (IMethod m : typeMethods) {
                    if (Taming.isRelevant(type, m)) {
                        if (m.isConstructor()) {
                            relevantConstructors.put(Taming.getFlatSignature(m), m);
                        } else {
                            relevantMethods.put(Taming.getFlatSignature(m), m);
                        }
                    }
                }

                MemberProcessor mp = new MemberProcessor();
                for (Member m : staticMembers) {
                    mp.processMember(m, true);
                }
                
                for (Member m : instanceMembers) {
                    mp.processMember(m, false);
                }
                
                for (String s : honoraries) {
                    IType honorary = project.findType(s);
                    ITypeHierarchy sth = honorary.newSupertypeHierarchy(null);
                    for (IType superHonorary : sth.getAllInterfaces()) {
                        honoraryTypes.add(superHonorary);
                    }
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
                    err.println("WARNING: Field " + f + " not mentioned " + 
                                "in safej.");
                }
                for (String c : relevantConstructors.keySet()) {
                    err.println("WARNING: Constructor " + c + " not mentioned " + 
                                "in safej.");
                }
                for (String m : relevantMethods.keySet()) {
                    err.println("WARNING: Method " + m + " not mentioned " + 
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
                    err.println("WARNING: Type " + className + " not found!");
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
        private void failImporting(String problem) throws ImportFailure {
            err.println("Importing failed: " + problem);
            throw new ImportFailure();
        }
    }
}
