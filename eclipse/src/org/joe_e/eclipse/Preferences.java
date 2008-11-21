// Copyright 2005-06 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e.eclipse;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.joe_e.eclipse.Plugin;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class Preferences
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

    static final String P_TAMING_PATH = "tamingPathPreference";
    static final String P_ENABLE_TAMING = "enableTamingPreference";
    static final String P_ENABLE_DEBUG = "enableDebugPreference";
    
	public Preferences() {
		super(GRID);
		setPreferenceStore(Plugin.getDefault().getPreferenceStore());
		setDescription("Preferences for the Joe-E plugin.");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		addField(new DirectoryFieldEditor(P_TAMING_PATH, 
				     "&Taming database:", getFieldEditorParent()));
		addField(new BooleanFieldEditor(P_ENABLE_TAMING,
				     "&Enable Taming checks", getFieldEditorParent()));
        addField(new BooleanFieldEditor(P_ENABLE_DEBUG,
                     "Enable &debug output",  getFieldEditorParent()));

        /*
		addField(new RadioGroupFieldEditor(
				PreferenceConstants.P_CHOICE,
			"An example of a multiple-choice preference",
			1,
			new String[][] { { "&Choice 1", "choice1" }, {
				"C&hoice 2", "choice2" }
		}, getFieldEditorParent()));
		addField(
			new StringFieldEditor(PreferenceConstants.P_STRING, "A &text preference:", getFieldEditorParent()));
        */
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
    static boolean isTamingEnabled() {
        IPreferenceStore store = Plugin.getDefault().getPreferenceStore();
        return store.getBoolean(P_ENABLE_TAMING);
    }
    
    static String getTamingPath() {
        IPreferenceStore store = Plugin.getDefault().getPreferenceStore();
        return store.getString(P_TAMING_PATH);
    }
    
    static boolean isDebugEnabled() {
        IPreferenceStore store = Plugin.getDefault().getPreferenceStore();
        return store.getBoolean(P_ENABLE_DEBUG);
    }
}