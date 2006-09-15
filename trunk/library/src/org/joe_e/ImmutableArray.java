// Copyright 2006 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e;

/**
 * An immutable array containing immutable objects.
 * 
 * @param <E> the element type of objects contained in the array
 */
public class ImmutableArray<E> extends ConstArray<E> implements Immutable {	
    
    /**
     * Construct an immutable array with a copy of an existing array with
     * immutable element type as backing store.
     * 
     * @param arr the array to make an unmodifiable duplicate of
     */
	public ImmutableArray (E... arr) {
		super(arr);
		
		Class arrType = arr.getClass().getComponentType();
		if (!Utility.isSubtypeOf(arrType, Immutable.class)) {
			throw new IllegalArgumentException("ImmutableArray component type "
											   + arrType + " is not Immutable");
		}
	}
    
    /**
     * Package-scope back-door constructor for use by subclasses that
     * override all methods that make use of the field arr.  Nullity of arr is
     * used to distinguish between instances with which this class must interact
     * by using the public interface rather than through their arr field.
     */
    ImmutableArray() {
        super();
    }
}
