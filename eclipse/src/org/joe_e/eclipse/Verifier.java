package org.joe_e.eclipse;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.*;

import org.eclipse.jdt.core.dom.*;

import java.util.List;
import java.util.LinkedList;

public class Verifier {
	/**
	 * Run the Joe-E verifier on an IFile
	 * 
	 * @param icu
	 *            ICompilationUnit on which to run the verifier
	 * @return a List of Problems (Joe-E verification errors) encountered
	 */
	static List<Problem> checkJavaFile(IFile resource) {
		try {
			System.out.println("Joe-E Verifier examining " + resource.getName());
			IJavaElement element = JavaCore.create(resource);
			
			return checkICU((ICompilationUnit) element);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			// TODO: fail to verify here - an error has occured!
			return new LinkedList<Problem>();
		}	
	}		

	/**
	 * Run the Joe-E verifier on an ICompilationUnit
	 * 
	 * @param icu
	 *            ICompilationUnit on which to run the verifier
	 * @return a List of Problems (Joe-E verification errors) encounteredas
	 *         cleanly
	 */
	static List<Problem> checkICU(ICompilationUnit icu)
	{
		LinkedList<Problem> problems = new LinkedList<Problem>();
		
		try {
			// Check for package membership
			IPackageDeclaration[] pkg = icu.getPackageDeclarations();
			if (pkg.length > 1) {
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
			
				checkIType(type, problems);
			}
			
			// checks that require ugly DOM hacking directly
		
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setSource(icu);
			parser.setResolveBindings(true);
			ASTNode parse = parser.createAST(null);
			VerifierASTVisitor vav = new VerifierASTVisitor(icu.getJavaProject(), problems);
			parse.accept(vav);
		}	
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		System.out.println(problems);
		return problems;
	}


	String ppArray2(Object[][] array)
	{
		String out = "[";
		if (array.length > 0) {
			out += ppArray(array[0]);
		}
		for (int i = 1; i < array.length; ++i) {
			out += ", " + ppArray(array[i]);
		}
		return out + "]";
	}
	
	
	String ppArray(Object[] array) 
	{
		String out = "[";
		if (array.length > 0) {
			out += array[0];
		}
		for (int i = 1; i < array.length; ++i) {
			out += ", " + array[i];
		}
		return out + "]";
	}
	
	
	/*
	 * static void printParseTree(ASTNode t) { printParseTree(t, ""); } static
	 * void printParseTree(ASTNode t, String indent) { System.out.print(indent);
	 * System.out.println(t.getClass().getName()); if (t instanceof t.get }
	 */
	
	static void checkIType(IType type, List<Problem> problems)
	{
		try {
			if (type.isAnnotation() || type.isEnum()) {
				// I think these are fine as is. Should test.
				// Annotations are a special case of interfaces with (I believe)
				// no additional abilities.
				// Enumerations are final classes without run-time constructors
				// in which the enumeration values are implicitly static,
				// implicitly final fields.
			}
			
			// Restrictions on fields.
			IField[] fields = type.getFields();
			
			for (int i = 0; i < fields.length; ++i) {
				String name = fields[i].getElementName();
				System.out.println("Field " + name + ":");
				int flags = fields[i].getFlags();
				if (Flags.isStatic(flags)) { 
					if (Flags.isFinal(flags)) {
						String fieldType = fields[i].getTypeSignature();
						
						// must be Incapable
						
						if (MarkerInterface.is(fieldType, "Incapable", type)) {
							// OKAY
						} else {
							problems.add(new Problem("Non-incapable static field " 
													 + name + ".", 
													 fields[i].getNameRange()));					}
					} else {
						problems.add(new Problem("Non-final static field " + name + ".",
									 fields[i].getNameRange()));
					}
				} 
			}
			
			if (type.isInterface()) {
				// Nothing more to check. All fields are static final and have
				// already
				// been verified to be immutable.
				
				return;
			}

			//
			// Otherwise, it is a "real" class.
			//
			
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
			
			if (MarkerInterface.is(type, "Incapable") 
				&& !MarkerInterface.isDeemed(type, "Incapable")) {
				
				IType tokenType = type.getJavaProject().findType("org.joe_e.Token");
				if (sth.contains(tokenType)) {
					problems.add(new Problem("Incapable type " + type.getElementName() + 
							     			 " can't extend Token.", 
							     			 type.getNameRange()));
				}
				
				verifyFieldsAre(type, "Incapable", problems);
				
			} else if (MarkerInterface.is(type, "DeepFrozen")
					   && !MarkerInterface.isDeemed(type, "DeepFrozen")) {
				
				verifyFieldsAre(type, "DeepFrozen", problems);
			}
		} catch (JavaModelException jme) {
			jme.printStackTrace();
		}
	}

	/**
	 * Verify that all fields (declared and inherited) of a type are final and
	 * implement the specified marker interface in the overlay type system.
	 * 
	 * @param type
	 *            the type whose fields to verify
	 * @param mi
	 *            the marker interface, i.e. DeepFrozen or Incapable
	 * @throws JavaModelException
	 */
	static void verifyFieldsAre(IType type, String mi, List<Problem> problems) 
		throws JavaModelException {
		verifyFieldsAre(type, mi, type, problems);
	}
	
	static void verifyFieldsAre(IType type, String mi, IType candidate,
								List<Problem> problems) throws JavaModelException {
		//
		// Check declared instance fields implement mi
		//
		IField[] fields = type.getFields();
		
		for (int i = 0; i < fields.length; ++i) {
			String name = fields[i].getElementName();
			// System.out.println("Field " + name + ":");
			int flags = fields[i].getFlags();
			if (!Flags.isStatic(flags) && !Flags.isEnum(flags)) {
				if (Flags.isFinal(flags)) {
					String fieldType = fields[i].getTypeSignature();
					// must implement mi
					if (MarkerInterface.is(fieldType, mi, type)) {
						// OKAY
					} else if (type == candidate) {
						problems.add(
						    new Problem("Non-" + mi + " field " + name +  " in " + mi + " class " + 
							    		candidate.getElementName(), fields[i].getNameRange()));
					} else { // type != candidate
						problems.add(
						    new Problem("Non-" + mi + " field " + name + " from " +
						    			type.getElementName() +	" in " + mi + " class " + 
						    			candidate.getElementName(),
								        candidate.getNameRange()));
					}
				} else if (type == candidate) {
					problems.add(
						    new Problem("Non-final field " + name + " in " + mi + " class " + 
						    			candidate.getElementName(), fields[i].getNameRange()));
				} else { // type != candidate
					problems.add(
					    new Problem("Non-final field " + name + " from " + type.getElementName() 
					    			+ " in " + mi + " class " + candidate.getElementName(),
								    candidate.getNameRange()));
				}
			}
			
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
		}
	}

	// 
	// 
	//
	static class VerifierASTVisitor extends ASTVisitor
	{
		final IJavaProject project;
		final List<Problem> problems; 
		
		VerifierASTVisitor(IJavaProject project, List<Problem> problems)
		{
			// System.out.println("VAV init");
			this.project = project;
			this.problems = problems;
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
				
				if (MarkerInterface.is(type, "Incapable") 
					&& !MarkerInterface.isDeemed(type, "Incapable")) {
					
					IType tokenType = type.getJavaProject().findType("org.joe_e.Token");
					if (sth.contains(tokenType)) {
						problems.add(new Problem("Incapable type " + type.getElementName() + 
								     			 " can't extend Token.", 
								     			 type.getNameRange()));
					}
					
					verifyFieldsAre(type, "Incapable", problems);
					
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
			Type baseType = fd.getType();
			if (Flags.isStatic(flags)) {
				if (Flags.isFinal(flags)) {
					if (MarkerInterface.is(baseType, "Incapable")) {
						for (Object o: frags) {
							VariableDeclarationFragment vdf = (VariableDeclarationFragment) o;
							if (vdf.getExtraDimensions() > 0) {
								// sneaky sneaky... 
								String name = vdf.getName().getFullyQualifiedName();
								problems.add(new Problem("Non-incapable static field " 
										+ name + ".", vdf.getStartPosition(), vdf.getLength()));
							}
						}
					}
					else {
						String name = "";
						for (Object o: frags) {
							name += (VariableDeclarationFragment) o.getName().getFullyQualifiedName() + " ";
						}
						problems.add(new Problem ("Non-incapable static field(s) "
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
			
					
		public boolean visit(MethodDeclaration md) {
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
		
		public boolean visit(InfixExpression ie) {
			if (ie.getOperator() == InfixExpression.Operator.EQUALS ||
				ie.getOperator() == InfixExpression.Operator.NOT_EQUALS) {
				ITypeBinding leftTB = ie.getLeftOperand().resolveTypeBinding();
				// cases where we don't need to look at right hand type
				if (leftTB.isPrimitive() || leftTB.isNullType()) {
					return true;
				} else if (leftTB.isArray()) {
					problems.add(new Problem("== used to compare two arrays",
							 				 ie.getStartPosition(), ie.getLength()));
					return true;
				} else if (leftTB.isTypeVariable()) {
					problems.add(new Problem("== used to compare variable typed objects",
											 ie.getStartPosition(), ie.getLength()));
					return true;
				}
				
				ITypeBinding rightTB = ie.getRightOperand().resolveTypeBinding();
				
				// otherwise redundant isPrimitive check required for
				// auto-unboxing
				if (!rightTB.isNullType() && !rightTB.isPrimitive()) {
					try {
						// For now, generic types ignore their type parameters.
						// TODO: Possibly, deal with generics in more detail.
						String leftTypeName = Utility.stripGenerics(leftTB.getQualifiedName());
						String rightTypeName = Utility.stripGenerics(rightTB.getQualifiedName());
						// IJavaProject.findType is confused by type parameters.
						IType leftType = project.findType(leftTypeName);
						IType rightType = project.findType(rightTypeName);
						ITypeHierarchy leftSTH = leftType.newSupertypeHierarchy(null);
						ITypeHierarchy rightSTH = rightType.newSupertypeHierarchy(null);
						IType tokenType = project.findType("org.joe_e.Token");
						IType enumType = project.findType("java.lang.Enum");
						
						// Allow == and != on enumeration values.
						if (enumType != null && (leftSTH.contains(enumType) 
									             || rightSTH.contains(enumType))) {
							return true;
						}
						
						// Allow == and != on Tokens.
						if (tokenType != null && (leftSTH.contains(tokenType)
												  || rightSTH.contains(tokenType))) {
							return true;
						}
						
						problems.add(new Problem("== used on non-Token types",
								     ie.getStartPosition(), ie.getLength()));
					}
					catch (JavaModelException jme) {
						jme.printStackTrace();
					}
				}
			}
			return true;
		}
	}
}
