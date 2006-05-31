package org.joe_e.eclipse;

import java.util.Set;
import java.util.HashSet;
import org.eclipse.jdt.core.IType;


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
	
	final String name;
	final int flags;
	final Set<IType> subclasses;  // subclasses of this class
	final Set<IType> containers;  // classes with fields having this class as a declared type
								  //  and relying on this class to implement a marker interface
	
	ITypeState(String name, int flags, Set<IType> references) {
		this.name = name;
		this.flags = flags;
		this.subclasses = new HashSet<IType>();
		this.containers = new HashSet<IType>();
	}
	
	
	
	boolean implementsSelfless() {
		return ((flags & IMPL_SELFLESS) != 0);
	}
	
	boolean implementsImmutable() {
		return ((flags & IMPL_IMMUTABLE) != 0);
	}
	
	boolean implementsPowerless() {
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
	

	
	void addSubclass (IType newSubclass) {
		subclasses.add(newSubclass);
		
		if (containers.contains(newSubclass)) {
			containers.remove(newSubclass);
		}
	}
	
	void addContainer (IType newContainer) {
		if (!subclasses.contains(newContainer)) {
			containers.add(newContainer);
		}
	}
	
	void resetDependencies (IType otherClass) {
		subclasses.remove(otherClass);
		containers.remove(otherClass);
	}
}