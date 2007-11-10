// Copyright 2005-07 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e.eclipse;

import java.util.Iterator;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class TogglePackageAction implements IObjectActionDelegate {
    static final QualifiedName PROPERTY = new QualifiedName("Joe_E", "package-enabled");
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
                    if (action.isChecked()) {
                        setJoeE(pkg);
                    } else {
                        removeJoeE(pkg);
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
        // System.out.println("got a selection");
        // Set the checkmark to checked only if all projects in selection have
        // the verifier enabled.
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
                // project with verifier disabled
                if (pkg != null && !isJoeE(pkg)) {
                    action.setChecked(false);
                    return;
                }
            }
        }
        
        // no projects with verifier disabled found
        action.setChecked(true);
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
     * Return whether the specified project has the Joe-E nature, i.e.
     * whether the Joe-E verifier is enabled.
     */
    private boolean isJoeE(IPackageFragment pkg) {
        try {
            String enabled = pkg.getCorrespondingResource()
                                 .getPersistentProperty(PROPERTY);
            return enabled != null;
        } catch (CoreException ce) {
            System.err.println("Unhandled CoreException! Shouldn't happen.");
            ce.printStackTrace(System.err);
            return false;
        }
    } 
    
    /*
     * Set the Joe-E nature on the specified project, i.e. enable the Joe-E
     * verifier for the project.  Does nothing if the verifier is already
     * enabled for the project.  Triggers a full build of the Joe-E Builder
     * (i.e. runs the verifier on all files) if it wasn't already enabled.
     */
    private void setJoeE(IPackageFragment pkg) {
        try {
            pkg.getCorrespondingResource().setPersistentProperty(PROPERTY, 
                                                                 "enabled");
        } catch (CoreException ce) {
            System.err.println("Unhandled CoreException! Shouldn't happen.");
            ce.printStackTrace(System.err);
        }
    }

    /*
     * Remove the Joe-E nature on the specified project, i.e. disable the Joe-E
     * verifier for the project.  Does nothing if the verifier is already
     * disabled for the project.  Triggers a clean() (removing all Joe-E
     * markers from verifier errors) if the verifier was previously enabled.
     */
    private void removeJoeE(IPackageFragment pkg) {
        try {
            pkg.getCorrespondingResource().setPersistentProperty(PROPERTY, 
                                                                 null);
        } catch (CoreException ce) {
            System.err.println("Unhandled CoreException! Shouldn't happen.");
            ce.printStackTrace(System.err);
        }
    }
}
