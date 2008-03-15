package org.joe_e.eclipse;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.core.resources.ProjectScope;

public class ProjectProperties extends FieldEditorPreferencePage 
    implements IWorkbenchPropertyPage {
    IProject project;
    public void setElement(IAdaptable element) {
        project = (IProject) element.getAdapter(IProject.class);
        setPreferenceStore(getProjectPreferenceStore(project));
    }
    public IAdaptable getElement() {
        return project;
    }
    
    static IPreferenceStore getProjectPreferenceStore(IProject project) {
        return new ScopedPreferenceStore(new ProjectScope(project),
                                         Plugin.PLUGIN_ID);
    }
    
    // static final String P_BUILD_SAFEJ = "autobuildSafej";
    static final String P_BUILD_POLICY = "autobuildPolicy";
    
    /**
	 * 
	 */
	public ProjectProperties() {
		super(GRID);
        setDescription("Project-specific settings for the Joe-E plugin.");
	}
    
    /**
     * Creates the field editors. Field editors are abstractions of
     * the common GUI blocks needed to manipulate various types
     * of preferences. Each field editor knows how to save and
     * restore itself.
     */
    public void createFieldEditors() {
        // addField(
        //    new BooleanFieldEditor(P_BUILD_SAFEJ,
        //        "Automatically build &safej files",
        //        getFieldEditorParent()));
        addField(
            new BooleanFieldEditor(P_BUILD_POLICY,
                "Automatically build &Policy class",
                getFieldEditorParent()));
    }

    

    /*
	private void addFirstSection(Composite parent) {
		Composite composite = createDefaultComposite(parent);

		//Label for path field
		Label pathLabel = new Label(composite, SWT.NONE);
		pathLabel.setText(PATH_TITLE);

		// Path text field
		Text pathValueText = new Text(composite, SWT.WRAP | SWT.READ_ONLY);
		pathValueText.setText(((IResource) getElement()).getFullPath().toString());
	}

	private void addSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		separator.setLayoutData(gridData);
	}

	private void addSecondSection(Composite parent) {
		Composite composite = createDefaultComposite(parent);

		// Label for owner field
		Label ownerLabel = new Label(composite, SWT.NONE);
		ownerLabel.setText(OWNER_TITLE);

		// Owner text field
		ownerText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData();
		gd.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
		ownerText.setLayoutData(gd);

		// Populate owner text field
		try {
			String owner =
				((IResource) getElement()).getPersistentProperty(
					new QualifiedName("", OWNER_PROPERTY));
			ownerText.setText((owner != null) ? owner : DEFAULT_OWNER);
		} catch (CoreException e) {
			ownerText.setText(DEFAULT_OWNER);
		}
	}
    */
	/**
	 * @see PreferencePage#createContents(Composite)
	 */
    /*
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);

        //addFirstSection(composite);
		//addSeparator(composite);
		//addSecondSection(composite);
        return composite;
	}

	private Composite createDefaultComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);

		return composite;
	}
    
    
	protected void performDefaults() {
		// Populate the owner text field with the default value
		ownerText.setText(DEFAULT_OWNER);
	}
	
	public boolean performOk() {
		// store the value in the owner text field
		try {
			((IResource) getElement()).setPersistentProperty(
				new QualifiedName("", OWNER_PROPERTY),
				ownerText.getText());
		} catch (CoreException e) {
			return false;
		}
		return true;
	}
    
    static boolean isSafejOutputEnabled(IProject project) {
        return getProjectPreferenceStore(project).getBoolean(P_BUILD_SAFEJ);
    } */
    
    static boolean isPolicyOutputEnabled(IProject project) {
        return getProjectPreferenceStore(project).getBoolean(P_BUILD_POLICY);
    }
}