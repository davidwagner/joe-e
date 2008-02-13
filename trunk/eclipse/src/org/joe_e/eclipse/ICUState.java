// Copyright 2005-06 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
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

    public String toString() {
        StringBuilder b = new StringBuilder("references: [ ");
        for (IType type : references) {
            b.append(type.getFullyQualifiedName() + " ");
        }
        return b.append("]\n").toString();
    }
}