// Copyright 2005-07 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e.eclipse;

import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.resources.*;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;


public class TogglePackageAction implements IObjectActionDelegate {
    static final QualifiedName SKIP_PKG = new QualifiedName("Joe_E", "skip-package");
	private ISelection selection;

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {              
		if (selection instanceof IStructuredSelection) {
			for (Iterator it = ((IStructuredSelection) selection).iterator(); 
				    it.hasNext(); ) {
				Object element = it.next();
				IPackageFragment pkg = null;
				if (element instanceof IPackageFragment) {
					pkg = (IPackageFragment) element;
				} else if (element instanceof IAdaptable) {
					pkg = (IPackageFragment) ((IAdaptable) element)
                                         .getAdapter(IPackageFragment.class);
				}
				if (pkg != null) {
                    // isChecked after action (= unchecked before action)
                    if (action.isChecked()) {
                        setJoeE(pkg);
                    } else {
                        removeJoeE(pkg);
                    }
                    try {
                        for (ICompilationUnit icu : pkg.getCompilationUnits()) {
                            icu.getCorrespondingResource().touch(null);
                        }
                    } catch (CoreException ce) {
                       System.err.println("Unhandled CoreException! " +
                                          "Shouldn't happen.");
                       ce.printStackTrace(System.err);
                    }
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
        this.selection = selection;      
        boolean enabled = false;
        boolean checked = true;
        
        // System.out.println("got a selection");
        // Set the checkmark to checked only if all packages in selection
        // are Joe-E.
        if (selection instanceof IStructuredSelection) {
            for (Iterator it = ((IStructuredSelection) selection).iterator(); 
                    it.hasNext(); ) {
                Object element = it.next();
                IPackageFragment pkg = null;
                if (element instanceof IPackageFragment) {
                    pkg = (IPackageFragment) element;
                } else if (element instanceof IAdaptable) {
                    pkg = (IPackageFragment) ((IAdaptable) element)
                                         .getAdapter(IPackageFragment.class);
                }
                if (ToggleNatureAction.hasJoeENature(pkg.getJavaProject().getProject())) {
                    enabled = true;
                    // project with verifier disabled
                    if (pkg != null && !isJoeE(pkg)) {
                        checked = false;
                        break;
                    }
                }
            }
        }
        
        // action.setEnabled(enabled);
        action.setEnabled(false);
        action.setChecked(enabled && checked);
        return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction,
	 *      org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

    /*
     * Return whether the specified package is Joe-E, i.e. whether the
     * IsJoeE annotation is present.
     */
    static private boolean isJoeE(IPackageFragment pkg) {
        try {
            return isJoeE((IContainer) pkg.getCorrespondingResource());        
        } catch (CoreException ce) {
            System.err.println("Unhandled CoreException! Shouldn't happen.");
            ce.printStackTrace(System.err);
            return false;
        }
    } 
       
    /*
     * Returns true if the IContainer argument corresponds to a package
     * that the Joe-E verifier is not told to skip.
     */
    static boolean isJoeE(IContainer container) {
        try {
            if (!(container instanceof IFolder)) {
                return false;
            }
            
            IFolder folder = (IFolder) container;
            IFile packageInfo = folder.getFile("package-info.java");
            
            if (!packageInfo.exists() || Builder.hasJavaErrors(packageInfo)) {
                return false;
            }
            
            ICompilationUnit icu = (ICompilationUnit) JavaCore.create(packageInfo);
            
            ASTParser parser = ASTParser.newParser(AST.JLS3);
            parser.setSource(icu);
            parser.setResolveBindings(true);
            ASTNode parse = parser.createAST(null);
        
            IsJoeEASTVisitor ijav = new IsJoeEASTVisitor();
            parse.accept(ijav);
            
            return ijav.isJoeE;
        } catch (CoreException ce) {
            System.err.println("Unhandled CoreException! Shouldn't happen.");
            ce.printStackTrace(System.err);
            return false;
        }  
    }
    
    static class IsJoeEASTVisitor extends ASTVisitor {
        boolean isJoeE = false;
        
        public boolean visit (PackageDeclaration pd) {
            for (Object i :  pd.annotations()) {
                Annotation a = (Annotation) i;
                IType aType = 
                    (IType) a.resolveAnnotationBinding().getJavaElement();
                if (aType.getFullyQualifiedName().equals("org.joe_e.IsJoeE")) {
                   isJoeE = true;
                }
            }
            return false;
        }
    }
    
    /*
     * Clear the skip property on the specified project, i.e. enable the Joe-E
     * verifier for the project.
     * TODO: fix this (or remove)
     *   maybe only provide this feature if package-info doesn't already exist
     */
    private void setJoeE(IPackageFragment pkg) {
        try {
            pkg.getCorrespondingResource().setPersistentProperty(SKIP_PKG, 
                                                                 null);
        } catch (CoreException ce) {
            System.err.println("Unhandled CoreException! Shouldn't happen.");
            ce.printStackTrace(System.err);
        }
    }

    /*
     * Set the skip property for the specified project, i.e. disable the Joe-E
     * verifier for the project.
     * TODO: fix this (or remove)
     */
    private void removeJoeE(IPackageFragment pkg) {
        try {
            pkg.getCorrespondingResource().setPersistentProperty(SKIP_PKG, 
                                                                 "skip");
        } catch (CoreException ce) {
            System.err.println("Unhandled CoreException! Shouldn't happen.");
            ce.printStackTrace(System.err);
        }
    }
}
