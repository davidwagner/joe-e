// Copyright 2005-07 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e.eclipse;

import org.eclipse.jdt.core.*;

import org.eclipse.jdt.core.dom.*;

import java.io.File;
import java.io.PrintStream;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Queue;
import java.util.Arrays;

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
    // final IJavaProject project;
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
    Verifier(BuildState state, Taming taming) {
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
        
            VerifierASTVisitor vav = new VerifierASTVisitor(icu, parse.getAST(),
                                         dependents, problems);
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
        final ITypeBinding OBJECT;
        final ITypeBinding THROWABLE;
        final ITypeBinding ERROR;

        // Contains a stack of body declaration nodes that are ancestors of the
        // current node.  The top element is the context in which the current
        // node is executed.
        final Stack<BodyDeclaration> codeContext;
        // Contains a stack of the tags (as per the constants in BuildState) for
        // the classes that are ancestors of the current node.  The top element
        // gives the flags for the current class.
        final Stack<Integer> classTags;
        
        // For each class defined in the file, maintain information about it to
        // facilitate a global check to ensure that synthetic fields added to
        // allow access to local variables by inner classes don't violate
        // Immutable or Powerless.
        class ClassInfo {
            final int classTags; // tags on this class
            final Set<IVariableBinding> immutableVars; 
                // enclosing-scope immutable locals referenced by this class
            final Set<IVariableBinding> localVars; 
                // enclosing-scope non-immutable locals referenced by this class
            final List<ClassInstanceCreation> constructorCalls; 
                // calls to local constructors
            ClassInfo(int classTags) {
                this.classTags = classTags;
                // initially all references vacuously satisfy 
                // Immutable and Powerless
                localVars = new HashSet<IVariableBinding>();
                immutableVars = new HashSet<IVariableBinding>();
                constructorCalls = new LinkedList<ClassInstanceCreation>();
            }
            
            // add a reference to an immutable local
            void addImmutableVar(IVariableBinding newVar) {
                immutableVars.add(newVar);
            }
            
            // add a reference to a non-immutable local
            void addVar(IVariableBinding newVar) {
                localVars.add(newVar);
            }
            
            void addCall(ClassInstanceCreation cic) {
                constructorCalls.add(cic);
            }
        }     
        
        final HashMap<ITypeBinding, ClassInfo> classInfo = 
            	new HashMap<ITypeBinding, ClassInfo>();
        
        /**
         * Create a visitor for a specified compilation unit that appends Joe-E
         * verification errors to a specified list of problems.
         * 
         * @param icu
         *            the compilation unit to analyze
         * @param problems
         *            a list of problems to append Joe-E verification errors to
         */
        VerifierASTVisitor(ICompilationUnit icu, AST ast,
                Set<ICompilationUnit> dependents, List<Problem> problems) {
            // System.out.println("VAV init");
            this.icu = icu;
            this.dependents = dependents;
            this.problems = problems;
            this.codeContext = new Stack<BodyDeclaration>();
            this.classTags = new Stack<Integer>();
            OBJECT = ast.resolveWellKnownType("java.lang.Object");
            THROWABLE = ast.resolveWellKnownType("java.lang.Throwable");
            ERROR = ast.resolveWellKnownType("java.lang.Error");
        }

        /*
         * Convenience methods to add problems.
         */
        private void addProblem(String description, ASTNode source) {
            problems.add(new Problem(description, source.getStartPosition(),
                                     source.getLength()));
        }
        
        private void addProblem(String description, ASTNode source, 
                                ITypeBinding itb) {
            String classComment = taming.getTamingComment(itb);
            String fullDescription = description + 
                (classComment == null ? "" : (": " + classComment));
            problems.add(new Problem(fullDescription, source.getStartPosition(),
                                     source.getLength()));
        }       

        private String describe(String description, ITypeBinding itb, 
                                IVariableBinding field) {
            String fieldComment = taming.getTamingComment(itb, field);
            return description + 
                (fieldComment == null ? ""  : (": " + fieldComment));
        }
        
        private String describe(String description, ITypeBinding itb, 
                                IMethodBinding method) {
            String methodComment = taming.getTamingComment(itb, method);
            return description + 
                (methodComment == null ? "" : (": " + methodComment));
        }

        private void addProblem(String description, ASTNode source, 
                                ITypeBinding itb, IVariableBinding field) {
            problems.add(new Problem(describe(description, itb, field),
                    source.getStartPosition(), 
                    source.getLength()));
        }

        private void addProblem(String description, ASTNode source, 
                                ITypeBinding itb, IMethodBinding method) {
            problems.add(new Problem(describe(description, itb, method), 
                                     source.getStartPosition(), 
                                     source.getLength()));
        }

        private void addProblem(String description) {
            problems.add(new Problem(description));
        }           
        
        private void addProblem(String description, ISourceRange source) {
            problems.add(new Problem(description, source));
        }
        
        private void addProblem(String description, IType type) {
            try {
                ISourceRange range = type.getNameRange();
                addProblem(description, range);
            } catch (JavaModelException jme) {
                addProblem("Analysis incomplete: BUG IN VERIFIER or I/O error "
                        + "(JavaModelException) encountered finding source "
                        + "for type " + type.getElementName());             
            }
        }

        private void addProblem(String description, IVariableBinding field) {
            try {
                ISourceRange range = 
                    ((IField) field.getJavaElement()).getNameRange();
                addProblem(description, range);
            } catch (JavaModelException jme) {
                addProblem("Analysis incomplete: BUG IN VERIFIER or I/O error "
                           + "(JavaModelException) encountered finding source "
                           + "for field binding " + field.getName());
            }
        }
        
        private void addProblem(String description, ITypeBinding itb) {
            try {
                ISourceRange range = 
                    ((IType) itb.getJavaElement()).getNameRange();
                addProblem(description, range);
            } catch (JavaModelException jme) {
                addProblem("Analysis incomplete: BUG IN VERIFIER or I/O error "
                           + "(JavaModelException) encountered finding source "
                           + "for type binding " + itb.getName());
            }
        }
        
        /**
         * Check a simple name. If the name corresponds to a field, ensure
         * that the field being accessed is either present in source code, or
         * permitted by the taming database.
         * 
         * @param sn
         *            the simple name to check
         * @return true to visit children of this node
         */
        /* 
         * These checks should preclude the necessity of explicitly checking
         * FieldAccess or SuperFieldAccess expressions.
         */
        public boolean visit(SimpleName sn) {
            IBinding ib = sn.resolveBinding();
            if (ib instanceof IVariableBinding) {
                IVariableBinding ivb = (IVariableBinding) ib;
                if (ivb.isField()) {
                    checkFieldBinding(ivb, sn);
                } else {
                    // Local variable (including method parameters)
                    IMethodBinding declaringMethod = ivb.getDeclaringMethod();
                    if (declaringMethod != null && getCurrentClass() != 
                                        declaringMethod.getDeclaringClass()) {
                              	
                        try {
                            if (taming.implementsOverlay(ivb.getType(),
                                                         taming.POWERLESS)) {
                                return true;
                            }
                                                       
                            boolean immutableVar = 
                                taming.implementsOverlay(ivb.getType(),
                                                         taming.IMMUTABLE);
                            
                            if (inImmutableClass() && !immutableVar) {
                                addProblem("Non-Immutable local variable " + sn
                                           + " from enclosing scope referenced "
                                           + "in Immutable class", sn);
                            } else if (inPowerlessClass()) {
                                addProblem("Non-Powerless local variable " + sn
                                           + " from enclosing scope referenced "
                                           + "in Powerless class", sn);
                            } else if (immutableVar) {
                                // Record reference to immutable type
                                classInfo.get(getCurrentClass())
                                    .addImmutableVar(ivb);
                            } else {
                    	    	// Record reference to non-immutable type
                                classInfo.get(getCurrentClass()).addVar(ivb);
                            }
                        } catch (JavaModelException jme) {
                            addProblem("Analysis incomplete: BUG IN VERIFIER " +
                                       "or I/O error (JavaModelException) " +
                                       "encountered analyzing outer method's " +
                                       "local variable " + sn, sn);
                        }
                    }
                }
            } else if (ib instanceof ITypeBinding) {
                ITypeBinding itb = (ITypeBinding) ib;
                
                assert (itb.isClass() || itb.isInterface() || itb.isEnum()
                        || itb.isAnnotation()
                        || itb.isTypeVariable());
                
                // we depend on the type being tamed and/or verified as Joe-E
                state.addFlagDependency(icu, itb);
                
                if (itb.isTypeVariable()) {
                    // OK
                } else if (!taming.isJoeE(itb) && !taming.isTamed(itb)) {
                    addProblem("Reference to disabled class " +
                            itb.getName(), sn, itb);                   
                }
            } else if (ib instanceof IMethodBinding) {
                // Handled elsewhere since we wish to record the lexical type
                // as well as resolve the binding.
            } else {
                assert (ib == null || ib instanceof IPackageBinding);
                // Don't worry about these.
            }
            return true;
        }

        /**
         * Helper method to check a field binding, resolved from a simple name.
         * Ensure that the field is either present in source code or permitted
         * by the taming database.  Updates dependencies and problems.
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
            } 

            // Depend on whether class is tamed or if it is Joe-E
            state.addFlagDependency(icu, classBinding);
            
            if (taming.isJoeE(classBinding)) {
                // OK
            } else if (taming.isTamed(classBinding)) {
                if (!taming.isAllowed(classBinding, fieldBinding)) {
                    addProblem("Disabled field " + fieldBinding.getName() +
                               " from class " + classBinding.getName() +
                               " accessed", source, classBinding, fieldBinding);
                }                
            } else {
                addProblem("Field " + fieldBinding.getName() + " from " +
                        "disabled class " + classBinding.getName() + 
                        " accessed", source, classBinding);
            }
        }
        
        /**
         * Check a class instance creation. Look up the constructor called in
         * the taming database for non-source types.  Additionally, if we are in
         * a constructor context (see inConstructorContext()), then if the
         * object being constructed is of a (transitively) inner class of the
         * current class, flag an error: it may be able to see this class'
         * partially initialized state. Anonymous classes are inner classes of
         * the current class and thus also result in an error being flagged.
         * 
         * @param cic   the ClassInstanceCreation to check
         * @return true to visit children of this node
         */
        public boolean visit(ClassInstanceCreation cic) {
            IMethodBinding actualCtor = cic.resolveConstructorBinding();
            ITypeBinding actualClass = actualCtor.getDeclaringClass();
            // The lexical class may differ from the actual class in the case
            // of anonymous types
            ITypeBinding lexicalClass = cic.getType().resolveBinding();
            ITypeBinding currentClass = getCurrentClass();

            // Actual types from source may be local classes         
            if (actualClass != currentClass && isLocal(actualClass)) {
                classInfo.get(getCurrentClass()).addCall(cic);
            }

            // If the lexical class is from the project, we depend on whether
            // or not it is Joe-E; redundant with SimpleName
            // state.addFlagDependency(icu, lexicalClass);
            
            // Check if taming is violated for the constructor invocation.
            // Only applies to non-Joe-E classes (not interfaces).
            // Disabled classes here are covered by the SimpleName check.
            if (!taming.isJoeE(lexicalClass)
                && taming.isTamed(lexicalClass) && lexicalClass.isClass()) {    
                // Find constructor called from non-source type: signature will
                // exactly match that of the implicit synthetic anonymous 
                // constructor that is actually called (JLS3 15.9.5.1)
                IMethodBinding[] methods = lexicalClass.getDeclaredMethods();
                IMethodBinding lexicalCtor = null;
                for (IMethodBinding m : methods) {
                    if (m.isConstructor() 
                        && Arrays.equals(m.getParameterTypes(),
                                         actualCtor.getParameterTypes())) {
                        lexicalCtor = m;
                        break;
                    }
                }
                
                if (lexicalCtor == null) {
                    addProblem("VERIFIER ERROR: Couldn't find constructor for" +
                               " class " + lexicalClass.getName(), 
                               cic.getType());
                    return true;
                } else if (!taming.isAllowed(lexicalClass, lexicalCtor)) {
                    addProblem("Disabled constructor from class " +
                               lexicalClass.getName() + " called", 
                               cic.getType(), lexicalClass, lexicalCtor);
                    return true;
                }
            }

            // If we are in a constructor context, make sure that it
            // isn't an anonymous type or a non-static inner type.
            if (inConstructorContext()) {
                if (actualClass.isAnonymous()) {
                    addProblem("Construction of anonymous class during " +
                               "initialization of enclosing object", 
                               cic.getType());
                    return true;
                }

                ITypeBinding ancestor = actualClass;
                while (ancestor != null 
                       && !Flags.isStatic(ancestor.getDeclaredModifiers())) {
                    ancestor = ancestor.getDeclaringClass();                   
                    if (ancestor == currentClass) {
                        addProblem("Construction of inner class " + 
                                   actualClass.getName() + " during " +
                                   "initialization of enclosing object",
                                   cic.getType());
                        return true;
                    }
                }
            }

            return true;
        }

        /**
         * Test if a type is a local type (or defined within a local type)
         * @param itb
         * @return true if itb is a local type or defined within a local type
         */
        boolean isLocal(ITypeBinding itb) {
            while (itb != null) {
                if (itb.isLocal()) {
                    return true;
                }
                itb = itb.getDeclaringClass();
            }
            return false;
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
            IMethodBinding superCtor = sci.resolveConstructorBinding();
            ITypeBinding classBinding = superCtor.getDeclaringClass();

            // No need for dependency here, we already have a dependency
            // on the SimpleName for the superclass.
            // state.addFlagDependency(icu, classBinding);
            
            // Check if taming is violated for non-source types.
            // If the superclass is a disabled class, we have already flagged
            // it. 
            if (!taming.isJoeE(classBinding) && taming.isTamed(classBinding)
                && !taming.isAllowed(classBinding, superCtor)) {
                addProblem("Disabled superconstructor called", sci, 
                           classBinding, superCtor);
            }
            
            return true;
        }
        
        /**
         * Check a method invocation. Ensure that the method being called is
         * either present in source code, or permitted by the taming database.
         * Additionally, if we are in a constructor context, forbid calling
         * local non-static methods.  Updates dependencies and problems.
         * 
         * @param mi
         *            the method invocation to check
         * @return true to visit children of this node
         */
        public boolean visit(MethodInvocation mi) {
            ITypeBinding lexicalClass = null;
            Expression expression = mi.getExpression();
            if (expression != null) {
                lexicalClass = expression.resolveTypeBinding();
            }
            IMethodBinding imb = mi.resolveMethodBinding();
            ITypeBinding actualClass = imb.getDeclaringClass();
            
            // Unlike for a field, getDeclaringClass on a method binding will 
            // never return null.  It returns java.lang.Object for methods
            // invoked on arrays.

            if (lexicalClass != null) {
                // Add deep dependency on method resolving the way it does.
                // This is against the lexical class, as changes to it may
                // change method dispatch.  Transitive dependencies also cover
                // ancestors of the lexical class.
                // TODO: Dependencies probably still broken if there are several 
                // non-Joe-E source classes involved.  Maybe not worth fixing.
                state.addDeepDependency(icu, lexicalClass);
            }
            
            if (taming.isJoeE(actualClass)) {
                // OK
            } else if (taming.isTamed(actualClass)) {
                if (!taming.isAllowed(actualClass, imb)) {
                    addProblem("Disabled method " + imb.getName() +
                               " from class " + actualClass.getName() +
                               " called", mi.getName(), actualClass, imb);
                }
            } else {
                addProblem("Method from disabled class " +
                           actualClass.getName() + " called", 
                           mi.getName(), actualClass);               
            }
            
            // If we are in constructor or instance initializer, forbid 
            // non-static local method calls.  These are calls with no explicit
            // target object expression; calls using "this" are flagged in the
            // checks on ThisExpression nodes.
            if (inConstructorContext() && !Flags.isStatic(imb.getModifiers())
                && expression == null) {
                addProblem("Called non-static method " + imb.getName() + 
                           " on object being initialized", mi.getName());
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
            Name qualifier = smi.getQualifier();        
            ITypeBinding target = (qualifier == null)
                ? getCurrentClass() : (ITypeBinding) qualifier.resolveBinding();
                        
            if (inConstructorContext()  
                && !Flags.isStatic(imb.getModifiers())
                && (target == getCurrentClass())) {
                addProblem("Called non-static supermethod " + imb.getName() + 
                           " on object being initialized", smi.getName());                
            }
            
            ITypeBinding classBinding = imb.getDeclaringClass();
            
            // Check if taming is violated for non-source types.  If the 
            // superclass is an disabled class, we have already flagged it.
            if (taming.isJoeE(classBinding)) {
                // Can't violate taming and can't be Object.equals()
            } else if (taming.isTamed(classBinding)
                       && !taming.isAllowed(classBinding, imb)) {
                addProblem("Disabled supermethod " + imb.getName() + " from " +
                           "class " + classBinding.getName() + " called", smi,
                           classBinding, imb);
            } else if (BuildState.isSelfless(classInfo.get(target).classTags)) {
                ITypeBinding superTB = target.getSuperclass();
                if (superTB == OBJECT && imb.getName().equals("equals")
                    && Arrays.equals(imb.getParameterTypes(),
                                     new ITypeBinding[] {OBJECT})) {
                    addProblem("Can't call super.equals() for a Selfless " +
                               "class with superclass java.lang.Object",
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
        ITypeBinding getCurrentClass() {
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
         * Check whether the current class is selfless
         * 
         * @return true if the current class is selfless according to its tags
         */
        boolean inSelflessClass() {
            return BuildState.isSelfless(classTags.peek());
        }
        
        /**
         * Check whether the current class is immutable
         * 
         * @return true if the current class is immutable according to its tags
         */
        boolean inImmutableClass() {
            return BuildState.isImmutable(classTags.peek());
        }
                
        /**
         * Check whether the current class is powerless
         * 
         * @return true if the current class is powerless according to its tags
         */
        boolean inPowerlessClass() {
            return BuildState.isPowerless(classTags.peek());
        }
        
        /**
         * Test whether we are in an initializer.
         * 
         * @return true if the traversal is currently within an initializer
         */
        boolean inInitializer() {
            return !codeContext.isEmpty() 
                && codeContext.peek() instanceof Initializer;
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
            if (codeContext.isEmpty()) {
                return false;
            }
            
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
         * If the method being defined is a constructor without an explicit
         * superconstructor, ensure that the default superconstructor is allowed
         * by taming.
         * 
         * @param md    the method declaration being traversed
         * @return true to visit children of this node
         */
        public boolean visit(MethodDeclaration md) {
            codeContext.push(md);
            SimpleName name = md.getName();
            
            if (Modifier.isNative(md.getModifiers())) {
                addProblem("Native method " + name, name);
            }
            
            if (name.getIdentifier().equals("finalize") 
                && md.parameters().isEmpty()) {
                addProblem("Finalizers are not allowed", name);
            } else if (name.getIdentifier().equals("readObject")
                       && md.parameters().size() == 1) {
                SingleVariableDeclaration arg = 
                    (SingleVariableDeclaration) md.parameters().get(0);
                if (arg.getType().resolveBinding().getQualifiedName()
                       .equals("java.io.ObjectInputStream")) {
                    addProblem("Custom serialization behavior is not allowed.");
                }
            } else if (name.getIdentifier().equals("writeObject")
                       && md.parameters().size() == 1) {
                SingleVariableDeclaration arg = 
                    (SingleVariableDeclaration) md.parameters().get(0);
                if (arg.getType().resolveBinding().getQualifiedName()
                       .equals("java.io.ObjectOutputStream")) {
                    addProblem("Custom serialization behavior is not allowed.");
                }    
            }
                
            if (md.isConstructor()) {
                List statements = md.getBody().statements();
                           
                if (statements.isEmpty() || 
                    !(statements.get(0) instanceof SuperConstructorInvocation))
                {
                    ITypeBinding cc = md.resolveBinding().getDeclaringClass();
                    String problem = checkDefaultSuper(cc);
                    if (problem != null) {
                        addProblem(problem, name);
                    }
                }
            }    
            return true;
        }
    
        /**
         * Check that the default superconstructor for a class is safe to call.
         * @param cc    the class to check
         * @return      an error message for a problem, or <code>null</code> 
         *              if no problem
         */
        String checkDefaultSuper(ITypeBinding cc) {
            if (cc.isEnum()) {
                return null;
            }
            
            ITypeBinding sc = cc.getSuperclass();
            
            // If sc is not tamed, the "extends" clause will flag an error
            if (!taming.isJoeE(sc) && taming.isTamed(sc)) {
                // check zero-ary constructor
                IMethodBinding[] methods = sc.getDeclaredMethods();
                for (IMethodBinding imb : methods) {                            
                    if (imb.isConstructor()
                        && imb.getParameterTypes().length == 0
                        && canInvoke(cc, imb)) {
                        if (taming.isAllowed(sc, imb)) {
                            return null;
                        } else {
                            return describe("Implicit call to disabled " +
                                            "superconstructor", sc, imb);
                        }
                    }
                }    
                    
                // Try varargs constructors:
                // subtype... is more specific than supertype..., 
                // primitive... more specific than Object...
                // any incomparable options -> compile error.
                IMethodBinding mostSpecific = null;
                for (IMethodBinding imb : methods) {
                    ITypeBinding[] types = imb.getParameterTypes();
                    if (imb.isConstructor() && types.length == 1
                        && imb.isVarargs() && canInvoke(cc, imb)) {
                        if (mostSpecific == null
                            || types[0].isPrimitive()
                            || types[0].isSubTypeCompatible(
                                 mostSpecific.getParameterTypes()[0])) {
                            mostSpecific = imb;
                        }
                    }
                }
                
                if (mostSpecific == null) {
                    return "VERIFIER BUG: Could not find superconstructor " +
                           "implicitly invoked for this constructor";
                } else if (!taming.isAllowed(sc, mostSpecific)) {                          
                    return describe("Implicit call to disabled varargs " +
                                    "superconstructor", sc, mostSpecific);
                }                       
            }
            return null;
        }
        
        /**
         * Check if a class can invoke a method defined in another class.  This
         * is determined by the access level modifiers on the method and by 
         * whether the class and method belong to the same package.
         * @return true if the method is accessble
         */
        boolean canInvoke(ITypeBinding type, IMethodBinding method) {
            int modifiers = method.getModifiers();
            return Modifier.isPublic(modifiers)
                   || Modifier.isProtected(modifiers)
                   || (!Modifier.isPrivate(modifiers)
                       && type.getPackage() == 
                          method.getDeclaringClass().getPackage());
        }

        /**
         * Record in the codeContext when we visit an annotation type member
         * declaration.
         *
         * @param atmd
         *            the annotation type member declaration being traversed
         * @return true to visit children of this node
         */
        public boolean visit(AnnotationTypeMemberDeclaration atmd) {
            // System.out.println("visit(BodyDeclaration bd) of <" + bd + ">");
            codeContext.push(atmd);
            return true;
        }

        /**
         * Record in the codeContext when we visit an enum constant declaration.
         *
         * @param ecd
         *            the enum constant declaration being traversed
         * @return true to visit children of this node
         */
        public boolean visit(EnumConstantDeclaration ecd) {
            // System.out.println("visit(BodyDeclaration bd) of <" + bd + ">");
            codeContext.push(ecd);
            return true;
        }

        /*
         * Type declarations: call checkType(), which adds the type to
         * classTags after determining what they are.
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
            // Bail out early in the case of a type defined in an initializer.
            // These trigger an Eclipse bug when I ask for their JavaElement (I
            // get the initializer instead!!)
            if (inInitializer()) {
                addProblem("Definition of local type " + td.getName() + " not " +
                            "allowed in an initializer (due to bug in Eclipse)",
                            td.getName());
                return false; // avoid encountering eclipse bugs
            } else {
                checkType((ITypeBinding) td.resolveBinding());
                return true;
            }
            // codeContext.push(td);
        }

        public boolean visit(AnonymousClassDeclaration acd) {
            // Bail out early in the case of a type defined in an initializer.
            // IVariableBindings for local variables defined by the initializer
            // are hard to resolve.  Bugs similar to the one mentioned above may
            // also turn up here?
            if (inInitializer()) {
                Initializer init = (Initializer) codeContext.peek();
                if (Flags.isStatic(init.getModifiers())) {
                    addProblem("Definition of anonymous class not allowed in " +
                               "an initializer.  (Locals defined in an " +
                               "initializer are not supported.)", acd);
                }
                return false; // avoid encountering eclipse bugs
            } else {
                checkType((ITypeBinding) acd.resolveBinding());
                return true;
            }
            // codeContext.push(acd);
        }
        
        /**
         * Verify an ITypeBinding, updating the list of dependents and problems.
         * 
         * @param type
         *            the type to verify
         */
        void checkType(ITypeBinding itb) {
            System.out.println(". type " + itb.getName());
                        
            // Add a deep dependency on supertype as it can affect method call
            // resolution, which is important for taming.  Also a flag 
            // dependency on superinterfaces, in case marker interfaces change. 
            ITypeBinding superTB = itb.getSuperclass();
            if (superTB != null) {
                state.addDeepDependency(icu, superTB);    
            }
            for (ITypeBinding i : itb.getInterfaces()) {
                state.addDeepDependency(icu, i);
            }
            
            // TODO: is there a way to do this using just bindings?
            IType type = (IType) itb.getJavaElement();
            
            
            try {
                if (!Flags.isPrivate(type.getFlags())) {  // TODO: is this right?
                    taming.processJoeEType(type);
                }
                
                ITypeHierarchy sth;
                if (type.isEnum() && type.isAnonymous()) {
                    // workaround for an Eclipse bug(?): anonymous type of a
                    // constant-specific class body of an Enum gives a bogus
                    // supertype hierarchy, so use the enclosing class instead
                    sth = type.getDeclaringType().newSupertypeHierarchy(null);
                } else {
                    sth = type.newSupertypeHierarchy(null);
                }
                
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

                // put tags in classTags
                classTags.add(tags);
                
                // update flags and add dependents
                dependents.addAll(state.updateTags(type, tags));
                
                // Restrictions on static fields.
                // TODO: more more of these checks to other elements, e.g.
                // FieldDeclaration?
                IVariableBinding[] fields = itb.getDeclaredFields();

                for (IVariableBinding fb : fields) {
                    String name = fb.getName();
                    // System.out.println("Field " + name + ":");
                    int modifiers = fb.getModifiers();
                    if (Modifier.isStatic(modifiers)) {
                        if (Modifier.isFinal(modifiers)) {
                            ITypeBinding fieldTB = fb.getType();
                            // must be Powerless; redundant with SimpleName
                            // state.addFlagDependency(icu, fieldTB);

                            if (!taming.implementsOverlay(fieldTB,
                                    taming.POWERLESS)) {
                                addProblem("Non-powerless static field " + name, 
                                           fb);
                            }
                        } else {
                            addProblem("Non-final static field " + name, fb);
                        }
                    }
                }

                // 4.4b: Selfless classes and interfaces cannot be equatable.
                if (isEquatable && isSelfless) {
                    addProblem("A type cannot be both Selfless and Equatable",
                               type);
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
                classInfo.put(itb, new ClassInfo(tags));
                
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
                               " inherited from " + superTB.getName() + 
                               " not explicitly implemented", type);
                }
                
                // If the class has no explicit constructors, verify that
                // implicit superconstructor is permitted by taming.
                boolean defaultConstructor = true;
                for (IMethodBinding m : itb.getDeclaredMethods()) {
                    if (m.isDefaultConstructor()) {
                        break;
                    }
                    else if (m.isConstructor() && !m.isSynthetic()) {
                        defaultConstructor = false;
                        break;
                    }
                }
                if (defaultConstructor) {
                    String problem = checkDefaultSuper(itb);
                    if (problem != null) {
                        addProblem(problem, type);
                    }
                }
                 
                // Selfless checks: Must extend Object and override
                // equals(Object), or extend another selfless class
                if (isSelfless) {
                    if (!taming.implementsOverlay(superTB, taming.SELFLESS)) {
                        if (superTB != OBJECT) {
                            addProblem("Selfless class extends non-Selfless " +
                                       "class other than java.lang.Object",
                                       type);
                        }

                        boolean overridesEquals = false;
                        for (IMethodBinding m : itb.getDeclaredMethods()) {
                            if (m.getName().equals("equals")
                                && Arrays.equals(m.getParameterTypes(), 
                                                 new ITypeBinding[]{OBJECT})) {
                                overridesEquals = true;
                                break;
                            }
                        }
                        
                        if (!overridesEquals) {
                            addProblem("Selfless class does not override " +
                                       "equals(Object)", type);
                        }
                    }
                }
                
                // Powerless check for Token, checks on fields
                if (isPowerless 
                    /* && !taming.isDeemed(type, taming.POWERLESS) */) {
                    if (sth.contains(taming.TOKEN)) {
                            addProblem("Powerless class " + itb.getName() +
                                       " can't extend Token", type);
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
                
                // Recursive ascent into superclasses to find inherited 
                // interfaces not necessary because any interface methods that 
                // superclasses implement incorrectly are already errors in the
                // superclasses.  So, only need to include interfaces explicitly
                // implemented by the current class (possibly transitively).
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
                    IMethodBinding dm[] = current.getDeclaredMethods();
                    LinkedList<IMethodBinding> newNeeds = 
                        new LinkedList<IMethodBinding>();
                    for (IMethodBinding need : methodsNeeded) {
                        boolean needFilled = false;
                        for (IMethodBinding have : dm) {
                            // the (symmetric) "subsignature" relation is used 
                            // to determine interface implementation: JLS3 8.4.2
                            // TODO: **NOT** symmetric! oops! 
                            // -- but is this a problem, i.e. can one override
                            // both ways?
                            if (have.isSubsignature(need)) {
                                needFilled = true;
                                //System.out.println("have: " + have);
                                //System.out.println("need: " + need);
                                if (taming.isTamed(current) &&
                                    !taming.isAllowed(current, have)) {
                                    String description =
                                        describe("Method " + need.getName() +
                                                 " required by interface " +
                                                 "satisfied by disabled method",
                                                 current, have);
                                    addProblem(description, type);
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
                               methodsNeeded,  type);                    
                }
            } catch (Throwable e) {
                e.printStackTrace(); // TODO: prettier debug
                addProblem("Analysis incomplete: BUG IN VERIFIER or I/O " +
                           "error (unhandled exception) encountered " +
                           "analyzing type" + type.getElementName());
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
         *            the marker interface, i.e. Selfless, Immutable, or
         *            Powerless
         * @throws JavaModelException
         */
        void verifyAllFieldsAre(ITypeBinding itb, IType mi)
                throws JavaModelException {
            Set<ITypeBinding> needCheck = findClassesToCheck(itb, mi);
            for (ITypeBinding i : needCheck) {
                verifyFieldsAre(i, mi, itb);
            }
        }
        
        /**
         * Find the set of all classes that contribute instance fields to a 
         * given class, including the class itself.  This will be the class
         * and all of its superclasses.  Classes already declared to implement
         * the marker interface are not returned.
         * 
         * @param type
         *            the type at which to start
         * @param mi
         *            the marker interface whose implementors to skip
         * @return the set of classes found
         * @throws JavaModelException
         */
        Set<ITypeBinding> findClassesToCheck(ITypeBinding itb, IType mi)
                throws JavaModelException {
            Set<ITypeBinding> found = new HashSet<ITypeBinding>();
            found.add(itb);
            ITypeBinding current = itb.getSuperclass();
            
            while (current != null) {
                if (taming.implementsOverlay(current, mi)) {
                    break;
                } else {
                    found.add(current);
                    current = current.getSuperclass();
                }
            }

            return found;
        }

        /**
         * Verify that a class's explicit instance fields are final and
         * honorarily implement the specified marker interface.  For
         * non-static inner classes, also verifies that the declaring class
         * implements the specified interface, corresponding to the synthetic
         * field generated to hold the parent instance. Exception: Selfless is
         * special-cased so that its fields need only be final.
         * 
         * @param type
         *            the type whose instance fields to check
         * @param mi
         *            the marker interface to check implementation of
         * @param candidate
         *            the type for which to report Problems
         * @throws JavaModelException
         */
        void verifyFieldsAre(ITypeBinding type, IType mi, ITypeBinding candidate)
            throws JavaModelException {
            String miName = mi.getElementName();
            String candName = candidate.getName();
            if (candName.length() == 0) {
                candName = "anonymous class";
            } else {
                candName = "class " + candName;
            }
            String typeName = type.getName();
            //
            // Check declared instance fields implement mi
            //
            IVariableBinding[] fields = type.getDeclaredFields();

            for (IVariableBinding fb : fields) {
                String fieldName = fb.getName();
                // System.out.println("Field " + name + ":");
                int modifiers = fb.getModifiers();
                if (!Flags.isStatic(modifiers) && !Flags.isEnum(modifiers)) {
                    if (Flags.isFinal(modifiers) && 
                	  !Flags.isTransient(modifiers)) {
                        // Field must implement mi
                        ITypeBinding fieldTB = fb.getType();

                        // dependent on field implementing mi
                        // mostly redundant with SimpleName, but no harm
                        state.addFlagDependency(icu, fieldTB);

                        if (taming.implementsOverlay(fieldTB, mi)
                            || mi.equals(taming.SELFLESS)) {
                            // OKAY
                        } else if (type.equals(candidate)) {
                            addProblem(
                                String.format("Non-%s field %s in %s %s", 
                                    miName, fieldName, miName, candName), fb);
                        } else { // type != candidate
                            addProblem(
                               String.format("Non-%s field %s from %s in %s %s",
                                             miName, fieldName, typeName,
                                             miName, candName), candidate);
                        }
                    } else if (type.equals(candidate)) {
                	String bad = (Flags.isFinal(modifiers)) ? "Transient"
                                                            : "Non-final";
                        addProblem(String.format("%s field %s in %s %s", bad,
                        			             fieldName, miName, candName),
                        		   fb);
                    } else { // type != candidate
                	String bad = (Flags.isFinal(modifiers)) ? "Transient"
                                                            : "Non-final";
                        addProblem(
                            String.format("%s field %s from %s in %s %s", bad,
                                          fieldName, typeName, miName, 
                                          candName), candidate);
                    }
                }
            }
            
            // Check that enclosing class implements Immutable or Powerless
            // when neccessary.
            if (mi == taming.SELFLESS || Flags.isStatic(type.getModifiers())) {
                return;
            }

            // isLocal means a type that is not a member of any class
            // We can't just check declaringMethod for null since a class
            // defined within another class that is within a method has a
            // declaringMethod.  The only local classes without declaring
            // methods are those defined in initializers and field initializing
            // expressions, which must already be static.
            IMethodBinding declaringMethod = type.getDeclaringMethod();
            if (type.isLocal() && (declaringMethod == null ||
                Flags.isStatic(declaringMethod.getModifiers()))) {
                return;
            }
                
            ITypeBinding parent = type.getDeclaringClass();
            if (parent != null && !taming.implementsOverlay(parent, mi)) {
                String parentName = parent.getName();
                if (type == candidate) {
                    addProblem(String.format("Non-%s enclosing class %s for " +
                                             "%s %s", miName, 
                                             parentName, miName, candName),
                               candidate);
                } else {
                    addProblem(String.format("Non-%s enclosing class %s for " +
                                             "supertype %s of %s %s", 
                                             miName, parentName, typeName, 
                                             miName, candName),
                               candidate);
                }
            }
        }

        /**
         * Check usage of the keyword 'this'. If we are in a constructor
         * context, bare use of the 'this' keyword is prohibited.  Use of 
         * 'this' is permitted as part of a field access, and when it is
         * qualified with a class name other than that of the current class.
         * 
         * @param te
         *            the this expression to check
         * @return true to visit children of this node
         */
        public boolean visit(ThisExpression te) {
            if (inConstructorContext()
                && !(te.getParent() instanceof FieldAccess)
                && (te.getQualifier() == null ||
                    te.getQualifier().resolveBinding() == getCurrentClass())) {
                addProblem("Possible escape of reference to 'this' during " +
                           "its initialization", te);
            }
            return true;
        }

        /**
         * Check an infix expression. If the expression is an object identity
         * comparison (== or !=), then ensure that at least one of the operands
         * is Equatable.  If it is an addition or string concatenation 
         * operation (+), ensure that the arguments are safe to call toString()
         * on.  The implementation assumes that all types that unbox to
         * primitives have their toString() method enabled.
         * 
         * @param ie
         *            the infix expression to check
         * @return true to visit children of this node
         */
        public boolean visit(InfixExpression ie) {
            InfixExpression.Operator op = ie.getOperator();
            if (op == InfixExpression.Operator.EQUALS ||
				op == InfixExpression.Operator.NOT_EQUALS) {
                // Test for Equatability
                try {
                    ITypeBinding leftTB = resolveType(ie.getLeftOperand());
                    if (taming.implementsOverlay(leftTB, taming.EQUATABLE)) {
                        // need to recheck if left type becomes un-Equatable
                        state.addFlagDependency(icu, leftTB);
                        return true;
                    } else {
                        ITypeBinding rightTB = 
                            resolveType(ie.getRightOperand());
                        if (taming.implementsOverlay(rightTB, 
                                                     taming.EQUATABLE)) {
                            // need to recheck if right type becomes
                            // un-Equatable
                            state.addFlagDependency(icu, rightTB);
                            return true;
                        } else {
                            addProblem("Object identity test on non-Equatable "
                                       + "types.  Fields and return values " +
                                       "of parameterized type may require " +
                                       "explicit casts.", ie);
                            // need to recheck if either type becomes Equatable
                            state.addFlagDependency(icu, rightTB);
                            state.addFlagDependency(icu, leftTB);       
                        }
                    }
                
                } catch (JavaModelException jme) {
                    addProblem("Analysis of file incomplete: BUG IN VERIFER " +
                                "or I/O error (unhandled exception) " +
                                "encountered analyzing infix expression", ie);
                }             
            } else if (op == InfixExpression.Operator.PLUS) {
                // left operand NOT always statically a string or boxed 
                // primitive... only one of the first two need be.
                // Just go ahead and check both.
                checkToString(ie.getLeftOperand());
                checkToString(ie.getRightOperand());
                    
                 if (ie.hasExtendedOperands()) {
                    for (Object o : ie.extendedOperands()) {
                        Expression e = (Expression) o;
                        checkToString(e);
                    }
                }
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
        
        /**
         * Check an assert statement.  Ensure that the expression to the right
         * of the ":", if any, is safe to call toString() on.  Note that it is not
         * ensured that the assertion expression is pure; this means that a
         * program will be able to tell whether or not assertions are enabled.
         * TODO: this might be nice to enforce if we can.
         * 
         * @param as
         *            the assert statement to check
         * @return true to visit children of this node
         */
        public boolean visit(AssertStatement as) {
            Expression message = as.getMessage();
            if (message != null) {
                checkToString(as.getMessage());
            }
            return true;
        }
        
        public boolean visit(EnhancedForStatement efs) {
            Expression iterable = efs.getExpression();
            final ITypeBinding itb  = resolveType(iterable);
            if (itb.isArray()) {
                // not a problem
            } else { 
                checkImplicitMethodCall("iterator", itb, iterable);
            }
            return true;
        }
        
        /**
         * Checks if invoking toString() on expression e violates taming
         * decisions.  Adds problems if so.  Allows toString() on primitives
         * and denies the toString() method on arrays.  
         */
        void checkToString(Expression e) {
            final ITypeBinding itb  = resolveType(e);
            
            if (itb.isPrimitive()) {
                // not a problem
            } else if (itb.isArray()) { 
                addProblem("Implicit call of disabled toString() method for " +
                           "array type " + itb.getName(), e);
            } else { // no funny stuff, itb should be in the class hierarchy
                // add dependency on either toString existing, or what the
                // superclass is
                state.addDeepDependency(icu, itb); 
                
                checkImplicitMethodCall("toString", itb, e);
            }
        }

        void checkImplicitMethodCall(String methodName, ITypeBinding itb, 
                                     Expression e) {
            Stack<ITypeBinding> left = new Stack<ITypeBinding>();
            left.add(itb);
            
            while (!left.isEmpty()) {
                ITypeBinding current = left.pop();
                IMethodBinding[] imbs = current.getDeclaredMethods();
                for (IMethodBinding imb : imbs) {
                    // Most specific statically resolvable toString method.
                    // Thankfully, since Object declares this with no args,
                    // that takes precedence over any varargs matches
                    if (imb.getName().equals(methodName)
                            && imb.getParameterTypes().length == 0) {
                        if (!taming.isJoeE(current)
                            && (!taming.isTamed(current) 
                                || !taming.isAllowed(current, imb))) {
                            String description = 
                                describe("Implicit call of disabled method"
                                         + methodName + "for class " +
                                         current.getName(), current, imb);
                            addProblem(description + ".  Fields and return "
                                       + "values of parameter type " +
                                       "may require explicit casts.", e);
                        }
                        return;
                    }      
                }
                if (current.isClass() && current.getSuperclass() != null) {
                    left.push(current.getSuperclass());
                } else { // current is interface
                    // push interfaces onto stack, first interface listed on
                    // top.  Note that this does not handle *multiple* toString
                    // methods if they are tamed differently.
                    ITypeBinding[] superInterfaces = current.getInterfaces();
                    for (int i = superInterfaces.length - 1; i >= 0; --i) {
                        left.push(superInterfaces[i]);
                    }
                    // After interfaces exhausted, try Object
                    if (left.isEmpty()) {
                        left.push(OBJECT);
                    }
                }
            }
            
            addProblem("VERIFIER BUG: unable to resolve " + methodName + 
                       "() for " + e, e);
        }       
        
        /**
         * Conservatively resolves the type of an expression.
         * Aims to be safe in the face of heap pollution to avoid cheating
         * object identity and toString restrictions.  This is tricky, so this
         * method may be broken, leading to security bugs.  It may be impossible
         * to write correctly, due to underspecification.
         * The JLS is quite vague on this as far as I can tell, but from my
         * experiments ?: will throw a class cast exception and (x), ==, and
         * implicit toString() won't, if the type isn't what is expected.
         * The obvious way to handle heap pollution would be to add an implicit
         * cast to the expected type whenever expression might not match its
         * static type (i.e. field reads of parameter types and methods
         * returning same), but this is not what Java does.  It seems to only
         * catch the mistyped value when it is assigned to a variable whose type
         * does not match (including temporaries created for things like ?:), or
         * when a method call on the suspect object fails.
         *
         * @param  the expression to conservatively infer the type of
         */
        ITypeBinding resolveType(Expression e) {
            if (e instanceof ParenthesizedExpression) {
                return resolveType(((ParenthesizedExpression) e).getExpression());
            } else if (e instanceof MethodInvocation) {
                MethodInvocation mi = (MethodInvocation) e;
                IMethodBinding method = mi.resolveMethodBinding();
                IMethodBinding realMethod = method.getMethodDeclaration();
                return realMethod.getReturnType().getErasure();
            } else if (e instanceof SuperMethodInvocation) {
                SuperMethodInvocation mi = (SuperMethodInvocation) e;
                IMethodBinding method = mi.resolveMethodBinding();
                IMethodBinding realMethod = method.getMethodDeclaration();
                return realMethod.getReturnType().getErasure();
            } else if (e instanceof FieldAccess) {
                FieldAccess fa = (FieldAccess) e;
                IVariableBinding field = fa.resolveFieldBinding();
                IVariableBinding realField = field.getVariableDeclaration();
                return realField.getType().getErasure();
            } else if (e instanceof Name) {
                Name name = (Name) e;
                IVariableBinding var = (IVariableBinding) name.resolveBinding();
                IVariableBinding realVar = var.getVariableDeclaration();
                return realVar.getType().getErasure();
            } else {
                return e.resolveTypeBinding().getErasure();
            }
        }
        
        /**
         * Check a TryStatement.
         * Verify that it does not contain a finally clause, and that none of
         * its catch clauses can be used to catch an Error (i.e. are declared
         * to catch Throwable or Error or any of its subtypes).
         */
        public boolean visit(TryStatement ts) {
            for (Object o: ts.catchClauses()) {
                CatchClause cc = (CatchClause) o;
                ITypeBinding itb = cc.getException().getType().resolveBinding();
                if (itb == THROWABLE) {
                    addProblem("Catching type Throwable is not allowed", 
                               cc.getException());
                } else if (itb.isSubTypeCompatible(ERROR)) {
                    addProblem("Catching an Error is not allowed", 
                            cc.getException());
                }
            }
            
            if (ts.getFinally() != null) {
                addProblem("Finally clauses are not allowed",
                           ts.getFinally());
            }
            return true;
        }
        
        /*
         * endVisit: clean-up context information
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

        /*
         * Remove class tag for current class when done traversing that class.
         * There should be an endVisit here for every visit() method that
         * calls checkType().
         */
        public void endVisit(AnnotationTypeDeclaration atd) {
            classTags.pop();
        }

        public void endVisit(EnumDeclaration ed) {
            classTags.pop();
        }

        public void endVisit(TypeDeclaration td) {
            // Bail out early in the case of a type defined in an initializer.
            // These trigger an Eclipse bug when I ask for their JavaElement
            // (I get the initializer instead!!)
            if (!inInitializer()) {
                classTags.pop();
            }
        }
        
        public void endVisit(AnonymousClassDeclaration acd) {
            // Bail out early in the case of a type defined in an initializer.
            // These trigger an Eclipse bug when I ask for their JavaElement
            // (I get the initializer instead!!)
            if (!inInitializer()) {
                classTags.pop();
            }
        }
        
        /*
         * Sanity check to ensure that codeContext and classTags are empty upon
         * the completion of visiting a compilation unit (file).  Also perform
         * the file-global checks required for local classes.
         */
        public void endVisit(CompilationUnit cu) {
            assert (codeContext.isEmpty());
            assert (classTags.isEmpty());
            
            // perform file-global checks: i.e. restrictions on construction of
            // local classes.  
            
            //Iterate over each class in the file
            for (ITypeBinding itb : classInfo.keySet()) {
                ClassInfo entry = classInfo.get(itb);
                int needed = entry.classTags &
                    (BuildState.IMPL_IMMUTABLE | BuildState.IMPL_POWERLESS);
                
                // If class isn't immutable or powerless, skip it
                if (!BuildState.isImmutable(needed)) {
                    continue;
                }
                
                // Check local supertype
                ITypeBinding superTB = itb.getSuperclass();
                if (isLocal(superTB)) {
                    int superTags = reduceTags(needed, superTB, itb);
                    if (!BuildState.isImmutable(superTags)) {
                        addProblem("Superclass of Immutable class " + 
                                   itb.getName() + " may access non-Immutable "
                                   + "local state", itb);
                    } else if (BuildState.isPowerless(needed)
                            && !BuildState.isPowerless(superTags)) {
                        addProblem("Superclass of Powerless class " + 
                                   itb.getName() + " may access non-Powerless "
                                   + "local state", itb);
                    }
                }
                
                // Otherwise, check each constructor in the class.
                for (ClassInstanceCreation call : entry.constructorCalls) {                  
                    // work queue of classes to process
                    ITypeBinding constructed = call.resolveConstructorBinding()
                    	                           .getDeclaringClass();
                    String className = constructed.isAnonymous() 
                        ? "anonymous class" : "class " + constructed.getName();
                    int callTags = reduceTags(needed, constructed, itb);                  
                    if (!BuildState.isImmutable(callTags)) {
                        addProblem("Construction of " + className + " may " +
                                   "grant access to non-Immutable local state", 
                                   call.getType());
                    } else if (BuildState.isPowerless(needed)
                	       && !BuildState.isPowerless(callTags)) {
                        addProblem("Construction of " + className + " may " +
                                   "grant access to non-Powerless local state", 
                                   call.getType());
                    }
                }
            }
        }
        
        /**
         * Given an initial set of tags for immutable/powerless and a type 
         * <code>start</code>to search from (corresponding to a supertype or 
         * constructed class), return the set of tags that are still valid 
         * for the class <code>candidate</code> once all synthetic fields
         * corresponding to locals reachable from <code>start</code> have been
         * factored in.
         * @param callTags  the tags to test whether they still hold
         * @param start     place to start searching for reachable locals
         * @param candidate class to resolve found locals against.  Also, 
         *                  treated as already visited by the search.
         */
        int reduceTags(int callTags, ITypeBinding start, 
                       ITypeBinding candidate) {           
            // Record classes visited: only need to traverse each reachable
            // class once.  Add candidate to exclude anything reachable only
            // by looping back through it.
            HashSet<ITypeBinding> visited = new HashSet<ITypeBinding>();
            visited.add(candidate);
                        
            Queue<ITypeBinding> left =  new LinkedList<ITypeBinding>();
            left.add(start);
            
            // Process reachable classes until exhausted or a non-immutable
            // local is found and thus results won't change with more searching
            while (!left.isEmpty() && callTags != 0) {
                ITypeBinding current = left.remove();
                ClassInfo currentInfo = classInfo.get(current);
                if (currentInfo == null) {
                    // should happen only if the class was skipped due 
                    // to being defined in an initializer; this prior
                    // error should already have been flagged.
                    assert (!problems.isEmpty());
                    continue;
                } else if ((callTags & currentInfo.classTags)
                            == callTags) {
                    // believe declared tags; an error will be flagged
                    // on the class if they are wrong.
                    continue;
                }

                // Update tags
                for (IVariableBinding v : currentInfo.localVars) {
                    if (definedOutside(v, candidate)) {
                        callTags = 0;
                    }
                }              
                if (BuildState.isPowerless(callTags)) {               
                    for (IVariableBinding v : currentInfo.immutableVars) {
                        if (definedOutside(v, candidate)) {
                            callTags = BuildState.IMPL_IMMUTABLE;
                        }
                    }
                }
                
                // check local supertype
                ITypeBinding currentSuper = current.getSuperclass();
                if (currentSuper != null && isLocal(currentSuper) 
                    && visited.add(currentSuper)) {
                    left.add(currentSuper);
                }
                // check constructor calls
                for (ClassInstanceCreation cic :
                    classInfo.get(current).constructorCalls) {
                    ITypeBinding referenced =
                        cic.resolveConstructorBinding()
                        .getDeclaringClass();
                    if (visited.add(referenced)) {
                        left.add(referenced);
                    }
                }
            }

            return callTags;
        }
        
        /**
         * Returns true if the specified variable causes the specified class to
         * have a synthetic field for holding its value.  This is true if the
         * variable is defined outside the class, and false if it is defined 
         * within the class.
         * 
         * @param v     a variable binding for a final local variable (including 
         *              method arguments)
         * @param itb   a class to check
         * @return
         */
        boolean definedOutside (IVariableBinding v, ITypeBinding itb) {
            ITypeBinding varClass = v.getDeclaringMethod().getDeclaringClass();
            while (varClass != null) {
                if (varClass == itb) {
                    return false;
                } else {
                    varClass = varClass.getDeclaringClass();
                }
            }
            
            return true;
        }
    }
}