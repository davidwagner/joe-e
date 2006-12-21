// Copyright 2005-06 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e.eclipse;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class ToggleNatureAction implements IObjectActionDelegate {

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
				IProject project = null;
				if (element instanceof IProject) {
					project = (IProject) element;
				} else if (element instanceof IAdaptable) {
					project = (IProject) ((IAdaptable) element)
                                         .getAdapter(IProject.class);
				}
				if (project != null) {
                    if (action.isChecked()) {
                        setJoeENature(project);
                    } else {
                        removeJoeENature(project);
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
                IProject project = null;
                if (element instanceof IProject) {
                    project = (IProject) element;
                } else if (element instanceof IAdaptable) {
                    project = (IProject) ((IAdaptable) element)
                                         .getAdapter(IProject.class);
                }
                // project with verifier disabled
                if (project != null && !hasJoeENature(project)) {
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
    private boolean hasJoeENature(IProject project) {
        try {
            IProjectDescription description = project.getDescription();
            String[] natures = description.getNatureIds();        
            for (String s : natures) {
                if (Nature.NATURE_ID.equals(s)) {
                    return true;
                }
            }
            return false;
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
    private void setJoeENature(IProject project) {
        if (!hasJoeENature(project)) {
            try {
                IProjectDescription description = project.getDescription();
                String[] natures = description.getNatureIds();
                
                System.out.println("Enabling the Joe-E Verifier for project "
                                   + project.getName() + ".");
                // Add the nature
                String[] newNatures = new String[natures.length + 1];
                System.arraycopy(natures, 0, newNatures, 0, natures.length);
                newNatures[natures.length] = Nature.NATURE_ID;
                description.setNatureIds(newNatures);
                project.setDescription(description, null);
                
                // If auto-build is on, the verifier is triggered 
                // automatically, with a proper progress monitor.  Nice!
                // So we don't need this.
                // project.build(Builder.FULL_BUILD, Builder.BUILDER_ID, 
                //              null, null);
            } catch (CoreException ce) {
                System.err.println("Unhandled CoreException! Shouldn't happen.");
                ce.printStackTrace(System.err);
            }
        }
    }

    /*
     * Remove the Joe-E nature on the specified project, i.e. disable the Joe-E
     * verifier for the project.  Does nothing if the verifier is already
     * disabled for the project.  Triggers a clean() (removing all Joe-E
     * markers from verifier errors) if the verifier was previously enabled.
     */
    private void removeJoeENature(IProject project) {
        try {
            IProjectDescription description = project.getDescription();
            String[] natures = description.getNatureIds();

            for (int i = 0; i < natures.length; ++i) {
                if (Nature.NATURE_ID.equals(natures[i])) {
                    System.out.println("Disabling Joe-E Verifier for project" +
                                       project.getName() + ".");
                    
                    // Clean up any markers generated by the verifier.
                    project.build(Builder.CLEAN_BUILD, Builder.BUILDER_ID, 
                                  null, null);                    
                    
                    // Remove the nature
                    String[] newNatures = new String[natures.length - 1];
                    System.arraycopy(natures, 0, newNatures, 0, i);
                    System.arraycopy(natures, i + 1, newNatures, i,
                            natures.length - i - 1);
                    description.setNatureIds(newNatures);
                    project.setDescription(description, null);
                    return;
                }
            } 
        } catch (CoreException ce) {
                System.err.println("Unhandled CoreException! Shouldn't happen.");
                ce.printStackTrace(System.err);
        }
    }
}
