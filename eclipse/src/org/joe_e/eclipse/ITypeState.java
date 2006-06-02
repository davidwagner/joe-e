package org.joe_e.eclipse;

import java.util.Set;
import java.util.HashSet;

//import org.eclipse.jdt.core.IType;
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
	

	void addFlagDependent (ICompilationUnit newDependent) {
		allDependents.add(newDependent);
	}
	
	void addDeepDependent (ICompilationUnit newDependent) {
		deepDependents.add(newDependent);
		allDependents.add(newDependent);
	}
	
	void resetDependencies (ICompilationUnit dependent) {
		deepDependents.remove(dependent);
		allDependents.remove(dependent);
	}
}