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
    Verifier(BuildState state, Taming taming)
            throws JavaModelException {
        this.state = state;
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
     * @return a Collection of ICompilationUnits that must be rechecked due to
     *         changes made to icu since it was last built
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
            ASTParser parser = ASTParser.newParser(AST.JLS3);
            parser.setSource(icu);
            parser.setResolveBindings(true);
            ASTNode parse = parser.createAST(null);
            VerifierASTVisitor vav = new VerifierASTVisitor(icu, dependents,
                    problems);
            parse.accept(vav);      
        } catch (Throwable e) {
            // Catch any unexpected exceptions or errors during verification
            System.out.println("Abort due to undeclared Throwable! " + e);
            e.printStackTrace();
            problems.add(new Problem("Analysis of file failed due to BUG IN " +
                                     "VERIFIER or I/O error. (unexpected " +
                                     "exception)"));
        }

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
         * Convenience methods to add problems.
         */
        private void addProblem(String description, ASTNode source) {
            problems.add(new Problem(description, source.getStartPosition(),
                                     source.getLength()));
        }

        private void addProblem(String description) {
            problems.add(new Problem(description));
        }      
        
        private void addProblem(String description, ISourceRange source) {
            problems.add(new Problem(description, source));
        }

        /* 
         * No need to check FieldAccess or SuperFieldAccess expressions,
         * as they are handled by SimpleName.
         */
        /**
         * Check a simple name. If the name corresponds to a field, ensure
         * that the field being accessed is either present in source code, or
         * permitted by the taming database.
         * 
         * @param sn
         *            the simple name to check
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
                
                assert (itb.isClass() || itb.isInterface() || itb.isEnum()
                        || itb.isAnnotation() || itb.isWildcardType()
                        || itb.isTypeVariable());
                
                if (!itb.isFromSource()) {
                    // check in taming database
                    if (!taming.isTamed(itb)) {
                        addProblem("Reference to untamed class " +
                                   itb.getName() + ".", sn);
                    }
                }
            } else {
                assert (ib == null || ib instanceof IPackageBinding || 
                        ib instanceof IMethodBinding);
            }
            return true;
        }

        /**
         * Helper method to check a field binding, resolved from a simple name.
         * Ensure that the field is either present in source code or permitted
         * by the taming database.
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
            // getDeclaringClass returns null in this case. It needs to be
            // special-cased here; we allow it.
            if (classBinding == null) {
                return;
            } else if (classBinding.isFromSource()) {
                // add a dependency on the field existing
                state.addFlagDependency(icu, classBinding);
            } else {
                // check in taming database
                if (!taming.isTamed(classBinding)) {
                    addProblem("Field from untamed class " +
                               classBinding.getName() + " accessed.", source);
                    return;
                }

                if (!taming.isAllowed(classBinding, fieldBinding)) {
                    addProblem("Disabled field " + fieldBinding.getName() +
                               " from class " + classBinding.getName() +
                               " accessed.", source);
                }
            } 
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

            // Check if taming is violated for non-source types.
            if (!classBinding.isFromSource()) {
                // check in taming database
                if (!taming.isTamed(classBinding)) {
                    addProblem("Construction of untamed class " +
                               classBinding.getName() + ".", cic.getType());
                    return true;
                }

                if (!taming.isAllowed(classBinding, imb)) {
                    addProblem("Disabled constructor from class " +
                               classBinding.getName() + " called.", 
                               cic.getType());
                    return true;
                }
            }

            // Otherwise, if we are in a constructor context, make sure that it
            // isn't an anonymous type or a non-static inner type.
            if (inConstructorContext()) {
                ITypeBinding currentClass = getContext();
                if (classBinding.isAnonymous()) {
                    addProblem("Construction of anonymous class during " +
                               "instance initialization.", cic.getType());
                    return true;
                }

                ITypeBinding ancestor = classBinding;
                while (ancestor != null 
                       && !Flags.isStatic(ancestor.getDeclaredModifiers())) {
                    if (ancestor.equals(currentClass)) {
                        addProblem("Construction of non-static member " + 
                                   "class " + imb.getName() + 
                                   "during instance initialization.", 
                                   cic.getType());
                        return true;
                    }
                    ancestor = ancestor.getDeclaringClass();
                }
            }

            return true;
        }
        
        /**
         * Check a superconstructor invocation. Look up the constructor called
         * in the taming database for non-source types. 
         * 
         * @param sci 
         *            the SuperConstructorInvocation to check
         * @return true to visit children of this node
         */
        public boolean visit(SuperConstructorInvocation sci) {
            IMethodBinding imb = sci.resolveConstructorBinding();
            ITypeBinding classBinding = imb.getDeclaringClass();

            // Check if taming is violated for non-source types.
            // If the superclass is an untamed class, we have already flagged
            // it. No dependency needed here; we assume that the superclass
            // can't change from being from source to being part of the library.
            if (!classBinding.isFromSource() && taming.isTamed(classBinding)
                && !taming.isAllowed(classBinding, imb)) {
                addProblem("Disabled superconstructor called.", sci);
            }
            
            return true;
        }
        
        /**
         * Check a method invocation. Ensure that the method being called is
         * either present in source code, or permitted by the taming database.
         * Additionally, if we are in a constructor context, forbid calling
         * local non-static methods.
         * 
         * @param mi
         *            the method invocation to check
         * @return true to visit children of this node
         */
        public boolean visit(MethodInvocation mi) {
            IMethodBinding imb = mi.resolveMethodBinding();
            ITypeBinding classBinding = imb.getDeclaringClass();
            // Unlike for a field, getDeclaringClass on a method binding will 
            // never return null.  It returns java.lang.Object for methods
            // invoked on arrays.
            
            // TODO: better detection of when taming is necessary.
            if (classBinding.isFromSource()) {
                // add deep dependency on method existing
                state.addDeepDependency(icu, classBinding);
            } else {
                // check in taming database
                if (!taming.isTamed(classBinding)) {
                    addProblem("Method from untamed class " +
                               classBinding.getName() + " called.", 
                               mi.getName());
                    return true;
                }

                if (!taming.isAllowed(classBinding, imb)) {
                    addProblem("Disabled method " + imb.getName() +
                               " from class " + classBinding.getName() +
                               " called.", mi.getName());
                    return true;
                }
            }
            // If we are in constructor or instance initializer, forbid 
            // non-static local method calls.  These are calls with no explicit
            // target object expression; calls using "this" are flagged in the
            // checks on ThisExpression nodes.
            if (inConstructorContext() && !Flags.isStatic(imb.getModifiers())
                && mi.getExpression() == null) {
                addProblem("Called local non-static method " + 
                           imb.getName() + " during instance " +
                           "initialization.", mi.getName());
            }
            return true;
        }

        /**
         * Check a supermethod invocation.  If we are in a constructor context,
         * forbid all non-static supermethods.  Otherwise, ensure that the
         * method being called is either present in source code, or permitted
         * by the taming database.
         * 
         * @param mi
         *            the method invocation to check
         * @return true to visit children of this node
         */
        public boolean visit(SuperMethodInvocation smi) {
            IMethodBinding imb = smi.resolveMethodBinding();
            if (inConstructorContext() && smi.getQualifier() == null
                && Flags.isStatic(imb.getModifiers())) {
                addProblem("Called non-static local supermethod " + 
                           imb.getName() + " during instance initialization.",
                           smi.getName());                
            }
            
            ITypeBinding classBinding = imb.getDeclaringClass();
            
            // Check if taming is violated for non-source types.  If the 
            // superclass is an untamed class, we have already flagged it.
            if (classBinding.isFromSource()) {
                // we depend on the method existing
                state.addDeepDependency(icu, classBinding);
            } else if (taming.isTamed(classBinding)
                     && !taming.isAllowed(classBinding, imb)) {
                addProblem("Disabled method " + imb.getName() +
                           " from superclass called.", smi);
            } else {
                try {
                    ITypeBinding superTB = getContext().getSuperclass();
                    if (taming.implementsOverlay(getContext(), taming.SELFLESS)
                        && (superTB == null 
                            || superTB.getQualifiedName()
                            	    .equals("java.lang.Object"))
                        && imb.getName().equals("equals")) {
                        addProblem("Can't call super.equals() from a Selfless "
                                   + "class with superclass java.lang.Object.",
                                   smi.getName());
                    }
                } catch (JavaModelException jme) {
                    addProblem("Analysis incomplete: BUG IN VERIFIER or I/O " +
                               "error (JavaModelException) encountered " +
                               "analyzing supermethod invocation.",
                               smi.getName());
                }
            }
            return true;
        }

        /**
         * Get the current class by looking in the code context.
         * 
         * @return the type binding of the current class
         * @throws NullPointerException if not in a code context.
         */
        ITypeBinding getContext() {
            BodyDeclaration bd = codeContext.peek();
            ASTNode bdParent = bd.getParent();
            
            // Two possibilities: either bdParent is an AbstractTypeDeclaration
            // or it is an AnonymousClassDeclaration.
            if (bdParent instanceof AbstractTypeDeclaration) {
                return ((AbstractTypeDeclaration) bdParent).resolveBinding();
            } else {
                return ((AnonymousClassDeclaration) bdParent).resolveBinding();
            }
        }

        /**
         * Test whether we are in a constructor context, i.e. at a program point
         * at which the current object may be incompletely initialized.  This
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
                // constructor in the corresponding class.
                return !Flags.isStatic(init.getModifiers());
            } else if (bd instanceof MethodDeclaration) {
                MethodDeclaration md = (MethodDeclaration) bd;
                return md.isConstructor();
            } else if (bd instanceof FieldDeclaration) {
                FieldDeclaration fd = (FieldDeclaration) bd;
                // Non-static field declarations are essentially part of every
                // constructor in the corresponding class.
                return !Flags.isStatic(fd.getModifiers());
            } else {
                return false;
                // Why other cases are never constructor contexts:
                // AbstractTypeDeclaration (AnnotationTypeDeclaration, 
                //   EnumDeclaration, TypeDeclaration) are never added to
                //   codeContext as they don't directly contain code.
                // AnnotationTypeMemberDeclaration: 
                //   no instance fields in an annotation
                // EnumConstantDeclaration: 
                //   essentially, static field declarations
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
            codeContext.push(md);
            SimpleName name = md.getName();
            
            if (Modifier.isNative(md.getModifiers())) {
                addProblem("Native method " + name + ".", name);
            }
            
            // TODO: ban readObject and writeObject?
            // They allow an instance to "know" that it is being serialized;
            // determinism is then not fully guaranteed across machines?
                   
            /*
            // TODO? Check implicit superconstructor invocation. 
            if (md.isConstructor()) {
                List statements = md.getBody().statements();
                
                Statement first = null;
                boolean isEmpty = statements.isEmpty();
                if (!isEmpty) {
                    first = (Statement) statements.get(0);
                }
                
                if (isEmpty || !(first instanceof SuperConstructorInvocation)) {
                    ITypeBinding sc = md.resolveBinding()
                                        .getDeclaringClass().getSuperclass();
                     
                    if (!sc.isFromSource() && taming.isTamed(sc)) {
                        IMethodBinding[] methods = sc.getDeclaredMethods();
                        for (IMethodBinding imb : methods) {
                            if (imb.isConstructor() && 
                                imb.getParameterTypes().length == 0) {
                                if (!taming.isAllowed(sc, imb)) {
                                    addProblem("Implicit call to disabled " +
                                               "constructor.", 
                                               md.getName()); 
                                }
                                return true;
                            }
                            // TODO: YUCK!!! What if we dispatch incorrectly?
                            // (this is possible in the face of varargs...)
                            // Is there a way to add a super() and see what it
                            // maps to?  Alternately, should we have the
                            // invariant that the zero-ary constructor must 
                            // always be allowed for any tamed class?
                            // Also consider *implicit* default constructor!
                        }
                        addProblem("BUG: Could not find constructor " + 
                                   "implicitly invoked for this method.",
                                   md.getName());
                    }
                }
            }
            */
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

        /*
         * ClassDeclarations and InterfaceDeclarations have different production
         * rules in the Java grammar, but are represented by the same node type
         * in Eclipse.
         */
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
            System.out.println(". type " + itb.getName());
            
            // Add a flag dependency on all immediate supertypes as a change to
            // their flags would affect this type's flags. 
            ITypeBinding superTB = itb.getSuperclass();
            if (superTB != null) {
                state.addFlagDependency(icu, superTB);    
            }
            for (ITypeBinding i : itb.getInterfaces()) {
                state.addFlagDependency(icu, i);
            }
            
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
                    // System.out.println("Field " + name + ":");
                    int modifiers = fb.getModifiers();
                    if (Modifier.isStatic(modifiers)) {
                        if (Modifier.isFinal(modifiers)) {
                            ITypeBinding fieldTB = fb.getType();
                            // must be Powerless

                            state.addFlagDependency(icu, fieldTB);

                            if (!taming.implementsOverlay(fieldTB,
                                    taming.POWERLESS)) {
                                addProblem("Non-powerless static field " + name
                                           + ".", ((IField) fb.getJavaElement())
                                                      .getNameRange());
                            }
                        } else {
                            addProblem("Non-final static field " + name + ".",
                                       ((IField) fb.getJavaElement())
                                           .getNameRange());
                        }
                    }
                }

                // 4.4b: Selfless classes and interfaces cannot be equatable.
                if (isEquatable && isSelfless) {
                    addProblem("A type cannot be both Selfless and Equatable.",
                               type.getNameRange());
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

                if (superTB != null) {
                    // System.out.println("Superclass "
                    //         + superTB.getQualifiedName());

                    // See what honoraries superclass has; make sure that all
                    // are implemented by this class.
                    // TODO: If supertype is from source, we are already OK
                    // -- except for honoraries from interfaces we implement.
                    // refactor to avoid double-errors here.
                    Set<IType> unimp = taming.unimplementedHonoraries(sth);
                    for (IType i : unimp) {
                        addProblem("Honorary interface " + i.getElementName() +
                                   " not inherited from " + 
                                   superTB.getName(), 
                                   type.getNameRange());
                    }
                }

                // 4.4c: Object identity must not be visible
                if (isSelfless) {
                    boolean superTypeSelfless = false;
                    IType supertype = null;
                    if (superTB != null) {
                        supertype = (IType) superTB.getJavaElement();
                        
                        // We depend on whether or not superclass is selfless
                        state.addFlagDependency(icu, superTB);
                        
                        ITypeHierarchy ssth = 
                            supertype.newSupertypeHierarchy(null);
                        if (ssth.contains(taming.SELFLESS)) {
                            superTypeSelfless = true;
                        }
                    }
                    
                    if (!superTypeSelfless) {
                        if (superTB != null 
                            && !superTB.getQualifiedName()	
                            	    .equals("java.lang.Object")) {
                            addProblem("Selfless class extends non-Selfless " +
                                       "class other than java.lang.Object.",
                                       type.getNameRange());
                        }
                        
                        // TODO: fix grody hack below.
                        IMethod equalsMethod = 
                            type.getMethod("equals", new String[]{"QObject;"});
                        IMethod otherEqualsMethod = type.getMethod("equals", 
                        	           new String[]{"Qjava.lang.Object;"});
                        if (!equalsMethod.exists() 
                            && !otherEqualsMethod.exists()) {
                            addProblem("Selfless class does not override " +
                                       "equals(Object).",
                                       type.getNameRange());
                        }
                    }
                }
                
                // Checks on fields
                if (isPowerless 
                    /* && !taming.isDeemed(type, taming.POWERLESS) */) {
                    if (sth.contains(taming.TOKEN)) {
                        addProblem("Powerless type " + type.getElementName() +
                                   " can't extend Token.", type.getNameRange());
                    }

                    verifyAllFieldsAre(itb, taming.POWERLESS);

                } else if (isImmutable 
                           /* && !taming.isDeemed(type, taming.IMMUTABLE) */) {

                    verifyAllFieldsAre(itb, taming.IMMUTABLE);
                } else if (isSelfless
                           /* && !taming.isDeemed(type, taming.SELFLESS) */) {
                    verifyAllFieldsAre(itb, taming.SELFLESS);
                }
               
                                
                // Check for methods implemented
                LinkedList<ITypeBinding> ifQueue =
                    new LinkedList<ITypeBinding>();
                LinkedList<IMethodBinding> methodsNeeded = 
                    new LinkedList<IMethodBinding>();
                
                // Recursive descent into superclasses to find inherited 
                // interfaces not necessary because any methods from 
                // superclasses' interfaces that they fail to inherit are
                // already errors.  So, need to include only interfaces
                // reachable by interface-implementation relations.
                // Start with interfaces directly implemented by this class.
                for (ITypeBinding i : itb.getInterfaces()) {
                    ifQueue.add(i);
                }                            
                // Then find all methods needed, possibly required by interfaces
                // that are transitively implemented.
                while (!ifQueue.isEmpty()) {
                    ITypeBinding current = ifQueue.remove();
                    for (ITypeBinding i : current.getInterfaces()) {
                        ifQueue.add(i);
                    }
                    for (IMethodBinding i : current.getDeclaredMethods()) {
                        methodsNeeded.add(i);
                    }
                }

                // Check that all needed methods are implemented by methods
                // that are not tamed away.
                ITypeBinding current = itb;               
                while (current != null && !methodsNeeded.isEmpty()) {
                    // 'current' affects whether interface is fulfilled
                    state.addDeepDependency(icu, current);
                    IMethodBinding dm[] = current.getDeclaredMethods();
                    LinkedList<IMethodBinding> newNeeds = 
                        new LinkedList<IMethodBinding>();
                    for (IMethodBinding need : methodsNeeded) {
                        boolean needFilled = false;
                        for (IMethodBinding have : dm) {
                            // See documentation for IBinding.equals().  The
                            // ITypeBindings for 'have' and 'need' should be in
                            // the same cluster, but if they aren't, it will 
                            // cause a false positive or a verifier error below,
                            // not a loss of soundness.
                            if (have.isSubsignature(need) &&
                        	need.isSubsignature(have)
                        	 /*   have.getName().equals(need.getName())
                                && Arrays.equals(have.getParameterTypes(),
                                                 need.getParameterTypes()) */ ) {
                                needFilled = true;
                                if (taming.isTamed(current) &&
                                    !taming.isAllowed(current, have)) {
                                    addProblem("Method " + need.getName() +
                                               " required by interface " +
                                               "satisfied by banned method.",
                                               type.getNameRange());
                                }
                                break;
                            } 
                        }
                        if (!needFilled) {
                            newNeeds.add(need);
                        }
                    }
                    methodsNeeded = newNeeds;
                    current = current.getSuperclass();
                }
                if (!methodsNeeded.isEmpty()) {
                    addProblem("VERIFIER ERROR: Couldn't find implementation " +
                               "for these methods mandated by interfaces: " +
                               methodsNeeded,  type.getNameRange());                    
                }
            } catch (JavaModelException jme) {
                jme.printStackTrace(); // TODO: prettier debug
                addProblem("Analysis incomplete: BUG IN VERIFIER or I/O " +
                           "error (unhandled exception) encountered " +
                           "analyzing type" + type.getElementName() + ".");
            } catch (Throwable e) {
                e.printStackTrace(); // TODO: prettier debug
                addProblem("Analysis incomplete: BUG IN VERIFIER or I/O " +
                           "error (unhandled exception) encountered " +
                           "analyzing type" + type.getElementName() + ".");
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
            Set<ITypeBinding> needCheck = findClasses(itb, mi);
            for (ITypeBinding i : needCheck) {
                verifyFieldsAre(i, mi, itb);
            }
        }

        /**
         * Find the set of classes all of whose fields must satisfy a given
         * marker interface. This requires traversal of supertypes and enclosing
         * types. Classes already declared to implement the marker interface are
         * not returned.  This method updates dependencies during the traversal.
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
                if (next.isMember() 
                    && !Modifier.isStatic(next.getDeclaredModifiers())) {
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
         * honorarily implement the specified marker interface.  Exception:
         * Selfless is special-cased so that its fields need only be final.
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
                    if (Flags.isFinal(modifiers) && 
                	  !Flags.isTransient(modifiers)) {
                        // Field must implement mi
                        ITypeBinding fieldTB = fb.getType();

                        // add dependency on type of field
                        state.addFlagDependency(icu, fieldTB);

                        if (taming.implementsOverlay(fieldTB, mi)
                            || mi == taming.SELFLESS) {
                            // OKAY
                        } else if (itb.equals(candidate)) {
                            addProblem(
                                String.format("Non-%s field %s in %s %s", 
                                    miName, fieldName, miName, candName), 
                                ((IField) fb.getJavaElement()).getNameRange());
                        } else { // type != candidate
                            addProblem(
                               String.format("Non-%s field %s from %s in %s %s",
                                 miName, fieldName, typeName, miName, candName),
                               ((IType) candidate.getJavaElement())
                                   .getNameRange());
                        }
                    } else if (itb.equals(candidate)) {
                	String bad = (Flags.isFinal(modifiers))
                		         ? "Transient" : "Non-final";
                        addProblem(String.format("%s field %s in %s %s", bad,
                        			 fieldName, miName, candName),
                                   ((IField) fb.getJavaElement())
                                       .getNameRange());
                    } else { // type != candidate
                	String bad = (Flags.isFinal(modifiers))
		         ? "Transient" : "Non-final";
                        addProblem(
                            String.format("%s field %s from %s in %s %s",
                                bad, fieldName, typeName, miName, candName),
                            ((IType) candidate.getJavaElement())
                                .getNameRange());
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
         * is Equatable.  If it is an addition or string concatenation 
         * operation (+), ensure that the second and succesive arguments are
         * safe to call toString() on.  This assumes that all types that unbox
         * to primitives have their toString() method enabled.
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
                    ITypeBinding leftTB = 
                        ie.getLeftOperand().resolveTypeBinding();
                    if (taming.implementsOverlay(leftTB, taming.EQUATABLE)) {
                        // need to recheck if left type becomes un-Equatable
                        state.addFlagDependency(icu, leftTB);
                        return true;
                    } else {
                        ITypeBinding rightTB = 
                            ie.getRightOperand().resolveTypeBinding();
                        if (taming.implementsOverlay(rightTB, 
                                                     taming.EQUATABLE)) {
                            // need to recheck if right type becomes
                            // un-Equatable
                            state.addFlagDependency(icu, rightTB);
                            return true;
                        } else {
                            addProblem("Pointer equality test on non-Equatable "
                                       + "types", ie);
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
                //try {
                    // left operand always statically a string (or boxed 
                    // primitive)
                    // checkToString(ie.getLeftOperand());
                    checkToString(ie.getRightOperand());
                    
                    if (ie.hasExtendedOperands()) {
                        for (Object o : ie.extendedOperands()) {
                            Expression e = (Expression) o;
                            checkToString(e);
                        }
                    }
                // } catch {}
            }
            
            return true;
        }

        /**
         * Check an assignment. If the expression is a string append operation,
         * (+=), then ensure that the right hand side is safe to call toString
         * on.  This assumes that all types that unbox to primitives have their
         * toString() method enabled.
         * 
         * @param te
         *            the infix expression to check
         * @return true to visit children of this node
         */      
        public boolean visit(Assignment a) {
            if (a.getOperator() == Assignment.Operator.PLUS_ASSIGN) {
                checkToString(a.getRightHandSide());
            }
            
            return true;
        }
        
        /*
         * Checks if invoking toString() on expression e violates taming
         * decisions.  Adds problems if so.
         */
        void checkToString(Expression e) {
            ITypeBinding itb  = e.resolveTypeBinding();
            
            if (itb.isPrimitive()) {
                // not a problem
            } else if (itb.isArray() || itb.isGenericType()) { 
                addProblem("Taming-prohibited implicit call to " 
                           + itb.getName() + ".toString()", e);
            } else { // no funny stuff, itb should be in the class hierarchy
                while (itb != null) {
                    IMethodBinding[] imbs = itb.getDeclaredMethods();
                    for (IMethodBinding imb : imbs) {
                        // Most specific statically resolvable toString method
                        if (imb.getName().equals("toString")
                                && imb.getParameterTypes().length == 0) {
                            if (itb.isFromSource()) {
                                // add dependency on toString() existing
                                state.addDeepDependency(icu, itb);
                                return;
                            } else if (taming.isTamed(itb) &&
                                       taming.isAllowed(itb, imb)) {
                                return;
                            } else {
                                addProblem("Taming-prohibited implicit call to "
                                           + itb.getName() + ".toString()", e);
                                return;
                            }
                        }      
                    }
                    itb = itb.getSuperclass();
                }
                
                addProblem("VERIFIER BUG: unable to resolve toString() for "
                           + e, e);
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
            assert (codeContext.peek() == init);
            codeContext.pop();
        }

        public void endVisit(FieldDeclaration fd) {
            assert (codeContext.peek() == fd);
            codeContext.pop();
        }

        public void endVisit(MethodDeclaration md) {
            assert (codeContext.peek() == md);
            codeContext.pop();
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
            assert (codeContext.peek() == atmd);
            codeContext.pop();
        }

        public void endVisit(EnumConstantDeclaration ecd) {
            // System.out.println("End visit of <" + bd + ">");
            assert (codeContext.peek() == ecd);
            codeContext.pop();
        }
    }
}