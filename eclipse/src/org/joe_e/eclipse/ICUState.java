package org.joe_e.eclipse;

import java.util.Set;
import java.util.HashSet;
import org.eclipse.jdt.core.IType;


/**
 * Information determined about a class by the verifier
 */
class ICUState {
	Set<IType> references;  // verifer dependencies of this compilation unit on other classes 
						    //  (either shallow or deep dependencies -- see ITypeState)
	
	ICUState() {
		this.references = new HashSet<IType>();
	}
	
}