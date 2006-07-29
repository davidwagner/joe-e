// Copyright 2006 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e;

/**
 * An immutable array containing powerless objects
 * 
 * @param <E> the element type of objects contained in the array
 */
public class DataArray<E> extends PowerlessArray<E> implements Data {

    /**
     * Construct an immutable array with a copy of an existing array with
     * powerless element type as backing store.
     * 
     * @param arr the array to make an unmodifiable duplicate of
     */ 
	public DataArray (E... arr) {
		// could use a hack with package-scope constructor to avoid redundant
		// immutable check here as a minor optimization, but this is simpler,
		// and more obviously correct.
		super(arr); 
		
		Class arrType = arr.getClass().getComponentType();
		if (!Utility.isSubtypeOf(arrType, Data.class)) {
			throw new IllegalArgumentException("PowerlessArray component type "
											   + arrType + " is not Powerless");
		}
	}
}
