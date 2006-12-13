// Copyright 2005-06 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e.eclipse;

import org.eclipse.jdt.core.*;

import org.eclipse.jdt.core.dom.*;

import java.util.Collection;
import java.util.List;
import java.util.Stack;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;

/**
 * This class contains the actual checks performed by the Joe-E verifier. The
 * builder creates an instance of this class and gives it references to a Java
 * project and the associated build state and taming database. It then invokes
 * the checkICU() method of this class, specifying a compilation unit to be
 * checked.
 * 
 * A new instance of this class is created on a clean build, in order to
 * associate it with a new build state.
 */
public class Verifier {
    final IJavaProject project;

    final Taming taming;

    final BuildState state;

    /**
     * Create a new verifier object associated with a specific project. The
     * verifier does not maintain persistent state of its own aside from
     * pointers to other state objects.
     * 
     * @param project
     *            the Java project for the verifier to operate on
     * @param state
     *            the build state for the specified project
     * @param taming
     *            the taming database to use
     * @throws JavaModelException
     */
    Verifier(IJavaProject project, BuildState state, Taming taming)
            throws JavaModelException {
        this.state = state;
        this.project = project;
        this.taming = taming;
    }

    /**
     * Run the Joe-E verifier on an ICompilationUnit. Problems encountered are
     * appended to the supplied list of problems.
     * 
     * @param icu
     *            ICompilationUnit on which to run the verifier
     * @param problems
     *            A List of Problems (Joe-E verification errors) to which to
     *            append Problems encountered
     * @return a Collection of ICompilationUnits referenced by icu
     */
    Collection<ICompilationUnit> checkICU(ICompilationUnit icu,
            List<Problem> problems) {
        // Clear any state existing from previous build of icu.
        state.prebuild(icu);
        Set<ICompilationUnit> dependents = new HashSet<ICompilationUnit>();

        /*
         * Checks to ensure org.joe_e package is not used in source files.
         * Disabled for now.
         *
			IPackageDeclaration[] pkg = icu.getPackageDeclarations();
			if (pkg.length > 1) {
				// File shouldn't compile anyway...
				problems.add(new Problem("More than one package! I'm confused.", 
										 pkg[1].getSourceRange()));
			}
			String pkgName = "";
			if (pkg.length > 0) {
				pkgName = pkg[0].getElementName();
				
				System.out.println("Package " + pkgName);
			}
			else {
				System.out.println("Default (null) package");
			}
					
			if (pkgName.equals("org.joe_e" || pkgName.startsWith("org.joe_e."))
			{
				problems.add(new Problem("Bad package name.  Nice try.",
										 pkg[0].getSourceRange()));
			}
            
        */

        try {
            /*
            // Types defined in this file
			IType[] itypes = icu.getAllTypes();  // throws JavaModelException
			
            // TODO: lack of local types (see doc of icu.getAllTypes()):
            // possibly a big soundness hole!!
            
			System.out.println("Found " + itypes.length + " types.");
			for(int i = 0; i < itypes.length; ++i)
			{
				// Analyze each type.
				IType type = itypes[i];
				System.out.println("Analyzing " + type.getFullyQualifiedName() + ".");
			
				checkIType(type, dependents, problems);
                
                
                System.out.println("Done with " + type.getFullyQualifiedName() + ".");
			}
			
			// checks that require ugly DOM hacking directly
			*/

            ASTParser parser = ASTParser.newParser(AST.JLS3);
            parser.setSource(icu);
            parser.setResolveBindings(true);
            ASTNode parse = parser.createAST(null);
            VerifierASTVisitor vav = new VerifierASTVisitor(icu, dependents,
                    problems);
            parse.accept(vav);
        
        /*
         * JavaModelException *CANNOT* be thrown here, thus this special
         * casing is not necessary. 
			
	    } catch (JavaModelException jme) { 
            jme.printStackTrace();  // TODO: Fix ugly debug.
			problems.add(new Problem("Analysis of file failed due to BUG IN VERIFIER or " +
                                     "I/O error. (Unhandled exception)", 0, 0));   
		*/
        } catch (Throwable e) {
            System.out.println("Abort due to Undeclared Throwable!" + e);
            e.printStackTrace();
            problems.add(new Problem(
                    "Analysis of file failed due to BUG IN VERIFIER or "
                            + "I/O error. (unexpected exception)", 0, 0));
        }

        System.out.println(problems);
        return dependents;
    }

    /**
     * AST visitor class.
     */
    class VerifierASTVisitor extends ASTVisitor {
        final ICompilationUnit icu;

        final Set<ICompilationUnit> dependents;

        final List<Problem> problems;

        final Stack<BodyDeclaration> codeContext;

        /**
         * Create a visitor for a specified compilation unit that appends Joe-E
         * verification errors to a specified list of problems.
         * 
         * @param icu
         *            the compilation unit to analyze
         * @param problems
         *            a list of problems to append Joe-E verification errors to
         */
        VerifierASTVisitor(ICompilationUnit icu,
                Set<ICompilationUnit> dependents, List<Problem> problems) {
            // System.out.println("VAV init");
            this.icu = icu;
            this.dependents = dependents;
            this.problems = problems;
            this.codeContext = new Stack<BodyDeclaration>();
        }

        /*
         * Convenience method to add problems.
         */
        private void addProblem(String description, ASTNode source) {
            problems.add(new Problem(description, source.getStartPosition(),
                    source.getLength()));
        }

        /**
         * Check a field access. Ensure that the field being accessed is either
         * present in source code, or permitted by the taming database.
         * 
         * @param fa
         *            the field access to check
         * @return true to visit children of this node
         */
        public boolean visit(FieldAccess fa) {
            IVariableBinding ivb = fa.resolveFieldBinding();
            checkFieldBinding(ivb, fa);
            return true;
        }

        /**
         * Helper method to check a field binding, resolved from a field access
         * expression or a qualified name. Ensure that the field is either
         * present in source code or permitted by the taming database.
         * 
         * @param fieldBinding
         *            the field binding to check
         * @param source
         *            the AST node in the program source that resolves to this
         *            binding
         */
        private void checkFieldBinding(IVariableBinding fieldBinding,
                ASTNode source) {
            ITypeBinding classBinding = fieldBinding.getDeclaringClass();
            // "The field length of an array type has no declaring class."
            // It appears to return null. It needs to be special-cased here;
            // we allow it.
            if (classBinding != null && !classBinding.isFromSource()) {
                // check in taming database
                if (!taming.isTamed(classBinding)) {
                    addProblem("Field from untamed class "
                            + classBinding.getName() + " accessed.", source);
                    return;
                }

                if (!taming.isAllowed(classBinding, fieldBinding)) {
                    addProblem("Disabled field " + fieldBinding.getName()
                            + " from class " + classBinding.getName()
                            + " accessed.", source);
                }
            }
        }

        /**
         * Check a qualified name. If the name corresponds to a field, ensure
         * that the field being accessed is either present in source code, or
         * permitted by the taming database.
         * 
         * @param qn
         *            the qualified name to check
         * @return true to visit children of this node
         */
        public boolean visit(SimpleName sn) {
            IBinding ib = sn.resolveBinding();
            if (ib instanceof IVariableBinding) {
                IVariableBinding ivb = (IVariableBinding) ib;
                if (ivb.isField()) {
                    checkFieldBinding(ivb, sn);
                }
            } else if (ib instanceof ITypeBinding) {
                ITypeBinding itb = (ITypeBinding) ib;
                // TODO: temporary debugging cruft.
                if (!itb.isClass() && !itb.isInterface()) {
                    System.out.print("not a class or interface");
                    if (!itb.isEnum()) {
                        System.out.print(", not an enum");
                        assert (itb.isAnnotation());
                    }
                    System.out.println(".");
                }

                if (!itb.isFromSource()) {
                    // check in taming database
                    if (!taming.isTamed(itb)) {
                        addProblem("Reference to untamed class "
                                + itb.getName() + ".", sn);
                    }
                }
            } else {
                assert (ib instanceof IPackageBinding);
            }
            return true;
        }

        /**
         * Check a class instance creation. Look up the constructor called in
         * the taming database for non-source types. Otherwise, if we are in a
         * constructor context (see inConstructorContext()), then if the object
         * being constructed is of a (transitively) inner class of the current
         * class, flag an error: it may be able to see this class' partially
         * initialized state. Anonymous classes are inner classes of the current
         * class and thus also result in an error being flagged.
         * 
         * @param cic
         *            the ClassInstanceCreation to check
         * @return true to visit children of this node
         */
        public boolean visit(ClassInstanceCreation cic) {
            IMethodBinding imb = cic.resolveConstructorBinding();
            ITypeBinding classBinding = imb.getDeclaringClass();
            IType classType = (IType) classBinding.getJavaElement();

            // Check if taming is violated for non-source types.
            if (!classBinding.isFromSource()) {
                // check in taming database
                if (!taming.isTamed(classBinding)) {
                    addProblem("Construction of untamed class "
                            + classBinding.getName() + ".", cic.getType());
                    return true;
                }

                if (!taming.isAllowed(classBinding, imb)) {
                    addProblem("Disabled constructor from class "
                            + classBinding.getName() + " called.", cic
                            .getType());
                    return true;
                }
            }

            // Otherwise, if we are in a constructor context, make sure that it
            // isn't an anonymous type or a non-static inner type.
            IType currentClass = getConstructorContext();

            if (currentClass != null) {
                try {
                    if (classType.isAnonymous()) {
                        addProblem("Construction of anonymous class "
                                + "during instance initialization.", cic
                                .getType());
                        return true;
                    }

                    IJavaElement ancestor = classType;
                    while (ancestor instanceof IType
                            && !Flags.isStatic(((IType) ancestor).getFlags())) {
                        ancestor = ancestor.getParent();

                        if (ancestor.equals(currentClass)) {
                            addProblem(
                                    "Construction of non-static member class "
                                            + imb.getName()
                                            + " during instance "
                                            + "initialization.", cic.getType());
                            return true;
                        }
                    }
                } catch (JavaModelException jme) {
                    jme.printStackTrace(); // TODO: Fix ugly debug.
                    addProblem("Analysis of file incomplete: BUG IN VERIFIER"
                            + " or I/O error (unhandled exception) "
                            + "encountered analyzing class instance "
                            + "creation expression.", cic.getType());
                }
            }

            return true;
        }

        /**
         * Check a method invocation. Ensure that the method being called is
         * either present in source code, or permitted by the taming database.
         * 
         * @param mi
         *            the method invocation to check
         * @return true to visit children of this node
         */
        public boolean visit(MethodInvocation mi) {
            IMethodBinding imb = mi.resolveMethodBinding();
            ITypeBinding classBinding = imb.getDeclaringClass();
            IType classType = (IType) classBinding.getJavaElement();
            if (!classBinding.isFromSource()) {
                // check in taming database
                if (!taming.isTamed(classBinding)) {
                    addProblem("Method from untamed class "
                            + classBinding.getName() + " called.", mi.getName());
                    return true;
                }

                if (!taming.isAllowed(classBinding, imb)) {
                    addProblem("Disabled method " + imb.getName()
                            + " from class " + classBinding.getName()
                            + " called.", mi.getName());
                    return true;
                }
            }
            // If we are in constructor or instance initializer, forbid local
            // methods.
            IType currentClass = getConstructorContext();

            // check non-static methods called in construction contexts
            if (currentClass != null && !Flags.isStatic(imb.getModifiers())) {
                // if the method is invoked on the current object (no explicit
                // target given, this is bad.
                if (mi.getExpression() == null) {
                    addProblem("Called local non-static method "
                            + imb.getName() + " during instance "
                            + "initialization.", mi.getName());
                }
            }
            return true;
        }

        /**
         * If in a constructor context, i.e. at a program point in which the
         * current object may be incompletelly initialized, get the current
         * class.
         * 
         * @return null if not in a constructor context, the type of the current
         *         class if in a constructor context
         */
        IType getConstructorContext() {
            if (inConstructorContext()) {
                BodyDeclaration bd = codeContext.peek();
                // FIXME: Does not work for constructors of anonymous classes!!!
                ASTNode bdParent = bd.getParent();
                if (bdParent instanceof AbstractTypeDeclaration) {
                    return (IType) ((AbstractTypeDeclaration) bdParent)
                            .resolveBinding().getJavaElement();
                } else {
                    assert (bdParent instanceof AnonymousClassDeclaration);
                    return (IType) ((AnonymousClassDeclaration) bdParent)
                            .resolveBinding().getJavaElement();
                }
            } else {
                return null;
            }
        }

        /**
         * Test whether we are in a constructor context, i.e. at a program point
         * at which the current object may be incompletelly initialized. This
         * occurs during the traversal of instance initializers, constructors,
         * and non-static field declarations. This makes use of the codeContext
         * information updated when such nodes are entered and exited during
         * traversal.
         * 
         * @return true if the traversal is currently within a constructor
         *         context.
         */
        boolean inConstructorContext() {
            BodyDeclaration bd = codeContext.peek();
            if (bd instanceof Initializer) {
                Initializer init = (Initializer) bd;
                // Non-static initalizers are essentially part of every
                // constructor.
                return !Flags.isStatic(init.getModifiers());
            } else if (bd instanceof MethodDeclaration) {
                MethodDeclaration md = (MethodDeclaration) bd;
                return md.isConstructor();
            } else if (bd instanceof FieldDeclaration) {
                FieldDeclaration fd = (FieldDeclaration) bd;
                // Non-static field declarations are essentially part of every
                // constructor.
                return !Flags.isStatic(fd.getFlags());
            } else {
                return false;
                // FIXME: Include appropriate handling (or reasoning why not
                // necessary) for cases not handled above:
                // AbstractTypeDeclaration, AnnotationTypeMemberDeclaration,
                // EnumConstantDeclaration.
            }
        }

        /**
         * Record in the codeContext when we visit an initializer.
         * 
         * @param init
         *            the initializer being traversed
         * @return true to visit children of this node
         */
        public boolean visit(Initializer init) {
            codeContext.push(init);
            return true;
        }

        /**
         * Record in the codeContext when we visit a field declaration.
         * 
         * @param fd
         *            the field declaration being traversed
         * @return true to visit children of this node
         */
        public boolean visit(FieldDeclaration fd) {
            codeContext.push(fd);
            return true;
        }

        /**
         * Check a method declaration. Ensure that the method is not native.
         * Also, record the visiting of the method in the codeContext.
         * 
         * @param md
         *            the method declaration being traversed
         * @return true to visit children of this node
         */
        public boolean visit(MethodDeclaration md) {
            // System.out.println("visit(MethodDeclaration bd) of <" + md +
            // ">");
            codeContext.push(md);
            String name = md.getName().toString();
            if (Modifier.isNative(md.getModifiers())) {
                addProblem("Native method " + name, md);
            }
            return true;
        }

        /*
         * Body declarations not specifically handled above:
         * AnnotationTypeDeclaration, EnumDeclaration, TypeDeclaration,
         * AnnotationTypeMemberDeclaration, EnumConstantDeclaration
         */
        public boolean visit(AnnotationTypeDeclaration atd) {
            checkType((ITypeBinding) atd.resolveBinding());
            // codeContext.push(atd);
            return true;
        }

        public boolean visit(EnumDeclaration ed) {
            checkType((ITypeBinding) ed.resolveBinding());
            // codeContext.push(ed);
            return true;
        }

        public boolean visit(TypeDeclaration td) {
            checkType((ITypeBinding) td.resolveBinding());
            // codeContext.push(td);
            return true;
        }

        public boolean visit(AnnotationTypeMemberDeclaration atmd) {
            // System.out.println("visit(BodyDeclaration bd) of <" + bd + ">");
            codeContext.push(atmd);
            return true;
        }

        public boolean visit(EnumConstantDeclaration ecd) {
            // System.out.println("visit(BodyDeclaration bd) of <" + bd + ">");
            codeContext.push(ecd);
            return true;
        }

        public boolean visit(AnonymousClassDeclaration acd) {
            checkType((ITypeBinding) acd.resolveBinding());
            return true;
        }

        /**
         * Verify an IType, updating the list of dependents and problems.
         * 
         * @param type
         *            the type to verify
         */
        void checkType(ITypeBinding itb) {
            System.out.println("Checking IType " + itb.getName());
            IType type = (IType) itb.getJavaElement();
            try {
                ITypeHierarchy sth = type.newSupertypeHierarchy(null);

                // Marker interfaces here = implements in BASE type system
                boolean isSelfless = sth.contains(taming.SELFLESS);
                boolean isImmutable = sth.contains(taming.IMMUTABLE);
                boolean isPowerless = sth.contains(taming.POWERLESS);
                boolean isEquatable = sth.contains(taming.EQUATABLE);
                // TODO: special handling for enums to avoid needing to declare
                // implements Powerless, Equatable for all of them?

                int tags = isSelfless ? BuildState.IMPL_SELFLESS : 0;
                tags |= isImmutable ? BuildState.IMPL_IMMUTABLE : 0;
                tags |= isPowerless ? BuildState.IMPL_POWERLESS : 0;
                tags |= isEquatable ? BuildState.IS_EQUATABLE : 0;

                // update flags and add dependents
                dependents.addAll(state.updateTags(type, tags));

                // Restrictions on static fields.
                IVariableBinding[] fields = itb.getDeclaredFields();

                for (IVariableBinding fb : fields) {
                    String name = fb.getName();
                    System.out.println("Field " + name + ":");
                    int modifiers = fb.getModifiers();
                    if (Modifier.isStatic(modifiers)) {
                        if (Modifier.isFinal(modifiers)) {
                            ITypeBinding fieldTB = fb.getType();
                            // must be Powerless

                            state.addFlagDependency(icu, fieldTB);

                            if (!taming.implementsOverlay(fieldTB,
                                    taming.POWERLESS)) {
                                problems.add(new Problem(
                                        "Non-powerless static field " + name
                                                + ".", ((IField) fb
                                                .getJavaElement())
                                                .getNameRange()));
                            }
                        } else {
                            problems.add(new Problem("Non-final static field "
                                    + name + ".",
                                    ((IField) fb.getJavaElement())
                                            .getNameRange()));
                        }
                    }
                }

                if (type.isInterface()) {
                    // Nothing more to check. All fields are static final and
                    // have
                    // already been verified to be immutable.

                    return;
                }
                //
                // Otherwise, it is a "real" class.
                //

                ITypeBinding superTB = itb.getSuperclass();

                if (superTB != null) {
                    System.out.println("Superclass "
                            + superTB.getQualifiedName());

                    // See what honoraries superclass has; make sure that all
                    // are
                    // implemented by this class.

                    IType supertype = (IType) superTB.getJavaElement();
                    Set<IType> unimp = taming.unimplementedHonoraries(sth);
                    for (IType i : unimp) {
                        problems.add(new Problem("Honorary interface "
                                + i.getElementName() + " not inherited from "
                                + supertype.getElementName(), type
                                .getNameRange()));
                    }
                }

                if (isPowerless /* && !taming.isDeemed(type, taming.POWERLESS) */) {
                    if (sth.contains(taming.TOKEN)) {
                        problems.add(new Problem("Powerless type "
                                + type.getElementName()
                                + " can't extend Token.", type.getNameRange()));
                    }

                    verifyAllFieldsAre(itb, taming.POWERLESS);

                } else if (isImmutable /*
                                         * !taming.isDeemed(type,
                                         * taming.IMMUTABLE)
                                         */) {

                    verifyAllFieldsAre(itb, taming.IMMUTABLE);
                }
            } catch (JavaModelException jme) {
                jme.printStackTrace(); // TODO: prettier debug
                problems.add(new Problem(
                        "Analysis of file incomplete: BUG IN VERIFER "
                                + "or I/O error (unhandled exception) "
                                + "encountered analyzing type "
                                + type.getElementName() + ".", 0, 0));
            } catch (Throwable e) {
                e.printStackTrace(); // TODO: prettier debug
                problems.add(new Problem(
                        "Analysis of file incomplete: BUG IN VERIFER "
                                + "or I/O error (unexpected exception) "
                                + "encountered analyzing type "
                                + type.getElementName() + ".", 0, 0));
            }
        }

        /**
         * Verify that all fields (declared, inherited, and lexically visible)
         * of a type are final and implement the specified marker interface in
         * the overlay type system.
         * 
         * @param type
         *            the type whose fields to verify
         * @param mi
         *            the marker interface, i.e. Immutable or Powerless
         * @throws JavaModelException
         */
        void verifyAllFieldsAre(ITypeBinding itb, IType mi)
                throws JavaModelException {

            // FIXME: needs to depend on entire super class hierarchy that isn't
            // guaranteed to
            // implement interface. has flag dependency on first one (if any)
            // that does.
            // deep dependency on superclass, if one exists
            /*
            String  = type.getSuperclassTypeSignature();
            if (superclassSig != null) {
                IType superclass = Utility.lookupType(superclassSig, type);
                state.addDeepDependency(type.getCompilationUnit(), superclass);
            }
            */

            Set<ITypeBinding> needCheck = findClasses(itb, mi);
            for (ITypeBinding i : needCheck) {
                verifyFieldsAre(i, mi, itb);
            }
        }

        /**
         * Find the set of classes all of whose fields must satisfy a given
         * marker interface. This requires traversal of supertypes and enclosing
         * types. Classes already declared to implement the marker interface are
         * not returned.
         * 
         * @param type
         *            the type at which to start
         * @param mi
         *            the marker interface whose implementors to skip
         * @return the set of classes found
         * @throws JavaModelException
         */
        Set<ITypeBinding> findClasses(ITypeBinding itb, IType mi)
                throws JavaModelException {
            Set<ITypeBinding> found = new HashSet<ITypeBinding>();
            found.add(itb);
            LinkedList<ITypeBinding> left = new LinkedList<ITypeBinding>();
            left.add(itb);

            while (!left.isEmpty()) {
                ITypeBinding next = left.removeFirst();
                // non-static member classes get access to variables in their
                // containing class
                if (next.isMember() && !Modifier.isStatic(next.getModifiers())) {
                    ITypeBinding enclosingTB = next.getDeclaringClass();
                    // if following line fails, fix for local types

                    if (taming.implementsOverlay(enclosingTB, mi)) {
                        // System.out.println(enclosingType.getQualifiedName()
                        // + " is " + mi.getElementName());
                        state.addFlagDependency(icu, enclosingTB);
                    } else {
                        if (found.add(enclosingTB)) {
                            state.addDeepDependency(icu, enclosingTB);
                            left.add(enclosingTB); // only add if we haven't
                                                    // traversed it yet
                        }
                    }
                }

                ITypeBinding superTB = next.getSuperclass();
                if (superTB != null) {
                    if (taming.implementsOverlay(superTB, mi)) {
                        // System.out.println(supertype.getElementName()
                        // + " is " + mi.getElementName());
                        state.addFlagDependency(icu, superTB);
                        // already verified
                    } else {
                        if (found.add(superTB)) {
                            state.addDeepDependency(icu, superTB);
                            left.add(superTB); // only add if we haven't
                                                // traversed it yet
                        }
                    }
                }
            }

            return found;
        }

        /**
         * Verify that a class's explicit instance fields are final and
         * honorarily implement the specified marker interface
         * 
         * @param type
         *            the type whose instance fields to check
         * @param mi
         *            the marker interface to check implementation of
         * @param candidate
         *            the type for which to report Problems
         * @param problems
         *            the list to which to append problems
         * @throws JavaModelException
         */
        void verifyFieldsAre(ITypeBinding itb, IType mi, ITypeBinding candidate)
                throws JavaModelException {
            String miName = mi.getElementName();
            String candName = candidate.getName();
            if (candName.length() == 0) {
                candName = "anonymous class";
            } else {
                candName = "class " + candName;
            }
            String typeName = itb.getName();
            //
            // Check declared instance fields implement mi
            //
            IVariableBinding[] fields = itb.getDeclaredFields();

            for (IVariableBinding fb : fields) {
                String fieldName = fb.getName();
                // System.out.println("Field " + name + ":");
                int modifiers = fb.getModifiers();
                if (!Flags.isStatic(modifiers) && !Flags.isEnum(modifiers)) {
                    if (Flags.isFinal(modifiers)) {
                        // Field must implement mi
                        ITypeBinding fieldTB = fb.getType();

                        // add dependency on type of field
                        state.addFlagDependency(icu, fieldTB);

                        if (taming.implementsOverlay(fieldTB, mi)) {
                            // OKAY
                        } else if (itb.equals(candidate)) {
                            problems.add(new Problem(String.format(
                                    "Non-%s field %s in %s %s", miName,
                                    fieldName, miName, candName), ((IField) fb
                                    .getJavaElement()).getNameRange()));
                        } else { // type != candidate
                            problems.add(new Problem(String.format(
                                    "Non-%s field %s from %s in %s %s", miName,
                                    fieldName, typeName, miName, candName),
                                    ((IType) candidate.getJavaElement())
                                            .getNameRange()));
                        }
                    } else if (itb.equals(candidate)) {
                        problems.add(new Problem(String.format(
                                "Non-final field %s in %s %s", fieldName,
                                miName, candName), ((IField) fb
                                .getJavaElement()).getNameRange()));
                    } else { // type != candidate
                        problems.add(new Problem(String.format(
                                "Non-final field %s from %s in %s %s",
                                fieldName, typeName, miName, candName),
                                ((IType) candidate.getJavaElement())
                                        .getNameRange()));
                    }
                }
            }

            /*
			 * now handled by findClasses
            //
            // Check inherited instance fields also implement mi
            //
            String superclass = type.getSuperclassTypeSignature();
            if (superclass != null) {
                IType supertype = Utility.lookupType(superclass, type);
                if (MarkerInterface.is(supertype, mi)) {
                    // everything should be fine; verifier has already verified
                    // it
                } else {
                    verifyFieldsAre(supertype, mi, candidate, problems);
                }
            }
            */

        }

        /**
         * Check usage of the keyword 'this'. If we are in a constructor
         * context, bare use of the 'this' keyword is prohibited.
         * 
         * @param te
         *            the this expression to check
         * @return true to visit children of this node
         */
        public boolean visit(ThisExpression te) {
            if (inConstructorContext()
                    && !(te.getParent() instanceof FieldAccess)) {
                addProblem("Possible escapement of 'this' not allowed in "
                        + "instance initialization.", te);
            }
            return true;
        }

        /**
         * Check an infix expression. If the expression is an object identity
         * comparison (== or !=), then ensure that at least one of the operands
         * is Equatable.
         * 
         * @param te
         *            the infix expression to check
         * @return true to visit children of this node
         */
        public boolean visit(InfixExpression ie) {
            InfixExpression.Operator op = ie.getOperator();
            if (op == InfixExpression.Operator.EQUALS ||
				op == InfixExpression.Operator.NOT_EQUALS) {
                // Test for Equatability
                try {
                    ITypeBinding leftTB = ie.getLeftOperand().resolveTypeBinding();
                    if (taming.implementsOverlay(leftTB, taming.EQUATABLE)) {
                        // need to recheck if left type becomes un-Equatable
                        state.addFlagDependency(icu, leftTB);
                        return true;
                    } else {
                        ITypeBinding rightTB = ie.getRightOperand().resolveTypeBinding();
                        if (taming.implementsOverlay(rightTB, taming.EQUATABLE)) {
                            // need to recheck if right type becomes
                            // un-Equatable
                            state.addFlagDependency(icu, rightTB);
                            return true;
                        } else {
                            addProblem("Pointer equality test on non-Equatable types", ie);
                            // need to recheck if either type becomes Equatable
                            state.addFlagDependency(icu, rightTB);
                            state.addFlagDependency(icu, leftTB);       
                        }
                    }
                
                } catch (JavaModelException jme) {
                    addProblem("Analysis of file incomplete: BUG IN VERIFER " +
                                "or I/O error (unhandled exception) " +
                                "encountered analyzing infix expression.", ie);
                }             
            } else if (op == InfixExpression.Operator.PLUS) {
                AST ast = ie.getAST(); // needed for resolveWellKnown type
                
                //try {
                    ITypeBinding leftTB = ie.getLeftOperand().resolveTypeBinding();
                    if (!safeToString(leftTB, ast)) {
                        addProblem("Untamed implicit call to toString",
                                   ie.getLeftOperand());
                    }
                    
                    ITypeBinding rightTB = ie.getRightOperand().resolveTypeBinding();
                    if (!safeToString(rightTB, ast)) {
                        addProblem("Untamed implicit call to toString", 
                                   ie.getRightOperand());
                    }
                                        
                    if (ie.hasExtendedOperands()) {
                        for (Object o : ie.extendedOperands()) {
                            Expression e = (Expression) o;
                            ITypeBinding extendedTB = e.resolveTypeBinding();
                            if (!safeToString(extendedTB, ast)) {
                                addProblem("Untamed implicit call to toString*", e);
                            }
                        }
                    }
                //} catch {
            }
            
            return true;
            
            
            /*
            ITypeBinding leftTB = ie.getLeftOperand().resolveTypeBinding(); 
            if (leftTB == null) {
                addProblem("Analysis error: left type binding " +
                           "unresolvable.", ie.getLeftOperand()); return true; 
            }
            
            // cases where we don't need to look at right hand type 
            if (leftTB.isPrimitive() || leftTB.isNullType()) { 
                return true; 
            }
                 
            ITypeBinding rightTB = ie.getRightOperand().resolveTypeBinding(); 
            if (rightTB == null) {
                addProblem("Analysis error: right type binding " +
                           "unresolvable.", ie.getRightOperand()); return true; 
            }
            
            // (otherwise redundant isPrimitive check required for auto-unboxing) 
            if (rightTB.isNullType() || rightTB.isPrimitive()) {
                 return true; 
            }
                 
            InfixExpression.Operator EQUALS = InfixExpression.Operator.EQUALS; 
            InfixExpression.Operator NOT_EQUALS = InfixExpression.Operator.NOT_EQUALS;
            InfixExpression.Operator PLUS = InfixExpression.Operator.PLUS;
                  
            // Arrays bad for both equality tests and toString() 
            if (leftTB.isArray() || rightTB.isArray()) { 
                if (op == EQUALS) {
                    addProblem("== used to compare arrays", ie); 
                } else if (op == NOT_EQUALS) { 
                    addProblem("!= used to compare arrays", ie); 
                } else { // PLUS 
                    addProblem("toString() implicitly called on an array", ie); 
                } 
                return true; 
            }
            
            // Generic types are not typesafe, thus bad for both equality 
            // and toString() 
            else if (leftTB.isTypeVariable() || rightTB.isTypeVariable()) { 
                if (op == EQUALS) {
                    addProblem("== used to compare objects of generic"
                               + "type", ie); 
                } else if (op == NOT_EQUALS) { 
                    addProblem("!= used to compare objects of generic"
                               + "type", ie); 
                } else { // PLUS
                    addProblem("toString() implicitly called on object of" +
                               "generic type.", ie); } return true;
                }
                // At this point, should have dealt with any funny stuff.
                assert((leftTB.isClass() || leftTB.isInterface()) &&
                       (rightTB.isClass() || rightTB.isInterface()));
                  
                try { // Evaluate left type binding 
                    IType leftType = (IType)
                    leftTB.getJavaElement(); 
                    if (leftType == null) {
                         addProblem("Analysis error: couldn't find type " +
                                    leftTB.getQualifiedName() + ".", 
                                    ie.getLeftOperand()); 
                         return true;
                    }
                    // String concatenation 
                    if (op == PLUS) { 
                        if (leftType.equals(taming.STRING)) { 
                            // only need to check right type 
                            IType rightType = (IType) rightTB.getJavaElement(); 
                            if (rightType == null) {
                                addProblem("Analysis error: couldn't find type " +
                                           rightTB.getQualifiedName() + ".", 
                                           ie.getRightOperand());
                                return true;
                            }
                            // ensure Object's toString method not called ...
                 
                  
                  
                  if (taming.implementsOverlay(rightType, taming.EQUATABLE)) { 
                      // need to recheck if right side becomes un-equatable
                      state.addFlagDependency(icu, rightType); 
                      return true; 
                  }
                  
                  // Otherwise, we have a problem 
                  addProblem("Pointer equality test on non-Equatable types", ie);
                  
                  // need to recheck if either type becomes equatable
                  state.addFlagDependency(icu, rightType);
                  state.addFlagDependency(icu, leftType);
              } else { 
                  // only need to check left type
              }
            }
                  
            // Otherwise, equality test.
            // OK if left side is Equatable 
            if (taming.implementsOverlay(leftType, taming.EQUATABLE)) {
                // need to recheck if left side becomes un-equatable
                state.addFlagDependency(icu, leftType); 
                return true; 
            }
            
            // Otherwise, evaluate right type binding 
            IType rightType = (IType) rightTB.getJavaElement(); 
            if (rightType == null) {
                addProblem("Analysis error: couldn't find type " +
                rightTB.getQualifiedName() + ".", ie.getRightOperand());
                return true; 
            }
            
            // OK if right side is Equatable
            if (taming.implementsOverlay(rightType, taming.EQUATABLE)) {
                // need to recheck if right side becomes un-equatable
                state.addFlagDependency(icu, rightType); 
                return true; 
            }
             
            // Otherwise, we have a problem 
            addProblem("Pointer equality test on non-Equatable types", ie);
            // need to recheck if either type becomes equatable
            state.addFlagDependency(icu, rightType);
            state.addFlagDependency(icu, leftType);
            } catch (JavaModelException jme) { 
                jme.printStackTrace(); 
                // TODO: prettier debug 
                addProblem("Analysis of file incomplete: BUG IN VERIFER " 
                           + "or I/O error (unhandled exception) " +
                           "encountered analyzing infix expression.", ie); 
            } 
        }
        */
        
        }

        /*
         * returns true if invoking toString() on an object of
         * type itb is guaranteed to be safe (allowed by the taming
         * database).
         */
        boolean safeToString(ITypeBinding itb, AST ast) {
            if (itb.isPrimitive()) {
                return true;
            } else if (itb.isArray() || itb.isGenericType()) { 
                return false;
            } else { // no funny stuff, itb should be in the class hierarchy
                while (itb != null) {
                    IMethodBinding[] imbs = itb.getDeclaredMethods();
                    for (IMethodBinding imb : imbs) {
                        // Most specific statically resolvable toString method
                        if (imb.getName().equals("toString")
                                && imb.getParameterTypes().length == 0) {
                            return (itb.isFromSource() ||
                                    (taming.isTamed(itb) &&
                                     taming.isAllowed(itb, imb)));
                        }      
                    }
                    itb = itb.getSuperclass();
                }
                return false;
            }
        }

        /*
         * endVisit
         * 
         * If any of these assertions fail, see if .equals() is required here
         * instead of ==. I doubt this will be necessary, as I don't see why
         * we'd be given two different versions of the same object here.
         */
        public void endVisit(Initializer init) {
            assert (codeContext.pop() == init);
        }

        public void endVisit(FieldDeclaration fd) {
            assert (codeContext.pop() == fd);
        }

        public void endVisit(MethodDeclaration md) {
            assert (codeContext.pop() == md);
        }

        public void endVisit(AnnotationTypeDeclaration atd) {
            // assert(codeContext.pop() == atd);
        }

        public void endVisit(EnumDeclaration ed) {
            // assert(codeContext.pop() == ed);
        }

        public void endVisit(TypeDeclaration td) {
            // assert(codeContext.pop() == td);
        }

        public void endVisit(AnnotationTypeMemberDeclaration atmd) {
            // System.out.println("End visit of <" + bd + ">");
            assert (codeContext.pop() == atmd);
        }

        public void endVisit(EnumConstantDeclaration ecd) {
            // System.out.println("End visit of <" + bd + ">");
            assert (codeContext.pop() == ecd);
        }


    }
}