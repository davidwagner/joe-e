package org.joe_e.eclipse;

import java.util.Set;
import java.util.HashSet;

//import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ICompilationUnit;

/**
 * Information determined about a class by the verifier
 */
class ITypeState {
	static final int IMPL_SELFLESS =    0x0001;
	static final int IMPL_IMMUTABLE =   0x0002;
	static final int IMPL_POWERLESS =   0x0004;
	
	/*
	static final int VERIFY_SELFLESS =  0x0100;
	static final int VERIFY_IMMUTABLE = 0x0200;
	static final int VERIFY_POWERLESS = 0x0400;  
	*/
	
	//final String name;
	final int flags; // if set to < 0, flags invalid, otherwise bitfield based on constants
	final Set<ICompilationUnit> flagDependents;  // compilation units on this class to implement a marker interface
	final Set<ICompilationUnit> deepDependents;      // compiltion units relying on deeper properties of this class
	
	/* flags not known */
		
	ITypeState(int flags) {
		//this.name = name;
		this.flags = flags;
		this.deepDependents = new HashSet<ICompilationUnit>();
		this.flagDependents = new HashSet<ICompilationUnit>();
	}
	
	
	
	boolean implementsSelfless() {
		if (flags < 0) {
			throw new RuntimeException("flags not initialized!");
		}
		return ((flags & IMPL_SELFLESS) != 0);
	}
	
	boolean implementsImmutable() {
		if (flags < 0) {
			throw new RuntimeException("flags not initialized!");
		}
			return ((flags & IMPL_IMMUTABLE) != 0);
	}
	
	boolean implementsPowerless() {
		if (flags < 0) {
			throw new RuntimeException("flags not initialized!");
		}
		return ((flags & IMPL_POWERLESS) != 0);
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
		if (!deepDependents.contains(newDependent)) {
			flagDependents.add(newDependent);
		}
	}
	
	void addDeepDependent (ICompilationUnit newDependent) {
		deepDependents.add(newDependent);
		
		if (flagDependents.contains(newDependent)) {
			flagDependents.remove(newDependent);
		}
	}
	
	void resetDependencies (ICompilationUnit dependent) {
		deepDependents.remove(dependent);
		flagDependents.remove(dependent);
	}
}