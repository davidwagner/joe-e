// Copyright 2005-06 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e.eclipse;

import java.util.Set;
import java.util.HashSet;

import org.eclipse.jdt.core.ICompilationUnit;

/**
 * Information determined about a class by the verifier
 */
class ITypeState {
	
    //final String name;
	int tags; // if set to < 0, tags invalid, otherwise bitfield based on constants
	
	/**
	 * All compilation units relying on any property of this class.  This may 
	 * be a weak dependency only on properties of the class represented with 
	 * flags.  This is the set that must be rebuilt when the class's flags
	 * change. 
	 */
	final Set<ICompilationUnit> allDependents;
	
	/**
	 * Compilation units relying on deeper properties of the class.  These
	 * compilation units will be rebuilt whenever the source of the class
	 * changes.
	 */
	final Set<ICompilationUnit> deepDependents;
	
	/**
	 * Create a new ITypeState, with an empty set of dependents and an
	 * unititialized flags bitfield.
	 */
	ITypeState() {
		//this.name = name;
		this.tags = -1;  // negative means uninitialized
		this.deepDependents = new HashSet<ICompilationUnit>();
		this.allDependents = new HashSet<ICompilationUnit>();
	}
	
	
	/**
	 * Tests if this class implements Selfless (according to its flags)
	 * @return true if the class implements Selfless
	 * @throws RuntimeException if flags are unitialized
	 */
	boolean implementsSelfless() {
		if (tags < 0) {
			throw new RuntimeException("flags not initialized!");
		}
		return ((tags & BuildState.IMPL_SELFLESS) != 0);
	}

	/**
	 * Tests if this class implements Immutable (according to its flags)
	 * @return true if the class implements Immutable
	 * @throws RuntimeException if flags are unitialized
	 */
	boolean implementsImmutable() {
		if (tags < 0) {
			throw new RuntimeException("flags not initialized!");
		}
		return ((tags & BuildState.IMPL_IMMUTABLE) != 0);
	}
	
	/**
	 * Tests if this class implements Powerless (according to its flags)
	 * @return true if the class implements Powerless
	 * @throws RuntimeException if flags are unitialized
	 */
	boolean implementsPowerless() {
		if (tags < 0) {
			throw new RuntimeException("flags not initialized!");
		}
		return ((tags & BuildState.IMPL_POWERLESS) != 0);
	}
	
	/*
	boolean verifiablySelfless() {
		return ((flags & VERIFY_SELFLESS) != 0);
	}
	
	boolean verifiablyImmutable() {
		return ((flags & VERIFY_IMMUTABLE) != 0);
	}
	
	boolean verifiablyPowerless() {
		return ((flags & VERIFY_POWERLESS) != 0);
	}	
	*/
	
	/**
     * Add a flag dependent of the class corresponding to this state object.
     * The compilation unit specified will be rebuilt if changes to this
     * state object's class cause any of its flags to change.  This represents
     * a dependence on the marker interfaces the class implements but not on
     * its content.
     * 
     * @param newDependent
     *                  the compilation unit that has a new flag dependency
	 */
    void addFlagDependent(ICompilationUnit newDependent) {
		allDependents.add(newDependent);
	}
	
    /**
     * Add a deep dependent of the class corresponding to this state object.
     * The compilation unit specified will be rebuilt whenever this state
     * object's class is rebuilt.  This represents a "deep" dependency on the
     * content of the class.
     * 
     * @param newDependent
     *                  the compilation unit that has a new deep dependency
     */
    void addDeepDependent(ICompilationUnit newDependent) {
		deepDependents.add(newDependent);
		allDependents.add(newDependent);
	}

    /**
     * Remove a dependent from the class corresponding to this state object.
     * The compilation unit specified will no longer have rebuilds triggered
     * by changes to the class.
     * 
     * @param dependent
     *                  the compilation unit to have dependencies reset
     */    
	void resetDependencies(ICompilationUnit dependent) {
		deepDependents.remove(dependent);
		allDependents.remove(dependent);
	}
    
    public String toString() {
        StringBuilder b = new StringBuilder("deepDeps: [ ");
        for (ICompilationUnit icu : deepDependents) {
            b.append(icu.getElementName() + " ");
        }
        b.append("]\n  allDeps: [ ");
        for (ICompilationUnit icu : allDependents) {
            b.append(icu.getElementName() + " ");
        }
        return b.append("]\n  Tags: " + tags + "\n").toString();
    }
}