// Copyright 2005-06 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e.eclipse;

//import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.*;

import org.eclipse.jdt.core.dom.*;

import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;

/**
 * This class contains the actual checks performed by the Joe-E verifier.
 * The builder creates an instance of this class and gives it references to
 * a Java project and the associated build state and taming database.
 * It then invokes the checkICU() method of this class, specifying a
 * compilation unit to be checked.
 *
 * A new instance of this class is created on a clean build, in order to
 * associate it with a new build state.
 */
public class Verifier {
    final IJavaProject project;
    final Taming taming;
    final BuildState state;
    
    /**
     * Create a new verifier object associated with a specific project.  The
     * verifier does not maintain persistent state of its own aside from 
     * pointers to other state objects.
     * 
     * @param project 
     *              the Java project for the verifier to operate on
     * @param state 
     *              the build state for the specified project
     * @param taming
     *              the taming database to use
     * @throws JavaModelException
     */
    Verifier(IJavaProject project, BuildState state, Taming taming) throws JavaModelException {
        this.state = state;
        this.project = project;
        this.taming = taming;
    }
  

	/**
	 * Run the Joe-E verifier on an ICompilationUnit.  Problems encountered are
     * appended to the supplied list of problems.
	 * 
	 * @param icu
	 *            ICompilationUnit on which to run the verifier
     * @param problems
     *            A List of Problems (Joe-E verification errors) to which to 
     *            append Problems encountered
	 * @return a Collection of ICompilationUnits referenced by icu
   	 */
	Collection<ICompilationUnit> checkICU(ICompilationUnit icu, List<Problem> problems)
	{
           
		// Clear any state existing from previous build of icu.
		state.prebuild(icu);
        Set<ICompilationUnit> dependents = new HashSet<ICompilationUnit>();
		
        try {
			// Check for package membership
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
					
			if (pkgName.startsWith("org.joe_e."))
			{
				problems.add(new Problem("Bad package name.  Nice try.",
										 pkg[0].getSourceRange()));
			}
			
			// Types defined in this file
			IType[] itypes = icu.getAllTypes();
			System.out.println("Found " + itypes.length + " types.");
			for(int i = 0; i < itypes.length; ++i)
			{
				// Analyze each type.
				IType type = itypes[i];
				System.out.println("Analyzing " + type.getFullyQualifiedName() + ".");
			
				checkIType(type, dependents, problems);
			}
			
			// checks that require ugly DOM hacking directly
		
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setSource(icu);
			parser.setResolveBindings(true);
			ASTNode parse = parser.createAST(null);
			VerifierASTVisitor vav = new VerifierASTVisitor(icu, problems);
			parse.accept(vav);
		}	
		catch (Exception e) // TODO: !!!
		{
			e.printStackTrace();
		}
		
		System.out.println(problems); // TODO: fix ugly debug
		return dependents;
	}

	/**
     * Verify an IType, updating the list of dependents and problems.
     * 
     * @param type
     *          the type to verify
     * @param dependents
     *          a set of dependent ICompilationUnits to update with additional 
     *          dependencies discovered while traversing this type
     * @param problems
     *          a set of Problems (verification errors) to update with additional
     *          problems discovered while traversing this type
	 */
	void checkIType(IType type, Set<ICompilationUnit> dependents,
                    List<Problem> problems)
	{	
        try {
            //TODO: use progress monitor?
        
            ITypeHierarchy sth = type.newSupertypeHierarchy(null);
        
            // Marker interfaces here = implements in BASE type system
            boolean isSelfless = sth.contains(taming.SELFLESS);
            boolean isImmutable = sth.contains(taming.IMMUTABLE);
            boolean isPowerless = sth.contains(taming.POWERLESS);
            boolean isEquatable = sth.contains(taming.ENUM) ||
                sth.contains(taming.TOKEN); //TODO: change if MI is added for this
        
            int tags = isSelfless ? BuildState.IMPL_SELFLESS  : 0;
            tags |=   isImmutable ? BuildState.IMPL_IMMUTABLE : 0;
            tags |=   isPowerless ? BuildState.IMPL_POWERLESS : 0;
            tags |=   isEquatable ? BuildState.IS_EQUATABLE   : 0;    
        
            // update flags and add dependents
            dependents.addAll(state.updateTags(type, tags));
        
            // I don't think these need special handling here. Should test.
			// Annotations are a special case of interfaces with (I believe)
			// no additional abilities.
			// Enumerations are final classes without run-time constructors
			// in which the enumeration values are implicitly static,
			// implicitly final fields.
			
			// if (type.isAnnotation() || type.isEnum()) {
			// }
			
			// Restrictions on fields.
			IField[] fields = type.getFields();
			
			for (int i = 0; i < fields.length; ++i) {
				String name = fields[i].getElementName();
				// System.out.println("Field " + name + ":");
				int flags = fields[i].getFlags();
				if (Flags.isStatic(flags)) { 
					if (Flags.isFinal(flags)) {
						String fieldTypeSig = fields[i].getTypeSignature();
						// must be Powerless

						if (Utility.signatureIsClass(fieldTypeSig)) {
							IType fieldType = Utility.lookupType(fieldTypeSig, type);
							state.addFlagDependency(type.getCompilationUnit(), fieldType);
						}
						
						if (taming.implementsOverlay(fieldTypeSig, taming.POWERLESS, type)) {
							// OKAY
						} else {
							problems.add(new Problem("Non-powerless static field " 
													 + name + ".", 
													 fields[i].getNameRange()));					
						}
					} else {
						problems.add(new Problem("Non-final static field " + name + ".",
									 fields[i].getNameRange()));
					}
				} 
			}
			
			if (type.isInterface()) {
				// Nothing more to check. All fields are static final and have
				// already been verified to be immutable.
				
				return;
			}

			//
			// Otherwise, it is a "real" class.
			//
			
			String superclass = type.getSuperclassTypeSignature();
			
			if (superclass != null) {
				System.out.println("Superclass " + superclass);

				// See what honoraries superclass has; make sure that all are
				// implemented by this class.
				
				IType supertype = Utility.lookupType(superclass, type);
                Set<IType> unimp = taming.unimplementedHonoraries(sth);
                for (IType i : unimp) {
               		problems.add(
						new Problem("Honorary interface " + i.getElementName() + 
							"not inherited from " + supertype.getElementName(), 
							type.getNameRange()));
				}
			}
			
			if (isPowerless	&& !taming.isDeemed(type, taming.POWERLESS)) {
				if (sth.contains(taming.TOKEN)) {
					problems.add(new Problem("Powerless type " + type.getElementName() + 
							     			 " can't extend Token.", 
							     			 type.getNameRange()));
				}
				
				verifyFieldsAre(type, taming.POWERLESS, problems);
				
			} else if (isImmutable && !taming.isDeemed(type, taming.IMMUTABLE)) {
				
				verifyFieldsAre(type, taming.IMMUTABLE, problems);
			}
		} catch (JavaModelException jme) {
			jme.printStackTrace();
		}
	}

	/**
	 * Verify that all fields (declared, inherited, and lexically visible) of a type are 
	 * final and implement the specified marker interface in the overlay type system.
	 * 
	 * @param type
	 *            the type whose fields to verify
	 * @param mi
	 *            the marker interface, i.e. Immutable or Powerless
	 * @throws JavaModelException
	 */
	void verifyFieldsAre(IType type, IType mi, List<Problem> problems) 
			throws JavaModelException {
		
        // deep dependency on superclass, if one exists
        String superclassSig = type.getSuperclassTypeSignature();
        if (superclassSig != null) {
            IType superclass = Utility.lookupType(superclassSig, type);
            state.addDeepDependency(type.getCompilationUnit(), superclass);
        }
		
		HashSet<IType> needCheck = findClasses(type, mi);
		for (IType i : needCheck) {
			verifyFieldsAre(i, mi, type, problems);
		}
	}
	
	/**
	 * Find the set of classes all of whose fields must satisfy a given marker interface
	 * Classes already declared to implement the marker interface are not returned.
	 * 
	 * @param type 
     *              the type at which to start
	 * @param mi 
     *              the marker interface whose implementors to skip
	 * @return the set of classes found
	 * @throws JavaModelException
	 */
	HashSet<IType> findClasses(IType type, IType mi) 
			throws JavaModelException {
		HashSet<IType> found = new HashSet<IType>();
		found.add(type);
		LinkedList<IType> left = new LinkedList<IType>();
		left.add(type);
		
		while (!left.isEmpty()) {
			IType next = left.removeFirst();
			// non-static member classes get access to variables in their containing class
			if (next.isMember() && !Flags.isStatic(next.getFlags())) {
				IType enclosingType = next.getDeclaringType();
				if (taming.implementsOverlay(enclosingType, mi)) {
					System.out.println(enclosingType.getElementName() 
					                   + " is " + mi.getElementName());
					// already verified
				} else {
					if (found.add(enclosingType)) {
						left.add(enclosingType);  // only add if we haven't traversed it yet
					}
				}
			}
			
			String superclass = next.getSuperclassTypeSignature();
			if (superclass != null) {
				IType supertype = Utility.lookupType(superclass, next);
				if (taming.implementsOverlay(supertype, mi)) {
                    System.out.println(supertype.getElementName() 
                                       + " is " + mi.getElementName());
                    // already verified
				} else {
					if (found.add(supertype)) {
						left.add(supertype);  // only add if we haven't traversed it yet
					}
				}
			}
		}
		
		return found;
	}
	
	/**
	 * Verify that a class's explicit instance fields are final and honorarily implement the
	 * specified marker interface
	 * 
	 * @param type 
     *              the type whose instance fields to check
	 * @param mi 
     *              the marker interface to check implementation of
	 * @param candidate 
     *              the type for which to report Problems
	 * @param problems 
     *              the list to which to append problems
	 * @throws JavaModelException
	 */
	void verifyFieldsAre(IType type, IType mi, IType candidate,
								List<Problem> problems) throws JavaModelException {
        String miName = mi.getElementName();
        String candName = candidate.getElementName();
        String typeName = type.getElementName();
        //
		// Check declared instance fields implement mi
		//
		IField[] fields = type.getFields();
		
		for (int i = 0; i < fields.length; ++i) {
			String fieldName = fields[i].getElementName();
			// System.out.println("Field " + name + ":");
			int flags = fields[i].getFlags();
			if (!Flags.isStatic(flags) && !Flags.isEnum(flags)) {
				if (Flags.isFinal(flags)) {
                    // Field must implement mi
                    String fieldTypeSig = fields[i].getTypeSignature();

                    // add dependency on types of fields
                    if (Utility.signatureIsClass(fieldTypeSig)) {
                        IType fieldType = Utility.lookupType(fieldTypeSig, type);
                        state.addFlagDependency(candidate.getCompilationUnit(), fieldType);
                    }					
                    
                    if (taming.implementsOverlay(fieldTypeSig, mi, type)) {
						// OKAY
					} else if (type.equals(candidate)) {
						problems.add(
						    new Problem(String.format("Non-%s field %s in %s class %s",
                                                      miName, fieldName, miName, candName),
                                        fields[i].getNameRange()));
					} else { // type != candidate
						problems.add(
						    new Problem(String.format("Non-%s field %s from %s in %s class %s",
                                                      miName, fieldName, typeName, miName, candName),
								        candidate.getNameRange()));
					}
				} else if (type.equals(candidate)) {
					problems.add(
						    new Problem(String.format("Non-final field %s in %s class %s", 
                                                      fieldName, miName, candName), 
						    			fields[i].getNameRange()));
				} else { // type != candidate
					problems.add(
					    new Problem(String.format("Non-final field %s from %s in %s class %s",
                                                  fieldName, typeName, miName, candName),
                                    candidate.getNameRange()));
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
	 * AST visitor class.
	 * 
	 * Performs checks not possible using the IType interface, i.e. those that require
	 * examination of method source code.
	 */
	class VerifierASTVisitor extends ASTVisitor
	{
	    final ICompilationUnit icu;
		final List<Problem> problems; 
        final Queue<BodyDeclaration> codeContext;
        
        /**
         * Create a visitor for a specified compilation unit that appends Joe-E
         * verification errors to a specified list of problems.
         * 
         * @param icu
         *              the compilation unit to analyze
         * @param problems
         *              a list of problems to append Joe-E verification errors
         *              to
         */
        VerifierASTVisitor(ICompilationUnit icu, List<Problem> problems)
		{
			// System.out.println("VAV init");
            this.icu = icu;
			this.problems = problems;
            this.codeContext = new LinkedList<BodyDeclaration>();
		}
        
        /**
         * Check a field access.  Ensure that the field being accessed is
         * either present in source code, or permitted by the taming database.
         * 
         * @param fa
         *              the field access to check
         * @return
         *              true to visit children of this node
         */
        public boolean visit(FieldAccess fa) {
            IVariableBinding ivb = fa.resolveFieldBinding();
            checkFieldBinding(ivb, fa);
            return true;
        }
        
        /**
         * Helper method to check a field binding, resolved from a field access
         * expression or a qualified name.  Ensure that the field is either 
         * present in source code or permitted by the taming database.
         * 
         * @param fieldBinding
         *              the field binding to check
         * @param source
         *              the AST node in the program source that resolves to
         *              this binding
         */
        private void checkFieldBinding(IVariableBinding fieldBinding, ASTNode source) {
            ITypeBinding classBinding = fieldBinding.getDeclaringClass();
            // "The field length of an array type has no declaring class."
            // It appears to return null.  It needs to be special-cased here; 
            // we allow it.
            if (classBinding != null && !classBinding.isFromSource()) {
                IType classType = (IType) classBinding.getJavaElement();
                
                // check in taming database  
                if (!taming.isTamed(classType)) {
                    problems.add(
                        new Problem("Field from untamed class "
                                    + classType.getElementName() + " accessed.",
                                    source.getStartPosition(), source.getLength()));
                    return;
                }
                
                if (!taming.isAllowed((IField) fieldBinding.getJavaElement())) {
                    problems.add(
                        new Problem("Disabled field " + fieldBinding.getName() + " from class "
                                    + classType.getElementName() + " accessed.",
                                    source.getStartPosition(), source.getLength()));
                }
            }
        }
        
        /** 
         * Check a qualified name.  If the name corresponds to a field, ensure
         * that the field being accessed is either present in source code, or
         * permitted by the taming database.
         * 
         * @param qn
         *              the qualified name to check
         * @return
         *              true to visit children of this node
         */
        public boolean visit(QualifiedName qn) {
            IBinding ib = qn.resolveBinding();
            if (ib instanceof IVariableBinding)
            {
                IVariableBinding ivb = (IVariableBinding) ib;
                assert(ivb.isField());
                checkFieldBinding(ivb, qn);
            } else {
                assert (ib instanceof ITypeBinding || ib instanceof IPackageBinding);
            }
            return true;
        }
        
        /**
         * Check a class instance creation.  If we are in a constructor context
         * (see inConstructorContext()), then if the object being constructed
         * is of a (transitively) inner class of the current class, flag an
         * error: it may be able to see this class' partially initialized state.
         * 
         * @param cic
         *              the ClassInstanceCreation to check
         * @return
         *              true to visit children of this node
         */        
        public boolean visit(ClassInstanceCreation cic) {
            IMethodBinding imb = cic.resolveConstructorBinding();
            
            IType currentClass = getConstructorContext();
            ITypeBinding classBinding = imb.getDeclaringClass();
            IType classType = (IType) classBinding.getJavaElement();
            
            if (currentClass != null) {
                IJavaElement enclosingType = classType;
                try {
                    while (enclosingType instanceof IType && !enclosingType.equals(currentClass) 
                           && !Flags.isStatic(((IType) enclosingType).getFlags())) {                       
                        enclosingType = enclosingType.getParent();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (enclosingType.equals(currentClass)) {
                    problems.add(new Problem("Construction of non-static member class " + 
                                             imb.getName() + " during instance initialization.",
                                             cic.getStartPosition(), cic.getLength()));
                }
                   
            }
            return true;
        }
        
        /**
         * Check a method invocation.  Ensure that the method being called is
         * either present in source code, or permitted by the taming database.
         * 
         * @param mi
         *              the method invocation to check
         * @return
         *              true to visit children of this node
         */
        public boolean visit(MethodInvocation mi) {
            IMethodBinding imb = mi.resolveMethodBinding();
            ITypeBinding classBinding = imb.getDeclaringClass();
            IType classType = (IType) classBinding.getJavaElement();
            if (!classBinding.isFromSource()) {
                // check in taming database  
                if (!taming.isTamed(classType)) {
                    problems.add(
                        new Problem("Method from untamed class "
                                    + classType.getElementName() + " called.",
                                    mi.getStartPosition(), mi.getLength()));
                    return true;
                }
                
                if (!taming.isAllowed((IMethod) imb.getJavaElement())) {
                    problems.add(
                        new Problem("Disabled method " + imb.getName() +
                                    " from class " + classType.getElementName()
                                    + " called.", 
                                    mi.getStartPosition(), mi.getLength()));
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
                    problems.add(
                        new Problem("Called local non-static method "
                                    + imb.getName() 
                                    + " during instance initialization.",
                                    mi.getStartPosition(), mi.getLength()));
                }
            }
            return true;
        }
        
        /**
         * If in a constructor context, i.e. at a program point in which the
         * current object may be incompletelly initialized, get the current
         * class.
         * 
         * @return
         *              null if not in a constructor context, the type of the
         *              current class if in a constructor context
         */
        IType getConstructorContext() {
            if (inConstructorContext()) {
                BodyDeclaration bd = codeContext.peek();
                return (IType) 
                    ((AbstractTypeDeclaration) bd.getParent()).resolveBinding().getJavaElement();
            } else {
                return null;
            }
        }
        
        /**
         * Test whether we are in a constructor context, i.e. at a program
         * point at which the current object may be incompletelly initialized.
         * This occurs during the traversal of instance initializers,
         * constructors, and field declarations.  This makes use of the
         * codeContext information updated when such nodes are entered and
         * exited during traversal.
         *
         * @return
         *          true if the traversal is currently within a constructor
         *          context.
         */
        boolean inConstructorContext() {
            BodyDeclaration bd = codeContext.peek();
            if (bd instanceof Initializer) {
               Initializer init = (Initializer) bd;
                return !Flags.isStatic(init.getModifiers());
            } else if (bd instanceof MethodDeclaration) {
                MethodDeclaration md = (MethodDeclaration) bd;
                return md.isConstructor();
            } else if (bd instanceof FieldDeclaration) {
                FieldDeclaration fd = (FieldDeclaration) bd;
                return !Flags.isStatic(fd.getFlags());
            } else {
                return false;
                // FIXME: should sometimes be true? perhaps for enums?
            }
        }
        
        /**
         * Record in the codeContext when we visit an initializer.
         * 
         * @param init
         *              the initializer being traversed
         * @return
         *              true to visit children of this node
         */
        public boolean visit(Initializer init) {
            codeContext.add(init);
            return true;
        }
        
        /**
         * Record in the codeContext when we visit a field declaration.
         * 
         * @param fd
         *              the field declaration being traversed
         * @return
         *              true to visit children of this node
         */
        public boolean visit(FieldDeclaration fd) {
            //System.out.println("visit(FieldDeclaration fd) of <" + fd + ">");
            codeContext.add(fd);
            return true;
        }
        
        /**
         * Check a method declaration.  Ensure that the method is not native.
         * Also, record the visiting of the method in the codeContext.
         * 
         * @param md
         *              the method declaration being traversed
         * @return
         *              true to visit children of this node
         */
		public boolean visit(MethodDeclaration md) {
            //System.out.println("visit(MethodDeclaration bd) of <" + md + ">");
            codeContext.add(md);
			String name = md.getName().toString();
			int modifiers = md.getModifiers();
			if (Modifier.isNative(modifiers))
			{
				problems.add(new Problem("Native method " + name,
							 			 md.getStartPosition(),
							 			 md.getLength()));
			}
			return true;
		}
		
        /*
         * Body declarations not specifically handled above:
         * AbstractTypeDeclaration, AnnotationTypeMemberDeclaration, EnumConstantDeclaration
         */
        public boolean visit(AbstractTypeDeclaration atd) {
            // System.out.println("visit(BodyDeclaration bd) of <" + bd + ">");
            codeContext.add(atd);
            return true;
        }
        
        public boolean visit(AnnotationTypeMemberDeclaration atmd) {
            // System.out.println("visit(BodyDeclaration bd) of <" + bd + ">");
            codeContext.add(atmd);
            return true;
        }
        
        public boolean visit(EnumConstantDeclaration ecd) {
            // System.out.println("visit(BodyDeclaration bd) of <" + bd + ">");
            codeContext.add(ecd);
            return true;
        }
        
        /*
         * endVisit
         * 
         * If any of these assertions fail, see if .equals() is required here
         * instead of ==.  I doubt this will be necessary, as I don't see why
         * we'd be given two different versions of the same object here.
         */
        public void endVisit(Initializer init) {
            assert(codeContext.peek() == init);
            codeContext.remove();
        }
        
        public void endVisit(FieldDeclaration fd) {
            assert(codeContext.peek() == fd);
            codeContext.remove();
        }
        
        public void endVisit(MethodDeclaration md) {
            assert(codeContext.peek() == md);
            codeContext.remove();
        }
        
        public void endVisit(AbstractTypeDeclaration atd) {
            // System.out.println("End visit of <" + bd + ">");
            assert(codeContext.peek() == atd);
            codeContext.remove();
        }
        
        public void endVisit(AnnotationTypeMemberDeclaration atmd) {
            // System.out.println("End visit of <" + bd + ">");
            assert(codeContext.peek() == atmd);
            codeContext.remove();
        }
              
        public void endVisit(EnumConstantDeclaration ecd) {
            // System.out.println("End visit of <" + bd + ">");
            assert(codeContext.peek() == ecd);
            codeContext.remove();
        }


        /**
         * Check usage of the keyword 'this'.  If we are in a constructor
         * context, bare use of the 'this' keyword is prohibited.
         * 
         * @param te
         *              the this expression to check
         * @return
         *              true to visit children of this node
         */
        public boolean visit(ThisExpression te) {
            if (inConstructorContext()) {
                problems.add(new Problem("Possible escapement of 'this' not allowed in "+
                                         "instance initialization.",
                                         te.getStartPosition(), te.getLength()));
            }
            return true;
        }
                
        /**
         * Check an infix expression.  If the expression is an object identity
         * comparison (== or !=), then ensure that at least one of the operands
         * is Equatable.
         * 
         * @param te
         *              the infix expression to check
         * @return
         *              true to visit children of this node
         */
        public boolean visit(InfixExpression ie) {
			if (ie.getOperator() == InfixExpression.Operator.EQUALS ||
				ie.getOperator() == InfixExpression.Operator.NOT_EQUALS) {
				ITypeBinding leftTB = ie.getLeftOperand().resolveTypeBinding();
				if (leftTB == null) {
					System.out.println("ERROR: Left type binding unresolvable: "
                                       + ie.getLeftOperand().toString());
					return true;
				}
				
				// cases where we don't need to look at right hand type
				if (leftTB.isPrimitive() || leftTB.isNullType()) {
					return true;
				}
                				
				ITypeBinding rightTB = ie.getRightOperand().resolveTypeBinding();
				if (rightTB == null) {
					System.out.println("ERROR: Right type binding unresolvable: "
                                       + ie.getRightOperand().toString());
					return true;
				}
				
				// (otherwise redundant isPrimitive check required for auto-unboxing)
				if (rightTB.isNullType() || rightTB.isPrimitive()) {
				    return true;
                }
                
                if (leftTB.isArray() || rightTB.isArray()) {
                    problems.add(new Problem("== used to compare arrays",
                                                 ie.getStartPosition(), ie.getLength()));
                    return true;
                } else if (leftTB.isTypeVariable() || rightTB.isTypeVariable()) {
                    problems.add(new Problem("== used to compare objects of generic type",
                                             ie.getStartPosition(), ie.getLength()));
                    return true;
                }
                
                // At this point, we hope to have dealt with any funny stuff.
                assert((leftTB.isClass() || leftTB.isInterface()) && (rightTB.isClass() || rightTB.isInterface()));
                
                try {
					// Evaluate left type binding
                    IType leftType = (IType) leftTB.getJavaElement();
                    if (leftType == null) {
                        System.out.println("ERROR: Couldn't find type \"" + leftTB.getQualifiedName()
                                           + "\" for type binding " + leftTB);
                        return true;
                    }
                    ITypeHierarchy leftSTH = leftType.newSupertypeHierarchy(null);
                    
                    // OK if left side is Equatable
                    if (leftSTH.contains(taming.ENUM) || leftSTH.contains(taming.TOKEN)) {
                        // need to recheck if left side becomes un-equatable
                        state.addFlagDependency(icu, leftType);
                        return true;
                    }
                    
                    // Otherwise, evaluate right type binding
					IType rightType = (IType) rightTB.getJavaElement();
					if (rightType == null) {
						System.out.println("ERROR: Couldn't find type \"" + rightTB.getQualifiedName()
                                           + "\" for type binding " + rightTB); 
						return true;
					}

					ITypeHierarchy rightSTH = rightType.newSupertypeHierarchy(null);
                    // OK if right side is Equatable
                    if (leftSTH.contains(taming.ENUM) || leftSTH.contains(taming.TOKEN)) {
                        // need to recheck if right side becomes un-equatable
                        state.addFlagDependency(icu, rightType);
                        return true;
                    }
                    
                    // Otherwise, we have a problem
                    problems.add(new Problem("Pointer equality test on non-Equatable types",
								             ie.getStartPosition(), ie.getLength()));
                    
                    // need to recheck if either type becomes equatable
                    state.addFlagDependency(icu, rightType);
                    state.addFlagDependency(icu, leftType);              
				}
				catch (JavaModelException jme) {
					jme.printStackTrace();
				}
			}
			return true;
		}
        
        /*
         *
         * Alternate method: use the ASTVisitor for more stuff.  
         * Not necessary. (?)
         *
         
        public boolean visit(EnumDeclaration ed)
        
        
        public boolean visit(TypeDeclaration td) {
            if (td.isInterface()) {
                // Nothing more to check. All fields are static final.  Whether inherited or not,
                // they will be verified immutable.
            } else {
                //
                // Otherwise, it is a "real" class.
                //
                try {
                    // get supertype hierarchy, we'll need it.
                    ITypeHierarchy sth = type.newSupertypeHierarchy(null);
                String superclass = type.getSuperclassTypeSignature();
                
                if (superclass != null) {
                    System.out.println("Superclass " + superclass);

                    // See what honoraries superclass has, make sure that all are
                    // implemented by this class.
                    
                    IType supertype = Utility.lookupType(superclass, type);
                    String[] sh = MarkerInterface.getHonoraries(supertype);
                    for (int i = 0; i < sh.length; ++i) {
                        if (!MarkerInterface.is(type, sh[i])) {
                            problems.add(
                                new Problem("Honorary interface " + sh[i] + 
                                            "not inherited from " + supertype.getElementName(), 
                                            type.getNameRange()));
                        }
                    }
                }
                
                if (MarkerInterface.is(type, "Powerless") 
                    && !MarkerInterface.isDeemed(type, "Powerless")) {
                    
                    IType tokenType = type.getJavaProject().findType("org.joe_e.Token");
                    if (sth.contains(tokenType)) {
                        problems.add(new Problem("Powerless type " + type.getElementName() + 
                                                 " can't extend Token.", 
                                                 type.getNameRange()));
                    }
                    
                    verifyFieldsAre(type, "Powerless", problems);
                    
                } else if (MarkerInterface.is(type, "DeepFrozen")
                           && !MarkerInterface.isDeemed(type, "DeepFrozen")) {
                    
                    verifyFieldsAre(type, "DeepFrozen", problems);
                }
            } catch (JavaModelException jme) {
                jme.printStackTrace();
            }
            }
            return true;
        }
        
        public boolean visit(FieldDeclaration fd) {
            int flags = fd.getModifiers();
            List frags = fd.fragments();  // element type:
                                            // VariableDeclarationFragment
            Type baseType = fd.getType();rameter
            if (Flags.isStatic(flags)) {
                if (Flags.isFinal(flags)) {
                    if (MarkerInterface.is(baseType, "Powerless")) {
                        for (Object o: frags) {
                            VariableDeclarationFragment vdf = (VariableDeclarationFragment) o;
                            if (vdf.getExtraDimensions() > 0) {
                                // sneaky sneaky... 
                                String name = vdf.getName().getFullyQualifiedName();
                                problems.add(new Problem("Non-powerless static field " 
                                        + name + ".", vdf.getStartPosition(), vdf.getLength()));
                            }
                        }
                    }
                    else {
                        String name = "";
                        for (Object o: frags) {
                            name += (VariableDeclarationFragment) o.getName().getFullyQualifiedName() + " ";
                        }
                        problems.add(new Problem ("Non-powerless static field(s) "
                                + name + ".", fd.getStartPosition(), fd.getLength()));
                    }
                } else {
                    String name = "";
                    for (Object o: frags) {
                        name += (VariableDeclarationFragment) o.getName().getFullyQualifiedName() + " ";
                    }
                    problems.add(new Problem ("Non-final static field(s) "
                            + name + ".", fd.getStartPosition(), fd.getLength()));          
                }
            }
        */  
    }
}