// Copyright 2006 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e;

/*
 * I'd really like to say T extends DeepFrozen here, but then it wouldn't work for String, Integer, etc.,
 * without requiring one to use a DeepFrozenCell.
 */
public class ImmutableArray<T> extends SelflessArray<T> implements Immutable {	

	public ImmutableArray (T... arr) {
		super(arr);
		
		Class arrType = arr.getClass().getComponentType();
		if (!Utility.isSubtypeOf(arrType, Immutable.class)) {
			throw new IllegalArgumentException("ImmutableArray component type "
											   + arrType + " is not Immutable");
		}
	}
}
